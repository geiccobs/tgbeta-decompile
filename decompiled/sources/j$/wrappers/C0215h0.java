package j$.wrappers;

import java.util.function.LongFunction;
/* renamed from: j$.wrappers.h0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0215h0 implements j$.util.function.r {
    final /* synthetic */ LongFunction a;

    private /* synthetic */ C0215h0(LongFunction longFunction) {
        this.a = longFunction;
    }

    public static /* synthetic */ j$.util.function.r a(LongFunction longFunction) {
        if (longFunction == null) {
            return null;
        }
        return longFunction instanceof C0217i0 ? ((C0217i0) longFunction).a : new C0215h0(longFunction);
    }

    @Override // j$.util.function.r
    public /* synthetic */ Object apply(long j) {
        return this.a.apply(j);
    }
}
