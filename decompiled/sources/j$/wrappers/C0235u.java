package j$.wrappers;

import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.function.BinaryOperator;
/* renamed from: j$.wrappers.u */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0235u implements j$.util.function.b {
    final /* synthetic */ BinaryOperator a;

    private /* synthetic */ C0235u(BinaryOperator binaryOperator) {
        this.a = binaryOperator;
    }

    public static /* synthetic */ j$.util.function.b a(BinaryOperator binaryOperator) {
        if (binaryOperator == null) {
            return null;
        }
        return binaryOperator instanceof C0236v ? ((C0236v) binaryOperator).a : new C0235u(binaryOperator);
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return C0233s.a(this.a.andThen(N.a(function)));
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ Object apply(Object obj, Object obj2) {
        return this.a.apply(obj, obj2);
    }
}
