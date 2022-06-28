package j$.wrappers;

import java.util.function.IntToLongFunction;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntToLongFunction$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntToLongFunction$WRP implements IntToLongFunction {
    final /* synthetic */ j$.util.function.IntToLongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntToLongFunction$WRP(j$.util.function.IntToLongFunction intToLongFunction) {
        this.wrappedValue = intToLongFunction;
    }

    public static /* synthetic */ IntToLongFunction convert(j$.util.function.IntToLongFunction intToLongFunction) {
        if (intToLongFunction == null) {
            return null;
        }
        return intToLongFunction instanceof C$r8$wrapper$java$util$function$IntToLongFunction$VWRP ? ((C$r8$wrapper$java$util$function$IntToLongFunction$VWRP) intToLongFunction).wrappedValue : new C$r8$wrapper$java$util$function$IntToLongFunction$WRP(intToLongFunction);
    }

    @Override // java.util.function.IntToLongFunction
    public /* synthetic */ long applyAsLong(int i) {
        return this.wrappedValue.applyAsLong(i);
    }
}
