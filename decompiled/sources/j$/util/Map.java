package j$.util;

import j$.util.concurrent.ConcurrentMap;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.Function;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes2.dex */
public interface Map<K, V> {

    /* renamed from: j$.util.Map$-EL */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class EL {
        public static /* synthetic */ Object compute(java.util.Map map, Object obj, BiFunction biFunction) {
            return map instanceof Map ? ((Map) map).compute(obj, biFunction) : map instanceof ConcurrentMap ? ConcurrentMap.CC.$default$compute((java.util.concurrent.ConcurrentMap) map, obj, biFunction) : CC.$default$compute(map, obj, biFunction);
        }

        public static /* synthetic */ Object computeIfAbsent(java.util.Map map, Object obj, Function function) {
            return map instanceof Map ? ((Map) map).computeIfAbsent(obj, function) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$computeIfAbsent((java.util.concurrent.ConcurrentMap) map, obj, function) : CC.$default$computeIfAbsent(map, obj, function);
        }

        public static /* synthetic */ Object computeIfPresent(java.util.Map map, Object obj, BiFunction biFunction) {
            return map instanceof Map ? ((Map) map).computeIfPresent(obj, biFunction) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$computeIfPresent((java.util.concurrent.ConcurrentMap) map, obj, biFunction) : CC.$default$computeIfPresent(map, obj, biFunction);
        }

        public static /* synthetic */ void forEach(java.util.Map map, BiConsumer biConsumer) {
            if (map instanceof Map) {
                ((Map) map).forEach(biConsumer);
            } else if (map instanceof java.util.concurrent.ConcurrentMap) {
                ConcurrentMap.CC.$default$forEach((java.util.concurrent.ConcurrentMap) map, biConsumer);
            } else {
                CC.$default$forEach(map, biConsumer);
            }
        }

        public static /* synthetic */ Object getOrDefault(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).getOrDefault(obj, obj2) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$getOrDefault((java.util.concurrent.ConcurrentMap) map, obj, obj2) : CC.$default$getOrDefault(map, obj, obj2);
        }

        public static /* synthetic */ Object merge(java.util.Map map, Object obj, Object obj2, BiFunction biFunction) {
            return map instanceof Map ? ((Map) map).merge(obj, obj2, biFunction) : map instanceof java.util.concurrent.ConcurrentMap ? ConcurrentMap.CC.$default$merge((java.util.concurrent.ConcurrentMap) map, obj, obj2, biFunction) : CC.$default$merge(map, obj, obj2, biFunction);
        }

        public static /* synthetic */ Object putIfAbsent(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).putIfAbsent(obj, obj2) : CC.$default$putIfAbsent(map, obj, obj2);
        }

        public static /* synthetic */ boolean remove(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).remove(obj, obj2) : CC.$default$remove(map, obj, obj2);
        }

        public static /* synthetic */ Object replace(java.util.Map map, Object obj, Object obj2) {
            return map instanceof Map ? ((Map) map).replace(obj, obj2) : CC.$default$replace(map, obj, obj2);
        }

        public static /* synthetic */ boolean replace(java.util.Map map, Object obj, Object obj2, Object obj3) {
            return map instanceof Map ? ((Map) map).replace(obj, obj2, obj3) : CC.$default$replace(map, obj, obj2, obj3);
        }

        public static /* synthetic */ void replaceAll(java.util.Map map, BiFunction biFunction) {
            if (map instanceof Map) {
                ((Map) map).replaceAll(biFunction);
            } else if (map instanceof java.util.concurrent.ConcurrentMap) {
                ConcurrentMap.CC.$default$replaceAll((java.util.concurrent.ConcurrentMap) map, biFunction);
            } else {
                CC.$default$replaceAll(map, biFunction);
            }
        }
    }

    void clear();

    V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction);

    V computeIfAbsent(K k, Function<? super K, ? extends V> function);

    V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction);

    boolean containsKey(Object obj);

    boolean containsValue(Object obj);

    java.util.Set<Map.Entry<K, V>> entrySet();

    boolean equals(Object obj);

    void forEach(BiConsumer<? super K, ? super V> biConsumer);

    V get(Object obj);

    V getOrDefault(Object obj, V v);

    int hashCode();

    boolean isEmpty();

    java.util.Set<K> keySet();

    V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction);

    V put(K k, V v);

    void putAll(java.util.Map<? extends K, ? extends V> map);

    V putIfAbsent(K k, V v);

    V remove(Object obj);

    boolean remove(Object obj, Object obj2);

    V replace(K k, V v);

    boolean replace(K k, V v, V v2);

    void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction);

    int size();

    java.util.Collection<V> values();

    /* loaded from: classes2.dex */
    public interface Entry<K, V> {
        boolean equals(Object obj);

        K getKey();

        V getValue();

        int hashCode();

        V setValue(V v);

        /* renamed from: j$.util.Map$Entry$-CC */
        /* loaded from: classes2.dex */
        public final /* synthetic */ class CC {
            public static <K extends Comparable<? super K>, V> java.util.Comparator<Map.Entry<K, V>> comparingByKey() {
                return Map$Entry$$ExternalSyntheticLambda2.INSTANCE;
            }

            public static <K, V extends Comparable<? super V>> java.util.Comparator<Map.Entry<K, V>> comparingByValue() {
                return Map$Entry$$ExternalSyntheticLambda3.INSTANCE;
            }

            public static <K, V> java.util.Comparator<Map.Entry<K, V>> comparingByKey(java.util.Comparator<? super K> cmp) {
                cmp.getClass();
                return new Map$Entry$$ExternalSyntheticLambda0(cmp);
            }

            public static <K, V> java.util.Comparator<Map.Entry<K, V>> comparingByValue(java.util.Comparator<? super V> cmp) {
                cmp.getClass();
                return new Map$Entry$$ExternalSyntheticLambda1(cmp);
            }
        }
    }

    /* renamed from: j$.util.Map$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Object $default$getOrDefault(java.util.Map _this, Object key, Object obj) {
            Object obj2 = _this.get(key);
            if (obj2 != null || _this.containsKey(key)) {
                return obj2;
            }
            return obj;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiConsumer != java.util.function.BiConsumer<? super K, ? super V> */
        public static void $default$forEach(java.util.Map _this, BiConsumer biConsumer) {
            biConsumer.getClass();
            for (Map.Entry<K, V> entry : _this.entrySet()) {
                try {
                    K k = entry.getKey();
                    V v = entry.getValue();
                    biConsumer.accept(k, v);
                } catch (IllegalStateException ise) {
                    throw new ConcurrentModificationException(ise);
                }
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super K, ? super V, ? extends V> */
        public static void $default$replaceAll(java.util.Map _this, BiFunction biFunction) {
            biFunction.getClass();
            for (Map.Entry<K, V> entry : _this.entrySet()) {
                try {
                    K k = entry.getKey();
                    V v = entry.getValue();
                    try {
                        entry.setValue((V) biFunction.apply(k, v));
                    } catch (IllegalStateException ise) {
                        throw new ConcurrentModificationException(ise);
                    }
                } catch (IllegalStateException ise2) {
                    throw new ConcurrentModificationException(ise2);
                }
            }
        }

        public static Object $default$putIfAbsent(java.util.Map _this, Object obj, Object obj2) {
            Object obj3 = _this.get(obj);
            if (obj3 == null) {
                return _this.put(obj, obj2);
            }
            return obj3;
        }

        public static boolean $default$remove(java.util.Map _this, Object key, Object value) {
            Object curValue = _this.get(key);
            if (Objects.equals(curValue, value)) {
                if (curValue == null && !_this.containsKey(key)) {
                    return false;
                }
                _this.remove(key);
                return true;
            }
            return false;
        }

        public static boolean $default$replace(java.util.Map _this, Object obj, Object obj2, Object obj3) {
            Object curValue = _this.get(obj);
            if (Objects.equals(curValue, obj2)) {
                if (curValue == null && !_this.containsKey(obj)) {
                    return false;
                }
                _this.put(obj, obj3);
                return true;
            }
            return false;
        }

        public static Object $default$replace(java.util.Map _this, Object obj, Object obj2) {
            Object obj3 = _this.get(obj);
            return (obj3 != null || _this.containsKey(obj)) ? _this.put(obj, obj2) : obj3;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.Function != java.util.function.Function<? super K, ? extends V> */
        /* JADX WARN: Multi-variable type inference failed */
        public static Object $default$computeIfAbsent(java.util.Map _this, Object obj, Function function) {
            Object apply;
            function.getClass();
            Object obj2 = _this.get(obj);
            if (obj2 != null || (apply = function.apply(obj)) == null) {
                return obj2;
            }
            _this.put(obj, apply);
            return apply;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super K, ? super V, ? extends V> */
        /* JADX WARN: Multi-variable type inference failed */
        public static Object $default$computeIfPresent(java.util.Map _this, Object obj, BiFunction biFunction) {
            biFunction.getClass();
            Object obj2 = _this.get(obj);
            if (obj2 != null) {
                Object apply = biFunction.apply(obj, obj2);
                if (apply != null) {
                    _this.put(obj, apply);
                    return apply;
                }
                _this.remove(obj);
                return null;
            }
            return null;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super K, ? super V, ? extends V> */
        /* JADX WARN: Multi-variable type inference failed */
        public static Object $default$compute(java.util.Map _this, Object obj, BiFunction biFunction) {
            biFunction.getClass();
            Object obj2 = _this.get(obj);
            Object apply = biFunction.apply(obj, obj2);
            if (apply != null) {
                _this.put(obj, apply);
                return apply;
            } else if (obj2 == null && !_this.containsKey(obj)) {
                return null;
            } else {
                _this.remove(obj);
                return null;
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.function.BiFunction != java.util.function.BiFunction<? super V, ? super V, ? extends V> */
        /* JADX WARN: Multi-variable type inference failed */
        public static Object $default$merge(java.util.Map _this, Object obj, Object obj2, BiFunction biFunction) {
            biFunction.getClass();
            obj2.getClass();
            Object obj3 = _this.get(obj);
            Object apply = obj3 == null ? obj2 : biFunction.apply(obj3, obj2);
            if (apply == null) {
                _this.remove(obj);
            } else {
                _this.put(obj, apply);
            }
            return apply;
        }
    }
}
