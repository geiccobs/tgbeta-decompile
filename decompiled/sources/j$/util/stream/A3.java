package j$.util.stream;
/* loaded from: classes2.dex */
final class A3 extends AbstractC0067d {
    private final AbstractC0061c j;
    private final j$.util.function.m k;
    private final long l;
    private final long m;
    private long n;
    private volatile boolean o;

    A3(A3 a3, j$.util.u uVar) {
        super(a3, uVar);
        this.j = a3.j;
        this.k = a3.k;
        this.l = a3.l;
        this.m = a3.m;
    }

    public A3(AbstractC0061c abstractC0061c, AbstractC0188y2 abstractC0188y2, j$.util.u uVar, j$.util.function.m mVar, long j, long j2) {
        super(abstractC0188y2, uVar);
        this.j = abstractC0061c;
        this.k = mVar;
        this.l = j;
        this.m = j2;
    }

    private long m(long j) {
        if (this.o) {
            return this.n;
        }
        A3 a3 = (A3) this.d;
        A3 a32 = (A3) this.e;
        if (a3 == null || a32 == null) {
            return this.n;
        }
        long m = a3.m(j);
        return m >= j ? m : m + a32.m(j);
    }

    @Override // j$.util.stream.AbstractC0079f
    public Object a() {
        long j = -1;
        if (e()) {
            if (EnumC0072d4.SIZED.e(this.j.c)) {
                j = this.j.q0(this.b);
            }
            AbstractC0157s1 t0 = this.j.t0(j, this.k);
            AbstractC0125m3 H0 = this.j.H0(this.a.s0(), t0);
            AbstractC0188y2 abstractC0188y2 = this.a;
            abstractC0188y2.o0(abstractC0188y2.v0(H0), this.b);
            return t0.mo70a();
        }
        AbstractC0188y2 abstractC0188y22 = this.a;
        AbstractC0157s1 t02 = abstractC0188y22.t0(-1L, this.k);
        abstractC0188y22.u0(t02, this.b);
        A1 mo70a = t02.mo70a();
        this.n = mo70a.count();
        this.o = true;
        this.b = null;
        return mo70a;
    }

    @Override // j$.util.stream.AbstractC0079f
    public AbstractC0079f f(j$.util.u uVar) {
        return new A3(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0067d
    protected void i() {
        this.i = true;
        if (this.o) {
            g(k());
        }
    }

    /* renamed from: n */
    public final A1 k() {
        return AbstractC0183x2.k(this.j.B0());
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0065  */
    @Override // j$.util.stream.AbstractC0079f, java.util.concurrent.CountedCompleter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void onCompletion(java.util.concurrent.CountedCompleter r12) {
        /*
            Method dump skipped, instructions count: 228
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.A3.onCompletion(java.util.concurrent.CountedCompleter):void");
    }
}
