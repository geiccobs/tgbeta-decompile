package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzcw implements zzec {
    private static final zzcw zza = new zzcw();

    private zzcw() {
    }

    public static zzcw zza() {
        return zza;
    }

    @Override // com.google.android.gms.internal.icing.zzec
    public final boolean zzb(Class<?> cls) {
        return zzda.class.isAssignableFrom(cls);
    }

    @Override // com.google.android.gms.internal.icing.zzec
    public final zzeb zzc(Class<?> cls) {
        if (!zzda.class.isAssignableFrom(cls)) {
            String valueOf = String.valueOf(cls.getName());
            throw new IllegalArgumentException(valueOf.length() != 0 ? "Unsupported message type: ".concat(valueOf) : new String("Unsupported message type: "));
        }
        try {
            return (zzeb) zzda.zzp(cls.asSubclass(zzda.class)).zzf(3, null, null);
        } catch (Exception e) {
            String valueOf2 = String.valueOf(cls.getName());
            throw new RuntimeException(valueOf2.length() != 0 ? "Unable to get message info for ".concat(valueOf2) : new String("Unable to get message info for "), e);
        }
    }
}
