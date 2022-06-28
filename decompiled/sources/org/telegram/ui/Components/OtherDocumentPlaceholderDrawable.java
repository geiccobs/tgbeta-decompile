package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public class OtherDocumentPlaceholderDrawable extends RecyclableDrawable implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private String ext;
    private String fileName;
    private String fileSize;
    private boolean loaded;
    private boolean loading;
    private MessageObject parentMessageObject;
    private View parentView;
    private String progress;
    private boolean progressVisible;
    private Drawable thumbDrawable;
    private static Paint paint = new Paint();
    private static Paint progressPaint = new Paint(1);
    private static TextPaint docPaint = new TextPaint(1);
    private static TextPaint namePaint = new TextPaint(1);
    private static TextPaint sizePaint = new TextPaint(1);
    private static TextPaint buttonPaint = new TextPaint(1);
    private static TextPaint percentPaint = new TextPaint(1);
    private static TextPaint openPaint = new TextPaint(1);
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private long lastUpdateTime = 0;
    private float currentProgress = 0.0f;
    private float animationProgressStart = 0.0f;
    private long currentProgressTime = 0;
    private float animatedProgressValue = 0.0f;
    private float animatedAlphaValue = 1.0f;

    static {
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(-14209998);
        docPaint.setColor(-1);
        namePaint.setColor(-1);
        sizePaint.setColor(-10327179);
        buttonPaint.setColor(-10327179);
        percentPaint.setColor(-1);
        openPaint.setColor(-1);
        docPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        namePaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        buttonPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        percentPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        openPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
    }

    public OtherDocumentPlaceholderDrawable(Context context, View view, MessageObject messageObject) {
        docPaint.setTextSize(AndroidUtilities.dp(14.0f));
        namePaint.setTextSize(AndroidUtilities.dp(19.0f));
        sizePaint.setTextSize(AndroidUtilities.dp(15.0f));
        buttonPaint.setTextSize(AndroidUtilities.dp(15.0f));
        percentPaint.setTextSize(AndroidUtilities.dp(15.0f));
        openPaint.setTextSize(AndroidUtilities.dp(15.0f));
        progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.parentView = view;
        this.parentMessageObject = messageObject;
        this.TAG = DownloadController.getInstance(messageObject.currentAccount).generateObserverTag();
        TLRPC.Document document = messageObject.getDocument();
        if (document != null) {
            String documentFileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            this.fileName = documentFileName;
            if (TextUtils.isEmpty(documentFileName)) {
                this.fileName = CommonProperties.NAME;
            }
            int idx = this.fileName.lastIndexOf(46);
            String upperCase = idx == -1 ? "" : this.fileName.substring(idx + 1).toUpperCase();
            this.ext = upperCase;
            int w = (int) Math.ceil(docPaint.measureText(upperCase));
            if (w > AndroidUtilities.dp(40.0f)) {
                this.ext = TextUtils.ellipsize(this.ext, docPaint, AndroidUtilities.dp(40.0f), TextUtils.TruncateAt.END).toString();
            }
            this.thumbDrawable = context.getResources().getDrawable(AndroidUtilities.getThumbForNameOrMime(this.fileName, messageObject.getDocument().mime_type, true)).mutate();
            this.fileSize = AndroidUtilities.formatFileSize(document.size);
            int w2 = (int) Math.ceil(namePaint.measureText(this.fileName));
            if (w2 > AndroidUtilities.dp(320.0f)) {
                this.fileName = TextUtils.ellipsize(this.fileName, namePaint, AndroidUtilities.dp(320.0f), TextUtils.TruncateAt.END).toString();
            }
        }
        checkFileExist();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        Drawable drawable = this.thumbDrawable;
        if (drawable != null) {
            drawable.setAlpha(alpha);
        }
        paint.setAlpha(alpha);
        docPaint.setAlpha(alpha);
        namePaint.setAlpha(alpha);
        sizePaint.setAlpha(alpha);
        buttonPaint.setAlpha(alpha);
        percentPaint.setAlpha(alpha);
        openPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int offsetY;
        TextPaint paint2;
        String button;
        String str;
        int w;
        String button2;
        android.graphics.Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.drawRect(0.0f, 0.0f, width, height, paint);
        int y = (height - AndroidUtilities.dp(240.0f)) / 2;
        int x = (width - AndroidUtilities.dp(48.0f)) / 2;
        this.thumbDrawable.setBounds(x, y, AndroidUtilities.dp(48.0f) + x, AndroidUtilities.dp(48.0f) + y);
        this.thumbDrawable.draw(canvas);
        int w2 = (int) Math.ceil(docPaint.measureText(this.ext));
        canvas.drawText(this.ext, (width - w2) / 2, AndroidUtilities.dp(31.0f) + y, docPaint);
        int w3 = (int) Math.ceil(namePaint.measureText(this.fileName));
        canvas.drawText(this.fileName, (width - w3) / 2, AndroidUtilities.dp(96.0f) + y, namePaint);
        int w4 = (int) Math.ceil(sizePaint.measureText(this.fileSize));
        canvas.drawText(this.fileSize, (width - w4) / 2, AndroidUtilities.dp(125.0f) + y, sizePaint);
        if (this.loaded) {
            button = LocaleController.getString("OpenFile", R.string.OpenFile);
            paint2 = openPaint;
            offsetY = 0;
        } else {
            if (this.loading) {
                button2 = LocaleController.getString("Cancel", R.string.Cancel).toUpperCase();
            } else {
                button2 = LocaleController.getString("TapToDownload", R.string.TapToDownload);
            }
            int offsetY2 = AndroidUtilities.dp(28.0f);
            button = button2;
            paint2 = buttonPaint;
            offsetY = offsetY2;
        }
        int w5 = (int) Math.ceil(paint2.measureText(button));
        canvas.drawText(button, (width - w5) / 2, AndroidUtilities.dp(235.0f) + y + offsetY, paint2);
        if (this.progressVisible) {
            if (this.progress == null) {
                w = w5;
            } else {
                int w6 = (int) Math.ceil(percentPaint.measureText(str));
                canvas.drawText(this.progress, (width - w6) / 2, AndroidUtilities.dp(210.0f) + y, percentPaint);
                w = w6;
            }
            int w7 = AndroidUtilities.dp(240.0f);
            int x2 = (width - w7) / 2;
            int y2 = y + AndroidUtilities.dp(232.0f);
            progressPaint.setColor(-10327179);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            int start = (int) (AndroidUtilities.dp(240.0f) * this.animatedProgressValue);
            canvas.drawRect(x2 + start, y2, AndroidUtilities.dp(240.0f) + x2, y2 + AndroidUtilities.dp(2.0f), progressPaint);
            progressPaint.setColor(-1);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            canvas.drawRect(x2, y2, (AndroidUtilities.dp(240.0f) * this.animatedProgressValue) + x2, y2 + AndroidUtilities.dp(2.0f), progressPaint);
            updateAnimation();
        }
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.parentView.getMeasuredWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.parentView.getMeasuredHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return this.parentView.getMeasuredWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return this.parentView.getMeasuredHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -1;
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String name, boolean canceled) {
        checkFileExist();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String name) {
        setProgress(1.0f, true);
        checkFileExist();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        if (!this.progressVisible) {
            checkFileExist();
        }
        setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    @Override // org.telegram.ui.Components.RecyclableDrawable
    public void recycle() {
        DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        this.parentView = null;
        this.parentMessageObject = null;
    }

    public void checkFileExist() {
        MessageObject messageObject = this.parentMessageObject;
        if (messageObject != null && messageObject.messageOwner.media != null) {
            String fileName = null;
            if (TextUtils.isEmpty(this.parentMessageObject.messageOwner.attachPath) || !new File(this.parentMessageObject.messageOwner.attachPath).exists()) {
                File cacheFile = FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(this.parentMessageObject.messageOwner);
                if (!cacheFile.exists()) {
                    fileName = FileLoader.getAttachFileName(this.parentMessageObject.getDocument());
                }
            }
            this.loaded = false;
            if (fileName == null) {
                this.progressVisible = false;
                this.loading = false;
                this.loaded = true;
                DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
            } else {
                DownloadController.getInstance(this.parentMessageObject.currentAccount).addLoadingFileObserver(fileName, this);
                boolean isLoadingFile = FileLoader.getInstance(this.parentMessageObject.currentAccount).isLoadingFile(fileName);
                this.loading = isLoadingFile;
                if (isLoadingFile) {
                    this.progressVisible = true;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress == null) {
                        progress = Float.valueOf(0.0f);
                    }
                    setProgress(progress.floatValue(), false);
                } else {
                    this.progressVisible = false;
                }
            }
        } else {
            this.loading = false;
            this.loaded = true;
            this.progressVisible = false;
            setProgress(0.0f, false);
            DownloadController.getInstance(this.parentMessageObject.currentAccount).removeLoadingFileObserver(this);
        }
        this.parentView.invalidate();
    }

    private void updateAnimation() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        float f = this.animatedProgressValue;
        if (f != 1.0f) {
            float f2 = this.currentProgress;
            if (f != f2) {
                float f3 = this.animationProgressStart;
                float progressDiff = f2 - f3;
                if (progressDiff > 0.0f) {
                    long j = this.currentProgressTime + dt;
                    this.currentProgressTime = j;
                    if (j >= 300) {
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0L;
                    } else {
                        this.animatedProgressValue = f3 + (decelerateInterpolator.getInterpolation(((float) j) / 300.0f) * progressDiff);
                    }
                }
                this.parentView.invalidate();
            }
        }
        float f4 = this.animatedProgressValue;
        if (f4 < 1.0f || f4 != 1.0f) {
            return;
        }
        float f5 = this.animatedAlphaValue;
        if (f5 != 0.0f) {
            float f6 = f5 - (((float) dt) / 200.0f);
            this.animatedAlphaValue = f6;
            if (f6 <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
            }
            this.parentView.invalidate();
        }
    }

    public void setProgress(float value, boolean animated) {
        if (!animated) {
            this.animatedProgressValue = value;
            this.animationProgressStart = value;
        } else {
            this.animationProgressStart = this.animatedProgressValue;
        }
        this.progress = String.format("%d%%", Integer.valueOf((int) (100.0f * value)));
        if (value != 1.0f) {
            this.animatedAlphaValue = 1.0f;
        }
        this.currentProgress = value;
        this.currentProgressTime = 0L;
        this.lastUpdateTime = System.currentTimeMillis();
        this.parentView.invalidate();
    }

    public float getCurrentProgress() {
        return this.currentProgress;
    }
}
