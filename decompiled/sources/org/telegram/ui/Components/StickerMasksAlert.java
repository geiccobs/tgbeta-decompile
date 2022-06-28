package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.Components.StickerMasksAlert;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes5.dex */
public class StickerMasksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private StickerMasksAlertDelegate delegate;
    private RecyclerListView gridView;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    private String[] lastSearchKeyboardLanguage;
    private ImageView masksButton;
    private int scrollOffsetY;
    private Drawable shadowDrawable;
    private View shadowLine;
    private Drawable[] stickerIcons;
    private ImageView stickersButton;
    private StickersGridAdapter stickersGridAdapter;
    private GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private int stickersTabOffset;
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>()};
    private ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>()};
    private ArrayList<TLRPC.Document> favouriteStickers = new ArrayList<>();
    private int recentTabBum = -2;
    private int favTabBum = -2;
    private ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.StickerMasksAlert.1
        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ String getQuery(boolean z) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ void gifAddedOrDeleted() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ boolean needRemove() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
            StickerMasksAlert.this.delegate.onStickerSelected(parent, sticker);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean needSend() {
            return false;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean canSchedule() {
            return false;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean isInScheduleMode() {
            return false;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public long getDialogId() {
            return 0L;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean needMenu() {
            return false;
        }
    };
    private int currentType = 0;
    private int searchFieldHeight = AndroidUtilities.dp(64.0f);

    /* loaded from: classes5.dex */
    public interface StickerMasksAlertDelegate {
        void onStickerSelected(Object obj, TLRPC.Document document);
    }

    /* loaded from: classes5.dex */
    public class SearchField extends FrameLayout {
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private EditTextBoldCursor searchEditText;
        private AnimatorSet shadowAnimator;
        private View shadowView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SearchField(Context context, int type) {
            super(context);
            StickerMasksAlert.this = r21;
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(301989888);
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View backgroundView = new View(context);
            backgroundView.setBackgroundColor(-14342875);
            addView(backgroundView, new FrameLayout.LayoutParams(-1, r21.searchFieldHeight));
            View searchBackground = new View(context);
            searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -13224394));
            addView(searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView searchIconImageView = new ImageView(context);
            searchIconImageView.setScaleType(ImageView.ScaleType.CENTER);
            searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            searchIconImageView.setColorFilter(new PorterDuffColorFilter(-8947849, PorterDuff.Mode.MULTIPLY));
            addView(searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.clearSearchImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView2 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.1
                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                public int getCurrentColor() {
                    return -8947849;
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView2.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    StickerMasksAlert.SearchField.this.m3080xef7b9934(view2);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.2
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    if (event.getAction() == 0) {
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(-8947849);
            this.searchEditText.setTextColor(-1);
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            if (type == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", R.string.SearchStickersHint));
            } else if (type == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", R.string.SearchEmojiHint));
            } else if (type == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
            this.searchEditText.setCursorColor(-1);
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.3
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    boolean showed = true;
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        showed = false;
                    }
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    StickerMasksAlert.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-StickerMasksAlert$SearchField */
        public /* synthetic */ void m3080xef7b9934(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public void showShadow(boolean show, boolean animated) {
            if (!show || this.shadowView.getTag() != null) {
                if (!show && this.shadowView.getTag() != null) {
                    return;
                }
                AnimatorSet animatorSet = this.shadowAnimator;
                Integer num = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!show) {
                    num = 1;
                }
                view.setTag(num);
                float f = 1.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200L);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.4
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!show) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    public StickerMasksAlert(Context context, boolean isVideo, final Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        this.behindKeyboardColorKey = null;
        this.behindKeyboardColor = -14342875;
        this.useLightStatusBar = false;
        fixNavigationBar(-14342875);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
        MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
        MediaDataController.getInstance(this.currentAccount).loadRecents(1, false, true, false);
        MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-14342875, PorterDuff.Mode.MULTIPLY));
        this.containerView = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.2
            private long lastUpdateTime;
            private float statusBarProgress;
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21 && !StickerMasksAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(StickerMasksAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickerMasksAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int availableHeight = totalHeight - getPaddingTop();
                if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    padding = 0;
                    this.statusBarProgress = 1.0f;
                } else {
                    padding = (availableHeight - ((availableHeight / 5) * 3)) + AndroidUtilities.dp(8.0f);
                }
                if (StickerMasksAlert.this.gridView.getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    StickerMasksAlert.this.gridView.setPinnedSectionOffsetY(-padding);
                    StickerMasksAlert.this.gridView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                StickerMasksAlert.this.updateLayout(false);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && StickerMasksAlert.this.scrollOffsetY != 0 && ev.getY() < StickerMasksAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f)) {
                    StickerMasksAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !StickerMasksAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                float availableToMove;
                int height;
                int y;
                int top;
                long dt;
                long newTime;
                int offset = AndroidUtilities.dp(13.0f);
                int top2 = (StickerMasksAlert.this.scrollOffsetY - StickerMasksAlert.this.backgroundPaddingTop) - offset;
                if (StickerMasksAlert.this.currentSheetAnimationType == 1) {
                    top2 = (int) (top2 + StickerMasksAlert.this.gridView.getTranslationY());
                }
                int y2 = AndroidUtilities.dp(20.0f) + top2;
                int height2 = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + StickerMasksAlert.this.backgroundPaddingTop;
                int h = AndroidUtilities.dp(12.0f);
                if (StickerMasksAlert.this.backgroundPaddingTop + top2 >= h) {
                    height = height2;
                    availableToMove = 1.0f;
                } else {
                    float toMove = AndroidUtilities.dp(4.0f) + offset;
                    float moveProgress = Math.min(1.0f, ((h - top2) - StickerMasksAlert.this.backgroundPaddingTop) / toMove);
                    int diff = (int) ((h - toMove) * moveProgress);
                    top2 -= diff;
                    y2 -= diff;
                    float rad = 1.0f - moveProgress;
                    height = height2 + diff;
                    availableToMove = rad;
                }
                if (Build.VERSION.SDK_INT < 21) {
                    top = top2;
                    y = y2;
                } else {
                    top = top2 + AndroidUtilities.statusBarHeight;
                    y = y2 + AndroidUtilities.statusBarHeight;
                }
                StickerMasksAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                StickerMasksAlert.this.shadowDrawable.draw(canvas);
                if (availableToMove != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(-14342875);
                    this.rect.set(StickerMasksAlert.this.backgroundPaddingLeft, StickerMasksAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - StickerMasksAlert.this.backgroundPaddingLeft, StickerMasksAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * availableToMove, AndroidUtilities.dp(12.0f) * availableToMove, Theme.dialogs_onlineCirclePaint);
                }
                long newTime2 = SystemClock.elapsedRealtime();
                long dt2 = newTime2 - this.lastUpdateTime;
                if (dt2 <= 18) {
                    dt = dt2;
                } else {
                    dt = 18;
                }
                this.lastUpdateTime = newTime2;
                if (availableToMove > 0.0f) {
                    int w = AndroidUtilities.dp(36.0f);
                    newTime = newTime2;
                    this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                    int alpha = Color.alpha(-11842741);
                    Theme.dialogs_onlineCirclePaint.setColor(-11842741);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * 1.0f * availableToMove));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    float f = this.statusBarProgress;
                    if (f > 0.0f) {
                        float f2 = f - (((float) dt) / 180.0f);
                        this.statusBarProgress = f2;
                        if (f2 < 0.0f) {
                            this.statusBarProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                } else {
                    newTime = newTime2;
                    float f3 = this.statusBarProgress;
                    if (f3 < 1.0f) {
                        float f4 = f3 + (((float) dt) / 180.0f);
                        this.statusBarProgress = f4;
                        if (f4 > 1.0f) {
                            this.statusBarProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
                int finalColor = Color.argb((int) (this.statusBarProgress * 255.0f), (int) (Color.red(-14342875) * 0.8f), (int) (Color.green(-14342875) * 0.8f), (int) (Color.blue(-14342875) * 0.8f));
                Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                canvas.drawRect(StickerMasksAlert.this.backgroundPaddingLeft, 0.0f, getMeasuredWidth() - StickerMasksAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_recent, -11842741, -9520403), Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_favorites, -11842741, -9520403)};
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.3
            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) (StickerMasksAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, StickerMasksAlert.this.gridView, StickerMasksAlert.this.containerView.getMeasuredHeight(), StickerMasksAlert.this.contentPreviewViewerDelegate, this.resourcesProvider);
                return super.onInterceptTouchEvent(event) || result;
            }
        };
        this.gridView = recyclerListView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5) { // from class: org.telegram.ui.Components.StickerMasksAlert.4
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.StickerMasksAlert.4.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view, snapPreference);
                        return dy - (StickerMasksAlert.this.gridView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 4;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.StickerMasksAlert.5
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (StickerMasksAlert.this.gridView.getAdapter() == StickerMasksAlert.this.stickersGridAdapter) {
                    if (position == 0) {
                        return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                    }
                    if (position != StickerMasksAlert.this.stickersGridAdapter.totalItems) {
                        Object object = StickerMasksAlert.this.stickersGridAdapter.cache.get(position);
                        if (object == null || (StickerMasksAlert.this.stickersGridAdapter.cache.get(position) instanceof TLRPC.Document)) {
                            return 1;
                        }
                    }
                    return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                }
                if (position != StickerMasksAlert.this.stickersSearchGridAdapter.totalItems) {
                    Object object2 = StickerMasksAlert.this.stickersSearchGridAdapter.cache.get(position);
                    if (object2 == null || (StickerMasksAlert.this.stickersSearchGridAdapter.cache.get(position) instanceof TLRPC.Document)) {
                        return 1;
                    }
                }
                return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
            }
        });
        this.gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(48.0f));
        this.gridView.setClipToPadding(false);
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(-14342875);
        this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
        RecyclerListView recyclerListView2 = this.gridView;
        StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter;
        recyclerListView2.setAdapter(stickersGridAdapter);
        this.gridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda2
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickerMasksAlert.this.m3075lambda$new$0$orgtelegramuiComponentsStickerMasksAlert(resourcesProvider, view, motionEvent);
            }
        });
        RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                StickerMasksAlert.this.m3076lambda$new$1$orgtelegramuiComponentsStickerMasksAlert(view, i);
            }
        };
        this.stickersOnItemClickListener = onItemClickListener;
        this.gridView.setOnItemClickListener(onItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f));
        this.stickersTab = new ScrollSlidingTabStrip(context, resourcesProvider) { // from class: org.telegram.ui.Components.StickerMasksAlert.6
            @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        this.stickersSearchField = new SearchField(context, 0);
        this.containerView.addView(this.stickersSearchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
        this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
        this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
        this.stickersTab.setIndicatorColor(-9520403);
        this.stickersTab.setUnderlineColor(-16053493);
        this.stickersTab.setBackgroundColor(-14342875);
        this.containerView.addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
        this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
            public final void onPageSelected(int i) {
                StickerMasksAlert.this.m3077lambda$new$2$orgtelegramuiComponentsStickerMasksAlert(i);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.StickerMasksAlert.7
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    StickerMasksAlert.this.stickersSearchField.hideKeyboard();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickerMasksAlert.this.updateLayout(true);
            }
        });
        View topShadow = new View(context);
        topShadow.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, -1907225));
        this.containerView.addView(topShadow, LayoutHelper.createFrame(-1, 6.0f));
        if (!isVideo) {
            this.bottomTabContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.8
                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }
            };
            View view = new View(context);
            this.shadowLine = view;
            view.setBackgroundColor(301989888);
            this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
            View bottomTabContainerBackground = new View(context);
            bottomTabContainerBackground.setBackgroundColor(-14342875);
            this.bottomTabContainer.addView(bottomTabContainerBackground, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f), 83));
            this.containerView.addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + AndroidUtilities.getShadowHeight(), 83));
            LinearLayout itemsLayout = new LinearLayout(context);
            itemsLayout.setOrientation(0);
            this.bottomTabContainer.addView(itemsLayout, LayoutHelper.createFrame(-2, 48, 81));
            ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.9
                @Override // android.widget.ImageView, android.view.View
                public void setSelected(boolean selected) {
                    super.setSelected(selected);
                    Drawable background = getBackground();
                    if (Build.VERSION.SDK_INT >= 21 && background != null) {
                        int color = selected ? -9520403 : 520093695;
                        Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(color), Color.green(color), Color.blue(color)), true);
                    }
                }
            };
            this.stickersButton = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.stickersButton.setImageDrawable(Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_stickers, -1, -9520403));
            if (Build.VERSION.SDK_INT >= 21) {
                RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(520093695);
                Theme.setRippleDrawableForceSoftware(rippleDrawable);
                this.stickersButton.setBackground(rippleDrawable);
            }
            itemsLayout.addView(this.stickersButton, LayoutHelper.createLinear(70, 48));
            this.stickersButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    StickerMasksAlert.this.m3078lambda$new$3$orgtelegramuiComponentsStickerMasksAlert(view2);
                }
            });
            ImageView imageView2 = new ImageView(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.10
                @Override // android.widget.ImageView, android.view.View
                public void setSelected(boolean selected) {
                    super.setSelected(selected);
                    Drawable background = getBackground();
                    if (Build.VERSION.SDK_INT >= 21 && background != null) {
                        int color = selected ? -9520403 : 520093695;
                        Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(color), Color.green(color), Color.blue(color)), true);
                    }
                }
            };
            this.masksButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.masksButton.setImageDrawable(Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_masks_msk1, -1, -9520403));
            if (Build.VERSION.SDK_INT >= 21) {
                RippleDrawable rippleDrawable2 = (RippleDrawable) Theme.createSelectorDrawable(520093695);
                Theme.setRippleDrawableForceSoftware(rippleDrawable2);
                this.masksButton.setBackground(rippleDrawable2);
            }
            itemsLayout.addView(this.masksButton, LayoutHelper.createLinear(70, 48));
            this.masksButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    StickerMasksAlert.this.m3079lambda$new$4$orgtelegramuiComponentsStickerMasksAlert(view2);
                }
            });
        }
        checkDocuments(true);
        reloadStickersAdapter();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-StickerMasksAlert */
    public /* synthetic */ boolean m3075lambda$new$0$orgtelegramuiComponentsStickerMasksAlert(Theme.ResourcesProvider resourcesProvider, View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gridView, this.containerView.getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-StickerMasksAlert */
    public /* synthetic */ void m3076lambda$new$1$orgtelegramuiComponentsStickerMasksAlert(View view, int position) {
        if (!(view instanceof StickerEmojiCell)) {
            return;
        }
        ContentPreviewViewer.getInstance().reset();
        StickerEmojiCell cell = (StickerEmojiCell) view;
        this.delegate.onStickerSelected(cell.getParentObject(), cell.getSticker());
        dismiss();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-StickerMasksAlert */
    public /* synthetic */ void m3077lambda$new$2$orgtelegramuiComponentsStickerMasksAlert(int page) {
        int index;
        if (page == this.recentTabBum) {
            index = this.stickersGridAdapter.getPositionForPack("recent");
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i = this.recentTabBum;
            scrollSlidingTabStrip.onPageScrolled(i, i > 0 ? i : this.stickersTabOffset);
        } else {
            int scrollToPosition = this.favTabBum;
            if (page == scrollToPosition) {
                index = this.stickersGridAdapter.getPositionForPack("fav");
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
                int i2 = this.favTabBum;
                scrollSlidingTabStrip2.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else {
                int scrollToPosition2 = this.stickersTabOffset;
                int index2 = page - scrollToPosition2;
                if (index2 >= this.stickerSets[this.currentType].size()) {
                    return;
                }
                if (index2 >= this.stickerSets[this.currentType].size()) {
                    index2 = this.stickerSets[this.currentType].size() - 1;
                }
                index = this.stickersGridAdapter.getPositionForPack(this.stickerSets[this.currentType].get(index2));
            }
        }
        int currentPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (currentPosition == index) {
            return;
        }
        this.stickersLayoutManager.scrollToPositionWithOffset(index, (-this.gridView.getPaddingTop()) + this.searchFieldHeight + AndroidUtilities.dp(48.0f));
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-StickerMasksAlert */
    public /* synthetic */ void m3078lambda$new$3$orgtelegramuiComponentsStickerMasksAlert(View v) {
        if (this.currentType == 0) {
            return;
        }
        this.currentType = 0;
        updateType();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-StickerMasksAlert */
    public /* synthetic */ void m3079lambda$new$4$orgtelegramuiComponentsStickerMasksAlert(View v) {
        if (this.currentType == 1) {
            return;
        }
        this.currentType = 1;
        updateType();
    }

    private int getCurrentTop() {
        if (this.gridView.getChildCount() != 0) {
            int i = 0;
            View child = this.gridView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
            if (holder != null) {
                int paddingTop = this.gridView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                    i = child.getTop();
                }
                return paddingTop - i;
            }
            return -1000;
        }
        return -1000;
    }

    private void updateType() {
        View firstView;
        RecyclerView.ViewHolder holder;
        int top;
        if (this.gridView.getChildCount() > 0 && (holder = this.gridView.findContainingViewHolder((firstView = this.gridView.getChildAt(0)))) != null) {
            if (holder.getAdapterPosition() != 0) {
                top = -this.gridView.getPaddingTop();
            } else {
                top = (-this.gridView.getPaddingTop()) + firstView.getTop();
            }
            this.stickersLayoutManager.scrollToPositionWithOffset(0, top);
        }
        checkDocuments(true);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void setDelegate(StickerMasksAlertDelegate stickerMasksAlertDelegate) {
        this.delegate = stickerMasksAlertDelegate;
    }

    public void updateLayout(boolean animated) {
        RecyclerListView.Holder holder;
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        boolean z = false;
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder2 != null && holder2.getAdapterPosition() == 0) {
            newOffset = top;
        }
        int newOffset2 = newOffset + (-AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2);
            this.stickersTab.setTranslationY(newOffset2);
            this.stickersSearchField.setTranslationY(AndroidUtilities.dp(48.0f) + newOffset2);
            this.containerView.invalidate();
        }
        RecyclerListView.Holder holder3 = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(0);
        if (holder3 == null) {
            this.stickersSearchField.showShadow(true, animated);
        } else {
            SearchField searchField = this.stickersSearchField;
            if (holder3.itemView.getTop() < this.gridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, animated);
        }
        RecyclerView.Adapter adapter = this.gridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter && (holder = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(stickersSearchGridAdapter.getItemCount() - 1)) != null && holder.getItemViewType() == 5) {
            FrameLayout layout = (FrameLayout) holder.itemView;
            int count = layout.getChildCount();
            float tr = (-((layout.getTop() - this.searchFieldHeight) - AndroidUtilities.dp(48.0f))) / 2;
            for (int a = 0; a < count; a++) {
                layout.getChildAt(a).setTranslationY(tr);
            }
        }
        checkPanels();
    }

    private void showBottomTab(boolean show, boolean animated) {
        if (!show || this.bottomTabContainer.getTag() != null) {
            if (!show && this.bottomTabContainer.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.bottomTabContainerAnimation;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.bottomTabContainerAnimation = null;
            }
            FrameLayout frameLayout = this.bottomTabContainer;
            if (!show) {
                num = 1;
            }
            frameLayout.setTag(num);
            float f = 0.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.bottomTabContainerAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout2 = this.bottomTabContainer;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                fArr[0] = show ? 0.0f : AndroidUtilities.dp(49.0f);
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                View view = this.shadowLine;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = AndroidUtilities.dp(49.0f);
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.bottomTabContainerAnimation.setDuration(200L);
                this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.bottomTabContainerAnimation.start();
                return;
            }
            this.bottomTabContainer.setTranslationY(show ? 0.0f : AndroidUtilities.dp(49.0f));
            View view2 = this.shadowLine;
            if (!show) {
                f = AndroidUtilities.dp(49.0f);
            }
            view2.setTranslationY(f);
        }
    }

    private void updateStickerTabs() {
        if (this.stickersTab == null) {
            return;
        }
        ImageView imageView = this.stickersButton;
        if (imageView != null) {
            if (this.currentType == 0) {
                imageView.setSelected(true);
                this.masksButton.setSelected(false);
            } else {
                imageView.setSelected(false);
                this.masksButton.setSelected(true);
            }
        }
        this.recentTabBum = -2;
        this.favTabBum = -2;
        this.stickersTabOffset = 0;
        int lastPosition = this.stickersTab.getCurrentPosition();
        this.stickersTab.beginUpdate(false);
        if (this.currentType == 0 && !this.favouriteStickers.isEmpty()) {
            int i = this.stickersTabOffset;
            this.favTabBum = i;
            this.stickersTabOffset = i + 1;
            this.stickersTab.addIconTab(1, this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers));
        }
        if (!this.recentStickers[this.currentType].isEmpty()) {
            int i2 = this.stickersTabOffset;
            this.recentTabBum = i2;
            this.stickersTabOffset = i2 + 1;
            this.stickersTab.addIconTab(0, this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
        }
        this.stickerSets[this.currentType].clear();
        ArrayList<TLRPC.TL_messages_stickerSet> packs = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
        for (int a = 0; a < packs.size(); a++) {
            TLRPC.TL_messages_stickerSet pack = packs.get(a);
            if (!pack.set.archived && pack.documents != null && !pack.documents.isEmpty()) {
                this.stickerSets[this.currentType].add(pack);
            }
        }
        for (int a2 = 0; a2 < this.stickerSets[this.currentType].size(); a2++) {
            TLRPC.TL_messages_stickerSet stickerSet = this.stickerSets[this.currentType].get(a2);
            TLRPC.Document document = stickerSet.documents.get(0);
            TLObject thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
            if (thumb == null) {
                thumb = document;
            }
            View addStickerTab = this.stickersTab.addStickerTab(thumb, document, stickerSet);
            addStickerTab.setContentDescription(stickerSet.set.title + ", " + LocaleController.getString("AccDescrStickerSet", R.string.AccDescrStickerSet));
        }
        this.stickersTab.commitUpdate();
        this.stickersTab.updateTabStyles();
        if (lastPosition != 0) {
            this.stickersTab.onPageScrolled(lastPosition, lastPosition);
        }
        checkPanels();
    }

    private void checkPanels() {
        int firstTab;
        if (this.stickersTab == null) {
            return;
        }
        int count = this.gridView.getChildCount();
        View child = null;
        for (int a = 0; a < count; a++) {
            child = this.gridView.getChildAt(a);
            if (child.getBottom() > this.searchFieldHeight + AndroidUtilities.dp(48.0f)) {
                break;
            }
        }
        if (child == null) {
            return;
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int position = holder != null ? holder.getAdapterPosition() : -1;
        if (position != -1) {
            if (this.favTabBum > 0) {
                firstTab = this.favTabBum;
            } else {
                int firstTab2 = this.recentTabBum;
                if (firstTab2 > 0) {
                    firstTab = this.recentTabBum;
                } else {
                    firstTab = this.stickersTabOffset;
                }
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position), firstTab);
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).addRecentSticker(this.currentType, null, document, (int) (System.currentTimeMillis() / 1000), false);
        boolean wasEmpty = this.recentStickers[this.currentType].isEmpty();
        this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (wasEmpty) {
            updateStickerTabs();
        }
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter != null) {
            stickersSearchGridAdapter.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
    }

    private void checkDocuments(boolean force) {
        int previousCount = this.recentStickers[this.currentType].size();
        int previousCount2 = this.favouriteStickers.size();
        this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        if (this.currentType == 0) {
            for (int a = 0; a < this.favouriteStickers.size(); a++) {
                TLRPC.Document favSticker = this.favouriteStickers.get(a);
                int b = 0;
                while (true) {
                    if (b < this.recentStickers[this.currentType].size()) {
                        TLRPC.Document recSticker = this.recentStickers[this.currentType].get(b);
                        if (recSticker.dc_id != favSticker.dc_id || recSticker.id != favSticker.id) {
                            b++;
                        } else {
                            this.recentStickers[this.currentType].remove(b);
                            break;
                        }
                    }
                }
            }
        }
        if (force || previousCount != this.recentStickers[this.currentType].size() || previousCount2 != this.favouriteStickers.size()) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (!force) {
            checkPanels();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerListView recyclerListView;
        if (id == NotificationCenter.stickersDidLoad) {
            if (((Integer) args[0]).intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad) {
            boolean isGif = ((Boolean) args[0]).booleanValue();
            int type = ((Integer) args[1]).intValue();
            if (isGif) {
                return;
            }
            if (type == this.currentType || type == 2) {
                checkDocuments(false);
            }
        } else if (id == NotificationCenter.emojiLoaded && (recyclerListView = this.gridView) != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.gridView.getChildAt(a);
                if ((child instanceof StickerSetNameCell) || (child instanceof StickerEmojiCell)) {
                    child.invalidate();
                }
            }
        }
    }

    /* loaded from: classes5.dex */
    public class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int stickersPerRow;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private HashMap<Object, Integer> packStartPosition = new HashMap<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParents = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();

        public StickersGridAdapter(Context context) {
            StickerMasksAlert.this = r1;
            this.context = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(Object pack) {
            Integer pos = this.packStartPosition.get(pack);
            if (pos == null) {
                return -1;
            }
            return pos.intValue();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            Object object = this.cache.get(position);
            if (object != null) {
                if (object instanceof TLRPC.Document) {
                    return 0;
                }
                return 2;
            }
            return 1;
        }

        public int getTabForPosition(int position) {
            if (position == 0) {
                position = 1;
            }
            if (this.stickersPerRow == 0) {
                int width = StickerMasksAlert.this.gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            int row = this.positionToRow.get(position, Integer.MIN_VALUE);
            if (row == Integer.MIN_VALUE) {
                return (StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].size() - 1) + StickerMasksAlert.this.stickersTabOffset;
            }
            Object pack = this.rowStartPack.get(row);
            if (pack instanceof String) {
                return "recent".equals(pack) ? StickerMasksAlert.this.recentTabBum : StickerMasksAlert.this.favTabBum;
            }
            TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) pack;
            int idx = StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].indexOf(set);
            return StickerMasksAlert.this.stickersTabOffset + idx;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, false) { // from class: org.telegram.ui.Components.StickerMasksAlert.StickersGridAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    StickerSetNameCell cell = new StickerSetNameCell(this.context, false, StickerMasksAlert.this.resourcesProvider);
                    cell.setTitleColor(-7829368);
                    view = cell;
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<TLRPC.Document> documents;
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    cell.setSticker(sticker, this.cacheParents.get(position), false);
                    cell.setRecent(StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(sticker));
                    return;
                case 1:
                    EmptyCell cell2 = (EmptyCell) holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        int i = 1;
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TLRPC.TL_messages_stickerSet) {
                            documents = ((TLRPC.TL_messages_stickerSet) pack).documents;
                        } else if (pack instanceof String) {
                            documents = "recent".equals(pack) ? StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType] : StickerMasksAlert.this.favouriteStickers;
                        } else {
                            documents = null;
                        }
                        if (documents == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (!documents.isEmpty()) {
                            int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil(documents.size() / this.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i = height;
                            }
                            cell2.setHeight(i);
                            return;
                        } else {
                            cell2.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        }
                    }
                    cell2.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                    Object object = this.cache.get(position);
                    if (!(object instanceof TLRPC.TL_messages_stickerSet)) {
                        if (object != StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType]) {
                            if (object == StickerMasksAlert.this.favouriteStickers) {
                                cell3.setText(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers), 0);
                                return;
                            }
                            return;
                        }
                        cell3.setText(LocaleController.getString("RecentStickers", R.string.RecentStickers), 0);
                        return;
                    }
                    TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                    if (set.set != null) {
                        cell3.setText(set.set.title, 0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int width;
            String key;
            ArrayList<TLRPC.Document> documents;
            int width2 = StickerMasksAlert.this.gridView.getMeasuredWidth();
            if (width2 == 0) {
                width2 = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = width2 / AndroidUtilities.dp(72.0f);
            StickerMasksAlert.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList<TLRPC.TL_messages_stickerSet> packs = StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType];
            int startRow = 0;
            int a = -3;
            while (a < packs.size()) {
                TLRPC.TL_messages_stickerSet pack = null;
                if (a == -3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i = this.totalItems;
                    this.totalItems = i + 1;
                    sparseArray.put(i, "search");
                    startRow++;
                    width = width2;
                } else {
                    if (a == -2) {
                        if (StickerMasksAlert.this.currentType == 0) {
                            documents = StickerMasksAlert.this.favouriteStickers;
                            key = "fav";
                            this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        } else {
                            documents = null;
                            key = null;
                        }
                    } else if (a == -1) {
                        documents = StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType];
                        key = "recent";
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                    } else {
                        key = null;
                        pack = packs.get(a);
                        documents = pack.documents;
                        this.packStartPosition.put(pack, Integer.valueOf(this.totalItems));
                    }
                    if (documents == null) {
                        width = width2;
                    } else if (!documents.isEmpty()) {
                        int count = (int) Math.ceil(documents.size() / this.stickersPerRow);
                        if (pack != null) {
                            this.cache.put(this.totalItems, pack);
                        } else {
                            this.cache.put(this.totalItems, documents);
                        }
                        this.positionToRow.put(this.totalItems, startRow);
                        int b = 0;
                        while (b < documents.size()) {
                            int num = b + 1 + this.totalItems;
                            this.cache.put(num, documents.get(b));
                            if (pack != null) {
                                this.cacheParents.put(num, pack);
                            } else {
                                this.cacheParents.put(num, key);
                            }
                            this.positionToRow.put(b + 1 + this.totalItems, startRow + 1 + (b / this.stickersPerRow));
                            b++;
                            width2 = width2;
                        }
                        width = width2;
                        for (int b2 = 0; b2 < count + 1; b2++) {
                            if (pack != null) {
                                this.rowStartPack.put(startRow + b2, pack);
                            } else {
                                this.rowStartPack.put(startRow + b2, a == -1 ? "recent" : "fav");
                            }
                        }
                        int b3 = this.totalItems;
                        this.totalItems = b3 + (this.stickersPerRow * count) + 1;
                        startRow += count + 1;
                    } else {
                        width = width2;
                    }
                }
                a++;
                width2 = width;
            }
            super.notifyDataSetChanged();
        }
    }

    /* loaded from: classes5.dex */
    public class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        boolean cleared;
        private Context context;
        private int emojiSearchId;
        private int reqId2;
        private String searchQuery;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
        private HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        private HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
        private ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
        private Runnable searchRunnable = new AnonymousClass1();

        static /* synthetic */ int access$4904(StickersSearchGridAdapter x0) {
            int i = x0.emojiSearchId + 1;
            x0.emojiSearchId = i;
            return i;
        }

        /* renamed from: org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
                StickersSearchGridAdapter.this = this$1;
            }

            private void clear() {
                if (StickersSearchGridAdapter.this.cleared) {
                    return;
                }
                StickersSearchGridAdapter.this.cleared = true;
                StickersSearchGridAdapter.this.emojiStickers.clear();
                StickersSearchGridAdapter.this.emojiArrays.clear();
                StickersSearchGridAdapter.this.localPacks.clear();
                StickersSearchGridAdapter.this.localPacksByShortName.clear();
                StickersSearchGridAdapter.this.localPacksByName.clear();
            }

            @Override // java.lang.Runnable
            public void run() {
                int index;
                int index2;
                if (TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    return;
                }
                StickersSearchGridAdapter.this.cleared = false;
                final int lastId = StickersSearchGridAdapter.access$4904(StickersSearchGridAdapter.this);
                final ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                final LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                final HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getAllStickers();
                if (StickersSearchGridAdapter.this.searchQuery.length() <= 14) {
                    CharSequence emoji = StickersSearchGridAdapter.this.searchQuery;
                    int length = emoji.length();
                    int a = 0;
                    while (a < length) {
                        if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                            emoji = TextUtils.concat(emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length()));
                            length -= 2;
                            a--;
                        } else if (emoji.charAt(a) == 65039) {
                            emoji = TextUtils.concat(emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length()));
                            length--;
                            a--;
                        }
                        a++;
                    }
                    ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(emoji.toString()) : null;
                    if (newStickers != null && !newStickers.isEmpty()) {
                        clear();
                        emojiStickersArray.addAll(newStickers);
                        int size = newStickers.size();
                        for (int a2 = 0; a2 < size; a2++) {
                            TLRPC.Document document = newStickers.get(a2);
                            emojiStickersMap.put(document.id, document);
                        }
                        StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                        StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                    }
                }
                if (allStickers != null && !allStickers.isEmpty() && StickersSearchGridAdapter.this.searchQuery.length() > 1) {
                    String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                    if (!Arrays.equals(StickerMasksAlert.this.lastSearchKeyboardLanguage, newLanguage)) {
                        MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                    }
                    StickerMasksAlert.this.lastSearchKeyboardLanguage = newLanguage;
                    MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getEmojiSuggestions(StickerMasksAlert.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1
                        @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
                        public final void run(ArrayList arrayList, String str) {
                            StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1.this.m3081x56aca32f(lastId, allStickers, arrayList, str);
                        }
                    });
                }
                ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSets(StickerMasksAlert.this.currentType);
                int size2 = local.size();
                for (int a3 = 0; a3 < size2; a3++) {
                    TLRPC.TL_messages_stickerSet set = local.get(a3);
                    int index3 = AndroidUtilities.indexOfIgnoreCase(set.set.title, StickersSearchGridAdapter.this.searchQuery);
                    if (index3 >= 0) {
                        if (index3 == 0 || set.set.title.charAt(index3 - 1) == ' ') {
                            clear();
                            StickersSearchGridAdapter.this.localPacks.add(set);
                            StickersSearchGridAdapter.this.localPacksByName.put(set, Integer.valueOf(index3));
                        }
                    } else if (set.set.short_name != null && (index2 = AndroidUtilities.indexOfIgnoreCase(set.set.short_name, StickersSearchGridAdapter.this.searchQuery)) >= 0 && (index2 == 0 || set.set.short_name.charAt(index2 - 1) == ' ')) {
                        clear();
                        StickersSearchGridAdapter.this.localPacks.add(set);
                        StickersSearchGridAdapter.this.localPacksByShortName.put(set, true);
                    }
                }
                ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSets(3);
                int size3 = local2.size();
                for (int a4 = 0; a4 < size3; a4++) {
                    TLRPC.TL_messages_stickerSet set2 = local2.get(a4);
                    int index4 = AndroidUtilities.indexOfIgnoreCase(set2.set.title, StickersSearchGridAdapter.this.searchQuery);
                    if (index4 >= 0) {
                        if (index4 == 0 || set2.set.title.charAt(index4 - 1) == ' ') {
                            clear();
                            StickersSearchGridAdapter.this.localPacks.add(set2);
                            StickersSearchGridAdapter.this.localPacksByName.put(set2, Integer.valueOf(index4));
                        }
                    } else if (set2.set.short_name != null && (index = AndroidUtilities.indexOfIgnoreCase(set2.set.short_name, StickersSearchGridAdapter.this.searchQuery)) >= 0 && (index == 0 || set2.set.short_name.charAt(index - 1) == ' ')) {
                        clear();
                        StickersSearchGridAdapter.this.localPacks.add(set2);
                        StickersSearchGridAdapter.this.localPacksByShortName.put(set2, true);
                    }
                }
                boolean validEmoji = Emoji.isValidEmoji(StickersSearchGridAdapter.this.searchQuery);
                if (validEmoji) {
                    StickerMasksAlert.this.stickersSearchField.progressDrawable.startAnimation();
                    final TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                    req2.emoticon = StickersSearchGridAdapter.this.searchQuery;
                    req2.hash = 0L;
                    StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                    stickersSearchGridAdapter.reqId2 = ConnectionsManager.getInstance(StickerMasksAlert.this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1.this.m3083x8b8e956d(req2, emojiStickersArray, emojiStickersMap, tLObject, tL_error);
                        }
                    });
                }
                if ((!validEmoji || !StickersSearchGridAdapter.this.localPacks.isEmpty() || !StickersSearchGridAdapter.this.emojiStickers.isEmpty()) && StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersSearchGridAdapter) {
                    StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersSearchGridAdapter);
                }
                StickersSearchGridAdapter.this.notifyDataSetChanged();
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-StickerMasksAlert$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m3081x56aca32f(int lastId, HashMap allStickers, ArrayList param, String alias) {
                if (lastId != StickersSearchGridAdapter.this.emojiSearchId) {
                    return;
                }
                boolean added = false;
                int size = param.size();
                for (int a = 0; a < size; a++) {
                    String emoji = ((MediaDataController.KeywordResult) param.get(a)).emoji;
                    ArrayList<TLRPC.Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(emoji) : null;
                    if (newStickers != null && !newStickers.isEmpty()) {
                        clear();
                        if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(newStickers)) {
                            StickersSearchGridAdapter.this.emojiStickers.put(newStickers, emoji);
                            StickersSearchGridAdapter.this.emojiArrays.add(newStickers);
                            added = true;
                        }
                    }
                }
                if (!added) {
                    if (StickersSearchGridAdapter.this.reqId2 == 0) {
                        clear();
                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                StickersSearchGridAdapter.this.notifyDataSetChanged();
            }

            /* renamed from: lambda$run$2$org-telegram-ui-Components-StickerMasksAlert$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m3083x8b8e956d(final TLRPC.TL_messages_getStickers req2, final ArrayList emojiStickersArray, final LongSparseArray emojiStickersMap, final TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1.this.m3082x711d9c4e(req2, response, emojiStickersArray, emojiStickersMap);
                    }
                });
            }

            /* renamed from: lambda$run$1$org-telegram-ui-Components-StickerMasksAlert$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m3082x711d9c4e(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
                if (req2.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickerMasksAlert.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId2 = 0;
                    if (!(response instanceof TLRPC.TL_messages_stickers)) {
                        return;
                    }
                    TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                    int oldCount = emojiStickersArray.size();
                    int size = res.stickers.size();
                    for (int a = 0; a < size; a++) {
                        TLRPC.Document document = res.stickers.get(a);
                        if (emojiStickersMap.indexOfKey(document.id) < 0) {
                            emojiStickersArray.add(document);
                        }
                    }
                    int newCount = emojiStickersArray.size();
                    if (oldCount != newCount) {
                        StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                        if (oldCount == 0) {
                            StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                        }
                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                    }
                    if (StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersSearchGridAdapter) {
                        StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersSearchGridAdapter);
                    }
                }
            }
        }

        public StickersSearchGridAdapter(Context context) {
            StickerMasksAlert.this = r1;
            this.context = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public void search(String text) {
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(StickerMasksAlert.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                if (StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersGridAdapter) {
                    StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.searchQuery = text.toLowerCase();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            if (position == 1 && this.totalItems == 1) {
                return 5;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            return 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, false) { // from class: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.2
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new StickerSetNameCell(this.context, false, StickerMasksAlert.this.resourcesProvider);
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                    break;
                case 5:
                    FrameLayout frameLayout = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.3
                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int height = StickerMasksAlert.this.gridView.getMeasuredHeight();
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(((height - StickerMasksAlert.this.searchFieldHeight) - AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    imageView.setColorFilter(new PorterDuffColorFilter(-7038047, PorterDuff.Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 50.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(-7038047);
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
                    view = frameLayout;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Integer count;
            boolean z = false;
            int i = 1;
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    cell.setSticker(sticker, null, this.cacheParent.get(position), this.positionToEmoji.get(position), false);
                    if (StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(sticker) || StickerMasksAlert.this.favouriteStickers.contains(sticker)) {
                        z = true;
                    }
                    cell.setRecent(z);
                    return;
                case 1:
                    EmptyCell cell2 = (EmptyCell) holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TLRPC.TL_messages_stickerSet) {
                            count = Integer.valueOf(((TLRPC.TL_messages_stickerSet) pack).documents.size());
                        } else if (pack instanceof Integer) {
                            count = (Integer) pack;
                        } else {
                            count = null;
                        }
                        if (count == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (count.intValue() != 0) {
                            int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil(count.intValue() / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i = height;
                            }
                            cell2.setHeight(i);
                            return;
                        } else {
                            cell2.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        }
                    }
                    cell2.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                    Object object = this.cache.get(position);
                    if (object instanceof TLRPC.TL_messages_stickerSet) {
                        TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                        if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(set)) {
                            if (set.set != null) {
                                cell3.setText(set.set.title, 0);
                            }
                            cell3.setUrl(set.set.short_name, this.searchQuery.length());
                            return;
                        }
                        Integer start = this.localPacksByName.get(set);
                        if (set.set != null && start != null) {
                            cell3.setText(set.set.title, 0, start.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                        }
                        cell3.setUrl(null, 0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int localCount;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int startRow = 0;
            int a = -1;
            int localCount2 = this.localPacks.size();
            int emojiCount = !this.emojiArrays.isEmpty() ? 1 : 0;
            while (a < localCount2 + emojiCount) {
                if (a == -1) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i = this.totalItems;
                    this.totalItems = i + 1;
                    sparseArray.put(i, "search");
                    startRow++;
                    localCount = localCount2;
                } else if (a < localCount2) {
                    TLRPC.TL_messages_stickerSet set = this.localPacks.get(a);
                    ArrayList<TLRPC.Document> documents = set.documents;
                    if (!documents.isEmpty()) {
                        int count = (int) Math.ceil(documents.size() / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                        this.cache.put(this.totalItems, set);
                        this.positionToRow.put(this.totalItems, startRow);
                        int size = documents.size();
                        for (int b = 0; b < size; b++) {
                            int num = b + 1 + this.totalItems;
                            int row = startRow + 1 + (b / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(num, documents.get(b));
                            if (set != null) {
                                this.cacheParent.put(num, set);
                            }
                            this.positionToRow.put(num, row);
                        }
                        int N = count + 1;
                        for (int b2 = 0; b2 < N; b2++) {
                            this.rowStartPack.put(startRow + b2, set);
                        }
                        int b3 = this.totalItems;
                        this.totalItems = b3 + (StickerMasksAlert.this.stickersGridAdapter.stickersPerRow * count) + 1;
                        startRow += count + 1;
                        localCount = localCount2;
                    } else {
                        localCount = localCount2;
                    }
                } else {
                    int documentsCount = 0;
                    String lastEmoji = "";
                    int N2 = this.emojiArrays.size();
                    for (int i2 = 0; i2 < N2; i2++) {
                        ArrayList<TLRPC.Document> documents2 = this.emojiArrays.get(i2);
                        String emoji = this.emojiStickers.get(documents2);
                        if (emoji != null && !lastEmoji.equals(emoji)) {
                            lastEmoji = emoji;
                            this.positionToEmoji.put(this.totalItems + documentsCount, lastEmoji);
                        }
                        int b4 = 0;
                        int size2 = documents2.size();
                        while (b4 < size2) {
                            int num2 = this.totalItems + documentsCount;
                            int row2 = (documentsCount / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow) + startRow;
                            TLRPC.Document document = documents2.get(b4);
                            int localCount3 = localCount2;
                            this.cache.put(num2, document);
                            int N3 = N2;
                            ArrayList<TLRPC.Document> documents3 = documents2;
                            Object parent = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                            if (parent != null) {
                                this.cacheParent.put(num2, parent);
                            }
                            this.positionToRow.put(num2, row2);
                            documentsCount++;
                            b4++;
                            localCount2 = localCount3;
                            N2 = N3;
                            documents2 = documents3;
                        }
                    }
                    localCount = localCount2;
                    int count2 = (int) Math.ceil(documentsCount / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                    for (int b5 = 0; b5 < count2; b5++) {
                        this.rowStartPack.put(startRow + b5, Integer.valueOf(documentsCount));
                    }
                    int b6 = this.totalItems;
                    this.totalItems = b6 + (StickerMasksAlert.this.stickersGridAdapter.stickersPerRow * count2);
                    startRow += count2;
                }
                a++;
                localCount2 = localCount;
            }
            super.notifyDataSetChanged();
        }
    }
}
