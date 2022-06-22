package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class X1 extends AbstractC0051a2 implements AbstractC0176w1 {
    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractC0134o1.i(this, numArr, i);
    }

    @Override // j$.util.stream.AbstractC0051a2, j$.util.stream.A1
    public AbstractC0191z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.AbstractC0191z1
    public Object e() {
        int[] iArr;
        iArr = AbstractC0182x2.e;
        return iArr;
    }

    /* renamed from: f */
    public /* synthetic */ AbstractC0176w1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0134o1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0134o1.l(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return j$.util.L.c();
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return j$.util.L.c();
    }
}
