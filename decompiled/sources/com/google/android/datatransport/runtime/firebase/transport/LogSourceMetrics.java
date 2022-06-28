package com.google.android.datatransport.runtime.firebase.transport;

import com.google.firebase.encoders.annotations.Encodable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class LogSourceMetrics {
    private static final LogSourceMetrics DEFAULT_INSTANCE = new Builder().build();
    private final List<LogEventDropped> log_event_dropped_;
    private final String log_source_;

    LogSourceMetrics(String log_source_, List<LogEventDropped> log_event_dropped_) {
        this.log_source_ = log_source_;
        this.log_event_dropped_ = log_event_dropped_;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getLogSource() {
        return this.log_source_;
    }

    @Encodable.Field(name = "logEventDropped")
    public List<LogEventDropped> getLogEventDroppedList() {
        return this.log_event_dropped_;
    }

    public static LogSourceMetrics getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String log_source_ = "";
        private List<LogEventDropped> log_event_dropped_ = new ArrayList();

        Builder() {
        }

        public LogSourceMetrics build() {
            return new LogSourceMetrics(this.log_source_, Collections.unmodifiableList(this.log_event_dropped_));
        }

        public Builder setLogSource(String log_source_) {
            this.log_source_ = log_source_;
            return this;
        }

        public Builder addLogEventDropped(LogEventDropped log_event_dropped_) {
            this.log_event_dropped_.add(log_event_dropped_);
            return this;
        }

        public Builder setLogEventDroppedList(List<LogEventDropped> log_event_dropped_) {
            this.log_event_dropped_ = log_event_dropped_;
            return this;
        }
    }
}
