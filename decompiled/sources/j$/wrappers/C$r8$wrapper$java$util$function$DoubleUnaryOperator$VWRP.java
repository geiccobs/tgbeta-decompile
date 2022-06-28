package j$.wrappers;

import j$.util.function.DoubleUnaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleUnaryOperator$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP implements DoubleUnaryOperator {
    final /* synthetic */ java.util.function.DoubleUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP(java.util.function.DoubleUnaryOperator doubleUnaryOperator) {
        this.wrappedValue = doubleUnaryOperator;
    }

    public static /* synthetic */ DoubleUnaryOperator convert(java.util.function.DoubleUnaryOperator doubleUnaryOperator) {
        if (doubleUnaryOperator == null) {
            return null;
        }
        return doubleUnaryOperator instanceof C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP ? ((C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP) doubleUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$DoubleUnaryOperator$VWRP(doubleUnaryOperator);
    }

    @Override // j$.util.function.DoubleUnaryOperator
    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP.convert(doubleUnaryOperator)));
    }

    @Override // j$.util.function.DoubleUnaryOperator
    public /* synthetic */ double applyAsDouble(double d) {
        return this.wrappedValue.applyAsDouble(d);
    }

    @Override // j$.util.function.DoubleUnaryOperator
    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$DoubleUnaryOperator$WRP.convert(doubleUnaryOperator)));
    }
}
