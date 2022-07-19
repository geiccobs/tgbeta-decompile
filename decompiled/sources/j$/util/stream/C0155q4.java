package j$.util.stream;

import j$.util.AbstractC0039a;
import j$.util.function.Consumer;
import j$.util.u;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.q4 */
/* loaded from: classes2.dex */
public final class C0155q4 extends AbstractC0089f4 implements u.a {
    public C0155q4(AbstractC0193y2 abstractC0193y2, j$.util.function.y yVar, boolean z) {
        super(abstractC0193y2, yVar, z);
    }

    C0155q4(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z) {
        super(abstractC0193y2, uVar, z);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.k(this, consumer);
    }

    @Override // j$.util.u.a
    /* renamed from: c */
    public void forEachRemaining(j$.util.function.l lVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(lVar));
            return;
        }
        lVar.getClass();
        h();
        this.b.u0(new C0149p4(lVar), this.d);
        this.i = true;
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.c(this, consumer);
    }

    @Override // j$.util.u.a
    /* renamed from: g */
    public boolean tryAdvance(j$.util.function.l lVar) {
        lVar.getClass();
        boolean a = a();
        if (a) {
            W3 w3 = (W3) this.h;
            long j = this.g;
            int w = w3.w(j);
            lVar.accept((w3.c == 0 && w == 0) ? ((int[]) w3.e)[(int) j] : ((int[][]) w3.f)[w][(int) (j - w3.d[w])]);
        }
        return a;
    }

    @Override // j$.util.stream.AbstractC0089f4
    void j() {
        W3 w3 = new W3();
        this.h = w3;
        this.e = this.b.v0(new C0149p4(w3));
        this.f = new C0060b(this);
    }

    @Override // j$.util.stream.AbstractC0089f4
    AbstractC0089f4 l(j$.util.u uVar) {
        return new C0155q4(this.b, uVar, this.a);
    }

    @Override // j$.util.stream.AbstractC0089f4, j$.util.u
    public u.a trySplit() {
        return (u.a) super.trySplit();
    }
}
