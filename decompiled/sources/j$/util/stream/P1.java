package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class P1 extends R1 implements AbstractC0176w1 {
    public P1(AbstractC0176w1 abstractC0176w1, AbstractC0176w1 abstractC0176w12) {
        super(abstractC0176w1, abstractC0176w12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractC0134o1.i(this, numArr, i);
    }

    /* renamed from: f */
    public int[] c(int i) {
        return new int[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0134o1.l(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0176w1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0134o1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0087g2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0087g2(this);
    }
}
