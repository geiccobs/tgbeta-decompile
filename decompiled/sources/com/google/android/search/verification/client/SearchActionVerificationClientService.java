package com.google.android.search.verification.client;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.android.search.verification.api.ISearchActionVerificationService;
/* loaded from: classes3.dex */
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

    public abstract void performAction(Intent intent, boolean isVerified, Bundle options) throws Exception;

    public long getConnectionTimeout() {
        return 1000L;
    }

    public boolean isTestingMode() {
        return false;
    }

    private boolean isDebugMode() {
        return isTestingMode() || !"user".equals(Build.TYPE);
    }

    public SearchActionVerificationClientService() {
        super("SearchActionVerificationClientService");
        Intent intent = new Intent(REMOTE_GSA_SERVICE_ACTION).setPackage(SearchActionVerificationClientUtil.SEARCH_APP_PACKAGE);
        this.gsaServiceIntent = intent;
        Intent intent2 = new Intent(REMOTE_ASSISTANT_GO_SERVICE_ACTION).setPackage(SearchActionVerificationClientUtil.ASSISTANT_GO_PACKAGE);
        this.assistantGoServiceIntent = intent2;
        if (isTestingMode()) {
            intent.setPackage(SearchActionVerificationClientUtil.TESTING_APP_PACKAGE);
            intent2.setPackage(SearchActionVerificationClientUtil.TESTING_APP_PACKAGE);
        }
        this.connectionTimeout = getConnectionTimeout();
    }

    /* loaded from: classes3.dex */
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
        boolean isGsaInstalled = isPackageInstalled(SearchActionVerificationClientUtil.SEARCH_APP_PACKAGE);
        boolean isGsaReady = !isGsaInstalled || this.searchActionVerificationServiceConnection.isConnected();
        if (this.dbg) {
            Log.d(TAG, String.format("GSA app %s installed: %s connected %s", SearchActionVerificationClientUtil.SEARCH_APP_PACKAGE, Boolean.valueOf(isGsaInstalled), Boolean.valueOf(this.searchActionVerificationServiceConnection.isConnected())));
        }
        boolean isAssistantGoInstalled = isPackageInstalled(SearchActionVerificationClientUtil.ASSISTANT_GO_PACKAGE);
        boolean isAssistantGoReady = !isAssistantGoInstalled || this.assistantGoVerificationServiceConnection.isConnected();
        if (this.dbg) {
            Log.d(TAG, String.format("AssistantGo app %s installed: %s connected %s", SearchActionVerificationClientUtil.ASSISTANT_GO_PACKAGE, Boolean.valueOf(isAssistantGoInstalled), Boolean.valueOf(this.assistantGoVerificationServiceConnection.isConnected())));
        }
        return isGsaReady && isAssistantGoReady;
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
            if (info != null && info.applicationInfo != null) {
                if (info.applicationInfo.enabled) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.format("Couldn't find package name %s", packageName), e);
            return false;
        }
    }

    private boolean maybePerformActionIfVerified(String packageName, Intent intent, SearchActionVerificationServiceConnection searchActionVerificationServiceConnection) {
        int i = 0;
        if (!packageName.equals(SearchActionVerificationClientUtil.SEARCH_APP_PACKAGE) && !packageName.equals(SearchActionVerificationClientUtil.ASSISTANT_GO_PACKAGE)) {
            if (this.dbg) {
                Log.d(TAG, String.format("Unsupported package %s for verification.", packageName));
            }
            return false;
        }
        boolean isSafe = isDebugMode() || SearchActionVerificationClientUtil.isPackageGoogleSigned(this, packageName);
        if (!isSafe) {
            if (this.dbg) {
                Log.d(TAG, String.format("Cannot verify the intent with package %s in unsafe mode.", packageName));
            }
            return false;
        } else if (!intent.hasExtra(EXTRA_INTENT)) {
            if (this.dbg) {
                String valueOf = String.valueOf(intent);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 28);
                sb.append("No extra, nothing to check: ");
                sb.append(valueOf);
                Log.d(TAG, sb.toString());
            }
            return false;
        } else {
            Intent extraIntent = (Intent) intent.getParcelableExtra(EXTRA_INTENT);
            if (this.dbg) {
                SearchActionVerificationClientUtil.logIntentWithExtras(extraIntent);
            }
            boolean finalResult = false;
            String errorMessage = "";
            if (searchActionVerificationServiceConnection.isConnected()) {
                try {
                    Log.i(TAG, String.format("%s Service API version: %s", packageName, Integer.valueOf(searchActionVerificationServiceConnection.getRemoteService().getVersion())));
                    Bundle options = new Bundle();
                    boolean isVerified = searchActionVerificationServiceConnection.isVerified(extraIntent, options);
                    performAction(extraIntent, isVerified, options);
                    finalResult = isVerified;
                } catch (RemoteException exception) {
                    String valueOf2 = String.valueOf(exception.getMessage());
                    Log.e(TAG, valueOf2.length() != 0 ? "Remote exception: ".concat(valueOf2) : new String("Remote exception: "));
                    errorMessage = exception.getMessage();
                } catch (Exception exception2) {
                    String valueOf3 = String.valueOf(exception2.getMessage());
                    Log.e(TAG, valueOf3.length() != 0 ? "Exception: ".concat(valueOf3) : new String("Exception: "));
                    errorMessage = exception2.getMessage();
                }
            } else {
                Log.e(TAG, String.format("VerificationService is not connected to %s, unable to check intent: %s", packageName, intent));
                errorMessage = "VerificationService is not connected to %s, unable to check intent: %s";
            }
            if (extraIntent.hasExtra(SEND_MESSAGE_RESULT_RECEIVER)) {
                ResultReceiver resultReceiver = (ResultReceiver) extraIntent.getExtras().getParcelable(SEND_MESSAGE_RESULT_RECEIVER);
                Bundle bundle = new Bundle();
                bundle.putString(SEND_MESSAGE_ERROR_MESSAGE, errorMessage);
                if (!finalResult) {
                    i = -1;
                }
                resultReceiver.send(i, bundle);
            }
            return finalResult;
        }
    }

    @Override // android.app.IntentService
    protected final void onHandleIntent(Intent intent) {
        if (intent == null) {
            if (this.dbg) {
                Log.d(TAG, "Unable to verify null intent");
                return;
            }
            return;
        }
        long startTime = System.nanoTime();
        while (!installedServicesConnected() && System.nanoTime() - startTime < this.connectionTimeout * 1000000) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException exception) {
                if (this.dbg) {
                    String valueOf = String.valueOf(exception);
                    StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 33);
                    sb.append("Unexpected InterruptedException: ");
                    sb.append(valueOf);
                    Log.d(TAG, sb.toString());
                }
            }
        }
        boolean verifiedByGsa = maybePerformActionIfVerified(SearchActionVerificationClientUtil.SEARCH_APP_PACKAGE, intent, this.searchActionVerificationServiceConnection);
        if (verifiedByGsa) {
            Log.i(TAG, "Verified the intent with GSA.");
            return;
        }
        Log.i(TAG, "Unable to verify the intent with GSA.");
        boolean verifiedByAssistantGo = maybePerformActionIfVerified(SearchActionVerificationClientUtil.ASSISTANT_GO_PACKAGE, intent, this.assistantGoVerificationServiceConnection);
        if (verifiedByAssistantGo) {
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
        if (isPackageSafe(SearchActionVerificationClientUtil.SEARCH_APP_PACKAGE)) {
            bindService(this.gsaServiceIntent, this.searchActionVerificationServiceConnection, 1);
        }
        this.assistantGoVerificationServiceConnection = new SearchActionVerificationServiceConnection();
        if (isPackageSafe(SearchActionVerificationClientUtil.ASSISTANT_GO_PACKAGE)) {
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID).setGroup(NOTIFICATION_CHANNEL_ID).setContentTitle(getApplicationContext().getResources().getString(R.string.google_assistant_verification_notification_title)).setSmallIcon(17301545).setPriority(-2).setVisibility(1);
        startForeground(10000, notificationBuilder.build());
    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getApplicationContext().getResources().getString(R.string.google_assistant_verification_channel_name), 2);
        channel.enableVibration(false);
        channel.enableLights(false);
        channel.setShowBadge(false);
        ((NotificationManager) getApplicationContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);
    }
}
