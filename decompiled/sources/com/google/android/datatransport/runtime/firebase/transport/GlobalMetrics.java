package com.google.android.datatransport.runtime.firebase.transport;

import com.google.firebase.encoders.proto.Protobuf;
/* loaded from: classes.dex */
public final class GlobalMetrics {
    private final StorageMetrics storage_metrics_;

    static {
        new Builder().build();
    }

    GlobalMetrics(StorageMetrics storageMetrics) {
        this.storage_metrics_ = storageMetrics;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Protobuf(tag = 1)
    public StorageMetrics getStorageMetricsInternal() {
        return this.storage_metrics_;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private StorageMetrics storage_metrics_ = null;

        Builder() {
        }

        public GlobalMetrics build() {
            return new GlobalMetrics(this.storage_metrics_);
        }

        public Builder setStorageMetrics(StorageMetrics storageMetrics) {
            this.storage_metrics_ = storageMetrics;
            return this;
        }
    }
}
