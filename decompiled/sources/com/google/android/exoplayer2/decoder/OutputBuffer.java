package com.google.android.exoplayer2.decoder;
/* loaded from: classes3.dex */
public abstract class OutputBuffer extends Buffer {
    public int skippedOutputBufferCount;
    public long timeUs;

    public abstract void release();
}
