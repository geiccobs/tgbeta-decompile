package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.actions.SearchIntents;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Point;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.TwoStepVerificationActivity;
import org.telegram.ui.TwoStepVerificationSetupActivity;
/* loaded from: classes.dex */
public class SendMessagesHelper extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static final int ERROR_TYPE_FILE_TOO_LARGE = 2;
    private static final int ERROR_TYPE_UNSUPPORTED = 1;
    private static volatile SendMessagesHelper[] Instance;
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap<>();
    private SparseArray<MessageObject> unsentMessages = new SparseArray<>();
    private SparseArray<TLRPC.Message> sendingMessages = new SparseArray<>();
    private SparseArray<TLRPC.Message> editingMessages = new SparseArray<>();
    private SparseArray<TLRPC.Message> uploadMessages = new SparseArray<>();
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
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap<>(SendMessagesHelper.this.waitingForLocation);
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    });

    /* loaded from: classes4.dex */
    public static class SendingMediaInfo {
        public boolean canDeleteAfter;
        public String caption;
        public ArrayList<TLRPC.MessageEntity> entities;
        public boolean forceImage;
        public TLRPC.BotInlineResult inlineResult;
        public boolean isVideo;
        public ArrayList<TLRPC.InputDocument> masks;
        public String paintPath;
        public HashMap<String, String> params;
        public String path;
        public MediaController.SearchImage searchImage;
        public String thumbPath;
        public int ttl;
        public Uri uri;
        public VideoEditedInfo videoEditedInfo;
    }

    /* loaded from: classes4.dex */
    public class ImportingHistory {
        public long dialogId;
        public double estimatedUploadSpeed;
        public String historyPath;
        public long importId;
        private long lastUploadSize;
        private long lastUploadTime;
        public TLRPC.InputPeer peer;
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
            SendMessagesHelper.this = this$0;
        }

        public void initImport(TLRPC.InputFile inputFile) {
            TLRPC.TL_messages_initHistoryImport req = new TLRPC.TL_messages_initHistoryImport();
            req.file = inputFile;
            req.media_count = this.mediaPaths.size();
            req.peer = this.peer;
            SendMessagesHelper.this.getConnectionsManager().sendRequest(req, new AnonymousClass1(req), 2);
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingHistory$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements RequestDelegate {
            final /* synthetic */ TLRPC.TL_messages_initHistoryImport val$req;

            AnonymousClass1(TLRPC.TL_messages_initHistoryImport tL_messages_initHistoryImport) {
                ImportingHistory.this = this$1;
                this.val$req = tL_messages_initHistoryImport;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(final TLObject response, final TLRPC.TL_error error) {
                final TLRPC.TL_messages_initHistoryImport tL_messages_initHistoryImport = this.val$req;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingHistory$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingHistory.AnonymousClass1.this.m1223x6fa5ea93(response, tL_messages_initHistoryImport, error);
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-SendMessagesHelper$ImportingHistory$1 */
            public /* synthetic */ void m1223x6fa5ea93(TLObject response, TLRPC.TL_messages_initHistoryImport req, TLRPC.TL_error error) {
                if (!(response instanceof TLRPC.TL_messages_historyImport)) {
                    SendMessagesHelper.this.importingHistoryMap.remove(ImportingHistory.this.dialogId);
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId), req, error);
                    return;
                }
                ImportingHistory.this.importId = ((TLRPC.TL_messages_historyImport) response).id;
                ImportingHistory.this.uploadSet.remove(ImportingHistory.this.historyPath);
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                if (ImportingHistory.this.uploadSet.isEmpty()) {
                    ImportingHistory.this.startImport();
                }
                ImportingHistory.this.lastUploadTime = SystemClock.elapsedRealtime();
                int N = ImportingHistory.this.uploadMedia.size();
                for (int a = 0; a < N; a++) {
                    SendMessagesHelper.this.getFileLoader().uploadFile(ImportingHistory.this.uploadMedia.get(a), false, true, ConnectionsManager.FileTypeFile);
                }
            }
        }

        public long getUploadedCount() {
            return this.uploadedSize;
        }

        public long getTotalCount() {
            return this.totalSize;
        }

        public void onFileFailedToUpload(String path) {
            if (path.equals(this.historyPath)) {
                SendMessagesHelper.this.importingHistoryMap.remove(this.dialogId);
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.code = 400;
                error.text = "IMPORT_UPLOAD_FAILED";
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId), new TLRPC.TL_messages_initHistoryImport(), error);
                return;
            }
            this.uploadSet.remove(path);
        }

        public void addUploadProgress(String path, long sz, float progress) {
            this.uploadProgresses.put(path, Float.valueOf(progress));
            this.uploadSize.put(path, Long.valueOf(sz));
            this.uploadedSize = 0L;
            for (Map.Entry<String, Long> entry : this.uploadSize.entrySet()) {
                this.uploadedSize += entry.getValue().longValue();
            }
            long newTime = SystemClock.elapsedRealtime();
            if (!path.equals(this.historyPath)) {
                long j = this.uploadedSize;
                long j2 = this.lastUploadSize;
                if (j != j2) {
                    long j3 = this.lastUploadTime;
                    if (newTime != j3) {
                        double d = newTime - j3;
                        Double.isNaN(d);
                        double dt = d / 1000.0d;
                        double d2 = j - j2;
                        Double.isNaN(d2);
                        double uploadSpeed = d2 / dt;
                        double d3 = this.estimatedUploadSpeed;
                        if (d3 != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                            this.estimatedUploadSpeed = (0.01d * uploadSpeed) + ((1.0d - 0.01d) * d3);
                        } else {
                            this.estimatedUploadSpeed = uploadSpeed;
                        }
                        double d4 = (this.totalSize - j) * 1000;
                        double d5 = this.estimatedUploadSpeed;
                        Double.isNaN(d4);
                        this.timeUntilFinish = (int) (d4 / d5);
                        this.lastUploadSize = j;
                        this.lastUploadTime = newTime;
                    }
                }
            }
            float pr = ((float) getUploadedCount()) / ((float) getTotalCount());
            int newProgress = (int) (100.0f * pr);
            if (this.uploadProgress != newProgress) {
                this.uploadProgress = newProgress;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId));
            }
        }

        public void onMediaImport(String path, long size, TLRPC.InputFile inputFile) {
            addUploadProgress(path, size, 1.0f);
            TLRPC.TL_messages_uploadImportedMedia req = new TLRPC.TL_messages_uploadImportedMedia();
            req.peer = this.peer;
            req.import_id = this.importId;
            req.file_name = new File(path).getName();
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            String ext = "txt";
            int idx = req.file_name.lastIndexOf(46);
            if (idx != -1) {
                ext = req.file_name.substring(idx + 1).toLowerCase();
            }
            String mimeType = myMime.getMimeTypeFromExtension(ext);
            if (mimeType == null) {
                if ("opus".equals(ext)) {
                    mimeType = MimeTypes.AUDIO_OPUS;
                } else if ("webp".equals(ext)) {
                    mimeType = "image/webp";
                } else {
                    mimeType = ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN;
                }
            }
            if (mimeType.equals("image/jpg") || mimeType.equals("image/jpeg")) {
                TLRPC.TL_inputMediaUploadedPhoto inputMediaUploadedPhoto = new TLRPC.TL_inputMediaUploadedPhoto();
                inputMediaUploadedPhoto.file = inputFile;
                req.media = inputMediaUploadedPhoto;
            } else {
                TLRPC.TL_inputMediaUploadedDocument inputMediaDocument = new TLRPC.TL_inputMediaUploadedDocument();
                inputMediaDocument.file = inputFile;
                inputMediaDocument.mime_type = mimeType;
                req.media = inputMediaDocument;
            }
            SendMessagesHelper.this.getConnectionsManager().sendRequest(req, new AnonymousClass2(path), 2);
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingHistory$2 */
        /* loaded from: classes4.dex */
        public class AnonymousClass2 implements RequestDelegate {
            final /* synthetic */ String val$path;

            AnonymousClass2(String str) {
                ImportingHistory.this = this$1;
                this.val$path = str;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(TLObject response, TLRPC.TL_error error) {
                final String str = this.val$path;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingHistory$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingHistory.AnonymousClass2.this.m1224x6fa5ea94(str);
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-SendMessagesHelper$ImportingHistory$2 */
            public /* synthetic */ void m1224x6fa5ea94(String path) {
                ImportingHistory.this.uploadSet.remove(path);
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                if (ImportingHistory.this.uploadSet.isEmpty()) {
                    ImportingHistory.this.startImport();
                }
            }
        }

        public void startImport() {
            TLRPC.TL_messages_startHistoryImport req = new TLRPC.TL_messages_startHistoryImport();
            req.peer = this.peer;
            req.import_id = this.importId;
            SendMessagesHelper.this.getConnectionsManager().sendRequest(req, new AnonymousClass3(req));
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingHistory$3 */
        /* loaded from: classes4.dex */
        public class AnonymousClass3 implements RequestDelegate {
            final /* synthetic */ TLRPC.TL_messages_startHistoryImport val$req;

            AnonymousClass3(TLRPC.TL_messages_startHistoryImport tL_messages_startHistoryImport) {
                ImportingHistory.this = this$1;
                this.val$req = tL_messages_startHistoryImport;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(TLObject response, final TLRPC.TL_error error) {
                final TLRPC.TL_messages_startHistoryImport tL_messages_startHistoryImport = this.val$req;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingHistory$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingHistory.AnonymousClass3.this.m1225x6fa5ea95(error, tL_messages_startHistoryImport);
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-SendMessagesHelper$ImportingHistory$3 */
            public /* synthetic */ void m1225x6fa5ea95(TLRPC.TL_error error, TLRPC.TL_messages_startHistoryImport req) {
                SendMessagesHelper.this.importingHistoryMap.remove(ImportingHistory.this.dialogId);
                if (error == null) {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                } else {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId), req, error);
                }
            }
        }

        public void setImportProgress(int value) {
            if (value == 100) {
                SendMessagesHelper.this.importingHistoryMap.remove(this.dialogId);
            }
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId));
        }
    }

    /* loaded from: classes4.dex */
    public static class ImportingSticker {
        public boolean animated;
        public String emoji;
        public TLRPC.TL_inputStickerSetItem item;
        public String mimeType;
        public String path;
        public boolean validated;

        public void uploadMedia(int account, TLRPC.InputFile inputFile, Runnable onFinish) {
            TLRPC.TL_messages_uploadMedia req = new TLRPC.TL_messages_uploadMedia();
            req.peer = new TLRPC.TL_inputPeerSelf();
            req.media = new TLRPC.TL_inputMediaUploadedDocument();
            req.media.file = inputFile;
            req.media.mime_type = this.mimeType;
            ConnectionsManager.getInstance(account).sendRequest(req, new AnonymousClass1(onFinish), 2);
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingSticker$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements RequestDelegate {
            final /* synthetic */ Runnable val$onFinish;

            AnonymousClass1(Runnable runnable) {
                ImportingSticker.this = this$0;
                this.val$onFinish = runnable;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(final TLObject response, TLRPC.TL_error error) {
                final Runnable runnable = this.val$onFinish;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingSticker$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingSticker.AnonymousClass1.this.m1226x170488fc(response, runnable);
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-SendMessagesHelper$ImportingSticker$1 */
            public /* synthetic */ void m1226x170488fc(TLObject response, Runnable onFinish) {
                if (response instanceof TLRPC.TL_messageMediaDocument) {
                    TLRPC.TL_messageMediaDocument mediaDocument = (TLRPC.TL_messageMediaDocument) response;
                    ImportingSticker.this.item = new TLRPC.TL_inputStickerSetItem();
                    ImportingSticker.this.item.document = new TLRPC.TL_inputDocument();
                    ImportingSticker.this.item.document.id = mediaDocument.document.id;
                    ImportingSticker.this.item.document.access_hash = mediaDocument.document.access_hash;
                    ImportingSticker.this.item.document.file_reference = mediaDocument.document.file_reference;
                    ImportingSticker.this.item.emoji = ImportingSticker.this.emoji != null ? ImportingSticker.this.emoji : "";
                    ImportingSticker.this.mimeType = mediaDocument.document.mime_type;
                } else if (ImportingSticker.this.animated) {
                    ImportingSticker.this.mimeType = "application/x-bad-tgsticker";
                }
                onFinish.run();
            }
        }
    }

    /* loaded from: classes4.dex */
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
            SendMessagesHelper.this = this$0;
        }

        public void initImport() {
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            this.lastUploadTime = SystemClock.elapsedRealtime();
            int N = this.uploadMedia.size();
            for (int a = 0; a < N; a++) {
                SendMessagesHelper.this.getFileLoader().uploadFile(this.uploadMedia.get(a).path, false, true, ConnectionsManager.FileTypeFile);
            }
        }

        public long getUploadedCount() {
            return this.uploadedSize;
        }

        public long getTotalCount() {
            return this.totalSize;
        }

        public void onFileFailedToUpload(String path) {
            ImportingSticker file = this.uploadSet.remove(path);
            if (file != null) {
                this.uploadMedia.remove(file);
            }
        }

        public void addUploadProgress(String path, long sz, float progress) {
            this.uploadProgresses.put(path, Float.valueOf(progress));
            this.uploadSize.put(path, Long.valueOf(sz));
            this.uploadedSize = 0L;
            for (Map.Entry<String, Long> entry : this.uploadSize.entrySet()) {
                this.uploadedSize += entry.getValue().longValue();
            }
            long newTime = SystemClock.elapsedRealtime();
            long j = this.uploadedSize;
            long j2 = this.lastUploadSize;
            if (j != j2) {
                long j3 = this.lastUploadTime;
                if (newTime != j3) {
                    double d = newTime - j3;
                    Double.isNaN(d);
                    double dt = d / 1000.0d;
                    double d2 = j - j2;
                    Double.isNaN(d2);
                    double uploadSpeed = d2 / dt;
                    double d3 = this.estimatedUploadSpeed;
                    if (d3 != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                        this.estimatedUploadSpeed = (0.01d * uploadSpeed) + ((1.0d - 0.01d) * d3);
                    } else {
                        this.estimatedUploadSpeed = uploadSpeed;
                    }
                    double d4 = (this.totalSize - j) * 1000;
                    double d5 = this.estimatedUploadSpeed;
                    Double.isNaN(d4);
                    this.timeUntilFinish = (int) (d4 / d5);
                    this.lastUploadSize = j;
                    this.lastUploadTime = newTime;
                }
            }
            float pr = ((float) getUploadedCount()) / ((float) getTotalCount());
            int newProgress = (int) (100.0f * pr);
            if (this.uploadProgress != newProgress) {
                this.uploadProgress = newProgress;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            }
        }

        public void onMediaImport(final String path, long size, TLRPC.InputFile inputFile) {
            addUploadProgress(path, size, 1.0f);
            ImportingSticker file = this.uploadSet.get(path);
            if (file == null) {
                return;
            }
            file.uploadMedia(SendMessagesHelper.this.currentAccount, inputFile, new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingStickers$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.ImportingStickers.this.m1227xb92aa0e3(path);
                }
            });
        }

        /* renamed from: lambda$onMediaImport$0$org-telegram-messenger-SendMessagesHelper$ImportingStickers */
        public /* synthetic */ void m1227xb92aa0e3(String path) {
            this.uploadSet.remove(path);
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            if (this.uploadSet.isEmpty()) {
                startImport();
            }
        }

        public void startImport() {
            TLRPC.TL_stickers_createStickerSet req = new TLRPC.TL_stickers_createStickerSet();
            req.user_id = new TLRPC.TL_inputUserSelf();
            req.title = this.title;
            req.short_name = this.shortName;
            req.animated = this.uploadMedia.get(0).animated;
            String str = this.software;
            if (str != null) {
                req.software = str;
                req.flags |= 8;
            }
            int N = this.uploadMedia.size();
            for (int a = 0; a < N; a++) {
                ImportingSticker file = this.uploadMedia.get(a);
                if (file.item != null) {
                    req.stickers.add(file.item);
                }
            }
            SendMessagesHelper.this.getConnectionsManager().sendRequest(req, new AnonymousClass1(req));
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$ImportingStickers$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements RequestDelegate {
            final /* synthetic */ TLRPC.TL_stickers_createStickerSet val$req;

            AnonymousClass1(TLRPC.TL_stickers_createStickerSet tL_stickers_createStickerSet) {
                ImportingStickers.this = this$1;
                this.val$req = tL_stickers_createStickerSet;
            }

            @Override // org.telegram.tgnet.RequestDelegate
            public void run(final TLObject response, final TLRPC.TL_error error) {
                final TLRPC.TL_stickers_createStickerSet tL_stickers_createStickerSet = this.val$req;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.ImportingStickers.AnonymousClass1.this.m1228xc98dbdb1(error, tL_stickers_createStickerSet, response);
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-SendMessagesHelper$ImportingStickers$1 */
            public /* synthetic */ void m1228xc98dbdb1(TLRPC.TL_error error, TLRPC.TL_stickers_createStickerSet req, TLObject response) {
                SendMessagesHelper.this.importingStickersMap.remove(ImportingStickers.this.shortName);
                if (error == null) {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, ImportingStickers.this.shortName);
                } else {
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, ImportingStickers.this.shortName, req, error);
                }
                if (response instanceof TLRPC.TL_messages_stickerSet) {
                    if (SendMessagesHelper.this.getNotificationCenter().hasObservers(NotificationCenter.stickersImportComplete)) {
                        SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportComplete, response);
                    } else {
                        SendMessagesHelper.this.getMediaDataController().toggleStickerSet(null, response, 2, null, false, false);
                    }
                }
            }
        }

        public void setImportProgress(int value) {
            if (value == 100) {
                SendMessagesHelper.this.importingStickersMap.remove(this.shortName);
            }
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
        }
    }

    static {
        int cores;
        if (Build.VERSION.SDK_INT >= 17) {
            cores = Runtime.getRuntime().availableProcessors();
        } else {
            cores = 2;
        }
        mediaSendThreadPool = new ThreadPoolExecutor(cores, cores, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        Instance = new SendMessagesHelper[4];
    }

    /* loaded from: classes4.dex */
    public static class MediaSendPrepareWorker {
        public volatile String parentObject;
        public volatile TLRPC.TL_photo photo;
        public CountDownLatch sync;

        private MediaSendPrepareWorker() {
        }
    }

    /* loaded from: classes4.dex */
    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener();
        private GpsLocationListener networkLocationListener = new GpsLocationListener();

        /* loaded from: classes4.dex */
        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        /* loaded from: classes4.dex */
        public class GpsLocationListener implements LocationListener {
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
                if (location.getAccuracy() < 100.0f) {
                    if (LocationProvider.this.delegate != null) {
                        LocationProvider.this.delegate.onLocationAcquired(location);
                    }
                    if (LocationProvider.this.locationQueryCancelRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                    }
                    LocationProvider.this.cleanup();
                }
            }

            @Override // android.location.LocationListener
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override // android.location.LocationListener
            public void onProviderEnabled(String provider) {
            }

            @Override // android.location.LocationListener
            public void onProviderDisabled(String provider) {
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
                    SendMessagesHelper.LocationProvider.this.m1229x9940a2de();
                }
            };
            this.locationQueryCancelRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }

        /* renamed from: lambda$start$0$org-telegram-messenger-SendMessagesHelper$LocationProvider */
        public /* synthetic */ void m1229x9940a2de() {
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

    /* loaded from: classes4.dex */
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
            SendMessagesHelper.this = this$0;
        }
    }

    /* loaded from: classes4.dex */
    public class DelayedMessage {
        public TLRPC.EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public ArrayList<String> httpLocations;
        public ArrayList<TLRPC.InputMedia> inputMedias;
        public TLRPC.InputMedia inputUploadMedia;
        public TLObject locationParent;
        public ArrayList<TLRPC.PhotoSize> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<TLRPC.Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        public TLRPC.PhotoSize photoSize;
        ArrayList<DelayedMessageSendAfterRequest> requests;
        public boolean retriedToSend;
        public boolean scheduled;
        public TLObject sendEncryptedRequest;
        public TLObject sendRequest;
        public int topMessageId;
        public int type;
        public VideoEditedInfo videoEditedInfo;
        public ArrayList<VideoEditedInfo> videoEditedInfos;

        public DelayedMessage(long peer) {
            SendMessagesHelper.this = this$0;
            this.peer = peer;
        }

        public void initForGroup(long id) {
            this.type = 4;
            this.groupId = id;
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

        public void addDelayedRequest(TLObject req, MessageObject msgObj, String originalPath, Object parentObject, DelayedMessage delayedMessage, boolean scheduled) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObj = msgObj;
            request.originalPath = originalPath;
            request.delayedMessage = delayedMessage;
            request.parentObject = parentObject;
            request.scheduled = scheduled;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(request);
        }

        public void addDelayedRequest(TLObject req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths, ArrayList<Object> parentObjects, DelayedMessage delayedMessage, boolean scheduled) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObjs = msgObjs;
            request.originalPaths = originalPaths;
            request.delayedMessage = delayedMessage;
            request.parentObjects = parentObjects;
            request.scheduled = scheduled;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(request);
        }

        public void sendDelayedRequests() {
            ArrayList<DelayedMessageSendAfterRequest> arrayList = this.requests;
            if (arrayList != null) {
                int i = this.type;
                if (i != 4 && i != 0) {
                    return;
                }
                int size = arrayList.size();
                for (int a = 0; a < size; a++) {
                    DelayedMessageSendAfterRequest request = this.requests.get(a);
                    if (request.request instanceof TLRPC.TL_messages_sendEncryptedMultiMedia) {
                        SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia) request.request, this);
                    } else if (!(request.request instanceof TLRPC.TL_messages_sendMultiMedia)) {
                        SendMessagesHelper.this.performSendMessageRequest(request.request, request.msgObj, request.originalPath, request.delayedMessage, request.parentObject, null, request.scheduled);
                    } else {
                        SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia) request.request, request.msgObjs, request.originalPaths, request.parentObjects, request.delayedMessage, request.scheduled);
                    }
                }
                this.requests = null;
            }
        }

        public void markAsError() {
            if (this.type == 4) {
                for (int a = 0; a < this.messageObjects.size(); a++) {
                    MessageObject obj = this.messageObjects.get(a);
                    SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(obj.messageOwner, obj.scheduled);
                    obj.messageOwner.send_state = 2;
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(obj.getId()));
                    SendMessagesHelper.this.processSentMessage(obj.getId());
                    SendMessagesHelper.this.removeFromUploadingMessages(obj.getId(), this.scheduled);
                }
                HashMap hashMap = SendMessagesHelper.this.delayedMessages;
                hashMap.remove("group_" + this.groupId);
            } else {
                SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(this.obj.messageOwner, this.obj.scheduled);
                this.obj.messageOwner.send_state = 2;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
                SendMessagesHelper.this.processSentMessage(this.obj.getId());
                SendMessagesHelper.this.removeFromUploadingMessages(this.obj.getId(), this.scheduled);
            }
            sendDelayedRequests();
        }
    }

    public static SendMessagesHelper getInstance(int num) {
        SendMessagesHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (SendMessagesHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    SendMessagesHelper[] sendMessagesHelperArr = Instance;
                    SendMessagesHelper sendMessagesHelper = new SendMessagesHelper(num);
                    localInstance = sendMessagesHelper;
                    sendMessagesHelperArr[num] = sendMessagesHelper;
                }
            }
        }
        return localInstance;
    }

    public SendMessagesHelper(int instance) {
        super(instance);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1162lambda$new$0$orgtelegrammessengerSendMessagesHelper();
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1162lambda$new$0$orgtelegrammessengerSendMessagesHelper() {
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
    public void didReceivedNotification(int id, int account, Object... args) {
        String path;
        ArrayList<DelayedMessage> arr;
        final MessageObject messageObject;
        int fileType;
        ArrayList<DelayedMessage> arr2;
        long availableSize;
        String finalPath;
        TLRPC.InputMedia media;
        ImportingStickers importingStickers;
        ImportingHistory importingHistory;
        TLRPC.InputFile file;
        ArrayList<DelayedMessage> arr3;
        String location;
        TLRPC.InputEncryptedFile encryptedFile;
        int a;
        TLRPC.InputEncryptedFile encryptedFile2;
        int a2;
        ArrayList<DelayedMessage> arr4;
        if (id == NotificationCenter.fileUploadProgressChanged) {
            String fileName = (String) args[0];
            ImportingHistory importingHistory2 = this.importingHistoryFiles.get(fileName);
            if (importingHistory2 != null) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                importingHistory2.addUploadProgress(fileName, loadedSize.longValue(), ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
            }
            ImportingStickers importingStickers2 = this.importingStickersFiles.get(fileName);
            if (importingStickers2 != null) {
                Long loadedSize2 = (Long) args[1];
                Long totalSize2 = (Long) args[2];
                importingStickers2.addUploadProgress(fileName, loadedSize2.longValue(), ((float) loadedSize2.longValue()) / ((float) totalSize2.longValue()));
            }
        } else if (id == NotificationCenter.fileUploaded) {
            String location2 = (String) args[0];
            TLRPC.InputFile file2 = (TLRPC.InputFile) args[1];
            TLRPC.InputEncryptedFile encryptedFile3 = (TLRPC.InputEncryptedFile) args[2];
            ImportingHistory importingHistory3 = this.importingHistoryFiles.get(location2);
            if (importingHistory3 != null) {
                if (location2.equals(importingHistory3.historyPath)) {
                    importingHistory3.initImport(file2);
                } else {
                    importingHistory3.onMediaImport(location2, ((Long) args[5]).longValue(), file2);
                }
            }
            ImportingStickers importingStickers3 = this.importingStickersFiles.get(location2);
            if (importingStickers3 != null) {
                importingStickers3.onMediaImport(location2, ((Long) args[5]).longValue(), file2);
            }
            ArrayList<DelayedMessage> arr5 = this.delayedMessages.get(location2);
            if (arr5 != null) {
                int a3 = 0;
                while (a3 < arr5.size()) {
                    DelayedMessage message = arr5.get(a3);
                    if (message.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                        media = ((TLRPC.TL_messages_sendMedia) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TLRPC.TL_messages_editMessage) {
                        media = ((TLRPC.TL_messages_editMessage) message.sendRequest).media;
                    } else if (!(message.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia)) {
                        media = null;
                    } else {
                        media = (TLRPC.InputMedia) message.extraHashMap.get(location2);
                    }
                    if (file2 == null || media == null) {
                        ArrayList<DelayedMessage> arr6 = arr5;
                        importingStickers = importingStickers3;
                        importingHistory = importingHistory3;
                        file = file2;
                        location = location2;
                        a = a3;
                        encryptedFile = encryptedFile3;
                        if (encryptedFile == null || message.sendEncryptedRequest == null) {
                            arr3 = arr6;
                        } else {
                            TLRPC.TL_decryptedMessage decryptedMessage = null;
                            if (message.type == 4) {
                                TLRPC.TL_messages_sendEncryptedMultiMedia req = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                                TLRPC.InputEncryptedFile inputEncryptedFile = (TLRPC.InputEncryptedFile) message.extraHashMap.get(location);
                                int index = req.files.indexOf(inputEncryptedFile);
                                if (index < 0) {
                                    arr3 = arr6;
                                } else {
                                    req.files.set(index, encryptedFile);
                                    arr3 = arr6;
                                    if (inputEncryptedFile.id == 1) {
                                        MessageObject messageObject2 = (MessageObject) message.extraHashMap.get(location + "_i");
                                        message.photoSize = (TLRPC.PhotoSize) message.extraHashMap.get(location + "_t");
                                        stopVideoService(message.messageObjects.get(index).messageOwner.attachPath);
                                    }
                                    TLRPC.TL_decryptedMessage decryptedMessage2 = req.messages.get(index);
                                    decryptedMessage = decryptedMessage2;
                                }
                            } else {
                                arr3 = arr6;
                                decryptedMessage = (TLRPC.TL_decryptedMessage) message.sendEncryptedRequest;
                            }
                            if (decryptedMessage != null) {
                                if ((decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaVideo) || (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaPhoto) || (decryptedMessage.media instanceof TLRPC.TL_decryptedMessageMediaDocument)) {
                                    long size = ((Long) args[5]).longValue();
                                    decryptedMessage.media.size = (int) size;
                                }
                                decryptedMessage.media.key = (byte[]) args[3];
                                decryptedMessage.media.iv = (byte[]) args[4];
                                if (message.type == 4) {
                                    uploadMultiMedia(message, null, encryptedFile, location);
                                } else {
                                    getSecretChatHelper().performSendEncryptedRequest(decryptedMessage, message.obj.messageOwner, message.encryptedChat, encryptedFile, message.originalPath, message.obj);
                                }
                            }
                            arr3.remove(a);
                            a--;
                        }
                    } else {
                        if (message.type == 0) {
                            media.file = file2;
                            a2 = a3;
                            encryptedFile2 = encryptedFile3;
                            importingHistory = importingHistory3;
                            importingStickers = importingStickers3;
                            arr4 = arr5;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, message, true, null, message.parentObject, null, message.scheduled);
                            file = file2;
                            location = location2;
                        } else {
                            a2 = a3;
                            arr4 = arr5;
                            importingStickers = importingStickers3;
                            importingHistory = importingHistory3;
                            encryptedFile2 = encryptedFile3;
                            TLRPC.InputFile file3 = file2;
                            String location3 = location2;
                            TLRPC.InputMedia media2 = media;
                            if (message.type != 1) {
                                if (message.type == 2) {
                                    if (media2.file == null) {
                                        media2.file = file3;
                                        if (media2.thumb != null || message.photoSize == null || message.photoSize.location == null) {
                                            file = file3;
                                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject, null, message.scheduled);
                                            location = location3;
                                        } else {
                                            performSendDelayedMessage(message);
                                            file = file3;
                                            location = location3;
                                        }
                                    } else {
                                        file = file3;
                                        media2.thumb = file;
                                        media2.flags |= 4;
                                        performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject, null, message.scheduled);
                                        location = location3;
                                    }
                                } else {
                                    file = file3;
                                    if (message.type == 3) {
                                        media2.file = file;
                                        performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject, null, message.scheduled);
                                        location = location3;
                                    } else if (message.type != 4) {
                                        location = location3;
                                    } else if (media2 instanceof TLRPC.TL_inputMediaUploadedDocument) {
                                        if (media2.file == null) {
                                            media2.file = file;
                                            HashMap<Object, Object> hashMap = message.extraHashMap;
                                            StringBuilder sb = new StringBuilder();
                                            location = location3;
                                            sb.append(location);
                                            sb.append("_i");
                                            int index2 = message.messageObjects.indexOf((MessageObject) hashMap.get(sb.toString()));
                                            if (index2 >= 0) {
                                                stopVideoService(message.messageObjects.get(index2).messageOwner.attachPath);
                                            }
                                            message.photoSize = (TLRPC.PhotoSize) message.extraHashMap.get(location + "_t");
                                            if (media2.thumb != null || message.photoSize == null || message.photoSize.location == null) {
                                                uploadMultiMedia(message, media2, null, location);
                                            } else {
                                                message.performMediaUpload = true;
                                                performSendDelayedMessage(message, index2);
                                            }
                                        } else {
                                            location = location3;
                                            media2.thumb = file;
                                            media2.flags |= 4;
                                            uploadMultiMedia(message, media2, null, (String) message.extraHashMap.get(location + "_o"));
                                        }
                                    } else {
                                        location = location3;
                                        media2.file = file;
                                        uploadMultiMedia(message, media2, null, location);
                                    }
                                }
                            } else if (media2.file == null) {
                                media2.file = file3;
                                if (media2.thumb != null || message.photoSize == null || message.photoSize.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject, null, message.scheduled);
                                    file = file3;
                                    location = location3;
                                } else {
                                    performSendDelayedMessage(message);
                                    file = file3;
                                    location = location3;
                                }
                            } else {
                                media2.thumb = file3;
                                media2.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject, null, message.scheduled);
                                file = file3;
                                location = location3;
                            }
                        }
                        int a4 = a2;
                        arr4.remove(a4);
                        a = a4 - 1;
                        arr3 = arr4;
                        encryptedFile = encryptedFile2;
                    }
                    encryptedFile3 = encryptedFile;
                    file2 = file;
                    importingHistory3 = importingHistory;
                    importingStickers3 = importingStickers;
                    a3 = a + 1;
                    ArrayList<DelayedMessage> arrayList = arr3;
                    location2 = location;
                    arr5 = arrayList;
                }
                String location4 = location2;
                if (arr5.isEmpty()) {
                    this.delayedMessages.remove(location4);
                }
            }
        } else if (id == NotificationCenter.fileUploadFailed) {
            String location5 = (String) args[0];
            boolean enc = ((Boolean) args[1]).booleanValue();
            ImportingHistory importingHistory4 = this.importingHistoryFiles.get(location5);
            if (importingHistory4 != null) {
                importingHistory4.onFileFailedToUpload(location5);
            }
            ImportingStickers importingStickers4 = this.importingStickersFiles.get(location5);
            if (importingStickers4 != null) {
                importingStickers4.onFileFailedToUpload(location5);
            }
            ArrayList<DelayedMessage> arr7 = this.delayedMessages.get(location5);
            if (arr7 != null) {
                int a5 = 0;
                while (a5 < arr7.size()) {
                    DelayedMessage obj = arr7.get(a5);
                    if ((enc && obj.sendEncryptedRequest != null) || (!enc && obj.sendRequest != null)) {
                        obj.markAsError();
                        arr7.remove(a5);
                        a5--;
                    }
                    a5++;
                }
                if (arr7.isEmpty()) {
                    this.delayedMessages.remove(location5);
                }
            }
        } else if (id == NotificationCenter.filePreparingStarted) {
            MessageObject messageObject3 = (MessageObject) args[0];
            if (messageObject3.getId() == 0) {
                return;
            }
            String str = (String) args[1];
            ArrayList<DelayedMessage> arr8 = this.delayedMessages.get(messageObject3.messageOwner.attachPath);
            if (arr8 != null) {
                int a6 = 0;
                while (true) {
                    if (a6 >= arr8.size()) {
                        break;
                    }
                    DelayedMessage message2 = arr8.get(a6);
                    if (message2.type == 4) {
                        int index3 = message2.messageObjects.indexOf(messageObject3);
                        message2.photoSize = (TLRPC.PhotoSize) message2.extraHashMap.get(messageObject3.messageOwner.attachPath + "_t");
                        message2.performMediaUpload = true;
                        performSendDelayedMessage(message2, index3);
                        arr8.remove(a6);
                        break;
                    } else if (message2.obj != messageObject3) {
                        a6++;
                    } else {
                        message2.videoEditedInfo = null;
                        performSendDelayedMessage(message2);
                        arr8.remove(a6);
                        break;
                    }
                }
                if (arr8.isEmpty()) {
                    this.delayedMessages.remove(messageObject3.messageOwner.attachPath);
                }
            }
        } else if (id == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject4 = (MessageObject) args[0];
            if (messageObject4.getId() == 0) {
                return;
            }
            String finalPath2 = (String) args[1];
            long availableSize2 = ((Long) args[2]).longValue();
            long finalSize = ((Long) args[3]).longValue();
            boolean isEncrypted = DialogObject.isEncryptedDialog(messageObject4.getDialogId());
            getFileLoader().checkUploadNewDataAvailable(finalPath2, isEncrypted, availableSize2, finalSize);
            if (finalSize != 0) {
                stopVideoService(messageObject4.messageOwner.attachPath);
                ArrayList<DelayedMessage> arr9 = this.delayedMessages.get(messageObject4.messageOwner.attachPath);
                if (arr9 != null) {
                    int a7 = 0;
                    while (a7 < arr9.size()) {
                        DelayedMessage message3 = arr9.get(a7);
                        if (message3.type == 4) {
                            int b = 0;
                            while (true) {
                                if (b >= message3.messageObjects.size()) {
                                    finalPath = finalPath2;
                                    availableSize = availableSize2;
                                    break;
                                }
                                MessageObject obj2 = message3.messageObjects.get(b);
                                if (obj2 != messageObject4) {
                                    b++;
                                } else {
                                    finalPath = finalPath2;
                                    message3.obj.shouldRemoveVideoEditedInfo = true;
                                    obj2.messageOwner.params.remove("ve");
                                    availableSize = availableSize2;
                                    obj2.messageOwner.media.document.size = (int) finalSize;
                                    ArrayList<TLRPC.Message> messages = new ArrayList<>();
                                    messages.add(obj2.messageOwner);
                                    getMessagesStorage().putMessages(messages, false, true, false, 0, obj2.scheduled);
                                    break;
                                }
                            }
                        } else {
                            finalPath = finalPath2;
                            availableSize = availableSize2;
                            if (message3.obj == messageObject4) {
                                message3.obj.shouldRemoveVideoEditedInfo = true;
                                message3.obj.messageOwner.params.remove("ve");
                                message3.obj.messageOwner.media.document.size = (int) finalSize;
                                ArrayList<TLRPC.Message> messages2 = new ArrayList<>();
                                messages2.add(message3.obj.messageOwner);
                                getMessagesStorage().putMessages(messages2, false, true, false, 0, message3.obj.scheduled);
                                return;
                            }
                        }
                        a7++;
                        finalPath2 = finalPath;
                        availableSize2 = availableSize;
                    }
                }
            }
        } else if (id == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject5 = (MessageObject) args[0];
            if (messageObject5.getId() == 0) {
                return;
            }
            String finalPath3 = (String) args[1];
            stopVideoService(messageObject5.messageOwner.attachPath);
            ArrayList<DelayedMessage> arr10 = this.delayedMessages.get(finalPath3);
            if (arr10 != null) {
                int a8 = 0;
                while (a8 < arr10.size()) {
                    DelayedMessage message4 = arr10.get(a8);
                    if (message4.type == 4) {
                        int b2 = 0;
                        while (true) {
                            if (b2 < message4.messages.size()) {
                                if (message4.messageObjects.get(b2) != messageObject5) {
                                    b2++;
                                } else {
                                    message4.markAsError();
                                    arr10.remove(a8);
                                    a8--;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else if (message4.obj == messageObject5) {
                        message4.markAsError();
                        arr10.remove(a8);
                        a8--;
                    }
                    a8++;
                }
                if (arr10.isEmpty()) {
                    this.delayedMessages.remove(finalPath3);
                }
            }
        } else if (id == NotificationCenter.httpFileDidLoad) {
            final String path2 = (String) args[0];
            ArrayList<DelayedMessage> arr11 = this.delayedMessages.get(path2);
            if (arr11 != null) {
                int a9 = 0;
                while (a9 < arr11.size()) {
                    final DelayedMessage message5 = arr11.get(a9);
                    if (message5.type == 0) {
                        fileType = 0;
                        messageObject = message5.obj;
                    } else if (message5.type == 2) {
                        fileType = 1;
                        messageObject = message5.obj;
                    } else if (message5.type != 4) {
                        fileType = -1;
                        messageObject = null;
                    } else {
                        MessageObject messageObject6 = (MessageObject) message5.extraHashMap.get(path2);
                        if (messageObject6.getDocument() != null) {
                            fileType = 1;
                            messageObject = messageObject6;
                        } else {
                            fileType = 0;
                            messageObject = messageObject6;
                        }
                    }
                    if (fileType == 0) {
                        String md5 = Utilities.MD5(path2) + "." + ImageLoader.getHttpUrlExtension(path2, "file");
                        final File cacheFile = new File(FileLoader.getDirectory(4), md5);
                        final MessageObject messageObject7 = messageObject;
                        arr2 = arr11;
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda17
                            @Override // java.lang.Runnable
                            public final void run() {
                                SendMessagesHelper.this.m1157x5b683917(cacheFile, messageObject7, message5, path2);
                            }
                        });
                    } else {
                        arr2 = arr11;
                        if (fileType == 1) {
                            String md52 = Utilities.MD5(path2) + ".gif";
                            final File cacheFile2 = new File(FileLoader.getDirectory(4), md52);
                            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda32
                                @Override // java.lang.Runnable
                                public final void run() {
                                    SendMessagesHelper.this.m1159x25eb1699(message5, cacheFile2, messageObject);
                                }
                            });
                        }
                    }
                    a9++;
                    arr11 = arr2;
                }
                this.delayedMessages.remove(path2);
            }
        } else if (id == NotificationCenter.fileLoaded) {
            String path3 = (String) args[0];
            ArrayList<DelayedMessage> arr12 = this.delayedMessages.get(path3);
            if (arr12 != null) {
                for (int a10 = 0; a10 < arr12.size(); a10++) {
                    performSendDelayedMessage(arr12.get(a10));
                }
                this.delayedMessages.remove(path3);
            }
        } else if ((id == NotificationCenter.httpFileDidFailedLoad || id == NotificationCenter.fileLoadFailed) && (arr = this.delayedMessages.get((path = (String) args[0]))) != null) {
            for (int a11 = 0; a11 < arr.size(); a11++) {
                arr.get(a11).markAsError();
            }
            this.delayedMessages.remove(path);
        }
    }

    /* renamed from: lambda$didReceivedNotification$2$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1157x5b683917(final File cacheFile, final MessageObject messageObject, final DelayedMessage message, final String path) {
        final TLRPC.TL_photo photo = generatePhotoSizes(cacheFile.toString(), null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda58
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1156x7626ca56(photo, messageObject, cacheFile, message, path);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$1$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1156x7626ca56(TLRPC.TL_photo photo, MessageObject messageObject, File cacheFile, DelayedMessage message, String path) {
        if (photo != null) {
            messageObject.messageOwner.media.photo = photo;
            messageObject.messageOwner.attachPath = cacheFile.toString();
            ArrayList<TLRPC.Message> messages = new ArrayList<>();
            messages.add(messageObject.messageOwner);
            getMessagesStorage().putMessages(messages, false, true, false, 0, messageObject.scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            message.photoSize = photo.sizes.get(photo.sizes.size() - 1);
            message.locationParent = photo;
            message.httpLocation = null;
            if (message.type == 4) {
                message.performMediaUpload = true;
                performSendDelayedMessage(message, message.messageObjects.indexOf(messageObject));
                return;
            }
            performSendDelayedMessage(message);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("can't load image " + path + " to file " + cacheFile.toString());
        }
        message.markAsError();
    }

    /* renamed from: lambda$didReceivedNotification$4$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1159x25eb1699(final DelayedMessage message, final File cacheFile, final MessageObject messageObject) {
        final TLRPC.Document document = message.obj.getDocument();
        boolean z = false;
        if (document.thumbs.isEmpty() || (document.thumbs.get(0).location instanceof TLRPC.TL_fileLocationUnavailable)) {
            try {
                Bitmap bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                if (bitmap != null) {
                    document.thumbs.clear();
                    ArrayList<TLRPC.PhotoSize> arrayList = document.thumbs;
                    if (message.sendEncryptedRequest != null) {
                        z = true;
                    }
                    arrayList.add(ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z));
                    bitmap.recycle();
                }
            } catch (Exception e) {
                document.thumbs.clear();
                FileLog.e(e);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1158x40a9a7d8(message, cacheFile, document, messageObject);
            }
        });
    }

    /* renamed from: lambda$didReceivedNotification$3$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1158x40a9a7d8(DelayedMessage message, File cacheFile, TLRPC.Document document, MessageObject messageObject) {
        message.httpLocation = null;
        message.obj.messageOwner.attachPath = cacheFile.toString();
        if (!document.thumbs.isEmpty()) {
            TLRPC.PhotoSize photoSize = document.thumbs.get(0);
            if (!(photoSize instanceof TLRPC.TL_photoStrippedSize)) {
                message.photoSize = photoSize;
                message.locationParent = document;
            }
        }
        ArrayList<TLRPC.Message> messages = new ArrayList<>();
        messages.add(messageObject.messageOwner);
        getMessagesStorage().putMessages(messages, false, true, false, 0, messageObject.scheduled);
        message.performMediaUpload = true;
        performSendDelayedMessage(message);
        getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, message.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject object) {
        object.cancelEditing = true;
        object.messageOwner.media = object.previousMedia;
        object.messageOwner.message = object.previousMessage;
        object.messageOwner.entities = object.previousMessageEntities;
        object.messageOwner.attachPath = object.previousAttachPath;
        object.messageOwner.send_state = 0;
        if (object.messageOwner.entities != null) {
            object.messageOwner.flags |= 128;
        } else {
            object.messageOwner.flags &= -129;
        }
        object.previousMedia = null;
        object.previousMessage = null;
        object.previousMessageEntities = null;
        object.previousAttachPath = null;
        object.videoEditedInfo = null;
        object.type = -1;
        object.setType();
        object.caption = null;
        if (object.type != 0) {
            object.generateCaption();
        } else {
            object.resetLayout();
            object.checkLayout();
        }
        ArrayList<TLRPC.Message> arr = new ArrayList<>();
        arr.add(object.messageOwner);
        getMessagesStorage().putMessages(arr, false, true, false, 0, object.scheduled);
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(object);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(object.getDialogId()), arrayList);
    }

    public void cancelSendingMessage(MessageObject object) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(object);
        cancelSendingMessage(arrayList);
    }

    public void cancelSendingMessage(ArrayList<MessageObject> objects) {
        TLRPC.Message sendingMessage;
        Iterator<Map.Entry<String, ArrayList<DelayedMessage>>> it;
        boolean enc;
        long dialogId;
        int b;
        MessageObject messageObject;
        ArrayList<String> keysToRemove;
        ArrayList<MessageObject> arrayList = objects;
        ArrayList<String> keysToRemove2 = new ArrayList<>();
        ArrayList<DelayedMessage> checkReadyToSendGroups = new ArrayList<>();
        ArrayList<Integer> messageIds = new ArrayList<>();
        int c = 0;
        boolean enc2 = false;
        boolean scheduled = false;
        long dialogId2 = 0;
        while (c < objects.size()) {
            MessageObject object = arrayList.get(c);
            if (object.scheduled) {
                scheduled = true;
            }
            dialogId2 = object.getDialogId();
            messageIds.add(Integer.valueOf(object.getId()));
            TLRPC.Message sendingMessage2 = removeFromSendingMessages(object.getId(), object.scheduled);
            if (sendingMessage2 != null) {
                getConnectionsManager().cancelRequest(sendingMessage2.reqId, true);
            }
            Iterator<Map.Entry<String, ArrayList<DelayedMessage>>> it2 = this.delayedMessages.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<String, ArrayList<DelayedMessage>> entry = it2.next();
                ArrayList<DelayedMessage> messages = entry.getValue();
                int a = 0;
                while (true) {
                    if (a >= messages.size()) {
                        sendingMessage = sendingMessage2;
                        it = it2;
                        enc = enc2;
                        dialogId = dialogId2;
                        break;
                    }
                    DelayedMessage message = messages.get(a);
                    sendingMessage = sendingMessage2;
                    it = it2;
                    if (message.type == 4) {
                        MessageObject messageObject2 = null;
                        int index = 0;
                        while (true) {
                            MessageObject messageObject3 = messageObject2;
                            if (index >= message.messageObjects.size()) {
                                enc = enc2;
                                b = -1;
                                messageObject = messageObject3;
                                break;
                            }
                            MessageObject messageObject4 = message.messageObjects.get(index);
                            enc = enc2;
                            if (messageObject4.getId() == object.getId()) {
                                removeFromUploadingMessages(object.getId(), object.scheduled);
                                b = index;
                                messageObject = messageObject4;
                                break;
                            }
                            index++;
                            messageObject2 = messageObject4;
                            enc2 = enc;
                        }
                        if (b >= 0) {
                            message.messageObjects.remove(b);
                            message.messages.remove(b);
                            message.originalPaths.remove(b);
                            if (!message.parentObjects.isEmpty()) {
                                message.parentObjects.remove(b);
                            }
                            if (message.sendRequest != null) {
                                dialogId = dialogId2;
                                ((TLRPC.TL_messages_sendMultiMedia) message.sendRequest).multi_media.remove(b);
                            } else {
                                dialogId = dialogId2;
                                TLRPC.TL_messages_sendEncryptedMultiMedia request = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                                request.messages.remove(b);
                                request.files.remove(b);
                            }
                            MediaController.getInstance().cancelVideoConvert(object);
                            String keyToRemove = (String) message.extraHashMap.get(messageObject);
                            if (keyToRemove != null) {
                                keysToRemove2.add(keyToRemove);
                            }
                            if (message.messageObjects.isEmpty()) {
                                message.sendDelayedRequests();
                                keysToRemove = keysToRemove2;
                            } else {
                                int i = message.finalGroupMessage;
                                int index2 = object.getId();
                                if (i != index2) {
                                    keysToRemove = keysToRemove2;
                                } else {
                                    MessageObject prevMessage = message.messageObjects.get(message.messageObjects.size() - 1);
                                    message.finalGroupMessage = prevMessage.getId();
                                    prevMessage.messageOwner.params.put("final", IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
                                    TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
                                    messagesRes.messages.add(prevMessage.messageOwner);
                                    keysToRemove = keysToRemove2;
                                    getMessagesStorage().putMessages((TLRPC.messages_Messages) messagesRes, message.peer, -2, 0, false, scheduled);
                                }
                                if (!checkReadyToSendGroups.contains(message)) {
                                    checkReadyToSendGroups.add(message);
                                }
                            }
                            keysToRemove2 = keysToRemove;
                        } else {
                            dialogId = dialogId2;
                        }
                    } else {
                        ArrayList<String> keysToRemove3 = keysToRemove2;
                        enc = enc2;
                        dialogId = dialogId2;
                        if (message.obj.getId() == object.getId()) {
                            removeFromUploadingMessages(object.getId(), object.scheduled);
                            messages.remove(a);
                            message.sendDelayedRequests();
                            MediaController.getInstance().cancelVideoConvert(message.obj);
                            if (messages.size() != 0) {
                                keysToRemove2 = keysToRemove3;
                            } else {
                                keysToRemove2 = keysToRemove3;
                                keysToRemove2.add(entry.getKey());
                                if (message.sendEncryptedRequest != null) {
                                    enc2 = true;
                                }
                            }
                        } else {
                            keysToRemove2 = keysToRemove3;
                            a++;
                            sendingMessage2 = sendingMessage;
                            it2 = it;
                            enc2 = enc;
                            dialogId2 = dialogId;
                        }
                    }
                }
                enc2 = enc;
                sendingMessage2 = sendingMessage;
                it2 = it;
                dialogId2 = dialogId;
            }
            c++;
            arrayList = objects;
        }
        for (int a2 = 0; a2 < keysToRemove2.size(); a2++) {
            String key = keysToRemove2.get(a2);
            if (key.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(key);
            } else {
                getFileLoader().cancelFileUpload(key, enc2);
            }
            stopVideoService(key);
            this.delayedMessages.remove(key);
        }
        int N = checkReadyToSendGroups.size();
        for (int a3 = 0; a3 < N; a3++) {
            sendReadyToSendGroup(checkReadyToSendGroups.get(a3), false, true);
        }
        int a4 = objects.size();
        if (a4 == 1 && objects.get(0).isEditing() && objects.get(0).previousMedia != null) {
            revertEditingMessageObject(objects.get(0));
            return;
        }
        getMessagesController().deleteMessages(messageIds, null, null, dialogId2, false, scheduled);
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessage(messageObject, null, null, null, null, null, true, messageObject);
            }
            return false;
        } else if (messageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) {
            int enc_id = DialogObject.getEncryptedChatId(messageObject.getDialogId());
            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(enc_id));
            if (encryptedChat == null) {
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                messageObject.messageOwner.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                getSecretChatHelper().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                getSecretChatHelper().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                getSecretChatHelper().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                getSecretChatHelper().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!(messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionTyping)) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionResend) {
                    getSecretChatHelper().sendResendMessage(encryptedChat, 0, 0, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                    getSecretChatHelper().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                    getSecretChatHelper().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0L);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                    getSecretChatHelper().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                    getSecretChatHelper().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNoop) {
                    getSecretChatHelper().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        } else {
            if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(messageObject.getDialogId()));
                sendScreenshotMessage(user, messageObject.getReplyMsgId(), messageObject.messageOwner);
            }
            if (unsent) {
                this.unsentMessages.put(messageObject.getId(), messageObject);
            }
            sendMessage(messageObject);
            return true;
        }
    }

    public void processSentMessage(int id) {
        int prevSize = this.unsentMessages.size();
        this.unsentMessages.remove(id);
        if (prevSize != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long did) {
        ArrayList<TLRPC.MessageEntity> entities;
        HashMap<String, String> params;
        if (messageObject == null) {
            return;
        }
        if (messageObject.messageOwner.media != null && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame) && !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
            if (DialogObject.isEncryptedDialog(did) && messageObject.messageOwner.peer_id != null && ((messageObject.messageOwner.media.photo instanceof TLRPC.TL_photo) || (messageObject.messageOwner.media.document instanceof TLRPC.TL_document))) {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("parentObject", "sent_" + messageObject.messageOwner.peer_id.channel_id + "_" + messageObject.getId());
                params = params2;
            } else {
                params = null;
            }
            if (messageObject.messageOwner.media.photo instanceof TLRPC.TL_photo) {
                sendMessage((TLRPC.TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, null, messageObject.messageOwner.message, messageObject.messageOwner.entities, null, params, true, 0, messageObject.messageOwner.media.ttl_seconds, messageObject);
            } else if (messageObject.messageOwner.media.document instanceof TLRPC.TL_document) {
                sendMessage((TLRPC.TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, null, messageObject.messageOwner.message, messageObject.messageOwner.entities, null, params, true, 0, messageObject.messageOwner.media.ttl_seconds, messageObject, null);
            } else if ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, did, messageObject.replyMessageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                TLRPC.User user = new TLRPC.TL_userContact_old2();
                user.phone = messageObject.messageOwner.media.phone_number;
                user.first_name = messageObject.messageOwner.media.first_name;
                user.last_name = messageObject.messageOwner.media.last_name;
                user.id = messageObject.messageOwner.media.user_id;
                sendMessage(user, did, messageObject.replyMessageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            } else if (!DialogObject.isEncryptedDialog(did)) {
                ArrayList<MessageObject> arrayList = new ArrayList<>();
                arrayList.add(messageObject);
                sendMessage(arrayList, did, true, false, true, 0);
            }
        } else if (messageObject.messageOwner.message != null) {
            TLRPC.WebPage webPage = null;
            if (messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                webPage = messageObject.messageOwner.media.webpage;
            }
            if (messageObject.messageOwner.entities != null && !messageObject.messageOwner.entities.isEmpty()) {
                ArrayList<TLRPC.MessageEntity> entities2 = new ArrayList<>();
                for (int a = 0; a < messageObject.messageOwner.entities.size(); a++) {
                    TLRPC.MessageEntity entity = messageObject.messageOwner.entities.get(a);
                    if ((entity instanceof TLRPC.TL_messageEntityBold) || (entity instanceof TLRPC.TL_messageEntityItalic) || (entity instanceof TLRPC.TL_messageEntityPre) || (entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityTextUrl) || (entity instanceof TLRPC.TL_messageEntitySpoiler)) {
                        entities2.add(entity);
                    }
                }
                entities = entities2;
            } else {
                entities = null;
            }
            sendMessage(messageObject.messageOwner.message, did, messageObject.replyMessageObject, null, webPage, true, entities, null, null, true, 0, null);
        } else if (DialogObject.isEncryptedDialog(did)) {
            ArrayList<MessageObject> arrayList2 = new ArrayList<>();
            arrayList2.add(messageObject);
            sendMessage(arrayList2, did, true, false, true, 0);
        }
    }

    public void sendScreenshotMessage(TLRPC.User user, int messageId, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        if (user != null && messageId != 0) {
            if (user.id == getUserConfig().getClientUserId()) {
                return;
            }
            TLRPC.TL_messages_sendScreenshotNotification req = new TLRPC.TL_messages_sendScreenshotNotification();
            req.peer = new TLRPC.TL_inputPeerUser();
            req.peer.access_hash = user.access_hash;
            req.peer.user_id = user.id;
            if (resendMessage != null) {
                req.reply_to_msg_id = messageId;
                req.random_id = resendMessage.random_id;
                message = resendMessage;
            } else {
                TLRPC.Message message2 = new TLRPC.TL_messageService();
                message2.random_id = getNextRandomId();
                message2.dialog_id = user.id;
                message2.unread = true;
                message2.out = true;
                int newMessageId = getUserConfig().getNewMessageId();
                message2.id = newMessageId;
                message2.local_id = newMessageId;
                message2.from_id = new TLRPC.TL_peerUser();
                message2.from_id.user_id = getUserConfig().getClientUserId();
                message2.flags |= 256;
                message2.flags |= 8;
                message2.reply_to = new TLRPC.TL_messageReplyHeader();
                message2.reply_to.reply_to_msg_id = messageId;
                message2.peer_id = new TLRPC.TL_peerUser();
                message2.peer_id.user_id = user.id;
                message2.date = getConnectionsManager().getCurrentTime();
                message2.action = new TLRPC.TL_messageActionScreenshotTaken();
                getUserConfig().saveConfig(false);
                message = message2;
            }
            req.random_id = message.random_id;
            MessageObject newMsgObj = new MessageObject(this.currentAccount, message, false, true);
            newMsgObj.messageOwner.send_state = 1;
            newMsgObj.wasJustSent = true;
            ArrayList<MessageObject> objArr = new ArrayList<>();
            objArr.add(newMsgObj);
            getMessagesController().updateInterfaceWithMessages(message.dialog_id, objArr, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList<TLRPC.Message> arr = new ArrayList<>();
            arr.add(message);
            getMessagesStorage().putMessages(arr, false, true, false, 0, false);
            performSendMessageRequest(req, newMsgObj, null, null, null, null, false);
        }
    }

    public void sendSticker(TLRPC.Document document, String query, final long peer, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final Object parentObject, final MessageObject.SendAnimationData sendAnimationData, final boolean notify, final int scheduleDate) {
        TLRPC.Document document2;
        HashMap<String, String> params;
        if (document == null) {
            return;
        }
        if (!DialogObject.isEncryptedDialog(peer)) {
            document2 = document;
        } else {
            int encryptedId = DialogObject.getEncryptedChatId(peer);
            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(encryptedId));
            if (encryptedChat == null) {
                return;
            }
            TLRPC.TL_document_layer82 newDocument = new TLRPC.TL_document_layer82();
            newDocument.id = document.id;
            newDocument.access_hash = document.access_hash;
            newDocument.date = document.date;
            newDocument.mime_type = document.mime_type;
            newDocument.file_reference = document.file_reference;
            if (newDocument.file_reference == null) {
                newDocument.file_reference = new byte[0];
            }
            newDocument.size = document.size;
            newDocument.dc_id = document.dc_id;
            newDocument.attributes = new ArrayList<>(document.attributes);
            if (newDocument.mime_type == null) {
                newDocument.mime_type = "";
            }
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
            if ((thumb instanceof TLRPC.TL_photoSize) || (thumb instanceof TLRPC.TL_photoSizeProgressive)) {
                File file = FileLoader.getInstance(this.currentAccount).getPathToAttach(thumb, true);
                if (file.exists()) {
                    try {
                        int length = (int) file.length();
                        byte[] arr = new byte[(int) file.length()];
                        RandomAccessFile reader = new RandomAccessFile(file, "r");
                        reader.readFully(arr);
                        TLRPC.PhotoSize newThumb = new TLRPC.TL_photoCachedSize();
                        TLRPC.TL_fileLocation_layer82 fileLocation = new TLRPC.TL_fileLocation_layer82();
                        fileLocation.dc_id = thumb.location.dc_id;
                        fileLocation.volume_id = thumb.location.volume_id;
                        fileLocation.local_id = thumb.location.local_id;
                        fileLocation.secret = thumb.location.secret;
                        newThumb.location = fileLocation;
                        newThumb.size = thumb.size;
                        newThumb.w = thumb.w;
                        newThumb.h = thumb.h;
                        newThumb.type = thumb.type;
                        newThumb.bytes = arr;
                        newDocument.thumbs.add(newThumb);
                        newDocument.flags = 1 | newDocument.flags;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            if (newDocument.thumbs.isEmpty()) {
                TLRPC.PhotoSize thumb2 = new TLRPC.TL_photoSizeEmpty();
                thumb2.type = "s";
                newDocument.thumbs.add(thumb2);
            }
            document2 = newDocument;
        }
        final TLRPC.Document finalDocument = document2;
        if (MessageObject.isGifDocument(document2)) {
            mediaSendQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1216lambda$sendSticker$6$orgtelegrammessengerSendMessagesHelper(finalDocument, peer, replyToMsg, replyToTopMsg, notify, scheduleDate, parentObject, sendAnimationData);
                }
            });
            return;
        }
        if (!TextUtils.isEmpty(query)) {
            params = new HashMap<>();
            params.put(SearchIntents.EXTRA_QUERY, query);
        } else {
            params = null;
        }
        sendMessage((TLRPC.TL_document) finalDocument, null, null, peer, replyToMsg, replyToTopMsg, null, null, null, params, notify, scheduleDate, 0, parentObject, sendAnimationData);
    }

    /* renamed from: lambda$sendSticker$6$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1216lambda$sendSticker$6$orgtelegrammessengerSendMessagesHelper(final TLRPC.Document finalDocument, final long peer, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final boolean notify, final int scheduleDate, final Object parentObject, final MessageObject.SendAnimationData sendAnimationData) {
        String docExt;
        File docFile;
        final Bitmap[] bitmapFinal = new Bitmap[1];
        final String[] keyFinal = new String[1];
        String mediaLocationKey = ImageLocation.getForDocument(finalDocument).getKey(null, null, false);
        if (MimeTypes.VIDEO_MP4.equals(finalDocument.mime_type)) {
            docExt = ".mp4";
        } else if ("video/x-matroska".equals(finalDocument.mime_type)) {
            docExt = ".mkv";
        } else {
            docExt = "";
        }
        File directory = FileLoader.getDirectory(3);
        File docFile2 = new File(directory, mediaLocationKey + docExt);
        if (docFile2.exists()) {
            docFile = docFile2;
        } else {
            File directory2 = FileLoader.getDirectory(2);
            docFile = new File(directory2, mediaLocationKey + docExt);
        }
        ensureMediaThumbExists(getAccountInstance(), false, finalDocument, docFile.getAbsolutePath(), null, 0L);
        keyFinal[0] = getKeyForPhotoSize(getAccountInstance(), FileLoader.getClosestPhotoSizeWithSize(finalDocument.thumbs, GroupCallActivity.TABLET_LIST_SIZE), bitmapFinal, true, true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda69
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1215lambda$sendSticker$5$orgtelegrammessengerSendMessagesHelper(bitmapFinal, keyFinal, finalDocument, peer, replyToMsg, replyToTopMsg, notify, scheduleDate, parentObject, sendAnimationData);
            }
        });
    }

    /* renamed from: lambda$sendSticker$5$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1215lambda$sendSticker$5$orgtelegrammessengerSendMessagesHelper(Bitmap[] bitmapFinal, String[] keyFinal, TLRPC.Document finalDocument, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, boolean notify, int scheduleDate, Object parentObject, MessageObject.SendAnimationData sendAnimationData) {
        if (bitmapFinal[0] != null && keyFinal[0] != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapFinal[0]), keyFinal[0], false);
        }
        sendMessage((TLRPC.TL_document) finalDocument, null, null, peer, replyToMsg, replyToTopMsg, null, null, null, null, notify, scheduleDate, 0, parentObject, sendAnimationData);
    }

    /* JADX WARN: Removed duplicated region for block: B:119:0x039a  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x047f  */
    /* JADX WARN: Removed duplicated region for block: B:313:0x08ea  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x08fa  */
    /* JADX WARN: Removed duplicated region for block: B:319:0x0927  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x096c  */
    /* JADX WARN: Removed duplicated region for block: B:330:0x096e  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x09b3  */
    /* JADX WARN: Removed duplicated region for block: B:334:0x09db  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x02dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r53, final long r54, boolean r56, boolean r57, boolean r58, final int r59) {
        /*
            Method dump skipped, instructions count: 2758
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, boolean, boolean, int):int");
    }

    /* renamed from: lambda$sendMessage$14$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1207lambda$sendMessage$14$orgtelegrammessengerSendMessagesHelper(final long peer, final int scheduleDate, boolean scheduledOnline, boolean toMyself, LongSparseArray messagesByRandomIdsFinal, ArrayList newMsgObjArr, ArrayList newMsgArr, final MessageObject msgObj, final TLRPC.Peer peer_id, final TLRPC.TL_messages_forwardMessages req, TLObject response, final TLRPC.TL_error error) {
        SendMessagesHelper sendMessagesHelper;
        Integer value;
        SparseLongArray newMessagesByIds;
        TLRPC.Updates updates;
        int i;
        int sentCount;
        boolean currentSchedule;
        TLRPC.Message message;
        int sentCount2;
        int i2 = scheduleDate;
        ArrayList arrayList = newMsgObjArr;
        ArrayList arrayList2 = newMsgArr;
        if (error == null) {
            SparseLongArray newMessagesByIds2 = new SparseLongArray();
            TLRPC.Updates updates2 = (TLRPC.Updates) response;
            int a1 = 0;
            while (a1 < updates2.updates.size()) {
                TLRPC.Update update = updates2.updates.get(a1);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    TLRPC.TL_updateMessageID updateMessageID = (TLRPC.TL_updateMessageID) update;
                    newMessagesByIds2.put(updateMessageID.id, updateMessageID.random_id);
                    updates2.updates.remove(a1);
                    a1--;
                }
                a1++;
            }
            Integer value2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(peer));
            if (value2 == null) {
                Integer value3 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, peer));
                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(peer), value3);
                value = value3;
            } else {
                value = value2;
            }
            int a12 = 0;
            int sentCount3 = 0;
            while (a12 < updates2.updates.size()) {
                TLRPC.Update update2 = updates2.updates.get(a12);
                if ((update2 instanceof TLRPC.TL_updateNewMessage) || (update2 instanceof TLRPC.TL_updateNewChannelMessage) || (update2 instanceof TLRPC.TL_updateNewScheduledMessage)) {
                    boolean currentSchedule2 = i2 != 0;
                    updates2.updates.remove(a12);
                    int a13 = a12 - 1;
                    if (update2 instanceof TLRPC.TL_updateNewMessage) {
                        TLRPC.TL_updateNewMessage updateNewMessage = (TLRPC.TL_updateNewMessage) update2;
                        message = updateNewMessage.message;
                        MessagesController messagesController = getMessagesController();
                        currentSchedule = currentSchedule2;
                        int i3 = updateNewMessage.pts;
                        sentCount = sentCount3;
                        int sentCount4 = updateNewMessage.pts_count;
                        messagesController.processNewDifferenceParams(-1, i3, -1, sentCount4);
                    } else {
                        currentSchedule = currentSchedule2;
                        sentCount = sentCount3;
                        if (update2 instanceof TLRPC.TL_updateNewScheduledMessage) {
                            message = ((TLRPC.TL_updateNewScheduledMessage) update2).message;
                        } else {
                            TLRPC.TL_updateNewChannelMessage updateNewChannelMessage = (TLRPC.TL_updateNewChannelMessage) update2;
                            message = updateNewChannelMessage.message;
                            getMessagesController().processNewChannelDifferenceParams(updateNewChannelMessage.pts, updateNewChannelMessage.pts_count, message.peer_id.channel_id);
                        }
                    }
                    if (scheduledOnline && message.date != 2147483646) {
                        currentSchedule = false;
                    }
                    ImageLoader.saveMessageThumbs(message);
                    if (!currentSchedule) {
                        message.unread = value.intValue() < message.id;
                    }
                    if (toMyself) {
                        message.out = true;
                        message.unread = false;
                        message.media_unread = false;
                    }
                    long random_id = newMessagesByIds2.get(message.id);
                    if (random_id != 0) {
                        final TLRPC.Message newMsgObj1 = (TLRPC.Message) messagesByRandomIdsFinal.get(random_id);
                        if (newMsgObj1 == null) {
                            newMessagesByIds = newMessagesByIds2;
                            updates = updates2;
                            sentCount2 = sentCount;
                            i = 1;
                        } else {
                            int index = arrayList.indexOf(newMsgObj1);
                            if (index == -1) {
                                newMessagesByIds = newMessagesByIds2;
                                updates = updates2;
                                sentCount2 = sentCount;
                                i = 1;
                            } else {
                                MessageObject msgObj1 = (MessageObject) arrayList2.get(index);
                                arrayList.remove(index);
                                arrayList2.remove(index);
                                final int index2 = newMsgObj1.id;
                                final ArrayList<TLRPC.Message> sentMessages = new ArrayList<>();
                                sentMessages.add(message);
                                msgObj1.messageOwner.post_author = message.post_author;
                                if ((message.flags & ConnectionsManager.FileTypeVideo) != 0) {
                                    msgObj1.messageOwner.ttl_period = message.ttl_period;
                                    msgObj1.messageOwner.flags |= ConnectionsManager.FileTypeVideo;
                                }
                                newMessagesByIds = newMessagesByIds2;
                                updateMediaPaths(msgObj1, message, message.id, null, true);
                                final int existFlags = msgObj1.getMediaExistanceFlags();
                                newMsgObj1.id = message.id;
                                int sentCount5 = sentCount + 1;
                                if (i2 == 0 || currentSchedule) {
                                    updates = updates2;
                                    i = 1;
                                    final TLRPC.Message newMsgObj12 = message;
                                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda47
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            SendMessagesHelper.this.m1204lambda$sendMessage$11$orgtelegrammessengerSendMessagesHelper(newMsgObj1, peer_id, index2, scheduleDate, sentMessages, peer, newMsgObj12, existFlags);
                                        }
                                    });
                                } else {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            SendMessagesHelper.this.m1210lambda$sendMessage$9$orgtelegrammessengerSendMessagesHelper(index2, newMsgObj1, sentMessages, msgObj, scheduleDate);
                                        }
                                    });
                                    updates = updates2;
                                    i = 1;
                                }
                                a12 = a13;
                                sentCount3 = sentCount5;
                            }
                        }
                    } else {
                        newMessagesByIds = newMessagesByIds2;
                        updates = updates2;
                        sentCount2 = sentCount;
                        i = 1;
                    }
                    sentCount3 = sentCount2;
                    a12 = a13;
                } else {
                    newMessagesByIds = newMessagesByIds2;
                    updates = updates2;
                    i = 1;
                }
                a12 += i;
                arrayList = newMsgObjArr;
                arrayList2 = newMsgArr;
                updates2 = updates;
                newMessagesByIds2 = newMessagesByIds;
                i2 = scheduleDate;
            }
            TLRPC.Updates updates3 = updates2;
            int sentCount6 = sentCount3;
            if (!updates3.updates.isEmpty()) {
                getMessagesController().processUpdates(updates3, false);
            }
            getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, sentCount6);
            sendMessagesHelper = this;
        } else {
            sendMessagesHelper = this;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1205lambda$sendMessage$12$orgtelegrammessengerSendMessagesHelper(error, req);
                }
            });
        }
        for (int a14 = 0; a14 < newMsgObjArr.size(); a14++) {
            final TLRPC.Message newMsgObj13 = (TLRPC.Message) newMsgObjArr.get(a14);
            getMessagesStorage().markMessageAsSendError(newMsgObj13, scheduleDate != 0);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1206lambda$sendMessage$13$orgtelegrammessengerSendMessagesHelper(newMsgObj13, scheduleDate);
                }
            });
        }
    }

    /* renamed from: lambda$sendMessage$9$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1210lambda$sendMessage$9$orgtelegrammessengerSendMessagesHelper(final int oldId, final TLRPC.Message newMsgObj1, final ArrayList sentMessages, final MessageObject msgObj, final int scheduleDate) {
        ArrayList<Integer> messageIds = new ArrayList<>();
        messageIds.add(Integer.valueOf(oldId));
        getMessagesController().deleteMessages(messageIds, null, null, newMsgObj1.dialog_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1209lambda$sendMessage$8$orgtelegrammessengerSendMessagesHelper(sentMessages, msgObj, newMsgObj1, oldId, scheduleDate);
            }
        });
    }

    /* renamed from: lambda$sendMessage$8$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1209lambda$sendMessage$8$orgtelegrammessengerSendMessagesHelper(ArrayList sentMessages, final MessageObject msgObj, final TLRPC.Message newMsgObj1, final int oldId, final int scheduleDate) {
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1208lambda$sendMessage$7$orgtelegrammessengerSendMessagesHelper(msgObj, newMsgObj1, oldId, scheduleDate);
            }
        });
    }

    /* renamed from: lambda$sendMessage$7$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1208lambda$sendMessage$7$orgtelegrammessengerSendMessagesHelper(MessageObject msgObj, TLRPC.Message newMsgObj1, int oldId, int scheduleDate) {
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        boolean z = true;
        messageObjects.add(new MessageObject(msgObj.currentAccount, msgObj.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(newMsgObj1.dialog_id, messageObjects, false);
        getMediaDataController().increasePeerRaiting(newMsgObj1.dialog_id);
        processSentMessage(oldId);
        if (scheduleDate == 0) {
            z = false;
        }
        removeFromSendingMessages(oldId, z);
    }

    /* renamed from: lambda$sendMessage$11$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1204lambda$sendMessage$11$orgtelegrammessengerSendMessagesHelper(final TLRPC.Message newMsgObj1, TLRPC.Peer peer_id, final int oldId, final int scheduleDate, ArrayList sentMessages, final long peer, final TLRPC.Message message, final int existFlags) {
        getMessagesStorage().updateMessageStateAndId(newMsgObj1.random_id, MessageObject.getPeerId(peer_id), Integer.valueOf(oldId), newMsgObj1.id, 0, false, scheduleDate != 0 ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, false, false, 0, scheduleDate != 0);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1203lambda$sendMessage$10$orgtelegrammessengerSendMessagesHelper(newMsgObj1, peer, oldId, message, existFlags, scheduleDate);
            }
        });
    }

    /* renamed from: lambda$sendMessage$10$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1203lambda$sendMessage$10$orgtelegrammessengerSendMessagesHelper(TLRPC.Message newMsgObj1, long peer, int oldId, TLRPC.Message message, int existFlags, int scheduleDate) {
        boolean z = false;
        newMsgObj1.send_state = 0;
        getMediaDataController().increasePeerRaiting(peer);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[7];
        objArr[0] = Integer.valueOf(oldId);
        objArr[1] = Integer.valueOf(message.id);
        objArr[2] = message;
        objArr[3] = Long.valueOf(peer);
        objArr[4] = 0L;
        objArr[5] = Integer.valueOf(existFlags);
        objArr[6] = Boolean.valueOf(scheduleDate != 0);
        notificationCenter.postNotificationName(i, objArr);
        processSentMessage(oldId);
        if (scheduleDate != 0) {
            z = true;
        }
        removeFromSendingMessages(oldId, z);
    }

    /* renamed from: lambda$sendMessage$12$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1205lambda$sendMessage$12$orgtelegrammessengerSendMessagesHelper(TLRPC.TL_error error, TLRPC.TL_messages_forwardMessages req) {
        AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
    }

    /* renamed from: lambda$sendMessage$13$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1206lambda$sendMessage$13$orgtelegrammessengerSendMessagesHelper(TLRPC.Message newMsgObj1, int scheduleDate) {
        newMsgObj1.send_state = 2;
        boolean z = true;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj1.id));
        processSentMessage(newMsgObj1.id);
        int i = newMsgObj1.id;
        if (scheduleDate == 0) {
            z = false;
        }
        removeFromSendingMessages(i, z);
    }

    private void writePreviousMessageData(TLRPC.Message message, SerializedData data) {
        if (message.media == null) {
            TLRPC.TL_messageMediaEmpty media = new TLRPC.TL_messageMediaEmpty();
            media.serializeToStream(data);
        } else {
            message.media.serializeToStream(data);
        }
        String str = "";
        data.writeString(message.message != null ? message.message : str);
        if (message.attachPath != null) {
            str = message.attachPath;
        }
        data.writeString(str);
        int count = message.entities.size();
        data.writeInt32(count);
        for (int a = 0; a < count; a++) {
            message.entities.get(a).serializeToStream(data);
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(15:(1:6)(1:7)|8|388|9|(17:(25:421|11|(1:16)(2:13|14)|17|22|(11:24|(9:31|(1:33)(2:34|(7:36|37|395|38|(2:42|44)|43|44)(1:47))|49|414|50|(1:56)(1:55)|428|57|58)|48|49|414|50|(0)|56|428|57|58)(9:63|(2:65|66)|67|(2:69|70)|71|394|(3:73|74|(1:81)(1:80))(1:(6:85|(4:89|(1:92)|93|94)|90|(0)|93|94)(1:95))|96|97)|404|98|(2:424|100)|103|104|105|(3:390|108|109)|412|113|(2:115|116)|117|(1:122)(2:120|121)|(11:397|124|(5:386|126|127|411|(2:129|(1:131)(2:132|(2:134|135)(1:136)))(3:139|(2:141|142)(4:143|408|144|(2:149|(1:151))(1:148))|152))(1:157)|392|158|159|(3:161|(1:166)|167)|168|169|419|170)(1:175)|176|(1:184)(2:180|181)|185|(1:376)(15:192|(1:194)(15:195|399|(7:197|(2:199|(4:201|(3:203|204|205)|438|206)(1:207))(1:208)|209|(1:211)(3:212|(1:214)|215)|216|(1:223)(1:222)|224)(3:229|403|(14:436|231|(3:233|234|(4:236|(3:238|239|240)|439|241)(1:242))|243|430|244|(3:(4:247|434|248|(1:250))(1:251)|252|(1:254))(1:257)|258|259|(1:261)(3:262|(1:264)|265)|266|(2:268|(1:270))|271|272)(2:277|(6:279|409|280|281|(3:283|284|285)(6:288|289|422|290|(1:292)|293)|(4:295|(2:297|(1:299))|300|301)(1:302))(1:305)))|406|307|(1:309)|312|(1:314)|315|(5:317|(1:319)(1:320)|321|(1:323)(2:324|(2:326|(1:328)))|329)(1:330)|(1:332)|333|401|(5:335|426|336|337|338)(4:343|417|(1:(2:346|347)(5:348|432|349|350|351))(2:354|(1:(1:357)(1:358))(2:359|(1:361)(2:364|(1:(1:367)(1:368))(2:369|(1:(1:372)(1:373))))))|416)|400)|306|406|307|(0)|312|(0)|315|(0)(0)|(0)|333|401|(0)(0)|400)|377|440)(1:20)|412|113|(0)|117|(0)|122|(0)(0)|176|(1:178)|184|185|(1:187)|189|376|377|440)|21|22|(0)(0)|404|98|(0)|103|104|105|(3:390|108|109)) */
    /* JADX WARN: Can't wrap try/catch for region: R(8:24|(4:(9:31|(1:33)(2:34|(7:36|37|395|38|(2:42|44)|43|44)(1:47))|49|414|50|(1:56)(1:55)|428|57|58)|428|57|58)|48|49|414|50|(0)|56) */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x0229, code lost:
        if (r32.type != 2) goto L117;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x004c, code lost:
        if (org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r6.layer) >= 101) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:380:0x085e, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:381:0x085f, code lost:
        r28 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0109, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x010a, code lost:
        r28 = r5;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:115:0x023f  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x0381  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0069 A[Catch: Exception -> 0x0054, TRY_ENTER, TryCatch #19 {Exception -> 0x0054, blocks: (B:11:0x002e, B:13:0x0042, B:24:0x0069, B:26:0x0071, B:28:0x0077, B:31:0x0080, B:33:0x0088, B:34:0x0095, B:36:0x009d, B:65:0x012c, B:69:0x014c, B:76:0x017f, B:78:0x0185, B:80:0x018b), top: B:421:0x002e }] */
    /* JADX WARN: Removed duplicated region for block: B:309:0x0688 A[Catch: Exception -> 0x0691, TRY_ENTER, TRY_LEAVE, TryCatch #20 {Exception -> 0x0691, blocks: (B:290:0x0609, B:292:0x061f, B:295:0x062b, B:297:0x063f, B:299:0x064c, B:300:0x0650, B:309:0x0688, B:314:0x069b, B:317:0x06ad, B:321:0x06c2, B:323:0x06c8, B:324:0x06d6, B:326:0x06eb, B:328:0x06f1, B:329:0x06fb, B:332:0x0705), top: B:422:0x0609 }] */
    /* JADX WARN: Removed duplicated region for block: B:314:0x069b A[Catch: Exception -> 0x0691, TRY_ENTER, TRY_LEAVE, TryCatch #20 {Exception -> 0x0691, blocks: (B:290:0x0609, B:292:0x061f, B:295:0x062b, B:297:0x063f, B:299:0x064c, B:300:0x0650, B:309:0x0688, B:314:0x069b, B:317:0x06ad, B:321:0x06c2, B:323:0x06c8, B:324:0x06d6, B:326:0x06eb, B:328:0x06f1, B:329:0x06fb, B:332:0x0705), top: B:422:0x0609 }] */
    /* JADX WARN: Removed duplicated region for block: B:317:0x06ad A[Catch: Exception -> 0x0691, TRY_ENTER, TryCatch #20 {Exception -> 0x0691, blocks: (B:290:0x0609, B:292:0x061f, B:295:0x062b, B:297:0x063f, B:299:0x064c, B:300:0x0650, B:309:0x0688, B:314:0x069b, B:317:0x06ad, B:321:0x06c2, B:323:0x06c8, B:324:0x06d6, B:326:0x06eb, B:328:0x06f1, B:329:0x06fb, B:332:0x0705), top: B:422:0x0609 }] */
    /* JADX WARN: Removed duplicated region for block: B:330:0x0701  */
    /* JADX WARN: Removed duplicated region for block: B:332:0x0705 A[Catch: Exception -> 0x0691, TRY_LEAVE, TryCatch #20 {Exception -> 0x0691, blocks: (B:290:0x0609, B:292:0x061f, B:295:0x062b, B:297:0x063f, B:299:0x064c, B:300:0x0650, B:309:0x0688, B:314:0x069b, B:317:0x06ad, B:321:0x06c2, B:323:0x06c8, B:324:0x06d6, B:326:0x06eb, B:328:0x06f1, B:329:0x06fb, B:332:0x0705), top: B:422:0x0609 }] */
    /* JADX WARN: Removed duplicated region for block: B:335:0x070b  */
    /* JADX WARN: Removed duplicated region for block: B:343:0x074f  */
    /* JADX WARN: Removed duplicated region for block: B:397:0x0250 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:424:0x020c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0118 A[Catch: Exception -> 0x0870, TRY_ENTER, TRY_LEAVE, TryCatch #1 {Exception -> 0x0870, blocks: (B:9:0x0022, B:63:0x0118, B:67:0x0132, B:71:0x0152, B:96:0x01f6), top: B:388:0x0022 }] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01e7 A[Catch: Exception -> 0x01b5, TryCatch #4 {Exception -> 0x01b5, blocks: (B:73:0x0169, B:81:0x018f, B:85:0x01c6, B:92:0x01e7, B:93:0x01f0), top: B:394:0x0167 }] */
    /* JADX WARN: Type inference failed for: r1v74, types: [org.telegram.tgnet.TLRPC$TL_inputMediaPhoto] */
    /* JADX WARN: Type inference failed for: r20v12 */
    /* JADX WARN: Type inference failed for: r20v13 */
    /* JADX WARN: Type inference failed for: r20v16 */
    /* JADX WARN: Type inference failed for: r20v17 */
    /* JADX WARN: Type inference failed for: r20v18 */
    /* JADX WARN: Type inference failed for: r22v13 */
    /* JADX WARN: Type inference failed for: r22v14 */
    /* JADX WARN: Type inference failed for: r22v16 */
    /* JADX WARN: Type inference failed for: r22v17 */
    /* JADX WARN: Type inference failed for: r22v18 */
    /* JADX WARN: Type inference failed for: r22v19 */
    /* JADX WARN: Type inference failed for: r22v20 */
    /* JADX WARN: Type inference failed for: r22v3 */
    /* JADX WARN: Type inference failed for: r22v6 */
    /* JADX WARN: Type inference failed for: r22v8 */
    /* JADX WARN: Type inference failed for: r25v12 */
    /* JADX WARN: Type inference failed for: r25v13 */
    /* JADX WARN: Type inference failed for: r25v14 */
    /* JADX WARN: Type inference failed for: r25v2 */
    /* JADX WARN: Type inference failed for: r25v22 */
    /* JADX WARN: Type inference failed for: r25v23 */
    /* JADX WARN: Type inference failed for: r25v24 */
    /* JADX WARN: Type inference failed for: r25v27 */
    /* JADX WARN: Type inference failed for: r25v28 */
    /* JADX WARN: Type inference failed for: r25v5 */
    /* JADX WARN: Type inference failed for: r25v7 */
    /* JADX WARN: Type inference failed for: r2v34, types: [org.telegram.tgnet.TLRPC$TL_inputMediaDocument] */
    /* JADX WARN: Type inference failed for: r31v0, types: [org.telegram.messenger.SendMessagesHelper] */
    /* JADX WARN: Type inference failed for: r34v1 */
    /* JADX WARN: Type inference failed for: r34v2 */
    /* JADX WARN: Type inference failed for: r34v6 */
    /* JADX WARN: Type inference failed for: r34v7 */
    /* JADX WARN: Type inference failed for: r39v0, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r4v35, types: [org.telegram.tgnet.TLRPC$TL_inputMediaDocument] */
    /* JADX WARN: Type inference failed for: r8v14, types: [org.telegram.messenger.MediaDataController] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void editMessage(org.telegram.messenger.MessageObject r32, org.telegram.tgnet.TLRPC.TL_photo r33, org.telegram.messenger.VideoEditedInfo r34, org.telegram.tgnet.TLRPC.TL_document r35, java.lang.String r36, java.util.HashMap<java.lang.String, java.lang.String> r37, boolean r38, java.lang.Object r39) {
        /*
            Method dump skipped, instructions count: 2182
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessage(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, final BaseFragment fragment, ArrayList<TLRPC.MessageEntity> entities, int scheduleDate) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return 0;
        }
        final TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
        req.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
        if (message != null) {
            req.message = message;
            req.flags |= 2048;
            req.no_webpage = !searchLinks;
        }
        req.id = messageObject.getId();
        if (entities != null) {
            req.entities = entities;
            req.flags |= 8;
        }
        if (scheduleDate != 0) {
            req.schedule_date = scheduleDate;
            req.flags |= 32768;
        }
        return getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda86
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1161lambda$editMessage$16$orgtelegrammessengerSendMessagesHelper(fragment, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$editMessage$16$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1161lambda$editMessage$16$orgtelegrammessengerSendMessagesHelper(final BaseFragment fragment, final TLRPC.TL_messages_editMessage req, TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda56
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1160lambda$editMessage$15$orgtelegrammessengerSendMessagesHelper(error, fragment, req);
                }
            });
        }
    }

    /* renamed from: lambda$editMessage$15$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1160lambda$editMessage$15$orgtelegrammessengerSendMessagesHelper(TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_messages_editMessage req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
    }

    public void sendLocation(Location location) {
        TLRPC.TL_messageMediaGeo mediaGeo = new TLRPC.TL_messageMediaGeo();
        mediaGeo.geo = new TLRPC.TL_geoPoint();
        mediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        mediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Map.Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = entry.getValue();
            sendMessage((TLRPC.MessageMedia) mediaGeo, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, TLRPC.KeyboardButton button) {
        if (messageObject == null || button == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(button.data));
        sb.append("_");
        sb.append(button instanceof TLRPC.TL_keyboardButtonGame ? IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE : "0");
        String key = sb.toString();
        this.waitingForLocation.put(key, messageObject);
        this.locationProvider.start();
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, TLRPC.KeyboardButton button) {
        if (messageObject == null || button == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(button.data));
        sb.append("_");
        sb.append(button instanceof TLRPC.TL_keyboardButtonGame ? IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE : "0");
        String key = sb.toString();
        return this.waitingForLocation.containsKey(key);
    }

    public void sendNotificationCallback(final long dialogId, final int msgId, final byte[] data) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1213xb2d0ec92(dialogId, msgId, data);
            }
        });
    }

    /* renamed from: lambda$sendNotificationCallback$19$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1213xb2d0ec92(long dialogId, int msgId, byte[] data) {
        TLRPC.Chat chat;
        TLRPC.User user;
        final String key = dialogId + "_" + msgId + "_" + Utilities.bytesToHex(data) + "_0";
        this.waitingForCallback.put(key, true);
        if (DialogObject.isUserDialog(dialogId)) {
            if (getMessagesController().getUser(Long.valueOf(dialogId)) == null && (user = getMessagesStorage().getUserSync(dialogId)) != null) {
                getMessagesController().putUser(user, true);
            }
        } else if (getMessagesController().getChat(Long.valueOf(-dialogId)) == null && (chat = getMessagesStorage().getChatSync(-dialogId)) != null) {
            getMessagesController().putChat(chat, true);
        }
        TLRPC.TL_messages_getBotCallbackAnswer req = new TLRPC.TL_messages_getBotCallbackAnswer();
        req.peer = getMessagesController().getInputPeer(dialogId);
        req.msg_id = msgId;
        req.game = false;
        if (data != null) {
            req.flags |= 1;
            req.data = data;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda79
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1212xcd8f7dd1(key, tLObject, tL_error);
            }
        }, 2);
        getMessagesController().markDialogAsRead(dialogId, msgId, msgId, 0, false, 0, 0, true, 0);
    }

    /* renamed from: lambda$sendNotificationCallback$17$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1211xe84e0f10(String key) {
        this.waitingForCallback.remove(key);
    }

    /* renamed from: lambda$sendNotificationCallback$18$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1212xcd8f7dd1(final String key, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1211xe84e0f10(key);
            }
        });
    }

    public byte[] isSendingVote(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        String key = "poll_" + messageObject.getPollId();
        return this.waitingForVote.get(key);
    }

    public int sendVote(final MessageObject messageObject, ArrayList<TLRPC.TL_pollAnswer> answers, final Runnable finishRunnable) {
        byte[] options;
        if (messageObject == null) {
            return 0;
        }
        final String key = "poll_" + messageObject.getPollId();
        if (this.waitingForCallback.containsKey(key)) {
            return 0;
        }
        TLRPC.TL_messages_sendVote req = new TLRPC.TL_messages_sendVote();
        req.msg_id = messageObject.getId();
        req.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
        if (answers != null) {
            options = new byte[answers.size()];
            for (int a = 0; a < answers.size(); a++) {
                TLRPC.TL_pollAnswer answer = answers.get(a);
                req.options.add(answer.option);
                options[a] = answer.option[0];
            }
        } else {
            options = new byte[0];
        }
        this.waitingForVote.put(key, options);
        return getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda82
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1218lambda$sendVote$21$orgtelegrammessengerSendMessagesHelper(messageObject, key, finishRunnable, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$sendVote$21$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1218lambda$sendVote$21$orgtelegrammessengerSendMessagesHelper(MessageObject messageObject, final String key, final Runnable finishRunnable, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.voteSendTime.put(messageObject.getPollId(), 0L);
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.elapsedRealtime()));
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1217lambda$sendVote$20$orgtelegrammessengerSendMessagesHelper(key, finishRunnable);
            }
        });
    }

    /* renamed from: lambda$sendVote$20$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1217lambda$sendVote$20$orgtelegrammessengerSendMessagesHelper(String key, Runnable finishRunnable) {
        this.waitingForVote.remove(key);
        if (finishRunnable != null) {
            finishRunnable.run();
        }
    }

    public long getVoteSendTime(long pollId) {
        return this.voteSendTime.get(pollId, 0L).longValue();
    }

    public void sendReaction(MessageObject messageObject, CharSequence reaction, boolean big, ChatActivity parentFragment, final Runnable callback) {
        if (messageObject == null || parentFragment == null) {
            return;
        }
        TLRPC.TL_messages_sendReaction req = new TLRPC.TL_messages_sendReaction();
        if (messageObject.messageOwner.isThreadMessage && messageObject.messageOwner.fwd_from != null) {
            req.peer = getMessagesController().getInputPeer(messageObject.getFromChatId());
            req.msg_id = messageObject.messageOwner.fwd_from.saved_from_msg_id;
        } else {
            req.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
            req.msg_id = messageObject.getId();
        }
        if (reaction != null) {
            req.reaction = reaction.toString();
            req.flags |= 1;
        }
        if (big) {
            req.flags |= 2;
            req.big = true;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda78
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1214lambda$sendReaction$22$orgtelegrammessengerSendMessagesHelper(callback, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$sendReaction$22$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1214lambda$sendReaction$22$orgtelegrammessengerSendMessagesHelper(Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            if (callback != null) {
                AndroidUtilities.runOnUIThread(callback);
            }
        }
    }

    public void requestUrlAuth(final String url, final ChatActivity parentFragment, final boolean ask) {
        final TLRPC.TL_messages_requestUrlAuth req = new TLRPC.TL_messages_requestUrlAuth();
        req.url = url;
        req.flags |= 4;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda89
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.lambda$requestUrlAuth$23(ChatActivity.this, req, url, ask, tLObject, tL_error);
            }
        }, 2);
    }

    public static /* synthetic */ void lambda$requestUrlAuth$23(ChatActivity parentFragment, TLRPC.TL_messages_requestUrlAuth req, String url, boolean ask, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            if (response instanceof TLRPC.TL_urlAuthResultRequest) {
                TLRPC.TL_urlAuthResultRequest res = (TLRPC.TL_urlAuthResultRequest) response;
                parentFragment.showRequestUrlAlert(res, req, url, ask);
                return;
            } else if (response instanceof TLRPC.TL_urlAuthResultAccepted) {
                TLRPC.TL_urlAuthResultAccepted res2 = (TLRPC.TL_urlAuthResultAccepted) response;
                AlertsCreator.showOpenUrlAlert(parentFragment, res2.url, false, false);
                return;
            } else if (response instanceof TLRPC.TL_urlAuthResultDefault) {
                AlertsCreator.showOpenUrlAlert(parentFragment, url, false, ask);
                return;
            } else {
                return;
            }
        }
        AlertsCreator.showOpenUrlAlert(parentFragment, url, false, ask);
    }

    public void sendCallback(boolean cache, MessageObject messageObject, TLRPC.KeyboardButton button, ChatActivity parentFragment) {
        m1196lambda$sendCallback$24$orgtelegrammessengerSendMessagesHelper(cache, messageObject, button, null, null, parentFragment);
    }

    /* renamed from: sendCallback */
    public void m1196lambda$sendCallback$24$orgtelegrammessengerSendMessagesHelper(final boolean cache, final MessageObject messageObject, final TLRPC.KeyboardButton button, final TLRPC.InputCheckPasswordSRP srp, final TwoStepVerificationActivity passwordFragment, final ChatActivity parentFragment) {
        int type;
        boolean cacheFinal;
        if (messageObject == null || button == null || parentFragment == null) {
            return;
        }
        if (button instanceof TLRPC.TL_keyboardButtonUrlAuth) {
            cacheFinal = false;
            type = 3;
        } else if (button instanceof TLRPC.TL_keyboardButtonGame) {
            cacheFinal = false;
            type = 1;
        } else if (button instanceof TLRPC.TL_keyboardButtonBuy) {
            cacheFinal = cache;
            type = 2;
        } else {
            cacheFinal = cache;
            type = 0;
        }
        final String key = messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type;
        this.waitingForCallback.put(key, true);
        final TLObject[] request = new TLObject[1];
        final boolean z = cacheFinal;
        RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda80
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1201lambda$sendCallback$30$orgtelegrammessengerSendMessagesHelper(key, z, messageObject, button, parentFragment, passwordFragment, request, srp, cache, tLObject, tL_error);
            }
        };
        if (cacheFinal) {
            getMessagesStorage().getBotCache(key, requestDelegate);
        } else if (button instanceof TLRPC.TL_keyboardButtonUrlAuth) {
            TLRPC.TL_messages_requestUrlAuth req = new TLRPC.TL_messages_requestUrlAuth();
            req.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
            req.msg_id = messageObject.getId();
            req.button_id = button.button_id;
            req.flags |= 2;
            request[0] = req;
            getConnectionsManager().sendRequest(req, requestDelegate, 2);
        } else if (button instanceof TLRPC.TL_keyboardButtonBuy) {
            if ((messageObject.messageOwner.media.flags & 4) == 0) {
                TLRPC.TL_payments_getPaymentForm req2 = new TLRPC.TL_payments_getPaymentForm();
                TLRPC.TL_inputInvoiceMessage inputInvoice = new TLRPC.TL_inputInvoiceMessage();
                inputInvoice.msg_id = messageObject.getId();
                inputInvoice.peer = getMessagesController().getInputPeer(messageObject.messageOwner.peer_id);
                req2.invoice = inputInvoice;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bg_color", Theme.getColor(Theme.key_windowBackgroundWhite));
                    jsonObject.put("text_color", Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    jsonObject.put("hint_color", Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
                    jsonObject.put("link_color", Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
                    jsonObject.put("button_color", Theme.getColor(Theme.key_featuredStickers_addButton));
                    jsonObject.put("button_text_color", Theme.getColor(Theme.key_featuredStickers_buttonText));
                    req2.theme_params = new TLRPC.TL_dataJSON();
                    req2.theme_params.data = jsonObject.toString();
                    req2.flags |= 1;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                getConnectionsManager().sendRequest(req2, requestDelegate, 2);
                return;
            }
            TLRPC.TL_payments_getPaymentReceipt req3 = new TLRPC.TL_payments_getPaymentReceipt();
            req3.msg_id = messageObject.messageOwner.media.receipt_msg_id;
            req3.peer = getMessagesController().getInputPeer(messageObject.messageOwner.peer_id);
            getConnectionsManager().sendRequest(req3, requestDelegate, 2);
        } else {
            TLRPC.TL_messages_getBotCallbackAnswer req4 = new TLRPC.TL_messages_getBotCallbackAnswer();
            req4.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
            req4.msg_id = messageObject.getId();
            req4.game = button instanceof TLRPC.TL_keyboardButtonGame;
            if (button.requires_password) {
                TLRPC.InputCheckPasswordSRP tL_inputCheckPasswordEmpty = srp != null ? srp : new TLRPC.TL_inputCheckPasswordEmpty();
                req4.password = tL_inputCheckPasswordEmpty;
                req4.password = tL_inputCheckPasswordEmpty;
                req4.flags |= 4;
            }
            if (button.data != null) {
                req4.flags |= 1;
                req4.data = button.data;
            }
            getConnectionsManager().sendRequest(req4, requestDelegate, 2);
        }
    }

    /* renamed from: lambda$sendCallback$30$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1201lambda$sendCallback$30$orgtelegrammessengerSendMessagesHelper(final String key, final boolean cacheFinal, final MessageObject messageObject, final TLRPC.KeyboardButton button, final ChatActivity parentFragment, final TwoStepVerificationActivity passwordFragment, final TLObject[] request, final TLRPC.InputCheckPasswordSRP srp, final boolean cache, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1200lambda$sendCallback$29$orgtelegrammessengerSendMessagesHelper(key, cacheFinal, response, messageObject, button, parentFragment, passwordFragment, request, error, srp, cache);
            }
        });
    }

    /* renamed from: lambda$sendCallback$29$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1200lambda$sendCallback$29$orgtelegrammessengerSendMessagesHelper(String key, boolean cacheFinal, TLObject response, final MessageObject messageObject, final TLRPC.KeyboardButton button, final ChatActivity parentFragment, final TwoStepVerificationActivity passwordFragment, TLObject[] request, TLRPC.TL_error error, TLRPC.InputCheckPasswordSRP srp, final boolean cache) {
        long uid;
        String name;
        boolean z;
        this.waitingForCallback.remove(key);
        if (cacheFinal && response == null) {
            sendCallback(false, messageObject, button, parentFragment);
        } else if (response != null) {
            if (passwordFragment != null) {
                passwordFragment.needHideProgress();
                passwordFragment.finishFragment();
            }
            long uid2 = messageObject.getFromChatId();
            if (messageObject.messageOwner.via_bot_id == 0) {
                uid = uid2;
            } else {
                long uid3 = messageObject.messageOwner.via_bot_id;
                uid = uid3;
            }
            String name2 = null;
            if (uid > 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(uid));
                if (user != null) {
                    name2 = ContactsController.formatName(user.first_name, user.last_name);
                }
            } else {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-uid));
                if (chat != null) {
                    name2 = chat.title;
                }
            }
            if (name2 != null) {
                name = name2;
            } else {
                name = "bot";
            }
            if (button instanceof TLRPC.TL_keyboardButtonUrlAuth) {
                if (response instanceof TLRPC.TL_urlAuthResultRequest) {
                    parentFragment.showRequestUrlAlert((TLRPC.TL_urlAuthResultRequest) response, (TLRPC.TL_messages_requestUrlAuth) request[0], button.url, false);
                } else if (response instanceof TLRPC.TL_urlAuthResultAccepted) {
                    AlertsCreator.showOpenUrlAlert(parentFragment, ((TLRPC.TL_urlAuthResultAccepted) response).url, false, false);
                } else if (response instanceof TLRPC.TL_urlAuthResultDefault) {
                    TLRPC.TL_urlAuthResultDefault tL_urlAuthResultDefault = (TLRPC.TL_urlAuthResultDefault) response;
                    AlertsCreator.showOpenUrlAlert(parentFragment, button.url, false, true);
                }
            } else if (button instanceof TLRPC.TL_keyboardButtonBuy) {
                if (response instanceof TLRPC.TL_payments_paymentForm) {
                    TLRPC.TL_payments_paymentForm form = (TLRPC.TL_payments_paymentForm) response;
                    getMessagesController().putUsers(form.users, false);
                    parentFragment.presentFragment(new PaymentFormActivity(form, messageObject, parentFragment));
                } else if (response instanceof TLRPC.TL_payments_paymentReceipt) {
                    parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response));
                }
            } else {
                TLRPC.TL_messages_botCallbackAnswer res = (TLRPC.TL_messages_botCallbackAnswer) response;
                if (!cacheFinal && res.cache_time != 0 && !button.requires_password) {
                    getMessagesStorage().saveBotCache(key, res);
                }
                if (res.message != null) {
                    if (res.alert) {
                        if (parentFragment.getParentActivity() == null) {
                            return;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(parentFragment.getParentActivity());
                        builder.setTitle(name);
                        builder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), null);
                        builder.setMessage(res.message);
                        parentFragment.showDialog(builder.create());
                        return;
                    }
                    parentFragment.showAlert(name, res.message);
                } else if (res.url == null || parentFragment.getParentActivity() == null) {
                } else {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(uid));
                    boolean verified = user2 != null && user2.verified;
                    if (button instanceof TLRPC.TL_keyboardButtonGame) {
                        TLRPC.TL_game game = messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame ? messageObject.messageOwner.media.game : null;
                        if (game == null) {
                            return;
                        }
                        String str = res.url;
                        if (!verified) {
                            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                            if (notificationsSettings.getBoolean("askgame_" + uid, true)) {
                                z = true;
                                parentFragment.showOpenGameAlert(game, messageObject, str, z, uid);
                                return;
                            }
                        }
                        z = false;
                        parentFragment.showOpenGameAlert(game, messageObject, str, z, uid);
                        return;
                    }
                    AlertsCreator.showOpenUrlAlert(parentFragment, res.url, false, false);
                }
            }
        } else if (error == null || parentFragment.getParentActivity() == null) {
        } else {
            if ("PASSWORD_HASH_INVALID".equals(error.text)) {
                if (srp == null) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(parentFragment.getParentActivity());
                    builder2.setTitle(LocaleController.getString("BotOwnershipTransfer", org.telegram.messenger.beta.R.string.BotOwnershipTransfer));
                    builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BotOwnershipTransferReadyAlertText", org.telegram.messenger.beta.R.string.BotOwnershipTransferReadyAlertText, new Object[0])));
                    builder2.setPositiveButton(LocaleController.getString("BotOwnershipTransferChangeOwner", org.telegram.messenger.beta.R.string.BotOwnershipTransferChangeOwner), new DialogInterface.OnClickListener() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            SendMessagesHelper.this.m1197lambda$sendCallback$25$orgtelegrammessengerSendMessagesHelper(cache, messageObject, button, parentFragment, dialogInterface, i);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", org.telegram.messenger.beta.R.string.Cancel), null);
                    parentFragment.showDialog(builder2.create());
                }
            } else if ("PASSWORD_MISSING".equals(error.text) || error.text.startsWith("PASSWORD_TOO_FRESH_") || error.text.startsWith("SESSION_TOO_FRESH_")) {
                if (passwordFragment != null) {
                    passwordFragment.needHideProgress();
                }
                AlertDialog.Builder builder3 = new AlertDialog.Builder(parentFragment.getParentActivity());
                builder3.setTitle(LocaleController.getString("EditAdminTransferAlertTitle", org.telegram.messenger.beta.R.string.EditAdminTransferAlertTitle));
                LinearLayout linearLayout = new LinearLayout(parentFragment.getParentActivity());
                linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
                linearLayout.setOrientation(1);
                builder3.setView(linearLayout);
                TextView messageTextView = new TextView(parentFragment.getParentActivity());
                messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView.setTextSize(1, 16.0f);
                messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("BotOwnershipTransferAlertText", org.telegram.messenger.beta.R.string.BotOwnershipTransferAlertText, new Object[0])));
                linearLayout.addView(messageTextView, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout2 = new LinearLayout(parentFragment.getParentActivity());
                linearLayout2.setOrientation(0);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                ImageView dotImageView = new ImageView(parentFragment.getParentActivity());
                dotImageView.setImageResource(org.telegram.messenger.beta.R.drawable.list_circle);
                dotImageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                dotImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
                TextView messageTextView2 = new TextView(parentFragment.getParentActivity());
                messageTextView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView2.setTextSize(1, 16.0f);
                messageTextView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText1", org.telegram.messenger.beta.R.string.EditAdminTransferAlertText1)));
                if (LocaleController.isRTL) {
                    linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                    linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2, 5));
                } else {
                    linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2));
                    linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                }
                LinearLayout linearLayout22 = new LinearLayout(parentFragment.getParentActivity());
                linearLayout22.setOrientation(0);
                linearLayout.addView(linearLayout22, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                ImageView dotImageView2 = new ImageView(parentFragment.getParentActivity());
                dotImageView2.setImageResource(org.telegram.messenger.beta.R.drawable.list_circle);
                dotImageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                dotImageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
                TextView messageTextView3 = new TextView(parentFragment.getParentActivity());
                messageTextView3.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView3.setTextSize(1, 16.0f);
                messageTextView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText2", org.telegram.messenger.beta.R.string.EditAdminTransferAlertText2)));
                if (LocaleController.isRTL) {
                    linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                    linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2, 5));
                } else {
                    linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2));
                    linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                }
                if ("PASSWORD_MISSING".equals(error.text)) {
                    builder3.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", org.telegram.messenger.beta.R.string.EditAdminTransferSetPassword), new DialogInterface.OnClickListener() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda11
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatActivity.this.presentFragment(new TwoStepVerificationSetupActivity(6, null));
                        }
                    });
                    builder3.setNegativeButton(LocaleController.getString("Cancel", org.telegram.messenger.beta.R.string.Cancel), null);
                } else {
                    TextView messageTextView4 = new TextView(parentFragment.getParentActivity());
                    messageTextView4.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    messageTextView4.setTextSize(1, 16.0f);
                    messageTextView4.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    messageTextView4.setText(LocaleController.getString("EditAdminTransferAlertText3", org.telegram.messenger.beta.R.string.EditAdminTransferAlertText3));
                    linearLayout.addView(messageTextView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                    builder3.setNegativeButton(LocaleController.getString("OK", org.telegram.messenger.beta.R.string.OK), null);
                }
                parentFragment.showDialog(builder3.create());
            } else if ("SRP_ID_INVALID".equals(error.text)) {
                TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda87
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SendMessagesHelper.this.m1199lambda$sendCallback$28$orgtelegrammessengerSendMessagesHelper(passwordFragment, cache, messageObject, button, parentFragment, tLObject, tL_error);
                    }
                }, 8);
            } else if (passwordFragment != null) {
                passwordFragment.needHideProgress();
                passwordFragment.finishFragment();
            }
        }
    }

    /* renamed from: lambda$sendCallback$25$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1197lambda$sendCallback$25$orgtelegrammessengerSendMessagesHelper(final boolean cache, final MessageObject messageObject, final TLRPC.KeyboardButton button, final ChatActivity parentFragment, DialogInterface dialogInterface, int i) {
        final TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        fragment.setDelegate(new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda90
            @Override // org.telegram.ui.TwoStepVerificationActivity.TwoStepVerificationActivityDelegate
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                SendMessagesHelper.this.m1196lambda$sendCallback$24$orgtelegrammessengerSendMessagesHelper(cache, messageObject, button, fragment, parentFragment, inputCheckPasswordSRP);
            }
        });
        parentFragment.presentFragment(fragment);
    }

    /* renamed from: lambda$sendCallback$28$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1199lambda$sendCallback$28$orgtelegrammessengerSendMessagesHelper(final TwoStepVerificationActivity passwordFragment, final boolean cache, final MessageObject messageObject, final TLRPC.KeyboardButton button, final ChatActivity parentFragment, final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1198lambda$sendCallback$27$orgtelegrammessengerSendMessagesHelper(error2, response2, passwordFragment, cache, messageObject, button, parentFragment);
            }
        });
    }

    /* renamed from: lambda$sendCallback$27$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1198lambda$sendCallback$27$orgtelegrammessengerSendMessagesHelper(TLRPC.TL_error error2, TLObject response2, TwoStepVerificationActivity passwordFragment, boolean cache, MessageObject messageObject, TLRPC.KeyboardButton button, ChatActivity parentFragment) {
        if (error2 == null) {
            TLRPC.TL_account_password currentPassword = (TLRPC.TL_account_password) response2;
            passwordFragment.setCurrentPasswordInfo(null, currentPassword);
            TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
            m1196lambda$sendCallback$24$orgtelegrammessengerSendMessagesHelper(cache, messageObject, button, passwordFragment.getNewSrpPassword(), passwordFragment, parentFragment);
        }
    }

    public boolean isSendingCallback(MessageObject messageObject, TLRPC.KeyboardButton button) {
        int type;
        if (messageObject == null || button == null) {
            return false;
        }
        if (button instanceof TLRPC.TL_keyboardButtonUrlAuth) {
            type = 3;
        } else if (button instanceof TLRPC.TL_keyboardButtonGame) {
            type = 1;
        } else if (button instanceof TLRPC.TL_keyboardButtonBuy) {
            type = 2;
        } else {
            type = 0;
        }
        String key = messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type;
        return this.waitingForCallback.containsKey(key);
    }

    public void sendGame(TLRPC.InputPeer peer, TLRPC.TL_inputMediaGame game, long random_id, long taskId) {
        final long newTaskId;
        if (peer == null || game == null) {
            return;
        }
        TLRPC.TL_messages_sendMedia request = new TLRPC.TL_messages_sendMedia();
        request.peer = peer;
        if (request.peer instanceof TLRPC.TL_inputPeerChannel) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            request.silent = notificationsSettings.getBoolean("silent_" + (-peer.channel_id), false);
        } else if (request.peer instanceof TLRPC.TL_inputPeerChat) {
            SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
            request.silent = notificationsSettings2.getBoolean("silent_" + (-peer.chat_id), false);
        } else {
            SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(this.currentAccount);
            request.silent = notificationsSettings3.getBoolean("silent_" + peer.user_id, false);
        }
        request.random_id = random_id != 0 ? random_id : getNextRandomId();
        request.message = "";
        request.media = game;
        long fromId = ChatObject.getSendAsPeerId(getMessagesController().getChat(Long.valueOf(peer.chat_id)), getMessagesController().getChatFull(peer.chat_id));
        if (fromId != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            request.send_as = getMessagesController().getInputPeer(fromId);
        }
        if (taskId == 0) {
            NativeByteBuffer data = null;
            try {
                data = new NativeByteBuffer(peer.getObjectSize() + game.getObjectSize() + 4 + 8);
                data.writeInt32(3);
                data.writeInt64(random_id);
                peer.serializeToStream(data);
                game.serializeToStream(data);
            } catch (Exception e) {
                FileLog.e(e);
            }
            newTaskId = getMessagesStorage().createPendingTask(data);
        } else {
            newTaskId = taskId;
        }
        getConnectionsManager().sendRequest(request, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda75
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1202lambda$sendGame$31$orgtelegrammessengerSendMessagesHelper(newTaskId, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$sendGame$31$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1202lambda$sendGame$31$orgtelegrammessengerSendMessagesHelper(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void sendMessage(MessageObject retryMessageObject) {
        sendMessage(null, null, null, null, null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, null, true, retryMessageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params, !retryMessageObject.messageOwner.silent, retryMessageObject.scheduled ? retryMessageObject.messageOwner.date : 0, 0, null, null);
    }

    public void sendMessage(TLRPC.User user, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage(null, null, null, null, null, user, null, null, null, null, peer, null, replyToMsg, replyToTopMsg, null, true, null, null, replyMarkup, params, notify, scheduleDate, 0, null, null);
    }

    public void sendMessage(TLRPC.TL_messageMediaInvoice invoice, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage(null, null, null, null, null, null, null, null, null, invoice, peer, null, replyToMsg, replyToTopMsg, null, true, null, null, replyMarkup, params, notify, scheduleDate, 0, null, null);
    }

    public void sendMessage(TLRPC.TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, String caption, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate, int ttl, Object parentObject, MessageObject.SendAnimationData sendAnimationData) {
        sendMessage(null, caption, null, null, videoEditedInfo, null, document, null, null, null, peer, path, replyToMsg, replyToTopMsg, null, true, null, entities, replyMarkup, params, notify, scheduleDate, ttl, parentObject, sendAnimationData);
    }

    public void sendMessage(String message, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, TLRPC.WebPage webPage, boolean searchLinks, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate, MessageObject.SendAnimationData sendAnimationData) {
        sendMessage(message, null, null, null, null, null, null, null, null, null, peer, null, replyToMsg, replyToTopMsg, webPage, searchLinks, null, entities, replyMarkup, params, notify, scheduleDate, 0, null, sendAnimationData);
    }

    public void sendMessage(TLRPC.MessageMedia location, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage(null, null, location, null, null, null, null, null, null, null, peer, null, replyToMsg, replyToTopMsg, null, true, null, null, replyMarkup, params, notify, scheduleDate, 0, null, null);
    }

    public void sendMessage(TLRPC.TL_messageMediaPoll poll, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage(null, null, null, null, null, null, null, null, poll, null, peer, null, replyToMsg, replyToTopMsg, null, true, null, null, replyMarkup, params, notify, scheduleDate, 0, null, null);
    }

    public void sendMessage(TLRPC.TL_game game, long peer, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage(null, null, null, null, null, null, null, game, null, null, peer, null, null, null, null, true, null, null, replyMarkup, params, notify, scheduleDate, 0, null, null);
    }

    public void sendMessage(TLRPC.TL_photo photo, String path, long peer, MessageObject replyToMsg, MessageObject replyToTopMsg, String caption, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate, int ttl, Object parentObject) {
        sendMessage(null, caption, null, photo, null, null, null, null, null, null, peer, path, replyToMsg, replyToTopMsg, null, true, null, entities, replyMarkup, params, notify, scheduleDate, ttl, parentObject, null);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:694:0x1512
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    private void sendMessage(java.lang.String r91, java.lang.String r92, org.telegram.tgnet.TLRPC.MessageMedia r93, org.telegram.tgnet.TLRPC.TL_photo r94, org.telegram.messenger.VideoEditedInfo r95, org.telegram.tgnet.TLRPC.User r96, org.telegram.tgnet.TLRPC.TL_document r97, org.telegram.tgnet.TLRPC.TL_game r98, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r99, org.telegram.tgnet.TLRPC.TL_messageMediaInvoice r100, long r101, java.lang.String r103, org.telegram.messenger.MessageObject r104, org.telegram.messenger.MessageObject r105, org.telegram.tgnet.TLRPC.WebPage r106, boolean r107, org.telegram.messenger.MessageObject r108, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r109, org.telegram.tgnet.TLRPC.ReplyMarkup r110, java.util.HashMap<java.lang.String, java.lang.String> r111, boolean r112, int r113, int r114, java.lang.Object r115, org.telegram.messenger.MessageObject.SendAnimationData r116) {
        /*
            Method dump skipped, instructions count: 17456
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object, org.telegram.messenger.MessageObject$SendAnimationData):void");
    }

    private void performSendDelayedMessage(DelayedMessage message) {
        performSendDelayedMessage(message, -1);
    }

    private TLRPC.PhotoSize getThumbForSecretChat(ArrayList<TLRPC.PhotoSize> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            TLRPC.PhotoSize size = arrayList.get(a);
            if (size != null && !(size instanceof TLRPC.TL_photoStrippedSize) && !(size instanceof TLRPC.TL_photoPathSize) && !(size instanceof TLRPC.TL_photoSizeEmpty) && size.location != null) {
                TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize_layer127();
                photoSize.type = size.type;
                photoSize.w = size.w;
                photoSize.h = size.h;
                photoSize.size = size.size;
                photoSize.bytes = size.bytes;
                if (photoSize.bytes == null) {
                    photoSize.bytes = new byte[0];
                }
                photoSize.location = new TLRPC.TL_fileLocation_layer82();
                photoSize.location.dc_id = size.location.dc_id;
                photoSize.location.volume_id = size.location.volume_id;
                photoSize.location.local_id = size.location.local_id;
                photoSize.location.secret = size.location.secret;
                return photoSize;
            }
        }
        return null;
    }

    private void performSendDelayedMessage(final DelayedMessage message, int index) {
        int index2;
        TLObject inputMedia;
        MessageObject messageObject;
        TLRPC.InputMedia media;
        TLRPC.InputMedia media2;
        TLRPC.InputMedia media3;
        boolean z = false;
        boolean z2 = true;
        if (message.type == 0) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file", this.currentAccount);
            } else if (message.sendRequest != null) {
                String location = FileLoader.getInstance(this.currentAccount).getPathToAttach(message.photoSize).toString();
                putToDelayedMessages(location, message);
                getFileLoader().uploadFile(location, false, true, 16777216);
                putToUploadingMessages(message.obj);
            } else {
                String location2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(message.photoSize).toString();
                if (message.sendEncryptedRequest != null && message.photoSize.location.dc_id != 0) {
                    File file = new File(location2);
                    if (!file.exists()) {
                        location2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(message.photoSize, true).toString();
                        file = new File(location2);
                    }
                    if (!file.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(message.photoSize), message);
                        getFileLoader().loadFile(ImageLocation.getForObject(message.photoSize, message.locationParent), message.parentObject, "jpg", 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(location2, message);
                getFileLoader().uploadFile(location2, true, true, 16777216);
                putToUploadingMessages(message.obj);
            }
        } else if (message.type == 1) {
            if (message.videoEditedInfo != null && message.videoEditedInfo.needConvert()) {
                String location3 = message.obj.messageOwner.attachPath;
                TLRPC.Document document = message.obj.getDocument();
                if (location3 == null) {
                    location3 = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                }
                putToDelayedMessages(location3, message);
                MediaController.getInstance().scheduleVideoConvert(message.obj);
                putToUploadingMessages(message.obj);
            } else {
                if (message.videoEditedInfo != null) {
                    if (message.videoEditedInfo.file != null) {
                        if (message.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                            media3 = ((TLRPC.TL_messages_sendMedia) message.sendRequest).media;
                        } else {
                            media3 = ((TLRPC.TL_messages_editMessage) message.sendRequest).media;
                        }
                        media3.file = message.videoEditedInfo.file;
                        message.videoEditedInfo.file = null;
                    } else if (message.videoEditedInfo.encryptedFile != null) {
                        TLRPC.TL_decryptedMessage decryptedMessage = (TLRPC.TL_decryptedMessage) message.sendEncryptedRequest;
                        decryptedMessage.media.size = (int) message.videoEditedInfo.estimatedSize;
                        decryptedMessage.media.key = message.videoEditedInfo.key;
                        decryptedMessage.media.iv = message.videoEditedInfo.iv;
                        getSecretChatHelper().performSendEncryptedRequest(decryptedMessage, message.obj.messageOwner, message.encryptedChat, message.videoEditedInfo.encryptedFile, message.originalPath, message.obj);
                        message.videoEditedInfo.encryptedFile = null;
                        return;
                    }
                }
                if (message.sendRequest != null) {
                    if (message.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                        media2 = ((TLRPC.TL_messages_sendMedia) message.sendRequest).media;
                    } else {
                        media2 = ((TLRPC.TL_messages_editMessage) message.sendRequest).media;
                    }
                    if (media2.file == null) {
                        String location4 = message.obj.messageOwner.attachPath;
                        TLRPC.Document document2 = message.obj.getDocument();
                        if (location4 == null) {
                            location4 = FileLoader.getDirectory(4) + "/" + document2.id + ".mp4";
                        }
                        putToDelayedMessages(location4, message);
                        if (message.obj.videoEditedInfo != null && message.obj.videoEditedInfo.needConvert()) {
                            getFileLoader().uploadFile(location4, false, false, document2.size, ConnectionsManager.FileTypeVideo, false);
                        } else {
                            getFileLoader().uploadFile(location4, false, false, ConnectionsManager.FileTypeVideo);
                        }
                        putToUploadingMessages(message.obj);
                    } else {
                        String location5 = FileLoader.getDirectory(4) + "/" + message.photoSize.location.volume_id + "_" + message.photoSize.location.local_id + ".jpg";
                        putToDelayedMessages(location5, message);
                        getFileLoader().uploadFile(location5, false, true, 16777216);
                        putToUploadingMessages(message.obj);
                    }
                } else {
                    String location6 = message.obj.messageOwner.attachPath;
                    TLRPC.Document document3 = message.obj.getDocument();
                    if (location6 == null) {
                        location6 = FileLoader.getDirectory(4) + "/" + document3.id + ".mp4";
                    }
                    if (message.sendEncryptedRequest != null && document3.dc_id != 0) {
                        File file2 = new File(location6);
                        if (!file2.exists()) {
                            putToDelayedMessages(FileLoader.getAttachFileName(document3), message);
                            getFileLoader().loadFile(document3, message.parentObject, 2, 0);
                            return;
                        }
                    }
                    putToDelayedMessages(location6, message);
                    if (message.obj.videoEditedInfo != null && message.obj.videoEditedInfo.needConvert()) {
                        getFileLoader().uploadFile(location6, true, false, document3.size, ConnectionsManager.FileTypeVideo, false);
                    } else {
                        getFileLoader().uploadFile(location6, true, false, ConnectionsManager.FileTypeVideo);
                    }
                    putToUploadingMessages(message.obj);
                }
            }
        } else if (message.type == 2) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "gif", this.currentAccount);
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                    media = ((TLRPC.TL_messages_sendMedia) message.sendRequest).media;
                } else {
                    media = ((TLRPC.TL_messages_editMessage) message.sendRequest).media;
                }
                if (media.file == null) {
                    String location7 = message.obj.messageOwner.attachPath;
                    putToDelayedMessages(location7, message);
                    FileLoader fileLoader = getFileLoader();
                    if (message.sendRequest != null) {
                        z2 = false;
                    }
                    fileLoader.uploadFile(location7, z2, false, ConnectionsManager.FileTypeFile);
                    putToUploadingMessages(message.obj);
                } else if (media.thumb == null && message.photoSize != null && !(message.photoSize instanceof TLRPC.TL_photoStrippedSize)) {
                    String location8 = FileLoader.getDirectory(4) + "/" + message.photoSize.location.volume_id + "_" + message.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(location8, message);
                    getFileLoader().uploadFile(location8, false, true, 16777216);
                    putToUploadingMessages(message.obj);
                }
            } else {
                String location9 = message.obj.messageOwner.attachPath;
                TLRPC.Document document4 = message.obj.getDocument();
                if (message.sendEncryptedRequest != null && document4.dc_id != 0) {
                    File file3 = new File(location9);
                    if (!file3.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(document4), message);
                        getFileLoader().loadFile(document4, message.parentObject, 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(location9, message);
                getFileLoader().uploadFile(location9, true, false, ConnectionsManager.FileTypeFile);
                putToUploadingMessages(message.obj);
            }
        } else if (message.type == 3) {
            String location10 = message.obj.messageOwner.attachPath;
            putToDelayedMessages(location10, message);
            FileLoader fileLoader2 = getFileLoader();
            if (message.sendRequest == null) {
                z = true;
            }
            fileLoader2.uploadFile(location10, z, true, ConnectionsManager.FileTypeAudio);
            putToUploadingMessages(message.obj);
        } else if (message.type == 4) {
            boolean add = index < 0;
            if (message.performMediaUpload) {
                if (index >= 0) {
                    index2 = index;
                } else {
                    index2 = message.messageObjects.size() - 1;
                }
                MessageObject messageObject2 = message.messageObjects.get(index2);
                if (messageObject2.getDocument() != null) {
                    if (message.videoEditedInfo != null) {
                        String location11 = messageObject2.messageOwner.attachPath;
                        TLRPC.Document document5 = messageObject2.getDocument();
                        if (location11 == null) {
                            location11 = FileLoader.getDirectory(4) + "/" + document5.id + ".mp4";
                        }
                        putToDelayedMessages(location11, message);
                        message.extraHashMap.put(messageObject2, location11);
                        message.extraHashMap.put(location11 + "_i", messageObject2);
                        if (message.photoSize != null && message.photoSize.location != null) {
                            message.extraHashMap.put(location11 + "_t", message.photoSize);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject2);
                        message.obj = messageObject2;
                        putToUploadingMessages(messageObject2);
                    } else {
                        TLRPC.Document document6 = messageObject2.getDocument();
                        String documentLocation = messageObject2.messageOwner.attachPath;
                        if (documentLocation != null) {
                            messageObject = messageObject2;
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(FileLoader.getDirectory(4));
                            sb.append("/");
                            messageObject = messageObject2;
                            sb.append(document6.id);
                            sb.append(".mp4");
                            documentLocation = sb.toString();
                        }
                        if (message.sendRequest != null) {
                            TLRPC.TL_messages_sendMultiMedia request = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
                            TLRPC.InputMedia media4 = request.multi_media.get(index2).media;
                            if (media4.file == null) {
                                putToDelayedMessages(documentLocation, message);
                                MessageObject messageObject3 = messageObject;
                                message.extraHashMap.put(messageObject3, documentLocation);
                                message.extraHashMap.put(documentLocation, media4);
                                message.extraHashMap.put(documentLocation + "_i", messageObject3);
                                if (message.photoSize != null && message.photoSize.location != null) {
                                    message.extraHashMap.put(documentLocation + "_t", message.photoSize);
                                }
                                if (messageObject3.videoEditedInfo != null && messageObject3.videoEditedInfo.needConvert()) {
                                    getFileLoader().uploadFile(documentLocation, false, false, document6.size, ConnectionsManager.FileTypeVideo, false);
                                } else {
                                    getFileLoader().uploadFile(documentLocation, false, false, ConnectionsManager.FileTypeVideo);
                                }
                                putToUploadingMessages(messageObject3);
                            } else {
                                MessageObject messageObject4 = messageObject;
                                if (message.photoSize != null) {
                                    String location12 = FileLoader.getDirectory(4) + "/" + message.photoSize.location.volume_id + "_" + message.photoSize.location.local_id + ".jpg";
                                    putToDelayedMessages(location12, message);
                                    message.extraHashMap.put(location12 + "_o", documentLocation);
                                    message.extraHashMap.put(messageObject4, location12);
                                    message.extraHashMap.put(location12, media4);
                                    getFileLoader().uploadFile(location12, false, true, 16777216);
                                    putToUploadingMessages(messageObject4);
                                }
                            }
                        } else {
                            MessageObject messageObject5 = messageObject;
                            TLRPC.TL_messages_sendEncryptedMultiMedia request2 = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                            putToDelayedMessages(documentLocation, message);
                            message.extraHashMap.put(messageObject5, documentLocation);
                            message.extraHashMap.put(documentLocation, request2.files.get(index2));
                            message.extraHashMap.put(documentLocation + "_i", messageObject5);
                            if (message.photoSize != null && message.photoSize.location != null) {
                                message.extraHashMap.put(documentLocation + "_t", message.photoSize);
                            }
                            if (messageObject5.videoEditedInfo != null && messageObject5.videoEditedInfo.needConvert()) {
                                getFileLoader().uploadFile(documentLocation, true, false, document6.size, ConnectionsManager.FileTypeVideo, false);
                            } else {
                                getFileLoader().uploadFile(documentLocation, true, false, ConnectionsManager.FileTypeVideo);
                            }
                            putToUploadingMessages(messageObject5);
                        }
                    }
                    message.videoEditedInfo = null;
                    message.photoSize = null;
                } else if (message.httpLocation != null) {
                    putToDelayedMessages(message.httpLocation, message);
                    message.extraHashMap.put(messageObject2, message.httpLocation);
                    message.extraHashMap.put(message.httpLocation, messageObject2);
                    ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file", this.currentAccount);
                    message.httpLocation = null;
                } else {
                    if (message.sendRequest != null) {
                        TLRPC.TL_messages_sendMultiMedia request3 = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
                        inputMedia = request3.multi_media.get(index2).media;
                    } else {
                        TLObject inputMedia2 = message.sendEncryptedRequest;
                        TLRPC.TL_messages_sendEncryptedMultiMedia request4 = (TLRPC.TL_messages_sendEncryptedMultiMedia) inputMedia2;
                        inputMedia = request4.files.get(index2);
                    }
                    String location13 = FileLoader.getInstance(this.currentAccount).getPathToAttach(message.photoSize).toString();
                    putToDelayedMessages(location13, message);
                    message.extraHashMap.put(location13, inputMedia);
                    message.extraHashMap.put(messageObject2, location13);
                    getFileLoader().uploadFile(location13, message.sendEncryptedRequest != null, true, 16777216);
                    putToUploadingMessages(messageObject2);
                    message.photoSize = null;
                }
                message.performMediaUpload = false;
            } else if (!message.messageObjects.isEmpty()) {
                putToSendingMessages(message.messageObjects.get(message.messageObjects.size() - 1).messageOwner, message.finalGroupMessage != 0);
            }
            sendReadyToSendGroup(message, add, true);
        } else if (message.type == 5) {
            final String key = "stickerset_" + message.obj.getId();
            TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
            req.stickerset = (TLRPC.InputStickerSet) message.parentObject;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda83
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.m1164x4ece33ee(message, key, tLObject, tL_error);
                }
            });
            putToDelayedMessages(key, message);
        }
    }

    /* renamed from: lambda$performSendDelayedMessage$33$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1164x4ece33ee(final DelayedMessage message, final String key, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1163x698cc52d(response, message, key);
            }
        });
    }

    /* renamed from: lambda$performSendDelayedMessage$32$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1163x698cc52d(TLObject response, DelayedMessage message, String key) {
        boolean found = false;
        if (response != null) {
            TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) response;
            getMediaDataController().storeTempStickerSet(set);
            TLRPC.TL_documentAttributeSticker_layer55 attributeSticker = (TLRPC.TL_documentAttributeSticker_layer55) message.locationParent;
            attributeSticker.stickerset = new TLRPC.TL_inputStickerSetShortName();
            attributeSticker.stickerset.short_name = set.set.short_name;
            found = true;
        }
        ArrayList<DelayedMessage> arrayList = this.delayedMessages.remove(key);
        if (arrayList != null && !arrayList.isEmpty()) {
            if (found) {
                getMessagesStorage().replaceMessageIfExists(arrayList.get(0).obj.messageOwner, null, null, false);
            }
            getSecretChatHelper().performSendEncryptedRequest((TLRPC.DecryptedMessage) message.sendEncryptedRequest, message.obj.messageOwner, message.encryptedChat, null, null, message.obj);
        }
    }

    private void uploadMultiMedia(final DelayedMessage message, final TLRPC.InputMedia inputMedia, TLRPC.InputEncryptedFile inputEncryptedFile, String key) {
        if (inputMedia != null) {
            TLRPC.TL_messages_sendMultiMedia multiMedia = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
            int a = 0;
            while (true) {
                if (a >= multiMedia.multi_media.size()) {
                    break;
                } else if (multiMedia.multi_media.get(a).media != inputMedia) {
                    a++;
                } else {
                    putToSendingMessages(message.messages.get(a), message.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.fileUploadProgressChanged, key, -1L, -1L, false);
                    break;
                }
            }
            TLRPC.TL_messages_uploadMedia req = new TLRPC.TL_messages_uploadMedia();
            req.media = inputMedia;
            req.peer = ((TLRPC.TL_messages_sendMultiMedia) message.sendRequest).peer;
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda85
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.m1222xbfc806b8(inputMedia, message, tLObject, tL_error);
                }
            });
        } else if (inputEncryptedFile != null) {
            TLRPC.TL_messages_sendEncryptedMultiMedia multiMedia2 = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
            int a2 = 0;
            while (true) {
                if (a2 >= multiMedia2.files.size()) {
                    break;
                } else if (multiMedia2.files.get(a2) != inputEncryptedFile) {
                    a2++;
                } else {
                    putToSendingMessages(message.messages.get(a2), message.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.fileUploadProgressChanged, key, -1L, -1L, false);
                    break;
                }
            }
            sendReadyToSendGroup(message, false, true);
        }
    }

    /* renamed from: lambda$uploadMultiMedia$35$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1222xbfc806b8(final TLRPC.InputMedia inputMedia, final DelayedMessage message, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1221xda8697f7(response, inputMedia, message);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$uploadMultiMedia$34$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1221xda8697f7(TLObject response, TLRPC.InputMedia inputMedia, DelayedMessage message) {
        TLRPC.InputMedia newInputMedia = null;
        if (response != null) {
            TLRPC.MessageMedia messageMedia = (TLRPC.MessageMedia) response;
            if ((inputMedia instanceof TLRPC.TL_inputMediaUploadedPhoto) && (messageMedia instanceof TLRPC.TL_messageMediaPhoto)) {
                TLRPC.TL_inputMediaPhoto inputMediaPhoto = new TLRPC.TL_inputMediaPhoto();
                inputMediaPhoto.id = new TLRPC.TL_inputPhoto();
                inputMediaPhoto.id.id = messageMedia.photo.id;
                inputMediaPhoto.id.access_hash = messageMedia.photo.access_hash;
                inputMediaPhoto.id.file_reference = messageMedia.photo.file_reference;
                newInputMedia = inputMediaPhoto;
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("set uploaded photo");
                }
            } else if ((inputMedia instanceof TLRPC.TL_inputMediaUploadedDocument) && (messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                TLRPC.TL_inputMediaDocument inputMediaDocument = new TLRPC.TL_inputMediaDocument();
                inputMediaDocument.id = new TLRPC.TL_inputDocument();
                inputMediaDocument.id.id = messageMedia.document.id;
                inputMediaDocument.id.access_hash = messageMedia.document.access_hash;
                inputMediaDocument.id.file_reference = messageMedia.document.file_reference;
                newInputMedia = inputMediaDocument;
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("set uploaded document");
                }
            }
        }
        if (newInputMedia != null) {
            if (inputMedia.ttl_seconds != 0) {
                newInputMedia.ttl_seconds = inputMedia.ttl_seconds;
                newInputMedia.flags |= 1;
            }
            TLRPC.TL_messages_sendMultiMedia req1 = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
            int a = 0;
            while (true) {
                if (a >= req1.multi_media.size()) {
                    break;
                } else if (req1.multi_media.get(a).media != inputMedia) {
                    a++;
                } else {
                    req1.multi_media.get(a).media = newInputMedia;
                    break;
                }
            }
            sendReadyToSendGroup(message, false, true);
            return;
        }
        message.markAsError();
    }

    private void sendReadyToSendGroup(DelayedMessage message, boolean add, boolean check) {
        DelayedMessage maxDelayedMessage;
        if (message.messageObjects.isEmpty()) {
            message.markAsError();
            return;
        }
        String key = "group_" + message.groupId;
        if (message.finalGroupMessage != message.messageObjects.get(message.messageObjects.size() - 1).getId()) {
            if (add) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("final message not added, add");
                }
                putToDelayedMessages(key, message);
                return;
            } else if (BuildVars.DEBUG_VERSION) {
                FileLog.d("final message not added");
                return;
            } else {
                return;
            }
        }
        if (add) {
            this.delayedMessages.remove(key);
            getMessagesStorage().putMessages(message.messages, false, true, false, 0, message.scheduled);
            getMessagesController().updateInterfaceWithMessages(message.peer, message.messageObjects, message.scheduled);
            if (!message.scheduled) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("add message");
            }
        }
        if (message.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia) {
            TLRPC.TL_messages_sendMultiMedia request = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
            for (int a = 0; a < request.multi_media.size(); a++) {
                TLRPC.InputMedia inputMedia = request.multi_media.get(a).media;
                if ((inputMedia instanceof TLRPC.TL_inputMediaUploadedPhoto) || (inputMedia instanceof TLRPC.TL_inputMediaUploadedDocument)) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("multi media not ready");
                        return;
                    } else {
                        return;
                    }
                }
            }
            if (check && (maxDelayedMessage = findMaxDelayedMessageForMessageId(message.finalGroupMessage, message.peer)) != null) {
                maxDelayedMessage.addDelayedRequest(message.sendRequest, message.messageObjects, message.originalPaths, message.parentObjects, message, message.scheduled);
                if (message.requests != null) {
                    maxDelayedMessage.requests.addAll(message.requests);
                }
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("has maxDelayedMessage, delay");
                    return;
                }
                return;
            }
        } else {
            TLRPC.TL_messages_sendEncryptedMultiMedia request2 = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
            for (int a2 = 0; a2 < request2.files.size(); a2++) {
                if (request2.files.get(a2) instanceof TLRPC.TL_inputEncryptedFile) {
                    return;
                }
            }
        }
        if (message.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia) {
            performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia) message.sendRequest, message.messageObjects, message.originalPaths, message.parentObjects, message, message.scheduled);
        } else {
            getSecretChatHelper().performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest, message);
        }
        message.sendDelayedRequests();
    }

    /* renamed from: lambda$stopVideoService$36$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1219x610147a9(String path) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, path, Integer.valueOf(this.currentAccount));
    }

    /* renamed from: lambda$stopVideoService$37$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1220x4642b66a(final String path) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1219x610147a9(path);
            }
        });
    }

    public void stopVideoService(final String path) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1220x4642b66a(path);
            }
        });
    }

    public void putToSendingMessages(final TLRPC.Message message, final boolean scheduled) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda49
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1195x5747eb0b(message, scheduled);
                }
            });
        } else {
            putToSendingMessages(message, scheduled, true);
        }
    }

    /* renamed from: lambda$putToSendingMessages$38$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1195x5747eb0b(TLRPC.Message message, boolean scheduled) {
        putToSendingMessages(message, scheduled, true);
    }

    protected void putToSendingMessages(TLRPC.Message message, boolean scheduled, boolean notify) {
        if (message == null) {
            return;
        }
        if (message.id > 0) {
            this.editingMessages.put(message.id, message);
            return;
        }
        boolean contains = this.sendingMessages.indexOfKey(message.id) >= 0;
        removeFromUploadingMessages(message.id, scheduled);
        this.sendingMessages.put(message.id, message);
        if (!scheduled && !contains) {
            long did = MessageObject.getDialogId(message);
            LongSparseArray<Integer> longSparseArray = this.sendingMessagesIdDialogs;
            longSparseArray.put(did, Integer.valueOf(longSparseArray.get(did, 0).intValue() + 1));
            if (notify) {
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    public TLRPC.Message removeFromSendingMessages(int mid, boolean scheduled) {
        TLRPC.Message message;
        long did;
        Integer currentCount;
        if (mid > 0) {
            message = this.editingMessages.get(mid);
            if (message != null) {
                this.editingMessages.remove(mid);
            }
        } else {
            message = this.sendingMessages.get(mid);
            if (message != null) {
                this.sendingMessages.remove(mid);
                if (!scheduled && (currentCount = this.sendingMessagesIdDialogs.get((did = MessageObject.getDialogId(message)))) != null) {
                    int count = currentCount.intValue() - 1;
                    if (count <= 0) {
                        this.sendingMessagesIdDialogs.remove(did);
                    } else {
                        this.sendingMessagesIdDialogs.put(did, Integer.valueOf(count));
                    }
                    getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                }
            }
        }
        return message;
    }

    public int getSendingMessageId(long did) {
        for (int a = 0; a < this.sendingMessages.size(); a++) {
            TLRPC.Message message = this.sendingMessages.valueAt(a);
            if (message.dialog_id == did) {
                return message.id;
            }
        }
        for (int a2 = 0; a2 < this.uploadMessages.size(); a2++) {
            TLRPC.Message message2 = this.uploadMessages.valueAt(a2);
            if (message2.dialog_id == did) {
                return message2.id;
            }
        }
        return 0;
    }

    protected void putToUploadingMessages(MessageObject obj) {
        if (obj == null || obj.getId() > 0 || obj.scheduled) {
            return;
        }
        TLRPC.Message message = obj.messageOwner;
        boolean contains = this.uploadMessages.indexOfKey(message.id) >= 0;
        this.uploadMessages.put(message.id, message);
        if (!contains) {
            long did = MessageObject.getDialogId(message);
            LongSparseArray<Integer> longSparseArray = this.uploadingMessagesIdDialogs;
            longSparseArray.put(did, Integer.valueOf(longSparseArray.get(did, 0).intValue() + 1));
            getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
        }
    }

    protected void removeFromUploadingMessages(int mid, boolean scheduled) {
        TLRPC.Message message;
        if (mid <= 0 && !scheduled && (message = this.uploadMessages.get(mid)) != null) {
            this.uploadMessages.remove(mid);
            long did = MessageObject.getDialogId(message);
            Integer currentCount = this.uploadingMessagesIdDialogs.get(did);
            if (currentCount != null) {
                int count = currentCount.intValue() - 1;
                if (count <= 0) {
                    this.uploadingMessagesIdDialogs.remove(did);
                } else {
                    this.uploadingMessagesIdDialogs.put(did, Integer.valueOf(count));
                }
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    public boolean isSendingMessage(int mid) {
        return this.sendingMessages.indexOfKey(mid) >= 0 || this.editingMessages.indexOfKey(mid) >= 0;
    }

    public boolean isSendingMessageIdDialog(long did) {
        return this.sendingMessagesIdDialogs.get(did, 0).intValue() > 0;
    }

    public boolean isUploadingMessageIdDialog(long did) {
        return this.uploadingMessagesIdDialogs.get(did, 0).intValue() > 0;
    }

    public void performSendMessageRequestMulti(final TLRPC.TL_messages_sendMultiMedia req, final ArrayList<MessageObject> msgObjs, final ArrayList<String> originalPaths, final ArrayList<Object> parentObjects, final DelayedMessage delayedMessage, final boolean scheduled) {
        int size = msgObjs.size();
        for (int a = 0; a < size; a++) {
            putToSendingMessages(msgObjs.get(a).messageOwner, scheduled);
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda81
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1188xc62865dc(parentObjects, req, msgObjs, originalPaths, delayedMessage, scheduled, tLObject, tL_error);
            }
        }, (QuickAckDelegate) null, 68);
    }

    /* renamed from: lambda$performSendMessageRequestMulti$46$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1188xc62865dc(ArrayList parentObjects, final TLRPC.TL_messages_sendMultiMedia req, final ArrayList msgObjs, final ArrayList originalPaths, final DelayedMessage delayedMessage, final boolean scheduled, final TLObject response, final TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text)) {
            if (parentObjects != null) {
                ArrayList<Object> arrayList = new ArrayList<>(parentObjects);
                getFileRefController().requestReference(arrayList, req, msgObjs, originalPaths, arrayList, delayedMessage, Boolean.valueOf(scheduled));
                return;
            } else if (delayedMessage != null && !delayedMessage.retriedToSend) {
                delayedMessage.retriedToSend = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda57
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.this.m1181xb30048c0(req, delayedMessage, msgObjs, scheduled);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1187xe0e6f71b(error, response, msgObjs, originalPaths, scheduled, req);
            }
        });
    }

    /* renamed from: lambda$performSendMessageRequestMulti$39$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1181xb30048c0(TLRPC.TL_messages_sendMultiMedia req, DelayedMessage delayedMessage, ArrayList msgObjs, boolean scheduled) {
        boolean hasEmptyFile = false;
        int size = req.multi_media.size();
        for (int a = 0; a < size; a++) {
            if (delayedMessage.parentObjects.get(a) != null) {
                removeFromSendingMessages(((MessageObject) msgObjs.get(a)).getId(), scheduled);
                TLRPC.TL_inputSingleMedia request = req.multi_media.get(a);
                if (request.media instanceof TLRPC.TL_inputMediaPhoto) {
                    request.media = delayedMessage.inputMedias.get(a);
                } else if (request.media instanceof TLRPC.TL_inputMediaDocument) {
                    request.media = delayedMessage.inputMedias.get(a);
                }
                delayedMessage.videoEditedInfo = delayedMessage.videoEditedInfos.get(a);
                delayedMessage.httpLocation = delayedMessage.httpLocations.get(a);
                delayedMessage.photoSize = delayedMessage.locations.get(a);
                delayedMessage.performMediaUpload = true;
                if (request.media.file == null || delayedMessage.photoSize != null) {
                    hasEmptyFile = true;
                }
                performSendDelayedMessage(delayedMessage, a);
            }
        }
        if (!hasEmptyFile) {
            for (int i = 0; i < msgObjs.size(); i++) {
                TLRPC.Message newMsgObj = ((MessageObject) msgObjs.get(i)).messageOwner;
                getMessagesStorage().markMessageAsSendError(newMsgObj, scheduled);
                newMsgObj.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                processSentMessage(newMsgObj.id);
                removeFromSendingMessages(newMsgObj.id, scheduled);
            }
        }
    }

    /* renamed from: lambda$performSendMessageRequestMulti$45$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1187xe0e6f71b(TLRPC.TL_error error, TLObject response, ArrayList msgObjs, ArrayList originalPaths, final boolean scheduled, TLRPC.TL_messages_sendMultiMedia req) {
        SendMessagesHelper sendMessagesHelper;
        int i;
        boolean isSentError;
        final TLRPC.Updates updates;
        TLRPC.Message newMsgObj;
        LongSparseArray<Integer> newIds;
        SparseArray<TLRPC.Message> newMessages;
        boolean isSentError2;
        int i2;
        LongSparseArray<Integer> newIds2;
        SparseArray<TLRPC.Message> newMessages2;
        TLRPC.Updates updates2;
        SendMessagesHelper sendMessagesHelper2;
        TLRPC.MessageReplies messageReplies;
        SendMessagesHelper sendMessagesHelper3 = this;
        ArrayList arrayList = msgObjs;
        boolean isSentError3 = false;
        if (error == null) {
            SparseArray<TLRPC.Message> newMessages3 = new SparseArray<>();
            LongSparseArray<Integer> newIds3 = new LongSparseArray<>();
            TLRPC.Updates updates3 = (TLRPC.Updates) response;
            ArrayList<TLRPC.Update> updatesArr = ((TLRPC.Updates) response).updates;
            int a = 0;
            LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies = null;
            while (a < updatesArr.size()) {
                TLRPC.Update update = updatesArr.get(a);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    TLRPC.TL_updateMessageID updateMessageID = (TLRPC.TL_updateMessageID) update;
                    newIds3.put(updateMessageID.random_id, Integer.valueOf(updateMessageID.id));
                    updatesArr.remove(a);
                    a--;
                    sendMessagesHelper2 = this;
                    updates2 = updates3;
                } else if (update instanceof TLRPC.TL_updateNewMessage) {
                    final TLRPC.TL_updateNewMessage newMessage = (TLRPC.TL_updateNewMessage) update;
                    newMessages3.put(newMessage.message.id, newMessage.message);
                    sendMessagesHelper2 = this;
                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda62
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.m1182x669fcd56(newMessage);
                        }
                    });
                    updatesArr.remove(a);
                    a--;
                    updates2 = updates3;
                } else {
                    sendMessagesHelper2 = this;
                    if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                        final TLRPC.TL_updateNewChannelMessage newMessage2 = (TLRPC.TL_updateNewChannelMessage) update;
                        long channelId = MessagesController.getUpdateChannelId(newMessage2);
                        updates2 = updates3;
                        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(channelId));
                        if (chat == null || chat.megagroup) {
                            if (newMessage2.message.reply_to != null) {
                                if (newMessage2.message.reply_to.reply_to_top_id != 0 || newMessage2.message.reply_to.reply_to_msg_id != 0) {
                                    if (channelReplies == null) {
                                        channelReplies = new LongSparseArray<>();
                                    }
                                    long did = MessageObject.getDialogId(newMessage2.message);
                                    SparseArray<TLRPC.MessageReplies> replies = channelReplies.get(did);
                                    if (replies == null) {
                                        replies = new SparseArray<>();
                                        channelReplies.put(did, replies);
                                    }
                                    LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies2 = channelReplies;
                                    int id = newMessage2.message.reply_to.reply_to_top_id != 0 ? newMessage2.message.reply_to.reply_to_top_id : newMessage2.message.reply_to.reply_to_msg_id;
                                    TLRPC.MessageReplies messageReplies2 = replies.get(id);
                                    if (messageReplies2 == null) {
                                        messageReplies = new TLRPC.TL_messageReplies();
                                        replies.put(id, messageReplies);
                                    } else {
                                        messageReplies = messageReplies2;
                                    }
                                    if (newMessage2.message.from_id != null) {
                                        messageReplies.recent_repliers.add(0, newMessage2.message.from_id);
                                    }
                                    messageReplies.replies++;
                                    channelReplies = channelReplies2;
                                }
                            }
                            newMessages3.put(newMessage2.message.id, newMessage2.message);
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda60
                                @Override // java.lang.Runnable
                                public final void run() {
                                    SendMessagesHelper.this.m1183x4be13c17(newMessage2);
                                }
                            });
                            updatesArr.remove(a);
                            a--;
                        }
                        newMessages3.put(newMessage2.message.id, newMessage2.message);
                        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda60
                            @Override // java.lang.Runnable
                            public final void run() {
                                SendMessagesHelper.this.m1183x4be13c17(newMessage2);
                            }
                        });
                        updatesArr.remove(a);
                        a--;
                    } else {
                        updates2 = updates3;
                        if (update instanceof TLRPC.TL_updateNewScheduledMessage) {
                            TLRPC.TL_updateNewScheduledMessage newMessage3 = (TLRPC.TL_updateNewScheduledMessage) update;
                            newMessages3.put(newMessage3.message.id, newMessage3.message);
                            updatesArr.remove(a);
                            a--;
                        }
                    }
                }
                a++;
                sendMessagesHelper3 = sendMessagesHelper2;
                updates3 = updates2;
            }
            TLRPC.Updates updates4 = updates3;
            sendMessagesHelper = sendMessagesHelper3;
            if (channelReplies != null) {
                getMessagesStorage().putChannelViews(null, null, channelReplies, true);
                getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, null, null, channelReplies, true);
            }
            int i3 = 0;
            while (true) {
                if (i3 >= msgObjs.size()) {
                    boolean isSentError4 = isSentError3;
                    updates = updates4;
                    i = 1;
                    isSentError = isSentError4;
                    break;
                }
                MessageObject msgObj = (MessageObject) arrayList.get(i3);
                String originalPath = (String) originalPaths.get(i3);
                TLRPC.Message newMsgObj2 = msgObj.messageOwner;
                final int oldId = newMsgObj2.id;
                final ArrayList<TLRPC.Message> sentMessages = new ArrayList<>();
                String str = newMsgObj2.attachPath;
                Integer id2 = newIds3.get(newMsgObj2.random_id);
                if (id2 == null) {
                    updates = updates4;
                    i = 1;
                    isSentError = true;
                    break;
                }
                TLRPC.Message message = newMessages3.get(id2.intValue());
                if (message == null) {
                    updates = updates4;
                    i = 1;
                    isSentError = true;
                    break;
                }
                MessageObject.getDialogId(message);
                sentMessages.add(message);
                if ((message.flags & ConnectionsManager.FileTypeVideo) == 0) {
                    newMsgObj = newMsgObj2;
                } else {
                    newMsgObj = newMsgObj2;
                    msgObj.messageOwner.ttl_period = message.ttl_period;
                    msgObj.messageOwner.flags |= ConnectionsManager.FileTypeVideo;
                }
                final TLRPC.Message newMsgObj3 = newMsgObj;
                LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies3 = channelReplies;
                ArrayList<TLRPC.Update> updatesArr2 = updatesArr;
                TLRPC.Updates updates5 = updates4;
                updateMediaPaths(msgObj, message, message.id, originalPath, false);
                final int existFlags = msgObj.getMediaExistanceFlags();
                newMsgObj3.id = message.id;
                final long grouped_id = message.grouped_id;
                if (scheduled) {
                    newMessages = newMessages3;
                    newIds = newIds3;
                } else {
                    Integer value = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                    if (value != null) {
                        newMessages = newMessages3;
                        newIds = newIds3;
                    } else {
                        newMessages = newMessages3;
                        newIds = newIds3;
                        value = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                        getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), value);
                    }
                    message.unread = value.intValue() < message.id;
                }
                if (!isSentError3) {
                    getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    newMsgObj3.send_state = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj3.id), newMsgObj3, Long.valueOf(newMsgObj3.dialog_id), Long.valueOf(grouped_id), Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
                    newMessages2 = newMessages;
                    newIds2 = newIds;
                    i2 = i3;
                    isSentError2 = isSentError3;
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda45
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.m1185x16641999(newMsgObj3, oldId, scheduled, sentMessages, grouped_id, existFlags);
                        }
                    });
                } else {
                    isSentError2 = isSentError3;
                    newMessages2 = newMessages;
                    newIds2 = newIds;
                    i2 = i3;
                }
                i3 = i2 + 1;
                updates4 = updates5;
                newMessages3 = newMessages2;
                updatesArr = updatesArr2;
                channelReplies = channelReplies3;
                newIds3 = newIds2;
                isSentError3 = isSentError2;
                arrayList = msgObjs;
            }
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda65
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1186xfba5885a(updates);
                }
            });
        } else {
            sendMessagesHelper = sendMessagesHelper3;
            i = 1;
            AlertsCreator.processError(sendMessagesHelper.currentAccount, error, null, req, new Object[0]);
            isSentError = true;
        }
        if (isSentError) {
            for (int i4 = 0; i4 < msgObjs.size(); i4++) {
                TLRPC.Message newMsgObj4 = ((MessageObject) msgObjs.get(i4)).messageOwner;
                getMessagesStorage().markMessageAsSendError(newMsgObj4, scheduled);
                newMsgObj4.send_state = 2;
                NotificationCenter notificationCenter = getNotificationCenter();
                int i5 = NotificationCenter.messageSendError;
                Object[] objArr = new Object[i];
                objArr[0] = Integer.valueOf(newMsgObj4.id);
                notificationCenter.postNotificationName(i5, objArr);
                sendMessagesHelper.processSentMessage(newMsgObj4.id);
                sendMessagesHelper.removeFromSendingMessages(newMsgObj4.id, scheduled);
            }
        }
    }

    /* renamed from: lambda$performSendMessageRequestMulti$40$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1182x669fcd56(TLRPC.TL_updateNewMessage newMessage) {
        getMessagesController().processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    /* renamed from: lambda$performSendMessageRequestMulti$41$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1183x4be13c17(TLRPC.TL_updateNewChannelMessage newMessage) {
        getMessagesController().processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.peer_id.channel_id);
    }

    /* renamed from: lambda$performSendMessageRequestMulti$43$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1185x16641999(final TLRPC.Message newMsgObj, final int oldId, final boolean scheduled, ArrayList sentMessages, final long grouped_id, final int existFlags) {
        getMessagesStorage().updateMessageStateAndId(newMsgObj.random_id, MessageObject.getPeerId(newMsgObj.peer_id), Integer.valueOf(oldId), newMsgObj.id, 0, false, scheduled ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, false, false, 0, scheduled);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda42
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1184x3122aad8(newMsgObj, oldId, grouped_id, existFlags, scheduled);
            }
        });
    }

    /* renamed from: lambda$performSendMessageRequestMulti$42$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1184x3122aad8(TLRPC.Message newMsgObj, int oldId, long grouped_id, int existFlags, boolean scheduled) {
        getMediaDataController().increasePeerRaiting(newMsgObj.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id), Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId, scheduled);
    }

    /* renamed from: lambda$performSendMessageRequestMulti$44$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1186xfba5885a(TLRPC.Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath, DelayedMessage delayedMessage, Object parentObject, HashMap<String, String> params, boolean scheduled) {
        performSendMessageRequest(req, msgObj, originalPath, null, false, delayedMessage, parentObject, params, scheduled);
    }

    private DelayedMessage findMaxDelayedMessageForMessageId(int messageId, long dialogId) {
        DelayedMessage maxDelayedMessage = null;
        int maxDalyedMessageId = Integer.MIN_VALUE;
        for (Map.Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> messages = entry.getValue();
            int size = messages.size();
            for (int a = 0; a < size; a++) {
                DelayedMessage delayedMessage = messages.get(a);
                if ((delayedMessage.type == 4 || delayedMessage.type == 0) && delayedMessage.peer == dialogId) {
                    int mid = 0;
                    if (delayedMessage.obj != null) {
                        mid = delayedMessage.obj.getId();
                    } else if (delayedMessage.messageObjects != null && !delayedMessage.messageObjects.isEmpty()) {
                        mid = delayedMessage.messageObjects.get(delayedMessage.messageObjects.size() - 1).getId();
                    }
                    if (mid != 0 && mid > messageId && maxDelayedMessage == null && maxDalyedMessageId < mid) {
                        maxDelayedMessage = delayedMessage;
                        maxDalyedMessageId = mid;
                    }
                }
            }
        }
        return maxDelayedMessage;
    }

    public void performSendMessageRequest(final TLObject req, final MessageObject msgObj, final String originalPath, final DelayedMessage parentMessage, final boolean check, final DelayedMessage delayedMessage, final Object parentObject, HashMap<String, String> params, final boolean scheduled) {
        DelayedMessage maxDelayedMessage;
        if (!(req instanceof TLRPC.TL_messages_editMessage) && check && (maxDelayedMessage = findMaxDelayedMessageForMessageId(msgObj.getId(), msgObj.getDialogId())) != null) {
            maxDelayedMessage.addDelayedRequest(req, msgObj, originalPath, parentObject, delayedMessage, parentMessage != null ? parentMessage.scheduled : false);
            if (parentMessage != null && parentMessage.requests != null) {
                maxDelayedMessage.requests.addAll(parentMessage.requests);
                return;
            }
            return;
        }
        final TLRPC.Message newMsgObj = msgObj.messageOwner;
        putToSendingMessages(newMsgObj, scheduled);
        newMsgObj.reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda84
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.m1178xc47ba465(req, parentObject, msgObj, originalPath, parentMessage, check, delayedMessage, scheduled, newMsgObj, tLObject, tL_error);
            }
        }, new QuickAckDelegate() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74
            @Override // org.telegram.tgnet.QuickAckDelegate
            public final void run() {
                SendMessagesHelper.this.m1180x8efe81e7(newMsgObj);
            }
        }, (req instanceof TLRPC.TL_messages_sendMessage ? 128 : 0) | 68);
        if (parentMessage != null) {
            parentMessage.sendDelayedRequests();
        }
    }

    /* renamed from: lambda$performSendMessageRequest$60$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1178xc47ba465(final TLObject req, Object parentObject, final MessageObject msgObj, final String originalPath, DelayedMessage parentMessage, boolean check, final DelayedMessage delayedMessage, final boolean scheduled, final TLRPC.Message newMsgObj, final TLObject response, final TLRPC.TL_error error) {
        if (error != null && (((req instanceof TLRPC.TL_messages_sendMedia) || (req instanceof TLRPC.TL_messages_editMessage)) && FileRefController.isFileRefError(error.text))) {
            if (parentObject != null) {
                getFileRefController().requestReference(parentObject, req, msgObj, originalPath, parentMessage, Boolean.valueOf(check), delayedMessage, Boolean.valueOf(scheduled));
                return;
            } else if (delayedMessage != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda50
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.this.m1165x836cd8ee(newMsgObj, scheduled, req, delayedMessage);
                    }
                });
                return;
            }
        }
        if (req instanceof TLRPC.TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda53
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1168x18f3b06(error, newMsgObj, response, msgObj, originalPath, scheduled, req);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda68
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1177x10dc1fcf(scheduled, error, newMsgObj, response, msgObj, originalPath, req);
                }
            });
        }
    }

    /* renamed from: lambda$performSendMessageRequest$47$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1165x836cd8ee(TLRPC.Message newMsgObj, boolean scheduled, TLObject req, DelayedMessage delayedMessage) {
        removeFromSendingMessages(newMsgObj.id, scheduled);
        if (req instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia request = (TLRPC.TL_messages_sendMedia) req;
            if (request.media instanceof TLRPC.TL_inputMediaPhoto) {
                request.media = delayedMessage.inputUploadMedia;
            } else if (request.media instanceof TLRPC.TL_inputMediaDocument) {
                request.media = delayedMessage.inputUploadMedia;
            }
        } else if (req instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage request2 = (TLRPC.TL_messages_editMessage) req;
            if (request2.media instanceof TLRPC.TL_inputMediaPhoto) {
                request2.media = delayedMessage.inputUploadMedia;
            } else if (request2.media instanceof TLRPC.TL_inputMediaDocument) {
                request2.media = delayedMessage.inputUploadMedia;
            }
        }
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
    }

    /* renamed from: lambda$performSendMessageRequest$50$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1168x18f3b06(TLRPC.TL_error error, final TLRPC.Message newMsgObj, TLObject response, MessageObject msgObj, String originalPath, final boolean scheduled, TLObject req) {
        TLRPC.Message message;
        if (error == null) {
            String attachPath = newMsgObj.attachPath;
            final TLRPC.Updates updates = (TLRPC.Updates) response;
            ArrayList<TLRPC.Update> updatesArr = ((TLRPC.Updates) response).updates;
            int a = 0;
            while (true) {
                if (a >= updatesArr.size()) {
                    message = null;
                    break;
                }
                TLRPC.Update update = updatesArr.get(a);
                if (update instanceof TLRPC.TL_updateEditMessage) {
                    TLRPC.TL_updateEditMessage newMessage = (TLRPC.TL_updateEditMessage) update;
                    TLRPC.Message message2 = newMessage.message;
                    message = message2;
                    break;
                } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                    TLRPC.TL_updateEditChannelMessage newMessage2 = (TLRPC.TL_updateEditChannelMessage) update;
                    TLRPC.Message message3 = newMessage2.message;
                    message = message3;
                    break;
                } else if (!(update instanceof TLRPC.TL_updateNewScheduledMessage)) {
                    a++;
                } else {
                    TLRPC.TL_updateNewScheduledMessage newMessage3 = (TLRPC.TL_updateNewScheduledMessage) update;
                    TLRPC.Message message4 = newMessage3.message;
                    message = message4;
                    break;
                }
            }
            if (message != null) {
                ImageLoader.saveMessageThumbs(message);
                updateMediaPaths(msgObj, message, message.id, originalPath, false);
            }
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda67
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1167x4defb670(updates, newMsgObj, scheduled);
                }
            });
            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                stopVideoService(attachPath);
            }
            return;
        }
        AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(newMsgObj.attachPath);
        }
        removeFromSendingMessages(newMsgObj.id, scheduled);
        revertEditingMessageObject(msgObj);
    }

    /* renamed from: lambda$performSendMessageRequest$49$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1167x4defb670(TLRPC.Updates updates, final TLRPC.Message newMsgObj, final boolean scheduled) {
        getMessagesController().processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda48
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1166x68ae47af(newMsgObj, scheduled);
            }
        });
    }

    /* renamed from: lambda$performSendMessageRequest$48$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1166x68ae47af(TLRPC.Message newMsgObj, boolean scheduled) {
        processSentMessage(newMsgObj.id);
        removeFromSendingMessages(newMsgObj.id, scheduled);
    }

    /* renamed from: lambda$performSendMessageRequest$59$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1177x10dc1fcf(final boolean scheduled, TLRPC.TL_error error, final TLRPC.Message newMsgObj, TLObject response, final MessageObject msgObj, String originalPath, TLObject req) {
        boolean isSentError;
        boolean currentSchedule;
        String attachPath;
        int oldId;
        int existFlags;
        ArrayList<TLRPC.Message> sentMessages;
        LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies;
        TLRPC.Message message;
        ArrayList<TLRPC.Update> updatesArr;
        int existFlags2;
        SparseArray<TLRPC.MessageReplies> replies;
        TLRPC.MessageReplies messageReplies;
        boolean currentSchedule2 = scheduled;
        boolean isSentError2 = false;
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
            isSentError = true;
        } else {
            int oldId2 = newMsgObj.id;
            ArrayList<TLRPC.Message> sentMessages2 = new ArrayList<>();
            String attachPath2 = newMsgObj.attachPath;
            boolean scheduledOnline = newMsgObj.date == 2147483646;
            if (response instanceof TLRPC.TL_updateShortSentMessage) {
                final TLRPC.TL_updateShortSentMessage res = (TLRPC.TL_updateShortSentMessage) response;
                attachPath = attachPath2;
                sentMessages = sentMessages2;
                oldId = oldId2;
                updateMediaPaths(msgObj, null, res.id, null, false);
                int existFlags3 = msgObj.getMediaExistanceFlags();
                int i = res.id;
                newMsgObj.id = i;
                newMsgObj.local_id = i;
                newMsgObj.date = res.date;
                newMsgObj.entities = res.entities;
                newMsgObj.out = res.out;
                if ((res.flags & ConnectionsManager.FileTypeVideo) != 0) {
                    newMsgObj.ttl_period = res.ttl_period;
                    newMsgObj.flags |= ConnectionsManager.FileTypeVideo;
                }
                if (res.media != null) {
                    newMsgObj.media = res.media;
                    newMsgObj.flags |= 512;
                    ImageLoader.saveMessageThumbs(newMsgObj);
                }
                if (((res.media instanceof TLRPC.TL_messageMediaGame) || (res.media instanceof TLRPC.TL_messageMediaInvoice)) && !TextUtils.isEmpty(res.message)) {
                    newMsgObj.message = res.message;
                }
                if (!newMsgObj.entities.isEmpty()) {
                    newMsgObj.flags |= 128;
                }
                if (0 == 0) {
                    Integer value = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(newMsgObj.dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(getMessagesStorage().getDialogReadMax(newMsgObj.out, newMsgObj.dialog_id));
                        getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(newMsgObj.dialog_id), value);
                    }
                    newMsgObj.unread = value.intValue() < newMsgObj.id;
                }
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda63
                    @Override // java.lang.Runnable
                    public final void run() {
                        SendMessagesHelper.this.m1169xe6d0a9c7(res);
                    }
                });
                sentMessages.add(newMsgObj);
                existFlags = existFlags3;
                currentSchedule = false;
                isSentError = false;
            } else {
                attachPath = attachPath2;
                sentMessages = sentMessages2;
                oldId = oldId2;
                if (response instanceof TLRPC.Updates) {
                    final TLRPC.Updates updates = (TLRPC.Updates) response;
                    ArrayList<TLRPC.Update> updatesArr2 = ((TLRPC.Updates) response).updates;
                    TLRPC.Message message2 = null;
                    LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies2 = null;
                    int a = 0;
                    while (true) {
                        if (a >= updatesArr2.size()) {
                            currentSchedule = currentSchedule2;
                            isSentError = isSentError2;
                            channelReplies = null;
                            message = message2;
                            break;
                        }
                        TLRPC.Update update = updatesArr2.get(a);
                        if (update instanceof TLRPC.TL_updateNewMessage) {
                            final TLRPC.TL_updateNewMessage newMessage = (TLRPC.TL_updateNewMessage) update;
                            TLRPC.Message message3 = newMessage.message;
                            sentMessages.add(message3);
                            currentSchedule = currentSchedule2;
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda61
                                @Override // java.lang.Runnable
                                public final void run() {
                                    SendMessagesHelper.this.m1170xcc121888(newMessage);
                                }
                            });
                            updatesArr2.remove(a);
                            isSentError = isSentError2;
                            message = message3;
                            channelReplies = null;
                            break;
                        }
                        TLRPC.Message message4 = message2;
                        currentSchedule = currentSchedule2;
                        if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                            final TLRPC.TL_updateNewChannelMessage newMessage2 = (TLRPC.TL_updateNewChannelMessage) update;
                            long channelId = MessagesController.getUpdateChannelId(newMessage2);
                            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(channelId));
                            if (chat == null || chat.megagroup) {
                                if (newMessage2.message.reply_to == null) {
                                    isSentError = isSentError2;
                                } else if (newMessage2.message.reply_to.reply_to_top_id != 0 || newMessage2.message.reply_to.reply_to_msg_id != 0) {
                                    if (0 == 0) {
                                        channelReplies2 = new LongSparseArray<>();
                                    }
                                    isSentError = isSentError2;
                                    long did = MessageObject.getDialogId(newMessage2.message);
                                    SparseArray<TLRPC.MessageReplies> replies2 = channelReplies2.get(did);
                                    if (replies2 == null) {
                                        replies = new SparseArray<>();
                                        channelReplies2.put(did, replies);
                                    } else {
                                        replies = replies2;
                                    }
                                    LongSparseArray<SparseArray<TLRPC.MessageReplies>> channelReplies3 = channelReplies2;
                                    int id = newMessage2.message.reply_to.reply_to_top_id != 0 ? newMessage2.message.reply_to.reply_to_top_id : newMessage2.message.reply_to.reply_to_msg_id;
                                    TLRPC.MessageReplies messageReplies2 = replies.get(id);
                                    if (messageReplies2 == null) {
                                        messageReplies = new TLRPC.TL_messageReplies();
                                        replies.put(id, messageReplies);
                                    } else {
                                        messageReplies = messageReplies2;
                                    }
                                    if (newMessage2.message.from_id != null) {
                                        messageReplies.recent_repliers.add(0, newMessage2.message.from_id);
                                    }
                                    messageReplies.replies++;
                                    channelReplies2 = channelReplies3;
                                }
                                TLRPC.Message message5 = newMessage2.message;
                                message = message5;
                                sentMessages.add(message5);
                                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda59
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        SendMessagesHelper.this.m1171xb1538749(newMessage2);
                                    }
                                });
                                updatesArr2.remove(a);
                                channelReplies = channelReplies2;
                            }
                            isSentError = isSentError2;
                            TLRPC.Message message52 = newMessage2.message;
                            message = message52;
                            sentMessages.add(message52);
                            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda59
                                @Override // java.lang.Runnable
                                public final void run() {
                                    SendMessagesHelper.this.m1171xb1538749(newMessage2);
                                }
                            });
                            updatesArr2.remove(a);
                            channelReplies = channelReplies2;
                        } else {
                            isSentError = isSentError2;
                            if (!(update instanceof TLRPC.TL_updateNewScheduledMessage)) {
                                a++;
                                message2 = message4;
                                currentSchedule2 = currentSchedule;
                                isSentError2 = isSentError;
                            } else {
                                TLRPC.Message message6 = ((TLRPC.TL_updateNewScheduledMessage) update).message;
                                message = message6;
                                sentMessages.add(message6);
                                updatesArr2.remove(a);
                                channelReplies = null;
                                break;
                            }
                        }
                    }
                    if (channelReplies == null) {
                        updatesArr = updatesArr2;
                    } else {
                        getMessagesStorage().putChannelViews(null, null, channelReplies, true);
                        updatesArr = updatesArr2;
                        getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, null, null, channelReplies, true);
                    }
                    if (message != null) {
                        MessageObject.getDialogId(message);
                        if (scheduledOnline && message.date != 2147483646) {
                            currentSchedule = false;
                        }
                        ImageLoader.saveMessageThumbs(message);
                        if (!currentSchedule) {
                            Integer value2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                            if (value2 == null) {
                                value2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), value2);
                            }
                            message.unread = value2.intValue() < message.id;
                        }
                        msgObj.messageOwner.post_author = message.post_author;
                        if ((message.flags & ConnectionsManager.FileTypeVideo) != 0) {
                            msgObj.messageOwner.ttl_period = message.ttl_period;
                            msgObj.messageOwner.flags |= ConnectionsManager.FileTypeVideo;
                        }
                        msgObj.messageOwner.entities = message.entities;
                        updateMediaPaths(msgObj, message, message.id, originalPath, false);
                        existFlags2 = msgObj.getMediaExistanceFlags();
                        newMsgObj.id = message.id;
                    } else {
                        isSentError = true;
                        existFlags2 = 0;
                    }
                    Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.m1172x9694f60a(updates);
                        }
                    });
                    existFlags = existFlags2;
                } else {
                    currentSchedule = currentSchedule2;
                    isSentError = false;
                    existFlags = 0;
                }
            }
            if (MessageObject.isLiveLocationMessage(newMsgObj) && newMsgObj.via_bot_id == 0 && TextUtils.isEmpty(newMsgObj.via_bot_name)) {
                getLocationController().addSharingLocation(newMsgObj);
            }
            if (!isSentError) {
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                newMsgObj.send_state = 0;
                if (scheduled && !currentSchedule) {
                    ArrayList<Integer> messageIds = new ArrayList<>();
                    messageIds.add(Integer.valueOf(oldId));
                    getMessagesController().deleteMessages(messageIds, null, null, newMsgObj.dialog_id, false, true);
                    final ArrayList<TLRPC.Message> arrayList = sentMessages;
                    final int i2 = oldId;
                    final String str = attachPath;
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda28
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.m1174x6117d38c(arrayList, msgObj, newMsgObj, i2, scheduled, str);
                        }
                    });
                } else {
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), 0L, Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
                    final int i3 = oldId;
                    final ArrayList<TLRPC.Message> arrayList2 = sentMessages;
                    final int i4 = existFlags;
                    final String str2 = attachPath;
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda43
                        @Override // java.lang.Runnable
                        public final void run() {
                            SendMessagesHelper.this.m1176x2b9ab10e(newMsgObj, i3, scheduled, arrayList2, i4, str2);
                        }
                    });
                }
            }
        }
        if (isSentError) {
            getMessagesStorage().markMessageAsSendError(newMsgObj, scheduled);
            newMsgObj.send_state = 2;
            getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
            processSentMessage(newMsgObj.id);
            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                stopVideoService(newMsgObj.attachPath);
            }
            removeFromSendingMessages(newMsgObj.id, scheduled);
        }
    }

    /* renamed from: lambda$performSendMessageRequest$51$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1169xe6d0a9c7(TLRPC.TL_updateShortSentMessage res) {
        getMessagesController().processNewDifferenceParams(-1, res.pts, res.date, res.pts_count);
    }

    /* renamed from: lambda$performSendMessageRequest$52$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1170xcc121888(TLRPC.TL_updateNewMessage newMessage) {
        getMessagesController().processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    /* renamed from: lambda$performSendMessageRequest$53$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1171xb1538749(TLRPC.TL_updateNewChannelMessage newMessage) {
        getMessagesController().processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.peer_id.channel_id);
    }

    /* renamed from: lambda$performSendMessageRequest$54$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1172x9694f60a(TLRPC.Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    /* renamed from: lambda$performSendMessageRequest$56$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1174x6117d38c(ArrayList sentMessages, final MessageObject msgObj, final TLRPC.Message newMsgObj, final int oldId, final boolean scheduled, String attachPath) {
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1173x7bd664cb(msgObj, newMsgObj, oldId, scheduled);
            }
        });
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(attachPath);
        }
    }

    /* renamed from: lambda$performSendMessageRequest$55$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1173x7bd664cb(MessageObject msgObj, TLRPC.Message newMsgObj, int oldId, boolean scheduled) {
        ArrayList<MessageObject> messageObjects = new ArrayList<>();
        messageObjects.add(new MessageObject(msgObj.currentAccount, msgObj.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(newMsgObj.dialog_id, messageObjects, false);
        getMediaDataController().increasePeerRaiting(newMsgObj.dialog_id);
        processSentMessage(oldId);
        removeFromSendingMessages(oldId, scheduled);
    }

    /* renamed from: lambda$performSendMessageRequest$58$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1176x2b9ab10e(final TLRPC.Message newMsgObj, final int oldId, final boolean scheduled, ArrayList sentMessages, final int existFlags, String attachPath) {
        getMessagesStorage().updateMessageStateAndId(newMsgObj.random_id, MessageObject.getPeerId(newMsgObj.peer_id), Integer.valueOf(oldId), newMsgObj.id, 0, false, scheduled ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, false, false, 0, scheduled);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1175x4659424d(newMsgObj, oldId, existFlags, scheduled);
            }
        });
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(attachPath);
        }
    }

    /* renamed from: lambda$performSendMessageRequest$57$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1175x4659424d(TLRPC.Message newMsgObj, int oldId, int existFlags, boolean scheduled) {
        getMediaDataController().increasePeerRaiting(newMsgObj.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), 0L, Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId, scheduled);
    }

    /* renamed from: lambda$performSendMessageRequest$62$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1180x8efe81e7(final TLRPC.Message newMsgObj) {
        final int msg_id = newMsgObj.id;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1179xa9bd1326(newMsgObj, msg_id);
            }
        });
    }

    /* renamed from: lambda$performSendMessageRequest$61$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1179xa9bd1326(TLRPC.Message newMsgObj, int msg_id) {
        newMsgObj.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(msg_id));
    }

    /* JADX WARN: Removed duplicated region for block: B:143:0x035a  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x03ca  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r22, org.telegram.tgnet.TLRPC.Message r23, int r24, java.lang.String r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 1991
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.updateMediaPaths(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Message, int, java.lang.String, boolean):void");
    }

    private void putToDelayedMessages(String location, DelayedMessage message) {
        ArrayList<DelayedMessage> arrayList = this.delayedMessages.get(location);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.delayedMessages.put(location, arrayList);
        }
        arrayList.add(message);
    }

    public ArrayList<DelayedMessage> getDelayedMessages(String location) {
        return this.delayedMessages.get(location);
    }

    public long getNextRandomId() {
        long val = 0;
        while (val == 0) {
            val = Utilities.random.nextLong();
        }
        return val;
    }

    public void checkUnsentMessages() {
        getMessagesStorage().getUnsentMessages(1000);
    }

    public void processUnsentMessages(final ArrayList<TLRPC.Message> messages, final ArrayList<TLRPC.Message> scheduledMessages, final ArrayList<TLRPC.User> users, final ArrayList<TLRPC.Chat> chats, final ArrayList<TLRPC.EncryptedChat> encryptedChats) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1194x625b480d(users, chats, encryptedChats, messages, scheduledMessages);
            }
        });
    }

    /* renamed from: lambda$processUnsentMessages$63$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1194x625b480d(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList messages, ArrayList scheduledMessages) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        int N = messages.size();
        for (int a = 0; a < N; a++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) messages.get(a), false, true);
            long groupId = messageObject.getGroupId();
            if (groupId != 0 && messageObject.messageOwner.params != null && !messageObject.messageOwner.params.containsKey("final") && (a == N - 1 || ((TLRPC.Message) messages.get(a + 1)).grouped_id != groupId)) {
                messageObject.messageOwner.params.put("final", IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
            }
            retrySendMessage(messageObject, true);
        }
        if (scheduledMessages != null) {
            for (int a2 = 0; a2 < scheduledMessages.size(); a2++) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, (TLRPC.Message) scheduledMessages.get(a2), false, true);
                messageObject2.scheduled = true;
                retrySendMessage(messageObject2, true);
            }
        }
    }

    public ImportingStickers getImportingStickers(String shortName) {
        return this.importingStickersMap.get(shortName);
    }

    public ImportingHistory getImportingHistory(long dialogId) {
        return this.importingHistoryMap.get(dialogId);
    }

    public boolean isImportingStickers() {
        return this.importingStickersMap.size() != 0;
    }

    public boolean isImportingHistory() {
        return this.importingHistoryMap.size() != 0;
    }

    public void prepareImportHistory(final long dialogId, final Uri uri, final ArrayList<Uri> mediaUris, final MessagesStorage.LongCallback onStartImport) {
        TLRPC.Chat chat;
        if (this.importingHistoryMap.get(dialogId) != null) {
            onStartImport.run(0L);
        } else if (DialogObject.isChatDialog(dialogId) && (chat = getMessagesController().getChat(Long.valueOf(-dialogId))) != null && !chat.megagroup) {
            getMessagesController().convertToMegaGroup(null, -dialogId, null, new MessagesStorage.LongCallback() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda73
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    SendMessagesHelper.this.m1189x12cad7b8(uri, mediaUris, onStartImport, j);
                }
            });
        } else {
            new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1191x8d12017d(mediaUris, dialogId, uri, onStartImport);
                }
            }).start();
        }
    }

    /* renamed from: lambda$prepareImportHistory$64$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1189x12cad7b8(Uri uri, ArrayList mediaUris, MessagesStorage.LongCallback onStartImport, long chatId) {
        if (chatId != 0) {
            prepareImportHistory(-chatId, uri, mediaUris, onStartImport);
        } else {
            onStartImport.run(0L);
        }
    }

    /* renamed from: lambda$prepareImportHistory$69$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1191x8d12017d(ArrayList mediaUris, final long dialogId, Uri uri, final MessagesStorage.LongCallback onStartImport) {
        Uri mediaUri;
        ArrayList arrayList = mediaUris != null ? mediaUris : new ArrayList();
        final ImportingHistory importingHistory = new ImportingHistory();
        importingHistory.mediaPaths = arrayList;
        importingHistory.dialogId = dialogId;
        importingHistory.peer = getMessagesController().getInputPeer(dialogId);
        final HashMap<String, ImportingHistory> files = new HashMap<>();
        int N = arrayList.size();
        for (int a = 0; a < N + 1; a++) {
            if (a == 0) {
                mediaUri = uri;
            } else {
                mediaUri = (Uri) arrayList.get(a - 1);
            }
            if (mediaUri == null || AndroidUtilities.isInternalUri(mediaUri)) {
                if (a == 0) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            MessagesStorage.LongCallback.this.run(0L);
                        }
                    });
                    return;
                }
            } else {
                String path = MediaController.copyFileToCache(mediaUri, "txt");
                if (path == null) {
                    continue;
                } else {
                    File f = new File(path);
                    if (f.exists()) {
                        long size = f.length();
                        if (size != 0) {
                            importingHistory.totalSize += size;
                            if (a == 0) {
                                if (size > 33554432) {
                                    f.delete();
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda10
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            SendMessagesHelper.lambda$prepareImportHistory$67(MessagesStorage.LongCallback.this);
                                        }
                                    });
                                    return;
                                }
                                importingHistory.historyPath = path;
                            } else {
                                importingHistory.uploadMedia.add(path);
                            }
                            importingHistory.uploadSet.add(path);
                            files.put(path, importingHistory);
                        }
                    }
                    if (a == 0) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda9
                            @Override // java.lang.Runnable
                            public final void run() {
                                MessagesStorage.LongCallback.this.run(0L);
                            }
                        });
                        return;
                    }
                }
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1190xa7d092bc(files, dialogId, importingHistory, onStartImport);
            }
        });
    }

    public static /* synthetic */ void lambda$prepareImportHistory$67(MessagesStorage.LongCallback onStartImport) {
        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("ImportFileTooLarge", org.telegram.messenger.beta.R.string.ImportFileTooLarge), 0).show();
        onStartImport.run(0L);
    }

    /* renamed from: lambda$prepareImportHistory$68$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1190xa7d092bc(HashMap files, long dialogId, ImportingHistory importingHistory, MessagesStorage.LongCallback onStartImport) {
        this.importingHistoryFiles.putAll(files);
        this.importingHistoryMap.put(dialogId, importingHistory);
        getFileLoader().uploadFile(importingHistory.historyPath, false, true, 0L, ConnectionsManager.FileTypeFile, true);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(dialogId));
        onStartImport.run(dialogId);
        Intent intent = new Intent(ApplicationLoader.applicationContext, ImportingService.class);
        try {
            ApplicationLoader.applicationContext.startService(intent);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void prepareImportStickers(final String title, final String shortName, final String sofrware, final ArrayList<ImportingSticker> paths, final MessagesStorage.StringCallback onStartImport) {
        if (this.importingStickersMap.get(shortName) != null) {
            onStartImport.run(null);
        } else {
            new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.this.m1193xfa297655(title, shortName, sofrware, paths, onStartImport);
                }
            }).start();
        }
    }

    /* renamed from: lambda$prepareImportStickers$72$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1193xfa297655(String title, final String shortName, String sofrware, ArrayList paths, final MessagesStorage.StringCallback onStartImport) {
        final ImportingStickers importingStickers = new ImportingStickers();
        importingStickers.title = title;
        importingStickers.shortName = shortName;
        importingStickers.software = sofrware;
        final HashMap<String, ImportingStickers> files = new HashMap<>();
        int N = paths.size();
        for (int a = 0; a < N; a++) {
            ImportingSticker sticker = (ImportingSticker) paths.get(a);
            File f = new File(sticker.path);
            if (f.exists()) {
                long size = f.length();
                if (size != 0) {
                    importingStickers.totalSize += size;
                    importingStickers.uploadMedia.add(sticker);
                    importingStickers.uploadSet.put(sticker.path, sticker);
                    files.put(sticker.path, importingStickers);
                }
            }
            if (a == 0) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.StringCallback.this.run(null);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.this.m1192x14e80794(importingStickers, files, shortName, onStartImport);
            }
        });
    }

    /* renamed from: lambda$prepareImportStickers$71$org-telegram-messenger-SendMessagesHelper */
    public /* synthetic */ void m1192x14e80794(ImportingStickers importingStickers, HashMap files, String shortName, MessagesStorage.StringCallback onStartImport) {
        if (importingStickers.uploadMedia.get(0).item != null) {
            importingStickers.startImport();
        } else {
            this.importingStickersFiles.putAll(files);
            this.importingStickersMap.put(shortName, importingStickers);
            importingStickers.initImport();
            getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, shortName);
            onStartImport.run(shortName);
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, ImportingService.class);
        try {
            ApplicationLoader.applicationContext.startService(intent);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public TLRPC.TL_photo generatePhotoSizes(String path, Uri imageUri) {
        return generatePhotoSizes(null, path, imageUri);
    }

    public TLRPC.TL_photo generatePhotoSizes(TLRPC.TL_photo photo, String path, Uri imageUri) {
        Bitmap bitmap = ImageLoader.loadBitmap(path, imageUri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
        if (bitmap == null) {
            bitmap = ImageLoader.loadBitmap(path, imageUri, 800.0f, 800.0f, true);
        }
        ArrayList<TLRPC.PhotoSize> sizes = new ArrayList<>();
        TLRPC.PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, true);
        if (size != null) {
            sizes.add(size);
        }
        TLRPC.PhotoSize size2 = ImageLoader.scaleAndSaveImage(bitmap, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true, 80, false, 101, 101);
        if (size2 != null) {
            sizes.add(size2);
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (sizes.isEmpty()) {
            return null;
        }
        getUserConfig().saveConfig(false);
        if (photo == null) {
            photo = new TLRPC.TL_photo();
        }
        photo.date = getConnectionsManager().getCurrentTime();
        photo.sizes = sizes;
        photo.file_reference = new byte[0];
        return photo;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(8:355|67|(7:69|359|70|71|353|72|73)(1:82)|(3:351|84|(6:86|87|(1:89)|333|95|98))|94|333|95|98) */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0196, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x0197, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0246  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x027a  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x0281  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x02d2  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x02df A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:191:0x03c1  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x0573  */
    /* JADX WARN: Removed duplicated region for block: B:281:0x0580  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x0586  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x0597  */
    /* JADX WARN: Removed duplicated region for block: B:286:0x059f  */
    /* JADX WARN: Removed duplicated region for block: B:289:0x05a5 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:292:0x05ae  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x05bb  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x061a  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x061d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:339:0x01fa A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:349:0x01de A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int prepareSendingDocumentInternal(final org.telegram.messenger.AccountInstance r48, java.lang.String r49, java.lang.String r50, android.net.Uri r51, java.lang.String r52, final long r53, final org.telegram.messenger.MessageObject r55, final org.telegram.messenger.MessageObject r56, java.lang.CharSequence r57, final java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r58, final org.telegram.messenger.MessageObject r59, long[] r60, boolean r61, boolean r62, final boolean r63, final int r64, java.lang.Integer[] r65) {
        /*
            Method dump skipped, instructions count: 1752
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, long[], boolean, boolean, boolean, int, java.lang.Integer[]):int");
    }

    public static /* synthetic */ void lambda$prepareSendingDocumentInternal$73(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_document documentFinal, String pathFinal, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, String captionFinal, ArrayList entities, boolean notify, int scheduleDate) {
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(editingMessageObject, null, null, documentFinal, pathFinal, params, false, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(documentFinal, null, pathFinal, dialogId, replyToMsg, replyToTopMsg, captionFinal, entities, null, params, notify, scheduleDate, 0, parentFinal, null);
        }
    }

    private static boolean checkFileSize(AccountInstance accountInstance, Uri uri) {
        long len = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                AssetFileDescriptor assetFileDescriptor = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri, "r", null);
                if (assetFileDescriptor != null) {
                    len = assetFileDescriptor.getLength();
                }
                Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_size"}, null, null, null);
                int sizeIndex = cursor.getColumnIndex("_size");
                cursor.moveToFirst();
                len = cursor.getLong(sizeIndex);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return !FileLoader.checkUploadFileSize(accountInstance.getCurrentAccount(), len);
    }

    public static void prepareSendingDocument(AccountInstance accountInstance, String path, String originalPath, Uri uri, String caption, String mine, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, InputContentInfoCompat inputContent, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        ArrayList<Uri> uris;
        if ((path == null || originalPath == null) && uri == null) {
            return;
        }
        ArrayList<String> paths = new ArrayList<>();
        ArrayList<String> originalPaths = new ArrayList<>();
        if (uri == null) {
            uris = null;
        } else {
            ArrayList<Uri> uris2 = new ArrayList<>();
            uris2.add(uri);
            uris = uris2;
        }
        if (path != null) {
            paths.add(path);
            originalPaths.add(originalPath);
        }
        prepareSendingDocuments(accountInstance, paths, originalPaths, uris, caption, mine, dialogId, replyToMsg, replyToTopMsg, inputContent, editingMessageObject, notify, scheduleDate);
    }

    public static void prepareSendingAudioDocuments(final AccountInstance accountInstance, final ArrayList<MessageObject> messageObjects, final String caption, final long dialogId, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final MessageObject editingMessageObject, final boolean notify, final int scheduleDate) {
        new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingAudioDocuments$75(messageObjects, dialogId, accountInstance, caption, editingMessageObject, replyToMsg, replyToTopMsg, notify, scheduleDate);
            }
        }).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0086  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0092  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0096  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00b2  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00c4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$75(java.util.ArrayList r29, final long r30, final org.telegram.messenger.AccountInstance r32, java.lang.String r33, final org.telegram.messenger.MessageObject r34, final org.telegram.messenger.MessageObject r35, final org.telegram.messenger.MessageObject r36, final boolean r37, final int r38) {
        /*
            Method dump skipped, instructions count: 280
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$75(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$74(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_document documentFinal, MessageObject messageObject, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, String captionFinal, boolean notify, int scheduleDate) {
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(editingMessageObject, null, null, documentFinal, messageObject.messageOwner.attachPath, params, false, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(documentFinal, null, messageObject.messageOwner.attachPath, dialogId, replyToMsg, replyToTopMsg, captionFinal, null, null, params, notify, scheduleDate, 0, parentFinal, null);
        }
    }

    private static void finishGroup(final AccountInstance accountInstance, final long groupId, final int scheduleDate) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$finishGroup$76(AccountInstance.this, groupId, scheduleDate);
            }
        });
    }

    public static /* synthetic */ void lambda$finishGroup$76(AccountInstance accountInstance, long groupId, int scheduleDate) {
        SendMessagesHelper instance = accountInstance.getSendMessagesHelper();
        HashMap<String, ArrayList<DelayedMessage>> hashMap = instance.delayedMessages;
        ArrayList<DelayedMessage> arrayList = hashMap.get("group_" + groupId);
        if (arrayList != null && !arrayList.isEmpty()) {
            DelayedMessage message = arrayList.get(0);
            MessageObject prevMessage = message.messageObjects.get(message.messageObjects.size() - 1);
            message.finalGroupMessage = prevMessage.getId();
            prevMessage.messageOwner.params.put("final", IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
            TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
            messagesRes.messages.add(prevMessage.messageOwner);
            accountInstance.getMessagesStorage().putMessages((TLRPC.messages_Messages) messagesRes, message.peer, -2, 0, false, scheduleDate != 0);
            instance.sendReadyToSendGroup(message, true, true);
        }
    }

    public static void prepareSendingDocuments(final AccountInstance accountInstance, final ArrayList<String> paths, final ArrayList<String> originalPaths, final ArrayList<Uri> uris, final String caption, final String mime, final long dialogId, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final InputContentInfoCompat inputContent, final MessageObject editingMessageObject, final boolean notify, final int scheduleDate) {
        if (paths == null && originalPaths == null && uris == null) {
            return;
        }
        if (paths != null && originalPaths != null && paths.size() != originalPaths.size()) {
            return;
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda33
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingDocuments$77(dialogId, paths, caption, accountInstance, scheduleDate, originalPaths, mime, replyToMsg, replyToTopMsg, editingMessageObject, inputContent, notify, uris);
            }
        });
    }

    /* JADX WARN: Incorrect condition in loop: B:39:0x00e8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingDocuments$77(long r36, java.util.ArrayList r38, java.lang.String r39, org.telegram.messenger.AccountInstance r40, int r41, java.util.ArrayList r42, java.lang.String r43, org.telegram.messenger.MessageObject r44, org.telegram.messenger.MessageObject r45, org.telegram.messenger.MessageObject r46, androidx.core.view.inputmethod.InputContentInfoCompat r47, boolean r48, java.util.ArrayList r49) {
        /*
            Method dump skipped, instructions count: 417
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingDocuments$77(long, java.util.ArrayList, java.lang.String, org.telegram.messenger.AccountInstance, int, java.util.ArrayList, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, androidx.core.view.inputmethod.InputContentInfoCompat, boolean, java.util.ArrayList):void");
    }

    private static void handleError(final int error, final AccountInstance accountInstance) {
        if (error != 0) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.lambda$handleError$78(error, accountInstance);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$handleError$78(int finalError, AccountInstance accountInstance) {
        try {
            if (finalError == 1) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.getString("UnsupportedAttachment", org.telegram.messenger.beta.R.string.UnsupportedAttachment));
            } else if (finalError == 2) {
                NotificationCenter.getInstance(accountInstance.getCurrentAccount()).postNotificationName(NotificationCenter.currentUserShowLimitReachedDialog, 6);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String imageFilePath, Uri imageUri, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, CharSequence caption, ArrayList<TLRPC.MessageEntity> entities, ArrayList<TLRPC.InputDocument> stickers, InputContentInfoCompat inputContent, int ttl, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        prepareSendingPhoto(accountInstance, imageFilePath, null, imageUri, dialogId, replyToMsg, replyToTopMsg, caption, entities, stickers, inputContent, ttl, editingMessageObject, null, notify, scheduleDate, false);
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String imageFilePath, String thumbFilePath, Uri imageUri, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, CharSequence caption, ArrayList<TLRPC.MessageEntity> entities, ArrayList<TLRPC.InputDocument> stickers, InputContentInfoCompat inputContent, int ttl, MessageObject editingMessageObject, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
        SendingMediaInfo info = new SendingMediaInfo();
        info.path = imageFilePath;
        info.thumbPath = thumbFilePath;
        info.uri = imageUri;
        if (caption != null) {
            info.caption = caption.toString();
        }
        info.entities = entities;
        info.ttl = ttl;
        if (stickers != null) {
            info.masks = new ArrayList<>(stickers);
        }
        info.videoEditedInfo = videoEditedInfo;
        ArrayList<SendingMediaInfo> infos = new ArrayList<>();
        infos.add(info);
        prepareSendingMedia(accountInstance, infos, dialogId, replyToMsg, replyToTopMsg, inputContent, forceDocument, false, editingMessageObject, notify, scheduleDate);
    }

    public static void prepareSendingBotContextResult(final AccountInstance accountInstance, final TLRPC.BotInlineResult result, final HashMap<String, String> params, final long dialogId, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final boolean notify, final int scheduleDate) {
        if (result == null) {
            return;
        }
        if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto) {
            new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    SendMessagesHelper.lambda$prepareSendingBotContextResult$80(dialogId, result, accountInstance, params, replyToMsg, replyToTopMsg, notify, scheduleDate);
                }
            }).run();
        } else if (!(result.send_message instanceof TLRPC.TL_botInlineMessageText)) {
            if (!(result.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue)) {
                if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo) {
                    if (result.send_message.period != 0 || result.send_message.proximity_notification_radius != 0) {
                        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
                        location.period = result.send_message.period != 0 ? result.send_message.period : 900;
                        location.geo = result.send_message.geo;
                        location.heading = result.send_message.heading;
                        location.proximity_notification_radius = result.send_message.proximity_notification_radius;
                        accountInstance.getSendMessagesHelper().sendMessage(location, dialogId, replyToMsg, replyToTopMsg, result.send_message.reply_markup, params, notify, scheduleDate);
                        return;
                    }
                    TLRPC.TL_messageMediaGeo location2 = new TLRPC.TL_messageMediaGeo();
                    location2.geo = result.send_message.geo;
                    location2.heading = result.send_message.heading;
                    accountInstance.getSendMessagesHelper().sendMessage(location2, dialogId, replyToMsg, replyToTopMsg, result.send_message.reply_markup, params, notify, scheduleDate);
                    return;
                } else if (result.send_message instanceof TLRPC.TL_botInlineMessageMediaContact) {
                    TLRPC.User user = new TLRPC.TL_user();
                    user.phone = result.send_message.phone_number;
                    user.first_name = result.send_message.first_name;
                    user.last_name = result.send_message.last_name;
                    TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
                    reason.text = result.send_message.vcard;
                    reason.platform = "";
                    reason.reason = "";
                    user.restriction_reason.add(reason);
                    accountInstance.getSendMessagesHelper().sendMessage(user, dialogId, replyToMsg, replyToTopMsg, result.send_message.reply_markup, params, notify, scheduleDate);
                    return;
                } else if (!(result.send_message instanceof TLRPC.TL_botInlineMessageMediaInvoice) || DialogObject.isEncryptedDialog(dialogId)) {
                    return;
                } else {
                    TLRPC.TL_botInlineMessageMediaInvoice invoice = (TLRPC.TL_botInlineMessageMediaInvoice) result.send_message;
                    TLRPC.TL_messageMediaInvoice messageMediaInvoice = new TLRPC.TL_messageMediaInvoice();
                    messageMediaInvoice.shipping_address_requested = invoice.shipping_address_requested;
                    messageMediaInvoice.test = invoice.test;
                    messageMediaInvoice.title = invoice.title;
                    messageMediaInvoice.description = invoice.description;
                    if (invoice.photo != null) {
                        messageMediaInvoice.photo = invoice.photo;
                        messageMediaInvoice.flags |= 1;
                    }
                    messageMediaInvoice.currency = invoice.currency;
                    messageMediaInvoice.total_amount = invoice.total_amount;
                    messageMediaInvoice.start_param = "";
                    accountInstance.getSendMessagesHelper().sendMessage(messageMediaInvoice, dialogId, replyToMsg, replyToTopMsg, result.send_message.reply_markup, params, notify, scheduleDate);
                    return;
                }
            }
            TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
            venue.geo = result.send_message.geo;
            venue.address = result.send_message.address;
            venue.title = result.send_message.title;
            venue.provider = result.send_message.provider;
            venue.venue_id = result.send_message.venue_id;
            String str = result.send_message.venue_type;
            venue.venue_id = str;
            venue.venue_type = str;
            if (venue.venue_type == null) {
                venue.venue_type = "";
            }
            accountInstance.getSendMessagesHelper().sendMessage(venue, dialogId, replyToMsg, replyToTopMsg, result.send_message.reply_markup, params, notify, scheduleDate);
        } else {
            TLRPC.WebPage webPage = null;
            if (DialogObject.isEncryptedDialog(dialogId)) {
                int a = 0;
                while (true) {
                    if (a >= result.send_message.entities.size()) {
                        break;
                    }
                    TLRPC.MessageEntity entity = result.send_message.entities.get(a);
                    if (!(entity instanceof TLRPC.TL_messageEntityUrl)) {
                        a++;
                    } else {
                        webPage = new TLRPC.TL_webPagePending();
                        webPage.url = result.send_message.message.substring(entity.offset, entity.offset + entity.length);
                        break;
                    }
                }
            }
            accountInstance.getSendMessagesHelper().sendMessage(result.send_message.message, dialogId, replyToMsg, replyToTopMsg, webPage, !result.send_message.no_webpage, result.send_message.entities, result.send_message.reply_markup, params, notify, scheduleDate, null);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:163:0x0492  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0498  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x04a4  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x04f8  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x053b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingBotContextResult$80(final long r28, final org.telegram.tgnet.TLRPC.BotInlineResult r30, final org.telegram.messenger.AccountInstance r31, final java.util.HashMap r32, final org.telegram.messenger.MessageObject r33, final org.telegram.messenger.MessageObject r34, final boolean r35, final int r36) {
        /*
            Method dump skipped, instructions count: 1466
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$80(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    public static /* synthetic */ void lambda$prepareSendingBotContextResult$79(TLRPC.TL_document finalDocument, Bitmap[] precahcedThumb, String[] precachedKey, AccountInstance accountInstance, String finalPathFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, TLRPC.BotInlineResult result, HashMap params, boolean notify, int scheduleDate, TLRPC.TL_photo finalPhoto, TLRPC.TL_game finalGame) {
        if (finalDocument != null) {
            if (precahcedThumb[0] != null && precachedKey[0] != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(precahcedThumb[0]), precachedKey[0], false);
            }
            accountInstance.getSendMessagesHelper().sendMessage(finalDocument, null, finalPathFinal, dialogId, replyToMsg, replyToTopMsg, result.send_message.message, result.send_message.entities, result.send_message.reply_markup, params, notify, scheduleDate, 0, result, null);
        } else if (finalPhoto != null) {
            accountInstance.getSendMessagesHelper().sendMessage(finalPhoto, result.content != null ? result.content.url : null, dialogId, replyToMsg, replyToTopMsg, result.send_message.message, result.send_message.entities, result.send_message.reply_markup, params, notify, scheduleDate, 0, result);
        } else if (finalGame != null) {
            accountInstance.getSendMessagesHelper().sendMessage(finalGame, dialogId, result.send_message.reply_markup, params, notify, scheduleDate);
        }
    }

    private static String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static void prepareSendingText(final AccountInstance accountInstance, final String text, final long dialogId, final boolean notify, final int scheduleDate) {
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda91
            @Override // java.lang.Runnable
            public final void run() {
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda88
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda77
                            @Override // java.lang.Runnable
                            public final void run() {
                                SendMessagesHelper.lambda$prepareSendingText$81(r1, r2, r3, r5, r6);
                            }
                        });
                    }
                });
            }
        });
    }

    public static /* synthetic */ void lambda$prepareSendingText$81(String text, AccountInstance accountInstance, long dialogId, boolean notify, int scheduleDate) {
        String textFinal = getTrimmedString(text);
        if (textFinal.length() != 0) {
            int count = (int) Math.ceil(textFinal.length() / 4096.0f);
            for (int a = 0; a < count; a++) {
                String mess = textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length()));
                accountInstance.getSendMessagesHelper().sendMessage(mess, dialogId, null, null, null, true, null, null, null, notify, scheduleDate, null);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x00ad, code lost:
        r6.recycle();
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void ensureMediaThumbExists(org.telegram.messenger.AccountInstance r23, boolean r24, org.telegram.tgnet.TLObject r25, java.lang.String r26, android.net.Uri r27, long r28) {
        /*
            Method dump skipped, instructions count: 292
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.ensureMediaThumbExists(org.telegram.messenger.AccountInstance, boolean, org.telegram.tgnet.TLObject, java.lang.String, android.net.Uri, long):void");
    }

    public static String getKeyForPhotoSize(AccountInstance accountInstance, TLRPC.PhotoSize photoSize, Bitmap[] bitmap, boolean blur, boolean forceCache) {
        if (photoSize != null && photoSize.location != null) {
            Point point = ChatMessageCell.getMessageSize(photoSize.w, photoSize.h);
            if (bitmap != null) {
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    try {
                        File file = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(photoSize, forceCache);
                        FileInputStream is = new FileInputStream(file);
                        BitmapFactory.decodeStream(is, null, opts);
                        is.close();
                        float photoW = opts.outWidth;
                        float photoH = opts.outHeight;
                        float scaleFactor = Math.max(photoW / point.x, photoH / point.y);
                        if (scaleFactor < 1.0f) {
                            scaleFactor = 1.0f;
                        }
                        opts.inJustDecodeBounds = false;
                        opts.inSampleSize = (int) scaleFactor;
                        opts.inPreferredConfig = Bitmap.Config.RGB_565;
                        if (Build.VERSION.SDK_INT >= 21) {
                            FileInputStream is2 = new FileInputStream(file);
                            bitmap[0] = BitmapFactory.decodeStream(is2, null, opts);
                            is2.close();
                        }
                    } catch (Throwable th) {
                    }
                } catch (Throwable th2) {
                }
            }
            return String.format(Locale.US, blur ? "%d_%d@%d_%d_b" : "%d_%d@%d_%d", Long.valueOf(photoSize.location.volume_id), Integer.valueOf(photoSize.location.local_id), Integer.valueOf((int) (point.x / AndroidUtilities.density)), Integer.valueOf((int) (point.y / AndroidUtilities.density)));
        }
        return null;
    }

    public static boolean shouldSendWebPAsSticker(String path, Uri uri) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (path != null) {
            try {
                RandomAccessFile file = new RandomAccessFile(path, "r");
                ByteBuffer buffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, path.length());
                Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                file.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else {
            try {
                InputStream inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, null, bmOptions);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e2) {
            }
        }
        return bmOptions.outWidth < 800 && bmOptions.outHeight < 800;
    }

    public static void prepareSendingMedia(final AccountInstance accountInstance, final ArrayList<SendingMediaInfo> media, final long dialogId, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final InputContentInfoCompat inputContent, final boolean forceDocument, boolean groupMedia, final MessageObject editingMessageObject, final boolean notify, final int scheduleDate) {
        boolean groupMedia2;
        if (media.isEmpty()) {
            return;
        }
        int a = 0;
        int N = media.size();
        while (true) {
            if (a >= N) {
                groupMedia2 = groupMedia;
                break;
            } else if (media.get(a).ttl <= 0) {
                a++;
            } else {
                groupMedia2 = false;
                break;
            }
        }
        final boolean groupMediaFinal = groupMedia2;
        mediaSendQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingMedia$89(media, dialogId, forceDocument, groupMediaFinal, accountInstance, editingMessageObject, replyToMsg, replyToTopMsg, notify, scheduleDate, inputContent);
            }
        });
    }

    /* JADX WARN: Can't wrap try/catch for region: R(14:389|(1:394)(1:393)|395|(4:397|(2:400|398)|704|401)|402|(1:404)|(1:406)|(4:676|408|409|(4:411|(3:425|(2:427|(1:429))(1:430)|431)(1:432)|433|702))(1:414)|674|415|(3:417|662|418)(1:420)|(0)(0)|433|702) */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0065, code lost:
        if (r7 != false) goto L27;
     */
    /* JADX WARN: Code restructure failed: missing block: B:421:0x0a7b, code lost:
        r0 = e;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:143:0x036e A[Catch: Exception -> 0x035d, TRY_ENTER, TRY_LEAVE, TryCatch #12 {Exception -> 0x035d, blocks: (B:135:0x0355, B:143:0x036e), top: B:672:0x0355 }] */
    /* JADX WARN: Removed duplicated region for block: B:145:0x0379 A[Catch: Exception -> 0x03b2, TRY_ENTER, TRY_LEAVE, TryCatch #9 {Exception -> 0x03b2, blocks: (B:141:0x0364, B:145:0x0379), top: B:666:0x0364 }] */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0387  */
    /* JADX WARN: Removed duplicated region for block: B:159:0x03ab  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x03c5  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x03f0  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x040c  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x0411  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x041e  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x0427  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x07ab  */
    /* JADX WARN: Removed duplicated region for block: B:321:0x0832  */
    /* JADX WARN: Removed duplicated region for block: B:324:0x0837  */
    /* JADX WARN: Removed duplicated region for block: B:325:0x0841  */
    /* JADX WARN: Removed duplicated region for block: B:328:0x084b  */
    /* JADX WARN: Removed duplicated region for block: B:333:0x08a3  */
    /* JADX WARN: Removed duplicated region for block: B:425:0x0a84  */
    /* JADX WARN: Removed duplicated region for block: B:432:0x0ab6  */
    /* JADX WARN: Removed duplicated region for block: B:506:0x0d63  */
    /* JADX WARN: Removed duplicated region for block: B:564:0x0e84  */
    /* JADX WARN: Removed duplicated region for block: B:566:0x0e95  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:570:0x0ea3  */
    /* JADX WARN: Removed duplicated region for block: B:575:0x0eb4  */
    /* JADX WARN: Removed duplicated region for block: B:577:0x0ec0  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x010a  */
    /* JADX WARN: Removed duplicated region for block: B:584:0x0f10  */
    /* JADX WARN: Removed duplicated region for block: B:586:0x0f15  */
    /* JADX WARN: Removed duplicated region for block: B:588:0x0f1a A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:598:0x0f5e  */
    /* JADX WARN: Removed duplicated region for block: B:605:0x0f95 A[LOOP:4: B:603:0x0f8d->B:605:0x0f95, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:705:0x0eb2 A[EDGE_INSN: B:705:0x0eb2->B:574:0x0eb2 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01af  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01b4  */
    /* JADX WARN: Type inference failed for: r7v22 */
    /* JADX WARN: Type inference failed for: r7v23 */
    /* JADX WARN: Type inference failed for: r8v0 */
    /* JADX WARN: Type inference failed for: r8v58, types: [boolean] */
    /* JADX WARN: Type inference failed for: r8v59 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingMedia$89(java.util.ArrayList r75, final long r76, boolean r78, boolean r79, final org.telegram.messenger.AccountInstance r80, final org.telegram.messenger.MessageObject r81, final org.telegram.messenger.MessageObject r82, final org.telegram.messenger.MessageObject r83, final boolean r84, final int r85, androidx.core.view.inputmethod.InputContentInfoCompat r86) {
        /*
            Method dump skipped, instructions count: 4510
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$89(java.util.ArrayList, long, boolean, boolean, org.telegram.messenger.AccountInstance, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$84(MediaSendPrepareWorker worker, AccountInstance accountInstance, SendingMediaInfo info, boolean isEncrypted) {
        worker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(info.path, info.uri);
        if (isEncrypted && info.canDeleteAfter) {
            new File(info.path).delete();
        }
        worker.sync.countDown();
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$85(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_document documentFinal, String pathFinal, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, SendingMediaInfo info, boolean notify, int scheduleDate) {
        if (editingMessageObject == null) {
            accountInstance.getSendMessagesHelper().sendMessage(documentFinal, null, pathFinal, dialogId, replyToMsg, replyToTopMsg, info.caption, info.entities, null, params, notify, scheduleDate, 0, parentFinal, null);
        } else {
            accountInstance.getSendMessagesHelper().editMessage(editingMessageObject, null, null, documentFinal, pathFinal, params, false, parentFinal);
        }
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$86(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_photo photoFinal, boolean needDownloadHttpFinal, SendingMediaInfo info, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, boolean notify, int scheduleDate) {
        String str = null;
        if (editingMessageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (needDownloadHttpFinal) {
                str = info.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessage(editingMessageObject, photoFinal, null, null, str, params, false, parentFinal);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (needDownloadHttpFinal) {
            str = info.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(photoFinal, str, dialogId, replyToMsg, replyToTopMsg, info.caption, info.entities, null, params, notify, scheduleDate, info.ttl, parentFinal);
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$87(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document videoFinal, String finalPath, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, SendingMediaInfo info, boolean notify, int scheduleDate) {
        if (thumbFinal != null && thumbKeyFinal != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal, false);
        }
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(editingMessageObject, null, videoEditedInfo, videoFinal, finalPath, params, false, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(videoFinal, videoEditedInfo, finalPath, dialogId, replyToMsg, replyToTopMsg, info.caption, info.entities, null, params, notify, scheduleDate, info.ttl, parentFinal, null);
        }
    }

    public static /* synthetic */ void lambda$prepareSendingMedia$88(Bitmap[] bitmapFinal, String[] keyFinal, MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_photo photoFinal, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, SendingMediaInfo info, boolean notify, int scheduleDate) {
        if (bitmapFinal[0] != null && keyFinal[0] != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapFinal[0]), keyFinal[0], false);
        }
        if (editingMessageObject == null) {
            accountInstance.getSendMessagesHelper().sendMessage(photoFinal, null, dialogId, replyToMsg, replyToTopMsg, info.caption, info.entities, null, params, notify, scheduleDate, info.ttl, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().editMessage(editingMessageObject, photoFinal, null, null, null, params, false, parentFinal);
        }
    }

    private static void fillVideoAttribute(String videoPath, TLRPC.TL_documentAttributeVideo attributeVideo, VideoEditedInfo videoEditedInfo) {
        String rotation;
        boolean infoObtained = false;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            try {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                    mediaMetadataRetriever2.setDataSource(videoPath);
                    String width = mediaMetadataRetriever2.extractMetadata(18);
                    if (width != null) {
                        attributeVideo.w = Integer.parseInt(width);
                    }
                    String height = mediaMetadataRetriever2.extractMetadata(19);
                    if (height != null) {
                        attributeVideo.h = Integer.parseInt(height);
                    }
                    String duration = mediaMetadataRetriever2.extractMetadata(9);
                    if (duration != null) {
                        attributeVideo.duration = (int) Math.ceil(((float) Long.parseLong(duration)) / 1000.0f);
                    }
                    if (Build.VERSION.SDK_INT >= 17 && (rotation = mediaMetadataRetriever2.extractMetadata(24)) != null) {
                        int val = Utilities.parseInt((CharSequence) rotation).intValue();
                        if (videoEditedInfo != null) {
                            videoEditedInfo.rotationValue = val;
                        } else if (val == 90 || val == 270) {
                            int temp = attributeVideo.w;
                            attributeVideo.w = attributeVideo.h;
                            attributeVideo.h = temp;
                        }
                    }
                    infoObtained = true;
                    mediaMetadataRetriever2.release();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } catch (Exception e2) {
                FileLog.e(e2);
                if (0 != 0) {
                    mediaMetadataRetriever.release();
                }
            }
            if (!infoObtained) {
                try {
                    MediaPlayer mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(videoPath)));
                    if (mp != null) {
                        attributeVideo.duration = (int) Math.ceil(mp.getDuration() / 1000.0f);
                        attributeVideo.w = mp.getVideoWidth();
                        attributeVideo.h = mp.getVideoHeight();
                        mp.release();
                    }
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
            throw th;
        }
    }

    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        float size;
        if (kind == 2) {
            size = 1920.0f;
        } else if (kind == 3) {
            size = 96.0f;
        } else {
            size = 512.0f;
        }
        Bitmap bitmap = createVideoThumbnailAtTime(filePath, 0L);
        if (bitmap != null) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            if (w > size || h > size) {
                float scale = Math.max(w, h) / size;
                return Bitmap.createScaledBitmap(bitmap, (int) (w / scale), (int) (h / scale), true);
            }
            return bitmap;
        }
        return bitmap;
    }

    public static Bitmap createVideoThumbnailAtTime(String filePath, long time) {
        return createVideoThumbnailAtTime(filePath, time, null, false);
    }

    public static Bitmap createVideoThumbnailAtTime(String filePath, long time, int[] orientation, boolean precise) {
        Bitmap bitmap = null;
        if (precise) {
            AnimatedFileDrawable fileDrawable = new AnimatedFileDrawable(new File(filePath), true, 0L, null, null, null, 0L, 0, true);
            bitmap = fileDrawable.getFrameAtTime(time, precise);
            if (orientation != null) {
                orientation[0] = fileDrawable.getOrientation();
            }
            fileDrawable.recycle();
            if (bitmap == null) {
                return createVideoThumbnailAtTime(filePath, time, orientation, false);
            }
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                try {
                    retriever.setDataSource(filePath);
                    bitmap = retriever.getFrameAtTime(time, 1);
                    if (bitmap == null) {
                        bitmap = retriever.getFrameAtTime(time, 3);
                    }
                    retriever.release();
                } catch (RuntimeException e) {
                }
            } catch (Exception e2) {
                retriever.release();
            } catch (Throwable th) {
                try {
                    retriever.release();
                } catch (RuntimeException e3) {
                }
                throw th;
            }
        }
        return bitmap;
    }

    private static VideoEditedInfo createCompressionSettings(String videoPath) {
        int compressionsCount;
        float maxSize;
        int[] params = new int[11];
        AnimatedFileDrawable.getVideoInfo(videoPath, params);
        if (params[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't avc1 atom");
            }
            return null;
        }
        int originalBitrate = MediaController.getVideoBitrate(videoPath);
        if (originalBitrate == -1) {
            originalBitrate = params[3];
        }
        int bitrate = originalBitrate;
        float videoDuration = params[4];
        long j = params[6];
        long audioFramesSize = params[5];
        int videoFramerate = params[7];
        if (Build.VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo codecInfo = MediaController.selectCodec("video/avc");
                if (codecInfo == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("no codec info for video/avc");
                    }
                    return null;
                }
                String name = codecInfo.getName();
                if (!name.equals("OMX.google.h264.encoder") && !name.equals("OMX.ST.VFM.H264Enc") && !name.equals("OMX.Exynos.avc.enc") && !name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") && !name.equals("OMX.MARVELL.VIDEO.H264ENCODER") && !name.equals("OMX.k3.video.encoder.avc") && !name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                    if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
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
            } catch (Exception e) {
                return null;
            }
        }
        VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
        videoEditedInfo.startTime = -1L;
        videoEditedInfo.endTime = -1L;
        videoEditedInfo.bitrate = bitrate;
        videoEditedInfo.originalPath = videoPath;
        videoEditedInfo.framerate = videoFramerate;
        videoEditedInfo.estimatedDuration = (long) Math.ceil(videoDuration);
        int i = params[1];
        videoEditedInfo.originalWidth = i;
        videoEditedInfo.resultWidth = i;
        int i2 = params[2];
        videoEditedInfo.originalHeight = i2;
        videoEditedInfo.resultHeight = i2;
        videoEditedInfo.rotationValue = params[8];
        videoEditedInfo.originalDuration = videoDuration * 1000.0f;
        float maxSize2 = Math.max(videoEditedInfo.originalWidth, videoEditedInfo.originalHeight);
        if (maxSize2 > 1280.0f) {
            compressionsCount = 4;
        } else if (maxSize2 > 854.0f) {
            compressionsCount = 3;
        } else if (maxSize2 > 640.0f) {
            compressionsCount = 2;
        } else {
            compressionsCount = 1;
        }
        int selectedCompression = Math.round(DownloadController.getInstance(UserConfig.selectedAccount).getMaxVideoBitrate() / (100.0f / compressionsCount));
        if (selectedCompression > compressionsCount) {
            selectedCompression = compressionsCount;
        }
        boolean needCompress = false;
        if (selectedCompression != compressionsCount || Math.max(videoEditedInfo.originalWidth, videoEditedInfo.originalHeight) > 1280) {
            needCompress = true;
            switch (selectedCompression) {
                case 1:
                    maxSize = 432.0f;
                    break;
                case 2:
                    maxSize = 640.0f;
                    break;
                case 3:
                    maxSize = 848.0f;
                    break;
                default:
                    maxSize = 1280.0f;
                    break;
            }
            float scale = maxSize / (videoEditedInfo.originalWidth > videoEditedInfo.originalHeight ? videoEditedInfo.originalWidth : videoEditedInfo.originalHeight);
            videoEditedInfo.resultWidth = Math.round((videoEditedInfo.originalWidth * scale) / 2.0f) * 2;
            videoEditedInfo.resultHeight = Math.round((videoEditedInfo.originalHeight * scale) / 2.0f) * 2;
        }
        int i3 = videoEditedInfo.originalHeight;
        int i4 = videoEditedInfo.originalWidth;
        int i5 = videoEditedInfo.resultHeight;
        int bitrate2 = videoEditedInfo.resultWidth;
        int bitrate3 = MediaController.makeVideoBitrate(i3, i4, originalBitrate, i5, bitrate2);
        if (!needCompress) {
            videoEditedInfo.resultWidth = videoEditedInfo.originalWidth;
            videoEditedInfo.resultHeight = videoEditedInfo.originalHeight;
            videoEditedInfo.bitrate = bitrate3;
        } else {
            videoEditedInfo.bitrate = bitrate3;
        }
        videoEditedInfo.estimatedSize = (int) (((float) audioFramesSize) + (((videoDuration / 1000.0f) * bitrate3) / 8.0f));
        if (videoEditedInfo.estimatedSize == 0) {
            videoEditedInfo.estimatedSize = 1L;
        }
        return videoEditedInfo;
    }

    public static void prepareSendingVideo(final AccountInstance accountInstance, final String videoPath, final VideoEditedInfo info, final long dialogId, final MessageObject replyToMsg, final MessageObject replyToTopMsg, final CharSequence caption, final ArrayList<TLRPC.MessageEntity> entities, final int ttl, final MessageObject editingMessageObject, final boolean notify, final int scheduleDate, final boolean forceDocument) {
        if (videoPath == null || videoPath.length() == 0) {
            return;
        }
        new Thread(new Runnable() { // from class: org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda70
            @Override // java.lang.Runnable
            public final void run() {
                SendMessagesHelper.lambda$prepareSendingVideo$91(VideoEditedInfo.this, videoPath, dialogId, ttl, accountInstance, caption, editingMessageObject, replyToMsg, replyToTopMsg, entities, notify, scheduleDate, forceDocument);
            }
        }).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:133:0x036e  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x03c4  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x03ca  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x03cd  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x03d4  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0154  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo r35, java.lang.String r36, final long r37, final int r39, final org.telegram.messenger.AccountInstance r40, java.lang.CharSequence r41, final org.telegram.messenger.MessageObject r42, final org.telegram.messenger.MessageObject r43, final org.telegram.messenger.MessageObject r44, final java.util.ArrayList r45, final boolean r46, final int r47, boolean r48) {
        /*
            Method dump skipped, instructions count: 1031
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, int, org.telegram.messenger.AccountInstance, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int, boolean):void");
    }

    public static /* synthetic */ void lambda$prepareSendingVideo$90(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document videoFinal, String finalPath, HashMap params, String parentFinal, long dialogId, MessageObject replyToMsg, MessageObject replyToTopMsg, String captionFinal, ArrayList entities, boolean notify, int scheduleDate, int ttl) {
        if (thumbFinal != null && thumbKeyFinal != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal, false);
        }
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(editingMessageObject, null, videoEditedInfo, videoFinal, finalPath, params, false, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(videoFinal, videoEditedInfo, finalPath, dialogId, replyToMsg, replyToTopMsg, captionFinal, entities, null, params, notify, scheduleDate, ttl, parentFinal, null);
        }
    }
}
