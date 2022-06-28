package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class VideoTimelineView extends View {
    private static final Object sync = new Object();
    private AsyncTask<Integer, Integer, Bitmap> currentTask;
    private VideoTimelineViewDelegate delegate;
    private int frameHeight;
    private long frameTimeOffset;
    private int frameWidth;
    private boolean framesLoaded;
    private int framesToLoad;
    private boolean isRoundFrames;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private Paint paint;
    private Paint paint2;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedRight;
    private float progressLeft;
    private android.graphics.Rect rect1;
    private android.graphics.Rect rect2;
    private Bitmap roundCornerBitmap;
    private int roundCornersSize;
    private TimeHintView timeHintView;
    private long videoLength;
    private float progressRight = 1.0f;
    private ArrayList<Bitmap> frames = new ArrayList<>();
    private float maxProgressDiff = 1.0f;
    private float minProgressDiff = 0.0f;
    private ArrayList<Bitmap> keyframes = new ArrayList<>();
    Paint thumbPaint = new Paint(1);
    Paint thumbRipplePaint = new Paint(1);
    private Paint backgroundGrayPaint = new Paint();

    /* loaded from: classes5.dex */
    public interface VideoTimelineViewDelegate {
        void didStartDragging();

        void didStopDragging();

        void onLeftProgressChanged(float f);

        void onRightProgressChanged(float f);
    }

    public void setKeyframes(ArrayList<Bitmap> keyframes) {
        this.keyframes.clear();
        this.keyframes.addAll(keyframes);
    }

    public VideoTimelineView(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.paint = paint;
        paint.setColor(-1);
        Paint paint2 = new Paint();
        this.paint2 = paint2;
        paint2.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.thumbPaint.setColor(-1);
        this.thumbPaint.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
        this.thumbPaint.setStyle(Paint.Style.STROKE);
        this.thumbPaint.setStrokeCap(Paint.Cap.ROUND);
        updateColors();
    }

    public void updateColors() {
        this.backgroundGrayPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.thumbRipplePaint.setColor(Theme.getColor(Theme.key_chat_recordedVoiceHighlight));
        this.roundCornersSize = 0;
        TimeHintView timeHintView = this.timeHintView;
        if (timeHintView != null) {
            timeHintView.updateColors();
        }
    }

    public float getLeftProgress() {
        return this.progressLeft;
    }

    public float getRightProgress() {
        return this.progressRight;
    }

    public void setMinProgressDiff(float value) {
        this.minProgressDiff = value;
    }

    public void setMaxProgressDiff(float value) {
        this.maxProgressDiff = value;
        float f = this.progressRight;
        float f2 = this.progressLeft;
        if (f - f2 > value) {
            this.progressRight = f2 + value;
            invalidate();
        }
    }

    public void setRoundFrames(boolean value) {
        this.isRoundFrames = value;
        if (value) {
            this.rect1 = new android.graphics.Rect(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.rect2 = new android.graphics.Rect();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int width = getMeasuredWidth() - AndroidUtilities.dp(24.0f);
        int startX = ((int) (width * this.progressLeft)) + AndroidUtilities.dp(12.0f);
        int endX = ((int) (width * this.progressRight)) + AndroidUtilities.dp(12.0f);
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int additionWidth = AndroidUtilities.dp(24.0f);
            if (startX - additionWidth <= x && x <= startX + additionWidth && y >= 0.0f && y <= getMeasuredHeight()) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (int) (x - startX);
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressLeft));
                this.timeHintView.setCx(getLeft() + startX + AndroidUtilities.dp(4.0f));
                this.timeHintView.show(true);
                invalidate();
                return true;
            } else if (endX - additionWidth <= x && x <= endX + additionWidth && y >= 0.0f && y <= getMeasuredHeight()) {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (int) (x - endX);
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressRight));
                this.timeHintView.setCx((getLeft() + endX) - AndroidUtilities.dp(4.0f));
                this.timeHintView.show(true);
                invalidate();
                return true;
            } else {
                this.timeHintView.show(false);
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (this.pressedLeft) {
                VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
                if (videoTimelineViewDelegate3 != null) {
                    videoTimelineViewDelegate3.didStopDragging();
                }
                this.pressedLeft = false;
                invalidate();
                this.timeHintView.show(false);
                return true;
            } else if (this.pressedRight) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStopDragging();
                }
                this.pressedRight = false;
                invalidate();
                this.timeHintView.show(false);
                return true;
            }
        } else if (event.getAction() == 2) {
            if (this.pressedLeft) {
                int startX2 = (int) (x - this.pressDx);
                if (startX2 < AndroidUtilities.dp(16.0f)) {
                    startX2 = AndroidUtilities.dp(16.0f);
                } else if (startX2 > endX) {
                    startX2 = endX;
                }
                float dp = (startX2 - AndroidUtilities.dp(16.0f)) / width;
                this.progressLeft = dp;
                float f = this.progressRight;
                float f2 = this.maxProgressDiff;
                if (f - dp > f2) {
                    this.progressRight = dp + f2;
                } else {
                    float f3 = this.minProgressDiff;
                    if (f3 != 0.0f && f - dp < f3) {
                        float f4 = f - f3;
                        this.progressLeft = f4;
                        if (f4 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                this.timeHintView.setCx((((width * this.progressLeft) + AndroidUtilities.dpf2(12.0f)) + getLeft()) - AndroidUtilities.dp(4.0f));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressLeft));
                this.timeHintView.show(true);
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                int endX2 = (int) (x - this.pressDx);
                if (endX2 < startX) {
                    endX2 = startX;
                } else if (endX2 > AndroidUtilities.dp(16.0f) + width) {
                    endX2 = width + AndroidUtilities.dp(16.0f);
                }
                float dp2 = (endX2 - AndroidUtilities.dp(16.0f)) / width;
                this.progressRight = dp2;
                float f5 = this.progressLeft;
                float f6 = this.maxProgressDiff;
                if (dp2 - f5 > f6) {
                    this.progressLeft = dp2 - f6;
                } else {
                    float f7 = this.minProgressDiff;
                    if (f7 != 0.0f && dp2 - f5 < f7) {
                        float f8 = f5 + f7;
                        this.progressRight = f8;
                        if (f8 > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                this.timeHintView.setCx((width * this.progressRight) + AndroidUtilities.dpf2(12.0f) + getLeft() + AndroidUtilities.dp(4.0f));
                this.timeHintView.show(true);
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressRight));
                VideoTimelineViewDelegate videoTimelineViewDelegate6 = this.delegate;
                if (videoTimelineViewDelegate6 != null) {
                    videoTimelineViewDelegate6.onRightProgressChanged(this.progressRight);
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
        invalidate();
    }

    public void setVideoPath(String path) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever;
        this.progressLeft = 0.0f;
        this.progressRight = 1.0f;
        try {
            mediaMetadataRetriever.setDataSource(path);
            String duration = this.mediaMetadataRetriever.extractMetadata(9);
            this.videoLength = Long.parseLong(duration);
        } catch (Exception e) {
            FileLog.e(e);
        }
        invalidate();
    }

    public void setDelegate(VideoTimelineViewDelegate videoTimelineViewDelegate) {
        this.delegate = videoTimelineViewDelegate;
    }

    public void reloadFrames(int frameNum) {
        if (this.mediaMetadataRetriever == null) {
            return;
        }
        if (frameNum == 0) {
            if (this.isRoundFrames) {
                int dp = AndroidUtilities.dp(56.0f);
                this.frameWidth = dp;
                this.frameHeight = dp;
                this.framesToLoad = Math.max(1, (int) Math.ceil((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / (this.frameHeight / 2.0f)));
            } else {
                this.frameHeight = AndroidUtilities.dp(40.0f);
                this.framesToLoad = Math.max(1, (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight);
                this.frameWidth = (int) Math.ceil((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.framesToLoad);
            }
            this.frameTimeOffset = this.videoLength / this.framesToLoad;
            if (!this.keyframes.isEmpty()) {
                int keyFramesCount = this.keyframes.size();
                float step = keyFramesCount / this.framesToLoad;
                float currentP = 0.0f;
                for (int i = 0; i < this.framesToLoad; i++) {
                    this.frames.add(this.keyframes.get((int) currentP));
                    currentP += step;
                }
                return;
            }
        }
        this.framesLoaded = false;
        AsyncTask<Integer, Integer, Bitmap> asyncTask = new AsyncTask<Integer, Integer, Bitmap>() { // from class: org.telegram.ui.Components.VideoTimelineView.1
            private int frameNum = 0;

            public Bitmap doInBackground(Integer... objects) {
                this.frameNum = objects[0].intValue();
                Bitmap bitmap = null;
                if (isCancelled()) {
                    return null;
                }
                try {
                    bitmap = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelineView.this.frameTimeOffset * this.frameNum * 1000, 2);
                    if (isCancelled()) {
                        return null;
                    }
                    if (bitmap != null) {
                        Bitmap result = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, bitmap.getConfig());
                        Canvas canvas = new Canvas(result);
                        float scaleX = VideoTimelineView.this.frameWidth / bitmap.getWidth();
                        float scaleY = VideoTimelineView.this.frameHeight / bitmap.getHeight();
                        float scale = Math.max(scaleX, scaleY);
                        int w = (int) (bitmap.getWidth() * scale);
                        int h = (int) (bitmap.getHeight() * scale);
                        android.graphics.Rect srcRect = new android.graphics.Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        android.graphics.Rect destRect = new android.graphics.Rect((VideoTimelineView.this.frameWidth - w) / 2, (VideoTimelineView.this.frameHeight - h) / 2, w, h);
                        canvas.drawBitmap(bitmap, srcRect, destRect, (Paint) null);
                        bitmap.recycle();
                        return result;
                    }
                    return bitmap;
                } catch (Exception e) {
                    FileLog.e(e);
                    return bitmap;
                }
            }

            public void onPostExecute(Bitmap bitmap) {
                if (!isCancelled()) {
                    VideoTimelineView.this.frames.add(bitmap);
                    VideoTimelineView.this.invalidate();
                    if (this.frameNum < VideoTimelineView.this.framesToLoad) {
                        VideoTimelineView.this.reloadFrames(this.frameNum + 1);
                    } else {
                        VideoTimelineView.this.framesLoaded = true;
                    }
                }
            }
        };
        this.currentTask = asyncTask;
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Integer.valueOf(frameNum), null, null);
    }

    public void destroy() {
        synchronized (sync) {
            try {
                MediaMetadataRetriever mediaMetadataRetriever = this.mediaMetadataRetriever;
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                    this.mediaMetadataRetriever = null;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (!this.keyframes.isEmpty()) {
            for (int a = 0; a < this.keyframes.size(); a++) {
                Bitmap bitmap = this.keyframes.get(a);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } else {
            for (int a2 = 0; a2 < this.frames.size(); a2++) {
                Bitmap bitmap2 = this.frames.get(a2);
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
            }
        }
        this.keyframes.clear();
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
    }

    public void clearFrames() {
        if (this.keyframes.isEmpty()) {
            for (int a = 0; a < this.frames.size(); a++) {
                Bitmap bitmap = this.frames.get(a);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth() - AndroidUtilities.dp(24.0f);
        int startX = ((int) (width * this.progressLeft)) + AndroidUtilities.dp(12.0f);
        int endX = ((int) (width * this.progressRight)) + AndroidUtilities.dp(12.0f);
        int topOffset = (getMeasuredHeight() - AndroidUtilities.dp(32.0f)) >> 1;
        if (this.frames.isEmpty() && this.currentTask == null) {
            reloadFrames(0);
        }
        if (!this.frames.isEmpty()) {
            if (!this.framesLoaded) {
                canvas.drawRect(0.0f, topOffset, getMeasuredWidth(), getMeasuredHeight() - topOffset, this.backgroundGrayPaint);
            }
            int offset = 0;
            for (int a = 0; a < this.frames.size(); a++) {
                Bitmap bitmap = this.frames.get(a);
                if (bitmap != null) {
                    boolean z = this.isRoundFrames;
                    int i = this.frameWidth;
                    if (z) {
                        i /= 2;
                    }
                    int x = i * offset;
                    if (!z) {
                        canvas.drawBitmap(bitmap, x, topOffset, (Paint) null);
                    } else {
                        this.rect2.set(x, topOffset, AndroidUtilities.dp(28.0f) + x, AndroidUtilities.dp(32.0f) + topOffset);
                        canvas.drawBitmap(bitmap, this.rect1, this.rect2, (Paint) null);
                    }
                }
                offset++;
            }
            canvas.drawRect(0.0f, topOffset, startX, getMeasuredHeight() - topOffset, this.paint2);
            canvas.drawRect(endX, topOffset, getMeasuredWidth(), getMeasuredHeight() - topOffset, this.paint2);
            canvas.drawLine(startX - AndroidUtilities.dp(4.0f), AndroidUtilities.dp(10.0f) + topOffset, startX - AndroidUtilities.dp(4.0f), (getMeasuredHeight() - AndroidUtilities.dp(10.0f)) - topOffset, this.thumbPaint);
            canvas.drawLine(AndroidUtilities.dp(4.0f) + endX, AndroidUtilities.dp(10.0f) + topOffset, AndroidUtilities.dp(4.0f) + endX, (getMeasuredHeight() - AndroidUtilities.dp(10.0f)) - topOffset, this.thumbPaint);
            drawCorners(canvas, getMeasuredHeight() - (topOffset * 2), getMeasuredWidth(), 0, topOffset);
        }
    }

    private void drawCorners(Canvas canvas, int height, int width, int left, int top) {
        if (AndroidUtilities.dp(6.0f) != this.roundCornersSize) {
            this.roundCornersSize = AndroidUtilities.dp(6.0f);
            this.roundCornerBitmap = Bitmap.createBitmap(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(this.roundCornerBitmap);
            Paint xRefP = new Paint(1);
            xRefP.setColor(0);
            xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            bitmapCanvas.drawColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
            int i = this.roundCornersSize;
            bitmapCanvas.drawCircle(i, i, i, xRefP);
        }
        int sizeHalf = this.roundCornersSize >> 1;
        canvas.save();
        canvas.drawBitmap(this.roundCornerBitmap, left, top, (Paint) null);
        canvas.rotate(-90.0f, left + sizeHalf, (top + height) - sizeHalf);
        canvas.drawBitmap(this.roundCornerBitmap, left, (top + height) - this.roundCornersSize, (Paint) null);
        canvas.restore();
        canvas.save();
        canvas.rotate(180.0f, (left + width) - sizeHalf, (top + height) - sizeHalf);
        Bitmap bitmap = this.roundCornerBitmap;
        int i2 = this.roundCornersSize;
        canvas.drawBitmap(bitmap, (left + width) - i2, (top + height) - i2, (Paint) null);
        canvas.restore();
        canvas.save();
        canvas.rotate(90.0f, (left + width) - sizeHalf, top + sizeHalf);
        canvas.drawBitmap(this.roundCornerBitmap, (left + width) - this.roundCornersSize, top, (Paint) null);
        canvas.restore();
    }

    public void setTimeHintView(TimeHintView timeHintView) {
        this.timeHintView = timeHintView;
    }

    /* loaded from: classes5.dex */
    public static class TimeHintView extends View {
        private float cx;
        private float scale;
        private boolean show;
        private boolean showTooltip;
        private long showTooltipStartTime;
        private float tooltipAlpha;
        private Drawable tooltipBackgroundArrow;
        private StaticLayout tooltipLayout;
        private TextPaint tooltipPaint;
        private long lastTime = -1;
        private Drawable tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground));

        public TimeHintView(Context context) {
            super(context);
            TextPaint textPaint = new TextPaint(1);
            this.tooltipPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, R.drawable.tooltip_arrow);
            updateColors();
            setTime(0);
        }

        public void setTime(int timeInSeconds) {
            if (timeInSeconds != this.lastTime) {
                this.lastTime = timeInSeconds;
                String s = AndroidUtilities.formatShortDuration(timeInSeconds);
                TextPaint textPaint = this.tooltipPaint;
                this.tooltipLayout = new StaticLayout(s, textPaint, (int) textPaint.measureText(s), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.tooltipLayout.getHeight() + AndroidUtilities.dp(4.0f) + this.tooltipBackgroundArrow.getIntrinsicHeight(), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.tooltipLayout == null) {
                return;
            }
            if (this.show) {
                float f = this.scale;
                if (f != 1.0f) {
                    float f2 = f + 0.12f;
                    this.scale = f2;
                    if (f2 > 1.0f) {
                        this.scale = 1.0f;
                    }
                    invalidate();
                }
            } else {
                float f3 = this.scale;
                if (f3 != 0.0f) {
                    float f4 = f3 - 0.12f;
                    this.scale = f4;
                    if (f4 < 0.0f) {
                        this.scale = 0.0f;
                    }
                    invalidate();
                }
                if (this.scale == 0.0f) {
                    return;
                }
            }
            float f5 = this.scale;
            int alpha = (int) ((f5 > 0.5f ? 1.0f : f5 / 0.5f) * 255.0f);
            canvas.save();
            float f6 = this.scale;
            canvas.scale(f6, f6, this.cx, getMeasuredHeight());
            canvas.translate(this.cx - (this.tooltipLayout.getWidth() / 2.0f), 0.0f);
            this.tooltipBackground.setBounds(-AndroidUtilities.dp(8.0f), 0, this.tooltipLayout.getWidth() + AndroidUtilities.dp(8.0f), (int) (this.tooltipLayout.getHeight() + AndroidUtilities.dpf2(4.0f)));
            this.tooltipBackgroundArrow.setBounds((this.tooltipLayout.getWidth() / 2) - (this.tooltipBackgroundArrow.getIntrinsicWidth() / 2), (int) (this.tooltipLayout.getHeight() + AndroidUtilities.dpf2(4.0f)), (this.tooltipLayout.getWidth() / 2) + (this.tooltipBackgroundArrow.getIntrinsicWidth() / 2), ((int) (this.tooltipLayout.getHeight() + AndroidUtilities.dpf2(4.0f))) + this.tooltipBackgroundArrow.getIntrinsicHeight());
            this.tooltipBackgroundArrow.setAlpha(alpha);
            this.tooltipBackground.setAlpha(alpha);
            this.tooltipPaint.setAlpha(alpha);
            this.tooltipBackgroundArrow.draw(canvas);
            this.tooltipBackground.draw(canvas);
            canvas.translate(0.0f, AndroidUtilities.dpf2(1.0f));
            this.tooltipLayout.draw(canvas);
            canvas.restore();
        }

        public void updateColors() {
            this.tooltipPaint.setColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground));
            this.tooltipBackgroundArrow.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_gifSaveHintBackground), PorterDuff.Mode.MULTIPLY));
        }

        public void setCx(float v) {
            this.cx = v;
            invalidate();
        }

        public void show(boolean s) {
            this.show = s;
            invalidate();
        }
    }
}
