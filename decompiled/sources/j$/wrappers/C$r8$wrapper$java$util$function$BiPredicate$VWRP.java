package j$.wrappers;

import j$.util.function.BiPredicate;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BiPredicate$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BiPredicate$VWRP implements BiPredicate {
    final /* synthetic */ java.util.function.BiPredicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BiPredicate$VWRP(java.util.function.BiPredicate biPredicate) {
        this.wrappedValue = biPredicate;
    }

    public static /* synthetic */ BiPredicate convert(java.util.function.BiPredicate biPredicate) {
        if (biPredicate == null) {
            return null;
        }
        return biPredicate instanceof C$r8$wrapper$java$util$function$BiPredicate$WRP ? ((C$r8$wrapper$java$util$function$BiPredicate$WRP) biPredicate).wrappedValue : new C$r8$wrapper$java$util$function$BiPredicate$VWRP(biPredicate);
    }

    @Override // j$.util.function.BiPredicate
    public /* synthetic */ BiPredicate and(BiPredicate biPredicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$BiPredicate$WRP.convert(biPredicate)));
    }

    @Override // j$.util.function.BiPredicate
    public /* synthetic */ BiPredicate negate() {
        return convert(this.wrappedValue.negate());
    }

    @Override // j$.util.function.BiPredicate
    public /* synthetic */ BiPredicate or(BiPredicate biPredicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$BiPredicate$WRP.convert(biPredicate)));
    }

    @Override // j$.util.function.BiPredicate
    public /* synthetic */ boolean test(Object obj, Object obj2) {
        return this.wrappedValue.test(obj, obj2);
    }
}
