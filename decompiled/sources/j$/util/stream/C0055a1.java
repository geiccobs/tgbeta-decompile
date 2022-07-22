package j$.util.stream;
/* renamed from: j$.util.stream.a1 */
/* loaded from: classes2.dex */
public class C0055a1 extends AbstractC0073d1 {
    public C0055a1(j$.util.u uVar, int i, boolean z) {
        super(uVar, i, z);
    }

    @Override // j$.util.stream.AbstractC0065c
    final boolean G0() {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0065c
    public final AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        throw new UnsupportedOperationException();
    }

    @Override // j$.util.stream.AbstractC0073d1, j$.util.stream.AbstractC0079e1
    public void Z(j$.util.function.q qVar) {
        j$.util.v M0;
        if (!isParallel()) {
            M0 = AbstractC0073d1.M0(J0());
            M0.d(qVar);
            return;
        }
        qVar.getClass();
        x0(new C0126m0(qVar, true));
    }

    @Override // j$.util.stream.AbstractC0073d1, j$.util.stream.AbstractC0079e1
    public void d(j$.util.function.q qVar) {
        j$.util.v M0;
        if (isParallel()) {
            super.d(qVar);
            return;
        }
        M0 = AbstractC0073d1.M0(J0());
        M0.d(qVar);
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0079e1 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0079e1 sequential() {
        sequential();
        return this;
    }
}
