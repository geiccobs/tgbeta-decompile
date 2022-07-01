package j$.wrappers;

import j$.util.stream.AbstractC0084g;
import java.util.Iterator;
import java.util.stream.BaseStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class H0 implements AbstractC0084g {
    final /* synthetic */ BaseStream a;

    private /* synthetic */ H0(BaseStream baseStream) {
        this.a = baseStream;
    }

    public static /* synthetic */ AbstractC0084g n0(BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof I0 ? ((I0) baseStream).a : new H0(baseStream);
    }

    @Override // j$.util.stream.AbstractC0084g, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // j$.util.stream.AbstractC0084g
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // j$.util.stream.AbstractC0084g
    /* renamed from: iterator */
    public /* synthetic */ Iterator mo66iterator() {
        return this.a.iterator();
    }

    @Override // j$.util.stream.AbstractC0084g
    public /* synthetic */ AbstractC0084g onClose(Runnable runnable) {
        return n0(this.a.onClose(runnable));
    }

    @Override // j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    public /* synthetic */ AbstractC0084g parallel() {
        return n0(this.a.parallel());
    }

    @Override // j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    public /* synthetic */ AbstractC0084g sequential() {
        return n0(this.a.sequential());
    }

    @Override // j$.util.stream.AbstractC0084g
    public /* synthetic */ j$.util.u spliterator() {
        return C0206g.a(this.a.spliterator());
    }

    @Override // j$.util.stream.AbstractC0084g
    public /* synthetic */ AbstractC0084g unordered() {
        return n0(this.a.unordered());
    }
}
