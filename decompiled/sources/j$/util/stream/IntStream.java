package j$.util.stream;

import j$.util.C0046h;
import j$.util.C0048j;
import j$.util.C0049k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0202b0;
/* loaded from: classes2.dex */
public interface IntStream extends AbstractC0089g {
    U A(j$.wrappers.X x);

    boolean C(j$.wrappers.V v);

    boolean F(j$.wrappers.V v);

    void I(j$.util.function.l lVar);

    Stream J(j$.util.function.m mVar);

    int N(int i, j$.util.function.j jVar);

    IntStream P(j$.util.function.m mVar);

    void U(j$.util.function.l lVar);

    C0049k a0(j$.util.function.j jVar);

    U asDoubleStream();

    AbstractC0079e1 asLongStream();

    C0048j average();

    Stream boxed();

    IntStream c0(j$.util.function.l lVar);

    long count();

    IntStream distinct();

    AbstractC0079e1 f(j$.util.function.n nVar);

    C0049k findAny();

    C0049k findFirst();

    IntStream h(j$.wrappers.V v);

    @Override // j$.util.stream.AbstractC0089g
    /* renamed from: iterator */
    p.a mo66iterator();

    Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer);

    IntStream limit(long j);

    C0049k max();

    C0049k min();

    @Override // 
    IntStream parallel();

    IntStream q(C0202b0 c0202b0);

    @Override // 
    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    @Override // j$.util.stream.AbstractC0089g
    u.a spliterator();

    int sum();

    C0046h summaryStatistics();

    int[] toArray();

    boolean v(j$.wrappers.V v);
}
