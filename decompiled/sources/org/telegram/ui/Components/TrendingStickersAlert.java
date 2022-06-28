package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TrendingStickersAlert;
/* loaded from: classes5.dex */
public class TrendingStickersAlert extends BottomSheet {
    private final AlertContainerView alertContainerView;
    private final TrendingStickersLayout layout;
    private int scrollOffsetY;
    private final int topOffset = AndroidUtilities.dp(12.0f);
    private final GradientDrawable shapeDrawable = new GradientDrawable();

    public TrendingStickersAlert(Context context, BaseFragment parentFragment, TrendingStickersLayout trendingStickersLayout, Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        AlertContainerView alertContainerView = new AlertContainerView(context);
        this.alertContainerView = alertContainerView;
        alertContainerView.addView(trendingStickersLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.containerView = alertContainerView;
        this.layout = trendingStickersLayout;
        trendingStickersLayout.setParentFragment(parentFragment);
        trendingStickersLayout.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.TrendingStickersAlert.1
            private int scrolledY;

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    this.scrolledY = 0;
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                this.scrolledY += dy;
                if (recyclerView.getScrollState() == 1 && Math.abs(this.scrolledY) > AndroidUtilities.dp(96.0f)) {
                    View view = TrendingStickersAlert.this.layout.findFocus();
                    if (view == null) {
                        view = TrendingStickersAlert.this.layout;
                    }
                    AndroidUtilities.hideKeyboard(view);
                }
                if (dy != 0) {
                    TrendingStickersAlert.this.updateLayout();
                }
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        setHeavyOperationsEnabled(false);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        this.layout.recycle();
        setHeavyOperationsEnabled(true);
    }

    public void setHeavyOperationsEnabled(boolean enabled) {
        NotificationCenter.getGlobalInstance().postNotificationName(enabled ? NotificationCenter.startAllHeavyOperations : NotificationCenter.stopAllHeavyOperations, 2);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public TrendingStickersLayout getLayout() {
        return this.layout;
    }

    public void updateLayout() {
        if (this.layout.update()) {
            this.scrollOffsetY = this.layout.getContentTopOffset();
            this.containerView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> descriptions = new ArrayList<>();
        final TrendingStickersLayout trendingStickersLayout = this.layout;
        trendingStickersLayout.getClass();
        trendingStickersLayout.getThemeDescriptions(descriptions, new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.TrendingStickersAlert$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                TrendingStickersLayout.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        });
        descriptions.add(new ThemeDescription(this.alertContainerView, 0, null, null, new Drawable[]{this.shadowDrawable}, null, Theme.key_dialogBackground));
        descriptions.add(new ThemeDescription(this.alertContainerView, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        return descriptions;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void setAllowNestedScroll(boolean allowNestedScroll) {
        this.allowNestedScroll = allowNestedScroll;
    }

    /* loaded from: classes5.dex */
    public class AlertContainerView extends SizeNotifierFrameLayout {
        private ValueAnimator statusBarAnimator;
        private final Paint paint = new Paint(1);
        private boolean gluedToTop = false;
        private boolean ignoreLayout = false;
        private boolean statusBarVisible = false;
        private float statusBarAlpha = 0.0f;
        private float[] radii = new float[8];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AlertContainerView(Context context) {
            super(context);
            TrendingStickersAlert.this = r4;
            setWillNotDraw(false);
            setPadding(r4.backgroundPaddingLeft, 0, r4.backgroundPaddingLeft, 0);
            setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.Components.TrendingStickersAlert.AlertContainerView.1
                private boolean lastIsWidthGreater;
                private int lastKeyboardHeight;

                @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
                public void onSizeChanged(int keyboardHeight, boolean isWidthGreater) {
                    if (this.lastKeyboardHeight != keyboardHeight || this.lastIsWidthGreater != isWidthGreater) {
                        this.lastKeyboardHeight = keyboardHeight;
                        this.lastIsWidthGreater = isWidthGreater;
                        if (keyboardHeight > AndroidUtilities.dp(20.0f) && !AlertContainerView.this.gluedToTop) {
                            TrendingStickersAlert.this.setAllowNestedScroll(false);
                            AlertContainerView.this.gluedToTop = true;
                        }
                    }
                }
            });
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TrendingStickersAlert$AlertContainerView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TrendingStickersAlert.AlertContainerView.this.requestLayout();
                }
            }, 200L);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() == 0 && TrendingStickersAlert.this.scrollOffsetY != 0 && ev.getY() < TrendingStickersAlert.this.scrollOffsetY) {
                TrendingStickersAlert.this.dismiss();
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent e) {
            return !TrendingStickersAlert.this.isDismissed() && super.onTouchEvent(e);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int statusBarHeight = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            int height = View.MeasureSpec.getSize(getMeasuredHeight()) - statusBarHeight;
            int keyboardHeight = measureKeyboardHeight();
            int padding = (int) ((height + keyboardHeight) * 0.2f);
            this.ignoreLayout = true;
            if (keyboardHeight <= AndroidUtilities.dp(20.0f)) {
                TrendingStickersAlert.this.layout.glueToTop(false);
                TrendingStickersAlert.this.setAllowNestedScroll(true);
                this.gluedToTop = false;
            } else {
                TrendingStickersAlert.this.layout.glueToTop(true);
                TrendingStickersAlert.this.setAllowNestedScroll(false);
                this.gluedToTop = true;
            }
            TrendingStickersAlert.this.layout.setContentViewPaddingTop(padding);
            if (getPaddingTop() != statusBarHeight) {
                setPadding(TrendingStickersAlert.this.backgroundPaddingLeft, statusBarHeight, TrendingStickersAlert.this.backgroundPaddingLeft, 0);
            }
            this.ignoreLayout = false;
            super.onLayout(changed, l, t, r, b);
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            TrendingStickersAlert.this.updateLayout();
            super.onDraw(canvas);
            float fraction = getFraction();
            int offset = (int) (TrendingStickersAlert.this.topOffset * (1.0f - fraction));
            int translationY = (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) - TrendingStickersAlert.this.topOffset;
            canvas.save();
            canvas.translate(0.0f, TrendingStickersAlert.this.layout.getTranslationY() + translationY);
            TrendingStickersAlert.this.shadowDrawable.setBounds(0, (TrendingStickersAlert.this.scrollOffsetY - TrendingStickersAlert.this.backgroundPaddingTop) + offset, getMeasuredWidth(), getMeasuredHeight() + (translationY < 0 ? -translationY : 0));
            TrendingStickersAlert.this.shadowDrawable.draw(canvas);
            if (fraction > 0.0f && fraction < 1.0f) {
                float radius = AndroidUtilities.dp(12.0f) * fraction;
                TrendingStickersAlert.this.shapeDrawable.setColor(TrendingStickersAlert.this.getThemedColor(Theme.key_dialogBackground));
                float[] fArr = this.radii;
                fArr[3] = radius;
                fArr[2] = radius;
                fArr[1] = radius;
                fArr[0] = radius;
                TrendingStickersAlert.this.shapeDrawable.setCornerRadii(this.radii);
                TrendingStickersAlert.this.shapeDrawable.setBounds(TrendingStickersAlert.this.backgroundPaddingLeft, TrendingStickersAlert.this.scrollOffsetY + offset, getWidth() - TrendingStickersAlert.this.backgroundPaddingLeft, TrendingStickersAlert.this.scrollOffsetY + offset + AndroidUtilities.dp(24.0f));
                TrendingStickersAlert.this.shapeDrawable.draw(canvas);
            }
            canvas.restore();
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            float fraction = getFraction();
            canvas.save();
            boolean z = false;
            canvas.translate(0.0f, (TrendingStickersAlert.this.layout.getTranslationY() + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) - TrendingStickersAlert.this.topOffset);
            int w = AndroidUtilities.dp(36.0f);
            int h = AndroidUtilities.dp(4.0f);
            int offset = (int) (h * 2.0f * (1.0f - fraction));
            TrendingStickersAlert.this.shapeDrawable.setCornerRadius(AndroidUtilities.dp(2.0f));
            int sheetScrollUpColor = TrendingStickersAlert.this.getThemedColor(Theme.key_sheet_scrollUp);
            TrendingStickersAlert.this.shapeDrawable.setColor(ColorUtils.setAlphaComponent(sheetScrollUpColor, (int) (Color.alpha(sheetScrollUpColor) * fraction)));
            TrendingStickersAlert.this.shapeDrawable.setBounds((getWidth() - w) / 2, TrendingStickersAlert.this.scrollOffsetY + AndroidUtilities.dp(10.0f) + offset, (getWidth() + w) / 2, TrendingStickersAlert.this.scrollOffsetY + AndroidUtilities.dp(10.0f) + offset + h);
            TrendingStickersAlert.this.shapeDrawable.draw(canvas);
            canvas.restore();
            if (fraction == 0.0f && Build.VERSION.SDK_INT >= 21 && !TrendingStickersAlert.this.isDismissed()) {
                z = true;
            }
            setStatusBarVisible(z, true);
            if (this.statusBarAlpha > 0.0f) {
                int color = TrendingStickersAlert.this.getThemedColor(Theme.key_dialogBackground);
                this.paint.setColor(Color.argb((int) (this.statusBarAlpha * 255.0f), (int) (Color.red(color) * 0.8f), (int) (Color.green(color) * 0.8f), (int) (Color.blue(color) * 0.8f)));
                canvas.drawRect(TrendingStickersAlert.this.backgroundPaddingLeft, 0.0f, getMeasuredWidth() - TrendingStickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, this.paint);
            }
        }

        @Override // android.view.View
        public void setTranslationY(float translationY) {
            TrendingStickersAlert.this.layout.setTranslationY(translationY);
            invalidate();
        }

        @Override // android.view.View
        public float getTranslationY() {
            return TrendingStickersAlert.this.layout.getTranslationY();
        }

        private float getFraction() {
            return Math.min(1.0f, Math.max(0.0f, TrendingStickersAlert.this.scrollOffsetY / (TrendingStickersAlert.this.topOffset * 2.0f)));
        }

        private void setStatusBarVisible(boolean visible, boolean animated) {
            if (this.statusBarVisible != visible) {
                ValueAnimator valueAnimator = this.statusBarAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.statusBarVisible = visible;
                float f = 1.0f;
                if (animated) {
                    ValueAnimator valueAnimator2 = this.statusBarAnimator;
                    if (valueAnimator2 == null) {
                        float[] fArr = new float[2];
                        fArr[0] = this.statusBarAlpha;
                        if (!visible) {
                            f = 0.0f;
                        }
                        fArr[1] = f;
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                        this.statusBarAnimator = ofFloat;
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TrendingStickersAlert$AlertContainerView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                                TrendingStickersAlert.AlertContainerView.this.m3179x25fea2ec(valueAnimator3);
                            }
                        });
                        this.statusBarAnimator.setDuration(200L);
                    } else {
                        float[] fArr2 = new float[2];
                        fArr2[0] = this.statusBarAlpha;
                        if (!visible) {
                            f = 0.0f;
                        }
                        fArr2[1] = f;
                        valueAnimator2.setFloatValues(fArr2);
                    }
                    this.statusBarAnimator.start();
                    return;
                }
                if (!visible) {
                    f = 0.0f;
                }
                this.statusBarAlpha = f;
                invalidate();
            }
        }

        /* renamed from: lambda$setStatusBarVisible$0$org-telegram-ui-Components-TrendingStickersAlert$AlertContainerView */
        public /* synthetic */ void m3179x25fea2ec(ValueAnimator a) {
            this.statusBarAlpha = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }
    }
}
