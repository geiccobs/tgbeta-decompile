package org.webrtc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Looper;
import android.view.TextureView;
import android.view.View;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda4;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;
/* loaded from: classes5.dex */
public class TextureViewRenderer extends TextureView implements TextureView.SurfaceTextureListener, VideoSink, RendererCommon.RendererEvents {
    private static final String TAG = "TextureViewRenderer";
    private TextureView backgroundRenderer;
    private int cameraRotation;
    private final TextureEglRenderer eglRenderer;
    private boolean enableFixedSize;
    private boolean isCamera;
    private int maxTextureSize;
    private boolean mirror;
    private OrientationHelper orientationHelper;
    private VideoSink parentSink;
    private RendererCommon.RendererEvents rendererEvents;
    private final String resourceName;
    private boolean rotateTextureWithScreen;
    public int rotatedFrameHeight;
    public int rotatedFrameWidth;
    private int screenRotation;
    private int surfaceHeight;
    private int surfaceWidth;
    int textureRotation;
    Runnable updateScreenRunnable;
    boolean useCameraRotation;
    private int videoHeight;
    private final RendererCommon.VideoLayoutMeasure videoLayoutMeasure = new RendererCommon.VideoLayoutMeasure();
    private int videoWidth;

    public void setBackgroundRenderer(TextureView backgroundRenderer) {
        this.backgroundRenderer = backgroundRenderer;
        if (backgroundRenderer == null) {
            ThreadUtils.checkIsOnMainThread();
            this.eglRenderer.releaseEglSurface(null, true);
            return;
        }
        backgroundRenderer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: org.webrtc.TextureViewRenderer.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                TextureViewRenderer.this.createBackgroundSurface(surfaceTexture);
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                ThreadUtils.checkIsOnMainThread();
                TextureViewRenderer.this.eglRenderer.releaseEglSurface(null, true);
                return false;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
    }

    public void clearFirstFrame() {
        this.eglRenderer.firstFrameRendered = false;
        this.eglRenderer.isFirstFrameRendered = false;
    }

    /* loaded from: classes5.dex */
    public static class TextureEglRenderer extends EglRenderer implements TextureView.SurfaceTextureListener {
        private static final String TAG = "TextureEglRenderer";
        private int frameRotation;
        private boolean isFirstFrameRendered;
        private boolean isRenderingPaused;
        private final Object layoutLock = new Object();
        private RendererCommon.RendererEvents rendererEvents;
        private int rotatedFrameHeight;
        private int rotatedFrameWidth;

        public TextureEglRenderer(String name) {
            super(name);
        }

        public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents, int[] configAttributes, RendererCommon.GlDrawer drawer) {
            ThreadUtils.checkIsOnMainThread();
            this.rendererEvents = rendererEvents;
            synchronized (this.layoutLock) {
                this.isFirstFrameRendered = false;
                this.rotatedFrameWidth = 0;
                this.rotatedFrameHeight = 0;
                this.frameRotation = 0;
            }
            super.init(sharedContext, configAttributes, drawer);
        }

        @Override // org.webrtc.EglRenderer
        public void init(EglBase.Context sharedContext, int[] configAttributes, RendererCommon.GlDrawer drawer) {
            init(sharedContext, (RendererCommon.RendererEvents) null, configAttributes, drawer);
        }

        @Override // org.webrtc.EglRenderer
        public void setFpsReduction(float fps) {
            synchronized (this.layoutLock) {
                this.isRenderingPaused = fps == 0.0f;
            }
            super.setFpsReduction(fps);
        }

        @Override // org.webrtc.EglRenderer
        public void disableFpsReduction() {
            synchronized (this.layoutLock) {
                this.isRenderingPaused = false;
            }
            super.disableFpsReduction();
        }

        @Override // org.webrtc.EglRenderer
        public void pauseVideo() {
            synchronized (this.layoutLock) {
                this.isRenderingPaused = true;
            }
            super.pauseVideo();
        }

        @Override // org.webrtc.EglRenderer, org.webrtc.VideoSink
        public void onFrame(VideoFrame frame) {
            updateFrameDimensionsAndReportEvents(frame);
            super.onFrame(frame);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            ThreadUtils.checkIsOnMainThread();
            createEglSurface(surfaceTexture);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            ThreadUtils.checkIsOnMainThread();
            logD("surfaceChanged: size: " + width + "x" + height);
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            ThreadUtils.checkIsOnMainThread();
            CountDownLatch completionLatch = new CountDownLatch(1);
            completionLatch.getClass();
            releaseEglSurface(new Theme$$ExternalSyntheticLambda4(completionLatch), false);
            ThreadUtils.awaitUninterruptibly(completionLatch);
            return true;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        private void updateFrameDimensionsAndReportEvents(VideoFrame frame) {
            synchronized (this.layoutLock) {
                if (this.isRenderingPaused) {
                    return;
                }
                if (this.rotatedFrameWidth != frame.getRotatedWidth() || this.rotatedFrameHeight != frame.getRotatedHeight() || this.frameRotation != frame.getRotation()) {
                    logD("Reporting frame resolution changed to " + frame.getBuffer().getWidth() + "x" + frame.getBuffer().getHeight() + " with rotation " + frame.getRotation());
                    RendererCommon.RendererEvents rendererEvents = this.rendererEvents;
                    if (rendererEvents != null) {
                        rendererEvents.onFrameResolutionChanged(frame.getBuffer().getWidth(), frame.getBuffer().getHeight(), frame.getRotation());
                    }
                    this.rotatedFrameWidth = frame.getRotatedWidth();
                    this.rotatedFrameHeight = frame.getRotatedHeight();
                    this.frameRotation = frame.getRotation();
                }
            }
        }

        private void logD(String string) {
            Logging.d(TAG, this.name + ": " + string);
        }

        @Override // org.webrtc.EglRenderer
        protected void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.webrtc.TextureViewRenderer$TextureEglRenderer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TextureViewRenderer.TextureEglRenderer.this.m4855xd9a95a80();
                }
            });
        }

        /* renamed from: lambda$onFirstFrameRendered$0$org-webrtc-TextureViewRenderer$TextureEglRenderer */
        public /* synthetic */ void m4855xd9a95a80() {
            this.isFirstFrameRendered = true;
            this.rendererEvents.onFirstFrameRendered();
        }
    }

    public TextureViewRenderer(Context context) {
        super(context);
        String resourceName = getResourceName();
        this.resourceName = resourceName;
        this.eglRenderer = new TextureEglRenderer(resourceName);
        setSurfaceTextureListener(this);
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents) {
        init(sharedContext, rendererEvents, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents, int[] configAttributes, RendererCommon.GlDrawer drawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents;
        this.rotatedFrameWidth = 0;
        this.rotatedFrameHeight = 0;
        this.eglRenderer.init(sharedContext, this, configAttributes, drawer);
    }

    public void release() {
        this.eglRenderer.release();
        OrientationHelper orientationHelper = this.orientationHelper;
        if (orientationHelper != null) {
            orientationHelper.stop();
        }
    }

    public void addFrameListener(EglRenderer.FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam) {
        this.eglRenderer.addFrameListener(listener, scale, drawerParam);
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback callback) {
        this.eglRenderer.getTexture(callback);
    }

    public void addFrameListener(EglRenderer.FrameListener listener, float scale) {
        this.eglRenderer.addFrameListener(listener, scale);
    }

    public void removeFrameListener(EglRenderer.FrameListener listener) {
        this.eglRenderer.removeFrameListener(listener);
    }

    public void setIsCamera(boolean value) {
        this.isCamera = value;
        if (!value) {
            OrientationHelper orientationHelper = new OrientationHelper() { // from class: org.webrtc.TextureViewRenderer.2
                @Override // org.webrtc.OrientationHelper
                protected void onOrientationUpdate(int orientation) {
                    if (!TextureViewRenderer.this.isCamera) {
                        TextureViewRenderer.this.updateRotation();
                    }
                }
            };
            this.orientationHelper = orientationHelper;
            orientationHelper.start();
        }
    }

    public void setEnableHardwareScaler(boolean enabled) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = enabled;
        updateSurfaceSize();
    }

    public void updateRotation() {
        View parentView;
        float h;
        float w;
        float scale;
        if (this.orientationHelper == null || this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0 || (parentView = (View) getParent()) == null) {
            return;
        }
        int orientation = this.orientationHelper.getOrientation();
        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();
        float targetWidth = parentView.getMeasuredWidth();
        float targetHeight = parentView.getMeasuredHeight();
        if (orientation == 90 || orientation == 270) {
            w = viewHeight;
            h = viewWidth;
        } else {
            w = viewWidth;
            h = viewHeight;
        }
        if (w < h) {
            scale = Math.max(w / viewWidth, h / viewHeight);
        } else {
            scale = Math.min(w / viewWidth, h / viewHeight);
        }
        float w2 = w * scale;
        float h2 = h * scale;
        if (Math.abs((w2 / h2) - (targetWidth / targetHeight)) < 0.1f) {
            scale *= Math.max(targetWidth / w2, targetHeight / h2);
        }
        if (orientation == 270) {
            orientation = -90;
        }
        animate().scaleX(scale).scaleY(scale).rotation(-orientation).setDuration(180L).start();
    }

    public void setMirror(boolean mirror) {
        if (this.mirror != mirror) {
            this.mirror = mirror;
            if (this.rotateTextureWithScreen) {
                onRotationChanged();
            } else {
                this.eglRenderer.setMirror(mirror);
            }
            updateSurfaceSize();
            requestLayout();
        }
    }

    public void setScalingType(RendererCommon.ScalingType scalingType) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType);
        requestLayout();
    }

    public void setScalingType(RendererCommon.ScalingType scalingTypeMatchOrientation, RendererCommon.ScalingType scalingTypeMismatchOrientation) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingTypeMatchOrientation, scalingTypeMismatchOrientation);
        requestLayout();
    }

    public void setFpsReduction(float fps) {
        this.eglRenderer.setFpsReduction(fps);
    }

    public void disableFpsReduction() {
        this.eglRenderer.disableFpsReduction();
    }

    public void pauseVideo() {
        this.eglRenderer.pauseVideo();
    }

    @Override // org.webrtc.VideoSink
    public void onFrame(VideoFrame frame) {
        this.eglRenderer.onFrame(frame);
    }

    @Override // android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        Point size;
        ThreadUtils.checkIsOnMainThread();
        if (!this.isCamera && this.rotateTextureWithScreen) {
            updateVideoSizes();
        }
        int i = this.maxTextureSize;
        if (i > 0) {
            size = this.videoLayoutMeasure.measure(this.isCamera, View.MeasureSpec.makeMeasureSpec(Math.min(i, View.MeasureSpec.getSize(widthSpec)), View.MeasureSpec.getMode(widthSpec)), View.MeasureSpec.makeMeasureSpec(Math.min(this.maxTextureSize, View.MeasureSpec.getSize(heightSpec)), View.MeasureSpec.getMode(heightSpec)), this.rotatedFrameWidth, this.rotatedFrameHeight);
        } else {
            size = this.videoLayoutMeasure.measure(this.isCamera, widthSpec, heightSpec, this.rotatedFrameWidth, this.rotatedFrameHeight);
        }
        setMeasuredDimension(size.x, size.y);
        if (this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0) {
            this.eglRenderer.setLayoutAspectRatio(getMeasuredWidth() / getMeasuredHeight());
        }
        updateSurfaceSize();
    }

    private void updateSurfaceSize() {
        int drawnFrameWidth;
        int drawnFrameWidth2;
        ThreadUtils.checkIsOnMainThread();
        if (this.enableFixedSize && this.rotatedFrameWidth != 0 && this.rotatedFrameHeight != 0 && getWidth() != 0 && getHeight() != 0) {
            float layoutAspectRatio = getWidth() / getHeight();
            int i = this.rotatedFrameHeight;
            float frameAspectRatio = this.rotatedFrameWidth / i;
            if (frameAspectRatio > layoutAspectRatio) {
                drawnFrameWidth2 = (int) (i * layoutAspectRatio);
                drawnFrameWidth = this.rotatedFrameHeight;
            } else {
                int drawnFrameHeight = this.rotatedFrameWidth;
                drawnFrameWidth = (int) (i / layoutAspectRatio);
                drawnFrameWidth2 = drawnFrameHeight;
            }
            int width = Math.min(getWidth(), drawnFrameWidth2);
            int height = Math.min(getHeight(), drawnFrameWidth);
            logD("updateSurfaceSize. Layout size: " + getWidth() + "x" + getHeight() + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + width + "x" + height + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
            if (width != this.surfaceWidth || height != this.surfaceHeight) {
                this.surfaceWidth = width;
                this.surfaceHeight = height;
                return;
            }
            return;
        }
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        ThreadUtils.checkIsOnMainThread();
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
        updateSurfaceSize();
        this.eglRenderer.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
        this.eglRenderer.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        VideoSink videoSink = this.parentSink;
        if (videoSink instanceof VoIPService.ProxyVideoSink) {
            VoIPService.ProxyVideoSink proxyVideoSink = (VoIPService.ProxyVideoSink) videoSink;
            proxyVideoSink.removeTarget(this);
            proxyVideoSink.removeBackground(this);
        }
        this.eglRenderer.onSurfaceTextureDestroyed(surfaceTexture);
        return true;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.eglRenderer.onSurfaceTextureUpdated(surfaceTexture);
    }

    private String getResourceName() {
        try {
            return getResources().getResourceEntryName(getId());
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
        this.eglRenderer.isFirstFrameRendered = false;
    }

    @Override // org.webrtc.VideoSink
    public void setParentSink(VideoSink parent) {
        this.parentSink = parent;
    }

    public void onFirstFrameRendered() {
        RendererCommon.RendererEvents rendererEvents = this.rendererEvents;
        if (rendererEvents != null) {
            rendererEvents.onFirstFrameRendered();
        }
    }

    public boolean isFirstFrameRendered() {
        return this.eglRenderer.isFirstFrameRendered;
    }

    @Override // org.webrtc.RendererCommon.RendererEvents
    public void onFrameResolutionChanged(final int videoWidth, final int videoHeight, int rotation) {
        int rotatedWidth;
        int rotatedHeight;
        RendererCommon.RendererEvents rendererEvents = this.rendererEvents;
        if (rendererEvents != null) {
            rendererEvents.onFrameResolutionChanged(videoWidth, videoHeight, rotation);
        }
        this.textureRotation = rotation;
        if (this.rotateTextureWithScreen) {
            if (this.isCamera) {
                onRotationChanged();
            }
            if (this.useCameraRotation) {
                int i = this.screenRotation;
                int rotatedWidth2 = i == 0 ? videoHeight : videoWidth;
                rotatedHeight = i == 0 ? videoWidth : videoHeight;
                rotatedWidth = rotatedWidth2;
            } else {
                int rotatedHeight2 = this.textureRotation;
                int rotatedWidth3 = (rotatedHeight2 == 0 || rotatedHeight2 == 180 || rotatedHeight2 == -180) ? videoWidth : videoHeight;
                rotatedHeight = (rotatedHeight2 == 0 || rotatedHeight2 == 180 || rotatedHeight2 == -180) ? videoHeight : videoWidth;
                rotatedWidth = rotatedWidth3;
            }
        } else {
            if (this.isCamera) {
                this.eglRenderer.setRotation(-OrientationHelper.cameraRotation);
            }
            int rotation2 = rotation - OrientationHelper.cameraOrientation;
            int rotatedWidth4 = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? videoWidth : videoHeight;
            rotatedWidth = rotatedWidth4;
            rotatedHeight = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? videoHeight : videoWidth;
        }
        synchronized (this.eglRenderer.layoutLock) {
            Runnable runnable = this.updateScreenRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            final int i2 = rotatedWidth;
            final int i3 = rotatedHeight;
            Runnable runnable2 = new Runnable() { // from class: org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TextureViewRenderer.this.m4853lambda$onFrameResolutionChanged$0$orgwebrtcTextureViewRenderer(videoWidth, videoHeight, i2, i3);
                }
            };
            this.updateScreenRunnable = runnable2;
            postOrRun(runnable2);
        }
    }

    /* renamed from: lambda$onFrameResolutionChanged$0$org-webrtc-TextureViewRenderer */
    public /* synthetic */ void m4853lambda$onFrameResolutionChanged$0$orgwebrtcTextureViewRenderer(int videoWidth, int videoHeight, int rotatedWidth, int rotatedHeight) {
        this.updateScreenRunnable = null;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.rotatedFrameWidth = rotatedWidth;
        this.rotatedFrameHeight = rotatedHeight;
        updateSurfaceSize();
        requestLayout();
    }

    public void setScreenRotation(int screenRotation) {
        this.screenRotation = screenRotation;
        onRotationChanged();
        updateVideoSizes();
    }

    private void updateVideoSizes() {
        int i;
        final int rotatedWidth;
        final int rotation = this.videoHeight;
        if (rotation != 0 && (i = this.videoWidth) != 0) {
            if (this.rotateTextureWithScreen) {
                if (this.useCameraRotation) {
                    int i2 = this.screenRotation;
                    rotatedWidth = i2 == 0 ? rotation : i;
                    if (i2 == 0) {
                        rotation = i;
                    }
                } else {
                    int i3 = this.textureRotation;
                    int rotatedWidth2 = (i3 == 0 || i3 == 180 || i3 == -180) ? i : rotation;
                    if (i3 != 0 && i3 != 180 && i3 != -180) {
                        rotation = i;
                    }
                    rotatedWidth = rotatedWidth2;
                }
            } else {
                int rotatedHeight = this.textureRotation;
                int rotation2 = rotatedHeight - OrientationHelper.cameraOrientation;
                rotatedWidth = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? this.videoWidth : this.videoHeight;
                rotation = (rotation2 == 0 || rotation2 == 180 || rotation2 == -180) ? this.videoHeight : this.videoWidth;
            }
            if (this.rotatedFrameWidth != rotatedWidth || this.rotatedFrameHeight != rotation) {
                synchronized (this.eglRenderer.layoutLock) {
                    Runnable runnable = this.updateScreenRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    Runnable runnable2 = new Runnable() { // from class: org.webrtc.TextureViewRenderer$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            TextureViewRenderer.this.m4854lambda$updateVideoSizes$1$orgwebrtcTextureViewRenderer(rotatedWidth, rotation);
                        }
                    };
                    this.updateScreenRunnable = runnable2;
                    postOrRun(runnable2);
                }
            }
        }
    }

    /* renamed from: lambda$updateVideoSizes$1$org-webrtc-TextureViewRenderer */
    public /* synthetic */ void m4854lambda$updateVideoSizes$1$orgwebrtcTextureViewRenderer(int rotatedWidth, int rotatedHeight) {
        this.updateScreenRunnable = null;
        this.rotatedFrameWidth = rotatedWidth;
        this.rotatedFrameHeight = rotatedHeight;
        updateSurfaceSize();
        requestLayout();
    }

    public void setRotateTextureWithScreen(boolean rotateTextureWithScreen) {
        if (this.rotateTextureWithScreen != rotateTextureWithScreen) {
            this.rotateTextureWithScreen = rotateTextureWithScreen;
            requestLayout();
        }
    }

    public void setUseCameraRotation(boolean useCameraRotation) {
        if (this.useCameraRotation != useCameraRotation) {
            this.useCameraRotation = useCameraRotation;
            onRotationChanged();
            updateVideoSizes();
        }
    }

    private void onRotationChanged() {
        int rotation = this.useCameraRotation ? OrientationHelper.cameraOrientation : 0;
        boolean z = this.mirror;
        if (z) {
            rotation = 360 - rotation;
        }
        int r = -rotation;
        if (this.useCameraRotation) {
            int i = this.screenRotation;
            if (i == 1) {
                r += z ? 90 : -90;
            } else if (i == 3) {
                r += z ? 270 : -270;
            }
        }
        this.eglRenderer.setRotation(r);
        this.eglRenderer.setMirror(this.mirror);
    }

    @Override // android.view.View
    public void setRotation(float rotation) {
        super.setRotation(rotation);
    }

    @Override // android.view.View
    public void setRotationY(float rotation) {
        super.setRotationY(rotation);
    }

    @Override // android.view.View
    public void setRotationX(float rotation) {
        super.setRotationX(rotation);
    }

    private void postOrRun(Runnable r) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            r.run();
        } else {
            AndroidUtilities.runOnUIThread(r);
        }
    }

    private void logD(String string) {
        Logging.d(TAG, this.resourceName + ": " + string);
    }

    public void createBackgroundSurface(SurfaceTexture bluSurfaceTexturerRenderer) {
        this.eglRenderer.createBackgroundSurface(bluSurfaceTexturerRenderer);
    }

    public void setMaxTextureSize(int maxTextureSize) {
        this.maxTextureSize = maxTextureSize;
    }
}
