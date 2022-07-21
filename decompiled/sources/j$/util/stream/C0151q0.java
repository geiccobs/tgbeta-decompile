package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.q0 */
/* loaded from: classes2.dex */
final class C0151q0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final AbstractC0193y2 a;
    private j$.util.u b;
    private final long c;
    private final ConcurrentHashMap d;
    private final AbstractC0130m3 e;
    private final C0151q0 f;
    private A1 g;

    C0151q0(C0151q0 c0151q0, j$.util.u uVar, C0151q0 c0151q02) {
        super(c0151q0);
        this.a = c0151q0.a;
        this.b = uVar;
        this.c = c0151q0.c;
        this.d = c0151q0.d;
        this.e = c0151q0.e;
        this.f = c0151q02;
    }

    public C0151q0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, AbstractC0130m3 abstractC0130m3) {
        super(null);
        this.a = abstractC0193y2;
        this.b = uVar;
        this.c = AbstractC0084f.h(uVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, AbstractC0084f.g << 1));
        this.e = abstractC0130m3;
        this.f = null;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.b;
        long j = this.c;
        boolean z = false;
        C0151q0 c0151q0 = this;
        while (uVar.estimateSize() > j && (trySplit = uVar.trySplit()) != null) {
            C0151q0 c0151q02 = new C0151q0(c0151q0, trySplit, c0151q0.f);
            C0151q0 c0151q03 = new C0151q0(c0151q0, uVar, c0151q02);
            c0151q0.addToPendingCount(1);
            c0151q03.addToPendingCount(1);
            c0151q0.d.put(c0151q02, c0151q03);
            if (c0151q0.f != null) {
                c0151q02.addToPendingCount(1);
                if (c0151q0.d.replace(c0151q0.f, c0151q0, c0151q02)) {
                    c0151q0.addToPendingCount(-1);
                } else {
                    c0151q02.addToPendingCount(-1);
                }
            }
            if (z) {
                uVar = trySplit;
                c0151q0 = c0151q02;
                c0151q02 = c0151q03;
            } else {
                c0151q0 = c0151q03;
            }
            z = !z;
            c0151q02.fork();
        }
        if (c0151q0.getPendingCount() > 0) {
            C0145p0 c0145p0 = C0145p0.a;
            AbstractC0193y2 abstractC0193y2 = c0151q0.a;
            AbstractC0162s1 t0 = abstractC0193y2.t0(abstractC0193y2.q0(uVar), c0145p0);
            AbstractC0066c abstractC0066c = (AbstractC0066c) c0151q0.a;
            abstractC0066c.getClass();
            t0.getClass();
            abstractC0066c.n0(abstractC0066c.v0(t0), uVar);
            c0151q0.g = t0.mo70a();
            c0151q0.b = null;
        }
        c0151q0.tryComplete();
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
                AbstractC0193y2 abstractC0193y2 = this.a;
                AbstractC0130m3 abstractC0130m3 = this.e;
                AbstractC0066c abstractC0066c = (AbstractC0066c) abstractC0193y2;
                abstractC0066c.getClass();
                abstractC0130m3.getClass();
                abstractC0066c.n0(abstractC0066c.v0(abstractC0130m3), uVar);
                this.b = null;
            }
        }
        C0151q0 c0151q0 = (C0151q0) this.d.remove(this);
        if (c0151q0 != null) {
            c0151q0.tryComplete();
        }
    }
}
