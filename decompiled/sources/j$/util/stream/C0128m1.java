package j$.util.stream;
/* renamed from: j$.util.stream.m1 */
/* loaded from: classes2.dex */
final class C0128m1 extends AbstractC0072d {
    private final C0122l1 j;

    public C0128m1(C0122l1 c0122l1, AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        super(abstractC0193y2, uVar);
        this.j = c0122l1;
    }

    C0128m1(C0128m1 c0128m1, j$.util.u uVar) {
        super(c0128m1, uVar);
        this.j = c0128m1.j;
    }

    @Override // j$.util.stream.AbstractC0084f
    public Object a() {
        boolean z;
        AbstractC0193y2 abstractC0193y2 = this.a;
        AbstractC0110j1 abstractC0110j1 = (AbstractC0110j1) this.j.c.get();
        abstractC0193y2.u0(abstractC0110j1, this.b);
        boolean z2 = abstractC0110j1.b;
        z = this.j.b.b;
        if (z2 == z) {
            l(Boolean.valueOf(z2));
            return null;
        }
        return null;
    }

    @Override // j$.util.stream.AbstractC0084f
    public AbstractC0084f f(j$.util.u uVar) {
        return new C0128m1(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0072d
    protected Object k() {
        boolean z;
        z = this.j.b.b;
        return Boolean.valueOf(!z);
    }
}
