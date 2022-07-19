package j$.util.stream;
/* renamed from: j$.util.stream.x3 */
/* loaded from: classes2.dex */
class C0184x3 extends AbstractC0083f3 {
    long b;
    long c;
    final /* synthetic */ C0189y3 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0184x3(C0189y3 c0189y3, AbstractC0125m3 abstractC0125m3) {
        super(abstractC0125m3);
        this.d = c0189y3;
        this.b = c0189y3.l;
        long j = c0189y3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.stream.AbstractC0107j3, j$.util.stream.AbstractC0125m3
    public void accept(double d) {
        long j = this.b;
        if (j != 0) {
            this.b = j - 1;
            return;
        }
        long j2 = this.c;
        if (j2 <= 0) {
            return;
        }
        this.c = j2 - 1;
        this.a.accept(d);
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractC0083f3, j$.util.stream.AbstractC0125m3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
