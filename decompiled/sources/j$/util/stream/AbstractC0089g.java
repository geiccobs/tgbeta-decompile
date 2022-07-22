package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g */
/* loaded from: classes2.dex */
public interface AbstractC0089g extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator mo66iterator();

    AbstractC0089g onClose(Runnable runnable);

    AbstractC0089g parallel();

    AbstractC0089g sequential();

    j$.util.u spliterator();

    AbstractC0089g unordered();
}
