package com.google.android.datatransport.runtime.firebase.transport;

import com.google.android.datatransport.runtime.ProtoEncoderDoNotUse;
import com.google.firebase.encoders.proto.Protobuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public final class ClientMetrics {
    private final String app_namespace_;
    private final GlobalMetrics global_metrics_;
    private final List<LogSourceMetrics> log_source_metrics_;
    private final TimeWindow window_;

    static {
        new Builder().build();
    }

    ClientMetrics(TimeWindow timeWindow, List<LogSourceMetrics> list, GlobalMetrics globalMetrics, String str) {
        this.window_ = timeWindow;
        this.log_source_metrics_ = list;
        this.global_metrics_ = globalMetrics;
        this.app_namespace_ = str;
    }

    public byte[] toByteArray() {
        return ProtoEncoderDoNotUse.encode(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Protobuf(tag = 1)
    public TimeWindow getWindowInternal() {
        return this.window_;
    }

    @Protobuf(tag = 2)
    public List<LogSourceMetrics> getLogSourceMetricsList() {
        return this.log_source_metrics_;
    }

    @Protobuf(tag = 3)
    public GlobalMetrics getGlobalMetricsInternal() {
        return this.global_metrics_;
    }

    @Protobuf(tag = 4)
    public String getAppNamespace() {
        return this.app_namespace_;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private TimeWindow window_ = null;
        private List<LogSourceMetrics> log_source_metrics_ = new ArrayList();
        private GlobalMetrics global_metrics_ = null;
        private String app_namespace_ = "";

        Builder() {
        }

        public ClientMetrics build() {
            return new ClientMetrics(this.window_, Collections.unmodifiableList(this.log_source_metrics_), this.global_metrics_, this.app_namespace_);
        }

        public Builder setWindow(TimeWindow timeWindow) {
            this.window_ = timeWindow;
            return this;
        }

        public Builder addLogSourceMetrics(LogSourceMetrics logSourceMetrics) {
            this.log_source_metrics_.add(logSourceMetrics);
            return this;
        }

        public Builder setGlobalMetrics(GlobalMetrics globalMetrics) {
            this.global_metrics_ = globalMetrics;
            return this;
        }

        public Builder setAppNamespace(String str) {
            this.app_namespace_ = str;
            return this;
        }
    }
}
