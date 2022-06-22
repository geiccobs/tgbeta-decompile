package j$.util.stream;
/* renamed from: j$.util.stream.o2 */
/* loaded from: classes2.dex */
public final class C0135o2 extends AbstractC0157s2 implements AbstractC0106j3 {
    private final double[] h;

    C0135o2(C0135o2 c0135o2, j$.util.u uVar, long j, long j2) {
        super(c0135o2, uVar, j, j2, c0135o2.h.length);
        this.h = c0135o2.h;
    }

    public C0135o2(j$.util.u uVar, AbstractC0187y2 abstractC0187y2, double[] dArr) {
        super(uVar, abstractC0187y2, dArr.length);
        this.h = dArr;
    }

    @Override // j$.util.stream.AbstractC0157s2, j$.util.stream.AbstractC0124m3
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

    @Override // j$.util.stream.AbstractC0157s2
    AbstractC0157s2 b(j$.util.u uVar, long j, long j2) {
        return new C0135o2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Double d) {
        AbstractC0134o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
