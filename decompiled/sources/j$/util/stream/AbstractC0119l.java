package j$.util.stream;

import java.util.Collections;
import java.util.EnumSet;
/* renamed from: j$.util.stream.l */
/* loaded from: classes2.dex */
public abstract class AbstractC0119l {
    static {
        EnumC0095h enumC0095h = EnumC0095h.CONCURRENT;
        EnumC0095h enumC0095h2 = EnumC0095h.UNORDERED;
        EnumC0095h enumC0095h3 = EnumC0095h.IDENTITY_FINISH;
        Collections.unmodifiableSet(EnumSet.of(enumC0095h, enumC0095h2, enumC0095h3));
        Collections.unmodifiableSet(EnumSet.of(enumC0095h, enumC0095h2));
        Collections.unmodifiableSet(EnumSet.of(enumC0095h3));
        Collections.unmodifiableSet(EnumSet.of(enumC0095h2, enumC0095h3));
        Collections.emptySet();
    }

    public static double a(double[] dArr) {
        double d = dArr[0] + dArr[1];
        double d2 = dArr[dArr.length - 1];
        return (!Double.isNaN(d) || !Double.isInfinite(d2)) ? d : d2;
    }

    public static double[] b(double[] dArr, double d) {
        double d2 = d - dArr[1];
        double d3 = dArr[0];
        double d4 = d3 + d2;
        dArr[1] = (d4 - d3) - d2;
        dArr[0] = d4;
        return dArr;
    }
}
