package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.n2 */
/* loaded from: classes2.dex */
public final class C0129n2 extends Y3 implements AbstractC0186y1, AbstractC0151r1 {
    @Override // j$.util.stream.Y3
    public j$.util.v B() {
        return super.mo70spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Long l) {
        AbstractC0134o1.c(this, l);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Long[] lArr, int i) {
        AbstractC0134o1.j(this, lArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ AbstractC0186y1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0134o1.p(this, j, j2, mVar);
    }

    @Override // j$.util.stream.AbstractC0151r1, j$.util.stream.AbstractC0156s1
    /* renamed from: a */
    public A1 mo69a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0151r1, j$.util.stream.AbstractC0156s1
    /* renamed from: a */
    public AbstractC0186y1 mo69a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(double d) {
        AbstractC0134o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(int i) {
        AbstractC0134o1.d(this);
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

    @Override // j$.util.stream.AbstractC0191z1, j$.util.stream.A1
    public AbstractC0191z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0191z1
    public void d(Object obj, int i) {
        super.d((long[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0191z1
    public Object e() {
        return (long[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0191z1
    public void g(Object obj) {
        super.g((j$.util.function.q) obj);
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

    @Override // j$.util.stream.Y3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.w mo70spliterator() {
        return super.mo70spliterator();
    }

    @Override // j$.util.stream.Y3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo70spliterator() {
        return super.mo70spliterator();
    }
}