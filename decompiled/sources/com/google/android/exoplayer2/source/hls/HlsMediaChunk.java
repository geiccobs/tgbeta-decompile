package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
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
/* loaded from: classes.dex */
public final class HlsMediaChunk extends MediaChunk {
    private static final PositionHolder DUMMY_POSITION_HOLDER = new PositionHolder();
    private static final AtomicInteger uidSource = new AtomicInteger();
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

    public static HlsMediaChunk createInstance(HlsExtractorFactory hlsExtractorFactory, DataSource dataSource, Format format, long j, HlsMediaPlaylist hlsMediaPlaylist, int i, Uri uri, List<Format> list, int i2, Object obj, boolean z, TimestampAdjusterProvider timestampAdjusterProvider, HlsMediaChunk hlsMediaChunk, byte[] bArr, byte[] bArr2) {
        DataSource dataSource2;
        boolean z2;
        DataSpec dataSpec;
        boolean z3;
        ParsableByteArray parsableByteArray;
        Id3Decoder id3Decoder;
        Extractor extractor;
        HlsMediaPlaylist.Segment segment = hlsMediaPlaylist.segments.get(i);
        DataSpec dataSpec2 = new DataSpec(UriUtil.resolveToUri(hlsMediaPlaylist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null);
        boolean z4 = bArr != null;
        DataSource buildDataSource = buildDataSource(dataSource, bArr, z4 ? getEncryptionIvArray((String) Assertions.checkNotNull(segment.encryptionIV)) : null);
        HlsMediaPlaylist.Segment segment2 = segment.initializationSegment;
        if (segment2 != null) {
            boolean z5 = bArr2 != null;
            byte[] encryptionIvArray = z5 ? getEncryptionIvArray((String) Assertions.checkNotNull(segment2.encryptionIV)) : null;
            DataSpec dataSpec3 = new DataSpec(UriUtil.resolveToUri(hlsMediaPlaylist.baseUri, segment2.url), segment2.byterangeOffset, segment2.byterangeLength, null);
            z2 = z5;
            dataSource2 = buildDataSource(dataSource, bArr2, encryptionIvArray);
            dataSpec = dataSpec3;
        } else {
            dataSpec = null;
            z2 = false;
            dataSource2 = null;
        }
        long j2 = j + segment.relativeStartTimeUs;
        long j3 = j2 + segment.durationUs;
        int i3 = hlsMediaPlaylist.discontinuitySequence + segment.relativeDiscontinuitySequence;
        if (hlsMediaChunk != null) {
            Id3Decoder id3Decoder2 = hlsMediaChunk.id3Decoder;
            ParsableByteArray parsableByteArray2 = hlsMediaChunk.scratchId3Data;
            boolean z6 = !uri.equals(hlsMediaChunk.playlistUrl) || !hlsMediaChunk.loadCompleted;
            id3Decoder = id3Decoder2;
            parsableByteArray = parsableByteArray2;
            extractor = (!hlsMediaChunk.isExtractorReusable || hlsMediaChunk.discontinuitySequenceNumber != i3 || z6) ? null : hlsMediaChunk.extractor;
            z3 = z6;
        } else {
            id3Decoder = new Id3Decoder();
            parsableByteArray = new ParsableByteArray(10);
            extractor = null;
            z3 = false;
        }
        return new HlsMediaChunk(hlsExtractorFactory, buildDataSource, dataSpec2, format, z4, dataSource2, dataSpec, z2, uri, list, i2, obj, j2, j3, hlsMediaPlaylist.mediaSequence + i, i3, segment.hasGapTag, z, timestampAdjusterProvider.getAdjuster(i3), segment.drmInitData, extractor, id3Decoder, parsableByteArray, z3);
    }

    private HlsMediaChunk(HlsExtractorFactory hlsExtractorFactory, DataSource dataSource, DataSpec dataSpec, Format format, boolean z, DataSource dataSource2, DataSpec dataSpec2, boolean z2, Uri uri, List<Format> list, int i, Object obj, long j, long j2, long j3, int i2, boolean z3, boolean z4, TimestampAdjuster timestampAdjuster, DrmInitData drmInitData, Extractor extractor, Id3Decoder id3Decoder, ParsableByteArray parsableByteArray, boolean z5) {
        super(dataSource, dataSpec, format, i, obj, j, j2, j3);
        this.mediaSegmentEncrypted = z;
        this.discontinuitySequenceNumber = i2;
        this.initDataSpec = dataSpec2;
        this.initDataSource = dataSource2;
        this.initDataLoadRequired = dataSpec2 != null;
        this.initSegmentEncrypted = z2;
        this.playlistUrl = uri;
        this.isMasterTimestampSource = z4;
        this.timestampAdjuster = timestampAdjuster;
        this.hasGapTag = z3;
        this.extractorFactory = hlsExtractorFactory;
        this.muxedCaptionFormats = list;
        this.drmInitData = drmInitData;
        this.previousExtractor = extractor;
        this.id3Decoder = id3Decoder;
        this.scratchId3Data = parsableByteArray;
        this.shouldSpliceIn = z5;
        this.uid = uidSource.getAndIncrement();
    }

    public void init(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        this.output = hlsSampleStreamWrapper;
        hlsSampleStreamWrapper.init(this.uid, this.shouldSpliceIn);
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
    private void feedDataToExtractor(DataSource dataSource, DataSpec dataSpec, boolean z) throws IOException, InterruptedException {
        DataSpec dataSpec2;
        boolean z2;
        int i = 0;
        if (z) {
            z2 = this.nextLoadPosition != 0;
            dataSpec2 = dataSpec;
        } else {
            dataSpec2 = dataSpec.subrange(this.nextLoadPosition);
            z2 = false;
        }
        try {
            DefaultExtractorInput prepareExtraction = prepareExtraction(dataSource, dataSpec2);
            if (z2) {
                prepareExtraction.skipFully(this.nextLoadPosition);
            }
            while (i == 0 && !this.loadCanceled) {
                i = this.extractor.read(prepareExtraction, DUMMY_POSITION_HOLDER);
            }
            this.nextLoadPosition = (int) (prepareExtraction.getPosition() - dataSpec.absoluteStreamPosition);
        } finally {
            Util.closeQuietly(dataSource);
        }
    }

    @EnsuresNonNull({"extractor"})
    @RequiresNonNull({"output"})
    private DefaultExtractorInput prepareExtraction(DataSource dataSource, DataSpec dataSpec) throws IOException, InterruptedException {
        DefaultExtractorInput defaultExtractorInput;
        long j;
        DefaultExtractorInput defaultExtractorInput2 = new DefaultExtractorInput(dataSource, dataSpec.absoluteStreamPosition, dataSource.open(dataSpec));
        if (this.extractor == null) {
            long peekId3PrivTimestamp = peekId3PrivTimestamp(defaultExtractorInput2);
            defaultExtractorInput2.resetPeekPosition();
            defaultExtractorInput = defaultExtractorInput2;
            HlsExtractorFactory.Result createExtractor = this.extractorFactory.createExtractor(this.previousExtractor, dataSpec.uri, this.trackFormat, this.muxedCaptionFormats, this.timestampAdjuster, dataSource.getResponseHeaders(), defaultExtractorInput2);
            this.extractor = createExtractor.extractor;
            this.isExtractorReusable = createExtractor.isReusable;
            if (createExtractor.isPackedAudioExtractor) {
                HlsSampleStreamWrapper hlsSampleStreamWrapper = this.output;
                if (peekId3PrivTimestamp != -9223372036854775807L) {
                    j = this.timestampAdjuster.adjustTsTimestamp(peekId3PrivTimestamp);
                } else {
                    j = this.startTimeUs;
                }
                hlsSampleStreamWrapper.setSampleOffsetUs(j);
            } else {
                this.output.setSampleOffsetUs(0L);
            }
            this.output.onNewExtractor();
            this.extractor.init(this.output);
        } else {
            defaultExtractorInput = defaultExtractorInput2;
        }
        this.output.setDrmInitData(this.drmInitData);
        return defaultExtractorInput;
    }

    private long peekId3PrivTimestamp(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.resetPeekPosition();
        try {
            extractorInput.peekFully(this.scratchId3Data.data, 0, 10);
            this.scratchId3Data.reset(10);
        } catch (EOFException unused) {
        }
        if (this.scratchId3Data.readUnsignedInt24() != 4801587) {
            return -9223372036854775807L;
        }
        this.scratchId3Data.skipBytes(3);
        int readSynchSafeInt = this.scratchId3Data.readSynchSafeInt();
        int i = readSynchSafeInt + 10;
        if (i > this.scratchId3Data.capacity()) {
            ParsableByteArray parsableByteArray = this.scratchId3Data;
            byte[] bArr = parsableByteArray.data;
            parsableByteArray.reset(i);
            System.arraycopy(bArr, 0, this.scratchId3Data.data, 0, 10);
        }
        extractorInput.peekFully(this.scratchId3Data.data, 10, readSynchSafeInt);
        Metadata decode = this.id3Decoder.decode(this.scratchId3Data.data, readSynchSafeInt);
        if (decode == null) {
            return -9223372036854775807L;
        }
        int length = decode.length();
        for (int i2 = 0; i2 < length; i2++) {
            Metadata.Entry entry = decode.get(i2);
            if (entry instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) entry;
                if ("com.apple.streaming.transportStreamTimestamp".equals(privFrame.owner)) {
                    System.arraycopy(privFrame.privateData, 0, this.scratchId3Data.data, 0, 8);
                    this.scratchId3Data.reset(8);
                    return this.scratchId3Data.readLong() & 8589934591L;
                }
            }
        }
        return -9223372036854775807L;
    }

    private static byte[] getEncryptionIvArray(String str) {
        if (Util.toLowerInvariant(str).startsWith("0x")) {
            str = str.substring(2);
        }
        byte[] byteArray = new BigInteger(str, 16).toByteArray();
        byte[] bArr = new byte[16];
        int length = byteArray.length > 16 ? byteArray.length - 16 : 0;
        System.arraycopy(byteArray, length, bArr, (16 - byteArray.length) + length, byteArray.length - length);
        return bArr;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] bArr, byte[] bArr2) {
        if (bArr != null) {
            Assertions.checkNotNull(bArr2);
            return new Aes128DataSource(dataSource, bArr, bArr2);
        }
        return dataSource;
    }
}
