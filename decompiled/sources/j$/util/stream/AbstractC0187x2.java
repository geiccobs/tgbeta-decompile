package j$.util.stream;
/* renamed from: j$.util.stream.x2 */
/* loaded from: classes2.dex */
public abstract class AbstractC0187x2 {
    private static final A1 a = new Z1(null);
    private static final AbstractC0181w1 b = new X1();
    private static final AbstractC0191y1 c = new Y1();
    private static final AbstractC0171u1 d = new W1();
    private static final int[] e = new int[0];
    private static final long[] f = new long[0];
    private static final double[] g = new double[0];

    public static AbstractC0161s1 d(long j, j$.util.function.m mVar) {
        return (j < 0 || j >= 2147483639) ? new C0167t2() : new C0062b2(j, mVar);
    }

    public static A1 e(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        long q0 = abstractC0192y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            A1 a1 = (A1) new H1(abstractC0192y2, mVar, uVar).invoke();
            return z ? l(a1, mVar) : a1;
        } else if (q0 < 2147483639) {
            Object[] objArr = (Object[]) mVar.apply((int) q0);
            new C0157r2(uVar, abstractC0192y2, objArr).invoke();
            return new D1(objArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0171u1 f(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0192y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0171u1 abstractC0171u1 = (AbstractC0171u1) new H1(abstractC0192y2, uVar, 0).invoke();
            return z ? m(abstractC0171u1) : abstractC0171u1;
        } else if (q0 < 2147483639) {
            double[] dArr = new double[(int) q0];
            new C0140o2(uVar, abstractC0192y2, dArr).invoke();
            return new T1(dArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0181w1 g(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0192y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0181w1 abstractC0181w1 = (AbstractC0181w1) new H1(abstractC0192y2, uVar, 1).invoke();
            return z ? n(abstractC0181w1) : abstractC0181w1;
        } else if (q0 < 2147483639) {
            int[] iArr = new int[(int) q0];
            new C0146p2(uVar, abstractC0192y2, iArr).invoke();
            return new C0068c2(iArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static AbstractC0191y1 h(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, boolean z) {
        long q0 = abstractC0192y2.q0(uVar);
        if (q0 < 0 || !uVar.hasCharacteristics(16384)) {
            AbstractC0191y1 abstractC0191y1 = (AbstractC0191y1) new H1(abstractC0192y2, uVar, 2).invoke();
            return z ? o(abstractC0191y1) : abstractC0191y1;
        } else if (q0 < 2147483639) {
            long[] jArr = new long[(int) q0];
            new C0152q2(uVar, abstractC0192y2, jArr).invoke();
            return new C0122l2(jArr);
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }

    public static A1 i(EnumC0082e4 enumC0082e4, A1 a1, A1 a12) {
        int i = B1.a[enumC0082e4.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return new P1((AbstractC0181w1) a1, (AbstractC0181w1) a12);
            }
            if (i == 3) {
                return new Q1((AbstractC0191y1) a1, (AbstractC0191y1) a12);
            }
            if (i == 4) {
                return new O1((AbstractC0171u1) a1, (AbstractC0171u1) a12);
            }
            throw new IllegalStateException("Unknown shape " + enumC0082e4);
        }
        return new S1(a1, a12);
    }

    public static AbstractC0145p1 j(long j) {
        return (j < 0 || j >= 2147483639) ? new V1() : new U1(j);
    }

    public static A1 k(EnumC0082e4 enumC0082e4) {
        int i = B1.a[enumC0082e4.ordinal()];
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
            throw new IllegalStateException("Unknown shape " + enumC0082e4);
        }
        return a;
    }

    public static A1 l(A1 a1, j$.util.function.m mVar) {
        if (a1.p() > 0) {
            long count = a1.count();
            if (count < 2147483639) {
                Object[] objArr = (Object[]) mVar.apply((int) count);
                new C0177v2(a1, objArr, 0, (B1) null).invoke();
                return new D1(objArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return a1;
    }

    public static AbstractC0171u1 m(AbstractC0171u1 abstractC0171u1) {
        if (abstractC0171u1.p() > 0) {
            long count = abstractC0171u1.count();
            if (count < 2147483639) {
                double[] dArr = new double[(int) count];
                new C0172u2(abstractC0171u1, dArr, 0).invoke();
                return new T1(dArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0171u1;
    }

    public static AbstractC0181w1 n(AbstractC0181w1 abstractC0181w1) {
        if (abstractC0181w1.p() > 0) {
            long count = abstractC0181w1.count();
            if (count < 2147483639) {
                int[] iArr = new int[(int) count];
                new C0172u2(abstractC0181w1, iArr, 0).invoke();
                return new C0068c2(iArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0181w1;
    }

    public static AbstractC0191y1 o(AbstractC0191y1 abstractC0191y1) {
        if (abstractC0191y1.p() > 0) {
            long count = abstractC0191y1.count();
            if (count < 2147483639) {
                long[] jArr = new long[(int) count];
                new C0172u2(abstractC0191y1, jArr, 0).invoke();
                return new C0122l2(jArr);
            }
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        return abstractC0191y1;
    }

    public static AbstractC0151q1 p(long j) {
        return (j < 0 || j >= 2147483639) ? new C0080e2() : new C0074d2(j);
    }

    public static AbstractC0156r1 q(long j) {
        return (j < 0 || j >= 2147483639) ? new C0134n2() : new C0128m2(j);
    }
}
