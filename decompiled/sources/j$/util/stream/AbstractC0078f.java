package j$.util.stream;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
/* renamed from: j$.util.stream.f */
/* loaded from: classes2.dex */
abstract class AbstractC0078f extends CountedCompleter {
    static final int g = ForkJoinPool.getCommonPoolParallelism() << 2;
    protected final AbstractC0187y2 a;
    protected j$.util.u b;
    protected long c;
    protected AbstractC0078f d;
    protected AbstractC0078f e;
    private Object f;

    public AbstractC0078f(AbstractC0078f abstractC0078f, j$.util.u uVar) {
        super(abstractC0078f);
        this.b = uVar;
        this.a = abstractC0078f.a;
        this.c = abstractC0078f.c;
    }

    public AbstractC0078f(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        super(null);
        this.a = abstractC0187y2;
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

    public AbstractC0078f c() {
        return (AbstractC0078f) getCompleter();
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
        AbstractC0078f abstractC0078f = this;
        while (estimateSize > j && (trySplit = uVar.trySplit()) != null) {
            AbstractC0078f f = abstractC0078f.f(trySplit);
            abstractC0078f.d = f;
            AbstractC0078f f2 = abstractC0078f.f(uVar);
            abstractC0078f.e = f2;
            abstractC0078f.setPendingCount(1);
            if (z) {
                uVar = trySplit;
                abstractC0078f = f;
                f = f2;
            } else {
                abstractC0078f = f2;
            }
            z = !z;
            f.fork();
            estimateSize = uVar.estimateSize();
        }
        abstractC0078f.g(abstractC0078f.a());
        abstractC0078f.tryComplete();
    }

    public boolean d() {
        return this.d == null;
    }

    public boolean e() {
        return c() == null;
    }

    public abstract AbstractC0078f f(j$.util.u uVar);

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
