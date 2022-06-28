package com.google.android.datatransport.runtime.time;

import com.google.android.datatransport.runtime.dagger.internal.Factory;
import com.google.android.datatransport.runtime.dagger.internal.Preconditions;
/* loaded from: classes3.dex */
public final class TimeModule_EventClockFactory implements Factory<Clock> {
    @Override // javax.inject.Provider
    public Clock get() {
        return eventClock();
    }

    public static TimeModule_EventClockFactory create() {
        return InstanceHolder.INSTANCE;
    }

    public static Clock eventClock() {
        return (Clock) Preconditions.checkNotNull(TimeModule.eventClock(), "Cannot return null from a non-@Nullable @Provides method");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class InstanceHolder {
        private static final TimeModule_EventClockFactory INSTANCE = new TimeModule_EventClockFactory();

        private InstanceHolder() {
        }
    }
}
