package j$.util.stream;

import java.util.concurrent.CountedCompleter;
/* renamed from: j$.util.stream.j0 */
/* loaded from: classes2.dex */
final class C0104j0 extends AbstractC0067d {
    private final C0068d0 j;

    public C0104j0(C0068d0 c0068d0, AbstractC0188y2 abstractC0188y2, j$.util.u uVar) {
        super(abstractC0188y2, uVar);
        this.j = c0068d0;
    }

    C0104j0(C0104j0 c0104j0, j$.util.u uVar) {
        super(c0104j0, uVar);
        this.j = c0104j0.j;
    }

    private void m(Object obj) {
        boolean z;
        C0104j0 c0104j0 = this;
        while (true) {
            if (c0104j0 != null) {
                AbstractC0079f c = c0104j0.c();
                if (c != null && c.d != c0104j0) {
                    z = false;
                    break;
                }
                c0104j0 = c;
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

    @Override // j$.util.stream.AbstractC0079f
    public Object a() {
        AbstractC0188y2 abstractC0188y2 = this.a;
        O4 o4 = (O4) this.j.e.get();
        abstractC0188y2.u0(o4, this.b);
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

    @Override // j$.util.stream.AbstractC0079f
    public AbstractC0079f f(j$.util.u uVar) {
        return new C0104j0(this, uVar);
    }

    @Override // j$.util.stream.AbstractC0067d
    protected Object k() {
        return this.j.c;
    }

    @Override // j$.util.stream.AbstractC0079f, java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter countedCompleter) {
        if (this.j.b) {
            C0104j0 c0104j0 = (C0104j0) this.d;
            C0104j0 c0104j02 = null;
            while (true) {
                if (c0104j0 != c0104j02) {
                    Object b = c0104j0.b();
                    if (b != null && this.j.d.test(b)) {
                        g(b);
                        m(b);
                        break;
                    }
                    c0104j02 = c0104j0;
                    c0104j0 = (C0104j0) this.e;
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
