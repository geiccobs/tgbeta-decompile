package com.google.android.exoplayer2.audio;

import android.media.AudioAttributes;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class AudioAttributes {
    public static final AudioAttributes DEFAULT = new Builder().build();
    public final int allowedCapturePolicy;
    private android.media.AudioAttributes audioAttributesV21;
    public final int contentType;
    public final int flags;
    public final int usage;

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int contentType = 0;
        private int flags = 0;
        private int usage = 1;
        private int allowedCapturePolicy = 1;

        public Builder setContentType(int contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder setUsage(int usage) {
            this.usage = usage;
            return this;
        }

        public Builder setAllowedCapturePolicy(int allowedCapturePolicy) {
            this.allowedCapturePolicy = allowedCapturePolicy;
            return this;
        }

        public AudioAttributes build() {
            return new AudioAttributes(this.contentType, this.flags, this.usage, this.allowedCapturePolicy);
        }
    }

    private AudioAttributes(int contentType, int flags, int usage, int allowedCapturePolicy) {
        this.contentType = contentType;
        this.flags = flags;
        this.usage = usage;
        this.allowedCapturePolicy = allowedCapturePolicy;
    }

    public android.media.AudioAttributes getAudioAttributesV21() {
        if (this.audioAttributesV21 == null) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder().setContentType(this.contentType).setFlags(this.flags).setUsage(this.usage);
            if (Util.SDK_INT >= 29) {
                builder.setAllowedCapturePolicy(this.allowedCapturePolicy);
            }
            this.audioAttributesV21 = builder.build();
        }
        return this.audioAttributesV21;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AudioAttributes other = (AudioAttributes) obj;
        return this.contentType == other.contentType && this.flags == other.flags && this.usage == other.usage && this.allowedCapturePolicy == other.allowedCapturePolicy;
    }

    public int hashCode() {
        int result = (17 * 31) + this.contentType;
        return (((((result * 31) + this.flags) * 31) + this.usage) * 31) + this.allowedCapturePolicy;
    }
}
