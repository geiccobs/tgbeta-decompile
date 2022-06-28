package org.webrtc;

import android.graphics.Matrix;
import android.os.Handler;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import org.telegram.messenger.FileLog;
import org.webrtc.VideoFrame;
/* loaded from: classes5.dex */
public class TextureBufferImpl implements VideoFrame.TextureBuffer {
    private final int height;
    private final int id;
    private final RefCountDelegate refCountDelegate;
    private final RefCountMonitor refCountMonitor;
    private final Handler toI420Handler;
    private final Matrix transformMatrix;
    private final VideoFrame.TextureBuffer.Type type;
    private final int unscaledHeight;
    private final int unscaledWidth;
    private final int width;
    private final YuvConverter yuvConverter;

    /* loaded from: classes5.dex */
    public interface RefCountMonitor {
        void onDestroy(TextureBufferImpl textureBufferImpl);

        void onRelease(TextureBufferImpl textureBufferImpl);

        void onRetain(TextureBufferImpl textureBufferImpl);
    }

    @Override // org.webrtc.VideoFrame.Buffer
    public /* synthetic */ int getBufferType() {
        return VideoFrame.Buffer.CC.$default$getBufferType(this);
    }

    public TextureBufferImpl(int width, int height, VideoFrame.TextureBuffer.Type type, int id, Matrix transformMatrix, Handler toI420Handler, YuvConverter yuvConverter, final Runnable releaseCallback) {
        this(width, height, width, height, type, id, transformMatrix, toI420Handler, yuvConverter, new RefCountMonitor() { // from class: org.webrtc.TextureBufferImpl.1
            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onRetain(TextureBufferImpl textureBuffer) {
            }

            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onRelease(TextureBufferImpl textureBuffer) {
            }

            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onDestroy(TextureBufferImpl textureBuffer) {
                Runnable runnable = releaseCallback;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    public TextureBufferImpl(int width, int height, VideoFrame.TextureBuffer.Type type, int id, Matrix transformMatrix, Handler toI420Handler, YuvConverter yuvConverter, RefCountMonitor refCountMonitor) {
        this(width, height, width, height, type, id, transformMatrix, toI420Handler, yuvConverter, refCountMonitor);
    }

    private TextureBufferImpl(int unscaledWidth, int unscaledHeight, int width, int height, VideoFrame.TextureBuffer.Type type, int id, Matrix transformMatrix, Handler toI420Handler, YuvConverter yuvConverter, final RefCountMonitor refCountMonitor) {
        this.unscaledWidth = unscaledWidth;
        this.unscaledHeight = unscaledHeight;
        this.width = width;
        this.height = height;
        this.type = type;
        this.id = id;
        this.transformMatrix = transformMatrix;
        this.toI420Handler = toI420Handler;
        this.yuvConverter = yuvConverter;
        this.refCountDelegate = new RefCountDelegate(new Runnable() { // from class: org.webrtc.TextureBufferImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                TextureBufferImpl.this.m4851lambda$new$0$orgwebrtcTextureBufferImpl(refCountMonitor);
            }
        });
        this.refCountMonitor = refCountMonitor;
    }

    /* renamed from: lambda$new$0$org-webrtc-TextureBufferImpl */
    public /* synthetic */ void m4851lambda$new$0$orgwebrtcTextureBufferImpl(RefCountMonitor refCountMonitor) {
        refCountMonitor.onDestroy(this);
    }

    @Override // org.webrtc.VideoFrame.TextureBuffer
    public VideoFrame.TextureBuffer.Type getType() {
        return this.type;
    }

    @Override // org.webrtc.VideoFrame.TextureBuffer
    public int getTextureId() {
        return this.id;
    }

    @Override // org.webrtc.VideoFrame.TextureBuffer
    public Matrix getTransformMatrix() {
        return this.transformMatrix;
    }

    @Override // org.webrtc.VideoFrame.Buffer
    public int getWidth() {
        return this.width;
    }

    @Override // org.webrtc.VideoFrame.Buffer
    public int getHeight() {
        return this.height;
    }

    @Override // org.webrtc.VideoFrame.Buffer
    public VideoFrame.I420Buffer toI420() {
        try {
            return (VideoFrame.I420Buffer) ThreadUtils.invokeAtFrontUninterruptibly(this.toI420Handler, new Callable() { // from class: org.webrtc.TextureBufferImpl$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return TextureBufferImpl.this.m4852lambda$toI420$1$orgwebrtcTextureBufferImpl();
                }
            });
        } catch (Throwable e) {
            FileLog.e(e);
            int frameWidth = getWidth();
            int frameHeight = getHeight();
            int stride = ((frameWidth + 7) / 8) * 8;
            int uvHeight = (frameHeight + 1) / 2;
            int totalHeight = frameHeight + uvHeight;
            final ByteBuffer i420ByteBuffer = JniCommon.nativeAllocateByteBuffer(stride * totalHeight);
            while (i420ByteBuffer.hasRemaining()) {
                i420ByteBuffer.put((byte) 0);
            }
            int i = stride / 4;
            int uPos = (stride * frameHeight) + 0;
            int vPos = uPos + (stride / 2);
            i420ByteBuffer.position(0);
            i420ByteBuffer.limit((stride * frameHeight) + 0);
            ByteBuffer dataY = i420ByteBuffer.slice();
            i420ByteBuffer.position(uPos);
            int uvSize = ((uvHeight - 1) * stride) + (stride / 2);
            i420ByteBuffer.limit(uPos + uvSize);
            ByteBuffer dataU = i420ByteBuffer.slice();
            i420ByteBuffer.position(vPos);
            i420ByteBuffer.limit(vPos + uvSize);
            ByteBuffer dataV = i420ByteBuffer.slice();
            return JavaI420Buffer.wrap(frameWidth, frameHeight, dataY, stride, dataU, stride, dataV, stride, new Runnable() { // from class: org.webrtc.TextureBufferImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    JniCommon.nativeFreeByteBuffer(i420ByteBuffer);
                }
            });
        }
    }

    /* renamed from: lambda$toI420$1$org-webrtc-TextureBufferImpl */
    public /* synthetic */ VideoFrame.I420Buffer m4852lambda$toI420$1$orgwebrtcTextureBufferImpl() throws Exception {
        return this.yuvConverter.convert(this);
    }

    @Override // org.webrtc.VideoFrame.Buffer, org.webrtc.RefCounted
    public void retain() {
        this.refCountMonitor.onRetain(this);
        this.refCountDelegate.retain();
    }

    @Override // org.webrtc.VideoFrame.Buffer, org.webrtc.RefCounted
    public void release() {
        this.refCountMonitor.onRelease(this);
        this.refCountDelegate.release();
    }

    @Override // org.webrtc.VideoFrame.Buffer
    public VideoFrame.Buffer cropAndScale(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight) {
        Matrix cropAndScaleMatrix = new Matrix();
        int i = this.height;
        int cropYFromBottom = i - (cropY + cropHeight);
        cropAndScaleMatrix.preTranslate(cropX / this.width, cropYFromBottom / i);
        cropAndScaleMatrix.preScale(cropWidth / this.width, cropHeight / this.height);
        return applyTransformMatrix(cropAndScaleMatrix, Math.round((this.unscaledWidth * cropWidth) / this.width), Math.round((this.unscaledHeight * cropHeight) / this.height), scaleWidth, scaleHeight);
    }

    public int getUnscaledWidth() {
        return this.unscaledWidth;
    }

    public int getUnscaledHeight() {
        return this.unscaledHeight;
    }

    public Handler getToI420Handler() {
        return this.toI420Handler;
    }

    public YuvConverter getYuvConverter() {
        return this.yuvConverter;
    }

    public TextureBufferImpl applyTransformMatrix(Matrix transformMatrix, int newWidth, int newHeight) {
        return applyTransformMatrix(transformMatrix, newWidth, newHeight, newWidth, newHeight);
    }

    private TextureBufferImpl applyTransformMatrix(Matrix transformMatrix, int unscaledWidth, int unscaledHeight, int scaledWidth, int scaledHeight) {
        Matrix newMatrix = new Matrix(this.transformMatrix);
        newMatrix.preConcat(transformMatrix);
        retain();
        return new TextureBufferImpl(unscaledWidth, unscaledHeight, scaledWidth, scaledHeight, this.type, this.id, newMatrix, this.toI420Handler, this.yuvConverter, new RefCountMonitor() { // from class: org.webrtc.TextureBufferImpl.2
            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onRetain(TextureBufferImpl textureBuffer) {
                TextureBufferImpl.this.refCountMonitor.onRetain(TextureBufferImpl.this);
            }

            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onRelease(TextureBufferImpl textureBuffer) {
                TextureBufferImpl.this.refCountMonitor.onRelease(TextureBufferImpl.this);
            }

            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onDestroy(TextureBufferImpl textureBuffer) {
                TextureBufferImpl.this.release();
            }
        });
    }
}
