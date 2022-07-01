package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class W1 extends AbstractC0051a2 implements AbstractC0166u1 {
    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractC0134o1.h(this, dArr, i);
    }

    @Override // j$.util.stream.AbstractC0051a2, j$.util.stream.A1
    public AbstractC0191z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.AbstractC0191z1
    public Object e() {
        double[] dArr;
        dArr = AbstractC0182x2.g;
        return dArr;
    }

    /* renamed from: f */
    public /* synthetic */ AbstractC0166u1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0134o1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0134o1.k(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return j$.util.L.b();
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return j$.util.L.b();
    }
}
