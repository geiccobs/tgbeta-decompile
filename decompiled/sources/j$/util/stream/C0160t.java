package j$.util.stream;

import j$.util.C0041g;
import j$.util.function.BiConsumer;
/* renamed from: j$.util.stream.t */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0160t implements BiConsumer {
    public static final /* synthetic */ C0160t a = new C0160t();

    private /* synthetic */ C0160t() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((C0041g) obj).b((C0041g) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
