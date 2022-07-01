package j$.util.stream;

import j$.util.C0041h;
import j$.util.C0043j;
import j$.util.C0044k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0197b0;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class L0 extends AbstractC0060c implements IntStream {
    public L0(AbstractC0060c abstractC0060c, int i) {
        super(abstractC0060c, i);
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
        Q4.a(AbstractC0060c.class, "using IntStream.adapt(Spliterator<Integer> s)");
        throw null;
    }

    @Override // j$.util.stream.IntStream
    public final U A(j$.wrappers.X x) {
        x.getClass();
        return new K(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n, x);
    }

    @Override // j$.util.stream.AbstractC0060c
    final void A0(j$.util.u uVar, AbstractC0124m3 abstractC0124m3) {
        j$.util.function.l lVar;
        u.a M0 = M0(uVar);
        if (abstractC0124m3 instanceof j$.util.function.l) {
            lVar = (j$.util.function.l) abstractC0124m3;
        } else if (Q4.a) {
            Q4.a(AbstractC0060c.class, "using IntStream.adapt(Sink<Integer> s)");
            throw null;
        } else {
            lVar = new B0(abstractC0124m3);
        }
        while (!abstractC0124m3.o() && M0.g(lVar)) {
        }
    }

    @Override // j$.util.stream.AbstractC0060c
    public final EnumC0077e4 B0() {
        return EnumC0077e4.INT_VALUE;
    }

    @Override // j$.util.stream.IntStream
    public final boolean C(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0134o1.v(v, EnumC0110k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final boolean F(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0134o1.v(v, EnumC0110k1.ANY))).booleanValue();
    }

    public void I(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new C0115l0(lVar, true));
    }

    @Override // j$.util.stream.IntStream
    public final Stream J(j$.util.function.m mVar) {
        mVar.getClass();
        return new L(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n, mVar);
    }

    @Override // j$.util.stream.AbstractC0060c
    final j$.util.u K0(AbstractC0187y2 abstractC0187y2, j$.util.function.y yVar, boolean z) {
        return new C0149q4(abstractC0187y2, yVar, z);
    }

    @Override // j$.util.stream.IntStream
    public final int N(int i, j$.util.function.j jVar) {
        jVar.getClass();
        return ((Integer) x0(new L2(EnumC0077e4.INT_VALUE, jVar, i))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream P(j$.util.function.m mVar) {
        return new M(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n | EnumC0071d4.t, mVar);
    }

    public void U(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new C0115l0(lVar, false));
    }

    @Override // j$.util.stream.IntStream
    public final C0044k a0(j$.util.function.j jVar) {
        jVar.getClass();
        return (C0044k) x0(new D2(EnumC0077e4.INT_VALUE, jVar));
    }

    @Override // j$.util.stream.IntStream
    public final U asDoubleStream() {
        return new O(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n);
    }

    @Override // j$.util.stream.IntStream
    public final AbstractC0074e1 asLongStream() {
        return new G0(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n);
    }

    @Override // j$.util.stream.IntStream
    public final C0043j average() {
        long[] jArr = (long[]) k0(C0170v0.a, C0165u0.a, C0180x0.a);
        if (jArr[0] > 0) {
            double d = jArr[1];
            double d2 = jArr[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return C0043j.d(d / d2);
        }
        return C0043j.a();
    }

    @Override // j$.util.stream.IntStream
    public final Stream boxed() {
        return J(C0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream c0(j$.util.function.l lVar) {
        lVar.getClass();
        return new M(this, this, EnumC0077e4.INT_VALUE, 0, lVar);
    }

    @Override // j$.util.stream.IntStream
    public final long count() {
        return ((AbstractC0068d1) f(E0.a)).sum();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream distinct() {
        return ((AbstractC0076e3) J(C0.a)).distinct().m(C0175w0.a);
    }

    @Override // j$.util.stream.IntStream
    public final AbstractC0074e1 f(j$.util.function.n nVar) {
        nVar.getClass();
        return new N(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n, nVar);
    }

    @Override // j$.util.stream.IntStream
    public final C0044k findAny() {
        return (C0044k) x0(new C0067d0(false, EnumC0077e4.INT_VALUE, C0044k.a(), X.a, C0049a0.a));
    }

    @Override // j$.util.stream.IntStream
    public final C0044k findFirst() {
        return (C0044k) x0(new C0067d0(true, EnumC0077e4.INT_VALUE, C0044k.a(), X.a, C0049a0.a));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream h(j$.wrappers.V v) {
        v.getClass();
        return new M(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.t, v);
    }

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    public final p.a mo66iterator() {
        return j$.util.L.g(spliterator());
    }

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    public Iterator mo66iterator() {
        return j$.util.L.g(spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 1);
        yVar.getClass();
        vVar.getClass();
        return x0(new C0192z2(EnumC0077e4.INT_VALUE, c, vVar, yVar));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream limit(long j) {
        if (j >= 0) {
            return B3.g(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.IntStream
    public final C0044k max() {
        return a0(C0190z0.a);
    }

    @Override // j$.util.stream.IntStream
    public final C0044k min() {
        return a0(A0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream q(C0197b0 c0197b0) {
        c0197b0.getClass();
        return new M(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.p | EnumC0071d4.n, c0197b0);
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

    @Override // j$.util.stream.AbstractC0060c, j$.util.stream.AbstractC0084g
    public final u.a spliterator() {
        return M0(super.spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final int sum() {
        return ((Integer) x0(new L2(EnumC0077e4.INT_VALUE, C0185y0.a, 0))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final C0041h summaryStatistics() {
        return (C0041h) k0(C0102j.a, C0160t0.a, C0155s0.a);
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final AbstractC0156s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0182x2.p(j);
    }

    @Override // j$.util.stream.IntStream
    public final int[] toArray() {
        return (int[]) AbstractC0182x2.n((AbstractC0176w1) y0(D0.a)).e();
    }

    @Override // j$.util.stream.AbstractC0084g
    public AbstractC0084g unordered() {
        return !C0() ? this : new H0(this, this, EnumC0077e4.INT_VALUE, EnumC0071d4.r);
    }

    @Override // j$.util.stream.IntStream
    public final boolean v(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0134o1.v(v, EnumC0110k1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0060c
    final A1 z0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0182x2.g(abstractC0187y2, uVar, z);
    }
}
