package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.PaddedListAdapter;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes5.dex */
public class MentionsContainerView extends BlurredFrameLayout {
    private MentionsAdapter adapter;
    ChatActivity chatActivity;
    private Integer color;
    private float containerBottom;
    private float containerPadding;
    private float containerTop;
    private ExtendedGridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private MentionsListView listView;
    private SpringAnimation listViewTranslationAnimator;
    private PaddedListAdapter paddedAdapter;
    private Paint paint;
    private Path path;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private boolean shouldLiftMentions = false;
    private android.graphics.Rect rect = new android.graphics.Rect();
    private boolean ignoreLayout = false;
    private boolean scrollToFirst = false;
    private boolean shown = false;
    private Runnable updateVisibilityRunnable = new Runnable() { // from class: org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            MentionsContainerView.this.m2763lambda$new$0$orgtelegramuiComponentsMentionsContainerView();
        }
    };
    private int animationIndex = -1;
    private boolean listViewHiding = false;
    private float hideT = 0.0f;
    private boolean switchLayoutManagerOnEnd = false;
    private float listViewPadding = (int) Math.min(AndroidUtilities.dp(126.0f), AndroidUtilities.displaySize.y * 0.22f);

    public MentionsContainerView(Context context, long dialogId, int threadMessageId, final ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        super(context, chatActivity.contentView);
        this.chatActivity = chatActivity;
        this.sizeNotifierFrameLayout = chatActivity.contentView;
        this.resourcesProvider = resourcesProvider;
        this.drawBlur = false;
        this.isTopView = false;
        setVisibility(8);
        setWillNotDraw(false);
        MentionsListView mentionsListView = new MentionsListView(context, resourcesProvider);
        this.listView = mentionsListView;
        mentionsListView.setTranslationY(AndroidUtilities.dp(6.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.Components.MentionsContainerView.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager
            public void setReverseLayout(boolean reverseLayout) {
                super.setReverseLayout(reverseLayout);
                MentionsContainerView.this.listView.setTranslationY((reverseLayout ? -1 : 1) * AndroidUtilities.dp(6.0f));
            }
        };
        this.linearLayoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        ExtendedGridLayoutManager extendedGridLayoutManager = new ExtendedGridLayoutManager(context, 100, false, false) { // from class: org.telegram.ui.Components.MentionsContainerView.2
            private Size size = new Size();

            @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
            protected Size getSizeForItem(int i) {
                TLRPC.PhotoSize photoSize;
                if (i == 0) {
                    this.size.width = getWidth();
                    this.size.height = MentionsContainerView.this.paddedAdapter.getPadding();
                    return this.size;
                }
                int i2 = i - 1;
                if (MentionsContainerView.this.adapter.getBotContextSwitch() != null) {
                    i2++;
                }
                this.size.width = 0.0f;
                this.size.height = 0.0f;
                Object object = MentionsContainerView.this.adapter.getItem(i2);
                if (object instanceof TLRPC.BotInlineResult) {
                    TLRPC.BotInlineResult inlineResult = (TLRPC.BotInlineResult) object;
                    if (inlineResult.document != null) {
                        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(inlineResult.document.thumbs, 90);
                        float f = 100.0f;
                        this.size.width = thumb != null ? thumb.w : 100.0f;
                        Size size = this.size;
                        if (thumb != null) {
                            f = thumb.h;
                        }
                        size.height = f;
                        for (int b = 0; b < inlineResult.document.attributes.size(); b++) {
                            TLRPC.DocumentAttribute attribute = inlineResult.document.attributes.get(b);
                            if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                this.size.width = attribute.w;
                                this.size.height = attribute.h;
                                break;
                            }
                        }
                    } else if (inlineResult.content != null) {
                        for (int b2 = 0; b2 < inlineResult.content.attributes.size(); b2++) {
                            TLRPC.DocumentAttribute attribute2 = inlineResult.content.attributes.get(b2);
                            if ((attribute2 instanceof TLRPC.TL_documentAttributeImageSize) || (attribute2 instanceof TLRPC.TL_documentAttributeVideo)) {
                                this.size.width = attribute2.w;
                                this.size.height = attribute2.h;
                                break;
                            }
                        }
                    } else if (inlineResult.thumb != null) {
                        for (int b3 = 0; b3 < inlineResult.thumb.attributes.size(); b3++) {
                            TLRPC.DocumentAttribute attribute3 = inlineResult.thumb.attributes.get(b3);
                            if ((attribute3 instanceof TLRPC.TL_documentAttributeImageSize) || (attribute3 instanceof TLRPC.TL_documentAttributeVideo)) {
                                this.size.width = attribute3.w;
                                this.size.height = attribute3.h;
                                break;
                            }
                        }
                    } else if (inlineResult.photo != null && (photoSize = FileLoader.getClosestPhotoSizeWithSize(inlineResult.photo.sizes, AndroidUtilities.photoSize.intValue())) != null) {
                        this.size.width = photoSize.w;
                        this.size.height = photoSize.h;
                    }
                }
                return this.size;
            }

            @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
            public int getFlowItemCount() {
                if (MentionsContainerView.this.adapter.getBotContextSwitch() != null) {
                    return getItemCount() - 2;
                }
                return super.getFlowItemCount() - 1;
            }
        };
        this.gridLayoutManager = extendedGridLayoutManager;
        extendedGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.MentionsContainerView.3
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 100;
                }
                int position2 = position - 1;
                Object object = MentionsContainerView.this.adapter.getItem(position2);
                if (object instanceof TLRPC.TL_inlineBotSwitchPM) {
                    return 100;
                }
                if (!(object instanceof TLRPC.Document)) {
                    if (MentionsContainerView.this.adapter.getBotContextSwitch() != null) {
                        position2--;
                    }
                    return MentionsContainerView.this.gridLayoutManager.getSpanSizeForItem(position2);
                }
                return 20;
            }
        });
        DefaultItemAnimator mentionItemAnimator = new DefaultItemAnimator();
        mentionItemAnimator.setAddDuration(150L);
        mentionItemAnimator.setMoveDuration(150L);
        mentionItemAnimator.setChangeDuration(150L);
        mentionItemAnimator.setRemoveDuration(150L);
        mentionItemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        mentionItemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(mentionItemAnimator);
        this.listView.setClipToPadding(false);
        this.listView.setLayoutManager(this.linearLayoutManager);
        this.adapter = new MentionsAdapter(context, false, dialogId, threadMessageId, new MentionsAdapter.MentionsAdapterDelegate() { // from class: org.telegram.ui.Components.MentionsContainerView.4
            @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
            public void onItemCountUpdate(int oldCount, int newCount) {
                if (MentionsContainerView.this.listView.getLayoutManager() != MentionsContainerView.this.gridLayoutManager && MentionsContainerView.this.shown) {
                    AndroidUtilities.cancelRunOnUIThread(MentionsContainerView.this.updateVisibilityRunnable);
                    AndroidUtilities.runOnUIThread(MentionsContainerView.this.updateVisibilityRunnable, chatActivity.fragmentOpened ? 0L : 100L);
                }
            }

            @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
            public void needChangePanelVisibility(boolean show) {
                if (MentionsContainerView.this.getNeededLayoutManager() != MentionsContainerView.this.getCurrentLayoutManager() && MentionsContainerView.this.canOpen() && MentionsContainerView.this.adapter.getItemCountInternal() > 0) {
                    MentionsContainerView.this.switchLayoutManagerOnEnd = true;
                    MentionsContainerView.this.updateVisibility(false);
                    return;
                }
                if (show && !MentionsContainerView.this.canOpen()) {
                    show = false;
                }
                if (show && MentionsContainerView.this.adapter.getItemCountInternal() <= 0) {
                    show = false;
                }
                MentionsContainerView.this.updateVisibility(show);
            }

            @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
            public void onContextSearch(boolean searching) {
                MentionsContainerView.this.onContextSearch(searching);
            }

            @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
            public void onContextClick(TLRPC.BotInlineResult result) {
                MentionsContainerView.this.onContextClick(result);
            }
        }, resourcesProvider);
        PaddedListAdapter paddedListAdapter = new PaddedListAdapter(this.adapter);
        this.paddedAdapter = paddedListAdapter;
        this.listView.setAdapter(paddedListAdapter);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        setReversed(false);
    }

    protected boolean canOpen() {
        return true;
    }

    protected void onOpen() {
    }

    protected void onClose() {
    }

    protected void onContextSearch(boolean searching) {
    }

    protected void onContextClick(TLRPC.BotInlineResult result) {
    }

    public void onPanTransitionStart() {
        this.shouldLiftMentions = isReversed();
    }

    public void onPanTransitionUpdate(float translationY) {
        if (this.shouldLiftMentions) {
            setTranslationY(translationY);
        }
    }

    public void onPanTransitionEnd() {
    }

    public MentionsListView getListView() {
        return this.listView;
    }

    public MentionsAdapter getAdapter() {
        return this.adapter;
    }

    public void setReversed(boolean reversed) {
        this.scrollToFirst = true;
        this.linearLayoutManager.setReverseLayout(reversed);
        this.adapter.setIsReversed(reversed);
    }

    public boolean isReversed() {
        RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        LinearLayoutManager linearLayoutManager = this.linearLayoutManager;
        return layoutManager == linearLayoutManager && linearLayoutManager.getReverseLayout();
    }

    public LinearLayoutManager getCurrentLayoutManager() {
        RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        LinearLayoutManager linearLayoutManager = this.linearLayoutManager;
        return layoutManager == linearLayoutManager ? linearLayoutManager : this.gridLayoutManager;
    }

    public LinearLayoutManager getNeededLayoutManager() {
        return ((this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout()) ? this.gridLayoutManager : this.linearLayoutManager;
    }

    public float clipBottom() {
        if (getVisibility() == 0 && !isReversed()) {
            return getMeasuredHeight() - this.containerTop;
        }
        return 0.0f;
    }

    public float clipTop() {
        if (getVisibility() == 0 && isReversed()) {
            return this.containerBottom;
        }
        return 0.0f;
    }

    @Override // org.telegram.ui.Components.BlurredFrameLayout, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        float r;
        boolean reversed = isReversed();
        boolean topPadding = (this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout() && this.adapter.getBotContextSwitch() == null;
        this.containerPadding = AndroidUtilities.dp((topPadding ? 2 : 0) + 2);
        float r2 = AndroidUtilities.dp(4.0f);
        if (reversed) {
            int paddingViewTop = this.paddedAdapter.paddingViewAttached ? this.paddedAdapter.paddingView.getTop() : getHeight();
            float top = Math.min(Math.max(0.0f, paddingViewTop + this.listView.getTranslationY()) + this.containerPadding, (1.0f - this.hideT) * getHeight());
            android.graphics.Rect rect = this.rect;
            this.containerTop = 0.0f;
            int measuredWidth = getMeasuredWidth();
            this.containerBottom = top;
            rect.set(0, (int) 0.0f, measuredWidth, (int) top);
            float r3 = Math.min(r2, Math.abs(getMeasuredHeight() - this.containerBottom));
            if (r3 > 0.0f) {
                this.rect.top -= (int) r3;
            }
            r = r3;
        } else {
            if (this.listView.getLayoutManager() == this.gridLayoutManager) {
                this.containerPadding += AndroidUtilities.dp(2.0f);
                r2 += AndroidUtilities.dp(2.0f);
            }
            int paddingViewBottom = this.paddedAdapter.paddingViewAttached ? this.paddedAdapter.paddingView.getBottom() : 0;
            float top2 = Math.max(0.0f, paddingViewBottom + this.listView.getTranslationY()) - this.containerPadding;
            this.containerTop = top2;
            float top3 = Math.max(top2, this.hideT * getHeight());
            android.graphics.Rect rect2 = this.rect;
            this.containerTop = top3;
            int measuredWidth2 = getMeasuredWidth();
            float measuredHeight = getMeasuredHeight();
            this.containerBottom = measuredHeight;
            rect2.set(0, (int) top3, measuredWidth2, (int) measuredHeight);
            float r4 = Math.min(r2, Math.abs(this.containerTop));
            if (r4 > 0.0f) {
                this.rect.bottom += (int) r4;
            }
            r = r4;
        }
        if (this.paint == null) {
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setShadowLayer(AndroidUtilities.dp(4.0f), 0.0f, 0.0f, 503316480);
        }
        Paint paint2 = this.paint;
        Integer num = this.color;
        paint2.setColor(num != null ? num.intValue() : getThemedColor(Theme.key_chat_messagePanelBackground));
        if (SharedConfig.chatBlurEnabled() && this.sizeNotifierFrameLayout != null) {
            if (r > 0.0f) {
                canvas.save();
                Path path = this.path;
                if (path == null) {
                    this.path = new Path();
                } else {
                    path.reset();
                }
                AndroidUtilities.rectTmp.set(this.rect);
                this.path.addRoundRect(AndroidUtilities.rectTmp, r, r, Path.Direction.CW);
                canvas.clipPath(this.path);
            }
            this.sizeNotifierFrameLayout.drawBlurRect(canvas, getY(), this.rect, this.paint, reversed);
            if (r > 0.0f) {
                canvas.restore();
            }
        } else {
            AndroidUtilities.rectTmp.set(this.rect);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, this.paint);
        }
        canvas.save();
        canvas.clipRect(this.rect);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void setOverrideColor(int color) {
        this.color = Integer.valueOf(color);
        invalidate();
    }

    public void setIgnoreLayout(boolean ignore) {
        this.ignoreLayout = ignore;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-MentionsContainerView */
    public /* synthetic */ void m2763lambda$new$0$orgtelegramuiComponentsMentionsContainerView() {
        updateListViewTranslation(!this.shown, true);
    }

    public void updateVisibility(boolean show) {
        if (show) {
            boolean reversed = isReversed();
            if (!this.shown) {
                this.scrollToFirst = true;
                RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
                LinearLayoutManager linearLayoutManager = this.linearLayoutManager;
                if (layoutManager == linearLayoutManager) {
                    linearLayoutManager.scrollToPositionWithOffset(0, reversed ? -100000 : 100000);
                }
                if (getVisibility() == 8) {
                    this.hideT = 1.0f;
                    MentionsListView mentionsListView = this.listView;
                    mentionsListView.setTranslationY(reversed ? -(this.listViewPadding + AndroidUtilities.dp(12.0f)) : mentionsListView.computeVerticalScrollOffset() + this.listViewPadding);
                }
            }
            setVisibility(0);
        } else {
            this.scrollToFirst = false;
        }
        this.shown = show;
        AndroidUtilities.cancelRunOnUIThread(this.updateVisibilityRunnable);
        SpringAnimation springAnimation = this.listViewTranslationAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        AndroidUtilities.runOnUIThread(this.updateVisibilityRunnable, this.chatActivity.fragmentOpened ? 0L : 100L);
        if (show) {
            onOpen();
        } else {
            onClose();
        }
    }

    public boolean isOpen() {
        return this.shown;
    }

    private void updateListViewTranslation(final boolean forceZeroHeight, boolean animated) {
        float itemHeight;
        float newTranslationY;
        SpringAnimation springAnimation;
        if (this.listView == null || this.paddedAdapter == null) {
            return;
        }
        if (this.listViewHiding && (springAnimation = this.listViewTranslationAnimator) != null && springAnimation.isRunning() && forceZeroHeight) {
            return;
        }
        boolean reversed = isReversed();
        if (!forceZeroHeight) {
            itemHeight = (this.listView.computeVerticalScrollRange() - this.paddedAdapter.getPadding()) + this.containerPadding;
        } else {
            itemHeight = (-this.containerPadding) - AndroidUtilities.dp(6.0f);
        }
        float f = 0.0f;
        float f2 = this.listViewPadding;
        float newTranslationY2 = reversed ? -Math.max(0.0f, f2 - itemHeight) : Math.max(0.0f, f2 - itemHeight) + (-f2);
        if (forceZeroHeight && !reversed) {
            newTranslationY = newTranslationY2 + this.listView.computeVerticalScrollOffset();
        } else {
            newTranslationY = newTranslationY2;
        }
        setVisibility(0);
        SpringAnimation springAnimation2 = this.listViewTranslationAnimator;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
        }
        int i = 8;
        if (!animated) {
            if (forceZeroHeight) {
                f = 1.0f;
            }
            this.hideT = f;
            this.listView.setTranslationY(newTranslationY);
            if (forceZeroHeight) {
                setVisibility(8);
                return;
            }
            return;
        }
        this.listViewHiding = forceZeroHeight;
        final float fromTranslation = this.listView.getTranslationY();
        final float toTranslation = newTranslationY;
        final float fromHideT = this.hideT;
        final float toHideT = forceZeroHeight ? 1.0f : 0.0f;
        if (fromTranslation == toTranslation) {
            this.listViewTranslationAnimator = null;
            if (!forceZeroHeight) {
                i = 0;
            }
            setVisibility(i);
            if (this.switchLayoutManagerOnEnd && forceZeroHeight) {
                this.switchLayoutManagerOnEnd = false;
                this.listView.setLayoutManager(getNeededLayoutManager());
                this.shown = true;
                updateVisibility(true);
                return;
            }
            return;
        }
        final int account = UserConfig.selectedAccount;
        this.animationIndex = NotificationCenter.getInstance(account).setAnimationInProgress(this.animationIndex, null);
        SpringAnimation spring = new SpringAnimation(new FloatValueHolder(fromTranslation)).setSpring(new SpringForce(toTranslation).setDampingRatio(1.0f).setStiffness(550.0f));
        this.listViewTranslationAnimator = spring;
        spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda2
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f3, float f4) {
                MentionsContainerView.this.m2764xb07e35ab(fromHideT, toHideT, fromTranslation, toTranslation, dynamicAnimation, f3, f4);
            }
        });
        if (forceZeroHeight) {
            this.listViewTranslationAnimator.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda1
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
                    MentionsContainerView.this.m2765xa227dbca(forceZeroHeight, dynamicAnimation, z, f3, f4);
                }
            });
        }
        this.listViewTranslationAnimator.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda0
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
                MentionsContainerView.this.m2766x93d181e9(account, dynamicAnimation, z, f3, f4);
            }
        });
        this.listViewTranslationAnimator.start();
    }

    /* renamed from: lambda$updateListViewTranslation$1$org-telegram-ui-Components-MentionsContainerView */
    public /* synthetic */ void m2764xb07e35ab(float fromHideT, float toHideT, float fromTranslation, float toTranslation, DynamicAnimation anm, float val, float vel) {
        this.listView.setTranslationY(val);
        this.hideT = AndroidUtilities.lerp(fromHideT, toHideT, (val - fromTranslation) / (toTranslation - fromTranslation));
    }

    /* renamed from: lambda$updateListViewTranslation$2$org-telegram-ui-Components-MentionsContainerView */
    public /* synthetic */ void m2765xa227dbca(boolean forceZeroHeight, DynamicAnimation a, boolean cancelled, float b, float c) {
        if (!cancelled) {
            this.listViewTranslationAnimator = null;
            setVisibility(forceZeroHeight ? 8 : 0);
            if (this.switchLayoutManagerOnEnd && forceZeroHeight) {
                this.switchLayoutManagerOnEnd = false;
                this.listView.setLayoutManager(getNeededLayoutManager());
                this.shown = true;
                updateVisibility(true);
            }
        }
    }

    /* renamed from: lambda$updateListViewTranslation$3$org-telegram-ui-Components-MentionsContainerView */
    public /* synthetic */ void m2766x93d181e9(int account, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        NotificationCenter.getInstance(account).onAnimationFinish(this.animationIndex);
    }

    /* loaded from: classes5.dex */
    public class MentionsListView extends RecyclerListView {
        private boolean isDragging;
        private boolean isScrolling;
        private int lastHeight;
        private int lastWidth;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MentionsListView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            MentionsContainerView.this = this$0;
            setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.MentionsContainerView.MentionsListView.1
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    boolean z = false;
                    MentionsListView.this.isScrolling = newState != 0;
                    MentionsListView mentionsListView = MentionsListView.this;
                    if (newState == 1) {
                        z = true;
                    }
                    mentionsListView.isDragging = z;
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int lastVisibleItem = MentionsListView.this.getLayoutManager() == MentionsContainerView.this.gridLayoutManager ? MentionsContainerView.this.gridLayoutManager.findLastVisibleItemPosition() : MentionsContainerView.this.linearLayoutManager.findLastVisibleItemPosition();
                    int visibleItemCount = lastVisibleItem == -1 ? 0 : lastVisibleItem;
                    if (visibleItemCount > 0 && lastVisibleItem > MentionsContainerView.this.adapter.getLastItemCount() - 5) {
                        MentionsContainerView.this.adapter.searchForContextBotForNextOffset();
                    }
                }
            });
            addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.MentionsContainerView.MentionsListView.2
                @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position;
                    int i = 0;
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    if (parent.getLayoutManager() != MentionsContainerView.this.gridLayoutManager || (position = parent.getChildAdapterPosition(view)) == 0) {
                        return;
                    }
                    int position2 = position - 1;
                    if (!MentionsContainerView.this.adapter.isStickers()) {
                        if (MentionsContainerView.this.adapter.getBotContextSwitch() != null) {
                            if (position2 == 0) {
                                return;
                            }
                            position2--;
                            if (!MentionsContainerView.this.gridLayoutManager.isFirstRow(position2)) {
                                outRect.top = AndroidUtilities.dp(2.0f);
                            }
                        } else {
                            outRect.top = AndroidUtilities.dp(2.0f);
                        }
                        if (!MentionsContainerView.this.gridLayoutManager.isLastInRow(position2)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        outRect.right = i;
                    }
                }
            });
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() > MentionsContainerView.this.paddedAdapter.paddingView.getTop()) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() < MentionsContainerView.this.paddedAdapter.paddingView.getBottom()) {
                return false;
            }
            boolean result = !this.isScrolling && ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, MentionsContainerView.this.listView, 0, null, this.resourcesProvider);
            if ((MentionsContainerView.this.adapter.isStickers() && event.getAction() == 0) || event.getAction() == 2) {
                MentionsContainerView.this.adapter.doSomeStickersAction();
            }
            return super.onInterceptTouchEvent(event) || result;
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() > MentionsContainerView.this.paddedAdapter.paddingView.getTop()) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() < MentionsContainerView.this.paddedAdapter.paddingView.getBottom()) {
                return false;
            }
            return super.onTouchEvent(event);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (MentionsContainerView.this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int offset;
            int width = r - l;
            int height = b - t;
            boolean reversed = MentionsContainerView.this.isReversed();
            LinearLayoutManager layoutManager = MentionsContainerView.this.getCurrentLayoutManager();
            int position = reversed ? layoutManager.findFirstVisibleItemPosition() : layoutManager.findLastVisibleItemPosition();
            View child = layoutManager.findViewByPosition(position);
            if (child != null) {
                int offset2 = child.getTop() - (reversed ? 0 : this.lastHeight - height);
                offset = offset2;
            } else {
                offset = 0;
            }
            super.onLayout(changed, l, t, r, b);
            if (MentionsContainerView.this.scrollToFirst) {
                MentionsContainerView.this.ignoreLayout = true;
                layoutManager.scrollToPositionWithOffset(0, 100000);
                super.onLayout(false, l, t, r, b);
                MentionsContainerView.this.ignoreLayout = false;
                MentionsContainerView.this.scrollToFirst = false;
            } else if (position != -1 && width == this.lastWidth && height - this.lastHeight != 0) {
                MentionsContainerView.this.ignoreLayout = true;
                layoutManager.scrollToPositionWithOffset(position, offset, false);
                super.onLayout(false, l, t, r, b);
                MentionsContainerView.this.ignoreLayout = false;
            }
            this.lastHeight = height;
            this.lastWidth = width;
        }

        @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            MentionsContainerView.this.invalidate();
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public void onMeasure(int widthSpec, int heightSpec) {
            int height = View.MeasureSpec.getSize(heightSpec);
            if (MentionsContainerView.this.paddedAdapter != null) {
                MentionsContainerView.this.paddedAdapter.setPadding(height);
            }
            MentionsContainerView.this.listViewPadding = (int) Math.min(AndroidUtilities.dp(126.0f), AndroidUtilities.displaySize.y * 0.22f);
            super.onMeasure(widthSpec, View.MeasureSpec.makeMeasureSpec(((int) MentionsContainerView.this.listViewPadding) + height, C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // androidx.recyclerview.widget.RecyclerView
        public void onScrolled(int dx, int dy) {
            super.onScrolled(dx, dy);
            MentionsContainerView.this.invalidate();
        }
    }

    private Paint getThemedPaint(String paintKey) {
        Paint paint = this.resourcesProvider.getPaint(paintKey);
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
