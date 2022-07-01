package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.b3 */
/* loaded from: classes2.dex */
public class C0058b3 extends AbstractC0076e3 {
    public C0058b3(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractC0060c
    final boolean G0() {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0060c
    public final AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0076e3, j$.util.stream.Stream
    public void e(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
            return;
        }
        consumer.getClass();
        x0(new C0127n0(consumer, true));
    }

    @Override // j$.util.stream.AbstractC0076e3, j$.util.stream.Stream
    public void forEach(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
        } else {
            super.forEach(consumer);
        }
    }
}
