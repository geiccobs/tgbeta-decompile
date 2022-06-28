package j$.wrappers;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$Spliterator$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$Spliterator$VWRP implements Spliterator {
    final /* synthetic */ java.util.Spliterator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$Spliterator$VWRP(java.util.Spliterator spliterator) {
        this.wrappedValue = spliterator;
    }

    public static /* synthetic */ Spliterator convert(java.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof C$r8$wrapper$java$util$Spliterator$WRP ? ((C$r8$wrapper$java$util$Spliterator$WRP) spliterator).wrappedValue : new C$r8$wrapper$java$util$Spliterator$VWRP(spliterator);
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ int characteristics() {
        return this.wrappedValue.characteristics();
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ long estimateSize() {
        return this.wrappedValue.estimateSize();
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.wrappedValue.forEachRemaining(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
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

    @Override // j$.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.wrappedValue.tryAdvance(C$r8$wrapper$java$util$function$Consumer$WRP.convert(consumer));
    }

    @Override // j$.util.Spliterator
    public /* synthetic */ Spliterator trySplit() {
        return convert(this.wrappedValue.trySplit());
    }
}
