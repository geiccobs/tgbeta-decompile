package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class P1 extends R1 implements AbstractC0182w1 {
    public P1(AbstractC0182w1 abstractC0182w1, AbstractC0182w1 abstractC0182w12) {
        super(abstractC0182w1, abstractC0182w12);
    }

    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractC0140o1.i(this, numArr, i);
    }

    /* renamed from: f */
    public int[] c(int i) {
        return new int[i];
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0140o1.l(this, consumer);
    }

    /* renamed from: h */
    public /* synthetic */ AbstractC0182w1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return new C0093g2(this);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return new C0093g2(this);
    }
}
