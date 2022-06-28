package com.google.android.datatransport.cct.internal;

import com.google.android.datatransport.cct.internal.AutoValue_LogRequest;
import com.google.firebase.encoders.annotations.Encodable;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class LogRequest {
    public abstract ClientInfo getClientInfo();

    @Encodable.Field(name = "logEvent")
    public abstract List<LogEvent> getLogEvents();

    public abstract Integer getLogSource();

    public abstract String getLogSourceName();

    public abstract QosTier getQosTier();

    public abstract long getRequestTimeMs();

    public abstract long getRequestUptimeMs();

    public static Builder builder() {
        return new AutoValue_LogRequest.Builder();
    }

    /* loaded from: classes3.dex */
    public static abstract class Builder {
        public abstract LogRequest build();

        public abstract Builder setClientInfo(ClientInfo clientInfo);

        public abstract Builder setLogEvents(List<LogEvent> list);

        abstract Builder setLogSource(Integer num);

        abstract Builder setLogSourceName(String str);

        public abstract Builder setQosTier(QosTier qosTier);

        public abstract Builder setRequestTimeMs(long j);

        public abstract Builder setRequestUptimeMs(long j);

        public Builder setSource(int value) {
            return setLogSource(Integer.valueOf(value));
        }

        public Builder setSource(String value) {
            return setLogSourceName(value);
        }
    }
}
