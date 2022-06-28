package com.google.android.datatransport.runtime.scheduling.jobscheduling;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.datatransport.Encoding;
import com.google.android.datatransport.runtime.EncodedPayload;
import com.google.android.datatransport.runtime.EventInternal;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.backends.BackendRegistry;
import com.google.android.datatransport.runtime.backends.BackendRequest;
import com.google.android.datatransport.runtime.backends.BackendResponse;
import com.google.android.datatransport.runtime.backends.TransportBackend;
import com.google.android.datatransport.runtime.firebase.transport.ClientMetrics;
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped;
import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.datatransport.runtime.scheduling.persistence.ClientHealthMetricsStore;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStore;
import com.google.android.datatransport.runtime.scheduling.persistence.PersistedEvent;
import com.google.android.datatransport.runtime.synchronization.SynchronizationException;
import com.google.android.datatransport.runtime.synchronization.SynchronizationGuard;
import com.google.android.datatransport.runtime.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.inject.Inject;
/* loaded from: classes3.dex */
public class Uploader {
    private static final String CLIENT_HEALTH_METRICS_LOG_SOURCE = "GDT_CLIENT_METRICS";
    private static final String LOG_TAG = "Uploader";
    private final BackendRegistry backendRegistry;
    private final ClientHealthMetricsStore clientHealthMetricsStore;
    private final Clock clock;
    private final Context context;
    private final EventStore eventStore;
    private final Executor executor;
    private final SynchronizationGuard guard;
    private final Clock uptimeClock;
    private final WorkScheduler workScheduler;

    @Inject
    public Uploader(Context context, BackendRegistry backendRegistry, EventStore eventStore, WorkScheduler workScheduler, Executor executor, SynchronizationGuard guard, Clock clock, Clock uptimeClock, ClientHealthMetricsStore clientHealthMetricsStore) {
        this.context = context;
        this.backendRegistry = backendRegistry;
        this.eventStore = eventStore;
        this.workScheduler = workScheduler;
        this.executor = executor;
        this.guard = guard;
        this.clock = clock;
        this.uptimeClock = uptimeClock;
        this.clientHealthMetricsStore = clientHealthMetricsStore;
    }

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void upload(final TransportContext transportContext, final int attemptNumber, final Runnable callback) {
        this.executor.execute(new Runnable() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                Uploader.this.m16x80c37673(transportContext, attemptNumber, callback);
            }
        });
    }

    /* renamed from: lambda$upload$1$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ void m16x80c37673(final TransportContext transportContext, final int attemptNumber, Runnable callback) {
        try {
            try {
                SynchronizationGuard synchronizationGuard = this.guard;
                final EventStore eventStore = this.eventStore;
                eventStore.getClass();
                synchronizationGuard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda8
                    @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                    public final Object execute() {
                        return Integer.valueOf(EventStore.this.cleanUp());
                    }
                });
                if (!isNetworkAvailable()) {
                    this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda2
                        @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                        public final Object execute() {
                            return Uploader.this.m15x3eac4914(transportContext, attemptNumber);
                        }
                    });
                } else {
                    logAndUpdateState(transportContext, attemptNumber);
                }
            } catch (SynchronizationException e) {
                this.workScheduler.schedule(transportContext, attemptNumber + 1);
            }
        } finally {
            callback.run();
        }
    }

    /* renamed from: lambda$upload$0$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Object m15x3eac4914(TransportContext transportContext, int attemptNumber) {
        this.workScheduler.schedule(transportContext, attemptNumber + 1);
        return null;
    }

    void logAndUpdateState(final TransportContext transportContext, int attemptNumber) {
        BackendResponse response;
        TransportBackend backend = this.backendRegistry.get(transportContext.getBackendName());
        long maxNextRequestWaitMillis = 0;
        while (((Boolean) this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda0
            @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
            public final Object execute() {
                return Uploader.this.m9x65f78bd8(transportContext);
            }
        })).booleanValue()) {
            final Iterable<PersistedEvent> persistedEvents = (Iterable) this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda1
                @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                public final Object execute() {
                    return Uploader.this.m10xa80eb937(transportContext);
                }
            });
            if (!persistedEvents.iterator().hasNext()) {
                return;
            }
            if (backend == null) {
                Logging.d(LOG_TAG, "Unknown backend for %s, deleting event batch for it...", transportContext);
                response = BackendResponse.fatalError();
            } else {
                List<EventInternal> eventInternals = new ArrayList<>();
                for (PersistedEvent persistedEvent : persistedEvents) {
                    eventInternals.add(persistedEvent.getEvent());
                }
                if (transportContext.shouldUploadClientHealthMetrics()) {
                    SynchronizationGuard synchronizationGuard = this.guard;
                    final ClientHealthMetricsStore clientHealthMetricsStore = this.clientHealthMetricsStore;
                    clientHealthMetricsStore.getClass();
                    ClientMetrics clientMetrics = (ClientMetrics) synchronizationGuard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda7
                        @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                        public final Object execute() {
                            return ClientHealthMetricsStore.this.loadClientMetrics();
                        }
                    });
                    EventInternal eventInternal = EventInternal.builder().setEventMillis(this.clock.getTime()).setUptimeMillis(this.uptimeClock.getTime()).setTransportName(CLIENT_HEALTH_METRICS_LOG_SOURCE).setEncodedPayload(new EncodedPayload(Encoding.of("proto"), clientMetrics.toByteArray())).build();
                    EventInternal decoratedEvent = backend.decorate(eventInternal);
                    eventInternals.add(decoratedEvent);
                }
                response = backend.send(BackendRequest.builder().setEvents(eventInternals).setExtras(transportContext.getExtras()).build());
            }
            if (response.getStatus() != BackendResponse.Status.TRANSIENT_ERROR) {
                this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda4
                    @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                    public final Object execute() {
                        return Uploader.this.m12x2c3d13f5(persistedEvents);
                    }
                });
                if (response.getStatus() == BackendResponse.Status.OK) {
                    maxNextRequestWaitMillis = Math.max(maxNextRequestWaitMillis, response.getNextRequestWaitMillis());
                } else if (response.getStatus() == BackendResponse.Status.INVALID_PAYLOAD) {
                    final Map<String, Integer> countMap = new HashMap<>();
                    for (PersistedEvent persistedEvent2 : persistedEvents) {
                        String logSource = persistedEvent2.getEvent().getTransportName();
                        if (!countMap.containsKey(logSource)) {
                            countMap.put(logSource, 1);
                        } else {
                            countMap.put(logSource, Integer.valueOf(countMap.get(logSource).intValue() + 1));
                        }
                    }
                    this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda6
                        @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                        public final Object execute() {
                            return Uploader.this.m13x6e544154(countMap);
                        }
                    });
                }
            } else {
                final long finalMaxNextRequestWaitMillis1 = maxNextRequestWaitMillis;
                this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda5
                    @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
                    public final Object execute() {
                        return Uploader.this.m11xea25e696(persistedEvents, transportContext, finalMaxNextRequestWaitMillis1);
                    }
                });
                this.workScheduler.schedule(transportContext, attemptNumber + 1, true);
                return;
            }
        }
        final long finalMaxNextRequestWaitMillis = maxNextRequestWaitMillis;
        this.guard.runCriticalSection(new SynchronizationGuard.CriticalSection() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.Uploader$$ExternalSyntheticLambda3
            @Override // com.google.android.datatransport.runtime.synchronization.SynchronizationGuard.CriticalSection
            public final Object execute() {
                return Uploader.this.m14xb06b6eb3(transportContext, finalMaxNextRequestWaitMillis);
            }
        });
    }

    /* renamed from: lambda$logAndUpdateState$2$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Boolean m9x65f78bd8(TransportContext transportContext) {
        return Boolean.valueOf(this.eventStore.hasPendingEventsFor(transportContext));
    }

    /* renamed from: lambda$logAndUpdateState$3$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Iterable m10xa80eb937(TransportContext transportContext) {
        return this.eventStore.loadBatch(transportContext);
    }

    /* renamed from: lambda$logAndUpdateState$4$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Object m11xea25e696(Iterable persistedEvents, TransportContext transportContext, long finalMaxNextRequestWaitMillis1) {
        this.eventStore.recordFailure(persistedEvents);
        this.eventStore.recordNextCallTime(transportContext, this.clock.getTime() + finalMaxNextRequestWaitMillis1);
        return null;
    }

    /* renamed from: lambda$logAndUpdateState$5$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Object m12x2c3d13f5(Iterable persistedEvents) {
        this.eventStore.recordSuccess(persistedEvents);
        return null;
    }

    /* renamed from: lambda$logAndUpdateState$6$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Object m13x6e544154(Map countMap) {
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            this.clientHealthMetricsStore.recordLogEventDropped(entry.getValue().intValue(), LogEventDropped.Reason.INVALID_PAYLOD, entry.getKey());
        }
        return null;
    }

    /* renamed from: lambda$logAndUpdateState$7$com-google-android-datatransport-runtime-scheduling-jobscheduling-Uploader */
    public /* synthetic */ Object m14xb06b6eb3(TransportContext transportContext, long finalMaxNextRequestWaitMillis) {
        this.eventStore.recordNextCallTime(transportContext, this.clock.getTime() + finalMaxNextRequestWaitMillis);
        return null;
    }
}
