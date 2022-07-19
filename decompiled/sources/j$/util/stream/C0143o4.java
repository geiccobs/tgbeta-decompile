package j$.util.stream;

import j$.util.AbstractC0039a;
import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.o4 */
/* loaded from: classes2.dex */
public final class C0143o4 extends AbstractC0089f4 implements j$.util.t {
    public C0143o4(AbstractC0193y2 abstractC0193y2, j$.util.function.y yVar, boolean z) {
        super(abstractC0193y2, yVar, z);
    }

    C0143o4(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z) {
        super(abstractC0193y2, uVar, z);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.j(this, consumer);
    }

    @Override // j$.util.t
    /* renamed from: e */
    public void forEachRemaining(j$.util.function.f fVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(fVar));
            return;
        }
        fVar.getClass();
        h();
        this.b.u0(new C0137n4(fVar), this.d);
        this.i = true;
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.b(this, consumer);
    }

    @Override // j$.util.stream.AbstractC0089f4
    void j() {
        U3 u3 = new U3();
        this.h = u3;
        this.e = this.b.v0(new C0137n4(u3));
        this.f = new C0060b(this);
    }

    @Override // j$.util.t
    /* renamed from: k */
    public boolean tryAdvance(j$.util.function.f fVar) {
        fVar.getClass();
        boolean a = a();
        if (a) {
            U3 u3 = (U3) this.h;
            long j = this.g;
            int w = u3.w(j);
            fVar.accept((u3.c == 0 && w == 0) ? ((double[]) u3.e)[(int) j] : ((double[][]) u3.f)[w][(int) (j - u3.d[w])]);
        }
        return a;
    }

    @Override // j$.util.stream.AbstractC0089f4
    AbstractC0089f4 l(j$.util.u uVar) {
        return new C0143o4(this.b, uVar, this.a);
    }

    @Override // j$.util.stream.AbstractC0089f4, j$.util.u
    public j$.util.t trySplit() {
        return (j$.util.t) super.trySplit();
    }
}
