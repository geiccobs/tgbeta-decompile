package j$.lang;

import j$.util.Collection;
import j$.util.DesugarCollections;
import j$.util.DesugarLinkedHashSet;
import j$.util.SortedSet;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.Consumer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
/* loaded from: classes2.dex */
public interface Iterable<T> {

    /* renamed from: j$.lang.Iterable$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void forEach(java.lang.Iterable iterable, Consumer consumer) {
            if (iterable instanceof Iterable) {
                ((Iterable) iterable).forEach(consumer);
            } else if (iterable instanceof Collection) {
                Collection.CC.$default$forEach((java.util.Collection) iterable, consumer);
            } else {
                CC.$default$forEach(iterable, consumer);
            }
        }

        public static /* synthetic */ Spliterator spliterator(java.lang.Iterable iterable) {
            return iterable instanceof Iterable ? ((Iterable) iterable).spliterator() : iterable instanceof LinkedHashSet ? DesugarLinkedHashSet.spliterator((LinkedHashSet) iterable) : iterable instanceof SortedSet ? SortedSet.CC.$default$spliterator((java.util.SortedSet) iterable) : iterable instanceof Set ? Spliterators.spliterator((Set) iterable, 1) : iterable instanceof List ? Spliterators.spliterator((List) iterable, 16) : iterable instanceof java.util.Collection ? Spliterators.spliterator((java.util.Collection) iterable, 0) : CC.$default$spliterator(iterable);
        }
    }

    void forEach(Consumer<? super T> consumer);

    Iterator<T> iterator();

    Spliterator<T> spliterator();

    /* renamed from: j$.lang.Iterable$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super T> */
        public static void $default$forEach(java.lang.Iterable _this, Consumer consumer) {
            if (DesugarCollections.SYNCHRONIZED_COLLECTION.isInstance(_this)) {
                DesugarCollections.forEach(_this, consumer);
                return;
            }
            consumer.getClass();
            for (T t : _this) {
                consumer.accept(t);
            }
        }

        public static Spliterator $default$spliterator(java.lang.Iterable _this) {
            return Spliterators.spliteratorUnknownSize(_this.iterator(), 0);
        }
    }
}
