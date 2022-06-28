package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.VorbisUtil;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.flac.PictureFrame;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class FlacMetadataReader {
    private static final int SEEK_POINT_SIZE = 18;
    private static final int STREAM_MARKER = 1716281667;
    private static final int SYNC_CODE = 16382;

    /* loaded from: classes3.dex */
    public static final class FlacStreamMetadataHolder {
        public FlacStreamMetadata flacStreamMetadata;

        public FlacStreamMetadataHolder(FlacStreamMetadata flacStreamMetadata) {
            this.flacStreamMetadata = flacStreamMetadata;
        }
    }

    public static Metadata peekId3Metadata(ExtractorInput input, boolean parseData) throws IOException, InterruptedException {
        Id3Decoder.FramePredicate id3FramePredicate = parseData ? null : Id3Decoder.NO_FRAMES_PREDICATE;
        Metadata id3Metadata = new Id3Peeker().peekId3Data(input, id3FramePredicate);
        if (id3Metadata == null || id3Metadata.length() == 0) {
            return null;
        }
        return id3Metadata;
    }

    public static boolean checkAndPeekStreamMarker(ExtractorInput input) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(4);
        input.peekFully(scratch.data, 0, 4);
        return scratch.readUnsignedInt() == 1716281667;
    }

    public static Metadata readId3Metadata(ExtractorInput input, boolean parseData) throws IOException, InterruptedException {
        input.resetPeekPosition();
        long startingPeekPosition = input.getPeekPosition();
        Metadata id3Metadata = peekId3Metadata(input, parseData);
        int peekedId3Bytes = (int) (input.getPeekPosition() - startingPeekPosition);
        input.skipFully(peekedId3Bytes);
        return id3Metadata;
    }

    public static void readStreamMarker(ExtractorInput input) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(4);
        input.readFully(scratch.data, 0, 4);
        if (scratch.readUnsignedInt() != 1716281667) {
            throw new ParserException("Failed to read FLAC stream marker.");
        }
    }

    public static boolean readMetadataBlock(ExtractorInput input, FlacStreamMetadataHolder metadataHolder) throws IOException, InterruptedException {
        input.resetPeekPosition();
        ParsableBitArray scratch = new ParsableBitArray(new byte[4]);
        input.peekFully(scratch.data, 0, 4);
        boolean isLastMetadataBlock = scratch.readBit();
        int type = scratch.readBits(7);
        int length = scratch.readBits(24) + 4;
        if (type == 0) {
            metadataHolder.flacStreamMetadata = readStreamInfoBlock(input);
        } else {
            FlacStreamMetadata flacStreamMetadata = metadataHolder.flacStreamMetadata;
            if (flacStreamMetadata == null) {
                throw new IllegalArgumentException();
            }
            if (type == 3) {
                FlacStreamMetadata.SeekTable seekTable = readSeekTableMetadataBlock(input, length);
                metadataHolder.flacStreamMetadata = flacStreamMetadata.copyWithSeekTable(seekTable);
            } else if (type == 4) {
                List<String> vorbisComments = readVorbisCommentMetadataBlock(input, length);
                metadataHolder.flacStreamMetadata = flacStreamMetadata.copyWithVorbisComments(vorbisComments);
            } else if (type == 6) {
                PictureFrame pictureFrame = readPictureMetadataBlock(input, length);
                metadataHolder.flacStreamMetadata = flacStreamMetadata.copyWithPictureFrames(Collections.singletonList(pictureFrame));
            } else {
                input.skipFully(length);
            }
        }
        return isLastMetadataBlock;
    }

    public static FlacStreamMetadata.SeekTable readSeekTableMetadataBlock(ParsableByteArray data) {
        data.skipBytes(1);
        int length = data.readUnsignedInt24();
        long seekTableEndPosition = data.getPosition() + length;
        int seekPointCount = length / 18;
        long[] pointSampleNumbers = new long[seekPointCount];
        long[] pointOffsets = new long[seekPointCount];
        int i = 0;
        while (true) {
            if (i >= seekPointCount) {
                break;
            }
            long sampleNumber = data.readLong();
            if (sampleNumber == -1) {
                pointSampleNumbers = Arrays.copyOf(pointSampleNumbers, i);
                pointOffsets = Arrays.copyOf(pointOffsets, i);
                break;
            }
            pointSampleNumbers[i] = sampleNumber;
            pointOffsets[i] = data.readLong();
            data.skipBytes(2);
            i++;
        }
        int i2 = data.getPosition();
        data.skipBytes((int) (seekTableEndPosition - i2));
        return new FlacStreamMetadata.SeekTable(pointSampleNumbers, pointOffsets);
    }

    public static int getFrameStartMarker(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        ParsableByteArray scratch = new ParsableByteArray(2);
        input.peekFully(scratch.data, 0, 2);
        int frameStartMarker = scratch.readUnsignedShort();
        int syncCode = frameStartMarker >> 2;
        if (syncCode != SYNC_CODE) {
            input.resetPeekPosition();
            throw new ParserException("First frame does not start with sync code.");
        }
        input.resetPeekPosition();
        return frameStartMarker;
    }

    private static FlacStreamMetadata readStreamInfoBlock(ExtractorInput input) throws IOException, InterruptedException {
        byte[] scratchData = new byte[38];
        input.readFully(scratchData, 0, 38);
        return new FlacStreamMetadata(scratchData, 4);
    }

    private static FlacStreamMetadata.SeekTable readSeekTableMetadataBlock(ExtractorInput input, int length) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(length);
        input.readFully(scratch.data, 0, length);
        return readSeekTableMetadataBlock(scratch);
    }

    private static List<String> readVorbisCommentMetadataBlock(ExtractorInput input, int length) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(length);
        input.readFully(scratch.data, 0, length);
        scratch.skipBytes(4);
        VorbisUtil.CommentHeader commentHeader = VorbisUtil.readVorbisCommentHeader(scratch, false, false);
        return Arrays.asList(commentHeader.comments);
    }

    private static PictureFrame readPictureMetadataBlock(ExtractorInput input, int length) throws IOException, InterruptedException {
        ParsableByteArray scratch = new ParsableByteArray(length);
        input.readFully(scratch.data, 0, length);
        scratch.skipBytes(4);
        int pictureType = scratch.readInt();
        int mimeTypeLength = scratch.readInt();
        String mimeType = scratch.readString(mimeTypeLength, Charset.forName(C.ASCII_NAME));
        int descriptionLength = scratch.readInt();
        String description = scratch.readString(descriptionLength);
        int width = scratch.readInt();
        int height = scratch.readInt();
        int depth = scratch.readInt();
        int colors = scratch.readInt();
        int pictureDataLength = scratch.readInt();
        byte[] pictureData = new byte[pictureDataLength];
        scratch.readBytes(pictureData, 0, pictureDataLength);
        return new PictureFrame(pictureType, mimeType, description, width, height, depth, colors, pictureData);
    }

    private FlacMetadataReader() {
    }
}
