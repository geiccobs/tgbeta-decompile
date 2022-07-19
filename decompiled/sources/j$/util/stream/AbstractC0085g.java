package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g */
/* loaded from: classes2.dex */
public interface AbstractC0085g extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator mo66iterator();

    AbstractC0085g onClose(Runnable runnable);

    AbstractC0085g parallel();

    AbstractC0085g sequential();

    j$.util.u spliterator();

    AbstractC0085g unordered();
}
