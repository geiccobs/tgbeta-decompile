package j$.wrappers;

import j$.util.function.LongFunction;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongFunction$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongFunction$VWRP implements LongFunction {
    final /* synthetic */ java.util.function.LongFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongFunction$VWRP(java.util.function.LongFunction longFunction) {
        this.wrappedValue = longFunction;
    }

    public static /* synthetic */ LongFunction convert(java.util.function.LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof C$r8$wrapper$java$util$function$LongFunction$WRP ? ((C$r8$wrapper$java$util$function$LongFunction$WRP) longFunction).wrappedValue : new C$r8$wrapper$java$util$function$LongFunction$VWRP(longFunction);
    }

    @Override // j$.util.function.LongFunction
    public /* synthetic */ Object apply(long j) {
        return this.wrappedValue.apply(j);
    }
}
