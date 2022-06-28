package com.google.android.gms.internal.icing;

import sun.misc.Unsafe;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
final class zzfl extends zzfm {
    public zzfl(Unsafe unsafe) {
        super(unsafe);
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final void zza(Object obj, long j, byte b) {
        if (zzfn.zzb) {
            zzfn.zzD(obj, j, b);
        } else {
            zzfn.zzE(obj, j, b);
        }
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final boolean zzb(Object obj, long j) {
        if (zzfn.zzb) {
            return zzfn.zzv(obj, j);
        }
        return zzfn.zzw(obj, j);
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final void zzc(Object obj, long j, boolean z) {
        if (zzfn.zzb) {
            zzfn.zzD(obj, j, r3 ? (byte) 1 : (byte) 0);
        } else {
            zzfn.zzE(obj, j, r3 ? (byte) 1 : (byte) 0);
        }
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final float zzd(Object obj, long j) {
        return Float.intBitsToFloat(zzk(obj, j));
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final void zze(Object obj, long j, float f) {
        zzl(obj, j, Float.floatToIntBits(f));
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final double zzf(Object obj, long j) {
        return Double.longBitsToDouble(zzm(obj, j));
    }

    @Override // com.google.android.gms.internal.icing.zzfm
    public final void zzg(Object obj, long j, double d) {
        zzn(obj, j, Double.doubleToLongBits(d));
    }
}
