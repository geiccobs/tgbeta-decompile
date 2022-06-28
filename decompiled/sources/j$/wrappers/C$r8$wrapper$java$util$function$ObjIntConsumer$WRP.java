package j$.wrappers;

import java.util.function.ObjIntConsumer;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$ObjIntConsumer$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$ObjIntConsumer$WRP implements ObjIntConsumer {
    final /* synthetic */ j$.util.function.ObjIntConsumer wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$ObjIntConsumer$WRP(j$.util.function.ObjIntConsumer objIntConsumer) {
        this.wrappedValue = objIntConsumer;
    }

    public static /* synthetic */ ObjIntConsumer convert(j$.util.function.ObjIntConsumer objIntConsumer) {
        if (objIntConsumer == null) {
            return null;
        }
        return objIntConsumer instanceof C$r8$wrapper$java$util$function$ObjIntConsumer$VWRP ? ((C$r8$wrapper$java$util$function$ObjIntConsumer$VWRP) objIntConsumer).wrappedValue : new C$r8$wrapper$java$util$function$ObjIntConsumer$WRP(objIntConsumer);
    }

    @Override // java.util.function.ObjIntConsumer
    public /* synthetic */ void accept(Object obj, int i) {
        this.wrappedValue.accept(obj, i);
    }
}
