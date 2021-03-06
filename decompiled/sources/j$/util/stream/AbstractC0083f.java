package j$.util.stream;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
/* renamed from: j$.util.stream.f */
/* loaded from: classes2.dex */
abstract class AbstractC0083f extends CountedCompleter {
    static final int g = ForkJoinPool.getCommonPoolParallelism() << 2;
    protected final AbstractC0192y2 a;
    protected j$.util.u b;
    protected long c;
    protected AbstractC0083f d;
    protected AbstractC0083f e;
    private Object f;

    public AbstractC0083f(AbstractC0083f abstractC0083f, j$.util.u uVar) {
        super(abstractC0083f);
        this.b = uVar;
        this.a = abstractC0083f.a;
        this.c = abstractC0083f.c;
    }

    public AbstractC0083f(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        super(null);
        this.a = abstractC0192y2;
        this.b = uVar;
        this.c = 0L;
    }

    public static long h(long j) {
        long j2 = j / g;
        if (j2 > 0) {
            return j2;
        }
        return 1L;
    }

    public abstract Object a();

    public Object b() {
        return this.f;
    }

    public AbstractC0083f c() {
        return (AbstractC0083f) getCompleter();
    }

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.b;
        long estimateSize = uVar.estimateSize();
        long j = this.c;
        if (j == 0) {
            j = h(estimateSize);
            this.c = j;
        }
        boolean z = false;
        AbstractC0083f abstractC0083f = this;
        while (estimateSize > j && (trySplit = uVar.trySplit()) != null) {
            AbstractC0083f f = abstractC0083f.f(trySplit);
            abstractC0083f.d = f;
            AbstractC0083f f2 = abstractC0083f.f(uVar);
            abstractC0083f.e = f2;
            abstractC0083f.setPendingCount(1);
            if (z) {
                uVar = trySplit;
                abstractC0083f = f;
                f = f2;
            } else {
                abstractC0083f = f2;
            }
            z = !z;
            f.fork();
            estimateSize = uVar.estimateSize();
        }
        abstractC0083f.g(abstractC0083f.a());
        abstractC0083f.tryComplete();
    }

    public boolean d() {
        return this.d == null;
    }

    public boolean e() {
        return c() == null;
    }

    public abstract AbstractC0083f f(j$.util.u uVar);

    public void g(Object obj) {
        this.f = obj;
    }

    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public Object getRawResult() {
        return this.f;
    }

    @Override // java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        this.b = null;
        this.e = null;
        this.d = null;
    }

    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    protected void setRawResult(Object obj) {
        if (obj == null) {
            return;
        }
        throw new IllegalStateException();
    }
}
