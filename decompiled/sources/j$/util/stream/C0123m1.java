package j$.util.stream;
/* renamed from: j$.util.stream.m1 */
/* loaded from: classes2.dex */
final class C0123m1 extends AbstractC0067d {
    private final C0117l1 j;

    public C0123m1(C0117l1 c0117l1, AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        super(abstractC0188y2, uVar);
        this.j = c0117l1;
    }

    C0123m1(C0123m1 c0123m1, j$.util.u uVar) {
        super(c0123m1, uVar);
        this.j = c0123m1.j;
    }

    @Override // j$.util.stream.AbstractC0079f
    public Object a() {
        boolean z;
        AbstractC0188y2 abstractC0188y2 = this.a;
        AbstractC0105j1 abstractC0105j1 = (AbstractC0105j1) this.j.c.get();
        abstractC0188y2.u0(abstractC0105j1, this.b);
        boolean z2 = abstractC0105j1.b;
        z = this.j.b.b;
        if (z2 == z) {
            l(Boolean.valueOf(z2));
            return null;
        }
        return null;
    }

    @Override // j$.util.stream.AbstractC0079f
    public AbstractC0079f f(j$.util.u uVar) {
        return new C0123m1(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0067d
    protected Object k() {
        boolean z;
        z = this.j.b.b;
        return Boolean.valueOf(!z);
    }
}
