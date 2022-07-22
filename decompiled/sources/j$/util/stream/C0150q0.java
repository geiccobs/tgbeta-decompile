package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.q0 */
/* loaded from: classes2.dex */
final class C0150q0 extends CountedCompleter {
    public static final /* synthetic */ int h = 0;
    private final AbstractC0192y2 a;
    private j$.util.u b;
    private final long c;
    private final ConcurrentHashMap d;
    private final AbstractC0129m3 e;
    private final C0150q0 f;
    private A1 g;

    C0150q0(C0150q0 c0150q0, j$.util.u uVar, C0150q0 c0150q02) {
        super(c0150q0);
        this.a = c0150q0.a;
        this.b = uVar;
        this.c = c0150q0.c;
        this.d = c0150q0.d;
        this.e = c0150q0.e;
        this.f = c0150q02;
    }

    public C0150q0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, AbstractC0129m3 abstractC0129m3) {
        super(null);
        this.a = abstractC0192y2;
        this.b = uVar;
        this.c = AbstractC0083f.h(uVar.estimateSize());
        this.d = new ConcurrentHashMap(Math.max(16, AbstractC0083f.g << 1));
        this.e = abstractC0129m3;
        this.f = null;
    }

    @Override // java.util.concurrent.CountedCompleter
    public final void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.b;
        long j = this.c;
        boolean z = false;
        C0150q0 c0150q0 = this;
        while (uVar.estimateSize() > j && (trySplit = uVar.trySplit()) != null) {
            C0150q0 c0150q02 = new C0150q0(c0150q0, trySplit, c0150q0.f);
            C0150q0 c0150q03 = new C0150q0(c0150q0, uVar, c0150q02);
            c0150q0.addToPendingCount(1);
            c0150q03.addToPendingCount(1);
            c0150q0.d.put(c0150q02, c0150q03);
            if (c0150q0.f != null) {
                c0150q02.addToPendingCount(1);
                if (c0150q0.d.replace(c0150q0.f, c0150q0, c0150q02)) {
                    c0150q0.addToPendingCount(-1);
                } else {
                    c0150q02.addToPendingCount(-1);
                }
            }
            if (z) {
                uVar = trySplit;
                c0150q0 = c0150q02;
                c0150q02 = c0150q03;
            } else {
                c0150q0 = c0150q03;
            }
            z = !z;
            c0150q02.fork();
        }
        if (c0150q0.getPendingCount() > 0) {
            C0144p0 c0144p0 = C0144p0.a;
            AbstractC0192y2 abstractC0192y2 = c0150q0.a;
            AbstractC0161s1 t0 = abstractC0192y2.t0(abstractC0192y2.q0(uVar), c0144p0);
            AbstractC0065c abstractC0065c = (AbstractC0065c) c0150q0.a;
            abstractC0065c.getClass();
            t0.getClass();
            abstractC0065c.n0(abstractC0065c.v0(t0), uVar);
            c0150q0.g = t0.mo70a();
            c0150q0.b = null;
        }
        c0150q0.tryComplete();
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
                AbstractC0192y2 abstractC0192y2 = this.a;
                AbstractC0129m3 abstractC0129m3 = this.e;
                AbstractC0065c abstractC0065c = (AbstractC0065c) abstractC0192y2;
                abstractC0065c.getClass();
                abstractC0129m3.getClass();
                abstractC0065c.n0(abstractC0065c.v0(abstractC0129m3), uVar);
                this.b = null;
            }
        }
        C0150q0 c0150q0 = (C0150q0) this.d.remove(this);
        if (c0150q0 != null) {
            c0150q0.tryComplete();
        }
    }
}
