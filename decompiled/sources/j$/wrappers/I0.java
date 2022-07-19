package j$.wrappers;

import j$.util.stream.AbstractC0090g;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;
/* loaded from: classes2.dex */
public final /* synthetic */ class I0 implements BaseStream {
    final /* synthetic */ AbstractC0090g a;

    private /* synthetic */ I0(AbstractC0090g abstractC0090g) {
        this.a = abstractC0090g;
    }

    public static /* synthetic */ BaseStream n0(AbstractC0090g abstractC0090g) {
        if (abstractC0090g == null) {
            return null;
        }
        return abstractC0090g instanceof H0 ? ((H0) abstractC0090g).a : new I0(abstractC0090g);
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
        return C0214h.a(this.a.spliterator());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream unordered() {
        return n0(this.a.unordered());
    }
}
