package org.telegram.messenger.video;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;
/* loaded from: classes4.dex */
public class AudioRecoder {
    private final MediaCodec decoder;
    private ByteBuffer[] decoderInputBuffers;
    private ByteBuffer[] decoderOutputBuffers;
    private final MediaCodec encoder;
    private ByteBuffer[] encoderInputBuffers;
    private ByteBuffer[] encoderOutputBuffers;
    private final MediaExtractor extractor;
    public final MediaFormat format;
    private final int trackIndex;
    private final MediaCodec.BufferInfo decoderOutputBufferInfo = new MediaCodec.BufferInfo();
    private final MediaCodec.BufferInfo encoderOutputBufferInfo = new MediaCodec.BufferInfo();
    private boolean extractorDone = false;
    private boolean decoderDone = false;
    private boolean encoderDone = false;
    private int pendingAudioDecoderOutputBufferIndex = -1;
    private final int TIMEOUT_USEC = 2500;
    public long startTime = 0;
    public long endTime = 0;

    public AudioRecoder(MediaFormat inputAudioFormat, MediaExtractor extractor, int trackIndex) throws IOException {
        this.extractor = extractor;
        this.trackIndex = trackIndex;
        MediaCodec createDecoderByType = MediaCodec.createDecoderByType(inputAudioFormat.getString("mime"));
        this.decoder = createDecoderByType;
        createDecoderByType.configure(inputAudioFormat, (Surface) null, (MediaCrypto) null, 0);
        createDecoderByType.start();
        MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
        this.encoder = createEncoderByType;
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", inputAudioFormat.getInteger("sample-rate"), inputAudioFormat.getInteger("channel-count"));
        this.format = createAudioFormat;
        createAudioFormat.setInteger("bitrate", 65536);
        createEncoderByType.configure(createAudioFormat, (Surface) null, (MediaCrypto) null, 1);
        createEncoderByType.start();
        this.decoderInputBuffers = createDecoderByType.getInputBuffers();
        this.decoderOutputBuffers = createDecoderByType.getOutputBuffers();
        this.encoderInputBuffers = createEncoderByType.getInputBuffers();
        this.encoderOutputBuffers = createEncoderByType.getOutputBuffers();
    }

    public void release() {
        try {
            this.encoder.stop();
            this.decoder.stop();
            this.extractor.unselectTrack(this.trackIndex);
            this.extractor.release();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean step(MP4Builder muxer, int audioTrackIndex) throws Exception {
        int encoderOutputBufferIndex;
        int encoderInputBufferIndex;
        int decoderOutputBufferIndex;
        int decoderInputBufferIndex;
        ByteBuffer decoderInputBuffer;
        if (!this.extractorDone && (decoderInputBufferIndex = this.decoder.dequeueInputBuffer(2500L)) != -1) {
            if (Build.VERSION.SDK_INT >= 21) {
                decoderInputBuffer = this.decoder.getInputBuffer(decoderInputBufferIndex);
            } else {
                decoderInputBuffer = this.decoderInputBuffers[decoderInputBufferIndex];
            }
            int size = this.extractor.readSampleData(decoderInputBuffer, 0);
            long presentationTime = this.extractor.getSampleTime();
            long j = this.endTime;
            if (j > 0 && presentationTime >= j) {
                this.encoderDone = true;
                this.decoderOutputBufferInfo.flags |= 4;
            }
            if (size >= 0) {
                this.decoder.queueInputBuffer(decoderInputBufferIndex, 0, size, this.extractor.getSampleTime(), this.extractor.getSampleFlags());
            }
            boolean z = !this.extractor.advance();
            this.extractorDone = z;
            if (z) {
                this.decoder.queueInputBuffer(this.decoder.dequeueInputBuffer(2500L), 0, 0, 0L, 4);
            }
        }
        if (!this.decoderDone && this.pendingAudioDecoderOutputBufferIndex == -1 && (decoderOutputBufferIndex = this.decoder.dequeueOutputBuffer(this.decoderOutputBufferInfo, 2500L)) != -1) {
            if (decoderOutputBufferIndex == -3) {
                this.decoderOutputBuffers = this.decoder.getOutputBuffers();
            } else if (decoderOutputBufferIndex != -2) {
                if ((this.decoderOutputBufferInfo.flags & 2) != 0) {
                    this.decoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
                } else {
                    this.pendingAudioDecoderOutputBufferIndex = decoderOutputBufferIndex;
                }
            }
        }
        if (this.pendingAudioDecoderOutputBufferIndex != -1 && (encoderInputBufferIndex = this.encoder.dequeueInputBuffer(2500L)) != -1) {
            ByteBuffer encoderInputBuffer = this.encoderInputBuffers[encoderInputBufferIndex];
            int size2 = this.decoderOutputBufferInfo.size;
            long presentationTime2 = this.decoderOutputBufferInfo.presentationTimeUs;
            if (size2 >= 0) {
                ByteBuffer decoderOutputBuffer = this.decoderOutputBuffers[this.pendingAudioDecoderOutputBufferIndex].duplicate();
                decoderOutputBuffer.position(this.decoderOutputBufferInfo.offset);
                decoderOutputBuffer.limit(this.decoderOutputBufferInfo.offset + size2);
                encoderInputBuffer.position(0);
                encoderInputBuffer.put(decoderOutputBuffer);
                this.encoder.queueInputBuffer(encoderInputBufferIndex, 0, size2, presentationTime2, this.decoderOutputBufferInfo.flags);
            }
            this.decoder.releaseOutputBuffer(this.pendingAudioDecoderOutputBufferIndex, false);
            this.pendingAudioDecoderOutputBufferIndex = -1;
            if ((this.decoderOutputBufferInfo.flags & 4) != 0) {
                this.decoderDone = true;
            }
        }
        if (!this.encoderDone && (encoderOutputBufferIndex = this.encoder.dequeueOutputBuffer(this.encoderOutputBufferInfo, 2500L)) != -1) {
            if (encoderOutputBufferIndex == -3) {
                this.encoderOutputBuffers = this.encoder.getOutputBuffers();
            } else if (encoderOutputBufferIndex != -2) {
                ByteBuffer encoderOutputBuffer = this.encoderOutputBuffers[encoderOutputBufferIndex];
                if ((this.encoderOutputBufferInfo.flags & 2) != 0) {
                    this.encoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                } else {
                    if (this.encoderOutputBufferInfo.size != 0) {
                        muxer.writeSampleData(audioTrackIndex, encoderOutputBuffer, this.encoderOutputBufferInfo, false);
                    }
                    if ((this.encoderOutputBufferInfo.flags & 4) != 0) {
                        this.encoderDone = true;
                    }
                    this.encoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                }
            }
        }
        return this.encoderDone;
    }
}
