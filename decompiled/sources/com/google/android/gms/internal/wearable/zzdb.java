package com.google.android.gms.internal.wearable;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzdb<T> implements zzdi<T> {
    private final zzcx zza;
    private final zzdw<?, ?> zzb;
    private final boolean zzc;
    private final zzbh<?> zzd;

    private zzdb(zzdw<?, ?> zzdwVar, zzbh<?> zzbhVar, zzcx zzcxVar) {
        this.zzb = zzdwVar;
        this.zzc = zzbhVar.zza(zzcxVar);
        this.zzd = zzbhVar;
        this.zza = zzcxVar;
    }

    public static <T> zzdb<T> zzf(zzdw<?, ?> zzdwVar, zzbh<?> zzbhVar, zzcx zzcxVar) {
        return new zzdb<>(zzdwVar, zzbhVar, zzcxVar);
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final T zza() {
        return (T) this.zza.zzab().zzw();
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final boolean zzb(T t, T t2) {
        if (!this.zzb.zzd(t).equals(this.zzb.zzd(t2))) {
            return false;
        }
        if (this.zzc) {
            this.zzd.zzb(t);
            this.zzd.zzb(t2);
            throw null;
        }
        return true;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final int zzc(T t) {
        int hashCode = this.zzb.zzd(t).hashCode();
        if (this.zzc) {
            this.zzd.zzb(t);
            throw null;
        }
        return hashCode;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzd(T t, T t2) {
        zzdk.zzF(this.zzb, t, t2);
        if (this.zzc) {
            zzdk.zzE(this.zzd, t, t2);
        }
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final int zze(T t) {
        zzdw<?, ?> zzdwVar = this.zzb;
        int zzg = zzdwVar.zzg(zzdwVar.zzd(t));
        if (!this.zzc) {
            return zzg;
        }
        this.zzd.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzh(T t, byte[] bArr, int i, int i2, zzai zzaiVar) throws IOException {
        zzbs zzbsVar = (zzbs) t;
        if (zzbsVar.zzc == zzdx.zza()) {
            zzbsVar.zzc = zzdx.zzb();
        }
        zzbq zzbqVar = (zzbq) t;
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzi(T t) {
        this.zzb.zze(t);
        this.zzd.zzc(t);
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final boolean zzj(T t) {
        this.zzd.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.wearable.zzdi
    public final void zzm(T t, zzbc zzbcVar) throws IOException {
        this.zzd.zzb(t);
        throw null;
    }
}
