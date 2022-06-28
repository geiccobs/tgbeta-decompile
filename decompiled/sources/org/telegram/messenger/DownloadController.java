package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.Pair;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.microsoft.appcenter.distribute.DistributeConstants;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class DownloadController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static final int AUTODOWNLOAD_TYPE_AUDIO = 2;
    public static final int AUTODOWNLOAD_TYPE_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_TYPE_PHOTO = 1;
    public static final int AUTODOWNLOAD_TYPE_VIDEO = 4;
    private static volatile DownloadController[] Instance = new DownloadController[4];
    public static final int PRESET_NUM_CHANNEL = 3;
    public static final int PRESET_NUM_CONTACT = 0;
    public static final int PRESET_NUM_GROUP = 2;
    public static final int PRESET_NUM_PM = 1;
    public static final int PRESET_SIZE_NUM_AUDIO = 3;
    public static final int PRESET_SIZE_NUM_DOCUMENT = 2;
    public static final int PRESET_SIZE_NUM_PHOTO = 0;
    public static final int PRESET_SIZE_NUM_VIDEO = 1;
    public int currentMobilePreset;
    public int currentRoamingPreset;
    public int currentWifiPreset;
    public Preset highPreset;
    private boolean loadingAutoDownloadConfig;
    public Preset lowPreset;
    public Preset mediumPreset;
    public Preset mobilePreset;
    public Preset roamingPreset;
    public Preset wifiPreset;
    private int lastCheckMask = 0;
    private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList<>();
    private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList<>();
    private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList<>();
    private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList<>();
    private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap<>();
    private HashMap<Pair<Long, Integer>, DownloadObject> downloadQueuePairs = new HashMap<>();
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap<>();
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap<>();
    private SparseArray<String> observersByTag = new SparseArray<>();
    private boolean listenerInProgress = false;
    private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap<>();
    private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList<>();
    private int lastTag = 0;
    private LongSparseArray<Long> typingTimes = new LongSparseArray<>();
    public final ArrayList<MessageObject> downloadingFiles = new ArrayList<>();
    public final ArrayList<MessageObject> recentDownloadingFiles = new ArrayList<>();
    public final SparseArray<MessageObject> unviewedDownloads = new SparseArray<>();
    Runnable clearUnviewedDownloadsRunnale = new Runnable() { // from class: org.telegram.messenger.DownloadController.2
        @Override // java.lang.Runnable
        public void run() {
            DownloadController.this.clearUnviewedDownloads();
            DownloadController.this.getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        }
    };

    /* loaded from: classes4.dex */
    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str, boolean z);

        void onProgressDownload(String str, long j, long j2);

        void onProgressUpload(String str, long j, long j2, boolean z);

        void onSuccessDownload(String str);
    }

    /* loaded from: classes4.dex */
    public static class Preset {
        public boolean enabled;
        public boolean lessCallData;
        public int[] mask;
        public int maxVideoBitrate;
        public boolean preloadMusic;
        public boolean preloadVideo;
        public long[] sizes;

        public Preset(int[] m, long p, long v, long f, boolean pv, boolean pm, boolean e, boolean l, int bitrate) {
            int[] iArr = new int[4];
            this.mask = iArr;
            this.sizes = new long[4];
            System.arraycopy(m, 0, iArr, 0, iArr.length);
            long[] jArr = this.sizes;
            jArr[0] = p;
            jArr[1] = v;
            jArr[2] = f;
            jArr[3] = 524288;
            this.preloadVideo = pv;
            this.preloadMusic = pm;
            this.lessCallData = l;
            this.maxVideoBitrate = bitrate;
            this.enabled = e;
        }

        public Preset(String str, String deafultValue) {
            this.mask = new int[4];
            this.sizes = new long[4];
            String[] args = str.split("_");
            String[] defaultArgs = null;
            if (args.length >= 11) {
                boolean z = false;
                this.mask[0] = Utilities.parseInt((CharSequence) args[0]).intValue();
                this.mask[1] = Utilities.parseInt((CharSequence) args[1]).intValue();
                this.mask[2] = Utilities.parseInt((CharSequence) args[2]).intValue();
                this.mask[3] = Utilities.parseInt((CharSequence) args[3]).intValue();
                this.sizes[0] = Utilities.parseInt((CharSequence) args[4]).intValue();
                this.sizes[1] = Utilities.parseInt((CharSequence) args[5]).intValue();
                this.sizes[2] = Utilities.parseInt((CharSequence) args[6]).intValue();
                this.sizes[3] = Utilities.parseInt((CharSequence) args[7]).intValue();
                this.preloadVideo = Utilities.parseInt((CharSequence) args[8]).intValue() == 1;
                this.preloadMusic = Utilities.parseInt((CharSequence) args[9]).intValue() == 1;
                this.enabled = Utilities.parseInt((CharSequence) args[10]).intValue() == 1;
                if (args.length >= 12) {
                    this.lessCallData = Utilities.parseInt((CharSequence) args[11]).intValue() == 1 ? true : z;
                } else {
                    defaultArgs = deafultValue.split("_");
                    this.lessCallData = Utilities.parseInt((CharSequence) defaultArgs[11]).intValue() == 1 ? true : z;
                }
                if (args.length >= 13) {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) args[12]).intValue();
                } else {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) (defaultArgs == null ? deafultValue.split("_") : defaultArgs)[12]).intValue();
                }
            }
        }

        public void set(Preset preset) {
            int[] iArr = preset.mask;
            int[] iArr2 = this.mask;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            long[] jArr = preset.sizes;
            long[] jArr2 = this.sizes;
            System.arraycopy(jArr, 0, jArr2, 0, jArr2.length);
            this.preloadVideo = preset.preloadVideo;
            this.preloadMusic = preset.preloadMusic;
            this.lessCallData = preset.lessCallData;
            this.maxVideoBitrate = preset.maxVideoBitrate;
        }

        public void set(TLRPC.TL_autoDownloadSettings settings) {
            this.preloadMusic = settings.audio_preload_next;
            this.preloadVideo = settings.video_preload_large;
            this.lessCallData = settings.phonecalls_less_data;
            this.maxVideoBitrate = settings.video_upload_maxbitrate;
            this.sizes[0] = Math.max(512000, settings.photo_size_max);
            this.sizes[1] = Math.max(512000L, settings.video_size_max);
            this.sizes[2] = Math.max(512000L, settings.file_size_max);
            for (int a = 0; a < this.mask.length; a++) {
                if (settings.photo_size_max != 0 && !settings.disabled) {
                    int[] iArr = this.mask;
                    iArr[a] = iArr[a] | 1;
                } else {
                    int[] iArr2 = this.mask;
                    iArr2[a] = iArr2[a] & (-2);
                }
                if (settings.video_size_max != 0 && !settings.disabled) {
                    int[] iArr3 = this.mask;
                    iArr3[a] = iArr3[a] | 4;
                } else {
                    int[] iArr4 = this.mask;
                    iArr4[a] = iArr4[a] & (-5);
                }
                if (settings.file_size_max != 0 && !settings.disabled) {
                    int[] iArr5 = this.mask;
                    iArr5[a] = iArr5[a] | 8;
                } else {
                    int[] iArr6 = this.mask;
                    iArr6[a] = iArr6[a] & (-9);
                }
            }
        }

        public String toString() {
            return this.mask[0] + "_" + this.mask[1] + "_" + this.mask[2] + "_" + this.mask[3] + "_" + this.sizes[0] + "_" + this.sizes[1] + "_" + this.sizes[2] + "_" + this.sizes[3] + "_" + (this.preloadVideo ? 1 : 0) + "_" + (this.preloadMusic ? 1 : 0) + "_" + (this.enabled ? 1 : 0) + "_" + (this.lessCallData ? 1 : 0) + "_" + this.maxVideoBitrate;
        }

        public boolean equals(Preset obj) {
            int[] iArr = this.mask;
            int i = iArr[0];
            int[] iArr2 = obj.mask;
            if (i == iArr2[0] && iArr[1] == iArr2[1] && iArr[2] == iArr2[2] && iArr[3] == iArr2[3]) {
                long[] jArr = this.sizes;
                long j = jArr[0];
                long[] jArr2 = obj.sizes;
                return j == jArr2[0] && jArr[1] == jArr2[1] && jArr[2] == jArr2[2] && jArr[3] == jArr2[3] && this.preloadVideo == obj.preloadVideo && this.preloadMusic == obj.preloadMusic && this.maxVideoBitrate == obj.maxVideoBitrate;
            }
            return false;
        }

        public boolean isEnabled() {
            int a = 0;
            while (true) {
                int[] iArr = this.mask;
                if (a < iArr.length) {
                    if (iArr[a] == 0) {
                        a++;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public static DownloadController getInstance(int num) {
        DownloadController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (DownloadController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    DownloadController[] downloadControllerArr = Instance;
                    DownloadController downloadController = new DownloadController(num);
                    localInstance = downloadController;
                    downloadControllerArr[num] = downloadController;
                }
            }
        }
        return localInstance;
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0312  */
    /* JADX WARN: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public DownloadController(int r38) {
        /*
            Method dump skipped, instructions count: 790
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.<init>(int):void");
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m197lambda$new$0$orgtelegrammessengerDownloadController() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        loadAutoDownloadConfig(false);
    }

    public void loadAutoDownloadConfig(boolean force) {
        if (!this.loadingAutoDownloadConfig) {
            if (!force && Math.abs(System.currentTimeMillis() - getUserConfig().autoDownloadConfigLoadTime) < 86400000) {
                return;
            }
            this.loadingAutoDownloadConfig = true;
            TLRPC.TL_account_getAutoDownloadSettings req = new TLRPC.TL_account_getAutoDownloadSettings();
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    DownloadController.this.m194xc6f34c67(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$loadAutoDownloadConfig$2$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m194xc6f34c67(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m193xe1b1dda6(response);
            }
        });
    }

    /* renamed from: lambda$loadAutoDownloadConfig$1$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m193xe1b1dda6(TLObject response) {
        Preset preset;
        this.loadingAutoDownloadConfig = false;
        getUserConfig().autoDownloadConfigLoadTime = System.currentTimeMillis();
        getUserConfig().saveConfig(false);
        if (response != null) {
            TLRPC.TL_account_autoDownloadSettings res = (TLRPC.TL_account_autoDownloadSettings) response;
            this.lowPreset.set(res.low);
            this.mediumPreset.set(res.medium);
            this.highPreset.set(res.high);
            for (int a = 0; a < 3; a++) {
                if (a == 0) {
                    preset = this.mobilePreset;
                } else if (a == 1) {
                    preset = this.wifiPreset;
                } else {
                    preset = this.roamingPreset;
                }
                if (preset.equals(this.lowPreset)) {
                    preset.set(res.low);
                } else if (preset.equals(this.mediumPreset)) {
                    preset.set(res.medium);
                } else if (preset.equals(this.highPreset)) {
                    preset.set(res.high);
                }
            }
            int a2 = this.currentAccount;
            SharedPreferences.Editor editor = MessagesController.getMainSettings(a2).edit();
            editor.putString("mobilePreset", this.mobilePreset.toString());
            editor.putString("wifiPreset", this.wifiPreset.toString());
            editor.putString("roamingPreset", this.roamingPreset.toString());
            editor.putString("preset0", this.lowPreset.toString());
            editor.putString("preset1", this.mediumPreset.toString());
            editor.putString("preset2", this.highPreset.toString());
            editor.commit();
            this.lowPreset.toString();
            this.mediumPreset.toString();
            this.highPreset.toString();
            checkAutodownloadSettings();
        }
    }

    public Preset getCurrentMobilePreset() {
        int i = this.currentMobilePreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.mobilePreset;
    }

    public Preset getCurrentWiFiPreset() {
        int i = this.currentWifiPreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.wifiPreset;
    }

    public Preset getCurrentRoamingPreset() {
        int i = this.currentRoamingPreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.roamingPreset;
    }

    public static int typeToIndex(int type) {
        if (type == 1) {
            return 0;
        }
        if (type == 2) {
            return 2;
        }
        if (type == 4) {
            return 1;
        }
        if (type != 8) {
            return 0;
        }
        return 2;
    }

    public void cleanup() {
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.documentDownloadQueue.clear();
        this.videoDownloadQueue.clear();
        this.downloadQueueKeys.clear();
        this.downloadQueuePairs.clear();
        this.typingTimes.clear();
    }

    public int getMaxVideoBitrate() {
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            return getCurrentWiFiPreset().maxVideoBitrate;
        }
        if (networkType == 2) {
            return getCurrentRoamingPreset().maxVideoBitrate;
        }
        return getCurrentMobilePreset().maxVideoBitrate;
    }

    public int getAutodownloadMask() {
        int[] masksArray;
        int result = 0;
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            masksArray = getCurrentWiFiPreset().mask;
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            masksArray = getCurrentRoamingPreset().mask;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            masksArray = getCurrentMobilePreset().mask;
        }
        for (int a = 0; a < masksArray.length; a++) {
            int mask = 0;
            if ((masksArray[a] & 1) != 0) {
                mask = 0 | 1;
            }
            if ((masksArray[a] & 2) != 0) {
                mask |= 2;
            }
            if ((masksArray[a] & 4) != 0) {
                mask |= 4;
            }
            if ((masksArray[a] & 8) != 0) {
                mask |= 8;
            }
            result |= mask << (a * 8);
        }
        return result;
    }

    protected int getAutodownloadMaskAll() {
        if (!this.mobilePreset.enabled && !this.roamingPreset.enabled && !this.wifiPreset.enabled) {
            return 0;
        }
        int mask = 0;
        for (int a = 0; a < 4; a++) {
            if ((getCurrentMobilePreset().mask[a] & 1) != 0 || (getCurrentWiFiPreset().mask[a] & 1) != 0 || (getCurrentRoamingPreset().mask[a] & 1) != 0) {
                mask |= 1;
            }
            if ((getCurrentMobilePreset().mask[a] & 2) != 0 || (getCurrentWiFiPreset().mask[a] & 2) != 0 || (getCurrentRoamingPreset().mask[a] & 2) != 0) {
                mask |= 2;
            }
            if ((getCurrentMobilePreset().mask[a] & 4) != 0 || (getCurrentWiFiPreset().mask[a] & 4) != 0 || (4 & getCurrentRoamingPreset().mask[a]) != 0) {
                mask |= 4;
            }
            if ((getCurrentMobilePreset().mask[a] & 8) != 0 || (getCurrentWiFiPreset().mask[a] & 8) != 0 || (getCurrentRoamingPreset().mask[a] & 8) != 0) {
                mask |= 8;
            }
        }
        return mask;
    }

    public void checkAutodownloadSettings() {
        int currentMask = getCurrentDownloadMask();
        if (currentMask == this.lastCheckMask) {
            return;
        }
        this.lastCheckMask = currentMask;
        if ((currentMask & 1) != 0) {
            if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
        } else {
            for (int a = 0; a < this.photoDownloadQueue.size(); a++) {
                DownloadObject downloadObject = this.photoDownloadQueue.get(a);
                if (downloadObject.object instanceof TLRPC.Photo) {
                    TLRPC.Photo photo = (TLRPC.Photo) downloadObject.object;
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                    getFileLoader().cancelLoadFile(photoSize);
                } else if (downloadObject.object instanceof TLRPC.Document) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) downloadObject.object);
                }
            }
            this.photoDownloadQueue.clear();
        }
        if ((currentMask & 2) != 0) {
            if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
        } else {
            for (int a2 = 0; a2 < this.audioDownloadQueue.size(); a2++) {
                DownloadObject downloadObject2 = this.audioDownloadQueue.get(a2);
                getFileLoader().cancelLoadFile((TLRPC.Document) downloadObject2.object);
            }
            this.audioDownloadQueue.clear();
        }
        if ((currentMask & 8) != 0) {
            if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
        } else {
            for (int a3 = 0; a3 < this.documentDownloadQueue.size(); a3++) {
                DownloadObject downloadObject3 = this.documentDownloadQueue.get(a3);
                TLRPC.Document document = (TLRPC.Document) downloadObject3.object;
                getFileLoader().cancelLoadFile(document);
            }
            this.documentDownloadQueue.clear();
        }
        if ((currentMask & 4) != 0) {
            if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
        } else {
            for (int a4 = 0; a4 < this.videoDownloadQueue.size(); a4++) {
                DownloadObject downloadObject4 = this.videoDownloadQueue.get(a4);
                getFileLoader().cancelLoadFile((TLRPC.Document) downloadObject4.object);
            }
            this.videoDownloadQueue.clear();
        }
        int mask = getAutodownloadMaskAll();
        if (mask == 0) {
            getMessagesStorage().clearDownloadQueue(0);
            return;
        }
        if ((mask & 1) == 0) {
            getMessagesStorage().clearDownloadQueue(1);
        }
        if ((mask & 2) == 0) {
            getMessagesStorage().clearDownloadQueue(2);
        }
        if ((mask & 4) == 0) {
            getMessagesStorage().clearDownloadQueue(4);
        }
        if ((mask & 8) == 0) {
            getMessagesStorage().clearDownloadQueue(8);
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner) == 1;
    }

    public boolean canDownloadMedia(int type, long size) {
        Preset preset;
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            preset = getCurrentWiFiPreset();
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            preset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return false;
        } else {
            preset = getCurrentMobilePreset();
        }
        int mask = preset.mask[1];
        long maxSize = preset.sizes[typeToIndex(type)];
        if (type == 1 || (size != 0 && size <= maxSize)) {
            return type == 2 || (mask & type) != 0;
        }
        return false;
    }

    public int canDownloadMedia(TLRPC.Message message) {
        int type;
        int index;
        Preset preset;
        long maxSize;
        if (message == null) {
            return 0;
        }
        boolean isVideo = MessageObject.isVideoMessage(message);
        if (isVideo || MessageObject.isGifMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isGameMessage(message)) {
            type = 4;
        } else if (MessageObject.isVoiceMessage(message)) {
            type = 2;
        } else if (MessageObject.isPhoto(message) || MessageObject.isStickerMessage(message) || MessageObject.isAnimatedStickerMessage(message)) {
            type = 1;
        } else if (MessageObject.getDocument(message) == null) {
            return 0;
        } else {
            type = 8;
        }
        TLRPC.Peer peer = message.peer_id;
        if (peer != null) {
            if (peer.user_id == 0) {
                if (peer.chat_id == 0) {
                    TLRPC.Chat chat = message.peer_id.channel_id != 0 ? getMessagesController().getChat(Long.valueOf(message.peer_id.channel_id)) : null;
                    if (ChatObject.isChannel(chat) && chat.megagroup) {
                        if ((message.from_id instanceof TLRPC.TL_peerUser) && getContactsController().contactsDict.containsKey(Long.valueOf(message.from_id.user_id))) {
                            index = 0;
                        } else {
                            index = 2;
                        }
                    } else {
                        index = 3;
                    }
                } else if ((message.from_id instanceof TLRPC.TL_peerUser) && getContactsController().contactsDict.containsKey(Long.valueOf(message.from_id.user_id))) {
                    index = 0;
                } else {
                    index = 2;
                }
            } else if (getContactsController().contactsDict.containsKey(Long.valueOf(peer.user_id))) {
                index = 0;
            } else {
                index = 1;
            }
        } else {
            index = 1;
        }
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            preset = getCurrentWiFiPreset();
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            preset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            preset = getCurrentMobilePreset();
        }
        int mask = preset.mask[index];
        if (type == 2) {
            maxSize = Math.max((long) DistributeConstants.UPDATE_PROGRESS_BYTES_THRESHOLD, preset.sizes[typeToIndex(type)]);
        } else {
            maxSize = preset.sizes[typeToIndex(type)];
        }
        long size = MessageObject.getMessageSize(message);
        return (!isVideo || !preset.preloadVideo || size <= maxSize || maxSize <= 2097152) ? ((type == 1 || (size != 0 && size <= maxSize)) && (type == 2 || (mask & type) != 0)) ? 1 : 0 : (mask & type) != 0 ? 2 : 0;
    }

    public boolean canDownloadNextTrack() {
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        return networkType == 1 ? this.wifiPreset.enabled && getCurrentWiFiPreset().preloadMusic : networkType == 2 ? this.roamingPreset.enabled && getCurrentRoamingPreset().preloadMusic : this.mobilePreset.enabled && getCurrentMobilePreset().preloadMusic;
    }

    public int getCurrentDownloadMask() {
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            int mask = 0;
            for (int a = 0; a < 4; a++) {
                mask |= getCurrentWiFiPreset().mask[a];
            }
            return mask;
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            int mask2 = 0;
            for (int a2 = 0; a2 < 4; a2++) {
                mask2 |= getCurrentRoamingPreset().mask[a2];
            }
            return mask2;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            int mask3 = 0;
            for (int a3 = 0; a3 < 4; a3++) {
                mask3 |= getCurrentMobilePreset().mask[a3];
            }
            return mask3;
        }
    }

    public void savePresetToServer(int type) {
        boolean enabled;
        Preset preset;
        TLRPC.TL_account_saveAutoDownloadSettings req = new TLRPC.TL_account_saveAutoDownloadSettings();
        if (type == 0) {
            preset = getCurrentMobilePreset();
            enabled = this.mobilePreset.enabled;
        } else if (type == 1) {
            preset = getCurrentWiFiPreset();
            enabled = this.wifiPreset.enabled;
        } else {
            preset = getCurrentRoamingPreset();
            enabled = this.roamingPreset.enabled;
        }
        req.settings = new TLRPC.TL_autoDownloadSettings();
        req.settings.audio_preload_next = preset.preloadMusic;
        req.settings.video_preload_large = preset.preloadVideo;
        req.settings.phonecalls_less_data = preset.lessCallData;
        req.settings.video_upload_maxbitrate = preset.maxVideoBitrate;
        req.settings.disabled = !enabled;
        boolean photo = false;
        boolean video = false;
        boolean document = false;
        for (int a = 0; a < preset.mask.length; a++) {
            if ((preset.mask[a] & 1) != 0) {
                photo = true;
            }
            if ((preset.mask[a] & 4) != 0) {
                video = true;
            }
            if ((preset.mask[a] & 8) != 0) {
                document = true;
            }
            if (photo && video && document) {
                break;
            }
        }
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings = req.settings;
        int i = 0;
        if (photo) {
            i = (int) preset.sizes[0];
        }
        tL_autoDownloadSettings.photo_size_max = i;
        long j = 0;
        req.settings.video_size_max = video ? preset.sizes[1] : 0L;
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings2 = req.settings;
        if (document) {
            j = preset.sizes[2];
        }
        tL_autoDownloadSettings2.file_size_max = j;
        getConnectionsManager().sendRequest(req, DownloadController$$ExternalSyntheticLambda4.INSTANCE);
    }

    public static /* synthetic */ void lambda$savePresetToServer$3(TLObject response, TLRPC.TL_error error) {
    }

    public void cancelDownloading(ArrayList<Pair<Long, Integer>> arrayList) {
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            Pair<Long, Integer> pair = arrayList.get(a);
            DownloadObject downloadObject = this.downloadQueuePairs.get(pair);
            if (downloadObject != null) {
                if (downloadObject.object instanceof TLRPC.Document) {
                    TLRPC.Document document = (TLRPC.Document) downloadObject.object;
                    getFileLoader().cancelLoadFile(document, true);
                } else if (downloadObject.object instanceof TLRPC.Photo) {
                    TLRPC.Photo photo = (TLRPC.Photo) downloadObject.object;
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                    if (photoSize != null) {
                        getFileLoader().cancelLoadFile(photoSize, true);
                    }
                }
            }
        }
    }

    public void processDownloadObjects(int type, ArrayList<DownloadObject> objects) {
        ArrayList<DownloadObject> queue;
        String path;
        int cacheType;
        if (objects.isEmpty()) {
            return;
        }
        if (type == 1) {
            queue = this.photoDownloadQueue;
        } else if (type == 2) {
            queue = this.audioDownloadQueue;
        } else if (type == 4) {
            queue = this.videoDownloadQueue;
        } else {
            queue = this.documentDownloadQueue;
        }
        for (int a = 0; a < objects.size(); a++) {
            DownloadObject downloadObject = objects.get(a);
            TLRPC.PhotoSize photoSize = null;
            if (downloadObject.object instanceof TLRPC.Document) {
                TLRPC.Document document = (TLRPC.Document) downloadObject.object;
                path = FileLoader.getAttachFileName(document);
            } else if (downloadObject.object instanceof TLRPC.Photo) {
                TLRPC.Photo photo = (TLRPC.Photo) downloadObject.object;
                photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                path = FileLoader.getAttachFileName(photoSize);
            } else {
                path = null;
            }
            if (path != null && !this.downloadQueueKeys.containsKey(path)) {
                boolean added = true;
                if (photoSize != null) {
                    TLRPC.Photo photo2 = (TLRPC.Photo) downloadObject.object;
                    if (downloadObject.secret) {
                        cacheType = 2;
                    } else if (downloadObject.forceCache) {
                        cacheType = 1;
                    } else {
                        cacheType = 0;
                    }
                    getFileLoader().loadFile(ImageLocation.getForPhoto(photoSize, photo2), downloadObject.parent, null, 0, cacheType);
                } else if (downloadObject.object instanceof TLRPC.Document) {
                    TLRPC.Document document2 = (TLRPC.Document) downloadObject.object;
                    getFileLoader().loadFile(document2, downloadObject.parent, 0, downloadObject.secret ? 2 : 0);
                } else {
                    added = false;
                }
                if (added) {
                    queue.add(downloadObject);
                    this.downloadQueueKeys.put(path, downloadObject);
                    this.downloadQueuePairs.put(new Pair<>(Long.valueOf(downloadObject.id), Integer.valueOf(downloadObject.type)), downloadObject);
                }
            }
        }
    }

    public void newDownloadObjectsAvailable(int downloadMask) {
        int mask = getCurrentDownloadMask();
        if ((mask & 1) != 0 && (downloadMask & 1) != 0 && this.photoDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(1);
        }
        if ((mask & 2) != 0 && (downloadMask & 2) != 0 && this.audioDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(2);
        }
        if ((mask & 4) != 0 && (downloadMask & 4) != 0 && this.videoDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(4);
        }
        if ((mask & 8) != 0 && (downloadMask & 8) != 0 && this.documentDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(8);
        }
    }

    private void checkDownloadFinished(String fileName, int state) {
        DownloadObject downloadObject = this.downloadQueueKeys.get(fileName);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(fileName);
            this.downloadQueuePairs.remove(new Pair(Long.valueOf(downloadObject.id), Integer.valueOf(downloadObject.type)));
            if (state == 0 || state == 2) {
                getMessagesStorage().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type != 1) {
                if (downloadObject.type == 2) {
                    this.audioDownloadQueue.remove(downloadObject);
                    if (this.audioDownloadQueue.isEmpty()) {
                        newDownloadObjectsAvailable(2);
                        return;
                    }
                    return;
                } else if (downloadObject.type == 4) {
                    this.videoDownloadQueue.remove(downloadObject);
                    if (this.videoDownloadQueue.isEmpty()) {
                        newDownloadObjectsAvailable(4);
                        return;
                    }
                    return;
                } else if (downloadObject.type == 8) {
                    this.documentDownloadQueue.remove(downloadObject);
                    if (this.documentDownloadQueue.isEmpty()) {
                        newDownloadObjectsAvailable(8);
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
            this.photoDownloadQueue.remove(downloadObject);
            if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
        }
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + 1;
        return i;
    }

    public void addLoadingFileObserver(String fileName, FileDownloadProgressListener observer) {
        addLoadingFileObserver(fileName, null, observer);
    }

    public void addLoadingFileObserver(String fileName, MessageObject messageObject, FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(fileName, observer);
            return;
        }
        removeLoadingFileObserver(observer);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(fileName);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.loadingFileObservers.put(fileName, arrayList);
        }
        arrayList.add(new WeakReference<>(observer));
        if (messageObject != null) {
            ArrayList<MessageObject> messageObjects = this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects == null) {
                messageObjects = new ArrayList<>();
                this.loadingFileMessagesObservers.put(fileName, messageObjects);
            }
            messageObjects.add(messageObject);
        }
        this.observersByTag.put(observer.getObserverTag(), fileName);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(observer);
            return;
        }
        String fileName = this.observersByTag.get(observer.getObserverTag());
        if (fileName != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int a = 0;
                while (a < arrayList.size()) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() == null || reference.get() == observer) {
                        arrayList.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arrayList.isEmpty()) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.observersByTag.remove(observer.getObserverTag());
        }
    }

    private void processLaterArrays() {
        for (Map.Entry<String, FileDownloadProgressListener> listener : this.addLaterArray.entrySet()) {
            addLoadingFileObserver(listener.getKey(), listener.getValue());
        }
        this.addLaterArray.clear();
        Iterator<FileDownloadProgressListener> it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver(it.next());
        }
        this.deleteLaterArray.clear();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int a;
        if (id == NotificationCenter.fileLoadFailed || id == NotificationCenter.httpFileDidFailedLoad) {
            String fileName = (String) args[0];
            Integer canceled = (Integer) args[1];
            this.listenerInProgress = true;
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int size = arrayList.size();
                for (int a2 = 0; a2 < size; a2++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a2);
                    if (reference.get() != null) {
                        reference.get().onFailedDownload(fileName, canceled.intValue() == 1);
                        if (canceled.intValue() != 1) {
                            this.observersByTag.remove(reference.get().getObserverTag());
                        }
                    }
                }
                int a3 = canceled.intValue();
                if (a3 != 1) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, canceled.intValue());
        } else if (id == NotificationCenter.fileLoaded || id == NotificationCenter.httpFileDidLoad) {
            this.listenerInProgress = true;
            String fileName2 = (String) args[0];
            ArrayList<MessageObject> messageObjects = this.loadingFileMessagesObservers.get(fileName2);
            if (messageObjects != null) {
                int size2 = messageObjects.size();
                for (int a4 = 0; a4 < size2; a4++) {
                    messageObjects.get(a4).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(fileName2);
            }
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList2 = this.loadingFileObservers.get(fileName2);
            if (arrayList2 != null) {
                int size3 = arrayList2.size();
                for (int a5 = 0; a5 < size3; a5++) {
                    WeakReference<FileDownloadProgressListener> reference2 = arrayList2.get(a5);
                    if (reference2.get() != null) {
                        reference2.get().onSuccessDownload(fileName2);
                        this.observersByTag.remove(reference2.get().getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(fileName2);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName2, 0);
        } else if (id == NotificationCenter.fileLoadProgressChanged) {
            this.listenerInProgress = true;
            String fileName3 = (String) args[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList3 = this.loadingFileObservers.get(fileName3);
            if (arrayList3 != null) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                int size4 = arrayList3.size();
                for (int a6 = 0; a6 < size4; a6++) {
                    WeakReference<FileDownloadProgressListener> reference3 = arrayList3.get(a6);
                    if (reference3.get() != null) {
                        reference3.get().onProgressDownload(fileName3, loadedSize.longValue(), totalSize.longValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (id == NotificationCenter.fileUploadProgressChanged) {
            this.listenerInProgress = true;
            String fileName4 = (String) args[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList4 = this.loadingFileObservers.get(fileName4);
            if (arrayList4 != null) {
                Long loadedSize2 = (Long) args[1];
                Long totalSize2 = (Long) args[2];
                Boolean enc = (Boolean) args[3];
                int size5 = arrayList4.size();
                int a7 = 0;
                while (a7 < size5) {
                    WeakReference<FileDownloadProgressListener> reference4 = arrayList4.get(a7);
                    if (reference4.get() == null) {
                        a = a7;
                    } else {
                        a = a7;
                        reference4.get().onProgressUpload(fileName4, loadedSize2.longValue(), totalSize2.longValue(), enc.booleanValue());
                    }
                    a7 = a + 1;
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<SendMessagesHelper.DelayedMessage> delayedMessages = getSendMessagesHelper().getDelayedMessages(fileName4);
                if (delayedMessages != null) {
                    for (int a8 = 0; a8 < delayedMessages.size(); a8++) {
                        SendMessagesHelper.DelayedMessage delayedMessage = delayedMessages.get(a8);
                        if (delayedMessage.encryptedChat == null) {
                            long dialogId = delayedMessage.peer;
                            int topMessageId = delayedMessage.topMessageId;
                            Long lastTime = this.typingTimes.get(dialogId);
                            if (delayedMessage.type == 4) {
                                if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                    MessageObject messageObject = (MessageObject) delayedMessage.extraHashMap.get(fileName4 + "_i");
                                    if (messageObject != null && messageObject.isVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 5, 0);
                                    } else if (messageObject != null && messageObject.getDocument() != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 3, 0);
                                    } else {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 4, 0);
                                    }
                                    this.typingTimes.put(dialogId, Long.valueOf(System.currentTimeMillis()));
                                }
                            } else {
                                delayedMessage.obj.getDocument();
                                if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                    if (delayedMessage.obj.isRoundVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 8, 0);
                                    } else if (delayedMessage.obj.isVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 5, 0);
                                    } else if (delayedMessage.obj.isVoice()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 9, 0);
                                    } else if (delayedMessage.obj.getDocument() != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 3, 0);
                                    } else if (delayedMessage.photoSize != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 4, 0);
                                    }
                                    this.typingTimes.put(dialogId, Long.valueOf(System.currentTimeMillis()));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static float getProgress(long[] progressSizes) {
        if (progressSizes == null || progressSizes.length < 2 || progressSizes[1] == 0) {
            return 0.0f;
        }
        return Math.min(1.0f, ((float) progressSizes[0]) / ((float) progressSizes[1]));
    }

    public void startDownloadFile(TLRPC.Document document, final MessageObject parentObject) {
        if (parentObject.getDocument() == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m203x71f65eb7(parentObject);
            }
        });
    }

    /* renamed from: lambda$startDownloadFile$5$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m203x71f65eb7(final MessageObject parentObject) {
        boolean contains = false;
        int i = 0;
        while (true) {
            if (i < this.recentDownloadingFiles.size()) {
                if (this.recentDownloadingFiles.get(i).getDocument() == null || this.recentDownloadingFiles.get(i).getDocument().id != parentObject.getDocument().id) {
                    i++;
                } else {
                    contains = true;
                    break;
                }
            } else {
                break;
            }
        }
        if (!contains) {
            int i2 = 0;
            while (true) {
                if (i2 < this.downloadingFiles.size()) {
                    if (this.downloadingFiles.get(i2).getDocument() == null || this.downloadingFiles.get(i2).getDocument().id != parentObject.getDocument().id) {
                        i2++;
                    } else {
                        contains = true;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (!contains) {
            this.downloadingFiles.add(parentObject);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    DownloadController.this.m202x8cb4eff6(parentObject);
                }
            });
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    /* renamed from: lambda$startDownloadFile$4$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m202x8cb4eff6(MessageObject parentObject) {
        try {
            NativeByteBuffer data = new NativeByteBuffer(parentObject.messageOwner.getObjectSize());
            parentObject.messageOwner.serializeToStream(data);
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO downloading_documents VALUES(?, ?, ?, ?, ?)");
            state.bindByteBuffer(1, data);
            state.bindInteger(2, parentObject.getDocument().dc_id);
            state.bindLong(3, parentObject.getDocument().id);
            state.bindLong(4, System.currentTimeMillis());
            state.bindInteger(4, 0);
            state.step();
            state.dispose();
            data.reuse();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onDownloadComplete(final MessageObject parentObject) {
        if (parentObject == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m199x872e680d(parentObject);
            }
        });
    }

    /* renamed from: lambda$onDownloadComplete$7$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m199x872e680d(final MessageObject parentObject) {
        boolean removed = false;
        int i = 0;
        while (true) {
            if (i >= this.downloadingFiles.size()) {
                break;
            } else if (this.downloadingFiles.get(i).getDocument().id != parentObject.getDocument().id) {
                i++;
            } else {
                this.downloadingFiles.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            boolean contains = false;
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentDownloadingFiles.size()) {
                    break;
                } else if (this.recentDownloadingFiles.get(i2).getDocument().id != parentObject.getDocument().id) {
                    i2++;
                } else {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                this.recentDownloadingFiles.add(0, parentObject);
                putToUnviewedDownloads(parentObject);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    DownloadController.this.m198xa1ecf94c(parentObject);
                }
            });
        }
    }

    /* renamed from: lambda$onDownloadComplete$6$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m198xa1ecf94c(MessageObject parentObject) {
        try {
            String req = String.format(Locale.ENGLISH, "UPDATE downloading_documents SET state = 1, date = %d WHERE hash = %d AND id = %d", Long.valueOf(System.currentTimeMillis()), Integer.valueOf(parentObject.getDocument().dc_id), Long.valueOf(parentObject.getDocument().id));
            getMessagesStorage().getDatabase().executeFast(req).stepThis().dispose();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT COUNT(*) FROM downloading_documents WHERE state = 1", new Object[0]);
            int count = 0;
            if (cursor.next()) {
                count = cursor.intValue(0);
            }
            cursor.dispose();
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized("SELECT state FROM downloading_documents WHERE state = 1", new Object[0]);
            if (cursor2.next()) {
                cursor2.intValue(0);
            }
            cursor2.dispose();
            if (count > 100) {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                SQLiteCursor cursor3 = database.queryFinalized("SELECT hash, id FROM downloading_documents WHERE state = 1 ORDER BY date ASC LIMIT " + (100 - count), new Object[0]);
                ArrayList<DownloadingDocumentEntry> entriesToRemove = new ArrayList<>();
                while (cursor3.next()) {
                    DownloadingDocumentEntry entry = new DownloadingDocumentEntry();
                    entry.hash = cursor3.intValue(0);
                    entry.id = cursor3.longValue(1);
                    entriesToRemove.add(entry);
                }
                cursor3.dispose();
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
                for (int i = 0; i < entriesToRemove.size(); i++) {
                    state.requery();
                    state.bindInteger(1, entriesToRemove.get(i).hash);
                    state.bindLong(2, entriesToRemove.get(i).id);
                    state.step();
                }
                state.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onDownloadFail(final MessageObject parentObject, final int reason) {
        if (parentObject == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m200x867eb33(parentObject, reason);
            }
        });
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m201xeda959f4(parentObject);
            }
        });
    }

    /* renamed from: lambda$onDownloadFail$8$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m200x867eb33(MessageObject parentObject, int reason) {
        boolean removed = false;
        int i = 0;
        while (true) {
            if (i >= this.downloadingFiles.size()) {
                break;
            } else if (this.downloadingFiles.get(i).getDocument().id != parentObject.getDocument().id) {
                i++;
            } else {
                this.downloadingFiles.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            if (reason == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("MessageNotFound", org.telegram.messenger.beta.R.string.MessageNotFound, new Object[0]));
            }
        }
    }

    /* renamed from: lambda$onDownloadFail$9$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m201xeda959f4(MessageObject parentObject) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            state.bindInteger(1, parentObject.getDocument().dc_id);
            state.bindLong(2, parentObject.getDocument().id);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void putToUnviewedDownloads(MessageObject parentObject) {
        this.unviewedDownloads.put(parentObject.getId(), parentObject);
        AndroidUtilities.cancelRunOnUIThread(this.clearUnviewedDownloadsRunnale);
        AndroidUtilities.runOnUIThread(this.clearUnviewedDownloadsRunnale, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
    }

    public void clearUnviewedDownloads() {
        this.unviewedDownloads.clear();
    }

    public void checkUnviewedDownloads(int messageId, long dialogId) {
        MessageObject messageObject = this.unviewedDownloads.get(messageId);
        if (messageObject != null && messageObject.getDialogId() == dialogId) {
            this.unviewedDownloads.remove(messageId);
            if (this.unviewedDownloads.size() == 0) {
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            }
        }
    }

    public boolean hasUnviewedDownloads() {
        return this.unviewedDownloads.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class DownloadingDocumentEntry {
        int hash;
        long id;

        private DownloadingDocumentEntry() {
            DownloadController.this = r1;
        }
    }

    public void loadDownloadingFiles() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m196x5c0ef21();
            }
        });
    }

    /* renamed from: lambda$loadDownloadingFiles$11$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m196x5c0ef21() {
        final ArrayList<MessageObject> downloadingMessages = new ArrayList<>();
        final ArrayList<MessageObject> recentlyDownloadedMessages = new ArrayList<>();
        ArrayList<MessageObject> newMessages = new ArrayList<>();
        try {
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized("SELECT data, state FROM downloading_documents ORDER BY date DESC", new Object[0]);
            while (cursor2.next()) {
                NativeByteBuffer data = cursor2.byteBufferValue(0);
                int state = cursor2.intValue(1);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    if (message != null) {
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        MessageObject messageObject = new MessageObject(this.currentAccount, message, false, false);
                        newMessages.add(messageObject);
                        if (state == 0) {
                            downloadingMessages.add(messageObject);
                        } else {
                            recentlyDownloadedMessages.add(messageObject);
                        }
                    }
                    data.reuse();
                }
            }
            cursor2.dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
        getFileLoader().checkMediaExistance(downloadingMessages);
        getFileLoader().checkMediaExistance(recentlyDownloadedMessages);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m195x207f8060(downloadingMessages, recentlyDownloadedMessages);
            }
        });
    }

    /* renamed from: lambda$loadDownloadingFiles$10$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m195x207f8060(ArrayList downloadingMessages, ArrayList recentlyDownloadedMessages) {
        this.downloadingFiles.clear();
        this.downloadingFiles.addAll(downloadingMessages);
        this.recentDownloadingFiles.clear();
        this.recentDownloadingFiles.addAll(recentlyDownloadedMessages);
    }

    public void clearRecentDownloadedFiles() {
        this.recentDownloadingFiles.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m191x5e3299d();
            }
        });
    }

    /* renamed from: lambda$clearRecentDownloadedFiles$12$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m191x5e3299d() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE state = 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void deleteRecentFiles(final ArrayList<MessageObject> messageObjects) {
        for (int i = 0; i < messageObjects.size(); i++) {
            boolean found = false;
            int j = 0;
            while (true) {
                if (j >= this.recentDownloadingFiles.size()) {
                    break;
                } else if (messageObjects.get(i).getId() != this.recentDownloadingFiles.get(j).getId() || this.recentDownloadingFiles.get(j).getDialogId() != messageObjects.get(i).getDialogId()) {
                    j++;
                } else {
                    this.recentDownloadingFiles.remove(j);
                    found = true;
                    break;
                }
            }
            if (!found) {
                int j2 = 0;
                while (true) {
                    if (j2 >= this.downloadingFiles.size()) {
                        break;
                    } else if (messageObjects.get(i).getId() != this.downloadingFiles.get(j2).getId() || this.downloadingFiles.get(j2).getDialogId() != messageObjects.get(i).getDialogId()) {
                        j2++;
                    } else {
                        this.downloadingFiles.remove(j2);
                        break;
                    }
                }
            }
            messageObjects.get(i).putInDownloadsStore = false;
            FileLoader.getInstance(this.currentAccount).loadFile(messageObjects.get(i).getDocument(), messageObjects.get(i), 0, 0);
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(messageObjects.get(i).getDocument(), true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.messenger.DownloadController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DownloadController.this.m192x7bb1c48f(messageObjects);
            }
        });
    }

    /* renamed from: lambda$deleteRecentFiles$13$org-telegram-messenger-DownloadController */
    public /* synthetic */ void m192x7bb1c48f(ArrayList messageObjects) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            for (int i = 0; i < messageObjects.size(); i++) {
                state.requery();
                state.bindInteger(1, ((MessageObject) messageObjects.get(i)).getDocument().dc_id);
                state.bindLong(2, ((MessageObject) messageObjects.get(i)).getDocument().id);
                state.step();
                try {
                    File file = FileLoader.getInstance(this.currentAccount).getPathToMessage(((MessageObject) messageObjects.get(i)).messageOwner);
                    file.delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            state.dispose();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }
}
