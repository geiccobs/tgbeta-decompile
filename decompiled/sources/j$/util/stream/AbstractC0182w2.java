package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.w2 */
/* loaded from: classes2.dex */
abstract class AbstractC0182w2 extends CountedCompleter {
    protected final A1 a;
    protected final int b;

    public AbstractC0182w2(A1 a1, int i) {
        this.a = a1;
        this.b = i;
    }

    public AbstractC0182w2(AbstractC0182w2 abstractC0182w2, A1 a1, int i) {
        super(abstractC0182w2);
        this.a = a1;
        this.b = i;
    }

    abstract void a();

    abstract AbstractC0182w2 b(int i, int i2);

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        AbstractC0182w2 abstractC0182w2 = this;
        while (abstractC0182w2.a.p() != 0) {
            abstractC0182w2.setPendingCount(abstractC0182w2.a.p() - 1);
            int i = 0;
            int i2 = 0;
            while (i < abstractC0182w2.a.p() - 1) {
                AbstractC0182w2 b = abstractC0182w2.b(i, abstractC0182w2.b + i2);
                i2 = (int) (i2 + b.a.count());
                b.fork();
                i++;
            }
            abstractC0182w2 = abstractC0182w2.b(i, abstractC0182w2.b + i2);
        }
        abstractC0182w2.a();
        abstractC0182w2.propagateCompletion();
    }
}
