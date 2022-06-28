package com.google.android.gms.internal.vision;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzky {
    private static final zzky zza = new zzky();
    private final ConcurrentMap<Class<?>, zzlc<?>> zzc = new ConcurrentHashMap();
    private final zzlf zzb = new zzkb();

    public static zzky zza() {
        return zza;
    }

    public final <T> zzlc<T> zza(Class<T> cls) {
        zzjf.zza(cls, "messageType");
        zzlc<T> zzlcVar = (zzlc<T>) this.zzc.get(cls);
        if (zzlcVar == null) {
            zzlc<T> zza2 = this.zzb.zza(cls);
            zzjf.zza(cls, "messageType");
            zzjf.zza(zza2, "schema");
            zzlc<T> zzlcVar2 = (zzlc<T>) this.zzc.putIfAbsent(cls, zza2);
            if (zzlcVar2 != null) {
                return zzlcVar2;
            }
            return zza2;
        }
        return zzlcVar;
    }

    public final <T> zzlc<T> zza(T t) {
        return zza((Class) t.getClass());
    }

    private zzky() {
    }
}
