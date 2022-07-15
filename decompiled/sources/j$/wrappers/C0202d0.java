package j$.wrappers;

import java.util.function.LongBinaryOperator;
/* renamed from: j$.wrappers.d0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0202d0 implements j$.util.function.o {
    final /* synthetic */ LongBinaryOperator a;

    private /* synthetic */ C0202d0(LongBinaryOperator longBinaryOperator) {
        this.a = longBinaryOperator;
    }

    public static /* synthetic */ j$.util.function.o a(LongBinaryOperator longBinaryOperator) {
        if (longBinaryOperator == null) {
            return null;
        }
        return longBinaryOperator instanceof C0204e0 ? ((C0204e0) longBinaryOperator).a : new C0202d0(longBinaryOperator);
    }

    @Override // j$.util.function.o
    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
