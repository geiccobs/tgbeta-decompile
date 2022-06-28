package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class EmptyTextProgressView extends FrameLayout {
    private boolean inLayout;
    private RLottieImageView lottieImageView;
    private View progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    private int showAtPos;
    private TextView textView;
    private LinearLayout textViewLayout;

    public EmptyTextProgressView(Context context) {
        this(context, null, null);
    }

    public EmptyTextProgressView(Context context, View progressView) {
        this(context, progressView, null);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public EmptyTextProgressView(Context context, View progressView, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        View progressView2 = progressView;
        this.resourcesProvider = resourcesProvider;
        if (progressView2 != null) {
            addView(progressView2, LayoutHelper.createFrame(-1, -1.0f));
        } else {
            progressView2 = new RadialProgressView(context);
            addView(progressView2, LayoutHelper.createFrame(-2, -2.0f));
        }
        this.progressView = progressView2;
        LinearLayout linearLayout = new LinearLayout(context);
        this.textViewLayout = linearLayout;
        linearLayout.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.textViewLayout.setGravity(1);
        this.textViewLayout.setClipChildren(false);
        this.textViewLayout.setClipToPadding(false);
        this.textViewLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.lottieImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.lottieImageView.setImportantForAccessibility(2);
        this.lottieImageView.setVisibility(8);
        this.textViewLayout.addView(this.lottieImageView, LayoutHelper.createLinear(150, 150, 17, 0, 0, 0, 20));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 20.0f);
        this.textView.setTextColor(getThemedColor(Theme.key_emptyListPlaceholder));
        this.textView.setGravity(1);
        this.textView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.textViewLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 17));
        addView(this.textViewLayout, LayoutHelper.createFrame(-2, -2.0f));
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, false, 2.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(progressView2, false, 1.0f, false);
        setOnTouchListener(EmptyTextProgressView$$ExternalSyntheticLambda0.INSTANCE);
    }

    public static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean animated) {
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, false, 0.9f, animated);
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 1.0f, animated);
    }

    public void showTextView() {
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, true, 0.9f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 1.0f, true);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setLottie(int resource, int w, int h) {
        this.lottieImageView.setVisibility(resource != 0 ? 0 : 8);
        if (resource != 0) {
            this.lottieImageView.setAnimation(resource, w, h);
            this.lottieImageView.playAnimation();
        }
    }

    public void setProgressBarColor(int color) {
        View view = this.progressView;
        if (view instanceof RadialProgressView) {
            ((RadialProgressView) view).setProgressColor(color);
        }
    }

    public void setTopImage(int resId) {
        if (resId == 0) {
            this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        Drawable drawable = getContext().getResources().getDrawable(resId).mutate();
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_emptyListPlaceholder), PorterDuff.Mode.MULTIPLY));
        }
        this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(1.0f));
    }

    public void setTextSize(int size) {
        this.textView.setTextSize(1, size);
    }

    public void setShowAtCenter(boolean value) {
        this.showAtPos = value ? 1 : 0;
    }

    public void setShowAtTop(boolean value) {
        this.showAtPos = value ? 2 : 0;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int y;
        this.inLayout = true;
        int width = r - l;
        int height = b - t;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int x = (width - child.getMeasuredWidth()) / 2;
                View view = this.progressView;
                if (child == view && (view instanceof FlickerLoadingView)) {
                    y = ((height - child.getMeasuredHeight()) / 2) + getPaddingTop();
                } else {
                    int y2 = this.showAtPos;
                    if (y2 == 2) {
                        y = ((AndroidUtilities.dp(100.0f) - child.getMeasuredHeight()) / 2) + getPaddingTop();
                    } else if (y2 == 1) {
                        y = (((height / 2) - child.getMeasuredHeight()) / 2) + getPaddingTop();
                    } else {
                        y = ((height - child.getMeasuredHeight()) / 2) + getPaddingTop();
                    }
                }
                child.layout(x, y, child.getMeasuredWidth() + x, child.getMeasuredHeight() + y);
            }
        }
        this.inLayout = false;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
