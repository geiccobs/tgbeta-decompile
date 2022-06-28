package j$.wrappers;

import java.util.function.DoublePredicate;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoublePredicate$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoublePredicate$WRP implements DoublePredicate {
    final /* synthetic */ j$.util.function.DoublePredicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoublePredicate$WRP(j$.util.function.DoublePredicate doublePredicate) {
        this.wrappedValue = doublePredicate;
    }

    public static /* synthetic */ DoublePredicate convert(j$.util.function.DoublePredicate doublePredicate) {
        if (doublePredicate == null) {
            return null;
        }
        return doublePredicate instanceof C$r8$wrapper$java$util$function$DoublePredicate$VWRP ? ((C$r8$wrapper$java$util$function$DoublePredicate$VWRP) doublePredicate).wrappedValue : new C$r8$wrapper$java$util$function$DoublePredicate$WRP(doublePredicate);
    }

    @Override // java.util.function.DoublePredicate
    public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate)));
    }

    @Override // java.util.function.DoublePredicate
    public /* synthetic */ DoublePredicate negate() {
        return convert(this.wrappedValue.negate());
    }

    @Override // java.util.function.DoublePredicate
    public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$DoublePredicate$VWRP.convert(doublePredicate)));
    }

    @Override // java.util.function.DoublePredicate
    public /* synthetic */ boolean test(double d) {
        return this.wrappedValue.test(d);
    }
}
