package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzav;
import com.google.android.gms.internal.mlkit_common.zzds;
import com.google.firebase.components.Component;
import com.google.firebase.components.Dependency;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzdq implements zzds.zza {
    public static final Component<?> zza = Component.builder(zzds.zza.class).add(Dependency.required(zzdo.class)).factory(zzdp.zza).build();
    private final zzdo zzb;

    public zzdq(zzdo zzdoVar) {
        this.zzb = zzdoVar;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzds.zza
    public final void zza(zzav.zzad zzadVar) {
        this.zzb.zza((zzav.zzad) ((zzfq) zzav.zzad.zza(zzadVar).zza(zzav.zzbh.zza(zzadVar.zza()).zza(true)).zzg()));
    }
}
