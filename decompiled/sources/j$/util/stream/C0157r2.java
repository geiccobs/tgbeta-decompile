package j$.util.stream;
/* renamed from: j$.util.stream.r2 */
/* loaded from: classes2.dex */
final class C0157r2 extends AbstractC0162s2 {
    private final Object[] h;

    C0157r2(C0157r2 c0157r2, j$.util.u uVar, long j, long j2) {
        super(c0157r2, uVar, j, j2, c0157r2.h.length);
        this.h = c0157r2.h;
    }

    public C0157r2(j$.util.u uVar, AbstractC0192y2 abstractC0192y2, Object[] objArr) {
        super(uVar, abstractC0192y2, objArr.length);
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

    @Override // j$.util.stream.AbstractC0162s2
    AbstractC0162s2 b(j$.util.u uVar, long j, long j2) {
        return new C0157r2(this, uVar, j, j2);
    }
}
