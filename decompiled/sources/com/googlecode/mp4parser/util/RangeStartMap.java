package com.googlecode.mp4parser.util;

import java.lang.Comparable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/* loaded from: classes3.dex */
public class RangeStartMap<K extends Comparable, V> implements Map<K, V> {
    TreeMap<K, V> base = new TreeMap<>((Comparator<? super K>) new Comparator<K>() { // from class: com.googlecode.mp4parser.util.RangeStartMap.1
        public int compare(K o1, K o2) {
            return -o1.compareTo(o2);
        }
    });

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object put(Object obj, Object obj2) {
        return put((RangeStartMap<K, V>) ((Comparable) obj), (Comparable) obj2);
    }

    public RangeStartMap() {
    }

    public RangeStartMap(K k, V v) {
        put((RangeStartMap<K, V>) k, (K) v);
    }

    @Override // java.util.Map
    public int size() {
        return this.base.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.base.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.base.get(key) != null;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return false;
    }

    @Override // java.util.Map
    public V get(Object k) {
        if (!(k instanceof Comparable)) {
            return null;
        }
        Comparable<K> key = (Comparable) k;
        if (isEmpty()) {
            return null;
        }
        Iterator<K> keys = this.base.keySet().iterator();
        K a = keys.next();
        while (true) {
            K a2 = a;
            if (keys.hasNext()) {
                if (key.compareTo(a2) < 0) {
                    a = keys.next();
                } else {
                    return this.base.get(a2);
                }
            } else {
                return this.base.get(a2);
            }
        }
    }

    public V put(K key, V value) {
        return this.base.put(key, value);
    }

    @Override // java.util.Map
    public V remove(Object k) {
        if (!(k instanceof Comparable)) {
            return null;
        }
        Comparable<K> key = (Comparable) k;
        if (isEmpty()) {
            return null;
        }
        Iterator<K> keys = this.base.keySet().iterator();
        K a = keys.next();
        while (true) {
            K a2 = a;
            if (keys.hasNext()) {
                if (key.compareTo(a2) < 0) {
                    a = keys.next();
                } else {
                    return this.base.remove(a2);
                }
            } else {
                return this.base.remove(a2);
            }
        }
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        this.base.putAll(m);
    }

    @Override // java.util.Map
    public void clear() {
        this.base.clear();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this.base.keySet();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return this.base.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return this.base.entrySet();
    }
}
