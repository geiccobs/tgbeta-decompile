package j$.util.stream;
/* renamed from: j$.util.stream.r3 */
/* loaded from: classes2.dex */
class C0153r3 extends AbstractC0088g3 {
    long b;
    long c;
    final /* synthetic */ C0158s3 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0153r3(C0158s3 c0158s3, AbstractC0124m3 abstractC0124m3) {
        super(abstractC0124m3);
        this.d = c0158s3;
        this.b = c0158s3.l;
        long j = c0158s3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.stream.AbstractC0112k3, j$.util.stream.AbstractC0124m3
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

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractC0088g3, j$.util.stream.AbstractC0124m3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
