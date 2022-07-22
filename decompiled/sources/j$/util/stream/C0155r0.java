package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.r0 */
/* loaded from: classes2.dex */
public final class C0155r0 extends CountedCompleter {
    private j$.util.u a;
    private final AbstractC0129m3 b;
    private final AbstractC0192y2 c;
    private long d;

    C0155r0(C0155r0 c0155r0, j$.util.u uVar) {
        super(c0155r0);
        this.a = uVar;
        this.b = c0155r0.b;
        this.d = c0155r0.d;
        this.c = c0155r0.c;
    }

    public C0155r0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, AbstractC0129m3 abstractC0129m3) {
        super(null);
        this.b = abstractC0129m3;
        this.c = abstractC0192y2;
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
            j = AbstractC0083f.h(estimateSize);
            this.d = j;
        }
        boolean d = EnumC0076d4.SHORT_CIRCUIT.d(this.c.s0());
        boolean z = false;
        AbstractC0129m3 abstractC0129m3 = this.b;
        C0155r0 c0155r0 = this;
        while (true) {
            if (d && abstractC0129m3.o()) {
                break;
            } else if (estimateSize <= j || (trySplit = uVar.trySplit()) == null) {
                break;
            } else {
                C0155r0 c0155r02 = new C0155r0(c0155r0, trySplit);
                c0155r0.addToPendingCount(1);
                if (z) {
                    uVar = trySplit;
                } else {
                    C0155r0 c0155r03 = c0155r0;
                    c0155r0 = c0155r02;
                    c0155r02 = c0155r03;
                }
                z = !z;
                c0155r0.fork();
                c0155r0 = c0155r02;
                estimateSize = uVar.estimateSize();
            }
        }
        c0155r0.c.n0(abstractC0129m3, uVar);
        c0155r0.a = null;
        c0155r0.propagateCompletion();
    }
}
