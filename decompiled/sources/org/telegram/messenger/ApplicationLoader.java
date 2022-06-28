package org.telegram.messenger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDex;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.File;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.LauncherIconController;
/* loaded from: classes.dex */
public class ApplicationLoader extends Application {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    public static boolean canDrawOverlays;
    private static ConnectivityManager connectivityManager;
    public static volatile NetworkInfo currentNetworkInfo;
    public static boolean hasPlayServices;
    private static long lastNetworkCheckTypeTime;
    public static volatile long mainInterfacePausedStageQueueTime;
    private static volatile ConnectivityManager.NetworkCallback networkCallback;
    public static long startTime;
    private static volatile boolean applicationInited = false;
    private static int lastKnownNetworkType = -1;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfaceStopped = true;
    public static volatile boolean externalInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;

    @Override // android.content.ContextWrapper
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static File getFilesDirFixed() {
        for (int a = 0; a < 10; a++) {
            File path = applicationContext.getFilesDir();
            if (path != null) {
                return path;
            }
        }
        try {
            ApplicationInfo info = applicationContext.getApplicationInfo();
            File path2 = new File(info.dataDir, "files");
            path2.mkdirs();
            return path2;
        } catch (Exception e) {
            FileLog.e(e);
            return new File("/data/data/org.telegram.messenger/files");
        }
    }

    public static void postInitApplication() {
        if (applicationInited || applicationContext == null) {
            return;
        }
        applicationInited = true;
        try {
            LocaleController.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
            BroadcastReceiver networkStateReceiver = new BroadcastReceiver() { // from class: org.telegram.messenger.ApplicationLoader.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    try {
                        ApplicationLoader.currentNetworkInfo = ApplicationLoader.connectivityManager.getActiveNetworkInfo();
                    } catch (Throwable th) {
                    }
                    boolean isSlow = ApplicationLoader.isConnectionSlow();
                    for (int a = 0; a < 4; a++) {
                        ConnectionsManager.getInstance(a).checkConnection();
                        FileLoader.getInstance(a).onNetworkChanged(isSlow);
                    }
                }
            };
            applicationContext.registerReceiver(networkStateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
            filter.addAction("android.intent.action.SCREEN_OFF");
            BroadcastReceiver mReceiver = new ScreenReceiver();
            applicationContext.registerReceiver(mReceiver, filter);
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        try {
            PowerManager pm = (PowerManager) applicationContext.getSystemService("power");
            isScreenOn = pm.isScreenOn();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("screen state = " + isScreenOn);
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        SharedConfig.loadConfig();
        SharedPrefsHelper.init(applicationContext);
        for (int a = 0; a < 4; a++) {
            UserConfig.getInstance(a).loadConfig();
            MessagesController.getInstance(a);
            if (a == 0) {
                SharedConfig.pushStringStatus = "__FIREBASE_GENERATING_SINCE_" + ConnectionsManager.getInstance(a).getCurrentTime() + "__";
            } else {
                ConnectionsManager.getInstance(a);
            }
            TLRPC.User user = UserConfig.getInstance(a).getCurrentUser();
            if (user != null) {
                MessagesController.getInstance(a).putUser(user, true);
                SendMessagesHelper.getInstance(a).checkUnsentMessages();
            }
        }
        ApplicationLoader app = (ApplicationLoader) applicationContext;
        app.initPlayServices();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("app initied");
        }
        MediaController.getInstance();
        for (int a2 = 0; a2 < 4; a2++) {
            ContactsController.getInstance(a2).checkAppAccount();
            DownloadController.getInstance(a2);
        }
        ChatThemeController.init();
        BillingController.getInstance().startConnection();
    }

    @Override // android.app.Application
    public void onCreate() {
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable th) {
        }
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder sb = new StringBuilder();
            sb.append("app start time = ");
            long elapsedRealtime = SystemClock.elapsedRealtime();
            startTime = elapsedRealtime;
            sb.append(elapsedRealtime);
            FileLog.d(sb.toString());
        }
        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }
        NativeLoader.initNativeLibs(applicationContext);
        ConnectionsManager.native_setJava(false);
        new ForegroundDetector(this) { // from class: org.telegram.messenger.ApplicationLoader.2
            @Override // org.telegram.ui.Components.ForegroundDetector, android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStarted(Activity activity) {
                boolean wasInBackground = isBackground();
                super.onActivityStarted(activity);
                if (wasInBackground) {
                    ApplicationLoader.ensureCurrentNetworkGet(true);
                }
            }
        };
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load libs time = " + (SystemClock.elapsedRealtime() - startTime));
        }
        applicationHandler = new Handler(applicationContext.getMainLooper());
        AndroidUtilities.runOnUIThread(ApplicationLoader$$ExternalSyntheticLambda3.INSTANCE);
        LauncherIconController.tryFixLauncherIconIfNeeded();
    }

    public static void startPushService() {
        boolean enabled;
        SharedPreferences preferences = MessagesController.getGlobalNotificationsSettings();
        if (preferences.contains("pushService")) {
            enabled = preferences.getBoolean("pushService", true);
        } else {
            enabled = MessagesController.getMainSettings(UserConfig.selectedAccount).getBoolean("keepAliveService", false);
        }
        if (enabled) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
                return;
            } catch (Throwable th) {
                return;
            }
        }
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
        PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
        AlarmManager alarm = (AlarmManager) applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        alarm.cancel(pintent);
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
            VideoCapturerDevice.checkScreenCapturerSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ApplicationLoader$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ApplicationLoader.this.m112xd905017d();
            }
        }, 1000L);
    }

    /* renamed from: lambda$initPlayServices$2$org-telegram-messenger-ApplicationLoader */
    public /* synthetic */ void m112xd905017d() {
        boolean checkPlayServices = checkPlayServices();
        hasPlayServices = checkPlayServices;
        if (checkPlayServices) {
            String currentPushString = SharedConfig.pushString;
            if (!TextUtils.isEmpty(currentPushString)) {
                if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                    FileLog.d("GCM regId = " + currentPushString);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("GCM Registration not found.");
            }
            Utilities.globalQueue.postRunnable(ApplicationLoader$$ExternalSyntheticLambda2.INSTANCE);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("No valid Google Play Services APK found.");
        }
        SharedConfig.pushStringStatus = "__NO_GOOGLE_PLAY_SERVICES__";
        GcmPushListenerService.sendRegistrationToServer(null);
    }

    public static /* synthetic */ void lambda$initPlayServices$1() {
        try {
            SharedConfig.pushStringGetTimeStart = SystemClock.elapsedRealtime();
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(ApplicationLoader$$ExternalSyntheticLambda0.INSTANCE);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void lambda$initPlayServices$0(Task task) {
        SharedConfig.pushStringGetTimeEnd = SystemClock.elapsedRealtime();
        if (!task.isSuccessful()) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("Failed to get regid");
            }
            SharedConfig.pushStringStatus = "__FIREBASE_FAILED__";
            GcmPushListenerService.sendRegistrationToServer(null);
            return;
        }
        String token = (String) task.getResult();
        if (!TextUtils.isEmpty(token)) {
            GcmPushListenerService.sendRegistrationToServer(token);
        }
    }

    private boolean checkPlayServices() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            return resultCode == 0;
        } catch (Exception e) {
            FileLog.e(e);
            return true;
        }
    }

    public static void ensureCurrentNetworkGet(boolean force) {
        if (force || currentNetworkInfo == null) {
            try {
                if (connectivityManager == null) {
                    connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
                }
                currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (Build.VERSION.SDK_INT >= 24 && networkCallback == null) {
                    networkCallback = new ConnectivityManager.NetworkCallback() { // from class: org.telegram.messenger.ApplicationLoader.3
                        @Override // android.net.ConnectivityManager.NetworkCallback
                        public void onAvailable(Network network) {
                            int unused = ApplicationLoader.lastKnownNetworkType = -1;
                        }

                        @Override // android.net.ConnectivityManager.NetworkCallback
                        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                            int unused = ApplicationLoader.lastKnownNetworkType = -1;
                        }
                    };
                    connectivityManager.registerDefaultNetworkCallback(networkCallback);
                }
            } catch (Throwable th) {
            }
        }
    }

    public static boolean isRoaming() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo == null) {
                return false;
            }
            return currentNetworkInfo.isRoaming();
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && (currentNetworkInfo.getType() == 1 || currentNetworkInfo.getType() == 9)) {
                NetworkInfo.State state = currentNetworkInfo.getState();
                if (state != NetworkInfo.State.CONNECTED && state != NetworkInfo.State.CONNECTING) {
                    if (state == NetworkInfo.State.SUSPENDED) {
                    }
                }
                return true;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectedToWiFi() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && (currentNetworkInfo.getType() == 1 || currentNetworkInfo.getType() == 9)) {
                if (currentNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }

    public static boolean isConnectionSlow() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && currentNetworkInfo.getType() == 0) {
                switch (currentNetworkInfo.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return true;
                }
            }
        } catch (Throwable th) {
        }
        return false;
    }

    public static int getAutodownloadNetworkType() {
        int i;
        try {
            ensureCurrentNetworkGet(false);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (currentNetworkInfo == null) {
            return 0;
        }
        if (currentNetworkInfo.getType() != 1 && currentNetworkInfo.getType() != 9) {
            if (!currentNetworkInfo.isRoaming()) {
                return 0;
            }
            return 2;
        }
        if (Build.VERSION.SDK_INT >= 24 && (((i = lastKnownNetworkType) == 0 || i == 1) && System.currentTimeMillis() - lastNetworkCheckTypeTime < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS)) {
            return lastKnownNetworkType;
        }
        if (connectivityManager.isActiveNetworkMetered()) {
            lastKnownNetworkType = 0;
        } else {
            lastKnownNetworkType = 1;
        }
        lastNetworkCheckTypeTime = System.currentTimeMillis();
        return lastKnownNetworkType;
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return 1;
        }
        if (isRoaming()) {
            return 2;
        }
        return 0;
    }

    public static boolean isNetworkOnlineFast() {
        try {
            ensureCurrentNetworkGet(false);
            if (currentNetworkInfo != null && !currentNetworkInfo.isConnectedOrConnecting() && !currentNetworkInfo.isAvailable()) {
                NetworkInfo netInfo = connectivityManager.getNetworkInfo(0);
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    return true;
                }
                NetworkInfo netInfo2 = connectivityManager.getNetworkInfo(1);
                if (netInfo2 != null) {
                    if (netInfo2.isConnectedOrConnecting()) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return true;
        }
    }

    public static boolean isNetworkOnlineRealtime() {
        try {
            ConnectivityManager connectivityManager2 = (ConnectivityManager) applicationContext.getSystemService("connectivity");
            NetworkInfo netInfo = connectivityManager2.getActiveNetworkInfo();
            if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
                return true;
            }
            NetworkInfo netInfo2 = connectivityManager2.getNetworkInfo(0);
            if (netInfo2 != null && netInfo2.isConnectedOrConnecting()) {
                return true;
            }
            NetworkInfo netInfo3 = connectivityManager2.getNetworkInfo(1);
            if (netInfo3 != null) {
                if (netInfo3.isConnectedOrConnecting()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
            return true;
        }
    }

    public static boolean isNetworkOnline() {
        boolean result = isNetworkOnlineRealtime();
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            boolean result2 = isNetworkOnlineFast();
            if (result != result2) {
                FileLog.d("network online mismatch");
            }
        }
        return result;
    }
}
