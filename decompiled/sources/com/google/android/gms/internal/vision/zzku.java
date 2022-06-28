package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzku {
    private static final zzks zza = zzc();
    private static final zzks zzb = new zzkv();

    public static zzks zza() {
        return zza;
    }

    public static zzks zzb() {
        return zzb;
    }

    private static zzks zzc() {
        try {
            return (zzks) Class.forName("com.google.protobuf.NewInstanceSchemaFull").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
