package j$.util;

import j$.util.SortedSet;
import java.util.LinkedHashSet;
/* loaded from: classes2.dex */
public interface Set<E> extends Collection<E> {

    /* renamed from: j$.util.Set$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Spliterator spliterator(java.util.Set set) {
            return set instanceof Set ? ((Set) set).spliterator() : set instanceof LinkedHashSet ? DesugarLinkedHashSet.spliterator((LinkedHashSet) set) : set instanceof java.util.SortedSet ? SortedSet.CC.$default$spliterator((java.util.SortedSet) set) : Spliterators.spliterator(set, 1);
        }
    }

    @Override // j$.util.Collection
    boolean add(E e);

    @Override // j$.util.Collection
    boolean addAll(java.util.Collection<? extends E> collection);

    @Override // j$.util.Collection
    void clear();

    @Override // j$.util.Collection
    boolean contains(Object obj);

    @Override // j$.util.Collection
    boolean containsAll(java.util.Collection<?> collection);

    @Override // j$.util.Collection
    boolean equals(Object obj);

    @Override // j$.util.Collection
    int hashCode();

    @Override // j$.util.Collection
    boolean isEmpty();

    @Override // j$.util.Collection, j$.lang.Iterable
    java.util.Iterator<E> iterator();

    @Override // j$.util.Collection
    boolean remove(Object obj);

    @Override // j$.util.Collection
    boolean removeAll(java.util.Collection<?> collection);

    @Override // j$.util.Collection
    boolean retainAll(java.util.Collection<?> collection);

    @Override // j$.util.Collection
    int size();

    @Override // j$.util.Collection, j$.lang.Iterable
    Spliterator<E> spliterator();

    @Override // j$.util.Collection
    Object[] toArray();

    @Override // j$.util.Collection
    <T> T[] toArray(T[] tArr);

    /* renamed from: j$.util.Set$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Spliterator $default$spliterator(java.util.Set _this) {
            return Spliterators.spliterator(_this, 1);
        }
    }
}
