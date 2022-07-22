package j$.util.stream;
/* renamed from: j$.util.stream.r3 */
/* loaded from: classes2.dex */
class C0158r3 extends AbstractC0093g3 {
    long b;
    long c;
    final /* synthetic */ C0163s3 d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0158r3(C0163s3 c0163s3, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.d = c0163s3;
        this.b = c0163s3.l;
        long j = c0163s3.m;
        this.c = j < 0 ? Long.MAX_VALUE : j;
    }

    @Override // j$.util.stream.AbstractC0117k3, j$.util.stream.AbstractC0129m3
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

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        this.a.n(B3.c(j, this.d.l, this.c));
    }

    @Override // j$.util.stream.AbstractC0093g3, j$.util.stream.AbstractC0129m3
    public boolean o() {
        return this.c == 0 || this.a.o();
    }
}
