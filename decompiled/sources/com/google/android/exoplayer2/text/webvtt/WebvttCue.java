package com.google.android.exoplayer2.text.webvtt;

import android.text.Layout;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class WebvttCue extends Cue {
    private static final float DEFAULT_POSITION = 0.5f;
    public final long endTime;
    public final long startTime;

    private WebvttCue(long startTime, long endTime, CharSequence text, Layout.Alignment textAlignment, float line, int lineType, int lineAnchor, float position, int positionAnchor, float width) {
        super(text, textAlignment, line, lineType, lineAnchor, position, positionAnchor, width);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isNormalCue() {
        return this.line == -3.4028235E38f && this.position == 0.5f;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private static final String TAG = "WebvttCueBuilder";
        public static final int TEXT_ALIGNMENT_CENTER = 2;
        public static final int TEXT_ALIGNMENT_END = 3;
        public static final int TEXT_ALIGNMENT_LEFT = 4;
        public static final int TEXT_ALIGNMENT_RIGHT = 5;
        public static final int TEXT_ALIGNMENT_START = 1;
        private long endTime;
        private float line;
        private int lineAnchor;
        private int lineType;
        private float position;
        private int positionAnchor;
        private long startTime;
        private CharSequence text;
        private int textAlignment;
        private float width;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface TextAlignment {
        }

        public Builder() {
            reset();
        }

        public void reset() {
            this.startTime = 0L;
            this.endTime = 0L;
            this.text = null;
            this.textAlignment = 2;
            this.line = -3.4028235E38f;
            this.lineType = 1;
            this.lineAnchor = 0;
            this.position = -3.4028235E38f;
            this.positionAnchor = Integer.MIN_VALUE;
            this.width = 1.0f;
        }

        public WebvttCue build() {
            this.line = computeLine(this.line, this.lineType);
            if (this.position == -3.4028235E38f) {
                this.position = derivePosition(this.textAlignment);
            }
            if (this.positionAnchor == Integer.MIN_VALUE) {
                this.positionAnchor = derivePositionAnchor(this.textAlignment);
            }
            this.width = Math.min(this.width, deriveMaxSize(this.positionAnchor, this.position));
            return new WebvttCue(this.startTime, this.endTime, (CharSequence) Assertions.checkNotNull(this.text), convertTextAlignment(this.textAlignment), this.line, this.lineType, this.lineAnchor, this.position, this.positionAnchor, this.width);
        }

        public Builder setStartTime(long time) {
            this.startTime = time;
            return this;
        }

        public Builder setEndTime(long time) {
            this.endTime = time;
            return this;
        }

        public Builder setText(CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder setTextAlignment(int textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        public Builder setLine(float line) {
            this.line = line;
            return this;
        }

        public Builder setLineType(int lineType) {
            this.lineType = lineType;
            return this;
        }

        public Builder setLineAnchor(int lineAnchor) {
            this.lineAnchor = lineAnchor;
            return this;
        }

        public Builder setPosition(float position) {
            this.position = position;
            return this;
        }

        public Builder setPositionAnchor(int positionAnchor) {
            this.positionAnchor = positionAnchor;
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        private static float computeLine(float line, int lineType) {
            if (line != -3.4028235E38f && lineType == 0 && (line < 0.0f || line > 1.0f)) {
                return 1.0f;
            }
            if (line != -3.4028235E38f) {
                return line;
            }
            return lineType == 0 ? 1.0f : -3.4028235E38f;
        }

        private static float derivePosition(int textAlignment) {
            switch (textAlignment) {
                case 4:
                    return 0.0f;
                case 5:
                    return 1.0f;
                default:
                    return 0.5f;
            }
        }

        private static int derivePositionAnchor(int textAlignment) {
            switch (textAlignment) {
                case 1:
                case 4:
                    return 0;
                case 2:
                default:
                    return 1;
                case 3:
                case 5:
                    return 2;
            }
        }

        private static Layout.Alignment convertTextAlignment(int textAlignment) {
            switch (textAlignment) {
                case 1:
                case 4:
                    return Layout.Alignment.ALIGN_NORMAL;
                case 2:
                    return Layout.Alignment.ALIGN_CENTER;
                case 3:
                case 5:
                    return Layout.Alignment.ALIGN_OPPOSITE;
                default:
                    Log.w(TAG, "Unknown textAlignment: " + textAlignment);
                    return null;
            }
        }

        private static float deriveMaxSize(int positionAnchor, float position) {
            switch (positionAnchor) {
                case 0:
                    return 1.0f - position;
                case 1:
                    if (position <= 0.5f) {
                        return 2.0f * position;
                    }
                    return (1.0f - position) * 2.0f;
                case 2:
                    return position;
                default:
                    throw new IllegalStateException(String.valueOf(positionAnchor));
            }
        }
    }
}
