package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
final class FloatResamplingAudioProcessor extends BaseAudioProcessor {
    private static final int FLOAT_NAN_AS_INT = Float.floatToIntBits(Float.NaN);
    private static final double PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR = 4.656612875245797E-10d;

    @Override // com.google.android.exoplayer2.audio.BaseAudioProcessor
    public AudioProcessor.AudioFormat onConfigure(AudioProcessor.AudioFormat inputAudioFormat) throws AudioProcessor.UnhandledAudioFormatException {
        int encoding = inputAudioFormat.encoding;
        if (!Util.isEncodingHighResolutionPcm(encoding)) {
            throw new AudioProcessor.UnhandledAudioFormatException(inputAudioFormat);
        }
        if (encoding != 4) {
            return new AudioProcessor.AudioFormat(inputAudioFormat.sampleRate, inputAudioFormat.channelCount, 4);
        }
        return AudioProcessor.AudioFormat.NOT_SET;
    }

    @Override // com.google.android.exoplayer2.audio.AudioProcessor
    public void queueInput(ByteBuffer inputBuffer) {
        ByteBuffer buffer;
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int size = limit - position;
        switch (this.inputAudioFormat.encoding) {
            case 536870912:
                buffer = replaceOutputBuffer((size / 3) * 4);
                for (int i = position; i < limit; i += 3) {
                    int pcm32BitInteger = ((inputBuffer.get(i) & 255) << 8) | ((inputBuffer.get(i + 1) & 255) << 16) | ((inputBuffer.get(i + 2) & 255) << 24);
                    writePcm32BitFloat(pcm32BitInteger, buffer);
                }
                break;
            case C.ENCODING_PCM_32BIT /* 805306368 */:
                buffer = replaceOutputBuffer(size);
                for (int i2 = position; i2 < limit; i2 += 4) {
                    int pcm32BitInteger2 = (inputBuffer.get(i2) & 255) | ((inputBuffer.get(i2 + 1) & 255) << 8) | ((inputBuffer.get(i2 + 2) & 255) << 16) | ((inputBuffer.get(i2 + 3) & 255) << 24);
                    writePcm32BitFloat(pcm32BitInteger2, buffer);
                }
                break;
            default:
                throw new IllegalStateException();
        }
        inputBuffer.position(inputBuffer.limit());
        buffer.flip();
    }

    private static void writePcm32BitFloat(int pcm32BitInt, ByteBuffer buffer) {
        double d = pcm32BitInt;
        Double.isNaN(d);
        float pcm32BitFloat = (float) (d * PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR);
        int floatBits = Float.floatToIntBits(pcm32BitFloat);
        if (floatBits == FLOAT_NAN_AS_INT) {
            floatBits = Float.floatToIntBits(0.0f);
        }
        buffer.putInt(floatBits);
    }
}
