package j$.util.stream;

import j$.util.AbstractC0052n;
import j$.util.C0045g;
import j$.util.C0048j;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public interface U extends AbstractC0089g {
    C0048j G(j$.util.function.d dVar);

    Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer);

    double K(double d, j$.util.function.d dVar);

    Stream M(j$.util.function.g gVar);

    IntStream R(j$.wrappers.G g);

    boolean Y(j$.wrappers.E e);

    C0048j average();

    U b(j$.util.function.f fVar);

    Stream boxed();

    long count();

    U distinct();

    C0048j findAny();

    C0048j findFirst();

    boolean h0(j$.wrappers.E e);

    boolean i0(j$.wrappers.E e);

    @Override // j$.util.stream.AbstractC0089g
    /* renamed from: iterator */
    AbstractC0052n mo66iterator();

    void j(j$.util.function.f fVar);

    void l0(j$.util.function.f fVar);

    U limit(long j);

    C0048j max();

    C0048j min();

    @Override // j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    U parallel();

    U r(j$.wrappers.E e);

    @Override // j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    U sequential();

    U skip(long j);

    U sorted();

    @Override // j$.util.stream.AbstractC0089g
    j$.util.t spliterator();

    double sum();

    C0045g summaryStatistics();

    double[] toArray();

    U w(j$.util.function.g gVar);

    AbstractC0079e1 x(j$.util.function.h hVar);

    U y(j$.wrappers.K k);
}
