package j$.wrappers;

import java.util.function.LongUnaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongUnaryOperator$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongUnaryOperator$WRP implements LongUnaryOperator {
    final /* synthetic */ j$.util.function.LongUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongUnaryOperator$WRP(j$.util.function.LongUnaryOperator longUnaryOperator) {
        this.wrappedValue = longUnaryOperator;
    }

    public static /* synthetic */ LongUnaryOperator convert(j$.util.function.LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP) longUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$LongUnaryOperator$WRP(longUnaryOperator);
    }

    @Override // java.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP.convert(longUnaryOperator)));
    }

    @Override // java.util.function.LongUnaryOperator
    public /* synthetic */ long applyAsLong(long j) {
        return this.wrappedValue.applyAsLong(j);
    }

    @Override // java.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP.convert(longUnaryOperator)));
    }
}
