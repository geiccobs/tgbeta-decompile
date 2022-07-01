package j$.util.stream;
/* renamed from: j$.util.stream.p2 */
/* loaded from: classes2.dex */
public final class C0141p2 extends AbstractC0157s2 implements AbstractC0112k3 {
    private final int[] h;

    C0141p2(C0141p2 c0141p2, j$.util.u uVar, long j, long j2) {
        super(c0141p2, uVar, j, j2, c0141p2.h.length);
        this.h = c0141p2.h;
    }

    public C0141p2(j$.util.u uVar, AbstractC0187y2 abstractC0187y2, int[] iArr) {
        super(uVar, abstractC0187y2, iArr.length);
        this.h = iArr;
    }

    @Override // j$.util.stream.AbstractC0157s2, j$.util.stream.AbstractC0124m3
    public void accept(int i) {
        int i2 = this.f;
        if (i2 < this.g) {
            int[] iArr = this.h;
            this.f = i2 + 1;
            iArr[i2] = i;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractC0157s2
    AbstractC0157s2 b(j$.util.u uVar, long j, long j2) {
        return new C0141p2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Integer num) {
        AbstractC0134o1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
