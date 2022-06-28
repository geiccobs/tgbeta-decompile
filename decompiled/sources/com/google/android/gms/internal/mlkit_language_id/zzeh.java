package com.google.android.gms.internal.mlkit_language_id;

import com.google.android.gms.internal.mlkit_language_id.zzeo;
import java.io.IOException;
import java.util.Map;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes3.dex */
final class zzeh extends zzee<zzeo.zzf> {
    @Override // com.google.android.gms.internal.mlkit_language_id.zzee
    public final boolean zza(zzfz zzfzVar) {
        return zzfzVar instanceof zzeo.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzee
    public final zzej<zzeo.zzf> zza(Object obj) {
        return ((zzeo.zzc) obj).zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzee
    public final zzej<zzeo.zzf> zzb(Object obj) {
        zzeo.zzc zzcVar = (zzeo.zzc) obj;
        if (zzcVar.zzc.zzc()) {
            zzcVar.zzc = (zzej) zzcVar.zzc.clone();
        }
        return zzcVar.zzc;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzee
    public final void zzc(Object obj) {
        zza(obj).zzb();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzee
    public final int zza(Map.Entry<?, ?> entry) {
        zzeo.zzf zzfVar = (zzeo.zzf) entry.getKey();
        throw new NoSuchMethodError();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzee
    public final void zza(zzib zzibVar, Map.Entry<?, ?> entry) throws IOException {
        zzeo.zzf zzfVar = (zzeo.zzf) entry.getKey();
        throw new NoSuchMethodError();
    }
}
