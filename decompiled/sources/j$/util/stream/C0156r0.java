package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.r0 */
/* loaded from: classes2.dex */
public final class C0156r0 extends CountedCompleter {
    private j$.util.u a;
    private final AbstractC0130m3 b;
    private final AbstractC0193y2 c;
    private long d;

    C0156r0(C0156r0 c0156r0, j$.util.u uVar) {
        super(c0156r0);
        this.a = uVar;
        this.b = c0156r0.b;
        this.d = c0156r0.d;
        this.c = c0156r0.c;
    }

    public C0156r0(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, AbstractC0130m3 abstractC0130m3) {
        super(null);
        this.b = abstractC0130m3;
        this.c = abstractC0193y2;
        this.a = uVar;
        this.d = 0L;
    }

    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        j$.util.u trySplit;
        j$.util.u uVar = this.a;
        long estimateSize = uVar.estimateSize();
        long j = this.d;
        if (j == 0) {
            j = AbstractC0084f.h(estimateSize);
            this.d = j;
        }
        boolean d = EnumC0077d4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        AbstractC0130m3 abstractC0130m3 = this.b;
        C0156r0 c0156r0 = this;
        while (true) {
            if (d && abstractC0130m3.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = uVar.trySplit()) == null) {
                break;
            } else {
                C0156r0 c0156r02 = new C0156r0(c0156r0, trySplit);
                c0156r0.addToPendingCount(1);
                if (z) {
                    uVar = trySplit;
                } else {
                    C0156r0 c0156r03 = c0156r0;
                    c0156r0 = c0156r02;
                    c0156r02 = c0156r03;
                }
                z = !z;
                c0156r0.fork();
                c0156r0 = c0156r02;
                estimateSize = uVar.estimateSize();
            }
        }
        c0156r0.c.n0(abstractC0130m3, uVar);
        c0156r0.a = null;
        c0156r0.propagateCompletion();
    }
}
