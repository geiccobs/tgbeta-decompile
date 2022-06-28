package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzfo implements zzfw {
    private zzfw[] zza;

    public zzfo(zzfw... zzfwVarArr) {
        this.zza = zzfwVarArr;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfw
    public final boolean zza(Class<?> cls) {
        for (zzfw zzfwVar : this.zza) {
            if (zzfwVar.zza(cls)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzfw
    public final zzfx zzb(Class<?> cls) {
        zzfw[] zzfwVarArr;
        for (zzfw zzfwVar : this.zza) {
            if (zzfwVar.zza(cls)) {
                return zzfwVar.zzb(cls);
            }
        }
        String valueOf = String.valueOf(cls.getName());
        throw new UnsupportedOperationException(valueOf.length() != 0 ? "No factory is available for message type: ".concat(valueOf) : new String("No factory is available for message type: "));
    }
}
