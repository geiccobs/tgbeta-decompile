package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.ToIntFunction;
/* loaded from: classes2.dex */
class Y2 extends AbstractC0105i3 {
    public final /* synthetic */ int b = 5;
    final /* synthetic */ Object c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(K k, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.c = k;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.b) {
            case 0:
                ((Consumer) ((L) this.c).m).accept(obj);
                this.a.accept((AbstractC0129m3) obj);
                return;
            case 1:
                if (!((Predicate) ((L) this.c).m).test(obj)) {
                    return;
                }
                this.a.accept((AbstractC0129m3) obj);
                return;
            case 2:
                this.a.accept((AbstractC0129m3) ((C0057a3) this.c).m.apply(obj));
                return;
            case 3:
                this.a.accept(((ToIntFunction) ((M) this.c).m).applyAsInt(obj));
                return;
            case 4:
                this.a.accept(((j$.util.function.A) ((N) this.c).m).applyAsLong(obj));
                return;
            case 5:
                this.a.accept(((j$.util.function.z) ((K) this.c).m).applyAsDouble(obj));
                return;
            default:
                Stream stream = (Stream) ((C0057a3) this.c).m.apply(obj);
                if (stream != null) {
                    try {
                        ((Stream) stream.sequential()).forEach(this.a);
                    } finally {
                        try {
                            stream.close();
                        } catch (Throwable unused) {
                        }
                    }
                }
                if (stream == null) {
                    return;
                }
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        switch (this.b) {
            case 1:
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
    public Y2(L l, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(L l, AbstractC0129m3 abstractC0129m3, j$.lang.a aVar) {
        super(abstractC0129m3);
        this.c = l;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(M m, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.c = m;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(N n, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.c = n;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(C0057a3 c0057a3, AbstractC0129m3 abstractC0129m3) {
        super(abstractC0129m3);
        this.c = c0057a3;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public Y2(C0057a3 c0057a3, AbstractC0129m3 abstractC0129m3, j$.lang.a aVar) {
        super(abstractC0129m3);
        this.c = c0057a3;
    }
}
