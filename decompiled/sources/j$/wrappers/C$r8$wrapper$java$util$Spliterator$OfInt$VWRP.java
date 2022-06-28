package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.IntConsumer;
import java.util.Comparator;
import java.util.Spliterator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfInt$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfInt$VWRP implements Spliterator.OfInt {
    final /* synthetic */ Spliterator.OfInt wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfInt$VWRP(Spliterator.OfInt ofInt) {
        this.wrappedValue = ofInt;
    }

    public static /* synthetic */ Spliterator.OfInt convert(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C$r8$wrapper$java$util$Spliterator$OfInt$WRP ? ((C$r8$wrapper$java$util$Spliterator$OfInt$WRP) ofInt).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfInt$VWRP(ofInt);
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ int characteristics() {
        return this.wrappedValue.characteristics();
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ long estimateSize() {
        return this.wrappedValue.estimateSize();
    }

    @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.Spliterator.OfInt
    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    @Override // j$.util.Spliterator.OfPrimitive
    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.wrappedValue.forEachRemaining((Spliterator.OfInt) intConsumer);
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ Comparator getComparator() {
        return this.wrappedValue.getComparator();
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ long getExactSizeIfKnown() {
        return this.wrappedValue.getExactSizeIfKnown();
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.wrappedValue.hasCharacteristics(i);
    }

    @Override // j$.util.Spliterator.OfInt, j$.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.Spliterator.OfInt
    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    @Override // j$.util.Spliterator.OfPrimitive
    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.wrappedValue.tryAdvance((Spliterator.OfInt) intConsumer);
    }
}
