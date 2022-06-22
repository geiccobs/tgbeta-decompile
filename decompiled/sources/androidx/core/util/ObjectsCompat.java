package androidx.core.util;

import android.os.Build;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ObjectsCompat {
    public static boolean equals(Object a, Object b) {
        if (Build.VERSION.SDK_INT >= 19) {
            return ObjectsCompat$$ExternalSyntheticBackport0.m(a, b);
        }
        return a == b || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Arrays.hashCode(values);
        }
        return Arrays.hashCode(values);
    }
}
