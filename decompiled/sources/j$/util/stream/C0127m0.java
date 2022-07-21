package j$.util.stream;
/* renamed from: j$.util.stream.m0 */
/* loaded from: classes2.dex */
public final class C0127m0 extends AbstractC0139o0 implements AbstractC0124l3 {
    final j$.util.function.q b;

    public C0127m0(j$.util.function.q qVar, boolean z) {
        super(z);
        this.b = qVar;
    }

    @Override // j$.util.stream.AbstractC0139o0, j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
    public void accept(long j) {
        this.b.accept(j);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Long l) {
        AbstractC0140o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
