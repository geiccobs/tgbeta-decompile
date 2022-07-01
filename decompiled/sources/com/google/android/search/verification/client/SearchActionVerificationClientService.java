package com.google.android.search.verification.client;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.android.search.verification.api.ISearchActionVerificationService;
/* loaded from: classes.dex */
public abstract class SearchActionVerificationClientService extends IntentService {
    private static final int CONNECTION_TIMEOUT_IN_MS = 1000;
    public static final String EXTRA_INTENT = "SearchActionVerificationClientExtraIntent";
    private static final long MS_TO_NS = 1000000;
    private static final String NOTIFICATION_CHANNEL_ID = "Assistant_verifier";
    private static final int NOTIFICATION_ID = 10000;
    private static final String REMOTE_ASSISTANT_GO_SERVICE_ACTION = "com.google.android.apps.assistant.go.verification.VERIFICATION_SERVICE";
    private static final String REMOTE_GSA_SERVICE_ACTION = "com.google.android.googlequicksearchbox.SEARCH_ACTION_VERIFICATION_SERVICE";
    private static final String SEND_MESSAGE_ERROR_MESSAGE = "com.google.android.voicesearch.extra.ERROR_MESSAGE";
    private static final String SEND_MESSAGE_RESULT_RECEIVER = "com.google.android.voicesearch.extra.SEND_MESSAGE_RESULT_RECEIVER";
    private static final String TAG = "SAVerificationClientS";
    private static final int TIME_TO_SLEEP_IN_MS = 50;
    private final Intent assistantGoServiceIntent;
    private SearchActionVerificationServiceConnection assistantGoVerificationServiceConnection;
    private final long connectionTimeout;
    private final boolean dbg = isDebugMode();
    private final Intent gsaServiceIntent;
    private SearchActionVerificationServiceConnection searchActionVerificationServiceConnection;

    public long getConnectionTimeout() {
        return 1000L;
    }

    public boolean isTestingMode() {
        return false;
    }

    public abstract void performAction(Intent intent, boolean isVerified, Bundle options) throws Exception;

    private boolean isDebugMode() {
        return isTestingMode() || !"user".equals(Build.TYPE);
    }

    public SearchActionVerificationClientService() {
        super("SearchActionVerificationClientService");
        Intent intent = new Intent(REMOTE_GSA_SERVICE_ACTION).setPackage("com.google.android.googlequicksearchbox");
        this.gsaServiceIntent = intent;
        Intent intent2 = new Intent(REMOTE_ASSISTANT_GO_SERVICE_ACTION).setPackage("com.google.android.apps.assistant");
        this.assistantGoServiceIntent = intent2;
        if (isTestingMode()) {
            intent.setPackage("com.google.verificationdemo.fakeverification");
            intent2.setPackage("com.google.verificationdemo.fakeverification");
        }
        this.connectionTimeout = getConnectionTimeout();
    }

    /* loaded from: classes.dex */
    public class SearchActionVerificationServiceConnection implements ServiceConnection {
        private ISearchActionVerificationService iRemoteService;

        SearchActionVerificationServiceConnection() {
            SearchActionVerificationClientService.this = this$0;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            if (SearchActionVerificationClientService.this.dbg) {
                Log.d(SearchActionVerificationClientService.TAG, "onServiceConnected");
            }
            this.iRemoteService = ISearchActionVerificationService.Stub.asInterface(binder);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            this.iRemoteService = null;
            if (SearchActionVerificationClientService.this.dbg) {
                Log.d(SearchActionVerificationClientService.TAG, "onServiceDisconnected");
            }
        }

        public ISearchActionVerificationService getRemoteService() {
            return this.iRemoteService;
        }

        public boolean isVerified(Intent intent, Bundle options) throws RemoteException {
            ISearchActionVerificationService iSearchActionVerificationService = this.iRemoteService;
            return iSearchActionVerificationService != null && iSearchActionVerificationService.isSearchAction(intent, options);
        }

        public boolean isConnected() {
            return this.iRemoteService != null;
        }
    }

    private boolean isPackageSafe(String packageName) {
        return isPackageInstalled(packageName) && (isDebugMode() || SearchActionVerificationClientUtil.isPackageGoogleSigned(this, packageName));
    }

    private boolean installedServicesConnected() {
        boolean isPackageInstalled = isPackageInstalled("com.google.android.googlequicksearchbox");
        boolean z = !isPackageInstalled || this.searchActionVerificationServiceConnection.isConnected();
        if (this.dbg) {
            Log.d(TAG, String.format("GSA app %s installed: %s connected %s", "com.google.android.googlequicksearchbox", Boolean.valueOf(isPackageInstalled), Boolean.valueOf(this.searchActionVerificationServiceConnection.isConnected())));
        }
        boolean isPackageInstalled2 = isPackageInstalled("com.google.android.apps.assistant");
        boolean z2 = !isPackageInstalled2 || this.assistantGoVerificationServiceConnection.isConnected();
        if (this.dbg) {
            Log.d(TAG, String.format("AssistantGo app %s installed: %s connected %s", "com.google.android.apps.assistant", Boolean.valueOf(isPackageInstalled2), Boolean.valueOf(this.assistantGoVerificationServiceConnection.isConnected())));
        }
        return z && z2;
    }

    private boolean isPackageInstalled(String packageName) {
        ApplicationInfo applicationInfo;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo != null && (applicationInfo = packageInfo.applicationInfo) != null) {
                if (applicationInfo.enabled) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.format("Couldn't find package name %s", packageName), e);
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0113  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean maybePerformActionIfVerified(java.lang.String r8, android.content.Intent r9, com.google.android.search.verification.client.SearchActionVerificationClientService.SearchActionVerificationServiceConnection r10) {
        /*
            Method dump skipped, instructions count: 303
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.search.verification.client.SearchActionVerificationClientService.maybePerformActionIfVerified(java.lang.String, android.content.Intent, com.google.android.search.verification.client.SearchActionVerificationClientService$SearchActionVerificationServiceConnection):boolean");
    }

    @Override // android.app.IntentService
    protected final void onHandleIntent(Intent intent) {
        if (intent == null) {
            if (!this.dbg) {
                return;
            }
            Log.d(TAG, "Unable to verify null intent");
            return;
        }
        long nanoTime = System.nanoTime();
        while (!installedServicesConnected() && System.nanoTime() - nanoTime < this.connectionTimeout * MS_TO_NS) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                if (this.dbg) {
                    String valueOf = String.valueOf(e);
                    StringBuilder sb = new StringBuilder(valueOf.length() + 33);
                    sb.append("Unexpected InterruptedException: ");
                    sb.append(valueOf);
                    Log.d(TAG, sb.toString());
                }
            }
        }
        if (maybePerformActionIfVerified("com.google.android.googlequicksearchbox", intent, this.searchActionVerificationServiceConnection)) {
            Log.i(TAG, "Verified the intent with GSA.");
            return;
        }
        Log.i(TAG, "Unable to verify the intent with GSA.");
        if (maybePerformActionIfVerified("com.google.android.apps.assistant", intent, this.assistantGoVerificationServiceConnection)) {
            Log.i(TAG, "Verified the intent with Assistant Go.");
        } else {
            Log.i(TAG, "Unable to verify the intent with Assistant Go.");
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public final void onCreate() {
        if (this.dbg) {
            Log.d(TAG, "onCreate");
        }
        super.onCreate();
        this.searchActionVerificationServiceConnection = new SearchActionVerificationServiceConnection();
        if (isPackageSafe("com.google.android.googlequicksearchbox")) {
            bindService(this.gsaServiceIntent, this.searchActionVerificationServiceConnection, 1);
        }
        this.assistantGoVerificationServiceConnection = new SearchActionVerificationServiceConnection();
        if (isPackageSafe("com.google.android.apps.assistant")) {
            bindService(this.assistantGoServiceIntent, this.assistantGoVerificationServiceConnection, 1);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            postForegroundNotification();
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public final void onDestroy() {
        if (this.dbg) {
            Log.d(TAG, "onDestroy");
        }
        super.onDestroy();
        if (this.searchActionVerificationServiceConnection.isConnected()) {
            unbindService(this.searchActionVerificationServiceConnection);
        }
        if (this.assistantGoVerificationServiceConnection.isConnected()) {
            unbindService(this.assistantGoVerificationServiceConnection);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            stopForeground(true);
        }
    }

    protected void postForegroundNotification() {
        createChannel();
        startForeground(NOTIFICATION_ID, new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID).setGroup(NOTIFICATION_CHANNEL_ID).setContentTitle(getApplicationContext().getResources().getString(R$string.google_assistant_verification_notification_title)).setSmallIcon(17301545).setPriority(-2).setVisibility(1).build());
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getApplicationContext().getResources().getString(R$string.google_assistant_verification_channel_name), 2);
        notificationChannel.enableVibration(false);
        notificationChannel.enableLights(false);
        notificationChannel.setShowBadge(false);
        ((NotificationManager) getApplicationContext().getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
    }
}
