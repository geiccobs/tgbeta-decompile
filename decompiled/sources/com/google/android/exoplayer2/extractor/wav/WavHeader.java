package com.google.android.exoplayer2.extractor.wav;
/* loaded from: classes3.dex */
final class WavHeader {
    public final int averageBytesPerSecond;
    public final int bitsPerSample;
    public final int blockSize;
    public final byte[] extraData;
    public final int formatType;
    public final int frameRateHz;
    public final int numChannels;

    public WavHeader(int formatType, int numChannels, int frameRateHz, int averageBytesPerSecond, int blockSize, int bitsPerSample, byte[] extraData) {
        this.formatType = formatType;
        this.numChannels = numChannels;
        this.frameRateHz = frameRateHz;
        this.averageBytesPerSecond = averageBytesPerSecond;
        this.blockSize = blockSize;
        this.bitsPerSample = bitsPerSample;
        this.extraData = extraData;
    }
}
