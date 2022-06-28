package j$.wrappers;

import java.util.function.IntUnaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntUnaryOperator$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntUnaryOperator$WRP implements IntUnaryOperator {
    final /* synthetic */ j$.util.function.IntUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntUnaryOperator$WRP(j$.util.function.IntUnaryOperator intUnaryOperator) {
        this.wrappedValue = intUnaryOperator;
    }

    public static /* synthetic */ IntUnaryOperator convert(j$.util.function.IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP) intUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$IntUnaryOperator$WRP(intUnaryOperator);
    }

    @Override // java.util.function.IntUnaryOperator
    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP.convert(intUnaryOperator)));
    }

    @Override // java.util.function.IntUnaryOperator
    public /* synthetic */ int applyAsInt(int i) {
        return this.wrappedValue.applyAsInt(i);
    }

    @Override // java.util.function.IntUnaryOperator
    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$IntUnaryOperator$VWRP.convert(intUnaryOperator)));
    }
}
