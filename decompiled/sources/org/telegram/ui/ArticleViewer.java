package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.MetricAffectingSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.internal.view.SupportMenu;
import androidx.core.net.MailTo;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnchorSpan;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TableLayout;
import org.telegram.ui.Components.TextPaintImageReceiverSpan;
import org.telegram.ui.Components.TextPaintMarkSpan;
import org.telegram.ui.Components.TextPaintSpan;
import org.telegram.ui.Components.TextPaintUrlSpan;
import org.telegram.ui.Components.TextPaintWebpageUrlSpan;
import org.telegram.ui.Components.TranslateAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes4.dex */
public class ArticleViewer implements NotificationCenter.NotificationCenterDelegate {
    private static final int TEXT_FLAG_ITALIC = 2;
    private static final int TEXT_FLAG_MARKED = 64;
    private static final int TEXT_FLAG_MEDIUM = 1;
    private static final int TEXT_FLAG_MONO = 4;
    private static final int TEXT_FLAG_REGULAR = 0;
    private static final int TEXT_FLAG_STRIKE = 32;
    private static final int TEXT_FLAG_SUB = 128;
    private static final int TEXT_FLAG_SUP = 256;
    private static final int TEXT_FLAG_UNDERLINE = 16;
    private static final int TEXT_FLAG_URL = 8;
    private static final int TEXT_FLAG_WEBPAGE_URL = 512;
    private static TextPaint channelNamePaint = null;
    private static TextPaint channelNamePhotoPaint = null;
    private static Paint dividerPaint = null;
    private static Paint dotsPaint = null;
    private static TextPaint embedPostAuthorPaint = null;
    private static TextPaint embedPostDatePaint = null;
    private static TextPaint errorTextPaint = null;
    private static TextPaint listTextNumPaint = null;
    private static TextPaint listTextPointerPaint = null;
    private static final int open_item = 3;
    private static Paint photoBackgroundPaint = null;
    private static Paint preformattedBackgroundPaint = null;
    private static Paint quoteLinePaint = null;
    private static TextPaint relatedArticleHeaderPaint = null;
    private static TextPaint relatedArticleTextPaint = null;
    private static final int search_item = 1;
    private static final int settings_item = 4;
    private static final int share_item = 2;
    private static Paint tableHalfLinePaint;
    private static Paint tableHeaderPaint;
    private static Paint tableLinePaint;
    private static Paint tableStripPaint;
    private static Paint urlPaint;
    private static Paint webpageMarkPaint;
    private static Paint webpageSearchPaint;
    private static Paint webpageUrlPaint;
    private WebpageAdapter[] adapter;
    private int anchorsOffsetMeasuredWidth;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private boolean attachedToWindow;
    private ImageView backButton;
    private BackDrawable backDrawable;
    private Paint backgroundPaint;
    private ImageView clearButton;
    private boolean closeAnimationInProgress;
    private boolean collapsed;
    private FrameLayout containerView;
    private int currentAccount;
    private int currentHeaderHeight;
    private WebPlayerView currentPlayingVideo;
    private int currentSearchIndex;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private TextView deleteView;
    private boolean drawBlockSelection;
    private AspectRatioFrameLayout fullscreenAspectRatioView;
    private TextureView fullscreenTextureView;
    private FrameLayout fullscreenVideoContainer;
    private WebPlayerView fullscreenedVideo;
    private boolean hasCutout;
    private FrameLayout headerView;
    private boolean ignoreOnTextChange;
    private boolean isVisible;
    private boolean keyboardVisible;
    private Object lastInsets;
    private int lastReqId;
    private Drawable layerShadowDrawable;
    private LinearLayoutManager[] layoutManager;
    private Runnable lineProgressTickRunnable;
    private LineProgressView lineProgressView;
    private BottomSheet linkSheet;
    private RecyclerListView[] listView;
    private TLRPC.Chat loadedChannel;
    private boolean loadingChannel;
    private ActionBarMenuItem menuButton;
    private FrameLayout menuContainer;
    private int openUrlReqId;
    private AnimatorSet pageSwitchAnimation;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    PinchToZoomHelper pinchToZoomHelper;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private int pressedLayoutY;
    private LinkSpanDrawable<TextPaintUrlSpan> pressedLink;
    private DrawingText pressedLinkOwnerLayout;
    private View pressedLinkOwnerView;
    private int previewsReqId;
    private ContextProgressView progressView;
    private AnimatorSet progressViewAnimation;
    private AnimatorSet runAfterKeyboardClose;
    private Paint scrimPaint;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private EditTextBoldCursor searchField;
    private FrameLayout searchPanel;
    private Runnable searchRunnable;
    private View searchShadow;
    private String searchText;
    private ImageView searchUpButton;
    private Drawable slideDotBigDrawable;
    private Drawable slideDotDrawable;
    TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;
    TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelperBottomSheet;
    private SimpleTextView titleTextView;
    private long transitionAnimationStartTime;
    private Dialog visibleDialog;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowView windowView;
    private static volatile ArticleViewer Instance = null;
    public static final Property<WindowView, Float> ARTICLE_VIEWER_INNER_TRANSLATION_X = new AnimationProperties.FloatProperty<WindowView>("innerTranslationX") { // from class: org.telegram.ui.ArticleViewer.1
        public void setValue(WindowView object, float value) {
            object.setInnerTranslationX(value);
        }

        public Float get(WindowView object) {
            return Float.valueOf(object.getInnerTranslationX());
        }
    };
    private static TextPaint audioTimePaint = new TextPaint(1);
    private static SparseArray<TextPaint> photoCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> photoCreditTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> titleTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> kickerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> subtitleTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> subheaderTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> authorTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> footerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> listTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> embedPostTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> embedPostCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> mediaCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> mediaCreditTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> relatedArticleTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> detailsTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> tableTextPaints = new SparseArray<>();
    private ArrayList<BlockEmbedCell> createdWebViews = new ArrayList<>();
    private int lastBlockNum = 1;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private ArrayList<TLRPC.WebPage> pagesStack = new ArrayList<>();
    private boolean animateClear = true;
    private Paint headerPaint = new Paint();
    private Paint statusBarPaint = new Paint();
    private Paint navigationBarPaint = new Paint();
    private Paint headerProgressPaint = new Paint();
    private boolean checkingForLongPress = false;
    private CheckForLongPress pendingCheckForLongPress = null;
    private int pressCount = 0;
    private CheckForTap pendingCheckForTap = null;
    private LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector();
    private LinkPath urlPath = new LinkPath();
    private int allowAnimationIndex = -1;
    private final String BOTTOM_SHEET_VIEW_TAG = "bottomSheet";
    private int selectedFont = 0;
    private FontCell[] fontCells = new FontCell[2];
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private int lastSearchIndex = -1;

    static /* synthetic */ int access$13208(ArticleViewer x0) {
        int i = x0.lastBlockNum;
        x0.lastBlockNum = i + 1;
        return i;
    }

    static /* synthetic */ int access$2104(ArticleViewer x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    public static ArticleViewer getInstance() {
        ArticleViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (ArticleViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ArticleViewer articleViewer = new ArticleViewer();
                    localInstance = articleViewer;
                    Instance = articleViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    /* loaded from: classes4.dex */
    public static class TL_pageBlockRelatedArticlesChild extends TLRPC.PageBlock {
        private int num;
        private TLRPC.TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }
    }

    /* loaded from: classes4.dex */
    public static class TL_pageBlockRelatedArticlesShadow extends TLRPC.PageBlock {
        private TLRPC.TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }
    }

    /* loaded from: classes4.dex */
    public static class TL_pageBlockDetailsChild extends TLRPC.PageBlock {
        private TLRPC.PageBlock block;
        private TLRPC.PageBlock parent;

        private TL_pageBlockDetailsChild() {
        }
    }

    /* loaded from: classes4.dex */
    public static class TL_pageBlockDetailsBottom extends TLRPC.PageBlock {
        private TLRPC.TL_pageBlockDetails parent;

        private TL_pageBlockDetailsBottom() {
        }
    }

    /* loaded from: classes4.dex */
    public class TL_pageBlockListParent extends TLRPC.PageBlock {
        private ArrayList<TL_pageBlockListItem> items;
        private int lastFontSize;
        private int lastMaxNumCalcWidth;
        private int level;
        private int maxNumWidth;
        private TLRPC.TL_pageBlockList pageBlockList;

        private TL_pageBlockListParent() {
            ArticleViewer.this = r1;
            this.items = new ArrayList<>();
        }
    }

    /* loaded from: classes4.dex */
    public class TL_pageBlockListItem extends TLRPC.PageBlock {
        private TLRPC.PageBlock blockItem;
        private int index;
        private String num;
        private DrawingText numLayout;
        private TL_pageBlockListParent parent;
        private TLRPC.RichText textItem;

        private TL_pageBlockListItem() {
            ArticleViewer.this = r1;
            this.index = Integer.MAX_VALUE;
        }
    }

    /* loaded from: classes4.dex */
    public class TL_pageBlockOrderedListParent extends TLRPC.PageBlock {
        private ArrayList<TL_pageBlockOrderedListItem> items;
        private int lastFontSize;
        private int lastMaxNumCalcWidth;
        private int level;
        private int maxNumWidth;
        private TLRPC.TL_pageBlockOrderedList pageBlockOrderedList;

        private TL_pageBlockOrderedListParent() {
            ArticleViewer.this = r1;
            this.items = new ArrayList<>();
        }
    }

    /* loaded from: classes4.dex */
    public class TL_pageBlockOrderedListItem extends TLRPC.PageBlock {
        private TLRPC.PageBlock blockItem;
        private int index;
        private String num;
        private DrawingText numLayout;
        private TL_pageBlockOrderedListParent parent;
        private TLRPC.RichText textItem;

        private TL_pageBlockOrderedListItem() {
            ArticleViewer.this = r1;
            this.index = Integer.MAX_VALUE;
        }
    }

    /* loaded from: classes4.dex */
    public static class TL_pageBlockEmbedPostCaption extends TLRPC.TL_pageBlockEmbedPost {
        private TLRPC.TL_pageBlockEmbedPost parent;

        private TL_pageBlockEmbedPostCaption() {
        }
    }

    /* loaded from: classes4.dex */
    public class DrawingText implements TextSelectionHelper.TextLayoutBlock {
        public View latestParentView;
        public LinkPath markPath;
        public TLRPC.PageBlock parentBlock;
        public Object parentText;
        public CharSequence prefix;
        public int row;
        public int searchIndex = -1;
        public LinkPath searchPath;
        public StaticLayout textLayout;
        public LinkPath textPath;
        public int x;
        public int y;

        public DrawingText() {
            ArticleViewer.this = this$0;
        }

        public void draw(Canvas canvas, View view) {
            float x;
            float width;
            this.latestParentView = view;
            if (!ArticleViewer.this.searchResults.isEmpty()) {
                SearchResult result = (SearchResult) ArticleViewer.this.searchResults.get(ArticleViewer.this.currentSearchIndex);
                if (result.block != this.parentBlock || (result.text != this.parentText && (!(result.text instanceof String) || this.parentText != null))) {
                    this.searchIndex = -1;
                    this.searchPath = null;
                } else if (this.searchIndex != result.index) {
                    LinkPath linkPath = new LinkPath(true);
                    this.searchPath = linkPath;
                    linkPath.setAllowReset(false);
                    this.searchPath.setCurrentLayout(this.textLayout, result.index, 0.0f);
                    this.searchPath.setBaselineShift(0);
                    this.textLayout.getSelectionPath(result.index, result.index + ArticleViewer.this.searchText.length(), this.searchPath);
                    this.searchPath.setAllowReset(true);
                }
            } else {
                this.searchIndex = -1;
                this.searchPath = null;
            }
            LinkPath linkPath2 = this.searchPath;
            if (linkPath2 != null) {
                canvas.drawPath(linkPath2, ArticleViewer.webpageSearchPaint);
            }
            LinkPath linkPath3 = this.textPath;
            if (linkPath3 != null) {
                canvas.drawPath(linkPath3, ArticleViewer.webpageUrlPaint);
            }
            LinkPath linkPath4 = this.markPath;
            if (linkPath4 != null) {
                canvas.drawPath(linkPath4, ArticleViewer.webpageMarkPaint);
            }
            if (ArticleViewer.this.links.draw(canvas, this)) {
                view.invalidate();
            }
            if (ArticleViewer.this.pressedLinkOwnerLayout == this && ArticleViewer.this.pressedLink == null && ArticleViewer.this.drawBlockSelection) {
                if (getLineCount() == 1) {
                    width = getLineWidth(0);
                    x = getLineLeft(0);
                } else {
                    width = getWidth();
                    x = 0.0f;
                }
                canvas.drawRect((-AndroidUtilities.dp(2.0f)) + x, 0.0f, x + width + AndroidUtilities.dp(2.0f), getHeight(), ArticleViewer.urlPaint);
            }
            this.textLayout.draw(canvas);
        }

        public CharSequence getText() {
            return this.textLayout.getText();
        }

        public int getLineCount() {
            return this.textLayout.getLineCount();
        }

        public int getLineAscent(int line) {
            return this.textLayout.getLineAscent(line);
        }

        public float getLineLeft(int line) {
            return this.textLayout.getLineLeft(line);
        }

        public float getLineWidth(int line) {
            return this.textLayout.getLineWidth(line);
        }

        public int getHeight() {
            return this.textLayout.getHeight();
        }

        public int getWidth() {
            return this.textLayout.getWidth();
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock
        public StaticLayout getLayout() {
            return this.textLayout;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock
        public int getX() {
            return this.x;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock
        public int getY() {
            return this.y;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock
        public int getRow() {
            return this.row;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock
        public CharSequence getPrefix() {
            return this.prefix;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class TextSizeCell extends FrameLayout {
        private int lastWidth;
        private SeekBarView sizeBar;
        private TextPaint textPaint;
        private int startFontSize = 12;
        private int endFontSize = 30;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextSizeCell(Context context) {
            super(context);
            ArticleViewer.this = r8;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.ArticleViewer.TextSizeCell.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean stop, float progress) {
                    int fontSize = Math.round(TextSizeCell.this.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * progress));
                    if (fontSize != SharedConfig.ivFontSize) {
                        SharedConfig.ivFontSize = fontSize;
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("iv_font_size", SharedConfig.ivFontSize);
                        editor.commit();
                        ArticleViewer.this.adapter[0].searchTextOffset.clear();
                        ArticleViewer.this.updatePaintSize();
                        TextSizeCell.this.invalidate();
                    }
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean pressed) {
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(TextSizeCell.this.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * TextSizeCell.this.sizeBar.getProgress())));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public int getStepsCount() {
                    return TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize;
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + SharedConfig.ivFontSize, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int w = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.lastWidth != w) {
                SeekBarView seekBarView = this.sizeBar;
                int i = SharedConfig.ivFontSize;
                int i2 = this.startFontSize;
                seekBarView.setProgress((i - i2) / (this.endFontSize - i2));
                this.lastWidth = w;
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }
    }

    /* loaded from: classes4.dex */
    public static class FontCell extends FrameLayout {
        private RadioButton radioButton;
        private TextView textView;

        public FontCell(Context context) {
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            RadioButton radioButton = new RadioButton(context);
            this.radioButton = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            this.radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            int i = 5;
            addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0 : 22, 13.0f, LocaleController.isRTL ? 22 : 0, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 17 : 62, 0.0f, LocaleController.isRTL ? 62 : 17, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void select(boolean value, boolean animated) {
            this.radioButton.setChecked(value, animated);
        }

        public void setTextAndTypeface(String text, Typeface typeface) {
            this.textView.setText(text);
            this.textView.setTypeface(typeface);
            setContentDescription(text);
            invalidate();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(RadioButton.class.getName());
            info.setChecked(this.radioButton.isChecked());
            info.setCheckable(true);
        }
    }

    /* loaded from: classes4.dex */
    public final class CheckForTap implements Runnable {
        private CheckForTap() {
            ArticleViewer.this = r1;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$2104(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
            }
        }
    }

    /* loaded from: classes4.dex */
    public class WindowView extends FrameLayout {
        private float alpha;
        private Runnable attachRunnable;
        private int bHeight;
        private int bWidth;
        private int bX;
        private int bY;
        private final Paint blackPaint = new Paint();
        private float innerTranslationX;
        private boolean maybeStartTracking;
        private boolean movingPage;
        private boolean selfLayout;
        private int startMovingHeaderHeight;
        private boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker tracker;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public WindowView(Context context) {
            super(context);
            ArticleViewer.this = r1;
        }

        @Override // android.view.ViewGroup, android.view.View
        public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
            DisplayCutout cutout;
            List<Rect> rects;
            WindowInsets oldInsets = (WindowInsets) ArticleViewer.this.lastInsets;
            ArticleViewer.this.lastInsets = insets;
            if ((oldInsets == null || !oldInsets.toString().equals(insets.toString())) && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.requestLayout();
            }
            if (Build.VERSION.SDK_INT >= 28 && ArticleViewer.this.parentActivity != null && (cutout = ArticleViewer.this.parentActivity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout()) != null && (rects = cutout.getBoundingRects()) != null && !rects.isEmpty()) {
                ArticleViewer articleViewer = ArticleViewer.this;
                boolean z = false;
                if (rects.get(0).height() != 0) {
                    z = true;
                }
                articleViewer.hasCutout = z;
            }
            return super.dispatchApplyWindowInsets(insets);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (Build.VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                setMeasuredDimension(widthSize, heightSize);
                WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    heightSize += AndroidUtilities.statusBarHeight;
                }
                int heightSize2 = heightSize - insets.getSystemWindowInsetBottom();
                widthSize -= insets.getSystemWindowInsetRight() + insets.getSystemWindowInsetLeft();
                if (insets.getSystemWindowInsetRight() != 0) {
                    this.bWidth = insets.getSystemWindowInsetRight();
                    this.bHeight = heightSize2;
                } else if (insets.getSystemWindowInsetLeft() != 0) {
                    this.bWidth = insets.getSystemWindowInsetLeft();
                    this.bHeight = heightSize2;
                } else {
                    this.bWidth = widthSize;
                    this.bHeight = insets.getStableInsetBottom();
                }
                heightSize = heightSize2 - insets.getSystemWindowInsetTop();
            } else {
                setMeasuredDimension(widthSize, heightSize);
            }
            boolean z = false;
            ArticleViewer.this.menuButton.setAdditionalYOffset(((-(ArticleViewer.this.currentHeaderHeight - AndroidUtilities.dp(56.0f))) / 2) + (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0));
            ArticleViewer articleViewer = ArticleViewer.this;
            if (heightSize < AndroidUtilities.displaySize.y - AndroidUtilities.dp(100.0f)) {
                z = true;
            }
            articleViewer.keyboardVisible = z;
            ArticleViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(heightSize, C.BUFFER_FLAG_ENCRYPTED));
            ArticleViewer.this.fullscreenVideoContainer.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(heightSize, C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ArticleViewer.this.pinchToZoomHelper.isInOverlayMode()) {
                ev.offsetLocation(-ArticleViewer.this.containerView.getX(), -ArticleViewer.this.containerView.getY());
                return ArticleViewer.this.pinchToZoomHelper.onTouchEvent(ev);
            }
            TextSelectionHelper.TextSelectionOverlay selectionOverlay = ArticleViewer.this.textSelectionHelper.getOverlayView(getContext());
            MotionEvent textSelectionEv = MotionEvent.obtain(ev);
            textSelectionEv.offsetLocation(-ArticleViewer.this.containerView.getX(), -ArticleViewer.this.containerView.getY());
            if (ArticleViewer.this.textSelectionHelper.isSelectionMode() && ArticleViewer.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                return true;
            }
            if (selectionOverlay.checkOnTap(ev)) {
                ev.setAction(3);
            }
            if (ev.getAction() == 0 && ArticleViewer.this.textSelectionHelper.isSelectionMode() && (ev.getY() < ArticleViewer.this.containerView.getTop() || ev.getY() > ArticleViewer.this.containerView.getBottom())) {
                if (!ArticleViewer.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int x;
            if (this.selfLayout) {
                return;
            }
            int width = right - left;
            if (ArticleViewer.this.anchorsOffsetMeasuredWidth != width) {
                for (int i = 0; i < ArticleViewer.this.listView.length; i++) {
                    for (Map.Entry<String, Integer> entry : ArticleViewer.this.adapter[i].anchorsOffset.entrySet()) {
                        entry.setValue(-1);
                    }
                }
                ArticleViewer.this.anchorsOffsetMeasuredWidth = width;
            }
            int y = 0;
            if (Build.VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                x = insets.getSystemWindowInsetLeft();
                if (insets.getSystemWindowInsetRight() != 0) {
                    this.bX = width - this.bWidth;
                    this.bY = 0;
                } else if (insets.getSystemWindowInsetLeft() != 0) {
                    this.bX = 0;
                    this.bY = 0;
                } else {
                    this.bX = 0;
                    this.bY = (bottom - top) - this.bHeight;
                }
                y = 0 + insets.getSystemWindowInsetTop();
            } else {
                x = 0;
            }
            ArticleViewer.this.containerView.layout(x, y, ArticleViewer.this.containerView.getMeasuredWidth() + x, ArticleViewer.this.containerView.getMeasuredHeight() + y);
            ArticleViewer.this.fullscreenVideoContainer.layout(x, y, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + x, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight() + y);
            if (ArticleViewer.this.runAfterKeyboardClose != null) {
                ArticleViewer.this.runAfterKeyboardClose.start();
                ArticleViewer.this.runAfterKeyboardClose = null;
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ArticleViewer.this.attachedToWindow = true;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ArticleViewer.this.attachedToWindow = false;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            handleTouchEvent(null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(ev) || super.onInterceptTouchEvent(ev));
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(event) || super.onTouchEvent(event));
        }

        public void setInnerTranslationX(float value) {
            this.innerTranslationX = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            float opacity;
            int width = getMeasuredWidth();
            int translationX = (int) this.innerTranslationX;
            int restoreCount = canvas.save();
            canvas.clipRect(translationX, 0, width, getHeight());
            boolean result = super.drawChild(canvas, child, drawingTime);
            canvas.restoreToCount(restoreCount);
            if (translationX != 0 && child == ArticleViewer.this.containerView) {
                float opacity2 = Math.min(0.8f, (width - translationX) / width);
                if (opacity2 >= 0.0f) {
                    opacity = opacity2;
                } else {
                    opacity = 0.0f;
                }
                ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                canvas.drawRect(0.0f, 0.0f, translationX, getHeight(), ArticleViewer.this.scrimPaint);
                float alpha = Math.max(0.0f, Math.min((width - translationX) / AndroidUtilities.dp(20.0f), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                ArticleViewer.this.layerShadowDrawable.draw(canvas);
            }
            return result;
        }

        public float getInnerTranslationX() {
            return this.innerTranslationX;
        }

        private void prepareForMoving(MotionEvent ev) {
            this.maybeStartTracking = false;
            this.startedTracking = true;
            this.startedTrackingX = (int) ev.getX();
            if (ArticleViewer.this.pagesStack.size() > 1) {
                this.movingPage = true;
                this.startMovingHeaderHeight = ArticleViewer.this.currentHeaderHeight;
                ArticleViewer.this.listView[1].setVisibility(0);
                ArticleViewer.this.listView[1].setAlpha(1.0f);
                ArticleViewer.this.listView[1].setTranslationX(0.0f);
                ArticleViewer.this.listView[0].setBackgroundColor(ArticleViewer.this.backgroundPaint.getColor());
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.updateInterfaceForCurrentPage((TLRPC.WebPage) articleViewer.pagesStack.get(ArticleViewer.this.pagesStack.size() - 2), true, -1);
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent event) {
            float distToMove;
            if (ArticleViewer.this.pageSwitchAnimation != null || ArticleViewer.this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0 || ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                return false;
            }
            if (event == null || event.getAction() != 0 || this.startedTracking || this.maybeStartTracking) {
                if (event == null || event.getAction() != 2 || event.getPointerId(0) != this.startedTrackingPointerId) {
                    if (event != null && event.getPointerId(0) == this.startedTrackingPointerId && (event.getAction() == 3 || event.getAction() == 1 || event.getAction() == 6)) {
                        if (this.tracker == null) {
                            this.tracker = VelocityTracker.obtain();
                        }
                        this.tracker.computeCurrentVelocity(1000);
                        float velX = this.tracker.getXVelocity();
                        float velY = this.tracker.getYVelocity();
                        if (!this.startedTracking && velX >= 3500.0f && velX > Math.abs(velY)) {
                            prepareForMoving(event);
                        }
                        if (this.startedTracking) {
                            View movingView = this.movingPage ? ArticleViewer.this.listView[0] : ArticleViewer.this.containerView;
                            float x = movingView.getX();
                            final boolean backAnimation = x < ((float) movingView.getMeasuredWidth()) / 3.0f && (velX < 3500.0f || velX < velY);
                            AnimatorSet animatorSet = new AnimatorSet();
                            if (!backAnimation) {
                                distToMove = movingView.getMeasuredWidth() - x;
                                if (this.movingPage) {
                                    animatorSet.playTogether(ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, movingView.getMeasuredWidth()));
                                } else {
                                    animatorSet.playTogether(ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, movingView.getMeasuredWidth()), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, movingView.getMeasuredWidth()));
                                }
                            } else {
                                distToMove = x;
                                if (this.movingPage) {
                                    animatorSet.playTogether(ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, 0.0f));
                                } else {
                                    animatorSet.playTogether(ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, 0.0f));
                                }
                            }
                            animatorSet.setDuration(Math.max((int) ((200.0f / movingView.getMeasuredWidth()) * distToMove), 50));
                            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.WindowView.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    if (WindowView.this.movingPage) {
                                        ArticleViewer.this.listView[0].setBackgroundDrawable(null);
                                        if (!backAnimation) {
                                            WebpageAdapter adapterToUpdate = ArticleViewer.this.adapter[1];
                                            ArticleViewer.this.adapter[1] = ArticleViewer.this.adapter[0];
                                            ArticleViewer.this.adapter[0] = adapterToUpdate;
                                            RecyclerListView listToUpdate = ArticleViewer.this.listView[1];
                                            ArticleViewer.this.listView[1] = ArticleViewer.this.listView[0];
                                            ArticleViewer.this.listView[0] = listToUpdate;
                                            LinearLayoutManager layoutManagerToUpdate = ArticleViewer.this.layoutManager[1];
                                            ArticleViewer.this.layoutManager[1] = ArticleViewer.this.layoutManager[0];
                                            ArticleViewer.this.layoutManager[0] = layoutManagerToUpdate;
                                            ArticleViewer.this.pagesStack.remove(ArticleViewer.this.pagesStack.size() - 1);
                                            ArticleViewer.this.textSelectionHelper.setParentView(ArticleViewer.this.listView[0]);
                                            ArticleViewer.this.textSelectionHelper.layoutManager = ArticleViewer.this.layoutManager[0];
                                            ArticleViewer.this.titleTextView.setText(ArticleViewer.this.adapter[0].currentPage.site_name == null ? "" : ArticleViewer.this.adapter[0].currentPage.site_name);
                                            ArticleViewer.this.textSelectionHelper.clear(true);
                                            ArticleViewer.this.headerView.invalidate();
                                        }
                                        ArticleViewer.this.listView[1].setVisibility(8);
                                        ArticleViewer.this.headerView.invalidate();
                                    } else if (!backAnimation) {
                                        ArticleViewer.this.saveCurrentPagePosition();
                                        ArticleViewer.this.onClosed();
                                    }
                                    WindowView.this.movingPage = false;
                                    WindowView.this.startedTracking = false;
                                    ArticleViewer.this.closeAnimationInProgress = false;
                                }
                            });
                            animatorSet.start();
                            ArticleViewer.this.closeAnimationInProgress = true;
                        } else {
                            this.maybeStartTracking = false;
                            this.startedTracking = false;
                            this.movingPage = false;
                        }
                        VelocityTracker velocityTracker = this.tracker;
                        if (velocityTracker != null) {
                            velocityTracker.recycle();
                            this.tracker = null;
                        }
                    } else if (event == null) {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                        this.movingPage = false;
                        VelocityTracker velocityTracker2 = this.tracker;
                        if (velocityTracker2 != null) {
                            velocityTracker2.recycle();
                            this.tracker = null;
                        }
                        if (ArticleViewer.this.textSelectionHelper != null && !ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                            ArticleViewer.this.textSelectionHelper.clear();
                        }
                    }
                } else {
                    if (this.tracker == null) {
                        this.tracker = VelocityTracker.obtain();
                    }
                    int dx = Math.max(0, (int) (event.getX() - this.startedTrackingX));
                    int dy = Math.abs(((int) event.getY()) - this.startedTrackingY);
                    this.tracker.addMovement(event);
                    if (this.maybeStartTracking && !this.startedTracking && dx >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                        prepareForMoving(event);
                    } else if (this.startedTracking) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                        if (this.movingPage) {
                            ArticleViewer.this.listView[0].setTranslationX(dx);
                        } else {
                            ArticleViewer.this.containerView.setTranslationX(dx);
                            setInnerTranslationX(dx);
                        }
                    }
                }
            } else {
                this.startedTrackingPointerId = event.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) event.getX();
                this.startedTrackingY = (int) event.getY();
                VelocityTracker velocityTracker3 = this.tracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.clear();
                }
            }
            return this.startedTracking;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            int i;
            super.dispatchDraw(canvas);
            if ((Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) && this.bWidth != 0 && this.bHeight != 0) {
                this.blackPaint.setAlpha((int) (ArticleViewer.this.windowView.getAlpha() * 255.0f));
                int i2 = this.bX;
                if (i2 == 0 && (i = this.bY) == 0) {
                    canvas.drawRect(i2, i, i2 + this.bWidth, i + this.bHeight, this.blackPaint);
                } else {
                    canvas.drawRect(i2 - getTranslationX(), this.bY, (this.bX + this.bWidth) - getTranslationX(), this.bY + this.bHeight, this.blackPaint);
                }
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            canvas.drawRect(this.innerTranslationX, 0.0f, w, h, ArticleViewer.this.backgroundPaint);
            if (Build.VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                canvas.drawRect(this.innerTranslationX, 0.0f, w, insets.getSystemWindowInsetTop(), ArticleViewer.this.statusBarPaint);
                if (ArticleViewer.this.hasCutout) {
                    int left = insets.getSystemWindowInsetLeft();
                    if (left != 0) {
                        canvas.drawRect(0.0f, 0.0f, left, h, ArticleViewer.this.statusBarPaint);
                    }
                    int right = insets.getSystemWindowInsetRight();
                    if (right != 0) {
                        canvas.drawRect(w - right, 0.0f, w, h, ArticleViewer.this.statusBarPaint);
                    }
                }
                canvas.drawRect(0.0f, h - insets.getStableInsetBottom(), w, h, ArticleViewer.this.navigationBarPaint);
            }
        }

        @Override // android.view.View
        public void setAlpha(float value) {
            ArticleViewer.this.backgroundPaint.setAlpha((int) (value * 255.0f));
            ArticleViewer.this.statusBarPaint.setAlpha((int) (255.0f * value));
            this.alpha = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        @Override // android.view.View
        public float getAlpha() {
            return this.alpha;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (event != null && event.getKeyCode() == 4 && event.getAction() == 1) {
                if (ArticleViewer.this.searchField.isFocused()) {
                    ArticleViewer.this.searchField.clearFocus();
                    AndroidUtilities.hideKeyboard(ArticleViewer.this.searchField);
                } else {
                    ArticleViewer.this.close(true, false);
                }
                return true;
            }
            return super.dispatchKeyEventPreIme(event);
        }
    }

    /* loaded from: classes4.dex */
    public class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
            ArticleViewer.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ArticleViewer.this.checkingForLongPress && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.checkingForLongPress = false;
                if (ArticleViewer.this.pressedLink != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0, 2);
                    ArticleViewer articleViewer = ArticleViewer.this;
                    articleViewer.showCopyPopup(((TextPaintUrlSpan) articleViewer.pressedLink.getSpan()).getUrl());
                    ArticleViewer.this.pressedLink = null;
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    if (ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    }
                } else if (ArticleViewer.this.pressedLinkOwnerView == null || !ArticleViewer.this.textSelectionHelper.isSelectable(ArticleViewer.this.pressedLinkOwnerView)) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.windowView.performHapticFeedback(0, 2);
                        int[] location = new int[2];
                        ArticleViewer.this.pressedLinkOwnerView.getLocationInWindow(location);
                        int y = (location[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.dp(54.0f);
                        if (y < 0) {
                            y = 0;
                        }
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                        ArticleViewer.this.drawBlockSelection = true;
                        ArticleViewer articleViewer2 = ArticleViewer.this;
                        articleViewer2.showPopup(articleViewer2.pressedLinkOwnerView, 48, 0, y);
                        ArticleViewer.this.listView[0].setLayoutFrozen(true);
                        ArticleViewer.this.listView[0].setLayoutFrozen(false);
                    }
                } else {
                    if (ArticleViewer.this.pressedLinkOwnerView.getTag() == null || ArticleViewer.this.pressedLinkOwnerView.getTag() != "bottomSheet" || ArticleViewer.this.textSelectionHelperBottomSheet == null) {
                        ArticleViewer.this.textSelectionHelper.trySelect(ArticleViewer.this.pressedLinkOwnerView);
                    } else {
                        ArticleViewer.this.textSelectionHelperBottomSheet.trySelect(ArticleViewer.this.pressedLinkOwnerView);
                    }
                    if (ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                        ArticleViewer.this.windowView.performHapticFeedback(0, 2);
                    }
                }
            }
        }
    }

    private void createPaint(boolean update) {
        if (quoteLinePaint == null) {
            quoteLinePaint = new Paint();
            preformattedBackgroundPaint = new Paint();
            Paint paint = new Paint(1);
            tableLinePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            tableLinePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
            Paint paint2 = new Paint();
            tableHalfLinePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            tableHalfLinePaint.setStrokeWidth(AndroidUtilities.dp(1.0f) / 2.0f);
            tableHeaderPaint = new Paint();
            tableStripPaint = new Paint();
            urlPaint = new Paint();
            webpageUrlPaint = new Paint(1);
            webpageSearchPaint = new Paint(1);
            photoBackgroundPaint = new Paint();
            dividerPaint = new Paint();
            webpageMarkPaint = new Paint(1);
        } else if (!update) {
            return;
        }
        int color2 = Theme.getColor(Theme.key_windowBackgroundWhite);
        float lightness = (((Color.red(color2) * 0.2126f) + (Color.green(color2) * 0.7152f)) + (Color.blue(color2) * 0.0722f)) / 255.0f;
        webpageSearchPaint.setColor(lightness <= 0.705f ? -3041234 : -6551);
        webpageUrlPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection) & 872415231);
        webpageUrlPaint.setPathEffect(LinkPath.getRoundedEffect());
        urlPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection) & 872415231);
        urlPaint.setPathEffect(LinkPath.getRoundedEffect());
        tableHalfLinePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputField));
        tableLinePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputField));
        photoBackgroundPaint.setColor(AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY);
        dividerPaint.setColor(Theme.getColor(Theme.key_divider));
        webpageMarkPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection) & 872415231);
        webpageMarkPaint.setPathEffect(LinkPath.getRoundedEffect());
        int color = Theme.getColor(Theme.key_switchTrack);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        tableStripPaint.setColor(Color.argb(20, r, g, b));
        tableHeaderPaint.setColor(Color.argb(34, r, g, b));
        int color3 = Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection);
        preformattedBackgroundPaint.setColor(Color.argb(20, Color.red(color3), Color.green(color3), Color.blue(color3)));
        quoteLinePaint.setColor(Theme.getColor(Theme.key_chat_inReplyLine));
    }

    public void showCopyPopup(final String urlFinal) {
        if (this.parentActivity == null) {
            return;
        }
        BottomSheet bottomSheet = this.linkSheet;
        if (bottomSheet != null) {
            bottomSheet.dismiss();
            this.linkSheet = null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
        builder.setTitle(urlFinal);
        builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ArticleViewer.this.m1548lambda$showCopyPopup$0$orgtelegramuiArticleViewer(urlFinal, dialogInterface, i);
            }
        });
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda22
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                ArticleViewer.this.m1549lambda$showCopyPopup$1$orgtelegramuiArticleViewer(dialogInterface);
            }
        });
        BottomSheet sheet = builder.create();
        showDialog(sheet);
    }

    /* renamed from: lambda$showCopyPopup$0$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1548lambda$showCopyPopup$0$orgtelegramuiArticleViewer(String urlFinal, DialogInterface dialog, int which) {
        String anchor;
        if (this.parentActivity == null) {
            return;
        }
        if (which == 0) {
            int index = urlFinal.lastIndexOf(35);
            if (index != -1) {
                String webPageUrl = !TextUtils.isEmpty(this.adapter[0].currentPage.cached_page.url) ? this.adapter[0].currentPage.cached_page.url.toLowerCase() : this.adapter[0].currentPage.url.toLowerCase();
                try {
                    anchor = URLDecoder.decode(urlFinal.substring(index + 1), "UTF-8");
                } catch (Exception e) {
                    anchor = "";
                }
                if (urlFinal.toLowerCase().contains(webPageUrl)) {
                    if (TextUtils.isEmpty(anchor)) {
                        this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                        checkScrollAnimated();
                        return;
                    }
                    scrollToAnchor(anchor);
                    return;
                }
            }
            Browser.openUrl(this.parentActivity, urlFinal);
        } else if (which == 1) {
            String url = urlFinal;
            if (url.startsWith(MailTo.MAILTO_SCHEME)) {
                url = url.substring(7);
            } else if (url.startsWith("tel:")) {
                url = url.substring(4);
            }
            AndroidUtilities.addToClipboard(url);
        }
    }

    /* renamed from: lambda$showCopyPopup$1$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1549lambda$showCopyPopup$1$orgtelegramuiArticleViewer(DialogInterface di) {
        this.links.clear();
    }

    public void showPopup(View parent, int gravity, int x, int y) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
            return;
        }
        if (this.popupLayout == null) {
            this.popupRect = new Rect();
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
            this.popupLayout.setBackgroundDrawable(this.parentActivity.getResources().getDrawable(R.drawable.menu_copy));
            this.popupLayout.setAnimationEnabled(false);
            this.popupLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda4
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return ArticleViewer.this.m1551lambda$showPopup$2$orgtelegramuiArticleViewer(view, motionEvent);
                }
            });
            this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda35
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    ArticleViewer.this.m1552lambda$showPopup$3$orgtelegramuiArticleViewer(keyEvent);
                }
            });
            this.popupLayout.setShownFromBottom(false);
            TextView textView = new TextView(this.parentActivity);
            this.deleteView = textView;
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            this.deleteView.setGravity(16);
            this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.deleteView.setTextSize(1, 15.0f);
            this.deleteView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.deleteView.setText(LocaleController.getString("Copy", R.string.Copy).toUpperCase());
            this.deleteView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ArticleViewer.this.m1553lambda$showPopup$4$orgtelegramuiArticleViewer(view);
                }
            });
            this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
            ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
            this.popupWindow = actionBarPopupWindow2;
            actionBarPopupWindow2.setAnimationEnabled(false);
            this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            this.popupWindow.setOutsideTouchable(true);
            this.popupWindow.setClippingEnabled(true);
            this.popupWindow.setInputMethodMode(2);
            this.popupWindow.setSoftInputMode(0);
            this.popupWindow.getContentView().setFocusableInTouchMode(true);
            this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda6
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    ArticleViewer.this.m1554lambda$showPopup$5$orgtelegramuiArticleViewer();
                }
            });
        }
        this.deleteView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
        if (actionBarPopupWindowLayout2 != null) {
            actionBarPopupWindowLayout2.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
        }
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.popupWindow.setFocusable(true);
        this.popupWindow.showAtLocation(parent, gravity, x, y);
        this.popupWindow.startAnimation();
    }

    /* renamed from: lambda$showPopup$2$org-telegram-ui-ArticleViewer */
    public /* synthetic */ boolean m1551lambda$showPopup$2$orgtelegramuiArticleViewer(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            v.getHitRect(this.popupRect);
            if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                this.popupWindow.dismiss();
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$showPopup$3$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1552lambda$showPopup$3$orgtelegramuiArticleViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* renamed from: lambda$showPopup$4$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1553lambda$showPopup$4$orgtelegramuiArticleViewer(View v) {
        DrawingText drawingText = this.pressedLinkOwnerLayout;
        if (drawingText != null) {
            AndroidUtilities.addToClipboard(drawingText.getText());
            if (Build.VERSION.SDK_INT < 31) {
                Toast.makeText(this.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
            }
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* renamed from: lambda$showPopup$5$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1554lambda$showPopup$5$orgtelegramuiArticleViewer() {
        View view = this.pressedLinkOwnerView;
        if (view != null) {
            this.pressedLinkOwnerLayout = null;
            view.invalidate();
            this.pressedLinkOwnerView = null;
        }
    }

    public TLRPC.RichText getBlockCaption(TLRPC.PageBlock block, int type) {
        if (type == 2) {
            TLRPC.RichText text1 = getBlockCaption(block, 0);
            if (text1 instanceof TLRPC.TL_textEmpty) {
                text1 = null;
            }
            TLRPC.RichText text2 = getBlockCaption(block, 1);
            if (text2 instanceof TLRPC.TL_textEmpty) {
                text2 = null;
            }
            if (text1 != null && text2 == null) {
                return text1;
            }
            if (text1 == null && text2 != null) {
                return text2;
            }
            if (text1 == null || text2 == null) {
                return null;
            }
            TLRPC.TL_textPlain text3 = new TLRPC.TL_textPlain();
            text3.text = " ";
            TLRPC.TL_textConcat textConcat = new TLRPC.TL_textConcat();
            textConcat.texts.add(text1);
            textConcat.texts.add(text3);
            textConcat.texts.add(text2);
            return textConcat;
        }
        if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
            TLRPC.TL_pageBlockEmbedPost blockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block;
            if (type == 0) {
                return blockEmbedPost.caption.text;
            }
            if (type == 1) {
                return blockEmbedPost.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
            TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) block;
            if (type == 0) {
                return pageBlockSlideshow.caption.text;
            }
            if (type == 1) {
                return pageBlockSlideshow.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
            if (type == 0) {
                return pageBlockPhoto.caption.text;
            }
            if (type == 1) {
                return pageBlockPhoto.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockCollage) {
            TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) block;
            if (type == 0) {
                return pageBlockCollage.caption.text;
            }
            if (type == 1) {
                return pageBlockCollage.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockEmbed) {
            TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) block;
            if (type == 0) {
                return pageBlockEmbed.caption.text;
            }
            if (type == 1) {
                return pageBlockEmbed.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockBlockquote) {
            TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) block;
            return pageBlockBlockquote.caption;
        } else if (block instanceof TLRPC.TL_pageBlockVideo) {
            TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
            if (type == 0) {
                return pageBlockVideo.caption.text;
            }
            if (type == 1) {
                return pageBlockVideo.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockPullquote) {
            TLRPC.TL_pageBlockPullquote pageBlockPullquote = (TLRPC.TL_pageBlockPullquote) block;
            return pageBlockPullquote.caption;
        } else if (block instanceof TLRPC.TL_pageBlockAudio) {
            TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
            if (type == 0) {
                return pageBlockAudio.caption.text;
            }
            if (type == 1) {
                return pageBlockAudio.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockCover) {
            TLRPC.TL_pageBlockCover pageBlockCover = (TLRPC.TL_pageBlockCover) block;
            return getBlockCaption(pageBlockCover.cover, type);
        } else if (block instanceof TLRPC.TL_pageBlockMap) {
            TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) block;
            if (type == 0) {
                return pageBlockMap.caption.text;
            }
            if (type == 1) {
                return pageBlockMap.caption.credit;
            }
        }
        return null;
    }

    private View getLastNonListCell(View view) {
        if (view instanceof BlockListItemCell) {
            BlockListItemCell cell = (BlockListItemCell) view;
            if (cell.blockLayout != null) {
                return getLastNonListCell(cell.blockLayout.itemView);
            }
        } else if (view instanceof BlockOrderedListItemCell) {
            BlockOrderedListItemCell cell2 = (BlockOrderedListItemCell) view;
            if (cell2.blockLayout != null) {
                return getLastNonListCell(cell2.blockLayout.itemView);
            }
        }
        return view;
    }

    public boolean isListItemBlock(TLRPC.PageBlock block) {
        return (block instanceof TL_pageBlockListItem) || (block instanceof TL_pageBlockOrderedListItem);
    }

    public TLRPC.PageBlock getLastNonListPageBlock(TLRPC.PageBlock block) {
        if (block instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem blockListItem = (TL_pageBlockListItem) block;
            return blockListItem.blockItem != null ? getLastNonListPageBlock(blockListItem.blockItem) : blockListItem.blockItem;
        } else if (block instanceof TL_pageBlockOrderedListItem) {
            TL_pageBlockOrderedListItem blockListItem2 = (TL_pageBlockOrderedListItem) block;
            return blockListItem2.blockItem != null ? getLastNonListPageBlock(blockListItem2.blockItem) : blockListItem2.blockItem;
        } else {
            return block;
        }
    }

    private boolean openAllParentBlocks(TL_pageBlockDetailsChild child) {
        TLRPC.PageBlock parentBlock = getLastNonListPageBlock(child.parent);
        if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
            TLRPC.TL_pageBlockDetails blockDetails = (TLRPC.TL_pageBlockDetails) parentBlock;
            if (blockDetails.open) {
                return false;
            }
            blockDetails.open = true;
            return true;
        } else if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
            return false;
        } else {
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            TLRPC.PageBlock parentBlock2 = getLastNonListPageBlock(parent.block);
            boolean opened = false;
            if (parentBlock2 instanceof TLRPC.TL_pageBlockDetails) {
                TLRPC.TL_pageBlockDetails blockDetails2 = (TLRPC.TL_pageBlockDetails) parentBlock2;
                if (!blockDetails2.open) {
                    blockDetails2.open = true;
                    opened = true;
                }
            }
            return openAllParentBlocks(parent) || opened;
        }
    }

    public TLRPC.PageBlock fixListBlock(TLRPC.PageBlock parentBlock, TLRPC.PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem blockListItem = (TL_pageBlockListItem) parentBlock;
            blockListItem.blockItem = childBlock;
            return parentBlock;
        } else if (parentBlock instanceof TL_pageBlockOrderedListItem) {
            TL_pageBlockOrderedListItem blockListItem2 = (TL_pageBlockOrderedListItem) parentBlock;
            blockListItem2.blockItem = childBlock;
            return parentBlock;
        } else {
            return childBlock;
        }
    }

    public TLRPC.PageBlock wrapInTableBlock(TLRPC.PageBlock parentBlock, TLRPC.PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem parent = (TL_pageBlockListItem) parentBlock;
            TL_pageBlockListItem item = new TL_pageBlockListItem();
            item.parent = parent.parent;
            item.blockItem = wrapInTableBlock(parent.blockItem, childBlock);
            return item;
        } else if (parentBlock instanceof TL_pageBlockOrderedListItem) {
            TL_pageBlockOrderedListItem parent2 = (TL_pageBlockOrderedListItem) parentBlock;
            TL_pageBlockOrderedListItem item2 = new TL_pageBlockOrderedListItem();
            item2.parent = parent2.parent;
            item2.blockItem = wrapInTableBlock(parent2.blockItem, childBlock);
            return item2;
        } else {
            return childBlock;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:85:0x029b  */
    /* JADX WARN: Removed duplicated region for block: B:91:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateInterfaceForCurrentPage(org.telegram.tgnet.TLRPC.WebPage r20, boolean r21, int r22) {
        /*
            Method dump skipped, instructions count: 688
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.updateInterfaceForCurrentPage(org.telegram.tgnet.TLRPC$WebPage, boolean, int):void");
    }

    private boolean addPageToStack(TLRPC.WebPage webPage, String anchor, int order) {
        saveCurrentPagePosition();
        this.pagesStack.add(webPage);
        showSearch(false);
        updateInterfaceForCurrentPage(webPage, false, order);
        return scrollToAnchor(anchor);
    }

    private boolean scrollToAnchor(String anchor) {
        if (TextUtils.isEmpty(anchor)) {
            return false;
        }
        String anchor2 = anchor.toLowerCase();
        Integer row = (Integer) this.adapter[0].anchors.get(anchor2);
        if (row == null) {
            return false;
        }
        TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) this.adapter[0].anchorsParent.get(anchor2);
        if (textAnchor != null) {
            TLRPC.TL_pageBlockParagraph paragraph = new TLRPC.TL_pageBlockParagraph();
            paragraph.text = textAnchor.text;
            int type = this.adapter[0].getTypeForBlock(paragraph);
            RecyclerView.ViewHolder holder = this.adapter[0].onCreateViewHolder(null, type);
            this.adapter[0].bindBlockToHolder(type, holder, paragraph, 0, 0);
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setApplyTopPadding(false);
            builder.setApplyBottomPadding(false);
            final LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
            this.textSelectionHelperBottomSheet = articleTextSelectionHelper;
            articleTextSelectionHelper.setParentView(linearLayout);
            this.textSelectionHelperBottomSheet.setCallback(new TextSelectionHelper.Callback() { // from class: org.telegram.ui.ArticleViewer.3
                @Override // org.telegram.ui.Cells.TextSelectionHelper.Callback
                public void onStateChanged(boolean isSelected) {
                    if (ArticleViewer.this.linkSheet != null) {
                        ArticleViewer.this.linkSheet.setDisableScroll(isSelected);
                    }
                }
            });
            TextView textView = new TextView(this.parentActivity) { // from class: org.telegram.ui.ArticleViewer.4
                @Override // android.widget.TextView, android.view.View
                protected void onDraw(Canvas canvas) {
                    canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, ArticleViewer.dividerPaint);
                    super.onDraw(canvas);
                }
            };
            textView.setTextSize(1, 16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setText(LocaleController.getString("InstantViewReference", R.string.InstantViewReference));
            textView.setGravity((this.adapter[0].isRtl ? 5 : 3) | 16);
            textView.setTextColor(getTextColor());
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
            holder.itemView.setTag("bottomSheet");
            linearLayout.addView(holder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
            View overlayView = this.textSelectionHelperBottomSheet.getOverlayView(this.parentActivity);
            FrameLayout frameLayout = new FrameLayout(this.parentActivity) { // from class: org.telegram.ui.ArticleViewer.5
                @Override // android.view.ViewGroup, android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    TextSelectionHelper.TextSelectionOverlay selectionOverlay = ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext());
                    MotionEvent textSelectionEv = MotionEvent.obtain(ev);
                    textSelectionEv.offsetLocation(-linearLayout.getX(), -linearLayout.getY());
                    if (!ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode() || !ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                        if (selectionOverlay.checkOnTap(ev)) {
                            ev.setAction(3);
                        }
                        if (ev.getAction() == 0 && ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode() && (ev.getY() < linearLayout.getTop() || ev.getY() > linearLayout.getBottom())) {
                            if (!ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                                return true;
                            }
                            return super.dispatchTouchEvent(ev);
                        }
                        return super.dispatchTouchEvent(ev);
                    }
                    return true;
                }

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(linearLayout.getMeasuredHeight() + AndroidUtilities.dp(8.0f), C.BUFFER_FLAG_ENCRYPTED);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec2);
                }
            };
            builder.setDelegate(new BottomSheet.BottomSheetDelegate() { // from class: org.telegram.ui.ArticleViewer.6
                @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate, org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
                public boolean canDismiss() {
                    if (ArticleViewer.this.textSelectionHelperBottomSheet != null && ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode()) {
                        ArticleViewer.this.textSelectionHelperBottomSheet.clear();
                        return false;
                    }
                    return true;
                }
            });
            frameLayout.addView(linearLayout, -1, -2);
            frameLayout.addView(overlayView, -1, -2);
            builder.setCustomView(frameLayout);
            if (this.textSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
            }
            BottomSheet create = builder.create();
            this.linkSheet = create;
            showDialog(create);
        } else if (row.intValue() < 0 || row.intValue() >= this.adapter[0].blocks.size()) {
            return false;
        } else {
            TLRPC.PageBlock originalBlock = (TLRPC.PageBlock) this.adapter[0].blocks.get(row.intValue());
            TLRPC.PageBlock block = getLastNonListPageBlock(originalBlock);
            if ((block instanceof TL_pageBlockDetailsChild) && openAllParentBlocks((TL_pageBlockDetailsChild) block)) {
                this.adapter[0].updateRows();
                this.adapter[0].notifyDataSetChanged();
            }
            int position = this.adapter[0].localBlocks.indexOf(originalBlock);
            if (position != -1) {
                row = Integer.valueOf(position);
            }
            Integer offset = (Integer) this.adapter[0].anchorsOffset.get(anchor2);
            if (offset == null) {
                offset = 0;
            } else if (offset.intValue() == -1) {
                int type2 = this.adapter[0].getTypeForBlock(originalBlock);
                RecyclerView.ViewHolder holder2 = this.adapter[0].onCreateViewHolder(null, type2);
                this.adapter[0].bindBlockToHolder(type2, holder2, originalBlock, 0, 0);
                holder2.itemView.measure(View.MeasureSpec.makeMeasureSpec(this.listView[0].getMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                Integer offset2 = (Integer) this.adapter[0].anchorsOffset.get(anchor2);
                if (offset2.intValue() != -1) {
                    offset = offset2;
                } else {
                    offset = 0;
                }
            }
            this.layoutManager[0].scrollToPositionWithOffset(row.intValue(), (this.currentHeaderHeight - AndroidUtilities.dp(56.0f)) - offset.intValue());
        }
        return true;
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        ArrayList<TLRPC.WebPage> arrayList = this.pagesStack;
        arrayList.remove(arrayList.size() - 1);
        ArrayList<TLRPC.WebPage> arrayList2 = this.pagesStack;
        updateInterfaceForCurrentPage(arrayList2.get(arrayList2.size() - 1), false, -1);
        return true;
    }

    protected void startCheckLongPress(float x, float y, View parentView) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper;
        if (this.checkingForLongPress) {
            return;
        }
        this.checkingForLongPress = true;
        if (this.pendingCheckForTap == null) {
            this.pendingCheckForTap = new CheckForTap();
        }
        if (parentView.getTag() != null && parentView.getTag() == "bottomSheet" && (articleTextSelectionHelper = this.textSelectionHelperBottomSheet) != null) {
            articleTextSelectionHelper.setMaybeView((int) x, (int) y, parentView);
        } else {
            this.textSelectionHelper.setMaybeView((int) x, (int) y, parentView);
        }
        this.windowView.postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }

    protected void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            this.windowView.removeCallbacks(checkForLongPress);
            this.pendingCheckForLongPress = null;
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            this.windowView.removeCallbacks(checkForTap);
            this.pendingCheckForTap = null;
        }
    }

    private int getTextFlags(TLRPC.RichText richText) {
        if (richText instanceof TLRPC.TL_textFixed) {
            return getTextFlags(richText.parentRichText) | 4;
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getTextFlags(richText.parentRichText) | 2;
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getTextFlags(richText.parentRichText) | 1;
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getTextFlags(richText.parentRichText) | 16;
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getTextFlags(richText.parentRichText) | 32;
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TLRPC.TL_textPhone) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            TLRPC.TL_textUrl textUrl = (TLRPC.TL_textUrl) richText;
            if (textUrl.webpage_id != 0) {
                return getTextFlags(richText.parentRichText) | 512;
            }
            return getTextFlags(richText.parentRichText) | 8;
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getTextFlags(richText.parentRichText) | 128;
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getTextFlags(richText.parentRichText) | 256;
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getTextFlags(richText.parentRichText) | 64;
            }
            if (richText != null) {
                return getTextFlags(richText.parentRichText);
            }
            return 0;
        }
    }

    private TLRPC.RichText getLastRichText(TLRPC.RichText richText) {
        if (richText == null) {
            return null;
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getLastRichText(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getLastRichText(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getLastRichText(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getLastRichText(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getLastRichText(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getLastRichText(((TLRPC.TL_textEmail) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return getLastRichText(((TLRPC.TL_textUrl) richText).text);
        }
        if (richText instanceof TLRPC.TL_textAnchor) {
            getLastRichText(((TLRPC.TL_textAnchor) richText).text);
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getLastRichText(((TLRPC.TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getLastRichText(((TLRPC.TL_textSuperscript) richText).text);
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getLastRichText(((TLRPC.TL_textMarked) richText).text);
            }
            if (richText instanceof TLRPC.TL_textPhone) {
                return getLastRichText(((TLRPC.TL_textPhone) richText).text);
            }
        }
        return richText;
    }

    public CharSequence getText(WebpageAdapter adapter, View parentView, TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock, int maxWidth) {
        return getText(adapter.currentPage, parentView, parentRichText, richText, parentBlock, maxWidth);
    }

    public CharSequence getText(TLRPC.WebPage page, View parentView, TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock, int maxWidth) {
        MetricAffectingSpan span;
        MetricAffectingSpan span2;
        TextPaint textPaint = null;
        if (richText == null) {
            return null;
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textFixed) richText).text, parentBlock, maxWidth);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textItalic) richText).text, parentBlock, maxWidth);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textBold) richText).text, parentBlock, maxWidth);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textUnderline) richText).text, parentBlock, maxWidth);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textStrike) richText).text, parentBlock, maxWidth);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textEmail) richText).text, parentBlock, maxWidth));
            MetricAffectingSpan[] innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (spannableStringBuilder.length() != 0) {
                if (innerSpans == null || innerSpans.length == 0) {
                    textPaint = getTextPaint(parentRichText, richText, parentBlock);
                }
                spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, MailTo.MAILTO_SCHEME + getUrl(richText)), 0, spannableStringBuilder.length(), 33);
            }
            return spannableStringBuilder;
        }
        long j = 0;
        if (richText instanceof TLRPC.TL_textUrl) {
            TLRPC.TL_textUrl textUrl = (TLRPC.TL_textUrl) richText;
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textUrl) richText).text, parentBlock, maxWidth));
            MetricAffectingSpan[] innerSpans2 = (MetricAffectingSpan[]) spannableStringBuilder2.getSpans(0, spannableStringBuilder2.length(), MetricAffectingSpan.class);
            TextPaint paint = (innerSpans2 == null || innerSpans2.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null;
            if (textUrl.webpage_id != 0) {
                span2 = new TextPaintWebpageUrlSpan(paint, getUrl(richText));
            } else {
                span2 = new TextPaintUrlSpan(paint, getUrl(richText));
            }
            if (spannableStringBuilder2.length() != 0) {
                spannableStringBuilder2.setSpan(span2, 0, spannableStringBuilder2.length(), 33);
            }
            return spannableStringBuilder2;
        } else if (richText instanceof TLRPC.TL_textPlain) {
            return ((TLRPC.TL_textPlain) richText).text;
        } else {
            if (richText instanceof TLRPC.TL_textAnchor) {
                TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) richText;
                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(getText(page, parentView, parentRichText, textAnchor.text, parentBlock, maxWidth));
                spannableStringBuilder3.setSpan(new AnchorSpan(textAnchor.name), 0, spannableStringBuilder3.length(), 17);
                return spannableStringBuilder3;
            } else if (richText instanceof TLRPC.TL_textEmpty) {
                return "";
            } else {
                if (richText instanceof TLRPC.TL_textConcat) {
                    SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder();
                    int count = richText.texts.size();
                    int a = 0;
                    while (a < count) {
                        TLRPC.RichText innerRichText = richText.texts.get(a);
                        TLRPC.RichText lastRichText = getLastRichText(innerRichText);
                        boolean extraSpace = maxWidth >= 0 && (innerRichText instanceof TLRPC.TL_textUrl) && ((TLRPC.TL_textUrl) innerRichText).webpage_id != j;
                        if (extraSpace && spannableStringBuilder4.length() != 0 && spannableStringBuilder4.charAt(spannableStringBuilder4.length() - 1) != '\n') {
                            spannableStringBuilder4.append((CharSequence) " ");
                            spannableStringBuilder4.setSpan(new TextSelectionHelper.IgnoreCopySpannable(), spannableStringBuilder4.length() - 1, spannableStringBuilder4.length(), 33);
                        }
                        int a2 = a;
                        int count2 = count;
                        CharSequence innerText = getText(page, parentView, parentRichText, innerRichText, parentBlock, maxWidth);
                        int flags = getTextFlags(lastRichText);
                        int startLength = spannableStringBuilder4.length();
                        spannableStringBuilder4.append(innerText);
                        if (flags != 0 && !(innerText instanceof SpannableStringBuilder)) {
                            if ((flags & 8) != 0 || (flags & 512) != 0) {
                                String url = getUrl(innerRichText);
                                if (url == null) {
                                    url = getUrl(parentRichText);
                                }
                                if ((flags & 512) != 0) {
                                    span = new TextPaintWebpageUrlSpan(getTextPaint(parentRichText, lastRichText, parentBlock), url);
                                } else {
                                    span = new TextPaintUrlSpan(getTextPaint(parentRichText, lastRichText, parentBlock), url);
                                }
                                if (startLength != spannableStringBuilder4.length()) {
                                    spannableStringBuilder4.setSpan(span, startLength, spannableStringBuilder4.length(), 33);
                                }
                            } else if (startLength != spannableStringBuilder4.length()) {
                                spannableStringBuilder4.setSpan(new TextPaintSpan(getTextPaint(parentRichText, lastRichText, parentBlock)), startLength, spannableStringBuilder4.length(), 33);
                            }
                        }
                        if (extraSpace && a2 != count2 - 1) {
                            spannableStringBuilder4.append((CharSequence) " ");
                            spannableStringBuilder4.setSpan(new TextSelectionHelper.IgnoreCopySpannable(), spannableStringBuilder4.length() - 1, spannableStringBuilder4.length(), 33);
                        }
                        a = a2 + 1;
                        count = count2;
                        j = 0;
                    }
                    return spannableStringBuilder4;
                } else if (richText instanceof TLRPC.TL_textSubscript) {
                    return getText(page, parentView, parentRichText, ((TLRPC.TL_textSubscript) richText).text, parentBlock, maxWidth);
                } else {
                    if (richText instanceof TLRPC.TL_textSuperscript) {
                        return getText(page, parentView, parentRichText, ((TLRPC.TL_textSuperscript) richText).text, parentBlock, maxWidth);
                    }
                    if (richText instanceof TLRPC.TL_textMarked) {
                        SpannableStringBuilder spannableStringBuilder5 = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textMarked) richText).text, parentBlock, maxWidth));
                        MetricAffectingSpan[] innerSpans3 = (MetricAffectingSpan[]) spannableStringBuilder5.getSpans(0, spannableStringBuilder5.length(), MetricAffectingSpan.class);
                        if (spannableStringBuilder5.length() != 0) {
                            spannableStringBuilder5.setSpan(new TextPaintMarkSpan((innerSpans3 == null || innerSpans3.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null), 0, spannableStringBuilder5.length(), 33);
                        }
                        return spannableStringBuilder5;
                    } else if (richText instanceof TLRPC.TL_textPhone) {
                        SpannableStringBuilder spannableStringBuilder6 = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textPhone) richText).text, parentBlock, maxWidth));
                        MetricAffectingSpan[] innerSpans4 = (MetricAffectingSpan[]) spannableStringBuilder6.getSpans(0, spannableStringBuilder6.length(), MetricAffectingSpan.class);
                        if (spannableStringBuilder6.length() != 0) {
                            spannableStringBuilder6.setSpan(new TextPaintUrlSpan((innerSpans4 == null || innerSpans4.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null, "tel:" + getUrl(richText)), 0, spannableStringBuilder6.length(), 33);
                        }
                        return spannableStringBuilder6;
                    } else if (!(richText instanceof TLRPC.TL_textImage)) {
                        return "not supported " + richText;
                    } else {
                        TLRPC.TL_textImage textImage = (TLRPC.TL_textImage) richText;
                        TLRPC.Document document = WebPageUtils.getDocumentWithId(page, textImage.document_id);
                        if (document == null) {
                            return "";
                        }
                        SpannableStringBuilder spannableStringBuilder7 = new SpannableStringBuilder("*");
                        int w = AndroidUtilities.dp(textImage.w);
                        int h = AndroidUtilities.dp(textImage.h);
                        int maxWidth2 = Math.abs(maxWidth);
                        if (w > maxWidth2) {
                            float scale = maxWidth2 / w;
                            w = maxWidth2;
                            h = (int) (h * scale);
                        }
                        if (parentView != null) {
                            int color = Theme.getColor(Theme.key_windowBackgroundWhite);
                            float lightness = (((Color.red(color) * 0.2126f) + (Color.green(color) * 0.7152f)) + (Color.blue(color) * 0.0722f)) / 255.0f;
                            spannableStringBuilder7.setSpan(new TextPaintImageReceiverSpan(parentView, document, page, w, h, false, lightness <= 0.705f), 0, spannableStringBuilder7.length(), 33);
                        }
                        return spannableStringBuilder7;
                    }
                }
            }
        }
    }

    public static CharSequence getPlainText(TLRPC.RichText richText) {
        if (richText == null) {
            return "";
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getPlainText(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getPlainText(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getPlainText(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getPlainText(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getPlainText(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getPlainText(((TLRPC.TL_textEmail) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return getPlainText(((TLRPC.TL_textUrl) richText).text);
        }
        if (richText instanceof TLRPC.TL_textPlain) {
            return ((TLRPC.TL_textPlain) richText).text;
        }
        if (richText instanceof TLRPC.TL_textAnchor) {
            return getPlainText(((TLRPC.TL_textAnchor) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmpty) {
            return "";
        }
        if (richText instanceof TLRPC.TL_textConcat) {
            StringBuilder stringBuilder = new StringBuilder();
            int count = richText.texts.size();
            for (int a = 0; a < count; a++) {
                stringBuilder.append(getPlainText(richText.texts.get(a)));
            }
            return stringBuilder;
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getPlainText(((TLRPC.TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getPlainText(((TLRPC.TL_textSuperscript) richText).text);
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getPlainText(((TLRPC.TL_textMarked) richText).text);
            }
            if (richText instanceof TLRPC.TL_textPhone) {
                return getPlainText(((TLRPC.TL_textPhone) richText).text);
            }
            boolean z = richText instanceof TLRPC.TL_textImage;
            return "";
        }
    }

    public static String getUrl(TLRPC.RichText richText) {
        if (richText instanceof TLRPC.TL_textFixed) {
            return getUrl(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getUrl(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getUrl(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getUrl(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getUrl(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return ((TLRPC.TL_textEmail) richText).email;
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return ((TLRPC.TL_textUrl) richText).url;
        }
        if (richText instanceof TLRPC.TL_textPhone) {
            return ((TLRPC.TL_textPhone) richText).phone;
        }
        return null;
    }

    public int getTextColor() {
        return Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
    }

    public int getLinkTextColor() {
        return Theme.getColor(Theme.key_windowBackgroundWhiteLinkText);
    }

    public static int getGrayTextColor() {
        return Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
    }

    private TextPaint getTextPaint(TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock) {
        int flags = getTextFlags(richText);
        SparseArray<TextPaint> currentMap = null;
        int textSize = AndroidUtilities.dp(14.0f);
        int textColor = SupportMenu.CATEGORY_MASK;
        int additionalSize = AndroidUtilities.dp(SharedConfig.ivFontSize - 16);
        if (parentBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) parentBlock;
            if (pageBlockPhoto.caption.text == richText || pageBlockPhoto.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockMap) {
            TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) parentBlock;
            if (pageBlockMap.caption.text == richText || pageBlockMap.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockTitle) {
            currentMap = titleTextPaints;
            textSize = AndroidUtilities.dp(23.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockKicker) {
            currentMap = kickerTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockAuthorDate) {
            currentMap = authorTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockFooter) {
            currentMap = footerTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockSubtitle) {
            currentMap = subtitleTextPaints;
            textSize = AndroidUtilities.dp(20.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockHeader) {
            currentMap = headerTextPaints;
            textSize = AndroidUtilities.dp(20.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockSubheader) {
            currentMap = subheaderTextPaints;
            textSize = AndroidUtilities.dp(17.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockBlockquote) {
            TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) parentBlock;
            if (pageBlockBlockquote.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote.caption == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TLRPC.TL_pageBlockPullquote) {
            TLRPC.TL_pageBlockPullquote pageBlockBlockquote2 = (TLRPC.TL_pageBlockPullquote) parentBlock;
            if (pageBlockBlockquote2.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote2.caption == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TLRPC.TL_pageBlockPreformatted) {
            currentMap = preformattedTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockParagraph) {
            currentMap = paragraphTextPaints;
            textSize = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (isListItemBlock(parentBlock)) {
            currentMap = listTextPaints;
            textSize = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockEmbed) {
            TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) parentBlock;
            if (pageBlockEmbed.caption.text == richText || pageBlockEmbed.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockSlideshow) {
            TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) parentBlock;
            if (pageBlockSlideshow.caption.text == richText || pageBlockSlideshow.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockCollage) {
            TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) parentBlock;
            if (pageBlockCollage.caption.text == richText || pageBlockCollage.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockEmbedPost) {
            TLRPC.TL_pageBlockEmbedPost pageBlockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) parentBlock;
            if (richText == pageBlockEmbedPost.caption.text) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            } else if (richText == pageBlockEmbedPost.caption.credit) {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
                textColor = getGrayTextColor();
            } else if (richText != null) {
                currentMap = embedPostTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            }
        } else if (parentBlock instanceof TLRPC.TL_pageBlockVideo) {
            TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) parentBlock;
            if (richText == pageBlockVideo.caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockAudio) {
            TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) parentBlock;
            if (richText == pageBlockAudio.caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockRelatedArticles) {
            currentMap = relatedArticleTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
            currentMap = detailsTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockTable) {
            currentMap = tableTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        }
        if ((flags & 256) != 0 || (flags & 128) != 0) {
            textSize -= AndroidUtilities.dp(4.0f);
        }
        if (currentMap == null) {
            if (errorTextPaint == null) {
                TextPaint textPaint = new TextPaint(1);
                errorTextPaint = textPaint;
                textPaint.setColor(SupportMenu.CATEGORY_MASK);
            }
            errorTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint paint = currentMap.get(flags);
        if (paint == null) {
            paint = new TextPaint(1);
            if ((flags & 4) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else if (!(parentBlock instanceof TLRPC.TL_pageBlockRelatedArticles)) {
                if (this.selectedFont == 1 || (parentBlock instanceof TLRPC.TL_pageBlockTitle) || (parentBlock instanceof TLRPC.TL_pageBlockKicker) || (parentBlock instanceof TLRPC.TL_pageBlockHeader) || (parentBlock instanceof TLRPC.TL_pageBlockSubtitle) || (parentBlock instanceof TLRPC.TL_pageBlockSubheader)) {
                    if ((parentBlock instanceof TLRPC.TL_pageBlockTitle) || (parentBlock instanceof TLRPC.TL_pageBlockHeader) || (parentBlock instanceof TLRPC.TL_pageBlockSubtitle) || (parentBlock instanceof TLRPC.TL_pageBlockSubheader)) {
                        paint.setTypeface(AndroidUtilities.getTypeface("fonts/mw_bold.ttf"));
                    } else if ((flags & 1) != 0 && (flags & 2) != 0) {
                        paint.setTypeface(Typeface.create(C.SERIF_NAME, 3));
                    } else if ((flags & 1) != 0) {
                        paint.setTypeface(Typeface.create(C.SERIF_NAME, 1));
                    } else if ((flags & 2) != 0) {
                        paint.setTypeface(Typeface.create(C.SERIF_NAME, 2));
                    } else {
                        paint.setTypeface(Typeface.create(C.SERIF_NAME, 0));
                    }
                } else if ((flags & 1) != 0 && (flags & 2) != 0) {
                    paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
                } else if ((flags & 1) != 0) {
                    paint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                } else if ((flags & 2) != 0) {
                    paint.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
                }
            } else {
                paint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            }
            if ((flags & 32) != 0) {
                paint.setFlags(paint.getFlags() | 16);
            }
            if ((flags & 16) != 0) {
                paint.setFlags(paint.getFlags() | 8);
            }
            if ((flags & 8) != 0 || (flags & 512) != 0) {
                paint.setFlags(paint.getFlags());
                textColor = getLinkTextColor();
            }
            if ((flags & 256) != 0) {
                paint.baselineShift -= AndroidUtilities.dp(6.0f);
            } else if ((flags & 128) != 0) {
                paint.baselineShift += AndroidUtilities.dp(2.0f);
            }
            paint.setColor(textColor);
            currentMap.put(flags, paint);
        }
        paint.setTextSize(textSize + additionalSize);
        return paint;
    }

    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, int textY, TLRPC.PageBlock parentBlock, Layout.Alignment align, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, align, 0, parentAdapter);
    }

    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, int textY, TLRPC.PageBlock parentBlock, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, textY, parentBlock, Layout.Alignment.ALIGN_NORMAL, 0, parentAdapter);
    }

    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, int textY, TLRPC.PageBlock parentBlock, Layout.Alignment align, int maxLines, WebpageAdapter parentAdapter) {
        int width2;
        CharSequence text;
        TextPaint paint;
        StaticLayout result;
        CharSequence text2;
        int additionalSize;
        String lowerString;
        if (plainText != null || (richText != null && !(richText instanceof TLRPC.TL_textEmpty))) {
            if (width >= 0) {
                width2 = width;
            } else {
                width2 = AndroidUtilities.dp(10.0f);
            }
            if (plainText != null) {
                text = plainText;
            } else {
                text = getText(parentAdapter, parentView, richText, richText, parentBlock, width2);
            }
            if (TextUtils.isEmpty(text)) {
                return null;
            }
            int additionalSize2 = AndroidUtilities.dp(SharedConfig.ivFontSize - 16);
            if ((parentBlock instanceof TLRPC.TL_pageBlockEmbedPost) && richText == null) {
                TLRPC.TL_pageBlockEmbedPost pageBlockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) parentBlock;
                if (pageBlockEmbedPost.author == plainText) {
                    if (embedPostAuthorPaint == null) {
                        TextPaint textPaint = new TextPaint(1);
                        embedPostAuthorPaint = textPaint;
                        textPaint.setColor(getTextColor());
                    }
                    embedPostAuthorPaint.setTextSize(AndroidUtilities.dp(15.0f) + additionalSize2);
                    paint = embedPostAuthorPaint;
                } else {
                    if (embedPostDatePaint == null) {
                        TextPaint textPaint2 = new TextPaint(1);
                        embedPostDatePaint = textPaint2;
                        textPaint2.setColor(getGrayTextColor());
                    }
                    embedPostDatePaint.setTextSize(AndroidUtilities.dp(14.0f) + additionalSize2);
                    paint = embedPostDatePaint;
                }
            } else if (parentBlock instanceof TLRPC.TL_pageBlockChannel) {
                if (channelNamePaint == null) {
                    TextPaint textPaint3 = new TextPaint(1);
                    channelNamePaint = textPaint3;
                    textPaint3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    TextPaint textPaint4 = new TextPaint(1);
                    channelNamePhotoPaint = textPaint4;
                    textPaint4.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                }
                channelNamePaint.setColor(getTextColor());
                channelNamePaint.setTextSize(AndroidUtilities.dp(15.0f));
                channelNamePhotoPaint.setColor(-1);
                channelNamePhotoPaint.setTextSize(AndroidUtilities.dp(15.0f));
                paint = parentAdapter.channelBlock != null ? channelNamePhotoPaint : channelNamePaint;
            } else if (!(parentBlock instanceof TL_pageBlockRelatedArticlesChild)) {
                if (isListItemBlock(parentBlock) && plainText != null) {
                    if (listTextPointerPaint == null) {
                        TextPaint textPaint5 = new TextPaint(1);
                        listTextPointerPaint = textPaint5;
                        textPaint5.setColor(getTextColor());
                    }
                    if (listTextNumPaint == null) {
                        TextPaint textPaint6 = new TextPaint(1);
                        listTextNumPaint = textPaint6;
                        textPaint6.setColor(getTextColor());
                    }
                    listTextPointerPaint.setTextSize(AndroidUtilities.dp(19.0f) + additionalSize2);
                    listTextNumPaint.setTextSize(AndroidUtilities.dp(16.0f) + additionalSize2);
                    if ((parentBlock instanceof TL_pageBlockListItem) && !((TL_pageBlockListItem) parentBlock).parent.pageBlockList.ordered) {
                        paint = listTextPointerPaint;
                    } else {
                        paint = listTextNumPaint;
                    }
                } else {
                    paint = getTextPaint(richText, richText, parentBlock);
                }
            } else {
                TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) parentBlock;
                if (plainText == pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num).title) {
                    if (relatedArticleHeaderPaint == null) {
                        TextPaint textPaint7 = new TextPaint(1);
                        relatedArticleHeaderPaint = textPaint7;
                        textPaint7.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    }
                    relatedArticleHeaderPaint.setColor(getTextColor());
                    relatedArticleHeaderPaint.setTextSize(AndroidUtilities.dp(15.0f) + additionalSize2);
                    paint = relatedArticleHeaderPaint;
                } else {
                    if (relatedArticleTextPaint == null) {
                        relatedArticleTextPaint = new TextPaint(1);
                    }
                    relatedArticleTextPaint.setColor(getGrayTextColor());
                    relatedArticleTextPaint.setTextSize(AndroidUtilities.dp(14.0f) + additionalSize2);
                    paint = relatedArticleTextPaint;
                }
            }
            char c = 0;
            if (maxLines != 0) {
                if (parentBlock instanceof TLRPC.TL_pageBlockPullquote) {
                    result = StaticLayoutEx.createStaticLayout(text, paint, width2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, width2, maxLines);
                } else {
                    result = StaticLayoutEx.createStaticLayout(text, paint, width2, align, 1.0f, AndroidUtilities.dp(4.0f), false, TextUtils.TruncateAt.END, width2, maxLines);
                }
            } else {
                if (text.charAt(text.length() - 1) == '\n') {
                    text = text.subSequence(0, text.length() - 1);
                }
                if (parentBlock instanceof TLRPC.TL_pageBlockPullquote) {
                    result = new StaticLayout(text, paint, width2, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                } else {
                    result = new StaticLayout(text, paint, width2, align, 1.0f, AndroidUtilities.dp(4.0f), false);
                }
            }
            if (result == null) {
                return null;
            }
            CharSequence finalText = result.getText();
            LinkPath textPath = null;
            LinkPath markPath = null;
            if (textY >= 0 && result != null && !this.searchResults.isEmpty() && this.searchText != null) {
                String lowerString2 = text.toString().toLowerCase();
                int startIndex = 0;
                while (true) {
                    int index = lowerString2.indexOf(this.searchText, startIndex);
                    if (index < 0) {
                        break;
                    }
                    startIndex = index + this.searchText.length();
                    if (index == 0 || AndroidUtilities.isPunctuationCharacter(lowerString2.charAt(index - 1))) {
                        HashMap hashMap = this.adapter[c].searchTextOffset;
                        StringBuilder sb = new StringBuilder();
                        lowerString = lowerString2;
                        String lowerString3 = this.searchText;
                        sb.append(lowerString3);
                        sb.append(parentBlock);
                        sb.append(richText);
                        sb.append(index);
                        hashMap.put(sb.toString(), Integer.valueOf(textY + result.getLineTop(result.getLineForOffset(index))));
                    } else {
                        lowerString = lowerString2;
                    }
                    lowerString2 = lowerString;
                    c = 0;
                }
            }
            if (result != null && (finalText instanceof Spanned)) {
                Spanned spanned = (Spanned) finalText;
                try {
                    AnchorSpan[] innerSpans = (AnchorSpan[]) spanned.getSpans(0, spanned.length(), AnchorSpan.class);
                    int linesCount = result.getLineCount();
                    if (innerSpans != null && innerSpans.length > 0) {
                        int a = 0;
                        while (a < innerSpans.length) {
                            if (linesCount > 1) {
                                text2 = text;
                                additionalSize = additionalSize2;
                                parentAdapter.anchorsOffset.put(innerSpans[a].getName(), Integer.valueOf(textY + result.getLineTop(result.getLineForOffset(spanned.getSpanStart(innerSpans[a])))));
                            } else {
                                text2 = text;
                                try {
                                    additionalSize = additionalSize2;
                                    try {
                                        parentAdapter.anchorsOffset.put(innerSpans[a].getName(), Integer.valueOf(textY));
                                    } catch (Exception e) {
                                    }
                                } catch (Exception e2) {
                                }
                            }
                            a++;
                            additionalSize2 = additionalSize;
                            text = text2;
                        }
                    }
                } catch (Exception e3) {
                }
                float f = 0.0f;
                try {
                    TextPaintWebpageUrlSpan[] innerSpans2 = (TextPaintWebpageUrlSpan[]) spanned.getSpans(0, spanned.length(), TextPaintWebpageUrlSpan.class);
                    if (innerSpans2 != null && innerSpans2.length > 0) {
                        textPath = new LinkPath(true);
                        textPath.setAllowReset(false);
                        int a2 = 0;
                        while (a2 < innerSpans2.length) {
                            int start = spanned.getSpanStart(innerSpans2[a2]);
                            int end = spanned.getSpanEnd(innerSpans2[a2]);
                            textPath.setCurrentLayout(result, start, f);
                            int shift = innerSpans2[a2].getTextPaint() != null ? innerSpans2[a2].getTextPaint().baselineShift : 0;
                            textPath.setBaselineShift(shift != 0 ? shift + AndroidUtilities.dp(shift > 0 ? 5.0f : -2.0f) : 0);
                            result.getSelectionPath(start, end, textPath);
                            a2++;
                            f = 0.0f;
                        }
                        textPath.setAllowReset(true);
                    }
                } catch (Exception e4) {
                }
                try {
                    TextPaintMarkSpan[] innerSpans3 = (TextPaintMarkSpan[]) spanned.getSpans(0, spanned.length(), TextPaintMarkSpan.class);
                    if (innerSpans3 != null && innerSpans3.length > 0) {
                        LinkPath markPath2 = new LinkPath(true);
                        try {
                            markPath2.setAllowReset(false);
                            for (int a3 = 0; a3 < innerSpans3.length; a3++) {
                                int start2 = spanned.getSpanStart(innerSpans3[a3]);
                                int end2 = spanned.getSpanEnd(innerSpans3[a3]);
                                markPath2.setCurrentLayout(result, start2, 0.0f);
                                int shift2 = innerSpans3[a3].getTextPaint() != null ? innerSpans3[a3].getTextPaint().baselineShift : 0;
                                markPath2.setBaselineShift(shift2 != 0 ? shift2 + AndroidUtilities.dp(shift2 > 0 ? 5.0f : -2.0f) : 0);
                                result.getSelectionPath(start2, end2, markPath2);
                            }
                            markPath2.setAllowReset(true);
                            markPath = markPath2;
                        } catch (Exception e5) {
                            markPath = markPath2;
                        }
                    }
                } catch (Exception e6) {
                }
            }
            DrawingText drawingText = new DrawingText();
            drawingText.textLayout = result;
            drawingText.textPath = textPath;
            drawingText.markPath = markPath;
            drawingText.parentBlock = parentBlock;
            drawingText.parentText = richText;
            return drawingText;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x0243  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x024e  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x026d  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0275  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean checkLayoutForLinks(org.telegram.ui.ArticleViewer.WebpageAdapter r27, android.view.MotionEvent r28, android.view.View r29, org.telegram.ui.ArticleViewer.DrawingText r30, int r31, int r32) {
        /*
            Method dump skipped, instructions count: 639
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.checkLayoutForLinks(org.telegram.ui.ArticleViewer$WebpageAdapter, android.view.MotionEvent, android.view.View, org.telegram.ui.ArticleViewer$DrawingText, int, int):boolean");
    }

    public void removePressedLink() {
        if (this.pressedLink == null && this.pressedLinkOwnerView == null) {
            return;
        }
        View parentView = this.pressedLinkOwnerView;
        this.links.clear();
        this.pressedLink = null;
        this.pressedLinkOwnerLayout = null;
        this.pressedLinkOwnerView = null;
        if (parentView != null) {
            parentView.invalidate();
        }
    }

    public void openWebpageUrl(String url, final String anchor) {
        if (this.openUrlReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, false);
            this.openUrlReqId = 0;
        }
        final int reqId = this.lastReqId + 1;
        this.lastReqId = reqId;
        showProgressView(true, true);
        final TLRPC.TL_messages_getWebPage req = new TLRPC.TL_messages_getWebPage();
        req.url = url;
        req.hash = 0;
        this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda29
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ArticleViewer.this.m1529lambda$openWebpageUrl$7$orgtelegramuiArticleViewer(reqId, anchor, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$openWebpageUrl$7$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1529lambda$openWebpageUrl$7$orgtelegramuiArticleViewer(final int reqId, final String anchor, final TLRPC.TL_messages_getWebPage req, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1528lambda$openWebpageUrl$6$orgtelegramuiArticleViewer(reqId, response, anchor, req);
            }
        });
    }

    /* renamed from: lambda$openWebpageUrl$6$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1528lambda$openWebpageUrl$6$orgtelegramuiArticleViewer(int reqId, TLObject response, String anchor, TLRPC.TL_messages_getWebPage req) {
        if (this.openUrlReqId == 0 || reqId != this.lastReqId) {
            return;
        }
        this.openUrlReqId = 0;
        showProgressView(true, false);
        if (this.isVisible) {
            if ((response instanceof TLRPC.TL_webPage) && (((TLRPC.TL_webPage) response).cached_page instanceof TLRPC.TL_page)) {
                addPageToStack((TLRPC.TL_webPage) response, anchor, 1);
            } else {
                Browser.openUrl(this.parentActivity, req.url);
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        BlockAudioCell cell;
        MessageObject playing;
        if (id == NotificationCenter.messagePlayingDidStart) {
            MessageObject messageObject = (MessageObject) args[0];
            if (this.listView != null) {
                int i = 0;
                while (true) {
                    RecyclerListView[] recyclerListViewArr = this.listView;
                    if (i < recyclerListViewArr.length) {
                        int count = recyclerListViewArr[i].getChildCount();
                        for (int a = 0; a < count; a++) {
                            View view = this.listView[i].getChildAt(a);
                            if (view instanceof BlockAudioCell) {
                                ((BlockAudioCell) view).updateButtonState(true);
                            }
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (this.listView != null) {
                int i2 = 0;
                while (true) {
                    RecyclerListView[] recyclerListViewArr2 = this.listView;
                    if (i2 < recyclerListViewArr2.length) {
                        int count2 = recyclerListViewArr2[i2].getChildCount();
                        for (int a2 = 0; a2 < count2; a2++) {
                            View view2 = this.listView[i2].getChildAt(a2);
                            if (view2 instanceof BlockAudioCell) {
                                BlockAudioCell cell2 = (BlockAudioCell) view2;
                                MessageObject messageObject2 = cell2.getMessageObject();
                                if (messageObject2 != null) {
                                    cell2.updateButtonState(true);
                                }
                            }
                        }
                        i2++;
                    } else {
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid = (Integer) args[0];
            if (this.listView != null) {
                int i3 = 0;
                while (true) {
                    RecyclerListView[] recyclerListViewArr3 = this.listView;
                    if (i3 < recyclerListViewArr3.length) {
                        int count3 = recyclerListViewArr3[i3].getChildCount();
                        int a3 = 0;
                        while (true) {
                            if (a3 < count3) {
                                View view3 = this.listView[i3].getChildAt(a3);
                                if (!(view3 instanceof BlockAudioCell) || (playing = (cell = (BlockAudioCell) view3).getMessageObject()) == null || playing.getId() != mid.intValue()) {
                                    a3++;
                                } else {
                                    MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                                    if (player != null) {
                                        playing.audioProgress = player.audioProgress;
                                        playing.audioProgressSec = player.audioProgressSec;
                                        playing.audioPlayerDuration = player.audioPlayerDuration;
                                        cell.updatePlayingMessageProgress();
                                    }
                                }
                            }
                        }
                        i3++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void updateThemeColors(float progress) {
        refreshThemeColors();
        updatePaintColors();
        if (this.windowView != null) {
            this.listView[0].invalidateViews();
            this.listView[1].invalidateViews();
            this.windowView.invalidate();
            this.searchPanel.invalidate();
            if (progress == 1.0f) {
                this.adapter[0].notifyDataSetChanged();
                this.adapter[1].notifyDataSetChanged();
            }
        }
    }

    public void updatePaintSize() {
        for (int i = 0; i < 2; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    private void updatePaintFonts() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        Typeface typefaceNormal = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typefaceItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create(C.SERIF_NAME, 2);
        Typeface typefaceBold = this.selectedFont == 0 ? AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM) : Typeface.create(C.SERIF_NAME, 1);
        Typeface typefaceBoldItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create(C.SERIF_NAME, 3);
        for (int a = 0; a < quoteTextPaints.size(); a++) {
            updateFontEntry(quoteTextPaints.keyAt(a), quoteTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a2 = 0; a2 < preformattedTextPaints.size(); a2++) {
            updateFontEntry(preformattedTextPaints.keyAt(a2), preformattedTextPaints.valueAt(a2), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a3 = 0; a3 < paragraphTextPaints.size(); a3++) {
            updateFontEntry(paragraphTextPaints.keyAt(a3), paragraphTextPaints.valueAt(a3), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a4 = 0; a4 < listTextPaints.size(); a4++) {
            updateFontEntry(listTextPaints.keyAt(a4), listTextPaints.valueAt(a4), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a5 = 0; a5 < embedPostTextPaints.size(); a5++) {
            updateFontEntry(embedPostTextPaints.keyAt(a5), embedPostTextPaints.valueAt(a5), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a6 = 0; a6 < mediaCaptionTextPaints.size(); a6++) {
            updateFontEntry(mediaCaptionTextPaints.keyAt(a6), mediaCaptionTextPaints.valueAt(a6), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a7 = 0; a7 < mediaCreditTextPaints.size(); a7++) {
            updateFontEntry(mediaCreditTextPaints.keyAt(a7), mediaCreditTextPaints.valueAt(a7), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a8 = 0; a8 < photoCaptionTextPaints.size(); a8++) {
            updateFontEntry(photoCaptionTextPaints.keyAt(a8), photoCaptionTextPaints.valueAt(a8), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a9 = 0; a9 < photoCreditTextPaints.size(); a9++) {
            updateFontEntry(photoCreditTextPaints.keyAt(a9), photoCreditTextPaints.valueAt(a9), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a10 = 0; a10 < authorTextPaints.size(); a10++) {
            updateFontEntry(authorTextPaints.keyAt(a10), authorTextPaints.valueAt(a10), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a11 = 0; a11 < footerTextPaints.size(); a11++) {
            updateFontEntry(footerTextPaints.keyAt(a11), footerTextPaints.valueAt(a11), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a12 = 0; a12 < embedPostCaptionTextPaints.size(); a12++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(a12), embedPostCaptionTextPaints.valueAt(a12), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a13 = 0; a13 < relatedArticleTextPaints.size(); a13++) {
            updateFontEntry(relatedArticleTextPaints.keyAt(a13), relatedArticleTextPaints.valueAt(a13), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a14 = 0; a14 < detailsTextPaints.size(); a14++) {
            updateFontEntry(detailsTextPaints.keyAt(a14), detailsTextPaints.valueAt(a14), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a15 = 0; a15 < tableTextPaints.size(); a15++) {
            updateFontEntry(tableTextPaints.keyAt(a15), tableTextPaints.valueAt(a15), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
    }

    private void updateFontEntry(int flags, TextPaint paint, Typeface typefaceNormal, Typeface typefaceBoldItalic, Typeface typefaceBold, Typeface typefaceItalic) {
        if ((flags & 1) != 0 && (flags & 2) != 0) {
            paint.setTypeface(typefaceBoldItalic);
        } else if ((flags & 1) != 0) {
            paint.setTypeface(typefaceBold);
        } else if ((flags & 2) != 0) {
            paint.setTypeface(typefaceItalic);
        } else if ((flags & 4) == 0) {
            paint.setTypeface(typefaceNormal);
        }
    }

    private void updatePaintColors() {
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int i = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i >= recyclerListViewArr.length) {
                break;
            }
            recyclerListViewArr[i].setGlowColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            i++;
        }
        TextPaint textPaint = listTextPointerPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        TextPaint textPaint2 = listTextNumPaint;
        if (textPaint2 != null) {
            textPaint2.setColor(getTextColor());
        }
        TextPaint textPaint3 = embedPostAuthorPaint;
        if (textPaint3 != null) {
            textPaint3.setColor(getTextColor());
        }
        TextPaint textPaint4 = channelNamePaint;
        if (textPaint4 != null) {
            textPaint4.setColor(getTextColor());
        }
        TextPaint textPaint5 = channelNamePhotoPaint;
        if (textPaint5 != null) {
            textPaint5.setColor(-1);
        }
        TextPaint textPaint6 = relatedArticleHeaderPaint;
        if (textPaint6 != null) {
            textPaint6.setColor(getTextColor());
        }
        TextPaint textPaint7 = relatedArticleTextPaint;
        if (textPaint7 != null) {
            textPaint7.setColor(getGrayTextColor());
        }
        TextPaint textPaint8 = embedPostDatePaint;
        if (textPaint8 != null) {
            textPaint8.setColor(getGrayTextColor());
        }
        createPaint(true);
        setMapColors(titleTextPaints);
        setMapColors(kickerTextPaints);
        setMapColors(subtitleTextPaints);
        setMapColors(headerTextPaints);
        setMapColors(subheaderTextPaints);
        setMapColors(quoteTextPaints);
        setMapColors(preformattedTextPaints);
        setMapColors(paragraphTextPaints);
        setMapColors(listTextPaints);
        setMapColors(embedPostTextPaints);
        setMapColors(mediaCaptionTextPaints);
        setMapColors(mediaCreditTextPaints);
        setMapColors(photoCaptionTextPaints);
        setMapColors(photoCreditTextPaints);
        setMapColors(authorTextPaints);
        setMapColors(footerTextPaints);
        setMapColors(embedPostCaptionTextPaints);
        setMapColors(relatedArticleTextPaints);
        setMapColors(detailsTextPaints);
        setMapColors(tableTextPaints);
    }

    private void setMapColors(SparseArray<TextPaint> map) {
        for (int a = 0; a < map.size(); a++) {
            int flags = map.keyAt(a);
            TextPaint paint = map.valueAt(a);
            if ((flags & 8) != 0 || (flags & 512) != 0) {
                paint.setColor(getLinkTextColor());
            } else {
                paint.setColor(getTextColor());
            }
        }
    }

    public void setParentActivity(Activity activity, BaseFragment fragment) {
        this.parentFragment = fragment;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        if (this.parentActivity == activity) {
            updatePaintColors();
            refreshThemeColors();
            return;
        }
        this.parentActivity = activity;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
        this.selectedFont = sharedPreferences.getInt("font_type", 0);
        createPaint(false);
        this.backgroundPaint = new Paint();
        this.layerShadowDrawable = activity.getResources().getDrawable(R.drawable.layer_shadow);
        this.slideDotDrawable = activity.getResources().getDrawable(R.drawable.slide_dot_small);
        this.slideDotBigDrawable = activity.getResources().getDrawable(R.drawable.slide_dot_big);
        this.scrimPaint = new Paint();
        WindowView windowView = new WindowView(activity);
        this.windowView = windowView;
        windowView.setWillNotDraw(false);
        this.windowView.setClipChildren(true);
        this.windowView.setFocusable(false);
        FrameLayout frameLayout = new FrameLayout(activity) { // from class: org.telegram.ui.ArticleViewer.7
            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                int clipRight;
                int clipLeft;
                float opacity;
                if (ArticleViewer.this.windowView.movingPage) {
                    int width = getMeasuredWidth();
                    int translationX = (int) ArticleViewer.this.listView[0].getTranslationX();
                    if (child != ArticleViewer.this.listView[1]) {
                        if (child != ArticleViewer.this.listView[0]) {
                            clipLeft = 0;
                            clipRight = width;
                        } else {
                            clipLeft = translationX;
                            clipRight = width;
                        }
                    } else {
                        clipLeft = 0;
                        clipRight = translationX;
                    }
                    int restoreCount = canvas.save();
                    canvas.clipRect(clipLeft, 0, clipRight, getHeight());
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    canvas.restoreToCount(restoreCount);
                    if (translationX != 0) {
                        if (child != ArticleViewer.this.listView[0]) {
                            if (child == ArticleViewer.this.listView[1]) {
                                float opacity2 = Math.min(0.8f, (width - translationX) / width);
                                if (opacity2 >= 0.0f) {
                                    opacity = opacity2;
                                } else {
                                    opacity = 0.0f;
                                }
                                ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                                canvas.drawRect(clipLeft, 0.0f, clipRight, getHeight(), ArticleViewer.this.scrimPaint);
                            }
                        } else {
                            float alpha = Math.max(0.0f, Math.min((width - translationX) / AndroidUtilities.dp(20.0f), 1.0f));
                            ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                            ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                            ArticleViewer.this.layerShadowDrawable.draw(canvas);
                        }
                    }
                    return result;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.containerView = frameLayout;
        this.windowView.addView(frameLayout, LayoutHelper.createFrame(-1, -1, 51));
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowView.setFitsSystemWindows(true);
            this.containerView.setOnApplyWindowInsetsListener(ArticleViewer$$ExternalSyntheticLambda39.INSTANCE);
        }
        FrameLayout frameLayout2 = new FrameLayout(activity);
        this.fullscreenVideoContainer = frameLayout2;
        frameLayout2.setBackgroundColor(-16777216);
        this.fullscreenVideoContainer.setVisibility(4);
        this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity);
        this.fullscreenAspectRatioView = aspectRatioFrameLayout;
        aspectRatioFrameLayout.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(activity);
        this.listView = new RecyclerListView[2];
        this.adapter = new WebpageAdapter[2];
        this.layoutManager = new LinearLayoutManager[2];
        int i2 = 0;
        while (i2 < this.listView.length) {
            WebpageAdapter[] webpageAdapterArr = this.adapter;
            final WebpageAdapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            webpageAdapterArr[i2] = webpageAdapter;
            this.listView[i2] = new RecyclerListView(activity) { // from class: org.telegram.ui.ArticleViewer.8
                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    int count = getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if (child.getTag() instanceof Integer) {
                            Integer tag = (Integer) child.getTag();
                            if (tag.intValue() == 90) {
                                int bottom = child.getBottom();
                                if (bottom < getMeasuredHeight()) {
                                    int height = getMeasuredHeight();
                                    child.layout(0, height - child.getMeasuredHeight(), child.getMeasuredWidth(), height);
                                    return;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout == null || ArticleViewer.this.pressedLink != null || ((ArticleViewer.this.popupWindow != null && ArticleViewer.this.popupWindow.isShowing()) || (e.getAction() != 1 && e.getAction() != 3))) {
                        if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink != null && e.getAction() == 1) {
                            ArticleViewer articleViewer = ArticleViewer.this;
                            articleViewer.checkLayoutForLinks(webpageAdapter, e, articleViewer.pressedLinkOwnerView, ArticleViewer.this.pressedLinkOwnerLayout, 0, 0);
                        }
                    } else {
                        ArticleViewer.this.pressedLink = null;
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onInterceptTouchEvent(e);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
                public boolean onTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        ArticleViewer.this.pressedLink = null;
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onTouchEvent(e);
                }

                @Override // android.view.View
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (ArticleViewer.this.windowView.movingPage) {
                        ArticleViewer.this.containerView.invalidate();
                        float progress = translationX / getMeasuredWidth();
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.setCurrentHeaderHeight((int) (articleViewer.windowView.startMovingHeaderHeight + ((AndroidUtilities.dp(56.0f) - ArticleViewer.this.windowView.startMovingHeaderHeight) * progress)));
                    }
                }
            };
            ((DefaultItemAnimator) this.listView[i2].getItemAnimator()).setDelayAnimations(false);
            RecyclerListView recyclerListView = this.listView[i2];
            LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            linearLayoutManagerArr[i2] = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.listView[i2].setAdapter(webpageAdapter);
            this.listView[i2].setClipToPadding(false);
            this.listView[i2].setVisibility(i2 == 0 ? 0 : 8);
            this.listView[i2].setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView[i2].setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.containerView.addView(this.listView[i2], LayoutHelper.createFrame(-1, -1.0f));
            this.listView[i2].setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda38
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public final boolean onItemClick(View view, int i3) {
                    return ArticleViewer.this.m1547lambda$setParentActivity$9$orgtelegramuiArticleViewer(view, i3);
                }
            });
            this.listView[i2].setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda37
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
                public /* synthetic */ boolean hasDoubleTap(View view, int i3) {
                    return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i3);
                }

                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
                public /* synthetic */ void onDoubleTap(View view, int i3, float f, float f2) {
                    RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i3, f, f2);
                }

                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
                public final void onItemClick(View view, int i3, float f, float f2) {
                    ArticleViewer.this.m1535lambda$setParentActivity$12$orgtelegramuiArticleViewer(webpageAdapter, view, i3, f, f2);
                }
            });
            this.listView[i2].setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ArticleViewer.9
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 0) {
                        ArticleViewer.this.textSelectionHelper.stopScrolling();
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getChildCount() == 0) {
                        return;
                    }
                    ArticleViewer.this.textSelectionHelper.onParentScrolled();
                    ArticleViewer.this.headerView.invalidate();
                    ArticleViewer.this.checkScroll(dy);
                }
            });
            i2++;
        }
        this.headerPaint.setColor(-16777216);
        this.statusBarPaint.setColor(-16777216);
        this.headerProgressPaint.setColor(-14408666);
        this.navigationBarPaint.setColor(-16777216);
        FrameLayout frameLayout3 = new FrameLayout(activity) { // from class: org.telegram.ui.ArticleViewer.10
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                float viewProgress;
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                canvas.drawRect(0.0f, 0.0f, width, height, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    int first = ArticleViewer.this.layoutManager[0].findFirstVisibleItemPosition();
                    int last = ArticleViewer.this.layoutManager[0].findLastVisibleItemPosition();
                    int count = ArticleViewer.this.layoutManager[0].getItemCount();
                    View view = last >= count + (-2) ? ArticleViewer.this.layoutManager[0].findViewByPosition(count - 2) : ArticleViewer.this.layoutManager[0].findViewByPosition(first);
                    if (view == null) {
                        return;
                    }
                    float itemProgress = width / (count - 1);
                    ArticleViewer.this.layoutManager[0].getChildCount();
                    float viewHeight = view.getMeasuredHeight();
                    if (last >= count - 2) {
                        viewProgress = ((((count - 2) - first) * itemProgress) * (ArticleViewer.this.listView[0].getMeasuredHeight() - view.getTop())) / viewHeight;
                    } else {
                        viewProgress = (1.0f - ((Math.min(0, view.getTop() - ArticleViewer.this.listView[0].getPaddingTop()) + viewHeight) / viewHeight)) * itemProgress;
                    }
                    float progress = (first * itemProgress) + viewProgress;
                    canvas.drawRect(0.0f, 0.0f, progress, height, ArticleViewer.this.headerProgressPaint);
                }
            }
        };
        this.headerView = frameLayout3;
        frameLayout3.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        this.headerView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda40
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ArticleViewer.this.m1536lambda$setParentActivity$13$orgtelegramuiArticleViewer(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(activity);
        this.titleTextView = simpleTextView;
        simpleTextView.setGravity(19);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleTextView.setTextColor(-5000269);
        this.titleTextView.setPivotX(0.0f);
        this.titleTextView.setPivotY(AndroidUtilities.dp(28.0f));
        this.headerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 56.0f, 51, 72.0f, 0.0f, 96.0f, 0.0f));
        LineProgressView lineProgressView = new LineProgressView(activity);
        this.lineProgressView = lineProgressView;
        lineProgressView.setProgressColor(-1);
        this.lineProgressView.setPivotX(0.0f);
        this.lineProgressView.setPivotY(AndroidUtilities.dp(2.0f));
        this.headerView.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 1.0f));
        this.lineProgressTickRunnable = new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1537lambda$setParentActivity$14$orgtelegramuiArticleViewer();
            }
        };
        FrameLayout frameLayout4 = new FrameLayout(activity);
        this.menuContainer = frameLayout4;
        this.headerView.addView(frameLayout4, LayoutHelper.createFrame(48, 56, 53));
        View view = new View(activity);
        this.searchShadow = view;
        view.setBackgroundResource(R.drawable.header_shadow);
        this.searchShadow.setAlpha(0.0f);
        this.containerView.addView(this.searchShadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 56.0f, 0.0f, 0.0f));
        FrameLayout frameLayout5 = new FrameLayout(this.parentActivity);
        this.searchContainer = frameLayout5;
        frameLayout5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.searchContainer.setVisibility(4);
        if (Build.VERSION.SDK_INT < 21) {
            this.searchContainer.setAlpha(0.0f);
        }
        this.headerView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 56.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(this.parentActivity) { // from class: org.telegram.ui.ArticleViewer.11
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
            }
        };
        this.searchField = editTextBoldCursor;
        editTextBoldCursor.setCursorWidth(1.5f);
        this.searchField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.searchField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.searchField.setTextSize(1, 18.0f);
        this.searchField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.searchField.setSingleLine(true);
        this.searchField.setHint(LocaleController.getString("Search", R.string.Search));
        this.searchField.setBackgroundResource(0);
        this.searchField.setPadding(0, 0, 0, 0);
        int inputType = 524288 | this.searchField.getInputType();
        this.searchField.setInputType(inputType);
        if (Build.VERSION.SDK_INT < 23) {
            this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.ArticleViewer.12
                @Override // android.view.ActionMode.Callback
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override // android.view.ActionMode.Callback
                public void onDestroyActionMode(ActionMode mode) {
                }

                @Override // android.view.ActionMode.Callback
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override // android.view.ActionMode.Callback
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
        }
        this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda7
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                return ArticleViewer.this.m1538lambda$setParentActivity$15$orgtelegramuiArticleViewer(textView, i3, keyEvent);
            }
        });
        this.searchField.addTextChangedListener(new AnonymousClass13());
        this.searchField.setImeOptions(33554435);
        this.searchField.setTextIsSelectable(false);
        this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 72.0f, 0.0f, 48.0f, 0.0f));
        ImageView imageView = new ImageView(this.parentActivity) { // from class: org.telegram.ui.ArticleViewer.14
            @Override // android.widget.ImageView, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                clearAnimation();
                if (getTag() == null) {
                    ArticleViewer.this.clearButton.setVisibility(4);
                    ArticleViewer.this.clearButton.setAlpha(0.0f);
                    ArticleViewer.this.clearButton.setRotation(45.0f);
                    ArticleViewer.this.clearButton.setScaleX(0.0f);
                    ArticleViewer.this.clearButton.setScaleY(0.0f);
                    return;
                }
                ArticleViewer.this.clearButton.setAlpha(1.0f);
                ArticleViewer.this.clearButton.setRotation(0.0f);
                ArticleViewer.this.clearButton.setScaleX(1.0f);
                ArticleViewer.this.clearButton.setScaleY(1.0f);
            }
        };
        this.clearButton = imageView;
        imageView.setImageDrawable(new CloseProgressDrawable2() { // from class: org.telegram.ui.ArticleViewer.15
            @Override // org.telegram.ui.Components.CloseProgressDrawable2
            protected int getCurrentColor() {
                return Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
            }
        });
        this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
        this.clearButton.setAlpha(0.0f);
        this.clearButton.setRotation(45.0f);
        this.clearButton.setScaleX(0.0f);
        this.clearButton.setScaleY(0.0f);
        this.clearButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda41
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ArticleViewer.this.m1539lambda$setParentActivity$16$orgtelegramuiArticleViewer(view2);
            }
        });
        this.clearButton.setContentDescription(LocaleController.getString("ClearButton", R.string.ClearButton));
        this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        ImageView imageView2 = new ImageView(activity);
        this.backButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        BackDrawable backDrawable = new BackDrawable(false);
        this.backDrawable = backDrawable;
        backDrawable.setAnimationTime(200.0f);
        this.backDrawable.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.backDrawable.setRotatedColor(-5000269);
        this.backDrawable.setRotation(1.0f, false);
        this.backButton.setImageDrawable(this.backDrawable);
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda42
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ArticleViewer.this.m1540lambda$setParentActivity$17$orgtelegramuiArticleViewer(view2);
            }
        });
        this.backButton.setContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(this.parentActivity, null, Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, -5000269) { // from class: org.telegram.ui.ArticleViewer.16
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem
            public void toggleSubMenu() {
                super.toggleSubMenu();
                ArticleViewer.this.listView[0].stopScroll();
                ArticleViewer.this.checkScrollAnimated();
            }
        };
        this.menuButton = actionBarMenuItem;
        actionBarMenuItem.setLayoutInScreen(true);
        this.menuButton.setDuplicateParentStateEnabled(false);
        this.menuButton.setClickable(true);
        this.menuButton.setIcon(R.drawable.ic_ab_other);
        this.menuButton.addSubItem(1, R.drawable.msg_search, LocaleController.getString("Search", R.string.Search));
        this.menuButton.addSubItem(2, R.drawable.msg_share, LocaleController.getString("ShareFile", R.string.ShareFile));
        this.menuButton.addSubItem(3, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        this.menuButton.addSubItem(4, R.drawable.msg_settings_old, LocaleController.getString("Settings", R.string.Settings));
        this.menuButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.menuButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.menuContainer.addView(this.menuButton, LayoutHelper.createFrame(48, 56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(activity, 2);
        this.progressView = contextProgressView;
        contextProgressView.setVisibility(8);
        this.menuContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        this.menuButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda43
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ArticleViewer.this.m1541lambda$setParentActivity$18$orgtelegramuiArticleViewer(view2);
            }
        });
        this.menuButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda34
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i3) {
                ArticleViewer.this.m1543lambda$setParentActivity$20$orgtelegramuiArticleViewer(i3);
            }
        });
        FrameLayout frameLayout6 = new FrameLayout(this.parentActivity) { // from class: org.telegram.ui.ArticleViewer.17
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchPanel = frameLayout6;
        frameLayout6.setOnTouchListener(ArticleViewer$$ExternalSyntheticLambda5.INSTANCE);
        this.searchPanel.setWillNotDraw(false);
        this.searchPanel.setVisibility(4);
        this.searchPanel.setFocusable(true);
        this.searchPanel.setFocusableInTouchMode(true);
        this.searchPanel.setClickable(true);
        this.searchPanel.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.containerView.addView(this.searchPanel, LayoutHelper.createFrame(-1, 51, 80));
        ImageView imageView3 = new ImageView(this.parentActivity);
        this.searchUpButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.searchUpButton.setImageResource(R.drawable.msg_go_up);
        this.searchUpButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
        this.searchUpButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), 1));
        this.searchPanel.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 48.0f, 0.0f));
        this.searchUpButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ArticleViewer.this.m1544lambda$setParentActivity$22$orgtelegramuiArticleViewer(view2);
            }
        });
        this.searchUpButton.setContentDescription(LocaleController.getString("AccDescrSearchNext", R.string.AccDescrSearchNext));
        ImageView imageView4 = new ImageView(this.parentActivity);
        this.searchDownButton = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.searchDownButton.setImageResource(R.drawable.msg_go_down);
        this.searchDownButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
        this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), 1));
        this.searchPanel.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ArticleViewer.this.m1545lambda$setParentActivity$23$orgtelegramuiArticleViewer(view2);
            }
        });
        this.searchDownButton.setContentDescription(LocaleController.getString("AccDescrSearchPrev", R.string.AccDescrSearchPrev));
        SimpleTextView simpleTextView2 = new SimpleTextView(this.parentActivity);
        this.searchCountText = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.searchCountText.setGravity(3);
        this.searchPanel.addView(this.searchCountText, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, 0.0f, 108.0f, 0.0f));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.windowLayoutParams = layoutParams;
        layoutParams.height = -1;
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.width = -1;
        this.windowLayoutParams.gravity = 51;
        this.windowLayoutParams.type = 98;
        this.windowLayoutParams.softInputMode = 48;
        this.windowLayoutParams.flags = 131072;
        int uiFlags = 1792;
        int navigationColor = Theme.getColor(Theme.key_windowBackgroundGray, null, true);
        float navigationBrightness = AndroidUtilities.computePerceivedBrightness(navigationColor);
        boolean isLightNavigation = navigationBrightness >= 0.721f;
        if (isLightNavigation && Build.VERSION.SDK_INT >= 26) {
            uiFlags = 1792 | 16;
            this.navigationBarPaint.setColor(navigationColor);
        } else if (!isLightNavigation) {
            this.navigationBarPaint.setColor(navigationColor);
        }
        this.windowLayoutParams.systemUiVisibility = uiFlags;
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags |= -2147417856;
            if (Build.VERSION.SDK_INT >= 28) {
                this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
            }
        }
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
        this.textSelectionHelper = articleTextSelectionHelper;
        articleTextSelectionHelper.setParentView(this.listView[0]);
        if (MessagesController.getGlobalMainSettings().getBoolean("translate_button", false)) {
            this.textSelectionHelper.setOnTranslate(new TextSelectionHelper.OnTranslateListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda36
                @Override // org.telegram.ui.Cells.TextSelectionHelper.OnTranslateListener
                public final void run(CharSequence charSequence, String str, String str2, Runnable runnable) {
                    ArticleViewer.this.m1546lambda$setParentActivity$24$orgtelegramuiArticleViewer(charSequence, str, str2, runnable);
                }
            });
        }
        this.textSelectionHelper.layoutManager = this.layoutManager[0];
        this.textSelectionHelper.setCallback(new TextSelectionHelper.Callback() { // from class: org.telegram.ui.ArticleViewer.18
            @Override // org.telegram.ui.Cells.TextSelectionHelper.Callback
            public void onStateChanged(boolean isSelected) {
                if (isSelected) {
                    ArticleViewer.this.showSearch(false);
                }
            }

            @Override // org.telegram.ui.Cells.TextSelectionHelper.Callback
            public void onTextCopied() {
                if (Build.VERSION.SDK_INT < 31) {
                    BulletinFactory.of(ArticleViewer.this.containerView, null).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
                }
            }
        });
        this.containerView.addView(this.textSelectionHelper.getOverlayView(activity));
        PinchToZoomHelper pinchToZoomHelper = new PinchToZoomHelper(this.containerView, this.windowView);
        this.pinchToZoomHelper = pinchToZoomHelper;
        pinchToZoomHelper.setClipBoundsListener(new PinchToZoomHelper.ClipBoundsListener() { // from class: org.telegram.ui.ArticleViewer.19
            @Override // org.telegram.ui.PinchToZoomHelper.ClipBoundsListener
            public void getClipTopBottom(float[] topBottom) {
                topBottom[0] = ArticleViewer.this.currentHeaderHeight;
                topBottom[1] = ArticleViewer.this.listView[0].getMeasuredHeight();
            }
        });
        this.pinchToZoomHelper.setCallback(new PinchToZoomHelper.Callback() { // from class: org.telegram.ui.ArticleViewer.20
            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ TextureView getCurrentTextureView() {
                return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ void onZoomFinished(MessageObject messageObject) {
                PinchToZoomHelper.Callback.CC.$default$onZoomFinished(this, messageObject);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public void onZoomStarted(MessageObject messageObject) {
                if (ArticleViewer.this.listView[0] != null) {
                    ArticleViewer.this.listView[0].cancelClickRunnables(true);
                }
            }
        });
        updatePaintColors();
    }

    public static /* synthetic */ WindowInsets lambda$setParentActivity$8(View v, WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    /* renamed from: lambda$setParentActivity$9$org-telegram-ui-ArticleViewer */
    public /* synthetic */ boolean m1547lambda$setParentActivity$9$orgtelegramuiArticleViewer(View view, int position) {
        if (view instanceof BlockRelatedArticlesCell) {
            BlockRelatedArticlesCell cell = (BlockRelatedArticlesCell) view;
            showCopyPopup(cell.currentBlock.parent.articles.get(cell.currentBlock.num).url);
            return true;
        }
        return false;
    }

    /* renamed from: lambda$setParentActivity$12$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1535lambda$setParentActivity$12$orgtelegramuiArticleViewer(WebpageAdapter webpageAdapter, View view, int position, float x, float y) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelper;
        if (articleTextSelectionHelper != null) {
            if (articleTextSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
                return;
            }
            this.textSelectionHelper.clear();
        }
        if ((view instanceof ReportCell) && webpageAdapter.currentPage != null) {
            ReportCell cell = (ReportCell) view;
            if (this.previewsReqId != 0) {
                return;
            }
            if (cell.hasViews && x < view.getMeasuredWidth() / 2) {
                return;
            }
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat("previews");
            if (!(object instanceof TLRPC.TL_user)) {
                final int currentAccount = UserConfig.selectedAccount;
                final long pageId = webpageAdapter.currentPage.id;
                showProgressView(true, true);
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = "previews";
                this.previewsReqId = ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda28
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ArticleViewer.this.m1534lambda$setParentActivity$11$orgtelegramuiArticleViewer(currentAccount, pageId, tLObject, tL_error);
                    }
                });
                return;
            }
            openPreviewsChat((TLRPC.User) object, webpageAdapter.currentPage.id);
        } else if (position < 0 || position >= webpageAdapter.localBlocks.size()) {
        } else {
            TLRPC.PageBlock pageBlock = (TLRPC.PageBlock) webpageAdapter.localBlocks.get(position);
            TLRPC.PageBlock pageBlock2 = getLastNonListPageBlock(pageBlock);
            if (pageBlock2 instanceof TL_pageBlockDetailsChild) {
                TL_pageBlockDetailsChild detailsChild = (TL_pageBlockDetailsChild) pageBlock2;
                pageBlock2 = detailsChild.block;
            }
            if (pageBlock2 instanceof TLRPC.TL_pageBlockChannel) {
                TLRPC.TL_pageBlockChannel pageBlockChannel = (TLRPC.TL_pageBlockChannel) pageBlock2;
                MessagesController.getInstance(this.currentAccount).openByUserName(pageBlockChannel.channel.username, this.parentFragment, 2);
                close(false, true);
            } else if (pageBlock2 instanceof TL_pageBlockRelatedArticlesChild) {
                TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) pageBlock2;
                openWebpageUrl(pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num).url, null);
            } else if (pageBlock2 instanceof TLRPC.TL_pageBlockDetails) {
                View view2 = getLastNonListCell(view);
                if (!(view2 instanceof BlockDetailsCell)) {
                    return;
                }
                this.pressedLinkOwnerLayout = null;
                this.pressedLinkOwnerView = null;
                int index = webpageAdapter.blocks.indexOf(pageBlock);
                if (index < 0) {
                    return;
                }
                TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) pageBlock2;
                pageBlockDetails.open = true ^ pageBlockDetails.open;
                int oldCount = webpageAdapter.getItemCount();
                webpageAdapter.updateRows();
                int newCount = webpageAdapter.getItemCount();
                int changeCount = Math.abs(newCount - oldCount);
                BlockDetailsCell cell2 = (BlockDetailsCell) view2;
                cell2.arrow.setAnimationProgressAnimated(pageBlockDetails.open ? 0.0f : 1.0f);
                cell2.invalidate();
                if (changeCount != 0) {
                    if (pageBlockDetails.open) {
                        webpageAdapter.notifyItemRangeInserted(position + 1, changeCount);
                    } else {
                        webpageAdapter.notifyItemRangeRemoved(position + 1, changeCount);
                    }
                }
            }
        }
    }

    /* renamed from: lambda$setParentActivity$11$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1534lambda$setParentActivity$11$orgtelegramuiArticleViewer(final int currentAccount, final long pageId, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1533lambda$setParentActivity$10$orgtelegramuiArticleViewer(response, currentAccount, pageId);
            }
        });
    }

    /* renamed from: lambda$setParentActivity$10$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1533lambda$setParentActivity$10$orgtelegramuiArticleViewer(TLObject response, int currentAccount, long pageId) {
        if (this.previewsReqId == 0) {
            return;
        }
        this.previewsReqId = 0;
        showProgressView(true, false);
        if (response != null) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(currentAccount).putUsers(res.users, false);
            MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, false, true);
            if (!res.users.isEmpty()) {
                openPreviewsChat(res.users.get(0), pageId);
            }
        }
    }

    /* renamed from: lambda$setParentActivity$13$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1536lambda$setParentActivity$13$orgtelegramuiArticleViewer(View v) {
        this.listView[0].smoothScrollToPosition(0);
    }

    /* renamed from: lambda$setParentActivity$14$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1537lambda$setParentActivity$14$orgtelegramuiArticleViewer() {
        float tick;
        float progressLeft = 0.7f - this.lineProgressView.getCurrentProgress();
        if (progressLeft > 0.0f) {
            if (progressLeft < 0.25f) {
                tick = 0.01f;
            } else {
                tick = 0.02f;
            }
            LineProgressView lineProgressView = this.lineProgressView;
            lineProgressView.setProgress(lineProgressView.getCurrentProgress() + tick, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100L);
        }
    }

    /* renamed from: lambda$setParentActivity$15$org-telegram-ui-ArticleViewer */
    public /* synthetic */ boolean m1538lambda$setParentActivity$15$orgtelegramuiArticleViewer(TextView v, int actionId, KeyEvent event) {
        if (event != null) {
            if ((event.getAction() == 1 && event.getKeyCode() == 84) || (event.getAction() == 0 && event.getKeyCode() == 66)) {
                AndroidUtilities.hideKeyboard(this.searchField);
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: org.telegram.ui.ArticleViewer$13 */
    /* loaded from: classes4.dex */
    public class AnonymousClass13 implements TextWatcher {
        AnonymousClass13() {
            ArticleViewer.this = this$0;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (ArticleViewer.this.ignoreOnTextChange) {
                ArticleViewer.this.ignoreOnTextChange = false;
                return;
            }
            ArticleViewer.this.processSearch(s.toString().toLowerCase());
            if (ArticleViewer.this.clearButton != null) {
                if (TextUtils.isEmpty(s)) {
                    if (ArticleViewer.this.clearButton.getTag() != null) {
                        ArticleViewer.this.clearButton.setTag(null);
                        ArticleViewer.this.clearButton.clearAnimation();
                        if (ArticleViewer.this.animateClear) {
                            ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180L).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new Runnable() { // from class: org.telegram.ui.ArticleViewer$13$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ArticleViewer.AnonymousClass13.this.m1557lambda$onTextChanged$0$orgtelegramuiArticleViewer$13();
                                }
                            }).start();
                            return;
                        }
                        ArticleViewer.this.clearButton.setAlpha(0.0f);
                        ArticleViewer.this.clearButton.setRotation(45.0f);
                        ArticleViewer.this.clearButton.setScaleX(0.0f);
                        ArticleViewer.this.clearButton.setScaleY(0.0f);
                        ArticleViewer.this.clearButton.setVisibility(4);
                        ArticleViewer.this.animateClear = true;
                    }
                } else if (ArticleViewer.this.clearButton.getTag() == null) {
                    ArticleViewer.this.clearButton.setTag(1);
                    ArticleViewer.this.clearButton.clearAnimation();
                    ArticleViewer.this.clearButton.setVisibility(0);
                    if (ArticleViewer.this.animateClear) {
                        ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180L).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                        return;
                    }
                    ArticleViewer.this.clearButton.setAlpha(1.0f);
                    ArticleViewer.this.clearButton.setRotation(0.0f);
                    ArticleViewer.this.clearButton.setScaleX(1.0f);
                    ArticleViewer.this.clearButton.setScaleY(1.0f);
                    ArticleViewer.this.animateClear = true;
                }
            }
        }

        /* renamed from: lambda$onTextChanged$0$org-telegram-ui-ArticleViewer$13 */
        public /* synthetic */ void m1557lambda$onTextChanged$0$orgtelegramuiArticleViewer$13() {
            ArticleViewer.this.clearButton.setVisibility(4);
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }
    }

    /* renamed from: lambda$setParentActivity$16$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1539lambda$setParentActivity$16$orgtelegramuiArticleViewer(View v) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    /* renamed from: lambda$setParentActivity$17$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1540lambda$setParentActivity$17$orgtelegramuiArticleViewer(View v) {
        if (this.searchContainer.getTag() != null) {
            showSearch(false);
        } else {
            close(true, true);
        }
    }

    /* renamed from: lambda$setParentActivity$18$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1541lambda$setParentActivity$18$orgtelegramuiArticleViewer(View v) {
        this.menuButton.toggleSubMenu();
    }

    /* renamed from: lambda$setParentActivity$20$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1543lambda$setParentActivity$20$orgtelegramuiArticleViewer(int id) {
        if (this.adapter[0].currentPage == null || this.parentActivity == null) {
            return;
        }
        if (id == 1) {
            showSearch(true);
        } else if (id == 2) {
            showDialog(new ShareAlert(this.parentActivity, null, this.adapter[0].currentPage.url, false, this.adapter[0].currentPage.url, false));
        } else if (id != 3) {
            if (id == 4) {
                BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
                builder.setApplyTopPadding(false);
                LinearLayout settingsContainer = new LinearLayout(this.parentActivity);
                settingsContainer.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                settingsContainer.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(this.parentActivity);
                headerCell.setText(LocaleController.getString("FontSize", R.string.FontSize));
                settingsContainer.addView(headerCell, LayoutHelper.createLinear(-2, -2, 51, 3, 1, 3, 0));
                TextSizeCell sizeCell = new TextSizeCell(this.parentActivity);
                settingsContainer.addView(sizeCell, LayoutHelper.createLinear(-1, -2, 51, 3, 0, 3, 0));
                HeaderCell headerCell2 = new HeaderCell(this.parentActivity);
                headerCell2.setText(LocaleController.getString("FontType", R.string.FontType));
                settingsContainer.addView(headerCell2, LayoutHelper.createLinear(-2, -2, 51, 3, 4, 3, 2));
                int a = 0;
                while (a < 2) {
                    this.fontCells[a] = new FontCell(this.parentActivity);
                    switch (a) {
                        case 0:
                            this.fontCells[a].setTextAndTypeface(LocaleController.getString("Default", R.string.Default), Typeface.DEFAULT);
                            break;
                        case 1:
                            this.fontCells[a].setTextAndTypeface("Serif", Typeface.SERIF);
                            break;
                    }
                    this.fontCells[a].select(a == this.selectedFont, false);
                    this.fontCells[a].setTag(Integer.valueOf(a));
                    this.fontCells[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda44
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ArticleViewer.this.m1542lambda$setParentActivity$19$orgtelegramuiArticleViewer(view);
                        }
                    });
                    settingsContainer.addView(this.fontCells[a], LayoutHelper.createLinear(-1, 50));
                    a++;
                }
                builder.setCustomView(settingsContainer);
                BottomSheet create = builder.create();
                this.linkSheet = create;
                showDialog(create);
            }
        } else {
            String webPageUrl = !TextUtils.isEmpty(this.adapter[0].currentPage.cached_page.url) ? this.adapter[0].currentPage.cached_page.url : this.adapter[0].currentPage.url;
            Browser.openUrl((Context) this.parentActivity, webPageUrl, true, false);
        }
    }

    /* renamed from: lambda$setParentActivity$19$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1542lambda$setParentActivity$19$orgtelegramuiArticleViewer(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.selectedFont = num;
        int a1 = 0;
        while (a1 < 2) {
            this.fontCells[a1].select(a1 == num, true);
            a1++;
        }
        updatePaintFonts();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    public static /* synthetic */ boolean lambda$setParentActivity$21(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$setParentActivity$22$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1544lambda$setParentActivity$22$orgtelegramuiArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex - 1);
    }

    /* renamed from: lambda$setParentActivity$23$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1545lambda$setParentActivity$23$orgtelegramuiArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex + 1);
    }

    /* renamed from: lambda$setParentActivity$24$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1546lambda$setParentActivity$24$orgtelegramuiArticleViewer(CharSequence text, String fromLang, String toLang, Runnable onAlertDismiss) {
        TranslateAlert.showAlert(this.parentActivity, this.parentFragment, fromLang, toLang, text, false, null, onAlertDismiss);
    }

    public void showSearch(final boolean show) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            if ((frameLayout.getTag() != null) == show) {
                return;
            }
            this.searchContainer.setTag(show ? 1 : null);
            this.searchResults.clear();
            this.searchText = null;
            this.adapter[0].searchTextOffset.clear();
            this.currentSearchIndex = 0;
            float f = 1.0f;
            if (this.attachedToWindow) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(250L);
                if (show) {
                    this.searchContainer.setVisibility(0);
                    this.backDrawable.setRotation(0.0f, true);
                } else {
                    this.menuButton.setVisibility(0);
                    this.listView[0].invalidateViews();
                    AndroidUtilities.hideKeyboard(this.searchField);
                    updateWindowLayoutParamsForSearch();
                }
                ArrayList<Animator> animators = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= 21) {
                    if (show) {
                        this.searchContainer.setAlpha(1.0f);
                    }
                    int x = this.menuContainer.getLeft() + (this.menuContainer.getMeasuredWidth() / 2);
                    int y = this.menuContainer.getTop() + (this.menuContainer.getMeasuredHeight() / 2);
                    float rad = (float) Math.sqrt((x * x) + (y * y));
                    Animator animator = ViewAnimationUtils.createCircularReveal(this.searchContainer, x, y, show ? 0.0f : rad, show ? rad : 0.0f);
                    animators.add(animator);
                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.21
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (!show) {
                                ArticleViewer.this.searchContainer.setAlpha(0.0f);
                            }
                        }
                    });
                } else {
                    FrameLayout frameLayout2 = this.searchContainer;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = show ? 1.0f : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(frameLayout2, property, fArr));
                }
                if (!show) {
                    animators.add(ObjectAnimator.ofFloat(this.searchPanel, View.ALPHA, 0.0f));
                }
                View view = this.searchShadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animators.add(ObjectAnimator.ofFloat(view, property2, fArr2));
                animatorSet.playTogether(animators);
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.22
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (show) {
                            ArticleViewer.this.updateWindowLayoutParamsForSearch();
                            ArticleViewer.this.searchField.requestFocus();
                            AndroidUtilities.showKeyboard(ArticleViewer.this.searchField);
                            ArticleViewer.this.menuButton.setVisibility(4);
                            return;
                        }
                        ArticleViewer.this.searchContainer.setVisibility(4);
                        ArticleViewer.this.searchPanel.setVisibility(4);
                        ArticleViewer.this.searchField.setText("");
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        if (!show) {
                            ArticleViewer.this.backDrawable.setRotation(1.0f, true);
                        }
                    }
                });
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                if (!show && !AndroidUtilities.usingHardwareInput && this.keyboardVisible) {
                    this.runAfterKeyboardClose = animatorSet;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda16
                        @Override // java.lang.Runnable
                        public final void run() {
                            ArticleViewer.this.m1555lambda$showSearch$25$orgtelegramuiArticleViewer();
                        }
                    }, 300L);
                    return;
                }
                animatorSet.start();
                return;
            }
            this.searchContainer.setAlpha(show ? 1.0f : 0.0f);
            this.menuButton.setVisibility(show ? 4 : 0);
            this.backDrawable.setRotation(show ? 0.0f : 1.0f, false);
            View view2 = this.searchShadow;
            if (!show) {
                f = 0.0f;
            }
            view2.setAlpha(f);
            if (show) {
                this.searchContainer.setVisibility(0);
            } else {
                this.searchContainer.setVisibility(4);
                this.searchPanel.setVisibility(4);
                this.searchField.setText("");
            }
            updateWindowLayoutParamsForSearch();
        }
    }

    /* renamed from: lambda$showSearch$25$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1555lambda$showSearch$25$orgtelegramuiArticleViewer() {
        AnimatorSet animatorSet = this.runAfterKeyboardClose;
        if (animatorSet != null) {
            animatorSet.start();
            this.runAfterKeyboardClose = null;
        }
    }

    public void updateWindowLayoutParamsForSearch() {
    }

    private void updateSearchButtons() {
        ArrayList<SearchResult> arrayList = this.searchResults;
        if (arrayList == null) {
            return;
        }
        this.searchUpButton.setEnabled(!arrayList.isEmpty() && this.currentSearchIndex != 0);
        this.searchDownButton.setEnabled(!this.searchResults.isEmpty() && this.currentSearchIndex != this.searchResults.size() - 1);
        ImageView imageView = this.searchUpButton;
        float f = 1.0f;
        imageView.setAlpha(imageView.isEnabled() ? 1.0f : 0.5f);
        ImageView imageView2 = this.searchDownButton;
        if (!imageView2.isEnabled()) {
            f = 0.5f;
        }
        imageView2.setAlpha(f);
        int count = this.searchResults.size();
        if (count < 0) {
            this.searchCountText.setText("");
        } else if (count == 0) {
            this.searchCountText.setText(LocaleController.getString("NoResult", R.string.NoResult));
        } else if (count == 1) {
            this.searchCountText.setText(LocaleController.getString("OneResult", R.string.OneResult));
        } else {
            this.searchCountText.setText(String.format(LocaleController.getPluralString("CountOfResults", count), Integer.valueOf(this.currentSearchIndex + 1), Integer.valueOf(count)));
        }
    }

    /* loaded from: classes4.dex */
    public static class SearchResult {
        private TLRPC.PageBlock block;
        private int index;
        private Object text;

        private SearchResult() {
        }
    }

    public void processSearch(final String text) {
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
        if (TextUtils.isEmpty(text)) {
            this.searchResults.clear();
            this.searchText = text;
            this.adapter[0].searchTextOffset.clear();
            this.searchPanel.setVisibility(4);
            this.listView[0].invalidateViews();
            scrollToSearchIndex(0);
            this.lastSearchIndex = -1;
            return;
        }
        final int searchIndex = this.lastSearchIndex + 1;
        this.lastSearchIndex = searchIndex;
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1532lambda$processSearch$28$orgtelegramuiArticleViewer(text, searchIndex);
            }
        };
        this.searchRunnable = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 400L);
    }

    /* renamed from: lambda$processSearch$28$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1532lambda$processSearch$28$orgtelegramuiArticleViewer(final String text, final int searchIndex) {
        final HashMap<Object, TLRPC.PageBlock> copy = new HashMap<>(this.adapter[0].textToBlocks);
        final ArrayList<Object> array = new ArrayList<>(this.adapter[0].textBlocks);
        this.searchRunnable = null;
        Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1531lambda$processSearch$27$orgtelegramuiArticleViewer(array, copy, text, searchIndex);
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0090 A[SYNTHETIC] */
    /* renamed from: lambda$processSearch$27$org-telegram-ui-ArticleViewer */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m1531lambda$processSearch$27$orgtelegramuiArticleViewer(java.util.ArrayList r19, java.util.HashMap r20, final java.lang.String r21, final int r22) {
        /*
            r18 = this;
            r7 = r18
            r8 = r21
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r9 = r0
            r0 = 0
            int r10 = r19.size()
            r11 = r0
        L10:
            if (r11 >= r10) goto L94
            r12 = r19
            java.lang.Object r13 = r12.get(r11)
            r14 = r20
            java.lang.Object r0 = r14.get(r13)
            r15 = r0
            org.telegram.tgnet.TLRPC$PageBlock r15 = (org.telegram.tgnet.TLRPC.PageBlock) r15
            r16 = 0
            boolean r0 = r13 instanceof org.telegram.tgnet.TLRPC.RichText
            if (r0 == 0) goto L4f
            r17 = r13
            org.telegram.tgnet.TLRPC$RichText r17 = (org.telegram.tgnet.TLRPC.RichText) r17
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r0 = r7.adapter
            r1 = 0
            r1 = r0[r1]
            r2 = 0
            r6 = 1000(0x3e8, float:1.401E-42)
            r0 = r18
            r3 = r17
            r4 = r17
            r5 = r15
            java.lang.CharSequence r0 = r0.getText(r1, r2, r3, r4, r5, r6)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L5d
            java.lang.String r1 = r0.toString()
            java.lang.String r1 = r1.toLowerCase()
            r16 = r1
            goto L5d
        L4f:
            boolean r0 = r13 instanceof java.lang.String
            if (r0 == 0) goto L5d
            r0 = r13
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r16 = r0.toLowerCase()
            r0 = r16
            goto L5f
        L5d:
            r0 = r16
        L5f:
            if (r0 == 0) goto L90
            r1 = 0
        L62:
            int r2 = r0.indexOf(r8, r1)
            r3 = r2
            if (r2 < 0) goto L90
            int r2 = r21.length()
            int r1 = r3 + r2
            if (r3 == 0) goto L7d
            int r2 = r3 + (-1)
            char r2 = r0.charAt(r2)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r2)
            if (r2 == 0) goto L62
        L7d:
            org.telegram.ui.ArticleViewer$SearchResult r2 = new org.telegram.ui.ArticleViewer$SearchResult
            r4 = 0
            r2.<init>()
            org.telegram.ui.ArticleViewer.SearchResult.access$402(r2, r3)
            org.telegram.ui.ArticleViewer.SearchResult.access$202(r2, r15)
            org.telegram.ui.ArticleViewer.SearchResult.access$302(r2, r13)
            r9.add(r2)
            goto L62
        L90:
            int r11 = r11 + 1
            goto L10
        L94:
            r12 = r19
            r14 = r20
            org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda18 r0 = new org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda18
            r1 = r22
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.m1531lambda$processSearch$27$orgtelegramuiArticleViewer(java.util.ArrayList, java.util.HashMap, java.lang.String, int):void");
    }

    /* renamed from: lambda$processSearch$26$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1530lambda$processSearch$26$orgtelegramuiArticleViewer(int searchIndex, ArrayList results, String text) {
        if (searchIndex == this.lastSearchIndex) {
            this.searchPanel.setAlpha(1.0f);
            this.searchPanel.setVisibility(0);
            this.searchResults = results;
            this.searchText = text;
            this.adapter[0].searchTextOffset.clear();
            this.listView[0].invalidateViews();
            scrollToSearchIndex(0);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x007e  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00b5 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00b6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void scrollToSearchIndex(int r15) {
        /*
            Method dump skipped, instructions count: 380
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.scrollToSearchIndex(int):void");
    }

    /* loaded from: classes4.dex */
    public static class ScrollEvaluator extends IntEvaluator {
        @Override // android.animation.IntEvaluator
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return super.evaluate(fraction, startValue, endValue);
        }
    }

    public void checkScrollAnimated() {
        int maxHeight = AndroidUtilities.dp(56.0f);
        if (this.currentHeaderHeight == maxHeight) {
            return;
        }
        ValueAnimator va = ValueAnimator.ofObject(new IntEvaluator(), Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))).setDuration(180L);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ArticleViewer.this.m1516lambda$checkScrollAnimated$29$orgtelegramuiArticleViewer(valueAnimator);
            }
        });
        va.start();
    }

    /* renamed from: lambda$checkScrollAnimated$29$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1516lambda$checkScrollAnimated$29$orgtelegramuiArticleViewer(ValueAnimator animation) {
        setCurrentHeaderHeight(((Integer) animation.getAnimatedValue()).intValue());
    }

    public void setCurrentHeaderHeight(int newHeight) {
        if (this.searchContainer.getTag() != null) {
            return;
        }
        int maxHeight = AndroidUtilities.dp(56.0f);
        int minHeight = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
        if (newHeight < minHeight) {
            newHeight = minHeight;
        } else if (newHeight > maxHeight) {
            newHeight = maxHeight;
        }
        float heightDiff = maxHeight - minHeight;
        if (heightDiff == 0.0f) {
            heightDiff = 1.0f;
        }
        this.currentHeaderHeight = newHeight;
        float scale = (((newHeight - minHeight) / heightDiff) * 0.2f) + 0.8f;
        float scale2 = (((newHeight - minHeight) / heightDiff) * 0.5f) + 0.5f;
        this.backButton.setScaleX(scale);
        this.backButton.setScaleY(scale);
        this.backButton.setTranslationY((maxHeight - this.currentHeaderHeight) / 2);
        this.menuContainer.setScaleX(scale);
        this.menuContainer.setScaleY(scale);
        this.titleTextView.setScaleX(scale);
        this.titleTextView.setScaleY(scale);
        this.lineProgressView.setScaleY(scale2);
        this.menuContainer.setTranslationY((maxHeight - this.currentHeaderHeight) / 2);
        this.titleTextView.setTranslationY((maxHeight - this.currentHeaderHeight) / 2);
        this.headerView.setTranslationY(this.currentHeaderHeight - maxHeight);
        this.searchShadow.setTranslationY(this.currentHeaderHeight - maxHeight);
        this.menuButton.setAdditionalYOffset(((-(this.currentHeaderHeight - maxHeight)) / 2) + (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0));
        this.textSelectionHelper.setTopOffset(this.currentHeaderHeight);
        int i = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i < recyclerListViewArr.length) {
                recyclerListViewArr[i].setTopGlowOffset(this.currentHeaderHeight);
                i++;
            } else {
                return;
            }
        }
    }

    public void checkScroll(int dy) {
        setCurrentHeaderHeight(this.currentHeaderHeight - dy);
    }

    private void openPreviewsChat(TLRPC.User user, long wid) {
        if (user == null || !(this.parentActivity instanceof LaunchActivity)) {
            return;
        }
        Bundle args = new Bundle();
        args.putLong("user_id", user.id);
        args.putString("botUser", "webpage" + wid);
        ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(args), false, true);
        close(false, true);
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, null, null, true);
    }

    public boolean open(TLRPC.TL_webPage webpage, String url) {
        return open(null, webpage, url, true);
    }

    private boolean open(final MessageObject messageObject, TLRPC.WebPage webpage, String url, boolean first) {
        String anchor;
        TLRPC.WebPage webpage2;
        Paint paint;
        int index;
        String webPageUrl;
        if (this.parentActivity != null) {
            if (this.isVisible && !this.collapsed) {
                return false;
            }
            if (messageObject == null && webpage == null) {
                return false;
            }
            String anchor2 = null;
            int i = -1;
            if (messageObject != null) {
                TLRPC.WebPage webpage3 = messageObject.messageOwner.media.webpage;
                int a = 0;
                String url2 = url;
                while (true) {
                    if (a >= messageObject.messageOwner.entities.size()) {
                        break;
                    }
                    TLRPC.MessageEntity entity = messageObject.messageOwner.entities.get(a);
                    if (entity instanceof TLRPC.TL_messageEntityUrl) {
                        try {
                            url2 = messageObject.messageOwner.message.substring(entity.offset, entity.offset + entity.length).toLowerCase();
                            if (!TextUtils.isEmpty(webpage3.cached_page.url)) {
                                webPageUrl = webpage3.cached_page.url.toLowerCase();
                            } else {
                                webPageUrl = webpage3.url.toLowerCase();
                            }
                            if (!url2.contains(webPageUrl) && !webPageUrl.contains(url2)) {
                            }
                            int index2 = url2.lastIndexOf(35);
                            if (index2 == i) {
                                break;
                            }
                            anchor2 = url2.substring(index2 + 1);
                            break;
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    a++;
                }
                anchor = anchor2;
                webpage2 = webpage3;
            } else if (url != null && (index = url.lastIndexOf(35)) != -1) {
                webpage2 = webpage;
                anchor = url.substring(index + 1);
            } else {
                webpage2 = webpage;
                anchor = null;
            }
            this.pagesStack.clear();
            this.collapsed = false;
            this.containerView.setTranslationX(0.0f);
            this.containerView.setTranslationY(0.0f);
            this.listView[0].setTranslationY(0.0f);
            this.listView[0].setTranslationX(0.0f);
            this.listView[1].setTranslationX(0.0f);
            this.listView[0].setAlpha(1.0f);
            this.windowView.setInnerTranslationX(0.0f);
            this.layoutManager[0].scrollToPositionWithOffset(0, 0);
            if (first) {
                setCurrentHeaderHeight(AndroidUtilities.dp(56.0f));
            } else {
                checkScrollAnimated();
            }
            boolean scrolledToAnchor = addPageToStack(webpage2, anchor, 0);
            if (!first) {
                paint = null;
            } else {
                final String anchorFinal = (scrolledToAnchor || anchor == null) ? null : anchor;
                TLRPC.TL_messages_getWebPage req = new TLRPC.TL_messages_getWebPage();
                req.url = webpage2.url;
                if ((webpage2.cached_page instanceof TLRPC.TL_pagePart_layer82) || webpage2.cached_page.part) {
                    req.hash = 0;
                } else {
                    req.hash = webpage2.hash;
                }
                final TLRPC.WebPage webPageFinal = webpage2;
                final int currentAccount = UserConfig.selectedAccount;
                paint = null;
                ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda30
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ArticleViewer.this.m1525lambda$open$31$orgtelegramuiArticleViewer(webPageFinal, messageObject, currentAccount, anchorFinal, tLObject, tL_error);
                    }
                });
            }
            this.lastInsets = paint;
            if (!this.isVisible) {
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                if (this.attachedToWindow) {
                    try {
                        wm.removeView(this.windowView);
                    } catch (Exception e2) {
                    }
                }
                try {
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.windowLayoutParams.flags = -2013200384;
                        if (Build.VERSION.SDK_INT >= 28) {
                            this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
                        }
                    }
                    this.windowView.setFocusable(false);
                    this.containerView.setFocusable(false);
                    wm.addView(this.windowView, this.windowLayoutParams);
                } catch (Exception e3) {
                    FileLog.e(e3);
                    return false;
                }
            } else {
                this.windowLayoutParams.flags &= -17;
                ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
            }
            this.isVisible = true;
            this.animationInProgress = 1;
            this.windowView.setAlpha(0.0f);
            this.containerView.setAlpha(0.0f);
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, AndroidUtilities.dp(56.0f), 0.0f));
            this.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    ArticleViewer.this.m1526lambda$open$32$orgtelegramuiArticleViewer();
                }
            };
            animatorSet.setDuration(150L);
            animatorSet.setInterpolator(this.interpolator);
            animatorSet.addListener(new AnonymousClass23());
            this.transitionAnimationStartTime = System.currentTimeMillis();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    ArticleViewer.this.m1527lambda$open$33$orgtelegramuiArticleViewer(animatorSet);
                }
            });
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, paint);
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$open$31$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1525lambda$open$31$orgtelegramuiArticleViewer(final TLRPC.WebPage webPageFinal, final MessageObject messageObject, final int currentAccount, final String anchorFinal, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1524lambda$open$30$orgtelegramuiArticleViewer(response, webPageFinal, messageObject, currentAccount, anchorFinal);
            }
        });
    }

    /* renamed from: lambda$open$30$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1524lambda$open$30$orgtelegramuiArticleViewer(TLObject response, TLRPC.WebPage webPageFinal, MessageObject messageObject, int currentAccount, String anchorFinal) {
        if (response instanceof TLRPC.TL_webPage) {
            TLRPC.TL_webPage webPage = (TLRPC.TL_webPage) response;
            if (webPage.cached_page == null) {
                return;
            }
            if (!this.pagesStack.isEmpty() && this.pagesStack.get(0) == webPageFinal) {
                if (messageObject != null) {
                    messageObject.messageOwner.media.webpage = webPage;
                    TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
                    messagesRes.messages.add(messageObject.messageOwner);
                    MessagesStorage.getInstance(currentAccount).putMessages((TLRPC.messages_Messages) messagesRes, messageObject.getDialogId(), -2, 0, false, messageObject.scheduled);
                }
                this.pagesStack.set(0, webPage);
                if (this.pagesStack.size() == 1) {
                    ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().remove("article" + webPage.id).commit();
                    updateInterfaceForCurrentPage(webPage, false, 0);
                    if (anchorFinal != null) {
                        scrollToAnchor(anchorFinal);
                    }
                }
            }
            LongSparseArray<TLRPC.WebPage> webpages = new LongSparseArray<>(1);
            webpages.put(webPage.id, webPage);
            MessagesStorage.getInstance(currentAccount).putWebPages(webpages);
        } else if (response instanceof TLRPC.TL_webPageNotModified) {
            TLRPC.TL_webPageNotModified webPage2 = (TLRPC.TL_webPageNotModified) response;
            if (webPageFinal != null && webPageFinal.cached_page != null && webPageFinal.cached_page.views != webPage2.cached_page_views) {
                webPageFinal.cached_page.views = webPage2.cached_page_views;
                webPageFinal.cached_page.flags |= 8;
                int a = 0;
                while (true) {
                    WebpageAdapter[] webpageAdapterArr = this.adapter;
                    if (a >= webpageAdapterArr.length) {
                        break;
                    }
                    if (webpageAdapterArr[a].currentPage == webPageFinal) {
                        int p = this.adapter[a].getItemCount() - 1;
                        RecyclerView.ViewHolder holder = this.listView[a].findViewHolderForAdapterPosition(p);
                        if (holder != null) {
                            this.adapter[a].onViewAttachedToWindow(holder);
                        }
                    }
                    a++;
                }
                if (messageObject != null) {
                    TLRPC.TL_messages_messages messagesRes2 = new TLRPC.TL_messages_messages();
                    messagesRes2.messages.add(messageObject.messageOwner);
                    MessagesStorage.getInstance(currentAccount).putMessages((TLRPC.messages_Messages) messagesRes2, messageObject.getDialogId(), -2, 0, false, messageObject.scheduled);
                }
            }
        }
    }

    /* renamed from: lambda$open$32$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1526lambda$open$32$orgtelegramuiArticleViewer() {
        if (this.containerView == null || this.windowView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
    }

    /* renamed from: org.telegram.ui.ArticleViewer$23 */
    /* loaded from: classes4.dex */
    public class AnonymousClass23 extends AnimatorListenerAdapter {
        AnonymousClass23() {
            ArticleViewer.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$23$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ArticleViewer.AnonymousClass23.this.m1558lambda$onAnimationEnd$0$orgtelegramuiArticleViewer$23();
                }
            });
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ArticleViewer$23 */
        public /* synthetic */ void m1558lambda$onAnimationEnd$0$orgtelegramuiArticleViewer$23() {
            NotificationCenter.getInstance(ArticleViewer.this.currentAccount).onAnimationFinish(ArticleViewer.this.allowAnimationIndex);
            if (ArticleViewer.this.animationEndRunnable != null) {
                ArticleViewer.this.animationEndRunnable.run();
                ArticleViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* renamed from: lambda$open$33$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1527lambda$open$33$orgtelegramuiArticleViewer(AnimatorSet animatorSet) {
        this.allowAnimationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        animatorSet.start();
    }

    private void showProgressView(boolean useLine, final boolean show) {
        if (useLine) {
            AndroidUtilities.cancelRunOnUIThread(this.lineProgressTickRunnable);
            if (show) {
                this.lineProgressView.setProgress(0.0f, false);
                this.lineProgressView.setProgress(0.3f, true);
                AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100L);
                return;
            }
            this.lineProgressView.setProgress(1.0f, true);
            return;
        }
        AnimatorSet animatorSet = this.progressViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressViewAnimation = new AnimatorSet();
        if (show) {
            this.progressView.setVisibility(0);
            this.menuContainer.setEnabled(false);
            this.progressViewAnimation.playTogether(ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 1.0f));
        } else {
            this.menuButton.setVisibility(0);
            this.menuContainer.setEnabled(true);
            this.progressViewAnimation.playTogether(ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, 1.0f));
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.24
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    if (!show) {
                        ArticleViewer.this.progressView.setVisibility(4);
                    } else {
                        ArticleViewer.this.menuButton.setVisibility(4);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    ArticleViewer.this.progressViewAnimation = null;
                }
            }
        });
        this.progressViewAnimation.setDuration(150L);
        this.progressViewAnimation.start();
    }

    public void collapse() {
        if (this.parentActivity == null || !this.isVisible || checkAnimation()) {
            return;
        }
        if (this.fullscreenVideoContainer.getVisibility() == 0) {
            if (this.customView != null) {
                this.fullscreenVideoContainer.setVisibility(4);
                this.customViewCallback.onCustomViewHidden();
                this.fullscreenVideoContainer.removeView(this.customView);
                this.customView = null;
            } else {
                WebPlayerView webPlayerView = this.fullscreenedVideo;
                if (webPlayerView != null) {
                    webPlayerView.exitFullscreen();
                }
            }
        }
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null) {
                dialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[12];
        animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, this.containerView.getMeasuredWidth() - AndroidUtilities.dp(56.0f));
        FrameLayout frameLayout = this.containerView;
        Property property = View.TRANSLATION_Y;
        float[] fArr = new float[1];
        fArr[0] = ActionBar.getCurrentActionBarHeight() + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 0.0f);
        animatorArr[3] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, 0.0f);
        animatorArr[4] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, -AndroidUtilities.dp(56.0f));
        animatorArr[5] = ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, 0.0f);
        animatorArr[6] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, 1.0f);
        animatorArr[7] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, 1.0f);
        animatorArr[8] = ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, 0.0f);
        animatorArr[9] = ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_X, 1.0f);
        animatorArr[10] = ObjectAnimator.ofFloat(this.menuContainer, View.TRANSLATION_Y, 0.0f);
        animatorArr[11] = ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_Y, 1.0f);
        animatorSet.playTogether(animatorArr);
        this.collapsed = true;
        this.animationInProgress = 2;
        this.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1518lambda$collapse$34$orgtelegramuiArticleViewer();
            }
        };
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(250L);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.25
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ArticleViewer.this.animationEndRunnable != null) {
                    ArticleViewer.this.animationEndRunnable.run();
                    ArticleViewer.this.animationEndRunnable = null;
                }
            }
        });
        this.transitionAnimationStartTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(2, null);
        }
        this.backDrawable.setRotation(1.0f, true);
        animatorSet.start();
    }

    /* renamed from: lambda$collapse$34$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1518lambda$collapse$34$orgtelegramuiArticleViewer() {
        if (this.containerView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
        wm.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    public void uncollapse() {
        if (this.parentActivity == null || !this.isVisible || checkAnimation()) {
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.menuContainer, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_Y, 1.0f));
        this.collapsed = false;
        this.animationInProgress = 2;
        this.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1556lambda$uncollapse$35$orgtelegramuiArticleViewer();
            }
        };
        animatorSet.setDuration(250L);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.26
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ArticleViewer.this.animationEndRunnable != null) {
                    ArticleViewer.this.animationEndRunnable.run();
                    ArticleViewer.this.animationEndRunnable = null;
                }
            }
        });
        this.transitionAnimationStartTime = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(2, null);
        }
        this.backDrawable.setRotation(0.0f, true);
        animatorSet.start();
    }

    /* renamed from: lambda$uncollapse$35$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1556lambda$uncollapse$35$orgtelegramuiArticleViewer() {
        if (this.containerView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
    }

    public void saveCurrentPagePosition() {
        int position;
        int offset;
        boolean z = false;
        if (this.adapter[0].currentPage != null && (position = this.layoutManager[0].findFirstVisibleItemPosition()) != -1) {
            View view = this.layoutManager[0].findViewByPosition(position);
            if (view != null) {
                offset = view.getTop();
            } else {
                offset = 0;
            }
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
            String key = "article" + this.adapter[0].currentPage.id;
            SharedPreferences.Editor putInt = editor.putInt(key, position).putInt(key + "o", offset);
            String str = key + "r";
            if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                z = true;
            }
            putInt.putBoolean(str, z).commit();
        }
    }

    private void refreshThemeColors() {
        TextView textView = this.deleteView;
        if (textView != null) {
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            this.deleteView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
        }
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.searchField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.searchField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        }
        ImageView imageView = this.searchUpButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
            this.searchUpButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), 1));
        }
        ImageView imageView2 = this.searchDownButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
            this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), 1));
        }
        SimpleTextView simpleTextView = this.searchCountText;
        if (simpleTextView != null) {
            simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
        ActionBarMenuItem actionBarMenuItem = this.menuButton;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
            this.menuButton.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
            this.menuButton.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true);
        }
        ImageView imageView3 = this.clearButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.MULTIPLY));
        }
        BackDrawable backDrawable = this.backDrawable;
        if (backDrawable != null) {
            backDrawable.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
    }

    public void close(boolean byBackPress, boolean force) {
        if (this.parentActivity == null || this.closeAnimationInProgress || !this.isVisible || checkAnimation()) {
            return;
        }
        if (this.fullscreenVideoContainer.getVisibility() == 0) {
            if (this.customView != null) {
                this.fullscreenVideoContainer.setVisibility(4);
                this.customViewCallback.onCustomViewHidden();
                this.fullscreenVideoContainer.removeView(this.customView);
                this.customView = null;
            } else {
                WebPlayerView webPlayerView = this.fullscreenedVideo;
                if (webPlayerView != null) {
                    webPlayerView.exitFullscreen();
                }
            }
            if (!force) {
                return;
            }
        }
        if (this.textSelectionHelper.isSelectionMode()) {
            this.textSelectionHelper.clear();
        } else if (this.searchContainer.getTag() != null) {
            showSearch(false);
        } else {
            if (this.openUrlReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, true);
                this.openUrlReqId = 0;
                showProgressView(true, false);
            }
            if (this.previewsReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.previewsReqId, true);
                this.previewsReqId = 0;
                showProgressView(true, false);
            }
            saveCurrentPagePosition();
            if (byBackPress && !force && removeLastPageFromStack()) {
                return;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
            this.parentFragment = null;
            try {
                Dialog dialog = this.visibleDialog;
                if (dialog != null) {
                    dialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, 0.0f, AndroidUtilities.dp(56.0f)));
            this.animationInProgress = 2;
            this.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ArticleViewer.this.m1517lambda$close$36$orgtelegramuiArticleViewer();
                }
            };
            animatorSet.setDuration(150L);
            animatorSet.setInterpolator(this.interpolator);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ArticleViewer.27
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ArticleViewer.this.animationEndRunnable != null) {
                        ArticleViewer.this.animationEndRunnable.run();
                        ArticleViewer.this.animationEndRunnable = null;
                    }
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, null);
            }
            animatorSet.start();
        }
    }

    /* renamed from: lambda$close$36$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1517lambda$close$36$orgtelegramuiArticleViewer() {
        if (this.containerView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        onClosed();
    }

    public void onClosed() {
        this.isVisible = false;
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].cleanup();
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e) {
            FileLog.e(e);
        }
        for (int a = 0; a < this.createdWebViews.size(); a++) {
            BlockEmbedCell cell = this.createdWebViews.get(a);
            cell.destroyWebView(false);
        }
        this.containerView.post(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1523lambda$onClosed$37$orgtelegramuiArticleViewer();
            }
        });
    }

    /* renamed from: lambda$onClosed$37$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1523lambda$onClosed$37$orgtelegramuiArticleViewer() {
        try {
            if (this.windowView.getParent() != null) {
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                wm.removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void loadChannel(final BlockChannelCell cell, final WebpageAdapter adapter, TLRPC.Chat channel) {
        if (this.loadingChannel || TextUtils.isEmpty(channel.username)) {
            return;
        }
        this.loadingChannel = true;
        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        req.username = channel.username;
        final int currentAccount = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda32
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ArticleViewer.this.m1522lambda$loadChannel$39$orgtelegramuiArticleViewer(adapter, currentAccount, cell, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadChannel$39$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1522lambda$loadChannel$39$orgtelegramuiArticleViewer(final WebpageAdapter adapter, final int currentAccount, final BlockChannelCell cell, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.this.m1521lambda$loadChannel$38$orgtelegramuiArticleViewer(adapter, error, response, currentAccount, cell);
            }
        });
    }

    /* renamed from: lambda$loadChannel$38$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1521lambda$loadChannel$38$orgtelegramuiArticleViewer(WebpageAdapter adapter, TLRPC.TL_error error, TLObject response, int currentAccount, BlockChannelCell cell) {
        this.loadingChannel = false;
        if (this.parentFragment == null || adapter.blocks.isEmpty()) {
            return;
        }
        if (error == null) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            if (!res.chats.isEmpty()) {
                MessagesController.getInstance(currentAccount).putUsers(res.users, false);
                MessagesController.getInstance(currentAccount).putChats(res.chats, false);
                MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, false, true);
                TLRPC.Chat chat = res.chats.get(0);
                this.loadedChannel = chat;
                if (chat.left && !this.loadedChannel.kicked) {
                    cell.setState(0, false);
                    return;
                } else {
                    cell.setState(4, false);
                    return;
                }
            }
            cell.setState(4, false);
            return;
        }
        cell.setState(4, false);
    }

    public void joinChannel(final BlockChannelCell cell, final TLRPC.Chat channel) {
        final TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
        req.channel = MessagesController.getInputChannel(channel);
        final int currentAccount = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda31
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ArticleViewer.this.m1520lambda$joinChannel$43$orgtelegramuiArticleViewer(cell, currentAccount, req, channel, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$joinChannel$43$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1520lambda$joinChannel$43$orgtelegramuiArticleViewer(final BlockChannelCell cell, final int currentAccount, final TLRPC.TL_channels_joinChannel req, final TLRPC.Chat channel, TLObject response, final TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    ArticleViewer.this.m1519lambda$joinChannel$40$orgtelegramuiArticleViewer(cell, currentAccount, error, req);
                }
            });
            return;
        }
        boolean hasJoinMessage = false;
        TLRPC.Updates updates = (TLRPC.Updates) response;
        int a = 0;
        while (true) {
            if (a >= updates.updates.size()) {
                break;
            }
            TLRPC.Update update = updates.updates.get(a);
            if (!(update instanceof TLRPC.TL_updateNewChannelMessage) || !(((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                a++;
            } else {
                hasJoinMessage = true;
                break;
            }
        }
        MessagesController.getInstance(currentAccount).processUpdates(updates, false);
        if (!hasJoinMessage) {
            MessagesController.getInstance(currentAccount).generateJoinMessage(channel.id, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ArticleViewer.BlockChannelCell.this.setState(2, false);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.getInstance(currentAccount).loadFullChat(channel.id, 0, true);
            }
        }, 1000L);
        MessagesStorage.getInstance(currentAccount).updateDialogsWithDeletedMessages(-channel.id, channel.id, new ArrayList<>(), null, true);
    }

    /* renamed from: lambda$joinChannel$40$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1519lambda$joinChannel$40$orgtelegramuiArticleViewer(BlockChannelCell cell, int currentAccount, TLRPC.TL_error error, TLRPC.TL_channels_joinChannel req) {
        cell.setState(0, false);
        AlertsCreator.processError(currentAccount, error, this.parentFragment, req, true);
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.animationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        return this.animationInProgress != 0;
    }

    public void destroyArticleViewer() {
        WindowView windowView;
        if (this.parentActivity == null || (windowView = this.windowView) == null) {
            return;
        }
        try {
            if (windowView.getParent() != null) {
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                wm.removeViewImmediate(this.windowView);
            }
            this.windowView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        for (int a = 0; a < this.createdWebViews.size(); a++) {
            BlockEmbedCell cell = this.createdWebViews.get(a);
            cell.destroyWebView(true);
        }
        this.createdWebViews.clear();
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        this.parentActivity = null;
        this.parentFragment = null;
        Instance = null;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void showDialog(Dialog dialog) {
        if (this.parentActivity == null) {
            return;
        }
        try {
            Dialog dialog2 = this.visibleDialog;
            if (dialog2 != null) {
                dialog2.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.visibleDialog = dialog;
            dialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda33
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ArticleViewer.this.m1550lambda$showDialog$44$orgtelegramuiArticleViewer(dialogInterface);
                }
            });
            dialog.show();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* renamed from: lambda$showDialog$44$org-telegram-ui-ArticleViewer */
    public /* synthetic */ void m1550lambda$showDialog$44$orgtelegramuiArticleViewer(DialogInterface dialog1) {
        this.visibleDialog = null;
    }

    /* loaded from: classes4.dex */
    public static final class WebPageUtils {
        private WebPageUtils() {
        }

        public static TLRPC.Photo getPhotoWithId(TLRPC.WebPage page, long id) {
            if (page == null || page.cached_page == null) {
                return null;
            }
            if (page.photo != null && page.photo.id == id) {
                return page.photo;
            }
            for (int a = 0; a < page.cached_page.photos.size(); a++) {
                TLRPC.Photo photo = page.cached_page.photos.get(a);
                if (photo.id == id) {
                    return photo;
                }
            }
            return null;
        }

        public static TLRPC.Document getDocumentWithId(TLRPC.WebPage page, long id) {
            if (page == null || page.cached_page == null) {
                return null;
            }
            if (page.document != null && page.document.id == id) {
                return page.document;
            }
            for (int a = 0; a < page.cached_page.documents.size(); a++) {
                TLRPC.Document document = page.cached_page.documents.get(a);
                if (document.id == id) {
                    return document;
                }
            }
            return null;
        }

        public static boolean isVideo(TLRPC.WebPage page, TLRPC.PageBlock block) {
            TLRPC.Document document;
            if ((block instanceof TLRPC.TL_pageBlockVideo) && (document = getDocumentWithId(page, ((TLRPC.TL_pageBlockVideo) block).video_id)) != null) {
                return MessageObject.isVideoDocument(document);
            }
            return false;
        }

        public static TLObject getMedia(TLRPC.WebPage page, TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                return getPhotoWithId(page, ((TLRPC.TL_pageBlockPhoto) block).photo_id);
            }
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                return getDocumentWithId(page, ((TLRPC.TL_pageBlockVideo) block).video_id);
            }
            return null;
        }

        public static File getMediaFile(TLRPC.WebPage page, TLRPC.PageBlock block) {
            TLRPC.Document document;
            TLRPC.PhotoSize sizeFull;
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.Photo photo = getPhotoWithId(page, ((TLRPC.TL_pageBlockPhoto) block).photo_id);
                if (photo != null && (sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize())) != null) {
                    return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(sizeFull, true);
                }
                return null;
            } else if ((block instanceof TLRPC.TL_pageBlockVideo) && (document = getDocumentWithId(page, ((TLRPC.TL_pageBlockVideo) block).video_id)) != null) {
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
            } else {
                return null;
            }
        }
    }

    /* loaded from: classes4.dex */
    public class WebpageAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.TL_pageBlockChannel channelBlock;
        private Context context;
        private TLRPC.WebPage currentPage;
        private boolean isRtl;
        private ArrayList<TLRPC.PageBlock> localBlocks = new ArrayList<>();
        private ArrayList<TLRPC.PageBlock> blocks = new ArrayList<>();
        private ArrayList<TLRPC.PageBlock> photoBlocks = new ArrayList<>();
        private HashMap<String, Integer> anchors = new HashMap<>();
        private HashMap<String, Integer> anchorsOffset = new HashMap<>();
        private HashMap<String, TLRPC.TL_textAnchor> anchorsParent = new HashMap<>();
        private HashMap<TLRPC.TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap<>();
        private ArrayList<MessageObject> audioMessages = new ArrayList<>();
        private HashMap<Object, TLRPC.PageBlock> textToBlocks = new HashMap<>();
        private ArrayList<Object> textBlocks = new ArrayList<>();
        private HashMap<String, Integer> searchTextOffset = new HashMap<>();

        public WebpageAdapter(Context ctx) {
            ArticleViewer.this = r1;
            this.context = ctx;
        }

        public TLRPC.Photo getPhotoWithId(long id) {
            return WebPageUtils.getPhotoWithId(this.currentPage, id);
        }

        public TLRPC.Document getDocumentWithId(long id) {
            return WebPageUtils.getDocumentWithId(this.currentPage, id);
        }

        private void setRichTextParents(TLRPC.RichText parentRichText, TLRPC.RichText richText) {
            if (richText == null) {
                return;
            }
            richText.parentRichText = parentRichText;
            if (richText instanceof TLRPC.TL_textFixed) {
                setRichTextParents(richText, ((TLRPC.TL_textFixed) richText).text);
            } else if (richText instanceof TLRPC.TL_textItalic) {
                setRichTextParents(richText, ((TLRPC.TL_textItalic) richText).text);
            } else if (richText instanceof TLRPC.TL_textBold) {
                setRichTextParents(richText, ((TLRPC.TL_textBold) richText).text);
            } else if (richText instanceof TLRPC.TL_textUnderline) {
                setRichTextParents(richText, ((TLRPC.TL_textUnderline) richText).text);
            } else if (richText instanceof TLRPC.TL_textStrike) {
                setRichTextParents(richText, ((TLRPC.TL_textStrike) richText).text);
            } else if (richText instanceof TLRPC.TL_textEmail) {
                setRichTextParents(richText, ((TLRPC.TL_textEmail) richText).text);
            } else if (richText instanceof TLRPC.TL_textPhone) {
                setRichTextParents(richText, ((TLRPC.TL_textPhone) richText).text);
            } else if (richText instanceof TLRPC.TL_textUrl) {
                setRichTextParents(richText, ((TLRPC.TL_textUrl) richText).text);
            } else if (richText instanceof TLRPC.TL_textConcat) {
                int count = richText.texts.size();
                for (int a = 0; a < count; a++) {
                    setRichTextParents(richText, richText.texts.get(a));
                }
            } else if (richText instanceof TLRPC.TL_textSubscript) {
                setRichTextParents(richText, ((TLRPC.TL_textSubscript) richText).text);
            } else if (richText instanceof TLRPC.TL_textSuperscript) {
                setRichTextParents(richText, ((TLRPC.TL_textSuperscript) richText).text);
            } else if (richText instanceof TLRPC.TL_textMarked) {
                setRichTextParents(richText, ((TLRPC.TL_textMarked) richText).text);
            } else if (richText instanceof TLRPC.TL_textAnchor) {
                TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) richText;
                setRichTextParents(richText, textAnchor.text);
                String name = textAnchor.name.toLowerCase();
                this.anchors.put(name, Integer.valueOf(this.blocks.size()));
                if (textAnchor.text instanceof TLRPC.TL_textPlain) {
                    TLRPC.TL_textPlain textPlain = (TLRPC.TL_textPlain) textAnchor.text;
                    if (!TextUtils.isEmpty(textPlain.text)) {
                        this.anchorsParent.put(name, textAnchor);
                    }
                } else if (!(textAnchor.text instanceof TLRPC.TL_textEmpty)) {
                    this.anchorsParent.put(name, textAnchor);
                }
                this.anchorsOffset.put(name, -1);
            }
        }

        private void addTextBlock(Object text, TLRPC.PageBlock block) {
            if ((text instanceof TLRPC.TL_textEmpty) || this.textToBlocks.containsKey(text)) {
                return;
            }
            this.textToBlocks.put(text, block);
            this.textBlocks.add(text);
        }

        private void setRichTextParents(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
                TLRPC.TL_pageBlockEmbedPost blockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block;
                setRichTextParents(null, blockEmbedPost.caption.text);
                setRichTextParents(null, blockEmbedPost.caption.credit);
                addTextBlock(blockEmbedPost.caption.text, blockEmbedPost);
                addTextBlock(blockEmbedPost.caption.credit, blockEmbedPost);
            } else if (block instanceof TLRPC.TL_pageBlockParagraph) {
                TLRPC.TL_pageBlockParagraph pageBlockParagraph = (TLRPC.TL_pageBlockParagraph) block;
                setRichTextParents(null, pageBlockParagraph.text);
                addTextBlock(pageBlockParagraph.text, pageBlockParagraph);
            } else if (block instanceof TLRPC.TL_pageBlockKicker) {
                TLRPC.TL_pageBlockKicker pageBlockKicker = (TLRPC.TL_pageBlockKicker) block;
                setRichTextParents(null, pageBlockKicker.text);
                addTextBlock(pageBlockKicker.text, pageBlockKicker);
            } else if (block instanceof TLRPC.TL_pageBlockFooter) {
                TLRPC.TL_pageBlockFooter pageBlockFooter = (TLRPC.TL_pageBlockFooter) block;
                setRichTextParents(null, pageBlockFooter.text);
                addTextBlock(pageBlockFooter.text, pageBlockFooter);
            } else if (block instanceof TLRPC.TL_pageBlockHeader) {
                TLRPC.TL_pageBlockHeader pageBlockHeader = (TLRPC.TL_pageBlockHeader) block;
                setRichTextParents(null, pageBlockHeader.text);
                addTextBlock(pageBlockHeader.text, pageBlockHeader);
            } else if (block instanceof TLRPC.TL_pageBlockPreformatted) {
                TLRPC.TL_pageBlockPreformatted pageBlockPreformatted = (TLRPC.TL_pageBlockPreformatted) block;
                setRichTextParents(null, pageBlockPreformatted.text);
                addTextBlock(pageBlockPreformatted.text, pageBlockPreformatted);
            } else if (block instanceof TLRPC.TL_pageBlockSubheader) {
                TLRPC.TL_pageBlockSubheader pageBlockTitle = (TLRPC.TL_pageBlockSubheader) block;
                setRichTextParents(null, pageBlockTitle.text);
                addTextBlock(pageBlockTitle.text, pageBlockTitle);
            } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) block;
                setRichTextParents(null, pageBlockSlideshow.caption.text);
                setRichTextParents(null, pageBlockSlideshow.caption.credit);
                addTextBlock(pageBlockSlideshow.caption.text, pageBlockSlideshow);
                addTextBlock(pageBlockSlideshow.caption.credit, pageBlockSlideshow);
                int size = pageBlockSlideshow.items.size();
                for (int a = 0; a < size; a++) {
                    setRichTextParents(pageBlockSlideshow.items.get(a));
                }
            } else if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                setRichTextParents(null, pageBlockPhoto.caption.text);
                setRichTextParents(null, pageBlockPhoto.caption.credit);
                addTextBlock(pageBlockPhoto.caption.text, pageBlockPhoto);
                addTextBlock(pageBlockPhoto.caption.credit, pageBlockPhoto);
            } else if (block instanceof TL_pageBlockListItem) {
                TL_pageBlockListItem pageBlockListItem = (TL_pageBlockListItem) block;
                if (pageBlockListItem.textItem != null) {
                    setRichTextParents(null, pageBlockListItem.textItem);
                    addTextBlock(pageBlockListItem.textItem, pageBlockListItem);
                } else if (pageBlockListItem.blockItem != null) {
                    setRichTextParents(pageBlockListItem.blockItem);
                }
            } else if (block instanceof TL_pageBlockOrderedListItem) {
                TL_pageBlockOrderedListItem pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) block;
                if (pageBlockOrderedListItem.textItem != null) {
                    setRichTextParents(null, pageBlockOrderedListItem.textItem);
                    addTextBlock(pageBlockOrderedListItem.textItem, pageBlockOrderedListItem);
                } else if (pageBlockOrderedListItem.blockItem != null) {
                    setRichTextParents(pageBlockOrderedListItem.blockItem);
                }
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) block;
                setRichTextParents(null, pageBlockCollage.caption.text);
                setRichTextParents(null, pageBlockCollage.caption.credit);
                addTextBlock(pageBlockCollage.caption.text, pageBlockCollage);
                addTextBlock(pageBlockCollage.caption.credit, pageBlockCollage);
                int size2 = pageBlockCollage.items.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    setRichTextParents(pageBlockCollage.items.get(a2));
                }
            } else if (block instanceof TLRPC.TL_pageBlockEmbed) {
                TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) block;
                setRichTextParents(null, pageBlockEmbed.caption.text);
                setRichTextParents(null, pageBlockEmbed.caption.credit);
                addTextBlock(pageBlockEmbed.caption.text, pageBlockEmbed);
                addTextBlock(pageBlockEmbed.caption.credit, pageBlockEmbed);
            } else if (block instanceof TLRPC.TL_pageBlockSubtitle) {
                TLRPC.TL_pageBlockSubtitle pageBlockSubtitle = (TLRPC.TL_pageBlockSubtitle) block;
                setRichTextParents(null, pageBlockSubtitle.text);
                addTextBlock(pageBlockSubtitle.text, pageBlockSubtitle);
            } else if (block instanceof TLRPC.TL_pageBlockBlockquote) {
                TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) block;
                setRichTextParents(null, pageBlockBlockquote.text);
                setRichTextParents(null, pageBlockBlockquote.caption);
                addTextBlock(pageBlockBlockquote.text, pageBlockBlockquote);
                addTextBlock(pageBlockBlockquote.caption, pageBlockBlockquote);
            } else if (block instanceof TLRPC.TL_pageBlockDetails) {
                TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) block;
                setRichTextParents(null, pageBlockDetails.title);
                addTextBlock(pageBlockDetails.title, pageBlockDetails);
                int size3 = pageBlockDetails.blocks.size();
                for (int a3 = 0; a3 < size3; a3++) {
                    setRichTextParents(pageBlockDetails.blocks.get(a3));
                }
            } else if (block instanceof TLRPC.TL_pageBlockVideo) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                setRichTextParents(null, pageBlockVideo.caption.text);
                setRichTextParents(null, pageBlockVideo.caption.credit);
                addTextBlock(pageBlockVideo.caption.text, pageBlockVideo);
                addTextBlock(pageBlockVideo.caption.credit, pageBlockVideo);
            } else if (block instanceof TLRPC.TL_pageBlockPullquote) {
                TLRPC.TL_pageBlockPullquote pageBlockPullquote = (TLRPC.TL_pageBlockPullquote) block;
                setRichTextParents(null, pageBlockPullquote.text);
                setRichTextParents(null, pageBlockPullquote.caption);
                addTextBlock(pageBlockPullquote.text, pageBlockPullquote);
                addTextBlock(pageBlockPullquote.caption, pageBlockPullquote);
            } else if (block instanceof TLRPC.TL_pageBlockAudio) {
                TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                setRichTextParents(null, pageBlockAudio.caption.text);
                setRichTextParents(null, pageBlockAudio.caption.credit);
                addTextBlock(pageBlockAudio.caption.text, pageBlockAudio);
                addTextBlock(pageBlockAudio.caption.credit, pageBlockAudio);
            } else if (block instanceof TLRPC.TL_pageBlockTable) {
                TLRPC.TL_pageBlockTable pageBlockTable = (TLRPC.TL_pageBlockTable) block;
                setRichTextParents(null, pageBlockTable.title);
                addTextBlock(pageBlockTable.title, pageBlockTable);
                int size4 = pageBlockTable.rows.size();
                for (int a4 = 0; a4 < size4; a4++) {
                    TLRPC.TL_pageTableRow row = pageBlockTable.rows.get(a4);
                    int size22 = row.cells.size();
                    for (int b = 0; b < size22; b++) {
                        TLRPC.TL_pageTableCell cell = row.cells.get(b);
                        setRichTextParents(null, cell.text);
                        addTextBlock(cell.text, pageBlockTable);
                    }
                }
            } else if (block instanceof TLRPC.TL_pageBlockTitle) {
                TLRPC.TL_pageBlockTitle pageBlockTitle2 = (TLRPC.TL_pageBlockTitle) block;
                setRichTextParents(null, pageBlockTitle2.text);
                addTextBlock(pageBlockTitle2.text, pageBlockTitle2);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                TLRPC.TL_pageBlockCover pageBlockCover = (TLRPC.TL_pageBlockCover) block;
                setRichTextParents(pageBlockCover.cover);
            } else if (block instanceof TLRPC.TL_pageBlockAuthorDate) {
                TLRPC.TL_pageBlockAuthorDate pageBlockAuthorDate = (TLRPC.TL_pageBlockAuthorDate) block;
                setRichTextParents(null, pageBlockAuthorDate.author);
                addTextBlock(pageBlockAuthorDate.author, pageBlockAuthorDate);
            } else if (block instanceof TLRPC.TL_pageBlockMap) {
                TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                setRichTextParents(null, pageBlockMap.caption.text);
                setRichTextParents(null, pageBlockMap.caption.credit);
                addTextBlock(pageBlockMap.caption.text, pageBlockMap);
                addTextBlock(pageBlockMap.caption.credit, pageBlockMap);
            } else if (block instanceof TLRPC.TL_pageBlockRelatedArticles) {
                TLRPC.TL_pageBlockRelatedArticles pageBlockRelatedArticles = (TLRPC.TL_pageBlockRelatedArticles) block;
                setRichTextParents(null, pageBlockRelatedArticles.title);
                addTextBlock(pageBlockRelatedArticles.title, pageBlockRelatedArticles);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:106:0x0348  */
        /* JADX WARN: Removed duplicated region for block: B:186:0x03cb A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:98:0x02ee  */
        /* JADX WARN: Removed duplicated region for block: B:99:0x031f  */
        /* JADX WARN: Type inference failed for: r27v0, types: [org.telegram.ui.ArticleViewer$WebpageAdapter] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void addBlock(org.telegram.ui.ArticleViewer.WebpageAdapter r28, org.telegram.tgnet.TLRPC.PageBlock r29, int r30, int r31, int r32) {
            /*
                Method dump skipped, instructions count: 1592
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.WebpageAdapter.addBlock(org.telegram.ui.ArticleViewer$WebpageAdapter, org.telegram.tgnet.TLRPC$PageBlock, int, int, int):void");
        }

        private void addAllMediaFromBlock(WebpageAdapter adapter, TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                TLRPC.Photo photo = getPhotoWithId(pageBlockPhoto.photo_id);
                if (photo != null) {
                    pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 56, true);
                    pageBlockPhoto.thumbObject = photo;
                    this.photoBlocks.add(block);
                }
            } else if ((block instanceof TLRPC.TL_pageBlockVideo) && WebPageUtils.isVideo(adapter.currentPage, block)) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                TLRPC.Document document = getDocumentWithId(pageBlockVideo.video_id);
                if (document != null) {
                    pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 56, true);
                    pageBlockVideo.thumbObject = document;
                    this.photoBlocks.add(block);
                }
            } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow slideshow = (TLRPC.TL_pageBlockSlideshow) block;
                int count = slideshow.items.size();
                for (int a = 0; a < count; a++) {
                    TLRPC.PageBlock innerBlock = slideshow.items.get(a);
                    innerBlock.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(adapter, innerBlock);
                }
                ArticleViewer.access$13208(ArticleViewer.this);
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage collage = (TLRPC.TL_pageBlockCollage) block;
                int count2 = collage.items.size();
                for (int a2 = 0; a2 < count2; a2++) {
                    TLRPC.PageBlock innerBlock2 = collage.items.get(a2);
                    innerBlock2.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(adapter, innerBlock2);
                }
                ArticleViewer.access$13208(ArticleViewer.this);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                TLRPC.TL_pageBlockCover pageBlockCover = (TLRPC.TL_pageBlockCover) block;
                addAllMediaFromBlock(adapter, pageBlockCover.cover);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new BlockParagraphCell(this.context, this);
                    break;
                case 1:
                    view = new BlockHeaderCell(this.context, this);
                    break;
                case 2:
                    view = new BlockDividerCell(this.context);
                    break;
                case 3:
                    view = new BlockEmbedCell(this.context, this);
                    break;
                case 4:
                    view = new BlockSubtitleCell(this.context, this);
                    break;
                case 5:
                    view = new BlockVideoCell(this.context, this, 0);
                    break;
                case 6:
                    view = new BlockPullquoteCell(this.context, this);
                    break;
                case 7:
                    view = new BlockBlockquoteCell(this.context, this);
                    break;
                case 8:
                    view = new BlockSlideshowCell(this.context, this);
                    break;
                case 9:
                    view = new BlockPhotoCell(this.context, this, 0);
                    break;
                case 10:
                    view = new BlockAuthorDateCell(this.context, this);
                    break;
                case 11:
                    view = new BlockTitleCell(this.context, this);
                    break;
                case 12:
                    view = new BlockListItemCell(this.context, this);
                    break;
                case 13:
                    view = new BlockFooterCell(this.context, this);
                    break;
                case 14:
                    view = new BlockPreformattedCell(this.context, this);
                    break;
                case 15:
                    view = new BlockSubheaderCell(this.context, this);
                    break;
                case 16:
                    view = new BlockEmbedPostCell(this.context, this);
                    break;
                case 17:
                    view = new BlockCollageCell(this.context, this);
                    break;
                case 18:
                    view = new BlockChannelCell(this.context, this, 0);
                    break;
                case 19:
                    view = new BlockAudioCell(this.context, this);
                    break;
                case 20:
                    view = new BlockKickerCell(this.context, this);
                    break;
                case 21:
                    view = new BlockOrderedListItemCell(this.context, this);
                    break;
                case 22:
                    view = new BlockMapCell(this.context, this, 0);
                    break;
                case 23:
                    view = new BlockRelatedArticlesCell(this.context, this);
                    break;
                case 24:
                    view = new BlockDetailsCell(this.context, this);
                    break;
                case 25:
                    view = new BlockTableCell(this.context, this);
                    break;
                case 26:
                    view = new BlockRelatedArticlesHeaderCell(this.context, this);
                    break;
                case 27:
                    view = new BlockDetailsBottomCell(this.context);
                    break;
                case 28:
                    view = new BlockRelatedArticlesShadowCell(this.context);
                    break;
                case 90:
                    view = new ReportCell(this.context);
                    break;
                default:
                    TextView textView = new TextView(this.context);
                    textView.setBackgroundColor(SupportMenu.CATEGORY_MASK);
                    textView.setTextColor(-16777216);
                    textView.setTextSize(1, 20.0f);
                    view = textView;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            view.setFocusable(true);
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 23 || type == 24) {
                return true;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < this.localBlocks.size()) {
                TLRPC.PageBlock block = this.localBlocks.get(position);
                bindBlockToHolder(holder.getItemViewType(), holder, block, position, this.localBlocks.size());
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 90) {
                ReportCell cell = (ReportCell) holder.itemView;
                cell.setViews(this.currentPage.cached_page != null ? this.currentPage.cached_page.views : 0);
            }
        }

        public void bindBlockToHolder(int type, RecyclerView.ViewHolder holder, TLRPC.PageBlock block, int position, int total) {
            if (block instanceof TLRPC.TL_pageBlockCover) {
                block = ((TLRPC.TL_pageBlockCover) block).cover;
            } else if (block instanceof TL_pageBlockDetailsChild) {
                TL_pageBlockDetailsChild pageBlockDetailsChild = (TL_pageBlockDetailsChild) block;
                block = pageBlockDetailsChild.block;
            }
            boolean z = false;
            switch (type) {
                case 0:
                    BlockParagraphCell cell = (BlockParagraphCell) holder.itemView;
                    cell.setBlock((TLRPC.TL_pageBlockParagraph) block);
                    return;
                case 1:
                    BlockHeaderCell cell2 = (BlockHeaderCell) holder.itemView;
                    cell2.setBlock((TLRPC.TL_pageBlockHeader) block);
                    return;
                case 2:
                    BlockDividerCell blockDividerCell = (BlockDividerCell) holder.itemView;
                    return;
                case 3:
                    BlockEmbedCell cell3 = (BlockEmbedCell) holder.itemView;
                    cell3.setBlock((TLRPC.TL_pageBlockEmbed) block);
                    return;
                case 4:
                    BlockSubtitleCell cell4 = (BlockSubtitleCell) holder.itemView;
                    cell4.setBlock((TLRPC.TL_pageBlockSubtitle) block);
                    return;
                case 5:
                    BlockVideoCell cell5 = (BlockVideoCell) holder.itemView;
                    TLRPC.TL_pageBlockVideo tL_pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                    boolean z2 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell5.setBlock(tL_pageBlockVideo, z2, z);
                    cell5.setParentBlock(this.channelBlock, block);
                    return;
                case 6:
                    BlockPullquoteCell cell6 = (BlockPullquoteCell) holder.itemView;
                    cell6.setBlock((TLRPC.TL_pageBlockPullquote) block);
                    return;
                case 7:
                    BlockBlockquoteCell cell7 = (BlockBlockquoteCell) holder.itemView;
                    cell7.setBlock((TLRPC.TL_pageBlockBlockquote) block);
                    return;
                case 8:
                    BlockSlideshowCell cell8 = (BlockSlideshowCell) holder.itemView;
                    cell8.setBlock((TLRPC.TL_pageBlockSlideshow) block);
                    return;
                case 9:
                    BlockPhotoCell cell9 = (BlockPhotoCell) holder.itemView;
                    TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                    boolean z3 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell9.setBlock(tL_pageBlockPhoto, z3, z);
                    cell9.setParentBlock(block);
                    return;
                case 10:
                    BlockAuthorDateCell cell10 = (BlockAuthorDateCell) holder.itemView;
                    cell10.setBlock((TLRPC.TL_pageBlockAuthorDate) block);
                    return;
                case 11:
                    BlockTitleCell cell11 = (BlockTitleCell) holder.itemView;
                    cell11.setBlock((TLRPC.TL_pageBlockTitle) block);
                    return;
                case 12:
                    BlockListItemCell cell12 = (BlockListItemCell) holder.itemView;
                    cell12.setBlock((TL_pageBlockListItem) block);
                    return;
                case 13:
                    BlockFooterCell cell13 = (BlockFooterCell) holder.itemView;
                    cell13.setBlock((TLRPC.TL_pageBlockFooter) block);
                    return;
                case 14:
                    BlockPreformattedCell cell14 = (BlockPreformattedCell) holder.itemView;
                    cell14.setBlock((TLRPC.TL_pageBlockPreformatted) block);
                    return;
                case 15:
                    BlockSubheaderCell cell15 = (BlockSubheaderCell) holder.itemView;
                    cell15.setBlock((TLRPC.TL_pageBlockSubheader) block);
                    return;
                case 16:
                    BlockEmbedPostCell cell16 = (BlockEmbedPostCell) holder.itemView;
                    cell16.setBlock((TLRPC.TL_pageBlockEmbedPost) block);
                    return;
                case 17:
                    BlockCollageCell cell17 = (BlockCollageCell) holder.itemView;
                    cell17.setBlock((TLRPC.TL_pageBlockCollage) block);
                    return;
                case 18:
                    BlockChannelCell cell18 = (BlockChannelCell) holder.itemView;
                    cell18.setBlock((TLRPC.TL_pageBlockChannel) block);
                    return;
                case 19:
                    BlockAudioCell cell19 = (BlockAudioCell) holder.itemView;
                    TLRPC.TL_pageBlockAudio tL_pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                    boolean z4 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell19.setBlock(tL_pageBlockAudio, z4, z);
                    return;
                case 20:
                    BlockKickerCell cell20 = (BlockKickerCell) holder.itemView;
                    cell20.setBlock((TLRPC.TL_pageBlockKicker) block);
                    return;
                case 21:
                    BlockOrderedListItemCell cell21 = (BlockOrderedListItemCell) holder.itemView;
                    cell21.setBlock((TL_pageBlockOrderedListItem) block);
                    return;
                case 22:
                    BlockMapCell cell22 = (BlockMapCell) holder.itemView;
                    TLRPC.TL_pageBlockMap tL_pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                    boolean z5 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell22.setBlock(tL_pageBlockMap, z5, z);
                    return;
                case 23:
                    BlockRelatedArticlesCell cell23 = (BlockRelatedArticlesCell) holder.itemView;
                    cell23.setBlock((TL_pageBlockRelatedArticlesChild) block);
                    return;
                case 24:
                    BlockDetailsCell cell24 = (BlockDetailsCell) holder.itemView;
                    cell24.setBlock((TLRPC.TL_pageBlockDetails) block);
                    return;
                case 25:
                    BlockTableCell cell25 = (BlockTableCell) holder.itemView;
                    cell25.setBlock((TLRPC.TL_pageBlockTable) block);
                    return;
                case 26:
                    BlockRelatedArticlesHeaderCell cell26 = (BlockRelatedArticlesHeaderCell) holder.itemView;
                    cell26.setBlock((TLRPC.TL_pageBlockRelatedArticles) block);
                    return;
                case 27:
                    BlockDetailsBottomCell blockDetailsBottomCell = (BlockDetailsBottomCell) holder.itemView;
                    return;
                case 100:
                    TextView textView = (TextView) holder.itemView;
                    textView.setText("unsupported block " + block);
                    return;
                default:
                    return;
            }
        }

        public int getTypeForBlock(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockParagraph) {
                return 0;
            }
            if (block instanceof TLRPC.TL_pageBlockHeader) {
                return 1;
            }
            if (block instanceof TLRPC.TL_pageBlockDivider) {
                return 2;
            }
            if (block instanceof TLRPC.TL_pageBlockEmbed) {
                return 3;
            }
            if (block instanceof TLRPC.TL_pageBlockSubtitle) {
                return 4;
            }
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                return 5;
            }
            if (block instanceof TLRPC.TL_pageBlockPullquote) {
                return 6;
            }
            if (block instanceof TLRPC.TL_pageBlockBlockquote) {
                return 7;
            }
            if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                return 8;
            }
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                return 9;
            }
            if (block instanceof TLRPC.TL_pageBlockAuthorDate) {
                return 10;
            }
            if (block instanceof TLRPC.TL_pageBlockTitle) {
                return 11;
            }
            if (block instanceof TL_pageBlockListItem) {
                return 12;
            }
            if (block instanceof TLRPC.TL_pageBlockFooter) {
                return 13;
            }
            if (block instanceof TLRPC.TL_pageBlockPreformatted) {
                return 14;
            }
            if (block instanceof TLRPC.TL_pageBlockSubheader) {
                return 15;
            }
            if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
                return 16;
            }
            if (block instanceof TLRPC.TL_pageBlockCollage) {
                return 17;
            }
            if (block instanceof TLRPC.TL_pageBlockChannel) {
                return 18;
            }
            if (block instanceof TLRPC.TL_pageBlockAudio) {
                return 19;
            }
            if (block instanceof TLRPC.TL_pageBlockKicker) {
                return 20;
            }
            if (block instanceof TL_pageBlockOrderedListItem) {
                return 21;
            }
            if (block instanceof TLRPC.TL_pageBlockMap) {
                return 22;
            }
            if (block instanceof TL_pageBlockRelatedArticlesChild) {
                return 23;
            }
            if (block instanceof TLRPC.TL_pageBlockDetails) {
                return 24;
            }
            if (block instanceof TLRPC.TL_pageBlockTable) {
                return 25;
            }
            if (block instanceof TLRPC.TL_pageBlockRelatedArticles) {
                return 26;
            }
            if (block instanceof TL_pageBlockDetailsBottom) {
                return 27;
            }
            if (block instanceof TL_pageBlockRelatedArticlesShadow) {
                return 28;
            }
            if (block instanceof TL_pageBlockDetailsChild) {
                TL_pageBlockDetailsChild pageBlockDetailsChild = (TL_pageBlockDetailsChild) block;
                return getTypeForBlock(pageBlockDetailsChild.block);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                TLRPC.TL_pageBlockCover pageBlockCover = (TLRPC.TL_pageBlockCover) block;
                return getTypeForBlock(pageBlockCover.cover);
            } else {
                return 100;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.localBlocks.size()) {
                return 90;
            }
            return getTypeForBlock(this.localBlocks.get(position));
        }

        public TLRPC.PageBlock getItem(int position) {
            return this.localBlocks.get(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            TLRPC.WebPage webPage = this.currentPage;
            if (webPage == null || webPage.cached_page == null) {
                return 0;
            }
            return this.localBlocks.size() + 1;
        }

        private boolean isBlockOpened(TL_pageBlockDetailsChild child) {
            TLRPC.PageBlock parentBlock = ArticleViewer.this.getLastNonListPageBlock(child.parent);
            if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
                return ((TLRPC.TL_pageBlockDetails) parentBlock).open;
            }
            if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            TLRPC.PageBlock parentBlock2 = ArticleViewer.this.getLastNonListPageBlock(parent.block);
            if ((parentBlock2 instanceof TLRPC.TL_pageBlockDetails) && !((TLRPC.TL_pageBlockDetails) parentBlock2).open) {
                return false;
            }
            return isBlockOpened(parent);
        }

        public void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int a = 0; a < size; a++) {
                TLRPC.PageBlock originalBlock = this.blocks.get(a);
                TLRPC.PageBlock block = ArticleViewer.this.getLastNonListPageBlock(originalBlock);
                if (block instanceof TL_pageBlockDetailsChild) {
                    TL_pageBlockDetailsChild pageBlockDetailsChild = (TL_pageBlockDetailsChild) block;
                    if (!isBlockOpened(pageBlockDetailsChild)) {
                    }
                }
                this.localBlocks.add(originalBlock);
            }
        }

        public void cleanup() {
            this.currentPage = null;
            this.blocks.clear();
            this.photoBlocks.clear();
            this.audioBlocks.clear();
            this.audioMessages.clear();
            this.anchors.clear();
            this.anchorsParent.clear();
            this.anchorsOffset.clear();
            this.textBlocks.clear();
            this.textToBlocks.clear();
            this.channelBlock = null;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position) {
            updateRows();
            super.notifyItemChanged(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position, Object payload) {
            updateRows();
            super.notifyItemChanged(position, payload);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int position) {
            updateRows();
            super.notifyItemInserted(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            super.notifyItemMoved(fromPosition, toPosition);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int position) {
            updateRows();
            super.notifyItemRemoved(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    /* loaded from: classes4.dex */
    public class BlockVideoCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, TextSelectionHelper.ArticleSelectableView {
        private int TAG;
        private boolean autoDownload;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private boolean cancelLoading;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockVideo currentBlock;
        private TLRPC.Document currentDocument;
        private int currentType;
        private MessageObject.GroupedMessagePosition groupPosition;
        private ImageReceiver imageView;
        private boolean isFirst;
        private boolean isGif;
        private WebpageAdapter parentAdapter;
        private TLRPC.PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockVideoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            ArticleViewer.this = r7;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setNeedsQualityThumb(true);
            this.imageView.setShouldGenerateQualityThumb(true);
            this.currentType = type;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(1711276032, Theme.ACTION_BAR_PHOTO_VIEWER_COLOR, -1, -2500135);
            this.TAG = DownloadController.getInstance(r7.currentAccount).generateObserverTag();
            BlockChannelCell blockChannelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            this.channelCell = blockChannelCell;
            addView(blockChannelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TLRPC.TL_pageBlockVideo block, boolean first, boolean last) {
            this.currentBlock = block;
            this.parentBlock = null;
            TLRPC.Document documentWithId = this.parentAdapter.getDocumentWithId(block.video_id);
            this.currentDocument = documentWithId;
            this.isGif = MessageObject.isVideoDocument(documentWithId) || MessageObject.isGifDocument(this.currentDocument);
            this.isFirst = first;
            this.channelCell.setVisibility(4);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC.TL_pageBlockChannel channelBlock, TLRPC.PageBlock block) {
            this.parentBlock = block;
            if (channelBlock != null && (block instanceof TLRPC.TL_pageBlockCover)) {
                this.channelCell.setBlock(channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARN: Code restructure failed: missing block: B:30:0x00a3, code lost:
            if (r2 <= (r3 + org.telegram.messenger.AndroidUtilities.dp(48.0f))) goto L33;
         */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            /*
                Method dump skipped, instructions count: 282
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockVideoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width;
            int height;
            int textWidth;
            int photoX;
            int height2;
            int i;
            int width2 = View.MeasureSpec.getSize(widthMeasureSpec);
            int height3 = 0;
            int i2 = this.currentType;
            if (i2 == 1) {
                int width3 = ((View) getParent()).getMeasuredWidth();
                height3 = ((View) getParent()).getMeasuredHeight();
                width = width3;
            } else if (i2 != 2) {
                width = width2;
            } else {
                height3 = (int) Math.ceil(this.groupPosition.ph * Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f);
                width = width2;
            }
            TLRPC.TL_pageBlockVideo tL_pageBlockVideo = this.currentBlock;
            if (tL_pageBlockVideo != null) {
                int photoWidth = width;
                int photoHeight = height3;
                if (this.currentType == 0 && tL_pageBlockVideo.level > 0) {
                    int dp = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0f);
                    photoX = dp;
                    this.textX = dp;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                } else {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                if (this.currentDocument == null) {
                    height = height3;
                } else {
                    int size = AndroidUtilities.dp(48.0f);
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 48);
                    int i3 = this.currentType;
                    if (i3 == 0) {
                        boolean found = false;
                        int a = 0;
                        int count = this.currentDocument.attributes.size();
                        while (true) {
                            if (a >= count) {
                                break;
                            }
                            TLRPC.DocumentAttribute attribute = this.currentDocument.attributes.get(a);
                            if (!(attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                a++;
                            } else {
                                float scale = photoWidth / attribute.w;
                                height3 = (int) (attribute.h * scale);
                                found = true;
                                break;
                            }
                        }
                        float w = thumb != null ? thumb.w : 100.0f;
                        float h = thumb != null ? thumb.h : 100.0f;
                        if (!found) {
                            float scale2 = photoWidth / w;
                            height3 = (int) (scale2 * h);
                        }
                        if (this.parentBlock instanceof TLRPC.TL_pageBlockCover) {
                            height3 = Math.min(height3, photoWidth);
                        } else {
                            int maxHeight = (int) ((Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(56.0f)) * 0.9f);
                            if (height3 > maxHeight) {
                                height3 = maxHeight;
                                float scale3 = height3 / h;
                                photoWidth = (int) (scale3 * w);
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        if (height3 == 0) {
                            height3 = AndroidUtilities.dp(100.0f);
                        } else if (height3 < size) {
                            height3 = size;
                        }
                        photoHeight = height3;
                    } else if (i3 == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.dp(2.0f);
                        }
                    }
                    this.imageView.setQualityThumbDocument(this.currentDocument);
                    this.imageView.setImageCoords(photoX, (this.isFirst || (i = this.currentType) == 1 || i == 2 || this.currentBlock.level > 0) ? 0.0f : AndroidUtilities.dp(8.0f), photoWidth, photoHeight);
                    if (this.isGif) {
                        this.autoDownload = DownloadController.getInstance(ArticleViewer.this.currentAccount).canDownloadMedia(4, this.currentDocument.size);
                        File path = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentDocument, true);
                        if (!this.autoDownload && !path.exists()) {
                            this.imageView.setStrippedLocation(ImageLocation.getForDocument(this.currentDocument));
                            this.imageView.setImage(null, null, null, null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", null, this.currentDocument.size, null, this.parentAdapter.currentPage, 1);
                        } else {
                            this.imageView.setStrippedLocation(null);
                            this.imageView.setImage(ImageLocation.getForDocument(this.currentDocument), ImageLoader.AUTOPLAY_FILTER, null, null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", null, this.currentDocument.size, null, this.parentAdapter.currentPage, 1);
                        }
                    } else {
                        this.imageView.setStrippedLocation(null);
                        this.imageView.setImage(null, null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", 0L, null, this.parentAdapter.currentPage, 1);
                    }
                    this.imageView.setAspectFit(true);
                    this.buttonX = (int) (this.imageView.getImageX() + ((this.imageView.getImageWidth() - size) / 2.0f));
                    int imageY = (int) (this.imageView.getImageY() + ((this.imageView.getImageHeight() - size) / 2.0f));
                    this.buttonY = imageY;
                    RadialProgress2 radialProgress2 = this.radialProgress;
                    int i4 = this.buttonX;
                    radialProgress2.setProgressRect(i4, imageY, i4 + size, imageY + size);
                    height = height3;
                }
                this.textY = (int) (this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0f));
                if (this.currentType == 0) {
                    DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                    this.captionLayout = createLayoutForText;
                    if (createLayoutForText != null) {
                        int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp2;
                        int height4 = height + dp2 + AndroidUtilities.dp(4.0f);
                        this.captionLayout.x = this.textX;
                        this.captionLayout.y = this.textY;
                        height2 = height4;
                    } else {
                        height2 = height;
                    }
                    DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = createLayoutForText2;
                    if (createLayoutForText2 != null) {
                        height = height2 + AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        this.creditLayout.x = this.textX;
                        this.creditLayout.y = this.textY + this.creditOffset;
                    } else {
                        height = height2;
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                boolean nextIsChannel = (this.parentBlock instanceof TLRPC.TL_pageBlockCover) && this.parentAdapter.blocks.size() > 1 && (this.parentAdapter.blocks.get(1) instanceof TLRPC.TL_pageBlockChannel);
                if (this.currentType != 2 && !nextIsChannel) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY(this.imageView.getImageHeight() - AndroidUtilities.dp(39.0f));
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            if (!this.imageView.hasBitmapImage() || this.imageView.getCurrentAlpha() != 1.0f) {
                canvas.drawRect(this.imageView.getDrawRegion(), ArticleViewer.photoBackgroundPaint);
            }
            if (!ArticleViewer.this.pinchToZoomHelper.isInOverlayModeFor(this)) {
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
            }
            int count = 0;
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 0) {
                return 2;
            }
            if (i == 1) {
                return 3;
            }
            if (i == 2) {
                return 8;
            }
            if (i == 3) {
                return 0;
            }
            return 4;
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            File path = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentDocument, true);
            boolean fileExists = path.exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                if (!this.isGif) {
                    this.buttonState = 3;
                } else {
                    this.buttonState = -1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, null, this);
                float setProgress = 0.0f;
                boolean progressVisible = false;
                if (!FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    if (!this.cancelLoading && this.autoDownload && this.isGif) {
                        progressVisible = true;
                        this.buttonState = 1;
                    } else {
                        this.buttonState = 0;
                    }
                } else {
                    progressVisible = true;
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), progressVisible, animated);
                this.radialProgress.setProgress(setProgress, false);
            }
            invalidate();
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (!this.isGif) {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, this.parentAdapter.currentPage, 1, 1);
                } else {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 40);
                    this.imageView.setImage(ImageLocation.getForDocument(this.currentDocument), null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", this.currentDocument.size, null, this.parentAdapter.currentPage, 1);
                }
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 1) {
                this.cancelLoading = true;
                if (!this.isGif) {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                } else {
                    this.imageView.cancelLoadImage();
                }
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            } else if (i == 2) {
                this.imageView.setAllowStartAnimation(true);
                this.imageView.startAnimation();
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else if (i == 3) {
                ArticleViewer.this.openPhoto(this.currentBlock, this.parentAdapter);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            if (this.isGif) {
                this.buttonState = 2;
                didPressedButton(true);
                return;
            }
            updateButtonState(true);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public int getObserverTag() {
            return this.TAG;
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachVideo", R.string.AttachVideo));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockAudioCell extends View implements DownloadController.FileDownloadProgressListener, TextSelectionHelper.ArticleSelectableView {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockAudio currentBlock;
        private TLRPC.Document currentDocument;
        private MessageObject currentMessageObject;
        private StaticLayout durationLayout;
        private boolean isFirst;
        private String lastTimeString;
        private WebpageAdapter parentAdapter;
        private RadialProgress2 radialProgress;
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private int textX;
        private int textY = AndroidUtilities.dp(58.0f);
        private DrawingText titleLayout;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockAudioCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r3;
            this.parentAdapter = adapter;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setCircleRadius(AndroidUtilities.dp(24.0f));
            this.TAG = DownloadController.getInstance(r3.currentAccount).generateObserverTag();
            SeekBar seekBar = new SeekBar(this);
            this.seekBar = seekBar;
            seekBar.setDelegate(new SeekBar.SeekBarDelegate() { // from class: org.telegram.ui.ArticleViewer$BlockAudioCell$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                @Override // org.telegram.ui.Components.SeekBar.SeekBarDelegate
                public final void onSeekBarDrag(float f) {
                    ArticleViewer.BlockAudioCell.this.m1559lambda$new$0$orgtelegramuiArticleViewer$BlockAudioCell(f);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ArticleViewer$BlockAudioCell */
        public /* synthetic */ void m1559lambda$new$0$orgtelegramuiArticleViewer$BlockAudioCell(float progress) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject == null) {
                return;
            }
            messageObject.audioProgress = progress;
            MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
        }

        public void setBlock(TLRPC.TL_pageBlockAudio block, boolean first, boolean last) {
            this.currentBlock = block;
            MessageObject messageObject = (MessageObject) this.parentAdapter.audioBlocks.get(this.currentBlock);
            this.currentMessageObject = messageObject;
            if (messageObject != null) {
                this.currentDocument = messageObject.getDocument();
            }
            this.isFirst = first;
            this.seekBar.setColors(Theme.getColor(Theme.key_chat_inAudioSeekbar), Theme.getColor(Theme.key_chat_inAudioCacheSeekbar), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarFill), Theme.getColor(Theme.key_chat_inAudioSeekbarSelected));
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        /* JADX WARN: Code restructure failed: missing block: B:20:0x0064, code lost:
            if (r1 <= (r4 + org.telegram.messenger.AndroidUtilities.dp(48.0f))) goto L23;
         */
        /* JADX WARN: Code restructure failed: missing block: B:22:0x0068, code lost:
            if (r13.buttonState == 0) goto L23;
         */
        /* JADX WARN: Code restructure failed: missing block: B:23:0x006a, code lost:
            r13.buttonPressed = 1;
            invalidate();
         */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r14) {
            /*
                r13 = this;
                float r0 = r14.getX()
                float r1 = r14.getY()
                org.telegram.ui.Components.SeekBar r2 = r13.seekBar
                int r3 = r14.getAction()
                float r4 = r14.getX()
                int r5 = r13.seekBarX
                float r5 = (float) r5
                float r4 = r4 - r5
                float r5 = r14.getY()
                int r6 = r13.seekBarY
                float r6 = (float) r6
                float r5 = r5 - r6
                boolean r2 = r2.onTouch(r3, r4, r5)
                r3 = 1
                if (r2 == 0) goto L36
                int r4 = r14.getAction()
                if (r4 != 0) goto L32
                android.view.ViewParent r4 = r13.getParent()
                r4.requestDisallowInterceptTouchEvent(r3)
            L32:
                r13.invalidate()
                return r3
            L36:
                int r4 = r14.getAction()
                r5 = 0
                if (r4 != 0) goto L70
                int r4 = r13.buttonState
                r6 = -1
                if (r4 == r6) goto L66
                int r4 = r13.buttonX
                float r6 = (float) r4
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 < 0) goto L66
                r6 = 1111490560(0x42400000, float:48.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r4 = r4 + r7
                float r4 = (float) r4
                int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r4 > 0) goto L66
                int r4 = r13.buttonY
                float r7 = (float) r4
                int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
                if (r7 < 0) goto L66
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r4 = r4 + r6
                float r4 = (float) r4
                int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r4 <= 0) goto L6a
            L66:
                int r4 = r13.buttonState
                if (r4 != 0) goto L8f
            L6a:
                r13.buttonPressed = r3
                r13.invalidate()
                goto L8f
            L70:
                int r4 = r14.getAction()
                if (r4 != r3) goto L86
                int r4 = r13.buttonPressed
                if (r4 != r3) goto L8f
                r13.buttonPressed = r5
                r13.playSoundEffect(r5)
                r13.didPressedButton(r3)
                r13.invalidate()
                goto L8f
            L86:
                int r4 = r14.getAction()
                r6 = 3
                if (r4 != r6) goto L8f
                r13.buttonPressed = r5
            L8f:
                int r4 = r13.buttonPressed
                if (r4 != 0) goto Lc3
                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r13.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r10 = r13.captionLayout
                int r11 = r13.textX
                int r12 = r13.textY
                r8 = r14
                r9 = r13
                boolean r4 = org.telegram.ui.ArticleViewer.access$8900(r6, r7, r8, r9, r10, r11, r12)
                if (r4 != 0) goto Lc3
                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r13.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r10 = r13.creditLayout
                int r11 = r13.textX
                int r4 = r13.textY
                int r8 = r13.creditOffset
                int r12 = r4 + r8
                r8 = r14
                r9 = r13
                boolean r4 = org.telegram.ui.ArticleViewer.access$8900(r6, r7, r8, r9, r10, r11, r12)
                if (r4 != 0) goto Lc3
                boolean r4 = super.onTouchEvent(r14)
                if (r4 == 0) goto Lc2
                goto Lc3
            Lc2:
                r3 = 0
            Lc3:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockAudioCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            SpannableStringBuilder stringBuilder;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height2 = AndroidUtilities.dp(54.0f);
            TLRPC.TL_pageBlockAudio tL_pageBlockAudio = this.currentBlock;
            if (tL_pageBlockAudio != null) {
                if (tL_pageBlockAudio.level > 0) {
                    this.textX = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(18.0f);
                }
                int textWidth = (width - this.textX) - AndroidUtilities.dp(18.0f);
                int size = AndroidUtilities.dp(44.0f);
                this.buttonX = AndroidUtilities.dp(16.0f);
                int dp = AndroidUtilities.dp(5.0f);
                this.buttonY = dp;
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i = this.buttonX;
                radialProgress2.setProgressRect(i, dp, i + size, dp + size);
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    int dp2 = AndroidUtilities.dp(8.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height = height2 + dp2 + AndroidUtilities.dp(8.0f);
                } else {
                    height = height2;
                }
                DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = createLayoutForText2;
                if (createLayoutForText2 != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                String author = this.currentMessageObject.getMusicAuthor(false);
                String title = this.currentMessageObject.getMusicTitle(false);
                int dp3 = this.buttonX + AndroidUtilities.dp(50.0f) + size;
                this.seekBarX = dp3;
                int w = (width - dp3) - AndroidUtilities.dp(18.0f);
                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(author)) {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2);
                } else {
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        stringBuilder = new SpannableStringBuilder(String.format("%s - %s", author, title));
                    } else if (!TextUtils.isEmpty(title)) {
                        stringBuilder = new SpannableStringBuilder(title);
                    } else {
                        stringBuilder = new SpannableStringBuilder(author);
                    }
                    if (!TextUtils.isEmpty(author)) {
                        TypefaceSpan span = new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        stringBuilder.setSpan(span, 0, author.length(), 18);
                    }
                    CharSequence stringFinal = TextUtils.ellipsize(stringBuilder, Theme.chat_audioTitlePaint, w, TextUtils.TruncateAt.END);
                    DrawingText drawingText = new DrawingText();
                    this.titleLayout = drawingText;
                    drawingText.textLayout = new StaticLayout(stringFinal, ArticleViewer.audioTimePaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleLayout.parentBlock = this.currentBlock;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2) + AndroidUtilities.dp(11.0f);
                }
                this.seekBar.setSize(w, AndroidUtilities.dp(30.0f));
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            updatePlayingMessageProgress();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            this.radialProgress.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
            this.radialProgress.setProgressColor(Theme.getColor(Theme.key_chat_inFileProgress));
            this.radialProgress.draw(canvas);
            canvas.save();
            canvas.translate(this.seekBarX, this.seekBarY);
            this.seekBar.draw(canvas);
            canvas.restore();
            int count = 0;
            if (this.durationLayout != null) {
                canvas.save();
                canvas.translate(this.buttonX + AndroidUtilities.dp(54.0f), this.seekBarY + AndroidUtilities.dp(6.0f));
                this.durationLayout.draw(canvas);
                canvas.restore();
            }
            if (this.titleLayout != null) {
                canvas.save();
                this.titleLayout.x = this.buttonX + AndroidUtilities.dp(54.0f);
                this.titleLayout.y = this.seekBarY - AndroidUtilities.dp(16.0f);
                canvas.translate(this.titleLayout.x, this.titleLayout.y);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.titleLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.captionLayout != null) {
                canvas.save();
                this.captionLayout.x = this.textX;
                this.captionLayout.y = this.textY;
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count++;
            }
            if (this.creditLayout != null) {
                canvas.save();
                this.creditLayout.x = this.textX;
                this.creditLayout.y = this.textY + this.creditOffset;
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
            if (i == 3) {
                return 3;
            }
            return 0;
        }

        public void updatePlayingMessageProgress() {
            if (this.currentDocument == null || this.currentMessageObject == null) {
                return;
            }
            if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
            }
            int duration = 0;
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                duration = this.currentMessageObject.audioProgressSec;
            } else {
                int a = 0;
                while (true) {
                    if (a >= this.currentDocument.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = this.currentDocument.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                        a++;
                    } else {
                        duration = attribute.duration;
                        break;
                    }
                }
            }
            String timeString = AndroidUtilities.formatShortDuration(duration);
            String str = this.lastTimeString;
            if (str == null || (str != null && !str.equals(timeString))) {
                this.lastTimeString = timeString;
                ArticleViewer.audioTimePaint.setTextSize(AndroidUtilities.dp(16.0f));
                int timeWidth = (int) Math.ceil(ArticleViewer.audioTimePaint.measureText(timeString));
                this.durationLayout = new StaticLayout(timeString, ArticleViewer.audioTimePaint, timeWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            ArticleViewer.audioTimePaint.setColor(ArticleViewer.this.getTextColor());
            invalidate();
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            File path = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentDocument, true);
            boolean fileExists = path.exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, null, this);
                if (!FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                } else {
                    this.buttonState = 3;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                if (MediaController.getInstance().setPlaylist(this.parentAdapter.audioMessages, this.currentMessageObject, 0L, false, null)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                }
            } else if (i == 1) {
                boolean result = MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject);
                if (result) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                }
            } else if (i == 2) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, this.parentAdapter.currentPage, 1, 1);
                this.buttonState = 3;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 3) {
                FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            }
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateButtonState(false);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(true);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
            if (this.buttonState != 3) {
                updateButtonState(true);
            }
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public int getObserverTag() {
            return this.TAG;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.captionLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
            DrawingText drawingText3 = this.creditLayout;
            if (drawingText3 != null) {
                blocks.add(drawingText3);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockEmbedPostCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private ImageReceiver avatarImageView;
        private boolean avatarVisible;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int dateX;
        private int lineHeight;
        private DrawingText nameLayout;
        private int nameX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockEmbedPostCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r5;
            this.parentAdapter = adapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.avatarImageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
        }

        public void setBlock(TLRPC.TL_pageBlockEmbedPost block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                if (tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption) {
                    this.textX = AndroidUtilities.dp(18.0f);
                    this.textY = AndroidUtilities.dp(4.0f);
                    int textWidth = width - AndroidUtilities.dp(50.0f);
                    DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                    this.captionLayout = createLayoutForText;
                    if (createLayoutForText != null) {
                        int dp = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp;
                        int height3 = 0 + dp + AndroidUtilities.dp(4.0f);
                        height = height3;
                    } else {
                        height = 0;
                    }
                    DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = createLayoutForText2;
                    if (createLayoutForText2 != null) {
                        height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                } else {
                    int i = 0;
                    boolean z = tL_pageBlockEmbedPost.author_photo_id != 0;
                    this.avatarVisible = z;
                    if (z) {
                        TLRPC.Photo photo = this.parentAdapter.getPhotoWithId(this.currentBlock.author_photo_id);
                        boolean z2 = photo instanceof TLRPC.TL_photo;
                        this.avatarVisible = z2;
                        if (z2) {
                            this.avatarDrawable.setInfo(0L, this.currentBlock.author, null);
                            TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(40.0f), true);
                            this.avatarImageView.setImage(ImageLocation.getForPhoto(image, photo), "40_40", this.avatarDrawable, 0L, (String) null, this.parentAdapter.currentPage, 1);
                        }
                    }
                    DrawingText createLayoutForText3 = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, null, width - AndroidUtilities.dp((this.avatarVisible ? 54 : 0) + 50), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                    this.nameLayout = createLayoutForText3;
                    if (createLayoutForText3 != null) {
                        createLayoutForText3.x = AndroidUtilities.dp((this.avatarVisible ? 54 : 0) + 32);
                        this.nameLayout.y = AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f);
                    }
                    if (this.currentBlock.date != 0) {
                        this.dateLayout = ArticleViewer.this.createLayoutForText(this, LocaleController.getInstance().chatFullDate.format(this.currentBlock.date * 1000), null, width - AndroidUtilities.dp((this.avatarVisible ? 54 : 0) + 50), AndroidUtilities.dp(29.0f), this.currentBlock, this.parentAdapter);
                    } else {
                        this.dateLayout = null;
                    }
                    int height4 = AndroidUtilities.dp(56.0f);
                    if (this.currentBlock.blocks.isEmpty()) {
                        this.textX = AndroidUtilities.dp(32.0f);
                        this.textY = AndroidUtilities.dp(56.0f);
                        int textWidth2 = width - AndroidUtilities.dp(50.0f);
                        DrawingText createLayoutForText4 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth2, this.textY, this.currentBlock, this.parentAdapter);
                        this.captionLayout = createLayoutForText4;
                        if (createLayoutForText4 != null) {
                            int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                            this.creditOffset = dp2;
                            height2 = height4 + dp2 + AndroidUtilities.dp(4.0f);
                        } else {
                            height2 = height4;
                        }
                        DrawingText createLayoutForText5 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth2, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                        this.creditLayout = createLayoutForText5;
                        if (createLayoutForText5 != null) {
                            height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        }
                        height = height2;
                    } else {
                        this.captionLayout = null;
                        this.creditLayout = null;
                        height = height4;
                    }
                    DrawingText drawingText = this.dateLayout;
                    if (drawingText != null) {
                        if (this.avatarVisible) {
                            i = 54;
                        }
                        drawingText.x = AndroidUtilities.dp(i + 32);
                        this.dateLayout.y = AndroidUtilities.dp(29.0f);
                    }
                    DrawingText drawingText2 = this.captionLayout;
                    if (drawingText2 != null) {
                        drawingText2.x = this.textX;
                        this.captionLayout.y = this.textY;
                    }
                    DrawingText drawingText3 = this.creditLayout;
                    if (drawingText3 != null) {
                        drawingText3.x = this.textX;
                        this.creditLayout.y = this.textY;
                    }
                }
                this.lineHeight = height;
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            TLRPC.TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost == null) {
                return;
            }
            int count = 0;
            if (!(tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption)) {
                if (this.avatarVisible) {
                    this.avatarImageView.draw(canvas);
                }
                int i = 54;
                int i2 = 0;
                if (this.nameLayout != null) {
                    canvas.save();
                    canvas.translate(AndroidUtilities.dp((this.avatarVisible ? 54 : 0) + 32), AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f));
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.nameLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.dateLayout != null) {
                    canvas.save();
                    if (!this.avatarVisible) {
                        i = 0;
                    }
                    canvas.translate(AndroidUtilities.dp(i + 32), AndroidUtilities.dp(29.0f));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.dateLayout.draw(canvas, this);
                    canvas.restore();
                    count++;
                }
                float dp = AndroidUtilities.dp(18.0f);
                float dp2 = AndroidUtilities.dp(6.0f);
                float dp3 = AndroidUtilities.dp(20.0f);
                int i3 = this.lineHeight;
                if (this.currentBlock.level == 0) {
                    i2 = AndroidUtilities.dp(6.0f);
                }
                canvas.drawRect(dp, dp2, dp3, i3 - i2, ArticleViewer.quoteLinePaint);
            }
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count++;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.nameLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.dateLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
            DrawingText drawingText3 = this.captionLayout;
            if (drawingText3 != null) {
                blocks.add(drawingText3);
            }
            DrawingText drawingText4 = this.creditLayout;
            if (drawingText4 != null) {
                blocks.add(drawingText4);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockParagraphCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        public DrawingText textLayout;
        public int textX;
        public int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockParagraphCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = this$0;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockParagraph block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph = this.currentBlock;
            if (tL_pageBlockParagraph != null) {
                if (tL_pageBlockParagraph.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((this.currentBlock.level * 14) + 18);
                }
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    int height2 = createLayoutForText.getHeight();
                    if (this.currentBlock.level > 0) {
                        height = height2 + AndroidUtilities.dp(8.0f);
                    } else {
                        height = height2 + AndroidUtilities.dp(16.0f);
                    }
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText == null) {
                return;
            }
            info.setText(drawingText.getText());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockEmbedCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockEmbed currentBlock;
        private int exactWebViewHeight;
        private int listX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;
        private WebPlayerView videoView;
        private boolean wasUserInteraction;
        private TouchyWebView webView;

        /* loaded from: classes4.dex */
        public class TelegramWebviewProxy {
            private TelegramWebviewProxy() {
                BlockEmbedCell.this = r1;
            }

            @JavascriptInterface
            public void postEvent(final String eventName, final String eventData) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ArticleViewer.BlockEmbedCell.TelegramWebviewProxy.this.m1562x9281b05f(eventName, eventData);
                    }
                });
            }

            /* renamed from: lambda$postEvent$0$org-telegram-ui-ArticleViewer$BlockEmbedCell$TelegramWebviewProxy */
            public /* synthetic */ void m1562x9281b05f(String eventName, String eventData) {
                if ("resize_frame".equals(eventName)) {
                    try {
                        JSONObject object = new JSONObject(eventData);
                        BlockEmbedCell.this.exactWebViewHeight = Utilities.parseInt((CharSequence) object.getString("height")).intValue();
                        BlockEmbedCell.this.requestLayout();
                    } catch (Throwable th) {
                    }
                }
            }
        }

        /* loaded from: classes4.dex */
        public class TouchyWebView extends WebView {
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public TouchyWebView(Context context) {
                super(context);
                BlockEmbedCell.this = this$1;
                setFocusable(false);
            }

            @Override // android.webkit.WebView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                BlockEmbedCell.this.wasUserInteraction = true;
                if (BlockEmbedCell.this.currentBlock != null) {
                    if (!BlockEmbedCell.this.currentBlock.allow_scrolling) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    } else {
                        requestDisallowInterceptTouchEvent(true);
                    }
                }
                return super.onTouchEvent(event);
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockEmbedCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r6;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            WebPlayerView webPlayerView = new WebPlayerView(context, false, false, new WebPlayerView.WebPlayerViewDelegate() { // from class: org.telegram.ui.ArticleViewer.BlockEmbedCell.1
                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public void onInitFailed() {
                    BlockEmbedCell.this.webView.setVisibility(0);
                    BlockEmbedCell.this.videoView.setVisibility(4);
                    BlockEmbedCell.this.videoView.loadVideo(null, null, null, null, false);
                    HashMap<String, String> args = new HashMap<>();
                    args.put("Referer", ApplicationLoader.applicationContext.getPackageName());
                    BlockEmbedCell.this.webView.loadUrl(BlockEmbedCell.this.currentBlock.url, args);
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public void onVideoSizeChanged(float aspectRatio, int rotation) {
                    ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(aspectRatio, rotation);
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public void onInlineSurfaceTextureReady() {
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public TextureView onSwitchToFullscreen(View controlsView, boolean fullscreen, float aspectRatio, int rotation, boolean byButton) {
                    if (fullscreen) {
                        ArticleViewer.this.fullscreenAspectRatioView.addView(ArticleViewer.this.fullscreenTextureView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(0);
                        ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(aspectRatio, rotation);
                        ArticleViewer.this.fullscreenedVideo = BlockEmbedCell.this.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        ArticleViewer.this.fullscreenedVideo = null;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public void prepareToSwitchInlineMode(boolean inline, Runnable switchInlineModeRunnable, float aspectRatio, boolean animated) {
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public TextureView onSwitchInlineMode(View controlsView, boolean inline, int videoWidth, int videoHeight, int rotation, boolean animated) {
                    return null;
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, false));
                    }
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                    if (playing) {
                        if (ArticleViewer.this.currentPlayingVideo != null && ArticleViewer.this.currentPlayingVideo != playerView) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        ArticleViewer.this.currentPlayingVideo = playerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                            return;
                        } catch (Exception e) {
                            FileLog.e(e);
                            return;
                        }
                    }
                    if (ArticleViewer.this.currentPlayingVideo == playerView) {
                        ArticleViewer.this.currentPlayingVideo = null;
                    }
                    try {
                        ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public boolean checkInlinePermissions() {
                    return false;
                }

                @Override // org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate
                public ViewGroup getTextureViewContainer() {
                    return null;
                }
            });
            this.videoView = webPlayerView;
            addView(webPlayerView);
            r6.createdWebViews.add(this);
            TouchyWebView touchyWebView = new TouchyWebView(context);
            this.webView = touchyWebView;
            touchyWebView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setDomStorageEnabled(true);
            this.webView.getSettings().setAllowContentAccess(true);
            if (Build.VERSION.SDK_INT >= 17) {
                this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new AnonymousClass2(r6));
            this.webView.setWebViewClient(new WebViewClient() { // from class: org.telegram.ui.ArticleViewer.BlockEmbedCell.3
                @Override // android.webkit.WebViewClient
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                @Override // android.webkit.WebViewClient
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                @Override // android.webkit.WebViewClient
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (BlockEmbedCell.this.wasUserInteraction) {
                        Browser.openUrl(ArticleViewer.this.parentActivity, url);
                        return true;
                    }
                    return false;
                }
            });
            addView(this.webView);
        }

        /* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedCell$2 */
        /* loaded from: classes4.dex */
        public class AnonymousClass2 extends WebChromeClient {
            final /* synthetic */ ArticleViewer val$this$0;

            AnonymousClass2(ArticleViewer articleViewer) {
                BlockEmbedCell.this = this$1;
                this.val$this$0 = articleViewer;
            }

            @Override // android.webkit.WebChromeClient
            public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
                onShowCustomView(view, callback);
            }

            @Override // android.webkit.WebChromeClient
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                if (ArticleViewer.this.customView == null) {
                    ArticleViewer.this.customView = view;
                    ArticleViewer.this.customViewCallback = callback;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ArticleViewer$BlockEmbedCell$2$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ArticleViewer.BlockEmbedCell.AnonymousClass2.this.m1561x5355f817();
                        }
                    }, 100L);
                    return;
                }
                callback.onCustomViewHidden();
            }

            /* renamed from: lambda$onShowCustomView$0$org-telegram-ui-ArticleViewer$BlockEmbedCell$2 */
            public /* synthetic */ void m1561x5355f817() {
                if (ArticleViewer.this.customView != null) {
                    ArticleViewer.this.fullscreenVideoContainer.addView(ArticleViewer.this.customView, LayoutHelper.createFrame(-1, -1.0f));
                    ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                }
            }

            @Override // android.webkit.WebChromeClient
            public void onHideCustomView() {
                super.onHideCustomView();
                if (ArticleViewer.this.customView != null) {
                    ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    ArticleViewer.this.fullscreenVideoContainer.removeView(ArticleViewer.this.customView);
                    if (ArticleViewer.this.customViewCallback != null && !ArticleViewer.this.customViewCallback.getClass().getName().contains(".chromium.")) {
                        ArticleViewer.this.customViewCallback.onCustomViewHidden();
                    }
                    ArticleViewer.this.customView = null;
                }
            }
        }

        public void destroyWebView(boolean completely) {
            try {
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                if (completely) {
                    this.webView.destroy();
                }
                this.currentBlock = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TLRPC.TL_pageBlockEmbed block) {
            TLRPC.TL_pageBlockEmbed previousBlock = this.currentBlock;
            this.currentBlock = block;
            this.webView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            TLRPC.TL_pageBlockEmbed tL_pageBlockEmbed = this.currentBlock;
            if (previousBlock != tL_pageBlockEmbed) {
                this.wasUserInteraction = false;
                if (!tL_pageBlockEmbed.allow_scrolling) {
                    this.webView.setVerticalScrollBarEnabled(false);
                    this.webView.setHorizontalScrollBarEnabled(false);
                } else {
                    this.webView.setVerticalScrollBarEnabled(true);
                    this.webView.setHorizontalScrollBarEnabled(true);
                }
                this.exactWebViewHeight = 0;
                try {
                    this.webView.loadUrl("about:blank");
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", this.currentBlock.html, "text/html", "UTF-8", null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo(null, null, null, null, false);
                        this.webView.setVisibility(0);
                    } else {
                        TLRPC.Photo thumb = this.currentBlock.poster_photo_id != 0 ? this.parentAdapter.getPhotoWithId(this.currentBlock.poster_photo_id) : null;
                        boolean handled = this.videoView.loadVideo(block.url, thumb, this.parentAdapter.currentPage, null, false);
                        if (!handled) {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo(null, null, null, null, false);
                            HashMap<String, String> args = new HashMap<>();
                            args.put("Referer", ApplicationLoader.applicationContext.getPackageName());
                            this.webView.loadUrl(this.currentBlock.url, args);
                        } else {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl("about:blank");
                        }
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            requestLayout();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (!ArticleViewer.this.isVisible) {
                this.currentBlock = null;
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int listWidth;
            int textWidth;
            float scale;
            int height2;
            int height3;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockEmbed tL_pageBlockEmbed = this.currentBlock;
            if (tL_pageBlockEmbed != null) {
                if (tL_pageBlockEmbed.level > 0) {
                    int dp = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    int listWidth2 = width - (dp + AndroidUtilities.dp(18.0f));
                    textWidth = listWidth2;
                    listWidth = listWidth2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    int textWidth2 = width - AndroidUtilities.dp(36.0f);
                    if (!this.currentBlock.full_width) {
                        int listWidth3 = width - AndroidUtilities.dp(36.0f);
                        this.listX += AndroidUtilities.dp(18.0f);
                        listWidth = listWidth3;
                        textWidth = textWidth2;
                    } else {
                        listWidth = width;
                        textWidth = textWidth2;
                    }
                }
                if (this.currentBlock.w == 0) {
                    scale = 1.0f;
                } else {
                    float scale2 = width;
                    scale = scale2 / this.currentBlock.w;
                }
                int i = this.exactWebViewHeight;
                if (i != 0) {
                    height2 = AndroidUtilities.dp(i);
                } else {
                    height2 = (int) ((this.currentBlock.w == 0 ? AndroidUtilities.dp(this.currentBlock.h) : this.currentBlock.h) * scale);
                }
                if (height2 != 0) {
                    height3 = height2;
                } else {
                    int height4 = AndroidUtilities.dp(10.0f);
                    height3 = height4;
                }
                this.webView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height3, C.BUFFER_FLAG_ENCRYPTED));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f) + height3, C.BUFFER_FLAG_ENCRYPTED));
                }
                this.textY = AndroidUtilities.dp(8.0f) + height3;
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height3 += dp2 + AndroidUtilities.dp(4.0f);
                } else {
                    this.creditOffset = 0;
                }
                DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = createLayoutForText2;
                if (createLayoutForText2 != null) {
                    height3 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.creditOffset;
                }
                height = height3 + AndroidUtilities.dp(5.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.captionLayout != null) {
                    height += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.captionLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.captionLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            TouchyWebView touchyWebView = this.webView;
            int i = this.listX;
            touchyWebView.layout(i, 0, touchyWebView.getMeasuredWidth() + i, this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                WebPlayerView webPlayerView = this.videoView;
                int i2 = this.listX;
                webPlayerView.layout(i2, 0, webPlayerView.getMeasuredWidth() + i2, this.videoView.getMeasuredHeight());
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int count = 0;
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockTableCell extends FrameLayout implements TableLayout.TableLayoutDelegate, TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockTable currentBlock;
        private boolean firstLayout;
        private int listX;
        private int listY;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        private TableLayout tableLayout;
        private int textX;
        private int textY;
        private DrawingText titleLayout;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockTableCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = this$0;
            this.parentAdapter = adapter;
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context) { // from class: org.telegram.ui.ArticleViewer.BlockTableCell.1
                @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    boolean intercept = super.onInterceptTouchEvent(ev);
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f) && intercept) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return intercept;
                }

                @Override // android.widget.HorizontalScrollView, android.view.View
                public boolean onTouchEvent(MotionEvent ev) {
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() <= getMeasuredWidth() - AndroidUtilities.dp(36.0f)) {
                        return false;
                    }
                    return super.onTouchEvent(ev);
                }

                @Override // android.view.View
                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    BlockTableCell.this.updateChildTextPositions();
                    if (ArticleViewer.this.textSelectionHelper != null && ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                        ArticleViewer.this.textSelectionHelper.invalidate();
                    }
                }

                @Override // android.view.View
                protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
                    ArticleViewer.this.removePressedLink();
                    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
                }

                @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    BlockTableCell.this.tableLayout.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), 0), heightMeasureSpec);
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), BlockTableCell.this.tableLayout.getMeasuredHeight());
                }
            };
            this.scrollView = horizontalScrollView;
            horizontalScrollView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.scrollView.setClipToPadding(false);
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            TableLayout tableLayout = new TableLayout(context, this, this$0.textSelectionHelper);
            this.tableLayout = tableLayout;
            tableLayout.setOrientation(0);
            this.tableLayout.setRowOrderPreserved(true);
            this.scrollView.addView(this.tableLayout, new FrameLayout.LayoutParams(-2, -2));
            setWillNotDraw(false);
        }

        @Override // org.telegram.ui.Components.TableLayout.TableLayoutDelegate
        public DrawingText createTextLayout(TLRPC.TL_pageTableCell cell, int maxWidth) {
            Layout.Alignment alignment;
            if (cell == null) {
                return null;
            }
            if (cell.align_right) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else if (cell.align_center) {
                alignment = Layout.Alignment.ALIGN_CENTER;
            } else {
                alignment = Layout.Alignment.ALIGN_NORMAL;
            }
            return ArticleViewer.this.createLayoutForText(this, null, cell.text, maxWidth, -1, this.currentBlock, alignment, 0, this.parentAdapter);
        }

        @Override // org.telegram.ui.Components.TableLayout.TableLayoutDelegate
        public Paint getLinePaint() {
            return ArticleViewer.tableLinePaint;
        }

        @Override // org.telegram.ui.Components.TableLayout.TableLayoutDelegate
        public Paint getHalfLinePaint() {
            return ArticleViewer.tableHalfLinePaint;
        }

        @Override // org.telegram.ui.Components.TableLayout.TableLayoutDelegate
        public Paint getHeaderPaint() {
            return ArticleViewer.tableHeaderPaint;
        }

        @Override // org.telegram.ui.Components.TableLayout.TableLayoutDelegate
        public Paint getStripPaint() {
            return ArticleViewer.tableStripPaint;
        }

        @Override // org.telegram.ui.Components.TableLayout.TableLayoutDelegate
        public void onLayoutChild(DrawingText text, int x, int y) {
            if (text != null && !ArticleViewer.this.searchResults.isEmpty() && ArticleViewer.this.searchText != null) {
                String lowerString = text.textLayout.getText().toString().toLowerCase();
                int startIndex = 0;
                while (true) {
                    int index = lowerString.indexOf(ArticleViewer.this.searchText, startIndex);
                    if (index >= 0) {
                        startIndex = index + ArticleViewer.this.searchText.length();
                        if (index == 0 || AndroidUtilities.isPunctuationCharacter(lowerString.charAt(index - 1))) {
                            HashMap hashMap = ArticleViewer.this.adapter[0].searchTextOffset;
                            hashMap.put(ArticleViewer.this.searchText + this.currentBlock + text.parentText + index, Integer.valueOf(text.textLayout.getLineTop(text.textLayout.getLineForOffset(index)) + y));
                        }
                    } else {
                        return;
                    }
                }
            }
        }

        public void setBlock(TLRPC.TL_pageBlockTable block) {
            this.currentBlock = block;
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(this.parentAdapter.isRtl);
            int maxCols = 0;
            if (!this.currentBlock.rows.isEmpty()) {
                TLRPC.TL_pageTableRow row = this.currentBlock.rows.get(0);
                int size2 = row.cells.size();
                for (int c = 0; c < size2; c++) {
                    TLRPC.TL_pageTableCell cell = row.cells.get(c);
                    maxCols += cell.colspan != 0 ? cell.colspan : 1;
                }
            }
            int size = this.currentBlock.rows.size();
            for (int r = 0; r < size; r++) {
                TLRPC.TL_pageTableRow row2 = this.currentBlock.rows.get(r);
                int cols = 0;
                int size22 = row2.cells.size();
                for (int c2 = 0; c2 < size22; c2++) {
                    TLRPC.TL_pageTableCell cell2 = row2.cells.get(c2);
                    int colspan = cell2.colspan != 0 ? cell2.colspan : 1;
                    int rowspan = cell2.rowspan != 0 ? cell2.rowspan : 1;
                    if (cell2.text != null) {
                        this.tableLayout.addChild(cell2, cols, r, colspan);
                    } else {
                        this.tableLayout.addChild(cols, r, colspan, rowspan);
                    }
                    cols += colspan;
                }
            }
            this.tableLayout.setColumnCount(maxCols);
            this.firstLayout = true;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, c.textLayout, (this.scrollView.getPaddingLeft() - this.scrollView.getScrollX()) + this.listX + c.getTextX(), this.listY + c.getTextY())) {
                    return true;
                }
            }
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.titleLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
        public void invalidate() {
            super.invalidate();
            this.tableLayout.invalidate();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int textWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height2 = 0;
            TLRPC.TL_pageBlockTable tL_pageBlockTable = this.currentBlock;
            if (tL_pageBlockTable != null) {
                if (tL_pageBlockTable.level > 0) {
                    int dp = AndroidUtilities.dp(this.currentBlock.level * 14);
                    this.listX = dp;
                    int dp2 = dp + AndroidUtilities.dp(18.0f);
                    this.textX = dp2;
                    textWidth = width - dp2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.title, textWidth, 0, this.currentBlock, Layout.Alignment.ALIGN_CENTER, 0, this.parentAdapter);
                this.titleLayout = createLayoutForText;
                if (createLayoutForText == null) {
                    this.listY = AndroidUtilities.dp(8.0f);
                } else {
                    this.textY = 0;
                    height2 = 0 + createLayoutForText.getHeight() + AndroidUtilities.dp(8.0f);
                    this.listY = height2;
                    this.titleLayout.x = this.textX;
                    this.titleLayout.y = this.textY;
                }
                this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width - this.listX, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                height = height2 + this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            updateChildTextPositions();
        }

        public void updateChildTextPositions() {
            int count = this.titleLayout == null ? 0 : 1;
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (c.textLayout != null) {
                    c.textLayout.x = ((c.getTextX() + this.listX) + AndroidUtilities.dp(18.0f)) - this.scrollView.getScrollX();
                    c.textLayout.y = c.getTextY() + this.listY;
                    c.textLayout.row = c.getRow();
                    c.setSelectionIndex(count);
                    count++;
                }
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            HorizontalScrollView horizontalScrollView = this.scrollView;
            int i = this.listX;
            horizontalScrollView.layout(i, this.listY, horizontalScrollView.getMeasuredWidth() + i, this.listY + this.scrollView.getMeasuredHeight());
            if (this.firstLayout) {
                if (this.parentAdapter.isRtl) {
                    this.scrollView.setScrollX((this.tableLayout.getMeasuredWidth() - this.scrollView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
                } else {
                    this.scrollView.setScrollX(0);
                }
                this.firstLayout = false;
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int i = 0;
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.titleLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                float dp = AndroidUtilities.dp(18.0f);
                float dp2 = AndroidUtilities.dp(20.0f);
                int measuredHeight = getMeasuredHeight();
                if (this.currentBlock.bottom) {
                    i = AndroidUtilities.dp(6.0f);
                }
                canvas.drawRect(dp, 0.0f, dp2, measuredHeight - i, ArticleViewer.quoteLinePaint);
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (c.textLayout != null) {
                    blocks.add(c.textLayout);
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockCollageCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockCollage currentBlock;
        private GroupedMessages group = new GroupedMessages();
        private boolean inLayout;
        private RecyclerView.Adapter innerAdapter;
        private RecyclerListView innerListView;
        private int listX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        /* loaded from: classes4.dex */
        public class GroupedMessages {
            public long groupId;
            public boolean hasSibling;
            public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
            public HashMap<TLObject, MessageObject.GroupedMessagePosition> positions = new HashMap<>();
            private int maxSizeWidth = 1000;

            public GroupedMessages() {
                BlockCollageCell.this = this$1;
            }

            /* loaded from: classes4.dex */
            public class MessageGroupedLayoutAttempt {
                public float[] heights;
                public int[] lineCounts;

                public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                    GroupedMessages.this = r4;
                    this.lineCounts = new int[]{i1, i2};
                    this.heights = new float[]{f1, f2};
                }

                public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                    GroupedMessages.this = r5;
                    this.lineCounts = new int[]{i1, i2, i3};
                    this.heights = new float[]{f1, f2, f3};
                }

                public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                    GroupedMessages.this = r6;
                    this.lineCounts = new int[]{i1, i2, i3, i4};
                    this.heights = new float[]{f1, f2, f3, f4};
                }
            }

            private float multiHeight(float[] array, int start, int end) {
                float sum = 0.0f;
                for (int a = start; a < end; a++) {
                    sum += array[a];
                }
                int a2 = this.maxSizeWidth;
                return a2 / sum;
            }

            /* JADX WARN: Code restructure failed: missing block: B:160:0x07a7, code lost:
                if (r4.lineCounts[2] > r4.lineCounts[3]) goto L163;
             */
            /* JADX WARN: Removed duplicated region for block: B:197:0x084e  */
            /* JADX WARN: Removed duplicated region for block: B:20:0x008d  */
            /* JADX WARN: Removed duplicated region for block: B:21:0x008f  */
            /* JADX WARN: Removed duplicated region for block: B:235:0x0863 A[SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:24:0x0094  */
            /* JADX WARN: Removed duplicated region for block: B:25:0x0097  */
            /* JADX WARN: Removed duplicated region for block: B:28:0x00a6  */
            /* JADX WARN: Removed duplicated region for block: B:29:0x00ac  */
            /* JADX WARN: Removed duplicated region for block: B:35:0x00c9  */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void calculate() {
                /*
                    Method dump skipped, instructions count: 2148
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void");
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockCollageCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r10;
            this.parentAdapter = adapter;
            RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ArticleViewer.BlockCollageCell.1
                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (BlockCollageCell.this.inLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.innerListView = recyclerListView;
            recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.ArticleViewer.BlockCollageCell.2
                @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    MessageObject.GroupedMessagePosition position;
                    outRect.bottom = 0;
                    if (!(view instanceof BlockPhotoCell)) {
                        if (view instanceof BlockVideoCell) {
                            position = BlockCollageCell.this.group.positions.get(((BlockVideoCell) view).currentBlock);
                        } else {
                            position = null;
                        }
                    } else {
                        position = BlockCollageCell.this.group.positions.get(((BlockPhotoCell) view).currentBlock);
                    }
                    if (position != null && position.siblingHeights != null) {
                        float maxHeight = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                        int h = 0;
                        for (int a = 0; a < position.siblingHeights.length; a++) {
                            h += (int) Math.ceil(position.siblingHeights[a] * maxHeight);
                        }
                        int a2 = position.maxY;
                        int h2 = h + ((a2 - position.minY) * AndroidUtilities.dp2(11.0f));
                        int count = BlockCollageCell.this.group.posArray.size();
                        int a3 = 0;
                        while (true) {
                            if (a3 >= count) {
                                break;
                            }
                            MessageObject.GroupedMessagePosition pos = BlockCollageCell.this.group.posArray.get(a3);
                            if (pos.minY != position.minY || ((pos.minX == position.minX && pos.maxX == position.maxX && pos.minY == position.minY && pos.maxY == position.maxY) || pos.minY != position.minY)) {
                                a3++;
                            } else {
                                h2 -= ((int) Math.ceil(pos.ph * maxHeight)) - AndroidUtilities.dp(4.0f);
                                break;
                            }
                        }
                        int a4 = -h2;
                        outRect.bottom = a4;
                    }
                }
            });
            GridLayoutManager gridLayoutManager = new GridLayoutManagerFixed(context, 1000, 1, true) { // from class: org.telegram.ui.ArticleViewer.BlockCollageCell.3
                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManagerFixed
                public boolean shouldLayoutChildFromOpositeSide(View child) {
                    return false;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManagerFixed
                protected boolean hasSiblingChild(int position) {
                    TLObject message = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    MessageObject.GroupedMessagePosition pos = BlockCollageCell.this.group.positions.get(message);
                    if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == 0) {
                        return false;
                    }
                    int count = BlockCollageCell.this.group.posArray.size();
                    for (int a = 0; a < count; a++) {
                        MessageObject.GroupedMessagePosition p = BlockCollageCell.this.group.posArray.get(a);
                        if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.ArticleViewer.BlockCollageCell.4
                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int position) {
                    TLObject message = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    return BlockCollageCell.this.group.positions.get(message).spanSize;
                }
            });
            this.innerListView.setLayoutManager(gridLayoutManager);
            RecyclerListView recyclerListView2 = this.innerListView;
            RecyclerView.Adapter adapter2 = new RecyclerView.Adapter() { // from class: org.telegram.ui.ArticleViewer.BlockCollageCell.5
                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    switch (viewType) {
                        case 0:
                            view = new BlockPhotoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                            break;
                        default:
                            view = new BlockVideoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                            break;
                    }
                    return new RecyclerListView.Holder(view);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    TLRPC.PageBlock pageBlock = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    switch (holder.getItemViewType()) {
                        case 0:
                            BlockPhotoCell cell = (BlockPhotoCell) holder.itemView;
                            cell.groupPosition = BlockCollageCell.this.group.positions.get(pageBlock);
                            cell.setBlock((TLRPC.TL_pageBlockPhoto) pageBlock, true, true);
                            return;
                        default:
                            BlockVideoCell cell2 = (BlockVideoCell) holder.itemView;
                            cell2.groupPosition = BlockCollageCell.this.group.positions.get(pageBlock);
                            cell2.setBlock((TLRPC.TL_pageBlockVideo) pageBlock, true, true);
                            return;
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public int getItemCount() {
                    if (BlockCollageCell.this.currentBlock != null) {
                        return BlockCollageCell.this.currentBlock.items.size();
                    }
                    return 0;
                }

                @Override // androidx.recyclerview.widget.RecyclerView.Adapter
                public int getItemViewType(int position) {
                    TLRPC.PageBlock block = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    return block instanceof TLRPC.TL_pageBlockPhoto ? 0 : 1;
                }
            };
            this.innerAdapter = adapter2;
            recyclerListView2.setAdapter(adapter2);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockCollage block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                this.group.calculate();
            }
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setGlowColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int listWidth;
            int textWidth;
            int height2;
            this.inLayout = true;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockCollage tL_pageBlockCollage = this.currentBlock;
            if (tL_pageBlockCollage != null) {
                if (tL_pageBlockCollage.level > 0) {
                    int dp = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    int listWidth2 = width - (dp + AndroidUtilities.dp(18.0f));
                    textWidth = listWidth2;
                    listWidth = listWidth2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                    listWidth = width;
                }
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                int height3 = this.innerListView.getMeasuredHeight();
                this.textY = AndroidUtilities.dp(8.0f) + height3;
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    int height4 = height3 + dp2 + AndroidUtilities.dp(4.0f);
                    this.captionLayout.x = this.textX;
                    this.captionLayout.y = this.textY;
                    height2 = height4;
                } else {
                    this.creditOffset = 0;
                    height2 = height3;
                }
                DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = createLayoutForText2;
                if (createLayoutForText2 != null) {
                    height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.textY + this.creditOffset;
                }
                int height5 = height2 + AndroidUtilities.dp(16.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height5 += AndroidUtilities.dp(8.0f);
                }
                height = height5;
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(this.listX, AndroidUtilities.dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int count = 0;
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockSlideshowCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockSlideshow currentBlock;
        private int currentPage;
        private View dotsContainer;
        private PagerAdapter innerAdapter;
        private ViewPager innerListView;
        private float pageOffset;
        private WebpageAdapter parentAdapter;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockSlideshowCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r3;
            this.parentAdapter = adapter;
            if (ArticleViewer.dotsPaint == null) {
                Paint unused = ArticleViewer.dotsPaint = new Paint(1);
                ArticleViewer.dotsPaint.setColor(-1);
            }
            ViewPager viewPager = new ViewPager(context) { // from class: org.telegram.ui.ArticleViewer.BlockSlideshowCell.1
                @Override // androidx.viewpager.widget.ViewPager, android.view.View
                public boolean onTouchEvent(MotionEvent ev) {
                    return super.onTouchEvent(ev);
                }

                @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    ArticleViewer.this.cancelCheckLongPress();
                    return super.onInterceptTouchEvent(ev);
                }
            };
            this.innerListView = viewPager;
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.ArticleViewer.BlockSlideshowCell.2
                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    BlockSlideshowCell blockSlideshowCell;
                    float width = BlockSlideshowCell.this.innerListView.getMeasuredWidth();
                    if (width == 0.0f) {
                        return;
                    }
                    BlockSlideshowCell.this.pageOffset = (((position * width) + positionOffsetPixels) - (blockSlideshowCell.currentPage * width)) / width;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int position) {
                    BlockSlideshowCell.this.currentPage = position;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int state) {
                }
            });
            ViewPager viewPager2 = this.innerListView;
            PagerAdapter pagerAdapter = new PagerAdapter() { // from class: org.telegram.ui.ArticleViewer.BlockSlideshowCell.3

                /* renamed from: org.telegram.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer */
                /* loaded from: classes4.dex */
                class ObjectContainer {
                    private TLRPC.PageBlock block;
                    private View view;

                    ObjectContainer() {
                        AnonymousClass3.this = this$2;
                    }
                }

                @Override // androidx.viewpager.widget.PagerAdapter
                public int getCount() {
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        return BlockSlideshowCell.this.currentBlock.items.size();
                    }
                    return 0;
                }

                @Override // androidx.viewpager.widget.PagerAdapter
                public boolean isViewFromObject(View view, Object object) {
                    return ((ObjectContainer) object).view == view;
                }

                @Override // androidx.viewpager.widget.PagerAdapter
                public int getItemPosition(Object object) {
                    ObjectContainer objectContainer = (ObjectContainer) object;
                    if (BlockSlideshowCell.this.currentBlock.items.contains(objectContainer.block)) {
                        return -1;
                    }
                    return -2;
                }

                @Override // androidx.viewpager.widget.PagerAdapter
                public Object instantiateItem(ViewGroup container, int position) {
                    View view;
                    TLRPC.PageBlock block = BlockSlideshowCell.this.currentBlock.items.get(position);
                    if (block instanceof TLRPC.TL_pageBlockPhoto) {
                        view = new BlockPhotoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockPhotoCell) view).setBlock((TLRPC.TL_pageBlockPhoto) block, true, true);
                    } else {
                        view = new BlockVideoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockVideoCell) view).setBlock((TLRPC.TL_pageBlockVideo) block, true, true);
                    }
                    container.addView(view);
                    ObjectContainer objectContainer = new ObjectContainer();
                    objectContainer.view = view;
                    objectContainer.block = block;
                    return objectContainer;
                }

                @Override // androidx.viewpager.widget.PagerAdapter
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(((ObjectContainer) object).view);
                }

                @Override // androidx.viewpager.widget.PagerAdapter
                public void unregisterDataSetObserver(DataSetObserver observer) {
                    if (observer != null) {
                        super.unregisterDataSetObserver(observer);
                    }
                }
            };
            this.innerAdapter = pagerAdapter;
            viewPager2.setAdapter(pagerAdapter);
            AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, Theme.getColor(Theme.key_windowBackgroundWhite));
            addView(this.innerListView);
            View view = new View(context) { // from class: org.telegram.ui.ArticleViewer.BlockSlideshowCell.4
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    int xOffset;
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int count = BlockSlideshowCell.this.innerAdapter.getCount();
                        int totalWidth = (AndroidUtilities.dp(7.0f) * count) + ((count - 1) * AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(4.0f);
                        if (totalWidth < getMeasuredWidth()) {
                            xOffset = (getMeasuredWidth() - totalWidth) / 2;
                        } else {
                            xOffset = AndroidUtilities.dp(4.0f);
                            int size = AndroidUtilities.dp(13.0f);
                            int halfCount = ((getMeasuredWidth() - AndroidUtilities.dp(8.0f)) / 2) / size;
                            if (BlockSlideshowCell.this.currentPage != (count - halfCount) - 1 || BlockSlideshowCell.this.pageOffset >= 0.0f) {
                                if (BlockSlideshowCell.this.currentPage < (count - halfCount) - 1) {
                                    if (BlockSlideshowCell.this.currentPage > halfCount) {
                                        xOffset -= ((int) (BlockSlideshowCell.this.pageOffset * size)) + ((BlockSlideshowCell.this.currentPage - halfCount) * size);
                                    } else if (BlockSlideshowCell.this.currentPage == halfCount && BlockSlideshowCell.this.pageOffset > 0.0f) {
                                        xOffset -= (int) (BlockSlideshowCell.this.pageOffset * size);
                                    }
                                } else {
                                    xOffset -= ((count - (halfCount * 2)) - 1) * size;
                                }
                            } else {
                                xOffset -= ((int) (BlockSlideshowCell.this.pageOffset * size)) + (((count - (halfCount * 2)) - 1) * size);
                            }
                        }
                        int a = 0;
                        while (a < BlockSlideshowCell.this.currentBlock.items.size()) {
                            int cx = AndroidUtilities.dp(4.0f) + xOffset + (AndroidUtilities.dp(13.0f) * a);
                            Drawable drawable = BlockSlideshowCell.this.currentPage == a ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            drawable.setBounds(cx - AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(10.0f));
                            drawable.draw(canvas);
                            a++;
                        }
                    }
                }
            };
            this.dotsContainer = view;
            addView(view);
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockSlideshow block) {
            this.currentBlock = block;
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setCurrentItem(0, false);
            this.innerListView.forceLayout();
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                int height3 = AndroidUtilities.dp(310.0f);
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height3, C.BUFFER_FLAG_ENCRYPTED));
                this.currentBlock.items.size();
                this.dotsContainer.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), C.BUFFER_FLAG_ENCRYPTED));
                int textWidth = width - AndroidUtilities.dp(36.0f);
                this.textY = AndroidUtilities.dp(16.0f) + height3;
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    int dp = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp;
                    int height4 = height3 + dp + AndroidUtilities.dp(4.0f);
                    this.captionLayout.x = this.textX;
                    this.captionLayout.y = this.textY;
                    height2 = height4;
                } else {
                    this.creditOffset = 0;
                    height2 = height3;
                }
                DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = createLayoutForText2;
                if (createLayoutForText2 != null) {
                    height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.textY + this.creditOffset;
                }
                height = height2 + AndroidUtilities.dp(16.0f);
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            int y = this.innerListView.getBottom() - AndroidUtilities.dp(23.0f);
            View view = this.dotsContainer;
            view.layout(0, y, view.getMeasuredWidth(), this.dotsContainer.getMeasuredHeight() + y);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int count = 0;
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockListItemCell extends ViewGroup implements TextSelectionHelper.ArticleSelectableView {
        private RecyclerView.ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockListItem currentBlock;
        private int currentBlockType;
        private boolean drawDot;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int typeForBlock = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = typeForBlock;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, typeForBlock);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TL_pageBlockListItem tL_pageBlockListItem = this.currentBlock;
            if (tL_pageBlockListItem != null) {
                this.textLayout = null;
                this.textY = (tL_pageBlockListItem.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.numOffsetY = 0;
                if (this.currentBlock.parent.lastMaxNumCalcWidth != width || this.currentBlock.parent.lastFontSize != SharedConfig.ivFontSize) {
                    this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    this.currentBlock.parent.lastFontSize = SharedConfig.ivFontSize;
                    this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockListItem item = (TL_pageBlockListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, null, width - AndroidUtilities.dp(54.0f), this.textY, this.currentBlock, this.parentAdapter);
                            this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil(item.numLayout.getLineWidth(0)));
                        }
                    }
                    this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil(ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                this.drawDot = !this.currentBlock.parent.pageBlockList.ordered;
                if (this.parentAdapter.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                }
                int maxWidth2 = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (this.parentAdapter.isRtl) {
                    maxWidth = maxWidth2 - ((AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                } else {
                    maxWidth = maxWidth2;
                }
                if (this.currentBlock.textItem != null) {
                    DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.textItem, maxWidth, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.textLayout = createLayoutForText;
                    if (createLayoutForText != null && createLayoutForText.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            int ascent = this.textLayout.getLineAscent(0);
                            this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - ascent;
                        }
                        height = 0 + this.textLayout.getHeight() + AndroidUtilities.dp(8.0f);
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    RecyclerView.ViewHolder viewHolder = this.blockLayout;
                    if (viewHolder != null) {
                        if (viewHolder.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!this.parentAdapter.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if (!(this.blockLayout.itemView instanceof BlockHeaderCell) && !(this.blockLayout.itemView instanceof BlockSubheaderCell) && !(this.blockLayout.itemView instanceof BlockTitleCell) && !(this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                                this.blockX = 0;
                                this.blockY = 0;
                                this.textY = 0;
                                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                    height = 0 - AndroidUtilities.dp(10.0f);
                                }
                                maxWidth = width;
                                height -= AndroidUtilities.dp(8.0f);
                            } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                                maxWidth += AndroidUtilities.dp(36.0f);
                            }
                        } else {
                            if (!this.parentAdapter.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                        }
                        this.blockLayout.itemView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = (BlockParagraphCell) this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                int ascent2 = paragraphCell.textLayout.getLineAscent(0);
                                this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - ascent2;
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TLRPC.TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height -= AndroidUtilities.dp(10.0f);
                            }
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = ((this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2) - AndroidUtilities.dp(4.0f);
                            this.drawDot = false;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.dp(10.0f);
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                }
                RecyclerView.ViewHolder viewHolder2 = this.blockLayout;
                if (viewHolder2 != null && (viewHolder2.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                    ArticleViewer.this.textSelectionHelper.arrayList.clear();
                    ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(ArticleViewer.this.textSelectionHelper.arrayList);
                    Iterator<TextSelectionHelper.TextLayoutBlock> it = ArticleViewer.this.textSelectionHelper.arrayList.iterator();
                    while (it.hasNext()) {
                        TextSelectionHelper.TextLayoutBlock block = it.next();
                        if (block instanceof DrawingText) {
                            ((DrawingText) block).x += this.blockX;
                            ((DrawingText) block).y += this.blockY;
                        }
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i = this.blockX;
                view.layout(i, this.blockY, this.blockLayout.itemView.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int width = getMeasuredWidth();
            if (this.currentBlock.numLayout != null) {
                canvas.save();
                int i = 0;
                if (this.parentAdapter.isRtl) {
                    float dp = ((width - AndroidUtilities.dp(15.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                    int i2 = this.textY + this.numOffsetY;
                    if (this.drawDot) {
                        i = AndroidUtilities.dp(1.0f);
                    }
                    canvas.translate(dp, i2 - i);
                } else {
                    float dp2 = ((AndroidUtilities.dp(15.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil(this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                    int i3 = this.textY + this.numOffsetY;
                    if (this.drawDot) {
                        i = AndroidUtilities.dp(1.0f);
                    }
                    canvas.translate(dp2, i3 - i);
                }
                this.currentBlock.numLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
        public void invalidate() {
            super.invalidate();
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                viewHolder.itemView.invalidate();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText == null) {
                return;
            }
            info.setText(drawingText.getText());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null && (viewHolder.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(blocks);
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockOrderedListItemCell extends ViewGroup implements TextSelectionHelper.ArticleSelectableView {
        private RecyclerView.ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockOrderedListItem currentBlock;
        private int currentBlockType;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockOrderedListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockOrderedListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int typeForBlock = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = typeForBlock;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, typeForBlock);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = this.currentBlock;
            if (tL_pageBlockOrderedListItem != null) {
                this.textLayout = null;
                this.textY = (tL_pageBlockOrderedListItem.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.numOffsetY = 0;
                if (this.currentBlock.parent.lastMaxNumCalcWidth != width || this.currentBlock.parent.lastFontSize != SharedConfig.ivFontSize) {
                    this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    this.currentBlock.parent.lastFontSize = SharedConfig.ivFontSize;
                    this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockOrderedListItem item = (TL_pageBlockOrderedListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, null, width - AndroidUtilities.dp(54.0f), this.textY, this.currentBlock, this.parentAdapter);
                            this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil(item.numLayout.getLineWidth(0)));
                        }
                    }
                    this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil(ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                if (this.parentAdapter.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f));
                }
                this.verticalAlign = false;
                int maxWidth2 = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (this.parentAdapter.isRtl) {
                    maxWidth = maxWidth2 - ((AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f)));
                } else {
                    maxWidth = maxWidth2;
                }
                if (this.currentBlock.textItem != null) {
                    DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.textItem, maxWidth, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.textLayout = createLayoutForText;
                    if (createLayoutForText != null && createLayoutForText.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            int ascent = this.textLayout.getLineAscent(0);
                            this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - ascent;
                        }
                        height = 0 + this.textLayout.getHeight() + AndroidUtilities.dp(8.0f);
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    RecyclerView.ViewHolder viewHolder = this.blockLayout;
                    if (viewHolder != null) {
                        if (viewHolder.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!this.parentAdapter.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if (!(this.blockLayout.itemView instanceof BlockHeaderCell) && !(this.blockLayout.itemView instanceof BlockSubheaderCell) && !(this.blockLayout.itemView instanceof BlockTitleCell) && !(this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                                this.blockX = 0;
                                this.blockY = 0;
                                this.textY = 0;
                                maxWidth = width;
                                height = 0 - AndroidUtilities.dp(8.0f);
                            } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                                maxWidth += AndroidUtilities.dp(36.0f);
                            }
                        } else {
                            if (!this.parentAdapter.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                        }
                        this.blockLayout.itemView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = (BlockParagraphCell) this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                int ascent2 = paragraphCell.textLayout.getLineAscent(0);
                                this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - ascent2;
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TLRPC.TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = (this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.dp(10.0f);
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                    if (this.currentBlock.numLayout != null) {
                        this.textLayout.prefix = this.currentBlock.numLayout.textLayout.getText();
                    }
                }
                RecyclerView.ViewHolder viewHolder2 = this.blockLayout;
                if (viewHolder2 != null && (viewHolder2.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                    ArticleViewer.this.textSelectionHelper.arrayList.clear();
                    ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(ArticleViewer.this.textSelectionHelper.arrayList);
                    Iterator<TextSelectionHelper.TextLayoutBlock> it = ArticleViewer.this.textSelectionHelper.arrayList.iterator();
                    while (it.hasNext()) {
                        TextSelectionHelper.TextLayoutBlock block = it.next();
                        if (block instanceof DrawingText) {
                            ((DrawingText) block).x += this.blockX;
                            ((DrawingText) block).y += this.blockY;
                        }
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i = this.blockX;
                view.layout(i, this.blockY, this.blockLayout.itemView.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int width = getMeasuredWidth();
            if (this.currentBlock.numLayout != null) {
                canvas.save();
                if (this.parentAdapter.isRtl) {
                    canvas.translate(((width - AndroidUtilities.dp(18.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f)), this.textY + this.numOffsetY);
                } else {
                    canvas.translate(((AndroidUtilities.dp(18.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil(this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f)), this.textY + this.numOffsetY);
                }
                this.currentBlock.numLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
        public void invalidate() {
            super.invalidate();
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                viewHolder.itemView.invalidate();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText == null) {
                return;
            }
            info.setText(drawingText.getText());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null && (viewHolder.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(blocks);
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockDetailsCell extends View implements Drawable.Callback, TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockDetails currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(50.0f);
        private int textY = AndroidUtilities.dp(11.0f) + 1;
        private AnimatedArrowDrawable arrow = new AnimatedArrowDrawable(ArticleViewer.getGrayTextColor(), true);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockDetailsCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r3;
            this.parentAdapter = adapter;
        }

        @Override // android.view.View, android.graphics.drawable.Drawable.Callback
        public void invalidateDrawable(Drawable drawable) {
            invalidate();
        }

        @Override // android.view.View, android.graphics.drawable.Drawable.Callback
        public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
        }

        @Override // android.view.View, android.graphics.drawable.Drawable.Callback
        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        }

        public void setBlock(TLRPC.TL_pageBlockDetails block) {
            this.currentBlock = block;
            this.arrow.setAnimationProgress(block.open ? 0.0f : 1.0f);
            this.arrow.setCallback(this);
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.dp(39.0f);
            TLRPC.TL_pageBlockDetails tL_pageBlockDetails = this.currentBlock;
            if (tL_pageBlockDetails != null) {
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockDetails.title, width - AndroidUtilities.dp(52.0f), 0, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    h = Math.max(h, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            }
            setMeasuredDimension(width, h + 1);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            canvas.save();
            canvas.translate(AndroidUtilities.dp(18.0f), ((getMeasuredHeight() - AndroidUtilities.dp(13.0f)) - 1) / 2);
            this.arrow.draw(canvas);
            canvas.restore();
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
            int y = getMeasuredHeight() - 1;
            canvas.drawLine(0.0f, y, getMeasuredWidth(), y, ArticleViewer.dividerPaint);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class BlockDetailsBottomCell extends View {
        private RectF rect = new RectF();

        public BlockDetailsBottomCell(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(4.0f) + 1);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(0.0f, 0.0f, getMeasuredWidth(), 0.0f, ArticleViewer.dividerPaint);
        }
    }

    /* loaded from: classes4.dex */
    public static class BlockRelatedArticlesShadowCell extends View {
        private CombinedDrawable shadowDrawable;

        public BlockRelatedArticlesShadowCell(Context context) {
            super(context);
            Drawable drawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, -16777216);
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
            this.shadowDrawable = combinedDrawable;
            combinedDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(12.0f));
            Theme.setCombinedDrawableColor(this.shadowDrawable, Theme.getColor(Theme.key_windowBackgroundGray), false);
        }
    }

    /* loaded from: classes4.dex */
    public class BlockRelatedArticlesHeaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockRelatedArticles currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockRelatedArticlesHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockRelatedArticles block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockRelatedArticles tL_pageBlockRelatedArticles = this.currentBlock;
            if (tL_pageBlockRelatedArticles != null) {
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockRelatedArticles.title, width - AndroidUtilities.dp(52.0f), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    this.textY = AndroidUtilities.dp(6.0f) + ((AndroidUtilities.dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(width, AndroidUtilities.dp(38.0f));
                this.textLayout.x = this.textX;
                this.textLayout.y = this.textY;
                return;
            }
            setMeasuredDimension(width, 1);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockRelatedArticlesCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TL_pageBlockRelatedArticlesChild currentBlock;
        private boolean divider;
        private boolean drawImage;
        private ImageReceiver imageView;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textOffset;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(10.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockRelatedArticlesCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r2;
            this.parentAdapter = adapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
        }

        public void setBlock(TL_pageBlockRelatedArticlesChild block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int availableWidth;
            int layoutHeight;
            boolean isTitleRtl;
            int height;
            int height2;
            String description;
            int height3;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.divider = this.currentBlock.num != this.currentBlock.parent.articles.size() - 1;
            TLRPC.TL_pageRelatedArticle item = this.currentBlock.parent.articles.get(this.currentBlock.num);
            int additionalHeight = AndroidUtilities.dp(SharedConfig.ivFontSize - 16);
            TLRPC.Photo photo = item.photo_id != 0 ? this.parentAdapter.getPhotoWithId(item.photo_id) : null;
            if (photo != null) {
                this.drawImage = true;
                TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                if (image == thumb) {
                    thumb = null;
                }
                this.imageView.setImage(ImageLocation.getForPhoto(image, photo), "64_64", ImageLocation.getForPhoto(thumb, photo), "64_64_b", image.size, null, this.parentAdapter.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int layoutHeight2 = AndroidUtilities.dp(60.0f);
            int availableWidth2 = width - AndroidUtilities.dp(36.0f);
            if (!this.drawImage) {
                availableWidth = availableWidth2;
            } else {
                int imageWidth = AndroidUtilities.dp(44.0f);
                this.imageView.setImageCoords((width - imageWidth) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), imageWidth, imageWidth);
                availableWidth = (int) (availableWidth2 - (this.imageView.getImageWidth() + AndroidUtilities.dp(6.0f)));
            }
            int height4 = AndroidUtilities.dp(18.0f);
            boolean isTitleRtl2 = false;
            if (item.title != null) {
                layoutHeight = layoutHeight2;
                this.textLayout = ArticleViewer.this.createLayoutForText(this, item.title, null, availableWidth, this.textY, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 3, this.parentAdapter);
            } else {
                layoutHeight = layoutHeight2;
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                int count = drawingText.getLineCount();
                int lineCount = 4 - count;
                this.textOffset = this.textLayout.getHeight() + AndroidUtilities.dp(6.0f) + additionalHeight;
                int height5 = height4 + this.textLayout.getHeight();
                int a = 0;
                while (true) {
                    if (a >= count) {
                        break;
                    } else if (this.textLayout.getLineLeft(a) == 0.0f) {
                        a++;
                    } else {
                        isTitleRtl2 = true;
                        break;
                    }
                }
                this.textLayout.x = this.textX;
                this.textLayout.y = this.textY;
                isTitleRtl = isTitleRtl2;
                height = height5;
                height2 = lineCount;
            } else {
                this.textOffset = 0;
                isTitleRtl = false;
                height = height4;
                height2 = 4;
            }
            int lineCount2 = item.published_date;
            if (lineCount2 != 0 && !TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(item.published_date * 1000), item.author);
            } else {
                String description2 = item.author;
                if (!TextUtils.isEmpty(description2)) {
                    description = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, item.author);
                } else if (item.published_date != 0) {
                    description = LocaleController.getInstance().chatFullDate.format(item.published_date * 1000);
                } else {
                    String description3 = item.description;
                    if (!TextUtils.isEmpty(description3)) {
                        description = item.description;
                    } else {
                        String description4 = item.url;
                        description = description4;
                    }
                }
            }
            DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, description, null, availableWidth, this.textY + this.textOffset, this.currentBlock, (this.parentAdapter.isRtl || isTitleRtl) ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, height2, this.parentAdapter);
            this.textLayout2 = createLayoutForText;
            if (createLayoutForText == null) {
                height3 = height;
            } else {
                int height6 = height + createLayoutForText.getHeight();
                if (this.textLayout != null) {
                    height6 += AndroidUtilities.dp(6.0f) + additionalHeight;
                }
                this.textLayout2.x = this.textX;
                this.textLayout2.y = this.textY + this.textOffset;
                height3 = height6;
            }
            setMeasuredDimension(width, (this.divider ? 1 : 0) + Math.max(layoutHeight, height3));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            if (this.drawImage) {
                this.imageView.draw(canvas);
            }
            int count = 0;
            canvas.save();
            canvas.translate(this.textX, AndroidUtilities.dp(10.0f));
            if (this.textLayout != null) {
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.textLayout.draw(canvas, this);
                count = count2;
            }
            if (this.textLayout2 != null) {
                canvas.translate(0.0f, this.textOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.textLayout2.draw(canvas, this);
            }
            canvas.restore();
            if (this.divider) {
                canvas.drawLine(this.parentAdapter.isRtl ? 0.0f : AndroidUtilities.dp(17.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (this.parentAdapter.isRtl ? AndroidUtilities.dp(17.0f) : 0), getMeasuredHeight() - 1, ArticleViewer.dividerPaint);
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockHeaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockHeader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockHeader block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockHeader tL_pageBlockHeader = this.currentBlock;
            if (tL_pageBlockHeader != null) {
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockHeader.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout == null) {
                return;
            }
            info.setText(((Object) this.textLayout.getText()) + ", " + LocaleController.getString("AccDescrIVHeading", R.string.AccDescrIVHeading));
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(18.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int width = getMeasuredWidth() / 3;
            this.rect.set(width, AndroidUtilities.dp(8.0f), width * 2, AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    /* loaded from: classes4.dex */
    public class BlockSubtitleCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockSubtitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockSubtitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockSubtitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockSubtitle tL_pageBlockSubtitle = this.currentBlock;
            if (tL_pageBlockSubtitle != null) {
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockSubtitle.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout == null) {
                return;
            }
            info.setText(((Object) this.textLayout.getText()) + ", " + LocaleController.getString("AccDescrIVHeading", R.string.AccDescrIVHeading));
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockPullquoteCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockPullquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockPullquoteCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockPullquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockPullquote tL_pageBlockPullquote = this.currentBlock;
            if (tL_pageBlockPullquote != null) {
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockPullquote.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
                this.textY2 = AndroidUtilities.dp(2.0f) + height;
                DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.textY2, this.currentBlock, this.parentAdapter);
                this.textLayout2 = createLayoutForText2;
                if (createLayoutForText2 != null) {
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                    this.textLayout2.x = this.textX;
                    this.textLayout2.y = this.textY2;
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int count = 0;
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.textLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.textLayout2 != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY2);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.textLayout2.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockBlockquoteCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockBlockquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockBlockquoteCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockBlockquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                int textWidth = width - AndroidUtilities.dp(50.0f);
                if (this.currentBlock.level > 0) {
                    textWidth -= AndroidUtilities.dp(this.currentBlock.level * 14);
                }
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                }
                if (this.currentBlock.level > 0) {
                    if (this.parentAdapter.isRtl) {
                        this.textX = AndroidUtilities.dp((this.currentBlock.level * 14) + 14);
                    } else {
                        this.textX = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(32.0f);
                    }
                } else if (this.parentAdapter.isRtl) {
                    this.textX = AndroidUtilities.dp(14.0f);
                } else {
                    this.textX = AndroidUtilities.dp(32.0f);
                }
                this.textY2 = AndroidUtilities.dp(8.0f) + height;
                DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, textWidth, this.textY2, this.currentBlock, this.parentAdapter);
                this.textLayout2 = createLayoutForText2;
                if (createLayoutForText2 != null) {
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                }
                DrawingText drawingText2 = this.textLayout2;
                if (drawingText2 != null) {
                    drawingText2.x = this.textX;
                    this.textLayout2.y = this.textY2;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            int counter = 0;
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int counter2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.textLayout.draw(canvas, this);
                canvas.restore();
                counter = counter2;
            }
            if (this.textLayout2 != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY2);
                ArticleViewer.this.drawTextSelection(canvas, this, counter);
                this.textLayout2.draw(canvas, this);
                canvas.restore();
            }
            if (!this.parentAdapter.isRtl) {
                canvas.drawRect(AndroidUtilities.dp((this.currentBlock.level * 14) + 18), AndroidUtilities.dp(6.0f), AndroidUtilities.dp((this.currentBlock.level * 14) + 20), getMeasuredHeight() - AndroidUtilities.dp(6.0f), ArticleViewer.quoteLinePaint);
            } else {
                int x = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                canvas.drawRect(x, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.0f) + x, getMeasuredHeight() - AndroidUtilities.dp(6.0f), ArticleViewer.quoteLinePaint);
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockPhotoCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, TextSelectionHelper.ArticleSelectableView {
        private int TAG;
        boolean autoDownload;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockPhoto currentBlock;
        private String currentFilter;
        private TLRPC.Photo currentPhoto;
        private TLRPC.PhotoSize currentPhotoObject;
        private TLRPC.PhotoSize currentPhotoObjectThumb;
        private String currentThumbFilter;
        private int currentType;
        private MessageObject.GroupedMessagePosition groupPosition;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private Drawable linkDrawable;
        private WebpageAdapter parentAdapter;
        private TLRPC.PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockPhotoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            ArticleViewer.this = r6;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(1711276032, Theme.ACTION_BAR_PHOTO_VIEWER_COLOR, -1, -2500135);
            this.TAG = DownloadController.getInstance(r6.currentAccount).generateObserverTag();
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = type;
        }

        public void setBlock(TLRPC.TL_pageBlockPhoto block, boolean first, boolean last) {
            this.parentBlock = null;
            this.currentBlock = block;
            this.isFirst = first;
            this.channelCell.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                this.linkDrawable = getResources().getDrawable(R.drawable.msg_instant_link);
            }
            TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = this.currentBlock;
            if (tL_pageBlockPhoto != null) {
                TLRPC.Photo photo = this.parentAdapter.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                if (photo != null) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                } else {
                    this.currentPhotoObject = null;
                }
            } else {
                this.currentPhotoObject = null;
            }
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC.PageBlock block) {
            this.parentBlock = block;
            if (this.parentAdapter.channelBlock != null && (this.parentBlock instanceof TLRPC.TL_pageBlockCover)) {
                this.channelCell.setBlock(this.parentAdapter.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARN: Code restructure failed: missing block: B:30:0x00a3, code lost:
            if (r2 <= (r3 + org.telegram.messenger.AndroidUtilities.dp(48.0f))) goto L33;
         */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            /*
                Method dump skipped, instructions count: 284
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width;
            int height;
            int textWidth;
            int photoX;
            int height2;
            int i;
            int width2 = View.MeasureSpec.getSize(widthMeasureSpec);
            int height3 = 0;
            int i2 = this.currentType;
            boolean z = true;
            if (i2 == 1) {
                int width3 = ((View) getParent()).getMeasuredWidth();
                height3 = ((View) getParent()).getMeasuredHeight();
                width = width3;
            } else if (i2 != 2) {
                width = width2;
            } else {
                height3 = (int) Math.ceil(this.groupPosition.ph * Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f);
                width = width2;
            }
            TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = this.currentBlock;
            if (tL_pageBlockPhoto != null) {
                this.currentPhoto = this.parentAdapter.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                int size = AndroidUtilities.dp(48.0f);
                int photoWidth = width;
                int photoHeight = height3;
                if (this.currentType == 0 && this.currentBlock.level > 0) {
                    int dp = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0f);
                    photoX = dp;
                    this.textX = dp;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                } else {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                TLRPC.Photo photo = this.currentPhoto;
                if (photo == null || this.currentPhotoObject == null) {
                    height = height3;
                } else {
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 40, true);
                    this.currentPhotoObjectThumb = closestPhotoSizeWithSize;
                    TLRPC.PhotoSize photoSize = this.currentPhotoObject;
                    if (photoSize == closestPhotoSizeWithSize) {
                        this.currentPhotoObjectThumb = null;
                    }
                    int i3 = this.currentType;
                    if (i3 == 0) {
                        float scale = photoWidth / photoSize.w;
                        height3 = (int) (this.currentPhotoObject.h * scale);
                        if (this.parentBlock instanceof TLRPC.TL_pageBlockCover) {
                            height3 = Math.min(height3, photoWidth);
                        } else {
                            int maxHeight = (int) ((Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(56.0f)) * 0.9f);
                            if (height3 > maxHeight) {
                                height3 = maxHeight;
                                float scale2 = height3 / this.currentPhotoObject.h;
                                photoWidth = (int) (this.currentPhotoObject.w * scale2);
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        photoHeight = height3;
                    } else if (i3 == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.dp(2.0f);
                        }
                        if (this.groupPosition.leftSpanOffset != 0) {
                            int offset = (int) Math.ceil((this.groupPosition.leftSpanOffset * width) / 1000.0f);
                            photoWidth -= offset;
                            photoX += offset;
                        }
                    }
                    this.imageView.setImageCoords(photoX, (this.isFirst || (i = this.currentType) == 1 || i == 2 || this.currentBlock.level > 0) ? 0.0f : AndroidUtilities.dp(8.0f), photoWidth, photoHeight);
                    if (this.currentType != 0) {
                        this.currentFilter = String.format(Locale.US, "%d_%d", Integer.valueOf(photoWidth), Integer.valueOf(photoHeight));
                    } else {
                        this.currentFilter = null;
                    }
                    this.currentThumbFilter = "80_80_b";
                    this.autoDownload = (DownloadController.getInstance(ArticleViewer.this.currentAccount).getCurrentDownloadMask() & 1) != 0;
                    File path = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentPhotoObject, true);
                    if (!this.autoDownload && !path.exists()) {
                        this.imageView.setStrippedLocation(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto));
                        this.imageView.setImage(null, this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, this.currentPhotoObject.size, null, this.parentAdapter.currentPage, 1);
                    } else {
                        this.imageView.setStrippedLocation(null);
                        this.imageView.setImage(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto), this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, this.currentPhotoObject.size, null, this.parentAdapter.currentPage, 1);
                    }
                    this.buttonX = (int) (this.imageView.getImageX() + ((this.imageView.getImageWidth() - size) / 2.0f));
                    int imageY = (int) (this.imageView.getImageY() + ((this.imageView.getImageHeight() - size) / 2.0f));
                    this.buttonY = imageY;
                    RadialProgress2 radialProgress2 = this.radialProgress;
                    int i4 = this.buttonX;
                    radialProgress2.setProgressRect(i4, imageY, i4 + size, imageY + size);
                    height = height3;
                }
                this.textY = (int) (this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0f));
                if (this.currentType == 0) {
                    DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                    this.captionLayout = createLayoutForText;
                    if (createLayoutForText != null) {
                        int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp2;
                        height2 = height + dp2 + AndroidUtilities.dp(4.0f);
                    } else {
                        height2 = height;
                    }
                    DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                    this.creditLayout = createLayoutForText2;
                    if (createLayoutForText2 != null) {
                        height = height2 + AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    } else {
                        height = height2;
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (!(this.parentBlock instanceof TLRPC.TL_pageBlockCover) || this.parentAdapter.blocks == null || this.parentAdapter.blocks.size() <= 1 || !(this.parentAdapter.blocks.get(1) instanceof TLRPC.TL_pageBlockChannel)) {
                    z = false;
                }
                boolean nextIsChannel = z;
                if (this.currentType != 2 && !nextIsChannel) {
                    height += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.captionLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.captionLayout.y = this.textY;
                }
                DrawingText drawingText2 = this.creditLayout;
                if (drawingText2 != null) {
                    drawingText2.x = this.textX;
                    this.creditLayout.y = this.textY + this.creditOffset;
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY(this.imageView.getImageHeight() - AndroidUtilities.dp(39.0f));
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            if (!this.imageView.hasBitmapImage() || this.imageView.getCurrentAlpha() != 1.0f) {
                canvas.drawRect(this.imageView.getImageX(), this.imageView.getImageY(), this.imageView.getImageX2(), this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
            }
            if (!ArticleViewer.this.pinchToZoomHelper.isInOverlayModeFor(this)) {
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
            }
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                int x = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                int y = (int) (this.imageView.getImageY() + AndroidUtilities.dp(11.0f));
                this.linkDrawable.setBounds(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                this.linkDrawable.draw(canvas);
            }
            int count = 0;
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 0) {
                return 2;
            }
            if (i == 1) {
                return 3;
            }
            return 4;
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                this.radialProgress.setProgress(0.0f, animated);
                this.imageView.setImage(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto), this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, this.currentPhotoObject.size, null, this.parentAdapter.currentPage, 1);
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 1) {
                this.imageView.cancelLoadImage();
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            }
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            File path = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentPhotoObject, true);
            boolean fileExists = path.exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, null, this);
                float setProgress = 0.0f;
                if (this.autoDownload || FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else {
                    this.buttonState = 0;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                this.radialProgress.setProgress(setProgress, false);
            }
            invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
        public int getObserverTag() {
            return this.TAG;
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockMapCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockMap currentBlock;
        private int currentMapProvider;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private WebpageAdapter parentAdapter;
        private boolean photoPressed;
        private int textX;
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockMapCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.currentType = type;
        }

        public void setBlock(TLRPC.TL_pageBlockMap block, boolean first, boolean last) {
            this.currentBlock = block;
            this.isFirst = first;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (event.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                try {
                    double lat = this.currentBlock.geo.lat;
                    double lon = this.currentBlock.geo._long;
                    Activity activity = ArticleViewer.this.parentActivity;
                    activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (event.getAction() == 3) {
                this.photoPressed = false;
            }
            return this.photoPressed || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width;
            int height;
            int textWidth;
            int photoX;
            int photoX2;
            int photoWidth;
            int height2;
            int i;
            int width2 = View.MeasureSpec.getSize(widthMeasureSpec);
            int height3 = 0;
            int i2 = this.currentType;
            if (i2 == 1) {
                int width3 = ((View) getParent()).getMeasuredWidth();
                height3 = ((View) getParent()).getMeasuredHeight();
                width = width3;
            } else if (i2 != 2) {
                width = width2;
            } else {
                height3 = width2;
                width = width2;
            }
            TLRPC.TL_pageBlockMap tL_pageBlockMap = this.currentBlock;
            if (tL_pageBlockMap != null) {
                int photoWidth2 = width;
                if (this.currentType == 0 && tL_pageBlockMap.level > 0) {
                    int dp = AndroidUtilities.dp(this.currentBlock.level * 14) + AndroidUtilities.dp(18.0f);
                    photoX = dp;
                    this.textX = dp;
                    photoWidth2 -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth2;
                } else {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                if (this.currentType == 0) {
                    float scale = photoWidth2 / this.currentBlock.w;
                    int height4 = (int) (this.currentBlock.h * scale);
                    int maxHeight = (int) ((Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(56.0f)) * 0.9f);
                    if (height4 > maxHeight) {
                        float scale2 = maxHeight / this.currentBlock.h;
                        int photoWidth3 = (int) (this.currentBlock.w * scale2);
                        height = maxHeight;
                        photoWidth = photoWidth3;
                        photoX2 = photoX + (((width - photoX) - photoWidth3) / 2);
                    } else {
                        height = height4;
                        photoWidth = photoWidth2;
                        photoX2 = photoX;
                    }
                } else {
                    height = height3;
                    photoWidth = photoWidth2;
                    photoX2 = photoX;
                }
                this.imageView.setImageCoords(photoX2, (this.isFirst || (i = this.currentType) == 1 || i == 2 || this.currentBlock.level > 0) ? 0.0f : AndroidUtilities.dp(8.0f), photoWidth, height);
                String currentUrl = AndroidUtilities.formapMapUrl(ArticleViewer.this.currentAccount, this.currentBlock.geo.lat, this.currentBlock.geo._long, (int) (photoWidth / AndroidUtilities.density), (int) (height / AndroidUtilities.density), true, 15, -1);
                WebFile currentWebFile = WebFile.createWithGeoPoint(this.currentBlock.geo, (int) (photoWidth / AndroidUtilities.density), (int) (height / AndroidUtilities.density), 15, Math.min(2, (int) Math.ceil(AndroidUtilities.density)));
                int i3 = MessagesController.getInstance(ArticleViewer.this.currentAccount).mapProvider;
                this.currentMapProvider = i3;
                if (i3 == 2) {
                    if (currentWebFile != null) {
                        this.imageView.setImage(ImageLocation.getForWebFile(currentWebFile), null, null, null, this.parentAdapter.currentPage, 0);
                    }
                } else if (currentUrl != null) {
                    this.imageView.setImage(currentUrl, null, null, null, 0L);
                }
                this.textY = (int) (this.imageView.getImageY() + this.imageView.getImageHeight() + AndroidUtilities.dp(8.0f));
                if (this.currentType == 0) {
                    DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                    this.captionLayout = createLayoutForText;
                    if (createLayoutForText != null) {
                        int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp2;
                        int height5 = height + dp2 + AndroidUtilities.dp(4.0f);
                        this.captionLayout.x = this.textX;
                        this.captionLayout.y = this.textY;
                        height2 = height5;
                    } else {
                        height2 = height;
                    }
                    DrawingText createLayoutForText2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = createLayoutForText2;
                    if (createLayoutForText2 != null) {
                        height = height2 + AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        this.creditLayout.x = this.textX;
                        this.creditLayout.y = this.textY + this.creditOffset;
                    } else {
                        height = height2;
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentType != 2) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            Theme.chat_docBackPaint.setColor(Theme.getColor(Theme.key_chat_inLocationBackground));
            canvas.drawRect(this.imageView.getImageX(), this.imageView.getImageY(), this.imageView.getImageX2(), this.imageView.getImageY2(), Theme.chat_docBackPaint);
            int i = 0;
            int left = (int) (this.imageView.getCenterX() - (Theme.chat_locationDrawable[0].getIntrinsicWidth() / 2));
            int top = (int) (this.imageView.getCenterY() - (Theme.chat_locationDrawable[0].getIntrinsicHeight() / 2));
            Theme.chat_locationDrawable[0].setBounds(left, top, Theme.chat_locationDrawable[0].getIntrinsicWidth() + left, Theme.chat_locationDrawable[0].getIntrinsicHeight() + top);
            Theme.chat_locationDrawable[0].draw(canvas);
            this.imageView.draw(canvas);
            if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                int w = (int) (Theme.chat_redLocationIcon.getIntrinsicWidth() * 0.8f);
                int h = (int) (Theme.chat_redLocationIcon.getIntrinsicHeight() * 0.8f);
                int x = (int) (this.imageView.getImageX() + ((this.imageView.getImageWidth() - w) / 2.0f));
                int y = (int) (this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2.0f) - h));
                Theme.chat_redLocationIcon.setAlpha((int) (this.imageView.getCurrentAlpha() * 255.0f));
                Theme.chat_redLocationIcon.setBounds(x, y, x + w, y + h);
                Theme.chat_redLocationIcon.draw(canvas);
            }
            int count = 0;
            if (this.captionLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                int count2 = 0 + 1;
                ArticleViewer.this.drawTextSelection(canvas, this, 0);
                this.captionLayout.draw(canvas, this);
                canvas.restore();
                count = count2;
            }
            if (this.creditLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY + this.creditOffset);
                ArticleViewer.this.drawTextSelection(canvas, this, count);
                this.creditLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                float dp = AndroidUtilities.dp(18.0f);
                float dp2 = AndroidUtilities.dp(20.0f);
                int measuredHeight = getMeasuredHeight();
                if (this.currentBlock.bottom) {
                    i = AndroidUtilities.dp(6.0f);
                }
                canvas.drawRect(dp, 0.0f, dp2, measuredHeight - i, ArticleViewer.quoteLinePaint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("Map", R.string.Map));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockChannelCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private int buttonWidth;
        private AnimatorSet currentAnimation;
        private TLRPC.TL_pageBlockChannel currentBlock;
        private int currentState;
        private int currentType;
        private ImageView imageView;
        private WebpageAdapter parentAdapter;
        private ContextProgressView progressView;
        private DrawingText textLayout;
        private TextView textView;
        private int textX2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(11.0f);
        private Paint backgroundPaint = new Paint();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockChannelCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            ArticleViewer.this = r5;
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.currentType = type;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textView.setText(LocaleController.getString("ChannelJoin", R.string.ChannelJoin));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ArticleViewer$BlockChannelCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ArticleViewer.BlockChannelCell.this.m1560lambda$new$0$orgtelegramuiArticleViewer$BlockChannelCell(view);
                }
            });
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setImageResource(R.drawable.list_check);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            ContextProgressView contextProgressView = new ContextProgressView(context, 0);
            this.progressView = contextProgressView;
            addView(contextProgressView, LayoutHelper.createFrame(39, 39, 53));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ArticleViewer$BlockChannelCell */
        public /* synthetic */ void m1560lambda$new$0$orgtelegramuiArticleViewer$BlockChannelCell(View v) {
            if (this.currentState != 0) {
                return;
            }
            setState(1, true);
            ArticleViewer articleViewer = ArticleViewer.this;
            articleViewer.joinChannel(this, articleViewer.loadedChannel);
        }

        public void setBlock(TLRPC.TL_pageBlockChannel block) {
            this.currentBlock = block;
            if (this.currentType == 0) {
                int color = Theme.getColor(Theme.key_switchTrack);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                this.textView.setTextColor(ArticleViewer.this.getLinkTextColor());
                this.backgroundPaint.setColor(Color.argb(34, r, g, b));
                this.imageView.setColorFilter(new PorterDuffColorFilter(ArticleViewer.getGrayTextColor(), PorterDuff.Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            }
            TLRPC.Chat channel = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Long.valueOf(block.channel.id));
            if (channel == null || channel.min) {
                ArticleViewer.this.loadChannel(this, this.parentAdapter, block.channel);
                setState(1, false);
            } else {
                ArticleViewer.this.loadedChannel = channel;
                if (!channel.left || channel.kicked) {
                    setState(4, false);
                } else {
                    setState(0, false);
                }
            }
            requestLayout();
        }

        public void setState(int state, boolean animated) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.currentState = state;
            float f = 0.0f;
            float f2 = 0.1f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[9];
                TextView textView = this.textView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = state == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
                TextView textView2 = this.textView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = state == 0 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView2, property2, fArr2);
                TextView textView3 = this.textView;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = state == 0 ? 1.0f : 0.1f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView3, property3, fArr3);
                ContextProgressView contextProgressView = this.progressView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = state == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, property4, fArr4);
                ContextProgressView contextProgressView2 = this.progressView;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = state == 1 ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView2, property5, fArr5);
                ContextProgressView contextProgressView3 = this.progressView;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = state == 1 ? 1.0f : 0.1f;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView3, property6, fArr6);
                ImageView imageView = this.imageView;
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                if (state == 2) {
                    f = 1.0f;
                }
                fArr7[0] = f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView, property7, fArr7);
                ImageView imageView2 = this.imageView;
                Property property8 = View.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = state == 2 ? 1.0f : 0.1f;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView2, property8, fArr8);
                ImageView imageView3 = this.imageView;
                Property property9 = View.SCALE_Y;
                float[] fArr9 = new float[1];
                if (state == 2) {
                    f2 = 1.0f;
                }
                fArr9[0] = f2;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView3, property9, fArr9);
                animatorSet2.playTogether(animatorArr);
                this.currentAnimation.setDuration(150L);
                this.currentAnimation.start();
                return;
            }
            this.textView.setAlpha(state == 0 ? 1.0f : 0.0f);
            this.textView.setScaleX(state == 0 ? 1.0f : 0.1f);
            this.textView.setScaleY(state == 0 ? 1.0f : 0.1f);
            this.progressView.setAlpha(state == 1 ? 1.0f : 0.0f);
            this.progressView.setScaleX(state == 1 ? 1.0f : 0.1f);
            this.progressView.setScaleY(state == 1 ? 1.0f : 0.1f);
            ImageView imageView4 = this.imageView;
            if (state == 2) {
                f = 1.0f;
            }
            imageView4.setAlpha(f);
            this.imageView.setScaleX(state == 2 ? 1.0f : 0.1f);
            ImageView imageView5 = this.imageView;
            if (state == 2) {
                f2 = 1.0f;
            }
            imageView5.setScaleY(f2);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (this.currentType != 0) {
                return super.onTouchEvent(event);
            }
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, AndroidUtilities.dp(48.0f));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), C.BUFFER_FLAG_ENCRYPTED));
            TLRPC.TL_pageBlockChannel tL_pageBlockChannel = this.currentBlock;
            if (tL_pageBlockChannel != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, tL_pageBlockChannel.channel.title, null, (width - AndroidUtilities.dp(52.0f)) - this.buttonWidth, this.textY, this.currentBlock, StaticLayoutEx.ALIGN_LEFT(), this.parentAdapter);
                if (this.parentAdapter.isRtl) {
                    this.textX2 = this.textX;
                } else {
                    this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            TextView textView = this.textView;
            int i = this.textX2;
            textView.layout(i, 0, textView.getMeasuredWidth() + i, this.textView.getMeasuredHeight());
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(39.0f), this.backgroundPaint);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null && drawingText.getLineCount() > 0) {
                canvas.save();
                if (this.parentAdapter.isRtl) {
                    canvas.translate((getMeasuredWidth() - this.textLayout.getLineWidth(0)) - this.textX, this.textY);
                } else {
                    canvas.translate(this.textX, this.textY);
                }
                if (this.currentType == 0) {
                    ArticleViewer.this.drawTextSelection(canvas, this);
                }
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockAuthorDateCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockAuthorDate currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockAuthorDateCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockAuthorDate block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            MetricAffectingSpan[] spans;
            Spannable spannableAuthor;
            CharSequence text;
            CharSequence text2;
            int idx;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockAuthorDate tL_pageBlockAuthorDate = this.currentBlock;
            if (tL_pageBlockAuthorDate != null) {
                CharSequence author = ArticleViewer.this.getText(this.parentAdapter, this, tL_pageBlockAuthorDate.author, this.currentBlock.author, this.currentBlock, width);
                if (author instanceof Spannable) {
                    Spannable spannableAuthor2 = (Spannable) author;
                    spannableAuthor = spannableAuthor2;
                    spans = (MetricAffectingSpan[]) spannableAuthor2.getSpans(0, author.length(), MetricAffectingSpan.class);
                } else {
                    spannableAuthor = null;
                    spans = null;
                }
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(author)) {
                    text = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(this.currentBlock.published_date * 1000), author);
                } else if (TextUtils.isEmpty(author)) {
                    text = LocaleController.getInstance().chatFullDate.format(this.currentBlock.published_date * 1000);
                } else {
                    text = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, author);
                }
                if (spans != null) {
                    try {
                        if (spans.length > 0 && (idx = TextUtils.indexOf(text, author)) != -1) {
                            Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
                            text = spannable;
                            for (int a = 0; a < spans.length; a++) {
                                spannable.setSpan(spans[a], spannableAuthor.getSpanStart(spans[a]) + idx, spannableAuthor.getSpanEnd(spans[a]) + idx, 33);
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                        text2 = text;
                    }
                }
                text2 = text;
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, text2, null, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    if (this.parentAdapter.isRtl) {
                        this.textX = (int) Math.floor((width - this.textLayout.getLineWidth(0)) - AndroidUtilities.dp(16.0f));
                    } else {
                        this.textX = AndroidUtilities.dp(18.0f);
                    }
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText == null) {
                return;
            }
            info.setText(drawingText.getText());
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockTitleCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockTitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockTitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockTitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockTitle tL_pageBlockTitle = this.currentBlock;
            if (tL_pageBlockTitle != null) {
                if (tL_pageBlockTitle.first) {
                    height = 0 + AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout == null) {
                return;
            }
            info.setText(((Object) this.textLayout.getText()) + ", " + LocaleController.getString("AccDescrIVTitle", R.string.AccDescrIVTitle));
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockKickerCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockKicker currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockKickerCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockKicker block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockKicker tL_pageBlockKicker = this.currentBlock;
            if (tL_pageBlockKicker != null) {
                if (tL_pageBlockKicker.first) {
                    this.textY = AndroidUtilities.dp(16.0f);
                    height = 0 + AndroidUtilities.dp(8.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockFooterCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockFooter currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockFooterCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockFooter block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockFooter tL_pageBlockFooter = this.currentBlock;
            if (tL_pageBlockFooter != null) {
                if (tL_pageBlockFooter.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((this.currentBlock.level * 14) + 18);
                }
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    int height2 = createLayoutForText.getHeight();
                    if (this.currentBlock.level > 0) {
                        height = height2 + AndroidUtilities.dp(8.0f);
                    } else {
                        height = height2 + AndroidUtilities.dp(16.0f);
                    }
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            if (this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
            if (this.currentBlock.level > 0) {
                canvas.drawRect(AndroidUtilities.dp(18.0f), 0.0f, AndroidUtilities.dp(20.0f), getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0), ArticleViewer.quoteLinePaint);
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class BlockPreformattedCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockPreformatted currentBlock;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        private View textContainer;
        private DrawingText textLayout;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockPreformattedCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r5;
            this.parentAdapter = adapter;
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context) { // from class: org.telegram.ui.ArticleViewer.BlockPreformattedCell.1
                @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockPreformattedCell.this.textContainer.getMeasuredWidth() > getMeasuredWidth()) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                @Override // android.view.View
                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }
            };
            this.scrollView = horizontalScrollView;
            horizontalScrollView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context) { // from class: org.telegram.ui.ArticleViewer.BlockPreformattedCell.2
                @Override // android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int height = 0;
                    int width = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        blockPreformattedCell.textLayout = ArticleViewer.this.createLayoutForText(this, null, BlockPreformattedCell.this.currentBlock.text, AndroidUtilities.dp(5000.0f), 0, BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
                        if (BlockPreformattedCell.this.textLayout != null) {
                            height = 0 + BlockPreformattedCell.this.textLayout.getHeight();
                            int count = BlockPreformattedCell.this.textLayout.getLineCount();
                            for (int a = 0; a < count; a++) {
                                width = Math.max((int) Math.ceil(BlockPreformattedCell.this.textLayout.getLineWidth(a)), width);
                            }
                        }
                    } else {
                        height = 1;
                    }
                    setMeasuredDimension(AndroidUtilities.dp(32.0f) + width, height);
                }

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    ArticleViewer articleViewer = ArticleViewer.this;
                    WebpageAdapter webpageAdapter = BlockPreformattedCell.this.parentAdapter;
                    BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                    return articleViewer.checkLayoutForLinks(webpageAdapter, event, blockPreformattedCell, blockPreformattedCell.textLayout, 0, 0) || super.onTouchEvent(event);
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (BlockPreformattedCell.this.textLayout != null) {
                        canvas.save();
                        ArticleViewer.this.drawTextSelection(canvas, BlockPreformattedCell.this);
                        BlockPreformattedCell.this.textLayout.draw(canvas, this);
                        canvas.restore();
                        BlockPreformattedCell.this.textLayout.x = (int) getX();
                        BlockPreformattedCell.this.textLayout.y = (int) getY();
                    }
                }
            };
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
            int dp = AndroidUtilities.dp(16.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            int dp2 = AndroidUtilities.dp(12.0f);
            layoutParams.bottomMargin = dp2;
            layoutParams.topMargin = dp2;
            this.scrollView.addView(this.textContainer, layoutParams);
            if (Build.VERSION.SDK_INT >= 23) {
                this.scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() { // from class: org.telegram.ui.ArticleViewer$BlockPreformattedCell$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnScrollChangeListener
                    public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                        ArticleViewer.BlockPreformattedCell.this.m1563lambda$new$0$orgtelegramuiArticleViewer$BlockPreformattedCell(view, i, i2, i3, i4);
                    }
                });
            }
            setWillNotDraw(false);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ArticleViewer$BlockPreformattedCell */
        public /* synthetic */ void m1563lambda$new$0$orgtelegramuiArticleViewer$BlockPreformattedCell(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (ArticleViewer.this.textSelectionHelper != null && ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                ArticleViewer.this.textSelectionHelper.invalidate();
            }
        }

        public void setBlock(TLRPC.TL_pageBlockPreformatted block) {
            this.currentBlock = block;
            this.scrollView.setScrollX(0);
            this.textContainer.requestLayout();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
            setMeasuredDimension(width, this.scrollView.getMeasuredHeight());
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock == null) {
                return;
            }
            canvas.drawRect(0.0f, AndroidUtilities.dp(8.0f), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(8.0f), ArticleViewer.preformattedBackgroundPaint);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }

        @Override // android.view.View, org.telegram.ui.Cells.TextSelectionHelper.SelectableView
        public void invalidate() {
            this.textContainer.invalidate();
            super.invalidate();
        }
    }

    /* loaded from: classes4.dex */
    public class BlockSubheaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockSubheader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BlockSubheaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            ArticleViewer.this = r1;
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockSubheader block) {
            this.currentBlock = block;
            requestLayout();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockSubheader tL_pageBlockSubheader = this.currentBlock;
            if (tL_pageBlockSubheader != null) {
                DrawingText createLayoutForText = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockSubheader.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = createLayoutForText;
                if (createLayoutForText != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate(this.textX, this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout == null) {
                return;
            }
            info.setText(((Object) this.textLayout.getText()) + ", " + LocaleController.getString("AccDescrIVHeading", R.string.AccDescrIVHeading));
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class ReportCell extends FrameLayout {
        private boolean hasViews;
        private TextView textView;
        private TextView viewsTextView;

        public ReportCell(Context context) {
            super(context);
            setTag(90);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setText(LocaleController.getString("PreviewFeedback2", R.string.PreviewFeedback2));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.viewsTextView = textView2;
            textView2.setTextSize(1, 12.0f);
            this.viewsTextView.setGravity(19);
            this.viewsTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            addView(this.viewsTextView, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setViews(int count) {
            if (count == 0) {
                this.hasViews = false;
                this.viewsTextView.setVisibility(8);
                this.textView.setGravity(17);
            } else {
                this.hasViews = true;
                this.viewsTextView.setVisibility(0);
                this.textView.setGravity(21);
                this.viewsTextView.setText(LocaleController.formatPluralStringComma("Views", count));
            }
            int color = Theme.getColor(Theme.key_switchTrack);
            this.textView.setTextColor(ArticleViewer.getGrayTextColor());
            this.viewsTextView.setTextColor(ArticleViewer.getGrayTextColor());
            this.textView.setBackgroundColor(Color.argb(34, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    public void drawTextSelection(Canvas canvas, TextSelectionHelper.ArticleSelectableView view) {
        drawTextSelection(canvas, view, 0);
    }

    public void drawTextSelection(Canvas canvas, TextSelectionHelper.ArticleSelectableView view, int i) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper;
        View v = (View) view;
        if (v.getTag() != null && v.getTag() == "bottomSheet" && (articleTextSelectionHelper = this.textSelectionHelperBottomSheet) != null) {
            articleTextSelectionHelper.draw(canvas, view, i);
        } else {
            this.textSelectionHelper.draw(canvas, view, i);
        }
    }

    public boolean openPhoto(TLRPC.PageBlock block, WebpageAdapter adapter) {
        int index;
        List<TLRPC.PageBlock> pageBlocks;
        if (!(block instanceof TLRPC.TL_pageBlockVideo) || WebPageUtils.isVideo(adapter.currentPage, block)) {
            pageBlocks = new ArrayList<>(adapter.photoBlocks);
            index = adapter.photoBlocks.indexOf(block);
        } else {
            pageBlocks = Collections.singletonList(block);
            index = 0;
        }
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        photoViewer.setParentActivity(this.parentActivity);
        return photoViewer.openPhoto(index, new RealPageBlocksAdapter(adapter.currentPage, pageBlocks), new PageBlocksPhotoViewerProvider(pageBlocks));
    }

    /* loaded from: classes4.dex */
    public class RealPageBlocksAdapter implements PhotoViewer.PageBlocksAdapter {
        private final TLRPC.WebPage page;
        private final List<TLRPC.PageBlock> pageBlocks;

        private RealPageBlocksAdapter(TLRPC.WebPage page, List<TLRPC.PageBlock> pageBlocks) {
            ArticleViewer.this = r1;
            this.page = page;
            this.pageBlocks = pageBlocks;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public int getItemsCount() {
            return this.pageBlocks.size();
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public TLRPC.PageBlock get(int index) {
            return this.pageBlocks.get(index);
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public List<TLRPC.PageBlock> getAll() {
            return this.pageBlocks;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public boolean isVideo(int index) {
            return index < this.pageBlocks.size() && index >= 0 && WebPageUtils.isVideo(this.page, get(index));
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public TLObject getMedia(int index) {
            if (index >= this.pageBlocks.size() || index < 0) {
                return null;
            }
            return WebPageUtils.getMedia(this.page, get(index));
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public File getFile(int index) {
            if (index >= this.pageBlocks.size() || index < 0) {
                return null;
            }
            return WebPageUtils.getMediaFile(this.page, get(index));
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public String getFileName(int index) {
            TLObject media = getMedia(index);
            if (media instanceof TLRPC.Photo) {
                media = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) media).sizes, AndroidUtilities.getPhotoSize());
            }
            return FileLoader.getAttachFileName(media);
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public CharSequence getCaption(int index) {
            CharSequence caption = null;
            TLRPC.PageBlock pageBlock = get(index);
            if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
                String url = ((TLRPC.TL_pageBlockPhoto) pageBlock).url;
                if (!TextUtils.isEmpty(url)) {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(url);
                    stringBuilder.setSpan(new URLSpan(url) { // from class: org.telegram.ui.ArticleViewer.RealPageBlocksAdapter.1
                        @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
                        public void onClick(View widget) {
                            ArticleViewer.this.openWebpageUrl(getURL(), null);
                        }
                    }, 0, url.length(), 34);
                    caption = stringBuilder;
                }
            }
            if (caption == null) {
                TLRPC.RichText captionRichText = ArticleViewer.this.getBlockCaption(pageBlock, 2);
                caption = ArticleViewer.this.getText(this.page, (View) null, captionRichText, captionRichText, pageBlock, -AndroidUtilities.dp(100.0f));
                if (caption instanceof Spannable) {
                    Spannable spannable = (Spannable) caption;
                    TextPaintUrlSpan[] spans = (TextPaintUrlSpan[]) spannable.getSpans(0, caption.length(), TextPaintUrlSpan.class);
                    SpannableStringBuilder builder = new SpannableStringBuilder(caption.toString());
                    caption = builder;
                    if (spans != null && spans.length > 0) {
                        for (int a = 0; a < spans.length; a++) {
                            builder.setSpan(new URLSpan(spans[a].getUrl()) { // from class: org.telegram.ui.ArticleViewer.RealPageBlocksAdapter.2
                                @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
                                public void onClick(View widget) {
                                    ArticleViewer.this.openWebpageUrl(getURL(), null);
                                }
                            }, spannable.getSpanStart(spans[a]), spannable.getSpanEnd(spans[a]), 33);
                        }
                    }
                }
            }
            return caption;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public TLRPC.PhotoSize getFileLocation(TLObject media, int[] size) {
            if (media instanceof TLRPC.Photo) {
                TLRPC.Photo photo = (TLRPC.Photo) media;
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    size[0] = sizeFull.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    return sizeFull;
                }
                size[0] = -1;
                return null;
            } else if (media instanceof TLRPC.Document) {
                TLRPC.Document document = (TLRPC.Document) media;
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                if (thumb != null) {
                    size[0] = thumb.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    return thumb;
                }
                return null;
            } else {
                return null;
            }
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public void updateSlideshowCell(TLRPC.PageBlock currentPageBlock) {
            int count = ArticleViewer.this.listView[0].getChildCount();
            for (int a = 0; a < count; a++) {
                View child = ArticleViewer.this.listView[0].getChildAt(a);
                if (child instanceof BlockSlideshowCell) {
                    BlockSlideshowCell cell = (BlockSlideshowCell) child;
                    int idx = cell.currentBlock.items.indexOf(currentPageBlock);
                    if (idx != -1) {
                        cell.innerListView.setCurrentItem(idx, false);
                        return;
                    }
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public Object getParentObject() {
            return this.page;
        }
    }

    /* loaded from: classes4.dex */
    public class PageBlocksPhotoViewerProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private final List<TLRPC.PageBlock> pageBlocks;
        private final int[] tempArr = new int[2];

        public PageBlocksPhotoViewerProvider(List<TLRPC.PageBlock> pageBlocks) {
            ArticleViewer.this = r1;
            this.pageBlocks = pageBlocks;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            ImageReceiver imageReceiver;
            if (index < 0 || index >= this.pageBlocks.size() || (imageReceiver = getImageReceiverFromListView(ArticleViewer.this.listView[0], this.pageBlocks.get(index), this.tempArr)) == null) {
                return null;
            }
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = this.tempArr[0];
            object.viewY = this.tempArr[1];
            object.parentView = ArticleViewer.this.listView[0];
            object.imageReceiver = imageReceiver;
            object.thumb = imageReceiver.getBitmapSafe();
            object.radius = imageReceiver.getRoundRadius();
            object.clipTopAddition = ArticleViewer.this.currentHeaderHeight;
            return object;
        }

        private ImageReceiver getImageReceiverFromListView(ViewGroup listView, TLRPC.PageBlock pageBlock, int[] coords) {
            int count = listView.getChildCount();
            for (int a = 0; a < count; a++) {
                ImageReceiver imageReceiver = getImageReceiverView(listView.getChildAt(a), pageBlock, coords);
                if (imageReceiver != null) {
                    return imageReceiver;
                }
            }
            return null;
        }

        private ImageReceiver getImageReceiverView(View view, TLRPC.PageBlock pageBlock, int[] coords) {
            ImageReceiver imageReceiver;
            ImageReceiver imageReceiver2;
            if (view instanceof BlockPhotoCell) {
                BlockPhotoCell cell = (BlockPhotoCell) view;
                if (cell.currentBlock == pageBlock) {
                    view.getLocationInWindow(coords);
                    return cell.imageView;
                }
                return null;
            } else if (view instanceof BlockVideoCell) {
                BlockVideoCell cell2 = (BlockVideoCell) view;
                if (cell2.currentBlock == pageBlock) {
                    view.getLocationInWindow(coords);
                    return cell2.imageView;
                }
                return null;
            } else if (view instanceof BlockCollageCell) {
                ImageReceiver imageReceiver3 = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, pageBlock, coords);
                if (imageReceiver3 != null) {
                    return imageReceiver3;
                }
                return null;
            } else if (view instanceof BlockSlideshowCell) {
                ImageReceiver imageReceiver4 = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, pageBlock, coords);
                if (imageReceiver4 != null) {
                    return imageReceiver4;
                }
                return null;
            } else if (view instanceof BlockListItemCell) {
                BlockListItemCell blockListItemCell = (BlockListItemCell) view;
                if (blockListItemCell.blockLayout != null && (imageReceiver2 = getImageReceiverView(blockListItemCell.blockLayout.itemView, pageBlock, coords)) != null) {
                    return imageReceiver2;
                }
                return null;
            } else if (view instanceof BlockOrderedListItemCell) {
                BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
                if (blockOrderedListItemCell.blockLayout != null && (imageReceiver = getImageReceiverView(blockOrderedListItemCell.blockLayout.itemView, pageBlock, coords)) != null) {
                    return imageReceiver;
                }
                return null;
            } else {
                return null;
            }
        }
    }
}
