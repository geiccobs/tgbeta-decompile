package com.google.android.gms.vision;

import android.util.SparseIntArray;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzc {
    private static final Object zza = new Object();
    private static int zzb = 0;
    private final SparseIntArray zzc = new SparseIntArray();
    private final SparseIntArray zzd = new SparseIntArray();

    public final int zza(int i) {
        synchronized (zza) {
            int i2 = this.zzc.get(i, -1);
            if (i2 != -1) {
                return i2;
            }
            int i3 = zzb;
            zzb = i3 + 1;
            this.zzc.append(i, i3);
            this.zzd.append(i3, i);
            return i3;
        }
    }

    public final int zzb(int i) {
        int i2;
        synchronized (zza) {
            i2 = this.zzd.get(i);
        }
        return i2;
    }
}
