package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.h3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0099h3 implements AbstractC0123l3 {
    protected final AbstractC0129m3 a;

    public AbstractC0099h3(AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        this.a = abstractC0129m3;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(double d) {
        AbstractC0139o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(int i) {
        AbstractC0139o1.d(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractC0139o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
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
