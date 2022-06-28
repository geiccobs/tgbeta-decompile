package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzbo implements zzcv {
    private static final zzbo zza = new zzbo();

    private zzbo() {
    }

    public static zzbo zza() {
        return zza;
    }

    @Override // com.google.android.gms.internal.wearable.zzcv
    public final boolean zzb(Class<?> cls) {
        return zzbs.class.isAssignableFrom(cls);
    }

    @Override // com.google.android.gms.internal.wearable.zzcv
    public final zzcu zzc(Class<?> cls) {
        if (!zzbs.class.isAssignableFrom(cls)) {
            String valueOf = String.valueOf(cls.getName());
            throw new IllegalArgumentException(valueOf.length() != 0 ? "Unsupported message type: ".concat(valueOf) : new String("Unsupported message type: "));
        }
        try {
            return (zzcu) zzbs.zzQ(cls.asSubclass(zzbs.class)).zzG(3, null, null);
        } catch (Exception e) {
            String valueOf2 = String.valueOf(cls.getName());
            throw new RuntimeException(valueOf2.length() != 0 ? "Unable to get message info for ".concat(valueOf2) : new String("Unable to get message info for "), e);
        }
    }
}
