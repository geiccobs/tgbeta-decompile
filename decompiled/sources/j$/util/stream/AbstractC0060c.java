package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.c */
/* loaded from: classes2.dex */
public abstract class AbstractC0060c extends AbstractC0187y2 implements AbstractC0084g {
    private final AbstractC0060c a;
    private final AbstractC0060c b;
    protected final int c;
    private AbstractC0060c d;
    private int e;
    private int f;
    private j$.util.u g;
    private boolean h;
    private boolean i;
    private Runnable j;
    private boolean k;

    public AbstractC0060c(AbstractC0060c abstractC0060c, int i) {
        if (!abstractC0060c.h) {
            abstractC0060c.h = true;
            abstractC0060c.d = this;
            this.b = abstractC0060c;
            this.c = EnumC0071d4.h & i;
            this.f = EnumC0071d4.a(i, abstractC0060c.f);
            AbstractC0060c abstractC0060c2 = abstractC0060c.a;
            this.a = abstractC0060c2;
            if (G0()) {
                abstractC0060c2.i = true;
            }
            this.e = abstractC0060c.e + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    public AbstractC0060c(j$.util.u uVar, int i, boolean z) {
        this.b = null;
        this.g = uVar;
        this.a = this;
        int i2 = EnumC0071d4.g & i;
        this.c = i2;
        this.f = ((i2 << 1) ^ (-1)) & EnumC0071d4.l;
        this.e = 0;
        this.k = z;
    }

    private j$.util.u I0(int i) {
        int i2;
        int i3;
        AbstractC0060c abstractC0060c = this.a;
        j$.util.u uVar = abstractC0060c.g;
        if (uVar != null) {
            abstractC0060c.g = null;
            if (abstractC0060c.k && abstractC0060c.i) {
                AbstractC0060c abstractC0060c2 = abstractC0060c.d;
                int i4 = 1;
                while (abstractC0060c != this) {
                    int i5 = abstractC0060c2.c;
                    if (abstractC0060c2.G0()) {
                        i4 = 0;
                        if (EnumC0071d4.SHORT_CIRCUIT.d(i5)) {
                            i5 &= EnumC0071d4.u ^ (-1);
                        }
                        uVar = abstractC0060c2.F0(abstractC0060c, uVar);
                        if (uVar.hasCharacteristics(64)) {
                            i3 = i5 & (EnumC0071d4.t ^ (-1));
                            i2 = EnumC0071d4.s;
                        } else {
                            i3 = i5 & (EnumC0071d4.s ^ (-1));
                            i2 = EnumC0071d4.t;
                        }
                        i5 = i3 | i2;
                    }
                    abstractC0060c2.e = i4;
                    abstractC0060c2.f = EnumC0071d4.a(i5, abstractC0060c.f);
                    i4++;
                    AbstractC0060c abstractC0060c3 = abstractC0060c2;
                    abstractC0060c2 = abstractC0060c2.d;
                    abstractC0060c = abstractC0060c3;
                }
            }
            if (i != 0) {
                this.f = EnumC0071d4.a(i, this.f);
            }
            return uVar;
        }
        throw new IllegalStateException("source already consumed or closed");
    }

    abstract void A0(j$.util.u uVar, AbstractC0124m3 abstractC0124m3);

    public abstract EnumC0077e4 B0();

    public final boolean C0() {
        return EnumC0071d4.ORDERED.d(this.f);
    }

    public /* synthetic */ j$.util.u D0() {
        return I0(0);
    }

    A1 E0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.m mVar) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    j$.util.u F0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        return E0(abstractC0187y2, uVar, C0048a.a).mo69spliterator();
    }

    abstract boolean G0();

    public abstract AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3);

    public final j$.util.u J0() {
        AbstractC0060c abstractC0060c = this.a;
        if (this == abstractC0060c) {
            if (this.h) {
                throw new IllegalStateException("stream has already been operated upon or closed");
            }
            this.h = true;
            j$.util.u uVar = abstractC0060c.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractC0060c.g = null;
            return uVar;
        }
        throw new IllegalStateException();
    }

    abstract j$.util.u K0(AbstractC0187y2 abstractC0187y2, j$.util.function.y yVar, boolean z);

    @Override // j$.util.stream.AbstractC0084g, java.lang.AutoCloseable
    public void close() {
        this.h = true;
        this.g = null;
        AbstractC0060c abstractC0060c = this.a;
        Runnable runnable = abstractC0060c.j;
        if (runnable != null) {
            abstractC0060c.j = null;
            runnable.run();
        }
    }

    @Override // j$.util.stream.AbstractC0084g
    public final boolean isParallel() {
        return this.a.k;
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final void n0(AbstractC0124m3 abstractC0124m3, j$.util.u uVar) {
        abstractC0124m3.getClass();
        if (EnumC0071d4.SHORT_CIRCUIT.d(this.f)) {
            o0(abstractC0124m3, uVar);
            return;
        }
        abstractC0124m3.n(uVar.getExactSizeIfKnown());
        uVar.forEachRemaining(abstractC0124m3);
        abstractC0124m3.m();
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final void o0(AbstractC0124m3 abstractC0124m3, j$.util.u uVar) {
        AbstractC0060c abstractC0060c = this;
        while (abstractC0060c.e > 0) {
            abstractC0060c = abstractC0060c.b;
        }
        abstractC0124m3.n(uVar.getExactSizeIfKnown());
        abstractC0060c.A0(uVar, abstractC0124m3);
        abstractC0124m3.m();
    }

    @Override // j$.util.stream.AbstractC0084g
    public AbstractC0084g onClose(Runnable runnable) {
        AbstractC0060c abstractC0060c = this.a;
        Runnable runnable2 = abstractC0060c.j;
        if (runnable2 != null) {
            runnable = new M4(runnable2, runnable);
        }
        abstractC0060c.j = runnable;
        return this;
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final A1 p0(j$.util.u uVar, boolean z, j$.util.function.m mVar) {
        if (this.a.k) {
            return z0(this, uVar, z, mVar);
        }
        AbstractC0156s1 t0 = t0(q0(uVar), mVar);
        t0.getClass();
        n0(v0(t0), uVar);
        return t0.mo70a();
    }

    public final AbstractC0084g parallel() {
        this.a.k = true;
        return this;
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final long q0(j$.util.u uVar) {
        if (EnumC0071d4.SIZED.d(this.f)) {
            return uVar.getExactSizeIfKnown();
        }
        return -1L;
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final EnumC0077e4 r0() {
        AbstractC0060c abstractC0060c = this;
        while (abstractC0060c.e > 0) {
            abstractC0060c = abstractC0060c.b;
        }
        return abstractC0060c.B0();
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final int s0() {
        return this.f;
    }

    public final AbstractC0084g sequential() {
        this.a.k = false;
        return this;
    }

    public j$.util.u spliterator() {
        if (!this.h) {
            this.h = true;
            AbstractC0060c abstractC0060c = this.a;
            if (this != abstractC0060c) {
                return K0(this, new C0054b(this), abstractC0060c.k);
            }
            j$.util.u uVar = abstractC0060c.g;
            if (uVar == null) {
                throw new IllegalStateException("source already consumed or closed");
            }
            abstractC0060c.g = null;
            return uVar;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final AbstractC0124m3 u0(AbstractC0124m3 abstractC0124m3, j$.util.u uVar) {
        abstractC0124m3.getClass();
        n0(v0(abstractC0124m3), uVar);
        return abstractC0124m3;
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final AbstractC0124m3 v0(AbstractC0124m3 abstractC0124m3) {
        abstractC0124m3.getClass();
        for (AbstractC0060c abstractC0060c = this; abstractC0060c.e > 0; abstractC0060c = abstractC0060c.b) {
            abstractC0124m3 = abstractC0060c.H0(abstractC0060c.b.f, abstractC0124m3);
        }
        return abstractC0124m3;
    }

    @Override // j$.util.stream.AbstractC0187y2
    public final j$.util.u w0(j$.util.u uVar) {
        return this.e == 0 ? uVar : K0(this, new C0054b(uVar), this.a.k);
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
            AbstractC0060c abstractC0060c = this.b;
            return E0(abstractC0060c, abstractC0060c.I0(0), mVar);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    abstract A1 z0(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z, j$.util.function.m mVar);
}
