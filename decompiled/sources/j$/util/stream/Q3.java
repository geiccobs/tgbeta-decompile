package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class Q3 extends E3 {
    private long[] c;
    private int d;

    public Q3(AbstractC0124m3 abstractC0124m3) {
        super(abstractC0124m3);
    }

    @Override // j$.util.stream.AbstractC0118l3, j$.util.function.q
    public void accept(long j) {
        long[] jArr = this.c;
        int i = this.d;
        this.d = i + 1;
        jArr[i] = j;
    }

    @Override // j$.util.stream.AbstractC0094h3, j$.util.stream.AbstractC0124m3
    public void m() {
        int i = 0;
        Arrays.sort(this.c, 0, this.d);
        this.a.n(this.d);
        if (!this.b) {
            while (i < this.d) {
                this.a.accept(this.c[i]);
                i++;
            }
        } else {
            while (i < this.d && !this.a.o()) {
                this.a.accept(this.c[i]);
                i++;
            }
        }
        this.a.m();
        this.c = null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        if (j < 2147483639) {
            this.c = new long[(int) j];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
