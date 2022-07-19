package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.f3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0083f3 implements AbstractC0107j3 {
    protected final AbstractC0125m3 a;

    public AbstractC0083f3(AbstractC0125m3 abstractC0125m3) {
        abstractC0125m3.getClass();
        this.a = abstractC0125m3;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void accept(int i) {
        AbstractC0135o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0125m3, j$.util.stream.AbstractC0119l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0135o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        AbstractC0135o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void m() {
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0125m3
    public boolean o() {
        return this.a.o();
    }
}
