package org.webrtc;
/* loaded from: classes5.dex */
public class StatsReport {
    public final String id;
    public final double timestamp;
    public final String type;
    public final Value[] values;

    /* loaded from: classes5.dex */
    public static class Value {
        public final String name;
        public final String value;

        public Value(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String toString() {
            return "[" + this.name + ": " + this.value + "]";
        }
    }

    public StatsReport(String id, String type, double timestamp, Value[] values) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.values = values;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id: ");
        builder.append(this.id);
        builder.append(", type: ");
        builder.append(this.type);
        builder.append(", timestamp: ");
        builder.append(this.timestamp);
        builder.append(", values: ");
        int i = 0;
        while (true) {
            Value[] valueArr = this.values;
            if (i < valueArr.length) {
                builder.append(valueArr[i].toString());
                builder.append(", ");
                i++;
            } else {
                return builder.toString();
            }
        }
    }
}
