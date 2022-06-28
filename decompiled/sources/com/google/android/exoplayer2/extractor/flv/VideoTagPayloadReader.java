package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.flv.TagPayloadReader;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.video.AvcConfig;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class VideoTagPayloadReader extends TagPayloadReader {
    private static final int AVC_PACKET_TYPE_AVC_NALU = 1;
    private static final int AVC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int VIDEO_CODEC_AVC = 7;
    private static final int VIDEO_FRAME_KEYFRAME = 1;
    private static final int VIDEO_FRAME_VIDEO_INFO = 5;
    private int frameType;
    private boolean hasOutputFormat;
    private boolean hasOutputKeyframe;
    private int nalUnitLengthFieldLength;
    private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    private final ParsableByteArray nalLength = new ParsableByteArray(4);

    public VideoTagPayloadReader(TrackOutput output) {
        super(output);
    }

    @Override // com.google.android.exoplayer2.extractor.flv.TagPayloadReader
    public void seek() {
        this.hasOutputKeyframe = false;
    }

    @Override // com.google.android.exoplayer2.extractor.flv.TagPayloadReader
    protected boolean parseHeader(ParsableByteArray data) throws TagPayloadReader.UnsupportedFormatException {
        int header = data.readUnsignedByte();
        int frameType = (header >> 4) & 15;
        int videoCodec = header & 15;
        if (videoCodec != 7) {
            throw new TagPayloadReader.UnsupportedFormatException("Video format not supported: " + videoCodec);
        }
        this.frameType = frameType;
        return frameType != 5;
    }

    @Override // com.google.android.exoplayer2.extractor.flv.TagPayloadReader
    protected boolean parsePayload(ParsableByteArray data, long timeUs) throws ParserException {
        int packetType = data.readUnsignedByte();
        int compositionTimeMs = data.readInt24();
        long timeUs2 = timeUs + (compositionTimeMs * 1000);
        if (packetType == 0 && !this.hasOutputFormat) {
            ParsableByteArray videoSequence = new ParsableByteArray(new byte[data.bytesLeft()]);
            data.readBytes(videoSequence.data, 0, data.bytesLeft());
            AvcConfig avcConfig = AvcConfig.parse(videoSequence);
            this.nalUnitLengthFieldLength = avcConfig.nalUnitLengthFieldLength;
            Format format = Format.createVideoSampleFormat(null, "video/avc", null, -1, -1, avcConfig.width, avcConfig.height, -1.0f, avcConfig.initializationData, -1, avcConfig.pixelWidthAspectRatio, null);
            this.output.format(format);
            this.hasOutputFormat = true;
            return false;
        } else if (packetType != 1 || !this.hasOutputFormat) {
            return false;
        } else {
            boolean isKeyframe = this.frameType == 1;
            if (!this.hasOutputKeyframe && !isKeyframe) {
                return false;
            }
            byte[] nalLengthData = this.nalLength.data;
            nalLengthData[0] = 0;
            nalLengthData[1] = 0;
            nalLengthData[2] = 0;
            int nalUnitLengthFieldLengthDiff = 4 - this.nalUnitLengthFieldLength;
            int bytesWritten = 0;
            while (data.bytesLeft() > 0) {
                data.readBytes(this.nalLength.data, nalUnitLengthFieldLengthDiff, this.nalUnitLengthFieldLength);
                this.nalLength.setPosition(0);
                int bytesToWrite = this.nalLength.readUnsignedIntToInt();
                this.nalStartCode.setPosition(0);
                this.output.sampleData(this.nalStartCode, 4);
                this.output.sampleData(data, bytesToWrite);
                bytesWritten = bytesWritten + 4 + bytesToWrite;
            }
            this.output.sampleMetadata(timeUs2, isKeyframe ? 1 : 0, bytesWritten, 0, null);
            this.hasOutputKeyframe = true;
            return true;
        }
    }
}
