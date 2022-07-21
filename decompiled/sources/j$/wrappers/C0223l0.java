package j$.wrappers;

import java.util.function.LongToDoubleFunction;
/* renamed from: j$.wrappers.l0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0223l0 {
    final /* synthetic */ LongToDoubleFunction a;

    private /* synthetic */ C0223l0(LongToDoubleFunction longToDoubleFunction) {
        this.a = longToDoubleFunction;
    }

    public static /* synthetic */ C0223l0 b(LongToDoubleFunction longToDoubleFunction) {
        if (longToDoubleFunction == null) {
            return null;
        }
        return longToDoubleFunction instanceof AbstractC0225m0 ? ((AbstractC0225m0) longToDoubleFunction).a : new C0223l0(longToDoubleFunction);
    }

    public double a(long j) {
        return this.a.applyAsDouble(j);
    }
}
