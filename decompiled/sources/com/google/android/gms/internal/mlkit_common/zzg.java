package com.google.android.gms.internal.mlkit_common;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzg {
    private final zzag<zzo> zza;
    private Boolean zzb;
    private boolean zzc;

    private zzg() {
        this.zza = zzad.zze();
        this.zzc = false;
    }

    public final zzg zza() {
        zzy.zza(this.zzb == null, "A SourcePolicy can only set internal() or external() once.");
        this.zzb = true;
        return this;
    }

    public final zzg zzb() {
        zzy.zza(this.zzb == null, "A SourcePolicy can only set internal() or external() once.");
        this.zzb = false;
        return this;
    }

    public final zzh zzc() {
        if (this.zzb == null) {
            throw new NullPointerException(String.valueOf("Must call internal() or external() when building a SourcePolicy."));
        }
        return new zzh(this.zzb.booleanValue(), false, this.zza.zza(), null);
    }

    public /* synthetic */ zzg(zze zzeVar) {
        this();
    }
}
