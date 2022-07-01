package j$.util.stream;

import j$.util.AbstractC0047n;
import j$.util.C0040g;
import j$.util.C0043j;
import j$.util.function.BiConsumer;
import java.util.Iterator;
/* loaded from: classes2.dex */
public abstract class T extends AbstractC0060c implements U {
    public T(AbstractC0060c abstractC0060c, int i) {
        super(abstractC0060c, i);
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
        Q4.a(AbstractC0060c.class, "using DoubleStream.adapt(Spliterator<Double> s)");
        throw null;
    }

    @Override // j$.util.stream.AbstractC0060c
    final void A0(j$.util.u uVar, AbstractC0124m3 abstractC0124m3) {
        j$.util.function.f fVar;
        j$.util.t M0 = M0(uVar);
        if (abstractC0124m3 instanceof j$.util.function.f) {
            fVar = (j$.util.function.f) abstractC0124m3;
        } else if (Q4.a) {
            Q4.a(AbstractC0060c.class, "using DoubleStream.adapt(Sink<Double> s)");
            throw null;
        } else {
            fVar = new F(abstractC0124m3);
        }
        while (!abstractC0124m3.o() && M0.k(fVar)) {
        }
    }

    @Override // j$.util.stream.AbstractC0060c
    public final EnumC0077e4 B0() {
        return EnumC0077e4.DOUBLE_VALUE;
    }

    @Override // j$.util.stream.U
    public final C0043j G(j$.util.function.d dVar) {
        dVar.getClass();
        return (C0043j) x0(new D2(EnumC0077e4.DOUBLE_VALUE, dVar));
    }

    @Override // j$.util.stream.U
    public final Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 0);
        yVar.getClass();
        uVar.getClass();
        return x0(new C0192z2(EnumC0077e4.DOUBLE_VALUE, c, uVar, yVar));
    }

    @Override // j$.util.stream.U
    public final double K(double d, j$.util.function.d dVar) {
        dVar.getClass();
        return ((Double) x0(new B2(EnumC0077e4.DOUBLE_VALUE, dVar, d))).doubleValue();
    }

    @Override // j$.util.stream.AbstractC0060c
    final j$.util.u K0(AbstractC0187y2 abstractC0187y2, j$.util.function.y yVar, boolean z) {
        return new C0137o4(abstractC0187y2, yVar, z);
    }

    @Override // j$.util.stream.U
    public final Stream M(j$.util.function.g gVar) {
        gVar.getClass();
        return new L(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.p | EnumC0071d4.n, gVar);
    }

    @Override // j$.util.stream.U
    public final IntStream R(j$.wrappers.G g) {
        g.getClass();
        return new M(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.p | EnumC0071d4.n, g);
    }

    @Override // j$.util.stream.U
    public final boolean Y(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractC0134o1.u(e, EnumC0110k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.U
    public final C0043j average() {
        double[] dArr = (double[]) H(C0179x.a, C0169v.a, A.a);
        return dArr[2] > 0.0d ? C0043j.d(AbstractC0114l.a(dArr) / dArr[2]) : C0043j.a();
    }

    @Override // j$.util.stream.U
    public final U b(j$.util.function.f fVar) {
        fVar.getClass();
        return new K(this, this, EnumC0077e4.DOUBLE_VALUE, 0, fVar);
    }

    @Override // j$.util.stream.U
    public final Stream boxed() {
        return M(G.a);
    }

    @Override // j$.util.stream.U
    public final long count() {
        return ((AbstractC0068d1) x(H.a)).sum();
    }

    @Override // j$.util.stream.U
    public final U distinct() {
        return ((AbstractC0076e3) M(G.a)).distinct().j0(C0189z.a);
    }

    @Override // j$.util.stream.U
    public final C0043j findAny() {
        return (C0043j) x0(new C0067d0(false, EnumC0077e4.DOUBLE_VALUE, C0043j.a(), W.a, Z.a));
    }

    @Override // j$.util.stream.U
    public final C0043j findFirst() {
        return (C0043j) x0(new C0067d0(true, EnumC0077e4.DOUBLE_VALUE, C0043j.a(), W.a, Z.a));
    }

    @Override // j$.util.stream.U
    public final boolean h0(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractC0134o1.u(e, EnumC0110k1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.U
    public final boolean i0(j$.wrappers.E e) {
        return ((Boolean) x0(AbstractC0134o1.u(e, EnumC0110k1.NONE))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    public final AbstractC0047n mo66iterator() {
        return j$.util.L.f(spliterator());
    }

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    public Iterator mo66iterator() {
        return j$.util.L.f(spliterator());
    }

    public void j(j$.util.function.f fVar) {
        fVar.getClass();
        x0(new C0109k0(fVar, false));
    }

    public void l0(j$.util.function.f fVar) {
        fVar.getClass();
        x0(new C0109k0(fVar, true));
    }

    @Override // j$.util.stream.U
    public final U limit(long j) {
        if (j >= 0) {
            return B3.f(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.U
    public final C0043j max() {
        return G(D.a);
    }

    @Override // j$.util.stream.U
    public final C0043j min() {
        return G(E.a);
    }

    @Override // j$.util.stream.U
    public final U r(j$.wrappers.E e) {
        e.getClass();
        return new K(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.t, e);
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

    @Override // j$.util.stream.AbstractC0060c, j$.util.stream.AbstractC0084g
    public final j$.util.t spliterator() {
        return M0(super.spliterator());
    }

    @Override // j$.util.stream.U
    public final double sum() {
        return AbstractC0114l.a((double[]) H(C0184y.a, C0174w.a, B.a));
    }

    @Override // j$.util.stream.U
    public final C0040g summaryStatistics() {
        return (C0040g) H(C0096i.a, C0164u.a, C0159t.a);
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final AbstractC0156s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0182x2.j(j);
    }

    @Override // j$.util.stream.U
    public final double[] toArray() {
        return (double[]) AbstractC0182x2.m((AbstractC0166u1) y0(I.a)).e();
    }

    @Override // j$.util.stream.AbstractC0084g
    public AbstractC0084g unordered() {
        return !C0() ? this : new O(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.r);
    }

    @Override // j$.util.stream.U
    public final U w(j$.util.function.g gVar) {
        return new K(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.p | EnumC0071d4.n | EnumC0071d4.t, gVar);
    }

    @Override // j$.util.stream.U
    public final AbstractC0074e1 x(j$.util.function.h hVar) {
        hVar.getClass();
        return new N(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.p | EnumC0071d4.n, hVar);
    }

    @Override // j$.util.stream.U
    public final U y(j$.wrappers.K k) {
        k.getClass();
        return new K(this, this, EnumC0077e4.DOUBLE_VALUE, EnumC0071d4.p | EnumC0071d4.n, k);
    }

    @Override // j$.util.stream.AbstractC0060c
    final A1 z0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0182x2.f(abstractC0187y2, uVar, z);
    }
}
