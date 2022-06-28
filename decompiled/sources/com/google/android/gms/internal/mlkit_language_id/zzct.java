package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzcv;
import com.google.android.gms.internal.mlkit_language_id.zzy;
import com.google.firebase.components.Component;
import com.google.firebase.components.Dependency;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
public final class zzct implements zzcv.zzb {
    public static final Component<?> zza = Component.builder(zzcv.zzb.class).add(Dependency.required(zzcr.class)).factory(zzcs.zza).build();
    private final zzcr zzb;

    public zzct(zzcr zzcrVar) {
        this.zzb = zzcrVar;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzcv.zzb
    public final void zza(zzy.zzad zzadVar) {
        this.zzb.zza((zzy.zzad) ((zzeo) zzy.zzad.zza(zzadVar).zza(zzy.zzbh.zza(zzadVar.zza()).zza(true)).zzg()));
    }
}
