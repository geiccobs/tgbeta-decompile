package j$.wrappers;

import java.util.function.BiFunction;
import java.util.function.Function;
/* renamed from: j$.wrappers.t */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0233t implements BiFunction {
    final /* synthetic */ j$.util.function.BiFunction a;

    private /* synthetic */ C0233t(j$.util.function.BiFunction biFunction) {
        this.a = biFunction;
    }

    public static /* synthetic */ BiFunction a(j$.util.function.BiFunction biFunction) {
        if (biFunction == null) {
            return null;
        }
        return biFunction instanceof C0232s ? ((C0232s) biFunction).a : new C0233t(biFunction);
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return a(this.a.andThen(M.a(function)));
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
