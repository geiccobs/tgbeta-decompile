package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class F2 extends T2 implements S2, AbstractC0111j3 {
    final /* synthetic */ j$.util.function.y b;
    final /* synthetic */ j$.util.function.u c;
    final /* synthetic */ j$.util.function.b d;

    public F2(j$.util.function.y yVar, j$.util.function.u uVar, j$.util.function.b bVar) {
        this.b = yVar;
        this.c = uVar;
        this.d = bVar;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void accept(double d) {
        this.c.accept(this.a, d);
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

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((F2) s2).a);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        this.a = this.b.get();
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
