package j$.wrappers;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
/* renamed from: j$.wrappers.v */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0230v implements BinaryOperator {
    final /* synthetic */ j$.util.function.b a;

    private /* synthetic */ C0230v(j$.util.function.b bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ BinaryOperator a(j$.util.function.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof C0229u ? ((C0229u) bVar).a : new C0230v(bVar);
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return C0228t.a(this.a.andThen(M.a(function)));
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
