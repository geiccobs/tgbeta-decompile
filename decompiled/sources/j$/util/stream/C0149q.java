package j$.util.stream;
/* renamed from: j$.util.stream.q */
/* loaded from: classes2.dex */
class C0149q extends AbstractC0105i3 {
    boolean b;
    Object c;

    public C0149q(C0159s c0159s, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        if (obj == null) {
            if (this.b) {
                return;
            }
            this.b = true;
            AbstractC0129m3 abstractC0129m3 = this.a;
            this.c = null;
            abstractC0129m3.accept((AbstractC0129m3) null);
            return;
        }
        Object obj2 = this.c;
        if (obj2 != null && obj.equals(obj2)) {
            return;
        }
        AbstractC0129m3 abstractC0129m32 = this.a;
        this.c = obj;
        abstractC0129m32.accept((AbstractC0129m3) obj);
    }

    @Override // j$.util.stream.AbstractC0105i3, j$.util.stream.AbstractC0129m3
    public void m() {
        this.b = false;
        this.c = null;
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        this.b = false;
        this.c = null;
        this.a.n(-1L);
    }
}
