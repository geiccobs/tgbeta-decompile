package j$.wrappers;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BinaryOperator$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BinaryOperator$WRP implements BinaryOperator {
    final /* synthetic */ j$.util.function.BinaryOperator wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BinaryOperator$WRP(j$.util.function.BinaryOperator binaryOperator) {
        this.wrappedValue = binaryOperator;
    }

    public static /* synthetic */ BinaryOperator convert(j$.util.function.BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof C$r8$wrapper$java$util$function$BinaryOperator$VWRP ? ((C$r8$wrapper$java$util$function$BinaryOperator$VWRP) binaryOperator).wrappedValue : new C$r8$wrapper$java$util$function$BinaryOperator$WRP(binaryOperator);
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return C$r8$wrapper$java$util$function$BiFunction$WRP.convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$Function$VWRP.convert(function)));
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.wrappedValue.apply(obj, obj2);
    }
}
