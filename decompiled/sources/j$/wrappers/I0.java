package j$.wrappers;

import j$.util.stream.AbstractC0084g;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class I0 implements BaseStream {
    final /* synthetic */ AbstractC0084g a;

    private /* synthetic */ I0(AbstractC0084g abstractC0084g) {
        this.a = abstractC0084g;
    }

    public static /* synthetic */ BaseStream n0(AbstractC0084g abstractC0084g) {
        if (abstractC0084g == null) {
            return null;
        }
        return abstractC0084g instanceof H0 ? ((H0) abstractC0084g).a : new I0(abstractC0084g);
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.a.close();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.a.isParallel();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Iterator iterator() {
        return this.a.mo66iterator();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return n0(this.a.onClose(runnable));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream parallel() {
        return n0(this.a.parallel());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream sequential() {
        return n0(this.a.sequential());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Spliterator spliterator() {
        return C0208h.a(this.a.spliterator());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream unordered() {
        return n0(this.a.unordered());
    }
}
