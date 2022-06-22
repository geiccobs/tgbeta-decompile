package com.google.android.gms.internal.mlkit_language_id;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final class zzv extends WeakReference<Throwable> {
    private final int zza;

    public zzv(Throwable th, ReferenceQueue<Throwable> referenceQueue) {
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
        if (obj != null && obj.getClass() == zzv.class) {
            if (this == obj) {
                return true;
            }
            zzv zzvVar = (zzv) obj;
            if (this.zza == zzvVar.zza && get() == zzvVar.get()) {
                return true;
            }
        }
        return false;
    }
}
