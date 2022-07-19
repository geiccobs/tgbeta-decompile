package j$.util.stream;

import j$.util.C0042h;
import j$.util.C0044j;
import j$.util.C0045k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0198b0;
/* loaded from: classes2.dex */
public interface IntStream extends AbstractC0085g {
    U A(j$.wrappers.X x);

    boolean C(j$.wrappers.V v);

    boolean F(j$.wrappers.V v);

    void I(j$.util.function.l lVar);

    Stream J(j$.util.function.m mVar);

    int N(int i, j$.util.function.j jVar);

    IntStream P(j$.util.function.m mVar);

    void U(j$.util.function.l lVar);

    C0045k a0(j$.util.function.j jVar);

    U asDoubleStream();

    AbstractC0075e1 asLongStream();

    C0044j average();

    Stream boxed();

    IntStream c0(j$.util.function.l lVar);

    long count();

    IntStream distinct();

    AbstractC0075e1 f(j$.util.function.n nVar);

    C0045k findAny();

    C0045k findFirst();

    IntStream h(j$.wrappers.V v);

    @Override // j$.util.stream.AbstractC0085g
    /* renamed from: iterator */
    p.a mo66iterator();

    Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer);

    IntStream limit(long j);

    C0045k max();

    C0045k min();

    @Override // 
    IntStream parallel();

    IntStream q(C0198b0 c0198b0);

    @Override // 
    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    @Override // j$.util.stream.AbstractC0085g
    u.a spliterator();

    int sum();

    C0042h summaryStatistics();

    int[] toArray();

    boolean v(j$.wrappers.V v);
}
