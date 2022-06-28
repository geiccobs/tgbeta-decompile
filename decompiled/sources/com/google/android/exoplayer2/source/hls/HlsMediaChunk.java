package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.hls.HlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
/* loaded from: classes3.dex */
public final class HlsMediaChunk extends MediaChunk {
    public static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
    public final int discontinuitySequenceNumber;
    private final DrmInitData drmInitData;
    private Extractor extractor;
    private final HlsExtractorFactory extractorFactory;
    private final boolean hasGapTag;
    private final Id3Decoder id3Decoder;
    private boolean initDataLoadRequired;
    private final DataSource initDataSource;
    private final DataSpec initDataSpec;
    private final boolean initSegmentEncrypted;
    private boolean isExtractorReusable;
    private final boolean isMasterTimestampSource;
    private volatile boolean loadCanceled;
    private boolean loadCompleted;
    private final boolean mediaSegmentEncrypted;
    private final List<Format> muxedCaptionFormats;
    private int nextLoadPosition;
    private HlsSampleStreamWrapper output;
    public final Uri playlistUrl;
    private final Extractor previousExtractor;
    private final ParsableByteArray scratchId3Data;
    private final boolean shouldSpliceIn;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid;
    private static final PositionHolder DUMMY_POSITION_HOLDER = new PositionHolder();
    private static final AtomicInteger uidSource = new AtomicInteger();

    public static HlsMediaChunk createInstance(HlsExtractorFactory extractorFactory, DataSource dataSource, Format format, long startOfPlaylistInPeriodUs, HlsMediaPlaylist mediaPlaylist, int segmentIndexInPlaylist, Uri playlistUrl, List<Format> muxedCaptionFormats, int trackSelectionReason, Object trackSelectionData, boolean isMasterTimestampSource, TimestampAdjusterProvider timestampAdjusterProvider, HlsMediaChunk previousChunk, byte[] mediaSegmentKey, byte[] initSegmentKey) {
        byte[] bArr;
        DataSource initDataSource;
        boolean initSegmentEncrypted;
        DataSpec initDataSpec;
        boolean shouldSpliceIn;
        ParsableByteArray scratchId3Data;
        Id3Decoder id3Decoder;
        Extractor previousExtractor;
        byte[] initSegmentIv;
        HlsMediaPlaylist.Segment mediaSegment = mediaPlaylist.segments.get(segmentIndexInPlaylist);
        DataSpec dataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, mediaSegment.url), mediaSegment.byterangeOffset, mediaSegment.byterangeLength, null);
        boolean mediaSegmentEncrypted = mediaSegmentKey != null;
        if (mediaSegmentEncrypted) {
            bArr = getEncryptionIvArray((String) Assertions.checkNotNull(mediaSegment.encryptionIV));
        } else {
            bArr = null;
        }
        byte[] mediaSegmentIv = bArr;
        DataSource mediaDataSource = buildDataSource(dataSource, mediaSegmentKey, mediaSegmentIv);
        HlsMediaPlaylist.Segment initSegment = mediaSegment.initializationSegment;
        if (initSegment == null) {
            initSegmentEncrypted = false;
            initDataSource = null;
            initDataSpec = null;
        } else {
            boolean initSegmentEncrypted2 = initSegmentKey != null;
            if (initSegmentEncrypted2) {
                initSegmentIv = getEncryptionIvArray((String) Assertions.checkNotNull(initSegment.encryptionIV));
            } else {
                initSegmentIv = null;
            }
            Uri initSegmentUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, initSegment.url);
            DataSpec initDataSpec2 = new DataSpec(initSegmentUri, initSegment.byterangeOffset, initSegment.byterangeLength, null);
            DataSource initDataSource2 = buildDataSource(dataSource, initSegmentKey, initSegmentIv);
            initDataSpec = initDataSpec2;
            initDataSource = initDataSource2;
            initSegmentEncrypted = initSegmentEncrypted2;
        }
        long segmentStartTimeInPeriodUs = startOfPlaylistInPeriodUs + mediaSegment.relativeStartTimeUs;
        long segmentEndTimeInPeriodUs = segmentStartTimeInPeriodUs + mediaSegment.durationUs;
        int discontinuitySequenceNumber = mediaPlaylist.discontinuitySequence + mediaSegment.relativeDiscontinuitySequence;
        if (previousChunk != null) {
            Id3Decoder id3Decoder2 = previousChunk.id3Decoder;
            ParsableByteArray scratchId3Data2 = previousChunk.scratchId3Data;
            boolean shouldSpliceIn2 = !playlistUrl.equals(previousChunk.playlistUrl) || !previousChunk.loadCompleted;
            if (previousChunk.isExtractorReusable && previousChunk.discontinuitySequenceNumber == discontinuitySequenceNumber && !shouldSpliceIn2) {
                previousExtractor = previousChunk.extractor;
            } else {
                previousExtractor = null;
            }
            id3Decoder = id3Decoder2;
            scratchId3Data = scratchId3Data2;
            shouldSpliceIn = shouldSpliceIn2;
        } else {
            Id3Decoder id3Decoder3 = new Id3Decoder();
            ParsableByteArray scratchId3Data3 = new ParsableByteArray(10);
            previousExtractor = null;
            id3Decoder = id3Decoder3;
            scratchId3Data = scratchId3Data3;
            shouldSpliceIn = false;
        }
        return new HlsMediaChunk(extractorFactory, mediaDataSource, dataSpec, format, mediaSegmentEncrypted, initDataSource, initDataSpec, initSegmentEncrypted, playlistUrl, muxedCaptionFormats, trackSelectionReason, trackSelectionData, segmentStartTimeInPeriodUs, segmentEndTimeInPeriodUs, mediaPlaylist.mediaSequence + segmentIndexInPlaylist, discontinuitySequenceNumber, mediaSegment.hasGapTag, isMasterTimestampSource, timestampAdjusterProvider.getAdjuster(discontinuitySequenceNumber), mediaSegment.drmInitData, previousExtractor, id3Decoder, scratchId3Data, shouldSpliceIn);
    }

    private HlsMediaChunk(HlsExtractorFactory extractorFactory, DataSource mediaDataSource, DataSpec dataSpec, Format format, boolean mediaSegmentEncrypted, DataSource initDataSource, DataSpec initDataSpec, boolean initSegmentEncrypted, Uri playlistUrl, List<Format> muxedCaptionFormats, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, long chunkMediaSequence, int discontinuitySequenceNumber, boolean hasGapTag, boolean isMasterTimestampSource, TimestampAdjuster timestampAdjuster, DrmInitData drmInitData, Extractor previousExtractor, Id3Decoder id3Decoder, ParsableByteArray scratchId3Data, boolean shouldSpliceIn) {
        super(mediaDataSource, dataSpec, format, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, chunkMediaSequence);
        this.mediaSegmentEncrypted = mediaSegmentEncrypted;
        this.discontinuitySequenceNumber = discontinuitySequenceNumber;
        this.initDataSpec = initDataSpec;
        this.initDataSource = initDataSource;
        this.initDataLoadRequired = initDataSpec != null;
        this.initSegmentEncrypted = initSegmentEncrypted;
        this.playlistUrl = playlistUrl;
        this.isMasterTimestampSource = isMasterTimestampSource;
        this.timestampAdjuster = timestampAdjuster;
        this.hasGapTag = hasGapTag;
        this.extractorFactory = extractorFactory;
        this.muxedCaptionFormats = muxedCaptionFormats;
        this.drmInitData = drmInitData;
        this.previousExtractor = previousExtractor;
        this.id3Decoder = id3Decoder;
        this.scratchId3Data = scratchId3Data;
        this.shouldSpliceIn = shouldSpliceIn;
        this.uid = uidSource.getAndIncrement();
    }

    public void init(HlsSampleStreamWrapper output) {
        this.output = output;
        output.init(this.uid, this.shouldSpliceIn);
    }

    @Override // com.google.android.exoplayer2.source.chunk.MediaChunk
    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public void cancelLoad() {
        this.loadCanceled = true;
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public void load() throws IOException, InterruptedException {
        Extractor extractor;
        Assertions.checkNotNull(this.output);
        if (this.extractor == null && (extractor = this.previousExtractor) != null) {
            this.extractor = extractor;
            this.isExtractorReusable = true;
            this.initDataLoadRequired = false;
        }
        maybeLoadInitData();
        if (!this.loadCanceled) {
            if (!this.hasGapTag) {
                loadMedia();
            }
            this.loadCompleted = true;
        }
    }

    @RequiresNonNull({"output"})
    private void maybeLoadInitData() throws IOException, InterruptedException {
        if (!this.initDataLoadRequired) {
            return;
        }
        Assertions.checkNotNull(this.initDataSource);
        Assertions.checkNotNull(this.initDataSpec);
        feedDataToExtractor(this.initDataSource, this.initDataSpec, this.initSegmentEncrypted);
        this.nextLoadPosition = 0;
        this.initDataLoadRequired = false;
    }

    @RequiresNonNull({"output"})
    private void loadMedia() throws IOException, InterruptedException {
        if (!this.isMasterTimestampSource) {
            this.timestampAdjuster.waitUntilInitialized();
        } else if (this.timestampAdjuster.getFirstSampleTimestampUs() == Long.MAX_VALUE) {
            this.timestampAdjuster.setFirstSampleTimestampUs(this.startTimeUs);
        }
        feedDataToExtractor(this.dataSource, this.dataSpec, this.mediaSegmentEncrypted);
    }

    @RequiresNonNull({"output"})
    private void feedDataToExtractor(DataSource dataSource, DataSpec dataSpec, boolean dataIsEncrypted) throws IOException, InterruptedException {
        boolean skipLoadedBytes;
        DataSpec loadDataSpec;
        if (dataIsEncrypted) {
            loadDataSpec = dataSpec;
            skipLoadedBytes = this.nextLoadPosition != 0;
        } else {
            loadDataSpec = dataSpec.subrange(this.nextLoadPosition);
            skipLoadedBytes = false;
        }
        try {
            ExtractorInput input = prepareExtraction(dataSource, loadDataSpec);
            if (skipLoadedBytes) {
                input.skipFully(this.nextLoadPosition);
            }
            int result = 0;
            while (result == 0 && !this.loadCanceled) {
                result = this.extractor.read(input, DUMMY_POSITION_HOLDER);
            }
            this.nextLoadPosition = (int) (input.getPosition() - dataSpec.absoluteStreamPosition);
        } finally {
            Util.closeQuietly(dataSource);
        }
    }

    @EnsuresNonNull({"extractor"})
    @RequiresNonNull({"output"})
    private DefaultExtractorInput prepareExtraction(DataSource dataSource, DataSpec dataSpec) throws IOException, InterruptedException {
        long j;
        long bytesToRead = dataSource.open(dataSpec);
        DefaultExtractorInput extractorInput = new DefaultExtractorInput(dataSource, dataSpec.absoluteStreamPosition, bytesToRead);
        if (this.extractor == null) {
            long id3Timestamp = peekId3PrivTimestamp(extractorInput);
            extractorInput.resetPeekPosition();
            HlsExtractorFactory.Result result = this.extractorFactory.createExtractor(this.previousExtractor, dataSpec.uri, this.trackFormat, this.muxedCaptionFormats, this.timestampAdjuster, dataSource.getResponseHeaders(), extractorInput);
            this.extractor = result.extractor;
            this.isExtractorReusable = result.isReusable;
            if (result.isPackedAudioExtractor) {
                HlsSampleStreamWrapper hlsSampleStreamWrapper = this.output;
                if (id3Timestamp != C.TIME_UNSET) {
                    j = this.timestampAdjuster.adjustTsTimestamp(id3Timestamp);
                } else {
                    j = this.startTimeUs;
                }
                hlsSampleStreamWrapper.setSampleOffsetUs(j);
            } else {
                this.output.setSampleOffsetUs(0L);
            }
            this.output.onNewExtractor();
            this.extractor.init(this.output);
        }
        this.output.setDrmInitData(this.drmInitData);
        return extractorInput;
    }

    private long peekId3PrivTimestamp(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        try {
            input.peekFully(this.scratchId3Data.data, 0, 10);
            this.scratchId3Data.reset(10);
            int id = this.scratchId3Data.readUnsignedInt24();
            if (id != 4801587) {
                return C.TIME_UNSET;
            }
            this.scratchId3Data.skipBytes(3);
            int id3Size = this.scratchId3Data.readSynchSafeInt();
            int requiredCapacity = id3Size + 10;
            if (requiredCapacity > this.scratchId3Data.capacity()) {
                byte[] data = this.scratchId3Data.data;
                this.scratchId3Data.reset(requiredCapacity);
                System.arraycopy(data, 0, this.scratchId3Data.data, 0, 10);
            }
            input.peekFully(this.scratchId3Data.data, 10, id3Size);
            Metadata metadata = this.id3Decoder.decode(this.scratchId3Data.data, id3Size);
            if (metadata == null) {
                return C.TIME_UNSET;
            }
            int metadataLength = metadata.length();
            for (int i = 0; i < metadataLength; i++) {
                Metadata.Entry frame = metadata.get(i);
                if (frame instanceof PrivFrame) {
                    PrivFrame privFrame = (PrivFrame) frame;
                    if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                        System.arraycopy(privFrame.privateData, 0, this.scratchId3Data.data, 0, 8);
                        this.scratchId3Data.reset(8);
                        return this.scratchId3Data.readLong() & 8589934591L;
                    }
                }
            }
            return C.TIME_UNSET;
        } catch (EOFException e) {
            return C.TIME_UNSET;
        }
    }

    private static byte[] getEncryptionIvArray(String ivString) {
        String trimmedIv;
        if (Util.toLowerInvariant(ivString).startsWith("0x")) {
            trimmedIv = ivString.substring(2);
        } else {
            trimmedIv = ivString;
        }
        byte[] ivData = new BigInteger(trimmedIv, 16).toByteArray();
        byte[] ivDataWithPadding = new byte[16];
        int offset = ivData.length > 16 ? ivData.length - 16 : 0;
        System.arraycopy(ivData, offset, ivDataWithPadding, (ivDataWithPadding.length - ivData.length) + offset, ivData.length - offset);
        return ivDataWithPadding;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        if (fullSegmentEncryptionKey != null) {
            Assertions.checkNotNull(encryptionIv);
            return new Aes128DataSource(dataSource, fullSegmentEncryptionKey, encryptionIv);
        }
        return dataSource;
    }
}
