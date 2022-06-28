package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzep implements zzfw {
    private static final zzep zza = new zzep();

    private zzep() {
    }

    public static zzep zza() {
        return zza;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfw
    public final boolean zza(Class<?> cls) {
        return zzeo.class.isAssignableFrom(cls);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfw
    public final zzfx zzb(Class<?> cls) {
        if (!zzeo.class.isAssignableFrom(cls)) {
            String valueOf = String.valueOf(cls.getName());
            throw new IllegalArgumentException(valueOf.length() != 0 ? "Unsupported message type: ".concat(valueOf) : new String("Unsupported message type: "));
        }
        try {
            return (zzfx) zzeo.zza((Class<zzeo>) cls.asSubclass(zzeo.class)).zza(zzeo.zze.zzc, (Object) null, (Object) null);
        } catch (Exception e) {
            String valueOf2 = String.valueOf(cls.getName());
            throw new RuntimeException(valueOf2.length() != 0 ? "Unable to get message info for ".concat(valueOf2) : new String("Unable to get message info for "), e);
        }
    }
}
