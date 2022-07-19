package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class EmojiPacksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private TextView buttonView;
    private EmojiPacksLoader customEmojiPacks;
    private BaseFragment fragment;
    private Float fromY;
    private GridLayoutManager gridLayoutManager;
    private boolean hasDescription;
    private float lastY;
    private RecyclerListView listView;
    private ValueAnimator loadAnimator;
    private float loadT;
    boolean loaded = true;
    private View paddingView;
    private long premiumButtonClicked;
    private PremiumButtonView premiumButtonView;
    private CircularProgressDrawable progressDrawable;
    private View shadowView;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public EmojiPacksAlert(final BaseFragment baseFragment, Context context, final Theme.ResourcesProvider resourcesProvider, final ArrayList<TLRPC$InputStickerSet> arrayList) {
        super(context, false, resourcesProvider);
        new Paint();
        boolean z = arrayList.size() <= 1;
        this.fragment = baseFragment;
        fixNavigationBar();
        this.customEmojiPacks = new EmojiPacksLoader(this.currentAccount, arrayList) { // from class: org.telegram.ui.Components.EmojiPacksAlert.1
            @Override // org.telegram.ui.Components.EmojiPacksAlert.EmojiPacksLoader
            protected void onUpdate() {
                EmojiPacksAlert.this.updateButton();
                if (EmojiPacksAlert.this.listView == null || EmojiPacksAlert.this.listView.getAdapter() == null) {
                    return;
                }
                EmojiPacksAlert.this.listView.getAdapter().notifyDataSetChanged();
            }
        };
        this.progressDrawable = new CircularProgressDrawable(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(3.5f), getThemedColor("featuredStickers_addButton"));
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
                EmojiPacksAlert emojiPacksAlert;
                float f;
                if (!this.attached) {
                    return;
                }
                this.paint.setColor(EmojiPacksAlert.this.getThemedColor("dialogBackground"));
                this.paint.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(-0.66f), 503316480);
                this.path.reset();
                float f2 = EmojiPacksAlert.this.lastY = emojiPacksAlert.getListTop();
                if (EmojiPacksAlert.this.fromY != null) {
                    float lerp = AndroidUtilities.lerp(EmojiPacksAlert.this.fromY.floatValue(), ((BottomSheet) EmojiPacksAlert.this).containerView.getY() + f2, EmojiPacksAlert.this.loadT) - ((BottomSheet) EmojiPacksAlert.this).containerView.getY();
                    f = lerp - f2;
                    f2 = lerp;
                } else {
                    f = 0.0f;
                }
                float clamp = 1.0f - MathUtils.clamp((f2 - ((BottomSheet) EmojiPacksAlert.this).containerView.getPaddingTop()) / AndroidUtilities.dp(32.0f), 0.0f, 1.0f);
                float paddingTop = f2 - (((BottomSheet) EmojiPacksAlert.this).containerView.getPaddingTop() * clamp);
                float dp = AndroidUtilities.dp((1.0f - clamp) * 14.0f);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(getPaddingLeft(), paddingTop, getWidth() - getPaddingRight(), getBottom() + dp);
                this.path.addRoundRect(rectF, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                boolean z2 = clamp > 0.75f;
                Boolean bool = this.lastOpen;
                if (bool == null || z2 != bool.booleanValue()) {
                    EmojiPacksAlert emojiPacksAlert2 = EmojiPacksAlert.this;
                    Boolean valueOf = Boolean.valueOf(z2);
                    this.lastOpen = valueOf;
                    emojiPacksAlert2.updateLightStatusBar(valueOf.booleanValue());
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
                    canvas.translate(EmojiPacksAlert.this.listView.getLeft(), EmojiPacksAlert.this.listView.getTop() + f);
                    canvas.clipRect(0, 0, EmojiPacksAlert.this.listView.getWidth(), EmojiPacksAlert.this.listView.getHeight());
                    canvas.saveLayerAlpha(0.0f, 0.0f, EmojiPacksAlert.this.listView.getWidth(), EmojiPacksAlert.this.listView.getHeight(), (int) (EmojiPacksAlert.this.listView.getAlpha() * 255.0f), 31);
                    for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                        ArrayList<EmojiImageView> valueAt = this.viewsGroupedByLines.valueAt(i);
                        valueAt.clear();
                        this.unusedArrays.add(valueAt);
                    }
                    this.viewsGroupedByLines.clear();
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
                                    AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(((BottomSheet) EmojiPacksAlert.this).currentAccount, 2, documentId);
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
                    canvas.restore();
                    if (EmojiPacksAlert.this.listView.getAlpha() < 1.0f) {
                        int width = getWidth() / 2;
                        int height = (((int) dp3) + getHeight()) / 2;
                        int dp4 = AndroidUtilities.dp(16.0f);
                        EmojiPacksAlert.this.progressDrawable.setAlpha((int) ((1.0f - EmojiPacksAlert.this.listView.getAlpha()) * 255.0f));
                        EmojiPacksAlert.this.progressDrawable.setBounds(width - dp4, height - dp4, width + dp4, height + dp4);
                        EmojiPacksAlert.this.progressDrawable.draw(canvas);
                        invalidate();
                    }
                }
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < EmojiPacksAlert.this.getListTop() - AndroidUtilities.dp(6.0f)) {
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
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x;
                int i4 = point.y;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (i4 * (i3 < i4 ? 0.55f : 0.3f)), 1073741824));
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
        this.listView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.EmojiPacksAlert.5
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                if (view instanceof SeparatorView) {
                    rect.left = -EmojiPacksAlert.this.listView.getPaddingLeft();
                    rect.right = -EmojiPacksAlert.this.listView.getPaddingRight();
                } else if (EmojiPacksAlert.this.listView.getChildAdapterPosition(view) != 1) {
                } else {
                    rect.top = AndroidUtilities.dp(14.0f);
                }
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                EmojiPacksAlert.this.lambda$new$0(arrayList, baseFragment, resourcesProvider, view, i2);
            }
        });
        this.gridLayoutManager.setReverseLayout(false);
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiPacksAlert.6
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i2) {
                if (EmojiPacksAlert.this.listView.getAdapter() == null || EmojiPacksAlert.this.listView.getAdapter().getItemViewType(i2) == 1) {
                    return 1;
                }
                return EmojiPacksAlert.this.gridLayoutManager.getSpanCount();
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
            this.premiumButtonView.buttonLayout.setClickable(true);
            this.containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
            updateButton();
        }
    }

    public /* synthetic */ void lambda$new$0(ArrayList arrayList, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (arrayList == null || arrayList.size() <= 1 || SystemClock.elapsedRealtime() - this.premiumButtonClicked < 250) {
            return;
        }
        int i2 = 0;
        int i3 = 0;
        while (true) {
            ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.customEmojiPacks.data;
            if (i2 >= arrayListArr.length) {
                break;
            }
            int size = arrayListArr[i2].size();
            if (this.customEmojiPacks.data.length > 1) {
                size = Math.min(this.gridLayoutManager.getSpanCount() * 2, size);
            }
            i3 += size + 1 + 1;
            if (i < i3) {
                break;
            }
            i2++;
        }
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList2 = this.customEmojiPacks.stickerSets;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (arrayList2 == null || i2 >= arrayList2.size()) ? null : this.customEmojiPacks.stickerSets.get(i2);
        if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.set == null) {
            return;
        }
        ArrayList arrayList3 = new ArrayList();
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        arrayList3.add(tLRPC$TL_inputStickerSetID);
        new EmojiPacksAlert(baseFragment, getContext(), resourcesProvider, arrayList3).show();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            updateInstallment();
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

    public void updateInstallment() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof EmojiPackHeader) {
                EmojiPackHeader emojiPackHeader = (EmojiPackHeader) childAt;
                emojiPackHeader.toggle(isInstalled(emojiPackHeader.set), true);
            }
        }
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

    public static void installSet(BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z) {
        installSet(baseFragment, tLRPC$TL_messages_stickerSet, z, null);
    }

    public static void installSet(final BaseFragment baseFragment, final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final boolean z, final Runnable runnable) {
        if (tLRPC$TL_messages_stickerSet == null || baseFragment == null || MediaDataController.getInstance(baseFragment.getCurrentAccount()).cancelRemovingStickerSet(tLRPC$TL_messages_stickerSet.set.id)) {
            return;
        }
        TLRPC$TL_messages_installStickerSet tLRPC$TL_messages_installStickerSet = new TLRPC$TL_messages_installStickerSet();
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_messages_installStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        ConnectionsManager.getInstance(baseFragment.getCurrentAccount()).sendRequest(tLRPC$TL_messages_installStickerSet, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                EmojiPacksAlert.lambda$installSet$3(TLRPC$TL_messages_stickerSet.this, z, baseFragment, runnable, tLObject, tLRPC$TL_error);
            }
        });
    }

    public static /* synthetic */ void lambda$installSet$3(final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final boolean z, final BaseFragment baseFragment, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                EmojiPacksAlert.lambda$installSet$2(TLRPC$TL_messages_stickerSet.this, tLRPC$TL_error, z, baseFragment, tLObject, runnable);
            }
        });
    }

    public static /* synthetic */ void lambda$installSet$2(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_error tLRPC$TL_error, boolean z, BaseFragment baseFragment, TLObject tLObject, final Runnable runnable) {
        int i;
        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
        if (tLRPC$StickerSet.masks) {
            i = 1;
        } else {
            i = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        try {
            if (tLRPC$TL_error == null) {
                if (z && baseFragment.getFragmentView() != null) {
                    Bulletin.make(baseFragment, new StickerSetBulletinLayout(baseFragment.getFragmentView().getContext(), tLRPC$TL_messages_stickerSet, 2, null, baseFragment.getResourceProvider()), 1500).show();
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(baseFragment.getCurrentAccount()).processStickerSetInstallResultArchive(baseFragment, true, i, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
                }
            } else if (baseFragment.getFragmentView() != null) {
                Toast.makeText(baseFragment.getFragmentView().getContext(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        MediaDataController.getInstance(baseFragment.getCurrentAccount()).loadStickers(i, false, true, false, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda5
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                EmojiPacksAlert.lambda$installSet$1(runnable, (ArrayList) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$installSet$1(Runnable runnable, ArrayList arrayList) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void uninstallSet(BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z, Runnable runnable) {
        if (baseFragment == null || tLRPC$TL_messages_stickerSet == null || baseFragment.getFragmentView() == null) {
            return;
        }
        MediaDataController.getInstance(baseFragment.getCurrentAccount()).toggleStickerSet(baseFragment.getFragmentView().getContext(), tLRPC$TL_messages_stickerSet, tLRPC$TL_messages_stickerSet.set.official ? 1 : 0, baseFragment, true, z, runnable);
    }

    private void loadAnimation() {
        if (this.loadAnimator != null) {
            return;
        }
        this.loadT = 0.0f;
        this.loadAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.fromY = Float.valueOf(this.lastY + this.containerView.getY());
        this.loadAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                EmojiPacksAlert.this.lambda$loadAnimation$4(valueAnimator);
            }
        });
        this.loadAnimator.setDuration(250L);
        this.loadAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.loadAnimator.start();
    }

    public /* synthetic */ void lambda$loadAnimation$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.loadT = floatValue;
        this.listView.setAlpha(floatValue);
        this.buttonView.setAlpha(this.loadT);
        this.premiumButtonView.setAlpha(this.loadT);
        this.containerView.invalidate();
    }

    public void updateButton() {
        boolean z;
        if (this.buttonView == null) {
            return;
        }
        ArrayList arrayList = this.customEmojiPacks.stickerSets == null ? new ArrayList() : new ArrayList(this.customEmojiPacks.stickerSets);
        int i = 0;
        while (true) {
            z = true;
            if (i >= arrayList.size()) {
                break;
            }
            if (arrayList.get(i) == null) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList.get(i2);
            if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.set != null) {
                if (!isInstalled(tLRPC$TL_messages_stickerSet)) {
                    arrayList3.add(tLRPC$TL_messages_stickerSet);
                } else {
                    arrayList2.add(tLRPC$TL_messages_stickerSet);
                }
            }
        }
        boolean z2 = false;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) arrayList.get(i3);
            if (tLRPC$TL_messages_stickerSet2 != null && tLRPC$TL_messages_stickerSet2.documents != null) {
                for (int i4 = 0; i4 < tLRPC$TL_messages_stickerSet2.documents.size(); i4++) {
                    if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet2.documents.get(i4))) {
                        z2 = true;
                    }
                }
            }
        }
        if (z2 && !UserConfig.getInstance(this.currentAccount).isPremium()) {
            if (!this.loaded && arrayList.size() > 0) {
                loadAnimation();
            }
            if (arrayList.size() <= 0) {
                z = false;
            }
            this.loaded = z;
            if (!z) {
                this.listView.setAlpha(0.0f);
            }
            this.premiumButtonView.setVisibility(0);
            this.buttonView.setVisibility(4);
            this.premiumButtonView.setButton(LocaleController.getString("UnlockPremiumEmoji", R.string.UnlockPremiumEmoji), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiPacksAlert.this.lambda$updateButton$5(view);
                }
            });
            return;
        }
        this.premiumButtonView.setVisibility(4);
        if (arrayList3.size() > 0) {
            if (!this.loaded) {
                loadAnimation();
            }
            this.loaded = true;
            this.buttonView.setVisibility(0);
            this.buttonView.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 6.0f));
            this.buttonView.setTextColor(getThemedColor("featuredStickers_buttonText"));
            if (arrayList3.size() == 1) {
                this.buttonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList3.get(0)).documents.size(), new Object[0])));
            } else {
                this.buttonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList3.size(), new Object[0])));
            }
            this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiPacksAlert.this.lambda$updateButton$6(arrayList3, view);
                }
            });
        } else if (arrayList.size() == 0) {
            this.loaded = false;
            this.listView.setAlpha(0.0f);
            this.buttonView.setVisibility(4);
        } else {
            if (!this.loaded) {
                loadAnimation();
            }
            this.loaded = true;
            this.buttonView.setVisibility(0);
            this.buttonView.setBackground(Theme.createRadSelectorDrawable(268435455 & getThemedColor("dialogTextRed"), 6, 6));
            this.buttonView.setTextColor(getThemedColor("dialogTextRed"));
            if (arrayList2.size() == 1) {
                this.buttonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList2.get(0)).documents.size(), new Object[0])));
            } else {
                this.buttonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList2.size(), new Object[0])));
            }
            this.buttonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiPacksAlert.this.lambda$updateButton$7(arrayList2, view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateButton$5(View view) {
        showPremiumAlert();
    }

    public /* synthetic */ void lambda$updateButton$6(ArrayList arrayList, View view) {
        int i = 0;
        while (i < arrayList.size()) {
            installSet(this.fragment, (TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0);
            i++;
        }
        dismiss();
    }

    public /* synthetic */ void lambda$updateButton$7(ArrayList arrayList, View view) {
        int i = 0;
        while (i < arrayList.size()) {
            uninstallSet(this.fragment, (TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0, null);
            i++;
        }
        dismiss();
    }

    public int getListTop() {
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
        return (this.listView.getMeasuredHeight() - getListTop()) + this.containerView.getPaddingTop() + AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(8.0f);
    }

    /* loaded from: classes3.dex */
    class SeparatorView extends View {
        public SeparatorView(EmojiPacksAlert emojiPacksAlert, Context context) {
            super(context);
            setBackgroundColor(emojiPacksAlert.getThemedColor("chat_emojiPanelShadowLine"));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.getShadowHeight());
            ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(14.0f);
            setLayoutParams(layoutParams);
        }
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
                } else if (i == 3) {
                    view = new TextView(EmojiPacksAlert.this.getContext());
                } else if (i == 4) {
                    EmojiPacksAlert emojiPacksAlert3 = EmojiPacksAlert.this;
                    view = new SeparatorView(emojiPacksAlert3, emojiPacksAlert3.getContext());
                } else {
                    view = null;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            EmojiView.CustomEmoji customEmoji;
            ArrayList<TLRPC$Document> arrayList;
            int i2 = i - 1;
            int itemViewType = viewHolder.getItemViewType();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            int i3 = 0;
            boolean z = true;
            if (itemViewType == 1) {
                if (EmojiPacksAlert.this.hasDescription) {
                    i2--;
                }
                EmojiImageView emojiImageView = (EmojiImageView) viewHolder.itemView;
                int i4 = 0;
                while (true) {
                    if (i3 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                        customEmoji = null;
                        break;
                    }
                    int size = EmojiPacksAlert.this.customEmojiPacks.data[i3].size();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        size = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size);
                    }
                    if (i2 > i4 && i2 <= i4 + size) {
                        customEmoji = EmojiPacksAlert.this.customEmojiPacks.data[i3].get((i2 - i4) - 1);
                        break;
                    } else {
                        i4 += size + 1 + 1;
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
            } else if (itemViewType == 2) {
                if (EmojiPacksAlert.this.hasDescription && i2 > 0) {
                    i2--;
                }
                int i5 = 0;
                int i6 = 0;
                while (i5 < EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    int size2 = EmojiPacksAlert.this.customEmojiPacks.data[i5].size();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        size2 = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size2);
                    }
                    if (i2 == i6) {
                        break;
                    }
                    i6 += size2 + 1 + 1;
                    i5++;
                }
                if (EmojiPacksAlert.this.customEmojiPacks.stickerSets != null && i5 < EmojiPacksAlert.this.customEmojiPacks.stickerSets.size()) {
                    tLRPC$TL_messages_stickerSet = EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(i5);
                }
                if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                    for (int i7 = 0; i7 < tLRPC$TL_messages_stickerSet.documents.size(); i7++) {
                        if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i7))) {
                            break;
                        }
                    }
                }
                z = false;
                if (i5 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    return;
                }
                EmojiPackHeader emojiPackHeader = (EmojiPackHeader) viewHolder.itemView;
                if (tLRPC$TL_messages_stickerSet != null && (arrayList = tLRPC$TL_messages_stickerSet.documents) != null) {
                    i3 = arrayList.size();
                }
                emojiPackHeader.set(tLRPC$TL_messages_stickerSet, i3, z);
            } else {
                if (itemViewType != 3) {
                    return;
                }
                TextView textView = (TextView) viewHolder.itemView;
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(EmojiPacksAlert.this.getThemedColor("chat_emojiPanelTrendingDescription"));
                textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PremiumPreviewEmojiPack", R.string.PremiumPreviewEmojiPack)));
                textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            int i2 = i - 1;
            if (EmojiPacksAlert.this.hasDescription) {
                if (i2 == 1) {
                    return 3;
                }
                if (i2 > 0) {
                    i2--;
                }
            }
            int i3 = 0;
            for (int i4 = 0; i4 < EmojiPacksAlert.this.customEmojiPacks.data.length; i4++) {
                if (i2 == i3) {
                    return 2;
                }
                int size = EmojiPacksAlert.this.customEmojiPacks.data[i4].size();
                if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                    size = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size);
                }
                int i5 = i3 + size + 1;
                if (i2 == i5) {
                    return 4;
                }
                i3 = i5 + 1;
            }
            return 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
            emojiPacksAlert.hasDescription = !UserConfig.getInstance(((BottomSheet) emojiPacksAlert).currentAccount).isPremium() && EmojiPacksAlert.this.customEmojiPacks.stickerSets != null && EmojiPacksAlert.this.customEmojiPacks.stickerSets.size() == 1 && MessageObject.isPremiumEmojiPack(EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(0));
            return (EmojiPacksAlert.this.hasDescription ? 1 : 0) + 1 + EmojiPacksAlert.this.customEmojiPacks.getItemsCount() + Math.max(0, EmojiPacksAlert.this.customEmojiPacks.data.length - 1);
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
        public BaseFragment dummyFragment = new BaseFragment() { // from class: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.1
            @Override // org.telegram.ui.ActionBar.BaseFragment
            public int getCurrentAccount() {
                return this.currentAccount;
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public View getFragmentView() {
                return ((BottomSheet) EmojiPacksAlert.this).containerView;
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public FrameLayout getLayoutContainer() {
                return (FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView;
            }

            @Override // org.telegram.ui.ActionBar.BaseFragment
            public Theme.ResourcesProvider getResourceProvider() {
                return ((BottomSheet) EmojiPacksAlert.this).resourcesProvider;
            }
        };
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
                    addView(this.unlockButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 15.66f, 5.66f, 0.0f));
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
                addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 15.66f, 5.66f, 0.0f));
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
                addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 15.66f, 5.66f, 0.0f));
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
                addView(this.titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 14.0f, 11.0f, f, 0.0f));
            } else {
                this.titleView.setTextSize(1, 17.0f);
                addView(this.titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 8.0f, 10.0f, f, 0.0f));
            }
            if (!z) {
                TextView textView4 = new TextView(context);
                this.subtitleView = textView4;
                textView4.setTextSize(1, 13.0f);
                this.subtitleView.setTextColor(r26.getThemedColor("dialogTextGray2"));
                this.subtitleView.setEllipsize(TextUtils.TruncateAt.END);
                this.subtitleView.setSingleLine(true);
                this.subtitleView.setLines(1);
                addView(this.subtitleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 8.0f, 31.66f, f, 0.0f));
            }
        }

        public /* synthetic */ void lambda$new$0(View view) {
            EmojiPacksAlert.this.premiumButtonClicked = SystemClock.elapsedRealtime();
            EmojiPacksAlert.this.showPremiumAlert();
        }

        public /* synthetic */ void lambda$new$1(View view) {
            EmojiPacksAlert.installSet(this.dummyFragment, this.set, true);
            toggle(true, true);
        }

        public /* synthetic */ void lambda$new$3(View view) {
            EmojiPacksAlert.uninstallSet(this.dummyFragment, this.set, true, new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4
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

        public void toggle(boolean z, boolean z2) {
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
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.single ? 43.0f : 56.0f), 1073741824));
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPacksLoader implements NotificationCenter.NotificationCenterDelegate {
        private int currentAccount;
        public ArrayList<EmojiView.CustomEmoji>[] data;
        public ArrayList<TLRPC$InputStickerSet> inputStickerSets;
        public ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;

        protected void onUpdate() {
            throw null;
        }

        public EmojiPacksLoader(int i, ArrayList<TLRPC$InputStickerSet> arrayList) {
            EmojiPacksAlert.this = r1;
            this.currentAccount = i;
            this.inputStickerSets = arrayList == null ? new ArrayList<>() : arrayList;
            init();
        }

        private void init() {
            TLRPC$StickerSet tLRPC$StickerSet;
            this.stickerSets = new ArrayList<>(this.inputStickerSets.size());
            this.data = new ArrayList[this.inputStickerSets.size()];
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            final boolean[] zArr = new boolean[1];
            for (int i = 0; i < this.data.length; i++) {
                TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i), false, new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiPacksAlert.EmojiPacksLoader.this.lambda$init$1(zArr);
                    }
                });
                if (this.data.length == 1 && stickerSet != null && (tLRPC$StickerSet = stickerSet.set) != null && !tLRPC$StickerSet.emojis) {
                    EmojiPacksAlert.this.dismiss();
                    new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i), null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider).show();
                    return;
                }
                this.stickerSets.add(stickerSet);
                putStickerSet(i, stickerSet);
            }
        }

        public /* synthetic */ void lambda$init$1(boolean[] zArr) {
            if (!zArr[0]) {
                zArr[0] = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiPacksAlert.EmojiPacksLoader.this.lambda$init$0();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$init$0() {
            EmojiPacksAlert.this.dismiss();
            BulletinFactory.of(EmojiPacksAlert.this.fragment).createErrorBulletin(LocaleController.getString("AddEmojiNotFound", R.string.AddEmojiNotFound)).show();
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC$StickerSet tLRPC$StickerSet;
            if (i == NotificationCenter.groupStickersDidLoad) {
                for (int i3 = 0; i3 < this.stickerSets.size(); i3++) {
                    if (this.stickerSets.get(i3) == null) {
                        TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i3), true);
                        if (this.stickerSets.size() == 1 && stickerSet != null && (tLRPC$StickerSet = stickerSet.set) != null && !tLRPC$StickerSet.emojis) {
                            EmojiPacksAlert.this.dismiss();
                            new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i3), null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider).show();
                            return;
                        }
                        this.stickerSets.set(i3, stickerSet);
                        if (stickerSet != null) {
                            putStickerSet(i3, stickerSet);
                        }
                    }
                }
                onUpdate();
            }
        }

        private void putStickerSet(int i, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            if (i >= 0) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i >= arrayListArr.length) {
                    return;
                }
                int i2 = 0;
                if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null) {
                    arrayListArr[i] = new ArrayList<>(12);
                    while (i2 < 12) {
                        this.data[i].add(null);
                        i2++;
                    }
                    return;
                }
                arrayListArr[i] = new ArrayList<>();
                while (i2 < tLRPC$TL_messages_stickerSet.documents.size()) {
                    TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                    if (tLRPC$Document == null) {
                        this.data[i].add(null);
                    } else {
                        EmojiView.CustomEmoji customEmoji = new EmojiView.CustomEmoji();
                        findEmoticon(tLRPC$TL_messages_stickerSet, tLRPC$Document.id);
                        customEmoji.stickerSet = tLRPC$TL_messages_stickerSet;
                        customEmoji.documentId = tLRPC$Document.id;
                        this.data[i].add(customEmoji);
                    }
                    i2++;
                }
            }
        }

        public int getItemsCount() {
            int i;
            int i2 = 0;
            int i3 = 0;
            while (true) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i2 < arrayListArr.length) {
                    if (arrayListArr.length != 1) {
                        i = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, this.data[i2].size());
                    } else {
                        i = arrayListArr[i2].size();
                    }
                    i3 = i3 + i + 1;
                    i2++;
                } else {
                    return i3;
                }
            }
        }

        public String findEmoticon(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, long j) {
            if (tLRPC$TL_messages_stickerSet == null) {
                return null;
            }
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.packs.size(); i++) {
                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i);
                ArrayList<Long> arrayList = tLRPC$TL_stickerPack.documents;
                if (arrayList != null && arrayList.contains(Long.valueOf(j))) {
                    return tLRPC$TL_stickerPack.emoticon;
                }
            }
            return null;
        }
    }
}
