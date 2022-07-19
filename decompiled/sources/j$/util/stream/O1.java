package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class O1 extends R1 implements AbstractC0167u1 {
    public O1(AbstractC0167u1 abstractC0167u1, AbstractC0167u1 abstractC0167u12) {
        super(abstractC0167u1, abstractC0167u12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractC0135o1.h(this, dArr, i);
    }

    /* renamed from: f */
    public double[] c(int i) {
        return new double[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0135o1.k(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0167u1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0135o1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0082f2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0082f2(this);
    }
}
