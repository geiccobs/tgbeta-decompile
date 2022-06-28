package com.google.android.datatransport.runtime.firebase.transport;
/* loaded from: classes3.dex */
public final class StorageMetrics {
    private static final StorageMetrics DEFAULT_INSTANCE = new Builder().build();
    private final long current_cache_size_bytes_;
    private final long max_cache_size_bytes_;

    StorageMetrics(long current_cache_size_bytes_, long max_cache_size_bytes_) {
        this.current_cache_size_bytes_ = current_cache_size_bytes_;
        this.max_cache_size_bytes_ = max_cache_size_bytes_;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getCurrentCacheSizeBytes() {
        return this.current_cache_size_bytes_;
    }

    public long getMaxCacheSizeBytes() {
        return this.max_cache_size_bytes_;
    }

    public static StorageMetrics getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long current_cache_size_bytes_ = 0;
        private long max_cache_size_bytes_ = 0;

        Builder() {
        }

        public StorageMetrics build() {
            return new StorageMetrics(this.current_cache_size_bytes_, this.max_cache_size_bytes_);
        }

        public Builder setCurrentCacheSizeBytes(long current_cache_size_bytes_) {
            this.current_cache_size_bytes_ = current_cache_size_bytes_;
            return this;
        }

        public Builder setMaxCacheSizeBytes(long max_cache_size_bytes_) {
            this.max_cache_size_bytes_ = max_cache_size_bytes_;
            return this;
        }
    }
}
