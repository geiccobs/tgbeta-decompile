package j$.wrappers;

import j$.util.function.DoubleConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$DoubleConsumer$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$DoubleConsumer$VWRP implements DoubleConsumer {
    final /* synthetic */ java.util.function.DoubleConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$DoubleConsumer$VWRP(java.util.function.DoubleConsumer doubleConsumer) {
        this.wrappedValue = doubleConsumer;
    }

    public static /* synthetic */ DoubleConsumer convert(java.util.function.DoubleConsumer doubleConsumer) {
        if (doubleConsumer == null) {
            return null;
        }
        return doubleConsumer instanceof C$r8$wrapper$java$util$function$DoubleConsumer$WRP ? ((C$r8$wrapper$java$util$function$DoubleConsumer$WRP) doubleConsumer).wrappedValue : new C$r8$wrapper$java$util$function$DoubleConsumer$VWRP(doubleConsumer);
    }

    @Override // j$.util.function.DoubleConsumer
    public /* synthetic */ void accept(double d) {
        this.wrappedValue.accept(d);
    }

    @Override // j$.util.function.DoubleConsumer
    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$DoubleConsumer$WRP.convert(doubleConsumer)));
    }
}
