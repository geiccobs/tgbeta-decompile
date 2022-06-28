package j$.wrappers;

import java.util.function.DoubleUnaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleUnaryOperator$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP implements DoubleUnaryOperator {
    final /* synthetic */ j$.util.function.DoubleUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP(j$.util.function.DoubleUnaryOperator doubleUnaryOperator) {
        this.wrappedValue = doubleUnaryOperator;
    }

    public static /* synthetic */ DoubleUnaryOperator convert(j$.util.function.DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP) doubleUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP(doubleUnaryOperator);
    }

    @Override // java.util.function.DoubleUnaryOperator
    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP.convert(doubleUnaryOperator)));
    }

    @Override // java.util.function.DoubleUnaryOperator
    public /* synthetic */ double applyAsDouble(double d) {
        return this.wrappedValue.applyAsDouble(d);
    }

    @Override // java.util.function.DoubleUnaryOperator
    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP.convert(doubleUnaryOperator)));
    }
}
