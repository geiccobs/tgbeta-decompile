package com.google.firebase.remoteconfig.internal;

import android.util.Log;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes3.dex */
public class ConfigCacheClient {
    static final long DISK_READ_TIMEOUT_IN_SECONDS = 5;
    private Task<ConfigContainer> cachedContainerTask = null;
    private final ExecutorService executorService;
    private final ConfigStorageClient storageClient;
    private static final Map<String, ConfigCacheClient> clientInstances = new HashMap();
    private static final Executor DIRECT_EXECUTOR = ConfigCacheClient$$ExternalSyntheticLambda3.INSTANCE;

    private ConfigCacheClient(ExecutorService executorService, ConfigStorageClient storageClient) {
        this.executorService = executorService;
        this.storageClient = storageClient;
    }

    public ConfigContainer getBlocking() {
        return getBlocking(DISK_READ_TIMEOUT_IN_SECONDS);
    }

    ConfigContainer getBlocking(long diskReadTimeoutInSeconds) {
        synchronized (this) {
            Task<ConfigContainer> task = this.cachedContainerTask;
            if (task != null && task.isSuccessful()) {
                return this.cachedContainerTask.getResult();
            }
            try {
                return (ConfigContainer) await(get(), diskReadTimeoutInSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.d(FirebaseRemoteConfig.TAG, "Reading from storage file failed.", e);
                return null;
            }
        }
    }

    public Task<ConfigContainer> put(ConfigContainer configContainer) {
        return put(configContainer, true);
    }

    /* renamed from: lambda$put$0$com-google-firebase-remoteconfig-internal-ConfigCacheClient */
    public /* synthetic */ Void m97xddaae01c(ConfigContainer configContainer) throws Exception {
        return this.storageClient.write(configContainer);
    }

    public Task<ConfigContainer> put(final ConfigContainer configContainer, final boolean shouldUpdateInMemoryContainer) {
        return Tasks.call(this.executorService, new Callable() { // from class: com.google.firebase.remoteconfig.internal.ConfigCacheClient$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return ConfigCacheClient.this.m97xddaae01c(configContainer);
            }
        }).onSuccessTask(this.executorService, new SuccessContinuation() { // from class: com.google.firebase.remoteconfig.internal.ConfigCacheClient$$ExternalSyntheticLambda0
            @Override // com.google.android.gms.tasks.SuccessContinuation
            public final Task then(Object obj) {
                return ConfigCacheClient.this.m98x9820809d(shouldUpdateInMemoryContainer, configContainer, (Void) obj);
            }
        });
    }

    /* renamed from: lambda$put$1$com-google-firebase-remoteconfig-internal-ConfigCacheClient */
    public /* synthetic */ Task m98x9820809d(boolean shouldUpdateInMemoryContainer, ConfigContainer configContainer, Void unusedVoid) throws Exception {
        if (shouldUpdateInMemoryContainer) {
            updateInMemoryConfigContainer(configContainer);
        }
        return Tasks.forResult(configContainer);
    }

    public synchronized Task<ConfigContainer> get() {
        Task<ConfigContainer> task = this.cachedContainerTask;
        if (task == null || (task.isComplete() && !this.cachedContainerTask.isSuccessful())) {
            ExecutorService executorService = this.executorService;
            final ConfigStorageClient configStorageClient = this.storageClient;
            configStorageClient.getClass();
            this.cachedContainerTask = Tasks.call(executorService, new Callable() { // from class: com.google.firebase.remoteconfig.internal.ConfigCacheClient$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return ConfigStorageClient.this.read();
                }
            });
        }
        return this.cachedContainerTask;
    }

    public void clear() {
        synchronized (this) {
            this.cachedContainerTask = Tasks.forResult(null);
        }
        this.storageClient.clear();
    }

    private synchronized void updateInMemoryConfigContainer(ConfigContainer configContainer) {
        this.cachedContainerTask = Tasks.forResult(configContainer);
    }

    synchronized Task<ConfigContainer> getCachedContainerTask() {
        return this.cachedContainerTask;
    }

    public static synchronized ConfigCacheClient getInstance(ExecutorService executorService, ConfigStorageClient storageClient) {
        ConfigCacheClient configCacheClient;
        synchronized (ConfigCacheClient.class) {
            String fileName = storageClient.getFileName();
            Map<String, ConfigCacheClient> map = clientInstances;
            if (!map.containsKey(fileName)) {
                map.put(fileName, new ConfigCacheClient(executorService, storageClient));
            }
            configCacheClient = map.get(fileName);
        }
        return configCacheClient;
    }

    public static synchronized void clearInstancesForTest() {
        synchronized (ConfigCacheClient.class) {
            clientInstances.clear();
        }
    }

    private static <TResult> TResult await(Task<TResult> task, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        AwaitListener<TResult> waiter = new AwaitListener<>();
        Executor executor = DIRECT_EXECUTOR;
        task.addOnSuccessListener(executor, waiter);
        task.addOnFailureListener(executor, waiter);
        task.addOnCanceledListener(executor, waiter);
        if (!waiter.await(timeout, unit)) {
            throw new TimeoutException("Task await timed out.");
        }
        if (task.isSuccessful()) {
            return task.getResult();
        }
        throw new ExecutionException(task.getException());
    }

    /* loaded from: classes3.dex */
    public static class AwaitListener<TResult> implements OnSuccessListener<TResult>, OnFailureListener, OnCanceledListener {
        private final CountDownLatch latch;

        private AwaitListener() {
            this.latch = new CountDownLatch(1);
        }

        @Override // com.google.android.gms.tasks.OnSuccessListener
        public void onSuccess(TResult o) {
            this.latch.countDown();
        }

        @Override // com.google.android.gms.tasks.OnFailureListener
        public void onFailure(Exception e) {
            this.latch.countDown();
        }

        @Override // com.google.android.gms.tasks.OnCanceledListener
        public void onCanceled() {
            this.latch.countDown();
        }

        public void await() throws InterruptedException {
            this.latch.await();
        }

        public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return this.latch.await(timeout, unit);
        }
    }
}
