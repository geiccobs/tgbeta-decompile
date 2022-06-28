package com.google.android.gms.internal.wearable;

import java.util.Collections;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzaa extends zzab {
    public static List<Float> zza(float... fArr) {
        int length = fArr.length;
        if (length == 0) {
            return Collections.emptyList();
        }
        return new zzz(fArr, 0, length);
    }

    public static /* synthetic */ int zzb(float[] fArr, float f, int i, int i2) {
        while (i < i2) {
            if (fArr[i] == f) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
