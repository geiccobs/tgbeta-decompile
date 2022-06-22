package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final class zzgg {
    private static final zzge zza = zzc();
    private static final zzge zzb = new zzgh();

    public static zzge zza() {
        return zza;
    }

    public static zzge zzb() {
        return zzb;
    }

    private static zzge zzc() {
        try {
            return (zzge) Class.forName("com.google.protobuf.NewInstanceSchemaFull").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }
}
