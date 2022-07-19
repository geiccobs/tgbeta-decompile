package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Consumer;
/* loaded from: classes2.dex */
class G2 extends T2 implements S2 {
    final /* synthetic */ Object b;
    final /* synthetic */ BiFunction c;
    final /* synthetic */ j$.util.function.b d;

    public G2(Object obj, BiFunction biFunction, j$.util.function.b bVar) {
        this.b = obj;
        this.c = biFunction;
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
    public /* synthetic */ void accept(long j) {
        AbstractC0135o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.a = this.c.apply(this.a, obj);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.S2
    public void h(S2 s2) {
        this.a = this.d.apply(this.a, ((G2) s2).a);
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
