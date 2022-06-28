package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class Tooltip extends TextView {
    private View anchor;
    private ViewPropertyAnimator animator;
    Runnable dismissRunnable = new Runnable() { // from class: org.telegram.ui.Components.Tooltip$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            Tooltip.this.m3150lambda$new$0$orgtelegramuiComponentsTooltip();
        }
    };
    private boolean showing;

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Tooltip */
    public /* synthetic */ void m3150lambda$new$0$orgtelegramuiComponentsTooltip() {
        ViewPropertyAnimator duration = animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Tooltip.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Tooltip.this.setVisibility(8);
            }
        }).setDuration(300L);
        this.animator = duration;
        duration.start();
    }

    public Tooltip(Context context, ViewGroup parentView, int backgroundColor, int textColor) {
        super(context);
        setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), backgroundColor));
        setTextColor(textColor);
        setTextSize(1, 14.0f);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        setGravity(16);
        parentView.addView(this, LayoutHelper.createFrame(-2, -2.0f, 51, 5.0f, 0.0f, 5.0f, 3.0f));
        setVisibility(8);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateTooltipPosition();
    }

    private void updateTooltipPosition() {
        if (this.anchor == null) {
            return;
        }
        int top = 0;
        int left = 0;
        View containerView = (View) getParent();
        for (View view = this.anchor; view != containerView; view = (View) view.getParent()) {
            top += view.getTop();
            left += view.getLeft();
        }
        int x = ((this.anchor.getWidth() / 2) + left) - (getMeasuredWidth() / 2);
        if (x < 0) {
            x = 0;
        } else if (getMeasuredWidth() + x > containerView.getMeasuredWidth()) {
            x = (containerView.getMeasuredWidth() - getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
        }
        setTranslationX(x);
        int y = top - getMeasuredHeight();
        setTranslationY(y);
    }

    public void show(View anchor) {
        if (anchor == null) {
            return;
        }
        this.anchor = anchor;
        updateTooltipPosition();
        this.showing = true;
        AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
        AndroidUtilities.runOnUIThread(this.dismissRunnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        ViewPropertyAnimator viewPropertyAnimator = this.animator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.setListener(null);
            this.animator.cancel();
            this.animator = null;
        }
        if (getVisibility() != 0) {
            setAlpha(0.0f);
            setVisibility(0);
            ViewPropertyAnimator listener = animate().setDuration(300L).alpha(1.0f).setListener(null);
            this.animator = listener;
            listener.start();
        }
    }

    public void hide() {
        if (this.showing) {
            ViewPropertyAnimator viewPropertyAnimator = this.animator;
            if (viewPropertyAnimator != null) {
                viewPropertyAnimator.setListener(null);
                this.animator.cancel();
                this.animator = null;
            }
            AndroidUtilities.cancelRunOnUIThread(this.dismissRunnable);
            this.dismissRunnable.run();
        }
        this.showing = false;
    }
}
