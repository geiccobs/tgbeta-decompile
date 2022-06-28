package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Assertions;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
final class ChannelMappingAudioProcessor extends BaseAudioProcessor {
    private int[] outputChannels;
    private int[] pendingOutputChannels;

    public void setChannelMap(int[] outputChannels) {
        this.pendingOutputChannels = outputChannels;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    public AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat inputAudioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        int[] outputChannels = this.pendingOutputChannels;
        if (outputChannels == null) {
            return AudioProcessor.AudioFormat.NOT_SET;
        }
        if (inputAudioFormat.encoding != 2) {
            throw new AudioProcessor.UnhandledAudioFormatException(inputAudioFormat);
        }
        boolean active = inputAudioFormat.channelCount != outputChannels.length;
        int i = 0;
        while (i < outputChannels.length) {
            int channelIndex = outputChannels[i];
            if (channelIndex >= inputAudioFormat.channelCount) {
                throw new AudioProcessor.UnhandledAudioFormatException(inputAudioFormat);
            }
            active |= channelIndex != i;
            i++;
        }
        if (active) {
            return new AudioProcessor.AudioFormat(inputAudioFormat.sampleRate, outputChannels.length, 2);
        }
        return AudioProcessor.AudioFormat.NOT_SET;
    }

    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    public void queueInput(ByteBuffer inputBuffer) {
        int[] outputChannels = (int[]) Assertions.checkNotNull(this.outputChannels);
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int frameCount = (limit - position) / this.inputAudioFormat.bytesPerFrame;
        int outputSize = this.outputAudioFormat.bytesPerFrame * frameCount;
        ByteBuffer buffer = replaceOutputBuffer(outputSize);
        while (position < limit) {
            for (int channelIndex : outputChannels) {
                buffer.putShort(inputBuffer.getShort((channelIndex * 2) + position));
            }
            position += this.inputAudioFormat.bytesPerFrame;
        }
        inputBuffer.position(limit);
        buffer.flip();
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onFlush() {
        this.outputChannels = this.pendingOutputChannels;
    }

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    protected void onReset() {
        this.outputChannels = null;
        this.pendingOutputChannels = null;
    }
}
