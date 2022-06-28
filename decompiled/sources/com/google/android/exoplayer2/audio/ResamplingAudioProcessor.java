package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.audio.AudioProcessor;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
final class ResamplingAudioProcessor extends BaseAudioProcessor {
    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    public AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat inputAudioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        int encoding = inputAudioFormat.encoding;
        if (encoding != 3 && encoding != 2 && encoding != 268435456 && encoding != 536870912 && encoding != 805306368 && encoding != 4) {
            throw new AudioProcessor.UnhandledAudioFormatException(inputAudioFormat);
        }
        if (encoding != 2) {
            return new AudioProcessor.AudioFormat(inputAudioFormat.sampleRate, inputAudioFormat.channelCount, 2);
        }
        return AudioProcessor.AudioFormat.NOT_SET;
    }

    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    public void queueInput(ByteBuffer inputBuffer) {
        int resampledSize;
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int size = limit - position;
        switch (this.inputAudioFormat.encoding) {
            case 3:
                resampledSize = size * 2;
                break;
            case 4:
            case C.ENCODING_PCM_32BIT /* 805306368 */:
                resampledSize = size / 2;
                break;
            case 268435456:
                resampledSize = size;
                break;
            case 536870912:
                resampledSize = (size / 3) * 2;
                break;
            default:
                throw new IllegalStateException();
        }
        ByteBuffer buffer = replaceOutputBuffer(resampledSize);
        switch (this.inputAudioFormat.encoding) {
            case 3:
                for (int i = position; i < limit; i++) {
                    buffer.put((byte) 0);
                    buffer.put((byte) ((inputBuffer.get(i) & 255) - 128));
                }
                break;
            case 4:
                for (int i2 = position; i2 < limit; i2 += 4) {
                    short value = (short) (inputBuffer.getFloat(i2) * 32767.0f);
                    buffer.put((byte) (value & 255));
                    buffer.put((byte) ((value >> 8) & 255));
                }
                break;
            case 268435456:
                for (int i3 = position; i3 < limit; i3 += 2) {
                    buffer.put(inputBuffer.get(i3 + 1));
                    buffer.put(inputBuffer.get(i3));
                }
                break;
            case 536870912:
                for (int i4 = position; i4 < limit; i4 += 3) {
                    buffer.put(inputBuffer.get(i4 + 1));
                    buffer.put(inputBuffer.get(i4 + 2));
                }
                break;
            case C.ENCODING_PCM_32BIT /* 805306368 */:
                for (int i5 = position; i5 < limit; i5 += 4) {
                    buffer.put(inputBuffer.get(i5 + 2));
                    buffer.put(inputBuffer.get(i5 + 3));
                }
                break;
            default:
                throw new IllegalStateException();
        }
        inputBuffer.position(inputBuffer.limit());
        buffer.flip();
    }
}
