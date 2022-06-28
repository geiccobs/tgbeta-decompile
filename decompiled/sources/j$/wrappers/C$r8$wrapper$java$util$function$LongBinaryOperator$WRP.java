package j$.wrappers;

import java.util.function.LongBinaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongBinaryOperator$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongBinaryOperator$WRP implements LongBinaryOperator {
    final /* synthetic */ j$.util.function.LongBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongBinaryOperator$WRP(j$.util.function.LongBinaryOperator longBinaryOperator) {
        this.wrappedValue = longBinaryOperator;
    }

    public static /* synthetic */ LongBinaryOperator convert(j$.util.function.LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return longBinaryOperator instanceof C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP) longBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$LongBinaryOperator$WRP(longBinaryOperator);
    }

    @Override // java.util.function.LongBinaryOperator
    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.wrappedValue.applyAsLong(j, j2);
    }
}
