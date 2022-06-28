package j$.wrappers;

import j$.util.LongSummaryStatisticsConversions;
import j$.util.OptionalConversions;
import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$LongStream$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$LongStream$WRP implements LongStream {
    final /* synthetic */ j$.util.stream.LongStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$LongStream$WRP(j$.util.stream.LongStream longStream) {
        this.wrappedValue = longStream;
    }

    public static /* synthetic */ LongStream convert(j$.util.stream.LongStream longStream) {
        if (longStream == null) {
            return null;
        }
        return longStream instanceof C$r8$wrapper$java$util$stream$LongStream$VWRP ? ((C$r8$wrapper$java$util$stream$LongStream$VWRP) longStream).wrappedValue : new C$r8$wrapper$java$util$stream$LongStream$WRP(longStream);
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ boolean allMatch(LongPredicate longPredicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$LongPredicate$VWRP.convert(longPredicate));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ boolean anyMatch(LongPredicate longPredicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$LongPredicate$VWRP.convert(longPredicate));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ DoubleStream asDoubleStream() {
        return C$r8$wrapper$java$util$stream$DoubleStream$WRP.convert(this.wrappedValue.asDoubleStream());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalDouble average() {
        return OptionalConversions.convert(this.wrappedValue.average());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ Stream boxed() {
        return C$r8$wrapper$java$util$stream$Stream$WRP.convert(this.wrappedValue.boxed());
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ Object collect(Supplier supplier, ObjLongConsumer objLongConsumer, BiConsumer biConsumer) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$VWRP.convert(supplier), C$r8$wrapper$java$util$function$ObjLongConsumer$VWRP.convert(objLongConsumer), C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream filter(LongPredicate longPredicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$LongPredicate$VWRP.convert(longPredicate)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream flatMap(LongFunction longFunction) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$LongFunction$VWRP.convert(longFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ void forEach(LongConsumer longConsumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$LongConsumer$VWRP.convert(longConsumer));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ void forEachOrdered(LongConsumer longConsumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$LongConsumer$VWRP.convert(longConsumer));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream map(LongUnaryOperator longUnaryOperator) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP.convert(longUnaryOperator)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ DoubleStream mapToDouble(LongToDoubleFunction longToDoubleFunction) {
        return C$r8$wrapper$java$util$stream$DoubleStream$WRP.convert(this.wrappedValue.mapToDouble(C$r8$wrapper$java$util$function$LongToDoubleFunction$VWRP.convert(longToDoubleFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ IntStream mapToInt(LongToIntFunction longToIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$WRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$LongToIntFunction$VWRP.convert(longToIntFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ Stream mapToObj(LongFunction longFunction) {
        return C$r8$wrapper$java$util$stream$Stream$WRP.convert(this.wrappedValue.mapToObj(C$r8$wrapper$java$util$function$LongFunction$VWRP.convert(longFunction)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong max() {
        return OptionalConversions.convert(this.wrappedValue.max());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong min() {
        return OptionalConversions.convert(this.wrappedValue.min());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ boolean noneMatch(LongPredicate longPredicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$LongPredicate$VWRP.convert(longPredicate));
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [java.util.stream.LongStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ LongStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.onClose(runnable));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream peek(LongConsumer longConsumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$LongConsumer$VWRP.convert(longConsumer)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long reduce(long j, LongBinaryOperator longBinaryOperator) {
        return this.wrappedValue.reduce(j, C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP.convert(longBinaryOperator));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ OptionalLong reduce(LongBinaryOperator longBinaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP.convert(longBinaryOperator)));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongStream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long sum() {
        return this.wrappedValue.sum();
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ LongSummaryStatistics summaryStatistics() {
        return LongSummaryStatisticsConversions.convert(this.wrappedValue.summaryStatistics());
    }

    @Override // java.util.stream.LongStream
    public /* synthetic */ long[] toArray() {
        return this.wrappedValue.toArray();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.util.stream.LongStream, java.util.stream.BaseStream] */
    @Override // java.util.stream.BaseStream
    public /* synthetic */ LongStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$WRP.convert(this.wrappedValue.unordered());
    }
}
