package com.google.android.gms.internal.mlkit_language_id;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final class zzgc<T> implements zzgp<T> {
    private final zzfz zza;
    private final zzhh<?, ?> zzb;
    private final boolean zzc;
    private final zzee<?> zzd;

    private zzgc(zzhh<?, ?> zzhhVar, zzee<?> zzeeVar, zzfz zzfzVar) {
        this.zzb = zzhhVar;
        this.zzc = zzeeVar.zza(zzfzVar);
        this.zzd = zzeeVar;
        this.zza = zzfzVar;
    }

    public static <T> zzgc<T> zza(zzhh<?, ?> zzhhVar, zzee<?> zzeeVar, zzfz zzfzVar) {
        return new zzgc<>(zzhhVar, zzeeVar, zzfzVar);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final boolean zza(T t, T t2) {
        if (!this.zzb.zza(t).equals(this.zzb.zza(t2))) {
            return false;
        }
        if (!this.zzc) {
            return true;
        }
        return this.zzd.zza(t).equals(this.zzd.zza(t2));
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final int zza(T t) {
        int hashCode = this.zzb.zza(t).hashCode();
        return this.zzc ? (hashCode * 53) + this.zzd.zza(t).hashCode() : hashCode;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final void zzb(T t, T t2) {
        zzgr.zza(this.zzb, t, t2);
        if (this.zzc) {
            zzgr.zza(this.zzd, t, t2);
        }
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final void zza(T t, zzib zzibVar) throws IOException {
        Iterator<Map.Entry<?, Object>> zzd = this.zzd.zza(t).zzd();
        while (zzd.hasNext()) {
            Map.Entry<?, Object> next = zzd.next();
            zzel zzelVar = (zzel) next.getKey();
            if (zzelVar.zzc() != zzhy.MESSAGE || zzelVar.zzd() || zzelVar.zze()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            if (next instanceof zzfc) {
                zzibVar.zza(zzelVar.zza(), (Object) ((zzfc) next).zza().zzc());
            } else {
                zzibVar.zza(zzelVar.zza(), next.getValue());
            }
        }
        zzhh<?, ?> zzhhVar = this.zzb;
        zzhhVar.zzb((zzhh<?, ?>) zzhhVar.zza(t), zzibVar);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final void zzb(T t) {
        this.zzb.zzb(t);
        this.zzd.zzc(t);
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final boolean zzc(T t) {
        return this.zzd.zza(t).zzf();
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzgp
    public final int zzd(T t) {
        zzhh<?, ?> zzhhVar = this.zzb;
        int zzc = zzhhVar.zzc(zzhhVar.zza(t)) + 0;
        return this.zzc ? zzc + this.zzd.zza(t).zzg() : zzc;
    }
}
