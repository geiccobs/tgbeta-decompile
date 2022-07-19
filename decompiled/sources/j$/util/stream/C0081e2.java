package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.u;
/* renamed from: j$.util.stream.e2 */
/* loaded from: classes2.dex */
public final class C0081e2 extends W3 implements AbstractC0182w1, AbstractC0152q1 {
    @Override // j$.util.stream.W3
    public u.a B() {
        return super.mo71spliterator();
    }

    /* renamed from: C */
    public /* synthetic */ void accept(Integer num) {
        AbstractC0140o1.b(this, num);
    }

    /* renamed from: D */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractC0140o1.i(this, numArr, i);
    }

    /* renamed from: E */
    public /* synthetic */ AbstractC0182w1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.AbstractC0152q1, j$.util.stream.AbstractC0162s1
    /* renamed from: a */
    public A1 mo70a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0152q1, j$.util.stream.AbstractC0162s1
    /* renamed from: a */
    public AbstractC0182w1 mo70a() {
        return this;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void accept(double d) {
        AbstractC0140o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.W3, j$.util.function.l
    public void accept(int i) {
        super.accept(i);
    }

    @Override // j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0140o1.e(this);
        throw null;
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
        super.d((int[]) obj, i);
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0197z1
    public Object e() {
        return (int[]) super.e();
    }

    @Override // j$.util.stream.Z3, j$.util.stream.AbstractC0197z1
    public void g(Object obj) {
        super.g((j$.util.function.l) obj);
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

    @Override // j$.util.stream.W3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.w mo71spliterator() {
        return super.mo71spliterator();
    }

    @Override // j$.util.stream.W3, j$.util.stream.Z3, java.lang.Iterable, j$.lang.e
    /* renamed from: spliterator */
    public j$.util.u mo71spliterator() {
        return super.mo71spliterator();
    }
}
