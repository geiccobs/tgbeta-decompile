package com.google.android.datatransport.runtime.scheduling;

import com.google.android.datatransport.runtime.backends.BackendRegistry;
import com.google.android.datatransport.runtime.dagger.internal.Factory;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.WorkScheduler;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import java.util.concurrent.Executor;
import javax.inject.Provider;
/* loaded from: classes3.dex */
public final class DefaultScheduler_Factory implements Factory<DefaultScheduler> {
    private final Provider<BackendRegistry> backendRegistryProvider;
    private final Provider<EventStore> eventStoreProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<SynchronizationGuard> guardProvider;
    private final Provider<WorkScheduler> workSchedulerProvider;

    public DefaultScheduler_Factory(Provider<Executor> executorProvider, Provider<BackendRegistry> backendRegistryProvider, Provider<WorkScheduler> workSchedulerProvider, Provider<EventStore> eventStoreProvider, Provider<SynchronizationGuard> guardProvider) {
        this.executorProvider = executorProvider;
        this.backendRegistryProvider = backendRegistryProvider;
        this.workSchedulerProvider = workSchedulerProvider;
        this.eventStoreProvider = eventStoreProvider;
        this.guardProvider = guardProvider;
    }

    @Override // javax.inject.Provider
    public DefaultScheduler get() {
        return newInstance(this.executorProvider.get(), this.backendRegistryProvider.get(), this.workSchedulerProvider.get(), this.eventStoreProvider.get(), this.guardProvider.get());
    }

    public static DefaultScheduler_Factory create(Provider<Executor> executorProvider, Provider<BackendRegistry> backendRegistryProvider, Provider<WorkScheduler> workSchedulerProvider, Provider<EventStore> eventStoreProvider, Provider<SynchronizationGuard> guardProvider) {
        return new DefaultScheduler_Factory(executorProvider, backendRegistryProvider, workSchedulerProvider, eventStoreProvider, guardProvider);
    }

    public static DefaultScheduler newInstance(Executor executor, BackendRegistry backendRegistry, WorkScheduler workScheduler, EventStore eventStore, SynchronizationGuard guard) {
        return new DefaultScheduler(executor, backendRegistry, workScheduler, eventStore, guard);
    }
}
