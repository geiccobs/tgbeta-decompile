package org.webrtc;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.view.Surface;
import org.telegram.messenger.FileLog;
import org.webrtc.VideoSink;
/* loaded from: classes5.dex */
public class ScreenCapturerAndroid implements VideoCapturer, VideoSink {
    private static final int DISPLAY_FLAGS = 3;
    private static final int VIRTUAL_DISPLAY_DPI = 400;
    private CapturerObserver capturerObserver;
    private int height;
    private boolean isDisposed;
    private MediaProjection mediaProjection;
    private final MediaProjection.Callback mediaProjectionCallback;
    private MediaProjectionManager mediaProjectionManager;
    private final Intent mediaProjectionPermissionResultData;
    private long numCapturedFrames;
    private SurfaceTextureHelper surfaceTextureHelper;
    private VirtualDisplay virtualDisplay;
    private int width;

    @Override // org.webrtc.VideoSink
    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }

    public ScreenCapturerAndroid(Intent mediaProjectionPermissionResultData, MediaProjection.Callback mediaProjectionCallback) {
        this.mediaProjectionPermissionResultData = mediaProjectionPermissionResultData;
        this.mediaProjectionCallback = mediaProjectionCallback;
    }

    private void checkNotDisposed() {
        if (this.isDisposed) {
            throw new RuntimeException("capturer is disposed.");
        }
    }

    public MediaProjection getMediaProjection() {
        return this.mediaProjection;
    }

    @Override // org.webrtc.VideoCapturer
    public synchronized void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        checkNotDisposed();
        if (capturerObserver == null) {
            throw new RuntimeException("capturerObserver not set.");
        }
        this.capturerObserver = capturerObserver;
        if (surfaceTextureHelper == null) {
            throw new RuntimeException("surfaceTextureHelper not set.");
        }
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.mediaProjectionManager = (MediaProjectionManager) applicationContext.getSystemService("media_projection");
    }

    @Override // org.webrtc.VideoCapturer
    public synchronized void startCapture(int width, int height, int ignoredFramerate) {
        if (this.mediaProjection != null || this.mediaProjectionManager == null) {
            return;
        }
        checkNotDisposed();
        this.width = width;
        this.height = height;
        MediaProjection mediaProjection = this.mediaProjectionManager.getMediaProjection(-1, this.mediaProjectionPermissionResultData);
        this.mediaProjection = mediaProjection;
        mediaProjection.registerCallback(this.mediaProjectionCallback, this.surfaceTextureHelper.getHandler());
        createVirtualDisplay();
        this.capturerObserver.onCapturerStarted(true);
        this.surfaceTextureHelper.startListening(this);
    }

    @Override // org.webrtc.VideoCapturer
    public synchronized void stopCapture() {
        checkNotDisposed();
        ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable() { // from class: org.webrtc.ScreenCapturerAndroid$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ScreenCapturerAndroid.this.m4842lambda$stopCapture$0$orgwebrtcScreenCapturerAndroid();
            }
        });
    }

    /* renamed from: lambda$stopCapture$0$org-webrtc-ScreenCapturerAndroid */
    public /* synthetic */ void m4842lambda$stopCapture$0$orgwebrtcScreenCapturerAndroid() {
        this.surfaceTextureHelper.stopListening();
        this.capturerObserver.onCapturerStopped();
        VirtualDisplay virtualDisplay = this.virtualDisplay;
        if (virtualDisplay != null) {
            virtualDisplay.release();
            this.virtualDisplay = null;
        }
        MediaProjection mediaProjection = this.mediaProjection;
        if (mediaProjection != null) {
            mediaProjection.unregisterCallback(this.mediaProjectionCallback);
            this.mediaProjection.stop();
            this.mediaProjection = null;
        }
    }

    @Override // org.webrtc.VideoCapturer
    public synchronized void dispose() {
        this.isDisposed = true;
    }

    @Override // org.webrtc.VideoCapturer
    public synchronized void changeCaptureFormat(int width, int height, int ignoredFramerate) {
        checkNotDisposed();
        this.width = width;
        this.height = height;
        if (this.virtualDisplay == null) {
            return;
        }
        ThreadUtils.invokeAtFrontUninterruptibly(this.surfaceTextureHelper.getHandler(), new Runnable() { // from class: org.webrtc.ScreenCapturerAndroid$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ScreenCapturerAndroid.this.m4841lambda$changeCaptureFormat$1$orgwebrtcScreenCapturerAndroid();
            }
        });
    }

    /* renamed from: lambda$changeCaptureFormat$1$org-webrtc-ScreenCapturerAndroid */
    public /* synthetic */ void m4841lambda$changeCaptureFormat$1$orgwebrtcScreenCapturerAndroid() {
        this.virtualDisplay.release();
        createVirtualDisplay();
    }

    private void createVirtualDisplay() {
        this.surfaceTextureHelper.setTextureSize(this.width, this.height);
        try {
            this.virtualDisplay = this.mediaProjection.createVirtualDisplay("WebRTC_ScreenCapture", this.width, this.height, VIRTUAL_DISPLAY_DPI, 3, new Surface(this.surfaceTextureHelper.getSurfaceTexture()), null, null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    @Override // org.webrtc.VideoSink
    public void onFrame(VideoFrame frame) {
        this.numCapturedFrames++;
        this.capturerObserver.onFrameCaptured(frame);
    }

    @Override // org.webrtc.VideoCapturer
    public boolean isScreencast() {
        return true;
    }

    public long getNumCapturedFrames() {
        return this.numCapturedFrames;
    }
}
