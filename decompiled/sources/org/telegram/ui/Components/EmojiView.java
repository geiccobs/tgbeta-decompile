package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_emojiURL;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$messages_BotResults;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ListView.RecyclerListViewWithOverlayDraw;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final ViewTreeObserver.OnScrollChangedListener NOP;
    private static final Field superListenerField;
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    private FrameLayout bulletinContainer;
    private ChooseStickerActionTracker chooseStickerActionTracker;
    private long currentChatId;
    private int currentPage;
    private EmojiViewDelegate delegate;
    private Paint dotPaint;
    private DragListener dragListener;
    private EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    private RecyclerListView emojiGridView;
    private float emojiLastX;
    private float emojiLastY;
    private GridLayoutManager emojiLayoutManager;
    private Drawable emojiLockDrawable;
    private Paint emojiLockPaint;
    private RecyclerAnimationScrollHelper emojiScrollHelper;
    private EmojiSearchAdapter emojiSearchAdapter;
    private SearchField emojiSearchField;
    private int emojiSize;
    private boolean emojiSmoothScrolling;
    private AnimatorSet emojiTabShadowAnimator;
    private EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private ImageViewEmoji emojiTouchedView;
    private float emojiTouchedX;
    private float emojiTouchedY;
    private boolean expandStickersByDragg;
    private boolean firstTabUpdate;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    private BaseFragment fragment;
    private GifAdapter gifAdapter;
    private FrameLayout gifContainer;
    private RecyclerListView gifGridView;
    private Drawable[] gifIcons;
    private GifLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    private GifAdapter gifSearchAdapter;
    private SearchField gifSearchField;
    private ScrollSlidingTabStrip gifTabs;
    private int groupStickerPackNum;
    private int groupStickerPackPosition;
    private TLRPC$TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private boolean hasChatStickers;
    private boolean ignoreStickersScroll;
    private TLRPC$ChatFull info;
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    private String[] lastSearchKeyboardLanguage;
    private float lastStickersX;
    private TextView mediaBanTooltip;
    private boolean needEmojiSearch;
    private Object outlineProvider;
    private ViewPager pager;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private View shadowLine;
    private boolean showing;
    private Drawable[] stickerIcons;
    private ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private boolean stickersContainerAttached;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private RecyclerAnimationScrollHelper stickersScrollHelper;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private FrameLayout stickersTabContainer;
    private int stickersTabOffset;
    private Drawable[] tabIcons;
    private View topShadow;
    private TrendingAdapter trendingAdapter;
    private PagerSlidingTabStrip typeTabs;
    private ArrayList<View> views = new ArrayList<>();
    private boolean firstEmojiAttach = true;
    private GifSearchPreloader gifSearchPreloader = new GifSearchPreloader();
    private final Map<String, TLRPC$messages_BotResults> gifCache = new HashMap();
    private boolean firstGifAttach = true;
    private int gifRecentTabNum = -2;
    private int gifTrendingTabNum = -2;
    private int gifFirstEmojiTabNum = -2;
    private boolean firstStickersAttach = true;
    private final int[] tabsMinusDy = new int[3];
    private ObjectAnimator[] tabsYAnimators = new ObjectAnimator[3];
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = new ArrayList<>();
    private ArrayList<TLRPC$Document> recentGifs = new ArrayList<>();
    private ArrayList<TLRPC$Document> recentStickers = new ArrayList<>();
    private ArrayList<TLRPC$Document> favouriteStickers = new ArrayList<>();
    private ArrayList<TLRPC$Document> premiumStickers = new ArrayList<>();
    private ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private TLRPC$StickerSetCovered[] primaryInstallingStickerSets = new TLRPC$StickerSetCovered[10];
    private LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets = new LongSparseArray<>();
    private int[] location = new int[2];
    private int recentTabNum = -2;
    private int favTabNum = -2;
    private int trendingTabNum = -2;
    private int premiumTabNum = -2;
    private int currentBackgroundType = -1;
    private Runnable checkExpandStickerTabsRunnable = new Runnable() { // from class: org.telegram.ui.Components.EmojiView.1
        {
            EmojiView.this = this;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!EmojiView.this.stickersTab.isDragging()) {
                EmojiView.this.expandStickersByDragg = false;
                EmojiView.this.updateStickerTabsPosition();
            }
        }
    };
    private ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.EmojiView.2
        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ boolean can() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$can(this);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ boolean needMenu() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
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
        public boolean needSend() {
            return true;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        {
            EmojiView.this = this;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z, int i) {
            EmojiView.this.delegate.onStickerSelected(null, tLRPC$Document, str, obj, null, z, i);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean canSchedule() {
            return EmojiView.this.delegate.canSchedule();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean isInScheduleMode() {
            return EmojiView.this.delegate.isInScheduleMode();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
            if (tLRPC$InputStickerSet == null) {
                return;
            }
            EmojiView.this.delegate.onShowStickerSet(null, tLRPC$InputStickerSet);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendGif(Object obj, Object obj2, boolean z, int i) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected(null, obj, null, obj2, z, i);
            } else if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter) {
            } else {
                EmojiView.this.delegate.onGifSelected(null, obj, null, obj2, z, i);
            }
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void gifAddedOrDeleted() {
            EmojiView.this.updateRecentGifs();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public long getDialogId() {
            return EmojiView.this.delegate.getDialogId();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public String getQuery(boolean z) {
            if (z) {
                if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter) {
                    return null;
                }
                return EmojiView.this.gifSearchAdapter.lastSearchImageString;
            } else if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                return null;
            } else {
                return EmojiView.this.emojiSearchAdapter.lastSearchEmojiString;
            }
        }
    };
    android.graphics.Rect rect = new android.graphics.Rect();
    private int searchFieldHeight = AndroidUtilities.dp(64.0f);
    private String[] emojiTitles = {LocaleController.getString("Emoji1", R.string.Emoji1), LocaleController.getString("Emoji2", R.string.Emoji2), LocaleController.getString("Emoji3", R.string.Emoji3), LocaleController.getString("Emoji4", R.string.Emoji4), LocaleController.getString("Emoji5", R.string.Emoji5), LocaleController.getString("Emoji6", R.string.Emoji6), LocaleController.getString("Emoji7", R.string.Emoji7), LocaleController.getString("Emoji8", R.string.Emoji8)};

    /* loaded from: classes3.dex */
    public static class CustomEmoji {
        public long documentId;
        public TLRPC$TL_messages_stickerSet stickerSet;
    }

    /* loaded from: classes3.dex */
    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    /* loaded from: classes3.dex */
    public interface EmojiViewDelegate {

        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiViewDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$canSchedule(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate emojiViewDelegate) {
                return 0L;
            }

            public static float $default$getProgressToSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return 0.0f;
            }

            public static int $default$getThreadId(EmojiViewDelegate emojiViewDelegate) {
                return 0;
            }

            public static void $default$invalidateEnterView(EmojiViewDelegate emojiViewDelegate) {
            }

            public static boolean $default$isExpanded(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate emojiViewDelegate, View view, Object obj, String str, Object obj2, boolean z, int i) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate emojiViewDelegate, View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate emojiViewDelegate, long j) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$showTrendingStickersAlert(EmojiViewDelegate emojiViewDelegate, TrendingStickersLayout trendingStickersLayout) {
            }
        }

        boolean canSchedule();

        long getDialogId();

        float getProgressToSearchOpened();

        int getThreadId();

        void invalidateEnterView();

        boolean isExpanded();

        boolean isInScheduleMode();

        boolean isSearchOpened();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onCustomEmojiSelected(long j, String str);

        void onEmojiSelected(String str);

        void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet);

        void onStickerSelected(View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i);

        void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void onStickersGroupClick(long j);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout);
    }

    public static /* synthetic */ void lambda$static$0() {
    }

    public void updateEmojiTabs() {
    }

    public static /* synthetic */ void access$4300(EmojiView emojiView) {
        emojiView.openPremiumAnimatedEmojiFeature();
    }

    public AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
        AnimatedEmojiSpan[] animatedEmojiSpanArr = new AnimatedEmojiSpan[this.emojiGridView.getChildCount()];
        for (int i = 0; i < this.emojiGridView.getChildCount(); i++) {
            View childAt = this.emojiGridView.getChildAt(i);
            if (childAt instanceof ImageViewEmoji) {
                animatedEmojiSpanArr[i] = ((ImageViewEmoji) childAt).getSpan();
            }
        }
        return animatedEmojiSpanArr;
    }

    static {
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException unused) {
        }
        superListenerField = field;
        NOP = EmojiView$$ExternalSyntheticLambda4.INSTANCE;
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.searchEditText.setEnabled(z);
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.searchEditText.setEnabled(z);
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.searchEditText.setEnabled(z);
        }
    }

    /* loaded from: classes3.dex */
    public class SearchField extends FrameLayout {
        private View backgroundView;
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;
        private AnimatorSet shadowAnimator;
        private View shadowView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SearchField(Context context, int i) {
            super(context);
            EmojiView.this = r13;
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(r13.getThemedColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            this.backgroundView = view2;
            view2.setBackgroundColor(r13.getThemedColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, r13.searchFieldHeight));
            View view3 = new View(context);
            this.searchBackground = view3;
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), r13.getThemedColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(r13.getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2(r13) { // from class: org.telegram.ui.Components.EmojiView.SearchField.1
                {
                    SearchField.this = this;
                }

                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                protected int getCurrentColor() {
                    return EmojiView.this.getThemedColor("chat_emojiSearchIcon");
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    EmojiView.SearchField.this.lambda$new$0(view4);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context, r13, i) { // from class: org.telegram.ui.Components.EmojiView.SearchField.2
                final /* synthetic */ int val$type;

                {
                    SearchField.this = this;
                    this.val$type = i;
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(motionEvent);
                    }
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            SearchField searchField = SearchField.this;
                            EmojiView.this.openSearch(searchField);
                        }
                        EmojiViewDelegate emojiViewDelegate = EmojiView.this.delegate;
                        int i2 = 1;
                        if (this.val$type == 1) {
                            i2 = 2;
                        }
                        emojiViewDelegate.onSearchOpenClose(i2);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(r13.getThemedColor("chat_emojiSearchIcon"));
            this.searchEditText.setTextColor(r13.getThemedColor("windowBackgroundWhiteBlackText"));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            if (i == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", R.string.SearchStickersHint));
            } else if (i == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", R.string.SearchEmojiHint));
            } else if (i == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
            this.searchEditText.setCursorColor(r13.getThemedColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(r13, i) { // from class: org.telegram.ui.Components.EmojiView.SearchField.3
                final /* synthetic */ int val$type;

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                {
                    SearchField.this = this;
                    this.val$type = i;
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    boolean z = false;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        z = true;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    int i2 = this.val$type;
                    if (i2 == 0) {
                        EmojiView.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i2 == 1) {
                        EmojiView.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i2 != 2) {
                    } else {
                        EmojiView.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public void showShadow(boolean z, boolean z2) {
            if (!z || this.shadowView.getTag() != null) {
                if (!z && this.shadowView.getTag() != null) {
                    return;
                }
                AnimatorSet animatorSet = this.shadowAnimator;
                Integer num = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!z) {
                    num = 1;
                }
                view.setTag(num);
                float f = 1.0f;
                if (z2) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200L);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.SearchField.4
                        {
                            SearchField.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!z) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int i) {
            EmojiView.this = r1;
            this.type = i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
            } else if (i == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                if (EmojiView.this.ignoreStickersScroll) {
                    EmojiView.this.ignoreStickersScroll = false;
                }
                this.smoothScrolling = false;
            } else {
                if (i == 1) {
                    if (EmojiView.this.ignoreStickersScroll) {
                        EmojiView.this.ignoreStickersScroll = false;
                    }
                    SearchField searchFieldForType = EmojiView.this.getSearchFieldForType(this.type);
                    if (searchFieldForType != null) {
                        searchFieldForType.hideKeyboard();
                    }
                    this.smoothScrolling = false;
                }
                if (!this.smoothScrolling) {
                    EmojiView.this.stopAnimatingTabsY(this.type);
                }
                if (this.type != 0) {
                    return;
                }
                if (EmojiView.this.chooseStickerActionTracker == null) {
                    EmojiView.this.createStickersChooseActionTracker();
                }
                EmojiView.this.chooseStickerActionTracker.doSomeAction();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, i2);
            checkSearchFieldScroll();
            if (!this.smoothScrolling) {
                EmojiView.this.checkBottomTabScroll(i2);
            }
        }

        private void checkSearchFieldScroll() {
            int i = this.type;
            if (i == 0) {
                EmojiView.this.checkStickersSearchFieldScroll(false);
            } else if (i == 1) {
                EmojiView.this.checkEmojiSearchFieldScroll(false);
            } else if (i != 2) {
            } else {
                EmojiView.this.checkGifSearchFieldScroll(false);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class DraggableScrollSlidingTabStrip extends ScrollSlidingTabStrip {
        private float downX;
        private float downY;
        private boolean draggingHorizontally;
        private boolean draggingVertically;
        private boolean first = true;
        private float lastTranslateX;
        private float lastX;
        private boolean startedScroll;
        private final int touchSlop;
        private VelocityTracker vTracker;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DraggableScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            EmojiView.this = r1;
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(motionEvent.getRawY() - this.downY) >= this.touchSlop) {
                this.draggingVertically = true;
                this.downY = motionEvent.getRawY();
                EmojiView.this.dragListener.onDragStart();
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
                return true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onTouchEvent(motionEvent);
            }
            if (this.first) {
                this.first = false;
                this.lastX = motionEvent.getX();
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                EmojiView.this.lastStickersX = motionEvent.getRawX();
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(motionEvent.getRawX() - this.downX) >= this.touchSlop && canScrollHorizontally((int) (this.downX - motionEvent.getRawX()))) {
                    this.draggingHorizontally = true;
                    AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
                    EmojiView.this.expandStickersByDragg = true;
                    EmojiView.this.updateStickerTabsPosition();
                } else if (Math.abs(motionEvent.getRawY() - this.downY) >= this.touchSlop) {
                    this.draggingVertically = true;
                    this.downY = motionEvent.getRawY();
                    EmojiView.this.dragListener.onDragStart();
                    if (this.startedScroll) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                }
            }
            if (EmojiView.this.expandStickersByDragg && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3)) {
                AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(motionEvent);
                if (motionEvent.getAction() != 1 && motionEvent.getAction() != 3) {
                    EmojiView.this.dragListener.onDrag(Math.round(motionEvent.getRawY() - this.downY));
                } else {
                    this.vTracker.computeCurrentVelocity(1000);
                    float yVelocity = this.vTracker.getYVelocity();
                    this.vTracker.recycle();
                    this.vTracker = null;
                    if (motionEvent.getAction() == 1) {
                        EmojiView.this.dragListener.onDragEnd(yVelocity);
                    } else {
                        EmojiView.this.dragListener.onDragCancel();
                    }
                    this.first = true;
                    this.draggingHorizontally = false;
                    this.draggingVertically = false;
                }
                cancelLongPress();
                return true;
            }
            float translationX = getTranslationX();
            if (getScrollX() == 0 && translationX == 0.0f) {
                if (!this.startedScroll && this.lastX - motionEvent.getX() < 0.0f) {
                    if (EmojiView.this.pager.beginFakeDrag()) {
                        this.startedScroll = true;
                        this.lastTranslateX = getTranslationX();
                    }
                } else if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            if (this.startedScroll) {
                motionEvent.getX();
                try {
                    this.lastTranslateX = translationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception unused) {
                    }
                    this.startedScroll = false;
                    FileLog.e(e);
                }
            }
            this.lastX = motionEvent.getX();
            if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                this.first = true;
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            return this.startedScroll || super.onTouchEvent(motionEvent);
        }
    }

    /* loaded from: classes3.dex */
    public class ImageViewEmoji extends ImageView {
        private ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public ImageReceiver imageReceiver;
        private boolean isRecent;
        public int position;
        private AnimatedEmojiSpan span;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ImageViewEmoji(Context context) {
            super(context);
            EmojiView.this = r2;
            setScaleType(ImageView.ScaleType.CENTER);
            setBackground(Theme.createRadSelectorDrawable(r2.getThemedColor("listSelectorSDK21"), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        }

        public void sendEmoji(String str) {
            String str2;
            if (getSpan() != null) {
                if (EmojiView.this.delegate == null) {
                    return;
                }
                long j = getSpan().documentId;
                TLRPC$Document tLRPC$Document = null;
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
                String str3 = null;
                for (int i = 0; EmojiView.this.emojiAdapter.emojipacks != null && i < EmojiView.this.emojiAdapter.emojipacks.size(); i++) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) EmojiView.this.emojiAdapter.emojipacks.get(i);
                    int i2 = 0;
                    while (true) {
                        if (i2 >= tLRPC$TL_messages_stickerSet2.packs.size()) {
                            break;
                        }
                        TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet2.packs.get(i2);
                        if (tLRPC$TL_stickerPack != null && tLRPC$TL_stickerPack.documents.contains(Long.valueOf(j))) {
                            str3 = tLRPC$TL_stickerPack.emoticon;
                            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
                            break;
                        }
                        i2++;
                    }
                    if (tLRPC$TL_messages_stickerSet != null) {
                        break;
                    }
                }
                if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= tLRPC$TL_messages_stickerSet.documents.size()) {
                            break;
                        } else if (tLRPC$TL_messages_stickerSet.documents.get(i3).id == j) {
                            tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i3);
                            break;
                        } else {
                            i3++;
                        }
                    }
                }
                if (tLRPC$Document == null) {
                    tLRPC$Document = AnimatedEmojiDrawable.findDocument(EmojiView.this.currentAccount, j);
                }
                if (str3 == null && tLRPC$Document != null) {
                    str3 = MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document);
                }
                if (MessageObject.isFreeEmoji(tLRPC$Document) || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                    EmojiView.this.showBottomTab(true, true);
                    EmojiView.this.addEmojiToRecent("animated_" + j);
                    EmojiView.this.delegate.onCustomEmojiSelected(j, str3);
                    return;
                }
                EmojiView.this.showBottomTab(false, true);
                BulletinFactory of = BulletinFactory.of(EmojiView.this.bulletinContainer, EmojiView.this.resourcesProvider);
                SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint", R.string.UnlockPremiumEmojiHint));
                String string = LocaleController.getString("PremiumMore", R.string.PremiumMore);
                final EmojiView emojiView = EmojiView.this;
                of.createEmojiBulletin(tLRPC$Document, replaceTags, string, new Runnable() { // from class: org.telegram.ui.Components.EmojiView$ImageViewEmoji$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.access$4300(EmojiView.this);
                    }
                }).show();
                return;
            }
            EmojiView.this.showBottomTab(true, true);
            String str4 = str != null ? str : (String) getTag();
            new SpannableStringBuilder().append((CharSequence) str4);
            if (str != null) {
                if (EmojiView.this.delegate == null) {
                    return;
                }
                EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str));
                return;
            }
            if (!this.isRecent && (str2 = Emoji.emojiColor.get(str4)) != null) {
                str4 = EmojiView.addColorToCode(str4, str2);
            }
            EmojiView.this.addEmojiToRecent(str4);
            if (EmojiView.this.delegate == null) {
                return;
            }
            EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str4));
        }

        public void setImageDrawable(Drawable drawable, boolean z) {
            super.setImageDrawable(drawable);
            this.isRecent = z;
        }

        public void setSpan(AnimatedEmojiSpan animatedEmojiSpan) {
            this.span = animatedEmojiSpan;
        }

        public AnimatedEmojiSpan getSpan() {
            return this.span;
        }

        @Override // android.widget.ImageView, android.view.View
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.view.View");
        }

        @Override // android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPopupWindow extends PopupWindow {
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow(EmojiView emojiView, View view, int i, int i2) {
            super(view, i, i2);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception unused) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            ViewTreeObserver viewTreeObserver;
            if (this.mSuperScrollListener == null || (viewTreeObserver = this.mViewTreeObserver) == null) {
                return;
            }
            if (viewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
            }
            this.mViewTreeObserver = null;
        }

        private void registerListener(View view) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver2 = this.mViewTreeObserver;
                if (viewTreeObserver == viewTreeObserver2) {
                    return;
                }
                if (viewTreeObserver2 != null && viewTreeObserver2.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = viewTreeObserver;
                if (viewTreeObserver == null) {
                    return;
                }
                viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
            }
        }

        @Override // android.widget.PopupWindow
        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                registerListener(view);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.widget.PopupWindow
        public void update(View view, int i, int i2, int i3, int i4) {
            super.update(view, i, i2, i3, i4);
            registerListener(view);
        }

        @Override // android.widget.PopupWindow
        public void update(View view, int i, int i2) {
            super.update(view, i, i2);
            registerListener(view);
        }

        @Override // android.widget.PopupWindow
        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            unregisterListener();
        }

        @Override // android.widget.PopupWindow
        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception unused) {
            }
            unregisterListener();
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiColorPickerView extends View {
        private int arrowX;
        private String currentEmoji;
        private int selection;
        private Paint rectPaint = new Paint(1);
        private RectF rect = new RectF();
        private Drawable backgroundDrawable = getResources().getDrawable(R.drawable.stickers_back_all);
        private Drawable arrowDrawable = getResources().getDrawable(R.drawable.stickers_back_arrow);

        public void setEmoji(String str, int i) {
            this.currentEmoji = str;
            this.arrowX = i;
            this.rectPaint.setColor(788529152);
            invalidate();
        }

        public void setSelection(int i) {
            if (this.selection == i) {
                return;
            }
            this.selection = i;
            invalidate();
        }

        public int getSelection() {
            return this.selection;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiColorPickerView(Context context) {
            super(context);
            EmojiView.this = r3;
            Theme.setDrawableColor(this.backgroundDrawable, r3.getThemedColor("dialogBackground"));
            Theme.setDrawableColor(this.arrowDrawable, r3.getThemedColor("dialogBackground"));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int i = 0;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            float f = 55.5f;
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                while (i < 6) {
                    int dp4 = (EmojiView.this.emojiSize * i) + AndroidUtilities.dp((i * 4) + 5);
                    int dp5 = AndroidUtilities.dp(9.0f);
                    if (this.selection == i) {
                        this.rect.set(dp4, dp5 - ((int) AndroidUtilities.dpf2(3.5f)), EmojiView.this.emojiSize + dp4, EmojiView.this.emojiSize + dp5 + AndroidUtilities.dp(3.0f));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String str = this.currentEmoji;
                    if (i != 0) {
                        str = EmojiView.addColorToCode(str, i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? "" : "🏿" : "🏾" : "🏽" : "🏼" : "🏻");
                    }
                    Drawable emojiBigDrawable = Emoji.getEmojiBigDrawable(str);
                    if (emojiBigDrawable != null) {
                        emojiBigDrawable.setBounds(dp4, dp5, EmojiView.this.emojiSize + dp4, EmojiView.this.emojiSize + dp5);
                        emojiBigDrawable.draw(canvas);
                    }
                    i++;
                }
            }
        }
    }

    public EmojiView(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, Context context, boolean z4, TLRPC$ChatFull tLRPC$ChatFull, ViewGroup viewGroup, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.fragment = baseFragment;
        this.resourcesProvider = resourcesProvider;
        int themedColor = getThemedColor("chat_emojiBottomPanelIcon");
        int argb = Color.argb(30, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor));
        this.needEmojiSearch = z4;
        this.tabIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_smiles, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_gif, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_stickers, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected"))};
        Drawable createEmojiIconSelectorDrawable = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new1, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected"));
        this.searchIconDrawable = createEmojiIconSelectorDrawable;
        Drawable createEmojiIconSelectorDrawable2 = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new2, getThemedColor("chat_emojiPanelStickerPackSelectorLine"), getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
        this.searchIconDotDrawable = createEmojiIconSelectorDrawable2;
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_recent, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_faves, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new3, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), new LayerDrawable(new Drawable[]{createEmojiIconSelectorDrawable, createEmojiIconSelectorDrawable2})};
        this.gifIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_recent, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_gifs_trending, getThemedColor("chat_emojiBottomPanelIcon"), getThemedColor("chat_emojiPanelIconSelected"))};
        this.info = tLRPC$ChatFull;
        Paint paint = new Paint(1);
        this.dotPaint = paint;
        paint.setColor(getThemedColor("chat_emojiPanelNewTrending"));
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            this.outlineProvider = new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.EmojiView.3
                @Override // android.view.ViewOutlineProvider
                @TargetApi(R.styleable.MapAttrs_uiZoomGestures)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), AndroidUtilities.dp(6.0f));
                }
            };
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.emojiContainer = frameLayout;
        this.views.add(frameLayout);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiView.4
            private boolean ignoreLayout;
            private Path lockPath;
            private HashMap<Integer, TouchDownInfo> touches;
            SparseArray<ArrayList<ImageViewEmoji>> viewsGroupedByLines = new SparseArray<>();
            ArrayList<DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
            ArrayList<ArrayList<ImageViewEmoji>> unusedArrays = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();
            private SparseIntArray headerWidthsCache = new SparseIntArray();

            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i2, int i3) {
                this.ignoreLayout = true;
                EmojiView.this.emojiLayoutManager.setSpanCount(Math.max(1, View.MeasureSpec.getSize(i2) / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
                this.ignoreLayout = false;
                super.onMeasure(i2, i3);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z5, int i2, int i3, int i4, int i5) {
                if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                    this.ignoreLayout = true;
                    EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(1, 0);
                    EmojiView.this.firstEmojiAttach = false;
                    this.ignoreLayout = false;
                }
                super.onLayout(z5, i2, i3, i4, i5);
                EmojiView.this.checkEmojiSearchFieldScroll(true);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            /* JADX WARN: Removed duplicated region for block: B:20:0x006f  */
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean onTouchEvent(android.view.MotionEvent r12) {
                /*
                    Method dump skipped, instructions count: 456
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass4.onTouchEvent(android.view.MotionEvent):boolean");
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrolled(int i2, int i3) {
                super.onScrolled(i2, i3);
                EmojiView emojiView = EmojiView.this;
                emojiView.animatedEmojiDrawables = AnimatedEmojiSpan.update(1, this, emojiView.getAnimatedEmojiSpans(), EmojiView.this.animatedEmojiDrawables);
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrollStateChanged(int i2) {
                super.onScrollStateChanged(i2);
                if (i2 == 0) {
                    if (canScrollVertically(-1) && canScrollVertically(1)) {
                        return;
                    }
                    EmojiView.this.showBottomTab(true, true);
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                drawDashedOutlines(canvas);
                for (int i2 = 0; i2 < this.viewsGroupedByLines.size(); i2++) {
                    ArrayList<ImageViewEmoji> valueAt = this.viewsGroupedByLines.valueAt(i2);
                    valueAt.clear();
                    this.unusedArrays.add(valueAt);
                }
                this.viewsGroupedByLines.clear();
                if (EmojiView.this.animatedEmojiDrawables != null && EmojiView.this.emojiGridView != null) {
                    for (int i3 = 0; i3 < EmojiView.this.emojiGridView.getChildCount(); i3++) {
                        View childAt = EmojiView.this.emojiGridView.getChildAt(i3);
                        if (childAt instanceof ImageViewEmoji) {
                            ArrayList<ImageViewEmoji> arrayList = this.viewsGroupedByLines.get(childAt.getTop());
                            if (arrayList == null) {
                                if (!this.unusedArrays.isEmpty()) {
                                    ArrayList<ArrayList<ImageViewEmoji>> arrayList2 = this.unusedArrays;
                                    arrayList = arrayList2.remove(arrayList2.size() - 1);
                                } else {
                                    arrayList = new ArrayList<>();
                                }
                                this.viewsGroupedByLines.put(childAt.getTop(), arrayList);
                            }
                            arrayList.add((ImageViewEmoji) childAt);
                        }
                    }
                }
                this.lineDrawablesTmp.clear();
                this.lineDrawablesTmp.addAll(this.lineDrawables);
                this.lineDrawables.clear();
                long currentTimeMillis = System.currentTimeMillis();
                int i4 = 0;
                while (true) {
                    DrawingInBackgroundLine drawingInBackgroundLine = null;
                    if (i4 < this.viewsGroupedByLines.size()) {
                        ArrayList<ImageViewEmoji> valueAt2 = this.viewsGroupedByLines.valueAt(i4);
                        ImageViewEmoji imageViewEmoji = valueAt2.get(0);
                        int childAdapterPosition = getChildAdapterPosition(imageViewEmoji);
                        int i5 = 0;
                        while (true) {
                            if (i5 >= this.lineDrawablesTmp.size()) {
                                break;
                            } else if (this.lineDrawablesTmp.get(i5).position == childAdapterPosition) {
                                drawingInBackgroundLine = this.lineDrawablesTmp.get(i5);
                                this.lineDrawablesTmp.remove(i5);
                                break;
                            } else {
                                i5++;
                            }
                        }
                        if (drawingInBackgroundLine == null) {
                            if (!this.unusedLineDrawables.isEmpty()) {
                                ArrayList<DrawingInBackgroundLine> arrayList3 = this.unusedLineDrawables;
                                drawingInBackgroundLine = arrayList3.remove(arrayList3.size() - 1);
                            } else {
                                drawingInBackgroundLine = new DrawingInBackgroundLine();
                            }
                            drawingInBackgroundLine.position = childAdapterPosition;
                            drawingInBackgroundLine.onAttachToWindow();
                        }
                        this.lineDrawables.add(drawingInBackgroundLine);
                        drawingInBackgroundLine.imageViewEmojis = valueAt2;
                        canvas.save();
                        canvas.translate(0.0f, imageViewEmoji.getY() + imageViewEmoji.getPaddingTop());
                        drawingInBackgroundLine.draw(canvas, currentTimeMillis, getMeasuredWidth(), imageViewEmoji.getMeasuredHeight() - imageViewEmoji.getPaddingBottom(), 1.0f);
                        canvas.restore();
                        i4++;
                    }
                }
                for (int i6 = 0; i6 < this.lineDrawablesTmp.size(); i6++) {
                    if (this.unusedLineDrawables.size() < 3) {
                        this.unusedLineDrawables.add(this.lineDrawablesTmp.get(i6));
                        this.lineDrawablesTmp.get(i6).imageViewEmojis = null;
                        this.lineDrawablesTmp.get(i6).reset();
                    } else {
                        this.lineDrawablesTmp.get(i6).onDetachFromWindow();
                    }
                }
                this.lineDrawablesTmp.clear();
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                EmojiView emojiView = EmojiView.this;
                emojiView.animatedEmojiDrawables = AnimatedEmojiSpan.update(1, this, emojiView.getAnimatedEmojiSpans(), EmojiView.this.animatedEmojiDrawables);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AnimatedEmojiSpan.release(this, EmojiView.this.animatedEmojiDrawables);
                for (int i2 = 0; i2 < this.lineDrawables.size(); i2++) {
                    this.lineDrawables.get(i2).onDetachFromWindow();
                }
                for (int i3 = 0; i3 < this.unusedLineDrawables.size(); i3++) {
                    this.unusedLineDrawables.get(i3).onDetachFromWindow();
                }
                this.lineDrawables.clear();
            }

            /* renamed from: org.telegram.ui.Components.EmojiView$4$TouchDownInfo */
            /* loaded from: classes3.dex */
            class TouchDownInfo {
                View view;
                float x;
                float y;

                TouchDownInfo(AnonymousClass4 anonymousClass4) {
                }
            }

            /* renamed from: org.telegram.ui.Components.EmojiView$4$DrawingInBackgroundLine */
            /* loaded from: classes3.dex */
            public class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
                ArrayList<ImageViewEmoji> drawInBackgroundViews = new ArrayList<>();
                ArrayList<ImageViewEmoji> imageViewEmojis;
                public int position;

                DrawingInBackgroundLine() {
                    AnonymousClass4.this = r1;
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void prepareDraw(long j) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    this.drawInBackgroundViews.clear();
                    for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                        ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                        if (imageViewEmoji.getSpan() != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiView.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null) {
                            animatedEmojiDrawable.update(j);
                            imageViewEmoji.backgroundThreadDrawHolder = animatedEmojiDrawable.getImageReceiver().setDrawInBackgroundThread(imageViewEmoji.backgroundThreadDrawHolder);
                            imageViewEmoji.backgroundThreadDrawHolder.time = j;
                            animatedEmojiDrawable.setAlpha(255);
                            android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                            rect.set(imageViewEmoji.getLeft() + imageViewEmoji.getPaddingLeft(), 0, imageViewEmoji.getRight() - imageViewEmoji.getPaddingRight(), (imageViewEmoji.getMeasuredHeight() - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom());
                            imageViewEmoji.backgroundThreadDrawHolder.setBounds(rect);
                            imageViewEmoji.imageReceiver = animatedEmojiDrawable.getImageReceiver();
                            this.drawInBackgroundViews.add(imageViewEmoji);
                        }
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void drawInBackground(Canvas canvas) {
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        ImageViewEmoji imageViewEmoji = this.drawInBackgroundViews.get(i);
                        imageViewEmoji.imageReceiver.draw(canvas, imageViewEmoji.backgroundThreadDrawHolder);
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                protected void drawInUiThread(Canvas canvas) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    if (this.imageViewEmojis != null) {
                        for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                            ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                            if (imageViewEmoji.getSpan() != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiView.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null && animatedEmojiDrawable.getImageReceiver() != null) {
                                animatedEmojiDrawable.setAlpha(255);
                                android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                                rect.set(imageViewEmoji.getLeft() + imageViewEmoji.getPaddingLeft(), 0, imageViewEmoji.getRight() - imageViewEmoji.getPaddingRight(), (imageViewEmoji.getMeasuredHeight() - imageViewEmoji.getPaddingBottom()) - imageViewEmoji.getPaddingTop());
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
                        ImageViewEmoji imageViewEmoji = this.drawInBackgroundViews.get(i);
                        if (imageViewEmoji.backgroundThreadDrawHolder != null) {
                            imageViewEmoji.backgroundThreadDrawHolder.release();
                        }
                    }
                    EmojiView.this.emojiGridView.invalidate();
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                View view;
                boolean z5 = motionEvent.getActionMasked() == 5 || motionEvent.getActionMasked() == 0;
                boolean z6 = motionEvent.getActionMasked() == 6 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3;
                if (z5 || z6) {
                    int actionIndex = motionEvent.getActionIndex();
                    int pointerId = motionEvent.getPointerId(actionIndex);
                    if (this.touches == null) {
                        this.touches = new HashMap<>();
                    }
                    float x = motionEvent.getX(actionIndex);
                    float y = motionEvent.getY(actionIndex);
                    View findChildViewUnder = findChildViewUnder(x, y);
                    if (!z5) {
                        TouchDownInfo remove = this.touches.remove(Integer.valueOf(pointerId));
                        if (findChildViewUnder != null && remove != null && Math.sqrt(Math.pow(x - remove.x, 2.0d) + Math.pow(y - remove.y, 2.0d)) < AndroidUtilities.touchSlop * 3.0f) {
                            View view2 = remove.view;
                            if (view2 instanceof ImageViewEmoji) {
                                ((ImageViewEmoji) view2).sendEmoji(null);
                                try {
                                    performHapticFeedback(3, 1);
                                } catch (Exception unused) {
                                }
                            }
                        }
                        if (remove != null && (view = remove.view) != null && Build.VERSION.SDK_INT >= 21 && (view.getBackground() instanceof RippleDrawable)) {
                            remove.view.getBackground().setState(new int[0]);
                        }
                    } else if (findChildViewUnder != null) {
                        TouchDownInfo touchDownInfo = new TouchDownInfo(this);
                        touchDownInfo.x = x;
                        touchDownInfo.y = y;
                        SystemClock.elapsedRealtime();
                        touchDownInfo.view = findChildViewUnder;
                        if (Build.VERSION.SDK_INT >= 21 && (findChildViewUnder.getBackground() instanceof RippleDrawable)) {
                            ((RippleDrawable) findChildViewUnder.getBackground()).setState(new int[]{16842919, 16842910});
                        }
                        this.touches.put(Integer.valueOf(pointerId), touchDownInfo);
                    } else {
                        this.touches.remove(Integer.valueOf(pointerId));
                    }
                }
                return super.dispatchTouchEvent(motionEvent) || !this.touches.isEmpty();
            }

            public void drawDashedOutlines(Canvas canvas) {
                float f;
                int i2;
                Canvas canvas2;
                float f2;
                float f3;
                float f4;
                int i3;
                int i4;
                Canvas canvas3 = canvas;
                if (EmojiView.this.emojiAdapter == null || EmojiView.this.emojiAdapter.premiumPackPositions == null) {
                    return;
                }
                float dp = AndroidUtilities.dp(20.0f);
                float dp2 = AndroidUtilities.dp(5.0f);
                float dp3 = AndroidUtilities.dp(11.0f);
                float measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) / EmojiView.this.emojiLayoutManager.getSpanCount();
                int i5 = 0;
                while (i5 < EmojiView.this.emojiAdapter.premiumPackPositions.size()) {
                    Pair pair = (Pair) EmojiView.this.emojiAdapter.premiumPackPositions.get(i5);
                    StickerSetNameCell stickerSetNameCell = null;
                    float measuredHeight = getMeasuredHeight();
                    int i6 = -1;
                    float f5 = 0.0f;
                    for (int i7 = 0; i7 < getChildCount(); i7++) {
                        View childAt = getChildAt(i7);
                        if (childAt instanceof ImageViewEmoji) {
                            i4 = ((ImageViewEmoji) childAt).position;
                        } else if (childAt instanceof StickerSetNameCell) {
                            i4 = ((StickerSetNameCell) childAt).position;
                        } else if (childAt instanceof PremiumButtonHolder) {
                            i4 = ((PremiumButtonHolder) childAt).position;
                        } else {
                            i4 = getChildAdapterPosition(childAt);
                        }
                        if (i4 >= ((Integer) pair.first).intValue() && i4 <= ((Integer) pair.second).intValue()) {
                            if (stickerSetNameCell == null) {
                                stickerSetNameCell = childAt;
                                i6 = i4;
                            }
                            float min = Math.min(measuredHeight, childAt.getTop() + childAt.getTranslationY());
                            f5 = Math.max(f5, childAt.getBottom() + childAt.getTranslationY());
                            measuredHeight = min;
                        }
                    }
                    if (stickerSetNameCell == null) {
                        canvas2 = canvas3;
                        f = measuredWidth;
                        i2 = i5;
                    } else {
                        float dp4 = f5 + AndroidUtilities.dp(6.0f);
                        float f6 = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() ? 0.0f : 1.0f;
                        int intValue = i6 - ((Integer) pair.first).intValue();
                        if (intValue == 0) {
                            f2 = stickerSetNameCell.getTop() + stickerSetNameCell.getTranslationY() + AndroidUtilities.dp(20.0f);
                        } else {
                            f2 = (stickerSetNameCell.getTop() + stickerSetNameCell.getTranslationY()) - AndroidUtilities.dp(16.0f);
                        }
                        if (intValue > 0) {
                            i2 = i5;
                            f2 -= ((intValue - 1) / EmojiView.this.emojiLayoutManager.getSpanCount()) * measuredWidth;
                        } else {
                            i2 = i5;
                        }
                        if (intValue == 0) {
                            f4 = stickerSetNameCell.getBottom();
                            f3 = stickerSetNameCell.getTranslationY();
                        } else {
                            f4 = stickerSetNameCell.getTop();
                            f3 = stickerSetNameCell.getTranslationY();
                        }
                        float f7 = f4 + f3;
                        int intValue2 = ((Integer) pair.second).intValue() - ((Integer) pair.first).intValue();
                        float f8 = measuredHeight;
                        if (EmojiView.this.emojiAdapter.positionToUnlock.indexOfKey(((Integer) pair.second).intValue()) >= 0) {
                            f7 += AndroidUtilities.dp(60.0f);
                            intValue2--;
                        }
                        if (i6 < ((Integer) pair.second).intValue()) {
                            f7 += (((((intValue2 - intValue) - 1) + (intValue % EmojiView.this.emojiLayoutManager.getSpanCount())) / EmojiView.this.emojiLayoutManager.getSpanCount()) * measuredWidth) + measuredWidth;
                        } else {
                            f2 -= measuredWidth;
                        }
                        boolean z5 = stickerSetNameCell instanceof StickerSetNameCell;
                        if (z5) {
                            TextView textView = stickerSetNameCell.getTextView();
                            i3 = textView.getMeasuredWidth();
                            textView.setTranslationX((dp3 / 2.0f) * f6);
                            this.headerWidthsCache.put(((Integer) pair.first).intValue(), i3);
                        } else {
                            i3 = this.headerWidthsCache.get(((Integer) pair.first).intValue());
                        }
                        Path path = this.lockPath;
                        if (path == null) {
                            this.lockPath = new Path();
                        } else {
                            path.rewind();
                        }
                        if (i3 > 0) {
                            this.lockPath.moveTo(((getWidth() - ((i3 + dp3) + dp2)) / 2.0f) - dp2, f2);
                        }
                        RectF rectF = AndroidUtilities.rectTmp;
                        float f9 = dp2 + dp;
                        float f10 = f2 + dp;
                        rectF.set(dp2, f2, f9, f10);
                        f = measuredWidth;
                        this.lockPath.arcTo(rectF, 270.0f, -90.0f);
                        float f11 = f7 - dp;
                        rectF.set(dp2, f11, f9, f7);
                        this.lockPath.arcTo(rectF, 180.0f, -90.0f);
                        rectF.set((getMeasuredWidth() - dp2) - dp, f11, getMeasuredWidth() - dp2, f7);
                        this.lockPath.arcTo(rectF, 90.0f, -90.0f);
                        rectF.set((getMeasuredWidth() - dp2) - dp, f2, getMeasuredWidth() - dp2, f10);
                        this.lockPath.arcTo(rectF, 0.0f, -90.0f);
                        if (i3 > 0) {
                            this.lockPath.lineTo(((getWidth() + (i3 + dp3)) / 2.0f) + dp2, f2);
                        } else {
                            this.lockPath.close();
                        }
                        if (EmojiView.this.emojiLockPaint == null) {
                            EmojiView.this.emojiLockPaint = new Paint(1);
                            EmojiView.this.emojiLockPaint.setColor(getThemedColor("chat_emojiPanelStickerSetName"));
                            EmojiView.this.emojiLockPaint.setAlpha((int) (EmojiView.this.emojiLockPaint.getAlpha() * 0.5f));
                            EmojiView.this.emojiLockPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                            EmojiView.this.emojiLockPaint.setStyle(Paint.Style.STROKE);
                            EmojiView.this.emojiLockPaint.setStrokeCap(Paint.Cap.ROUND);
                            EmojiView.this.emojiLockPaint.setPathEffect(new DashPathEffect(new float[]{AndroidUtilities.dp(5.5f), AndroidUtilities.dp(7.0f)}, 0.5f));
                        }
                        canvas.save();
                        canvas2 = canvas;
                        canvas2.clipRect(0.0f, Math.min(f8, dp4), getMeasuredWidth(), Math.max(f8, dp4));
                        if (f6 < 1.0f) {
                            float f12 = 1.1f - (0.1f * f6);
                            canvas2.scale(f12, f12, getMeasuredWidth() / 2.0f, (f7 + f2) / 2.0f);
                        }
                        int alpha = EmojiView.this.emojiLockPaint.getAlpha();
                        EmojiView.this.emojiLockPaint.setAlpha((int) (alpha * f6));
                        canvas2.drawPath(this.lockPath, EmojiView.this.emojiLockPaint);
                        EmojiView.this.emojiLockPaint.setAlpha(alpha);
                        if (z5) {
                            if (EmojiView.this.emojiLockDrawable == null) {
                                EmojiView.this.emojiLockDrawable = getResources().getDrawable(R.drawable.permission_locked).mutate();
                                EmojiView.this.emojiLockDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelStickerSetName"), PorterDuff.Mode.MULTIPLY));
                            }
                            float f13 = i3 + dp3 + dp2;
                            float f14 = dp3 / 2.0f;
                            float f15 = (1.0f - f6) * f14;
                            EmojiView.this.emojiLockDrawable.setBounds((int) (((getWidth() - f13) / 2.0f) + f15), (int) (f2 - f14), (int) (((getWidth() - f13) / 2.0f) + dp3 + f15), (int) (f2 + f14));
                            EmojiView.this.emojiLockDrawable.setAlpha((int) (f6 * 255.0f));
                            EmojiView.this.emojiLockDrawable.draw(canvas2);
                        }
                        canvas.restore();
                    }
                    i5 = i2 + 1;
                    canvas3 = canvas2;
                    measuredWidth = f;
                }
            }
        };
        this.emojiGridView = recyclerListView;
        recyclerListView.setInstantClick(true);
        RecyclerListView recyclerListView2 = this.emojiGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8) { // from class: org.telegram.ui.Components.EmojiView.5
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i2) {
                try {
                    LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2) { // from class: org.telegram.ui.Components.EmojiView.5.1
                        {
                            AnonymousClass5.this = this;
                        }

                        @Override // androidx.recyclerview.widget.LinearSmoothScrollerCustom
                        public void onEnd() {
                            EmojiView.this.emojiSmoothScrolling = false;
                        }
                    };
                    linearSmoothScrollerCustom.setTargetPosition(i2);
                    startSmoothScroll(linearSmoothScrollerCustom);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        };
        this.emojiLayoutManager = gridLayoutManager;
        recyclerListView2.setLayoutManager(gridLayoutManager);
        this.emojiGridView.setTopGlowOffset(AndroidUtilities.dp(38.0f));
        this.emojiGridView.setBottomGlowOffset(AndroidUtilities.dp(36.0f));
        float f = 40.0f;
        this.emojiGridView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(44.0f));
        this.emojiGridView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        this.emojiGridView.setSelectorDrawableColor(0);
        this.emojiGridView.setClipToPadding(false);
        this.emojiLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.6
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i2) {
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                    if ((EmojiView.this.needEmojiSearch && i2 == 0) || EmojiView.this.emojiAdapter.positionToSection.indexOfKey(i2) >= 0 || EmojiView.this.emojiAdapter.positionToUnlock.indexOfKey(i2) >= 0) {
                        return EmojiView.this.emojiLayoutManager.getSpanCount();
                    }
                } else if (i2 == 0 || (i2 == 1 && EmojiView.this.emojiSearchAdapter.searchWas && EmojiView.this.emojiSearchAdapter.result.isEmpty())) {
                    return EmojiView.this.emojiLayoutManager.getSpanCount();
                }
                return 1;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanGroupIndex(int i2, int i3) {
                return super.getSpanGroupIndex(i2, i3);
            }
        });
        RecyclerListView recyclerListView3 = this.emojiGridView;
        EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter();
        this.emojiAdapter = emojiGridAdapter;
        recyclerListView3.setAdapter(emojiGridAdapter);
        this.emojiGridView.addItemDecoration(new EmojiGridSpacing());
        this.emojiSearchAdapter = new EmojiSearchAdapter();
        this.emojiContainer.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = new RecyclerAnimationScrollHelper(this.emojiGridView, this.emojiLayoutManager);
        this.emojiScrollHelper = recyclerAnimationScrollHelper;
        recyclerAnimationScrollHelper.setAnimationCallback(new RecyclerAnimationScrollHelper.AnimationCallback() { // from class: org.telegram.ui.Components.EmojiView.7
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
            public void onEndAnimation() {
                EmojiView.this.emojiSmoothScrolling = false;
            }
        });
        this.emojiGridView.setOnScrollListener(new TypedScrollListener(1) { // from class: org.telegram.ui.Components.EmojiView.8
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView.TypedScrollListener, androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                int findFirstVisibleItemPosition = EmojiView.this.emojiLayoutManager.findFirstVisibleItemPosition() + EmojiView.this.emojiLayoutManager.getSpanCount();
                int i4 = -1;
                if (findFirstVisibleItemPosition != -1) {
                    int size = Emoji.recentEmoji.size() + (EmojiView.this.needEmojiSearch ? 1 : 0);
                    int i5 = 0;
                    if (findFirstVisibleItemPosition >= size) {
                        int i6 = 0;
                        while (true) {
                            String[][] strArr = EmojiData.dataColored;
                            if (i6 >= strArr.length) {
                                break;
                            }
                            int length = strArr[i6].length + 1 + size;
                            if (findFirstVisibleItemPosition < length) {
                                i4 = (!Emoji.recentEmoji.isEmpty() ? 1 : 0) + i6;
                                break;
                            } else {
                                i6++;
                                size = length;
                            }
                        }
                        if (i4 < 0 && EmojiView.this.emojiAdapter.emojipacksProcessed != null) {
                            while (i5 < EmojiView.this.emojiAdapter.emojipacksProcessed.size()) {
                                EmojiGridAdapter.EmojiPack emojiPack = (EmojiGridAdapter.EmojiPack) EmojiView.this.emojiAdapter.emojipacksProcessed.get(i5);
                                int size2 = emojiPack.documents.size() + 1;
                                if (!emojiPack.free) {
                                    size2++;
                                }
                                size += size2;
                                if (findFirstVisibleItemPosition < size) {
                                    i5 = EmojiData.dataColored.length + emojiPack.index + (!Emoji.recentEmoji.isEmpty() ? 1 : 0);
                                    break;
                                }
                                i5++;
                            }
                        }
                        i5 = i4;
                    }
                    if (!EmojiView.this.emojiSmoothScrolling && i5 >= 0) {
                        EmojiView.this.emojiTabs.select(i5);
                    }
                }
                super.onScrolled(recyclerView, i2, i3);
            }

            @Override // org.telegram.ui.Components.EmojiView.TypedScrollListener, androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 0) {
                    EmojiView.this.emojiSmoothScrolling = false;
                }
                super.onScrollStateChanged(recyclerView, i2);
            }
        });
        this.emojiGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.EmojiView.9
            {
                EmojiView.this = this;
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Removed duplicated region for block: B:79:0x0193  */
            /* JADX WARN: Removed duplicated region for block: B:82:0x01a3  */
            /* JADX WARN: Removed duplicated region for block: B:83:0x01a6  */
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean onItemClick(android.view.View r18, int r19) {
                /*
                    Method dump skipped, instructions count: 572
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass9.onItemClick(android.view.View, int):boolean");
            }
        });
        if (z) {
            MediaDataController.getInstance(this.currentAccount).checkStickers(5);
        }
        this.emojiTabs = new EmojiTabsStrip(context, resourcesProvider, z) { // from class: org.telegram.ui.Components.EmojiView.10
            {
                EmojiView.this = this;
            }

            @Override // android.view.View
            public void setTranslationY(float f2) {
                super.setTranslationY(f2);
                if (EmojiView.this.emojiTabsShadow != null) {
                    EmojiView.this.emojiTabsShadow.setTranslationY(f2);
                }
            }

            /* JADX WARN: Removed duplicated region for block: B:12:0x0025  */
            @Override // org.telegram.ui.Components.EmojiTabsStrip
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected boolean onTabClick(int r4) {
                /*
                    r3 = this;
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    boolean r0 = org.telegram.ui.Components.EmojiView.access$6800(r0)
                    r1 = 0
                    if (r0 == 0) goto La
                    return r1
                La:
                    java.util.ArrayList<java.lang.String> r0 = org.telegram.messenger.Emoji.recentEmoji
                    boolean r0 = r0.isEmpty()
                    r2 = 0
                    if (r0 != 0) goto L22
                    if (r4 != 0) goto L20
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    boolean r0 = org.telegram.ui.Components.EmojiView.access$4800(r0)
                    java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                    goto L23
                L20:
                    int r4 = r4 + (-1)
                L22:
                    r0 = r2
                L23:
                    if (r0 != 0) goto L37
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$EmojiGridAdapter r0 = org.telegram.ui.Components.EmojiView.access$3600(r0)
                    android.util.SparseIntArray r0 = org.telegram.ui.Components.EmojiView.EmojiGridAdapter.access$7900(r0)
                    int r4 = r0.get(r4)
                    java.lang.Integer r0 = java.lang.Integer.valueOf(r4)
                L37:
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.RecyclerListView r4 = org.telegram.ui.Components.EmojiView.access$1000(r4)
                    r4.stopScroll()
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    int r0 = r0.intValue()
                    org.telegram.ui.Components.EmojiView.access$8000(r4, r0, r1)
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView.access$8100(r4, r2, r1)
                    r4 = 1
                    return r4
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass10.onTabClick(int):boolean");
            }
        };
        if (z4) {
            SearchField searchField = new SearchField(context, 1);
            this.emojiSearchField = searchField;
            this.emojiContainer.addView(searchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.Components.EmojiView.11
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z5) {
                    if (z5) {
                        EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
        }
        this.emojiTabs.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 36.0f));
        View view = new View(context);
        this.emojiTabsShadow = view;
        view.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(1);
        this.emojiTabsShadow.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(36.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        if (z2) {
            if (z3) {
                FrameLayout frameLayout2 = new FrameLayout(context);
                this.gifContainer = frameLayout2;
                this.views.add(frameLayout2);
                RecyclerListView recyclerListView4 = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiView.12
                    private boolean ignoreLayout;
                    private boolean wasMeasured;

                    {
                        EmojiView.this = this;
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, i3);
                        if (!this.wasMeasured) {
                            EmojiView.this.gifAdapter.notifyDataSetChanged();
                            this.wasMeasured = true;
                        }
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                    public void onLayout(boolean z5, int i2, int i3, int i4, int i5) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
                            EmojiView.this.gifSearchField.setVisibility(0);
                            EmojiView.this.gifTabs.onPageScrolled(0, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(z5, i2, i3, i4, i5);
                        EmojiView.this.checkGifSearchFieldScroll(true);
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                    public void requestLayout() {
                        if (this.ignoreLayout) {
                            return;
                        }
                        super.requestLayout();
                    }
                };
                this.gifGridView = recyclerListView4;
                recyclerListView4.setClipToPadding(false);
                RecyclerListView recyclerListView5 = this.gifGridView;
                GifLayoutManager gifLayoutManager = new GifLayoutManager(context);
                this.gifLayoutManager = gifLayoutManager;
                recyclerListView5.setLayoutManager(gifLayoutManager);
                this.gifGridView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.EmojiView.13
                    {
                        EmojiView.this = this;
                    }

                    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                    public void getItemOffsets(android.graphics.Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view2);
                        int i2 = 0;
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter && childAdapterPosition == EmojiView.this.gifAdapter.trendingSectionItem) {
                            rect.set(0, 0, 0, 0);
                        } else if (childAdapterPosition != 0) {
                            rect.left = 0;
                            rect.bottom = 0;
                            rect.top = AndroidUtilities.dp(2.0f);
                            if (!EmojiView.this.gifLayoutManager.isLastInRow(childAdapterPosition - 1)) {
                                i2 = AndroidUtilities.dp(2.0f);
                            }
                            rect.right = i2;
                        } else {
                            rect.set(0, 0, 0, 0);
                        }
                    }
                });
                this.gifGridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                this.gifGridView.setOverScrollMode(2);
                ((SimpleItemAnimator) this.gifGridView.getItemAnimator()).setSupportsChangeAnimations(false);
                RecyclerListView recyclerListView6 = this.gifGridView;
                GifAdapter gifAdapter = new GifAdapter(this, context, true);
                this.gifAdapter = gifAdapter;
                recyclerListView6.setAdapter(gifAdapter);
                this.gifSearchAdapter = new GifAdapter(this, context);
                this.gifGridView.setOnScrollListener(new TypedScrollListener(2));
                this.gifGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view2, MotionEvent motionEvent) {
                        boolean lambda$new$1;
                        lambda$new$1 = EmojiView.this.lambda$new$1(resourcesProvider, view2, motionEvent);
                        return lambda$new$1;
                    }
                });
                RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda7
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view2, int i2) {
                        EmojiView.this.lambda$new$2(view2, i2);
                    }
                };
                this.gifOnItemClickListener = onItemClickListener;
                this.gifGridView.setOnItemClickListener(onItemClickListener);
                this.gifContainer.addView(this.gifGridView, LayoutHelper.createFrame(-1, -1.0f));
                SearchField searchField2 = new SearchField(context, 2);
                this.gifSearchField = searchField2;
                searchField2.setVisibility(4);
                this.gifContainer.addView(this.gifSearchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
                DraggableScrollSlidingTabStrip draggableScrollSlidingTabStrip = new DraggableScrollSlidingTabStrip(context, resourcesProvider);
                this.gifTabs = draggableScrollSlidingTabStrip;
                draggableScrollSlidingTabStrip.setType(ScrollSlidingTabStrip.Type.TAB);
                this.gifTabs.setUnderlineHeight(AndroidUtilities.getShadowHeight());
                this.gifTabs.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
                this.gifTabs.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
                this.gifTabs.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
                this.gifContainer.addView(this.gifTabs, LayoutHelper.createFrame(-1, 36, 51));
                updateGifTabs();
                this.gifTabs.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9
                    @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                    public final void onPageSelected(int i2) {
                        EmojiView.this.lambda$new$3(i2);
                    }
                });
                this.gifAdapter.loadTrendingGifs();
            }
            this.stickersContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.14
                {
                    EmojiView.this = this;
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    EmojiView.this.stickersContainerAttached = true;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    EmojiView.this.stickersContainerAttached = false;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }
            };
            MediaDataController.getInstance(this.currentAccount).checkStickers(0);
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            RecyclerListViewWithOverlayDraw recyclerListViewWithOverlayDraw = new RecyclerListViewWithOverlayDraw(context) { // from class: org.telegram.ui.Components.EmojiView.15
                boolean ignoreLayout;

                {
                    EmojiView.this = this;
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
                public void setVisibility(int i2) {
                    super.setVisibility(i2);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                public void onLayout(boolean z5, int i2, int i3, int i4, int i5) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(z5, i2, i3, i4, i5);
                    EmojiView.this.checkStickersSearchFieldScroll(true);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.stickersGridView = recyclerListViewWithOverlayDraw;
            GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, 5) { // from class: org.telegram.ui.Components.EmojiView.16
                {
                    EmojiView.this = this;
                }

                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i2) {
                    try {
                        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2);
                        linearSmoothScrollerCustom.setTargetPosition(i2);
                        startSmoothScroll(linearSmoothScrollerCustom);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public int scrollVerticallyBy(int i2, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int scrollVerticallyBy = super.scrollVerticallyBy(i2, recycler, state);
                    if (scrollVerticallyBy != 0 && EmojiView.this.stickersGridView.getScrollState() == 1) {
                        EmojiView.this.expandStickersByDragg = false;
                        EmojiView.this.updateStickerTabsPosition();
                    }
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                    return scrollVerticallyBy;
                }
            };
            this.stickersLayoutManager = gridLayoutManager2;
            recyclerListViewWithOverlayDraw.setLayoutManager(gridLayoutManager2);
            this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.17
                {
                    EmojiView.this = this;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i2) {
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                        if (i2 != EmojiView.this.stickersSearchGridAdapter.totalItems && (EmojiView.this.stickersSearchGridAdapter.cache.get(i2) == null || (EmojiView.this.stickersSearchGridAdapter.cache.get(i2) instanceof TLRPC$Document))) {
                            return 1;
                        }
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    } else if (i2 == 0) {
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    } else {
                        if (i2 != EmojiView.this.stickersGridAdapter.totalItems && (EmojiView.this.stickersGridAdapter.cache.get(i2) == null || (EmojiView.this.stickersGridAdapter.cache.get(i2) instanceof TLRPC$Document))) {
                            return 1;
                        }
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    }
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersContainer);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
            RecyclerListView recyclerListView7 = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView7.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$new$4;
                    lambda$new$4 = EmojiView.this.lambda$new$4(resourcesProvider, view2, motionEvent);
                    return lambda$new$4;
                }
            });
            RecyclerListView.OnItemClickListener onItemClickListener2 = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view2, int i2) {
                    EmojiView.this.lambda$new$5(view2, i2);
                }
            };
            this.stickersOnItemClickListener = onItemClickListener2;
            this.stickersGridView.setOnItemClickListener(onItemClickListener2);
            this.stickersGridView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
            this.stickersContainer.addView(this.stickersGridView);
            this.stickersScrollHelper = new RecyclerAnimationScrollHelper(this.stickersGridView, this.stickersLayoutManager);
            SearchField searchField3 = new SearchField(context, 0);
            this.stickersSearchField = searchField3;
            this.stickersContainer.addView(searchField3, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            AnonymousClass18 anonymousClass18 = new AnonymousClass18(context, resourcesProvider);
            this.stickersTab = anonymousClass18;
            anonymousClass18.setDragEnabled(true);
            this.stickersTab.setWillNotDraw(false);
            this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
            this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
            this.stickersTab.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            if (viewGroup != null) {
                FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.19
                    Paint paint = new Paint();

                    {
                        EmojiView.this = this;
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        EmojiView.this.delegate.getProgressToSearchOpened();
                        float dp = AndroidUtilities.dp(50.0f) * EmojiView.this.delegate.getProgressToSearchOpened();
                        if (dp > getMeasuredHeight()) {
                            return;
                        }
                        canvas.save();
                        if (dp != 0.0f) {
                            canvas.clipRect(0.0f, dp, getMeasuredWidth(), getMeasuredHeight());
                        }
                        this.paint.setColor(EmojiView.this.getThemedColor("chat_emojiPanelBackground"));
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(36.0f) + EmojiView.this.stickersTab.getExpandedOffset(), this.paint);
                        super.dispatchDraw(canvas);
                        EmojiView.this.stickersTab.drawOverlays(canvas);
                        canvas.restore();
                    }

                    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean z5, int i2, int i3, int i4, int i5) {
                        super.onLayout(z5, i2, i3, i4, i5);
                        EmojiView.this.updateStickerTabsPosition();
                    }
                };
                this.stickersTabContainer = frameLayout3;
                frameLayout3.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
                viewGroup.addView(this.stickersTabContainer, LayoutHelper.createFrame(-1, -2.0f));
            } else {
                this.stickersContainer.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
            }
            updateStickerTabs();
            this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10
                @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                public final void onPageSelected(int i2) {
                    EmojiView.this.lambda$new$6(i2);
                }
            });
            this.stickersGridView.setOnScrollListener(new TypedScrollListener(0));
        }
        ViewPager viewPager = new ViewPager(context) { // from class: org.telegram.ui.Components.EmojiView.20
            {
                EmojiView.this = this;
            }

            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                }
                try {
                    return super.onInterceptTouchEvent(motionEvent);
                } catch (IllegalArgumentException unused) {
                    return false;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager
            public void setCurrentItem(int i2, boolean z5) {
                EmojiView.this.startStopVisibleGifs(i2 == 1);
                if (i2 != getCurrentItem()) {
                    super.setCurrentItem(i2, z5);
                } else if (i2 == 0) {
                    EmojiView.this.scrollEmojisToPosition(0, 0);
                    if (EmojiView.this.emojiTabs == null) {
                        return;
                    }
                    EmojiView.this.emojiTabs.select(0);
                } else if (i2 == 1) {
                    EmojiView.this.gifGridView.smoothScrollToPosition(1);
                } else {
                    EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                }
            }
        };
        this.pager = viewPager;
        viewPager.setAdapter(new EmojiPagesAdapter());
        View view2 = new View(context);
        this.topShadow = view2;
        view2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, -1907225));
        addView(this.topShadow, LayoutHelper.createFrame(-1, 6.0f));
        ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.EmojiView.21
            {
                EmojiView.this = this;
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!EmojiView.this.backspaceOnce && EmojiView.this.delegate != null && EmojiView.this.delegate.onBackspace()) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(motionEvent);
                return true;
            }
        };
        this.backspaceButton = imageView;
        imageView.setHapticFeedbackEnabled(true);
        this.backspaceButton.setImageResource(R.drawable.smiles_tab_clear);
        this.backspaceButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        this.backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
        this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
        this.backspaceButton.setFocusable(true);
        this.backspaceButton.setOnClickListener(new View.OnClickListener(this) { // from class: org.telegram.ui.Components.EmojiView.22
            @Override // android.view.View.OnClickListener
            public void onClick(View view3) {
            }
        });
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.bulletinContainer = frameLayout4;
        int i2 = 40;
        int i3 = 44;
        if (z4) {
            addView(frameLayout4, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, (AndroidUtilities.getShadowHeight() / AndroidUtilities.density) + 44.0f));
        } else {
            addView(frameLayout4, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, (i >= 21 ? 40 : 44) + 12));
        }
        this.bottomTabContainer = new FrameLayout(this, context) { // from class: org.telegram.ui.Components.EmojiView.23
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        View view3 = new View(context);
        this.shadowLine = view3;
        view3.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
        View view4 = new View(context);
        this.bottomTabContainerBackground = view4;
        this.bottomTabContainer.addView(view4, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(44.0f), 83));
        if (z4) {
            addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(44.0f) + AndroidUtilities.getShadowHeight(), 83));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(52, 44, 85));
            if (i >= 21) {
                this.backspaceButton.setBackground(Theme.createSelectorDrawable(argb));
            }
            ImageView imageView2 = new ImageView(context);
            this.stickerSettingsButton = imageView2;
            imageView2.setImageResource(R.drawable.smiles_tab_settings);
            this.stickerSettingsButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            this.stickerSettingsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.stickerSettingsButton.setFocusable(true);
            if (i >= 21) {
                this.stickerSettingsButton.setBackground(Theme.createSelectorDrawable(argb));
            }
            this.stickerSettingsButton.setContentDescription(LocaleController.getString("Settings", R.string.Settings));
            this.bottomTabContainer.addView(this.stickerSettingsButton, LayoutHelper.createFrame(52, 44, 85));
            this.stickerSettingsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.24
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view5) {
                    if (EmojiView.this.delegate != null) {
                        EmojiView.this.delegate.onStickersSettingsClick();
                    }
                }
            });
            PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(context, resourcesProvider);
            this.typeTabs = pagerSlidingTabStrip;
            pagerSlidingTabStrip.setViewPager(this.pager);
            this.typeTabs.setShouldExpand(false);
            this.typeTabs.setIndicatorHeight(0);
            this.typeTabs.setUnderlineHeight(0);
            this.typeTabs.setTabPaddingLeftRight(AndroidUtilities.dp(10.0f));
            this.bottomTabContainer.addView(this.typeTabs, LayoutHelper.createFrame(-2, 44, 81));
            this.typeTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.EmojiView.25
                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int i4) {
                }

                {
                    EmojiView.this = this;
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int i4, float f2, int i5) {
                    EmojiView.this.checkGridVisibility(i4, f2);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.onPageScrolled(i4, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i5);
                    boolean z5 = true;
                    EmojiView.this.showBottomTab(true, true);
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    SearchField searchField4 = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    String obj = searchField4.searchEditText.getText().toString();
                    int i6 = 0;
                    while (i6 < 3) {
                        SearchField searchField5 = i6 == 0 ? EmojiView.this.emojiSearchField : i6 == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                        if (searchField5 != null && searchField5 != searchField4 && searchField5.searchEditText != null && !searchField5.searchEditText.getText().toString().equals(obj)) {
                            searchField5.searchEditText.setText(obj);
                            searchField5.searchEditText.setSelection(obj.length());
                        }
                        i6++;
                    }
                    EmojiView emojiView2 = EmojiView.this;
                    if ((i4 != 0 || f2 <= 0.0f) && i4 != 1) {
                        z5 = false;
                    }
                    emojiView2.startStopVisibleGifs(z5);
                    EmojiView.this.updateStickerTabsPosition();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int i4) {
                    EmojiView.this.saveNewPage();
                    boolean z5 = false;
                    EmojiView.this.showBackspaceButton(i4 == 0, true);
                    EmojiView emojiView = EmojiView.this;
                    if (i4 == 2) {
                        z5 = true;
                    }
                    emojiView.showStickerSettingsButton(z5, true);
                    if (EmojiView.this.delegate.isSearchOpened()) {
                        if (i4 == 0) {
                            if (EmojiView.this.emojiSearchField == null) {
                                return;
                            }
                            EmojiView.this.emojiSearchField.searchEditText.requestFocus();
                        } else if (i4 == 1) {
                            if (EmojiView.this.gifSearchField == null) {
                                return;
                            }
                            EmojiView.this.gifSearchField.searchEditText.requestFocus();
                        } else if (EmojiView.this.stickersSearchField == null) {
                        } else {
                            EmojiView.this.stickersSearchField.searchEditText.requestFocus();
                        }
                    }
                }
            });
            ImageView imageView3 = new ImageView(context);
            this.searchButton = imageView3;
            imageView3.setImageResource(R.drawable.smiles_tab_search);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            this.searchButton.setScaleType(ImageView.ScaleType.CENTER);
            this.searchButton.setContentDescription(LocaleController.getString("Search", R.string.Search));
            this.searchButton.setFocusable(true);
            if (i >= 21) {
                this.searchButton.setBackground(Theme.createSelectorDrawable(argb));
            }
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(52, 44, 83));
            this.searchButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.26
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view5) {
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    SearchField searchField4 = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    if (searchField4 == null) {
                        return;
                    }
                    searchField4.searchEditText.requestFocus();
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, 0, 0.0f, 0.0f, 0);
                    searchField4.searchEditText.onTouchEvent(obtain);
                    obtain.recycle();
                    MotionEvent obtain2 = MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0);
                    searchField4.searchEditText.onTouchEvent(obtain2);
                    obtain2.recycle();
                }
            });
        } else {
            addView(this.bottomTabContainer, LayoutHelper.createFrame((i >= 21 ? 40 : 44) + 20, (i >= 21 ? 40 : 44) + 12, (LocaleController.isRTL ? 3 : 5) | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor("chat_emojiPanelBackground"), getThemedColor("chat_emojiPanelBackground"));
            if (i < 21) {
                Drawable mutate = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            } else {
                StateListAnimator stateListAnimator = new StateListAnimator();
                ImageView imageView4 = this.floatingButton;
                Property property = View.TRANSLATION_Z;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView4, property, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, property, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                this.backspaceButton.setStateListAnimator(stateListAnimator);
                this.backspaceButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.EmojiView.27
                    @Override // android.view.ViewOutlineProvider
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view5, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                    }
                });
            }
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackground(createSimpleSelectorCircleDrawable);
            this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
            this.backspaceButton.setFocusable(true);
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(i >= 21 ? 40 : 44, i >= 21 ? 40 : i3, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.shadowLine.setVisibility(8);
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), getThemedColor("chat_gifSaveHintBackground")));
        this.mediaBanTooltip.setTextColor(getThemedColor("chat_gifSaveHintText"));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(!AndroidUtilities.isTablet() ? 32.0f : f);
        this.pickerView = new EmojiColorPickerView(context);
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        int dp = AndroidUtilities.dp(((!AndroidUtilities.isTablet() ? 32 : i2) * 6) + 10 + 20);
        this.popupWidth = dp;
        int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = dp2;
        EmojiPopupWindow emojiPopupWindow = new EmojiPopupWindow(this, emojiColorPickerView, dp, dp2);
        this.pickerViewPopup = emojiPopupWindow;
        emojiPopupWindow.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View view5, int i4, KeyEvent keyEvent) {
                boolean lambda$new$7;
                lambda$new$7 = EmojiView.this.lambda$new$7(view5, i4, keyEvent);
                return lambda$new$7;
            }
        });
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
        if (this.typeTabs != null) {
            if (this.views.size() == 1 && this.typeTabs.getVisibility() == 0) {
                this.typeTabs.setVisibility(4);
            } else if (this.views.size() == 1 || this.typeTabs.getVisibility() == 0) {
            } else {
                this.typeTabs.setVisibility(0);
            }
        }
    }

    public /* synthetic */ boolean lambda$new$1(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public /* synthetic */ void lambda$new$2(View view, int i) {
        if (this.delegate == null) {
            return;
        }
        int i2 = i - 1;
        RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
        GifAdapter gifAdapter = this.gifAdapter;
        if (adapter != gifAdapter) {
            RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
            GifAdapter gifAdapter2 = this.gifSearchAdapter;
            if (adapter2 != gifAdapter2 || i2 < 0 || i2 >= gifAdapter2.results.size()) {
                return;
            }
            this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(i2), this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.bot, true, 0);
            updateRecentGifs();
        } else if (i2 < 0) {
        } else {
            if (i2 < gifAdapter.recentItemsCount) {
                this.delegate.onGifSelected(view, this.recentGifs.get(i2), null, "gif", true, 0);
                return;
            }
            if (this.gifAdapter.recentItemsCount > 0) {
                i2 = (i2 - this.gifAdapter.recentItemsCount) - 1;
            }
            if (i2 < 0 || i2 >= this.gifAdapter.results.size()) {
                return;
            }
            this.delegate.onGifSelected(view, this.gifAdapter.results.get(i2), null, this.gifAdapter.bot, true, 0);
        }
    }

    public /* synthetic */ void lambda$new$3(int i) {
        if (i != this.gifTrendingTabNum || !this.gifAdapter.results.isEmpty()) {
            this.gifGridView.stopScroll();
            this.gifTabs.onPageScrolled(i, 0);
            int i2 = 1;
            if (i == this.gifRecentTabNum || i == this.gifTrendingTabNum) {
                this.gifSearchField.searchEditText.setText("");
                if (i != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                    GifLayoutManager gifLayoutManager = this.gifLayoutManager;
                    EmojiViewDelegate emojiViewDelegate = this.delegate;
                    if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                        i2 = 0;
                    }
                    gifLayoutManager.scrollToPositionWithOffset(i2, 0);
                } else {
                    this.gifLayoutManager.scrollToPositionWithOffset(this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
                }
                if (i == this.gifTrendingTabNum) {
                    ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                    if (!arrayList.isEmpty()) {
                        this.gifSearchPreloader.preload(arrayList.get(0));
                    }
                }
            } else {
                ArrayList<String> arrayList2 = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                this.gifSearchAdapter.searchEmoji(arrayList2.get(i - this.gifFirstEmojiTabNum));
                int i3 = this.gifFirstEmojiTabNum;
                if (i - i3 > 0) {
                    this.gifSearchPreloader.preload(arrayList2.get((i - i3) - 1));
                }
                if (i - this.gifFirstEmojiTabNum < arrayList2.size() - 1) {
                    this.gifSearchPreloader.preload(arrayList2.get((i - this.gifFirstEmojiTabNum) + 1));
                }
            }
            resetTabsY(2);
        }
    }

    public /* synthetic */ boolean lambda$new$4(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public /* synthetic */ void lambda$new$5(View view, int i) {
        String str;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter) {
            String str2 = stickersSearchGridAdapter.searchQuery;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(i);
            if (tLRPC$StickerSetCovered != null) {
                this.delegate.onShowStickerSet(tLRPC$StickerSetCovered.set, null);
                return;
            }
            str = str2;
        } else {
            str = null;
        }
        if (!(view instanceof StickerEmojiCell)) {
            return;
        }
        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
        if (stickerEmojiCell.getSticker() != null && MessageObject.isPremiumSticker(stickerEmojiCell.getSticker()) && !AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
            ContentPreviewViewer.getInstance().showMenuFor(stickerEmojiCell);
            return;
        }
        ContentPreviewViewer.getInstance().reset();
        if (stickerEmojiCell.isDisabled()) {
            return;
        }
        stickerEmojiCell.disable();
        this.delegate.onStickerSelected(stickerEmojiCell, stickerEmojiCell.getSticker(), str, stickerEmojiCell.getParentObject(), stickerEmojiCell.getSendAnimationData(), true, 0);
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$18 */
    /* loaded from: classes3.dex */
    public class AnonymousClass18 extends DraggableScrollSlidingTabStrip {
        public static /* synthetic */ void lambda$sendReorder$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass18(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            EmojiView.this = r1;
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void updatePosition() {
            EmojiView.this.updateStickerTabsPosition();
            EmojiView.this.stickersTabContainer.invalidate();
            invalidate();
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.invalidateEnterView();
            }
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void stickerSetPositionChanged(int i, int i2) {
            int i3 = i - EmojiView.this.stickersTabOffset;
            int i4 = i2 - EmojiView.this.stickersTabOffset;
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            swapListElements(EmojiView.this.stickerSets, i3, i4);
            if (EmojiView.this.hasChatStickers) {
                swapListElements(mediaDataController.getStickerSets(0), i3 - 1, i4 - 1);
            } else {
                swapListElements(mediaDataController.getStickerSets(0), i3, i4);
            }
            EmojiView.this.reloadStickersAdapter();
            AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
            AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            sendReorder();
            EmojiView.this.updateStickerTabs();
        }

        private void swapListElements(List<TLRPC$TL_messages_stickerSet> list, int i, int i2) {
            list.add(i2, list.remove(i));
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v2, types: [int] */
        /* JADX WARN: Type inference failed for: r2v8 */
        /* JADX WARN: Type inference failed for: r4v2, types: [java.util.ArrayList] */
        private void sendReorder() {
            MediaDataController.getInstance(EmojiView.this.currentAccount).calcNewHash(0);
            TLRPC$TL_messages_reorderStickerSets tLRPC$TL_messages_reorderStickerSets = new TLRPC$TL_messages_reorderStickerSets();
            tLRPC$TL_messages_reorderStickerSets.masks = false;
            tLRPC$TL_messages_reorderStickerSets.emojis = false;
            for (int i = EmojiView.this.hasChatStickers; i < EmojiView.this.stickerSets.size(); i++) {
                tLRPC$TL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC$TL_messages_stickerSet) EmojiView.this.stickerSets.get(i)).set.id));
            }
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_reorderStickerSets, EmojiView$18$$ExternalSyntheticLambda0.INSTANCE);
            NotificationCenter.getInstance(EmojiView.this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, 0);
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void invalidateOverlays() {
            EmojiView.this.stickersTabContainer.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$6(int i) {
        if (this.firstTabUpdate) {
            return;
        }
        if (i == this.trendingTabNum) {
            openTrendingStickers(null);
        } else if (i == this.recentTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("recent"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i2 = this.recentTabNum;
            scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
        } else if (i == this.favTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("fav"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            int i3 = this.favTabNum;
            scrollSlidingTabStrip2.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
        } else if (i == this.premiumTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("premium"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.stickersTab;
            int i4 = this.premiumTabNum;
            scrollSlidingTabStrip3.onPageScrolled(i4, i4 > 0 ? i4 : this.stickersTabOffset);
        } else {
            int i5 = i - this.stickersTabOffset;
            if (i5 >= this.stickerSets.size()) {
                return;
            }
            if (i5 >= this.stickerSets.size()) {
                i5 = this.stickerSets.size() - 1;
            }
            this.firstStickersAttach = false;
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(i5)), 0);
            resetTabsY(0);
            checkScroll(0);
            int i6 = this.favTabNum;
            if (i6 <= 0 && (i6 = this.recentTabNum) <= 0) {
                i6 = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(i, i6);
            this.expandStickersByDragg = false;
            updateStickerTabsPosition();
        }
    }

    public /* synthetic */ boolean lambda$new$7(View view, int i, KeyEvent keyEvent) {
        EmojiPopupWindow emojiPopupWindow;
        if (i == 82 && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 1 && (emojiPopupWindow = this.pickerViewPopup) != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
            return true;
        }
        return false;
    }

    public void createStickersChooseActionTracker() {
        ChooseStickerActionTracker chooseStickerActionTracker = new ChooseStickerActionTracker(this.currentAccount, this.delegate.getDialogId(), this.delegate.getThreadId()) { // from class: org.telegram.ui.Components.EmojiView.28
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView.ChooseStickerActionTracker
            public boolean isShown() {
                return EmojiView.this.delegate != null && EmojiView.this.getVisibility() == 0 && EmojiView.this.stickersContainerAttached;
            }
        };
        this.chooseStickerActionTracker = chooseStickerActionTracker;
        chooseStickerActionTracker.checkVisibility();
    }

    public void checkGridVisibility(int i, float f) {
        if (this.stickersContainer == null || this.gifContainer == null) {
            return;
        }
        int i2 = 0;
        if (i == 0) {
            this.emojiGridView.setVisibility(0);
            this.gifGridView.setVisibility(f == 0.0f ? 8 : 0);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (f == 0.0f) {
                i2 = 8;
            }
            scrollSlidingTabStrip.setVisibility(i2);
            this.stickersGridView.setVisibility(8);
            this.stickersTab.setVisibility(8);
        } else if (i != 1) {
            if (i != 2) {
                return;
            }
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(8);
            this.gifTabs.setVisibility(8);
            this.stickersGridView.setVisibility(0);
            this.stickersTab.setVisibility(0);
        } else {
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(0);
            this.gifTabs.setVisibility(0);
            this.stickersGridView.setVisibility(f == 0.0f ? 8 : 0);
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            if (f == 0.0f) {
                i2 = 8;
            }
            scrollSlidingTabStrip2.setVisibility(i2);
        }
    }

    public void openPremiumAnimatedEmojiFeature() {
        if (this.fragment != null) {
            new PremiumFeatureBottomSheet(this.fragment, 11, false).show();
        } else if (!(getContext() instanceof LaunchActivity)) {
        } else {
            ((LaunchActivity) getContext()).lambda$runLinkRequest$59(new PremiumPreviewFragment(null));
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiGridSpacing extends RecyclerView.ItemDecoration {
        public EmojiGridSpacing() {
            EmojiView.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            if (view instanceof StickerSetNameCell) {
                rect.left = AndroidUtilities.dp(5.0f);
                rect.right = AndroidUtilities.dp(5.0f);
                if (recyclerView.getChildAdapterPosition(view) + 1 <= EmojiView.this.emojiAdapter.plainEmojisCount || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                    return;
                }
                rect.top = AndroidUtilities.dp(10.0f);
            } else if (!(view instanceof PremiumButtonHolder) || ((PremiumButtonHolder) view).position + 1 < EmojiView.this.emojiAdapter.getItemCount()) {
            } else {
                rect.bottom = AndroidUtilities.dp(7.0f);
            }
        }
    }

    public static String addColorToCode(String str, String str2) {
        String str3;
        int length = str.length();
        if (length > 2 && str.charAt(str.length() - 2) == 8205) {
            str3 = str.substring(str.length() - 2);
            str = str.substring(0, str.length() - 2);
        } else if (length <= 3 || str.charAt(str.length() - 3) != 8205) {
            str3 = null;
        } else {
            str3 = str.substring(str.length() - 3);
            str = str.substring(0, str.length() - 3);
        }
        String str4 = str + str2;
        if (str3 != null) {
            return str4 + str3;
        }
        return str4;
    }

    public void openTrendingStickers(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(getContext(), new TrendingStickersLayout.Delegate() { // from class: org.telegram.ui.Components.EmojiView.29
            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean canSendSticker() {
                return true;
            }

            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered2, boolean z) {
                EmojiView.this.delegate.onStickerSetAdd(tLRPC$StickerSetCovered2);
                if (z) {
                    EmojiView.this.updateStickerTabs();
                }
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered2) {
                EmojiView.this.delegate.onStickerSetRemove(tLRPC$StickerSetCovered2);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onTouch(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public String[] getLastSearchKeyboardLanguage() {
                return EmojiView.this.lastSearchKeyboardLanguage;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void setLastSearchKeyboardLanguage(String[] strArr) {
                EmojiView.this.lastSearchKeyboardLanguage = strArr;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSelected(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i) {
                EmojiView.this.delegate.onStickerSelected(null, tLRPC$Document, null, obj, null, z2, i);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }
        }, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets, tLRPC$StickerSetCovered, this.resourcesProvider));
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        updateStickerTabsPosition();
        updateBottomTabContainerPosition();
    }

    private void updateBottomTabContainerPosition() {
        View view;
        int i;
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            ViewPager viewPager = this.pager;
            if ((viewPager != null && viewPager.getCurrentItem() == 0) || (view = (View) getParent()) == null) {
                return;
            }
            float y = getY() - view.getHeight();
            if (getLayoutParams().height > 0) {
                i = getLayoutParams().height;
            } else {
                i = getMeasuredHeight();
            }
            float f = y + i;
            if (this.bottomTabContainer.getTop() - f < 0.0f) {
                f = this.bottomTabContainer.getTop();
            }
            float f2 = -f;
            this.bottomTabContainer.setTranslationY(f2);
            this.bulletinContainer.setTranslationY(f2);
        }
    }

    public void updateStickerTabsPosition() {
        if (this.stickersTabContainer == null) {
            return;
        }
        boolean z = getVisibility() == 0 && this.stickersContainerAttached && this.delegate.getProgressToSearchOpened() != 1.0f;
        this.stickersTabContainer.setVisibility(z ? 0 : 8);
        if (z) {
            this.rect.setEmpty();
            this.pager.getChildVisibleRect(this.stickersContainer, this.rect, null);
            float dp = AndroidUtilities.dp(50.0f) * this.delegate.getProgressToSearchOpened();
            int i = this.rect.left;
            if (i != 0 || dp != 0.0f) {
                this.expandStickersByDragg = false;
            }
            this.stickersTabContainer.setTranslationX(i);
            float top = (((getTop() + getTranslationY()) - this.stickersTabContainer.getTop()) - this.stickersTab.getExpandedOffset()) - dp;
            if (this.stickersTabContainer.getTranslationY() != top) {
                this.stickersTabContainer.setTranslationY(top);
                this.stickersTabContainer.invalidate();
            }
        }
        if (this.expandStickersByDragg && z && this.showing) {
            this.stickersTab.expandStickers(this.lastStickersX, true);
            return;
        }
        this.expandStickersByDragg = false;
        this.stickersTab.expandStickers(this.lastStickersX, false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        updateBottomTabContainerPosition();
        super.dispatchDraw(canvas);
    }

    public void startStopVisibleGifs(boolean z) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.gifGridView.getChildAt(i);
            if (childAt instanceof ContextLinkCell) {
                ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                if (z) {
                    photoImage.setAllowStartAnimation(true);
                    photoImage.startAnimation();
                } else {
                    photoImage.setAllowStartAnimation(false);
                    photoImage.stopAnimation();
                }
            }
        }
    }

    public void addEmojiToRecent(String str) {
        if (str != null) {
            if (!str.startsWith("animated_") && !Emoji.isValidEmoji(str)) {
                return;
            }
            Emoji.recentEmoji.size();
            Emoji.addRecentEmoji(str);
            if (getVisibility() != 0 || this.pager.getCurrentItem() != 0) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
        }
    }

    public void showSearchField(boolean z) {
        for (int i = 0; i < 3; i++) {
            GridLayoutManager layoutManagerForType = getLayoutManagerForType(i);
            int findFirstVisibleItemPosition = layoutManagerForType.findFirstVisibleItemPosition();
            if (z) {
                if (findFirstVisibleItemPosition == 1 || findFirstVisibleItemPosition == 2) {
                    layoutManagerForType.scrollToPosition(0);
                    resetTabsY(i);
                }
            } else if (findFirstVisibleItemPosition == 0) {
                layoutManagerForType.scrollToPositionWithOffset(1, 0);
            }
        }
    }

    public void hideSearchKeyboard() {
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.hideKeyboard();
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.hideKeyboard();
        }
    }

    public void openSearch(SearchField searchField) {
        LinearLayoutManager linearLayoutManager;
        View view;
        final RecyclerListView recyclerListView;
        SearchField searchField2;
        EmojiViewDelegate emojiViewDelegate;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int i = 0; i < 3; i++) {
            boolean z = true;
            if (i == 0) {
                searchField2 = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                view = this.emojiTabs;
                linearLayoutManager = this.emojiLayoutManager;
            } else if (i == 1) {
                searchField2 = this.gifSearchField;
                recyclerListView = this.gifGridView;
                view = this.gifTabs;
                linearLayoutManager = this.gifLayoutManager;
            } else {
                searchField2 = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                view = this.stickersTab;
                linearLayoutManager = this.stickersLayoutManager;
            }
            if (searchField2 != null) {
                if (searchField == searchField2 && (emojiViewDelegate = this.delegate) != null && emojiViewDelegate.isExpanded()) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view != null && i != 2) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -AndroidUtilities.dp(40.0f)), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, AndroidUtilities.dp(0.0f)));
                    } else {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, AndroidUtilities.dp(0.0f)));
                    }
                    this.searchAnimation.setDuration(220L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.30
                        {
                            EmojiView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView != EmojiView.this.stickersGridView) {
                                    if (recyclerListView == EmojiView.this.emojiGridView || recyclerListView == EmojiView.this.gifGridView) {
                                        recyclerListView.setPadding(0, 0, 0, 0);
                                    }
                                } else {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                } else {
                    searchField2.setTranslationY(AndroidUtilities.dp(0.0f));
                    if (view != null && i != 2) {
                        view.setTranslationY(-AndroidUtilities.dp(40.0f));
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    } else if (recyclerListView == this.emojiGridView || recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, 0, 0, 0);
                    }
                    if (recyclerListView == this.gifGridView) {
                        GifAdapter gifAdapter = this.gifSearchAdapter;
                        if (this.gifAdapter.results.size() <= 0) {
                            z = false;
                        }
                        if (gifAdapter.showTrendingWhenSearchEmpty = z) {
                            this.gifSearchAdapter.search("");
                            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
                            GifAdapter gifAdapter2 = this.gifSearchAdapter;
                            if (adapter != gifAdapter2) {
                                this.gifGridView.setAdapter(gifAdapter2);
                            }
                        }
                    }
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                }
            }
        }
    }

    private void showEmojiShadow(boolean z, boolean z2) {
        if (!z || this.emojiTabsShadow.getTag() != null) {
            if (!z && this.emojiTabsShadow.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!z) {
                num = 1;
            }
            view.setTag(num);
            float f = 1.0f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.emojiTabShadowAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                View view2 = this.emojiTabsShadow;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.emojiTabShadowAnimator.setDuration(200L);
                this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.31
                    {
                        EmojiView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        EmojiView.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
                return;
            }
            View view3 = this.emojiTabsShadow;
            if (!z) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void closeSearch(boolean z) {
        closeSearch(z, -1L);
    }

    private void scrollStickersToPosition(int i, int i2) {
        View findViewByPosition = this.stickersLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findViewByPosition == null && Math.abs(i - findFirstVisibleItemPosition) > 40) {
            this.stickersScrollHelper.setScrollDirection(this.stickersLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
            this.stickersScrollHelper.scrollToPosition(i, i2, false, true);
            return;
        }
        this.ignoreStickersScroll = true;
        this.stickersGridView.smoothScrollToPosition(i);
    }

    public void scrollEmojisToPosition(int i, int i2) {
        View findViewByPosition = this.emojiLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.emojiLayoutManager.findFirstVisibleItemPosition();
        if (findViewByPosition == null && Math.abs(i - findFirstVisibleItemPosition) > this.emojiLayoutManager.getSpanCount() * 9.0f) {
            this.emojiScrollHelper.setScrollDirection(this.emojiLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
            this.emojiScrollHelper.scrollToPosition(i, i2, false, true);
        } else {
            this.ignoreStickersScroll = true;
            this.emojiGridView.smoothScrollToPosition(i);
        }
        this.emojiSmoothScrolling = true;
    }

    public void closeSearch(boolean z, long j) {
        View view;
        final GridLayoutManager gridLayoutManager;
        final RecyclerListView recyclerListView;
        SearchField searchField;
        TLRPC$TL_messages_stickerSet stickerSetById;
        int positionForPack;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && j != -1 && (stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j)) != null && (positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById)) >= 0 && positionForPack < this.stickersGridAdapter.getItemCount()) {
            scrollStickersToPosition(positionForPack, AndroidUtilities.dp(48.0f));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.showTrendingWhenSearchEmpty = false;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            if (i2 == 0) {
                searchField = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                gridLayoutManager = this.emojiLayoutManager;
                view = this.emojiTabs;
            } else if (i2 == 1) {
                searchField = this.gifSearchField;
                recyclerListView = this.gifGridView;
                gridLayoutManager = this.gifLayoutManager;
                view = this.gifTabs;
            } else {
                searchField = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                gridLayoutManager = this.stickersLayoutManager;
                view = this.stickersTab;
            }
            if (searchField != null) {
                searchField.searchEditText.setText("");
                if (i2 == currentItem && z) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view != null && i2 != i) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight));
                    } else {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, -this.searchFieldHeight));
                    }
                    this.searchAnimation.setDuration(200L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.32
                        {
                            EmojiView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                int findFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                int top = findFirstVisibleItemPosition != -1 ? (int) (gridLayoutManager.findViewByPosition(findFirstVisibleItemPosition).getTop() + recyclerListView.getTranslationY()) : 0;
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView != EmojiView.this.stickersGridView) {
                                    if (recyclerListView != EmojiView.this.gifGridView) {
                                        if (recyclerListView == EmojiView.this.emojiGridView) {
                                            recyclerListView.setPadding(0, AndroidUtilities.dp(36.0f), 0, AndroidUtilities.dp(44.0f));
                                        }
                                    } else {
                                        recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                    }
                                } else {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (findFirstVisibleItemPosition != -1) {
                                    gridLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top - recyclerListView.getPaddingTop());
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                    i = 2;
                } else {
                    searchField.setTranslationY(AndroidUtilities.dp(36.0f) - this.searchFieldHeight);
                    i = 2;
                    if (view != null && i2 != 2) {
                        view.setTranslationY(0.0f);
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.emojiGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(36.0f), 0, AndroidUtilities.dp(44.0f));
                    }
                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                }
            }
        }
        if (!z) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(false, false);
        showBottomTab(true, z);
    }

    public void checkStickersSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.stickersSearchField.showShadow(true, !z);
                return;
            }
            SearchField searchField = this.stickersSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, !z);
        } else if (this.stickersSearchField == null || (recyclerListView = this.stickersGridView) == null) {
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.stickersSearchField.setTranslationY(findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.stickersSearchField.showShadow(false, !z);
        }
    }

    public void checkBottomTabScroll(float f) {
        int i;
        this.lastBottomScrollDy += f;
        if (this.pager.getCurrentItem() == 0) {
            i = AndroidUtilities.dp(38.0f);
        } else {
            i = AndroidUtilities.dp(48.0f);
        }
        float f2 = this.lastBottomScrollDy;
        if (f2 >= i) {
            showBottomTab(false, true);
        } else if (f2 <= (-i)) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() != null || this.lastBottomScrollDy >= 0.0f) && (this.bottomTabContainer.getTag() == null || this.lastBottomScrollDy <= 0.0f)) {
        } else {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    public void showBackspaceButton(final boolean z, boolean z2) {
        if (!z || this.backspaceButton.getTag() != null) {
            if (!z && this.backspaceButton.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!z) {
                num = 1;
            }
            imageView.setTag(num);
            int i = 0;
            float f = 1.0f;
            if (z2) {
                if (z) {
                    this.backspaceButton.setVisibility(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.backspaceButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView2 = this.backspaceButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.backspaceButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.backspaceButtonAnimation.setDuration(200L);
                this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.33
                    {
                        EmojiView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            EmojiView.this.backspaceButton.setVisibility(4);
                        }
                    }
                });
                this.backspaceButtonAnimation.start();
                return;
            }
            this.backspaceButton.setAlpha(z ? 1.0f : 0.0f);
            this.backspaceButton.setScaleX(z ? 1.0f : 0.0f);
            ImageView imageView5 = this.backspaceButton;
            if (!z) {
                f = 0.0f;
            }
            imageView5.setScaleY(f);
            ImageView imageView6 = this.backspaceButton;
            if (!z) {
                i = 4;
            }
            imageView6.setVisibility(i);
        }
    }

    public void showStickerSettingsButton(final boolean z, boolean z2) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView == null) {
            return;
        }
        if (z && imageView.getTag() == null) {
            return;
        }
        if (!z && this.stickerSettingsButton.getTag() != null) {
            return;
        }
        AnimatorSet animatorSet = this.stickersButtonAnimation;
        Integer num = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.stickersButtonAnimation = null;
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (!z) {
            num = 1;
        }
        imageView2.setTag(num);
        int i = 0;
        float f = 1.0f;
        if (z2) {
            if (z) {
                this.stickerSettingsButton.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.stickersButtonAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[3];
            ImageView imageView3 = this.stickerSettingsButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView3, property, fArr);
            ImageView imageView4 = this.stickerSettingsButton;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView4, property2, fArr2);
            ImageView imageView5 = this.stickerSettingsButton;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr3[0] = f;
            animatorArr[2] = ObjectAnimator.ofFloat(imageView5, property3, fArr3);
            animatorSet2.playTogether(animatorArr);
            this.stickersButtonAnimation.setDuration(200L);
            this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.34
                {
                    EmojiView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (!z) {
                        EmojiView.this.stickerSettingsButton.setVisibility(4);
                    }
                }
            });
            this.stickersButtonAnimation.start();
            return;
        }
        this.stickerSettingsButton.setAlpha(z ? 1.0f : 0.0f);
        this.stickerSettingsButton.setScaleX(z ? 1.0f : 0.0f);
        ImageView imageView6 = this.stickerSettingsButton;
        if (!z) {
            f = 0.0f;
        }
        imageView6.setScaleY(f);
        ImageView imageView7 = this.stickerSettingsButton;
        if (!z) {
            i = 4;
        }
        imageView7.setVisibility(i);
    }

    public void showBottomTab(boolean z, boolean z2) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (!z || this.bottomTabContainer.getTag() != null) {
            if (!z && this.bottomTabContainer.getTag() != null) {
                return;
            }
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            AnimatorSet animatorSet = this.bottomTabContainerAnimation;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.bottomTabContainerAnimation = null;
            }
            FrameLayout frameLayout = this.bottomTabContainer;
            if (!z) {
                num = 1;
            }
            frameLayout.setTag(num);
            float f6 = 54.0f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.bottomTabContainerAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                FrameLayout frameLayout2 = this.bottomTabContainer;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                if (z) {
                    f3 = 0.0f;
                } else {
                    f3 = AndroidUtilities.dp(this.needEmojiSearch ? 49.0f : 54.0f);
                }
                fArr[0] = f3;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                FrameLayout frameLayout3 = this.bulletinContainer;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                if (z) {
                    f4 = 0.0f;
                } else {
                    if (this.needEmojiSearch) {
                        f6 = 49.0f;
                    }
                    f4 = AndroidUtilities.dp(f6);
                }
                fArr2[0] = f4;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout3, property2, fArr2);
                View view = this.shadowLine;
                Property property3 = View.TRANSLATION_Y;
                float[] fArr3 = new float[1];
                if (!z) {
                    f5 = AndroidUtilities.dp(49.0f);
                }
                fArr3[0] = f5;
                animatorArr[2] = ObjectAnimator.ofFloat(view, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.bottomTabContainerAnimation.setDuration(200L);
                this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.bottomTabContainerAnimation.start();
                return;
            }
            FrameLayout frameLayout4 = this.bottomTabContainer;
            if (z) {
                f = 0.0f;
            } else {
                f = AndroidUtilities.dp(this.needEmojiSearch ? 49.0f : 54.0f);
            }
            frameLayout4.setTranslationY(f);
            FrameLayout frameLayout5 = this.bulletinContainer;
            if (z) {
                f2 = 0.0f;
            } else {
                if (this.needEmojiSearch) {
                    f6 = 49.0f;
                }
                f2 = AndroidUtilities.dp(f6);
            }
            frameLayout5.setTranslationY(f2);
            View view2 = this.shadowLine;
            if (!z) {
                f5 = AndroidUtilities.dp(49.0f);
            }
            view2.setTranslationY(f5);
        }
    }

    public void checkTabsY(int i, int i2) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (i == 1) {
            checkEmojiTabY(this.emojiGridView, i2);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) || this.ignoreStickersScroll) {
            return;
        }
        RecyclerListView listViewForType = getListViewForType(i);
        if (i2 > 0 && listViewForType != null && listViewForType.getVisibility() == 0 && (findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0)) != null && findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight >= listViewForType.getPaddingTop()) {
            return;
        }
        int[] iArr = this.tabsMinusDy;
        iArr[i] = iArr[i] - i2;
        if (iArr[i] > 0) {
            iArr[i] = 0;
        } else if (iArr[i] < (-AndroidUtilities.dp(288.0f))) {
            this.tabsMinusDy[i] = -AndroidUtilities.dp(288.0f);
        }
        if (i == 0) {
            updateStickerTabsPosition();
        } else {
            getTabsForType(i).setTranslationY(Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[i]));
        }
    }

    private void resetTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            View tabsForType = getTabsForType(i);
            this.tabsMinusDy[i] = 0;
            tabsForType.setTranslationY(0);
        }
    }

    public void animateTabsY(final int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            float dpf2 = AndroidUtilities.dpf2(i == 1 ? 38.0f : 48.0f);
            float f = this.tabsMinusDy[i] / (-dpf2);
            if (f <= 0.0f || f >= 1.0f) {
                animateSearchField(i);
                return;
            }
            View tabsForType = getTabsForType(i);
            int i2 = f > 0.5f ? (int) (-Math.ceil(dpf2)) : 0;
            if (f > 0.5f) {
                animateSearchField(i, false, i2);
            }
            if (i == 1) {
                checkEmojiShadow(i2);
            }
            ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
            if (objectAnimatorArr[i] == null) {
                objectAnimatorArr[i] = ObjectAnimator.ofFloat(tabsForType, View.TRANSLATION_Y, tabsForType.getTranslationY(), i2);
                this.tabsYAnimators[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EmojiView.this.lambda$animateTabsY$8(i, valueAnimator);
                    }
                });
                this.tabsYAnimators[i].setDuration(200L);
            } else {
                objectAnimatorArr[i].setFloatValues(tabsForType.getTranslationY(), i2);
            }
            this.tabsYAnimators[i].start();
        }
    }

    public /* synthetic */ void lambda$animateTabsY$8(int i, ValueAnimator valueAnimator) {
        this.tabsMinusDy[i] = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    public void stopAnimatingTabsY(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[i] == null || !objectAnimatorArr[i].isRunning()) {
            return;
        }
        this.tabsYAnimators[i].cancel();
    }

    private void animateSearchField(int i) {
        RecyclerListView listViewForType = getListViewForType(i);
        boolean z = true;
        int dp = AndroidUtilities.dp(i == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
            int[] iArr = this.tabsMinusDy;
            float f = (bottom - (dp + iArr[i])) / this.searchFieldHeight;
            if (f <= 0.0f && f >= 1.0f) {
                return;
            }
            if (f <= 0.5f) {
                z = false;
            }
            animateSearchField(i, z, iArr[i]);
        }
    }

    private void animateSearchField(int i, boolean z, final int i2) {
        if (getListViewForType(i).findViewHolderForAdapterPosition(0) == null) {
            return;
        }
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(this, getContext()) { // from class: org.telegram.ui.Components.EmojiView.35
            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int getVerticalSnapPreference() {
                return -1;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateTimeForDeceleration(int i3) {
                return super.calculateTimeForDeceleration(i3) * 16;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateDtToFit(int i3, int i4, int i5, int i6, int i7) {
                return super.calculateDtToFit(i3, i4, i5, i6, i7) + i2;
            }
        };
        linearSmoothScroller.setTargetPosition(!z ? 1 : 0);
        getLayoutManagerForType(i).startSmoothScroll(linearSmoothScroller);
    }

    private View getTabsForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiTabs;
            }
            if (i == 2) {
                return this.gifTabs;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersTab;
    }

    private RecyclerListView getListViewForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiGridView;
            }
            if (i == 2) {
                return this.gifGridView;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersGridView;
    }

    private GridLayoutManager getLayoutManagerForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiLayoutManager;
            }
            if (i == 2) {
                return this.gifLayoutManager;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersLayoutManager;
    }

    public SearchField getSearchFieldForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiSearchField;
            }
            if (i == 2) {
                return this.gifSearchField;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersSearchField;
    }

    public void checkEmojiSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.emojiSearchField.showShadow(true, !z);
            } else {
                this.emojiSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.emojiGridView.getPaddingTop(), !z);
            }
            showEmojiShadow(false, !z);
        } else if (this.emojiSearchField == null || (recyclerListView = this.emojiGridView) == null) {
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.emojiSearchField.setTranslationY(findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.emojiSearchField.showShadow(false, !z);
            checkEmojiShadow(Math.round(this.emojiTabs.getTranslationY()));
        }
    }

    private void checkEmojiShadow(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[1] == null || !objectAnimatorArr[1].isRunning()) {
            boolean z = false;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            int dp = AndroidUtilities.dp(38.0f) + i;
            if (dp > 0 && (findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.itemView.getBottom() < dp)) {
                z = true;
            }
            showEmojiShadow(z, !this.isLayout);
        }
    }

    public void checkEmojiTabY(View view, int i) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            emojiTabsStrip.setTranslationY(0);
        } else if (view.getVisibility() != 0 || this.emojiSmoothScrolling) {
        } else {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            if (i > 0 && (recyclerListView = this.emojiGridView) != null && recyclerListView.getVisibility() == 0 && (findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                if (findViewHolderForAdapterPosition.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                    return;
                }
            }
            int[] iArr = this.tabsMinusDy;
            iArr[1] = iArr[1] - i;
            if (iArr[1] > 0) {
                iArr[1] = 0;
            } else if (iArr[1] < (-AndroidUtilities.dp(108.0f))) {
                this.tabsMinusDy[1] = -AndroidUtilities.dp(108.0f);
            }
            this.emojiTabs.setTranslationY(Math.max(-AndroidUtilities.dp(36.0f), this.tabsMinusDy[1]));
        }
    }

    public void checkGifSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        int findLastVisibleItemPosition;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && (recyclerListView2.getAdapter() instanceof GifAdapter)) {
            GifAdapter gifAdapter = (GifAdapter) this.gifGridView.getAdapter();
            if (!gifAdapter.searchEndReached && gifAdapter.reqId == 0 && !gifAdapter.results.isEmpty() && (findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
                gifAdapter.search(gifAdapter.lastSearchImageString, gifAdapter.nextSearchOffset, true, gifAdapter.lastSearchIsEmoji, gifAdapter.lastSearchIsEmoji);
            }
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.gifSearchField.showShadow(true, !z);
                return;
            }
            SearchField searchField = this.gifSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.gifGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, !z);
        } else if (this.gifSearchField == null || (recyclerListView = this.gifGridView) == null) {
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.gifSearchField.setTranslationY(findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.gifSearchField.showShadow(false, !z);
        }
    }

    public void scrollGifsToTop() {
        GifLayoutManager gifLayoutManager = this.gifLayoutManager;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        gifLayoutManager.scrollToPositionWithOffset((emojiViewDelegate == null || !emojiViewDelegate.isExpanded()) ? 1 : 0, 0);
        resetTabsY(2);
    }

    public void checkScroll(int i) {
        int findFirstVisibleItemPosition;
        int findFirstVisibleItemPosition2;
        if (i == 0) {
            if (this.ignoreStickersScroll || (findFirstVisibleItemPosition2 = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1 || this.stickersGridView == null) {
                return;
            }
            int i2 = this.favTabNum;
            if (i2 <= 0 && (i2 = this.recentTabNum) <= 0) {
                i2 = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition2), i2);
        } else if (i != 2) {
        } else {
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter = this.gifAdapter;
            if (adapter != gifAdapter || gifAdapter.trendingSectionItem < 0 || this.gifTrendingTabNum < 0 || this.gifRecentTabNum < 0 || (findFirstVisibleItemPosition = this.gifLayoutManager.findFirstVisibleItemPosition()) == -1) {
                return;
            }
            this.gifTabs.onPageScrolled(findFirstVisibleItemPosition >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
        }
    }

    public void saveNewPage() {
        ViewPager viewPager = this.pager;
        if (viewPager == null) {
            return;
        }
        int currentItem = viewPager.getCurrentItem();
        int i = 1;
        if (currentItem != 2) {
            i = currentItem == 1 ? 2 : 0;
        }
        if (this.currentPage == i) {
            return;
        }
        this.currentPage = i;
        MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", i).commit();
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    public void onPageScrolled(int i, int i2, int i3) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null) {
            return;
        }
        int i4 = 0;
        if (i == 1) {
            if (i3 != 0) {
                i4 = 2;
            }
            emojiViewDelegate.onTabOpened(i4);
        } else if (i == 2) {
            emojiViewDelegate.onTabOpened(3);
        } else {
            emojiViewDelegate.onTabOpened(0);
        }
    }

    public void postBackspaceRunnable(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                EmojiView.this.lambda$postBackspaceRunnable$9(i);
            }
        }, i);
    }

    public /* synthetic */ void lambda$postBackspaceRunnable$9(int i) {
        if (!this.backspacePressed) {
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
            this.backspaceButton.performHapticFeedback(3);
        }
        this.backspaceOnce = true;
        postBackspaceRunnable(Math.max(50, i - 100));
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    public void updateStickerTabs() {
        ArrayList<TLRPC$Document> arrayList;
        ArrayList<TLRPC$Document> arrayList2;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip == null || scrollSlidingTabStrip.isDragging()) {
            return;
        }
        this.recentTabNum = -2;
        this.favTabNum = -2;
        this.trendingTabNum = -2;
        this.premiumTabNum = -2;
        this.hasChatStickers = false;
        this.stickersTabOffset = 0;
        int currentPosition = this.stickersTab.getCurrentPosition();
        this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        SharedPreferences emojiSettings = MessagesController.getEmojiSettings(this.currentAccount);
        this.featuredStickerSets.clear();
        ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = mediaDataController.getFeaturedStickerSets();
        int size = featuredStickerSets.size();
        for (int i = 0; i < size; i++) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredStickerSets.get(i);
            if (!mediaDataController.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id)) {
                this.featuredStickerSets.add(tLRPC$StickerSetCovered);
            }
        }
        TrendingAdapter trendingAdapter = this.trendingAdapter;
        if (trendingAdapter != null) {
            trendingAdapter.notifyDataSetChanged();
        }
        if (!featuredStickerSets.isEmpty() && (this.featuredStickerSets.isEmpty() || emojiSettings.getLong("featured_hidden", 0L) == featuredStickerSets.get(0).set.id)) {
            int i2 = mediaDataController.getUnreadStickerSets().isEmpty() ? 2 : 3;
            StickerTabView addStickerIconTab = this.stickersTab.addStickerIconTab(i2, this.stickerIcons[i2]);
            addStickerIconTab.textView.setText(LocaleController.getString("FeaturedStickersShort", R.string.FeaturedStickersShort));
            addStickerIconTab.setContentDescription(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers));
            int i3 = this.stickersTabOffset;
            this.trendingTabNum = i3;
            this.stickersTabOffset = i3 + 1;
        }
        if (!this.favouriteStickers.isEmpty()) {
            int i4 = this.stickersTabOffset;
            this.favTabNum = i4;
            this.stickersTabOffset = i4 + 1;
            StickerTabView addStickerIconTab2 = this.stickersTab.addStickerIconTab(1, this.stickerIcons[1]);
            addStickerIconTab2.textView.setText(LocaleController.getString("FavoriteStickersShort", R.string.FavoriteStickersShort));
            addStickerIconTab2.setContentDescription(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers));
        }
        if (!this.recentStickers.isEmpty()) {
            int i5 = this.stickersTabOffset;
            this.recentTabNum = i5;
            this.stickersTabOffset = i5 + 1;
            StickerTabView addStickerIconTab3 = this.stickersTab.addStickerIconTab(0, this.stickerIcons[0]);
            addStickerIconTab3.textView.setText(LocaleController.getString("RecentStickersShort", R.string.RecentStickersShort));
            addStickerIconTab3.setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
        }
        this.stickerSets.clear();
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
        this.groupStickerSet = null;
        this.groupStickerPackPosition = -1;
        this.groupStickerPackNum = -10;
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = mediaDataController.getStickerSets(0);
        int i6 = 0;
        while (true) {
            TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i6 >= tLRPC$StickerSetCoveredArr.length) {
                break;
            }
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = tLRPC$StickerSetCoveredArr[i6];
            if (tLRPC$StickerSetCovered2 != null) {
                TLRPC$TL_messages_stickerSet stickerSetById = mediaDataController.getStickerSetById(tLRPC$StickerSetCovered2.set.id);
                if (stickerSetById != null && !stickerSetById.set.archived) {
                    this.primaryInstallingStickerSets[i6] = null;
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = new TLRPC$TL_messages_stickerSet();
                    tLRPC$TL_messages_stickerSet2.set = tLRPC$StickerSetCovered2.set;
                    TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered2.cover;
                    if (tLRPC$Document != null) {
                        tLRPC$TL_messages_stickerSet2.documents.add(tLRPC$Document);
                    } else if (!tLRPC$StickerSetCovered2.covers.isEmpty()) {
                        tLRPC$TL_messages_stickerSet2.documents.addAll(tLRPC$StickerSetCovered2.covers);
                    }
                    if (!tLRPC$TL_messages_stickerSet2.documents.isEmpty()) {
                        this.stickerSets.add(tLRPC$TL_messages_stickerSet2);
                    }
                }
            }
            i6++;
        }
        this.premiumStickers.clear();
        ArrayList<TLRPC$TL_messages_stickerSet> filterPremiumStickers = MessagesController.getInstance(this.currentAccount).filterPremiumStickers(stickerSets);
        for (int i7 = 0; i7 < filterPremiumStickers.size(); i7++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = filterPremiumStickers.get(i7);
            if (!tLRPC$TL_messages_stickerSet3.set.archived && (arrayList2 = tLRPC$TL_messages_stickerSet3.documents) != null && !arrayList2.isEmpty()) {
                this.stickerSets.add(tLRPC$TL_messages_stickerSet3);
                if (!MessagesController.getInstance(this.currentAccount).premiumLocked && UserConfig.getInstance(this.currentAccount).isPremium()) {
                    for (int i8 = 0; i8 < tLRPC$TL_messages_stickerSet3.documents.size(); i8++) {
                        if (MessageObject.isPremiumSticker(tLRPC$TL_messages_stickerSet3.documents.get(i8))) {
                            this.premiumStickers.add(tLRPC$TL_messages_stickerSet3.documents.get(i8));
                        }
                    }
                }
            }
        }
        if (!this.premiumStickers.isEmpty()) {
            int i9 = this.stickersTabOffset;
            this.premiumTabNum = i9;
            this.stickersTabOffset = i9 + 1;
            StickerTabView addStickerIconTab4 = this.stickersTab.addStickerIconTab(4, PremiumGradient.getInstance().premiumStarMenuDrawable2);
            addStickerIconTab4.textView.setText(LocaleController.getString("PremiumStickersShort", R.string.PremiumStickersShort));
            addStickerIconTab4.setContentDescription(LocaleController.getString("PremiumStickers", R.string.PremiumStickers));
        }
        if (this.info != null) {
            long j = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1L);
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
            if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                this.groupStickersHidden = j != -1;
            } else {
                TLRPC$StickerSet tLRPC$StickerSet = this.info.stickerset;
                if (tLRPC$StickerSet != null) {
                    this.groupStickersHidden = j == tLRPC$StickerSet.id;
                }
            }
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$ChatFull.stickerset;
            if (tLRPC$StickerSet2 != null) {
                TLRPC$TL_messages_stickerSet groupStickerSetById = mediaDataController.getGroupStickerSetById(tLRPC$StickerSet2);
                if (groupStickerSetById != null && (arrayList = groupStickerSetById.documents) != null && !arrayList.isEmpty() && groupStickerSetById.set != null) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet4 = new TLRPC$TL_messages_stickerSet();
                    tLRPC$TL_messages_stickerSet4.documents = groupStickerSetById.documents;
                    tLRPC$TL_messages_stickerSet4.packs = groupStickerSetById.packs;
                    tLRPC$TL_messages_stickerSet4.set = groupStickerSetById.set;
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(tLRPC$TL_messages_stickerSet4);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, tLRPC$TL_messages_stickerSet4);
                    }
                    if (this.info.can_set_stickers) {
                        tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet4;
                    }
                    this.groupStickerSet = tLRPC$TL_messages_stickerSet;
                }
            } else if (tLRPC$ChatFull.can_set_stickers) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet5 = new TLRPC$TL_messages_stickerSet();
                if (this.groupStickersHidden) {
                    this.groupStickerPackNum = this.stickerSets.size();
                    this.stickerSets.add(tLRPC$TL_messages_stickerSet5);
                } else {
                    this.groupStickerPackNum = 0;
                    this.stickerSets.add(0, tLRPC$TL_messages_stickerSet5);
                }
            }
        }
        int i10 = 0;
        while (i10 < this.stickerSets.size()) {
            if (i10 == this.groupStickerPackNum) {
                TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
                if (chat2 == null) {
                    this.stickerSets.remove(0);
                    i10--;
                } else {
                    this.hasChatStickers = true;
                    this.stickersTab.addStickerTab(chat2);
                }
            } else {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet6 = this.stickerSets.get(i10);
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_messages_stickerSet6.documents.get(0);
                TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet6.set.thumbs, 90);
                if (closestPhotoSizeWithSize == null || tLRPC$TL_messages_stickerSet6.set.gifs) {
                    closestPhotoSizeWithSize = tLRPC$Document2;
                }
                this.stickersTab.addStickerTab(closestPhotoSizeWithSize, tLRPC$Document2, tLRPC$TL_messages_stickerSet6).setContentDescription(tLRPC$TL_messages_stickerSet6.set.title + ", " + LocaleController.getString("AccDescrStickerSet", R.string.AccDescrStickerSet));
            }
            i10++;
        }
        this.stickersTab.commitUpdate();
        this.stickersTab.updateTabStyles();
        if (currentPosition != 0) {
            this.stickersTab.onPageScrolled(currentPosition, currentPosition);
        }
        checkPanels();
    }

    private void checkPanels() {
        int findFirstVisibleItemPosition;
        if (this.stickersTab == null || (findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1) {
            return;
        }
        int i = this.favTabNum;
        if (i <= 0 && (i = this.recentTabNum) <= 0) {
            i = this.stickersTabOffset;
        }
        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
    }

    private void updateGifTabs() {
        int i;
        int currentPosition = this.gifTabs.getCurrentPosition();
        int i2 = this.gifRecentTabNum;
        boolean z = currentPosition == i2;
        boolean z2 = i2 >= 0;
        boolean z3 = !this.recentGifs.isEmpty();
        this.gifTabs.beginUpdate(false);
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        if (z3) {
            this.gifRecentTabNum = 0;
            this.gifTabs.addIconTab(0, this.gifIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
            i = 1;
        } else {
            i = 0;
        }
        this.gifTrendingTabNum = i;
        this.gifTabs.addIconTab(1, this.gifIcons[1]).setContentDescription(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs));
        this.gifFirstEmojiTabNum = i + 1;
        AndroidUtilities.dp(13.0f);
        AndroidUtilities.dp(11.0f);
        ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            String str = arrayList.get(i3);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(str);
            if (emojiDrawable != null) {
                this.gifTabs.addEmojiTab(i3 + 3, emojiDrawable, MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(str)).setContentDescription(str);
            }
        }
        this.gifTabs.commitUpdate();
        this.gifTabs.updateTabStyles();
        if (z && !z3) {
            this.gifTabs.selectTab(this.gifTrendingTabNum);
        } else if (ViewCompat.isLaidOut(this.gifTabs)) {
            if (z3 && !z2) {
                this.gifTabs.onPageScrolled(currentPosition + 1, 0);
            } else if (!z3 && z2) {
                this.gifTabs.onPageScrolled(currentPosition - 1, 0);
            }
        }
    }

    public void addRecentSticker(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, null, tLRPC$Document, (int) (System.currentTimeMillis() / 1000), false);
        boolean isEmpty = this.recentStickers.isEmpty();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (!isEmpty) {
            return;
        }
        updateStickerTabs();
    }

    public void addRecentGif(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return;
        }
        boolean isEmpty = this.recentGifs.isEmpty();
        updateRecentGifs();
        if (!isEmpty) {
            return;
        }
        updateStickerTabs();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.isLayout) {
            return;
        }
        super.requestLayout();
    }

    public void updateColors() {
        SearchField searchField;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            }
        }
        EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
        if (emojiTabsStrip != null) {
            emojiTabsStrip.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            this.emojiTabsShadow.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        }
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        if (emojiColorPickerView != null) {
            Theme.setDrawableColor(emojiColorPickerView.backgroundDrawable, getThemedColor("dialogBackground"));
            Theme.setDrawableColor(this.pickerView.arrowDrawable, getThemedColor("dialogBackground"));
        }
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                searchField = this.stickersSearchField;
            } else if (i == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
                searchField.shadowView.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), "chat_emojiSearchBackground");
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(getThemedColor("chat_emojiSearchIcon"));
                searchField.searchEditText.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            }
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(getThemedColor("chat_emojiPanelNewTrending"));
        }
        RecyclerListView recyclerListView = this.emojiGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        }
        RecyclerListView recyclerListView2 = this.stickersGridView;
        if (recyclerListView2 != null) {
            recyclerListView2.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.gifTabs.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            this.gifTabs.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ImageView imageView = this.backspaceButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            if (this.emojiSearchField == null) {
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor("chat_emojiPanelBackground"), false);
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor("chat_emojiPanelBackground"), true);
            }
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.searchButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(getThemedColor("chat_gifSaveHintBackground"));
            this.mediaBanTooltip.setTextColor(getThemedColor("chat_gifSaveHintText"));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.progressEmptyView.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            this.gifSearchAdapter.progressEmptyView.textView.setTextColor(getThemedColor("chat_emojiPanelEmptyText"));
            this.gifSearchAdapter.progressEmptyView.progressView.setProgressColor(getThemedColor("progressCircle"));
        }
        int i2 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (i2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[i2], getThemedColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.tabIcons[i2], getThemedColor("chat_emojiPanelIconSelected"), true);
            i2++;
        }
        EmojiTabsStrip emojiTabsStrip2 = this.emojiTabs;
        if (emojiTabsStrip2 != null) {
            emojiTabsStrip2.updateColors();
        }
        int i3 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.stickerIcons;
            if (i3 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[i3], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[i3], getThemedColor("chat_emojiPanelIconSelected"), true);
            i3++;
        }
        int i4 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.gifIcons;
            if (i4 >= drawableArr3.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr3[i4], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.gifIcons[i4], getThemedColor("chat_emojiPanelIconSelected"), true);
            i4++;
        }
        Drawable drawable = this.searchIconDrawable;
        if (drawable != null) {
            Theme.setEmojiDrawableColor(drawable, getThemedColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.searchIconDrawable, getThemedColor("chat_emojiPanelIconSelected"), true);
        }
        Drawable drawable2 = this.searchIconDotDrawable;
        if (drawable2 != null) {
            Theme.setEmojiDrawableColor(drawable2, getThemedColor("chat_emojiPanelStickerPackSelectorLine"), false);
            Theme.setEmojiDrawableColor(this.searchIconDotDrawable, getThemedColor("chat_emojiPanelStickerPackSelectorLine"), true);
        }
        Paint paint2 = this.emojiLockPaint;
        if (paint2 != null) {
            paint2.setColor(getThemedColor("chat_emojiPanelStickerSetName"));
            Paint paint3 = this.emojiLockPaint;
            paint3.setAlpha((int) (paint3.getAlpha() * 0.5f));
        }
        Drawable drawable3 = this.emojiLockDrawable;
        if (drawable3 != null) {
            drawable3.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelStickerSetName"), PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation(AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(R.drawable.smiles_popup);
                getBackground().setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
        this.isLayout = false;
        setTranslationY(getTranslationY());
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        View view = (View) getParent();
        if (view != null) {
            int i6 = i4 - i2;
            int height = view.getHeight();
            if (this.lastNotifyHeight != i6 || this.lastNotifyHeight2 != height) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY(AndroidUtilities.dp(49.0f));
                    this.bulletinContainer.setTranslationY(AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null && i6 <= this.lastNotifyHeight) {
                    this.bottomTabContainer.setTranslationY(0.0f);
                    this.bulletinContainer.setTranslationY(0.0f);
                }
                this.lastNotifyHeight = i6;
                this.lastNotifyHeight2 = height;
            }
        }
        super.onLayout(z, i, i2, i3, i4);
        updateStickerTabsPosition();
    }

    public void reloadStickersAdapter() {
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

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean z) {
        this.forseMultiwindowLayout = z;
    }

    public void onOpen(boolean z) {
        if (this.currentPage != 0 && this.currentChatId != 0) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || z || this.views.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() == 0) {
                return;
            }
            this.pager.setCurrentItem(0, !z);
            return;
        }
        int i = this.currentPage;
        if (i != 1) {
            if (i != 2) {
                return;
            }
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip == null) {
                return;
            }
            scrollSlidingTabStrip.selectTab(0);
            return;
        }
        showBackspaceButton(false, false);
        showStickerSettingsButton(true, false);
        if (this.pager.getCurrentItem() != 2) {
            this.pager.setCurrentItem(2, false);
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 == null) {
            return;
        }
        this.firstTabUpdate = true;
        int i2 = this.favTabNum;
        if (i2 >= 0) {
            scrollSlidingTabStrip2.selectTab(i2);
        } else {
            int i3 = this.recentTabNum;
            if (i3 >= 0) {
                scrollSlidingTabStrip2.selectTab(i3);
            } else {
                scrollSlidingTabStrip2.selectTab(this.stickersTabOffset);
            }
        }
        this.firstTabUpdate = false;
        this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.this.lambda$onAttachedToWindow$10();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAttachedToWindow$10() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                updateStickerTabs();
                reloadStickersAdapter();
            }
            checkDocuments(true);
            checkDocuments(false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
        ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker != null) {
            chooseStickerActionTracker.checkVisibility();
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
        if (emojiPopupWindow == null || !emojiPopupWindow.isShowing()) {
            return;
        }
        this.pickerViewPopup.dismiss();
    }

    private void checkDocuments(boolean z) {
        if (z) {
            updateRecentGifs();
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        int i = 0;
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        for (int i2 = 0; i2 < this.favouriteStickers.size(); i2++) {
            TLRPC$Document tLRPC$Document = this.favouriteStickers.get(i2);
            int i3 = 0;
            while (true) {
                if (i3 < this.recentStickers.size()) {
                    TLRPC$Document tLRPC$Document2 = this.recentStickers.get(i3);
                    if (tLRPC$Document2.dc_id == tLRPC$Document.dc_id && tLRPC$Document2.id == tLRPC$Document.id) {
                        this.recentStickers.remove(i3);
                        break;
                    }
                    i3++;
                }
            }
        }
        if (MessagesController.getInstance(this.currentAccount).premiumLocked) {
            int i4 = 0;
            while (i4 < this.favouriteStickers.size()) {
                if (MessageObject.isPremiumSticker(this.favouriteStickers.get(i4))) {
                    this.favouriteStickers.remove(i4);
                    i4--;
                }
                i4++;
            }
            while (i < this.recentStickers.size()) {
                if (MessageObject.isPremiumSticker(this.recentStickers.get(i))) {
                    this.recentStickers.remove(i);
                    i--;
                }
                i++;
            }
        }
        if (size != this.recentStickers.size() || size2 != this.favouriteStickers.size()) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void updateRecentGifs() {
        GifAdapter gifAdapter;
        int size = this.recentGifs.size();
        long calcDocumentsHash = MediaDataController.calcDocumentsHash(this.recentGifs, ConnectionsManager.DEFAULT_DATACENTER_ID);
        ArrayList<TLRPC$Document> recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs;
        long calcDocumentsHash2 = MediaDataController.calcDocumentsHash(recentGifs, ConnectionsManager.DEFAULT_DATACENTER_ID);
        if ((this.gifTabs != null && size == 0 && !this.recentGifs.isEmpty()) || (size != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((size == this.recentGifs.size() && calcDocumentsHash == calcDocumentsHash2) || (gifAdapter = this.gifAdapter) == null) {
            return;
        }
        gifAdapter.notifyDataSetChanged();
    }

    public void setStickersBanned(boolean z, long j) {
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip == null) {
            return;
        }
        if (z) {
            this.currentChatId = j;
        } else {
            this.currentChatId = 0L;
        }
        View tab = pagerSlidingTabStrip.getTab(2);
        if (tab == null) {
            return;
        }
        tab.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
        if (this.currentChatId == 0 || this.pager.getCurrentItem() == 0) {
            return;
        }
        showBackspaceButton(true, true);
        showStickerSettingsButton(false, true);
        this.pager.setCurrentItem(0, false);
    }

    public void showStickerBanHint(boolean z) {
        TLRPC$Chat chat;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        if (this.mediaBanTooltip.getVisibility() == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId))) == null) {
            return;
        }
        if (ChatObject.hasAdminRights(chat) || (tLRPC$TL_chatBannedRights = chat.default_banned_rights) == null || !tLRPC$TL_chatBannedRights.send_stickers) {
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = chat.banned_rights;
            if (tLRPC$TL_chatBannedRights2 == null) {
                return;
            }
            if (AndroidUtilities.isBannedForever(tLRPC$TL_chatBannedRights2)) {
                if (z) {
                    this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", R.string.AttachGifRestrictedForever));
                } else {
                    this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", R.string.AttachStickersRestrictedForever));
                }
            } else if (z) {
                this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
            } else {
                this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
            }
        } else if (z) {
            this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", R.string.GlobalAttachGifRestricted));
        } else {
            this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", R.string.GlobalAttachStickersRestricted));
        }
        this.mediaBanTooltip.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, 0.0f, 1.0f));
        animatorSet.addListener(new AnonymousClass36());
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$36 */
    /* loaded from: classes3.dex */
    public class AnonymousClass36 extends AnimatorListenerAdapter {
        AnonymousClass36() {
            EmojiView.this = r1;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$36$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.AnonymousClass36.this.lambda$onAnimationEnd$0();
                }
            }, 5000L);
        }

        public /* synthetic */ void lambda$onAnimationEnd$0() {
            if (EmojiView.this.mediaBanTooltip == null) {
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.36.1
                {
                    AnonymousClass36.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (EmojiView.this.mediaBanTooltip != null) {
                        EmojiView.this.mediaBanTooltip.setVisibility(4);
                    }
                }
            });
            animatorSet.setDuration(300L);
            animatorSet.start();
        }
    }

    private void updateVisibleTrendingSets() {
        boolean z;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView == null) {
            return;
        }
        try {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.stickersGridView.getChildAt(i);
                if ((childAt instanceof FeaturedStickerSetInfoCell) && ((RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(childAt)) != null) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                    ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                    TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                    boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                    int i2 = 0;
                    while (true) {
                        TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
                        if (i2 >= tLRPC$StickerSetCoveredArr.length) {
                            z = false;
                            break;
                        } else if (tLRPC$StickerSetCoveredArr[i2] != null && tLRPC$StickerSetCoveredArr[i2].set.id == stickerSet.set.id) {
                            z = true;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    featuredStickerSetInfoCell.setStickerSet(stickerSet, z2, true, 0, 0, z);
                    if (z2) {
                        MediaDataController.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSet.set.id);
                    }
                    boolean z3 = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                    boolean z4 = this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                    if (z3 || z4) {
                        if (z3 && featuredStickerSetInfoCell.isInstalled()) {
                            this.installingStickerSets.remove(stickerSet.set.id);
                            z3 = false;
                        } else if (z4 && !featuredStickerSetInfoCell.isInstalled()) {
                            this.removingStickerSets.remove(stickerSet.set.id);
                        }
                    }
                    featuredStickerSetInfoCell.setAddDrawProgress(!z && z3, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        return stickersGridAdapter != null && stickersGridAdapter.getItemCount() > 0;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$StickerSet tLRPC$StickerSet;
        int i3 = 0;
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() == 0) {
                updateStickerTabs();
                updateVisibleTrendingSets();
                reloadStickersAdapter();
                checkPanels();
            } else if (((Integer) objArr[0]).intValue() != 5) {
            } else {
                this.emojiAdapter.notifyDataSetChanged();
                EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
                if (emojiTabsStrip == null) {
                    return;
                }
                emojiTabsStrip.updateEmojiPacks();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
            int intValue = ((Integer) objArr[1]).intValue();
            if (!booleanValue && intValue != 0 && intValue != 2) {
                return;
            }
            checkDocuments(booleanValue);
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            updateVisibleTrendingSets();
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int childCount = pagerSlidingTabStrip.getChildCount();
                while (i3 < childCount) {
                    this.typeTabs.getChildAt(i3).invalidate();
                    i3++;
                }
            }
            updateStickerTabs();
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull == null || (tLRPC$StickerSet = tLRPC$ChatFull.stickerset) == null || tLRPC$StickerSet.id != ((Long) objArr[0]).longValue()) {
                return;
            }
            updateStickerTabs();
        } else if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int childCount2 = recyclerListView.getChildCount();
                while (i3 < childCount2) {
                    View childAt = this.stickersGridView.getChildAt(i3);
                    if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                        childAt.invalidate();
                    }
                    i3++;
                }
            }
            EmojiColorPickerView emojiColorPickerView = this.pickerView;
            if (emojiColorPickerView != null) {
                emojiColorPickerView.invalidate();
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip == null) {
                return;
            }
            scrollSlidingTabStrip.invalidateTabs();
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            if (this.emojiGridView == null || !this.needEmojiSearch) {
                return;
            }
            if ((!this.emojiSearchField.progressDrawable.isAnimating() && this.emojiGridView.getAdapter() != this.emojiSearchAdapter) || TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                return;
            }
            EmojiSearchAdapter emojiSearchAdapter = this.emojiSearchAdapter;
            emojiSearchAdapter.search(emojiSearchAdapter.lastSearchEmojiString);
        } else if (i != NotificationCenter.currentUserPremiumStatusChanged) {
        } else {
            updateStickerTabs();
            EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
            if (emojiGridAdapter == null) {
                return;
            }
            emojiGridAdapter.notifyDataSetChanged();
        }
    }

    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    /* loaded from: classes3.dex */
    public class TrendingAdapter extends RecyclerListView.SelectionAdapter {
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private TrendingAdapter() {
            EmojiView.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            BackupImageView backupImageView = new BackupImageView(EmojiView.this.getContext()) { // from class: org.telegram.ui.Components.EmojiView.TrendingAdapter.1
                {
                    TrendingAdapter.this = this;
                }

                @Override // org.telegram.ui.Components.BackupImageView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (!MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(((TLRPC$StickerSetCovered) getTag()).set.id) || EmojiView.this.dotPaint == null) {
                        return;
                    }
                    canvas.drawCircle(canvas.getWidth() - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
                }
            };
            backupImageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            backupImageView.setLayerNum(1);
            backupImageView.setAspectFit(true);
            backupImageView.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f)));
            return new RecyclerListView.Holder(backupImageView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageLocation forSticker;
            BackupImageView backupImageView = (BackupImageView) viewHolder.itemView;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) EmojiView.this.featuredStickerSets.get(i);
            backupImageView.setTag(tLRPC$StickerSetCovered);
            TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
            if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                tLRPC$Document = tLRPC$StickerSetCovered.covers.get(0);
            }
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered.set.thumbs, 90);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$StickerSetCovered.set.thumbs, "emptyListPlaceholder", 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            if (closestPhotoSizeWithSize == null || MessageObject.isVideoSticker(tLRPC$Document)) {
                closestPhotoSizeWithSize = tLRPC$Document;
            }
            boolean z = closestPhotoSizeWithSize instanceof TLRPC$Document;
            if (z) {
                forSticker = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else if (!(closestPhotoSizeWithSize instanceof TLRPC$PhotoSize)) {
                return;
            } else {
                forSticker = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$StickerSetCovered.set.thumb_version);
            }
            ImageLocation imageLocation = forSticker;
            if (imageLocation == null) {
                return;
            }
            if (!z || (!MessageObject.isAnimatedStickerDocument(tLRPC$Document, true) && !MessageObject.isVideoSticker(tLRPC$Document))) {
                if (imageLocation.imageType == 1) {
                    backupImageView.setImage(imageLocation, "30_30", "tgs", svgThumb, tLRPC$StickerSetCovered);
                } else {
                    backupImageView.setImage(imageLocation, (String) null, "webp", svgThumb, tLRPC$StickerSetCovered);
                }
            } else if (svgThumb != null) {
                backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", svgThumb, 0, tLRPC$StickerSetCovered);
            } else {
                backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", imageLocation, (String) null, 0, tLRPC$StickerSetCovered);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return EmojiView.this.featuredStickerSets.size();
        }
    }

    /* loaded from: classes3.dex */
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
            EmojiView.this = r1;
            this.context = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.itemView instanceof RecyclerListView;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public int getPositionForPack(Object obj) {
            Integer num = this.packStartPosition.get(obj);
            if (num == null) {
                return -1;
            }
            return num.intValue();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            if (!(obj instanceof String)) {
                return 2;
            }
            if ("trend1".equals(obj)) {
                return 5;
            }
            return "trend2".equals(obj) ? 6 : 3;
        }

        public int getTabForPosition(int i) {
            Object obj = this.cache.get(i);
            if ("search".equals(obj) || "trend1".equals(obj) || "trend2".equals(obj)) {
                if (EmojiView.this.favTabNum >= 0) {
                    return EmojiView.this.favTabNum;
                }
                if (EmojiView.this.recentTabNum < 0) {
                    return 0;
                }
                return EmojiView.this.recentTabNum;
            }
            if (i == 0) {
                i = 1;
            }
            if (this.stickersPerRow == 0) {
                int measuredWidth = EmojiView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            int i2 = this.positionToRow.get(i, Integer.MIN_VALUE);
            if (i2 == Integer.MIN_VALUE) {
                return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
            }
            Object obj2 = this.rowStartPack.get(i2);
            if (!(obj2 instanceof String)) {
                return EmojiView.this.stickerSets.indexOf((TLRPC$TL_messages_stickerSet) obj2) + EmojiView.this.stickersTabOffset;
            } else if ("premium".equals(obj2)) {
                return EmojiView.this.premiumTabNum;
            } else {
                return "recent".equals(obj2) ? EmojiView.this.recentTabNum : EmojiView.this.favTabNum;
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1(StickerSetNameCell stickerSetNameCell, View view) {
            RecyclerView.ViewHolder childViewHolder;
            if (EmojiView.this.stickersGridView.indexOfChild(stickerSetNameCell) == -1 || (childViewHolder = EmojiView.this.stickersGridView.getChildViewHolder(stickerSetNameCell)) == null) {
                return;
            }
            if (childViewHolder.getAdapterPosition() == EmojiView.this.groupStickerPackPosition) {
                if (EmojiView.this.groupStickerSet != null) {
                    if (EmojiView.this.delegate == null) {
                        return;
                    }
                    EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
                    return;
                }
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                edit.putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0L).apply();
                EmojiView.this.updateStickerTabs();
                if (EmojiView.this.stickersGridAdapter == null) {
                    return;
                }
                EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
            } else if (this.cache.get(childViewHolder.getAdapterPosition()) != EmojiView.this.recentStickers) {
            } else {
                AlertDialog create = new AlertDialog.Builder(this.context).setTitle(LocaleController.getString((int) R.string.ClearRecentStickersAlertTitle)).setMessage(LocaleController.getString((int) R.string.ClearRecentStickersAlertMessage)).setPositiveButton(LocaleController.getString((int) R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$0(dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).create();
                create.show();
                TextView textView = (TextView) create.getButton(-1);
                if (textView == null) {
                    return;
                }
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0(DialogInterface dialogInterface, int i) {
            MediaDataController.getInstance(EmojiView.this.currentAccount).clearRecentStickers();
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2(View view) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$3(View view) {
            ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
            if (!featuredStickerSets.isEmpty()) {
                MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("featured_hidden", featuredStickerSets.get(0).set.id).commit();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyItemRangeRemoved(1, 2);
                }
                EmojiView.this.updateStickerTabs();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        @SuppressLint({"NotifyDataSetChanged"})
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            RecyclerListView recyclerListView = null;
            switch (i) {
                case 0:
                    recyclerListView = new StickerEmojiCell(this, this.context, true) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                        }
                    };
                    break;
                case 1:
                    recyclerListView = new EmptyCell(this.context);
                    break;
                case 2:
                    final StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell.setOnIconClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$1(stickerSetNameCell, view);
                        }
                    });
                    recyclerListView = stickerSetNameCell;
                    break;
                case 3:
                    StickerSetGroupInfoCell stickerSetGroupInfoCell = new StickerSetGroupInfoCell(this.context);
                    stickerSetGroupInfoCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$2(view);
                        }
                    });
                    stickerSetGroupInfoCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    recyclerListView = stickerSetGroupInfoCell;
                    break;
                case 4:
                    View view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    recyclerListView = view;
                    break;
                case 5:
                    StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell2.setOnIconClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$3(view2);
                        }
                    });
                    recyclerListView = stickerSetNameCell2;
                    break;
                case 6:
                    RecyclerListView recyclerListView2 = new RecyclerListView(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.2
                        {
                            StickersGridAdapter.this = this;
                        }

                        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1) || canScrollHorizontally(1));
                                EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    recyclerListView2.setSelectorRadius(AndroidUtilities.dp(4.0f));
                    recyclerListView2.setSelectorDrawableColor(EmojiView.this.getThemedColor("listSelectorSDK21"));
                    recyclerListView2.setTag(9);
                    recyclerListView2.setItemAnimator(null);
                    recyclerListView2.setLayoutAnimation(null);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.3
                        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    linearLayoutManager.setOrientation(0);
                    recyclerListView2.setLayoutManager(linearLayoutManager);
                    EmojiView emojiView = EmojiView.this;
                    recyclerListView2.setAdapter(emojiView.trendingAdapter = new TrendingAdapter());
                    recyclerListView2.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view2, int i2) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$4(view2, i2);
                        }
                    });
                    recyclerListView2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(52.0f)));
                    recyclerListView = recyclerListView2;
                    break;
            }
            return new RecyclerListView.Holder(recyclerListView);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$4(View view, int i) {
            EmojiView.this.openTrendingStickers((TLRPC$StickerSetCovered) view.getTag());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            String str;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, this.cacheParents.get(i), false);
                stickerEmojiCell.setRecent(EmojiView.this.recentStickers.contains(tLRPC$Document));
                return;
            }
            ArrayList<TLRPC$Document> arrayList = null;
            TLRPC$Chat tLRPC$Chat = null;
            int i3 = 1;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i4 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i4 == Integer.MIN_VALUE) {
                        emptyCell.setHeight(1);
                        return;
                    }
                    Object obj = this.rowStartPack.get(i4);
                    if (obj instanceof TLRPC$TL_messages_stickerSet) {
                        arrayList = ((TLRPC$TL_messages_stickerSet) obj).documents;
                    } else if (obj instanceof String) {
                        arrayList = "recent".equals(obj) ? EmojiView.this.recentStickers : EmojiView.this.favouriteStickers;
                    }
                    if (arrayList == null) {
                        emptyCell.setHeight(1);
                        return;
                    } else if (!arrayList.isEmpty()) {
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(arrayList.size() / this.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                        if (height > 0) {
                            i3 = height;
                        }
                        emptyCell.setHeight(i3);
                        return;
                    } else {
                        emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                        return;
                    }
                }
                emptyCell.setHeight(AndroidUtilities.dp(82.0f));
                return;
            }
            int i5 = R.drawable.msg_close;
            if (itemViewType != 2) {
                if (itemViewType == 3) {
                    StickerSetGroupInfoCell stickerSetGroupInfoCell = (StickerSetGroupInfoCell) viewHolder.itemView;
                    if (i == this.totalItems - 1) {
                        z = true;
                    }
                    stickerSetGroupInfoCell.setIsLast(z);
                    return;
                } else if (itemViewType != 5) {
                    return;
                } else {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    if (MediaDataController.getInstance(EmojiView.this.currentAccount).loadFeaturedPremium) {
                        i2 = R.string.FeaturedStickersPremium;
                        str = "FeaturedStickersPremium";
                    } else {
                        i2 = R.string.FeaturedStickers;
                        str = "FeaturedStickers";
                    }
                    stickerSetNameCell.setText(LocaleController.getString(str, i2), R.drawable.msg_close);
                    return;
                }
            }
            StickerSetNameCell stickerSetNameCell2 = (StickerSetNameCell) viewHolder.itemView;
            if (i == EmojiView.this.groupStickerPackPosition) {
                if (EmojiView.this.groupStickersHidden && EmojiView.this.groupStickerSet == null) {
                    i5 = 0;
                } else if (EmojiView.this.groupStickerSet != null) {
                    i5 = R.drawable.msg_mini_customize;
                }
                if (EmojiView.this.info != null) {
                    tLRPC$Chat = MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Long.valueOf(EmojiView.this.info.id));
                }
                Object[] objArr = new Object[1];
                objArr[0] = tLRPC$Chat != null ? tLRPC$Chat.title : "Group Stickers";
                stickerSetNameCell2.setText(LocaleController.formatString("CurrentGroupStickers", R.string.CurrentGroupStickers, objArr), i5);
                return;
            }
            Object obj2 = this.cache.get(i);
            if (!(obj2 instanceof TLRPC$TL_messages_stickerSet)) {
                if (obj2 != EmojiView.this.recentStickers) {
                    if (obj2 != EmojiView.this.favouriteStickers) {
                        if (obj2 != EmojiView.this.premiumStickers) {
                            return;
                        }
                        stickerSetNameCell2.setText(LocaleController.getString("PremiumStickers", R.string.PremiumStickers), 0);
                        return;
                    }
                    stickerSetNameCell2.setText(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers), 0);
                    return;
                }
                stickerSetNameCell2.setText(LocaleController.getString("RecentStickers", R.string.RecentStickers), R.drawable.msg_close, LocaleController.getString((int) R.string.ClearRecentStickersAlertTitle));
                return;
            }
            TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$TL_messages_stickerSet) obj2).set;
            if (tLRPC$StickerSet == null) {
                return;
            }
            stickerSetNameCell2.setText(tLRPC$StickerSet.title, 0);
        }

        private void updateItems() {
            ArrayList arrayList;
            Object obj;
            ArrayList<TLRPC$Document> arrayList2;
            int i;
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            int i2 = 0;
            this.totalItems = 0;
            ArrayList arrayList3 = EmojiView.this.stickerSets;
            int i3 = -5;
            int i4 = -5;
            int i5 = 0;
            while (i4 < arrayList3.size()) {
                if (i4 == i3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                } else if (i4 == -4) {
                    MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
                    SharedPreferences emojiSettings = MessagesController.getEmojiSettings(EmojiView.this.currentAccount);
                    ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = mediaDataController.getFeaturedStickerSets();
                    if (!EmojiView.this.featuredStickerSets.isEmpty() && emojiSettings.getLong("featured_hidden", 0L) != featuredStickerSets.get(i2).set.id) {
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i7 = this.totalItems;
                        this.totalItems = i7 + 1;
                        sparseArray2.put(i7, "trend1");
                        SparseArray<Object> sparseArray3 = this.cache;
                        int i8 = this.totalItems;
                        this.totalItems = i8 + 1;
                        sparseArray3.put(i8, "trend2");
                        i5 += 2;
                    }
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
                    if (i4 == -3) {
                        arrayList2 = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        obj = "fav";
                    } else if (i4 == -2) {
                        arrayList2 = EmojiView.this.recentStickers;
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                        obj = "recent";
                    } else if (i4 == -1) {
                        arrayList2 = EmojiView.this.premiumStickers;
                        this.packStartPosition.put("premium", Integer.valueOf(this.totalItems));
                        obj = "premium";
                    } else {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) arrayList3.get(i4);
                        ArrayList<TLRPC$Document> arrayList4 = tLRPC$TL_messages_stickerSet2.documents;
                        this.packStartPosition.put(tLRPC$TL_messages_stickerSet2, Integer.valueOf(this.totalItems));
                        tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
                        arrayList2 = arrayList4;
                        obj = null;
                    }
                    if (i4 == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (arrayList2.isEmpty()) {
                            this.rowStartPack.put(i5, tLRPC$TL_messages_stickerSet);
                            int i9 = i5 + 1;
                            this.positionToRow.put(this.totalItems, i5);
                            this.rowStartPack.put(i9, tLRPC$TL_messages_stickerSet);
                            this.positionToRow.put(this.totalItems + 1, i9);
                            SparseArray<Object> sparseArray4 = this.cache;
                            int i10 = this.totalItems;
                            this.totalItems = i10 + 1;
                            sparseArray4.put(i10, tLRPC$TL_messages_stickerSet);
                            SparseArray<Object> sparseArray5 = this.cache;
                            int i11 = this.totalItems;
                            this.totalItems = i11 + 1;
                            sparseArray5.put(i11, "group");
                            arrayList = arrayList3;
                            i5 = i9 + 1;
                            i4++;
                            arrayList3 = arrayList;
                            i2 = 0;
                            i3 = -5;
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        int ceil = (int) Math.ceil(arrayList2.size() / this.stickersPerRow);
                        if (tLRPC$TL_messages_stickerSet != null) {
                            this.cache.put(this.totalItems, tLRPC$TL_messages_stickerSet);
                        } else {
                            this.cache.put(this.totalItems, arrayList2);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int i12 = 0;
                        while (i12 < arrayList2.size()) {
                            int i13 = i12 + 1;
                            int i14 = this.totalItems + i13;
                            this.cache.put(i14, arrayList2.get(i12));
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.cacheParents.put(i14, tLRPC$TL_messages_stickerSet);
                            } else {
                                this.cacheParents.put(i14, obj);
                            }
                            this.positionToRow.put(this.totalItems + i13, i5 + 1 + (i12 / this.stickersPerRow));
                            i12 = i13;
                            arrayList3 = arrayList3;
                        }
                        arrayList = arrayList3;
                        int i15 = 0;
                        while (true) {
                            i = ceil + 1;
                            if (i15 >= i) {
                                break;
                            }
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.rowStartPack.put(i5 + i15, tLRPC$TL_messages_stickerSet);
                            } else if (i4 == -1) {
                                this.rowStartPack.put(i5 + i15, "premium");
                            } else {
                                if (i4 == -2) {
                                    this.rowStartPack.put(i5 + i15, "recent");
                                } else {
                                    this.rowStartPack.put(i5 + i15, "fav");
                                }
                                i15++;
                            }
                            i15++;
                        }
                        this.totalItems += (ceil * this.stickersPerRow) + 1;
                        i5 += i;
                        i4++;
                        arrayList3 = arrayList;
                        i2 = 0;
                        i3 = -5;
                    }
                }
                arrayList = arrayList3;
                i4++;
                arrayList3 = arrayList;
                i2 = 0;
                i3 = -5;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            updateItems();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateItems();
            super.notifyDataSetChanged();
        }
    }

    /* loaded from: classes3.dex */
    public class PremiumButtonHolder extends FrameLayout {
        public PremiumButtonView buttonView;
        public int position;

        public PremiumButtonHolder(EmojiView emojiView, Context context) {
            super(context);
            PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
            this.buttonView = premiumButtonView;
            addView(premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 119, 5.0f, 5.0f, 5.0f, 5.0f));
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<TLRPC$TL_messages_stickerSet> emojipacks;
        private ArrayList<EmojiPack> emojipacksProcessed;
        private int itemCount;
        public int plainEmojisCount;
        private SparseIntArray positionToSection;
        private SparseIntArray positionToUnlock;
        private ArrayList<Pair<Integer, Integer>> premiumPackPositions;
        private SparseIntArray sectionToPosition;
        private Boolean wasPremium;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        private EmojiGridAdapter() {
            EmojiView.this = r1;
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
            this.positionToUnlock = new SparseIntArray();
        }

        /* loaded from: classes3.dex */
        public class EmojiPack {
            public ArrayList<TLRPC$Document> documents;
            public boolean free;
            public int index;
            public TLRPC$TL_messages_stickerSet stickerSet;

            private EmojiPack(EmojiGridAdapter emojiGridAdapter) {
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                view = new ImageViewEmoji(emojiView.getContext());
            } else if (i == 1) {
                view = new StickerSetNameCell(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
            } else if (i == 3) {
                EmojiView emojiView2 = EmojiView.this;
                view = new PremiumButtonHolder(emojiView2, emojiView2.getContext());
            } else {
                view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            Long l;
            String str;
            String str2;
            String str3;
            String str4;
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType != 0) {
                String str5 = "";
                if (itemViewType == 1) {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    stickerSetNameCell.position = i;
                    int i3 = this.positionToSection.get(i);
                    if (i3 >= EmojiView.this.emojiTitles.length) {
                        try {
                            str5 = this.emojipacksProcessed.get(i3 - EmojiView.this.emojiTitles.length).stickerSet.set.title;
                        } catch (Exception unused) {
                        }
                    } else {
                        str5 = EmojiView.this.emojiTitles[i3];
                    }
                    stickerSetNameCell.setText(str5, 0);
                    return;
                } else if (itemViewType != 3) {
                    return;
                } else {
                    PremiumButtonHolder premiumButtonHolder = (PremiumButtonHolder) viewHolder.itemView;
                    premiumButtonHolder.position = i;
                    PremiumButtonView premiumButtonView = premiumButtonHolder.buttonView;
                    int i4 = this.positionToUnlock.get(i);
                    if (i4 >= 0 && i4 < this.emojipacksProcessed.size()) {
                        try {
                            str5 = this.emojipacksProcessed.get(i4).stickerSet.set.title;
                        } catch (Exception unused2) {
                        }
                    }
                    premiumButtonView.setButton(LocaleController.formatString("UnlockPremiumEmojiPack", R.string.UnlockPremiumEmojiPack, str5), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.EmojiGridAdapter.this.lambda$onBindViewHolder$0(view);
                        }
                    });
                    return;
                }
            }
            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
            imageViewEmoji.position = i;
            if (EmojiView.this.needEmojiSearch) {
                i--;
            }
            int size = Emoji.recentEmoji.size();
            if (i < size) {
                String str6 = Emoji.recentEmoji.get(i);
                if (str6 != null && str6.startsWith("animated_")) {
                    try {
                        l = Long.valueOf(Long.parseLong(str6.substring(9)));
                        str2 = null;
                        str = null;
                    } catch (Exception unused3) {
                    }
                }
                str2 = str6;
                str = str2;
                l = null;
            } else {
                int i5 = 0;
                while (true) {
                    String[][] strArr = EmojiData.dataColored;
                    if (i5 >= strArr.length) {
                        str3 = null;
                        break;
                    }
                    int length = strArr[i5].length + 1 + size;
                    if (i < length) {
                        str3 = strArr[i5][(i - size) - 1];
                        String str7 = Emoji.emojiColor.get(str3);
                        if (str7 != null) {
                            String addColorToCode = EmojiView.addColorToCode(str3, str7);
                            str4 = str3;
                            str3 = addColorToCode;
                        }
                    } else {
                        i5++;
                        size = length;
                    }
                }
                str4 = str3;
                if (str4 == null && this.emojipacksProcessed != null) {
                    int i6 = 0;
                    while (true) {
                        if (i6 >= this.emojipacksProcessed.size()) {
                            break;
                        }
                        int size2 = this.emojipacksProcessed.get(i6).documents.size() + 1;
                        if (!this.emojipacksProcessed.get(i6).free && !UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                            size2++;
                        }
                        int i7 = size2 + size;
                        if (i >= i7 || (i2 = (i - size) - 1) < 0) {
                            i6++;
                            size = i7;
                        } else {
                            TLRPC$Document tLRPC$Document = this.emojipacksProcessed.get(i6).documents.get(i2);
                            if (tLRPC$Document != null) {
                                l = Long.valueOf(tLRPC$Document.id);
                            }
                        }
                    }
                    str2 = str3;
                    str = str4;
                    z = false;
                }
                l = null;
                str2 = str3;
                str = str4;
                z = false;
            }
            if (l != null) {
                imageViewEmoji.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
            } else {
                imageViewEmoji.setPadding(0, 0, 0, 0);
            }
            if (l != null) {
                imageViewEmoji.setImageDrawable(null, z);
                if (imageViewEmoji.getSpan() == null || imageViewEmoji.getSpan().getDocumentId() != l.longValue()) {
                    imageViewEmoji.setSpan(new AnimatedEmojiSpan(l.longValue(), (Paint.FontMetricsInt) null));
                }
            } else {
                imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str2), z);
                imageViewEmoji.setSpan(null);
            }
            imageViewEmoji.setTag(str);
            imageViewEmoji.setContentDescription(str2);
        }

        public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (!EmojiView.this.needEmojiSearch || i != 0) {
                if (this.positionToSection.indexOfKey(i) >= 0) {
                    return 1;
                }
                return this.positionToUnlock.indexOfKey(i) >= 0 ? 3 : 0;
            }
            return 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.sectionToPosition.clear();
            this.positionToUnlock.clear();
            this.itemCount = Emoji.recentEmoji.size() + (EmojiView.this.needEmojiSearch ? 1 : 0);
            this.emojipacks = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(5);
            ArrayList<EmojiPack> arrayList = this.emojipacksProcessed;
            if (arrayList != null) {
                arrayList.clear();
            } else {
                this.emojipacksProcessed = new ArrayList<>();
            }
            ArrayList<Pair<Integer, Integer>> arrayList2 = this.premiumPackPositions;
            if (arrayList2 != null) {
                arrayList2.clear();
            } else {
                this.premiumPackPositions = new ArrayList<>();
            }
            int i = 0;
            while (true) {
                ArrayList<TLRPC$TL_messages_stickerSet> arrayList3 = this.emojipacks;
                if (arrayList3 == null || i >= arrayList3.size()) {
                    break;
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.emojipacks.get(i);
                if (UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                    EmojiPack emojiPack = new EmojiPack();
                    emojiPack.index = i;
                    emojiPack.stickerSet = tLRPC$TL_messages_stickerSet;
                    emojiPack.documents = tLRPC$TL_messages_stickerSet.documents;
                    emojiPack.free = false;
                    this.emojipacksProcessed.add(emojiPack);
                } else {
                    ArrayList<TLRPC$Document> arrayList4 = new ArrayList<>();
                    ArrayList<TLRPC$Document> arrayList5 = new ArrayList<>();
                    if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                        for (int i2 = 0; i2 < tLRPC$TL_messages_stickerSet.documents.size(); i2++) {
                            if (MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i2))) {
                                arrayList4.add(tLRPC$TL_messages_stickerSet.documents.get(i2));
                            } else {
                                arrayList5.add(tLRPC$TL_messages_stickerSet.documents.get(i2));
                            }
                        }
                    }
                    if (arrayList4.size() > 0) {
                        EmojiPack emojiPack2 = new EmojiPack();
                        emojiPack2.index = i;
                        emojiPack2.stickerSet = tLRPC$TL_messages_stickerSet;
                        emojiPack2.documents = arrayList4;
                        emojiPack2.free = true;
                        this.emojipacksProcessed.add(emojiPack2);
                    }
                    if (arrayList5.size() > 0) {
                        EmojiPack emojiPack3 = new EmojiPack();
                        emojiPack3.index = i;
                        emojiPack3.stickerSet = tLRPC$TL_messages_stickerSet;
                        emojiPack3.documents = arrayList5;
                        emojiPack3.free = false;
                        this.emojipacksProcessed.add(emojiPack3);
                    }
                }
                i++;
            }
            int i3 = 0;
            while (true) {
                String[][] strArr = EmojiData.dataColored;
                if (i3 >= strArr.length) {
                    break;
                }
                this.positionToSection.put(this.itemCount, i3);
                this.sectionToPosition.put(i3, this.itemCount);
                this.itemCount += strArr[i3].length + 1;
                i3++;
            }
            boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
            this.plainEmojisCount = this.itemCount;
            if (this.emojipacks != null) {
                for (int i4 = 0; i4 < this.emojipacksProcessed.size(); i4++) {
                    SparseIntArray sparseIntArray = this.positionToSection;
                    int i5 = this.itemCount;
                    String[][] strArr2 = EmojiData.dataColored;
                    sparseIntArray.put(i5, strArr2.length + i4);
                    this.sectionToPosition.put(strArr2.length + i4, this.itemCount);
                    int size = this.emojipacksProcessed.get(i4).documents.size() + 1;
                    if (!this.emojipacksProcessed.get(i4).free) {
                        if (!isPremium) {
                            size++;
                            this.positionToUnlock.put((this.itemCount + size) - 1, i4);
                        }
                        Boolean bool = this.wasPremium;
                        if (bool == null || isPremium != bool.booleanValue()) {
                            if (isPremium) {
                                notifyItemRemoved((this.itemCount + size) - 1);
                            } else {
                                notifyItemInserted((this.itemCount + size) - 1);
                            }
                        }
                        this.premiumPackPositions.add(new Pair<>(Integer.valueOf(this.itemCount), Integer.valueOf((this.itemCount + size) - 1)));
                    }
                    this.itemCount += size;
                }
            }
            this.wasPremium = Boolean.valueOf(isPremium);
            EmojiView.this.updateEmojiTabs();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.EmojiGridAdapter.this.lambda$notifyDataSetChanged$1();
                }
            });
        }

        public /* synthetic */ void lambda$notifyDataSetChanged$1() {
            super.notifyDataSetChanged();
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiSearchAdapter extends RecyclerListView.SelectionAdapter {
        private String lastSearchAlias;
        private String lastSearchEmojiString;
        private ArrayList<MediaDataController.KeywordResult> result;
        private Runnable searchRunnable;
        private boolean searchWas;

        private EmojiSearchAdapter() {
            EmojiView.this = r1;
            this.result = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int size;
            if (this.result.isEmpty() && !this.searchWas) {
                size = Emoji.recentEmoji.size();
            } else if (this.result.isEmpty()) {
                return 2;
            } else {
                size = this.result.size();
            }
            return size + 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                frameLayout = new ImageViewEmoji(emojiView.getContext());
            } else if (i == 1) {
                View view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                frameLayout = view;
            } else {
                FrameLayout frameLayout2 = new FrameLayout(EmojiView.this.getContext()) { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.1
                    {
                        EmojiSearchAdapter.this = this;
                    }

                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int i2, int i3) {
                        int i4;
                        View view2 = (View) EmojiView.this.getParent();
                        if (view2 != null) {
                            i4 = (int) (view2.getMeasuredHeight() - EmojiView.this.getY());
                        } else {
                            i4 = AndroidUtilities.dp(120.0f);
                        }
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(i4 - EmojiView.this.searchFieldHeight, 1073741824));
                    }
                };
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString("NoEmojiFound", R.string.NoEmojiFound));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.msg_emoji_question);
                imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                frameLayout2.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2
                    {
                        EmojiSearchAdapter.this = this;
                    }

                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        Object obj;
                        boolean[] zArr = new boolean[1];
                        BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                        LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                        linearLayout.setOrientation(1);
                        linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        ImageView imageView2 = new ImageView(EmojiView.this.getContext());
                        imageView2.setImageResource(R.drawable.smiles_info);
                        linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                        TextView textView2 = new TextView(EmojiView.this.getContext());
                        textView2.setText(LocaleController.getString("EmojiSuggestions", R.string.EmojiSuggestions));
                        textView2.setTextSize(1, 15.0f);
                        textView2.setTextColor(EmojiView.this.getThemedColor("dialogTextBlue2"));
                        int i2 = 5;
                        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                        TextView textView3 = new TextView(EmojiView.this.getContext());
                        textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", R.string.EmojiSuggestionsInfo)));
                        textView3.setTextSize(1, 15.0f);
                        textView3.setTextColor(EmojiView.this.getThemedColor("dialogTextBlack"));
                        textView3.setGravity(LocaleController.isRTL ? 5 : 3);
                        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                        TextView textView4 = new TextView(EmojiView.this.getContext());
                        Object[] objArr = new Object[1];
                        if (EmojiSearchAdapter.this.lastSearchAlias == null) {
                            obj = EmojiView.this.lastSearchKeyboardLanguage;
                        } else {
                            obj = EmojiSearchAdapter.this.lastSearchAlias;
                        }
                        objArr[0] = obj;
                        textView4.setText(LocaleController.formatString("EmojiSuggestionsUrl", R.string.EmojiSuggestionsUrl, objArr));
                        textView4.setTextSize(1, 15.0f);
                        textView4.setTextColor(EmojiView.this.getThemedColor("dialogTextLink"));
                        if (!LocaleController.isRTL) {
                            i2 = 3;
                        }
                        textView4.setGravity(i2);
                        linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                        textView4.setOnClickListener(new AnonymousClass1(zArr, builder));
                        builder.setCustomView(linearLayout);
                        builder.show();
                    }

                    /* renamed from: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 */
                    /* loaded from: classes3.dex */
                    public class AnonymousClass1 implements View.OnClickListener {
                        final /* synthetic */ BottomSheet.Builder val$builder;
                        final /* synthetic */ boolean[] val$loadingUrl;

                        AnonymousClass1(boolean[] zArr, BottomSheet.Builder builder) {
                            AnonymousClass2.this = r1;
                            this.val$loadingUrl = zArr;
                            this.val$builder = builder;
                        }

                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            String str;
                            boolean[] zArr = this.val$loadingUrl;
                            if (zArr[0]) {
                                return;
                            }
                            zArr[0] = true;
                            final AlertDialog[] alertDialogArr = {new AlertDialog(EmojiView.this.getContext(), 3)};
                            TLRPC$TL_messages_getEmojiURL tLRPC$TL_messages_getEmojiURL = new TLRPC$TL_messages_getEmojiURL();
                            if (EmojiSearchAdapter.this.lastSearchAlias == null) {
                                str = EmojiView.this.lastSearchKeyboardLanguage[0];
                            } else {
                                str = EmojiSearchAdapter.this.lastSearchAlias;
                            }
                            tLRPC$TL_messages_getEmojiURL.lang_code = str;
                            ConnectionsManager connectionsManager = ConnectionsManager.getInstance(EmojiView.this.currentAccount);
                            final BottomSheet.Builder builder = this.val$builder;
                            final int sendRequest = connectionsManager.sendRequest(tLRPC$TL_messages_getEmojiURL, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$1(alertDialogArr, builder, tLObject, tLRPC$TL_error);
                                }
                            });
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$3(alertDialogArr, sendRequest);
                                }
                            }, 1000L);
                        }

                        public /* synthetic */ void lambda$onClick$1(final AlertDialog[] alertDialogArr, final BottomSheet.Builder builder, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$0(alertDialogArr, tLObject, builder);
                                }
                            });
                        }

                        public /* synthetic */ void lambda$onClick$0(AlertDialog[] alertDialogArr, TLObject tLObject, BottomSheet.Builder builder) {
                            try {
                                alertDialogArr[0].dismiss();
                            } catch (Throwable unused) {
                            }
                            alertDialogArr[0] = null;
                            if (tLObject instanceof TLRPC$TL_emojiURL) {
                                Browser.openUrl(EmojiView.this.getContext(), ((TLRPC$TL_emojiURL) tLObject).url);
                                builder.getDismissRunnable().run();
                            }
                        }

                        public /* synthetic */ void lambda$onClick$3(AlertDialog[] alertDialogArr, final int i) {
                            if (alertDialogArr[0] == null) {
                                return;
                            }
                            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda0
                                @Override // android.content.DialogInterface.OnCancelListener
                                public final void onCancel(DialogInterface dialogInterface) {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$2(i, dialogInterface);
                                }
                            });
                            alertDialogArr[0].show();
                        }

                        public /* synthetic */ void lambda$onClick$2(int i, DialogInterface dialogInterface) {
                            ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                        }
                    }
                });
                frameLayout2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                frameLayout = frameLayout2;
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            boolean z;
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
            int i2 = i - 1;
            if (this.result.isEmpty() && !this.searchWas) {
                str = Emoji.recentEmoji.get(i2);
                z = true;
            } else {
                str = this.result.get(i2).emoji;
                z = false;
            }
            imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str), z);
            imageViewEmoji.setTag(str);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            return (i != 1 || !this.searchWas || !this.result.isEmpty()) ? 0 : 2;
        }

        public void search(String str) {
            if (TextUtils.isEmpty(str)) {
                this.lastSearchEmojiString = null;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                    this.searchWas = false;
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchEmojiString = str.toLowerCase();
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (!TextUtils.isEmpty(this.lastSearchEmojiString)) {
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.3
                    {
                        EmojiSearchAdapter.this = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                        final String str2 = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.3.1
                            {
                                AnonymousClass3.this = this;
                            }

                            @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
                            public void run(ArrayList<MediaDataController.KeywordResult> arrayList, String str3) {
                                if (str2.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    EmojiSearchAdapter.this.lastSearchAlias = str3;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    EmojiSearchAdapter.this.result = arrayList;
                                    EmojiSearchAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private EmojiPagesAdapter() {
            EmojiView.this = r1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) EmojiView.this.views.get(i));
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public boolean canScrollToTab(int i) {
            boolean z = true;
            if ((i == 1 || i == 2) && EmojiView.this.currentChatId != 0) {
                EmojiView emojiView = EmojiView.this;
                if (i != 1) {
                    z = false;
                }
                emojiView.showStickerBanHint(z);
                return false;
            }
            return true;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return EmojiView.this.views.size();
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public Drawable getPageIconDrawable(int i) {
            return EmojiView.this.tabIcons[i];
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            if (i != 0) {
                if (i == 1) {
                    return LocaleController.getString("AccDescrGIFs", R.string.AccDescrGIFs);
                }
                if (i == 2) {
                    return LocaleController.getString("AccDescrStickers", R.string.AccDescrStickers);
                }
                return null;
            }
            return LocaleController.getString("Emoji", R.string.Emoji);
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public void customOnDraw(Canvas canvas, int i) {
            if (i != 2 || MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() || EmojiView.this.dotPaint == null) {
                return;
            }
            canvas.drawCircle((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f), (canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = (View) EmojiView.this.views.get(i);
            viewGroup.addView(view);
            return view;
        }
    }

    /* loaded from: classes3.dex */
    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC$User bot;
        private final Context context;
        private int firstResultItem;
        private int itemsCount;
        private String lastSearchImageString;
        private boolean lastSearchIsEmoji;
        private final int maxRecentRowsCount;
        private String nextSearchOffset;
        private final GifProgressEmptyView progressEmptyView;
        private int recentItemsCount;
        private int reqId;
        private ArrayList<TLRPC$BotInlineResult> results;
        private HashMap<String, TLRPC$BotInlineResult> resultsMap;
        private boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private boolean showTrendingWhenSearchEmpty;
        private int trendingSectionItem;
        private final boolean withRecent;

        public GifAdapter(EmojiView emojiView, Context context) {
            this(context, false, 0);
        }

        public GifAdapter(EmojiView emojiView, Context context, boolean z) {
            this(context, z, z ? ConnectionsManager.DEFAULT_DATACENTER_ID : 0);
        }

        public GifAdapter(Context context, boolean z, int i) {
            EmojiView.this = r2;
            this.results = new ArrayList<>();
            this.resultsMap = new HashMap<>();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context;
            this.withRecent = z;
            this.maxRecentRowsCount = i;
            this.progressEmptyView = z ? null : new GifProgressEmptyView(context);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            boolean z = this.withRecent;
            if (z && i == this.trendingSectionItem) {
                return 2;
            }
            return (z || !this.results.isEmpty()) ? 0 : 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerSetNameCell stickerSetNameCell;
            if (i == 0) {
                ContextLinkCell contextLinkCell = new ContextLinkCell(this.context);
                contextLinkCell.setCanPreviewGif(true);
                stickerSetNameCell = contextLinkCell;
            } else if (i == 1) {
                View view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                stickerSetNameCell = view;
            } else if (i == 2) {
                StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                stickerSetNameCell2.setText(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs), 0);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(2.5f);
                ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = AndroidUtilities.dp(5.5f);
                stickerSetNameCell2.setLayoutParams(layoutParams);
                stickerSetNameCell = stickerSetNameCell2;
            } else {
                GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
                gifProgressEmptyView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                stickerSetNameCell = gifProgressEmptyView;
            }
            return new RecyclerListView.Holder(stickerSetNameCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
            int i2 = this.firstResultItem;
            if (i2 < 0 || i < i2) {
                contextLinkCell.setGif((TLRPC$Document) EmojiView.this.recentGifs.get(i - 1), false);
            } else {
                contextLinkCell.setLink(this.results.get(i - i2), this.bot, true, false, false, true);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRecentItemsCount();
            updateItems();
            super.notifyDataSetChanged();
        }

        private void updateItems() {
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.itemsCount = 1;
            if (this.withRecent) {
                this.itemsCount = this.recentItemsCount + 1;
            }
            if (!this.results.isEmpty()) {
                if (this.withRecent && this.recentItemsCount > 0) {
                    int i = this.itemsCount;
                    this.itemsCount = i + 1;
                    this.trendingSectionItem = i;
                }
                int i2 = this.itemsCount;
                this.firstResultItem = i2;
                this.itemsCount = i2 + this.results.size();
            } else if (this.withRecent) {
            } else {
                this.itemsCount++;
            }
        }

        private void updateRecentItemsCount() {
            int i;
            if (!this.withRecent || (i = this.maxRecentRowsCount) == 0) {
                return;
            }
            if (i == Integer.MAX_VALUE) {
                this.recentItemsCount = EmojiView.this.recentGifs.size();
            } else if (EmojiView.this.gifGridView.getMeasuredWidth() != 0) {
                int measuredWidth = EmojiView.this.gifGridView.getMeasuredWidth();
                int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
                int dp = AndroidUtilities.dp(100.0f);
                this.recentItemsCount = 0;
                int size = EmojiView.this.recentGifs.size();
                int i2 = spanCount;
                int i3 = 0;
                int i4 = 0;
                for (int i5 = 0; i5 < size; i5++) {
                    Size fixSize = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC$Document) EmojiView.this.recentGifs.get(i5)));
                    int min = Math.min(spanCount, (int) Math.floor(spanCount * (((fixSize.width / fixSize.height) * dp) / measuredWidth)));
                    if (i2 < min) {
                        this.recentItemsCount += i3;
                        i4++;
                        if (i4 == this.maxRecentRowsCount) {
                            break;
                        }
                        i2 = spanCount;
                        i3 = 0;
                    }
                    i3++;
                    i2 -= min;
                }
                if (i4 >= this.maxRecentRowsCount) {
                    return;
                }
                this.recentItemsCount += i3;
            }
        }

        public void loadTrendingGifs() {
            search("", "", true, true, true);
        }

        private void searchBotUser() {
            if (this.searchingUser) {
                return;
            }
            this.searchingUser = true;
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            tLRPC$TL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    EmojiView.GifAdapter.this.lambda$searchBotUser$1(tLObject, tLRPC$TL_error);
                }
            });
        }

        public /* synthetic */ void lambda$searchBotUser$1(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.GifAdapter.this.lambda$searchBotUser$0(tLObject);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$searchBotUser$0(TLObject tLObject) {
            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        public void search(final String str) {
            if (this.withRecent) {
                return;
            }
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchIsEmoji = false;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(false);
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (TextUtils.isEmpty(str)) {
                this.lastSearchImageString = null;
                if (!this.showTrendingWhenSearchEmpty) {
                    int currentPosition = EmojiView.this.gifTabs.getCurrentPosition();
                    if (currentPosition == EmojiView.this.gifRecentTabNum || currentPosition == EmojiView.this.gifTrendingTabNum) {
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                            return;
                        }
                        EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                        return;
                    }
                    searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(currentPosition - EmojiView.this.gifFirstEmojiTabNum));
                    return;
                }
                loadTrendingGifs();
                return;
            }
            String lowerCase = str.toLowerCase();
            this.lastSearchImageString = lowerCase;
            if (TextUtils.isEmpty(lowerCase)) {
                return;
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.EmojiView.GifAdapter.1
                {
                    GifAdapter.this = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    GifAdapter.this.search(str, "", true);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }

        public void searchEmoji(String str) {
            if (this.lastSearchIsEmoji && TextUtils.equals(this.lastSearchImageString, str)) {
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
            } else {
                search(str, "", true, true, true);
            }
        }

        protected void search(String str, String str2, boolean z) {
            search(str, str2, z, false, false);
        }

        protected void search(final String str, final String str2, final boolean z, final boolean z2, final boolean z3) {
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchImageString = str;
            this.lastSearchIsEmoji = z2;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(z2);
            }
            TLObject userOrChat = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (!(userOrChat instanceof TLRPC$User)) {
                if (!z) {
                    return;
                }
                searchBotUser();
                if (this.withRecent) {
                    return;
                }
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                return;
            }
            if (!this.withRecent && TextUtils.isEmpty(str2)) {
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
            }
            this.bot = (TLRPC$User) userOrChat;
            final String str3 = "gif_search_" + str + "_" + str2;
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    EmojiView.GifAdapter.this.lambda$search$3(str, str2, z, z2, z3, str3, tLObject, tLRPC$TL_error);
                }
            };
            if (!z3 && !this.withRecent && z2 && TextUtils.isEmpty(str2)) {
                this.results.clear();
                this.resultsMap.clear();
                if (EmojiView.this.gifGridView.getAdapter() != this) {
                    EmojiView.this.gifGridView.setAdapter(this);
                }
                notifyDataSetChanged();
                EmojiView.this.scrollGifsToTop();
            }
            if (!z3 || !EmojiView.this.gifCache.containsKey(str3)) {
                if (EmojiView.this.gifSearchPreloader.isLoading(str3)) {
                    return;
                }
                if (z3) {
                    this.reqId = -1;
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, requestDelegate);
                    return;
                }
                TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                if (str == null) {
                    str = "";
                }
                tLRPC$TL_messages_getInlineBotResults.query = str;
                tLRPC$TL_messages_getInlineBotResults.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                tLRPC$TL_messages_getInlineBotResults.offset = str2;
                tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, requestDelegate, 2);
                return;
            }
            lambda$search$2(str, str2, z, z2, true, str3, (TLObject) EmojiView.this.gifCache.get(str3));
        }

        public /* synthetic */ void lambda$search$3(final String str, final String str2, final boolean z, final boolean z2, final boolean z3, final String str3, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifAdapter.this.lambda$search$2(str, str2, z, z2, z3, str3, tLObject);
                }
            });
        }

        /* renamed from: processResponse */
        public void lambda$search$2(String str, String str2, boolean z, boolean z2, boolean z3, String str3, TLObject tLObject) {
            if (str == null || !str.equals(this.lastSearchImageString)) {
                return;
            }
            boolean z4 = false;
            this.reqId = 0;
            if (z3 && (!(tLObject instanceof TLRPC$messages_BotResults) || ((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                search(str, str2, z, z2, false);
                return;
            }
            if (!this.withRecent && TextUtils.isEmpty(str2)) {
                this.results.clear();
                this.resultsMap.clear();
                EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
            }
            if (tLObject instanceof TLRPC$messages_BotResults) {
                int size = this.results.size();
                TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
                if (!EmojiView.this.gifCache.containsKey(str3)) {
                    EmojiView.this.gifCache.put(str3, tLRPC$messages_BotResults);
                }
                if (!z3 && tLRPC$messages_BotResults.cache_time != 0) {
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).saveBotCache(str3, tLRPC$messages_BotResults);
                }
                this.nextSearchOffset = tLRPC$messages_BotResults.next_offset;
                int i = 0;
                for (int i2 = 0; i2 < tLRPC$messages_BotResults.results.size(); i2++) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(i2);
                    if (!this.resultsMap.containsKey(tLRPC$BotInlineResult.id)) {
                        tLRPC$BotInlineResult.query_id = tLRPC$messages_BotResults.query_id;
                        this.results.add(tLRPC$BotInlineResult);
                        this.resultsMap.put(tLRPC$BotInlineResult.id, tLRPC$BotInlineResult);
                        i++;
                    }
                }
                if (size == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                    z4 = true;
                }
                this.searchEndReached = z4;
                if (i != 0) {
                    if (!z2 || size != 0) {
                        updateItems();
                        if (!this.withRecent) {
                            if (size != 0) {
                                notifyItemChanged(size);
                            }
                            notifyItemRangeInserted(size + 1, i);
                        } else if (size != 0) {
                            notifyItemChanged(this.recentItemsCount + 1 + size);
                            notifyItemRangeInserted(this.recentItemsCount + 1 + size + 1, i);
                        } else {
                            notifyItemRangeInserted(this.recentItemsCount + 1, i + 1);
                        }
                    } else {
                        notifyDataSetChanged();
                    }
                } else if (this.results.isEmpty()) {
                    notifyDataSetChanged();
                }
            } else {
                notifyDataSetChanged();
            }
            if (this.withRecent) {
                return;
            }
            if (EmojiView.this.gifGridView.getAdapter() != this) {
                EmojiView.this.gifGridView.setAdapter(this);
            }
            if (!z2 || TextUtils.isEmpty(str) || !TextUtils.isEmpty(str2)) {
                return;
            }
            EmojiView.this.scrollGifsToTop();
        }
    }

    /* loaded from: classes3.dex */
    public class GifSearchPreloader {
        private final List<String> loadingKeys;

        private GifSearchPreloader() {
            EmojiView.this = r1;
            this.loadingKeys = new ArrayList();
        }

        public boolean isLoading(String str) {
            return this.loadingKeys.contains(str);
        }

        public void preload(String str) {
            preload(str, "", true);
        }

        private void preload(final String str, final String str2, final boolean z) {
            final String str3 = "gif_search_" + str + "_" + str2;
            if (!z || !EmojiView.this.gifCache.containsKey(str3)) {
                RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        EmojiView.GifSearchPreloader.this.lambda$preload$1(str, str2, z, str3, tLObject, tLRPC$TL_error);
                    }
                };
                if (!z) {
                    MessagesController messagesController = MessagesController.getInstance(EmojiView.this.currentAccount);
                    TLObject userOrChat = messagesController.getUserOrChat(messagesController.gifSearchBot);
                    if (!(userOrChat instanceof TLRPC$User)) {
                        return;
                    }
                    this.loadingKeys.add(str3);
                    TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                    if (str == null) {
                        str = "";
                    }
                    tLRPC$TL_messages_getInlineBotResults.query = str;
                    tLRPC$TL_messages_getInlineBotResults.bot = messagesController.getInputUser((TLRPC$User) userOrChat);
                    tLRPC$TL_messages_getInlineBotResults.offset = str2;
                    tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, requestDelegate, 2);
                    return;
                }
                this.loadingKeys.add(str3);
                MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, requestDelegate);
            }
        }

        public /* synthetic */ void lambda$preload$1(final String str, final String str2, final boolean z, final String str3, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifSearchPreloader.this.lambda$preload$0(str, str2, z, str3, tLObject);
                }
            });
        }

        /* renamed from: processResponse */
        public void lambda$preload$0(String str, String str2, boolean z, String str3, TLObject tLObject) {
            this.loadingKeys.remove(str3);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(str)) {
                EmojiView.this.gifSearchAdapter.lambda$search$2(str, str2, false, true, z, str3, tLObject);
            } else if (z && (!(tLObject instanceof TLRPC$messages_BotResults) || ((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                preload(str, str2, false);
            } else if (!(tLObject instanceof TLRPC$messages_BotResults) || EmojiView.this.gifCache.containsKey(str3)) {
            } else {
                EmojiView.this.gifCache.put(str3, (TLRPC$messages_BotResults) tLObject);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size = new Size();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GifLayoutManager(Context context) {
            super(context, 100, true);
            EmojiView.this = r3;
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(r3) { // from class: org.telegram.ui.Components.EmojiView.GifLayoutManager.1
                {
                    GifLayoutManager.this = this;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i) {
                    if (i == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    return GifLayoutManager.this.getSpanSizeForItem(i - 1);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v10, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v11, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v19, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v20, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v21, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v9, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        protected Size getSizeForItem(int i) {
            List<TLRPC$DocumentAttribute> list;
            TLRPC$Document tLRPC$Document;
            TLRPC$Document tLRPC$Document2;
            TLRPC$Document tLRPC$Document3 = null;
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                if (i > EmojiView.this.gifAdapter.recentItemsCount) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) EmojiView.this.gifAdapter.results.get((i - EmojiView.this.gifAdapter.recentItemsCount) - 1);
                    tLRPC$Document = tLRPC$BotInlineResult.document;
                    if (tLRPC$Document != null) {
                        tLRPC$Document2 = tLRPC$Document.attributes;
                    } else {
                        TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.content;
                        if (tLRPC$WebDocument != null) {
                            tLRPC$Document2 = tLRPC$WebDocument.attributes;
                        } else {
                            TLRPC$WebDocument tLRPC$WebDocument2 = tLRPC$BotInlineResult.thumb;
                            if (tLRPC$WebDocument2 != null) {
                                tLRPC$Document2 = tLRPC$WebDocument2.attributes;
                            }
                            list = tLRPC$Document3;
                            tLRPC$Document3 = tLRPC$Document;
                            return getSizeForItem(tLRPC$Document3, list);
                        }
                    }
                    tLRPC$Document3 = tLRPC$Document2;
                    list = tLRPC$Document3;
                    tLRPC$Document3 = tLRPC$Document;
                    return getSizeForItem(tLRPC$Document3, list);
                } else if (i == EmojiView.this.gifAdapter.recentItemsCount) {
                    return null;
                } else {
                    tLRPC$Document3 = (TLRPC$Document) EmojiView.this.recentGifs.get(i);
                    list = tLRPC$Document3.attributes;
                    return getSizeForItem(tLRPC$Document3, list);
                }
            } else if (!EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                TLRPC$BotInlineResult tLRPC$BotInlineResult2 = (TLRPC$BotInlineResult) EmojiView.this.gifSearchAdapter.results.get(i);
                tLRPC$Document = tLRPC$BotInlineResult2.document;
                if (tLRPC$Document != null) {
                    tLRPC$Document2 = tLRPC$Document.attributes;
                } else {
                    TLRPC$WebDocument tLRPC$WebDocument3 = tLRPC$BotInlineResult2.content;
                    if (tLRPC$WebDocument3 != null) {
                        tLRPC$Document2 = tLRPC$WebDocument3.attributes;
                    } else {
                        TLRPC$WebDocument tLRPC$WebDocument4 = tLRPC$BotInlineResult2.thumb;
                        if (tLRPC$WebDocument4 != null) {
                            tLRPC$Document2 = tLRPC$WebDocument4.attributes;
                        }
                        list = tLRPC$Document3;
                        tLRPC$Document3 = tLRPC$Document;
                        return getSizeForItem(tLRPC$Document3, list);
                    }
                }
                tLRPC$Document3 = tLRPC$Document2;
                list = tLRPC$Document3;
                tLRPC$Document3 = tLRPC$Document;
                return getSizeForItem(tLRPC$Document3, list);
            } else {
                list = null;
                return getSizeForItem(tLRPC$Document3, list);
            }
        }

        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        public int getFlowItemCount() {
            if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter || !EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                return getItemCount() - 1;
            }
            return 0;
        }

        public Size getSizeForItem(TLRPC$Document tLRPC$Document) {
            return getSizeForItem(tLRPC$Document, tLRPC$Document.attributes);
        }

        public Size getSizeForItem(TLRPC$Document tLRPC$Document, List<TLRPC$DocumentAttribute> list) {
            TLRPC$PhotoSize closestPhotoSizeWithSize;
            int i;
            int i2;
            Size size = this.size;
            size.height = 100.0f;
            size.width = 100.0f;
            if (tLRPC$Document != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90)) != null && (i = closestPhotoSizeWithSize.w) != 0 && (i2 = closestPhotoSizeWithSize.h) != 0) {
                Size size2 = this.size;
                size2.width = i;
                size2.height = i2;
            }
            if (list != null) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = list.get(i3);
                    if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                        Size size3 = this.size;
                        size3.width = tLRPC$DocumentAttribute.w;
                        size3.height = tLRPC$DocumentAttribute.h;
                        break;
                    }
                }
            }
            return this.size;
        }
    }

    /* loaded from: classes3.dex */
    public class GifProgressEmptyView extends FrameLayout {
        private final ImageView imageView;
        private boolean loadingState;
        private final RadialProgressView progressView;
        private final TextView textView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GifProgressEmptyView(Context context) {
            super(context);
            EmojiView.this = r13;
            ImageView imageView = new ImageView(getContext());
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.gif_empty);
            imageView.setColorFilter(new PorterDuffColorFilter(r13.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setText(LocaleController.getString("NoGIFsFound", R.string.NoGIFsFound));
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(r13.getThemedColor("chat_emojiPanelEmptyText"));
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context, r13.resourcesProvider);
            this.progressView = radialProgressView;
            radialProgressView.setVisibility(8);
            radialProgressView.setProgressColor(r13.getThemedColor("progressCircle"));
            addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int i3;
            int measuredHeight = EmojiView.this.gifGridView.getMeasuredHeight();
            if (!this.loadingState) {
                i3 = (int) ((((measuredHeight - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f);
            } else {
                i3 = measuredHeight - AndroidUtilities.dp(80.0f);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3, 1073741824));
        }

        public void setLoadingState(boolean z) {
            if (this.loadingState != z) {
                this.loadingState = z;
                int i = 8;
                this.imageView.setVisibility(z ? 8 : 0);
                this.textView.setVisibility(z ? 8 : 0);
                RadialProgressView radialProgressView = this.progressView;
                if (z) {
                    i = 0;
                }
                radialProgressView.setVisibility(i);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        boolean cleared;
        private Context context;
        private int emojiSearchId;
        private int reqId;
        private int reqId2;
        private String searchQuery;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private ArrayList<TLRPC$StickerSetCovered> serverPacks = new ArrayList<>();
        private ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
        private HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        private HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
        private ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
        private SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
        private Runnable searchRunnable = new AnonymousClass1();

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        static /* synthetic */ int access$17904(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
                StickersSearchGridAdapter.this = r1;
            }

            public void clear() {
                StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                if (stickersSearchGridAdapter.cleared) {
                    return;
                }
                stickersSearchGridAdapter.cleared = true;
                stickersSearchGridAdapter.emojiStickers.clear();
                StickersSearchGridAdapter.this.emojiArrays.clear();
                StickersSearchGridAdapter.this.localPacks.clear();
                StickersSearchGridAdapter.this.serverPacks.clear();
                StickersSearchGridAdapter.this.localPacksByShortName.clear();
                StickersSearchGridAdapter.this.localPacksByName.clear();
            }

            /* JADX WARN: Code restructure failed: missing block: B:16:0x007b, code lost:
                if (r5.charAt(r9) <= 57343) goto L23;
             */
            /* JADX WARN: Code restructure failed: missing block: B:22:0x0095, code lost:
                if (r5.charAt(r9) != 9794) goto L24;
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 863
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.run():void");
            }

            public /* synthetic */ void lambda$run$1(final TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject instanceof TLRPC$TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$run$0(tLRPC$TL_messages_searchStickerSets, tLObject);
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$run$0(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject) {
                if (tLRPC$TL_messages_searchStickerSets.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC$TL_messages_foundStickerSets) tLObject).sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            public /* synthetic */ void lambda$run$3(final TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, final ArrayList arrayList, final LongSparseArray longSparseArray, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$run$2(tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray);
                    }
                });
            }

            public /* synthetic */ void lambda$run$2(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickersSearchGridAdapter.this.reqId2 = 0;
                    if (!(tLObject instanceof TLRPC$TL_messages_stickers)) {
                        return;
                    }
                    TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
                    int size = arrayList.size();
                    int size2 = tLRPC$TL_messages_stickers.stickers.size();
                    for (int i = 0; i < size2; i++) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickers.stickers.get(i);
                        if (longSparseArray.indexOfKey(tLRPC$Document.id) < 0) {
                            arrayList.add(tLRPC$Document);
                        }
                    }
                    if (size == arrayList.size()) {
                        return;
                    }
                    StickersSearchGridAdapter.this.emojiStickers.put(arrayList, StickersSearchGridAdapter.this.searchQuery);
                    if (size == 0) {
                        StickersSearchGridAdapter.this.emojiArrays.add(arrayList);
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }
        }

        public StickersSearchGridAdapter(Context context) {
            EmojiView.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public void search(String str) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.serverPacks.clear();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.searchQuery = str.toLowerCase();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            if (i == 1 && this.totalItems == 1) {
                return 5;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            return obj instanceof TLRPC$StickerSetCovered ? 3 : 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                return;
            }
            if (featuredStickerSetInfoCell.isInstalled()) {
                EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                return;
            }
            featuredStickerSetInfoCell.setAddDrawProgress(true, true);
            EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
            EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r15v10, types: [android.widget.FrameLayout, android.view.View, org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3] */
        /* JADX WARN: Type inference failed for: r15v2 */
        /* JADX WARN: Type inference failed for: r15v3, types: [org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell;
            ?? r15;
            if (i == 0) {
                r15 = new StickerEmojiCell(this, this.context, true) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.2
                    @Override // android.widget.FrameLayout, android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                    }
                };
            } else {
                if (i == 1) {
                    featuredStickerSetInfoCell = new EmptyCell(this.context);
                } else if (i == 2) {
                    featuredStickerSetInfoCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                } else if (i == 3) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell2 = new FeaturedStickerSetInfoCell(this.context, 17, false, true, EmojiView.this.resourcesProvider);
                    featuredStickerSetInfoCell2.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.StickersSearchGridAdapter.this.lambda$onCreateViewHolder$0(view);
                        }
                    });
                    featuredStickerSetInfoCell = featuredStickerSetInfoCell2;
                } else if (i == 4) {
                    View view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    featuredStickerSetInfoCell = view;
                } else if (i != 5) {
                    featuredStickerSetInfoCell = null;
                } else {
                    r15 = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.3
                        {
                            StickersSearchGridAdapter.this = this;
                        }

                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec((int) ((((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f), 1073741824));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                    r15.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                    r15.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    r15.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                }
                return new RecyclerListView.Holder(featuredStickerSetInfoCell);
            }
            featuredStickerSetInfoCell = r15;
            return new RecyclerListView.Holder(featuredStickerSetInfoCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            char c = 1;
            int i2 = 1;
            z = true;
            if (itemViewType == 0) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, null, this.cacheParent.get(i), this.positionToEmoji.get(i), false);
                if (!EmojiView.this.recentStickers.contains(tLRPC$Document) && !EmojiView.this.favouriteStickers.contains(tLRPC$Document)) {
                    z = false;
                }
                stickerEmojiCell.setRecent(z);
                return;
            }
            Integer num = null;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i3 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i3 == Integer.MIN_VALUE) {
                        emptyCell.setHeight(1);
                        return;
                    }
                    Object obj = this.rowStartPack.get(i3);
                    if (obj instanceof TLRPC$TL_messages_stickerSet) {
                        num = Integer.valueOf(((TLRPC$TL_messages_stickerSet) obj).documents.size());
                    } else if (obj instanceof Integer) {
                        num = (Integer) obj;
                    }
                    if (num == null) {
                        emptyCell.setHeight(1);
                        return;
                    } else if (num.intValue() != 0) {
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(num.intValue() / EmojiView.this.stickersGridAdapter.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                        if (height > 0) {
                            i2 = height;
                        }
                        emptyCell.setHeight(i2);
                        return;
                    } else {
                        emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                        return;
                    }
                }
                emptyCell.setHeight(AndroidUtilities.dp(82.0f));
            } else if (itemViewType == 2) {
                StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                Object obj2 = this.cache.get(i);
                if (!(obj2 instanceof TLRPC$TL_messages_stickerSet)) {
                    return;
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) obj2;
                if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tLRPC$TL_messages_stickerSet)) {
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                    if (tLRPC$StickerSet != null) {
                        stickerSetNameCell.setText(tLRPC$StickerSet.title, 0);
                    }
                    stickerSetNameCell.setUrl(tLRPC$TL_messages_stickerSet.set.short_name, this.searchQuery.length());
                    return;
                }
                Integer num2 = this.localPacksByName.get(tLRPC$TL_messages_stickerSet);
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                if (tLRPC$StickerSet2 != null && num2 != null) {
                    stickerSetNameCell.setText(tLRPC$StickerSet2.title, 0, num2.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                }
                stickerSetNameCell.setUrl(null, 0);
            } else if (itemViewType != 3) {
            } else {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.cache.get(i);
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                boolean z2 = EmojiView.this.installingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) >= 0;
                if (EmojiView.this.removingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) < 0) {
                    c = 0;
                }
                if (z2 || c != 0) {
                    if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                        EmojiView.this.installingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                        z2 = false;
                    } else if (c != 0 && !featuredStickerSetInfoCell.isInstalled()) {
                        EmojiView.this.removingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                    }
                }
                featuredStickerSetInfoCell.setAddDrawProgress(z2, false);
                int indexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.title, this.searchQuery);
                if (indexOfIgnoreCase >= 0) {
                    featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, false, false, indexOfIgnoreCase, this.searchQuery.length());
                    return;
                }
                featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, false);
                if (TextUtils.isEmpty(this.searchQuery) || AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.short_name, this.searchQuery) != 0) {
                    return;
                }
                featuredStickerSetInfoCell.setUrl(tLRPC$StickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r8v19, types: [org.telegram.tgnet.TLRPC$messages_StickerSet, org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int i;
            ArrayList<TLRPC$Document> arrayList;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int size = this.serverPacks.size();
            int size2 = this.localPacks.size();
            int i2 = !this.emojiArrays.isEmpty() ? 1 : 0;
            int i3 = -1;
            int i4 = -1;
            int i5 = 0;
            while (i4 < size + size2 + i2) {
                if (i4 == i3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                    i = size;
                } else {
                    if (i4 < size2) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.localPacks.get(i4);
                        arrayList = tLRPC$TL_messages_stickerSet.documents;
                        i = size;
                        tLRPC$StickerSetCovered = tLRPC$TL_messages_stickerSet;
                    } else {
                        int i7 = i4 - size2;
                        if (i7 < i2) {
                            int size3 = this.emojiArrays.size();
                            String str = "";
                            int i8 = 0;
                            for (int i9 = 0; i9 < size3; i9++) {
                                ArrayList<TLRPC$Document> arrayList2 = this.emojiArrays.get(i9);
                                String str2 = this.emojiStickers.get(arrayList2);
                                if (str2 != null && !str.equals(str2)) {
                                    this.positionToEmoji.put(this.totalItems + i8, str2);
                                    str = str2;
                                }
                                int size4 = arrayList2.size();
                                int i10 = 0;
                                while (i10 < size4) {
                                    int i11 = this.totalItems + i8;
                                    int i12 = (i8 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i5;
                                    TLRPC$Document tLRPC$Document = arrayList2.get(i10);
                                    int i13 = size;
                                    this.cache.put(i11, tLRPC$Document);
                                    int i14 = size3;
                                    String str3 = str;
                                    TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(tLRPC$Document));
                                    if (stickerSetById != null) {
                                        this.cacheParent.put(i11, stickerSetById);
                                    }
                                    this.positionToRow.put(i11, i12);
                                    i8++;
                                    i10++;
                                    size = i13;
                                    size3 = i14;
                                    str = str3;
                                }
                            }
                            i = size;
                            int ceil = (int) Math.ceil(i8 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            for (int i15 = 0; i15 < ceil; i15++) {
                                this.rowStartPack.put(i5 + i15, Integer.valueOf(i8));
                            }
                            this.totalItems += EmojiView.this.stickersGridAdapter.stickersPerRow * ceil;
                            i5 += ceil;
                        } else {
                            i = size;
                            TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.serverPacks.get(i7 - i2);
                            arrayList = tLRPC$StickerSetCovered2.covers;
                            tLRPC$StickerSetCovered = tLRPC$StickerSetCovered2;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil2 = (int) Math.ceil(arrayList.size() / EmojiView.this.stickersGridAdapter.stickersPerRow);
                        this.cache.put(this.totalItems, tLRPC$StickerSetCovered);
                        if (i4 >= size2 && (tLRPC$StickerSetCovered instanceof TLRPC$StickerSetCovered)) {
                            this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int size5 = arrayList.size();
                        int i16 = 0;
                        while (i16 < size5) {
                            int i17 = i16 + 1;
                            int i18 = this.totalItems + i17;
                            int i19 = i5 + 1 + (i16 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(i18, arrayList.get(i16));
                            this.cacheParent.put(i18, tLRPC$StickerSetCovered);
                            this.positionToRow.put(i18, i19);
                            if (i4 >= size2 && (tLRPC$StickerSetCovered instanceof TLRPC$StickerSetCovered)) {
                                this.positionsToSets.put(i18, tLRPC$StickerSetCovered);
                            }
                            i16 = i17;
                        }
                        int i20 = ceil2 + 1;
                        for (int i21 = 0; i21 < i20; i21++) {
                            this.rowStartPack.put(i5 + i21, tLRPC$StickerSetCovered);
                        }
                        this.totalItems += (ceil2 * EmojiView.this.stickersGridAdapter.stickersPerRow) + 1;
                        i5 += i20;
                    }
                }
                i4++;
                size = i;
                i3 = -1;
            }
            super.notifyDataSetChanged();
        }
    }

    public void searchProgressChanged() {
        updateStickerTabsPosition();
    }

    public float getStickersExpandOffset() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip == null) {
            return 0.0f;
        }
        return scrollSlidingTabStrip.getExpandedOffset();
    }

    public void setShowing(boolean z) {
        this.showing = z;
        updateStickerTabsPosition();
    }

    public void onMessageSend() {
        ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker != null) {
            chooseStickerActionTracker.reset();
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class ChooseStickerActionTracker {
        private final int currentAccount;
        private final long dialogId;
        private final int threadId;
        boolean typingWasSent;
        boolean visible = false;
        long lastActionTime = -1;

        public abstract boolean isShown();

        public ChooseStickerActionTracker(int i, long j, int i2) {
            this.currentAccount = i;
            this.dialogId = j;
            this.threadId = i2;
        }

        public void doSomeAction() {
            if (this.visible) {
                if (this.lastActionTime == -1) {
                    this.lastActionTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - this.lastActionTime <= 2000) {
                } else {
                    this.typingWasSent = true;
                    this.lastActionTime = System.currentTimeMillis();
                    MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 10, 0);
                }
            }
        }

        public void reset() {
            if (this.typingWasSent) {
                MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 2, 0);
            }
            this.lastActionTime = -1L;
        }

        public void checkVisibility() {
            boolean isShown = isShown();
            this.visible = isShown;
            if (!isShown) {
                reset();
            }
        }
    }
}
