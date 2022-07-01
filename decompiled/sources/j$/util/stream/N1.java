package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* loaded from: classes2.dex */
class N1 extends AbstractC0078f {
    protected final AbstractC0187y2 h;
    protected final j$.util.function.r i;
    protected final j$.util.function.b j;

    N1(N1 n1, j$.util.u uVar) {
        super(n1, uVar);
        this.h = n1.h;
        this.i = n1.i;
        this.j = n1.j;
    }

    public N1(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, j$.util.function.r rVar, j$.util.function.b bVar) {
        super(abstractC0187y2, uVar);
        this.h = abstractC0187y2;
        this.i = rVar;
        this.j = bVar;
    }

    @Override // j$.util.stream.AbstractC0078f
    public Object a() {
        AbstractC0156s1 abstractC0156s1 = (AbstractC0156s1) this.i.apply(this.h.q0(this.b));
        this.h.u0(abstractC0156s1, this.b);
        return abstractC0156s1.mo70a();
    }

    @Override // j$.util.stream.AbstractC0078f
    public AbstractC0078f f(j$.util.u uVar) {
        return new N1(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0078f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            g((A1) this.j.apply((A1) ((N1) this.d).b(), (A1) ((N1) this.e).b()));
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
