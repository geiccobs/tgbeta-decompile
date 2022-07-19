package j$.util.stream;

import java.util.Arrays;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class R3 extends F3 {
    private Object[] d;
    private int e;

    public R3(AbstractC0130m3 abstractC0130m3, Comparator comparator) {
        super(abstractC0130m3, comparator);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        Object[] objArr = this.d;
        int i = this.e;
        this.e = i + 1;
        objArr[i] = obj;
    }

    @Override // j$.util.stream.AbstractC0106i3, j$.util.stream.AbstractC0130m3
    public void m() {
        int i = 0;
        Arrays.sort(this.d, 0, this.e, this.b);
        this.a.n(this.e);
        if (!this.c) {
            while (i < this.e) {
                this.a.accept((AbstractC0130m3) this.d[i]);
                i++;
            }
        } else {
            while (i < this.e && !this.a.o()) {
                this.a.accept((AbstractC0130m3) this.d[i]);
                i++;
            }
        }
        this.a.m();
        this.d = null;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void n(long j) {
        if (j < 2147483639) {
            this.d = new Object[(int) j];
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
