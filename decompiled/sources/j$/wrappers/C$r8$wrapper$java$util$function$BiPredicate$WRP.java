package j$.wrappers;

import java.util.function.BiPredicate;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BiPredicate$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BiPredicate$WRP implements BiPredicate {
    final /* synthetic */ j$.util.function.BiPredicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BiPredicate$WRP(j$.util.function.BiPredicate biPredicate) {
        this.wrappedValue = biPredicate;
    }

    public static /* synthetic */ BiPredicate convert(j$.util.function.BiPredicate biPredicate) {
        if (biPredicate == null) {
            return null;
        }
        return biPredicate instanceof C$r8$wrapper$java$util$function$BiPredicate$VWRP ? ((C$r8$wrapper$java$util$function$BiPredicate$VWRP) biPredicate).wrappedValue : new C$r8$wrapper$java$util$function$BiPredicate$WRP(biPredicate);
    }

    @Override // java.util.function.BiPredicate
    public /* synthetic */ BiPredicate and(BiPredicate biPredicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$BiPredicate$VWRP.convert(biPredicate)));
    }

    @Override // java.util.function.BiPredicate
    public /* synthetic */ BiPredicate negate() {
        return convert(this.wrappedValue.negate());
    }

    @Override // java.util.function.BiPredicate
    public /* synthetic */ BiPredicate or(BiPredicate biPredicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$BiPredicate$VWRP.convert(biPredicate)));
    }

    @Override // java.util.function.BiPredicate
    public /* synthetic */ boolean test(Object obj, Object obj2) {
        return this.wrappedValue.test(obj, obj2);
    }
}
