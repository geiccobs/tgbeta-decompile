package j$.wrappers;

import j$.util.AbstractC0038a;
import j$.util.C0045g;
import j$.util.C0048j;
import j$.util.function.BiConsumer;
import j$.util.stream.AbstractC0079e1;
import j$.util.stream.AbstractC0089g;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import java.util.stream.DoubleStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class L0 implements j$.util.stream.U {
    final /* synthetic */ DoubleStream a;

    private /* synthetic */ L0(DoubleStream doubleStream) {
        this.a = doubleStream;
    }

    public static /* synthetic */ j$.util.stream.U n0(DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof M0 ? ((M0) doubleStream).a : new L0(doubleStream);
    }

    @Override // j$.util.stream.U
    public /* synthetic */ C0048j G(j$.util.function.d dVar) {
        return AbstractC0038a.q(this.a.reduce(C0239z.a(dVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ Object H(j$.util.function.y yVar, j$.util.function.u uVar, BiConsumer biConsumer) {
        return this.a.collect(A0.a(yVar), s0.a(uVar), r.a(biConsumer));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ double K(double d, j$.util.function.d dVar) {
        return this.a.reduce(d, C0239z.a(dVar));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ Stream M(j$.util.function.g gVar) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.mapToObj(D.a(gVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ IntStream R(G g) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.a.mapToInt(g == null ? null : g.a));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ boolean Y(E e) {
        return this.a.allMatch(F.a(e));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ C0048j average() {
        return AbstractC0038a.q(this.a.average());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U b(j$.util.function.f fVar) {
        return n0(this.a.peek(B.a(fVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.a.boxed());
    }

    @Override // j$.util.stream.AbstractC0089g, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.U
    public /* synthetic */ long count() {
        return this.a.count();
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U distinct() {
        return n0(this.a.distinct());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ C0048j findAny() {
        return AbstractC0038a.q(this.a.findAny());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ C0048j findFirst() {
        return AbstractC0038a.q(this.a.findFirst());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ boolean h0(E e) {
        return this.a.anyMatch(F.a(e));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ boolean i0(E e) {
        return this.a.noneMatch(F.a(e));
    }

    @Override // j$.util.stream.AbstractC0089g
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.U
    public /* synthetic */ void j(j$.util.function.f fVar) {
        this.a.forEach(B.a(fVar));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ void l0(j$.util.function.f fVar) {
        this.a.forEachOrdered(B.a(fVar));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U limit(long j) {
        return n0(this.a.limit(j));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ C0048j max() {
        return AbstractC0038a.q(this.a.max());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ C0048j min() {
        return AbstractC0038a.q(this.a.min());
    }

    @Override // j$.util.stream.AbstractC0089g
    public /* synthetic */ AbstractC0089g onClose(Runnable runnable) {
        return H0.n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U r(E e) {
        return n0(this.a.filter(F.a(e)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U skip(long j) {
        return n0(this.a.skip(j));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U sorted() {
        return n0(this.a.sorted());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ double sum() {
        return this.a.sum();
    }

    @Override // j$.util.stream.U
    public C0045g summaryStatistics() {
        this.a.summaryStatistics();
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.DoubleSummaryStatistics");
    }

    @Override // j$.util.stream.U
    public /* synthetic */ double[] toArray() {
        return this.a.toArray();
    }

    @Override // j$.util.stream.AbstractC0089g
    public /* synthetic */ AbstractC0089g unordered() {
        return H0.n0(this.a.unordered());
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U w(j$.util.function.g gVar) {
        return n0(this.a.flatMap(D.a(gVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ AbstractC0079e1 x(j$.util.function.h hVar) {
        return N0.n0(this.a.mapToLong(J.a(hVar)));
    }

    @Override // j$.util.stream.U
    public /* synthetic */ j$.util.stream.U y(K k) {
        return n0(this.a.map(L.a(k)));
    }
}
