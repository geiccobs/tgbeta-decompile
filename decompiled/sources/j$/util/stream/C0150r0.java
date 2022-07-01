package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.r0 */
/* loaded from: classes2.dex */
public final class C0150r0 extends CountedCompleter {
    private j$.util.u a;
    private final AbstractC0124m3 b;
    private final AbstractC0187y2 c;
    private long d;

    C0150r0(C0150r0 c0150r0, j$.util.u uVar) {
        super(c0150r0);
        this.a = uVar;
        this.b = c0150r0.b;
        this.d = c0150r0.d;
        this.c = c0150r0.c;
    }

    public C0150r0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, AbstractC0124m3 abstractC0124m3) {
        super(null);
        this.b = abstractC0124m3;
        this.c = abstractC0187y2;
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
            j = AbstractC0078f.h(estimateSize);
            this.d = j;
        }
        boolean d = EnumC0071d4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        AbstractC0124m3 abstractC0124m3 = this.b;
        C0150r0 c0150r0 = this;
        while (true) {
            if (d && abstractC0124m3.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = uVar.trySplit()) == null) {
                break;
            } else {
                C0150r0 c0150r02 = new C0150r0(c0150r0, trySplit);
                c0150r0.addToPendingCount(1);
                if (z) {
                    uVar = trySplit;
                } else {
                    C0150r0 c0150r03 = c0150r0;
                    c0150r0 = c0150r02;
                    c0150r02 = c0150r03;
                }
                z = !z;
                c0150r0.fork();
                c0150r0 = c0150r02;
                estimateSize = uVar.estimateSize();
            }
        }
        c0150r0.c.n0(abstractC0124m3, uVar);
        c0150r0.a = null;
        c0150r0.propagateCompletion();
    }
}
