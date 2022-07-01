package com.google.android.gms.common.util;

import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.Objects;
import java.util.ArrayList;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public final class ArrayUtils {
    public static <T> boolean contains(@RecentlyNonNull T[] tArr, @RecentlyNonNull T t) {
        int length = tArr != null ? tArr.length : 0;
        int i = 0;
        while (true) {
            if (i >= length) {
                i = -1;
                break;
            } else if (Objects.equal(tArr[i], t)) {
                break;
            } else {
                i++;
            }
        }
        return i >= 0;
    }

    public static boolean contains(@RecentlyNonNull int[] iArr, int i) {
        if (iArr == null) {
            return false;
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    @RecentlyNonNull
    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<>();
    }
}
