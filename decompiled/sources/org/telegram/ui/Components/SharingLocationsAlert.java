package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes5.dex */
public class SharingLocationsAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private SharingLocationsAlertDelegate delegate;
    private boolean ignoreLayout;
    private RecyclerListView listView;
    private int reqId;
    private int scrollOffsetY;
    private Drawable shadowDrawable;
    private TextView textView;
    private Pattern urlPattern;

    /* loaded from: classes5.dex */
    public interface SharingLocationsAlertDelegate {
        void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo);
    }

    public SharingLocationsAlert(Context context, SharingLocationsAlertDelegate sharingLocationsAlertDelegate, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
        this.delegate = sharingLocationsAlertDelegate;
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.SharingLocationsAlert.1
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && SharingLocationsAlert.this.scrollOffsetY != 0 && ev.getY() < SharingLocationsAlert.this.scrollOffsetY) {
                    SharingLocationsAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !SharingLocationsAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int contentSize = AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(56.0f) + 1 + (LocationController.getLocationsCount() * AndroidUtilities.dp(54.0f));
                if (contentSize < (height / 5) * 3) {
                    padding = AndroidUtilities.dp(8.0f);
                } else {
                    padding = (height / 5) * 2;
                    if (contentSize < height) {
                        padding -= height - contentSize;
                    }
                }
                if (SharingLocationsAlert.this.listView.getPaddingTop() != padding) {
                    SharingLocationsAlert.this.ignoreLayout = true;
                    SharingLocationsAlert.this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(8.0f));
                    SharingLocationsAlert.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                SharingLocationsAlert.this.updateLayout();
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (SharingLocationsAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                SharingLocationsAlert.this.shadowDrawable.setBounds(0, SharingLocationsAlert.this.scrollOffsetY - SharingLocationsAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                SharingLocationsAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.SharingLocationsAlert.2
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, SharingLocationsAlert.this.listView, 0, null, this.resourcesProvider);
                return super.onInterceptTouchEvent(event) || result;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (SharingLocationsAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.SharingLocationsAlert.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                SharingLocationsAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.SharingLocationsAlert$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                SharingLocationsAlert.this.m3055lambda$new$0$orgtelegramuiComponentsSharingLocationsAlert(view, i);
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.containerView.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false);
        pickerBottomLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        this.containerView.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        pickerBottomLayout.cancelButton.setTextColor(getThemedColor(Theme.key_dialogTextRed));
        pickerBottomLayout.cancelButton.setText(LocaleController.getString("StopAllLocationSharings", R.string.StopAllLocationSharings));
        pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharingLocationsAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SharingLocationsAlert.this.m3056lambda$new$1$orgtelegramuiComponentsSharingLocationsAlert(view);
            }
        });
        pickerBottomLayout.doneButtonTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlue2));
        pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
        pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharingLocationsAlert$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SharingLocationsAlert.this.m3057lambda$new$2$orgtelegramuiComponentsSharingLocationsAlert(view);
            }
        });
        pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
        this.adapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SharingLocationsAlert */
    public /* synthetic */ void m3055lambda$new$0$orgtelegramuiComponentsSharingLocationsAlert(View view, int position) {
        int position2 = position - 1;
        if (position2 < 0 || position2 >= LocationController.getLocationsCount()) {
            return;
        }
        this.delegate.didSelectLocation(getLocation(position2));
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SharingLocationsAlert */
    public /* synthetic */ void m3056lambda$new$1$orgtelegramuiComponentsSharingLocationsAlert(View view) {
        for (int a = 0; a < 4; a++) {
            LocationController.getInstance(a).removeAllLocationSharings();
        }
        dismiss();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-SharingLocationsAlert */
    public /* synthetic */ void m3057lambda$new$2$orgtelegramuiComponentsSharingLocationsAlert(View view) {
        dismiss();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.liveLocationsChanged) {
            if (LocationController.getLocationsCount() == 0) {
                dismiss();
            } else {
                this.adapter.notifyDataSetChanged();
            }
        }
    }

    public LocationController.SharingLocationInfo getLocation(int position) {
        for (int a = 0; a < 4; a++) {
            ArrayList<LocationController.SharingLocationInfo> infos = LocationController.getInstance(a).sharingLocationsUI;
            if (position >= infos.size()) {
                position -= infos.size();
            } else {
                return infos.get(position);
            }
        }
        return null;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            SharingLocationsAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return LocationController.getLocationsCount() + 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SharingLiveLocationCell(this.context, false, 54, SharingLocationsAlert.this.resourcesProvider);
                    break;
                default:
                    FrameLayout frameLayout = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.SharingLocationsAlert.ListAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + 1, C.BUFFER_FLAG_ENCRYPTED));
                        }

                        @Override // android.view.View
                        protected void onDraw(Canvas canvas) {
                            canvas.drawLine(0.0f, AndroidUtilities.dp(40.0f), getMeasuredWidth(), AndroidUtilities.dp(40.0f), Theme.dividerPaint);
                        }
                    };
                    frameLayout.setWillNotDraw(false);
                    SharingLocationsAlert.this.textView = new TextView(this.context);
                    SharingLocationsAlert.this.textView.setTextColor(SharingLocationsAlert.this.getThemedColor(Theme.key_dialogIcon));
                    SharingLocationsAlert.this.textView.setTextSize(1, 14.0f);
                    SharingLocationsAlert.this.textView.setGravity(17);
                    SharingLocationsAlert.this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                    frameLayout.addView(SharingLocationsAlert.this.textView, LayoutHelper.createFrame(-1, 40.0f));
                    view = frameLayout;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    SharingLiveLocationCell cell = (SharingLiveLocationCell) holder.itemView;
                    cell.setDialog(SharingLocationsAlert.this.getLocation(position - 1));
                    return;
                case 1:
                    if (SharingLocationsAlert.this.textView != null) {
                        SharingLocationsAlert.this.textView.setText(LocaleController.formatString("SharingLiveLocationTitle", R.string.SharingLiveLocationTitle, LocaleController.formatPluralString("Chats", LocationController.getLocationsCount(), new Object[0])));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
