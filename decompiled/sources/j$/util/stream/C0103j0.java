package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.j0 */
/* loaded from: classes2.dex */
final class C0103j0 extends AbstractC0066d {
    private final C0067d0 j;

    public C0103j0(C0067d0 c0067d0, AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        super(abstractC0187y2, uVar);
        this.j = c0067d0;
    }

    C0103j0(C0103j0 c0103j0, j$.util.u uVar) {
        super(c0103j0, uVar);
        this.j = c0103j0.j;
    }

    private void m(Object obj) {
        boolean z;
        C0103j0 c0103j0 = this;
        while (true) {
            if (c0103j0 != null) {
                AbstractC0078f c = c0103j0.c();
                if (c != null && c.d != c0103j0) {
                    z = false;
                    break;
                }
                c0103j0 = c;
            } else {
                z = true;
                break;
            }
        }
        if (z) {
            l(obj);
        } else {
            j();
        }
    }

    @Override // j$.util.stream.AbstractC0078f
    public Object a() {
        AbstractC0187y2 abstractC0187y2 = this.a;
        O4 o4 = (O4) this.j.e.get();
        abstractC0187y2.u0(o4, this.b);
        Object obj = o4.get();
        if (!this.j.b) {
            if (obj != null) {
                l(obj);
            }
            return null;
        } else if (obj == null) {
            return null;
        } else {
            m(obj);
            return obj;
        }
    }

    @Override // j$.util.stream.AbstractC0078f
    public AbstractC0078f f(j$.util.u uVar) {
        return new C0103j0(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0066d
    protected Object k() {
        return this.j.c;
    }

    @Override // j$.util.stream.AbstractC0078f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            C0103j0 c0103j0 = (C0103j0) this.d;
            C0103j0 c0103j02 = null;
            while (true) {
                if (c0103j0 != c0103j02) {
                    Object b = c0103j0.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    c0103j02 = c0103j0;
                    c0103j0 = (C0103j0) this.e;
                } else {
                    break;
                }
            }
        }
        this.b = null;
        this.e = null;
        this.d = null;
    }
}
