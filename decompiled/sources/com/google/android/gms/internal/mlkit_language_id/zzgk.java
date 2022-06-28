package com.google.android.gms.internal.mlkit_language_id;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzgk {
    private static final zzgk zza = new zzgk();
    private final ConcurrentMap<Class<?>, zzgp<?>> zzc = new ConcurrentHashMap();
    private final zzgo zzb = new zzfm();

    public static zzgk zza() {
        return zza;
    }

    public final <T> zzgp<T> zza(Class<T> cls) {
        zzeq.zza(cls, "messageType");
        zzgp<T> zzgpVar = (zzgp<T>) this.zzc.get(cls);
        if (zzgpVar == null) {
            zzgp<T> zza2 = this.zzb.zza(cls);
            zzeq.zza(cls, "messageType");
            zzeq.zza(zza2, "schema");
            zzgp<T> zzgpVar2 = (zzgp<T>) this.zzc.putIfAbsent(cls, zza2);
            if (zzgpVar2 != null) {
                return zzgpVar2;
            }
            return zza2;
        }
        return zzgpVar;
    }

    public final <T> zzgp<T> zza(T t) {
        return zza((Class) t.getClass());
    }

    private zzgk() {
    }
}
