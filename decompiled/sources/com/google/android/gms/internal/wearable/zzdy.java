package com.google.android.gms.internal.wearable;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzdy extends zzdw<zzdx, zzdx> {
    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ void zza(zzdx zzdxVar, int i, long j) {
        zzdxVar.zzh(i << 3, Long.valueOf(j));
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ zzdx zzb() {
        return zzdx.zzb();
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ void zzc(Object obj, zzdx zzdxVar) {
        ((zzbs) obj).zzc = zzdxVar;
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ zzdx zzd(Object obj) {
        return ((zzbs) obj).zzc;
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final void zze(Object obj) {
        ((zzbs) obj).zzc.zzd();
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ zzdx zzf(zzdx zzdxVar, zzdx zzdxVar2) {
        zzdx zzdxVar3 = zzdxVar2;
        return zzdxVar3.equals(zzdx.zza()) ? zzdxVar : zzdx.zzc(zzdxVar, zzdxVar3);
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ int zzg(zzdx zzdxVar) {
        return zzdxVar.zze();
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ int zzh(zzdx zzdxVar) {
        return zzdxVar.zzf();
    }

    @Override // com.google.android.gms.internal.wearable.zzdw
    public final /* bridge */ /* synthetic */ void zzi(zzdx zzdxVar, zzbc zzbcVar) throws IOException {
        zzdxVar.zzi(zzbcVar);
    }
}
