package j$.util.stream;

import j$.util.AbstractC0033a;
import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.g2 */
/* loaded from: classes2.dex */
final class C0087g2 extends AbstractC0099i2 implements u.a {
    public C0087g2(AbstractC0176w1 abstractC0176w1) {
        super(abstractC0176w1);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0033a.k(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0033a.c(this, consumer);
    }
}
