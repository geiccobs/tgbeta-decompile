package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private FlickerLoadingView globalGradientView;
    private Context mContext;

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        if (isSearching()) {
            return 3;
        }
        return this.places.size();
    }

    public boolean isEmpty() {
        return this.places.size() == 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LocationCell locationCell = new LocationCell(this.mContext, false, null);
        return new RecyclerListView.Holder(locationCell);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TLRPC.TL_messageMediaVenue place = getItem(position);
        String iconUrl = (isSearching() || position < 0 || position >= this.iconUrls.size()) ? null : this.iconUrls.get(position);
        LocationCell locationCell = (LocationCell) holder.itemView;
        boolean z = true;
        if (position == getItemCount() - 1) {
            z = false;
        }
        locationCell.setLocation(place, iconUrl, position, z);
    }

    public TLRPC.TL_messageMediaVenue getItem(int i) {
        if (!isSearching() && i >= 0 && i < this.places.size()) {
            return this.places.get(i);
        }
        return null;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    @Override // org.telegram.ui.Adapters.BaseLocationAdapter
    protected void notifyStartSearch(boolean wasSearching, int oldItemCount, boolean animated) {
        if (wasSearching) {
            return;
        }
        notifyDataSetChanged();
    }
}
