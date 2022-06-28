package com.google.android.datatransport.runtime.time;

import android.os.SystemClock;
/* loaded from: classes3.dex */
public class UptimeClock implements Clock {
    @Override // com.google.android.datatransport.runtime.time.Clock
    public long getTime() {
        return SystemClock.elapsedRealtime();
    }
}
