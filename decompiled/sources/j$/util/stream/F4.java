package j$.util.stream;

import j$.util.AbstractC0039a;
import j$.util.function.Consumer;
import j$.util.u;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class F4 extends H4 implements u.a, j$.util.function.l {
    int e;

    public F4(u.a aVar, long j, long j2) {
        super(aVar, j, j2);
    }

    F4(u.a aVar, F4 f4) {
        super(aVar, f4);
    }

    @Override // j$.util.function.l
    public void accept(int i) {
        this.e = i;
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.k(this, consumer);
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.c(this, consumer);
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }

    @Override // j$.util.stream.J4
    protected j$.util.u q(j$.util.u uVar) {
        return new F4((u.a) uVar, this);
    }

    @Override // j$.util.stream.H4
    protected void s(Object obj) {
        ((j$.util.function.l) obj).accept(this.e);
    }

    @Override // j$.util.stream.H4
    protected AbstractC0113j4 t(int i) {
        return new C0101h4(i);
    }
}
