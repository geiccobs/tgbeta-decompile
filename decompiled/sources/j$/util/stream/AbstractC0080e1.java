package j$.util.stream;

import j$.util.C0048i;
import j$.util.C0049j;
import j$.util.C0051l;
import j$.util.function.BiConsumer;
import j$.wrappers.C0219j0;
import j$.wrappers.C0223l0;
import j$.wrappers.C0227n0;
/* renamed from: j$.util.stream.e1 */
/* loaded from: classes2.dex */
public interface AbstractC0080e1 extends AbstractC0090g {
    long D(long j, j$.util.function.o oVar);

    boolean L(C0219j0 c0219j0);

    U O(C0223l0 c0223l0);

    Stream Q(j$.util.function.r rVar);

    boolean S(C0219j0 c0219j0);

    void Z(j$.util.function.q qVar);

    U asDoubleStream();

    C0049j average();

    Stream boxed();

    long count();

    void d(j$.util.function.q qVar);

    AbstractC0080e1 distinct();

    IntStream e0(C0227n0 c0227n0);

    Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer);

    C0051l findAny();

    C0051l findFirst();

    C0051l g(j$.util.function.o oVar);

    @Override // j$.util.stream.AbstractC0090g
    /* renamed from: iterator */
    j$.util.r mo66iterator();

    boolean k(C0219j0 c0219j0);

    AbstractC0080e1 limit(long j);

    C0051l max();

    C0051l min();

    AbstractC0080e1 p(j$.util.function.q qVar);

    @Override // j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    AbstractC0080e1 parallel();

    AbstractC0080e1 s(j$.util.function.r rVar);

    @Override // j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    AbstractC0080e1 sequential();

    AbstractC0080e1 skip(long j);

    AbstractC0080e1 sorted();

    @Override // j$.util.stream.AbstractC0090g
    j$.util.v spliterator();

    long sum();

    C0048i summaryStatistics();

    long[] toArray();

    AbstractC0080e1 u(C0219j0 c0219j0);

    AbstractC0080e1 z(j$.util.function.t tVar);
}
