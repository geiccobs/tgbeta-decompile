package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.f3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0088f3 implements AbstractC0112j3 {
    protected final AbstractC0130m3 a;

    public AbstractC0088f3(AbstractC0130m3 abstractC0130m3) {
        abstractC0130m3.getClass();
        this.a = abstractC0130m3;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(int i) {
        AbstractC0140o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0140o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Double d) {
        AbstractC0140o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void m() {
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0130m3
    public boolean o() {
        return this.a.o();
    }
}