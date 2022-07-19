package j$.wrappers;

import java.util.function.DoubleBinaryOperator;
/* renamed from: j$.wrappers.y */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0239y implements j$.util.function.d {
    final /* synthetic */ DoubleBinaryOperator a;

    private /* synthetic */ C0239y(DoubleBinaryOperator doubleBinaryOperator) {
        this.a = doubleBinaryOperator;
    }

    public static /* synthetic */ j$.util.function.d a(DoubleBinaryOperator doubleBinaryOperator) {
        if (doubleBinaryOperator == null) {
            return null;
        }
        return doubleBinaryOperator instanceof C0240z ? ((C0240z) doubleBinaryOperator).a : new C0239y(doubleBinaryOperator);
    }

    @Override // j$.util.function.d
    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
