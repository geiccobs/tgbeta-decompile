package j$.util.stream;

import j$.util.C0041h;
import j$.util.C0043j;
import j$.util.C0044k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0197b0;
/* loaded from: classes2.dex */
public interface IntStream extends AbstractC0084g {
    U A(j$.wrappers.X x);

    boolean C(j$.wrappers.V v);

    boolean F(j$.wrappers.V v);

    void I(j$.util.function.l lVar);

    Stream J(j$.util.function.m mVar);

    int N(int i, j$.util.function.j jVar);

    IntStream P(j$.util.function.m mVar);

    void U(j$.util.function.l lVar);

    C0044k a0(j$.util.function.j jVar);

    U asDoubleStream();

    AbstractC0074e1 asLongStream();

    C0043j average();

    Stream boxed();

    IntStream c0(j$.util.function.l lVar);

    long count();

    IntStream distinct();

    AbstractC0074e1 f(j$.util.function.n nVar);

    C0044k findAny();

    C0044k findFirst();

    IntStream h(j$.wrappers.V v);

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    p.a mo66iterator();

    Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer);

    IntStream limit(long j);

    C0044k max();

    C0044k min();

    @Override // 
    IntStream parallel();

    IntStream q(C0197b0 c0197b0);

    @Override // 
    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    @Override // j$.util.stream.AbstractC0084g
    u.a spliterator();

    int sum();

    C0041h summaryStatistics();

    int[] toArray();

    boolean v(j$.wrappers.V v);
}
