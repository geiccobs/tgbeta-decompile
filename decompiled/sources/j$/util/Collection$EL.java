package j$.util;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
/* renamed from: j$.util.Collection$-EL */
/* loaded from: classes2.dex */
public final /* synthetic */ class Collection$EL {
    public static /* synthetic */ void a(Collection collection, Consumer consumer) {
        if (collection instanceof AbstractC0035b) {
            ((AbstractC0035b) collection).forEach(consumer);
        } else {
            AbstractC0034a.a(collection, consumer);
        }
    }

    public static u b(Collection collection) {
        if (collection instanceof AbstractC0035b) {
            return ((AbstractC0035b) collection).mo71spliterator();
        }
        if (collection instanceof LinkedHashSet) {
            LinkedHashSet linkedHashSet = (LinkedHashSet) collection;
            linkedHashSet.getClass();
            return new J(linkedHashSet, 17);
        } else if (collection instanceof SortedSet) {
            SortedSet sortedSet = (SortedSet) collection;
            return new s(sortedSet, sortedSet, 21);
        } else if (collection instanceof Set) {
            Set set = (Set) collection;
            set.getClass();
            return new J(set, 1);
        } else if (collection instanceof List) {
            List list = (List) collection;
            list.getClass();
            return new J(list, 16);
        } else {
            collection.getClass();
            return new J(collection, 0);
        }
    }

    public static /* synthetic */ boolean removeIf(Collection collection, Predicate predicate) {
        return collection instanceof AbstractC0035b ? ((AbstractC0035b) collection).k(predicate) : AbstractC0034a.h(collection, predicate);
    }
}
