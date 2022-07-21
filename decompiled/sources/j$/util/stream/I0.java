package j$.util.stream;

import j$.util.u;
/* loaded from: classes2.dex */
public class I0 extends L0 {
    public I0(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractC0066c
    final boolean G0() {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0066c
    public final AbstractC0130m3 H0(int i, AbstractC0130m3 abstractC0130m3) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.L0, j$.util.stream.IntStream
    public void I(j$.util.function.l lVar) {
        u.a M0;
        if (!isParallel()) {
            M0 = L0.M0(J0());
            M0.c(lVar);
            return;
        }
        lVar.getClass();
        x0(new C0121l0(lVar, true));
    }

    @Override // j$.util.stream.L0, j$.util.stream.IntStream
    public void U(j$.util.function.l lVar) {
        u.a M0;
        if (isParallel()) {
            super.U(lVar);
            return;
        }
        M0 = L0.M0(J0());
        M0.c(lVar);
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ IntStream parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ IntStream sequential() {
        sequential();
        return this;
    }
}
