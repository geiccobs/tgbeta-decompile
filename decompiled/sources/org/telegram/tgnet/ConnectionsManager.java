package org.telegram.tgnet;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.KeepAliveJob;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes.dex */
public class ConnectionsManager extends BaseController {
    private static final int CORE_POOL_SIZE;
    public static final int CPU_COUNT;
    public static final int ConnectionStateConnected = 3;
    public static final int ConnectionStateConnecting = 1;
    public static final int ConnectionStateConnectingToProxy = 4;
    public static final int ConnectionStateUpdating = 5;
    public static final int ConnectionStateWaitingForNetwork = 2;
    public static final int ConnectionTypeDownload = 2;
    public static final int ConnectionTypeDownload2 = 65538;
    public static final int ConnectionTypeGeneric = 1;
    public static final int ConnectionTypePush = 8;
    public static final int ConnectionTypeUpload = 4;
    public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
    public static final Executor DNS_THREAD_POOL_EXECUTOR;
    public static final int FileTypeAudio = 50331648;
    public static final int FileTypeFile = 67108864;
    public static final int FileTypePhoto = 16777216;
    public static final int FileTypeVideo = 33554432;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE;
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    public static final byte USE_IPV4_IPV6_RANDOM = 2;
    public static final byte USE_IPV4_ONLY = 0;
    public static final byte USE_IPV6_ONLY = 1;
    private static AsyncTask currentTask;
    private static long lastDnsRequestTime;
    private static final BlockingQueue<Runnable> sPoolWorkQueue;
    private static final ThreadFactory sThreadFactory;
    private int appResumeCount;
    private boolean forceTryIpV6;
    private boolean isUpdating;
    private static HashMap<String, ResolveHostByNameTask> resolvingHostnameTasks = new HashMap<>();
    private static HashMap<String, ResolvedDomain> dnsCache = new HashMap<>();
    private static int lastClassGuid = 1;
    private static final ConnectionsManager[] Instance = new ConnectionsManager[4];
    private long lastPauseTime = System.currentTimeMillis();
    private boolean appPaused = true;
    private AtomicInteger lastRequestToken = new AtomicInteger(1);
    private int connectionState = native_getConnectionState(this.currentAccount);

    public static native void native_applyDatacenterAddress(int i, int i2, String str, int i3);

    public static native void native_applyDnsConfig(int i, long j, String str, int i2);

    public static native void native_bindRequestToGuid(int i, int i2, int i3);

    public static native void native_cancelRequest(int i, int i2, boolean z);

    public static native void native_cancelRequestsForGuid(int i, int i2);

    public static native long native_checkProxy(int i, String str, int i2, String str2, String str3, String str4, RequestTimeDelegate requestTimeDelegate);

    public static native void native_cleanUp(int i, boolean z);

    public static native int native_getConnectionState(int i);

    public static native int native_getCurrentDatacenterId(int i);

    public static native int native_getCurrentTime(int i);

    public static native long native_getCurrentTimeMillis(int i);

    public static native int native_getTimeDifference(int i);

    public static native void native_init(int i, int i2, int i3, int i4, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, int i5, long j, boolean z, boolean z2, int i6);

    public static native int native_isTestBackend(int i);

    public static native void native_onHostNameResolved(String str, long j, String str2);

    public static native void native_pauseNetwork(int i);

    public static native void native_resumeNetwork(int i, boolean z);

    public static native void native_seSystemLangCode(int i, String str);

    public static native void native_sendRequest(int i, long j, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, WriteToSocketDelegate writeToSocketDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setIpStrategy(int i, byte b);

    public static native void native_setJava(boolean z);

    public static native void native_setLangCode(int i, String str);

    public static native void native_setNetworkAvailable(int i, boolean z, int i2, boolean z2);

    public static native void native_setProxySettings(int i, String str, int i2, String str2, String str3, String str4);

    public static native void native_setPushConnectionEnabled(int i, boolean z);

    public static native void native_setRegId(int i, String str);

    public static native void native_setSystemLangCode(int i, String str);

    public static native void native_setUserId(int i, long j);

    public static native void native_switchBackend(int i, boolean z);

    public static native void native_updateDcSettings(int i);

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = availableProcessors;
        int max = Math.max(2, Math.min(availableProcessors - 1, 4));
        CORE_POOL_SIZE = max;
        int i = (availableProcessors * 2) + 1;
        MAXIMUM_POOL_SIZE = i;
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(128);
        sPoolWorkQueue = linkedBlockingQueue;
        ThreadFactory threadFactory = new ThreadFactory() { // from class: org.telegram.tgnet.ConnectionsManager.1
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override // java.util.concurrent.ThreadFactory
            public Thread newThread(Runnable r) {
                return new Thread(r, "DnsAsyncTask #" + this.mCount.getAndIncrement());
            }
        };
        sThreadFactory = threadFactory;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(max, i, 30L, TimeUnit.SECONDS, linkedBlockingQueue, threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        DNS_THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public void setForceTryIpV6(boolean forceTryIpV6) {
        if (this.forceTryIpV6 != forceTryIpV6) {
            this.forceTryIpV6 = forceTryIpV6;
            checkConnection();
        }
    }

    /* loaded from: classes4.dex */
    public static class ResolvedDomain {
        public ArrayList<String> addresses;
        long ttl;

        public ResolvedDomain(ArrayList<String> a, long t) {
            this.addresses = a;
            this.ttl = t;
        }

        public String getAddress() {
            return this.addresses.get(Utilities.random.nextInt(this.addresses.size()));
        }
    }

    public static ConnectionsManager getInstance(int num) {
        ConnectionsManager[] connectionsManagerArr = Instance;
        ConnectionsManager localInstance = connectionsManagerArr[num];
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                localInstance = connectionsManagerArr[num];
                if (localInstance == null) {
                    ConnectionsManager connectionsManager = new ConnectionsManager(num);
                    localInstance = connectionsManager;
                    connectionsManagerArr[num] = connectionsManager;
                }
            }
        }
        return localInstance;
    }

    public ConnectionsManager(int instance) {
        super(instance);
        File config;
        String langCode;
        String appVersion;
        String langCode2;
        String systemVersion;
        String systemLangCode;
        String deviceModel;
        String appVersion2;
        String systemVersion2;
        SharedPreferences mainPreferences;
        File config2 = ApplicationLoader.getFilesDirFixed();
        if (instance == 0) {
            config = config2;
        } else {
            File config3 = new File(config2, "account" + instance);
            config3.mkdirs();
            config = config3;
        }
        String configPath = config.toString();
        boolean enablePushConnection = isPushConnectionEnabled();
        try {
            systemLangCode = LocaleController.getSystemLocaleStringIso639().toLowerCase();
            String langCode3 = LocaleController.getLocaleStringIso639().toLowerCase();
            langCode2 = Build.MANUFACTURER + Build.MODEL;
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                appVersion = appVersion + " pbeta";
            } else if (BuildVars.DEBUG_VERSION) {
                appVersion = appVersion + " beta";
            }
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
            langCode = langCode3;
        } catch (Exception e) {
            appVersion = "App version unknown";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
            systemLangCode = "en";
            langCode = "";
            langCode2 = "Android unknown";
        }
        systemLangCode = systemLangCode.trim().length() == 0 ? "en" : systemLangCode;
        if (langCode2.trim().length() != 0) {
            deviceModel = langCode2;
        } else {
            deviceModel = "Android unknown";
        }
        if (appVersion.trim().length() != 0) {
            appVersion2 = appVersion;
        } else {
            appVersion2 = "App version unknown";
        }
        if (systemVersion.trim().length() != 0) {
            systemVersion2 = systemVersion;
        } else {
            systemVersion2 = "SDK Unknown";
        }
        getUserConfig().loadConfig();
        String pushString = getRegId();
        String fingerprint = AndroidUtilities.getCertificateSHA256Fingerprint();
        int timezoneOffset = (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000;
        if (this.currentAccount == 0) {
            mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        } else {
            mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig" + this.currentAccount, 0);
        }
        this.forceTryIpV6 = mainPreferences.getBoolean("forceTryIpV6", false);
        init(BuildVars.BUILD_VERSION, TLRPC.LAYER, BuildVars.APP_ID, deviceModel, systemVersion2, appVersion2, langCode, systemLangCode, configPath, FileLog.getNetworkLogPath(), pushString, fingerprint, timezoneOffset, getUserConfig().getClientUserId(), enablePushConnection);
    }

    private String getRegId() {
        String pushString = SharedConfig.pushString;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(SharedConfig.pushStringStatus)) {
            pushString = SharedConfig.pushStringStatus;
        }
        if (TextUtils.isEmpty(pushString)) {
            String pushString2 = "__FIREBASE_GENERATING_SINCE_" + getCurrentTime() + "__";
            SharedConfig.pushStringStatus = pushString2;
            return pushString2;
        }
        return pushString;
    }

    public boolean isPushConnectionEnabled() {
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        if (preferences.contains("pushConnection")) {
            return preferences.getBoolean("pushConnection", true);
        }
        return MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("backgroundConnection", false);
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis(this.currentAccount);
    }

    public int getCurrentTime() {
        return native_getCurrentTime(this.currentAccount);
    }

    public int getCurrentDatacenterId() {
        return native_getCurrentDatacenterId(this.currentAccount);
    }

    public int getTimeDifference() {
        return native_getTimeDifference(this.currentAccount);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock) {
        return sendRequest(object, completionBlock, (QuickAckDelegate) null, 0);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags) {
        return sendRequest(object, completionBlock, null, null, null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, null, null, null, flags, Integer.MAX_VALUE, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegateTimestamp completionBlock, int flags, int connetionType, int datacenterId) {
        return sendRequest(object, null, completionBlock, null, null, flags, datacenterId, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, null, quickAckBlock, null, flags, Integer.MAX_VALUE, 1, true);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        return sendRequest(object, onComplete, null, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate);
    }

    public int sendRequest(final TLObject object, final RequestDelegate onComplete, final RequestDelegateTimestamp onCompleteTimestamp, final QuickAckDelegate onQuickAck, final WriteToSocketDelegate onWriteToSocket, final int flags, final int datacenterId, final int connetionType, final boolean immediate) {
        final int requestToken = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.this.m1369lambda$sendRequest$2$orgtelegramtgnetConnectionsManager(object, requestToken, onComplete, onCompleteTimestamp, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate);
            }
        });
        return requestToken;
    }

    /* renamed from: lambda$sendRequest$2$org-telegram-tgnet-ConnectionsManager */
    public /* synthetic */ void m1369lambda$sendRequest$2$orgtelegramtgnetConnectionsManager(final TLObject object, int requestToken, final RequestDelegate onComplete, final RequestDelegateTimestamp onCompleteTimestamp, QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, int flags, int datacenterId, int connetionType, boolean immediate) {
        Exception e;
        NativeByteBuffer buffer;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("send request " + object + " with token = " + requestToken);
        }
        try {
            buffer = new NativeByteBuffer(object.getObjectSize());
            object.serializeToStream(buffer);
            object.freeResources();
        } catch (Exception e2) {
            e = e2;
        }
        try {
        } catch (Exception e3) {
            e = e3;
            FileLog.e(e);
        }
        try {
            native_sendRequest(this.currentAccount, buffer.address, new RequestDelegateInternal() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegateInternal
                public final void run(long j, int i, String str, int i2, long j2) {
                    ConnectionsManager.lambda$sendRequest$1(TLObject.this, onComplete, onCompleteTimestamp, j, i, str, i2, j2);
                }
            }, onQuickAck, onWriteToSocket, flags, datacenterId, connetionType, immediate, requestToken);
        } catch (Exception e4) {
            e = e4;
            FileLog.e(e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x006c A[Catch: Exception -> 0x009d, TryCatch #1 {Exception -> 0x009d, blocks: (B:18:0x0063, B:20:0x0068, B:22:0x006c, B:23:0x0088), top: B:30:0x0063 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ void lambda$sendRequest$1(org.telegram.tgnet.TLObject r15, final org.telegram.tgnet.RequestDelegate r16, final org.telegram.tgnet.RequestDelegateTimestamp r17, long r18, int r20, java.lang.String r21, int r22, final long r23) {
        /*
            r1 = r15
            r2 = r21
            r0 = 0
            r3 = 0
            r4 = 0
            int r6 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r6 == 0) goto L25
            org.telegram.tgnet.NativeByteBuffer r4 = org.telegram.tgnet.NativeByteBuffer.wrap(r18)     // Catch: java.lang.Exception -> L1e
            r5 = 1
            r4.reused = r5     // Catch: java.lang.Exception -> L1e
            int r6 = r4.readInt32(r5)     // Catch: java.lang.Exception -> L1e
            org.telegram.tgnet.TLObject r5 = r15.deserializeResponse(r4, r6, r5)     // Catch: java.lang.Exception -> L1e
            r0 = r5
            r4 = r20
            goto L5f
        L1e:
            r0 = move-exception
            r4 = r20
        L21:
            r5 = r22
            goto L9e
        L25:
            if (r2 == 0) goto L5d
            org.telegram.tgnet.TLRPC$TL_error r4 = new org.telegram.tgnet.TLRPC$TL_error     // Catch: java.lang.Exception -> L1e
            r4.<init>()     // Catch: java.lang.Exception -> L1e
            r3 = r4
            r4 = r20
            r3.code = r4     // Catch: java.lang.Exception -> L5b
            r3.text = r2     // Catch: java.lang.Exception -> L5b
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch: java.lang.Exception -> L5b
            if (r5 == 0) goto L5f
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L5b
            r5.<init>()     // Catch: java.lang.Exception -> L5b
            r5.append(r15)     // Catch: java.lang.Exception -> L5b
            java.lang.String r6 = " got error "
            r5.append(r6)     // Catch: java.lang.Exception -> L5b
            int r6 = r3.code     // Catch: java.lang.Exception -> L5b
            r5.append(r6)     // Catch: java.lang.Exception -> L5b
            java.lang.String r6 = " "
            r5.append(r6)     // Catch: java.lang.Exception -> L5b
            java.lang.String r6 = r3.text     // Catch: java.lang.Exception -> L5b
            r5.append(r6)     // Catch: java.lang.Exception -> L5b
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Exception -> L5b
            org.telegram.messenger.FileLog.e(r5)     // Catch: java.lang.Exception -> L5b
            goto L5f
        L5b:
            r0 = move-exception
            goto L21
        L5d:
            r4 = r20
        L5f:
            if (r0 == 0) goto L66
            r5 = r22
            r0.networkType = r5     // Catch: java.lang.Exception -> L9d
            goto L68
        L66:
            r5 = r22
        L68:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch: java.lang.Exception -> L9d
            if (r6 == 0) goto L88
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L9d
            r6.<init>()     // Catch: java.lang.Exception -> L9d
            java.lang.String r7 = "java received "
            r6.append(r7)     // Catch: java.lang.Exception -> L9d
            r6.append(r0)     // Catch: java.lang.Exception -> L9d
            java.lang.String r7 = " error = "
            r6.append(r7)     // Catch: java.lang.Exception -> L9d
            r6.append(r3)     // Catch: java.lang.Exception -> L9d
            java.lang.String r6 = r6.toString()     // Catch: java.lang.Exception -> L9d
            org.telegram.messenger.FileLog.d(r6)     // Catch: java.lang.Exception -> L9d
        L88:
            r8 = r0
            r9 = r3
            org.telegram.messenger.DispatchQueue r13 = org.telegram.messenger.Utilities.stageQueue     // Catch: java.lang.Exception -> L9d
            org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda2 r14 = new org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda2     // Catch: java.lang.Exception -> L9d
            r6 = r14
            r7 = r16
            r10 = r17
            r11 = r23
            r6.<init>()     // Catch: java.lang.Exception -> L9d
            r13.postRunnable(r14)     // Catch: java.lang.Exception -> L9d
            goto La1
        L9d:
            r0 = move-exception
        L9e:
            org.telegram.messenger.FileLog.e(r0)
        La1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.tgnet.ConnectionsManager.lambda$sendRequest$1(org.telegram.tgnet.TLObject, org.telegram.tgnet.RequestDelegate, org.telegram.tgnet.RequestDelegateTimestamp, long, int, java.lang.String, int, long):void");
    }

    public static /* synthetic */ void lambda$sendRequest$0(RequestDelegate onComplete, TLObject finalResponse, TLRPC.TL_error finalError, RequestDelegateTimestamp onCompleteTimestamp, long timestamp) {
        if (onComplete != null) {
            onComplete.run(finalResponse, finalError);
        } else if (onCompleteTimestamp != null) {
            onCompleteTimestamp.run(finalResponse, finalError, timestamp);
        }
        if (finalResponse != null) {
            finalResponse.freeResources();
        }
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(this.currentAccount, token, notifyServer);
    }

    public void cleanup(boolean resetKeys) {
        native_cleanUp(this.currentAccount, resetKeys);
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(this.currentAccount, guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(this.currentAccount, requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port) {
        native_applyDatacenterAddress(this.currentAccount, datacenterId, ipAddress, port);
    }

    public int getConnectionState() {
        int i = this.connectionState;
        if (i == 3 && this.isUpdating) {
            return 5;
        }
        return i;
    }

    public void setUserId(long id) {
        native_setUserId(this.currentAccount, id);
    }

    public void checkConnection() {
        native_setIpStrategy(this.currentAccount, getIpStrategy());
        native_setNetworkAvailable(this.currentAccount, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType(), ApplicationLoader.isConnectionSlow());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(this.currentAccount, value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String systemLangCode, String configPath, String logPath, String regId, String cFingerprint, int timezoneOffset, long userId, boolean enablePushConnection) {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String proxyAddress = preferences.getString("proxy_ip", "");
        String proxyUsername = preferences.getString("proxy_user", "");
        String proxyPassword = preferences.getString("proxy_pass", "");
        String proxySecret = preferences.getString("proxy_secret", "");
        int proxyPort = preferences.getInt("proxy_port", 1080);
        if (preferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(proxyAddress)) {
            native_setProxySettings(this.currentAccount, proxyAddress, proxyPort, proxyUsername, proxyPassword, proxySecret);
        }
        String installer = "";
        try {
            installer = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
        } catch (Throwable th) {
        }
        if (installer == null) {
            installer = "";
        }
        String packageId = "";
        try {
            packageId = ApplicationLoader.applicationContext.getPackageName();
        } catch (Throwable th2) {
        }
        if (packageId == null) {
            packageId = "";
        }
        native_init(this.currentAccount, version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, systemLangCode, configPath, logPath, regId, cFingerprint, installer, packageId, timezoneOffset, userId, enablePushConnection, ApplicationLoader.isNetworkOnline(), ApplicationLoader.getCurrentNetworkType());
        checkConnection();
    }

    public static void setLangCode(String langCode) {
        String langCode2 = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 4; a++) {
            native_setLangCode(a, langCode2);
        }
    }

    public static void setRegId(String regId, String status) {
        String pushString = regId;
        if (TextUtils.isEmpty(pushString) && !TextUtils.isEmpty(status)) {
            pushString = status;
        }
        if (TextUtils.isEmpty(pushString)) {
            String str = "__FIREBASE_GENERATING_SINCE_" + getInstance(0).getCurrentTime() + "__";
            SharedConfig.pushStringStatus = str;
            pushString = str;
        }
        for (int a = 0; a < 4; a++) {
            native_setRegId(a, pushString);
        }
    }

    public static void setSystemLangCode(String langCode) {
        String langCode2 = langCode.replace('_', '-').toLowerCase();
        for (int a = 0; a < 4; a++) {
            native_setSystemLangCode(a, langCode2);
        }
    }

    public void switchBackend(boolean restart) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        preferences.edit().remove("language_showed2").commit();
        native_switchBackend(this.currentAccount, restart);
    }

    public boolean isTestBackend() {
        return native_isTestBackend(this.currentAccount) != 0;
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(this.currentAccount, true);
    }

    public void updateDcSettings() {
        native_updateDcSettings(this.currentAccount);
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public long checkProxy(String address, int port, String username, String password, String secret, RequestTimeDelegate requestTimeDelegate) {
        if (TextUtils.isEmpty(address)) {
            return 0L;
        }
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        return native_checkProxy(this.currentAccount, address, port, username, password, secret, requestTimeDelegate);
    }

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            this.appPaused = value;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app paused = " + value);
            }
            if (value) {
                this.appResumeCount--;
            } else {
                this.appResumeCount++;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("app resume count " + this.appResumeCount);
            }
            if (this.appResumeCount < 0) {
                this.appResumeCount = 0;
            }
        }
        if (this.appResumeCount == 0) {
            if (this.lastPauseTime == 0) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork(this.currentAccount);
        } else if (this.appPaused) {
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset app pause time");
            }
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                getContactsController().checkContacts();
            }
            this.lastPauseTime = 0L;
            native_resumeNetwork(this.currentAccount, false);
        }
    }

    public static void onUnparsedMessageReceived(long address, final int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            int constructor = buff.readInt32(true);
            final TLObject message = TLClassStore.Instance().TLdeserialize(buff, constructor, true);
            if (message instanceof TLRPC.Updates) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("java received " + message);
                }
                KeepAliveJob.finishJob();
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccountInstance.getInstance(currentAccount).getMessagesController().processUpdates((TLRPC.Updates) message, false);
                    }
                });
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d(String.format("java received unknown constructor 0x%x", Integer.valueOf(constructor)));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onUpdate(final int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                AccountInstance.getInstance(currentAccount).getMessagesController().updateTimerProc();
            }
        });
    }

    public static void onSessionCreated(final int currentAccount) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                AccountInstance.getInstance(currentAccount).getMessagesController().getDifference();
            }
        });
    }

    public static void onConnectionStateChanged(final int state, final int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.lambda$onConnectionStateChanged$6(currentAccount, state);
            }
        });
    }

    public static /* synthetic */ void lambda$onConnectionStateChanged$6(int currentAccount, int state) {
        getInstance(currentAccount).connectionState = state;
        AccountInstance.getInstance(currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
    }

    public static void onLogout(final int currentAccount) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.lambda$onLogout$7(currentAccount);
            }
        });
    }

    public static /* synthetic */ void lambda$onLogout$7(int currentAccount) {
        AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
        if (accountInstance.getUserConfig().getClientUserId() != 0) {
            accountInstance.getUserConfig().clearConfig();
            accountInstance.getMessagesController().performLogout(0);
        }
    }

    public static int getInitFlags() {
        EmuDetector detector = EmuDetector.with(ApplicationLoader.applicationContext);
        if (!detector.detect()) {
            return 0;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("detected emu");
        }
        int flags = 0 | 1024;
        return flags;
    }

    public static void onBytesSent(int amount, int networkType, int currentAccount) {
        try {
            AccountInstance.getInstance(currentAccount).getStatsController().incrementSentBytesCount(networkType, 6, amount);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onRequestNewServerIpAndPort(final int second, final int currentAccount) {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.lambda$onRequestNewServerIpAndPort$9(second, currentAccount);
            }
        });
    }

    public static /* synthetic */ void lambda$onRequestNewServerIpAndPort$9(final int second, final int currentAccount) {
        final boolean networkOnline = ApplicationLoader.isNetworkOnline();
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.lambda$onRequestNewServerIpAndPort$8(second, networkOnline, currentAccount);
            }
        });
    }

    public static /* synthetic */ void lambda$onRequestNewServerIpAndPort$8(int second, boolean networkOnline, int currentAccount) {
        if (currentTask != null || ((second == 0 && Math.abs(lastDnsRequestTime - System.currentTimeMillis()) < 10000) || !networkOnline)) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("don't start task, current task = " + currentTask + " next task = " + second + " time diff = " + Math.abs(lastDnsRequestTime - System.currentTimeMillis()) + " network = " + ApplicationLoader.isNetworkOnline());
                return;
            }
            return;
        }
        lastDnsRequestTime = System.currentTimeMillis();
        if (second == 3) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start mozilla txt task");
            }
            MozillaDnsLoadTask task = new MozillaDnsLoadTask(currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            currentTask = task;
        } else if (second == 2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start google txt task");
            }
            GoogleDnsLoadTask task2 = new GoogleDnsLoadTask(currentAccount);
            task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            currentTask = task2;
        } else if (second == 1) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask task3 = new DnsTxtLoadTask(currentAccount);
            task3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            currentTask = task3;
        } else {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start firebase task");
            }
            FirebaseTask task4 = new FirebaseTask(currentAccount);
            task4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            currentTask = task4;
        }
    }

    public static void onProxyError() {
        AndroidUtilities.runOnUIThread(ConnectionsManager$$ExternalSyntheticLambda3.INSTANCE);
    }

    public static void getHostByName(final String hostName, final long address) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.lambda$getHostByName$11(hostName, address);
            }
        });
    }

    public static /* synthetic */ void lambda$getHostByName$11(String hostName, long address) {
        ResolvedDomain resolvedDomain = dnsCache.get(hostName);
        if (resolvedDomain != null && SystemClock.elapsedRealtime() - resolvedDomain.ttl < 300000) {
            native_onHostNameResolved(hostName, address, resolvedDomain.getAddress());
            return;
        }
        ResolveHostByNameTask task = resolvingHostnameTasks.get(hostName);
        if (task == null) {
            task = new ResolveHostByNameTask(hostName);
            try {
                task.executeOnExecutor(DNS_THREAD_POOL_EXECUTOR, null, null, null);
                resolvingHostnameTasks.put(hostName, task);
            } catch (Throwable e) {
                FileLog.e(e);
                native_onHostNameResolved(hostName, address, "");
                return;
            }
        }
        task.addAddress(address);
    }

    public static void onBytesReceived(int amount, int networkType, int currentAccount) {
        try {
            StatsController.getInstance(currentAccount).incrementReceivedBytesCount(networkType, 6, amount);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onUpdateConfig(long address, final int currentAccount) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            final TLRPC.TL_config message = TLRPC.TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccountInstance.getInstance(currentAccount).getMessagesController().updateConfig(message);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void onInternalPushReceived(int currentAccount) {
        KeepAliveJob.startJob();
    }

    public static void setProxySettings(boolean enabled, String address, int port, String username, String password, String secret) {
        if (address == null) {
            address = "";
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (secret == null) {
            secret = "";
        }
        for (int a = 0; a < 4; a++) {
            if (enabled && !TextUtils.isEmpty(address)) {
                native_setProxySettings(a, address, port, username, password, secret);
            } else {
                native_setProxySettings(a, "", 1080, "", "", "");
            }
            AccountInstance accountInstance = AccountInstance.getInstance(a);
            if (accountInstance.getUserConfig().isClientActivated()) {
                accountInstance.getMessagesController().checkPromoInfo(true);
            }
        }
    }

    public static int generateClassGuid() {
        int i = lastClassGuid;
        lastClassGuid = i + 1;
        return i;
    }

    public void setIsUpdating(final boolean value) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ConnectionsManager.this.m1370lambda$setIsUpdating$13$orgtelegramtgnetConnectionsManager(value);
            }
        });
    }

    /* renamed from: lambda$setIsUpdating$13$org-telegram-tgnet-ConnectionsManager */
    public /* synthetic */ void m1370lambda$setIsUpdating$13$orgtelegramtgnetConnectionsManager(boolean value) {
        if (this.isUpdating == value) {
            return;
        }
        this.isUpdating = value;
        if (this.connectionState == 3) {
            AccountInstance.getInstance(this.currentAccount).getNotificationCenter().postNotificationName(NotificationCenter.didUpdateConnectionState, new Object[0]);
        }
    }

    protected byte getIpStrategy() {
        if (Build.VERSION.SDK_INT < 19) {
            return (byte) 0;
        }
        if (BuildVars.LOGS_ENABLED) {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.getInterfaceAddresses().isEmpty()) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("valid interface: " + networkInterface);
                        }
                        List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                        for (int a = 0; a < interfaceAddresses.size(); a++) {
                            InterfaceAddress address = interfaceAddresses.get(a);
                            InetAddress inetAddress = address.getAddress();
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("address: " + inetAddress.getHostAddress());
                            }
                            if (!inetAddress.isLinkLocalAddress() && !inetAddress.isLoopbackAddress() && !inetAddress.isMulticastAddress() && BuildVars.LOGS_ENABLED) {
                                FileLog.d("address is good");
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            Enumeration<NetworkInterface> networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            boolean hasStrangeIpv4 = false;
            while (networkInterfaces2.hasMoreElements()) {
                NetworkInterface networkInterface2 = networkInterfaces2.nextElement();
                if (networkInterface2.isUp() && !networkInterface2.isLoopback()) {
                    List<InterfaceAddress> interfaceAddresses2 = networkInterface2.getInterfaceAddresses();
                    for (int a2 = 0; a2 < interfaceAddresses2.size(); a2++) {
                        InterfaceAddress address2 = interfaceAddresses2.get(a2);
                        InetAddress inetAddress2 = address2.getAddress();
                        if (!inetAddress2.isLinkLocalAddress() && !inetAddress2.isLoopbackAddress() && !inetAddress2.isMulticastAddress()) {
                            if (inetAddress2 instanceof Inet6Address) {
                                hasIpv6 = true;
                            } else if (inetAddress2 instanceof Inet4Address) {
                                String addrr = inetAddress2.getHostAddress();
                                if (!addrr.startsWith("192.0.0.")) {
                                    hasIpv4 = true;
                                } else {
                                    hasStrangeIpv4 = true;
                                }
                            }
                        }
                    }
                }
            }
            if (hasIpv6) {
                if (this.forceTryIpV6) {
                    return (byte) 1;
                }
                if (hasStrangeIpv4) {
                    return (byte) 2;
                }
                if (!hasIpv4) {
                    return (byte) 1;
                }
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        return (byte) 0;
    }

    /* loaded from: classes4.dex */
    public static class ResolveHostByNameTask extends AsyncTask<Void, Void, ResolvedDomain> {
        private ArrayList<Long> addresses = new ArrayList<>();
        private String currentHostName;

        public ResolveHostByNameTask(String hostName) {
            this.currentHostName = hostName;
        }

        public void addAddress(long address) {
            if (this.addresses.contains(Long.valueOf(address))) {
                return;
            }
            this.addresses.add(Long.valueOf(address));
        }

        public ResolvedDomain doInBackground(Void... voids) {
            InputStream httpConnectionStream;
            ByteArrayOutputStream outbuf;
            JSONObject jsonObject;
            JSONArray array;
            int len;
            boolean done = false;
            try {
                URL downloadUrl = new URL("https://www.google.com/resolve?name=" + this.currentHostName + "&type=A");
                URLConnection httpConnection = downloadUrl.openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("Host", "dns.google.com");
                httpConnection.setConnectTimeout(1000);
                httpConnection.setReadTimeout(2000);
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (true) {
                    int read = httpConnectionStream.read(data);
                    if (read <= 0) {
                        break;
                    }
                    outbuf.write(data, 0, read);
                }
                jsonObject = new JSONObject(new String(outbuf.toByteArray()));
            } catch (Exception e) {
            }
            if (jsonObject.has("Answer") && (len = (array = jsonObject.getJSONArray("Answer")).length()) > 0) {
                ArrayList<String> addresses = new ArrayList<>(len);
                for (int a = 0; a < len; a++) {
                    addresses.add(array.getJSONObject(a).getString("data"));
                }
                ResolvedDomain resolvedDomain = new ResolvedDomain(addresses, SystemClock.elapsedRealtime());
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e2) {
                        FileLog.e(e2, false);
                    }
                }
                try {
                    outbuf.close();
                } catch (Exception e3) {
                }
                return resolvedDomain;
            }
            done = true;
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e4) {
                    FileLog.e(e4, false);
                }
            }
            outbuf.close();
            if (done) {
                return null;
            }
            try {
                InetAddress address = InetAddress.getByName(this.currentHostName);
                ArrayList<String> addresses2 = new ArrayList<>(1);
                addresses2.add(address.getHostAddress());
                return new ResolvedDomain(addresses2, SystemClock.elapsedRealtime());
            } catch (Exception e5) {
                FileLog.e((Throwable) e5, false);
                return null;
            }
        }

        public void onPostExecute(ResolvedDomain result) {
            if (result != null) {
                ConnectionsManager.dnsCache.put(this.currentHostName, result);
                int N = this.addresses.size();
                for (int a = 0; a < N; a++) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(a).longValue(), result.getAddress());
                }
            } else {
                int N2 = this.addresses.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    ConnectionsManager.native_onHostNameResolved(this.currentHostName, this.addresses.get(a2).longValue(), "");
                }
            }
            ConnectionsManager.resolvingHostnameTasks.remove(this.currentHostName);
        }
    }

    /* loaded from: classes4.dex */
    public static class DnsTxtLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public DnsTxtLoadTask(int instance) {
            this.currentAccount = instance;
        }

        public NativeByteBuffer doInBackground(Void... voids) {
            String googleDomain;
            String str;
            Throwable e;
            ArrayList<String> arrayList;
            int read;
            DnsTxtLoadTask dnsTxtLoadTask = this;
            String str2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            int i = 0;
            InputStream httpConnectionStream = null;
            ByteArrayOutputStream outbuf = null;
            while (i < 3) {
                if (i == 0) {
                    googleDomain = "www.google.com";
                } else if (i == 1) {
                    googleDomain = "www.google.ru";
                } else {
                    googleDomain = "google.com";
                }
                try {
                    String domain = ConnectionsManager.native_isTestBackend(dnsTxtLoadTask.currentAccount) != 0 ? "tapv3.stel.com" : AccountInstance.getInstance(dnsTxtLoadTask.currentAccount).getMessagesController().dcDomainName;
                    int len = Utilities.random.nextInt(116) + 13;
                    StringBuilder padding = new StringBuilder(len);
                    for (int a = 0; a < len; a++) {
                        padding.append(str2.charAt(Utilities.random.nextInt(str2.length())));
                    }
                    URL downloadUrl = new URL("https://" + googleDomain + "/resolve?name=" + domain + "&type=ANY&random_padding=" + ((Object) padding));
                    URLConnection httpConnection = downloadUrl.openConnection();
                    httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    httpConnection.addRequestProperty("Host", "dns.google.com");
                    httpConnection.setConnectTimeout(5000);
                    httpConnection.setReadTimeout(5000);
                    httpConnection.connect();
                    httpConnectionStream = httpConnection.getInputStream();
                    dnsTxtLoadTask.responseDate = (int) (httpConnection.getDate() / 1000);
                    outbuf = new ByteArrayOutputStream();
                    byte[] data = new byte[32768];
                    while (!isCancelled() && (read = httpConnectionStream.read(data)) > 0) {
                        outbuf.write(data, 0, read);
                    }
                    JSONObject jsonObject = new JSONObject(new String(outbuf.toByteArray()));
                    JSONArray array = jsonObject.getJSONArray("Answer");
                    int len2 = array.length();
                    ArrayList<String> arrayList2 = new ArrayList<>(len2);
                    int a2 = 0;
                    while (a2 < len2) {
                        JSONObject object = array.getJSONObject(a2);
                        str = str2;
                        try {
                            int type = object.getInt(CommonProperties.TYPE);
                            JSONObject jsonObject2 = jsonObject;
                            if (type != 16) {
                                arrayList = arrayList2;
                            } else {
                                arrayList = arrayList2;
                                arrayList.add(object.getString("data"));
                            }
                            a2++;
                            arrayList2 = arrayList;
                            str2 = str;
                            jsonObject = jsonObject2;
                        } catch (Throwable th) {
                            e = th;
                            try {
                                FileLog.e(e, false);
                                if (httpConnectionStream != null) {
                                    try {
                                        httpConnectionStream.close();
                                    } catch (Throwable e2) {
                                        FileLog.e(e2, false);
                                    }
                                }
                                if (outbuf != null) {
                                    try {
                                        outbuf.close();
                                    } catch (Exception e3) {
                                    }
                                }
                                i++;
                                dnsTxtLoadTask = this;
                                str2 = str;
                            } catch (Throwable th2) {
                                if (httpConnectionStream != null) {
                                    try {
                                        httpConnectionStream.close();
                                    } catch (Throwable e4) {
                                        FileLog.e(e4, false);
                                    }
                                }
                                if (outbuf != null) {
                                    try {
                                        outbuf.close();
                                    } catch (Exception e5) {
                                    }
                                }
                                throw th2;
                            }
                        }
                    }
                    str = str2;
                    ArrayList<String> arrayList3 = arrayList2;
                    Collections.sort(arrayList3, ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda1.INSTANCE);
                    StringBuilder builder = new StringBuilder();
                    int a3 = 0;
                    while (a3 < arrayList3.size()) {
                        builder.append(arrayList3.get(a3).replace("\"", ""));
                        a3++;
                        arrayList3 = arrayList3;
                        googleDomain = googleDomain;
                    }
                    byte[] bytes = Base64.decode(builder.toString(), 0);
                    NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                    buffer.writeBytes(bytes);
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e6) {
                            FileLog.e(e6, false);
                        }
                    }
                    try {
                        outbuf.close();
                    } catch (Exception e7) {
                    }
                    return buffer;
                } catch (Throwable th3) {
                    e = th3;
                    str = str2;
                }
            }
            return null;
        }

        public static /* synthetic */ int lambda$doInBackground$0(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        public void onPostExecute(final NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$DnsTxtLoadTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ConnectionsManager.DnsTxtLoadTask.this.m1371xfe7b2956(result);
                }
            });
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-tgnet-ConnectionsManager$DnsTxtLoadTask */
        public /* synthetic */ void m1371xfe7b2956(NativeByteBuffer result) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get dns txt result");
                FileLog.d("start google task");
            }
            GoogleDnsLoadTask task = new GoogleDnsLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            AsyncTask unused2 = ConnectionsManager.currentTask = task;
        }
    }

    /* loaded from: classes4.dex */
    public static class GoogleDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public GoogleDnsLoadTask(int instance) {
            this.currentAccount = instance;
        }

        public NativeByteBuffer doInBackground(Void... voids) {
            int read;
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                String domain = ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : AccountInstance.getInstance(this.currentAccount).getMessagesController().dcDomainName;
                int len = Utilities.random.nextInt(116) + 13;
                StringBuilder padding = new StringBuilder(len);
                for (int a = 0; a < len; a++) {
                    padding.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(Utilities.random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length())));
                }
                URL downloadUrl = new URL("https://dns.google.com/resolve?name=" + domain + "&type=ANY&random_padding=" + ((Object) padding));
                URLConnection httpConnection = downloadUrl.openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                this.responseDate = (int) (httpConnection.getDate() / 1000);
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (!isCancelled() && (read = httpConnectionStream.read(data)) > 0) {
                    outbuf.write(data, 0, read);
                }
                JSONObject jsonObject = new JSONObject(new String(outbuf.toByteArray()));
                JSONArray array = jsonObject.getJSONArray("Answer");
                int len2 = array.length();
                ArrayList<String> arrayList = new ArrayList<>(len2);
                for (int a2 = 0; a2 < len2; a2++) {
                    JSONObject object = array.getJSONObject(a2);
                    int type = object.getInt(CommonProperties.TYPE);
                    if (type == 16) {
                        arrayList.add(object.getString("data"));
                    }
                }
                Collections.sort(arrayList, ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda1.INSTANCE);
                StringBuilder builder = new StringBuilder();
                int a3 = 0;
                while (a3 < arrayList.size()) {
                    builder.append(arrayList.get(a3).replace("\"", ""));
                    a3++;
                    domain = domain;
                }
                byte[] bytes = Base64.decode(builder.toString(), 0);
                NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                try {
                    outbuf.close();
                } catch (Exception e2) {
                }
                return buffer;
            } catch (Throwable e3) {
                try {
                    FileLog.e(e3);
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e4) {
                            FileLog.e(e4);
                        }
                    }
                    if (outbuf != null) {
                        try {
                            outbuf.close();
                            return null;
                        } catch (Exception e5) {
                            return null;
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e6) {
                            FileLog.e(e6);
                        }
                    }
                    if (outbuf != null) {
                        try {
                            outbuf.close();
                        } catch (Exception e7) {
                        }
                    }
                    throw th;
                }
            }
        }

        public static /* synthetic */ int lambda$doInBackground$0(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        public void onPostExecute(final NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$GoogleDnsLoadTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ConnectionsManager.GoogleDnsLoadTask.this.m1376x742a8d37(result);
                }
            });
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-tgnet-ConnectionsManager$GoogleDnsLoadTask */
        public /* synthetic */ void m1376x742a8d37(NativeByteBuffer result) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), this.responseDate);
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get google result");
                FileLog.d("start mozilla task");
            }
            MozillaDnsLoadTask task = new MozillaDnsLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            AsyncTask unused2 = ConnectionsManager.currentTask = task;
        }
    }

    /* loaded from: classes4.dex */
    public static class MozillaDnsLoadTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private int responseDate;

        public MozillaDnsLoadTask(int instance) {
            this.currentAccount = instance;
        }

        public NativeByteBuffer doInBackground(Void... voids) {
            int read;
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                String domain = ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv3.stel.com" : AccountInstance.getInstance(this.currentAccount).getMessagesController().dcDomainName;
                int len = Utilities.random.nextInt(116) + 13;
                StringBuilder padding = new StringBuilder(len);
                for (int a = 0; a < len; a++) {
                    padding.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(Utilities.random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".length())));
                }
                URL downloadUrl = new URL("https://mozilla.cloudflare-dns.com/dns-query?name=" + domain + "&type=TXT&random_padding=" + ((Object) padding));
                URLConnection httpConnection = downloadUrl.openConnection();
                httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                httpConnection.addRequestProperty("accept", "application/dns-json");
                httpConnection.setConnectTimeout(5000);
                httpConnection.setReadTimeout(5000);
                httpConnection.connect();
                httpConnectionStream = httpConnection.getInputStream();
                this.responseDate = (int) (httpConnection.getDate() / 1000);
                outbuf = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                while (!isCancelled() && (read = httpConnectionStream.read(data)) > 0) {
                    outbuf.write(data, 0, read);
                }
                JSONObject jsonObject = new JSONObject(new String(outbuf.toByteArray()));
                JSONArray array = jsonObject.getJSONArray("Answer");
                int len2 = array.length();
                ArrayList<String> arrayList = new ArrayList<>(len2);
                for (int a2 = 0; a2 < len2; a2++) {
                    JSONObject object = array.getJSONObject(a2);
                    int type = object.getInt(CommonProperties.TYPE);
                    if (type == 16) {
                        arrayList.add(object.getString("data"));
                    }
                }
                Collections.sort(arrayList, ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda1.INSTANCE);
                StringBuilder builder = new StringBuilder();
                int a3 = 0;
                while (a3 < arrayList.size()) {
                    builder.append(arrayList.get(a3).replace("\"", ""));
                    a3++;
                    domain = domain;
                }
                byte[] bytes = Base64.decode(builder.toString(), 0);
                NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                buffer.writeBytes(bytes);
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                try {
                    outbuf.close();
                } catch (Exception e2) {
                }
                return buffer;
            } catch (Throwable e3) {
                try {
                    FileLog.e(e3);
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e4) {
                            FileLog.e(e4);
                        }
                    }
                    if (outbuf != null) {
                        try {
                            outbuf.close();
                            return null;
                        } catch (Exception e5) {
                            return null;
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e6) {
                            FileLog.e(e6);
                        }
                    }
                    if (outbuf != null) {
                        try {
                            outbuf.close();
                        } catch (Exception e7) {
                        }
                    }
                    throw th;
                }
            }
        }

        public static /* synthetic */ int lambda$doInBackground$0(String o1, String o2) {
            int l1 = o1.length();
            int l2 = o2.length();
            if (l1 > l2) {
                return -1;
            }
            if (l1 < l2) {
                return 1;
            }
            return 0;
        }

        public void onPostExecute(final NativeByteBuffer result) {
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$MozillaDnsLoadTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ConnectionsManager.MozillaDnsLoadTask.this.m1377x8645eae8(result);
                }
            });
        }

        /* renamed from: lambda$onPostExecute$1$org-telegram-tgnet-ConnectionsManager$MozillaDnsLoadTask */
        public /* synthetic */ void m1377x8645eae8(NativeByteBuffer result) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            if (result != null) {
                ConnectionsManager.native_applyDnsConfig(this.currentAccount, result.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), this.responseDate);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get mozilla txt result");
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class FirebaseTask extends AsyncTask<Void, Void, NativeByteBuffer> {
        private int currentAccount;
        private FirebaseRemoteConfig firebaseRemoteConfig;

        public FirebaseTask(int instance) {
            this.currentAccount = instance;
        }

        public NativeByteBuffer doInBackground(Void... voids) {
            try {
                if (ConnectionsManager.native_isTestBackend(this.currentAccount) != 0) {
                    throw new Exception("test backend");
                }
                FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                this.firebaseRemoteConfig = firebaseRemoteConfig;
                String currentValue = firebaseRemoteConfig.getString("ipconfigv3");
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("current firebase value = " + currentValue);
                }
                this.firebaseRemoteConfig.fetch(0L).addOnCompleteListener(new OnCompleteListener() { // from class: org.telegram.tgnet.ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda1
                    @Override // com.google.android.gms.tasks.OnCompleteListener
                    public final void onComplete(Task task) {
                        ConnectionsManager.FirebaseTask.this.m1374xe0d4db4d(task);
                    }
                });
                return null;
            } catch (Throwable e) {
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ConnectionsManager.FirebaseTask.this.m1375xead75ac();
                    }
                });
                FileLog.e(e);
                return null;
            }
        }

        /* renamed from: lambda$doInBackground$2$org-telegram-tgnet-ConnectionsManager$FirebaseTask */
        public /* synthetic */ void m1374xe0d4db4d(Task finishedTask) {
            final boolean success = finishedTask.isSuccessful();
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.tgnet.ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ConnectionsManager.FirebaseTask.this.m1373xb2fc40ee(success);
                }
            });
        }

        /* renamed from: lambda$doInBackground$1$org-telegram-tgnet-ConnectionsManager$FirebaseTask */
        public /* synthetic */ void m1373xb2fc40ee(boolean success) {
            if (success) {
                this.firebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener() { // from class: org.telegram.tgnet.ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda0
                    @Override // com.google.android.gms.tasks.OnCompleteListener
                    public final void onComplete(Task task) {
                        ConnectionsManager.FirebaseTask.this.m1372x8523a68f(task);
                    }
                });
            }
        }

        /* renamed from: lambda$doInBackground$0$org-telegram-tgnet-ConnectionsManager$FirebaseTask */
        public /* synthetic */ void m1372x8523a68f(Task finishedTask2) {
            AsyncTask unused = ConnectionsManager.currentTask = null;
            String config = this.firebaseRemoteConfig.getString("ipconfigv3");
            if (!TextUtils.isEmpty(config)) {
                byte[] bytes = Base64.decode(config, 0);
                try {
                    NativeByteBuffer buffer = new NativeByteBuffer(bytes.length);
                    buffer.writeBytes(bytes);
                    int date = (int) (this.firebaseRemoteConfig.getInfo().getFetchTimeMillis() / 1000);
                    ConnectionsManager.native_applyDnsConfig(this.currentAccount, buffer.address, AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientPhone(), date);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get firebase result");
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask task = new DnsTxtLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            AsyncTask unused2 = ConnectionsManager.currentTask = task;
        }

        /* renamed from: lambda$doInBackground$3$org-telegram-tgnet-ConnectionsManager$FirebaseTask */
        public /* synthetic */ void m1375xead75ac() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to get firebase result");
                FileLog.d("start dns txt task");
            }
            DnsTxtLoadTask task = new DnsTxtLoadTask(this.currentAccount);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            AsyncTask unused = ConnectionsManager.currentTask = task;
        }

        public void onPostExecute(NativeByteBuffer result) {
        }
    }
}
