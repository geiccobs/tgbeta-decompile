package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
final class TrimmingAudioProcessor extends BaseAudioProcessor {
    private static final int OUTPUT_ENCODING = 2;
    private byte[] endBuffer = Util.EMPTY_BYTE_ARRAY;
    private int endBufferSize;
    private int pendingTrimStartBytes;
    private boolean reconfigurationPending;
    private int trimEndFrames;
    private int trimStartFrames;
    private long trimmedFrameCount;

    public void setTrimFrameCount(int trimStartFrames, int trimEndFrames) {
        this.trimStartFrames = trimStartFrames;
        this.trimEndFrames = trimEndFrames;
    }

    public void resetTrimmedFrameCount() {
        this.trimmedFrameCount = 0L;
    }

    public long getTrimmedFrameCount() {
        return this.trimmedFrameCount;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    public AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat inputAudioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        if (inputAudioFormat.encoding != 2) {
            throw new AudioProcessor.UnhandledAudioFormatException(inputAudioFormat);
        }
        this.reconfigurationPending = true;
        return (this.trimStartFrames == 0 && this.trimEndFrames == 0) ? AudioProcessor.AudioFormat.NOT_SET : inputAudioFormat;
    }

    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    public void queueInput(ByteBuffer inputBuffer) {
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int remaining = limit - position;
        if (remaining == 0) {
            return;
        }
        int trimBytes = Math.min(remaining, this.pendingTrimStartBytes);
        this.trimmedFrameCount += trimBytes / this.inputAudioFormat.bytesPerFrame;
        this.pendingTrimStartBytes -= trimBytes;
        inputBuffer.position(position + trimBytes);
        if (this.pendingTrimStartBytes > 0) {
            return;
        }
        int remaining2 = remaining - trimBytes;
        int remainingBytesToOutput = (this.endBufferSize + remaining2) - this.endBuffer.length;
        ByteBuffer buffer = replaceOutputBuffer(remainingBytesToOutput);
        int endBufferBytesToOutput = Util.constrainValue(remainingBytesToOutput, 0, this.endBufferSize);
        buffer.put(this.endBuffer, 0, endBufferBytesToOutput);
        int inputBufferBytesToOutput = Util.constrainValue(remainingBytesToOutput - endBufferBytesToOutput, 0, remaining2);
        inputBuffer.limit(inputBuffer.position() + inputBufferBytesToOutput);
        buffer.put(inputBuffer);
        inputBuffer.limit(limit);
        int remaining3 = remaining2 - inputBufferBytesToOutput;
        int i = this.endBufferSize - endBufferBytesToOutput;
        this.endBufferSize = i;
        byte[] bArr = this.endBuffer;
        System.arraycopy(bArr, endBufferBytesToOutput, bArr, 0, i);
        inputBuffer.get(this.endBuffer, this.endBufferSize, remaining3);
        this.endBufferSize += remaining3;
        buffer.flip();
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor, com.google.android.exoplayer2.audio.AudioProcessor
    public ByteBuffer getOutput() {
        int i;
        if (super.isEnded() && (i = this.endBufferSize) > 0) {
            replaceOutputBuffer(i).put(this.endBuffer, 0, this.endBufferSize).flip();
            this.endBufferSize = 0;
        }
        return super.getOutput();
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor, com.google.android.exoplayer2.audio.AudioProcessor
    public boolean isEnded() {
        return super.isEnded() && this.endBufferSize == 0;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onQueueEndOfStream() {
        int i;
        if (this.reconfigurationPending) {
            if (this.endBufferSize > 0) {
                this.trimmedFrameCount += i / this.inputAudioFormat.bytesPerFrame;
            }
            this.endBufferSize = 0;
        }
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onFlush() {
        if (this.reconfigurationPending) {
            this.reconfigurationPending = false;
            this.endBuffer = new byte[this.trimEndFrames * this.inputAudioFormat.bytesPerFrame];
            this.pendingTrimStartBytes = this.trimStartFrames * this.inputAudioFormat.bytesPerFrame;
        }
        this.endBufferSize = 0;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onReset() {
        this.endBuffer = Util.EMPTY_BYTE_ARRAY;
    }
}
