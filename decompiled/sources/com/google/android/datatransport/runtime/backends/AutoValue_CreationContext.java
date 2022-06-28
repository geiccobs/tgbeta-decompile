package com.google.android.datatransport.runtime.backends;

import android.content.Context;
import com.google.android.datatransport.runtime.time.Clock;
/* loaded from: classes3.dex */
public final class AutoValue_CreationContext extends CreationContext {
    private final Context applicationContext;
    private final String backendName;
    private final Clock monotonicClock;
    private final Clock wallClock;

    public AutoValue_CreationContext(Context applicationContext, Clock wallClock, Clock monotonicClock, String backendName) {
        if (applicationContext == null) {
            throw new NullPointerException("Null applicationContext");
        }
        this.applicationContext = applicationContext;
        if (wallClock == null) {
            throw new NullPointerException("Null wallClock");
        }
        this.wallClock = wallClock;
        if (monotonicClock == null) {
            throw new NullPointerException("Null monotonicClock");
        }
        this.monotonicClock = monotonicClock;
        if (backendName == null) {
            throw new NullPointerException("Null backendName");
        }
        this.backendName = backendName;
    }

    @Override // com.google.android.datatransport.runtime.backends.CreationContext
    public Context getApplicationContext() {
        return this.applicationContext;
    }

    @Override // com.google.android.datatransport.runtime.backends.CreationContext
    public Clock getWallClock() {
        return this.wallClock;
    }

    @Override // com.google.android.datatransport.runtime.backends.CreationContext
    public Clock getMonotonicClock() {
        return this.monotonicClock;
    }

    @Override // com.google.android.datatransport.runtime.backends.CreationContext
    public String getBackendName() {
        return this.backendName;
    }

    public String toString() {
        return "CreationContext{applicationContext=" + this.applicationContext + ", wallClock=" + this.wallClock + ", monotonicClock=" + this.monotonicClock + ", backendName=" + this.backendName + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreationContext)) {
            return false;
        }
        CreationContext that = (CreationContext) o;
        return this.applicationContext.equals(that.getApplicationContext()) && this.wallClock.equals(that.getWallClock()) && this.monotonicClock.equals(that.getMonotonicClock()) && this.backendName.equals(that.getBackendName());
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        return ((((((h$ ^ this.applicationContext.hashCode()) * 1000003) ^ this.wallClock.hashCode()) * 1000003) ^ this.monotonicClock.hashCode()) * 1000003) ^ this.backendName.hashCode();
    }
}
