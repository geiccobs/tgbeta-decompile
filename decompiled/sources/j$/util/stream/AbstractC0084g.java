package j$.util.stream;

import java.util.Iterator;
/* renamed from: j$.util.stream.g */
/* loaded from: classes2.dex */
public interface AbstractC0084g extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    boolean isParallel();

    /* renamed from: iterator */
    Iterator mo66iterator();

    AbstractC0084g onClose(Runnable runnable);

    AbstractC0084g parallel();

    AbstractC0084g sequential();

    j$.util.u spliterator();

    AbstractC0084g unordered();
}
