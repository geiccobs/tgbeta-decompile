package com.google.android.datatransport.runtime.dagger.internal;

import java.util.Collections;
import java.util.Map;
/* loaded from: classes3.dex */
public final class MapBuilder<K, V> {
    private final Map<K, V> contributions;

    private MapBuilder(int size) {
        this.contributions = DaggerCollections.newLinkedHashMapWithExpectedSize(size);
    }

    public static <K, V> MapBuilder<K, V> newMapBuilder(int size) {
        return new MapBuilder<>(size);
    }

    public MapBuilder<K, V> put(K key, V value) {
        this.contributions.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(Map<K, V> map) {
        this.contributions.putAll(map);
        return this;
    }

    public Map<K, V> build() {
        switch (this.contributions.size()) {
            case 0:
                return Collections.emptyMap();
            default:
                return Collections.unmodifiableMap(this.contributions);
        }
    }
}
