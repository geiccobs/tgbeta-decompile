package com.google.android.datatransport.cct.internal;

import com.google.android.datatransport.cct.internal.LogEvent;
import java.util.Arrays;
/* loaded from: classes3.dex */
final class AutoValue_LogEvent extends LogEvent {
    private final Integer eventCode;
    private final long eventTimeMs;
    private final long eventUptimeMs;
    private final NetworkConnectionInfo networkConnectionInfo;
    private final byte[] sourceExtension;
    private final String sourceExtensionJsonProto3;
    private final long timezoneOffsetSeconds;

    private AutoValue_LogEvent(long eventTimeMs, Integer eventCode, long eventUptimeMs, byte[] sourceExtension, String sourceExtensionJsonProto3, long timezoneOffsetSeconds, NetworkConnectionInfo networkConnectionInfo) {
        this.eventTimeMs = eventTimeMs;
        this.eventCode = eventCode;
        this.eventUptimeMs = eventUptimeMs;
        this.sourceExtension = sourceExtension;
        this.sourceExtensionJsonProto3 = sourceExtensionJsonProto3;
        this.timezoneOffsetSeconds = timezoneOffsetSeconds;
        this.networkConnectionInfo = networkConnectionInfo;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public long getEventTimeMs() {
        return this.eventTimeMs;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public Integer getEventCode() {
        return this.eventCode;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public long getEventUptimeMs() {
        return this.eventUptimeMs;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public byte[] getSourceExtension() {
        return this.sourceExtension;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public String getSourceExtensionJsonProto3() {
        return this.sourceExtensionJsonProto3;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public long getTimezoneOffsetSeconds() {
        return this.timezoneOffsetSeconds;
    }

    @Override // com.google.android.datatransport.cct.internal.LogEvent
    public NetworkConnectionInfo getNetworkConnectionInfo() {
        return this.networkConnectionInfo;
    }

    public String toString() {
        return "LogEvent{eventTimeMs=" + this.eventTimeMs + ", eventCode=" + this.eventCode + ", eventUptimeMs=" + this.eventUptimeMs + ", sourceExtension=" + Arrays.toString(this.sourceExtension) + ", sourceExtensionJsonProto3=" + this.sourceExtensionJsonProto3 + ", timezoneOffsetSeconds=" + this.timezoneOffsetSeconds + ", networkConnectionInfo=" + this.networkConnectionInfo + "}";
    }

    public boolean equals(Object o) {
        Integer num;
        String str;
        if (o == this) {
            return true;
        }
        if (!(o instanceof LogEvent)) {
            return false;
        }
        LogEvent that = (LogEvent) o;
        if (this.eventTimeMs == that.getEventTimeMs() && ((num = this.eventCode) != null ? num.equals(that.getEventCode()) : that.getEventCode() == null) && this.eventUptimeMs == that.getEventUptimeMs()) {
            if (Arrays.equals(this.sourceExtension, that instanceof AutoValue_LogEvent ? ((AutoValue_LogEvent) that).sourceExtension : that.getSourceExtension()) && ((str = this.sourceExtensionJsonProto3) != null ? str.equals(that.getSourceExtensionJsonProto3()) : that.getSourceExtensionJsonProto3() == null) && this.timezoneOffsetSeconds == that.getTimezoneOffsetSeconds()) {
                NetworkConnectionInfo networkConnectionInfo = this.networkConnectionInfo;
                if (networkConnectionInfo == null) {
                    if (that.getNetworkConnectionInfo() == null) {
                        return true;
                    }
                } else if (networkConnectionInfo.equals(that.getNetworkConnectionInfo())) {
                    return true;
                }
            }
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        long j = this.eventTimeMs;
        int h$2 = (h$ ^ ((int) (j ^ (j >>> 32)))) * 1000003;
        Integer num = this.eventCode;
        int i = 0;
        int hashCode = num == null ? 0 : num.hashCode();
        long j2 = this.eventUptimeMs;
        int h$3 = (((((h$2 ^ hashCode) * 1000003) ^ ((int) (j2 ^ (j2 >>> 32)))) * 1000003) ^ Arrays.hashCode(this.sourceExtension)) * 1000003;
        String str = this.sourceExtensionJsonProto3;
        int hashCode2 = str == null ? 0 : str.hashCode();
        long j3 = this.timezoneOffsetSeconds;
        int h$4 = (((h$3 ^ hashCode2) * 1000003) ^ ((int) (j3 ^ (j3 >>> 32)))) * 1000003;
        NetworkConnectionInfo networkConnectionInfo = this.networkConnectionInfo;
        if (networkConnectionInfo != null) {
            i = networkConnectionInfo.hashCode();
        }
        return h$4 ^ i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class Builder extends LogEvent.Builder {
        private Integer eventCode;
        private Long eventTimeMs;
        private Long eventUptimeMs;
        private NetworkConnectionInfo networkConnectionInfo;
        private byte[] sourceExtension;
        private String sourceExtensionJsonProto3;
        private Long timezoneOffsetSeconds;

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        public LogEvent.Builder setEventTimeMs(long eventTimeMs) {
            this.eventTimeMs = Long.valueOf(eventTimeMs);
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        public LogEvent.Builder setEventCode(Integer eventCode) {
            this.eventCode = eventCode;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        public LogEvent.Builder setEventUptimeMs(long eventUptimeMs) {
            this.eventUptimeMs = Long.valueOf(eventUptimeMs);
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        LogEvent.Builder setSourceExtension(byte[] sourceExtension) {
            this.sourceExtension = sourceExtension;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        LogEvent.Builder setSourceExtensionJsonProto3(String sourceExtensionJsonProto3) {
            this.sourceExtensionJsonProto3 = sourceExtensionJsonProto3;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        public LogEvent.Builder setTimezoneOffsetSeconds(long timezoneOffsetSeconds) {
            this.timezoneOffsetSeconds = Long.valueOf(timezoneOffsetSeconds);
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        public LogEvent.Builder setNetworkConnectionInfo(NetworkConnectionInfo networkConnectionInfo) {
            this.networkConnectionInfo = networkConnectionInfo;
            return this;
        }

        @Override // com.google.android.datatransport.cct.internal.LogEvent.Builder
        public LogEvent build() {
            String missing = "";
            if (this.eventTimeMs == null) {
                missing = missing + " eventTimeMs";
            }
            if (this.eventUptimeMs == null) {
                missing = missing + " eventUptimeMs";
            }
            if (this.timezoneOffsetSeconds == null) {
                missing = missing + " timezoneOffsetSeconds";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_LogEvent(this.eventTimeMs.longValue(), this.eventCode, this.eventUptimeMs.longValue(), this.sourceExtension, this.sourceExtensionJsonProto3, this.timezoneOffsetSeconds.longValue(), this.networkConnectionInfo);
        }
    }
}
