package j$.util;

import j$.util.Spliterators;
/* loaded from: classes2.dex */
public interface SortedSet<E> extends Set<E> {

    /* renamed from: j$.util.SortedSet$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Spliterator spliterator(java.util.SortedSet sortedSet) {
            return sortedSet instanceof SortedSet ? ((SortedSet) sortedSet).spliterator() : CC.$default$spliterator(sortedSet);
        }
    }

    java.util.Comparator<? super E> comparator();

    E first();

    java.util.SortedSet<E> headSet(E e);

    E last();

    @Override // j$.util.Set, j$.util.Collection, j$.lang.Iterable
    Spliterator<E> spliterator();

    java.util.SortedSet<E> subSet(E e, E e2);

    java.util.SortedSet<E> tailSet(E e);

    /* renamed from: j$.util.SortedSet$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Spliterator $default$spliterator(final java.util.SortedSet _this) {
            return new Spliterators.IteratorSpliterator<E>(_this, 21) { // from class: j$.util.SortedSet.1
                @Override // j$.util.Spliterators.IteratorSpliterator, j$.util.Spliterator
                public java.util.Comparator<? super E> getComparator() {
                    return _this.comparator();
                }
            };
        }
    }
}
