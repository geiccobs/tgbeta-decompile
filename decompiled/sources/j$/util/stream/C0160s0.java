package j$.util.stream;

import j$.util.C0046h;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.s0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0160s0 implements BiConsumer {
    public static final /* synthetic */ C0160s0 a = new C0160s0();

    private /* synthetic */ C0160s0() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((C0046h) obj).b((C0046h) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
