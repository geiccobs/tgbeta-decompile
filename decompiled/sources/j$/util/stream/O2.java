package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class O2 extends T2 implements S2, AbstractC0112k3 {
    final /* synthetic */ j$.util.function.y b;
    final /* synthetic */ j$.util.function.v c;
    final /* synthetic */ j$.util.function.b d;

    public O2(j$.util.function.y yVar, j$.util.function.v vVar, j$.util.function.b bVar) {
        this.b = yVar;
        this.c = vVar;
        this.d = bVar;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(double d) {
        AbstractC0134o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void accept(int i) {
        this.c.accept(this.a, i);
    }

    @Override // j$.util.stream.AbstractC0124m3, j$.util.stream.AbstractC0118l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0134o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Integer num) {
        AbstractC0134o1.b(this, num);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((O2) s2).a);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        this.a = this.b.get();
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
