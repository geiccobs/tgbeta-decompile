package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class C2 implements S2, AbstractC0107j3 {
    private double a;
    final /* synthetic */ double b;
    final /* synthetic */ j$.util.function.d c;

    public C2(double d, j$.util.function.d dVar) {
        this.b = d;
        this.c = dVar;
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void accept(double d) {
        this.a = this.c.applyAsDouble(this.a, d);
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

    @Override // j$.util.function.y
    public Object get() {
        return Double.valueOf(this.a);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        accept(((C2) s2).a);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
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
