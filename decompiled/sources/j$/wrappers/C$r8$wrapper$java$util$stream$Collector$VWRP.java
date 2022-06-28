package j$.wrappers;

import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.function.Supplier;
import j$.util.stream.Collector;
import java.util.Set;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$Collector$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$Collector$VWRP implements Collector {
    final /* synthetic */ java.util.stream.Collector wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$Collector$VWRP(java.util.stream.Collector collector) {
        this.wrappedValue = collector;
    }

    public static /* synthetic */ Collector convert(java.util.stream.Collector collector) {
        if (collector == null) {
            return null;
        }
        return collector instanceof C$r8$wrapper$java$util$stream$Collector$WRP ? ((C$r8$wrapper$java$util$stream$Collector$WRP) collector).wrappedValue : new C$r8$wrapper$java$util$stream$Collector$VWRP(collector);
    }

    @Override // j$.util.stream.Collector
    public /* synthetic */ BiConsumer accumulator() {
        return C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(this.wrappedValue.accumulator());
    }

    @Override // j$.util.stream.Collector
    public /* synthetic */ Set characteristics() {
        return this.wrappedValue.characteristics();
    }

    @Override // j$.util.stream.Collector
    public /* synthetic */ BinaryOperator combiner() {
        return C$r8$wrapper$java$util$function$BinaryOperator$VWRP.convert(this.wrappedValue.combiner());
    }

    @Override // j$.util.stream.Collector
    public /* synthetic */ Function finisher() {
        return C$r8$wrapper$java$util$function$Function$VWRP.convert(this.wrappedValue.finisher());
    }

    @Override // j$.util.stream.Collector
    public /* synthetic */ Supplier supplier() {
        return C$r8$wrapper$java$util$function$Supplier$VWRP.convert(this.wrappedValue.supplier());
    }
}
