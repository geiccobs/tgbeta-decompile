package j$.util.stream;

import j$.util.C0042i;
import j$.util.C0043j;
import j$.util.C0045l;
import j$.util.function.BiConsumer;
import j$.wrappers.C0213j0;
import j$.wrappers.C0217l0;
import j$.wrappers.C0221n0;
/* renamed from: j$.util.stream.e1 */
/* loaded from: classes2.dex */
public interface AbstractC0074e1 extends AbstractC0084g {
    long D(long j, j$.util.function.o oVar);

    boolean L(C0213j0 c0213j0);

    U O(C0217l0 c0217l0);

    Stream Q(j$.util.function.r rVar);

    boolean S(C0213j0 c0213j0);

    void Z(j$.util.function.q qVar);

    U asDoubleStream();

    C0043j average();

    Stream boxed();

    long count();

    void d(j$.util.function.q qVar);

    AbstractC0074e1 distinct();

    IntStream e0(C0221n0 c0221n0);

    Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer);

    C0045l findAny();

    C0045l findFirst();

    C0045l g(j$.util.function.o oVar);

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    j$.util.r mo66iterator();

    boolean k(C0213j0 c0213j0);

    AbstractC0074e1 limit(long j);

    C0045l max();

    C0045l min();

    AbstractC0074e1 p(j$.util.function.q qVar);

    @Override // j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    AbstractC0074e1 parallel();

    AbstractC0074e1 s(j$.util.function.r rVar);

    @Override // j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    AbstractC0074e1 sequential();

    AbstractC0074e1 skip(long j);

    AbstractC0074e1 sorted();

    @Override // j$.util.stream.AbstractC0084g
    j$.util.v spliterator();

    long sum();

    C0042i summaryStatistics();

    long[] toArray();

    AbstractC0074e1 u(C0213j0 c0213j0);

    AbstractC0074e1 z(j$.util.function.t tVar);
}
