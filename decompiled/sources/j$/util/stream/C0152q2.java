package j$.util.stream;
/* renamed from: j$.util.stream.q2 */
/* loaded from: classes2.dex */
public final class C0152q2 extends AbstractC0162s2 implements AbstractC0123l3 {
    private final long[] h;

    C0152q2(C0152q2 c0152q2, j$.util.u uVar, long j, long j2) {
        super(c0152q2, uVar, j, j2, c0152q2.h.length);
        this.h = c0152q2.h;
    }

    public C0152q2(j$.util.u uVar, AbstractC0192y2 abstractC0192y2, long[] jArr) {
        super(uVar, abstractC0192y2, jArr.length);
        this.h = jArr;
    }

    @Override // j$.util.stream.AbstractC0162s2, j$.util.stream.AbstractC0129m3, j$.util.stream.AbstractC0123l3, j$.util.function.q
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

    @Override // j$.util.stream.AbstractC0162s2
    AbstractC0162s2 b(j$.util.u uVar, long j, long j2) {
        return new C0152q2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Long l) {
        AbstractC0139o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
