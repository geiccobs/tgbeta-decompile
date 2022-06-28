package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes3.dex */
public class LinearSmoothScrollerCustom extends RecyclerView.SmoothScroller {
    private static final float MILLISECONDS_PER_INCH = 25.0f;
    public static final int POSITION_END = 1;
    public static final int POSITION_MIDDLE = 0;
    public static final int POSITION_TOP = 2;
    private static final float TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f;
    private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
    private final float MILLISECONDS_PER_PX;
    protected PointF mTargetVector;
    private int scrollPosition;
    protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    protected final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(1.5f);
    protected int mInterimTargetDx = 0;
    protected int mInterimTargetDy = 0;

    public LinearSmoothScrollerCustom(Context context, int position) {
        this.MILLISECONDS_PER_PX = MILLISECONDS_PER_INCH / context.getResources().getDisplayMetrics().densityDpi;
        this.scrollPosition = position;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onStart() {
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        int dy = calculateDyToMakeVisible(targetView);
        int time = calculateTimeForDeceleration(dy);
        if (time > 0) {
            action.update(0, -dy, Math.max(400, time), this.mDecelerateInterpolator);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, dx);
        int clampApplyScroll = clampApplyScroll(this.mInterimTargetDy, dy);
        this.mInterimTargetDy = clampApplyScroll;
        if (this.mInterimTargetDx == 0 && clampApplyScroll == 0) {
            updateActionForInterimTarget(action);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    protected void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }

    protected int calculateTimeForDeceleration(int dx) {
        double calculateTimeForScrolling = calculateTimeForScrolling(dx);
        Double.isNaN(calculateTimeForScrolling);
        return (int) Math.ceil(calculateTimeForScrolling / 0.3356d);
    }

    protected int calculateTimeForScrolling(int dx) {
        return (int) Math.ceil(Math.abs(dx) * this.MILLISECONDS_PER_PX);
    }

    protected void updateActionForInterimTarget(RecyclerView.SmoothScroller.Action action) {
        PointF scrollVector = computeScrollVectorForPosition(getTargetPosition());
        if (scrollVector == null || (scrollVector.x == 0.0f && scrollVector.y == 0.0f)) {
            int target = getTargetPosition();
            action.jumpTo(target);
            stop();
            return;
        }
        normalize(scrollVector);
        this.mTargetVector = scrollVector;
        this.mInterimTargetDx = (int) (scrollVector.x * 10000.0f);
        this.mInterimTargetDy = (int) (scrollVector.y * 10000.0f);
        int time = calculateTimeForScrolling(10000);
        action.update((int) (this.mInterimTargetDx * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (this.mInterimTargetDy * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (time * TARGET_SEEK_EXTRA_SCROLL_RATIO), this.mLinearInterpolator);
    }

    private int clampApplyScroll(int tmpDt, int dt) {
        int tmpDt2 = tmpDt - dt;
        if (tmpDt * tmpDt2 <= 0) {
            return 0;
        }
        return tmpDt2;
    }

    public int calculateDyToMakeVisible(View view) {
        int start;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null || !layoutManager.canScrollVertically()) {
            return 0;
        }
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int top = layoutManager.getDecoratedTop(view) - params.topMargin;
        int bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin;
        int start2 = layoutManager.getPaddingTop();
        int end = layoutManager.getHeight() - layoutManager.getPaddingBottom();
        int boxSize = end - start2;
        int viewSize = bottom - top;
        int i = this.scrollPosition;
        if (i == 2) {
            start = layoutManager.getPaddingTop();
        } else if (viewSize > boxSize) {
            start = 0;
        } else if (i == 0) {
            start = (boxSize - viewSize) / 2;
        } else {
            start = layoutManager.getPaddingTop() - AndroidUtilities.dp(88.0f);
        }
        int end2 = start + viewSize;
        int dtStart = start - top;
        if (dtStart > 0) {
            return dtStart;
        }
        int dtEnd = end2 - bottom;
        if (dtEnd >= 0) {
            return 0;
        }
        return dtEnd;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.SmoothScroller
    public PointF computeScrollVectorForPosition(int targetPosition) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return ((RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(targetPosition);
        }
        return null;
    }
}
