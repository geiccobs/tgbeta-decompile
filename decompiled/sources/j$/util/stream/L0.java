package j$.util.stream;

import j$.util.C0046h;
import j$.util.C0048j;
import j$.util.C0049k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0202b0;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class L0 extends AbstractC0065c implements IntStream {
    public L0(AbstractC0065c abstractC0065c, int i) {
        super(abstractC0065c, i);
    }

    public L0(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    public static /* synthetic */ u.a L0(j$.util.u uVar) {
        return M0(uVar);
    }

    public static u.a M0(j$.util.u uVar) {
        if (uVar instanceof u.a) {
            return (u.a) uVar;
        }
        if (!Q4.a) {
            throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
        }
        Q4.a(AbstractC0065c.class, "using IntStream.adapt(Spliterator<Integer> s)");
        throw null;
    }

    @Override // j$.util.stream.IntStream
    public final U A(j$.wrappers.X x) {
        x.getClass();
        return new K(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n, x);
    }

    @Override // j$.util.stream.AbstractC0065c
    final void A0(j$.util.u uVar, AbstractC0129m3 abstractC0129m3) {
        j$.util.function.l lVar;
        u.a M0 = M0(uVar);
        if (abstractC0129m3 instanceof j$.util.function.l) {
            lVar = (j$.util.function.l) abstractC0129m3;
        } else if (Q4.a) {
            Q4.a(AbstractC0065c.class, "using IntStream.adapt(Sink<Integer> s)");
            throw null;
        } else {
            lVar = new B0(abstractC0129m3);
        }
        while (!abstractC0129m3.o() && M0.g(lVar)) {
        }
    }

    @Override // j$.util.stream.AbstractC0065c
    public final EnumC0082e4 B0() {
        return EnumC0082e4.INT_VALUE;
    }

    @Override // j$.util.stream.IntStream
    public final boolean C(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0139o1.v(v, EnumC0115k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final boolean F(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0139o1.v(v, EnumC0115k1.ANY))).booleanValue();
    }

    public void I(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new C0120l0(lVar, true));
    }

    @Override // j$.util.stream.IntStream
    public final Stream J(j$.util.function.m mVar) {
        mVar.getClass();
        return new L(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n, mVar);
    }

    @Override // j$.util.stream.AbstractC0065c
    final j$.util.u K0(AbstractC0192y2 abstractC0192y2, j$.util.function.y yVar, boolean z) {
        return new C0154q4(abstractC0192y2, yVar, z);
    }

    @Override // j$.util.stream.IntStream
    public final int N(int i, j$.util.function.j jVar) {
        jVar.getClass();
        return ((Integer) x0(new L2(EnumC0082e4.INT_VALUE, jVar, i))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream P(j$.util.function.m mVar) {
        return new M(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n | EnumC0076d4.t, mVar);
    }

    public void U(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new C0120l0(lVar, false));
    }

    @Override // j$.util.stream.IntStream
    public final C0049k a0(j$.util.function.j jVar) {
        jVar.getClass();
        return (C0049k) x0(new D2(EnumC0082e4.INT_VALUE, jVar));
    }

    @Override // j$.util.stream.IntStream
    public final U asDoubleStream() {
        return new O(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n);
    }

    @Override // j$.util.stream.IntStream
    public final AbstractC0079e1 asLongStream() {
        return new G0(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n);
    }

    @Override // j$.util.stream.IntStream
    public final C0048j average() {
        long[] jArr = (long[]) k0(C0175v0.a, C0170u0.a, C0185x0.a);
        if (jArr[0] > 0) {
            double d = jArr[1];
            double d2 = jArr[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return C0048j.d(d / d2);
        }
        return C0048j.a();
    }

    @Override // j$.util.stream.IntStream
    public final Stream boxed() {
        return J(C0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream c0(j$.util.function.l lVar) {
        lVar.getClass();
        return new M(this, this, EnumC0082e4.INT_VALUE, 0, lVar);
    }

    @Override // j$.util.stream.IntStream
    public final long count() {
        return ((AbstractC0073d1) f(E0.a)).sum();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream distinct() {
        return ((AbstractC0081e3) J(C0.a)).distinct().m(C0180w0.a);
    }

    @Override // j$.util.stream.IntStream
    public final AbstractC0079e1 f(j$.util.function.n nVar) {
        nVar.getClass();
        return new N(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n, nVar);
    }

    @Override // j$.util.stream.IntStream
    public final C0049k findAny() {
        return (C0049k) x0(new C0072d0(false, EnumC0082e4.INT_VALUE, C0049k.a(), X.a, C0054a0.a));
    }

    @Override // j$.util.stream.IntStream
    public final C0049k findFirst() {
        return (C0049k) x0(new C0072d0(true, EnumC0082e4.INT_VALUE, C0049k.a(), X.a, C0054a0.a));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream h(j$.wrappers.V v) {
        v.getClass();
        return new M(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.t, v);
    }

    @Override // j$.util.stream.AbstractC0089g
    /* renamed from: iterator */
    public final p.a mo66iterator() {
        return j$.util.L.g(spliterator());
    }

    @Override // j$.util.stream.AbstractC0089g
    /* renamed from: iterator */
    public Iterator mo66iterator() {
        return j$.util.L.g(spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 1);
        yVar.getClass();
        vVar.getClass();
        return x0(new C0197z2(EnumC0082e4.INT_VALUE, c, vVar, yVar));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream limit(long j) {
        if (j >= 0) {
            return B3.g(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.IntStream
    public final C0049k max() {
        return a0(C0195z0.a);
    }

    @Override // j$.util.stream.IntStream
    public final C0049k min() {
        return a0(A0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream q(C0202b0 c0202b0) {
        c0202b0.getClass();
        return new M(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.p | EnumC0076d4.n, c0202b0);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.g(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream sorted() {
        return new K3(this);
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g
    public final u.a spliterator() {
        return M0(super.spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final int sum() {
        return ((Integer) x0(new L2(EnumC0082e4.INT_VALUE, C0190y0.a, 0))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final C0046h summaryStatistics() {
        return (C0046h) k0(C0107j.a, C0165t0.a, C0160s0.a);
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final AbstractC0161s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0187x2.p(j);
    }

    @Override // j$.util.stream.IntStream
    public final int[] toArray() {
        return (int[]) AbstractC0187x2.n((AbstractC0181w1) y0(D0.a)).e();
    }

    @Override // j$.util.stream.AbstractC0089g
    public AbstractC0089g unordered() {
        return !C0() ? this : new H0(this, this, EnumC0082e4.INT_VALUE, EnumC0076d4.r);
    }

    @Override // j$.util.stream.IntStream
    public final boolean v(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0139o1.v(v, EnumC0115k1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0065c
    final A1 z0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0187x2.g(abstractC0192y2, uVar, z);
    }
}
