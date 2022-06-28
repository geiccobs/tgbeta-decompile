package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ProfileGalleryView;
/* loaded from: classes4.dex */
public class AvatarPreviewPagerIndicator extends View implements ProfileGalleryView.Callback {
    private final ValueAnimator animator;
    private final Paint backgroundPaint;
    private final Paint barPaint;
    private final GradientDrawable bottomOverlayGradient;
    private float currentAnimationValue;
    private float currentLoadingAnimationProgress;
    private float currentProgress;
    private boolean isOverlaysVisible;
    private long lastTime;
    private float previousSelectedProgress;
    protected ProfileGalleryView profileGalleryView;
    private float progressToCounter;
    private final Paint selectedBarPaint;
    private int selectedPosition;
    TextPaint textPaint;
    String title;
    private final GradientDrawable topOverlayGradient;
    private final RectF indicatorRect = new RectF();
    private final int statusBarHeight = 0;
    private int overlayCountVisible = 1;
    private final Rect topOverlayRect = new Rect();
    private final Rect bottomOverlayRect = new Rect();
    private final RectF rect = new RectF();
    private final float[] animatorValues = {0.0f, 1.0f};
    Path path = new Path();
    RectF rectF = new RectF();
    private final GradientDrawable[] pressedOverlayGradient = new GradientDrawable[2];
    private final boolean[] pressedOverlayVisible = new boolean[2];
    private final float[] pressedOverlayAlpha = new float[2];
    private float alpha = 0.0f;
    private float[] alphas = null;
    private int previousSelectedPotision = -1;
    private int currentLoadingAnimationDirection = 1;
    int lastCurrentItem = -1;

    public AvatarPreviewPagerIndicator(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.barPaint = paint;
        paint.setColor(1442840575);
        Paint paint2 = new Paint(1);
        this.selectedBarPaint = paint2;
        paint2.setColor(-1);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{1107296256, 0});
        this.topOverlayGradient = gradientDrawable;
        gradientDrawable.setShape(0);
        GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{1107296256, 0});
        this.bottomOverlayGradient = gradientDrawable2;
        gradientDrawable2.setShape(0);
        int i = 0;
        while (i < 2) {
            GradientDrawable.Orientation orientation = i == 0 ? GradientDrawable.Orientation.LEFT_RIGHT : GradientDrawable.Orientation.RIGHT_LEFT;
            this.pressedOverlayGradient[i] = new GradientDrawable(orientation, new int[]{838860800, 0});
            this.pressedOverlayGradient[i].setShape(0);
            i++;
        }
        Paint paint3 = new Paint(1);
        this.backgroundPaint = paint3;
        paint3.setColor(-16777216);
        paint3.setAlpha(66);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.setDuration(250L);
        ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.AvatarPreviewPagerIndicator$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarPreviewPagerIndicator.this.m1564lambda$new$0$orgtelegramuiAvatarPreviewPagerIndicator(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.AvatarPreviewPagerIndicator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!AvatarPreviewPagerIndicator.this.isOverlaysVisible) {
                    AvatarPreviewPagerIndicator.this.setVisibility(8);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                AvatarPreviewPagerIndicator.this.setVisibility(0);
            }
        });
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setColor(-1);
        this.textPaint.setTypeface(Typeface.SANS_SERIF);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-AvatarPreviewPagerIndicator */
    public /* synthetic */ void m1564lambda$new$0$orgtelegramuiAvatarPreviewPagerIndicator(ValueAnimator anim) {
        float[] fArr = this.animatorValues;
        float animatedFraction = anim.getAnimatedFraction();
        this.currentAnimationValue = animatedFraction;
        float value = AndroidUtilities.lerp(fArr, animatedFraction);
        setAlphaValue(value, true);
    }

    public void saveCurrentPageProgress() {
        this.previousSelectedProgress = this.currentProgress;
        this.previousSelectedPotision = this.selectedPosition;
        this.currentLoadingAnimationProgress = 0.0f;
        this.currentLoadingAnimationDirection = 1;
    }

    public void setAlphaValue(float value, boolean self) {
        if (Build.VERSION.SDK_INT > 18) {
            int alpha = (int) (255.0f * value);
            this.topOverlayGradient.setAlpha(alpha);
            this.bottomOverlayGradient.setAlpha(alpha);
            this.backgroundPaint.setAlpha((int) (66.0f * value));
            this.barPaint.setAlpha((int) (85.0f * value));
            this.selectedBarPaint.setAlpha(alpha);
            this.alpha = value;
        } else {
            setAlpha(value);
        }
        if (!self) {
            this.currentAnimationValue = value;
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.path.reset();
        this.rectF.set(0.0f, 0.0f, getMeasuredHeight(), getMeasuredWidth());
        this.path.addRoundRect(this.rectF, new float[]{AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), 0.0f, 0.0f, 0.0f, 0.0f}, Path.Direction.CCW);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int actionBarHeight = ActionBar.getCurrentActionBarHeight() + 0;
        this.topOverlayRect.set(0, 0, w, (int) (actionBarHeight * 0.5f));
        this.bottomOverlayRect.set(0, (int) (h - (AndroidUtilities.dp(72.0f) * 0.5f)), w, h);
        this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, w, AndroidUtilities.dp(16.0f) + actionBarHeight);
        this.bottomOverlayGradient.setBounds(0, (h - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), w, this.bottomOverlayRect.top);
        this.pressedOverlayGradient[0].setBounds(0, 0, w / 5, h);
        this.pressedOverlayGradient[1].setBounds(w - (w / 5), 0, w, h);
    }

    /* JADX WARN: Removed duplicated region for block: B:118:0x032b  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x032e  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            Method dump skipped, instructions count: 967
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AvatarPreviewPagerIndicator.onDraw(android.graphics.Canvas):void");
    }

    private String getCurrentTitle() {
        if (this.lastCurrentItem != this.profileGalleryView.getCurrentItem()) {
            this.title = this.profileGalleryView.getAdapter().getPageTitle(this.profileGalleryView.getCurrentItem()).toString();
            this.lastCurrentItem = this.profileGalleryView.getCurrentItem();
        }
        return this.title;
    }

    @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
    public void onDown(boolean left) {
        this.pressedOverlayVisible[!left ? 1 : 0] = true;
        postInvalidateOnAnimation();
    }

    @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
    public void onRelease() {
        Arrays.fill(this.pressedOverlayVisible, false);
        postInvalidateOnAnimation();
    }

    @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
    public void onPhotosLoaded() {
    }

    @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
    public void onVideoSet() {
        invalidate();
    }

    public void setProfileGalleryView(ProfileGalleryView profileGalleryView) {
        this.profileGalleryView = profileGalleryView;
    }

    public ProfileGalleryView getProfileGalleryView() {
        return this.profileGalleryView;
    }
}
