package com.google.android.datatransport.runtime.firebase.transport;

import com.google.firebase.encoders.proto.Protobuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class LogSourceMetrics {
    private final List<LogEventDropped> log_event_dropped_;
    private final String log_source_;

    static {
        new Builder().build();
    }

    LogSourceMetrics(String str, List<LogEventDropped> list) {
        this.log_source_ = str;
        this.log_event_dropped_ = list;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Protobuf(tag = 1)
    public String getLogSource() {
        return this.log_source_;
    }

    @Protobuf(tag = 2)
    public List<LogEventDropped> getLogEventDroppedList() {
        return this.log_event_dropped_;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private String log_source_ = "";
        private List<LogEventDropped> log_event_dropped_ = new ArrayList();

        Builder() {
        }

        public LogSourceMetrics build() {
            return new LogSourceMetrics(this.log_source_, Collections.unmodifiableList(this.log_event_dropped_));
        }

        public Builder setLogSource(String str) {
            this.log_source_ = str;
            return this;
        }

        public Builder setLogEventDroppedList(List<LogEventDropped> list) {
            this.log_event_dropped_ = list;
            return this;
        }
    }
}
