package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class GridLayoutManagerFixed extends GridLayoutManager {
    private ArrayList<View> additionalViews = new ArrayList<>(4);
    private boolean canScrollVertically = true;

    public GridLayoutManagerFixed(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerFixed(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    protected boolean hasSiblingChild(int position) {
        return false;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager
    protected void recycleViewsFromStart(RecyclerView.Recycler recycler, int scrollingOffset, int noRecycleSpace) {
        if (scrollingOffset < 0) {
            return;
        }
        int childCount = getChildCount();
        if (this.mShouldReverseLayout) {
            for (int i = childCount - 1; i >= 0; i--) {
                View child = getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                if (child.getBottom() + params.bottomMargin > scrollingOffset || child.getTop() + child.getHeight() > scrollingOffset) {
                    recycleChildren(recycler, childCount - 1, i);
                    return;
                }
            }
            return;
        }
        for (int i2 = 0; i2 < childCount; i2++) {
            View child2 = getChildAt(i2);
            if (this.mOrientationHelper.getDecoratedEnd(child2) > scrollingOffset || this.mOrientationHelper.getTransformedEndWithDecoration(child2) > scrollingOffset) {
                recycleChildren(recycler, 0, i2);
                return;
            }
        }
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager
    protected int[] calculateItemBorders(int[] cachedBorders, int spanCount, int totalSpace) {
        if (cachedBorders == null || cachedBorders.length != spanCount + 1 || cachedBorders[cachedBorders.length - 1] != totalSpace) {
            cachedBorders = new int[spanCount + 1];
        }
        cachedBorders[0] = 0;
        for (int i = 1; i <= spanCount; i++) {
            cachedBorders[i] = (int) Math.ceil((i / spanCount) * totalSpace);
        }
        return cachedBorders;
    }

    public boolean shouldLayoutChildFromOpositeSide(View child) {
        return false;
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager
    public void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        Rect decorInsets = lp.mDecorInsets;
        int verticalInsets = decorInsets.top + decorInsets.bottom + lp.topMargin + lp.bottomMargin;
        int horizontalInsets = decorInsets.left + decorInsets.right + lp.leftMargin + lp.rightMargin;
        int availableSpaceInOther = this.mCachedBorders[lp.mSpanSize];
        int wSpec = getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode, horizontalInsets, lp.width, false);
        int hSpec = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), verticalInsets, lp.height, true);
        measureChildWithDecorationsAndMargin(view, wSpec, hSpec, alreadyMeasured);
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager
    void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state, LinearLayoutManager.LayoutState layoutState, LinearLayoutManager.LayoutChunkResult result) {
        int maxSize;
        int top;
        int bottom;
        int left;
        int left2;
        int top2;
        int bottom2;
        int left3;
        int left4;
        int firstPositionStart;
        int otherDirSpecMode;
        int startPosition;
        int pos;
        int spanSize;
        View view;
        RecyclerView.Recycler recycler2 = recycler;
        RecyclerView.State state2 = state;
        int otherDirSpecMode2 = this.mOrientationHelper.getModeInOther();
        boolean z = false;
        boolean z2 = true;
        boolean layingOutInPrimaryDirection = layoutState.mItemDirection == 1;
        boolean working = true;
        result.mConsumed = 0;
        int startPosition2 = layoutState.mCurrentPosition;
        if (layoutState.mLayoutDirection != -1 && hasSiblingChild(layoutState.mCurrentPosition) && findViewByPosition(layoutState.mCurrentPosition + 1) == null) {
            if (hasSiblingChild(layoutState.mCurrentPosition + 1)) {
                layoutState.mCurrentPosition += 3;
            } else {
                layoutState.mCurrentPosition += 2;
            }
            int backupPosition = layoutState.mCurrentPosition;
            int a = layoutState.mCurrentPosition;
            while (a > startPosition2) {
                View view2 = layoutState.next(recycler2);
                if (view2 != null) {
                    this.additionalViews.add(view2);
                    if (a != backupPosition) {
                        calculateItemDecorationsForChild(view2, this.mDecorInsets);
                        measureChild(view2, otherDirSpecMode2, z);
                        int size = this.mOrientationHelper.getDecoratedMeasurement(view2);
                        layoutState.mOffset -= size;
                        layoutState.mAvailable += size;
                    }
                }
                a--;
                z = false;
            }
            layoutState.mCurrentPosition = backupPosition;
        }
        while (working) {
            int remainingSpan = this.mSpanCount;
            boolean working2 = this.additionalViews.isEmpty() ^ z2;
            int firstPositionStart2 = layoutState.mCurrentPosition;
            boolean working3 = working2;
            int count = 0;
            int consumedSpanCount = 0;
            while (count < this.mSpanCount && layoutState.hasMore(state2) && remainingSpan > 0 && (remainingSpan = remainingSpan - (spanSize = getSpanSize(recycler2, state2, (pos = layoutState.mCurrentPosition)))) >= 0) {
                if (!this.additionalViews.isEmpty()) {
                    View view3 = this.additionalViews.get(0);
                    this.additionalViews.remove(0);
                    layoutState.mCurrentPosition--;
                    view = view3;
                } else {
                    view = layoutState.next(recycler2);
                }
                if (view == null) {
                    break;
                }
                consumedSpanCount += spanSize;
                this.mSet[count] = view;
                count++;
                if (layoutState.mLayoutDirection == -1 && remainingSpan <= 0 && hasSiblingChild(pos)) {
                    working3 = true;
                }
            }
            if (count == 0) {
                result.mFinished = true;
                return;
            }
            assignSpans(recycler2, state2, count, layingOutInPrimaryDirection);
            int i = 0;
            int maxSize2 = 0;
            float maxSizeInOther = 0.0f;
            while (i < count) {
                View view4 = this.mSet[i];
                if (layoutState.mScrapList == null) {
                    if (!layingOutInPrimaryDirection) {
                        addView(view4, 0);
                    } else {
                        addView(view4);
                    }
                } else if (layingOutInPrimaryDirection) {
                    addDisappearingView(view4);
                } else {
                    addDisappearingView(view4, 0);
                }
                calculateItemDecorationsForChild(view4, this.mDecorInsets);
                measureChild(view4, otherDirSpecMode2, false);
                int size2 = this.mOrientationHelper.getDecoratedMeasurement(view4);
                if (size2 > maxSize2) {
                    maxSize2 = size2;
                }
                int maxSize3 = maxSize2;
                float otherSize = (this.mOrientationHelper.getDecoratedMeasurementInOther(view4) * 1.0f) / ((GridLayoutManager.LayoutParams) view4.getLayoutParams()).mSpanSize;
                if (otherSize > maxSizeInOther) {
                    maxSizeInOther = otherSize;
                }
                i++;
                maxSize2 = maxSize3;
            }
            int i2 = 0;
            while (i2 < count) {
                View view5 = this.mSet[i2];
                if (this.mOrientationHelper.getDecoratedMeasurement(view5) == maxSize2) {
                    startPosition = startPosition2;
                    otherDirSpecMode = otherDirSpecMode2;
                    firstPositionStart = firstPositionStart2;
                } else {
                    GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view5.getLayoutParams();
                    startPosition = startPosition2;
                    Rect decorInsets = lp.mDecorInsets;
                    int verticalInsets = decorInsets.top + decorInsets.bottom + lp.topMargin + lp.bottomMargin;
                    otherDirSpecMode = otherDirSpecMode2;
                    int horizontalInsets = decorInsets.left + decorInsets.right + lp.leftMargin + lp.rightMargin;
                    int totalSpaceInOther = this.mCachedBorders[lp.mSpanSize];
                    firstPositionStart = firstPositionStart2;
                    int wSpec = getChildMeasureSpec(totalSpaceInOther, C.BUFFER_FLAG_ENCRYPTED, horizontalInsets, lp.width, false);
                    int hSpec = View.MeasureSpec.makeMeasureSpec(maxSize2 - verticalInsets, C.BUFFER_FLAG_ENCRYPTED);
                    measureChildWithDecorationsAndMargin(view5, wSpec, hSpec, true);
                }
                i2++;
                startPosition2 = startPosition;
                otherDirSpecMode2 = otherDirSpecMode;
                firstPositionStart2 = firstPositionStart;
            }
            int startPosition3 = startPosition2;
            int otherDirSpecMode3 = otherDirSpecMode2;
            boolean fromOpositeSide = shouldLayoutChildFromOpositeSide(this.mSet[0]);
            if ((!fromOpositeSide || layoutState.mLayoutDirection != -1) && (fromOpositeSide || layoutState.mLayoutDirection != 1)) {
                maxSize = maxSize2;
                int count2 = count;
                if (layoutState.mLayoutDirection == -1) {
                    int bottom3 = layoutState.mOffset - result.mConsumed;
                    int top3 = bottom3 - maxSize;
                    left3 = getWidth();
                    bottom2 = bottom3;
                    top2 = top3;
                } else {
                    int bottom4 = layoutState.mOffset;
                    int top4 = bottom4 + result.mConsumed;
                    int bottom5 = top4 + maxSize;
                    left3 = 0;
                    top2 = top4;
                    bottom2 = bottom5;
                }
                int i3 = 0;
                while (i3 < count2) {
                    View view6 = this.mSet[i3];
                    GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view6.getLayoutParams();
                    int right = this.mOrientationHelper.getDecoratedMeasurementInOther(view6);
                    if (layoutState.mLayoutDirection != -1) {
                        left4 = left3;
                    } else {
                        left4 = left3 - right;
                    }
                    int i4 = left4 + right;
                    int i5 = i3;
                    int i6 = bottom2;
                    layoutDecoratedWithMargins(view6, left4, top2, i4, i6);
                    if (layoutState.mLayoutDirection == -1) {
                        left3 = left4;
                    } else {
                        left3 = left4 + right;
                    }
                    if (params.isItemRemoved() || params.isItemChanged()) {
                        result.mIgnoreConsumed = true;
                    }
                    result.mFocusable |= view6.hasFocusable();
                    i3 = i5 + 1;
                }
            } else {
                if (layoutState.mLayoutDirection == -1) {
                    int bottom6 = layoutState.mOffset - result.mConsumed;
                    int top5 = bottom6 - maxSize2;
                    left = 0;
                    bottom = bottom6;
                    top = top5;
                } else {
                    int bottom7 = layoutState.mOffset;
                    int top6 = result.mConsumed + bottom7;
                    int bottom8 = top6 + maxSize2;
                    left = getWidth();
                    bottom = bottom8;
                    top = top6;
                }
                int bottom9 = count - 1;
                int i7 = bottom9;
                while (i7 >= 0) {
                    View view7 = this.mSet[i7];
                    GridLayoutManager.LayoutParams params2 = (GridLayoutManager.LayoutParams) view7.getLayoutParams();
                    int right2 = this.mOrientationHelper.getDecoratedMeasurementInOther(view7);
                    if (layoutState.mLayoutDirection != 1) {
                        left2 = left;
                    } else {
                        left2 = left - right2;
                    }
                    int maxSize4 = maxSize2;
                    int maxSize5 = top;
                    int count3 = count;
                    boolean fromOpositeSide2 = fromOpositeSide;
                    layoutDecoratedWithMargins(view7, left2, maxSize5, left2 + right2, bottom);
                    if (layoutState.mLayoutDirection != -1) {
                        left = left2;
                    } else {
                        left = left2 + right2;
                    }
                    if (params2.isItemRemoved() || params2.isItemChanged()) {
                        result.mIgnoreConsumed = true;
                    }
                    result.mFocusable |= view7.hasFocusable();
                    i7--;
                    count = count3;
                    fromOpositeSide = fromOpositeSide2;
                    maxSize2 = maxSize4;
                }
                maxSize = maxSize2;
            }
            result.mConsumed += maxSize;
            Arrays.fill(this.mSet, (Object) null);
            recycler2 = recycler;
            state2 = state;
            working = working3;
            startPosition2 = startPosition3;
            otherDirSpecMode2 = otherDirSpecMode3;
            z2 = true;
        }
    }
}
