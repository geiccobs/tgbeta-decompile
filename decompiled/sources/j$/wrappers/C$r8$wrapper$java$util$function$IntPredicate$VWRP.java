package j$.wrappers;

import j$.util.function.IntPredicate;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntPredicate$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntPredicate$VWRP implements IntPredicate {
    final /* synthetic */ java.util.function.IntPredicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntPredicate$VWRP(java.util.function.IntPredicate intPredicate) {
        this.wrappedValue = intPredicate;
    }

    public static /* synthetic */ IntPredicate convert(java.util.function.IntPredicate intPredicate) {
        if (intPredicate == null) {
            return null;
        }
        return intPredicate instanceof C$r8$wrapper$java$util$function$IntPredicate$WRP ? ((C$r8$wrapper$java$util$function$IntPredicate$WRP) intPredicate).wrappedValue : new C$r8$wrapper$java$util$function$IntPredicate$VWRP(intPredicate);
    }

    @Override // j$.util.function.IntPredicate
    public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$IntPredicate$WRP.convert(intPredicate)));
    }

    @Override // j$.util.function.IntPredicate
    public /* synthetic */ IntPredicate negate() {
        return convert(this.wrappedValue.negate());
    }

    @Override // j$.util.function.IntPredicate
    public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$IntPredicate$WRP.convert(intPredicate)));
    }

    @Override // j$.util.function.IntPredicate
    public /* synthetic */ boolean test(int i) {
        return this.wrappedValue.test(i);
    }
}
