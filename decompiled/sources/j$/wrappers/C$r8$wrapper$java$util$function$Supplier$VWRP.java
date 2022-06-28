package j$.wrappers;

import j$.util.function.Supplier;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$Supplier$-V-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$Supplier$VWRP implements Supplier {
    final /* synthetic */ java.util.function.Supplier wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$Supplier$VWRP(java.util.function.Supplier supplier) {
        this.wrappedValue = supplier;
    }

    public static /* synthetic */ Supplier convert(java.util.function.Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof C$r8$wrapper$java$util$function$Supplier$WRP ? ((C$r8$wrapper$java$util$function$Supplier$WRP) supplier).wrappedValue : new C$r8$wrapper$java$util$function$Supplier$VWRP(supplier);
    }

    @Override // j$.util.function.Supplier
    public /* synthetic */ Object get() {
        return this.wrappedValue.get();
    }
}
