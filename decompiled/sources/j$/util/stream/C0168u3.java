package j$.util.stream;
/* renamed from: j$.util.stream.u3 */
/* loaded from: classes2.dex */
class C0168u3 extends AbstractC0094h3 {
    long b;
    long c;
    final /* synthetic */ C0173v3 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0168u3(C0173v3 c0173v3, AbstractC0124m3 abstractC0124m3) {
        super(abstractC0124m3);
        this.d = c0173v3;
        this.b = c0173v3.l;
        long j = c0173v3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.stream.AbstractC0118l3, j$.util.function.q
    public void accept(long j) {
        long j2 = this.b;
        if (j2 != 0) {
            this.b = j2 - 1;
            return;
        }
        long j3 = this.c;
        if (j3 <= 0) {
            return;
        }
        this.c = j3 - 1;
        this.a.accept(j);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractC0094h3, j$.util.stream.AbstractC0124m3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
