package j$.wrappers;

import java.util.function.IntConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$IntConsumer$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$IntConsumer$WRP implements IntConsumer {
    final /* synthetic */ j$.util.function.IntConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$IntConsumer$WRP(j$.util.function.IntConsumer intConsumer) {
        this.wrappedValue = intConsumer;
    }

    public static /* synthetic */ IntConsumer convert(j$.util.function.IntConsumer intConsumer) {
        if (intConsumer == null) {
            return null;
        }
        return intConsumer instanceof C$r8$wrapper$java$util$function$IntConsumer$VWRP ? ((C$r8$wrapper$java$util$function$IntConsumer$VWRP) intConsumer).wrappedValue : new C$r8$wrapper$java$util$function$IntConsumer$WRP(intConsumer);
    }

    @Override // java.util.function.IntConsumer
    public /* synthetic */ void accept(int i) {
        this.wrappedValue.accept(i);
    }

    @Override // java.util.function.IntConsumer
    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$IntConsumer$VWRP.convert(intConsumer)));
    }
}