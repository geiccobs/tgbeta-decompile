package com.google.android.datatransport.runtime.synchronization;
/* loaded from: classes3.dex */
public interface SynchronizationGuard {

    /* loaded from: classes3.dex */
    public interface CriticalSection<T> {
        T execute();
    }

    <T> T runCriticalSection(CriticalSection<T> criticalSection);
}
