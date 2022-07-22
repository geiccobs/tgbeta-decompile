package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class O1 extends R1 implements AbstractC0171u1 {
    public O1(AbstractC0171u1 abstractC0171u1, AbstractC0171u1 abstractC0171u12) {
        super(abstractC0171u1, abstractC0171u12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractC0139o1.h(this, dArr, i);
    }

    /* renamed from: f */
    public double[] c(int i) {
        return new double[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0139o1.k(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0171u1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0139o1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0086f2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0086f2(this);
    }
}
