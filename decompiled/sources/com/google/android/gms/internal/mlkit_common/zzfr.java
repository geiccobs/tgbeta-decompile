package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzfq;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzfr implements zzgy {
    private static final zzfr zza = new zzfr();

    private zzfr() {
    }

    public static zzfr zza() {
        return zza;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgy
    public final boolean zza(Class<?> cls) {
        return zzfq.class.isAssignableFrom(cls);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgy
    public final zzgz zzb(Class<?> cls) {
        if (!zzfq.class.isAssignableFrom(cls)) {
            String valueOf = String.valueOf(cls.getName());
            throw new IllegalArgumentException(valueOf.length() != 0 ? "Unsupported message type: ".concat(valueOf) : new String("Unsupported message type: "));
        }
        try {
            return (zzgz) zzfq.zza((Class<zzfq>) cls.asSubclass(zzfq.class)).zza(zzfq.zze.zzc, (Object) null, (Object) null);
        } catch (Exception e) {
            String valueOf2 = String.valueOf(cls.getName());
            throw new RuntimeException(valueOf2.length() != 0 ? "Unable to get message info for ".concat(valueOf2) : new String("Unable to get message info for "), e);
        }
    }
}
