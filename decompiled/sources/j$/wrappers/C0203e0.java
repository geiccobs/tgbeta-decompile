package j$.wrappers;

import java.util.function.LongBinaryOperator;
/* renamed from: j$.wrappers.e0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0203e0 implements LongBinaryOperator {
    final /* synthetic */ j$.util.function.o a;

    private /* synthetic */ C0203e0(j$.util.function.o oVar) {
        this.a = oVar;
    }

    public static /* synthetic */ LongBinaryOperator a(j$.util.function.o oVar) {
        if (oVar == null) {
            return null;
        }
        return oVar instanceof C0201d0 ? ((C0201d0) oVar).a : new C0203e0(oVar);
    }

    @Override // java.util.function.LongBinaryOperator
    public /* synthetic */ long applyAsLong(long j, long j2) {
        return this.a.applyAsLong(j, j2);
    }
}
