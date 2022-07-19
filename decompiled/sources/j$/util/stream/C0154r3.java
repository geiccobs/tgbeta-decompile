package j$.util.stream;
/* renamed from: j$.util.stream.r3 */
/* loaded from: classes2.dex */
class C0154r3 extends AbstractC0089g3 {
    long b;
    long c;
    final /* synthetic */ C0159s3 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0154r3(C0159s3 c0159s3, AbstractC0125m3 abstractC0125m3) {
        super(abstractC0125m3);
        this.d = c0159s3;
        this.b = c0159s3.l;
        long j = c0159s3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.stream.AbstractC0113k3, j$.util.stream.AbstractC0125m3
    public void accept(int i) {
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
        this.a.accept(i);
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractC0089g3, j$.util.stream.AbstractC0125m3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
