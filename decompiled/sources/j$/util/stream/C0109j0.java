package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.j0 */
/* loaded from: classes2.dex */
final class C0109j0 extends AbstractC0072d {
    private final C0073d0 j;

    public C0109j0(C0073d0 c0073d0, AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        super(abstractC0193y2, uVar);
        this.j = c0073d0;
    }

    C0109j0(C0109j0 c0109j0, j$.util.u uVar) {
        super(c0109j0, uVar);
        this.j = c0109j0.j;
    }

    private void m(Object obj) {
        boolean z;
        C0109j0 c0109j0 = this;
        while (true) {
            if (c0109j0 != null) {
                AbstractC0084f c = c0109j0.c();
                if (c != null && c.d != c0109j0) {
                    z = false;
                    break;
                }
                c0109j0 = c;
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

    @Override // j$.util.stream.AbstractC0084f
    public Object a() {
        AbstractC0193y2 abstractC0193y2 = this.a;
        O4 o4 = (O4) this.j.e.get();
        abstractC0193y2.u0(o4, this.b);
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

    @Override // j$.util.stream.AbstractC0084f
    public AbstractC0084f f(j$.util.u uVar) {
        return new C0109j0(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0072d
    protected Object k() {
        return this.j.c;
    }

    @Override // j$.util.stream.AbstractC0084f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            C0109j0 c0109j0 = (C0109j0) this.d;
            C0109j0 c0109j02 = null;
            while (true) {
                if (c0109j0 != c0109j02) {
                    Object b = c0109j0.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    c0109j02 = c0109j0;
                    c0109j0 = (C0109j0) this.e;
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
