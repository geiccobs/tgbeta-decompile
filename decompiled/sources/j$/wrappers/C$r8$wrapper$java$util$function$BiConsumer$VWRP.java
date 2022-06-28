package j$.wrappers;

import j$.util.function.BiConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$BiConsumer$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$BiConsumer$VWRP implements BiConsumer {
    final /* synthetic */ java.util.function.BiConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$BiConsumer$VWRP(java.util.function.BiConsumer biConsumer) {
        this.wrappedValue = biConsumer;
    }

    public static /* synthetic */ BiConsumer convert(java.util.function.BiConsumer biConsumer) {
        if (biConsumer == null) {
            return null;
        }
        return biConsumer instanceof C$r8$wrapper$java$util$function$BiConsumer$WRP ? ((C$r8$wrapper$java$util$function$BiConsumer$WRP) biConsumer).wrappedValue : new C$r8$wrapper$java$util$function$BiConsumer$VWRP(biConsumer);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ void accept(Object obj, Object obj2) {
        this.wrappedValue.accept(obj, obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return convert(this.wrappedValue.andThen(C$r8$wrapper$java$util$function$BiConsumer$WRP.convert(biConsumer)));
    }
}
