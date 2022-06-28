package com.google.android.exoplayer2.text.cea;

import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.text.SubtitleOutputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
/* loaded from: classes3.dex */
public abstract class CeaDecoder implements SubtitleDecoder {
    private static final int NUM_INPUT_BUFFERS = 10;
    private static final int NUM_OUTPUT_BUFFERS = 2;
    private final ArrayDeque<CeaInputBuffer> availableInputBuffers = new ArrayDeque<>();
    private final ArrayDeque<SubtitleOutputBuffer> availableOutputBuffers;
    private CeaInputBuffer dequeuedInputBuffer;
    private long playbackPositionUs;
    private long queuedInputBufferCount;
    private final PriorityQueue<CeaInputBuffer> queuedInputBuffers;

    protected abstract Subtitle createSubtitle();

    protected abstract void decode(SubtitleInputBuffer subtitleInputBuffer);

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public abstract String getName();

    protected abstract boolean isNewSubtitleDataAvailable();

    public CeaDecoder() {
        for (int i = 0; i < 10; i++) {
            this.availableInputBuffers.add(new CeaInputBuffer());
        }
        this.availableOutputBuffers = new ArrayDeque<>();
        for (int i2 = 0; i2 < 2; i2++) {
            this.availableOutputBuffers.add(new CeaOutputBuffer());
        }
        this.queuedInputBuffers = new PriorityQueue<>();
    }

    @Override // com.google.android.exoplayer2.text.SubtitleDecoder
    public void setPositionUs(long positionUs) {
        this.playbackPositionUs = positionUs;
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        Assertions.checkState(this.dequeuedInputBuffer == null);
        if (this.availableInputBuffers.isEmpty()) {
            return null;
        }
        CeaInputBuffer pollFirst = this.availableInputBuffers.pollFirst();
        this.dequeuedInputBuffer = pollFirst;
        return pollFirst;
    }

    public void queueInputBuffer(SubtitleInputBuffer inputBuffer) throws SubtitleDecoderException {
        Assertions.checkArgument(inputBuffer == this.dequeuedInputBuffer);
        if (inputBuffer.isDecodeOnly()) {
            releaseInputBuffer(this.dequeuedInputBuffer);
        } else {
            CeaInputBuffer ceaInputBuffer = this.dequeuedInputBuffer;
            long j = this.queuedInputBufferCount;
            this.queuedInputBufferCount = 1 + j;
            ceaInputBuffer.queuedInputBufferCount = j;
            this.queuedInputBuffers.add(this.dequeuedInputBuffer);
        }
        this.dequeuedInputBuffer = null;
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        if (this.availableOutputBuffers.isEmpty()) {
            return null;
        }
        while (!this.queuedInputBuffers.isEmpty() && this.queuedInputBuffers.peek().timeUs <= this.playbackPositionUs) {
            CeaInputBuffer inputBuffer = this.queuedInputBuffers.poll();
            if (inputBuffer.isEndOfStream()) {
                SubtitleOutputBuffer outputBuffer = this.availableOutputBuffers.pollFirst();
                outputBuffer.addFlag(4);
                releaseInputBuffer(inputBuffer);
                return outputBuffer;
            }
            decode(inputBuffer);
            if (isNewSubtitleDataAvailable()) {
                Subtitle subtitle = createSubtitle();
                if (!inputBuffer.isDecodeOnly()) {
                    SubtitleOutputBuffer outputBuffer2 = this.availableOutputBuffers.pollFirst();
                    outputBuffer2.setContent(inputBuffer.timeUs, subtitle, Long.MAX_VALUE);
                    releaseInputBuffer(inputBuffer);
                    return outputBuffer2;
                }
            }
            releaseInputBuffer(inputBuffer);
        }
        return null;
    }

    private void releaseInputBuffer(CeaInputBuffer inputBuffer) {
        inputBuffer.clear();
        this.availableInputBuffers.add(inputBuffer);
    }

    protected void releaseOutputBuffer(SubtitleOutputBuffer outputBuffer) {
        outputBuffer.clear();
        this.availableOutputBuffers.add(outputBuffer);
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public void flush() {
        this.queuedInputBufferCount = 0L;
        this.playbackPositionUs = 0L;
        while (!this.queuedInputBuffers.isEmpty()) {
            releaseInputBuffer(this.queuedInputBuffers.poll());
        }
        CeaInputBuffer ceaInputBuffer = this.dequeuedInputBuffer;
        if (ceaInputBuffer != null) {
            releaseInputBuffer(ceaInputBuffer);
            this.dequeuedInputBuffer = null;
        }
    }

    @Override // com.google.android.exoplayer2.decoder.Decoder
    public void release() {
    }

    /* loaded from: classes3.dex */
    public static final class CeaInputBuffer extends SubtitleInputBuffer implements Comparable<CeaInputBuffer> {
        private long queuedInputBufferCount;

        private CeaInputBuffer() {
        }

        public int compareTo(CeaInputBuffer other) {
            if (isEndOfStream() != other.isEndOfStream()) {
                return isEndOfStream() ? 1 : -1;
            }
            long delta = this.timeUs - other.timeUs;
            if (delta == 0) {
                delta = this.queuedInputBufferCount - other.queuedInputBufferCount;
                if (delta == 0) {
                    return 0;
                }
            }
            return delta > 0 ? 1 : -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class CeaOutputBuffer extends SubtitleOutputBuffer {
        private CeaOutputBuffer() {
            CeaDecoder.this = r1;
        }

        @Override // com.google.android.exoplayer2.text.SubtitleOutputBuffer, com.google.android.exoplayer2.decoder.OutputBuffer
        public final void release() {
            CeaDecoder.this.releaseOutputBuffer(this);
        }
    }
}
