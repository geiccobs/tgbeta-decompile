package j$.wrappers;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;
/* renamed from: j$.wrappers.$r8$wrapper$java$util$stream$BaseStream$-WRP */
/* loaded from: classes2.dex */
public final /* synthetic */ class C$r8$wrapper$java$util$stream$BaseStream$WRP implements BaseStream {
    final /* synthetic */ j$.util.stream.BaseStream wrappedValue;

    private /* synthetic */ C$r8$wrapper$java$util$stream$BaseStream$WRP(j$.util.stream.BaseStream baseStream) {
        this.wrappedValue = baseStream;
    }

    public static /* synthetic */ BaseStream convert(j$.util.stream.BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof C$r8$wrapper$java$util$stream$BaseStream$VWRP ? ((C$r8$wrapper$java$util$stream$BaseStream$VWRP) baseStream).wrappedValue : new C$r8$wrapper$java$util$stream$BaseStream$WRP(baseStream);
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public /* synthetic */ void close() {
        this.wrappedValue.close();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ boolean isParallel() {
        return this.wrappedValue.isParallel();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Iterator iterator() {
        return this.wrappedValue.iterator();
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return convert(this.wrappedValue.onClose(runnable));
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream parallel() {
        return convert(this.wrappedValue.parallel());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream sequential() {
        return convert(this.wrappedValue.sequential());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ Spliterator spliterator() {
        return C$r8$wrapper$java$util$Spliterator$WRP.convert(this.wrappedValue.spliterator());
    }

    @Override // java.util.stream.BaseStream
    public /* synthetic */ BaseStream unordered() {
        return convert(this.wrappedValue.unordered());
    }
}
