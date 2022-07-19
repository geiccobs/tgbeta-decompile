package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class Q1 extends R1 implements AbstractC0192y1 {
    public Q1(AbstractC0192y1 abstractC0192y1, AbstractC0192y1 abstractC0192y12) {
        super(abstractC0192y1, abstractC0192y12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractC0140o1.j(this, lArr, i);
    }

    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0140o1.m(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0192y1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0099h2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0099h2(this);
    }
}
