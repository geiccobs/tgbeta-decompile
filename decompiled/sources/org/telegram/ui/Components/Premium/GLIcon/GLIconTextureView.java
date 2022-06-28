package org.telegram.ui.Components.Premium.GLIcon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.ArrayList;
import java.util.Collections;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.StarParticlesView;
/* loaded from: classes5.dex */
public class GLIconTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    int animationPointer;
    boolean attached;
    ValueAnimator backAnimation;
    private EGLConfig eglConfig;
    GestureDetector gestureDetector;
    private EGL10 mEgl;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private GL10 mGl;
    public GLIconRenderer mRenderer;
    private SurfaceTexture mSurface;
    StarParticlesView starParticlesView;
    private int surfaceHeight;
    private int surfaceWidth;
    private int targetFps;
    private int targetFrameDurationMillis;
    private RenderThread thread;
    public boolean touched;
    public boolean isRunning = false;
    private boolean paused = true;
    private boolean rendererChanged = false;
    private boolean dialogIsVisible = false;
    private long idleDelay = AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;
    private final int animationsCount = 5;
    ArrayList<Integer> animationIndexes = new ArrayList<>();
    AnimatorSet animatorSet = new AnimatorSet();
    Runnable idleAnimation = new Runnable() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.2
        @Override // java.lang.Runnable
        public void run() {
            if ((GLIconTextureView.this.animatorSet == null || !GLIconTextureView.this.animatorSet.isRunning()) && (GLIconTextureView.this.backAnimation == null || !GLIconTextureView.this.backAnimation.isRunning())) {
                GLIconTextureView.this.startIdleAnimation();
                return;
            }
            GLIconTextureView gLIconTextureView = GLIconTextureView.this;
            gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
        }
    };
    ValueAnimator.AnimatorUpdateListener xUpdater2 = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            GLIconTextureView.this.m2882x3ddf7f16(valueAnimator);
        }
    };
    ValueAnimator.AnimatorUpdateListener xUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView$$ExternalSyntheticLambda1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            GLIconTextureView.this.m2883xf8551f97(valueAnimator);
        }
    };
    ValueAnimator.AnimatorUpdateListener yUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView$$ExternalSyntheticLambda2
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            GLIconTextureView.this.m2884xb2cac018(valueAnimator);
        }
    };

    public GLIconTextureView(Context context, int style) {
        super(context);
        setOpaque(false);
        setRenderer(new GLIconRenderer(context, style));
        initialize(context);
        GestureDetector gestureDetector = new GestureDetector(context, new AnonymousClass1());
        this.gestureDetector = gestureDetector;
        gestureDetector.setIsLongpressEnabled(true);
        for (int i = 0; i < 5; i++) {
            this.animationIndexes.add(Integer.valueOf(i));
        }
        Collections.shuffle(this.animationIndexes);
    }

    /* renamed from: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 implements GestureDetector.OnGestureListener {
        AnonymousClass1() {
            GLIconTextureView.this = this$0;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent motionEvent) {
            if (GLIconTextureView.this.backAnimation != null) {
                GLIconTextureView.this.backAnimation.removeAllListeners();
                GLIconTextureView.this.backAnimation.cancel();
                GLIconTextureView.this.backAnimation = null;
            }
            if (GLIconTextureView.this.animatorSet != null) {
                GLIconTextureView.this.animatorSet.removeAllListeners();
                GLIconTextureView.this.animatorSet.cancel();
                GLIconTextureView.this.animatorSet = null;
            }
            AndroidUtilities.cancelRunOnUIThread(GLIconTextureView.this.idleAnimation);
            GLIconTextureView.this.touched = true;
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            float rad = GLIconTextureView.this.getMeasuredWidth() / 2.0f;
            final float toAngleX = ((Utilities.random.nextInt(30) + 40) * (rad - motionEvent.getX())) / rad;
            final float toAngleY = ((Utilities.random.nextInt(30) + 40) * (rad - motionEvent.getY())) / rad;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GLIconTextureView.AnonymousClass1.this.m2886x35462f59(toAngleX, toAngleY);
                }
            }, 16L);
            return true;
        }

        /* renamed from: lambda$onSingleTapUp$0$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView$1 */
        public /* synthetic */ void m2886x35462f59(float toAngleX, float toAngleY) {
            if (GLIconTextureView.this.backAnimation != null) {
                GLIconTextureView.this.backAnimation.removeAllListeners();
                GLIconTextureView.this.backAnimation.cancel();
                GLIconTextureView.this.backAnimation = null;
            }
            if (GLIconTextureView.this.animatorSet != null) {
                GLIconTextureView.this.animatorSet.removeAllListeners();
                GLIconTextureView.this.animatorSet.cancel();
                GLIconTextureView.this.animatorSet = null;
            }
            if (Math.abs(GLIconTextureView.this.mRenderer.angleX) > 10.0f) {
                GLIconTextureView.this.startBackAnimation();
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(GLIconTextureView.this.idleAnimation);
            GLIconTextureView.this.animatorSet = new AnimatorSet();
            ValueAnimator v1 = ValueAnimator.ofFloat(GLIconTextureView.this.mRenderer.angleX, toAngleX);
            v1.addUpdateListener(GLIconTextureView.this.xUpdater);
            v1.setDuration(220);
            v1.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ValueAnimator v2 = ValueAnimator.ofFloat(toAngleX, 0.0f);
            v2.addUpdateListener(GLIconTextureView.this.xUpdater);
            v2.setStartDelay(220);
            v2.setDuration(600L);
            v2.setInterpolator(AndroidUtilities.overshootInterpolator);
            ValueAnimator v3 = ValueAnimator.ofFloat(GLIconTextureView.this.mRenderer.angleY, toAngleY);
            v3.addUpdateListener(GLIconTextureView.this.yUpdater);
            v3.setDuration(220);
            v3.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ValueAnimator v4 = ValueAnimator.ofFloat(toAngleY, 0.0f);
            v4.addUpdateListener(GLIconTextureView.this.yUpdater);
            v4.setStartDelay(220);
            v4.setDuration(600L);
            v4.setInterpolator(AndroidUtilities.overshootInterpolator);
            GLIconTextureView.this.animatorSet.playTogether(v1, v2, v3, v4);
            GLIconTextureView.this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.1.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GLIconTextureView.this.mRenderer.angleX = 0.0f;
                    GLIconTextureView.this.animatorSet = null;
                    GLIconTextureView.this.scheduleIdleAnimation(GLIconTextureView.this.idleDelay);
                }
            });
            GLIconTextureView.this.animatorSet.start();
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            GLIconTextureView.this.mRenderer.angleX += 0.5f * v;
            GLIconTextureView.this.mRenderer.angleY += 0.05f * v1;
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent motionEvent) {
            GLIconTextureView.this.onLongPress();
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    public void onLongPress() {
    }

    public synchronized void setRenderer(GLIconRenderer renderer) {
        this.mRenderer = renderer;
        this.rendererChanged = true;
    }

    private void initialize(Context context) {
        this.targetFps = (int) AndroidUtilities.screenRefreshRate;
        setSurfaceTextureListener(this);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startThread(surface, width, height);
    }

    public void startThread(SurfaceTexture surface, int width, int height) {
        this.thread = new RenderThread(this, null);
        this.mSurface = surface;
        setDimensions(width, height);
        this.targetFrameDurationMillis = Math.max(0, ((int) ((1.0f / this.targetFps) * 1000.0f)) - 1);
        this.thread.start();
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        setDimensions(width, height);
        GLIconRenderer gLIconRenderer = this.mRenderer;
        if (gLIconRenderer != null) {
            gLIconRenderer.onSurfaceChanged(this.mGl, width, height);
        }
    }

    public synchronized void setPaused(boolean isPaused) {
        this.paused = isPaused;
    }

    public synchronized boolean isPaused() {
        return this.paused;
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopThread();
        return false;
    }

    public void stopThread() {
        RenderThread renderThread = this.thread;
        if (renderThread != null) {
            this.isRunning = false;
            try {
                renderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.thread = null;
        }
    }

    public boolean shouldSleep() {
        return isPaused() || this.mRenderer == null;
    }

    public void setBackgroundBitmap(Bitmap gradientTextureBitmap) {
        this.mRenderer.setBackground(gradientTextureBitmap);
    }

    /* loaded from: classes5.dex */
    public class RenderThread extends Thread {
        private RenderThread() {
            GLIconTextureView.this = r1;
        }

        /* synthetic */ RenderThread(GLIconTextureView x0, AnonymousClass1 x1) {
            this();
        }

        /* JADX WARN: Incorrect condition in loop: B:20:0x0068 */
        @Override // java.lang.Thread, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                r9 = this;
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r0 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                r1 = 1
                r0.isRunning = r1
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r0 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$400(r0)
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r0 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$500(r0)
                long r0 = java.lang.System.currentTimeMillis()
            L13:
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                boolean r2 = r2.isRunning
                if (r2 == 0) goto L74
            L19:
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer r2 = r2.mRenderer
                r3 = 100
                if (r2 != 0) goto L27
                java.lang.Thread.sleep(r3)     // Catch: java.lang.InterruptedException -> L25
            L24:
                goto L19
            L25:
                r2 = move-exception
                goto L24
            L27:
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                boolean r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$600(r2)
                if (r2 == 0) goto L3c
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer r5 = r2.mRenderer
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$700(r2, r5)
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                r5 = 0
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$602(r2, r5)
            L3c:
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                boolean r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$800(r2)
                if (r2 != 0) goto L4d
                long r0 = java.lang.System.currentTimeMillis()
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$900(r2)
            L4d:
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this     // Catch: java.lang.InterruptedException -> L72
                boolean r2 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$800(r2)     // Catch: java.lang.InterruptedException -> L72
                if (r2 == 0) goto L59
                java.lang.Thread.sleep(r3)     // Catch: java.lang.InterruptedException -> L72
                goto L73
            L59:
                long r2 = java.lang.System.currentTimeMillis()     // Catch: java.lang.InterruptedException -> L72
                long r4 = r2 - r0
            L5f:
                org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView r6 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.this     // Catch: java.lang.InterruptedException -> L72
                int r6 = org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.access$1000(r6)     // Catch: java.lang.InterruptedException -> L72
                long r6 = (long) r6     // Catch: java.lang.InterruptedException -> L72
                int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r8 >= 0) goto L73
                long r6 = java.lang.System.currentTimeMillis()     // Catch: java.lang.InterruptedException -> L72
                r2 = r6
                long r4 = r2 - r0
                goto L5f
            L72:
                r2 = move-exception
            L73:
                goto L13
            L74:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.RenderThread.run():void");
        }
    }

    public synchronized void initializeRenderer(GLIconRenderer renderer) {
        if (renderer != null) {
            if (this.isRunning) {
                renderer.onSurfaceCreated(this.mGl, this.eglConfig);
                renderer.onSurfaceChanged(this.mGl, this.surfaceWidth, this.surfaceHeight);
            }
        }
    }

    public synchronized void drawSingleFrame() {
        checkCurrent();
        GLIconRenderer gLIconRenderer = this.mRenderer;
        if (gLIconRenderer != null) {
            gLIconRenderer.onDrawFrame(this.mGl);
        }
        checkGlError();
        this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
    }

    public void setDimensions(int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
    }

    private void checkCurrent() {
        if (!this.mEglContext.equals(this.mEgl.eglGetCurrentContext()) || !this.mEglSurface.equals(this.mEgl.eglGetCurrentSurface(12377))) {
            checkEglError();
            EGL10 egl10 = this.mEgl;
            EGLDisplay eGLDisplay = this.mEglDisplay;
            EGLSurface eGLSurface = this.mEglSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mEglContext)) {
                throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
            }
            checkEglError();
        }
    }

    private void checkEglError() {
        int error = this.mEgl.eglGetError();
        if (error != 12288) {
            FileLog.e("cannot swap buffers!");
        }
    }

    public void checkGlError() {
        int error = this.mGl.glGetError();
        if (error != 0) {
            FileLog.e("GL error = 0x" + Integer.toHexString(error));
        }
    }

    public void initGL() {
        int[] configSpec;
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        this.mEgl = egl10;
        EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        this.mEglDisplay = eglGetDisplay;
        if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
        }
        int[] version = new int[2];
        if (!this.mEgl.eglInitialize(this.mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
        }
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        if (EmuDetector.with(getContext()).detect()) {
            configSpec = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12344};
        } else {
            configSpec = new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 16, 12326, 0, 12338, 1, 12344};
        }
        this.eglConfig = null;
        if (!this.mEgl.eglChooseConfig(this.mEglDisplay, configSpec, configs, 1, configsCount)) {
            throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
        }
        if (configsCount[0] > 0) {
            this.eglConfig = configs[0];
        }
        EGLConfig eGLConfig = this.eglConfig;
        if (eGLConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
        this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
        checkEglError();
        this.mEglSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.eglConfig, this.mSurface, null);
        checkEglError();
        EGLSurface eGLSurface = this.mEglSurface;
        if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
            int error = this.mEgl.eglGetError();
            if (error == 12299) {
                FileLog.e("eglCreateWindowSurface returned EGL10.EGL_BAD_NATIVE_WINDOW");
                return;
            }
            throw new RuntimeException("eglCreateWindowSurface failed " + GLUtils.getEGLErrorString(error));
        }
        EGL10 egl102 = this.mEgl;
        EGLDisplay eGLDisplay = this.mEglDisplay;
        EGLSurface eGLSurface2 = this.mEglSurface;
        if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.mEgl.eglGetError()));
        }
        checkEglError();
        this.mGl = (GL10) this.mEglContext.getGL();
        checkEglError();
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (event.getAction() == 3 || event.getAction() == 1) {
            this.touched = false;
            startBackAnimation();
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return this.gestureDetector.onTouchEvent(event);
    }

    public void startBackAnimation() {
        cancelAnimatons();
        final float fromX = this.mRenderer.angleX;
        final float fromY = this.mRenderer.angleY;
        final float fromX2 = this.mRenderer.angleX2;
        float sum = fromX + fromY;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.backAnimation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GLIconTextureView.this.m2885x94cc1daa(fromX, fromX2, fromY, valueAnimator);
            }
        });
        this.backAnimation.setDuration(600L);
        this.backAnimation.setInterpolator(new OvershootInterpolator());
        this.backAnimation.start();
        StarParticlesView starParticlesView = this.starParticlesView;
        if (starParticlesView != null) {
            starParticlesView.flingParticles(Math.abs(sum));
        }
        scheduleIdleAnimation(this.idleDelay);
    }

    /* renamed from: lambda$startBackAnimation$0$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView */
    public /* synthetic */ void m2885x94cc1daa(float fromX, float fromX2, float fromY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mRenderer.angleX = v * fromX;
        this.mRenderer.angleX2 = v * fromX2;
        this.mRenderer.angleY = v * fromY;
    }

    private void cancelAnimatons() {
        ValueAnimator valueAnimator = this.backAnimation;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.backAnimation.cancel();
            this.backAnimation = null;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.animatorSet.cancel();
            this.animatorSet = null;
        }
    }

    @Override // android.view.TextureView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        this.rendererChanged = true;
        scheduleIdleAnimation(this.idleDelay);
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimatons();
        this.attached = false;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView */
    public /* synthetic */ void m2882x3ddf7f16(ValueAnimator valueAnimator) {
        this.mRenderer.angleX2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView */
    public /* synthetic */ void m2883xf8551f97(ValueAnimator valueAnimator) {
        this.mRenderer.angleX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-Premium-GLIcon-GLIconTextureView */
    public /* synthetic */ void m2884xb2cac018(ValueAnimator valueAnimator) {
        this.mRenderer.angleY = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    public void scheduleIdleAnimation(long time) {
        AndroidUtilities.cancelRunOnUIThread(this.idleAnimation);
        if (this.dialogIsVisible) {
            return;
        }
        AndroidUtilities.runOnUIThread(this.idleAnimation, time);
    }

    public void startIdleAnimation() {
        if (!this.attached) {
            return;
        }
        int i = this.animationIndexes.get(this.animationPointer).intValue();
        int i2 = this.animationPointer + 1;
        this.animationPointer = i2;
        if (i2 >= this.animationIndexes.size()) {
            Collections.shuffle(this.animationIndexes);
            this.animationPointer = 0;
        }
        if (i == 0) {
            pullAnimation();
        } else if (i == 1) {
            slowFlipAination();
        } else if (i == 2) {
            sleepAnimation();
        } else {
            flipAnimation();
        }
    }

    private void slowFlipAination() {
        this.animatorSet = new AnimatorSet();
        ValueAnimator v1 = ValueAnimator.ofFloat(this.mRenderer.angleX, 360.0f);
        v1.addUpdateListener(this.xUpdater);
        v1.setDuration(8000L);
        v1.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animatorSet.playTogether(v1);
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    private void pullAnimation() {
        int i = Math.abs(Utilities.random.nextInt() % 4);
        this.animatorSet = new AnimatorSet();
        if (i == 0) {
            ValueAnimator v1 = ValueAnimator.ofFloat(this.mRenderer.angleY, 48);
            v1.addUpdateListener(this.yUpdater);
            v1.setDuration(2300L);
            v1.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ValueAnimator v2 = ValueAnimator.ofFloat(48, 0.0f);
            v2.addUpdateListener(this.yUpdater);
            v2.setDuration(500L);
            v2.setStartDelay(2300L);
            v2.setInterpolator(AndroidUtilities.overshootInterpolator);
            this.animatorSet.playTogether(v1, v2);
        } else {
            int a = 485;
            if (i == 2) {
                a = -485;
            }
            ValueAnimator v12 = ValueAnimator.ofFloat(this.mRenderer.angleY, a);
            v12.addUpdateListener(this.xUpdater);
            v12.setDuration(3000L);
            v12.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ValueAnimator v22 = ValueAnimator.ofFloat(a, 0.0f);
            v22.addUpdateListener(this.xUpdater);
            v22.setDuration(1000L);
            v22.setStartDelay(3000L);
            v22.setInterpolator(AndroidUtilities.overshootInterpolator);
            this.animatorSet.playTogether(v12, v22);
        }
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    private void flipAnimation() {
        this.animatorSet = new AnimatorSet();
        ValueAnimator v1 = ValueAnimator.ofFloat(this.mRenderer.angleX, 180.0f);
        v1.addUpdateListener(this.xUpdater);
        v1.setDuration(600L);
        v1.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ValueAnimator v2 = ValueAnimator.ofFloat(180.0f, 360.0f);
        v2.addUpdateListener(this.xUpdater);
        v2.setDuration(600L);
        v2.setStartDelay(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        v2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animatorSet.playTogether(v1, v2);
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    private void sleepAnimation() {
        this.animatorSet = new AnimatorSet();
        ValueAnimator v1 = ValueAnimator.ofFloat(this.mRenderer.angleX, 184.0f);
        v1.addUpdateListener(this.xUpdater);
        v1.setDuration(600L);
        v1.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        ValueAnimator v2 = ValueAnimator.ofFloat(this.mRenderer.angleY, 50.0f);
        v2.addUpdateListener(this.yUpdater);
        v2.setDuration(600L);
        v2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        ValueAnimator v3 = ValueAnimator.ofFloat(180.0f, 0.0f);
        v3.addUpdateListener(this.xUpdater);
        v3.setDuration(800L);
        v3.setStartDelay(10000L);
        v3.setInterpolator(AndroidUtilities.overshootInterpolator);
        ValueAnimator v4 = ValueAnimator.ofFloat(60.0f, 0.0f);
        v4.addUpdateListener(this.yUpdater);
        v4.setDuration(800L);
        v4.setStartDelay(10000L);
        v4.setInterpolator(AndroidUtilities.overshootInterpolator);
        ValueAnimator v5 = ValueAnimator.ofFloat(0.0f, 2.0f, -3.0f, 2.0f, -1.0f, 2.0f, -3.0f, 2.0f, -1.0f, 0.0f);
        v5.addUpdateListener(this.xUpdater2);
        v5.setDuration(10000L);
        v5.setInterpolator(new LinearInterpolator());
        this.animatorSet.playTogether(v1, v2, v3, v4, v5);
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GLIconTextureView.this.mRenderer.angleX = 0.0f;
                GLIconTextureView.this.animatorSet = null;
                GLIconTextureView gLIconTextureView = GLIconTextureView.this;
                gLIconTextureView.scheduleIdleAnimation(gLIconTextureView.idleDelay);
            }
        });
        this.animatorSet.start();
    }

    public void setStarParticlesView(StarParticlesView starParticlesView) {
        this.starParticlesView = starParticlesView;
    }

    public void startEnterAnimation(int angle, long delay) {
        GLIconRenderer gLIconRenderer = this.mRenderer;
        if (gLIconRenderer != null) {
            gLIconRenderer.angleX = -180.0f;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView.7
                @Override // java.lang.Runnable
                public void run() {
                    GLIconTextureView.this.startBackAnimation();
                }
            }, delay);
        }
    }

    public void setDialogVisible(boolean isVisible) {
        this.dialogIsVisible = isVisible;
        if (isVisible) {
            AndroidUtilities.cancelRunOnUIThread(this.idleAnimation);
            startBackAnimation();
            return;
        }
        scheduleIdleAnimation(this.idleDelay);
    }
}
