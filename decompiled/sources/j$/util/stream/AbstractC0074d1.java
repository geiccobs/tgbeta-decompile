package j$.util.stream;

import j$.util.C0048i;
import j$.util.C0049j;
import j$.util.C0051l;
import j$.util.function.BiConsumer;
import j$.wrappers.C0219j0;
import j$.wrappers.C0223l0;
import j$.wrappers.C0227n0;
import java.util.Iterator;
/* renamed from: j$.util.stream.d1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0074d1 extends AbstractC0066c implements AbstractC0080e1 {
    public AbstractC0074d1(AbstractC0066c abstractC0066c, int i) {
        super(abstractC0066c, i);
    }

    public AbstractC0074d1(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    public static /* synthetic */ j$.util.v L0(j$.util.u uVar) {
        return M0(uVar);
    }

    public static j$.util.v M0(j$.util.u uVar) {
        if (uVar instanceof j$.util.v) {
            return (j$.util.v) uVar;
        }
        if (!Q4.a) {
            throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
        }
        Q4.a(AbstractC0066c.class, "using LongStream.adapt(Spliterator<Long> s)");
        throw null;
    }

    @Override // j$.util.stream.AbstractC0066c
    final void A0(j$.util.u uVar, AbstractC0130m3 abstractC0130m3) {
        j$.util.function.q qVar;
        j$.util.v M0 = M0(uVar);
        if (abstractC0130m3 instanceof j$.util.function.q) {
            qVar = (j$.util.function.q) abstractC0130m3;
        } else if (Q4.a) {
            Q4.a(AbstractC0066c.class, "using LongStream.adapt(Sink<Long> s)");
            throw null;
        } else {
            qVar = new W0(abstractC0130m3);
        }
        while (!abstractC0130m3.o() && M0.i(qVar)) {
        }
    }

    @Override // j$.util.stream.AbstractC0066c
    public final EnumC0083e4 B0() {
        return EnumC0083e4.LONG_VALUE;
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final long D(long j, j$.util.function.o oVar) {
        oVar.getClass();
        return ((Long) x0(new P2(EnumC0083e4.LONG_VALUE, oVar, j))).longValue();
    }

    @Override // j$.util.stream.AbstractC0066c
    final j$.util.u K0(AbstractC0193y2 abstractC0193y2, j$.util.function.y yVar, boolean z) {
        return new s4(abstractC0193y2, yVar, z);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final boolean L(C0219j0 c0219j0) {
        return ((Boolean) x0(AbstractC0140o1.w(c0219j0, EnumC0116k1.ALL))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final U O(C0223l0 c0223l0) {
        c0223l0.getClass();
        return new K(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.p | EnumC0077d4.n, c0223l0);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final Stream Q(j$.util.function.r rVar) {
        rVar.getClass();
        return new L(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.p | EnumC0077d4.n, rVar);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final boolean S(C0219j0 c0219j0) {
        return ((Boolean) x0(AbstractC0140o1.w(c0219j0, EnumC0116k1.NONE))).booleanValue();
    }

    public void Z(j$.util.function.q qVar) {
        qVar.getClass();
        x0(new C0127m0(qVar, true));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final U asDoubleStream() {
        return new O(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.p | EnumC0077d4.n);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0049j average() {
        long[] jArr = (long[]) f0(P0.a, O0.a, R0.a);
        if (jArr[0] > 0) {
            double d = jArr[1];
            double d2 = jArr[0];
            Double.isNaN(d);
            Double.isNaN(d2);
            return C0049j.d(d / d2);
        }
        return C0049j.a();
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final Stream boxed() {
        return Q(X0.a);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final long count() {
        return ((AbstractC0074d1) z(Y0.a)).sum();
    }

    public void d(j$.util.function.q qVar) {
        qVar.getClass();
        x0(new C0127m0(qVar, false));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 distinct() {
        return ((AbstractC0082e3) Q(X0.a)).distinct().g0(Q0.a);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final IntStream e0(C0227n0 c0227n0) {
        c0227n0.getClass();
        return new M(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.p | EnumC0077d4.n, c0227n0);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer) {
        C c = new C(biConsumer, 2);
        yVar.getClass();
        wVar.getClass();
        return x0(new C0198z2(EnumC0083e4.LONG_VALUE, c, wVar, yVar));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0051l findAny() {
        return (C0051l) x0(new C0073d0(false, EnumC0083e4.LONG_VALUE, C0051l.a(), Y.a, C0061b0.a));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0051l findFirst() {
        return (C0051l) x0(new C0073d0(true, EnumC0083e4.LONG_VALUE, C0051l.a(), Y.a, C0061b0.a));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0051l g(j$.util.function.o oVar) {
        oVar.getClass();
        return (C0051l) x0(new D2(EnumC0083e4.LONG_VALUE, oVar));
    }

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    public final j$.util.r mo66iterator() {
        return j$.util.L.h(spliterator());
    }

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    public Iterator mo66iterator() {
        return j$.util.L.h(spliterator());
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final boolean k(C0219j0 c0219j0) {
        return ((Boolean) x0(AbstractC0140o1.w(c0219j0, EnumC0116k1.ANY))).booleanValue();
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 limit(long j) {
        if (j >= 0) {
            return B3.h(this, 0L, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0051l max() {
        return g(U0.a);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0051l min() {
        return g(V0.a);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 p(j$.util.function.q qVar) {
        qVar.getClass();
        return new N(this, this, EnumC0083e4.LONG_VALUE, 0, qVar);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 s(j$.util.function.r rVar) {
        return new N(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.p | EnumC0077d4.n | EnumC0077d4.t, rVar);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 skip(long j) {
        int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : B3.h(this, j, -1L);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 sorted() {
        return new L3(this);
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g
    public final j$.util.v spliterator() {
        return M0(super.spliterator());
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final long sum() {
        return ((Long) x0(new P2(EnumC0083e4.LONG_VALUE, T0.a, 0L))).longValue();
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final C0048i summaryStatistics() {
        return (C0048i) f0(C0114k.a, N0.a, M0.a);
    }

    @Override // j$.util.stream.AbstractC0193y2
    public final AbstractC0162s1 t0(long j, j$.util.function.m mVar) {
        return AbstractC0188x2.q(j);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final long[] toArray() {
        return (long[]) AbstractC0188x2.o((AbstractC0192y1) y0(S0.a)).e();
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 u(C0219j0 c0219j0) {
        c0219j0.getClass();
        return new N(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.t, c0219j0);
    }

    @Override // j$.util.stream.AbstractC0090g
    public AbstractC0090g unordered() {
        return !C0() ? this : new G0(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.r);
    }

    @Override // j$.util.stream.AbstractC0080e1
    public final AbstractC0080e1 z(j$.util.function.t tVar) {
        tVar.getClass();
        return new N(this, this, EnumC0083e4.LONG_VALUE, EnumC0077d4.p | EnumC0077d4.n, tVar);
    }

    @Override // j$.util.stream.AbstractC0066c
    final A1 z0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        return AbstractC0188x2.h(abstractC0193y2, uVar, z);
    }
}
