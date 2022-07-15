package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.q0 */
/* loaded from: classes2.dex */
final class C0146q0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final AbstractC0188y2 a;
    private j$.util.u b;
    private final long c;
    private final ConcurrentHashMap d;
    private final AbstractC0125m3 e;
    private final C0146q0 f;
    private A1 g;

    C0146q0(C0146q0 c0146q0, j$.util.u uVar, C0146q0 c0146q02) {
        super(c0146q0);
        this.a = c0146q0.a;
        this.b = uVar;
        this.c = c0146q0.c;
        this.d = c0146q0.d;
        this.e = c0146q0.e;
        this.f = c0146q02;
    }

    public C0146q0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, AbstractC0125m3 abstractC0125m3) {
        super(null);
        this.a = abstractC0188y2;
        this.b = uVar;
        this.c = AbstractC0079f.h(uVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, AbstractC0079f.g << 1));
        this.e = abstractC0125m3;
        this.f = null;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.b;
        long j = this.c;
        boolean z = false;
        C0146q0 c0146q0 = this;
        while (uVar.estimateSize() > j && (trySplit = uVar.trySplit()) != null) {
            C0146q0 c0146q02 = new C0146q0(c0146q0, trySplit, c0146q0.f);
            C0146q0 c0146q03 = new C0146q0(c0146q0, uVar, c0146q02);
            c0146q0.addToPendingCount(1);
            c0146q03.addToPendingCount(1);
            c0146q0.d.put(c0146q02, c0146q03);
            if (c0146q0.f != null) {
                c0146q02.addToPendingCount(1);
                if (c0146q0.d.replace(c0146q0.f, c0146q0, c0146q02)) {
                    c0146q0.addToPendingCount(-1);
                } else {
                    c0146q02.addToPendingCount(-1);
                }
            }
            if (z) {
                uVar = trySplit;
                c0146q0 = c0146q02;
                c0146q02 = c0146q03;
            } else {
                c0146q0 = c0146q03;
            }
            z = !z;
            c0146q02.fork();
        }
        if (c0146q0.getPendingCount() > 0) {
            C0140p0 c0140p0 = C0140p0.a;
            AbstractC0188y2 abstractC0188y2 = c0146q0.a;
            AbstractC0157s1 t0 = abstractC0188y2.t0(abstractC0188y2.q0(uVar), c0140p0);
            AbstractC0061c abstractC0061c = (AbstractC0061c) c0146q0.a;
            abstractC0061c.getClass();
            t0.getClass();
            abstractC0061c.n0(abstractC0061c.v0(t0), uVar);
            c0146q0.g = t0.mo70a();
            c0146q0.b = null;
        }
        c0146q0.tryComplete();
    }

    @Override // java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        A1 a1 = this.g;
        if (a1 != null) {
            a1.forEach(this.e);
            this.g = null;
        } else {
            j$.util.u uVar = this.b;
            if (uVar != null) {
                AbstractC0188y2 abstractC0188y2 = this.a;
                AbstractC0125m3 abstractC0125m3 = this.e;
                AbstractC0061c abstractC0061c = (AbstractC0061c) abstractC0188y2;
                abstractC0061c.getClass();
                abstractC0125m3.getClass();
                abstractC0061c.n0(abstractC0061c.v0(abstractC0125m3), uVar);
                this.b = null;
            }
        }
        C0146q0 c0146q0 = (C0146q0) this.d.remove(this);
        if (c0146q0 != null) {
            c0146q0.tryComplete();
        }
    }
}
