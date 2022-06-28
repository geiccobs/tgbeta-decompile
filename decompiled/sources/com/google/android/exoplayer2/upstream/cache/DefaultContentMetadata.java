package com.google.android.exoplayer2.upstream.cache;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
public final class DefaultContentMetadata implements ContentMetadata {
    public static final DefaultContentMetadata EMPTY = new DefaultContentMetadata(Collections.emptyMap());
    private int hashCode;
    private final Map<String, byte[]> metadata;

    public DefaultContentMetadata() {
        this(Collections.emptyMap());
    }

    public DefaultContentMetadata(Map<String, byte[]> metadata) {
        this.metadata = Collections.unmodifiableMap(metadata);
    }

    public DefaultContentMetadata copyWithMutationsApplied(ContentMetadataMutations mutations) {
        Map<String, byte[]> mutatedMetadata = applyMutations(this.metadata, mutations);
        if (isMetadataEqual(this.metadata, mutatedMetadata)) {
            return this;
        }
        return new DefaultContentMetadata(mutatedMetadata);
    }

    public Set<Map.Entry<String, byte[]>> entrySet() {
        return this.metadata.entrySet();
    }

    @Override // com.google.android.exoplayer2.upstream.cache.ContentMetadata
    public final byte[] get(String name, byte[] defaultValue) {
        if (this.metadata.containsKey(name)) {
            byte[] bytes = this.metadata.get(name);
            return Arrays.copyOf(bytes, bytes.length);
        }
        return defaultValue;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.ContentMetadata
    public final String get(String name, String defaultValue) {
        if (this.metadata.containsKey(name)) {
            byte[] bytes = this.metadata.get(name);
            return new String(bytes, Charset.forName("UTF-8"));
        }
        return defaultValue;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.ContentMetadata
    public final long get(String name, long defaultValue) {
        if (this.metadata.containsKey(name)) {
            byte[] bytes = this.metadata.get(name);
            return ByteBuffer.wrap(bytes).getLong();
        }
        return defaultValue;
    }

    @Override // com.google.android.exoplayer2.upstream.cache.ContentMetadata
    public final boolean contains(String name) {
        return this.metadata.containsKey(name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return isMetadataEqual(this.metadata, ((DefaultContentMetadata) o).metadata);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 0;
            for (Map.Entry<String, byte[]> entry : this.metadata.entrySet()) {
                result += entry.getKey().hashCode() ^ Arrays.hashCode(entry.getValue());
            }
            this.hashCode = result;
        }
        int result2 = this.hashCode;
        return result2;
    }

    private static boolean isMetadataEqual(Map<String, byte[]> first, Map<String, byte[]> second) {
        if (first.size() != second.size()) {
            return false;
        }
        for (Map.Entry<String, byte[]> entry : first.entrySet()) {
            byte[] value = entry.getValue();
            byte[] otherValue = second.get(entry.getKey());
            if (!Arrays.equals(value, otherValue)) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, byte[]> applyMutations(Map<String, byte[]> otherMetadata, ContentMetadataMutations mutations) {
        HashMap<String, byte[]> metadata = new HashMap<>(otherMetadata);
        removeValues(metadata, mutations.getRemovedValues());
        addValues(metadata, mutations.getEditedValues());
        return metadata;
    }

    private static void removeValues(HashMap<String, byte[]> metadata, List<String> names) {
        for (int i = 0; i < names.size(); i++) {
            metadata.remove(names.get(i));
        }
    }

    private static void addValues(HashMap<String, byte[]> metadata, Map<String, Object> values) {
        for (String name : values.keySet()) {
            metadata.put(name, getBytes(values.get(name)));
        }
    }

    private static byte[] getBytes(Object value) {
        if (value instanceof Long) {
            return ByteBuffer.allocate(8).putLong(((Long) value).longValue()).array();
        }
        if (value instanceof String) {
            return ((String) value).getBytes(Charset.forName("UTF-8"));
        }
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new IllegalArgumentException();
    }
}
