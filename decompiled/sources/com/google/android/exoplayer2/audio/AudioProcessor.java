package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes3.dex */
public interface AudioProcessor {
    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder());

    AudioFormat configure(AudioFormat audioFormat) throws UnhandledAudioFormatException;

    void flush();

    ByteBuffer getOutput();

    boolean isActive();

    boolean isEnded();

    void queueEndOfStream();

    void queueInput(ByteBuffer byteBuffer);

    void reset();

    /* loaded from: classes3.dex */
    public static final class AudioFormat {
        public static final AudioFormat NOT_SET = new AudioFormat(-1, -1, -1);
        public final int bytesPerFrame;
        public final int channelCount;
        public final int encoding;
        public final int sampleRate;

        public AudioFormat(int sampleRate, int channelCount, int encoding) {
            int i;
            this.sampleRate = sampleRate;
            this.channelCount = channelCount;
            this.encoding = encoding;
            if (Util.isEncodingLinearPcm(encoding)) {
                i = Util.getPcmFrameSize(encoding, channelCount);
            } else {
                i = -1;
            }
            this.bytesPerFrame = i;
        }

        public String toString() {
            return "AudioFormat[sampleRate=" + this.sampleRate + ", channelCount=" + this.channelCount + ", encoding=" + this.encoding + ']';
        }
    }

    /* loaded from: classes3.dex */
    public static final class UnhandledAudioFormatException extends Exception {
        public UnhandledAudioFormatException(AudioFormat inputAudioFormat) {
            super("Unhandled format: " + inputAudioFormat);
        }
    }
}
