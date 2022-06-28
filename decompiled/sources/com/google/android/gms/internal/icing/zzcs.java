package com.google.android.gms.internal.icing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzcs {
    private static final zzcq<?> zza = new zzcr();
    private static final zzcq<?> zzb;

    static {
        zzcq<?> zzcqVar;
        try {
            zzcqVar = (zzcq) Class.forName("com.google.protobuf.ExtensionSchemaFull").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            zzcqVar = null;
        }
        zzb = zzcqVar;
    }

    public static zzcq<?> zza() {
        return zza;
    }

    public static zzcq<?> zzb() {
        zzcq<?> zzcqVar = zzb;
        if (zzcqVar != null) {
            return zzcqVar;
        }
        throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
    }
}
