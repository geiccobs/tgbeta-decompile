package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.DataChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class HlsChunkSource {
    private static final int KEY_CACHE_SIZE = 4;
    private final DataSource encryptionDataSource;
    private Uri expectedPlaylistUrl;
    private final HlsExtractorFactory extractorFactory;
    private IOException fatalError;
    private boolean independentSegments;
    private boolean isTimestampMaster;
    private final DataSource mediaDataSource;
    private final List<Format> muxedCaptionFormats;
    private final Format[] playlistFormats;
    private final HlsPlaylistTracker playlistTracker;
    private final Uri[] playlistUrls;
    private boolean seenExpectedPlaylistError;
    private final TimestampAdjusterProvider timestampAdjusterProvider;
    private final TrackGroup trackGroup;
    private TrackSelection trackSelection;
    private final FullSegmentEncryptionKeyCache keyCache = new FullSegmentEncryptionKeyCache(4);
    private byte[] scratchSpace = Util.EMPTY_BYTE_ARRAY;
    private long liveEdgeInPeriodTimeUs = C.TIME_UNSET;

    /* loaded from: classes3.dex */
    public static final class HlsChunkHolder {
        public Chunk chunk;
        public boolean endOfStream;
        public Uri playlistUrl;

        public HlsChunkHolder() {
            clear();
        }

        public void clear() {
            this.chunk = null;
            this.endOfStream = false;
            this.playlistUrl = null;
        }
    }

    public HlsChunkSource(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, Uri[] playlistUrls, Format[] playlistFormats, HlsDataSourceFactory dataSourceFactory, TransferListener mediaTransferListener, TimestampAdjusterProvider timestampAdjusterProvider, List<Format> muxedCaptionFormats) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.playlistUrls = playlistUrls;
        this.playlistFormats = playlistFormats;
        this.timestampAdjusterProvider = timestampAdjusterProvider;
        this.muxedCaptionFormats = muxedCaptionFormats;
        DataSource createDataSource = dataSourceFactory.createDataSource(1);
        this.mediaDataSource = createDataSource;
        if (mediaTransferListener != null) {
            createDataSource.addTransferListener(mediaTransferListener);
        }
        this.encryptionDataSource = dataSourceFactory.createDataSource(3);
        this.trackGroup = new TrackGroup(playlistFormats);
        int[] initialTrackSelection = new int[playlistUrls.length];
        for (int i = 0; i < playlistUrls.length; i++) {
            initialTrackSelection[i] = i;
        }
        this.trackSelection = new InitializationTrackSelection(this.trackGroup, initialTrackSelection);
    }

    public void maybeThrowError() throws IOException {
        IOException iOException = this.fatalError;
        if (iOException != null) {
            throw iOException;
        }
        Uri uri = this.expectedPlaylistUrl;
        if (uri != null && this.seenExpectedPlaylistError) {
            this.playlistTracker.maybeThrowPlaylistRefreshError(uri);
        }
    }

    public TrackGroup getTrackGroup() {
        return this.trackGroup;
    }

    public void setTrackSelection(TrackSelection trackSelection) {
        this.trackSelection = trackSelection;
    }

    public TrackSelection getTrackSelection() {
        return this.trackSelection;
    }

    public void reset() {
        this.fatalError = null;
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.isTimestampMaster = isTimestampMaster;
    }

    public void getNextChunk(long playbackPositionUs, long loadPositionUs, List<HlsMediaChunk> queue, boolean allowEndOfStream, HlsChunkHolder out) {
        long timeToLiveEdgeUs;
        long bufferedDurationUs;
        Uri selectedPlaylistUrl;
        HlsMediaPlaylist mediaPlaylist;
        long startOfPlaylistInPeriodUs;
        int selectedTrackIndex;
        HlsMediaChunk previous = queue.isEmpty() ? null : queue.get(queue.size() - 1);
        int oldTrackIndex = previous == null ? -1 : this.trackGroup.indexOf(previous.trackFormat);
        long bufferedDurationUs2 = loadPositionUs - playbackPositionUs;
        long timeToLiveEdgeUs2 = resolveTimeToLiveEdgeUs(playbackPositionUs);
        if (previous != null && !this.independentSegments) {
            long subtractedDurationUs = previous.getDurationUs();
            bufferedDurationUs = Math.max(0L, bufferedDurationUs2 - subtractedDurationUs);
            if (timeToLiveEdgeUs2 == C.TIME_UNSET) {
                timeToLiveEdgeUs = timeToLiveEdgeUs2;
            } else {
                timeToLiveEdgeUs = Math.max(0L, timeToLiveEdgeUs2 - subtractedDurationUs);
            }
        } else {
            timeToLiveEdgeUs = timeToLiveEdgeUs2;
            bufferedDurationUs = bufferedDurationUs2;
        }
        MediaChunkIterator[] mediaChunkIterators = createMediaChunkIterators(previous, loadPositionUs);
        this.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, mediaChunkIterators);
        int selectedTrackIndex2 = this.trackSelection.getSelectedIndexInTrackGroup();
        boolean switchingTrack = oldTrackIndex != selectedTrackIndex2;
        Uri selectedPlaylistUrl2 = this.playlistUrls[selectedTrackIndex2];
        if (!this.playlistTracker.isSnapshotValid(selectedPlaylistUrl2)) {
            out.playlistUrl = selectedPlaylistUrl2;
            this.seenExpectedPlaylistError &= selectedPlaylistUrl2.equals(this.expectedPlaylistUrl);
            this.expectedPlaylistUrl = selectedPlaylistUrl2;
            return;
        }
        HlsMediaPlaylist mediaPlaylist2 = this.playlistTracker.getPlaylistSnapshot(selectedPlaylistUrl2, true);
        Assertions.checkNotNull(mediaPlaylist2);
        this.independentSegments = mediaPlaylist2.hasIndependentSegments;
        updateLiveEdgeTimeUs(mediaPlaylist2);
        long startOfPlaylistInPeriodUs2 = mediaPlaylist2.startTimeUs - this.playlistTracker.getInitialStartTimeUs();
        long chunkMediaSequence = getChunkMediaSequence(previous, switchingTrack, mediaPlaylist2, startOfPlaylistInPeriodUs2, loadPositionUs);
        if (chunkMediaSequence < mediaPlaylist2.mediaSequence && previous != null && switchingTrack) {
            selectedPlaylistUrl = this.playlistUrls[oldTrackIndex];
            mediaPlaylist = this.playlistTracker.getPlaylistSnapshot(selectedPlaylistUrl, true);
            Assertions.checkNotNull(mediaPlaylist);
            long startOfPlaylistInPeriodUs3 = mediaPlaylist.startTimeUs - this.playlistTracker.getInitialStartTimeUs();
            chunkMediaSequence = previous.getNextChunkIndex();
            selectedTrackIndex = oldTrackIndex;
            startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs3;
        } else {
            selectedTrackIndex = selectedTrackIndex2;
            mediaPlaylist = mediaPlaylist2;
            startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs2;
            selectedPlaylistUrl = selectedPlaylistUrl2;
        }
        if (chunkMediaSequence < mediaPlaylist.mediaSequence) {
            this.fatalError = new BehindLiveWindowException();
            return;
        }
        int segmentIndexInPlaylist = (int) (chunkMediaSequence - mediaPlaylist.mediaSequence);
        int availableSegmentCount = mediaPlaylist.segments.size();
        if (segmentIndexInPlaylist >= availableSegmentCount) {
            if (mediaPlaylist.hasEndTag) {
                if (allowEndOfStream || availableSegmentCount == 0) {
                    out.endOfStream = true;
                    return;
                }
                segmentIndexInPlaylist = availableSegmentCount - 1;
            } else {
                out.playlistUrl = selectedPlaylistUrl;
                this.seenExpectedPlaylistError = selectedPlaylistUrl.equals(this.expectedPlaylistUrl) & this.seenExpectedPlaylistError;
                this.expectedPlaylistUrl = selectedPlaylistUrl;
                return;
            }
        }
        this.seenExpectedPlaylistError = false;
        this.expectedPlaylistUrl = null;
        HlsMediaPlaylist.Segment segment = mediaPlaylist.segments.get(segmentIndexInPlaylist);
        Uri initSegmentKeyUri = getFullEncryptionKeyUri(mediaPlaylist, segment.initializationSegment);
        out.chunk = maybeCreateEncryptionChunkFor(initSegmentKeyUri, selectedTrackIndex);
        if (out.chunk != null) {
            return;
        }
        Uri mediaSegmentKeyUri = getFullEncryptionKeyUri(mediaPlaylist, segment);
        out.chunk = maybeCreateEncryptionChunkFor(mediaSegmentKeyUri, selectedTrackIndex);
        if (out.chunk != null) {
            return;
        }
        out.chunk = HlsMediaChunk.createInstance(this.extractorFactory, this.mediaDataSource, this.playlistFormats[selectedTrackIndex], startOfPlaylistInPeriodUs, mediaPlaylist, segmentIndexInPlaylist, selectedPlaylistUrl, this.muxedCaptionFormats, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), this.isTimestampMaster, this.timestampAdjusterProvider, previous, this.keyCache.get(mediaSegmentKeyUri), this.keyCache.get(initSegmentKeyUri));
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            this.keyCache.put(encryptionKeyChunk.dataSpec.uri, (byte[]) Assertions.checkNotNull(encryptionKeyChunk.getResult()));
        }
    }

    public boolean maybeBlacklistTrack(Chunk chunk, long blacklistDurationMs) {
        TrackSelection trackSelection = this.trackSelection;
        return trackSelection.blacklist(trackSelection.indexOf(this.trackGroup.indexOf(chunk.trackFormat)), blacklistDurationMs);
    }

    public boolean onPlaylistError(Uri playlistUrl, long blacklistDurationMs) {
        int trackSelectionIndex;
        int trackGroupIndex = -1;
        int i = 0;
        while (true) {
            Uri[] uriArr = this.playlistUrls;
            if (i >= uriArr.length) {
                break;
            } else if (!uriArr[i].equals(playlistUrl)) {
                i++;
            } else {
                trackGroupIndex = i;
                break;
            }
        }
        if (trackGroupIndex == -1 || (trackSelectionIndex = this.trackSelection.indexOf(trackGroupIndex)) == -1) {
            return true;
        }
        this.seenExpectedPlaylistError |= playlistUrl.equals(this.expectedPlaylistUrl);
        return blacklistDurationMs == C.TIME_UNSET || this.trackSelection.blacklist(trackSelectionIndex, blacklistDurationMs);
    }

    public MediaChunkIterator[] createMediaChunkIterators(HlsMediaChunk previous, long loadPositionUs) {
        HlsChunkSource hlsChunkSource = this;
        int oldTrackIndex = previous == null ? -1 : hlsChunkSource.trackGroup.indexOf(previous.trackFormat);
        MediaChunkIterator[] chunkIterators = new MediaChunkIterator[hlsChunkSource.trackSelection.length()];
        int i = 0;
        while (i < chunkIterators.length) {
            int trackIndex = hlsChunkSource.trackSelection.getIndexInTrackGroup(i);
            Uri playlistUrl = hlsChunkSource.playlistUrls[trackIndex];
            if (!hlsChunkSource.playlistTracker.isSnapshotValid(playlistUrl)) {
                chunkIterators[i] = MediaChunkIterator.EMPTY;
            } else {
                HlsMediaPlaylist playlist = hlsChunkSource.playlistTracker.getPlaylistSnapshot(playlistUrl, false);
                Assertions.checkNotNull(playlist);
                long startOfPlaylistInPeriodUs = playlist.startTimeUs - hlsChunkSource.playlistTracker.getInitialStartTimeUs();
                boolean switchingTrack = trackIndex != oldTrackIndex;
                long chunkMediaSequence = getChunkMediaSequence(previous, switchingTrack, playlist, startOfPlaylistInPeriodUs, loadPositionUs);
                if (chunkMediaSequence < playlist.mediaSequence) {
                    chunkIterators[i] = MediaChunkIterator.EMPTY;
                } else {
                    int chunkIndex = (int) (chunkMediaSequence - playlist.mediaSequence);
                    chunkIterators[i] = new HlsMediaPlaylistSegmentIterator(playlist, startOfPlaylistInPeriodUs, chunkIndex);
                }
            }
            i++;
            hlsChunkSource = this;
        }
        return chunkIterators;
    }

    private long getChunkMediaSequence(HlsMediaChunk previous, boolean switchingTrack, HlsMediaPlaylist mediaPlaylist, long startOfPlaylistInPeriodUs, long loadPositionUs) {
        if (previous == null || switchingTrack) {
            long endOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs + mediaPlaylist.durationUs;
            long targetPositionInPeriodUs = (previous == null || this.independentSegments) ? loadPositionUs : previous.startTimeUs;
            if (!mediaPlaylist.hasEndTag && targetPositionInPeriodUs >= endOfPlaylistInPeriodUs) {
                return mediaPlaylist.mediaSequence + mediaPlaylist.segments.size();
            }
            long targetPositionInPlaylistUs = targetPositionInPeriodUs - startOfPlaylistInPeriodUs;
            return Util.binarySearchFloor((List<? extends Comparable<? super Long>>) mediaPlaylist.segments, Long.valueOf(targetPositionInPlaylistUs), true, !this.playlistTracker.isLive() || previous == null) + mediaPlaylist.mediaSequence;
        }
        return previous.getNextChunkIndex();
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        long j = this.liveEdgeInPeriodTimeUs;
        boolean resolveTimeToLiveEdgePossible = j != C.TIME_UNSET;
        return resolveTimeToLiveEdgePossible ? j - playbackPositionUs : C.TIME_UNSET;
    }

    private void updateLiveEdgeTimeUs(HlsMediaPlaylist mediaPlaylist) {
        long j;
        if (mediaPlaylist.hasEndTag) {
            j = C.TIME_UNSET;
        } else {
            j = mediaPlaylist.getEndTimeUs() - this.playlistTracker.getInitialStartTimeUs();
        }
        this.liveEdgeInPeriodTimeUs = j;
    }

    private Chunk maybeCreateEncryptionChunkFor(Uri keyUri, int selectedTrackIndex) {
        if (keyUri == null) {
            return null;
        }
        byte[] encryptionKey = this.keyCache.remove(keyUri);
        if (encryptionKey != null) {
            this.keyCache.put(keyUri, encryptionKey);
            return null;
        }
        DataSpec dataSpec = new DataSpec(keyUri, 0L, -1L, null, 1);
        return new EncryptionKeyChunk(this.encryptionDataSource, dataSpec, this.playlistFormats[selectedTrackIndex], this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), this.scratchSpace);
    }

    private static Uri getFullEncryptionKeyUri(HlsMediaPlaylist playlist, HlsMediaPlaylist.Segment segment) {
        if (segment == null || segment.fullSegmentEncryptionKeyUri == null) {
            return null;
        }
        return UriUtil.resolveToUri(playlist.baseUri, segment.fullSegmentEncryptionKeyUri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class InitializationTrackSelection extends BaseTrackSelection {
        private int selectedIndex;

        public InitializationTrackSelection(TrackGroup group, int[] tracks) {
            super(group, tracks);
            this.selectedIndex = indexOf(group.getFormat(0));
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> queue, MediaChunkIterator[] mediaChunkIterators) {
            long nowMs = SystemClock.elapsedRealtime();
            if (!isBlacklisted(this.selectedIndex, nowMs)) {
                return;
            }
            for (int i = this.length - 1; i >= 0; i--) {
                if (!isBlacklisted(i, nowMs)) {
                    this.selectedIndex = i;
                    return;
                }
            }
            throw new IllegalStateException();
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectedIndex() {
            return this.selectedIndex;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public int getSelectionReason() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelection
        public Object getSelectionData() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static final class EncryptionKeyChunk extends DataChunk {
        private byte[] result;

        public EncryptionKeyChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, byte[] scratchSpace) {
            super(dataSource, dataSpec, 3, trackFormat, trackSelectionReason, trackSelectionData, scratchSpace);
        }

        @Override // com.google.android.exoplayer2.source.chunk.DataChunk
        protected void consume(byte[] data, int limit) {
            this.result = Arrays.copyOf(data, limit);
        }

        public byte[] getResult() {
            return this.result;
        }
    }

    /* loaded from: classes3.dex */
    public static final class HlsMediaPlaylistSegmentIterator extends BaseMediaChunkIterator {
        private final HlsMediaPlaylist playlist;
        private final long startOfPlaylistInPeriodUs;

        public HlsMediaPlaylistSegmentIterator(HlsMediaPlaylist playlist, long startOfPlaylistInPeriodUs, int chunkIndex) {
            super(chunkIndex, playlist.segments.size() - 1);
            this.playlist = playlist;
            this.startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs;
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public DataSpec getDataSpec() {
            checkInBounds();
            HlsMediaPlaylist.Segment segment = this.playlist.segments.get((int) getCurrentIndex());
            Uri chunkUri = UriUtil.resolveToUri(this.playlist.baseUri, segment.url);
            return new DataSpec(chunkUri, segment.byterangeOffset, segment.byterangeLength, null);
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public long getChunkStartTimeUs() {
            checkInBounds();
            HlsMediaPlaylist.Segment segment = this.playlist.segments.get((int) getCurrentIndex());
            return this.startOfPlaylistInPeriodUs + segment.relativeStartTimeUs;
        }

        @Override // com.google.android.exoplayer2.source.chunk.MediaChunkIterator
        public long getChunkEndTimeUs() {
            checkInBounds();
            HlsMediaPlaylist.Segment segment = this.playlist.segments.get((int) getCurrentIndex());
            long segmentStartTimeInPeriodUs = this.startOfPlaylistInPeriodUs + segment.relativeStartTimeUs;
            return segment.durationUs + segmentStartTimeInPeriodUs;
        }
    }
}
