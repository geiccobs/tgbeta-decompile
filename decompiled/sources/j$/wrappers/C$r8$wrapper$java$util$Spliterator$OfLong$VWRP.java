package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.LongConsumer;
import java.util.Comparator;
import java.util.Spliterator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$OfLong$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$OfLong$VWRP implements Spliterator.OfLong {
    final /* synthetic */ Spliterator.OfLong wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$OfLong$VWRP(Spliterator.OfLong ofLong) {
        this.wrappedValue = ofLong;
    }

    public static /* synthetic */ Spliterator.OfLong convert(Spliterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof C$r8$wrapper$java$util$Spliterator$OfLong$WRP ? ((C$r8$wrapper$java$util$Spliterator$OfLong$WRP) ofLong).wrappedValue : new C$r8$wrapper$java$util$Spliterator$OfLong$VWRP(ofLong);
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ int characteristics() {
        return this.wrappedValue.characteristics();
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ long estimateSize() {
        return this.wrappedValue.estimateSize();
    }

    @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.Spliterator.OfLong
    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer));
    }

    @Override // j$.util.Spliterator.OfPrimitive
    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.wrappedValue.forEachRemaining((Spliterator.OfLong) longConsumer);
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

    @Override // j$.util.Spliterator.OfLong, j$.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.Spliterator.OfLong
    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer));
    }

    @Override // j$.util.Spliterator.OfPrimitive
    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.wrappedValue.tryAdvance((Spliterator.OfLong) longConsumer);
    }
}
