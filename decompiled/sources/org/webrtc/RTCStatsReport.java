package org.webrtc;

import java.util.Map;
/* loaded from: classes5.dex */
public class RTCStatsReport {
    private final Map<String, RTCStats> stats;
    private final long timestampUs;

    public RTCStatsReport(long timestampUs, Map<String, RTCStats> stats) {
        this.timestampUs = timestampUs;
        this.stats = stats;
    }

    public double getTimestampUs() {
        return this.timestampUs;
    }

    public Map<String, RTCStats> getStatsMap() {
        return this.stats;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ timestampUs: ");
        builder.append(this.timestampUs);
        builder.append(", stats: [\n");
        boolean first = true;
        for (RTCStats stat : this.stats.values()) {
            if (!first) {
                builder.append(",\n");
            }
            builder.append(stat);
            first = false;
        }
        builder.append(" ] }");
        return builder.toString();
    }

    private static RTCStatsReport create(long timestampUs, Map stats) {
        return new RTCStatsReport(timestampUs, stats);
    }
}
