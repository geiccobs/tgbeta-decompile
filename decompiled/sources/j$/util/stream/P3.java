package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class P3 extends D3 {
    private int[] c;
    private int d;

    public P3(AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
    }

    @Override // j$.util.stream.AbstractC0117k3, j$.util.stream.AbstractC0129m3
    public void accept(int i) {
        int[] iArr = this.c;
        int i2 = this.d;
        this.d = i2 + 1;
        iArr[i2] = i;
    }

    @Override // j$.util.stream.AbstractC0093g3, j$.util.stream.AbstractC0129m3
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

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        if (j < 2147483639) {
            this.c = new int[(int) j];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
