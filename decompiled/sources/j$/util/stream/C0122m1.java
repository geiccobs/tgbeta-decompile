package j$.util.stream;
/* renamed from: j$.util.stream.m1 */
/* loaded from: classes2.dex */
final class C0122m1 extends AbstractC0066d {
    private final C0116l1 j;

    public C0122m1(C0116l1 c0116l1, AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        super(abstractC0187y2, uVar);
        this.j = c0116l1;
    }

    C0122m1(C0122m1 c0122m1, j$.util.u uVar) {
        super(c0122m1, uVar);
        this.j = c0122m1.j;
    }

    @Override // j$.util.stream.AbstractC0078f
    public Object a() {
        boolean z;
        AbstractC0187y2 abstractC0187y2 = this.a;
        AbstractC0104j1 abstractC0104j1 = (AbstractC0104j1) this.j.c.get();
        abstractC0187y2.u0(abstractC0104j1, this.b);
        boolean z2 = abstractC0104j1.b;
        z = this.j.b.b;
        if (z2 == z) {
            l(Boolean.valueOf(z2));
            return null;
        }
        return null;
    }

    @Override // j$.util.stream.AbstractC0078f
    public AbstractC0078f f(j$.util.u uVar) {
        return new C0122m1(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0066d
    protected Object k() {
        boolean z;
        z = this.j.b.b;
        return Boolean.valueOf(!z);
    }
}
