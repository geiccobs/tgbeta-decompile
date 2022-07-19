package j$.wrappers;

import java.util.function.LongConsumer;
/* renamed from: j$.wrappers.f0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0211f0 implements j$.util.function.q {
    final /* synthetic */ LongConsumer a;

    private /* synthetic */ C0211f0(LongConsumer longConsumer) {
        this.a = longConsumer;
    }

    public static /* synthetic */ j$.util.function.q b(LongConsumer longConsumer) {
        if (longConsumer == null) {
            return null;
        }
        return longConsumer instanceof C0213g0 ? ((C0213g0) longConsumer).a : new C0211f0(longConsumer);
    }

    @Override // j$.util.function.q
    public /* synthetic */ void accept(long j) {
        this.a.accept(j);
    }

    @Override // j$.util.function.q
    public /* synthetic */ j$.util.function.q f(j$.util.function.q qVar) {
        return b(this.a.andThen(C0213g0.a(qVar)));
    }
}
