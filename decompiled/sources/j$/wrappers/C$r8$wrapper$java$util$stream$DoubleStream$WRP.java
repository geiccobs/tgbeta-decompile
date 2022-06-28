package j$.wrappers;

import j$.util.DoubleSummaryStatisticsConversions;
import j$.util.OptionalConversions;
import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$DoubleStream$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$DoubleStream$WRP implements DoubleStream {
    final /* synthetic */ j$.util.stream.DoubleStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$DoubleStream$WRP(j$.util.stream.DoubleStream doubleStream) {
        this.wrappedValue = doubleStream;
    }

    public static /* synthetic */ DoubleStream convert(j$.util.stream.DoubleStream doubleStream) {
        if (doubleStream == null) {
            return null;
        }
        return doubleStream instanceof C$r8$wrapper$java$util$stream$DoubleStream$VWRP ? ((C$r8$wrapper$java$util$stream$DoubleStream$VWRP) doubleStream).wrappedValue : new C$r8$wrapper$java$util$stream$DoubleStream$WRP(doubleStream);
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ boolean allMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ boolean anyMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble average() {
        return OptionalConversions.convert(this.wrappedValue.average());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$WRP.convert(this.wrappedValue.boxed());
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ Object collect(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BiConsumer biConsumer) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$VWRP.convert(supplier), C$r8$wrapper$java$util$function$ObjDoubleConsumer$VWRP.convert(objDoubleConsumer), C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream filter(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream flatMap(DoubleFunction doubleFunction) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$DoubleFunction$VWRP.convert(doubleFunction)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ void forEach(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ void forEachOrdered(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream map(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP.convert(doubleUnaryOperator)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ IntStream mapToInt(DoubleToIntFunction doubleToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP.convert(doubleToIntFunction)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ LongStream mapToLong(DoubleToLongFunction doubleToLongFunction) {
        return C$r8$wrapper$java$util$stream$LongStream$WRP.convert(this.wrappedValue.mapToLong(C$r8$wrapper$java$util$function$DoubleToLongFunction$VWRP.convert(doubleToLongFunction)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ Stream mapToObj(DoubleFunction doubleFunction) {
        return C$r8$wrapper$java$util$stream$Stream$WRP.convert(this.wrappedValue.mapToObj(C$r8$wrapper$java$util$function$DoubleFunction$VWRP.convert(doubleFunction)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble max() {
        return OptionalConversions.convert(this.wrappedValue.max());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble min() {
        return OptionalConversions.convert(this.wrappedValue.min());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ boolean noneMatch(DoublePredicate doublePredicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate));
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [java.util.stream.BaseStream, java.util.stream.DoubleStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ DoubleStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.onClose(runnable));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream peek(DoubleConsumer doubleConsumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ double reduce(double d, DoubleBinaryOperator doubleBinaryOperator) {
        return this.wrappedValue.reduce(d, C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP.convert(doubleBinaryOperator));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ OptionalDouble reduce(DoubleBinaryOperator doubleBinaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$DoubleBinaryOperator$VWRP.convert(doubleBinaryOperator)));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleStream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ double sum() {
        return this.wrappedValue.sum();
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ DoubleSummaryStatistics summaryStatistics() {
        return DoubleSummaryStatisticsConversions.convert(this.wrappedValue.summaryStatistics());
    }

    @Override // java.util.stream.DoubleStream
    public /* synthetic */ double[] toArray() {
        return this.wrappedValue.toArray();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.BaseStream, java.util.stream.DoubleStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ DoubleStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.unordered());
    }
}
