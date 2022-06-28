package j$.wrappers;

import j$.util.function.LongPredicate;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongPredicate$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongPredicate$VWRP implements LongPredicate {
    final /* synthetic */ java.util.function.LongPredicate wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongPredicate$VWRP(java.util.function.LongPredicate longPredicate) {
        this.wrappedValue = longPredicate;
    }

    public static /* synthetic */ LongPredicate convert(java.util.function.LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof C$r8$wrapper$java$util$function$LongPredicate$WRP ? ((C$r8$wrapper$java$util$function$LongPredicate$WRP) longPredicate).wrappedValue : new C$r8$wrapper$java$util$function$LongPredicate$VWRP(longPredicate);
    }

    @Override // j$.util.function.LongPredicate
    public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
        return convert(this.wrappedValue.and(C$r8$wrapper$java$util$function$LongPredicate$WRP.convert(longPredicate)));
    }

    @Override // j$.util.function.LongPredicate
    public /* synthetic */ LongPredicate negate() {
        return convert(this.wrappedValue.negate());
    }

    @Override // j$.util.function.LongPredicate
    public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
        return convert(this.wrappedValue.or(C$r8$wrapper$java$util$function$LongPredicate$WRP.convert(longPredicate)));
    }

    @Override // j$.util.function.LongPredicate
    public /* synthetic */ boolean test(long j) {
        return this.wrappedValue.test(j);
    }
}
