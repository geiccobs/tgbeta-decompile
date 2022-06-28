package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
final class MediaPeriodInfo {
    public final long contentPositionUs;
    public final long durationUs;
    public final long endPositionUs;
    public final MediaSource.MediaPeriodId id;
    public final boolean isFinal;
    public final boolean isLastInTimelinePeriod;
    public final long startPositionUs;

    public MediaPeriodInfo(MediaSource.MediaPeriodId id, long startPositionUs, long contentPositionUs, long endPositionUs, long durationUs, boolean isLastInTimelinePeriod, boolean isFinal) {
        this.id = id;
        this.startPositionUs = startPositionUs;
        this.contentPositionUs = contentPositionUs;
        this.endPositionUs = endPositionUs;
        this.durationUs = durationUs;
        this.isLastInTimelinePeriod = isLastInTimelinePeriod;
        this.isFinal = isFinal;
    }

    public MediaPeriodInfo copyWithStartPositionUs(long startPositionUs) {
        if (startPositionUs == this.startPositionUs) {
            return this;
        }
        return new MediaPeriodInfo(this.id, startPositionUs, this.contentPositionUs, this.endPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
    }

    public MediaPeriodInfo copyWithContentPositionUs(long contentPositionUs) {
        if (contentPositionUs == this.contentPositionUs) {
            return this;
        }
        return new MediaPeriodInfo(this.id, this.startPositionUs, contentPositionUs, this.endPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaPeriodInfo that = (MediaPeriodInfo) o;
        return this.startPositionUs == that.startPositionUs && this.contentPositionUs == that.contentPositionUs && this.endPositionUs == that.endPositionUs && this.durationUs == that.durationUs && this.isLastInTimelinePeriod == that.isLastInTimelinePeriod && this.isFinal == that.isFinal && Util.areEqual(this.id, that.id);
    }

    public int hashCode() {
        int result = (17 * 31) + this.id.hashCode();
        return (((((((((((result * 31) + ((int) this.startPositionUs)) * 31) + ((int) this.contentPositionUs)) * 31) + ((int) this.endPositionUs)) * 31) + ((int) this.durationUs)) * 31) + (this.isLastInTimelinePeriod ? 1 : 0)) * 31) + (this.isFinal ? 1 : 0);
    }
}
