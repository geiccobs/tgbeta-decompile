package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class M2 implements S2, AbstractC0113k3 {
    private int a;
    final /* synthetic */ int b;
    final /* synthetic */ j$.util.function.j c;

    public M2(int i, j$.util.function.j jVar) {
        this.b = i;
        this.c = jVar;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void accept(double d) {
        AbstractC0135o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void accept(int i) {
        this.a = this.c.applyAsInt(this.a, i);
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
    public /* synthetic */ void accept(Integer num) {
        AbstractC0135o1.b(this, num);
    }

    @Override // j$.util.function.y
    public Object get() {
        return Integer.valueOf(this.a);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        accept(((M2) s2).a);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void n(long j) {
        this.a = this.b;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
