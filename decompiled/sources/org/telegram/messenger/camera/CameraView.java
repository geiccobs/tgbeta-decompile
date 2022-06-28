package org.telegram.messenger.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.LayoutHelper;
import org.webrtc.EglBase;
/* loaded from: classes4.dex */
public class CameraView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private static final int audioSampleRate = 44100;
    private ImageView blurredStubView;
    private File cameraFile;
    private CameraSession cameraSession;
    CameraGLThread cameraThread;
    private int clipBottom;
    private int clipTop;
    private int cx;
    private int cy;
    private CameraViewDelegate delegate;
    boolean firstFrameRendered;
    ValueAnimator flipAnimator;
    boolean flipHalfReached;
    CameraInfo info;
    private boolean inited;
    private boolean initialFrontface;
    private float innerAlpha;
    private boolean isFrontface;
    private long lastDrawTime;
    private boolean mirror;
    long nextFrameTimeNs;
    Runnable onRecordingFinishRunnable;
    private boolean optimizeForBarcode;
    private float outerAlpha;
    private Size pictureSize;
    private Size previewSize;
    File recordFile;
    private volatile int surfaceHeight;
    private volatile int surfaceWidth;
    private FloatBuffer textureBuffer;
    private TextureView textureView;
    private boolean useMaxPreview;
    private FloatBuffer vertexBuffer;
    private VideoRecorder videoEncoder;
    private Matrix txform = new Matrix();
    private Matrix matrix = new Matrix();
    private float focusProgress = 1.0f;
    private Paint outerPaint = new Paint(1);
    private Paint innerPaint = new Paint(1);
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private final Object layoutLock = new Object();
    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private float[] moldSTMatrix = new float[16];
    private int fpsLimit = -1;
    private int measurementsCount = 0;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private final Runnable updateRotationMatrix = new Runnable() { // from class: org.telegram.messenger.camera.CameraView$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            CameraView.this.m1250lambda$new$1$orgtelegrammessengercameraCameraView();
        }
    };
    private float takePictureProgress = 1.0f;
    private int[] position = new int[2];
    private int[] cameraTexture = new int[1];
    private int[] oldCameraTexture = new int[1];
    private int focusAreaSize = AndroidUtilities.dp(96.0f);

    /* loaded from: classes4.dex */
    public interface CameraViewDelegate {
        void onCameraInit();
    }

    public void setRecordFile(File generateVideoPath) {
        this.recordFile = generateVideoPath;
    }

    public boolean startRecording(File path, Runnable onFinished) {
        this.cameraThread.startRecording(path);
        this.onRecordingFinishRunnable = onFinished;
        return true;
    }

    public void stopRecording() {
        this.cameraThread.stopRecording();
    }

    public void startSwitchingAnimation() {
        ValueAnimator valueAnimator = this.flipAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.blurredStubView.animate().setListener(null).cancel();
        if (this.firstFrameRendered) {
            Bitmap bitmap = this.textureView.getBitmap(100, 100);
            if (bitmap != null) {
                Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                Drawable drawable = new BitmapDrawable(bitmap);
                this.blurredStubView.setBackground(drawable);
            }
            this.blurredStubView.setAlpha(0.0f);
        } else {
            this.blurredStubView.setAlpha(1.0f);
        }
        this.blurredStubView.setVisibility(0);
        synchronized (this.layoutLock) {
            this.firstFrameRendered = false;
        }
        this.flipHalfReached = false;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.flipAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.messenger.camera.CameraView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                float rotation;
                float v = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                boolean halfReached = false;
                if (v < 0.5f) {
                    rotation = v;
                } else {
                    halfReached = true;
                    rotation = v - 1.0f;
                }
                float rotation2 = rotation * 180.0f;
                CameraView.this.textureView.setRotationY(rotation2);
                CameraView.this.blurredStubView.setRotationY(rotation2);
                if (halfReached && !CameraView.this.flipHalfReached) {
                    CameraView.this.blurredStubView.setAlpha(1.0f);
                    CameraView.this.flipHalfReached = true;
                }
            }
        });
        this.flipAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.messenger.camera.CameraView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                CameraView.this.flipAnimator = null;
                CameraView.this.textureView.setTranslationY(0.0f);
                CameraView.this.textureView.setRotationX(0.0f);
                CameraView.this.textureView.setRotationY(0.0f);
                CameraView.this.textureView.setScaleX(1.0f);
                CameraView.this.textureView.setScaleY(1.0f);
                CameraView.this.blurredStubView.setRotationY(0.0f);
                if (!CameraView.this.flipHalfReached) {
                    CameraView.this.blurredStubView.setAlpha(1.0f);
                    CameraView.this.flipHalfReached = true;
                }
                CameraView.this.invalidate();
            }
        });
        this.flipAnimator.setDuration(400L);
        this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.flipAnimator.start();
        invalidate();
    }

    public CameraView(Context context, boolean frontface) {
        super(context, null);
        this.isFrontface = frontface;
        this.initialFrontface = frontface;
        TextureView textureView = new TextureView(context);
        this.textureView = textureView;
        textureView.setSurfaceTextureListener(this);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        ImageView imageView = new ImageView(context);
        this.blurredStubView = imageView;
        addView(imageView, LayoutHelper.createFrame(-1, -1, 17));
        this.blurredStubView.setVisibility(8);
        this.outerPaint.setColor(-1);
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.innerPaint.setColor(Integer.MAX_VALUE);
    }

    public void setOptimizeForBarcode(boolean value) {
        this.optimizeForBarcode = value;
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.setOptimizeForBarcode(true);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.measurementsCount = 0;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CameraSession cameraSession;
        int frameHeight;
        int frameWidth;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.previewSize != null && (cameraSession = this.cameraSession) != null) {
            if ((this.lastWidth != width || this.lastHeight != height) && this.measurementsCount > 1) {
                cameraSession.updateRotation();
            }
            this.measurementsCount++;
            if (this.cameraSession.getWorldAngle() == 90 || this.cameraSession.getWorldAngle() == 270) {
                frameWidth = this.previewSize.getWidth();
                frameHeight = this.previewSize.getHeight();
            } else {
                frameWidth = this.previewSize.getHeight();
                frameHeight = this.previewSize.getWidth();
            }
            float s = Math.max(View.MeasureSpec.getSize(widthMeasureSpec) / frameWidth, View.MeasureSpec.getSize(heightMeasureSpec) / frameHeight);
            ViewGroup.LayoutParams layoutParams = this.blurredStubView.getLayoutParams();
            int i = (int) (frameWidth * s);
            this.textureView.getLayoutParams().width = i;
            layoutParams.width = i;
            ViewGroup.LayoutParams layoutParams2 = this.blurredStubView.getLayoutParams();
            int i2 = (int) (frameHeight * s);
            this.textureView.getLayoutParams().height = i2;
            layoutParams2.height = i2;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        checkPreviewMatrix();
        this.lastWidth = width;
        this.lastHeight = height;
    }

    public float getTextureHeight(float width, float height) {
        CameraSession cameraSession;
        int frameHeight;
        int frameWidth;
        if (this.previewSize == null || (cameraSession = this.cameraSession) == null) {
            return height;
        }
        if (cameraSession.getWorldAngle() == 90 || this.cameraSession.getWorldAngle() == 270) {
            frameWidth = this.previewSize.getWidth();
            frameHeight = this.previewSize.getHeight();
        } else {
            frameWidth = this.previewSize.getHeight();
            frameHeight = this.previewSize.getWidth();
        }
        float s = Math.max(width / frameWidth, height / frameHeight);
        return (int) (frameHeight * s);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkPreviewMatrix();
    }

    public void setMirror(boolean value) {
        this.mirror = value;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void setUseMaxPreview(boolean value) {
        this.useMaxPreview = value;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        for (int a = 0; a < cameraInfos.size(); a++) {
            if (cameraInfos.get(a).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, new CountDownLatch(1), null);
            this.cameraSession = null;
        }
        this.inited = false;
        this.isFrontface = !this.isFrontface;
        updateCameraInfoSize();
        this.cameraThread.reinitForNewCamera();
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        updateCameraInfoSize();
        this.surfaceHeight = height;
        this.surfaceWidth = width;
        if (this.cameraThread == null && surface != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView start create thread");
            }
            this.cameraThread = new CameraGLThread(surface);
            checkPreviewMatrix();
        }
    }

    private void updateCameraInfoSize() {
        int wantedHeight;
        int photoMaxHeight;
        int wantedWidth;
        int photoMaxWidth;
        Size aspectRatio;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return;
        }
        for (int a = 0; a < cameraInfos.size(); a++) {
            CameraInfo cameraInfo = cameraInfos.get(a);
            if ((this.isFrontface && cameraInfo.frontCamera != 0) || (!this.isFrontface && cameraInfo.frontCamera == 0)) {
                this.info = cameraInfo;
                break;
            }
        }
        if (this.info == null) {
            return;
        }
        float screenSize = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        if (this.initialFrontface) {
            aspectRatio = new Size(16, 9);
            photoMaxWidth = 1280;
            wantedWidth = 1280;
            photoMaxHeight = 720;
            wantedHeight = 720;
        } else if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
            aspectRatio = new Size(4, 3);
            wantedWidth = 1280;
            wantedHeight = 960;
            if (SharedConfig.getDevicePerformanceClass() == 0) {
                photoMaxWidth = 1280;
                photoMaxHeight = 960;
            } else {
                photoMaxWidth = 1920;
                photoMaxHeight = 1440;
            }
        } else {
            aspectRatio = new Size(16, 9);
            wantedWidth = 1280;
            wantedHeight = 720;
            if (SharedConfig.getDevicePerformanceClass() == 0) {
                photoMaxWidth = 1280;
                photoMaxHeight = 960;
            } else {
                photoMaxWidth = 1920;
                photoMaxHeight = 1080;
            }
        }
        this.previewSize = CameraController.chooseOptimalSize(this.info.getPreviewSizes(), wantedWidth, wantedHeight, aspectRatio);
        this.pictureSize = CameraController.chooseOptimalSize(this.info.getPictureSizes(), photoMaxWidth, photoMaxHeight, aspectRatio);
        requestLayout();
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int surfaceW, int surfaceH) {
        this.surfaceHeight = surfaceH;
        this.surfaceWidth = surfaceW;
        checkPreviewMatrix();
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        CameraGLThread cameraGLThread = this.cameraThread;
        if (cameraGLThread != null) {
            cameraGLThread.shutdown(0);
            this.cameraThread.postRunnable(new Runnable() { // from class: org.telegram.messenger.camera.CameraView$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    CameraView.this.m1251xfbe6589e();
                }
            });
        }
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
        }
        return false;
    }

    /* renamed from: lambda$onSurfaceTextureDestroyed$0$org-telegram-messenger-camera-CameraView */
    public /* synthetic */ void m1251xfbe6589e() {
        this.cameraThread = null;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        CameraSession cameraSession;
        if (!this.inited && (cameraSession = this.cameraSession) != null && cameraSession.isInitied()) {
            CameraViewDelegate cameraViewDelegate = this.delegate;
            if (cameraViewDelegate != null) {
                cameraViewDelegate.onCameraInit();
            }
            this.inited = true;
        }
    }

    public void setClipTop(int value) {
        this.clipTop = value;
    }

    public void setClipBottom(int value) {
        this.clipBottom = value;
    }

    /* renamed from: lambda$new$1$org-telegram-messenger-camera-CameraView */
    public /* synthetic */ void m1250lambda$new$1$orgtelegrammessengercameraCameraView() {
        CameraGLThread cameraThread = this.cameraThread;
        if (cameraThread == null || cameraThread.currentSession == null) {
            return;
        }
        int rotationAngle = cameraThread.currentSession.getWorldAngle();
        android.opengl.Matrix.setIdentityM(this.mMVPMatrix, 0);
        if (rotationAngle != 0) {
            android.opengl.Matrix.rotateM(this.mMVPMatrix, 0, rotationAngle, 0.0f, 0.0f, 1.0f);
        }
    }

    private void checkPreviewMatrix() {
        if (this.previewSize == null) {
            return;
        }
        int viewWidth = this.textureView.getWidth();
        int viewHeight = this.textureView.getHeight();
        Matrix matrix = new Matrix();
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            matrix.postRotate(cameraSession.getDisplayOrientation());
        }
        matrix.postScale(viewWidth / 2000.0f, viewHeight / 2000.0f);
        matrix.postTranslate(viewWidth / 2.0f, viewHeight / 2.0f);
        matrix.invert(this.matrix);
        CameraGLThread cameraGLThread = this.cameraThread;
        if (cameraGLThread != null) {
            if (!cameraGLThread.isReady()) {
                this.updateRotationMatrix.run();
            } else {
                this.cameraThread.postRunnable(this.updateRotationMatrix);
            }
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(this.focusAreaSize * coefficient).intValue();
        int left = clamp(((int) x) - (areaSize / 2), 0, getWidth() - areaSize);
        int top = clamp(((int) y) - (areaSize / 2), 0, getHeight() - areaSize);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        this.matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public void focusToPoint(int x, int y) {
        Rect focusRect = calculateTapArea(x, y, 1.0f);
        Rect meteringRect = calculateTapArea(x, y, 1.5f);
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.focusToRect(focusRect, meteringRect);
        }
        this.focusProgress = 0.0f;
        this.innerAlpha = 1.0f;
        this.outerAlpha = 1.0f;
        this.cx = x;
        this.cy = y;
        this.lastDrawTime = System.currentTimeMillis();
        invalidate();
    }

    public void setZoom(float value) {
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.setZoom(value);
        }
    }

    public void setDelegate(CameraViewDelegate cameraViewDelegate) {
        this.delegate = cameraViewDelegate;
    }

    public boolean isInited() {
        return this.inited;
    }

    public CameraSession getCameraSession() {
        return this.cameraSession;
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    @Override // android.view.View
    public Matrix getMatrix() {
        return this.txform;
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.focusProgress != 1.0f || this.innerAlpha != 0.0f || this.outerAlpha != 0.0f) {
            int baseRad = AndroidUtilities.dp(30.0f);
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastDrawTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            this.lastDrawTime = newTime;
            this.outerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.outerAlpha) * 255.0f));
            this.innerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.innerAlpha) * 127.0f));
            float interpolated = this.interpolator.getInterpolation(this.focusProgress);
            canvas.drawCircle(this.cx, this.cy, baseRad + (baseRad * (1.0f - interpolated)), this.outerPaint);
            canvas.drawCircle(this.cx, this.cy, baseRad * interpolated, this.innerPaint);
            float f = this.focusProgress;
            if (f < 1.0f) {
                float f2 = f + (((float) dt) / 200.0f);
                this.focusProgress = f2;
                if (f2 > 1.0f) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else {
                float f3 = this.innerAlpha;
                if (f3 != 0.0f) {
                    float f4 = f3 - (((float) dt) / 150.0f);
                    this.innerAlpha = f4;
                    if (f4 < 0.0f) {
                        this.innerAlpha = 0.0f;
                    }
                    invalidate();
                } else {
                    float f5 = this.outerAlpha;
                    if (f5 != 0.0f) {
                        float f6 = f5 - (((float) dt) / 150.0f);
                        this.outerAlpha = f6;
                        if (f6 < 0.0f) {
                            this.outerAlpha = 0.0f;
                        }
                        invalidate();
                    }
                }
            }
        }
        return result;
    }

    public void startTakePictureAnimation() {
        this.takePictureProgress = 0.0f;
        invalidate();
        runHaptic();
    }

    public void runHaptic() {
        long[] vibrationWaveFormDurationPattern = {0, 1};
        if (Build.VERSION.SDK_INT < 26) {
            performHapticFeedback(3, 2);
            return;
        }
        Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
        VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, -1);
        vibrator.cancel();
        vibrator.vibrate(vibrationEffect);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.flipAnimator != null) {
            canvas.drawColor(-16777216);
        }
        super.dispatchDraw(canvas);
        float f = this.takePictureProgress;
        if (f != 1.0f) {
            float f2 = f + 0.10666667f;
            this.takePictureProgress = f2;
            if (f2 > 1.0f) {
                this.takePictureProgress = 1.0f;
            } else {
                invalidate();
            }
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) ((1.0f - this.takePictureProgress) * 150.0f)));
        }
    }

    /* loaded from: classes4.dex */
    public class CameraGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private SurfaceTexture cameraSurface;
        private CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initied;
        private boolean needRecord;
        private int positionHandle;
        private boolean recording;
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_SETSESSION_MESSAGE = 3;
        private final int DO_START_RECORDING = 4;
        private final int DO_STOP_RECORDING = 5;
        private Integer cameraId = 0;
        final int[] array = new int[1];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CameraGLThread(SurfaceTexture surface) {
            super("CameraGLThread");
            CameraView.this = this$0;
            this.surfaceTexture = surface;
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView start init gl");
            }
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                this.eglDisplay = null;
                finish();
                return false;
            }
            int[] version = new int[2];
            if (!this.egl10.eglInitialize(this.eglDisplay, version)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            int[] configSpec = {12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344};
            if (!this.egl10.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
                this.eglConfig = eGLConfig;
                int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                    this.eglContext = null;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture = this.surfaceTexture;
                if (surfaceTexture != null) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture, null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    EGL10 egl102 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    this.eglContext.getGL();
                    android.opengl.Matrix.setIdentityM(CameraView.this.mSTMatrix, 0);
                    int vertexShader = CameraView.this.loadShader(35633, CameraView.VERTEX_SHADER);
                    int fragmentShader = CameraView.this.loadShader(35632, CameraView.FRAGMENT_SCREEN_SHADER);
                    if (vertexShader != 0 && fragmentShader != 0) {
                        int glCreateProgram = GLES20.glCreateProgram();
                        this.drawProgram = glCreateProgram;
                        GLES20.glAttachShader(glCreateProgram, vertexShader);
                        GLES20.glAttachShader(this.drawProgram, fragmentShader);
                        GLES20.glLinkProgram(this.drawProgram);
                        int[] linkStatus = new int[1];
                        GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                        if (linkStatus[0] != 0) {
                            this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                            this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                            this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                            this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("failed link shader");
                            }
                            GLES20.glDeleteProgram(this.drawProgram);
                            this.drawProgram = 0;
                        }
                        GLES20.glGenTextures(1, CameraView.this.cameraTexture, 0);
                        GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                        GLES20.glTexParameteri(36197, 10241, 9729);
                        GLES20.glTexParameteri(36197, 10240, 9729);
                        GLES20.glTexParameteri(36197, 10242, 33071);
                        GLES20.glTexParameteri(36197, 10243, 33071);
                        android.opengl.Matrix.setIdentityM(CameraView.this.mMVPMatrix, 0);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("gl initied");
                        }
                        float[] verticesData = {-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                        float[] texData = {0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f + 0.5f, 0.5f + 0.5f};
                        CameraView.this.vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        CameraView.this.vertexBuffer.put(verticesData).position(0);
                        CameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        CameraView.this.textureBuffer.put(texData).position(0);
                        SurfaceTexture surfaceTexture2 = new SurfaceTexture(CameraView.this.cameraTexture[0]);
                        this.cameraSurface = surfaceTexture2;
                        surfaceTexture2.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() { // from class: org.telegram.messenger.camera.CameraView$CameraGLThread$$ExternalSyntheticLambda1
                            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                            public final void onFrameAvailable(SurfaceTexture surfaceTexture3) {
                                CameraView.CameraGLThread.this.m1253xf735937d(surfaceTexture3);
                            }
                        });
                        CameraView.this.createCamera(this.cameraSurface);
                        return true;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("failed creating shader");
                    }
                    finish();
                    return false;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        /* renamed from: lambda$initGL$0$org-telegram-messenger-camera-CameraView$CameraGLThread */
        public /* synthetic */ void m1253xf735937d(SurfaceTexture surfaceTexture) {
            requestRender();
        }

        public void reinitForNewCamera() {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2, Integer.valueOf(CameraView.this.info.cameraId)), 0);
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw(Integer cameraId, boolean updateTexImage) {
            boolean shouldRenderFrame;
            if (!this.initied) {
                return;
            }
            if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                EGL10 egl10 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        return;
                    }
                    return;
                }
            }
            if (updateTexImage) {
                try {
                    this.cameraSurface.updateTexImage();
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            synchronized (CameraView.this.layoutLock) {
                if (CameraView.this.fpsLimit <= 0) {
                    shouldRenderFrame = true;
                } else {
                    long currentTimeNs = System.nanoTime();
                    if (currentTimeNs < CameraView.this.nextFrameTimeNs) {
                        shouldRenderFrame = false;
                    } else {
                        CameraView.this.nextFrameTimeNs += TimeUnit.SECONDS.toNanos(1L) / CameraView.this.fpsLimit;
                        CameraView cameraView = CameraView.this;
                        cameraView.nextFrameTimeNs = Math.max(cameraView.nextFrameTimeNs, currentTimeNs);
                        shouldRenderFrame = true;
                    }
                }
            }
            CameraSession cameraSession = this.currentSession;
            if (cameraSession == null || cameraSession.cameraInfo.cameraId != cameraId.intValue()) {
                return;
            }
            if (this.recording && CameraView.this.videoEncoder != null) {
                CameraView.this.videoEncoder.frameAvailable(this.cameraSurface, cameraId, System.nanoTime());
            }
            if (shouldRenderFrame) {
                this.cameraSurface.getTransformMatrix(CameraView.this.mSTMatrix);
                this.egl10.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, this.array);
                int[] iArr = this.array;
                int drawnWidth = iArr[0];
                this.egl10.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, iArr);
                int drawnHeight = this.array[0];
                GLES20.glViewport(0, 0, drawnWidth, drawnHeight);
                GLES20.glUseProgram(this.drawProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, (Buffer) CameraView.this.vertexBuffer);
                GLES20.glEnableVertexAttribArray(this.positionHandle);
                GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, (Buffer) CameraView.this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.textureHandle);
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, CameraView.this.mSTMatrix, 0);
                GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, CameraView.this.mMVPMatrix, 0);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glDisableVertexAttribArray(this.positionHandle);
                GLES20.glDisableVertexAttribArray(this.textureHandle);
                GLES20.glBindTexture(36197, 0);
                GLES20.glUseProgram(0);
                this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
                synchronized (CameraView.this.layoutLock) {
                    if (!CameraView.this.firstFrameRendered) {
                        CameraView.this.firstFrameRendered = true;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraView$CameraGLThread$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                CameraView.CameraGLThread.this.m1254x9c0a5ccc();
                            }
                        });
                    }
                }
            }
        }

        /* renamed from: lambda$onDraw$1$org-telegram-messenger-camera-CameraView$CameraGLThread */
        public /* synthetic */ void m1254x9c0a5ccc() {
            CameraView.this.onFirstFrameRendered();
        }

        @Override // org.telegram.messenger.DispatchQueue, java.lang.Thread, java.lang.Runnable
        public void run() {
            this.initied = initGL();
            super.run();
        }

        @Override // org.telegram.messenger.DispatchQueue
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            switch (what) {
                case 0:
                    onDraw((Integer) inputMessage.obj, true);
                    return;
                case 1:
                    finish();
                    if (this.recording) {
                        CameraView.this.videoEncoder.stopRecording(inputMessage.arg1);
                    }
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                        return;
                    }
                    return;
                case 2:
                    EGL10 egl10 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("CameraView eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            return;
                        }
                        return;
                    }
                    SurfaceTexture surfaceTexture = this.cameraSurface;
                    if (surfaceTexture != null) {
                        surfaceTexture.getTransformMatrix(CameraView.this.moldSTMatrix);
                        this.cameraSurface.setOnFrameAvailableListener(null);
                        this.cameraSurface.release();
                    }
                    this.cameraId = (Integer) inputMessage.obj;
                    GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9729);
                    GLES20.glTexParameteri(36197, 10240, 9729);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    SurfaceTexture surfaceTexture2 = new SurfaceTexture(CameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture2;
                    surfaceTexture2.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() { // from class: org.telegram.messenger.camera.CameraView$CameraGLThread$$ExternalSyntheticLambda0
                        @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                        public final void onFrameAvailable(SurfaceTexture surfaceTexture3) {
                            CameraView.CameraGLThread.this.m1252xd2064eaf(surfaceTexture3);
                        }
                    });
                    CameraView.this.createCamera(this.cameraSurface);
                    return;
                case 3:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("CameraView set gl renderer session");
                    }
                    CameraSession newSession = (CameraSession) inputMessage.obj;
                    if (this.currentSession != newSession) {
                        this.currentSession = newSession;
                        this.cameraId = Integer.valueOf(newSession.cameraInfo.cameraId);
                    }
                    this.currentSession.updateRotation();
                    int rotationAngle = this.currentSession.getWorldAngle();
                    android.opengl.Matrix.setIdentityM(CameraView.this.mMVPMatrix, 0);
                    if (rotationAngle != 0) {
                        android.opengl.Matrix.rotateM(CameraView.this.mMVPMatrix, 0, rotationAngle, 0.0f, 0.0f, 1.0f);
                        return;
                    }
                    return;
                case 4:
                    if (!this.initied) {
                        return;
                    }
                    CameraView.this.recordFile = (File) inputMessage.obj;
                    CameraView.this.videoEncoder = new VideoRecorder();
                    this.recording = true;
                    CameraView.this.videoEncoder.startRecording(CameraView.this.recordFile, EGL14.eglGetCurrentContext());
                    return;
                case 5:
                    if (CameraView.this.videoEncoder != null) {
                        CameraView.this.videoEncoder.stopRecording(0);
                        CameraView.this.videoEncoder = null;
                    }
                    this.recording = false;
                    return;
                default:
                    return;
            }
        }

        /* renamed from: lambda$handleMessage$2$org-telegram-messenger-camera-CameraView$CameraGLThread */
        public /* synthetic */ void m1252xd2064eaf(SurfaceTexture surfaceTexture) {
            requestRender();
        }

        public void shutdown(int send) {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, send, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }

        public boolean startRecording(File path) {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(4, path), 0);
                return false;
            }
            return true;
        }

        public void stopRecording() {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(5), 0);
            }
        }
    }

    public void onFirstFrameRendered() {
        if (this.blurredStubView.getVisibility() == 0) {
            this.blurredStubView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.messenger.camera.CameraView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    CameraView.this.blurredStubView.setVisibility(8);
                }
            }).start();
        }
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(GLES20.glGetShaderInfoLog(shader));
            }
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    public void createCamera(final SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                CameraView.this.m1249lambda$createCamera$4$orgtelegrammessengercameraCameraView(surfaceTexture);
            }
        });
    }

    /* renamed from: lambda$createCamera$4$org-telegram-messenger-camera-CameraView */
    public /* synthetic */ void m1249lambda$createCamera$4$orgtelegrammessengercameraCameraView(SurfaceTexture surfaceTexture) {
        if (this.cameraThread == null) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("CameraView create camera session");
        }
        if (this.previewSize == null) {
            updateCameraInfoSize();
        }
        Size size = this.previewSize;
        if (size == null) {
            return;
        }
        surfaceTexture.setDefaultBufferSize(size.getWidth(), this.previewSize.getHeight());
        CameraSession cameraSession = new CameraSession(this.info, this.previewSize, this.pictureSize, 256, false);
        this.cameraSession = cameraSession;
        this.cameraThread.setCurrentSession(cameraSession);
        requestLayout();
        CameraController.getInstance().open(this.cameraSession, surfaceTexture, new Runnable() { // from class: org.telegram.messenger.camera.CameraView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CameraView.this.m1247lambda$createCamera$2$orgtelegrammessengercameraCameraView();
            }
        }, new Runnable() { // from class: org.telegram.messenger.camera.CameraView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CameraView.this.m1248lambda$createCamera$3$orgtelegrammessengercameraCameraView();
            }
        });
    }

    /* renamed from: lambda$createCamera$2$org-telegram-messenger-camera-CameraView */
    public /* synthetic */ void m1247lambda$createCamera$2$orgtelegrammessengercameraCameraView() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView camera initied");
            }
            this.cameraSession.setInitied();
            requestLayout();
        }
    }

    /* renamed from: lambda$createCamera$3$org-telegram-messenger-camera-CameraView */
    public /* synthetic */ void m1248lambda$createCamera$3$orgtelegrammessengercameraCameraView() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    /* loaded from: classes4.dex */
    public class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private MediaCodec.BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        private AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        private ArrayBlockingQueue<InstantCameraView.AudioBufferInfo> buffers;
        private ArrayList<InstantCameraView.AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private android.opengl.EGLConfig eglConfig;
        private android.opengl.EGLContext eglContext;
        private android.opengl.EGLDisplay eglDisplay;
        private android.opengl.EGLSurface eglSurface;
        private boolean firstEncode;
        private int frameCount;
        private DispatchQueue generateKeyframeThumbsQueue;
        private volatile EncoderHandler handler;
        private ArrayList<Bitmap> keyframeThumbs;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private int prependHeaderSize;
        private boolean ready;
        private Runnable recorderRunnable;
        private volatile boolean running;
        private volatile int sendWhenDone;
        private android.opengl.EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private FloatBuffer textureBuffer;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private MediaCodec.BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private File videoFile;
        private long videoFirst;
        private int videoHeight;
        private long videoLast;
        private int videoTrackIndex;
        private int videoWidth;
        private int zeroTimeStamps;

        private VideoRecorder() {
            CameraView.this = r5;
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList<>();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1L;
            this.currentTimestamp = 0L;
            this.lastTimestamp = -1L;
            this.sync = new Object();
            this.videoFirst = -1L;
            this.audioFirst = -1L;
            this.lastCameraId = 0;
            this.buffers = new ArrayBlockingQueue<>(10);
            this.keyframeThumbs = new ArrayList<>();
            this.recorderRunnable = new Runnable() { // from class: org.telegram.messenger.camera.CameraView.VideoRecorder.1
                /* JADX WARN: Code restructure failed: missing block: B:13:0x002d, code lost:
                    if (org.telegram.messenger.camera.CameraView.VideoRecorder.this.sendWhenDone == 0) goto L51;
                 */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public void run() {
                    /*
                        Method dump skipped, instructions count: 266
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.AnonymousClass1.run():void");
                }
            };
        }

        public void startRecording(File outputFile, android.opengl.EGLContext sharedContext) {
            int bitrate;
            String model = Build.DEVICE;
            if (model == null) {
            }
            Size pictureSize = CameraView.this.previewSize;
            if (Math.min(pictureSize.mHeight, pictureSize.mWidth) >= 720) {
                bitrate = 3500000;
            } else {
                bitrate = 1800000;
            }
            this.videoFile = outputFile;
            if (CameraView.this.cameraSession.getWorldAngle() == 90 || CameraView.this.cameraSession.getWorldAngle() == 270) {
                this.videoWidth = pictureSize.getWidth();
                this.videoHeight = pictureSize.getHeight();
            } else {
                this.videoWidth = pictureSize.getHeight();
                this.videoHeight = pictureSize.getWidth();
            }
            this.videoBitrate = bitrate;
            this.sharedEglContext = sharedContext;
            synchronized (this.sync) {
                if (this.running) {
                    return;
                }
                this.running = true;
                Thread thread = new Thread(this, "TextureMovieEncoder");
                thread.setPriority(10);
                thread.start();
                while (!this.ready) {
                    try {
                        this.sync.wait();
                    } catch (InterruptedException e) {
                    }
                }
                this.keyframeThumbs.clear();
                this.frameCount = 0;
                DispatchQueue dispatchQueue = this.generateKeyframeThumbsQueue;
                if (dispatchQueue != null) {
                    dispatchQueue.cleanupQueue();
                    this.generateKeyframeThumbsQueue.recycle();
                }
                this.generateKeyframeThumbsQueue = new DispatchQueue("keyframes_thumb_queque");
                this.handler.sendMessage(this.handler.obtainMessage(0));
            }
        }

        public void stopRecording(int send) {
            this.handler.sendMessage(this.handler.obtainMessage(1, send, 0));
        }

        public void frameAvailable(SurfaceTexture st, Integer cameraId, long timestampInternal) {
            synchronized (this.sync) {
                if (!this.ready) {
                    return;
                }
                long timestamp = st.getTimestamp();
                if (timestamp == 0) {
                    int i = this.zeroTimeStamps + 1;
                    this.zeroTimeStamps = i;
                    if (i > 1) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("CameraView fix timestamp enabled");
                        }
                        timestamp = timestampInternal;
                    } else {
                        return;
                    }
                } else {
                    this.zeroTimeStamps = 0;
                }
                this.handler.sendMessage(this.handler.obtainMessage(2, (int) (timestamp >> 32), (int) timestamp, cameraId));
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (this.sync) {
                this.handler = new EncoderHandler(this);
                this.ready = true;
                this.sync.notify();
            }
            Looper.loop();
            synchronized (this.sync) {
                this.ready = false;
            }
        }

        public void handleAudioFrameAvailable(InstantCameraView.AudioBufferInfo input) {
            ByteBuffer inputBuffer;
            if (this.audioStopedByTime) {
                return;
            }
            InstantCameraView.AudioBufferInfo input2 = input;
            this.buffersToWrite.add(input2);
            if (this.audioFirst == -1) {
                if (this.videoFirst != -1) {
                    while (true) {
                        boolean ok = false;
                        int a = 0;
                        while (true) {
                            if (a >= input2.results) {
                                break;
                            } else if (a != 0 || Math.abs(this.videoFirst - input2.offset[a]) <= 10000000) {
                                if (input2.offset[a] >= this.videoFirst) {
                                    input2.lastWroteBuffer = a;
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("CameraView found first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("CameraView ignore first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                    a++;
                                }
                            } else {
                                this.desyncTime = this.videoFirst - input2.offset[a];
                                this.audioFirst = input2.offset[a];
                                ok = true;
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("CameraView detected desync between audio and video " + this.desyncTime);
                                }
                            }
                        }
                        if (ok) {
                            break;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("CameraView first audio frame not found, removing buffers " + input2.results);
                        }
                        this.buffersToWrite.remove(input2);
                        if (!this.buffersToWrite.isEmpty()) {
                            InstantCameraView.AudioBufferInfo input3 = this.buffersToWrite.get(0);
                            input2 = input3;
                        } else {
                            return;
                        }
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView video record not yet started");
                    return;
                } else {
                    return;
                }
            }
            if (this.audioStartTime == -1) {
                this.audioStartTime = input2.offset[input2.lastWroteBuffer];
            }
            if (this.buffersToWrite.size() > 1) {
                InstantCameraView.AudioBufferInfo input4 = this.buffersToWrite.get(0);
                input2 = input4;
            }
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            boolean isLast = false;
            while (input2 != null) {
                try {
                    int inputBufferIndex = this.audioEncoder.dequeueInputBuffer(0L);
                    if (inputBufferIndex >= 0) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            inputBuffer = this.audioEncoder.getInputBuffer(inputBufferIndex);
                        } else {
                            ByteBuffer[] inputBuffers = this.audioEncoder.getInputBuffers();
                            ByteBuffer inputBuffer2 = inputBuffers[inputBufferIndex];
                            inputBuffer2.clear();
                            inputBuffer = inputBuffer2;
                        }
                        long startWriteTime = input2.offset[input2.lastWroteBuffer];
                        int a2 = input2.lastWroteBuffer;
                        while (true) {
                            if (a2 > input2.results) {
                                break;
                            }
                            if (a2 < input2.results) {
                                if (!this.running && input2.offset[a2] >= this.videoLast - this.desyncTime) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("CameraView stop audio encoding because of stoped video recording at " + input2.offset[a2] + " last video " + this.videoLast);
                                    }
                                    this.audioStopedByTime = true;
                                    isLast = true;
                                    input2 = null;
                                    this.buffersToWrite.clear();
                                } else if (inputBuffer.remaining() < input2.read[a2]) {
                                    input2.lastWroteBuffer = a2;
                                    input2 = null;
                                    break;
                                } else {
                                    inputBuffer.put(input2.buffer[a2]);
                                }
                            }
                            if (a2 >= input2.results - 1) {
                                this.buffersToWrite.remove(input2);
                                if (this.running) {
                                    this.buffers.put(input2);
                                }
                                if (!this.buffersToWrite.isEmpty()) {
                                    input2 = this.buffersToWrite.get(0);
                                } else {
                                    isLast = input2.last;
                                    input2 = null;
                                    break;
                                }
                            }
                            a2++;
                        }
                        MediaCodec mediaCodec = this.audioEncoder;
                        int position = inputBuffer.position();
                        long j = 0;
                        if (startWriteTime != 0) {
                            j = startWriteTime - this.audioStartTime;
                        }
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, position, j, isLast ? 4 : 0);
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                    return;
                }
            }
        }

        public void handleVideoFrameAvailable(long timestampNanos, Integer cameraId) {
            long dt;
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!this.lastCameraId.equals(cameraId)) {
                this.lastTimestamp = -1L;
                this.lastCameraId = cameraId;
            }
            long dt2 = this.lastTimestamp;
            if (dt2 == -1) {
                this.lastTimestamp = timestampNanos;
                if (this.currentTimestamp != 0) {
                    dt = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000;
                } else {
                    dt = 0;
                }
            } else {
                dt = timestampNanos - dt2;
                this.lastTimestamp = timestampNanos;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                long j = this.skippedTime + dt;
                this.skippedTime = j;
                if (j < 200000000) {
                    return;
                }
                this.skippedFirst = true;
            }
            this.currentTimestamp += dt;
            if (this.videoFirst == -1) {
                this.videoFirst = timestampNanos / 1000;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView first video frame was at " + this.videoFirst);
                }
            }
            this.videoLast = timestampNanos;
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, (Buffer) CameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, (Buffer) this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, CameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            if (CameraView.this.oldCameraTexture[0] != 0) {
                if (!this.blendEnabled) {
                    GLES20.glEnable(3042);
                    this.blendEnabled = true;
                }
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, CameraView.this.moldSTMatrix, 0);
                GLES20.glBindTexture(36197, CameraView.this.oldCameraTexture[0]);
                GLES20.glDrawArrays(5, 0, 4);
            }
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, CameraView.this.mSTMatrix, 0);
            GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
            EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
        }

        public void handleStopRecording(int send) {
            if (this.running) {
                this.sendWhenDone = send;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
            MediaCodec mediaCodec = this.videoEncoder;
            if (mediaCodec != null) {
                try {
                    mediaCodec.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            MediaCodec mediaCodec2 = this.audioEncoder;
            if (mediaCodec2 != null) {
                try {
                    mediaCodec2.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
            MP4Builder mP4Builder = this.mediaMuxer;
            if (mP4Builder != null) {
                try {
                    mP4Builder.finishMovie();
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Surface surface = this.surface;
            if (surface != null) {
                surface.release();
                this.surface = null;
            }
            if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(this.eglDisplay);
            }
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglConfig = null;
            this.handler.exit();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraView$VideoRecorder$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CameraView.VideoRecorder.this.m1255x81855938();
                }
            });
        }

        /* renamed from: lambda$handleStopRecording$0$org-telegram-messenger-camera-CameraView$VideoRecorder */
        public /* synthetic */ void m1255x81855938() {
            CameraView.this.cameraSession.stopVideoRecording();
            CameraView.this.onRecordingFinishRunnable.run();
        }

        public void prepareEncoder() {
            try {
                int recordBufferSize = AudioRecord.getMinBufferSize(CameraView.audioSampleRate, 16, 2);
                if (recordBufferSize <= 0) {
                    recordBufferSize = 3584;
                }
                int bufferSize = 49152;
                if (49152 < recordBufferSize) {
                    bufferSize = ((recordBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int a = 0; a < 3; a++) {
                    this.buffers.add(new InstantCameraView.AudioBufferInfo());
                }
                AudioRecord audioRecord = new AudioRecord(0, CameraView.audioSampleRate, 16, 2, bufferSize);
                this.audioRecorder = audioRecord;
                audioRecord.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + bufferSize);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("sample-rate", CameraView.audioSampleRate);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", 32000);
                audioFormat.setInteger("max-input-size", CacheDataSink.DEFAULT_BUFFER_SIZE);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(audioFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.firstEncode = true;
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", 2130708361);
                format.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(format, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie, false);
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("EGL already set up");
                }
                android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                this.eglDisplay = eglGetDisplay;
                if (eglGetDisplay == EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("unable to get EGL14 display");
                }
                int[] version = new int[2];
                if (!EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                    this.eglDisplay = null;
                    throw new RuntimeException("unable to initialize EGL14");
                }
                if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                    int[] attribList = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, EglBase.EGL_RECORDABLE_ANDROID, 1, 12344};
                    android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
                    int[] numConfigs = new int[1];
                    if (!EGL14.eglChooseConfig(this.eglDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
                        throw new RuntimeException("Unable to find a suitable EGLConfig");
                    }
                    int[] attrib2_list = {12440, 2, 12344};
                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, attrib2_list, 0);
                    this.eglConfig = configs[0];
                }
                int[] values = new int[1];
                EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, values, 0);
                if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                    int[] surfaceAttribs = {12344};
                    android.opengl.EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, surfaceAttribs, 0);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface != null) {
                        if (!EGL14.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                            }
                            throw new RuntimeException("eglMakeCurrent failed");
                        }
                        GLES20.glBlendFunc(770, 771);
                        float[] texData = {0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f + 0.5f, 0.5f + 0.5f};
                        FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        this.textureBuffer = asFloatBuffer;
                        asFloatBuffer.put(texData).position(0);
                        int vertexShader = CameraView.this.loadShader(35633, CameraView.VERTEX_SHADER);
                        int fragmentShader = CameraView.this.loadShader(35632, CameraView.FRAGMENT_SCREEN_SHADER);
                        if (vertexShader != 0 && fragmentShader != 0) {
                            int glCreateProgram = GLES20.glCreateProgram();
                            this.drawProgram = glCreateProgram;
                            GLES20.glAttachShader(glCreateProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] != 0) {
                                this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                                return;
                            }
                            GLES20.glDeleteProgram(this.drawProgram);
                            this.drawProgram = 0;
                            return;
                        }
                        return;
                    }
                    throw new RuntimeException("surface was null");
                }
                throw new IllegalStateException("surface already created");
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            ByteBuffer encodedData;
            ByteBuffer encodedData2;
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = null;
            int i = 21;
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000L);
                byte b = 1;
                if (encoderStatus == -1) {
                    if (!endOfStream) {
                        break;
                    }
                    i = 21;
                } else {
                    if (encoderStatus == -3) {
                        if (Build.VERSION.SDK_INT < i) {
                            encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                        }
                    } else if (encoderStatus == -2) {
                        MediaFormat newFormat = this.videoEncoder.getOutputFormat();
                        if (this.videoTrackIndex == -5) {
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                            if (newFormat.containsKey("prepend-sps-pps-to-idr-frames") && newFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                                ByteBuffer spsBuff = newFormat.getByteBuffer("csd-0");
                                ByteBuffer ppsBuff = newFormat.getByteBuffer("csd-1");
                                this.prependHeaderSize = spsBuff.limit() + ppsBuff.limit();
                            }
                        }
                    } else if (encoderStatus < 0) {
                        continue;
                    } else {
                        if (Build.VERSION.SDK_INT < i) {
                            encodedData2 = encoderOutputBuffers[encoderStatus];
                        } else {
                            encodedData2 = this.videoEncoder.getOutputBuffer(encoderStatus);
                        }
                        if (encodedData2 == null) {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                        }
                        if (this.videoBufferInfo.size > 1) {
                            if ((this.videoBufferInfo.flags & 2) == 0) {
                                if (this.prependHeaderSize != 0 && (this.videoBufferInfo.flags & 1) != 0) {
                                    this.videoBufferInfo.offset += this.prependHeaderSize;
                                    this.videoBufferInfo.size -= this.prependHeaderSize;
                                }
                                if (this.firstEncode && (this.videoBufferInfo.flags & 1) != 0) {
                                    if (this.videoBufferInfo.size > 100) {
                                        encodedData2.position(this.videoBufferInfo.offset);
                                        byte[] temp = new byte[100];
                                        encodedData2.get(temp);
                                        int nalCount = 0;
                                        int a = 0;
                                        while (true) {
                                            if (a < temp.length - 4) {
                                                if (temp[a] != 0 || temp[a + 1] != 0 || temp[a + 2] != 0 || temp[a + 3] != 1 || (nalCount = nalCount + 1) <= 1) {
                                                    a++;
                                                } else {
                                                    this.videoBufferInfo.offset += a;
                                                    this.videoBufferInfo.size -= a;
                                                    break;
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    this.firstEncode = false;
                                }
                                this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData2, this.videoBufferInfo, true);
                            } else if (this.videoTrackIndex == -5) {
                                byte[] csd = new byte[this.videoBufferInfo.size];
                                encodedData2.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                                encodedData2.position(this.videoBufferInfo.offset);
                                encodedData2.get(csd);
                                ByteBuffer sps = null;
                                ByteBuffer pps = null;
                                int a2 = this.videoBufferInfo.size - 1;
                                while (true) {
                                    if (a2 < 0 || a2 <= 3) {
                                        break;
                                    } else if (csd[a2] != b || csd[a2 - 1] != 0 || csd[a2 - 2] != 0 || csd[a2 - 3] != 0) {
                                        a2--;
                                        b = 1;
                                    } else {
                                        sps = ByteBuffer.allocate(a2 - 3);
                                        pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a2 - 3));
                                        sps.put(csd, 0, a2 - 3).position(0);
                                        pps.put(csd, a2 - 3, this.videoBufferInfo.size - (a2 - 3)).position(0);
                                        break;
                                    }
                                }
                                int a3 = this.videoWidth;
                                MediaFormat newFormat2 = MediaFormat.createVideoFormat("video/avc", a3, this.videoHeight);
                                if (sps != null && pps != null) {
                                    newFormat2.setByteBuffer("csd-0", sps);
                                    newFormat2.setByteBuffer("csd-1", pps);
                                }
                                this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat2, false);
                            }
                        }
                        this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                        if ((this.videoBufferInfo.flags & 4) != 0) {
                            break;
                        }
                    }
                    i = 21;
                }
            }
            if (Build.VERSION.SDK_INT < i) {
                encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0L);
                if (encoderStatus2 == -1) {
                    if (endOfStream) {
                        if (!this.running && this.sendWhenDone == 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else if (encoderStatus2 == -3) {
                    if (Build.VERSION.SDK_INT < i) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus2 == -2) {
                    MediaFormat newFormat3 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat3, true);
                    }
                } else if (encoderStatus2 < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < i) {
                        encodedData = encoderOutputBuffers[encoderStatus2];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus2);
                    }
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus2 + " was null");
                    }
                    if ((this.audioBufferInfo.flags & 2) != 0) {
                        this.audioBufferInfo.size = 0;
                    }
                    if (this.audioBufferInfo.size != 0) {
                        this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false);
                    }
                    this.audioEncoder.releaseOutputBuffer(encoderStatus2, false);
                    if ((this.audioBufferInfo.flags & 4) != 0) {
                        return;
                    }
                }
                i = 21;
            }
        }

        protected void finalize() throws Throwable {
            try {
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(this.eglDisplay);
                    this.eglDisplay = EGL14.EGL_NO_DISPLAY;
                    this.eglContext = EGL14.EGL_NO_CONTEXT;
                    this.eglConfig = null;
                }
            } finally {
                super.finalize();
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference<>(encoder);
        }

        @Override // android.os.Handler
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            VideoRecorder encoder = this.mWeakEncoder.get();
            if (encoder == null) {
                return;
            }
            switch (what) {
                case 0:
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("start encoder");
                        }
                        encoder.prepareEncoder();
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        encoder.handleStopRecording(0);
                        Looper.myLooper().quit();
                        return;
                    }
                case 1:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stop encoder");
                    }
                    encoder.handleStopRecording(inputMessage.arg1);
                    return;
                case 2:
                    long timestamp = (inputMessage.arg1 << 32) | (inputMessage.arg2 & 4294967295L);
                    Integer cameraId = (Integer) inputMessage.obj;
                    encoder.handleVideoFrameAvailable(timestamp, cameraId);
                    return;
                case 3:
                    encoder.handleAudioFrameAvailable((InstantCameraView.AudioBufferInfo) inputMessage.obj);
                    return;
                default:
                    return;
            }
        }

        public void exit() {
            Looper.myLooper().quit();
        }
    }

    public void setFpsLimit(int fpsLimit) {
        this.fpsLimit = fpsLimit;
    }
}
