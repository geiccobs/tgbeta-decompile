package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class Q1 extends R1 implements AbstractC0191y1 {
    public Q1(AbstractC0191y1 abstractC0191y1, AbstractC0191y1 abstractC0191y12) {
        super(abstractC0191y1, abstractC0191y12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractC0139o1.j(this, lArr, i);
    }

    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0139o1.m(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0191y1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0139o1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0098h2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0098h2(this);
    }
}
