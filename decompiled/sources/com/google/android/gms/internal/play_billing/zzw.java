package com.google.android.gms.internal.play_billing;

import java.util.Arrays;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public final class zzw {
    Object[] zza = new Object[8];
    int zzb = 0;
    zzv zzc;

    public final zzw zza(Object obj, Object obj2) {
        int i = this.zzb + 1;
        int i2 = i + i;
        Object[] objArr = this.zza;
        int length = objArr.length;
        if (i2 > length) {
            int i3 = length + (length >> 1) + 1;
            if (i3 < i2) {
                int highestOneBit = Integer.highestOneBit(i2 - 1);
                i3 = highestOneBit + highestOneBit;
            }
            if (i3 < 0) {
                i3 = Integer.MAX_VALUE;
            }
            this.zza = Arrays.copyOf(objArr, i3);
        }
        zzp.zza(obj, obj2);
        Object[] objArr2 = this.zza;
        int i4 = this.zzb;
        int i5 = i4 + i4;
        objArr2[i5] = obj;
        objArr2[i5 + 1] = obj2;
        this.zzb = i4 + 1;
        return this;
    }

    public final zzx zzb() {
        zzv zzvVar = this.zzc;
        if (zzvVar == null) {
            zzaf zzf = zzaf.zzf(this.zzb, this.zza, this);
            zzv zzvVar2 = this.zzc;
            if (zzvVar2 != null) {
                throw zzvVar2.zza();
            }
            return zzf;
        }
        throw zzvVar.zza();
    }
}
