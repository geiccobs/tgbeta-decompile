package j$.util.stream;
/* renamed from: j$.util.stream.o2 */
/* loaded from: classes2.dex */
public final class C0136o2 extends AbstractC0158s2 implements AbstractC0107j3 {
    private final double[] h;

    C0136o2(C0136o2 c0136o2, j$.util.u uVar, long j, long j2) {
        super(c0136o2, uVar, j, j2, c0136o2.h.length);
        this.h = c0136o2.h;
    }

    public C0136o2(j$.util.u uVar, AbstractC0188y2 abstractC0188y2, double[] dArr) {
        super(uVar, abstractC0188y2, dArr.length);
        this.h = dArr;
    }

    @Override // j$.util.stream.AbstractC0158s2, j$.util.stream.AbstractC0125m3
    public void accept(double d) {
        int i = this.f;
        if (i < this.g) {
            double[] dArr = this.h;
            this.f = i + 1;
            dArr[i] = d;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractC0158s2
    AbstractC0158s2 b(j$.util.u uVar, long j, long j2) {
        return new C0136o2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Double d) {
        AbstractC0135o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
