package j$.util.stream;

import j$.util.C0045g;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.t */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0164t implements BiConsumer {
    public static final /* synthetic */ C0164t a = new C0164t();

    private /* synthetic */ C0164t() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((C0045g) obj).b((C0045g) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
