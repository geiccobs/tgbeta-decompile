package j$.util.stream;

import j$.util.C0047h;
import j$.util.C0049j;
import j$.util.C0050k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0203b0;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class L0 extends AbstractC0066c implements IntStream {
    public L0(AbstractC0066c abstractC0066c, int i) {
        super(abstractC0066c, i);
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
        Q4.a(AbstractC0066c.class, "using IntStream.adapt(Spliterator<Integer> s)");
        throw null;
    }

    @Override // j$.util.stream.IntStream
    public final U A(j$.wrappers.X x) {
        x.getClass();
        return new K(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n, x);
    }

    @Override // j$.util.stream.AbstractC0066c
    final void A0(j$.util.u uVar, AbstractC0130m3 abstractC0130m3) {
        j$.util.function.l lVar;
        u.a M0 = M0(uVar);
        if (abstractC0130m3 instanceof j$.util.function.l) {
            lVar = (j$.util.function.l) abstractC0130m3;
        } else if (Q4.a) {
            Q4.a(AbstractC0066c.class, "using IntStream.adapt(Sink<Integer> s)");
            throw null;
        } else {
            lVar = new B0(abstractC0130m3);
        }
        while (!abstractC0130m3.o() && M0.g(lVar)) {
        }
    }

    @Override // j$.util.stream.AbstractC0066c
    public final EnumC0083e4 B0() {
        return EnumC0083e4.INT_VALUE;
    }

    @Override // j$.util.stream.IntStream
    public final boolean C(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0140o1.v(v, EnumC0116k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.IntStream
    public final boolean F(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0140o1.v(v, EnumC0116k1.ANY))).booleanValue();
    }

    public void I(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new C0121l0(lVar, true));
    }

    @Override // j$.util.stream.IntStream
    public final Stream J(j$.util.function.m mVar) {
        mVar.getClass();
        return new L(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n, mVar);
    }

    @Override // j$.util.stream.AbstractC0066c
    final j$.util.u K0(AbstractC0193y2 abstractC0193y2, j$.util.function.y yVar, boolean z) {
        return new C0155q4(abstractC0193y2, yVar, z);
    }

    @Override // j$.util.stream.IntStream
    public final int N(int i, j$.util.function.j jVar) {
        jVar.getClass();
        return ((Integer) x0(new L2(EnumC0083e4.INT_VALUE, jVar, i))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream P(j$.util.function.m mVar) {
        return new M(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n | EnumC0077d4.t, mVar);
    }

    public void U(j$.util.function.l lVar) {
        lVar.getClass();
        x0(new C0121l0(lVar, false));
    }

    @Override // j$.util.stream.IntStream
    public final C0050k a0(j$.util.function.j jVar) {
        jVar.getClass();
        return (C0050k) x0(new D2(EnumC0083e4.INT_VALUE, jVar));
    }

    @Override // j$.util.stream.IntStream
    public final U asDoubleStream() {
        return new O(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n);
    }

    @Override // j$.util.stream.IntStream
    public final AbstractC0080e1 asLongStream() {
        return new G0(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n);
    }

    @Override // j$.util.stream.IntStream
    public final C0049j average() {
        long[] jArr = (long[]) k0(C0176v0.a, C0171u0.a, C0186x0.a);
        if (jArr[0] > 0) {
            double d = jArr[1];
            double d2 = jArr[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return C0049j.d(d / d2);
        }
        return C0049j.a();
    }

    @Override // j$.util.stream.IntStream
    public final Stream boxed() {
        return J(C0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream c0(j$.util.function.l lVar) {
        lVar.getClass();
        return new M(this, this, EnumC0083e4.INT_VALUE, 0, lVar);
    }

    @Override // j$.util.stream.IntStream
    public final long count() {
        return ((AbstractC0074d1) f(E0.a)).sum();
    }

    @Override // j$.util.stream.IntStream
    public final IntStream distinct() {
        return ((AbstractC0082e3) J(C0.a)).distinct().m(C0181w0.a);
    }

    @Override // j$.util.stream.IntStream
    public final AbstractC0080e1 f(j$.util.function.n nVar) {
        nVar.getClass();
        return new N(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n, nVar);
    }

    @Override // j$.util.stream.IntStream
    public final C0050k findAny() {
        return (C0050k) x0(new C0073d0(false, EnumC0083e4.INT_VALUE, C0050k.a(), X.a, C0055a0.a));
    }

    @Override // j$.util.stream.IntStream
    public final C0050k findFirst() {
        return (C0050k) x0(new C0073d0(true, EnumC0083e4.INT_VALUE, C0050k.a(), X.a, C0055a0.a));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream h(j$.wrappers.V v) {
        v.getClass();
        return new M(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.t, v);
    }

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    public final p.a mo66iterator() {
        return j$.util.L.g(spliterator());
    }

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    public Iterator mo66iterator() {
        return j$.util.L.g(spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 1);
        yVar.getClass();
        vVar.getClass();
        return x0(new C0198z2(EnumC0083e4.INT_VALUE, c, vVar, yVar));
    }

    @Override // j$.util.stream.IntStream
    public final IntStream limit(long j) {
        if (j >= 0) {
            return B3.g(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.IntStream
    public final C0050k max() {
        return a0(C0196z0.a);
    }

    @Override // j$.util.stream.IntStream
    public final C0050k min() {
        return a0(A0.a);
    }

    @Override // j$.util.stream.IntStream
    public final IntStream q(C0203b0 c0203b0) {
        c0203b0.getClass();
        return new M(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.p | EnumC0077d4.n, c0203b0);
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

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g
    public final u.a spliterator() {
        return M0(super.spliterator());
    }

    @Override // j$.util.stream.IntStream
    public final int sum() {
        return ((Integer) x0(new L2(EnumC0083e4.INT_VALUE, C0191y0.a, 0))).intValue();
    }

    @Override // j$.util.stream.IntStream
    public final C0047h summaryStatistics() {
        return (C0047h) k0(C0108j.a, C0166t0.a, C0161s0.a);
    }

    @Override // j$.util.stream.AbstractC0193y2
    public final AbstractC0162s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0188x2.p(j);
    }

    @Override // j$.util.stream.IntStream
    public final int[] toArray() {
        return (int[]) AbstractC0188x2.n((AbstractC0182w1) y0(D0.a)).e();
    }

    @Override // j$.util.stream.AbstractC0090g
    public AbstractC0090g unordered() {
        return !C0() ? this : new H0(this, this, EnumC0083e4.INT_VALUE, EnumC0077d4.r);
    }

    @Override // j$.util.stream.IntStream
    public final boolean v(j$.wrappers.V v) {
        return ((Boolean) x0(AbstractC0140o1.v(v, EnumC0116k1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0066c
    final A1 z0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0188x2.g(abstractC0193y2, uVar, z);
    }
}
