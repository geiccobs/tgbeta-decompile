package com.google.android.datatransport.cct.internal;

import com.google.android.datatransport.cct.internal.LogRequest;
import com.google.firebase.encoders.annotations.Encodable;
import java.util.List;
/* loaded from: classes3.dex */
final class AutoValue_LogRequest extends LogRequest {
    private final ClientInfo clientInfo;
    private final List<LogEvent> logEvents;
    private final Integer logSource;
    private final String logSourceName;
    private final QosTier qosTier;
    private final long requestTimeMs;
    private final long requestUptimeMs;

    private AutoValue_LogRequest(long requestTimeMs, long requestUptimeMs, ClientInfo clientInfo, Integer logSource, String logSourceName, List<LogEvent> logEvents, QosTier qosTier) {
        this.requestTimeMs = requestTimeMs;
        this.requestUptimeMs = requestUptimeMs;
        this.clientInfo = clientInfo;
        this.logSource = logSource;
        this.logSourceName = logSourceName;
        this.logEvents = logEvents;
        this.qosTier = qosTier;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    public long getRequestTimeMs() {
        return this.requestTimeMs;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    public long getRequestUptimeMs() {
        return this.requestUptimeMs;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    public ClientInfo getClientInfo() {
        return this.clientInfo;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    public Integer getLogSource() {
        return this.logSource;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    public String getLogSourceName() {
        return this.logSourceName;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    @Encodable.Field(name = "logEvent")
    public List<LogEvent> getLogEvents() {
        return this.logEvents;
    }

    @Override // com.google.android.datatransport.cct.internal.LogRequest
    public QosTier getQosTier() {
        return this.qosTier;
    }

    public String toString() {
        return "LogRequest{requestTimeMs=" + this.requestTimeMs + ", requestUptimeMs=" + this.requestUptimeMs + ", clientInfo=" + this.clientInfo + ", logSource=" + this.logSource + ", logSourceName=" + this.logSourceName + ", logEvents=" + this.logEvents + ", qosTier=" + this.qosTier + "}";
    }

    public boolean equals(Object o) {
        ClientInfo clientInfo;
        Integer num;
        String str;
        List<LogEvent> list;
        if (o == this) {
            return true;
        }
        if (!(o instanceof LogRequest)) {
            return false;
        }
        LogRequest that = (LogRequest) o;
        if (this.requestTimeMs == that.getRequestTimeMs() && this.requestUptimeMs == that.getRequestUptimeMs() && ((clientInfo = this.clientInfo) != null ? clientInfo.equals(that.getClientInfo()) : that.getClientInfo() == null) && ((num = this.logSource) != null ? num.equals(that.getLogSource()) : that.getLogSource() == null) && ((str = this.logSourceName) != null ? str.equals(that.getLogSourceName()) : that.getLogSourceName() == null) && ((list = this.logEvents) != null ? list.equals(that.getLogEvents()) : that.getLogEvents() == null)) {
            QosTier qosTier = this.qosTier;
            if (qosTier == null) {
                if (that.getQosTier() == null) {
                    return true;
                }
            } else if (qosTier.equals(that.getQosTier())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        long j = this.requestTimeMs;
        long j2 = this.requestUptimeMs;
        int h$2 = (((h$ ^ ((int) (j ^ (j >>> 32)))) * 1000003) ^ ((int) (j2 ^ (j2 >>> 32)))) * 1000003;
        ClientInfo clientInfo = this.clientInfo;
        int i = 0;
        int h$3 = (h$2 ^ (clientInfo == null ? 0 : clientInfo.hashCode())) * 1000003;
        Integer num = this.logSource;
        int h$4 = (h$3 ^ (num == null ? 0 : num.hashCode())) * 1000003;
        String str = this.logSourceName;
        int h$5 = (h$4 ^ (str == null ? 0 : str.hashCode())) * 1000003;
        List<LogEvent> list = this.logEvents;
        int h$6 = (h$5 ^ (list == null ? 0 : list.hashCode())) * 1000003;
        QosTier qosTier = this.qosTier;
        if (qosTier != null) {
            i = qosTier.hashCode();
        }
        return h$6 ^ i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class Builder extends LogRequest.Builder {
        private ClientInfo clientInfo;
        private List<LogEvent> logEvents;
        private Integer logSource;
        private String logSourceName;
        private QosTier qosTier;
        private Long requestTimeMs;
        private Long requestUptimeMs;

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        public LogRequest.Builder setRequestTimeMs(long requestTimeMs) {
            this.requestTimeMs = Long.valueOf(requestTimeMs);
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        public LogRequest.Builder setRequestUptimeMs(long requestUptimeMs) {
            this.requestUptimeMs = Long.valueOf(requestUptimeMs);
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        public LogRequest.Builder setClientInfo(ClientInfo clientInfo) {
            this.clientInfo = clientInfo;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        LogRequest.Builder setLogSource(Integer logSource) {
            this.logSource = logSource;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        LogRequest.Builder setLogSourceName(String logSourceName) {
            this.logSourceName = logSourceName;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        public LogRequest.Builder setLogEvents(List<LogEvent> logEvents) {
            this.logEvents = logEvents;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        public LogRequest.Builder setQosTier(QosTier qosTier) {
            this.qosTier = qosTier;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogRequest.Builder
        public LogRequest build() {
            String missing = "";
            if (this.requestTimeMs == null) {
                missing = missing + " requestTimeMs";
            }
            if (this.requestUptimeMs == null) {
                missing = missing + " requestUptimeMs";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_LogRequest(this.requestTimeMs.longValue(), this.requestUptimeMs.longValue(), this.clientInfo, this.logSource, this.logSourceName, this.logEvents, this.qosTier);
        }
    }
}
