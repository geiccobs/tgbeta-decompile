package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.webkit.WebView;
import androidx.core.content.pm.ShortcutManagerCompat;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.audio.SilenceSkippingAudioProcessor;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class SharedConfig {
    public static final int PASSCODE_TYPE_PASSWORD = 1;
    public static final int PASSCODE_TYPE_PIN = 0;
    public static final int PERFORMANCE_CLASS_AVERAGE = 1;
    public static final int PERFORMANCE_CLASS_HIGH = 2;
    public static final int PERFORMANCE_CLASS_LOW = 0;
    public static final int SAVE_TO_GALLERY_FLAG_CHANNELS = 4;
    public static final int SAVE_TO_GALLERY_FLAG_GROUP = 2;
    public static final int SAVE_TO_GALLERY_FLAG_PEER = 1;
    public static boolean allowBigEmoji;
    public static boolean allowScreenCapture;
    public static boolean appLocked;
    public static boolean archiveHidden;
    public static boolean autoplayGifs;
    public static boolean autoplayVideo;
    public static int badPasscodeTries;
    public static int bubbleRadius;
    public static boolean chatBlur;
    public static boolean chatBubbles;
    private static int chatSwipeAction;
    private static boolean configLoaded;
    public static ProxyInfo currentProxy;
    public static boolean customTabs;
    public static int dayNightThemeSwitchHintCount;
    public static boolean debugWebView;
    private static int devicePerformanceClass;
    public static boolean directShare;
    public static String directShareHash;
    public static boolean disableVoiceAudioEffects;
    public static int distanceSystemType;
    public static boolean dontAskManageStorage;
    public static boolean drawDialogIcons;
    public static int emojiInteractionsHintCount;
    public static int fastScrollHintCount;
    public static int fontSize;
    public static boolean forceRtmpStream;
    public static boolean forwardingOptionsHintShown;
    public static boolean hasCameraCache;
    public static boolean inappCamera;
    public static boolean isWaitingForPasscodeEnter;
    public static int ivFontSize;
    public static int lastKeepMediaCheckTime;
    public static int lastLogsCheckTime;
    public static int lastPauseTime;
    public static long lastUpdateCheckTime;
    public static String lastUpdateVersion;
    public static long lastUptimeMillis;
    public static int lockRecordAudioVideoHint;
    public static boolean loopStickers;
    public static int mediaColumnsCount;
    public static int messageSeenHintCount;
    public static boolean noSoundHintShowed;
    public static boolean noStatusBar;
    public static boolean noiseSupression;
    public static long passcodeRetryInMs;
    public static int passcodeType;
    public static int passportConfigHash;
    private static HashMap<String, String> passportConfigMap;
    public static boolean pauseMusicOnRecord;
    public static TLRPC.TL_help_appUpdate pendingAppUpdate;
    public static int pendingAppUpdateBuildVersion;
    public static boolean playOrderReversed;
    public static ArrayList<ProxyInfo> proxyList;
    private static boolean proxyListLoaded;
    public static byte[] pushAuthKey;
    public static byte[] pushAuthKeyId;
    public static boolean pushStatSent;
    public static long pushStringGetTimeEnd;
    public static long pushStringGetTimeStart;
    public static boolean raiseToSpeak;
    public static int repeatMode;
    public static boolean roundCamera16to9;
    public static boolean saveIncomingPhotos;
    public static boolean saveStreamMedia;
    public static int saveToGalleryFlags;
    public static int scheduledOrNoSoundHintShows;
    public static int searchMessagesAsListHintShows;
    public static boolean searchMessagesAsListUsed;
    public static boolean showNotificationsForAllAccounts;
    public static boolean shuffleMusic;
    public static boolean smoothKeyboard;
    public static boolean sortContactsByName;
    public static boolean sortFilesByName;
    public static boolean stickersReorderingHintUsed;
    public static String storageCacheDir;
    public static boolean streamAllVideo;
    public static boolean streamMedia;
    public static boolean streamMkv;
    public static int suggestStickers;
    public static int textSelectionHintShows;
    public static boolean useSystemEmoji;
    public static boolean useThreeLinesLayout;
    public static String pushString = "";
    public static String pushStringStatus = "";
    public static String passcodeHash = "";
    public static byte[] passcodeSalt = new byte[0];
    public static int autoLockIn = 3600;
    public static boolean useFingerprint = true;
    public static int keepMedia = 2;
    private static int lastLocalId = -210000;
    private static String passportConfigJson = "";
    private static final Object sync = new Object();
    private static final Object localIdSync = new Object();
    public static int mapPreviewType = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PasscodeType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PerformanceClass {
    }

    static {
        chatBubbles = Build.VERSION.SDK_INT >= 30;
        autoplayGifs = true;
        autoplayVideo = true;
        raiseToSpeak = false;
        customTabs = true;
        directShare = true;
        inappCamera = true;
        roundCamera16to9 = true;
        noSoundHintShowed = false;
        streamMedia = true;
        streamAllVideo = false;
        streamMkv = false;
        saveStreamMedia = true;
        smoothKeyboard = true;
        pauseMusicOnRecord = true;
        chatBlur = true;
        noStatusBar = true;
        showNotificationsForAllAccounts = true;
        fontSize = 16;
        bubbleRadius = 17;
        ivFontSize = 16;
        mediaColumnsCount = 3;
        fastScrollHintCount = 3;
        loadConfig();
        proxyList = new ArrayList<>();
    }

    /* loaded from: classes4.dex */
    public static class ProxyInfo {
        public String address;
        public boolean available;
        public long availableCheckTime;
        public boolean checking;
        public String password;
        public long ping;
        public int port;
        public long proxyCheckPingId;
        public String secret;
        public String username;

        public ProxyInfo(String a, int p, String u, String pw, String s) {
            this.address = a;
            this.port = p;
            this.username = u;
            this.password = pw;
            this.secret = s;
            if (a == null) {
                this.address = "";
            }
            if (pw == null) {
                this.password = "";
            }
            if (u == null) {
                this.username = "";
            }
            if (s == null) {
                this.secret = "";
            }
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                editor.putString("passcodeHash1", passcodeHash);
                byte[] bArr = passcodeSalt;
                editor.putString("passcodeSalt", bArr.length > 0 ? Base64.encodeToString(bArr, 0) : "");
                editor.putBoolean("appLocked", appLocked);
                editor.putInt("passcodeType", passcodeType);
                editor.putLong("passcodeRetryInMs", passcodeRetryInMs);
                editor.putLong("lastUptimeMillis", lastUptimeMillis);
                editor.putInt("badPasscodeTries", badPasscodeTries);
                editor.putInt("autoLockIn", autoLockIn);
                editor.putInt("lastPauseTime", lastPauseTime);
                editor.putString("lastUpdateVersion2", lastUpdateVersion);
                editor.putBoolean("useFingerprint", useFingerprint);
                editor.putBoolean("allowScreenCapture", allowScreenCapture);
                editor.putString("pushString2", pushString);
                editor.putBoolean("pushStatSent", pushStatSent);
                byte[] bArr2 = pushAuthKey;
                editor.putString("pushAuthKey", bArr2 != null ? Base64.encodeToString(bArr2, 0) : "");
                editor.putInt("lastLocalId", lastLocalId);
                editor.putString("passportConfigJson", passportConfigJson);
                editor.putInt("passportConfigHash", passportConfigHash);
                editor.putBoolean("sortContactsByName", sortContactsByName);
                editor.putBoolean("sortFilesByName", sortFilesByName);
                editor.putInt("textSelectionHintShows", textSelectionHintShows);
                editor.putInt("scheduledOrNoSoundHintShows", scheduledOrNoSoundHintShows);
                editor.putBoolean("forwardingOptionsHintShown", forwardingOptionsHintShown);
                editor.putInt("lockRecordAudioVideoHint", lockRecordAudioVideoHint);
                editor.putString("storageCacheDir", !TextUtils.isEmpty(storageCacheDir) ? storageCacheDir : "");
                if (pendingAppUpdate != null) {
                    try {
                        SerializedData data = new SerializedData(pendingAppUpdate.getObjectSize());
                        pendingAppUpdate.serializeToStream(data);
                        String str = Base64.encodeToString(data.toByteArray(), 0);
                        editor.putString("appUpdate", str);
                        editor.putInt("appUpdateBuild", pendingAppUpdateBuildVersion);
                        data.cleanup();
                    } catch (Exception e) {
                    }
                } else {
                    editor.remove("appUpdate");
                }
                editor.putLong("appUpdateCheckTime", lastUpdateCheckTime);
                editor.apply();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public static int getLastLocalId() {
        int value;
        synchronized (localIdSync) {
            value = lastLocalId;
            lastLocalId = value - 1;
        }
        return value;
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (!configLoaded && ApplicationLoader.applicationContext != null) {
                SharedPreferences unused = BackgroundActivityPrefs.prefs = ApplicationLoader.applicationContext.getSharedPreferences("background_activity", 0);
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                saveIncomingPhotos = preferences.getBoolean("saveIncomingPhotos", false);
                passcodeHash = preferences.getString("passcodeHash1", "");
                appLocked = preferences.getBoolean("appLocked", false);
                passcodeType = preferences.getInt("passcodeType", 0);
                passcodeRetryInMs = preferences.getLong("passcodeRetryInMs", 0L);
                lastUptimeMillis = preferences.getLong("lastUptimeMillis", 0L);
                badPasscodeTries = preferences.getInt("badPasscodeTries", 0);
                autoLockIn = preferences.getInt("autoLockIn", 3600);
                lastPauseTime = preferences.getInt("lastPauseTime", 0);
                useFingerprint = preferences.getBoolean("useFingerprint", true);
                lastUpdateVersion = preferences.getString("lastUpdateVersion2", "3.5");
                allowScreenCapture = preferences.getBoolean("allowScreenCapture", false);
                lastLocalId = preferences.getInt("lastLocalId", -210000);
                pushString = preferences.getString("pushString2", "");
                pushStatSent = preferences.getBoolean("pushStatSent", false);
                passportConfigJson = preferences.getString("passportConfigJson", "");
                passportConfigHash = preferences.getInt("passportConfigHash", 0);
                storageCacheDir = preferences.getString("storageCacheDir", null);
                String authKeyString = preferences.getString("pushAuthKey", null);
                if (!TextUtils.isEmpty(authKeyString)) {
                    pushAuthKey = Base64.decode(authKeyString, 0);
                }
                if (passcodeHash.length() > 0 && lastPauseTime == 0) {
                    lastPauseTime = (int) ((SystemClock.elapsedRealtime() / 1000) - 600);
                }
                String passcodeSaltString = preferences.getString("passcodeSalt", "");
                if (passcodeSaltString.length() > 0) {
                    passcodeSalt = Base64.decode(passcodeSaltString, 0);
                } else {
                    passcodeSalt = new byte[0];
                }
                lastUpdateCheckTime = preferences.getLong("appUpdateCheckTime", System.currentTimeMillis());
                try {
                    String update = preferences.getString("appUpdate", null);
                    if (update != null) {
                        pendingAppUpdateBuildVersion = preferences.getInt("appUpdateBuild", BuildVars.BUILD_VERSION);
                        byte[] arr = Base64.decode(update, 0);
                        if (arr != null) {
                            SerializedData data = new SerializedData(arr);
                            pendingAppUpdate = (TLRPC.TL_help_appUpdate) TLRPC.help_AppUpdate.TLdeserialize(data, data.readInt32(false), false);
                            data.cleanup();
                        }
                    }
                    if (pendingAppUpdate != null) {
                        int updateVersion = 0;
                        String updateVersionString = null;
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            updateVersion = packageInfo.versionCode;
                            updateVersionString = packageInfo.versionName;
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        if (updateVersion == 0) {
                            updateVersion = BuildVars.BUILD_VERSION;
                        }
                        if (updateVersionString == null) {
                            updateVersionString = BuildVars.BUILD_VERSION_STRING;
                        }
                        if (pendingAppUpdateBuildVersion != updateVersion || pendingAppUpdate.version == null || updateVersionString.compareTo(pendingAppUpdate.version) >= 0) {
                            pendingAppUpdate = null;
                            AndroidUtilities.runOnUIThread(SharedConfig$$ExternalSyntheticLambda4.INSTANCE);
                        }
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean saveToGalleryLegacy = preferences2.getBoolean("save_gallery", false);
                if (saveToGalleryLegacy && BuildVars.NO_SCOPED_STORAGE) {
                    saveToGalleryFlags = 7;
                    preferences2.edit().remove("save_gallery").putInt("save_gallery_flags", saveToGalleryFlags).apply();
                } else {
                    saveToGalleryFlags = preferences2.getInt("save_gallery_flags", 0);
                }
                autoplayGifs = preferences2.getBoolean("autoplay_gif", true);
                autoplayVideo = preferences2.getBoolean("autoplay_video", true);
                mapPreviewType = preferences2.getInt("mapPreviewType", 2);
                raiseToSpeak = preferences2.getBoolean("raise_to_speak", false);
                customTabs = preferences2.getBoolean("custom_tabs", true);
                directShare = preferences2.getBoolean("direct_share", true);
                boolean z = preferences2.getBoolean("shuffleMusic", false);
                shuffleMusic = z;
                playOrderReversed = !z && preferences2.getBoolean("playOrderReversed", false);
                inappCamera = preferences2.getBoolean("inappCamera", true);
                hasCameraCache = preferences2.contains("cameraCache");
                roundCamera16to9 = true;
                repeatMode = preferences2.getInt("repeatMode", 0);
                fontSize = preferences2.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                bubbleRadius = preferences2.getInt("bubbleRadius", 17);
                ivFontSize = preferences2.getInt("iv_font_size", fontSize);
                allowBigEmoji = preferences2.getBoolean("allowBigEmoji", true);
                useSystemEmoji = preferences2.getBoolean("useSystemEmoji", false);
                streamMedia = preferences2.getBoolean("streamMedia", true);
                saveStreamMedia = preferences2.getBoolean("saveStreamMedia", true);
                smoothKeyboard = preferences2.getBoolean("smoothKeyboard2", true);
                pauseMusicOnRecord = preferences2.getBoolean("pauseMusicOnRecord", false);
                chatBlur = preferences2.getBoolean("chatBlur", true);
                streamAllVideo = preferences2.getBoolean("streamAllVideo", BuildVars.DEBUG_VERSION);
                streamMkv = preferences2.getBoolean("streamMkv", false);
                suggestStickers = preferences2.getInt("suggestStickers", 0);
                sortContactsByName = preferences2.getBoolean("sortContactsByName", false);
                sortFilesByName = preferences2.getBoolean("sortFilesByName", false);
                noSoundHintShowed = preferences2.getBoolean("noSoundHintShowed", false);
                directShareHash = preferences2.getString("directShareHash2", null);
                useThreeLinesLayout = preferences2.getBoolean("useThreeLinesLayout", false);
                archiveHidden = preferences2.getBoolean("archiveHidden", false);
                distanceSystemType = preferences2.getInt("distanceSystemType", 0);
                devicePerformanceClass = preferences2.getInt("devicePerformanceClass", -1);
                loopStickers = preferences2.getBoolean("loopStickers", true);
                keepMedia = preferences2.getInt("keep_media", 2);
                noStatusBar = preferences2.getBoolean("noStatusBar", true);
                forceRtmpStream = preferences2.getBoolean("forceRtmpStream", false);
                debugWebView = preferences2.getBoolean("debugWebView", false);
                lastKeepMediaCheckTime = preferences2.getInt("lastKeepMediaCheckTime", 0);
                lastLogsCheckTime = preferences2.getInt("lastLogsCheckTime", 0);
                searchMessagesAsListHintShows = preferences2.getInt("searchMessagesAsListHintShows", 0);
                searchMessagesAsListUsed = preferences2.getBoolean("searchMessagesAsListUsed", false);
                stickersReorderingHintUsed = preferences2.getBoolean("stickersReorderingHintUsed", false);
                textSelectionHintShows = preferences2.getInt("textSelectionHintShows", 0);
                scheduledOrNoSoundHintShows = preferences2.getInt("scheduledOrNoSoundHintShows", 0);
                forwardingOptionsHintShown = preferences2.getBoolean("forwardingOptionsHintShown", false);
                lockRecordAudioVideoHint = preferences2.getInt("lockRecordAudioVideoHint", 0);
                disableVoiceAudioEffects = preferences2.getBoolean("disableVoiceAudioEffects", false);
                noiseSupression = preferences2.getBoolean("noiseSupression", false);
                chatSwipeAction = preferences2.getInt("ChatSwipeAction", -1);
                messageSeenHintCount = preferences2.getInt("messageSeenCount", 3);
                emojiInteractionsHintCount = preferences2.getInt("emojiInteractionsHintCount", 3);
                dayNightThemeSwitchHintCount = preferences2.getInt("dayNightThemeSwitchHintCount", 3);
                mediaColumnsCount = preferences2.getInt("mediaColumnsCount", 3);
                fastScrollHintCount = preferences2.getInt("fastScrollHintCount", 3);
                dontAskManageStorage = preferences2.getBoolean("dontAskManageStorage", false);
                showNotificationsForAllAccounts = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("AllAccounts", true);
                configLoaded = true;
                try {
                    if (Build.VERSION.SDK_INT >= 19 && debugWebView) {
                        WebView.setWebContentsDebuggingEnabled(true);
                    }
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
        }
    }

    public static void increaseBadPasscodeTries() {
        int i = badPasscodeTries + 1;
        badPasscodeTries = i;
        if (i >= 3) {
            switch (i) {
                case 3:
                    passcodeRetryInMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                    break;
                case 4:
                    passcodeRetryInMs = 10000L;
                    break;
                case 5:
                    passcodeRetryInMs = 15000L;
                    break;
                case 6:
                    passcodeRetryInMs = SilenceSkippingAudioProcessor.DEFAULT_PADDING_SILENCE_US;
                    break;
                case 7:
                    passcodeRetryInMs = 25000L;
                    break;
                default:
                    passcodeRetryInMs = 30000L;
                    break;
            }
            lastUptimeMillis = SystemClock.elapsedRealtime();
        }
        saveConfig();
    }

    public static boolean isPassportConfigLoaded() {
        return passportConfigMap != null;
    }

    public static void setPassportConfig(String json, int hash) {
        passportConfigMap = null;
        passportConfigJson = json;
        passportConfigHash = hash;
        saveConfig();
        getCountryLangs();
    }

    public static HashMap<String, String> getCountryLangs() {
        if (passportConfigMap == null) {
            passportConfigMap = new HashMap<>();
            try {
                JSONObject object = new JSONObject(passportConfigJson);
                Iterator<String> iter = object.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    passportConfigMap.put(key.toUpperCase(), object.getString(key).toUpperCase());
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        return passportConfigMap;
    }

    public static boolean isAppUpdateAvailable() {
        int currentVersion;
        TLRPC.TL_help_appUpdate tL_help_appUpdate = pendingAppUpdate;
        if (tL_help_appUpdate == null || tL_help_appUpdate.document == null || !BuildVars.isStandaloneApp()) {
            return false;
        }
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            currentVersion = pInfo.versionCode;
        } catch (Exception e) {
            FileLog.e(e);
            currentVersion = BuildVars.BUILD_VERSION;
        }
        return pendingAppUpdateBuildVersion == currentVersion;
    }

    public static boolean setNewAppVersionAvailable(TLRPC.TL_help_appUpdate update) {
        String updateVersionString = null;
        int versionCode = 0;
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            updateVersionString = packageInfo.versionName;
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (versionCode == 0) {
            versionCode = BuildVars.BUILD_VERSION;
        }
        if (updateVersionString == null) {
            updateVersionString = BuildVars.BUILD_VERSION_STRING;
        }
        if (update.version == null || updateVersionString.compareTo(update.version) >= 0) {
            return false;
        }
        pendingAppUpdate = update;
        pendingAppUpdateBuildVersion = versionCode;
        saveConfig();
        return true;
    }

    public static boolean checkPasscode(String passcode) {
        if (passcodeSalt.length == 0) {
            boolean result = Utilities.MD5(passcode).equals(passcodeHash);
            if (result) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    byte[] passcodeBytes = passcode.getBytes("UTF-8");
                    byte[] bytes = new byte[passcodeBytes.length + 32];
                    System.arraycopy(passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                    saveConfig();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return result;
        }
        try {
            byte[] passcodeBytes2 = passcode.getBytes("UTF-8");
            byte[] bytes2 = new byte[passcodeBytes2.length + 32];
            System.arraycopy(passcodeSalt, 0, bytes2, 0, 16);
            System.arraycopy(passcodeBytes2, 0, bytes2, 16, passcodeBytes2.length);
            System.arraycopy(passcodeSalt, 0, bytes2, passcodeBytes2.length + 16, 16);
            String hash = Utilities.bytesToHex(Utilities.computeSHA256(bytes2, 0, bytes2.length));
            return passcodeHash.equals(hash);
        } catch (Exception e2) {
            FileLog.e(e2);
            return false;
        }
    }

    public static void clearConfig() {
        saveIncomingPhotos = false;
        appLocked = false;
        passcodeType = 0;
        passcodeRetryInMs = 0L;
        lastUptimeMillis = 0L;
        badPasscodeTries = 0;
        passcodeHash = "";
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        lastPauseTime = 0;
        useFingerprint = true;
        isWaitingForPasscodeEnter = false;
        allowScreenCapture = false;
        lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
        textSelectionHintShows = 0;
        scheduledOrNoSoundHintShows = 0;
        lockRecordAudioVideoHint = 0;
        forwardingOptionsHintShown = false;
        messageSeenHintCount = 3;
        emojiInteractionsHintCount = 3;
        dayNightThemeSwitchHintCount = 3;
        saveConfig();
    }

    public static void setSuggestStickers(int type) {
        suggestStickers = type;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("suggestStickers", suggestStickers);
        editor.commit();
    }

    public static void setSearchMessagesAsListUsed(boolean value) {
        searchMessagesAsListUsed = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("searchMessagesAsListUsed", searchMessagesAsListUsed);
        editor.commit();
    }

    public static void setStickersReorderingHintUsed(boolean value) {
        stickersReorderingHintUsed = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("stickersReorderingHintUsed", stickersReorderingHintUsed);
        editor.commit();
    }

    public static void increaseTextSelectionHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        int i = textSelectionHintShows + 1;
        textSelectionHintShows = i;
        editor.putInt("textSelectionHintShows", i);
        editor.commit();
    }

    public static void removeTextSelectionHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("textSelectionHintShows", 3);
        editor.commit();
    }

    public static void increaseScheduledOrNoSuoundHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        int i = scheduledOrNoSoundHintShows + 1;
        scheduledOrNoSoundHintShows = i;
        editor.putInt("scheduledOrNoSoundHintShows", i);
        editor.commit();
    }

    public static void forwardingOptionsHintHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        forwardingOptionsHintShown = true;
        editor.putBoolean("forwardingOptionsHintShown", true);
        editor.commit();
    }

    public static void removeScheduledOrNoSoundHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("scheduledOrNoSoundHintShows", 3);
        editor.commit();
    }

    public static void increaseLockRecordAudioVideoHintShowed() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        int i = lockRecordAudioVideoHint + 1;
        lockRecordAudioVideoHint = i;
        editor.putInt("lockRecordAudioVideoHint", i);
        editor.commit();
    }

    public static void removeLockRecordAudioVideoHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lockRecordAudioVideoHint", 3);
        editor.commit();
    }

    public static void increaseSearchAsListHintShows() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        int i = searchMessagesAsListHintShows + 1;
        searchMessagesAsListHintShows = i;
        editor.putInt("searchMessagesAsListHintShows", i);
        editor.commit();
    }

    public static void setKeepMedia(int value) {
        keepMedia = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("keep_media", keepMedia);
        editor.commit();
    }

    public static void checkLogsToDelete() {
        if (!BuildVars.LOGS_ENABLED) {
            return;
        }
        final int time = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(time - lastLogsCheckTime) < 3600) {
            return;
        }
        lastLogsCheckTime = time;
        Utilities.cacheClearQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SharedConfig$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SharedConfig.lambda$checkLogsToDelete$0(time);
            }
        });
    }

    public static /* synthetic */ void lambda$checkLogsToDelete$0(int time) {
        long currentTime = time - 864000;
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            Utilities.clearDir(dir.getAbsolutePath(), 0, currentTime, false);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastLogsCheckTime", lastLogsCheckTime);
        editor.commit();
    }

    public static void checkKeepMedia() {
        final int time = (int) (System.currentTimeMillis() / 1000);
        if (Math.abs(time - lastKeepMediaCheckTime) < 3600) {
            return;
        }
        lastKeepMediaCheckTime = time;
        final File cacheDir = FileLoader.checkDirectory(4);
        Utilities.cacheClearQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.SharedConfig$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SharedConfig.lambda$checkKeepMedia$1(time, cacheDir);
            }
        });
    }

    public static /* synthetic */ void lambda$checkKeepMedia$1(int time, File cacheDir) {
        int days;
        int i = keepMedia;
        if (i != 2) {
            if (i == 0) {
                days = 7;
            } else if (i == 1) {
                days = 30;
            } else {
                days = 3;
            }
            long currentTime = time - (days * 86400);
            SparseArray<File> paths = ImageLoader.getInstance().createMediaPaths();
            for (int a = 0; a < paths.size(); a++) {
                if (paths.keyAt(a) != 4) {
                    try {
                        Utilities.clearDir(paths.valueAt(a).getAbsolutePath(), 0, currentTime, false);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        File stickersPath = new File(cacheDir, "acache");
        if (stickersPath.exists()) {
            long currentTime2 = time - 86400;
            try {
                Utilities.clearDir(stickersPath.getAbsolutePath(), 0, currentTime2, false);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastKeepMediaCheckTime", lastKeepMediaCheckTime);
        editor.commit();
    }

    public static void toggleDisableVoiceAudioEffects() {
        disableVoiceAudioEffects = !disableVoiceAudioEffects;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableVoiceAudioEffects", disableVoiceAudioEffects);
        editor.commit();
    }

    public static void toggleNoiseSupression() {
        noiseSupression = !noiseSupression;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noiseSupression", noiseSupression);
        editor.commit();
    }

    public static void toggleForceRTMPStream() {
        forceRtmpStream = !forceRtmpStream;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("forceRtmpStream", forceRtmpStream);
        editor.apply();
    }

    public static void toggleDebugWebView() {
        debugWebView = !debugWebView;
        if (Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(debugWebView);
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("debugWebView", debugWebView);
        editor.apply();
    }

    public static void toggleNoStatusBar() {
        noStatusBar = !noStatusBar;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noStatusBar", noStatusBar);
        editor.commit();
    }

    public static void toggleLoopStickers() {
        loopStickers = !loopStickers;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loopStickers", loopStickers);
        editor.commit();
    }

    public static void toggleBigEmoji() {
        allowBigEmoji = !allowBigEmoji;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allowBigEmoji", allowBigEmoji);
        editor.commit();
    }

    public static void setPlaybackOrderType(int type) {
        if (type == 2) {
            shuffleMusic = true;
            playOrderReversed = false;
        } else if (type == 1) {
            playOrderReversed = true;
            shuffleMusic = false;
        } else {
            playOrderReversed = false;
            shuffleMusic = false;
        }
        MediaController.getInstance().checkIsNextMediaFileDownloaded();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("shuffleMusic", shuffleMusic);
        editor.putBoolean("playOrderReversed", playOrderReversed);
        editor.commit();
    }

    public static void setRepeatMode(int mode) {
        repeatMode = mode;
        if (mode < 0 || mode > 2) {
            repeatMode = 0;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("repeatMode", repeatMode);
        editor.commit();
    }

    public static void toggleSaveToGalleryFlag(int flag) {
        int i = saveToGalleryFlags;
        if ((i & flag) != 0) {
            saveToGalleryFlags = i & (flag ^ (-1));
        } else {
            saveToGalleryFlags = i | flag;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        preferences.edit().putInt("save_gallery_flags", saveToGalleryFlags).apply();
        ImageLoader.getInstance().checkMediaPaths();
        ImageLoader.getInstance().getCacheOutQueue().postRunnable(SharedConfig$$ExternalSyntheticLambda3.INSTANCE);
    }

    public static void toggleAutoplayGifs() {
        autoplayGifs = !autoplayGifs;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoplay_gif", autoplayGifs);
        editor.commit();
    }

    public static void setUseThreeLinesLayout(boolean value) {
        useThreeLinesLayout = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useThreeLinesLayout", useThreeLinesLayout);
        editor.commit();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.dialogsNeedReload, true);
    }

    public static void toggleArchiveHidden() {
        archiveHidden = !archiveHidden;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("archiveHidden", archiveHidden);
        editor.commit();
    }

    public static void toggleAutoplayVideo() {
        autoplayVideo = !autoplayVideo;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoplay_video", autoplayVideo);
        editor.commit();
    }

    public static boolean isSecretMapPreviewSet() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        return preferences.contains("mapPreviewType");
    }

    public static void setSecretMapPreviewType(int value) {
        mapPreviewType = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("mapPreviewType", mapPreviewType);
        editor.commit();
    }

    public static void setNoSoundHintShowed(boolean value) {
        if (noSoundHintShowed == value) {
            return;
        }
        noSoundHintShowed = value;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("noSoundHintShowed", noSoundHintShowed);
        editor.commit();
    }

    public static void toogleRaiseToSpeak() {
        raiseToSpeak = !raiseToSpeak;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("raise_to_speak", raiseToSpeak);
        editor.commit();
    }

    public static void toggleCustomTabs() {
        customTabs = !customTabs;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("custom_tabs", customTabs);
        editor.commit();
    }

    public static void toggleDirectShare() {
        directShare = !directShare;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("direct_share", directShare);
        editor.commit();
        ShortcutManagerCompat.removeAllDynamicShortcuts(ApplicationLoader.applicationContext);
        MediaDataController.getInstance(UserConfig.selectedAccount).buildShortcuts();
    }

    public static void toggleStreamMedia() {
        streamMedia = !streamMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamMedia", streamMedia);
        editor.commit();
    }

    public static void toggleSortContactsByName() {
        sortContactsByName = !sortContactsByName;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sortContactsByName", sortContactsByName);
        editor.commit();
    }

    public static void toggleSortFilesByName() {
        sortFilesByName = !sortFilesByName;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sortFilesByName", sortFilesByName);
        editor.commit();
    }

    public static void toggleStreamAllVideo() {
        streamAllVideo = !streamAllVideo;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamAllVideo", streamAllVideo);
        editor.commit();
    }

    public static void toggleStreamMkv() {
        streamMkv = !streamMkv;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("streamMkv", streamMkv);
        editor.commit();
    }

    public static void toggleSaveStreamMedia() {
        saveStreamMedia = !saveStreamMedia;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("saveStreamMedia", saveStreamMedia);
        editor.commit();
    }

    public static void toggleSmoothKeyboard() {
        smoothKeyboard = !smoothKeyboard;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("smoothKeyboard2", smoothKeyboard);
        editor.commit();
    }

    public static void togglePauseMusicOnRecord() {
        pauseMusicOnRecord = !pauseMusicOnRecord;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pauseMusicOnRecord", pauseMusicOnRecord);
        editor.commit();
    }

    public static void toggleChatBlur() {
        chatBlur = !chatBlur;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("chatBlur", chatBlur);
        editor.commit();
    }

    public static void toggleInappCamera() {
        inappCamera = !inappCamera;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("inappCamera", inappCamera);
        editor.commit();
    }

    public static void toggleRoundCamera16to9() {
        roundCamera16to9 = !roundCamera16to9;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("roundCamera16to9", roundCamera16to9);
        editor.commit();
    }

    public static void setDistanceSystemType(int type) {
        distanceSystemType = type;
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("distanceSystemType", distanceSystemType);
        editor.commit();
        LocaleController.resetImperialSystemType();
    }

    public static void loadProxyList() {
        if (proxyListLoaded) {
            return;
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", "");
        String proxyUsername = preferences.getString("proxy_user", "");
        String proxyPassword = preferences.getString("proxy_pass", "");
        String proxySecret = preferences.getString("proxy_secret", "");
        int proxyPort = preferences.getInt("proxy_port", 1080);
        proxyListLoaded = true;
        proxyList.clear();
        currentProxy = null;
        String list = preferences.getString("proxy_list", null);
        if (!TextUtils.isEmpty(list)) {
            byte[] bytes = Base64.decode(list, 0);
            SerializedData data = new SerializedData(bytes);
            int count = data.readInt32(false);
            for (int a = 0; a < count; a++) {
                ProxyInfo info = new ProxyInfo(data.readString(false), data.readInt32(false), data.readString(false), data.readString(false), data.readString(false));
                proxyList.add(info);
                if (currentProxy == null && !TextUtils.isEmpty(proxyAddress) && proxyAddress.equals(info.address) && proxyPort == info.port && proxyUsername.equals(info.username) && proxyPassword.equals(info.password)) {
                    currentProxy = info;
                }
            }
            data.cleanup();
        }
        if (currentProxy == null && !TextUtils.isEmpty(proxyAddress)) {
            ProxyInfo info2 = new ProxyInfo(proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
            currentProxy = info2;
            proxyList.add(0, info2);
        }
    }

    public static void saveProxyList() {
        SerializedData serializedData = new SerializedData();
        int count = proxyList.size();
        serializedData.writeInt32(count);
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            String str = "";
            serializedData.writeString(info.address != null ? info.address : str);
            serializedData.writeInt32(info.port);
            serializedData.writeString(info.username != null ? info.username : str);
            serializedData.writeString(info.password != null ? info.password : str);
            if (info.secret != null) {
                str = info.secret;
            }
            serializedData.writeString(str);
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        preferences.edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).commit();
        serializedData.cleanup();
    }

    public static ProxyInfo addProxy(ProxyInfo proxyInfo) {
        loadProxyList();
        int count = proxyList.size();
        for (int a = 0; a < count; a++) {
            ProxyInfo info = proxyList.get(a);
            if (proxyInfo.address.equals(info.address) && proxyInfo.port == info.port && proxyInfo.username.equals(info.username) && proxyInfo.password.equals(info.password) && proxyInfo.secret.equals(info.secret)) {
                return info;
            }
        }
        proxyList.add(proxyInfo);
        saveProxyList();
        return proxyInfo;
    }

    public static void deleteProxy(ProxyInfo proxyInfo) {
        if (currentProxy == proxyInfo) {
            currentProxy = null;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean enabled = preferences.getBoolean("proxy_enabled", false);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("proxy_ip", "");
            editor.putString("proxy_pass", "");
            editor.putString("proxy_user", "");
            editor.putString("proxy_secret", "");
            editor.putInt("proxy_port", 1080);
            editor.putBoolean("proxy_enabled", false);
            editor.putBoolean("proxy_enabled_calls", false);
            editor.commit();
            if (enabled) {
                ConnectionsManager.setProxySettings(false, "", 0, "", "", "");
            }
        }
        proxyList.remove(proxyInfo);
        saveProxyList();
    }

    public static void checkSaveToGalleryFiles() {
        Utilities.globalQueue.postRunnable(SharedConfig$$ExternalSyntheticLambda2.INSTANCE);
    }

    public static /* synthetic */ void lambda$checkSaveToGalleryFiles$3() {
        try {
            File telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
            File imagePath = new File(telegramPath, "Telegram Images");
            imagePath.mkdir();
            File videoPath = new File(telegramPath, "Telegram Video");
            videoPath.mkdir();
            if (saveToGalleryFlags == 0 && BuildVars.NO_SCOPED_STORAGE) {
                if (imagePath.isDirectory()) {
                    AndroidUtilities.createEmptyFile(new File(imagePath, ".nomedia"));
                }
                if (videoPath.isDirectory()) {
                    AndroidUtilities.createEmptyFile(new File(videoPath, ".nomedia"));
                    return;
                }
                return;
            }
            if (imagePath.isDirectory()) {
                new File(imagePath, ".nomedia").delete();
            }
            if (videoPath.isDirectory()) {
                new File(videoPath, ".nomedia").delete();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static int getChatSwipeAction(int currentAccount) {
        int i = chatSwipeAction;
        if (i < 0) {
            return !MessagesController.getInstance(currentAccount).dialogFilters.isEmpty() ? 5 : 2;
        } else if (i == 5 && MessagesController.getInstance(currentAccount).dialogFilters.isEmpty()) {
            return 2;
        } else {
            return chatSwipeAction;
        }
    }

    public static void updateChatListSwipeSetting(int newAction) {
        chatSwipeAction = newAction;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        preferences.edit().putInt("ChatSwipeAction", chatSwipeAction).apply();
    }

    public static void updateMessageSeenHintCount(int count) {
        messageSeenHintCount = count;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        preferences.edit().putInt("messageSeenCount", messageSeenHintCount).apply();
    }

    public static void updateEmojiInteractionsHintCount(int count) {
        emojiInteractionsHintCount = count;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        preferences.edit().putInt("emojiInteractionsHintCount", emojiInteractionsHintCount).apply();
    }

    public static void updateDayNightThemeSwitchHintCount(int count) {
        dayNightThemeSwitchHintCount = count;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        preferences.edit().putInt("dayNightThemeSwitchHintCount", dayNightThemeSwitchHintCount).apply();
    }

    public static int getDevicePerformanceClass() {
        if (devicePerformanceClass == -1) {
            int androidVersion = Build.VERSION.SDK_INT;
            int cpuCount = ConnectionsManager.CPU_COUNT;
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
            int totalCpuFreq = 0;
            int freqResolved = 0;
            for (int i = 0; i < cpuCount; i++) {
                try {
                    RandomAccessFile reader = new RandomAccessFile(String.format(Locale.ENGLISH, "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", Integer.valueOf(i)), "r");
                    String line = reader.readLine();
                    if (line != null) {
                        totalCpuFreq += Utilities.parseInt((CharSequence) line).intValue() / 1000;
                        freqResolved++;
                    }
                    reader.close();
                } catch (Throwable th) {
                }
            }
            int maxCpuFreq = freqResolved == 0 ? -1 : (int) Math.ceil(totalCpuFreq / freqResolved);
            if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || ((cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250) || ((cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21) || (cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24)))) {
                devicePerformanceClass = 0;
            } else if (cpuCount < 8 || memoryClass <= 160 || ((maxCpuFreq != -1 && maxCpuFreq <= 2050) || (maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23))) {
                devicePerformanceClass = 1;
            } else {
                devicePerformanceClass = 2;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("device performance info selected_class = " + devicePerformanceClass + " (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + ")");
            }
        }
        return devicePerformanceClass;
    }

    public static void setMediaColumnsCount(int count) {
        if (mediaColumnsCount != count) {
            mediaColumnsCount = count;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("mediaColumnsCount", mediaColumnsCount).apply();
        }
    }

    public static void setFastScrollHintCount(int count) {
        if (fastScrollHintCount != count) {
            fastScrollHintCount = count;
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("fastScrollHintCount", fastScrollHintCount).apply();
        }
    }

    public static void setDontAskManageStorage(boolean b) {
        dontAskManageStorage = b;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putBoolean("dontAskManageStorage", dontAskManageStorage).apply();
    }

    public static boolean canBlurChat() {
        return getDevicePerformanceClass() == 2;
    }

    public static boolean chatBlurEnabled() {
        return canBlurChat() && chatBlur;
    }

    /* loaded from: classes4.dex */
    public static class BackgroundActivityPrefs {
        private static SharedPreferences prefs;

        public static long getLastCheckedBackgroundActivity() {
            return prefs.getLong("last_checked", 0L);
        }

        public static void setLastCheckedBackgroundActivity(long l) {
            prefs.edit().putLong("last_checked", l).apply();
        }
    }
}
