package j$.wrappers;

import j$.util.function.LongUnaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongUnaryOperator$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP implements LongUnaryOperator {
    final /* synthetic */ java.util.function.LongUnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP(java.util.function.LongUnaryOperator longUnaryOperator) {
        this.wrappedValue = longUnaryOperator;
    }

    public static /* synthetic */ LongUnaryOperator convert(java.util.function.LongUnaryOperator longUnaryOperator) {
        if (longUnaryOperator == null) {
            return null;
        }
        return longUnaryOperator instanceof C$r8$wrapper$java$util$function$LongUnaryOperator$WRP ? ((C$r8$wrapper$java$util$function$LongUnaryOperator$WRP) longUnaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$LongUnaryOperator$VWRP(longUnaryOperator);
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$LongUnaryOperator$WRP.convert(longUnaryOperator)));
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ long applyAsLong(long j) {
        return this.wrappedValue.applyAsLong(j);
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$LongUnaryOperator$WRP.convert(longUnaryOperator)));
    }
}
