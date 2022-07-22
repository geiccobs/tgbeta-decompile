package j$.util.stream;

import j$.util.function.Function;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes2.dex */
class r extends AbstractC0105i3 {
    public final /* synthetic */ int b = 3;
    Object c;
    final /* synthetic */ Object d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(C0159s c0159s, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.d = c0159s;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        IntStream intStream;
        switch (this.b) {
            case 0:
                if (((Set) this.c).contains(obj)) {
                    return;
                }
                ((Set) this.c).add(obj);
                this.a.accept((AbstractC0129m3) obj);
                return;
            case 1:
                intStream = (AbstractC0079e1) ((Function) ((N) this.d).m).apply(obj);
                if (intStream != null) {
                    try {
                        intStream.sequential().d((j$.util.function.q) this.c);
                    } finally {
                        try {
                            intStream.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (intStream == null) {
                    return;
                }
                intStream.close();
                return;
            case 2:
                intStream = (IntStream) ((Function) ((M) this.d).m).apply(obj);
                if (intStream != null) {
                    try {
                        intStream.sequential().U((j$.util.function.l) this.c);
                    } finally {
                    }
                }
                if (intStream == null) {
                    return;
                }
                return;
            default:
                U u = (U) ((Function) ((K) this.d).m).apply(obj);
                if (u != null) {
                    try {
                        u.sequential().j((j$.util.function.f) this.c);
                    } finally {
                        try {
                            u.close();
                        } catch (Throwable unused2) {
                        }
                    }
                }
                if (u == null) {
                    return;
                }
                u.close();
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0105i3, j$.util.stream.AbstractC0129m3
    public void m() {
        switch (this.b) {
            case 0:
                this.c = null;
                this.a.m();
                return;
            default:
                this.a.m();
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        switch (this.b) {
            case 0:
                this.c = new HashSet();
                this.a.n(-1L);
                return;
            case 1:
                this.a.n(-1L);
                return;
            case 2:
                this.a.n(-1L);
                return;
            default:
                this.a.n(-1L);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(K k, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.d = k;
        this.c = new F(abstractC0129m3);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(M m, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.d = m;
        this.c = new B0(abstractC0129m3);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(N n, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.d = n;
        this.c = new W0(abstractC0129m3);
    }
}
