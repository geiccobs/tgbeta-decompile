package com.google.android.exoplayer2.decoder;

import com.google.android.exoplayer2.C;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
/* loaded from: classes3.dex */
public class DecoderInputBuffer extends Buffer {
    public static final int BUFFER_REPLACEMENT_MODE_DIRECT = 2;
    public static final int BUFFER_REPLACEMENT_MODE_DISABLED = 0;
    public static final int BUFFER_REPLACEMENT_MODE_NORMAL = 1;
    private final int bufferReplacementMode;
    public final CryptoInfo cryptoInfo = new CryptoInfo();
    public ByteBuffer data;
    public ByteBuffer supplementalData;
    public long timeUs;
    public boolean waitingForKeys;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BufferReplacementMode {
    }

    public static DecoderInputBuffer newFlagsOnlyInstance() {
        return new DecoderInputBuffer(0);
    }

    public DecoderInputBuffer(int bufferReplacementMode) {
        this.bufferReplacementMode = bufferReplacementMode;
    }

    @EnsuresNonNull({"supplementalData"})
    public void resetSupplementalData(int length) {
        ByteBuffer byteBuffer = this.supplementalData;
        if (byteBuffer == null || byteBuffer.capacity() < length) {
            this.supplementalData = ByteBuffer.allocate(length);
        } else {
            this.supplementalData.clear();
        }
    }

    @EnsuresNonNull({"data"})
    public void ensureSpaceForWrite(int length) {
        ByteBuffer byteBuffer = this.data;
        if (byteBuffer == null) {
            this.data = createReplacementByteBuffer(length);
            return;
        }
        int capacity = byteBuffer.capacity();
        int position = this.data.position();
        int requiredCapacity = position + length;
        if (capacity >= requiredCapacity) {
            return;
        }
        ByteBuffer newData = createReplacementByteBuffer(requiredCapacity);
        newData.order(this.data.order());
        if (position > 0) {
            this.data.flip();
            newData.put(this.data);
        }
        this.data = newData;
    }

    public final boolean isFlagsOnly() {
        return this.data == null && this.bufferReplacementMode == 0;
    }

    public final boolean isEncrypted() {
        return getFlag(C.BUFFER_FLAG_ENCRYPTED);
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

    private ByteBuffer createReplacementByteBuffer(int requiredCapacity) {
        int i = this.bufferReplacementMode;
        if (i == 1) {
            return ByteBuffer.allocate(requiredCapacity);
        }
        if (i == 2) {
            return ByteBuffer.allocateDirect(requiredCapacity);
        }
        ByteBuffer byteBuffer = this.data;
        int currentCapacity = byteBuffer == null ? 0 : byteBuffer.capacity();
        throw new IllegalStateException("Buffer too small (" + currentCapacity + " < " + requiredCapacity + ")");
    }
}
