package com.google.android.gms.internal.mlkit_common;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
final class zzhe<T> implements zzhr<T> {
    private final zzhb zza;
    private final zzij<?, ?> zzb;
    private final boolean zzc;
    private final zzfg<?> zzd;

    private zzhe(zzij<?, ?> zzijVar, zzfg<?> zzfgVar, zzhb zzhbVar) {
        this.zzb = zzijVar;
        this.zzc = zzfgVar.zza(zzhbVar);
        this.zzd = zzfgVar;
        this.zza = zzhbVar;
    }

    public static <T> zzhe<T> zza(zzij<?, ?> zzijVar, zzfg<?> zzfgVar, zzhb zzhbVar) {
        return new zzhe<>(zzijVar, zzfgVar, zzhbVar);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final boolean zza(T t, T t2) {
        if (!this.zzb.zza(t).equals(this.zzb.zza(t2))) {
            return false;
        }
        if (this.zzc) {
            return this.zzd.zza(t).equals(this.zzd.zza(t2));
        }
        return true;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final int zza(T t) {
        int hashCode = this.zzb.zza(t).hashCode();
        if (this.zzc) {
            return (hashCode * 53) + this.zzd.zza(t).hashCode();
        }
        return hashCode;
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final void zzb(T t, T t2) {
        zzht.zza(this.zzb, t, t2);
        if (this.zzc) {
            zzht.zza(this.zzd, t, t2);
        }
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final void zza(T t, zzjd zzjdVar) throws IOException {
        Iterator<Map.Entry<?, Object>> zzd = this.zzd.zza(t).zzd();
        while (zzd.hasNext()) {
            Map.Entry<?, Object> next = zzd.next();
            zzfn zzfnVar = (zzfn) next.getKey();
            if (zzfnVar.zzc() != zzja.MESSAGE || zzfnVar.zzd() || zzfnVar.zze()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            if (next instanceof zzge) {
                zzjdVar.zza(zzfnVar.zza(), (Object) ((zzge) next).zza().zzc());
            } else {
                zzjdVar.zza(zzfnVar.zza(), next.getValue());
            }
        }
        zzij<?, ?> zzijVar = this.zzb;
        zzijVar.zzb((zzij<?, ?>) zzijVar.zza(t), zzjdVar);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final void zzb(T t) {
        this.zzb.zzb(t);
        this.zzd.zzc(t);
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final boolean zzc(T t) {
        return this.zzd.zza(t).zzf();
    }

    @Override // com.google.android.gms.internal.mlkit_common.zzhr
    public final int zzd(T t) {
        zzij<?, ?> zzijVar = this.zzb;
        int zzc = zzijVar.zzc(zzijVar.zza(t)) + 0;
        if (this.zzc) {
            return zzc + this.zzd.zza(t).zzg();
        }
        return zzc;
    }
}
