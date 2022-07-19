package j$.util.stream;
/* renamed from: j$.util.stream.a1 */
/* loaded from: classes2.dex */
public class C0056a1 extends AbstractC0074d1 {
    public C0056a1(j$.util.u uVar, int i, boolean z) {
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

    @Override // j$.util.stream.AbstractC0074d1, j$.util.stream.AbstractC0080e1
    public void Z(j$.util.function.q qVar) {
        j$.util.v M0;
        if (!isParallel()) {
            M0 = AbstractC0074d1.M0(J0());
            M0.d(qVar);
            return;
        }
        qVar.getClass();
        x0(new C0127m0(qVar, true));
    }

    @Override // j$.util.stream.AbstractC0074d1, j$.util.stream.AbstractC0080e1
    public void d(j$.util.function.q qVar) {
        j$.util.v M0;
        if (isParallel()) {
            super.d(qVar);
            return;
        }
        M0 = AbstractC0074d1.M0(J0());
        M0.d(qVar);
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0080e1 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0080e1 sequential() {
        sequential();
        return this;
    }
}
