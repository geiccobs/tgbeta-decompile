package j$.wrappers;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$Collector$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$Collector$WRP implements Collector {
    final /* synthetic */ j$.util.stream.Collector wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$Collector$WRP(j$.util.stream.Collector collector) {
        this.wrappedValue = collector;
    }

    public static /* synthetic */ Collector convert(j$.util.stream.Collector collector) {
        if (collector == null) {
            return null;
        }
        return collector instanceof C$r8$wrapper$java$util$stream$Collector$VWRP ? ((C$r8$wrapper$java$util$stream$Collector$VWRP) collector).wrappedValue : new C$r8$wrapper$java$util$stream$Collector$WRP(collector);
    }

    @Override // java.util.stream.Collector
    public /* synthetic */ BiConsumer accumulator() {
        return C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(this.wrappedValue.accumulator());
    }

    @Override // java.util.stream.Collector
    public /* synthetic */ Set characteristics() {
        return this.wrappedValue.characteristics();
    }

    @Override // java.util.stream.Collector
    public /* synthetic */ BinaryOperator combiner() {
        return C$r8$wrapper$java$util$function$BinaryOperator$WRP.convert(this.wrappedValue.combiner());
    }

    @Override // java.util.stream.Collector
    public /* synthetic */ Function finisher() {
        return C$r8$wrapper$java$util$function$Function$WRP.convert(this.wrappedValue.finisher());
    }

    @Override // java.util.stream.Collector
    public /* synthetic */ Supplier supplier() {
        return C$r8$wrapper$java$util$function$Supplier$WRP.convert(this.wrappedValue.supplier());
    }
}
