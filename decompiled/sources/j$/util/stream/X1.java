package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class X1 extends AbstractC0056a2 implements AbstractC0181w1 {
    /* renamed from: a */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractC0139o1.i(this, numArr, i);
    }

    @Override // j$.util.stream.AbstractC0056a2, j$.util.stream.A1
    public AbstractC0196z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.AbstractC0196z1
    public Object e() {
        int[] iArr;
        iArr = AbstractC0187x2.e;
        return iArr;
    }

    /* renamed from: f */
    public /* synthetic */ AbstractC0181w1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0139o1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0139o1.l(this, consumer);
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
