package j$.wrappers;

import java.util.function.LongConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$LongConsumer$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$LongConsumer$WRP implements LongConsumer {
    final /* synthetic */ j$.util.function.LongConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$LongConsumer$WRP(j$.util.function.LongConsumer longConsumer) {
        this.wrappedValue = longConsumer;
    }

    public static /* synthetic */ LongConsumer convert(j$.util.function.LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof C$r8$wrapper$java$util$function$LongConsumer$VWRP ? ((C$r8$wrapper$java$util$function$LongConsumer$VWRP) longConsumer).wrappedValue : new C$r8$wrapper$java$util$function$LongConsumer$WRP(longConsumer);
    }

    @Override // java.util.function.LongConsumer
    public /* synthetic */ void accept(long j) {
        this.wrappedValue.accept(j);
    }

    @Override // java.util.function.LongConsumer
    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$LongConsumer$VWRP.convert(longConsumer)));
    }
}
