package j$.util;

import j$.util.function.Consumer;
/* loaded from: classes2.dex */
public interface Iterator<E> {

    /* renamed from: j$.util.Iterator$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ void forEachRemaining(java.util.Iterator it, Consumer consumer) {
            if (it instanceof Iterator) {
                ((Iterator) it).forEachRemaining(consumer);
            } else {
                CC.$default$forEachRemaining(it, consumer);
            }
        }

        public static /* synthetic */ void remove(java.util.Iterator it) {
            if (it instanceof Iterator) {
                ((Iterator) it).remove();
            } else {
                CC.$default$remove(it);
            }
        }
    }

    void forEachRemaining(Consumer<? super E> consumer);

    boolean hasNext();

    E next();

    void remove();

    /* renamed from: j$.util.Iterator$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static void $default$remove(java.util.Iterator _this) {
            throw new UnsupportedOperationException("remove");
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E> */
        public static void $default$forEachRemaining(java.util.Iterator _this, Consumer consumer) {
            consumer.getClass();
            while (_this.hasNext()) {
                consumer.accept(_this.next());
            }
        }
    }
}
