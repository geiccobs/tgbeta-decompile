package j$.util.stream;
/* renamed from: j$.util.stream.e */
/* loaded from: classes2.dex */
abstract class AbstractC0072e {
    protected final int a;
    protected int b;
    protected int c;
    protected long[] d;

    public AbstractC0072e() {
        this.a = 4;
    }

    public AbstractC0072e(int i) {
        if (i >= 0) {
            this.a = Math.max(4, 32 - Integer.numberOfLeadingZeros(i - 1));
            return;
        }
        throw new IllegalArgumentException("Illegal Capacity: " + i);
    }

    public abstract void clear();

    public long count() {
        int i = this.c;
        return i == 0 ? this.b : this.d[i] + this.b;
    }

    public int s(int i) {
        return 1 << ((i == 0 || i == 1) ? this.a : Math.min((this.a + i) - 1, 30));
    }
}
