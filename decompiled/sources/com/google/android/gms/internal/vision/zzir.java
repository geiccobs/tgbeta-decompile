package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzir {
    private static final zziq<?> zza = new zzip();
    private static final zziq<?> zzb = zzc();

    private static zziq<?> zzc() {
        try {
            return (zziq) Class.forName("com.google.protobuf.ExtensionSchemaFull").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static zziq<?> zza() {
        return zza;
    }

    public static zziq<?> zzb() {
        zziq<?> zziqVar = zzb;
        if (zziqVar == null) {
            throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
        }
        return zziqVar;
    }
}
