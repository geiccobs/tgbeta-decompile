package org.webrtc;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes5.dex */
public class MediaConstraints {
    public final List<KeyValuePair> mandatory = new ArrayList();
    public final List<KeyValuePair> optional = new ArrayList();

    /* loaded from: classes5.dex */
    public static class KeyValuePair {
        private final String key;
        private final String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.key + ": " + this.value;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            KeyValuePair that = (KeyValuePair) other;
            return this.key.equals(that.key) && this.value.equals(that.value);
        }

        public int hashCode() {
            return this.key.hashCode() + this.value.hashCode();
        }
    }

    private static String stringifyKeyValuePairList(List<KeyValuePair> list) {
        StringBuilder builder = new StringBuilder("[");
        for (KeyValuePair pair : list) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            builder.append(pair.toString());
        }
        builder.append("]");
        return builder.toString();
    }

    public String toString() {
        return "mandatory: " + stringifyKeyValuePairList(this.mandatory) + ", optional: " + stringifyKeyValuePairList(this.optional);
    }

    List<KeyValuePair> getMandatory() {
        return this.mandatory;
    }

    List<KeyValuePair> getOptional() {
        return this.optional;
    }
}
