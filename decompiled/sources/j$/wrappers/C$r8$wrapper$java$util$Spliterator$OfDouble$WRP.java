package j$.wrappers;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfDouble$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfDouble$WRP implements Spliterator.OfDouble {
    final /* synthetic */ Spliterator.OfDouble wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfDouble$WRP(Spliterator.OfDouble ofDouble) {
        this.wrappedValue = ofDouble;
    }

    public static /* synthetic */ Spliterator.OfDouble convert(Spliterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof C$r8$wrapper$java$util$Spliterator$OfDouble$VWRP ? ((C$r8$wrapper$java$util$Spliterator$OfDouble$VWRP) ofDouble).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfDouble$WRP(ofDouble);
    }

    @Override // java.util.Spliterator
    public /* synthetic */ int characteristics() {
        return this.wrappedValue.characteristics();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ long estimateSize() {
        return this.wrappedValue.estimateSize();
    }

    @Override // java.util.Spliterator.OfPrimitive
    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachRemaining((Spliterator.OfDouble) doubleConsumer);
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    @Override // java.util.Spliterator.OfDouble
    /* renamed from: forEachRemaining */
    public /* synthetic */ void forEachRemaining2(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }

    @Override // java.util.Spliterator
    public /* synthetic */ Comparator getComparator() {
        return this.wrappedValue.getComparator();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ long getExactSizeIfKnown() {
        return this.wrappedValue.getExactSizeIfKnown();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.wrappedValue.hasCharacteristics(i);
    }

    @Override // java.util.Spliterator.OfPrimitive
    public /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.wrappedValue.tryAdvance((Spliterator.OfDouble) doubleConsumer);
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$Consumer$VWRP.convert(consumer));
    }

    @Override // java.util.Spliterator.OfDouble
    /* renamed from: tryAdvance */
    public /* synthetic */ boolean tryAdvance2(DoubleConsumer doubleConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$DoubleConsumer$VWRP.convert(doubleConsumer));
    }
}
