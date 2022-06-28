package org.telegram.messenger.voip;

import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Display;
import android.view.WindowManager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.voiceengine.WebRtcAudioRecord;
/* loaded from: classes.dex */
public class VideoCapturerDevice {
    private static final int CAPTURE_FPS = 30;
    private static final int CAPTURE_HEIGHT;
    private static final int CAPTURE_WIDTH;
    public static EglBase eglBase;
    private static VideoCapturerDevice[] instance;
    public static Intent mediaProjectionPermissionResultData;
    private int currentHeight;
    private int currentWidth;
    private Handler handler;
    private CapturerObserver nativeCapturerObserver;
    private long nativePtr;
    private HandlerThread thread;
    private VideoCapturer videoCapturer;
    private SurfaceTextureHelper videoCapturerSurfaceTextureHelper;

    private static native CapturerObserver nativeGetJavaVideoCapturerObserver(long j);

    static {
        CAPTURE_WIDTH = Build.VERSION.SDK_INT <= 19 ? 480 : 1280;
        CAPTURE_HEIGHT = Build.VERSION.SDK_INT <= 19 ? GroupCallActivity.TABLET_LIST_SIZE : 720;
        instance = new VideoCapturerDevice[2];
    }

    public VideoCapturerDevice(final boolean screencast) {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);
        Logging.d("VideoCapturerDevice", "device model = " + Build.MANUFACTURER + Build.MODEL);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1276lambda$new$0$orgtelegrammessengervoipVideoCapturerDevice(screencast);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1276lambda$new$0$orgtelegrammessengervoipVideoCapturerDevice(boolean screencast) {
        if (eglBase == null) {
            eglBase = EglBase.CC.create(null, EglBase.CONFIG_PLAIN);
        }
        instance[screencast ? 1 : 0] = this;
        HandlerThread handlerThread = new HandlerThread("CallThread");
        this.thread = handlerThread;
        handlerThread.start();
        this.handler = new Handler(this.thread.getLooper());
    }

    public static void checkScreenCapturerSize() {
        if (instance[1] == null) {
            return;
        }
        final Point size = getScreenCaptureSize();
        if (instance[1].currentWidth != size.x || instance[1].currentHeight != size.y) {
            instance[1].currentWidth = size.x;
            instance[1].currentHeight = size.y;
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            VideoCapturerDevice device = videoCapturerDeviceArr[1];
            videoCapturerDeviceArr[1].handler.post(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    VideoCapturerDevice.lambda$checkScreenCapturerSize$1(VideoCapturerDevice.this, size);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$checkScreenCapturerSize$1(VideoCapturerDevice device, Point size) {
        VideoCapturer videoCapturer = device.videoCapturer;
        if (videoCapturer != null) {
            videoCapturer.changeCaptureFormat(size.x, size.y, 30);
        }
    }

    private static Point getScreenCaptureSize() {
        float aspect;
        WindowManager wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        if (size.x > size.y) {
            aspect = size.y / size.x;
        } else {
            aspect = size.x / size.y;
        }
        int dx = -1;
        int dy = -1;
        int a = 1;
        while (true) {
            if (a > 100) {
                break;
            }
            float val = a * aspect;
            if (val != ((int) val)) {
                a++;
            } else if (size.x > size.y) {
                dx = a;
                dy = (int) (a * aspect);
            } else {
                dy = a;
                dx = (int) (a * aspect);
            }
        }
        if (dx != -1 && aspect != 1.0f) {
            while (true) {
                if (size.x <= 1000 && size.y <= 1000 && size.x % 4 == 0 && size.y % 4 == 0) {
                    break;
                }
                size.x -= dx;
                size.y -= dy;
                if (size.x < 800 && size.y < 800) {
                    dx = -1;
                    break;
                }
            }
        }
        if (dx == -1 || aspect == 1.0f) {
            float scale = Math.max(size.x / 970.0f, size.y / 970.0f);
            size.x = ((int) Math.ceil((size.x / scale) / 4.0f)) * 4;
            size.y = ((int) Math.ceil((size.y / scale) / 4.0f)) * 4;
        }
        return size;
    }

    private void init(final long ptr, final String deviceName) {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1275lambda$init$5$orgtelegrammessengervoipVideoCapturerDevice(ptr, deviceName);
            }
        });
    }

    /* renamed from: lambda$init$5$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1275lambda$init$5$orgtelegrammessengervoipVideoCapturerDevice(long ptr, String deviceName) {
        if (eglBase == null) {
            return;
        }
        this.nativePtr = ptr;
        if ("screen".equals(deviceName)) {
            if (Build.VERSION.SDK_INT >= 21 && this.videoCapturer == null) {
                this.videoCapturer = new ScreenCapturerAndroid(mediaProjectionPermissionResultData, new AnonymousClass1());
                final Point size = getScreenCaptureSize();
                this.currentWidth = size.x;
                this.currentHeight = size.y;
                this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("ScreenCapturerThread", eglBase.getEglBaseContext());
                this.handler.post(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoCapturerDevice.this.m1272lambda$init$2$orgtelegrammessengervoipVideoCapturerDevice(size);
                    }
                });
                return;
            }
            return;
        }
        CameraEnumerator enumerator = Camera2Enumerator.isSupported(ApplicationLoader.applicationContext) ? new Camera2Enumerator(ApplicationLoader.applicationContext) : new Camera1Enumerator();
        int index = -1;
        String[] names = enumerator.getDeviceNames();
        int a = 0;
        while (true) {
            if (a >= names.length) {
                break;
            }
            boolean isFrontFace = enumerator.isFrontFacing(names[a]);
            if (isFrontFace != "front".equals(deviceName)) {
                a++;
            } else {
                index = a;
                break;
            }
        }
        if (index == -1) {
            return;
        }
        final String cameraName = names[index];
        if (this.videoCapturer == null) {
            this.videoCapturer = enumerator.createCapturer(cameraName, new AnonymousClass2());
            this.videoCapturerSurfaceTextureHelper = SurfaceTextureHelper.create("VideoCapturerThread", eglBase.getEglBaseContext());
            this.handler.post(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VideoCapturerDevice.this.m1273lambda$init$3$orgtelegrammessengervoipVideoCapturerDevice();
                }
            });
            return;
        }
        this.handler.post(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1274lambda$init$4$orgtelegrammessengervoipVideoCapturerDevice(cameraName);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.voip.VideoCapturerDevice$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends MediaProjection.Callback {
        AnonymousClass1() {
            VideoCapturerDevice.this = this$0;
        }

        @Override // android.media.projection.MediaProjection.Callback
        public void onStop() {
            AndroidUtilities.runOnUIThread(VideoCapturerDevice$1$$ExternalSyntheticLambda0.INSTANCE);
        }

        public static /* synthetic */ void lambda$onStop$0() {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().stopScreenCapture();
            }
        }
    }

    /* renamed from: lambda$init$2$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1272lambda$init$2$orgtelegrammessengervoipVideoCapturerDevice(Point size) {
        if (this.videoCapturerSurfaceTextureHelper != null) {
            long j = this.nativePtr;
            if (j == 0) {
                return;
            }
            this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(j);
            this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
            this.videoCapturer.startCapture(size.x, size.y, 30);
            WebRtcAudioRecord audioRecord = WebRtcAudioRecord.Instance;
            if (audioRecord != null) {
                audioRecord.initDeviceAudioRecord(((ScreenCapturerAndroid) this.videoCapturer).getMediaProjection());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.voip.VideoCapturerDevice$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 implements CameraVideoCapturer.CameraEventsHandler {
        AnonymousClass2() {
            VideoCapturerDevice.this = this$0;
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraEventsHandler
        public void onCameraError(String errorDescription) {
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraEventsHandler
        public void onCameraDisconnected() {
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraEventsHandler
        public void onCameraFreezed(String errorDescription) {
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraEventsHandler
        public void onCameraOpening(String cameraName) {
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraEventsHandler
        public void onFirstFrameAvailable() {
            AndroidUtilities.runOnUIThread(VideoCapturerDevice$2$$ExternalSyntheticLambda0.INSTANCE);
        }

        public static /* synthetic */ void lambda$onFirstFrameAvailable$0() {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().onCameraFirstFrameAvailable();
            }
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraEventsHandler
        public void onCameraClosed() {
        }
    }

    /* renamed from: lambda$init$3$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1273lambda$init$3$orgtelegrammessengervoipVideoCapturerDevice() {
        if (this.videoCapturerSurfaceTextureHelper == null) {
            return;
        }
        this.nativeCapturerObserver = nativeGetJavaVideoCapturerObserver(this.nativePtr);
        this.videoCapturer.initialize(this.videoCapturerSurfaceTextureHelper, ApplicationLoader.applicationContext, this.nativeCapturerObserver);
        this.videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.voip.VideoCapturerDevice$3 */
    /* loaded from: classes4.dex */
    public class AnonymousClass3 implements CameraVideoCapturer.CameraSwitchHandler {
        AnonymousClass3() {
            VideoCapturerDevice.this = this$0;
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraSwitchHandler
        public void onCameraSwitchDone(final boolean isFrontCamera) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VideoCapturerDevice.AnonymousClass3.lambda$onCameraSwitchDone$0(isFrontCamera);
                }
            });
        }

        public static /* synthetic */ void lambda$onCameraSwitchDone$0(boolean isFrontCamera) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().setSwitchingCamera(false, isFrontCamera);
            }
        }

        @Override // org.webrtc.CameraVideoCapturer.CameraSwitchHandler
        public void onCameraSwitchError(String errorDescription) {
        }
    }

    /* renamed from: lambda$init$4$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1274lambda$init$4$orgtelegrammessengervoipVideoCapturerDevice(String cameraName) {
        ((CameraVideoCapturer) this.videoCapturer).switchCamera(new AnonymousClass3(), cameraName);
    }

    public static MediaProjection getMediaProjection() {
        VideoCapturerDevice[] videoCapturerDeviceArr = instance;
        if (videoCapturerDeviceArr[1] == null) {
            return null;
        }
        return ((ScreenCapturerAndroid) videoCapturerDeviceArr[1].videoCapturer).getMediaProjection();
    }

    private void onAspectRatioRequested(float aspectRatio) {
    }

    private void onStateChanged(final long ptr, final int state) {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1280x7b06e05b(ptr, state);
            }
        });
    }

    /* renamed from: lambda$onStateChanged$7$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1280x7b06e05b(long ptr, final int state) {
        if (this.nativePtr != ptr) {
            return;
        }
        this.handler.post(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1279x7b7d465a(state);
            }
        });
    }

    /* renamed from: lambda$onStateChanged$6$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1279x7b7d465a(int state) {
        VideoCapturer videoCapturer = this.videoCapturer;
        if (videoCapturer == null) {
            return;
        }
        if (state == 2) {
            videoCapturer.startCapture(CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
            return;
        }
        try {
            videoCapturer.stopCapture();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void onDestroy() {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        this.nativePtr = 0L;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1278x8c9ffc3c();
            }
        });
    }

    /* renamed from: lambda$onDestroy$9$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1278x8c9ffc3c() {
        int a = 0;
        while (true) {
            VideoCapturerDevice[] videoCapturerDeviceArr = instance;
            if (a >= videoCapturerDeviceArr.length) {
                break;
            } else if (videoCapturerDeviceArr[a] != this) {
                a++;
            } else {
                videoCapturerDeviceArr[a] = null;
                break;
            }
        }
        this.handler.post(new Runnable() { // from class: org.telegram.messenger.voip.VideoCapturerDevice$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                VideoCapturerDevice.this.m1277x8d16623b();
            }
        });
        try {
            this.thread.quitSafely();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$onDestroy$8$org-telegram-messenger-voip-VideoCapturerDevice */
    public /* synthetic */ void m1277x8d16623b() {
        WebRtcAudioRecord audioRecord;
        if ((this.videoCapturer instanceof ScreenCapturerAndroid) && (audioRecord = WebRtcAudioRecord.Instance) != null) {
            audioRecord.stopDeviceAudioRecord();
        }
        VideoCapturer videoCapturer = this.videoCapturer;
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
                this.videoCapturer.dispose();
                this.videoCapturer = null;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        SurfaceTextureHelper surfaceTextureHelper = this.videoCapturerSurfaceTextureHelper;
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            this.videoCapturerSurfaceTextureHelper = null;
        }
    }

    private EglBase.Context getSharedEGLContext() {
        if (eglBase == null) {
            eglBase = EglBase.CC.create(null, EglBase.CONFIG_PLAIN);
        }
        EglBase eglBase2 = eglBase;
        if (eglBase2 != null) {
            return eglBase2.getEglBaseContext();
        }
        return null;
    }

    public static EglBase getEglBase() {
        if (eglBase == null) {
            eglBase = EglBase.CC.create(null, EglBase.CONFIG_PLAIN);
        }
        return eglBase;
    }
}
