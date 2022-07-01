package j$.util.stream;
/* renamed from: j$.util.stream.q */
/* loaded from: classes2.dex */
class C0144q extends AbstractC0100i3 {
    boolean b;
    Object c;

    public C0144q(C0154s c0154s, AbstractC0124m3 abstractC0124m3) {
        super(abstractC0124m3);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        if (obj == null) {
            if (this.b) {
                return;
            }
            this.b = true;
            AbstractC0124m3 abstractC0124m3 = this.a;
            this.c = null;
            abstractC0124m3.accept((AbstractC0124m3) null);
            return;
        }
        Object obj2 = this.c;
        if (obj2 != null && obj.equals(obj2)) {
            return;
        }
        AbstractC0124m3 abstractC0124m32 = this.a;
        this.c = obj;
        abstractC0124m32.accept((AbstractC0124m3) obj);
    }

    @Override // j$.util.stream.AbstractC0100i3, j$.util.stream.AbstractC0124m3
    public void m() {
        this.b = false;
        this.c = null;
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        this.b = false;
        this.c = null;
        this.a.n(-1L);
    }
}
