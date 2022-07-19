package j$.wrappers;

import java.util.function.LongPredicate;
/* renamed from: j$.wrappers.j0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0214j0 {
    final /* synthetic */ LongPredicate a;

    private /* synthetic */ C0214j0(LongPredicate longPredicate) {
        this.a = longPredicate;
    }

    public static /* synthetic */ C0214j0 a(LongPredicate longPredicate) {
        if (longPredicate == null) {
            return null;
        }
        return longPredicate instanceof AbstractC0216k0 ? ((AbstractC0216k0) longPredicate).a : new C0214j0(longPredicate);
    }

    public boolean b(long j) {
        return this.a.test(j);
    }
}
