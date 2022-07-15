package j$.util.stream;

import j$.util.AbstractC0034a;
import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.g2 */
/* loaded from: classes2.dex */
final class C0088g2 extends AbstractC0100i2 implements u.a {
    public C0088g2(AbstractC0177w1 abstractC0177w1) {
        super(abstractC0177w1);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0034a.k(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0034a.c(this, consumer);
    }
}
