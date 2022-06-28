package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
/* loaded from: classes5.dex */
public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    boolean animateToExpanded;
    int currentDragPosition;
    private int currentPosition;
    private ScrollSlidingTabStripDelegate delegate;
    float dragDx;
    private boolean dragEnabled;
    float draggindViewDxOnScreen;
    float draggindViewXOnScreen;
    View draggingView;
    float draggingViewIndicatorOutProgress;
    float draggingViewOutProgress;
    private float expandOffset;
    float expandProgress;
    ValueAnimator expandStickerAnimator;
    private int indicatorHeight;
    private long lastAnimationTime;
    boolean longClickRunning;
    private float positionAnimationProgress;
    float pressedX;
    float pressedY;
    private Paint rectPaint;
    private final Theme.ResourcesProvider resourcesProvider;
    boolean scrollRight;
    long scrollStartTime;
    private boolean shouldExpand;
    private float startAnimationPosition;
    int startDragFromPosition;
    float startDragFromX;
    private int tabCount;
    private LinearLayout tabsContainer;
    private float touchSlop;
    private Type type = Type.LINE;
    private HashMap<String, View> tabTypes = new HashMap<>();
    private HashMap<String, View> prevTypes = new HashMap<>();
    private SparseArray<View> futureTabsPositions = new SparseArray<>();
    private int indicatorColor = -10066330;
    private int underlineColor = 436207616;
    private GradientDrawable indicatorDrawable = new GradientDrawable();
    private int scrollOffset = AndroidUtilities.dp(38.0f);
    private int underlineHeight = AndroidUtilities.dp(2.0f);
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private int lastScrollX = 0;
    SparseArray<StickerTabView> currentPlayingImages = new SparseArray<>();
    SparseArray<StickerTabView> currentPlayingImagesTmp = new SparseArray<>();
    Runnable longClickRunnable = new Runnable() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip.1
        @Override // java.lang.Runnable
        public void run() {
            ScrollSlidingTabStrip scrollSlidingTabStrip;
            ScrollSlidingTabStrip.this.longClickRunning = false;
            ScrollSlidingTabStrip.this.startDragFromX = scrollSlidingTabStrip.getScrollX() + ScrollSlidingTabStrip.this.pressedX;
            ScrollSlidingTabStrip.this.dragDx = 0.0f;
            int p = ((int) Math.ceil(ScrollSlidingTabStrip.this.startDragFromX / ScrollSlidingTabStrip.this.getTabSize())) - 1;
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = ScrollSlidingTabStrip.this;
            scrollSlidingTabStrip2.currentDragPosition = p;
            scrollSlidingTabStrip2.startDragFromPosition = p;
            if (ScrollSlidingTabStrip.this.canSwap(p) && p >= 0 && p < ScrollSlidingTabStrip.this.tabsContainer.getChildCount()) {
                ScrollSlidingTabStrip.this.performHapticFeedback(0);
                ScrollSlidingTabStrip.this.draggindViewDxOnScreen = 0.0f;
                ScrollSlidingTabStrip.this.draggingViewOutProgress = 0.0f;
                ScrollSlidingTabStrip scrollSlidingTabStrip3 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip3.draggingView = scrollSlidingTabStrip3.tabsContainer.getChildAt(p);
                ScrollSlidingTabStrip scrollSlidingTabStrip4 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip4.draggindViewXOnScreen = scrollSlidingTabStrip4.draggingView.getX() - ScrollSlidingTabStrip.this.getScrollX();
                ScrollSlidingTabStrip.this.draggingView.invalidate();
                ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                ScrollSlidingTabStrip.this.invalidateOverlays();
                ScrollSlidingTabStrip.this.invalidate();
            }
        }
    };
    boolean expanded = false;
    private float stickerTabExpandedWidth = AndroidUtilities.dp(86.0f);
    private float stickerTabWidth = AndroidUtilities.dp(38.0f);
    private int scrollByOnNextMeasure = -1;
    Runnable scrollRunnable = new Runnable() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip.6
        @Override // java.lang.Runnable
        public void run() {
            int dx;
            long currentTime = System.currentTimeMillis() - ScrollSlidingTabStrip.this.scrollStartTime;
            int i = -1;
            if (currentTime < 3000) {
                int max = Math.max(1, AndroidUtilities.dp(1.0f));
                if (ScrollSlidingTabStrip.this.scrollRight) {
                    i = 1;
                }
                dx = max * i;
            } else if (currentTime < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                int max2 = Math.max(1, AndroidUtilities.dp(2.0f));
                if (ScrollSlidingTabStrip.this.scrollRight) {
                    i = 1;
                }
                dx = max2 * i;
            } else {
                int max3 = Math.max(1, AndroidUtilities.dp(4.0f));
                if (ScrollSlidingTabStrip.this.scrollRight) {
                    i = 1;
                }
                dx = max3 * i;
            }
            ScrollSlidingTabStrip.this.scrollBy(dx, 0);
            AndroidUtilities.runOnUIThread(ScrollSlidingTabStrip.this.scrollRunnable);
        }
    };
    private LinearLayout.LayoutParams defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(38.0f), -1);
    private LinearLayout.LayoutParams defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);

    /* loaded from: classes5.dex */
    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    /* loaded from: classes5.dex */
    public enum Type {
        LINE,
        TAB
    }

    public ScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip.2
            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child instanceof StickerTabView) {
                    ((StickerTabView) child).updateExpandProgress(ScrollSlidingTabStrip.this.expandProgress);
                }
                if (child == ScrollSlidingTabStrip.this.draggingView) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        if (type != null && this.type != type) {
            this.type = type;
            switch (AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[type.ordinal()]) {
                case 1:
                    this.indicatorDrawable.setCornerRadius(0.0f);
                    return;
                case 2:
                    float rad = AndroidUtilities.dpf2(3.0f);
                    this.indicatorDrawable.setCornerRadii(new float[]{rad, rad, rad, rad, 0.0f, 0.0f, 0.0f, 0.0f});
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip$7 */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type = iArr;
            try {
                iArr[Type.LINE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[Type.TAB.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabTypes.clear();
        this.prevTypes.clear();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        this.currentPosition = 0;
        this.animateFromPosition = false;
    }

    public void beginUpdate(boolean animated) {
        this.prevTypes = this.tabTypes;
        this.tabTypes = new HashMap<>();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        if (animated && Build.VERSION.SDK_INT >= 19) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(250L);
            transition.setOrdering(0);
            transition.addTransition(new AnonymousClass3());
            TransitionManager.beginDelayedTransition(this.tabsContainer, transition);
        }
    }

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip$3 */
    /* loaded from: classes5.dex */
    public class AnonymousClass3 extends Transition {
        AnonymousClass3() {
            ScrollSlidingTabStrip.this = this$0;
        }

        @Override // android.transition.Transition
        public void captureStartValues(TransitionValues transitionValues) {
        }

        @Override // android.transition.Transition
        public void captureEndValues(TransitionValues transitionValues) {
        }

        @Override // android.transition.Transition
        public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
            ValueAnimator invalidateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            invalidateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$3$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ScrollSlidingTabStrip.AnonymousClass3.this.m2969xddfcbcca(valueAnimator);
                }
            });
            return invalidateAnimator;
        }

        /* renamed from: lambda$createAnimator$0$org-telegram-ui-Components-ScrollSlidingTabStrip$3 */
        public /* synthetic */ void m2969xddfcbcca(ValueAnimator a) {
            ScrollSlidingTabStrip.this.invalidate();
        }
    }

    public void commitUpdate() {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            for (Map.Entry<String, View> entry : hashMap.entrySet()) {
                this.tabsContainer.removeView(entry.getValue());
            }
            this.prevTypes.clear();
        }
        int N = this.futureTabsPositions.size();
        for (int a = 0; a < N; a++) {
            int index = this.futureTabsPositions.keyAt(a);
            View view = this.futureTabsPositions.valueAt(a);
            int currentIndex = this.tabsContainer.indexOfChild(view);
            if (currentIndex != index) {
                this.tabsContainer.removeView(view);
                this.tabsContainer.addView(view, index);
            }
        }
        this.futureTabsPositions.clear();
    }

    public void selectTab(int num) {
        if (num < 0 || num >= this.tabCount) {
            return;
        }
        View tab = this.tabsContainer.getChildAt(num);
        tab.performClick();
    }

    private void checkViewIndex(String key, View view, int index) {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            hashMap.remove(key);
        }
        this.futureTabsPositions.put(index, view);
    }

    public TextView addIconTabWithCounter(int id, Drawable drawable) {
        TextView textView;
        String key = "textTab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = (FrameLayout) this.prevTypes.get(key);
        boolean z = false;
        if (tab != null) {
            textView = (TextView) tab.getChildAt(1);
            checkViewIndex(key, tab, position);
        } else {
            tab = new FrameLayout(getContext());
            tab.setFocusable(true);
            this.tabsContainer.addView(tab, position);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.m2963x4b831afd(view);
                }
            });
            tab.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            TextView textView2 = new TextView(getContext());
            textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView2.setTextSize(1, 12.0f);
            textView2.setTextColor(getThemedColor(Theme.key_chat_emojiPanelBadgeText));
            textView2.setGravity(17);
            textView2.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.0f), getThemedColor(Theme.key_chat_emojiPanelBadgeBackground)));
            textView2.setMinWidth(AndroidUtilities.dp(18.0f));
            textView2.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
            tab.addView(textView2, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
            textView = textView2;
        }
        tab.setTag(R.id.index_tag, Integer.valueOf(position));
        if (position == this.currentPosition) {
            z = true;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return textView;
    }

    /* renamed from: lambda$addIconTabWithCounter$0$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2963x4b831afd(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(R.id.index_tag)).intValue());
    }

    public ImageView addIconTab(int id, Drawable drawable) {
        String key = "tab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        ImageView tab = (ImageView) this.prevTypes.get(key);
        boolean z = true;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new ImageView(getContext());
            tab.setFocusable(true);
            tab.setImageDrawable(drawable);
            tab.setScaleType(ImageView.ScaleType.CENTER);
            tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.m2962xcb3ec722(view);
                }
            });
            this.tabsContainer.addView(tab, position);
        }
        tab.setTag(R.id.index_tag, Integer.valueOf(position));
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addIconTab$1$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2962xcb3ec722(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(R.id.index_tag)).intValue());
    }

    public StickerTabView addStickerIconTab(int id, Drawable drawable) {
        String key = "tab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = true;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new StickerTabView(getContext(), 1);
            tab.iconView.setImageDrawable(drawable);
            tab.setFocusable(true);
            tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.m2964x30882916(view);
                }
            });
            tab.setExpanded(this.expanded);
            tab.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(tab, position);
        }
        tab.isChatSticker = false;
        tab.setTag(R.id.index_tag, Integer.valueOf(position));
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addStickerIconTab$2$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2964x30882916(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(R.id.index_tag)).intValue());
    }

    public void addStickerTab(TLRPC.Chat chat) {
        String key = "chat" + chat.id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = false;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            StickerTabView stickerTabView = new StickerTabView(getContext(), 0);
            tab = stickerTabView;
            tab.setFocusable(true);
            tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.m2965x80889fdc(view);
                }
            });
            this.tabsContainer.addView(tab, position);
            stickerTabView.setRoundImage();
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(chat);
            BackupImageView imageView = stickerTabView.imageView;
            imageView.setLayerNum(1);
            imageView.setForUserOrChat(chat, avatarDrawable);
            imageView.setAspectFit(true);
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            stickerTabView.textView.setText(chat.title);
        }
        tab.isChatSticker = true;
        tab.setTag(R.id.index_tag, Integer.valueOf(position));
        if (position == this.currentPosition) {
            z = true;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
    }

    /* renamed from: lambda$addStickerTab$3$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2965x80889fdc(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(R.id.index_tag)).intValue());
    }

    public View addEmojiTab(int id, Emoji.EmojiDrawable emojiDrawable, TLRPC.Document emojiSticker) {
        String key = "tab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = true;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new StickerTabView(getContext(), 2);
            tab.setFocusable(true);
            tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.m2961xc5ead344(view);
                }
            });
            tab.setExpanded(this.expanded);
            tab.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(tab, position);
        }
        tab.isChatSticker = false;
        tab.setTag(R.id.index_tag, Integer.valueOf(position));
        tab.setTag(R.id.parent_tag, emojiDrawable);
        tab.setTag(R.id.object_tag, emojiSticker);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addEmojiTab$4$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2961xc5ead344(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(R.id.index_tag)).intValue());
    }

    public View addStickerTab(TLObject thumb, TLRPC.Document sticker, TLRPC.TL_messages_stickerSet parentObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("set");
        sb.append(parentObject == null ? sticker.id : parentObject.set.id);
        String key = sb.toString();
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = false;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new StickerTabView(getContext(), 0);
            tab.setFocusable(true);
            tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ScrollSlidingTabStrip.this.m2966x63dbec1a(view);
                }
            });
            tab.setExpanded(this.expanded);
            tab.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(tab, position);
        }
        tab.isChatSticker = false;
        tab.setTag(thumb);
        tab.setTag(R.id.index_tag, Integer.valueOf(position));
        tab.setTag(R.id.parent_tag, parentObject);
        tab.setTag(R.id.object_tag, sticker);
        if (position == this.currentPosition) {
            z = true;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addStickerTab$5$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2966x63dbec1a(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(R.id.index_tag)).intValue());
    }

    public void expandStickers(final float x, final boolean expanded) {
        if (this.expanded != expanded) {
            this.expanded = expanded;
            if (!expanded) {
                fling(0);
            }
            ValueAnimator valueAnimator = this.expandStickerAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.expandStickerAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.expandProgress;
            fArr[1] = expanded ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.expandStickerAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ScrollSlidingTabStrip.this.m2968xe446e308(expanded, x, valueAnimator2);
                }
            });
            this.expandStickerAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ScrollSlidingTabStrip.this.expandStickerAnimator = null;
                    ScrollSlidingTabStrip.this.expandProgress = expanded ? 1.0f : 0.0f;
                    for (int i = 0; i < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i++) {
                        ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i).invalidate();
                    }
                    ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                    ScrollSlidingTabStrip.this.updatePosition();
                    if (!expanded) {
                        float allSize = ScrollSlidingTabStrip.this.stickerTabWidth * ScrollSlidingTabStrip.this.tabsContainer.getChildCount();
                        float totalXRelative = (ScrollSlidingTabStrip.this.getScrollX() + x) / (ScrollSlidingTabStrip.this.stickerTabExpandedWidth * ScrollSlidingTabStrip.this.tabsContainer.getChildCount());
                        float maxXRelative = (allSize - ScrollSlidingTabStrip.this.getMeasuredWidth()) / allSize;
                        float additionalX = x;
                        if (totalXRelative > maxXRelative) {
                            totalXRelative = maxXRelative;
                            additionalX = 0.0f;
                        }
                        float scrollToX = allSize * totalXRelative;
                        if (scrollToX - additionalX < 0.0f) {
                            scrollToX = additionalX;
                        }
                        ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                        scrollSlidingTabStrip.expandOffset = (scrollSlidingTabStrip.getScrollX() + additionalX) - scrollToX;
                        ScrollSlidingTabStrip.this.scrollByOnNextMeasure = (int) (scrollToX - additionalX);
                        if (ScrollSlidingTabStrip.this.scrollByOnNextMeasure < 0) {
                            ScrollSlidingTabStrip.this.scrollByOnNextMeasure = 0;
                        }
                        for (int i2 = 0; i2 < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i2++) {
                            View child = ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i2);
                            if (child instanceof StickerTabView) {
                                ((StickerTabView) child).setExpanded(false);
                            }
                            child.getLayoutParams().width = AndroidUtilities.dp(38.0f);
                        }
                        ScrollSlidingTabStrip.this.animateToExpanded = false;
                        ScrollSlidingTabStrip.this.getLayoutParams().height = AndroidUtilities.dp(36.0f);
                        ScrollSlidingTabStrip.this.tabsContainer.requestLayout();
                    }
                }
            });
            this.expandStickerAnimator.start();
            if (expanded) {
                this.animateToExpanded = true;
                for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
                    View child = this.tabsContainer.getChildAt(i);
                    if (child instanceof StickerTabView) {
                        ((StickerTabView) child).setExpanded(true);
                    }
                    child.getLayoutParams().width = AndroidUtilities.dp(86.0f);
                }
                this.tabsContainer.requestLayout();
                getLayoutParams().height = AndroidUtilities.dp(86.0f);
            }
            if (expanded) {
                float totalXRelative = (getScrollX() + x) / (this.stickerTabWidth * this.tabsContainer.getChildCount());
                float scrollToX = this.stickerTabExpandedWidth * this.tabsContainer.getChildCount() * totalXRelative;
                this.expandOffset = scrollToX - (getScrollX() + x);
                this.scrollByOnNextMeasure = (int) (scrollToX - x);
            }
        }
    }

    /* renamed from: lambda$expandStickers$6$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2968xe446e308(boolean expanded, float x, ValueAnimator valueAnimator) {
        if (!expanded) {
            float allSize = this.stickerTabWidth * this.tabsContainer.getChildCount();
            float totalXRelative = (getScrollX() + x) / (this.stickerTabExpandedWidth * this.tabsContainer.getChildCount());
            float maxXRelative = (allSize - getMeasuredWidth()) / allSize;
            float additionalX = x;
            if (totalXRelative > maxXRelative) {
                totalXRelative = maxXRelative;
                additionalX = 0.0f;
            }
            float scrollToX = allSize * totalXRelative;
            if (scrollToX - additionalX < 0.0f) {
                scrollToX = additionalX;
            }
            this.expandOffset = (getScrollX() + additionalX) - scrollToX;
        }
        this.expandProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
        this.tabsContainer.invalidate();
        updatePosition();
    }

    protected void updatePosition() {
    }

    public float getExpandedOffset() {
        if (this.animateToExpanded) {
            return AndroidUtilities.dp(50.0f) * this.expandProgress;
        }
        return 0.0f;
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            if (this.shouldExpand) {
                v.setLayoutParams(this.defaultExpandLayoutParams);
            } else {
                v.setLayoutParams(this.defaultTabLayoutParams);
            }
        }
    }

    private void scrollToChild(int position) {
        if (this.tabCount == 0 || this.tabsContainer.getChildAt(position) == null) {
            return;
        }
        int newScrollX = this.tabsContainer.getChildAt(position).getLeft();
        if (position > 0) {
            newScrollX -= this.scrollOffset;
        }
        int currentScrollX = getScrollX();
        if (newScrollX == this.lastScrollX) {
            return;
        }
        if (newScrollX < currentScrollX) {
            this.lastScrollX = newScrollX;
            smoothScrollTo(newScrollX, 0);
        } else if (this.scrollOffset + newScrollX > (getWidth() + currentScrollX) - (this.scrollOffset * 2)) {
            int width = (newScrollX - getWidth()) + (this.scrollOffset * 3);
            this.lastScrollX = width;
            smoothScrollTo(width, 0);
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setImages();
        int i = this.scrollByOnNextMeasure;
        if (i >= 0) {
            scrollTo(i, 0);
            this.scrollByOnNextMeasure = -1;
        }
    }

    public void setImages() {
        ImageLocation imageLocation;
        float f = this.expandProgress;
        float tabSize = AndroidUtilities.dp(38.0f) + (AndroidUtilities.dp(48.0f) * f);
        float scrollOffset = this.animateToExpanded ? this.expandOffset * (1.0f - f) : 0.0f;
        int start = (int) (((getScrollX() - scrollOffset) - this.tabsContainer.getPaddingLeft()) / tabSize);
        int end = Math.min(this.tabsContainer.getChildCount(), ((int) Math.ceil(getMeasuredWidth() / tabSize)) + start + 1);
        if (this.animateToExpanded) {
            start -= 2;
            end += 2;
            if (start < 0) {
                start = 0;
            }
            if (end > this.tabsContainer.getChildCount()) {
                end = this.tabsContainer.getChildCount();
            }
        }
        this.currentPlayingImagesTmp.clear();
        for (int i = 0; i < this.currentPlayingImages.size(); i++) {
            this.currentPlayingImagesTmp.put(this.currentPlayingImages.valueAt(i).index, this.currentPlayingImages.valueAt(i));
        }
        this.currentPlayingImages.clear();
        for (int a = start; a < end; a++) {
            View child = this.tabsContainer.getChildAt(a);
            if (child instanceof StickerTabView) {
                StickerTabView tabView = (StickerTabView) child;
                if (tabView.type == 2) {
                    Object thumb = tabView.getTag(R.id.parent_tag);
                    Object sticker = tabView.getTag(R.id.object_tag);
                    Drawable thumbDrawable = null;
                    if (thumb instanceof Drawable) {
                        thumbDrawable = (Drawable) thumb;
                    }
                    if (sticker instanceof TLRPC.Document) {
                        tabView.imageView.setImage(ImageLocation.getForDocument((TLRPC.Document) sticker), "36_36_nolimit", (Drawable) null, (Object) null);
                    } else {
                        tabView.imageView.setImageDrawable(thumbDrawable);
                    }
                } else {
                    Object object = child.getTag();
                    Object parentObject = child.getTag(R.id.parent_tag);
                    TLRPC.Document sticker2 = (TLRPC.Document) child.getTag(R.id.object_tag);
                    if (object instanceof TLRPC.Document) {
                        TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(sticker2.thumbs, 90);
                        if (!tabView.inited) {
                            tabView.svgThumb = DocumentObject.getSvgThumb((TLRPC.Document) object, Theme.key_emptyListPlaceholder, 0.2f);
                        }
                        imageLocation = ImageLocation.getForDocument(thumb2, sticker2);
                    } else if (object instanceof TLRPC.PhotoSize) {
                        TLRPC.PhotoSize thumb3 = (TLRPC.PhotoSize) object;
                        int thumbVersion = 0;
                        if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
                            thumbVersion = ((TLRPC.TL_messages_stickerSet) parentObject).set.thumb_version;
                        }
                        imageLocation = ImageLocation.getForSticker(thumb3, sticker2, thumbVersion);
                    }
                    if (imageLocation != null) {
                        tabView.inited = true;
                        SvgHelper.SvgDrawable svgThumb = tabView.svgThumb;
                        BackupImageView imageView = tabView.imageView;
                        if ((object instanceof TLRPC.Document) && MessageObject.isVideoSticker(sticker2)) {
                            if (svgThumb != null) {
                                imageView.setImage(ImageLocation.getForDocument(sticker2), "40_40", svgThumb, 0, parentObject);
                            } else {
                                imageView.setImage(ImageLocation.getForDocument(sticker2), "40_40", imageLocation, (String) null, 0, parentObject);
                            }
                        } else if ((object instanceof TLRPC.Document) && MessageObject.isAnimatedStickerDocument(sticker2, true)) {
                            if (svgThumb != null) {
                                imageView.setImage(ImageLocation.getForDocument(sticker2), "40_40", svgThumb, 0, parentObject);
                            } else {
                                imageView.setImage(ImageLocation.getForDocument(sticker2), "40_40", imageLocation, (String) null, 0, parentObject);
                            }
                        } else if (imageLocation.imageType == 1) {
                            imageView.setImage(imageLocation, "40_40", "tgs", svgThumb, parentObject);
                        } else {
                            imageView.setImage(imageLocation, (String) null, "webp", svgThumb, parentObject);
                        }
                        String title = null;
                        if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
                            title = ((TLRPC.TL_messages_stickerSet) parentObject).set.title;
                        }
                        tabView.textView.setText(title);
                    }
                }
                this.currentPlayingImages.put(tabView.index, tabView);
                this.currentPlayingImagesTmp.remove(tabView.index);
            }
        }
        for (int i2 = 0; i2 < this.currentPlayingImagesTmp.size(); i2++) {
            StickerTabView stickerTabView = this.currentPlayingImagesTmp.valueAt(i2);
            if (stickerTabView != this.draggingView) {
                this.currentPlayingImagesTmp.valueAt(i2).imageView.setImageDrawable(null);
            }
        }
    }

    public int getTabSize() {
        return AndroidUtilities.dp(this.animateToExpanded ? 86.0f : 38.0f);
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        setImages();
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x0134  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0162  */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void dispatchDraw(android.graphics.Canvas r17) {
        /*
            Method dump skipped, instructions count: 414
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.dispatchDraw(android.graphics.Canvas):void");
    }

    public void drawOverlays(Canvas canvas) {
        if (this.draggingView != null) {
            canvas.save();
            float x = this.draggindViewXOnScreen - this.draggindViewDxOnScreen;
            float f = this.draggingViewOutProgress;
            if (f > 0.0f) {
                x = ((1.0f - f) * x) + ((this.draggingView.getX() - getScrollX()) * this.draggingViewOutProgress);
            }
            canvas.translate(x, 0.0f);
            this.draggingView.draw(canvas);
            canvas.restore();
        }
    }

    public void setShouldExpand(boolean value) {
        this.shouldExpand = value;
        requestLayout();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void cancelPositionAnimation() {
        this.animateFromPosition = false;
        this.positionAnimationProgress = 1.0f;
    }

    public void onPageScrolled(int position, int first) {
        int i = this.currentPosition;
        if (i == position) {
            return;
        }
        View currentTab = this.tabsContainer.getChildAt(i);
        if (currentTab != null) {
            this.startAnimationPosition = currentTab.getLeft();
            this.positionAnimationProgress = 0.0f;
            this.animateFromPosition = true;
            this.lastAnimationTime = SystemClock.elapsedRealtime();
        } else {
            this.animateFromPosition = false;
        }
        this.currentPosition = position;
        if (position >= this.tabsContainer.getChildCount()) {
            return;
        }
        this.positionAnimationProgress = 0.0f;
        int a = 0;
        while (a < this.tabsContainer.getChildCount()) {
            this.tabsContainer.getChildAt(a).setSelected(a == position);
            a++;
        }
        if (this.expandStickerAnimator == null) {
            if (first == position && position > 1) {
                scrollToChild(position - 1);
            } else {
                scrollToChild(position);
            }
        }
        invalidate();
    }

    public void invalidateTabs() {
        int N = this.tabsContainer.getChildCount();
        for (int a = 0; a < N; a++) {
            this.tabsContainer.getChildAt(a).invalidate();
        }
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setIndicatorHeight(int value) {
        this.indicatorHeight = value;
        invalidate();
    }

    public void setIndicatorColor(int value) {
        this.indicatorColor = value;
        invalidate();
    }

    public void setUnderlineColor(int value) {
        this.underlineColor = value;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setUnderlineHeight(int value) {
        this.underlineHeight = value;
        invalidate();
    }

    protected void invalidateOverlays() {
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return checkLongPress(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        return checkLongPress(ev) || super.onTouchEvent(ev);
    }

    public boolean checkLongPress(MotionEvent ev) {
        if (ev.getAction() == 0 && this.draggingView == null) {
            this.longClickRunning = true;
            AndroidUtilities.runOnUIThread(this.longClickRunnable, 500L);
            this.pressedX = ev.getX();
            this.pressedY = ev.getY();
        }
        if (this.longClickRunning && ev.getAction() == 2 && (Math.abs(ev.getX() - this.pressedX) > this.touchSlop || Math.abs(ev.getY() - this.pressedY) > this.touchSlop)) {
            this.longClickRunning = false;
            AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
        }
        if (ev.getAction() == 2 && this.draggingView != null) {
            float x = getScrollX() + ev.getX();
            int p = ((int) Math.ceil(x / getTabSize())) - 1;
            int i = this.currentDragPosition;
            if (p != i) {
                if (p < i) {
                    while (!canSwap(p) && p != this.currentDragPosition) {
                        p++;
                    }
                } else {
                    while (!canSwap(p) && p != this.currentDragPosition) {
                        p--;
                    }
                }
            }
            if (this.currentDragPosition != p && canSwap(p)) {
                for (int i2 = 0; i2 < this.tabsContainer.getChildCount(); i2++) {
                    if (i2 != this.currentDragPosition) {
                        StickerTabView stickerTabView = (StickerTabView) this.tabsContainer.getChildAt(i2);
                        stickerTabView.saveXPosition();
                    }
                }
                this.startDragFromX += (p - this.currentDragPosition) * getTabSize();
                this.currentDragPosition = p;
                this.tabsContainer.removeView(this.draggingView);
                this.tabsContainer.addView(this.draggingView, this.currentDragPosition);
                invalidate();
            }
            this.dragDx = x - this.startDragFromX;
            this.draggindViewDxOnScreen = this.pressedX - ev.getX();
            float viewScreenX = ev.getX();
            if (viewScreenX < this.draggingView.getMeasuredWidth() / 2.0f) {
                startScroll(false);
            } else if (viewScreenX > getMeasuredWidth() - (this.draggingView.getMeasuredWidth() / 2.0f)) {
                startScroll(true);
            } else {
                stopScroll();
            }
            this.tabsContainer.invalidate();
            invalidateOverlays();
            return true;
        }
        if (ev.getAction() == 1 || ev.getAction() == 3) {
            stopScroll();
            AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
            if (this.draggingView != null) {
                int i3 = this.startDragFromPosition;
                int i4 = this.currentDragPosition;
                if (i3 != i4) {
                    stickerSetPositionChanged(i3, i4);
                }
                ValueAnimator dragViewOutAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                dragViewOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ScrollSlidingTabStrip.this.m2967x18a44158(valueAnimator);
                    }
                });
                dragViewOutAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ScrollSlidingTabStrip.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (ScrollSlidingTabStrip.this.draggingView != null) {
                            ScrollSlidingTabStrip.this.invalidateOverlays();
                            ScrollSlidingTabStrip.this.draggingView.invalidate();
                            ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                            ScrollSlidingTabStrip.this.invalidate();
                            ScrollSlidingTabStrip.this.draggingView = null;
                        }
                    }
                });
                dragViewOutAnimator.start();
            }
            this.longClickRunning = false;
            invalidateOverlays();
        }
        return false;
    }

    /* renamed from: lambda$checkLongPress$7$org-telegram-ui-Components-ScrollSlidingTabStrip */
    public /* synthetic */ void m2967x18a44158(ValueAnimator valueAnimator) {
        this.draggingViewOutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateOverlays();
    }

    protected void stickerSetPositionChanged(int fromPosition, int toPosition) {
    }

    public boolean canSwap(int p) {
        if (this.dragEnabled && p >= 0 && p < this.tabsContainer.getChildCount()) {
            View child = this.tabsContainer.getChildAt(p);
            return (child instanceof StickerTabView) && ((StickerTabView) child).type == 0 && !((StickerTabView) child).isChatSticker;
        }
        return false;
    }

    private void startScroll(boolean scrollRight) {
        this.scrollRight = scrollRight;
        if (this.scrollStartTime <= 0) {
            this.scrollStartTime = System.currentTimeMillis();
        }
        AndroidUtilities.runOnUIThread(this.scrollRunnable, 16L);
    }

    private void stopScroll() {
        this.scrollStartTime = -1L;
        AndroidUtilities.cancelRunOnUIThread(this.scrollRunnable);
    }

    public boolean isDragging() {
        return this.draggingView != null;
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        this.longClickRunning = false;
        AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
    }

    public void setDragEnabled(boolean enabled) {
        this.dragEnabled = enabled;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
