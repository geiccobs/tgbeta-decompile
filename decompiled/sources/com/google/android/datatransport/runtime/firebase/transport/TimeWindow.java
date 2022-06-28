package com.google.android.datatransport.runtime.firebase.transport;
/* loaded from: classes3.dex */
public final class TimeWindow {
    private static final TimeWindow DEFAULT_INSTANCE = new Builder().build();
    private final long end_ms_;
    private final long start_ms_;

    TimeWindow(long start_ms_, long end_ms_) {
        this.start_ms_ = start_ms_;
        this.end_ms_ = end_ms_;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getStartMs() {
        return this.start_ms_;
    }

    public long getEndMs() {
        return this.end_ms_;
    }

    public static TimeWindow getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long start_ms_ = 0;
        private long end_ms_ = 0;

        Builder() {
        }

        public TimeWindow build() {
            return new TimeWindow(this.start_ms_, this.end_ms_);
        }

        public Builder setStartMs(long start_ms_) {
            this.start_ms_ = start_ms_;
            return this;
        }

        public Builder setEndMs(long end_ms_) {
            this.end_ms_ = end_ms_;
            return this;
        }
    }
}
