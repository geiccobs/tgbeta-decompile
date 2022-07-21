package j$.util;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.stream.Stream;
/* renamed from: j$.util.b */
/* loaded from: classes2.dex */
public interface AbstractC0040b extends j$.lang.e {
    @Override // j$.lang.e
    void forEach(Consumer consumer);

    boolean k(Predicate predicate);

    @Override // j$.lang.e
    /* renamed from: spliterator */
    u mo71spliterator();

    Stream stream();
}
