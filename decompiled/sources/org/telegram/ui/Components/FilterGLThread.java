package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Looper;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
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
import org.telegram.ui.Components.FilterShaders;
/* loaded from: classes5.dex */
public class FilterGLThread extends DispatchQueue {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private boolean blurred;
    private Bitmap currentBitmap;
    private Runnable drawRunnable;
    private EGL10 egl10;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private FilterShaders filterShaders;
    private boolean initied;
    private long lastRenderCallTime;
    private int orientation;
    private int renderBufferHeight;
    private int renderBufferWidth;
    private boolean renderDataSet;
    private int simpleInputTexCoordHandle;
    private int simplePositionHandle;
    private int simpleShaderProgram;
    private int simpleSourceImageHandle;
    private volatile int surfaceHeight;
    private SurfaceTexture surfaceTexture;
    private volatile int surfaceWidth;
    private FloatBuffer textureBuffer;
    private boolean updateSurface;
    private FilterGLThreadVideoDelegate videoDelegate;
    private boolean videoFrameAvailable;
    private int videoHeight;
    private SurfaceTexture videoSurfaceTexture;
    private int[] videoTexture;
    private float[] videoTextureMatrix;
    private int videoWidth;

    /* loaded from: classes5.dex */
    public interface FilterGLThreadVideoDelegate {
        void onVideoSurfaceCreated(SurfaceTexture surfaceTexture);
    }

    public FilterGLThread(SurfaceTexture surface, Bitmap bitmap, int bitmapOrientation, boolean mirror) {
        super("PhotoFilterGLThread", false);
        this.videoTextureMatrix = new float[16];
        this.videoTexture = new int[1];
        this.drawRunnable = new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread.1
            @Override // java.lang.Runnable
            public void run() {
                if (FilterGLThread.this.initied) {
                    if ((FilterGLThread.this.eglContext.equals(FilterGLThread.this.egl10.eglGetCurrentContext()) && FilterGLThread.this.eglSurface.equals(FilterGLThread.this.egl10.eglGetCurrentSurface(12377))) || FilterGLThread.this.egl10.eglMakeCurrent(FilterGLThread.this.eglDisplay, FilterGLThread.this.eglSurface, FilterGLThread.this.eglSurface, FilterGLThread.this.eglContext)) {
                        if (FilterGLThread.this.updateSurface) {
                            FilterGLThread.this.videoSurfaceTexture.updateTexImage();
                            FilterGLThread.this.videoSurfaceTexture.getTransformMatrix(FilterGLThread.this.videoTextureMatrix);
                            FilterGLThread.this.setRenderData();
                            FilterGLThread.this.updateSurface = false;
                            FilterGLThread.this.filterShaders.onVideoFrameUpdate(FilterGLThread.this.videoTextureMatrix);
                            FilterGLThread.this.videoFrameAvailable = true;
                        }
                        if (FilterGLThread.this.renderDataSet) {
                            if (FilterGLThread.this.videoDelegate == null || FilterGLThread.this.videoFrameAvailable) {
                                GLES20.glViewport(0, 0, FilterGLThread.this.renderBufferWidth, FilterGLThread.this.renderBufferHeight);
                                FilterGLThread.this.filterShaders.drawSkinSmoothPass();
                                FilterGLThread.this.filterShaders.drawEnhancePass();
                                if (FilterGLThread.this.videoDelegate == null) {
                                    FilterGLThread.this.filterShaders.drawSharpenPass();
                                }
                                FilterGLThread.this.filterShaders.drawCustomParamsPass();
                                FilterGLThread filterGLThread = FilterGLThread.this;
                                filterGLThread.blurred = filterGLThread.filterShaders.drawBlurPass();
                            }
                            GLES20.glViewport(0, 0, FilterGLThread.this.surfaceWidth, FilterGLThread.this.surfaceHeight);
                            GLES20.glBindFramebuffer(36160, 0);
                            GLES20.glUseProgram(FilterGLThread.this.simpleShaderProgram);
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, FilterGLThread.this.filterShaders.getRenderTexture(1 ^ (FilterGLThread.this.blurred ? 1 : 0)));
                            GLES20.glUniform1i(FilterGLThread.this.simpleSourceImageHandle, 0);
                            GLES20.glEnableVertexAttribArray(FilterGLThread.this.simpleInputTexCoordHandle);
                            GLES20.glVertexAttribPointer(FilterGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, (Buffer) (FilterGLThread.this.textureBuffer != null ? FilterGLThread.this.textureBuffer : FilterGLThread.this.filterShaders.getTextureBuffer()));
                            GLES20.glEnableVertexAttribArray(FilterGLThread.this.simplePositionHandle);
                            GLES20.glVertexAttribPointer(FilterGLThread.this.simplePositionHandle, 2, 5126, false, 8, (Buffer) FilterGLThread.this.filterShaders.getVertexBuffer());
                            GLES20.glDrawArrays(5, 0, 4);
                            FilterGLThread.this.egl10.eglSwapBuffers(FilterGLThread.this.eglDisplay, FilterGLThread.this.eglSurface);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(FilterGLThread.this.egl10.eglGetError()));
                    }
                }
            }
        };
        this.surfaceTexture = surface;
        this.currentBitmap = bitmap;
        this.orientation = bitmapOrientation;
        this.filterShaders = new FilterShaders(false);
        float[] textureCoordinates = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (mirror) {
            float temp = textureCoordinates[2];
            textureCoordinates[2] = textureCoordinates[0];
            textureCoordinates[0] = temp;
            float temp2 = textureCoordinates[6];
            textureCoordinates[6] = textureCoordinates[4];
            textureCoordinates[4] = temp2;
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = bb.asFloatBuffer();
        this.textureBuffer = asFloatBuffer;
        asFloatBuffer.put(textureCoordinates);
        this.textureBuffer.position(0);
        start();
    }

    public FilterGLThread(SurfaceTexture surface, FilterGLThreadVideoDelegate filterGLThreadVideoDelegate) {
        super("VideoFilterGLThread", false);
        this.videoTextureMatrix = new float[16];
        this.videoTexture = new int[1];
        this.drawRunnable = new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread.1
            @Override // java.lang.Runnable
            public void run() {
                if (FilterGLThread.this.initied) {
                    if ((FilterGLThread.this.eglContext.equals(FilterGLThread.this.egl10.eglGetCurrentContext()) && FilterGLThread.this.eglSurface.equals(FilterGLThread.this.egl10.eglGetCurrentSurface(12377))) || FilterGLThread.this.egl10.eglMakeCurrent(FilterGLThread.this.eglDisplay, FilterGLThread.this.eglSurface, FilterGLThread.this.eglSurface, FilterGLThread.this.eglContext)) {
                        if (FilterGLThread.this.updateSurface) {
                            FilterGLThread.this.videoSurfaceTexture.updateTexImage();
                            FilterGLThread.this.videoSurfaceTexture.getTransformMatrix(FilterGLThread.this.videoTextureMatrix);
                            FilterGLThread.this.setRenderData();
                            FilterGLThread.this.updateSurface = false;
                            FilterGLThread.this.filterShaders.onVideoFrameUpdate(FilterGLThread.this.videoTextureMatrix);
                            FilterGLThread.this.videoFrameAvailable = true;
                        }
                        if (FilterGLThread.this.renderDataSet) {
                            if (FilterGLThread.this.videoDelegate == null || FilterGLThread.this.videoFrameAvailable) {
                                GLES20.glViewport(0, 0, FilterGLThread.this.renderBufferWidth, FilterGLThread.this.renderBufferHeight);
                                FilterGLThread.this.filterShaders.drawSkinSmoothPass();
                                FilterGLThread.this.filterShaders.drawEnhancePass();
                                if (FilterGLThread.this.videoDelegate == null) {
                                    FilterGLThread.this.filterShaders.drawSharpenPass();
                                }
                                FilterGLThread.this.filterShaders.drawCustomParamsPass();
                                FilterGLThread filterGLThread = FilterGLThread.this;
                                filterGLThread.blurred = filterGLThread.filterShaders.drawBlurPass();
                            }
                            GLES20.glViewport(0, 0, FilterGLThread.this.surfaceWidth, FilterGLThread.this.surfaceHeight);
                            GLES20.glBindFramebuffer(36160, 0);
                            GLES20.glUseProgram(FilterGLThread.this.simpleShaderProgram);
                            GLES20.glActiveTexture(33984);
                            GLES20.glBindTexture(3553, FilterGLThread.this.filterShaders.getRenderTexture(1 ^ (FilterGLThread.this.blurred ? 1 : 0)));
                            GLES20.glUniform1i(FilterGLThread.this.simpleSourceImageHandle, 0);
                            GLES20.glEnableVertexAttribArray(FilterGLThread.this.simpleInputTexCoordHandle);
                            GLES20.glVertexAttribPointer(FilterGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, (Buffer) (FilterGLThread.this.textureBuffer != null ? FilterGLThread.this.textureBuffer : FilterGLThread.this.filterShaders.getTextureBuffer()));
                            GLES20.glEnableVertexAttribArray(FilterGLThread.this.simplePositionHandle);
                            GLES20.glVertexAttribPointer(FilterGLThread.this.simplePositionHandle, 2, 5126, false, 8, (Buffer) FilterGLThread.this.filterShaders.getVertexBuffer());
                            GLES20.glDrawArrays(5, 0, 4);
                            FilterGLThread.this.egl10.eglSwapBuffers(FilterGLThread.this.eglDisplay, FilterGLThread.this.eglSurface);
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(FilterGLThread.this.egl10.eglGetError()));
                    }
                }
            }
        };
        this.surfaceTexture = surface;
        this.videoDelegate = filterGLThreadVideoDelegate;
        this.filterShaders = new FilterShaders(true);
        start();
    }

    /* renamed from: lambda$setFilterGLThreadDelegate$0$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2613xa51347c1(FilterShaders.FilterShadersDelegate filterShadersDelegate) {
        this.filterShaders.setDelegate(filterShadersDelegate);
    }

    public void setFilterGLThreadDelegate(final FilterShaders.FilterShadersDelegate filterShadersDelegate) {
        postRunnable(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                FilterGLThread.this.m2613xa51347c1(filterShadersDelegate);
            }
        });
    }

    private boolean initGL() {
        int h;
        int w;
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
                int vertexShader = FilterShaders.loadShader(35633, FilterShaders.simpleVertexShaderCode);
                int fragmentShader = FilterShaders.loadShader(35632, FilterShaders.simpleFragmentShaderCode);
                if (vertexShader == 0 || fragmentShader == 0) {
                    return false;
                }
                int glCreateProgram = GLES20.glCreateProgram();
                this.simpleShaderProgram = glCreateProgram;
                GLES20.glAttachShader(glCreateProgram, vertexShader);
                GLES20.glAttachShader(this.simpleShaderProgram, fragmentShader);
                GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                GLES20.glLinkProgram(this.simpleShaderProgram);
                int[] linkStatus = new int[1];
                GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, linkStatus, 0);
                if (linkStatus[0] != 0) {
                    this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                    this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                    this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                } else {
                    GLES20.glDeleteProgram(this.simpleShaderProgram);
                    this.simpleShaderProgram = 0;
                }
                Bitmap bitmap = this.currentBitmap;
                if (bitmap != null) {
                    w = bitmap.getWidth();
                    h = this.currentBitmap.getHeight();
                } else {
                    w = this.videoWidth;
                    h = this.videoHeight;
                }
                if (this.videoDelegate != null) {
                    GLES20.glGenTextures(1, this.videoTexture, 0);
                    Matrix.setIdentityM(this.videoTextureMatrix, 0);
                    SurfaceTexture surfaceTexture2 = new SurfaceTexture(this.videoTexture[0]);
                    this.videoSurfaceTexture = surfaceTexture2;
                    surfaceTexture2.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda0
                        @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                        public final void onFrameAvailable(SurfaceTexture surfaceTexture3) {
                            FilterGLThread.this.m2610lambda$initGL$1$orgtelegramuiComponentsFilterGLThread(surfaceTexture3);
                        }
                    });
                    GLES20.glBindTexture(36197, this.videoTexture[0]);
                    GLES20.glTexParameterf(36197, 10240, 9729.0f);
                    GLES20.glTexParameterf(36197, 10241, 9728.0f);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            FilterGLThread.this.m2611lambda$initGL$2$orgtelegramuiComponentsFilterGLThread();
                        }
                    });
                }
                if (!this.filterShaders.create()) {
                    finish();
                    return false;
                } else if (w != 0 && h != 0) {
                    this.filterShaders.setRenderData(this.currentBitmap, this.orientation, this.videoTexture[0], w, h);
                    this.renderDataSet = true;
                    this.renderBufferWidth = this.filterShaders.getRenderBufferWidth();
                    this.renderBufferHeight = this.filterShaders.getRenderBufferHeight();
                    return true;
                } else {
                    return true;
                }
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

    /* renamed from: lambda$initGL$1$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2610lambda$initGL$1$orgtelegramuiComponentsFilterGLThread(SurfaceTexture surfaceTexture) {
        requestRender(false, true, true);
    }

    /* renamed from: lambda$initGL$2$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2611lambda$initGL$2$orgtelegramuiComponentsFilterGLThread() {
        this.videoDelegate.onVideoSurfaceCreated(this.videoSurfaceTexture);
    }

    public void setVideoSize(final int width, final int height) {
        postRunnable(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                FilterGLThread.this.m2615lambda$setVideoSize$3$orgtelegramuiComponentsFilterGLThread(width, height);
            }
        });
    }

    /* renamed from: lambda$setVideoSize$3$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2615lambda$setVideoSize$3$orgtelegramuiComponentsFilterGLThread(int width, int height) {
        if (this.videoWidth == width && this.videoHeight == height) {
            return;
        }
        this.videoWidth = width;
        this.videoHeight = height;
        if (width > 1280 || height > 1280) {
            this.videoWidth = width / 2;
            this.videoHeight = height / 2;
        }
        this.renderDataSet = false;
        setRenderData();
        this.drawRunnable.run();
    }

    public void finish() {
        this.currentBitmap = null;
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
        SurfaceTexture surfaceTexture = this.surfaceTexture;
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
    }

    public void setRenderData() {
        int i;
        int i2;
        if (this.renderDataSet || (i = this.videoWidth) <= 0 || (i2 = this.videoHeight) <= 0) {
            return;
        }
        this.filterShaders.setRenderData(this.currentBitmap, this.orientation, this.videoTexture[0], i, i2);
        this.renderDataSet = true;
        this.renderBufferWidth = this.filterShaders.getRenderBufferWidth();
        this.renderBufferHeight = this.filterShaders.getRenderBufferHeight();
    }

    private Bitmap getRenderBufferBitmap() {
        int i;
        int i2 = this.renderBufferWidth;
        if (i2 == 0 || (i = this.renderBufferHeight) == 0) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(i2 * i * 4);
        GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, buffer);
        Bitmap bitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }

    public Bitmap getTexture() {
        if (!this.initied || !isAlive()) {
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Bitmap[] object = new Bitmap[1];
        try {
            if (postRunnable(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    FilterGLThread.this.m2609lambda$getTexture$4$orgtelegramuiComponentsFilterGLThread(object, countDownLatch);
                }
            })) {
                countDownLatch.await();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return object[0];
    }

    /* renamed from: lambda$getTexture$4$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2609lambda$getTexture$4$orgtelegramuiComponentsFilterGLThread(Bitmap[] object, CountDownLatch countDownLatch) {
        GLES20.glBindFramebuffer(36160, this.filterShaders.getRenderFrameBuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.filterShaders.getRenderTexture(!this.blurred ? 1 : 0), 0);
        GLES20.glClear(0);
        object[0] = getRenderBufferBitmap();
        countDownLatch.countDown();
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glClear(0);
    }

    public void shutdown() {
        postRunnable(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FilterGLThread.this.m2616lambda$shutdown$5$orgtelegramuiComponentsFilterGLThread();
            }
        });
    }

    /* renamed from: lambda$shutdown$5$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2616lambda$shutdown$5$orgtelegramuiComponentsFilterGLThread() {
        finish();
        Looper looper = Looper.myLooper();
        if (looper != null) {
            looper.quit();
        }
    }

    public void setSurfaceTextureSize(final int width, final int height) {
        postRunnable(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                FilterGLThread.this.m2614x6ec591aa(width, height);
            }
        });
    }

    /* renamed from: lambda$setSurfaceTextureSize$6$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2614x6ec591aa(int width, int height) {
        this.surfaceWidth = width;
        this.surfaceHeight = height;
    }

    @Override // org.telegram.messenger.DispatchQueue, java.lang.Thread, java.lang.Runnable
    public void run() {
        this.initied = initGL();
        super.run();
    }

    public void requestRender(boolean updateBlur) {
        requestRender(updateBlur, false, false);
    }

    public void requestRender(final boolean updateBlur, final boolean force, final boolean surface) {
        postRunnable(new Runnable() { // from class: org.telegram.ui.Components.FilterGLThread$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                FilterGLThread.this.m2612lambda$requestRender$7$orgtelegramuiComponentsFilterGLThread(updateBlur, surface, force);
            }
        });
    }

    /* renamed from: lambda$requestRender$7$org-telegram-ui-Components-FilterGLThread */
    public /* synthetic */ void m2612lambda$requestRender$7$orgtelegramuiComponentsFilterGLThread(boolean updateBlur, boolean surface, boolean force) {
        if (updateBlur) {
            this.filterShaders.requestUpdateBlurTexture();
        }
        if (surface) {
            this.updateSurface = true;
        }
        long newTime = System.currentTimeMillis();
        if (force || Math.abs(this.lastRenderCallTime - newTime) > 30) {
            this.lastRenderCallTime = newTime;
            this.drawRunnable.run();
        }
    }
}
