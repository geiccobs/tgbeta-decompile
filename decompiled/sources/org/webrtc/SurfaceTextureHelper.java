package org.webrtc;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import java.util.concurrent.Callable;
import org.webrtc.EglBase;
import org.webrtc.TextureBufferImpl;
import org.webrtc.VideoFrame;
/* loaded from: classes5.dex */
public class SurfaceTextureHelper {
    private static final String TAG = "SurfaceTextureHelper";
    private final EglBase eglBase;
    private final FrameRefMonitor frameRefMonitor;
    private int frameRotation;
    private final Handler handler;
    private boolean hasPendingTexture;
    private boolean isQuitting;
    private volatile boolean isTextureInUse;
    private VideoSink listener;
    private final int oesTextureId;
    private VideoSink pendingListener;
    final Runnable setListenerRunnable;
    private final SurfaceTexture surfaceTexture;
    private int textureHeight;
    private final TextureBufferImpl.RefCountMonitor textureRefCountMonitor;
    private int textureWidth;
    private final TimestampAligner timestampAligner;
    private final YuvConverter yuvConverter;

    /* loaded from: classes5.dex */
    public interface FrameRefMonitor {
        void onDestroyBuffer(VideoFrame.TextureBuffer textureBuffer);

        void onNewBuffer(VideoFrame.TextureBuffer textureBuffer);

        void onReleaseBuffer(VideoFrame.TextureBuffer textureBuffer);

        void onRetainBuffer(VideoFrame.TextureBuffer textureBuffer);
    }

    public static SurfaceTextureHelper create(final String threadName, final EglBase.Context sharedContext, final boolean alignTimestamps, final YuvConverter yuvConverter, final FrameRefMonitor frameRefMonitor) {
        HandlerThread thread = new HandlerThread(threadName);
        thread.start();
        final Handler handler = new Handler(thread.getLooper());
        return (SurfaceTextureHelper) ThreadUtils.invokeAtFrontUninterruptibly(handler, new Callable<SurfaceTextureHelper>() { // from class: org.webrtc.SurfaceTextureHelper.1
            @Override // java.util.concurrent.Callable
            public SurfaceTextureHelper call() {
                try {
                    return new SurfaceTextureHelper(sharedContext, handler, alignTimestamps, yuvConverter, frameRefMonitor);
                } catch (RuntimeException e) {
                    Logging.e(SurfaceTextureHelper.TAG, threadName + " create failure", e);
                    return null;
                }
            }
        });
    }

    public static SurfaceTextureHelper create(String threadName, EglBase.Context sharedContext) {
        return create(threadName, sharedContext, false, new YuvConverter(), null);
    }

    public static SurfaceTextureHelper create(String threadName, EglBase.Context sharedContext, boolean alignTimestamps) {
        return create(threadName, sharedContext, alignTimestamps, new YuvConverter(), null);
    }

    public static SurfaceTextureHelper create(String threadName, EglBase.Context sharedContext, boolean alignTimestamps, YuvConverter yuvConverter) {
        return create(threadName, sharedContext, alignTimestamps, yuvConverter, null);
    }

    private SurfaceTextureHelper(EglBase.Context sharedContext, Handler handler, boolean alignTimestamps, YuvConverter yuvConverter, FrameRefMonitor frameRefMonitor) {
        this.textureRefCountMonitor = new TextureBufferImpl.RefCountMonitor() { // from class: org.webrtc.SurfaceTextureHelper.2
            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onRetain(TextureBufferImpl textureBuffer) {
                if (SurfaceTextureHelper.this.frameRefMonitor != null) {
                    SurfaceTextureHelper.this.frameRefMonitor.onRetainBuffer(textureBuffer);
                }
            }

            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onRelease(TextureBufferImpl textureBuffer) {
                if (SurfaceTextureHelper.this.frameRefMonitor != null) {
                    SurfaceTextureHelper.this.frameRefMonitor.onReleaseBuffer(textureBuffer);
                }
            }

            @Override // org.webrtc.TextureBufferImpl.RefCountMonitor
            public void onDestroy(TextureBufferImpl textureBuffer) {
                SurfaceTextureHelper.this.returnTextureFrame();
                if (SurfaceTextureHelper.this.frameRefMonitor != null) {
                    SurfaceTextureHelper.this.frameRefMonitor.onDestroyBuffer(textureBuffer);
                }
            }
        };
        this.setListenerRunnable = new Runnable() { // from class: org.webrtc.SurfaceTextureHelper.3
            @Override // java.lang.Runnable
            public void run() {
                Logging.d(SurfaceTextureHelper.TAG, "Setting listener to " + SurfaceTextureHelper.this.pendingListener);
                SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.this;
                surfaceTextureHelper.listener = surfaceTextureHelper.pendingListener;
                SurfaceTextureHelper.this.pendingListener = null;
                if (SurfaceTextureHelper.this.hasPendingTexture) {
                    SurfaceTextureHelper.this.updateTexImage();
                    SurfaceTextureHelper.this.hasPendingTexture = false;
                }
            }
        };
        if (handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("SurfaceTextureHelper must be created on the handler thread");
        }
        this.handler = handler;
        this.timestampAligner = alignTimestamps ? new TimestampAligner() : null;
        this.yuvConverter = yuvConverter;
        this.frameRefMonitor = frameRefMonitor;
        EglBase create = EglBase.CC.create(sharedContext, EglBase.CONFIG_PIXEL_BUFFER);
        this.eglBase = create;
        try {
            create.createDummyPbufferSurface();
            create.makeCurrent();
            int generateTexture = GlUtil.generateTexture(36197);
            this.oesTextureId = generateTexture;
            SurfaceTexture surfaceTexture = new SurfaceTexture(generateTexture);
            this.surfaceTexture = surfaceTexture;
            setOnFrameAvailableListener(surfaceTexture, new SurfaceTexture.OnFrameAvailableListener() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda0
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public final void onFrameAvailable(SurfaceTexture surfaceTexture2) {
                    SurfaceTextureHelper.this.m4845lambda$new$0$orgwebrtcSurfaceTextureHelper(surfaceTexture2);
                }
            }, handler);
        } catch (RuntimeException e) {
            this.eglBase.release();
            handler.getLooper().quit();
            throw e;
        }
    }

    /* renamed from: lambda$new$0$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4845lambda$new$0$orgwebrtcSurfaceTextureHelper(SurfaceTexture st) {
        if (this.hasPendingTexture) {
            Logging.d(TAG, "A frame is already pending, dropping frame.");
        }
        this.hasPendingTexture = true;
        tryDeliverTextureFrame();
    }

    private static void setOnFrameAvailableListener(SurfaceTexture surfaceTexture, SurfaceTexture.OnFrameAvailableListener listener, Handler handler) {
        if (Build.VERSION.SDK_INT >= 21) {
            surfaceTexture.setOnFrameAvailableListener(listener, handler);
        } else {
            surfaceTexture.setOnFrameAvailableListener(listener);
        }
    }

    public void startListening(VideoSink listener) {
        if (this.listener != null || this.pendingListener != null) {
            throw new IllegalStateException("SurfaceTextureHelper listener has already been set.");
        }
        this.pendingListener = listener;
        this.handler.post(this.setListenerRunnable);
    }

    public void stopListening() {
        Logging.d(TAG, "stopListening()");
        this.handler.removeCallbacks(this.setListenerRunnable);
        ThreadUtils.invokeAtFrontUninterruptibly(this.handler, new Runnable() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceTextureHelper.this.m4849lambda$stopListening$1$orgwebrtcSurfaceTextureHelper();
            }
        });
    }

    /* renamed from: lambda$stopListening$1$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4849lambda$stopListening$1$orgwebrtcSurfaceTextureHelper() {
        this.listener = null;
        this.pendingListener = null;
    }

    public void setTextureSize(final int textureWidth, final int textureHeight) {
        if (textureWidth <= 0) {
            throw new IllegalArgumentException("Texture width must be positive, but was " + textureWidth);
        } else if (textureHeight <= 0) {
            throw new IllegalArgumentException("Texture height must be positive, but was " + textureHeight);
        } else {
            this.surfaceTexture.setDefaultBufferSize(textureWidth, textureHeight);
            this.handler.post(new Runnable() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    SurfaceTextureHelper.this.m4848lambda$setTextureSize$2$orgwebrtcSurfaceTextureHelper(textureWidth, textureHeight);
                }
            });
        }
    }

    /* renamed from: lambda$setTextureSize$2$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4848lambda$setTextureSize$2$orgwebrtcSurfaceTextureHelper(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        tryDeliverTextureFrame();
    }

    public void forceFrame() {
        this.handler.post(new Runnable() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceTextureHelper.this.m4844lambda$forceFrame$3$orgwebrtcSurfaceTextureHelper();
            }
        });
    }

    /* renamed from: lambda$forceFrame$3$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4844lambda$forceFrame$3$orgwebrtcSurfaceTextureHelper() {
        this.hasPendingTexture = true;
        tryDeliverTextureFrame();
    }

    /* renamed from: lambda$setFrameRotation$4$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4847lambda$setFrameRotation$4$orgwebrtcSurfaceTextureHelper(int rotation) {
        this.frameRotation = rotation;
    }

    public void setFrameRotation(final int rotation) {
        this.handler.post(new Runnable() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceTextureHelper.this.m4847lambda$setFrameRotation$4$orgwebrtcSurfaceTextureHelper(rotation);
            }
        });
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.surfaceTexture;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void returnTextureFrame() {
        this.handler.post(new Runnable() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceTextureHelper.this.m4846lambda$returnTextureFrame$5$orgwebrtcSurfaceTextureHelper();
            }
        });
    }

    /* renamed from: lambda$returnTextureFrame$5$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4846lambda$returnTextureFrame$5$orgwebrtcSurfaceTextureHelper() {
        this.isTextureInUse = false;
        if (this.isQuitting) {
            release();
        } else {
            tryDeliverTextureFrame();
        }
    }

    public boolean isTextureInUse() {
        return this.isTextureInUse;
    }

    public void dispose() {
        Logging.d(TAG, "dispose()");
        ThreadUtils.invokeAtFrontUninterruptibly(this.handler, new Runnable() { // from class: org.webrtc.SurfaceTextureHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceTextureHelper.this.m4843lambda$dispose$6$orgwebrtcSurfaceTextureHelper();
            }
        });
    }

    /* renamed from: lambda$dispose$6$org-webrtc-SurfaceTextureHelper */
    public /* synthetic */ void m4843lambda$dispose$6$orgwebrtcSurfaceTextureHelper() {
        this.isQuitting = true;
        if (!this.isTextureInUse) {
            release();
        }
    }

    @Deprecated
    public VideoFrame.I420Buffer textureToYuv(VideoFrame.TextureBuffer textureBuffer) {
        return textureBuffer.toI420();
    }

    public void updateTexImage() {
        synchronized (EglBase.lock) {
            try {
                this.surfaceTexture.updateTexImage();
            } catch (Throwable th) {
            }
        }
    }

    private void tryDeliverTextureFrame() {
        if (this.handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Wrong thread.");
        }
        if (this.isQuitting || !this.hasPendingTexture || this.isTextureInUse || this.listener == null) {
            return;
        }
        if (this.textureWidth == 0 || this.textureHeight == 0) {
            Logging.w(TAG, "Texture size has not been set.");
            return;
        }
        this.isTextureInUse = true;
        this.hasPendingTexture = false;
        updateTexImage();
        float[] transformMatrix = new float[16];
        this.surfaceTexture.getTransformMatrix(transformMatrix);
        long timestampNs = this.surfaceTexture.getTimestamp();
        TimestampAligner timestampAligner = this.timestampAligner;
        if (timestampAligner != null) {
            timestampNs = timestampAligner.translateTimestamp(timestampNs);
        }
        VideoFrame.TextureBuffer buffer = new TextureBufferImpl(this.textureWidth, this.textureHeight, VideoFrame.TextureBuffer.Type.OES, this.oesTextureId, RendererCommon.convertMatrixToAndroidGraphicsMatrix(transformMatrix), this.handler, this.yuvConverter, this.textureRefCountMonitor);
        FrameRefMonitor frameRefMonitor = this.frameRefMonitor;
        if (frameRefMonitor != null) {
            frameRefMonitor.onNewBuffer(buffer);
        }
        VideoFrame frame = new VideoFrame(buffer, this.frameRotation, timestampNs);
        this.listener.onFrame(frame);
        frame.release();
    }

    private void release() {
        if (this.handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Wrong thread.");
        }
        if (this.isTextureInUse || !this.isQuitting) {
            throw new IllegalStateException("Unexpected release.");
        }
        this.yuvConverter.release();
        GLES20.glDeleteTextures(1, new int[]{this.oesTextureId}, 0);
        this.surfaceTexture.release();
        this.eglBase.release();
        this.handler.getLooper().quit();
        TimestampAligner timestampAligner = this.timestampAligner;
        if (timestampAligner != null) {
            timestampAligner.dispose();
        }
    }
}
