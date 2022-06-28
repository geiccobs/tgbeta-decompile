package com.google.android.exoplayer2.source.hls.playlist;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.offline.StreamKey;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class HlsMediaPlaylist extends HlsPlaylist {
    public static final int PLAYLIST_TYPE_EVENT = 2;
    public static final int PLAYLIST_TYPE_UNKNOWN = 0;
    public static final int PLAYLIST_TYPE_VOD = 1;
    public final int discontinuitySequence;
    public final long durationUs;
    public final boolean hasDiscontinuitySequence;
    public final boolean hasEndTag;
    public final boolean hasProgramDateTime;
    public final long mediaSequence;
    public final int playlistType;
    public final DrmInitData protectionSchemes;
    public final List<Segment> segments;
    public final long startOffsetUs;
    public final long startTimeUs;
    public final long targetDurationUs;
    public final int version;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface PlaylistType {
    }

    /* loaded from: classes3.dex */
    public static final class Segment implements Comparable<Long> {
        public final long byterangeLength;
        public final long byterangeOffset;
        public final DrmInitData drmInitData;
        public final long durationUs;
        public final String encryptionIV;
        public final String fullSegmentEncryptionKeyUri;
        public final boolean hasGapTag;
        public final Segment initializationSegment;
        public final int relativeDiscontinuitySequence;
        public final long relativeStartTimeUs;
        public final String title;
        public final String url;

        public Segment(String uri, long byterangeOffset, long byterangeLength, String fullSegmentEncryptionKeyUri, String encryptionIV) {
            this(uri, null, "", 0L, -1, C.TIME_UNSET, null, fullSegmentEncryptionKeyUri, encryptionIV, byterangeOffset, byterangeLength, false);
        }

        public Segment(String url, Segment initializationSegment, String title, long durationUs, int relativeDiscontinuitySequence, long relativeStartTimeUs, DrmInitData drmInitData, String fullSegmentEncryptionKeyUri, String encryptionIV, long byterangeOffset, long byterangeLength, boolean hasGapTag) {
            this.url = url;
            this.initializationSegment = initializationSegment;
            this.title = title;
            this.durationUs = durationUs;
            this.relativeDiscontinuitySequence = relativeDiscontinuitySequence;
            this.relativeStartTimeUs = relativeStartTimeUs;
            this.drmInitData = drmInitData;
            this.fullSegmentEncryptionKeyUri = fullSegmentEncryptionKeyUri;
            this.encryptionIV = encryptionIV;
            this.byterangeOffset = byterangeOffset;
            this.byterangeLength = byterangeLength;
            this.hasGapTag = hasGapTag;
        }

        public int compareTo(Long relativeStartTimeUs) {
            if (this.relativeStartTimeUs > relativeStartTimeUs.longValue()) {
                return 1;
            }
            return this.relativeStartTimeUs < relativeStartTimeUs.longValue() ? -1 : 0;
        }
    }

    public HlsMediaPlaylist(int playlistType, String baseUri, List<String> tags, long startOffsetUs, long startTimeUs, boolean hasDiscontinuitySequence, int discontinuitySequence, long mediaSequence, int version, long targetDurationUs, boolean hasIndependentSegments, boolean hasEndTag, boolean hasProgramDateTime, DrmInitData protectionSchemes, List<Segment> segments) {
        super(baseUri, tags, hasIndependentSegments);
        long j;
        this.playlistType = playlistType;
        this.startTimeUs = startTimeUs;
        this.hasDiscontinuitySequence = hasDiscontinuitySequence;
        this.discontinuitySequence = discontinuitySequence;
        this.mediaSequence = mediaSequence;
        this.version = version;
        this.targetDurationUs = targetDurationUs;
        this.hasEndTag = hasEndTag;
        this.hasProgramDateTime = hasProgramDateTime;
        this.protectionSchemes = protectionSchemes;
        this.segments = Collections.unmodifiableList(segments);
        if (!segments.isEmpty()) {
            Segment last = segments.get(segments.size() - 1);
            this.durationUs = last.relativeStartTimeUs + last.durationUs;
            j = 0;
        } else {
            j = 0;
            this.durationUs = 0L;
        }
        this.startOffsetUs = startOffsetUs != C.TIME_UNSET ? startOffsetUs >= j ? startOffsetUs : this.durationUs + startOffsetUs : C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.offline.FilterableManifest
    public HlsPlaylist copy(List<StreamKey> streamKeys) {
        return this;
    }

    public boolean isNewerThan(HlsMediaPlaylist other) {
        if (other != null) {
            long j = this.mediaSequence;
            long j2 = other.mediaSequence;
            if (j <= j2) {
                if (j < j2) {
                    return false;
                }
                int segmentCount = this.segments.size();
                int otherSegmentCount = other.segments.size();
                if (segmentCount > otherSegmentCount) {
                    return true;
                }
                return segmentCount == otherSegmentCount && this.hasEndTag && !other.hasEndTag;
            }
        }
        return true;
    }

    public long getEndTimeUs() {
        return this.startTimeUs + this.durationUs;
    }

    public HlsMediaPlaylist copyWith(long startTimeUs, int discontinuitySequence) {
        return new HlsMediaPlaylist(this.playlistType, this.baseUri, this.tags, this.startOffsetUs, startTimeUs, true, discontinuitySequence, this.mediaSequence, this.version, this.targetDurationUs, this.hasIndependentSegments, this.hasEndTag, this.hasProgramDateTime, this.protectionSchemes, this.segments);
    }

    public HlsMediaPlaylist copyWithEndTag() {
        if (this.hasEndTag) {
            return this;
        }
        return new HlsMediaPlaylist(this.playlistType, this.baseUri, this.tags, this.startOffsetUs, this.startTimeUs, this.hasDiscontinuitySequence, this.discontinuitySequence, this.mediaSequence, this.version, this.targetDurationUs, this.hasIndependentSegments, true, this.hasProgramDateTime, this.protectionSchemes, this.segments);
    }
}
