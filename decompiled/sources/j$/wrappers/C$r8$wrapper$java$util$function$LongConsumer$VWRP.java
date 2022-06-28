package j$.wrappers;

import j$.util.function.LongConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongConsumer$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongConsumer$VWRP implements LongConsumer {
    final /* synthetic */ java.util.function.LongConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongConsumer$VWRP(java.util.function.LongConsumer longConsumer) {
        this.wrappedValue = longConsumer;
    }

    public static /* synthetic */ LongConsumer convert(java.util.function.LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof C$r8$wrapper$java$util$function$LongConsumer$WRP ? ((C$r8$wrapper$java$util$function$LongConsumer$WRP) longConsumer).wrappedValue : new C$r8$wrapper$java$util$function$LongConsumer$VWRP(longConsumer);
    }

    @Override // j$.util.function.LongConsumer
    public /* synthetic */ void accept(long j) {
        this.wrappedValue.accept(j);
    }

    @Override // j$.util.function.LongConsumer
    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$LongConsumer$WRP.convert(longConsumer)));
    }
}
