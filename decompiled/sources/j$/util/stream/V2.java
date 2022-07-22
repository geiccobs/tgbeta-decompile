package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class V2 extends AbstractC0083f {
    private final U2 h;

    public V2(U2 u2, AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        super(abstractC0192y2, uVar);
        this.h = u2;
    }

    V2(V2 v2, j$.util.u uVar) {
        super(v2, uVar);
        this.h = v2.h;
    }

    @Override // j$.util.stream.AbstractC0083f
    public Object a() {
        AbstractC0192y2 abstractC0192y2 = this.a;
        S2 a = this.h.a();
        abstractC0192y2.u0(a, this.b);
        return a;
    }

    @Override // j$.util.stream.AbstractC0083f
    public AbstractC0083f f(j$.util.u uVar) {
        return new V2(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0083f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (!d()) {
            S2 s2 = (S2) ((V2) this.d).b();
            s2.h((S2) ((V2) this.e).b());
            g(s2);
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
