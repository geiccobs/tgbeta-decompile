package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SeekBarView extends FrameLayout {
    private AnimatedFloat animatedThumbX;
    private float bufferedProgress;
    boolean captured;
    private float currentRadius;
    public SeekBarViewDelegate delegate;
    private Drawable hoverDrawable;
    private Paint innerPaint1;
    private long lastUpdateTime;
    private Paint outerPaint1;
    private boolean pressed;
    private int[] pressedState;
    private float progressToSet;
    private boolean reportChanges;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private int selectorWidth;
    private int separatorsCount;
    float sx;
    float sy;
    private int thumbDX;
    private int thumbSize;
    private int thumbX;
    private float transitionProgress;
    private int transitionThumbX;
    private boolean twoSided;

    /* loaded from: classes5.dex */
    public interface SeekBarViewDelegate {
        CharSequence getContentDescription();

        int getStepsCount();

        void onSeekBarDrag(boolean z, float f);

        void onSeekBarPressed(boolean z);

        /* renamed from: org.telegram.ui.Components.SeekBarView$SeekBarViewDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static CharSequence $default$getContentDescription(SeekBarViewDelegate _this) {
                return null;
            }

            public static int $default$getStepsCount(SeekBarViewDelegate _this) {
                return 0;
            }
        }
    }

    public SeekBarView(Context context) {
        this(context, null);
    }

    public SeekBarView(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, false, resourcesProvider);
    }

    public SeekBarView(Context context, boolean inPercents, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.animatedThumbX = new AnimatedFloat(this, 150L, CubicBezierInterpolator.DEFAULT);
        this.progressToSet = -100.0f;
        this.pressedState = new int[]{16842910, 16842919};
        this.transitionProgress = 1.0f;
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        this.innerPaint1 = new Paint(1);
        Paint paint = new Paint(1);
        this.outerPaint1 = paint;
        paint.setColor(getThemedColor(Theme.key_player_progress));
        this.selectorWidth = AndroidUtilities.dp(32.0f);
        this.thumbSize = AndroidUtilities.dp(24.0f);
        this.currentRadius = AndroidUtilities.dp(6.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable createSelectorDrawable = Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_player_progress), 40), 1, AndroidUtilities.dp(16.0f));
            this.hoverDrawable = createSelectorDrawable;
            createSelectorDrawable.setCallback(this);
            this.hoverDrawable.setVisible(true, false);
        }
        setImportantForAccessibility(1);
        FloatSeekBarAccessibilityDelegate floatSeekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate(inPercents) { // from class: org.telegram.ui.Components.SeekBarView.1
            {
                SeekBarView.this = this;
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                return SeekBarView.this.getProgress();
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float progress) {
                SeekBarView.this.pressed = true;
                SeekBarView.this.setProgress(progress);
                if (SeekBarView.this.delegate != null) {
                    SeekBarView.this.delegate.onSeekBarDrag(true, progress);
                }
                SeekBarView.this.pressed = false;
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getDelta() {
                int stepsCount = SeekBarView.this.delegate.getStepsCount();
                if (stepsCount > 0) {
                    return 1.0f / stepsCount;
                }
                return super.getDelta();
            }

            @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
            public CharSequence getContentDescription(View host) {
                if (SeekBarView.this.delegate != null) {
                    return SeekBarView.this.delegate.getContentDescription();
                }
                return null;
            }
        };
        this.seekBarAccessibilityDelegate = floatSeekBarAccessibilityDelegate;
        setAccessibilityDelegate(floatSeekBarAccessibilityDelegate);
    }

    public void setSeparatorsCount(int separatorsCount) {
        this.separatorsCount = separatorsCount;
    }

    public void setColors(int inner, int outer) {
        this.innerPaint1.setColor(inner);
        this.outerPaint1.setColor(outer);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setSelectorDrawableColor(drawable, ColorUtils.setAlphaComponent(outer, 40), true);
        }
    }

    public void setTwoSided(boolean value) {
        this.twoSided = value;
    }

    public boolean isTwoSided() {
        return this.twoSided;
    }

    public void setInnerColor(int color) {
        this.innerPaint1.setColor(color);
    }

    public void setOuterColor(int color) {
        this.outerPaint1.setColor(color);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setSelectorDrawableColor(drawable, ColorUtils.setAlphaComponent(color, 40), true);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouch(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return onTouch(event);
    }

    public void setReportChanges(boolean value) {
        this.reportChanges = value;
    }

    public void setDelegate(SeekBarViewDelegate seekBarViewDelegate) {
        this.delegate = seekBarViewDelegate;
    }

    public boolean onTouch(MotionEvent ev) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (ev.getAction() == 0) {
            this.sx = ev.getX();
            this.sy = ev.getY();
            return true;
        }
        if (ev.getAction() == 1 || ev.getAction() == 3) {
            this.captured = false;
            if (ev.getAction() == 1) {
                if (Math.abs(ev.getY() - this.sy) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    int additionWidth = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (this.thumbX - additionWidth > ev.getX() || ev.getX() > this.thumbX + this.thumbSize + additionWidth) {
                        int x = ((int) ev.getX()) - (this.thumbSize / 2);
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth() - this.selectorWidth) {
                            this.thumbX = getMeasuredWidth() - this.selectorWidth;
                        }
                    }
                    this.thumbDX = (int) (ev.getX() - this.thumbX);
                    this.pressed = true;
                }
            }
            if (this.pressed) {
                if (ev.getAction() == 1) {
                    if (this.twoSided) {
                        float w = (getMeasuredWidth() - this.selectorWidth) / 2;
                        int i = this.thumbX;
                        if (i >= w) {
                            this.delegate.onSeekBarDrag(false, (i - w) / w);
                        } else {
                            this.delegate.onSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((w - i) / w)));
                        }
                    } else {
                        this.delegate.onSeekBarDrag(true, this.thumbX / (getMeasuredWidth() - this.selectorWidth));
                    }
                }
                if (Build.VERSION.SDK_INT >= 21 && (drawable = this.hoverDrawable) != null) {
                    drawable.setState(StateSet.NOTHING);
                }
                this.delegate.onSeekBarPressed(false);
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (ev.getAction() == 2) {
            if (!this.captured) {
                ViewConfiguration vc = ViewConfiguration.get(getContext());
                if (Math.abs(ev.getY() - this.sy) <= vc.getScaledTouchSlop() && Math.abs(ev.getX() - this.sx) > vc.getScaledTouchSlop()) {
                    this.captured = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int additionWidth2 = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (ev.getY() >= 0.0f && ev.getY() <= getMeasuredHeight()) {
                        if (this.thumbX - additionWidth2 > ev.getX() || ev.getX() > this.thumbX + this.thumbSize + additionWidth2) {
                            int x2 = ((int) ev.getX()) - (this.thumbSize / 2);
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth() - this.selectorWidth) {
                                this.thumbX = getMeasuredWidth() - this.selectorWidth;
                            }
                        }
                        this.thumbDX = (int) (ev.getX() - this.thumbX);
                        this.pressed = true;
                        this.delegate.onSeekBarPressed(true);
                        if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.hoverDrawable) != null) {
                            drawable3.setState(this.pressedState);
                            this.hoverDrawable.setHotspot(ev.getX(), ev.getY());
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (this.pressed) {
                int x3 = (int) (ev.getX() - this.thumbDX);
                this.thumbX = x3;
                if (x3 < 0) {
                    this.thumbX = 0;
                } else if (x3 > getMeasuredWidth() - this.selectorWidth) {
                    this.thumbX = getMeasuredWidth() - this.selectorWidth;
                }
                if (this.reportChanges) {
                    if (this.twoSided) {
                        float w2 = (getMeasuredWidth() - this.selectorWidth) / 2;
                        int i2 = this.thumbX;
                        if (i2 >= w2) {
                            this.delegate.onSeekBarDrag(false, (i2 - w2) / w2);
                        } else {
                            this.delegate.onSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((w2 - i2) / w2)));
                        }
                    } else {
                        this.delegate.onSeekBarDrag(false, this.thumbX / (getMeasuredWidth() - this.selectorWidth));
                    }
                }
                if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.hoverDrawable) != null) {
                    drawable2.setHotspot(ev.getX(), ev.getY());
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public float getProgress() {
        if (getMeasuredWidth() == 0) {
            return this.progressToSet;
        }
        return this.thumbX / (getMeasuredWidth() - this.selectorWidth);
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setProgress(float progress, boolean animated) {
        int newThumbX;
        if (getMeasuredWidth() == 0) {
            this.progressToSet = progress;
            return;
        }
        this.progressToSet = -100.0f;
        if (this.twoSided) {
            int w = getMeasuredWidth() - this.selectorWidth;
            float cx = w / 2;
            if (progress < 0.0f) {
                newThumbX = (int) Math.ceil(((w / 2) * (-(1.0f + progress))) + cx);
            } else {
                newThumbX = (int) Math.ceil(((w / 2) * progress) + cx);
            }
        } else {
            newThumbX = (int) Math.ceil((getMeasuredWidth() - this.selectorWidth) * progress);
        }
        int i = this.thumbX;
        if (i != newThumbX) {
            if (animated) {
                this.transitionThumbX = i;
                this.transitionProgress = 0.0f;
            }
            this.thumbX = newThumbX;
            if (newThumbX < 0) {
                this.thumbX = 0;
            } else if (newThumbX > getMeasuredWidth() - this.selectorWidth) {
                this.thumbX = getMeasuredWidth() - this.selectorWidth;
            }
            invalidate();
        }
    }

    public void setBufferedProgress(float progress) {
        this.bufferedProgress = progress;
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.progressToSet != -100.0f && getMeasuredWidth() > 0) {
            setProgress(this.progressToSet);
            this.progressToSet = -100.0f;
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.hoverDrawable;
    }

    public boolean isDragging() {
        return this.pressed;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        int thumbX = this.thumbX;
        if (!this.twoSided && this.separatorsCount > 1) {
            float step = (getMeasuredWidth() - this.selectorWidth) / (this.separatorsCount - 1.0f);
            thumbX = (int) this.animatedThumbX.set(Math.round(thumbX / step) * step);
        }
        int y = (getMeasuredHeight() - this.thumbSize) / 2;
        this.innerPaint1.setColor(getThemedColor(Theme.key_player_progressBackground));
        canvas.drawRect(this.selectorWidth / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), getMeasuredWidth() - (this.selectorWidth / 2), (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.innerPaint1);
        if (!this.twoSided && this.separatorsCount > 1) {
            for (int i2 = 0; i2 < this.separatorsCount; i2++) {
                canvas.drawCircle(AndroidUtilities.lerp(this.selectorWidth / 2, getMeasuredWidth() - (this.selectorWidth / 2), i2 / (this.separatorsCount - 1.0f)), getMeasuredHeight() / 2, AndroidUtilities.dp(1.6f), this.innerPaint1);
            }
        }
        if (this.bufferedProgress > 0.0f) {
            this.innerPaint1.setColor(getThemedColor(Theme.key_player_progressCachedBackground));
            canvas.drawRect(this.selectorWidth / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), (this.selectorWidth / 2) + (this.bufferedProgress * (getMeasuredWidth() - this.selectorWidth)), (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.innerPaint1);
        }
        float f = 6.0f;
        if (this.twoSided) {
            canvas.drawRect((getMeasuredWidth() / 2) - AndroidUtilities.dp(1.0f), (getMeasuredHeight() / 2) - AndroidUtilities.dp(6.0f), (getMeasuredWidth() / 2) + AndroidUtilities.dp(1.0f), (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f), this.outerPaint1);
            if (thumbX > (getMeasuredWidth() - this.selectorWidth) / 2) {
                canvas.drawRect(getMeasuredWidth() / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), (this.selectorWidth / 2) + thumbX, (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.outerPaint1);
            } else {
                canvas.drawRect((i / 2) + thumbX, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), getMeasuredWidth() / 2, (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.outerPaint1);
            }
        } else {
            canvas.drawRect(this.selectorWidth / 2, (getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f), (this.selectorWidth / 2) + thumbX, (getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f), this.outerPaint1);
            if (this.separatorsCount > 1) {
                for (int i3 = 0; i3 < this.separatorsCount; i3++) {
                    float cx = AndroidUtilities.lerp(this.selectorWidth / 2, getMeasuredWidth() - (this.selectorWidth / 2), i3 / (this.separatorsCount - 1.0f));
                    if (cx > (this.selectorWidth / 2) + thumbX) {
                        break;
                    }
                    canvas.drawCircle(cx, getMeasuredHeight() / 2, AndroidUtilities.dp(1.4f), this.outerPaint1);
                }
            }
        }
        if (this.hoverDrawable != null) {
            int dx = ((this.selectorWidth / 2) + thumbX) - AndroidUtilities.dp(16.0f);
            int dy = ((this.thumbSize / 2) + y) - AndroidUtilities.dp(16.0f);
            this.hoverDrawable.setBounds(dx, dy, AndroidUtilities.dp(32.0f) + dx, AndroidUtilities.dp(32.0f) + dy);
            this.hoverDrawable.draw(canvas);
        }
        boolean needInvalidate = false;
        if (this.pressed) {
            f = 8.0f;
        }
        int newRad = AndroidUtilities.dp(f);
        long newUpdateTime = SystemClock.elapsedRealtime();
        long dt = newUpdateTime - this.lastUpdateTime;
        if (dt > 18) {
            dt = 16;
        }
        float f2 = this.currentRadius;
        if (f2 != newRad) {
            if (f2 < newRad) {
                float dp = f2 + (AndroidUtilities.dp(1.0f) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > newRad) {
                    this.currentRadius = newRad;
                }
            } else {
                float dp2 = f2 - (AndroidUtilities.dp(1.0f) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < newRad) {
                    this.currentRadius = newRad;
                }
            }
            needInvalidate = true;
        }
        float f3 = this.transitionProgress;
        if (f3 < 1.0f) {
            float f4 = f3 + (((float) dt) / 225.0f);
            this.transitionProgress = f4;
            if (f4 < 1.0f) {
                needInvalidate = true;
            } else {
                this.transitionProgress = 1.0f;
            }
        }
        if (this.transitionProgress < 1.0f) {
            float oldCircleProgress = 1.0f - Easings.easeInQuad.getInterpolation(Math.min(1.0f, this.transitionProgress * 3.0f));
            float newCircleProgress = Easings.easeOutQuad.getInterpolation(this.transitionProgress);
            if (oldCircleProgress > 0.0f) {
                canvas.drawCircle(this.transitionThumbX + (this.selectorWidth / 2), (this.thumbSize / 2) + y, this.currentRadius * oldCircleProgress, this.outerPaint1);
            }
            canvas.drawCircle((this.selectorWidth / 2) + thumbX, (this.thumbSize / 2) + y, this.currentRadius * newCircleProgress, this.outerPaint1);
        } else {
            canvas.drawCircle((this.selectorWidth / 2) + thumbX, (this.thumbSize / 2) + y, this.currentRadius, this.outerPaint1);
        }
        if (needInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    public SeekBarAccessibilityDelegate getSeekBarAccessibilityDelegate() {
        return this.seekBarAccessibilityDelegate;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
