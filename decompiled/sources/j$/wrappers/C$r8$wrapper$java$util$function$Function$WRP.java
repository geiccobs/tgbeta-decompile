package j$.wrappers;

import java.util.function.Function;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$Function$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$Function$WRP implements Function {
    final /* synthetic */ j$.util.function.Function wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$Function$WRP(j$.util.function.Function function) {
        this.wrappedValue = function;
    }

    public static /* synthetic */ Function convert(j$.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof C$r8$wrapper$java$util$function$Function$VWRP ? ((C$r8$wrapper$java$util$function$Function$VWRP) function).wrappedValue : new C$r8$wrapper$java$util$function$Function$WRP(function);
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    @Override // java.util.function.Function
    public /* synthetic */ Object apply(Object obj) {
        return this.wrappedValue.apply(obj);
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }
}