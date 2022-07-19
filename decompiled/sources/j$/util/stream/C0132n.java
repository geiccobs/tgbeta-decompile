package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.LinkedHashSet;
/* renamed from: j$.util.stream.n */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0132n implements BiConsumer {
    public static final /* synthetic */ C0132n a = new C0132n();

    private /* synthetic */ C0132n() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).addAll((LinkedHashSet) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
