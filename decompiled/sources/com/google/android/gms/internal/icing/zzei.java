package com.google.android.gms.internal.icing;

import java.io.IOException;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzei<T> implements zzep<T> {
    private final zzee zza;
    private final zzfd<?, ?> zzb;
    private final boolean zzc;
    private final zzcq<?> zzd;

    private zzei(zzfd<?, ?> zzfdVar, zzcq<?> zzcqVar, zzee zzeeVar) {
        this.zzb = zzfdVar;
        this.zzc = zzcqVar.zza(zzeeVar);
        this.zzd = zzcqVar;
        this.zza = zzeeVar;
    }

    public static <T> zzei<T> zzg(zzfd<?, ?> zzfdVar, zzcq<?> zzcqVar, zzee zzeeVar) {
        return new zzei<>(zzfdVar, zzcqVar, zzeeVar);
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final boolean zza(T t, T t2) {
        if (!this.zzb.zzb(t).equals(this.zzb.zzb(t2))) {
            return false;
        }
        if (this.zzc) {
            this.zzd.zzb(t);
            this.zzd.zzb(t2);
            throw null;
        }
        return true;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final int zzb(T t) {
        int hashCode = this.zzb.zzb(t).hashCode();
        if (this.zzc) {
            this.zzd.zzb(t);
            throw null;
        }
        return hashCode;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final void zzc(T t, T t2) {
        zzer.zzF(this.zzb, t, t2);
        if (this.zzc) {
            zzer.zzE(this.zzd, t, t2);
        }
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final int zzd(T t) {
        zzfd<?, ?> zzfdVar = this.zzb;
        int zze = zzfdVar.zze(zzfdVar.zzb(t));
        if (!this.zzc) {
            return zze;
        }
        this.zzd.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final void zze(T t) {
        this.zzb.zzc(t);
        this.zzd.zzc(t);
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final boolean zzf(T t) {
        this.zzd.zzb(t);
        throw null;
    }

    @Override // com.google.android.gms.internal.icing.zzep
    public final void zzi(T t, zzcn zzcnVar) throws IOException {
        this.zzd.zzb(t);
        throw null;
    }
}
