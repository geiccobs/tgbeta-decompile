package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SlideChooseView extends View {
    private final SeekBarAccessibilityDelegate accessibilityDelegate;
    private Callback callback;
    private int circleSize;
    private int dashedFrom;
    private int gapSize;
    private int lastDash;
    private Paint linePaint;
    private int lineSize;
    private boolean moving;
    private AnimatedFloat movingAnimatedHolder;
    private int[] optionsSizes;
    private String[] optionsStr;
    private Paint paint;
    private final Theme.ResourcesProvider resourcesProvider;
    private int selectedIndex;
    private AnimatedFloat selectedIndexAnimatedHolder;
    private float selectedIndexTouch;
    private int sideSide;
    private boolean startMoving;
    private int startMovingPreset;
    private TextPaint textPaint;
    private boolean touchWasClose;
    private float xTouchDown;
    private float yTouchDown;

    public SlideChooseView(Context context) {
        this(context, null);
    }

    public SlideChooseView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.dashedFrom = -1;
        this.selectedIndexAnimatedHolder = new AnimatedFloat(this, 120L, CubicBezierInterpolator.DEFAULT);
        this.movingAnimatedHolder = new AnimatedFloat(this, 150L, CubicBezierInterpolator.DEFAULT);
        this.touchWasClose = false;
        this.resourcesProvider = resourcesProvider;
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        Paint paint = new Paint(1);
        this.linePaint = paint;
        paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.accessibilityDelegate = new IntSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.Components.SlideChooseView.1
            @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
            protected int getProgress() {
                return SlideChooseView.this.selectedIndex;
            }

            @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
            protected void setProgress(int progress) {
                SlideChooseView.this.setOption(progress);
            }

            @Override // org.telegram.ui.Components.IntSeekBarAccessibilityDelegate
            protected int getMaxValue() {
                return SlideChooseView.this.optionsStr.length - 1;
            }

            @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
            protected CharSequence getContentDescription(View host) {
                if (SlideChooseView.this.selectedIndex < SlideChooseView.this.optionsStr.length) {
                    return SlideChooseView.this.optionsStr[SlideChooseView.this.selectedIndex];
                }
                return null;
            }
        };
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setOptions(int selected, String... options) {
        this.optionsStr = options;
        this.selectedIndex = selected;
        this.optionsSizes = new int[options.length];
        int i = 0;
        while (true) {
            String[] strArr = this.optionsStr;
            if (i < strArr.length) {
                this.optionsSizes[i] = (int) Math.ceil(this.textPaint.measureText(strArr[i]));
                i++;
            } else {
                requestLayout();
                return;
            }
        }
    }

    public void setDashedFrom(int from) {
        this.dashedFrom = from;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int i;
        float x = event.getX();
        float y = event.getY();
        float indexTouch = MathUtils.clamp(((x - this.sideSide) + (this.circleSize / 2.0f)) / ((this.lineSize + (this.gapSize * 2)) + i), 0.0f, this.optionsStr.length - 1);
        boolean isClose = Math.abs(indexTouch - ((float) Math.round(indexTouch))) < 0.35f;
        if (isClose) {
            indexTouch = Math.round(indexTouch);
        }
        if (event.getAction() == 0) {
            this.xTouchDown = x;
            this.yTouchDown = y;
            this.selectedIndexTouch = indexTouch;
            this.startMovingPreset = this.selectedIndex;
            this.startMoving = true;
            invalidate();
        } else if (event.getAction() == 2) {
            if (!this.moving && Math.abs(this.xTouchDown - x) > Math.abs(this.yTouchDown - y)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (this.startMoving && Math.abs(this.xTouchDown - x) >= AndroidUtilities.dp(2.0f)) {
                this.moving = true;
                this.startMoving = false;
            }
            if (this.moving) {
                this.selectedIndexTouch = indexTouch;
                invalidate();
                if (Math.round(this.selectedIndexTouch) != this.selectedIndex && isClose) {
                    setOption(Math.round(this.selectedIndexTouch));
                }
            }
            invalidate();
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            if (!this.moving) {
                this.selectedIndexTouch = indexTouch;
                if (Math.round(indexTouch) != this.selectedIndex) {
                    setOption(Math.round(this.selectedIndexTouch));
                }
            } else {
                int i2 = this.selectedIndex;
                if (i2 != this.startMovingPreset) {
                    setOption(i2);
                }
            }
            Callback callback = this.callback;
            if (callback != null) {
                callback.onTouchEnd();
            }
            this.startMoving = false;
            this.moving = false;
            invalidate();
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return true;
    }

    public void setOption(int index) {
        if (this.selectedIndex != index) {
            try {
                performHapticFeedback(9, 1);
            } catch (Exception e) {
            }
        }
        this.selectedIndex = index;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onOptionSelected(index);
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), C.BUFFER_FLAG_ENCRYPTED));
        this.circleSize = AndroidUtilities.dp(6.0f);
        this.gapSize = AndroidUtilities.dp(2.0f);
        this.sideSide = AndroidUtilities.dp(22.0f);
        int measuredWidth = getMeasuredWidth();
        int i = this.circleSize;
        String[] strArr = this.optionsStr;
        this.lineSize = (((measuredWidth - (i * strArr.length)) - ((this.gapSize * 2) * (strArr.length - 1))) - (this.sideSide * 2)) / (strArr.length - 1);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float t;
        int x;
        int width;
        float selectedIndexAnimated = this.selectedIndexAnimatedHolder.set(this.selectedIndex);
        float f = 0.0f;
        float f2 = 1.0f;
        float movingAnimated = this.movingAnimatedHolder.set(this.moving ? 1.0f : 0.0f);
        int i = 2;
        int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
        int a = 0;
        while (a < this.optionsStr.length) {
            int i2 = this.sideSide;
            int i3 = this.lineSize + (this.gapSize * 2);
            int i4 = this.circleSize;
            int cx = i2 + ((i3 + i4) * a) + (i4 / i);
            float t2 = Math.max(f, f2 - Math.abs(a - selectedIndexAnimated));
            float ut = MathUtils.clamp((selectedIndexAnimated - a) + f2, f, f2);
            int color = ColorUtils.blendARGB(getThemedColor(Theme.key_switchTrack), getThemedColor(Theme.key_switchTrackChecked), ut);
            this.paint.setColor(color);
            this.linePaint.setColor(color);
            canvas.drawCircle(cx, cy, AndroidUtilities.lerp(this.circleSize / i, AndroidUtilities.dp(6.0f), t2), this.paint);
            if (a != 0) {
                int x2 = ((cx - (this.circleSize / i)) - this.gapSize) - this.lineSize;
                int width2 = this.lineSize;
                int i5 = this.dashedFrom;
                if (i5 != -1 && a - 1 >= i5) {
                    int x3 = AndroidUtilities.dp(3.0f) + x2;
                    int dash = (width2 - AndroidUtilities.dp(3.0f)) / AndroidUtilities.dp(13.0f);
                    if (this.lastDash != dash) {
                        float gap = (width - (AndroidUtilities.dp(8.0f) * dash)) / (dash - 1);
                        Paint paint = this.linePaint;
                        float[] fArr = new float[i];
                        fArr[0] = AndroidUtilities.dp(6.0f);
                        fArr[1] = gap;
                        paint.setPathEffect(new DashPathEffect(fArr, 0.0f));
                        this.lastDash = dash;
                    }
                    t = t2;
                    canvas.drawLine(AndroidUtilities.dp(1.0f) + x3, cy, (x3 + width) - AndroidUtilities.dp(1.0f), cy, this.linePaint);
                } else {
                    t = t2;
                    float nt = MathUtils.clamp(1.0f - Math.abs((a - selectedIndexAnimated) - 1.0f), 0.0f, 1.0f);
                    float nct = MathUtils.clamp(1.0f - Math.min(Math.abs(a - selectedIndexAnimated), Math.abs((a - selectedIndexAnimated) - 1.0f)), 0.0f, 1.0f);
                    int width3 = (int) (width2 - (AndroidUtilities.dp(3.0f) * nct));
                    canvas.drawRect((int) (x2 + (AndroidUtilities.dp(3.0f) * nt)), cy - AndroidUtilities.dp(1.0f), x + width3, AndroidUtilities.dp(1.0f) + cy, this.paint);
                }
            } else {
                t = t2;
            }
            int size = this.optionsSizes[a];
            String text = this.optionsStr[a];
            this.textPaint.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_windowBackgroundWhiteGrayText), getThemedColor(Theme.key_windowBackgroundWhiteBlueText), t));
            if (a == 0) {
                canvas.drawText(text, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(28.0f), this.textPaint);
            } else if (a == this.optionsStr.length - 1) {
                canvas.drawText(text, (getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f), AndroidUtilities.dp(28.0f), this.textPaint);
            } else {
                canvas.drawText(text, cx - (size / 2), AndroidUtilities.dp(28.0f), this.textPaint);
            }
            a++;
            f = 0.0f;
            f2 = 1.0f;
            i = 2;
        }
        int i6 = this.lineSize + (this.gapSize * 2);
        int i7 = this.circleSize;
        float cx2 = this.sideSide + ((i6 + i7) * selectedIndexAnimated) + (i7 / 2);
        this.paint.setColor(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_switchTrackChecked), 80));
        canvas.drawCircle(cx2, cy, AndroidUtilities.dp(12.0f * movingAnimated), this.paint);
        this.paint.setColor(getThemedColor(Theme.key_switchTrackChecked));
        canvas.drawCircle(cx2, cy, AndroidUtilities.dp(6.0f), this.paint);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        this.accessibilityDelegate.onInitializeAccessibilityNodeInfoInternal(this, info);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return super.performAccessibilityAction(action, arguments) || this.accessibilityDelegate.performAccessibilityActionInternal(this, action, arguments);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes5.dex */
    public interface Callback {
        void onOptionSelected(int i);

        void onTouchEnd();

        /* renamed from: org.telegram.ui.Components.SlideChooseView$Callback$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onTouchEnd(Callback _this) {
            }
        }
    }
}
