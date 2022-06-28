package j$.wrappers;

import j$.util.function.Function;
import j$.util.function.UnaryOperator;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$UnaryOperator$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$UnaryOperator$VWRP implements UnaryOperator {
    final /* synthetic */ java.util.function.UnaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$UnaryOperator$VWRP(java.util.function.UnaryOperator unaryOperator) {
        this.wrappedValue = unaryOperator;
    }

    public static /* synthetic */ UnaryOperator convert(java.util.function.UnaryOperator unaryOperator) {
        if (unaryOperator == null) {
            return null;
        }
        return unaryOperator instanceof C$r8$wrapper$java$util$function$UnaryOperator$WRP ? ((C$r8$wrapper$java$util$function$UnaryOperator$WRP) unaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$UnaryOperator$VWRP(unaryOperator);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return C$r8$wrapper$java$util$function$Function$VWRP.convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Object apply(Object obj) {
        return this.wrappedValue.apply(obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return C$r8$wrapper$java$util$function$Function$VWRP.convert(this.wrappedValue.compose(C$r8$wrapper$java$util$function$Function$WRP.convert(function)));
    }
}
