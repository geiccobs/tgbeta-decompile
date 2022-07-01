package com.google.android.datatransport.runtime.time;
/* loaded from: classes.dex */
public abstract class TimeModule {
    public static Clock eventClock() {
        return new WallTimeClock();
    }

    public static Clock uptimeClock() {
        return new UptimeClock();
    }
}
