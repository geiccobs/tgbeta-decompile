package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class VideoTimelinePlayView extends View {
    public static final int MODE_AVATAR = 1;
    public static final int MODE_VIDEO = 0;
    private AsyncTask<Integer, Integer, Bitmap> currentTask;
    private VideoTimelineViewDelegate delegate;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private int frameHeight;
    private long frameTimeOffset;
    private int frameWidth;
    private int framesToLoad;
    private int lastWidth;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private Paint paint;
    private Paint paint2;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedPlay;
    private boolean pressedRight;
    private float progressLeft;
    private long videoLength;
    private static final Object sync = new Object();
    public static int TYPE_LEFT = 0;
    public static int TYPE_RIGHT = 1;
    public static int TYPE_PROGRESS = 2;
    private float progressRight = 1.0f;
    private float playProgress = 0.5f;
    private ArrayList<BitmapFrame> frames = new ArrayList<>();
    private float maxProgressDiff = 1.0f;
    private float minProgressDiff = 0.0f;
    private RectF rect3 = new RectF();
    private int currentMode = 0;
    Paint bitmapPaint = new Paint();
    private ArrayList<android.graphics.Rect> exclusionRects = new ArrayList<>();
    private android.graphics.Rect exclustionRect = new android.graphics.Rect();

    /* loaded from: classes5.dex */
    public interface VideoTimelineViewDelegate {
        void didStartDragging(int i);

        void didStopDragging(int i);

        void onLeftProgressChanged(float f);

        void onPlayProgressChanged(float f);

        void onRightProgressChanged(float f);
    }

    public VideoTimelinePlayView(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.paint = paint;
        paint.setColor(-1);
        Paint paint2 = new Paint();
        this.paint2 = paint2;
        paint2.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        Drawable drawable = context.getResources().getDrawable(R.drawable.video_cropleft);
        this.drawableLeft = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        Drawable drawable2 = context.getResources().getDrawable(R.drawable.video_cropright);
        this.drawableRight = drawable2;
        drawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.exclusionRects.add(this.exclustionRect);
    }

    public float getProgress() {
        return this.playProgress;
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

    public void setMode(int mode) {
        if (this.currentMode == mode) {
            return;
        }
        this.currentMode = mode;
        invalidate();
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

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (Build.VERSION.SDK_INT >= 29) {
            this.exclustionRect.set(left, 0, right, getMeasuredHeight());
            setSystemGestureExclusionRects(this.exclusionRects);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int width = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        int startX = ((int) (width * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        int playX = ((int) (width * this.playProgress)) + AndroidUtilities.dp(16.0f);
        int endX = ((int) (width * this.progressRight)) + AndroidUtilities.dp(16.0f);
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int additionWidth = AndroidUtilities.dp(16.0f);
            int additionWidthPlay = AndroidUtilities.dp(8.0f);
            if (endX != startX && playX - additionWidthPlay <= x && x <= playX + additionWidthPlay && y >= 0.0f && y <= getMeasuredHeight()) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = true;
                this.pressDx = (int) (x - playX);
                invalidate();
                return true;
            } else if (startX - additionWidth <= x && x <= Math.min(startX + additionWidth, endX) && y >= 0.0f && y <= getMeasuredHeight()) {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging(TYPE_LEFT);
                }
                this.pressedLeft = true;
                this.pressDx = (int) (x - startX);
                invalidate();
                return true;
            } else if (endX - additionWidth <= x && x <= endX + additionWidth && y >= 0.0f && y <= getMeasuredHeight()) {
                VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
                if (videoTimelineViewDelegate3 != null) {
                    videoTimelineViewDelegate3.didStartDragging(TYPE_RIGHT);
                }
                this.pressedRight = true;
                this.pressDx = (int) (x - endX);
                invalidate();
                return true;
            } else if (startX <= x && x <= endX && y >= 0.0f && y <= getMeasuredHeight()) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStartDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = true;
                float dp = (x - AndroidUtilities.dp(16.0f)) / width;
                this.playProgress = dp;
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.onPlayProgressChanged(dp);
                }
                this.pressDx = 0.0f;
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (this.pressedLeft) {
                VideoTimelineViewDelegate videoTimelineViewDelegate6 = this.delegate;
                if (videoTimelineViewDelegate6 != null) {
                    videoTimelineViewDelegate6.didStopDragging(TYPE_LEFT);
                }
                this.pressedLeft = false;
                return true;
            } else if (this.pressedRight) {
                VideoTimelineViewDelegate videoTimelineViewDelegate7 = this.delegate;
                if (videoTimelineViewDelegate7 != null) {
                    videoTimelineViewDelegate7.didStopDragging(TYPE_RIGHT);
                }
                this.pressedRight = false;
                return true;
            } else if (this.pressedPlay) {
                VideoTimelineViewDelegate videoTimelineViewDelegate8 = this.delegate;
                if (videoTimelineViewDelegate8 != null) {
                    videoTimelineViewDelegate8.didStopDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = false;
                return true;
            }
        } else if (event.getAction() == 2) {
            if (this.pressedPlay) {
                float dp2 = (((int) (x - this.pressDx)) - AndroidUtilities.dp(16.0f)) / width;
                this.playProgress = dp2;
                float f = this.progressLeft;
                if (dp2 < f) {
                    this.playProgress = f;
                } else {
                    float f2 = this.progressRight;
                    if (dp2 > f2) {
                        this.playProgress = f2;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate9 = this.delegate;
                if (videoTimelineViewDelegate9 != null) {
                    videoTimelineViewDelegate9.onPlayProgressChanged(this.playProgress);
                }
                invalidate();
                return true;
            } else if (this.pressedLeft) {
                int startX2 = (int) (x - this.pressDx);
                if (startX2 < AndroidUtilities.dp(16.0f)) {
                    startX2 = AndroidUtilities.dp(16.0f);
                } else if (startX2 > endX) {
                    startX2 = endX;
                }
                float dp3 = (startX2 - AndroidUtilities.dp(16.0f)) / width;
                this.progressLeft = dp3;
                float f3 = this.progressRight;
                float f4 = this.maxProgressDiff;
                if (f3 - dp3 > f4) {
                    this.progressRight = dp3 + f4;
                } else {
                    float f5 = this.minProgressDiff;
                    if (f5 != 0.0f && f3 - dp3 < f5) {
                        float f6 = f3 - f5;
                        this.progressLeft = f6;
                        if (f6 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                float f7 = this.progressLeft;
                float f8 = this.playProgress;
                if (f7 > f8) {
                    this.playProgress = f7;
                } else {
                    float f9 = this.progressRight;
                    if (f9 < f8) {
                        this.playProgress = f9;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate10 = this.delegate;
                if (videoTimelineViewDelegate10 != null) {
                    videoTimelineViewDelegate10.onLeftProgressChanged(f7);
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
                float dp4 = (endX2 - AndroidUtilities.dp(16.0f)) / width;
                this.progressRight = dp4;
                float f10 = this.progressLeft;
                float f11 = this.maxProgressDiff;
                if (dp4 - f10 > f11) {
                    this.progressLeft = dp4 - f11;
                } else {
                    float f12 = this.minProgressDiff;
                    if (f12 != 0.0f && dp4 - f10 < f12) {
                        float f13 = f10 + f12;
                        this.progressRight = f13;
                        if (f13 > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                float f14 = this.progressLeft;
                float f15 = this.playProgress;
                if (f14 > f15) {
                    this.playProgress = f14;
                } else {
                    float f16 = this.progressRight;
                    if (f16 < f15) {
                        this.playProgress = f16;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate11 = this.delegate;
                if (videoTimelineViewDelegate11 != null) {
                    videoTimelineViewDelegate11.onRightProgressChanged(this.progressRight);
                }
                invalidate();
                return true;
            }
        }
        return true;
    }

    public void setVideoPath(String path, float left, float right) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever;
        this.progressLeft = left;
        this.progressRight = right;
        try {
            mediaMetadataRetriever.setDataSource(path);
            String duration = this.mediaMetadataRetriever.extractMetadata(9);
            this.videoLength = Long.parseLong(duration);
        } catch (Exception e) {
            FileLog.e(e);
        }
        invalidate();
    }

    public void setRightProgress(float value) {
        this.progressRight = value;
        VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
        if (videoTimelineViewDelegate != null) {
            videoTimelineViewDelegate.didStartDragging(TYPE_RIGHT);
        }
        VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
        if (videoTimelineViewDelegate2 != null) {
            videoTimelineViewDelegate2.onRightProgressChanged(this.progressRight);
        }
        VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
        if (videoTimelineViewDelegate3 != null) {
            videoTimelineViewDelegate3.didStopDragging(TYPE_RIGHT);
        }
        invalidate();
    }

    public void setDelegate(VideoTimelineViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void reloadFrames(int frameNum) {
        if (this.mediaMetadataRetriever == null) {
            return;
        }
        if (frameNum == 0) {
            this.frameHeight = AndroidUtilities.dp(40.0f);
            this.framesToLoad = Math.max(1, (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight);
            this.frameWidth = (int) Math.ceil((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.framesToLoad);
            this.frameTimeOffset = this.videoLength / this.framesToLoad;
        }
        AsyncTask<Integer, Integer, Bitmap> asyncTask = new AsyncTask<Integer, Integer, Bitmap>() { // from class: org.telegram.ui.Components.VideoTimelinePlayView.1
            private int frameNum = 0;

            public Bitmap doInBackground(Integer... objects) {
                this.frameNum = objects[0].intValue();
                Bitmap bitmap = null;
                if (isCancelled()) {
                    return null;
                }
                try {
                    bitmap = VideoTimelinePlayView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelinePlayView.this.frameTimeOffset * this.frameNum * 1000, 2);
                    if (isCancelled()) {
                        return null;
                    }
                    if (bitmap != null) {
                        Bitmap result = Bitmap.createBitmap(VideoTimelinePlayView.this.frameWidth, VideoTimelinePlayView.this.frameHeight, bitmap.getConfig());
                        Canvas canvas = new Canvas(result);
                        float scaleX = VideoTimelinePlayView.this.frameWidth / bitmap.getWidth();
                        float scaleY = VideoTimelinePlayView.this.frameHeight / bitmap.getHeight();
                        float scale = Math.max(scaleX, scaleY);
                        int w = (int) (bitmap.getWidth() * scale);
                        int h = (int) (bitmap.getHeight() * scale);
                        android.graphics.Rect srcRect = new android.graphics.Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        android.graphics.Rect destRect = new android.graphics.Rect((VideoTimelinePlayView.this.frameWidth - w) / 2, (VideoTimelinePlayView.this.frameHeight - h) / 2, w, h);
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
                    VideoTimelinePlayView.this.frames.add(new BitmapFrame(bitmap));
                    VideoTimelinePlayView.this.invalidate();
                    if (this.frameNum < VideoTimelinePlayView.this.framesToLoad) {
                        VideoTimelinePlayView.this.reloadFrames(this.frameNum + 1);
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
        for (int a = 0; a < this.frames.size(); a++) {
            BitmapFrame bitmap = this.frames.get(a);
            if (bitmap != null && bitmap.bitmap != null) {
                bitmap.bitmap.recycle();
            }
        }
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
    }

    public boolean isDragging() {
        return this.pressedPlay;
    }

    public void setProgress(float value) {
        this.playProgress = value;
        invalidate();
    }

    public void clearFrames() {
        for (int a = 0; a < this.frames.size(); a++) {
            BitmapFrame frame = this.frames.get(a);
            if (frame != null) {
                frame.bitmap.recycle();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.lastWidth != widthSize) {
            clearFrames();
            this.lastWidth = widthSize;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        float f = 16.0f;
        int startX = ((int) (width * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        int endX = ((int) (width * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        canvas.clipRect(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(20.0f) + width, AndroidUtilities.dp(48.0f));
        float f2 = 1.0f;
        if (this.frames.isEmpty() && this.currentTask == null) {
            reloadFrames(0);
        } else {
            int offset = 0;
            int a = 0;
            while (a < this.frames.size()) {
                BitmapFrame bitmap = this.frames.get(a);
                if (bitmap.bitmap != null) {
                    int x = AndroidUtilities.dp(f) + (this.frameWidth * offset);
                    int y = AndroidUtilities.dp(6.0f);
                    if (bitmap.alpha != f2) {
                        bitmap.alpha += 0.16f;
                        if (bitmap.alpha > f2) {
                            bitmap.alpha = f2;
                        } else {
                            invalidate();
                        }
                        this.bitmapPaint.setAlpha((int) (bitmap.alpha * 255.0f));
                        canvas.drawBitmap(bitmap.bitmap, x, y, this.bitmapPaint);
                    } else {
                        canvas.drawBitmap(bitmap.bitmap, x, y, (Paint) null);
                    }
                }
                offset++;
                a++;
                f = 16.0f;
                f2 = 1.0f;
            }
        }
        int top = AndroidUtilities.dp(6.0f);
        int end = AndroidUtilities.dp(48.0f);
        canvas.drawRect(AndroidUtilities.dp(16.0f), top, startX, AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect(AndroidUtilities.dp(4.0f) + endX, top, AndroidUtilities.dp(16.0f) + width + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect(startX, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f) + startX, end, this.paint);
        canvas.drawRect(AndroidUtilities.dp(2.0f) + endX, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + endX, end, this.paint);
        canvas.drawRect(AndroidUtilities.dp(2.0f) + startX, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + endX, top, this.paint);
        canvas.drawRect(AndroidUtilities.dp(2.0f) + startX, end - AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f) + endX, end, this.paint);
        canvas.restore();
        this.rect3.set(startX - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f) + startX, end);
        canvas.drawRoundRect(this.rect3, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint);
        this.drawableLeft.setBounds(startX - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), AndroidUtilities.dp(2.0f) + startX, ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableLeft.draw(canvas);
        this.rect3.set(AndroidUtilities.dp(2.0f) + endX, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f) + endX, end);
        canvas.drawRoundRect(this.rect3, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint);
        this.drawableRight.setBounds(AndroidUtilities.dp(2.0f) + endX, AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), AndroidUtilities.dp(12.0f) + endX, ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableRight.draw(canvas);
        float cx = AndroidUtilities.dp(18.0f) + (width * this.playProgress);
        this.rect3.set(cx - AndroidUtilities.dp(1.5f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.5f) + cx, AndroidUtilities.dp(50.0f));
        canvas.drawRoundRect(this.rect3, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), this.paint2);
        canvas.drawCircle(cx, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(3.5f), this.paint2);
        this.rect3.set(cx - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f) + cx, AndroidUtilities.dp(50.0f));
        canvas.drawRoundRect(this.rect3, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), this.paint);
        canvas.drawCircle(cx, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(3.0f), this.paint);
    }

    /* loaded from: classes5.dex */
    public static class BitmapFrame {
        float alpha;
        Bitmap bitmap;

        public BitmapFrame(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }
}
