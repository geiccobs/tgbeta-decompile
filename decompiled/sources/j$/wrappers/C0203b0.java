package j$.wrappers;

import java.util.function.IntUnaryOperator;
/* renamed from: j$.wrappers.b0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0203b0 {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ C0203b0(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ C0203b0 b(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof AbstractC0205c0 ? ((AbstractC0205c0) intUnaryOperator).a : new C0203b0(intUnaryOperator);
    }

    public int a(int i) {
        return this.a.applyAsInt(i);
    }
}
