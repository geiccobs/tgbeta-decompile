package com.google.android.datatransport.runtime.firebase.transport;

import com.google.firebase.encoders.proto.ProtoEnum;
/* loaded from: classes3.dex */
public final class LogEventDropped {
    private static final LogEventDropped DEFAULT_INSTANCE = new Builder().build();
    private final long events_dropped_count_;
    private final Reason reason_;

    LogEventDropped(long events_dropped_count_, Reason reason_) {
        this.events_dropped_count_ = events_dropped_count_;
        this.reason_ = reason_;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getEventsDroppedCount() {
        return this.events_dropped_count_;
    }

    public Reason getReason() {
        return this.reason_;
    }

    public static LogEventDropped getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private long events_dropped_count_ = 0;
        private Reason reason_ = Reason.REASON_UNKNOWN;

        Builder() {
        }

        public LogEventDropped build() {
            return new LogEventDropped(this.events_dropped_count_, this.reason_);
        }

        public Builder setEventsDroppedCount(long events_dropped_count_) {
            this.events_dropped_count_ = events_dropped_count_;
            return this;
        }

        public Builder setReason(Reason reason_) {
            this.reason_ = reason_;
            return this;
        }
    }

    /* loaded from: classes3.dex */
    public enum Reason implements ProtoEnum {
        REASON_UNKNOWN(0),
        MESSAGE_TOO_OLD(1),
        CACHE_FULL(2),
        PAYLOAD_TOO_BIG(3),
        MAX_RETRIES_REACHED(4),
        INVALID_PAYLOD(5),
        SERVER_ERROR(6);
        
        private final int number_;

        Reason(int number_) {
            this.number_ = number_;
        }

        @Override // com.google.firebase.encoders.proto.ProtoEnum
        public int getNumber() {
            return this.number_;
        }
    }
}
