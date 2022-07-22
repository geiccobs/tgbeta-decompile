package j$.util.stream;

import j$.util.C0049k;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class N2 implements S2, AbstractC0117k3 {
    private boolean a;
    private int b;
    final /* synthetic */ j$.util.function.j c;

    public N2(j$.util.function.j jVar) {
        this.c = jVar;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(double d) {
        AbstractC0139o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void accept(int i) {
        if (this.a) {
            this.a = false;
        } else {
            i = this.c.applyAsInt(this.b, i);
        }
        this.b = i;
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
    public /* synthetic */ void accept(Integer num) {
        AbstractC0139o1.b(this, num);
    }

    @Override // j$.util.function.y
    public Object get() {
        return this.a ? C0049k.a() : C0049k.d(this.b);
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        N2 n2 = (N2) s2;
        if (!n2.a) {
            accept(n2.b);
        }
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        this.a = true;
        this.b = 0;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
