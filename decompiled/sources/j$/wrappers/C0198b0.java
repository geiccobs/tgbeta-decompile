package j$.wrappers;

import java.util.function.IntUnaryOperator;
/* renamed from: j$.wrappers.b0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0198b0 {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ C0198b0(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ C0198b0 b(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof AbstractC0200c0 ? ((AbstractC0200c0) intUnaryOperator).a : new C0198b0(intUnaryOperator);
    }

    public int a(int i) {
        return this.a.applyAsInt(i);
    }
}
