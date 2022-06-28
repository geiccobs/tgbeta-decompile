package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetCell2;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;
/* loaded from: classes5.dex */
public class TrendingStickersLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final TrendingStickersAdapter adapter;
    private final int currentAccount;
    private final Delegate delegate;
    ValueAnimator glueToTopAnimator;
    private boolean gluedToTop;
    private long hash;
    private float highlightProgress;
    private boolean ignoreLayout;
    private final LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets;
    private final GridLayoutManager layoutManager;
    private final RecyclerListView listView;
    private boolean loaded;
    private boolean motionEventCatchedByListView;
    private RecyclerView.OnScrollListener onScrollListener;
    Paint paint;
    private BaseFragment parentFragment;
    private final TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean scrollFromAnimator;
    private TLRPC.StickerSetCovered scrollToSet;
    private final StickersSearchAdapter searchAdapter;
    private final FrameLayout searchLayout;
    private final SearchField searchView;
    private final View shadowView;
    private boolean shadowVisible;
    private int topOffset;
    private boolean wasLayout;

    /* loaded from: classes5.dex */
    public static abstract class Delegate {
        private String[] lastSearchKeyboardLanguage = new String[0];

        public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
        }

        public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
        }

        public boolean onListViewInterceptTouchEvent(RecyclerListView listView, MotionEvent event) {
            return false;
        }

        public boolean onListViewTouchEvent(RecyclerListView listView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent event) {
            return false;
        }

        public String[] getLastSearchKeyboardLanguage() {
            return this.lastSearchKeyboardLanguage;
        }

        public void setLastSearchKeyboardLanguage(String[] language) {
            this.lastSearchKeyboardLanguage = language;
        }

        public boolean canSendSticker() {
            return false;
        }

        public void onStickerSelected(TLRPC.Document sticker, Object parent, boolean clearsInputField, boolean notify, int scheduleDate) {
        }

        public boolean canSchedule() {
            return false;
        }

        public boolean isInScheduleMode() {
            return false;
        }
    }

    public TrendingStickersLayout(Context context, Delegate delegate) {
        this(context, delegate, new TLRPC.StickerSetCovered[10], new LongSparseArray(), new LongSparseArray(), null, null);
    }

    public TrendingStickersLayout(Context context, final Delegate delegate, TLRPC.StickerSetCovered[] primaryInstallingStickerSets, LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets, LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets, TLRPC.StickerSetCovered scrollToSet, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.highlightProgress = 1.0f;
        this.paint = new Paint();
        this.delegate = delegate;
        this.primaryInstallingStickerSets = primaryInstallingStickerSets;
        this.installingStickerSets = installingStickerSets;
        this.removingStickerSets = removingStickerSets;
        this.scrollToSet = scrollToSet;
        this.resourcesProvider = resourcesProvider;
        TrendingStickersAdapter trendingStickersAdapter = new TrendingStickersAdapter(context);
        this.adapter = trendingStickersAdapter;
        StickersSearchAdapter.Delegate searchAdapterDelegate = new StickersSearchAdapter.Delegate() { // from class: org.telegram.ui.Components.TrendingStickersLayout.1
            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public void onSearchStart() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().startAnimation();
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public void onSearchStop() {
                TrendingStickersLayout.this.searchView.getProgressDrawable().stopAnimation();
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public void setAdapterVisible(boolean visible) {
                boolean changed = false;
                if (visible && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.searchAdapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.searchAdapter);
                    changed = true;
                } else if (!visible && TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    TrendingStickersLayout.this.listView.setAdapter(TrendingStickersLayout.this.adapter);
                    changed = true;
                }
                if (changed && TrendingStickersLayout.this.listView.getAdapter().getItemCount() > 0) {
                    TrendingStickersLayout.this.layoutManager.scrollToPositionWithOffset(0, (-TrendingStickersLayout.this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f) + TrendingStickersLayout.this.topOffset, false);
                }
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
                delegate.onStickerSetAdd(stickerSet, primary);
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                delegate.onStickerSetRemove(stickerSet);
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public int getStickersPerRow() {
                return TrendingStickersLayout.this.adapter.stickersPerRow;
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public String[] getLastSearchKeyboardLanguage() {
                return delegate.getLastSearchKeyboardLanguage();
            }

            @Override // org.telegram.ui.Adapters.StickersSearchAdapter.Delegate
            public void setLastSearchKeyboardLanguage(String[] language) {
                delegate.setLastSearchKeyboardLanguage(language);
            }
        };
        this.searchAdapter = new StickersSearchAdapter(context, searchAdapterDelegate, primaryInstallingStickerSets, installingStickerSets, removingStickerSets, resourcesProvider);
        FrameLayout frameLayout = new FrameLayout(context);
        this.searchLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        SearchField searchField = new SearchField(context, true, resourcesProvider) { // from class: org.telegram.ui.Components.TrendingStickersLayout.2
            @Override // org.telegram.ui.Components.SearchField
            public void onTextChange(String text) {
                TrendingStickersLayout.this.searchAdapter.search(text);
            }
        };
        this.searchView = searchField;
        searchField.setHint(LocaleController.getString("SearchTrendingStickersHint", R.string.SearchTrendingStickersHint));
        frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 48));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.TrendingStickersLayout.3
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = delegate.onListViewInterceptTouchEvent(this, event);
                return super.onInterceptTouchEvent(event) || result;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                TrendingStickersLayout.this.motionEventCatchedByListView = true;
                return super.dispatchTouchEvent(ev);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                if (TrendingStickersLayout.this.glueToTopAnimator != null) {
                    return false;
                }
                return super.onTouchEvent(e);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (!TrendingStickersLayout.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (TrendingStickersLayout.this.topOffset + AndroidUtilities.dp(58.0f)));
            }
        };
        this.listView = recyclerListView;
        final RecyclerListView.OnItemClickListener trendingOnItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.TrendingStickersLayout$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                TrendingStickersLayout.this.m3180lambda$new$0$orgtelegramuiComponentsTrendingStickersLayout(view, i2);
            }
        };
        recyclerListView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.TrendingStickersLayout$$ExternalSyntheticLambda0
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return TrendingStickersLayout.this.m3181lambda$new$1$orgtelegramuiComponentsTrendingStickersLayout(delegate, trendingOnItemClickListener, view, motionEvent);
            }
        });
        recyclerListView.setOverScrollMode(2);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setLayoutAnimation(null);
        FillLastGridLayoutManager fillLastGridLayoutManager = new FillLastGridLayoutManager(context, 5, AndroidUtilities.dp(58.0f), recyclerListView) { // from class: org.telegram.ui.Components.TrendingStickersLayout.4
            @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager
            public boolean isLayoutRTL() {
                return LocaleController.isRTL;
            }

            @Override // org.telegram.ui.Components.FillLastGridLayoutManager
            protected boolean shouldCalcLastItemHeight() {
                return TrendingStickersLayout.this.listView.getAdapter() == TrendingStickersLayout.this.searchAdapter;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                View minView;
                if (TrendingStickersLayout.this.scrollFromAnimator) {
                    return super.scrollVerticallyBy(dy, recycler, state);
                }
                if (TrendingStickersLayout.this.glueToTopAnimator == null) {
                    if (TrendingStickersLayout.this.gluedToTop) {
                        int minPosition = 1;
                        int i2 = 0;
                        while (true) {
                            if (i2 >= getChildCount()) {
                                break;
                            }
                            int p = TrendingStickersLayout.this.listView.getChildAdapterPosition(getChildAt(i2));
                            if (p >= 1) {
                                i2++;
                            } else {
                                minPosition = p;
                                break;
                            }
                        }
                        if (minPosition == 0 && (minView = TrendingStickersLayout.this.layoutManager.findViewByPosition(minPosition)) != null && minView.getTop() - dy > AndroidUtilities.dp(58.0f)) {
                            dy = minView.getTop() - AndroidUtilities.dp(58.0f);
                        }
                    }
                    int minPosition2 = super.scrollVerticallyBy(dy, recycler, state);
                    return minPosition2;
                }
                return 0;
            }
        };
        this.layoutManager = fillLastGridLayoutManager;
        recyclerListView.setLayoutManager(fillLastGridLayoutManager);
        fillLastGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.TrendingStickersLayout.5
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    return TrendingStickersLayout.this.searchAdapter.getSpanSize(position);
                }
                if (!(TrendingStickersLayout.this.adapter.cache.get(position) instanceof Integer) && position < TrendingStickersLayout.this.adapter.totalItems) {
                    return 1;
                }
                return TrendingStickersLayout.this.adapter.stickersPerRow;
            }
        });
        recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.TrendingStickersLayout.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrolled(TrendingStickersLayout.this.listView, dx, dy);
                }
                if (dy <= 0 || TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter || !TrendingStickersLayout.this.loaded || TrendingStickersLayout.this.adapter.loadingMore || TrendingStickersLayout.this.adapter.endReached) {
                    return;
                }
                int threshold = (TrendingStickersLayout.this.adapter.stickersPerRow + 1) * 10;
                if (TrendingStickersLayout.this.layoutManager.findLastVisibleItemPosition() >= (TrendingStickersLayout.this.adapter.getItemCount() - threshold) - 1) {
                    TrendingStickersLayout.this.adapter.loadMoreStickerSets();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (TrendingStickersLayout.this.onScrollListener != null) {
                    TrendingStickersLayout.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }
        });
        recyclerListView.setAdapter(trendingStickersAdapter);
        recyclerListView.setOnItemClickListener(trendingOnItemClickListener);
        addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        this.shadowView = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        view.setAlpha(0.0f);
        FrameLayout.LayoutParams shadowViewParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight());
        shadowViewParams.topMargin = AndroidUtilities.dp(58.0f);
        addView(view, shadowViewParams);
        addView(frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateColors();
        NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
        notificationCenter.addObserver(this, NotificationCenter.stickersDidLoad);
        notificationCenter.addObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TrendingStickersLayout */
    public /* synthetic */ void m3180lambda$new$0$orgtelegramuiComponentsTrendingStickersLayout(View view, int position) {
        TLRPC.StickerSetCovered pack;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        StickersSearchAdapter stickersSearchAdapter = this.searchAdapter;
        if (adapter == stickersSearchAdapter) {
            pack = stickersSearchAdapter.getSetForPosition(position);
        } else if (position >= this.adapter.totalItems) {
            pack = null;
        } else {
            pack = (TLRPC.StickerSetCovered) this.adapter.positionsToSets.get(position);
        }
        if (pack != null) {
            showStickerSet(pack.set);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TrendingStickersLayout */
    public /* synthetic */ boolean m3181lambda$new$1$orgtelegramuiComponentsTrendingStickersLayout(Delegate delegate, RecyclerListView.OnItemClickListener trendingOnItemClickListener, View v, MotionEvent event) {
        return delegate.onListViewTouchEvent(this.listView, trendingOnItemClickListener, event);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Integer pos;
        super.onLayout(changed, l, t, r, b);
        if (!this.wasLayout) {
            this.wasLayout = true;
            this.adapter.refreshStickerSets();
            if (this.scrollToSet != null && (pos = (Integer) this.adapter.setsToPosition.get(this.scrollToSet)) != null) {
                this.layoutManager.scrollToPositionWithOffset(pos.intValue(), (-this.listView.getPaddingTop()) + AndroidUtilities.dp(58.0f));
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float f = this.highlightProgress;
        if (f != 0.0f && this.scrollToSet != null) {
            float f2 = f - 0.0053333333f;
            this.highlightProgress = f2;
            if (f2 < 0.0f) {
                this.highlightProgress = 0.0f;
            } else {
                invalidate();
            }
            Integer pos = (Integer) this.adapter.setsToPosition.get(this.scrollToSet);
            if (pos != null) {
                View view1 = this.layoutManager.findViewByPosition(pos.intValue());
                int t = -1;
                int b = -1;
                if (view1 != null) {
                    t = (int) view1.getY();
                    b = ((int) view1.getY()) + view1.getMeasuredHeight();
                }
                View view2 = this.layoutManager.findViewByPosition(pos.intValue() + 1);
                if (view2 != null) {
                    if (view1 == null) {
                        t = (int) view2.getY();
                    }
                    b = ((int) view2.getY()) + view2.getMeasuredHeight();
                }
                if (view1 != null || view2 != null) {
                    this.paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                    float f3 = this.highlightProgress;
                    float p = f3 < 0.06f ? f3 / 0.06f : 1.0f;
                    this.paint.setAlpha((int) (25.5f * p));
                    canvas.drawRect(0.0f, t, getMeasuredWidth(), b, this.paint);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLastItemInAdapter();
        this.wasLayout = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.motionEventCatchedByListView = false;
        boolean result = super.dispatchTouchEvent(ev);
        if (!this.motionEventCatchedByListView) {
            MotionEvent e = MotionEvent.obtain(ev);
            this.listView.dispatchTouchEvent(e);
            e.recycle();
        }
        return result;
    }

    private void showStickerSet(TLRPC.StickerSet pack) {
        showStickerSet(pack, null);
    }

    public void showStickerSet(TLRPC.StickerSet pack, TLRPC.InputStickerSet inputStickerSet) {
        if (pack != null) {
            inputStickerSet = new TLRPC.TL_inputStickerSetID();
            inputStickerSet.access_hash = pack.access_hash;
            inputStickerSet.id = pack.id;
        }
        if (inputStickerSet != null) {
            showStickerSet(inputStickerSet);
        }
    }

    private void showStickerSet(final TLRPC.InputStickerSet inputStickerSet) {
        StickersAlert.StickersAlertDelegate stickersAlertDelegate;
        if (this.delegate.canSendSticker()) {
            stickersAlertDelegate = new StickersAlert.StickersAlertDelegate() { // from class: org.telegram.ui.Components.TrendingStickersLayout.7
                @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
                public void onStickerSelected(TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean clearsInputField, boolean notify, int scheduleDate) {
                    TrendingStickersLayout.this.delegate.onStickerSelected(sticker, parent, clearsInputField, notify, scheduleDate);
                }

                @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
                public boolean canSchedule() {
                    return TrendingStickersLayout.this.delegate.canSchedule();
                }

                @Override // org.telegram.ui.Components.StickersAlert.StickersAlertDelegate
                public boolean isInScheduleMode() {
                    return TrendingStickersLayout.this.delegate.isInScheduleMode();
                }
            };
        } else {
            stickersAlertDelegate = null;
        }
        StickersAlert stickersAlert = new StickersAlert(getContext(), this.parentFragment, inputStickerSet, null, stickersAlertDelegate, this.resourcesProvider);
        stickersAlert.setShowTooltipWhenToggle(false);
        stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() { // from class: org.telegram.ui.Components.TrendingStickersLayout.8
            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate
            public void onStickerSetInstalled() {
                if (TrendingStickersLayout.this.listView.getAdapter() != TrendingStickersLayout.this.adapter) {
                    TrendingStickersLayout.this.searchAdapter.installStickerSet(inputStickerSet);
                    return;
                }
                for (int i = 0; i < TrendingStickersLayout.this.adapter.sets.size(); i++) {
                    TLRPC.StickerSetCovered setCovered = (TLRPC.StickerSetCovered) TrendingStickersLayout.this.adapter.sets.get(i);
                    if (setCovered.set.id == inputStickerSet.id) {
                        TrendingStickersLayout.this.adapter.installStickerSet(setCovered, null);
                        return;
                    }
                }
            }

            @Override // org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate
            public void onStickerSetUninstalled() {
            }
        });
        this.parentFragment.showDialog(stickersAlert);
    }

    public void recycle() {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        notificationCenter.removeObserver(this, NotificationCenter.stickersDidLoad);
        notificationCenter.removeObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (((Integer) args[0]).intValue() == 0) {
                if (this.loaded) {
                    updateVisibleTrendingSets();
                } else {
                    this.adapter.refreshStickerSets();
                }
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
            if (this.hash != MediaDataController.getInstance(this.currentAccount).getFeaturesStickersHashWithoutUnread()) {
                this.loaded = false;
            }
            if (this.loaded) {
                updateVisibleTrendingSets();
            } else {
                this.adapter.refreshStickerSets();
            }
        }
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setParentFragment(BaseFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void setContentViewPaddingTop(int paddingTop) {
        int paddingTop2 = paddingTop + AndroidUtilities.dp(58.0f);
        if (this.listView.getPaddingTop() != paddingTop2) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, paddingTop2, 0, 0);
            this.ignoreLayout = false;
        }
    }

    private void updateLastItemInAdapter() {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        adapter.notifyItemChanged(adapter.getItemCount() - 1);
    }

    public int getContentTopOffset() {
        return this.topOffset;
    }

    public boolean update() {
        if (this.listView.getChildCount() <= 0) {
            int paddingTop = this.listView.getPaddingTop();
            this.topOffset = paddingTop;
            this.listView.setTopGlowOffset(paddingTop);
            this.searchLayout.setTranslationY(this.topOffset);
            this.shadowView.setTranslationY(this.topOffset);
            setShadowVisible(false);
            return true;
        }
        View child = this.listView.getChildAt(0);
        for (int i = 1; i < this.listView.getChildCount(); i++) {
            View view = this.listView.getChildAt(i);
            if (view.getTop() < child.getTop()) {
                child = view;
            }
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(58.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        setShadowVisible(top < 0);
        if (this.topOffset == newOffset) {
            return false;
        }
        this.topOffset = newOffset;
        this.listView.setTopGlowOffset(AndroidUtilities.dp(58.0f) + newOffset);
        this.searchLayout.setTranslationY(this.topOffset);
        this.shadowView.setTranslationY(this.topOffset);
        return true;
    }

    private void updateVisibleTrendingSets() {
        RecyclerView.Adapter listAdapter = this.listView.getAdapter();
        if (listAdapter != null) {
            listAdapter.notifyItemRangeChanged(0, listAdapter.getItemCount(), 0);
        }
    }

    private void setShadowVisible(boolean visible) {
        if (this.shadowVisible != visible) {
            this.shadowVisible = visible;
            this.shadowView.animate().alpha(visible ? 1.0f : 0.0f).setDuration(200L).start();
        }
    }

    public void updateColors() {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        TrendingStickersAdapter trendingStickersAdapter = this.adapter;
        if (adapter == trendingStickersAdapter) {
            trendingStickersAdapter.updateColors(this.listView);
        } else {
            this.searchAdapter.updateColors(this.listView);
        }
    }

    public void getThemeDescriptions(List<ThemeDescription> descriptions, ThemeDescription.ThemeDescriptionDelegate delegate) {
        this.searchView.getThemeDescriptions(descriptions);
        this.adapter.getThemeDescriptions(descriptions, this.listView, delegate);
        this.searchAdapter.getThemeDescriptions(descriptions, this.listView, delegate);
        descriptions.add(new ThemeDescription(this.shadowView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        descriptions.add(new ThemeDescription(this.searchLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
    }

    public void glueToTop(boolean glue) {
        this.gluedToTop = glue;
        if (glue) {
            if (getContentTopOffset() > 0 && this.glueToTopAnimator == null) {
                final int startFrom = getContentTopOffset();
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.glueToTopAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.TrendingStickersLayout.9
                    int dy = 0;

                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int currentDy = (int) (startFrom * ((Float) valueAnimator.getAnimatedValue()).floatValue());
                        TrendingStickersLayout.this.scrollFromAnimator = true;
                        TrendingStickersLayout.this.listView.scrollBy(0, currentDy - this.dy);
                        TrendingStickersLayout.this.scrollFromAnimator = false;
                        this.dy = currentDy;
                    }
                });
                this.glueToTopAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.TrendingStickersLayout.10
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        TrendingStickersLayout.this.glueToTopAnimator = null;
                    }
                });
                this.glueToTopAnimator.setDuration(250L);
                this.glueToTopAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                this.glueToTopAnimator.start();
                return;
            }
            return;
        }
        ValueAnimator valueAnimator = this.glueToTopAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.glueToTopAnimator.cancel();
            this.glueToTopAnimator = null;
        }
    }

    /* loaded from: classes5.dex */
    public class TrendingStickersAdapter extends RecyclerListView.SelectionAdapter {
        private static final int ITEM_SECTION = -1;
        public static final int PAYLOAD_ANIMATED = 0;
        private final Context context;
        private boolean endReached;
        private boolean loadingMore;
        private int totalItems;
        private final SparseArray<Object> cache = new SparseArray<>();
        private final ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();
        private final SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        private final HashMap<TLRPC.StickerSetCovered, Integer> setsToPosition = new HashMap<>();
        private final ArrayList<TLRPC.StickerSetCovered> otherPacks = new ArrayList<>();
        private int stickersPerRow = 5;

        public TrendingStickersAdapter(Context context) {
            TrendingStickersLayout.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.totalItems + 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 5;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 3;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            if (object.equals(-1)) {
                return 4;
            }
            return 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    StickerEmojiCell stickerCell = new StickerEmojiCell(this.context, false) { // from class: org.telegram.ui.Components.TrendingStickersLayout.TrendingStickersAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    stickerCell.getImageView().setLayerNum(3);
                    view = stickerCell;
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(this.context, 17, true, true, TrendingStickersLayout.this.resourcesProvider);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            TrendingStickersLayout.TrendingStickersAdapter.this.m3184xd64a6007(view2);
                        }
                    });
                    break;
                case 3:
                    view = new View(this.context);
                    break;
                case 4:
                    view = new GraySectionCell(this.context, TrendingStickersLayout.this.resourcesProvider);
                    break;
                case 5:
                    FeaturedStickerSetCell2 stickerSetCell = new FeaturedStickerSetCell2(this.context, TrendingStickersLayout.this.resourcesProvider);
                    stickerSetCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            TrendingStickersLayout.TrendingStickersAdapter.this.m3185x9f88ac8(view2);
                        }
                    });
                    stickerSetCell.getImageView().setLayerNum(3);
                    view = stickerSetCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter */
        public /* synthetic */ void m3184xd64a6007(View v) {
            FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) v.getParent();
            TLRPC.StickerSetCovered pack = cell.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(pack.set.id) >= 0 || TrendingStickersLayout.this.removingStickerSets.indexOfKey(pack.set.id) >= 0) {
                return;
            }
            if (cell.isInstalled()) {
                TrendingStickersLayout.this.removingStickerSets.put(pack.set.id, pack);
                TrendingStickersLayout.this.delegate.onStickerSetRemove(pack);
                return;
            }
            installStickerSet(pack, cell);
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter */
        public /* synthetic */ void m3185x9f88ac8(View v) {
            FeaturedStickerSetCell2 cell = (FeaturedStickerSetCell2) v.getParent();
            TLRPC.StickerSetCovered pack = cell.getStickerSet();
            if (TrendingStickersLayout.this.installingStickerSets.indexOfKey(pack.set.id) >= 0 || TrendingStickersLayout.this.removingStickerSets.indexOfKey(pack.set.id) >= 0) {
                return;
            }
            if (cell.isInstalled()) {
                TrendingStickersLayout.this.removingStickerSets.put(pack.set.id, pack);
                TrendingStickersLayout.this.delegate.onStickerSetRemove(pack);
                return;
            }
            installStickerSet(pack, cell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    ((StickerEmojiCell) holder.itemView).setSticker(sticker, this.positionsToSets.get(position), false);
                    return;
                case 1:
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                case 5:
                    bindStickerSetCell(holder.itemView, position, false);
                    return;
                case 3:
                default:
                    return;
                case 4:
                    ((GraySectionCell) holder.itemView).setText(LocaleController.getString("OtherStickers", R.string.OtherStickers));
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
            if (payloads.contains(0)) {
                int type = holder.getItemViewType();
                if (type == 2 || type == 5) {
                    bindStickerSetCell(holder.itemView, position, true);
                    return;
                }
                return;
            }
            super.onBindViewHolder(holder, position, payloads);
        }

        private void bindStickerSetCell(View view, int position, boolean animated) {
            TLRPC.StickerSetCovered stickerSetCovered;
            boolean forceInstalled;
            boolean installing;
            MediaDataController mediaDataController = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount);
            boolean unread = false;
            boolean z = true;
            if (position < this.totalItems) {
                stickerSetCovered = this.sets.get(((Integer) this.cache.get(position)).intValue());
                ArrayList<Long> unreadStickers = mediaDataController.getUnreadStickerSets();
                unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                if (unread) {
                    mediaDataController.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                }
            } else {
                stickerSetCovered = this.sets.get(((Integer) this.cache.get(position)).intValue());
            }
            mediaDataController.preloadStickerSetThumb(stickerSetCovered);
            int i = 0;
            while (true) {
                if (i < TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    if (TrendingStickersLayout.this.primaryInstallingStickerSets[i] != null) {
                        TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount).getStickerSetById(TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id);
                        if (s == null || s.set.archived) {
                            if (TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id == stickerSetCovered.set.id) {
                                forceInstalled = true;
                                break;
                            }
                        } else {
                            TrendingStickersLayout.this.primaryInstallingStickerSets[i] = null;
                        }
                    }
                    i++;
                } else {
                    forceInstalled = false;
                    break;
                }
            }
            boolean isSetInstalled = mediaDataController.isStickerPackInstalled(stickerSetCovered.set.id);
            boolean installing2 = TrendingStickersLayout.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
            boolean removing = TrendingStickersLayout.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
            if (installing2 && isSetInstalled) {
                TrendingStickersLayout.this.installingStickerSets.remove(stickerSetCovered.set.id);
                installing = false;
            } else {
                if (removing && !isSetInstalled) {
                    TrendingStickersLayout.this.removingStickerSets.remove(stickerSetCovered.set.id);
                }
                installing = installing2;
            }
            FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) view;
            cell.setStickerSet(stickerSetCovered, unread, animated, 0, 0, forceInstalled);
            cell.setAddDrawProgress(!forceInstalled && installing, animated);
            if (position <= 0 || (this.cache.get(position - 1) != null && this.cache.get(position - 1).equals(-1))) {
                z = false;
            }
            cell.setNeedDivider(z);
        }

        public void installStickerSet(TLRPC.StickerSetCovered pack, View view) {
            int i = 0;
            while (true) {
                if (i >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    break;
                }
                if (TrendingStickersLayout.this.primaryInstallingStickerSets[i] != null) {
                    TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount).getStickerSetById(TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id);
                    if (s == null || s.set.archived) {
                        if (TrendingStickersLayout.this.primaryInstallingStickerSets[i].set.id == pack.set.id) {
                            return;
                        }
                    } else {
                        TrendingStickersLayout.this.primaryInstallingStickerSets[i] = null;
                        break;
                    }
                }
                i++;
            }
            boolean primary = false;
            int i2 = 0;
            while (true) {
                if (i2 >= TrendingStickersLayout.this.primaryInstallingStickerSets.length) {
                    break;
                } else if (TrendingStickersLayout.this.primaryInstallingStickerSets[i2] == null) {
                    TrendingStickersLayout.this.primaryInstallingStickerSets[i2] = pack;
                    primary = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!primary && view != null) {
                if (view instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) view).setDrawProgress(true, true);
                } else if (view instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) view).setAddDrawProgress(true, true);
                }
            }
            TrendingStickersLayout.this.installingStickerSets.put(pack.set.id, pack);
            if (view != null) {
                TrendingStickersLayout.this.delegate.onStickerSetAdd(pack, primary);
                return;
            }
            int size = this.positionsToSets.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.StickerSetCovered item = this.positionsToSets.get(i3);
                if (item != null && item.set.id == pack.set.id) {
                    notifyItemChanged(i3, 0);
                    return;
                }
            }
        }

        public void refreshStickerSets() {
            int count;
            int i;
            int width = TrendingStickersLayout.this.getMeasuredWidth();
            if (width != 0) {
                this.stickersPerRow = Math.max(5, width / AndroidUtilities.dp(72.0f));
                if (TrendingStickersLayout.this.layoutManager.getSpanCount() != this.stickersPerRow) {
                    TrendingStickersLayout.this.layoutManager.setSpanCount(this.stickersPerRow);
                    TrendingStickersLayout.this.loaded = false;
                }
            }
            if (TrendingStickersLayout.this.loaded) {
                return;
            }
            this.cache.clear();
            this.positionsToSets.clear();
            this.setsToPosition.clear();
            this.sets.clear();
            this.totalItems = 0;
            int count2 = 0;
            MediaDataController mediaDataController = MediaDataController.getInstance(TrendingStickersLayout.this.currentAccount);
            ArrayList<TLRPC.StickerSetCovered> packs = new ArrayList<>(mediaDataController.getFeaturedStickerSets());
            int otherStickersSectionPosition = packs.size();
            packs.addAll(this.otherPacks);
            for (int a = 0; a < packs.size(); a++) {
                TLRPC.StickerSetCovered pack = packs.get(a);
                if (!pack.covers.isEmpty() || pack.cover != null) {
                    if (a == otherStickersSectionPosition) {
                        SparseArray<Object> sparseArray = this.cache;
                        int i2 = this.totalItems;
                        this.totalItems = i2 + 1;
                        sparseArray.put(i2, -1);
                    }
                    this.sets.add(pack);
                    this.positionsToSets.put(this.totalItems, pack);
                    this.setsToPosition.put(pack, Integer.valueOf(this.totalItems));
                    SparseArray<Object> sparseArray2 = this.cache;
                    int i3 = this.totalItems;
                    this.totalItems = i3 + 1;
                    int num = count2 + 1;
                    sparseArray2.put(i3, Integer.valueOf(count2));
                    if (!pack.covers.isEmpty()) {
                        count = (int) Math.ceil(pack.covers.size() / this.stickersPerRow);
                        for (int b = 0; b < pack.covers.size(); b++) {
                            this.cache.put(this.totalItems + b, pack.covers.get(b));
                        }
                    } else {
                        count = 1;
                        this.cache.put(this.totalItems, pack.cover);
                    }
                    int b2 = 0;
                    while (true) {
                        i = this.stickersPerRow;
                        if (b2 >= count * i) {
                            break;
                        }
                        this.positionsToSets.put(this.totalItems + b2, pack);
                        b2++;
                    }
                    int b3 = this.totalItems;
                    this.totalItems = b3 + (i * count);
                    count2 = num;
                }
            }
            int a2 = this.totalItems;
            if (a2 != 0) {
                TrendingStickersLayout.this.loaded = true;
                TrendingStickersLayout.this.hash = mediaDataController.getFeaturesStickersHashWithoutUnread();
            }
            notifyDataSetChanged();
        }

        public void loadMoreStickerSets() {
            if (!TrendingStickersLayout.this.loaded || this.loadingMore || this.endReached) {
                return;
            }
            this.loadingMore = true;
            TLRPC.TL_messages_getOldFeaturedStickers req = new TLRPC.TL_messages_getOldFeaturedStickers();
            req.offset = this.otherPacks.size();
            req.limit = 40;
            ConnectionsManager.getInstance(TrendingStickersLayout.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TrendingStickersLayout.TrendingStickersAdapter.this.m3183x81515877(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$loadMoreStickerSets$3$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter */
        public /* synthetic */ void m3183x81515877(final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TrendingStickersLayout.TrendingStickersAdapter.this.m3182x4da32db6(error, response);
                }
            });
        }

        /* renamed from: lambda$loadMoreStickerSets$2$org-telegram-ui-Components-TrendingStickersLayout$TrendingStickersAdapter */
        public /* synthetic */ void m3182x4da32db6(TLRPC.TL_error error, TLObject response) {
            int count;
            int i;
            this.loadingMore = false;
            if (error == null && (response instanceof TLRPC.TL_messages_featuredStickers)) {
                TLRPC.TL_messages_featuredStickers stickersResponse = (TLRPC.TL_messages_featuredStickers) response;
                List<TLRPC.StickerSetCovered> packs = stickersResponse.sets;
                if (packs.size() < 40) {
                    this.endReached = true;
                }
                if (!packs.isEmpty()) {
                    if (this.otherPacks.isEmpty()) {
                        SparseArray<Object> sparseArray = this.cache;
                        int i2 = this.totalItems;
                        this.totalItems = i2 + 1;
                        sparseArray.put(i2, -1);
                    }
                    this.otherPacks.addAll(packs);
                    int count2 = this.sets.size();
                    for (int a = 0; a < packs.size(); a++) {
                        TLRPC.StickerSetCovered pack = packs.get(a);
                        if (!pack.covers.isEmpty() || pack.cover != null) {
                            this.sets.add(pack);
                            this.positionsToSets.put(this.totalItems, pack);
                            SparseArray<Object> sparseArray2 = this.cache;
                            int i3 = this.totalItems;
                            this.totalItems = i3 + 1;
                            int num = count2 + 1;
                            sparseArray2.put(i3, Integer.valueOf(count2));
                            if (!pack.covers.isEmpty()) {
                                count = (int) Math.ceil(pack.covers.size() / this.stickersPerRow);
                                for (int b = 0; b < pack.covers.size(); b++) {
                                    this.cache.put(this.totalItems + b, pack.covers.get(b));
                                }
                            } else {
                                count = 1;
                                this.cache.put(this.totalItems, pack.cover);
                            }
                            int b2 = 0;
                            while (true) {
                                i = this.stickersPerRow;
                                if (b2 >= count * i) {
                                    break;
                                }
                                this.positionsToSets.put(this.totalItems + b2, pack);
                                b2++;
                            }
                            int b3 = this.totalItems;
                            this.totalItems = b3 + (i * count);
                            count2 = num;
                        }
                    }
                    notifyDataSetChanged();
                    return;
                }
                return;
            }
            this.endReached = true;
        }

        public void updateColors(RecyclerListView listView) {
            int size = listView.getChildCount();
            for (int i = 0; i < size; i++) {
                View child = listView.getChildAt(i);
                if (child instanceof FeaturedStickerSetInfoCell) {
                    ((FeaturedStickerSetInfoCell) child).updateColors();
                } else if (child instanceof FeaturedStickerSetCell2) {
                    ((FeaturedStickerSetCell2) child).updateColors();
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
            FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, listView, delegate);
            FeaturedStickerSetCell2.createThemeDescriptions(descriptions, listView, delegate);
            GraySectionCell.createThemeDescriptions(descriptions, listView);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
