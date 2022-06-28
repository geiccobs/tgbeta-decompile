package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzfq;
import java.io.IOException;
import java.util.Map;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzfj extends zzfg<zzfq.zzf> {
    @Override // com.google.android.gms.internal.mlkit_common.zzfg
    public final boolean zza(zzhb zzhbVar) {
        return zzhbVar instanceof zzfq.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzfg
    public final zzfl<zzfq.zzf> zza(Object obj) {
        return ((zzfq.zzc) obj).zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzfg
    public final zzfl<zzfq.zzf> zzb(Object obj) {
        zzfq.zzc zzcVar = (zzfq.zzc) obj;
        if (zzcVar.zzc.zzc()) {
            zzcVar.zzc = (zzfl) zzcVar.zzc.clone();
        }
        return zzcVar.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzfg
    public final void zzc(Object obj) {
        zza(obj).zzb();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzfg
    public final int zza(Map.Entry<?, ?> entry) {
        zzfq.zzf zzfVar = (zzfq.zzf) entry.getKey();
        throw new NoSuchMethodError();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzfg
    public final void zza(zzjd zzjdVar, Map.Entry<?, ?> entry) throws IOException {
        zzfq.zzf zzfVar = (zzfq.zzf) entry.getKey();
        throw new NoSuchMethodError();
    }
}
