package j$.wrappers;

import java.util.function.Consumer;
/* renamed from: j$.wrappers.x */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0232x implements Consumer {
    final /* synthetic */ j$.util.function.Consumer a;

    private /* synthetic */ C0232x(j$.util.function.Consumer consumer) {
        this.a = consumer;
    }

    public static /* synthetic */ Consumer a(j$.util.function.Consumer consumer) {
        if (consumer == null) {
            return null;
        }
        return consumer instanceof C0231w ? ((C0231w) consumer).a : new C0232x(consumer);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ void accept(Object obj) {
        this.a.accept(obj);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return a(this.a.andThen(C0231w.b(consumer)));
    }
}
