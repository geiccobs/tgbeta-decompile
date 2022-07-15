package j$.util.stream;

import j$.util.function.Consumer;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.s2 */
/* loaded from: classes2.dex */
abstract class AbstractC0158s2 extends CountedCompleter implements AbstractC0125m3 {
    protected final j$.util.u a;
    protected final AbstractC0188y2 b;
    protected final long c;
    protected long d;
    protected long e;
    protected int f;
    protected int g;

    public AbstractC0158s2(AbstractC0158s2 abstractC0158s2, j$.util.u uVar, long j, long j2, int i) {
        super(abstractC0158s2);
        this.a = uVar;
        this.b = abstractC0158s2.b;
        this.c = abstractC0158s2.c;
        this.d = j;
        this.e = j2;
        if (j < 0 || j2 < 0 || (j + j2) - 1 >= i) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", Long.valueOf(j), Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i)));
        }
    }

    public AbstractC0158s2(j$.util.u uVar, AbstractC0188y2 abstractC0188y2, int i) {
        this.a = uVar;
        this.b = abstractC0188y2;
        this.c = AbstractC0079f.h(uVar.estimateSize());
        this.d = 0L;
        this.e = i;
    }

    public /* synthetic */ void accept(double d) {
        AbstractC0135o1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        AbstractC0135o1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        AbstractC0135o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    abstract AbstractC0158s2 b(j$.util.u uVar, long j, long j2);

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.a;
        AbstractC0158s2 abstractC0158s2 = this;
        while (uVar.estimateSize() > abstractC0158s2.c && (trySplit = uVar.trySplit()) != null) {
            abstractC0158s2.setPendingCount(1);
            long estimateSize = trySplit.estimateSize();
            abstractC0158s2.b(trySplit, abstractC0158s2.d, estimateSize).fork();
            abstractC0158s2 = abstractC0158s2.b(uVar, abstractC0158s2.d + estimateSize, abstractC0158s2.e - estimateSize);
        }
        AbstractC0061c abstractC0061c = (AbstractC0061c) abstractC0158s2.b;
        abstractC0061c.getClass();
        abstractC0061c.n0(abstractC0061c.v0(abstractC0158s2), uVar);
        abstractC0158s2.propagateCompletion();
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0125m3
    public void n(long j) {
        long j2 = this.e;
        if (j <= j2) {
            int i = (int) this.d;
            this.f = i;
            this.g = i + ((int) j2);
            return;
        }
        throw new IllegalStateException("size passed to Sink.begin exceeds array length");
    }

    @Override // j$.util.stream.AbstractC0125m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
