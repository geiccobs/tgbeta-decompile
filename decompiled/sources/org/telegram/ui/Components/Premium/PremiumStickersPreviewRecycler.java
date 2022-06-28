package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class PremiumStickersPreviewRecycler extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate, PagerHeaderView {
    boolean autoPlayEnabled;
    private boolean checkEffect;
    private final int currentAccount;
    boolean haptic;
    boolean hasSelectedView;
    boolean isVisible;
    LinearLayoutManager layoutManager;
    View oldSelectedView;
    private int size;
    private final ArrayList<TLRPC.Document> premiumStickers = new ArrayList<>();
    boolean firstMeasure = true;
    boolean firstDraw = true;
    Runnable autoScrollRunnable = new Runnable() { // from class: org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler.1
        @Override // java.lang.Runnable
        public void run() {
            if (!PremiumStickersPreviewRecycler.this.autoPlayEnabled) {
                return;
            }
            if (!PremiumStickersPreviewRecycler.this.sortedView.isEmpty()) {
                StickerView stickerView = PremiumStickersPreviewRecycler.this.sortedView.get(PremiumStickersPreviewRecycler.this.sortedView.size() - 1);
                int adapterPosition = PremiumStickersPreviewRecycler.this.getChildAdapterPosition(stickerView);
                if (adapterPosition >= 0) {
                    View view = PremiumStickersPreviewRecycler.this.layoutManager.findViewByPosition(adapterPosition + 1);
                    if (view != null) {
                        PremiumStickersPreviewRecycler.this.haptic = false;
                        PremiumStickersPreviewRecycler.this.drawEffectForView(view, true);
                        PremiumStickersPreviewRecycler.this.smoothScrollBy(0, view.getTop() - ((PremiumStickersPreviewRecycler.this.getMeasuredHeight() - view.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
                    }
                }
            }
            PremiumStickersPreviewRecycler.this.scheduleAutoScroll();
        }
    };
    CubicBezierInterpolator interpolator = new CubicBezierInterpolator(0.0f, 0.5f, 0.5f, 1.0f);
    ArrayList<StickerView> sortedView = new ArrayList<>();
    Comparator<StickerView> comparator = PremiumStickersPreviewRecycler$$ExternalSyntheticLambda1.INSTANCE;
    int selectStickerOnNextLayout = -1;

    public static /* synthetic */ int lambda$new$0(StickerView o1, StickerView o2) {
        return (int) ((o1.progress * 100.0f) - (o2.progress * 100.0f));
    }

    public PremiumStickersPreviewRecycler(Context context, int currentAccount) {
        super(context);
        this.currentAccount = currentAccount;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        setLayoutManager(linearLayoutManager);
        setAdapter(new Adapter());
        setClipChildren(false);
        setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getScrollState() == 1) {
                    PremiumStickersPreviewRecycler.this.drawEffectForView(null, true);
                }
                PremiumStickersPreviewRecycler.this.invalidate();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {
                    PremiumStickersPreviewRecycler.this.haptic = true;
                }
                if (newState == 0) {
                    StickerView scrollToView = null;
                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        StickerView view = (StickerView) PremiumStickersPreviewRecycler.this.getChildAt(i);
                        if (scrollToView == null || view.progress > scrollToView.progress) {
                            scrollToView = view;
                        }
                    }
                    if (scrollToView != null) {
                        PremiumStickersPreviewRecycler.this.drawEffectForView(scrollToView, true);
                        PremiumStickersPreviewRecycler.this.haptic = false;
                        PremiumStickersPreviewRecycler.this.smoothScrollBy(0, scrollToView.getTop() - ((PremiumStickersPreviewRecycler.this.getMeasuredHeight() - scrollToView.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
                    }
                    PremiumStickersPreviewRecycler.this.scheduleAutoScroll();
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(PremiumStickersPreviewRecycler.this.autoScrollRunnable);
            }
        });
        setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                PremiumStickersPreviewRecycler.this.m2905x52e0d79d(view, i);
            }
        });
        MediaDataController.getInstance(currentAccount).preloadPremiumPreviewStickers();
        setStickers();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-PremiumStickersPreviewRecycler */
    public /* synthetic */ void m2905x52e0d79d(View view, int position) {
        if (view != null) {
            drawEffectForView(view, true);
            this.haptic = false;
            smoothScrollBy(0, view.getTop() - ((getMeasuredHeight() - view.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        if (View.MeasureSpec.getSize(heightSpec) > View.MeasureSpec.getSize(widthSpec)) {
            this.size = View.MeasureSpec.getSize(widthSpec);
        } else {
            this.size = View.MeasureSpec.getSize(heightSpec);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    public void scheduleAutoScroll() {
        if (!this.autoPlayEnabled) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        AndroidUtilities.runOnUIThread(this.autoScrollRunnable, 2700L);
    }

    public void drawEffectForView(View view, boolean animated) {
        this.hasSelectedView = view != null;
        for (int i = 0; i < getChildCount(); i++) {
            StickerView child = (StickerView) getChildAt(i);
            if (child != view) {
                child.setDrawImage(!this.hasSelectedView, false, animated);
            } else {
                child.setDrawImage(true, true, animated);
            }
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.firstMeasure && !this.premiumStickers.isEmpty() && getChildCount() > 0) {
            this.firstMeasure = false;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PremiumStickersPreviewRecycler.this.m2906xf073dcc1();
                }
            });
        }
        int i = this.selectStickerOnNextLayout;
        if (i > 0) {
            RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(i);
            if (holder != null) {
                drawEffectForView(holder.itemView, false);
            }
            this.selectStickerOnNextLayout = -1;
        }
    }

    /* renamed from: lambda$onLayout$2$org-telegram-ui-Components-Premium-PremiumStickersPreviewRecycler */
    public /* synthetic */ void m2906xf073dcc1() {
        int startPosition = 1073741823 - (1073741823 % this.premiumStickers.size());
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        this.selectStickerOnNextLayout = startPosition;
        linearLayoutManager.scrollToPositionWithOffset(startPosition, (getMeasuredHeight() - getChildAt(0).getMeasuredHeight()) >> 1);
        drawEffectForView(null, false);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        ArrayList<StickerView> arrayList;
        ArrayList<StickerView> arrayList2;
        ArrayList<StickerView> arrayList3;
        if (!this.isVisible) {
            return;
        }
        this.sortedView.clear();
        for (int i = 0; i < getChildCount(); i++) {
            StickerView child = (StickerView) getChildAt(i);
            float cY = (getMeasuredHeight() >> 1) + child.getMeasuredHeight();
            float p = ((child.getTop() + child.getMeasuredHeight()) + (child.getMeasuredHeight() >> 1)) / cY;
            if (p > 1.0f) {
                p = 2.0f - p;
            }
            float p2 = Utilities.clamp(p, 1.0f, 0.0f);
            child.progress = p2;
            child.view.setTranslationX((-getMeasuredWidth()) * 2.0f * (1.0f - this.interpolator.getInterpolation(p2)));
            this.sortedView.add(child);
        }
        Collections.sort(this.sortedView, this.comparator);
        if ((this.firstDraw || this.checkEffect) && this.sortedView.size() > 0 && !this.premiumStickers.isEmpty()) {
            StickerView stickerView = this.sortedView.get(arrayList3.size() - 1);
            this.oldSelectedView = stickerView;
            drawEffectForView(stickerView, !this.firstDraw);
            this.firstDraw = false;
            this.checkEffect = false;
        } else {
            if (this.oldSelectedView != this.sortedView.get(arrayList.size() - 1)) {
                this.oldSelectedView = this.sortedView.get(arrayList2.size() - 1);
                if (this.haptic) {
                    performHapticFeedback(3);
                }
            }
        }
        for (int i2 = 0; i2 < this.sortedView.size(); i2++) {
            canvas.save();
            canvas.translate(this.sortedView.get(i2).getX(), this.sortedView.get(i2).getY());
            this.sortedView.get(i2).draw(canvas);
            canvas.restore();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return true;
    }

    public void setOffset(float translationX) {
        float p = Math.abs(translationX / getMeasuredWidth());
        boolean localVisible = p < 1.0f;
        if (this.isVisible != localVisible) {
            this.isVisible = localVisible;
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            PremiumStickersPreviewRecycler.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new StickerView(parent.getContext());
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (PremiumStickersPreviewRecycler.this.premiumStickers.isEmpty()) {
                return;
            }
            StickerView stickerView = (StickerView) holder.itemView;
            stickerView.setSticker((TLRPC.Document) PremiumStickersPreviewRecycler.this.premiumStickers.get(position % PremiumStickersPreviewRecycler.this.premiumStickers.size()));
            stickerView.setDrawImage(!PremiumStickersPreviewRecycler.this.hasSelectedView, false, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumStickersPreviewLoaded);
        scheduleAutoScroll();
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumStickersPreviewLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.premiumStickersPreviewLoaded) {
            setStickers();
        }
    }

    private void setStickers() {
        this.premiumStickers.clear();
        this.premiumStickers.addAll(MediaDataController.getInstance(this.currentAccount).premiumPreviewStickers);
        getAdapter().notifyDataSetChanged();
        invalidate();
    }

    /* loaded from: classes5.dex */
    public class StickerView extends FrameLayout {
        boolean animateImage = true;
        private float animateImageProgress;
        ImageReceiver centerImage;
        TLRPC.Document document;
        boolean drawEffect;
        ImageReceiver effectImage;
        private float effectProgress;
        public float progress;
        boolean update;
        View view;

        static /* synthetic */ float access$416(StickerView x0, float x1) {
            float f = x0.effectProgress + x1;
            x0.effectProgress = f;
            return f;
        }

        static /* synthetic */ float access$424(StickerView x0, float x1) {
            float f = x0.effectProgress - x1;
            x0.effectProgress = f;
            return f;
        }

        static /* synthetic */ float access$516(StickerView x0, float x1) {
            float f = x0.animateImageProgress + x1;
            x0.animateImageProgress = f;
            return f;
        }

        static /* synthetic */ float access$524(StickerView x0, float x1) {
            float f = x0.animateImageProgress - x1;
            x0.animateImageProgress = f;
            return f;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public StickerView(Context context) {
            super(context);
            PremiumStickersPreviewRecycler.this = r4;
            this.view = new View(context) { // from class: org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler.StickerView.1
                @Override // android.view.View
                public void draw(Canvas canvas) {
                    super.draw(canvas);
                    if (StickerView.this.update) {
                        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(StickerView.this.document, Theme.key_windowBackgroundGray, 0.5f);
                        StickerView.this.centerImage.setImage(ImageLocation.getForDocument(StickerView.this.document), null, svgThumb, "webp", null, 1);
                        if (MessageObject.isPremiumSticker(StickerView.this.document)) {
                            StickerView.this.effectImage.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(StickerView.this.document), StickerView.this.document), "140_140", (ImageLocation) null, (String) null, "tgs", (Object) null, 1);
                        }
                    }
                    if (StickerView.this.drawEffect) {
                        if (StickerView.this.effectProgress == 0.0f) {
                            StickerView.this.effectProgress = 1.0f;
                            if (StickerView.this.effectImage.getLottieAnimation() != null) {
                                StickerView.this.effectImage.getLottieAnimation().setCurrentFrame(0, false);
                            }
                        }
                        if (StickerView.this.effectImage.getLottieAnimation() != null) {
                            StickerView.this.effectImage.getLottieAnimation().start();
                        }
                        if (StickerView.this.effectImage.getLottieAnimation() != null && StickerView.this.effectImage.getLottieAnimation().isLastFrame() && PremiumStickersPreviewRecycler.this.autoPlayEnabled) {
                            AndroidUtilities.cancelRunOnUIThread(PremiumStickersPreviewRecycler.this.autoScrollRunnable);
                            AndroidUtilities.runOnUIThread(PremiumStickersPreviewRecycler.this.autoScrollRunnable, 0L);
                        }
                    } else if (StickerView.this.effectImage.getLottieAnimation() != null) {
                        StickerView.this.effectImage.getLottieAnimation().stop();
                    }
                    if (StickerView.this.animateImage) {
                        if (StickerView.this.centerImage.getLottieAnimation() != null) {
                            StickerView.this.centerImage.getLottieAnimation().start();
                        }
                    } else if (StickerView.this.centerImage.getLottieAnimation() != null) {
                        StickerView.this.centerImage.getLottieAnimation().stop();
                    }
                    if (StickerView.this.animateImage && StickerView.this.animateImageProgress != 1.0f) {
                        StickerView.access$516(StickerView.this, 0.10666667f);
                        invalidate();
                    } else if (!StickerView.this.animateImage && StickerView.this.animateImageProgress != 0.0f) {
                        StickerView.access$524(StickerView.this, 0.10666667f);
                        invalidate();
                    }
                    StickerView stickerView = StickerView.this;
                    stickerView.animateImageProgress = Utilities.clamp(stickerView.animateImageProgress, 1.0f, 0.0f);
                    if (StickerView.this.drawEffect && StickerView.this.effectProgress != 1.0f) {
                        StickerView.access$416(StickerView.this, 0.10666667f);
                        invalidate();
                    } else if (!StickerView.this.drawEffect && StickerView.this.effectProgress != 0.0f) {
                        StickerView.access$424(StickerView.this, 0.10666667f);
                        invalidate();
                    }
                    StickerView stickerView2 = StickerView.this;
                    stickerView2.effectProgress = Utilities.clamp(stickerView2.effectProgress, 1.0f, 0.0f);
                    float smallImageSize = PremiumStickersPreviewRecycler.this.size * 0.45f;
                    float size = 1.499267f * smallImageSize;
                    float x = getMeasuredWidth() - size;
                    float y = (getMeasuredHeight() - size) / 2.0f;
                    StickerView.this.centerImage.setImageCoords(((size - smallImageSize) - (0.02f * size)) + x, ((size - smallImageSize) / 2.0f) + y, smallImageSize, smallImageSize);
                    StickerView.this.centerImage.setAlpha((StickerView.this.animateImageProgress * 0.7f) + 0.3f);
                    StickerView.this.centerImage.draw(canvas);
                    if (StickerView.this.effectProgress != 0.0f) {
                        StickerView.this.effectImage.setImageCoords(x, y, size, size);
                        StickerView.this.effectImage.setAlpha(StickerView.this.effectProgress);
                        StickerView.this.effectImage.draw(canvas);
                    }
                }
            };
            this.centerImage = new ImageReceiver(this.view);
            this.effectImage = new ImageReceiver(this.view);
            this.centerImage.setAllowStartAnimation(false);
            this.effectImage.setAllowStartAnimation(false);
            setClipChildren(false);
            addView(this.view, LayoutHelper.createFrame(-1, -2, 21));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int size = (int) (PremiumStickersPreviewRecycler.this.size * 0.6f);
            ViewGroup.LayoutParams layoutParams = this.view.getLayoutParams();
            ViewGroup.LayoutParams layoutParams2 = this.view.getLayoutParams();
            int dp = size - AndroidUtilities.dp(16.0f);
            layoutParams2.height = dp;
            layoutParams.width = dp;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (size * 0.7f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setSticker(TLRPC.Document document) {
            this.document = document;
            this.update = true;
        }

        public void setDrawImage(boolean animateImage, boolean drawEffect, boolean animated) {
            float f = 1.0f;
            if (this.drawEffect != drawEffect) {
                this.drawEffect = drawEffect;
                if (!animated) {
                    this.effectProgress = drawEffect ? 1.0f : 0.0f;
                }
                this.view.invalidate();
            }
            if (this.animateImage != animateImage) {
                this.animateImage = animateImage;
                if (!animated) {
                    if (!animateImage) {
                        f = 0.0f;
                    }
                    this.animateImageProgress = f;
                }
                this.view.invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.centerImage.onAttachedToWindow();
            this.effectImage.onAttachedToWindow();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.centerImage.onDetachedFromWindow();
            this.effectImage.onDetachedFromWindow();
        }
    }

    public void setAutoPlayEnabled(boolean b) {
        if (this.autoPlayEnabled != b) {
            this.autoPlayEnabled = b;
            if (b) {
                scheduleAutoScroll();
                this.checkEffect = true;
                invalidate();
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            drawEffectForView(null, true);
        }
    }
}
