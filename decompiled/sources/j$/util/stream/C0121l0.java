package j$.util.stream;
/* renamed from: j$.util.stream.l0 */
/* loaded from: classes2.dex */
public final class C0121l0 extends AbstractC0139o0 implements AbstractC0118k3 {
    final j$.util.function.l b;

    public C0121l0(j$.util.function.l lVar, boolean z) {
        super(z);
        this.b = lVar;
    }

    @Override // j$.util.stream.AbstractC0139o0, j$.util.stream.AbstractC0130m3
    public void accept(int i) {
        this.b.accept(i);
    }

    /* renamed from: e */
    public /* synthetic */ void accept(Integer num) {
        AbstractC0140o1.b(this, num);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
