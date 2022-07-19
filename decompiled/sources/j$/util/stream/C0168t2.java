package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.t2 */
/* loaded from: classes2.dex */
public final class C0168t2 extends C0059a4 implements A1, AbstractC0162s1 {
    @Override // j$.util.stream.AbstractC0162s1
    /* renamed from: a */
    public A1 mo70a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(double d) {
        AbstractC0140o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(int i) {
        AbstractC0140o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0140o1.e(this);
        throw null;
    }

    @Override // j$.util.stream.C0059a4, j$.util.function.Consumer
    public void accept(Object obj) {
        super.accept(obj);
    }

    @Override // j$.util.stream.A1
    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.C0059a4, j$.lang.e
    public void forEach(Consumer consumer) {
        super.forEach(consumer);
    }

    @Override // j$.util.stream.C0059a4, j$.util.stream.A1
    public void i(Object[] objArr, int i) {
        super.i(objArr, i);
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void m() {
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void n(long j) {
        clear();
        u(j);
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ boolean o() {
        return false;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public Object[] q(j$.util.function.m mVar) {
        long count = count();
        if (count < 2147483639) {
            Object[] objArr = (Object[]) mVar.apply((int) count);
            i(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ A1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.q(this, j, j2, mVar);
    }

    @Override // j$.util.stream.C0059a4, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo71spliterator() {
        return super.mo71spliterator();
    }
}
