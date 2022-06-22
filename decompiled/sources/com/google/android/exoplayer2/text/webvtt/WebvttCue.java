package com.google.android.exoplayer2.text.webvtt;

import android.text.Layout;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
/* loaded from: classes.dex */
public final class WebvttCue extends Cue {
    public final long endTime;
    public final long startTime;

    private WebvttCue(long j, long j2, CharSequence charSequence, Layout.Alignment alignment, float f, int i, int i2, float f2, int i3, float f3) {
        super(charSequence, alignment, f, i, i2, f2, i3, f3);
        this.startTime = j;
        this.endTime = j2;
    }

    public boolean isNormalCue() {
        return this.line == -3.4028235E38f && this.position == 0.5f;
    }

    /* loaded from: classes.dex */
    public static class Builder {
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

        private static float computeLine(float f, int i) {
            if (f == -3.4028235E38f || i != 0 || (f >= 0.0f && f <= 1.0f)) {
                return f != -3.4028235E38f ? f : i == 0 ? 1.0f : -3.4028235E38f;
            }
            return 1.0f;
        }

        private static float derivePosition(int i) {
            if (i != 4) {
                return i != 5 ? 0.5f : 1.0f;
            }
            return 0.0f;
        }

        private static int derivePositionAnchor(int i) {
            if (i != 1) {
                if (i == 3) {
                    return 2;
                }
                if (i == 4) {
                    return 0;
                }
                return i != 5 ? 1 : 2;
            }
            return 0;
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

        public Builder setStartTime(long j) {
            this.startTime = j;
            return this;
        }

        public Builder setEndTime(long j) {
            this.endTime = j;
            return this;
        }

        public Builder setText(CharSequence charSequence) {
            this.text = charSequence;
            return this;
        }

        public Builder setTextAlignment(int i) {
            this.textAlignment = i;
            return this;
        }

        public Builder setLine(float f) {
            this.line = f;
            return this;
        }

        public Builder setLineType(int i) {
            this.lineType = i;
            return this;
        }

        public Builder setLineAnchor(int i) {
            this.lineAnchor = i;
            return this;
        }

        public Builder setPosition(float f) {
            this.position = f;
            return this;
        }

        public Builder setPositionAnchor(int i) {
            this.positionAnchor = i;
            return this;
        }

        public Builder setWidth(float f) {
            this.width = f;
            return this;
        }

        private static Layout.Alignment convertTextAlignment(int i) {
            if (i != 1) {
                if (i == 2) {
                    return Layout.Alignment.ALIGN_CENTER;
                }
                if (i != 3) {
                    if (i != 4) {
                        if (i != 5) {
                            Log.w("WebvttCueBuilder", "Unknown textAlignment: " + i);
                            return null;
                        }
                    }
                }
                return Layout.Alignment.ALIGN_OPPOSITE;
            }
            return Layout.Alignment.ALIGN_NORMAL;
        }

        private static float deriveMaxSize(int i, float f) {
            if (i != 0) {
                if (i == 1) {
                    return f <= 0.5f ? f * 2.0f : (1.0f - f) * 2.0f;
                } else if (i != 2) {
                    throw new IllegalStateException(String.valueOf(i));
                } else {
                    return f;
                }
            }
            return 1.0f - f;
        }
    }
}
