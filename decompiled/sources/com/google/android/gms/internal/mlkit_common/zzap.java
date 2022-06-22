package com.google.android.gms.internal.mlkit_common;

import j$.util.concurrent.ConcurrentHashMap;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.List;
import java.util.Vector;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
final class zzap {
    private final ConcurrentHashMap<zzas, List<Throwable>> zza = new ConcurrentHashMap<>(16, 0.75f, 10);
    private final ReferenceQueue<Throwable> zzb = new ReferenceQueue<>();

    public final List<Throwable> zza(Throwable th, boolean z) {
        Reference<? extends Throwable> poll = this.zzb.poll();
        while (poll != null) {
            this.zza.remove(poll);
            poll = this.zzb.poll();
        }
        List<Throwable> list = this.zza.get(new zzas(th, null));
        if (list != null) {
            return list;
        }
        Vector vector = new Vector(2);
        List<Throwable> putIfAbsent = this.zza.putIfAbsent(new zzas(th, this.zzb), vector);
        return putIfAbsent == null ? vector : putIfAbsent;
    }
}
