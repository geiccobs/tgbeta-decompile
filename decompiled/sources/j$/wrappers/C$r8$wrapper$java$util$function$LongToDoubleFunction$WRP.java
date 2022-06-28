package j$.wrappers;

import java.util.function.LongToDoubleFunction;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongToDoubleFunction$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongToDoubleFunction$WRP implements LongToDoubleFunction {
    final /* synthetic */ j$.util.function.LongToDoubleFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongToDoubleFunction$WRP(j$.util.function.LongToDoubleFunction longToDoubleFunction) {
        this.wrappedValue = longToDoubleFunction;
    }

    public static /* synthetic */ LongToDoubleFunction convert(j$.util.function.LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof C$r8$wrapper$java$util$function$LongToDoubleFunction$VWRP ? ((C$r8$wrapper$java$util$function$LongToDoubleFunction$VWRP) longToDoubleFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongToDoubleFunction$WRP(longToDoubleFunction);
    }

    @Override // java.util.function.LongToDoubleFunction
    public /* synthetic */ double applyAsDouble(long j) {
        return this.wrappedValue.applyAsDouble(j);
    }
}
