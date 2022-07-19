package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.c */
/* loaded from: classes2.dex */
public abstract class AbstractC0061c extends AbstractC0188y2 implements AbstractC0085g {
    private final AbstractC0061c a;
    private final AbstractC0061c b;
    protected final int c;
    private AbstractC0061c d;
    private int e;
    private int f;
    private j$.util.u g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    public AbstractC0061c(AbstractC0061c abstractC0061c, int i) {
        if (!abstractC0061c.h) {
            abstractC0061c.h = true;
            abstractC0061c.d = this;
            this.b = abstractC0061c;
            this.c = EnumC0072d4.h & i;
            this.f = EnumC0072d4.a(i, abstractC0061c.f);
            AbstractC0061c abstractC0061c2 = abstractC0061c.a;
            this.a = abstractC0061c2;
            if (G0()) {
                abstractC0061c2.i = true;
            }
            this.e = abstractC0061c.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    public AbstractC0061c(j$.util.u uVar, int i, boolean z) {
        this.b = null;
        this.g = uVar;
        this.a = this;
        int i2 = EnumC0072d4.g & i;
        this.c = i2;
        this.f = ((i2 << 1) ^ (-1)) & EnumC0072d4.l;
        this.e = 0;
        this.k = z;
    }

    private j$.util.u I0(int i) {
        int i2;
        int i3;
        AbstractC0061c abstractC0061c = this.a;
        j$.util.u uVar = abstractC0061c.g;
        if (uVar != null) {
            abstractC0061c.g = null;
            if (abstractC0061c.k && abstractC0061c.i) {
                AbstractC0061c abstractC0061c2 = abstractC0061c.d;
                int i4 = 1;
                while (abstractC0061c != this) {
                    int i5 = abstractC0061c2.c;
                    if (abstractC0061c2.G0()) {
                        i4 = 0;
                        if (EnumC0072d4.SHORT_CIRCUIT.d(i5)) {
                            i5 &= EnumC0072d4.u ^ (-1);
                        }
                        uVar = abstractC0061c2.F0(abstractC0061c, uVar);
                        if (uVar.hasCharacteristics(64)) {
                            i3 = i5 & (EnumC0072d4.t ^ (-1));
                            i2 = EnumC0072d4.s;
                        } else {
                            i3 = i5 & (EnumC0072d4.s ^ (-1));
                            i2 = EnumC0072d4.t;
                        }
                        i5 = i3 | i2;
                    }
                    abstractC0061c2.e = i4;
                    abstractC0061c2.f = EnumC0072d4.a(i5, abstractC0061c.f);
                    i4++;
                    AbstractC0061c abstractC0061c3 = abstractC0061c2;
                    abstractC0061c2 = abstractC0061c2.d;
                    abstractC0061c = abstractC0061c3;
                }
            }
            if (i != 0) {
                this.f = EnumC0072d4.a(i, this.f);
            }
            return uVar;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    abstract void A0(j$.util.u uVar, AbstractC0125m3 abstractC0125m3);

    public abstract EnumC0078e4 B0();

    public final boolean C0() {
        return EnumC0072d4.ORDERED.d(this.f);
    }

    public /* synthetic */ j$.util.u D0() {
        return I0(0);
    }

    A1 E0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, j$.util.function.m mVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    j$.util.u F0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        return E0(abstractC0188y2, uVar, C0049a.a).mo69spliterator();
    }

    abstract boolean G0();

    public abstract AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3);

    public final j$.util.u J0() {
        AbstractC0061c abstractC0061c = this.a;
        if (this == abstractC0061c) {
            if (this.h) {
                throw new IllegalStateException("stream has already been operated upon or closed");
            }
            this.h = true;
            j$.util.u uVar = abstractC0061c.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractC0061c.g = null;
            return uVar;
        }
        throw new IllegalStateException();
    }

    abstract j$.util.u K0(AbstractC0188y2 abstractC0188y2, j$.util.function.y yVar, boolean z);

    @Override // j$.util.stream.AbstractC0085g, java.lang.AutoCloseable
    public void close() {
        this.h = true;
        this.g = null;
        AbstractC0061c abstractC0061c = this.a;
        Runnable runnable = abstractC0061c.j;
        if (runnable != null) {
            abstractC0061c.j = null;
            runnable.run();
        }
    }

    @Override // j$.util.stream.AbstractC0085g
    public final boolean isParallel() {
        return this.a.k;
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final void n0(AbstractC0125m3 abstractC0125m3, j$.util.u uVar) {
        abstractC0125m3.getClass();
        if (EnumC0072d4.SHORT_CIRCUIT.d(this.f)) {
            o0(abstractC0125m3, uVar);
            return;
        }
        abstractC0125m3.n(uVar.getExactSizeIfKnown());
        uVar.forEachRemaining(abstractC0125m3);
        abstractC0125m3.m();
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final void o0(AbstractC0125m3 abstractC0125m3, j$.util.u uVar) {
        AbstractC0061c abstractC0061c = this;
        while (abstractC0061c.e > 0) {
            abstractC0061c = abstractC0061c.b;
        }
        abstractC0125m3.n(uVar.getExactSizeIfKnown());
        abstractC0061c.A0(uVar, abstractC0125m3);
        abstractC0125m3.m();
    }

    @Override // j$.util.stream.AbstractC0085g
    public AbstractC0085g onClose(Runnable runnable) {
        AbstractC0061c abstractC0061c = this.a;
        Runnable runnable2 = abstractC0061c.j;
        if (runnable2 != null) {
            runnable = new M4(runnable2, runnable);
        }
        abstractC0061c.j = runnable;
        return this;
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final A1 p0(j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        if (this.a.k) {
            return z0(this, uVar, z, mVar);
        }
        AbstractC0157s1 t0 = t0(q0(uVar), mVar);
        t0.getClass();
        n0(v0(t0), uVar);
        return t0.mo70a();
    }

    public final AbstractC0085g parallel() {
        this.a.k = true;
        return this;
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final long q0(j$.util.u uVar) {
        if (EnumC0072d4.SIZED.d(this.f)) {
            return uVar.getExactSizeIfKnown();
        }
        return -1L;
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final EnumC0078e4 r0() {
        AbstractC0061c abstractC0061c = this;
        while (abstractC0061c.e > 0) {
            abstractC0061c = abstractC0061c.b;
        }
        return abstractC0061c.B0();
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final int s0() {
        return this.f;
    }

    public final AbstractC0085g sequential() {
        this.a.k = false;
        return this;
    }

    public j$.util.u spliterator() {
        if (!this.h) {
            this.h = true;
            AbstractC0061c abstractC0061c = this.a;
            if (this != abstractC0061c) {
                return K0(this, new C0055b(this), abstractC0061c.k);
            }
            j$.util.u uVar = abstractC0061c.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractC0061c.g = null;
            return uVar;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final AbstractC0125m3 u0(AbstractC0125m3 abstractC0125m3, j$.util.u uVar) {
        abstractC0125m3.getClass();
        n0(v0(abstractC0125m3), uVar);
        return abstractC0125m3;
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final AbstractC0125m3 v0(AbstractC0125m3 abstractC0125m3) {
        abstractC0125m3.getClass();
        for (AbstractC0061c abstractC0061c = this; abstractC0061c.e > 0; abstractC0061c = abstractC0061c.b) {
            abstractC0125m3 = abstractC0061c.H0(abstractC0061c.b.f, abstractC0125m3);
        }
        return abstractC0125m3;
    }

    @Override // j$.util.stream.AbstractC0188y2
    public final j$.util.u w0(j$.util.u uVar) {
        return this.e == 0 ? uVar : K0(this, new C0055b(uVar), this.a.k);
    }

    public final Object x0(N4 n4) {
        if (!this.h) {
            this.h = true;
            return this.a.k ? n4.c(this, I0(n4.b())) : n4.d(this, I0(n4.b()));
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    public final A1 y0(j$.util.function.m mVar) {
        if (!this.h) {
            this.h = true;
            if (!this.a.k || this.b == null || !G0()) {
                return p0(I0(0), true, mVar);
            }
            this.e = 0;
            AbstractC0061c abstractC0061c = this.b;
            return E0(abstractC0061c, abstractC0061c.I0(0), mVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    abstract A1 z0(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, boolean z, j$.util.function.m mVar);
}
