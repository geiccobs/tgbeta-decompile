package j$.util.stream;

import j$.util.C0046g;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.t */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0165t implements BiConsumer {
    public static final /* synthetic */ C0165t a = new C0165t();

    private /* synthetic */ C0165t() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((C0046g) obj).b((C0046g) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
