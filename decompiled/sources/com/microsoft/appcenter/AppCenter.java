package com.microsoft.appcenter;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.channel.DefaultChannel;
import com.microsoft.appcenter.channel.OneCollectorChannelListener;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.ingestion.models.CustomPropertiesLog;
import com.microsoft.appcenter.ingestion.models.StartServiceLog;
import com.microsoft.appcenter.ingestion.models.WrapperSdk;
import com.microsoft.appcenter.ingestion.models.json.CustomPropertiesLogFactory;
import com.microsoft.appcenter.ingestion.models.json.DefaultLogSerializer;
import com.microsoft.appcenter.ingestion.models.json.LogFactory;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.json.StartServiceLogFactory;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.ApplicationLifecycleListener;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.IdHelper;
import com.microsoft.appcenter.utils.InstrumentationRegistryHelper;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import com.microsoft.appcenter.utils.PrefStorageConstants;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
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
import java.util.UUID;
/* loaded from: classes3.dex */
public class AppCenter {
    static final String APP_SECRET_KEY = "appsecret";
    static final String CORE_GROUP = "group_core";
    static final long DEFAULT_MAX_STORAGE_SIZE_IN_BYTES = 10485760;
    static final String KEY_VALUE_DELIMITER = "=";
    public static final String LOG_TAG = "AppCenter";
    static final long MINIMUM_STORAGE_SIZE = 24576;
    static final String PAIR_DELIMITER = ";";
    static final String RUNNING_IN_APP_CENTER = "RUNNING_IN_APP_CENTER";
    static final String TRANSMISSION_TARGET_TOKEN_KEY = "target";
    private static final String TRUE_ENVIRONMENT_STRING = "1";
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
    private long mMaxStorageSizeInBytes = DEFAULT_MAX_STORAGE_SIZE_IN_BYTES;

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

    static synchronized void unsetInstance() {
        synchronized (AppCenter.class) {
            sInstance = null;
            NetworkStateHelper.unsetInstance();
        }
    }

    public static void setWrapperSdk(WrapperSdk wrapperSdk) {
        getInstance().setInstanceWrapperSdk(wrapperSdk);
    }

    public static int getLogLevel() {
        return AppCenterLog.getLogLevel();
    }

    public static void setLogLevel(int logLevel) {
        getInstance().setInstanceLogLevel(logLevel);
    }

    public static void setLogUrl(String logUrl) {
        getInstance().setInstanceLogUrl(logUrl);
    }

    public static String getSdkVersion() {
        return "3.3.1";
    }

    public static void setCustomProperties(CustomProperties customProperties) {
        getInstance().setInstanceCustomProperties(customProperties);
    }

    public static boolean isConfigured() {
        return getInstance().isInstanceConfigured();
    }

    public static boolean isRunningInAppCenterTestCloud() {
        try {
            Bundle arguments = InstrumentationRegistryHelper.getArguments();
            String runningValue = arguments.getString(RUNNING_IN_APP_CENTER);
            return "1".equals(runningValue);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void configure(Application application, String appSecret) {
        getInstance().configureInstanceWithRequiredAppSecret(application, appSecret);
    }

    public static void configure(Application application) {
        getInstance().configureInstance(application, null, true);
    }

    @SafeVarargs
    public static void start(Class<? extends AppCenterService>... services) {
        getInstance().startServices(true, services);
    }

    @SafeVarargs
    public static void start(Application application, String appSecret, Class<? extends AppCenterService>... services) {
        getInstance().configureAndStartServices(application, appSecret, services);
    }

    @SafeVarargs
    public static void start(Application application, Class<? extends AppCenterService>... services) {
        getInstance().configureAndStartServices(application, null, true, services);
    }

    @SafeVarargs
    public static void startFromLibrary(Context context, Class<? extends AppCenterService>... services) {
        getInstance().startInstanceFromLibrary(context, services);
    }

    public static AppCenterFuture<Boolean> isEnabled() {
        return getInstance().isInstanceEnabledAsync();
    }

    public static AppCenterFuture<Void> setEnabled(boolean enabled) {
        return getInstance().setInstanceEnabledAsync(enabled);
    }

    public static AppCenterFuture<UUID> getInstallId() {
        return getInstance().getInstanceInstallIdAsync();
    }

    public static AppCenterFuture<Boolean> setMaxStorageSize(long storageSizeInBytes) {
        return getInstance().setInstanceMaxStorageSizeAsync(storageSizeInBytes);
    }

    private synchronized void setInstanceUserId(String userId) {
        if (!this.mConfiguredFromApp) {
            AppCenterLog.error("AppCenter", "AppCenter must be configured from application, libraries cannot use call setUserId.");
            return;
        }
        String str = this.mAppSecret;
        if (str == null && this.mTransmissionTargetToken == null) {
            AppCenterLog.error("AppCenter", "AppCenter must be configured with a secret from application to call setUserId.");
            return;
        }
        if (userId != null) {
            if (str != null && !UserIdContext.checkUserIdValidForAppCenter(userId)) {
                return;
            }
            if (this.mTransmissionTargetToken != null && !UserIdContext.checkUserIdValidForOneCollector(userId)) {
                return;
            }
        }
        UserIdContext.getInstance().setUserId(userId);
    }

    private synchronized boolean checkPrecondition() {
        if (isInstanceConfigured()) {
            return true;
        }
        AppCenterLog.error("AppCenter", "App Center hasn't been configured. You need to call AppCenter.start with appSecret or AppCenter.configure first.");
        return false;
    }

    private synchronized void setInstanceWrapperSdk(WrapperSdk wrapperSdk) {
        DeviceInfoHelper.setWrapperSdk(wrapperSdk);
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.1
                @Override // java.lang.Runnable
                public void run() {
                    AppCenter.this.mChannel.invalidateDeviceCache();
                }
            });
        }
    }

    private synchronized void setInstanceLogLevel(int logLevel) {
        this.mLogLevelConfigured = true;
        AppCenterLog.setLogLevel(logLevel);
    }

    private synchronized void setInstanceLogUrl(final String logUrl) {
        this.mLogUrl = logUrl;
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.2
                @Override // java.lang.Runnable
                public void run() {
                    if (AppCenter.this.mAppSecret != null) {
                        AppCenterLog.info("AppCenter", "The log url of App Center endpoint has been changed to " + logUrl);
                        AppCenter.this.mChannel.setLogUrl(logUrl);
                        return;
                    }
                    AppCenterLog.info("AppCenter", "The log url of One Collector endpoint has been changed to " + logUrl);
                    AppCenter.this.mOneCollectorChannelListener.setLogUrl(logUrl);
                }
            });
        }
    }

    private synchronized void setInstanceCustomProperties(CustomProperties customProperties) {
        if (customProperties == null) {
            AppCenterLog.error("AppCenter", "Custom properties may not be null.");
            return;
        }
        final Map<String, Object> properties = customProperties.getProperties();
        if (properties.size() == 0) {
            AppCenterLog.error("AppCenter", "Custom properties may not be empty.");
        } else {
            handlerAppCenterOperation(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.3
                @Override // java.lang.Runnable
                public void run() {
                    AppCenter.this.queueCustomProperties(properties);
                }
            }, null);
        }
    }

    private synchronized AppCenterFuture<Boolean> setInstanceMaxStorageSizeAsync(long storageSizeInBytes) {
        DefaultAppCenterFuture<Boolean> setMaxStorageSizeFuture = new DefaultAppCenterFuture<>();
        if (this.mConfiguredFromApp) {
            AppCenterLog.error("AppCenter", "setMaxStorageSize may not be called after App Center has been configured.");
            setMaxStorageSizeFuture.complete(false);
            return setMaxStorageSizeFuture;
        } else if (storageSizeInBytes < MINIMUM_STORAGE_SIZE) {
            AppCenterLog.error("AppCenter", "Maximum storage size must be at least 24576 bytes.");
            setMaxStorageSizeFuture.complete(false);
            return setMaxStorageSizeFuture;
        } else if (this.mSetMaxStorageSizeFuture != null) {
            AppCenterLog.error("AppCenter", "setMaxStorageSize may only be called once per app launch.");
            setMaxStorageSizeFuture.complete(false);
            return setMaxStorageSizeFuture;
        } else {
            this.mMaxStorageSizeInBytes = storageSizeInBytes;
            this.mSetMaxStorageSizeFuture = setMaxStorageSizeFuture;
            return setMaxStorageSizeFuture;
        }
    }

    private synchronized boolean isInstanceConfigured() {
        return this.mApplication != null;
    }

    private void configureInstanceWithRequiredAppSecret(Application application, String appSecret) {
        if (appSecret == null || appSecret.isEmpty()) {
            AppCenterLog.error("AppCenter", "appSecret may not be null or empty.");
        } else {
            configureInstance(application, appSecret, true);
        }
    }

    private synchronized boolean configureInstance(Application application, String secretString, final boolean configureFromApp) {
        if (application == null) {
            AppCenterLog.error("AppCenter", "Application context may not be null.");
            return false;
        }
        if (!this.mLogLevelConfigured && (application.getApplicationInfo().flags & 2) == 2) {
            AppCenterLog.setLogLevel(5);
        }
        String previousAppSecret = this.mAppSecret;
        if (configureFromApp && !configureSecretString(secretString)) {
            return false;
        }
        if (this.mHandler != null) {
            String str = this.mAppSecret;
            if (str != null && !str.equals(previousAppSecret)) {
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
            public void post(Runnable runnable, Runnable disabledRunnable) {
                AppCenter.this.handlerAppCenterOperation(runnable, disabledRunnable);
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
                AppCenter.this.finishConfiguration(configureFromApp);
            }
        });
        AppCenterLog.info("AppCenter", "App Center SDK configured successfully.");
        return true;
    }

    private boolean configureSecretString(String secretString) {
        if (this.mConfiguredFromApp) {
            AppCenterLog.warn("AppCenter", "App Center may only be configured once.");
            return false;
        }
        this.mConfiguredFromApp = true;
        if (secretString != null) {
            String[] pairs = secretString.split(PAIR_DELIMITER);
            for (String pair : pairs) {
                String[] keyValue = pair.split(KEY_VALUE_DELIMITER, -1);
                String key = keyValue[0];
                if (keyValue.length == 1) {
                    if (!key.isEmpty()) {
                        this.mAppSecret = key;
                    }
                } else if (!keyValue[1].isEmpty()) {
                    String value = keyValue[1];
                    if (APP_SECRET_KEY.equals(key)) {
                        this.mAppSecret = value;
                    } else if (TRANSMISSION_TARGET_TOKEN_KEY.equals(key)) {
                        this.mTransmissionTargetToken = value;
                    }
                }
            }
        }
        return true;
    }

    public synchronized void handlerAppCenterOperation(final Runnable runnable, final Runnable disabledRunnable) {
        if (checkPrecondition()) {
            Runnable wrapperRunnable = new Runnable() { // from class: com.microsoft.appcenter.AppCenter.7
                @Override // java.lang.Runnable
                public void run() {
                    if (AppCenter.this.isInstanceEnabled()) {
                        runnable.run();
                        return;
                    }
                    Runnable runnable2 = disabledRunnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    } else {
                        AppCenterLog.error("AppCenter", "App Center SDK is disabled.");
                    }
                }
            };
            if (Thread.currentThread() == this.mHandlerThread) {
                runnable.run();
            } else {
                this.mHandler.post(wrapperRunnable);
            }
        }
    }

    public void finishConfiguration(boolean configureFromApp) {
        Constants.loadFromContext(this.mApplication);
        FileManager.initialize(this.mApplication);
        SharedPreferencesManager.initialize(this.mApplication);
        SessionContext.getInstance();
        boolean enabled = isInstanceEnabled();
        HttpClient httpClient = DependencyConfiguration.getHttpClient();
        if (httpClient == null) {
            httpClient = HttpUtils.createHttpClient(this.mApplication);
        }
        DefaultLogSerializer defaultLogSerializer = new DefaultLogSerializer();
        this.mLogSerializer = defaultLogSerializer;
        defaultLogSerializer.addLogFactory(StartServiceLog.TYPE, new StartServiceLogFactory());
        this.mLogSerializer.addLogFactory(CustomPropertiesLog.TYPE, new CustomPropertiesLogFactory());
        DefaultChannel defaultChannel = new DefaultChannel(this.mApplication, this.mAppSecret, this.mLogSerializer, httpClient, this.mHandler);
        this.mChannel = defaultChannel;
        if (configureFromApp) {
            applyStorageMaxSize();
        } else {
            defaultChannel.setMaxStorageSize(DEFAULT_MAX_STORAGE_SIZE_IN_BYTES);
        }
        this.mChannel.setEnabled(enabled);
        this.mChannel.addGroup(CORE_GROUP, 50, 3000L, 3, null, null);
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
        if (!enabled) {
            NetworkStateHelper.getSharedInstance(this.mApplication).close();
        }
        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler(this.mHandler, this.mChannel);
        this.mUncaughtExceptionHandler = uncaughtExceptionHandler;
        if (enabled) {
            uncaughtExceptionHandler.register();
        }
        AppCenterLog.debug("AppCenter", "App Center initialized.");
    }

    public void applyStorageMaxSize() {
        boolean resizeResult = this.mChannel.setMaxStorageSize(this.mMaxStorageSizeInBytes);
        DefaultAppCenterFuture<Boolean> defaultAppCenterFuture = this.mSetMaxStorageSizeFuture;
        if (defaultAppCenterFuture != null) {
            defaultAppCenterFuture.complete(Boolean.valueOf(resizeResult));
        }
    }

    @SafeVarargs
    private final synchronized void startServices(final boolean startFromApp, Class<? extends AppCenterService>... services) {
        if (services == null) {
            AppCenterLog.error("AppCenter", "Cannot start services, services array is null. Failed to start services.");
            return;
        }
        if (this.mApplication == null) {
            StringBuilder serviceNames = new StringBuilder();
            for (Class<? extends AppCenterService> service : services) {
                serviceNames.append("\t");
                serviceNames.append(service.getName());
                serviceNames.append("\n");
            }
            AppCenterLog.error("AppCenter", "Cannot start services, App Center has not been configured. Failed to start the following services:\n" + ((Object) serviceNames));
            return;
        }
        final Collection<AppCenterService> startedServices = new ArrayList<>();
        final Collection<AppCenterService> updatedServices = new ArrayList<>();
        for (Class<? extends AppCenterService> service2 : services) {
            if (service2 == null) {
                AppCenterLog.warn("AppCenter", "Skipping null service, please check your varargs/array does not contain any null reference.");
            } else {
                try {
                    AppCenterService serviceInstance = (AppCenterService) service2.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
                    startOrUpdateService(serviceInstance, startedServices, updatedServices, startFromApp);
                } catch (Exception e) {
                    AppCenterLog.error("AppCenter", "Failed to get service instance '" + service2.getName() + "', skipping it.", e);
                }
            }
        }
        this.mHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.8
            @Override // java.lang.Runnable
            public void run() {
                AppCenter.this.finishStartServices(updatedServices, startedServices, startFromApp);
            }
        });
    }

    private void startOrUpdateService(AppCenterService serviceInstance, Collection<AppCenterService> startedServices, Collection<AppCenterService> updatedServices, boolean startFromApp) {
        if (startFromApp) {
            startOrUpdateServiceFromApp(serviceInstance, startedServices, updatedServices);
        } else if (!this.mServices.contains(serviceInstance)) {
            startServiceFromLibrary(serviceInstance, startedServices);
        }
    }

    private void startOrUpdateServiceFromApp(AppCenterService serviceInstance, Collection<AppCenterService> startedServices, Collection<AppCenterService> updatedServices) {
        String serviceName = serviceInstance.getServiceName();
        if (this.mServices.contains(serviceInstance)) {
            if (this.mServicesStartedFromLibrary.remove(serviceInstance)) {
                updatedServices.add(serviceInstance);
                return;
            }
            AppCenterLog.warn("AppCenter", "App Center has already started the service with class name: " + serviceInstance.getServiceName());
        } else if (this.mAppSecret == null && serviceInstance.isAppSecretRequired()) {
            AppCenterLog.error("AppCenter", "App Center was started without app secret, but the service requires it; not starting service " + serviceName + ".");
        } else {
            startService(serviceInstance, startedServices);
        }
    }

    private void startServiceFromLibrary(AppCenterService serviceInstance, Collection<AppCenterService> startedServices) {
        String serviceName = serviceInstance.getServiceName();
        if (serviceInstance.isAppSecretRequired()) {
            AppCenterLog.error("AppCenter", "This service cannot be started from a library: " + serviceName + ".");
        } else if (startService(serviceInstance, startedServices)) {
            this.mServicesStartedFromLibrary.add(serviceInstance);
        }
    }

    private boolean startService(AppCenterService serviceInstance, Collection<AppCenterService> startedServices) {
        String serviceName = serviceInstance.getServiceName();
        if (ServiceInstrumentationUtils.isServiceDisabledByInstrumentation(serviceName)) {
            AppCenterLog.debug("AppCenter", "Instrumentation variable to disable service has been set; not starting service " + serviceName + ".");
            return false;
        }
        serviceInstance.onStarting(this.mAppCenterHandler);
        this.mApplicationLifecycleListener.registerApplicationLifecycleCallbacks(serviceInstance);
        this.mApplication.registerActivityLifecycleCallbacks(serviceInstance);
        this.mServices.add(serviceInstance);
        startedServices.add(serviceInstance);
        return true;
    }

    public void finishStartServices(Iterable<AppCenterService> updatedServices, Iterable<AppCenterService> startedServices, boolean startFromApp) {
        for (AppCenterService service : updatedServices) {
            service.onConfigurationUpdated(this.mAppSecret, this.mTransmissionTargetToken);
            AppCenterLog.info("AppCenter", service.getClass().getSimpleName() + " service configuration updated.");
        }
        boolean enabled = isInstanceEnabled();
        for (AppCenterService service2 : startedServices) {
            Map<String, LogFactory> logFactories = service2.getLogFactories();
            if (logFactories != null) {
                for (Map.Entry<String, LogFactory> logFactory : logFactories.entrySet()) {
                    this.mLogSerializer.addLogFactory(logFactory.getKey(), logFactory.getValue());
                }
            }
            if (!enabled && service2.isInstanceEnabled()) {
                service2.setInstanceEnabled(false);
            }
            if (startFromApp) {
                service2.onStarted(this.mApplication, this.mChannel, this.mAppSecret, this.mTransmissionTargetToken, true);
                AppCenterLog.info("AppCenter", service2.getClass().getSimpleName() + " service started from application.");
            } else {
                service2.onStarted(this.mApplication, this.mChannel, null, null, false);
                AppCenterLog.info("AppCenter", service2.getClass().getSimpleName() + " service started from library.");
            }
        }
        if (startFromApp) {
            for (AppCenterService service3 : updatedServices) {
                this.mStartedServicesNamesToLog.add(service3.getServiceName());
            }
            for (AppCenterService service4 : startedServices) {
                this.mStartedServicesNamesToLog.add(service4.getServiceName());
            }
            sendStartServiceLog();
        }
    }

    private void sendStartServiceLog() {
        if (!this.mStartedServicesNamesToLog.isEmpty() && isInstanceEnabled()) {
            List<String> allServiceNamesToStart = new ArrayList<>(this.mStartedServicesNamesToLog);
            this.mStartedServicesNamesToLog.clear();
            StartServiceLog startServiceLog = new StartServiceLog();
            startServiceLog.setServices(allServiceNamesToStart);
            this.mChannel.enqueue(startServiceLog, CORE_GROUP, 1);
        }
    }

    private synchronized void configureAndStartServices(Application application, String appSecret, Class<? extends AppCenterService>[] services) {
        if (appSecret != null) {
            if (!appSecret.isEmpty()) {
                configureAndStartServices(application, appSecret, true, services);
            }
        }
        AppCenterLog.error("AppCenter", "appSecret may not be null or empty.");
    }

    private synchronized void startInstanceFromLibrary(Context context, Class<? extends AppCenterService>[] services) {
        Application application;
        if (context != null) {
            try {
                application = (Application) context.getApplicationContext();
            } catch (Throwable th) {
                throw th;
            }
        } else {
            application = null;
        }
        configureAndStartServices(application, null, false, services);
    }

    private void configureAndStartServices(Application application, String appSecret, boolean startFromApp, Class<? extends AppCenterService>[] services) {
        boolean configuredSuccessfully = configureInstance(application, appSecret, startFromApp);
        if (configuredSuccessfully) {
            startServices(startFromApp, services);
        }
    }

    public void queueCustomProperties(Map<String, Object> properties) {
        CustomPropertiesLog customPropertiesLog = new CustomPropertiesLog();
        customPropertiesLog.setProperties(properties);
        this.mChannel.enqueue(customPropertiesLog, CORE_GROUP, 1);
    }

    private synchronized AppCenterFuture<Boolean> isInstanceEnabledAsync() {
        final DefaultAppCenterFuture<Boolean> future;
        future = new DefaultAppCenterFuture<>();
        if (checkPrecondition()) {
            this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.9
                @Override // java.lang.Runnable
                public void run() {
                    future.complete(true);
                }
            }, new Runnable() { // from class: com.microsoft.appcenter.AppCenter.10
                @Override // java.lang.Runnable
                public void run() {
                    future.complete(false);
                }
            });
        } else {
            future.complete(false);
        }
        return future;
    }

    public boolean isInstanceEnabled() {
        return SharedPreferencesManager.getBoolean(PrefStorageConstants.KEY_ENABLED, true);
    }

    public void setInstanceEnabled(boolean enabled) {
        this.mChannel.setEnabled(enabled);
        boolean previouslyEnabled = isInstanceEnabled();
        boolean switchToDisabled = previouslyEnabled && !enabled;
        boolean switchToEnabled = !previouslyEnabled && enabled;
        if (switchToEnabled) {
            this.mUncaughtExceptionHandler.register();
            NetworkStateHelper.getSharedInstance(this.mApplication).reopen();
        } else if (switchToDisabled) {
            this.mUncaughtExceptionHandler.unregister();
            NetworkStateHelper.getSharedInstance(this.mApplication).close();
        }
        String str = PrefStorageConstants.KEY_ENABLED;
        if (enabled) {
            SharedPreferencesManager.putBoolean(str, true);
        }
        if (!this.mStartedServicesNamesToLog.isEmpty() && switchToEnabled) {
            sendStartServiceLog();
        }
        for (AppCenterService service : this.mServices) {
            if (service.isInstanceEnabled() != enabled) {
                service.setInstanceEnabled(enabled);
            }
        }
        if (!enabled) {
            SharedPreferencesManager.putBoolean(str, false);
        }
        if (switchToDisabled) {
            AppCenterLog.info("AppCenter", "App Center has been disabled.");
        } else if (switchToEnabled) {
            AppCenterLog.info("AppCenter", "App Center has been enabled.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("App Center has already been ");
            if (!enabled) {
                str = "disabled";
            }
            sb.append(str);
            sb.append(".");
            AppCenterLog.info("AppCenter", sb.toString());
        }
    }

    private synchronized AppCenterFuture<Void> setInstanceEnabledAsync(final boolean enabled) {
        final DefaultAppCenterFuture<Void> future;
        future = new DefaultAppCenterFuture<>();
        if (checkPrecondition()) {
            this.mHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.11
                @Override // java.lang.Runnable
                public void run() {
                    AppCenter.this.setInstanceEnabled(enabled);
                    future.complete(null);
                }
            });
        } else {
            future.complete(null);
        }
        return future;
    }

    private synchronized AppCenterFuture<UUID> getInstanceInstallIdAsync() {
        final DefaultAppCenterFuture<UUID> future;
        future = new DefaultAppCenterFuture<>();
        if (checkPrecondition()) {
            this.mAppCenterHandler.post(new Runnable() { // from class: com.microsoft.appcenter.AppCenter.12
                @Override // java.lang.Runnable
                public void run() {
                    future.complete(IdHelper.getInstallId());
                }
            }, new Runnable() { // from class: com.microsoft.appcenter.AppCenter.13
                @Override // java.lang.Runnable
                public void run() {
                    future.complete(null);
                }
            });
        } else {
            future.complete(null);
        }
        return future;
    }

    public static void setUserId(String userId) {
        getInstance().setInstanceUserId(userId);
    }

    Set<AppCenterService> getServices() {
        return this.mServices;
    }

    Application getApplication() {
        return this.mApplication;
    }

    UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.mUncaughtExceptionHandler;
    }

    public void setChannel(Channel channel) {
        this.mChannel = channel;
    }
}
