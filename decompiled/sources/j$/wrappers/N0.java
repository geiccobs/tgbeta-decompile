package j$.wrappers;

import j$.util.AbstractC0034a;
import j$.util.C0043i;
import j$.util.C0044j;
import j$.util.C0046l;
import j$.util.function.BiConsumer;
import j$.util.stream.AbstractC0075e1;
import j$.util.stream.AbstractC0085g;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import java.util.stream.LongStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class N0 implements AbstractC0075e1 {
    final /* synthetic */ LongStream a;

    private /* synthetic */ N0(LongStream longStream) {
        this.a = longStream;
    }

    public static /* synthetic */ AbstractC0075e1 n0(LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof O0 ? ((O0) longStream).a : new N0(longStream);
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ long D(long j, j$.util.function.o oVar) {
        return this.a.reduce(j, C0204e0.a(oVar));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ boolean L(C0214j0 c0214j0) {
        return this.a.allMatch(AbstractC0216k0.a(c0214j0));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ j$.util.stream.U O(C0218l0 c0218l0) {
        return L0.n0(this.a.mapToDouble(c0218l0 == null ? null : c0218l0.a));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ Stream Q(j$.util.function.r rVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(C0212i0.a(rVar)));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ boolean S(C0214j0 c0214j0) {
        return this.a.noneMatch(AbstractC0216k0.a(c0214j0));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ void Z(j$.util.function.q qVar) {
        this.a.forEachOrdered(C0208g0.a(qVar));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ j$.util.stream.U asDoubleStream() {
        return L0.n0(this.a.asDoubleStream());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ C0044j average() {
        return AbstractC0034a.q(this.a.average());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    @Override // j$.util.stream.AbstractC0085g, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ void d(j$.util.function.q qVar) {
        this.a.forEach(C0208g0.a(qVar));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 distinct() {
        return n0(this.a.distinct());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ IntStream e0(C0222n0 c0222n0) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.mapToInt(c0222n0 == null ? null : c0222n0.a));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ Object f0(j$.util.function.y yVar, j$.util.function.w wVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), w0.a(wVar), r.a(biConsumer));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ C0046l findAny() {
        return AbstractC0034a.s(this.a.findAny());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ C0046l findFirst() {
        return AbstractC0034a.s(this.a.findFirst());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ C0046l g(j$.util.function.o oVar) {
        return AbstractC0034a.s(this.a.reduce(C0204e0.a(oVar)));
    }

    @Override // j$.util.stream.AbstractC0085g
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ boolean k(C0214j0 c0214j0) {
        return this.a.anyMatch(AbstractC0216k0.a(c0214j0));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 limit(long j) {
        return n0(this.a.limit(j));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ C0046l max() {
        return AbstractC0034a.s(this.a.max());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ C0046l min() {
        return AbstractC0034a.s(this.a.min());
    }

    @Override // j$.util.stream.AbstractC0085g
    public /* synthetic */ AbstractC0085g onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 p(j$.util.function.q qVar) {
        return n0(this.a.peek(C0208g0.a(qVar)));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 s(j$.util.function.r rVar) {
        return n0(this.a.flatMap(C0212i0.a(rVar)));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 skip(long j) {
        return n0(this.a.skip(j));
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 sorted() {
        return n0(this.a.sorted());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ long sum() {
        return this.a.sum();
    }

    @Override // j$.util.stream.AbstractC0075e1
    public C0043i summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ long[] toArray() {
        return this.a.toArray();
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 u(C0214j0 c0214j0) {
        return n0(this.a.filter(AbstractC0216k0.a(c0214j0)));
    }

    @Override // j$.util.stream.AbstractC0085g
    public /* synthetic */ AbstractC0085g unordered() {
        return H0.n0(this.a.unordered());
    }

    @Override // j$.util.stream.AbstractC0075e1
    public /* synthetic */ AbstractC0075e1 z(j$.util.function.t tVar) {
        return n0(this.a.map(q0.a(tVar)));
    }
}
