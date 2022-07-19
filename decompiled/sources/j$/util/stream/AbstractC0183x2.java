package j$.util.stream;
/* renamed from: j$.util.stream.x2 */
/* loaded from: classes2.dex */
public abstract class AbstractC0183x2 {
    private static final A1 a = new Z1(null);
    private static final AbstractC0177w1 b = new X1();
    private static final AbstractC0187y1 c = new Y1();
    private static final AbstractC0167u1 d = new W1();
    private static final int[] e = new int[0];
    private static final long[] f = new long[0];
    private static final double[] g = new double[0];

    public static AbstractC0157s1 d(long j, j$.util.function.m mVar) {
        return (j < 0 || j >= 2147483639) ? new C0163t2() : new C0058b2(j, mVar);
    }

    public static A1 e(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        long q0 = abstractC0188y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            A1 a1 = (A1) new H1(abstractC0188y2, mVar, uVar).invoke();
            return z ? l(a1, mVar) : a1;
        } else if (q0 < 2147483639) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new C0153r2(uVar, abstractC0188y2, objArr).invoke();
            return new D1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0167u1 f(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0188y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0167u1 abstractC0167u1 = (AbstractC0167u1) new H1(abstractC0188y2, uVar, 0).invoke();
            return z ? m(abstractC0167u1) : abstractC0167u1;
        } else if (q0 < 2147483639) {
            double[] dArr = new double[(int) q0];
            new C0136o2(uVar, abstractC0188y2, dArr).invoke();
            return new T1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0177w1 g(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0188y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0177w1 abstractC0177w1 = (AbstractC0177w1) new H1(abstractC0188y2, uVar, 1).invoke();
            return z ? n(abstractC0177w1) : abstractC0177w1;
        } else if (q0 < 2147483639) {
            int[] iArr = new int[(int) q0];
            new C0142p2(uVar, abstractC0188y2, iArr).invoke();
            return new C0064c2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0187y1 h(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0188y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0187y1 abstractC0187y1 = (AbstractC0187y1) new H1(abstractC0188y2, uVar, 2).invoke();
            return z ? o(abstractC0187y1) : abstractC0187y1;
        } else if (q0 < 2147483639) {
            long[] jArr = new long[(int) q0];
            new C0148q2(uVar, abstractC0188y2, jArr).invoke();
            return new C0118l2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static A1 i(EnumC0078e4 enumC0078e4, A1 a1, A1 a12) {
        int i = B1.a[enumC0078e4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new P1((AbstractC0177w1) a1, (AbstractC0177w1) a12);
            }
            if (i == 3) {
                return new Q1((AbstractC0187y1) a1, (AbstractC0187y1) a12);
            }
            if (i == 4) {
                return new O1((AbstractC0167u1) a1, (AbstractC0167u1) a12);
            }
            throw new IllegalStateException("Unknown shape " + enumC0078e4);
        }
        return new S1(a1, a12);
    }

    public static AbstractC0141p1 j(long j) {
        return (j < 0 || j >= 2147483639) ? new V1() : new U1(j);
    }

    public static A1 k(EnumC0078e4 enumC0078e4) {
        int i = B1.a[enumC0078e4.ordinal()];
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
            throw new IllegalStateException("Unknown shape " + enumC0078e4);
        }
        return a;
    }

    public static A1 l(A1 a1, j$.util.function.m mVar) {
        if (a1.p() > 0) {
            long count = a1.count();
            if (count < 2147483639) {
                Object[] objArr = (Object[]) mVar.apply((int) count);
                new C0173v2(a1, objArr, 0, (B1) null).invoke();
                return new D1(objArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return a1;
    }

    public static AbstractC0167u1 m(AbstractC0167u1 abstractC0167u1) {
        if (abstractC0167u1.p() > 0) {
            long count = abstractC0167u1.count();
            if (count < 2147483639) {
                double[] dArr = new double[(int) count];
                new C0168u2(abstractC0167u1, dArr, 0).invoke();
                return new T1(dArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0167u1;
    }

    public static AbstractC0177w1 n(AbstractC0177w1 abstractC0177w1) {
        if (abstractC0177w1.p() > 0) {
            long count = abstractC0177w1.count();
            if (count < 2147483639) {
                int[] iArr = new int[(int) count];
                new C0168u2(abstractC0177w1, iArr, 0).invoke();
                return new C0064c2(iArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0177w1;
    }

    public static AbstractC0187y1 o(AbstractC0187y1 abstractC0187y1) {
        if (abstractC0187y1.p() > 0) {
            long count = abstractC0187y1.count();
            if (count < 2147483639) {
                long[] jArr = new long[(int) count];
                new C0168u2(abstractC0187y1, jArr, 0).invoke();
                return new C0118l2(jArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0187y1;
    }

    public static AbstractC0147q1 p(long j) {
        return (j < 0 || j >= 2147483639) ? new C0076e2() : new C0070d2(j);
    }

    public static AbstractC0152r1 q(long j) {
        return (j < 0 || j >= 2147483639) ? new C0130n2() : new C0124m2(j);
    }
}
