package j$.util.stream;

import j$.util.u;
/* loaded from: classes2.dex */
abstract class B3 {
    public static j$.util.u b(EnumC0083e4 enumC0083e4, j$.util.u uVar, long j, long j2) {
        long d = d(j, j2);
        int i = AbstractC0199z3.a[enumC0083e4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new w4((u.a) uVar, j, d);
            }
            if (i == 3) {
                return new y4((j$.util.v) uVar, j, d);
            }
            if (i == 4) {
                return new u4((j$.util.t) uVar, j, d);
            }
            throw new IllegalStateException("Unknown shape " + enumC0083e4);
        }
        return new C4(uVar, j, d);
    }

    public static long c(long j, long j2, long j3) {
        if (j >= 0) {
            return Math.max(-1L, Math.min(j - j2, j3));
        }
        return -1L;
    }

    public static long d(long j, long j2) {
        long j3 = j2 >= 0 ? j + j2 : Long.MAX_VALUE;
        if (j3 >= 0) {
            return j3;
        }
        return Long.MAX_VALUE;
    }

    private static int e(long j) {
        return (j != -1 ? EnumC0077d4.u : 0) | EnumC0077d4.t;
    }

    public static U f(AbstractC0066c abstractC0066c, long j, long j2) {
        if (j >= 0) {
            return new C0194y3(abstractC0066c, EnumC0083e4.DOUBLE_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static IntStream g(AbstractC0066c abstractC0066c, long j, long j2) {
        if (j >= 0) {
            return new C0164s3(abstractC0066c, EnumC0083e4.INT_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static AbstractC0080e1 h(AbstractC0066c abstractC0066c, long j, long j2) {
        if (j >= 0) {
            return new C0179v3(abstractC0066c, EnumC0083e4.LONG_VALUE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }

    public static Stream i(AbstractC0066c abstractC0066c, long j, long j2) {
        if (j >= 0) {
            return new C0148p3(abstractC0066c, EnumC0083e4.REFERENCE, e(j2), j, j2);
        }
        throw new IllegalArgumentException("Skip must be non-negative: " + j);
    }
}
