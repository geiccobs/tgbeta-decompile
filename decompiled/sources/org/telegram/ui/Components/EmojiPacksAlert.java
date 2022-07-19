package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class EmojiPacksAlert extends BottomSheet {
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private TextView buttonView;
    private EmojiPacksLoader customEmojiPacks;
    private BaseFragment fragment;
    private GridLayoutManager gridLayoutManager;
    private RecyclerListView listView;
    private View paddingView;
    private PremiumButtonView premiumButtonView;
    private View shadowView;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public EmojiPacksAlert(BaseFragment baseFragment, Context context, Theme.ResourcesProvider resourcesProvider, ArrayList<TLRPC$InputStickerSet> arrayList) {
        super(context, false, resourcesProvider);
        boolean z = arrayList.size() <= 1;
        this.fragment = baseFragment;
        fixNavigationBar();
        this.customEmojiPacks = new EmojiPacksLoader(this.currentAccount, arrayList) { // from class: org.telegram.ui.Components.EmojiPacksAlert.1
            @Override // org.telegram.ui.Components.EmojiPacksLoader
            protected void onUpdate() {
                if (EmojiPacksAlert.this.listView != null && EmojiPacksAlert.this.listView.getAdapter() != null) {
                    EmojiPacksAlert.this.listView.getAdapter().notifyDataSetChanged();
                }
                EmojiPacksAlert.this.updateButton();
            }
        };
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiPacksAlert.2
            boolean attached;
            private Paint paint = new Paint();
            private Path path = new Path();
            private Boolean lastOpen = null;
            SparseArray<ArrayList<EmojiImageView>> viewsGroupedByLines = new SparseArray<>();
            ArrayList<DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
            ArrayList<ArrayList<EmojiImageView>> unusedArrays = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                if (!this.attached) {
                    return;
                }
                this.paint.setColor(EmojiPacksAlert.this.getThemedColor("dialogBackground"));
                this.paint.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(-0.66f), 503316480);
                this.path.reset();
                float top = EmojiPacksAlert.this.getTop();
                float clamp = 1.0f - MathUtils.clamp((top - ((BottomSheet) EmojiPacksAlert.this).containerView.getPaddingTop()) / AndroidUtilities.dp(32.0f), 0.0f, 1.0f);
                float paddingTop = top - (((BottomSheet) EmojiPacksAlert.this).containerView.getPaddingTop() * clamp);
                float dp = AndroidUtilities.dp((1.0f - clamp) * 14.0f);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(getPaddingLeft(), paddingTop, getWidth() - getPaddingRight(), getBottom() + dp);
                this.path.addRoundRect(rectF, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                boolean z2 = clamp > 0.75f;
                Boolean bool = this.lastOpen;
                if (bool == null || z2 != bool.booleanValue()) {
                    EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                    Boolean valueOf = Boolean.valueOf(z2);
                    this.lastOpen = valueOf;
                    emojiPacksAlert.updateLightStatusBar(valueOf.booleanValue());
                }
                Theme.dialogs_onlineCirclePaint.setColor(EmojiPacksAlert.this.getThemedColor("key_sheet_scrollUp"));
                Theme.dialogs_onlineCirclePaint.setAlpha((int) (MathUtils.clamp(paddingTop / AndroidUtilities.dp(20.0f), 0.0f, 1.0f) * Theme.dialogs_onlineCirclePaint.getAlpha()));
                int dp2 = AndroidUtilities.dp(36.0f);
                float dp3 = paddingTop + AndroidUtilities.dp(10.0f);
                rectF.set((getMeasuredWidth() - dp2) / 2, dp3, (getMeasuredWidth() + dp2) / 2, AndroidUtilities.dp(4.0f) + dp3);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                EmojiPacksAlert.this.shadowView.setVisibility(EmojiPacksAlert.this.listView.canScrollVertically(1) ? 0 : 4);
                if (EmojiPacksAlert.this.listView != null) {
                    canvas.save();
                    canvas.translate(EmojiPacksAlert.this.listView.getLeft(), EmojiPacksAlert.this.listView.getTop());
                    canvas.clipRect(0, 0, EmojiPacksAlert.this.listView.getWidth(), EmojiPacksAlert.this.listView.getHeight());
                    for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                        ArrayList<EmojiImageView> valueAt = this.viewsGroupedByLines.valueAt(i);
                        valueAt.clear();
                        this.unusedArrays.add(valueAt);
                    }
                    this.viewsGroupedByLines.clear();
                    if (EmojiPacksAlert.this.listView != null) {
                        for (int i2 = 0; i2 < EmojiPacksAlert.this.listView.getChildCount(); i2++) {
                            View childAt = EmojiPacksAlert.this.listView.getChildAt(i2);
                            if (childAt instanceof EmojiImageView) {
                                if (EmojiPacksAlert.this.animatedEmojiDrawables == null) {
                                    EmojiPacksAlert.this.animatedEmojiDrawables = new LongSparseArray();
                                }
                                AnimatedEmojiSpan animatedEmojiSpan = ((EmojiImageView) childAt).span;
                                if (animatedEmojiSpan != null) {
                                    long documentId = animatedEmojiSpan.getDocumentId();
                                    AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(documentId);
                                    if (animatedEmojiDrawable == null) {
                                        LongSparseArray longSparseArray = EmojiPacksAlert.this.animatedEmojiDrawables;
                                        AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(2, documentId);
                                        longSparseArray.put(documentId, make);
                                        animatedEmojiDrawable = make;
                                    }
                                    animatedEmojiDrawable.addView(this);
                                    ArrayList<EmojiImageView> arrayList2 = this.viewsGroupedByLines.get(childAt.getTop());
                                    if (arrayList2 == null) {
                                        if (!this.unusedArrays.isEmpty()) {
                                            ArrayList<ArrayList<EmojiImageView>> arrayList3 = this.unusedArrays;
                                            arrayList2 = arrayList3.remove(arrayList3.size() - 1);
                                        } else {
                                            arrayList2 = new ArrayList<>();
                                        }
                                        this.viewsGroupedByLines.put(childAt.getTop(), arrayList2);
                                    }
                                    arrayList2.add((EmojiImageView) childAt);
                                }
                            } else {
                                canvas.save();
                                canvas.translate(childAt.getLeft(), childAt.getTop());
                                childAt.draw(canvas);
                                canvas.restore();
                            }
                        }
                    }
                    this.lineDrawablesTmp.clear();
                    this.lineDrawablesTmp.addAll(this.lineDrawables);
                    this.lineDrawables.clear();
                    long currentTimeMillis = System.currentTimeMillis();
                    int i3 = 0;
                    while (true) {
                        DrawingInBackgroundLine drawingInBackgroundLine = null;
                        if (i3 < this.viewsGroupedByLines.size()) {
                            ArrayList<EmojiImageView> valueAt2 = this.viewsGroupedByLines.valueAt(i3);
                            EmojiImageView emojiImageView = valueAt2.get(0);
                            int childAdapterPosition = EmojiPacksAlert.this.listView.getChildAdapterPosition(emojiImageView);
                            int i4 = 0;
                            while (true) {
                                if (i4 >= this.lineDrawablesTmp.size()) {
                                    break;
                                } else if (this.lineDrawablesTmp.get(i4).position == childAdapterPosition) {
                                    drawingInBackgroundLine = this.lineDrawablesTmp.get(i4);
                                    this.lineDrawablesTmp.remove(i4);
                                    break;
                                } else {
                                    i4++;
                                }
                            }
                            if (drawingInBackgroundLine == null) {
                                if (!this.unusedLineDrawables.isEmpty()) {
                                    ArrayList<DrawingInBackgroundLine> arrayList4 = this.unusedLineDrawables;
                                    drawingInBackgroundLine = arrayList4.remove(arrayList4.size() - 1);
                                } else {
                                    drawingInBackgroundLine = new DrawingInBackgroundLine();
                                }
                                drawingInBackgroundLine.position = childAdapterPosition;
                                drawingInBackgroundLine.onAttachToWindow();
                            }
                            this.lineDrawables.add(drawingInBackgroundLine);
                            drawingInBackgroundLine.imageViewEmojis = valueAt2;
                            canvas.save();
                            canvas.translate(0.0f, emojiImageView.getY() + emojiImageView.getPaddingTop());
                            drawingInBackgroundLine.draw(canvas, currentTimeMillis, getMeasuredWidth(), emojiImageView.getMeasuredHeight() - emojiImageView.getPaddingBottom(), 1.0f);
                            canvas.restore();
                            i3++;
                        }
                    }
                    for (int i5 = 0; i5 < this.lineDrawablesTmp.size(); i5++) {
                        if (this.unusedLineDrawables.size() < 3) {
                            this.unusedLineDrawables.add(this.lineDrawablesTmp.get(i5));
                            this.lineDrawablesTmp.get(i5).imageViewEmojis = null;
                            this.lineDrawablesTmp.get(i5).reset();
                        } else {
                            this.lineDrawablesTmp.get(i5).onDetachFromWindow();
                        }
                    }
                    this.lineDrawablesTmp.clear();
                    canvas.restore();
                }
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < EmojiPacksAlert.this.getTop() - AndroidUtilities.dp(6.0f)) {
                    EmojiPacksAlert.this.dismiss();
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            /* renamed from: org.telegram.ui.Components.EmojiPacksAlert$2$DrawingInBackgroundLine */
            /* loaded from: classes3.dex */
            public class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
                ArrayList<EmojiImageView> drawInBackgroundViews = new ArrayList<>();
                ArrayList<EmojiImageView> imageViewEmojis;
                public int position;

                DrawingInBackgroundLine() {
                    AnonymousClass2.this = r1;
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void prepareDraw(long j) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    this.drawInBackgroundViews.clear();
                    for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                        EmojiImageView emojiImageView = this.imageViewEmojis.get(i);
                        if (emojiImageView.span != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(emojiImageView.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null) {
                            animatedEmojiDrawable.update(j);
                            ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = animatedEmojiDrawable.getImageReceiver().setDrawInBackgroundThread(emojiImageView.backgroundThreadDrawHolder);
                            emojiImageView.backgroundThreadDrawHolder = drawInBackgroundThread;
                            drawInBackgroundThread.time = j;
                            animatedEmojiDrawable.setAlpha(255);
                            android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                            rect.set(emojiImageView.getLeft() + emojiImageView.getPaddingLeft(), emojiImageView.getPaddingTop(), emojiImageView.getRight() - emojiImageView.getPaddingRight(), emojiImageView.getMeasuredHeight() - emojiImageView.getPaddingBottom());
                            emojiImageView.backgroundThreadDrawHolder.setBounds(rect);
                            emojiImageView.imageReceiver = animatedEmojiDrawable.getImageReceiver();
                            this.drawInBackgroundViews.add(emojiImageView);
                        }
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void drawInBackground(Canvas canvas) {
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        EmojiImageView emojiImageView = this.drawInBackgroundViews.get(i);
                        emojiImageView.imageReceiver.draw(canvas, emojiImageView.backgroundThreadDrawHolder);
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                protected void drawInUiThread(Canvas canvas) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    if (this.imageViewEmojis != null) {
                        for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                            EmojiImageView emojiImageView = this.imageViewEmojis.get(i);
                            if (emojiImageView.span != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(emojiImageView.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null && emojiImageView.imageReceiver != null) {
                                animatedEmojiDrawable.setAlpha(255);
                                android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                                rect.set(emojiImageView.getLeft() + emojiImageView.getPaddingLeft(), emojiImageView.getPaddingTop(), emojiImageView.getRight() - emojiImageView.getPaddingRight(), emojiImageView.getMeasuredHeight() - emojiImageView.getPaddingBottom());
                                animatedEmojiDrawable.getImageReceiver().setImageCoords(rect);
                                animatedEmojiDrawable.getImageReceiver().draw(canvas);
                            }
                        }
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void onFrameReady() {
                    super.onFrameReady();
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        this.drawInBackgroundViews.get(i).backgroundThreadDrawHolder.release();
                    }
                    ((BottomSheet) EmojiPacksAlert.this).containerView.invalidate();
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.attached = true;
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.attached = false;
                for (int i = 0; i < this.lineDrawables.size(); i++) {
                    this.lineDrawables.get(i).onDetachFromWindow();
                }
                for (int i2 = 0; i2 < this.unusedLineDrawables.size(); i2++) {
                    this.unusedLineDrawables.get(i2).onDetachFromWindow();
                }
                this.lineDrawables.clear();
            }
        };
        this.paddingView = new View(this, context) { // from class: org.telegram.ui.Components.EmojiPacksAlert.3
            @Override // android.view.View
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.displaySize.y * 0.4f), 1073741824));
            }
        };
        this.listView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiPacksAlert.4
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                return false;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i, int i2) {
                EmojiPacksAlert.this.gridLayoutManager.setSpanCount(Math.max(1, View.MeasureSpec.getSize(i) / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
                super.onMeasure(i, i2);
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
                ((BottomSheet) EmojiPacksAlert.this).containerView.invalidate();
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AnimatedEmojiSpan.release(((BottomSheet) EmojiPacksAlert.this).containerView, EmojiPacksAlert.this.animatedEmojiDrawables);
            }
        };
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, AndroidUtilities.statusBarHeight, i, 0);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        this.containerView.setWillNotDraw(false);
        this.listView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), z ? 0 : AndroidUtilities.dp(16.0f));
        this.listView.setAdapter(new Adapter());
        RecyclerListView recyclerListView = this.listView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8);
        this.gridLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.gridLayoutManager.setReverseLayout(false);
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiPacksAlert.5
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i2) {
                if (EmojiPacksAlert.this.listView.getAdapter().getItemViewType(i2) != 1) {
                    return EmojiPacksAlert.this.gridLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, z ? 68.0f : 0.0f));
        View view = new View(context);
        this.shadowView = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.shadowView, LayoutHelper.createFrame(-1, 1.0f / AndroidUtilities.density, 80, 0.0f, 0.0f, 0.0f, z ? 68.0f : 0.0f));
        if (z) {
            TextView textView = new TextView(context);
            this.buttonView = textView;
            textView.setTextColor(getThemedColor("featuredStickers_buttonText"));
            this.buttonView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.buttonView.setGravity(17);
            this.buttonView.setClickable(true);
            this.containerView.addView(this.buttonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
            PremiumButtonView premiumButtonView = new PremiumButtonView(context, false);
            this.premiumButtonView = premiumButtonView;
            premiumButtonView.setIcon(R.raw.unlock_icon);
            this.containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
            updateButton();
        }
    }

    public void showPremiumAlert() {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            new PremiumFeatureBottomSheet(baseFragment, 11, false).show();
        } else if (!(getContext() instanceof LaunchActivity)) {
        } else {
            ((LaunchActivity) getContext()).lambda$runLinkRequest$59(new PremiumPreviewFragment(null));
        }
    }

    public void updateLightStatusBar(boolean z) {
        boolean z2 = true;
        boolean z3 = AndroidUtilities.computePerceivedBrightness(getThemedColor("dialogBackground")) > 0.721f;
        if (AndroidUtilities.computePerceivedBrightness(Theme.blendOver(getThemedColor("actionBarDefault"), AndroidUtilities.DARK_STATUS_BAR_OVERLAY)) <= 0.721f) {
            z2 = false;
        }
        if (!z) {
            z3 = z2;
        }
        AndroidUtilities.setLightStatusBar(getWindow(), z3);
    }

    public boolean isInstalled(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;
        if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.set != null && (stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(5)) != null) {
            for (int i = 0; i < stickerSets.size(); i++) {
                if (stickerSets.get(i).set.id == tLRPC$TL_messages_stickerSet.set.id) {
                    return true;
                }
            }
        }
        return false;
    }

    public void installSet(final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final BaseFragment baseFragment) {
        if (tLRPC$TL_messages_stickerSet == null || MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(tLRPC$TL_messages_stickerSet.set.id)) {
            return;
        }
        TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_installStickerSet, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                EmojiPacksAlert.this.lambda$installSet$1(tLRPC$TL_messages_stickerSet, baseFragment, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$installSet$1(final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final BaseFragment baseFragment, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                EmojiPacksAlert.this.lambda$installSet$0(tLRPC$TL_messages_stickerSet, tLRPC$TL_error, baseFragment, tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$installSet$0(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLObject tLObject) {
        int i;
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        try {
            if (tLRPC$TL_error == null) {
                if (baseFragment != null) {
                    Bulletin.make(baseFragment, new StickerSetBulletinLayout(getContext(), tLRPC$TL_messages_stickerSet, 2, null, this.resourcesProvider), 1500).show();
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.fragment, true, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
                }
            } else {
                Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        MediaDataController.getInstance(this.currentAccount).loadStickers(i, false, true);
    }

    public void uninstallSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, BaseFragment baseFragment, Runnable runnable) {
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), tLRPC$TL_messages_stickerSet, tLRPC$TL_messages_stickerSet.set.official ? 1 : 0, baseFragment, true, baseFragment != null, runnable);
    }

    public void updateButton() {
        if (this.buttonView == null) {
            return;
        }
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList = this.customEmojiPacks.stickerSets;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = arrayList.get(i);
            if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.set != null) {
                if (!isInstalled(tLRPC$TL_messages_stickerSet)) {
                    arrayList3.add(tLRPC$TL_messages_stickerSet);
                } else {
                    arrayList2.add(tLRPC$TL_messages_stickerSet);
                }
            }
        }
        boolean z = false;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = arrayList.get(i2);
            if (tLRPC$TL_messages_stickerSet2 != null && tLRPC$TL_messages_stickerSet2.documents != null) {
                for (int i3 = 0; i3 < tLRPC$TL_messages_stickerSet2.documents.size(); i3++) {
                    if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet2.documents.get(i3))) {
                        z = true;
                    }
                }
            }
        }
        if (z && !UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.premiumButtonView.setVisibility(0);
            this.buttonView.setVisibility(4);
            this.premiumButtonView.setButton(LocaleController.getString("UnlockPremiumEmoji", R.string.UnlockPremiumEmoji), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiPacksAlert.this.lambda$updateButton$2(view);
                }
            });
            return;
        }
        this.premiumButtonView.setVisibility(4);
        this.buttonView.setVisibility(0);
        if (arrayList3.size() > 0) {
            this.buttonView.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 6.0f));
            this.buttonView.setTextColor(getThemedColor("featuredStickers_buttonText"));
            if (arrayList3.size() == 1) {
                this.buttonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList3.get(0)).documents.size(), new Object[0])));
            } else {
                this.buttonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList3.size(), new Object[0])));
            }
            this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiPacksAlert.this.lambda$updateButton$3(arrayList3, view);
                }
            });
            return;
        }
        this.buttonView.setBackground(Theme.createRadSelectorDrawable(268435455 & getThemedColor("dialogTextRed"), 6, 6));
        this.buttonView.setTextColor(getThemedColor("dialogTextRed"));
        if (arrayList2.size() == 1) {
            this.buttonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList2.get(0)).documents.size(), new Object[0])));
        } else {
            this.buttonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList2.size(), new Object[0])));
        }
        this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EmojiPacksAlert.this.lambda$updateButton$4(arrayList2, view);
            }
        });
    }

    public /* synthetic */ void lambda$updateButton$2(View view) {
        showPremiumAlert();
    }

    public /* synthetic */ void lambda$updateButton$3(ArrayList arrayList, View view) {
        dismiss();
        int i = 0;
        while (i < arrayList.size()) {
            installSet((TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0 ? this.fragment : null);
            i++;
        }
    }

    public /* synthetic */ void lambda$updateButton$4(ArrayList arrayList, View view) {
        dismiss();
        int i = 0;
        while (i < arrayList.size()) {
            uninstallSet((TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0 ? this.fragment : null, null);
            i++;
        }
    }

    public int getTop() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || recyclerListView.getChildCount() < 1) {
            return this.containerView.getPaddingTop();
        }
        View childAt = this.listView.getChildAt(0);
        View view = this.paddingView;
        if (childAt != view) {
            return this.containerView.getPaddingTop();
        }
        return view.getBottom() + this.containerView.getPaddingTop();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public int getContainerViewHeight() {
        return (this.listView.getMeasuredHeight() - getTop()) + this.containerView.getPaddingTop() + AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(8.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class Adapter extends RecyclerView.Adapter {
        private Adapter() {
            EmojiPacksAlert.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = EmojiPacksAlert.this.paddingView;
            } else {
                boolean z = true;
                if (i == 1) {
                    EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                    view = new EmojiImageView(emojiPacksAlert, emojiPacksAlert.getContext());
                } else if (i == 2) {
                    EmojiPacksAlert emojiPacksAlert2 = EmojiPacksAlert.this;
                    Context context = emojiPacksAlert2.getContext();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        z = false;
                    }
                    view = new EmojiPackHeader(context, z);
                } else {
                    view = null;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            EmojiView.CustomEmoji customEmoji;
            int i2 = i - 1;
            int itemViewType = viewHolder.getItemViewType();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            int i3 = 0;
            r2 = false;
            r2 = false;
            boolean z = false;
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    return;
                }
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                while (i4 < EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    i6 = EmojiPacksAlert.this.customEmojiPacks.data[i4].size();
                    if (i2 == i5) {
                        break;
                    }
                    i5 += EmojiPacksAlert.this.customEmojiPacks.data[i4].size() + 1;
                    i4++;
                }
                if (EmojiPacksAlert.this.customEmojiPacks.stickerSets != null) {
                    tLRPC$TL_messages_stickerSet = EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(i4);
                }
                if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                    int i7 = 0;
                    while (true) {
                        if (i7 >= tLRPC$TL_messages_stickerSet.documents.size()) {
                            break;
                        } else if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i7))) {
                            z = true;
                            break;
                        } else {
                            i7++;
                        }
                    }
                }
                if (i4 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    return;
                }
                ((EmojiPackHeader) viewHolder.itemView).set(tLRPC$TL_messages_stickerSet, i6, z);
                return;
            }
            EmojiImageView emojiImageView = (EmojiImageView) viewHolder.itemView;
            int i8 = 0;
            while (true) {
                if (i3 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    customEmoji = null;
                    break;
                }
                int size = EmojiPacksAlert.this.customEmojiPacks.data[i3].size();
                if (i2 > i8 && i2 <= i8 + size) {
                    customEmoji = EmojiPacksAlert.this.customEmojiPacks.data[i3].get((i2 - i8) - 1);
                    break;
                } else {
                    i8 += size + 1;
                    i3++;
                }
            }
            AnimatedEmojiSpan animatedEmojiSpan = emojiImageView.span;
            if ((animatedEmojiSpan != null || customEmoji == null) && ((customEmoji != null || animatedEmojiSpan == null) && (customEmoji == null || animatedEmojiSpan.documentId == customEmoji.documentId))) {
                return;
            }
            if (customEmoji == null) {
                emojiImageView.span = null;
                return;
            }
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            TLRPC$StickerSet tLRPC$StickerSet = customEmoji.stickerSet.set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.short_name = tLRPC$StickerSet.short_name;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            emojiImageView.span = new AnimatedEmojiSpan(customEmoji.documentId, (Paint.FontMetricsInt) null);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            int i2 = i - 1;
            int i3 = 0;
            for (int i4 = 0; i4 < EmojiPacksAlert.this.customEmojiPacks.data.length; i4++) {
                if (i2 == i3) {
                    return 2;
                }
                i3 += EmojiPacksAlert.this.customEmojiPacks.data[i4].size() + 1;
            }
            return 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return EmojiPacksAlert.this.customEmojiPacks.getItemsCount() + 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class EmojiImageView extends View {
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public ImageReceiver imageReceiver;
        public AnimatedEmojiSpan span;

        public EmojiImageView(EmojiPacksAlert emojiPacksAlert, Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824));
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPackHeader extends FrameLayout {
        public TextView addButtonView;
        private ValueAnimator animator;
        public TextView removeButtonView;
        private TLRPC$TL_messages_stickerSet set;
        private boolean single;
        public TextView subtitleView;
        public TextView titleView;
        public PremiumButtonView unlockButtonView;
        private boolean toggled = false;
        private float toggleT = 0.0f;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPackHeader(Context context, boolean z) {
            super(context);
            EmojiPacksAlert.this = r26;
            this.single = z;
            float f = 8.0f;
            if (!z) {
                if (!UserConfig.getInstance(((BottomSheet) r26).currentAccount).isPremium()) {
                    PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(4.0f), false);
                    this.unlockButtonView = premiumButtonView;
                    premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiPacksAlert.EmojiPackHeader.this.lambda$new$0(view);
                        }
                    });
                    this.unlockButtonView.setIcon(R.raw.unlock_icon);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.unlockButtonView.getIconView().getLayoutParams();
                    marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
                    marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
                    int dp = AndroidUtilities.dp(20.0f);
                    marginLayoutParams.height = dp;
                    marginLayoutParams.width = dp;
                    ((ViewGroup.MarginLayoutParams) this.unlockButtonView.getTextView().getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
                    this.unlockButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                    addView(this.unlockButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 29.66f, 5.66f, 0.0f));
                    this.unlockButtonView.measure(View.MeasureSpec.makeMeasureSpec(99999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), 1073741824));
                    f = (this.unlockButtonView.getMeasuredWidth() + AndroidUtilities.dp(16.0f)) / AndroidUtilities.density;
                }
                TextView textView = new TextView(context);
                this.addButtonView = textView;
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.addButtonView.setTextColor(r26.getThemedColor("featuredStickers_buttonText"));
                this.addButtonView.setBackground(Theme.AdaptiveRipple.filledRect(r26.getThemedColor("featuredStickers_addButton"), 4.0f));
                this.addButtonView.setText(LocaleController.getString("Add", R.string.Add));
                this.addButtonView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                this.addButtonView.setGravity(17);
                this.addButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$new$1(view);
                    }
                });
                addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 29.66f, 5.66f, 0.0f));
                this.addButtonView.measure(View.MeasureSpec.makeMeasureSpec(99999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), 1073741824));
                float max = Math.max(f, (this.addButtonView.getMeasuredWidth() + AndroidUtilities.dp(16.0f)) / AndroidUtilities.density);
                TextView textView2 = new TextView(context);
                this.removeButtonView = textView2;
                textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.removeButtonView.setTextColor(r26.getThemedColor("featuredStickers_addButton"));
                this.removeButtonView.setBackground(Theme.createRadSelectorDrawable(r26.getThemedColor("featuredStickers_addButton") & 268435455, 4, 4));
                this.removeButtonView.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
                this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
                this.removeButtonView.setGravity(17);
                this.removeButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$new$3(view);
                    }
                });
                this.removeButtonView.setClickable(false);
                addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 29.66f, 5.66f, 0.0f));
                this.removeButtonView.setScaleX(0.0f);
                this.removeButtonView.setScaleY(0.0f);
                this.removeButtonView.setAlpha(0.0f);
                this.removeButtonView.measure(View.MeasureSpec.makeMeasureSpec(99999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), 1073741824));
                f = Math.max(max, (this.removeButtonView.getMeasuredWidth() + AndroidUtilities.dp(16.0f)) / AndroidUtilities.density);
            }
            TextView textView3 = new TextView(context);
            this.titleView = textView3;
            textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            this.titleView.setSingleLine(true);
            this.titleView.setLines(1);
            this.titleView.setTextColor(r26.getThemedColor("dialogTextBlack"));
            if (z) {
                this.titleView.setTextSize(1, 20.0f);
                addView(this.titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 14.66f, 25.0f, f, 0.0f));
            } else {
                this.titleView.setTextSize(1, 17.0f);
                addView(this.titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 8.0f, 24.0f, f, 0.0f));
            }
            if (!z) {
                TextView textView4 = new TextView(context);
                this.subtitleView = textView4;
                textView4.setTextSize(1, 13.0f);
                this.subtitleView.setTextColor(r26.getThemedColor("dialogTextGray2"));
                this.subtitleView.setEllipsize(TextUtils.TruncateAt.END);
                this.subtitleView.setSingleLine(true);
                this.subtitleView.setLines(1);
                addView(this.subtitleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 8.0f, 45.66f, f, 0.0f));
            }
        }

        public /* synthetic */ void lambda$new$0(View view) {
            EmojiPacksAlert.this.showPremiumAlert();
        }

        public /* synthetic */ void lambda$new$1(View view) {
            EmojiPacksAlert.this.installSet(this.set, new BaseFragment() { // from class: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.1
                @Override // org.telegram.ui.ActionBar.BaseFragment
                public FrameLayout getLayoutContainer() {
                    return (FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView;
                }
            });
            toggle(true, true);
        }

        public /* synthetic */ void lambda$new$3(View view) {
            EmojiPacksAlert.this.uninstallSet(this.set, new BaseFragment() { // from class: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.2
                @Override // org.telegram.ui.ActionBar.BaseFragment
                public FrameLayout getLayoutContainer() {
                    return (FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView;
                }
            }, new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiPacksAlert.EmojiPackHeader.this.lambda$new$2();
                }
            });
            toggle(false, true);
        }

        public /* synthetic */ void lambda$new$2() {
            toggle(true, true);
        }

        private void toggle(boolean z, boolean z2) {
            if (this.toggled == z) {
                return;
            }
            this.toggled = z;
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            TextView textView = this.addButtonView;
            if (textView == null || this.removeButtonView == null) {
                return;
            }
            textView.setClickable(!z);
            this.removeButtonView.setClickable(z);
            float f = 1.0f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.toggleT;
                if (!z) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$toggle$4(valueAnimator2);
                    }
                });
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.animator.setDuration(250L);
                this.animator.start();
                return;
            }
            this.toggleT = z ? 1.0f : 0.0f;
            this.addButtonView.setScaleX(z ? 0.0f : 1.0f);
            this.addButtonView.setScaleY(z ? 0.0f : 1.0f);
            this.addButtonView.setAlpha(z ? 0.0f : 1.0f);
            this.removeButtonView.setScaleX(z ? 1.0f : 0.0f);
            this.removeButtonView.setScaleY(z ? 1.0f : 0.0f);
            TextView textView2 = this.removeButtonView;
            if (!z) {
                f = 0.0f;
            }
            textView2.setAlpha(f);
        }

        public /* synthetic */ void lambda$toggle$4(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.toggleT = floatValue;
            this.addButtonView.setScaleX(1.0f - floatValue);
            this.addButtonView.setScaleY(1.0f - this.toggleT);
            this.addButtonView.setAlpha(1.0f - this.toggleT);
            this.removeButtonView.setScaleX(this.toggleT);
            this.removeButtonView.setScaleY(this.toggleT);
            this.removeButtonView.setAlpha(this.toggleT);
        }

        public void set(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, boolean z) {
            TLRPC$StickerSet tLRPC$StickerSet;
            this.set = tLRPC$TL_messages_stickerSet;
            if (tLRPC$TL_messages_stickerSet != null && (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) != null) {
                this.titleView.setText(tLRPC$StickerSet.title);
            } else {
                this.titleView.setText((CharSequence) null);
            }
            TextView textView = this.subtitleView;
            if (textView != null) {
                textView.setText(LocaleController.formatPluralString("EmojiCount", i, new Object[0]));
            }
            if (z && this.unlockButtonView != null && !UserConfig.getInstance(((BottomSheet) EmojiPacksAlert.this).currentAccount).isPremium()) {
                this.unlockButtonView.setVisibility(0);
                TextView textView2 = this.addButtonView;
                if (textView2 != null) {
                    textView2.setVisibility(8);
                }
                TextView textView3 = this.removeButtonView;
                if (textView3 == null) {
                    return;
                }
                textView3.setVisibility(8);
                return;
            }
            PremiumButtonView premiumButtonView = this.unlockButtonView;
            if (premiumButtonView != null) {
                premiumButtonView.setVisibility(8);
            }
            TextView textView4 = this.addButtonView;
            if (textView4 != null) {
                textView4.setVisibility(0);
            }
            TextView textView5 = this.removeButtonView;
            if (textView5 != null) {
                textView5.setVisibility(0);
            }
            toggle(EmojiPacksAlert.this.isInstalled(tLRPC$TL_messages_stickerSet), false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.single ? 65.0f : 70.0f), 1073741824));
        }
    }
}
