package com.google.android.gms.internal.wearable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzco implements zzcv {
    private final zzcv[] zza;

    public zzco(zzcv... zzcvVarArr) {
        this.zza = zzcvVarArr;
    }

    @Override // com.google.android.gms.internal.wearable.zzcv
    public final boolean zzb(Class<?> cls) {
        zzcv[] zzcvVarArr = this.zza;
        for (int i = 0; i < 2; i++) {
            if (zzcvVarArr[i].zzb(cls)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.gms.internal.wearable.zzcv
    public final zzcu zzc(Class<?> cls) {
        zzcv[] zzcvVarArr = this.zza;
        for (int i = 0; i < 2; i++) {
            zzcv zzcvVar = zzcvVarArr[i];
            if (zzcvVar.zzb(cls)) {
                return zzcvVar.zzc(cls);
            }
        }
        String valueOf = String.valueOf(cls.getName());
        throw new UnsupportedOperationException(valueOf.length() != 0 ? "No factory is available for message type: ".concat(valueOf) : new String("No factory is available for message type: "));
    }
}
