package j$.wrappers;

import j$.util.function.ToIntFunction;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ToIntFunction$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ToIntFunction$VWRP implements ToIntFunction {
    final /* synthetic */ java.util.function.ToIntFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ToIntFunction$VWRP(java.util.function.ToIntFunction toIntFunction) {
        this.wrappedValue = toIntFunction;
    }

    public static /* synthetic */ ToIntFunction convert(java.util.function.ToIntFunction toIntFunction) {
        if (toIntFunction == null) {
            return null;
        }
        return toIntFunction instanceof C$r8$wrapper$java$util$function$ToIntFunction$WRP ? ((C$r8$wrapper$java$util$function$ToIntFunction$WRP) toIntFunction).wrappedValue : new C$r8$wrapper$java$util$function$ToIntFunction$VWRP(toIntFunction);
    }

    @Override // j$.util.function.ToIntFunction
    public /* synthetic */ int applyAsInt(Object obj) {
        return this.wrappedValue.applyAsInt(obj);
    }
}
