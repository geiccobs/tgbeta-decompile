package j$.wrappers;

import j$.util.PrimitiveIterator;
import j$.util.function.Consumer;
import j$.util.function.IntConsumer;
import java.util.PrimitiveIterator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$PrimitiveIterator$OfInt$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP implements PrimitiveIterator.OfInt {
    final /* synthetic */ PrimitiveIterator.OfInt wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP(PrimitiveIterator.OfInt ofInt) {
        this.wrappedValue = ofInt;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt convert(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP ? ((C$r8$wrapper$java$util$PrimitiveIterator$OfInt$WRP) ofInt).wrappedValue : new C$r8$wrapper$java$util$PrimitiveIterator$OfInt$VWRP(ofInt);
    }

    @Override // j$.util.PrimitiveIterator.OfInt, j$.util.Iterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.PrimitiveIterator.OfInt
    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$IntConsumer$WRP.convert(intConsumer));
    }

    @Override // j$.util.PrimitiveIterator
    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.wrappedValue.forEachRemaining((PrimitiveIterator.OfInt) intConsumer);
    }

    @Override // java.util.Iterator
    public /* synthetic */ boolean hasNext() {
        return this.wrappedValue.hasNext();
    }

    @Override // j$.util.PrimitiveIterator.OfInt
    public /* synthetic */ int nextInt() {
        return this.wrappedValue.nextInt();
    }

    @Override // java.util.Iterator
    public /* synthetic */ void remove() {
        this.wrappedValue.remove();
    }
}
