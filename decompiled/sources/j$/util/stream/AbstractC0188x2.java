package j$.util.stream;
/* renamed from: j$.util.stream.x2 */
/* loaded from: classes2.dex */
public abstract class AbstractC0188x2 {
    private static final A1 a = new Z1(null);
    private static final AbstractC0182w1 b = new X1();
    private static final AbstractC0192y1 c = new Y1();
    private static final AbstractC0172u1 d = new W1();
    private static final int[] e = new int[0];
    private static final long[] f = new long[0];
    private static final double[] g = new double[0];

    public static AbstractC0162s1 d(long j, j$.util.function.m mVar) {
        return (j < 0 || j >= 2147483639) ? new C0168t2() : new C0063b2(j, mVar);
    }

    public static A1 e(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        long q0 = abstractC0193y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            A1 a1 = (A1) new H1(abstractC0193y2, mVar, uVar).invoke();
            return z ? l(a1, mVar) : a1;
        } else if (q0 < 2147483639) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new C0158r2(uVar, abstractC0193y2, objArr).invoke();
            return new D1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0172u1 f(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0193y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0172u1 abstractC0172u1 = (AbstractC0172u1) new H1(abstractC0193y2, uVar, 0).invoke();
            return z ? m(abstractC0172u1) : abstractC0172u1;
        } else if (q0 < 2147483639) {
            double[] dArr = new double[(int) q0];
            new C0141o2(uVar, abstractC0193y2, dArr).invoke();
            return new T1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0182w1 g(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0193y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0182w1 abstractC0182w1 = (AbstractC0182w1) new H1(abstractC0193y2, uVar, 1).invoke();
            return z ? n(abstractC0182w1) : abstractC0182w1;
        } else if (q0 < 2147483639) {
            int[] iArr = new int[(int) q0];
            new C0147p2(uVar, abstractC0193y2, iArr).invoke();
            return new C0069c2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0192y1 h(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0193y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0192y1 abstractC0192y1 = (AbstractC0192y1) new H1(abstractC0193y2, uVar, 2).invoke();
            return z ? o(abstractC0192y1) : abstractC0192y1;
        } else if (q0 < 2147483639) {
            long[] jArr = new long[(int) q0];
            new C0153q2(uVar, abstractC0193y2, jArr).invoke();
            return new C0123l2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static A1 i(EnumC0083e4 enumC0083e4, A1 a1, A1 a12) {
        int i = B1.a[enumC0083e4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new P1((AbstractC0182w1) a1, (AbstractC0182w1) a12);
            }
            if (i == 3) {
                return new Q1((AbstractC0192y1) a1, (AbstractC0192y1) a12);
            }
            if (i == 4) {
                return new O1((AbstractC0172u1) a1, (AbstractC0172u1) a12);
            }
            throw new IllegalStateException("Unknown shape " + enumC0083e4);
        }
        return new S1(a1, a12);
    }

    public static AbstractC0146p1 j(long j) {
        return (j < 0 || j >= 2147483639) ? new V1() : new U1(j);
    }

    public static A1 k(EnumC0083e4 enumC0083e4) {
        int i = B1.a[enumC0083e4.ordinal()];
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
            throw new IllegalStateException("Unknown shape " + enumC0083e4);
        }
        return a;
    }

    public static A1 l(A1 a1, j$.util.function.m mVar) {
        if (a1.p() > 0) {
            long count = a1.count();
            if (count < 2147483639) {
                Object[] objArr = (Object[]) mVar.apply((int) count);
                new C0178v2(a1, objArr, 0, (B1) null).invoke();
                return new D1(objArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return a1;
    }

    public static AbstractC0172u1 m(AbstractC0172u1 abstractC0172u1) {
        if (abstractC0172u1.p() > 0) {
            long count = abstractC0172u1.count();
            if (count < 2147483639) {
                double[] dArr = new double[(int) count];
                new C0173u2(abstractC0172u1, dArr, 0).invoke();
                return new T1(dArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0172u1;
    }

    public static AbstractC0182w1 n(AbstractC0182w1 abstractC0182w1) {
        if (abstractC0182w1.p() > 0) {
            long count = abstractC0182w1.count();
            if (count < 2147483639) {
                int[] iArr = new int[(int) count];
                new C0173u2(abstractC0182w1, iArr, 0).invoke();
                return new C0069c2(iArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0182w1;
    }

    public static AbstractC0192y1 o(AbstractC0192y1 abstractC0192y1) {
        if (abstractC0192y1.p() > 0) {
            long count = abstractC0192y1.count();
            if (count < 2147483639) {
                long[] jArr = new long[(int) count];
                new C0173u2(abstractC0192y1, jArr, 0).invoke();
                return new C0123l2(jArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0192y1;
    }

    public static AbstractC0152q1 p(long j) {
        return (j < 0 || j >= 2147483639) ? new C0081e2() : new C0075d2(j);
    }

    public static AbstractC0157r1 q(long j) {
        return (j < 0 || j >= 2147483639) ? new C0135n2() : new C0129m2(j);
    }
}
