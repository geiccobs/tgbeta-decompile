package j$.util;

import j$.util.Collection;
import j$.util.List;
import j$.util.Map;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.UnaryOperator;
import j$.wrappers.C$r8$wrapper$java$util$function$BiConsumer$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$BiFunction$VWRP;
import j$.wrappers.C$r8$wrapper$java$util$function$Function$VWRP;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes2.dex */
public class DesugarCollections {
    private static final Field COLLECTION_FIELD;
    private static final Field MUTEX_FIELD;
    public static final Class<? extends java.util.Collection> SYNCHRONIZED_COLLECTION;
    private static final Constructor<? extends java.util.Collection> SYNCHRONIZED_COLLECTION_CONSTRUCTOR;
    static final Class<? extends java.util.List> SYNCHRONIZED_LIST = Collections.synchronizedList(new LinkedList()).getClass();
    private static final Constructor<? extends java.util.Set> SYNCHRONIZED_SET_CONSTRUCTOR;

    private DesugarCollections() {
    }

    static {
        Class cls = Collections.synchronizedCollection(new ArrayList()).getClass();
        SYNCHRONIZED_COLLECTION = cls;
        Field field = getField(cls, "mutex");
        MUTEX_FIELD = field;
        if (field != null) {
            field.setAccessible(true);
        }
        Field field2 = getField(cls, Theme.COLOR_BACKGROUND_SLUG);
        COLLECTION_FIELD = field2;
        if (field2 != null) {
            field2.setAccessible(true);
        }
        Constructor<? extends java.util.Set> constructor = getConstructor(Collections.synchronizedSet(new HashSet()).getClass(), java.util.Set.class, Object.class);
        SYNCHRONIZED_SET_CONSTRUCTOR = constructor;
        if (constructor != null) {
            constructor.setAccessible(true);
        }
        Constructor<? extends java.util.Collection> constructor2 = getConstructor(cls, java.util.Collection.class, Object.class);
        SYNCHRONIZED_COLLECTION_CONSTRUCTOR = constructor2;
        if (constructor2 != null) {
            constructor2.setAccessible(true);
        }
    }

    private static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static <E> Constructor<? extends E> getConstructor(Class<? extends E> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Predicate != java.util.function.Predicate<? super E> */
    public static <E> boolean removeIf(java.util.Collection<E> collection, Predicate<? super E> predicate) {
        IllegalAccessException e;
        boolean removeIf;
        Field field = MUTEX_FIELD;
        if (field == null) {
            try {
                return Collection.EL.removeIf((java.util.Collection) COLLECTION_FIELD.get(collection), predicate);
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection removeIf fall-back.", e2);
            }
        }
        try {
            synchronized (field.get(collection)) {
                try {
                    removeIf = Collection.EL.removeIf((java.util.Collection) COLLECTION_FIELD.get(collection), predicate);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (IllegalAccessException e3) {
                        e = e3;
                        throw new Error("Runtime illegal access in synchronized collection removeIf.", e);
                    }
                }
            }
            return removeIf;
        } catch (IllegalAccessException e4) {
            e = e4;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.Consumer != java.util.function.Consumer<? super E> */
    public static <E> void forEach(Iterable<E> iterable, Consumer<? super E> consumer) {
        IllegalAccessException e;
        Field field = MUTEX_FIELD;
        if (field == null) {
            try {
                Collection.EL.forEach((java.util.Collection) COLLECTION_FIELD.get(iterable), consumer);
                return;
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection forEach fall-back.", e2);
            }
        }
        try {
            synchronized (field.get(iterable)) {
                try {
                    Collection.EL.forEach((java.util.Collection) COLLECTION_FIELD.get(iterable), consumer);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (IllegalAccessException e3) {
                        e = e3;
                        throw new Error("Runtime illegal access in synchronized collection forEach.", e);
                    }
                }
            }
        } catch (IllegalAccessException e4) {
            e = e4;
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.function.UnaryOperator != java.util.function.UnaryOperator<E> */
    public static <E> void replaceAll(java.util.List<E> list, UnaryOperator<E> unaryOperator) {
        IllegalAccessException e;
        Field field = MUTEX_FIELD;
        if (field == null) {
            try {
                List.EL.replaceAll((java.util.List) COLLECTION_FIELD.get(list), unaryOperator);
                return;
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized list replaceAll fall-back.", e2);
            }
        }
        try {
            synchronized (field.get(list)) {
                try {
                    List.EL.replaceAll((java.util.List) COLLECTION_FIELD.get(list), unaryOperator);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (IllegalAccessException e3) {
                        e = e3;
                        throw new Error("Runtime illegal access in synchronized list replaceAll.", e);
                    }
                }
            }
        } catch (IllegalAccessException e4) {
            e = e4;
        }
    }

    public static <E> void sort(java.util.List<E> list, java.util.Comparator<? super E> comparator) {
        IllegalAccessException e;
        Field field = MUTEX_FIELD;
        if (field == null) {
            try {
                List.EL.sort((java.util.List) COLLECTION_FIELD.get(list), comparator);
                return;
            } catch (IllegalAccessException e2) {
                throw new Error("Runtime illegal access in synchronized collection sort fall-back.", e2);
            }
        }
        try {
            synchronized (field.get(list)) {
                try {
                    List.EL.sort((java.util.List) COLLECTION_FIELD.get(list), comparator);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (IllegalAccessException e3) {
                        e = e3;
                        throw new Error("Runtime illegal access in synchronized list sort.", e);
                    }
                }
            }
        } catch (IllegalAccessException e4) {
            e = e4;
        }
    }

    public static <K, V> java.util.Map<K, V> synchronizedMap(java.util.Map<K, V> m) {
        return new SynchronizedMap(m);
    }

    /* loaded from: classes2.dex */
    public static class SynchronizedMap<K, V> implements java.util.Map<K, V>, Serializable, Map<K, V> {
        private static final long serialVersionUID = 1978198479659022715L;
        private transient java.util.Set<Map.Entry<K, V>> entrySet;
        private transient java.util.Set<K> keySet;
        private final java.util.Map<K, V> m;
        final Object mutex;
        private transient java.util.Collection<V> values;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map
        public /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
            return compute((SynchronizedMap<K, V>) obj, (j$.util.function.BiFunction<? super SynchronizedMap<K, V>, ? super V, ? extends V>) C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map
        public /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
            return computeIfAbsent((SynchronizedMap<K, V>) obj, (j$.util.function.Function<? super SynchronizedMap<K, V>, ? extends V>) C$r8$wrapper$java$util$function$Function$VWRP.convert(function));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map
        public /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
            return computeIfPresent((SynchronizedMap<K, V>) obj, (j$.util.function.BiFunction<? super SynchronizedMap<K, V>, ? super V, ? extends V>) C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        @Override // java.util.Map
        public /* synthetic */ void forEach(BiConsumer biConsumer) {
            forEach(C$r8$wrapper$java$util$function$BiConsumer$VWRP.convert(biConsumer));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.Map
        public /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
            return merge((SynchronizedMap<K, V>) obj, obj2, C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        @Override // java.util.Map
        public /* synthetic */ void replaceAll(BiFunction biFunction) {
            replaceAll(C$r8$wrapper$java$util$function$BiFunction$VWRP.convert(biFunction));
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        SynchronizedMap(java.util.Map<K, V> m) {
            m.getClass();
            this.m = m;
            this.mutex = this;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        SynchronizedMap(java.util.Map<K, V> m, Object mutex) {
            this.m = m;
            this.mutex = mutex;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.m.size();
            }
            return size;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public boolean isEmpty() {
            boolean isEmpty;
            synchronized (this.mutex) {
                isEmpty = this.m.isEmpty();
            }
            return isEmpty;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public boolean containsKey(Object key) {
            boolean containsKey;
            synchronized (this.mutex) {
                containsKey = this.m.containsKey(key);
            }
            return containsKey;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public boolean containsValue(Object value) {
            boolean containsValue;
            synchronized (this.mutex) {
                containsValue = this.m.containsValue(value);
            }
            return containsValue;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public V get(Object key) {
            V v;
            synchronized (this.mutex) {
                v = this.m.get(key);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public V put(K key, V value) {
            V put;
            synchronized (this.mutex) {
                put = this.m.put(key, value);
            }
            return put;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public V remove(Object key) {
            V remove;
            synchronized (this.mutex) {
                remove = this.m.remove(key);
            }
            return remove;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public void putAll(java.util.Map<? extends K, ? extends V> map) {
            synchronized (this.mutex) {
                this.m.putAll(map);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public void clear() {
            synchronized (this.mutex) {
                this.m.clear();
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        private <T> java.util.Set<T> instantiateSet(java.util.Set<T> set, Object mutex) {
            if (DesugarCollections.SYNCHRONIZED_SET_CONSTRUCTOR == null) {
                return Collections.synchronizedSet(set);
            }
            try {
                return (java.util.Set) DesugarCollections.SYNCHRONIZED_SET_CONSTRUCTOR.newInstance(set, mutex);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new Error("Unable to instantiate a synchronized list.", e);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        private <T> java.util.Collection<T> instantiateCollection(java.util.Collection<T> collection, Object mutex) {
            if (DesugarCollections.SYNCHRONIZED_COLLECTION_CONSTRUCTOR == null) {
                return Collections.synchronizedCollection(collection);
            }
            try {
                return (java.util.Collection) DesugarCollections.SYNCHRONIZED_COLLECTION_CONSTRUCTOR.newInstance(collection, mutex);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new Error("Unable to instantiate a synchronized list.", e);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public java.util.Set<K> keySet() {
            java.util.Set<K> set;
            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = (java.util.Set<K>) instantiateSet((java.util.Set<K>) this.m.keySet(), this.mutex);
                }
                set = this.keySet;
            }
            return set;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public java.util.Set<Map.Entry<K, V>> entrySet() {
            java.util.Set<Map.Entry<K, V>> set;
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = (java.util.Set<Map.Entry<K, V>>) instantiateSet((java.util.Set<Map.Entry<K, V>>) this.m.entrySet(), this.mutex);
                }
                set = this.entrySet;
            }
            return set;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public java.util.Collection<V> values() {
            java.util.Collection<V> collection;
            synchronized (this.mutex) {
                if (this.values == null) {
                    this.values = (java.util.Collection<V>) instantiateCollection((java.util.Collection<V>) this.m.values(), this.mutex);
                }
                collection = this.values;
            }
            return collection;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public boolean equals(Object o) {
            boolean equals;
            if (this == o) {
                return true;
            }
            synchronized (this.mutex) {
                equals = this.m.equals(o);
            }
            return equals;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.m.hashCode();
            }
            return hashCode;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        public String toString() {
            String obj;
            synchronized (this.mutex) {
                obj = this.m.toString();
            }
            return obj;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public V getOrDefault(Object k, V defaultValue) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.getOrDefault(this.m, k, defaultValue);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<? super K, ? super V> */
        @Override // j$.util.Map
        public void forEach(j$.util.function.BiConsumer<? super K, ? super V> biConsumer) {
            synchronized (this.mutex) {
                Map.EL.forEach(this.m, biConsumer);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super K, ? super V, ? extends V> */
        @Override // j$.util.Map
        public void replaceAll(j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
            synchronized (this.mutex) {
                Map.EL.replaceAll(this.m, biFunction);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public V putIfAbsent(K key, V value) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.putIfAbsent(this.m, key, value);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public boolean remove(Object key, Object value) {
            boolean remove;
            synchronized (this.mutex) {
                remove = Map.EL.remove(this.m, key, value);
            }
            return remove;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public boolean replace(K key, V oldValue, V newValue) {
            boolean replace;
            synchronized (this.mutex) {
                replace = Map.EL.replace(this.m, key, oldValue, newValue);
            }
            return replace;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        @Override // java.util.Map, j$.util.Map
        public V replace(K key, V value) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.replace(this.m, key, value);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super K, ? extends V> */
        @Override // j$.util.Map
        public V computeIfAbsent(K key, j$.util.function.Function<? super K, ? extends V> function) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.computeIfAbsent(this.m, key, function);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super K, ? super V, ? extends V> */
        @Override // j$.util.Map
        public V computeIfPresent(K key, j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.computeIfPresent(this.m, key, biFunction);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super K, ? super V, ? extends V> */
        @Override // j$.util.Map
        public V compute(K key, j$.util.function.BiFunction<? super K, ? super V, ? extends V> biFunction) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.compute(this.m, key, biFunction);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super V, ? super V, ? extends V> */
        @Override // j$.util.Map
        public V merge(K key, V value, j$.util.function.BiFunction<? super V, ? super V, ? extends V> biFunction) {
            V v;
            synchronized (this.mutex) {
                v = (V) Map.EL.merge(this.m, key, value, biFunction);
            }
            return v;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedMap != java.util.DesugarCollections$SynchronizedMap<K, V> */
        private void writeObject(ObjectOutputStream s) {
            synchronized (this.mutex) {
                s.defaultWriteObject();
            }
        }
    }

    public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> m) {
        return new SynchronizedSortedMap(m);
    }

    /* loaded from: classes2.dex */
    static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V>, Map<K, V> {
        private static final long serialVersionUID = -8798146769416483793L;
        private final SortedMap<K, V> sm;

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        SynchronizedSortedMap(SortedMap<K, V> m) {
            super(m);
            this.sm = m;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        SynchronizedSortedMap(SortedMap<K, V> m, Object mutex) {
            super(m, mutex);
            this.sm = m;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        @Override // java.util.SortedMap
        public java.util.Comparator<? super K> comparator() {
            java.util.Comparator<? super K> comparator;
            synchronized (this.mutex) {
                comparator = this.sm.comparator();
            }
            return comparator;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        @Override // java.util.SortedMap
        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.subMap(fromKey, toKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        @Override // java.util.SortedMap
        public SortedMap<K, V> headMap(K toKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.headMap(toKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        @Override // java.util.SortedMap
        public SortedMap<K, V> tailMap(K fromKey) {
            SynchronizedSortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.tailMap(fromKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        @Override // java.util.SortedMap
        public K firstKey() {
            K firstKey;
            synchronized (this.mutex) {
                firstKey = this.sm.firstKey();
            }
            return firstKey;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.DesugarCollections$SynchronizedSortedMap != java.util.DesugarCollections$SynchronizedSortedMap<K, V> */
        @Override // java.util.SortedMap
        public K lastKey() {
            K lastKey;
            synchronized (this.mutex) {
                lastKey = this.sm.lastKey();
            }
            return lastKey;
        }
    }
}
