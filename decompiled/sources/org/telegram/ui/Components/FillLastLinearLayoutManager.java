package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes5.dex */
public class FillLastLinearLayoutManager extends LinearLayoutManager {
    private int additionalHeight;
    boolean fixedLastItemHeight;
    private int listHeight;
    private RecyclerView listView;
    private int listWidth;
    private int minimumHeight;
    private boolean skipFirstItem;
    private SparseArray<RecyclerView.ViewHolder> heights = new SparseArray<>();
    private int lastItemHeight = -1;
    private boolean bind = true;
    private boolean canScrollVertically = true;

    public FillLastLinearLayoutManager(Context context, int h, RecyclerView recyclerView) {
        super(context);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public FillLastLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int h, RecyclerView recyclerView) {
        super(context, orientation, reverseLayout);
        this.listView = recyclerView;
        this.additionalHeight = h;
    }

    public void setAdditionalHeight(int value) {
        this.additionalHeight = value;
        calcLastItemHeight();
    }

    public void setSkipFirstItem() {
        this.skipFirstItem = true;
    }

    public void setBind(boolean value) {
        this.bind = value;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void calcLastItemHeight() {
        RecyclerView.Adapter adapter;
        if (this.listHeight <= 0 || (adapter = this.listView.getAdapter()) == null) {
            return;
        }
        int count = adapter.getItemCount() - 1;
        int allHeight = 0;
        int firstItemHeight = 0;
        for (int a = this.skipFirstItem; a < count; a++) {
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
            int widthSpec = getChildMeasureSpec(this.listWidth, getWidthMode(), getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width, canScrollHorizontally());
            int heightSpec = getChildMeasureSpec(this.listHeight, getHeightMode(), getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin, lp.height, canScrollVertically());
            holder.itemView.measure(widthSpec, heightSpec);
            allHeight += holder.itemView.getMeasuredHeight();
            if (a == 0) {
                firstItemHeight = holder.itemView.getMeasuredHeight();
            }
            if (this.fixedLastItemHeight) {
                if (allHeight >= this.listHeight + firstItemHeight) {
                    break;
                }
            } else if (allHeight >= this.listHeight) {
                break;
            }
        }
        if (this.fixedLastItemHeight) {
            this.lastItemHeight = Math.max(this.minimumHeight, (((this.listHeight - allHeight) - this.additionalHeight) - this.listView.getPaddingBottom()) + firstItemHeight);
        } else {
            this.lastItemHeight = Math.max(this.minimumHeight, ((this.listHeight - allHeight) - this.additionalHeight) - this.listView.getPaddingBottom());
        }
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

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        this.heights.clear();
        calcLastItemHeight();
        super.onItemsChanged(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        super.onItemsMoved(recyclerView, from, to, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount, payload);
        calcLastItemHeight();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
        int pos = holder.getAdapterPosition();
        if (pos == getItemCount() - 1) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            layoutParams.height = Math.max(this.lastItemHeight, 0);
        }
        super.measureChildWithMargins(child, 0, 0);
    }

    public void setFixedLastItemHeight() {
        this.fixedLastItemHeight = true;
    }

    public void setMinimumLastViewHeight(int height) {
        this.minimumHeight = height;
    }
}
