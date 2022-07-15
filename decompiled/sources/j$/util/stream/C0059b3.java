package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.b3 */
/* loaded from: classes2.dex */
public class C0059b3 extends AbstractC0077e3 {
    public C0059b3(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractC0061c
    final boolean G0() {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0061c
    public final AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0077e3, j$.util.stream.Stream
    public void e(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
            return;
        }
        consumer.getClass();
        x0(new C0128n0(consumer, true));
    }

    @Override // j$.util.stream.AbstractC0077e3, j$.util.stream.Stream
    public void forEach(Consumer consumer) {
        if (!isParallel()) {
            J0().forEachRemaining(consumer);
        } else {
            super.forEach(consumer);
        }
    }
}
