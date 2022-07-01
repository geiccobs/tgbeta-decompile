package j$.util.stream;

import j$.util.AbstractC0047n;
import j$.util.C0040g;
import j$.util.C0043j;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public interface U extends AbstractC0084g {
    C0043j G(j$.util.function.d dVar);

    Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer);

    double K(double d, j$.util.function.d dVar);

    Stream M(j$.util.function.g gVar);

    IntStream R(j$.wrappers.G g);

    boolean Y(j$.wrappers.E e);

    C0043j average();

    U b(j$.util.function.f fVar);

    Stream boxed();

    long count();

    U distinct();

    C0043j findAny();

    C0043j findFirst();

    boolean h0(j$.wrappers.E e);

    boolean i0(j$.wrappers.E e);

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    AbstractC0047n mo66iterator();

    void j(j$.util.function.f fVar);

    void l0(j$.util.function.f fVar);

    U limit(long j);

    C0043j max();

    C0043j min();

    @Override // j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    U parallel();

    U r(j$.wrappers.E e);

    @Override // j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    U sequential();

    U skip(long j);

    U sorted();

    @Override // j$.util.stream.AbstractC0084g
    j$.util.t spliterator();

    double sum();

    C0040g summaryStatistics();

    double[] toArray();

    U w(j$.util.function.g gVar);

    AbstractC0074e1 x(j$.util.function.h hVar);

    U y(j$.wrappers.K k);
}
