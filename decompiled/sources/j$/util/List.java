package j$.util;

import j$.util.function.UnaryOperator;
import java.util.Arrays;
import java.util.ListIterator;
/* loaded from: classes2.dex */
public interface List<E> extends Collection<E> {

    /* renamed from: j$.util.List$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void replaceAll(java.util.List list, UnaryOperator unaryOperator) {
            if (list instanceof List) {
                ((List) list).replaceAll(unaryOperator);
            } else {
                CC.$default$replaceAll(list, unaryOperator);
            }
        }

        public static /* synthetic */ void sort(java.util.List list, java.util.Comparator comparator) {
            if (list instanceof List) {
                ((List) list).sort(comparator);
            } else {
                CC.$default$sort(list, comparator);
            }
        }

        public static /* synthetic */ Spliterator spliterator(java.util.List list) {
            return list instanceof List ? ((List) list).spliterator() : Spliterators.spliterator(list, 16);
        }
    }

    void add(int i, E e);

    @Override // j$.util.Collection
    boolean add(E e);

    boolean addAll(int i, java.util.Collection<? extends E> collection);

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

    E get(int i);

    @Override // j$.util.Collection
    int hashCode();

    int indexOf(Object obj);

    @Override // j$.util.Collection
    boolean isEmpty();

    @Override // j$.util.Collection, j$.lang.Iterable
    java.util.Iterator<E> iterator();

    int lastIndexOf(Object obj);

    ListIterator<E> listIterator();

    ListIterator<E> listIterator(int i);

    E remove(int i);

    @Override // j$.util.Collection
    boolean remove(Object obj);

    @Override // j$.util.Collection
    boolean removeAll(java.util.Collection<?> collection);

    void replaceAll(UnaryOperator<E> unaryOperator);

    @Override // j$.util.Collection
    boolean retainAll(java.util.Collection<?> collection);

    E set(int i, E e);

    @Override // j$.util.Collection
    int size();

    void sort(java.util.Comparator<? super E> comparator);

    @Override // j$.util.Collection, j$.lang.Iterable
    Spliterator<E> spliterator();

    java.util.List<E> subList(int i, int i2);

    @Override // j$.util.Collection
    Object[] toArray();

    @Override // j$.util.Collection
    <T> T[] toArray(T[] tArr);

    /* renamed from: j$.util.List$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.UnaryOperator != java.util.function.UnaryOperator<E> */
        public static void $default$replaceAll(java.util.List _this, UnaryOperator unaryOperator) {
            if (DesugarCollections.SYNCHRONIZED_LIST.isInstance(_this)) {
                DesugarCollections.replaceAll(_this, unaryOperator);
                return;
            }
            unaryOperator.getClass();
            ListIterator<E> li = _this.listIterator();
            while (li.hasNext()) {
                li.set((E) unaryOperator.apply(li.next()));
            }
        }

        public static void $default$sort(java.util.List _this, java.util.Comparator comparator) {
            if (DesugarCollections.SYNCHRONIZED_LIST.isInstance(_this)) {
                DesugarCollections.sort(_this, comparator);
                return;
            }
            Object[] a = _this.toArray();
            Arrays.sort(a, comparator);
            ListIterator<E> i = _this.listIterator();
            for (Object e : a) {
                i.next();
                i.set(e);
            }
        }

        public static Spliterator $default$spliterator(java.util.List _this) {
            return Spliterators.spliterator(_this, 16);
        }
    }
}
