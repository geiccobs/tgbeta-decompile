package j$.util;

import java.util.LinkedHashSet;
/* loaded from: classes2.dex */
public class DesugarLinkedHashSet {
    private DesugarLinkedHashSet() {
    }

    public static <E> Spliterator<E> spliterator(LinkedHashSet<E> instance) {
        return Spliterators.spliterator(instance, 17);
    }
}
