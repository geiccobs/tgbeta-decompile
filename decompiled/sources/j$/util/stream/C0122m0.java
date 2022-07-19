package j$.util.stream;
/* renamed from: j$.util.stream.m0 */
/* loaded from: classes2.dex */
public final class C0122m0 extends AbstractC0134o0 implements AbstractC0119l3 {
    final j$.util.function.q b;

    public C0122m0(j$.util.function.q qVar, boolean z) {
        super(z);
        this.b = qVar;
    }

    @Override // j$.util.stream.AbstractC0134o0, j$.util.stream.AbstractC0125m3, j$.util.stream.AbstractC0119l3, j$.util.function.q
    public void accept(long j) {
        this.b.accept(j);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Long l) {
        AbstractC0135o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
