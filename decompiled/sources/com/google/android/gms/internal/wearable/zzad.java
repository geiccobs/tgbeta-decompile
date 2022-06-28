package com.google.android.gms.internal.wearable;

import java.util.Collections;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzad {
    public static List<Long> zza(long... jArr) {
        int length = jArr.length;
        if (length == 0) {
            return Collections.emptyList();
        }
        return new zzac(jArr, 0, length);
    }

    public static /* synthetic */ int zzb(long[] jArr, long j, int i, int i2) {
        while (i < i2) {
            if (jArr[i] == j) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
