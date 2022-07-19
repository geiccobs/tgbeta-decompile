package com.google.android.exoplayer2.decoder;

import com.huawei.hms.push.constant.RemoteMessageConst;
import java.nio.ByteBuffer;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
/* loaded from: classes.dex */
public class DecoderInputBuffer extends Buffer {
    private final int bufferReplacementMode;
    public final CryptoInfo cryptoInfo = new CryptoInfo();
    public ByteBuffer data;
    public ByteBuffer supplementalData;
    public long timeUs;
    public boolean waitingForKeys;

    public static DecoderInputBuffer newFlagsOnlyInstance() {
        return new DecoderInputBuffer(0);
    }

    public DecoderInputBuffer(int i) {
        this.bufferReplacementMode = i;
    }

    @EnsuresNonNull({"supplementalData"})
    public void resetSupplementalData(int i) {
        ByteBuffer byteBuffer = this.supplementalData;
        if (byteBuffer == null || byteBuffer.capacity() < i) {
            this.supplementalData = ByteBuffer.allocate(i);
        } else {
            this.supplementalData.clear();
        }
    }

    @EnsuresNonNull({RemoteMessageConst.DATA})
    public void ensureSpaceForWrite(int i) {
        ByteBuffer byteBuffer = this.data;
        if (byteBuffer == null) {
            this.data = createReplacementByteBuffer(i);
            return;
        }
        int capacity = byteBuffer.capacity();
        int position = this.data.position();
        int i2 = i + position;
        if (capacity >= i2) {
            return;
        }
        ByteBuffer createReplacementByteBuffer = createReplacementByteBuffer(i2);
        createReplacementByteBuffer.order(this.data.order());
        if (position > 0) {
            this.data.flip();
            createReplacementByteBuffer.put(this.data);
        }
        this.data = createReplacementByteBuffer;
    }

    public final boolean isFlagsOnly() {
        return this.data == null && this.bufferReplacementMode == 0;
    }

    public final boolean isEncrypted() {
        return getFlag(1073741824);
    }

    public final void flip() {
        this.data.flip();
        ByteBuffer byteBuffer = this.supplementalData;
        if (byteBuffer != null) {
            byteBuffer.flip();
        }
    }

    @Override // com.google.android.exoplayer2.decoder.Buffer
    public void clear() {
        super.clear();
        ByteBuffer byteBuffer = this.data;
        if (byteBuffer != null) {
            byteBuffer.clear();
        }
        ByteBuffer byteBuffer2 = this.supplementalData;
        if (byteBuffer2 != null) {
            byteBuffer2.clear();
        }
        this.waitingForKeys = false;
    }

    private ByteBuffer createReplacementByteBuffer(int i) {
        int i2 = this.bufferReplacementMode;
        if (i2 == 1) {
            return ByteBuffer.allocate(i);
        }
        if (i2 == 2) {
            return ByteBuffer.allocateDirect(i);
        }
        ByteBuffer byteBuffer = this.data;
        int capacity = byteBuffer == null ? 0 : byteBuffer.capacity();
        throw new IllegalStateException("Buffer too small (" + capacity + " < " + i + ")");
    }
}
