package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzbj {
    private static final zzbh<?> zza = new zzbi();
    private static final zzbh<?> zzb;

    static {
        zzbh<?> zzbhVar;
        try {
            zzbhVar = (zzbh) Class.forName("com.google.protobuf.ExtensionSchemaFull").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            zzbhVar = null;
        }
        zzb = zzbhVar;
    }

    public static zzbh<?> zza() {
        return zza;
    }

    public static zzbh<?> zzb() {
        zzbh<?> zzbhVar = zzb;
        if (zzbhVar != null) {
            return zzbhVar;
        }
        throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
    }
}
