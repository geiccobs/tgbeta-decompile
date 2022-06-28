package com.google.android.exoplayer2.util;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class EGLSurfaceTexture implements SurfaceTexture.OnFrameAvailableListener, Runnable {
    private static final int[] EGL_CONFIG_ATTRIBUTES = {12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12339, 4, 12344};
    private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
    private static final int EGL_SURFACE_HEIGHT = 1;
    private static final int EGL_SURFACE_WIDTH = 1;
    public static final int SECURE_MODE_NONE = 0;
    public static final int SECURE_MODE_PROTECTED_PBUFFER = 2;
    public static final int SECURE_MODE_SURFACELESS_CONTEXT = 1;
    private final TextureImageListener callback;
    private EGLContext context;
    private EGLDisplay display;
    private final Handler handler;
    private EGLSurface surface;
    private SurfaceTexture texture;
    private final int[] textureIdHolder;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SecureMode {
    }

    /* loaded from: classes3.dex */
    public interface TextureImageListener {
        void onFrameAvailable();
    }

    /* loaded from: classes3.dex */
    public static final class GlException extends RuntimeException {
        private GlException(String msg) {
            super(msg);
        }
    }

    public EGLSurfaceTexture(Handler handler) {
        this(handler, null);
    }

    public EGLSurfaceTexture(Handler handler, TextureImageListener callback) {
        this.handler = handler;
        this.callback = callback;
        this.textureIdHolder = new int[1];
    }

    public void init(int secureMode) {
        EGLDisplay defaultDisplay = getDefaultDisplay();
        this.display = defaultDisplay;
        EGLConfig config = chooseEGLConfig(defaultDisplay);
        EGLContext createEGLContext = createEGLContext(this.display, config, secureMode);
        this.context = createEGLContext;
        this.surface = createEGLSurface(this.display, config, createEGLContext, secureMode);
        generateTextureIds(this.textureIdHolder);
        SurfaceTexture surfaceTexture = new SurfaceTexture(this.textureIdHolder[0]);
        this.texture = surfaceTexture;
        surfaceTexture.setOnFrameAvailableListener(this);
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [android.opengl.EGLContext, android.graphics.SurfaceTexture, android.opengl.EGLSurface, android.opengl.EGLDisplay] */
    public void release() {
        this.handler.removeCallbacks(this);
        try {
            SurfaceTexture surfaceTexture = this.texture;
            if (surfaceTexture != null) {
                surfaceTexture.release();
                GLES20.glDeleteTextures(1, this.textureIdHolder, 0);
            }
        } finally {
            EGLDisplay eGLDisplay = this.display;
            if (eGLDisplay != null && !eGLDisplay.equals(EGL14.EGL_NO_DISPLAY)) {
                EGL14.eglMakeCurrent(this.display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            }
            EGLSurface eGLSurface = this.surface;
            if (eGLSurface != null && !eGLSurface.equals(EGL14.EGL_NO_SURFACE)) {
                EGL14.eglDestroySurface(this.display, this.surface);
            }
            EGLContext eGLContext = this.context;
            if (eGLContext != null) {
                EGL14.eglDestroyContext(this.display, eGLContext);
            }
            if (Util.SDK_INT >= 19) {
                EGL14.eglReleaseThread();
            }
            EGLDisplay eGLDisplay2 = this.display;
            if (eGLDisplay2 != null && !eGLDisplay2.equals(EGL14.EGL_NO_DISPLAY)) {
                EGL14.eglTerminate(this.display);
            }
            this.display = null;
            this.context = null;
            this.surface = null;
            this.texture = null;
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return (SurfaceTexture) Assertions.checkNotNull(this.texture);
    }

    @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.handler.post(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        dispatchOnFrameAvailable();
        SurfaceTexture surfaceTexture = this.texture;
        if (surfaceTexture != null) {
            try {
                surfaceTexture.updateTexImage();
            } catch (RuntimeException e) {
            }
        }
    }

    private void dispatchOnFrameAvailable() {
        TextureImageListener textureImageListener = this.callback;
        if (textureImageListener != null) {
            textureImageListener.onFrameAvailable();
        }
    }

    private static EGLDisplay getDefaultDisplay() {
        EGLDisplay display = EGL14.eglGetDisplay(0);
        if (display == null) {
            throw new GlException("eglGetDisplay failed");
        }
        int[] version = new int[2];
        boolean eglInitialized = EGL14.eglInitialize(display, version, 0, version, 1);
        if (!eglInitialized) {
            throw new GlException("eglInitialize failed");
        }
        return display;
    }

    private static EGLConfig chooseEGLConfig(EGLDisplay display) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        boolean success = EGL14.eglChooseConfig(display, EGL_CONFIG_ATTRIBUTES, 0, configs, 0, 1, numConfigs, 0);
        if (!success || numConfigs[0] <= 0 || configs[0] == null) {
            throw new GlException(Util.formatInvariant("eglChooseConfig failed: success=%b, numConfigs[0]=%d, configs[0]=%s", Boolean.valueOf(success), Integer.valueOf(numConfigs[0]), configs[0]));
        }
        return configs[0];
    }

    private static EGLContext createEGLContext(EGLDisplay display, EGLConfig config, int secureMode) {
        int[] glAttributes;
        if (secureMode == 0) {
            glAttributes = new int[]{12440, 2, 12344};
        } else {
            glAttributes = new int[]{12440, 2, EGL_PROTECTED_CONTENT_EXT, 1, 12344};
        }
        EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, glAttributes, 0);
        if (context == null) {
            throw new GlException("eglCreateContext failed");
        }
        return context;
    }

    private static EGLSurface createEGLSurface(EGLDisplay display, EGLConfig config, EGLContext context, int secureMode) {
        EGLSurface surface;
        int[] pbufferAttributes;
        if (secureMode == 1) {
            surface = EGL14.EGL_NO_SURFACE;
        } else {
            if (secureMode == 2) {
                pbufferAttributes = new int[]{12375, 1, 12374, 1, EGL_PROTECTED_CONTENT_EXT, 1, 12344};
            } else {
                pbufferAttributes = new int[]{12375, 1, 12374, 1, 12344};
            }
            EGLSurface surface2 = EGL14.eglCreatePbufferSurface(display, config, pbufferAttributes, 0);
            if (surface2 == null) {
                throw new GlException("eglCreatePbufferSurface failed");
            }
            surface = surface2;
        }
        boolean eglMadeCurrent = EGL14.eglMakeCurrent(display, surface, surface, context);
        if (!eglMadeCurrent) {
            throw new GlException("eglMakeCurrent failed");
        }
        return surface;
    }

    private static void generateTextureIds(int[] textureIdHolder) {
        GLES20.glGenTextures(1, textureIdHolder, 0);
        GlUtil.checkGlError();
    }
}
