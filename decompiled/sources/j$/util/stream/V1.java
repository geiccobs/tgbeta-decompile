package j$.util.stream;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public final class V1 extends U3 implements AbstractC0166u1, AbstractC0140p1 {
    @Override // j$.util.stream.U3
    public j$.util.t B() {
        return super.mo71spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Double d) {
        AbstractC0134o1.a(this, d);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Double[] dArr, int i) {
        AbstractC0134o1.h(this, dArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ AbstractC0166u1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0134o1.n(this, j, j2, mVar);
    }

    @Override // j$.util.stream.AbstractC0140p1, j$.util.stream.AbstractC0156s1
    /* renamed from: a */
    public A1 mo70a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0140p1, j$.util.stream.AbstractC0156s1
    /* renamed from: a */
    public AbstractC0166u1 mo70a() {
        return this;
    }

    @Override // j$.util.stream.U3, j$.util.function.f
    public void accept(double d) {
        super.accept(d);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(int i) {
        AbstractC0134o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3, j$.util.stream.AbstractC0118l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0134o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.AbstractC0191z1, j$.util.stream.A1
    public AbstractC0191z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0191z1
    public void d(Object obj, int i) {
        super.d((double[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0191z1
    public Object e() {
        return (double[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0191z1
    public void g(Object obj) {
        super.g((j$.util.function.f) obj);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void m() {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        clear();
        x(j);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ boolean o() {
        return false;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractC0134o1.g(this, mVar);
    }

    @Override // j$.util.stream.U3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.w mo71spliterator() {
        return super.mo71spliterator();
    }

    @Override // j$.util.stream.U3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo71spliterator() {
        return super.mo71spliterator();
    }
}
