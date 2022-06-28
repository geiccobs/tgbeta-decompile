package j$.wrappers;

import j$.util.function.DoubleToIntFunction;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleToIntFunction$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP implements DoubleToIntFunction {
    final /* synthetic */ java.util.function.DoubleToIntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP(java.util.function.DoubleToIntFunction doubleToIntFunction) {
        this.wrappedValue = doubleToIntFunction;
    }

    public static /* synthetic */ DoubleToIntFunction convert(java.util.function.DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return doubleToIntFunction instanceof C$r8$wrapper$java$util$function$DoubleToIntFunction$WRP ? ((C$r8$wrapper$java$util$function$DoubleToIntFunction$WRP) doubleToIntFunction).wrappedValue : new C$r8$wrapper$java$util$function$DoubleToIntFunction$VWRP(doubleToIntFunction);
    }

    @Override // j$.util.function.DoubleToIntFunction
    public /* synthetic */ int applyAsInt(double d) {
        return this.wrappedValue.applyAsInt(d);
    }
}
