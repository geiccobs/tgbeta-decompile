package com.google.android.datatransport.runtime.time;

import com.google.android.datatransport.runtime.dagger.Module;
import com.google.android.datatransport.runtime.dagger.Provides;
@Module
/* loaded from: classes3.dex */
public abstract class TimeModule {
    @Provides
    public static Clock eventClock() {
        return new WallTimeClock();
    }

    @Provides
    public static Clock uptimeClock() {
        return new UptimeClock();
    }
}
