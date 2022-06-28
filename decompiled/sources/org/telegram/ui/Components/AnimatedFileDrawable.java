package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatedFileDrawableStream;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    public static final int PARAM_NUM_AUDIO_FRAME_SIZE = 5;
    public static final int PARAM_NUM_BITRATE = 3;
    public static final int PARAM_NUM_COUNT = 11;
    public static final int PARAM_NUM_DURATION = 4;
    public static final int PARAM_NUM_FRAMERATE = 7;
    public static final int PARAM_NUM_HAS_AUDIO = 10;
    public static final int PARAM_NUM_HEIGHT = 2;
    public static final int PARAM_NUM_ROTATION = 8;
    public static final int PARAM_NUM_SUPPORTED_AUDIO_CODEC = 9;
    public static final int PARAM_NUM_SUPPORTED_VIDEO_CODEC = 0;
    public static final int PARAM_NUM_VIDEO_FRAME_SIZE = 6;
    public static final int PARAM_NUM_WIDTH = 1;
    private RectF actualDrawRect;
    private boolean applyTransformation;
    private Bitmap backgroundBitmap;
    private int backgroundBitmapTime;
    private BitmapShader backgroundShader;
    private int currentAccount;
    private DispatchQueue decodeQueue;
    private boolean decodeSingleFrame;
    private boolean decoderCreated;
    private boolean destroyWhenDone;
    private final TLRPC.Document document;
    private final android.graphics.Rect dstRect;
    private float endTime;
    private boolean forceDecodeAfterNextFrame;
    private int invalidateAfter;
    private boolean invalidateParentViewWithSecond;
    private boolean invalidatePath;
    private boolean invalidateTaskIsRunning;
    private volatile boolean isRecycled;
    private boolean isRestarted;
    private volatile boolean isRunning;
    public boolean isWebmSticker;
    private long lastFrameDecodeTime;
    private long lastFrameTime;
    private int lastTimeStamp;
    private boolean limitFps;
    private Runnable loadFrameRunnable;
    private Runnable loadFrameTask;
    private final Runnable mStartTask;
    private final int[] metaData;
    public volatile long nativePtr;
    private Bitmap nextRenderingBitmap;
    private int nextRenderingBitmapTime;
    private BitmapShader nextRenderingShader;
    private View parentView;
    private ArrayList<ImageReceiver> parents;
    private File path;
    private boolean pendingRemoveLoading;
    private int pendingRemoveLoadingFramesReset;
    private volatile long pendingSeekTo;
    private volatile long pendingSeekToUI;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private int renderingBitmapTime;
    private int renderingHeight;
    private BitmapShader renderingShader;
    private int renderingWidth;
    public int repeatCount;
    private Path roundPath;
    private int[] roundRadius;
    private int[] roundRadiusBackup;
    private float scaleFactor;
    private float scaleX;
    private float scaleY;
    private ArrayList<View> secondParentViews;
    private Matrix shaderMatrix;
    private boolean singleFrameDecoded;
    private float startTime;
    private AnimatedFileDrawableStream stream;
    private long streamFileSize;
    private final Object sync;
    private Runnable uiRunnable;
    private Runnable uiRunnableNoFrame;
    private boolean useSharedQueue;
    private static float[] radii = new float[8];
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8, new ThreadPoolExecutor.DiscardPolicy());
    private static ScheduledThreadPoolExecutor generateCache = new ScheduledThreadPoolExecutor(4, new ThreadPoolExecutor.DiscardPolicy());

    public static native long createDecoder(String str, int[] iArr, int i, long j, Object obj, boolean z);

    public static native void destroyDecoder(long j);

    private static native int getFrameAtTime(long j, long j2, Bitmap bitmap, int[] iArr, int i);

    public static native int getVideoFrame(long j, Bitmap bitmap, int[] iArr, int i, boolean z, float f, float f2);

    private static native void getVideoInfo(int i, String str, int[] iArr);

    private static native void prepareToSeek(long j);

    public static native void seekToMs(long j, long j2, boolean z);

    private static native void stopDecoder(long j);

    static /* synthetic */ int access$1110(AnimatedFileDrawable x0) {
        int i = x0.pendingRemoveLoadingFramesReset;
        x0.pendingRemoveLoadingFramesReset = i - 1;
        return i;
    }

    public void invalidateInternal() {
        for (int i = 0; i < this.parents.size(); i++) {
            if (this.parents.get(i).getParentView() != null) {
                this.parents.get(i).getParentView().invalidate();
            }
        }
    }

    public void checkRepeat() {
        int count = 0;
        int j = 0;
        while (j < this.parents.size()) {
            ImageReceiver parent = this.parents.get(j);
            if (!parent.isAttachedToWindow()) {
                this.parents.remove(j);
                j--;
            }
            if (parent.animatedFileDrawableRepeatMaxCount > 0 && this.repeatCount >= parent.animatedFileDrawableRepeatMaxCount) {
                count++;
            }
            j++;
        }
        if (this.parents.size() == count) {
            stop();
        } else {
            start();
        }
    }

    public void updateScaleFactor() {
        int i;
        int i2;
        if (!this.isWebmSticker && (i = this.renderingHeight) > 0 && (i2 = this.renderingWidth) > 0) {
            int[] iArr = this.metaData;
            if (iArr[0] > 0 && iArr[1] > 0) {
                float max = Math.max(i2 / iArr[0], i / iArr[1]);
                this.scaleFactor = max;
                if (max <= 0.0f || max > 0.7d) {
                    this.scaleFactor = 1.0f;
                    return;
                }
                return;
            }
        }
        this.scaleFactor = 1.0f;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AnimatedFileDrawable */
    public /* synthetic */ void m2181lambda$new$0$orgtelegramuiComponentsAnimatedFileDrawable() {
        View view;
        if (!this.secondParentViews.isEmpty()) {
            int N = this.secondParentViews.size();
            for (int a = 0; a < N; a++) {
                this.secondParentViews.get(a).invalidate();
            }
        }
        if ((this.secondParentViews.isEmpty() || this.invalidateParentViewWithSecond) && (view = this.parentView) != null) {
            view.invalidate();
        }
    }

    public AnimatedFileDrawable(File file, boolean createDecoder, long streamSize, TLRPC.Document document, ImageLocation location, Object parentObject, long seekTo, int account, boolean preview) {
        this(file, createDecoder, streamSize, document, location, parentObject, seekTo, account, preview, 0, 0);
    }

    public AnimatedFileDrawable(File file, boolean createDecoder, long streamSize, TLRPC.Document document, ImageLocation location, Object parentObject, long seekTo, int account, boolean preview, int w, int h) {
        long j;
        this.invalidateAfter = 50;
        int[] iArr = new int[5];
        this.metaData = iArr;
        this.pendingSeekTo = -1L;
        this.pendingSeekToUI = -1L;
        this.sync = new Object();
        this.actualDrawRect = new RectF();
        this.roundRadius = new int[4];
        this.shaderMatrix = new Matrix();
        this.roundPath = new Path();
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new android.graphics.Rect();
        this.scaleFactor = 1.0f;
        this.secondParentViews = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.invalidatePath = true;
        this.uiRunnableNoFrame = new Runnable() { // from class: org.telegram.ui.Components.AnimatedFileDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                    AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                    AnimatedFileDrawable.this.nativePtr = 0L;
                }
                if (AnimatedFileDrawable.this.nativePtr == 0) {
                    if (AnimatedFileDrawable.this.renderingBitmap != null) {
                        AnimatedFileDrawable.this.renderingBitmap.recycle();
                        AnimatedFileDrawable.this.renderingBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                        AnimatedFileDrawable.this.backgroundBitmap.recycle();
                        AnimatedFileDrawable.this.backgroundBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.decodeQueue != null) {
                        AnimatedFileDrawable.this.decodeQueue.recycle();
                        AnimatedFileDrawable.this.decodeQueue = null;
                        return;
                    }
                    return;
                }
                AnimatedFileDrawable.this.loadFrameTask = null;
                AnimatedFileDrawable.this.scheduleNextGetFrame();
                AnimatedFileDrawable.this.invalidateInternal();
            }
        };
        this.uiRunnable = new Runnable() { // from class: org.telegram.ui.Components.AnimatedFileDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                    AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                    AnimatedFileDrawable.this.nativePtr = 0L;
                }
                if (AnimatedFileDrawable.this.nativePtr == 0) {
                    if (AnimatedFileDrawable.this.renderingBitmap != null) {
                        AnimatedFileDrawable.this.renderingBitmap.recycle();
                        AnimatedFileDrawable.this.renderingBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                        AnimatedFileDrawable.this.backgroundBitmap.recycle();
                        AnimatedFileDrawable.this.backgroundBitmap = null;
                    }
                    if (AnimatedFileDrawable.this.decodeQueue != null) {
                        AnimatedFileDrawable.this.decodeQueue.recycle();
                        AnimatedFileDrawable.this.decodeQueue = null;
                        return;
                    }
                    return;
                }
                if (AnimatedFileDrawable.this.stream != null && AnimatedFileDrawable.this.pendingRemoveLoading) {
                    FileLoader.getInstance(AnimatedFileDrawable.this.currentAccount).removeLoadingVideo(AnimatedFileDrawable.this.stream.getDocument(), false, false);
                }
                if (AnimatedFileDrawable.this.pendingRemoveLoadingFramesReset <= 0) {
                    AnimatedFileDrawable.this.pendingRemoveLoading = true;
                } else {
                    AnimatedFileDrawable.access$1110(AnimatedFileDrawable.this);
                }
                if (!AnimatedFileDrawable.this.forceDecodeAfterNextFrame) {
                    AnimatedFileDrawable.this.singleFrameDecoded = true;
                } else {
                    AnimatedFileDrawable.this.forceDecodeAfterNextFrame = false;
                }
                AnimatedFileDrawable.this.loadFrameTask = null;
                AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
                animatedFileDrawable.nextRenderingBitmap = animatedFileDrawable.backgroundBitmap;
                AnimatedFileDrawable animatedFileDrawable2 = AnimatedFileDrawable.this;
                animatedFileDrawable2.nextRenderingBitmapTime = animatedFileDrawable2.backgroundBitmapTime;
                AnimatedFileDrawable animatedFileDrawable3 = AnimatedFileDrawable.this;
                animatedFileDrawable3.nextRenderingShader = animatedFileDrawable3.backgroundShader;
                if (AnimatedFileDrawable.this.isRestarted) {
                    AnimatedFileDrawable.this.isRestarted = false;
                    AnimatedFileDrawable.this.repeatCount++;
                    AnimatedFileDrawable.this.checkRepeat();
                }
                if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
                    AnimatedFileDrawable animatedFileDrawable4 = AnimatedFileDrawable.this;
                    animatedFileDrawable4.lastTimeStamp = animatedFileDrawable4.startTime > 0.0f ? (int) (AnimatedFileDrawable.this.startTime * 1000.0f) : 0;
                }
                if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                    AnimatedFileDrawable animatedFileDrawable5 = AnimatedFileDrawable.this;
                    animatedFileDrawable5.invalidateAfter = animatedFileDrawable5.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp;
                    if (AnimatedFileDrawable.this.limitFps && AnimatedFileDrawable.this.invalidateAfter < 32) {
                        AnimatedFileDrawable.this.invalidateAfter = 32;
                    }
                }
                if (AnimatedFileDrawable.this.pendingSeekToUI >= 0 && AnimatedFileDrawable.this.pendingSeekTo == -1) {
                    AnimatedFileDrawable.this.pendingSeekToUI = -1L;
                    AnimatedFileDrawable.this.invalidateAfter = 0;
                }
                AnimatedFileDrawable animatedFileDrawable6 = AnimatedFileDrawable.this;
                animatedFileDrawable6.lastTimeStamp = animatedFileDrawable6.metaData[3];
                if (!AnimatedFileDrawable.this.secondParentViews.isEmpty()) {
                    int N = AnimatedFileDrawable.this.secondParentViews.size();
                    for (int a = 0; a < N; a++) {
                        ((View) AnimatedFileDrawable.this.secondParentViews.get(a)).invalidate();
                    }
                }
                AnimatedFileDrawable.this.invalidateInternal();
                AnimatedFileDrawable.this.scheduleNextGetFrame();
            }
        };
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.AnimatedFileDrawable.3
            @Override // java.lang.Runnable
            public void run() {
                if (!AnimatedFileDrawable.this.isRecycled) {
                    if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                        AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
                        animatedFileDrawable.nativePtr = AnimatedFileDrawable.createDecoder(animatedFileDrawable.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.currentAccount, AnimatedFileDrawable.this.streamFileSize, AnimatedFileDrawable.this.stream, false);
                        if (AnimatedFileDrawable.this.nativePtr != 0 && (AnimatedFileDrawable.this.metaData[0] > 3840 || AnimatedFileDrawable.this.metaData[1] > 3840)) {
                            AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                            AnimatedFileDrawable.this.nativePtr = 0L;
                        }
                        AnimatedFileDrawable.this.updateScaleFactor();
                        AnimatedFileDrawable.this.decoderCreated = true;
                    }
                    try {
                        if (AnimatedFileDrawable.this.nativePtr == 0 && AnimatedFileDrawable.this.metaData[0] != 0 && AnimatedFileDrawable.this.metaData[1] != 0) {
                            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                            return;
                        }
                        if (AnimatedFileDrawable.this.backgroundBitmap == null && AnimatedFileDrawable.this.metaData[0] > 0 && AnimatedFileDrawable.this.metaData[1] > 0) {
                            AnimatedFileDrawable animatedFileDrawable2 = AnimatedFileDrawable.this;
                            animatedFileDrawable2.backgroundBitmap = Bitmap.createBitmap((int) (animatedFileDrawable2.metaData[0] * AnimatedFileDrawable.this.scaleFactor), (int) (AnimatedFileDrawable.this.metaData[1] * AnimatedFileDrawable.this.scaleFactor), Bitmap.Config.ARGB_8888);
                            if (AnimatedFileDrawable.this.backgroundShader == null && AnimatedFileDrawable.this.backgroundBitmap != null && AnimatedFileDrawable.this.hasRoundRadius()) {
                                AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                            }
                        }
                        boolean seekWas = false;
                        if (AnimatedFileDrawable.this.pendingSeekTo >= 0) {
                            AnimatedFileDrawable.this.metaData[3] = (int) AnimatedFileDrawable.this.pendingSeekTo;
                            long seekTo2 = AnimatedFileDrawable.this.pendingSeekTo;
                            synchronized (AnimatedFileDrawable.this.sync) {
                                AnimatedFileDrawable.this.pendingSeekTo = -1L;
                            }
                            seekWas = true;
                            if (AnimatedFileDrawable.this.stream != null) {
                                AnimatedFileDrawable.this.stream.reset();
                            }
                            AnimatedFileDrawable.seekToMs(AnimatedFileDrawable.this.nativePtr, seekTo2, true);
                        }
                        if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                            AnimatedFileDrawable.this.lastFrameDecodeTime = System.currentTimeMillis();
                            if (AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.backgroundBitmap.getRowBytes(), false, AnimatedFileDrawable.this.startTime, AnimatedFileDrawable.this.endTime) == 0) {
                                AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                                return;
                            }
                            if (AnimatedFileDrawable.this.lastTimeStamp != 0 && AnimatedFileDrawable.this.metaData[3] == 0) {
                                AnimatedFileDrawable.this.isRestarted = true;
                            }
                            if (seekWas) {
                                AnimatedFileDrawable animatedFileDrawable3 = AnimatedFileDrawable.this;
                                animatedFileDrawable3.lastTimeStamp = animatedFileDrawable3.metaData[3];
                            }
                            AnimatedFileDrawable animatedFileDrawable4 = AnimatedFileDrawable.this;
                            animatedFileDrawable4.backgroundBitmapTime = animatedFileDrawable4.metaData[3];
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
            }
        };
        this.mStartTask = new Runnable() { // from class: org.telegram.ui.Components.AnimatedFileDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnimatedFileDrawable.this.m2181lambda$new$0$orgtelegramuiComponentsAnimatedFileDrawable();
            }
        };
        this.path = file;
        this.streamFileSize = streamSize;
        this.currentAccount = account;
        this.renderingHeight = h;
        this.renderingWidth = w;
        this.document = document;
        getPaint().setFlags(3);
        if (streamSize == 0) {
            j = 0;
        } else if (document != null || location != null) {
            j = 0;
            this.stream = new AnimatedFileDrawableStream(document, location, parentObject, account, preview);
        } else {
            j = 0;
        }
        if (createDecoder) {
            this.nativePtr = createDecoder(file.getAbsolutePath(), iArr, this.currentAccount, this.streamFileSize, this.stream, preview);
            if (this.nativePtr != j && (iArr[0] > 3840 || iArr[1] > 3840)) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = j;
            }
            updateScaleFactor();
            this.decoderCreated = true;
        }
        if (seekTo != j) {
            seekTo(seekTo, false);
        }
    }

    public void setIsWebmSticker(boolean b) {
        this.isWebmSticker = b;
        if (b) {
            this.useSharedQueue = true;
        }
    }

    public Bitmap getFrameAtTime(long ms) {
        return getFrameAtTime(ms, false);
    }

    public Bitmap getFrameAtTime(long ms, boolean precise) {
        int result;
        if (!this.decoderCreated || this.nativePtr == 0) {
            return null;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(false);
            this.stream.reset();
        }
        if (!precise) {
            seekToMs(this.nativePtr, ms, precise);
        }
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            float f = this.scaleFactor;
            this.backgroundBitmap = Bitmap.createBitmap((int) (iArr[0] * f), (int) (iArr[1] * f), Bitmap.Config.ARGB_8888);
        }
        if (precise) {
            long j = this.nativePtr;
            Bitmap bitmap = this.backgroundBitmap;
            result = getFrameAtTime(j, ms, bitmap, this.metaData, bitmap.getRowBytes());
        } else {
            long j2 = this.nativePtr;
            Bitmap bitmap2 = this.backgroundBitmap;
            result = getVideoFrame(j2, bitmap2, this.metaData, bitmap2.getRowBytes(), true, 0.0f, 0.0f);
        }
        if (result == 0) {
            return null;
        }
        return this.backgroundBitmap;
    }

    public void setParentView(View view) {
        if (this.parentView != null) {
            return;
        }
        this.parentView = view;
    }

    public void addParent(ImageReceiver imageReceiver) {
        if (imageReceiver != null && !this.parents.contains(imageReceiver)) {
            this.parents.add(imageReceiver);
            if (this.isRunning) {
                scheduleNextGetFrame();
            }
        }
    }

    public void removeParent(ImageReceiver imageReceiver) {
        this.parents.remove(imageReceiver);
        if (this.parents.size() == 0) {
            this.repeatCount = 0;
        }
    }

    public void setInvalidateParentViewWithSecond(boolean value) {
        this.invalidateParentViewWithSecond = value;
    }

    public void addSecondParentView(View view) {
        if (view == null || this.secondParentViews.contains(view)) {
            return;
        }
        this.secondParentViews.add(view);
    }

    public void removeSecondParentView(View view) {
        this.secondParentViews.remove(view);
        if (this.secondParentViews.isEmpty()) {
            if (this.recycleWithSecond) {
                recycle();
                return;
            }
            int[] iArr = this.roundRadiusBackup;
            if (iArr != null) {
                setRoundRadius(iArr);
            }
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (value) {
            scheduleNextGetFrame();
        }
    }

    public void seekTo(long ms, boolean removeLoading) {
        seekTo(ms, removeLoading, false);
    }

    public void seekTo(long ms, boolean removeLoading, boolean force) {
        AnimatedFileDrawableStream animatedFileDrawableStream;
        synchronized (this.sync) {
            this.pendingSeekTo = ms;
            this.pendingSeekToUI = ms;
            if (this.nativePtr != 0) {
                prepareToSeek(this.nativePtr);
            }
            if (this.decoderCreated && (animatedFileDrawableStream = this.stream) != null) {
                animatedFileDrawableStream.cancel(removeLoading);
                this.pendingRemoveLoading = removeLoading;
                this.pendingRemoveLoadingFramesReset = removeLoading ? 0 : 10;
            }
            if (force && this.decodeSingleFrame) {
                this.singleFrameDecoded = false;
                if (this.loadFrameTask == null) {
                    scheduleNextGetFrame();
                } else {
                    this.forceDecodeAfterNextFrame = true;
                }
            }
        }
    }

    public void recycle() {
        if (!this.secondParentViews.isEmpty()) {
            this.recycleWithSecond = true;
            return;
        }
        this.isRunning = false;
        this.isRecycled = true;
        if (this.loadFrameTask == null) {
            if (this.nativePtr != 0) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = 0L;
            }
            ArrayList<Bitmap> bitmapToRecycle = new ArrayList<>();
            bitmapToRecycle.add(this.renderingBitmap);
            bitmapToRecycle.add(this.nextRenderingBitmap);
            if (this.renderingBitmap != null) {
                this.renderingBitmap = null;
            }
            if (this.nextRenderingBitmap != null) {
                this.nextRenderingBitmap = null;
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            if (dispatchQueue != null) {
                dispatchQueue.recycle();
                this.decodeQueue = null;
            }
            getPaint().setShader(null);
            AndroidUtilities.recycleBitmaps(bitmapToRecycle);
        } else {
            this.destroyWhenDone = true;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
        invalidateInternal();
    }

    public void resetStream(boolean stop) {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
        if (this.nativePtr != 0) {
            if (stop) {
                stopDecoder(this.nativePtr);
            } else {
                prepareToSeek(this.nativePtr);
            }
        }
    }

    protected static void runOnUiThread(Runnable task) {
        Looper myLooper = Looper.myLooper();
        Handler handler = uiHandler;
        if (myLooper == handler.getLooper()) {
            task.run();
        } else {
            handler.post(task);
        }
    }

    public void setUseSharedQueue(boolean value) {
        if (this.isWebmSticker) {
            return;
        }
        this.useSharedQueue = value;
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
        if (this.isRunning || this.parents.size() == 0) {
            return;
        }
        this.isRunning = true;
        scheduleNextGetFrame();
        runOnUiThread(this.mStartTask);
    }

    public float getCurrentProgress() {
        if (this.metaData[4] == 0) {
            return 0.0f;
        }
        if (this.pendingSeekToUI >= 0) {
            return ((float) this.pendingSeekToUI) / this.metaData[4];
        }
        int[] iArr = this.metaData;
        return iArr[3] / iArr[4];
    }

    public int getCurrentProgressMs() {
        if (this.pendingSeekToUI >= 0) {
            return (int) this.pendingSeekToUI;
        }
        int i = this.nextRenderingBitmapTime;
        return i != 0 ? i : this.renderingBitmapTime;
    }

    public int getDurationMs() {
        return this.metaData[4];
    }

    public void scheduleNextGetFrame() {
        if (this.loadFrameTask == null) {
            if ((this.nativePtr == 0 && this.decoderCreated) || this.destroyWhenDone) {
                return;
            }
            if (!this.isRunning) {
                boolean z = this.decodeSingleFrame;
                if (!z) {
                    return;
                }
                if (z && this.singleFrameDecoded) {
                    return;
                }
            }
            if (this.parents.size() == 0) {
                return;
            }
            long ms = 0;
            if (this.lastFrameDecodeTime != 0) {
                int i = this.invalidateAfter;
                ms = Math.min(i, Math.max(0L, i - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
            }
            if (this.useSharedQueue) {
                ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
                Runnable runnable = this.loadFrameRunnable;
                this.loadFrameTask = runnable;
                scheduledThreadPoolExecutor.schedule(runnable, ms, TimeUnit.MILLISECONDS);
                return;
            }
            if (this.decodeQueue == null) {
                this.decodeQueue = new DispatchQueue("decodeQueue" + this);
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            Runnable runnable2 = this.loadFrameRunnable;
            this.loadFrameTask = runnable2;
            dispatchQueue.postRunnable(runnable2, ms);
        }
    }

    public boolean isLoadingStream() {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        return animatedFileDrawableStream != null && animatedFileDrawableStream.isWaitingForLoad();
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.isRunning = false;
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.isRunning;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[0] : iArr[1];
        }
        int height = i;
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return (int) (height * this.scaleFactor);
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[1] : iArr[0];
        }
        int width = i;
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return (int) (width * this.scaleFactor);
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    protected void onBoundsChange(android.graphics.Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Bitmap bitmap;
        if ((this.nativePtr == 0 && this.decoderCreated) || this.destroyWhenDone) {
            return;
        }
        long now = System.currentTimeMillis();
        if (this.isRunning) {
            Bitmap bitmap2 = this.renderingBitmap;
            if (bitmap2 == null && this.nextRenderingBitmap == null) {
                scheduleNextGetFrame();
            } else if (this.nextRenderingBitmap != null && (bitmap2 == null || Math.abs(now - this.lastFrameTime) >= this.invalidateAfter)) {
                this.renderingBitmap = this.nextRenderingBitmap;
                this.renderingBitmapTime = this.nextRenderingBitmapTime;
                this.renderingShader = this.nextRenderingShader;
                this.nextRenderingBitmap = null;
                this.nextRenderingBitmapTime = 0;
                this.nextRenderingShader = null;
                this.lastFrameTime = now;
            }
        } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(now - this.lastFrameTime) >= this.invalidateAfter && (bitmap = this.nextRenderingBitmap) != null) {
            this.renderingBitmap = bitmap;
            this.renderingBitmapTime = this.nextRenderingBitmapTime;
            this.renderingShader = this.nextRenderingShader;
            this.nextRenderingBitmap = null;
            this.nextRenderingBitmapTime = 0;
            this.nextRenderingShader = null;
            this.lastFrameTime = now;
        }
        Bitmap bitmap3 = this.renderingBitmap;
        if (bitmap3 != null) {
            if (this.applyTransformation) {
                int bitmapW = bitmap3.getWidth();
                int bitmapH = this.renderingBitmap.getHeight();
                int[] iArr = this.metaData;
                if (iArr[2] == 90 || iArr[2] == 270) {
                    bitmapW = bitmapH;
                    bitmapH = bitmapW;
                }
                this.dstRect.set(getBounds());
                this.scaleX = this.dstRect.width() / bitmapW;
                this.scaleY = this.dstRect.height() / bitmapH;
                this.applyTransformation = false;
            }
            if (!hasRoundRadius()) {
                canvas.translate(this.dstRect.left, this.dstRect.top);
                int[] iArr2 = this.metaData;
                if (iArr2[2] == 90) {
                    canvas.rotate(90.0f);
                    canvas.translate(0.0f, -this.dstRect.width());
                } else if (iArr2[2] == 180) {
                    canvas.rotate(180.0f);
                    canvas.translate(-this.dstRect.width(), -this.dstRect.height());
                } else if (iArr2[2] == 270) {
                    canvas.rotate(270.0f);
                    canvas.translate(-this.dstRect.height(), 0.0f);
                }
                canvas.scale(this.scaleX, this.scaleY);
                canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                return;
            }
            if (this.renderingShader == null) {
                this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            Paint paint = getPaint();
            paint.setShader(this.renderingShader);
            this.shaderMatrix.reset();
            this.shaderMatrix.setTranslate(this.dstRect.left, this.dstRect.top);
            int[] iArr3 = this.metaData;
            if (iArr3[2] == 90) {
                this.shaderMatrix.preRotate(90.0f);
                this.shaderMatrix.preTranslate(0.0f, -this.dstRect.width());
            } else if (iArr3[2] == 180) {
                this.shaderMatrix.preRotate(180.0f);
                this.shaderMatrix.preTranslate(-this.dstRect.width(), -this.dstRect.height());
            } else if (iArr3[2] == 270) {
                this.shaderMatrix.preRotate(270.0f);
                this.shaderMatrix.preTranslate(-this.dstRect.height(), 0.0f);
            }
            this.shaderMatrix.preScale(this.scaleX, this.scaleY);
            this.renderingShader.setLocalMatrix(this.shaderMatrix);
            if (this.invalidatePath) {
                this.invalidatePath = false;
                int a = 0;
                while (true) {
                    int[] iArr4 = this.roundRadius;
                    if (a >= iArr4.length) {
                        break;
                    }
                    float[] fArr = radii;
                    fArr[a * 2] = iArr4[a];
                    fArr[(a * 2) + 1] = iArr4[a];
                    a++;
                }
                this.roundPath.reset();
                this.roundPath.addRoundRect(this.actualDrawRect, radii, Path.Direction.CW);
                this.roundPath.close();
            }
            canvas.drawPath(this.roundPath, paint);
        }
    }

    public long getLastFrameTimestamp() {
        return this.lastTimeStamp;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[0] : iArr[1];
        }
        int height = i;
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return height;
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? iArr[1] : iArr[0];
        }
        int width = i;
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return width;
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
        Bitmap bitmap = this.renderingBitmap;
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap bitmap2 = this.nextRenderingBitmap;
        if (bitmap2 != null) {
            return bitmap2;
        }
        return null;
    }

    public void setActualDrawRect(float x, float y, float width, float height) {
        float bottom = y + height;
        float right = x + width;
        if (this.actualDrawRect.left != x || this.actualDrawRect.top != y || this.actualDrawRect.right != right || this.actualDrawRect.bottom != bottom) {
            this.actualDrawRect.set(x, y, right, bottom);
            this.invalidatePath = true;
        }
    }

    public void setRoundRadius(int[] value) {
        if (!this.secondParentViews.isEmpty()) {
            if (this.roundRadiusBackup == null) {
                this.roundRadiusBackup = new int[4];
            }
            int[] iArr = this.roundRadius;
            int[] iArr2 = this.roundRadiusBackup;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        }
        for (int i = 0; i < 4; i++) {
            if (!this.invalidatePath && value[i] != this.roundRadius[i]) {
                this.invalidatePath = true;
            }
            this.roundRadius[i] = value[i];
        }
    }

    public boolean hasRoundRadius() {
        int a = 0;
        while (true) {
            int[] iArr = this.roundRadius;
            if (a < iArr.length) {
                if (iArr[a] == 0) {
                    a++;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }

    public int getOrientation() {
        return this.metaData[2];
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable drawable;
        if (this.stream != null) {
            File file = this.path;
            long j = this.streamFileSize;
            TLRPC.Document document = this.stream.getDocument();
            ImageLocation location = this.stream.getLocation();
            Object parentObject = this.stream.getParentObject();
            long j2 = this.pendingSeekToUI;
            int i = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
            drawable = new AnimatedFileDrawable(file, false, j, document, location, parentObject, j2, i, animatedFileDrawableStream != null && animatedFileDrawableStream.isPreview());
        } else {
            File file2 = this.path;
            long j3 = this.streamFileSize;
            TLRPC.Document document2 = this.document;
            long j4 = this.pendingSeekToUI;
            int i2 = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream2 = this.stream;
            drawable = new AnimatedFileDrawable(file2, false, j3, document2, null, null, j4, i2, animatedFileDrawableStream2 != null && animatedFileDrawableStream2.isPreview());
        }
        int[] iArr = drawable.metaData;
        int[] iArr2 = this.metaData;
        iArr[0] = iArr2[0];
        iArr[1] = iArr2[1];
        return drawable;
    }

    public static void getVideoInfo(String src, int[] params) {
        getVideoInfo(Build.VERSION.SDK_INT, src, params);
    }

    public void setStartEndTime(long startTime, long endTime) {
        this.startTime = ((float) startTime) / 1000.0f;
        this.endTime = ((float) endTime) / 1000.0f;
        if (getCurrentProgressMs() < startTime) {
            seekTo(startTime, true);
        }
    }

    public long getStartTime() {
        return this.startTime * 1000.0f;
    }

    public boolean isRecycled() {
        return this.isRecycled;
    }

    public Bitmap getNextFrame() {
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            float f = this.scaleFactor;
            this.backgroundBitmap = Bitmap.createBitmap((int) (iArr[0] * f), (int) (iArr[1] * f), Bitmap.Config.ARGB_8888);
        }
        long j = this.nativePtr;
        Bitmap bitmap = this.backgroundBitmap;
        getVideoFrame(j, bitmap, this.metaData, bitmap.getRowBytes(), false, this.startTime, this.endTime);
        return this.backgroundBitmap;
    }

    public void setLimitFps(boolean limitFps) {
        this.limitFps = limitFps;
    }

    public ArrayList<ImageReceiver> getParents() {
        return this.parents;
    }

    public File getFilePath() {
        return this.path;
    }
}
