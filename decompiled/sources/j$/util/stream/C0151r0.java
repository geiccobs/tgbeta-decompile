package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.r0 */
/* loaded from: classes2.dex */
public final class C0151r0 extends CountedCompleter {
    private j$.util.u a;
    private final AbstractC0125m3 b;
    private final AbstractC0188y2 c;
    private long d;

    C0151r0(C0151r0 c0151r0, j$.util.u uVar) {
        super(c0151r0);
        this.a = uVar;
        this.b = c0151r0.b;
        this.d = c0151r0.d;
        this.c = c0151r0.c;
    }

    public C0151r0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, AbstractC0125m3 abstractC0125m3) {
        super(null);
        this.b = abstractC0125m3;
        this.c = abstractC0188y2;
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
            j = AbstractC0079f.h(estimateSize);
            this.d = j;
        }
        boolean d = EnumC0072d4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        AbstractC0125m3 abstractC0125m3 = this.b;
        C0151r0 c0151r0 = this;
        while (true) {
            if (d && abstractC0125m3.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = uVar.trySplit()) == null) {
                break;
            } else {
                C0151r0 c0151r02 = new C0151r0(c0151r0, trySplit);
                c0151r0.addToPendingCount(1);
                if (z) {
                    uVar = trySplit;
                } else {
                    C0151r0 c0151r03 = c0151r0;
                    c0151r0 = c0151r02;
                    c0151r02 = c0151r03;
                }
                z = !z;
                c0151r0.fork();
                c0151r0 = c0151r02;
                estimateSize = uVar.estimateSize();
            }
        }
        c0151r0.c.n0(abstractC0125m3, uVar);
        c0151r0.a = null;
        c0151r0.propagateCompletion();
    }
}
