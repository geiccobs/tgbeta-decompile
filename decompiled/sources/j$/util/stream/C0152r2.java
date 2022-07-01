package j$.util.stream;
/* renamed from: j$.util.stream.r2 */
/* loaded from: classes2.dex */
final class C0152r2 extends AbstractC0157s2 {
    private final Object[] h;

    C0152r2(C0152r2 c0152r2, j$.util.u uVar, long j, long j2) {
        super(c0152r2, uVar, j, j2, c0152r2.h.length);
        this.h = c0152r2.h;
    }

    public C0152r2(j$.util.u uVar, AbstractC0187y2 abstractC0187y2, Object[] objArr) {
        super(uVar, abstractC0187y2, objArr.length);
        this.h = objArr;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        int i = this.f;
        if (i < this.g) {
            Object[] objArr = this.h;
            this.f = i + 1;
            objArr[i] = obj;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }

    @Override // j$.util.stream.AbstractC0157s2
    AbstractC0157s2 b(j$.util.u uVar, long j, long j2) {
        return new C0152r2(this, uVar, j, j2);
    }
}
