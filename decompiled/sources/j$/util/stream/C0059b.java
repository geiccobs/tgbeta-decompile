package j$.util.stream;

import j$.util.function.Consumer;
import java.util.List;
/* renamed from: j$.util.stream.b */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0059b implements j$.util.function.y, j$.util.function.r, Consumer, j$.util.function.c {
    public final /* synthetic */ int a = 2;
    public final /* synthetic */ Object b;

    public /* synthetic */ C0059b(j$.util.u uVar) {
        this.b = uVar;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        switch (this.a) {
            case 3:
                ((AbstractC0129m3) this.b).accept((AbstractC0129m3) obj);
                return;
            default:
                ((List) this.b).add(obj);
                return;
        }
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 3:
                return consumer.getClass();
            default:
                return consumer.getClass();
        }
    }

    @Override // j$.util.function.r
    public Object apply(long j) {
        int i = H1.k;
        return AbstractC0187x2.d(j, (j$.util.function.m) this.b);
    }

    @Override // j$.util.function.y
    public Object get() {
        switch (this.a) {
            case 0:
                return (j$.util.u) this.b;
            default:
                return ((AbstractC0065c) this.b).D0();
        }
    }

    public /* synthetic */ C0059b(j$.util.function.m mVar) {
        this.b = mVar;
    }

    public /* synthetic */ C0059b(AbstractC0065c abstractC0065c) {
        this.b = abstractC0065c;
    }
}
