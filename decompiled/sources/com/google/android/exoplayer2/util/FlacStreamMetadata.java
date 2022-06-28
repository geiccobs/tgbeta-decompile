package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.flac.PictureFrame;
import com.google.android.exoplayer2.metadata.flac.VorbisComment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class FlacStreamMetadata {
    public static final int NOT_IN_LOOKUP_TABLE = -1;
    private static final String SEPARATOR = "=";
    private static final String TAG = "FlacStreamMetadata";
    public final int bitsPerSample;
    public final int bitsPerSampleLookupKey;
    public final int channels;
    public final int maxBlockSizeSamples;
    public final int maxFrameSize;
    private final Metadata metadata;
    public final int minBlockSizeSamples;
    public final int minFrameSize;
    public final int sampleRate;
    public final int sampleRateLookupKey;
    public final SeekTable seekTable;
    public final long totalSamples;

    /* loaded from: classes3.dex */
    public static class SeekTable {
        public final long[] pointOffsets;
        public final long[] pointSampleNumbers;

        public SeekTable(long[] pointSampleNumbers, long[] pointOffsets) {
            this.pointSampleNumbers = pointSampleNumbers;
            this.pointOffsets = pointOffsets;
        }
    }

    public FlacStreamMetadata(byte[] data, int offset) {
        ParsableBitArray scratch = new ParsableBitArray(data);
        scratch.setPosition(offset * 8);
        this.minBlockSizeSamples = scratch.readBits(16);
        this.maxBlockSizeSamples = scratch.readBits(16);
        this.minFrameSize = scratch.readBits(24);
        this.maxFrameSize = scratch.readBits(24);
        int readBits = scratch.readBits(20);
        this.sampleRate = readBits;
        this.sampleRateLookupKey = getSampleRateLookupKey(readBits);
        this.channels = scratch.readBits(3) + 1;
        int readBits2 = scratch.readBits(5) + 1;
        this.bitsPerSample = readBits2;
        this.bitsPerSampleLookupKey = getBitsPerSampleLookupKey(readBits2);
        this.totalSamples = scratch.readBitsToLong(36);
        this.seekTable = null;
        this.metadata = null;
    }

    public FlacStreamMetadata(int minBlockSizeSamples, int maxBlockSizeSamples, int minFrameSize, int maxFrameSize, int sampleRate, int channels, int bitsPerSample, long totalSamples, ArrayList<String> vorbisComments, ArrayList<PictureFrame> pictureFrames) {
        this(minBlockSizeSamples, maxBlockSizeSamples, minFrameSize, maxFrameSize, sampleRate, channels, bitsPerSample, totalSamples, (SeekTable) null, buildMetadata(vorbisComments, pictureFrames));
    }

    private FlacStreamMetadata(int minBlockSizeSamples, int maxBlockSizeSamples, int minFrameSize, int maxFrameSize, int sampleRate, int channels, int bitsPerSample, long totalSamples, SeekTable seekTable, Metadata metadata) {
        this.minBlockSizeSamples = minBlockSizeSamples;
        this.maxBlockSizeSamples = maxBlockSizeSamples;
        this.minFrameSize = minFrameSize;
        this.maxFrameSize = maxFrameSize;
        this.sampleRate = sampleRate;
        this.sampleRateLookupKey = getSampleRateLookupKey(sampleRate);
        this.channels = channels;
        this.bitsPerSample = bitsPerSample;
        this.bitsPerSampleLookupKey = getBitsPerSampleLookupKey(bitsPerSample);
        this.totalSamples = totalSamples;
        this.seekTable = seekTable;
        this.metadata = metadata;
    }

    public int getMaxDecodedFrameSize() {
        return this.maxBlockSizeSamples * this.channels * (this.bitsPerSample / 8);
    }

    public int getBitRate() {
        return this.bitsPerSample * this.sampleRate * this.channels;
    }

    public long getDurationUs() {
        long j = this.totalSamples;
        return j == 0 ? C.TIME_UNSET : (j * 1000000) / this.sampleRate;
    }

    public long getSampleNumber(long timeUs) {
        long sampleNumber = (this.sampleRate * timeUs) / 1000000;
        return Util.constrainValue(sampleNumber, 0L, this.totalSamples - 1);
    }

    public long getApproxBytesPerFrame() {
        long blockSizeSamples;
        int i = this.maxFrameSize;
        if (i > 0) {
            long approxBytesPerFrame = ((i + this.minFrameSize) / 2) + 1;
            return approxBytesPerFrame;
        }
        int i2 = this.minBlockSizeSamples;
        if (i2 == this.maxBlockSizeSamples && i2 > 0) {
            blockSizeSamples = i2;
        } else {
            blockSizeSamples = 4096;
        }
        long approxBytesPerFrame2 = (((this.channels * blockSizeSamples) * this.bitsPerSample) / 8) + 64;
        return approxBytesPerFrame2;
    }

    public Format getFormat(byte[] streamMarkerAndInfoBlock, Metadata id3Metadata) {
        streamMarkerAndInfoBlock[4] = Byte.MIN_VALUE;
        int i = this.maxFrameSize;
        int maxInputSize = i > 0 ? i : -1;
        Metadata metadataWithId3 = getMetadataCopyWithAppendedEntriesFrom(id3Metadata);
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_FLAC, null, getBitRate(), maxInputSize, this.channels, this.sampleRate, -1, 0, 0, Collections.singletonList(streamMarkerAndInfoBlock), null, 0, null, metadataWithId3);
    }

    public Metadata getMetadataCopyWithAppendedEntriesFrom(Metadata other) {
        Metadata metadata = this.metadata;
        return metadata == null ? other : metadata.copyWithAppendedEntriesFrom(other);
    }

    public FlacStreamMetadata copyWithSeekTable(SeekTable seekTable) {
        return new FlacStreamMetadata(this.minBlockSizeSamples, this.maxBlockSizeSamples, this.minFrameSize, this.maxFrameSize, this.sampleRate, this.channels, this.bitsPerSample, this.totalSamples, seekTable, this.metadata);
    }

    public FlacStreamMetadata copyWithVorbisComments(List<String> vorbisComments) {
        Metadata appendedMetadata = getMetadataCopyWithAppendedEntriesFrom(buildMetadata(vorbisComments, Collections.emptyList()));
        return new FlacStreamMetadata(this.minBlockSizeSamples, this.maxBlockSizeSamples, this.minFrameSize, this.maxFrameSize, this.sampleRate, this.channels, this.bitsPerSample, this.totalSamples, this.seekTable, appendedMetadata);
    }

    public FlacStreamMetadata copyWithPictureFrames(List<PictureFrame> pictureFrames) {
        Metadata appendedMetadata = getMetadataCopyWithAppendedEntriesFrom(buildMetadata(Collections.emptyList(), pictureFrames));
        return new FlacStreamMetadata(this.minBlockSizeSamples, this.maxBlockSizeSamples, this.minFrameSize, this.maxFrameSize, this.sampleRate, this.channels, this.bitsPerSample, this.totalSamples, this.seekTable, appendedMetadata);
    }

    private static int getSampleRateLookupKey(int sampleRate) {
        switch (sampleRate) {
            case 8000:
                return 4;
            case 16000:
                return 5;
            case 22050:
                return 6;
            case 24000:
                return 7;
            case 32000:
                return 8;
            case 44100:
                return 9;
            case 48000:
                return 10;
            case 88200:
                return 1;
            case 96000:
                return 11;
            case 176400:
                return 2;
            case 192000:
                return 3;
            default:
                return -1;
        }
    }

    private static int getBitsPerSampleLookupKey(int bitsPerSample) {
        switch (bitsPerSample) {
            case 8:
                return 1;
            case 12:
                return 2;
            case 16:
                return 4;
            case 20:
                return 5;
            case 24:
                return 6;
            default:
                return -1;
        }
    }

    private static Metadata buildMetadata(List<String> vorbisComments, List<PictureFrame> pictureFrames) {
        if (!vorbisComments.isEmpty() || !pictureFrames.isEmpty()) {
            ArrayList<Metadata.Entry> metadataEntries = new ArrayList<>();
            for (int i = 0; i < vorbisComments.size(); i++) {
                String vorbisComment = vorbisComments.get(i);
                String[] keyAndValue = Util.splitAtFirst(vorbisComment, SEPARATOR);
                if (keyAndValue.length != 2) {
                    Log.w(TAG, "Failed to parse Vorbis comment: " + vorbisComment);
                } else {
                    VorbisComment entry = new VorbisComment(keyAndValue[0], keyAndValue[1]);
                    metadataEntries.add(entry);
                }
            }
            metadataEntries.addAll(pictureFrames);
            if (!metadataEntries.isEmpty()) {
                return new Metadata(metadataEntries);
            }
            return null;
        }
        return null;
    }
}
