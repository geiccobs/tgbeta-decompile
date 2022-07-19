package j$.util.stream;

import j$.util.AbstractC0034a;
import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class E4 extends H4 implements j$.util.t, j$.util.function.f {
    double e;

    public E4(j$.util.t tVar, long j, long j2) {
        super(tVar, j, j2);
    }

    E4(j$.util.t tVar, E4 e4) {
        super(tVar, e4);
    }

    @Override // j$.util.function.f
    public void accept(double d) {
        this.e = d;
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0034a.j(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0034a.b(this, consumer);
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }

    @Override // j$.util.stream.J4
    protected j$.util.u q(j$.util.u uVar) {
        return new E4((j$.util.t) uVar, this);
    }

    @Override // j$.util.stream.H4
    protected void s(Object obj) {
        ((j$.util.function.f) obj).accept(this.e);
    }

    @Override // j$.util.stream.H4
    protected AbstractC0108j4 t(int i) {
        return new C0090g4(i);
    }
}
