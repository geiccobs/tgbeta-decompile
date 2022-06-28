package com.google.android.gms.internal.wearable;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzdf {
    private static final zzdf zza = new zzdf();
    private final ConcurrentMap<Class<?>, zzdi<?>> zzc = new ConcurrentHashMap();
    private final zzdj zzb = new zzcp();

    private zzdf() {
    }

    public static zzdf zza() {
        return zza;
    }

    public final <T> zzdi<T> zzb(Class<T> cls) {
        zzca.zzb(cls, "messageType");
        zzdi<T> zzdiVar = (zzdi<T>) this.zzc.get(cls);
        if (zzdiVar == null) {
            zzdiVar = this.zzb.zza(cls);
            zzca.zzb(cls, "messageType");
            zzca.zzb(zzdiVar, "schema");
            zzdi putIfAbsent = this.zzc.putIfAbsent(cls, zzdiVar);
            if (putIfAbsent != null) {
                return putIfAbsent;
            }
        }
        return zzdiVar;
    }
}
