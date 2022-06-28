package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public abstract class BottomSheetWithRecyclerListView extends BottomSheet {
    protected ActionBar actionBar;
    private BaseFragment baseFragment;
    protected boolean clipToActionBar;
    private int contentHeight;
    public final boolean hasFixedSize;
    protected RecyclerListView recyclerListView;
    public float topPadding = 0.4f;
    boolean wasDrawn;

    protected abstract RecyclerListView.SelectionAdapter createAdapter();

    protected abstract CharSequence getTitle();

    public BottomSheetWithRecyclerListView(BaseFragment fragment, boolean needFocus, final boolean hasFixedSize) {
        super(fragment.getParentActivity(), needFocus);
        this.baseFragment = fragment;
        this.hasFixedSize = hasFixedSize;
        final Context context = fragment.getParentActivity();
        final Drawable headerShadowDrawable = ContextCompat.getDrawable(context, R.drawable.header_shadow).mutate();
        final FrameLayout containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.BottomSheetWithRecyclerListView.1
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                BottomSheetWithRecyclerListView.this.contentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                BottomSheetWithRecyclerListView.this.onPreMeasure(widthMeasureSpec, heightMeasureSpec);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                if (!hasFixedSize) {
                    RecyclerView.ViewHolder holder = BottomSheetWithRecyclerListView.this.recyclerListView.findViewHolderForAdapterPosition(0);
                    int top = -AndroidUtilities.dp(16.0f);
                    if (holder != null) {
                        top = holder.itemView.getBottom() - AndroidUtilities.dp(16.0f);
                    }
                    float progressToFullView = 1.0f - ((AndroidUtilities.dp(16.0f) + top) / AndroidUtilities.dp(56.0f));
                    if (progressToFullView < 0.0f) {
                        progressToFullView = 0.0f;
                    }
                    AndroidUtilities.updateViewVisibilityAnimated(BottomSheetWithRecyclerListView.this.actionBar, progressToFullView != 0.0f, 1.0f, BottomSheetWithRecyclerListView.this.wasDrawn);
                    BottomSheetWithRecyclerListView.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), getMeasuredHeight());
                    BottomSheetWithRecyclerListView.this.shadowDrawable.draw(canvas);
                    BottomSheetWithRecyclerListView.this.onPreDraw(canvas, top, progressToFullView);
                }
                super.dispatchDraw(canvas);
                if (BottomSheetWithRecyclerListView.this.actionBar != null && BottomSheetWithRecyclerListView.this.actionBar.getVisibility() == 0 && BottomSheetWithRecyclerListView.this.actionBar.getAlpha() != 0.0f) {
                    headerShadowDrawable.setBounds(0, BottomSheetWithRecyclerListView.this.actionBar.getBottom(), getMeasuredWidth(), BottomSheetWithRecyclerListView.this.actionBar.getBottom() + headerShadowDrawable.getIntrinsicHeight());
                    headerShadowDrawable.setAlpha((int) (BottomSheetWithRecyclerListView.this.actionBar.getAlpha() * 255.0f));
                    headerShadowDrawable.draw(canvas);
                }
                BottomSheetWithRecyclerListView.this.wasDrawn = true;
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!hasFixedSize && BottomSheetWithRecyclerListView.this.clipToActionBar && child == BottomSheetWithRecyclerListView.this.recyclerListView) {
                    canvas.save();
                    canvas.clipRect(0, BottomSheetWithRecyclerListView.this.actionBar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                    super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent event) {
                if (event.getAction() == 0 && event.getY() < BottomSheetWithRecyclerListView.this.shadowDrawable.getBounds().top) {
                    BottomSheetWithRecyclerListView.this.dismiss();
                }
                return super.dispatchTouchEvent(event);
            }
        };
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.recyclerListView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        final RecyclerListView.SelectionAdapter adapter = createAdapter();
        if (hasFixedSize) {
            this.recyclerListView.setHasFixedSize(true);
            this.recyclerListView.setAdapter(adapter);
            setCustomView(containerView);
            containerView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, -2.0f));
        } else {
            this.recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.Components.BottomSheetWithRecyclerListView.2
                @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return adapter.isEnabled(holder);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    if (viewType == -1000) {
                        View view = new View(context) { // from class: org.telegram.ui.Components.BottomSheetWithRecyclerListView.2.1
                            @Override // android.view.View
                            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                                int h;
                                if (BottomSheetWithRecyclerListView.this.contentHeight != 0) {
                                    h = (int) (BottomSheetWithRecyclerListView.this.contentHeight * BottomSheetWithRecyclerListView.this.topPadding);
                                } else {
                                    h = AndroidUtilities.dp(300.0f);
                                }
                                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h, C.BUFFER_FLAG_ENCRYPTED));
                            }
                        };
                        return new RecyclerListView.Holder(view);
                    }
                    return adapter.onCreateViewHolder(parent, viewType);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    if (position != 0) {
                        adapter.onBindViewHolder(holder, position - 1);
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public int getItemViewType(int position) {
                    if (position == 0) {
                        return -1000;
                    }
                    return adapter.getItemViewType(position - 1);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public int getItemCount() {
                    return adapter.getItemCount() + 1;
                }
            });
            this.containerView = containerView;
            ActionBar actionBar = new ActionBar(context) { // from class: org.telegram.ui.Components.BottomSheetWithRecyclerListView.3
                @Override // android.view.View
                public void setAlpha(float alpha) {
                    if (getAlpha() != alpha) {
                        super.setAlpha(alpha);
                        containerView.invalidate();
                    }
                }

                @Override // android.view.View
                public void setTag(Object tag) {
                    super.setTag(tag);
                    BottomSheetWithRecyclerListView.this.updateStatusBar();
                }
            };
            this.actionBar = actionBar;
            actionBar.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
            this.actionBar.setTitleColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), false);
            this.actionBar.setItemsColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), false);
            this.actionBar.setCastShadows(true);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setTitle(getTitle());
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.BottomSheetWithRecyclerListView.4
                @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
                public void onItemClick(int id) {
                    if (id == -1) {
                        BottomSheetWithRecyclerListView.this.dismiss();
                    }
                }
            });
            containerView.addView(this.recyclerListView);
            containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f, 0, 6.0f, 0.0f, 6.0f, 0.0f));
            this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.BottomSheetWithRecyclerListView.5
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    containerView.invalidate();
                }
            });
        }
        onViewCreated(containerView);
        updateStatusBar();
    }

    public void onPreMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    }

    protected void onPreDraw(Canvas canvas, int top, float progressToFullView) {
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_dialogBackground)) > 0.699999988079071d;
    }

    public void onViewCreated(FrameLayout containerView) {
    }

    public void notifyDataSetChanged() {
        this.recyclerListView.getAdapter().notifyDataSetChanged();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updateStatusBar() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && actionBar.getTag() != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), isLightStatusBar());
        } else if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }
}
