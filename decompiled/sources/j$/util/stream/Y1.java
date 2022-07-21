package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
final class Y1 extends AbstractC0057a2 implements AbstractC0192y1 {
    /* renamed from: a */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractC0140o1.j(this, lArr, i);
    }

    @Override // j$.util.stream.AbstractC0057a2, j$.util.stream.A1
    public AbstractC0197z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.AbstractC0197z1
    public Object e() {
        long[] jArr;
        jArr = AbstractC0188x2.f;
        return jArr;
    }

    /* renamed from: f */
    public /* synthetic */ AbstractC0192y1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0140o1.m(this, consumer);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return j$.util.L.d();
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return j$.util.L.d();
    }
}
