package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.webrtc.Camera1Session;
import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraSession;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
/* loaded from: classes5.dex */
public class Camera1Session implements CameraSession {
    private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
    private static final String TAG = "Camera1Session";
    private final Context applicationContext;
    private final Camera camera;
    private final int cameraId;
    private final CameraEnumerationAndroid.CaptureFormat captureFormat;
    private final boolean captureToTexture;
    private final long constructionTimeNs;
    private final CameraSession.Events events;
    private boolean firstFrameReported;
    private final Camera.CameraInfo info;
    private SessionState state;
    private final SurfaceTextureHelper surfaceTextureHelper;
    private static final Histogram camera1StartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StartTimeMs", 1, 10000, 50);
    private static final Histogram camera1StopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StopTimeMs", 1, 10000, 50);
    private static final Histogram camera1ResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.Camera1.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    private final Handler cameraThreadHandler = new Handler();
    private OrientationHelper orientationHelper = new OrientationHelper();

    /* loaded from: classes5.dex */
    public enum SessionState {
        RUNNING,
        STOPPED
    }

    public static void create(CameraSession.CreateSessionCallback callback, CameraSession.Events events, boolean captureToTexture, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, int cameraId, int width, int height, int framerate) {
        long constructionTimeNs = System.nanoTime();
        Logging.d(TAG, "Open camera " + cameraId);
        events.onCameraOpening();
        try {
            Camera camera = Camera.open(cameraId);
            if (camera == null) {
                CameraSession.FailureType failureType = CameraSession.FailureType.ERROR;
                callback.onFailure(failureType, "android.hardware.Camera.open returned null for camera id = " + cameraId);
                return;
            }
            try {
                camera.setPreviewTexture(surfaceTextureHelper.getSurfaceTexture());
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, info);
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    CameraEnumerationAndroid.CaptureFormat captureFormat = findClosestCaptureFormat(parameters, width, height, framerate);
                    Size pictureSize = findClosestPictureSize(parameters, width, height);
                    updateCameraParameters(camera, parameters, captureFormat, pictureSize, captureToTexture);
                    if (!captureToTexture) {
                        int frameSize = captureFormat.frameSize();
                        for (int i = 0; i < 3; i++) {
                            ByteBuffer buffer = ByteBuffer.allocateDirect(frameSize);
                            camera.addCallbackBuffer(buffer.array());
                        }
                    }
                    camera.setDisplayOrientation(0);
                    callback.onDone(new Camera1Session(events, captureToTexture, applicationContext, surfaceTextureHelper, cameraId, camera, info, captureFormat, constructionTimeNs));
                } catch (RuntimeException e) {
                    camera.release();
                    callback.onFailure(CameraSession.FailureType.ERROR, e.getMessage());
                }
            } catch (IOException | RuntimeException e2) {
                camera.release();
                callback.onFailure(CameraSession.FailureType.ERROR, e2.getMessage());
            }
        } catch (RuntimeException e3) {
            callback.onFailure(CameraSession.FailureType.ERROR, e3.getMessage());
        }
    }

    private static void updateCameraParameters(Camera camera, Camera.Parameters parameters, CameraEnumerationAndroid.CaptureFormat captureFormat, Size pictureSize, boolean captureToTexture) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        parameters.setPreviewFpsRange(captureFormat.framerate.min, captureFormat.framerate.max);
        parameters.setPreviewSize(captureFormat.width, captureFormat.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        if (!captureToTexture) {
            captureFormat.getClass();
            parameters.setPreviewFormat(17);
        }
        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }
        if (focusModes.contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
        camera.setParameters(parameters);
    }

    private static CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat(Camera.Parameters parameters, int width, int height, int framerate) {
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> supportedFramerates = Camera1Enumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        Logging.d(TAG, "Available fps ranges: " + supportedFramerates);
        CameraEnumerationAndroid.CaptureFormat.FramerateRange fpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(supportedFramerates, framerate);
        Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPreviewSizes()), width, height);
        CameraEnumerationAndroid.reportCameraResolution(camera1ResolutionHistogram, previewSize);
        return new CameraEnumerationAndroid.CaptureFormat(previewSize.width, previewSize.height, fpsRange);
    }

    private static Size findClosestPictureSize(Camera.Parameters parameters, int width, int height) {
        return CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(parameters.getSupportedPictureSizes()), width, height);
    }

    private Camera1Session(CameraSession.Events events, boolean captureToTexture, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, int cameraId, Camera camera, Camera.CameraInfo info, CameraEnumerationAndroid.CaptureFormat captureFormat, long constructionTimeNs) {
        Logging.d(TAG, "Create new camera1 session on camera " + cameraId);
        this.events = events;
        this.captureToTexture = captureToTexture;
        this.applicationContext = applicationContext;
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.cameraId = cameraId;
        this.camera = camera;
        this.info = info;
        this.captureFormat = captureFormat;
        this.constructionTimeNs = constructionTimeNs;
        surfaceTextureHelper.setTextureSize(captureFormat.width, captureFormat.height);
        startCapturing();
    }

    @Override // org.webrtc.CameraSession
    public void stop() {
        Logging.d(TAG, "Stop camera1 session on camera " + this.cameraId);
        checkIsOnCameraThread();
        if (this.state != SessionState.STOPPED) {
            long stopStartTime = System.nanoTime();
            stopInternal();
            int stopTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - stopStartTime);
            camera1StopTimeMsHistogram.addSample(stopTimeMs);
        }
    }

    private void startCapturing() {
        Logging.d(TAG, "Start capturing");
        checkIsOnCameraThread();
        this.state = SessionState.RUNNING;
        this.camera.setErrorCallback(new Camera.ErrorCallback() { // from class: org.webrtc.Camera1Session.1
            @Override // android.hardware.Camera.ErrorCallback
            public void onError(int error, Camera camera) {
                String errorMessage;
                if (error == 100) {
                    errorMessage = "Camera server died!";
                } else {
                    errorMessage = "Camera error: " + error;
                }
                Logging.e(Camera1Session.TAG, errorMessage);
                Camera1Session.this.stopInternal();
                if (error == 2) {
                    Camera1Session.this.events.onCameraDisconnected(Camera1Session.this);
                } else {
                    Camera1Session.this.events.onCameraError(Camera1Session.this, errorMessage);
                }
            }
        });
        if (this.captureToTexture) {
            listenForTextureFrames();
        } else {
            listenForBytebufferFrames();
        }
        this.orientationHelper.start();
        try {
            this.camera.startPreview();
        } catch (RuntimeException e) {
            stopInternal();
            this.events.onCameraError(this, e.getMessage());
        }
    }

    public void stopInternal() {
        Logging.d(TAG, "Stop internal");
        checkIsOnCameraThread();
        if (this.state == SessionState.STOPPED) {
            Logging.d(TAG, "Camera is already stopped");
            return;
        }
        this.state = SessionState.STOPPED;
        this.surfaceTextureHelper.stopListening();
        this.camera.stopPreview();
        this.camera.release();
        this.events.onCameraClosed(this);
        OrientationHelper orientationHelper = this.orientationHelper;
        if (orientationHelper != null) {
            orientationHelper.stop();
        }
        Logging.d(TAG, "Stop done");
    }

    private void listenForTextureFrames() {
        this.surfaceTextureHelper.startListening(new VideoSink() { // from class: org.webrtc.Camera1Session$$ExternalSyntheticLambda0
            @Override // org.webrtc.VideoSink
            public final void onFrame(VideoFrame videoFrame) {
                Camera1Session.this.m4826lambda$listenForTextureFrames$0$orgwebrtcCamera1Session(videoFrame);
            }

            @Override // org.webrtc.VideoSink
            public /* synthetic */ void setParentSink(VideoSink videoSink) {
                VideoSink.CC.$default$setParentSink(this, videoSink);
            }
        });
    }

    /* renamed from: lambda$listenForTextureFrames$0$org-webrtc-Camera1Session */
    public /* synthetic */ void m4826lambda$listenForTextureFrames$0$orgwebrtcCamera1Session(VideoFrame frame) {
        checkIsOnCameraThread();
        if (this.state != SessionState.RUNNING) {
            Logging.d(TAG, "Texture frame captured but camera is no longer running.");
            return;
        }
        boolean z = true;
        if (!this.firstFrameReported) {
            int startTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.constructionTimeNs);
            camera1StartTimeMsHistogram.addSample(startTimeMs);
            this.firstFrameReported = true;
        }
        TextureBufferImpl textureBufferImpl = (TextureBufferImpl) frame.getBuffer();
        if (this.info.facing != 1) {
            z = false;
        }
        VideoFrame modifiedFrame = new VideoFrame(CameraSession.CC.createTextureBufferWithModifiedTransformMatrix(textureBufferImpl, z, 0), getFrameOrientation(), frame.getTimestampNs());
        this.events.onFrameCaptured(this, modifiedFrame);
        modifiedFrame.release();
    }

    /* renamed from: org.webrtc.Camera1Session$2 */
    /* loaded from: classes5.dex */
    public class AnonymousClass2 implements Camera.PreviewCallback {
        AnonymousClass2() {
            Camera1Session.this = this$0;
        }

        @Override // android.hardware.Camera.PreviewCallback
        public void onPreviewFrame(final byte[] data, Camera callbackCamera) {
            Camera1Session.this.checkIsOnCameraThread();
            if (callbackCamera == Camera1Session.this.camera) {
                if (Camera1Session.this.state != SessionState.RUNNING) {
                    Logging.d(Camera1Session.TAG, "Bytebuffer frame captured but camera is no longer running.");
                    return;
                }
                long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                if (!Camera1Session.this.firstFrameReported) {
                    int startTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Camera1Session.this.constructionTimeNs);
                    Camera1Session.camera1StartTimeMsHistogram.addSample(startTimeMs);
                    Camera1Session.this.firstFrameReported = true;
                }
                VideoFrame.Buffer frameBuffer = new NV21Buffer(data, Camera1Session.this.captureFormat.width, Camera1Session.this.captureFormat.height, new Runnable() { // from class: org.webrtc.Camera1Session$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Camera1Session.AnonymousClass2.this.m4828lambda$onPreviewFrame$1$orgwebrtcCamera1Session$2(data);
                    }
                });
                VideoFrame frame = new VideoFrame(frameBuffer, Camera1Session.this.getFrameOrientation(), captureTimeNs);
                Camera1Session.this.events.onFrameCaptured(Camera1Session.this, frame);
                frame.release();
                return;
            }
            Logging.e(Camera1Session.TAG, "Callback from a different camera. This should never happen.");
        }

        /* renamed from: lambda$onPreviewFrame$1$org-webrtc-Camera1Session$2 */
        public /* synthetic */ void m4828lambda$onPreviewFrame$1$orgwebrtcCamera1Session$2(final byte[] data) {
            Camera1Session.this.cameraThreadHandler.post(new Runnable() { // from class: org.webrtc.Camera1Session$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Camera1Session.AnonymousClass2.this.m4827lambda$onPreviewFrame$0$orgwebrtcCamera1Session$2(data);
                }
            });
        }

        /* renamed from: lambda$onPreviewFrame$0$org-webrtc-Camera1Session$2 */
        public /* synthetic */ void m4827lambda$onPreviewFrame$0$orgwebrtcCamera1Session$2(byte[] data) {
            if (Camera1Session.this.state == SessionState.RUNNING) {
                Camera1Session.this.camera.addCallbackBuffer(data);
            }
        }
    }

    private void listenForBytebufferFrames() {
        this.camera.setPreviewCallbackWithBuffer(new AnonymousClass2());
    }

    public int getFrameOrientation() {
        int rotation = this.orientationHelper.getOrientation();
        OrientationHelper.cameraOrientation = rotation;
        if (this.info.facing == 1) {
            rotation = 360 - rotation;
        }
        OrientationHelper.cameraRotation = rotation;
        return (this.info.orientation + rotation) % 360;
    }

    public void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }
}
