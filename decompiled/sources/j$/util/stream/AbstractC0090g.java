package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g */
/* loaded from: classes2.dex */
public interface AbstractC0090g extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator mo66iterator();

    AbstractC0090g onClose(Runnable runnable);

    AbstractC0090g parallel();

    AbstractC0090g sequential();

    j$.util.u spliterator();

    AbstractC0090g unordered();
}
