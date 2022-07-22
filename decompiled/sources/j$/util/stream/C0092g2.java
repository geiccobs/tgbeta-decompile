package j$.util.stream;

import j$.util.AbstractC0038a;
import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.g2 */
/* loaded from: classes2.dex */
final class C0092g2 extends AbstractC0104i2 implements u.a {
    public C0092g2(AbstractC0181w1 abstractC0181w1) {
        super(abstractC0181w1);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0038a.k(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0038a.c(this, consumer);
    }
}
