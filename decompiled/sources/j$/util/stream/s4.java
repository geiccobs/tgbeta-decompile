package j$.util.stream;

import j$.util.AbstractC0039a;
import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class s4 extends AbstractC0089f4 implements j$.util.v {
    public s4(AbstractC0193y2 abstractC0193y2, j$.util.function.y yVar, boolean z) {
        super(abstractC0193y2, yVar, z);
    }

    s4(AbstractC0193y2 abstractC0193y2, j$.util.u uVar, boolean z) {
        super(abstractC0193y2, uVar, z);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.l(this, consumer);
    }

    @Override // j$.util.v
    /* renamed from: d */
    public void forEachRemaining(j$.util.function.q qVar) {
        if (this.h != null || this.i) {
            do {
            } while (tryAdvance(qVar));
            return;
        }
        qVar.getClass();
        h();
        this.b.u0(new r4(qVar), this.d);
        this.i = true;
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.d(this, consumer);
    }

    @Override // j$.util.v
    /* renamed from: i */
    public boolean tryAdvance(j$.util.function.q qVar) {
        qVar.getClass();
        boolean a = a();
        if (a) {
            Y3 y3 = (Y3) this.h;
            long j = this.g;
            int w = y3.w(j);
            qVar.accept((y3.c == 0 && w == 0) ? ((long[]) y3.e)[(int) j] : ((long[][]) y3.f)[w][(int) (j - y3.d[w])]);
        }
        return a;
    }

    @Override // j$.util.stream.AbstractC0089f4
    void j() {
        Y3 y3 = new Y3();
        this.h = y3;
        this.e = this.b.v0(new r4(y3));
        this.f = new C0060b(this);
    }

    @Override // j$.util.stream.AbstractC0089f4
    AbstractC0089f4 l(j$.util.u uVar) {
        return new s4(this.b, uVar, this.a);
    }

    @Override // j$.util.stream.AbstractC0089f4, j$.util.u
    public j$.util.v trySplit() {
        return (j$.util.v) super.trySplit();
    }
}
