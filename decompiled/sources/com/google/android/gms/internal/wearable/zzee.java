package com.google.android.gms.internal.wearable;

import sun.misc.Unsafe;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzee extends zzef {
    public zzee(Unsafe unsafe) {
        super(unsafe);
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final void zza(Object obj, long j, byte b) {
        if (zzeg.zzb) {
            zzeg.zzD(obj, j, b);
        } else {
            zzeg.zzE(obj, j, b);
        }
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final boolean zzb(Object obj, long j) {
        if (zzeg.zzb) {
            return zzeg.zzv(obj, j);
        }
        return zzeg.zzw(obj, j);
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final void zzc(Object obj, long j, boolean z) {
        if (zzeg.zzb) {
            zzeg.zzD(obj, j, r3 ? (byte) 1 : (byte) 0);
        } else {
            zzeg.zzE(obj, j, r3 ? (byte) 1 : (byte) 0);
        }
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final float zzd(Object obj, long j) {
        return Float.intBitsToFloat(zzk(obj, j));
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final void zze(Object obj, long j, float f) {
        zzl(obj, j, Float.floatToIntBits(f));
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final double zzf(Object obj, long j) {
        return Double.longBitsToDouble(zzm(obj, j));
    }

    @Override // com.google.android.gms.internal.wearable.zzef
    public final void zzg(Object obj, long j, double d) {
        zzn(obj, j, Double.doubleToLongBits(d));
    }
}
