package j$.wrappers;

import j$.util.PrimitiveIterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfDouble$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$VWRP implements PrimitiveIterator.OfDouble {
    final /* synthetic */ PrimitiveIterator.OfDouble wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$VWRP(PrimitiveIterator.OfDouble ofDouble) {
        this.wrappedValue = ofDouble;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble convert(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$WRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$WRP) ofDouble).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfDouble$VWRP(ofDouble);
    }

    @Override // j$.util.PrimitiveIterator.OfDouble, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.PrimitiveIterator.OfDouble
    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer));
    }

    @Override // j$.util.PrimitiveIterator
    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.wrappedValue.forEachRemaining((PrimitiveIterator.OfDouble) doubleConsumer);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    @Override // j$.util.PrimitiveIterator.OfDouble
    public /* synthetic */ double nextDouble() {
        return this.wrappedValue.nextDouble();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
