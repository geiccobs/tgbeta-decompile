package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.function.BinaryOperator;
/* renamed from: j$.wrappers.u */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0230u implements j$.util.function.b {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ C0230u(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ j$.util.function.b a(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof C0231v ? ((C0231v) binaryOperator).a : new C0230u(binaryOperator);
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return C0228s.a(this.a.andThen(N.a(function)));
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
