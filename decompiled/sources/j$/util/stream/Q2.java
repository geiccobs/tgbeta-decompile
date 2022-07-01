package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class Q2 implements S2, AbstractC0118l3 {
    private long a;
    final /* synthetic */ long b;
    final /* synthetic */ j$.util.function.o c;

    public Q2(long j, j$.util.function.o oVar) {
        this.b = j;
        this.c = oVar;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(double d) {
        AbstractC0134o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(int i) {
        AbstractC0134o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3, j$.util.stream.AbstractC0118l3, j$.util.function.q
    public void accept(long j) {
        this.a = this.c.applyAsLong(this.a, j);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractC0134o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.function.y
    public Object get() {
        return Long.valueOf(this.a);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        accept(((Q2) s2).a);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        this.a = this.b;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
