package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.n2 */
/* loaded from: classes2.dex */
public final class C0135n2 extends Y3 implements AbstractC0192y1, AbstractC0157r1 {
    @Override // j$.util.stream.Y3
    public j$.util.v B() {
        return super.mo71spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Long l) {
        AbstractC0140o1.c(this, l);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractC0140o1.j(this, lArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ AbstractC0192y1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.AbstractC0157r1, j$.util.stream.AbstractC0162s1
    /* renamed from: a */
    public A1 mo70a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0157r1, j$.util.stream.AbstractC0162s1
    /* renamed from: a */
    public AbstractC0192y1 mo70a() {
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

    @Override // j$.util.stream.Y3, j$.util.function.q
    public void accept(long j) {
        super.accept(j);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.AbstractC0197z1, j$.util.stream.A1
    public AbstractC0197z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0197z1
    public void d(Object obj, int i) {
        super.d((long[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0197z1
    public Object e() {
        return (long[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0197z1
    public void g(Object obj) {
        super.g((j$.util.function.q) obj);
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void m() {
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void n(long j) {
        clear();
        x(j);
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
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractC0140o1.g(this, mVar);
    }

    @Override // j$.util.stream.Y3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.w mo71spliterator() {
        return super.mo71spliterator();
    }

    @Override // j$.util.stream.Y3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo71spliterator() {
        return super.mo71spliterator();
    }
}
