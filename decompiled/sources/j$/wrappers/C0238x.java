package j$.wrappers;

import java.util.function.Consumer;
/* renamed from: j$.wrappers.x */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0238x implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ C0238x(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof C0237w ? ((C0237w) consumer).a : new C0238x(consumer);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.andThen(C0237w.b(consumer)));
    }
}
