package j$.wrappers;

import j$.util.function.DoublePredicate;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoublePredicate$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoublePredicate$VWRP implements DoublePredicate {
    final /* synthetic */ java.util.function.DoublePredicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoublePredicate$VWRP(java.util.function.DoublePredicate doublePredicate) {
        this.wrappedValue = doublePredicate;
    }

    public static /* synthetic */ DoublePredicate convert(java.util.function.DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof C$r8$wrapper$java$util$function$DoublePredicate$WRP ? ((C$r8$wrapper$java$util$function$DoublePredicate$WRP) doublePredicate).wrappedValue : new C$r8$wrapper$java$util$function$DoublePredicate$VWRP(doublePredicate);
    }

    @Override // j$.util.function.DoublePredicate
    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$DoublePredicate$WRP.convert(doublePredicate)));
    }

    @Override // j$.util.function.DoublePredicate
    public /* synthetic */ DoublePredicate negate() {
        return convert(this.wrappedValue.negate());
    }

    @Override // j$.util.function.DoublePredicate
    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$DoublePredicate$WRP.convert(doublePredicate)));
    }

    @Override // j$.util.function.DoublePredicate
    public /* synthetic */ boolean test(double d) {
        return this.wrappedValue.test(d);
    }
}
