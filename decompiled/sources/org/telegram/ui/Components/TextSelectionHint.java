package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class TextSelectionHint extends View {
    Animator a;
    int animateToEnd;
    int animateToStart;
    int currentEnd;
    int currentStart;
    int end;
    float endOffsetValue;
    float enterValue;
    int lastW;
    float prepareProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean showOnMeasure;
    boolean showing;
    int start;
    float startOffsetValue;
    StaticLayout textLayout;
    TextPaint textPaint = new TextPaint(1);
    Paint selectionPaint = new Paint(1);
    int padding = AndroidUtilities.dp(24.0f);
    private Interpolator interpolator = new OvershootInterpolator();
    Runnable dismissTunnable = new Runnable() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda5
        @Override // java.lang.Runnable
        public final void run() {
            TextSelectionHint.this.hideInternal();
        }
    };
    Path path = new Path();

    public TextSelectionHint(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        int textColor = getThemedColor(Theme.key_undo_infoColor);
        int alpha = Color.alpha(textColor);
        this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
        this.textPaint.setColor(textColor);
        this.selectionPaint.setColor(textColor);
        Paint paint = this.selectionPaint;
        double d = alpha;
        Double.isNaN(d);
        paint.setAlpha((int) (d * 0.14d));
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_undo_background)));
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != this.lastW || this.textLayout == null) {
            Animator animator = this.a;
            if (animator != null) {
                animator.removeAllListeners();
                this.a.cancel();
            }
            String text = LocaleController.getString("TextSelectionHit", R.string.TextSelectionHit);
            Pattern pattern = Pattern.compile("\\*\\*.*\\*\\*");
            Matcher matcher = pattern.matcher(text);
            String word = null;
            if (matcher.matches()) {
                word = matcher.group();
            }
            String text2 = text.replace("**", "");
            this.textLayout = new StaticLayout(text2, this.textPaint, getMeasuredWidth() - (this.padding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.start = 0;
            this.end = 0;
            if (word != null) {
                this.start = text2.indexOf(word);
            }
            int i = this.start;
            if (i > 0) {
                this.end = i + word.length();
            } else {
                int k = 0;
                for (int i2 = 0; i2 < text2.length(); i2++) {
                    if (text2.charAt(i2) == ' ') {
                        k++;
                        if (k == 2) {
                            this.start = i2 + 1;
                        }
                        if (k == 3) {
                            this.end = i2 - 1;
                        }
                    }
                }
            }
            int k2 = this.end;
            if (k2 == 0) {
                this.end = text2.length();
            }
            this.animateToStart = 0;
            StaticLayout staticLayout = this.textLayout;
            int offsetForHorizontal = staticLayout.getOffsetForHorizontal(staticLayout.getLineForOffset(this.end), this.textLayout.getWidth() - 1);
            this.animateToEnd = offsetForHorizontal;
            this.currentStart = this.start;
            this.currentEnd = this.end;
            if (this.showing) {
                this.prepareProgress = 1.0f;
                this.enterValue = 1.0f;
                this.currentStart = this.animateToStart;
                this.currentEnd = offsetForHorizontal;
                this.startOffsetValue = 0.0f;
                this.endOffsetValue = 0.0f;
            } else if (this.showOnMeasure) {
                show();
            }
            this.showOnMeasure = false;
            this.lastW = getMeasuredWidth();
        }
        int h = this.textLayout.getHeight() + (AndroidUtilities.dp(8.0f) * 2);
        if (h < AndroidUtilities.dp(56.0f)) {
            h = AndroidUtilities.dp(56.0f);
        }
        setMeasuredDimension(getMeasuredWidth(), h);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int topPadding;
        float enterProgress;
        if (this.textLayout == null) {
            return;
        }
        super.onDraw(canvas);
        canvas.save();
        int topPadding2 = (getMeasuredHeight() - this.textLayout.getHeight()) >> 1;
        canvas.translate(this.padding, topPadding2);
        if (this.enterValue != 0.0f) {
            drawSelection(canvas, this.textLayout, this.currentStart, this.currentEnd);
        }
        this.textLayout.draw(canvas);
        int handleViewSize = AndroidUtilities.dp(14.0f);
        int line = this.textLayout.getLineForOffset(this.currentEnd);
        this.textLayout.getPrimaryHorizontal(this.currentEnd);
        int y = this.textLayout.getLineBottom(line);
        int i = this.currentEnd;
        int i2 = this.animateToEnd;
        if (i == i2) {
            topPadding = y;
            roundedRect(this.path, this.textLayout.getPrimaryHorizontal(i2), this.textLayout.getLineTop(line), this.textLayout.getPrimaryHorizontal(this.animateToEnd) + AndroidUtilities.dpf2(4.0f), this.textLayout.getLineBottom(line), AndroidUtilities.dpf2(4.0f), AndroidUtilities.dpf2(4.0f), false, true);
            canvas.drawPath(this.path, this.selectionPaint);
        } else {
            topPadding = y;
        }
        float enterProgress2 = this.interpolator.getInterpolation(this.enterValue);
        int xOffset = (int) (this.textLayout.getPrimaryHorizontal(this.animateToEnd) + (AndroidUtilities.dpf2(4.0f) * (1.0f - this.endOffsetValue)) + ((this.textLayout.getPrimaryHorizontal(this.end) - this.textLayout.getPrimaryHorizontal(this.animateToEnd)) * this.endOffsetValue));
        canvas.save();
        canvas.translate(xOffset, topPadding);
        canvas.scale(enterProgress2, enterProgress2, handleViewSize / 2.0f, handleViewSize / 2.0f);
        this.path.reset();
        this.path.addCircle(handleViewSize / 2.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
        this.path.addRect(0.0f, 0.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
        canvas.drawPath(this.path, this.textPaint);
        canvas.restore();
        int line2 = this.textLayout.getLineForOffset(this.currentStart);
        this.textLayout.getPrimaryHorizontal(this.currentStart);
        int y2 = this.textLayout.getLineBottom(line2);
        if (this.currentStart == this.animateToStart) {
            enterProgress = enterProgress2;
            roundedRect(this.path, -AndroidUtilities.dp(4.0f), this.textLayout.getLineTop(line2), 0.0f, this.textLayout.getLineBottom(line2), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), true, false);
            canvas.drawPath(this.path, this.selectionPaint);
        } else {
            enterProgress = enterProgress2;
        }
        canvas.save();
        int xOffset2 = (int) ((this.textLayout.getPrimaryHorizontal(this.animateToStart) - (AndroidUtilities.dp(4.0f) * (1.0f - this.startOffsetValue))) + ((this.textLayout.getPrimaryHorizontal(this.start) - this.textLayout.getPrimaryHorizontal(this.animateToStart)) * this.startOffsetValue));
        canvas.translate(xOffset2 - handleViewSize, y2);
        canvas.scale(enterProgress, enterProgress, handleViewSize / 2.0f, handleViewSize / 2.0f);
        this.path.reset();
        this.path.addCircle(handleViewSize / 2.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
        this.path.addRect(handleViewSize / 2.0f, 0.0f, handleViewSize, handleViewSize / 2.0f, Path.Direction.CCW);
        canvas.drawPath(this.path, this.textPaint);
        canvas.restore();
        canvas.restore();
    }

    private void roundedRect(Path path, float left, float top, float right, float bottom, float rx, float ry, boolean tl, boolean tr) {
        path.reset();
        float rx2 = rx < 0.0f ? 0.0f : rx;
        float ry2 = ry < 0.0f ? 0.0f : ry;
        float width = right - left;
        float height = bottom - top;
        if (rx2 > width / 2.0f) {
            rx2 = width / 2.0f;
        }
        if (ry2 > height / 2.0f) {
            ry2 = height / 2.0f;
        }
        float widthMinusCorners = width - (rx2 * 2.0f);
        float heightMinusCorners = height - (2.0f * ry2);
        path.moveTo(right, top + ry2);
        if (tr) {
            path.rQuadTo(0.0f, -ry2, -rx2, -ry2);
        } else {
            path.rLineTo(0.0f, -ry2);
            path.rLineTo(-rx2, 0.0f);
        }
        path.rLineTo(-widthMinusCorners, 0.0f);
        if (tl) {
            path.rQuadTo(-rx2, 0.0f, -rx2, ry2);
        } else {
            path.rLineTo(-rx2, 0.0f);
            path.rLineTo(0.0f, ry2);
        }
        path.rLineTo(0.0f, heightMinusCorners);
        path.rLineTo(0.0f, ry2);
        path.rLineTo(rx2, 0.0f);
        path.rLineTo(widthMinusCorners, 0.0f);
        path.rLineTo(rx2, 0.0f);
        path.rLineTo(0.0f, -ry2);
        path.rLineTo(0.0f, -heightMinusCorners);
        path.close();
    }

    private void drawSelection(Canvas canvas, StaticLayout layout, int selectionStart, int selectionEnd) {
        int startLine = layout.getLineForOffset(selectionStart);
        int endLine = layout.getLineForOffset(selectionEnd);
        int startX = (int) layout.getPrimaryHorizontal(selectionStart);
        int endX = (int) layout.getPrimaryHorizontal(selectionEnd);
        if (startLine == endLine) {
            canvas.drawRect(startX, layout.getLineTop(startLine), endX, layout.getLineBottom(startLine), this.selectionPaint);
            return;
        }
        canvas.drawRect(startX, layout.getLineTop(startLine), layout.getLineWidth(startLine), layout.getLineBottom(startLine), this.selectionPaint);
        canvas.drawRect(0.0f, layout.getLineTop(endLine), endX, layout.getLineBottom(endLine), this.selectionPaint);
        for (int i = startLine + 1; i < endLine; i++) {
            canvas.drawRect(0.0f, layout.getLineTop(i), layout.getLineWidth(i), layout.getLineBottom(i), this.selectionPaint);
        }
    }

    public void show() {
        AndroidUtilities.cancelRunOnUIThread(this.dismissTunnable);
        Animator animator = this.a;
        if (animator != null) {
            animator.removeAllListeners();
            this.a.cancel();
        }
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            this.showOnMeasure = true;
            return;
        }
        this.showing = true;
        setVisibility(0);
        this.prepareProgress = 0.0f;
        this.enterValue = 0.0f;
        this.currentStart = this.start;
        this.currentEnd = this.end;
        this.startOffsetValue = 1.0f;
        this.endOffsetValue = 1.0f;
        invalidate();
        ValueAnimator prepareAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        prepareAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.m3129lambda$show$0$orgtelegramuiComponentsTextSelectionHint(valueAnimator);
            }
        });
        prepareAnimation.setDuration(210L);
        prepareAnimation.setInterpolator(new DecelerateInterpolator());
        ValueAnimator enterAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        enterAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.m3130lambda$show$1$orgtelegramuiComponentsTextSelectionHint(valueAnimator);
            }
        });
        enterAnimation.setStartDelay(600L);
        enterAnimation.setDuration(250L);
        ValueAnimator moveStart = ValueAnimator.ofFloat(1.0f, 0.0f);
        moveStart.setStartDelay(500L);
        moveStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.m3131lambda$show$2$orgtelegramuiComponentsTextSelectionHint(valueAnimator);
            }
        });
        moveStart.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        moveStart.setDuration(500L);
        ValueAnimator moveEnd = ValueAnimator.ofFloat(1.0f, 0.0f);
        moveEnd.setStartDelay(400L);
        moveEnd.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.m3132lambda$show$3$orgtelegramuiComponentsTextSelectionHint(valueAnimator);
            }
        });
        moveEnd.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        moveEnd.setDuration(900L);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(prepareAnimation, enterAnimation, moveStart, moveEnd);
        this.a = set;
        set.start();
        AndroidUtilities.runOnUIThread(this.dismissTunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    /* renamed from: lambda$show$0$org-telegram-ui-Components-TextSelectionHint */
    public /* synthetic */ void m3129lambda$show$0$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        this.prepareProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$show$1$org-telegram-ui-Components-TextSelectionHint */
    public /* synthetic */ void m3130lambda$show$1$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        this.enterValue = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$show$2$org-telegram-ui-Components-TextSelectionHint */
    public /* synthetic */ void m3131lambda$show$2$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
        this.startOffsetValue = floatValue;
        int i = this.animateToStart;
        this.currentStart = (int) (i + ((this.start - i) * floatValue));
        invalidate();
    }

    /* renamed from: lambda$show$3$org-telegram-ui-Components-TextSelectionHint */
    public /* synthetic */ void m3132lambda$show$3$orgtelegramuiComponentsTextSelectionHint(ValueAnimator animation) {
        float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
        this.endOffsetValue = floatValue;
        int i = this.animateToEnd;
        this.currentEnd = i + ((int) Math.ceil((this.end - i) * floatValue));
        invalidate();
    }

    public void hide() {
        AndroidUtilities.cancelRunOnUIThread(this.dismissTunnable);
        hideInternal();
    }

    public void hideInternal() {
        Animator animator = this.a;
        if (animator != null) {
            animator.removeAllListeners();
            this.a.cancel();
        }
        this.showing = false;
        ValueAnimator animator2 = ValueAnimator.ofFloat(this.prepareProgress, 0.0f);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TextSelectionHint$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TextSelectionHint.this.m3128x3af79fdd(valueAnimator);
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TextSelectionHint.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                TextSelectionHint.this.setVisibility(4);
            }
        });
        this.a = animator2;
        animator2.start();
    }

    /* renamed from: lambda$hideInternal$4$org-telegram-ui-Components-TextSelectionHint */
    public /* synthetic */ void m3128x3af79fdd(ValueAnimator animation) {
        this.prepareProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getPrepareProgress() {
        return this.prepareProgress;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
