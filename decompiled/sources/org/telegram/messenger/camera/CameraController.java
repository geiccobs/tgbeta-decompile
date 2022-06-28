package org.telegram.messenger.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
/* loaded from: classes4.dex */
public class CameraController implements MediaRecorder.OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = 1;
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private boolean loadingCameras;
    private boolean mirrorRecorderVideo;
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    CameraView recordingCurrentCameraView;
    protected ArrayList<String> availableFlashModes = new ArrayList<>();
    private ArrayList<Runnable> onFinishCameraInitRunnables = new ArrayList<>();
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    /* loaded from: classes4.dex */
    public interface VideoTakeCallback {
        void onFinishVideoRecording(String str, long j);
    }

    public static CameraController getInstance() {
        CameraController localInstance = Instance;
        if (localInstance == null) {
            synchronized (CameraController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    CameraController cameraController = new CameraController();
                    localInstance = cameraController;
                    Instance = cameraController;
                }
            }
        }
        return localInstance;
    }

    public void cancelOnInitRunnable(Runnable onInitRunnable) {
        this.onFinishCameraInitRunnables.remove(onInitRunnable);
    }

    public void initCamera(Runnable onInitRunnable) {
        initCamera(onInitRunnable, false);
    }

    private void initCamera(final Runnable onInitRunnable, final boolean withDelay) {
        if (this.cameraInitied) {
            return;
        }
        if (onInitRunnable != null && !this.onFinishCameraInitRunnables.contains(onInitRunnable)) {
            this.onFinishCameraInitRunnables.add(onInitRunnable);
        }
        if (this.loadingCameras || this.cameraInitied) {
            return;
        }
        this.loadingCameras = true;
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.this.m1241x6e3d3dee(withDelay, onInitRunnable);
            }
        });
    }

    /* renamed from: lambda$initCamera$4$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1241x6e3d3dee(final boolean withDelay, final Runnable onInitRunnable) {
        String str;
        final Exception e;
        Camera.CameraInfo info;
        List<Camera.Size> list;
        String str2;
        CameraController cameraController = this;
        String str3 = "cameraCache";
        String str4 = "APP_PAUSED";
        try {
            if (cameraController.cameraInfos != null) {
                str = str4;
            } else {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                String cache = preferences.getString(str3, null);
                Comparator<Size> comparator = CameraController$$ExternalSyntheticLambda8.INSTANCE;
                ArrayList<CameraInfo> result = new ArrayList<>();
                if (cache != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(cache, 0));
                    int count = serializedData.readInt32(false);
                    int a = 0;
                    while (a < count) {
                        CameraInfo cameraInfo = new CameraInfo(serializedData.readInt32(false), serializedData.readInt32(false));
                        int pCount = serializedData.readInt32(false);
                        int b = 0;
                        while (b < pCount) {
                            cameraInfo.previewSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                            b++;
                            cache = cache;
                        }
                        String cache2 = cache;
                        int pCount2 = serializedData.readInt32(false);
                        for (int b2 = 0; b2 < pCount2; b2++) {
                            cameraInfo.pictureSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        result.add(cameraInfo);
                        Collections.sort(cameraInfo.previewSizes, comparator);
                        Collections.sort(cameraInfo.pictureSizes, comparator);
                        a++;
                        cache = cache2;
                    }
                    serializedData.cleanup();
                    str = str4;
                } else {
                    int count2 = Camera.getNumberOfCameras();
                    Camera.CameraInfo info2 = new Camera.CameraInfo();
                    int bufferSize = 4;
                    int cameraId = 0;
                    while (cameraId < count2) {
                        try {
                            Camera.getCameraInfo(cameraId, info2);
                            CameraInfo cameraInfo2 = new CameraInfo(cameraId, info2.facing);
                            if (ApplicationLoader.mainInterfacePaused && ApplicationLoader.externalInterfacePaused) {
                                throw new RuntimeException(str4);
                            }
                            Camera camera = Camera.open(cameraInfo2.getCameraId());
                            Camera.Parameters params = camera.getParameters();
                            List<Camera.Size> list2 = params.getSupportedPreviewSizes();
                            int a2 = 0;
                            while (true) {
                                info = info2;
                                str = str4;
                                if (a2 >= list2.size()) {
                                    break;
                                }
                                try {
                                    Camera.Size size = list2.get(a2);
                                    List<Camera.Size> list3 = list2;
                                    if (size.width == 1280) {
                                        try {
                                            if (size.height != 720) {
                                                str2 = str3;
                                                a2++;
                                                cameraController = this;
                                                info2 = info;
                                                str4 = str;
                                                list2 = list3;
                                                str3 = str2;
                                            }
                                        } catch (Exception e2) {
                                            e = e2;
                                            FileLog.e(e, !str.equals(e.getMessage()));
                                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda3
                                                @Override // java.lang.Runnable
                                                public final void run() {
                                                    CameraController.this.m1240x34729c0f(withDelay, e, onInitRunnable);
                                                }
                                            });
                                            return;
                                        }
                                    }
                                    if (size.height >= 2160 || size.width >= 2160) {
                                        str2 = str3;
                                    } else {
                                        str2 = str3;
                                        cameraInfo2.previewSizes.add(new Size(size.width, size.height));
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("preview size = " + size.width + " " + size.height);
                                        }
                                    }
                                    a2++;
                                    cameraController = this;
                                    info2 = info;
                                    str4 = str;
                                    list2 = list3;
                                    str3 = str2;
                                } catch (Exception e3) {
                                    e = e3;
                                    cameraController = this;
                                    FileLog.e(e, !str.equals(e.getMessage()));
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda3
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            CameraController.this.m1240x34729c0f(withDelay, e, onInitRunnable);
                                        }
                                    });
                                    return;
                                }
                            }
                            String str5 = str3;
                            List<Camera.Size> list4 = params.getSupportedPictureSizes();
                            int a3 = 0;
                            while (a3 < list4.size()) {
                                Camera.Size size2 = list4.get(a3);
                                if (size2.width == 1280 && size2.height != 720) {
                                    list = list4;
                                    a3++;
                                    list4 = list;
                                }
                                if ("samsung".equals(Build.MANUFACTURER) && "jflteuc".equals(Build.PRODUCT) && size2.width >= 2048) {
                                    list = list4;
                                    a3++;
                                    list4 = list;
                                }
                                list = list4;
                                cameraInfo2.pictureSizes.add(new Size(size2.width, size2.height));
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("picture size = " + size2.width + " " + size2.height);
                                }
                                a3++;
                                list4 = list;
                            }
                            camera.release();
                            result.add(cameraInfo2);
                            Collections.sort(cameraInfo2.previewSizes, comparator);
                            Collections.sort(cameraInfo2.pictureSizes, comparator);
                            bufferSize += ((cameraInfo2.previewSizes.size() + cameraInfo2.pictureSizes.size()) * 8) + 8;
                            cameraId++;
                            cameraController = this;
                            info2 = info;
                            str4 = str;
                            str3 = str5;
                        } catch (Exception e4) {
                            e = e4;
                            str = str4;
                            cameraController = this;
                            FileLog.e(e, !str.equals(e.getMessage()));
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda3
                                @Override // java.lang.Runnable
                                public final void run() {
                                    CameraController.this.m1240x34729c0f(withDelay, e, onInitRunnable);
                                }
                            });
                            return;
                        }
                    }
                    String str6 = str3;
                    str = str4;
                    SerializedData serializedData2 = new SerializedData(bufferSize);
                    serializedData2.writeInt32(result.size());
                    for (int a4 = 0; a4 < count2; a4++) {
                        CameraInfo cameraInfo3 = result.get(a4);
                        serializedData2.writeInt32(cameraInfo3.cameraId);
                        serializedData2.writeInt32(cameraInfo3.frontCamera);
                        int pCount3 = cameraInfo3.previewSizes.size();
                        serializedData2.writeInt32(pCount3);
                        for (int b3 = 0; b3 < pCount3; b3++) {
                            Size size3 = cameraInfo3.previewSizes.get(b3);
                            serializedData2.writeInt32(size3.mWidth);
                            serializedData2.writeInt32(size3.mHeight);
                        }
                        int pCount4 = cameraInfo3.pictureSizes.size();
                        serializedData2.writeInt32(pCount4);
                        for (int b4 = 0; b4 < pCount4; b4++) {
                            Size size4 = cameraInfo3.pictureSizes.get(b4);
                            serializedData2.writeInt32(size4.mWidth);
                            serializedData2.writeInt32(size4.mHeight);
                        }
                    }
                    preferences.edit().putString(str6, Base64.encodeToString(serializedData2.toByteArray(), 0)).commit();
                    serializedData2.cleanup();
                }
                cameraController = this;
                cameraController.cameraInfos = result;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    CameraController.this.m1238xc0dd5851();
                }
            });
        } catch (Exception e5) {
            e = e5;
            str = str4;
        }
    }

    public static /* synthetic */ int lambda$initCamera$0(Size o1, Size o2) {
        if (o1.mWidth < o2.mWidth) {
            return 1;
        }
        if (o1.mWidth > o2.mWidth) {
            return -1;
        }
        if (o1.mHeight < o2.mHeight) {
            return 1;
        }
        return o1.mHeight > o2.mHeight ? -1 : 0;
    }

    /* renamed from: lambda$initCamera$1$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1238xc0dd5851() {
        this.loadingCameras = false;
        this.cameraInitied = true;
        if (!this.onFinishCameraInitRunnables.isEmpty()) {
            for (int a = 0; a < this.onFinishCameraInitRunnables.size(); a++) {
                this.onFinishCameraInitRunnables.get(a).run();
            }
            this.onFinishCameraInitRunnables.clear();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
    }

    /* renamed from: lambda$initCamera$3$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1240x34729c0f(boolean withDelay, Exception e, final Runnable onInitRunnable) {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
        if (!withDelay && "APP_PAUSED".equals(e.getMessage()) && onInitRunnable != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    CameraController.this.m1239xfaa7fa30(onInitRunnable);
                }
            }, 1000L);
        }
    }

    /* renamed from: lambda$initCamera$2$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1239xfaa7fa30(Runnable onInitRunnable) {
        initCamera(onInitRunnable, true);
    }

    public boolean isCameraInitied() {
        return this.cameraInitied && this.cameraInfos != null && !this.cameraInfos.isEmpty();
    }

    public void close(final CameraSession session, final CountDownLatch countDownLatch, final Runnable beforeDestroyRunnable) {
        session.destroy();
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.lambda$close$5(beforeDestroyRunnable, session, countDownLatch);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ void lambda$close$5(Runnable beforeDestroyRunnable, CameraSession session, CountDownLatch countDownLatch) {
        if (beforeDestroyRunnable != null) {
            beforeDestroyRunnable.run();
        }
        if (session.cameraInfo.camera != null) {
            try {
                session.cameraInfo.camera.stopPreview();
                session.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                session.cameraInfo.camera.release();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            session.cameraInfo.camera = null;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x0062, code lost:
        r1 = r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0067, code lost:
        if (r2 <= 8) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0069, code lost:
        r3 = pack(r10, r1, 4, false);
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x0070, code lost:
        if (r3 == 1229531648) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x0075, code lost:
        if (r3 == 1296891946) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0077, code lost:
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0078, code lost:
        if (r3 != 1229531648) goto L44;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x007b, code lost:
        r6 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x007c, code lost:
        r4 = r6;
        r5 = pack(r10, r1 + 4, 4, r4) + 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0086, code lost:
        if (r5 < 10) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0088, code lost:
        if (r5 <= r2) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x008b, code lost:
        r1 = r1 + r5;
        r2 = r2 - r5;
        r5 = pack(r10, r1 - 2, 2, r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0093, code lost:
        r6 = r5 - 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0095, code lost:
        if (r5 <= 0) goto L82;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x0099, code lost:
        if (r2 < 12) goto L83;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00a1, code lost:
        if (pack(r10, r1, 2, r4) != 274) goto L62;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x00a3, code lost:
        r5 = pack(r10, r1 + 8, 2, r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00a9, code lost:
        switch(r5) {
            case 1: goto L61;
            case 3: goto L60;
            case 6: goto L59;
            case 8: goto L58;
            default: goto L57;
        };
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00ac, code lost:
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00ad, code lost:
        return 270;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x00b0, code lost:
        return 90;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00b3, code lost:
        return 180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00b6, code lost:
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x00b7, code lost:
        r1 = r1 + 12;
        r2 = r2 - 12;
        r5 = r6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00bd, code lost:
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x00be, code lost:
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int getOrientation(byte[] r10) {
        /*
            Method dump skipped, instructions count: 210
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.getOrientation(byte[]):int");
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        while (true) {
            int length2 = length - 1;
            if (length > 0) {
                value = (value << 8) | (bytes[offset] & 255);
                offset += step;
                length = length2;
            } else {
                return value;
            }
        }
    }

    public boolean takePicture(final File path, CameraSession session, final Runnable callback) {
        if (session == null) {
            return false;
        }
        final CameraInfo info = session.cameraInfo;
        final boolean flipFront = session.isFlipFront();
        Camera camera = info.camera;
        try {
            camera.takePicture(null, null, new Camera.PictureCallback() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda0
                @Override // android.hardware.Camera.PictureCallback
                public final void onPictureTaken(byte[] bArr, Camera camera2) {
                    CameraController.lambda$takePicture$6(path, info, flipFront, callback, bArr, camera2);
                }
            });
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static /* synthetic */ void lambda$takePicture$6(File path, CameraInfo info, boolean flipFront, Runnable callback, byte[] data, Camera camera1) {
        Bitmap bitmap = null;
        int size = (int) (AndroidUtilities.getPhotoSize() / AndroidUtilities.density);
        String key = String.format(Locale.US, "%s@%d_%d", Utilities.MD5(path.getAbsolutePath()), Integer.valueOf(size), Integer.valueOf(size));
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (info.frontCamera != 0 && flipFront) {
            Matrix matrix = new Matrix();
            matrix.setRotate(getOrientation(data));
            matrix.postScale(-1.0f, 1.0f);
            Bitmap scaled = Bitmaps.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (scaled != bitmap) {
                bitmap.recycle();
            }
            FileOutputStream outputStream = new FileOutputStream(path);
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.getFD().sync();
            outputStream.close();
            if (scaled != 0) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(scaled), key, false);
            }
            if (callback != null) {
                callback.run();
                return;
            }
            return;
        }
        FileOutputStream outputStream2 = new FileOutputStream(path);
        outputStream2.write(data);
        outputStream2.flush();
        outputStream2.getFD().sync();
        outputStream2.close();
        if (bitmap != null) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), key, false);
        }
        if (callback != null) {
            callback.run();
        }
    }

    public void startPreview(final CameraSession session) {
        if (session == null) {
            return;
        }
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.lambda$startPreview$7(CameraSession.this);
            }
        });
    }

    public static /* synthetic */ void lambda$startPreview$7(CameraSession session) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e(e);
                return;
            }
        }
        camera.startPreview();
    }

    public void stopPreview(final CameraSession session) {
        if (session == null) {
            return;
        }
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.lambda$stopPreview$8(CameraSession.this);
            }
        });
    }

    public static /* synthetic */ void lambda$stopPreview$8(CameraSession session) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e(e);
                return;
            }
        }
        camera.stopPreview();
    }

    public void openRound(final CameraSession session, final SurfaceTexture texture, final Runnable callback, final Runnable configureCallback) {
        if (session == null || texture == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("failed to open round " + session + " tex = " + texture);
                return;
            }
            return;
        }
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.lambda$openRound$9(CameraSession.this, configureCallback, texture, callback);
            }
        });
    }

    public static /* synthetic */ void lambda$openRound$9(CameraSession session, Runnable configureCallback, SurfaceTexture texture, Runnable callback) {
        Camera camera = session.cameraInfo.camera;
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start creating round camera session");
            }
            if (camera == null) {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            }
            camera.getParameters();
            session.configureRoundCamera(true);
            if (configureCallback != null) {
                configureCallback.run();
            }
            camera.setPreviewTexture(texture);
            camera.startPreview();
            if (callback != null) {
                AndroidUtilities.runOnUIThread(callback);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("round camera session created");
            }
        } catch (Exception e) {
            session.cameraInfo.camera = null;
            if (camera != null) {
                camera.release();
            }
            FileLog.e(e);
        }
    }

    public void open(final CameraSession session, final SurfaceTexture texture, final Runnable callback, final Runnable prestartCallback) {
        if (session == null || texture == null) {
            return;
        }
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.this.m1242lambda$open$10$orgtelegrammessengercameraCameraController(session, prestartCallback, texture, callback);
            }
        });
    }

    /* renamed from: lambda$open$10$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1242lambda$open$10$orgtelegrammessengercameraCameraController(CameraSession session, Runnable prestartCallback, SurfaceTexture texture, Runnable callback) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e(e);
                return;
            }
        }
        Camera.Parameters params = camera.getParameters();
        List<String> rawFlashModes = params.getSupportedFlashModes();
        this.availableFlashModes.clear();
        if (rawFlashModes != null) {
            for (int a = 0; a < rawFlashModes.size(); a++) {
                String rawFlashMode = rawFlashModes.get(a);
                if (rawFlashMode.equals("off") || rawFlashMode.equals("on") || rawFlashMode.equals("auto")) {
                    this.availableFlashModes.add(rawFlashMode);
                }
            }
            session.checkFlashMode(this.availableFlashModes.get(0));
        }
        if (prestartCallback != null) {
            prestartCallback.run();
        }
        session.configurePhotoCamera();
        camera.setPreviewTexture(texture);
        camera.startPreview();
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    public void recordVideo(final CameraSession session, final File path, final boolean mirror, final VideoTakeCallback callback, final Runnable onVideoStartRecord, final CameraView cameraView) {
        if (session == null) {
            return;
        }
        final CameraInfo info = session.cameraInfo;
        final Camera camera = info.camera;
        if (cameraView == null) {
            this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    CameraController.this.m1245x8fc01197(camera, session, mirror, path, info, callback, onVideoStartRecord);
                }
            });
            return;
        }
        this.recordingCurrentCameraView = cameraView;
        this.onVideoTakeCallback = callback;
        this.recordedFile = path.getAbsolutePath();
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.this.m1244x55f56fb8(camera, session, cameraView, path, onVideoStartRecord);
            }
        });
    }

    /* renamed from: lambda$recordVideo$12$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1244x55f56fb8(Camera camera, CameraSession session, final CameraView cameraView, final File path, final Runnable onVideoStartRecord) {
        try {
            if (camera != null) {
                try {
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(session.getCurrentFlashMode().equals("on") ? "torch" : "off");
                    camera.setParameters(params);
                    session.onStartRecord();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        CameraController.this.m1243x1c2acdd9(cameraView, path, onVideoStartRecord);
                    }
                });
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: lambda$recordVideo$11$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1243x1c2acdd9(CameraView cameraView, File path, Runnable onVideoStartRecord) {
        cameraView.startRecording(path, new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.this.finishRecordingVideo();
            }
        });
        if (onVideoStartRecord != null) {
            onVideoStartRecord.run();
        }
    }

    /* renamed from: lambda$recordVideo$13$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1245x8fc01197(Camera camera, CameraSession session, boolean mirror, File path, CameraInfo info, VideoTakeCallback callback, Runnable onVideoStartRecord) {
        int bitrate;
        if (camera != null) {
            try {
                try {
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(session.getCurrentFlashMode().equals("on") ? "torch" : "off");
                    camera.setParameters(params);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                camera.unlock();
                try {
                    this.mirrorRecorderVideo = mirror;
                    MediaRecorder mediaRecorder = new MediaRecorder();
                    this.recorder = mediaRecorder;
                    mediaRecorder.setCamera(camera);
                    this.recorder.setVideoSource(1);
                    this.recorder.setAudioSource(5);
                    session.configureRecorder(1, this.recorder);
                    this.recorder.setOutputFile(path.getAbsolutePath());
                    this.recorder.setMaxFileSize(1073741824L);
                    this.recorder.setVideoFrameRate(30);
                    this.recorder.setMaxDuration(0);
                    Size pictureSize = chooseOptimalSize(info.getPictureSizes(), 720, 480, new Size(16, 9));
                    if (Math.min(pictureSize.mHeight, pictureSize.mWidth) >= 720) {
                        bitrate = 3500000;
                    } else {
                        bitrate = 1800000;
                    }
                    this.recorder.setVideoEncodingBitRate(bitrate);
                    this.recorder.setVideoSize(pictureSize.getWidth(), pictureSize.getHeight());
                    this.recorder.setOnInfoListener(this);
                    this.recorder.prepare();
                    this.recorder.start();
                    this.onVideoTakeCallback = callback;
                    this.recordedFile = path.getAbsolutePath();
                    if (onVideoStartRecord != null) {
                        AndroidUtilities.runOnUIThread(onVideoStartRecord);
                    }
                } catch (Exception e2) {
                    this.recorder.release();
                    this.recorder = null;
                    FileLog.e(e2);
                }
            } catch (Exception e3) {
                FileLog.e(e3);
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:31|3|(2:5|6)|(2:37|7)|16|(1:18)(1:19)|20|33|21|24|25) */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00b2, code lost:
        r1 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x00b3, code lost:
        org.telegram.messenger.FileLog.e(r1);
     */
    /* JADX WARN: Removed duplicated region for block: B:18:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x007e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void finishRecordingVideo() {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            r3.<init>()     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            r0 = r3
            java.lang.String r3 = r13.recordedFile     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            r0.setDataSource(r3)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            r3 = 9
            java.lang.String r3 = r0.extractMetadata(r3)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            if (r3 == 0) goto L25
            long r4 = java.lang.Long.parseLong(r3)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            float r4 = (float) r4     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r4 = r4 / r5
            double r4 = (double) r4     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            double r4 = java.lang.Math.ceil(r4)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2f
            int r4 = (int) r4
            long r1 = (long) r4
        L25:
            r0.release()     // Catch: java.lang.Exception -> L2a
            goto L3e
        L2a:
            r3 = move-exception
            goto L3a
        L2c:
            r3 = move-exception
            goto Lc7
        L2f:
            r3 = move-exception
            org.telegram.messenger.FileLog.e(r3)     // Catch: java.lang.Throwable -> L2c
            if (r0 == 0) goto L3e
            r0.release()     // Catch: java.lang.Exception -> L39
            goto L3e
        L39:
            r3 = move-exception
        L3a:
            org.telegram.messenger.FileLog.e(r3)
            goto L3f
        L3e:
        L3f:
            r7 = r1
            java.lang.String r1 = r13.recordedFile
            r2 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(r1, r2)
            boolean r2 = r13.mirrorRecorderVideo
            if (r2 == 0) goto L7e
            int r2 = r1.getWidth()
            int r3 = r1.getHeight()
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4)
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>(r2)
            r4 = -1082130432(0xffffffffbf800000, float:-1.0)
            r5 = 1065353216(0x3f800000, float:1.0)
            int r6 = r2.getWidth()
            int r6 = r6 / 2
            float r6 = (float) r6
            int r9 = r2.getHeight()
            int r9 = r9 / 2
            float r9 = (float) r9
            r3.scale(r4, r5, r6, r9)
            r4 = 0
            r5 = 0
            r3.drawBitmap(r1, r5, r5, r4)
            r1.recycle()
            r1 = r2
            r9 = r1
            goto L7f
        L7e:
            r9 = r1
        L7f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "-2147483648_"
            r1.append(r2)
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r1.append(r2)
            java.lang.String r2 = ".jpg"
            r1.append(r2)
            java.lang.String r10 = r1.toString()
            java.io.File r1 = new java.io.File
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            r1.<init>(r2, r10)
            r11 = r1
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> Lb2
            r1.<init>(r11)     // Catch: java.lang.Throwable -> Lb2
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch: java.lang.Throwable -> Lb2
            r3 = 87
            r9.compress(r2, r3, r1)     // Catch: java.lang.Throwable -> Lb2
            goto Lb6
        Lb2:
            r1 = move-exception
            org.telegram.messenger.FileLog.e(r1)
        Lb6:
            org.telegram.messenger.SharedConfig.saveConfig()
            r5 = r7
            r4 = r9
            org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda15 r12 = new org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda15
            r1 = r12
            r2 = r13
            r3 = r11
            r1.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)
            return
        Lc7:
            if (r0 == 0) goto Ld2
            r0.release()     // Catch: java.lang.Exception -> Lcd
            goto Ld2
        Lcd:
            r4 = move-exception
            org.telegram.messenger.FileLog.e(r4)
            goto Ld3
        Ld2:
        Ld3:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraController.finishRecordingVideo():void");
    }

    /* renamed from: lambda$finishRecordingVideo$14$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1237x1d61ce23(File cacheFile, Bitmap bitmapFinal, long durationFinal) {
        if (this.onVideoTakeCallback != null) {
            String path = cacheFile.getAbsolutePath();
            if (bitmapFinal != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapFinal), Utilities.MD5(path), false);
            }
            this.onVideoTakeCallback.onFinishVideoRecording(path, durationFinal);
            this.onVideoTakeCallback = null;
        }
    }

    @Override // android.media.MediaRecorder.OnInfoListener
    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        if (what == 800 || what == 801 || what == 1) {
            MediaRecorder tempRecorder = this.recorder;
            this.recorder = null;
            if (tempRecorder != null) {
                tempRecorder.stop();
                tempRecorder.release();
            }
            if (this.onVideoTakeCallback != null) {
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(final CameraSession session, final boolean abandon) {
        CameraView cameraView = this.recordingCurrentCameraView;
        if (cameraView != null) {
            cameraView.stopRecording();
            this.recordingCurrentCameraView = null;
            return;
        }
        this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CameraController.this.m1246x9d3b7c3c(session, abandon);
            }
        });
    }

    /* renamed from: lambda$stopVideoRecording$16$org-telegram-messenger-camera-CameraController */
    public /* synthetic */ void m1246x9d3b7c3c(final CameraSession session, boolean abandon) {
        MediaRecorder tempRecorder;
        try {
            CameraInfo info = session.cameraInfo;
            final Camera camera = info.camera;
            if (camera != null && (tempRecorder = this.recorder) != null) {
                this.recorder = null;
                try {
                    tempRecorder.stop();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    tempRecorder.release();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                try {
                    camera.reconnect();
                    camera.startPreview();
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
                try {
                    session.stopVideoRecording();
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
            try {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode("off");
                camera.setParameters(params);
            } catch (Exception e5) {
                FileLog.e(e5);
            }
            this.threadPool.execute(new Runnable() { // from class: org.telegram.messenger.camera.CameraController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    CameraController.lambda$stopVideoRecording$15(camera, session);
                }
            });
            if (!abandon && this.onVideoTakeCallback != null) {
                finishRecordingVideo();
            } else {
                this.onVideoTakeCallback = null;
            }
        } catch (Exception e6) {
        }
    }

    public static /* synthetic */ void lambda$stopVideoRecording$15(Camera camera, CameraSession session) {
        try {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(session.getCurrentFlashMode());
            camera.setParameters(params);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static Size chooseOptimalSize(List<Size> choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnoughWithAspectRatio = new ArrayList<>(choices.size());
        List<Size> bigEnough = new ArrayList<>(choices.size());
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (int a = 0; a < choices.size(); a++) {
            Size option = choices.get(a);
            if (option.getHeight() == (option.getWidth() * h) / w && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnoughWithAspectRatio.add(option);
            } else if (option.getHeight() * option.getWidth() <= width * height * 4) {
                bigEnough.add(option);
            }
        }
        int a2 = bigEnoughWithAspectRatio.size();
        if (a2 > 0) {
            return (Size) Collections.min(bigEnoughWithAspectRatio, new CompareSizesByArea());
        }
        if (bigEnough.size() > 0) {
            return (Size) Collections.min(bigEnough, new CompareSizesByArea());
        }
        return (Size) Collections.max(choices, new CompareSizesByArea());
    }

    /* loaded from: classes4.dex */
    public static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size lhs, Size rhs) {
            return Long.signum((lhs.getWidth() * lhs.getHeight()) - (rhs.getWidth() * rhs.getHeight()));
        }
    }
}
