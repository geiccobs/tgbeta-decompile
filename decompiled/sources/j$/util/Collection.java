package j$.util;

import j$.lang.Iterable;
import j$.util.SortedSet;
import j$.util.function.Consumer;
import j$.util.function.IntFunction;
import j$.util.function.Predicate;
import j$.util.stream.Stream;
import j$.util.stream.StreamSupport;
import java.util.LinkedHashSet;
/* loaded from: classes2.dex */
public interface Collection<E> extends Iterable<E> {

    /* renamed from: j$.util.Collection$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void forEach(java.util.Collection collection, Consumer consumer) {
            if (collection instanceof Collection) {
                ((Collection) collection).forEach(consumer);
            } else {
                CC.$default$forEach(collection, consumer);
            }
        }

        public static /* synthetic */ Stream parallelStream(java.util.Collection collection) {
            return collection instanceof Collection ? ((Collection) collection).parallelStream() : CC.$default$parallelStream(collection);
        }

        public static /* synthetic */ boolean removeIf(java.util.Collection collection, Predicate predicate) {
            return collection instanceof Collection ? ((Collection) collection).removeIf(predicate) : CC.$default$removeIf(collection, predicate);
        }

        public static /* synthetic */ Spliterator spliterator(java.util.Collection collection) {
            return collection instanceof Collection ? ((Collection) collection).spliterator() : collection instanceof LinkedHashSet ? DesugarLinkedHashSet.spliterator((LinkedHashSet) collection) : collection instanceof java.util.SortedSet ? SortedSet.CC.$default$spliterator((java.util.SortedSet) collection) : collection instanceof java.util.Set ? Spliterators.spliterator((java.util.Set) collection, 1) : collection instanceof java.util.List ? Spliterators.spliterator((java.util.List) collection, 16) : Spliterators.spliterator(collection, 0);
        }

        public static /* synthetic */ Stream stream(java.util.Collection collection) {
            return collection instanceof Collection ? ((Collection) collection).stream() : CC.$default$stream(collection);
        }

        public static /* synthetic */ Object[] toArray(java.util.Collection collection, IntFunction intFunction) {
            return collection instanceof Collection ? ((Collection) collection).toArray(intFunction) : CC.$default$toArray(collection, intFunction);
        }
    }

    boolean add(E e);

    boolean addAll(java.util.Collection<? extends E> collection);

    void clear();

    boolean contains(Object obj);

    boolean containsAll(java.util.Collection<?> collection);

    boolean equals(Object obj);

    @Override // j$.lang.Iterable
    void forEach(Consumer<? super E> consumer);

    int hashCode();

    boolean isEmpty();

    @Override // j$.lang.Iterable
    java.util.Iterator<E> iterator();

    Stream<E> parallelStream();

    boolean remove(Object obj);

    boolean removeAll(java.util.Collection<?> collection);

    boolean removeIf(Predicate<? super E> predicate);

    boolean retainAll(java.util.Collection<?> collection);

    int size();

    @Override // j$.lang.Iterable
    Spliterator<E> spliterator();

    Stream<E> stream();

    Object[] toArray();

    <T> T[] toArray(IntFunction<T[]> intFunction);

    <T> T[] toArray(T[] tArr);

    /* renamed from: j$.util.Collection$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.IntFunction != java.util.function.IntFunction<T[]> */
        public static Object[] $default$toArray(java.util.Collection _this, IntFunction intFunction) {
            return _this.toArray((Object[]) intFunction.apply(0));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super E> */
        public static boolean $default$removeIf(java.util.Collection _this, Predicate predicate) {
            if (DesugarCollections.SYNCHRONIZED_COLLECTION.isInstance(_this)) {
                return DesugarCollections.removeIf(_this, predicate);
            }
            predicate.getClass();
            boolean removed = false;
            java.util.Iterator<E> each = _this.iterator();
            while (each.hasNext()) {
                if (predicate.test(each.next())) {
                    each.remove();
                    removed = true;
                }
            }
            return removed;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E> */
        public static void $default$forEach(java.util.Collection _this, Consumer consumer) {
            consumer.getClass();
            for (E t : _this) {
                consumer.accept(t);
            }
        }

        public static Stream $default$stream(java.util.Collection _this) {
            return StreamSupport.stream(EL.spliterator(_this), false);
        }

        public static Stream $default$parallelStream(java.util.Collection _this) {
            return StreamSupport.stream(EL.spliterator(_this), true);
        }
    }
}
