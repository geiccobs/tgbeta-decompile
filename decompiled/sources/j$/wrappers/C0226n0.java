package j$.wrappers;

import java.util.function.LongToIntFunction;
/* renamed from: j$.wrappers.n0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0226n0 {
    final /* synthetic */ LongToIntFunction a;

    private /* synthetic */ C0226n0(LongToIntFunction longToIntFunction) {
        this.a = longToIntFunction;
    }

    public static /* synthetic */ C0226n0 b(LongToIntFunction longToIntFunction) {
        if (longToIntFunction == null) {
            return null;
        }
        return longToIntFunction instanceof AbstractC0228o0 ? ((AbstractC0228o0) longToIntFunction).a : new C0226n0(longToIntFunction);
    }

    public int a(long j) {
        return this.a.applyAsInt(j);
    }
}
