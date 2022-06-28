package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitPreviewView;
import org.telegram.ui.Components.Premium.PremiumGradient;
/* loaded from: classes5.dex */
public class LimitPreviewView extends LinearLayout {
    boolean animationCanPlay = true;
    TextView defaultCount;
    public int gradientTotalHeight;
    int gradientYOffset;
    int icon;
    boolean inc;
    CounterView limitIcon;
    LinearLayout limitsContainer;
    private View parentVideForGradient;
    private float position;
    TextView premiumCount;
    private boolean premiumLocked;
    float progress;
    PremiumGradient.GradientTools staticGradient;
    boolean wasAnimation;
    boolean wasHaptic;

    public LimitPreviewView(Context context, int icon, int currentValue, int premiumLimit) {
        super(context);
        this.icon = icon;
        setOrientation(1);
        setClipChildren(false);
        setClipToPadding(false);
        if (icon != 0) {
            setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
            this.limitIcon = new CounterView(context);
            setIconValue(currentValue);
            this.limitIcon.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(14.0f));
            addView(this.limitIcon, LayoutHelper.createLinear(-2, -2, 0.0f, 3));
        }
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.Premium.LimitPreviewView.1
            Paint grayPaint = new Paint();

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                this.grayPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.grayPaint);
                canvas.save();
                canvas.clipRect(getMeasuredWidth() / 2.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                Paint paint = PremiumGradient.getInstance().getMainGradientPaint();
                if (LimitPreviewView.this.parentVideForGradient != null) {
                    View parent = LimitPreviewView.this.parentVideForGradient;
                    if (LimitPreviewView.this.staticGradient != null) {
                        paint = LimitPreviewView.this.staticGradient.paint;
                        LimitPreviewView.this.staticGradient.gradientMatrixLinear(LimitPreviewView.this.gradientTotalHeight, -LimitPreviewView.this.gradientYOffset);
                    } else {
                        float y = 0.0f;
                        for (View child = this; child != parent; child = (View) child.getParent()) {
                            y += child.getY();
                        }
                        PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, parent.getMeasuredWidth(), parent.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getLeft(), -y);
                    }
                } else {
                    PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getLeft(), -getTop());
                }
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint);
                canvas.restore();
                if (LimitPreviewView.this.staticGradient == null) {
                    invalidate();
                }
                super.dispatchDraw(canvas);
            }
        };
        this.limitsContainer = linearLayout;
        linearLayout.setOrientation(0);
        FrameLayout limitLayout = new FrameLayout(context);
        TextView freeTextView = new TextView(context);
        freeTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        freeTextView.setText(LocaleController.getString("LimitFree", R.string.LimitFree));
        freeTextView.setGravity(16);
        freeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        freeTextView.setPadding(AndroidUtilities.dp(12.0f), 0, 0, 0);
        TextView textView = new TextView(context);
        this.defaultCount = textView;
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.defaultCount.setText(Integer.toString(premiumLimit));
        this.defaultCount.setGravity(16);
        this.defaultCount.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        limitLayout.addView(freeTextView, LayoutHelper.createFrame(-1, 30.0f, 3, 0.0f, 0.0f, 36.0f, 0.0f));
        limitLayout.addView(this.defaultCount, LayoutHelper.createFrame(-2, 30.0f, 5, 0.0f, 0.0f, 12.0f, 0.0f));
        this.limitsContainer.addView(limitLayout, LayoutHelper.createLinear(0, 30, 1.0f));
        FrameLayout limitLayout2 = new FrameLayout(context);
        TextView limitTextView = new TextView(context);
        limitTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        limitTextView.setText(LocaleController.getString("LimitPremium", R.string.LimitPremium));
        limitTextView.setGravity(16);
        limitTextView.setTextColor(-1);
        limitTextView.setPadding(AndroidUtilities.dp(12.0f), 0, 0, 0);
        TextView textView2 = new TextView(context);
        this.premiumCount = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.premiumCount.setText(Integer.toString(premiumLimit));
        this.premiumCount.setGravity(16);
        this.premiumCount.setTextColor(-1);
        limitLayout2.addView(limitTextView, LayoutHelper.createFrame(-1, 30.0f, 3, 0.0f, 0.0f, 36.0f, 0.0f));
        limitLayout2.addView(this.premiumCount, LayoutHelper.createFrame(-2, 30.0f, 5, 0.0f, 0.0f, 12.0f, 0.0f));
        this.limitsContainer.addView(limitLayout2, LayoutHelper.createLinear(0, 30, 1.0f));
        addView(this.limitsContainer, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 14, icon == 0 ? 0 : 12, 14, 0));
    }

    public void setIconValue(int currentValue) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "d ").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
        spannableStringBuilder.append((CharSequence) Integer.toString(currentValue));
        this.limitIcon.setText(spannableStringBuilder);
    }

    public float getGlobalXOffset() {
        return (((-getMeasuredWidth()) * 0.1f) * this.progress) - (getMeasuredWidth() * 0.2f);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.staticGradient == null) {
            if (this.inc) {
                float f = this.progress + 0.016f;
                this.progress = f;
                if (f > 3.0f) {
                    this.inc = false;
                }
            } else {
                float f2 = this.progress - 0.016f;
                this.progress = f2;
                if (f2 < 1.0f) {
                    this.inc = true;
                }
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int padding;
        float toProgressCenter;
        float toX;
        CounterView counterView;
        CounterView counterView2;
        super.onLayout(changed, l, t, r, b);
        if (this.wasAnimation || this.limitIcon == null || !this.animationCanPlay || this.premiumLocked) {
            if (this.premiumLocked) {
                float toX2 = (AndroidUtilities.dp(14.0f) + ((getMeasuredWidth() - (padding * 2)) * 0.5f)) - (this.limitIcon.getMeasuredWidth() / 2.0f);
                boolean z = this.wasAnimation;
                if (!z && this.animationCanPlay) {
                    this.wasAnimation = true;
                    this.limitIcon.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200L).setInterpolator(new OvershootInterpolator()).start();
                } else if (!z) {
                    this.limitIcon.setAlpha(0.0f);
                    this.limitIcon.setScaleX(0.0f);
                    this.limitIcon.setScaleY(0.0f);
                } else {
                    this.limitIcon.setAlpha(1.0f);
                    this.limitIcon.setScaleX(1.0f);
                    this.limitIcon.setScaleY(1.0f);
                }
                this.limitIcon.setTranslationX(toX2);
                return;
            }
            CounterView counterView3 = this.limitIcon;
            if (counterView3 != null) {
                counterView3.setAlpha(0.0f);
                return;
            }
            return;
        }
        int padding2 = AndroidUtilities.dp(14.0f);
        float toX3 = (padding2 + ((getMeasuredWidth() - (padding2 * 2)) * this.position)) - (this.limitIcon.getMeasuredWidth() / 2.0f);
        if (toX3 > (getMeasuredWidth() - padding2) - this.limitIcon.getMeasuredWidth()) {
            float toX4 = (getMeasuredWidth() - padding2) - this.limitIcon.getMeasuredWidth();
            toX = toX4;
            toProgressCenter = 1.0f;
        } else {
            toX = toX3;
            toProgressCenter = 0.5f;
        }
        this.limitIcon.setAlpha(1.0f);
        this.limitIcon.setTranslationX(0.0f);
        this.limitIcon.setPivotX(counterView.getMeasuredWidth() / 2.0f);
        this.limitIcon.setPivotY(counterView2.getMeasuredHeight());
        this.limitIcon.setScaleX(0.0f);
        this.limitIcon.setScaleY(0.0f);
        this.limitIcon.createAnimationLayouts();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        final float finalToX = toX;
        final float finalToProgressCenter = toProgressCenter;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.LimitPreviewView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LimitPreviewView.this.m2887xfbb0c7b7(r2, finalToX, r4, finalToProgressCenter, valueAnimator2);
            }
        });
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.setDuration(1000L);
        valueAnimator.setStartDelay(200L);
        valueAnimator.start();
        this.wasAnimation = true;
    }

    /* renamed from: lambda$onLayout$0$org-telegram-ui-Components-Premium-LimitPreviewView */
    public /* synthetic */ void m2887xfbb0c7b7(float fromX, float finalToX, float fromProgressCenter, float finalToProgressCenter, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        float moveValue = Math.min(1.0f, v);
        if (v > 1.0f) {
            if (!this.wasHaptic) {
                this.wasHaptic = true;
                this.limitIcon.performHapticFeedback(3);
            }
            this.limitIcon.setRotation((v - 1.0f) * 60.0f);
        } else {
            this.limitIcon.setRotation(0.0f);
        }
        this.limitIcon.setTranslationX(((1.0f - moveValue) * fromX) + (finalToX * moveValue));
        float arrowCenter = ((1.0f - moveValue) * fromProgressCenter) + (finalToProgressCenter * moveValue);
        this.limitIcon.setArrowCenter(arrowCenter);
        float scale = Math.min(1.0f, 2.0f * moveValue);
        this.limitIcon.setScaleX(scale);
        this.limitIcon.setScaleY(scale);
        CounterView counterView = this.limitIcon;
        counterView.setPivotX(counterView.getMeasuredWidth() * arrowCenter);
    }

    public void setType(int type) {
        if (type == 6) {
            if (this.limitIcon != null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append((CharSequence) "d ").setSpan(new ColoredImageSpan(this.icon), 0, 1, 0);
                spannableStringBuilder.append((CharSequence) (UserConfig.getInstance(UserConfig.selectedAccount).isPremium() ? "4 GB" : "2 GB"));
                this.limitIcon.setText(spannableStringBuilder);
            }
            this.premiumCount.setText("4 GB");
        }
    }

    public void setBagePosition(float position) {
        this.position = position;
    }

    public void setParentViewForGradien(ViewGroup containerView) {
        this.parentVideForGradient = containerView;
    }

    public void setStaticGradinet(PremiumGradient.GradientTools gradientTools) {
        this.staticGradient = gradientTools;
    }

    public void setDelayedAnimation() {
        this.animationCanPlay = false;
    }

    public void startDelayedAnimation() {
        this.animationCanPlay = true;
        requestLayout();
    }

    public void setPremiumLocked() {
        this.limitsContainer.setVisibility(8);
        this.limitIcon.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f));
        this.premiumLocked = true;
    }

    /* loaded from: classes5.dex */
    private class LimitTextView extends LinearLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LimitTextView(Context context) {
            super(context);
            LimitPreviewView.this = r1;
        }
    }

    /* loaded from: classes5.dex */
    public class CounterView extends View {
        StaticLayout animatedStableLayout;
        boolean animationInProgress;
        float arrowCenter;
        boolean invalidatePath;
        CharSequence text;
        StaticLayout textLayout;
        float textWidth;
        Path path = new Path();
        PathEffect pathEffect = new CornerPathEffect(AndroidUtilities.dp(6.0f));
        TextPaint textPaint = new TextPaint(1);
        ArrayList<AnimatedLayout> animatedLayouts = new ArrayList<>();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CounterView(Context context) {
            super(context);
            LimitPreviewView.this = r2;
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textPaint.setTextSize(AndroidUtilities.dp(22.0f));
            this.textPaint.setColor(-1);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            TextPaint textPaint = this.textPaint;
            CharSequence charSequence = this.text;
            this.textWidth = textPaint.measureText(charSequence, 0, charSequence.length());
            this.textLayout = new StaticLayout(this.text, this.textPaint, AndroidUtilities.dp(12.0f) + ((int) this.textWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            setMeasuredDimension((int) (this.textWidth + getPaddingRight() + getPaddingLeft()), AndroidUtilities.dp(44.0f) + AndroidUtilities.dp(8.0f));
            updatePath();
        }

        private void updatePath() {
            int h = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            float widthHalf = getMeasuredWidth() * this.arrowCenter;
            float x2 = Utilities.clamp(AndroidUtilities.dp(8.0f) + widthHalf, getMeasuredWidth(), 0.0f);
            float x3 = Utilities.clamp(AndroidUtilities.dp(10.0f) + widthHalf, getMeasuredWidth(), 0.0f);
            this.path.rewind();
            this.path.moveTo(widthHalf - AndroidUtilities.dp(24.0f), (h - (h / 2.0f)) - AndroidUtilities.dp(2.0f));
            this.path.lineTo(widthHalf - AndroidUtilities.dp(24.0f), h);
            this.path.lineTo(widthHalf - AndroidUtilities.dp(8.0f), h);
            this.path.lineTo(widthHalf, AndroidUtilities.dp(8.0f) + h);
            if (this.arrowCenter < 0.7f) {
                this.path.lineTo(x2, h);
            }
            this.path.lineTo(x3, h);
            this.path.lineTo(x3, (h - (h / 2.0f)) - AndroidUtilities.dp(2.0f));
            this.path.close();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int h = getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            if (LimitPreviewView.this.premiumLocked) {
                h = getMeasuredHeight();
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getX(), -getTop());
                AndroidUtilities.rectTmp.set(0.0f, AndroidUtilities.dp(3.0f), getMeasuredWidth(), h - AndroidUtilities.dp(3.0f));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, h / 2.0f, h / 2.0f, PremiumGradient.getInstance().getMainGradientPaint());
            } else {
                if (this.invalidatePath) {
                    this.invalidatePath = false;
                    updatePath();
                }
                PremiumGradient.getInstance().updateMainGradientMatrix(0, 0, LimitPreviewView.this.getMeasuredWidth(), LimitPreviewView.this.getMeasuredHeight(), LimitPreviewView.this.getGlobalXOffset() - getX(), -getTop());
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), h);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, h / 2.0f, h / 2.0f, PremiumGradient.getInstance().getMainGradientPaint());
                PremiumGradient.getInstance().getMainGradientPaint().setPathEffect(this.pathEffect);
                canvas.drawPath(this.path, PremiumGradient.getInstance().getMainGradientPaint());
                PremiumGradient.getInstance().getMainGradientPaint().setPathEffect(null);
                invalidate();
            }
            float x = (getMeasuredWidth() - this.textLayout.getWidth()) / 2.0f;
            float y = (h - this.textLayout.getHeight()) / 2.0f;
            if (!this.animationInProgress) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate(x, y);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                    return;
                }
                return;
            }
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(8.0f));
            if (this.animatedStableLayout != null) {
                canvas.save();
                canvas.translate(x, y);
                this.animatedStableLayout.draw(canvas);
                canvas.restore();
            }
            for (int i = 0; i < this.animatedLayouts.size(); i++) {
                AnimatedLayout animatedLayout = this.animatedLayouts.get(i);
                canvas.save();
                if (animatedLayout.direction) {
                    canvas.translate(animatedLayout.x + x, (y - ((h * 10) * animatedLayout.progress)) + ((10 - animatedLayout.staticLayouts.size()) * h));
                    for (int j = 0; j < animatedLayout.staticLayouts.size(); j++) {
                        canvas.translate(0.0f, h);
                        animatedLayout.staticLayouts.get(j).draw(canvas);
                    }
                } else {
                    canvas.translate(animatedLayout.x + x, (((h * 10) * animatedLayout.progress) + y) - ((10 - animatedLayout.staticLayouts.size()) * h));
                    for (int j2 = 0; j2 < animatedLayout.staticLayouts.size(); j2++) {
                        canvas.translate(0.0f, -h);
                        animatedLayout.staticLayouts.get(j2).draw(canvas);
                    }
                }
                canvas.restore();
            }
            canvas.restore();
        }

        @Override // android.view.View
        public void setTranslationX(float translationX) {
            if (translationX != getTranslationX()) {
                super.setTranslationX(translationX);
                invalidate();
            }
        }

        void createAnimationLayouts() {
            this.animatedLayouts.clear();
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.text);
            boolean direction = true;
            int directionCount = 0;
            for (int i = 0; i < this.text.length(); i++) {
                if (Character.isDigit(this.text.charAt(i))) {
                    AnimatedLayout animatedLayout = new AnimatedLayout();
                    this.animatedLayouts.add(animatedLayout);
                    animatedLayout.x = this.textLayout.getSecondaryHorizontal(i);
                    animatedLayout.direction = direction;
                    if (directionCount >= 1) {
                        direction = !direction;
                        directionCount = 0;
                    }
                    directionCount++;
                    int digit = this.text.charAt(i) - '0';
                    if (digit == 0) {
                        digit = 10;
                    }
                    for (int j = 1; j <= digit; j++) {
                        int k = j;
                        if (k == 10) {
                            k = 0;
                        }
                        String str = "" + k;
                        StaticLayout staticLayout = new StaticLayout(str, this.textPaint, (int) this.textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        animatedLayout.staticLayouts.add(staticLayout);
                    }
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), i, i + 1, 0);
                }
            }
            this.animatedStableLayout = new StaticLayout(spannableStringBuilder, this.textPaint, ((int) this.textWidth) + AndroidUtilities.dp(12.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            for (int i2 = 0; i2 < this.animatedLayouts.size(); i2++) {
                this.animationInProgress = true;
                final AnimatedLayout layout = this.animatedLayouts.get(i2);
                layout.valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                layout.valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.LimitPreviewView$CounterView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LimitPreviewView.CounterView.this.m2888x1ef3fe0c(layout, valueAnimator);
                    }
                });
                layout.valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.LimitPreviewView.CounterView.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        layout.valueAnimator = null;
                        CounterView.this.checkAnimationComplete();
                    }
                });
                layout.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                layout.valueAnimator.setDuration(750L);
                layout.valueAnimator.setStartDelay(((this.animatedLayouts.size() - 1) - i2) * 60);
                layout.valueAnimator.start();
            }
        }

        /* renamed from: lambda$createAnimationLayouts$0$org-telegram-ui-Components-Premium-LimitPreviewView$CounterView */
        public /* synthetic */ void m2888x1ef3fe0c(AnimatedLayout layout, ValueAnimator animation) {
            layout.progress = ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }

        public void checkAnimationComplete() {
            for (int i = 0; i < this.animatedLayouts.size(); i++) {
                if (this.animatedLayouts.get(i).valueAnimator != null) {
                    return;
                }
            }
            this.animatedLayouts.clear();
            this.animationInProgress = false;
            invalidate();
        }

        public void setText(CharSequence text) {
            this.text = text;
        }

        public void setArrowCenter(float v) {
            if (this.arrowCenter != v) {
                this.arrowCenter = v;
                this.invalidatePath = true;
                invalidate();
            }
        }

        /* loaded from: classes5.dex */
        public class AnimatedLayout {
            public boolean direction;
            float progress;
            ArrayList<StaticLayout> staticLayouts;
            ValueAnimator valueAnimator;
            float x;

            private AnimatedLayout() {
                CounterView.this = r1;
                this.staticLayouts = new ArrayList<>();
            }
        }
    }
}
