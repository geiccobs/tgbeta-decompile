package com.google.android.exoplayer2.source.hls.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes3.dex */
public final class HlsDownloader extends SegmentDownloader<HlsPlaylist> {
    public HlsDownloader(Uri playlistUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        super(playlistUri, streamKeys, constructorHelper);
    }

    @Override // com.google.android.exoplayer2.offline.SegmentDownloader
    public HlsPlaylist getManifest(DataSource dataSource, DataSpec dataSpec) throws IOException {
        return loadManifest(dataSource, dataSpec);
    }

    public List<SegmentDownloader.Segment> getSegments(DataSource dataSource, HlsPlaylist playlist, boolean allowIncompleteList) throws IOException {
        ArrayList<DataSpec> mediaPlaylistDataSpecs = new ArrayList<>();
        if (playlist instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) playlist;
            addMediaPlaylistDataSpecs(masterPlaylist.mediaPlaylistUrls, mediaPlaylistDataSpecs);
        } else {
            mediaPlaylistDataSpecs.add(SegmentDownloader.getCompressibleDataSpec(Uri.parse(playlist.baseUri)));
        }
        ArrayList<SegmentDownloader.Segment> segments = new ArrayList<>();
        HashSet<Uri> seenEncryptionKeyUris = new HashSet<>();
        Iterator<DataSpec> it = mediaPlaylistDataSpecs.iterator();
        while (it.hasNext()) {
            DataSpec mediaPlaylistDataSpec = it.next();
            segments.add(new SegmentDownloader.Segment(0L, mediaPlaylistDataSpec));
            try {
                HlsMediaPlaylist mediaPlaylist = (HlsMediaPlaylist) loadManifest(dataSource, mediaPlaylistDataSpec);
                HlsMediaPlaylist.Segment lastInitSegment = null;
                List<HlsMediaPlaylist.Segment> hlsSegments = mediaPlaylist.segments;
                for (int i = 0; i < hlsSegments.size(); i++) {
                    HlsMediaPlaylist.Segment segment = hlsSegments.get(i);
                    HlsMediaPlaylist.Segment initSegment = segment.initializationSegment;
                    if (initSegment != null && initSegment != lastInitSegment) {
                        lastInitSegment = initSegment;
                        addSegment(mediaPlaylist, initSegment, seenEncryptionKeyUris, segments);
                    }
                    addSegment(mediaPlaylist, segment, seenEncryptionKeyUris, segments);
                }
            } catch (IOException e) {
                if (!allowIncompleteList) {
                    throw e;
                }
            }
        }
        return segments;
    }

    private void addMediaPlaylistDataSpecs(List<Uri> mediaPlaylistUrls, List<DataSpec> out) {
        for (int i = 0; i < mediaPlaylistUrls.size(); i++) {
            out.add(SegmentDownloader.getCompressibleDataSpec(mediaPlaylistUrls.get(i)));
        }
    }

    private static HlsPlaylist loadManifest(DataSource dataSource, DataSpec dataSpec) throws IOException {
        return (HlsPlaylist) ParsingLoadable.load(dataSource, new HlsPlaylistParser(), dataSpec, 4);
    }

    private void addSegment(HlsMediaPlaylist mediaPlaylist, HlsMediaPlaylist.Segment segment, HashSet<Uri> seenEncryptionKeyUris, ArrayList<SegmentDownloader.Segment> out) {
        String baseUri = mediaPlaylist.baseUri;
        long startTimeUs = mediaPlaylist.startTimeUs + segment.relativeStartTimeUs;
        if (segment.fullSegmentEncryptionKeyUri != null) {
            Uri keyUri = UriUtil.resolveToUri(baseUri, segment.fullSegmentEncryptionKeyUri);
            if (seenEncryptionKeyUris.add(keyUri)) {
                out.add(new SegmentDownloader.Segment(startTimeUs, SegmentDownloader.getCompressibleDataSpec(keyUri)));
            }
        }
        Uri segmentUri = UriUtil.resolveToUri(baseUri, segment.url);
        DataSpec dataSpec = new DataSpec(segmentUri, segment.byterangeOffset, segment.byterangeLength, null);
        out.add(new SegmentDownloader.Segment(startTimeUs, dataSpec));
    }
}
