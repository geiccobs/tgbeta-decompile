package j$.util.stream;

import j$.wrappers.C0219j0;
import j$.wrappers.C0223l0;
import j$.wrappers.C0227n0;
/* loaded from: classes2.dex */
class Z0 extends AbstractC0100h3 {
    public final /* synthetic */ int b = 4;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(K k, AbstractC0130m3 abstractC0130m3) {
        super(abstractC0130m3);
        this.c = k;
    }

    @Override // j$.util.stream.AbstractC0124l3, j$.util.function.q
    public void accept(long j) {
        switch (this.b) {
            case 0:
                this.a.accept(j);
                return;
            case 1:
                this.a.accept(((j$.util.function.t) ((N) this.c).m).applyAsLong(j));
                return;
            case 2:
                this.a.accept((AbstractC0130m3) ((j$.util.function.r) ((L) this.c).m).apply(j));
                return;
            case 3:
                this.a.accept(((C0227n0) ((M) this.c).m).a(j));
                return;
            case 4:
                this.a.accept(((C0223l0) ((K) this.c).m).a(j));
                return;
            case 5:
                AbstractC0080e1 abstractC0080e1 = (AbstractC0080e1) ((j$.util.function.r) ((N) this.c).m).apply(j);
                if (abstractC0080e1 != null) {
                    try {
                        abstractC0080e1.sequential().d(new W0(this));
                    } finally {
                        try {
                            abstractC0080e1.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (abstractC0080e1 == null) {
                    return;
                }
                return;
            case 6:
                if (!((C0219j0) ((N) this.c).m).b(j)) {
                    return;
                }
                this.a.accept(j);
                return;
            default:
                ((j$.util.function.q) ((N) this.c).m).accept(j);
                this.a.accept(j);
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0130m3
    public void n(long j) {
        switch (this.b) {
            case 5:
                this.a.n(-1L);
                return;
            case 6:
                this.a.n(-1L);
                return;
            default:
                this.a.n(j);
                return;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(L l, AbstractC0130m3 abstractC0130m3) {
        super(abstractC0130m3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(M m, AbstractC0130m3 abstractC0130m3) {
        super(abstractC0130m3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, AbstractC0130m3 abstractC0130m3) {
        super(abstractC0130m3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, AbstractC0130m3 abstractC0130m3, j$.lang.a aVar) {
        super(abstractC0130m3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, AbstractC0130m3 abstractC0130m3, j$.lang.b bVar) {
        super(abstractC0130m3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(N n, AbstractC0130m3 abstractC0130m3, j$.lang.c cVar) {
        super(abstractC0130m3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Z0(O o, AbstractC0130m3 abstractC0130m3) {
        super(abstractC0130m3);
        this.c = o;
    }
}
