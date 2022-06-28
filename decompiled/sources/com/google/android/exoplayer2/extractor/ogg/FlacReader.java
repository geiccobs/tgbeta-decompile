package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.FlacFrameReader;
import com.google.android.exoplayer2.extractor.FlacMetadataReader;
import com.google.android.exoplayer2.extractor.FlacSeekTableSeekMap;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.ogg.StreamReader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamMetadata;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class FlacReader extends StreamReader {
    private static final byte AUDIO_PACKET_TYPE = -1;
    private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
    private FlacOggSeeker flacOggSeeker;
    private FlacStreamMetadata streamMetadata;

    public static boolean verifyBitstreamType(ParsableByteArray data) {
        return data.bytesLeft() >= 5 && data.readUnsignedByte() == 127 && data.readUnsignedInt() == 1179402563;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.exoplayer2.extractor.ogg.StreamReader
    public void reset(boolean headerData) {
        super.reset(headerData);
        if (headerData) {
            this.streamMetadata = null;
            this.flacOggSeeker = null;
        }
    }

    private static boolean isAudioPacket(byte[] data) {
        return data[0] == -1;
    }

    @Override // com.google.android.exoplayer2.extractor.ogg.StreamReader
    protected long preparePayload(ParsableByteArray packet) {
        if (!isAudioPacket(packet.data)) {
            return -1L;
        }
        return getFlacFrameBlockSize(packet);
    }

    @Override // com.google.android.exoplayer2.extractor.ogg.StreamReader
    protected boolean readHeaders(ParsableByteArray packet, long position, StreamReader.SetupData setupData) {
        byte[] data = packet.data;
        if (this.streamMetadata == null) {
            this.streamMetadata = new FlacStreamMetadata(data, 17);
            byte[] metadata = Arrays.copyOfRange(data, 9, packet.limit());
            setupData.format = this.streamMetadata.getFormat(metadata, null);
            return true;
        } else if ((data[0] & Byte.MAX_VALUE) == 3) {
            this.flacOggSeeker = new FlacOggSeeker();
            FlacStreamMetadata.SeekTable seekTable = FlacMetadataReader.readSeekTableMetadataBlock(packet);
            this.streamMetadata = this.streamMetadata.copyWithSeekTable(seekTable);
            return true;
        } else if (isAudioPacket(data)) {
            FlacOggSeeker flacOggSeeker = this.flacOggSeeker;
            if (flacOggSeeker != null) {
                flacOggSeeker.setFirstFrameOffset(position);
                setupData.oggSeeker = this.flacOggSeeker;
            }
            return false;
        } else {
            return true;
        }
    }

    private int getFlacFrameBlockSize(ParsableByteArray packet) {
        int blockSizeKey = (packet.data[2] & AUDIO_PACKET_TYPE) >> 4;
        if (blockSizeKey == 6 || blockSizeKey == 7) {
            packet.skipBytes(4);
            packet.readUtf8EncodedLong();
        }
        int result = FlacFrameReader.readFrameBlockSizeSamplesFromKey(packet, blockSizeKey);
        packet.setPosition(0);
        return result;
    }

    /* loaded from: classes3.dex */
    private class FlacOggSeeker implements OggSeeker {
        private long firstFrameOffset = -1;
        private long pendingSeekGranule = -1;

        public FlacOggSeeker() {
            FlacReader.this = r3;
        }

        public void setFirstFrameOffset(long firstFrameOffset) {
            this.firstFrameOffset = firstFrameOffset;
        }

        @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
        public long read(ExtractorInput input) throws IOException, InterruptedException {
            long j = this.pendingSeekGranule;
            if (j >= 0) {
                long result = -(j + 2);
                this.pendingSeekGranule = -1L;
                return result;
            }
            return -1L;
        }

        @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
        public void startSeek(long targetGranule) {
            Assertions.checkNotNull(FlacReader.this.streamMetadata.seekTable);
            long[] seekPointGranules = FlacReader.this.streamMetadata.seekTable.pointSampleNumbers;
            int index = Util.binarySearchFloor(seekPointGranules, targetGranule, true, true);
            this.pendingSeekGranule = seekPointGranules[index];
        }

        @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
        public SeekMap createSeekMap() {
            Assertions.checkState(this.firstFrameOffset != -1);
            return new FlacSeekTableSeekMap(FlacReader.this.streamMetadata, this.firstFrameOffset);
        }
    }
}
