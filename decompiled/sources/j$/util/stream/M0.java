package j$.util.stream;

import j$.util.C0047i;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class M0 implements BiConsumer {
    public static final /* synthetic */ M0 a = new M0();

    private /* synthetic */ M0() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((C0047i) obj).b((C0047i) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
