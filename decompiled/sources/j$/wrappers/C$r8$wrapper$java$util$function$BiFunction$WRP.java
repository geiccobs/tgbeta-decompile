package j$.wrappers;

import java.util.function.BiFunction;
import java.util.function.Function;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BiFunction$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BiFunction$WRP implements BiFunction {
    final /* synthetic */ j$.util.function.BiFunction wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BiFunction$WRP(j$.util.function.BiFunction biFunction) {
        this.wrappedValue = biFunction;
    }

    public static /* synthetic */ BiFunction convert(j$.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof C$r8$wrapper$java$util$function$BiFunction$VWRP ? ((C$r8$wrapper$java$util$function$BiFunction$VWRP) biFunction).wrappedValue : new C$r8$wrapper$java$util$function$BiFunction$WRP(biFunction);
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.wrappedValue.apply(obj, obj2);
    }
}
