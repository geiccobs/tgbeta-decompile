package j$.util.stream;
/* renamed from: j$.util.stream.p2 */
/* loaded from: classes2.dex */
public final class C0146p2 extends AbstractC0162s2 implements AbstractC0117k3 {
    private final int[] h;

    C0146p2(C0146p2 c0146p2, j$.util.u uVar, long j, long j2) {
        super(c0146p2, uVar, j, j2, c0146p2.h.length);
        this.h = c0146p2.h;
    }

    public C0146p2(j$.util.u uVar, AbstractC0192y2 abstractC0192y2, int[] iArr) {
        super(uVar, abstractC0192y2, iArr.length);
        this.h = iArr;
    }

    @Override // j$.util.stream.AbstractC0162s2, j$.util.stream.AbstractC0129m3
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

    @Override // j$.util.stream.AbstractC0162s2
    AbstractC0162s2 b(j$.util.u uVar, long j, long j2) {
        return new C0146p2(this, uVar, j, j2);
    }

    /* renamed from: c */
    public /* synthetic */ void accept(Integer num) {
        AbstractC0139o1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
