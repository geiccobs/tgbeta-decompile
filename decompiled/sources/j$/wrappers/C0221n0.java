package j$.wrappers;

import java.util.function.LongToIntFunction;
/* renamed from: j$.wrappers.n0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0221n0 {
    final /* synthetic */ LongToIntFunction a;

    private /* synthetic */ C0221n0(LongToIntFunction longToIntFunction) {
        this.a = longToIntFunction;
    }

    public static /* synthetic */ C0221n0 b(LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof AbstractC0223o0 ? ((AbstractC0223o0) longToIntFunction).a : new C0221n0(longToIntFunction);
    }

    public int a(long j) {
        return this.a.applyAsInt(j);
    }
}
