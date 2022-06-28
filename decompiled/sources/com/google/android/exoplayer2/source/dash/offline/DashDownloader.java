package com.google.android.exoplayer2.source.dash.offline;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.offline.DownloadException;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.dash.DashSegmentIndex;
import com.google.android.exoplayer2.source.dash.DashUtil;
import com.google.android.exoplayer2.source.dash.DashWrappingSegmentIndex;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class DashDownloader extends SegmentDownloader<DashManifest> {
    public DashDownloader(Uri manifestUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        super(manifestUri, streamKeys, constructorHelper);
    }

    @Override // com.google.android.exoplayer2.offline.SegmentDownloader
    public DashManifest getManifest(DataSource dataSource, DataSpec dataSpec) throws IOException {
        return (DashManifest) ParsingLoadable.load(dataSource, new DashManifestParser(), dataSpec, 4);
    }

    public List<SegmentDownloader.Segment> getSegments(DataSource dataSource, DashManifest manifest, boolean allowIncompleteList) throws InterruptedException, IOException {
        ArrayList<SegmentDownloader.Segment> segments = new ArrayList<>();
        for (int i = 0; i < manifest.getPeriodCount(); i++) {
            Period period = manifest.getPeriod(i);
            long periodStartUs = C.msToUs(period.startMs);
            long periodDurationUs = manifest.getPeriodDurationUs(i);
            int j = 0;
            for (List<AdaptationSet> adaptationSets = period.adaptationSets; j < adaptationSets.size(); adaptationSets = adaptationSets) {
                addSegmentsForAdaptationSet(dataSource, adaptationSets.get(j), periodStartUs, periodDurationUs, allowIncompleteList, segments);
                j++;
            }
        }
        return segments;
    }

    private static void addSegmentsForAdaptationSet(DataSource dataSource, AdaptationSet adaptationSet, long periodStartUs, long periodDurationUs, boolean allowIncompleteList, ArrayList<SegmentDownloader.Segment> out) throws IOException, InterruptedException {
        IOException e;
        DashSegmentIndex index;
        AdaptationSet adaptationSet2 = adaptationSet;
        int i = 0;
        while (i < adaptationSet2.representations.size()) {
            Representation representation = adaptationSet2.representations.get(i);
            try {
                index = getSegmentIndex(dataSource, adaptationSet2.type, representation);
            } catch (IOException e2) {
                e = e2;
            }
            if (index == null) {
                try {
                    throw new DownloadException("Missing segment index");
                    break;
                } catch (IOException e3) {
                    e = e3;
                    if (allowIncompleteList) {
                        i++;
                        adaptationSet2 = adaptationSet;
                    } else {
                        throw e;
                    }
                }
            } else {
                int segmentCount = index.getSegmentCount(periodDurationUs);
                if (segmentCount == -1) {
                    throw new DownloadException("Unbounded segment index");
                }
                String baseUrl = representation.baseUrl;
                RangedUri initializationUri = representation.getInitializationUri();
                if (initializationUri != null) {
                    addSegment(periodStartUs, baseUrl, initializationUri, out);
                }
                RangedUri indexUri = representation.getIndexUri();
                if (indexUri != null) {
                    addSegment(periodStartUs, baseUrl, indexUri, out);
                }
                long firstSegmentNum = index.getFirstSegmentNum();
                long lastSegmentNum = (segmentCount + firstSegmentNum) - 1;
                long j = firstSegmentNum;
                while (j <= lastSegmentNum) {
                    long lastSegmentNum2 = lastSegmentNum;
                    long lastSegmentNum3 = periodStartUs + index.getTimeUs(j);
                    addSegment(lastSegmentNum3, baseUrl, index.getSegmentUrl(j), out);
                    j++;
                    lastSegmentNum = lastSegmentNum2;
                }
                i++;
                adaptationSet2 = adaptationSet;
            }
        }
    }

    private static void addSegment(long startTimeUs, String baseUrl, RangedUri rangedUri, ArrayList<SegmentDownloader.Segment> out) {
        DataSpec dataSpec = new DataSpec(rangedUri.resolveUri(baseUrl), rangedUri.start, rangedUri.length, null);
        out.add(new SegmentDownloader.Segment(startTimeUs, dataSpec));
    }

    private static DashSegmentIndex getSegmentIndex(DataSource dataSource, int trackType, Representation representation) throws IOException, InterruptedException {
        DashSegmentIndex index = representation.getIndex();
        if (index != null) {
            return index;
        }
        ChunkIndex seekMap = DashUtil.loadChunkIndex(dataSource, trackType, representation);
        if (seekMap == null) {
            return null;
        }
        return new DashWrappingSegmentIndex(seekMap, representation.presentationTimeOffsetUs);
    }
}
