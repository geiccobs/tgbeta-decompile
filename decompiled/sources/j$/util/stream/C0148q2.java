package j$.util.stream;
/* renamed from: j$.util.stream.q2 */
/* loaded from: classes2.dex */
public final class C0148q2 extends AbstractC0158s2 implements AbstractC0119l3 {
    private final long[] h;

    C0148q2(C0148q2 c0148q2, j$.util.u uVar, long j, long j2) {
        super(c0148q2, uVar, j, j2, c0148q2.h.length);
        this.h = c0148q2.h;
    }

    public C0148q2(j$.util.u uVar, AbstractC0188y2 abstractC0188y2, long[] jArr) {
        super(uVar, abstractC0188y2, jArr.length);
        this.h = jArr;
    }

    @Override // j$.util.stream.AbstractC0158s2, j$.util.stream.AbstractC0125m3, j$.util.stream.AbstractC0119l3, j$.util.function.q
    public void accept(long j) {
        int i = this.f;
        if (i < this.g) {
            long[] jArr = this.h;
            this.f = i + 1;
            jArr[i] = j;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractC0158s2
    AbstractC0158s2 b(j$.util.u uVar, long j, long j2) {
        return new C0148q2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Long l) {
        AbstractC0135o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
