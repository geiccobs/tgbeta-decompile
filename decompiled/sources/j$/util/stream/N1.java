package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* loaded from: classes2.dex */
class N1 extends AbstractC0083f {
    protected final AbstractC0192y2 h;
    protected final j$.util.function.r i;
    protected final j$.util.function.b j;

    N1(N1 n1, j$.util.u uVar) {
        super(n1, uVar);
        this.h = n1.h;
        this.i = n1.i;
        this.j = n1.j;
    }

    public N1(AbstractC0192y2 abstractC0192y2, j$.util.u uVar, j$.util.function.r rVar, j$.util.function.b bVar) {
        super(abstractC0192y2, uVar);
        this.h = abstractC0192y2;
        this.i = rVar;
        this.j = bVar;
    }

    @Override // j$.util.stream.AbstractC0083f
    public Object a() {
        AbstractC0161s1 abstractC0161s1 = (AbstractC0161s1) this.i.apply(this.h.q0(this.b));
        this.h.u0(abstractC0161s1, this.b);
        return abstractC0161s1.mo70a();
    }

    @Override // j$.util.stream.AbstractC0083f
    public AbstractC0083f f(j$.util.u uVar) {
        return new N1(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0083f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            g((A1) this.j.apply((A1) ((N1) this.d).b(), (A1) ((N1) this.e).b()));
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
