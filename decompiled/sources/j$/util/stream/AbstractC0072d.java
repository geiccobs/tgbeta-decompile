package j$.util.stream;

import java.util.concurrent.atomic.AtomicReference;
/* renamed from: j$.util.stream.d */
/* loaded from: classes2.dex */
abstract class AbstractC0072d extends AbstractC0084f {
    protected final AtomicReference h;
    protected volatile boolean i;

    public AbstractC0072d(AbstractC0072d abstractC0072d, j$.util.u uVar) {
        super(abstractC0072d, uVar);
        this.h = abstractC0072d.h;
    }

    public AbstractC0072d(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        super(abstractC0193y2, uVar);
        this.h = new AtomicReference(null);
    }

    @Override // j$.util.stream.AbstractC0084f
    public Object b() {
        if (e()) {
            Object obj = this.h.get();
            return obj == null ? k() : obj;
        }
        return super.b();
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x006b, code lost:
        r8 = r7.a();
     */
    @Override // j$.util.stream.AbstractC0084f, java.util.concurrent.CountedCompleter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void compute() {
        /*
            r10 = this;
            j$.util.u r0 = r10.b
            long r1 = r0.estimateSize()
            long r3 = r10.c
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto Lf
            goto L15
        Lf:
            long r3 = j$.util.stream.AbstractC0084f.h(r1)
            r10.c = r3
        L15:
            r5 = 0
            java.util.concurrent.atomic.AtomicReference r6 = r10.h
            r7 = r10
        L19:
            java.lang.Object r8 = r6.get()
            if (r8 != 0) goto L6f
            boolean r8 = r7.i
            if (r8 != 0) goto L34
            j$.util.stream.f r9 = r7.c()
        L27:
            j$.util.stream.d r9 = (j$.util.stream.AbstractC0072d) r9
            if (r8 != 0) goto L34
            if (r9 == 0) goto L34
            boolean r8 = r9.i
            j$.util.stream.f r9 = r9.c()
            goto L27
        L34:
            if (r8 == 0) goto L3b
            java.lang.Object r8 = r7.k()
            goto L6f
        L3b:
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L6b
            j$.util.u r1 = r0.trySplit()
            if (r1 != 0) goto L46
            goto L6b
        L46:
            j$.util.stream.f r2 = r7.f(r1)
            j$.util.stream.d r2 = (j$.util.stream.AbstractC0072d) r2
            r7.d = r2
            j$.util.stream.f r8 = r7.f(r0)
            j$.util.stream.d r8 = (j$.util.stream.AbstractC0072d) r8
            r7.e = r8
            r9 = 1
            r7.setPendingCount(r9)
            if (r5 == 0) goto L60
            r0 = r1
            r7 = r2
            r2 = r8
            goto L61
        L60:
            r7 = r8
        L61:
            r5 = r5 ^ 1
            r2.fork()
            long r1 = r0.estimateSize()
            goto L19
        L6b:
            java.lang.Object r8 = r7.a()
        L6f:
            r7.g(r8)
            r7.tryComplete()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.AbstractC0072d.compute():void");
    }

    @Override // j$.util.stream.AbstractC0084f
    public void g(Object obj) {
        if (!e()) {
            super.g(obj);
        } else if (obj == null) {
        } else {
            this.h.compareAndSet(null, obj);
        }
    }

    @Override // j$.util.stream.AbstractC0084f, java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public Object getRawResult() {
        return b();
    }

    protected void i() {
        this.i = true;
    }

    public void j() {
        AbstractC0072d abstractC0072d = this;
        for (AbstractC0072d abstractC0072d2 = (AbstractC0072d) c(); abstractC0072d2 != null; abstractC0072d2 = (AbstractC0072d) abstractC0072d2.c()) {
            if (abstractC0072d2.d == abstractC0072d) {
                AbstractC0072d abstractC0072d3 = (AbstractC0072d) abstractC0072d2.e;
                if (!abstractC0072d3.i) {
                    abstractC0072d3.i();
                }
            }
            abstractC0072d = abstractC0072d2;
        }
    }

    protected abstract Object k();

    public void l(Object obj) {
        if (obj != null) {
            this.h.compareAndSet(null, obj);
        }
    }
}