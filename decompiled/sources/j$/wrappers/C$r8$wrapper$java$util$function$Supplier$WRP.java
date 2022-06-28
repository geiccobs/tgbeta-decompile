package j$.wrappers;

import java.util.function.Supplier;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$function$Supplier$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$function$Supplier$WRP implements Supplier {
    final /* synthetic */ j$.util.function.Supplier wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$function$Supplier$WRP(j$.util.function.Supplier supplier) {
        this.wrappedValue = supplier;
    }

    public static /* synthetic */ Supplier convert(j$.util.function.Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier instanceof C$r8$wrapper$java$util$function$Supplier$VWRP ? ((C$r8$wrapper$java$util$function$Supplier$VWRP) supplier).wrappedValue : new C$r8$wrapper$java$util$function$Supplier$WRP(supplier);
    }

    @Override // java.util.function.Supplier
    public /* synthetic */ Object get() {
        return this.wrappedValue.get();
    }
}
