package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.q0 */
/* loaded from: classes2.dex */
final class C0145q0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final AbstractC0187y2 a;
    private j$.util.u b;
    private final long c;
    private final ConcurrentHashMap d;
    private final AbstractC0124m3 e;
    private final C0145q0 f;
    private A1 g;

    C0145q0(C0145q0 c0145q0, j$.util.u uVar, C0145q0 c0145q02) {
        super(c0145q0);
        this.a = c0145q0.a;
        this.b = uVar;
        this.c = c0145q0.c;
        this.d = c0145q0.d;
        this.e = c0145q0.e;
        this.f = c0145q02;
    }

    public C0145q0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, AbstractC0124m3 abstractC0124m3) {
        super(null);
        this.a = abstractC0187y2;
        this.b = uVar;
        this.c = AbstractC0078f.h(uVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, AbstractC0078f.g << 1));
        this.e = abstractC0124m3;
        this.f = null;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.b;
        long j = this.c;
        boolean z = false;
        C0145q0 c0145q0 = this;
        while (uVar.estimateSize() > j && (trySplit = uVar.trySplit()) != null) {
            C0145q0 c0145q02 = new C0145q0(c0145q0, trySplit, c0145q0.f);
            C0145q0 c0145q03 = new C0145q0(c0145q0, uVar, c0145q02);
            c0145q0.addToPendingCount(1);
            c0145q03.addToPendingCount(1);
            c0145q0.d.put(c0145q02, c0145q03);
            if (c0145q0.f != null) {
                c0145q02.addToPendingCount(1);
                if (c0145q0.d.replace(c0145q0.f, c0145q0, c0145q02)) {
                    c0145q0.addToPendingCount(-1);
                } else {
                    c0145q02.addToPendingCount(-1);
                }
            }
            if (z) {
                uVar = trySplit;
                c0145q0 = c0145q02;
                c0145q02 = c0145q03;
            } else {
                c0145q0 = c0145q03;
            }
            z = !z;
            c0145q02.fork();
        }
        if (c0145q0.getPendingCount() > 0) {
            C0139p0 c0139p0 = C0139p0.a;
            AbstractC0187y2 abstractC0187y2 = c0145q0.a;
            AbstractC0156s1 t0 = abstractC0187y2.t0(abstractC0187y2.q0(uVar), c0139p0);
            AbstractC0060c abstractC0060c = (AbstractC0060c) c0145q0.a;
            abstractC0060c.getClass();
            t0.getClass();
            abstractC0060c.n0(abstractC0060c.v0(t0), uVar);
            c0145q0.g = t0.mo70a();
            c0145q0.b = null;
        }
        c0145q0.tryComplete();
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
                AbstractC0187y2 abstractC0187y2 = this.a;
                AbstractC0124m3 abstractC0124m3 = this.e;
                AbstractC0060c abstractC0060c = (AbstractC0060c) abstractC0187y2;
                abstractC0060c.getClass();
                abstractC0124m3.getClass();
                abstractC0060c.n0(abstractC0060c.v0(abstractC0124m3), uVar);
                this.b = null;
            }
        }
        C0145q0 c0145q0 = (C0145q0) this.d.remove(this);
        if (c0145q0 != null) {
            c0145q0.tryComplete();
        }
    }
}
