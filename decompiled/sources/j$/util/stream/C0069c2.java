package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
/* renamed from: j$.util.stream.c2 */
/* loaded from: classes2.dex */
public class C0069c2 implements AbstractC0182w1 {
    final int[] a;
    int b;

    public C0069c2(long j) {
        if (j < 2147483639) {
            this.a = new int[(int) j];
            this.b = 0;
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    public C0069c2(int[] iArr) {
        this.a = iArr;
        this.b = iArr.length;
    }

    @Override // j$.util.stream.AbstractC0197z1, j$.util.stream.A1
    public AbstractC0197z1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.b;
    }

    @Override // j$.util.stream.AbstractC0197z1
    public void d(Object obj, int i) {
        System.arraycopy(this.a, 0, (int[]) obj, i, this.b);
    }

    @Override // j$.util.stream.AbstractC0197z1
    public Object e() {
        int[] iArr = this.a;
        int length = iArr.length;
        int i = this.b;
        return length == i ? iArr : Arrays.copyOf(iArr, i);
    }

    /* renamed from: f */
    public /* synthetic */ void i(Integer[] numArr, int i) {
        AbstractC0140o1.i(this, numArr, i);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ void forEach(Consumer consumer) {
        AbstractC0140o1.l(this, consumer);
    }

    @Override // j$.util.stream.AbstractC0197z1
    public void g(Object obj) {
        j$.util.function.l lVar = (j$.util.function.l) obj;
        for (int i = 0; i < this.b; i++) {
            lVar.accept(this.a[i]);
        }
    }

    /* renamed from: j */
    public /* synthetic */ AbstractC0182w1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0140o1.o(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractC0140o1.g(this, mVar);
    }

    @Override // j$.util.stream.AbstractC0197z1, j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.w mo69spliterator() {
        return j$.util.L.k(this.a, 0, this.b, 1040);
    }

    public String toString() {
        return String.format("IntArrayNode[%d][%s]", Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a));
    }

    @Override // j$.util.stream.AbstractC0197z1, j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        return j$.util.L.k(this.a, 0, this.b, 1040);
    }
}
