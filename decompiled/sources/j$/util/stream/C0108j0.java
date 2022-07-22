package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.j0 */
/* loaded from: classes2.dex */
final class C0108j0 extends AbstractC0071d {
    private final C0072d0 j;

    public C0108j0(C0072d0 c0072d0, AbstractC0192y2 abstractC0192y2, j$.util.u uVar) {
        super(abstractC0192y2, uVar);
        this.j = c0072d0;
    }

    C0108j0(C0108j0 c0108j0, j$.util.u uVar) {
        super(c0108j0, uVar);
        this.j = c0108j0.j;
    }

    private void m(Object obj) {
        boolean z;
        C0108j0 c0108j0 = this;
        while (true) {
            if (c0108j0 != null) {
                AbstractC0083f c = c0108j0.c();
                if (c != null && c.d != c0108j0) {
                    z = false;
                    break;
                }
                c0108j0 = c;
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

    @Override // j$.util.stream.AbstractC0083f
    public Object a() {
        AbstractC0192y2 abstractC0192y2 = this.a;
        O4 o4 = (O4) this.j.e.get();
        abstractC0192y2.u0(o4, this.b);
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

    @Override // j$.util.stream.AbstractC0083f
    public AbstractC0083f f(j$.util.u uVar) {
        return new C0108j0(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0071d
    protected Object k() {
        return this.j.c;
    }

    @Override // j$.util.stream.AbstractC0083f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            C0108j0 c0108j0 = (C0108j0) this.d;
            C0108j0 c0108j02 = null;
            while (true) {
                if (c0108j0 != c0108j02) {
                    Object b = c0108j0.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    c0108j02 = c0108j0;
                    c0108j0 = (C0108j0) this.e;
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
