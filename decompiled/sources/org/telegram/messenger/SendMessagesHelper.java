package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.huawei.hms.actions.SearchIntents;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineMessage;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$DecryptedMessage;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$DecryptedMessageMedia;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_botInlineMediaResult;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageText;
import org.telegram.tgnet.TLRPC$TL_decryptedMessage;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionTyping;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_document_layer82;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_fileLocation_layer82;
import org.telegram.tgnet.TLRPC$TL_game;
import org.telegram.tgnet.TLRPC$TL_geoPoint;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC$TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerSelf;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetItem;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_inputUserSelf;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messageReplies;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_historyImport;
import org.telegram.tgnet.TLRPC$TL_messages_initHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth;
import org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendReaction;
import org.telegram.tgnet.TLRPC$TL_messages_sendScreenshotNotification;
import org.telegram.tgnet.TLRPC$TL_messages_sendVote;
import org.telegram.tgnet.TLRPC$TL_messages_startHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_uploadImportedMedia;
import org.telegram.tgnet.TLRPC$TL_messages_uploadMedia;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photoPathSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.tgnet.TLRPC$TL_photoSize_layer127;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_restrictionReason;
import org.telegram.tgnet.TLRPC$TL_stickers_createStickerSet;
import org.telegram.tgnet.TLRPC$TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateEditMessage;
import org.telegram.tgnet.TLRPC$TL_updateMessageID;
import org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage;
import org.telegram.tgnet.TLRPC$TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_webDocument;
import org.telegram.tgnet.TLRPC$TL_webPagePending;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.Point;
import org.telegram.ui.TwoStepVerificationActivity;
import org.telegram.ui.TwoStepVerificationSetupActivity;
import org.webrtc.MediaStreamTrack;
/* loaded from: classes.dex */
public class SendMessagesHelper extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static final int ERROR_TYPE_FILE_TOO_LARGE = 2;
    private static final int ERROR_TYPE_UNSUPPORTED = 1;
    private static volatile SendMessagesHelper[] Instance;
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap<>();
    private SparseArray<MessageObject> unsentMessages = new SparseArray<>();
    private SparseArray<TLRPC$Message> sendingMessages = new SparseArray<>();
    private SparseArray<TLRPC$Message> editingMessages = new SparseArray<>();
    private SparseArray<TLRPC$Message> uploadMessages = new SparseArray<>();
    private LongSparseArray<Integer> sendingMessagesIdDialogs = new LongSparseArray<>();
    private LongSparseArray<Integer> uploadingMessagesIdDialogs = new LongSparseArray<>();
    private HashMap<String, MessageObject> waitingForLocation = new HashMap<>();
    private HashMap<String, Boolean> waitingForCallback = new HashMap<>();
    private HashMap<String, byte[]> waitingForVote = new HashMap<>();
    private LongSparseArray<Long> voteSendTime = new LongSparseArray<>();
    private HashMap<String, ImportingHistory> importingHistoryFiles = new HashMap<>();
    private LongSparseArray<ImportingHistory> importingHistoryMap = new LongSparseArray<>();
    private HashMap<String, ImportingStickers> importingStickersFiles = new HashMap<>();
    private HashMap<String, ImportingStickers> importingStickersMap = new HashMap<>();
    private LocationProvider locationProvider = new LocationProvider(new LocationProvider.LocationProviderDelegate() { // from class: org.telegram.messenger.SendMessagesHelper.1
        @Override // org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate
        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        @Override // org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate
        public void onUnableLocationAcquire() {
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, new HashMap(SendMessagesHelper.this.waitingForLocation));
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    });

    /* loaded from: classes.dex */
    public static class SendingMediaInfo {
        public boolean canDeleteAfter;
        public String caption;
        public ArrayList<TLRPC$MessageEntity> entities;
        public boolean forceImage;
        public TLRPC$BotInlineResult inlineResult;
        public boolean isVideo;
        public ArrayList<TLRPC$InputDocument> masks;
        public String paintPath;
        public HashMap<String, String> params;
        public String path;
        public MediaController.SearchImage searchImage;
        public String thumbPath;
        public int ttl;
        public Uri uri;
        public VideoEditedInfo videoEditedInfo;
    }

    /* loaded from: classes.dex */
    public class ImportingHistory {
        public long dialogId;
        public double estimatedUploadSpeed;
        public String historyPath;
        public long importId;
        private long lastUploadSize;
        private long lastUploadTime;
        public TLRPC$InputPeer peer;
        public long totalSize;
        public int uploadProgress;
        public long uploadedSize;
        public ArrayList<Uri> mediaPaths = new ArrayList<>();
        public HashSet<String> uploadSet = new HashSet<>();
        public HashMap<String, Float> uploadProgresses = new HashMap<>();
        public HashMap<String, Long> uploadSize = new HashMap<>();
        public ArrayList<String> uploadMedia = new ArrayList<>();
        public int timeUntilFinish = Integer.MAX_VALUE;

        public ImportingHistory() {
            SendMessagesHelper.this = r1;
        }

        public void initImport(TLRPC$InputFile tLRPC$InputFile) {
            TLRPC$TL_messages_initHistoryImport tLRPC$TL_messages_initHistoryImport = new TLRPC$TL_messages_initHistoryImport();
            tLRPC$TL_messages_initHistoryImport.file = tLRPC$InputFile;
            tLRPC$TL_messages_initHistoryImport.media_count = this.mediaPaths.size();
            tLRPC$TL_messages_initHistoryImport.peer = this.peer;
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_initHistoryImport, new AnonymousClass1(tLRPC$TL_messages_initHistoryImport), 2);
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingHistory$1 */
        /* loaded from: classes.dex */
        public class AnonymousClass1 implements RequestDelegate {
            final /* synthetic */ TLRPC$TL_messages_initHistoryImport val$req;

            AnonymousClass1(TLRPC$TL_messages_initHistoryImport tLRPC$TL_messages_initHistoryImport) {
                ImportingHistory.this = r1;
                this.val$req = tLRPC$TL_messages_initHistoryImport;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                final TLRPC$TL_messages_initHistoryImport tLRPC$TL_messages_initHistoryImport = this.val$req;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingHistory$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingHistory.AnonymousClass1.this.lambda$run$0(tLObject, tLRPC$TL_messages_initHistoryImport, tLRPC$TL_error);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0(TLObject tLObject, TLRPC$TL_messages_initHistoryImport tLRPC$TL_messages_initHistoryImport, TLRPC$TL_error tLRPC$TL_error) {
                if (!(tLObject instanceof TLRPC$TL_messages_historyImport)) {
                    SendMessagesHelper.this.importingHistoryMap.remove(ImportingHistory.this.dialogId);
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId), tLRPC$TL_messages_initHistoryImport, tLRPC$TL_error);
                    return;
                }
                ImportingHistory importingHistory = ImportingHistory.this;
                importingHistory.importId = ((TLRPC$TL_messages_historyImport) tLObject).id;
                importingHistory.uploadSet.remove(importingHistory.historyPath);
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                if (ImportingHistory.this.uploadSet.isEmpty()) {
                    ImportingHistory.this.startImport();
                }
                ImportingHistory.this.lastUploadTime = SystemClock.elapsedRealtime();
                int size = ImportingHistory.this.uploadMedia.size();
                for (int i = 0; i < size; i++) {
                    SendMessagesHelper.this.getFileLoader().uploadFile(ImportingHistory.this.uploadMedia.get(i), false, true, ConnectionsManager.FileTypeFile);
                }
            }
        }

        public long getUploadedCount() {
            return this.uploadedSize;
        }

        public long getTotalCount() {
            return this.totalSize;
        }

        public void onFileFailedToUpload(String str) {
            if (str.equals(this.historyPath)) {
                SendMessagesHelper.this.importingHistoryMap.remove(this.dialogId);
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.code = 400;
                tLRPC$TL_error.text = "IMPORT_UPLOAD_FAILED";
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId), new TLRPC$TL_messages_initHistoryImport(), tLRPC$TL_error);
                return;
            }
            this.uploadSet.remove(str);
        }

        public void addUploadProgress(String str, long j, float f) {
            this.uploadProgresses.put(str, Float.valueOf(f));
            this.uploadSize.put(str, Long.valueOf(j));
            this.uploadedSize = 0L;
            for (Map.Entry<String, Long> entry : this.uploadSize.entrySet()) {
                this.uploadedSize += entry.getValue().longValue();
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (!str.equals(this.historyPath)) {
                long j2 = this.uploadedSize;
                long j3 = this.lastUploadSize;
                if (j2 != j3) {
                    long j4 = this.lastUploadTime;
                    if (elapsedRealtime != j4) {
                        double d = elapsedRealtime - j4;
                        Double.isNaN(d);
                        double d2 = j2 - j3;
                        Double.isNaN(d2);
                        double d3 = d2 / (d / 1000.0d);
                        double d4 = this.estimatedUploadSpeed;
                        if (d4 == 0.0d) {
                            this.estimatedUploadSpeed = d3;
                        } else {
                            this.estimatedUploadSpeed = (d3 * 0.01d) + (0.99d * d4);
                        }
                        double d5 = (this.totalSize - j2) * 1000;
                        double d6 = this.estimatedUploadSpeed;
                        Double.isNaN(d5);
                        this.timeUntilFinish = (int) (d5 / d6);
                        this.lastUploadSize = j2;
                        this.lastUploadTime = elapsedRealtime;
                    }
                }
            }
            int uploadedCount = (int) ((((float) getUploadedCount()) / ((float) getTotalCount())) * 100.0f);
            if (this.uploadProgress != uploadedCount) {
                this.uploadProgress = uploadedCount;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId));
            }
        }

        public void onMediaImport(String str, long j, TLRPC$InputFile tLRPC$InputFile) {
            addUploadProgress(str, j, 1.0f);
            TLRPC$TL_messages_uploadImportedMedia tLRPC$TL_messages_uploadImportedMedia = new TLRPC$TL_messages_uploadImportedMedia();
            tLRPC$TL_messages_uploadImportedMedia.peer = this.peer;
            tLRPC$TL_messages_uploadImportedMedia.import_id = this.importId;
            tLRPC$TL_messages_uploadImportedMedia.file_name = new File(str).getName();
            MimeTypeMap singleton = MimeTypeMap.getSingleton();
            int lastIndexOf = tLRPC$TL_messages_uploadImportedMedia.file_name.lastIndexOf(46);
            String lowerCase = lastIndexOf != -1 ? tLRPC$TL_messages_uploadImportedMedia.file_name.substring(lastIndexOf + 1).toLowerCase() : "txt";
            String mimeTypeFromExtension = singleton.getMimeTypeFromExtension(lowerCase);
            if (mimeTypeFromExtension == null) {
                if ("opus".equals(lowerCase)) {
                    mimeTypeFromExtension = "audio/opus";
                } else {
                    mimeTypeFromExtension = "webp".equals(lowerCase) ? "image/webp" : "text/plain";
                }
            }
            if (mimeTypeFromExtension.equals("image/jpg") || mimeTypeFromExtension.equals("image/jpeg")) {
                TLRPC$TL_inputMediaUploadedPhoto tLRPC$TL_inputMediaUploadedPhoto = new TLRPC$TL_inputMediaUploadedPhoto();
                tLRPC$TL_inputMediaUploadedPhoto.file = tLRPC$InputFile;
                tLRPC$TL_messages_uploadImportedMedia.media = tLRPC$TL_inputMediaUploadedPhoto;
            } else {
                TLRPC$TL_inputMediaUploadedDocument tLRPC$TL_inputMediaUploadedDocument = new TLRPC$TL_inputMediaUploadedDocument();
                tLRPC$TL_inputMediaUploadedDocument.file = tLRPC$InputFile;
                tLRPC$TL_inputMediaUploadedDocument.mime_type = mimeTypeFromExtension;
                tLRPC$TL_messages_uploadImportedMedia.media = tLRPC$TL_inputMediaUploadedDocument;
            }
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_uploadImportedMedia, new AnonymousClass2(str), 2);
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingHistory$2 */
        /* loaded from: classes.dex */
        public class AnonymousClass2 implements RequestDelegate {
            final /* synthetic */ String val$path;

            AnonymousClass2(String str) {
                ImportingHistory.this = r1;
                this.val$path = str;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                final String str = this.val$path;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingHistory$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingHistory.AnonymousClass2.this.lambda$run$0(str);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0(String str) {
                ImportingHistory.this.uploadSet.remove(str);
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                if (ImportingHistory.this.uploadSet.isEmpty()) {
                    ImportingHistory.this.startImport();
                }
            }
        }

        public void startImport() {
            TLRPC$TL_messages_startHistoryImport tLRPC$TL_messages_startHistoryImport = new TLRPC$TL_messages_startHistoryImport();
            tLRPC$TL_messages_startHistoryImport.peer = this.peer;
            tLRPC$TL_messages_startHistoryImport.import_id = this.importId;
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_startHistoryImport, new AnonymousClass3(tLRPC$TL_messages_startHistoryImport));
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingHistory$3 */
        /* loaded from: classes.dex */
        public class AnonymousClass3 implements RequestDelegate {
            final /* synthetic */ TLRPC$TL_messages_startHistoryImport val$req;

            AnonymousClass3(TLRPC$TL_messages_startHistoryImport tLRPC$TL_messages_startHistoryImport) {
                ImportingHistory.this = r1;
                this.val$req = tLRPC$TL_messages_startHistoryImport;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                final TLRPC$TL_messages_startHistoryImport tLRPC$TL_messages_startHistoryImport = this.val$req;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingHistory$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingHistory.AnonymousClass3.this.lambda$run$0(tLRPC$TL_error, tLRPC$TL_messages_startHistoryImport);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_startHistoryImport tLRPC$TL_messages_startHistoryImport) {
                SendMessagesHelper.this.importingHistoryMap.remove(ImportingHistory.this.dialogId);
                if (tLRPC$TL_error == null) {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                } else {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId), tLRPC$TL_messages_startHistoryImport, tLRPC$TL_error);
                }
            }
        }

        public void setImportProgress(int i) {
            if (i == 100) {
                SendMessagesHelper.this.importingHistoryMap.remove(this.dialogId);
            }
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId));
        }
    }

    /* loaded from: classes.dex */
    public static class ImportingSticker {
        public boolean animated;
        public String emoji;
        public TLRPC$TL_inputStickerSetItem item;
        public String mimeType;
        public String path;
        public boolean validated;

        public void uploadMedia(int i, TLRPC$InputFile tLRPC$InputFile, Runnable runnable) {
            TLRPC$TL_messages_uploadMedia tLRPC$TL_messages_uploadMedia = new TLRPC$TL_messages_uploadMedia();
            tLRPC$TL_messages_uploadMedia.peer = new TLRPC$TL_inputPeerSelf();
            TLRPC$TL_inputMediaUploadedDocument tLRPC$TL_inputMediaUploadedDocument = new TLRPC$TL_inputMediaUploadedDocument();
            tLRPC$TL_messages_uploadMedia.media = tLRPC$TL_inputMediaUploadedDocument;
            tLRPC$TL_inputMediaUploadedDocument.file = tLRPC$InputFile;
            tLRPC$TL_inputMediaUploadedDocument.mime_type = this.mimeType;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_uploadMedia, new AnonymousClass1(runnable), 2);
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingSticker$1 */
        /* loaded from: classes.dex */
        public class AnonymousClass1 implements RequestDelegate {
            final /* synthetic */ Runnable val$onFinish;

            AnonymousClass1(Runnable runnable) {
                ImportingSticker.this = r1;
                this.val$onFinish = runnable;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                final Runnable runnable = this.val$onFinish;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingSticker$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingSticker.AnonymousClass1.this.lambda$run$0(tLObject, runnable);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0(TLObject tLObject, Runnable runnable) {
                if (tLObject instanceof TLRPC$TL_messageMediaDocument) {
                    ImportingSticker.this.item = new TLRPC$TL_inputStickerSetItem();
                    ImportingSticker.this.item.document = new TLRPC$TL_inputDocument();
                    ImportingSticker importingSticker = ImportingSticker.this;
                    TLRPC$TL_inputStickerSetItem tLRPC$TL_inputStickerSetItem = importingSticker.item;
                    TLRPC$InputDocument tLRPC$InputDocument = tLRPC$TL_inputStickerSetItem.document;
                    TLRPC$Document tLRPC$Document = ((TLRPC$TL_messageMediaDocument) tLObject).document;
                    tLRPC$InputDocument.id = tLRPC$Document.id;
                    tLRPC$InputDocument.access_hash = tLRPC$Document.access_hash;
                    tLRPC$InputDocument.file_reference = tLRPC$Document.file_reference;
                    String str = importingSticker.emoji;
                    if (str == null) {
                        str = "";
                    }
                    tLRPC$TL_inputStickerSetItem.emoji = str;
                    importingSticker.mimeType = tLRPC$Document.mime_type;
                } else {
                    ImportingSticker importingSticker2 = ImportingSticker.this;
                    if (importingSticker2.animated) {
                        importingSticker2.mimeType = "application/x-bad-tgsticker";
                    }
                }
                runnable.run();
            }
        }
    }

    /* loaded from: classes.dex */
    public class ImportingStickers {
        public double estimatedUploadSpeed;
        private long lastUploadSize;
        private long lastUploadTime;
        public String shortName;
        public String software;
        public String title;
        public long totalSize;
        public int uploadProgress;
        public long uploadedSize;
        public HashMap<String, ImportingSticker> uploadSet = new HashMap<>();
        public HashMap<String, Float> uploadProgresses = new HashMap<>();
        public HashMap<String, Long> uploadSize = new HashMap<>();
        public ArrayList<ImportingSticker> uploadMedia = new ArrayList<>();
        public int timeUntilFinish = Integer.MAX_VALUE;

        public ImportingStickers() {
            SendMessagesHelper.this = r1;
        }

        public void initImport() {
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            this.lastUploadTime = SystemClock.elapsedRealtime();
            int size = this.uploadMedia.size();
            for (int i = 0; i < size; i++) {
                SendMessagesHelper.this.getFileLoader().uploadFile(this.uploadMedia.get(i).path, false, true, ConnectionsManager.FileTypeFile);
            }
        }

        public long getUploadedCount() {
            return this.uploadedSize;
        }

        public long getTotalCount() {
            return this.totalSize;
        }

        public void onFileFailedToUpload(String str) {
            ImportingSticker remove = this.uploadSet.remove(str);
            if (remove != null) {
                this.uploadMedia.remove(remove);
            }
        }

        public void addUploadProgress(String str, long j, float f) {
            this.uploadProgresses.put(str, Float.valueOf(f));
            this.uploadSize.put(str, Long.valueOf(j));
            this.uploadedSize = 0L;
            for (Map.Entry<String, Long> entry : this.uploadSize.entrySet()) {
                this.uploadedSize += entry.getValue().longValue();
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = this.uploadedSize;
            long j3 = this.lastUploadSize;
            if (j2 != j3) {
                long j4 = this.lastUploadTime;
                if (elapsedRealtime != j4) {
                    double d = elapsedRealtime - j4;
                    Double.isNaN(d);
                    double d2 = j2 - j3;
                    Double.isNaN(d2);
                    double d3 = d2 / (d / 1000.0d);
                    double d4 = this.estimatedUploadSpeed;
                    if (d4 == 0.0d) {
                        this.estimatedUploadSpeed = d3;
                    } else {
                        this.estimatedUploadSpeed = (d3 * 0.01d) + (0.99d * d4);
                    }
                    double d5 = (this.totalSize - j2) * 1000;
                    double d6 = this.estimatedUploadSpeed;
                    Double.isNaN(d5);
                    this.timeUntilFinish = (int) (d5 / d6);
                    this.lastUploadSize = j2;
                    this.lastUploadTime = elapsedRealtime;
                }
            }
            int uploadedCount = (int) ((((float) getUploadedCount()) / ((float) getTotalCount())) * 100.0f);
            if (this.uploadProgress != uploadedCount) {
                this.uploadProgress = uploadedCount;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            }
        }

        public void onMediaImport(final String str, long j, TLRPC$InputFile tLRPC$InputFile) {
            addUploadProgress(str, j, 1.0f);
            ImportingSticker importingSticker = this.uploadSet.get(str);
            if (importingSticker == null) {
                return;
            }
            importingSticker.uploadMedia(SendMessagesHelper.this.currentAccount, tLRPC$InputFile, new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingStickers$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.ImportingStickers.this.lambda$onMediaImport$0(str);
                }
            });
        }

        public /* synthetic */ void lambda$onMediaImport$0(String str) {
            this.uploadSet.remove(str);
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            if (this.uploadSet.isEmpty()) {
                startImport();
            }
        }

        public void startImport() {
            TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet = new TLRPC$TL_stickers_createStickerSet();
            tLRPC$TL_stickers_createStickerSet.user_id = new TLRPC$TL_inputUserSelf();
            tLRPC$TL_stickers_createStickerSet.title = this.title;
            tLRPC$TL_stickers_createStickerSet.short_name = this.shortName;
            tLRPC$TL_stickers_createStickerSet.animated = this.uploadMedia.get(0).animated;
            String str = this.software;
            if (str != null) {
                tLRPC$TL_stickers_createStickerSet.software = str;
                tLRPC$TL_stickers_createStickerSet.flags |= 8;
            }
            int size = this.uploadMedia.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_inputStickerSetItem tLRPC$TL_inputStickerSetItem = this.uploadMedia.get(i).item;
                if (tLRPC$TL_inputStickerSetItem != null) {
                    tLRPC$TL_stickers_createStickerSet.stickers.add(tLRPC$TL_inputStickerSetItem);
                }
            }
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_stickers_createStickerSet, new AnonymousClass1(tLRPC$TL_stickers_createStickerSet));
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingStickers$1 */
        /* loaded from: classes.dex */
        public class AnonymousClass1 implements RequestDelegate {
            final /* synthetic */ TLRPC$TL_stickers_createStickerSet val$req;

            AnonymousClass1(TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet) {
                ImportingStickers.this = r1;
                this.val$req = tLRPC$TL_stickers_createStickerSet;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
                final TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet = this.val$req;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingStickers.AnonymousClass1.this.lambda$run$0(tLRPC$TL_error, tLRPC$TL_stickers_createStickerSet, tLObject);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet, TLObject tLObject) {
                SendMessagesHelper.this.importingStickersMap.remove(ImportingStickers.this.shortName);
                if (tLRPC$TL_error == null) {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, ImportingStickers.this.shortName);
                } else {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, ImportingStickers.this.shortName, tLRPC$TL_stickers_createStickerSet, tLRPC$TL_error);
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
                    NotificationCenter notificationCenter = SendMessagesHelper.this.getNotificationCenter();
                    int i = NotificationCenter.stickersImportComplete;
                    if (notificationCenter.hasObservers(i)) {
                        SendMessagesHelper.this.getNotificationCenter().postNotificationName(i, tLObject);
                    } else {
                        SendMessagesHelper.this.getMediaDataController().toggleStickerSet(null, tLObject, 2, null, false, false);
                    }
                }
            }
        }

        public void setImportProgress(int i) {
            if (i == 100) {
                SendMessagesHelper.this.importingStickersMap.remove(this.shortName);
            }
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
        }
    }

    static {
        int availableProcessors = Build.VERSION.SDK_INT >= 17 ? Runtime.getRuntime().availableProcessors() : 2;
        mediaSendThreadPool = new ThreadPoolExecutor(availableProcessors, availableProcessors, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        Instance = new SendMessagesHelper[4];
    }

    /* loaded from: classes.dex */
    public static class MediaSendPrepareWorker {
        public volatile String parentObject;
        public volatile TLRPC$TL_photo photo;
        public CountDownLatch sync;

        private MediaSendPrepareWorker() {
        }
    }

    /* loaded from: classes.dex */
    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener();
        private GpsLocationListener networkLocationListener = new GpsLocationListener();

        /* loaded from: classes.dex */
        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        /* loaded from: classes.dex */
        public class GpsLocationListener implements LocationListener {
            @Override // android.location.LocationListener
            public void onProviderDisabled(String str) {
            }

            @Override // android.location.LocationListener
            public void onProviderEnabled(String str) {
            }

            @Override // android.location.LocationListener
            public void onStatusChanged(String str, int i, Bundle bundle) {
            }

            private GpsLocationListener() {
                LocationProvider.this = r1;
            }

            @Override // android.location.LocationListener
            public void onLocationChanged(Location location) {
                if (location == null || LocationProvider.this.locationQueryCancelRunnable == null) {
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("found location " + location);
                }
                LocationProvider.this.lastKnownLocation = location;
                if (location.getAccuracy() >= 100.0f) {
                    return;
                }
                if (LocationProvider.this.delegate != null) {
                    LocationProvider.this.delegate.onLocationAcquired(location);
                }
                if (LocationProvider.this.locationQueryCancelRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                }
                LocationProvider.this.cleanup();
            }
        }

        public LocationProvider() {
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void cleanup() {
            this.locationManager.removeUpdates(this.gpsLocationListener);
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.lastKnownLocation = null;
            this.locationQueryCancelRunnable = null;
        }

        public void start() {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            }
            try {
                this.locationManager.requestLocationUpdates("gps", 1L, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1L, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            try {
                Location lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                this.lastKnownLocation = lastKnownLocation;
                if (lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
            Runnable runnable = this.locationQueryCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$LocationProvider$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.LocationProvider.this.lambda$start$0();
                }
            };
            this.locationQueryCancelRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 5000L);
        }

        public /* synthetic */ void lambda$start$0() {
            LocationProviderDelegate locationProviderDelegate = this.delegate;
            if (locationProviderDelegate != null) {
                Location location = this.lastKnownLocation;
                if (location != null) {
                    locationProviderDelegate.onLocationAcquired(location);
                } else {
                    locationProviderDelegate.onUnableLocationAcquire();
                }
            }
            cleanup();
        }

        public void stop() {
            if (this.locationManager == null) {
                return;
            }
            Runnable runnable = this.locationQueryCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            cleanup();
        }
    }

    /* loaded from: classes.dex */
    public class DelayedMessageSendAfterRequest {
        public DelayedMessage delayedMessage;
        public MessageObject msgObj;
        public ArrayList<MessageObject> msgObjs;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public TLObject request;
        public boolean scheduled;

        protected DelayedMessageSendAfterRequest() {
            SendMessagesHelper.this = r1;
        }
    }

    /* loaded from: classes.dex */
    public class DelayedMessage {
        public TLRPC$EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public ArrayList<String> httpLocations;
        public ArrayList<TLRPC$InputMedia> inputMedias;
        public TLRPC$InputMedia inputUploadMedia;
        public TLObject locationParent;
        public ArrayList<TLRPC$PhotoSize> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<TLRPC$Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        public TLRPC$PhotoSize photoSize;
        ArrayList<DelayedMessageSendAfterRequest> requests;
        public boolean retriedToSend;
        public boolean scheduled;
        public TLObject sendEncryptedRequest;
        public TLObject sendRequest;
        public int topMessageId;
        public int type;
        public VideoEditedInfo videoEditedInfo;
        public ArrayList<VideoEditedInfo> videoEditedInfos;

        public DelayedMessage(long j) {
            SendMessagesHelper.this = r1;
            this.peer = j;
        }

        public void initForGroup(long j) {
            this.type = 4;
            this.groupId = j;
            this.messageObjects = new ArrayList<>();
            this.messages = new ArrayList<>();
            this.inputMedias = new ArrayList<>();
            this.originalPaths = new ArrayList<>();
            this.parentObjects = new ArrayList<>();
            this.extraHashMap = new HashMap<>();
            this.locations = new ArrayList<>();
            this.httpLocations = new ArrayList<>();
            this.videoEditedInfos = new ArrayList<>();
        }

        public void addDelayedRequest(TLObject tLObject, MessageObject messageObject, String str, Object obj, DelayedMessage delayedMessage, boolean z) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObj = messageObject;
            delayedMessageSendAfterRequest.originalPath = str;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObject = obj;
            delayedMessageSendAfterRequest.scheduled = z;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(delayedMessageSendAfterRequest);
        }

        public void addDelayedRequest(TLObject tLObject, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage, boolean z) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObjs = arrayList;
            delayedMessageSendAfterRequest.originalPaths = arrayList2;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObjects = arrayList3;
            delayedMessageSendAfterRequest.scheduled = z;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(delayedMessageSendAfterRequest);
        }

        public void sendDelayedRequests() {
            ArrayList<DelayedMessageSendAfterRequest> arrayList = this.requests;
            if (arrayList != null) {
                int i = this.type;
                if (i != 4 && i != 0) {
                    return;
                }
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = this.requests.get(i2);
                    TLObject tLObject = delayedMessageSendAfterRequest.request;
                    if (tLObject instanceof TLRPC$TL_messages_sendEncryptedMultiMedia) {
                        SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessageSendAfterRequest.request, this);
                    } else if (!(tLObject instanceof TLRPC$TL_messages_sendMultiMedia)) {
                        SendMessagesHelper.this.performSendMessageRequest(tLObject, delayedMessageSendAfterRequest.msgObj, delayedMessageSendAfterRequest.originalPath, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.parentObject, null, delayedMessageSendAfterRequest.scheduled);
                    } else {
                        SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC$TL_messages_sendMultiMedia) tLObject, delayedMessageSendAfterRequest.msgObjs, delayedMessageSendAfterRequest.originalPaths, delayedMessageSendAfterRequest.parentObjects, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.scheduled);
                    }
                }
                this.requests = null;
            }
        }

        public void markAsError() {
            if (this.type == 4) {
                for (int i = 0; i < this.messageObjects.size(); i++) {
                    MessageObject messageObject = this.messageObjects.get(i);
                    SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                    messageObject.messageOwner.send_state = 2;
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                    SendMessagesHelper.this.processSentMessage(messageObject.getId());
                    SendMessagesHelper.this.removeFromUploadingMessages(messageObject.getId(), this.scheduled);
                }
                HashMap hashMap = SendMessagesHelper.this.delayedMessages;
                hashMap.remove("group_" + this.groupId);
            } else {
                MessagesStorage messagesStorage = SendMessagesHelper.this.getMessagesStorage();
                MessageObject messageObject2 = this.obj;
                messagesStorage.markMessageAsSendError(messageObject2.messageOwner, messageObject2.scheduled);
                this.obj.messageOwner.send_state = 2;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
                SendMessagesHelper.this.processSentMessage(this.obj.getId());
                SendMessagesHelper.this.removeFromUploadingMessages(this.obj.getId(), this.scheduled);
            }
            sendDelayedRequests();
        }
    }

    public static SendMessagesHelper getInstance(int i) {
        SendMessagesHelper sendMessagesHelper = Instance[i];
        if (sendMessagesHelper == null) {
            synchronized (SendMessagesHelper.class) {
                sendMessagesHelper = Instance[i];
                if (sendMessagesHelper == null) {
                    SendMessagesHelper[] sendMessagesHelperArr = Instance;
                    SendMessagesHelper sendMessagesHelper2 = new SendMessagesHelper(i);
                    sendMessagesHelperArr[i] = sendMessagesHelper2;
                    sendMessagesHelper = sendMessagesHelper2;
                }
            }
        }
        return sendMessagesHelper;
    }

    public SendMessagesHelper(int i) {
        super(i);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$new$0();
            }
        });
    }

    public /* synthetic */ void lambda$new$0() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingStarted);
        getNotificationCenter().addObserver(this, NotificationCenter.fileNewChunkAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.editingMessages.clear();
        this.sendingMessagesIdDialogs.clear();
        this.uploadMessages.clear();
        this.uploadingMessagesIdDialogs.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.waitingForVote.clear();
        this.importingHistoryFiles.clear();
        this.importingHistoryMap.clear();
        this.importingStickersFiles.clear();
        this.importingStickersMap.clear();
        this.locationProvider.stop();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        ArrayList<DelayedMessage> arrayList;
        final MessageObject messageObject;
        char c;
        MessageObject messageObject2;
        TLRPC$InputMedia tLRPC$InputMedia;
        TLRPC$InputFile tLRPC$InputFile;
        String str2;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        ArrayList<DelayedMessage> arrayList2;
        TLObject tLObject;
        TLRPC$TL_decryptedMessage tLRPC$TL_decryptedMessage;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile2;
        int i3;
        ArrayList<DelayedMessage> arrayList3;
        int i4;
        String str3;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$PhotoSize tLRPC$PhotoSize2;
        int i5 = 0;
        boolean z = true;
        if (i == NotificationCenter.fileUploadProgressChanged) {
            String str4 = (String) objArr[0];
            ImportingHistory importingHistory = this.importingHistoryFiles.get(str4);
            if (importingHistory != null) {
                Long l = (Long) objArr[1];
                importingHistory.addUploadProgress(str4, l.longValue(), ((float) l.longValue()) / ((float) ((Long) objArr[2]).longValue()));
            }
            ImportingStickers importingStickers = this.importingStickersFiles.get(str4);
            if (importingStickers == null) {
                return;
            }
            Long l2 = (Long) objArr[1];
            importingStickers.addUploadProgress(str4, l2.longValue(), ((float) l2.longValue()) / ((float) ((Long) objArr[2]).longValue()));
        } else if (i == NotificationCenter.fileUploaded) {
            String str5 = (String) objArr[0];
            TLRPC$InputFile tLRPC$InputFile2 = (TLRPC$InputFile) objArr[1];
            TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile3 = (TLRPC$InputEncryptedFile) objArr[2];
            ImportingHistory importingHistory2 = this.importingHistoryFiles.get(str5);
            if (importingHistory2 != null) {
                if (str5.equals(importingHistory2.historyPath)) {
                    importingHistory2.initImport(tLRPC$InputFile2);
                } else {
                    importingHistory2.onMediaImport(str5, ((Long) objArr[5]).longValue(), tLRPC$InputFile2);
                }
            }
            ImportingStickers importingStickers2 = this.importingStickersFiles.get(str5);
            if (importingStickers2 != null) {
                importingStickers2.onMediaImport(str5, ((Long) objArr[5]).longValue(), tLRPC$InputFile2);
            }
            ArrayList<DelayedMessage> arrayList4 = this.delayedMessages.get(str5);
            if (arrayList4 == null) {
                return;
            }
            while (i5 < arrayList4.size()) {
                DelayedMessage delayedMessage = arrayList4.get(i5);
                TLObject tLObject2 = delayedMessage.sendRequest;
                if (tLObject2 instanceof TLRPC$TL_messages_sendMedia) {
                    tLRPC$InputMedia = ((TLRPC$TL_messages_sendMedia) tLObject2).media;
                } else if (tLObject2 instanceof TLRPC$TL_messages_editMessage) {
                    tLRPC$InputMedia = ((TLRPC$TL_messages_editMessage) tLObject2).media;
                } else {
                    tLRPC$InputMedia = tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia ? (TLRPC$InputMedia) delayedMessage.extraHashMap.get(str5) : null;
                }
                if (tLRPC$InputFile2 == null || tLRPC$InputMedia == null) {
                    arrayList2 = arrayList4;
                    TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile4 = tLRPC$InputEncryptedFile3;
                    tLRPC$InputFile = tLRPC$InputFile2;
                    str2 = str5;
                    tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile4;
                    if (tLRPC$InputEncryptedFile != null && (tLObject = delayedMessage.sendEncryptedRequest) != null) {
                        if (delayedMessage.type == 4) {
                            TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) tLObject;
                            TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile5 = (TLRPC$InputEncryptedFile) delayedMessage.extraHashMap.get(str2);
                            int indexOf = tLRPC$TL_messages_sendEncryptedMultiMedia.files.indexOf(tLRPC$InputEncryptedFile5);
                            if (indexOf >= 0) {
                                tLRPC$TL_messages_sendEncryptedMultiMedia.files.set(indexOf, tLRPC$InputEncryptedFile);
                                if (tLRPC$InputEncryptedFile5.id == 1) {
                                    MessageObject messageObject3 = (MessageObject) delayedMessage.extraHashMap.get(str2 + "_i");
                                    delayedMessage.photoSize = (TLRPC$PhotoSize) delayedMessage.extraHashMap.get(str2 + "_t");
                                    stopVideoService(delayedMessage.messageObjects.get(indexOf).messageOwner.attachPath);
                                }
                                tLRPC$TL_decryptedMessage = tLRPC$TL_messages_sendEncryptedMultiMedia.messages.get(indexOf);
                            } else {
                                tLRPC$TL_decryptedMessage = null;
                            }
                        } else {
                            tLRPC$TL_decryptedMessage = (TLRPC$TL_decryptedMessage) tLObject;
                        }
                        if (tLRPC$TL_decryptedMessage != null) {
                            TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia = tLRPC$TL_decryptedMessage.media;
                            if ((tLRPC$DecryptedMessageMedia instanceof TLRPC$TL_decryptedMessageMediaVideo) || (tLRPC$DecryptedMessageMedia instanceof TLRPC$TL_decryptedMessageMediaPhoto) || (tLRPC$DecryptedMessageMedia instanceof TLRPC$TL_decryptedMessageMediaDocument)) {
                                tLRPC$TL_decryptedMessage.media.size = ((Long) objArr[5]).longValue();
                            }
                            TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia2 = tLRPC$TL_decryptedMessage.media;
                            tLRPC$DecryptedMessageMedia2.key = (byte[]) objArr[3];
                            tLRPC$DecryptedMessageMedia2.iv = (byte[]) objArr[4];
                            if (delayedMessage.type == 4) {
                                uploadMultiMedia(delayedMessage, null, tLRPC$InputEncryptedFile, str2);
                            } else {
                                SecretChatHelper secretChatHelper = getSecretChatHelper();
                                MessageObject messageObject4 = delayedMessage.obj;
                                secretChatHelper.performSendEncryptedRequest(tLRPC$TL_decryptedMessage, messageObject4.messageOwner, delayedMessage.encryptedChat, tLRPC$InputEncryptedFile, delayedMessage.originalPath, messageObject4);
                            }
                        }
                        arrayList2.remove(i5);
                        i5--;
                    }
                } else {
                    int i6 = delayedMessage.type;
                    if (i6 == 0) {
                        tLRPC$InputMedia.file = tLRPC$InputFile2;
                        arrayList3 = arrayList4;
                        tLRPC$InputEncryptedFile2 = tLRPC$InputEncryptedFile3;
                        i4 = i5;
                        tLRPC$InputFile = tLRPC$InputFile2;
                        str3 = str5;
                        performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, null, delayedMessage.parentObject, null, delayedMessage.scheduled);
                    } else {
                        arrayList3 = arrayList4;
                        tLRPC$InputEncryptedFile2 = tLRPC$InputEncryptedFile3;
                        i4 = i5;
                        tLRPC$InputFile = tLRPC$InputFile2;
                        str3 = str5;
                        if (i6 == z) {
                            if (tLRPC$InputMedia.file == null) {
                                tLRPC$InputMedia.file = tLRPC$InputFile;
                                if (tLRPC$InputMedia.thumb == null && (tLRPC$PhotoSize2 = delayedMessage.photoSize) != null && tLRPC$PhotoSize2.location != null) {
                                    performSendDelayedMessage(delayedMessage);
                                } else {
                                    performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, null, delayedMessage.scheduled);
                                }
                            } else {
                                tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                tLRPC$InputMedia.flags |= 4;
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, null, delayedMessage.scheduled);
                            }
                        } else if (i6 == 2) {
                            if (tLRPC$InputMedia.file == null) {
                                tLRPC$InputMedia.file = tLRPC$InputFile;
                                if (tLRPC$InputMedia.thumb == null && (tLRPC$PhotoSize = delayedMessage.photoSize) != null && tLRPC$PhotoSize.location != null) {
                                    performSendDelayedMessage(delayedMessage);
                                } else {
                                    performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, null, delayedMessage.scheduled);
                                }
                            } else {
                                tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                tLRPC$InputMedia.flags |= 4;
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, null, delayedMessage.scheduled);
                            }
                        } else if (i6 == 3) {
                            tLRPC$InputMedia.file = tLRPC$InputFile;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, null, delayedMessage.scheduled);
                        } else {
                            if (i6 != 4) {
                                str2 = str3;
                            } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedDocument) {
                                if (tLRPC$InputMedia.file == null) {
                                    tLRPC$InputMedia.file = tLRPC$InputFile;
                                    HashMap<Object, Object> hashMap = delayedMessage.extraHashMap;
                                    StringBuilder sb = new StringBuilder();
                                    str2 = str3;
                                    sb.append(str2);
                                    sb.append("_i");
                                    int indexOf2 = delayedMessage.messageObjects.indexOf((MessageObject) hashMap.get(sb.toString()));
                                    if (indexOf2 >= 0) {
                                        stopVideoService(delayedMessage.messageObjects.get(indexOf2).messageOwner.attachPath);
                                    }
                                    TLRPC$PhotoSize tLRPC$PhotoSize3 = (TLRPC$PhotoSize) delayedMessage.extraHashMap.get(str2 + "_t");
                                    delayedMessage.photoSize = tLRPC$PhotoSize3;
                                    if (tLRPC$InputMedia.thumb == null && tLRPC$PhotoSize3 != null && tLRPC$PhotoSize3.location != null) {
                                        delayedMessage.performMediaUpload = z;
                                        performSendDelayedMessage(delayedMessage, indexOf2);
                                    } else {
                                        uploadMultiMedia(delayedMessage, tLRPC$InputMedia, null, str2);
                                    }
                                } else {
                                    str2 = str3;
                                    tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                    tLRPC$InputMedia.flags |= 4;
                                    uploadMultiMedia(delayedMessage, tLRPC$InputMedia, null, (String) delayedMessage.extraHashMap.get(str2 + "_o"));
                                }
                            } else {
                                str2 = str3;
                                tLRPC$InputMedia.file = tLRPC$InputFile;
                                uploadMultiMedia(delayedMessage, tLRPC$InputMedia, null, str2);
                            }
                            arrayList2 = arrayList3;
                            i3 = i4;
                            arrayList2.remove(i3);
                            i5 = i3 - 1;
                            tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile2;
                        }
                    }
                    arrayList2 = arrayList3;
                    i3 = i4;
                    str2 = str3;
                    arrayList2.remove(i3);
                    i5 = i3 - 1;
                    tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile2;
                }
                i5++;
                arrayList4 = arrayList2;
                str5 = str2;
                tLRPC$InputFile2 = tLRPC$InputFile;
                z = true;
                tLRPC$InputEncryptedFile3 = tLRPC$InputEncryptedFile;
            }
            String str6 = str5;
            if (!arrayList4.isEmpty()) {
                return;
            }
            this.delayedMessages.remove(str6);
        } else if (i == NotificationCenter.fileUploadFailed) {
            String str7 = (String) objArr[0];
            boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
            ImportingHistory importingHistory3 = this.importingHistoryFiles.get(str7);
            if (importingHistory3 != null) {
                importingHistory3.onFileFailedToUpload(str7);
            }
            ImportingStickers importingStickers3 = this.importingStickersFiles.get(str7);
            if (importingStickers3 != null) {
                importingStickers3.onFileFailedToUpload(str7);
            }
            ArrayList<DelayedMessage> arrayList5 = this.delayedMessages.get(str7);
            if (arrayList5 == null) {
                return;
            }
            while (i5 < arrayList5.size()) {
                DelayedMessage delayedMessage2 = arrayList5.get(i5);
                if ((booleanValue && delayedMessage2.sendEncryptedRequest != null) || (!booleanValue && delayedMessage2.sendRequest != null)) {
                    delayedMessage2.markAsError();
                    arrayList5.remove(i5);
                    i5--;
                }
                i5++;
            }
            if (!arrayList5.isEmpty()) {
                return;
            }
            this.delayedMessages.remove(str7);
        } else if (i == NotificationCenter.filePreparingStarted) {
            MessageObject messageObject5 = (MessageObject) objArr[0];
            if (messageObject5.getId() == 0) {
                return;
            }
            String str8 = (String) objArr[1];
            ArrayList<DelayedMessage> arrayList6 = this.delayedMessages.get(messageObject5.messageOwner.attachPath);
            if (arrayList6 == null) {
                return;
            }
            while (true) {
                if (i5 >= arrayList6.size()) {
                    break;
                }
                DelayedMessage delayedMessage3 = arrayList6.get(i5);
                if (delayedMessage3.type == 4) {
                    int indexOf3 = delayedMessage3.messageObjects.indexOf(messageObject5);
                    delayedMessage3.photoSize = (TLRPC$PhotoSize) delayedMessage3.extraHashMap.get(messageObject5.messageOwner.attachPath + "_t");
                    delayedMessage3.performMediaUpload = true;
                    performSendDelayedMessage(delayedMessage3, indexOf3);
                    arrayList6.remove(i5);
                    break;
                } else if (delayedMessage3.obj == messageObject5) {
                    delayedMessage3.videoEditedInfo = null;
                    performSendDelayedMessage(delayedMessage3);
                    arrayList6.remove(i5);
                    break;
                } else {
                    i5++;
                }
            }
            if (!arrayList6.isEmpty()) {
                return;
            }
            this.delayedMessages.remove(messageObject5.messageOwner.attachPath);
        } else {
            MessageObject messageObject6 = null;
            if (i == NotificationCenter.fileNewChunkAvailable) {
                MessageObject messageObject7 = (MessageObject) objArr[0];
                if (messageObject7.getId() == 0) {
                    return;
                }
                long longValue = ((Long) objArr[2]).longValue();
                long longValue2 = ((Long) objArr[3]).longValue();
                getFileLoader().checkUploadNewDataAvailable((String) objArr[1], DialogObject.isEncryptedDialog(messageObject7.getDialogId()), longValue, longValue2);
                if (longValue2 == 0) {
                    return;
                }
                stopVideoService(messageObject7.messageOwner.attachPath);
                ArrayList<DelayedMessage> arrayList7 = this.delayedMessages.get(messageObject7.messageOwner.attachPath);
                if (arrayList7 == null) {
                    return;
                }
                for (int i7 = 0; i7 < arrayList7.size(); i7++) {
                    DelayedMessage delayedMessage4 = arrayList7.get(i7);
                    if (delayedMessage4.type == 4) {
                        int i8 = 0;
                        while (true) {
                            if (i8 >= delayedMessage4.messageObjects.size()) {
                                break;
                            }
                            MessageObject messageObject8 = delayedMessage4.messageObjects.get(i8);
                            if (messageObject8 == messageObject7) {
                                delayedMessage4.obj.shouldRemoveVideoEditedInfo = true;
                                messageObject8.messageOwner.params.remove("ve");
                                messageObject8.messageOwner.media.document.size = longValue2;
                                ArrayList<TLRPC$Message> arrayList8 = new ArrayList<>();
                                arrayList8.add(messageObject8.messageOwner);
                                getMessagesStorage().putMessages(arrayList8, false, true, false, 0, messageObject8.scheduled);
                                break;
                            }
                            i8++;
                        }
                    } else {
                        MessageObject messageObject9 = delayedMessage4.obj;
                        if (messageObject9 == messageObject7) {
                            messageObject9.shouldRemoveVideoEditedInfo = true;
                            messageObject9.messageOwner.params.remove("ve");
                            delayedMessage4.obj.messageOwner.media.document.size = longValue2;
                            ArrayList<TLRPC$Message> arrayList9 = new ArrayList<>();
                            arrayList9.add(delayedMessage4.obj.messageOwner);
                            getMessagesStorage().putMessages(arrayList9, false, true, false, 0, delayedMessage4.obj.scheduled);
                            return;
                        }
                    }
                }
            } else if (i == NotificationCenter.filePreparingFailed) {
                MessageObject messageObject10 = (MessageObject) objArr[0];
                if (messageObject10.getId() == 0) {
                    return;
                }
                String str9 = (String) objArr[1];
                stopVideoService(messageObject10.messageOwner.attachPath);
                ArrayList<DelayedMessage> arrayList10 = this.delayedMessages.get(str9);
                if (arrayList10 == null) {
                    return;
                }
                int i9 = 0;
                while (i9 < arrayList10.size()) {
                    DelayedMessage delayedMessage5 = arrayList10.get(i9);
                    if (delayedMessage5.type == 4) {
                        for (int i10 = 0; i10 < delayedMessage5.messages.size(); i10++) {
                            if (delayedMessage5.messageObjects.get(i10) == messageObject10) {
                                delayedMessage5.markAsError();
                                arrayList10.remove(i9);
                                i9--;
                                break;
                            }
                        }
                        i9++;
                    } else if (delayedMessage5.obj == messageObject10) {
                        delayedMessage5.markAsError();
                        arrayList10.remove(i9);
                        i9--;
                        break;
                        i9++;
                    } else {
                        i9++;
                    }
                }
                if (!arrayList10.isEmpty()) {
                    return;
                }
                this.delayedMessages.remove(str9);
            } else if (i == NotificationCenter.httpFileDidLoad) {
                final String str10 = (String) objArr[0];
                ArrayList<DelayedMessage> arrayList11 = this.delayedMessages.get(str10);
                if (arrayList11 == null) {
                    return;
                }
                int i11 = 0;
                while (i11 < arrayList11.size()) {
                    final DelayedMessage delayedMessage6 = arrayList11.get(i11);
                    int i12 = delayedMessage6.type;
                    if (i12 == 0) {
                        messageObject = delayedMessage6.obj;
                        c = 0;
                    } else {
                        if (i12 == 2) {
                            messageObject2 = delayedMessage6.obj;
                        } else if (i12 == 4) {
                            messageObject2 = (MessageObject) delayedMessage6.extraHashMap.get(str10);
                            if (messageObject2.getDocument() == null) {
                                messageObject = messageObject2;
                                c = 0;
                            }
                        } else {
                            c = 65535;
                            messageObject = messageObject6;
                        }
                        messageObject = messageObject2;
                        c = 1;
                    }
                    if (c == 0) {
                        final File file = new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + "." + ImageLoader.getHttpUrlExtension(str10, "file"));
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda25
                            @Override // java.lang.Runnable
                            public final void run() {
                                SendMessagesHelper.this.lambda$didReceivedNotification$2(file, messageObject, delayedMessage6, str10);
                            }
                        });
                    } else if (c == 1) {
                        final File file2 = new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + ".gif");
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda39
                            @Override // java.lang.Runnable
                            public final void run() {
                                SendMessagesHelper.this.lambda$didReceivedNotification$4(delayedMessage6, file2, messageObject);
                            }
                        });
                        i11++;
                        messageObject6 = null;
                    }
                    i11++;
                    messageObject6 = null;
                }
                this.delayedMessages.remove(str10);
            } else if (i == NotificationCenter.fileLoaded) {
                String str11 = (String) objArr[0];
                ArrayList<DelayedMessage> arrayList12 = this.delayedMessages.get(str11);
                if (arrayList12 == null) {
                    return;
                }
                while (i5 < arrayList12.size()) {
                    performSendDelayedMessage(arrayList12.get(i5));
                    i5++;
                }
                this.delayedMessages.remove(str11);
            } else if ((i == NotificationCenter.httpFileDidFailedLoad || i == NotificationCenter.fileLoadFailed) && (arrayList = this.delayedMessages.get((str = (String) objArr[0]))) != null) {
                while (i5 < arrayList.size()) {
                    arrayList.get(i5).markAsError();
                    i5++;
                }
                this.delayedMessages.remove(str);
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$2(final File file, final MessageObject messageObject, final DelayedMessage delayedMessage, final String str) {
        final TLRPC$TL_photo generatePhotoSizes = generatePhotoSizes(file.toString(), null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$didReceivedNotification$1(generatePhotoSizes, messageObject, file, delayedMessage, str);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$1(TLRPC$TL_photo tLRPC$TL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
        if (tLRPC$TL_photo != null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            tLRPC$Message.media.photo = tLRPC$TL_photo;
            tLRPC$Message.attachPath = file.toString();
            ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
            arrayList.add(messageObject.messageOwner);
            getMessagesStorage().putMessages(arrayList, false, true, false, 0, messageObject.scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            ArrayList<TLRPC$PhotoSize> arrayList2 = tLRPC$TL_photo.sizes;
            delayedMessage.photoSize = arrayList2.get(arrayList2.size() - 1);
            delayedMessage.locationParent = tLRPC$TL_photo;
            delayedMessage.httpLocation = null;
            if (delayedMessage.type == 4) {
                delayedMessage.performMediaUpload = true;
                performSendDelayedMessage(delayedMessage, delayedMessage.messageObjects.indexOf(messageObject));
                return;
            }
            performSendDelayedMessage(delayedMessage);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("can't load image " + str + " to file " + file.toString());
        }
        delayedMessage.markAsError();
    }

    public /* synthetic */ void lambda$didReceivedNotification$4(final DelayedMessage delayedMessage, final File file, final MessageObject messageObject) {
        final TLRPC$Document document = delayedMessage.obj.getDocument();
        boolean z = false;
        if (document.thumbs.isEmpty() || (document.thumbs.get(0).location instanceof TLRPC$TL_fileLocationUnavailable)) {
            try {
                Bitmap loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                if (loadBitmap != null) {
                    document.thumbs.clear();
                    ArrayList<TLRPC$PhotoSize> arrayList = document.thumbs;
                    if (delayedMessage.sendEncryptedRequest != null) {
                        z = true;
                    }
                    arrayList.add(ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, z));
                    loadBitmap.recycle();
                }
            } catch (Exception e) {
                document.thumbs.clear();
                FileLog.e(e);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$didReceivedNotification$3(delayedMessage, file, document, messageObject);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$3(DelayedMessage delayedMessage, File file, TLRPC$Document tLRPC$Document, MessageObject messageObject) {
        delayedMessage.httpLocation = null;
        delayedMessage.obj.messageOwner.attachPath = file.toString();
        if (!tLRPC$Document.thumbs.isEmpty()) {
            TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Document.thumbs.get(0);
            if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize)) {
                delayedMessage.photoSize = tLRPC$PhotoSize;
                delayedMessage.locationParent = tLRPC$Document;
            }
        }
        ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
        arrayList.add(messageObject.messageOwner);
        getMessagesStorage().putMessages(arrayList, false, true, false, 0, messageObject.scheduled);
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
        getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject messageObject) {
        messageObject.cancelEditing = true;
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        tLRPC$Message.media = messageObject.previousMedia;
        tLRPC$Message.message = messageObject.previousMessage;
        ArrayList<TLRPC$MessageEntity> arrayList = messageObject.previousMessageEntities;
        tLRPC$Message.entities = arrayList;
        tLRPC$Message.attachPath = messageObject.previousAttachPath;
        tLRPC$Message.send_state = 0;
        if (arrayList != null) {
            tLRPC$Message.flags |= ConnectionsManager.RequestFlagNeedQuickAck;
        } else {
            tLRPC$Message.flags &= -129;
        }
        messageObject.previousMedia = null;
        messageObject.previousMessage = null;
        messageObject.previousMessageEntities = null;
        messageObject.previousAttachPath = null;
        messageObject.videoEditedInfo = null;
        messageObject.type = -1;
        messageObject.setType();
        messageObject.caption = null;
        if (messageObject.type != 0) {
            messageObject.generateCaption();
        } else {
            messageObject.resetLayout();
            messageObject.checkLayout();
        }
        ArrayList<TLRPC$Message> arrayList2 = new ArrayList<>();
        arrayList2.add(messageObject.messageOwner);
        getMessagesStorage().putMessages(arrayList2, false, true, false, 0, messageObject.scheduled);
        ArrayList arrayList3 = new ArrayList();
        arrayList3.add(messageObject);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList3);
    }

    public void cancelSendingMessage(MessageObject messageObject) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(messageObject);
        cancelSendingMessage(arrayList);
    }

    /* JADX WARN: Code restructure failed: missing block: B:56:0x019d, code lost:
        r23 = r9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void cancelSendingMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r26) {
        /*
            Method dump skipped, instructions count: 551
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.cancelSendingMessage(java.util.ArrayList):void");
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean z) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessage(messageObject, null, null, null, null, null, true, messageObject);
            }
            return false;
        }
        TLRPC$MessageAction tLRPC$MessageAction = messageObject.messageOwner.action;
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(messageObject.getDialogId())));
            if (encryptedChat == null) {
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                messageObject.messageOwner.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message.random_id == 0) {
                tLRPC$Message.random_id = getNextRandomId();
            }
            TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = messageObject.messageOwner.action.encryptedAction;
            if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL) {
                getSecretChatHelper().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionDeleteMessages) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionFlushHistory) {
                getSecretChatHelper().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionNotifyLayer) {
                getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionReadMessages) {
                getSecretChatHelper().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) {
                getSecretChatHelper().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!(tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionTyping)) {
                if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionResend) {
                    getSecretChatHelper().sendResendMessage(encryptedChat, 0, 0, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionCommitKey) {
                    getSecretChatHelper().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionAbortKey) {
                    getSecretChatHelper().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0L);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionRequestKey) {
                    getSecretChatHelper().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionAcceptKey) {
                    getSecretChatHelper().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionNoop) {
                    getSecretChatHelper().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionScreenshotTaken) {
            sendScreenshotMessage(getMessagesController().getUser(Long.valueOf(messageObject.getDialogId())), messageObject.getReplyMsgId(), messageObject.messageOwner);
        }
        if (z) {
            this.unsentMessages.put(messageObject.getId(), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    public void processSentMessage(int i) {
        int size = this.unsentMessages.size();
        this.unsentMessages.remove(i);
        if (size == 0 || this.unsentMessages.size() != 0) {
            return;
        }
        checkUnsentMessages();
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0070  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x008e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void processForwardFromMyName(org.telegram.messenger.MessageObject r18, long r19) {
        /*
            Method dump skipped, instructions count: 408
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.processForwardFromMyName(org.telegram.messenger.MessageObject, long):void");
    }

    public void sendScreenshotMessage(TLRPC$User tLRPC$User, int i, TLRPC$Message tLRPC$Message) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        if (tLRPC$User == null || i == 0 || tLRPC$User.id == getUserConfig().getClientUserId()) {
            return;
        }
        TLRPC$TL_messages_sendScreenshotNotification tLRPC$TL_messages_sendScreenshotNotification = new TLRPC$TL_messages_sendScreenshotNotification();
        TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
        tLRPC$TL_messages_sendScreenshotNotification.peer = tLRPC$TL_inputPeerUser;
        tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
        tLRPC$TL_inputPeerUser.user_id = tLRPC$User.id;
        if (tLRPC$Message2 != null) {
            tLRPC$TL_messages_sendScreenshotNotification.reply_to_msg_id = i;
            tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message2.random_id;
        } else {
            tLRPC$Message2 = new TLRPC$TL_messageService();
            tLRPC$Message2.random_id = getNextRandomId();
            tLRPC$Message2.dialog_id = tLRPC$User.id;
            tLRPC$Message2.unread = true;
            tLRPC$Message2.out = true;
            int newMessageId = getUserConfig().getNewMessageId();
            tLRPC$Message2.id = newMessageId;
            tLRPC$Message2.local_id = newMessageId;
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$Message2.from_id = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
            int i2 = tLRPC$Message2.flags | 256;
            tLRPC$Message2.flags = i2;
            tLRPC$Message2.flags = i2 | 8;
            TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
            tLRPC$Message2.reply_to = tLRPC$TL_messageReplyHeader;
            tLRPC$TL_messageReplyHeader.reply_to_msg_id = i;
            TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
            tLRPC$Message2.peer_id = tLRPC$TL_peerUser2;
            tLRPC$TL_peerUser2.user_id = tLRPC$User.id;
            tLRPC$Message2.date = getConnectionsManager().getCurrentTime();
            tLRPC$Message2.action = new TLRPC$TL_messageActionScreenshotTaken();
            getUserConfig().saveConfig(false);
        }
        tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message2.random_id;
        MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message2, false, true);
        messageObject.messageOwner.send_state = 1;
        messageObject.wasJustSent = true;
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(messageObject);
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message2.dialog_id, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        ArrayList<TLRPC$Message> arrayList2 = new ArrayList<>();
        arrayList2.add(tLRPC$Message2);
        getMessagesStorage().putMessages(arrayList2, false, true, false, 0, false);
        performSendMessageRequest(tLRPC$TL_messages_sendScreenshotNotification, messageObject, null, null, null, null, false);
    }

    public void sendSticker(TLRPC$Document tLRPC$Document, String str, final long j, final MessageObject messageObject, final MessageObject messageObject2, final Object obj, final MessageObject.SendAnimationData sendAnimationData, final boolean z, final int i) {
        final TLRPC$TL_document_layer82 tLRPC$TL_document_layer82;
        HashMap<String, String> hashMap;
        if (tLRPC$Document == null) {
            return;
        }
        if (DialogObject.isEncryptedDialog(j)) {
            if (getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j))) == null) {
                return;
            }
            TLRPC$TL_document_layer82 tLRPC$TL_document_layer822 = new TLRPC$TL_document_layer82();
            tLRPC$TL_document_layer822.id = tLRPC$Document.id;
            tLRPC$TL_document_layer822.access_hash = tLRPC$Document.access_hash;
            tLRPC$TL_document_layer822.date = tLRPC$Document.date;
            tLRPC$TL_document_layer822.mime_type = tLRPC$Document.mime_type;
            byte[] bArr = tLRPC$Document.file_reference;
            tLRPC$TL_document_layer822.file_reference = bArr;
            if (bArr == null) {
                tLRPC$TL_document_layer822.file_reference = new byte[0];
            }
            tLRPC$TL_document_layer822.size = tLRPC$Document.size;
            tLRPC$TL_document_layer822.dc_id = tLRPC$Document.dc_id;
            tLRPC$TL_document_layer822.attributes = new ArrayList<>(tLRPC$Document.attributes);
            if (tLRPC$TL_document_layer822.mime_type == null) {
                tLRPC$TL_document_layer822.mime_type = "";
            }
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90);
            if ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoSizeProgressive)) {
                File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true);
                if (pathToAttach.exists()) {
                    try {
                        pathToAttach.length();
                        byte[] bArr2 = new byte[(int) pathToAttach.length()];
                        new RandomAccessFile(pathToAttach, "r").readFully(bArr2);
                        TLRPC$TL_photoCachedSize tLRPC$TL_photoCachedSize = new TLRPC$TL_photoCachedSize();
                        TLRPC$TL_fileLocation_layer82 tLRPC$TL_fileLocation_layer82 = new TLRPC$TL_fileLocation_layer82();
                        TLRPC$FileLocation tLRPC$FileLocation = closestPhotoSizeWithSize.location;
                        tLRPC$TL_fileLocation_layer82.dc_id = tLRPC$FileLocation.dc_id;
                        tLRPC$TL_fileLocation_layer82.volume_id = tLRPC$FileLocation.volume_id;
                        tLRPC$TL_fileLocation_layer82.local_id = tLRPC$FileLocation.local_id;
                        tLRPC$TL_fileLocation_layer82.secret = tLRPC$FileLocation.secret;
                        tLRPC$TL_photoCachedSize.location = tLRPC$TL_fileLocation_layer82;
                        tLRPC$TL_photoCachedSize.size = closestPhotoSizeWithSize.size;
                        tLRPC$TL_photoCachedSize.w = closestPhotoSizeWithSize.w;
                        tLRPC$TL_photoCachedSize.h = closestPhotoSizeWithSize.h;
                        tLRPC$TL_photoCachedSize.type = closestPhotoSizeWithSize.type;
                        tLRPC$TL_photoCachedSize.bytes = bArr2;
                        tLRPC$TL_document_layer822.thumbs.add(tLRPC$TL_photoCachedSize);
                        tLRPC$TL_document_layer822.flags |= 1;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            if (tLRPC$TL_document_layer822.thumbs.isEmpty()) {
                TLRPC$TL_photoSizeEmpty tLRPC$TL_photoSizeEmpty = new TLRPC$TL_photoSizeEmpty();
                tLRPC$TL_photoSizeEmpty.type = "s";
                tLRPC$TL_document_layer822.thumbs.add(tLRPC$TL_photoSizeEmpty);
            }
            tLRPC$TL_document_layer82 = tLRPC$TL_document_layer822;
        } else {
            tLRPC$TL_document_layer82 = tLRPC$Document;
        }
        if (MessageObject.isGifDocument(tLRPC$TL_document_layer82)) {
            mediaSendQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$sendSticker$6(tLRPC$TL_document_layer82, j, messageObject, messageObject2, z, i, obj, sendAnimationData);
                }
            });
            return;
        }
        if (!TextUtils.isEmpty(str)) {
            hashMap = new HashMap<>();
            hashMap.put(SearchIntents.EXTRA_QUERY, str);
        } else {
            hashMap = null;
        }
        sendMessage((TLRPC$TL_document) tLRPC$TL_document_layer82, null, null, j, messageObject, messageObject2, null, null, null, hashMap, z, i, 0, obj, sendAnimationData);
    }

    public /* synthetic */ void lambda$sendSticker$6(final TLRPC$Document tLRPC$Document, final long j, final MessageObject messageObject, final MessageObject messageObject2, final boolean z, final int i, final Object obj, final MessageObject.SendAnimationData sendAnimationData) {
        String str;
        final Bitmap[] bitmapArr = new Bitmap[1];
        final String[] strArr = new String[1];
        String key = ImageLocation.getForDocument(tLRPC$Document).getKey(null, null, false);
        if ("video/mp4".equals(tLRPC$Document.mime_type)) {
            str = ".mp4";
        } else {
            str = "video/x-matroska".equals(tLRPC$Document.mime_type) ? ".mkv" : "";
        }
        File directory = FileLoader.getDirectory(3);
        File file = new File(directory, key + str);
        if (!file.exists()) {
            File directory2 = FileLoader.getDirectory(2);
            file = new File(directory2, key + str);
        }
        ensureMediaThumbExists(getAccountInstance(), false, tLRPC$Document, file.getAbsolutePath(), null, 0L);
        strArr[0] = getKeyForPhotoSize(getAccountInstance(), FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 320), bitmapArr, true, true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda72
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendSticker$5(bitmapArr, strArr, tLRPC$Document, j, messageObject, messageObject2, z, i, obj, sendAnimationData);
            }
        });
    }

    public /* synthetic */ void lambda$sendSticker$5(Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        if (bitmapArr[0] != null && strArr[0] != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, null, null, j, messageObject, messageObject2, null, null, null, null, z, i, 0, obj, sendAnimationData);
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x02d3  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0390  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x03ae  */
    /* JADX WARN: Removed duplicated region for block: B:225:0x054a  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0554  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x056d  */
    /* JADX WARN: Removed duplicated region for block: B:244:0x05c7  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x05d9  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x05f6  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x061d  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x061f  */
    /* JADX WARN: Removed duplicated region for block: B:278:0x0690  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x0692  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x06ad  */
    /* JADX WARN: Removed duplicated region for block: B:290:0x06e1  */
    /* JADX WARN: Removed duplicated region for block: B:292:0x06e7  */
    /* JADX WARN: Removed duplicated region for block: B:293:0x06e9  */
    /* JADX WARN: Removed duplicated region for block: B:296:0x06f1  */
    /* JADX WARN: Removed duplicated region for block: B:299:0x072d  */
    /* JADX WARN: Removed duplicated region for block: B:306:0x0768  */
    /* JADX WARN: Removed duplicated region for block: B:309:0x077d  */
    /* JADX WARN: Removed duplicated region for block: B:310:0x0780  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x078d  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x078f  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x07af  */
    /* JADX WARN: Removed duplicated region for block: B:323:0x07d5  */
    /* JADX WARN: Removed duplicated region for block: B:326:0x07e5  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x0810  */
    /* JADX WARN: Removed duplicated region for block: B:332:0x082d  */
    /* JADX WARN: Removed duplicated region for block: B:338:0x084b  */
    /* JADX WARN: Removed duplicated region for block: B:339:0x084d  */
    /* JADX WARN: Removed duplicated region for block: B:342:0x0899  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r54, final long r55, boolean r57, boolean r58, boolean r59, final int r60) {
        /*
            Method dump skipped, instructions count: 2401
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, boolean, boolean, int):int");
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x00ed  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00fc  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0104  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$sendMessage$14(final long r27, final int r29, boolean r30, boolean r31, androidx.collection.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, final org.telegram.messenger.MessageObject r35, final org.telegram.tgnet.TLRPC$Peer r36, final org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, final org.telegram.tgnet.TLRPC$TL_error r39) {
        /*
            Method dump skipped, instructions count: 557
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14(long, int, boolean, boolean, androidx.collection.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$sendMessage$9(final int i, final TLRPC$Message tLRPC$Message, final ArrayList arrayList, final MessageObject messageObject, final int i2) {
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        arrayList2.add(Integer.valueOf(i));
        getMessagesController().deleteMessages(arrayList2, null, null, tLRPC$Message.dialog_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendMessage$8(arrayList, messageObject, tLRPC$Message, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$sendMessage$8(ArrayList arrayList, final MessageObject messageObject, final TLRPC$Message tLRPC$Message, final int i, final int i2) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendMessage$7(messageObject, tLRPC$Message, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$sendMessage$7(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, int i2) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        boolean z = true;
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        if (i2 == 0) {
            z = false;
        }
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$sendMessage$11(final TLRPC$Message tLRPC$Message, TLRPC$Peer tLRPC$Peer, final int i, final int i2, ArrayList arrayList, final long j, final TLRPC$Message tLRPC$Message2, final int i3) {
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message.random_id, MessageObject.getPeerId(tLRPC$Peer), Integer.valueOf(i), tLRPC$Message.id, 0, false, i2 != 0 ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, i2 != 0);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendMessage$10(tLRPC$Message, j, i, tLRPC$Message2, i3, i2);
            }
        });
    }

    public /* synthetic */ void lambda$sendMessage$10(TLRPC$Message tLRPC$Message, long j, int i, TLRPC$Message tLRPC$Message2, int i2, int i3) {
        boolean z = false;
        tLRPC$Message.send_state = 0;
        getMediaDataController().increasePeerRaiting(j);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i4 = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[7];
        objArr[0] = Integer.valueOf(i);
        objArr[1] = Integer.valueOf(tLRPC$Message2.id);
        objArr[2] = tLRPC$Message2;
        objArr[3] = Long.valueOf(j);
        objArr[4] = 0L;
        objArr[5] = Integer.valueOf(i2);
        objArr[6] = Boolean.valueOf(i3 != 0);
        notificationCenter.postNotificationName(i4, objArr);
        processSentMessage(i);
        if (i3 != 0) {
            z = true;
        }
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$sendMessage$12(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_forwardMessages tLRPC$TL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, null, tLRPC$TL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$sendMessage$13(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 2;
        boolean z = true;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message.id));
        processSentMessage(tLRPC$Message.id);
        int i2 = tLRPC$Message.id;
        if (i == 0) {
            z = false;
        }
        removeFromSendingMessages(i2, z);
    }

    private void writePreviousMessageData(TLRPC$Message tLRPC$Message, SerializedData serializedData) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia == null) {
            new TLRPC$TL_messageMediaEmpty().serializeToStream(serializedData);
        } else {
            tLRPC$MessageMedia.serializeToStream(serializedData);
        }
        String str = tLRPC$Message.message;
        String str2 = "";
        if (str == null) {
            str = str2;
        }
        serializedData.writeString(str);
        String str3 = tLRPC$Message.attachPath;
        if (str3 != null) {
            str2 = str3;
        }
        serializedData.writeString(str2);
        int size = tLRPC$Message.entities.size();
        serializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            tLRPC$Message.entities.get(i).serializeToStream(serializedData);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:218:0x0486 A[Catch: Exception -> 0x057d, TryCatch #0 {Exception -> 0x057d, blocks: (B:9:0x0020, B:11:0x002b, B:13:0x003d, B:20:0x004e, B:23:0x0058, B:26:0x005d, B:28:0x0061, B:29:0x0069, B:31:0x006d, B:37:0x007d, B:40:0x0087, B:43:0x008d, B:45:0x0093, B:47:0x009a, B:48:0x00a7, B:50:0x00b9, B:51:0x00be, B:53:0x00e5, B:55:0x00f6, B:57:0x00fc, B:59:0x0102, B:60:0x0105, B:63:0x0129, B:70:0x0146, B:71:0x014f, B:73:0x0153, B:74:0x015f, B:76:0x0163, B:77:0x0167, B:82:0x0176, B:84:0x017e, B:85:0x0181, B:90:0x018a, B:92:0x018e, B:94:0x019e, B:96:0x01a2, B:97:0x01ab, B:99:0x01b1, B:100:0x01b8, B:102:0x01bc, B:103:0x01c5, B:105:0x01d8, B:107:0x01de, B:108:0x01e7, B:110:0x01ef, B:111:0x01f5, B:113:0x01fb, B:115:0x0221, B:117:0x0229, B:120:0x022e, B:121:0x0235, B:122:0x0238, B:125:0x0263, B:127:0x0269, B:141:0x028a, B:143:0x0291, B:145:0x0299, B:147:0x02aa, B:148:0x02c3, B:150:0x02d1, B:153:0x02dc, B:155:0x02f6, B:157:0x02fd, B:159:0x0311, B:161:0x0317, B:163:0x031d, B:164:0x0320, B:168:0x033a, B:170:0x0343, B:172:0x034b, B:174:0x035c, B:175:0x036d, B:176:0x0376, B:179:0x0388, B:183:0x0391, B:185:0x0398, B:187:0x03a0, B:190:0x03ad, B:192:0x03c9, B:194:0x03cf, B:196:0x03e9, B:198:0x03f6, B:199:0x03fa, B:202:0x0405, B:205:0x041d, B:207:0x0437, B:209:0x043e, B:211:0x0452, B:213:0x045f, B:214:0x0463, B:216:0x046f, B:218:0x0486, B:219:0x048e, B:221:0x0492, B:222:0x04a0, B:224:0x04a4, B:228:0x04b7, B:230:0x04bd, B:232:0x04c6, B:234:0x04da, B:236:0x04e0, B:237:0x04ea, B:239:0x04f0, B:242:0x04f5, B:246:0x050b, B:247:0x0510, B:251:0x0526, B:252:0x052b, B:255:0x053d, B:259:0x0551, B:260:0x0555, B:264:0x056a, B:265:0x056e), top: B:269:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:221:0x0492 A[Catch: Exception -> 0x057d, TryCatch #0 {Exception -> 0x057d, blocks: (B:9:0x0020, B:11:0x002b, B:13:0x003d, B:20:0x004e, B:23:0x0058, B:26:0x005d, B:28:0x0061, B:29:0x0069, B:31:0x006d, B:37:0x007d, B:40:0x0087, B:43:0x008d, B:45:0x0093, B:47:0x009a, B:48:0x00a7, B:50:0x00b9, B:51:0x00be, B:53:0x00e5, B:55:0x00f6, B:57:0x00fc, B:59:0x0102, B:60:0x0105, B:63:0x0129, B:70:0x0146, B:71:0x014f, B:73:0x0153, B:74:0x015f, B:76:0x0163, B:77:0x0167, B:82:0x0176, B:84:0x017e, B:85:0x0181, B:90:0x018a, B:92:0x018e, B:94:0x019e, B:96:0x01a2, B:97:0x01ab, B:99:0x01b1, B:100:0x01b8, B:102:0x01bc, B:103:0x01c5, B:105:0x01d8, B:107:0x01de, B:108:0x01e7, B:110:0x01ef, B:111:0x01f5, B:113:0x01fb, B:115:0x0221, B:117:0x0229, B:120:0x022e, B:121:0x0235, B:122:0x0238, B:125:0x0263, B:127:0x0269, B:141:0x028a, B:143:0x0291, B:145:0x0299, B:147:0x02aa, B:148:0x02c3, B:150:0x02d1, B:153:0x02dc, B:155:0x02f6, B:157:0x02fd, B:159:0x0311, B:161:0x0317, B:163:0x031d, B:164:0x0320, B:168:0x033a, B:170:0x0343, B:172:0x034b, B:174:0x035c, B:175:0x036d, B:176:0x0376, B:179:0x0388, B:183:0x0391, B:185:0x0398, B:187:0x03a0, B:190:0x03ad, B:192:0x03c9, B:194:0x03cf, B:196:0x03e9, B:198:0x03f6, B:199:0x03fa, B:202:0x0405, B:205:0x041d, B:207:0x0437, B:209:0x043e, B:211:0x0452, B:213:0x045f, B:214:0x0463, B:216:0x046f, B:218:0x0486, B:219:0x048e, B:221:0x0492, B:222:0x04a0, B:224:0x04a4, B:228:0x04b7, B:230:0x04bd, B:232:0x04c6, B:234:0x04da, B:236:0x04e0, B:237:0x04ea, B:239:0x04f0, B:242:0x04f5, B:246:0x050b, B:247:0x0510, B:251:0x0526, B:252:0x052b, B:255:0x053d, B:259:0x0551, B:260:0x0555, B:264:0x056a, B:265:0x056e), top: B:269:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:224:0x04a4 A[Catch: Exception -> 0x057d, TryCatch #0 {Exception -> 0x057d, blocks: (B:9:0x0020, B:11:0x002b, B:13:0x003d, B:20:0x004e, B:23:0x0058, B:26:0x005d, B:28:0x0061, B:29:0x0069, B:31:0x006d, B:37:0x007d, B:40:0x0087, B:43:0x008d, B:45:0x0093, B:47:0x009a, B:48:0x00a7, B:50:0x00b9, B:51:0x00be, B:53:0x00e5, B:55:0x00f6, B:57:0x00fc, B:59:0x0102, B:60:0x0105, B:63:0x0129, B:70:0x0146, B:71:0x014f, B:73:0x0153, B:74:0x015f, B:76:0x0163, B:77:0x0167, B:82:0x0176, B:84:0x017e, B:85:0x0181, B:90:0x018a, B:92:0x018e, B:94:0x019e, B:96:0x01a2, B:97:0x01ab, B:99:0x01b1, B:100:0x01b8, B:102:0x01bc, B:103:0x01c5, B:105:0x01d8, B:107:0x01de, B:108:0x01e7, B:110:0x01ef, B:111:0x01f5, B:113:0x01fb, B:115:0x0221, B:117:0x0229, B:120:0x022e, B:121:0x0235, B:122:0x0238, B:125:0x0263, B:127:0x0269, B:141:0x028a, B:143:0x0291, B:145:0x0299, B:147:0x02aa, B:148:0x02c3, B:150:0x02d1, B:153:0x02dc, B:155:0x02f6, B:157:0x02fd, B:159:0x0311, B:161:0x0317, B:163:0x031d, B:164:0x0320, B:168:0x033a, B:170:0x0343, B:172:0x034b, B:174:0x035c, B:175:0x036d, B:176:0x0376, B:179:0x0388, B:183:0x0391, B:185:0x0398, B:187:0x03a0, B:190:0x03ad, B:192:0x03c9, B:194:0x03cf, B:196:0x03e9, B:198:0x03f6, B:199:0x03fa, B:202:0x0405, B:205:0x041d, B:207:0x0437, B:209:0x043e, B:211:0x0452, B:213:0x045f, B:214:0x0463, B:216:0x046f, B:218:0x0486, B:219:0x048e, B:221:0x0492, B:222:0x04a0, B:224:0x04a4, B:228:0x04b7, B:230:0x04bd, B:232:0x04c6, B:234:0x04da, B:236:0x04e0, B:237:0x04ea, B:239:0x04f0, B:242:0x04f5, B:246:0x050b, B:247:0x0510, B:251:0x0526, B:252:0x052b, B:255:0x053d, B:259:0x0551, B:260:0x0555, B:264:0x056a, B:265:0x056e), top: B:269:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:239:0x04f0 A[Catch: Exception -> 0x057d, TryCatch #0 {Exception -> 0x057d, blocks: (B:9:0x0020, B:11:0x002b, B:13:0x003d, B:20:0x004e, B:23:0x0058, B:26:0x005d, B:28:0x0061, B:29:0x0069, B:31:0x006d, B:37:0x007d, B:40:0x0087, B:43:0x008d, B:45:0x0093, B:47:0x009a, B:48:0x00a7, B:50:0x00b9, B:51:0x00be, B:53:0x00e5, B:55:0x00f6, B:57:0x00fc, B:59:0x0102, B:60:0x0105, B:63:0x0129, B:70:0x0146, B:71:0x014f, B:73:0x0153, B:74:0x015f, B:76:0x0163, B:77:0x0167, B:82:0x0176, B:84:0x017e, B:85:0x0181, B:90:0x018a, B:92:0x018e, B:94:0x019e, B:96:0x01a2, B:97:0x01ab, B:99:0x01b1, B:100:0x01b8, B:102:0x01bc, B:103:0x01c5, B:105:0x01d8, B:107:0x01de, B:108:0x01e7, B:110:0x01ef, B:111:0x01f5, B:113:0x01fb, B:115:0x0221, B:117:0x0229, B:120:0x022e, B:121:0x0235, B:122:0x0238, B:125:0x0263, B:127:0x0269, B:141:0x028a, B:143:0x0291, B:145:0x0299, B:147:0x02aa, B:148:0x02c3, B:150:0x02d1, B:153:0x02dc, B:155:0x02f6, B:157:0x02fd, B:159:0x0311, B:161:0x0317, B:163:0x031d, B:164:0x0320, B:168:0x033a, B:170:0x0343, B:172:0x034b, B:174:0x035c, B:175:0x036d, B:176:0x0376, B:179:0x0388, B:183:0x0391, B:185:0x0398, B:187:0x03a0, B:190:0x03ad, B:192:0x03c9, B:194:0x03cf, B:196:0x03e9, B:198:0x03f6, B:199:0x03fa, B:202:0x0405, B:205:0x041d, B:207:0x0437, B:209:0x043e, B:211:0x0452, B:213:0x045f, B:214:0x0463, B:216:0x046f, B:218:0x0486, B:219:0x048e, B:221:0x0492, B:222:0x04a0, B:224:0x04a4, B:228:0x04b7, B:230:0x04bd, B:232:0x04c6, B:234:0x04da, B:236:0x04e0, B:237:0x04ea, B:239:0x04f0, B:242:0x04f5, B:246:0x050b, B:247:0x0510, B:251:0x0526, B:252:0x052b, B:255:0x053d, B:259:0x0551, B:260:0x0555, B:264:0x056a, B:265:0x056e), top: B:269:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:242:0x04f5 A[Catch: Exception -> 0x057d, TryCatch #0 {Exception -> 0x057d, blocks: (B:9:0x0020, B:11:0x002b, B:13:0x003d, B:20:0x004e, B:23:0x0058, B:26:0x005d, B:28:0x0061, B:29:0x0069, B:31:0x006d, B:37:0x007d, B:40:0x0087, B:43:0x008d, B:45:0x0093, B:47:0x009a, B:48:0x00a7, B:50:0x00b9, B:51:0x00be, B:53:0x00e5, B:55:0x00f6, B:57:0x00fc, B:59:0x0102, B:60:0x0105, B:63:0x0129, B:70:0x0146, B:71:0x014f, B:73:0x0153, B:74:0x015f, B:76:0x0163, B:77:0x0167, B:82:0x0176, B:84:0x017e, B:85:0x0181, B:90:0x018a, B:92:0x018e, B:94:0x019e, B:96:0x01a2, B:97:0x01ab, B:99:0x01b1, B:100:0x01b8, B:102:0x01bc, B:103:0x01c5, B:105:0x01d8, B:107:0x01de, B:108:0x01e7, B:110:0x01ef, B:111:0x01f5, B:113:0x01fb, B:115:0x0221, B:117:0x0229, B:120:0x022e, B:121:0x0235, B:122:0x0238, B:125:0x0263, B:127:0x0269, B:141:0x028a, B:143:0x0291, B:145:0x0299, B:147:0x02aa, B:148:0x02c3, B:150:0x02d1, B:153:0x02dc, B:155:0x02f6, B:157:0x02fd, B:159:0x0311, B:161:0x0317, B:163:0x031d, B:164:0x0320, B:168:0x033a, B:170:0x0343, B:172:0x034b, B:174:0x035c, B:175:0x036d, B:176:0x0376, B:179:0x0388, B:183:0x0391, B:185:0x0398, B:187:0x03a0, B:190:0x03ad, B:192:0x03c9, B:194:0x03cf, B:196:0x03e9, B:198:0x03f6, B:199:0x03fa, B:202:0x0405, B:205:0x041d, B:207:0x0437, B:209:0x043e, B:211:0x0452, B:213:0x045f, B:214:0x0463, B:216:0x046f, B:218:0x0486, B:219:0x048e, B:221:0x0492, B:222:0x04a0, B:224:0x04a4, B:228:0x04b7, B:230:0x04bd, B:232:0x04c6, B:234:0x04da, B:236:0x04e0, B:237:0x04ea, B:239:0x04f0, B:242:0x04f5, B:246:0x050b, B:247:0x0510, B:251:0x0526, B:252:0x052b, B:255:0x053d, B:259:0x0551, B:260:0x0555, B:264:0x056a, B:265:0x056e), top: B:269:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:243:0x0506  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0146 A[Catch: Exception -> 0x057d, TryCatch #0 {Exception -> 0x057d, blocks: (B:9:0x0020, B:11:0x002b, B:13:0x003d, B:20:0x004e, B:23:0x0058, B:26:0x005d, B:28:0x0061, B:29:0x0069, B:31:0x006d, B:37:0x007d, B:40:0x0087, B:43:0x008d, B:45:0x0093, B:47:0x009a, B:48:0x00a7, B:50:0x00b9, B:51:0x00be, B:53:0x00e5, B:55:0x00f6, B:57:0x00fc, B:59:0x0102, B:60:0x0105, B:63:0x0129, B:70:0x0146, B:71:0x014f, B:73:0x0153, B:74:0x015f, B:76:0x0163, B:77:0x0167, B:82:0x0176, B:84:0x017e, B:85:0x0181, B:90:0x018a, B:92:0x018e, B:94:0x019e, B:96:0x01a2, B:97:0x01ab, B:99:0x01b1, B:100:0x01b8, B:102:0x01bc, B:103:0x01c5, B:105:0x01d8, B:107:0x01de, B:108:0x01e7, B:110:0x01ef, B:111:0x01f5, B:113:0x01fb, B:115:0x0221, B:117:0x0229, B:120:0x022e, B:121:0x0235, B:122:0x0238, B:125:0x0263, B:127:0x0269, B:141:0x028a, B:143:0x0291, B:145:0x0299, B:147:0x02aa, B:148:0x02c3, B:150:0x02d1, B:153:0x02dc, B:155:0x02f6, B:157:0x02fd, B:159:0x0311, B:161:0x0317, B:163:0x031d, B:164:0x0320, B:168:0x033a, B:170:0x0343, B:172:0x034b, B:174:0x035c, B:175:0x036d, B:176:0x0376, B:179:0x0388, B:183:0x0391, B:185:0x0398, B:187:0x03a0, B:190:0x03ad, B:192:0x03c9, B:194:0x03cf, B:196:0x03e9, B:198:0x03f6, B:199:0x03fa, B:202:0x0405, B:205:0x041d, B:207:0x0437, B:209:0x043e, B:211:0x0452, B:213:0x045f, B:214:0x0463, B:216:0x046f, B:218:0x0486, B:219:0x048e, B:221:0x0492, B:222:0x04a0, B:224:0x04a4, B:228:0x04b7, B:230:0x04bd, B:232:0x04c6, B:234:0x04da, B:236:0x04e0, B:237:0x04ea, B:239:0x04f0, B:242:0x04f5, B:246:0x050b, B:247:0x0510, B:251:0x0526, B:252:0x052b, B:255:0x053d, B:259:0x0551, B:260:0x0555, B:264:0x056a, B:265:0x056e), top: B:269:0x0020 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void editMessage(org.telegram.messenger.MessageObject r25, org.telegram.tgnet.TLRPC$TL_photo r26, org.telegram.messenger.VideoEditedInfo r27, org.telegram.tgnet.TLRPC$TL_document r28, java.lang.String r29, java.util.HashMap<java.lang.String, java.lang.String> r30, boolean r31, java.lang.Object r32) {
        /*
            Method dump skipped, instructions count: 1413
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessage(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, final BaseFragment baseFragment, ArrayList<TLRPC$MessageEntity> arrayList, int i) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return 0;
        }
        final TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
        tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
        if (str != null) {
            tLRPC$TL_messages_editMessage.message = str;
            tLRPC$TL_messages_editMessage.flags |= 2048;
            tLRPC$TL_messages_editMessage.no_webpage = !z;
        }
        tLRPC$TL_messages_editMessage.id = messageObject.getId();
        if (arrayList != null) {
            tLRPC$TL_messages_editMessage.entities = arrayList;
            tLRPC$TL_messages_editMessage.flags |= 8;
        }
        if (i != 0) {
            tLRPC$TL_messages_editMessage.schedule_date = i;
            tLRPC$TL_messages_editMessage.flags |= 32768;
        }
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda88
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$editMessage$16(baseFragment, tLRPC$TL_messages_editMessage, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$editMessage$16(final BaseFragment baseFragment, final TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda60
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$editMessage$15(tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editMessage);
                }
            });
        }
    }

    public /* synthetic */ void lambda$editMessage$15(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editMessage, new Object[0]);
    }

    public void sendLocation(Location location) {
        TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
        TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
        tLRPC$TL_messageMediaGeo.geo = tLRPC$TL_geoPoint;
        tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        tLRPC$TL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Map.Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject value = entry.getValue();
            sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeo, value.getDialogId(), value, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        if (messageObject == null || tLRPC$KeyboardButton == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(tLRPC$KeyboardButton.data));
        sb.append("_");
        sb.append(tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame ? "1" : "0");
        this.waitingForLocation.put(sb.toString(), messageObject);
        this.locationProvider.start();
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        if (messageObject == null || tLRPC$KeyboardButton == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(tLRPC$KeyboardButton.data));
        sb.append("_");
        sb.append(tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame ? "1" : "0");
        return this.waitingForLocation.containsKey(sb.toString());
    }

    public void sendNotificationCallback(final long j, final int i, final byte[] bArr) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendNotificationCallback$19(j, i, bArr);
            }
        });
    }

    public /* synthetic */ void lambda$sendNotificationCallback$19(long j, int i, byte[] bArr) {
        TLRPC$Chat chatSync;
        TLRPC$User userSync;
        final String str = j + "_" + i + "_" + Utilities.bytesToHex(bArr) + "_0";
        this.waitingForCallback.put(str, Boolean.TRUE);
        if (DialogObject.isUserDialog(j)) {
            if (getMessagesController().getUser(Long.valueOf(j)) == null && (userSync = getMessagesStorage().getUserSync(j)) != null) {
                getMessagesController().putUser(userSync, true);
            }
        } else {
            long j2 = -j;
            if (getMessagesController().getChat(Long.valueOf(j2)) == null && (chatSync = getMessagesStorage().getChatSync(j2)) != null) {
                getMessagesController().putChat(chatSync, true);
            }
        }
        TLRPC$TL_messages_getBotCallbackAnswer tLRPC$TL_messages_getBotCallbackAnswer = new TLRPC$TL_messages_getBotCallbackAnswer();
        tLRPC$TL_messages_getBotCallbackAnswer.peer = getMessagesController().getInputPeer(j);
        tLRPC$TL_messages_getBotCallbackAnswer.msg_id = i;
        tLRPC$TL_messages_getBotCallbackAnswer.game = false;
        if (bArr != null) {
            tLRPC$TL_messages_getBotCallbackAnswer.flags |= 1;
            tLRPC$TL_messages_getBotCallbackAnswer.data = bArr;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getBotCallbackAnswer, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda81
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$sendNotificationCallback$18(str, tLObject, tLRPC$TL_error);
            }
        }, 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, 0, true, 0);
    }

    public /* synthetic */ void lambda$sendNotificationCallback$17(String str) {
        this.waitingForCallback.remove(str);
    }

    public /* synthetic */ void lambda$sendNotificationCallback$18(final String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendNotificationCallback$17(str);
            }
        });
    }

    public byte[] isSendingVote(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        return this.waitingForVote.get("poll_" + messageObject.getPollId());
    }

    public int sendVote(final MessageObject messageObject, ArrayList<TLRPC$TL_pollAnswer> arrayList, final Runnable runnable) {
        byte[] bArr;
        if (messageObject == null) {
            return 0;
        }
        final String str = "poll_" + messageObject.getPollId();
        if (this.waitingForCallback.containsKey(str)) {
            return 0;
        }
        TLRPC$TL_messages_sendVote tLRPC$TL_messages_sendVote = new TLRPC$TL_messages_sendVote();
        tLRPC$TL_messages_sendVote.msg_id = messageObject.getId();
        tLRPC$TL_messages_sendVote.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
        if (arrayList != null) {
            bArr = new byte[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = arrayList.get(i);
                tLRPC$TL_messages_sendVote.options.add(tLRPC$TL_pollAnswer.option);
                bArr[i] = tLRPC$TL_pollAnswer.option[0];
            }
        } else {
            bArr = new byte[0];
        }
        this.waitingForVote.put(str, bArr);
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_sendVote, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda84
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$sendVote$21(messageObject, str, runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendVote$21(MessageObject messageObject, final String str, final Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), 0L);
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.elapsedRealtime()));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendVote$20(str, runnable);
            }
        });
    }

    public /* synthetic */ void lambda$sendVote$20(String str, Runnable runnable) {
        this.waitingForVote.remove(str);
        if (runnable != null) {
            runnable.run();
        }
    }

    public long getVoteSendTime(long j) {
        return this.voteSendTime.get(j, 0L).longValue();
    }

    public void sendReaction(MessageObject messageObject, CharSequence charSequence, boolean z, ChatActivity chatActivity, final Runnable runnable) {
        if (messageObject == null || chatActivity == null) {
            return;
        }
        TLRPC$TL_messages_sendReaction tLRPC$TL_messages_sendReaction = new TLRPC$TL_messages_sendReaction();
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        if (tLRPC$Message.isThreadMessage && tLRPC$Message.fwd_from != null) {
            tLRPC$TL_messages_sendReaction.peer = getMessagesController().getInputPeer(messageObject.getFromChatId());
            tLRPC$TL_messages_sendReaction.msg_id = messageObject.messageOwner.fwd_from.saved_from_msg_id;
        } else {
            tLRPC$TL_messages_sendReaction.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
            tLRPC$TL_messages_sendReaction.msg_id = messageObject.getId();
        }
        if (charSequence != null) {
            tLRPC$TL_messages_sendReaction.reaction = charSequence.toString();
            tLRPC$TL_messages_sendReaction.flags |= 1;
        }
        if (z) {
            tLRPC$TL_messages_sendReaction.flags |= 2;
            tLRPC$TL_messages_sendReaction.big = true;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_sendReaction, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda80
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$sendReaction$22(runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendReaction$22(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            if (runnable == null) {
                return;
            }
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void requestUrlAuth(final String str, final ChatActivity chatActivity, final boolean z) {
        final TLRPC$TL_messages_requestUrlAuth tLRPC$TL_messages_requestUrlAuth = new TLRPC$TL_messages_requestUrlAuth();
        tLRPC$TL_messages_requestUrlAuth.url = str;
        tLRPC$TL_messages_requestUrlAuth.flags |= 4;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_requestUrlAuth, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda90
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.lambda$requestUrlAuth$23(ChatActivity.this, tLRPC$TL_messages_requestUrlAuth, str, z, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    public static /* synthetic */ void lambda$requestUrlAuth$23(ChatActivity chatActivity, TLRPC$TL_messages_requestUrlAuth tLRPC$TL_messages_requestUrlAuth, String str, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            if (tLObject instanceof TLRPC$TL_urlAuthResultRequest) {
                chatActivity.showRequestUrlAlert((TLRPC$TL_urlAuthResultRequest) tLObject, tLRPC$TL_messages_requestUrlAuth, str, z);
                return;
            } else if (tLObject instanceof TLRPC$TL_urlAuthResultAccepted) {
                AlertsCreator.showOpenUrlAlert(chatActivity, ((TLRPC$TL_urlAuthResultAccepted) tLObject).url, false, false);
                return;
            } else if (!(tLObject instanceof TLRPC$TL_urlAuthResultDefault)) {
                return;
            } else {
                AlertsCreator.showOpenUrlAlert(chatActivity, str, false, z);
                return;
            }
        }
        AlertsCreator.showOpenUrlAlert(chatActivity, str, false, z);
    }

    public void sendCallback(boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        lambda$sendCallback$24(z, messageObject, tLRPC$KeyboardButton, null, null, chatActivity);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0088  */
    /* renamed from: sendCallback */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void lambda$sendCallback$24(final boolean r19, final org.telegram.messenger.MessageObject r20, final org.telegram.tgnet.TLRPC$KeyboardButton r21, final org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r22, final org.telegram.ui.TwoStepVerificationActivity r23, final org.telegram.ui.ChatActivity r24) {
        /*
            Method dump skipped, instructions count: 443
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendCallback$24(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, org.telegram.ui.TwoStepVerificationActivity, org.telegram.ui.ChatActivity):void");
    }

    public /* synthetic */ void lambda$sendCallback$30(final String str, final boolean z, final MessageObject messageObject, final TLRPC$KeyboardButton tLRPC$KeyboardButton, final ChatActivity chatActivity, final TwoStepVerificationActivity twoStepVerificationActivity, final TLObject[] tLObjectArr, final TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, final boolean z2, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendCallback$29(str, z, tLObject, messageObject, tLRPC$KeyboardButton, chatActivity, twoStepVerificationActivity, tLObjectArr, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, z2);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:80:0x0168, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r27.currentAccount).getBoolean("askgame_" + r12, true) != false) goto L82;
     */
    /* JADX WARN: Removed duplicated region for block: B:23:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0073  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00a1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$sendCallback$29(java.lang.String r28, boolean r29, org.telegram.tgnet.TLObject r30, final org.telegram.messenger.MessageObject r31, final org.telegram.tgnet.TLRPC$KeyboardButton r32, final org.telegram.ui.ChatActivity r33, final org.telegram.ui.TwoStepVerificationActivity r34, org.telegram.tgnet.TLObject[] r35, org.telegram.tgnet.TLRPC$TL_error r36, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r37, final boolean r38) {
        /*
            Method dump skipped, instructions count: 1182
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendCallback$29(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.ui.TwoStepVerificationActivity, org.telegram.tgnet.TLObject[], org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, boolean):void");
    }

    public /* synthetic */ void lambda$sendCallback$25(final boolean z, final MessageObject messageObject, final TLRPC$KeyboardButton tLRPC$KeyboardButton, final ChatActivity chatActivity, DialogInterface dialogInterface, int i) {
        final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda91
            @Override // org.telegram.ui.TwoStepVerificationActivity.TwoStepVerificationActivityDelegate
            public final void didEnterPassword(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP) {
                SendMessagesHelper.this.lambda$sendCallback$24(z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity, chatActivity, tLRPC$InputCheckPasswordSRP);
            }
        });
        chatActivity.presentFragment(twoStepVerificationActivity);
    }

    public static /* synthetic */ void lambda$sendCallback$26(ChatActivity chatActivity, DialogInterface dialogInterface, int i) {
        chatActivity.presentFragment(new TwoStepVerificationSetupActivity(6, null));
    }

    public /* synthetic */ void lambda$sendCallback$28(final TwoStepVerificationActivity twoStepVerificationActivity, final boolean z, final MessageObject messageObject, final TLRPC$KeyboardButton tLRPC$KeyboardButton, final ChatActivity chatActivity, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda57
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$sendCallback$27(tLRPC$TL_error, tLObject, twoStepVerificationActivity, z, messageObject, tLRPC$KeyboardButton, chatActivity);
            }
        });
    }

    public /* synthetic */ void lambda$sendCallback$27(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo(null, tLRPC$TL_account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            lambda$sendCallback$24(z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity, chatActivity);
        }
    }

    public boolean isSendingCallback(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        int i = 0;
        if (messageObject == null || tLRPC$KeyboardButton == null) {
            return false;
        }
        if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonUrlAuth) {
            i = 3;
        } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame) {
            i = 1;
        } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonBuy) {
            i = 2;
        }
        return this.waitingForCallback.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(tLRPC$KeyboardButton.data) + "_" + i);
    }

    public void sendGame(TLRPC$InputPeer tLRPC$InputPeer, TLRPC$TL_inputMediaGame tLRPC$TL_inputMediaGame, long j, final long j2) {
        NativeByteBuffer nativeByteBuffer;
        Exception e;
        if (tLRPC$InputPeer == null || tLRPC$TL_inputMediaGame == null) {
            return;
        }
        TLRPC$TL_messages_sendMedia tLRPC$TL_messages_sendMedia = new TLRPC$TL_messages_sendMedia();
        tLRPC$TL_messages_sendMedia.peer = tLRPC$InputPeer;
        if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChannel) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            tLRPC$TL_messages_sendMedia.silent = notificationsSettings.getBoolean("silent_" + (-tLRPC$InputPeer.channel_id), false);
        } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
            SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
            tLRPC$TL_messages_sendMedia.silent = notificationsSettings2.getBoolean("silent_" + (-tLRPC$InputPeer.chat_id), false);
        } else {
            SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(this.currentAccount);
            tLRPC$TL_messages_sendMedia.silent = notificationsSettings3.getBoolean("silent_" + tLRPC$InputPeer.user_id, false);
        }
        tLRPC$TL_messages_sendMedia.random_id = j != 0 ? j : getNextRandomId();
        tLRPC$TL_messages_sendMedia.message = "";
        tLRPC$TL_messages_sendMedia.media = tLRPC$TL_inputMediaGame;
        long sendAsPeerId = ChatObject.getSendAsPeerId(getMessagesController().getChat(Long.valueOf(tLRPC$InputPeer.chat_id)), getMessagesController().getChatFull(tLRPC$InputPeer.chat_id));
        if (sendAsPeerId != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            tLRPC$TL_messages_sendMedia.send_as = getMessagesController().getInputPeer(sendAsPeerId);
        }
        if (j2 == 0) {
            NativeByteBuffer nativeByteBuffer2 = null;
            try {
                nativeByteBuffer = new NativeByteBuffer(tLRPC$InputPeer.getObjectSize() + tLRPC$TL_inputMediaGame.getObjectSize() + 4 + 8);
                try {
                    nativeByteBuffer.writeInt32(3);
                    nativeByteBuffer.writeInt64(j);
                    tLRPC$InputPeer.serializeToStream(nativeByteBuffer);
                    tLRPC$TL_inputMediaGame.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(e);
                    nativeByteBuffer = nativeByteBuffer2;
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda78
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            SendMessagesHelper.this.lambda$sendGame$31(j2, tLObject, tLRPC$TL_error);
                        }
                    });
                }
            } catch (Exception e3) {
                e = e3;
            }
            j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda78
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$sendGame$31(j2, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendGame$31(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void sendMessage(MessageObject messageObject) {
        long dialogId = messageObject.getDialogId();
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        sendMessage(null, null, null, null, null, null, null, null, null, null, dialogId, tLRPC$Message.attachPath, null, null, null, true, messageObject, null, tLRPC$Message.reply_markup, tLRPC$Message.params, !tLRPC$Message.silent, messageObject.scheduled ? tLRPC$Message.date : 0, 0, null, null);
    }

    public void sendMessage(TLRPC$User tLRPC$User, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, tLRPC$User, null, null, null, null, j, null, messageObject, messageObject2, null, true, null, null, tLRPC$ReplyMarkup, hashMap, z, i, 0, null, null);
    }

    public void sendMessage(TLRPC$TL_messageMediaInvoice tLRPC$TL_messageMediaInvoice, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, null, null, null, null, tLRPC$TL_messageMediaInvoice, j, null, messageObject, messageObject2, null, true, null, null, tLRPC$ReplyMarkup, hashMap, z, i, 0, null, null);
    }

    public void sendMessage(TLRPC$TL_document tLRPC$TL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, MessageObject messageObject2, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        sendMessage(null, str2, null, null, videoEditedInfo, null, tLRPC$TL_document, null, null, null, j, str, messageObject, messageObject2, null, true, null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj, sendAnimationData);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$WebPage tLRPC$WebPage, boolean z, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z2, int i, MessageObject.SendAnimationData sendAnimationData) {
        sendMessage(str, null, null, null, null, null, null, null, null, null, j, null, messageObject, messageObject2, tLRPC$WebPage, z, null, arrayList, tLRPC$ReplyMarkup, hashMap, z2, i, 0, null, sendAnimationData);
    }

    public void sendMessage(TLRPC$MessageMedia tLRPC$MessageMedia, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, tLRPC$MessageMedia, null, null, null, null, null, null, null, j, null, messageObject, messageObject2, null, true, null, null, tLRPC$ReplyMarkup, hashMap, z, i, 0, null, null);
    }

    public void sendMessage(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, null, null, null, tLRPC$TL_messageMediaPoll, null, j, null, messageObject, messageObject2, null, true, null, null, tLRPC$ReplyMarkup, hashMap, z, i, 0, null, null);
    }

    public void sendMessage(TLRPC$TL_game tLRPC$TL_game, long j, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, null, null, tLRPC$TL_game, null, null, j, null, null, null, null, true, null, null, tLRPC$ReplyMarkup, hashMap, z, i, 0, null, null);
    }

    public void sendMessage(TLRPC$TL_photo tLRPC$TL_photo, String str, long j, MessageObject messageObject, MessageObject messageObject2, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        sendMessage(null, str2, null, tLRPC$TL_photo, null, null, null, null, null, null, j, str, messageObject, messageObject2, null, true, null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj, null);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(8:866|(7:(1:877)(3:870|871|(7:873|1452|897|898|(2:900|901)(6:904|905|906|(1:908)|(1:912)|913)|(3:(5:916|917|(1:919)(1:920)|1399|921)(1:924)|925|(2:927|(1:929)))(1:931)|932))|1452|897|898|(0)(0)|(0)(0)|932)|1435|878|(1:880)|(2:889|890)|(1:895)(1:894)|896) */
    /* JADX WARN: Code restructure failed: missing block: B:1145:0x16f9, code lost:
        r11 = r66;
        r3 = new org.telegram.messenger.SendMessagesHelper.DelayedMessage(r11);
        r3.encryptedChat = r8;
        r3.type = 1;
        r3.sendEncryptedRequest = r6;
        r5 = r50;
        r3.originalPath = r5;
        r3.obj = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1146:0x170d, code lost:
        if (r64 == null) goto L1150;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1148:0x1715, code lost:
        if (r64.containsKey(r44) == false) goto L1150;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1149:0x1717, code lost:
        r3.parentObject = r64.get(r44);
     */
    /* JADX WARN: Code restructure failed: missing block: B:1150:0x171e, code lost:
        r3.parentObject = r63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1151:0x1722, code lost:
        r3.performMediaUpload = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1152:0x1725, code lost:
        if (r78 == 0) goto L1154;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1153:0x1727, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1154:0x1729, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1155:0x172a, code lost:
        r3.scheduled = r1;
        r1 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1218:0x190e, code lost:
        r3 = new org.telegram.messenger.SendMessagesHelper.DelayedMessage(r11);
        r3.encryptedChat = r8;
        r3.type = 2;
        r3.sendEncryptedRequest = r6;
        r3.originalPath = r5;
        r3.obj = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1219:0x191e, code lost:
        if (r64 == null) goto L1223;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1221:0x1924, code lost:
        if (r64.containsKey(r44) == false) goto L1223;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1222:0x1926, code lost:
        r3.parentObject = r64.get(r44);
     */
    /* JADX WARN: Code restructure failed: missing block: B:1223:0x192d, code lost:
        r3.parentObject = r63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1224:0x1931, code lost:
        r3.performMediaUpload = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1225:0x1934, code lost:
        if (r78 == 0) goto L1227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1226:0x1936, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1227:0x1938, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1228:0x1939, code lost:
        r3.scheduled = r1;
        r1 = r3;
        r3 = r68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:247:0x050b, code lost:
        if (r6.containsKey(r5) != false) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x052c, code lost:
        if (r6.containsKey(r5) != false) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:259:0x052e, code lost:
        r22 = r22;
        r32 = r5;
        r7 = r18;
        r2 = null;
        r14 = false;
        r19 = '\t';
     */
    /* JADX WARN: Code restructure failed: missing block: B:741:0x0e9d, code lost:
        if (r14.roundVideo == false) goto L744;
     */
    /* JADX WARN: Code restructure failed: missing block: B:937:0x124d, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:938:0x124e, code lost:
        r18 = r18;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1076:0x1522  */
    /* JADX WARN: Removed duplicated region for block: B:1134:0x1690 A[Catch: Exception -> 0x1ad7, TryCatch #31 {Exception -> 0x1ad7, blocks: (B:1077:0x1539, B:1079:0x1543, B:1081:0x154c, B:1083:0x1560, B:1084:0x1569, B:1086:0x156f, B:1088:0x1575, B:1089:0x157d, B:1091:0x1583, B:1093:0x1589, B:1094:0x1592, B:1096:0x15a0, B:1098:0x15a8, B:1099:0x15b6, B:1101:0x15bf, B:1103:0x15c3, B:1104:0x15db, B:1105:0x15e2, B:1114:0x162e, B:1116:0x163d, B:1119:0x1644, B:1121:0x164d, B:1123:0x1651, B:1124:0x1656, B:1125:0x165e, B:1127:0x166b, B:1129:0x166f, B:1130:0x1674, B:1131:0x167b, B:1132:0x1688, B:1134:0x1690, B:1136:0x169c, B:1137:0x16ab, B:1138:0x16ae, B:1143:0x16c1, B:1145:0x16f9, B:1147:0x170f, B:1149:0x1717, B:1150:0x171e, B:1151:0x1722, B:1155:0x172a, B:1157:0x1734, B:1159:0x173c, B:1163:0x174f, B:1172:0x1791, B:1176:0x17a9, B:1178:0x17c0, B:1179:0x17d5, B:1180:0x17e3, B:1187:0x1802, B:1189:0x1808, B:1191:0x180f, B:1193:0x183a, B:1194:0x1841, B:1196:0x1858, B:1198:0x185f, B:1200:0x1869, B:1287:0x1a88, B:1292:0x1aa7), top: B:1443:0x1539 }] */
    /* JADX WARN: Removed duplicated region for block: B:1159:0x173c A[Catch: Exception -> 0x1ad7, TryCatch #31 {Exception -> 0x1ad7, blocks: (B:1077:0x1539, B:1079:0x1543, B:1081:0x154c, B:1083:0x1560, B:1084:0x1569, B:1086:0x156f, B:1088:0x1575, B:1089:0x157d, B:1091:0x1583, B:1093:0x1589, B:1094:0x1592, B:1096:0x15a0, B:1098:0x15a8, B:1099:0x15b6, B:1101:0x15bf, B:1103:0x15c3, B:1104:0x15db, B:1105:0x15e2, B:1114:0x162e, B:1116:0x163d, B:1119:0x1644, B:1121:0x164d, B:1123:0x1651, B:1124:0x1656, B:1125:0x165e, B:1127:0x166b, B:1129:0x166f, B:1130:0x1674, B:1131:0x167b, B:1132:0x1688, B:1134:0x1690, B:1136:0x169c, B:1137:0x16ab, B:1138:0x16ae, B:1143:0x16c1, B:1145:0x16f9, B:1147:0x170f, B:1149:0x1717, B:1150:0x171e, B:1151:0x1722, B:1155:0x172a, B:1157:0x1734, B:1159:0x173c, B:1163:0x174f, B:1172:0x1791, B:1176:0x17a9, B:1178:0x17c0, B:1179:0x17d5, B:1180:0x17e3, B:1187:0x1802, B:1189:0x1808, B:1191:0x180f, B:1193:0x183a, B:1194:0x1841, B:1196:0x1858, B:1198:0x185f, B:1200:0x1869, B:1287:0x1a88, B:1292:0x1aa7), top: B:1443:0x1539 }] */
    /* JADX WARN: Removed duplicated region for block: B:1263:0x1a37  */
    /* JADX WARN: Removed duplicated region for block: B:1264:0x1a39  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x02b8 A[Catch: Exception -> 0x032a, TryCatch #1 {Exception -> 0x032a, blocks: (B:52:0x0122, B:54:0x0128, B:56:0x012f, B:59:0x0137, B:62:0x013f, B:64:0x0145, B:66:0x0161, B:68:0x0167, B:71:0x016f, B:73:0x0176, B:76:0x0188, B:85:0x01ad, B:90:0x01b7, B:104:0x020e, B:110:0x0234, B:112:0x024f, B:116:0x0270, B:121:0x0299, B:124:0x02a8, B:127:0x02b8, B:131:0x02c5, B:191:0x0412, B:200:0x043a, B:202:0x043e), top: B:1393:0x011a }] */
    /* JADX WARN: Removed duplicated region for block: B:1295:0x1ab8  */
    /* JADX WARN: Removed duplicated region for block: B:1298:0x1abe A[Catch: Exception -> 0x1c0d, TRY_ENTER, TryCatch #25 {Exception -> 0x1c0d, blocks: (B:1298:0x1abe, B:1300:0x1ac4, B:1302:0x1aca, B:1309:0x1aec, B:1313:0x1af8, B:1315:0x1b08, B:1316:0x1b14, B:1318:0x1b18, B:1320:0x1b1c, B:1322:0x1b26, B:1324:0x1b2b, B:1326:0x1b31, B:1328:0x1b3c, B:1333:0x1b4b, B:1335:0x1b60, B:1337:0x1b66, B:1339:0x1b6c, B:1340:0x1b72, B:1342:0x1b7d, B:1344:0x1b83, B:1345:0x1b8b, B:1347:0x1b8f, B:1349:0x1b95, B:1351:0x1ba0, B:1353:0x1ba6, B:1354:0x1bb4, B:1356:0x1bc2, B:1358:0x1bc6, B:1359:0x1bd8, B:1360:0x1bdf, B:1362:0x1bfa, B:1364:0x1c00, B:1366:0x1c06), top: B:1434:0x0c38 }] */
    /* JADX WARN: Removed duplicated region for block: B:1309:0x1aec A[Catch: Exception -> 0x1c0d, TryCatch #25 {Exception -> 0x1c0d, blocks: (B:1298:0x1abe, B:1300:0x1ac4, B:1302:0x1aca, B:1309:0x1aec, B:1313:0x1af8, B:1315:0x1b08, B:1316:0x1b14, B:1318:0x1b18, B:1320:0x1b1c, B:1322:0x1b26, B:1324:0x1b2b, B:1326:0x1b31, B:1328:0x1b3c, B:1333:0x1b4b, B:1335:0x1b60, B:1337:0x1b66, B:1339:0x1b6c, B:1340:0x1b72, B:1342:0x1b7d, B:1344:0x1b83, B:1345:0x1b8b, B:1347:0x1b8f, B:1349:0x1b95, B:1351:0x1ba0, B:1353:0x1ba6, B:1354:0x1bb4, B:1356:0x1bc2, B:1358:0x1bc6, B:1359:0x1bd8, B:1360:0x1bdf, B:1362:0x1bfa, B:1364:0x1c00, B:1366:0x1c06), top: B:1434:0x0c38 }] */
    /* JADX WARN: Removed duplicated region for block: B:133:0x02cb  */
    /* JADX WARN: Removed duplicated region for block: B:1340:0x1b72 A[Catch: Exception -> 0x1c0d, TryCatch #25 {Exception -> 0x1c0d, blocks: (B:1298:0x1abe, B:1300:0x1ac4, B:1302:0x1aca, B:1309:0x1aec, B:1313:0x1af8, B:1315:0x1b08, B:1316:0x1b14, B:1318:0x1b18, B:1320:0x1b1c, B:1322:0x1b26, B:1324:0x1b2b, B:1326:0x1b31, B:1328:0x1b3c, B:1333:0x1b4b, B:1335:0x1b60, B:1337:0x1b66, B:1339:0x1b6c, B:1340:0x1b72, B:1342:0x1b7d, B:1344:0x1b83, B:1345:0x1b8b, B:1347:0x1b8f, B:1349:0x1b95, B:1351:0x1ba0, B:1353:0x1ba6, B:1354:0x1bb4, B:1356:0x1bc2, B:1358:0x1bc6, B:1359:0x1bd8, B:1360:0x1bdf, B:1362:0x1bfa, B:1364:0x1c00, B:1366:0x1c06), top: B:1434:0x0c38 }] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x02cd  */
    /* JADX WARN: Removed duplicated region for block: B:1384:0x1c31  */
    /* JADX WARN: Removed duplicated region for block: B:1385:0x1c33  */
    /* JADX WARN: Removed duplicated region for block: B:1388:0x1c39  */
    /* JADX WARN: Removed duplicated region for block: B:1391:0x0b19 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1401:0x1a71 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1406:0x0899 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1438:0x0ec3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0336  */
    /* JADX WARN: Removed duplicated region for block: B:1440:0x077a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1454:0x0aed A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1470:0x16ae A[EDGE_INSN: B:1470:0x16ae->B:1138:0x16ae ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:177:0x03d1 A[Catch: Exception -> 0x037d, TryCatch #36 {Exception -> 0x037d, blocks: (B:153:0x0368, B:155:0x036c, B:157:0x0370, B:162:0x0385, B:167:0x0393, B:169:0x0399, B:173:0x03b2, B:177:0x03d1, B:178:0x03d9, B:180:0x03e4, B:184:0x03ef), top: B:1449:0x0368 }] */
    /* JADX WARN: Removed duplicated region for block: B:178:0x03d9 A[Catch: Exception -> 0x037d, TryCatch #36 {Exception -> 0x037d, blocks: (B:153:0x0368, B:155:0x036c, B:157:0x0370, B:162:0x0385, B:167:0x0393, B:169:0x0399, B:173:0x03b2, B:177:0x03d1, B:178:0x03d9, B:180:0x03e4, B:184:0x03ef), top: B:1449:0x0368 }] */
    /* JADX WARN: Removed duplicated region for block: B:386:0x0792 A[Catch: Exception -> 0x0789, TryCatch #29 {Exception -> 0x0789, blocks: (B:380:0x077a, B:382:0x0780, B:386:0x0792, B:387:0x0795, B:389:0x0799, B:390:0x079b, B:392:0x079f, B:393:0x07a1, B:396:0x07b4, B:399:0x07d2, B:401:0x07d9, B:402:0x07ea), top: B:1440:0x077a }] */
    /* JADX WARN: Removed duplicated region for block: B:387:0x0795 A[Catch: Exception -> 0x0789, TryCatch #29 {Exception -> 0x0789, blocks: (B:380:0x077a, B:382:0x0780, B:386:0x0792, B:387:0x0795, B:389:0x0799, B:390:0x079b, B:392:0x079f, B:393:0x07a1, B:396:0x07b4, B:399:0x07d2, B:401:0x07d9, B:402:0x07ea), top: B:1440:0x077a }] */
    /* JADX WARN: Removed duplicated region for block: B:392:0x079f A[Catch: Exception -> 0x0789, TryCatch #29 {Exception -> 0x0789, blocks: (B:380:0x077a, B:382:0x0780, B:386:0x0792, B:387:0x0795, B:389:0x0799, B:390:0x079b, B:392:0x079f, B:393:0x07a1, B:396:0x07b4, B:399:0x07d2, B:401:0x07d9, B:402:0x07ea), top: B:1440:0x077a }] */
    /* JADX WARN: Removed duplicated region for block: B:395:0x07b2 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:399:0x07d2 A[Catch: Exception -> 0x0789, TryCatch #29 {Exception -> 0x0789, blocks: (B:380:0x077a, B:382:0x0780, B:386:0x0792, B:387:0x0795, B:389:0x0799, B:390:0x079b, B:392:0x079f, B:393:0x07a1, B:396:0x07b4, B:399:0x07d2, B:401:0x07d9, B:402:0x07ea), top: B:1440:0x077a }] */
    /* JADX WARN: Removed duplicated region for block: B:401:0x07d9 A[Catch: Exception -> 0x0789, TryCatch #29 {Exception -> 0x0789, blocks: (B:380:0x077a, B:382:0x0780, B:386:0x0792, B:387:0x0795, B:389:0x0799, B:390:0x079b, B:392:0x079f, B:393:0x07a1, B:396:0x07b4, B:399:0x07d2, B:401:0x07d9, B:402:0x07ea), top: B:1440:0x077a }] */
    /* JADX WARN: Removed duplicated region for block: B:405:0x0812  */
    /* JADX WARN: Removed duplicated region for block: B:414:0x083e  */
    /* JADX WARN: Removed duplicated region for block: B:418:0x084b A[Catch: Exception -> 0x0837, TRY_LEAVE, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:421:0x0855 A[Catch: Exception -> 0x0837, TRY_ENTER, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:424:0x085f A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:427:0x086c A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:431:0x088b A[Catch: Exception -> 0x0837, TRY_LEAVE, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:436:0x0897  */
    /* JADX WARN: Removed duplicated region for block: B:440:0x08a9  */
    /* JADX WARN: Removed duplicated region for block: B:454:0x08e6 A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:457:0x08f9 A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:468:0x0942  */
    /* JADX WARN: Removed duplicated region for block: B:471:0x0950 A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:474:0x096c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:478:0x097c A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:489:0x09ac A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:49:0x011c A[Catch: Exception -> 0x0331, TRY_ENTER, TRY_LEAVE, TryCatch #8 {Exception -> 0x0331, blocks: (B:49:0x011c, B:146:0x0344, B:150:0x035b, B:151:0x0361, B:189:0x0407, B:190:0x040d, B:198:0x042f, B:199:0x0435, B:210:0x0460), top: B:1393:0x011a }] */
    /* JADX WARN: Removed duplicated region for block: B:507:0x09e9  */
    /* JADX WARN: Removed duplicated region for block: B:516:0x0a19 A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:531:0x0a59 A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:536:0x0a66 A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:539:0x0a6f A[Catch: Exception -> 0x0837, TryCatch #4 {Exception -> 0x0837, blocks: (B:406:0x0814, B:416:0x0843, B:418:0x084b, B:421:0x0855, B:424:0x085f, B:426:0x0869, B:427:0x086c, B:428:0x087f, B:429:0x0885, B:431:0x088b, B:446:0x08bb, B:448:0x08cd, B:450:0x08d1, B:451:0x08d5, B:453:0x08dc, B:454:0x08e6, B:455:0x08eb, B:457:0x08f9, B:459:0x0902, B:461:0x090e, B:463:0x091c, B:464:0x0923, B:467:0x0931, B:471:0x0950, B:475:0x096e, B:476:0x0976, B:478:0x097c, B:480:0x098c, B:482:0x099a, B:484:0x09a0, B:486:0x09a4, B:489:0x09ac, B:491:0x09ba, B:492:0x09bf, B:494:0x09c3, B:496:0x09c7, B:498:0x09cd, B:500:0x09d1, B:501:0x09d9, B:503:0x09dd, B:505:0x09e3, B:508:0x09ea, B:510:0x09f6, B:512:0x0a06, B:513:0x0a09, B:515:0x0a0e, B:516:0x0a19, B:518:0x0a1f, B:521:0x0a26, B:523:0x0a32, B:525:0x0a42, B:526:0x0a45, B:528:0x0a49, B:529:0x0a53, B:531:0x0a59, B:533:0x0a5f, B:534:0x0a62, B:536:0x0a66, B:537:0x0a6a, B:539:0x0a6f, B:541:0x0a79, B:543:0x0a8e, B:549:0x0aa0), top: B:1398:0x0814 }] */
    /* JADX WARN: Removed duplicated region for block: B:548:0x0a9c  */
    /* JADX WARN: Removed duplicated region for block: B:554:0x0ae2  */
    /* JADX WARN: Removed duplicated region for block: B:555:0x0ae4  */
    /* JADX WARN: Removed duplicated region for block: B:571:0x0b09  */
    /* JADX WARN: Removed duplicated region for block: B:574:0x0b0e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:597:0x0b74  */
    /* JADX WARN: Removed duplicated region for block: B:615:0x0bc8  */
    /* JADX WARN: Removed duplicated region for block: B:619:0x0c32  */
    /* JADX WARN: Removed duplicated region for block: B:622:0x0c3a  */
    /* JADX WARN: Removed duplicated region for block: B:648:0x0c6c A[Catch: Exception -> 0x0b9d, TryCatch #12 {Exception -> 0x0b9d, blocks: (B:590:0x0b60, B:600:0x0b94, B:604:0x0ba1, B:608:0x0bb0, B:612:0x0bc0, B:617:0x0bcc, B:648:0x0c6c, B:650:0x0c7b, B:652:0x0c83, B:654:0x0ca8, B:656:0x0cbb, B:657:0x0ccb, B:658:0x0cd3, B:659:0x0cda, B:660:0x0ce4, B:661:0x0ceb, B:663:0x0cf1, B:664:0x0cf9, B:666:0x0d0a, B:667:0x0d18, B:669:0x0d1e, B:670:0x0d28, B:672:0x0d2c, B:677:0x0d3e, B:680:0x0d57, B:682:0x0d66, B:683:0x0d72, B:687:0x0d7d, B:689:0x0d83, B:691:0x0d87, B:692:0x0d8f, B:694:0x0d95, B:695:0x0d9d, B:697:0x0dbb, B:699:0x0dc4, B:701:0x0dca, B:706:0x0dd5, B:710:0x0def, B:712:0x0df5, B:713:0x0e0f, B:715:0x0e13, B:717:0x0e26, B:718:0x0e2b, B:720:0x0e2f, B:721:0x0e39, B:722:0x0e3e, B:735:0x0e8d, B:738:0x0e95, B:740:0x0e9b, B:747:0x0eac, B:749:0x0eb3), top: B:1410:0x0b17 }] */
    /* JADX WARN: Removed duplicated region for block: B:663:0x0cf1 A[Catch: Exception -> 0x0b9d, TryCatch #12 {Exception -> 0x0b9d, blocks: (B:590:0x0b60, B:600:0x0b94, B:604:0x0ba1, B:608:0x0bb0, B:612:0x0bc0, B:617:0x0bcc, B:648:0x0c6c, B:650:0x0c7b, B:652:0x0c83, B:654:0x0ca8, B:656:0x0cbb, B:657:0x0ccb, B:658:0x0cd3, B:659:0x0cda, B:660:0x0ce4, B:661:0x0ceb, B:663:0x0cf1, B:664:0x0cf9, B:666:0x0d0a, B:667:0x0d18, B:669:0x0d1e, B:670:0x0d28, B:672:0x0d2c, B:677:0x0d3e, B:680:0x0d57, B:682:0x0d66, B:683:0x0d72, B:687:0x0d7d, B:689:0x0d83, B:691:0x0d87, B:692:0x0d8f, B:694:0x0d95, B:695:0x0d9d, B:697:0x0dbb, B:699:0x0dc4, B:701:0x0dca, B:706:0x0dd5, B:710:0x0def, B:712:0x0df5, B:713:0x0e0f, B:715:0x0e13, B:717:0x0e26, B:718:0x0e2b, B:720:0x0e2f, B:721:0x0e39, B:722:0x0e3e, B:735:0x0e8d, B:738:0x0e95, B:740:0x0e9b, B:747:0x0eac, B:749:0x0eb3), top: B:1410:0x0b17 }] */
    /* JADX WARN: Removed duplicated region for block: B:666:0x0d0a A[Catch: Exception -> 0x0b9d, TryCatch #12 {Exception -> 0x0b9d, blocks: (B:590:0x0b60, B:600:0x0b94, B:604:0x0ba1, B:608:0x0bb0, B:612:0x0bc0, B:617:0x0bcc, B:648:0x0c6c, B:650:0x0c7b, B:652:0x0c83, B:654:0x0ca8, B:656:0x0cbb, B:657:0x0ccb, B:658:0x0cd3, B:659:0x0cda, B:660:0x0ce4, B:661:0x0ceb, B:663:0x0cf1, B:664:0x0cf9, B:666:0x0d0a, B:667:0x0d18, B:669:0x0d1e, B:670:0x0d28, B:672:0x0d2c, B:677:0x0d3e, B:680:0x0d57, B:682:0x0d66, B:683:0x0d72, B:687:0x0d7d, B:689:0x0d83, B:691:0x0d87, B:692:0x0d8f, B:694:0x0d95, B:695:0x0d9d, B:697:0x0dbb, B:699:0x0dc4, B:701:0x0dca, B:706:0x0dd5, B:710:0x0def, B:712:0x0df5, B:713:0x0e0f, B:715:0x0e13, B:717:0x0e26, B:718:0x0e2b, B:720:0x0e2f, B:721:0x0e39, B:722:0x0e3e, B:735:0x0e8d, B:738:0x0e95, B:740:0x0e9b, B:747:0x0eac, B:749:0x0eb3), top: B:1410:0x0b17 }] */
    /* JADX WARN: Removed duplicated region for block: B:667:0x0d18 A[Catch: Exception -> 0x0b9d, TryCatch #12 {Exception -> 0x0b9d, blocks: (B:590:0x0b60, B:600:0x0b94, B:604:0x0ba1, B:608:0x0bb0, B:612:0x0bc0, B:617:0x0bcc, B:648:0x0c6c, B:650:0x0c7b, B:652:0x0c83, B:654:0x0ca8, B:656:0x0cbb, B:657:0x0ccb, B:658:0x0cd3, B:659:0x0cda, B:660:0x0ce4, B:661:0x0ceb, B:663:0x0cf1, B:664:0x0cf9, B:666:0x0d0a, B:667:0x0d18, B:669:0x0d1e, B:670:0x0d28, B:672:0x0d2c, B:677:0x0d3e, B:680:0x0d57, B:682:0x0d66, B:683:0x0d72, B:687:0x0d7d, B:689:0x0d83, B:691:0x0d87, B:692:0x0d8f, B:694:0x0d95, B:695:0x0d9d, B:697:0x0dbb, B:699:0x0dc4, B:701:0x0dca, B:706:0x0dd5, B:710:0x0def, B:712:0x0df5, B:713:0x0e0f, B:715:0x0e13, B:717:0x0e26, B:718:0x0e2b, B:720:0x0e2f, B:721:0x0e39, B:722:0x0e3e, B:735:0x0e8d, B:738:0x0e95, B:740:0x0e9b, B:747:0x0eac, B:749:0x0eb3), top: B:1410:0x0b17 }] */
    /* JADX WARN: Removed duplicated region for block: B:675:0x0d3b  */
    /* JADX WARN: Removed duplicated region for block: B:676:0x0d3d  */
    /* JADX WARN: Removed duplicated region for block: B:678:0x0d53  */
    /* JADX WARN: Removed duplicated region for block: B:708:0x0dec  */
    /* JADX WARN: Removed duplicated region for block: B:749:0x0eb3 A[Catch: Exception -> 0x0b9d, TRY_LEAVE, TryCatch #12 {Exception -> 0x0b9d, blocks: (B:590:0x0b60, B:600:0x0b94, B:604:0x0ba1, B:608:0x0bb0, B:612:0x0bc0, B:617:0x0bcc, B:648:0x0c6c, B:650:0x0c7b, B:652:0x0c83, B:654:0x0ca8, B:656:0x0cbb, B:657:0x0ccb, B:658:0x0cd3, B:659:0x0cda, B:660:0x0ce4, B:661:0x0ceb, B:663:0x0cf1, B:664:0x0cf9, B:666:0x0d0a, B:667:0x0d18, B:669:0x0d1e, B:670:0x0d28, B:672:0x0d2c, B:677:0x0d3e, B:680:0x0d57, B:682:0x0d66, B:683:0x0d72, B:687:0x0d7d, B:689:0x0d83, B:691:0x0d87, B:692:0x0d8f, B:694:0x0d95, B:695:0x0d9d, B:697:0x0dbb, B:699:0x0dc4, B:701:0x0dca, B:706:0x0dd5, B:710:0x0def, B:712:0x0df5, B:713:0x0e0f, B:715:0x0e13, B:717:0x0e26, B:718:0x0e2b, B:720:0x0e2f, B:721:0x0e39, B:722:0x0e3e, B:735:0x0e8d, B:738:0x0e95, B:740:0x0e9b, B:747:0x0eac, B:749:0x0eb3), top: B:1410:0x0b17 }] */
    /* JADX WARN: Removed duplicated region for block: B:764:0x0f1b  */
    /* JADX WARN: Removed duplicated region for block: B:765:0x0f1e A[Catch: Exception -> 0x0f9b, TryCatch #13 {Exception -> 0x0f9b, blocks: (B:757:0x0ee8, B:758:0x0efc, B:762:0x0f15, B:765:0x0f1e, B:767:0x0f38, B:769:0x0f3f, B:771:0x0f45, B:774:0x0f57), top: B:1411:0x0ee8 }] */
    /* JADX WARN: Removed duplicated region for block: B:774:0x0f57 A[Catch: Exception -> 0x0f9b, TRY_LEAVE, TryCatch #13 {Exception -> 0x0f9b, blocks: (B:757:0x0ee8, B:758:0x0efc, B:762:0x0f15, B:765:0x0f1e, B:767:0x0f38, B:769:0x0f3f, B:771:0x0f45, B:774:0x0f57), top: B:1411:0x0ee8 }] */
    /* JADX WARN: Removed duplicated region for block: B:781:0x0f73  */
    /* JADX WARN: Removed duplicated region for block: B:784:0x0f85 A[Catch: Exception -> 0x1001, TryCatch #41 {Exception -> 0x1001, blocks: (B:776:0x0f61, B:780:0x0f70, B:782:0x0f79, B:784:0x0f85, B:786:0x0f92, B:787:0x0f96, B:794:0x0fb8, B:796:0x0fd3, B:798:0x0fe6, B:799:0x0ff4), top: B:1457:0x0e7a }] */
    /* JADX WARN: Removed duplicated region for block: B:857:0x10ff A[Catch: Exception -> 0x1155, TryCatch #32 {Exception -> 0x1155, blocks: (B:835:0x1094, B:843:0x10b3, B:845:0x10c0, B:847:0x10c8, B:850:0x10d8, B:852:0x10db, B:853:0x10f0, B:855:0x10fb, B:857:0x10ff, B:859:0x1107, B:862:0x111e, B:870:0x114a, B:880:0x1164, B:882:0x1170, B:884:0x1176, B:887:0x1184, B:892:0x1191), top: B:1403:0x1009 }] */
    /* JADX WARN: Removed duplicated region for block: B:900:0x11ac A[Catch: Exception -> 0x11b4, TRY_ENTER, TRY_LEAVE, TryCatch #7 {Exception -> 0x11b4, blocks: (B:821:0x1050, B:823:0x105e, B:826:0x1067, B:828:0x106d, B:830:0x107d, B:900:0x11ac), top: B:1403:0x1009 }] */
    /* JADX WARN: Removed duplicated region for block: B:904:0x11b9 A[Catch: Exception -> 0x1245, TRY_ENTER, TRY_LEAVE, TryCatch #38 {Exception -> 0x1245, blocks: (B:897:0x11a6, B:904:0x11b9), top: B:1452:0x11a6 }] */
    /* JADX WARN: Removed duplicated region for block: B:915:0x11f6  */
    /* JADX WARN: Removed duplicated region for block: B:931:0x1238  */
    /* JADX WARN: Type inference failed for: r13v11 */
    /* JADX WARN: Type inference failed for: r13v12 */
    /* JADX WARN: Type inference failed for: r13v13 */
    /* JADX WARN: Type inference failed for: r13v14 */
    /* JADX WARN: Type inference failed for: r13v15, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r13v18 */
    /* JADX WARN: Type inference failed for: r13v4 */
    /* JADX WARN: Type inference failed for: r13v5 */
    /* JADX WARN: Type inference failed for: r13v6 */
    /* JADX WARN: Type inference failed for: r13v71 */
    /* JADX WARN: Type inference failed for: r14v31 */
    /* JADX WARN: Type inference failed for: r14v32 */
    /* JADX WARN: Type inference failed for: r14v87 */
    /* JADX WARN: Type inference failed for: r14v90 */
    /* JADX WARN: Type inference failed for: r1v149 */
    /* JADX WARN: Type inference failed for: r1v158 */
    /* JADX WARN: Type inference failed for: r1v207, types: [org.telegram.tgnet.TLRPC$Message] */
    /* JADX WARN: Type inference failed for: r1v230 */
    /* JADX WARN: Type inference failed for: r1v237 */
    /* JADX WARN: Type inference failed for: r1v70, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r1v80, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r1v94, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r2v100, types: [org.telegram.tgnet.TLRPC$TL_inputMediaDocument, org.telegram.tgnet.TLRPC$InputMedia] */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v34 */
    /* JADX WARN: Type inference failed for: r2v35 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v7 */
    /* JADX WARN: Type inference failed for: r2v83, types: [java.lang.Object, org.telegram.messenger.MessageObject] */
    /* JADX WARN: Type inference failed for: r2v86 */
    /* JADX WARN: Type inference failed for: r2v94 */
    /* JADX WARN: Type inference failed for: r3v100, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r3v115, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r3v153, types: [org.telegram.messenger.SecretChatHelper] */
    /* JADX WARN: Type inference failed for: r4v91, types: [org.telegram.tgnet.TLRPC$TL_inputMediaDocument, org.telegram.tgnet.TLRPC$InputMedia] */
    /* JADX WARN: Type inference failed for: r5v149, types: [org.telegram.tgnet.TLRPC$TL_inputMediaPoll, org.telegram.tgnet.TLRPC$InputMedia] */
    /* JADX WARN: Type inference failed for: r7v103, types: [org.telegram.tgnet.TLRPC$TL_inputMediaDocument, org.telegram.tgnet.TLRPC$InputMedia] */
    /* JADX WARN: Type inference failed for: r7v206 */
    /* JADX WARN: Type inference failed for: r7v207 */
    /* JADX WARN: Type inference failed for: r7v40 */
    /* JADX WARN: Type inference failed for: r7v41, types: [org.telegram.tgnet.TLRPC$TL_messages_sendMedia, org.telegram.tgnet.TLObject] */
    /* JADX WARN: Type inference failed for: r7v43, types: [org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia] */
    /* JADX WARN: Type inference failed for: r7v49, types: [org.telegram.tgnet.TLRPC$TL_inputMediaPhoto] */
    /* JADX WARN: Type inference failed for: r80v0, types: [java.lang.Object] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void sendMessage(java.lang.String r56, java.lang.String r57, org.telegram.tgnet.TLRPC$MessageMedia r58, org.telegram.tgnet.TLRPC$TL_photo r59, org.telegram.messenger.VideoEditedInfo r60, org.telegram.tgnet.TLRPC$User r61, org.telegram.tgnet.TLRPC$TL_document r62, org.telegram.tgnet.TLRPC$TL_game r63, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r64, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r65, long r66, java.lang.String r68, org.telegram.messenger.MessageObject r69, org.telegram.messenger.MessageObject r70, org.telegram.tgnet.TLRPC$WebPage r71, boolean r72, org.telegram.messenger.MessageObject r73, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r74, org.telegram.tgnet.TLRPC$ReplyMarkup r75, java.util.HashMap<java.lang.String, java.lang.String> r76, boolean r77, int r78, int r79, java.lang.Object r80, org.telegram.messenger.MessageObject.SendAnimationData r81) {
        /*
            Method dump skipped, instructions count: 7259
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object, org.telegram.messenger.MessageObject$SendAnimationData):void");
    }

    private void performSendDelayedMessage(DelayedMessage delayedMessage) {
        performSendDelayedMessage(delayedMessage, -1);
    }

    private TLRPC$PhotoSize getThumbForSecretChat(ArrayList<TLRPC$PhotoSize> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                TLRPC$PhotoSize tLRPC$PhotoSize = arrayList.get(i);
                if (tLRPC$PhotoSize != null && !(tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) && !(tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize) && !(tLRPC$PhotoSize instanceof TLRPC$TL_photoSizeEmpty) && tLRPC$PhotoSize.location != null) {
                    TLRPC$TL_photoSize_layer127 tLRPC$TL_photoSize_layer127 = new TLRPC$TL_photoSize_layer127();
                    tLRPC$TL_photoSize_layer127.type = tLRPC$PhotoSize.type;
                    tLRPC$TL_photoSize_layer127.w = tLRPC$PhotoSize.w;
                    tLRPC$TL_photoSize_layer127.h = tLRPC$PhotoSize.h;
                    tLRPC$TL_photoSize_layer127.size = tLRPC$PhotoSize.size;
                    byte[] bArr = tLRPC$PhotoSize.bytes;
                    tLRPC$TL_photoSize_layer127.bytes = bArr;
                    if (bArr == null) {
                        tLRPC$TL_photoSize_layer127.bytes = new byte[0];
                    }
                    TLRPC$TL_fileLocation_layer82 tLRPC$TL_fileLocation_layer82 = new TLRPC$TL_fileLocation_layer82();
                    tLRPC$TL_photoSize_layer127.location = tLRPC$TL_fileLocation_layer82;
                    TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
                    tLRPC$TL_fileLocation_layer82.dc_id = tLRPC$FileLocation.dc_id;
                    tLRPC$TL_fileLocation_layer82.volume_id = tLRPC$FileLocation.volume_id;
                    tLRPC$TL_fileLocation_layer82.local_id = tLRPC$FileLocation.local_id;
                    tLRPC$TL_fileLocation_layer82.secret = tLRPC$FileLocation.secret;
                    return tLRPC$TL_photoSize_layer127;
                }
            }
        }
        return null;
    }

    private void performSendDelayedMessage(final DelayedMessage delayedMessage, int i) {
        boolean z;
        boolean z2;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        MessageObject messageObject;
        TLRPC$InputMedia tLRPC$InputMedia;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$InputMedia tLRPC$InputMedia2;
        TLRPC$InputMedia tLRPC$InputMedia3;
        int i2 = delayedMessage.type;
        boolean z3 = false;
        boolean z4 = true;
        if (i2 == 0) {
            String str = delayedMessage.httpLocation;
            if (str != null) {
                putToDelayedMessages(str, delayedMessage);
                ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "file", this.currentAccount);
            } else if (delayedMessage.sendRequest != null) {
                String file = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage.photoSize).toString();
                putToDelayedMessages(file, delayedMessage);
                getFileLoader().uploadFile(file, false, true, ConnectionsManager.FileTypePhoto);
                putToUploadingMessages(delayedMessage.obj);
            } else {
                String file2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage.photoSize).toString();
                if (delayedMessage.sendEncryptedRequest != null && delayedMessage.photoSize.location.dc_id != 0) {
                    File file3 = new File(file2);
                    if (!file3.exists()) {
                        file2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage.photoSize, true).toString();
                        file3 = new File(file2);
                    }
                    if (!file3.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(delayedMessage.photoSize), delayedMessage);
                        getFileLoader().loadFile(ImageLocation.getForObject(delayedMessage.photoSize, delayedMessage.locationParent), delayedMessage.parentObject, "jpg", 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(file2, delayedMessage);
                getFileLoader().uploadFile(file2, true, true, ConnectionsManager.FileTypePhoto);
                putToUploadingMessages(delayedMessage.obj);
            }
        } else if (i2 == 1) {
            VideoEditedInfo videoEditedInfo = delayedMessage.videoEditedInfo;
            if (videoEditedInfo != null && videoEditedInfo.needConvert()) {
                MessageObject messageObject2 = delayedMessage.obj;
                String str2 = messageObject2.messageOwner.attachPath;
                TLRPC$Document document = messageObject2.getDocument();
                if (str2 == null) {
                    str2 = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                }
                putToDelayedMessages(str2, delayedMessage);
                MediaController.getInstance().scheduleVideoConvert(delayedMessage.obj);
                putToUploadingMessages(delayedMessage.obj);
                return;
            }
            VideoEditedInfo videoEditedInfo2 = delayedMessage.videoEditedInfo;
            if (videoEditedInfo2 != null) {
                TLRPC$InputFile tLRPC$InputFile = videoEditedInfo2.file;
                if (tLRPC$InputFile != null) {
                    TLObject tLObject = delayedMessage.sendRequest;
                    if (tLObject instanceof TLRPC$TL_messages_sendMedia) {
                        tLRPC$InputMedia3 = ((TLRPC$TL_messages_sendMedia) tLObject).media;
                    } else {
                        tLRPC$InputMedia3 = ((TLRPC$TL_messages_editMessage) tLObject).media;
                    }
                    tLRPC$InputMedia3.file = tLRPC$InputFile;
                    videoEditedInfo2.file = null;
                } else if (videoEditedInfo2.encryptedFile != null) {
                    TLRPC$TL_decryptedMessage tLRPC$TL_decryptedMessage = (TLRPC$TL_decryptedMessage) delayedMessage.sendEncryptedRequest;
                    TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia = tLRPC$TL_decryptedMessage.media;
                    tLRPC$DecryptedMessageMedia.size = videoEditedInfo2.estimatedSize;
                    tLRPC$DecryptedMessageMedia.key = videoEditedInfo2.key;
                    tLRPC$DecryptedMessageMedia.iv = videoEditedInfo2.iv;
                    SecretChatHelper secretChatHelper = getSecretChatHelper();
                    MessageObject messageObject3 = delayedMessage.obj;
                    secretChatHelper.performSendEncryptedRequest(tLRPC$TL_decryptedMessage, messageObject3.messageOwner, delayedMessage.encryptedChat, delayedMessage.videoEditedInfo.encryptedFile, delayedMessage.originalPath, messageObject3);
                    delayedMessage.videoEditedInfo.encryptedFile = null;
                    return;
                }
            }
            TLObject tLObject2 = delayedMessage.sendRequest;
            if (tLObject2 != null) {
                if (tLObject2 instanceof TLRPC$TL_messages_sendMedia) {
                    tLRPC$InputMedia2 = ((TLRPC$TL_messages_sendMedia) tLObject2).media;
                } else {
                    tLRPC$InputMedia2 = ((TLRPC$TL_messages_editMessage) tLObject2).media;
                }
                if (tLRPC$InputMedia2.file == null) {
                    MessageObject messageObject4 = delayedMessage.obj;
                    String str3 = messageObject4.messageOwner.attachPath;
                    TLRPC$Document document2 = messageObject4.getDocument();
                    if (str3 == null) {
                        str3 = FileLoader.getDirectory(4) + "/" + document2.id + ".mp4";
                    }
                    String str4 = str3;
                    putToDelayedMessages(str4, delayedMessage);
                    VideoEditedInfo videoEditedInfo3 = delayedMessage.obj.videoEditedInfo;
                    if (videoEditedInfo3 != null && videoEditedInfo3.needConvert()) {
                        getFileLoader().uploadFile(str4, false, false, document2.size, ConnectionsManager.FileTypeVideo, false);
                    } else {
                        getFileLoader().uploadFile(str4, false, false, ConnectionsManager.FileTypeVideo);
                    }
                    putToUploadingMessages(delayedMessage.obj);
                    return;
                }
                String str5 = FileLoader.getDirectory(4) + "/" + delayedMessage.photoSize.location.volume_id + "_" + delayedMessage.photoSize.location.local_id + ".jpg";
                putToDelayedMessages(str5, delayedMessage);
                getFileLoader().uploadFile(str5, false, true, ConnectionsManager.FileTypePhoto);
                putToUploadingMessages(delayedMessage.obj);
                return;
            }
            MessageObject messageObject5 = delayedMessage.obj;
            String str6 = messageObject5.messageOwner.attachPath;
            TLRPC$Document document3 = messageObject5.getDocument();
            if (str6 == null) {
                str6 = FileLoader.getDirectory(4) + "/" + document3.id + ".mp4";
            }
            if (delayedMessage.sendEncryptedRequest != null && document3.dc_id != 0 && !new File(str6).exists()) {
                putToDelayedMessages(FileLoader.getAttachFileName(document3), delayedMessage);
                getFileLoader().loadFile(document3, delayedMessage.parentObject, 2, 0);
                return;
            }
            putToDelayedMessages(str6, delayedMessage);
            VideoEditedInfo videoEditedInfo4 = delayedMessage.obj.videoEditedInfo;
            if (videoEditedInfo4 != null && videoEditedInfo4.needConvert()) {
                getFileLoader().uploadFile(str6, true, false, document3.size, ConnectionsManager.FileTypeVideo, false);
            } else {
                getFileLoader().uploadFile(str6, true, false, ConnectionsManager.FileTypeVideo);
            }
            putToUploadingMessages(delayedMessage.obj);
        } else if (i2 == 2) {
            String str7 = delayedMessage.httpLocation;
            if (str7 != null) {
                putToDelayedMessages(str7, delayedMessage);
                ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "gif", this.currentAccount);
                return;
            }
            TLObject tLObject3 = delayedMessage.sendRequest;
            if (tLObject3 != null) {
                if (tLObject3 instanceof TLRPC$TL_messages_sendMedia) {
                    tLRPC$InputMedia = ((TLRPC$TL_messages_sendMedia) tLObject3).media;
                } else {
                    tLRPC$InputMedia = ((TLRPC$TL_messages_editMessage) tLObject3).media;
                }
                if (tLRPC$InputMedia.file == null) {
                    String str8 = delayedMessage.obj.messageOwner.attachPath;
                    putToDelayedMessages(str8, delayedMessage);
                    FileLoader fileLoader = getFileLoader();
                    if (delayedMessage.sendRequest != null) {
                        z4 = false;
                    }
                    fileLoader.uploadFile(str8, z4, false, ConnectionsManager.FileTypeFile);
                    putToUploadingMessages(delayedMessage.obj);
                    return;
                } else if (tLRPC$InputMedia.thumb != null || (tLRPC$PhotoSize = delayedMessage.photoSize) == null || (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize)) {
                    return;
                } else {
                    String str9 = FileLoader.getDirectory(4) + "/" + delayedMessage.photoSize.location.volume_id + "_" + delayedMessage.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(str9, delayedMessage);
                    getFileLoader().uploadFile(str9, false, true, ConnectionsManager.FileTypePhoto);
                    putToUploadingMessages(delayedMessage.obj);
                    return;
                }
            }
            MessageObject messageObject6 = delayedMessage.obj;
            String str10 = messageObject6.messageOwner.attachPath;
            TLRPC$Document document4 = messageObject6.getDocument();
            if (delayedMessage.sendEncryptedRequest != null && document4.dc_id != 0 && !new File(str10).exists()) {
                putToDelayedMessages(FileLoader.getAttachFileName(document4), delayedMessage);
                getFileLoader().loadFile(document4, delayedMessage.parentObject, 2, 0);
                return;
            }
            putToDelayedMessages(str10, delayedMessage);
            getFileLoader().uploadFile(str10, true, false, ConnectionsManager.FileTypeFile);
            putToUploadingMessages(delayedMessage.obj);
        } else if (i2 == 3) {
            String str11 = delayedMessage.obj.messageOwner.attachPath;
            putToDelayedMessages(str11, delayedMessage);
            FileLoader fileLoader2 = getFileLoader();
            if (delayedMessage.sendRequest == null) {
                z3 = true;
            }
            fileLoader2.uploadFile(str11, z3, true, ConnectionsManager.FileTypeAudio);
            putToUploadingMessages(delayedMessage.obj);
        } else if (i2 != 4) {
            if (i2 != 5) {
                return;
            }
            final String str12 = "stickerset_" + delayedMessage.obj.getId();
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet.stickerset = (TLRPC$InputStickerSet) delayedMessage.parentObject;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda85
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject4, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$performSendDelayedMessage$33(delayedMessage, str12, tLObject4, tLRPC$TL_error);
                }
            });
            putToDelayedMessages(str12, delayedMessage);
        } else {
            boolean z5 = i < 0;
            if (delayedMessage.performMediaUpload) {
                int size = i < 0 ? delayedMessage.messageObjects.size() - 1 : i;
                MessageObject messageObject7 = delayedMessage.messageObjects.get(size);
                if (messageObject7.getDocument() != null) {
                    if (delayedMessage.videoEditedInfo != null) {
                        String str13 = messageObject7.messageOwner.attachPath;
                        TLRPC$Document document5 = messageObject7.getDocument();
                        if (str13 == null) {
                            str13 = FileLoader.getDirectory(4) + "/" + document5.id + ".mp4";
                        }
                        putToDelayedMessages(str13, delayedMessage);
                        delayedMessage.extraHashMap.put(messageObject7, str13);
                        delayedMessage.extraHashMap.put(str13 + "_i", messageObject7);
                        TLRPC$PhotoSize tLRPC$PhotoSize2 = delayedMessage.photoSize;
                        if (tLRPC$PhotoSize2 != null && tLRPC$PhotoSize2.location != null) {
                            delayedMessage.extraHashMap.put(str13 + "_t", delayedMessage.photoSize);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject7);
                        delayedMessage.obj = messageObject7;
                        putToUploadingMessages(messageObject7);
                    } else {
                        TLRPC$Document document6 = messageObject7.getDocument();
                        String str14 = messageObject7.messageOwner.attachPath;
                        if (str14 == null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(FileLoader.getDirectory(4));
                            sb.append("/");
                            messageObject = messageObject7;
                            sb.append(document6.id);
                            sb.append(".mp4");
                            str14 = sb.toString();
                        } else {
                            messageObject = messageObject7;
                        }
                        TLObject tLObject4 = delayedMessage.sendRequest;
                        if (tLObject4 != null) {
                            TLRPC$InputMedia tLRPC$InputMedia4 = ((TLRPC$TL_messages_sendMultiMedia) tLObject4).multi_media.get(size).media;
                            if (tLRPC$InputMedia4.file == null) {
                                putToDelayedMessages(str14, delayedMessage);
                                MessageObject messageObject8 = messageObject;
                                delayedMessage.extraHashMap.put(messageObject8, str14);
                                delayedMessage.extraHashMap.put(str14, tLRPC$InputMedia4);
                                delayedMessage.extraHashMap.put(str14 + "_i", messageObject8);
                                TLRPC$PhotoSize tLRPC$PhotoSize3 = delayedMessage.photoSize;
                                if (tLRPC$PhotoSize3 != null && tLRPC$PhotoSize3.location != null) {
                                    delayedMessage.extraHashMap.put(str14 + "_t", delayedMessage.photoSize);
                                }
                                VideoEditedInfo videoEditedInfo5 = messageObject8.videoEditedInfo;
                                if (videoEditedInfo5 != null && videoEditedInfo5.needConvert()) {
                                    getFileLoader().uploadFile(str14, false, false, document6.size, ConnectionsManager.FileTypeVideo, false);
                                } else {
                                    getFileLoader().uploadFile(str14, false, false, ConnectionsManager.FileTypeVideo);
                                }
                                putToUploadingMessages(messageObject8);
                            } else {
                                MessageObject messageObject9 = messageObject;
                                if (delayedMessage.photoSize != null) {
                                    String str15 = FileLoader.getDirectory(4) + "/" + delayedMessage.photoSize.location.volume_id + "_" + delayedMessage.photoSize.location.local_id + ".jpg";
                                    putToDelayedMessages(str15, delayedMessage);
                                    delayedMessage.extraHashMap.put(str15 + "_o", str14);
                                    delayedMessage.extraHashMap.put(messageObject9, str15);
                                    delayedMessage.extraHashMap.put(str15, tLRPC$InputMedia4);
                                    getFileLoader().uploadFile(str15, false, true, ConnectionsManager.FileTypePhoto);
                                    putToUploadingMessages(messageObject9);
                                }
                            }
                        } else {
                            MessageObject messageObject10 = messageObject;
                            putToDelayedMessages(str14, delayedMessage);
                            delayedMessage.extraHashMap.put(messageObject10, str14);
                            delayedMessage.extraHashMap.put(str14, ((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest).files.get(size));
                            delayedMessage.extraHashMap.put(str14 + "_i", messageObject10);
                            TLRPC$PhotoSize tLRPC$PhotoSize4 = delayedMessage.photoSize;
                            if (tLRPC$PhotoSize4 != null && tLRPC$PhotoSize4.location != null) {
                                delayedMessage.extraHashMap.put(str14 + "_t", delayedMessage.photoSize);
                            }
                            VideoEditedInfo videoEditedInfo6 = messageObject10.videoEditedInfo;
                            if (videoEditedInfo6 != null && videoEditedInfo6.needConvert()) {
                                getFileLoader().uploadFile(str14, true, false, document6.size, ConnectionsManager.FileTypeVideo, false);
                            } else {
                                getFileLoader().uploadFile(str14, true, false, ConnectionsManager.FileTypeVideo);
                            }
                            putToUploadingMessages(messageObject10);
                        }
                    }
                    delayedMessage.videoEditedInfo = null;
                    delayedMessage.photoSize = null;
                } else {
                    String str16 = delayedMessage.httpLocation;
                    if (str16 != null) {
                        putToDelayedMessages(str16, delayedMessage);
                        delayedMessage.extraHashMap.put(messageObject7, delayedMessage.httpLocation);
                        delayedMessage.extraHashMap.put(delayedMessage.httpLocation, messageObject7);
                        ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "file", this.currentAccount);
                        delayedMessage.httpLocation = null;
                    } else {
                        TLObject tLObject5 = delayedMessage.sendRequest;
                        if (tLObject5 != null) {
                            tLRPC$InputEncryptedFile = ((TLRPC$TL_messages_sendMultiMedia) tLObject5).multi_media.get(size).media;
                        } else {
                            tLRPC$InputEncryptedFile = ((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest).files.get(size);
                        }
                        String file4 = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage.photoSize).toString();
                        putToDelayedMessages(file4, delayedMessage);
                        delayedMessage.extraHashMap.put(file4, tLRPC$InputEncryptedFile);
                        delayedMessage.extraHashMap.put(messageObject7, file4);
                        z = true;
                        getFileLoader().uploadFile(file4, delayedMessage.sendEncryptedRequest != null, true, ConnectionsManager.FileTypePhoto);
                        putToUploadingMessages(messageObject7);
                        delayedMessage.photoSize = null;
                        z2 = false;
                        delayedMessage.performMediaUpload = z2;
                    }
                }
                z2 = false;
                z = true;
                delayedMessage.performMediaUpload = z2;
            } else {
                z = true;
                if (!delayedMessage.messageObjects.isEmpty()) {
                    ArrayList<MessageObject> arrayList = delayedMessage.messageObjects;
                    putToSendingMessages(arrayList.get(arrayList.size() - 1).messageOwner, delayedMessage.finalGroupMessage != 0);
                }
            }
            sendReadyToSendGroup(delayedMessage, z5, z);
        }
    }

    public /* synthetic */ void lambda$performSendDelayedMessage$33(final DelayedMessage delayedMessage, final String str, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendDelayedMessage$32(tLObject, delayedMessage, str);
            }
        });
    }

    public /* synthetic */ void lambda$performSendDelayedMessage$32(TLObject tLObject, DelayedMessage delayedMessage, String str) {
        boolean z;
        if (tLObject != null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            getMediaDataController().storeTempStickerSet(tLRPC$TL_messages_stickerSet);
            TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName = new TLRPC$TL_inputStickerSetShortName();
            ((TLRPC$TL_documentAttributeSticker_layer55) delayedMessage.locationParent).stickerset = tLRPC$TL_inputStickerSetShortName;
            tLRPC$TL_inputStickerSetShortName.short_name = tLRPC$TL_messages_stickerSet.set.short_name;
            z = true;
        } else {
            z = false;
        }
        ArrayList<DelayedMessage> remove = this.delayedMessages.remove(str);
        if (remove == null || remove.isEmpty()) {
            return;
        }
        if (z) {
            getMessagesStorage().replaceMessageIfExists(remove.get(0).obj.messageOwner, null, null, false);
        }
        MessageObject messageObject = delayedMessage.obj;
        getSecretChatHelper().performSendEncryptedRequest((TLRPC$DecryptedMessage) delayedMessage.sendEncryptedRequest, messageObject.messageOwner, delayedMessage.encryptedChat, null, null, messageObject);
    }

    private void uploadMultiMedia(final DelayedMessage delayedMessage, final TLRPC$InputMedia tLRPC$InputMedia, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, String str) {
        if (tLRPC$InputMedia == null) {
            if (tLRPC$InputEncryptedFile == null) {
                return;
            }
            TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            int i = 0;
            while (true) {
                if (i >= tLRPC$TL_messages_sendEncryptedMultiMedia.files.size()) {
                    break;
                } else if (tLRPC$TL_messages_sendEncryptedMultiMedia.files.get(i) == tLRPC$InputEncryptedFile) {
                    putToSendingMessages(delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.fileUploadProgressChanged, str, -1L, -1L, Boolean.FALSE);
                    break;
                } else {
                    i++;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
            return;
        }
        TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest;
        int i2 = 0;
        while (true) {
            if (i2 >= tLRPC$TL_messages_sendMultiMedia.multi_media.size()) {
                break;
            } else if (tLRPC$TL_messages_sendMultiMedia.multi_media.get(i2).media == tLRPC$InputMedia) {
                putToSendingMessages(delayedMessage.messages.get(i2), delayedMessage.scheduled);
                getNotificationCenter().postNotificationName(NotificationCenter.fileUploadProgressChanged, str, -1L, -1L, Boolean.FALSE);
                break;
            } else {
                i2++;
            }
        }
        TLRPC$TL_messages_uploadMedia tLRPC$TL_messages_uploadMedia = new TLRPC$TL_messages_uploadMedia();
        tLRPC$TL_messages_uploadMedia.media = tLRPC$InputMedia;
        tLRPC$TL_messages_uploadMedia.peer = ((TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_uploadMedia, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda87
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$uploadMultiMedia$35(tLRPC$InputMedia, delayedMessage, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$uploadMultiMedia$35(final TLRPC$InputMedia tLRPC$InputMedia, final DelayedMessage delayedMessage, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$uploadMultiMedia$34(tLObject, tLRPC$InputMedia, delayedMessage);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:19:0x005f  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0097  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$uploadMultiMedia$34(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC$InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
        /*
            r5 = this;
            if (r6 == 0) goto L5c
            org.telegram.tgnet.TLRPC$MessageMedia r6 = (org.telegram.tgnet.TLRPC$MessageMedia) r6
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto
            if (r0 == 0) goto L30
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L30
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputPhoto
            r1.<init>()
            r0.id = r1
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            long r2 = r6.id
            r1.id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            byte[] r6 = r6.file_reference
            r1.file_reference = r6
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r6 == 0) goto L5d
            java.lang.String r6 = "set uploaded photo"
            org.telegram.messenger.FileLog.d(r6)
            goto L5d
        L30:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument
            if (r0 == 0) goto L5c
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L5c
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputDocument
            r1.<init>()
            r0.id = r1
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            long r2 = r6.id
            r1.id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            byte[] r6 = r6.file_reference
            r1.file_reference = r6
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r6 == 0) goto L5d
            java.lang.String r6 = "set uploaded document"
            org.telegram.messenger.FileLog.d(r6)
            goto L5d
        L5c:
            r0 = 0
        L5d:
            if (r0 == 0) goto L97
            int r6 = r7.ttl_seconds
            r1 = 1
            if (r6 == 0) goto L6b
            r0.ttl_seconds = r6
            int r6 = r0.flags
            r6 = r6 | r1
            r0.flags = r6
        L6b:
            org.telegram.tgnet.TLObject r6 = r8.sendRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r6 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r6
            r2 = 0
            r3 = 0
        L71:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            int r4 = r4.size()
            if (r3 >= r4) goto L93
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r4 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r4
            org.telegram.tgnet.TLRPC$InputMedia r4 = r4.media
            if (r4 != r7) goto L90
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r6 = r6.multi_media
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r6
            r6.media = r0
            goto L93
        L90:
            int r3 = r3 + 1
            goto L71
        L93:
            r5.sendReadyToSendGroup(r8, r2, r1)
            goto L9a
        L97:
            r8.markAsError()
        L9a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$uploadMultiMedia$34(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
    }

    private void sendReadyToSendGroup(DelayedMessage delayedMessage, boolean z, boolean z2) {
        ArrayList<MessageObject> arrayList;
        DelayedMessage findMaxDelayedMessageForMessageId;
        if (delayedMessage.messageObjects.isEmpty()) {
            delayedMessage.markAsError();
            return;
        }
        String str = "group_" + delayedMessage.groupId;
        if (delayedMessage.finalGroupMessage != delayedMessage.messageObjects.get(arrayList.size() - 1).getId()) {
            if (z) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("final message not added, add");
                }
                putToDelayedMessages(str, delayedMessage);
                return;
            } else if (!BuildVars.DEBUG_VERSION) {
                return;
            } else {
                FileLog.d("final message not added");
                return;
            }
        }
        int i = 0;
        if (z) {
            this.delayedMessages.remove(str);
            getMessagesStorage().putMessages(delayedMessage.messages, false, true, false, 0, delayedMessage.scheduled);
            getMessagesController().updateInterfaceWithMessages(delayedMessage.peer, delayedMessage.messageObjects, delayedMessage.scheduled);
            if (!delayedMessage.scheduled) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("add message");
            }
        }
        TLObject tLObject = delayedMessage.sendRequest;
        if (tLObject instanceof TLRPC$TL_messages_sendMultiMedia) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) tLObject;
            while (i < tLRPC$TL_messages_sendMultiMedia.multi_media.size()) {
                TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_messages_sendMultiMedia.multi_media.get(i).media;
                if ((tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedPhoto) || (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedDocument)) {
                    if (!BuildVars.DEBUG_VERSION) {
                        return;
                    }
                    FileLog.d("multi media not ready");
                    return;
                }
                i++;
            }
            if (z2 && (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(delayedMessage.finalGroupMessage, delayedMessage.peer)) != null) {
                findMaxDelayedMessageForMessageId.addDelayedRequest(delayedMessage.sendRequest, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
                ArrayList<DelayedMessageSendAfterRequest> arrayList2 = delayedMessage.requests;
                if (arrayList2 != null) {
                    findMaxDelayedMessageForMessageId.requests.addAll(arrayList2);
                }
                if (!BuildVars.DEBUG_VERSION) {
                    return;
                }
                FileLog.d("has maxDelayedMessage, delay");
                return;
            }
        } else {
            TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            while (i < tLRPC$TL_messages_sendEncryptedMultiMedia.files.size()) {
                if (tLRPC$TL_messages_sendEncryptedMultiMedia.files.get(i) instanceof TLRPC$TL_inputEncryptedFile) {
                    return;
                }
                i++;
            }
        }
        TLObject tLObject2 = delayedMessage.sendRequest;
        if (tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia) {
            performSendMessageRequestMulti((TLRPC$TL_messages_sendMultiMedia) tLObject2, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
        } else {
            getSecretChatHelper().performSendEncryptedRequest((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest, delayedMessage);
        }
        delayedMessage.sendDelayedRequests();
    }

    public /* synthetic */ void lambda$stopVideoService$36(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$37(final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$stopVideoService$36(str);
            }
        });
    }

    public void stopVideoService(final String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$stopVideoService$37(str);
            }
        });
    }

    public void putToSendingMessages(final TLRPC$Message tLRPC$Message, final boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$putToSendingMessages$38(tLRPC$Message, z);
                }
            });
        } else {
            putToSendingMessages(tLRPC$Message, z, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$38(TLRPC$Message tLRPC$Message, boolean z) {
        putToSendingMessages(tLRPC$Message, z, true);
    }

    protected void putToSendingMessages(TLRPC$Message tLRPC$Message, boolean z, boolean z2) {
        if (tLRPC$Message == null) {
            return;
        }
        int i = tLRPC$Message.id;
        if (i > 0) {
            this.editingMessages.put(i, tLRPC$Message);
            return;
        }
        boolean z3 = this.sendingMessages.indexOfKey(i) >= 0;
        removeFromUploadingMessages(tLRPC$Message.id, z);
        this.sendingMessages.put(tLRPC$Message.id, tLRPC$Message);
        if (z || z3) {
            return;
        }
        long dialogId = MessageObject.getDialogId(tLRPC$Message);
        LongSparseArray<Integer> longSparseArray = this.sendingMessagesIdDialogs;
        longSparseArray.put(dialogId, Integer.valueOf(longSparseArray.get(dialogId, 0).intValue() + 1));
        if (!z2) {
            return;
        }
        getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
    }

    public TLRPC$Message removeFromSendingMessages(int i, boolean z) {
        long dialogId;
        Integer num;
        if (i > 0) {
            TLRPC$Message tLRPC$Message = this.editingMessages.get(i);
            if (tLRPC$Message == null) {
                return tLRPC$Message;
            }
            this.editingMessages.remove(i);
            return tLRPC$Message;
        }
        TLRPC$Message tLRPC$Message2 = this.sendingMessages.get(i);
        if (tLRPC$Message2 != null) {
            this.sendingMessages.remove(i);
            if (!z && (num = this.sendingMessagesIdDialogs.get((dialogId = MessageObject.getDialogId(tLRPC$Message2)))) != null) {
                int intValue = num.intValue() - 1;
                if (intValue <= 0) {
                    this.sendingMessagesIdDialogs.remove(dialogId);
                } else {
                    this.sendingMessagesIdDialogs.put(dialogId, Integer.valueOf(intValue));
                }
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
        return tLRPC$Message2;
    }

    public int getSendingMessageId(long j) {
        for (int i = 0; i < this.sendingMessages.size(); i++) {
            TLRPC$Message valueAt = this.sendingMessages.valueAt(i);
            if (valueAt.dialog_id == j) {
                return valueAt.id;
            }
        }
        for (int i2 = 0; i2 < this.uploadMessages.size(); i2++) {
            TLRPC$Message valueAt2 = this.uploadMessages.valueAt(i2);
            if (valueAt2.dialog_id == j) {
                return valueAt2.id;
            }
        }
        return 0;
    }

    protected void putToUploadingMessages(MessageObject messageObject) {
        if (messageObject == null || messageObject.getId() > 0 || messageObject.scheduled) {
            return;
        }
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        boolean z = this.uploadMessages.indexOfKey(tLRPC$Message.id) >= 0;
        this.uploadMessages.put(tLRPC$Message.id, tLRPC$Message);
        if (z) {
            return;
        }
        long dialogId = MessageObject.getDialogId(tLRPC$Message);
        LongSparseArray<Integer> longSparseArray = this.uploadingMessagesIdDialogs;
        longSparseArray.put(dialogId, Integer.valueOf(longSparseArray.get(dialogId, 0).intValue() + 1));
        getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
    }

    protected void removeFromUploadingMessages(int i, boolean z) {
        TLRPC$Message tLRPC$Message;
        if (i > 0 || z || (tLRPC$Message = this.uploadMessages.get(i)) == null) {
            return;
        }
        this.uploadMessages.remove(i);
        long dialogId = MessageObject.getDialogId(tLRPC$Message);
        Integer num = this.uploadingMessagesIdDialogs.get(dialogId);
        if (num == null) {
            return;
        }
        int intValue = num.intValue() - 1;
        if (intValue <= 0) {
            this.uploadingMessagesIdDialogs.remove(dialogId);
        } else {
            this.uploadingMessagesIdDialogs.put(dialogId, Integer.valueOf(intValue));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
    }

    public boolean isSendingMessage(int i) {
        return this.sendingMessages.indexOfKey(i) >= 0 || this.editingMessages.indexOfKey(i) >= 0;
    }

    public boolean isSendingMessageIdDialog(long j) {
        return this.sendingMessagesIdDialogs.get(j, 0).intValue() > 0;
    }

    public boolean isUploadingMessageIdDialog(long j) {
        return this.uploadingMessagesIdDialogs.get(j, 0).intValue() > 0;
    }

    public void performSendMessageRequestMulti(final TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, final ArrayList<MessageObject> arrayList, final ArrayList<String> arrayList2, final ArrayList<Object> arrayList3, final DelayedMessage delayedMessage, final boolean z) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            putToSendingMessages(arrayList.get(i).messageOwner, z);
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMultiMedia, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda83
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$46(arrayList3, tLRPC$TL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage, z, tLObject, tLRPC$TL_error);
            }
        }, (QuickAckDelegate) null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$46(ArrayList arrayList, final TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, final ArrayList arrayList2, final ArrayList arrayList3, final DelayedMessage delayedMessage, final boolean z, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && FileRefController.isFileRefError(tLRPC$TL_error.text)) {
            if (arrayList != null) {
                ArrayList arrayList4 = new ArrayList(arrayList);
                getFileRefController().requestReference(arrayList4, tLRPC$TL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList4, delayedMessage, Boolean.valueOf(z));
                return;
            } else if (delayedMessage != null && !delayedMessage.retriedToSend) {
                delayedMessage.retriedToSend = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda61
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.this.lambda$performSendMessageRequestMulti$39(tLRPC$TL_messages_sendMultiMedia, delayedMessage, arrayList2, z);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda56
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$45(tLRPC$TL_error, tLObject, arrayList2, arrayList3, z, tLRPC$TL_messages_sendMultiMedia);
            }
        });
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$39(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        int size = tLRPC$TL_messages_sendMultiMedia.multi_media.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            if (delayedMessage.parentObjects.get(i) != null) {
                removeFromSendingMessages(((MessageObject) arrayList.get(i)).getId(), z);
                TLRPC$TL_inputSingleMedia tLRPC$TL_inputSingleMedia = tLRPC$TL_messages_sendMultiMedia.multi_media.get(i);
                TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_inputSingleMedia.media;
                if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                    tLRPC$TL_inputSingleMedia.media = delayedMessage.inputMedias.get(i);
                } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                    tLRPC$TL_inputSingleMedia.media = delayedMessage.inputMedias.get(i);
                }
                delayedMessage.videoEditedInfo = delayedMessage.videoEditedInfos.get(i);
                delayedMessage.httpLocation = delayedMessage.httpLocations.get(i);
                TLRPC$PhotoSize tLRPC$PhotoSize = delayedMessage.locations.get(i);
                delayedMessage.photoSize = tLRPC$PhotoSize;
                delayedMessage.performMediaUpload = true;
                if (tLRPC$TL_inputSingleMedia.media.file == null || tLRPC$PhotoSize != null) {
                    z2 = true;
                }
                performSendDelayedMessage(delayedMessage, i);
            }
        }
        if (!z2) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC$Message tLRPC$Message = ((MessageObject) arrayList.get(i2)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message, z);
                tLRPC$Message.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message.id));
                processSentMessage(tLRPC$Message.id);
                removeFromSendingMessages(tLRPC$Message.id, z);
            }
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$45(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, final boolean z, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia) {
        boolean z2;
        final TLRPC$Updates tLRPC$Updates;
        boolean z3;
        TLRPC$Message tLRPC$Message;
        TLRPC$Updates tLRPC$Updates2;
        int i;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader;
        if (tLRPC$TL_error == null) {
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC$Updates tLRPC$Updates3 = (TLRPC$Updates) tLObject;
            ArrayList<TLRPC$Update> arrayList3 = tLRPC$Updates3.updates;
            LongSparseArray<SparseArray<TLRPC$MessageReplies>> longSparseArray2 = null;
            int i2 = 0;
            while (i2 < arrayList3.size()) {
                TLRPC$Update tLRPC$Update = arrayList3.get(i2);
                if (tLRPC$Update instanceof TLRPC$TL_updateMessageID) {
                    TLRPC$TL_updateMessageID tLRPC$TL_updateMessageID = (TLRPC$TL_updateMessageID) tLRPC$Update;
                    longSparseArray.put(tLRPC$TL_updateMessageID.random_id, Integer.valueOf(tLRPC$TL_updateMessageID.id));
                    arrayList3.remove(i2);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
                    final TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage = (TLRPC$TL_updateNewMessage) tLRPC$Update;
                    TLRPC$Message tLRPC$Message2 = tLRPC$TL_updateNewMessage.message;
                    sparseArray.put(tLRPC$Message2.id, tLRPC$Message2);
                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda66
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.lambda$performSendMessageRequestMulti$40(tLRPC$TL_updateNewMessage);
                        }
                    });
                    arrayList3.remove(i2);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
                    final TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage = (TLRPC$TL_updateNewChannelMessage) tLRPC$Update;
                    TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(MessagesController.getUpdateChannelId(tLRPC$TL_updateNewChannelMessage)));
                    if ((chat == null || chat.megagroup) && (tLRPC$TL_messageReplyHeader = tLRPC$TL_updateNewChannelMessage.message.reply_to) != null && (tLRPC$TL_messageReplyHeader.reply_to_top_id != 0 || tLRPC$TL_messageReplyHeader.reply_to_msg_id != 0)) {
                        if (longSparseArray2 == null) {
                            longSparseArray2 = new LongSparseArray<>();
                        }
                        long dialogId = MessageObject.getDialogId(tLRPC$TL_updateNewChannelMessage.message);
                        SparseArray<TLRPC$MessageReplies> sparseArray2 = longSparseArray2.get(dialogId);
                        if (sparseArray2 == null) {
                            sparseArray2 = new SparseArray<>();
                            longSparseArray2.put(dialogId, sparseArray2);
                        }
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = tLRPC$TL_updateNewChannelMessage.message.reply_to;
                        int i3 = tLRPC$TL_messageReplyHeader2.reply_to_top_id;
                        if (i3 == 0) {
                            i3 = tLRPC$TL_messageReplyHeader2.reply_to_msg_id;
                        }
                        TLRPC$MessageReplies tLRPC$MessageReplies = sparseArray2.get(i3);
                        if (tLRPC$MessageReplies == null) {
                            tLRPC$MessageReplies = new TLRPC$TL_messageReplies();
                            sparseArray2.put(i3, tLRPC$MessageReplies);
                        }
                        TLRPC$Peer tLRPC$Peer = tLRPC$TL_updateNewChannelMessage.message.from_id;
                        if (tLRPC$Peer != null) {
                            tLRPC$MessageReplies.recent_repliers.add(0, tLRPC$Peer);
                        }
                        tLRPC$MessageReplies.replies++;
                    }
                    TLRPC$Message tLRPC$Message3 = tLRPC$TL_updateNewChannelMessage.message;
                    sparseArray.put(tLRPC$Message3.id, tLRPC$Message3);
                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda63
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.lambda$performSendMessageRequestMulti$41(tLRPC$TL_updateNewChannelMessage);
                        }
                    });
                    arrayList3.remove(i2);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewScheduledMessage) {
                    TLRPC$Message tLRPC$Message4 = ((TLRPC$TL_updateNewScheduledMessage) tLRPC$Update).message;
                    sparseArray.put(tLRPC$Message4.id, tLRPC$Message4);
                    arrayList3.remove(i2);
                } else {
                    i2++;
                }
                i2--;
                i2++;
            }
            if (longSparseArray2 != null) {
                getMessagesStorage().putChannelViews(null, null, longSparseArray2, true);
                getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, null, null, longSparseArray2, Boolean.TRUE);
            }
            int i4 = 0;
            while (i4 < arrayList.size()) {
                MessageObject messageObject = (MessageObject) arrayList.get(i4);
                String str = (String) arrayList2.get(i4);
                final TLRPC$Message tLRPC$Message5 = messageObject.messageOwner;
                final int i5 = tLRPC$Message5.id;
                final ArrayList arrayList4 = new ArrayList();
                Integer num = (Integer) longSparseArray.get(tLRPC$Message5.random_id);
                if (num == null || (tLRPC$Message = (TLRPC$Message) sparseArray.get(num.intValue())) == null) {
                    tLRPC$Updates = tLRPC$Updates3;
                    z3 = true;
                    break;
                }
                MessageObject.getDialogId(tLRPC$Message);
                arrayList4.add(tLRPC$Message);
                if ((tLRPC$Message.flags & ConnectionsManager.FileTypeVideo) != 0) {
                    TLRPC$Message tLRPC$Message6 = messageObject.messageOwner;
                    tLRPC$Message6.ttl_period = tLRPC$Message.ttl_period;
                    tLRPC$Message6.flags = 33554432 | tLRPC$Message6.flags;
                }
                updateMediaPaths(messageObject, tLRPC$Message, tLRPC$Message.id, str, false);
                final int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                tLRPC$Message5.id = tLRPC$Message.id;
                final long j = tLRPC$Message.grouped_id;
                if (!z) {
                    tLRPC$Updates2 = tLRPC$Updates3;
                    i = i4;
                    Integer num2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(tLRPC$Message.dialog_id));
                    if (num2 == null) {
                        num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(tLRPC$Message.out, tLRPC$Message.dialog_id));
                        getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(tLRPC$Message.dialog_id), num2);
                    }
                    tLRPC$Message.unread = num2.intValue() < tLRPC$Message.id;
                } else {
                    tLRPC$Updates2 = tLRPC$Updates3;
                    i = i4;
                }
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                tLRPC$Message5.send_state = 0;
                getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i5), Integer.valueOf(tLRPC$Message5.id), tLRPC$Message5, Long.valueOf(tLRPC$Message5.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda50
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.this.lambda$performSendMessageRequestMulti$43(tLRPC$Message5, i5, z, arrayList4, j, mediaExistanceFlags);
                    }
                });
                i4 = i + 1;
                sparseArray = sparseArray;
                tLRPC$Updates3 = tLRPC$Updates2;
                longSparseArray = longSparseArray;
            }
            tLRPC$Updates = tLRPC$Updates3;
            z3 = false;
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda68
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequestMulti$44(tLRPC$Updates);
                }
            });
            z2 = z3;
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, null, tLRPC$TL_messages_sendMultiMedia, new Object[0]);
            z2 = true;
        }
        if (z2) {
            for (int i6 = 0; i6 < arrayList.size(); i6++) {
                TLRPC$Message tLRPC$Message7 = ((MessageObject) arrayList.get(i6)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message7, z);
                tLRPC$Message7.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message7.id));
                processSentMessage(tLRPC$Message7.id);
                removeFromSendingMessages(tLRPC$Message7.id, z);
            }
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$40(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$41(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$43(final TLRPC$Message tLRPC$Message, final int i, final boolean z, ArrayList arrayList, final long j, final int i2) {
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message.random_id, MessageObject.getPeerId(tLRPC$Message.peer_id), Integer.valueOf(i), tLRPC$Message.id, 0, false, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$42(tLRPC$Message, i, j, i2, z);
            }
        });
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$42(TLRPC$Message tLRPC$Message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$44(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, Object obj, HashMap<String, String> hashMap, boolean z) {
        performSendMessageRequest(tLObject, messageObject, str, null, false, delayedMessage, obj, hashMap, z);
    }

    private DelayedMessage findMaxDelayedMessageForMessageId(int i, long j) {
        int i2;
        DelayedMessage delayedMessage = null;
        int i3 = Integer.MIN_VALUE;
        for (Map.Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> value = entry.getValue();
            int size = value.size();
            for (int i4 = 0; i4 < size; i4++) {
                DelayedMessage delayedMessage2 = value.get(i4);
                int i5 = delayedMessage2.type;
                if ((i5 == 4 || i5 == 0) && delayedMessage2.peer == j) {
                    MessageObject messageObject = delayedMessage2.obj;
                    if (messageObject != null) {
                        i2 = messageObject.getId();
                    } else {
                        ArrayList<MessageObject> arrayList = delayedMessage2.messageObjects;
                        if (arrayList == null || arrayList.isEmpty()) {
                            i2 = 0;
                        } else {
                            ArrayList<MessageObject> arrayList2 = delayedMessage2.messageObjects;
                            i2 = arrayList2.get(arrayList2.size() - 1).getId();
                        }
                    }
                    if (i2 != 0 && i2 > i && delayedMessage == null && i3 < i2) {
                        delayedMessage = delayedMessage2;
                        i3 = i2;
                    }
                }
            }
        }
        return delayedMessage;
    }

    public void performSendMessageRequest(final TLObject tLObject, final MessageObject messageObject, final String str, final DelayedMessage delayedMessage, final boolean z, final DelayedMessage delayedMessage2, final Object obj, HashMap<String, String> hashMap, final boolean z2) {
        DelayedMessage findMaxDelayedMessageForMessageId;
        ArrayList<DelayedMessageSendAfterRequest> arrayList;
        if (!(tLObject instanceof TLRPC$TL_messages_editMessage) && z && (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(messageObject.getId(), messageObject.getDialogId())) != null) {
            findMaxDelayedMessageForMessageId.addDelayedRequest(tLObject, messageObject, str, obj, delayedMessage2, delayedMessage != null ? delayedMessage.scheduled : false);
            if (delayedMessage == null || (arrayList = delayedMessage.requests) == null) {
                return;
            }
            findMaxDelayedMessageForMessageId.requests.addAll(arrayList);
            return;
        }
        final TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        putToSendingMessages(tLRPC$Message, z2);
        tLRPC$Message.reqId = getConnectionsManager().sendRequest(tLObject, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda86
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$performSendMessageRequest$60(tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z2, tLRPC$Message, tLObject2, tLRPC$TL_error);
            }
        }, new QuickAckDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda77
            @Override // org.telegram.tgnet.QuickAckDelegate
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequest$62(tLRPC$Message);
            }
        }, (tLObject instanceof TLRPC$TL_messages_sendMessage ? ConnectionsManager.RequestFlagNeedQuickAck : 0) | 68);
        if (delayedMessage == null) {
            return;
        }
        delayedMessage.sendDelayedRequests();
    }

    public /* synthetic */ void lambda$performSendMessageRequest$60(final TLObject tLObject, Object obj, final MessageObject messageObject, final String str, DelayedMessage delayedMessage, boolean z, final DelayedMessage delayedMessage2, final boolean z2, final TLRPC$Message tLRPC$Message, final TLObject tLObject2, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && (((tLObject instanceof TLRPC$TL_messages_sendMedia) || (tLObject instanceof TLRPC$TL_messages_editMessage)) && FileRefController.isFileRefError(tLRPC$TL_error.text))) {
            if (obj != null) {
                getFileRefController().requestReference(obj, tLObject, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda55
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.this.lambda$performSendMessageRequest$47(tLRPC$Message, z2, tLObject, delayedMessage2);
                    }
                });
                return;
            }
        }
        if (tLObject instanceof TLRPC$TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda58
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$50(tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, z2, tLObject);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda71
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$59(z2, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, tLObject);
                }
            });
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$47(TLRPC$Message tLRPC$Message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
        removeFromSendingMessages(tLRPC$Message.id, z);
        if (tLObject instanceof TLRPC$TL_messages_sendMedia) {
            TLRPC$TL_messages_sendMedia tLRPC$TL_messages_sendMedia = (TLRPC$TL_messages_sendMedia) tLObject;
            TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_messages_sendMedia.media;
            if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                tLRPC$TL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                tLRPC$TL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            }
        } else if (tLObject instanceof TLRPC$TL_messages_editMessage) {
            TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = (TLRPC$TL_messages_editMessage) tLObject;
            TLRPC$InputMedia tLRPC$InputMedia2 = tLRPC$TL_messages_editMessage.media;
            if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaPhoto) {
                tLRPC$TL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            } else if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaDocument) {
                tLRPC$TL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            }
        }
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$50(TLRPC$TL_error tLRPC$TL_error, final TLRPC$Message tLRPC$Message, TLObject tLObject, MessageObject messageObject, String str, final boolean z, TLObject tLObject2) {
        int i = 0;
        TLRPC$Message tLRPC$Message2 = null;
        if (tLRPC$TL_error == null) {
            String str2 = tLRPC$Message.attachPath;
            final TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            ArrayList<TLRPC$Update> arrayList = tLRPC$Updates.updates;
            while (true) {
                if (i >= arrayList.size()) {
                    break;
                }
                TLRPC$Update tLRPC$Update = arrayList.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateEditMessage) {
                    tLRPC$Message2 = ((TLRPC$TL_updateEditMessage) tLRPC$Update).message;
                    break;
                } else if (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) {
                    tLRPC$Message2 = ((TLRPC$TL_updateEditChannelMessage) tLRPC$Update).message;
                    break;
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewScheduledMessage) {
                    tLRPC$Message2 = ((TLRPC$TL_updateNewScheduledMessage) tLRPC$Update).message;
                    break;
                } else {
                    i++;
                }
            }
            TLRPC$Message tLRPC$Message3 = tLRPC$Message2;
            if (tLRPC$Message3 != null) {
                ImageLoader.saveMessageThumbs(tLRPC$Message3);
                updateMediaPaths(messageObject, tLRPC$Message3, tLRPC$Message3.id, str, false);
            }
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda70
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$49(tLRPC$Updates, tLRPC$Message, z);
                }
            });
            if (!MessageObject.isVideoMessage(tLRPC$Message) && !MessageObject.isRoundVideoMessage(tLRPC$Message) && !MessageObject.isNewGifMessage(tLRPC$Message)) {
                return;
            }
            stopVideoService(str2);
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, null, tLObject2, new Object[0]);
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(tLRPC$Message.attachPath);
        }
        removeFromSendingMessages(tLRPC$Message.id, z);
        revertEditingMessageObject(messageObject);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$49(TLRPC$Updates tLRPC$Updates, final TLRPC$Message tLRPC$Message, final boolean z) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequest$48(tLRPC$Message, z);
            }
        });
    }

    public /* synthetic */ void lambda$performSendMessageRequest$48(TLRPC$Message tLRPC$Message, boolean z) {
        processSentMessage(tLRPC$Message.id);
        removeFromSendingMessages(tLRPC$Message.id, z);
    }

    /* JADX WARN: Code restructure failed: missing block: B:42:0x0116, code lost:
        r12 = r3;
        r2 = null;
     */
    /* JADX WARN: Removed duplicated region for block: B:110:0x029f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$performSendMessageRequest$59(final boolean r28, org.telegram.tgnet.TLRPC$TL_error r29, final org.telegram.tgnet.TLRPC$Message r30, org.telegram.tgnet.TLObject r31, final org.telegram.messenger.MessageObject r32, java.lang.String r33, org.telegram.tgnet.TLObject r34) {
        /*
            Method dump skipped, instructions count: 923
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$performSendMessageRequest$59(boolean, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, java.lang.String, org.telegram.tgnet.TLObject):void");
    }

    public /* synthetic */ void lambda$performSendMessageRequest$51(TLRPC$TL_updateShortSentMessage tLRPC$TL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateShortSentMessage.pts, tLRPC$TL_updateShortSentMessage.date, tLRPC$TL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$52(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$53(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$54(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$56(ArrayList arrayList, final MessageObject messageObject, final TLRPC$Message tLRPC$Message, final int i, final boolean z, String str) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequest$55(messageObject, tLRPC$Message, i, z);
            }
        });
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$55(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$58(final TLRPC$Message tLRPC$Message, final int i, final boolean z, ArrayList arrayList, final int i2, String str) {
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message.random_id, MessageObject.getPeerId(tLRPC$Message.peer_id), Integer.valueOf(i), tLRPC$Message.id, 0, false, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda47
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequest$57(tLRPC$Message, i, i2, z);
            }
        });
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$57(TLRPC$Message tLRPC$Message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), 0L, Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$62(final TLRPC$Message tLRPC$Message) {
        final int i = tLRPC$Message.id;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$performSendMessageRequest$61(tLRPC$Message, i);
            }
        });
    }

    public /* synthetic */ void lambda$performSendMessageRequest$61(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARN: Removed duplicated region for block: B:138:0x02d6  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00da  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00f5  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0109  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r20, org.telegram.tgnet.TLRPC$Message r21, int r22, java.lang.String r23, boolean r24) {
        /*
            Method dump skipped, instructions count: 1714
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.updateMediaPaths(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Message, int, java.lang.String, boolean):void");
    }

    private void putToDelayedMessages(String str, DelayedMessage delayedMessage) {
        ArrayList<DelayedMessage> arrayList = this.delayedMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.delayedMessages.put(str, arrayList);
        }
        arrayList.add(delayedMessage);
    }

    public ArrayList<DelayedMessage> getDelayedMessages(String str) {
        return this.delayedMessages.get(str);
    }

    public long getNextRandomId() {
        long j = 0;
        while (j == 0) {
            j = Utilities.random.nextLong();
        }
        return j;
    }

    public void checkUnsentMessages() {
        getMessagesStorage().getUnsentMessages(1000);
    }

    public void processUnsentMessages(final ArrayList<TLRPC$Message> arrayList, final ArrayList<TLRPC$Message> arrayList2, final ArrayList<TLRPC$User> arrayList3, final ArrayList<TLRPC$Chat> arrayList4, final ArrayList<TLRPC$EncryptedChat> arrayList5) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$processUnsentMessages$63(arrayList3, arrayList4, arrayList5, arrayList, arrayList2);
            }
        });
    }

    public /* synthetic */ void lambda$processUnsentMessages$63(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        HashMap<String, String> hashMap;
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        int size = arrayList4.size();
        for (int i = 0; i < size; i++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList4.get(i), false, true);
            long groupId = messageObject.getGroupId();
            if (groupId != 0 && (hashMap = messageObject.messageOwner.params) != null && !hashMap.containsKey("final") && (i == size - 1 || ((TLRPC$Message) arrayList4.get(i + 1)).grouped_id != groupId)) {
                messageObject.messageOwner.params.put("final", "1");
            }
            retrySendMessage(messageObject, true);
        }
        if (arrayList5 != null) {
            for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList5.get(i2), false, true);
                messageObject2.scheduled = true;
                retrySendMessage(messageObject2, true);
            }
        }
    }

    public ImportingStickers getImportingStickers(String str) {
        return this.importingStickersMap.get(str);
    }

    public ImportingHistory getImportingHistory(long j) {
        return this.importingHistoryMap.get(j);
    }

    public boolean isImportingStickers() {
        return this.importingStickersMap.size() != 0;
    }

    public boolean isImportingHistory() {
        return this.importingHistoryMap.size() != 0;
    }

    public void prepareImportHistory(final long j, final Uri uri, final ArrayList<Uri> arrayList, final MessagesStorage.LongCallback longCallback) {
        if (this.importingHistoryMap.get(j) != null) {
            longCallback.run(0L);
            return;
        }
        if (DialogObject.isChatDialog(j)) {
            long j2 = -j;
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(j2));
            if (chat != null && !chat.megagroup) {
                getMessagesController().convertToMegaGroup(null, j2, null, new MessagesStorage.LongCallback() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda76
                    @Override // org.telegram.messenger.MessagesStorage.LongCallback
                    public final void run(long j3) {
                        SendMessagesHelper.this.lambda$prepareImportHistory$64(uri, arrayList, longCallback, j3);
                    }
                });
                return;
            }
        }
        new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$prepareImportHistory$69(arrayList, j, uri, longCallback);
            }
        }).start();
    }

    public /* synthetic */ void lambda$prepareImportHistory$64(Uri uri, ArrayList arrayList, MessagesStorage.LongCallback longCallback, long j) {
        if (j != 0) {
            prepareImportHistory(-j, uri, arrayList, longCallback);
        } else {
            longCallback.run(0L);
        }
    }

    public /* synthetic */ void lambda$prepareImportHistory$69(ArrayList arrayList, final long j, Uri uri, final MessagesStorage.LongCallback longCallback) {
        ArrayList arrayList2 = arrayList != null ? arrayList : new ArrayList();
        final ImportingHistory importingHistory = new ImportingHistory();
        importingHistory.mediaPaths = arrayList2;
        importingHistory.dialogId = j;
        importingHistory.peer = getMessagesController().getInputPeer(j);
        final HashMap hashMap = new HashMap();
        int i = 0;
        int size = arrayList2.size();
        while (i < size + 1) {
            Uri uri2 = i == 0 ? uri : (Uri) arrayList2.get(i - 1);
            if (uri2 != null && !AndroidUtilities.isInternalUri(uri2)) {
                String copyFileToCache = MediaController.copyFileToCache(uri2, "txt");
                if (copyFileToCache == null) {
                    continue;
                } else {
                    File file = new File(copyFileToCache);
                    if (file.exists()) {
                        long length = file.length();
                        if (length != 0) {
                            importingHistory.totalSize += length;
                            if (i != 0) {
                                importingHistory.uploadMedia.add(copyFileToCache);
                            } else if (length > 33554432) {
                                file.delete();
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda19
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        SendMessagesHelper.lambda$prepareImportHistory$67(MessagesStorage.LongCallback.this);
                                    }
                                });
                                return;
                            } else {
                                importingHistory.historyPath = copyFileToCache;
                            }
                            importingHistory.uploadSet.add(copyFileToCache);
                            hashMap.put(copyFileToCache, importingHistory);
                        }
                    }
                    if (i == 0) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda18
                            @Override // java.lang.Runnable
                            public final void run() {
                                MessagesStorage.LongCallback.this.run(0L);
                            }
                        });
                        return;
                    }
                }
            } else if (i == 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.LongCallback.this.run(0L);
                    }
                });
                return;
            }
            i++;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$prepareImportHistory$68(hashMap, j, importingHistory, longCallback);
            }
        });
    }

    public static /* synthetic */ void lambda$prepareImportHistory$67(MessagesStorage.LongCallback longCallback) {
        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("ImportFileTooLarge", org.telegram.messenger.beta.R.string.ImportFileTooLarge), 0).show();
        longCallback.run(0L);
    }

    public /* synthetic */ void lambda$prepareImportHistory$68(HashMap hashMap, long j, ImportingHistory importingHistory, MessagesStorage.LongCallback longCallback) {
        this.importingHistoryFiles.putAll(hashMap);
        this.importingHistoryMap.put(j, importingHistory);
        getFileLoader().uploadFile(importingHistory.historyPath, false, true, 0L, ConnectionsManager.FileTypeFile, true);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(j));
        longCallback.run(j);
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, ImportingService.class));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void prepareImportStickers(final String str, final String str2, final String str3, final ArrayList<ImportingSticker> arrayList, final MessagesStorage.StringCallback stringCallback) {
        if (this.importingStickersMap.get(str2) != null) {
            stringCallback.run(null);
        } else {
            new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.lambda$prepareImportStickers$72(str, str2, str3, arrayList, stringCallback);
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$prepareImportStickers$72(String str, final String str2, String str3, ArrayList arrayList, final MessagesStorage.StringCallback stringCallback) {
        final ImportingStickers importingStickers = new ImportingStickers();
        importingStickers.title = str;
        importingStickers.shortName = str2;
        importingStickers.software = str3;
        final HashMap hashMap = new HashMap();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ImportingSticker importingSticker = (ImportingSticker) arrayList.get(i);
            File file = new File(importingSticker.path);
            if (file.exists()) {
                long length = file.length();
                if (length != 0) {
                    importingStickers.totalSize += length;
                    importingStickers.uploadMedia.add(importingSticker);
                    importingStickers.uploadSet.put(importingSticker.path, importingSticker);
                    hashMap.put(importingSticker.path, importingStickers);
                }
            }
            if (i == 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda20
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.StringCallback.this.run(null);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.lambda$prepareImportStickers$71(importingStickers, hashMap, str2, stringCallback);
            }
        });
    }

    public /* synthetic */ void lambda$prepareImportStickers$71(ImportingStickers importingStickers, HashMap hashMap, String str, MessagesStorage.StringCallback stringCallback) {
        if (importingStickers.uploadMedia.get(0).item != null) {
            importingStickers.startImport();
        } else {
            this.importingStickersFiles.putAll(hashMap);
            this.importingStickersMap.put(str, importingStickers);
            importingStickers.initImport();
            getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, str);
            stringCallback.run(str);
        }
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, ImportingService.class));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public TLRPC$TL_photo generatePhotoSizes(String str, Uri uri) {
        return generatePhotoSizes(null, str, uri);
    }

    public TLRPC$TL_photo generatePhotoSizes(TLRPC$TL_photo tLRPC$TL_photo, String str, Uri uri) {
        Bitmap loadBitmap = ImageLoader.loadBitmap(str, uri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
        if (loadBitmap == null) {
            loadBitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
        }
        ArrayList<TLRPC$PhotoSize> arrayList = new ArrayList<>();
        TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, true);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        TLRPC$PhotoSize scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(loadBitmap, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true, 80, false, (int) FileLoader.MEDIA_DIR_VIDEO_PUBLIC, (int) FileLoader.MEDIA_DIR_VIDEO_PUBLIC);
        if (scaleAndSaveImage2 != null) {
            arrayList.add(scaleAndSaveImage2);
        }
        if (loadBitmap != null) {
            loadBitmap.recycle();
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        getUserConfig().saveConfig(false);
        if (tLRPC$TL_photo == null) {
            tLRPC$TL_photo = new TLRPC$TL_photo();
        }
        tLRPC$TL_photo.date = getConnectionsManager().getCurrentTime();
        tLRPC$TL_photo.sizes = arrayList;
        tLRPC$TL_photo.file_reference = new byte[0];
        return tLRPC$TL_photo;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Can't wrap try/catch for region: R(28:(5:308|61|315|62|(10:64|323|65|66|317|67|68|319|69|70)(1:76))|(3:321|78|(28:80|81|(25:83|313|88|91|(6:124|(1:126)|127|(1:129)|130|(1:132))(1:133)|(2:135|(18:137|144|(1:173)(7:147|(1:149)(1:150)|151|(1:156)(1:155)|(1:170)(4:161|(1:163)(1:164)|165|(2:169|172))|171|172)|174|(11:176|(1:178)|179|(3:181|182|202)(1:213)|(3:327|220|(10:222|223|329|224|(1:226)|227|233|(1:252)(8:236|309|237|238|311|239|244|(2:251|254))|253|254))|232|233|(0)|252|253|254)(1:255)|(1:257)(1:258)|259|(1:261)|262|(1:265)|(1:267)|268|(2:270|(2:275|(2:284|(1:286)(1:287))(1:283))(4:274|(1:302)(2:(1:297)(1:296)|(2:299|(1:301)))|303|304))(1:288)|289|(0)|302|303|304)(2:(1:139)(1:140)|141))(1:142)|143|144|(0)|173|174|(0)(0)|(0)(0)|259|(0)|262|(1:265)|(0)|268|(0)(0)|289|(0)|302|303|304)|87|313|88|91|(0)(0)|(0)(0)|143|144|(0)|173|174|(0)(0)|(0)(0)|259|(0)|262|(0)|(0)|268|(0)(0)|289|(0)|302|303|304))|86|87|313|88|91|(0)(0)|(0)(0)|143|144|(0)|173|174|(0)(0)|(0)(0)|259|(0)|262|(0)|(0)|268|(0)(0)|289|(0)|302|303|304) */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0058, code lost:
        if (r3 == false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x0149, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x014a, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:116:0x019f  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x01b7  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x01c3  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x01e5  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x01e8  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0226  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x022c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:176:0x02ea  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x03f6 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:255:0x046d  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x0477  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x047d  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0487  */
    /* JADX WARN: Removed duplicated region for block: B:264:0x0490 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:267:0x0499  */
    /* JADX WARN: Removed duplicated region for block: B:270:0x04a5  */
    /* JADX WARN: Removed duplicated region for block: B:288:0x04fe  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x0503 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:306:0x0189 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:325:0x0175 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int prepareSendingDocumentInternal(final org.telegram.messenger.AccountInstance r32, java.lang.String r33, java.lang.String r34, android.net.Uri r35, java.lang.String r36, final long r37, final org.telegram.messenger.MessageObject r39, final org.telegram.messenger.MessageObject r40, java.lang.CharSequence r41, final java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r42, final org.telegram.messenger.MessageObject r43, long[] r44, boolean r45, boolean r46, final boolean r47, final int r48, java.lang.Integer[] r49) {
        /*
            Method dump skipped, instructions count: 1428
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, long[], boolean, boolean, boolean, int, java.lang.Integer[]):int");
    }

    public static /* synthetic */ void lambda$prepareSendingDocumentInternal$73(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, null, null, tLRPC$TL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, null, str, j, messageObject2, messageObject3, str3, arrayList, null, hashMap, z, i, 0, str2, null);
        }
    }

    private static boolean checkFileSize(AccountInstance accountInstance, Uri uri) {
        long j = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                AssetFileDescriptor openAssetFileDescriptor = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri, "r", null);
                if (openAssetFileDescriptor != null) {
                    openAssetFileDescriptor.getLength();
                }
                Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_size"}, null, null, null);
                int columnIndex = query.getColumnIndex("_size");
                query.moveToFirst();
                j = query.getLong(columnIndex);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return !FileLoader.checkUploadFileSize(accountInstance.getCurrentAccount(), j);
    }

    public static void prepareSendingDocument(AccountInstance accountInstance, String str, String str2, Uri uri, String str3, String str4, long j, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject3, boolean z, int i) {
        if ((str == null || str2 == null) && uri == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = null;
        if (uri != null) {
            arrayList3 = new ArrayList();
            arrayList3.add(uri);
        }
        if (str != null) {
            arrayList.add(str);
            arrayList2.add(str2);
        }
        prepareSendingDocuments(accountInstance, arrayList, arrayList2, arrayList3, str3, str4, j, messageObject, messageObject2, inputContentInfoCompat, messageObject3, z, i);
    }

    public static void prepareSendingAudioDocuments(final AccountInstance accountInstance, final ArrayList<MessageObject> arrayList, final String str, final long j, final MessageObject messageObject, final MessageObject messageObject2, final MessageObject messageObject3, final boolean z, final int i) {
        new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingAudioDocuments$75(arrayList, j, accountInstance, str, messageObject3, messageObject, messageObject2, z, i);
            }
        }).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x008f  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00b0  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00b7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$75(java.util.ArrayList r22, final long r23, final org.telegram.messenger.AccountInstance r25, java.lang.String r26, final org.telegram.messenger.MessageObject r27, final org.telegram.messenger.MessageObject r28, final org.telegram.messenger.MessageObject r29, final boolean r30, final int r31) {
        /*
            Method dump skipped, instructions count: 254
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$75(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$74(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, MessageObject messageObject4, String str2, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, null, null, tLRPC$TL_document, messageObject2.messageOwner.attachPath, hashMap, false, str);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, null, messageObject2.messageOwner.attachPath, j, messageObject3, messageObject4, str2, null, null, hashMap, z, i, 0, str, null);
        }
    }

    private static void finishGroup(final AccountInstance accountInstance, final long j, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$finishGroup$76(AccountInstance.this, j, i);
            }
        });
    }

    public static /* synthetic */ void lambda$finishGroup$76(AccountInstance accountInstance, long j, int i) {
        SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
        HashMap<String, ArrayList<DelayedMessage>> hashMap = sendMessagesHelper.delayedMessages;
        ArrayList<DelayedMessage> arrayList = hashMap.get("group_" + j);
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        DelayedMessage delayedMessage = arrayList.get(0);
        ArrayList<MessageObject> arrayList2 = delayedMessage.messageObjects;
        MessageObject messageObject = arrayList2.get(arrayList2.size() - 1);
        delayedMessage.finalGroupMessage = messageObject.getId();
        messageObject.messageOwner.params.put("final", "1");
        TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
        tLRPC$TL_messages_messages.messages.add(messageObject.messageOwner);
        accountInstance.getMessagesStorage().putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, delayedMessage.peer, -2, 0, false, i != 0);
        sendMessagesHelper.sendReadyToSendGroup(delayedMessage, true, true);
    }

    public static void prepareSendingDocuments(final AccountInstance accountInstance, final ArrayList<String> arrayList, final ArrayList<String> arrayList2, final ArrayList<Uri> arrayList3, final String str, final String str2, final long j, final MessageObject messageObject, final MessageObject messageObject2, final InputContentInfoCompat inputContentInfoCompat, final MessageObject messageObject3, final boolean z, final int i) {
        if (arrayList == null && arrayList2 == null && arrayList3 == null) {
            return;
        }
        if (arrayList != null && arrayList2 != null && arrayList.size() != arrayList2.size()) {
            return;
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingDocuments$77(j, arrayList, str, accountInstance, i, arrayList2, str2, messageObject, messageObject2, messageObject3, inputContentInfoCompat, z, arrayList3);
            }
        });
    }

    public static /* synthetic */ void lambda$prepareSendingDocuments$77(long j, ArrayList arrayList, String str, AccountInstance accountInstance, int i, ArrayList arrayList2, String str2, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, InputContentInfoCompat inputContentInfoCompat, boolean z, ArrayList arrayList3) {
        long[] jArr;
        Integer[] numArr;
        ArrayList arrayList4;
        int i2;
        AccountInstance accountInstance2 = accountInstance;
        int i3 = i;
        int i4 = 1;
        long[] jArr2 = new long[1];
        Integer[] numArr2 = new Integer[1];
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(j);
        int i5 = 10;
        if (arrayList != null) {
            int size = arrayList.size();
            i2 = 0;
            int i6 = 0;
            int i7 = 0;
            while (i7 < size) {
                String str3 = i7 == 0 ? str : null;
                if (!isEncryptedDialog && size > i4 && i6 % 10 == 0) {
                    if (jArr2[0] != 0) {
                        finishGroup(accountInstance2, jArr2[0], i3);
                    }
                    jArr2[0] = Utilities.random.nextLong();
                    i6 = 0;
                }
                int i8 = i6 + 1;
                long j2 = jArr2[0];
                int i9 = i7;
                int i10 = size;
                Integer[] numArr3 = numArr2;
                long[] jArr3 = jArr2;
                i2 = prepareSendingDocumentInternal(accountInstance, (String) arrayList.get(i7), (String) arrayList2.get(i7), null, str2, j, messageObject, messageObject2, str3, null, messageObject3, jArr3, i8 == i5 || i7 == size + (-1), inputContentInfoCompat == null, z, i, numArr3);
                i6 = (j2 != jArr3[0] || jArr3[0] == -1) ? 1 : i8;
                i7 = i9 + 1;
                accountInstance2 = accountInstance;
                i3 = i;
                size = i10;
                numArr2 = numArr3;
                jArr2 = jArr3;
                i5 = 10;
                i4 = 1;
            }
            numArr = numArr2;
            jArr = jArr2;
            arrayList4 = arrayList3;
        } else {
            numArr = numArr2;
            jArr = jArr2;
            arrayList4 = arrayList3;
            i2 = 0;
        }
        if (arrayList4 != null) {
            jArr[0] = 0;
            int size2 = arrayList3.size();
            int i11 = 0;
            int i12 = 0;
            while (i12 < arrayList3.size()) {
                String str4 = (i12 == 0 && (arrayList == null || arrayList.size() == 0)) ? str : null;
                if (!isEncryptedDialog) {
                    if (size2 > 1 && i11 % 10 == 0) {
                        if (jArr[0] != 0) {
                            finishGroup(accountInstance, jArr[0], i);
                        }
                        jArr[0] = Utilities.random.nextLong();
                        i11 = 0;
                    }
                }
                int i13 = i11 + 1;
                long j3 = jArr[0];
                int i14 = i12;
                int i15 = size2;
                i2 = prepareSendingDocumentInternal(accountInstance, null, null, (Uri) arrayList4.get(i12), str2, j, messageObject, messageObject2, str4, null, messageObject3, jArr, i13 == 10 || i12 == size2 + (-1), inputContentInfoCompat == null, z, i, numArr);
                i11 = (j3 != jArr[0] || jArr[0] == -1) ? 1 : i13;
                i12 = i14 + 1;
                arrayList4 = arrayList3;
                size2 = i15;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        handleError(i2, accountInstance);
    }

    private static void handleError(final int i, final AccountInstance accountInstance) {
        if (i != 0) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.lambda$handleError$78(i, accountInstance);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$handleError$78(int i, AccountInstance accountInstance) {
        try {
            if (i == 1) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.getString("UnsupportedAttachment", org.telegram.messenger.beta.R.string.UnsupportedAttachment));
            } else if (i != 2) {
            } else {
                NotificationCenter.getInstance(accountInstance.getCurrentAccount()).postNotificationName(NotificationCenter.currentUserShowLimitReachedDialog, 6);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, boolean z, int i2) {
        prepareSendingPhoto(accountInstance, str, null, uri, j, messageObject, messageObject2, charSequence, arrayList, arrayList2, inputContentInfoCompat, i, messageObject3, null, z, i2, false);
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, String str2, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
        SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
        sendingMediaInfo.path = str;
        sendingMediaInfo.thumbPath = str2;
        sendingMediaInfo.uri = uri;
        if (charSequence != null) {
            sendingMediaInfo.caption = charSequence.toString();
        }
        sendingMediaInfo.entities = arrayList;
        sendingMediaInfo.ttl = i;
        if (arrayList2 != null) {
            sendingMediaInfo.masks = new ArrayList<>(arrayList2);
        }
        sendingMediaInfo.videoEditedInfo = videoEditedInfo;
        ArrayList arrayList3 = new ArrayList();
        arrayList3.add(sendingMediaInfo);
        prepareSendingMedia(accountInstance, arrayList3, j, messageObject, messageObject2, inputContentInfoCompat, z2, false, messageObject3, z, i2);
    }

    public static void prepareSendingBotContextResult(final AccountInstance accountInstance, final TLRPC$BotInlineResult tLRPC$BotInlineResult, final HashMap<String, String> hashMap, final long j, final MessageObject messageObject, final MessageObject messageObject2, final boolean z, final int i) {
        if (tLRPC$BotInlineResult == null) {
            return;
        }
        TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult.send_message;
        if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaAuto) {
            new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.lambda$prepareSendingBotContextResult$80(j, tLRPC$BotInlineResult, accountInstance, hashMap, messageObject, messageObject2, z, i);
                }
            }).run();
        } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageText) {
            TLRPC$TL_webPagePending tLRPC$TL_webPagePending = null;
            if (DialogObject.isEncryptedDialog(j)) {
                int i2 = 0;
                while (true) {
                    if (i2 >= tLRPC$BotInlineResult.send_message.entities.size()) {
                        break;
                    }
                    TLRPC$MessageEntity tLRPC$MessageEntity = tLRPC$BotInlineResult.send_message.entities.get(i2);
                    if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUrl) {
                        tLRPC$TL_webPagePending = new TLRPC$TL_webPagePending();
                        String str = tLRPC$BotInlineResult.send_message.message;
                        int i3 = tLRPC$MessageEntity.offset;
                        tLRPC$TL_webPagePending.url = str.substring(i3, tLRPC$MessageEntity.length + i3);
                        break;
                    }
                    i2++;
                }
            }
            TLRPC$TL_webPagePending tLRPC$TL_webPagePending2 = tLRPC$TL_webPagePending;
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage2 = tLRPC$BotInlineResult.send_message;
            sendMessagesHelper.sendMessage(tLRPC$BotInlineMessage2.message, j, messageObject, messageObject2, tLRPC$TL_webPagePending2, !tLRPC$BotInlineMessage2.no_webpage, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i, null);
        } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaVenue) {
            TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue = new TLRPC$TL_messageMediaVenue();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage3 = tLRPC$BotInlineResult.send_message;
            tLRPC$TL_messageMediaVenue.geo = tLRPC$BotInlineMessage3.geo;
            tLRPC$TL_messageMediaVenue.address = tLRPC$BotInlineMessage3.address;
            tLRPC$TL_messageMediaVenue.title = tLRPC$BotInlineMessage3.title;
            tLRPC$TL_messageMediaVenue.provider = tLRPC$BotInlineMessage3.provider;
            tLRPC$TL_messageMediaVenue.venue_id = tLRPC$BotInlineMessage3.venue_id;
            String str2 = tLRPC$BotInlineMessage3.venue_type;
            tLRPC$TL_messageMediaVenue.venue_id = str2;
            tLRPC$TL_messageMediaVenue.venue_type = str2;
            if (str2 == null) {
                tLRPC$TL_messageMediaVenue.venue_type = "";
            }
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_messageMediaVenue, j, messageObject, messageObject2, tLRPC$BotInlineResult.send_message.reply_markup, hashMap, z, i);
        } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaGeo) {
            if (tLRPC$BotInlineMessage.period != 0 || tLRPC$BotInlineMessage.proximity_notification_radius != 0) {
                TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
                TLRPC$BotInlineMessage tLRPC$BotInlineMessage4 = tLRPC$BotInlineResult.send_message;
                int i4 = tLRPC$BotInlineMessage4.period;
                if (i4 == 0) {
                    i4 = 900;
                }
                tLRPC$TL_messageMediaGeoLive.period = i4;
                tLRPC$TL_messageMediaGeoLive.geo = tLRPC$BotInlineMessage4.geo;
                tLRPC$TL_messageMediaGeoLive.heading = tLRPC$BotInlineMessage4.heading;
                tLRPC$TL_messageMediaGeoLive.proximity_notification_radius = tLRPC$BotInlineMessage4.proximity_notification_radius;
                accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_messageMediaGeoLive, j, messageObject, messageObject2, tLRPC$BotInlineResult.send_message.reply_markup, hashMap, z, i);
                return;
            }
            TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage5 = tLRPC$BotInlineResult.send_message;
            tLRPC$TL_messageMediaGeo.geo = tLRPC$BotInlineMessage5.geo;
            tLRPC$TL_messageMediaGeo.heading = tLRPC$BotInlineMessage5.heading;
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_messageMediaGeo, j, messageObject, messageObject2, tLRPC$BotInlineResult.send_message.reply_markup, hashMap, z, i);
        } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaContact) {
            TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage6 = tLRPC$BotInlineResult.send_message;
            tLRPC$TL_user.phone = tLRPC$BotInlineMessage6.phone_number;
            tLRPC$TL_user.first_name = tLRPC$BotInlineMessage6.first_name;
            tLRPC$TL_user.last_name = tLRPC$BotInlineMessage6.last_name;
            TLRPC$TL_restrictionReason tLRPC$TL_restrictionReason = new TLRPC$TL_restrictionReason();
            tLRPC$TL_restrictionReason.text = tLRPC$BotInlineResult.send_message.vcard;
            tLRPC$TL_restrictionReason.platform = "";
            tLRPC$TL_restrictionReason.reason = "";
            tLRPC$TL_user.restriction_reason.add(tLRPC$TL_restrictionReason);
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_user, j, messageObject, messageObject2, tLRPC$BotInlineResult.send_message.reply_markup, hashMap, z, i);
        } else if (!(tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaInvoice) || DialogObject.isEncryptedDialog(j)) {
        } else {
            TLRPC$TL_botInlineMessageMediaInvoice tLRPC$TL_botInlineMessageMediaInvoice = (TLRPC$TL_botInlineMessageMediaInvoice) tLRPC$BotInlineResult.send_message;
            TLRPC$TL_messageMediaInvoice tLRPC$TL_messageMediaInvoice = new TLRPC$TL_messageMediaInvoice();
            tLRPC$TL_messageMediaInvoice.shipping_address_requested = tLRPC$TL_botInlineMessageMediaInvoice.shipping_address_requested;
            tLRPC$TL_messageMediaInvoice.test = tLRPC$TL_botInlineMessageMediaInvoice.test;
            tLRPC$TL_messageMediaInvoice.title = tLRPC$TL_botInlineMessageMediaInvoice.title;
            tLRPC$TL_messageMediaInvoice.description = tLRPC$TL_botInlineMessageMediaInvoice.description;
            TLRPC$WebDocument tLRPC$WebDocument = tLRPC$TL_botInlineMessageMediaInvoice.photo;
            if (tLRPC$WebDocument != null) {
                tLRPC$TL_messageMediaInvoice.photo = tLRPC$WebDocument;
                tLRPC$TL_messageMediaInvoice.flags |= 1;
            }
            tLRPC$TL_messageMediaInvoice.currency = tLRPC$TL_botInlineMessageMediaInvoice.currency;
            tLRPC$TL_messageMediaInvoice.total_amount = tLRPC$TL_botInlineMessageMediaInvoice.total_amount;
            tLRPC$TL_messageMediaInvoice.start_param = "";
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_messageMediaInvoice, j, messageObject, messageObject2, tLRPC$BotInlineResult.send_message.reply_markup, hashMap, z, i);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$prepareSendingBotContextResult$80(final long j, final TLRPC$BotInlineResult tLRPC$BotInlineResult, final AccountInstance accountInstance, final HashMap hashMap, final MessageObject messageObject, final MessageObject messageObject2, final boolean z, final int i) {
        final TLRPC$TL_game tLRPC$TL_game;
        final TLRPC$TL_photo tLRPC$TL_photo;
        TLRPC$TL_photo tLRPC$TL_photo2;
        TLRPC$TL_document tLRPC$TL_document;
        TLRPC$WebDocument tLRPC$WebDocument;
        String str;
        String str2;
        char c;
        char c2;
        String str3;
        Object obj;
        Bitmap bitmap;
        String str4;
        int lastIndexOf;
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(j);
        if (!"game".equals(tLRPC$BotInlineResult.type)) {
            if (tLRPC$BotInlineResult instanceof TLRPC$TL_botInlineMediaResult) {
                TLRPC$Document tLRPC$Document = tLRPC$BotInlineResult.document;
                if (tLRPC$Document != null) {
                    if (tLRPC$Document instanceof TLRPC$TL_document) {
                        tLRPC$TL_document = (TLRPC$TL_document) tLRPC$Document;
                        tLRPC$TL_photo2 = null;
                    }
                } else {
                    TLRPC$Photo tLRPC$Photo = tLRPC$BotInlineResult.photo;
                    if (tLRPC$Photo != null && (tLRPC$Photo instanceof TLRPC$TL_photo)) {
                        tLRPC$TL_photo = (TLRPC$TL_photo) tLRPC$Photo;
                        tLRPC$TL_document = null;
                        tLRPC$TL_photo2 = null;
                        tLRPC$TL_game = 0;
                    }
                }
                tLRPC$TL_document = null;
                tLRPC$TL_photo2 = null;
            } else {
                TLRPC$WebDocument tLRPC$WebDocument2 = tLRPC$BotInlineResult.content;
                if (tLRPC$WebDocument2 != null) {
                    String httpUrlExtension = ImageLoader.getHttpUrlExtension(tLRPC$WebDocument2.url, null);
                    if (TextUtils.isEmpty(httpUrlExtension)) {
                        str = FileLoader.getExtensionByMimeType(tLRPC$BotInlineResult.content.mime_type);
                    } else {
                        str = "." + httpUrlExtension;
                    }
                    File file = new File(FileLoader.getDirectory(4), Utilities.MD5(tLRPC$BotInlineResult.content.url) + str);
                    if (file.exists()) {
                        str2 = file.getAbsolutePath();
                    } else {
                        str2 = tLRPC$BotInlineResult.content.url;
                    }
                    String str5 = str2;
                    String str6 = tLRPC$BotInlineResult.type;
                    str6.hashCode();
                    switch (str6.hashCode()) {
                        case -1890252483:
                            if (str6.equals("sticker")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 102340:
                            if (str6.equals("gif")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 3143036:
                            if (str6.equals("file")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 93166550:
                            if (str6.equals(MediaStreamTrack.AUDIO_TRACK_KIND)) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 106642994:
                            if (str6.equals("photo")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case 112202875:
                            if (str6.equals(MediaStreamTrack.VIDEO_TRACK_KIND)) {
                                c = 5;
                                break;
                            }
                            c = 65535;
                            break;
                        case 112386354:
                            if (str6.equals("voice")) {
                                c = 6;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 5:
                        case 6:
                            TLRPC$TL_document tLRPC$TL_document2 = new TLRPC$TL_document();
                            tLRPC$TL_document2.id = 0L;
                            tLRPC$TL_document2.size = 0L;
                            tLRPC$TL_document2.dc_id = 0;
                            tLRPC$TL_document2.mime_type = tLRPC$BotInlineResult.content.mime_type;
                            tLRPC$TL_document2.file_reference = new byte[0];
                            tLRPC$TL_document2.date = accountInstance.getConnectionsManager().getCurrentTime();
                            TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
                            tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeFilename);
                            String str7 = tLRPC$BotInlineResult.type;
                            str7.hashCode();
                            switch (str7.hashCode()) {
                                case -1890252483:
                                    str3 = "file";
                                    if (str7.equals("sticker")) {
                                        c2 = 0;
                                        break;
                                    }
                                    c2 = 65535;
                                    break;
                                case 102340:
                                    str3 = "file";
                                    if (str7.equals("gif")) {
                                        c2 = 1;
                                        break;
                                    }
                                    c2 = 65535;
                                    break;
                                case 3143036:
                                    str3 = "file";
                                    if (str7.equals(str3)) {
                                        c2 = 2;
                                        break;
                                    }
                                    c2 = 65535;
                                    break;
                                case 93166550:
                                    if (str7.equals(MediaStreamTrack.AUDIO_TRACK_KIND)) {
                                        str3 = "file";
                                        c2 = 3;
                                        break;
                                    }
                                    str3 = "file";
                                    c2 = 65535;
                                    break;
                                case 112202875:
                                    if (str7.equals(MediaStreamTrack.VIDEO_TRACK_KIND)) {
                                        str3 = "file";
                                        c2 = 4;
                                        break;
                                    }
                                    str3 = "file";
                                    c2 = 65535;
                                    break;
                                case 112386354:
                                    if (str7.equals("voice")) {
                                        str3 = "file";
                                        c2 = 5;
                                        break;
                                    }
                                    str3 = "file";
                                    c2 = 65535;
                                    break;
                                default:
                                    str3 = "file";
                                    c2 = 65535;
                                    break;
                            }
                            int i2 = 55;
                            switch (c2) {
                                case 0:
                                    TLRPC$TL_documentAttributeSticker tLRPC$TL_documentAttributeSticker = new TLRPC$TL_documentAttributeSticker();
                                    tLRPC$TL_documentAttributeSticker.alt = "";
                                    tLRPC$TL_documentAttributeSticker.stickerset = new TLRPC$TL_inputStickerSetEmpty();
                                    tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeSticker);
                                    TLRPC$TL_documentAttributeImageSize tLRPC$TL_documentAttributeImageSize = new TLRPC$TL_documentAttributeImageSize();
                                    int[] inlineResultWidthAndHeight = MessageObject.getInlineResultWidthAndHeight(tLRPC$BotInlineResult);
                                    tLRPC$TL_documentAttributeImageSize.w = inlineResultWidthAndHeight[0];
                                    tLRPC$TL_documentAttributeImageSize.h = inlineResultWidthAndHeight[1];
                                    tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeImageSize);
                                    tLRPC$TL_documentAttributeFilename.file_name = "sticker.webp";
                                    try {
                                        if (tLRPC$BotInlineResult.thumb != null) {
                                            Bitmap loadBitmap = ImageLoader.loadBitmap(new File(FileLoader.getDirectory(4), Utilities.MD5(tLRPC$BotInlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(tLRPC$BotInlineResult.thumb.url, "webp")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                            if (loadBitmap != null) {
                                                TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, false);
                                                if (scaleAndSaveImage != null) {
                                                    tLRPC$TL_document2.thumbs.add(scaleAndSaveImage);
                                                    tLRPC$TL_document2.flags |= 1;
                                                }
                                                loadBitmap.recycle();
                                                break;
                                            }
                                        }
                                    } catch (Throwable th) {
                                        FileLog.e(th);
                                        break;
                                    }
                                    break;
                                case 1:
                                    tLRPC$TL_documentAttributeFilename.file_name = "animation.gif";
                                    if (str5.endsWith("mp4")) {
                                        tLRPC$TL_document2.mime_type = "video/mp4";
                                        tLRPC$TL_document2.attributes.add(new TLRPC$TL_documentAttributeAnimated());
                                    } else {
                                        tLRPC$TL_document2.mime_type = "image/gif";
                                    }
                                    int i3 = isEncryptedDialog ? 90 : 320;
                                    try {
                                        if (str5.endsWith("mp4")) {
                                            bitmap = createVideoThumbnail(str5, 1);
                                            if (bitmap == null) {
                                                TLRPC$WebDocument tLRPC$WebDocument3 = tLRPC$BotInlineResult.thumb;
                                                if ((tLRPC$WebDocument3 instanceof TLRPC$TL_webDocument) && "video/mp4".equals(tLRPC$WebDocument3.mime_type)) {
                                                    String httpUrlExtension2 = ImageLoader.getHttpUrlExtension(tLRPC$BotInlineResult.thumb.url, null);
                                                    if (TextUtils.isEmpty(httpUrlExtension2)) {
                                                        str4 = FileLoader.getExtensionByMimeType(tLRPC$BotInlineResult.thumb.mime_type);
                                                    } else {
                                                        str4 = "." + httpUrlExtension2;
                                                    }
                                                    bitmap = createVideoThumbnail(new File(FileLoader.getDirectory(4), Utilities.MD5(tLRPC$BotInlineResult.thumb.url) + str4).getAbsolutePath(), 1);
                                                }
                                            }
                                        } else {
                                            float f = i3;
                                            bitmap = ImageLoader.loadBitmap(str5, null, f, f, true);
                                        }
                                        if (bitmap != null) {
                                            float f2 = i3;
                                            if (i3 > 90) {
                                                i2 = 80;
                                            }
                                            TLRPC$PhotoSize scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(bitmap, f2, f2, i2, false);
                                            if (scaleAndSaveImage2 != null) {
                                                tLRPC$TL_document2.thumbs.add(scaleAndSaveImage2);
                                                tLRPC$TL_document2.flags |= 1;
                                            }
                                            bitmap.recycle();
                                            break;
                                        }
                                    } catch (Throwable th2) {
                                        FileLog.e(th2);
                                        break;
                                    }
                                    break;
                                case 2:
                                    if (tLRPC$BotInlineResult.content.mime_type.lastIndexOf(47) != -1) {
                                        tLRPC$TL_documentAttributeFilename.file_name = "file." + tLRPC$BotInlineResult.content.mime_type.substring(lastIndexOf + 1);
                                        break;
                                    } else {
                                        tLRPC$TL_documentAttributeFilename.file_name = str3;
                                        break;
                                    }
                                case 3:
                                    TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
                                    tLRPC$TL_documentAttributeAudio.duration = MessageObject.getInlineResultDuration(tLRPC$BotInlineResult);
                                    tLRPC$TL_documentAttributeAudio.title = tLRPC$BotInlineResult.title;
                                    int i4 = tLRPC$TL_documentAttributeAudio.flags | 1;
                                    tLRPC$TL_documentAttributeAudio.flags = i4;
                                    String str8 = tLRPC$BotInlineResult.description;
                                    if (str8 != null) {
                                        tLRPC$TL_documentAttributeAudio.performer = str8;
                                        tLRPC$TL_documentAttributeAudio.flags = i4 | 2;
                                    }
                                    tLRPC$TL_documentAttributeFilename.file_name = "audio.mp3";
                                    tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeAudio);
                                    break;
                                case 4:
                                    tLRPC$TL_documentAttributeFilename.file_name = "video.mp4";
                                    TLRPC$TL_documentAttributeVideo tLRPC$TL_documentAttributeVideo = new TLRPC$TL_documentAttributeVideo();
                                    int[] inlineResultWidthAndHeight2 = MessageObject.getInlineResultWidthAndHeight(tLRPC$BotInlineResult);
                                    tLRPC$TL_documentAttributeVideo.w = inlineResultWidthAndHeight2[0];
                                    tLRPC$TL_documentAttributeVideo.h = inlineResultWidthAndHeight2[1];
                                    tLRPC$TL_documentAttributeVideo.duration = MessageObject.getInlineResultDuration(tLRPC$BotInlineResult);
                                    tLRPC$TL_documentAttributeVideo.supports_streaming = true;
                                    tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeVideo);
                                    try {
                                        if (tLRPC$BotInlineResult.thumb != null) {
                                            Bitmap loadBitmap2 = ImageLoader.loadBitmap(new File(FileLoader.getDirectory(4), Utilities.MD5(tLRPC$BotInlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(tLRPC$BotInlineResult.thumb.url, "jpg")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                            if (loadBitmap2 != null) {
                                                TLRPC$PhotoSize scaleAndSaveImage3 = ImageLoader.scaleAndSaveImage(loadBitmap2, 90.0f, 90.0f, 55, false);
                                                if (scaleAndSaveImage3 != null) {
                                                    tLRPC$TL_document2.thumbs.add(scaleAndSaveImage3);
                                                    tLRPC$TL_document2.flags |= 1;
                                                }
                                                loadBitmap2.recycle();
                                                break;
                                            }
                                        }
                                    } catch (Throwable th3) {
                                        FileLog.e(th3);
                                        break;
                                    }
                                    break;
                                case 5:
                                    TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio2 = new TLRPC$TL_documentAttributeAudio();
                                    tLRPC$TL_documentAttributeAudio2.duration = MessageObject.getInlineResultDuration(tLRPC$BotInlineResult);
                                    tLRPC$TL_documentAttributeAudio2.voice = true;
                                    tLRPC$TL_documentAttributeFilename.file_name = "audio.ogg";
                                    tLRPC$TL_document2.attributes.add(tLRPC$TL_documentAttributeAudio2);
                                    break;
                            }
                            if (tLRPC$TL_documentAttributeFilename.file_name == null) {
                                tLRPC$TL_documentAttributeFilename.file_name = str3;
                            }
                            if (tLRPC$TL_document2.mime_type == null) {
                                tLRPC$TL_document2.mime_type = "application/octet-stream";
                            }
                            if (tLRPC$TL_document2.thumbs.isEmpty()) {
                                TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
                                int[] inlineResultWidthAndHeight3 = MessageObject.getInlineResultWidthAndHeight(tLRPC$BotInlineResult);
                                tLRPC$TL_photoSize.w = inlineResultWidthAndHeight3[0];
                                tLRPC$TL_photoSize.h = inlineResultWidthAndHeight3[1];
                                tLRPC$TL_photoSize.size = 0;
                                tLRPC$TL_photoSize.location = new TLRPC$TL_fileLocationUnavailable();
                                tLRPC$TL_photoSize.type = "x";
                                tLRPC$TL_document2.thumbs.add(tLRPC$TL_photoSize);
                                tLRPC$TL_document2.flags |= 1;
                            }
                            tLRPC$TL_photo = null;
                            obj = null;
                            tLRPC$TL_document = tLRPC$TL_document2;
                            break;
                        case 4:
                            TLRPC$TL_photo generatePhotoSizes = file.exists() ? accountInstance.getSendMessagesHelper().generatePhotoSizes(str5, null) : null;
                            if (generatePhotoSizes == null) {
                                generatePhotoSizes = new TLRPC$TL_photo();
                                generatePhotoSizes.date = accountInstance.getConnectionsManager().getCurrentTime();
                                generatePhotoSizes.file_reference = new byte[0];
                                TLRPC$TL_photoSize tLRPC$TL_photoSize2 = new TLRPC$TL_photoSize();
                                int[] inlineResultWidthAndHeight4 = MessageObject.getInlineResultWidthAndHeight(tLRPC$BotInlineResult);
                                tLRPC$TL_photoSize2.w = inlineResultWidthAndHeight4[0];
                                tLRPC$TL_photoSize2.h = inlineResultWidthAndHeight4[1];
                                tLRPC$TL_photoSize2.size = 1;
                                tLRPC$TL_photoSize2.location = new TLRPC$TL_fileLocationUnavailable();
                                tLRPC$TL_photoSize2.type = "x";
                                generatePhotoSizes.sizes.add(tLRPC$TL_photoSize2);
                            }
                            tLRPC$TL_photo = generatePhotoSizes;
                            tLRPC$TL_document = null;
                            obj = null;
                            break;
                        default:
                            tLRPC$TL_document = null;
                            tLRPC$TL_photo = null;
                            obj = null;
                            break;
                    }
                    tLRPC$TL_photo2 = str5;
                    tLRPC$TL_game = obj;
                }
                tLRPC$TL_document = null;
                tLRPC$TL_photo2 = null;
            }
            tLRPC$TL_photo = tLRPC$TL_photo2;
            tLRPC$TL_game = tLRPC$TL_photo;
        } else if (isEncryptedDialog) {
            return;
        } else {
            TLRPC$TL_game tLRPC$TL_game2 = new TLRPC$TL_game();
            tLRPC$TL_game2.title = tLRPC$BotInlineResult.title;
            tLRPC$TL_game2.description = tLRPC$BotInlineResult.description;
            tLRPC$TL_game2.short_name = tLRPC$BotInlineResult.id;
            TLRPC$Photo tLRPC$Photo2 = tLRPC$BotInlineResult.photo;
            tLRPC$TL_game2.photo = tLRPC$Photo2;
            if (tLRPC$Photo2 == null) {
                tLRPC$TL_game2.photo = new TLRPC$TL_photoEmpty();
            }
            TLRPC$Document tLRPC$Document2 = tLRPC$BotInlineResult.document;
            if (tLRPC$Document2 instanceof TLRPC$TL_document) {
                tLRPC$TL_game2.document = tLRPC$Document2;
                tLRPC$TL_game2.flags |= 1;
            }
            tLRPC$TL_game = tLRPC$TL_game2;
            tLRPC$TL_document = null;
            tLRPC$TL_photo2 = null;
            tLRPC$TL_photo = null;
        }
        if (hashMap != null && (tLRPC$WebDocument = tLRPC$BotInlineResult.content) != null) {
            hashMap.put("originalPath", tLRPC$WebDocument.url);
        }
        final Bitmap[] bitmapArr = new Bitmap[1];
        final String[] strArr = new String[1];
        if (MessageObject.isGifDocument(tLRPC$TL_document)) {
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_document.thumbs, 320);
            File pathToAttach = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(tLRPC$TL_document);
            if (!pathToAttach.exists()) {
                pathToAttach = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(tLRPC$TL_document, true);
            }
            ensureMediaThumbExists(accountInstance, isEncryptedDialog, tLRPC$TL_document, pathToAttach.getAbsolutePath(), null, 0L);
            strArr[0] = getKeyForPhotoSize(accountInstance, closestPhotoSizeWithSize, bitmapArr, true, true);
        }
        final TLRPC$TL_document tLRPC$TL_document3 = tLRPC$TL_document;
        final String str9 = tLRPC$TL_photo2;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingBotContextResult$79(TLRPC$TL_document.this, bitmapArr, strArr, accountInstance, str9, j, messageObject, messageObject2, tLRPC$BotInlineResult, hashMap, z, i, tLRPC$TL_photo, tLRPC$TL_game);
            }
        });
    }

    public static /* synthetic */ void lambda$prepareSendingBotContextResult$79(TLRPC$TL_document tLRPC$TL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap hashMap, boolean z, int i, TLRPC$TL_photo tLRPC$TL_photo, TLRPC$TL_game tLRPC$TL_game) {
        if (tLRPC$TL_document != null) {
            if (bitmapArr[0] != null && strArr[0] != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
            }
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult.send_message;
            sendMessagesHelper.sendMessage(tLRPC$TL_document, null, str, j, messageObject, messageObject2, tLRPC$BotInlineMessage.message, tLRPC$BotInlineMessage.entities, tLRPC$BotInlineMessage.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult, null);
        } else if (tLRPC$TL_photo == null) {
            if (tLRPC$TL_game == null) {
                return;
            }
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_game, j, tLRPC$BotInlineResult.send_message.reply_markup, hashMap, z, i);
        } else {
            SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
            TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.content;
            String str2 = tLRPC$WebDocument != null ? tLRPC$WebDocument.url : null;
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage2 = tLRPC$BotInlineResult.send_message;
            sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject, messageObject2, tLRPC$BotInlineMessage2.message, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult);
        }
    }

    private static String getTrimmedString(String str) {
        String trim = str.trim();
        if (trim.length() == 0) {
            return trim;
        }
        while (str.startsWith("\n")) {
            str = str.substring(1);
        }
        while (str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static /* synthetic */ void lambda$prepareSendingText$82(final String str, final AccountInstance accountInstance, final long j, final boolean z, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingText$81(str, accountInstance, j, z, i);
            }
        });
    }

    public static /* synthetic */ void lambda$prepareSendingText$83(final String str, final AccountInstance accountInstance, final long j, final boolean z, final int i) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingText$82(str, accountInstance, j, z, i);
            }
        });
    }

    public static void prepareSendingText(final AccountInstance accountInstance, final String str, final long j, final boolean z, final int i) {
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingText$83(str, accountInstance, j, z, i);
            }
        });
    }

    public static /* synthetic */ void lambda$prepareSendingText$81(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil(trimmedString.length() / 4096.0f);
            int i2 = 0;
            while (i2 < ceil) {
                int i3 = i2 * 4096;
                i2++;
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, null, null, null, true, null, null, null, z, i, null);
            }
        }
    }

    public static void ensureMediaThumbExists(AccountInstance accountInstance, boolean z, TLObject tLObject, String str, Uri uri, long j) {
        TLRPC$PhotoSize scaleAndSaveImage;
        TLRPC$PhotoSize scaleAndSaveImage2;
        if (tLObject instanceof TLRPC$TL_photo) {
            TLRPC$TL_photo tLRPC$TL_photo = (TLRPC$TL_photo) tLObject;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, 90);
            boolean exists = ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoStrippedSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoPathSize)) ? true : FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(closestPhotoSizeWithSize, true).exists();
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists2 = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(closestPhotoSizeWithSize2, false).exists();
            if (exists && exists2) {
                return;
            }
            Bitmap loadBitmap = ImageLoader.loadBitmap(str, uri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
            if (loadBitmap == null) {
                loadBitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
            }
            Bitmap bitmap = loadBitmap;
            if (!exists2 && (scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, bitmap, Bitmap.CompressFormat.JPEG, true, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, FileLoader.MEDIA_DIR_VIDEO_PUBLIC, FileLoader.MEDIA_DIR_VIDEO_PUBLIC, false)) != closestPhotoSizeWithSize2) {
                tLRPC$TL_photo.sizes.add(0, scaleAndSaveImage2);
            }
            if (!exists && (scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, bitmap, 90.0f, 90.0f, 55, true, false)) != closestPhotoSizeWithSize) {
                tLRPC$TL_photo.sizes.add(0, scaleAndSaveImage);
            }
            if (bitmap == null) {
                return;
            }
            bitmap.recycle();
        } else if (!(tLObject instanceof TLRPC$TL_document)) {
        } else {
            TLRPC$TL_document tLRPC$TL_document = (TLRPC$TL_document) tLObject;
            if ((!MessageObject.isVideoDocument(tLRPC$TL_document) && !MessageObject.isNewGifDocument(tLRPC$TL_document)) || !MessageObject.isDocumentHasThumb(tLRPC$TL_document)) {
                return;
            }
            int i = 320;
            TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_document.thumbs, 320);
            if ((closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoStrippedSize) || (closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoPathSize) || FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(closestPhotoSizeWithSize3, true).exists()) {
                return;
            }
            Bitmap createVideoThumbnailAtTime = createVideoThumbnailAtTime(str, j);
            Bitmap createVideoThumbnail = createVideoThumbnailAtTime == null ? createVideoThumbnail(str, 1) : createVideoThumbnailAtTime;
            if (z) {
                i = 90;
            }
            float f = i;
            tLRPC$TL_document.thumbs.set(0, ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize3, createVideoThumbnail, f, f, i > 90 ? 80 : 55, false, true));
        }
    }

    public static String getKeyForPhotoSize(AccountInstance accountInstance, TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap[] bitmapArr, boolean z, boolean z2) {
        if (tLRPC$PhotoSize == null || tLRPC$PhotoSize.location == null) {
            return null;
        }
        Point messageSize = ChatMessageCell.getMessageSize(tLRPC$PhotoSize.w, tLRPC$PhotoSize.h);
        if (bitmapArr != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                File pathToAttach = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(tLRPC$PhotoSize, z2);
                FileInputStream fileInputStream = new FileInputStream(pathToAttach);
                BitmapFactory.decodeStream(fileInputStream, null, options);
                fileInputStream.close();
                float max = Math.max(options.outWidth / messageSize.x, options.outHeight / messageSize.y);
                if (max < 1.0f) {
                    max = 1.0f;
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) max;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                if (Build.VERSION.SDK_INT >= 21) {
                    FileInputStream fileInputStream2 = new FileInputStream(pathToAttach);
                    bitmapArr[0] = BitmapFactory.decodeStream(fileInputStream2, null, options);
                    fileInputStream2.close();
                }
            } catch (Throwable unused) {
            }
        }
        return String.format(Locale.US, z ? "%d_%d@%d_%d_b" : "%d_%d@%d_%d", Long.valueOf(tLRPC$PhotoSize.location.volume_id), Integer.valueOf(tLRPC$PhotoSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density)));
    }

    public static boolean shouldSendWebPAsSticker(String str, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (str != null) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(str, "r");
                MappedByteBuffer map = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, str.length());
                Utilities.loadWebpImage(null, map, map.limit(), options, true);
                randomAccessFile.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            try {
                InputStream openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(openInputStream, null, options);
                if (openInputStream != null) {
                    openInputStream.close();
                }
            } catch (Exception unused) {
            }
        }
        return options.outWidth < 800 && options.outHeight < 800;
    }

    public static void prepareSendingMedia(final AccountInstance accountInstance, final ArrayList<SendingMediaInfo> arrayList, final long j, final MessageObject messageObject, final MessageObject messageObject2, final InputContentInfoCompat inputContentInfoCompat, final boolean z, boolean z2, final MessageObject messageObject3, final boolean z3, final int i) {
        final boolean z4;
        if (arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                z4 = z2;
                break;
            } else if (arrayList.get(i2).ttl > 0) {
                z4 = false;
                break;
            } else {
                i2++;
            }
        }
        mediaSendQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingMedia$89(arrayList, j, z, z4, accountInstance, messageObject3, messageObject, messageObject2, z3, i, inputContentInfoCompat);
            }
        });
    }

    /* JADX WARN: Code restructure failed: missing block: B:242:0x062e, code lost:
        if (r5 != null) goto L596;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x005d, code lost:
        if (r4 != false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x066f, code lost:
        if (r3.endsWith(r8) != false) goto L276;
     */
    /* JADX WARN: Code restructure failed: missing block: B:383:0x0900, code lost:
        if (r11 == (r15 - 1)) goto L385;
     */
    /* JADX WARN: Code restructure failed: missing block: B:536:0x0cc6, code lost:
        if (r5 == (r11 - 1)) goto L538;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:134:0x02db A[Catch: Exception -> 0x02cc, TryCatch #1 {Exception -> 0x02cc, blocks: (B:125:0x02c4, B:132:0x02d1, B:134:0x02db, B:135:0x02e6), top: B:592:0x02c4 }] */
    /* JADX WARN: Removed duplicated region for block: B:135:0x02e6 A[Catch: Exception -> 0x02cc, TRY_LEAVE, TryCatch #1 {Exception -> 0x02cc, blocks: (B:125:0x02c4, B:132:0x02d1, B:134:0x02db, B:135:0x02e6), top: B:592:0x02c4 }] */
    /* JADX WARN: Removed duplicated region for block: B:139:0x02f3 A[Catch: Exception -> 0x0312, TryCatch #4 {Exception -> 0x0312, blocks: (B:137:0x02ed, B:139:0x02f3, B:143:0x02fd, B:145:0x0303, B:146:0x030e), top: B:598:0x02ed }] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x0320  */
    /* JADX WARN: Removed duplicated region for block: B:153:0x034c  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x035d  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x0368  */
    /* JADX WARN: Removed duplicated region for block: B:323:0x07b5  */
    /* JADX WARN: Removed duplicated region for block: B:380:0x08e2  */
    /* JADX WARN: Removed duplicated region for block: B:386:0x090b  */
    /* JADX WARN: Removed duplicated region for block: B:451:0x0b30  */
    /* JADX WARN: Removed duplicated region for block: B:507:0x0c27  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:513:0x0c3d  */
    /* JADX WARN: Removed duplicated region for block: B:519:0x0c4f  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00d0  */
    /* JADX WARN: Removed duplicated region for block: B:528:0x0c98  */
    /* JADX WARN: Removed duplicated region for block: B:530:0x0c9d  */
    /* JADX WARN: Removed duplicated region for block: B:548:0x0d14 A[LOOP:4: B:546:0x0d0c->B:548:0x0d14, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:590:0x0629 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:630:0x0c4c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0161  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0166  */
    /* JADX WARN: Type inference failed for: r6v27 */
    /* JADX WARN: Type inference failed for: r6v28 */
    /* JADX WARN: Type inference failed for: r8v0 */
    /* JADX WARN: Type inference failed for: r8v32, types: [boolean] */
    /* JADX WARN: Type inference failed for: r8v33 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingMedia$89(java.util.ArrayList r65, final long r66, boolean r68, boolean r69, final org.telegram.messenger.AccountInstance r70, final org.telegram.messenger.MessageObject r71, final org.telegram.messenger.MessageObject r72, final org.telegram.messenger.MessageObject r73, final boolean r74, final int r75, androidx.core.view.inputmethod.InputContentInfoCompat r76) {
        /*
            Method dump skipped, instructions count: 3815
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$89(java.util.ArrayList, long, boolean, boolean, org.telegram.messenger.AccountInstance, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$84(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$85(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, null, null, tLRPC$TL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, null, str, j, messageObject2, messageObject3, sendingMediaInfo.caption, sendingMediaInfo.entities, null, hashMap, z, i, 0, str2, null);
        }
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$86(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, boolean z2, int i) {
        String str2 = null;
        if (messageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (z) {
                str2 = sendingMediaInfo.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessage(messageObject, tLRPC$TL_photo, null, null, str2, hashMap, false, str);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (z) {
            str2 = sendingMediaInfo.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject2, messageObject3, sendingMediaInfo.caption, sendingMediaInfo.entities, null, hashMap, z2, i, sendingMediaInfo.ttl, str);
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$87(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        if (bitmap != null && str != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), str, false);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, sendingMediaInfo.caption, sendingMediaInfo.entities, null, hashMap, z, i, sendingMediaInfo.ttl, str3, null);
        }
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$88(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        if (bitmapArr[0] != null && strArr[0] != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, tLRPC$TL_photo, null, null, null, hashMap, false, str);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_photo, null, j, messageObject2, messageObject3, sendingMediaInfo.caption, sendingMediaInfo.entities, null, hashMap, z, i, sendingMediaInfo.ttl, str);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x008b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void fillVideoAttribute(java.lang.String r5, org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r6, org.telegram.messenger.VideoEditedInfo r7) {
        /*
            r0 = 1148846080(0x447a0000, float:1000.0)
            r1 = 0
            android.media.MediaMetadataRetriever r2 = new android.media.MediaMetadataRetriever     // Catch: java.lang.Throwable -> L78 java.lang.Exception -> L7a
            r2.<init>()     // Catch: java.lang.Throwable -> L78 java.lang.Exception -> L7a
            r2.setDataSource(r5)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r1 = 18
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            if (r1 == 0) goto L19
            int r1 = java.lang.Integer.parseInt(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r6.w = r1     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
        L19:
            r1 = 19
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            if (r1 == 0) goto L27
            int r1 = java.lang.Integer.parseInt(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r6.h = r1     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
        L27:
            r1 = 9
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            if (r1 == 0) goto L3d
            long r3 = java.lang.Long.parseLong(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            float r1 = (float) r3     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            float r1 = r1 / r0
            double r3 = (double) r1     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            double r3 = java.lang.Math.ceil(r3)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            int r1 = (int) r3     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r6.duration = r1     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
        L3d:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r3 = 17
            if (r1 < r3) goto L68
            r1 = 24
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            if (r1 == 0) goto L68
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            int r1 = r1.intValue()     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            if (r7 == 0) goto L58
            r7.rotationValue = r1     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            goto L68
        L58:
            r7 = 90
            if (r1 == r7) goto L60
            r7 = 270(0x10e, float:3.78E-43)
            if (r1 != r7) goto L68
        L60:
            int r7 = r6.w     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            int r1 = r6.h     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r6.w = r1     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r6.h = r7     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
        L68:
            r7 = 1
            r2.release()     // Catch: java.lang.Exception -> L6d
            goto L89
        L6d:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)
            goto L89
        L72:
            r5 = move-exception
            r1 = r2
            goto Lbf
        L75:
            r7 = move-exception
            r1 = r2
            goto L7b
        L78:
            r5 = move-exception
            goto Lbf
        L7a:
            r7 = move-exception
        L7b:
            org.telegram.messenger.FileLog.e(r7)     // Catch: java.lang.Throwable -> L78
            if (r1 == 0) goto L88
            r1.release()     // Catch: java.lang.Exception -> L84
            goto L88
        L84:
            r7 = move-exception
            org.telegram.messenger.FileLog.e(r7)
        L88:
            r7 = 0
        L89:
            if (r7 != 0) goto Lbe
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch: java.lang.Exception -> Lba
            java.io.File r1 = new java.io.File     // Catch: java.lang.Exception -> Lba
            r1.<init>(r5)     // Catch: java.lang.Exception -> Lba
            android.net.Uri r5 = android.net.Uri.fromFile(r1)     // Catch: java.lang.Exception -> Lba
            android.media.MediaPlayer r5 = android.media.MediaPlayer.create(r7, r5)     // Catch: java.lang.Exception -> Lba
            if (r5 == 0) goto Lbe
            int r7 = r5.getDuration()     // Catch: java.lang.Exception -> Lba
            float r7 = (float) r7     // Catch: java.lang.Exception -> Lba
            float r7 = r7 / r0
            double r0 = (double) r7     // Catch: java.lang.Exception -> Lba
            double r0 = java.lang.Math.ceil(r0)     // Catch: java.lang.Exception -> Lba
            int r7 = (int) r0     // Catch: java.lang.Exception -> Lba
            r6.duration = r7     // Catch: java.lang.Exception -> Lba
            int r7 = r5.getVideoWidth()     // Catch: java.lang.Exception -> Lba
            r6.w = r7     // Catch: java.lang.Exception -> Lba
            int r7 = r5.getVideoHeight()     // Catch: java.lang.Exception -> Lba
            r6.h = r7     // Catch: java.lang.Exception -> Lba
            r5.release()     // Catch: java.lang.Exception -> Lba
            goto Lbe
        Lba:
            r5 = move-exception
            org.telegram.messenger.FileLog.e(r5)
        Lbe:
            return
        Lbf:
            if (r1 == 0) goto Lc9
            r1.release()     // Catch: java.lang.Exception -> Lc5
            goto Lc9
        Lc5:
            r6 = move-exception
            org.telegram.messenger.FileLog.e(r6)
        Lc9:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.fillVideoAttribute(java.lang.String, org.telegram.tgnet.TLRPC$TL_documentAttributeVideo, org.telegram.messenger.VideoEditedInfo):void");
    }

    public static Bitmap createVideoThumbnail(String str, int i) {
        float f = i == 2 ? 1920.0f : i == 3 ? 96.0f : 512.0f;
        Bitmap createVideoThumbnailAtTime = createVideoThumbnailAtTime(str, 0L);
        if (createVideoThumbnailAtTime != null) {
            int width = createVideoThumbnailAtTime.getWidth();
            int height = createVideoThumbnailAtTime.getHeight();
            float f2 = width;
            if (f2 <= f && height <= f) {
                return createVideoThumbnailAtTime;
            }
            float max = Math.max(width, height) / f;
            return Bitmap.createScaledBitmap(createVideoThumbnailAtTime, (int) (f2 / max), (int) (height / max), true);
        }
        return createVideoThumbnailAtTime;
    }

    public static Bitmap createVideoThumbnailAtTime(String str, long j) {
        return createVideoThumbnailAtTime(str, j, null, false);
    }

    public static Bitmap createVideoThumbnailAtTime(String str, long j, int[] iArr, boolean z) {
        Bitmap bitmap;
        if (z) {
            AnimatedFileDrawable animatedFileDrawable = new AnimatedFileDrawable(new File(str), true, 0L, null, null, null, 0L, 0, true, null);
            bitmap = animatedFileDrawable.getFrameAtTime(j, z);
            if (iArr != null) {
                iArr[0] = animatedFileDrawable.getOrientation();
            }
            animatedFileDrawable.recycle();
            if (bitmap == null) {
                return createVideoThumbnailAtTime(str, j, iArr, false);
            }
        } else {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            bitmap = null;
            try {
                try {
                    mediaMetadataRetriever.setDataSource(str);
                    Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime(j, 1);
                    if (frameAtTime == null) {
                        try {
                            frameAtTime = mediaMetadataRetriever.getFrameAtTime(j, 3);
                        } catch (Exception unused) {
                        }
                    }
                    bitmap = frameAtTime;
                } catch (Exception unused2) {
                }
            } finally {
                try {
                    mediaMetadataRetriever.release();
                } catch (RuntimeException unused3) {
                }
            }
        }
        return bitmap;
    }

    private static VideoEditedInfo createCompressionSettings(String str) {
        MediaCodecInfo selectCodec;
        boolean z;
        int[] iArr = new int[11];
        AnimatedFileDrawable.getVideoInfo(str, iArr);
        if (iArr[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't avc1 atom");
            }
            return null;
        }
        int videoBitrate = MediaController.getVideoBitrate(str);
        if (videoBitrate == -1) {
            videoBitrate = iArr[3];
        }
        int i = 4;
        float f = iArr[4];
        int i2 = iArr[6];
        long j = iArr[5];
        int i3 = iArr[7];
        if (Build.VERSION.SDK_INT < 18) {
            try {
                selectCodec = MediaController.selectCodec(MediaController.VIDEO_MIME_TYPE);
            } catch (Exception unused) {
            }
            if (selectCodec == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("no codec info for video/avc");
                }
                return null;
            }
            String name = selectCodec.getName();
            if (!name.equals("OMX.google.h264.encoder") && !name.equals("OMX.ST.VFM.H264Enc") && !name.equals("OMX.Exynos.avc.enc") && !name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") && !name.equals("OMX.MARVELL.VIDEO.H264ENCODER") && !name.equals("OMX.k3.video.encoder.avc") && !name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                if (MediaController.selectColorFormat(selectCodec, MediaController.VIDEO_MIME_TYPE) == 0) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("no color format for video/avc");
                    }
                    return null;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("unsupported encoder = " + name);
            }
            return null;
        }
        VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
        videoEditedInfo.startTime = -1L;
        videoEditedInfo.endTime = -1L;
        videoEditedInfo.bitrate = videoBitrate;
        videoEditedInfo.originalPath = str;
        videoEditedInfo.framerate = i3;
        videoEditedInfo.estimatedDuration = (long) Math.ceil(f);
        int i4 = iArr[1];
        videoEditedInfo.originalWidth = i4;
        videoEditedInfo.resultWidth = i4;
        int i5 = iArr[2];
        videoEditedInfo.originalHeight = i5;
        videoEditedInfo.resultHeight = i5;
        videoEditedInfo.rotationValue = iArr[8];
        videoEditedInfo.originalDuration = f * 1000.0f;
        float max = Math.max(i4, i5);
        float f2 = 640.0f;
        if (max <= 1280.0f) {
            i = max > 854.0f ? 3 : max > 640.0f ? 2 : 1;
        }
        int round = Math.round(DownloadController.getInstance(UserConfig.selectedAccount).getMaxVideoBitrate() / (100.0f / i));
        if (round > i) {
            round = i;
        }
        if (new File(str).length() < 1048576000) {
            if (round != i || Math.max(videoEditedInfo.originalWidth, videoEditedInfo.originalHeight) > 1280) {
                if (round == 1) {
                    f2 = 432.0f;
                } else if (round != 2) {
                    f2 = round != 3 ? 1280.0f : 848.0f;
                }
                int i6 = videoEditedInfo.originalWidth;
                int i7 = videoEditedInfo.originalHeight;
                float f3 = f2 / (i6 > i7 ? i6 : i7);
                videoEditedInfo.resultWidth = Math.round((i6 * f3) / 2.0f) * 2;
                videoEditedInfo.resultHeight = Math.round((videoEditedInfo.originalHeight * f3) / 2.0f) * 2;
                z = true;
            } else {
                z = false;
            }
            videoBitrate = MediaController.makeVideoBitrate(videoEditedInfo.originalHeight, videoEditedInfo.originalWidth, videoBitrate, videoEditedInfo.resultHeight, videoEditedInfo.resultWidth);
        } else {
            z = false;
        }
        if (!z) {
            videoEditedInfo.resultWidth = videoEditedInfo.originalWidth;
            videoEditedInfo.resultHeight = videoEditedInfo.originalHeight;
            videoEditedInfo.bitrate = videoBitrate;
        } else {
            videoEditedInfo.bitrate = videoBitrate;
        }
        long j2 = ((float) j) + (((f / 1000.0f) * videoBitrate) / 8.0f);
        videoEditedInfo.estimatedSize = j2;
        if (j2 == 0) {
            videoEditedInfo.estimatedSize = 1L;
        }
        return videoEditedInfo;
    }

    public static void prepareSendingVideo(final AccountInstance accountInstance, final String str, final VideoEditedInfo videoEditedInfo, final long j, final MessageObject messageObject, final MessageObject messageObject2, final CharSequence charSequence, final ArrayList<TLRPC$MessageEntity> arrayList, final int i, final MessageObject messageObject3, final boolean z, final int i2, final boolean z2) {
        if (str == null || str.length() == 0) {
            return;
        }
        new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda73
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingVideo$91(VideoEditedInfo.this, str, j, i, accountInstance, charSequence, messageObject3, messageObject, messageObject2, arrayList, z, i2, z2);
            }
        }).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x027f  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x02e4  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x02f4  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0333  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x0339  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x033e  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0345  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0120  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x024a  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0266  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo r29, java.lang.String r30, final long r31, final int r33, final org.telegram.messenger.AccountInstance r34, java.lang.CharSequence r35, final org.telegram.messenger.MessageObject r36, final org.telegram.messenger.MessageObject r37, final org.telegram.messenger.MessageObject r38, final java.util.ArrayList r39, final boolean r40, final int r41, boolean r42) {
        /*
            Method dump skipped, instructions count: 878
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, int, org.telegram.messenger.AccountInstance, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int, boolean):void");
    }

    public static /* synthetic */ void lambda$prepareSendingVideo$90(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        if (bitmap != null && str != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), str, false);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, str4, arrayList, null, hashMap, z, i, i2, str3, null);
        }
    }
}
