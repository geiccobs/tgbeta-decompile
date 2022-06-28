package j$.wrappers;

import j$.util.DoubleSummaryStatistics;
import j$.util.DoubleSummaryStatisticsConversions;
import j$.util.OptionalConversions;
import j$.util.OptionalDouble;
import j$.util.function.BiConsumer;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleFunction;
import j$.util.function.DoublePredicate;
import j$.util.function.DoubleToIntFunction;
import j$.util.function.DoubleToLongFunction;
import j$.util.function.DoubleUnaryOperator;
import j$.util.function.ObjDoubleConsumer;
import j$.util.function.Supplier;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.Stream;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$DoubleStream$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$DoubleStream$VWRP implements DoubleStream {
    final /* synthetic */ java.util.stream.DoubleStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$DoubleStream$VWRP(java.util.stream.DoubleStream doubleStream) {
        this.wrappedValue = doubleStream;
    }

    public static /* synthetic */ DoubleStream convert(java.util.stream.DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof C$r8$wrapper$java$util$stream$DoubleStream$WRP ? ((C$r8$wrapper$java$util$stream$DoubleStream$WRP) doubleStream).wrappedValue : new C$r8$wrapper$java$util$stream$DoubleStream$VWRP(doubleStream);
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ boolean allMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$DoublePredicate$WRP.convert(doublePredicate));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ boolean anyMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$DoublePredicate$WRP.convert(doublePredicate));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble average() {
        return OptionalConversions.convert(this.wrappedValue.average());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.wrappedValue.boxed());
    }

    @Override // j$.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$WRP.convert(supplier), C$r8$wrapper$java$util$function$ObjDoubleConsumer$WRP.convert(objDoubleConsumer), C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(biConsumer));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream filter(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$DoublePredicate$WRP.convert(doublePredicate)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream flatMap(DoubleFunction doubleFunction) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$DoubleFunction$WRP.convert(doubleFunction)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream map(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP.convert(doubleUnaryOperator)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ IntStream mapToInt(DoubleToIntFunction doubleToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$DoubleToIntFunction$WRP.convert(doubleToIntFunction)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ LongStream mapToLong(DoubleToLongFunction doubleToLongFunction) {
        return C$r8$wrapper$java$util$stream$LongStream$VWRP.convert(this.wrappedValue.mapToLong(C$r8$wrapper$java$util$function$DoubleToLongFunction$WRP.convert(doubleToLongFunction)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return C$r8$wrapper$java$util$stream$Stream$VWRP.convert(this.wrappedValue.mapToObj(C$r8$wrapper$java$util$function$DoubleFunction$WRP.convert(doubleFunction)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble max() {
        return OptionalConversions.convert(this.wrappedValue.max());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble min() {
        return OptionalConversions.convert(this.wrappedValue.min());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ boolean noneMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$DoublePredicate$WRP.convert(doublePredicate));
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [j$.util.stream.DoubleStream, j$.util.stream.BaseStream] */
    @Override // j$.util.stream.BaseStream
    public /* synthetic */ DoubleStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.onClose(runnable));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream peek(DoubleConsumer doubleConsumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ double reduce(double d, DoubleBinaryOperator doubleBinaryOperator) {
        return this.wrappedValue.reduce(d, C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP.convert(doubleBinaryOperator));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$DoubleBinaryOperator$WRP.convert(doubleBinaryOperator)));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleStream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ double sum() {
        return this.wrappedValue.sum();
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ DoubleSummaryStatistics summaryStatistics() {
        return DoubleSummaryStatisticsConversions.convert(this.wrappedValue.summaryStatistics());
    }

    @Override // j$.util.stream.DoubleStream
    public /* synthetic */ double[] toArray() {
        return this.wrappedValue.toArray();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [j$.util.stream.DoubleStream, j$.util.stream.BaseStream] */
    @Override // j$.util.stream.BaseStream
    public /* synthetic */ DoubleStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.unordered());
    }
}
