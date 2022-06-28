package org.webrtc;

import android.graphics.Matrix;
import java.nio.ByteBuffer;
/* loaded from: classes5.dex */
public class VideoFrame implements RefCounted {
    private final Buffer buffer;
    private final int rotation;
    private final long timestampNs;

    /* loaded from: classes5.dex */
    public interface Buffer extends RefCounted {
        Buffer cropAndScale(int i, int i2, int i3, int i4, int i5, int i6);

        int getBufferType();

        int getHeight();

        int getWidth();

        @Override // org.webrtc.RefCounted
        void release();

        @Override // org.webrtc.RefCounted
        void retain();

        I420Buffer toI420();

        /* renamed from: org.webrtc.VideoFrame$Buffer$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static int $default$getBufferType(Buffer _this) {
                return 0;
            }
        }
    }

    /* loaded from: classes5.dex */
    public interface I420Buffer extends Buffer {
        @Override // org.webrtc.VideoFrame.Buffer
        int getBufferType();

        ByteBuffer getDataU();

        ByteBuffer getDataV();

        ByteBuffer getDataY();

        int getStrideU();

        int getStrideV();

        int getStrideY();

        /* renamed from: org.webrtc.VideoFrame$I420Buffer$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static int $default$getBufferType(I420Buffer _this) {
                return 1;
            }
        }
    }

    /* loaded from: classes5.dex */
    public interface TextureBuffer extends Buffer {
        int getTextureId();

        Matrix getTransformMatrix();

        Type getType();

        /* loaded from: classes5.dex */
        public enum Type {
            OES(36197),
            RGB(3553);
            
            private final int glTarget;

            Type(int glTarget) {
                this.glTarget = glTarget;
            }

            public int getGlTarget() {
                return this.glTarget;
            }
        }
    }

    public VideoFrame(Buffer buffer, int rotation, long timestampNs) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer not allowed to be null");
        }
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException("rotation must be a multiple of 90");
        }
        this.buffer = buffer;
        this.rotation = rotation;
        this.timestampNs = timestampNs;
    }

    public Buffer getBuffer() {
        return this.buffer;
    }

    public int getRotation() {
        return this.rotation;
    }

    public long getTimestampNs() {
        return this.timestampNs;
    }

    public int getRotatedWidth() {
        if (this.rotation % 180 == 0) {
            return this.buffer.getWidth();
        }
        return this.buffer.getHeight();
    }

    public int getRotatedHeight() {
        if (this.rotation % 180 == 0) {
            return this.buffer.getHeight();
        }
        return this.buffer.getWidth();
    }

    @Override // org.webrtc.RefCounted
    public void retain() {
        this.buffer.retain();
    }

    @Override // org.webrtc.RefCounted
    public void release() {
        this.buffer.release();
    }
}
