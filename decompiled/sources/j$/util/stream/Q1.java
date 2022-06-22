package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class Q1 extends R1 implements AbstractC0186y1 {
    public Q1(AbstractC0186y1 abstractC0186y1, AbstractC0186y1 abstractC0186y12) {
        super(abstractC0186y1, abstractC0186y12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractC0134o1.j(this, lArr, i);
    }

    /* renamed from: f */
    public long[] c(int i) {
        return new long[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0134o1.m(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0186y1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0134o1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0093h2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0093h2(this);
    }
}
