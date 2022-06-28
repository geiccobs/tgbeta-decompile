package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.dash.DashSegmentIndex;
import com.google.android.exoplayer2.source.dash.manifest.SegmentBase;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public abstract class Representation {
    public static final long REVISION_ID_DEFAULT = -1;
    public final String baseUrl;
    public final Format format;
    public final List<Descriptor> inbandEventStreams;
    private final RangedUri initializationUri;
    public final long presentationTimeOffsetUs;
    public final long revisionId;

    public abstract String getCacheKey();

    public abstract DashSegmentIndex getIndex();

    public abstract RangedUri getIndexUri();

    public static Representation newInstance(long revisionId, Format format, String baseUrl, SegmentBase segmentBase) {
        return newInstance(revisionId, format, baseUrl, segmentBase, null);
    }

    public static Representation newInstance(long revisionId, Format format, String baseUrl, SegmentBase segmentBase, List<Descriptor> inbandEventStreams) {
        return newInstance(revisionId, format, baseUrl, segmentBase, inbandEventStreams, null);
    }

    public static Representation newInstance(long revisionId, Format format, String baseUrl, SegmentBase segmentBase, List<Descriptor> inbandEventStreams, String cacheKey) {
        if (segmentBase instanceof SegmentBase.SingleSegmentBase) {
            return new SingleSegmentRepresentation(revisionId, format, baseUrl, (SegmentBase.SingleSegmentBase) segmentBase, inbandEventStreams, cacheKey, -1L);
        }
        if (segmentBase instanceof SegmentBase.MultiSegmentBase) {
            return new MultiSegmentRepresentation(revisionId, format, baseUrl, (SegmentBase.MultiSegmentBase) segmentBase, inbandEventStreams);
        }
        throw new IllegalArgumentException("segmentBase must be of type SingleSegmentBase or MultiSegmentBase");
    }

    private Representation(long revisionId, Format format, String baseUrl, SegmentBase segmentBase, List<Descriptor> inbandEventStreams) {
        List<Descriptor> list;
        this.revisionId = revisionId;
        this.format = format;
        this.baseUrl = baseUrl;
        if (inbandEventStreams == null) {
            list = Collections.emptyList();
        } else {
            list = Collections.unmodifiableList(inbandEventStreams);
        }
        this.inbandEventStreams = list;
        this.initializationUri = segmentBase.getInitialization(this);
        this.presentationTimeOffsetUs = segmentBase.getPresentationTimeOffsetUs();
    }

    public RangedUri getInitializationUri() {
        return this.initializationUri;
    }

    /* loaded from: classes3.dex */
    public static class SingleSegmentRepresentation extends Representation {
        private final String cacheKey;
        public final long contentLength;
        private final RangedUri indexUri;
        private final SingleSegmentIndex segmentIndex;
        public final Uri uri;

        public static SingleSegmentRepresentation newInstance(long revisionId, Format format, String uri, long initializationStart, long initializationEnd, long indexStart, long indexEnd, List<Descriptor> inbandEventStreams, String cacheKey, long contentLength) {
            RangedUri rangedUri = new RangedUri(null, initializationStart, (initializationEnd - initializationStart) + 1);
            SegmentBase.SingleSegmentBase segmentBase = new SegmentBase.SingleSegmentBase(rangedUri, 1L, 0L, indexStart, (indexEnd - indexStart) + 1);
            return new SingleSegmentRepresentation(revisionId, format, uri, segmentBase, inbandEventStreams, cacheKey, contentLength);
        }

        public SingleSegmentRepresentation(long revisionId, Format format, String baseUrl, SegmentBase.SingleSegmentBase segmentBase, List<Descriptor> inbandEventStreams, String cacheKey, long contentLength) {
            super(revisionId, format, baseUrl, segmentBase, inbandEventStreams);
            this.uri = Uri.parse(baseUrl);
            RangedUri index = segmentBase.getIndex();
            this.indexUri = index;
            this.cacheKey = cacheKey;
            this.contentLength = contentLength;
            this.segmentIndex = index != null ? null : new SingleSegmentIndex(new RangedUri(null, 0L, contentLength));
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.Representation
        public RangedUri getIndexUri() {
            return this.indexUri;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.Representation
        public DashSegmentIndex getIndex() {
            return this.segmentIndex;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.Representation
        public String getCacheKey() {
            return this.cacheKey;
        }
    }

    /* loaded from: classes3.dex */
    public static class MultiSegmentRepresentation extends Representation implements DashSegmentIndex {
        private final SegmentBase.MultiSegmentBase segmentBase;

        public MultiSegmentRepresentation(long revisionId, Format format, String baseUrl, SegmentBase.MultiSegmentBase segmentBase, List<Descriptor> inbandEventStreams) {
            super(revisionId, format, baseUrl, segmentBase, inbandEventStreams);
            this.segmentBase = segmentBase;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.Representation
        public RangedUri getIndexUri() {
            return null;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.Representation
        public DashSegmentIndex getIndex() {
            return this;
        }

        @Override // com.google.android.exoplayer2.source.dash.manifest.Representation
        public String getCacheKey() {
            return null;
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public RangedUri getSegmentUrl(long segmentIndex) {
            return this.segmentBase.getSegmentUrl(this, segmentIndex);
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public long getSegmentNum(long timeUs, long periodDurationUs) {
            return this.segmentBase.getSegmentNum(timeUs, periodDurationUs);
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public long getTimeUs(long segmentIndex) {
            return this.segmentBase.getSegmentTimeUs(segmentIndex);
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public long getDurationUs(long segmentIndex, long periodDurationUs) {
            return this.segmentBase.getSegmentDurationUs(segmentIndex, periodDurationUs);
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public long getFirstSegmentNum() {
            return this.segmentBase.getFirstSegmentNum();
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public int getSegmentCount(long periodDurationUs) {
            return this.segmentBase.getSegmentCount(periodDurationUs);
        }

        @Override // com.google.android.exoplayer2.source.dash.DashSegmentIndex
        public boolean isExplicit() {
            return this.segmentBase.isExplicit();
        }
    }
}
