package j$.util.stream;
/* renamed from: j$.util.stream.q */
/* loaded from: classes2.dex */
class C0145q extends AbstractC0101i3 {
    boolean b;
    Object c;

    public C0145q(C0155s c0155s, AbstractC0125m3 abstractC0125m3) {
        super(abstractC0125m3);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        if (obj == null) {
            if (this.b) {
                return;
            }
            this.b = true;
            AbstractC0125m3 abstractC0125m3 = this.a;
            this.c = null;
            abstractC0125m3.accept((AbstractC0125m3) null);
            return;
        }
        Object obj2 = this.c;
        if (obj2 != null && obj.equals(obj2)) {
            return;
        }
        AbstractC0125m3 abstractC0125m32 = this.a;
        this.c = obj;
        abstractC0125m32.accept((AbstractC0125m3) obj);
    }

    @Override // j$.util.stream.AbstractC0101i3, j$.util.stream.AbstractC0125m3
    public void m() {
        this.b = false;
        this.c = null;
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void n(long j) {
        this.b = false;
        this.c = null;
        this.a.n(-1L);
    }
}
