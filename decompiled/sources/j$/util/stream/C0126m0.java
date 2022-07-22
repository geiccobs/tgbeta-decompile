package j$.util.stream;
/* renamed from: j$.util.stream.m0 */
/* loaded from: classes2.dex */
public final class C0126m0 extends AbstractC0138o0 implements AbstractC0123l3 {
    final j$.util.function.q b;

    public C0126m0(j$.util.function.q qVar, boolean z) {
        super(z);
        this.b = qVar;
    }

    @Override // j$.util.stream.AbstractC0138o0, j$.util.stream.AbstractC0129m3, j$.util.stream.AbstractC0123l3, j$.util.function.q
    public void accept(long j) {
        this.b.accept(j);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Long l) {
        AbstractC0139o1.c(this, l);
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }
}
