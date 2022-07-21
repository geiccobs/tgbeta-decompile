package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.LinkedHashSet;
/* renamed from: j$.util.stream.m */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0126m implements BiConsumer {
    public static final /* synthetic */ C0126m a = new C0126m();

    private /* synthetic */ C0126m() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).add(obj2);
    }

    @Override // j$.util.function.BiConsumer
    public BiConsumer b(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new j$.util.concurrent.a(this, biConsumer);
    }
}
