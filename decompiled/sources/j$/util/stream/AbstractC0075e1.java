package j$.util.stream;

import j$.util.C0043i;
import j$.util.C0044j;
import j$.util.C0046l;
import j$.util.function.BiConsumer;
import j$.wrappers.C0214j0;
import j$.wrappers.C0218l0;
import j$.wrappers.C0222n0;
/* renamed from: j$.util.stream.e1 */
/* loaded from: classes2.dex */
public interface AbstractC0075e1 extends AbstractC0085g {
    long D(long j, j$.util.function.o oVar);

    boolean L(C0214j0 c0214j0);

    U O(C0218l0 c0218l0);

    Stream Q(j$.util.function.r rVar);

    boolean S(C0214j0 c0214j0);

    void Z(j$.util.function.q qVar);

    U asDoubleStream();

    C0044j average();

    Stream boxed();

    long count();

    void d(j$.util.function.q qVar);

    AbstractC0075e1 distinct();

    IntStream e0(C0222n0 c0222n0);

    Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer);

    C0046l findAny();

    C0046l findFirst();

    C0046l g(j$.util.function.o oVar);

    @Override // j$.util.stream.AbstractC0085g
    /* renamed from: iterator */
    j$.util.r mo66iterator();

    boolean k(C0214j0 c0214j0);

    AbstractC0075e1 limit(long j);

    C0046l max();

    C0046l min();

    AbstractC0075e1 p(j$.util.function.q qVar);

    @Override // j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    AbstractC0075e1 parallel();

    AbstractC0075e1 s(j$.util.function.r rVar);

    @Override // j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    AbstractC0075e1 sequential();

    AbstractC0075e1 skip(long j);

    AbstractC0075e1 sorted();

    @Override // j$.util.stream.AbstractC0085g
    j$.util.v spliterator();

    long sum();

    C0043i summaryStatistics();

    long[] toArray();

    AbstractC0075e1 u(C0214j0 c0214j0);

    AbstractC0075e1 z(j$.util.function.t tVar);
}
