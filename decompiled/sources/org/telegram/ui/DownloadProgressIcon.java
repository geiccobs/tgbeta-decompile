package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes4.dex */
public class DownloadProgressIcon extends View implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount;
    int currentColor;
    float currentProgress;
    RLottieDrawable downloadCompleteDrawable;
    RLottieDrawable downloadDrawable;
    boolean hasUnviewedDownloads;
    float progress;
    float progressDt;
    boolean showCompletedIcon;
    Paint paint = new Paint(1);
    Paint paint2 = new Paint(1);
    ArrayList<ProgressObserver> currentListeners = new ArrayList<>();
    ImageReceiver downloadImageReceiver = new ImageReceiver(this);
    ImageReceiver downloadCompleteImageReceiver = new ImageReceiver(this);

    public DownloadProgressIcon(int currentAccount, Context context) {
        super(context);
        this.currentAccount = currentAccount;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.download_progress, "download_progress", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
        this.downloadDrawable = rLottieDrawable;
        rLottieDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(R.raw.download_finish, "download_finish", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
        this.downloadCompleteDrawable = rLottieDrawable2;
        rLottieDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        this.downloadImageReceiver.setImageBitmap(this.downloadDrawable);
        this.downloadCompleteImageReceiver.setImageBitmap(this.downloadCompleteDrawable);
        this.downloadImageReceiver.setAutoRepeat(1);
        this.downloadDrawable.setAutoRepeat(1);
        this.downloadDrawable.start();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
        int padding = AndroidUtilities.dp(15.0f);
        this.downloadImageReceiver.setImageCoords(padding, padding, getMeasuredWidth() - (padding * 2), getMeasuredHeight() - (padding * 2));
        this.downloadCompleteImageReceiver.setImageCoords(padding, padding, getMeasuredWidth() - (padding * 2), getMeasuredHeight() - (padding * 2));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getAlpha() == 0.0f) {
            return;
        }
        if (this.currentColor != Theme.getColor(Theme.key_actionBarDefaultIcon)) {
            this.currentColor = Theme.getColor(Theme.key_actionBarDefaultIcon);
            this.paint.setColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            this.paint2.setColor(Theme.getColor(Theme.key_actionBarDefaultIcon));
            this.downloadImageReceiver.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
            this.downloadCompleteImageReceiver.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
            this.paint2.setAlpha(100);
        }
        float f = this.currentProgress;
        float f2 = this.progress;
        if (f != f2) {
            float f3 = this.progressDt;
            float f4 = f + f3;
            this.currentProgress = f4;
            if (f3 > 0.0f && f4 > f2) {
                this.currentProgress = f2;
            } else if (f3 < 0.0f && f4 < f2) {
                this.currentProgress = f2;
            } else {
                invalidate();
            }
        }
        int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(8.0f);
        float r = AndroidUtilities.dp(1.0f);
        float startPadding = AndroidUtilities.dp(16.0f);
        float width = getMeasuredWidth() - (2.0f * startPadding);
        AndroidUtilities.rectTmp.set(startPadding, cy - r, getMeasuredWidth() - startPadding, cy + r);
        canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, this.paint2);
        AndroidUtilities.rectTmp.set(startPadding, cy - r, (this.currentProgress * width) + startPadding, cy + r);
        canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, this.paint);
        canvas.save();
        canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), cy - r);
        if (this.progress != 1.0f) {
            this.showCompletedIcon = false;
        }
        if (this.showCompletedIcon) {
            this.downloadCompleteImageReceiver.draw(canvas);
        } else {
            this.downloadImageReceiver.draw(canvas);
        }
        if (this.progress == 1.0f && !this.showCompletedIcon && this.downloadDrawable.getCurrentFrame() == 0) {
            this.downloadCompleteDrawable.setCurrentFrame(0, false);
            this.downloadCompleteDrawable.start();
            this.showCompletedIcon = true;
        }
        canvas.restore();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateDownloadingListeners();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        this.downloadImageReceiver.onAttachedToWindow();
        this.downloadCompleteImageReceiver.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detachCurrentListeners();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        this.downloadImageReceiver.onDetachedFromWindow();
        this.downloadCompleteImageReceiver.onDetachedFromWindow();
    }

    private void updateDownloadingListeners() {
        DownloadController downloadController = DownloadController.getInstance(this.currentAccount);
        HashMap<String, ProgressObserver> observerHashMap = new HashMap<>();
        for (int i = 0; i < this.currentListeners.size(); i++) {
            observerHashMap.put(this.currentListeners.get(i).fileName, this.currentListeners.get(i));
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this.currentListeners.get(i));
        }
        this.currentListeners.clear();
        for (int i2 = 0; i2 < downloadController.downloadingFiles.size(); i2++) {
            String filename = downloadController.downloadingFiles.get(i2).getFileName();
            if (FileLoader.getInstance(this.currentAccount).isLoadingFile(filename)) {
                ProgressObserver progressObserver = observerHashMap.get(filename);
                if (progressObserver == null) {
                    progressObserver = new ProgressObserver(filename);
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(filename, progressObserver);
                this.currentListeners.add(progressObserver);
            }
        }
        if (this.currentListeners.size() == 0) {
            if (getVisibility() != 0 || getAlpha() != 1.0f) {
                this.progress = 0.0f;
                this.currentProgress = 0.0f;
            }
        }
    }

    public void updateProgress() {
        MessagesStorage.getInstance(this.currentAccount);
        long total = 0;
        long downloaded = 0;
        for (int i = 0; i < this.currentListeners.size(); i++) {
            total += this.currentListeners.get(i).total;
            downloaded += this.currentListeners.get(i).downloaded;
        }
        if (total == 0) {
            this.progress = 1.0f;
        } else {
            this.progress = ((float) downloaded) / ((float) total);
        }
        float f = this.progress;
        if (f > 1.0f) {
            this.progress = 1.0f;
        } else if (f < 0.0f) {
            this.progress = 0.0f;
        }
        this.progressDt = ((this.progress - this.currentProgress) * 16.0f) / 150.0f;
        invalidate();
    }

    private void detachCurrentListeners() {
        for (int i = 0; i < this.currentListeners.size(); i++) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this.currentListeners.get(i));
        }
        this.currentListeners.clear();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.onDownloadingFilesChanged) {
            updateDownloadingListeners();
            updateProgress();
        }
    }

    /* loaded from: classes4.dex */
    public class ProgressObserver implements DownloadController.FileDownloadProgressListener {
        long downloaded;
        private final String fileName;
        long total;

        private ProgressObserver(String fileName) {
            DownloadProgressIcon.this = r1;
            this.fileName = fileName;
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onFailedDownload(String fileName, boolean canceled) {
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onSuccessDownload(String fileName) {
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.downloaded = downloadSize;
            this.total = totalSize;
            DownloadProgressIcon.this.updateProgress();
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressUpload(String fileName, long downloadSize, long totalSize, boolean isEncrypted) {
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public int getObserverTag() {
            return 0;
        }
    }
}
