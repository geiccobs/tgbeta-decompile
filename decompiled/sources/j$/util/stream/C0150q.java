package j$.util.stream;
/* renamed from: j$.util.stream.q */
/* loaded from: classes2.dex */
class C0150q extends AbstractC0106i3 {
    boolean b;
    Object c;

    public C0150q(C0160s c0160s, AbstractC0130m3 abstractC0130m3) {
        super(abstractC0130m3);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        if (obj == null) {
            if (this.b) {
                return;
            }
            this.b = true;
            AbstractC0130m3 abstractC0130m3 = this.a;
            this.c = null;
            abstractC0130m3.accept((AbstractC0130m3) null);
            return;
        }
        Object obj2 = this.c;
        if (obj2 != null && obj.equals(obj2)) {
            return;
        }
        AbstractC0130m3 abstractC0130m32 = this.a;
        this.c = obj;
        abstractC0130m32.accept((AbstractC0130m3) obj);
    }

    @Override // j$.util.stream.AbstractC0106i3, j$.util.stream.AbstractC0130m3
    public void m() {
        this.b = false;
        this.c = null;
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void n(long j) {
        this.b = false;
        this.c = null;
        this.a.n(-1L);
    }
}
