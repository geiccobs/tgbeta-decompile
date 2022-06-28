package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes3.dex */
public final class SilenceSkippingAudioProcessor extends BaseAudioProcessor {
    public static final long DEFAULT_MINIMUM_SILENCE_DURATION_US = 150000;
    public static final long DEFAULT_PADDING_SILENCE_US = 20000;
    public static final short DEFAULT_SILENCE_THRESHOLD_LEVEL = 1024;
    private static final int STATE_MAYBE_SILENT = 1;
    private static final int STATE_NOISY = 0;
    private static final int STATE_SILENT = 2;
    private int bytesPerFrame;
    private boolean enabled;
    private boolean hasOutputNoise;
    private byte[] maybeSilenceBuffer;
    private int maybeSilenceBufferSize;
    private final long minimumSilenceDurationUs;
    private byte[] paddingBuffer;
    private final long paddingSilenceUs;
    private int paddingSize;
    private final short silenceThresholdLevel;
    private long skippedFrames;
    private int state;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface State {
    }

    public SilenceSkippingAudioProcessor() {
        this(DEFAULT_MINIMUM_SILENCE_DURATION_US, DEFAULT_PADDING_SILENCE_US, DEFAULT_SILENCE_THRESHOLD_LEVEL);
    }

    public SilenceSkippingAudioProcessor(long minimumSilenceDurationUs, long paddingSilenceUs, short silenceThresholdLevel) {
        Assertions.checkArgument(paddingSilenceUs <= minimumSilenceDurationUs);
        this.minimumSilenceDurationUs = minimumSilenceDurationUs;
        this.paddingSilenceUs = paddingSilenceUs;
        this.silenceThresholdLevel = silenceThresholdLevel;
        this.maybeSilenceBuffer = Util.EMPTY_BYTE_ARRAY;
        this.paddingBuffer = Util.EMPTY_BYTE_ARRAY;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getSkippedFrames() {
        return this.skippedFrames;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    public AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat inputAudioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        if (inputAudioFormat.encoding == 2) {
            return this.enabled ? inputAudioFormat : AudioProcessor.AudioFormat.NOT_SET;
        }
        throw new AudioProcessor.UnhandledAudioFormatException(inputAudioFormat);
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor, com.google.android.exoplayer2.audio.AudioProcessor
    public boolean isActive() {
        return this.enabled;
    }

    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    public void queueInput(ByteBuffer inputBuffer) {
        while (inputBuffer.hasRemaining() && !hasPendingOutput()) {
            switch (this.state) {
                case 0:
                    processNoisy(inputBuffer);
                    break;
                case 1:
                    processMaybeSilence(inputBuffer);
                    break;
                case 2:
                    processSilence(inputBuffer);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onQueueEndOfStream() {
        int i = this.maybeSilenceBufferSize;
        if (i > 0) {
            output(this.maybeSilenceBuffer, i);
        }
        if (!this.hasOutputNoise) {
            this.skippedFrames += this.paddingSize / this.bytesPerFrame;
        }
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onFlush() {
        if (this.enabled) {
            this.bytesPerFrame = this.inputAudioFormat.bytesPerFrame;
            int maybeSilenceBufferSize = durationUsToFrames(this.minimumSilenceDurationUs) * this.bytesPerFrame;
            if (this.maybeSilenceBuffer.length != maybeSilenceBufferSize) {
                this.maybeSilenceBuffer = new byte[maybeSilenceBufferSize];
            }
            int durationUsToFrames = durationUsToFrames(this.paddingSilenceUs) * this.bytesPerFrame;
            this.paddingSize = durationUsToFrames;
            if (this.paddingBuffer.length != durationUsToFrames) {
                this.paddingBuffer = new byte[durationUsToFrames];
            }
        }
        this.state = 0;
        this.skippedFrames = 0L;
        this.maybeSilenceBufferSize = 0;
        this.hasOutputNoise = false;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onReset() {
        this.enabled = false;
        this.paddingSize = 0;
        this.maybeSilenceBuffer = Util.EMPTY_BYTE_ARRAY;
        this.paddingBuffer = Util.EMPTY_BYTE_ARRAY;
    }

    private void processNoisy(ByteBuffer inputBuffer) {
        int limit = inputBuffer.limit();
        inputBuffer.limit(Math.min(limit, inputBuffer.position() + this.maybeSilenceBuffer.length));
        int noiseLimit = findNoiseLimit(inputBuffer);
        if (noiseLimit == inputBuffer.position()) {
            this.state = 1;
        } else {
            inputBuffer.limit(noiseLimit);
            output(inputBuffer);
        }
        inputBuffer.limit(limit);
    }

    private void processMaybeSilence(ByteBuffer inputBuffer) {
        int limit = inputBuffer.limit();
        int noisePosition = findNoisePosition(inputBuffer);
        int maybeSilenceInputSize = noisePosition - inputBuffer.position();
        byte[] bArr = this.maybeSilenceBuffer;
        int length = bArr.length;
        int i = this.maybeSilenceBufferSize;
        int maybeSilenceBufferRemaining = length - i;
        if (noisePosition < limit && maybeSilenceInputSize < maybeSilenceBufferRemaining) {
            output(bArr, i);
            this.maybeSilenceBufferSize = 0;
            this.state = 0;
            return;
        }
        int bytesToWrite = Math.min(maybeSilenceInputSize, maybeSilenceBufferRemaining);
        inputBuffer.limit(inputBuffer.position() + bytesToWrite);
        inputBuffer.get(this.maybeSilenceBuffer, this.maybeSilenceBufferSize, bytesToWrite);
        int i2 = this.maybeSilenceBufferSize + bytesToWrite;
        this.maybeSilenceBufferSize = i2;
        byte[] bArr2 = this.maybeSilenceBuffer;
        if (i2 == bArr2.length) {
            if (this.hasOutputNoise) {
                output(bArr2, this.paddingSize);
                this.skippedFrames += (this.maybeSilenceBufferSize - (this.paddingSize * 2)) / this.bytesPerFrame;
            } else {
                this.skippedFrames += (i2 - this.paddingSize) / this.bytesPerFrame;
            }
            updatePaddingBuffer(inputBuffer, this.maybeSilenceBuffer, this.maybeSilenceBufferSize);
            this.maybeSilenceBufferSize = 0;
            this.state = 2;
        }
        inputBuffer.limit(limit);
    }

    private void processSilence(ByteBuffer inputBuffer) {
        int limit = inputBuffer.limit();
        int noisyPosition = findNoisePosition(inputBuffer);
        inputBuffer.limit(noisyPosition);
        this.skippedFrames += inputBuffer.remaining() / this.bytesPerFrame;
        updatePaddingBuffer(inputBuffer, this.paddingBuffer, this.paddingSize);
        if (noisyPosition < limit) {
            output(this.paddingBuffer, this.paddingSize);
            this.state = 0;
            inputBuffer.limit(limit);
        }
    }

    private void output(byte[] data, int length) {
        replaceOutputBuffer(length).put(data, 0, length).flip();
        if (length > 0) {
            this.hasOutputNoise = true;
        }
    }

    private void output(ByteBuffer data) {
        int length = data.remaining();
        replaceOutputBuffer(length).put(data).flip();
        if (length > 0) {
            this.hasOutputNoise = true;
        }
    }

    private void updatePaddingBuffer(ByteBuffer input, byte[] buffer, int size) {
        int fromInputSize = Math.min(input.remaining(), this.paddingSize);
        int fromBufferSize = this.paddingSize - fromInputSize;
        System.arraycopy(buffer, size - fromBufferSize, this.paddingBuffer, 0, fromBufferSize);
        input.position(input.limit() - fromInputSize);
        input.get(this.paddingBuffer, fromBufferSize, fromInputSize);
    }

    private int durationUsToFrames(long durationUs) {
        return (int) ((this.inputAudioFormat.sampleRate * durationUs) / 1000000);
    }

    private int findNoisePosition(ByteBuffer buffer) {
        Assertions.checkArgument(buffer.order() == ByteOrder.LITTLE_ENDIAN);
        for (int i = buffer.position(); i < buffer.limit(); i += 2) {
            if (Math.abs((int) buffer.getShort(i)) > this.silenceThresholdLevel) {
                int i2 = this.bytesPerFrame;
                return i2 * (i / i2);
            }
        }
        int i3 = buffer.limit();
        return i3;
    }

    private int findNoiseLimit(ByteBuffer buffer) {
        Assertions.checkArgument(buffer.order() == ByteOrder.LITTLE_ENDIAN);
        int i = buffer.limit();
        while (true) {
            i -= 2;
            if (i >= buffer.position()) {
                if (Math.abs((int) buffer.getShort(i)) > this.silenceThresholdLevel) {
                    int i2 = this.bytesPerFrame;
                    return ((i / i2) * i2) + i2;
                }
            } else {
                int i3 = buffer.position();
                return i3;
            }
        }
    }
}
