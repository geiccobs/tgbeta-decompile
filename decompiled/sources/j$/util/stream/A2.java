package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class A2 extends T2 implements S2, AbstractC0119l3 {
    final /* synthetic */ j$.util.function.y b;
    final /* synthetic */ j$.util.function.w c;
    final /* synthetic */ j$.util.function.b d;

    public A2(j$.util.function.y yVar, j$.util.function.w wVar, j$.util.function.b bVar) {
        this.b = yVar;
        this.c = wVar;
        this.d = bVar;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void accept(double d) {
        AbstractC0135o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void accept(int i) {
        AbstractC0135o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0125m3, j$.util.stream.AbstractC0119l3, j$.util.function.q
    public void accept(long j) {
        this.c.accept(this.a, j);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractC0135o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((A2) s2).a);
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void n(long j) {
        this.a = this.b.get();
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
