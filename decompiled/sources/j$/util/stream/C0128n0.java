package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.n0 */
/* loaded from: classes2.dex */
final class C0128n0 extends AbstractC0134o0 {
    final Consumer b;

    public C0128n0(Consumer consumer, boolean z) {
        super(z);
        this.b = consumer;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.b.accept(obj);
    }
}
