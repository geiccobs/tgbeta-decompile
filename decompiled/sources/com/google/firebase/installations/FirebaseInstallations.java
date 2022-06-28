package com.google.firebase.installations;

import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.heartbeatinfo.HeartBeatInfo;
import com.google.firebase.inject.Provider;
import com.google.firebase.installations.FirebaseInstallationsException;
import com.google.firebase.installations.internal.FidListener;
import com.google.firebase.installations.internal.FidListenerHandle;
import com.google.firebase.installations.local.IidStore;
import com.google.firebase.installations.local.PersistedInstallation;
import com.google.firebase.installations.local.PersistedInstallationEntry;
import com.google.firebase.installations.remote.FirebaseInstallationServiceClient;
import com.google.firebase.installations.remote.InstallationResponse;
import com.google.firebase.installations.remote.TokenResult;
import com.google.firebase.platforminfo.UserAgentPublisher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes3.dex */
public class FirebaseInstallations implements FirebaseInstallationsApi {
    private static final String API_KEY_VALIDATION_MSG = "Please set a valid API key. A Firebase API key is required to communicate with Firebase server APIs: It authenticates your project with Google.Please refer to https://firebase.google.com/support/privacy/init-options.";
    private static final String APP_ID_VALIDATION_MSG = "Please set your Application ID. A valid Firebase App ID is required to communicate with Firebase server APIs: It identifies your application with Firebase.Please refer to https://firebase.google.com/support/privacy/init-options.";
    private static final String AUTH_ERROR_MSG = "Installation ID could not be validated with the Firebase servers (maybe it was deleted). Firebase Installations will need to create a new Installation ID and auth token. Please retry your last request.";
    private static final String CHIME_FIREBASE_APP_NAME = "CHIME_ANDROID_SDK";
    private static final int CORE_POOL_SIZE = 0;
    private static final long KEEP_ALIVE_TIME_IN_SECONDS = 30;
    private static final String LOCKFILE_NAME_GENERATE_FID = "generatefid.lock";
    private static final int MAXIMUM_POOL_SIZE = 1;
    private static final String PROJECT_ID_VALIDATION_MSG = "Please set your Project ID. A valid Firebase Project ID is required to communicate with Firebase server APIs: It identifies your application with Firebase.Please refer to https://firebase.google.com/support/privacy/init-options.";
    private final ExecutorService backgroundExecutor;
    private String cachedFid;
    private final RandomFidGenerator fidGenerator;
    private Set<FidListener> fidListeners;
    private final FirebaseApp firebaseApp;
    private final IidStore iidStore;
    private final List<StateListener> listeners;
    private final Object lock;
    private final ExecutorService networkExecutor;
    private final PersistedInstallation persistedInstallation;
    private final FirebaseInstallationServiceClient serviceClient;
    private final Utils utils;
    private static final Object lockGenerateFid = new Object();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() { // from class: com.google.firebase.installations.FirebaseInstallations.1
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            return new Thread(r, String.format("firebase-installations-executor-%d", Integer.valueOf(this.mCount.getAndIncrement())));
        }
    };

    public FirebaseInstallations(FirebaseApp firebaseApp, Provider<UserAgentPublisher> publisher, Provider<HeartBeatInfo> heartbeatInfo) {
        this(new ThreadPoolExecutor(0, 1, (long) KEEP_ALIVE_TIME_IN_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue(), THREAD_FACTORY), firebaseApp, new FirebaseInstallationServiceClient(firebaseApp.getApplicationContext(), publisher, heartbeatInfo), new PersistedInstallation(firebaseApp), Utils.getInstance(), new IidStore(firebaseApp), new RandomFidGenerator());
    }

    FirebaseInstallations(ExecutorService backgroundExecutor, FirebaseApp firebaseApp, FirebaseInstallationServiceClient serviceClient, PersistedInstallation persistedInstallation, Utils utils, IidStore iidStore, RandomFidGenerator fidGenerator) {
        this.lock = new Object();
        this.fidListeners = new HashSet();
        this.listeners = new ArrayList();
        this.firebaseApp = firebaseApp;
        this.serviceClient = serviceClient;
        this.persistedInstallation = persistedInstallation;
        this.utils = utils;
        this.iidStore = iidStore;
        this.fidGenerator = fidGenerator;
        this.backgroundExecutor = backgroundExecutor;
        this.networkExecutor = new ThreadPoolExecutor(0, 1, (long) KEEP_ALIVE_TIME_IN_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue(), THREAD_FACTORY);
    }

    private void preConditionChecks() {
        Preconditions.checkNotEmpty(getApplicationId(), APP_ID_VALIDATION_MSG);
        Preconditions.checkNotEmpty(getProjectIdentifier(), PROJECT_ID_VALIDATION_MSG);
        Preconditions.checkNotEmpty(getApiKey(), API_KEY_VALIDATION_MSG);
        Preconditions.checkArgument(Utils.isValidAppIdFormat(getApplicationId()), APP_ID_VALIDATION_MSG);
        Preconditions.checkArgument(Utils.isValidApiKeyFormat(getApiKey()), API_KEY_VALIDATION_MSG);
    }

    String getProjectIdentifier() {
        return this.firebaseApp.getOptions().getProjectId();
    }

    public static FirebaseInstallations getInstance() {
        FirebaseApp defaultFirebaseApp = FirebaseApp.getInstance();
        return getInstance(defaultFirebaseApp);
    }

    public static FirebaseInstallations getInstance(FirebaseApp app) {
        Preconditions.checkArgument(app != null, "Null is not a valid value of FirebaseApp.");
        return (FirebaseInstallations) app.get(FirebaseInstallationsApi.class);
    }

    String getApplicationId() {
        return this.firebaseApp.getOptions().getApplicationId();
    }

    String getApiKey() {
        return this.firebaseApp.getOptions().getApiKey();
    }

    String getName() {
        return this.firebaseApp.getName();
    }

    @Override // com.google.firebase.installations.FirebaseInstallationsApi
    public Task<String> getId() {
        preConditionChecks();
        String fid = getCacheFid();
        if (fid != null) {
            return Tasks.forResult(fid);
        }
        Task<String> task = addGetIdListener();
        this.backgroundExecutor.execute(new Runnable() { // from class: com.google.firebase.installations.FirebaseInstallations$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FirebaseInstallations.this.m91x9bfaa81c();
            }
        });
        return task;
    }

    /* renamed from: lambda$getId$0$com-google-firebase-installations-FirebaseInstallations */
    public /* synthetic */ void m91x9bfaa81c() {
        m92x4bb3eea9(false);
    }

    @Override // com.google.firebase.installations.FirebaseInstallationsApi
    public Task<InstallationTokenResult> getToken(final boolean forceRefresh) {
        preConditionChecks();
        Task<InstallationTokenResult> task = addGetAuthTokenListener();
        this.backgroundExecutor.execute(new Runnable() { // from class: com.google.firebase.installations.FirebaseInstallations$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FirebaseInstallations.this.m92x4bb3eea9(forceRefresh);
            }
        });
        return task;
    }

    @Override // com.google.firebase.installations.FirebaseInstallationsApi
    public Task<Void> delete() {
        return Tasks.call(this.backgroundExecutor, new Callable() { // from class: com.google.firebase.installations.FirebaseInstallations$$ExternalSyntheticLambda3
            @Override // java.util.concurrent.Callable
            public final Object call() {
                Void deleteFirebaseInstallationId;
                deleteFirebaseInstallationId = FirebaseInstallations.this.deleteFirebaseInstallationId();
                return deleteFirebaseInstallationId;
            }
        });
    }

    @Override // com.google.firebase.installations.FirebaseInstallationsApi
    public synchronized FidListenerHandle registerFidListener(final FidListener listener) {
        this.fidListeners.add(listener);
        return new FidListenerHandle() { // from class: com.google.firebase.installations.FirebaseInstallations.2
            @Override // com.google.firebase.installations.internal.FidListenerHandle
            public void unregister() {
                synchronized (FirebaseInstallations.this) {
                    FirebaseInstallations.this.fidListeners.remove(listener);
                }
            }
        };
    }

    private Task<String> addGetIdListener() {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        StateListener l = new GetIdListener(taskCompletionSource);
        addStateListeners(l);
        return taskCompletionSource.getTask();
    }

    private Task<InstallationTokenResult> addGetAuthTokenListener() {
        TaskCompletionSource<InstallationTokenResult> taskCompletionSource = new TaskCompletionSource<>();
        StateListener l = new GetAuthTokenListener(this.utils, taskCompletionSource);
        addStateListeners(l);
        return taskCompletionSource.getTask();
    }

    private void addStateListeners(StateListener l) {
        synchronized (this.lock) {
            this.listeners.add(l);
        }
    }

    private void triggerOnStateReached(PersistedInstallationEntry persistedInstallationEntry) {
        synchronized (this.lock) {
            Iterator<StateListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                StateListener l = it.next();
                boolean doneListening = l.onStateReached(persistedInstallationEntry);
                if (doneListening) {
                    it.remove();
                }
            }
        }
    }

    private void triggerOnException(Exception exception) {
        synchronized (this.lock) {
            Iterator<StateListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                StateListener l = it.next();
                boolean doneListening = l.onException(exception);
                if (doneListening) {
                    it.remove();
                }
            }
        }
    }

    private synchronized void updateCacheFid(String cachedFid) {
        this.cachedFid = cachedFid;
    }

    private synchronized String getCacheFid() {
        return this.cachedFid;
    }

    /* renamed from: doRegistrationOrRefresh */
    public final void m92x4bb3eea9(final boolean forceRefresh) {
        PersistedInstallationEntry prefs = getPrefsWithGeneratedIdMultiProcessSafe();
        if (forceRefresh) {
            prefs = prefs.withClearedAuthToken();
        }
        triggerOnStateReached(prefs);
        this.networkExecutor.execute(new Runnable() { // from class: com.google.firebase.installations.FirebaseInstallations$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FirebaseInstallations.this.m90x349c6181(forceRefresh);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x004c  */
    /* renamed from: doNetworkCallIfNecessary */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m90x349c6181(boolean r5) {
        /*
            r4 = this;
            com.google.firebase.installations.local.PersistedInstallationEntry r0 = r4.getMultiProcessSafePrefs()
            boolean r1 = r0.isErrored()     // Catch: com.google.firebase.installations.FirebaseInstallationsException -> L61
            if (r1 != 0) goto L22
            boolean r1 = r0.isUnregistered()     // Catch: com.google.firebase.installations.FirebaseInstallationsException -> L61
            if (r1 == 0) goto L11
            goto L22
        L11:
            if (r5 != 0) goto L1d
            com.google.firebase.installations.Utils r1 = r4.utils     // Catch: com.google.firebase.installations.FirebaseInstallationsException -> L61
            boolean r1 = r1.isAuthTokenExpired(r0)     // Catch: com.google.firebase.installations.FirebaseInstallationsException -> L61
            if (r1 == 0) goto L1c
            goto L1d
        L1c:
            return
        L1d:
            com.google.firebase.installations.local.PersistedInstallationEntry r1 = r4.fetchAuthTokenFromServer(r0)     // Catch: com.google.firebase.installations.FirebaseInstallationsException -> L61
            goto L26
        L22:
            com.google.firebase.installations.local.PersistedInstallationEntry r1 = r4.registerFidWithServer(r0)     // Catch: com.google.firebase.installations.FirebaseInstallationsException -> L61
        L26:
            r4.insertOrUpdatePrefs(r1)
            r4.updateFidListener(r0, r1)
            r0 = r1
            boolean r2 = r0.isRegistered()
            if (r2 == 0) goto L3b
            java.lang.String r2 = r0.getFirebaseInstallationId()
            r4.updateCacheFid(r2)
        L3b:
            boolean r2 = r0.isErrored()
            if (r2 == 0) goto L4c
            com.google.firebase.installations.FirebaseInstallationsException r2 = new com.google.firebase.installations.FirebaseInstallationsException
            com.google.firebase.installations.FirebaseInstallationsException$Status r3 = com.google.firebase.installations.FirebaseInstallationsException.Status.BAD_CONFIG
            r2.<init>(r3)
            r4.triggerOnException(r2)
            goto L60
        L4c:
            boolean r2 = r0.isNotGenerated()
            if (r2 == 0) goto L5d
            java.io.IOException r2 = new java.io.IOException
            java.lang.String r3 = "Installation ID could not be validated with the Firebase servers (maybe it was deleted). Firebase Installations will need to create a new Installation ID and auth token. Please retry your last request."
            r2.<init>(r3)
            r4.triggerOnException(r2)
            goto L60
        L5d:
            r4.triggerOnStateReached(r0)
        L60:
            return
        L61:
            r1 = move-exception
            r4.triggerOnException(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.installations.FirebaseInstallations.m90x349c6181(boolean):void");
    }

    private synchronized void updateFidListener(PersistedInstallationEntry prefs, PersistedInstallationEntry updatedPrefs) {
        if (this.fidListeners.size() != 0 && !prefs.getFirebaseInstallationId().equals(updatedPrefs.getFirebaseInstallationId())) {
            for (FidListener listener : this.fidListeners) {
                listener.onFidChanged(updatedPrefs.getFirebaseInstallationId());
            }
        }
    }

    private void insertOrUpdatePrefs(PersistedInstallationEntry prefs) {
        synchronized (lockGenerateFid) {
            CrossProcessLock lock = CrossProcessLock.acquire(this.firebaseApp.getApplicationContext(), LOCKFILE_NAME_GENERATE_FID);
            this.persistedInstallation.insertOrUpdatePersistedInstallationEntry(prefs);
            if (lock != null) {
                lock.releaseAndClose();
            }
        }
    }

    private PersistedInstallationEntry getPrefsWithGeneratedIdMultiProcessSafe() {
        PersistedInstallationEntry prefs;
        synchronized (lockGenerateFid) {
            CrossProcessLock lock = CrossProcessLock.acquire(this.firebaseApp.getApplicationContext(), LOCKFILE_NAME_GENERATE_FID);
            prefs = this.persistedInstallation.readPersistedInstallationEntryValue();
            if (prefs.isNotGenerated()) {
                String fid = readExistingIidOrCreateFid(prefs);
                prefs = this.persistedInstallation.insertOrUpdatePersistedInstallationEntry(prefs.withUnregisteredFid(fid));
            }
            if (lock != null) {
                lock.releaseAndClose();
            }
        }
        return prefs;
    }

    private String readExistingIidOrCreateFid(PersistedInstallationEntry prefs) {
        if ((!this.firebaseApp.getName().equals(CHIME_FIREBASE_APP_NAME) && !this.firebaseApp.isDefaultApp()) || !prefs.shouldAttemptMigration()) {
            return this.fidGenerator.createRandomFid();
        }
        String fid = this.iidStore.readIid();
        if (TextUtils.isEmpty(fid)) {
            return this.fidGenerator.createRandomFid();
        }
        return fid;
    }

    private PersistedInstallationEntry registerFidWithServer(PersistedInstallationEntry prefs) throws FirebaseInstallationsException {
        String iidToken = null;
        if (prefs.getFirebaseInstallationId() != null && prefs.getFirebaseInstallationId().length() == 11) {
            iidToken = this.iidStore.readToken();
        }
        InstallationResponse response = this.serviceClient.createFirebaseInstallation(getApiKey(), prefs.getFirebaseInstallationId(), getProjectIdentifier(), getApplicationId(), iidToken);
        switch (AnonymousClass3.$SwitchMap$com$google$firebase$installations$remote$InstallationResponse$ResponseCode[response.getResponseCode().ordinal()]) {
            case 1:
                return prefs.withRegisteredFid(response.getFid(), response.getRefreshToken(), this.utils.currentTimeInSecs(), response.getAuthToken().getToken(), response.getAuthToken().getTokenExpirationTimestamp());
            case 2:
                return prefs.withFisError("BAD CONFIG");
            default:
                throw new FirebaseInstallationsException("Firebase Installations Service is unavailable. Please try again later.", FirebaseInstallationsException.Status.UNAVAILABLE);
        }
    }

    private PersistedInstallationEntry fetchAuthTokenFromServer(PersistedInstallationEntry prefs) throws FirebaseInstallationsException {
        TokenResult tokenResult = this.serviceClient.generateAuthToken(getApiKey(), prefs.getFirebaseInstallationId(), getProjectIdentifier(), prefs.getRefreshToken());
        switch (AnonymousClass3.$SwitchMap$com$google$firebase$installations$remote$TokenResult$ResponseCode[tokenResult.getResponseCode().ordinal()]) {
            case 1:
                return prefs.withAuthToken(tokenResult.getToken(), tokenResult.getTokenExpirationTimestamp(), this.utils.currentTimeInSecs());
            case 2:
                return prefs.withFisError("BAD CONFIG");
            case 3:
                updateCacheFid(null);
                return prefs.withNoGeneratedFid();
            default:
                throw new FirebaseInstallationsException("Firebase Installations Service is unavailable. Please try again later.", FirebaseInstallationsException.Status.UNAVAILABLE);
        }
    }

    /* renamed from: com.google.firebase.installations.FirebaseInstallations$3 */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$google$firebase$installations$remote$InstallationResponse$ResponseCode;
        static final /* synthetic */ int[] $SwitchMap$com$google$firebase$installations$remote$TokenResult$ResponseCode;

        static {
            int[] iArr = new int[TokenResult.ResponseCode.values().length];
            $SwitchMap$com$google$firebase$installations$remote$TokenResult$ResponseCode = iArr;
            try {
                iArr[TokenResult.ResponseCode.OK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$firebase$installations$remote$TokenResult$ResponseCode[TokenResult.ResponseCode.BAD_CONFIG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$firebase$installations$remote$TokenResult$ResponseCode[TokenResult.ResponseCode.AUTH_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            int[] iArr2 = new int[InstallationResponse.ResponseCode.values().length];
            $SwitchMap$com$google$firebase$installations$remote$InstallationResponse$ResponseCode = iArr2;
            try {
                iArr2[InstallationResponse.ResponseCode.OK.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$firebase$installations$remote$InstallationResponse$ResponseCode[InstallationResponse.ResponseCode.BAD_CONFIG.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public Void deleteFirebaseInstallationId() throws FirebaseInstallationsException {
        updateCacheFid(null);
        PersistedInstallationEntry entry = getMultiProcessSafePrefs();
        if (entry.isRegistered()) {
            this.serviceClient.deleteFirebaseInstallation(getApiKey(), entry.getFirebaseInstallationId(), getProjectIdentifier(), entry.getRefreshToken());
        }
        insertOrUpdatePrefs(entry.withNoGeneratedFid());
        return null;
    }

    private PersistedInstallationEntry getMultiProcessSafePrefs() {
        PersistedInstallationEntry prefs;
        synchronized (lockGenerateFid) {
            CrossProcessLock lock = CrossProcessLock.acquire(this.firebaseApp.getApplicationContext(), LOCKFILE_NAME_GENERATE_FID);
            prefs = this.persistedInstallation.readPersistedInstallationEntryValue();
            if (lock != null) {
                lock.releaseAndClose();
            }
        }
        return prefs;
    }
}
