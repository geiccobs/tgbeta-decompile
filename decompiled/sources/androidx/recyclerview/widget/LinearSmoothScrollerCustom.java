package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes.dex */
public class LinearSmoothScrollerCustom extends RecyclerView.SmoothScroller {
    private final float MILLISECONDS_PER_PX;
    private int scrollPosition;
    protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    protected final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(1.5f);
    protected int mInterimTargetDx = 0;
    protected int mInterimTargetDy = 0;

    private int clampApplyScroll(int i, int i2) {
        int i3 = i - i2;
        if (i * i3 <= 0) {
            return 0;
        }
        return i3;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onStart() {
    }

    public LinearSmoothScrollerCustom(Context context, int i) {
        this.MILLISECONDS_PER_PX = 25.0f / context.getResources().getDisplayMetrics().densityDpi;
        this.scrollPosition = i;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        int calculateDyToMakeVisible = calculateDyToMakeVisible(view);
        int calculateTimeForDeceleration = calculateTimeForDeceleration(calculateDyToMakeVisible);
        if (calculateTimeForDeceleration > 0) {
            action.update(0, -calculateDyToMakeVisible, Math.max(400, calculateTimeForDeceleration), this.mDecelerateInterpolator);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onSeekTargetStep(int i, int i2, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, i);
        int clampApplyScroll = clampApplyScroll(this.mInterimTargetDy, i2);
        this.mInterimTargetDy = clampApplyScroll;
        if (this.mInterimTargetDx != 0 || clampApplyScroll != 0) {
            return;
        }
        updateActionForInterimTarget(action);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
    }

    protected int calculateTimeForDeceleration(int i) {
        double calculateTimeForScrolling = calculateTimeForScrolling(i);
        Double.isNaN(calculateTimeForScrolling);
        return (int) Math.ceil(calculateTimeForScrolling / 0.3356d);
    }

    protected int calculateTimeForScrolling(int i) {
        return (int) Math.ceil(Math.abs(i) * this.MILLISECONDS_PER_PX);
    }

    protected void updateActionForInterimTarget(RecyclerView.SmoothScroller.Action action) {
        PointF computeScrollVectorForPosition = computeScrollVectorForPosition(getTargetPosition());
        if (computeScrollVectorForPosition == null || (computeScrollVectorForPosition.x == 0.0f && computeScrollVectorForPosition.y == 0.0f)) {
            action.jumpTo(getTargetPosition());
            stop();
            return;
        }
        normalize(computeScrollVectorForPosition);
        this.mInterimTargetDx = (int) (computeScrollVectorForPosition.x * 10000.0f);
        this.mInterimTargetDy = (int) (computeScrollVectorForPosition.y * 10000.0f);
        action.update((int) (this.mInterimTargetDx * 1.2f), (int) (this.mInterimTargetDy * 1.2f), (int) (calculateTimeForScrolling(10000) * 1.2f), this.mLinearInterpolator);
    }

    public int calculateDyToMakeVisible(View view) {
        int i;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null && layoutManager.canScrollVertically()) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            int decoratedTop = layoutManager.getDecoratedTop(view) - ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
            int decoratedBottom = layoutManager.getDecoratedBottom(view) + ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
            int height = (layoutManager.getHeight() - layoutManager.getPaddingBottom()) - layoutManager.getPaddingTop();
            int i2 = decoratedBottom - decoratedTop;
            int i3 = this.scrollPosition;
            if (i3 == 2) {
                i = layoutManager.getPaddingTop();
            } else if (i2 > height) {
                i = 0;
            } else if (i3 == 0) {
                i = (height - i2) / 2;
            } else {
                i = layoutManager.getPaddingTop() - AndroidUtilities.dp(88.0f);
            }
            int i4 = i2 + i;
            int i5 = i - decoratedTop;
            if (i5 > 0) {
                return i5;
            }
            int i6 = i4 - decoratedBottom;
            if (i6 < 0) {
                return i6;
            }
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    public PointF computeScrollVectorForPosition(int i) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return ((RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(i);
        }
        return null;
    }
}
