package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzgq implements zzgy {
    private zzgy[] zza;

    public zzgq(zzgy... zzgyVarArr) {
        this.zza = zzgyVarArr;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgy
    public final boolean zza(Class<?> cls) {
        for (zzgy zzgyVar : this.zza) {
            if (zzgyVar.zza(cls)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzgy
    public final zzgz zzb(Class<?> cls) {
        zzgy[] zzgyVarArr;
        for (zzgy zzgyVar : this.zza) {
            if (zzgyVar.zza(cls)) {
                return zzgyVar.zzb(cls);
            }
        }
        String valueOf = String.valueOf(cls.getName());
        throw new UnsupportedOperationException(valueOf.length() != 0 ? "No factory is available for message type: ".concat(valueOf) : new String("No factory is available for message type: "));
    }
}
