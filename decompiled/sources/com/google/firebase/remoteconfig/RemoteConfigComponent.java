package com.google.firebase.remoteconfig;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.common.util.BiConsumer;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.DefaultClock;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.inject.Provider;
import com.google.firebase.installations.FirebaseInstallationsApi;
import com.google.firebase.remoteconfig.internal.ConfigCacheClient;
import com.google.firebase.remoteconfig.internal.ConfigContainer;
import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;
import com.google.firebase.remoteconfig.internal.ConfigFetchHttpClient;
import com.google.firebase.remoteconfig.internal.ConfigGetParameterHandler;
import com.google.firebase.remoteconfig.internal.ConfigMetadataClient;
import com.google.firebase.remoteconfig.internal.ConfigStorageClient;
import com.google.firebase.remoteconfig.internal.Personalization;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes3.dex */
public class RemoteConfigComponent {
    public static final String ACTIVATE_FILE_NAME = "activate";
    public static final long CONNECTION_TIMEOUT_IN_SECONDS = 60;
    public static final String DEFAULTS_FILE_NAME = "defaults";
    public static final String DEFAULT_NAMESPACE = "firebase";
    public static final String FETCH_FILE_NAME = "fetch";
    private static final String FIREBASE_REMOTE_CONFIG_FILE_NAME_PREFIX = "frc";
    private static final String PREFERENCES_FILE_NAME = "settings";
    private final Provider<AnalyticsConnector> analyticsConnector;
    private final String appId;
    private final Context context;
    private Map<String, String> customHeaders;
    private final ExecutorService executorService;
    private final FirebaseABTesting firebaseAbt;
    private final FirebaseApp firebaseApp;
    private final FirebaseInstallationsApi firebaseInstallations;
    private final Map<String, FirebaseRemoteConfig> frcNamespaceInstances;
    private static final Clock DEFAULT_CLOCK = DefaultClock.getInstance();
    private static final Random DEFAULT_RANDOM = new Random();

    public RemoteConfigComponent(Context context, FirebaseApp firebaseApp, FirebaseInstallationsApi firebaseInstallations, FirebaseABTesting firebaseAbt, Provider<AnalyticsConnector> analyticsConnector) {
        this(context, Executors.newCachedThreadPool(), firebaseApp, firebaseInstallations, firebaseAbt, analyticsConnector, true);
    }

    protected RemoteConfigComponent(Context context, ExecutorService executorService, FirebaseApp firebaseApp, FirebaseInstallationsApi firebaseInstallations, FirebaseABTesting firebaseAbt, Provider<AnalyticsConnector> analyticsConnector, boolean loadGetDefault) {
        this.frcNamespaceInstances = new HashMap();
        this.customHeaders = new HashMap();
        this.context = context;
        this.executorService = executorService;
        this.firebaseApp = firebaseApp;
        this.firebaseInstallations = firebaseInstallations;
        this.firebaseAbt = firebaseAbt;
        this.analyticsConnector = analyticsConnector;
        this.appId = firebaseApp.getOptions().getApplicationId();
        if (loadGetDefault) {
            Tasks.call(executorService, new Callable() { // from class: com.google.firebase.remoteconfig.RemoteConfigComponent$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return RemoteConfigComponent.this.getDefault();
                }
            });
        }
    }

    public FirebaseRemoteConfig getDefault() {
        return get(DEFAULT_NAMESPACE);
    }

    public synchronized FirebaseRemoteConfig get(String namespace) {
        ConfigCacheClient fetchedCacheClient;
        ConfigCacheClient activatedCacheClient;
        ConfigCacheClient defaultsCacheClient;
        ConfigMetadataClient metadataClient;
        ConfigGetParameterHandler getHandler;
        fetchedCacheClient = getCacheClient(namespace, FETCH_FILE_NAME);
        activatedCacheClient = getCacheClient(namespace, ACTIVATE_FILE_NAME);
        defaultsCacheClient = getCacheClient(namespace, DEFAULTS_FILE_NAME);
        metadataClient = getMetadataClient(this.context, this.appId, namespace);
        getHandler = getGetHandler(activatedCacheClient, defaultsCacheClient);
        final Personalization personalization = getPersonalization(this.firebaseApp, namespace, this.analyticsConnector);
        if (personalization != null) {
            personalization.getClass();
            getHandler.addListener(new BiConsumer() { // from class: com.google.firebase.remoteconfig.RemoteConfigComponent$$ExternalSyntheticLambda0
                @Override // com.google.android.gms.common.util.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    Personalization.this.logArmActive((String) obj, (ConfigContainer) obj2);
                }
            });
        }
        return get(this.firebaseApp, namespace, this.firebaseInstallations, this.firebaseAbt, this.executorService, fetchedCacheClient, activatedCacheClient, defaultsCacheClient, getFetchHandler(namespace, fetchedCacheClient, metadataClient), getHandler, metadataClient);
    }

    synchronized FirebaseRemoteConfig get(FirebaseApp firebaseApp, String namespace, FirebaseInstallationsApi firebaseInstallations, FirebaseABTesting firebaseAbt, Executor executor, ConfigCacheClient fetchedClient, ConfigCacheClient activatedClient, ConfigCacheClient defaultsClient, ConfigFetchHandler fetchHandler, ConfigGetParameterHandler getHandler, ConfigMetadataClient metadataClient) {
        if (!this.frcNamespaceInstances.containsKey(namespace)) {
            FirebaseRemoteConfig in = new FirebaseRemoteConfig(this.context, firebaseApp, firebaseInstallations, isAbtSupported(firebaseApp, namespace) ? firebaseAbt : null, executor, fetchedClient, activatedClient, defaultsClient, fetchHandler, getHandler, metadataClient);
            in.startLoadingConfigsFromDisk();
            this.frcNamespaceInstances.put(namespace, in);
        }
        return this.frcNamespaceInstances.get(namespace);
    }

    public synchronized void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }

    private ConfigCacheClient getCacheClient(String namespace, String configStoreType) {
        String fileName = String.format("%s_%s_%s_%s.json", "frc", this.appId, namespace, configStoreType);
        return ConfigCacheClient.getInstance(Executors.newCachedThreadPool(), ConfigStorageClient.getInstance(this.context, fileName));
    }

    ConfigFetchHttpClient getFrcBackendApiClient(String apiKey, String namespace, ConfigMetadataClient metadataClient) {
        String appId = this.firebaseApp.getOptions().getApplicationId();
        return new ConfigFetchHttpClient(this.context, appId, apiKey, namespace, metadataClient.getFetchTimeoutInSeconds(), metadataClient.getFetchTimeoutInSeconds());
    }

    synchronized ConfigFetchHandler getFetchHandler(String namespace, ConfigCacheClient fetchedCacheClient, ConfigMetadataClient metadataClient) {
        return new ConfigFetchHandler(this.firebaseInstallations, isPrimaryApp(this.firebaseApp) ? this.analyticsConnector : RemoteConfigComponent$$ExternalSyntheticLambda1.INSTANCE, this.executorService, DEFAULT_CLOCK, DEFAULT_RANDOM, fetchedCacheClient, getFrcBackendApiClient(this.firebaseApp.getOptions().getApiKey(), namespace, metadataClient), metadataClient, this.customHeaders);
    }

    public static /* synthetic */ AnalyticsConnector lambda$getFetchHandler$0() {
        return null;
    }

    private ConfigGetParameterHandler getGetHandler(ConfigCacheClient activatedCacheClient, ConfigCacheClient defaultsCacheClient) {
        return new ConfigGetParameterHandler(this.executorService, activatedCacheClient, defaultsCacheClient);
    }

    static ConfigMetadataClient getMetadataClient(Context context, String appId, String namespace) {
        String fileName = String.format("%s_%s_%s_%s", "frc", appId, namespace, PREFERENCES_FILE_NAME);
        SharedPreferences preferences = context.getSharedPreferences(fileName, 0);
        return new ConfigMetadataClient(preferences);
    }

    private static Personalization getPersonalization(FirebaseApp firebaseApp, String namespace, Provider<AnalyticsConnector> analyticsConnector) {
        if (isPrimaryApp(firebaseApp) && namespace.equals(DEFAULT_NAMESPACE)) {
            return new Personalization(analyticsConnector);
        }
        return null;
    }

    private static boolean isAbtSupported(FirebaseApp firebaseApp, String namespace) {
        return namespace.equals(DEFAULT_NAMESPACE) && isPrimaryApp(firebaseApp);
    }

    private static boolean isPrimaryApp(FirebaseApp firebaseApp) {
        return firebaseApp.getName().equals(FirebaseApp.DEFAULT_APP_NAME);
    }
}
