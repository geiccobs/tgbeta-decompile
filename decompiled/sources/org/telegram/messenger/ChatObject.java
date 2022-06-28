package org.telegram.messenger;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCallActivity;
/* loaded from: classes4.dex */
public class ChatObject {
    public static final int ACTION_ADD_ADMINS = 4;
    public static final int ACTION_BLOCK_USERS = 2;
    public static final int ACTION_CHANGE_INFO = 1;
    public static final int ACTION_DELETE_MESSAGES = 13;
    public static final int ACTION_EDIT_MESSAGES = 12;
    public static final int ACTION_EMBED_LINKS = 9;
    public static final int ACTION_INVITE = 3;
    public static final int ACTION_MANAGE_CALLS = 14;
    public static final int ACTION_PIN = 0;
    public static final int ACTION_POST = 5;
    public static final int ACTION_SEND = 6;
    public static final int ACTION_SEND_MEDIA = 7;
    public static final int ACTION_SEND_POLLS = 10;
    public static final int ACTION_SEND_STICKERS = 8;
    public static final int ACTION_VIEW = 11;
    public static final int CHAT_TYPE_CHANNEL = 2;
    public static final int CHAT_TYPE_CHAT = 0;
    public static final int CHAT_TYPE_MEGAGROUP = 4;
    public static final int CHAT_TYPE_USER = 3;
    private static final int MAX_PARTICIPANTS_COUNT = 5000;
    public static final int VIDEO_FRAME_HAS_FRAME = 2;
    public static final int VIDEO_FRAME_NO_FRAME = 0;
    public static final int VIDEO_FRAME_REQUESTING = 1;

    /* loaded from: classes4.dex */
    public static class Call {
        public static final int RECORD_TYPE_AUDIO = 0;
        public static final int RECORD_TYPE_VIDEO_LANDSCAPE = 2;
        public static final int RECORD_TYPE_VIDEO_PORTAIT = 1;
        private static int videoPointer;
        public int activeVideos;
        public TLRPC.GroupCall call;
        public boolean canStreamVideo;
        public long chatId;
        private Runnable checkQueueRunnable;
        public AccountInstance currentAccount;
        private long lastGroupCallReloadTime;
        private int lastLoadGuid;
        public boolean loadedRtmpStreamParticipant;
        private boolean loadingGroupCall;
        public boolean loadingMembers;
        public boolean membersLoadEndReached;
        private String nextLoadOffset;
        public boolean recording;
        public boolean reloadingMembers;
        public VideoParticipant rtmpStreamParticipant;
        public TLRPC.Peer selfPeer;
        public int speakingMembersCount;
        private boolean typingUpdateRunnableScheduled;
        private long updatesStartWaitTime;
        public VideoParticipant videoNotAvailableParticipant;
        public LongSparseArray<TLRPC.TL_groupCallParticipant> participants = new LongSparseArray<>();
        public final ArrayList<TLRPC.TL_groupCallParticipant> sortedParticipants = new ArrayList<>();
        public final ArrayList<VideoParticipant> visibleVideoParticipants = new ArrayList<>();
        public final ArrayList<TLRPC.TL_groupCallParticipant> visibleParticipants = new ArrayList<>();
        public final HashMap<String, Bitmap> thumbs = new HashMap<>();
        private final HashMap<String, VideoParticipant> videoParticipantsCache = new HashMap<>();
        public ArrayList<Long> invitedUsers = new ArrayList<>();
        public HashSet<Long> invitedUsersMap = new HashSet<>();
        public SparseArray<TLRPC.TL_groupCallParticipant> participantsBySources = new SparseArray<>();
        public SparseArray<TLRPC.TL_groupCallParticipant> participantsByVideoSources = new SparseArray<>();
        public SparseArray<TLRPC.TL_groupCallParticipant> participantsByPresentationSources = new SparseArray<>();
        private Runnable typingUpdateRunnable = new Runnable() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ChatObject.Call.this.m122lambda$new$0$orgtelegrammessengerChatObject$Call();
            }
        };
        private HashSet<Integer> loadingGuids = new HashSet<>();
        private ArrayList<TLRPC.TL_updateGroupCallParticipants> updatesQueue = new ArrayList<>();
        private HashSet<Long> loadingUids = new HashSet<>();
        private HashSet<Long> loadingSsrcs = new HashSet<>();
        public final LongSparseArray<TLRPC.TL_groupCallParticipant> currentSpeakingPeers = new LongSparseArray<>();
        private final Runnable updateCurrentSpeakingRunnable = new Runnable() { // from class: org.telegram.messenger.ChatObject.Call.1
            {
                Call.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                long uptime = SystemClock.uptimeMillis();
                boolean update = false;
                int i = 0;
                while (i < Call.this.currentSpeakingPeers.size()) {
                    long key = Call.this.currentSpeakingPeers.keyAt(i);
                    TLRPC.TL_groupCallParticipant participant = Call.this.currentSpeakingPeers.get(key);
                    if (uptime - participant.lastSpeakTime >= 500) {
                        update = true;
                        Call.this.currentSpeakingPeers.remove(key);
                        if (key > 0) {
                            TLRPC.User user = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount()).getUser(Long.valueOf(key));
                            StringBuilder sb = new StringBuilder();
                            sb.append("remove from speaking ");
                            sb.append(key);
                            sb.append(" ");
                            sb.append(user == null ? null : user.first_name);
                            Log.d("GroupCall", sb.toString());
                        } else {
                            TLRPC.Chat user2 = MessagesController.getInstance(Call.this.currentAccount.getCurrentAccount()).getChat(Long.valueOf(-key));
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("remove from speaking ");
                            sb2.append(key);
                            sb2.append(" ");
                            sb2.append(user2 == null ? null : user2.title);
                            Log.d("GroupCall", sb2.toString());
                        }
                        i--;
                    }
                    i++;
                }
                if (Call.this.currentSpeakingPeers.size() > 0) {
                    AndroidUtilities.runOnUIThread(Call.this.updateCurrentSpeakingRunnable, 550L);
                }
                if (update) {
                    Call.this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallSpeakingUsersUpdated, Long.valueOf(Call.this.chatId), Long.valueOf(Call.this.call.id), false);
                }
            }
        };

        /* loaded from: classes4.dex */
        public interface OnParticipantsLoad {
            void onLoad(ArrayList<Long> arrayList);
        }

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface RecordType {
        }

        /* renamed from: lambda$new$0$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m122lambda$new$0$orgtelegrammessengerChatObject$Call() {
            this.typingUpdateRunnableScheduled = false;
            checkOnlineParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallTypingsUpdated, new Object[0]);
        }

        public void setCall(AccountInstance account, long chatId, TLRPC.TL_phone_groupCall groupCall) {
            this.chatId = chatId;
            this.currentAccount = account;
            TLRPC.GroupCall groupCall2 = groupCall.call;
            this.call = groupCall2;
            this.recording = groupCall2.record_start_date != 0;
            int date = Integer.MAX_VALUE;
            int N = groupCall.participants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = groupCall.participants.get(a);
                this.participants.put(MessageObject.getPeerId(participant.peer), participant);
                this.sortedParticipants.add(participant);
                processAllSources(participant, true);
                date = Math.min(date, participant.date);
            }
            sortParticipants();
            this.nextLoadOffset = groupCall.participants_next_offset;
            loadMembers(true);
            createNoVideoParticipant();
            if (this.call.rtmp_stream) {
                createRtmpStreamParticipant(Collections.emptyList());
            }
        }

        public void createRtmpStreamParticipant(List<TLRPC.TL_groupCallStreamChannel> channels) {
            if (this.loadedRtmpStreamParticipant && this.rtmpStreamParticipant != null) {
                return;
            }
            VideoParticipant videoParticipant = this.rtmpStreamParticipant;
            TLRPC.TL_groupCallParticipant participant = videoParticipant != null ? videoParticipant.participant : new TLRPC.TL_groupCallParticipant();
            participant.peer = new TLRPC.TL_peerChat();
            participant.peer.channel_id = this.chatId;
            participant.video = new TLRPC.TL_groupCallParticipantVideo();
            TLRPC.TL_groupCallParticipantVideoSourceGroup sourceGroup = new TLRPC.TL_groupCallParticipantVideoSourceGroup();
            sourceGroup.semantics = "SIM";
            for (TLRPC.TL_groupCallStreamChannel channel : channels) {
                sourceGroup.sources.add(Integer.valueOf(channel.channel));
            }
            participant.video.source_groups.add(sourceGroup);
            participant.video.endpoint = "unified";
            participant.videoEndpoint = "unified";
            this.rtmpStreamParticipant = new VideoParticipant(participant, false, false);
            sortParticipants();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ChatObject.Call.this.m115x5582b510();
                }
            });
        }

        /* renamed from: lambda$createRtmpStreamParticipant$1$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m115x5582b510() {
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
        }

        public void createNoVideoParticipant() {
            if (this.videoNotAvailableParticipant != null) {
                return;
            }
            TLRPC.TL_groupCallParticipant noVideoParticipant = new TLRPC.TL_groupCallParticipant();
            noVideoParticipant.peer = new TLRPC.TL_peerChannel();
            noVideoParticipant.peer.channel_id = this.chatId;
            noVideoParticipant.muted = true;
            noVideoParticipant.video = new TLRPC.TL_groupCallParticipantVideo();
            noVideoParticipant.video.paused = true;
            noVideoParticipant.video.endpoint = "";
            this.videoNotAvailableParticipant = new VideoParticipant(noVideoParticipant, false, false);
        }

        public void addSelfDummyParticipant(boolean notify) {
            long selfId = getSelfId();
            if (this.participants.indexOfKey(selfId) >= 0) {
                return;
            }
            TLRPC.TL_groupCallParticipant selfDummyParticipant = new TLRPC.TL_groupCallParticipant();
            selfDummyParticipant.peer = this.selfPeer;
            selfDummyParticipant.muted = true;
            selfDummyParticipant.self = true;
            selfDummyParticipant.video_joined = this.call.can_start_video;
            TLRPC.Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
            selfDummyParticipant.can_self_unmute = !this.call.join_muted || ChatObject.canManageCalls(chat);
            selfDummyParticipant.date = this.currentAccount.getConnectionsManager().getCurrentTime();
            if (ChatObject.canManageCalls(chat) || !ChatObject.isChannel(chat) || chat.megagroup || selfDummyParticipant.can_self_unmute) {
                selfDummyParticipant.active_date = this.currentAccount.getConnectionsManager().getCurrentTime();
            }
            if (selfId > 0) {
                TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getUserFull(selfId);
                if (userFull != null) {
                    selfDummyParticipant.about = userFull.about;
                }
            } else {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount.getCurrentAccount()).getChatFull(-selfId);
                if (chatFull != null) {
                    selfDummyParticipant.about = chatFull.about;
                }
            }
            this.participants.put(selfId, selfDummyParticipant);
            this.sortedParticipants.add(selfDummyParticipant);
            sortParticipants();
            if (notify) {
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
            }
        }

        public void migrateToChat(TLRPC.Chat chat) {
            this.chatId = chat.id;
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService != null && voIPService.getAccount() == this.currentAccount.getCurrentAccount() && voIPService.getChat() != null && voIPService.getChat().id == (-this.chatId)) {
                voIPService.migrateToChat(chat);
            }
        }

        public boolean shouldShowPanel() {
            return this.call.participants_count > 0 || this.call.rtmp_stream || isScheduled();
        }

        public boolean isScheduled() {
            return (this.call.flags & 128) != 0;
        }

        private long getSelfId() {
            TLRPC.Peer peer = this.selfPeer;
            if (peer != null) {
                return MessageObject.getPeerId(peer);
            }
            return this.currentAccount.getUserConfig().getClientUserId();
        }

        private void onParticipantsLoad(ArrayList<TLRPC.TL_groupCallParticipant> loadedParticipants, boolean fromBegin, String reqOffset, String nextOffset, int version, int participantCount) {
            TLRPC.TL_groupCallParticipant oldSelf;
            long selfId;
            LongSparseArray<TLRPC.TL_groupCallParticipant> old;
            TLRPC.TL_groupCallParticipant participant;
            TLRPC.TL_groupCallParticipant oldParticipant;
            TLRPC.TL_groupCallParticipant oldParticipant2;
            LongSparseArray<TLRPC.TL_groupCallParticipant> old2 = null;
            long selfId2 = getSelfId();
            TLRPC.TL_groupCallParticipant oldSelf2 = this.participants.get(selfId2);
            if (TextUtils.isEmpty(reqOffset)) {
                if (this.participants.size() != 0) {
                    old2 = this.participants;
                    this.participants = new LongSparseArray<>();
                } else {
                    this.participants.clear();
                }
                this.sortedParticipants.clear();
                this.participantsBySources.clear();
                this.participantsByVideoSources.clear();
                this.participantsByPresentationSources.clear();
                this.loadingGuids.clear();
            }
            this.nextLoadOffset = nextOffset;
            if (loadedParticipants.isEmpty() || TextUtils.isEmpty(this.nextLoadOffset)) {
                this.membersLoadEndReached = true;
            }
            if (TextUtils.isEmpty(reqOffset)) {
                this.call.version = version;
                this.call.participants_count = participantCount;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("new participants count " + this.call.participants_count);
                }
            }
            long time = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(time));
            boolean hasSelf = false;
            int a = 0;
            int N = loadedParticipants.size();
            while (a <= N) {
                if (a == N) {
                    if (!fromBegin || oldSelf2 == null || hasSelf) {
                        old = old2;
                        selfId = selfId2;
                        oldSelf = oldSelf2;
                        a++;
                        old2 = old;
                        selfId2 = selfId;
                        oldSelf2 = oldSelf;
                    } else {
                        participant = oldSelf2;
                    }
                } else {
                    participant = loadedParticipants.get(a);
                    if (participant.self) {
                        hasSelf = true;
                    }
                }
                selfId = selfId2;
                TLRPC.TL_groupCallParticipant oldParticipant3 = this.participants.get(MessageObject.getPeerId(participant.peer));
                if (oldParticipant3 != null) {
                    this.sortedParticipants.remove(oldParticipant3);
                    processAllSources(oldParticipant3, false);
                    if (oldParticipant3.self) {
                        participant.lastTypingDate = oldParticipant3.active_date;
                    } else {
                        participant.lastTypingDate = Math.max(participant.active_date, oldParticipant3.active_date);
                    }
                    oldParticipant2 = oldParticipant3;
                    if (time == participant.lastVisibleDate) {
                        oldSelf = oldSelf2;
                    } else {
                        participant.active_date = participant.lastTypingDate;
                        oldSelf = oldSelf2;
                    }
                } else {
                    oldParticipant2 = oldParticipant3;
                    if (old2 == null) {
                        oldSelf = oldSelf2;
                    } else {
                        oldParticipant = old2.get(MessageObject.getPeerId(participant.peer));
                        if (oldParticipant == null) {
                            oldSelf = oldSelf2;
                        } else {
                            if (oldParticipant.self) {
                                participant.lastTypingDate = oldParticipant.active_date;
                            } else {
                                participant.lastTypingDate = Math.max(participant.active_date, oldParticipant.active_date);
                            }
                            oldSelf = oldSelf2;
                            if (time != participant.lastVisibleDate) {
                                participant.active_date = participant.lastTypingDate;
                            } else {
                                participant.active_date = oldParticipant.active_date;
                            }
                        }
                        old = old2;
                        this.participants.put(MessageObject.getPeerId(participant.peer), participant);
                        this.sortedParticipants.add(participant);
                        processAllSources(participant, true);
                        a++;
                        old2 = old;
                        selfId2 = selfId;
                        oldSelf2 = oldSelf;
                    }
                }
                oldParticipant = oldParticipant2;
                old = old2;
                this.participants.put(MessageObject.getPeerId(participant.peer), participant);
                this.sortedParticipants.add(participant);
                processAllSources(participant, true);
                a++;
                old2 = old;
                selfId2 = selfId;
                oldSelf2 = oldSelf;
            }
            if (this.call.participants_count < this.participants.size()) {
                this.call.participants_count = this.participants.size();
            }
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
            setParticiapantsVolume();
        }

        public void loadMembers(final boolean fromBegin) {
            if (fromBegin) {
                if (this.reloadingMembers) {
                    return;
                }
                this.membersLoadEndReached = false;
                this.nextLoadOffset = null;
            }
            if (this.membersLoadEndReached || this.sortedParticipants.size() > 5000) {
                return;
            }
            if (fromBegin) {
                this.reloadingMembers = true;
            }
            this.loadingMembers = true;
            final TLRPC.TL_phone_getGroupParticipants req = new TLRPC.TL_phone_getGroupParticipants();
            req.call = getInputGroupCall();
            String str = this.nextLoadOffset;
            if (str == null) {
                str = "";
            }
            req.offset = str;
            req.limit = 20;
            this.currentAccount.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatObject.Call.this.m119lambda$loadMembers$3$orgtelegrammessengerChatObject$Call(fromBegin, req, tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$loadMembers$3$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m119lambda$loadMembers$3$orgtelegrammessengerChatObject$Call(final boolean fromBegin, final TLRPC.TL_phone_getGroupParticipants req, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    ChatObject.Call.this.m118lambda$loadMembers$2$orgtelegrammessengerChatObject$Call(fromBegin, response, req);
                }
            });
        }

        /* renamed from: lambda$loadMembers$2$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m118lambda$loadMembers$2$orgtelegrammessengerChatObject$Call(boolean fromBegin, TLObject response, TLRPC.TL_phone_getGroupParticipants req) {
            this.loadingMembers = false;
            if (fromBegin) {
                this.reloadingMembers = false;
            }
            if (response != null) {
                TLRPC.TL_phone_groupParticipants groupParticipants = (TLRPC.TL_phone_groupParticipants) response;
                this.currentAccount.getMessagesController().putUsers(groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(groupParticipants.chats, false);
                onParticipantsLoad(groupParticipants.participants, fromBegin, req.offset, groupParticipants.next_offset, groupParticipants.version, groupParticipants.count);
            }
        }

        private void setParticiapantsVolume() {
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService != null && voIPService.getAccount() == this.currentAccount.getCurrentAccount() && voIPService.getChat() != null && voIPService.getChat().id == (-this.chatId)) {
                voIPService.setParticipantsVolume();
            }
        }

        public void setTitle(String title) {
            TLRPC.TL_phone_editGroupCallTitle req = new TLRPC.TL_phone_editGroupCallTitle();
            req.call = getInputGroupCall();
            req.title = title;
            this.currentAccount.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatObject.Call.this.m125lambda$setTitle$4$orgtelegrammessengerChatObject$Call(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$setTitle$4$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m125lambda$setTitle$4$orgtelegrammessengerChatObject$Call(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                TLRPC.Updates res = (TLRPC.Updates) response;
                this.currentAccount.getMessagesController().processUpdates(res, false);
            }
        }

        public void addInvitedUser(long uid) {
            if (this.participants.get(uid) != null || this.invitedUsersMap.contains(Long.valueOf(uid))) {
                return;
            }
            this.invitedUsersMap.add(Long.valueOf(uid));
            this.invitedUsers.add(Long.valueOf(uid));
        }

        public void processTypingsUpdate(AccountInstance accountInstance, ArrayList<Long> uids, int date) {
            boolean updated = false;
            ArrayList<Long> participantsToLoad = null;
            long time = SystemClock.elapsedRealtime();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.applyGroupCallVisibleParticipants, Long.valueOf(time));
            int N = uids.size();
            for (int a = 0; a < N; a++) {
                Long id = uids.get(a);
                TLRPC.TL_groupCallParticipant participant = this.participants.get(id.longValue());
                if (participant != null) {
                    if (date - participant.lastTypingDate > 10) {
                        if (participant.lastVisibleDate != date) {
                            participant.active_date = date;
                        }
                        participant.lastTypingDate = date;
                        updated = true;
                    }
                } else {
                    if (participantsToLoad == null) {
                        participantsToLoad = new ArrayList<>();
                    }
                    participantsToLoad.add(id);
                }
            }
            if (participantsToLoad != null) {
                loadUnknownParticipants(participantsToLoad, true, null);
            }
            if (updated) {
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
            }
        }

        private void loadUnknownParticipants(final ArrayList<Long> participantsToLoad, boolean isIds, final OnParticipantsLoad onLoad) {
            TLRPC.InputPeer inputPeer;
            HashSet<Long> set = isIds ? this.loadingUids : this.loadingSsrcs;
            int a = 0;
            int N = participantsToLoad.size();
            while (a < N) {
                if (set.contains(participantsToLoad.get(a))) {
                    participantsToLoad.remove(a);
                    a--;
                    N--;
                }
                a++;
            }
            if (participantsToLoad.isEmpty()) {
                return;
            }
            final int guid = this.lastLoadGuid + 1;
            this.lastLoadGuid = guid;
            this.loadingGuids.add(Integer.valueOf(guid));
            set.addAll(participantsToLoad);
            TLRPC.TL_phone_getGroupParticipants req = new TLRPC.TL_phone_getGroupParticipants();
            req.call = getInputGroupCall();
            int N2 = participantsToLoad.size();
            for (int a2 = 0; a2 < N2; a2++) {
                long uid = participantsToLoad.get(a2).longValue();
                if (isIds) {
                    if (uid > 0) {
                        TLRPC.TL_inputPeerUser peerUser = new TLRPC.TL_inputPeerUser();
                        peerUser.user_id = uid;
                        req.ids.add(peerUser);
                    } else {
                        TLRPC.Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(-uid));
                        if (chat == null || ChatObject.isChannel(chat)) {
                            inputPeer = new TLRPC.TL_inputPeerChannel();
                            inputPeer.channel_id = -uid;
                        } else {
                            inputPeer = new TLRPC.TL_inputPeerChat();
                            inputPeer.chat_id = -uid;
                        }
                        req.ids.add(inputPeer);
                    }
                } else {
                    req.sources.add(Integer.valueOf((int) uid));
                }
            }
            req.offset = "";
            req.limit = 100;
            final HashSet<Long> hashSet = set;
            this.currentAccount.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatObject.Call.this.m121x3a6fd8f9(guid, onLoad, participantsToLoad, hashSet, tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$loadUnknownParticipants$6$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m121x3a6fd8f9(final int guid, final OnParticipantsLoad onLoad, final ArrayList participantsToLoad, final HashSet set, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ChatObject.Call.this.m120xe351e81a(guid, response, onLoad, participantsToLoad, set);
                }
            });
        }

        /* renamed from: lambda$loadUnknownParticipants$5$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m120xe351e81a(int guid, TLObject response, OnParticipantsLoad onLoad, ArrayList participantsToLoad, HashSet set) {
            if (!this.loadingGuids.remove(Integer.valueOf(guid))) {
                return;
            }
            if (response != null) {
                TLRPC.TL_phone_groupParticipants groupParticipants = (TLRPC.TL_phone_groupParticipants) response;
                this.currentAccount.getMessagesController().putUsers(groupParticipants.users, false);
                this.currentAccount.getMessagesController().putChats(groupParticipants.chats, false);
                int N = groupParticipants.participants.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.TL_groupCallParticipant participant = groupParticipants.participants.get(a);
                    long pid = MessageObject.getPeerId(participant.peer);
                    TLRPC.TL_groupCallParticipant oldParticipant = this.participants.get(pid);
                    if (oldParticipant != null) {
                        this.sortedParticipants.remove(oldParticipant);
                        processAllSources(oldParticipant, false);
                    }
                    this.participants.put(pid, participant);
                    this.sortedParticipants.add(participant);
                    processAllSources(participant, true);
                    if (this.invitedUsersMap.contains(Long.valueOf(pid))) {
                        Long id = Long.valueOf(pid);
                        this.invitedUsersMap.remove(id);
                        this.invitedUsers.remove(id);
                    }
                }
                if (this.call.participants_count < this.participants.size()) {
                    this.call.participants_count = this.participants.size();
                }
                sortParticipants();
                this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
                if (onLoad != null) {
                    onLoad.onLoad(participantsToLoad);
                } else {
                    setParticiapantsVolume();
                }
            }
            set.removeAll(participantsToLoad);
        }

        private void processAllSources(TLRPC.TL_groupCallParticipant participant, boolean add) {
            if (participant.source != 0) {
                if (add) {
                    this.participantsBySources.put(participant.source, participant);
                } else {
                    this.participantsBySources.remove(participant.source);
                }
            }
            int c = 0;
            while (c < 2) {
                TLRPC.TL_groupCallParticipantVideo data = c == 0 ? participant.video : participant.presentation;
                if (data != null) {
                    if ((2 & data.flags) != 0 && data.audio_source != 0) {
                        if (add) {
                            this.participantsBySources.put(data.audio_source, participant);
                        } else {
                            this.participantsBySources.remove(data.audio_source);
                        }
                    }
                    SparseArray<TLRPC.TL_groupCallParticipant> sourcesArray = c == 0 ? this.participantsByVideoSources : this.participantsByPresentationSources;
                    int N = data.source_groups.size();
                    for (int a = 0; a < N; a++) {
                        TLRPC.TL_groupCallParticipantVideoSourceGroup sourceGroup = data.source_groups.get(a);
                        int N2 = sourceGroup.sources.size();
                        for (int b = 0; b < N2; b++) {
                            int source = sourceGroup.sources.get(b).intValue();
                            if (add) {
                                sourcesArray.put(source, participant);
                            } else {
                                sourcesArray.remove(source);
                            }
                        }
                    }
                    if (add) {
                        if (c == 0) {
                            participant.videoEndpoint = data.endpoint;
                        } else {
                            participant.presentationEndpoint = data.endpoint;
                        }
                    } else if (c == 0) {
                        participant.videoEndpoint = null;
                    } else {
                        participant.presentationEndpoint = null;
                    }
                }
                c++;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:30:0x00b6  */
        /* JADX WARN: Removed duplicated region for block: B:43:0x0130  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void processVoiceLevelsUpdate(int[] r27, float[] r28, boolean[] r29) {
            /*
                Method dump skipped, instructions count: 622
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processVoiceLevelsUpdate(int[], float[], boolean[]):void");
        }

        public void updateVisibleParticipants() {
            sortParticipants();
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false, 0L);
        }

        public void clearVideFramesInfo() {
            for (int i = 0; i < this.sortedParticipants.size(); i++) {
                this.sortedParticipants.get(i).hasCameraFrame = 0;
                this.sortedParticipants.get(i).hasPresentationFrame = 0;
                this.sortedParticipants.get(i).videoIndex = 0;
            }
            sortParticipants();
        }

        public void processUnknownVideoParticipants(int[] ssrc, OnParticipantsLoad onLoad) {
            ArrayList<Long> participantsToLoad = null;
            for (int a = 0; a < ssrc.length; a++) {
                if (this.participantsBySources.get(ssrc[a]) == null && this.participantsByVideoSources.get(ssrc[a]) == null && this.participantsByPresentationSources.get(ssrc[a]) == null) {
                    if (participantsToLoad == null) {
                        participantsToLoad = new ArrayList<>();
                    }
                    participantsToLoad.add(Long.valueOf(ssrc[a]));
                }
            }
            if (participantsToLoad != null) {
                loadUnknownParticipants(participantsToLoad, false, onLoad);
            } else {
                onLoad.onLoad(null);
            }
        }

        private int isValidUpdate(TLRPC.TL_updateGroupCallParticipants update) {
            if (this.call.version + 1 == update.version || this.call.version == update.version) {
                return 0;
            }
            return this.call.version < update.version ? 1 : 2;
        }

        public void setSelfPeer(TLRPC.InputPeer peer) {
            if (peer == null) {
                this.selfPeer = null;
            } else if (peer instanceof TLRPC.TL_inputPeerUser) {
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                this.selfPeer = tL_peerUser;
                tL_peerUser.user_id = peer.user_id;
            } else if (peer instanceof TLRPC.TL_inputPeerChat) {
                TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
                this.selfPeer = tL_peerChat;
                tL_peerChat.chat_id = peer.chat_id;
            } else {
                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                this.selfPeer = tL_peerChannel;
                tL_peerChannel.channel_id = peer.channel_id;
            }
        }

        private void processUpdatesQueue() {
            Collections.sort(this.updatesQueue, ChatObject$Call$$ExternalSyntheticLambda14.INSTANCE);
            ArrayList<TLRPC.TL_updateGroupCallParticipants> arrayList = this.updatesQueue;
            if (arrayList != null && !arrayList.isEmpty()) {
                boolean anyProceed = false;
                for (int a = 0; a < this.updatesQueue.size(); a = (a - 1) + 1) {
                    TLRPC.TL_updateGroupCallParticipants update = this.updatesQueue.get(a);
                    int updateState = isValidUpdate(update);
                    if (updateState == 0) {
                        processParticipantsUpdate(update, true);
                        anyProceed = true;
                        this.updatesQueue.remove(a);
                    } else if (updateState == 1) {
                        if (this.updatesStartWaitTime != 0 && (anyProceed || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTime) <= 1500)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("HOLE IN GROUP CALL UPDATES QUEUE - will wait more time");
                            }
                            if (anyProceed) {
                                this.updatesStartWaitTime = System.currentTimeMillis();
                                return;
                            }
                            return;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN GROUP CALL UPDATES QUEUE - reload participants");
                        }
                        this.updatesStartWaitTime = 0L;
                        this.updatesQueue.clear();
                        this.nextLoadOffset = null;
                        loadMembers(true);
                        return;
                    } else {
                        this.updatesQueue.remove(a);
                    }
                }
                this.updatesQueue.clear();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("GROUP CALL UPDATES QUEUE PROCEED - OK");
                }
            }
            this.updatesStartWaitTime = 0L;
        }

        public void checkQueue() {
            this.checkQueueRunnable = null;
            if (this.updatesStartWaitTime != 0 && System.currentTimeMillis() - this.updatesStartWaitTime >= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("QUEUE GROUP CALL UPDATES WAIT TIMEOUT - CHECK QUEUE");
                }
                processUpdatesQueue();
            }
            if (!this.updatesQueue.isEmpty()) {
                ChatObject$Call$$ExternalSyntheticLambda8 chatObject$Call$$ExternalSyntheticLambda8 = new ChatObject$Call$$ExternalSyntheticLambda8(this);
                this.checkQueueRunnable = chatObject$Call$$ExternalSyntheticLambda8;
                AndroidUtilities.runOnUIThread(chatObject$Call$$ExternalSyntheticLambda8, 1000L);
            }
        }

        public void reloadGroupCall() {
            TLRPC.TL_phone_getGroupCall req = new TLRPC.TL_phone_getGroupCall();
            req.call = getInputGroupCall();
            req.limit = 100;
            this.currentAccount.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatObject.Call.this.m124lambda$reloadGroupCall$9$orgtelegrammessengerChatObject$Call(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$reloadGroupCall$9$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m124lambda$reloadGroupCall$9$orgtelegrammessengerChatObject$Call(final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ChatObject.Call.this.m123lambda$reloadGroupCall$8$orgtelegrammessengerChatObject$Call(response);
                }
            });
        }

        /* renamed from: lambda$reloadGroupCall$8$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m123lambda$reloadGroupCall$8$orgtelegrammessengerChatObject$Call(TLObject response) {
            if (response instanceof TLRPC.TL_phone_groupCall) {
                TLRPC.TL_phone_groupCall phoneGroupCall = (TLRPC.TL_phone_groupCall) response;
                this.call = phoneGroupCall.call;
                this.currentAccount.getMessagesController().putUsers(phoneGroupCall.users, false);
                this.currentAccount.getMessagesController().putChats(phoneGroupCall.chats, false);
                onParticipantsLoad(phoneGroupCall.participants, true, "", phoneGroupCall.participants_next_offset, phoneGroupCall.call.version, phoneGroupCall.call.participants_count);
            }
        }

        private void loadGroupCall() {
            if (this.loadingGroupCall || SystemClock.elapsedRealtime() - this.lastGroupCallReloadTime < 30000) {
                return;
            }
            this.loadingGroupCall = true;
            TLRPC.TL_phone_getGroupParticipants req = new TLRPC.TL_phone_getGroupParticipants();
            req.call = getInputGroupCall();
            req.offset = "";
            req.limit = 1;
            this.currentAccount.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda0
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatObject.Call.this.m117lambda$loadGroupCall$11$orgtelegrammessengerChatObject$Call(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$loadGroupCall$11$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m117lambda$loadGroupCall$11$orgtelegrammessengerChatObject$Call(final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ChatObject.Call.this.m116lambda$loadGroupCall$10$orgtelegrammessengerChatObject$Call(response);
                }
            });
        }

        /* renamed from: lambda$loadGroupCall$10$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m116lambda$loadGroupCall$10$orgtelegrammessengerChatObject$Call(TLObject response) {
            this.lastGroupCallReloadTime = SystemClock.elapsedRealtime();
            this.loadingGroupCall = false;
            if (response != null) {
                TLRPC.TL_phone_groupParticipants res = (TLRPC.TL_phone_groupParticipants) response;
                this.currentAccount.getMessagesController().putUsers(res.users, false);
                this.currentAccount.getMessagesController().putChats(res.chats, false);
                if (this.call.participants_count != res.count) {
                    this.call.participants_count = res.count;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("new participants reload count " + this.call.participants_count);
                    }
                    this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:116:0x0321  */
        /* JADX WARN: Removed duplicated region for block: B:117:0x032a  */
        /* JADX WARN: Removed duplicated region for block: B:134:0x0380  */
        /* JADX WARN: Removed duplicated region for block: B:59:0x017b  */
        /* JADX WARN: Removed duplicated region for block: B:83:0x0247  */
        /* JADX WARN: Removed duplicated region for block: B:86:0x025d  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void processParticipantsUpdate(org.telegram.tgnet.TLRPC.TL_updateGroupCallParticipants r33, boolean r34) {
            /*
                Method dump skipped, instructions count: 1341
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatObject.Call.processParticipantsUpdate(org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants, boolean):void");
        }

        private boolean isSameVideo(TLRPC.TL_groupCallParticipantVideo oldVideo, TLRPC.TL_groupCallParticipantVideo newVideo) {
            if ((oldVideo != null || newVideo == null) && (oldVideo == null || newVideo != null)) {
                if (oldVideo == null || newVideo == null) {
                    return true;
                }
                if (!TextUtils.equals(oldVideo.endpoint, newVideo.endpoint) || oldVideo.source_groups.size() != newVideo.source_groups.size()) {
                    return false;
                }
                int N = oldVideo.source_groups.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.TL_groupCallParticipantVideoSourceGroup oldGroup = oldVideo.source_groups.get(a);
                    TLRPC.TL_groupCallParticipantVideoSourceGroup newGroup = newVideo.source_groups.get(a);
                    if (!TextUtils.equals(oldGroup.semantics, newGroup.semantics) || oldGroup.sources.size() != newGroup.sources.size()) {
                        return false;
                    }
                    int N2 = oldGroup.sources.size();
                    for (int b = 0; b < N2; b++) {
                        if (!newGroup.sources.contains(oldGroup.sources.get(b))) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }

        public void processGroupCallUpdate(TLRPC.TL_updateGroupCall update) {
            if (this.call.version < update.call.version) {
                this.nextLoadOffset = null;
                loadMembers(true);
            }
            this.call = update.call;
            this.participants.get(getSelfId());
            this.recording = this.call.record_start_date != 0;
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
        }

        public TLRPC.TL_inputGroupCall getInputGroupCall() {
            TLRPC.TL_inputGroupCall inputGroupCall = new TLRPC.TL_inputGroupCall();
            inputGroupCall.id = this.call.id;
            inputGroupCall.access_hash = this.call.access_hash;
            return inputGroupCall;
        }

        public static boolean videoIsActive(TLRPC.TL_groupCallParticipant participant, boolean presentation, Call call) {
            VoIPService service;
            VideoParticipant videoParticipant;
            if (participant == null || (service = VoIPService.getSharedInstance()) == null) {
                return false;
            }
            if (participant.self) {
                return service.getVideoState(presentation) == 2;
            }
            VideoParticipant videoParticipant2 = call.rtmpStreamParticipant;
            if ((videoParticipant2 == null || videoParticipant2.participant != participant) && (((videoParticipant = call.videoNotAvailableParticipant) == null || videoParticipant.participant != participant) && call.participants.get(MessageObject.getPeerId(participant.peer)) == null)) {
                return false;
            }
            return presentation ? participant.presentation != null : participant.video != null;
        }

        public void sortParticipants() {
            TLRPC.TL_groupCallParticipant lastParticipant;
            boolean hasAnyVideo;
            boolean z;
            boolean z2;
            VideoParticipant videoParticipant;
            Comparator<TLRPC.TL_groupCallParticipant> comparator;
            this.visibleVideoParticipants.clear();
            this.visibleParticipants.clear();
            TLRPC.Chat chat = this.currentAccount.getMessagesController().getChat(Long.valueOf(this.chatId));
            final boolean isAdmin = ChatObject.canManageCalls(chat);
            VideoParticipant videoParticipant2 = this.rtmpStreamParticipant;
            if (videoParticipant2 != null) {
                this.visibleVideoParticipants.add(videoParticipant2);
            }
            final long selfId = getSelfId();
            VoIPService.getSharedInstance();
            this.participants.get(selfId);
            this.canStreamVideo = true;
            boolean hasAnyVideo2 = false;
            boolean z3 = false;
            this.activeVideos = 0;
            int N = this.sortedParticipants.size();
            for (int i = 0; i < N; i++) {
                TLRPC.TL_groupCallParticipant participant = this.sortedParticipants.get(i);
                boolean cameraActive = videoIsActive(participant, false, this);
                boolean screenActive = videoIsActive(participant, true, this);
                if (!participant.self && (cameraActive || screenActive)) {
                    this.activeVideos++;
                }
                if (cameraActive || screenActive) {
                    hasAnyVideo2 = true;
                    if (this.canStreamVideo) {
                        if (participant.videoIndex == 0) {
                            if (!participant.self) {
                                int i2 = videoPointer + 1;
                                videoPointer = i2;
                                participant.videoIndex = i2;
                            } else {
                                participant.videoIndex = Integer.MAX_VALUE;
                            }
                        }
                    } else {
                        participant.videoIndex = 0;
                    }
                } else if (participant.self || !this.canStreamVideo || (participant.video == null && participant.presentation == null)) {
                    participant.videoIndex = 0;
                }
            }
            Comparator<TLRPC.TL_groupCallParticipant> comparator2 = new Comparator() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda13
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ChatObject.Call.this.m126x43a3748(selfId, isAdmin, (TLRPC.TL_groupCallParticipant) obj, (TLRPC.TL_groupCallParticipant) obj2);
                }
            };
            Collections.sort(this.sortedParticipants, comparator2);
            if (this.sortedParticipants.isEmpty()) {
                lastParticipant = null;
            } else {
                ArrayList<TLRPC.TL_groupCallParticipant> arrayList = this.sortedParticipants;
                lastParticipant = arrayList.get(arrayList.size() - 1);
            }
            if ((videoIsActive(lastParticipant, false, this) || videoIsActive(lastParticipant, true, this)) && this.call.unmuted_video_count > this.activeVideos) {
                this.activeVideos = this.call.unmuted_video_count;
                VoIPService voIPService = VoIPService.getSharedInstance();
                if (voIPService != null && voIPService.groupCall == this && (voIPService.getVideoState(false) == 2 || voIPService.getVideoState(true) == 2)) {
                    this.activeVideos--;
                }
            }
            int i3 = 5000;
            if (this.sortedParticipants.size() <= 5000) {
                hasAnyVideo = hasAnyVideo2;
            } else {
                if (ChatObject.canManageCalls(chat)) {
                    hasAnyVideo = hasAnyVideo2;
                    if (lastParticipant.raise_hand_rating != 0) {
                    }
                } else {
                    hasAnyVideo = hasAnyVideo2;
                }
                int a = 5000;
                int N2 = this.sortedParticipants.size();
                while (a < N2) {
                    TLRPC.TL_groupCallParticipant p = this.sortedParticipants.get(i3);
                    if (p.raise_hand_rating != 0) {
                        comparator = comparator2;
                    } else {
                        processAllSources(p, z3);
                        comparator = comparator2;
                        this.participants.remove(MessageObject.getPeerId(p.peer));
                        this.sortedParticipants.remove(5000);
                    }
                    a++;
                    comparator2 = comparator;
                    z3 = false;
                    i3 = 5000;
                }
            }
            checkOnlineParticipants();
            if (!this.canStreamVideo && hasAnyVideo && (videoParticipant = this.videoNotAvailableParticipant) != null) {
                this.visibleVideoParticipants.add(videoParticipant);
            }
            int wideVideoIndex = 0;
            for (int i4 = 0; i4 < this.sortedParticipants.size(); i4++) {
                TLRPC.TL_groupCallParticipant participant2 = this.sortedParticipants.get(i4);
                if (this.canStreamVideo && participant2.videoIndex != 0) {
                    if (!participant2.self && videoIsActive(participant2, true, this) && videoIsActive(participant2, false, this)) {
                        VideoParticipant videoParticipant3 = this.videoParticipantsCache.get(participant2.videoEndpoint);
                        if (videoParticipant3 == null) {
                            videoParticipant3 = new VideoParticipant(participant2, false, true);
                            this.videoParticipantsCache.put(participant2.videoEndpoint, videoParticipant3);
                            z2 = true;
                        } else {
                            videoParticipant3.participant = participant2;
                            videoParticipant3.presentation = false;
                            z2 = true;
                            videoParticipant3.hasSame = true;
                        }
                        VideoParticipant presentationParticipant = this.videoParticipantsCache.get(participant2.presentationEndpoint);
                        if (presentationParticipant == null) {
                            presentationParticipant = new VideoParticipant(participant2, z2, z2);
                        } else {
                            presentationParticipant.participant = participant2;
                            presentationParticipant.presentation = z2;
                            presentationParticipant.hasSame = z2;
                        }
                        this.visibleVideoParticipants.add(videoParticipant3);
                        if (videoParticipant3.aspectRatio > 1.0f) {
                            wideVideoIndex = this.visibleVideoParticipants.size() - 1;
                        }
                        this.visibleVideoParticipants.add(presentationParticipant);
                        if (presentationParticipant.aspectRatio > 1.0f) {
                            wideVideoIndex = this.visibleVideoParticipants.size() - 1;
                        }
                    } else if (participant2.self) {
                        if (videoIsActive(participant2, true, this)) {
                            z = false;
                            this.visibleVideoParticipants.add(new VideoParticipant(participant2, true, false));
                        } else {
                            z = false;
                        }
                        if (videoIsActive(participant2, z, this)) {
                            this.visibleVideoParticipants.add(new VideoParticipant(participant2, z, z));
                        }
                    } else {
                        boolean presentation = videoIsActive(participant2, true, this);
                        VideoParticipant videoParticipant4 = this.videoParticipantsCache.get(presentation ? participant2.presentationEndpoint : participant2.videoEndpoint);
                        if (videoParticipant4 == null) {
                            videoParticipant4 = new VideoParticipant(participant2, presentation, false);
                            this.videoParticipantsCache.put(presentation ? participant2.presentationEndpoint : participant2.videoEndpoint, videoParticipant4);
                        } else {
                            videoParticipant4.participant = participant2;
                            videoParticipant4.presentation = presentation;
                            videoParticipant4.hasSame = false;
                        }
                        this.visibleVideoParticipants.add(videoParticipant4);
                        if (videoParticipant4.aspectRatio > 1.0f) {
                            wideVideoIndex = this.visibleVideoParticipants.size() - 1;
                        }
                    }
                } else {
                    this.visibleParticipants.add(participant2);
                }
            }
            if (!GroupCallActivity.isLandscapeMode && this.visibleVideoParticipants.size() % 2 == 1) {
                this.visibleVideoParticipants.add(this.visibleVideoParticipants.remove(wideVideoIndex));
            }
        }

        /* renamed from: lambda$sortParticipants$12$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ int m126x43a3748(long selfId, boolean isAdmin, TLRPC.TL_groupCallParticipant o1, TLRPC.TL_groupCallParticipant o2) {
            boolean videoActive2 = false;
            boolean videoActive1 = o1.videoIndex > 0;
            if (o2.videoIndex > 0) {
                videoActive2 = true;
            }
            if (!videoActive1 || !videoActive2) {
                if (videoActive1) {
                    return -1;
                }
                if (videoActive2) {
                    return 1;
                }
                if (o1.active_date != 0 && o2.active_date != 0) {
                    return zzby$$ExternalSyntheticBackport0.m(o2.active_date, o1.active_date);
                }
                if (o1.active_date != 0) {
                    return -1;
                }
                if (o2.active_date != 0) {
                    return 1;
                }
                if (MessageObject.getPeerId(o1.peer) == selfId) {
                    return -1;
                }
                if (MessageObject.getPeerId(o2.peer) == selfId) {
                    return 1;
                }
                if (isAdmin) {
                    if (o1.raise_hand_rating != 0 && o2.raise_hand_rating != 0) {
                        return (o2.raise_hand_rating > o1.raise_hand_rating ? 1 : (o2.raise_hand_rating == o1.raise_hand_rating ? 0 : -1));
                    }
                    if (o1.raise_hand_rating != 0) {
                        return -1;
                    }
                    if (o2.raise_hand_rating != 0) {
                        return 1;
                    }
                }
                if (this.call.join_date_asc) {
                    return zzby$$ExternalSyntheticBackport0.m(o1.date, o2.date);
                }
                return zzby$$ExternalSyntheticBackport0.m(o2.date, o1.date);
            }
            return o2.videoIndex - o1.videoIndex;
        }

        public boolean canRecordVideo() {
            if (!this.canStreamVideo) {
                return false;
            }
            VoIPService voIPService = VoIPService.getSharedInstance();
            return (voIPService != null && voIPService.groupCall == this && (voIPService.getVideoState(false) == 2 || voIPService.getVideoState(true) == 2)) || this.activeVideos < this.call.unmuted_video_limit;
        }

        public void saveActiveDates() {
            int N = this.sortedParticipants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant p = this.sortedParticipants.get(a);
                p.lastActiveDate = p.active_date;
            }
        }

        private void checkOnlineParticipants() {
            if (this.typingUpdateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.typingUpdateRunnable);
                this.typingUpdateRunnableScheduled = false;
            }
            this.speakingMembersCount = 0;
            int currentTime = this.currentAccount.getConnectionsManager().getCurrentTime();
            int minDiff = Integer.MAX_VALUE;
            int N = this.sortedParticipants.size();
            for (int a = 0; a < N; a++) {
                TLRPC.TL_groupCallParticipant participant = this.sortedParticipants.get(a);
                int diff = currentTime - participant.active_date;
                if (diff < 5) {
                    this.speakingMembersCount++;
                    minDiff = Math.min(diff, minDiff);
                }
                if (Math.max(participant.date, participant.active_date) <= currentTime - 5) {
                    break;
                }
            }
            if (minDiff != Integer.MAX_VALUE) {
                AndroidUtilities.runOnUIThread(this.typingUpdateRunnable, minDiff * 1000);
                this.typingUpdateRunnableScheduled = true;
            }
        }

        public void toggleRecord(String title, int type) {
            this.recording = !this.recording;
            TLRPC.TL_phone_toggleGroupCallRecord req = new TLRPC.TL_phone_toggleGroupCallRecord();
            req.call = getInputGroupCall();
            req.start = this.recording;
            if (title != null) {
                req.title = title;
                req.flags |= 2;
            }
            if (type == 1 || type == 2) {
                req.flags |= 4;
                req.video = true;
                req.video_portrait = type == 1;
            }
            this.currentAccount.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.ChatObject$Call$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatObject.Call.this.m127lambda$toggleRecord$13$orgtelegrammessengerChatObject$Call(tLObject, tL_error);
                }
            });
            this.currentAccount.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(this.chatId), Long.valueOf(this.call.id), false);
        }

        /* renamed from: lambda$toggleRecord$13$org-telegram-messenger-ChatObject$Call */
        public /* synthetic */ void m127lambda$toggleRecord$13$orgtelegrammessengerChatObject$Call(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                TLRPC.Updates res = (TLRPC.Updates) response;
                this.currentAccount.getMessagesController().processUpdates(res, false);
            }
        }
    }

    public static int getParticipantVolume(TLRPC.TL_groupCallParticipant participant) {
        if ((participant.flags & 128) != 0) {
            return participant.volume;
        }
        return 10000;
    }

    private static boolean isBannableAction(int action) {
        switch (action) {
            case 0:
            case 1:
            case 3:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return true;
            case 2:
            case 4:
            case 5:
            default:
                return false;
        }
    }

    private static boolean isAdminAction(int action) {
        switch (action) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 12:
            case 13:
                return true;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                return false;
        }
    }

    private static boolean getBannedRight(TLRPC.TL_chatBannedRights rights, int action) {
        if (rights == null) {
            return false;
        }
        switch (action) {
            case 0:
                return rights.pin_messages;
            case 1:
                return rights.change_info;
            case 2:
            case 4:
            case 5:
            default:
                return false;
            case 3:
                return rights.invite_users;
            case 6:
                return rights.send_messages;
            case 7:
                return rights.send_media;
            case 8:
                return rights.send_stickers;
            case 9:
                return rights.embed_links;
            case 10:
                return rights.send_polls;
            case 11:
                return rights.view_messages;
        }
    }

    public static boolean isActionBannedByDefault(TLRPC.Chat chat, int action) {
        if (getBannedRight(chat.banned_rights, action)) {
            return false;
        }
        return getBannedRight(chat.default_banned_rights, action);
    }

    public static boolean isActionBanned(TLRPC.Chat chat, int action) {
        return chat != null && (getBannedRight(chat.banned_rights, action) || getBannedRight(chat.default_banned_rights, action));
    }

    public static boolean canUserDoAdminAction(TLRPC.Chat chat, int action) {
        boolean value;
        if (chat == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        if (chat.admin_rights != null) {
            switch (action) {
                case 0:
                    value = chat.admin_rights.pin_messages;
                    break;
                case 1:
                    value = chat.admin_rights.change_info;
                    break;
                case 2:
                    value = chat.admin_rights.ban_users;
                    break;
                case 3:
                    value = chat.admin_rights.invite_users;
                    break;
                case 4:
                    value = chat.admin_rights.add_admins;
                    break;
                case 5:
                    value = chat.admin_rights.post_messages;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                default:
                    value = false;
                    break;
                case 12:
                    value = chat.admin_rights.edit_messages;
                    break;
                case 13:
                    value = chat.admin_rights.delete_messages;
                    break;
                case 14:
                    value = chat.admin_rights.manage_call;
                    break;
            }
            if (value) {
                return true;
            }
        }
        return false;
    }

    public static boolean canUserDoAction(TLRPC.Chat chat, int action) {
        if (chat == null || canUserDoAdminAction(chat, action)) {
            return true;
        }
        if (getBannedRight(chat.banned_rights, action) || !isBannableAction(action)) {
            return false;
        }
        if (chat.admin_rights != null && !isAdminAction(action)) {
            return true;
        }
        if (chat.default_banned_rights == null && ((chat instanceof TLRPC.TL_chat_layer92) || (chat instanceof TLRPC.TL_chat_old) || (chat instanceof TLRPC.TL_chat_old2) || (chat instanceof TLRPC.TL_channel_layer92) || (chat instanceof TLRPC.TL_channel_layer77) || (chat instanceof TLRPC.TL_channel_layer72) || (chat instanceof TLRPC.TL_channel_layer67) || (chat instanceof TLRPC.TL_channel_layer48) || (chat instanceof TLRPC.TL_channel_old))) {
            return true;
        }
        return chat.default_banned_rights != null && !getBannedRight(chat.default_banned_rights, action);
    }

    public static boolean isLeftFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.deactivated;
    }

    public static boolean isKickedFromChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.kicked || chat.deactivated || (chat.banned_rights != null && chat.banned_rights.view_messages);
    }

    public static boolean isNotInChat(TLRPC.Chat chat) {
        return chat == null || (chat instanceof TLRPC.TL_chatEmpty) || (chat instanceof TLRPC.TL_chatForbidden) || (chat instanceof TLRPC.TL_channelForbidden) || chat.left || chat.kicked || chat.deactivated;
    }

    public static boolean canSendAsPeers(TLRPC.Chat chat) {
        return isChannel(chat) && chat.megagroup && (!TextUtils.isEmpty(chat.username) || chat.has_geo || chat.has_link);
    }

    public static boolean isChannel(TLRPC.Chat chat) {
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isChannelOrGiga(TLRPC.Chat chat) {
        return ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) && (!chat.megagroup || chat.gigagroup);
    }

    public static boolean isMegagroup(TLRPC.Chat chat) {
        return ((chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden)) && chat.megagroup;
    }

    public static boolean isChannelAndNotMegaGroup(TLRPC.Chat chat) {
        return isChannel(chat) && !isMegagroup(chat);
    }

    public static boolean isMegagroup(int currentAccount, long chatId) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return isChannel(chat) && chat.megagroup;
    }

    public static boolean hasAdminRights(TLRPC.Chat chat) {
        return chat != null && (chat.creator || !(chat.admin_rights == null || chat.admin_rights.flags == 0));
    }

    public static boolean canChangeChatInfo(TLRPC.Chat chat) {
        return canUserDoAction(chat, 1);
    }

    public static boolean canAddAdmins(TLRPC.Chat chat) {
        return canUserDoAction(chat, 4);
    }

    public static boolean canBlockUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 2);
    }

    public static boolean canManageCalls(TLRPC.Chat chat) {
        return canUserDoAction(chat, 14);
    }

    public static boolean canSendStickers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 8);
    }

    public static boolean canSendEmbed(TLRPC.Chat chat) {
        return canUserDoAction(chat, 9);
    }

    public static boolean canSendMedia(TLRPC.Chat chat) {
        return canUserDoAction(chat, 7);
    }

    public static boolean canSendPolls(TLRPC.Chat chat) {
        return canUserDoAction(chat, 10);
    }

    public static boolean canSendMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 6);
    }

    public static boolean canPost(TLRPC.Chat chat) {
        return canUserDoAction(chat, 5);
    }

    public static boolean canAddUsers(TLRPC.Chat chat) {
        return canUserDoAction(chat, 3);
    }

    public static boolean shouldSendAnonymously(TLRPC.Chat chat) {
        return (chat == null || chat.admin_rights == null || !chat.admin_rights.anonymous) ? false : true;
    }

    public static long getSendAsPeerId(TLRPC.Chat chat, TLRPC.ChatFull chatFull) {
        return getSendAsPeerId(chat, chatFull, false);
    }

    public static long getSendAsPeerId(TLRPC.Chat chat, TLRPC.ChatFull chatFull, boolean invertChannel) {
        if (chat != null && chatFull != null && chatFull.default_send_as != null) {
            TLRPC.Peer p = chatFull.default_send_as;
            if (p.user_id != 0) {
                return p.user_id;
            }
            long j = p.channel_id;
            return invertChannel ? -j : j;
        } else if (chat != null && chat.admin_rights != null && chat.admin_rights.anonymous) {
            long j2 = chat.id;
            return invertChannel ? -j2 : j2;
        } else {
            return UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        }
    }

    public static boolean canAddBotsToChat(TLRPC.Chat chat) {
        if (!isChannel(chat)) {
            return chat.migrated_to == null;
        } else if (!chat.megagroup) {
            return false;
        } else {
            return (chat.admin_rights != null && (chat.admin_rights.post_messages || chat.admin_rights.add_admins)) || chat.creator;
        }
    }

    public static boolean canPinMessages(TLRPC.Chat chat) {
        return canUserDoAction(chat, 0) || (isChannel(chat) && !chat.megagroup && chat.admin_rights != null && chat.admin_rights.edit_messages);
    }

    public static boolean isChannel(long chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return (chat instanceof TLRPC.TL_channel) || (chat instanceof TLRPC.TL_channelForbidden);
    }

    public static boolean isChannelAndNotMegaGroup(long chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return isChannelAndNotMegaGroup(chat);
    }

    public static boolean isCanWriteToChannel(long chatId, int currentAccount) {
        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(chatId));
        return canSendMessages(chat) || chat.megagroup;
    }

    public static boolean canWriteToChat(TLRPC.Chat chat) {
        return !isChannel(chat) || chat.creator || (chat.admin_rights != null && chat.admin_rights.post_messages) || ((!chat.broadcast && !chat.gigagroup) || (chat.gigagroup && hasAdminRights(chat)));
    }

    public static String getBannedRightsString(TLRPC.TL_chatBannedRights bannedRights) {
        String currentBannedRights = "" + (bannedRights.view_messages ? 1 : 0);
        return (((((((((((currentBannedRights + (bannedRights.send_messages ? 1 : 0)) + (bannedRights.send_media ? 1 : 0)) + (bannedRights.send_stickers ? 1 : 0)) + (bannedRights.send_gifs ? 1 : 0)) + (bannedRights.send_games ? 1 : 0)) + (bannedRights.send_inline ? 1 : 0)) + (bannedRights.embed_links ? 1 : 0)) + (bannedRights.send_polls ? 1 : 0)) + (bannedRights.invite_users ? 1 : 0)) + (bannedRights.change_info ? 1 : 0)) + (bannedRights.pin_messages ? 1 : 0)) + bannedRights.until_date;
    }

    public static boolean hasPhoto(TLRPC.Chat chat) {
        return (chat == null || chat.photo == null || (chat.photo instanceof TLRPC.TL_chatPhotoEmpty)) ? false : true;
    }

    public static TLRPC.ChatPhoto getPhoto(TLRPC.Chat chat) {
        if (hasPhoto(chat)) {
            return chat.photo;
        }
        return null;
    }

    /* loaded from: classes4.dex */
    public static class VideoParticipant {
        public float aspectRatio;
        public int aspectRatioFromHeight;
        public int aspectRatioFromWidth;
        public boolean hasSame;
        public TLRPC.TL_groupCallParticipant participant;
        public boolean presentation;

        public VideoParticipant(TLRPC.TL_groupCallParticipant participant, boolean presentation, boolean hasSame) {
            this.participant = participant;
            this.presentation = presentation;
            this.hasSame = hasSame;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            VideoParticipant that = (VideoParticipant) o;
            return this.presentation == that.presentation && MessageObject.getPeerId(this.participant.peer) == MessageObject.getPeerId(that.participant.peer);
        }

        public void setAspectRatio(int width, int height, Call call) {
            this.aspectRatioFromWidth = width;
            this.aspectRatioFromHeight = height;
            setAspectRatio(width / height, call);
        }

        private void setAspectRatio(float aspectRatio, Call call) {
            if (this.aspectRatio != aspectRatio) {
                this.aspectRatio = aspectRatio;
                if (!GroupCallActivity.isLandscapeMode && call.visibleVideoParticipants.size() % 2 == 1) {
                    call.updateVisibleParticipants();
                }
            }
        }
    }
}
