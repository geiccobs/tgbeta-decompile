package org.telegram.ui.Adapters;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class PaddedListAdapter extends RecyclerListView.SelectionAdapter {
    private GetPaddingRunnable getPaddingRunnable;
    private int lastPadding;
    private RecyclerView.AdapterDataObserver mDataObserver;
    public View paddingView;
    private RecyclerListView.SelectionAdapter wrappedAdapter;
    private final int PADDING_VIEW_TYPE = -983904;
    private Integer padding = null;
    public boolean paddingViewAttached = false;

    /* loaded from: classes4.dex */
    public interface GetPaddingRunnable {
        int run(int i);
    }

    public PaddedListAdapter(RecyclerListView.SelectionAdapter adapter) {
        RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: org.telegram.ui.Adapters.PaddedListAdapter.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                super.onChanged();
                PaddedListAdapter.this.notifyDataSetChanged();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(positionStart + 1, itemCount);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeInserted(positionStart + 1, itemCount);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeRemoved(positionStart + 1, itemCount);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(fromPosition + 1, toPosition + 1 + itemCount);
            }
        };
        this.mDataObserver = adapterDataObserver;
        this.wrappedAdapter = adapter;
        adapter.registerAdapterDataObserver(adapterDataObserver);
    }

    public PaddedListAdapter(RecyclerListView.SelectionAdapter adapter, GetPaddingRunnable getPaddingRunnable) {
        RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: org.telegram.ui.Adapters.PaddedListAdapter.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                super.onChanged();
                PaddedListAdapter.this.notifyDataSetChanged();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(positionStart + 1, itemCount);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeInserted(positionStart + 1, itemCount);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                PaddedListAdapter.this.notifyItemRangeRemoved(positionStart + 1, itemCount);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                PaddedListAdapter.this.notifyItemRangeChanged(fromPosition + 1, toPosition + 1 + itemCount);
            }
        };
        this.mDataObserver = adapterDataObserver;
        this.wrappedAdapter = adapter;
        adapter.registerAdapterDataObserver(adapterDataObserver);
        this.getPaddingRunnable = getPaddingRunnable;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        if (holder.getAdapterPosition() == 0) {
            return false;
        }
        return this.wrappedAdapter.isEnabled(holder);
    }

    public void setPadding(int padding) {
        this.padding = Integer.valueOf(padding);
        View view = this.paddingView;
        if (view != null) {
            view.requestLayout();
        }
    }

    public void setPadding(GetPaddingRunnable getPaddingRunnable) {
        this.getPaddingRunnable = getPaddingRunnable;
        View view = this.paddingView;
        if (view != null) {
            view.requestLayout();
        }
    }

    public int getPadding(int parentHeight) {
        Integer num = this.padding;
        if (num != null) {
            int intValue = num.intValue();
            this.lastPadding = intValue;
            return intValue;
        }
        GetPaddingRunnable getPaddingRunnable = this.getPaddingRunnable;
        if (getPaddingRunnable != null) {
            int run = getPaddingRunnable.run(parentHeight);
            this.lastPadding = run;
            return run;
        }
        this.lastPadding = 0;
        return 0;
    }

    public int getPadding() {
        return this.lastPadding;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -983904) {
            View view = new View(parent.getContext()) { // from class: org.telegram.ui.Adapters.PaddedListAdapter.1
                @Override // android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int parentHeight = ((View) getParent()).getMeasuredHeight();
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(PaddedListAdapter.this.getPadding(parentHeight), C.BUFFER_FLAG_ENCRYPTED));
                }

                @Override // android.view.View
                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    PaddedListAdapter.this.paddingViewAttached = true;
                }

                @Override // android.view.View
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    PaddedListAdapter.this.paddingViewAttached = false;
                }
            };
            this.paddingView = view;
            return new RecyclerListView.Holder(view);
        }
        return this.wrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        if (position == 0) {
            return -983904;
        }
        return this.wrappedAdapter.getItemViewType(position - 1);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > 0) {
            this.wrappedAdapter.onBindViewHolder(holder, position - 1);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.wrappedAdapter.getItemCount() + 1;
    }
}
