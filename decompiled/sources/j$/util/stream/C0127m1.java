package j$.util.stream;
/* renamed from: j$.util.stream.m1 */
/* loaded from: classes2.dex */
final class C0127m1 extends AbstractC0071d {
    private final C0121l1 j;

    public C0127m1(C0121l1 c0121l1, AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        super(abstractC0192y2, uVar);
        this.j = c0121l1;
    }

    C0127m1(C0127m1 c0127m1, j$.util.u uVar) {
        super(c0127m1, uVar);
        this.j = c0127m1.j;
    }

    @Override // j$.util.stream.AbstractC0083f
    public Object a() {
        boolean z;
        AbstractC0192y2 abstractC0192y2 = this.a;
        AbstractC0109j1 abstractC0109j1 = (AbstractC0109j1) this.j.c.get();
        abstractC0192y2.u0(abstractC0109j1, this.b);
        boolean z2 = abstractC0109j1.b;
        z = this.j.b.b;
        if (z2 == z) {
            l(Boolean.valueOf(z2));
            return null;
        }
        return null;
    }

    @Override // j$.util.stream.AbstractC0083f
    public AbstractC0083f f(j$.util.u uVar) {
        return new C0127m1(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0071d
    protected Object k() {
        boolean z;
        z = this.j.b.b;
        return Boolean.valueOf(!z);
    }
}
