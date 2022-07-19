package j$.util.stream;

import j$.util.AbstractC0053n;
import j$.util.C0046g;
import j$.util.C0049j;
import j$.util.function.BiConsumer;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class T extends AbstractC0066c implements U {
    public T(AbstractC0066c abstractC0066c, int i) {
        super(abstractC0066c, i);
    }

    public T(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    public static /* synthetic */ j$.util.t L0(j$.util.u uVar) {
        return M0(uVar);
    }

    public static j$.util.t M0(j$.util.u uVar) {
        if (uVar instanceof j$.util.t) {
            return (j$.util.t) uVar;
        }
        if (!Q4.a) {
            throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
        }
        Q4.a(AbstractC0066c.class, "using DoubleStream.adapt(Spliterator<Double> s)");
        throw null;
    }

    @Override // j$.util.stream.AbstractC0066c
    final void A0(j$.util.u uVar, AbstractC0130m3 abstractC0130m3) {
        j$.util.function.f fVar;
        j$.util.t M0 = M0(uVar);
        if (abstractC0130m3 instanceof j$.util.function.f) {
            fVar = (j$.util.function.f) abstractC0130m3;
        } else if (Q4.a) {
            Q4.a(AbstractC0066c.class, "using DoubleStream.adapt(Sink<Double> s)");
            throw null;
        } else {
            fVar = new F(abstractC0130m3);
        }
        while (!abstractC0130m3.o() && M0.k(fVar)) {
        }
    }

    @Override // j$.util.stream.AbstractC0066c
    public final EnumC0083e4 B0() {
        return EnumC0083e4.DOUBLE_VALUE;
    }

    @Override // j$.util.stream.U
    public final C0049j G(j$.util.function.d dVar) {
        dVar.getClass();
        return (C0049j) x0(new D2(EnumC0083e4.DOUBLE_VALUE, dVar));
    }

    @Override // j$.util.stream.U
    public final Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 0);
        yVar.getClass();
        uVar.getClass();
        return x0(new C0198z2(EnumC0083e4.DOUBLE_VALUE, c, uVar, yVar));
    }

    @Override // j$.util.stream.U
    public final double K(double d, j$.util.function.d dVar) {
        dVar.getClass();
        return ((Double) x0(new B2(EnumC0083e4.DOUBLE_VALUE, dVar, d))).doubleValue();
    }

    @Override // j$.util.stream.AbstractC0066c
    final j$.util.u K0(AbstractC0193y2 abstractC0193y2, j$.util.function.y yVar, boolean z) {
        return new C0143o4(abstractC0193y2, yVar, z);
    }

    @Override // j$.util.stream.U
    public final Stream M(j$.util.function.g gVar) {
        gVar.getClass();
        return new L(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.p | EnumC0077d4.n, gVar);
    }

    @Override // j$.util.stream.U
    public final IntStream R(j$.wrappers.G g) {
        g.getClass();
        return new M(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.p | EnumC0077d4.n, g);
    }

    @Override // j$.util.stream.U
    public final boolean Y(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractC0140o1.u(e, EnumC0116k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.U
    public final C0049j average() {
        double[] dArr = (double[]) H(C0185x.a, C0175v.a, A.a);
        return dArr[2] > 0.0d ? C0049j.d(AbstractC0120l.a(dArr) / dArr[2]) : C0049j.a();
    }

    @Override // j$.util.stream.U
    public final U b(j$.util.function.f fVar) {
        fVar.getClass();
        return new K(this, this, EnumC0083e4.DOUBLE_VALUE, 0, fVar);
    }

    @Override // j$.util.stream.U
    public final Stream boxed() {
        return M(G.a);
    }

    @Override // j$.util.stream.U
    public final long count() {
        return ((AbstractC0074d1) x(H.a)).sum();
    }

    @Override // j$.util.stream.U
    public final U distinct() {
        return ((AbstractC0082e3) M(G.a)).distinct().j0(C0195z.a);
    }

    @Override // j$.util.stream.U
    public final C0049j findAny() {
        return (C0049j) x0(new C0073d0(false, EnumC0083e4.DOUBLE_VALUE, C0049j.a(), W.a, Z.a));
    }

    @Override // j$.util.stream.U
    public final C0049j findFirst() {
        return (C0049j) x0(new C0073d0(true, EnumC0083e4.DOUBLE_VALUE, C0049j.a(), W.a, Z.a));
    }

    @Override // j$.util.stream.U
    public final boolean h0(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractC0140o1.u(e, EnumC0116k1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.U
    public final boolean i0(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractC0140o1.u(e, EnumC0116k1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    public final AbstractC0053n mo66iterator() {
        return j$.util.L.f(spliterator());
    }

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    public Iterator mo66iterator() {
        return j$.util.L.f(spliterator());
    }

    public void j(j$.util.function.f fVar) {
        fVar.getClass();
        x0(new C0115k0(fVar, false));
    }

    public void l0(j$.util.function.f fVar) {
        fVar.getClass();
        x0(new C0115k0(fVar, true));
    }

    @Override // j$.util.stream.U
    public final U limit(long j) {
        if (j >= 0) {
            return B3.f(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.U
    public final C0049j max() {
        return G(D.a);
    }

    @Override // j$.util.stream.U
    public final C0049j min() {
        return G(E.a);
    }

    @Override // j$.util.stream.U
    public final U r(j$.wrappers.E e) {
        e.getClass();
        return new K(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.t, e);
    }

    @Override // j$.util.stream.U
    public final U skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.f(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.U
    public final U sorted() {
        return new J3(this);
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g
    public final j$.util.t spliterator() {
        return M0(super.spliterator());
    }

    @Override // j$.util.stream.U
    public final double sum() {
        return AbstractC0120l.a((double[]) H(C0190y.a, C0180w.a, B.a));
    }

    @Override // j$.util.stream.U
    public final C0046g summaryStatistics() {
        return (C0046g) H(C0102i.a, C0170u.a, C0165t.a);
    }

    @Override // j$.util.stream.AbstractC0193y2
    public final AbstractC0162s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0188x2.j(j);
    }

    @Override // j$.util.stream.U
    public final double[] toArray() {
        return (double[]) AbstractC0188x2.m((AbstractC0172u1) y0(I.a)).e();
    }

    @Override // j$.util.stream.AbstractC0090g
    public AbstractC0090g unordered() {
        return !C0() ? this : new O(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.r);
    }

    @Override // j$.util.stream.U
    public final U w(j$.util.function.g gVar) {
        return new K(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.p | EnumC0077d4.n | EnumC0077d4.t, gVar);
    }

    @Override // j$.util.stream.U
    public final AbstractC0080e1 x(j$.util.function.h hVar) {
        hVar.getClass();
        return new N(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.p | EnumC0077d4.n, hVar);
    }

    @Override // j$.util.stream.U
    public final U y(j$.wrappers.K k) {
        k.getClass();
        return new K(this, this, EnumC0083e4.DOUBLE_VALUE, EnumC0077d4.p | EnumC0077d4.n, k);
    }

    @Override // j$.util.stream.AbstractC0066c
    final A1 z0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0188x2.f(abstractC0193y2, uVar, z);
    }
}
