package com.google.android.datatransport.runtime.firebase.transport;

import com.google.android.datatransport.runtime.ProtoEncoderDoNotUse;
import com.google.firebase.encoders.annotations.Encodable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class ClientMetrics {
    private static final ClientMetrics DEFAULT_INSTANCE = new Builder().build();
    private final String app_namespace_;
    private final GlobalMetrics global_metrics_;
    private final List<LogSourceMetrics> log_source_metrics_;
    private final TimeWindow window_;

    ClientMetrics(TimeWindow window_, List<LogSourceMetrics> log_source_metrics_, GlobalMetrics global_metrics_, String app_namespace_) {
        this.window_ = window_;
        this.log_source_metrics_ = log_source_metrics_;
        this.global_metrics_ = global_metrics_;
        this.app_namespace_ = app_namespace_;
    }

    public byte[] toByteArray() {
        return ProtoEncoderDoNotUse.encode(this);
    }

    public void writeTo(OutputStream output) throws IOException {
        ProtoEncoderDoNotUse.encode(this, output);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Encodable.Ignore
    public TimeWindow getWindow() {
        TimeWindow timeWindow = this.window_;
        return timeWindow == null ? TimeWindow.getDefaultInstance() : timeWindow;
    }

    @Encodable.Field(name = "window")
    public TimeWindow getWindowInternal() {
        return this.window_;
    }

    @Encodable.Field(name = "logSourceMetrics")
    public List<LogSourceMetrics> getLogSourceMetricsList() {
        return this.log_source_metrics_;
    }

    @Encodable.Ignore
    public GlobalMetrics getGlobalMetrics() {
        GlobalMetrics globalMetrics = this.global_metrics_;
        return globalMetrics == null ? GlobalMetrics.getDefaultInstance() : globalMetrics;
    }

    @Encodable.Field(name = "globalMetrics")
    public GlobalMetrics getGlobalMetricsInternal() {
        return this.global_metrics_;
    }

    public String getAppNamespace() {
        return this.app_namespace_;
    }

    public static ClientMetrics getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /* loaded from: classes3.dex */
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

        public Builder setWindow(TimeWindow window_) {
            this.window_ = window_;
            return this;
        }

        public Builder addLogSourceMetrics(LogSourceMetrics log_source_metrics_) {
            this.log_source_metrics_.add(log_source_metrics_);
            return this;
        }

        public Builder setLogSourceMetricsList(List<LogSourceMetrics> log_source_metrics_) {
            this.log_source_metrics_ = log_source_metrics_;
            return this;
        }

        public Builder setGlobalMetrics(GlobalMetrics global_metrics_) {
            this.global_metrics_ = global_metrics_;
            return this;
        }

        public Builder setAppNamespace(String app_namespace_) {
            this.app_namespace_ = app_namespace_;
            return this;
        }
    }
}
