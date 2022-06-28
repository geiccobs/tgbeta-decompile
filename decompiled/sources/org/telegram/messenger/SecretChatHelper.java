package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.util.MimeTypes;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLClassStore;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
/* loaded from: classes4.dex */
public class SecretChatHelper extends BaseController {
    public static int CURRENT_SECRET_CHAT_LAYER = 101;
    private static volatile SecretChatHelper[] Instance = new SecretChatHelper[4];
    private ArrayList<Integer> sendingNotifyLayer = new ArrayList<>();
    private SparseArray<ArrayList<TL_decryptedMessageHolder>> secretHolesQueue = new SparseArray<>();
    private SparseArray<ArrayList<TLRPC.Update>> pendingSecretMessages = new SparseArray<>();
    private SparseArray<SparseIntArray> requestedHoles = new SparseArray<>();
    private SparseArray<TLRPC.EncryptedChat> acceptingChats = new SparseArray<>();
    public ArrayList<TLRPC.Update> delayedEncryptedChatUpdates = new ArrayList<>();
    private ArrayList<Long> pendingEncMessagesToDelete = new ArrayList<>();
    private boolean startingSecretChat = false;

    /* loaded from: classes4.dex */
    public static class TL_decryptedMessageHolder extends TLObject {
        public static int constructor = 1431655929;
        public int date;
        public int decryptedWithVersion;
        public TLRPC.EncryptedFile file;
        public TLRPC.TL_decryptedMessageLayer layer;
        public boolean new_key_used;

        @Override // org.telegram.tgnet.TLObject
        public void readParams(AbstractSerializedData stream, boolean exception) {
            stream.readInt64(exception);
            this.date = stream.readInt32(exception);
            this.layer = TLRPC.TL_decryptedMessageLayer.TLdeserialize(stream, stream.readInt32(exception), exception);
            if (stream.readBool(exception)) {
                this.file = TLRPC.EncryptedFile.TLdeserialize(stream, stream.readInt32(exception), exception);
            }
            this.new_key_used = stream.readBool(exception);
        }

        @Override // org.telegram.tgnet.TLObject
        public void serializeToStream(AbstractSerializedData stream) {
            stream.writeInt32(constructor);
            stream.writeInt64(0L);
            stream.writeInt32(this.date);
            this.layer.serializeToStream(stream);
            stream.writeBool(this.file != null);
            TLRPC.EncryptedFile encryptedFile = this.file;
            if (encryptedFile != null) {
                encryptedFile.serializeToStream(stream);
            }
            stream.writeBool(this.new_key_used);
        }
    }

    public static SecretChatHelper getInstance(int num) {
        SecretChatHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (SecretChatHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    SecretChatHelper[] secretChatHelperArr = Instance;
                    SecretChatHelper secretChatHelper = new SecretChatHelper(num);
                    localInstance = secretChatHelper;
                    secretChatHelperArr[num] = secretChatHelper;
                }
            }
        }
        return localInstance;
    }

    public SecretChatHelper(int instance) {
        super(instance);
    }

    public void cleanup() {
        this.sendingNotifyLayer.clear();
        this.acceptingChats.clear();
        this.secretHolesQueue.clear();
        this.pendingSecretMessages.clear();
        this.requestedHoles.clear();
        this.delayedEncryptedChatUpdates.clear();
        this.pendingEncMessagesToDelete.clear();
        this.startingSecretChat = false;
    }

    public void processPendingEncMessages() {
        if (!this.pendingEncMessagesToDelete.isEmpty()) {
            final ArrayList<Long> pendingEncMessagesToDeleteCopy = new ArrayList<>(this.pendingEncMessagesToDelete);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1143xc3b53c7c(pendingEncMessagesToDeleteCopy);
                }
            });
            ArrayList<Long> arr = new ArrayList<>(this.pendingEncMessagesToDelete);
            getMessagesStorage().markMessagesAsDeletedByRandoms(arr);
            this.pendingEncMessagesToDelete.clear();
        }
    }

    /* renamed from: lambda$processPendingEncMessages$0$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1143xc3b53c7c(ArrayList pendingEncMessagesToDeleteCopy) {
        for (int a = 0; a < pendingEncMessagesToDeleteCopy.size(); a++) {
            MessageObject messageObject = getMessagesController().dialogMessagesByRandomIds.get(((Long) pendingEncMessagesToDeleteCopy.get(a)).longValue());
            if (messageObject != null) {
                messageObject.deleted = true;
            }
        }
    }

    private TLRPC.TL_messageService createServiceSecretMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.DecryptedMessageAction decryptedMessage) {
        TLRPC.TL_messageService newMsg = new TLRPC.TL_messageService();
        newMsg.action = new TLRPC.TL_messageEncryptedAction();
        newMsg.action.encryptedAction = decryptedMessage;
        int newMessageId = getUserConfig().getNewMessageId();
        newMsg.id = newMessageId;
        newMsg.local_id = newMessageId;
        newMsg.from_id = new TLRPC.TL_peerUser();
        newMsg.from_id.user_id = getUserConfig().getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = DialogObject.makeEncryptedDialogId(encryptedChat.id);
        newMsg.peer_id = new TLRPC.TL_peerUser();
        newMsg.send_state = 1;
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            newMsg.peer_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.peer_id.user_id = encryptedChat.participant_id;
        }
        if ((decryptedMessage instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (decryptedMessage instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
            newMsg.date = getConnectionsManager().getCurrentTime();
        } else {
            newMsg.date = 0;
        }
        newMsg.random_id = getSendMessagesHelper().getNextRandomId();
        getUserConfig().saveConfig(false);
        ArrayList<TLRPC.Message> arr = new ArrayList<>();
        arr.add(newMsg);
        getMessagesStorage().putMessages(arr, false, true, true, 0, false);
        return newMsg;
    }

    public void sendMessagesReadMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionReadMessages();
            reqSend.action.random_ids = random_ids;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.concurrent.ConcurrentHashMap != java.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$User> */
    public void processUpdateEncryption(TLRPC.TL_updateEncryption update, ConcurrentHashMap<Long, TLRPC.User> concurrentHashMap) {
        final TLRPC.EncryptedChat newChat = update.chat;
        final long dialog_id = DialogObject.makeEncryptedDialogId(newChat.id);
        final TLRPC.EncryptedChat existingChat = getMessagesController().getEncryptedChatDB(newChat.id, false);
        if ((newChat instanceof TLRPC.TL_encryptedChatRequested) && existingChat == null) {
            long userId = newChat.participant_id;
            if (userId == getUserConfig().getClientUserId()) {
                userId = newChat.admin_id;
            }
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(userId));
            if (user == null) {
                user = concurrentHashMap.get(Long.valueOf(userId));
            }
            newChat.user_id = userId;
            final TLRPC.Dialog dialog = new TLRPC.TL_dialog();
            dialog.id = dialog_id;
            dialog.folder_id = newChat.folder_id;
            dialog.unread_count = 0;
            dialog.top_message = 0;
            dialog.last_message_date = update.date;
            getMessagesController().putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1144xe82e9eba(dialog, dialog_id);
                }
            });
            getMessagesStorage().putEncryptedChat(newChat, user, dialog);
            acceptSecretChat(newChat);
        } else if (newChat instanceof TLRPC.TL_encryptedChat) {
            if ((existingChat instanceof TLRPC.TL_encryptedChatWaiting) && (existingChat.auth_key == null || existingChat.auth_key.length == 1)) {
                newChat.a_or_b = existingChat.a_or_b;
                newChat.user_id = existingChat.user_id;
                processAcceptedSecretChat(newChat);
            } else if (existingChat == null && this.startingSecretChat) {
                this.delayedEncryptedChatUpdates.add(update);
            }
        } else {
            if (existingChat != null) {
                newChat.user_id = existingChat.user_id;
                newChat.auth_key = existingChat.auth_key;
                newChat.key_create_date = existingChat.key_create_date;
                newChat.key_use_count_in = existingChat.key_use_count_in;
                newChat.key_use_count_out = existingChat.key_use_count_out;
                newChat.ttl = existingChat.ttl;
                newChat.seq_in = existingChat.seq_in;
                newChat.seq_out = existingChat.seq_out;
                newChat.admin_id = existingChat.admin_id;
                newChat.mtproto_seq = existingChat.mtproto_seq;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1145x74cec9bb(existingChat, newChat);
                }
            });
        }
        if ((newChat instanceof TLRPC.TL_encryptedChatDiscarded) && newChat.history_deleted) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda29
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1146x16ef4bc(dialog_id);
                }
            });
        }
    }

    /* renamed from: lambda$processUpdateEncryption$1$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1144xe82e9eba(TLRPC.Dialog dialog, long dialog_id) {
        if (dialog.folder_id == 1) {
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            editor.putBoolean("dialog_bar_archived" + dialog_id, true);
            editor.commit();
        }
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs(null);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* renamed from: lambda$processUpdateEncryption$2$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1145x74cec9bb(TLRPC.EncryptedChat exist, TLRPC.EncryptedChat newChat) {
        if (exist != null) {
            getMessagesController().putEncryptedChat(newChat, false);
        }
        getMessagesStorage().updateEncryptedChat(newChat);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    /* renamed from: lambda$processUpdateEncryption$3$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1146x16ef4bc(long dialog_id) {
        getMessagesController().deleteDialog(dialog_id, 0);
    }

    public void sendMessagesDeleteMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionDeleteMessages();
            reqSend.action.random_ids = random_ids;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendClearHistoryMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionFlushHistory();
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendNotifyLayerMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat) || this.sendingNotifyLayer.contains(Integer.valueOf(encryptedChat.id))) {
            return;
        }
        this.sendingNotifyLayer.add(Integer.valueOf(encryptedChat.id));
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionNotifyLayer();
            reqSend.action.layer = CURRENT_SECRET_CHAT_LAYER;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendRequestKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionRequestKey();
            reqSend.action.exchange_id = encryptedChat.exchange_id;
            reqSend.action.g_a = encryptedChat.g_a;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendAcceptKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionAcceptKey();
            reqSend.action.exchange_id = encryptedChat.exchange_id;
            reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
            reqSend.action.g_b = encryptedChat.g_a_or_b;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendCommitKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionCommitKey();
            reqSend.action.exchange_id = encryptedChat.exchange_id;
            reqSend.action.key_fingerprint = encryptedChat.future_key_fingerprint;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendAbortKeyMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage, long excange_id) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionAbortKey();
            reqSend.action.exchange_id = excange_id;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendNoopMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionNoop();
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendResendMessage(TLRPC.EncryptedChat encryptedChat, int start, int end, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        SparseIntArray array = this.requestedHoles.get(encryptedChat.id);
        if (array != null && array.indexOfKey(start) >= 0) {
            return;
        }
        if (array == null) {
            array = new SparseIntArray();
            this.requestedHoles.put(encryptedChat.id, array);
        }
        array.put(start, end);
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionResend();
            reqSend.action.start_seq_no = start;
            reqSend.action.end_seq_no = end;
            message = createServiceSecretMessage(encryptedChat, reqSend.action);
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendTTLMessage(TLRPC.EncryptedChat encryptedChat, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionSetMessageTTL();
            reqSend.action.ttl_seconds = encryptedChat.ttl;
            TLRPC.Message message2 = createServiceSecretMessage(encryptedChat, reqSend.action);
            MessageObject newMsgObj = new MessageObject(this.currentAccount, message2, false, false);
            newMsgObj.messageOwner.send_state = 1;
            newMsgObj.wasJustSent = true;
            ArrayList<MessageObject> objArr = new ArrayList<>();
            objArr.add(newMsgObj);
            getMessagesController().updateInterfaceWithMessages(message2.dialog_id, objArr, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            message = message2;
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    public void sendScreenshotMessage(TLRPC.EncryptedChat encryptedChat, ArrayList<Long> random_ids, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (!(encryptedChat instanceof TLRPC.TL_encryptedChat)) {
            return;
        }
        TLRPC.TL_decryptedMessageService reqSend = new TLRPC.TL_decryptedMessageService();
        if (resendMessage != null) {
            reqSend.action = resendMessage.action.encryptedAction;
            message = resendMessage;
        } else {
            reqSend.action = new TLRPC.TL_decryptedMessageActionScreenshotMessages();
            reqSend.action.random_ids = random_ids;
            TLRPC.Message message2 = createServiceSecretMessage(encryptedChat, reqSend.action);
            MessageObject newMsgObj = new MessageObject(this.currentAccount, message2, false, false);
            newMsgObj.messageOwner.send_state = 1;
            newMsgObj.wasJustSent = true;
            ArrayList<MessageObject> objArr = new ArrayList<>();
            objArr.add(newMsgObj);
            getMessagesController().updateInterfaceWithMessages(message2.dialog_id, objArr, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            message = message2;
        }
        reqSend.random_id = message.random_id;
        performSendEncryptedRequest(reqSend, message, encryptedChat, null, null, null);
    }

    private void updateMediaPaths(MessageObject newMsgObj, TLRPC.EncryptedFile file, TLRPC.DecryptedMessage decryptedMessage, String originalPath) {
        TLRPC.Message newMsg = newMsgObj.messageOwner;
        if (file != null) {
            if ((newMsg.media instanceof TLRPC.TL_messageMediaPhoto) && newMsg.media.photo != null) {
                TLRPC.PhotoSize size = newMsg.media.photo.sizes.get(newMsg.media.photo.sizes.size() - 1);
                String fileName = size.location.volume_id + "_" + size.location.local_id;
                size.location = new TLRPC.TL_fileEncryptedLocation();
                size.location.key = decryptedMessage.media.key;
                size.location.iv = decryptedMessage.media.iv;
                size.location.dc_id = file.dc_id;
                size.location.volume_id = file.id;
                size.location.secret = file.access_hash;
                size.location.local_id = file.key_fingerprint;
                String fileName2 = size.location.volume_id + "_" + size.location.local_id;
                File cacheFile = new File(FileLoader.getDirectory(4), fileName + ".jpg");
                File cacheFile2 = getFileLoader().getPathToAttach(size);
                cacheFile.renameTo(cacheFile2);
                ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, ImageLocation.getForPhoto(size, newMsg.media.photo), true);
                ArrayList<TLRPC.Message> arr = new ArrayList<>();
                arr.add(newMsg);
                getMessagesStorage().putMessages(arr, false, true, false, 0, false);
            } else if ((newMsg.media instanceof TLRPC.TL_messageMediaDocument) && newMsg.media.document != null) {
                TLRPC.Document document = newMsg.media.document;
                newMsg.media.document = new TLRPC.TL_documentEncrypted();
                newMsg.media.document.id = file.id;
                newMsg.media.document.access_hash = file.access_hash;
                newMsg.media.document.date = document.date;
                newMsg.media.document.attributes = document.attributes;
                newMsg.media.document.mime_type = document.mime_type;
                newMsg.media.document.size = file.size;
                newMsg.media.document.key = decryptedMessage.media.key;
                newMsg.media.document.iv = decryptedMessage.media.iv;
                newMsg.media.document.thumbs = document.thumbs;
                newMsg.media.document.dc_id = file.dc_id;
                if (newMsg.media.document.thumbs.isEmpty()) {
                    TLRPC.PhotoSize thumb = new TLRPC.TL_photoSizeEmpty();
                    thumb.type = "s";
                    newMsg.media.document.thumbs.add(thumb);
                }
                if (newMsg.attachPath != null && newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath())) {
                    File cacheFile3 = new File(newMsg.attachPath);
                    File cacheFile22 = getFileLoader().getPathToAttach(newMsg.media.document);
                    if (cacheFile3.renameTo(cacheFile22)) {
                        newMsgObj.mediaExists = newMsgObj.attachPathExists;
                        newMsgObj.attachPathExists = false;
                        newMsg.attachPath = "";
                    }
                }
                ArrayList<TLRPC.Message> arr2 = new ArrayList<>();
                arr2.add(newMsg);
                getMessagesStorage().putMessages(arr2, false, true, false, 0, false);
            }
        }
    }

    public static boolean isSecretVisibleMessage(TLRPC.Message message) {
        return (message.action instanceof TLRPC.TL_messageEncryptedAction) && ((message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL));
    }

    public static boolean isSecretInvisibleMessage(TLRPC.Message message) {
        return (message.action instanceof TLRPC.TL_messageEncryptedAction) && !(message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) && !(message.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL);
    }

    public void performSendEncryptedRequest(TLRPC.TL_messages_sendEncryptedMultiMedia req, SendMessagesHelper.DelayedMessage message) {
        for (int a = 0; a < req.files.size(); a++) {
            performSendEncryptedRequest(req.messages.get(a), message.messages.get(a), message.encryptedChat, req.files.get(a), message.originalPaths.get(a), message.messageObjects.get(a));
        }
    }

    public void performSendEncryptedRequest(final TLRPC.DecryptedMessage req, final TLRPC.Message newMsgObj, final TLRPC.EncryptedChat chat, final TLRPC.InputEncryptedFile encryptedFile, final String originalPath, final MessageObject newMsg) {
        if (req != null && chat.auth_key != null && !(chat instanceof TLRPC.TL_encryptedChatRequested)) {
            if (chat instanceof TLRPC.TL_encryptedChatWaiting) {
                return;
            }
            getSendMessagesHelper().putToSendingMessages(newMsgObj, false);
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1137x85cf021a(chat, req, newMsgObj, encryptedFile, newMsg, originalPath);
                }
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$performSendEncryptedRequest$8$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1137x85cf021a(final TLRPC.EncryptedChat chat, final TLRPC.DecryptedMessage req, final TLRPC.Message newMsgObj, TLRPC.InputEncryptedFile encryptedFile, final MessageObject newMsg, final String originalPath) {
        TLObject reqToSend;
        try {
            TLRPC.TL_decryptedMessageLayer layer = new TLRPC.TL_decryptedMessageLayer();
            int myLayer = Math.max(46, AndroidUtilities.getMyLayerVersion(chat.layer));
            layer.layer = Math.min(myLayer, Math.max(46, AndroidUtilities.getPeerLayerVersion(chat.layer)));
            layer.message = req;
            layer.random_bytes = new byte[15];
            Utilities.random.nextBytes(layer.random_bytes);
            if (chat.seq_in == 0 && chat.seq_out == 0) {
                if (chat.admin_id == getUserConfig().getClientUserId()) {
                    chat.seq_out = 1;
                    chat.seq_in = -2;
                } else {
                    chat.seq_in = -1;
                }
            }
            if (newMsgObj.seq_in == 0 && newMsgObj.seq_out == 0) {
                layer.in_seq_no = chat.seq_in > 0 ? chat.seq_in : chat.seq_in + 2;
                layer.out_seq_no = chat.seq_out;
                chat.seq_out += 2;
                if (chat.key_create_date == 0) {
                    chat.key_create_date = getConnectionsManager().getCurrentTime();
                }
                chat.key_use_count_out = (short) (chat.key_use_count_out + 1);
                if ((chat.key_use_count_out >= 100 || chat.key_create_date < getConnectionsManager().getCurrentTime() - 604800) && chat.exchange_id == 0 && chat.future_key_fingerprint == 0) {
                    requestNewSecretChatKey(chat);
                }
                getMessagesStorage().updateEncryptedChatSeq(chat, false);
                newMsgObj.seq_in = layer.in_seq_no;
                newMsgObj.seq_out = layer.out_seq_no;
                getMessagesStorage().setMessageSeq(newMsgObj.id, newMsgObj.seq_in, newMsgObj.seq_out);
            } else {
                layer.in_seq_no = newMsgObj.seq_in;
                layer.out_seq_no = newMsgObj.seq_out;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d(req + " send message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
            }
            int len = layer.getObjectSize();
            NativeByteBuffer toEncrypt = new NativeByteBuffer(len + 4);
            toEncrypt.writeInt32(len);
            layer.serializeToStream(toEncrypt);
            int len2 = toEncrypt.length();
            int extraLen = (len2 % 16 != 0 ? 16 - (len2 % 16) : 0) + ((Utilities.random.nextInt(3) + 2) * 16);
            NativeByteBuffer dataForEncryption = new NativeByteBuffer(len2 + extraLen);
            toEncrypt.position(0);
            dataForEncryption.writeBytes(toEncrypt);
            if (extraLen != 0) {
                byte[] b = new byte[extraLen];
                Utilities.random.nextBytes(b);
                dataForEncryption.writeBytes(b);
            }
            byte[] b2 = new byte[16];
            boolean incoming = chat.admin_id != getUserConfig().getClientUserId();
            byte[] messageKeyFull = Utilities.computeSHA256(chat.auth_key, 88 + (incoming ? 8 : 0), 32, dataForEncryption.buffer, 0, dataForEncryption.buffer.limit());
            System.arraycopy(messageKeyFull, 8, b2, 0, 16);
            toEncrypt.reuse();
            MessageKeyData keyData = MessageKeyData.generateMessageKeyData(chat.auth_key, b2, incoming, 2);
            Utilities.aesIgeEncryption(dataForEncryption.buffer, keyData.aesKey, keyData.aesIv, true, false, 0, dataForEncryption.limit());
            NativeByteBuffer data = new NativeByteBuffer(b2.length + 8 + dataForEncryption.length());
            dataForEncryption.position(0);
            data.writeInt64(chat.key_fingerprint);
            data.writeBytes(b2);
            data.writeBytes(dataForEncryption);
            dataForEncryption.reuse();
            data.position(0);
            if (encryptedFile == null) {
                if (req instanceof TLRPC.TL_decryptedMessageService) {
                    TLRPC.TL_messages_sendEncryptedService req2 = new TLRPC.TL_messages_sendEncryptedService();
                    req2.data = data;
                    req2.random_id = req.random_id;
                    req2.peer = new TLRPC.TL_inputEncryptedChat();
                    req2.peer.chat_id = chat.id;
                    req2.peer.access_hash = chat.access_hash;
                    reqToSend = req2;
                } else {
                    TLRPC.TL_messages_sendEncrypted req22 = new TLRPC.TL_messages_sendEncrypted();
                    req22.silent = newMsgObj.silent;
                    req22.data = data;
                    req22.random_id = req.random_id;
                    req22.peer = new TLRPC.TL_inputEncryptedChat();
                    req22.peer.chat_id = chat.id;
                    req22.peer.access_hash = chat.access_hash;
                    reqToSend = req22;
                }
            } else {
                TLRPC.TL_messages_sendEncryptedFile req23 = new TLRPC.TL_messages_sendEncryptedFile();
                req23.silent = newMsgObj.silent;
                req23.data = data;
                req23.random_id = req.random_id;
                req23.peer = new TLRPC.TL_inputEncryptedChat();
                req23.peer.chat_id = chat.id;
                req23.peer.access_hash = chat.access_hash;
                req23.file = encryptedFile;
                reqToSend = req23;
            }
            getConnectionsManager().sendRequest(reqToSend, new RequestDelegate() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda21
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.m1136xf92ed719(req, chat, newMsgObj, newMsg, originalPath, tLObject, tL_error);
                }
            }, 64);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$performSendEncryptedRequest$7$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1136xf92ed719(TLRPC.DecryptedMessage req, TLRPC.EncryptedChat chat, final TLRPC.Message newMsgObj, MessageObject newMsg, String originalPath, TLObject response, TLRPC.TL_error error) {
        int existFlags;
        TLRPC.EncryptedChat currentChat;
        if (error == null && (req.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
            TLRPC.EncryptedChat currentChat2 = getMessagesController().getEncryptedChat(Integer.valueOf(chat.id));
            if (currentChat2 != null) {
                currentChat = currentChat2;
            } else {
                currentChat = chat;
            }
            if (currentChat.key_hash == null) {
                currentChat.key_hash = AndroidUtilities.calcAuthKeyHash(currentChat.auth_key);
            }
            if (currentChat.key_hash.length == 16) {
                try {
                    byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                    byte[] key_hash = new byte[36];
                    System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                    System.arraycopy(sha256, 0, key_hash, 16, 20);
                    currentChat.key_hash = key_hash;
                    getMessagesStorage().updateEncryptedChat(currentChat);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            this.sendingNotifyLayer.remove(Integer.valueOf(currentChat.id));
            currentChat.layer = AndroidUtilities.setMyLayerVersion(currentChat.layer, CURRENT_SECRET_CHAT_LAYER);
            getMessagesStorage().updateEncryptedChatLayer(currentChat);
        }
        if (error != null) {
            getMessagesStorage().markMessageAsSendError(newMsgObj, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1135x6c8eac18(newMsgObj);
                }
            });
            return;
        }
        final String attachPath = newMsgObj.attachPath;
        final TLRPC.messages_SentEncryptedMessage res = (TLRPC.messages_SentEncryptedMessage) response;
        if (isSecretVisibleMessage(newMsgObj)) {
            newMsgObj.date = res.date;
        }
        if (newMsg != null && (res.file instanceof TLRPC.TL_encryptedFile)) {
            updateMediaPaths(newMsg, res.file, req, originalPath);
            existFlags = newMsg.getMediaExistanceFlags();
        } else {
            existFlags = 0;
        }
        final int i = existFlags;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1134xdfee8117(newMsgObj, res, i, attachPath);
            }
        });
    }

    /* renamed from: lambda$performSendEncryptedRequest$5$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1134xdfee8117(final TLRPC.Message newMsgObj, TLRPC.messages_SentEncryptedMessage res, final int existFlags, final String attachPath) {
        if (isSecretInvisibleMessage(newMsgObj)) {
            res.date = 0;
        }
        getMessagesStorage().updateMessageStateAndId(newMsgObj.random_id, 0L, Integer.valueOf(newMsgObj.id), newMsgObj.id, res.date, false, 0);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1133x534e5616(newMsgObj, existFlags, attachPath);
            }
        });
    }

    /* renamed from: lambda$performSendEncryptedRequest$4$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1133x534e5616(TLRPC.Message newMsgObj, int existFlags, String attachPath) {
        newMsgObj.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(newMsgObj.id), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), 0L, Integer.valueOf(existFlags), false);
        getSendMessagesHelper().processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            getSendMessagesHelper().stopVideoService(attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(newMsgObj.id, false);
    }

    /* renamed from: lambda$performSendEncryptedRequest$6$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1135x6c8eac18(TLRPC.Message newMsgObj) {
        newMsgObj.send_state = 2;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
        getSendMessagesHelper().processSentMessage(newMsgObj.id);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj)) {
            getSendMessagesHelper().stopVideoService(newMsgObj.attachPath);
        }
        getSendMessagesHelper().removeFromSendingMessages(newMsgObj.id, false);
    }

    private void applyPeerLayer(final TLRPC.EncryptedChat chat, int newPeerLayer) {
        int currentPeerLayer = AndroidUtilities.getPeerLayerVersion(chat.layer);
        if (newPeerLayer <= currentPeerLayer) {
            return;
        }
        if (chat.key_hash.length == 16) {
            try {
                byte[] sha256 = Utilities.computeSHA256(chat.auth_key, 0, chat.auth_key.length);
                byte[] key_hash = new byte[36];
                System.arraycopy(chat.key_hash, 0, key_hash, 0, 16);
                System.arraycopy(sha256, 0, key_hash, 16, 20);
                chat.key_hash = key_hash;
                getMessagesStorage().updateEncryptedChat(chat);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        chat.layer = AndroidUtilities.setPeerLayerVersion(chat.layer, newPeerLayer);
        getMessagesStorage().updateEncryptedChatLayer(chat);
        if (currentPeerLayer < CURRENT_SECRET_CHAT_LAYER) {
            sendNotifyLayerMessage(chat, null);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1130lambda$applyPeerLayer$9$orgtelegrammessengerSecretChatHelper(chat);
            }
        });
    }

    /* renamed from: lambda$applyPeerLayer$9$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1130lambda$applyPeerLayer$9$orgtelegrammessengerSecretChatHelper(TLRPC.EncryptedChat chat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, chat);
    }

    public TLRPC.Message processDecryptedObject(TLRPC.EncryptedChat chat, TLRPC.EncryptedFile file, int date, TLObject object, boolean new_key_used) {
        int i;
        TLRPC.PhotoSize photoSize;
        TLRPC.PhotoSize photoSize2;
        if (object != null) {
            long from_id = chat.admin_id;
            if (from_id == getUserConfig().getClientUserId()) {
                from_id = chat.participant_id;
            }
            if (chat.exchange_id == 0 && chat.future_key_fingerprint == 0 && chat.key_use_count_in >= 120) {
                requestNewSecretChatKey(chat);
            }
            if (chat.exchange_id == 0 && chat.future_key_fingerprint != 0 && !new_key_used) {
                chat.future_auth_key = new byte[256];
                chat.future_key_fingerprint = 0L;
                getMessagesStorage().updateEncryptedChat(chat);
            } else if (chat.exchange_id != 0 && new_key_used) {
                chat.key_fingerprint = chat.future_key_fingerprint;
                chat.auth_key = chat.future_auth_key;
                chat.key_create_date = getConnectionsManager().getCurrentTime();
                chat.future_auth_key = new byte[256];
                chat.future_key_fingerprint = 0L;
                chat.key_use_count_in = (short) 0;
                chat.key_use_count_out = (short) 0;
                chat.exchange_id = 0L;
                getMessagesStorage().updateEncryptedChat(chat);
            }
            if (object instanceof TLRPC.TL_decryptedMessage) {
                TLRPC.TL_decryptedMessage decryptedMessage = (TLRPC.TL_decryptedMessage) object;
                TLRPC.TL_message newMessage = new TLRPC.TL_message_secret();
                newMessage.ttl = decryptedMessage.ttl;
                newMessage.entities = decryptedMessage.entities;
                newMessage.message = decryptedMessage.message;
                newMessage.date = date;
                int newMessageId = getUserConfig().getNewMessageId();
                newMessage.id = newMessageId;
                newMessage.local_id = newMessageId;
                newMessage.silent = decryptedMessage.silent;
                getUserConfig().saveConfig(false);
                newMessage.from_id = new TLRPC.TL_peerUser();
                newMessage.from_id.user_id = from_id;
                newMessage.peer_id = new TLRPC.TL_peerUser();
                newMessage.peer_id.user_id = getUserConfig().getClientUserId();
                newMessage.random_id = decryptedMessage.random_id;
                newMessage.unread = true;
                newMessage.flags = 768;
                if (decryptedMessage.via_bot_name != null && decryptedMessage.via_bot_name.length() > 0) {
                    newMessage.via_bot_name = decryptedMessage.via_bot_name;
                    newMessage.flags |= 2048;
                }
                if (decryptedMessage.grouped_id != 0) {
                    newMessage.grouped_id = decryptedMessage.grouped_id;
                    newMessage.flags |= 131072;
                }
                newMessage.dialog_id = DialogObject.makeEncryptedDialogId(chat.id);
                if (decryptedMessage.reply_to_random_id != 0) {
                    newMessage.reply_to = new TLRPC.TL_messageReplyHeader();
                    newMessage.reply_to.reply_to_random_id = decryptedMessage.reply_to_random_id;
                    newMessage.flags |= 8;
                }
                if (decryptedMessage.media == null || (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaEmpty)) {
                    newMessage.media = new TLRPC.TL_messageMediaEmpty();
                } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaWebPage) {
                    newMessage.media = new TLRPC.TL_messageMediaWebPage();
                    newMessage.media.webpage = new TLRPC.TL_webPageUrlPending();
                    newMessage.media.webpage.url = decryptedMessage.media.url;
                } else {
                    String str = "";
                    if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaContact) {
                        newMessage.media = new TLRPC.TL_messageMediaContact();
                        newMessage.media.last_name = decryptedMessage.media.last_name;
                        newMessage.media.first_name = decryptedMessage.media.first_name;
                        newMessage.media.phone_number = decryptedMessage.media.phone_number;
                        newMessage.media.user_id = decryptedMessage.media.user_id;
                        newMessage.media.vcard = str;
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaGeoPoint) {
                        newMessage.media = new TLRPC.TL_messageMediaGeo();
                        newMessage.media.geo = new TLRPC.TL_geoPoint();
                        newMessage.media.geo.lat = decryptedMessage.media.lat;
                        newMessage.media.geo._long = decryptedMessage.media._long;
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaPhoto) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaPhoto();
                        newMessage.media.flags |= 3;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            if (decryptedMessage.media.caption != null) {
                                str = decryptedMessage.media.caption;
                            }
                            newMessage.message = str;
                        }
                        newMessage.media.photo = new TLRPC.TL_photo();
                        newMessage.media.photo.file_reference = new byte[0];
                        newMessage.media.photo.date = newMessage.date;
                        byte[] thumb = ((TLRPC.TL_decryptedMessageMediaPhoto) decryptedMessage.media).thumb;
                        if (thumb != null && thumb.length != 0 && thumb.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                            TLRPC.TL_photoCachedSize small = new TLRPC.TL_photoCachedSize();
                            small.w = decryptedMessage.media.thumb_w;
                            small.h = decryptedMessage.media.thumb_h;
                            small.bytes = thumb;
                            small.type = "s";
                            small.location = new TLRPC.TL_fileLocationUnavailable();
                            newMessage.media.photo.sizes.add(small);
                        }
                        if (newMessage.ttl != 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            newMessage.media.flags |= 4;
                        }
                        TLRPC.TL_photoSize big = new TLRPC.TL_photoSize_layer127();
                        big.w = decryptedMessage.media.w;
                        big.h = decryptedMessage.media.h;
                        big.type = "x";
                        big.size = (int) file.size;
                        big.location = new TLRPC.TL_fileEncryptedLocation();
                        big.location.key = decryptedMessage.media.key;
                        big.location.iv = decryptedMessage.media.iv;
                        big.location.dc_id = file.dc_id;
                        big.location.volume_id = file.id;
                        big.location.secret = file.access_hash;
                        big.location.local_id = file.key_fingerprint;
                        newMessage.media.photo.sizes.add(big);
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVideo) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        newMessage.media.document.dc_id = file.dc_id;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            if (decryptedMessage.media.caption != null) {
                                str = decryptedMessage.media.caption;
                            }
                            newMessage.message = str;
                        }
                        newMessage.media.document.date = date;
                        newMessage.media.document.size = file.size;
                        newMessage.media.document.id = file.id;
                        newMessage.media.document.access_hash = file.access_hash;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = MimeTypes.VIDEO_MP4;
                        }
                        byte[] thumb2 = ((TLRPC.TL_decryptedMessageMediaVideo) decryptedMessage.media).thumb;
                        if (thumb2 != null && thumb2.length != 0 && thumb2.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                            photoSize2 = new TLRPC.TL_photoCachedSize();
                            photoSize2.bytes = thumb2;
                            photoSize2.w = decryptedMessage.media.thumb_w;
                            photoSize2.h = decryptedMessage.media.thumb_h;
                            photoSize2.type = "s";
                            photoSize2.location = new TLRPC.TL_fileLocationUnavailable();
                        } else {
                            photoSize2 = new TLRPC.TL_photoSizeEmpty();
                            photoSize2.type = "s";
                        }
                        newMessage.media.document.thumbs.add(photoSize2);
                        newMessage.media.document.flags |= 1;
                        TLRPC.TL_documentAttributeVideo attributeVideo = new TLRPC.TL_documentAttributeVideo();
                        attributeVideo.w = decryptedMessage.media.w;
                        attributeVideo.h = decryptedMessage.media.h;
                        attributeVideo.duration = decryptedMessage.media.duration;
                        attributeVideo.supports_streaming = false;
                        newMessage.media.document.attributes.add(attributeVideo);
                        if (newMessage.ttl != 0) {
                            newMessage.media.ttl_seconds = newMessage.ttl;
                            newMessage.media.flags |= 4;
                        }
                        if (newMessage.ttl != 0) {
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            newMessage.message = decryptedMessage.media.caption != null ? decryptedMessage.media.caption : str;
                        }
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.id = file.id;
                        newMessage.media.document.access_hash = file.access_hash;
                        newMessage.media.document.date = date;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument_layer8) {
                            TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                            fileName.file_name = decryptedMessage.media.file_name;
                            newMessage.media.document.attributes.add(fileName);
                        } else {
                            newMessage.media.document.attributes = decryptedMessage.media.attributes;
                        }
                        if (newMessage.ttl > 0) {
                            int N = newMessage.media.document.attributes.size();
                            for (int a = 0; a < N; a++) {
                                TLRPC.DocumentAttribute attribute = newMessage.media.document.attributes.get(a);
                                if ((attribute instanceof TLRPC.TL_documentAttributeAudio) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                    newMessage.ttl = Math.max(attribute.duration + 1, newMessage.ttl);
                                    break;
                                }
                            }
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                        newMessage.media.document.size = decryptedMessage.media.size != 0 ? Math.min(decryptedMessage.media.size, file.size) : file.size;
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = str;
                        } else if ("application/x-tgsticker".equals(newMessage.media.document.mime_type) || "application/x-tgsdice".equals(newMessage.media.document.mime_type)) {
                            newMessage.media.document.mime_type = "application/x-bad_tgsticker";
                        }
                        byte[] thumb3 = ((TLRPC.TL_decryptedMessageMediaDocument) decryptedMessage.media).thumb;
                        if (thumb3 != null && thumb3.length != 0 && thumb3.length <= 6000 && decryptedMessage.media.thumb_w <= 100 && decryptedMessage.media.thumb_h <= 100) {
                            photoSize = new TLRPC.TL_photoCachedSize();
                            photoSize.bytes = thumb3;
                            photoSize.w = decryptedMessage.media.thumb_w;
                            photoSize.h = decryptedMessage.media.thumb_h;
                            photoSize.type = "s";
                            photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                        } else {
                            photoSize = new TLRPC.TL_photoSizeEmpty();
                            photoSize.type = "s";
                        }
                        newMessage.media.document.thumbs.add(photoSize);
                        newMessage.media.document.flags |= 1;
                        newMessage.media.document.dc_id = file.dc_id;
                        if (MessageObject.isVoiceMessage(newMessage) || MessageObject.isRoundVideoMessage(newMessage)) {
                            newMessage.media_unread = true;
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaExternalDocument) {
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.message = str;
                        newMessage.media.document = new TLRPC.TL_document();
                        newMessage.media.document.id = decryptedMessage.media.id;
                        newMessage.media.document.access_hash = decryptedMessage.media.access_hash;
                        newMessage.media.document.file_reference = new byte[0];
                        newMessage.media.document.date = decryptedMessage.media.date;
                        newMessage.media.document.attributes = decryptedMessage.media.attributes;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        newMessage.media.document.dc_id = decryptedMessage.media.dc_id;
                        newMessage.media.document.size = decryptedMessage.media.size;
                        newMessage.media.document.thumbs.add(((TLRPC.TL_decryptedMessageMediaExternalDocument) decryptedMessage.media).thumb);
                        newMessage.media.document.flags |= 1;
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = str;
                        }
                        if (MessageObject.isAnimatedStickerMessage(newMessage)) {
                            newMessage.stickerVerified = 0;
                            getMediaDataController().verifyAnimatedStickerMessage(newMessage, true);
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaAudio) {
                        if (decryptedMessage.media.key == null || decryptedMessage.media.key.length != 32 || decryptedMessage.media.iv == null || decryptedMessage.media.iv.length != 32) {
                            return null;
                        }
                        newMessage.media = new TLRPC.TL_messageMediaDocument();
                        newMessage.media.flags |= 3;
                        newMessage.media.document = new TLRPC.TL_documentEncrypted();
                        newMessage.media.document.key = decryptedMessage.media.key;
                        newMessage.media.document.iv = decryptedMessage.media.iv;
                        newMessage.media.document.id = file.id;
                        newMessage.media.document.access_hash = file.access_hash;
                        newMessage.media.document.date = date;
                        newMessage.media.document.size = file.size;
                        newMessage.media.document.dc_id = file.dc_id;
                        newMessage.media.document.mime_type = decryptedMessage.media.mime_type;
                        if (TextUtils.isEmpty(newMessage.message)) {
                            if (decryptedMessage.media.caption != null) {
                                str = decryptedMessage.media.caption;
                            }
                            newMessage.message = str;
                        }
                        if (newMessage.media.document.mime_type == null) {
                            newMessage.media.document.mime_type = "audio/ogg";
                        }
                        TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                        attributeAudio.duration = decryptedMessage.media.duration;
                        attributeAudio.voice = true;
                        newMessage.media.document.attributes.add(attributeAudio);
                        if (newMessage.ttl != 0) {
                            newMessage.ttl = Math.max(decryptedMessage.media.duration + 1, newMessage.ttl);
                        }
                        if (newMessage.media.document.thumbs.isEmpty()) {
                            TLRPC.PhotoSize thumb4 = new TLRPC.TL_photoSizeEmpty();
                            thumb4.type = "s";
                            newMessage.media.document.thumbs.add(thumb4);
                        }
                    } else if (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVenue) {
                        newMessage.media = new TLRPC.TL_messageMediaVenue();
                        newMessage.media.geo = new TLRPC.TL_geoPoint();
                        newMessage.media.geo.lat = decryptedMessage.media.lat;
                        newMessage.media.geo._long = decryptedMessage.media._long;
                        newMessage.media.title = decryptedMessage.media.title;
                        newMessage.media.address = decryptedMessage.media.address;
                        newMessage.media.provider = decryptedMessage.media.provider;
                        newMessage.media.venue_id = decryptedMessage.media.venue_id;
                        newMessage.media.venue_type = str;
                    } else {
                        return null;
                    }
                }
                if (newMessage.ttl != 0 && newMessage.media.ttl_seconds == 0) {
                    newMessage.media.ttl_seconds = newMessage.ttl;
                    newMessage.media.flags |= 4;
                }
                if (newMessage.message != null) {
                    newMessage.message = newMessage.message.replace((char) 8238, ' ');
                }
                return newMessage;
            }
            long from_id2 = from_id;
            if (!(object instanceof TLRPC.TL_decryptedMessageService)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("unknown message " + object);
                    return null;
                }
                return null;
            }
            TLRPC.TL_decryptedMessageService serviceMessage = (TLRPC.TL_decryptedMessageService) object;
            if ((serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) || (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
                TLRPC.TL_messageService newMessage2 = new TLRPC.TL_messageService();
                if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                    newMessage2.action = new TLRPC.TL_messageEncryptedAction();
                    if (serviceMessage.action.ttl_seconds < 0 || serviceMessage.action.ttl_seconds > 31536000) {
                        serviceMessage.action.ttl_seconds = 31536000;
                    }
                    chat.ttl = serviceMessage.action.ttl_seconds;
                    newMessage2.action.encryptedAction = serviceMessage.action;
                    getMessagesStorage().updateEncryptedChatTTL(chat);
                } else {
                    newMessage2.action = new TLRPC.TL_messageEncryptedAction();
                    newMessage2.action.encryptedAction = serviceMessage.action;
                }
                int newMessageId2 = getUserConfig().getNewMessageId();
                newMessage2.id = newMessageId2;
                newMessage2.local_id = newMessageId2;
                getUserConfig().saveConfig(false);
                newMessage2.unread = true;
                newMessage2.flags = 256;
                newMessage2.date = date;
                newMessage2.from_id = new TLRPC.TL_peerUser();
                newMessage2.from_id.user_id = from_id2;
                newMessage2.peer_id = new TLRPC.TL_peerUser();
                newMessage2.peer_id.user_id = getUserConfig().getClientUserId();
                newMessage2.dialog_id = DialogObject.makeEncryptedDialogId(chat.id);
                return newMessage2;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                final long did = DialogObject.makeEncryptedDialogId(chat.id);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda28
                    @Override // java.lang.Runnable
                    public final void run() {
                        SecretChatHelper.this.m1142x53e0a31b(did);
                    }
                });
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                if (!serviceMessage.action.random_ids.isEmpty()) {
                    this.pendingEncMessagesToDelete.addAll(serviceMessage.action.random_ids);
                    return null;
                }
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                if (!serviceMessage.action.random_ids.isEmpty()) {
                    int time = getConnectionsManager().getCurrentTime();
                    getMessagesStorage().createTaskForSecretChat(chat.id, time, time, 1, serviceMessage.action.random_ids);
                    return null;
                }
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                applyPeerLayer(chat, serviceMessage.action.layer);
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                if (chat.exchange_id != 0) {
                    if (chat.exchange_id <= serviceMessage.action.exchange_id) {
                        sendAbortKeyMessage(chat, null, chat.exchange_id);
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("we already have request key with higher exchange_id");
                        return null;
                    } else {
                        return null;
                    }
                }
                byte[] salt = new byte[256];
                Utilities.random.nextBytes(salt);
                BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                BigInteger g_b = BigInteger.valueOf(getMessagesStorage().getSecretG());
                BigInteger g_b2 = g_b.modPow(new BigInteger(1, salt), p);
                BigInteger g_a = new BigInteger(1, serviceMessage.action.g_a);
                if (!Utilities.isGoodGaAndGb(g_a, p)) {
                    sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                    return null;
                }
                byte[] g_b_bytes = g_b2.toByteArray();
                if (g_b_bytes.length <= 256) {
                    i = 1;
                } else {
                    byte[] correctedAuth = new byte[256];
                    i = 1;
                    System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                    g_b_bytes = correctedAuth;
                }
                byte[] authKey = g_a.modPow(new BigInteger(i, salt), p).toByteArray();
                if (authKey.length > 256) {
                    byte[] correctedAuth2 = new byte[256];
                    System.arraycopy(authKey, authKey.length - 256, correctedAuth2, 0, 256);
                    authKey = correctedAuth2;
                } else if (authKey.length < 256) {
                    byte[] correctedAuth3 = new byte[256];
                    System.arraycopy(authKey, 0, correctedAuth3, 256 - authKey.length, authKey.length);
                    for (int a2 = 0; a2 < 256 - authKey.length; a2++) {
                        correctedAuth3[a2] = 0;
                    }
                    authKey = correctedAuth3;
                }
                byte[] authKeyHash = Utilities.computeSHA1(authKey);
                byte[] authKeyId = new byte[8];
                System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
                chat.exchange_id = serviceMessage.action.exchange_id;
                chat.future_auth_key = authKey;
                chat.future_key_fingerprint = Utilities.bytesToLong(authKeyId);
                chat.g_a_or_b = g_b_bytes;
                getMessagesStorage().updateEncryptedChat(chat);
                sendAcceptKeyMessage(chat, null);
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                if (chat.exchange_id == serviceMessage.action.exchange_id) {
                    BigInteger p2 = new BigInteger(1, getMessagesStorage().getSecretPBytes());
                    BigInteger i_authKey = new BigInteger(1, serviceMessage.action.g_b);
                    if (!Utilities.isGoodGaAndGb(i_authKey, p2)) {
                        chat.future_auth_key = new byte[256];
                        chat.future_key_fingerprint = 0L;
                        chat.exchange_id = 0L;
                        getMessagesStorage().updateEncryptedChat(chat);
                        sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                        return null;
                    }
                    byte[] authKey2 = i_authKey.modPow(new BigInteger(1, chat.a_or_b), p2).toByteArray();
                    if (authKey2.length > 256) {
                        byte[] correctedAuth4 = new byte[256];
                        System.arraycopy(authKey2, authKey2.length - 256, correctedAuth4, 0, 256);
                        authKey2 = correctedAuth4;
                    } else if (authKey2.length < 256) {
                        byte[] correctedAuth5 = new byte[256];
                        byte b = 0;
                        System.arraycopy(authKey2, 0, correctedAuth5, 256 - authKey2.length, authKey2.length);
                        int a3 = 0;
                        while (a3 < 256 - authKey2.length) {
                            correctedAuth5[a3] = b;
                            a3++;
                            b = 0;
                        }
                        authKey2 = correctedAuth5;
                    }
                    byte[] authKeyHash2 = Utilities.computeSHA1(authKey2);
                    byte[] authKeyId2 = new byte[8];
                    System.arraycopy(authKeyHash2, authKeyHash2.length - 8, authKeyId2, 0, 8);
                    long fingerprint = Utilities.bytesToLong(authKeyId2);
                    if (serviceMessage.action.key_fingerprint == fingerprint) {
                        chat.future_auth_key = authKey2;
                        chat.future_key_fingerprint = fingerprint;
                        getMessagesStorage().updateEncryptedChat(chat);
                        sendCommitKeyMessage(chat, null);
                        return null;
                    }
                    chat.future_auth_key = new byte[256];
                    chat.future_key_fingerprint = 0L;
                    chat.exchange_id = 0L;
                    getMessagesStorage().updateEncryptedChat(chat);
                    sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                    return null;
                }
                chat.future_auth_key = new byte[256];
                chat.future_key_fingerprint = 0L;
                chat.exchange_id = 0L;
                getMessagesStorage().updateEncryptedChat(chat);
                sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                if (chat.exchange_id == serviceMessage.action.exchange_id && chat.future_key_fingerprint == serviceMessage.action.key_fingerprint) {
                    long old_fingerprint = chat.key_fingerprint;
                    byte[] old_key = chat.auth_key;
                    chat.key_fingerprint = chat.future_key_fingerprint;
                    chat.auth_key = chat.future_auth_key;
                    chat.key_create_date = getConnectionsManager().getCurrentTime();
                    chat.future_auth_key = old_key;
                    chat.future_key_fingerprint = old_fingerprint;
                    chat.key_use_count_in = (short) 0;
                    chat.key_use_count_out = (short) 0;
                    chat.exchange_id = 0L;
                    getMessagesStorage().updateEncryptedChat(chat);
                    sendNoopMessage(chat, null);
                    return null;
                }
                chat.future_auth_key = new byte[256];
                chat.future_key_fingerprint = 0L;
                chat.exchange_id = 0L;
                getMessagesStorage().updateEncryptedChat(chat);
                sendAbortKeyMessage(chat, null, serviceMessage.action.exchange_id);
                return null;
            } else if (serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                if (chat.exchange_id == serviceMessage.action.exchange_id) {
                    chat.future_auth_key = new byte[256];
                    chat.future_key_fingerprint = 0L;
                    chat.exchange_id = 0L;
                    getMessagesStorage().updateEncryptedChat(chat);
                    return null;
                }
                return null;
            } else if ((serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionNoop) || !(serviceMessage.action instanceof TLRPC.TL_decryptedMessageActionResend) || serviceMessage.action.end_seq_no < chat.in_seq_no || serviceMessage.action.end_seq_no < serviceMessage.action.start_seq_no) {
                return null;
            } else {
                if (serviceMessage.action.start_seq_no < chat.in_seq_no) {
                    serviceMessage.action.start_seq_no = chat.in_seq_no;
                }
                resendMessages(serviceMessage.action.start_seq_no, serviceMessage.action.end_seq_no, chat);
                return null;
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.e("unknown TLObject");
            return null;
        } else {
            return null;
        }
    }

    /* renamed from: lambda$processDecryptedObject$12$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1142x53e0a31b(final long did) {
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
        if (dialog != null) {
            dialog.unread_count = 0;
            getMessagesController().dialogMessage.remove(dialog.id);
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1141xc740781a(did);
            }
        });
        getMessagesStorage().deleteDialog(did, 1);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), false, null);
    }

    /* renamed from: lambda$processDecryptedObject$11$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1141xc740781a(final long did) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1140x3aa04d19(did);
            }
        });
    }

    /* renamed from: lambda$processDecryptedObject$10$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1140x3aa04d19(long did) {
        getNotificationsController().processReadMessages(null, did, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray dialogsToUpdate = new LongSparseIntArray(1);
        dialogsToUpdate.put(did, 0);
        getNotificationsController().processDialogsUpdateRead(dialogsToUpdate);
    }

    private TLRPC.Message createDeleteMessage(int mid, int seq_out, int seq_in, long random_id, TLRPC.EncryptedChat encryptedChat) {
        TLRPC.TL_messageService newMsg = new TLRPC.TL_messageService();
        newMsg.action = new TLRPC.TL_messageEncryptedAction();
        newMsg.action.encryptedAction = new TLRPC.TL_decryptedMessageActionDeleteMessages();
        newMsg.action.encryptedAction.random_ids.add(Long.valueOf(random_id));
        newMsg.id = mid;
        newMsg.local_id = mid;
        newMsg.from_id = new TLRPC.TL_peerUser();
        newMsg.from_id.user_id = getUserConfig().getClientUserId();
        newMsg.unread = true;
        newMsg.out = true;
        newMsg.flags = 256;
        newMsg.dialog_id = DialogObject.makeEncryptedDialogId(encryptedChat.id);
        newMsg.send_state = 1;
        newMsg.seq_in = seq_in;
        newMsg.seq_out = seq_out;
        newMsg.peer_id = new TLRPC.TL_peerUser();
        if (encryptedChat.participant_id == getUserConfig().getClientUserId()) {
            newMsg.peer_id.user_id = encryptedChat.admin_id;
        } else {
            newMsg.peer_id.user_id = encryptedChat.participant_id;
        }
        newMsg.date = 0;
        newMsg.random_id = random_id;
        return newMsg;
    }

    private void resendMessages(final int startSeq, final int endSeq, final TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat == null || endSeq - startSeq < 0) {
            return;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1148lambda$resendMessages$15$orgtelegrammessengerSecretChatHelper(startSeq, encryptedChat, endSeq);
            }
        });
    }

    /* renamed from: lambda$resendMessages$15$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1148lambda$resendMessages$15$orgtelegrammessengerSecretChatHelper(int startSeq, TLRPC.EncryptedChat encryptedChat, int endSeq) {
        Exception e;
        long dialog_id;
        SparseArray<TLRPC.Message> messagesToResend;
        ArrayList<TLRPC.Message> messages;
        int seq_out;
        TLRPC.Message message;
        int sSeq = startSeq;
        try {
            if (encryptedChat.admin_id == getUserConfig().getClientUserId() && sSeq % 2 == 0) {
                sSeq++;
            }
            boolean z = false;
            int i = 1;
            int i2 = 3;
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid FROM requested_holes WHERE uid = %d AND ((seq_out_start >= %d AND %d <= seq_out_end) OR (seq_out_start >= %d AND %d <= seq_out_end))", Integer.valueOf(encryptedChat.id), Integer.valueOf(sSeq), Integer.valueOf(sSeq), Integer.valueOf(endSeq), Integer.valueOf(endSeq)), new Object[0]);
            boolean exists = cursor.next();
            cursor.dispose();
            if (exists) {
                return;
            }
            long dialog_id2 = DialogObject.makeEncryptedDialogId(encryptedChat.id);
            SparseArray<TLRPC.Message> messagesToResend2 = new SparseArray<>();
            ArrayList<TLRPC.Message> messages2 = new ArrayList<>();
            for (int a = sSeq; a <= endSeq; a += 2) {
                messagesToResend2.put(a, null);
            }
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, r.random_id, s.seq_in, s.seq_out, m.ttl, s.mid FROM messages_seq as s LEFT JOIN randoms_v2 as r ON r.mid = s.mid LEFT JOIN messages_v2 as m ON m.mid = s.mid WHERE m.uid = %d AND m.out = 1 AND s.seq_out >= %d AND s.seq_out <= %d ORDER BY seq_out ASC", Long.valueOf(dialog_id2), Integer.valueOf(sSeq), Integer.valueOf(endSeq)), new Object[0]);
            while (cursor2.next()) {
                long random_id = cursor2.longValue(i);
                if (random_id == 0) {
                    random_id = Utilities.random.nextLong();
                }
                int seq_in = cursor2.intValue(2);
                int seq_out2 = cursor2.intValue(i2);
                int mid = cursor2.intValue(5);
                long random_id2 = random_id;
                int i3 = z ? 1 : 0;
                int i4 = z ? 1 : 0;
                NativeByteBuffer data = cursor2.byteBufferValue(i3);
                if (data != null) {
                    TLRPC.Message message2 = TLRPC.Message.TLdeserialize(data, data.readInt32(z), z);
                    message2.readAttachPath(data, getUserConfig().clientUserId);
                    data.reuse();
                    message2.random_id = random_id2;
                    message2.dialog_id = dialog_id2;
                    message2.seq_in = seq_in;
                    seq_out = seq_out2;
                    message2.seq_out = seq_out;
                    message2.ttl = cursor2.intValue(4);
                    dialog_id = dialog_id2;
                    message = message2;
                    messages = messages2;
                    messagesToResend = messagesToResend2;
                } else {
                    seq_out = seq_out2;
                    messages = messages2;
                    dialog_id = dialog_id2;
                    messagesToResend = messagesToResend2;
                    message = createDeleteMessage(mid, seq_out, seq_in, random_id2, encryptedChat);
                }
                messages.add(message);
                messagesToResend.remove(seq_out);
                messages2 = messages;
                messagesToResend2 = messagesToResend;
                dialog_id2 = dialog_id;
                z = false;
                i = 1;
                i2 = 3;
            }
            final ArrayList<TLRPC.Message> messages3 = messages2;
            SparseArray<TLRPC.Message> messagesToResend3 = messagesToResend2;
            cursor2.dispose();
            if (messagesToResend3.size() != 0) {
                for (int a2 = 0; a2 < messagesToResend3.size(); a2++) {
                    int seq = messagesToResend3.keyAt(a2);
                    messages3.add(createDeleteMessage(getUserConfig().getNewMessageId(), seq, seq + 1, Utilities.random.nextLong(), encryptedChat));
                }
                getUserConfig().saveConfig(false);
            }
            Collections.sort(messages3, SecretChatHelper$$ExternalSyntheticLambda17.INSTANCE);
            ArrayList<TLRPC.EncryptedChat> encryptedChats = new ArrayList<>();
            encryptedChats.add(encryptedChat);
            try {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        SecretChatHelper.this.m1147lambda$resendMessages$14$orgtelegrammessengerSecretChatHelper(messages3);
                    }
                });
                getSendMessagesHelper().processUnsentMessages(messages3, null, new ArrayList<>(), new ArrayList<>(), encryptedChats);
                getMessagesStorage().getDatabase().executeFast(String.format(Locale.US, "REPLACE INTO requested_holes VALUES(%d, %d, %d)", Integer.valueOf(encryptedChat.id), Integer.valueOf(sSeq), Integer.valueOf(endSeq))).stepThis().dispose();
            } catch (Exception e2) {
                e = e2;
                FileLog.e(e);
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    /* renamed from: lambda$resendMessages$14$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1147lambda$resendMessages$14$orgtelegrammessengerSecretChatHelper(ArrayList messages) {
        for (int a = 0; a < messages.size(); a++) {
            TLRPC.Message message = (TLRPC.Message) messages.get(a);
            MessageObject messageObject = new MessageObject(this.currentAccount, message, false, true);
            messageObject.resendAsIs = true;
            getSendMessagesHelper().retrySendMessage(messageObject, true);
        }
    }

    public void checkSecretHoles(TLRPC.EncryptedChat chat, ArrayList<TLRPC.Message> messages) {
        ArrayList<TL_decryptedMessageHolder> holes = this.secretHolesQueue.get(chat.id);
        if (holes == null) {
            return;
        }
        Collections.sort(holes, SecretChatHelper$$ExternalSyntheticLambda16.INSTANCE);
        boolean update = false;
        int a = 0;
        while (a < holes.size()) {
            TL_decryptedMessageHolder holder = holes.get(a);
            if (holder.layer.out_seq_no != chat.seq_in && chat.seq_in != holder.layer.out_seq_no - 2) {
                break;
            }
            applyPeerLayer(chat, holder.layer.layer);
            chat.seq_in = holder.layer.out_seq_no;
            chat.in_seq_no = holder.layer.in_seq_no;
            holes.remove(a);
            int a2 = a - 1;
            update = true;
            if (holder.decryptedWithVersion == 2) {
                chat.mtproto_seq = Math.min(chat.mtproto_seq, chat.seq_in);
            }
            TLRPC.Message message = processDecryptedObject(chat, holder.file, holder.date, holder.layer.message, holder.new_key_used);
            if (message != null) {
                messages.add(message);
            }
            a = a2 + 1;
        }
        if (holes.isEmpty()) {
            this.secretHolesQueue.remove(chat.id);
        }
        if (update) {
            getMessagesStorage().updateEncryptedChatSeq(chat, true);
        }
    }

    public static /* synthetic */ int lambda$checkSecretHoles$16(TL_decryptedMessageHolder lhs, TL_decryptedMessageHolder rhs) {
        if (lhs.layer.out_seq_no > rhs.layer.out_seq_no) {
            return 1;
        }
        if (lhs.layer.out_seq_no < rhs.layer.out_seq_no) {
            return -1;
        }
        return 0;
    }

    private boolean decryptWithMtProtoVersion(NativeByteBuffer is, byte[] keyToDecrypt, byte[] messageKey, int version, boolean incoming, boolean encryptOnError) {
        boolean incoming2;
        if (version != 1) {
            incoming2 = incoming;
        } else {
            incoming2 = false;
        }
        MessageKeyData keyData = MessageKeyData.generateMessageKeyData(keyToDecrypt, messageKey, incoming2, version);
        Utilities.aesIgeEncryption(is.buffer, keyData.aesKey, keyData.aesIv, false, false, 24, is.limit() - 24);
        int error = 0;
        int len = is.readInt32(false);
        if (version == 2) {
            if (!Utilities.arraysEquals(messageKey, 0, Utilities.computeSHA256(keyToDecrypt, (incoming2 ? 8 : 0) + 88, 32, is.buffer, 24, is.buffer.limit()), 8)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(is.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    is.position(24);
                }
                error = 0 | 1;
            }
        } else {
            int l = len + 28;
            if (l < is.buffer.limit() - 15 || l > is.buffer.limit()) {
                l = is.buffer.limit();
            }
            byte[] messageKeyFull = Utilities.computeSHA1(is.buffer, 24, l);
            if (!Utilities.arraysEquals(messageKey, 0, messageKeyFull, messageKeyFull.length - 16)) {
                if (encryptOnError) {
                    Utilities.aesIgeEncryption(is.buffer, keyData.aesKey, keyData.aesIv, true, false, 24, is.limit() - 24);
                    is.position(24);
                }
                error = 0 | 1;
            }
        }
        if (len <= 0) {
            error |= 1;
        }
        if (len > is.limit() - 28) {
            error |= 1;
        }
        int padding = (is.limit() - 28) - len;
        if (version == 2) {
            if (padding < 12) {
                error |= 1;
            }
            if (padding > 1024) {
                error |= 1;
            }
        } else if (padding > 15) {
            error |= 1;
        }
        return error ^ 1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.telegram.messenger.MessagesStorage] */
    /* JADX WARN: Type inference failed for: r25v0, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v13, types: [org.telegram.tgnet.TLRPC$Message, java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>] */
    /* JADX WARN: Type inference failed for: r2v14 */
    /* JADX WARN: Type inference failed for: r2v15 */
    /* JADX WARN: Type inference failed for: r2v18 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* JADX WARN: Type inference failed for: r5v15 */
    /* JADX WARN: Type inference failed for: r5v16 */
    /* JADX WARN: Type inference failed for: r5v18 */
    /* JADX WARN: Type inference failed for: r5v5, types: [int, boolean] */
    public ArrayList<TLRPC.Message> decryptMessage(TLRPC.EncryptedMessage message) {
        ArrayList<TLRPC.Message> arrayList;
        ArrayList<TLRPC.Message> arrayList2;
        Exception e;
        byte[] keyToDecrypt;
        boolean new_key_used;
        NativeByteBuffer is;
        int decryptedWithVersion;
        ?? r5;
        boolean new_key_used2;
        ?? r2;
        TLRPC.EncryptedChat chat;
        TLObject object;
        TLRPC.EncryptedChat chat2 = getMessagesController().getEncryptedChatDB(message.chat_id, true);
        if (chat2 != null && !(chat2 instanceof TLRPC.TL_encryptedChatDiscarded)) {
            try {
                try {
                } catch (Exception e2) {
                    e = e2;
                    arrayList2 = null;
                }
            } catch (Exception e3) {
                e = e3;
                arrayList2 = null;
            }
            if (chat2 instanceof TLRPC.TL_encryptedChatWaiting) {
                ArrayList<TLRPC.Update> updates = this.pendingSecretMessages.get(chat2.id);
                if (updates == null) {
                    updates = new ArrayList<>();
                    this.pendingSecretMessages.put(chat2.id, updates);
                }
                TLRPC.TL_updateNewEncryptedMessage updateNewEncryptedMessage = new TLRPC.TL_updateNewEncryptedMessage();
                updateNewEncryptedMessage.message = message;
                updates.add(updateNewEncryptedMessage);
                return null;
            }
            NativeByteBuffer is2 = new NativeByteBuffer(message.bytes.length);
            is2.writeBytes(message.bytes);
            is2.position(0);
            long fingerprint = is2.readInt64(false);
            arrayList2 = 0;
            arrayList2 = 0;
            if (chat2.key_fingerprint == fingerprint) {
                keyToDecrypt = chat2.auth_key;
                new_key_used = false;
            } else if (chat2.future_key_fingerprint == 0 || chat2.future_key_fingerprint != fingerprint) {
                keyToDecrypt = null;
                new_key_used = false;
            } else {
                arrayList2 = 1;
                keyToDecrypt = chat2.future_auth_key;
                new_key_used = true;
            }
            try {
            } catch (Exception e4) {
                e = e4;
            }
            if (keyToDecrypt == null) {
                ArrayList<TLRPC.Message> arrayList3 = null;
                is2.reuse();
                arrayList = arrayList3;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e(String.format("fingerprint mismatch %x", Long.valueOf(fingerprint)));
                    arrayList = arrayList3;
                }
                return arrayList;
            }
            byte[] messageKey = is2.readData(16, false);
            boolean incoming = chat2.admin_id == getUserConfig().getClientUserId();
            boolean tryAnotherDecrypt = 2 != 2 || chat2.mtproto_seq == 0;
            try {
                if (decryptWithMtProtoVersion(is2, keyToDecrypt, messageKey, 2, incoming, tryAnotherDecrypt)) {
                    new_key_used2 = new_key_used;
                    is = is2;
                    chat = chat2;
                    r2 = 0;
                    r5 = 1;
                    decryptedWithVersion = 2;
                } else if (2 != 2) {
                    new_key_used2 = new_key_used;
                    is = is2;
                    chat = chat2;
                    r2 = 0;
                    r5 = 1;
                    if (!decryptWithMtProtoVersion(is, keyToDecrypt, messageKey, 2, incoming, true)) {
                        return null;
                    }
                    decryptedWithVersion = 2;
                } else if (!tryAnotherDecrypt) {
                    return null;
                } else {
                    new_key_used2 = new_key_used;
                    is = is2;
                    chat = chat2;
                    r5 = 1;
                    try {
                        if (!decryptWithMtProtoVersion(is2, keyToDecrypt, messageKey, 1, incoming, false)) {
                            return null;
                        }
                        decryptedWithVersion = 1;
                        r2 = 0;
                    } catch (Exception e5) {
                        e = e5;
                        arrayList2 = 0;
                        FileLog.e(e);
                        arrayList = arrayList2;
                        return arrayList;
                    }
                }
                NativeByteBuffer is3 = is;
                TLObject object2 = TLClassStore.Instance().TLdeserialize(is3, is3.readInt32(false), false);
                is3.reuse();
                if (!new_key_used2) {
                    chat.key_use_count_in = (short) (chat.key_use_count_in + r5);
                }
                if (!(object2 instanceof TLRPC.TL_decryptedMessageLayer)) {
                    if ((object2 instanceof TLRPC.TL_decryptedMessageService) && (((TLRPC.TL_decryptedMessageService) object2).action instanceof TLRPC.TL_decryptedMessageActionNotifyLayer)) {
                        object = object2;
                    }
                    return r2;
                }
                TLRPC.TL_decryptedMessageLayer layer = (TLRPC.TL_decryptedMessageLayer) object2;
                if (chat.seq_in == 0 && chat.seq_out == 0) {
                    if (chat.admin_id == getUserConfig().getClientUserId()) {
                        int i = r5 == true ? 1 : 0;
                        int i2 = r5 == true ? 1 : 0;
                        int i3 = r5 == true ? 1 : 0;
                        chat.seq_out = i;
                        chat.seq_in = -2;
                    } else {
                        chat.seq_in = -1;
                    }
                }
                if (layer.random_bytes.length < 15) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("got random bytes less than needed");
                    }
                    return r2;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current chat in_seq = " + chat.seq_in + " out_seq = " + chat.seq_out);
                    FileLog.d("got message with in_seq = " + layer.in_seq_no + " out_seq = " + layer.out_seq_no);
                }
                if (layer.out_seq_no <= chat.seq_in) {
                    return r2;
                }
                if (decryptedWithVersion == r5 && chat.mtproto_seq != 0 && layer.out_seq_no >= chat.mtproto_seq) {
                    return r2;
                }
                if (chat.seq_in != layer.out_seq_no - 2) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("got hole");
                    }
                    sendResendMessage(chat, chat.seq_in + 2, layer.out_seq_no - 2, r2);
                    ArrayList<TL_decryptedMessageHolder> arr = this.secretHolesQueue.get(chat.id);
                    if (arr == null) {
                        arr = new ArrayList<>();
                        this.secretHolesQueue.put(chat.id, arr);
                    }
                    if (arr.size() < 4) {
                        TL_decryptedMessageHolder holder = new TL_decryptedMessageHolder();
                        holder.layer = layer;
                        holder.file = message.file;
                        holder.date = message.date;
                        holder.new_key_used = new_key_used2;
                        holder.decryptedWithVersion = decryptedWithVersion;
                        arr.add(holder);
                        return r2;
                    }
                    this.secretHolesQueue.remove(chat.id);
                    final TLRPC.TL_encryptedChatDiscarded newChat = new TLRPC.TL_encryptedChatDiscarded();
                    newChat.id = chat.id;
                    newChat.user_id = chat.user_id;
                    newChat.auth_key = chat.auth_key;
                    newChat.key_create_date = chat.key_create_date;
                    newChat.key_use_count_in = chat.key_use_count_in;
                    newChat.key_use_count_out = chat.key_use_count_out;
                    newChat.seq_in = chat.seq_in;
                    newChat.seq_out = chat.seq_out;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            SecretChatHelper.this.m1132lambda$decryptMessage$17$orgtelegrammessengerSecretChatHelper(newChat);
                        }
                    });
                    declineSecretChat(chat.id, false);
                    return r2;
                }
                if (decryptedWithVersion == 2) {
                    chat.mtproto_seq = Math.min(chat.mtproto_seq, chat.seq_in);
                }
                applyPeerLayer(chat, layer.layer);
                chat.seq_in = layer.out_seq_no;
                chat.in_seq_no = layer.in_seq_no;
                getMessagesStorage().updateEncryptedChatSeq(chat, r5);
                object = layer.message;
                ArrayList<TLRPC.Message> messages = new ArrayList<>();
                TLRPC.Message decryptedMessage = processDecryptedObject(chat, message.file, message.date, object, new_key_used2);
                if (decryptedMessage != null) {
                    messages.add(decryptedMessage);
                }
                checkSecretHoles(chat, messages);
                return messages;
            } catch (Exception e6) {
                e = e6;
                arrayList2 = 0;
            }
        }
        return null;
    }

    /* renamed from: lambda$decryptMessage$17$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1132lambda$decryptMessage$17$orgtelegrammessengerSecretChatHelper(TLRPC.TL_encryptedChatDiscarded newChat) {
        getMessagesController().putEncryptedChat(newChat, false);
        getMessagesStorage().updateEncryptedChat(newChat);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void requestNewSecretChatKey(TLRPC.EncryptedChat encryptedChat) {
        byte[] salt = new byte[256];
        Utilities.random.nextBytes(salt);
        BigInteger i_g_a = BigInteger.valueOf(getMessagesStorage().getSecretG());
        byte[] g_a = i_g_a.modPow(new BigInteger(1, salt), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
        if (g_a.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(g_a, 1, correctedAuth, 0, 256);
            g_a = correctedAuth;
        }
        encryptedChat.exchange_id = getSendMessagesHelper().getNextRandomId();
        encryptedChat.a_or_b = salt;
        encryptedChat.g_a = g_a;
        getMessagesStorage().updateEncryptedChat(encryptedChat);
        sendRequestKeyMessage(encryptedChat, null);
    }

    public void processAcceptedSecretChat(final TLRPC.EncryptedChat encryptedChat) {
        BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
        BigInteger i_authKey = new BigInteger(1, encryptedChat.g_a_or_b);
        if (!Utilities.isGoodGaAndGb(i_authKey, p)) {
            declineSecretChat(encryptedChat.id, false);
            return;
        }
        byte[] authKey = i_authKey.modPow(new BigInteger(1, encryptedChat.a_or_b), p).toByteArray();
        if (authKey.length > 256) {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(authKey, authKey.length - 256, correctedAuth, 0, 256);
            authKey = correctedAuth;
        } else if (authKey.length < 256) {
            byte[] correctedAuth2 = new byte[256];
            System.arraycopy(authKey, 0, correctedAuth2, 256 - authKey.length, authKey.length);
            for (int a = 0; a < 256 - authKey.length; a++) {
                correctedAuth2[a] = 0;
            }
            authKey = correctedAuth2;
        }
        byte[] authKeyHash = Utilities.computeSHA1(authKey);
        byte[] authKeyId = new byte[8];
        System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
        long fingerprint = Utilities.bytesToLong(authKeyId);
        if (encryptedChat.key_fingerprint == fingerprint) {
            encryptedChat.auth_key = authKey;
            encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
            encryptedChat.seq_in = -2;
            encryptedChat.seq_out = 1;
            getMessagesStorage().updateEncryptedChat(encryptedChat);
            getMessagesController().putEncryptedChat(encryptedChat, false);
            ArrayList<TLRPC.Update> pendingUpdates = this.pendingSecretMessages.get(encryptedChat.id);
            if (pendingUpdates != null) {
                getMessagesController().processUpdateArray(pendingUpdates, null, null, false, 0);
                this.pendingSecretMessages.remove(encryptedChat.id);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1138x790a9119(encryptedChat);
                }
            });
            return;
        }
        final TLRPC.TL_encryptedChatDiscarded newChat = new TLRPC.TL_encryptedChatDiscarded();
        newChat.id = encryptedChat.id;
        newChat.user_id = encryptedChat.user_id;
        newChat.auth_key = encryptedChat.auth_key;
        newChat.key_create_date = encryptedChat.key_create_date;
        newChat.key_use_count_in = encryptedChat.key_use_count_in;
        newChat.key_use_count_out = encryptedChat.key_use_count_out;
        newChat.seq_in = encryptedChat.seq_in;
        newChat.seq_out = encryptedChat.seq_out;
        newChat.admin_id = encryptedChat.admin_id;
        newChat.mtproto_seq = encryptedChat.mtproto_seq;
        getMessagesStorage().updateEncryptedChat(newChat);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1139x5aabc1a(newChat);
            }
        });
        declineSecretChat(encryptedChat.id, false);
    }

    /* renamed from: lambda$processAcceptedSecretChat$18$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1138x790a9119(TLRPC.EncryptedChat encryptedChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, encryptedChat);
        sendNotifyLayerMessage(encryptedChat, null);
    }

    /* renamed from: lambda$processAcceptedSecretChat$19$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1139x5aabc1a(TLRPC.TL_encryptedChatDiscarded newChat) {
        getMessagesController().putEncryptedChat(newChat, false);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
    }

    public void declineSecretChat(int chat_id, boolean revoke) {
        declineSecretChat(chat_id, revoke, 0L);
    }

    public void declineSecretChat(int chat_id, boolean revoke, long taskId) {
        final long newTaskId;
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(12);
                data.writeInt32(100);
                data.writeInt32(chat_id);
                data.writeBool(revoke);
            } catch (Exception e) {
                FileLog.e(e);
            }
            newTaskId = getMessagesStorage().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        TLRPC.TL_messages_discardEncryption req = new TLRPC.TL_messages_discardEncryption();
        req.chat_id = chat_id;
        req.delete_history = revoke;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda18
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SecretChatHelper.this.m1131xbee5b370(newTaskId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$declineSecretChat$20$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1131xbee5b370(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void acceptSecretChat(final TLRPC.EncryptedChat encryptedChat) {
        if (this.acceptingChats.get(encryptedChat.id) != null) {
            return;
        }
        this.acceptingChats.put(encryptedChat.id, encryptedChat);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = getMessagesStorage().getLastSecretVersion();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda24
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SecretChatHelper.this.m1129xb735687(encryptedChat, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$acceptSecretChat$23$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1129xb735687(final TLRPC.EncryptedChat encryptedChat, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
            if (response instanceof TLRPC.TL_messages_dhConfig) {
                if (!Utilities.isGoodPrime(res.p, res.g)) {
                    this.acceptingChats.remove(encryptedChat.id);
                    declineSecretChat(encryptedChat.id, false);
                    return;
                }
                getMessagesStorage().setSecretPBytes(res.p);
                getMessagesStorage().setSecretG(res.g);
                getMessagesStorage().setLastSecretVersion(res.version);
                getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
            }
            byte[] salt = new byte[256];
            for (int a = 0; a < 256; a++) {
                salt[a] = (byte) (((byte) (Utilities.random.nextDouble() * 256.0d)) ^ res.random[a]);
            }
            encryptedChat.a_or_b = salt;
            encryptedChat.seq_in = -1;
            encryptedChat.seq_out = 0;
            BigInteger p = new BigInteger(1, getMessagesStorage().getSecretPBytes());
            BigInteger g_b = BigInteger.valueOf(getMessagesStorage().getSecretG());
            BigInteger g_b2 = g_b.modPow(new BigInteger(1, salt), p);
            BigInteger g_a = new BigInteger(1, encryptedChat.g_a);
            if (!Utilities.isGoodGaAndGb(g_a, p)) {
                this.acceptingChats.remove(encryptedChat.id);
                declineSecretChat(encryptedChat.id, false);
                return;
            }
            byte[] g_b_bytes = g_b2.toByteArray();
            if (g_b_bytes.length > 256) {
                byte[] correctedAuth = new byte[256];
                System.arraycopy(g_b_bytes, 1, correctedAuth, 0, 256);
                g_b_bytes = correctedAuth;
            }
            byte[] authKey = g_a.modPow(new BigInteger(1, salt), p).toByteArray();
            if (authKey.length <= 256) {
                if (authKey.length < 256) {
                    byte[] correctedAuth2 = new byte[256];
                    System.arraycopy(authKey, 0, correctedAuth2, 256 - authKey.length, authKey.length);
                    for (int a2 = 0; a2 < 256 - authKey.length; a2++) {
                        correctedAuth2[a2] = 0;
                    }
                    authKey = correctedAuth2;
                }
            } else {
                byte[] correctedAuth3 = new byte[256];
                System.arraycopy(authKey, authKey.length - 256, correctedAuth3, 0, 256);
                authKey = correctedAuth3;
            }
            byte[] authKeyHash = Utilities.computeSHA1(authKey);
            byte[] authKeyId = new byte[8];
            System.arraycopy(authKeyHash, authKeyHash.length - 8, authKeyId, 0, 8);
            encryptedChat.auth_key = authKey;
            encryptedChat.key_create_date = getConnectionsManager().getCurrentTime();
            TLRPC.TL_messages_acceptEncryption req2 = new TLRPC.TL_messages_acceptEncryption();
            req2.g_b = g_b_bytes;
            req2.peer = new TLRPC.TL_inputEncryptedChat();
            req2.peer.chat_id = encryptedChat.id;
            req2.peer.access_hash = encryptedChat.access_hash;
            req2.key_fingerprint = Utilities.bytesToLong(authKeyId);
            getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda23
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SecretChatHelper.this.m1128x7ed32b86(encryptedChat, tLObject, tL_error);
                }
            }, 64);
            return;
        }
        this.acceptingChats.remove(encryptedChat.id);
    }

    /* renamed from: lambda$acceptSecretChat$22$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1128x7ed32b86(TLRPC.EncryptedChat encryptedChat, TLObject response1, TLRPC.TL_error error1) {
        this.acceptingChats.remove(encryptedChat.id);
        if (error1 == null) {
            final TLRPC.EncryptedChat newChat = (TLRPC.EncryptedChat) response1;
            newChat.auth_key = encryptedChat.auth_key;
            newChat.user_id = encryptedChat.user_id;
            newChat.seq_in = encryptedChat.seq_in;
            newChat.seq_out = encryptedChat.seq_out;
            newChat.key_create_date = encryptedChat.key_create_date;
            newChat.key_use_count_in = encryptedChat.key_use_count_in;
            newChat.key_use_count_out = encryptedChat.key_use_count_out;
            getMessagesStorage().updateEncryptedChat(newChat);
            getMessagesController().putEncryptedChat(newChat, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1127xf2330085(newChat);
                }
            });
        }
    }

    /* renamed from: lambda$acceptSecretChat$21$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1127xf2330085(TLRPC.EncryptedChat newChat) {
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatUpdated, newChat);
        sendNotifyLayerMessage(newChat, null);
    }

    public void startSecretChat(final Context context, final TLRPC.User user) {
        if (user == null || context == null) {
            return;
        }
        this.startingSecretChat = true;
        final AlertDialog progressDialog = new AlertDialog(context, 3);
        TLRPC.TL_messages_getDhConfig req = new TLRPC.TL_messages_getDhConfig();
        req.random_length = 256;
        req.version = getMessagesStorage().getLastSecretVersion();
        final int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda19
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SecretChatHelper.this.m1154x98b25de3(context, progressDialog, user, tLObject, tL_error);
            }
        }, 2);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                SecretChatHelper.this.m1155x255288e4(reqId, dialogInterface);
            }
        });
        try {
            progressDialog.show();
        } catch (Exception e) {
        }
    }

    /* renamed from: lambda$startSecretChat$30$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1154x98b25de3(final Context context, final AlertDialog progressDialog, final TLRPC.User user, TLObject response, TLRPC.TL_error error) {
        byte[] g_a;
        if (error != null) {
            this.delayedEncryptedChatUpdates.clear();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1153x82eeabcd(context, progressDialog);
                }
            });
            return;
        }
        TLRPC.messages_DhConfig res = (TLRPC.messages_DhConfig) response;
        if (response instanceof TLRPC.TL_messages_dhConfig) {
            if (!Utilities.isGoodPrime(res.p, res.g)) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda11
                    @Override // java.lang.Runnable
                    public final void run() {
                        SecretChatHelper.lambda$startSecretChat$24(context, progressDialog);
                    }
                });
                return;
            }
            getMessagesStorage().setSecretPBytes(res.p);
            getMessagesStorage().setSecretG(res.g);
            getMessagesStorage().setLastSecretVersion(res.version);
            getMessagesStorage().saveSecretParams(getMessagesStorage().getLastSecretVersion(), getMessagesStorage().getSecretG(), getMessagesStorage().getSecretPBytes());
        }
        final byte[] salt = new byte[256];
        for (int a = 0; a < 256; a++) {
            salt[a] = (byte) (((byte) (Utilities.random.nextDouble() * 256.0d)) ^ res.random[a]);
        }
        BigInteger i_g_a = BigInteger.valueOf(getMessagesStorage().getSecretG());
        byte[] g_a2 = i_g_a.modPow(new BigInteger(1, salt), new BigInteger(1, getMessagesStorage().getSecretPBytes())).toByteArray();
        if (g_a2.length <= 256) {
            g_a = g_a2;
        } else {
            byte[] correctedAuth = new byte[256];
            System.arraycopy(g_a2, 1, correctedAuth, 0, 256);
            g_a = correctedAuth;
        }
        TLRPC.TL_messages_requestEncryption req2 = new TLRPC.TL_messages_requestEncryption();
        req2.g_a = g_a;
        req2.user_id = getMessagesController().getInputUser(user);
        req2.random_id = Utilities.random.nextInt();
        getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda20
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SecretChatHelper.this.m1152xf64e80cc(context, progressDialog, salt, user, tLObject, tL_error);
            }
        }, 2);
    }

    public static /* synthetic */ void lambda$startSecretChat$24(Context context, AlertDialog progressDialog) {
        try {
            if (!((Activity) context).isFinishing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$startSecretChat$28$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1152xf64e80cc(final Context context, final AlertDialog progressDialog, final byte[] salt, final TLRPC.User user, final TLObject response1, TLRPC.TL_error error1) {
        if (error1 == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SecretChatHelper.this.m1150xdd0e2aca(context, progressDialog, response1, salt, user);
                }
            });
            return;
        }
        this.delayedEncryptedChatUpdates.clear();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1151x69ae55cb(context, progressDialog);
            }
        });
    }

    /* renamed from: lambda$startSecretChat$26$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1150xdd0e2aca(Context context, AlertDialog progressDialog, TLObject response1, byte[] salt, TLRPC.User user) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        TLRPC.EncryptedChat chat = (TLRPC.EncryptedChat) response1;
        chat.user_id = chat.participant_id;
        chat.seq_in = -2;
        chat.seq_out = 1;
        chat.a_or_b = salt;
        getMessagesController().putEncryptedChat(chat, false);
        TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        dialog.id = DialogObject.makeEncryptedDialogId(chat.id);
        dialog.unread_count = 0;
        dialog.top_message = 0;
        dialog.last_message_date = getConnectionsManager().getCurrentTime();
        getMessagesController().dialogs_dict.put(dialog.id, dialog);
        getMessagesController().allDialogs.add(dialog);
        getMessagesController().sortDialogs(null);
        getMessagesStorage().putEncryptedChat(chat, user, dialog);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().postNotificationName(NotificationCenter.encryptedChatCreated, chat);
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SecretChatHelper$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                SecretChatHelper.this.m1149x506dffc9();
            }
        });
    }

    /* renamed from: lambda$startSecretChat$25$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1149x506dffc9() {
        if (!this.delayedEncryptedChatUpdates.isEmpty()) {
            getMessagesController().processUpdateArray(this.delayedEncryptedChatUpdates, null, null, false, 0);
            this.delayedEncryptedChatUpdates.clear();
        }
    }

    /* renamed from: lambda$startSecretChat$27$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1151x69ae55cb(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            this.startingSecretChat = false;
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("AppName", org.telegram.messenger.beta.R.string.AppName));
            builder.setMessage(LocaleController.getString("CreateEncryptedChatError", org.telegram.messenger.beta.R.string.CreateEncryptedChatError));
            builder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    /* renamed from: lambda$startSecretChat$29$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1153x82eeabcd(Context context, AlertDialog progressDialog) {
        this.startingSecretChat = false;
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* renamed from: lambda$startSecretChat$31$org-telegram-messenger-SecretChatHelper */
    public /* synthetic */ void m1155x255288e4(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }
}
