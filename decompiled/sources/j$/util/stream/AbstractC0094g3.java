package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.g3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0094g3 implements AbstractC0118k3 {
    protected final AbstractC0130m3 a;

    public AbstractC0094g3(AbstractC0130m3 abstractC0130m3) {
        abstractC0130m3.getClass();
        this.a = abstractC0130m3;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(double d) {
        AbstractC0140o1.f(this);
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
    public /* synthetic */ void accept(Integer num) {
        AbstractC0140o1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
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
