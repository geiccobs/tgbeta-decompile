package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class SegmentBase {
    final RangedUri initialization;
    final long presentationTimeOffset;
    final long timescale;

    public SegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset) {
        this.initialization = initialization;
        this.timescale = timescale;
        this.presentationTimeOffset = presentationTimeOffset;
    }

    public RangedUri getInitialization(Representation representation) {
        return this.initialization;
    }

    public long getPresentationTimeOffsetUs() {
        return Util.scaleLargeTimestamp(this.presentationTimeOffset, 1000000L, this.timescale);
    }

    /* loaded from: classes3.dex */
    public static class SingleSegmentBase extends SegmentBase {
        final long indexLength;
        final long indexStart;

        public SingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long indexStart, long indexLength) {
            super(initialization, timescale, presentationTimeOffset);
            this.indexStart = indexStart;
            this.indexLength = indexLength;
        }

        public SingleSegmentBase() {
            this(null, 1L, 0L, 0L, 0L);
        }

        public RangedUri getIndex() {
            if (this.indexLength <= 0) {
                return null;
            }
            return new RangedUri(null, this.indexStart, this.indexLength);
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class MultiSegmentBase extends SegmentBase {
        final long duration;
        final List<SegmentTimelineElement> segmentTimeline;
        final long startNumber;

        public abstract int getSegmentCount(long j);

        public abstract RangedUri getSegmentUrl(Representation representation, long j);

        public MultiSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline) {
            super(initialization, timescale, presentationTimeOffset);
            this.startNumber = startNumber;
            this.duration = duration;
            this.segmentTimeline = segmentTimeline;
        }

        public long getSegmentNum(long timeUs, long periodDurationUs) {
            long firstSegmentNum = getFirstSegmentNum();
            long segmentCount = getSegmentCount(periodDurationUs);
            if (segmentCount == 0) {
                return firstSegmentNum;
            }
            if (this.segmentTimeline == null) {
                long durationUs = (this.duration * 1000000) / this.timescale;
                long segmentNum = this.startNumber + (timeUs / durationUs);
                return segmentNum < firstSegmentNum ? firstSegmentNum : segmentCount == -1 ? segmentNum : Math.min(segmentNum, (firstSegmentNum + segmentCount) - 1);
            }
            long lowIndex = firstSegmentNum;
            long highIndex = (firstSegmentNum + segmentCount) - 1;
            while (lowIndex <= highIndex) {
                long midIndex = ((highIndex - lowIndex) / 2) + lowIndex;
                long midTimeUs = getSegmentTimeUs(midIndex);
                if (midTimeUs < timeUs) {
                    lowIndex = midIndex + 1;
                } else if (midTimeUs > timeUs) {
                    highIndex = midIndex - 1;
                } else {
                    return midIndex;
                }
            }
            return lowIndex == firstSegmentNum ? lowIndex : highIndex;
        }

        public final long getSegmentDurationUs(long sequenceNumber, long periodDurationUs) {
            List<SegmentTimelineElement> list = this.segmentTimeline;
            if (list != null) {
                long duration = list.get((int) (sequenceNumber - this.startNumber)).duration;
                return (1000000 * duration) / this.timescale;
            }
            int segmentCount = getSegmentCount(periodDurationUs);
            if (segmentCount != -1 && sequenceNumber == (getFirstSegmentNum() + segmentCount) - 1) {
                return periodDurationUs - getSegmentTimeUs(sequenceNumber);
            }
            return (this.duration * 1000000) / this.timescale;
        }

        public final long getSegmentTimeUs(long sequenceNumber) {
            long unscaledSegmentTime;
            List<SegmentTimelineElement> list = this.segmentTimeline;
            if (list != null) {
                unscaledSegmentTime = list.get((int) (sequenceNumber - this.startNumber)).startTime - this.presentationTimeOffset;
            } else {
                long unscaledSegmentTime2 = this.startNumber;
                unscaledSegmentTime = (sequenceNumber - unscaledSegmentTime2) * this.duration;
            }
            return Util.scaleLargeTimestamp(unscaledSegmentTime, 1000000L, this.timescale);
        }

        public long getFirstSegmentNum() {
            return this.startNumber;
        }

        public boolean isExplicit() {
            return this.segmentTimeline != null;
        }
    }

    /* loaded from: classes3.dex */
    public static class SegmentList extends MultiSegmentBase {
        final List<RangedUri> mediaSegments;

        public SegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline, List<RangedUri> mediaSegments) {
            super(initialization, timescale, presentationTimeOffset, startNumber, duration, segmentTimeline);
            this.mediaSegments = mediaSegments;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase
        public RangedUri getSegmentUrl(Representation representation, long sequenceNumber) {
            return this.mediaSegments.get((int) (sequenceNumber - this.startNumber));
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase
        public int getSegmentCount(long periodDurationUs) {
            return this.mediaSegments.size();
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase
        public boolean isExplicit() {
            return true;
        }
    }

    /* loaded from: classes3.dex */
    public static class SegmentTemplate extends MultiSegmentBase {
        final long endNumber;
        final UrlTemplate initializationTemplate;
        final UrlTemplate mediaTemplate;

        public SegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long endNumber, long duration, List<SegmentTimelineElement> segmentTimeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate) {
            super(initialization, timescale, presentationTimeOffset, startNumber, duration, segmentTimeline);
            this.initializationTemplate = initializationTemplate;
            this.mediaTemplate = mediaTemplate;
            this.endNumber = endNumber;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.SegmentBase
        public RangedUri getInitialization(Representation representation) {
            UrlTemplate urlTemplate = this.initializationTemplate;
            if (urlTemplate != null) {
                String urlString = urlTemplate.buildUri(representation.format.id, 0L, representation.format.bitrate, 0L);
                return new RangedUri(urlString, 0L, -1L);
            }
            return super.getInitialization(representation);
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase
        public RangedUri getSegmentUrl(Representation representation, long sequenceNumber) {
            long time;
            if (this.segmentTimeline != null) {
                time = this.segmentTimeline.get((int) (sequenceNumber - this.startNumber)).startTime;
            } else {
                long time2 = this.startNumber;
                time = (sequenceNumber - time2) * this.duration;
            }
            String uriString = this.mediaTemplate.buildUri(representation.format.id, sequenceNumber, representation.format.bitrate, time);
            return new RangedUri(uriString, 0L, -1L);
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.SegmentBase.MultiSegmentBase
        public int getSegmentCount(long periodDurationUs) {
            if (this.segmentTimeline != null) {
                return this.segmentTimeline.size();
            }
            long j = this.endNumber;
            if (j != -1) {
                return (int) ((j - this.startNumber) + 1);
            }
            if (periodDurationUs != C.TIME_UNSET) {
                long durationUs = (this.duration * 1000000) / this.timescale;
                return (int) Util.ceilDivide(periodDurationUs, durationUs);
            }
            return -1;
        }
    }

    /* loaded from: classes3.dex */
    public static class SegmentTimelineElement {
        final long duration;
        final long startTime;

        public SegmentTimelineElement(long startTime, long duration) {
            this.startTime = startTime;
            this.duration = duration;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SegmentTimelineElement that = (SegmentTimelineElement) o;
            return this.startTime == that.startTime && this.duration == that.duration;
        }

        public int hashCode() {
            return (((int) this.startTime) * 31) + ((int) this.duration);
        }
    }
}
