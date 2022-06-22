package com.google.android.datatransport.runtime.firebase.transport;

import com.google.firebase.encoders.proto.Protobuf;
/* loaded from: classes.dex */
public final class TimeWindow {
    private final long end_ms_;
    private final long start_ms_;

    static {
        new Builder().build();
    }

    TimeWindow(long j, long j2) {
        this.start_ms_ = j;
        this.end_ms_ = j2;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Protobuf(tag = 1)
    public long getStartMs() {
        return this.start_ms_;
    }

    @Protobuf(tag = 2)
    public long getEndMs() {
        return this.end_ms_;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private long start_ms_ = 0;
        private long end_ms_ = 0;

        Builder() {
        }

        public TimeWindow build() {
            return new TimeWindow(this.start_ms_, this.end_ms_);
        }

        public Builder setStartMs(long j) {
            this.start_ms_ = j;
            return this;
        }

        public Builder setEndMs(long j) {
            this.end_ms_ = j;
            return this;
        }
    }
}
