package j$.util.stream;

import j$.util.C0051l;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class R2 implements S2, AbstractC0124l3 {
    private boolean a;
    private long b;
    final /* synthetic */ j$.util.function.o c;

    public R2(j$.util.function.o oVar) {
        this.c = oVar;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(double d) {
        AbstractC0140o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(int i) {
        AbstractC0140o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
    public void accept(long j) {
        if (this.a) {
            this.a = false;
        } else {
            j = this.c.applyAsLong(this.b, j);
        }
        this.b = j;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    /* renamed from: b */
    public /* synthetic */ void accept(Long l) {
        AbstractC0140o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.function.y
    public Object get() {
        return this.a ? C0051l.a() : C0051l.d(this.b);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        R2 r2 = (R2) s2;
        if (!r2.a) {
            accept(r2.b);
        }
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void n(long j) {
        this.a = true;
        this.b = 0L;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
