package j$.util.stream;

import j$.util.C0047h;
import j$.util.C0049j;
import j$.util.C0050k;
import j$.util.function.BiConsumer;
import j$.util.p;
import j$.util.u;
import j$.wrappers.C0203b0;
/* loaded from: classes2.dex */
public interface IntStream extends AbstractC0090g {
    U A(j$.wrappers.X x);

    boolean C(j$.wrappers.V v);

    boolean F(j$.wrappers.V v);

    void I(j$.util.function.l lVar);

    Stream J(j$.util.function.m mVar);

    int N(int i, j$.util.function.j jVar);

    IntStream P(j$.util.function.m mVar);

    void U(j$.util.function.l lVar);

    C0050k a0(j$.util.function.j jVar);

    U asDoubleStream();

    AbstractC0080e1 asLongStream();

    C0049j average();

    Stream boxed();

    IntStream c0(j$.util.function.l lVar);

    long count();

    IntStream distinct();

    AbstractC0080e1 f(j$.util.function.n nVar);

    C0050k findAny();

    C0050k findFirst();

    IntStream h(j$.wrappers.V v);

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    p.a mo66iterator();

    Object k0(j$.util.function.y yVar, j$.util.function.v vVar, BiConsumer biConsumer);

    IntStream limit(long j);

    C0050k max();

    C0050k min();

    @Override // 
    IntStream parallel();

    IntStream q(C0203b0 c0203b0);

    @Override // 
    IntStream sequential();

    IntStream skip(long j);

    IntStream sorted();

    @Override // j$.util.stream.AbstractC0090g
    u.a spliterator();

    int sum();

    C0047h summaryStatistics();

    int[] toArray();

    boolean v(j$.wrappers.V v);
}
