package j$.wrappers;

import j$.util.function.IntToLongFunction;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntToLongFunction$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntToLongFunction$VWRP implements IntToLongFunction {
    final /* synthetic */ java.util.function.IntToLongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntToLongFunction$VWRP(java.util.function.IntToLongFunction intToLongFunction) {
        this.wrappedValue = intToLongFunction;
    }

    public static /* synthetic */ IntToLongFunction convert(java.util.function.IntToLongFunction intToLongFunction) {
        if (intToLongFunction == null) {
            return null;
        }
        return intToLongFunction instanceof C$r8$wrapper$java$util$function$IntToLongFunction$WRP ? ((C$r8$wrapper$java$util$function$IntToLongFunction$WRP) intToLongFunction).wrappedValue : new C$r8$wrapper$java$util$function$IntToLongFunction$VWRP(intToLongFunction);
    }

    @Override // j$.util.function.IntToLongFunction
    public /* synthetic */ long applyAsLong(int i) {
        return this.wrappedValue.applyAsLong(i);
    }
}
