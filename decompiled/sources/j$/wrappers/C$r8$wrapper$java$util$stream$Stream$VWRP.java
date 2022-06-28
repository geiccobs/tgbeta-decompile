package j$.wrappers;

import j$.util.Optional;
import j$.util.OptionalConversions;
import j$.util.Spliterator;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.IntFunction;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.stream.BaseStream;
import j$.util.stream.Collector;
import j$.util.stream.DoubleStream;
import j$.util.stream.IntStream;
import j$.util.stream.LongStream;
import j$.util.stream.Stream;
import java.util.Comparator;
import java.util.Iterator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$Stream$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$Stream$VWRP implements Stream {
    final /* synthetic */ java.util.stream.Stream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$Stream$VWRP(java.util.stream.Stream stream) {
        this.wrappedValue = stream;
    }

    public static /* synthetic */ Stream convert(java.util.stream.Stream stream) {
        if (stream == null) {
            return null;
        }
        return stream instanceof C$r8$wrapper$java$util$stream$Stream$WRP ? ((C$r8$wrapper$java$util$stream$Stream$WRP) stream).wrappedValue : new C$r8$wrapper$java$util$stream$Stream$VWRP(stream);
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ boolean allMatch(Predicate predicate) {
        return this.wrappedValue.allMatch(C$r8$wrapper$java$util$function$Predicate$WRP.convert(predicate));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ boolean anyMatch(Predicate predicate) {
        return this.wrappedValue.anyMatch(C$r8$wrapper$java$util$function$Predicate$WRP.convert(predicate));
    }

    @Override // j$.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Object collect(Supplier supplier, BiConsumer biConsumer, BiConsumer biConsumer2) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$function$Supplier$WRP.convert(supplier), C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(biConsumer), C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(biConsumer2));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Object collect(Collector collector) {
        return this.wrappedValue.collect(C$r8$wrapper$java$util$stream$Collector$WRP.convert(collector));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ long count() {
        return this.wrappedValue.count();
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream distinct() {
        return convert(this.wrappedValue.distinct());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream filter(Predicate predicate) {
        return convert(this.wrappedValue.filter(C$r8$wrapper$java$util$function$Predicate$WRP.convert(predicate)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Optional findAny() {
        return OptionalConversions.convert(this.wrappedValue.findAny());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Optional findFirst() {
        return OptionalConversions.convert(this.wrappedValue.findFirst());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream flatMap(Function function) {
        return convert(this.wrappedValue.flatMap(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ DoubleStream flatMapToDouble(Function function) {
        return C$r8$wrapper$java$util$stream$DoubleStream$VWRP.convert(this.wrappedValue.flatMapToDouble(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ IntStream flatMapToInt(Function function) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.wrappedValue.flatMapToInt(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ LongStream flatMapToLong(Function function) {
        return C$r8$wrapper$java$util$stream$LongStream$VWRP.convert(this.wrappedValue.flatMapToLong(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ void forEach(Consumer consumer) {
        this.wrappedValue.forEach(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ void forEachOrdered(Consumer consumer) {
        this.wrappedValue.forEachOrdered(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    @Override // j$.util.stream.BaseStream, j$.util.stream.DoubleStream
    public /* synthetic */ Iterator iterator() {
        return this.wrappedValue.iterator();
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream limit(long j) {
        return convert(this.wrappedValue.limit(j));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream map(Function function) {
        return convert(this.wrappedValue.map(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ DoubleStream mapToDouble(ToDoubleFunction toDoubleFunction) {
        return C$r8$wrapper$java$util$stream$DoubleStream$VWRP.convert(this.wrappedValue.mapToDouble(C$r8$wrapper$java$util$function$ToDoubleFunction$WRP.convert(toDoubleFunction)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ IntStream mapToInt(ToIntFunction toIntFunction) {
        return C$r8$wrapper$java$util$stream$IntStream$VWRP.convert(this.wrappedValue.mapToInt(C$r8$wrapper$java$util$function$ToIntFunction$WRP.convert(toIntFunction)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ LongStream mapToLong(ToLongFunction toLongFunction) {
        return C$r8$wrapper$java$util$stream$LongStream$VWRP.convert(this.wrappedValue.mapToLong(C$r8$wrapper$java$util$function$ToLongFunction$WRP.convert(toLongFunction)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Optional max(Comparator comparator) {
        return OptionalConversions.convert(this.wrappedValue.max(comparator));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Optional min(Comparator comparator) {
        return OptionalConversions.convert(this.wrappedValue.min(comparator));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ boolean noneMatch(Predicate predicate) {
        return this.wrappedValue.noneMatch(C$r8$wrapper$java$util$function$Predicate$WRP.convert(predicate));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.onClose(runnable));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ BaseStream parallel() {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.parallel());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream peek(Consumer consumer) {
        return convert(this.wrappedValue.peek(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Optional reduce(BinaryOperator binaryOperator) {
        return OptionalConversions.convert(this.wrappedValue.reduce(C$r8$wrapper$java$util$function$BinaryOperator$WRP.convert(binaryOperator)));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Object reduce(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        return this.wrappedValue.reduce(obj, C$r8$wrapper$java$util$function$BiFunction$WRP.convert(biFunction), C$r8$wrapper$java$util$function$BinaryOperator$WRP.convert(binaryOperator));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Object reduce(Object obj, BinaryOperator binaryOperator) {
        return this.wrappedValue.reduce(obj, C$r8$wrapper$java$util$function$BinaryOperator$WRP.convert(binaryOperator));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ BaseStream sequential() {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.sequential());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream skip(long j) {
        return convert(this.wrappedValue.skip(j));
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream sorted() {
        return convert(this.wrappedValue.sorted());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Stream sorted(Comparator comparator) {
        return convert(this.wrappedValue.sorted(comparator));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ Spliterator spliterator() {
        return C$r8$wrapper$java$util$Spliterator$VWRP.convert(this.wrappedValue.spliterator());
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Object[] toArray() {
        return this.wrappedValue.toArray();
    }

    @Override // j$.util.stream.Stream
    public /* synthetic */ Object[] toArray(IntFunction intFunction) {
        return this.wrappedValue.toArray(C$r8$wrapper$java$util$function$IntFunction$WRP.convert(intFunction));
    }

    @Override // j$.util.stream.BaseStream
    public /* synthetic */ BaseStream unordered() {
        return C$r8$wrapper$java$util$stream$BaseStream$VWRP.convert(this.wrappedValue.unordered());
    }
}
