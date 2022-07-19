package j$.util.stream;
/* renamed from: j$.util.stream.k0 */
/* loaded from: classes2.dex */
public final class C0110k0 extends AbstractC0134o0 implements AbstractC0107j3 {
    final j$.util.function.f b;

    public C0110k0(j$.util.function.f fVar, boolean z) {
        super(z);
        this.b = fVar;
    }

    @Override // j$.util.stream.AbstractC0134o0, j$.util.stream.AbstractC0125m3
    public void accept(double d) {
        this.b.accept(d);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Double d) {
        AbstractC0135o1.a(this, d);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
