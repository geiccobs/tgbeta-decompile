package j$.wrappers;

import java.util.function.ObjLongConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ObjLongConsumer$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ObjLongConsumer$WRP implements ObjLongConsumer {
    final /* synthetic */ j$.util.function.ObjLongConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ObjLongConsumer$WRP(j$.util.function.ObjLongConsumer objLongConsumer) {
        this.wrappedValue = objLongConsumer;
    }

    public static /* synthetic */ ObjLongConsumer convert(j$.util.function.ObjLongConsumer objLongConsumer) {
        if (objLongConsumer == null) {
            return null;
        }
        return objLongConsumer instanceof C$r8$wrapper$java$util$function$ObjLongConsumer$VWRP ? ((C$r8$wrapper$java$util$function$ObjLongConsumer$VWRP) objLongConsumer).wrappedValue : new C$r8$wrapper$java$util$function$ObjLongConsumer$WRP(objLongConsumer);
    }

    @Override // java.util.function.ObjLongConsumer
    public /* synthetic */ void accept(Object obj, long j) {
        this.wrappedValue.accept(obj, j);
    }
}
