package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
final class zzkd implements zzkl {
    private zzkl[] zza;

    public zzkd(zzkl... zzklVarArr) {
        this.zza = zzklVarArr;
    }

    @Override // com.google.android.gms.internal.vision.zzkl
    public final boolean zza(Class<?> cls) {
        for (zzkl zzklVar : this.zza) {
            if (zzklVar.zza(cls)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.gms.internal.vision.zzkl
    public final zzki zzb(Class<?> cls) {
        zzkl[] zzklVarArr;
        for (zzkl zzklVar : this.zza) {
            if (zzklVar.zza(cls)) {
                return zzklVar.zzb(cls);
            }
        }
        String valueOf = String.valueOf(cls.getName());
        throw new UnsupportedOperationException(valueOf.length() != 0 ? "No factory is available for message type: ".concat(valueOf) : new String("No factory is available for message type: "));
    }
}
