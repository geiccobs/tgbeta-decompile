package j$.wrappers;

import java.util.function.DoubleBinaryOperator;
/* renamed from: j$.wrappers.z */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0235z implements DoubleBinaryOperator {
    final /* synthetic */ j$.util.function.d a;

    private /* synthetic */ C0235z(j$.util.function.d dVar) {
        this.a = dVar;
    }

    public static /* synthetic */ DoubleBinaryOperator a(j$.util.function.d dVar) {
        if (dVar == null) {
            return null;
        }
        return dVar instanceof C0234y ? ((C0234y) dVar).a : new C0235z(dVar);
    }

    @Override // java.util.function.DoubleBinaryOperator
    public /* synthetic */ double applyAsDouble(double d, double d2) {
        return this.a.applyAsDouble(d, d2);
    }
}
