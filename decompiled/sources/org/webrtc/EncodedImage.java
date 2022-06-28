package org.webrtc;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
/* loaded from: classes5.dex */
public class EncodedImage implements RefCounted {
    public final ByteBuffer buffer;
    public final long captureTimeMs;
    public final long captureTimeNs;
    public final int encodedHeight;
    public final int encodedWidth;
    public final FrameType frameType;
    public final Integer qp;
    private final RefCountDelegate refCountDelegate;
    public final int rotation;

    /* loaded from: classes5.dex */
    public enum FrameType {
        EmptyFrame(0),
        VideoFrameKey(3),
        VideoFrameDelta(4);
        
        private final int nativeIndex;

        FrameType(int nativeIndex) {
            this.nativeIndex = nativeIndex;
        }

        public int getNative() {
            return this.nativeIndex;
        }

        static FrameType fromNativeIndex(int nativeIndex) {
            FrameType[] values;
            for (FrameType type : values()) {
                if (type.getNative() == nativeIndex) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown native frame type: " + nativeIndex);
        }
    }

    @Override // org.webrtc.RefCounted
    public void retain() {
        this.refCountDelegate.retain();
    }

    @Override // org.webrtc.RefCounted
    public void release() {
        this.refCountDelegate.release();
    }

    private EncodedImage(ByteBuffer buffer, Runnable releaseCallback, int encodedWidth, int encodedHeight, long captureTimeNs, FrameType frameType, int rotation, Integer qp) {
        this.buffer = buffer;
        this.encodedWidth = encodedWidth;
        this.encodedHeight = encodedHeight;
        this.captureTimeMs = TimeUnit.NANOSECONDS.toMillis(captureTimeNs);
        this.captureTimeNs = captureTimeNs;
        this.frameType = frameType;
        this.rotation = rotation;
        this.qp = qp;
        this.refCountDelegate = new RefCountDelegate(releaseCallback);
    }

    private ByteBuffer getBuffer() {
        return this.buffer;
    }

    private int getEncodedWidth() {
        return this.encodedWidth;
    }

    private int getEncodedHeight() {
        return this.encodedHeight;
    }

    private long getCaptureTimeNs() {
        return this.captureTimeNs;
    }

    private int getFrameType() {
        return this.frameType.getNative();
    }

    private int getRotation() {
        return this.rotation;
    }

    private Integer getQp() {
        return this.qp;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* loaded from: classes5.dex */
    public static class Builder {
        private ByteBuffer buffer;
        private long captureTimeNs;
        private int encodedHeight;
        private int encodedWidth;
        private FrameType frameType;
        private Integer qp;
        private Runnable releaseCallback;
        private int rotation;

        private Builder() {
        }

        public Builder setBuffer(ByteBuffer buffer, Runnable releaseCallback) {
            this.buffer = buffer;
            this.releaseCallback = releaseCallback;
            return this;
        }

        public Builder setEncodedWidth(int encodedWidth) {
            this.encodedWidth = encodedWidth;
            return this;
        }

        public Builder setEncodedHeight(int encodedHeight) {
            this.encodedHeight = encodedHeight;
            return this;
        }

        @Deprecated
        public Builder setCaptureTimeMs(long captureTimeMs) {
            this.captureTimeNs = TimeUnit.MILLISECONDS.toNanos(captureTimeMs);
            return this;
        }

        public Builder setCaptureTimeNs(long captureTimeNs) {
            this.captureTimeNs = captureTimeNs;
            return this;
        }

        public Builder setFrameType(FrameType frameType) {
            this.frameType = frameType;
            return this;
        }

        public Builder setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setQp(Integer qp) {
            this.qp = qp;
            return this;
        }

        public EncodedImage createEncodedImage() {
            return new EncodedImage(this.buffer, this.releaseCallback, this.encodedWidth, this.encodedHeight, this.captureTimeNs, this.frameType, this.rotation, this.qp);
        }
    }
}
