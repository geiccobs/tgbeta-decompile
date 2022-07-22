package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.c */
/* loaded from: classes2.dex */
public abstract class AbstractC0065c extends AbstractC0192y2 implements AbstractC0089g {
    private final AbstractC0065c a;
    private final AbstractC0065c b;
    protected final int c;
    private AbstractC0065c d;
    private int e;
    private int f;
    private j$.util.u g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    public AbstractC0065c(AbstractC0065c abstractC0065c, int i) {
        if (!abstractC0065c.h) {
            abstractC0065c.h = true;
            abstractC0065c.d = this;
            this.b = abstractC0065c;
            this.c = EnumC0076d4.h & i;
            this.f = EnumC0076d4.a(i, abstractC0065c.f);
            AbstractC0065c abstractC0065c2 = abstractC0065c.a;
            this.a = abstractC0065c2;
            if (G0()) {
                abstractC0065c2.i = true;
            }
            this.e = abstractC0065c.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    public AbstractC0065c(j$.util.u uVar, int i, boolean z) {
        this.b = null;
        this.g = uVar;
        this.a = this;
        int i2 = EnumC0076d4.g & i;
        this.c = i2;
        this.f = ((i2 << 1) ^ (-1)) & EnumC0076d4.l;
        this.e = 0;
        this.k = z;
    }

    private j$.util.u I0(int i) {
        int i2;
        int i3;
        AbstractC0065c abstractC0065c = this.a;
        j$.util.u uVar = abstractC0065c.g;
        if (uVar != null) {
            abstractC0065c.g = null;
            if (abstractC0065c.k && abstractC0065c.i) {
                AbstractC0065c abstractC0065c2 = abstractC0065c.d;
                int i4 = 1;
                while (abstractC0065c != this) {
                    int i5 = abstractC0065c2.c;
                    if (abstractC0065c2.G0()) {
                        i4 = 0;
                        if (EnumC0076d4.SHORT_CIRCUIT.d(i5)) {
                            i5 &= EnumC0076d4.u ^ (-1);
                        }
                        uVar = abstractC0065c2.F0(abstractC0065c, uVar);
                        if (uVar.hasCharacteristics(64)) {
                            i3 = i5 & (EnumC0076d4.t ^ (-1));
                            i2 = EnumC0076d4.s;
                        } else {
                            i3 = i5 & (EnumC0076d4.s ^ (-1));
                            i2 = EnumC0076d4.t;
                        }
                        i5 = i3 | i2;
                    }
                    abstractC0065c2.e = i4;
                    abstractC0065c2.f = EnumC0076d4.a(i5, abstractC0065c.f);
                    i4++;
                    AbstractC0065c abstractC0065c3 = abstractC0065c2;
                    abstractC0065c2 = abstractC0065c2.d;
                    abstractC0065c = abstractC0065c3;
                }
            }
            if (i != 0) {
                this.f = EnumC0076d4.a(i, this.f);
            }
            return uVar;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    abstract void A0(j$.util.u uVar, AbstractC0129m3 abstractC0129m3);

    public abstract EnumC0082e4 B0();

    public final boolean C0() {
        return EnumC0076d4.ORDERED.d(this.f);
    }

    public /* synthetic */ j$.util.u D0() {
        return I0(0);
    }

    A1 E0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, j$.util.function.m mVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    j$.util.u F0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        return E0(abstractC0192y2, uVar, C0053a.a).mo69spliterator();
    }

    abstract boolean G0();

    public abstract AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3);

    public final j$.util.u J0() {
        AbstractC0065c abstractC0065c = this.a;
        if (this == abstractC0065c) {
            if (this.h) {
                throw new IllegalStateException("stream has already been operated upon or closed");
            }
            this.h = true;
            j$.util.u uVar = abstractC0065c.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractC0065c.g = null;
            return uVar;
        }
        throw new IllegalStateException();
    }

    abstract j$.util.u K0(AbstractC0192y2 abstractC0192y2, j$.util.function.y yVar, boolean z);

    @Override // j$.util.stream.AbstractC0089g, java.lang.AutoCloseable
    public void close() {
        this.h = true;
        this.g = null;
        AbstractC0065c abstractC0065c = this.a;
        Runnable runnable = abstractC0065c.j;
        if (runnable != null) {
            abstractC0065c.j = null;
            runnable.run();
        }
    }

    @Override // j$.util.stream.AbstractC0089g
    public final boolean isParallel() {
        return this.a.k;
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final void n0(AbstractC0129m3 abstractC0129m3, j$.util.u uVar) {
        abstractC0129m3.getClass();
        if (EnumC0076d4.SHORT_CIRCUIT.d(this.f)) {
            o0(abstractC0129m3, uVar);
            return;
        }
        abstractC0129m3.n(uVar.getExactSizeIfKnown());
        uVar.forEachRemaining(abstractC0129m3);
        abstractC0129m3.m();
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final void o0(AbstractC0129m3 abstractC0129m3, j$.util.u uVar) {
        AbstractC0065c abstractC0065c = this;
        while (abstractC0065c.e > 0) {
            abstractC0065c = abstractC0065c.b;
        }
        abstractC0129m3.n(uVar.getExactSizeIfKnown());
        abstractC0065c.A0(uVar, abstractC0129m3);
        abstractC0129m3.m();
    }

    @Override // j$.util.stream.AbstractC0089g
    public AbstractC0089g onClose(Runnable runnable) {
        AbstractC0065c abstractC0065c = this.a;
        Runnable runnable2 = abstractC0065c.j;
        if (runnable2 != null) {
            runnable = new M4(runnable2, runnable);
        }
        abstractC0065c.j = runnable;
        return this;
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final A1 p0(j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        if (this.a.k) {
            return z0(this, uVar, z, mVar);
        }
        AbstractC0161s1 t0 = t0(q0(uVar), mVar);
        t0.getClass();
        n0(v0(t0), uVar);
        return t0.mo70a();
    }

    public final AbstractC0089g parallel() {
        this.a.k = true;
        return this;
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final long q0(j$.util.u uVar) {
        if (EnumC0076d4.SIZED.d(this.f)) {
            return uVar.getExactSizeIfKnown();
        }
        return -1L;
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final EnumC0082e4 r0() {
        AbstractC0065c abstractC0065c = this;
        while (abstractC0065c.e > 0) {
            abstractC0065c = abstractC0065c.b;
        }
        return abstractC0065c.B0();
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final int s0() {
        return this.f;
    }

    public final AbstractC0089g sequential() {
        this.a.k = false;
        return this;
    }

    public j$.util.u spliterator() {
        if (!this.h) {
            this.h = true;
            AbstractC0065c abstractC0065c = this.a;
            if (this != abstractC0065c) {
                return K0(this, new C0059b(this), abstractC0065c.k);
            }
            j$.util.u uVar = abstractC0065c.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractC0065c.g = null;
            return uVar;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final AbstractC0129m3 u0(AbstractC0129m3 abstractC0129m3, j$.util.u uVar) {
        abstractC0129m3.getClass();
        n0(v0(abstractC0129m3), uVar);
        return abstractC0129m3;
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final AbstractC0129m3 v0(AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        for (AbstractC0065c abstractC0065c = this; abstractC0065c.e > 0; abstractC0065c = abstractC0065c.b) {
            abstractC0129m3 = abstractC0065c.H0(abstractC0065c.b.f, abstractC0129m3);
        }
        return abstractC0129m3;
    }

    @Override // j$.util.stream.AbstractC0192y2
    public final j$.util.u w0(j$.util.u uVar) {
        return this.e == 0 ? uVar : K0(this, new C0059b(uVar), this.a.k);
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
            AbstractC0065c abstractC0065c = this.b;
            return E0(abstractC0065c, abstractC0065c.I0(0), mVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    abstract A1 z0(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, boolean z, j$.util.function.m mVar);
}
