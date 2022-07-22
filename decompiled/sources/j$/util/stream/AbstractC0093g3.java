package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.g3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0093g3 implements AbstractC0117k3 {
    protected final AbstractC0129m3 a;

    public AbstractC0093g3(AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        this.a = abstractC0129m3;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(double d) {
        AbstractC0139o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0129m3, j$.util.stream.AbstractC0123l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0139o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        AbstractC0139o1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void m() {
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0129m3
    public boolean o() {
        return this.a.o();
    }
}
