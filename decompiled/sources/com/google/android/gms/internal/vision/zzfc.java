package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzfc extends zzfb {
    public static int zza(int i, int i2, int i3) {
        if (!(i2 <= 1073741823)) {
            throw new IllegalArgumentException(zzdg.zza("min (%s) must be less than or equal to max (%s)", Integer.valueOf(i2), 1073741823));
        }
        return Math.min(Math.max(i, i2), 1073741823);
    }
}
