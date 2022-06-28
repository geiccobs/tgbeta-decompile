package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueuePool;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes5.dex */
public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static ThreadPoolExecutor lottieCacheGenerateQueue;
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    protected int autoRepeat;
    protected int autoRepeatPlayCount;
    protected volatile Bitmap backgroundBitmap;
    File cacheFile;
    protected Runnable cacheGenerateTask;
    protected int currentFrame;
    private View currentParentView;
    protected int customEndFrame;
    private boolean decodeSingleFrame;
    protected boolean destroyAfterLoading;
    protected boolean destroyWhenDone;
    protected int diceSwitchFramesCount;
    private boolean doNotRemoveInvalidOnFrameReady;
    private final android.graphics.Rect dstRect;
    File file;
    private int finishFrame;
    private boolean forceFrameRedraw;
    private WeakReference<Runnable> frameReadyCallback;
    protected CountDownLatch frameWaitSync;
    protected int height;
    private boolean invalidateOnProgressSet;
    protected int isDice;
    private boolean isInvalid;
    protected volatile boolean isRecycled;
    protected volatile boolean isRunning;
    private long lastFrameTime;
    private DispatchQueuePool loadFrameQueue;
    protected Runnable loadFrameRunnable;
    protected Runnable loadFrameTask;
    protected boolean loadingInBackground;
    protected final int[] metaData;
    protected volatile long nativePtr;
    private boolean needScale;
    private HashMap<String, Integer> newColorUpdates;
    private int[] newReplaceColors;
    protected volatile boolean nextFrameIsLast;
    protected volatile Bitmap nextRenderingBitmap;
    private Runnable onAnimationEndListener;
    protected WeakReference<Runnable> onFinishCallback;
    private Runnable onFrameReadyRunnable;
    private ArrayList<WeakReference<View>> parentViews;
    private volatile HashMap<String, Integer> pendingColorUpdates;
    private int[] pendingReplaceColors;
    protected boolean playInDirectionOfCustomEndFrame;
    boolean precache;
    protected volatile Bitmap renderingBitmap;
    private float scaleX;
    private float scaleY;
    protected int secondFramesCount;
    protected boolean secondLoadingInBackground;
    protected volatile long secondNativePtr;
    protected volatile boolean setLastFrame;
    private boolean shouldLimitFps;
    private boolean singleFrameDecoded;
    long startTime;
    protected int timeBetweenFrames;
    protected Runnable uiRunnable;
    private Runnable uiRunnableCacheFinished;
    private Runnable uiRunnableGenerateCache;
    private Runnable uiRunnableLastFrame;
    protected Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    protected boolean waitingForNextTask;
    protected int width;
    protected static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    private static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static final DispatchQueuePool loadFrameRunnableQueue = new DispatchQueuePool(2);
    private static final DispatchQueuePool largeSizeLoadFrameRunnableQueue = new DispatchQueuePool(4);
    private static HashSet<String> generatingCacheFiles = new HashSet<>();

    public static native long create(String str, String str2, int i, int i2, int[] iArr, boolean z, int[] iArr2, boolean z2, int i3);

    public static native void createCache(long j, int i, int i2);

    public static native long createWithJson(String str, String str2, int[] iArr, int[] iArr2);

    public static native void destroy(long j);

    private static native String getCacheFile(long j);

    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4, boolean z);

    public static native void replaceColors(long j, int[] iArr);

    public static native void setLayerColor(long j, String str, int i);

    /* renamed from: org.telegram.ui.Components.RLottieDrawable$4 */
    /* loaded from: classes5.dex */
    public class AnonymousClass4 implements Runnable {
        AnonymousClass4() {
            RLottieDrawable.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                RLottieDrawable.this.startTime = System.currentTimeMillis();
                ThreadPoolExecutor threadPoolExecutor = RLottieDrawable.lottieCacheGenerateQueue;
                RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        RLottieDrawable.AnonymousClass4.this.m2930lambda$run$0$orgtelegramuiComponentsRLottieDrawable$4();
                    }
                };
                rLottieDrawable.cacheGenerateTask = runnable;
                threadPoolExecutor.execute(runnable);
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Components-RLottieDrawable$4 */
        public /* synthetic */ void m2930lambda$run$0$orgtelegramuiComponentsRLottieDrawable$4() {
            RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.width, RLottieDrawable.this.height);
            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
        }
    }

    public void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null && lottieCacheGenerateQueue.remove(runnable)) {
            this.cacheGenerateTask = null;
        }
        if (!hasParentView() && this.nextRenderingBitmap != null && this.loadFrameTask != null) {
            this.loadFrameTask = null;
            this.nextRenderingBitmap = null;
        }
    }

    protected void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null && this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0L;
                if (this.secondNativePtr != 0) {
                    destroy(this.secondNativePtr);
                    this.secondNativePtr = 0L;
                }
            }
        }
        if (this.nativePtr == 0 && this.secondNativePtr == 0) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }

    public void recycleResources() {
        ArrayList<Bitmap> bitmapToRecycle = new ArrayList<>();
        bitmapToRecycle.add(this.renderingBitmap);
        bitmapToRecycle.add(this.nextRenderingBitmap);
        this.renderingBitmap = null;
        this.backgroundBitmap = null;
        AndroidUtilities.recycleBitmaps(bitmapToRecycle);
        if (this.onAnimationEndListener != null) {
            this.onAnimationEndListener = null;
        }
        invalidateInternal();
    }

    public void setOnFinishCallback(Runnable callback, int frame) {
        if (callback != null) {
            this.onFinishCallback = new WeakReference<>(callback);
            this.finishFrame = frame;
        } else if (this.onFinishCallback != null) {
            this.onFinishCallback = null;
        }
    }

    public RLottieDrawable(File file, int w, int h, boolean precache, boolean limitFps) {
        this(file, w, h, precache, limitFps, null, 0);
    }

    public RLottieDrawable(File file, int w, int h, boolean precache, boolean limitFps, int[] colorReplacement, int fitzModifier) {
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new android.graphics.Rect();
        this.parentViews = new ArrayList<>();
        DispatchQueuePool dispatchQueuePool = loadFrameRunnableQueue;
        this.loadFrameQueue = dispatchQueuePool;
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableLastFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.3
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass4();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.6
            @Override // java.lang.Runnable
            public void run() {
                long ptrToUse;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                    return;
                }
                if (RLottieDrawable.this.backgroundBitmap == null) {
                    try {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (RLottieDrawable.this.backgroundBitmap != null) {
                    try {
                        if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                            for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                            }
                            RLottieDrawable.this.pendingColorUpdates.clear();
                        }
                    } catch (Exception e2) {
                    }
                    if (RLottieDrawable.this.pendingReplaceColors != null) {
                        RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                        RLottieDrawable.this.pendingReplaceColors = null;
                    }
                    try {
                        if (RLottieDrawable.this.isDice == 1) {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        } else if (RLottieDrawable.this.isDice == 2) {
                            ptrToUse = RLottieDrawable.this.secondNativePtr;
                            if (RLottieDrawable.this.setLastFrame) {
                                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                            }
                        } else {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        }
                        int result = RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true);
                        if (result == -1) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                            if (RLottieDrawable.this.frameWaitSync != null) {
                                RLottieDrawable.this.frameWaitSync.countDown();
                                return;
                            }
                            return;
                        }
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                        rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                        int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                        if (RLottieDrawable.this.isDice == 1) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.currentFrame = 0;
                                RLottieDrawable.this.nextFrameIsLast = false;
                                if (RLottieDrawable.this.secondNativePtr != 0) {
                                    RLottieDrawable.this.isDice = 2;
                                }
                            }
                        } else if (RLottieDrawable.this.isDice == 2) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            }
                        } else if (RLottieDrawable.this.customEndFrame >= 0 && RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                            if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                            if (RLottieDrawable.this.autoRepeat == 3) {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            } else {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            }
                        } else if (RLottieDrawable.this.autoRepeat == 1) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat == 2) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.autoRepeatPlayCount++;
                        } else {
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                        }
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                if (RLottieDrawable.this.frameWaitSync != null) {
                    RLottieDrawable.this.frameWaitSync.countDown();
                }
            }
        };
        this.width = w;
        this.height = h;
        this.shouldLimitFps = limitFps;
        getPaint().setFlags(2);
        this.file = file;
        this.nativePtr = create(file.getAbsolutePath(), null, w, h, iArr, precache, colorReplacement, this.shouldLimitFps, fitzModifier);
        if (precache && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (w > AndroidUtilities.dp(120.0f) || h > AndroidUtilities.dp(120.0f)) {
            this.loadFrameQueue = largeSizeLoadFrameRunnableQueue;
        } else {
            this.loadFrameQueue = dispatchQueuePool;
        }
        if (this.nativePtr == 0) {
            file.delete();
        }
        String cacheFilePath = getCacheFile(this.nativePtr);
        if (cacheFilePath != null) {
            this.cacheFile = new File(cacheFilePath);
        }
        if (this.shouldLimitFps && iArr[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / iArr[1]));
    }

    public RLottieDrawable(File file, String json, int w, int h, boolean precache, boolean limitFps, int[] colorReplacement, int fitzModifier) {
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new android.graphics.Rect();
        this.parentViews = new ArrayList<>();
        DispatchQueuePool dispatchQueuePool = loadFrameRunnableQueue;
        this.loadFrameQueue = dispatchQueuePool;
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableLastFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.3
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass4();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.6
            @Override // java.lang.Runnable
            public void run() {
                long ptrToUse;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                    return;
                }
                if (RLottieDrawable.this.backgroundBitmap == null) {
                    try {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (RLottieDrawable.this.backgroundBitmap != null) {
                    try {
                        if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                            for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                            }
                            RLottieDrawable.this.pendingColorUpdates.clear();
                        }
                    } catch (Exception e2) {
                    }
                    if (RLottieDrawable.this.pendingReplaceColors != null) {
                        RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                        RLottieDrawable.this.pendingReplaceColors = null;
                    }
                    try {
                        if (RLottieDrawable.this.isDice == 1) {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        } else if (RLottieDrawable.this.isDice == 2) {
                            ptrToUse = RLottieDrawable.this.secondNativePtr;
                            if (RLottieDrawable.this.setLastFrame) {
                                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                            }
                        } else {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        }
                        int result = RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true);
                        if (result == -1) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                            if (RLottieDrawable.this.frameWaitSync != null) {
                                RLottieDrawable.this.frameWaitSync.countDown();
                                return;
                            }
                            return;
                        }
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                        rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                        int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                        if (RLottieDrawable.this.isDice == 1) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.currentFrame = 0;
                                RLottieDrawable.this.nextFrameIsLast = false;
                                if (RLottieDrawable.this.secondNativePtr != 0) {
                                    RLottieDrawable.this.isDice = 2;
                                }
                            }
                        } else if (RLottieDrawable.this.isDice == 2) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            }
                        } else if (RLottieDrawable.this.customEndFrame >= 0 && RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                            if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                            if (RLottieDrawable.this.autoRepeat == 3) {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            } else {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            }
                        } else if (RLottieDrawable.this.autoRepeat == 1) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat == 2) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.autoRepeatPlayCount++;
                        } else {
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                        }
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                if (RLottieDrawable.this.frameWaitSync != null) {
                    RLottieDrawable.this.frameWaitSync.countDown();
                }
            }
        };
        this.width = w;
        this.height = h;
        this.shouldLimitFps = limitFps;
        getPaint().setFlags(2);
        this.nativePtr = create(file.getAbsolutePath(), json, w, h, iArr, precache, colorReplacement, this.shouldLimitFps, fitzModifier);
        if (precache && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (this.nativePtr == 0) {
            file.delete();
        }
        if (this.shouldLimitFps && iArr[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / iArr[1]));
        if (w > AndroidUtilities.dp(100.0f) || w > AndroidUtilities.dp(100.0f)) {
            this.loadFrameQueue = largeSizeLoadFrameRunnableQueue;
        } else {
            this.loadFrameQueue = dispatchQueuePool;
        }
    }

    public RLottieDrawable(int rawRes, String name, int w, int h) {
        this(rawRes, name, w, h, true, null);
    }

    public RLottieDrawable(String diceEmoji, int w, int h) {
        String jsonString;
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new android.graphics.Rect();
        this.parentViews = new ArrayList<>();
        this.loadFrameQueue = loadFrameRunnableQueue;
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableLastFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.3
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass4();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.6
            @Override // java.lang.Runnable
            public void run() {
                long ptrToUse;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                    return;
                }
                if (RLottieDrawable.this.backgroundBitmap == null) {
                    try {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (RLottieDrawable.this.backgroundBitmap != null) {
                    try {
                        if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                            for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                            }
                            RLottieDrawable.this.pendingColorUpdates.clear();
                        }
                    } catch (Exception e2) {
                    }
                    if (RLottieDrawable.this.pendingReplaceColors != null) {
                        RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                        RLottieDrawable.this.pendingReplaceColors = null;
                    }
                    try {
                        if (RLottieDrawable.this.isDice == 1) {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        } else if (RLottieDrawable.this.isDice == 2) {
                            ptrToUse = RLottieDrawable.this.secondNativePtr;
                            if (RLottieDrawable.this.setLastFrame) {
                                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                            }
                        } else {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        }
                        int result = RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true);
                        if (result == -1) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                            if (RLottieDrawable.this.frameWaitSync != null) {
                                RLottieDrawable.this.frameWaitSync.countDown();
                                return;
                            }
                            return;
                        }
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                        rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                        int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                        if (RLottieDrawable.this.isDice == 1) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.currentFrame = 0;
                                RLottieDrawable.this.nextFrameIsLast = false;
                                if (RLottieDrawable.this.secondNativePtr != 0) {
                                    RLottieDrawable.this.isDice = 2;
                                }
                            }
                        } else if (RLottieDrawable.this.isDice == 2) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            }
                        } else if (RLottieDrawable.this.customEndFrame >= 0 && RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                            if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                            if (RLottieDrawable.this.autoRepeat == 3) {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            } else {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            }
                        } else if (RLottieDrawable.this.autoRepeat == 1) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat == 2) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.autoRepeatPlayCount++;
                        } else {
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                        }
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                if (RLottieDrawable.this.frameWaitSync != null) {
                    RLottieDrawable.this.frameWaitSync.countDown();
                }
            }
        };
        this.width = w;
        this.height = h;
        this.isDice = 1;
        if ("🎲".equals(diceEmoji)) {
            jsonString = readRes(null, R.raw.diceloop);
            this.diceSwitchFramesCount = 60;
        } else if ("🎯".equals(diceEmoji)) {
            jsonString = readRes(null, R.raw.dartloop);
        } else {
            jsonString = null;
        }
        getPaint().setFlags(2);
        if (TextUtils.isEmpty(jsonString)) {
            this.timeBetweenFrames = 16;
            return;
        }
        this.nativePtr = createWithJson(jsonString, "dice", iArr, null);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr[1]));
    }

    public void checkDispatchOnAnimationEnd() {
        Runnable runnable = this.onAnimationEndListener;
        if (runnable != null) {
            runnable.run();
            this.onAnimationEndListener = null;
        }
    }

    public void setOnAnimationEndListener(Runnable onAnimationEndListener) {
        this.onAnimationEndListener = onAnimationEndListener;
    }

    public boolean isDice() {
        return this.isDice != 0;
    }

    public boolean setBaseDice(File path) {
        if (this.nativePtr != 0 || this.loadingInBackground) {
            return true;
        }
        final String jsonString = readRes(path, 0);
        if (TextUtils.isEmpty(jsonString)) {
            return false;
        }
        this.loadingInBackground = true;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.m2926lambda$setBaseDice$1$orgtelegramuiComponentsRLottieDrawable(jsonString);
            }
        });
        return true;
    }

    /* renamed from: lambda$setBaseDice$1$org-telegram-ui-Components-RLottieDrawable */
    public /* synthetic */ void m2926lambda$setBaseDice$1$orgtelegramuiComponentsRLottieDrawable(String jsonString) {
        this.nativePtr = createWithJson(jsonString, "dice", this.metaData, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.m2925lambda$setBaseDice$0$orgtelegramuiComponentsRLottieDrawable();
            }
        });
    }

    /* renamed from: lambda$setBaseDice$0$org-telegram-ui-Components-RLottieDrawable */
    public /* synthetic */ void m2925lambda$setBaseDice$0$orgtelegramuiComponentsRLottieDrawable() {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle();
            return;
        }
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public boolean hasBaseDice() {
        return this.nativePtr != 0 || this.loadingInBackground;
    }

    public boolean setDiceNumber(File path, boolean instant) {
        if (this.secondNativePtr != 0 || this.secondLoadingInBackground) {
            return true;
        }
        final String jsonString = readRes(path, 0);
        if (TextUtils.isEmpty(jsonString)) {
            return false;
        }
        if (instant && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
            this.isDice = 2;
            this.setLastFrame = true;
        }
        this.secondLoadingInBackground = true;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.m2929x8d5250cd(jsonString);
            }
        });
        return true;
    }

    /* renamed from: lambda$setDiceNumber$4$org-telegram-ui-Components-RLottieDrawable */
    public /* synthetic */ void m2929x8d5250cd(String jsonString) {
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RLottieDrawable.this.m2927x7797e0f();
                }
            });
            return;
        }
        final int[] metaData2 = new int[3];
        this.secondNativePtr = createWithJson(jsonString, "dice", metaData2, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                RLottieDrawable.this.m2928xca65e76e(metaData2);
            }
        });
    }

    /* renamed from: lambda$setDiceNumber$2$org-telegram-ui-Components-RLottieDrawable */
    public /* synthetic */ void m2927x7797e0f() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    /* renamed from: lambda$setDiceNumber$3$org-telegram-ui-Components-RLottieDrawable */
    public /* synthetic */ void m2928xca65e76e(int[] metaData2) {
        this.secondLoadingInBackground = false;
        if (this.destroyAfterLoading) {
            recycle();
            return;
        }
        this.secondFramesCount = metaData2[0];
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / metaData2[1]));
        scheduleNextGetFrame();
        invalidateInternal();
    }

    public RLottieDrawable(int rawRes, String name, int w, int h, boolean startDecode, int[] colorReplacement) {
        int[] iArr = new int[3];
        this.metaData = iArr;
        this.customEndFrame = -1;
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.diceSwitchFramesCount = -1;
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new android.graphics.Rect();
        this.parentViews = new ArrayList<>();
        this.loadFrameQueue = loadFrameRunnableQueue;
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
                if (RLottieDrawable.this.onFrameReadyRunnable != null) {
                    RLottieDrawable.this.onFrameReadyRunnable.run();
                }
            }
        };
        this.uiRunnableLastFrame = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.3
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new AnonymousClass4();
        this.uiRunnableCacheFinished = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.5
            @Override // java.lang.Runnable
            public void run() {
                RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.generatingCacheFiles.remove(RLottieDrawable.this.cacheFile.getPath());
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.RLottieDrawable.6
            @Override // java.lang.Runnable
            public void run() {
                long ptrToUse;
                if (RLottieDrawable.this.isRecycled) {
                    return;
                }
                if (RLottieDrawable.this.nativePtr == 0 || (RLottieDrawable.this.isDice == 2 && RLottieDrawable.this.secondNativePtr == 0)) {
                    if (RLottieDrawable.this.frameWaitSync != null) {
                        RLottieDrawable.this.frameWaitSync.countDown();
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                    return;
                }
                if (RLottieDrawable.this.backgroundBitmap == null) {
                    try {
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        rLottieDrawable.backgroundBitmap = Bitmap.createBitmap(rLottieDrawable.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                if (RLottieDrawable.this.backgroundBitmap != null) {
                    try {
                        if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                            for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                            }
                            RLottieDrawable.this.pendingColorUpdates.clear();
                        }
                    } catch (Exception e2) {
                    }
                    if (RLottieDrawable.this.pendingReplaceColors != null) {
                        RLottieDrawable.replaceColors(RLottieDrawable.this.nativePtr, RLottieDrawable.this.pendingReplaceColors);
                        RLottieDrawable.this.pendingReplaceColors = null;
                    }
                    try {
                        if (RLottieDrawable.this.isDice == 1) {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        } else if (RLottieDrawable.this.isDice == 2) {
                            ptrToUse = RLottieDrawable.this.secondNativePtr;
                            if (RLottieDrawable.this.setLastFrame) {
                                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                rLottieDrawable2.currentFrame = rLottieDrawable2.secondFramesCount - 1;
                            }
                        } else {
                            ptrToUse = RLottieDrawable.this.nativePtr;
                        }
                        int result = RLottieDrawable.getFrame(ptrToUse, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes(), true);
                        if (result == -1) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                            if (RLottieDrawable.this.frameWaitSync != null) {
                                RLottieDrawable.this.frameWaitSync.countDown();
                                return;
                            }
                            return;
                        }
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable3 = RLottieDrawable.this;
                        rLottieDrawable3.nextRenderingBitmap = rLottieDrawable3.backgroundBitmap;
                        int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                        if (RLottieDrawable.this.isDice == 1) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.diceSwitchFramesCount == -1 ? RLottieDrawable.this.metaData[0] : RLottieDrawable.this.diceSwitchFramesCount)) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.currentFrame = 0;
                                RLottieDrawable.this.nextFrameIsLast = false;
                                if (RLottieDrawable.this.secondNativePtr != 0) {
                                    RLottieDrawable.this.isDice = 2;
                                }
                            }
                        } else if (RLottieDrawable.this.isDice == 2) {
                            if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.secondFramesCount) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            }
                        } else if (RLottieDrawable.this.customEndFrame >= 0 && RLottieDrawable.this.playInDirectionOfCustomEndFrame) {
                            if (RLottieDrawable.this.currentFrame > RLottieDrawable.this.customEndFrame) {
                                if (RLottieDrawable.this.currentFrame - framesPerUpdates >= RLottieDrawable.this.customEndFrame) {
                                    RLottieDrawable.this.currentFrame -= framesPerUpdates;
                                    RLottieDrawable.this.nextFrameIsLast = false;
                                } else {
                                    RLottieDrawable.this.nextFrameIsLast = true;
                                    RLottieDrawable.this.checkDispatchOnAnimationEnd();
                                }
                            } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.customEndFrame) {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            } else {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.checkDispatchOnAnimationEnd();
                            }
                        } else if (RLottieDrawable.this.currentFrame + framesPerUpdates < (RLottieDrawable.this.customEndFrame >= 0 ? RLottieDrawable.this.customEndFrame : RLottieDrawable.this.metaData[0])) {
                            if (RLottieDrawable.this.autoRepeat == 3) {
                                RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.this.autoRepeatPlayCount++;
                            } else {
                                RLottieDrawable.this.currentFrame += framesPerUpdates;
                                RLottieDrawable.this.nextFrameIsLast = false;
                            }
                        } else if (RLottieDrawable.this.autoRepeat == 1) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat == 2) {
                            RLottieDrawable.this.currentFrame = 0;
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.autoRepeatPlayCount++;
                        } else {
                            RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.this.checkDispatchOnAnimationEnd();
                        }
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
                RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                if (RLottieDrawable.this.frameWaitSync != null) {
                    RLottieDrawable.this.frameWaitSync.countDown();
                }
            }
        };
        this.width = w;
        this.height = h;
        this.autoRepeat = 0;
        String jsonString = readRes(null, rawRes);
        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        getPaint().setFlags(2);
        this.nativePtr = createWithJson(jsonString, name, iArr, colorReplacement);
        this.timeBetweenFrames = Math.max(16, (int) (1000.0f / iArr[1]));
        if (startDecode) {
            setAllowDecodeSingleFrame(true);
        }
    }

    public static String readRes(File path, int rawRes) {
        int totalRead = 0;
        byte[] readBuffer = readBufferLocal.get();
        if (readBuffer == null) {
            readBuffer = new byte[65536];
            readBufferLocal.set(readBuffer);
        }
        InputStream inputStream = null;
        try {
            if (path != null) {
                inputStream = new FileInputStream(path);
            } else {
                inputStream = ApplicationLoader.applicationContext.getResources().openRawResource(rawRes);
            }
            byte[] buffer = bufferLocal.get();
            if (buffer == null) {
                buffer = new byte[4096];
                bufferLocal.set(buffer);
            }
            while (true) {
                int readLen = inputStream.read(buffer, 0, buffer.length);
                if (readLen < 0) {
                    break;
                }
                if (readBuffer.length < totalRead + readLen) {
                    byte[] newBuffer = new byte[readBuffer.length * 2];
                    System.arraycopy(readBuffer, 0, newBuffer, 0, totalRead);
                    readBuffer = newBuffer;
                    readBufferLocal.set(readBuffer);
                }
                if (readLen > 0) {
                    System.arraycopy(buffer, 0, readBuffer, totalRead, readLen);
                    totalRead += readLen;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable th) {
                }
            }
            return new String(readBuffer, 0, totalRead);
        } catch (Throwable th2) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable th3) {
                }
            }
            return null;
        }
    }

    public int getCurrentFrame() {
        return this.currentFrame;
    }

    public int getCustomEndFrame() {
        return this.customEndFrame;
    }

    public long getDuration() {
        int[] iArr = this.metaData;
        return (iArr[0] / iArr[1]) * 1000.0f;
    }

    public void setPlayInDirectionOfCustomEndFrame(boolean value) {
        this.playInDirectionOfCustomEndFrame = value;
    }

    public boolean setCustomEndFrame(int frame) {
        if (this.customEndFrame == frame || frame > this.metaData[0]) {
            return false;
        }
        this.customEndFrame = frame;
        return true;
    }

    public int getFramesCount() {
        return this.metaData[0];
    }

    public void addParentView(View view) {
        if (view == null) {
            return;
        }
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            if (this.parentViews.get(a).get() == view) {
                return;
            }
            if (this.parentViews.get(a).get() == null) {
                this.parentViews.remove(a);
                N--;
                a--;
            }
            a++;
        }
        this.parentViews.add(0, new WeakReference<>(view));
    }

    public void removeParentView(View view) {
        if (view == null) {
            return;
        }
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            View v = this.parentViews.get(a).get();
            if (v == view || v == null) {
                this.parentViews.remove(a);
                N--;
                a--;
            }
            a++;
        }
    }

    public boolean hasParentView() {
        if (getCallback() != null) {
            return true;
        }
        int N = this.parentViews.size();
        for (int a = 0; a < N; a = (a - 1) + 1) {
            View view = this.parentViews.get(a).get();
            if (view != null) {
                return true;
            }
            this.parentViews.remove(a);
            N--;
        }
        return false;
    }

    public void invalidateInternal() {
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            View view = this.parentViews.get(a).get();
            if (view != null) {
                view.invalidate();
            } else {
                this.parentViews.remove(a);
                N--;
                a--;
            }
            a++;
        }
        if (getCallback() != null) {
            invalidateSelf();
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (value) {
            scheduleNextGetFrame();
        }
    }

    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
        } else if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0L;
            }
            if (this.secondNativePtr != 0) {
                destroy(this.secondNativePtr);
                this.secondNativePtr = 0L;
            }
            recycleResources();
        } else {
            this.destroyWhenDone = true;
        }
    }

    public void setAutoRepeat(int value) {
        if (this.autoRepeat == 2 && value == 3 && this.currentFrame != 0) {
            return;
        }
        this.autoRepeat = value;
    }

    protected void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (!this.isRunning) {
            if ((this.autoRepeat >= 2 && this.autoRepeatPlayCount != 0) || this.customEndFrame == this.currentFrame) {
                return;
            }
            this.isRunning = true;
            if (this.invalidateOnProgressSet) {
                this.isInvalid = true;
                if (this.loadFrameTask != null) {
                    this.doNotRemoveInvalidOnFrameReady = true;
                }
            }
            scheduleNextGetFrame();
            invalidateInternal();
        }
    }

    public boolean restart() {
        if (this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) {
            return false;
        }
        this.autoRepeatPlayCount = 0;
        this.autoRepeat = 2;
        start();
        return true;
    }

    public void setVibrationPattern(HashMap<Integer, Integer> pattern) {
        this.vibrationPattern = pattern;
    }

    public void beginApplyLayerColors() {
        this.applyingLayerColors = true;
    }

    public void commitApplyLayerColors() {
        if (!this.applyingLayerColors) {
            return;
        }
        this.applyingLayerColors = false;
        if (!this.isRunning && this.decodeSingleFrame) {
            if (this.currentFrame <= 2) {
                this.currentFrame = 0;
            }
            this.nextFrameIsLast = false;
            this.singleFrameDecoded = false;
            if (!scheduleNextGetFrame()) {
                this.forceFrameRedraw = true;
            }
        }
        invalidateInternal();
    }

    public void replaceColors(int[] colors) {
        this.newReplaceColors = colors;
        requestRedrawColors();
    }

    public void setLayerColor(String layerName, int color) {
        this.newColorUpdates.put(layerName, Integer.valueOf(color));
        requestRedrawColors();
    }

    private void requestRedrawColors() {
        if (!this.applyingLayerColors && !this.isRunning && this.decodeSingleFrame) {
            if (this.currentFrame <= 2) {
                this.currentFrame = 0;
            }
            this.nextFrameIsLast = false;
            this.singleFrameDecoded = false;
            if (!scheduleNextGetFrame()) {
                this.forceFrameRedraw = true;
            }
        }
        invalidateInternal();
    }

    public boolean scheduleNextGetFrame() {
        if (this.loadFrameTask != null || this.nextRenderingBitmap != null || this.nativePtr == 0 || this.loadingInBackground || this.destroyWhenDone) {
            return false;
        }
        if (!this.isRunning) {
            boolean z = this.decodeSingleFrame;
            if (!z) {
                return false;
            }
            if (z && this.singleFrameDecoded) {
                return false;
            }
        }
        if (!this.newColorUpdates.isEmpty()) {
            this.pendingColorUpdates.putAll(this.newColorUpdates);
            this.newColorUpdates.clear();
        }
        int[] iArr = this.newReplaceColors;
        if (iArr != null) {
            this.pendingReplaceColors = iArr;
            this.newReplaceColors = null;
        }
        DispatchQueuePool dispatchQueuePool = this.loadFrameQueue;
        Runnable runnable = this.loadFrameRunnable;
        this.loadFrameTask = runnable;
        dispatchQueuePool.execute(runnable);
        return true;
    }

    public boolean isHeavyDrawable() {
        return this.isDice == 0;
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.isRunning = false;
    }

    public void setCurrentFrame(int frame) {
        setCurrentFrame(frame, true);
    }

    public void setCurrentFrame(int frame, boolean async) {
        setCurrentFrame(frame, async, false);
    }

    public void setCurrentFrame(int frame, boolean async, boolean resetFrame) {
        if (frame < 0 || frame > this.metaData[0]) {
            return;
        }
        if (this.currentFrame == frame && !resetFrame) {
            return;
        }
        this.currentFrame = frame;
        this.nextFrameIsLast = false;
        this.singleFrameDecoded = false;
        if (this.invalidateOnProgressSet) {
            this.isInvalid = true;
            if (this.loadFrameTask != null) {
                this.doNotRemoveInvalidOnFrameReady = true;
            }
        }
        if ((!async || resetFrame) && this.waitingForNextTask && this.nextRenderingBitmap != null) {
            this.backgroundBitmap = this.nextRenderingBitmap;
            this.nextRenderingBitmap = null;
            this.loadFrameTask = null;
            this.waitingForNextTask = false;
        }
        if (!async && this.loadFrameTask == null) {
            this.frameWaitSync = new CountDownLatch(1);
        }
        if (resetFrame && !this.isRunning) {
            this.isRunning = true;
        }
        if (scheduleNextGetFrame()) {
            if (!async) {
                try {
                    this.frameWaitSync.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.frameWaitSync = null;
            }
        } else {
            this.forceFrameRedraw = true;
        }
        invalidateSelf();
    }

    public void setProgressMs(long ms) {
        int frameNum = (int) ((Math.max(0L, ms) / this.timeBetweenFrames) % this.metaData[0]);
        setCurrentFrame(frameNum, true, true);
    }

    public void setProgress(float progress) {
        setProgress(progress, true);
    }

    public void setProgress(float progress, boolean async) {
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        setCurrentFrame((int) (this.metaData[0] * progress), async);
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    private boolean isCurrentParentViewMaster() {
        if (getCallback() == null && this.parentViews.size() > 1) {
            int a = 0;
            int N = this.parentViews.size();
            while (a < N) {
                View view = this.parentViews.get(a).get();
                if (view == null) {
                    this.parentViews.remove(a);
                    N--;
                    a--;
                } else if (view.isShown()) {
                    return view == this.currentParentView;
                }
                a++;
            }
            return true;
        }
        return true;
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.height;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    private void setCurrentFrame(long now, long timeDiff, long timeCheck, boolean force) {
        WeakReference<Runnable> weakReference;
        Runnable runnable;
        WeakReference<Runnable> weakReference2;
        this.backgroundBitmap = this.renderingBitmap;
        this.renderingBitmap = this.nextRenderingBitmap;
        this.nextRenderingBitmap = null;
        if (this.isDice == 2 && (weakReference2 = this.onFinishCallback) != null && this.currentFrame - 1 >= this.finishFrame) {
            Runnable runnable2 = weakReference2.get();
            if (runnable2 != null) {
                runnable2.run();
            }
            this.onFinishCallback = null;
        }
        if (this.nextFrameIsLast) {
            stop();
        }
        this.loadFrameTask = null;
        if (this.doNotRemoveInvalidOnFrameReady) {
            this.doNotRemoveInvalidOnFrameReady = false;
        } else if (this.isInvalid) {
            this.isInvalid = false;
        }
        this.singleFrameDecoded = true;
        this.waitingForNextTask = false;
        if (AndroidUtilities.screenRefreshRate <= 60.0f) {
            this.lastFrameTime = now;
        } else {
            this.lastFrameTime = now - Math.min(16L, timeDiff - timeCheck);
        }
        if (force && this.forceFrameRedraw) {
            this.singleFrameDecoded = false;
            this.forceFrameRedraw = false;
        }
        if (this.isDice == 0 && (weakReference = this.onFinishCallback) != null && this.currentFrame >= this.finishFrame && (runnable = weakReference.get()) != null) {
            runnable.run();
        }
        scheduleNextGetFrame();
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.nativePtr == 0 || this.destroyWhenDone) {
            return;
        }
        updateCurrentFrame();
        if (!this.isInvalid && this.renderingBitmap != null) {
            if (this.applyTransformation) {
                this.dstRect.set(getBounds());
                this.scaleX = this.dstRect.width() / this.width;
                this.scaleY = this.dstRect.height() / this.height;
                boolean z = false;
                this.applyTransformation = false;
                if (Math.abs(this.dstRect.width() - this.width) >= AndroidUtilities.dp(1.0f) || Math.abs(this.dstRect.width() - this.width) >= AndroidUtilities.dp(1.0f)) {
                    z = true;
                }
                this.needScale = z;
            }
            if (!this.needScale) {
                canvas.drawBitmap(this.renderingBitmap, this.dstRect.left, this.dstRect.top, getPaint());
            } else {
                canvas.save();
                canvas.translate(this.dstRect.left, this.dstRect.top);
                canvas.scale(this.scaleX, this.scaleY);
                canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                canvas.restore();
            }
            if (this.isRunning) {
                invalidateInternal();
            }
        }
    }

    public void updateCurrentFrame() {
        int timeCheck;
        Integer force;
        long now = SystemClock.elapsedRealtime();
        long timeDiff = Math.abs(now - this.lastFrameTime);
        if (AndroidUtilities.screenRefreshRate <= 60.0f) {
            timeCheck = this.timeBetweenFrames - 6;
        } else {
            timeCheck = this.timeBetweenFrames;
        }
        if (this.isRunning) {
            if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (this.nextRenderingBitmap == null) {
            } else {
                if (this.renderingBitmap == null || timeDiff >= timeCheck) {
                    HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                    if (hashMap != null && this.currentParentView != null && (force = hashMap.get(Integer.valueOf(this.currentFrame - 1))) != null) {
                        this.currentParentView.performHapticFeedback(force.intValue() == 1 ? 0 : 3, 2);
                    }
                    setCurrentFrame(now, timeDiff, timeCheck, false);
                }
            }
        } else if ((this.forceFrameRedraw || (this.decodeSingleFrame && timeDiff >= timeCheck)) && this.nextRenderingBitmap != null) {
            setCurrentFrame(now, timeDiff, timeCheck, true);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return this.height;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return this.width;
    }

    public Bitmap getRenderingBitmap() {
        return this.renderingBitmap;
    }

    public Bitmap getNextRenderingBitmap() {
        return this.nextRenderingBitmap;
    }

    public Bitmap getBackgroundBitmap() {
        return this.backgroundBitmap;
    }

    public Bitmap getAnimatedBitmap() {
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public boolean hasBitmap() {
        return this.nativePtr != 0 && !(this.renderingBitmap == null && this.nextRenderingBitmap == null) && !this.isInvalid;
    }

    public void setInvalidateOnProgressSet(boolean value) {
        this.invalidateOnProgressSet = value;
    }

    public boolean isGeneratingCache() {
        return this.cacheGenerateTask != null;
    }

    public void setOnFrameReadyRunnable(Runnable onFrameReadyRunnable) {
        this.onFrameReadyRunnable = onFrameReadyRunnable;
    }

    public boolean isLastFrame() {
        return this.currentFrame == getFramesCount() - 1;
    }
}
