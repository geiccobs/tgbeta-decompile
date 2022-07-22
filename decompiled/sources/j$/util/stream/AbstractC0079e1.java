package j$.util.stream;

import j$.util.C0047i;
import j$.util.C0048j;
import j$.util.C0050l;
import j$.util.function.BiConsumer;
import j$.wrappers.C0218j0;
import j$.wrappers.C0222l0;
import j$.wrappers.C0226n0;
/* renamed from: j$.util.stream.e1 */
/* loaded from: classes2.dex */
public interface AbstractC0079e1 extends AbstractC0089g {
    long D(long j, j$.util.function.o oVar);

    boolean L(C0218j0 c0218j0);

    U O(C0222l0 c0222l0);

    Stream Q(j$.util.function.r rVar);

    boolean S(C0218j0 c0218j0);

    void Z(j$.util.function.q qVar);

    U asDoubleStream();

    C0048j average();

    Stream boxed();

    long count();

    void d(j$.util.function.q qVar);

    AbstractC0079e1 distinct();

    IntStream e0(C0226n0 c0226n0);

    Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer);

    C0050l findAny();

    C0050l findFirst();

    C0050l g(j$.util.function.o oVar);

    @Override // j$.util.stream.AbstractC0089g
    /* renamed from: iterator */
    j$.util.r mo66iterator();

    boolean k(C0218j0 c0218j0);

    AbstractC0079e1 limit(long j);

    C0050l max();

    C0050l min();

    AbstractC0079e1 p(j$.util.function.q qVar);

    @Override // j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    AbstractC0079e1 parallel();

    AbstractC0079e1 s(j$.util.function.r rVar);

    @Override // j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    AbstractC0079e1 sequential();

    AbstractC0079e1 skip(long j);

    AbstractC0079e1 sorted();

    @Override // j$.util.stream.AbstractC0089g
    j$.util.v spliterator();

    long sum();

    C0047i summaryStatistics();

    long[] toArray();

    AbstractC0079e1 u(C0218j0 c0218j0);

    AbstractC0079e1 z(j$.util.function.t tVar);
}
