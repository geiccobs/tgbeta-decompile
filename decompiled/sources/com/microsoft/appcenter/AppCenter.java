package com.microsoft.appcenter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.channel.DefaultChannel;
import com.microsoft.appcenter.channel.OneCollectorChannelListener;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.ingestion.models.StartServiceLog;
import com.microsoft.appcenter.ingestion.models.json.CustomPropertiesLogFactory;
import com.microsoft.appcenter.ingestion.models.json.DefaultLogSerializer;
import com.microsoft.appcenter.ingestion.models.json.LogFactory;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.json.StartServiceLogFactory;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.ApplicationLifecycleListener;
import com.microsoft.appcenter.utils.IdHelper;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import com.microsoft.appcenter.utils.async.DefaultAppCenterFuture;
import com.microsoft.appcenter.utils.context.SessionContext;
import com.microsoft.appcenter.utils.context.UserIdContext;
import com.microsoft.appcenter.utils.storage.FileManager;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class AppCenter {
    @SuppressLint({"StaticFieldLeak"})
    private static AppCenter sInstance;
    private AppCenterHandler mAppCenterHandler;
    private String mAppSecret;
    private Application mApplication;
    private ApplicationLifecycleListener mApplicationLifecycleListener;
    private Channel mChannel;
    private boolean mConfiguredFromApp;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mLogLevelConfigured;
    private LogSerializer mLogSerializer;
    private String mLogUrl;
    private OneCollectorChannelListener mOneCollectorChannelListener;
    private Set<AppCenterService> mServices;
    private Set<AppCenterService> mServicesStartedFromLibrary;
    private DefaultAppCenterFuture<Boolean> mSetMaxStorageSizeFuture;
    private String mTransmissionTargetToken;
    private UncaughtExceptionHandler mUncaughtExceptionHandler;
    private final List<String> mStartedServicesNamesToLog = new ArrayList();
    private long mMaxStorageSizeInBytes = 10485760;

    public static synchronized AppCenter getInstance() {
        AppCenter appCenter;
        synchronized (AppCenter.class) {
            if (sInstance == null) {
                sInstance = new AppCenter();
            }
            appCenter = sInstance;
        }
        return appCenter;
    }

    @SafeVarargs
    public static void start(Application application, String str, Class<? extends AppCenterService>... clsArr) {
        getInstance().configureAndStartServices(application, str, clsArr);
    }

    private synchronized void setInstanceUserId(String str) {
        if (!this.mConfiguredFromApp) {
            AppCenterLog.error("AppCenter", "AppCenter must be configured from application, libraries cannot use call setUserId.");
            return;
        }
        String str2 = this.mAppSecret;
        if (str2 == null && this.mTransmissionTargetToken == null) {
            AppCenterLog.error("AppCenter", "AppCenter must be configured with a secret from application to call setUserId.");
            return;
        }
        if (str != null) {
            if (str2 != null && !UserIdContext.checkUserIdValidForAppCenter(str)) {
                return;
            }
            if (this.mTransmissionTargetToken != null && !UserIdContext.checkUserIdValidForOneCollector(str)) {
                return;
            }
        }
        UserIdContext.getInstance().setUserId(str);
    }

    private synchronized boolean checkPrecondition() {
        if (isInstanceConfigured()) {
            return true;
        }
        AppCenterLog.error("AppCenter", "App Center hasn't been configured. You need to call AppCenter.start with appSecret or AppCenter.configure first.");
        return false;
    }

    private synchronized boolean isInstanceConfigured() {
        return this.mApplication != null;
    }

    private synchronized boolean configureInstance(Application application, String str, final boolean z) {
        if (application == null) {
            AppCenterLog.error("AppCenter", "Application context may not be null.");
            return false;
        }
        if (!this.mLogLevelConfigured && (application.getApplicationInfo().flags & 2) == 2) {
            AppCenterLog.setLogLevel(5);
        }
        String str2 = this.mAppSecret;
        if (z && !configureSecretString(str)) {
            return false;
        }
        if (this.mHandler != null) {
            String str3 = this.mAppSecret;
            if (str3 != null && !str3.equals(str2)) {
                this.mHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.4
                    @Override // java.lang.Runnable
                    public void run() {
                        AppCenter.this.mChannel.setAppSecret(AppCenter.this.mAppSecret);
                        AppCenter.this.applyStorageMaxSize();
                    }
                });
            }
            return true;
        }
        this.mApplication = application;
        HandlerThread handlerThread = new HandlerThread("AppCenter.Looper");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mAppCenterHandler = new AppCenterHandler() { // from class: com.microsoft.appcenter.AppCenter.5
            @Override // com.microsoft.appcenter.AppCenterHandler
            public void post(Runnable runnable, Runnable runnable2) {
                AppCenter.this.handlerAppCenterOperation(runnable, runnable2);
            }
        };
        ApplicationLifecycleListener applicationLifecycleListener = new ApplicationLifecycleListener(this.mHandler);
        this.mApplicationLifecycleListener = applicationLifecycleListener;
        this.mApplication.registerActivityLifecycleCallbacks(applicationLifecycleListener);
        this.mServices = new HashSet();
        this.mServicesStartedFromLibrary = new HashSet();
        this.mHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.6
            @Override // java.lang.Runnable
            public void run() {
                AppCenter.this.finishConfiguration(z);
            }
        });
        AppCenterLog.info("AppCenter", "App Center SDK configured successfully.");
        return true;
    }

    private boolean configureSecretString(String str) {
        if (this.mConfiguredFromApp) {
            AppCenterLog.warn("AppCenter", "App Center may only be configured once.");
            return false;
        }
        this.mConfiguredFromApp = true;
        if (str != null) {
            for (String str2 : str.split(";")) {
                String[] split = str2.split("=", -1);
                String str3 = split[0];
                if (split.length == 1) {
                    if (!str3.isEmpty()) {
                        this.mAppSecret = str3;
                    }
                } else if (!split[1].isEmpty()) {
                    String str4 = split[1];
                    if ("appsecret".equals(str3)) {
                        this.mAppSecret = str4;
                    } else if ("target".equals(str3)) {
                        this.mTransmissionTargetToken = str4;
                    }
                }
            }
        }
        return true;
    }

    public synchronized void handlerAppCenterOperation(final Runnable runnable, final Runnable runnable2) {
        if (checkPrecondition()) {
            Runnable runnable3 = new Runnable() { // from class: com.microsoft.appcenter.AppCenter.7
                @Override // java.lang.Runnable
                public void run() {
                    if (AppCenter.this.isInstanceEnabled()) {
                        runnable.run();
                        return;
                    }
                    Runnable runnable4 = runnable2;
                    if (runnable4 != null) {
                        runnable4.run();
                    } else {
                        AppCenterLog.error("AppCenter", "App Center SDK is disabled.");
                    }
                }
            };
            if (Thread.currentThread() == this.mHandlerThread) {
                runnable.run();
            } else {
                this.mHandler.post(runnable3);
            }
        }
    }

    public void finishConfiguration(boolean z) {
        Constants.loadFromContext(this.mApplication);
        FileManager.initialize(this.mApplication);
        SharedPreferencesManager.initialize(this.mApplication);
        SessionContext.getInstance();
        boolean isInstanceEnabled = isInstanceEnabled();
        HttpClient httpClient = DependencyConfiguration.getHttpClient();
        if (httpClient == null) {
            httpClient = HttpUtils.createHttpClient(this.mApplication);
        }
        DefaultLogSerializer defaultLogSerializer = new DefaultLogSerializer();
        this.mLogSerializer = defaultLogSerializer;
        defaultLogSerializer.addLogFactory("startService", new StartServiceLogFactory());
        this.mLogSerializer.addLogFactory("customProperties", new CustomPropertiesLogFactory());
        DefaultChannel defaultChannel = new DefaultChannel(this.mApplication, this.mAppSecret, this.mLogSerializer, httpClient, this.mHandler);
        this.mChannel = defaultChannel;
        if (z) {
            applyStorageMaxSize();
        } else {
            defaultChannel.setMaxStorageSize(10485760L);
        }
        this.mChannel.setEnabled(isInstanceEnabled);
        this.mChannel.addGroup("group_core", 50, 3000L, 3, null, null);
        this.mOneCollectorChannelListener = new OneCollectorChannelListener(this.mChannel, this.mLogSerializer, httpClient, IdHelper.getInstallId());
        if (this.mLogUrl != null) {
            if (this.mAppSecret != null) {
                AppCenterLog.info("AppCenter", "The log url of App Center endpoint has been changed to " + this.mLogUrl);
                this.mChannel.setLogUrl(this.mLogUrl);
            } else {
                AppCenterLog.info("AppCenter", "The log url of One Collector endpoint has been changed to " + this.mLogUrl);
                this.mOneCollectorChannelListener.setLogUrl(this.mLogUrl);
            }
        }
        this.mChannel.addListener(this.mOneCollectorChannelListener);
        if (!isInstanceEnabled) {
            NetworkStateHelper.getSharedInstance(this.mApplication).close();
        }
        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler(this.mHandler, this.mChannel);
        this.mUncaughtExceptionHandler = uncaughtExceptionHandler;
        if (isInstanceEnabled) {
            uncaughtExceptionHandler.register();
        }
        AppCenterLog.debug("AppCenter", "App Center initialized.");
    }

    public void applyStorageMaxSize() {
        boolean maxStorageSize = this.mChannel.setMaxStorageSize(this.mMaxStorageSizeInBytes);
        DefaultAppCenterFuture<Boolean> defaultAppCenterFuture = this.mSetMaxStorageSizeFuture;
        if (defaultAppCenterFuture != null) {
            defaultAppCenterFuture.complete(Boolean.valueOf(maxStorageSize));
        }
    }

    @SafeVarargs
    private final synchronized void startServices(final boolean z, Class<? extends AppCenterService>... clsArr) {
        if (clsArr == null) {
            AppCenterLog.error("AppCenter", "Cannot start services, services array is null. Failed to start services.");
            return;
        }
        if (this.mApplication == null) {
            StringBuilder sb = new StringBuilder();
            for (Class<? extends AppCenterService> cls : clsArr) {
                sb.append("\t");
                sb.append(cls.getName());
                sb.append("\n");
            }
            AppCenterLog.error("AppCenter", "Cannot start services, App Center has not been configured. Failed to start the following services:\n" + ((Object) sb));
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        for (Class<? extends AppCenterService> cls2 : clsArr) {
            if (cls2 == null) {
                AppCenterLog.warn("AppCenter", "Skipping null service, please check your varargs/array does not contain any null reference.");
            } else {
                try {
                    startOrUpdateService((AppCenterService) cls2.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]), arrayList, arrayList2, z);
                } catch (Exception e) {
                    AppCenterLog.error("AppCenter", "Failed to get service instance '" + cls2.getName() + "', skipping it.", e);
                }
            }
        }
        this.mHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.8
            @Override // java.lang.Runnable
            public void run() {
                AppCenter.this.finishStartServices(arrayList2, arrayList, z);
            }
        });
    }

    private void startOrUpdateService(AppCenterService appCenterService, Collection<AppCenterService> collection, Collection<AppCenterService> collection2, boolean z) {
        if (z) {
            startOrUpdateServiceFromApp(appCenterService, collection, collection2);
        } else if (this.mServices.contains(appCenterService)) {
        } else {
            startServiceFromLibrary(appCenterService, collection);
        }
    }

    private void startOrUpdateServiceFromApp(AppCenterService appCenterService, Collection<AppCenterService> collection, Collection<AppCenterService> collection2) {
        String serviceName = appCenterService.getServiceName();
        if (this.mServices.contains(appCenterService)) {
            if (this.mServicesStartedFromLibrary.remove(appCenterService)) {
                collection2.add(appCenterService);
                return;
            }
            AppCenterLog.warn("AppCenter", "App Center has already started the service with class name: " + appCenterService.getServiceName());
        } else if (this.mAppSecret == null && appCenterService.isAppSecretRequired()) {
            AppCenterLog.error("AppCenter", "App Center was started without app secret, but the service requires it; not starting service " + serviceName + ".");
        } else {
            startService(appCenterService, collection);
        }
    }

    private void startServiceFromLibrary(AppCenterService appCenterService, Collection<AppCenterService> collection) {
        String serviceName = appCenterService.getServiceName();
        if (appCenterService.isAppSecretRequired()) {
            AppCenterLog.error("AppCenter", "This service cannot be started from a library: " + serviceName + ".");
        } else if (!startService(appCenterService, collection)) {
        } else {
            this.mServicesStartedFromLibrary.add(appCenterService);
        }
    }

    private boolean startService(AppCenterService appCenterService, Collection<AppCenterService> collection) {
        String serviceName = appCenterService.getServiceName();
        if (ServiceInstrumentationUtils.isServiceDisabledByInstrumentation(serviceName)) {
            AppCenterLog.debug("AppCenter", "Instrumentation variable to disable service has been set; not starting service " + serviceName + ".");
            return false;
        }
        appCenterService.onStarting(this.mAppCenterHandler);
        this.mApplicationLifecycleListener.registerApplicationLifecycleCallbacks(appCenterService);
        this.mApplication.registerActivityLifecycleCallbacks(appCenterService);
        this.mServices.add(appCenterService);
        collection.add(appCenterService);
        return true;
    }

    public void finishStartServices(Iterable<AppCenterService> iterable, Iterable<AppCenterService> iterable2, boolean z) {
        for (AppCenterService appCenterService : iterable) {
            appCenterService.onConfigurationUpdated(this.mAppSecret, this.mTransmissionTargetToken);
            AppCenterLog.info("AppCenter", appCenterService.getClass().getSimpleName() + " service configuration updated.");
        }
        boolean isInstanceEnabled = isInstanceEnabled();
        for (AppCenterService appCenterService2 : iterable2) {
            Map<String, LogFactory> logFactories = appCenterService2.getLogFactories();
            if (logFactories != null) {
                for (Map.Entry<String, LogFactory> entry : logFactories.entrySet()) {
                    this.mLogSerializer.addLogFactory(entry.getKey(), entry.getValue());
                }
            }
            if (!isInstanceEnabled && appCenterService2.isInstanceEnabled()) {
                appCenterService2.setInstanceEnabled(false);
            }
            if (z) {
                appCenterService2.onStarted(this.mApplication, this.mChannel, this.mAppSecret, this.mTransmissionTargetToken, true);
                AppCenterLog.info("AppCenter", appCenterService2.getClass().getSimpleName() + " service started from application.");
            } else {
                appCenterService2.onStarted(this.mApplication, this.mChannel, null, null, false);
                AppCenterLog.info("AppCenter", appCenterService2.getClass().getSimpleName() + " service started from library.");
            }
        }
        if (z) {
            for (AppCenterService appCenterService3 : iterable) {
                this.mStartedServicesNamesToLog.add(appCenterService3.getServiceName());
            }
            for (AppCenterService appCenterService4 : iterable2) {
                this.mStartedServicesNamesToLog.add(appCenterService4.getServiceName());
            }
            sendStartServiceLog();
        }
    }

    private void sendStartServiceLog() {
        if (this.mStartedServicesNamesToLog.isEmpty() || !isInstanceEnabled()) {
            return;
        }
        ArrayList arrayList = new ArrayList(this.mStartedServicesNamesToLog);
        this.mStartedServicesNamesToLog.clear();
        StartServiceLog startServiceLog = new StartServiceLog();
        startServiceLog.setServices(arrayList);
        this.mChannel.enqueue(startServiceLog, "group_core", 1);
    }

    private synchronized void configureAndStartServices(Application application, String str, Class<? extends AppCenterService>[] clsArr) {
        if (str != null) {
            if (!str.isEmpty()) {
                configureAndStartServices(application, str, true, clsArr);
            }
        }
        AppCenterLog.error("AppCenter", "appSecret may not be null or empty.");
    }

    private void configureAndStartServices(Application application, String str, boolean z, Class<? extends AppCenterService>[] clsArr) {
        if (configureInstance(application, str, z)) {
            startServices(z, clsArr);
        }
    }

    public boolean isInstanceEnabled() {
        return SharedPreferencesManager.getBoolean("enabled", true);
    }

    public static void setUserId(String str) {
        getInstance().setInstanceUserId(str);
    }
}
