package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
/* loaded from: classes5.dex */
public class VideoPlayerSeekBar {
    private static Paint paint;
    private static Paint strokePaint;
    private static int thumbWidth;
    private static Path tmpPath;
    private static float[] tmpRadii;
    private float animateFromBufferedProgress;
    private boolean animateResetBuffering;
    private AnimatedFloat animateThumbLoopBackProgress;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private float currentRadius;
    private SeekBarDelegate delegate;
    private int height;
    private int horizontalPadding;
    private CharSequence lastCaption;
    private long lastTimestampUpdate;
    private long lastTimestampsAppearingUpdate;
    private long lastUpdateTime;
    private long lastVideoDuration;
    private float loopBackWasThumbX;
    private View parentView;
    private float progress;
    private int progressColor;
    private boolean selected;
    private int smallLineColor;
    private int timestampChangeDirection;
    private StaticLayout[] timestampLabel;
    private TextPaint timestampLabelPaint;
    private ArrayList<Pair<Float, CharSequence>> timestamps;
    private float transitionProgress;
    private int width;
    private int thumbX = 0;
    private float animatedThumbX = 0.0f;
    private int draggingThumbX = 0;
    private int thumbDX = 0;
    private boolean pressed = false;
    private boolean pressedDelayed = false;
    private RectF rect = new RectF();
    private float bufferedAnimationValue = 1.0f;
    private int lineHeight = AndroidUtilities.dp(4.0f);
    private int smallLineHeight = AndroidUtilities.dp(2.0f);
    private int fromThumbX = 0;
    private float animateThumbProgress = 1.0f;
    private float timestampsAppearing = 0.0f;
    private final float TIMESTAMP_GAP = 1.0f;
    private int currentTimestamp = -1;
    private int lastTimestamp = -1;
    private float timestampChangeT = 1.0f;
    private float lastWidth = -1.0f;

    /* loaded from: classes5.dex */
    public interface SeekBarDelegate {
        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);

        /* renamed from: org.telegram.ui.Components.VideoPlayerSeekBar$SeekBarDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate _this, float progress) {
            }
        }
    }

    public VideoPlayerSeekBar(View parent) {
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            strokePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(-16777216);
            strokePaint.setStrokeWidth(1.0f);
        }
        this.parentView = parent;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = AndroidUtilities.dp(6.0f);
        this.animateThumbLoopBackProgress = new AnimatedFloat(0.0f, parent, 0L, 300L, CubicBezierInterpolator.EASE_OUT_QUINT);
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public boolean onTouch(int action, float x, float y) {
        SeekBarDelegate seekBarDelegate;
        if (action == 0) {
            if (this.transitionProgress > 0.0f) {
                return false;
            }
            int i = this.height;
            int i2 = thumbWidth;
            int additionWidth = (i - i2) / 2;
            if (x >= (-additionWidth)) {
                int i3 = this.width;
                if (x <= i3 + additionWidth && y >= 0.0f && y <= i) {
                    int i4 = this.thumbX;
                    if (i4 - additionWidth > x || x > i4 + i2 + additionWidth) {
                        int i5 = ((int) x) - (i2 / 2);
                        this.thumbX = i5;
                        if (i5 < 0) {
                            this.thumbX = 0;
                        } else if (i5 > i3 - i2) {
                            this.thumbX = i2 - i3;
                        }
                        this.animatedThumbX = this.thumbX;
                    }
                    this.pressedDelayed = true;
                    this.pressed = true;
                    int i6 = this.thumbX;
                    this.draggingThumbX = i6;
                    this.thumbDX = (int) (x - i6);
                    return true;
                }
            }
        } else if (action == 1 || action == 3) {
            if (this.pressed) {
                int i7 = this.draggingThumbX;
                this.thumbX = i7;
                this.animatedThumbX = i7;
                if (action == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(i7 / (this.width - thumbWidth));
                }
                this.pressed = false;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.VideoPlayerSeekBar$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoPlayerSeekBar.this.m3201lambda$onTouch$0$orgtelegramuiComponentsVideoPlayerSeekBar();
                    }
                }, 50L);
                return true;
            }
        } else if (action == 2 && this.pressed) {
            int i8 = (int) (x - this.thumbDX);
            this.draggingThumbX = i8;
            if (i8 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i9 = this.width;
                int i10 = thumbWidth;
                if (i8 > i9 - i10) {
                    this.draggingThumbX = i9 - i10;
                }
            }
            SeekBarDelegate seekBarDelegate2 = this.delegate;
            if (seekBarDelegate2 != null) {
                seekBarDelegate2.onSeekBarContinuousDrag(this.draggingThumbX / (this.width - thumbWidth));
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$onTouch$0$org-telegram-ui-Components-VideoPlayerSeekBar */
    public /* synthetic */ void m3201lambda$onTouch$0$orgtelegramuiComponentsVideoPlayerSeekBar() {
        this.pressedDelayed = false;
    }

    public void setColors(int background, int cache, int progress, int circle, int selected, int smallLineColor) {
        this.backgroundColor = background;
        this.cacheColor = cache;
        this.circleColor = circle;
        this.progressColor = progress;
        this.backgroundSelectedColor = selected;
        this.smallLineColor = smallLineColor;
    }

    public void setProgress(float progress, boolean animated) {
        if (Math.abs(this.progress - 1.0f) < 0.04f && Math.abs(progress) < 0.04f) {
            this.animateThumbLoopBackProgress.set(1.0f, true);
            this.loopBackWasThumbX = this.thumbX;
        }
        this.progress = progress;
        int newThumb = (int) Math.ceil((this.width - thumbWidth) * progress);
        if (animated) {
            if (Math.abs(newThumb - this.thumbX) > AndroidUtilities.dp(10.0f)) {
                float progressInterpolated = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                this.fromThumbX = (int) ((this.thumbX * progressInterpolated) + (this.fromThumbX * (1.0f - progressInterpolated)));
                this.animateThumbProgress = 0.0f;
            } else if (this.animateThumbProgress == 1.0f) {
                this.animateThumbProgress = 0.0f;
                this.fromThumbX = this.thumbX;
            }
        }
        this.thumbX = newThumb;
        if (newThumb < 0) {
            this.thumbX = 0;
        } else {
            int i = this.width;
            int i2 = thumbWidth;
            if (newThumb > i - i2) {
                this.thumbX = i - i2;
            }
        }
        if (Math.abs(this.animatedThumbX - this.thumbX) > AndroidUtilities.dp(8.0f)) {
            this.animatedThumbX = this.thumbX;
        }
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setBufferedProgress(float value) {
        float f = this.bufferedProgress;
        if (value != f) {
            this.animateFromBufferedProgress = f;
            this.animateResetBuffering = value < f;
            this.bufferedProgress = value;
            this.bufferedAnimationValue = 0.0f;
        }
    }

    public float getProgress() {
        return this.thumbX / (this.width - thumbWidth);
    }

    public int getThumbX() {
        return (this.pressed ? this.draggingThumbX : this.thumbX) + (thumbWidth / 2);
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public float getTransitionProgress() {
        return this.transitionProgress;
    }

    public void setTransitionProgress(float transitionProgress) {
        if (this.transitionProgress != transitionProgress) {
            this.transitionProgress = transitionProgress;
            this.parentView.invalidate();
        }
    }

    public int getHorizontalPadding() {
        return this.horizontalPadding;
    }

    public void setHorizontalPadding(int horizontalPadding) {
        this.horizontalPadding = horizontalPadding;
    }

    public void updateTimestamps(MessageObject messageObject, long videoDuration) {
        Integer seconds;
        boolean z = false;
        if (messageObject == null || videoDuration < 0) {
            this.timestamps = null;
            this.currentTimestamp = -1;
            this.timestampsAppearing = 0.0f;
            StaticLayout[] staticLayoutArr = this.timestampLabel;
            if (staticLayoutArr != null) {
                staticLayoutArr[1] = null;
                staticLayoutArr[0] = null;
            }
            this.lastCaption = null;
            this.lastVideoDuration = -1L;
            return;
        }
        CharSequence text = messageObject.caption;
        if (text == this.lastCaption && this.lastVideoDuration == videoDuration) {
            return;
        }
        this.lastCaption = text;
        this.lastVideoDuration = videoDuration;
        if (!(text instanceof Spanned)) {
            this.timestamps = null;
            this.currentTimestamp = -1;
            this.timestampsAppearing = 0.0f;
            StaticLayout[] staticLayoutArr2 = this.timestampLabel;
            if (staticLayoutArr2 != null) {
                staticLayoutArr2[1] = null;
                staticLayoutArr2[0] = null;
                return;
            }
            return;
        }
        Spanned spanned = (Spanned) text;
        try {
            URLSpanNoUnderline[] links = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
            this.timestamps = new ArrayList<>();
            this.timestampsAppearing = 0.0f;
            if (this.timestampLabelPaint == null) {
                TextPaint textPaint = new TextPaint(1);
                this.timestampLabelPaint = textPaint;
                textPaint.setTextSize(AndroidUtilities.dp(12.0f));
                this.timestampLabelPaint.setColor(-1);
            }
            int i = 0;
            while (i < links.length) {
                URLSpanNoUnderline link = links[i];
                if (link != null && link.getURL().startsWith("video?") && (seconds = Utilities.parseInt((CharSequence) link.getURL().substring(6))) != null && seconds.intValue() >= 0) {
                    float position = ((float) (seconds.intValue() * 1000)) / ((float) videoDuration);
                    String label = link.label;
                    SpannableStringBuilder builder = new SpannableStringBuilder(label);
                    Emoji.replaceEmoji(builder, this.timestampLabelPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), z);
                    this.timestamps.add(new Pair<>(Float.valueOf(position), builder));
                }
                i++;
                z = false;
            }
            Collections.sort(this.timestamps, VideoPlayerSeekBar$$ExternalSyntheticLambda1.INSTANCE);
        } catch (Exception e) {
            FileLog.e(e);
            this.timestamps = null;
            this.currentTimestamp = -1;
            this.timestampsAppearing = 0.0f;
            StaticLayout[] staticLayoutArr3 = this.timestampLabel;
            if (staticLayoutArr3 != null) {
                staticLayoutArr3[1] = null;
                staticLayoutArr3[0] = null;
            }
        }
    }

    public static /* synthetic */ int lambda$updateTimestamps$1(Pair a, Pair b) {
        if (((Float) a.first).floatValue() > ((Float) b.first).floatValue()) {
            return 1;
        }
        if (((Float) b.first).floatValue() > ((Float) a.first).floatValue()) {
            return -1;
        }
        return 0;
    }

    public void draw(Canvas canvas, View view) {
        int i;
        int i2;
        int i3;
        int i4;
        this.rect.left = this.horizontalPadding + AndroidUtilities.lerp(thumbWidth / 2.0f, 0.0f, this.transitionProgress);
        RectF rectF = this.rect;
        int i5 = this.height;
        rectF.top = AndroidUtilities.lerp((i5 - this.lineHeight) / 2.0f, (i5 - AndroidUtilities.dp(3.0f)) - this.smallLineHeight, this.transitionProgress);
        RectF rectF2 = this.rect;
        int i6 = this.height;
        rectF2.bottom = AndroidUtilities.lerp((this.lineHeight + i6) / 2.0f, i6 - AndroidUtilities.dp(3.0f), this.transitionProgress);
        float thumbX = this.thumbX;
        float min = Math.min(this.animatedThumbX, thumbX);
        this.animatedThumbX = min;
        float lerp = AndroidUtilities.lerp(min, thumbX, 0.5f);
        this.animatedThumbX = lerp;
        if (Math.abs(thumbX - lerp) > 0.005f) {
            this.parentView.invalidate();
        }
        float thumbX2 = this.animatedThumbX;
        float currentThumbX = thumbX2;
        float f = this.animateThumbProgress;
        if (f != 1.0f) {
            float f2 = f + 0.07272727f;
            this.animateThumbProgress = f2;
            if (f2 >= 1.0f) {
                this.animateThumbProgress = 1.0f;
            } else {
                view.invalidate();
                float progressInterpolated = CubicBezierInterpolator.DEFAULT.getInterpolation(this.animateThumbProgress);
                currentThumbX = (this.fromThumbX * (1.0f - progressInterpolated)) + (thumbX2 * progressInterpolated);
            }
        }
        float loopBack = this.animateThumbLoopBackProgress.set(0.0f);
        if (this.pressed) {
            loopBack = 0.0f;
        }
        this.rect.right = this.horizontalPadding + AndroidUtilities.lerp(this.width - (thumbWidth / 2.0f), this.parentView.getWidth() - (this.horizontalPadding * 2.0f), this.transitionProgress);
        setPaintColor(this.selected ? this.backgroundSelectedColor : this.backgroundColor, 1.0f - this.transitionProgress);
        drawProgressBar(canvas, this.rect, paint);
        float f3 = this.bufferedAnimationValue;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.16f;
            this.bufferedAnimationValue = f4;
            if (f4 > 1.0f) {
                this.bufferedAnimationValue = 1.0f;
            } else {
                this.parentView.invalidate();
            }
        }
        if (this.animateResetBuffering) {
            float f5 = this.animateFromBufferedProgress;
            if (f5 > 0.0f) {
                this.rect.right = this.horizontalPadding + AndroidUtilities.lerp((thumbWidth / 2.0f) + (f5 * (this.width - i4)), this.parentView.getWidth() - (this.horizontalPadding * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, (1.0f - this.transitionProgress) * (1.0f - this.bufferedAnimationValue));
                drawProgressBar(canvas, this.rect, paint);
            }
            float f6 = this.bufferedProgress;
            if (f6 > 0.0f) {
                this.rect.right = this.horizontalPadding + AndroidUtilities.lerp((thumbWidth / 2.0f) + (f6 * (this.width - i3)), this.parentView.getWidth() - (this.horizontalPadding * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                drawProgressBar(canvas, this.rect, paint);
            }
        } else {
            float f7 = this.animateFromBufferedProgress;
            float f8 = this.bufferedAnimationValue;
            float currentBufferedProgress = (f7 * (1.0f - f8)) + (this.bufferedProgress * f8);
            if (currentBufferedProgress > 0.0f) {
                this.rect.right = this.horizontalPadding + AndroidUtilities.lerp((thumbWidth / 2.0f) + ((this.width - i2) * currentBufferedProgress), this.parentView.getWidth() - (this.horizontalPadding * 2.0f), this.transitionProgress);
                setPaintColor(this.selected ? this.backgroundSelectedColor : this.cacheColor, 1.0f - this.transitionProgress);
                drawProgressBar(canvas, this.rect, paint);
            }
        }
        int newRad = AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f);
        if (this.currentRadius != newRad) {
            long newUpdateTime = SystemClock.elapsedRealtime();
            long dt = newUpdateTime - this.lastUpdateTime;
            this.lastUpdateTime = newUpdateTime;
            if (dt > 18) {
                dt = 16;
            }
            float f9 = this.currentRadius;
            if (f9 < newRad) {
                float dp = f9 + (AndroidUtilities.dp(1.0f) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > newRad) {
                    this.currentRadius = newRad;
                }
            } else {
                float dp2 = f9 - (AndroidUtilities.dp(1.0f) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < newRad) {
                    this.currentRadius = newRad;
                }
            }
            View view2 = this.parentView;
            if (view2 != null) {
                view2.invalidate();
            }
        }
        float circleRadius = AndroidUtilities.lerp(this.currentRadius, 0.0f, this.transitionProgress);
        if (loopBack > 0.0f) {
            float wasLeft = this.rect.left;
            this.rect.right = this.horizontalPadding + AndroidUtilities.lerp((thumbWidth / 2.0f) + (this.width - i), this.parentView.getWidth() - (this.horizontalPadding * 2.0f), this.transitionProgress);
            RectF rectF3 = this.rect;
            rectF3.left = AndroidUtilities.lerp(wasLeft, rectF3.right, 1.0f - loopBack);
            if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
                strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
                drawProgressBar(canvas, this.rect, strokePaint);
            }
            setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
            drawProgressBar(canvas, this.rect, paint);
            this.rect.left = wasLeft;
            setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
            float wasRight = this.horizontalPadding + AndroidUtilities.lerp((thumbWidth / 2.0f) + this.loopBackWasThumbX, (this.parentView.getWidth() - (this.horizontalPadding * 2.0f)) * (this.loopBackWasThumbX / (this.width - thumbWidth)), this.transitionProgress);
            canvas.drawCircle(wasRight, this.rect.centerY(), circleRadius * loopBack, paint);
        }
        this.rect.right = this.horizontalPadding + AndroidUtilities.lerp((thumbWidth / 2.0f) + (this.pressed ? this.draggingThumbX : currentThumbX), (this.parentView.getWidth() - (this.horizontalPadding * 2.0f)) * getProgress(), this.transitionProgress);
        if (this.transitionProgress > 0.0f && this.rect.width() > 0.0f) {
            strokePaint.setAlpha((int) (this.transitionProgress * 255.0f * 0.2f));
            drawProgressBar(canvas, this.rect, strokePaint);
        }
        setPaintColor(ColorUtils.blendARGB(this.progressColor, this.smallLineColor, this.transitionProgress), 1.0f);
        drawProgressBar(canvas, this.rect, paint);
        setPaintColor(ColorUtils.blendARGB(this.circleColor, getProgress() == 0.0f ? 0 : this.smallLineColor, this.transitionProgress), 1.0f - this.transitionProgress);
        canvas.drawCircle(this.rect.right, this.rect.centerY(), (1.0f - loopBack) * circleRadius, paint);
        drawTimestampLabel(canvas);
    }

    /* JADX WARN: Removed duplicated region for block: B:81:0x0218 A[EDGE_INSN: B:81:0x0218->B:74:0x0218 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0203 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawProgressBar(android.graphics.Canvas r27, android.graphics.RectF r28, android.graphics.Paint r29) {
        /*
            Method dump skipped, instructions count: 546
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayerSeekBar.drawProgressBar(android.graphics.Canvas, android.graphics.RectF, android.graphics.Paint):void");
    }

    private void drawTimestampLabel(Canvas canvas) {
        float right;
        float f;
        float f2;
        ArrayList<Pair<Float, CharSequence>> arrayList = this.timestamps;
        if (arrayList != null && !arrayList.isEmpty()) {
            float progress = ((this.pressed || this.pressedDelayed) ? this.draggingThumbX : this.animatedThumbX) / (this.width - thumbWidth);
            int i = this.timestamps.size() - 1;
            while (true) {
                if (i < 0) {
                    i = -1;
                    break;
                } else if (((Float) this.timestamps.get(i).first).floatValue() - 0.001f <= progress) {
                    break;
                } else {
                    i--;
                }
            }
            if (this.timestampLabel == null) {
                this.timestampLabel = new StaticLayout[2];
            }
            float left = AndroidUtilities.lerp(thumbWidth / 2.0f, 0.0f, this.transitionProgress) + this.horizontalPadding;
            float right2 = AndroidUtilities.lerp(this.width - (thumbWidth / 2.0f), this.parentView.getWidth() - (this.horizontalPadding * 2.0f), this.transitionProgress) + this.horizontalPadding;
            float rightPadded = (this.width - (thumbWidth / 2.0f)) + this.horizontalPadding;
            float width = Math.abs(left - rightPadded) - AndroidUtilities.dp(16.0f);
            float f3 = this.lastWidth;
            if (f3 > 0.0f && Math.abs(f3 - width) > 0.01f) {
                StaticLayout[] staticLayoutArr = this.timestampLabel;
                if (staticLayoutArr[0] != null) {
                    staticLayoutArr[0] = makeStaticLayout(staticLayoutArr[0].getText(), (int) width);
                }
                StaticLayout[] staticLayoutArr2 = this.timestampLabel;
                if (staticLayoutArr2[1] != null) {
                    staticLayoutArr2[1] = makeStaticLayout(staticLayoutArr2[1].getText(), (int) width);
                }
            }
            this.lastWidth = width;
            if (i != this.currentTimestamp) {
                StaticLayout[] staticLayoutArr3 = this.timestampLabel;
                staticLayoutArr3[1] = staticLayoutArr3[0];
                if (this.pressed) {
                    try {
                        this.parentView.performHapticFeedback(9, 1);
                    } catch (Exception e) {
                    }
                }
                if (i < 0 || i >= this.timestamps.size()) {
                    this.timestampLabel[0] = null;
                } else {
                    CharSequence label = (CharSequence) this.timestamps.get(i).second;
                    if (label != null) {
                        this.timestampLabel[0] = makeStaticLayout(label, (int) width);
                    } else {
                        this.timestampLabel[0] = null;
                    }
                }
                this.timestampChangeT = 0.0f;
                if (i == -1) {
                    this.timestampChangeDirection = -1;
                } else {
                    int i2 = this.currentTimestamp;
                    if (i2 == -1) {
                        this.timestampChangeDirection = 1;
                    } else if (i < i2) {
                        this.timestampChangeDirection = -1;
                    } else if (i > i2) {
                        this.timestampChangeDirection = 1;
                    }
                }
                this.lastTimestamp = this.currentTimestamp;
                this.currentTimestamp = i;
            }
            if (this.timestampChangeT < 1.0f) {
                long tx = Math.min(17L, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate));
                float duration = this.timestamps.size() > 8 ? 160.0f : 220.0f;
                this.timestampChangeT = Math.min(this.timestampChangeT + (((float) tx) / duration), 1.0f);
                this.parentView.invalidate();
                right = right2;
                this.lastTimestampUpdate = SystemClock.elapsedRealtime();
            } else {
                right = right2;
            }
            if (this.timestampsAppearing < 1.0f) {
                long tx2 = Math.min(17L, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate));
                this.timestampsAppearing = Math.min(this.timestampsAppearing + (((float) tx2) / 200.0f), 1.0f);
                this.parentView.invalidate();
                this.lastTimestampsAppearingUpdate = SystemClock.elapsedRealtime();
            }
            float changeT = CubicBezierInterpolator.DEFAULT.getInterpolation(this.timestampChangeT);
            canvas.save();
            int i3 = this.height;
            float bottom = AndroidUtilities.lerp((this.lineHeight + i3) / 2.0f, i3 - AndroidUtilities.dp(3.0f), this.transitionProgress);
            canvas.translate(((right - rightPadded) * this.transitionProgress) + left, AndroidUtilities.dp(12.0f) + bottom);
            if (this.timestampLabel[1] != null) {
                canvas.save();
                if (this.timestampChangeDirection != 0) {
                    f2 = 0.0f;
                    canvas.translate(AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(16.0f) * (-this.timestampChangeDirection) * changeT), 0.0f);
                } else {
                    f2 = 0.0f;
                }
                canvas.translate(f2, (-this.timestampLabel[1].getHeight()) / 2.0f);
                this.timestampLabelPaint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f * (1.0f - changeT) * this.timestampsAppearing));
                this.timestampLabel[1].draw(canvas);
                canvas.restore();
            }
            if (this.timestampLabel[0] != null) {
                canvas.save();
                if (this.timestampChangeDirection != 0) {
                    f = 0.0f;
                    canvas.translate(AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(16.0f) * this.timestampChangeDirection * (1.0f - changeT)), 0.0f);
                } else {
                    f = 0.0f;
                }
                canvas.translate(f, (-this.timestampLabel[0].getHeight()) / 2.0f);
                this.timestampLabelPaint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f * changeT * this.timestampsAppearing));
                this.timestampLabel[0].draw(canvas);
                canvas.restore();
            }
            canvas.restore();
        }
    }

    private StaticLayout makeStaticLayout(CharSequence text, int width) {
        if (this.timestampLabelPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            this.timestampLabelPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            this.timestampLabelPaint.setColor(-1);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            return StaticLayout.Builder.obtain(text, 0, text.length(), this.timestampLabelPaint, width).setMaxLines(1).setAlignment(Layout.Alignment.ALIGN_CENTER).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(Math.min(AndroidUtilities.dp(400.0f), width)).build();
        }
        return new StaticLayout(text, 0, text.length(), this.timestampLabelPaint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, Math.min(AndroidUtilities.dp(400.0f), width));
    }

    private void setPaintColor(int color, float alpha) {
        if (alpha < 1.0f) {
            color = ColorUtils.setAlphaComponent(color, (int) (Color.alpha(color) * alpha));
        }
        paint.setColor(color);
    }
}
