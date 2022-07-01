package j$.util.stream;

import j$.util.C0040g;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.t */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0159t implements BiConsumer {
    public static final /* synthetic */ C0159t a = new C0159t();

    private /* synthetic */ C0159t() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((C0040g) obj).b((C0040g) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
