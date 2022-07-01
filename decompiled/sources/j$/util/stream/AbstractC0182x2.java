package j$.util.stream;
/* renamed from: j$.util.stream.x2 */
/* loaded from: classes2.dex */
public abstract class AbstractC0182x2 {
    private static final A1 a = new Z1(null);
    private static final AbstractC0176w1 b = new X1();
    private static final AbstractC0186y1 c = new Y1();
    private static final AbstractC0166u1 d = new W1();
    private static final int[] e = new int[0];
    private static final long[] f = new long[0];
    private static final double[] g = new double[0];

    public static AbstractC0156s1 d(long j, j$.util.function.m mVar) {
        return (j < 0 || j >= 2147483639) ? new C0162t2() : new C0057b2(j, mVar);
    }

    public static A1 e(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        long q0 = abstractC0187y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            A1 a1 = (A1) new H1(abstractC0187y2, mVar, uVar).invoke();
            return z ? l(a1, mVar) : a1;
        } else if (q0 < 2147483639) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new C0152r2(uVar, abstractC0187y2, objArr).invoke();
            return new D1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0166u1 f(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0187y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0166u1 abstractC0166u1 = (AbstractC0166u1) new H1(abstractC0187y2, uVar, 0).invoke();
            return z ? m(abstractC0166u1) : abstractC0166u1;
        } else if (q0 < 2147483639) {
            double[] dArr = new double[(int) q0];
            new C0135o2(uVar, abstractC0187y2, dArr).invoke();
            return new T1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0176w1 g(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0187y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0176w1 abstractC0176w1 = (AbstractC0176w1) new H1(abstractC0187y2, uVar, 1).invoke();
            return z ? n(abstractC0176w1) : abstractC0176w1;
        } else if (q0 < 2147483639) {
            int[] iArr = new int[(int) q0];
            new C0141p2(uVar, abstractC0187y2, iArr).invoke();
            return new C0063c2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0186y1 h(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0187y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0186y1 abstractC0186y1 = (AbstractC0186y1) new H1(abstractC0187y2, uVar, 2).invoke();
            return z ? o(abstractC0186y1) : abstractC0186y1;
        } else if (q0 < 2147483639) {
            long[] jArr = new long[(int) q0];
            new C0147q2(uVar, abstractC0187y2, jArr).invoke();
            return new C0117l2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static A1 i(EnumC0077e4 enumC0077e4, A1 a1, A1 a12) {
        int i = B1.a[enumC0077e4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new P1((AbstractC0176w1) a1, (AbstractC0176w1) a12);
            }
            if (i == 3) {
                return new Q1((AbstractC0186y1) a1, (AbstractC0186y1) a12);
            }
            if (i == 4) {
                return new O1((AbstractC0166u1) a1, (AbstractC0166u1) a12);
            }
            throw new IllegalStateException("Unknown shape " + enumC0077e4);
        }
        return new S1(a1, a12);
    }

    public static AbstractC0140p1 j(long j) {
        return (j < 0 || j >= 2147483639) ? new V1() : new U1(j);
    }

    public static A1 k(EnumC0077e4 enumC0077e4) {
        int i = B1.a[enumC0077e4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return b;
            }
            if (i == 3) {
                return c;
            }
            if (i == 4) {
                return d;
            }
            throw new IllegalStateException("Unknown shape " + enumC0077e4);
        }
        return a;
    }

    public static A1 l(A1 a1, j$.util.function.m mVar) {
        if (a1.p() > 0) {
            long count = a1.count();
            if (count < 2147483639) {
                Object[] objArr = (Object[]) mVar.apply((int) count);
                new C0172v2(a1, objArr, 0, (B1) null).invoke();
                return new D1(objArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return a1;
    }

    public static AbstractC0166u1 m(AbstractC0166u1 abstractC0166u1) {
        if (abstractC0166u1.p() > 0) {
            long count = abstractC0166u1.count();
            if (count < 2147483639) {
                double[] dArr = new double[(int) count];
                new C0167u2(abstractC0166u1, dArr, 0).invoke();
                return new T1(dArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0166u1;
    }

    public static AbstractC0176w1 n(AbstractC0176w1 abstractC0176w1) {
        if (abstractC0176w1.p() > 0) {
            long count = abstractC0176w1.count();
            if (count < 2147483639) {
                int[] iArr = new int[(int) count];
                new C0167u2(abstractC0176w1, iArr, 0).invoke();
                return new C0063c2(iArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0176w1;
    }

    public static AbstractC0186y1 o(AbstractC0186y1 abstractC0186y1) {
        if (abstractC0186y1.p() > 0) {
            long count = abstractC0186y1.count();
            if (count < 2147483639) {
                long[] jArr = new long[(int) count];
                new C0167u2(abstractC0186y1, jArr, 0).invoke();
                return new C0117l2(jArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0186y1;
    }

    public static AbstractC0146q1 p(long j) {
        return (j < 0 || j >= 2147483639) ? new C0075e2() : new C0069d2(j);
    }

    public static AbstractC0151r1 q(long j) {
        return (j < 0 || j >= 2147483639) ? new C0129n2() : new C0123m2(j);
    }
}
