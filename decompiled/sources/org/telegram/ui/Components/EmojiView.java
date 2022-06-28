package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
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
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes5.dex */
public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final ViewTreeObserver.OnScrollChangedListener NOP;
    private static final Field superListenerField;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    private ChooseStickerActionTracker chooseStickerActionTracker;
    private long currentChatId;
    private int currentPage;
    private EmojiViewDelegate delegate;
    private Paint dotPaint;
    private DragListener dragListener;
    private EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    private RecyclerListView emojiGridView;
    private Drawable[] emojiIcons;
    private float emojiLastX;
    private float emojiLastY;
    private GridLayoutManager emojiLayoutManager;
    private EmojiSearchAdapter emojiSearchAdapter;
    private SearchField emojiSearchField;
    private int emojiSize;
    private AnimatorSet emojiTabShadowAnimator;
    private ScrollSlidingTabStrip emojiTabs;
    private View emojiTabsShadow;
    private ImageViewEmoji emojiTouchedView;
    private float emojiTouchedX;
    private float emojiTouchedY;
    private boolean expandStickersByDragg;
    private boolean firstTabUpdate;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
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
    private TLRPC.TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private boolean hasChatStickers;
    private boolean ignoreStickersScroll;
    private TLRPC.ChatFull info;
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
    private RecyclerAnimationScrollHelper scrollHelper;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private View shadowLine;
    private boolean showGifs;
    private boolean showing;
    private Drawable[] stickerIcons;
    private ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private boolean stickersContainerAttached;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private int stickersMinusDy;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
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
    private int hasRecentEmoji = -1;
    private GifSearchPreloader gifSearchPreloader = new GifSearchPreloader();
    private final Map<String, TLRPC.messages_BotResults> gifCache = new HashMap();
    private boolean firstGifAttach = true;
    private int gifRecentTabNum = -2;
    private int gifTrendingTabNum = -2;
    private int gifFirstEmojiTabNum = -2;
    private boolean firstStickersAttach = true;
    private final int[] tabsMinusDy = new int[3];
    private ObjectAnimator[] tabsYAnimators = new ObjectAnimator[3];
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList<>();
    private ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    private ArrayList<TLRPC.Document> recentStickers = new ArrayList<>();
    private ArrayList<TLRPC.Document> favouriteStickers = new ArrayList<>();
    private ArrayList<TLRPC.Document> premiumStickers = new ArrayList<>();
    private ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList<>();
    private TLRPC.StickerSetCovered[] primaryInstallingStickerSets = new TLRPC.StickerSetCovered[10];
    private LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets = new LongSparseArray<>();
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
        public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        {
            EmojiView.this = this;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
            EmojiView.this.delegate.onStickerSelected(null, sticker, query, parent, null, notify, scheduleDate);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean needSend() {
            return true;
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
        public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
            if (set != null) {
                EmojiView.this.delegate.onShowStickerSet(null, set);
            }
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendGif(Object gif, Object parent, boolean notify, int scheduleDate) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected(null, gif, null, parent, notify, scheduleDate);
            } else if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.onGifSelected(null, gif, null, parent, notify, scheduleDate);
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
        public String getQuery(boolean isGif) {
            if (isGif) {
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

    /* loaded from: classes5.dex */
    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface Type {
        public static final int EMOJIS = 1;
        public static final int GIFS = 2;
        public static final int STICKERS = 0;
    }

    /* loaded from: classes5.dex */
    public interface EmojiViewDelegate {
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

        void onEmojiSelected(String str);

        void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet);

        void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i);

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(long j);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout);

        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiViewDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$onBackspace(EmojiViewDelegate _this) {
                return false;
            }

            public static void $default$onEmojiSelected(EmojiViewDelegate _this, String emoji) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate _this, View view, TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean notify, int scheduleDate) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate _this) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate _this, long chatId) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate _this, View view, Object gif, String query, Object parent, boolean notify, int scheduleDate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate _this, int type) {
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate _this) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate _this, TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate _this, TLRPC.StickerSetCovered stickerSet) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate _this, TLRPC.StickerSetCovered stickerSet) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate _this, int type) {
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$isExpanded(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$canSchedule(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate _this) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate _this) {
                return 0L;
            }

            public static int $default$getThreadId(EmojiViewDelegate _this) {
                return 0;
            }

            public static void $default$showTrendingStickersAlert(EmojiViewDelegate _this, TrendingStickersLayout layout) {
            }

            public static void $default$invalidateEnterView(EmojiViewDelegate _this) {
            }

            public static float $default$getProgressToSearchOpened(EmojiViewDelegate _this) {
                return 0.0f;
            }
        }
    }

    static {
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
        NOP = EmojiView$$ExternalSyntheticLambda5.INSTANCE;
    }

    public static /* synthetic */ void lambda$static$0() {
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.searchEditText.setEnabled(enabled);
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.searchEditText.setEnabled(enabled);
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 == null) {
            return;
        }
        searchField3.searchEditText.setEnabled(enabled);
    }

    /* loaded from: classes5.dex */
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
        public SearchField(Context context, final int type) {
            super(context);
            EmojiView.this = r13;
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(r13.getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            this.backgroundView = view2;
            view2.setBackgroundColor(r13.getThemedColor(Theme.key_chat_emojiPanelBackground));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, r13.searchFieldHeight));
            View view3 = new View(context);
            this.searchBackground = view3;
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), r13.getThemedColor(Theme.key_chat_emojiSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(r13.getThemedColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.EmojiView.SearchField.1
                {
                    SearchField.this = this;
                }

                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                protected int getCurrentColor() {
                    return EmojiView.this.getThemedColor(Theme.key_chat_emojiSearchIcon);
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
                    EmojiView.SearchField.this.m2597lambda$new$0$orgtelegramuiComponentsEmojiView$SearchField(view4);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Components.EmojiView.SearchField.2
                {
                    SearchField.this = this;
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(event);
                    }
                    if (event.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            EmojiView.this.openSearch(SearchField.this);
                        }
                        EmojiViewDelegate emojiViewDelegate = EmojiView.this.delegate;
                        int i = 1;
                        if (type == 1) {
                            i = 2;
                        }
                        emojiViewDelegate.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(r13.getThemedColor(Theme.key_chat_emojiSearchIcon));
            this.searchEditText.setTextColor(r13.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
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
            this.searchEditText.setCursorColor(r13.getThemedColor(Theme.key_featuredStickers_addedIcon));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.EmojiView.SearchField.3
                {
                    SearchField.this = this;
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    boolean showed = false;
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        showed = true;
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
                    int i = type;
                    if (i == 0) {
                        EmojiView.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 1) {
                        EmojiView.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 2) {
                        EmojiView.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-EmojiView$SearchField */
        public /* synthetic */ void m2597lambda$new$0$orgtelegramuiComponentsEmojiView$SearchField(View v) {
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
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.SearchField.4
                        {
                            SearchField.this = this;
                        }

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

    /* loaded from: classes5.dex */
    public class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int type) {
            EmojiView.this = r1;
            this.type = type;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
            } else if (newState == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                if (EmojiView.this.ignoreStickersScroll) {
                    EmojiView.this.ignoreStickersScroll = false;
                }
                this.smoothScrolling = false;
            } else {
                if (newState == 1) {
                    if (EmojiView.this.ignoreStickersScroll) {
                        EmojiView.this.ignoreStickersScroll = false;
                    }
                    SearchField searchField = EmojiView.this.getSearchFieldForType(this.type);
                    if (searchField != null) {
                        searchField.hideKeyboard();
                    }
                    this.smoothScrolling = false;
                }
                if (!this.smoothScrolling) {
                    EmojiView.this.stopAnimatingTabsY(this.type);
                }
                if (this.type == 0) {
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, dy);
            checkSearchFieldScroll();
            if (!this.smoothScrolling) {
                EmojiView.this.checkBottomTabScroll(dy);
            }
        }

        private void checkSearchFieldScroll() {
            switch (this.type) {
                case 0:
                    EmojiView.this.checkStickersSearchFieldScroll(false);
                    return;
                case 1:
                    EmojiView.this.checkEmojiSearchFieldScroll(false);
                    return;
                case 2:
                    EmojiView.this.checkGifSearchFieldScroll(false);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes5.dex */
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
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (isDragging()) {
                return super.onInterceptTouchEvent(ev);
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (ev.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = ev.getRawX();
                this.downY = ev.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(ev.getRawY() - this.downY) >= this.touchSlop) {
                this.draggingVertically = true;
                this.downY = ev.getRawY();
                EmojiView.this.dragListener.onDragStart();
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            if (isDragging()) {
                return super.onTouchEvent(ev);
            }
            if (this.first) {
                this.first = false;
                this.lastX = ev.getX();
            }
            if (ev.getAction() == 0 || ev.getAction() == 2) {
                EmojiView.this.lastStickersX = ev.getRawX();
            }
            if (ev.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = ev.getRawX();
                this.downY = ev.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(ev.getRawX() - this.downX) >= this.touchSlop && canScrollHorizontally((int) (this.downX - ev.getRawX()))) {
                    this.draggingHorizontally = true;
                    AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
                    EmojiView.this.expandStickersByDragg = true;
                    EmojiView.this.updateStickerTabsPosition();
                } else if (Math.abs(ev.getRawY() - this.downY) >= this.touchSlop) {
                    this.draggingVertically = true;
                    this.downY = ev.getRawY();
                    EmojiView.this.dragListener.onDragStart();
                    if (this.startedScroll) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                }
            }
            if (EmojiView.this.expandStickersByDragg && (ev.getAction() == 1 || ev.getAction() == 3)) {
                AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(ev);
                if (ev.getAction() != 1 && ev.getAction() != 3) {
                    EmojiView.this.dragListener.onDrag(Math.round(ev.getRawY() - this.downY));
                } else {
                    this.vTracker.computeCurrentVelocity(1000);
                    float velocity = this.vTracker.getYVelocity();
                    this.vTracker.recycle();
                    this.vTracker = null;
                    if (ev.getAction() == 1) {
                        EmojiView.this.dragListener.onDragEnd(velocity);
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
            float newTranslationX = getTranslationX();
            if (getScrollX() == 0 && newTranslationX == 0.0f) {
                if (!this.startedScroll && this.lastX - ev.getX() < 0.0f) {
                    if (EmojiView.this.pager.beginFakeDrag()) {
                        this.startedScroll = true;
                        this.lastTranslateX = getTranslationX();
                    }
                } else if (this.startedScroll && this.lastX - ev.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            if (this.startedScroll) {
                int x = (int) (((ev.getX() - this.lastX) + newTranslationX) - this.lastTranslateX);
                try {
                    this.lastTranslateX = newTranslationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception e2) {
                    }
                    this.startedScroll = false;
                    FileLog.e(e);
                }
            }
            this.lastX = ev.getX();
            if (ev.getAction() == 3 || ev.getAction() == 1) {
                this.first = true;
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            return this.startedScroll || super.onTouchEvent(ev);
        }
    }

    /* loaded from: classes5.dex */
    private class ImageViewEmoji extends ImageView {
        private boolean isRecent;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ImageViewEmoji(Context context) {
            super(context);
            EmojiView.this = r3;
            setScaleType(ImageView.ScaleType.CENTER);
            setBackground(Theme.createRadSelectorDrawable(r3.getThemedColor(Theme.key_listSelector), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        }

        public void sendEmoji(String override) {
            String color;
            EmojiView.this.showBottomTab(true, true);
            String code = override != null ? override : (String) getTag();
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append((CharSequence) code);
            if (override != null) {
                if (EmojiView.this.delegate != null) {
                    EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(override));
                    return;
                }
                return;
            }
            if (!this.isRecent && (color = Emoji.emojiColor.get(code)) != null) {
                code = EmojiView.addColorToCode(code, color);
            }
            EmojiView.this.addEmojiToRecent(code);
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(code));
            }
        }

        public void setImageDrawable(Drawable drawable, boolean recent) {
            super.setImageDrawable(drawable);
            this.isRecent = recent;
        }

        @Override // android.widget.ImageView, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.view.View");
        }
    }

    /* loaded from: classes5.dex */
    public class EmojiPopupWindow extends PopupWindow {
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow() {
            EmojiView.this = r1;
            init();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPopupWindow(Context context) {
            super(context);
            EmojiView.this = r1;
            init();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPopupWindow(int width, int height) {
            super(width, height);
            EmojiView.this = r1;
            init();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPopupWindow(View contentView) {
            super(contentView);
            EmojiView.this = r1;
            init();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPopupWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
            EmojiView.this = r1;
            init();
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPopupWindow(View contentView, int width, int height) {
            super(contentView, width, height);
            EmojiView.this = r1;
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception e) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            ViewTreeObserver viewTreeObserver;
            if (this.mSuperScrollListener != null && (viewTreeObserver = this.mViewTreeObserver) != null) {
                if (viewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View anchor) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver = this.mViewTreeObserver;
                if (vto != viewTreeObserver) {
                    if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = vto;
                    if (vto != null) {
                        vto.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        @Override // android.widget.PopupWindow
        public void showAsDropDown(View anchor, int xoff, int yoff) {
            try {
                super.showAsDropDown(anchor, xoff, yoff);
                registerListener(anchor);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.widget.PopupWindow
        public void update(View anchor, int xoff, int yoff, int width, int height) {
            super.update(anchor, xoff, yoff, width, height);
            registerListener(anchor);
        }

        @Override // android.widget.PopupWindow
        public void update(View anchor, int width, int height) {
            super.update(anchor, width, height);
            registerListener(anchor);
        }

        @Override // android.widget.PopupWindow
        public void showAtLocation(View parent, int gravity, int x, int y) {
            super.showAtLocation(parent, gravity, x, y);
            unregisterListener();
        }

        @Override // android.widget.PopupWindow
        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            unregisterListener();
        }
    }

    /* loaded from: classes5.dex */
    public class EmojiColorPickerView extends View {
        private int arrowX;
        private String currentEmoji;
        private int selection;
        private Paint rectPaint = new Paint(1);
        private RectF rect = new RectF();
        private Drawable backgroundDrawable = getResources().getDrawable(R.drawable.stickers_back_all);
        private Drawable arrowDrawable = getResources().getDrawable(R.drawable.stickers_back_arrow);

        public void setEmoji(String emoji, int arrowPosition) {
            this.currentEmoji = emoji;
            this.arrowX = arrowPosition;
            this.rectPaint.setColor(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR);
            invalidate();
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public void setSelection(int position) {
            if (this.selection == position) {
                return;
            }
            this.selection = position;
            invalidate();
        }

        public int getSelection() {
            return this.selection;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiColorPickerView(Context context) {
            super(context);
            EmojiView.this = r4;
            Theme.setDrawableColor(this.backgroundDrawable, r4.getThemedColor(Theme.key_dialogBackground));
            Theme.setDrawableColor(this.arrowDrawable, r4.getThemedColor(Theme.key_dialogBackground));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            String color;
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
                for (int a = 0; a < 6; a++) {
                    int x = (EmojiView.this.emojiSize * a) + AndroidUtilities.dp((a * 4) + 5);
                    int y = AndroidUtilities.dp(9.0f);
                    if (this.selection == a) {
                        this.rect.set(x, y - ((int) AndroidUtilities.dpf2(3.5f)), EmojiView.this.emojiSize + x, EmojiView.this.emojiSize + y + AndroidUtilities.dp(3.0f));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String code = this.currentEmoji;
                    if (a != 0) {
                        switch (a) {
                            case 1:
                                color = "🏻";
                                break;
                            case 2:
                                color = "🏼";
                                break;
                            case 3:
                                color = "🏽";
                                break;
                            case 4:
                                color = "🏾";
                                break;
                            case 5:
                                color = "🏿";
                                break;
                            default:
                                color = "";
                                break;
                        }
                        code = EmojiView.addColorToCode(code, color);
                    }
                    Drawable drawable2 = Emoji.getEmojiBigDrawable(code);
                    if (drawable2 != null) {
                        drawable2.setBounds(x, y, EmojiView.this.emojiSize + x, EmojiView.this.emojiSize + y);
                        drawable2.draw(canvas);
                    }
                }
            }
        }
    }

    public EmojiView(boolean needStickers, boolean needGif, Context context, boolean needSearch, TLRPC.ChatFull chatFull, ViewGroup parentView, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        int color = getThemedColor(Theme.key_chat_emojiBottomPanelIcon);
        int color2 = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color));
        this.needEmojiSearch = needSearch;
        this.tabIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_smiles, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_gif, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_stickers, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected))};
        this.emojiIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_recent, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_smiles, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_cat, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_food, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_activities, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_travel, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_objects, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_other, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.msg_emoji_flags, getThemedColor(Theme.key_chat_emojiPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected))};
        Drawable createEmojiIconSelectorDrawable = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new1, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected));
        this.searchIconDrawable = createEmojiIconSelectorDrawable;
        Drawable createEmojiIconSelectorDrawable2 = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new2, getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine), getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
        this.searchIconDotDrawable = createEmojiIconSelectorDrawable2;
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_recent, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_faves, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new3, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), new LayerDrawable(new Drawable[]{createEmojiIconSelectorDrawable, createEmojiIconSelectorDrawable2})};
        this.gifIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_recent, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_gifs_trending, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), getThemedColor(Theme.key_chat_emojiPanelIconSelected))};
        this.showGifs = needGif;
        this.info = chatFull;
        Paint paint = new Paint(1);
        this.dotPaint = paint;
        paint.setColor(getThemedColor(Theme.key_chat_emojiPanelNewTrending));
        if (Build.VERSION.SDK_INT >= 21) {
            this.outlineProvider = new ViewOutlineProvider() { // from class: org.telegram.ui.Components.EmojiView.3
                {
                    EmojiView.this = this;
                }

                @Override // android.view.ViewOutlineProvider
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

            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int widthSpec, int heightSpec) {
                this.ignoreLayout = true;
                int width = View.MeasureSpec.getSize(widthSpec);
                EmojiView.this.emojiLayoutManager.setSpanCount(Math.max(1, width / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
                this.ignoreLayout = false;
                super.onMeasure(widthSpec, heightSpec);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                    this.ignoreLayout = true;
                    EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(1, 0);
                    EmojiView.this.firstEmojiAttach = false;
                    this.ignoreLayout = false;
                }
                super.onLayout(changed, l, t, r, b);
                EmojiView.this.checkEmojiSearchFieldScroll(true);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (EmojiView.this.emojiTouchedView != null) {
                    if (event.getAction() == 1 || event.getAction() == 3) {
                        if (EmojiView.this.pickerViewPopup != null && EmojiView.this.pickerViewPopup.isShowing()) {
                            EmojiView.this.pickerViewPopup.dismiss();
                            String color3 = null;
                            switch (EmojiView.this.pickerView.getSelection()) {
                                case 1:
                                    color3 = "🏻";
                                    break;
                                case 2:
                                    color3 = "🏼";
                                    break;
                                case 3:
                                    color3 = "🏽";
                                    break;
                                case 4:
                                    color3 = "🏾";
                                    break;
                                case 5:
                                    color3 = "🏿";
                                    break;
                            }
                            String code = (String) EmojiView.this.emojiTouchedView.getTag();
                            if (!EmojiView.this.emojiTouchedView.isRecent) {
                                if (color3 != null) {
                                    Emoji.emojiColor.put(code, color3);
                                    code = EmojiView.addColorToCode(code, color3);
                                } else {
                                    Emoji.emojiColor.remove(code);
                                }
                                EmojiView.this.emojiTouchedView.setImageDrawable(Emoji.getEmojiBigDrawable(code), EmojiView.this.emojiTouchedView.isRecent);
                                EmojiView.this.emojiTouchedView.sendEmoji(null);
                                Emoji.saveEmojiColors();
                            } else {
                                String code2 = code.replace("🏻", "").replace("🏼", "").replace("🏽", "").replace("🏾", "").replace("🏿", "");
                                if (color3 != null) {
                                    EmojiView.this.emojiTouchedView.sendEmoji(EmojiView.addColorToCode(code2, color3));
                                } else {
                                    EmojiView.this.emojiTouchedView.sendEmoji(code2);
                                }
                            }
                        }
                        EmojiView.this.emojiTouchedView = null;
                        EmojiView.this.emojiTouchedX = -10000.0f;
                        EmojiView.this.emojiTouchedY = -10000.0f;
                    } else if (event.getAction() == 2) {
                        boolean ignore = false;
                        if (EmojiView.this.emojiTouchedX != -10000.0f) {
                            if (Math.abs(EmojiView.this.emojiTouchedX - event.getX()) > AndroidUtilities.getPixelsInCM(0.2f, true) || Math.abs(EmojiView.this.emojiTouchedY - event.getY()) > AndroidUtilities.getPixelsInCM(0.2f, false)) {
                                EmojiView.this.emojiTouchedX = -10000.0f;
                                EmojiView.this.emojiTouchedY = -10000.0f;
                            } else {
                                ignore = true;
                            }
                        }
                        if (!ignore) {
                            getLocationOnScreen(EmojiView.this.location);
                            float x = EmojiView.this.location[0] + event.getX();
                            EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
                            int position = (int) ((x - (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f))) / (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0f)));
                            if (position < 0) {
                                position = 0;
                            } else if (position > 5) {
                                position = 5;
                            }
                            EmojiView.this.pickerView.setSelection(position);
                        }
                    }
                    return true;
                }
                EmojiView.this.emojiLastX = event.getX();
                EmojiView.this.emojiLastY = event.getY();
                return super.onTouchEvent(event);
            }
        };
        this.emojiGridView = recyclerListView;
        recyclerListView.setInstantClick(true);
        RecyclerListView recyclerListView2 = this.emojiGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8);
        this.emojiLayoutManager = gridLayoutManager;
        recyclerListView2.setLayoutManager(gridLayoutManager);
        this.emojiGridView.setTopGlowOffset(AndroidUtilities.dp(38.0f));
        this.emojiGridView.setBottomGlowOffset(AndroidUtilities.dp(36.0f));
        this.emojiGridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
        this.emojiGridView.setGlowColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        this.emojiGridView.setSelectorDrawableColor(0);
        this.emojiGridView.setClipToPadding(false);
        this.emojiLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.5
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                    if ((EmojiView.this.needEmojiSearch && position == 0) || EmojiView.this.emojiAdapter.positionToSection.indexOfKey(position) >= 0) {
                        return EmojiView.this.emojiLayoutManager.getSpanCount();
                    }
                } else if (position == 0 || (position == 1 && EmojiView.this.emojiSearchAdapter.searchWas && EmojiView.this.emojiSearchAdapter.result.isEmpty())) {
                    return EmojiView.this.emojiLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        RecyclerListView recyclerListView3 = this.emojiGridView;
        EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter();
        this.emojiAdapter = emojiGridAdapter;
        recyclerListView3.setAdapter(emojiGridAdapter);
        this.emojiSearchAdapter = new EmojiSearchAdapter();
        this.emojiContainer.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f));
        this.emojiGridView.setOnScrollListener(new TypedScrollListener(1) { // from class: org.telegram.ui.Components.EmojiView.6
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView.TypedScrollListener, androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = EmojiView.this.emojiLayoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    int tab = 0;
                    int count = Emoji.recentEmoji.size() + (EmojiView.this.needEmojiSearch ? 1 : 0);
                    if (position >= count) {
                        int a = 0;
                        while (true) {
                            if (a >= EmojiData.dataColored.length) {
                                break;
                            }
                            int size = EmojiData.dataColored[a].length + 1;
                            if (position < count + size) {
                                tab = a + (!Emoji.recentEmoji.isEmpty() ? 1 : 0);
                                break;
                            } else {
                                count += size;
                                a++;
                            }
                        }
                    }
                    EmojiView.this.emojiTabs.onPageScrolled(tab, 0);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        this.emojiGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView.7
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public void onItemClick(View view, int position) {
                if (view instanceof ImageViewEmoji) {
                    ImageViewEmoji viewEmoji = (ImageViewEmoji) view;
                    viewEmoji.sendEmoji(null);
                    EmojiView.this.performHapticFeedback(3, 1);
                }
            }
        });
        this.emojiGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.EmojiView.8
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public boolean onItemClick(View view, int position) {
                if (view instanceof ImageViewEmoji) {
                    ImageViewEmoji viewEmoji = (ImageViewEmoji) view;
                    String code = (String) viewEmoji.getTag();
                    String color3 = null;
                    String toCheck = code.replace("🏻", "");
                    if (toCheck != code) {
                        color3 = "🏻";
                    }
                    if (color3 == null && (toCheck = code.replace("🏼", "")) != code) {
                        color3 = "🏼";
                    }
                    if (color3 == null && (toCheck = code.replace("🏽", "")) != code) {
                        color3 = "🏽";
                    }
                    if (color3 == null && (toCheck = code.replace("🏾", "")) != code) {
                        color3 = "🏾";
                    }
                    if (color3 == null && (toCheck = code.replace("🏿", "")) != code) {
                        color3 = "🏿";
                    }
                    if (EmojiData.emojiColoredMap.contains(toCheck)) {
                        EmojiView.this.emojiTouchedView = viewEmoji;
                        EmojiView emojiView = EmojiView.this;
                        emojiView.emojiTouchedX = emojiView.emojiLastX;
                        EmojiView emojiView2 = EmojiView.this;
                        emojiView2.emojiTouchedY = emojiView2.emojiLastY;
                        if (color3 == null && !viewEmoji.isRecent) {
                            String color4 = Emoji.emojiColor.get(toCheck);
                            color3 = color4;
                        }
                        int i = 5;
                        if (color3 == null) {
                            EmojiView.this.pickerView.setSelection(0);
                        } else {
                            char c = 65535;
                            switch (color3.hashCode()) {
                                case 1773375:
                                    if (color3.equals("🏻")) {
                                        c = 0;
                                        break;
                                    }
                                    break;
                                case 1773376:
                                    if (color3.equals("🏼")) {
                                        c = 1;
                                        break;
                                    }
                                    break;
                                case 1773377:
                                    if (color3.equals("🏽")) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                                case 1773378:
                                    if (color3.equals("🏾")) {
                                        c = 3;
                                        break;
                                    }
                                    break;
                                case 1773379:
                                    if (color3.equals("🏿")) {
                                        c = 4;
                                        break;
                                    }
                                    break;
                            }
                            switch (c) {
                                case 0:
                                    EmojiView.this.pickerView.setSelection(1);
                                    break;
                                case 1:
                                    EmojiView.this.pickerView.setSelection(2);
                                    break;
                                case 2:
                                    EmojiView.this.pickerView.setSelection(3);
                                    break;
                                case 3:
                                    EmojiView.this.pickerView.setSelection(4);
                                    break;
                                case 4:
                                    EmojiView.this.pickerView.setSelection(5);
                                    break;
                            }
                        }
                        viewEmoji.getLocationOnScreen(EmojiView.this.location);
                        int selection = EmojiView.this.emojiSize * EmojiView.this.pickerView.getSelection();
                        int selection2 = EmojiView.this.pickerView.getSelection() * 4;
                        if (!AndroidUtilities.isTablet()) {
                            i = 1;
                        }
                        int x = selection + AndroidUtilities.dp(selection2 - i);
                        if (EmojiView.this.location[0] - x < AndroidUtilities.dp(5.0f)) {
                            x += (EmojiView.this.location[0] - x) - AndroidUtilities.dp(5.0f);
                        } else if ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                            x += ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                        }
                        int xOffset = -x;
                        int yOffset = viewEmoji.getTop() < 0 ? viewEmoji.getTop() : 0;
                        EmojiView.this.pickerView.setEmoji(toCheck, (AndroidUtilities.dp(AndroidUtilities.isTablet() ? 30.0f : 22.0f) - xOffset) + ((int) AndroidUtilities.dpf2(0.5f)));
                        EmojiView.this.pickerViewPopup.setFocusable(true);
                        EmojiView.this.pickerViewPopup.showAsDropDown(view, xOffset, (((-view.getMeasuredHeight()) - EmojiView.this.popupHeight) + ((view.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - yOffset);
                        EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                        EmojiView.this.emojiGridView.hideSelector(true);
                        return true;
                    } else if (viewEmoji.isRecent) {
                        RecyclerView.ViewHolder holder = EmojiView.this.emojiGridView.findContainingViewHolder(view);
                        if (holder != null && holder.getAdapterPosition() <= Emoji.recentEmoji.size()) {
                            EmojiView.this.delegate.onClearEmojiRecent();
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        this.emojiTabs = new ScrollSlidingTabStrip(context, resourcesProvider) { // from class: org.telegram.ui.Components.EmojiView.9
            {
                EmojiView.this = this;
            }

            @Override // android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (EmojiView.this.emojiTabsShadow != null) {
                    EmojiView.this.emojiTabsShadow.setTranslationY(translationY);
                }
            }
        };
        if (needSearch) {
            SearchField searchField = new SearchField(context, 1);
            this.emojiSearchField = searchField;
            this.emojiContainer.addView(searchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.Components.EmojiView.10
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
        }
        this.emojiTabs.setShouldExpand(true);
        this.emojiTabs.setIndicatorHeight(-1);
        this.emojiTabs.setUnderlineHeight(-1);
        this.emojiTabs.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 38.0f));
        this.emojiTabs.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView.11
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
            public void onPageSelected(int page) {
                if (!Emoji.recentEmoji.isEmpty()) {
                    if (page == 0) {
                        EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(EmojiView.this.needEmojiSearch ? 1 : 0, 0);
                        return;
                    }
                    page--;
                }
                EmojiView.this.emojiGridView.stopScroll();
                EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(EmojiView.this.emojiAdapter.sectionToPosition.get(page), 0);
                EmojiView.this.checkEmojiTabY(null, 0);
            }
        });
        View view = new View(context);
        this.emojiTabsShadow = view;
        view.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(1);
        this.emojiTabsShadow.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(38.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        if (needStickers) {
            if (needGif) {
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
                    public boolean onInterceptTouchEvent(MotionEvent event) {
                        boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                        return super.onInterceptTouchEvent(event) || result;
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
                    public void onMeasure(int widthSpec, int heightSpec) {
                        super.onMeasure(widthSpec, heightSpec);
                        if (!this.wasMeasured) {
                            EmojiView.this.gifAdapter.notifyDataSetChanged();
                            this.wasMeasured = true;
                        }
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
                            EmojiView.this.gifSearchField.setVisibility(0);
                            EmojiView.this.gifTabs.onPageScrolled(0, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(changed, l, t, r, b);
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
                    public void getItemOffsets(android.graphics.Rect outRect, View view2, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view2);
                        int i = 0;
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter && position == EmojiView.this.gifAdapter.trendingSectionItem) {
                            outRect.set(0, 0, 0, 0);
                        } else if (position != 0) {
                            outRect.left = 0;
                            outRect.bottom = 0;
                            outRect.top = AndroidUtilities.dp(2.0f);
                            if (!EmojiView.this.gifLayoutManager.isLastInRow(position - 1)) {
                                i = AndroidUtilities.dp(2.0f);
                            }
                            outRect.right = i;
                        } else {
                            outRect.set(0, 0, 0, 0);
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
                this.gifGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view2, MotionEvent motionEvent) {
                        return EmojiView.this.m2577lambda$new$1$orgtelegramuiComponentsEmojiView(resourcesProvider, view2, motionEvent);
                    }
                });
                RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view2, int i) {
                        EmojiView.this.m2578lambda$new$2$orgtelegramuiComponentsEmojiView(view2, i);
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
                this.gifTabs.setIndicatorColor(getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
                this.gifTabs.setUnderlineColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
                this.gifTabs.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
                this.gifContainer.addView(this.gifTabs, LayoutHelper.createFrame(-1, 36, 51));
                updateGifTabs();
                this.gifTabs.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10
                    @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                    public final void onPageSelected(int i) {
                        EmojiView.this.m2579lambda$new$3$orgtelegramuiComponentsEmojiView(i);
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
            RecyclerListView recyclerListView7 = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiView.15
                boolean ignoreLayout;

                {
                    EmojiView.this = this;
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent event) {
                    boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                    return super.onInterceptTouchEvent(event) || result;
                }

                @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(changed, l, t, r, b);
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
            this.stickersGridView = recyclerListView7;
            GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, 5) { // from class: org.telegram.ui.Components.EmojiView.16
                {
                    EmojiView.this = this;
                }

                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    try {
                        LinearSmoothScrollerCustom linearSmoothScroller = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2);
                        linearSmoothScroller.setTargetPosition(position);
                        startSmoothScroll(linearSmoothScroller);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int i = super.scrollVerticallyBy(dy, recycler, state);
                    if (i != 0 && EmojiView.this.stickersGridView.getScrollState() == 1) {
                        EmojiView.this.expandStickersByDragg = false;
                        EmojiView.this.updateStickerTabsPosition();
                    }
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                    return i;
                }
            };
            this.stickersLayoutManager = gridLayoutManager2;
            recyclerListView7.setLayoutManager(gridLayoutManager2);
            this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.17
                {
                    EmojiView.this = this;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int position) {
                    if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                        if (position == 0) {
                            return EmojiView.this.stickersGridAdapter.stickersPerRow;
                        }
                        if (position != EmojiView.this.stickersGridAdapter.totalItems) {
                            Object object = EmojiView.this.stickersGridAdapter.cache.get(position);
                            if (object == null || (EmojiView.this.stickersGridAdapter.cache.get(position) instanceof TLRPC.Document)) {
                                return 1;
                            }
                        }
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    }
                    if (position != EmojiView.this.stickersSearchGridAdapter.totalItems) {
                        Object object2 = EmojiView.this.stickersSearchGridAdapter.cache.get(position);
                        if (object2 == null || (EmojiView.this.stickersSearchGridAdapter.cache.get(position) instanceof TLRPC.Document)) {
                            return 1;
                        }
                    }
                    return EmojiView.this.stickersGridAdapter.stickersPerRow;
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersContainer);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
            RecyclerListView recyclerListView8 = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView8.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    return EmojiView.this.m2580lambda$new$4$orgtelegramuiComponentsEmojiView(resourcesProvider, view2, motionEvent);
                }
            });
            RecyclerListView.OnItemClickListener onItemClickListener2 = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view2, int i) {
                    EmojiView.this.m2581lambda$new$5$orgtelegramuiComponentsEmojiView(view2, i);
                }
            };
            this.stickersOnItemClickListener = onItemClickListener2;
            this.stickersGridView.setOnItemClickListener(onItemClickListener2);
            this.stickersGridView.setGlowColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            this.stickersContainer.addView(this.stickersGridView);
            this.scrollHelper = new RecyclerAnimationScrollHelper(this.stickersGridView, this.stickersLayoutManager);
            SearchField searchField3 = new SearchField(context, 0);
            this.stickersSearchField = searchField3;
            this.stickersContainer.addView(searchField3, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            AnonymousClass18 anonymousClass18 = new AnonymousClass18(context, resourcesProvider);
            this.stickersTab = anonymousClass18;
            anonymousClass18.setDragEnabled(true);
            this.stickersTab.setWillNotDraw(false);
            this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
            this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
            this.stickersTab.setIndicatorColor(getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
            this.stickersTab.setUnderlineColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            if (parentView != null) {
                FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.19
                    Paint paint = new Paint();

                    {
                        EmojiView.this = this;
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        EmojiView.this.delegate.getProgressToSearchOpened();
                        float searchProgressOffset = AndroidUtilities.dp(50.0f) * EmojiView.this.delegate.getProgressToSearchOpened();
                        if (searchProgressOffset > getMeasuredHeight()) {
                            return;
                        }
                        canvas.save();
                        if (searchProgressOffset != 0.0f) {
                            canvas.clipRect(0.0f, searchProgressOffset, getMeasuredWidth(), getMeasuredHeight());
                        }
                        this.paint.setColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground));
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(36.0f) + EmojiView.this.stickersTab.getExpandedOffset(), this.paint);
                        super.dispatchDraw(canvas);
                        EmojiView.this.stickersTab.drawOverlays(canvas);
                        canvas.restore();
                    }

                    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                        super.onLayout(changed, left, top, right, bottom);
                        EmojiView.this.updateStickerTabsPosition();
                    }
                };
                this.stickersTabContainer = frameLayout3;
                frameLayout3.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
                parentView.addView(this.stickersTabContainer, LayoutHelper.createFrame(-1, -2.0f));
            } else {
                this.stickersContainer.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
            }
            updateStickerTabs();
            this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                public final void onPageSelected(int i) {
                    EmojiView.this.m2582lambda$new$6$orgtelegramuiComponentsEmojiView(i);
                }
            });
            this.stickersGridView.setOnScrollListener(new TypedScrollListener(0));
        }
        ViewPager viewPager = new ViewPager(context) { // from class: org.telegram.ui.Components.EmojiView.20
            {
                EmojiView.this = this;
            }

            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                }
                try {
                    return super.onInterceptTouchEvent(ev);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager
            public void setCurrentItem(int item, boolean smoothScroll) {
                EmojiView.this.startStopVisibleGifs(item == 1);
                if (item == getCurrentItem()) {
                    if (item == 0) {
                        EmojiView.this.emojiGridView.smoothScrollToPosition(EmojiView.this.needEmojiSearch ? 1 : 0);
                        return;
                    } else if (item == 1) {
                        EmojiView.this.gifGridView.smoothScrollToPosition(1);
                        return;
                    } else {
                        EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                        return;
                    }
                }
                super.setCurrentItem(item, smoothScroll);
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
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (event.getAction() == 3 || event.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!EmojiView.this.backspaceOnce && EmojiView.this.delegate != null && EmojiView.this.delegate.onBackspace()) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(event);
                return true;
            }
        };
        this.backspaceButton = imageView;
        imageView.setHapticFeedbackEnabled(true);
        this.backspaceButton.setImageResource(R.drawable.smiles_tab_clear);
        this.backspaceButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        this.backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
        this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
        this.backspaceButton.setFocusable(true);
        this.backspaceButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.22
            {
                EmojiView.this = this;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
            }
        });
        this.bottomTabContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.23
            {
                EmojiView.this = this;
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        View view3 = new View(context);
        this.shadowLine = view3;
        view3.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
        this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
        View view4 = new View(context);
        this.bottomTabContainerBackground = view4;
        this.bottomTabContainer.addView(view4, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(44.0f), 83));
        if (needSearch) {
            addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(44.0f) + AndroidUtilities.getShadowHeight(), 83));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(52, 44, 85));
            if (Build.VERSION.SDK_INT >= 21) {
                this.backspaceButton.setBackground(Theme.createSelectorDrawable(color2));
            }
            ImageView imageView2 = new ImageView(context);
            this.stickerSettingsButton = imageView2;
            imageView2.setImageResource(R.drawable.smiles_tab_settings);
            this.stickerSettingsButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
            this.stickerSettingsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.stickerSettingsButton.setFocusable(true);
            if (Build.VERSION.SDK_INT >= 21) {
                this.stickerSettingsButton.setBackground(Theme.createSelectorDrawable(color2));
            }
            this.stickerSettingsButton.setContentDescription(LocaleController.getString("Settings", R.string.Settings));
            this.bottomTabContainer.addView(this.stickerSettingsButton, LayoutHelper.createFrame(52, 44, 85));
            this.stickerSettingsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.24
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
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
                {
                    EmojiView.this = this;
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    SearchField currentField;
                    SearchField field;
                    EmojiView.this.checkGridVisibility(position, positionOffset);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.onPageScrolled(position, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), positionOffsetPixels);
                    boolean z = true;
                    EmojiView.this.showBottomTab(true, true);
                    int p = EmojiView.this.pager.getCurrentItem();
                    if (p == 0) {
                        currentField = EmojiView.this.emojiSearchField;
                    } else {
                        currentField = p == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    }
                    String currentFieldText = currentField.searchEditText.getText().toString();
                    int a = 0;
                    while (a < 3) {
                        if (a == 0) {
                            field = EmojiView.this.emojiSearchField;
                        } else {
                            field = a == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                        }
                        if (field != null && field != currentField && field.searchEditText != null && !field.searchEditText.getText().toString().equals(currentFieldText)) {
                            field.searchEditText.setText(currentFieldText);
                            field.searchEditText.setSelection(currentFieldText.length());
                        }
                        a++;
                    }
                    EmojiView emojiView2 = EmojiView.this;
                    if ((position != 0 || positionOffset <= 0.0f) && position != 1) {
                        z = false;
                    }
                    emojiView2.startStopVisibleGifs(z);
                    EmojiView.this.updateStickerTabsPosition();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int position) {
                    EmojiView.this.saveNewPage();
                    boolean z = false;
                    EmojiView.this.showBackspaceButton(position == 0, true);
                    EmojiView emojiView = EmojiView.this;
                    if (position == 2) {
                        z = true;
                    }
                    emojiView.showStickerSettingsButton(z, true);
                    if (EmojiView.this.delegate.isSearchOpened()) {
                        if (position == 0) {
                            if (EmojiView.this.emojiSearchField != null) {
                                EmojiView.this.emojiSearchField.searchEditText.requestFocus();
                            }
                        } else if (position == 1) {
                            if (EmojiView.this.gifSearchField != null) {
                                EmojiView.this.gifSearchField.searchEditText.requestFocus();
                            }
                        } else if (EmojiView.this.stickersSearchField != null) {
                            EmojiView.this.stickersSearchField.searchEditText.requestFocus();
                        }
                    }
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int state) {
                }
            });
            ImageView imageView3 = new ImageView(context);
            this.searchButton = imageView3;
            imageView3.setImageResource(R.drawable.smiles_tab_search);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
            this.searchButton.setScaleType(ImageView.ScaleType.CENTER);
            this.searchButton.setContentDescription(LocaleController.getString("Search", R.string.Search));
            this.searchButton.setFocusable(true);
            if (Build.VERSION.SDK_INT >= 21) {
                this.searchButton.setBackground(Theme.createSelectorDrawable(color2));
            }
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(52, 44, 83));
            this.searchButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.26
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    SearchField currentField;
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    if (currentItem == 0) {
                        currentField = EmojiView.this.emojiSearchField;
                    } else {
                        currentField = currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    }
                    if (currentField != null) {
                        currentField.searchEditText.requestFocus();
                        MotionEvent event = MotionEvent.obtain(0L, 0L, 0, 0.0f, 0.0f, 0);
                        currentField.searchEditText.onTouchEvent(event);
                        event.recycle();
                        MotionEvent event2 = MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0);
                        currentField.searchEditText.onTouchEvent(event2);
                        event2.recycle();
                    }
                }
            });
        } else {
            addView(this.bottomTabContainer, LayoutHelper.createFrame((Build.VERSION.SDK_INT >= 21 ? 40 : 44) + 20, (Build.VERSION.SDK_INT >= 21 ? 40 : 44) + 12, (LocaleController.isRTL ? 3 : 5) | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_chat_emojiPanelBackground), getThemedColor(Theme.key_chat_emojiPanelBackground));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                drawable = combinedDrawable;
            } else {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                this.backspaceButton.setStateListAnimator(animator);
                this.backspaceButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.EmojiView.27
                    {
                        EmojiView.this = this;
                    }

                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view5, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                    }
                });
            }
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackground(drawable);
            this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
            this.backspaceButton.setFocusable(true);
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 40 : 44, Build.VERSION.SDK_INT >= 21 ? 40 : 44, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.shadowLine.setVisibility(8);
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), getThemedColor(Theme.key_chat_gifSaveHintBackground)));
        this.mediaBanTooltip.setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context);
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        int dp = AndroidUtilities.dp(((AndroidUtilities.isTablet() ? 40 : 32) * 6) + 10 + 20);
        this.popupWidth = dp;
        int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = dp2;
        EmojiPopupWindow emojiPopupWindow = new EmojiPopupWindow(emojiColorPickerView, dp, dp2);
        this.pickerViewPopup = emojiPopupWindow;
        emojiPopupWindow.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View view5, int i, KeyEvent keyEvent) {
                return EmojiView.this.m2583lambda$new$7$orgtelegramuiComponentsEmojiView(view5, i, keyEvent);
            }
        });
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
        if (this.typeTabs != null) {
            if (this.views.size() == 1 && this.typeTabs.getVisibility() == 0) {
                this.typeTabs.setVisibility(4);
            } else if (this.views.size() != 1 && this.typeTabs.getVisibility() != 0) {
                this.typeTabs.setVisibility(0);
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ boolean m2577lambda$new$1$orgtelegramuiComponentsEmojiView(Theme.ResourcesProvider resourcesProvider, View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2578lambda$new$2$orgtelegramuiComponentsEmojiView(View view, int position) {
        if (this.delegate == null) {
            return;
        }
        int position2 = position - 1;
        RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
        GifAdapter gifAdapter = this.gifAdapter;
        if (adapter == gifAdapter) {
            if (position2 < 0) {
                return;
            }
            if (position2 < gifAdapter.recentItemsCount) {
                this.delegate.onGifSelected(view, this.recentGifs.get(position2), null, "gif", true, 0);
                return;
            }
            int resultPos = position2;
            if (this.gifAdapter.recentItemsCount > 0) {
                resultPos = (resultPos - this.gifAdapter.recentItemsCount) - 1;
            }
            if (resultPos < 0 || resultPos >= this.gifAdapter.results.size()) {
                return;
            }
            this.delegate.onGifSelected(view, this.gifAdapter.results.get(resultPos), null, this.gifAdapter.bot, true, 0);
            return;
        }
        RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
        GifAdapter gifAdapter2 = this.gifSearchAdapter;
        if (adapter2 != gifAdapter2 || position2 < 0 || position2 >= gifAdapter2.results.size()) {
            return;
        }
        this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(position2), this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.bot, true, 0);
        updateRecentGifs();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2579lambda$new$3$orgtelegramuiComponentsEmojiView(int page) {
        if (page == this.gifTrendingTabNum && this.gifAdapter.results.isEmpty()) {
            return;
        }
        this.gifGridView.stopScroll();
        this.gifTabs.onPageScrolled(page, 0);
        int i = 1;
        if (page == this.gifRecentTabNum || page == this.gifTrendingTabNum) {
            this.gifSearchField.searchEditText.setText("");
            if (page != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                GifLayoutManager gifLayoutManager = this.gifLayoutManager;
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                    i = 0;
                }
                gifLayoutManager.scrollToPositionWithOffset(i, 0);
            } else {
                this.gifLayoutManager.scrollToPositionWithOffset(this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
            }
            if (page == this.gifTrendingTabNum) {
                ArrayList<String> gifSearchEmojies = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                if (!gifSearchEmojies.isEmpty()) {
                    this.gifSearchPreloader.preload(gifSearchEmojies.get(0));
                }
            }
        } else {
            ArrayList<String> gifSearchEmojies2 = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
            this.gifSearchAdapter.searchEmoji(gifSearchEmojies2.get(page - this.gifFirstEmojiTabNum));
            int i2 = this.gifFirstEmojiTabNum;
            if (page - i2 > 0) {
                this.gifSearchPreloader.preload(gifSearchEmojies2.get((page - i2) - 1));
            }
            if (page - this.gifFirstEmojiTabNum < gifSearchEmojies2.size() - 1) {
                this.gifSearchPreloader.preload(gifSearchEmojies2.get((page - this.gifFirstEmojiTabNum) + 1));
            }
        }
        resetTabsY(2);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ boolean m2580lambda$new$4$orgtelegramuiComponentsEmojiView(Theme.ResourcesProvider resourcesProvider, View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2581lambda$new$5$orgtelegramuiComponentsEmojiView(View view, int position) {
        String query = null;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter) {
            query = stickersSearchGridAdapter.searchQuery;
            TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(position);
            if (pack != null) {
                this.delegate.onShowStickerSet(pack.set, null);
                return;
            }
        }
        if (!(view instanceof StickerEmojiCell)) {
            return;
        }
        StickerEmojiCell cell = (StickerEmojiCell) view;
        if (cell.getSticker() != null && MessageObject.isPremiumSticker(cell.getSticker()) && !AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
            ContentPreviewViewer.getInstance().showMenuFor(cell);
            return;
        }
        ContentPreviewViewer.getInstance().reset();
        if (cell.isDisabled()) {
            return;
        }
        cell.disable();
        this.delegate.onStickerSelected(cell, cell.getSticker(), query, cell.getParentObject(), cell.getSendAnimationData(), true, 0);
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$18 */
    /* loaded from: classes5.dex */
    public class AnonymousClass18 extends DraggableScrollSlidingTabStrip {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass18(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            EmojiView.this = this$0;
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
        protected void stickerSetPositionChanged(int fromPosition, int toPosition) {
            int index1 = fromPosition - EmojiView.this.stickersTabOffset;
            int index2 = toPosition - EmojiView.this.stickersTabOffset;
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            swapListElements(EmojiView.this.stickerSets, index1, index2);
            if (EmojiView.this.hasChatStickers) {
                swapListElements(mediaDataController.getStickerSets(0), index1 - 1, index2 - 1);
            } else {
                swapListElements(mediaDataController.getStickerSets(0), index1, index2);
            }
            EmojiView.this.reloadStickersAdapter();
            AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
            AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            sendReorder();
            EmojiView.this.updateStickerTabs();
        }

        private void swapListElements(List<TLRPC.TL_messages_stickerSet> list, int index1, int index2) {
            TLRPC.TL_messages_stickerSet set1 = list.remove(index1);
            list.add(index2, set1);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v10 */
        /* JADX WARN: Type inference failed for: r2v2, types: [int] */
        /* JADX WARN: Type inference failed for: r4v3, types: [java.util.ArrayList] */
        private void sendReorder() {
            MediaDataController.getInstance(EmojiView.this.currentAccount).calcNewHash(0);
            TLRPC.TL_messages_reorderStickerSets req = new TLRPC.TL_messages_reorderStickerSets();
            req.masks = false;
            for (int a = EmojiView.this.hasChatStickers; a < EmojiView.this.stickerSets.size(); a++) {
                req.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet) EmojiView.this.stickerSets.get(a)).set.id));
            }
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, EmojiView$18$$ExternalSyntheticLambda0.INSTANCE);
            NotificationCenter.getInstance(EmojiView.this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, 0);
        }

        public static /* synthetic */ void lambda$sendReorder$0(TLObject response, TLRPC.TL_error error) {
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void invalidateOverlays() {
            EmojiView.this.stickersTabContainer.invalidate();
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2582lambda$new$6$orgtelegramuiComponentsEmojiView(int page) {
        int firstTab;
        if (this.firstTabUpdate) {
            return;
        }
        if (page == this.trendingTabNum) {
            openTrendingStickers(null);
        } else if (page == this.recentTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("recent"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i = this.recentTabNum;
            scrollSlidingTabStrip.onPageScrolled(i, i > 0 ? i : this.stickersTabOffset);
        } else if (page == this.favTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("fav"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            int i2 = this.favTabNum;
            scrollSlidingTabStrip2.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
        } else if (page == this.premiumTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("premium"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.stickersTab;
            int i3 = this.premiumTabNum;
            scrollSlidingTabStrip3.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
        } else {
            int index = page - this.stickersTabOffset;
            if (index >= this.stickerSets.size()) {
                return;
            }
            if (index >= this.stickerSets.size()) {
                index = this.stickerSets.size() - 1;
            }
            this.firstStickersAttach = false;
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(index)), 0);
            resetTabsY(0);
            checkScroll(0);
            if (this.favTabNum > 0) {
                firstTab = this.favTabNum;
            } else {
                int firstTab2 = this.recentTabNum;
                if (firstTab2 > 0) {
                    firstTab = this.recentTabNum;
                } else {
                    firstTab = this.stickersTabOffset;
                }
            }
            this.stickersTab.onPageScrolled(page, firstTab);
            this.expandStickersByDragg = false;
            updateStickerTabsPosition();
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ boolean m2583lambda$new$7$orgtelegramuiComponentsEmojiView(View v, int keyCode, KeyEvent event) {
        EmojiPopupWindow emojiPopupWindow;
        if (keyCode == 82 && event.getRepeatCount() == 0 && event.getAction() == 1 && (emojiPopupWindow = this.pickerViewPopup) != null && emojiPopupWindow.isShowing()) {
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

    public void checkGridVisibility(int position, float positionOffset) {
        if (this.stickersContainer == null || this.gifContainer == null) {
            return;
        }
        int i = 0;
        if (position == 0) {
            this.emojiGridView.setVisibility(0);
            this.gifGridView.setVisibility(positionOffset == 0.0f ? 8 : 0);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (positionOffset == 0.0f) {
                i = 8;
            }
            scrollSlidingTabStrip.setVisibility(i);
            this.stickersGridView.setVisibility(8);
            this.stickersTab.setVisibility(8);
        } else if (position == 1) {
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(0);
            this.gifTabs.setVisibility(0);
            this.stickersGridView.setVisibility(positionOffset == 0.0f ? 8 : 0);
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            if (positionOffset == 0.0f) {
                i = 8;
            }
            scrollSlidingTabStrip2.setVisibility(i);
        } else if (position == 2) {
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(8);
            this.gifTabs.setVisibility(8);
            this.stickersGridView.setVisibility(0);
            this.stickersTab.setVisibility(0);
        }
    }

    public static String addColorToCode(String code, String color) {
        String end = null;
        int length = code.length();
        if (length > 2 && code.charAt(code.length() - 2) == 8205) {
            end = code.substring(code.length() - 2);
            code = code.substring(0, code.length() - 2);
        } else if (length > 3 && code.charAt(code.length() - 3) == 8205) {
            end = code.substring(code.length() - 3);
            code = code.substring(0, code.length() - 3);
        }
        String code2 = code + color;
        if (end != null) {
            return code2 + end;
        }
        return code2;
    }

    public void openTrendingStickers(TLRPC.StickerSetCovered set) {
        TrendingStickersLayout.Delegate trendingDelegate = new TrendingStickersLayout.Delegate() { // from class: org.telegram.ui.Components.EmojiView.29
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
                EmojiView.this.delegate.onStickerSetAdd(stickerSet);
                if (primary) {
                    EmojiView.this.updateStickerTabs();
                }
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                EmojiView.this.delegate.onStickerSetRemove(stickerSet);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean onListViewInterceptTouchEvent(RecyclerListView listView, MotionEvent event) {
                return ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, listView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean onListViewTouchEvent(RecyclerListView listView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent event) {
                return ContentPreviewViewer.getInstance().onTouch(event, listView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public String[] getLastSearchKeyboardLanguage() {
                return EmojiView.this.lastSearchKeyboardLanguage;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void setLastSearchKeyboardLanguage(String[] language) {
                EmojiView.this.lastSearchKeyboardLanguage = language;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean canSendSticker() {
                return true;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSelected(TLRPC.Document sticker, Object parent, boolean clearsInputField, boolean notify, int scheduleDate) {
                EmojiView.this.delegate.onStickerSelected(null, sticker, null, parent, null, notify, scheduleDate);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }
        };
        this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(getContext(), trendingDelegate, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets, set, this.resourcesProvider));
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        updateStickerTabsPosition();
        updateBottomTabContainerPosition();
    }

    private void updateBottomTabContainerPosition() {
        View parent;
        float y;
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                ViewPager viewPager = this.pager;
                if ((viewPager == null || viewPager.getCurrentItem() != 0) && (parent = (View) getParent()) != null) {
                    float y2 = getY() - parent.getHeight();
                    if (getLayoutParams().height > 0) {
                        y = y2 + getLayoutParams().height;
                    } else {
                        y = y2 + getMeasuredHeight();
                    }
                    if (this.bottomTabContainer.getTop() - y < 0.0f) {
                        y = this.bottomTabContainer.getTop();
                    }
                    this.bottomTabContainer.setTranslationY(-y);
                }
            }
        }
    }

    public void updateStickerTabsPosition() {
        if (this.stickersTabContainer == null) {
            return;
        }
        boolean visible = getVisibility() == 0 && this.stickersContainerAttached && this.delegate.getProgressToSearchOpened() != 1.0f;
        this.stickersTabContainer.setVisibility(visible ? 0 : 8);
        if (visible) {
            this.rect.setEmpty();
            this.pager.getChildVisibleRect(this.stickersContainer, this.rect, null);
            float searchProgressOffset = AndroidUtilities.dp(50.0f) * this.delegate.getProgressToSearchOpened();
            int left = this.rect.left;
            if (left != 0 || searchProgressOffset != 0.0f) {
                this.expandStickersByDragg = false;
            }
            this.stickersTabContainer.setTranslationX(left);
            float y = (((getTop() + getTranslationY()) - this.stickersTabContainer.getTop()) - this.stickersTab.getExpandedOffset()) - searchProgressOffset;
            if (this.stickersTabContainer.getTranslationY() != y) {
                this.stickersTabContainer.setTranslationY(y);
                this.stickersTabContainer.invalidate();
            }
        }
        if (this.expandStickersByDragg && visible && this.showing) {
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

    public void startStopVisibleGifs(boolean start) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView == null) {
            return;
        }
        int count = recyclerListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.gifGridView.getChildAt(a);
            if (child instanceof ContextLinkCell) {
                ContextLinkCell cell = (ContextLinkCell) child;
                ImageReceiver imageReceiver = cell.getPhotoImage();
                if (start) {
                    imageReceiver.setAllowStartAnimation(true);
                    imageReceiver.startAnimation();
                } else {
                    imageReceiver.setAllowStartAnimation(false);
                    imageReceiver.stopAnimation();
                }
            }
        }
    }

    public void addEmojiToRecent(String code) {
        if (!Emoji.isValidEmoji(code)) {
            return;
        }
        Emoji.recentEmoji.size();
        Emoji.addRecentEmoji(code);
        if (getVisibility() != 0 || this.pager.getCurrentItem() != 0) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
        }
        Emoji.saveRecentEmoji();
    }

    public void showSearchField(boolean show) {
        for (int a = 0; a < 3; a++) {
            GridLayoutManager layoutManager = getLayoutManagerForType(a);
            int position = layoutManager.findFirstVisibleItemPosition();
            if (show) {
                if (position == 1 || position == 2) {
                    layoutManager.scrollToPosition(0);
                    resetTabsY(a);
                }
            } else if (position == 0) {
                layoutManager.scrollToPositionWithOffset(1, 0);
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
        GridLayoutManager layoutManager;
        ScrollSlidingTabStrip tabStrip;
        final RecyclerListView gridView;
        SearchField currentField;
        EmojiViewDelegate emojiViewDelegate;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int a = 0; a < 3; a++) {
            boolean z = true;
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                tabStrip = this.emojiTabs;
                layoutManager = this.emojiLayoutManager;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                tabStrip = this.gifTabs;
                layoutManager = this.gifLayoutManager;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                tabStrip = this.stickersTab;
                layoutManager = this.stickersLayoutManager;
            }
            if (currentField != null) {
                if (searchField == currentField && (emojiViewDelegate = this.delegate) != null && emojiViewDelegate.isExpanded()) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (tabStrip != null && a != 2) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, AndroidUtilities.dp(0.0f)));
                    } else {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, AndroidUtilities.dp(0.0f)));
                    }
                    this.searchAnimation.setDuration(220L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.30
                        {
                            EmojiView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                gridView.setTranslationY(0.0f);
                                if (gridView != EmojiView.this.stickersGridView) {
                                    if (gridView == EmojiView.this.emojiGridView || gridView == EmojiView.this.gifGridView) {
                                        gridView.setPadding(0, 0, 0, 0);
                                    }
                                } else {
                                    gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                } else {
                    currentField.setTranslationY(AndroidUtilities.dp(0.0f));
                    if (tabStrip != null && a != 2) {
                        tabStrip.setTranslationY(-AndroidUtilities.dp(36.0f));
                    }
                    if (gridView == this.stickersGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    } else if (gridView == this.emojiGridView || gridView == this.gifGridView) {
                        gridView.setPadding(0, 0, 0, 0);
                    }
                    if (gridView == this.gifGridView) {
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
                    layoutManager.scrollToPositionWithOffset(0, 0);
                }
            }
        }
    }

    private void showEmojiShadow(boolean show, boolean animated) {
        if (!show || this.emojiTabsShadow.getTag() != null) {
            if (!show && this.emojiTabsShadow.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!show) {
                num = 1;
            }
            view.setTag(num);
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.emojiTabShadowAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                View view2 = this.emojiTabsShadow;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!show) {
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
                    public void onAnimationEnd(Animator animation) {
                        EmojiView.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
                return;
            }
            View view3 = this.emojiTabsShadow;
            if (!show) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void closeSearch(boolean animated) {
        closeSearch(animated, -1L);
    }

    private void scrollStickersToPosition(int p, int offset) {
        View view = this.stickersLayoutManager.findViewByPosition(p);
        int firstPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (view == null && Math.abs(p - firstPosition) > 40) {
            this.scrollHelper.setScrollDirection(this.stickersLayoutManager.findFirstVisibleItemPosition() < p ? 0 : 1);
            this.scrollHelper.scrollToPosition(p, offset, false, true);
            return;
        }
        this.ignoreStickersScroll = true;
        this.stickersGridView.smoothScrollToPosition(p);
    }

    public void closeSearch(boolean animated, long scrollToSet) {
        ScrollSlidingTabStrip tabStrip;
        final GridLayoutManager layoutManager;
        final RecyclerListView gridView;
        SearchField currentField;
        TLRPC.TL_messages_stickerSet set;
        int pos;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && scrollToSet != -1 && (set = MediaDataController.getInstance(this.currentAccount).getStickerSetById(scrollToSet)) != null && (pos = this.stickersGridAdapter.getPositionForPack(set)) >= 0 && pos < this.stickersGridAdapter.getItemCount()) {
            scrollStickersToPosition(pos, AndroidUtilities.dp(48.0f));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.showTrendingWhenSearchEmpty = false;
        }
        int a = 0;
        while (a < 3) {
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                layoutManager = this.emojiLayoutManager;
                tabStrip = this.emojiTabs;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                layoutManager = this.gifLayoutManager;
                tabStrip = this.gifTabs;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                layoutManager = this.stickersLayoutManager;
                tabStrip = this.stickersTab;
            }
            if (currentField != null) {
                currentField.searchEditText.setText("");
                if (a != currentItem || !animated) {
                    currentField.setTranslationY(AndroidUtilities.dp(36.0f) - this.searchFieldHeight);
                    if (tabStrip != null && a != 2) {
                        tabStrip.setTranslationY(0.0f);
                    }
                    if (gridView == this.stickersGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (gridView == this.gifGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (gridView == this.emojiGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                    }
                    layoutManager.scrollToPositionWithOffset(1, 0);
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (tabStrip != null && a != i) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight));
                    } else {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, -this.searchFieldHeight));
                    }
                    this.searchAnimation.setDuration(200L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.32
                        {
                            EmojiView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                                int top = 0;
                                if (firstVisPos != -1) {
                                    View firstVisView = layoutManager.findViewByPosition(firstVisPos);
                                    top = (int) (firstVisView.getTop() + gridView.getTranslationY());
                                }
                                View firstVisView2 = gridView;
                                firstVisView2.setTranslationY(0.0f);
                                if (gridView != EmojiView.this.stickersGridView) {
                                    if (gridView != EmojiView.this.gifGridView) {
                                        if (gridView == EmojiView.this.emojiGridView) {
                                            gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                                        }
                                    } else {
                                        gridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                    }
                                } else {
                                    gridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (firstVisPos != -1) {
                                    layoutManager.scrollToPositionWithOffset(firstVisPos, top - gridView.getPaddingTop());
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
            }
            a++;
            i = 2;
        }
        if (!animated) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, animated);
    }

    public void checkStickersSearchFieldScroll(boolean isLayout) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.stickersSearchField.showShadow(true, !isLayout);
                return;
            }
            SearchField searchField = this.stickersSearchField;
            if (holder.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, !isLayout);
        } else if (this.stickersSearchField == null || (recyclerListView = this.stickersGridView) == null) {
        } else {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.stickersSearchField.setTranslationY(holder2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.stickersSearchField.showShadow(false, !isLayout);
        }
    }

    public void checkBottomTabScroll(float dy) {
        int offset;
        this.lastBottomScrollDy += dy;
        if (this.pager.getCurrentItem() == 0) {
            offset = AndroidUtilities.dp(38.0f);
        } else {
            offset = AndroidUtilities.dp(48.0f);
        }
        float f = this.lastBottomScrollDy;
        if (f >= offset) {
            showBottomTab(false, true);
        } else if (f <= (-offset)) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    public void showBackspaceButton(final boolean show, boolean animated) {
        if (!show || this.backspaceButton.getTag() != null) {
            if (!show && this.backspaceButton.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!show) {
                num = 1;
            }
            imageView.setTag(num);
            int i = 0;
            float f = 1.0f;
            if (animated) {
                if (show) {
                    this.backspaceButton.setVisibility(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.backspaceButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView2 = this.backspaceButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.backspaceButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!show) {
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
                    public void onAnimationEnd(Animator animation) {
                        if (!show) {
                            EmojiView.this.backspaceButton.setVisibility(4);
                        }
                    }
                });
                this.backspaceButtonAnimation.start();
                return;
            }
            this.backspaceButton.setAlpha(show ? 1.0f : 0.0f);
            this.backspaceButton.setScaleX(show ? 1.0f : 0.0f);
            ImageView imageView5 = this.backspaceButton;
            if (!show) {
                f = 0.0f;
            }
            imageView5.setScaleY(f);
            ImageView imageView6 = this.backspaceButton;
            if (!show) {
                i = 4;
            }
            imageView6.setVisibility(i);
        }
    }

    public void showStickerSettingsButton(final boolean show, boolean animated) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView == null) {
            return;
        }
        if (show && imageView.getTag() == null) {
            return;
        }
        if (!show && this.stickerSettingsButton.getTag() != null) {
            return;
        }
        AnimatorSet animatorSet = this.stickersButtonAnimation;
        Integer num = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.stickersButtonAnimation = null;
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (!show) {
            num = 1;
        }
        imageView2.setTag(num);
        int i = 0;
        float f = 1.0f;
        if (animated) {
            if (show) {
                this.stickerSettingsButton.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.stickersButtonAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[3];
            ImageView imageView3 = this.stickerSettingsButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView3, property, fArr);
            ImageView imageView4 = this.stickerSettingsButton;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView4, property2, fArr2);
            ImageView imageView5 = this.stickerSettingsButton;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (!show) {
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
                public void onAnimationEnd(Animator animation) {
                    if (!show) {
                        EmojiView.this.stickerSettingsButton.setVisibility(4);
                    }
                }
            });
            this.stickersButtonAnimation.start();
            return;
        }
        this.stickerSettingsButton.setAlpha(show ? 1.0f : 0.0f);
        this.stickerSettingsButton.setScaleX(show ? 1.0f : 0.0f);
        ImageView imageView6 = this.stickerSettingsButton;
        if (!show) {
            f = 0.0f;
        }
        imageView6.setScaleY(f);
        ImageView imageView7 = this.stickerSettingsButton;
        if (!show) {
            i = 4;
        }
        imageView7.setVisibility(i);
    }

    public void showBottomTab(boolean show, boolean animated) {
        float f;
        float f2;
        float f3 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (!show || this.bottomTabContainer.getTag() != null) {
            if (!show && this.bottomTabContainer.getTag() != null) {
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
            if (!show) {
                num = 1;
            }
            frameLayout.setTag(num);
            float f4 = 54.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.bottomTabContainerAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout2 = this.bottomTabContainer;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                if (show) {
                    f2 = 0.0f;
                } else {
                    if (this.needEmojiSearch) {
                        f4 = 49.0f;
                    }
                    f2 = AndroidUtilities.dp(f4);
                }
                fArr[0] = f2;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                View view = this.shadowLine;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                if (!show) {
                    f3 = AndroidUtilities.dp(49.0f);
                }
                fArr2[0] = f3;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.bottomTabContainerAnimation.setDuration(200L);
                this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.bottomTabContainerAnimation.start();
                return;
            }
            FrameLayout frameLayout3 = this.bottomTabContainer;
            if (show) {
                f = 0.0f;
            } else {
                if (this.needEmojiSearch) {
                    f4 = 49.0f;
                }
                f = AndroidUtilities.dp(f4);
            }
            frameLayout3.setTranslationY(f);
            View view2 = this.shadowLine;
            if (!show) {
                f3 = AndroidUtilities.dp(49.0f);
            }
            view2.setTranslationY(f3);
        }
    }

    public void checkTabsY(int type, int dy) {
        RecyclerView.ViewHolder holder;
        if (type == 1) {
            checkEmojiTabY(this.emojiGridView, dy);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) || this.ignoreStickersScroll) {
            return;
        }
        RecyclerListView listView = getListViewForType(type);
        if (dy > 0 && listView != null && listView.getVisibility() == 0 && (holder = listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() + this.searchFieldHeight >= listView.getPaddingTop()) {
            return;
        }
        int[] iArr = this.tabsMinusDy;
        iArr[type] = iArr[type] - dy;
        if (iArr[type] > 0) {
            iArr[type] = 0;
        } else if (iArr[type] < (-AndroidUtilities.dp(288.0f))) {
            this.tabsMinusDy[type] = -AndroidUtilities.dp(288.0f);
        }
        if (type == 0) {
            updateStickerTabsPosition();
        } else {
            getTabsForType(type).setTranslationY(Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[type]));
        }
    }

    private void resetTabsY(int type) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) || type == 0) {
            return;
        }
        ScrollSlidingTabStrip tabsForType = getTabsForType(type);
        this.tabsMinusDy[type] = 0;
        tabsForType.setTranslationY(0);
    }

    public void animateTabsY(final int type) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) || type == 0) {
            return;
        }
        float tabsHeight = AndroidUtilities.dpf2(type == 1 ? 38.0f : 48.0f);
        float fraction = this.tabsMinusDy[type] / (-tabsHeight);
        if (fraction <= 0.0f || fraction >= 1.0f) {
            animateSearchField(type);
            return;
        }
        ScrollSlidingTabStrip tabStrip = getTabsForType(type);
        int endValue = fraction > 0.5f ? (int) (-Math.ceil(tabsHeight)) : 0;
        if (fraction > 0.5f) {
            animateSearchField(type, false, endValue);
        }
        if (type == 1) {
            checkEmojiShadow(endValue);
        }
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[type] == null) {
            objectAnimatorArr[type] = ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, tabStrip.getTranslationY(), endValue);
            this.tabsYAnimators[type].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    EmojiView.this.m2576lambda$animateTabsY$8$orgtelegramuiComponentsEmojiView(type, valueAnimator);
                }
            });
            this.tabsYAnimators[type].setDuration(200L);
        } else {
            objectAnimatorArr[type].setFloatValues(tabStrip.getTranslationY(), endValue);
        }
        this.tabsYAnimators[type].start();
    }

    /* renamed from: lambda$animateTabsY$8$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2576lambda$animateTabsY$8$orgtelegramuiComponentsEmojiView(int type, ValueAnimator a) {
        this.tabsMinusDy[type] = (int) ((Float) a.getAnimatedValue()).floatValue();
    }

    public void stopAnimatingTabsY(int type) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[type] != null && objectAnimatorArr[type].isRunning()) {
            this.tabsYAnimators[type].cancel();
        }
    }

    private void animateSearchField(int type) {
        RecyclerListView listView = getListViewForType(type);
        boolean z = true;
        int tabsHeight = AndroidUtilities.dp(type == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            int bottom = holder.itemView.getBottom();
            int[] iArr = this.tabsMinusDy;
            float fraction = (bottom - (iArr[type] + tabsHeight)) / this.searchFieldHeight;
            if (fraction > 0.0f || fraction < 1.0f) {
                if (fraction <= 0.5f) {
                    z = false;
                }
                animateSearchField(type, z, iArr[type]);
            }
        }
    }

    private void animateSearchField(int type, boolean visible, final int tabsMinusDy) {
        if (getListViewForType(type).findViewHolderForAdapterPosition(0) == null) {
            return;
        }
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) { // from class: org.telegram.ui.Components.EmojiView.35
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int getVerticalSnapPreference() {
                return -1;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateTimeForDeceleration(int dx) {
                return super.calculateTimeForDeceleration(dx) * 16;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference) + tabsMinusDy;
            }
        };
        smoothScroller.setTargetPosition(!visible ? 1 : 0);
        getLayoutManagerForType(type).startSmoothScroll(smoothScroller);
    }

    private ScrollSlidingTabStrip getTabsForType(int type) {
        switch (type) {
            case 0:
                return this.stickersTab;
            case 1:
                return this.emojiTabs;
            case 2:
                return this.gifTabs;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    private RecyclerListView getListViewForType(int type) {
        switch (type) {
            case 0:
                return this.stickersGridView;
            case 1:
                return this.emojiGridView;
            case 2:
                return this.gifGridView;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    private GridLayoutManager getLayoutManagerForType(int type) {
        switch (type) {
            case 0:
                return this.stickersLayoutManager;
            case 1:
                return this.emojiLayoutManager;
            case 2:
                return this.gifLayoutManager;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    public SearchField getSearchFieldForType(int type) {
        switch (type) {
            case 0:
                return this.stickersSearchField;
            case 1:
                return this.emojiSearchField;
            case 2:
                return this.gifSearchField;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    public void checkEmojiSearchFieldScroll(boolean isLayout) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
            boolean z = true;
            if (holder == null) {
                this.emojiSearchField.showShadow(true, !isLayout);
            } else {
                SearchField searchField = this.emojiSearchField;
                if (holder.itemView.getTop() >= this.emojiGridView.getPaddingTop()) {
                    z = false;
                }
                searchField.showShadow(z, !isLayout);
            }
            showEmojiShadow(false, !isLayout);
        } else if (this.emojiSearchField == null || (recyclerListView = this.emojiGridView) == null) {
        } else {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.emojiSearchField.setTranslationY(holder2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.emojiSearchField.showShadow(false, !isLayout);
            checkEmojiShadow(Math.round(this.emojiTabs.getTranslationY()));
        }
    }

    private void checkEmojiShadow(int tabsTranslationY) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[1] != null && objectAnimatorArr[1].isRunning()) {
            return;
        }
        boolean z = false;
        RecyclerView.ViewHolder holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
        int translatedBottom = AndroidUtilities.dp(38.0f) + tabsTranslationY;
        if (translatedBottom > 0 && (holder == null || holder.itemView.getBottom() < translatedBottom)) {
            z = true;
        }
        showEmojiShadow(z, true ^ this.isLayout);
    }

    public void checkEmojiTabY(View list, int dy) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder holder;
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            scrollSlidingTabStrip.setTranslationY(0);
        } else if (list.getVisibility() != 0) {
        } else {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            if (dy > 0 && (recyclerListView = this.emojiGridView) != null && recyclerListView.getVisibility() == 0 && (holder = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                if (holder.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                    return;
                }
            }
            int[] iArr = this.tabsMinusDy;
            iArr[1] = iArr[1] - dy;
            if (iArr[1] > 0) {
                iArr[1] = 0;
            } else if (iArr[1] < (-AndroidUtilities.dp(216.0f))) {
                this.tabsMinusDy[1] = -AndroidUtilities.dp(216.0f);
            }
            this.emojiTabs.setTranslationY(Math.max(-AndroidUtilities.dp(38.0f), this.tabsMinusDy[1]));
        }
    }

    public void checkGifSearchFieldScroll(boolean isLayout) {
        RecyclerListView recyclerListView;
        int position;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && (recyclerListView2.getAdapter() instanceof GifAdapter)) {
            GifAdapter adapter = (GifAdapter) this.gifGridView.getAdapter();
            if (!adapter.searchEndReached && adapter.reqId == 0 && !adapter.results.isEmpty() && (position = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && position > this.gifLayoutManager.getItemCount() - 5) {
                adapter.search(adapter.lastSearchImageString, adapter.nextSearchOffset, true, adapter.lastSearchIsEmoji, adapter.lastSearchIsEmoji);
            }
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.gifSearchField.showShadow(true, !isLayout);
                return;
            }
            SearchField searchField = this.gifSearchField;
            if (holder.itemView.getTop() < this.gifGridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, !isLayout);
        } else if (this.gifSearchField == null || (recyclerListView = this.gifGridView) == null) {
        } else {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.gifSearchField.setTranslationY(holder2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.gifSearchField.showShadow(false, !isLayout);
        }
    }

    public void scrollGifsToTop() {
        GifLayoutManager gifLayoutManager = this.gifLayoutManager;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        gifLayoutManager.scrollToPositionWithOffset((emojiViewDelegate == null || !emojiViewDelegate.isExpanded()) ? 1 : 0, 0);
        resetTabsY(2);
    }

    public void checkScroll(int type) {
        int firstVisibleItem;
        int firstVisibleItem2;
        int firstTab;
        if (type == 0) {
            if (this.ignoreStickersScroll || (firstVisibleItem2 = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1 || this.stickersGridView == null) {
                return;
            }
            if (this.favTabNum > 0) {
                firstTab = this.favTabNum;
            } else {
                int firstTab2 = this.recentTabNum;
                if (firstTab2 > 0) {
                    firstTab = this.recentTabNum;
                } else {
                    firstTab = this.stickersTabOffset;
                }
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem2), firstTab);
        } else if (type == 2) {
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter = this.gifAdapter;
            if (adapter != gifAdapter || gifAdapter.trendingSectionItem < 0 || this.gifTrendingTabNum < 0 || this.gifRecentTabNum < 0 || (firstVisibleItem = this.gifLayoutManager.findFirstVisibleItemPosition()) == -1) {
                return;
            }
            this.gifTabs.onPageScrolled(firstVisibleItem >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
        }
    }

    public void saveNewPage() {
        int newPage;
        ViewPager viewPager = this.pager;
        if (viewPager == null) {
            return;
        }
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == 2) {
            newPage = 1;
        } else if (currentItem == 1) {
            newPage = 2;
        } else {
            newPage = 0;
        }
        if (this.currentPage != newPage) {
            this.currentPage = newPage;
            MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", newPage).commit();
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    public void onPageScrolled(int position, int width, int positionOffsetPixels) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null) {
            return;
        }
        int i = 0;
        if (position == 1) {
            if (positionOffsetPixels != 0) {
                i = 2;
            }
            emojiViewDelegate.onTabOpened(i);
        } else if (position == 2) {
            emojiViewDelegate.onTabOpened(3);
        } else {
            emojiViewDelegate.onTabOpened(0);
        }
    }

    public void postBackspaceRunnable(final int time) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                EmojiView.this.m2585xb66e279a(time);
            }
        }, time);
    }

    /* renamed from: lambda$postBackspaceRunnable$9$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2585xb66e279a(int time) {
        if (!this.backspacePressed) {
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
            this.backspaceButton.performHapticFeedback(3);
        }
        this.backspaceOnce = true;
        postBackspaceRunnable(Math.max(50, time - 100));
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    public void updateEmojiTabs() {
        int newHas = !Emoji.recentEmoji.isEmpty() ? 1 : 0;
        int i = this.hasRecentEmoji;
        if (i != -1 && i == newHas) {
            return;
        }
        this.hasRecentEmoji = newHas;
        this.emojiTabs.removeTabs();
        String[] descriptions = {LocaleController.getString("RecentStickers", R.string.RecentStickers), LocaleController.getString("Emoji1", R.string.Emoji1), LocaleController.getString("Emoji2", R.string.Emoji2), LocaleController.getString("Emoji3", R.string.Emoji3), LocaleController.getString("Emoji4", R.string.Emoji4), LocaleController.getString("Emoji5", R.string.Emoji5), LocaleController.getString("Emoji6", R.string.Emoji6), LocaleController.getString("Emoji7", R.string.Emoji7), LocaleController.getString("Emoji8", R.string.Emoji8)};
        for (int a = 0; a < this.emojiIcons.length; a++) {
            if (a != 0 || !Emoji.recentEmoji.isEmpty()) {
                this.emojiTabs.addIconTab(a, this.emojiIcons[a]).setContentDescription(descriptions[a]);
            }
        }
        this.emojiTabs.updateTabStyles();
    }

    public void updateStickerTabs() {
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
        int lastPosition = this.stickersTab.getCurrentPosition();
        this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        SharedPreferences preferences = MessagesController.getEmojiSettings(this.currentAccount);
        this.featuredStickerSets.clear();
        ArrayList<TLRPC.StickerSetCovered> featured = mediaDataController.getFeaturedStickerSets();
        int N = featured.size();
        for (int a = 0; a < N; a++) {
            TLRPC.StickerSetCovered set = featured.get(a);
            if (!mediaDataController.isStickerPackInstalled(set.set.id)) {
                this.featuredStickerSets.add(set);
            }
        }
        TrendingAdapter trendingAdapter = this.trendingAdapter;
        if (trendingAdapter != null) {
            trendingAdapter.notifyDataSetChanged();
        }
        if (!featured.isEmpty() && (this.featuredStickerSets.isEmpty() || preferences.getLong("featured_hidden", 0L) == featured.get(0).set.id)) {
            int id = mediaDataController.getUnreadStickerSets().isEmpty() ? 2 : 3;
            StickerTabView trendingStickersTabView = this.stickersTab.addStickerIconTab(id, this.stickerIcons[id]);
            trendingStickersTabView.textView.setText(LocaleController.getString("FeaturedStickersShort", R.string.FeaturedStickersShort));
            trendingStickersTabView.setContentDescription(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers));
            int i = this.stickersTabOffset;
            this.trendingTabNum = i;
            this.stickersTabOffset = i + 1;
        }
        if (!this.favouriteStickers.isEmpty()) {
            int i2 = this.stickersTabOffset;
            this.favTabNum = i2;
            this.stickersTabOffset = i2 + 1;
            StickerTabView stickerTabView = this.stickersTab.addStickerIconTab(1, this.stickerIcons[1]);
            stickerTabView.textView.setText(LocaleController.getString("FavoriteStickersShort", R.string.FavoriteStickersShort));
            stickerTabView.setContentDescription(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers));
        }
        if (!this.recentStickers.isEmpty()) {
            int i3 = this.stickersTabOffset;
            this.recentTabNum = i3;
            this.stickersTabOffset = i3 + 1;
            StickerTabView stickerTabView2 = this.stickersTab.addStickerIconTab(0, this.stickerIcons[0]);
            stickerTabView2.textView.setText(LocaleController.getString("RecentStickersShort", R.string.RecentStickersShort));
            stickerTabView2.setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
        }
        this.stickerSets.clear();
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
        this.groupStickerSet = null;
        this.groupStickerPackPosition = -1;
        this.groupStickerPackNum = -10;
        ArrayList<TLRPC.TL_messages_stickerSet> packs = mediaDataController.getStickerSets(0);
        int i4 = 0;
        while (true) {
            TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i4 >= stickerSetCoveredArr.length) {
                break;
            }
            TLRPC.StickerSetCovered installingStickerSet = stickerSetCoveredArr[i4];
            if (installingStickerSet != null) {
                TLRPC.TL_messages_stickerSet pack = mediaDataController.getStickerSetById(installingStickerSet.set.id);
                if (pack != null && !pack.set.archived) {
                    this.primaryInstallingStickerSets[i4] = null;
                } else {
                    TLRPC.TL_messages_stickerSet set2 = new TLRPC.TL_messages_stickerSet();
                    set2.set = installingStickerSet.set;
                    if (installingStickerSet.cover != null) {
                        set2.documents.add(installingStickerSet.cover);
                    } else if (!installingStickerSet.covers.isEmpty()) {
                        set2.documents.addAll(installingStickerSet.covers);
                    }
                    if (!set2.documents.isEmpty()) {
                        this.stickerSets.add(set2);
                    }
                }
            }
            i4++;
        }
        this.premiumStickers.clear();
        ArrayList<TLRPC.TL_messages_stickerSet> packs2 = MessagesController.getInstance(this.currentAccount).filterPremiumStickers(packs);
        for (int a2 = 0; a2 < packs2.size(); a2++) {
            TLRPC.TL_messages_stickerSet pack2 = packs2.get(a2);
            if (!pack2.set.archived && pack2.documents != null && !pack2.documents.isEmpty()) {
                this.stickerSets.add(pack2);
                if (!MessagesController.getInstance(this.currentAccount).premiumLocked && UserConfig.getInstance(this.currentAccount).isPremium()) {
                    for (int i5 = 0; i5 < pack2.documents.size(); i5++) {
                        if (MessageObject.isPremiumSticker(pack2.documents.get(i5))) {
                            this.premiumStickers.add(pack2.documents.get(i5));
                        }
                    }
                }
            }
        }
        if (!this.premiumStickers.isEmpty()) {
            int i6 = this.stickersTabOffset;
            this.premiumTabNum = i6;
            this.stickersTabOffset = i6 + 1;
            StickerTabView stickerTabView3 = this.stickersTab.addStickerIconTab(4, PremiumGradient.getInstance().premiumStarMenuDrawable2);
            stickerTabView3.textView.setText(LocaleController.getString("PremiumStickersShort", R.string.PremiumStickersShort));
            stickerTabView3.setContentDescription(LocaleController.getString("PremiumStickers", R.string.PremiumStickers));
        }
        if (this.info != null) {
            long hiddenStickerSetId = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1L);
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
            if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                this.groupStickersHidden = hiddenStickerSetId != -1;
            } else if (this.info.stickerset != null) {
                this.groupStickersHidden = hiddenStickerSetId == this.info.stickerset.id;
            }
            if (this.info.stickerset != null) {
                TLRPC.TL_messages_stickerSet pack3 = mediaDataController.getGroupStickerSetById(this.info.stickerset);
                if (pack3 != null && pack3.documents != null && !pack3.documents.isEmpty() && pack3.set != null) {
                    TLRPC.TL_messages_stickerSet set3 = new TLRPC.TL_messages_stickerSet();
                    set3.documents = pack3.documents;
                    set3.packs = pack3.packs;
                    set3.set = pack3.set;
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(set3);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, set3);
                    }
                    if (this.info.can_set_stickers) {
                        tL_messages_stickerSet = set3;
                    }
                    this.groupStickerSet = tL_messages_stickerSet;
                }
            } else if (this.info.can_set_stickers) {
                TLRPC.TL_messages_stickerSet pack4 = new TLRPC.TL_messages_stickerSet();
                if (this.groupStickersHidden) {
                    this.groupStickerPackNum = this.stickerSets.size();
                    this.stickerSets.add(pack4);
                } else {
                    this.groupStickerPackNum = 0;
                    this.stickerSets.add(0, pack4);
                }
            }
        }
        int a3 = 0;
        while (a3 < this.stickerSets.size()) {
            if (a3 == this.groupStickerPackNum) {
                TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
                if (chat2 == null) {
                    this.stickerSets.remove(0);
                    a3--;
                } else {
                    this.hasChatStickers = true;
                    this.stickersTab.addStickerTab(chat2);
                }
            } else {
                TLRPC.TL_messages_stickerSet stickerSet = this.stickerSets.get(a3);
                TLRPC.Document document = stickerSet.documents.get(0);
                TLObject thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
                if (thumb == null || stickerSet.set.gifs) {
                    thumb = document;
                }
                this.stickersTab.addStickerTab(thumb, document, stickerSet).setContentDescription(stickerSet.set.title + ", " + LocaleController.getString("AccDescrStickerSet", R.string.AccDescrStickerSet));
            }
            a3++;
        }
        this.stickersTab.commitUpdate();
        this.stickersTab.updateTabStyles();
        if (lastPosition != 0) {
            this.stickersTab.onPageScrolled(lastPosition, lastPosition);
        }
        checkPanels();
    }

    private void checkPanels() {
        int position;
        int firstTab;
        if (this.stickersTab != null && (position = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1) {
            if (this.favTabNum > 0) {
                firstTab = this.favTabNum;
            } else {
                int firstTab2 = this.recentTabNum;
                if (firstTab2 > 0) {
                    firstTab = this.recentTabNum;
                } else {
                    firstTab = this.stickersTabOffset;
                }
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position), firstTab);
        }
    }

    private void updateGifTabs() {
        int lastPosition = this.gifTabs.getCurrentPosition();
        int i = this.gifRecentTabNum;
        boolean wasRecentTabSelected = lastPosition == i;
        boolean hadRecent = i >= 0;
        boolean hasRecent = !this.recentGifs.isEmpty();
        this.gifTabs.beginUpdate(false);
        int gifTabsCount = 0;
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        if (hasRecent) {
            int gifTabsCount2 = 0 + 1;
            this.gifRecentTabNum = 0;
            this.gifTabs.addIconTab(0, this.gifIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
            gifTabsCount = gifTabsCount2;
        }
        int gifTabsCount3 = gifTabsCount + 1;
        this.gifTrendingTabNum = gifTabsCount;
        this.gifTabs.addIconTab(1, this.gifIcons[1]).setContentDescription(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs));
        this.gifFirstEmojiTabNum = gifTabsCount3;
        AndroidUtilities.dp(13.0f);
        AndroidUtilities.dp(11.0f);
        List<String> gifSearchEmojies = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
        int N = gifSearchEmojies.size();
        for (int i2 = 0; i2 < N; i2++) {
            String emoji = gifSearchEmojies.get(i2);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emoji);
            if (emojiDrawable != null) {
                gifTabsCount3++;
                TLRPC.Document document = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji);
                View iconTab = this.gifTabs.addEmojiTab(i2 + 3, emojiDrawable, document);
                iconTab.setContentDescription(emoji);
            }
        }
        this.gifTabs.commitUpdate();
        this.gifTabs.updateTabStyles();
        if (wasRecentTabSelected && !hasRecent) {
            this.gifTabs.selectTab(this.gifTrendingTabNum);
        } else if (ViewCompat.isLaidOut(this.gifTabs)) {
            if (hasRecent && !hadRecent) {
                this.gifTabs.onPageScrolled(lastPosition + 1, 0);
            } else if (!hasRecent && hadRecent) {
                this.gifTabs.onPageScrolled(lastPosition - 1, 0);
            }
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, null, document, (int) (System.currentTimeMillis() / 1000), false);
        boolean wasEmpty = this.recentStickers.isEmpty();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (wasEmpty) {
            updateStickerTabs();
        }
    }

    public void addRecentGif(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        boolean wasEmpty = this.recentGifs.isEmpty();
        updateRecentGifs();
        if (wasEmpty) {
            updateStickerTabs();
        }
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
                background.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackground), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            }
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            this.emojiTabsShadow.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
        }
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        if (emojiColorPickerView != null) {
            Theme.setDrawableColor(emojiColorPickerView.backgroundDrawable, getThemedColor(Theme.key_dialogBackground));
            Theme.setDrawableColor(this.pickerView.arrowDrawable, getThemedColor(Theme.key_dialogBackground));
        }
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                searchField = this.stickersSearchField;
            } else if (a == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
                searchField.shadowView.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), Theme.key_chat_emojiSearchBackground);
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(getThemedColor(Theme.key_chat_emojiSearchIcon));
                searchField.searchEditText.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            }
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(getThemedColor(Theme.key_chat_emojiPanelNewTrending));
        }
        RecyclerListView recyclerListView = this.emojiGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        }
        RecyclerListView recyclerListView2 = this.stickersGridView;
        if (recyclerListView2 != null) {
            recyclerListView2.setGlowColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
            this.stickersTab.setUnderlineColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            this.stickersTab.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.gifTabs;
        if (scrollSlidingTabStrip3 != null) {
            scrollSlidingTabStrip3.setIndicatorColor(getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
            this.gifTabs.setUnderlineColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            this.gifTabs.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        }
        ImageView imageView = this.backspaceButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
            if (this.emojiSearchField == null) {
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor(Theme.key_chat_emojiPanelBackground), false);
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor(Theme.key_chat_emojiPanelBackground), true);
            }
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.searchButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(getThemedColor(Theme.key_chat_gifSaveHintBackground));
            this.mediaBanTooltip.setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.progressEmptyView.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
            this.gifSearchAdapter.progressEmptyView.textView.setTextColor(getThemedColor(Theme.key_chat_emojiPanelEmptyText));
            this.gifSearchAdapter.progressEmptyView.progressView.setProgressColor(getThemedColor(Theme.key_progressCircle));
        }
        int a2 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (a2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[a2], getThemedColor(Theme.key_chat_emojiBottomPanelIcon), false);
            Theme.setEmojiDrawableColor(this.tabIcons[a2], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            a2++;
        }
        int a3 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.emojiIcons;
            if (a3 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[a3], getThemedColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.emojiIcons[a3], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            a3++;
        }
        int a4 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.stickerIcons;
            if (a4 >= drawableArr3.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr3[a4], getThemedColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[a4], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            a4++;
        }
        int a5 = 0;
        while (true) {
            Drawable[] drawableArr4 = this.gifIcons;
            if (a5 >= drawableArr4.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr4[a5], getThemedColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.gifIcons[a5], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            a5++;
        }
        Drawable drawable = this.searchIconDrawable;
        if (drawable != null) {
            Theme.setEmojiDrawableColor(drawable, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), false);
            Theme.setEmojiDrawableColor(this.searchIconDrawable, getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
        }
        Drawable drawable2 = this.searchIconDotDrawable;
        if (drawable2 != null) {
            Theme.setEmojiDrawableColor(drawable2, getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine), false);
            Theme.setEmojiDrawableColor(this.searchIconDotDrawable, getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine), true);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation(AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(R.drawable.smiles_popup);
                getBackground().setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackground), PorterDuff.Mode.MULTIPLY));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
        this.isLayout = false;
        setTranslationY(getTranslationY());
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        View parent = (View) getParent();
        if (parent != null) {
            int newHeight = bottom - top;
            int newHeight2 = parent.getHeight();
            if (this.lastNotifyHeight != newHeight || this.lastNotifyHeight2 != newHeight2) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY(AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null && newHeight <= this.lastNotifyHeight) {
                    this.bottomTabContainer.setTranslationY(0.0f);
                }
                this.lastNotifyHeight = newHeight;
                this.lastNotifyHeight2 = newHeight2;
            }
        }
        super.onLayout(changed, left, top, right, bottom);
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

    public void setDragListener(DragListener listener) {
        this.dragListener = listener;
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean value) {
        this.forseMultiwindowLayout = value;
    }

    public void onOpen(boolean forceEmoji) {
        if (this.currentPage != 0 && this.currentChatId != 0) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || forceEmoji || this.views.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, !forceEmoji);
                return;
            }
            return;
        }
        int i = this.currentPage;
        if (i == 1) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(true, false);
            if (this.pager.getCurrentItem() != 2) {
                this.pager.setCurrentItem(2, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            if (scrollSlidingTabStrip != null) {
                this.firstTabUpdate = true;
                int i2 = this.favTabNum;
                if (i2 >= 0) {
                    scrollSlidingTabStrip.selectTab(i2);
                } else {
                    int i3 = this.recentTabNum;
                    if (i3 >= 0) {
                        scrollSlidingTabStrip.selectTab(i3);
                    } else {
                        scrollSlidingTabStrip.selectTab(this.stickersTabOffset);
                    }
                }
                this.firstTabUpdate = false;
                this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
            }
        } else if (i == 2) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
            if (scrollSlidingTabStrip2 != null) {
                scrollSlidingTabStrip2.selectTab(0);
            }
        }
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
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.this.m2584xf626a476();
                }
            });
        }
    }

    /* renamed from: lambda$onAttachedToWindow$10$org-telegram-ui-Components-EmojiView */
    public /* synthetic */ void m2584xf626a476() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
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
        if (emojiPopupWindow != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean isGif) {
        if (isGif) {
            updateRecentGifs();
            return;
        }
        int previousCount = this.recentStickers.size();
        int previousCount2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        for (int a = 0; a < this.favouriteStickers.size(); a++) {
            TLRPC.Document favSticker = this.favouriteStickers.get(a);
            int b = 0;
            while (true) {
                if (b < this.recentStickers.size()) {
                    TLRPC.Document recSticker = this.recentStickers.get(b);
                    if (recSticker.dc_id != favSticker.dc_id || recSticker.id != favSticker.id) {
                        b++;
                    } else {
                        this.recentStickers.remove(b);
                        break;
                    }
                }
            }
        }
        int a2 = this.currentAccount;
        if (MessagesController.getInstance(a2).premiumLocked) {
            int a3 = 0;
            while (a3 < this.favouriteStickers.size()) {
                if (MessageObject.isPremiumSticker(this.favouriteStickers.get(a3))) {
                    this.favouriteStickers.remove(a3);
                    a3--;
                }
                a3++;
            }
            int a4 = 0;
            while (a4 < this.recentStickers.size()) {
                if (MessageObject.isPremiumSticker(this.recentStickers.get(a4))) {
                    this.recentStickers.remove(a4);
                    a4--;
                }
                a4++;
            }
        }
        if (previousCount != this.recentStickers.size() || previousCount2 != this.favouriteStickers.size()) {
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
        int prevSize = this.recentGifs.size();
        long prevHash = MediaDataController.calcDocumentsHash(this.recentGifs, Integer.MAX_VALUE);
        ArrayList<TLRPC.Document> recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs;
        long newHash = MediaDataController.calcDocumentsHash(recentGifs, Integer.MAX_VALUE);
        if ((this.gifTabs != null && prevSize == 0 && !this.recentGifs.isEmpty()) || (prevSize != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((prevSize != this.recentGifs.size() || prevHash != newHash) && (gifAdapter = this.gifAdapter) != null) {
            gifAdapter.notifyDataSetChanged();
        }
    }

    public void setStickersBanned(boolean value, long chatId) {
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip == null) {
            return;
        }
        if (value) {
            this.currentChatId = chatId;
        } else {
            this.currentChatId = 0L;
        }
        View view = pagerSlidingTabStrip.getTab(2);
        if (view != null) {
            view.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
            if (this.currentChatId != 0 && this.pager.getCurrentItem() != 0) {
                showBackspaceButton(true, true);
                showStickerSettingsButton(false, true);
                this.pager.setCurrentItem(0, false);
            }
        }
    }

    public void showStickerBanHint(boolean gif) {
        TLRPC.Chat chat;
        if (this.mediaBanTooltip.getVisibility() == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId))) == null) {
            return;
        }
        if (!ChatObject.hasAdminRights(chat) && chat.default_banned_rights != null && chat.default_banned_rights.send_stickers) {
            if (gif) {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", R.string.GlobalAttachGifRestricted));
            } else {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", R.string.GlobalAttachStickersRestricted));
            }
        } else if (chat.banned_rights == null) {
            return;
        } else {
            if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                if (gif) {
                    this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", R.string.AttachGifRestrictedForever));
                } else {
                    this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", R.string.AttachStickersRestrictedForever));
                }
            } else if (gif) {
                this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
            } else {
                this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
            }
        }
        this.mediaBanTooltip.setVisibility(0);
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, 0.0f, 1.0f));
        AnimatorSet.addListener(new AnonymousClass36());
        AnimatorSet.setDuration(300L);
        AnimatorSet.start();
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$36 */
    /* loaded from: classes5.dex */
    public class AnonymousClass36 extends AnimatorListenerAdapter {
        AnonymousClass36() {
            EmojiView.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$36$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.AnonymousClass36.this.m2586lambda$onAnimationEnd$0$orgtelegramuiComponentsEmojiView$36();
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-EmojiView$36 */
        public /* synthetic */ void m2586lambda$onAnimationEnd$0$orgtelegramuiComponentsEmojiView$36() {
            if (EmojiView.this.mediaBanTooltip == null) {
                return;
            }
            AnimatorSet AnimatorSet1 = new AnimatorSet();
            AnimatorSet1.playTogether(ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, 0.0f));
            AnimatorSet1.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.36.1
                {
                    AnonymousClass36.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation1) {
                    if (EmojiView.this.mediaBanTooltip != null) {
                        EmojiView.this.mediaBanTooltip.setVisibility(4);
                    }
                }
            });
            AnimatorSet1.setDuration(300L);
            AnimatorSet1.start();
        }
    }

    private void updateVisibleTrendingSets() {
        boolean forceInstalled;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView == null) {
            return;
        }
        try {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.stickersGridView.getChildAt(a);
                if (child instanceof FeaturedStickerSetInfoCell) {
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(child);
                    if (holder != null) {
                        FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) child;
                        ArrayList<Long> unreadStickers = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                        TLRPC.StickerSetCovered stickerSetCovered = cell.getStickerSet();
                        boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                        int i = 0;
                        while (true) {
                            TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
                            if (i >= stickerSetCoveredArr.length) {
                                forceInstalled = false;
                                break;
                            } else if (stickerSetCoveredArr[i] == null || stickerSetCoveredArr[i].set.id != stickerSetCovered.set.id) {
                                i++;
                            } else {
                                forceInstalled = true;
                                break;
                            }
                        }
                        cell.setStickerSet(stickerSetCovered, unread, true, 0, 0, forceInstalled);
                        if (unread) {
                            MediaDataController.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                        }
                        boolean installing = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                        boolean removing = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                        if (installing || removing) {
                            if (installing && cell.isInstalled()) {
                                this.installingStickerSets.remove(stickerSetCovered.set.id);
                                installing = false;
                            } else if (removing && !cell.isInstalled()) {
                                this.removingStickerSets.remove(stickerSetCovered.set.id);
                            }
                        }
                        cell.setAddDrawProgress(!forceInstalled && installing, true);
                    }
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
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (((Integer) args[0]).intValue() == 0) {
                updateStickerTabs();
                updateVisibleTrendingSets();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad) {
            boolean isGif = ((Boolean) args[0]).booleanValue();
            int type = ((Integer) args[1]).intValue();
            if (isGif || type == 0 || type == 2) {
                checkDocuments(isGif);
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
            updateVisibleTrendingSets();
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int count = pagerSlidingTabStrip.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.typeTabs.getChildAt(a).invalidate();
                }
            }
            updateStickerTabs();
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && chatFull.stickerset != null && this.info.stickerset.id == ((Long) args[0]).longValue()) {
                updateStickerTabs();
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int count2 = recyclerListView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child = this.stickersGridView.getChildAt(a2);
                    if ((child instanceof StickerSetNameCell) || (child instanceof StickerEmojiCell)) {
                        child.invalidate();
                    }
                }
            }
            EmojiColorPickerView emojiColorPickerView = this.pickerView;
            if (emojiColorPickerView != null) {
                emojiColorPickerView.invalidate();
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip != null) {
                scrollSlidingTabStrip.invalidateTabs();
            }
        } else if (id == NotificationCenter.newEmojiSuggestionsAvailable) {
            if (this.emojiGridView != null && this.needEmojiSearch) {
                if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                    EmojiSearchAdapter emojiSearchAdapter = this.emojiSearchAdapter;
                    emojiSearchAdapter.search(emojiSearchAdapter.lastSearchEmojiString);
                }
            }
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            updateStickerTabs();
        }
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes5.dex */
    public class TrendingAdapter extends RecyclerListView.SelectionAdapter {
        private TrendingAdapter() {
            EmojiView.this = r1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BackupImageView imageView = new BackupImageView(EmojiView.this.getContext()) { // from class: org.telegram.ui.Components.EmojiView.TrendingAdapter.1
                {
                    TrendingAdapter.this = this;
                }

                @Override // org.telegram.ui.Components.BackupImageView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    TLRPC.StickerSetCovered set = (TLRPC.StickerSetCovered) getTag();
                    if (MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(set.set.id) && EmojiView.this.dotPaint != null) {
                        int x = canvas.getWidth() - AndroidUtilities.dp(8.0f);
                        int y = AndroidUtilities.dp(14.0f);
                        canvas.drawCircle(x, y, AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
                    }
                }
            };
            imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            imageView.setLayerNum(1);
            imageView.setAspectFit(true);
            imageView.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f)));
            return new RecyclerListView.Holder(imageView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Document document;
            ImageLocation imageLocation;
            BackupImageView imageView = (BackupImageView) holder.itemView;
            TLRPC.StickerSetCovered set = (TLRPC.StickerSetCovered) EmojiView.this.featuredStickerSets.get(position);
            imageView.setTag(set);
            TLRPC.Document document2 = set.cover;
            if (set.covers.isEmpty()) {
                document = document2;
            } else {
                TLRPC.Document document3 = set.covers.get(0);
                document = document3;
            }
            TLObject object = FileLoader.getClosestPhotoSizeWithSize(set.set.thumbs, 90);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(set.set.thumbs, Theme.key_emptyListPlaceholder, 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            TLObject object2 = (object == null || MessageObject.isVideoSticker(document)) ? document : object;
            if (object2 instanceof TLRPC.Document) {
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                imageLocation = ImageLocation.getForDocument(thumb, document);
            } else if (object2 instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize thumb2 = (TLRPC.PhotoSize) object2;
                int thumbVersion = set.set.thumb_version;
                imageLocation = ImageLocation.getForSticker(thumb2, document, thumbVersion);
            } else {
                return;
            }
            if (imageLocation == null) {
                return;
            }
            if ((object2 instanceof TLRPC.Document) && (MessageObject.isAnimatedStickerDocument(document, true) || MessageObject.isVideoSticker(document))) {
                if (svgThumb != null) {
                    imageView.setImage(ImageLocation.getForDocument(document), "30_30", svgThumb, 0, set);
                } else {
                    imageView.setImage(ImageLocation.getForDocument(document), "30_30", imageLocation, (String) null, 0, set);
                }
            } else if (imageLocation.imageType == 1) {
                imageView.setImage(imageLocation, "30_30", "tgs", svgThumb, set);
            } else {
                imageView.setImage(imageLocation, (String) null, "webp", svgThumb, set);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return EmojiView.this.featuredStickerSets.size();
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
            EmojiView.this = r1;
            this.context = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.itemView instanceof RecyclerListView;
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
                if (object instanceof String) {
                    if ("trend1".equals(object)) {
                        return 5;
                    }
                    if ("trend2".equals(object)) {
                        return 6;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }

        public int getTabForPosition(int position) {
            Object cacheObject = this.cache.get(position);
            if ("search".equals(cacheObject) || "trend1".equals(cacheObject) || "trend2".equals(cacheObject)) {
                if (EmojiView.this.favTabNum >= 0) {
                    return EmojiView.this.favTabNum;
                }
                if (EmojiView.this.recentTabNum >= 0) {
                    return EmojiView.this.recentTabNum;
                }
                return 0;
            }
            if (position == 0) {
                position = 1;
            }
            if (this.stickersPerRow == 0) {
                int width = EmojiView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            int row = this.positionToRow.get(position, Integer.MIN_VALUE);
            if (row == Integer.MIN_VALUE) {
                return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
            }
            Object pack = this.rowStartPack.get(row);
            if (pack instanceof String) {
                if ("premium".equals(pack)) {
                    return EmojiView.this.premiumTabNum;
                }
                return "recent".equals(pack) ? EmojiView.this.recentTabNum : EmojiView.this.favTabNum;
            }
            TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) pack;
            int idx = EmojiView.this.stickerSets.indexOf(set);
            return EmojiView.this.stickersTabOffset + idx;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, true) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.1
                        {
                            StickersGridAdapter.this = this;
                        }

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
                    final StickerSetNameCell nameCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    view = nameCell;
                    nameCell.setOnIconClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            EmojiView.StickersGridAdapter.this.m2599x5fec4d27(nameCell, view2);
                        }
                    });
                    break;
                case 3:
                    view = new StickerSetGroupInfoCell(this.context);
                    ((StickerSetGroupInfoCell) view).setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            EmojiView.StickersGridAdapter.this.m2600xecd96446(view2);
                        }
                    });
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 5:
                    view = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    ((StickerSetNameCell) view).setOnIconClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            EmojiView.StickersGridAdapter.this.m2601x79c67b65(view2);
                        }
                    });
                    break;
                case 6:
                    RecyclerListView horizontalListView = new RecyclerListView(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.2
                        {
                            StickersGridAdapter.this = this;
                        }

                        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1) || canScrollHorizontally(1));
                                EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    horizontalListView.setSelectorRadius(AndroidUtilities.dp(4.0f));
                    horizontalListView.setSelectorDrawableColor(EmojiView.this.getThemedColor(Theme.key_listSelector));
                    horizontalListView.setTag(9);
                    horizontalListView.setItemAnimator(null);
                    horizontalListView.setLayoutAnimation(null);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.3
                        {
                            StickersGridAdapter.this = this;
                        }

                        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    layoutManager.setOrientation(0);
                    horizontalListView.setLayoutManager(layoutManager);
                    horizontalListView.setAdapter(EmojiView.this.trendingAdapter = new TrendingAdapter());
                    horizontalListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view2, int i) {
                            EmojiView.StickersGridAdapter.this.m2602x6b39284(view2, i);
                        }
                    });
                    view = horizontalListView;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(52.0f)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-EmojiView$StickersGridAdapter */
        public /* synthetic */ void m2599x5fec4d27(StickerSetNameCell nameCell, View v) {
            RecyclerView.ViewHolder holder;
            if (EmojiView.this.stickersGridView.indexOfChild(nameCell) != -1 && (holder = EmojiView.this.stickersGridView.getChildViewHolder(nameCell)) != null) {
                if (holder.getAdapterPosition() == EmojiView.this.groupStickerPackPosition) {
                    if (EmojiView.this.groupStickerSet != null) {
                        if (EmojiView.this.delegate != null) {
                            EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
                            return;
                        }
                        return;
                    }
                    SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                    edit.putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0L).apply();
                    EmojiView.this.updateStickerTabs();
                    if (EmojiView.this.stickersGridAdapter != null) {
                        EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                Object object = this.cache.get(holder.getAdapterPosition());
                if (object == EmojiView.this.recentStickers) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this.context).setTitle(LocaleController.getString((int) R.string.ClearRecentStickersAlertTitle)).setMessage(LocaleController.getString((int) R.string.ClearRecentStickersAlertMessage)).setPositiveButton(LocaleController.getString((int) R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            EmojiView.StickersGridAdapter.this.m2598xd2ff3608(dialogInterface, i);
                        }
                    }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).create();
                    alertDialog.show();
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    }
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-EmojiView$StickersGridAdapter */
        public /* synthetic */ void m2598xd2ff3608(DialogInterface dialog, int which) {
            MediaDataController.getInstance(EmojiView.this.currentAccount).clearRecentStickers();
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-Components-EmojiView$StickersGridAdapter */
        public /* synthetic */ void m2600xecd96446(View v) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-Components-EmojiView$StickersGridAdapter */
        public /* synthetic */ void m2601x79c67b65(View v) {
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            ArrayList<TLRPC.StickerSetCovered> featured = mediaDataController.getFeaturedStickerSets();
            if (!featured.isEmpty()) {
                MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("featured_hidden", featured.get(0).set.id).commit();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyItemRangeRemoved(1, 2);
                }
                EmojiView.this.updateStickerTabs();
            }
        }

        /* renamed from: lambda$onCreateViewHolder$4$org-telegram-ui-Components-EmojiView$StickersGridAdapter */
        public /* synthetic */ void m2602x6b39284(View view1, int position) {
            EmojiView.this.openTrendingStickers((TLRPC.StickerSetCovered) view1.getTag());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<TLRPC.Document> documents;
            String str;
            int i;
            int itemViewType = holder.getItemViewType();
            int icon = R.drawable.msg_close;
            boolean z = false;
            int i2 = 1;
            switch (itemViewType) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    cell.setSticker(sticker, this.cacheParents.get(position), false);
                    cell.setRecent(EmojiView.this.recentStickers.contains(sticker));
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
                            documents = ((TLRPC.TL_messages_stickerSet) pack).documents;
                        } else if (pack instanceof String) {
                            documents = "recent".equals(pack) ? EmojiView.this.recentStickers : EmojiView.this.favouriteStickers;
                        } else {
                            documents = null;
                        }
                        if (documents == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (!documents.isEmpty()) {
                            int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(documents.size() / this.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i2 = height;
                            }
                            cell2.setHeight(i2);
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
                    if (position == EmojiView.this.groupStickerPackPosition) {
                        if (!EmojiView.this.groupStickersHidden || EmojiView.this.groupStickerSet != null) {
                            if (EmojiView.this.groupStickerSet != null) {
                                icon = R.drawable.msg_mini_customize;
                            }
                        } else {
                            icon = 0;
                        }
                        TLRPC.Chat chat = EmojiView.this.info != null ? MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Long.valueOf(EmojiView.this.info.id)) : null;
                        Object[] objArr = new Object[1];
                        objArr[0] = chat != null ? chat.title : "Group Stickers";
                        cell3.setText(LocaleController.formatString("CurrentGroupStickers", R.string.CurrentGroupStickers, objArr), icon);
                        return;
                    }
                    Object object = this.cache.get(position);
                    if (!(object instanceof TLRPC.TL_messages_stickerSet)) {
                        if (object != EmojiView.this.recentStickers) {
                            if (object != EmojiView.this.favouriteStickers) {
                                if (object == EmojiView.this.premiumStickers) {
                                    cell3.setText(LocaleController.getString("PremiumStickers", R.string.PremiumStickers), 0);
                                    return;
                                }
                                return;
                            }
                            cell3.setText(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers), 0);
                            return;
                        }
                        cell3.setText(LocaleController.getString("RecentStickers", R.string.RecentStickers), R.drawable.msg_close, LocaleController.getString((int) R.string.ClearRecentStickersAlertTitle));
                        return;
                    }
                    TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                    if (set.set != null) {
                        cell3.setText(set.set.title, 0);
                        return;
                    }
                    return;
                case 3:
                    StickerSetGroupInfoCell cell4 = (StickerSetGroupInfoCell) holder.itemView;
                    if (position == this.totalItems - 1) {
                        z = true;
                    }
                    cell4.setIsLast(z);
                    return;
                case 4:
                default:
                    return;
                case 5:
                    StickerSetNameCell cell5 = (StickerSetNameCell) holder.itemView;
                    if (MediaDataController.getInstance(EmojiView.this.currentAccount).loadFeaturedPremium) {
                        i = R.string.FeaturedStickersPremium;
                        str = "FeaturedStickersPremium";
                    } else {
                        i = R.string.FeaturedStickers;
                        str = "FeaturedStickers";
                    }
                    cell5.setText(LocaleController.getString(str, i), R.drawable.msg_close);
                    return;
            }
        }

        private void updateItems() {
            int width;
            ArrayList<TLRPC.TL_messages_stickerSet> packs;
            String key;
            ArrayList<TLRPC.Document> documents;
            int width2 = EmojiView.this.getMeasuredWidth();
            if (width2 == 0) {
                width2 = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = width2 / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            int i = 0;
            this.totalItems = 0;
            ArrayList<TLRPC.TL_messages_stickerSet> packs2 = EmojiView.this.stickerSets;
            int startRow = 0;
            int a = -5;
            while (a < packs2.size()) {
                TLRPC.TL_messages_stickerSet pack = null;
                if (a == -5) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i2 = this.totalItems;
                    this.totalItems = i2 + 1;
                    sparseArray.put(i2, "search");
                    startRow++;
                    width = width2;
                    packs = packs2;
                } else if (a == -4) {
                    MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
                    SharedPreferences preferences = MessagesController.getEmojiSettings(EmojiView.this.currentAccount);
                    ArrayList<TLRPC.StickerSetCovered> featured = mediaDataController.getFeaturedStickerSets();
                    if (!EmojiView.this.featuredStickerSets.isEmpty() && preferences.getLong("featured_hidden", 0L) != featured.get(i).set.id) {
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        sparseArray2.put(i3, "trend1");
                        SparseArray<Object> sparseArray3 = this.cache;
                        int i4 = this.totalItems;
                        this.totalItems = i4 + 1;
                        sparseArray3.put(i4, "trend2");
                        startRow += 2;
                        width = width2;
                        packs = packs2;
                    }
                    width = width2;
                    packs = packs2;
                } else {
                    if (a == -3) {
                        documents = EmojiView.this.favouriteStickers;
                        key = "fav";
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                    } else if (a == -2) {
                        documents = EmojiView.this.recentStickers;
                        key = "recent";
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                    } else if (a == -1) {
                        documents = EmojiView.this.premiumStickers;
                        key = "premium";
                        this.packStartPosition.put("premium", Integer.valueOf(this.totalItems));
                    } else {
                        key = null;
                        pack = packs2.get(a);
                        documents = pack.documents;
                        this.packStartPosition.put(pack, Integer.valueOf(this.totalItems));
                    }
                    if (a == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (documents.isEmpty()) {
                            this.rowStartPack.put(startRow, pack);
                            int startRow2 = startRow + 1;
                            this.positionToRow.put(this.totalItems, startRow);
                            this.rowStartPack.put(startRow2, pack);
                            this.positionToRow.put(this.totalItems + 1, startRow2);
                            SparseArray<Object> sparseArray4 = this.cache;
                            int i5 = this.totalItems;
                            this.totalItems = i5 + 1;
                            sparseArray4.put(i5, pack);
                            SparseArray<Object> sparseArray5 = this.cache;
                            int i6 = this.totalItems;
                            this.totalItems = i6 + 1;
                            sparseArray5.put(i6, "group");
                            width = width2;
                            packs = packs2;
                            startRow = startRow2 + 1;
                        }
                    }
                    if (!documents.isEmpty()) {
                        packs = packs2;
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
                            } else if (a == -1) {
                                this.rowStartPack.put(startRow + b2, "premium");
                            } else if (a == -2) {
                                this.rowStartPack.put(startRow + b2, "recent");
                            } else {
                                this.rowStartPack.put(startRow + b2, "fav");
                            }
                        }
                        int b3 = this.totalItems;
                        this.totalItems = b3 + (this.stickersPerRow * count) + 1;
                        startRow += count + 1;
                    }
                    width = width2;
                    packs = packs2;
                }
                a++;
                packs2 = packs;
                width2 = width;
                i = 0;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateItems();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateItems();
            super.notifyDataSetChanged();
        }
    }

    /* loaded from: classes5.dex */
    public class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int itemCount;
        private SparseIntArray positionToSection;
        private SparseIntArray sectionToPosition;

        private EmojiGridAdapter() {
            EmojiView.this = r1;
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            return position;
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
                    EmojiView emojiView = EmojiView.this;
                    view = new ImageViewEmoji(emojiView.getContext());
                    break;
                case 1:
                    view = new StickerSetNameCell(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
                    break;
                default:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean recent;
            String code;
            String coloredCode;
            switch (holder.getItemViewType()) {
                case 0:
                    ImageViewEmoji imageView = (ImageViewEmoji) holder.itemView;
                    if (EmojiView.this.needEmojiSearch) {
                        position--;
                    }
                    int count = Emoji.recentEmoji.size();
                    if (position < count) {
                        coloredCode = Emoji.recentEmoji.get(position);
                        code = coloredCode;
                        recent = true;
                    } else {
                        int a = 0;
                        while (true) {
                            if (a >= EmojiData.dataColored.length) {
                                code = null;
                                coloredCode = null;
                            } else {
                                int size = EmojiData.dataColored[a].length + 1;
                                if (position < count + size) {
                                    String code2 = EmojiData.dataColored[a][(position - count) - 1];
                                    String color = Emoji.emojiColor.get(code2);
                                    if (color != null) {
                                        code = code2;
                                        coloredCode = EmojiView.addColorToCode(code2, color);
                                    } else {
                                        code = code2;
                                        coloredCode = code2;
                                    }
                                } else {
                                    count += size;
                                    a++;
                                }
                            }
                        }
                        recent = false;
                    }
                    imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                    imageView.setTag(code);
                    imageView.setContentDescription(coloredCode);
                    return;
                case 1:
                    StickerSetNameCell cell = (StickerSetNameCell) holder.itemView;
                    cell.setText(EmojiView.this.emojiTitles[this.positionToSection.get(position)], 0);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (EmojiView.this.needEmojiSearch && position == 0) {
                return 2;
            }
            if (this.positionToSection.indexOfKey(position) >= 0) {
                return 1;
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.itemCount = Emoji.recentEmoji.size() + (EmojiView.this.needEmojiSearch ? 1 : 0);
            for (int a = 0; a < EmojiData.dataColored.length; a++) {
                this.positionToSection.put(this.itemCount, a);
                this.sectionToPosition.put(a, this.itemCount);
                this.itemCount += EmojiData.dataColored[a].length + 1;
            }
            EmojiView.this.updateEmojiTabs();
            super.notifyDataSetChanged();
        }
    }

    /* loaded from: classes5.dex */
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
            if (this.result.isEmpty() && !this.searchWas) {
                return Emoji.recentEmoji.size() + 1;
            }
            if (!this.result.isEmpty()) {
                return this.result.size() + 1;
            }
            return 2;
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
                    EmojiView emojiView = EmojiView.this;
                    view = new ImageViewEmoji(emojiView.getContext());
                    break;
                case 1:
                    View view2 = new View(EmojiView.this.getContext());
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    view = view2;
                    break;
                default:
                    FrameLayout frameLayout = new FrameLayout(EmojiView.this.getContext()) { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.1
                        {
                            EmojiSearchAdapter.this = this;
                        }

                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int parentHeight;
                            View parent2 = (View) EmojiView.this.getParent();
                            if (parent2 != null) {
                                parentHeight = (int) (parent2.getMeasuredHeight() - EmojiView.this.getY());
                            } else {
                                parentHeight = AndroidUtilities.dp(120.0f);
                            }
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(parentHeight - EmojiView.this.searchFieldHeight, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    TextView textView = new TextView(EmojiView.this.getContext());
                    textView.setText(LocaleController.getString("NoEmojiFound", R.string.NoEmojiFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelEmptyText));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                    ImageView imageView = new ImageView(EmojiView.this.getContext());
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.msg_emoji_question);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                    imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2
                        {
                            EmojiSearchAdapter.this = this;
                        }

                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            Object obj;
                            boolean[] loadingUrl = new boolean[1];
                            BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                            LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                            linearLayout.setOrientation(1);
                            linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                            ImageView imageView1 = new ImageView(EmojiView.this.getContext());
                            imageView1.setImageResource(R.drawable.smiles_info);
                            linearLayout.addView(imageView1, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                            TextView textView2 = new TextView(EmojiView.this.getContext());
                            textView2.setText(LocaleController.getString("EmojiSuggestions", R.string.EmojiSuggestions));
                            textView2.setTextSize(1, 15.0f);
                            textView2.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextBlue2));
                            int i = 5;
                            textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                            textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                            TextView textView3 = new TextView(EmojiView.this.getContext());
                            textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", R.string.EmojiSuggestionsInfo)));
                            textView3.setTextSize(1, 15.0f);
                            textView3.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextBlack));
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
                            textView4.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextLink));
                            if (!LocaleController.isRTL) {
                                i = 3;
                            }
                            textView4.setGravity(i);
                            linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                            textView4.setOnClickListener(new AnonymousClass1(loadingUrl, builder));
                            builder.setCustomView(linearLayout);
                            builder.show();
                        }

                        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 */
                        /* loaded from: classes5.dex */
                        public class AnonymousClass1 implements View.OnClickListener {
                            final /* synthetic */ BottomSheet.Builder val$builder;
                            final /* synthetic */ boolean[] val$loadingUrl;

                            AnonymousClass1(boolean[] zArr, BottomSheet.Builder builder) {
                                AnonymousClass2.this = this$2;
                                this.val$loadingUrl = zArr;
                                this.val$builder = builder;
                            }

                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                String str;
                                boolean[] zArr = this.val$loadingUrl;
                                if (zArr[0]) {
                                    return;
                                }
                                zArr[0] = true;
                                final AlertDialog[] progressDialog = {new AlertDialog(EmojiView.this.getContext(), 3)};
                                TLRPC.TL_messages_getEmojiURL req = new TLRPC.TL_messages_getEmojiURL();
                                if (EmojiSearchAdapter.this.lastSearchAlias == null) {
                                    str = EmojiView.this.lastSearchKeyboardLanguage[0];
                                } else {
                                    str = EmojiSearchAdapter.this.lastSearchAlias;
                                }
                                req.lang_code = str;
                                ConnectionsManager connectionsManager = ConnectionsManager.getInstance(EmojiView.this.currentAccount);
                                final BottomSheet.Builder builder = this.val$builder;
                                final int requestId = connectionsManager.sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3
                                    @Override // org.telegram.tgnet.RequestDelegate
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.m2588x6d790acb(progressDialog, builder, tLObject, tL_error);
                                    }
                                });
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda1
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.m2590xe2644bcd(progressDialog, requestId);
                                    }
                                }, 1000L);
                            }

                            /* renamed from: lambda$onClick$1$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1 */
                            public /* synthetic */ void m2588x6d790acb(final AlertDialog[] progressDialog, final BottomSheet.Builder builder, final TLObject response, TLRPC.TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.m2587xb3036a4a(progressDialog, response, builder);
                                    }
                                });
                            }

                            /* renamed from: lambda$onClick$0$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1 */
                            public /* synthetic */ void m2587xb3036a4a(AlertDialog[] progressDialog, TLObject response, BottomSheet.Builder builder) {
                                try {
                                    progressDialog[0].dismiss();
                                } catch (Throwable th) {
                                }
                                progressDialog[0] = null;
                                if (response instanceof TLRPC.TL_emojiURL) {
                                    Browser.openUrl(EmojiView.this.getContext(), ((TLRPC.TL_emojiURL) response).url);
                                    builder.getDismissRunnable().run();
                                }
                            }

                            /* renamed from: lambda$onClick$3$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1 */
                            public /* synthetic */ void m2590xe2644bcd(AlertDialog[] progressDialog, final int requestId) {
                                if (progressDialog[0] == null) {
                                    return;
                                }
                                progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda0
                                    @Override // android.content.DialogInterface.OnCancelListener
                                    public final void onCancel(DialogInterface dialogInterface) {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.m2589x27eeab4c(requestId, dialogInterface);
                                    }
                                });
                                progressDialog[0].show();
                            }

                            /* renamed from: lambda$onClick$2$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1 */
                            public /* synthetic */ void m2589x27eeab4c(int requestId, DialogInterface dialog) {
                                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(requestId, true);
                            }
                        }
                    });
                    frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = frameLayout;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean recent;
            String code;
            String coloredCode;
            switch (holder.getItemViewType()) {
                case 0:
                    ImageViewEmoji imageView = (ImageViewEmoji) holder.itemView;
                    int position2 = position - 1;
                    if (this.result.isEmpty() && !this.searchWas) {
                        coloredCode = Emoji.recentEmoji.get(position2);
                        code = coloredCode;
                        recent = true;
                    } else {
                        coloredCode = this.result.get(position2).emoji;
                        code = coloredCode;
                        recent = false;
                    }
                    imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                    imageView.setTag(code);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (position == 1 && this.searchWas && this.result.isEmpty()) {
                return 2;
            }
            return 0;
        }

        public void search(String text) {
            if (TextUtils.isEmpty(text)) {
                this.lastSearchEmojiString = null;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                    this.searchWas = false;
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchEmojiString = text.toLowerCase();
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
                        final String query = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, newLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = newLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.3.1
                            {
                                AnonymousClass3.this = this;
                            }

                            @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
                            public void run(ArrayList<MediaDataController.KeywordResult> param, String alias) {
                                if (query.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    EmojiSearchAdapter.this.lastSearchAlias = alias;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    EmojiSearchAdapter.this.result = param;
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

    /* loaded from: classes5.dex */
    public class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        private EmojiPagesAdapter() {
            EmojiView.this = r1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            viewGroup.removeView((View) EmojiView.this.views.get(position));
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public boolean canScrollToTab(int position) {
            boolean z = true;
            if ((position != 1 && position != 2) || EmojiView.this.currentChatId == 0) {
                return true;
            }
            EmojiView emojiView = EmojiView.this;
            if (position != 1) {
                z = false;
            }
            emojiView.showStickerBanHint(z);
            return false;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return EmojiView.this.views.size();
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public Drawable getPageIconDrawable(int position) {
            return EmojiView.this.tabIcons[position];
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return LocaleController.getString("Emoji", R.string.Emoji);
                case 1:
                    return LocaleController.getString("AccDescrGIFs", R.string.AccDescrGIFs);
                case 2:
                    return LocaleController.getString("AccDescrStickers", R.string.AccDescrStickers);
                default:
                    return null;
            }
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public void customOnDraw(Canvas canvas, int position) {
            if (position == 2 && !MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiView.this.dotPaint != null) {
                int x = (canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f);
                int y = (canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f);
                canvas.drawCircle(x, y, AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
            }
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view = (View) EmojiView.this.views.get(position);
            viewGroup.addView(view);
            return view;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /* loaded from: classes5.dex */
    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.User bot;
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
        private ArrayList<TLRPC.BotInlineResult> results;
        private HashMap<String, TLRPC.BotInlineResult> resultsMap;
        private boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private boolean showTrendingWhenSearchEmpty;
        private int trendingSectionItem;
        private final boolean withRecent;

        public GifAdapter(EmojiView emojiView, Context context) {
            this(context, false, 0);
        }

        public GifAdapter(EmojiView emojiView, Context context, boolean withRecent) {
            this(context, withRecent, withRecent ? Integer.MAX_VALUE : 0);
        }

        public GifAdapter(Context context, boolean withRecent, int maxRecentRowsCount) {
            EmojiView.this = r2;
            this.results = new ArrayList<>();
            this.resultsMap = new HashMap<>();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context;
            this.withRecent = withRecent;
            this.maxRecentRowsCount = maxRecentRowsCount;
            this.progressEmptyView = withRecent ? null : new GifProgressEmptyView(context);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            boolean z = this.withRecent;
            if (z && position == this.trendingSectionItem) {
                return 2;
            }
            if (!z && this.results.isEmpty()) {
                return 3;
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ContextLinkCell cell = new ContextLinkCell(this.context);
                    cell.setCanPreviewGif(true);
                    view = cell;
                    break;
                case 1:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 2:
                    StickerSetNameCell cell1 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    cell1.setText(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs), 0);
                    view = cell1;
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
                    lp.topMargin = AndroidUtilities.dp(2.5f);
                    lp.bottomMargin = AndroidUtilities.dp(5.5f);
                    view.setLayoutParams(lp);
                    break;
                default:
                    view = this.progressEmptyView;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ContextLinkCell cell = (ContextLinkCell) holder.itemView;
                    int i = this.firstResultItem;
                    if (i < 0 || position < i) {
                        cell.setGif((TLRPC.Document) EmojiView.this.recentGifs.get(position - 1), false);
                        return;
                    } else {
                        cell.setLink(this.results.get(position - i), this.bot, true, false, false, true);
                        return;
                    }
                default:
                    return;
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
            } else if (!this.withRecent) {
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
                int listWidth = EmojiView.this.gifGridView.getMeasuredWidth();
                int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
                int preferredRowSize = AndroidUtilities.dp(100.0f);
                int rowCount = 0;
                int spanLeft = spanCount;
                int currentItemsInRow = 0;
                this.recentItemsCount = 0;
                int N = EmojiView.this.recentGifs.size();
                for (int i2 = 0; i2 < N; i2++) {
                    Size size = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC.Document) EmojiView.this.recentGifs.get(i2)));
                    int requiredSpan = Math.min(spanCount, (int) Math.floor(spanCount * (((size.width / size.height) * preferredRowSize) / listWidth)));
                    if (spanLeft < requiredSpan) {
                        this.recentItemsCount += currentItemsInRow;
                        rowCount++;
                        if (rowCount == this.maxRecentRowsCount) {
                            break;
                        }
                        currentItemsInRow = 0;
                        spanLeft = spanCount;
                    }
                    currentItemsInRow++;
                    spanLeft -= requiredSpan;
                }
                int i3 = this.maxRecentRowsCount;
                if (rowCount < i3) {
                    this.recentItemsCount += currentItemsInRow;
                }
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
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.GifAdapter.this.m2594xd8a53b(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$searchBotUser$1$org-telegram-ui-Components-EmojiView$GifAdapter */
        public /* synthetic */ void m2594xd8a53b(final TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.GifAdapter.this.m2593x14f0b3a(response);
                    }
                });
            }
        }

        /* renamed from: lambda$searchBotUser$0$org-telegram-ui-Components-EmojiView$GifAdapter */
        public /* synthetic */ void m2593x14f0b3a(TLObject response) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        public void search(final String text) {
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
            if (TextUtils.isEmpty(text)) {
                this.lastSearchImageString = null;
                if (!this.showTrendingWhenSearchEmpty) {
                    int page = EmojiView.this.gifTabs.getCurrentPosition();
                    if (page == EmojiView.this.gifRecentTabNum || page == EmojiView.this.gifTrendingTabNum) {
                        if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                            EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                            return;
                        }
                        return;
                    }
                    searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(page - EmojiView.this.gifFirstEmojiTabNum));
                    return;
                }
                loadTrendingGifs();
                return;
            }
            String lowerCase = text.toLowerCase();
            this.lastSearchImageString = lowerCase;
            if (!TextUtils.isEmpty(lowerCase)) {
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.EmojiView.GifAdapter.1
                    {
                        GifAdapter.this = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        GifAdapter.this.search(text, "", true);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            }
        }

        public void searchEmoji(String emoji) {
            if (this.lastSearchIsEmoji && TextUtils.equals(this.lastSearchImageString, emoji)) {
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
            } else {
                search(emoji, "", true, true, true);
            }
        }

        protected void search(String query, String offset, boolean searchUser) {
            search(query, offset, searchUser, false, false);
        }

        protected void search(final String query, final String offset, final boolean searchUser, final boolean isEmoji, final boolean cache) {
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchImageString = query;
            this.lastSearchIsEmoji = isEmoji;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(isEmoji);
            }
            TLObject object = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (!(object instanceof TLRPC.User)) {
                if (searchUser) {
                    searchBotUser();
                    if (!this.withRecent) {
                        EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                        return;
                    }
                    return;
                }
                return;
            }
            if (!this.withRecent && TextUtils.isEmpty(offset)) {
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
            }
            this.bot = (TLRPC.User) object;
            final String key = "gif_search_" + query + "_" + offset;
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.GifAdapter.this.m2592lambda$search$3$orgtelegramuiComponentsEmojiView$GifAdapter(query, offset, searchUser, isEmoji, cache, key, tLObject, tL_error);
                }
            };
            if (!cache && !this.withRecent && isEmoji && TextUtils.isEmpty(offset)) {
                this.results.clear();
                this.resultsMap.clear();
                if (EmojiView.this.gifGridView.getAdapter() != this) {
                    EmojiView.this.gifGridView.setAdapter(this);
                }
                notifyDataSetChanged();
                EmojiView.this.scrollGifsToTop();
            }
            if (!cache || !EmojiView.this.gifCache.containsKey(key)) {
                if (EmojiView.this.gifSearchPreloader.isLoading(key)) {
                    return;
                }
                if (cache) {
                    this.reqId = -1;
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(key, requestDelegate);
                    return;
                }
                TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                req.query = query == null ? "" : query;
                req.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                req.offset = offset;
                req.peer = new TLRPC.TL_inputPeerEmpty();
                this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, requestDelegate, 2);
                return;
            }
            m2591lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(query, offset, searchUser, isEmoji, true, key, (TLObject) EmojiView.this.gifCache.get(key));
        }

        /* renamed from: lambda$search$3$org-telegram-ui-Components-EmojiView$GifAdapter */
        public /* synthetic */ void m2592lambda$search$3$orgtelegramuiComponentsEmojiView$GifAdapter(final String query, final String offset, final boolean searchUser, final boolean isEmoji, final boolean cache, final String key, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifAdapter.this.m2591lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(query, offset, searchUser, isEmoji, cache, key, response);
                }
            });
        }

        /* renamed from: processResponse */
        public void m2591lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(String query, String offset, boolean searchUser, boolean isEmoji, boolean cache, String key, TLObject response) {
            if (query == null || !query.equals(this.lastSearchImageString)) {
                return;
            }
            boolean z = false;
            this.reqId = 0;
            if (cache && (!(response instanceof TLRPC.messages_BotResults) || ((TLRPC.messages_BotResults) response).results.isEmpty())) {
                search(query, offset, searchUser, isEmoji, false);
                return;
            }
            if (!this.withRecent && TextUtils.isEmpty(offset)) {
                this.results.clear();
                this.resultsMap.clear();
                EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
            }
            if (response instanceof TLRPC.messages_BotResults) {
                int addedCount = 0;
                int oldCount = this.results.size();
                TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                if (!EmojiView.this.gifCache.containsKey(key)) {
                    EmojiView.this.gifCache.put(key, res);
                }
                if (!cache && res.cache_time != 0) {
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).saveBotCache(key, res);
                }
                this.nextSearchOffset = res.next_offset;
                for (int a = 0; a < res.results.size(); a++) {
                    TLRPC.BotInlineResult result = res.results.get(a);
                    if (!this.resultsMap.containsKey(result.id)) {
                        result.query_id = res.query_id;
                        this.results.add(result);
                        this.resultsMap.put(result.id, result);
                        addedCount++;
                    }
                }
                if (oldCount == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                    z = true;
                }
                this.searchEndReached = z;
                if (addedCount != 0) {
                    if (!isEmoji || oldCount != 0) {
                        updateItems();
                        if (this.withRecent) {
                            if (oldCount != 0) {
                                notifyItemChanged(this.recentItemsCount + 1 + oldCount);
                                notifyItemRangeInserted(this.recentItemsCount + 1 + oldCount + 1, addedCount);
                            } else {
                                notifyItemRangeInserted(this.recentItemsCount + 1, addedCount + 1);
                            }
                        } else {
                            if (oldCount != 0) {
                                notifyItemChanged(oldCount);
                            }
                            notifyItemRangeInserted(oldCount + 1, addedCount);
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
            if (!this.withRecent) {
                if (EmojiView.this.gifGridView.getAdapter() != this) {
                    EmojiView.this.gifGridView.setAdapter(this);
                }
                if (isEmoji && !TextUtils.isEmpty(query) && TextUtils.isEmpty(offset)) {
                    EmojiView.this.scrollGifsToTop();
                }
            }
        }
    }

    /* loaded from: classes5.dex */
    public class GifSearchPreloader {
        private final List<String> loadingKeys;

        private GifSearchPreloader() {
            EmojiView.this = r1;
            this.loadingKeys = new ArrayList();
        }

        public boolean isLoading(String key) {
            return this.loadingKeys.contains(key);
        }

        public void preload(String query) {
            preload(query, "", true);
        }

        private void preload(final String query, final String offset, final boolean cache) {
            final String key = "gif_search_" + query + "_" + offset;
            if (cache && EmojiView.this.gifCache.containsKey(key)) {
                return;
            }
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.GifSearchPreloader.this.m2596x522811b9(query, offset, cache, key, tLObject, tL_error);
                }
            };
            if (!cache) {
                MessagesController messagesController = MessagesController.getInstance(EmojiView.this.currentAccount);
                TLObject gifSearchBot = messagesController.getUserOrChat(messagesController.gifSearchBot);
                if (!(gifSearchBot instanceof TLRPC.User)) {
                    return;
                }
                this.loadingKeys.add(key);
                TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                req.query = query == null ? "" : query;
                req.bot = messagesController.getInputUser((TLRPC.User) gifSearchBot);
                req.offset = offset;
                req.peer = new TLRPC.TL_inputPeerEmpty();
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, requestDelegate, 2);
                return;
            }
            this.loadingKeys.add(key);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(key, requestDelegate);
        }

        /* renamed from: lambda$preload$1$org-telegram-ui-Components-EmojiView$GifSearchPreloader */
        public /* synthetic */ void m2596x522811b9(final String query, final String offset, final boolean cache, final String key, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifSearchPreloader.this.m2595x2c9408b8(query, offset, cache, key, response);
                }
            });
        }

        /* renamed from: processResponse */
        public void m2595x2c9408b8(String query, String offset, boolean cache, String key, TLObject response) {
            this.loadingKeys.remove(key);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(query)) {
                EmojiView.this.gifSearchAdapter.m2591lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(query, offset, false, true, cache, key, response);
            } else if (cache && (!(response instanceof TLRPC.messages_BotResults) || ((TLRPC.messages_BotResults) response).results.isEmpty())) {
                preload(query, offset, false);
            } else if ((response instanceof TLRPC.messages_BotResults) && !EmojiView.this.gifCache.containsKey(key)) {
                EmojiView.this.gifCache.put(key, (TLRPC.messages_BotResults) response);
            }
        }
    }

    /* loaded from: classes5.dex */
    public class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size = new Size();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GifLayoutManager(Context context) {
            super(context, 100, true);
            EmojiView.this = r3;
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.GifLayoutManager.1
                {
                    GifLayoutManager.this = this;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int position) {
                    if (position == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    return GifLayoutManager.this.getSpanSizeForItem(position - 1);
                }
            });
        }

        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        protected Size getSizeForItem(int i) {
            ArrayList<TLRPC.DocumentAttribute> attributes;
            TLRPC.Document document;
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                if (i > EmojiView.this.gifAdapter.recentItemsCount) {
                    TLRPC.BotInlineResult result = (TLRPC.BotInlineResult) EmojiView.this.gifAdapter.results.get((i - EmojiView.this.gifAdapter.recentItemsCount) - 1);
                    document = result.document;
                    if (document != null) {
                        attributes = document.attributes;
                    } else if (result.content != null) {
                        attributes = result.content.attributes;
                    } else if (result.thumb != null) {
                        attributes = result.thumb.attributes;
                    } else {
                        attributes = null;
                    }
                } else if (i != EmojiView.this.gifAdapter.recentItemsCount) {
                    document = (TLRPC.Document) EmojiView.this.recentGifs.get(i);
                    attributes = document.attributes;
                } else {
                    return null;
                }
            } else if (!EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                TLRPC.BotInlineResult result2 = (TLRPC.BotInlineResult) EmojiView.this.gifSearchAdapter.results.get(i);
                document = result2.document;
                if (document != null) {
                    attributes = document.attributes;
                } else if (result2.content != null) {
                    attributes = result2.content.attributes;
                } else if (result2.thumb != null) {
                    attributes = result2.thumb.attributes;
                } else {
                    attributes = null;
                }
            } else {
                document = null;
                attributes = null;
            }
            return getSizeForItem(document, attributes);
        }

        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        public int getFlowItemCount() {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                return 0;
            }
            return getItemCount() - 1;
        }

        public Size getSizeForItem(TLRPC.Document document) {
            return getSizeForItem(document, document.attributes);
        }

        public Size getSizeForItem(TLRPC.Document document, List<TLRPC.DocumentAttribute> attributes) {
            TLRPC.PhotoSize thumb;
            Size size = this.size;
            size.height = 100.0f;
            size.width = 100.0f;
            if (document != null && (thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90)) != null && thumb.w != 0 && thumb.h != 0) {
                this.size.width = thumb.w;
                this.size.height = thumb.h;
            }
            if (attributes != null) {
                for (int b = 0; b < attributes.size(); b++) {
                    TLRPC.DocumentAttribute attribute = attributes.get(b);
                    if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                        this.size.width = attribute.w;
                        this.size.height = attribute.h;
                        break;
                    }
                }
            }
            return this.size;
        }
    }

    /* loaded from: classes5.dex */
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
            imageView.setColorFilter(new PorterDuffColorFilter(r13.getThemedColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setText(LocaleController.getString("NoGIFsFound", R.string.NoGIFsFound));
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(r13.getThemedColor(Theme.key_chat_emojiPanelEmptyText));
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context, r13.resourcesProvider);
            this.progressView = radialProgressView;
            radialProgressView.setVisibility(8);
            radialProgressView.setProgressColor(r13.getThemedColor(Theme.key_progressCircle));
            addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2 = EmojiView.this.gifGridView.getMeasuredHeight();
            if (!this.loadingState) {
                height = (int) ((((height2 - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f);
            } else {
                height = height2 - AndroidUtilities.dp(80.0f);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        }

        public boolean isLoadingState() {
            return this.loadingState;
        }

        public void setLoadingState(boolean loadingState) {
            if (this.loadingState != loadingState) {
                this.loadingState = loadingState;
                int i = 8;
                this.imageView.setVisibility(loadingState ? 8 : 0);
                this.textView.setVisibility(loadingState ? 8 : 0);
                RadialProgressView radialProgressView = this.progressView;
                if (loadingState) {
                    i = 0;
                }
                radialProgressView.setVisibility(i);
            }
        }
    }

    /* loaded from: classes5.dex */
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
        private ArrayList<TLRPC.StickerSetCovered> serverPacks = new ArrayList<>();
        private ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
        private HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        private HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
        private ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
        private SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        private Runnable searchRunnable = new AnonymousClass1();

        static /* synthetic */ int access$16404(StickersSearchGridAdapter x0) {
            int i = x0.emojiSearchId + 1;
            x0.emojiSearchId = i;
            return i;
        }

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
                StickersSearchGridAdapter.this = this$1;
            }

            public void clear() {
                if (StickersSearchGridAdapter.this.cleared) {
                    return;
                }
                StickersSearchGridAdapter.this.cleared = true;
                StickersSearchGridAdapter.this.emojiStickers.clear();
                StickersSearchGridAdapter.this.emojiArrays.clear();
                StickersSearchGridAdapter.this.localPacks.clear();
                StickersSearchGridAdapter.this.serverPacks.clear();
                StickersSearchGridAdapter.this.localPacksByShortName.clear();
                StickersSearchGridAdapter.this.localPacksByName.clear();
            }

            @Override // java.lang.Runnable
            public void run() {
                int index;
                int index2;
                if (!TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    EmojiView.this.stickersSearchField.progressDrawable.startAnimation();
                    StickersSearchGridAdapter.this.cleared = false;
                    final int lastId = StickersSearchGridAdapter.access$16404(StickersSearchGridAdapter.this);
                    final ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                    final LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                    final HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(EmojiView.this.currentAccount).getAllStickers();
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
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, newLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = newLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.1
                            {
                                AnonymousClass1.this = this;
                            }

                            @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
                            public void run(ArrayList<MediaDataController.KeywordResult> param, String alias) {
                                if (lastId != StickersSearchGridAdapter.this.emojiSearchId) {
                                    return;
                                }
                                boolean added = false;
                                int size2 = param.size();
                                for (int a3 = 0; a3 < size2; a3++) {
                                    String emoji2 = param.get(a3).emoji;
                                    HashMap hashMap = allStickers;
                                    ArrayList<TLRPC.Document> newStickers2 = hashMap != null ? (ArrayList) hashMap.get(emoji2) : null;
                                    if (newStickers2 != null && !newStickers2.isEmpty()) {
                                        AnonymousClass1.this.clear();
                                        if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(newStickers2)) {
                                            StickersSearchGridAdapter.this.emojiStickers.put(newStickers2, emoji2);
                                            StickersSearchGridAdapter.this.emojiArrays.add(newStickers2);
                                            added = true;
                                        }
                                    }
                                }
                                if (added) {
                                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(0);
                    MessagesController.getInstance(EmojiView.this.currentAccount).filterPremiumStickers(local);
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
                    ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(3);
                    MessagesController.getInstance(EmojiView.this.currentAccount).filterPremiumStickers(local2);
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
                    if ((!StickersSearchGridAdapter.this.localPacks.isEmpty() || !StickersSearchGridAdapter.this.emojiStickers.isEmpty()) && EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    final TLRPC.TL_messages_searchStickerSets req = new TLRPC.TL_messages_searchStickerSets();
                    req.q = StickersSearchGridAdapter.this.searchQuery;
                    StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                    stickersSearchGridAdapter.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda3
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.m2605x55f74527(req, tLObject, tL_error);
                        }
                    });
                    if (Emoji.isValidEmoji(StickersSearchGridAdapter.this.searchQuery)) {
                        final TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                        req2.emoticon = StickersSearchGridAdapter.this.searchQuery;
                        req2.hash = 0L;
                        StickersSearchGridAdapter stickersSearchGridAdapter2 = StickersSearchGridAdapter.this;
                        stickersSearchGridAdapter2.reqId2 = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.m2607x2a565565(req2, emojiStickersArray, emojiStickersMap, tLObject, tL_error);
                            }
                        });
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* renamed from: lambda$run$1$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m2605x55f74527(final TLRPC.TL_messages_searchStickerSets req, final TLObject response, TLRPC.TL_error error) {
                if (response instanceof TLRPC.TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.m2604xebc7bd08(req, response);
                        }
                    });
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m2604xebc7bd08(TLRPC.TL_messages_searchStickerSets req, TLObject response) {
                if (req.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    TLRPC.TL_messages_foundStickerSets res = (TLRPC.TL_messages_foundStickerSets) response;
                    StickersSearchGridAdapter.this.serverPacks.addAll(res.sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* renamed from: lambda$run$3$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m2607x2a565565(final TLRPC.TL_messages_getStickers req2, final ArrayList emojiStickersArray, final LongSparseArray emojiStickersMap, final TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.m2606xc026cd46(req2, response, emojiStickersArray, emojiStickersMap);
                    }
                });
            }

            /* renamed from: lambda$run$2$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1 */
            public /* synthetic */ void m2606xc026cd46(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
                if (req2.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
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
                }
            }
        }

        public StickersSearchGridAdapter(Context context) {
            EmojiView.this = r1;
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
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.serverPacks.clear();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
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
            if (object instanceof TLRPC.StickerSetCovered) {
                return 3;
            }
            return 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, true) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.2
                        {
                            StickersSearchGridAdapter.this = this;
                        }

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
                    view = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    break;
                case 3:
                    view = new FeaturedStickerSetInfoCell(this.context, 17, false, true, EmojiView.this.resourcesProvider);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            EmojiView.StickersSearchGridAdapter.this.m2603xcd177c00(view2);
                        }
                    });
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 5:
                    FrameLayout frameLayout = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.3
                        {
                            StickersSearchGridAdapter.this = this;
                        }

                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int height = EmojiView.this.stickersGridView.getMeasuredHeight();
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((int) ((((height - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelEmptyText));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    view = frameLayout;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter */
        public /* synthetic */ void m2603xcd177c00(View v) {
            FeaturedStickerSetInfoCell parent1 = (FeaturedStickerSetInfoCell) v.getParent();
            TLRPC.StickerSetCovered pack = parent1.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(pack.set.id) >= 0 || EmojiView.this.removingStickerSets.indexOfKey(pack.set.id) >= 0) {
                return;
            }
            if (parent1.isInstalled()) {
                EmojiView.this.removingStickerSets.put(pack.set.id, pack);
                EmojiView.this.delegate.onStickerSetRemove(parent1.getStickerSet());
                return;
            }
            parent1.setAddDrawProgress(true, true);
            EmojiView.this.installingStickerSets.put(pack.set.id, pack);
            EmojiView.this.delegate.onStickerSetAdd(parent1.getStickerSet());
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x007d  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x007f  */
        /* JADX WARN: Removed duplicated region for block: B:28:0x008c  */
        /* JADX WARN: Removed duplicated region for block: B:29:0x009c  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            /*
                Method dump skipped, instructions count: 514
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int serverCount;
            ArrayList<TLRPC.Document> documents;
            Object pack;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int startRow = 0;
            int a = -1;
            int serverCount2 = this.serverPacks.size();
            int localCount = this.localPacks.size();
            int emojiCount = !this.emojiArrays.isEmpty() ? 1 : 0;
            while (a < serverCount2 + localCount + emojiCount) {
                Object pack2 = null;
                if (a == -1) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i = this.totalItems;
                    this.totalItems = i + 1;
                    sparseArray.put(i, "search");
                    startRow++;
                    serverCount = serverCount2;
                } else {
                    int idx = a;
                    if (idx < localCount) {
                        TLRPC.TL_messages_stickerSet set = this.localPacks.get(idx);
                        documents = set.documents;
                        pack = set;
                        serverCount = serverCount2;
                    } else {
                        int idx2 = idx - localCount;
                        if (idx2 < emojiCount) {
                            int documentsCount = 0;
                            String lastEmoji = "";
                            int N = this.emojiArrays.size();
                            for (int i2 = 0; i2 < N; i2++) {
                                ArrayList<TLRPC.Document> documents2 = this.emojiArrays.get(i2);
                                String emoji = this.emojiStickers.get(documents2);
                                if (emoji != null && !lastEmoji.equals(emoji)) {
                                    lastEmoji = emoji;
                                    this.positionToEmoji.put(this.totalItems + documentsCount, lastEmoji);
                                }
                                int b = 0;
                                int size = documents2.size();
                                while (b < size) {
                                    int serverCount3 = serverCount2;
                                    int serverCount4 = this.totalItems;
                                    int num = serverCount4 + documentsCount;
                                    String lastEmoji2 = lastEmoji;
                                    int row = (documentsCount / EmojiView.this.stickersGridAdapter.stickersPerRow) + startRow;
                                    int N2 = N;
                                    TLRPC.Document document = documents2.get(b);
                                    ArrayList<TLRPC.Document> documents3 = documents2;
                                    this.cache.put(num, document);
                                    String emoji2 = emoji;
                                    int b2 = b;
                                    Object parent = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                                    if (parent != null) {
                                        this.cacheParent.put(num, parent);
                                    }
                                    this.positionToRow.put(num, row);
                                    if (a >= localCount && (pack2 instanceof TLRPC.StickerSetCovered)) {
                                        this.positionsToSets.put(num, null);
                                    }
                                    documentsCount++;
                                    b = b2 + 1;
                                    serverCount2 = serverCount3;
                                    lastEmoji = lastEmoji2;
                                    documents2 = documents3;
                                    N = N2;
                                    emoji = emoji2;
                                }
                            }
                            serverCount = serverCount2;
                            int count = (int) Math.ceil(documentsCount / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            for (int b3 = 0; b3 < count; b3++) {
                                this.rowStartPack.put(startRow + b3, Integer.valueOf(documentsCount));
                            }
                            int b4 = this.totalItems;
                            this.totalItems = b4 + (EmojiView.this.stickersGridAdapter.stickersPerRow * count);
                            startRow += count;
                        } else {
                            serverCount = serverCount2;
                            TLRPC.StickerSetCovered set2 = this.serverPacks.get(idx2 - emojiCount);
                            documents = set2.covers;
                            pack = set2;
                        }
                    }
                    if (!documents.isEmpty()) {
                        int count2 = (int) Math.ceil(documents.size() / EmojiView.this.stickersGridAdapter.stickersPerRow);
                        this.cache.put(this.totalItems, pack);
                        if (a >= localCount && (pack instanceof TLRPC.StickerSetCovered)) {
                            this.positionsToSets.put(this.totalItems, (TLRPC.StickerSetCovered) pack);
                        }
                        this.positionToRow.put(this.totalItems, startRow);
                        int size2 = documents.size();
                        for (int b5 = 0; b5 < size2; b5++) {
                            int num2 = b5 + 1 + this.totalItems;
                            int row2 = startRow + 1 + (b5 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(num2, documents.get(b5));
                            if (pack != null) {
                                this.cacheParent.put(num2, pack);
                            }
                            this.positionToRow.put(num2, row2);
                            if (a >= localCount && (pack instanceof TLRPC.StickerSetCovered)) {
                                this.positionsToSets.put(num2, (TLRPC.StickerSetCovered) pack);
                            }
                        }
                        int N3 = count2 + 1;
                        for (int b6 = 0; b6 < N3; b6++) {
                            this.rowStartPack.put(startRow + b6, pack);
                        }
                        int b7 = this.totalItems;
                        this.totalItems = b7 + (EmojiView.this.stickersGridAdapter.stickersPerRow * count2) + 1;
                        startRow += count2 + 1;
                    }
                }
                a++;
                serverCount2 = serverCount;
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

    public void setShowing(boolean showing) {
        this.showing = showing;
        updateStickerTabsPosition();
    }

    public void onMessageSend() {
        ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker == null) {
            return;
        }
        chooseStickerActionTracker.reset();
    }

    /* loaded from: classes5.dex */
    public static abstract class ChooseStickerActionTracker {
        private final int currentAccount;
        private final long dialogId;
        private final int threadId;
        boolean typingWasSent;
        boolean visible = false;
        long lastActionTime = -1;

        public abstract boolean isShown();

        public ChooseStickerActionTracker(int currentAccount, long dialogId, int threadId) {
            this.currentAccount = currentAccount;
            this.dialogId = dialogId;
            this.threadId = threadId;
        }

        public void doSomeAction() {
            if (this.visible) {
                if (this.lastActionTime == -1) {
                    this.lastActionTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - this.lastActionTime > AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
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
