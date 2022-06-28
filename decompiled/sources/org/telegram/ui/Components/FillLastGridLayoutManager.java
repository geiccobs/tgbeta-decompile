package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes5.dex */
public class FillLastGridLayoutManager extends GridLayoutManager {
    private int additionalHeight;
    private int listHeight;
    private RecyclerView listView;
    private int listWidth;
    private SparseArray<RecyclerView.ViewHolder> heights = new SparseArray<>();
    protected int lastItemHeight = -1;
    private boolean bind = true;
    private boolean canScrollVertically = true;

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public FillLastGridLayoutManager(Context context, int spanCount, int h, RecyclerView recyclerView) {
        super(context, spanCount);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public FillLastGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout, int h, RecyclerView recyclerView) {
        super(context, spanCount, orientation, reverseLayout);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public void setAdditionalHeight(int value) {
        this.additionalHeight = value;
        calcLastItemHeight();
    }

    protected void calcLastItemHeight() {
        RecyclerView.Adapter adapter;
        int spanCounter;
        int spanCount;
        RecyclerView.Adapter adapter2;
        if (this.listHeight <= 0 || !shouldCalcLastItemHeight() || (adapter = this.listView.getAdapter()) == null) {
            return;
        }
        int spanCount2 = getSpanCount();
        int spanCounter2 = 0;
        int count = adapter.getItemCount() - 1;
        int allHeight = 0;
        GridLayoutManager.SpanSizeLookup spanSizeLookup = getSpanSizeLookup();
        boolean add = true;
        int a = 0;
        while (a < count) {
            int spanSize = spanSizeLookup.getSpanSize(a);
            int spanCounter3 = spanCounter2 + spanSize;
            if (spanSize == spanCount2 || spanCounter3 > spanCount2) {
                spanCounter3 = spanSize;
                add = true;
            }
            if (!add) {
                adapter2 = adapter;
                spanCount = spanCount2;
                spanCounter = spanCounter3;
            } else {
                add = false;
                int type = adapter.getItemViewType(a);
                RecyclerView.ViewHolder holder = this.heights.get(type, null);
                if (holder == null) {
                    holder = adapter.createViewHolder(this.listView, type);
                    this.heights.put(type, holder);
                    if (holder.itemView.getLayoutParams() == null) {
                        holder.itemView.setLayoutParams(generateDefaultLayoutParams());
                    }
                }
                if (this.bind) {
                    adapter.onBindViewHolder(holder, a);
                }
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                adapter2 = adapter;
                spanCount = spanCount2;
                int widthSpec = getChildMeasureSpec(this.listWidth, getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width, canScrollHorizontally());
                spanCounter = spanCounter3;
                int heightSpec = getChildMeasureSpec(this.listHeight, getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height, canScrollVertically());
                holder.itemView.measure(widthSpec, heightSpec);
                allHeight += holder.itemView.getMeasuredHeight();
                if (allHeight >= (this.listHeight - this.additionalHeight) - this.listView.getPaddingBottom()) {
                    break;
                }
            }
            a++;
            adapter = adapter2;
            spanCount2 = spanCount;
            spanCounter2 = spanCounter;
        }
        this.lastItemHeight = Math.max(0, ((this.listHeight - allHeight) - this.additionalHeight) - this.listView.getPaddingBottom());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int lastHeight = this.listHeight;
        this.listWidth = View.MeasureSpec.getSize(widthSpec);
        int size = View.MeasureSpec.getSize(heightSpec);
        this.listHeight = size;
        if (lastHeight != size) {
            calcLastItemHeight();
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        this.heights.clear();
        calcLastItemHeight();
        super.onAdapterChanged(oldAdapter, newAdapter);
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        this.heights.clear();
        calcLastItemHeight();
        super.onItemsChanged(recyclerView);
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount, payload);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.GridLayoutManager
    public void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view);
        int pos = holder.getAdapterPosition();
        if (pos == getItemCount() - 1) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.height = Math.max(this.lastItemHeight, 0);
        }
        super.measureChild(view, otherDirParentSpecMode, alreadyMeasured);
    }

    protected boolean shouldCalcLastItemHeight() {
        return true;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }
}
