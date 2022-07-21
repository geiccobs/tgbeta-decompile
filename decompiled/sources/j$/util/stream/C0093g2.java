package j$.util.stream;

import j$.util.AbstractC0039a;
import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.g2 */
/* loaded from: classes2.dex */
final class C0093g2 extends AbstractC0105i2 implements u.a {
    public C0093g2(AbstractC0182w1 abstractC0182w1) {
        super(abstractC0182w1);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.k(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.c(this, consumer);
    }
}
