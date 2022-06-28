package org.telegram.messenger.camera;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
/* loaded from: classes4.dex */
public class CameraSession {
    public static final int ORIENTATION_HYSTERESIS = 5;
    protected CameraInfo cameraInfo;
    private String currentFlashMode;
    private int currentOrientation;
    private float currentZoom;
    private boolean destroyed;
    private int diffOrientation;
    private int displayOrientation;
    private boolean initied;
    private boolean isRound;
    private boolean isVideo;
    private int jpegOrientation;
    private int maxZoom;
    private boolean meteringAreaSupported;
    private boolean optimizeForBarcode;
    private OrientationEventListener orientationEventListener;
    private final int pictureFormat;
    private final Size pictureSize;
    private final Size previewSize;
    private boolean sameTakePictureOrientation;
    private boolean useTorch;
    private int lastOrientation = -1;
    private int lastDisplayOrientation = -1;
    private boolean flipFront = true;
    private int infoCameraId = -1;
    Camera.CameraInfo info = new Camera.CameraInfo();
    private Camera.AutoFocusCallback autoFocusCallback = CameraSession$$ExternalSyntheticLambda0.INSTANCE;

    public static /* synthetic */ void lambda$new$0(boolean success, Camera camera) {
    }

    public CameraSession(CameraInfo info, Size preview, Size picture, int format, boolean round) {
        this.previewSize = preview;
        this.pictureSize = picture;
        this.pictureFormat = format;
        this.cameraInfo = info;
        this.isRound = round;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0);
        this.currentFlashMode = sharedPreferences.getString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", "off");
        OrientationEventListener orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) { // from class: org.telegram.messenger.camera.CameraSession.1
            @Override // android.view.OrientationEventListener
            public void onOrientationChanged(int orientation) {
                if (CameraSession.this.orientationEventListener == null || !CameraSession.this.initied || orientation == -1) {
                    return;
                }
                CameraSession cameraSession = CameraSession.this;
                cameraSession.jpegOrientation = cameraSession.roundOrientation(orientation, cameraSession.jpegOrientation);
                WindowManager mgr = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
                int rotation = mgr.getDefaultDisplay().getRotation();
                if (CameraSession.this.lastOrientation != CameraSession.this.jpegOrientation || rotation != CameraSession.this.lastDisplayOrientation) {
                    if (!CameraSession.this.isVideo) {
                        CameraSession.this.configurePhotoCamera();
                    }
                    CameraSession.this.lastDisplayOrientation = rotation;
                    CameraSession cameraSession2 = CameraSession.this;
                    cameraSession2.lastOrientation = cameraSession2.jpegOrientation;
                }
            }
        };
        this.orientationEventListener = orientationEventListener;
        if (orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
            return;
        }
        this.orientationEventListener.disable();
        this.orientationEventListener = null;
    }

    private void updateCameraInfo() {
        if (this.infoCameraId != this.cameraInfo.getCameraId()) {
            int cameraId = this.cameraInfo.getCameraId();
            this.infoCameraId = cameraId;
            Camera.getCameraInfo(cameraId, this.info);
        }
    }

    public int roundOrientation(int orientation, int orientationHistory) {
        int dist;
        if (orientationHistory == -1) {
            dist = 1;
        } else {
            int dist2 = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist2, 360 - dist2) >= 50 ? 1 : 0;
        }
        if (dist != 0) {
            return (((orientation + 45) / 90) * 90) % 360;
        }
        return orientationHistory;
    }

    public void setOptimizeForBarcode(boolean value) {
        this.optimizeForBarcode = value;
        configurePhotoCamera();
    }

    public void checkFlashMode(String mode) {
        ArrayList<String> modes = CameraController.getInstance().availableFlashModes;
        if (modes.contains(this.currentFlashMode)) {
            return;
        }
        this.currentFlashMode = mode;
        configurePhotoCamera();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0);
        sharedPreferences.edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
    }

    public void setCurrentFlashMode(String mode) {
        this.currentFlashMode = mode;
        configurePhotoCamera();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0);
        sharedPreferences.edit().putString(this.cameraInfo.frontCamera != 0 ? "flashMode_front" : "flashMode", mode).commit();
    }

    public void setTorchEnabled(boolean enabled) {
        try {
            this.currentFlashMode = enabled ? "torch" : "off";
            configurePhotoCamera();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public String getCurrentFlashMode() {
        return this.currentFlashMode;
    }

    public String getNextFlashMode() {
        ArrayList<String> modes = CameraController.getInstance().availableFlashModes;
        for (int a = 0; a < modes.size(); a++) {
            String mode = modes.get(a);
            if (mode.equals(this.currentFlashMode)) {
                if (a < modes.size() - 1) {
                    return modes.get(a + 1);
                } else {
                    return modes.get(0);
                }
            }
        }
        return this.currentFlashMode;
    }

    public void setInitied() {
        this.initied = true;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public int getCurrentOrientation() {
        return this.currentOrientation;
    }

    public boolean isFlipFront() {
        return this.flipFront;
    }

    public void setFlipFront(boolean value) {
        this.flipFront = value;
    }

    public int getWorldAngle() {
        return this.diffOrientation;
    }

    public boolean isSameTakePictureOrientation() {
        return this.sameTakePictureOrientation;
    }

    public void configureRoundCamera(boolean initial) {
        try {
            this.isVideo = true;
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                Camera.Parameters params = null;
                try {
                    params = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                updateCameraInfo();
                updateRotation();
                if (params != null) {
                    if (initial && BuildVars.LOGS_ENABLED) {
                        FileLog.d("set preview size = " + this.previewSize.getWidth() + " " + this.previewSize.getHeight());
                    }
                    params.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    if (initial && BuildVars.LOGS_ENABLED) {
                        FileLog.d("set picture size = " + this.pictureSize.getWidth() + " " + this.pictureSize.getHeight());
                    }
                    params.setPictureSize(this.pictureSize.getWidth(), this.pictureSize.getHeight());
                    params.setPictureFormat(this.pictureFormat);
                    params.setRecordingHint(true);
                    this.maxZoom = params.getMaxZoom();
                    if (params.getSupportedFocusModes().contains("continuous-video")) {
                        params.setFocusMode("continuous-video");
                    } else if (params.getSupportedFocusModes().contains("auto")) {
                        params.setFocusMode("auto");
                    }
                    int outputOrientation = 0;
                    if (this.jpegOrientation != -1) {
                        if (this.info.facing == 1) {
                            outputOrientation = ((this.info.orientation - this.jpegOrientation) + 360) % 360;
                        } else {
                            outputOrientation = (this.info.orientation + this.jpegOrientation) % 360;
                        }
                    }
                    try {
                        params.setRotation(outputOrientation);
                        boolean z = false;
                        if (this.info.facing == 1) {
                            if ((360 - this.displayOrientation) % 360 == outputOrientation) {
                                z = true;
                            }
                            this.sameTakePictureOrientation = z;
                        } else {
                            if (this.displayOrientation == outputOrientation) {
                                z = true;
                            }
                            this.sameTakePictureOrientation = z;
                        }
                    } catch (Exception e2) {
                    }
                    params.setFlashMode("off");
                    params.setZoom((int) (this.currentZoom * this.maxZoom));
                    try {
                        camera.setParameters(params);
                        if (params.getMaxNumMeteringAreas() > 0) {
                            this.meteringAreaSupported = true;
                        }
                    } catch (Exception e3) {
                        throw new RuntimeException(e3);
                    }
                }
            }
        } catch (Throwable e4) {
            FileLog.e(e4);
        }
    }

    public void updateRotation() {
        int degrees;
        int temp;
        if (this.cameraInfo == null) {
            return;
        }
        try {
            updateCameraInfo();
            Camera camera = this.destroyed ? null : this.cameraInfo.camera;
            this.displayOrientation = getDisplayOrientation(this.info, true);
            if ("samsung".equals(Build.MANUFACTURER) && "sf2wifixx".equals(Build.PRODUCT)) {
                degrees = 0;
            } else {
                int degrees2 = 0;
                int temp2 = this.displayOrientation;
                switch (temp2) {
                    case 0:
                        degrees2 = 0;
                        break;
                    case 1:
                        degrees2 = 90;
                        break;
                    case 2:
                        degrees2 = 180;
                        break;
                    case 3:
                        degrees2 = 270;
                        break;
                }
                if (this.info.orientation % 90 != 0) {
                    this.info.orientation = 0;
                }
                if (this.info.facing == 1) {
                    int temp3 = (this.info.orientation + degrees2) % 360;
                    temp = (360 - temp3) % 360;
                } else {
                    temp = ((this.info.orientation - degrees2) + 360) % 360;
                }
                degrees = temp;
            }
            this.currentOrientation = degrees;
            if (camera != null) {
                try {
                    camera.setDisplayOrientation(degrees);
                } catch (Throwable th) {
                }
            }
            int i = this.currentOrientation - this.displayOrientation;
            this.diffOrientation = i;
            if (i < 0) {
                this.diffOrientation = i + 360;
            }
        } catch (Throwable throwable) {
            FileLog.e(throwable);
        }
    }

    public void configurePhotoCamera() {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                Camera.Parameters params = null;
                try {
                    params = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                updateCameraInfo();
                updateRotation();
                int i = this.currentOrientation - this.displayOrientation;
                this.diffOrientation = i;
                if (i < 0) {
                    this.diffOrientation = i + 360;
                }
                if (params != null) {
                    params.setPreviewSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    params.setPictureSize(this.pictureSize.getWidth(), this.pictureSize.getHeight());
                    params.setPictureFormat(this.pictureFormat);
                    params.setJpegQuality(100);
                    params.setJpegThumbnailQuality(100);
                    int maxZoom = params.getMaxZoom();
                    this.maxZoom = maxZoom;
                    params.setZoom((int) (this.currentZoom * maxZoom));
                    if (this.optimizeForBarcode) {
                        List<String> modes = params.getSupportedSceneModes();
                        if (modes != null && modes.contains("barcode")) {
                            params.setSceneMode("barcode");
                        }
                        if (params.getSupportedFocusModes().contains("continuous-video")) {
                            params.setFocusMode("continuous-video");
                        }
                    } else if (params.getSupportedFocusModes().contains("continuous-picture")) {
                        params.setFocusMode("continuous-picture");
                    }
                    int outputOrientation = 0;
                    boolean z = true;
                    if (this.jpegOrientation != -1) {
                        if (this.info.facing == 1) {
                            outputOrientation = ((this.info.orientation - this.jpegOrientation) + 360) % 360;
                        } else {
                            outputOrientation = (this.info.orientation + this.jpegOrientation) % 360;
                        }
                    }
                    try {
                        params.setRotation(outputOrientation);
                        if (this.info.facing == 1) {
                            if ((360 - this.displayOrientation) % 360 != outputOrientation) {
                                z = false;
                            }
                            this.sameTakePictureOrientation = z;
                        } else {
                            if (this.displayOrientation != outputOrientation) {
                                z = false;
                            }
                            this.sameTakePictureOrientation = z;
                        }
                    } catch (Exception e2) {
                    }
                    params.setFlashMode(this.useTorch ? "torch" : this.currentFlashMode);
                    try {
                        camera.setParameters(params);
                    } catch (Exception e3) {
                    }
                }
            }
        } catch (Throwable e4) {
            FileLog.e(e4);
        }
    }

    public void focusToRect(Rect focusRect, Rect meteringRect) {
        try {
            Camera camera = this.cameraInfo.camera;
            if (camera != null) {
                camera.cancelAutoFocus();
                Camera.Parameters parameters = null;
                try {
                    parameters = camera.getParameters();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (parameters != null) {
                    parameters.setFocusMode("auto");
                    ArrayList<Camera.Area> meteringAreas = new ArrayList<>();
                    meteringAreas.add(new Camera.Area(focusRect, 1000));
                    parameters.setFocusAreas(meteringAreas);
                    if (this.meteringAreaSupported) {
                        ArrayList<Camera.Area> meteringAreas2 = new ArrayList<>();
                        meteringAreas2.add(new Camera.Area(meteringRect, 1000));
                        parameters.setMeteringAreas(meteringAreas2);
                    }
                    try {
                        camera.setParameters(parameters);
                        camera.autoFocus(this.autoFocusCallback);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    protected int getMaxZoom() {
        return this.maxZoom;
    }

    public void onStartRecord() {
        this.isVideo = true;
    }

    public void setZoom(float value) {
        this.currentZoom = value;
        if (this.isVideo && "on".equals(this.currentFlashMode)) {
            this.useTorch = true;
        }
        if (this.isRound) {
            configureRoundCamera(false);
        } else {
            configurePhotoCamera();
        }
    }

    public void configureRecorder(int quality, MediaRecorder recorder) {
        updateCameraInfo();
        int outputOrientation = 0;
        if (this.jpegOrientation != -1) {
            if (this.info.facing == 1) {
                outputOrientation = ((this.info.orientation - this.jpegOrientation) + 360) % 360;
            } else {
                outputOrientation = (this.info.orientation + this.jpegOrientation) % 360;
            }
        }
        recorder.setOrientationHint(outputOrientation);
        int highProfile = getHigh();
        boolean canGoHigh = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, highProfile);
        boolean canGoLow = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
        if (canGoHigh && (quality == 1 || !canGoLow)) {
            recorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, highProfile));
        } else if (canGoLow) {
            recorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
        } else {
            throw new IllegalStateException("cannot find valid CamcorderProfile");
        }
        this.isVideo = true;
    }

    public void stopVideoRecording() {
        this.isVideo = false;
        this.useTorch = false;
        configurePhotoCamera();
    }

    private int getHigh() {
        if ("LGE".equals(Build.MANUFACTURER) && "g3_tmo_us".equals(Build.PRODUCT)) {
            return 4;
        }
        return 1;
    }

    private int getDisplayOrientation(Camera.CameraInfo info, boolean isStillCapture) {
        WindowManager mgr = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        int rotation = mgr.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
                break;
        }
        if (info.facing == 1) {
            int displayOrientation = (info.orientation + degrees) % 360;
            int displayOrientation2 = (360 - displayOrientation) % 360;
            if (!isStillCapture && displayOrientation2 == 90) {
                displayOrientation2 = 270;
            }
            if (!isStillCapture && "Huawei".equals(Build.MANUFACTURER) && "angler".equals(Build.PRODUCT) && displayOrientation2 == 270) {
                return 90;
            }
            return displayOrientation2;
        }
        int displayOrientation3 = ((info.orientation - degrees) + 360) % 360;
        return displayOrientation3;
    }

    public int getDisplayOrientation() {
        try {
            updateCameraInfo();
            return getDisplayOrientation(this.info, true);
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback callback) {
        this.cameraInfo.camera.setPreviewCallback(callback);
    }

    public void setOneShotPreviewCallback(Camera.PreviewCallback callback) {
        CameraInfo cameraInfo = this.cameraInfo;
        if (cameraInfo != null && cameraInfo.camera != null) {
            try {
                this.cameraInfo.camera.setOneShotPreviewCallback(callback);
            } catch (Exception e) {
            }
        }
    }

    public void destroy() {
        this.initied = false;
        this.destroyed = true;
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }
}
