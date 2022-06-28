package j$.wrappers;

import j$.util.function.LongBinaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongBinaryOperator$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP implements LongBinaryOperator {
    final /* synthetic */ java.util.function.LongBinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP(java.util.function.LongBinaryOperator longBinaryOperator) {
        this.wrappedValue = longBinaryOperator;
    }

    public static /* synthetic */ LongBinaryOperator convert(java.util.function.LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return longBinaryOperator instanceof C$r8$wrapper$java$util$function$LongBinaryOperator$WRP ? ((C$r8$wrapper$java$util$function$LongBinaryOperator$WRP) longBinaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$LongBinaryOperator$VWRP(longBinaryOperator);
    }

    @Override // j$.util.function.LongBinaryOperator
    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.wrappedValue.applyAsLong(j, j2);
    }
}
