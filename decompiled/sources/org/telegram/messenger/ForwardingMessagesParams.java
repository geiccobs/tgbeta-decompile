package org.telegram.messenger;

import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public class ForwardingMessagesParams {
    public boolean hasCaption;
    public boolean hasSenders;
    public boolean hasSpoilers;
    public boolean hideCaption;
    public boolean hideForwardSendersName;
    public boolean isSecret;
    public ArrayList<MessageObject> messages;
    public boolean multiplyUsers;
    public boolean willSeeSenders;
    public LongSparseArray<MessageObject.GroupedMessages> groupedMessagesMap = new LongSparseArray<>();
    public ArrayList<MessageObject> previewMessages = new ArrayList<>();
    public SparseBooleanArray selectedIds = new SparseBooleanArray();
    public ArrayList<TLRPC.TL_pollAnswerVoters> pollChoosenAnswers = new ArrayList<>();

    /* JADX WARN: Type inference failed for: r8v0 */
    /* JADX WARN: Type inference failed for: r8v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r8v2 */
    public ForwardingMessagesParams(ArrayList<MessageObject> messages, long newDialogId) {
        long uid;
        this.messages = messages;
        ?? r8 = 0;
        this.hasCaption = false;
        this.hasSenders = false;
        this.isSecret = DialogObject.isEncryptedDialog(newDialogId);
        this.hasSpoilers = false;
        ArrayList<String> hiddenSendersName = new ArrayList<>();
        int i = 0;
        while (i < messages.size()) {
            MessageObject messageObject = messages.get(i);
            if (!TextUtils.isEmpty(messageObject.caption)) {
                this.hasCaption = true;
            }
            this.selectedIds.put(messageObject.getId(), true);
            TLRPC.Message message = new TLRPC.TL_message();
            message.id = messageObject.messageOwner.id;
            message.grouped_id = messageObject.messageOwner.grouped_id;
            message.peer_id = messageObject.messageOwner.peer_id;
            message.from_id = messageObject.messageOwner.from_id;
            message.message = messageObject.messageOwner.message;
            message.media = messageObject.messageOwner.media;
            message.action = messageObject.messageOwner.action;
            int i2 = r8 == true ? 1 : 0;
            int i3 = r8 == true ? 1 : 0;
            message.edit_date = i2;
            if (messageObject.messageOwner.entities != null) {
                message.entities.addAll(messageObject.messageOwner.entities);
                if (!this.hasSpoilers) {
                    Iterator<TLRPC.MessageEntity> it = message.entities.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        TLRPC.MessageEntity e = it.next();
                        if (e instanceof TLRPC.TL_messageEntitySpoiler) {
                            this.hasSpoilers = true;
                            break;
                        }
                    }
                }
            }
            message.out = true;
            message.unread = r8;
            message.via_bot_id = messageObject.messageOwner.via_bot_id;
            message.reply_markup = messageObject.messageOwner.reply_markup;
            message.post = messageObject.messageOwner.post;
            message.legacy = messageObject.messageOwner.legacy;
            message.restriction_reason = messageObject.messageOwner.restriction_reason;
            message.replyMessage = messageObject.messageOwner.replyMessage;
            TLRPC.MessageFwdHeader header = null;
            long clientUserId = UserConfig.getInstance(messageObject.currentAccount).clientUserId;
            if (!this.isSecret) {
                if (messageObject.messageOwner.fwd_from != null) {
                    header = messageObject.messageOwner.fwd_from;
                    if (!messageObject.isDice()) {
                        this.hasSenders = true;
                    } else {
                        this.willSeeSenders = true;
                    }
                    if (header.from_id == null && !hiddenSendersName.contains(header.from_name)) {
                        hiddenSendersName.add(header.from_name);
                    }
                } else if (messageObject.messageOwner.from_id.user_id == 0 || messageObject.messageOwner.dialog_id != clientUserId || messageObject.messageOwner.from_id.user_id != clientUserId) {
                    header = new TLRPC.TL_messageFwdHeader();
                    header.from_id = messageObject.messageOwner.from_id;
                    if (!messageObject.isDice()) {
                        this.hasSenders = true;
                    } else {
                        this.willSeeSenders = true;
                    }
                }
            }
            TLRPC.MessageFwdHeader header2 = header;
            if (header2 != null) {
                message.fwd_from = header2;
                message.flags |= 4;
            }
            message.dialog_id = newDialogId;
            MessageObject previewMessage = new MessageObject(messageObject.currentAccount, message, true, false) { // from class: org.telegram.messenger.ForwardingMessagesParams.1
                @Override // org.telegram.messenger.MessageObject
                public boolean needDrawForwarded() {
                    if (ForwardingMessagesParams.this.hideForwardSendersName) {
                        return false;
                    }
                    return super.needDrawForwarded();
                }
            };
            previewMessage.preview = true;
            if (previewMessage.getGroupId() != 0) {
                MessageObject.GroupedMessages groupedMessages = this.groupedMessagesMap.get(previewMessage.getGroupId(), null);
                if (groupedMessages == null) {
                    groupedMessages = new MessageObject.GroupedMessages();
                    this.groupedMessagesMap.put(previewMessage.getGroupId(), groupedMessages);
                }
                groupedMessages.messages.add(previewMessage);
            }
            this.previewMessages.add(r8, previewMessage);
            if (messageObject.isPoll()) {
                TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) messageObject.messageOwner.media;
                PreviewMediaPoll newMediaPoll = new PreviewMediaPoll();
                newMediaPoll.poll = mediaPoll.poll;
                newMediaPoll.provider = mediaPoll.provider;
                newMediaPoll.results = new TLRPC.TL_pollResults();
                TLRPC.PollResults pollResults = newMediaPoll.results;
                int i4 = mediaPoll.results.total_voters;
                pollResults.total_voters = i4;
                newMediaPoll.totalVotersCached = i4;
                previewMessage.messageOwner.media = newMediaPoll;
                if (messageObject.canUnvote()) {
                    int N = mediaPoll.results.results.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_pollAnswerVoters answer = mediaPoll.results.results.get(a);
                        if (answer.chosen) {
                            TLRPC.TL_pollAnswerVoters newAnswer = new TLRPC.TL_pollAnswerVoters();
                            newAnswer.chosen = answer.chosen;
                            newAnswer.correct = answer.correct;
                            newAnswer.flags = answer.flags;
                            newAnswer.option = answer.option;
                            newAnswer.voters = answer.voters;
                            this.pollChoosenAnswers.add(newAnswer);
                            newMediaPoll.results.results.add(newAnswer);
                        } else {
                            newMediaPoll.results.results.add(answer);
                        }
                    }
                }
            }
            i++;
            r8 = 0;
        }
        ArrayList<Long> uids = new ArrayList<>();
        for (int a2 = 0; a2 < messages.size(); a2++) {
            MessageObject object = messages.get(a2);
            if (object.isFromUser()) {
                uid = object.messageOwner.from_id.user_id;
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(object.currentAccount).getChat(Long.valueOf(object.messageOwner.peer_id.channel_id));
                if (ChatObject.isChannel(chat) && chat.megagroup && object.isForwardedChannelPost()) {
                    uid = -object.messageOwner.fwd_from.from_id.channel_id;
                } else {
                    uid = -object.messageOwner.peer_id.channel_id;
                }
            }
            if (!uids.contains(Long.valueOf(uid))) {
                uids.add(Long.valueOf(uid));
            }
        }
        int a3 = uids.size();
        if (a3 + hiddenSendersName.size() > 1) {
            this.multiplyUsers = true;
        }
        for (int i5 = 0; i5 < this.groupedMessagesMap.size(); i5++) {
            this.groupedMessagesMap.valueAt(i5).calculate();
        }
    }

    public void getSelectedMessages(ArrayList<MessageObject> messagesToForward) {
        messagesToForward.clear();
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            int id = messageObject.getId();
            if (this.selectedIds.get(id, false)) {
                messagesToForward.add(messageObject);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class PreviewMediaPoll extends TLRPC.TL_messageMediaPoll {
        public int totalVotersCached;

        public PreviewMediaPoll() {
            ForwardingMessagesParams.this = this$0;
        }
    }
}
