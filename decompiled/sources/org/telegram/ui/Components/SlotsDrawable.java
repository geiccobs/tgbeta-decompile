package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.text.TextUtils;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatMessageCell;
/* loaded from: classes5.dex */
public class SlotsDrawable extends RLottieDrawable {
    private ReelValue center;
    private ReelValue left;
    private boolean playWinAnimation;
    private ReelValue right;
    private long[] nativePtrs = new long[5];
    private int[] frameCounts = new int[5];
    private int[] frameNums = new int[5];
    private long[] secondNativePtrs = new long[3];
    private int[] secondFrameCounts = new int[3];
    private int[] secondFrameNums = new int[3];

    /* loaded from: classes5.dex */
    public enum ReelValue {
        bar,
        berries,
        lemon,
        seven,
        sevenWin
    }

    public SlotsDrawable(String diceEmoji, int w, int h) {
        super(diceEmoji, w, h);
        this.loadFrameRunnable = new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SlotsDrawable.this.m3065lambda$new$0$orgtelegramuiComponentsSlotsDrawable();
            }
        };
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3065lambda$new$0$orgtelegramuiComponentsSlotsDrawable() {
        int i;
        int result;
        if (this.isRecycled) {
            return;
        }
        if (this.nativePtr == 0 || (this.isDice == 2 && this.secondNativePtr == 0)) {
            if (this.frameWaitSync != null) {
                this.frameWaitSync.countDown();
            }
            uiHandler.post(this.uiRunnableNoFrame);
            return;
        }
        if (this.backgroundBitmap == null) {
            try {
                this.backgroundBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        if (this.backgroundBitmap != null) {
            try {
                if (this.isDice == 1) {
                    result = -1;
                    int a = 0;
                    while (true) {
                        long[] jArr = this.nativePtrs;
                        if (a >= jArr.length) {
                            break;
                        }
                        result = getFrame(jArr[a], this.frameNums[a], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), a == 0);
                        if (a != 0) {
                            int[] iArr = this.frameNums;
                            if (iArr[a] + 1 < this.frameCounts[a]) {
                                iArr[a] = iArr[a] + 1;
                            } else if (a != 4) {
                                iArr[a] = 0;
                                this.nextFrameIsLast = false;
                                if (this.secondNativePtr != 0) {
                                    this.isDice = 2;
                                }
                            }
                        }
                        a++;
                    }
                    i = -1;
                } else {
                    if (this.setLastFrame) {
                        int a2 = 0;
                        while (true) {
                            int[] iArr2 = this.secondFrameNums;
                            if (a2 >= iArr2.length) {
                                break;
                            }
                            iArr2[a2] = this.secondFrameCounts[a2] - 1;
                            a2++;
                        }
                    }
                    if (this.playWinAnimation) {
                        int[] iArr3 = this.frameNums;
                        if (iArr3[0] + 1 < this.frameCounts[0]) {
                            iArr3[0] = iArr3[0] + 1;
                        } else {
                            iArr3[0] = -1;
                        }
                    }
                    getFrame(this.nativePtrs[0], Math.max(this.frameNums[0], 0), this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), true);
                    int a3 = 0;
                    while (true) {
                        long[] jArr2 = this.secondNativePtrs;
                        if (a3 >= jArr2.length) {
                            break;
                        }
                        long j = jArr2[a3];
                        int[] iArr4 = this.secondFrameNums;
                        getFrame(j, iArr4[a3] >= 0 ? iArr4[a3] : this.secondFrameCounts[a3] - 1, this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                        if (!this.nextFrameIsLast) {
                            int[] iArr5 = this.secondFrameNums;
                            if (iArr5[a3] + 1 < this.secondFrameCounts[a3]) {
                                iArr5[a3] = iArr5[a3] + 1;
                            } else {
                                iArr5[a3] = -1;
                            }
                        }
                        a3++;
                    }
                    result = getFrame(this.nativePtrs[4], this.frameNums[4], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                    int[] iArr6 = this.frameNums;
                    if (iArr6[4] + 1 < this.frameCounts[4]) {
                        iArr6[4] = iArr6[4] + 1;
                    }
                    int[] iArr7 = this.secondFrameNums;
                    if (iArr7[0] == -1 && iArr7[1] == -1 && iArr7[2] == -1) {
                        this.nextFrameIsLast = true;
                        this.autoRepeatPlayCount++;
                    }
                    ReelValue reelValue = this.left;
                    ReelValue reelValue2 = this.right;
                    if (reelValue == reelValue2 && reelValue2 == this.center) {
                        if (this.secondFrameNums[0] != this.secondFrameCounts[0] - 100) {
                            i = -1;
                        } else {
                            this.playWinAnimation = true;
                            if (reelValue != ReelValue.sevenWin) {
                                i = -1;
                            } else {
                                Runnable runnable = this.onFinishCallback.get();
                                if (runnable != null) {
                                    AndroidUtilities.runOnUIThread(runnable);
                                }
                                i = -1;
                            }
                        }
                    } else {
                        i = -1;
                        this.frameNums[0] = -1;
                    }
                }
                if (result == i) {
                    uiHandler.post(this.uiRunnableNoFrame);
                    if (this.frameWaitSync != null) {
                        this.frameWaitSync.countDown();
                        return;
                    }
                    return;
                }
                this.nextRenderingBitmap = this.backgroundBitmap;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        uiHandler.post(this.uiRunnable);
        if (this.frameWaitSync != null) {
            this.frameWaitSync.countDown();
        }
    }

    private ReelValue reelValue(int rawValue) {
        switch (rawValue) {
            case 0:
                return ReelValue.bar;
            case 1:
                return ReelValue.berries;
            case 2:
                return ReelValue.lemon;
            default:
                return ReelValue.seven;
        }
    }

    private void init(int rawValue) {
        int rawValue2 = rawValue - 1;
        int leftRawValue = rawValue2 & 3;
        int centerRawValue = (rawValue2 >> 2) & 3;
        int rightRawValue = rawValue2 >> 4;
        ReelValue leftReelValue = reelValue(leftRawValue);
        ReelValue centerReelValue = reelValue(centerRawValue);
        ReelValue rightReelValue = reelValue(rightRawValue);
        if (leftReelValue == ReelValue.seven && centerReelValue == ReelValue.seven && rightReelValue == ReelValue.seven) {
            leftReelValue = ReelValue.sevenWin;
            centerReelValue = ReelValue.sevenWin;
            rightReelValue = ReelValue.sevenWin;
        }
        this.left = leftReelValue;
        this.center = centerReelValue;
        this.right = rightReelValue;
    }

    private boolean is777() {
        return this.left == ReelValue.sevenWin && this.center == ReelValue.sevenWin && this.right == ReelValue.sevenWin;
    }

    public boolean setBaseDice(final ChatMessageCell messageCell, final TLRPC.TL_messages_stickerSet stickerSet) {
        if (this.nativePtr != 0 || this.loadingInBackground) {
            return true;
        }
        this.loadingInBackground = true;
        final MessageObject currentMessageObject = messageCell.getMessageObject();
        final int account = messageCell.getMessageObject().currentAccount;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                SlotsDrawable.this.m3069lambda$setBaseDice$5$orgtelegramuiComponentsSlotsDrawable(stickerSet, account, currentMessageObject, messageCell);
            }
        });
        return true;
    }

    /* renamed from: lambda$setBaseDice$5$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3069lambda$setBaseDice$5$orgtelegramuiComponentsSlotsDrawable(final TLRPC.TL_messages_stickerSet stickerSet, final int account, final MessageObject currentMessageObject, final ChatMessageCell messageCell) {
        int num;
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    SlotsDrawable.this.m3066lambda$setBaseDice$1$orgtelegramuiComponentsSlotsDrawable();
                }
            });
            return;
        }
        boolean loading = false;
        int a = 0;
        while (true) {
            long[] jArr = this.nativePtrs;
            if (a >= jArr.length) {
                break;
            }
            if (jArr[a] == 0) {
                if (a == 0) {
                    num = 1;
                } else if (a == 1) {
                    num = 8;
                } else if (a == 2) {
                    num = 14;
                } else if (a == 3) {
                    num = 20;
                } else {
                    num = 2;
                }
                final TLRPC.Document document = stickerSet.documents.get(num);
                File path = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
                String json = readRes(path, 0);
                if (!TextUtils.isEmpty(json)) {
                    this.nativePtrs[a] = createWithJson(json, "dice", this.metaData, null);
                    this.frameCounts[a] = this.metaData[0];
                } else {
                    loading = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            SlotsDrawable.lambda$setBaseDice$2(TLRPC.Document.this, account, currentMessageObject, messageCell, stickerSet);
                        }
                    });
                }
            }
            a++;
        }
        if (loading) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    SlotsDrawable.this.m3067lambda$setBaseDice$3$orgtelegramuiComponentsSlotsDrawable();
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    SlotsDrawable.this.m3068lambda$setBaseDice$4$orgtelegramuiComponentsSlotsDrawable(account, messageCell);
                }
            });
        }
    }

    /* renamed from: lambda$setBaseDice$1$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3066lambda$setBaseDice$1$orgtelegramuiComponentsSlotsDrawable() {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    public static /* synthetic */ void lambda$setBaseDice$2(TLRPC.Document document, int account, MessageObject currentMessageObject, ChatMessageCell messageCell, TLRPC.TL_messages_stickerSet stickerSet) {
        String fileName = FileLoader.getAttachFileName(document);
        DownloadController.getInstance(account).addLoadingFileObserver(fileName, currentMessageObject, messageCell);
        FileLoader.getInstance(account).loadFile(document, stickerSet, 1, 1);
    }

    /* renamed from: lambda$setBaseDice$3$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3067lambda$setBaseDice$3$orgtelegramuiComponentsSlotsDrawable() {
        this.loadingInBackground = false;
    }

    /* renamed from: lambda$setBaseDice$4$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3068lambda$setBaseDice$4$orgtelegramuiComponentsSlotsDrawable(int account, ChatMessageCell messageCell) {
        this.loadingInBackground = false;
        if (this.secondLoadingInBackground || !this.destroyAfterLoading) {
            this.nativePtr = this.nativePtrs[0];
            DownloadController.getInstance(account).removeLoadingFileObserver(messageCell);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    public boolean setDiceNumber(final ChatMessageCell messageCell, int number, final TLRPC.TL_messages_stickerSet stickerSet, final boolean instant) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            init(number);
            final MessageObject currentMessageObject = messageCell.getMessageObject();
            final int account = messageCell.getMessageObject().currentAccount;
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.SlotsDrawable$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    SlotsDrawable.this.m3070lambda$setDiceNumber$10$orgtelegramuiComponentsSlotsDrawable(stickerSet, account, currentMessageObject, messageCell, instant);
                }
            });
            return true;
        }
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:63:0x00d1  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00e5  */
    /* renamed from: lambda$setDiceNumber$10$org-telegram-ui-Components-SlotsDrawable */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m3070lambda$setDiceNumber$10$orgtelegramuiComponentsSlotsDrawable(final org.telegram.tgnet.TLRPC.TL_messages_stickerSet r17, final int r18, final org.telegram.messenger.MessageObject r19, final org.telegram.ui.Cells.ChatMessageCell r20, final boolean r21) {
        /*
            Method dump skipped, instructions count: 312
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SlotsDrawable.m3070lambda$setDiceNumber$10$orgtelegramuiComponentsSlotsDrawable(org.telegram.tgnet.TLRPC$TL_messages_stickerSet, int, org.telegram.messenger.MessageObject, org.telegram.ui.Cells.ChatMessageCell, boolean):void");
    }

    /* renamed from: lambda$setDiceNumber$6$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3071lambda$setDiceNumber$6$orgtelegramuiComponentsSlotsDrawable() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    public static /* synthetic */ void lambda$setDiceNumber$7(TLRPC.Document document, int account, MessageObject currentMessageObject, ChatMessageCell messageCell, TLRPC.TL_messages_stickerSet stickerSet) {
        String fileName = FileLoader.getAttachFileName(document);
        DownloadController.getInstance(account).addLoadingFileObserver(fileName, currentMessageObject, messageCell);
        FileLoader.getInstance(account).loadFile(document, stickerSet, 1, 1);
    }

    /* renamed from: lambda$setDiceNumber$8$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3072lambda$setDiceNumber$8$orgtelegramuiComponentsSlotsDrawable() {
        this.secondLoadingInBackground = false;
    }

    /* renamed from: lambda$setDiceNumber$9$org-telegram-ui-Components-SlotsDrawable */
    public /* synthetic */ void m3073lambda$setDiceNumber$9$orgtelegramuiComponentsSlotsDrawable(boolean instant, int account, ChatMessageCell messageCell) {
        if (instant && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
            this.isDice = 2;
            this.setLastFrame = true;
        }
        this.secondLoadingInBackground = false;
        if (this.loadingInBackground || !this.destroyAfterLoading) {
            this.secondNativePtr = this.secondNativePtrs[0];
            DownloadController.getInstance(account).removeLoadingFileObserver(messageCell);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / this.metaData[1]));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    @Override // org.telegram.ui.Components.RLottieDrawable
    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
        } else if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            int a = 0;
            while (true) {
                long[] jArr = this.nativePtrs;
                if (a >= jArr.length) {
                    break;
                }
                if (jArr[a] != 0) {
                    if (jArr[a] == this.nativePtr) {
                        this.nativePtr = 0L;
                    }
                    destroy(this.nativePtrs[a]);
                    this.nativePtrs[a] = 0;
                }
                a++;
            }
            int a2 = 0;
            while (true) {
                long[] jArr2 = this.secondNativePtrs;
                if (a2 < jArr2.length) {
                    if (jArr2[a2] != 0) {
                        if (jArr2[a2] == this.secondNativePtr) {
                            this.secondNativePtr = 0L;
                        }
                        destroy(this.secondNativePtrs[a2]);
                        this.secondNativePtrs[a2] = 0;
                    }
                    a2++;
                } else {
                    recycleResources();
                    return;
                }
            }
        } else {
            this.destroyWhenDone = true;
        }
    }

    @Override // org.telegram.ui.Components.RLottieDrawable
    protected void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
                int a = 0;
                while (true) {
                    long[] jArr = this.nativePtrs;
                    if (a >= jArr.length) {
                        break;
                    }
                    if (jArr[a] != 0) {
                        destroy(jArr[a]);
                        this.nativePtrs[a] = 0;
                    }
                    a++;
                }
                int a2 = 0;
                while (true) {
                    long[] jArr2 = this.secondNativePtrs;
                    if (a2 >= jArr2.length) {
                        break;
                    }
                    if (jArr2[a2] != 0) {
                        destroy(jArr2[a2]);
                        this.secondNativePtrs[a2] = 0;
                    }
                    a2++;
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
}
