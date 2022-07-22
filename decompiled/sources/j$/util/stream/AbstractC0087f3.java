package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.f3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0087f3 implements AbstractC0111j3 {
    protected final AbstractC0129m3 a;

    public AbstractC0087f3(AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        this.a = abstractC0129m3;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(int i) {
        AbstractC0139o1.d(this);
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
    public /* synthetic */ void accept(Double d) {
        AbstractC0139o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
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
