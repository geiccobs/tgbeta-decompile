package org.webrtc;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.webrtc.EglBase;
import org.webrtc.EglBase10;
/* loaded from: classes5.dex */
public class EglBase10Impl implements EglBase10 {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final String TAG = "EglBase10Impl";
    private final EGL10 egl;
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface = EGL10.EGL_NO_SURFACE;
    private EGLSurface eglBackgroundSurface = EGL10.EGL_NO_SURFACE;

    public static native long nativeGetCurrentNativeEGLContext();

    /* loaded from: classes5.dex */
    private static class Context implements EglBase10.Context {
        private final EGL10 egl;
        private final EGLContext eglContext;
        private final EGLConfig eglContextConfig;

        @Override // org.webrtc.EglBase10.Context
        public EGLContext getRawContext() {
            return this.eglContext;
        }

        @Override // org.webrtc.EglBase.Context
        public long getNativeEglContext() {
            EGLContext previousContext = this.egl.eglGetCurrentContext();
            EGLDisplay currentDisplay = this.egl.eglGetCurrentDisplay();
            EGLSurface previousDrawSurface = this.egl.eglGetCurrentSurface(12377);
            EGLSurface previousReadSurface = this.egl.eglGetCurrentSurface(12378);
            EGLSurface tempEglSurface = null;
            if (currentDisplay == EGL10.EGL_NO_DISPLAY) {
                currentDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            }
            try {
                if (previousContext != this.eglContext) {
                    int[] surfaceAttribs = {12375, 1, 12374, 1, 12344};
                    tempEglSurface = this.egl.eglCreatePbufferSurface(currentDisplay, this.eglContextConfig, surfaceAttribs);
                    if (!this.egl.eglMakeCurrent(currentDisplay, tempEglSurface, tempEglSurface, this.eglContext)) {
                        throw new RuntimeException("Failed to make temporary EGL surface active: " + this.egl.eglGetError());
                    }
                }
                return EglBase10Impl.nativeGetCurrentNativeEGLContext();
            } finally {
                if (0 != 0) {
                    this.egl.eglMakeCurrent(currentDisplay, previousDrawSurface, previousReadSurface, previousContext);
                    this.egl.eglDestroySurface(currentDisplay, null);
                }
            }
        }

        public Context(EGL10 egl, EGLContext eglContext, EGLConfig eglContextConfig) {
            this.egl = egl;
            this.eglContext = eglContext;
            this.eglContextConfig = eglContextConfig;
        }
    }

    public EglBase10Impl(EGLContext sharedContext, int[] configAttributes) {
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        this.egl = egl10;
        EGLDisplay eglDisplay = getEglDisplay();
        this.eglDisplay = eglDisplay;
        this.eglConfig = getEglConfig(egl10, eglDisplay, configAttributes);
        int openGlesVersion = EglBase.CC.getOpenGlesVersionFromConfig(configAttributes);
        Logging.d(TAG, "Using OpenGL ES version " + openGlesVersion);
        this.eglContext = createEglContext(sharedContext, this.eglDisplay, this.eglConfig, openGlesVersion);
    }

    @Override // org.webrtc.EglBase
    public void createSurface(Surface surface) {
        createSurfaceInternal(new FakeSurfaceHolder(surface), false);
    }

    @Override // org.webrtc.EglBase
    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture, false);
    }

    private void createSurfaceInternal(Object nativeWindow, boolean background) {
        if (!(nativeWindow instanceof SurfaceHolder) && !(nativeWindow instanceof SurfaceTexture)) {
            throw new IllegalStateException("Input must be either a SurfaceHolder or SurfaceTexture");
        }
        checkIsNotReleased();
        if (background) {
            if (this.eglBackgroundSurface != EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("Already has an EGLSurface");
            }
            int[] surfaceAttribs = {12344};
            EGLSurface eglCreateWindowSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, nativeWindow, surfaceAttribs);
            this.eglBackgroundSurface = eglCreateWindowSurface;
            if (eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
        } else if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        } else {
            int[] surfaceAttribs2 = {12344};
            EGLSurface eglCreateWindowSurface2 = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, nativeWindow, surfaceAttribs2);
            this.eglSurface = eglCreateWindowSurface2;
            if (eglCreateWindowSurface2 == EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
        }
    }

    @Override // org.webrtc.EglBase
    public void createDummyPbufferSurface() {
        createPbufferSurface(1, 1);
    }

    @Override // org.webrtc.EglBase
    public void createPbufferSurface(int width, int height) {
        checkIsNotReleased();
        if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        int[] surfaceAttribs = {12375, width, 12374, height, 12344};
        EGLSurface eglCreatePbufferSurface = this.egl.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, surfaceAttribs);
        this.eglSurface = eglCreatePbufferSurface;
        if (eglCreatePbufferSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Failed to create pixel buffer surface with size " + width + "x" + height + ": 0x" + Integer.toHexString(this.egl.eglGetError()));
        }
    }

    @Override // org.webrtc.EglBase
    public EglBase.Context getEglBaseContext() {
        return new Context(this.egl, this.eglContext, this.eglConfig);
    }

    @Override // org.webrtc.EglBase
    public boolean hasSurface() {
        return this.eglSurface != EGL10.EGL_NO_SURFACE;
    }

    @Override // org.webrtc.EglBase
    public int surfaceWidth() {
        int[] widthArray = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, widthArray);
        return widthArray[0];
    }

    @Override // org.webrtc.EglBase
    public int surfaceHeight() {
        int[] heightArray = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, heightArray);
        return heightArray[0];
    }

    @Override // org.webrtc.EglBase
    public void releaseSurface(boolean background) {
        if (background) {
            if (this.eglBackgroundSurface != EGL10.EGL_NO_SURFACE) {
                this.egl.eglDestroySurface(this.eglDisplay, this.eglBackgroundSurface);
                this.eglBackgroundSurface = EGL10.EGL_NO_SURFACE;
            }
        } else if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    private void checkIsNotReleased() {
        if (this.eglDisplay == EGL10.EGL_NO_DISPLAY || this.eglContext == EGL10.EGL_NO_CONTEXT || this.eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }

    @Override // org.webrtc.EglBase
    public void release() {
        checkIsNotReleased();
        releaseSurface(false);
        releaseSurface(true);
        detachCurrent();
        this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.egl.eglTerminate(this.eglDisplay);
        this.eglContext = EGL10.EGL_NO_CONTEXT;
        this.eglDisplay = EGL10.EGL_NO_DISPLAY;
        this.eglConfig = null;
    }

    @Override // org.webrtc.EglBase
    public void makeCurrent() {
        checkIsNotReleased();
        if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't make current");
        }
        synchronized (EglBase.lock) {
            EGL10 egl10 = this.egl;
            EGLDisplay eGLDisplay = this.eglDisplay;
            EGLSurface eGLSurface = this.eglSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
        }
    }

    @Override // org.webrtc.EglBase
    public void detachCurrent() {
        synchronized (EglBase.lock) {
            if (!this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
                throw new RuntimeException("eglDetachCurrent failed: 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
        }
    }

    @Override // org.webrtc.EglBase
    public void swapBuffers(boolean background) {
        EGLSurface surface = background ? this.eglBackgroundSurface : this.eglSurface;
        checkIsNotReleased();
        if (surface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't swap buffers");
        }
        synchronized (EglBase.lock) {
            this.egl.eglSwapBuffers(this.eglDisplay, surface);
        }
    }

    @Override // org.webrtc.EglBase
    public void swapBuffers(long timeStampNs, boolean background) {
        swapBuffers(background);
    }

    @Override // org.webrtc.EglBase
    public void createBackgroundSurface(SurfaceTexture surface) {
        createSurfaceInternal(surface, true);
    }

    @Override // org.webrtc.EglBase
    public void makeBackgroundCurrent() {
        checkIsNotReleased();
        if (this.eglBackgroundSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface - can't make current");
        }
        synchronized (EglBase.lock) {
            EGL10 egl10 = this.egl;
            EGLDisplay eGLDisplay = this.eglDisplay;
            EGLSurface eGLSurface = this.eglBackgroundSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
        }
    }

    @Override // org.webrtc.EglBase
    public boolean hasBackgroundSurface() {
        return this.eglBackgroundSurface != EGL10.EGL_NO_SURFACE;
    }

    private EGLDisplay getEglDisplay() {
        EGLDisplay eglDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL10 display: 0x" + Integer.toHexString(this.egl.eglGetError()));
        }
        int[] version = new int[2];
        if (!this.egl.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("Unable to initialize EGL10: 0x" + Integer.toHexString(this.egl.eglGetError()));
        }
        return eglDisplay;
    }

    private static EGLConfig getEglConfig(EGL10 egl, EGLDisplay eglDisplay, int[] configAttributes) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!egl.eglChooseConfig(eglDisplay, configAttributes, configs, configs.length, numConfigs)) {
            throw new RuntimeException("eglChooseConfig failed: 0x" + Integer.toHexString(egl.eglGetError()));
        } else if (numConfigs[0] <= 0) {
            throw new RuntimeException("Unable to find any matching EGL config");
        } else {
            EGLConfig eglConfig = configs[0];
            if (eglConfig == null) {
                throw new RuntimeException("eglChooseConfig returned null");
            }
            return eglConfig;
        }
    }

    private EGLContext createEglContext(EGLContext sharedContext, EGLDisplay eglDisplay, EGLConfig eglConfig, int openGlesVersion) {
        EGLContext eglContext;
        if (sharedContext != null && sharedContext == EGL10.EGL_NO_CONTEXT) {
            throw new RuntimeException("Invalid sharedContext");
        }
        int[] contextAttributes = {EGL_CONTEXT_CLIENT_VERSION, openGlesVersion, 12344};
        EGLContext rootContext = sharedContext == null ? EGL10.EGL_NO_CONTEXT : sharedContext;
        synchronized (EglBase.lock) {
            eglContext = this.egl.eglCreateContext(eglDisplay, eglConfig, rootContext, contextAttributes);
        }
        if (eglContext == EGL10.EGL_NO_CONTEXT) {
            throw new RuntimeException("Failed to create EGL context: 0x" + Integer.toHexString(this.egl.eglGetError()));
        }
        return eglContext;
    }

    /* loaded from: classes5.dex */
    private class FakeSurfaceHolder implements SurfaceHolder {
        private final Surface surface;

        FakeSurfaceHolder(Surface surface) {
            EglBase10Impl.this = r1;
            this.surface = surface;
        }

        @Override // android.view.SurfaceHolder
        public void addCallback(SurfaceHolder.Callback callback) {
        }

        @Override // android.view.SurfaceHolder
        public void removeCallback(SurfaceHolder.Callback callback) {
        }

        @Override // android.view.SurfaceHolder
        public boolean isCreating() {
            return false;
        }

        @Override // android.view.SurfaceHolder
        @Deprecated
        public void setType(int i) {
        }

        @Override // android.view.SurfaceHolder
        public void setFixedSize(int i, int i2) {
        }

        @Override // android.view.SurfaceHolder
        public void setSizeFromLayout() {
        }

        @Override // android.view.SurfaceHolder
        public void setFormat(int i) {
        }

        @Override // android.view.SurfaceHolder
        public void setKeepScreenOn(boolean b) {
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockCanvas() {
            return null;
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockCanvas(Rect rect) {
            return null;
        }

        @Override // android.view.SurfaceHolder
        public void unlockCanvasAndPost(Canvas canvas) {
        }

        @Override // android.view.SurfaceHolder
        public Rect getSurfaceFrame() {
            return null;
        }

        @Override // android.view.SurfaceHolder
        public Surface getSurface() {
            return this.surface;
        }
    }
}
