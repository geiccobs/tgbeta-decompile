package com.google.android.gms.internal.vision;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
final class zzff extends WeakReference<Throwable> {
    private final int zza;

    public zzff(Throwable th, ReferenceQueue<Throwable> referenceQueue) {
        super(th, referenceQueue);
        if (th == null) {
            throw new NullPointerException("The referent cannot be null");
        }
        this.zza = System.identityHashCode(th);
    }

    public final int hashCode() {
        return this.zza;
    }

    public final boolean equals(Object obj) {
        if (obj != null && obj.getClass() == zzff.class) {
            if (this == obj) {
                return true;
            }
            zzff zzffVar = (zzff) obj;
            if (this.zza == zzffVar.zza && get() == zzffVar.get()) {
                return true;
            }
        }
        return false;
    }
}
