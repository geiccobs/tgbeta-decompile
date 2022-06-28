package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Components.SeekBar;
/* loaded from: classes5.dex */
public class PopupAudioView extends BaseCell implements SeekBar.SeekBarDelegate, DownloadController.FileDownloadProgressListener {
    private int buttonX;
    private int buttonY;
    protected MessageObject currentMessageObject;
    private SeekBar seekBar;
    private int seekBarX;
    private int seekBarY;
    private StaticLayout timeLayout;
    private TextPaint timePaint;
    private int timeX;
    private boolean wasLayout = false;
    private int buttonState = 0;
    private int buttonPressed = 0;
    int timeWidth = 0;
    private String lastTimeString = null;
    private int currentAccount;
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private ProgressView progressView = new ProgressView();

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public PopupAudioView(Context context) {
        super(context);
        TextPaint textPaint = new TextPaint(1);
        this.timePaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        SeekBar seekBar = new SeekBar(this);
        this.seekBar = seekBar;
        seekBar.setDelegate(this);
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject) {
            this.currentAccount = messageObject.currentAccount;
            this.seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
            this.progressView.setProgressColors(-2497813, -7944712);
            this.currentMessageObject = messageObject;
            this.wasLayout = false;
            requestLayout();
        }
        updateButtonState();
    }

    public final MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, AndroidUtilities.dp(56.0f));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentMessageObject == null) {
            return;
        }
        this.seekBarX = AndroidUtilities.dp(54.0f);
        this.buttonX = AndroidUtilities.dp(10.0f);
        this.timeX = (getMeasuredWidth() - this.timeWidth) - AndroidUtilities.dp(16.0f);
        this.seekBar.setSize((getMeasuredWidth() - AndroidUtilities.dp(70.0f)) - this.timeWidth, AndroidUtilities.dp(30.0f));
        this.progressView.width = (getMeasuredWidth() - AndroidUtilities.dp(94.0f)) - this.timeWidth;
        this.progressView.height = AndroidUtilities.dp(30.0f);
        this.seekBarY = AndroidUtilities.dp(13.0f);
        this.buttonY = AndroidUtilities.dp(10.0f);
        updateProgress();
        if (changed || !this.wasLayout) {
            this.wasLayout = true;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject == null) {
            return;
        }
        if (!this.wasLayout) {
            requestLayout();
            return;
        }
        int h = AndroidUtilities.displaySize.y;
        int w = AndroidUtilities.displaySize.x;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();
        }
        Theme.chat_msgInMediaDrawable.setTop((int) getY(), w, h, false, false);
        setDrawableBounds((Drawable) Theme.chat_msgInMediaDrawable, 0, 0, getMeasuredWidth(), getMeasuredHeight());
        Theme.chat_msgInMediaDrawable.draw(canvas);
        if (this.currentMessageObject == null) {
            return;
        }
        canvas.save();
        int i = this.buttonState;
        if (i == 0 || i == 1) {
            canvas.translate(this.seekBarX, this.seekBarY);
            this.seekBar.draw(canvas);
        } else {
            canvas.translate(this.seekBarX + AndroidUtilities.dp(12.0f), this.seekBarY);
            this.progressView.draw(canvas);
        }
        canvas.restore();
        int state = this.buttonState + 5;
        this.timePaint.setColor(-6182221);
        Drawable buttonDrawable = Theme.chat_fileStatesDrawable[state][this.buttonPressed];
        int side = AndroidUtilities.dp(36.0f);
        int x = (side - buttonDrawable.getIntrinsicWidth()) / 2;
        int y = (side - buttonDrawable.getIntrinsicHeight()) / 2;
        setDrawableBounds(buttonDrawable, this.buttonX + x, this.buttonY + y);
        buttonDrawable.draw(canvas);
        canvas.save();
        canvas.translate(this.timeX, AndroidUtilities.dp(18.0f));
        this.timeLayout.draw(canvas);
        canvas.restore();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* JADX WARN: Code restructure failed: missing block: B:36:0x00a7, code lost:
        if (r1 <= (r3 + r4)) goto L38;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            float r0 = r8.getX()
            float r1 = r8.getY()
            org.telegram.ui.Components.SeekBar r2 = r7.seekBar
            int r3 = r8.getAction()
            float r4 = r8.getX()
            int r5 = r7.seekBarX
            float r5 = (float) r5
            float r4 = r4 - r5
            float r5 = r8.getY()
            int r6 = r7.seekBarY
            float r6 = (float) r6
            float r5 = r5 - r6
            boolean r2 = r2.onTouch(r3, r4, r5)
            r3 = 1
            if (r2 == 0) goto L37
            int r4 = r8.getAction()
            if (r4 != 0) goto L32
            android.view.ViewParent r4 = r7.getParent()
            r4.requestDisallowInterceptTouchEvent(r3)
        L32:
            r7.invalidate()
            goto Lb4
        L37:
            r4 = 1108344832(0x42100000, float:36.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r8.getAction()
            if (r5 != 0) goto L64
            int r5 = r7.buttonX
            float r6 = (float) r5
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 < 0) goto Lae
            int r5 = r5 + r4
            float r5 = (float) r5
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 > 0) goto Lae
            int r5 = r7.buttonY
            float r6 = (float) r5
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 < 0) goto Lae
            int r5 = r5 + r4
            float r5 = (float) r5
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 > 0) goto Lae
            r7.buttonPressed = r3
            r7.invalidate()
            r2 = 1
            goto Lae
        L64:
            int r5 = r7.buttonPressed
            if (r5 != r3) goto Lae
            int r5 = r8.getAction()
            r6 = 0
            if (r5 != r3) goto L7b
            r7.buttonPressed = r6
            r7.playSoundEffect(r6)
            r7.didPressedButton()
            r7.invalidate()
            goto Lae
        L7b:
            int r3 = r8.getAction()
            r5 = 3
            if (r3 != r5) goto L88
            r7.buttonPressed = r6
            r7.invalidate()
            goto Lae
        L88:
            int r3 = r8.getAction()
            r5 = 2
            if (r3 != r5) goto Lae
            int r3 = r7.buttonX
            float r5 = (float) r3
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 < 0) goto La9
            int r3 = r3 + r4
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 > 0) goto La9
            int r3 = r7.buttonY
            float r5 = (float) r3
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 < 0) goto La9
            int r3 = r3 + r4
            float r3 = (float) r3
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 <= 0) goto Lae
        La9:
            r7.buttonPressed = r6
            r7.invalidate()
        Lae:
            if (r2 != 0) goto Lb4
            boolean r2 = super.onTouchEvent(r8)
        Lb4:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PopupAudioView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void didPressedButton() {
        int i = this.buttonState;
        if (i == 0) {
            boolean result = MediaController.getInstance().playMessage(this.currentMessageObject);
            if (!this.currentMessageObject.isOut() && this.currentMessageObject.isContentUnread() && this.currentMessageObject.messageOwner.peer_id.channel_id == 0) {
                MessagesController.getInstance(this.currentAccount).markMessageContentAsRead(this.currentMessageObject);
                this.currentMessageObject.setContentIsRead();
            }
            if (result) {
                this.buttonState = 1;
                invalidate();
            }
        } else if (i == 1) {
            boolean result2 = MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject);
            if (result2) {
                this.buttonState = 0;
                invalidate();
            }
        } else if (i == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            invalidate();
        } else if (i == 3) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            invalidate();
        }
    }

    public void updateProgress() {
        if (this.currentMessageObject == null) {
            return;
        }
        if (!this.seekBar.isDragging()) {
            this.seekBar.setProgress(this.currentMessageObject.audioProgress);
        }
        int duration = 0;
        if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            int a = 0;
            while (true) {
                if (a >= this.currentMessageObject.getDocument().attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = this.currentMessageObject.getDocument().attributes.get(a);
                if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                    a++;
                } else {
                    duration = attribute.duration;
                    break;
                }
            }
        } else {
            duration = this.currentMessageObject.audioProgressSec;
        }
        String timeString = AndroidUtilities.formatLongDuration(duration);
        String str = this.lastTimeString;
        if (str == null || (str != null && !str.equals(timeString))) {
            this.timeWidth = (int) Math.ceil(this.timePaint.measureText(timeString));
            this.timeLayout = new StaticLayout(timeString, this.timePaint, this.timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        invalidate();
    }

    public void downloadAudioIfNeed() {
        if (this.buttonState == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 3;
            invalidate();
        }
    }

    public void updateButtonState() {
        String fileName = this.currentMessageObject.getFileName();
        File cacheFile = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
        if (cacheFile.exists()) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                this.buttonState = 0;
            } else {
                this.buttonState = 1;
            }
            this.progressView.setProgress(0.0f);
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                this.buttonState = 2;
                this.progressView.setProgress(0.0f);
            } else {
                this.buttonState = 3;
                Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    this.progressView.setProgress(progress.floatValue());
                } else {
                    this.progressView.setProgress(0.0f);
                }
            }
        }
        updateProgress();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String fileName) {
        updateButtonState();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        this.progressView.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)));
        if (this.buttonState != 3) {
            updateButtonState();
        }
        invalidate();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
    public void onSeekBarDrag(float progress) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = progress;
        MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
    }
}
