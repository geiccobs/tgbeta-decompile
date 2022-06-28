package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Size;
/* loaded from: classes5.dex */
public class RenderView extends TextureView {
    private Bitmap bitmap;
    private Brush brush;
    private int color;
    private RenderViewDelegate delegate;
    private boolean firstDrawSent;
    private Input input = new Input(this);
    private CanvasInternal internal;
    private Painting painting;
    private DispatchQueue queue;
    private boolean shuttingDown;
    private boolean transformedBitmap;
    private UndoStore undoStore;
    private float weight;

    /* loaded from: classes5.dex */
    public interface RenderViewDelegate {
        void onBeganDrawing();

        void onFinishedDrawing(boolean z);

        void onFirstDraw();

        boolean shouldDraw();
    }

    public RenderView(Context context, Painting paint, Bitmap b) {
        super(context);
        setOpaque(false);
        this.bitmap = b;
        this.painting = paint;
        paint.setRenderView(this);
        setSurfaceTextureListener(new AnonymousClass1());
        this.painting.setDelegate(new Painting.PaintingDelegate() { // from class: org.telegram.ui.Components.Paint.RenderView.2
            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public void contentChanged() {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.scheduleRedraw();
                }
            }

            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public void strokeCommited() {
            }

            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public UndoStore requestUndoStore() {
                return RenderView.this.undoStore;
            }

            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public DispatchQueue requestDispatchQueue() {
                return RenderView.this.queue;
            }
        });
    }

    /* renamed from: org.telegram.ui.Components.Paint.RenderView$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 implements TextureView.SurfaceTextureListener {
        AnonymousClass1() {
            RenderView.this = this$0;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (surface == null || RenderView.this.internal != null) {
                return;
            }
            RenderView.this.internal = new CanvasInternal(surface);
            RenderView.this.internal.setBufferSize(width, height);
            RenderView.this.updateTransform();
            RenderView.this.internal.requestRender();
            if (RenderView.this.painting.isPaused()) {
                RenderView.this.painting.onResume();
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (RenderView.this.internal != null) {
                RenderView.this.internal.setBufferSize(width, height);
                RenderView.this.updateTransform();
                RenderView.this.internal.requestRender();
                RenderView.this.internal.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        RenderView.AnonymousClass1.this.m2781xd0e9e8e9();
                    }
                });
            }
        }

        /* renamed from: lambda$onSurfaceTextureSizeChanged$0$org-telegram-ui-Components-Paint-RenderView$1 */
        public /* synthetic */ void m2781xd0e9e8e9() {
            if (RenderView.this.internal != null) {
                RenderView.this.internal.requestRender();
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (RenderView.this.internal != null && !RenderView.this.shuttingDown) {
                RenderView.this.painting.onPause(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        RenderView.AnonymousClass1.this.m2780xf10d7f50();
                    }
                });
            }
            return true;
        }

        /* renamed from: lambda$onSurfaceTextureDestroyed$1$org-telegram-ui-Components-Paint-RenderView$1 */
        public /* synthetic */ void m2780xf10d7f50() {
            RenderView.this.internal.shutdown();
            RenderView.this.internal = null;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    public void redraw() {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null) {
            return;
        }
        canvasInternal.requestRender();
    }

    public boolean onTouch(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return false;
        }
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null || !canvasInternal.initialized || !this.internal.ready) {
            return true;
        }
        this.input.process(event, getScaleX());
        return true;
    }

    public void setUndoStore(UndoStore store) {
        this.undoStore = store;
    }

    public void setQueue(DispatchQueue dispatchQueue) {
        this.queue = dispatchQueue;
    }

    public void setDelegate(RenderViewDelegate renderViewDelegate) {
        this.delegate = renderViewDelegate;
    }

    public Painting getPainting() {
        return this.painting;
    }

    private float brushWeightForSize(float size) {
        float paintingWidth = this.painting.getSize().width;
        return (0.00390625f * paintingWidth) + (0.043945312f * paintingWidth * size);
    }

    public int getCurrentColor() {
        return this.color;
    }

    public void setColor(int value) {
        this.color = value;
    }

    public float getCurrentWeight() {
        return this.weight;
    }

    public void setBrushSize(float size) {
        this.weight = brushWeightForSize(size);
    }

    public Brush getCurrentBrush() {
        return this.brush;
    }

    public void setBrush(Brush value) {
        Painting painting = this.painting;
        this.brush = value;
        painting.setBrush(value);
    }

    public void updateTransform() {
        Matrix matrix = new Matrix();
        float scale = this.painting != null ? getWidth() / this.painting.getSize().width : 1.0f;
        if (scale <= 0.0f) {
            scale = 1.0f;
        }
        Size paintingSize = getPainting().getSize();
        matrix.preTranslate(getWidth() / 2.0f, getHeight() / 2.0f);
        matrix.preScale(scale, -scale);
        matrix.preTranslate((-paintingSize.width) / 2.0f, (-paintingSize.height) / 2.0f);
        this.input.setMatrix(matrix);
        float[] proj = GLMatrix.LoadOrtho(0.0f, this.internal.bufferWidth, 0.0f, this.internal.bufferHeight, -1.0f, 1.0f);
        float[] effectiveProjection = GLMatrix.LoadGraphicsMatrix(matrix);
        float[] finalProjection = GLMatrix.MultiplyMat4f(proj, effectiveProjection);
        this.painting.setRenderProjection(finalProjection);
    }

    public boolean shouldDraw() {
        RenderViewDelegate renderViewDelegate = this.delegate;
        return renderViewDelegate == null || renderViewDelegate.shouldDraw();
    }

    public void onBeganDrawing() {
        RenderViewDelegate renderViewDelegate = this.delegate;
        if (renderViewDelegate != null) {
            renderViewDelegate.onBeganDrawing();
        }
    }

    public void onFinishedDrawing(boolean moved) {
        RenderViewDelegate renderViewDelegate = this.delegate;
        if (renderViewDelegate != null) {
            renderViewDelegate.onFinishedDrawing(moved);
        }
    }

    public void shutdown() {
        this.shuttingDown = true;
        if (this.internal != null) {
            performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.this.m2779lambda$shutdown$0$orgtelegramuiComponentsPaintRenderView();
                }
            });
        }
        setVisibility(8);
    }

    /* renamed from: lambda$shutdown$0$org-telegram-ui-Components-Paint-RenderView */
    public /* synthetic */ void m2779lambda$shutdown$0$orgtelegramuiComponentsPaintRenderView() {
        this.painting.cleanResources(this.transformedBitmap);
        this.internal.shutdown();
        this.internal = null;
    }

    /* loaded from: classes5.dex */
    public class CanvasInternal extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private int bufferHeight;
        private int bufferWidth;
        private Runnable drawRunnable = new AnonymousClass1();
        private EGL10 egl10;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initialized;
        private long lastRenderCallTime;
        private volatile boolean ready;
        private Runnable scheduledRunnable;
        private SurfaceTexture surfaceTexture;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CanvasInternal(SurfaceTexture surface) {
            super("CanvasInternal");
            RenderView.this = r1;
            this.surfaceTexture = surface;
        }

        @Override // org.telegram.messenger.DispatchQueue, java.lang.Thread, java.lang.Runnable
        public void run() {
            if (RenderView.this.bitmap == null || RenderView.this.bitmap.isRecycled()) {
                return;
            }
            this.initialized = initGL();
            super.run();
        }

        private boolean initGL() {
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
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
            int[] configSpec = {12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344};
            if (!this.egl10.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eglConfig = configs[0];
                int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture = this.surfaceTexture;
                if (surfaceTexture instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eglConfig, surfaceTexture, null);
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
                    GLES20.glEnable(3042);
                    GLES20.glDisable(3024);
                    GLES20.glDisable(2960);
                    GLES20.glDisable(2929);
                    RenderView.this.painting.setupShaders();
                    checkBitmap();
                    RenderView.this.painting.setBitmap(RenderView.this.bitmap);
                    Utils.HasGLError();
                    return true;
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

        private Bitmap createBitmap(Bitmap bitmap, float scale) {
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private void checkBitmap() {
            Size paintingSize = RenderView.this.painting.getSize();
            if (RenderView.this.bitmap.getWidth() != paintingSize.width || RenderView.this.bitmap.getHeight() != paintingSize.height) {
                Bitmap b = Bitmap.createBitmap((int) paintingSize.width, (int) paintingSize.height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                canvas.drawBitmap(RenderView.this.bitmap, (Rect) null, new RectF(0.0f, 0.0f, paintingSize.width, paintingSize.height), (Paint) null);
                RenderView.this.bitmap = b;
                RenderView.this.transformedBitmap = true;
            }
        }

        public boolean setCurrentContext() {
            if (!this.initialized) {
                return false;
            }
            if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                EGL10 egl10 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                return egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext);
            }
            return true;
        }

        /* renamed from: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
                CanvasInternal.this = this$1;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (CanvasInternal.this.initialized && !RenderView.this.shuttingDown) {
                    CanvasInternal.this.setCurrentContext();
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glViewport(0, 0, CanvasInternal.this.bufferWidth, CanvasInternal.this.bufferHeight);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                    GLES20.glClear(16384);
                    RenderView.this.painting.render();
                    GLES20.glBlendFunc(1, 771);
                    CanvasInternal.this.egl10.eglSwapBuffers(CanvasInternal.this.eglDisplay, CanvasInternal.this.eglSurface);
                    if (!RenderView.this.firstDrawSent) {
                        RenderView.this.firstDrawSent = true;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                RenderView.CanvasInternal.AnonymousClass1.this.m2786xb8108d85();
                            }
                        });
                    }
                    if (!CanvasInternal.this.ready) {
                        CanvasInternal.this.ready = true;
                    }
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-Paint-RenderView$CanvasInternal$1 */
            public /* synthetic */ void m2786xb8108d85() {
                RenderView.this.delegate.onFirstDraw();
            }
        }

        public void setBufferSize(int width, int height) {
            this.bufferWidth = width;
            this.bufferHeight = height;
        }

        /* renamed from: lambda$requestRender$0$org-telegram-ui-Components-Paint-RenderView$CanvasInternal */
        public /* synthetic */ void m2783x432bb27e() {
            this.drawRunnable.run();
        }

        public void requestRender() {
            postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.CanvasInternal.this.m2783x432bb27e();
                }
            });
        }

        public void scheduleRedraw() {
            Runnable runnable = this.scheduledRunnable;
            if (runnable != null) {
                cancelRunnable(runnable);
                this.scheduledRunnable = null;
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.CanvasInternal.this.m2784x2a8dd9ba();
                }
            };
            this.scheduledRunnable = runnable2;
            postRunnable(runnable2, 1L);
        }

        /* renamed from: lambda$scheduleRedraw$1$org-telegram-ui-Components-Paint-RenderView$CanvasInternal */
        public /* synthetic */ void m2784x2a8dd9ba() {
            this.scheduledRunnable = null;
            this.drawRunnable.run();
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

        public void shutdown() {
            postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.CanvasInternal.this.m2785x36125971();
                }
            });
        }

        /* renamed from: lambda$shutdown$2$org-telegram-ui-Components-Paint-RenderView$CanvasInternal */
        public /* synthetic */ void m2785x36125971() {
            finish();
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        }

        public Bitmap getTexture() {
            if (!this.initialized) {
                return null;
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Bitmap[] object = new Bitmap[1];
            try {
                postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        RenderView.CanvasInternal.this.m2782x889307e1(object, countDownLatch);
                    }
                });
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
            return object[0];
        }

        /* renamed from: lambda$getTexture$3$org-telegram-ui-Components-Paint-RenderView$CanvasInternal */
        public /* synthetic */ void m2782x889307e1(Bitmap[] object, CountDownLatch countDownLatch) {
            Painting.PaintingData data = RenderView.this.painting.getPaintingData(new RectF(0.0f, 0.0f, RenderView.this.painting.getSize().width, RenderView.this.painting.getSize().height), false);
            if (data != null) {
                object[0] = data.bitmap;
            }
            countDownLatch.countDown();
        }
    }

    public Bitmap getResultBitmap() {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null) {
            return canvasInternal.getTexture();
        }
        return null;
    }

    public void performInContext(final Runnable action) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null) {
            return;
        }
        canvasInternal.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RenderView.this.m2778xd0118e5a(action);
            }
        });
    }

    /* renamed from: lambda$performInContext$1$org-telegram-ui-Components-Paint-RenderView */
    public /* synthetic */ void m2778xd0118e5a(Runnable action) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null && canvasInternal.initialized) {
            this.internal.setCurrentContext();
            action.run();
        }
    }
}
