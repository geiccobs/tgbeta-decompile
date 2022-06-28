package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.CubicBezierInterpolator;
/* loaded from: classes5.dex */
public class VoIPFloatingLayout extends FrameLayout {
    public boolean alwaysFloating;
    public int bottomOffset;
    float bottomPadding;
    private VoIPFloatingLayoutDelegate delegate;
    private boolean floatingMode;
    int lastH;
    WindowInsets lastInsets;
    int lastW;
    float leftPadding;
    public boolean measuredAsFloatingMode;
    boolean moving;
    ValueAnimator mutedAnimator;
    Drawable mutedDrawable;
    Drawable outerShadow;
    float rightPadding;
    public float savedRelativePositionX;
    public float savedRelativePositionY;
    private boolean setedFloatingMode;
    float starX;
    float starY;
    float startMovingFromX;
    float startMovingFromY;
    long startTime;
    ValueAnimator switchToFloatingModeAnimator;
    private boolean switchingToFloatingMode;
    public boolean switchingToPip;
    View.OnClickListener tapListener;
    float topPadding;
    float touchSlop;
    private boolean uiVisible;
    public float updatePositionFromX;
    public float updatePositionFromY;
    private final float FLOATING_MODE_SCALE = 0.23f;
    final Path path = new Path();
    final RectF rectF = new RectF();
    final Paint xRefPaint = new Paint(1);
    Paint mutedPaint = new Paint(1);
    public float relativePositionToSetX = -1.0f;
    float relativePositionToSetY = -1.0f;
    float toFloatingModeProgress = 0.0f;
    float mutedProgress = 0.0f;
    private float overrideCornerRadius = -1.0f;
    private boolean active = true;
    private ValueAnimator.AnimatorUpdateListener progressUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPFloatingLayout.1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFloatingLayout.this.toFloatingModeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (VoIPFloatingLayout.this.delegate != null) {
                VoIPFloatingLayout.this.delegate.onChange(VoIPFloatingLayout.this.toFloatingModeProgress, VoIPFloatingLayout.this.measuredAsFloatingMode);
            }
            VoIPFloatingLayout.this.invalidate();
        }
    };
    private ValueAnimator.AnimatorUpdateListener mutedUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPFloatingLayout$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFloatingLayout.this.m3259lambda$new$0$orgtelegramuiComponentsvoipVoIPFloatingLayout(valueAnimator);
        }
    };

    /* loaded from: classes5.dex */
    public interface VoIPFloatingLayoutDelegate {
        void onChange(float f, boolean z);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-VoIPFloatingLayout */
    public /* synthetic */ void m3259lambda$new$0$orgtelegramuiComponentsvoipVoIPFloatingLayout(ValueAnimator valueAnimator) {
        this.mutedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public VoIPFloatingLayout(Context context) {
        super(context);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.voip.VoIPFloatingLayout.2
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    if (VoIPFloatingLayout.this.overrideCornerRadius >= 0.0f) {
                        if (VoIPFloatingLayout.this.overrideCornerRadius < 1.0f) {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        } else {
                            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), VoIPFloatingLayout.this.overrideCornerRadius);
                        }
                    } else if (!VoIPFloatingLayout.this.floatingMode) {
                        outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                    } else {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), VoIPFloatingLayout.this.floatingMode ? AndroidUtilities.dp(4.0f) : 0.0f);
                    }
                }
            });
            setClipToOutline(true);
        }
        this.mutedPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 102));
        this.mutedDrawable = ContextCompat.getDrawable(context, R.drawable.calls_mute_mini);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        this.measuredAsFloatingMode = false;
        if (this.floatingMode) {
            width = (int) (width * 0.23f);
            height = (int) (height * 0.23f);
            this.measuredAsFloatingMode = true;
        } else if (!this.switchingToPip) {
            setTranslationX(0.0f);
            setTranslationY(0.0f);
        }
        VoIPFloatingLayoutDelegate voIPFloatingLayoutDelegate = this.delegate;
        if (voIPFloatingLayoutDelegate != null) {
            voIPFloatingLayoutDelegate.onChange(this.toFloatingModeProgress, this.measuredAsFloatingMode);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        if (getMeasuredHeight() != this.lastH && getMeasuredWidth() != this.lastW) {
            this.path.reset();
            this.rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            this.path.addRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Path.Direction.CW);
            this.path.toggleInverseFillType();
        }
        this.lastH = getMeasuredHeight();
        this.lastW = getMeasuredWidth();
        updatePadding();
    }

    private void updatePadding() {
        float f = 16.0f;
        this.leftPadding = AndroidUtilities.dp(16.0f);
        this.rightPadding = AndroidUtilities.dp(16.0f);
        this.topPadding = this.uiVisible ? AndroidUtilities.dp(60.0f) : AndroidUtilities.dp(16.0f);
        if (this.uiVisible) {
            f = 100.0f;
        }
        this.bottomPadding = AndroidUtilities.dp(f) + this.bottomOffset;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void setDelegate(VoIPFloatingLayoutDelegate voIPFloatingLayoutDelegate) {
        this.delegate = voIPFloatingLayoutDelegate;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        WindowInsets windowInsets;
        ViewParent parent = getParent();
        if (!this.floatingMode || this.switchingToFloatingMode || !this.active) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                if (this.floatingMode && !this.switchingToFloatingMode) {
                    this.startTime = System.currentTimeMillis();
                    this.starX = event.getX() + getX();
                    this.starY = event.getY() + getY();
                    animate().setListener(null).cancel();
                    animate().scaleY(1.05f).scaleX(1.05f).alpha(1.0f).setStartDelay(0L).start();
                    break;
                }
                break;
            case 1:
            case 3:
                if (parent != null && this.floatingMode && !this.switchingToFloatingMode) {
                    parent.requestDisallowInterceptTouchEvent(false);
                    animate().setListener(null).cancel();
                    ViewPropertyAnimator animator = animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setStartDelay(0L);
                    if (this.tapListener != null && !this.moving && System.currentTimeMillis() - this.startTime < 200) {
                        this.tapListener.onClick(this);
                    }
                    int parentWidth = ((View) getParent()).getMeasuredWidth();
                    int parentHeight = ((View) getParent()).getMeasuredHeight();
                    float maxTop = this.topPadding;
                    float maxBottom = this.bottomPadding;
                    if (Build.VERSION.SDK_INT > 20 && (windowInsets = this.lastInsets) != null) {
                        maxTop += windowInsets.getSystemWindowInsetTop();
                        maxBottom += this.lastInsets.getSystemWindowInsetBottom();
                    }
                    float x = getX();
                    float f = this.leftPadding;
                    if (x < f) {
                        animator.translationX(f);
                    } else if (getX() + getMeasuredWidth() > parentWidth - this.rightPadding) {
                        animator.translationX((parentWidth - getMeasuredWidth()) - this.rightPadding);
                    }
                    if (getY() < maxTop) {
                        animator.translationY(maxTop);
                    } else if (getY() + getMeasuredHeight() > parentHeight - maxBottom) {
                        animator.translationY((parentHeight - getMeasuredHeight()) - maxBottom);
                    }
                    animator.setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
                this.moving = false;
                break;
            case 2:
                float dx = (event.getX() + getX()) - this.starX;
                float dy = (event.getY() + getY()) - this.starY;
                if (!this.moving) {
                    float f2 = (dx * dx) + (dy * dy);
                    float f3 = this.touchSlop;
                    if (f2 > f3 * f3) {
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        this.moving = true;
                        this.starX = event.getX() + getX();
                        this.starY = event.getY() + getY();
                        this.startMovingFromX = getTranslationX();
                        this.startMovingFromY = getTranslationY();
                        dx = 0.0f;
                        dy = 0.0f;
                    }
                }
                if (this.moving) {
                    setTranslationX(this.startMovingFromX + dx);
                    setTranslationY(this.startMovingFromY + dy);
                    break;
                }
                break;
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.updatePositionFromX >= 0.0f) {
            animate().setListener(null).cancel();
            setTranslationX(this.updatePositionFromX);
            setTranslationY(this.updatePositionFromY);
            setScaleX(1.0f);
            setScaleY(1.0f);
            setAlpha(1.0f);
            this.updatePositionFromX = -1.0f;
            this.updatePositionFromY = -1.0f;
        }
        if (this.relativePositionToSetX >= 0.0f && this.floatingMode && getMeasuredWidth() > 0) {
            setRelativePositionInternal(this.relativePositionToSetX, this.relativePositionToSetY, getMeasuredWidth(), getMeasuredHeight(), false);
            this.relativePositionToSetX = -1.0f;
            this.relativePositionToSetY = -1.0f;
        }
        super.dispatchDraw(canvas);
        if (!this.switchingToFloatingMode) {
            boolean z = this.floatingMode;
            boolean z2 = this.setedFloatingMode;
            if (z != z2) {
                setFloatingMode(z2, true);
            }
        }
        int cX = getMeasuredWidth() >> 1;
        int cY = getMeasuredHeight() - ((int) ((AndroidUtilities.dp(18.0f) * 1.0f) / getScaleY()));
        canvas.save();
        float scaleX = (1.0f / getScaleX()) * this.toFloatingModeProgress * this.mutedProgress;
        float scaleY = (1.0f / getScaleY()) * this.toFloatingModeProgress * this.mutedProgress;
        canvas.scale(scaleX, scaleY, cX, cY);
        canvas.drawCircle(cX, cY, AndroidUtilities.dp(14.0f), this.mutedPaint);
        Drawable drawable = this.mutedDrawable;
        drawable.setBounds(cX - (drawable.getIntrinsicWidth() / 2), cY - (this.mutedDrawable.getIntrinsicHeight() / 2), (this.mutedDrawable.getIntrinsicWidth() / 2) + cX, (this.mutedDrawable.getIntrinsicHeight() / 2) + cY);
        this.mutedDrawable.draw(canvas);
        canvas.restore();
        if (this.switchingToFloatingMode) {
            invalidate();
        }
    }

    public void setInsets(WindowInsets lastInsets) {
        this.lastInsets = lastInsets;
    }

    public void setRelativePosition(float x, float y) {
        ViewParent parent = getParent();
        if (!this.floatingMode || parent == null || ((View) parent).getMeasuredWidth() > 0 || getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            this.relativePositionToSetX = x;
            this.relativePositionToSetY = y;
            return;
        }
        setRelativePositionInternal(x, y, getMeasuredWidth(), getMeasuredHeight(), true);
    }

    public void setUiVisible(boolean uiVisible) {
        ViewParent parent = getParent();
        if (parent == null) {
            this.uiVisible = uiVisible;
        } else {
            this.uiVisible = uiVisible;
        }
    }

    public void setBottomOffset(int bottomOffset, boolean animated) {
        ViewParent parent = getParent();
        if (parent == null || !animated) {
            this.bottomOffset = bottomOffset;
        } else {
            this.bottomOffset = bottomOffset;
        }
    }

    private void setRelativePositionInternal(float xRelative, float yRelative, int width, int height, boolean animated) {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        ViewParent parent = getParent();
        if (parent == null || !this.floatingMode || this.switchingToFloatingMode || !this.active) {
            return;
        }
        float maxBottom = 0.0f;
        float maxTop = (Build.VERSION.SDK_INT < 20 || (windowInsets2 = this.lastInsets) == null) ? 0.0f : windowInsets2.getSystemWindowInsetTop() + this.topPadding;
        if (Build.VERSION.SDK_INT >= 20 && (windowInsets = this.lastInsets) != null) {
            maxBottom = windowInsets.getSystemWindowInsetBottom() + this.bottomPadding;
        }
        float xPoint = this.leftPadding + ((((((View) parent).getMeasuredWidth() - this.leftPadding) - this.rightPadding) - width) * xRelative);
        float yPoint = ((((((View) parent).getMeasuredHeight() - maxBottom) - maxTop) - height) * yRelative) + maxTop;
        if (animated) {
            animate().setListener(null).cancel();
            animate().scaleX(1.0f).scaleY(1.0f).translationX(xPoint).translationY(yPoint).alpha(1.0f).setStartDelay(0L).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            return;
        }
        if (!this.alwaysFloating) {
            animate().setListener(null).cancel();
            setScaleX(1.0f);
            setScaleY(1.0f);
            animate().alpha(1.0f).setDuration(150L).start();
        }
        setTranslationX(xPoint);
        setTranslationY(yPoint);
    }

    public void setFloatingMode(boolean show, boolean animated) {
        if (getMeasuredWidth() <= 0 || getVisibility() != 0) {
            animated = false;
        }
        float f = 1.0f;
        if (!animated) {
            if (this.floatingMode != show) {
                this.floatingMode = show;
                this.setedFloatingMode = show;
                if (!show) {
                    f = 0.0f;
                }
                this.toFloatingModeProgress = f;
                requestLayout();
                if (Build.VERSION.SDK_INT >= 21) {
                    invalidateOutline();
                }
            }
        } else if (this.switchingToFloatingMode) {
            this.setedFloatingMode = show;
        } else if (!show || this.floatingMode) {
            if (!show && this.floatingMode) {
                this.setedFloatingMode = show;
                final float fromX = getTranslationX();
                final float fromY = getTranslationY();
                updatePadding();
                this.floatingMode = false;
                this.switchingToFloatingMode = true;
                requestLayout();
                animate().setListener(null).cancel();
                getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.voip.VoIPFloatingLayout.4
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        if (VoIPFloatingLayout.this.measuredAsFloatingMode) {
                            VoIPFloatingLayout.this.floatingMode = false;
                            VoIPFloatingLayout.this.requestLayout();
                        } else {
                            if (VoIPFloatingLayout.this.switchToFloatingModeAnimator != null) {
                                VoIPFloatingLayout.this.switchToFloatingModeAnimator.cancel();
                            }
                            VoIPFloatingLayout voIPFloatingLayout = VoIPFloatingLayout.this;
                            voIPFloatingLayout.switchToFloatingModeAnimator = ValueAnimator.ofFloat(voIPFloatingLayout.toFloatingModeProgress, 0.0f);
                            VoIPFloatingLayout.this.switchToFloatingModeAnimator.addUpdateListener(VoIPFloatingLayout.this.progressUpdateListener);
                            VoIPFloatingLayout.this.switchToFloatingModeAnimator.setDuration(300L);
                            VoIPFloatingLayout.this.switchToFloatingModeAnimator.start();
                            float fromXfinal = fromX - ((VoIPFloatingLayout.this.getMeasuredWidth() - (VoIPFloatingLayout.this.getMeasuredWidth() * 0.23f)) / 2.0f);
                            float fromYfinal = fromY - ((VoIPFloatingLayout.this.getMeasuredHeight() - (VoIPFloatingLayout.this.getMeasuredHeight() * 0.23f)) / 2.0f);
                            VoIPFloatingLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                            VoIPFloatingLayout.this.setTranslationX(fromXfinal);
                            VoIPFloatingLayout.this.setTranslationY(fromYfinal);
                            VoIPFloatingLayout.this.setScaleX(0.23f);
                            VoIPFloatingLayout.this.setScaleY(0.23f);
                            VoIPFloatingLayout.this.animate().setListener(null).cancel();
                            VoIPFloatingLayout.this.animate().setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPFloatingLayout.4.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    VoIPFloatingLayout.this.switchingToFloatingMode = false;
                                    VoIPFloatingLayout.this.requestLayout();
                                }
                            }).scaleX(1.0f).scaleY(1.0f).translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(300L).setStartDelay(0L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        }
                        return false;
                    }
                });
                return;
            }
            if (!this.floatingMode) {
                f = 0.0f;
            }
            this.toFloatingModeProgress = f;
            this.floatingMode = show;
            this.setedFloatingMode = show;
            requestLayout();
        } else {
            this.floatingMode = true;
            this.setedFloatingMode = show;
            updatePadding();
            float f2 = this.relativePositionToSetX;
            if (f2 >= 0.0f) {
                setRelativePositionInternal(f2, this.relativePositionToSetY, (int) (getMeasuredWidth() * 0.23f), (int) (getMeasuredHeight() * 0.23f), false);
            }
            this.floatingMode = false;
            this.switchingToFloatingMode = true;
            final float toX = getTranslationX();
            final float toY = getTranslationY();
            setTranslationX(0.0f);
            setTranslationY(0.0f);
            invalidate();
            ValueAnimator valueAnimator = this.switchToFloatingModeAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.toFloatingModeProgress, 1.0f);
            this.switchToFloatingModeAnimator = ofFloat;
            ofFloat.addUpdateListener(this.progressUpdateListener);
            this.switchToFloatingModeAnimator.setDuration(300L);
            this.switchToFloatingModeAnimator.start();
            animate().setListener(null).cancel();
            animate().scaleX(0.23f).scaleY(0.23f).translationX(toX - ((getMeasuredWidth() - (getMeasuredWidth() * 0.23f)) / 2.0f)).translationY(toY - ((getMeasuredHeight() - (getMeasuredHeight() * 0.23f)) / 2.0f)).alpha(1.0f).setStartDelay(0L).setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPFloatingLayout.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPFloatingLayout.this.switchingToFloatingMode = false;
                    VoIPFloatingLayout.this.floatingMode = true;
                    VoIPFloatingLayout.this.updatePositionFromX = toX;
                    VoIPFloatingLayout.this.updatePositionFromY = toY;
                    VoIPFloatingLayout.this.requestLayout();
                }
            }).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    public void setMuted(boolean muted, boolean animated) {
        float f = 1.0f;
        if (!animated) {
            ValueAnimator valueAnimator = this.mutedAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!muted) {
                f = 0.0f;
            }
            this.mutedProgress = f;
            invalidate();
            return;
        }
        ValueAnimator valueAnimator2 = this.mutedAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.mutedProgress;
        if (!muted) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.mutedAnimator = ofFloat;
        ofFloat.addUpdateListener(this.mutedUpdateListener);
        this.mutedAnimator.setDuration(150L);
        this.mutedAnimator.start();
    }

    public void setCornerRadius(float cornerRadius) {
        this.overrideCornerRadius = cornerRadius;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
    }

    public void setOnTapListener(View.OnClickListener tapListener) {
        this.tapListener = tapListener;
    }

    public void setRelativePosition(VoIPFloatingLayout fromLayout) {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        ViewParent parent = getParent();
        if (parent == null) {
            return;
        }
        float maxTop = (Build.VERSION.SDK_INT < 20 || (windowInsets2 = this.lastInsets) == null) ? 0.0f : windowInsets2.getSystemWindowInsetTop() + this.topPadding;
        float maxBottom = (Build.VERSION.SDK_INT < 20 || (windowInsets = this.lastInsets) == null) ? 0.0f : windowInsets.getSystemWindowInsetBottom() + this.bottomPadding;
        float xRelative = (fromLayout.getTranslationX() - this.leftPadding) / (((((View) parent).getMeasuredWidth() - this.leftPadding) - this.rightPadding) - fromLayout.getMeasuredWidth());
        float yRelative = (fromLayout.getTranslationY() - maxTop) / (((((View) parent).getMeasuredHeight() - maxBottom) - maxTop) - fromLayout.getMeasuredHeight());
        setRelativePosition(Math.min(1.0f, Math.max(0.0f, xRelative)), Math.min(1.0f, Math.max(0.0f, yRelative)));
    }

    public void setIsActive(boolean b) {
        this.active = b;
    }

    public void saveRelativePosition() {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        if (getMeasuredWidth() > 0 && this.relativePositionToSetX < 0.0f) {
            ViewParent parent = getParent();
            if (parent == null) {
                return;
            }
            float maxTop = (Build.VERSION.SDK_INT < 20 || (windowInsets2 = this.lastInsets) == null) ? 0.0f : windowInsets2.getSystemWindowInsetTop() + this.topPadding;
            float maxBottom = (Build.VERSION.SDK_INT < 20 || (windowInsets = this.lastInsets) == null) ? 0.0f : windowInsets.getSystemWindowInsetBottom() + this.bottomPadding;
            this.savedRelativePositionX = (getTranslationX() - this.leftPadding) / (((((View) parent).getMeasuredWidth() - this.leftPadding) - this.rightPadding) - getMeasuredWidth());
            this.savedRelativePositionY = (getTranslationY() - maxTop) / (((((View) parent).getMeasuredHeight() - maxBottom) - maxTop) - getMeasuredHeight());
            this.savedRelativePositionX = Math.max(0.0f, Math.min(1.0f, this.savedRelativePositionX));
            this.savedRelativePositionY = Math.max(0.0f, Math.min(1.0f, this.savedRelativePositionY));
            return;
        }
        this.savedRelativePositionX = -1.0f;
        this.savedRelativePositionY = -1.0f;
    }

    public void restoreRelativePosition() {
        updatePadding();
        float f = this.savedRelativePositionX;
        if (f >= 0.0f && !this.switchingToFloatingMode) {
            setRelativePositionInternal(f, this.savedRelativePositionY, getMeasuredWidth(), getMeasuredHeight(), true);
            this.savedRelativePositionX = -1.0f;
            this.savedRelativePositionY = -1.0f;
        }
    }
}
