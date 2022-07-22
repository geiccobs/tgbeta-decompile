package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class H3 extends D3 {
    private W3 c;

    public H3(AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
    }

    @Override // j$.util.stream.AbstractC0117k3, j$.util.stream.AbstractC0129m3
    public void accept(int i) {
        this.c.accept(i);
    }

    @Override // j$.util.stream.AbstractC0093g3, j$.util.stream.AbstractC0129m3
    public void m() {
        int[] iArr = (int[]) this.c.e();
        Arrays.sort(iArr);
        this.a.n(iArr.length);
        int i = 0;
        if (!this.b) {
            int length = iArr.length;
            while (i < length) {
                this.a.accept(iArr[i]);
                i++;
            }
        } else {
            int length2 = iArr.length;
            while (i < length2) {
                int i2 = iArr[i];
                if (this.a.o()) {
                    break;
                }
                this.a.accept(i2);
                i++;
            }
        }
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        if (j < 2147483639) {
            this.c = j > 0 ? new W3((int) j) : new W3();
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
