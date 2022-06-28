package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;
import com.microsoft.appcenter.AbstractAppCenterService;
import com.microsoft.appcenter.DependencyConfiguration;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.distribute.channel.DistributeInfoTracker;
import com.microsoft.appcenter.distribute.download.ReleaseDownloader;
import com.microsoft.appcenter.distribute.download.ReleaseDownloaderFactory;
import com.microsoft.appcenter.distribute.ingestion.models.DistributionStartSessionLog;
import com.microsoft.appcenter.distribute.ingestion.models.json.DistributionStartSessionLogFactory;
import com.microsoft.appcenter.http.DefaultHttpClient;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpException;
import com.microsoft.appcenter.http.HttpResponse;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.http.ServiceCall;
import com.microsoft.appcenter.http.ServiceCallback;
import com.microsoft.appcenter.ingestion.models.json.LogFactory;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.AppNameHelper;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.HandlerUtils;
import com.microsoft.appcenter.utils.IdHelper;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.microsoft.appcenter.utils.context.SessionContext;
import com.microsoft.appcenter.utils.crypto.CryptoUtils;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
/* loaded from: classes3.dex */
public class Distribute extends AbstractAppCenterService {
    private static final String DISTRIBUTE_GROUP = "group_distribute";
    private static Distribute sInstance;
    private String mAppSecret;
    private boolean mAutomaticCheckForUpdateDisabled;
    private String mBeforeStartDistributionGroupId;
    private String mBeforeStartRequestId;
    private String mBeforeStartTesterAppUpdateSetupFailed;
    private String mBeforeStartUpdateSetupFailed;
    private String mBeforeStartUpdateToken;
    private boolean mBrowserOpenedOrAborted;
    private ServiceCall mCheckReleaseApiCall;
    private Object mCheckReleaseCallId;
    private boolean mCheckedDownload;
    private Dialog mCompletedDownloadDialog;
    private Context mContext;
    private DistributeInfoTracker mDistributeInfoTracker;
    private boolean mEnabledForDebuggableBuild;
    private final Map<String, LogFactory> mFactories;
    private Activity mForegroundActivity;
    private DistributeListener mListener;
    private boolean mManualCheckForUpdateRequested;
    private PackageInfo mPackageInfo;
    private ReleaseDetails mReleaseDetails;
    private ReleaseDownloader mReleaseDownloader;
    private ReleaseDownloadListener mReleaseDownloaderListener;
    private boolean mTesterAppOpenedOrAborted;
    private Dialog mUnknownSourcesDialog;
    private Dialog mUpdateDialog;
    private Dialog mUpdateSetupFailedDialog;
    private Boolean mUsingDefaultUpdateDialog;
    private boolean mWorkflowCompleted;
    private String mInstallUrl = "https://install.appcenter.ms";
    private String mApiUrl = "https://api.appcenter.ms/v0.1";
    private int mUpdateTrack = 1;
    private WeakReference<Activity> mLastActivityWithDialog = new WeakReference<>(null);

    private Distribute() {
        HashMap hashMap = new HashMap();
        this.mFactories = hashMap;
        hashMap.put(DistributionStartSessionLog.TYPE, new DistributionStartSessionLogFactory());
    }

    public static synchronized Distribute getInstance() {
        Distribute distribute;
        synchronized (Distribute.class) {
            if (sInstance == null) {
                sInstance = new Distribute();
            }
            distribute = sInstance;
        }
        return distribute;
    }

    static synchronized void unsetInstance() {
        synchronized (Distribute.class) {
            sInstance = null;
        }
    }

    public static AppCenterFuture<Boolean> isEnabled() {
        return getInstance().isInstanceEnabledAsync();
    }

    public static AppCenterFuture<Void> setEnabled(boolean enabled) {
        return getInstance().setInstanceEnabledAsync(enabled);
    }

    public static void setInstallUrl(String installUrl) {
        getInstance().setInstanceInstallUrl(installUrl);
    }

    public static void setApiUrl(String apiUrl) {
        getInstance().setInstanceApiUrl(apiUrl);
    }

    public static int getUpdateTrack() {
        return getInstance().getInstanceUpdateTrack();
    }

    public static void setUpdateTrack(int updateTrack) {
        getInstance().setInstanceUpdateTrack(updateTrack);
    }

    public static void setListener(DistributeListener listener) {
        getInstance().setInstanceListener(listener);
    }

    public static void setEnabledForDebuggableBuild(boolean enabled) {
        getInstance().setInstanceEnabledForDebuggableBuild(enabled);
    }

    public static void notifyUpdateAction(int updateAction) {
        getInstance().handleUpdateAction(updateAction);
    }

    public static void checkForUpdate() {
        getInstance().instanceCheckForUpdate();
    }

    public static void disableAutomaticCheckForUpdate() {
        getInstance().instanceDisableAutomaticCheckForUpdate();
    }

    private synchronized void instanceDisableAutomaticCheckForUpdate() {
        if (this.mChannel != null) {
            AppCenterLog.error(DistributeConstants.LOG_TAG, "Automatic check for update cannot be disabled after Distribute is started.");
        } else {
            this.mAutomaticCheckForUpdateDisabled = true;
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected String getGroupName() {
        return DISTRIBUTE_GROUP;
    }

    @Override // com.microsoft.appcenter.AppCenterService
    public String getServiceName() {
        return "Distribute";
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected String getLoggerTag() {
        return DistributeConstants.LOG_TAG;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected int getTriggerCount() {
        return 1;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, com.microsoft.appcenter.AppCenterService
    public Map<String, LogFactory> getLogFactories() {
        return this.mFactories;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, com.microsoft.appcenter.AppCenterService
    public synchronized void onStarted(Context context, Channel channel, String appSecret, String transmissionTargetToken, boolean startedFromApp) {
        this.mContext = context;
        this.mAppSecret = appSecret;
        try {
            this.mPackageInfo = context.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            AppCenterLog.error(DistributeConstants.LOG_TAG, "Could not get self package info.", e);
        }
        super.onStarted(context, channel, appSecret, transmissionTargetToken, startedFromApp);
    }

    public synchronized void startFromBackground(Context context) {
        if (this.mAppSecret == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Called before onStart, init storage");
            this.mContext = context;
            SharedPreferencesManager.initialize(context);
            updateReleaseDetails(DistributeUtils.loadCachedReleaseDetails());
        }
    }

    private boolean tryResetWorkflow() {
        if (DistributeUtils.getStoredDownloadState() == 0 && this.mCheckReleaseCallId == null) {
            this.mWorkflowCompleted = false;
            this.mBrowserOpenedOrAborted = false;
            return true;
        }
        return false;
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, android.app.Application.ActivityLifecycleCallbacks
    public synchronized void onActivityResumed(Activity activity) {
        this.mForegroundActivity = activity;
        if (this.mChannel != null) {
            resumeDistributeWorkflow();
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, android.app.Application.ActivityLifecycleCallbacks
    public synchronized void onActivityPaused(Activity activity) {
        this.mForegroundActivity = null;
        ReleaseDownloadListener releaseDownloadListener = this.mReleaseDownloaderListener;
        if (releaseDownloadListener != null) {
            releaseDownloadListener.hideProgressDialog();
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService, com.microsoft.appcenter.utils.ApplicationLifecycleListener.ApplicationLifecycleCallbacks
    public void onApplicationEnterForeground() {
        if (this.mChannel != null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Resetting workflow on entering foreground.");
            tryResetWorkflow();
        }
    }

    @Override // com.microsoft.appcenter.AbstractAppCenterService
    protected synchronized void applyEnabledState(boolean enabled) {
        if (enabled) {
            changeDistributionGroupIdAfterAppUpdateIfNeeded();
            String distributionGroupId = SharedPreferencesManager.getString("Distribute.distribution_group_id");
            this.mDistributeInfoTracker = new DistributeInfoTracker(distributionGroupId);
            this.mChannel.addListener(this.mDistributeInfoTracker);
            resumeWorkflowIfForeground();
        } else {
            this.mTesterAppOpenedOrAborted = false;
            this.mBrowserOpenedOrAborted = false;
            this.mWorkflowCompleted = false;
            cancelPreviousTasks();
            SharedPreferencesManager.remove("Distribute.request_id");
            SharedPreferencesManager.remove("Distribute.postpone_time");
            SharedPreferencesManager.remove("Distribute.update_setup_failed_package_hash");
            SharedPreferencesManager.remove("Distribute.update_setup_failed_message");
            SharedPreferencesManager.remove("Distribute.tester_app_update_setup_failed_message");
            this.mChannel.removeListener(this.mDistributeInfoTracker);
            this.mDistributeInfoTracker = null;
        }
    }

    private void resumeWorkflowIfForeground() {
        if (this.mForegroundActivity != null) {
            HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.distribute.Distribute.1
                @Override // java.lang.Runnable
                public void run() {
                    Distribute.this.resumeDistributeWorkflow();
                }
            });
        } else {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Distribute workflow will be resumed on activity resume event.");
        }
    }

    synchronized void handleUpdateAction(final int updateAction) {
        isInstanceEnabledAsync().thenAccept(new AppCenterConsumer<Boolean>() { // from class: com.microsoft.appcenter.distribute.Distribute.2
            public void accept(Boolean enabled) {
                if (enabled.booleanValue()) {
                    boolean isDownloading = Distribute.this.mReleaseDownloader != null && Distribute.this.mReleaseDownloader.isDownloading();
                    if (DistributeUtils.getStoredDownloadState() == 1 && !isDownloading) {
                        if (!Distribute.this.mUsingDefaultUpdateDialog.booleanValue()) {
                            switch (updateAction) {
                                case -2:
                                    if (Distribute.this.mReleaseDetails.isMandatoryUpdate()) {
                                        AppCenterLog.error(DistributeConstants.LOG_TAG, "Cannot postpone a mandatory update.");
                                        return;
                                    }
                                    Distribute distribute = Distribute.this;
                                    distribute.postponeRelease(distribute.mReleaseDetails);
                                    return;
                                case -1:
                                    Distribute distribute2 = Distribute.this;
                                    distribute2.enqueueDownloadOrShowUnknownSourcesDialog(distribute2.mReleaseDetails);
                                    return;
                                default:
                                    AppCenterLog.error(DistributeConstants.LOG_TAG, "Invalid update action: " + updateAction);
                                    return;
                            }
                        }
                        AppCenterLog.error(DistributeConstants.LOG_TAG, "Cannot handle user update action when using default dialog.");
                        return;
                    }
                    AppCenterLog.error(DistributeConstants.LOG_TAG, "Cannot handle user update action at this time.");
                    return;
                }
                AppCenterLog.error(DistributeConstants.LOG_TAG, "Distribute is disabled");
            }
        });
    }

    private synchronized void setInstanceInstallUrl(String installUrl) {
        this.mInstallUrl = installUrl;
    }

    private synchronized void setInstanceApiUrl(String apiUrl) {
        this.mApiUrl = apiUrl;
    }

    private synchronized int getInstanceUpdateTrack() {
        return this.mUpdateTrack;
    }

    private synchronized void setInstanceUpdateTrack(int updateTrack) {
        if (this.mContext != null) {
            AppCenterLog.error(DistributeConstants.LOG_TAG, "Update track cannot be set after Distribute is started.");
        } else if (DistributeUtils.isInvalidUpdateTrack(updateTrack)) {
            AppCenterLog.error(DistributeConstants.LOG_TAG, "Invalid argument passed to Distribute.setUpdateTrack().");
        } else {
            this.mUpdateTrack = updateTrack;
        }
    }

    private synchronized void setInstanceListener(DistributeListener listener) {
        this.mListener = listener;
    }

    private synchronized void setInstanceEnabledForDebuggableBuild(boolean enabled) {
        this.mEnabledForDebuggableBuild = enabled;
    }

    private void instanceCheckForUpdate() {
        post(new Runnable() { // from class: com.microsoft.appcenter.distribute.Distribute.3
            @Override // java.lang.Runnable
            public void run() {
                Distribute.this.handleCheckForUpdate();
            }
        });
    }

    public synchronized void handleCheckForUpdate() {
        this.mManualCheckForUpdateRequested = true;
        if (tryResetWorkflow()) {
            resumeWorkflowIfForeground();
        } else {
            AppCenterLog.info(DistributeConstants.LOG_TAG, "A check for update is already ongoing.");
        }
    }

    private synchronized void cancelPreviousTasks() {
        ServiceCall serviceCall = this.mCheckReleaseApiCall;
        if (serviceCall != null) {
            serviceCall.cancel();
            this.mCheckReleaseApiCall = null;
        }
        this.mCheckReleaseCallId = null;
        this.mUpdateDialog = null;
        this.mUnknownSourcesDialog = null;
        this.mCompletedDownloadDialog = null;
        this.mUpdateSetupFailedDialog = null;
        this.mLastActivityWithDialog.clear();
        this.mUsingDefaultUpdateDialog = null;
        this.mCheckedDownload = false;
        this.mManualCheckForUpdateRequested = false;
        updateReleaseDetails(null);
        SharedPreferencesManager.remove("Distribute.release_details");
        SharedPreferencesManager.remove("Distribute.download_state");
        SharedPreferencesManager.remove("Distribute.download_time");
    }

    public synchronized void resumeDistributeWorkflow() {
        String updateSetupFailedPackageHash;
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Resume distribute workflow...");
        if (this.mPackageInfo != null && this.mForegroundActivity != null && !this.mWorkflowCompleted && isInstanceEnabled()) {
            boolean shouldUseTesterAppForUpdateSetup = false;
            if ((this.mContext.getApplicationInfo().flags & 2) == 2 && !this.mEnabledForDebuggableBuild) {
                AppCenterLog.info(DistributeConstants.LOG_TAG, "Not checking for in-app updates in debuggable build.");
                this.mWorkflowCompleted = true;
                this.mManualCheckForUpdateRequested = false;
            } else if (InstallerUtils.isInstalledFromAppStore(DistributeConstants.LOG_TAG, this.mContext)) {
                AppCenterLog.info(DistributeConstants.LOG_TAG, "Not checking in app updates as installed from a store.");
                this.mWorkflowCompleted = true;
                this.mManualCheckForUpdateRequested = false;
            } else {
                boolean isPublicTrack = this.mUpdateTrack == 1;
                if (!isPublicTrack && (updateSetupFailedPackageHash = SharedPreferencesManager.getString("Distribute.update_setup_failed_package_hash")) != null) {
                    String releaseHash = DistributeUtils.computeReleaseHash(this.mPackageInfo);
                    if (releaseHash.equals(updateSetupFailedPackageHash)) {
                        AppCenterLog.info(DistributeConstants.LOG_TAG, "Skipping in-app updates setup, because it already failed on this release before.");
                        return;
                    }
                    AppCenterLog.info(DistributeConstants.LOG_TAG, "Re-attempting in-app updates setup and cleaning up failure info from storage.");
                    SharedPreferencesManager.remove("Distribute.update_setup_failed_package_hash");
                    SharedPreferencesManager.remove("Distribute.update_setup_failed_message");
                    SharedPreferencesManager.remove("Distribute.tester_app_update_setup_failed_message");
                }
                String str = null;
                if (this.mBeforeStartRequestId != null) {
                    AppCenterLog.debug(DistributeConstants.LOG_TAG, "Processing redirection parameters we kept in memory before onStarted");
                    String str2 = this.mBeforeStartDistributionGroupId;
                    if (str2 != null) {
                        storeRedirectionParameters(this.mBeforeStartRequestId, str2, this.mBeforeStartUpdateToken);
                    } else {
                        String str3 = this.mBeforeStartUpdateSetupFailed;
                        if (str3 != null) {
                            storeUpdateSetupFailedParameter(this.mBeforeStartRequestId, str3);
                        }
                    }
                    String str4 = this.mBeforeStartTesterAppUpdateSetupFailed;
                    if (str4 != null) {
                        storeTesterAppUpdateSetupFailedParameter(this.mBeforeStartRequestId, str4);
                    }
                    this.mBeforeStartRequestId = null;
                    this.mBeforeStartDistributionGroupId = null;
                    this.mBeforeStartUpdateToken = null;
                    this.mBeforeStartUpdateSetupFailed = null;
                    this.mBeforeStartTesterAppUpdateSetupFailed = null;
                    return;
                }
                int downloadState = DistributeUtils.getStoredDownloadState();
                if (this.mReleaseDetails == null && downloadState != 0) {
                    updateReleaseDetails(DistributeUtils.loadCachedReleaseDetails());
                    ReleaseDetails releaseDetails = this.mReleaseDetails;
                    if (releaseDetails != null && !releaseDetails.isMandatoryUpdate() && NetworkStateHelper.getSharedInstance(this.mContext).isNetworkConnected() && downloadState == 1) {
                        cancelPreviousTasks();
                    }
                }
                if (downloadState != 0 && downloadState != 1 && !this.mCheckedDownload) {
                    if (this.mPackageInfo.lastUpdateTime > SharedPreferencesManager.getLong("Distribute.download_time")) {
                        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Discarding previous download as application updated.");
                        cancelPreviousTasks();
                    } else {
                        this.mCheckedDownload = true;
                        resumeDownload();
                        ReleaseDetails releaseDetails2 = this.mReleaseDetails;
                        if (releaseDetails2 == null || !releaseDetails2.isMandatoryUpdate() || downloadState != 2) {
                            return;
                        }
                    }
                }
                ReleaseDetails releaseDetails3 = this.mReleaseDetails;
                if (releaseDetails3 != null) {
                    if (downloadState == 4) {
                        showMandatoryDownloadReadyDialog();
                    } else if (downloadState == 2) {
                        resumeDownload();
                        showDownloadProgress();
                    } else if (this.mUnknownSourcesDialog != null) {
                        enqueueDownloadOrShowUnknownSourcesDialog(releaseDetails3);
                    } else {
                        ReleaseDownloader releaseDownloader = this.mReleaseDownloader;
                        if (releaseDownloader == null || !releaseDownloader.isDownloading()) {
                            showUpdateDialog();
                        }
                    }
                    if (downloadState != 1 && downloadState != 4) {
                        return;
                    }
                }
                String updateSetupFailedMessage = SharedPreferencesManager.getString("Distribute.update_setup_failed_message");
                if (updateSetupFailedMessage != null) {
                    AppCenterLog.debug(DistributeConstants.LOG_TAG, "In-app updates setup failure detected.");
                    showUpdateSetupFailedDialog();
                } else if (this.mCheckReleaseCallId != null) {
                    AppCenterLog.verbose(DistributeConstants.LOG_TAG, "Already checking or checked latest release.");
                } else if (this.mAutomaticCheckForUpdateDisabled && !this.mManualCheckForUpdateRequested) {
                    AppCenterLog.debug(DistributeConstants.LOG_TAG, "Automatic check for update is disabled. The SDK will not check for update now.");
                } else {
                    String updateToken = SharedPreferencesManager.getString("Distribute.update_token");
                    String distributionGroupId = SharedPreferencesManager.getString("Distribute.distribution_group_id");
                    if (!isPublicTrack && updateToken == null) {
                        String testerAppUpdateSetupFailedMessage = SharedPreferencesManager.getString("Distribute.tester_app_update_setup_failed_message");
                        if (isAppCenterTesterAppInstalled() && TextUtils.isEmpty(testerAppUpdateSetupFailedMessage) && !this.mContext.getPackageName().equals("com.microsoft.hockeyapp.testerapp")) {
                            shouldUseTesterAppForUpdateSetup = true;
                        }
                        if (shouldUseTesterAppForUpdateSetup && !this.mTesterAppOpenedOrAborted) {
                            DistributeUtils.updateSetupUsingTesterApp(this.mForegroundActivity, this.mPackageInfo);
                            this.mTesterAppOpenedOrAborted = true;
                        } else if (!this.mBrowserOpenedOrAborted) {
                            DistributeUtils.updateSetupUsingBrowser(this.mForegroundActivity, this.mInstallUrl, this.mAppSecret, this.mPackageInfo);
                            this.mBrowserOpenedOrAborted = true;
                        }
                    }
                    str = updateToken;
                    decryptAndGetReleaseDetails(str, distributionGroupId);
                }
            }
        }
    }

    private boolean isAppCenterTesterAppInstalled() {
        try {
            this.mContext.getPackageManager().getPackageInfo("com.microsoft.hockeyapp.testerapp", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void decryptAndGetReleaseDetails(String updateToken, String distributionGroupId) {
        if (updateToken != null) {
            CryptoUtils.DecryptedData decryptedData = CryptoUtils.getInstance(this.mContext).decrypt(updateToken);
            String newEncryptedData = decryptedData.getNewEncryptedData();
            if (newEncryptedData != null) {
                SharedPreferencesManager.putString("Distribute.update_token", newEncryptedData);
            }
            updateToken = decryptedData.getDecryptedData();
        }
        getLatestReleaseDetails(distributionGroupId, updateToken);
    }

    public synchronized void completeWorkflow(ReleaseDetails releaseDetails) {
        if (releaseDetails == this.mReleaseDetails) {
            completeWorkflow();
        }
    }

    private synchronized void cancelNotification() {
        if (DistributeUtils.getStoredDownloadState() == 3) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Delete notification");
            NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
            notificationManager.cancel(DistributeUtils.getNotificationId());
        }
    }

    public synchronized void completeWorkflow() {
        cancelNotification();
        SharedPreferencesManager.remove("Distribute.release_details");
        SharedPreferencesManager.remove("Distribute.download_state");
        this.mCheckReleaseApiCall = null;
        this.mCheckReleaseCallId = null;
        this.mUpdateDialog = null;
        this.mUpdateSetupFailedDialog = null;
        this.mUnknownSourcesDialog = null;
        this.mLastActivityWithDialog.clear();
        this.mReleaseDetails = null;
        ReleaseDownloadListener releaseDownloadListener = this.mReleaseDownloaderListener;
        if (releaseDownloadListener != null) {
            releaseDownloadListener.hideProgressDialog();
        }
        this.mWorkflowCompleted = true;
        this.mManualCheckForUpdateRequested = false;
    }

    public synchronized void storeUpdateSetupFailedParameter(String requestId, String updateSetupFailed) {
        if (this.mContext == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Update setup failed parameter received before onStart, keep it in memory.");
            this.mBeforeStartRequestId = requestId;
            this.mBeforeStartUpdateSetupFailed = updateSetupFailed;
        } else if (requestId.equals(SharedPreferencesManager.getString("Distribute.request_id"))) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Stored update setup failed parameter.");
            SharedPreferencesManager.putString("Distribute.update_setup_failed_message", updateSetupFailed);
        } else {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "Ignoring redirection parameters as requestId is invalid.");
        }
    }

    public synchronized void storeTesterAppUpdateSetupFailedParameter(String requestId, String testerAppUpdateSetupFailed) {
        if (this.mContext == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Tester app update setup failed parameter received before onStart, keep it in memory.");
            this.mBeforeStartRequestId = requestId;
            this.mBeforeStartTesterAppUpdateSetupFailed = testerAppUpdateSetupFailed;
        } else if (requestId.equals(SharedPreferencesManager.getString("Distribute.request_id"))) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Stored tester app update setup failed parameter.");
            SharedPreferencesManager.putString("Distribute.tester_app_update_setup_failed_message", testerAppUpdateSetupFailed);
        } else {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "Ignoring redirection parameters as requestId is invalid.");
        }
    }

    public synchronized void storeRedirectionParameters(String requestId, String distributionGroupId, String updateToken) {
        if (this.mContext == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Redirection parameters received before onStart, keep them in memory.");
            this.mBeforeStartRequestId = requestId;
            this.mBeforeStartUpdateToken = updateToken;
            this.mBeforeStartDistributionGroupId = distributionGroupId;
        } else if (requestId.equals(SharedPreferencesManager.getString("Distribute.request_id"))) {
            if (updateToken != null) {
                String encryptedToken = CryptoUtils.getInstance(this.mContext).encrypt(updateToken);
                SharedPreferencesManager.putString("Distribute.update_token", encryptedToken);
            } else {
                SharedPreferencesManager.remove("Distribute.update_token");
            }
            SharedPreferencesManager.remove("Distribute.request_id");
            processDistributionGroupId(distributionGroupId);
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Stored redirection parameters.");
            cancelPreviousTasks();
            getLatestReleaseDetails(distributionGroupId, updateToken);
        } else {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "Ignoring redirection parameters as requestId is invalid.");
        }
    }

    private void processDistributionGroupId(String distributionGroupId) {
        SharedPreferencesManager.putString("Distribute.distribution_group_id", distributionGroupId);
        this.mDistributeInfoTracker.updateDistributionGroupId(distributionGroupId);
        enqueueDistributionStartSessionLog();
    }

    synchronized void getLatestReleaseDetails(final String distributionGroupId, String updateToken) {
        HttpClient httpClient;
        String url;
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Get latest release details...");
        HttpClient httpClient2 = DependencyConfiguration.getHttpClient();
        if (httpClient2 != null) {
            httpClient = httpClient2;
        } else {
            httpClient = HttpUtils.createHttpClient(this.mContext);
        }
        String releaseHash = DistributeUtils.computeReleaseHash(this.mPackageInfo);
        String url2 = this.mApiUrl;
        if (updateToken == null) {
            url = url2 + String.format("/public/sdk/apps/%s/releases/latest?release_hash=%s%s", this.mAppSecret, releaseHash, getReportingParametersForUpdatedRelease(true, distributionGroupId));
        } else {
            url = url2 + String.format("/sdk/apps/%s/releases/private/latest?release_hash=%s%s", this.mAppSecret, releaseHash, getReportingParametersForUpdatedRelease(false, distributionGroupId));
        }
        Map<String, String> headers = new HashMap<>();
        if (updateToken != null) {
            headers.put("x-api-token", updateToken);
        }
        final Object releaseCallId = new Object();
        this.mCheckReleaseCallId = releaseCallId;
        this.mCheckReleaseApiCall = httpClient.callAsync(url, DefaultHttpClient.METHOD_GET, headers, new HttpClient.CallTemplate() { // from class: com.microsoft.appcenter.distribute.Distribute.4
            @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
            public String buildRequestBody() {
                return null;
            }

            @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
            public void onBeforeCalling(URL url3, Map<String, String> headers2) {
                if (AppCenterLog.getLogLevel() <= 2) {
                    String urlString = url3.toString().replaceAll(Distribute.this.mAppSecret, HttpUtils.hideSecret(Distribute.this.mAppSecret));
                    AppCenterLog.verbose(DistributeConstants.LOG_TAG, "Calling " + urlString + "...");
                    Map<String, String> logHeaders = new HashMap<>(headers2);
                    String apiToken = logHeaders.get("x-api-token");
                    if (apiToken != null) {
                        logHeaders.put("x-api-token", HttpUtils.hideSecret(apiToken));
                    }
                    AppCenterLog.verbose(DistributeConstants.LOG_TAG, "Headers: " + logHeaders);
                }
            }
        }, new ServiceCallback() { // from class: com.microsoft.appcenter.distribute.Distribute.5
            @Override // com.microsoft.appcenter.http.ServiceCallback
            public void onCallSucceeded(HttpResponse httpResponse) {
                try {
                    String payload = httpResponse.getPayload();
                    Distribute.this.handleApiCallSuccess(releaseCallId, payload, ReleaseDetails.parse(payload), distributionGroupId);
                } catch (JSONException e) {
                    onCallFailed(e);
                }
            }

            @Override // com.microsoft.appcenter.http.ServiceCallback
            public void onCallFailed(Exception e) {
                Distribute.this.handleApiCallFailure(releaseCallId, e);
            }
        });
    }

    public synchronized void handleApiCallFailure(Object releaseCallId, Exception e) {
        if (this.mCheckReleaseCallId == releaseCallId) {
            completeWorkflow();
            if (!HttpUtils.isRecoverableError(e)) {
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    String code = null;
                    try {
                        ErrorDetails errorDetails = ErrorDetails.parse(httpException.getHttpResponse().getPayload());
                        code = errorDetails.getCode();
                    } catch (JSONException je) {
                        AppCenterLog.verbose(DistributeConstants.LOG_TAG, "Cannot read the error as JSON", je);
                    }
                    if ("no_releases_for_user".equals(code)) {
                        AppCenterLog.info(DistributeConstants.LOG_TAG, "No release available to the current user.");
                    } else {
                        AppCenterLog.error(DistributeConstants.LOG_TAG, "Failed to check latest release (delete setup state)", e);
                        SharedPreferencesManager.remove("Distribute.distribution_group_id");
                        SharedPreferencesManager.remove("Distribute.update_token");
                        SharedPreferencesManager.remove("Distribute.postpone_time");
                        this.mDistributeInfoTracker.removeDistributionGroupId();
                    }
                } else {
                    AppCenterLog.error(DistributeConstants.LOG_TAG, "Failed to check latest release", e);
                }
            }
        }
    }

    public synchronized void handleApiCallSuccess(Object releaseCallId, String rawReleaseDetails, ReleaseDetails releaseDetails, String sourceDistributionId) {
        String lastDownloadedReleaseHash = SharedPreferencesManager.getString("Distribute.downloaded_release_hash");
        if (!TextUtils.isEmpty(lastDownloadedReleaseHash)) {
            if (isCurrentReleaseWasUpdated(lastDownloadedReleaseHash)) {
                AppCenterLog.debug(DistributeConstants.LOG_TAG, "Successfully reported app update for downloaded release hash (" + lastDownloadedReleaseHash + "), removing from store..");
                SharedPreferencesManager.remove("Distribute.downloaded_release_hash");
                SharedPreferencesManager.remove("Distribute.downloaded_release_id");
            } else {
                AppCenterLog.debug(DistributeConstants.LOG_TAG, "Stored release hash doesn't match current installation, probably downloaded but not installed yet, keep in store");
            }
        }
        if (this.mCheckReleaseCallId == releaseCallId) {
            this.mCheckReleaseApiCall = null;
            if (sourceDistributionId == null) {
                processDistributionGroupId(releaseDetails.getDistributionGroupId());
            }
            if (Build.VERSION.SDK_INT >= releaseDetails.getMinApiLevel()) {
                AppCenterLog.debug(DistributeConstants.LOG_TAG, "Check if latest release is more recent.");
                if (isMoreRecent(releaseDetails) && canUpdateNow(releaseDetails)) {
                    if (this.mReleaseDetails == null) {
                        updateReleaseDetails(DistributeUtils.loadCachedReleaseDetails());
                    }
                    SharedPreferencesManager.putString("Distribute.release_details", rawReleaseDetails);
                    ReleaseDetails releaseDetails2 = this.mReleaseDetails;
                    if (releaseDetails2 != null && releaseDetails2.isMandatoryUpdate()) {
                        if (this.mReleaseDetails.getId() != releaseDetails.getId()) {
                            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Latest release is more recent than the previous mandatory.");
                            SharedPreferencesManager.putInt("Distribute.download_state", 1);
                        } else {
                            AppCenterLog.debug(DistributeConstants.LOG_TAG, "The latest release is mandatory and already being processed.");
                        }
                        return;
                    }
                    updateReleaseDetails(releaseDetails);
                    AppCenterLog.debug(DistributeConstants.LOG_TAG, "Latest release is more recent.");
                    SharedPreferencesManager.putInt("Distribute.download_state", 1);
                    if (this.mForegroundActivity != null) {
                        showUpdateDialog();
                    }
                    return;
                }
            } else {
                AppCenterLog.info(DistributeConstants.LOG_TAG, "This device is not compatible with the latest release.");
            }
            completeWorkflow();
        }
    }

    private synchronized void updateReleaseDetails(ReleaseDetails releaseDetails) {
        if (this.mReleaseDownloader != null) {
            if (releaseDetails == null || releaseDetails.getId() != this.mReleaseDownloader.getReleaseDetails().getId()) {
                this.mReleaseDownloader.cancel();
            }
            this.mReleaseDownloader = null;
        } else if (releaseDetails == null) {
            ReleaseDownloaderFactory.create(this.mContext, null, null).cancel();
        }
        ReleaseDownloadListener releaseDownloadListener = this.mReleaseDownloaderListener;
        if (releaseDownloadListener != null) {
            releaseDownloadListener.hideProgressDialog();
            this.mReleaseDownloaderListener = null;
        }
        this.mReleaseDetails = releaseDetails;
        if (releaseDetails != null) {
            ReleaseDownloadListener releaseDownloadListener2 = new ReleaseDownloadListener(this.mContext, releaseDetails);
            this.mReleaseDownloaderListener = releaseDownloadListener2;
            this.mReleaseDownloader = ReleaseDownloaderFactory.create(this.mContext, this.mReleaseDetails, releaseDownloadListener2);
        }
    }

    private String getReportingParametersForUpdatedRelease(boolean isPublic, String distributionGroupId) {
        String reportingParameters = "";
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Check if we need to report release installation..");
        String lastDownloadedReleaseHash = SharedPreferencesManager.getString("Distribute.downloaded_release_hash");
        if (TextUtils.isEmpty(lastDownloadedReleaseHash)) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Current release was already reported, skip reporting.");
            return reportingParameters;
        } else if (isCurrentReleaseWasUpdated(lastDownloadedReleaseHash)) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Current release was updated but not reported yet, reporting..");
            if (isPublic) {
                reportingParameters = reportingParameters + "&install_id=" + IdHelper.getInstallId();
            }
            String reportingParameters2 = reportingParameters + "&distribution_group_id=" + distributionGroupId;
            int lastDownloadedReleaseId = SharedPreferencesManager.getInt("Distribute.downloaded_release_id");
            return reportingParameters2 + "&downloaded_release_id=" + lastDownloadedReleaseId;
        } else {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "New release was downloaded but not installed yet, skip reporting.");
            return reportingParameters;
        }
    }

    private void changeDistributionGroupIdAfterAppUpdateIfNeeded() {
        String lastDownloadedReleaseHash = SharedPreferencesManager.getString("Distribute.downloaded_release_hash");
        String lastDownloadedDistributionGroupId = SharedPreferencesManager.getString("Distribute.downloaded_distribution_group_id");
        if (!isCurrentReleaseWasUpdated(lastDownloadedReleaseHash) || TextUtils.isEmpty(lastDownloadedDistributionGroupId)) {
            return;
        }
        String currentDistributionGroupId = SharedPreferencesManager.getString("Distribute.distribution_group_id");
        if (lastDownloadedDistributionGroupId.equals(currentDistributionGroupId)) {
            return;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Current group ID doesn't match the group ID of downloaded release, updating current group id=" + lastDownloadedDistributionGroupId);
        SharedPreferencesManager.putString("Distribute.distribution_group_id", lastDownloadedDistributionGroupId);
        SharedPreferencesManager.remove("Distribute.downloaded_distribution_group_id");
    }

    private boolean isCurrentReleaseWasUpdated(String lastDownloadedReleaseHash) {
        if (this.mPackageInfo == null || TextUtils.isEmpty(lastDownloadedReleaseHash)) {
            return false;
        }
        String currentInstalledReleaseHash = DistributeUtils.computeReleaseHash(this.mPackageInfo);
        return currentInstalledReleaseHash.equals(lastDownloadedReleaseHash);
    }

    private boolean isMoreRecent(ReleaseDetails releaseDetails) {
        boolean moreRecent;
        int versionCode = DeviceInfoHelper.getVersionCode(this.mPackageInfo);
        boolean z = true;
        if (releaseDetails.getVersion() == versionCode) {
            moreRecent = !releaseDetails.getReleaseHash().equals(DistributeUtils.computeReleaseHash(this.mPackageInfo));
        } else {
            if (releaseDetails.getVersion() <= versionCode) {
                z = false;
            }
            moreRecent = z;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Latest release more recent=" + moreRecent);
        return moreRecent;
    }

    private boolean canUpdateNow(ReleaseDetails releaseDetails) {
        if (releaseDetails.isMandatoryUpdate()) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Release is mandatory, ignoring any postpone action.");
            return true;
        }
        long now = System.currentTimeMillis();
        long postponedTime = SharedPreferencesManager.getLong("Distribute.postpone_time", 0L);
        if (now < postponedTime) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "User clock has been changed in past, cleaning postpone state and showing dialog");
            SharedPreferencesManager.remove("Distribute.postpone_time");
            return true;
        }
        long postponedUntil = 86400000 + postponedTime;
        if (now >= postponedUntil) {
            return true;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Optional updates are postponed until " + new Date(postponedUntil));
        return false;
    }

    private boolean shouldRefreshDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            if (this.mForegroundActivity == this.mLastActivityWithDialog.get()) {
                AppCenterLog.debug(DistributeConstants.LOG_TAG, "Previous dialog is still being shown in the same activity.");
                return false;
            }
            dialog.hide();
            return true;
        }
        return true;
    }

    private void showAndRememberDialogActivity(Dialog dialog) {
        dialog.show();
        this.mLastActivityWithDialog = new WeakReference<>(this.mForegroundActivity);
    }

    private synchronized void showUpdateDialog() {
        String message;
        boolean z = true;
        if (this.mListener == null && this.mUsingDefaultUpdateDialog == null) {
            this.mUsingDefaultUpdateDialog = true;
        }
        if (this.mListener != null && this.mForegroundActivity != this.mLastActivityWithDialog.get()) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Calling listener.onReleaseAvailable.");
            boolean customized = this.mListener.onReleaseAvailable(this.mForegroundActivity, this.mReleaseDetails);
            if (customized) {
                this.mLastActivityWithDialog = new WeakReference<>(this.mForegroundActivity);
            }
            if (customized) {
                z = false;
            }
            this.mUsingDefaultUpdateDialog = Boolean.valueOf(z);
        }
        if (this.mUsingDefaultUpdateDialog.booleanValue()) {
            if (!shouldRefreshDialog(this.mUpdateDialog)) {
                return;
            }
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Show default update dialog.");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.mForegroundActivity);
            dialogBuilder.setTitle(R.string.appcenter_distribute_update_dialog_title);
            final ReleaseDetails releaseDetails = this.mReleaseDetails;
            if (releaseDetails.isMandatoryUpdate()) {
                message = this.mContext.getString(R.string.appcenter_distribute_update_dialog_message_mandatory);
            } else {
                message = this.mContext.getString(R.string.appcenter_distribute_update_dialog_message_optional);
            }
            dialogBuilder.setMessage(formatAppNameAndVersion(message));
            dialogBuilder.setPositiveButton(R.string.appcenter_distribute_update_dialog_download, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Distribute.this.enqueueDownloadOrShowUnknownSourcesDialog(releaseDetails);
                }
            });
            dialogBuilder.setCancelable(false);
            if (!releaseDetails.isMandatoryUpdate()) {
                dialogBuilder.setNegativeButton(R.string.appcenter_distribute_update_dialog_postpone, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.7
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Distribute.this.postponeRelease(releaseDetails);
                    }
                });
            }
            if (!TextUtils.isEmpty(releaseDetails.getReleaseNotes()) && releaseDetails.getReleaseNotesUrl() != null) {
                dialogBuilder.setNeutralButton(R.string.appcenter_distribute_update_dialog_view_release_notes, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.8
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        Distribute.this.viewReleaseNotes(releaseDetails);
                    }
                });
            }
            AlertDialog create = dialogBuilder.create();
            this.mUpdateDialog = create;
            showAndRememberDialogActivity(create);
        }
    }

    public void viewReleaseNotes(ReleaseDetails releaseDetails) {
        try {
            this.mForegroundActivity.startActivity(new Intent("android.intent.action.VIEW", releaseDetails.getReleaseNotesUrl()));
        } catch (ActivityNotFoundException e) {
            AppCenterLog.error(DistributeConstants.LOG_TAG, "Failed to navigate to release notes.", e);
        }
    }

    public synchronized void storeUpdateSetupFailedPackageHash(DialogInterface dialog) {
        if (this.mUpdateSetupFailedDialog == dialog) {
            SharedPreferencesManager.putString("Distribute.update_setup_failed_package_hash", DistributeUtils.computeReleaseHash(this.mPackageInfo));
        } else {
            showDisabledToast();
        }
    }

    public synchronized void handleUpdateFailedDialogReinstallAction(DialogInterface dialog) {
        if (this.mUpdateSetupFailedDialog == dialog) {
            String url = this.mInstallUrl;
            try {
                url = BrowserUtils.appendUri(url, "update_setup_failed=true");
            } catch (URISyntaxException e) {
                AppCenterLog.error(DistributeConstants.LOG_TAG, "Could not append query parameter to url.", e);
            }
            BrowserUtils.openBrowser(url, this.mForegroundActivity);
            SharedPreferencesManager.remove("Distribute.update_setup_failed_package_hash");
            SharedPreferencesManager.remove("Distribute.tester_app_update_setup_failed_message");
        } else {
            showDisabledToast();
        }
    }

    private synchronized void showUnknownSourcesDialog() {
        if (!shouldRefreshDialog(this.mUnknownSourcesDialog)) {
            return;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Show new unknown sources dialog.");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.mForegroundActivity);
        dialogBuilder.setMessage(R.string.appcenter_distribute_unknown_sources_dialog_message);
        final ReleaseDetails releaseDetails = this.mReleaseDetails;
        if (releaseDetails.isMandatoryUpdate()) {
            dialogBuilder.setCancelable(false);
        } else {
            dialogBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.9
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Distribute.this.completeWorkflow(releaseDetails);
                }
            });
            dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.microsoft.appcenter.distribute.Distribute.10
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialog) {
                    Distribute.this.completeWorkflow(releaseDetails);
                }
            });
        }
        dialogBuilder.setPositiveButton(R.string.appcenter_distribute_unknown_sources_dialog_settings, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Distribute.this.goToUnknownAppsSettings(releaseDetails);
            }
        });
        AlertDialog create = dialogBuilder.create();
        this.mUnknownSourcesDialog = create;
        showAndRememberDialogActivity(create);
    }

    private synchronized void showUpdateSetupFailedDialog() {
        if (!shouldRefreshDialog(this.mUpdateSetupFailedDialog)) {
            return;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Show update setup failed dialog.");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.mForegroundActivity);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle(R.string.appcenter_distribute_update_failed_dialog_title);
        dialogBuilder.setMessage(R.string.appcenter_distribute_update_failed_dialog_message);
        dialogBuilder.setPositiveButton(R.string.appcenter_distribute_update_failed_dialog_ignore, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.12
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Distribute.this.storeUpdateSetupFailedPackageHash(dialog);
            }
        });
        dialogBuilder.setNegativeButton(R.string.appcenter_distribute_update_failed_dialog_reinstall, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Distribute.this.handleUpdateFailedDialogReinstallAction(dialog);
            }
        });
        AlertDialog create = dialogBuilder.create();
        this.mUpdateSetupFailedDialog = create;
        showAndRememberDialogActivity(create);
        SharedPreferencesManager.remove("Distribute.update_setup_failed_message");
    }

    public synchronized void goToUnknownAppsSettings(ReleaseDetails releaseDetails) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= 26) {
            intent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES");
            intent.setData(Uri.parse("package:" + this.mForegroundActivity.getPackageName()));
        } else {
            intent = new Intent("android.settings.SECURITY_SETTINGS");
        }
        try {
            this.mForegroundActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "No way to navigate to secure settings on this device automatically");
            if (releaseDetails == this.mReleaseDetails) {
                completeWorkflow();
            }
        }
    }

    public synchronized void postponeRelease(ReleaseDetails releaseDetails) {
        if (releaseDetails == this.mReleaseDetails) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Postpone updates for a day.");
            SharedPreferencesManager.putLong("Distribute.postpone_time", System.currentTimeMillis());
            completeWorkflow();
        } else {
            showDisabledToast();
        }
    }

    synchronized void enqueueDownloadOrShowUnknownSourcesDialog(ReleaseDetails releaseDetails) {
        if (releaseDetails == this.mReleaseDetails) {
            if (InstallerUtils.isUnknownSourcesEnabled(this.mContext)) {
                AppCenterLog.debug(DistributeConstants.LOG_TAG, "Schedule download...");
                resumeDownload();
                showDownloadProgress();
                ServiceCall serviceCall = this.mCheckReleaseApiCall;
                if (serviceCall != null) {
                    serviceCall.cancel();
                }
            } else {
                showUnknownSourcesDialog();
            }
        } else {
            showDisabledToast();
        }
    }

    private void showDisabledToast() {
        Toast.makeText(this.mContext, R.string.appcenter_distribute_dialog_actioned_on_disabled_toast, 0).show();
    }

    public synchronized void resumeApp(Context context) {
        if (this.mForegroundActivity == null) {
            Intent intent = new Intent(context, DeepLinkActivity.class);
            intent.addFlags(268435456);
            context.startActivity(intent);
        }
    }

    public synchronized boolean notifyDownload(ReleaseDetails releaseDetails, Intent intent) {
        Notification.Builder builder;
        if (releaseDetails != this.mReleaseDetails) {
            return true;
        }
        if (this.mForegroundActivity == null && DistributeUtils.getStoredDownloadState() != 3) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Post a notification as the download finished in background.");
            NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("appcenter.distribute", this.mContext.getString(R.string.appcenter_distribute_notification_category), 3);
                notificationManager.createNotificationChannel(channel);
                builder = new Notification.Builder(this.mContext, "appcenter.distribute");
            } else {
                builder = getOldNotificationBuilder();
            }
            builder.setTicker(this.mContext.getString(R.string.appcenter_distribute_install_ready_title)).setContentTitle(this.mContext.getString(R.string.appcenter_distribute_install_ready_title)).setContentText(getInstallReadyMessage()).setSmallIcon(this.mContext.getApplicationInfo().icon).setContentIntent(PendingIntent.getActivities(this.mContext, 0, new Intent[]{intent}, 0));
            builder.setStyle(new Notification.BigTextStyle().bigText(getInstallReadyMessage()));
            Notification notification = builder.build();
            notification.flags |= 16;
            notificationManager.notify(DistributeUtils.getNotificationId(), notification);
            SharedPreferencesManager.putInt("Distribute.download_state", 3);
            this.mCheckedDownload = false;
            return true;
        }
        return false;
    }

    private Notification.Builder getOldNotificationBuilder() {
        return new Notification.Builder(this.mContext);
    }

    private synchronized void showDownloadProgress() {
        Activity activity = this.mForegroundActivity;
        if (activity == null) {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "Could not display progress dialog in the background.");
            return;
        }
        ReleaseDownloadListener releaseDownloadListener = this.mReleaseDownloaderListener;
        if (releaseDownloadListener == null) {
            return;
        }
        Dialog progressDialog = releaseDownloadListener.showDownloadProgress(activity);
        if (progressDialog != null) {
            showAndRememberDialogActivity(progressDialog);
        }
    }

    private synchronized void showMandatoryDownloadReadyDialog() {
        if (shouldRefreshDialog(this.mCompletedDownloadDialog)) {
            final ReleaseDetails releaseDetails = this.mReleaseDetails;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.mForegroundActivity);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setTitle(R.string.appcenter_distribute_install_ready_title);
            dialogBuilder.setMessage(getInstallReadyMessage());
            dialogBuilder.setPositiveButton(R.string.appcenter_distribute_install, new DialogInterface.OnClickListener() { // from class: com.microsoft.appcenter.distribute.Distribute.14
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Distribute.this.installMandatoryUpdate(releaseDetails);
                }
            });
            AlertDialog create = dialogBuilder.create();
            this.mCompletedDownloadDialog = create;
            showAndRememberDialogActivity(create);
        }
    }

    private String getInstallReadyMessage() {
        return formatAppNameAndVersion(this.mContext.getString(R.string.appcenter_distribute_install_ready_message));
    }

    private String formatAppNameAndVersion(String format) {
        String appName = AppNameHelper.getAppName(this.mContext);
        return String.format(format, appName, this.mReleaseDetails.getShortVersion(), Integer.valueOf(this.mReleaseDetails.getVersion()));
    }

    public synchronized void installMandatoryUpdate(ReleaseDetails releaseDetails) {
        if (releaseDetails == this.mReleaseDetails) {
            resumeDownload();
        } else {
            showDisabledToast();
        }
    }

    public synchronized void resumeDownload() {
        ReleaseDownloader releaseDownloader = this.mReleaseDownloader;
        if (releaseDownloader != null) {
            releaseDownloader.resume();
            this.mCheckedDownload = true;
        }
    }

    public synchronized void setDownloading(ReleaseDetails releaseDetails, long enqueueTime) {
        if (releaseDetails != this.mReleaseDetails) {
            return;
        }
        SharedPreferencesManager.putInt("Distribute.download_state", 2);
        SharedPreferencesManager.putLong("Distribute.download_time", enqueueTime);
    }

    public synchronized void setInstalling(ReleaseDetails releaseDetails) {
        if (releaseDetails != this.mReleaseDetails) {
            return;
        }
        if (releaseDetails.isMandatoryUpdate()) {
            cancelNotification();
            SharedPreferencesManager.putInt("Distribute.download_state", 4);
        } else {
            completeWorkflow(releaseDetails);
        }
        String groupId = releaseDetails.getDistributionGroupId();
        String releaseHash = releaseDetails.getReleaseHash();
        int releaseId = releaseDetails.getId();
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Stored release details: group id=" + groupId + " release hash=" + releaseHash + " release id=" + releaseId);
        SharedPreferencesManager.putString("Distribute.downloaded_distribution_group_id", groupId);
        SharedPreferencesManager.putString("Distribute.downloaded_release_hash", releaseHash);
        SharedPreferencesManager.putInt("Distribute.downloaded_release_id", releaseId);
    }

    private synchronized void enqueueDistributionStartSessionLog() {
        SessionContext.SessionInfo lastSession = SessionContext.getInstance().getSessionAt(System.currentTimeMillis());
        if (lastSession != null && lastSession.getSessionId() != null) {
            post(new Runnable() { // from class: com.microsoft.appcenter.distribute.Distribute.15
                @Override // java.lang.Runnable
                public void run() {
                    DistributionStartSessionLog log = new DistributionStartSessionLog();
                    Distribute.this.mChannel.enqueue(log, Distribute.DISTRIBUTE_GROUP, 1);
                }
            });
            return;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "No sessions were logged before, ignore sending of the distribution start session log.");
    }
}
