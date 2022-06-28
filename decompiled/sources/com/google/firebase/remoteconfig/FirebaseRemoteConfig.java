package com.google.firebase.remoteconfig;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.abt.AbtException;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.installations.FirebaseInstallationsApi;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.remoteconfig.internal.ConfigCacheClient;
import com.google.firebase.remoteconfig.internal.ConfigContainer;
import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;
import com.google.firebase.remoteconfig.internal.ConfigGetParameterHandler;
import com.google.firebase.remoteconfig.internal.ConfigMetadataClient;
import com.google.firebase.remoteconfig.internal.DefaultsXmlParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class FirebaseRemoteConfig {
    public static final boolean DEFAULT_VALUE_FOR_BOOLEAN = false;
    public static final byte[] DEFAULT_VALUE_FOR_BYTE_ARRAY = new byte[0];
    public static final double DEFAULT_VALUE_FOR_DOUBLE = 0.0d;
    public static final long DEFAULT_VALUE_FOR_LONG = 0;
    public static final String DEFAULT_VALUE_FOR_STRING = "";
    public static final int LAST_FETCH_STATUS_FAILURE = 1;
    public static final int LAST_FETCH_STATUS_NO_FETCH_YET = 0;
    public static final int LAST_FETCH_STATUS_SUCCESS = -1;
    public static final int LAST_FETCH_STATUS_THROTTLED = 2;
    public static final String TAG = "FirebaseRemoteConfig";
    public static final int VALUE_SOURCE_DEFAULT = 1;
    public static final int VALUE_SOURCE_REMOTE = 2;
    public static final int VALUE_SOURCE_STATIC = 0;
    private final ConfigCacheClient activatedConfigsCache;
    private final Context context;
    private final ConfigCacheClient defaultConfigsCache;
    private final Executor executor;
    private final ConfigFetchHandler fetchHandler;
    private final ConfigCacheClient fetchedConfigsCache;
    private final FirebaseABTesting firebaseAbt;
    private final FirebaseApp firebaseApp;
    private final FirebaseInstallationsApi firebaseInstallations;
    private final ConfigMetadataClient frcMetadata;
    private final ConfigGetParameterHandler getHandler;

    public static FirebaseRemoteConfig getInstance() {
        return getInstance(FirebaseApp.getInstance());
    }

    public static FirebaseRemoteConfig getInstance(FirebaseApp app) {
        return ((RemoteConfigComponent) app.get(RemoteConfigComponent.class)).getDefault();
    }

    public FirebaseRemoteConfig(Context context, FirebaseApp firebaseApp, FirebaseInstallationsApi firebaseInstallations, FirebaseABTesting firebaseAbt, Executor executor, ConfigCacheClient fetchedConfigsCache, ConfigCacheClient activatedConfigsCache, ConfigCacheClient defaultConfigsCache, ConfigFetchHandler fetchHandler, ConfigGetParameterHandler getHandler, ConfigMetadataClient frcMetadata) {
        this.context = context;
        this.firebaseApp = firebaseApp;
        this.firebaseInstallations = firebaseInstallations;
        this.firebaseAbt = firebaseAbt;
        this.executor = executor;
        this.fetchedConfigsCache = fetchedConfigsCache;
        this.activatedConfigsCache = activatedConfigsCache;
        this.defaultConfigsCache = defaultConfigsCache;
        this.fetchHandler = fetchHandler;
        this.getHandler = getHandler;
        this.frcMetadata = frcMetadata;
    }

    public Task<FirebaseRemoteConfigInfo> ensureInitialized() {
        Task<ConfigContainer> activatedConfigsTask = this.activatedConfigsCache.get();
        Task<ConfigContainer> defaultsConfigsTask = this.defaultConfigsCache.get();
        Task<ConfigContainer> fetchedConfigsTask = this.fetchedConfigsCache.get();
        final Task<FirebaseRemoteConfigInfo> metadataTask = Tasks.call(this.executor, new Callable() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda7
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return FirebaseRemoteConfig.this.getInfo();
            }
        });
        Task<String> installationIdTask = this.firebaseInstallations.getId();
        Task<InstallationTokenResult> installationTokenTask = this.firebaseInstallations.getToken(false);
        return Tasks.whenAllComplete(activatedConfigsTask, defaultsConfigsTask, fetchedConfigsTask, metadataTask, installationIdTask, installationTokenTask).continueWith(this.executor, new Continuation() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda0
            @Override // com.google.android.gms.tasks.Continuation
            public final Object then(Task task) {
                return FirebaseRemoteConfig.lambda$ensureInitialized$0(Task.this, task);
            }
        });
    }

    public static /* synthetic */ FirebaseRemoteConfigInfo lambda$ensureInitialized$0(Task metadataTask, Task unusedListOfCompletedTasks) throws Exception {
        return (FirebaseRemoteConfigInfo) metadataTask.getResult();
    }

    public Task<Boolean> fetchAndActivate() {
        return fetch().onSuccessTask(this.executor, new SuccessContinuation() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda3
            @Override // com.google.android.gms.tasks.SuccessContinuation
            public final Task then(Object obj) {
                return FirebaseRemoteConfig.this.m94xebd5994c((Void) obj);
            }
        });
    }

    /* renamed from: lambda$fetchAndActivate$1$com-google-firebase-remoteconfig-FirebaseRemoteConfig */
    public /* synthetic */ Task m94xebd5994c(Void unusedVoid) throws Exception {
        return activate();
    }

    public Task<Boolean> activate() {
        final Task<ConfigContainer> fetchedConfigsTask = this.fetchedConfigsCache.get();
        final Task<ConfigContainer> activatedConfigsTask = this.activatedConfigsCache.get();
        return Tasks.whenAllComplete(fetchedConfigsTask, activatedConfigsTask).continueWithTask(this.executor, new Continuation() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda2
            @Override // com.google.android.gms.tasks.Continuation
            public final Object then(Task task) {
                return FirebaseRemoteConfig.this.m93x98ca96b0(fetchedConfigsTask, activatedConfigsTask, task);
            }
        });
    }

    /* renamed from: lambda$activate$2$com-google-firebase-remoteconfig-FirebaseRemoteConfig */
    public /* synthetic */ Task m93x98ca96b0(Task fetchedConfigsTask, Task activatedConfigsTask, Task unusedListOfCompletedTasks) throws Exception {
        if (!fetchedConfigsTask.isSuccessful() || fetchedConfigsTask.getResult() == null) {
            return Tasks.forResult(false);
        }
        ConfigContainer fetchedContainer = (ConfigContainer) fetchedConfigsTask.getResult();
        if (activatedConfigsTask.isSuccessful()) {
            ConfigContainer activatedContainer = (ConfigContainer) activatedConfigsTask.getResult();
            if (!isFetchedFresh(fetchedContainer, activatedContainer)) {
                return Tasks.forResult(false);
            }
        }
        return this.activatedConfigsCache.put(fetchedContainer).continueWith(this.executor, new Continuation() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda1
            @Override // com.google.android.gms.tasks.Continuation
            public final Object then(Task task) {
                boolean processActivatePutTask;
                processActivatePutTask = FirebaseRemoteConfig.this.processActivatePutTask(task);
                return Boolean.valueOf(processActivatePutTask);
            }
        });
    }

    public Task<Void> fetch() {
        Task<ConfigFetchHandler.FetchResponse> fetchTask = this.fetchHandler.fetch();
        return fetchTask.onSuccessTask(FirebaseRemoteConfig$$ExternalSyntheticLambda5.INSTANCE);
    }

    public Task<Void> fetch(long minimumFetchIntervalInSeconds) {
        Task<ConfigFetchHandler.FetchResponse> fetchTask = this.fetchHandler.fetch(minimumFetchIntervalInSeconds);
        return fetchTask.onSuccessTask(FirebaseRemoteConfig$$ExternalSyntheticLambda6.INSTANCE);
    }

    public String getString(String key) {
        return this.getHandler.getString(key);
    }

    public boolean getBoolean(String key) {
        return this.getHandler.getBoolean(key);
    }

    public double getDouble(String key) {
        return this.getHandler.getDouble(key);
    }

    public long getLong(String key) {
        return this.getHandler.getLong(key);
    }

    public FirebaseRemoteConfigValue getValue(String key) {
        return this.getHandler.getValue(key);
    }

    public Set<String> getKeysByPrefix(String prefix) {
        return this.getHandler.getKeysByPrefix(prefix);
    }

    public Map<String, FirebaseRemoteConfigValue> getAll() {
        return this.getHandler.getAll();
    }

    public FirebaseRemoteConfigInfo getInfo() {
        return this.frcMetadata.getInfo();
    }

    public Task<Void> setConfigSettingsAsync(final FirebaseRemoteConfigSettings settings) {
        return Tasks.call(this.executor, new Callable() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda9
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return FirebaseRemoteConfig.this.m96xd6203cf5(settings);
            }
        });
    }

    /* renamed from: lambda$setConfigSettingsAsync$5$com-google-firebase-remoteconfig-FirebaseRemoteConfig */
    public /* synthetic */ Void m96xd6203cf5(FirebaseRemoteConfigSettings settings) throws Exception {
        this.frcMetadata.setConfigSettings(settings);
        return null;
    }

    public Task<Void> setDefaultsAsync(Map<String, Object> defaults) {
        Map<String, String> defaultsStringMap = new HashMap<>();
        for (Map.Entry<String, Object> defaultsEntry : defaults.entrySet()) {
            Object value = defaultsEntry.getValue();
            if (value instanceof byte[]) {
                defaultsStringMap.put(defaultsEntry.getKey(), new String((byte[]) value));
            } else {
                defaultsStringMap.put(defaultsEntry.getKey(), value.toString());
            }
        }
        return setDefaultsWithStringsMapAsync(defaultsStringMap);
    }

    public Task<Void> setDefaultsAsync(int resourceId) {
        Map<String, String> xmlDefaults = DefaultsXmlParser.getDefaultsFromXml(this.context, resourceId);
        return setDefaultsWithStringsMapAsync(xmlDefaults);
    }

    public Task<Void> reset() {
        return Tasks.call(this.executor, new Callable() { // from class: com.google.firebase.remoteconfig.FirebaseRemoteConfig$$ExternalSyntheticLambda8
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return FirebaseRemoteConfig.this.m95xf98246b6();
            }
        });
    }

    /* renamed from: lambda$reset$6$com-google-firebase-remoteconfig-FirebaseRemoteConfig */
    public /* synthetic */ Void m95xf98246b6() throws Exception {
        this.activatedConfigsCache.clear();
        this.fetchedConfigsCache.clear();
        this.defaultConfigsCache.clear();
        this.frcMetadata.clear();
        return null;
    }

    public void startLoadingConfigsFromDisk() {
        this.activatedConfigsCache.get();
        this.defaultConfigsCache.get();
        this.fetchedConfigsCache.get();
    }

    public boolean processActivatePutTask(Task<ConfigContainer> putTask) {
        if (putTask.isSuccessful()) {
            this.fetchedConfigsCache.clear();
            if (putTask.getResult() != null) {
                updateAbtWithActivatedExperiments(putTask.getResult().getAbtExperiments());
                return true;
            }
            Log.e(TAG, "Activated configs written to disk are null.");
            return true;
        }
        return false;
    }

    private Task<Void> setDefaultsWithStringsMapAsync(Map<String, String> defaultsStringMap) {
        try {
            ConfigContainer defaultConfigs = ConfigContainer.newBuilder().replaceConfigsWith(defaultsStringMap).build();
            Task<ConfigContainer> putTask = this.defaultConfigsCache.put(defaultConfigs);
            return putTask.onSuccessTask(FirebaseRemoteConfig$$ExternalSyntheticLambda4.INSTANCE);
        } catch (JSONException e) {
            Log.e(TAG, "The provided defaults map could not be processed.", e);
            return Tasks.forResult(null);
        }
    }

    void updateAbtWithActivatedExperiments(JSONArray abtExperiments) {
        if (this.firebaseAbt == null) {
            return;
        }
        try {
            List<Map<String, String>> experimentInfoMaps = toExperimentInfoMaps(abtExperiments);
            this.firebaseAbt.replaceAllExperiments(experimentInfoMaps);
        } catch (AbtException e) {
            Log.w(TAG, "Could not update ABT experiments.", e);
        } catch (JSONException e2) {
            Log.e(TAG, "Could not parse ABT experiments from the JSON response.", e2);
        }
    }

    static List<Map<String, String>> toExperimentInfoMaps(JSONArray abtExperimentsJson) throws JSONException {
        List<Map<String, String>> experimentInfoMaps = new ArrayList<>();
        for (int index = 0; index < abtExperimentsJson.length(); index++) {
            Map<String, String> experimentInfo = new HashMap<>();
            JSONObject abtExperimentJson = abtExperimentsJson.getJSONObject(index);
            Iterator<String> experimentJsonKeyIterator = abtExperimentJson.keys();
            while (experimentJsonKeyIterator.hasNext()) {
                String key = experimentJsonKeyIterator.next();
                experimentInfo.put(key, abtExperimentJson.getString(key));
            }
            experimentInfoMaps.add(experimentInfo);
        }
        return experimentInfoMaps;
    }

    private static boolean isFetchedFresh(ConfigContainer fetched, ConfigContainer activated) {
        return activated == null || !fetched.getFetchTime().equals(activated.getFetchTime());
    }
}
