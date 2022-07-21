package j$.util.stream;
/* renamed from: j$.util.stream.i4 */
/* loaded from: classes2.dex */
final class C0107i4 extends AbstractC0113j4 implements j$.util.function.q {
    final long[] c;

    public C0107i4(int i) {
        this.c = new long[i];
    }

    @Override // j$.util.function.q
    public void accept(long j) {
        long[] jArr = this.c;
        int i = this.b;
        this.b = i + 1;
        jArr[i] = j;
    }

    @Override // j$.util.stream.AbstractC0113j4
    public void b(Object obj, long j) {
        j$.util.function.q qVar = (j$.util.function.q) obj;
        for (int i = 0; i < j; i++) {
            qVar.accept(this.c[i]);
        }
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
