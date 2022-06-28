package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class FilterTabsView extends FrameLayout {
    private String aActiveTextColorKey;
    private String aBackgroundColorKey;
    private String aTabLineColorKey;
    private String aUnactiveTextColorKey;
    private ListAdapter adapter;
    private int additionalTabWidth;
    private int allTabsWidth;
    private boolean animatingIndicator;
    private float animatingIndicatorProgress;
    private boolean animationRunning;
    private float animationTime;
    private float animationValue;
    private AnimatorSet colorChangeAnimator;
    private int currentPosition;
    private FilterTabsViewDelegate delegate;
    private float editingAnimationProgress;
    private boolean editingForwardAnimation;
    private float editingStartAnimationProgress;
    private boolean ignoreLayout;
    private boolean invalidated;
    private boolean isEditing;
    DefaultItemAnimator itemAnimator;
    private long lastAnimationTime;
    private long lastEditingAnimationTime;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private Drawable lockDrawable;
    private int lockDrawableColor;
    private boolean orderChanged;
    private int prevLayoutWidth;
    private int previousId;
    private int previousPosition;
    private TextPaint textPaint = new TextPaint(1);
    private TextPaint textCounterPaint = new TextPaint(1);
    private Paint deletePaint = new TextPaint(1);
    private Paint counterPaint = new Paint(1);
    private ArrayList<Tab> tabs = new ArrayList<>();
    private int selectedTabId = -1;
    private int manualScrollingToPosition = -1;
    private int manualScrollingToId = -1;
    private int scrollingToChild = -1;
    private String tabLineColorKey = Theme.key_actionBarTabLine;
    private String activeTextColorKey = Theme.key_actionBarTabActiveText;
    private String unactiveTextColorKey = Theme.key_actionBarTabUnactiveText;
    private String selectorColorKey = Theme.key_actionBarTabSelector;
    private String backgroundColorKey = Theme.key_actionBarDefault;
    private CubicBezierInterpolator interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    private SparseIntArray positionToId = new SparseIntArray(5);
    private SparseIntArray positionToStableId = new SparseIntArray(5);
    private SparseIntArray idToPosition = new SparseIntArray(5);
    private SparseIntArray positionToWidth = new SparseIntArray(5);
    private SparseIntArray positionToX = new SparseIntArray(5);
    private Runnable animationRunnable = new Runnable() { // from class: org.telegram.ui.Components.FilterTabsView.1
        @Override // java.lang.Runnable
        public void run() {
            if (!FilterTabsView.this.animatingIndicator) {
                return;
            }
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - FilterTabsView.this.lastAnimationTime;
            if (dt > 17) {
                dt = 17;
            }
            FilterTabsView.access$2616(FilterTabsView.this, ((float) dt) / 200.0f);
            FilterTabsView filterTabsView = FilterTabsView.this;
            filterTabsView.setAnimationIdicatorProgress(filterTabsView.interpolator.getInterpolation(FilterTabsView.this.animationTime));
            if (FilterTabsView.this.animationTime > 1.0f) {
                FilterTabsView.this.animationTime = 1.0f;
            }
            if (FilterTabsView.this.animationTime < 1.0f) {
                AndroidUtilities.runOnUIThread(FilterTabsView.this.animationRunnable);
                return;
            }
            FilterTabsView.this.animatingIndicator = false;
            FilterTabsView.this.setEnabled(true);
            if (FilterTabsView.this.delegate != null) {
                FilterTabsView.this.delegate.onPageScrolled(1.0f);
            }
        }
    };
    private final Property<FilterTabsView, Float> COLORS = new AnimationProperties.FloatProperty<FilterTabsView>("animationValue") { // from class: org.telegram.ui.Components.FilterTabsView.2
        public void setValue(FilterTabsView object, float value) {
            FilterTabsView.this.animationValue = value;
            int color1 = Theme.getColor(FilterTabsView.this.tabLineColorKey);
            int color2 = Theme.getColor(FilterTabsView.this.aTabLineColorKey);
            FilterTabsView.this.selectorDrawable.setColor(ColorUtils.blendARGB(color1, color2, value));
            FilterTabsView.this.listView.invalidateViews();
            FilterTabsView.this.listView.invalidate();
            object.invalidate();
        }

        public Float get(FilterTabsView object) {
            return Float.valueOf(FilterTabsView.this.animationValue);
        }
    };
    private GradientDrawable selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null);

    /* loaded from: classes5.dex */
    public interface FilterTabsViewDelegate {
        boolean canPerformActions();

        boolean didSelectTab(TabView tabView, boolean z);

        int getTabCounter(int i);

        boolean isTabMenuVisible();

        void onDeletePressed(int i);

        void onPageReorder(int i, int i2);

        void onPageScrolled(float f);

        void onPageSelected(Tab tab, boolean z);

        void onSamePageSelected();
    }

    static /* synthetic */ float access$2616(FilterTabsView x0, float x1) {
        float f = x0.animationTime + x1;
        x0.animationTime = f;
        return f;
    }

    public int getCurrentTabStableId() {
        return this.positionToStableId.get(this.currentPosition, -1);
    }

    public int getStableId(int selectedType) {
        return this.positionToStableId.get(selectedType, -1);
    }

    /* loaded from: classes5.dex */
    public class Tab {
        public int counter;
        public int id;
        public boolean isDefault;
        public boolean isLocked;
        public String title;
        public int titleWidth;

        public Tab(int i, String t) {
            FilterTabsView.this = this$0;
            this.id = i;
            this.title = t;
        }

        public int getWidth(boolean store) {
            int c;
            int width = (int) Math.ceil(FilterTabsView.this.textPaint.measureText(this.title));
            this.titleWidth = width;
            if (store) {
                c = FilterTabsView.this.delegate.getTabCounter(this.id);
                if (c < 0) {
                    c = 0;
                }
                if (store) {
                    this.counter = c;
                }
            } else {
                c = this.counter;
            }
            if (c > 0) {
                String counterText = String.format("%d", Integer.valueOf(c));
                int counterWidth = (int) Math.ceil(FilterTabsView.this.textCounterPaint.measureText(counterText));
                int countWidth = Math.max(AndroidUtilities.dp(10.0f), counterWidth) + AndroidUtilities.dp(10.0f);
                width += AndroidUtilities.dp(6.0f) + countWidth;
            }
            return Math.max(AndroidUtilities.dp(40.0f), width);
        }

        public boolean setTitle(String newTitle) {
            if (TextUtils.equals(this.title, newTitle)) {
                return false;
            }
            this.title = newTitle;
            return true;
        }
    }

    /* loaded from: classes5.dex */
    public class TabView extends View {
        public boolean animateChange;
        public boolean animateCounterChange;
        private float animateFromCountWidth;
        private float animateFromCounterWidth;
        int animateFromTabCount;
        private float animateFromTabWidth;
        float animateFromTextX;
        private int animateFromTitleWidth;
        private float animateFromWidth;
        boolean animateTabCounter;
        private boolean animateTabWidth;
        private boolean animateTextChange;
        private boolean animateTextChangeOut;
        boolean animateTextX;
        public ValueAnimator changeAnimator;
        public float changeProgress;
        private int currentPosition;
        private Tab currentTab;
        private String currentText;
        StaticLayout inCounter;
        private int lastCountWidth;
        private float lastCounterWidth;
        private float lastTabWidth;
        float lastTextX;
        String lastTitle;
        StaticLayout lastTitleLayout;
        private int lastTitleWidth;
        private float lastWidth;
        private float locIconXOffset;
        StaticLayout outCounter;
        private float progressToLocked;
        private float rotation;
        StaticLayout stableCounter;
        private int tabWidth;
        private int textHeight;
        private StaticLayout textLayout;
        private int textOffsetX;
        private StaticLayout titleAnimateInLayout;
        private StaticLayout titleAnimateOutLayout;
        private StaticLayout titleAnimateStableLayout;
        private float titleXOffset;
        private RectF rect = new RectF();
        int lastTabCount = -1;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TabView(Context context) {
            super(context);
            FilterTabsView.this = this$0;
        }

        public void setTab(Tab tab, int position) {
            this.currentTab = tab;
            this.currentPosition = position;
            setContentDescription(tab.title);
            requestLayout();
        }

        @Override // android.view.View
        public int getId() {
            return this.currentTab.id;
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.animateChange = false;
            this.animateTabCounter = false;
            this.animateCounterChange = false;
            this.animateTextChange = false;
            this.animateTextX = false;
            this.animateTabWidth = false;
            ValueAnimator valueAnimator = this.changeAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.changeAnimator.removeAllUpdateListeners();
                this.changeAnimator.cancel();
                this.changeAnimator = null;
            }
            invalidate();
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = this.currentTab.getWidth(false) + AndroidUtilities.dp(32.0f) + FilterTabsView.this.additionalTabWidth;
            setMeasuredDimension(w, View.MeasureSpec.getSize(heightMeasureSpec));
        }

        /* JADX WARN: Removed duplicated region for block: B:183:0x0555  */
        /* JADX WARN: Removed duplicated region for block: B:189:0x0571  */
        /* JADX WARN: Removed duplicated region for block: B:190:0x0582  */
        /* JADX WARN: Removed duplicated region for block: B:193:0x0599 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:196:0x05a3  */
        /* JADX WARN: Removed duplicated region for block: B:200:0x05cd  */
        /* JADX WARN: Removed duplicated region for block: B:223:0x0700  */
        /* JADX WARN: Removed duplicated region for block: B:229:0x0741 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:232:0x0748  */
        /* JADX WARN: Removed duplicated region for block: B:238:0x07fd  */
        /* JADX WARN: Removed duplicated region for block: B:261:0x08a2  */
        /* JADX WARN: Removed duplicated region for block: B:264:0x08be  */
        /* JADX WARN: Removed duplicated region for block: B:267:0x091c  */
        /* JADX WARN: Removed duplicated region for block: B:268:0x094f  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r39) {
            /*
                Method dump skipped, instructions count: 2393
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.TabView.onDraw(android.graphics.Canvas):void");
        }

        public boolean animateChange() {
            int countWidth;
            boolean changed;
            String substring;
            String maxStr;
            boolean animateOut;
            int i;
            boolean changed2 = false;
            int i2 = this.currentTab.counter;
            int i3 = this.lastTabCount;
            if (i2 != i3) {
                this.animateTabCounter = true;
                this.animateFromTabCount = i3;
                this.animateFromCountWidth = this.lastCountWidth;
                this.animateFromCounterWidth = this.lastCounterWidth;
                if (i3 > 0 && this.currentTab.counter > 0) {
                    String oldStr = String.valueOf(this.animateFromTabCount);
                    String newStr = String.valueOf(this.currentTab.counter);
                    if (oldStr.length() != newStr.length()) {
                        this.outCounter = new StaticLayout(oldStr, FilterTabsView.this.textCounterPaint, (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(oldStr)), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        int countNewWidth = (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(newStr));
                        this.inCounter = new StaticLayout(newStr, FilterTabsView.this.textCounterPaint, countNewWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    } else {
                        SpannableStringBuilder oldSpannableStr = new SpannableStringBuilder(oldStr);
                        SpannableStringBuilder newSpannableStr = new SpannableStringBuilder(newStr);
                        SpannableStringBuilder stableStr = new SpannableStringBuilder(newStr);
                        for (int i4 = 0; i4 < oldStr.length(); i4++) {
                            if (oldStr.charAt(i4) == newStr.charAt(i4)) {
                                oldSpannableStr.setSpan(new EmptyStubSpan(), i4, i4 + 1, 0);
                                newSpannableStr.setSpan(new EmptyStubSpan(), i4, i4 + 1, 0);
                            } else {
                                stableStr.setSpan(new EmptyStubSpan(), i4, i4 + 1, 0);
                            }
                        }
                        int countOldWidth = (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(oldStr));
                        this.outCounter = new StaticLayout(oldSpannableStr, FilterTabsView.this.textCounterPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.stableCounter = new StaticLayout(stableStr, FilterTabsView.this.textCounterPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                        this.inCounter = new StaticLayout(newSpannableStr, FilterTabsView.this.textCounterPaint, countOldWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                }
                changed2 = true;
            }
            String counterText = null;
            if (this.currentTab.counter > 0) {
                counterText = String.format("%d", Integer.valueOf(this.currentTab.counter));
                int counterWidth = (int) Math.ceil(FilterTabsView.this.textCounterPaint.measureText(counterText));
                countWidth = Math.max(AndroidUtilities.dp(10.0f), counterWidth) + AndroidUtilities.dp(10.0f);
            } else {
                countWidth = 0;
            }
            int tabWidth = this.currentTab.titleWidth + (countWidth != 0 ? AndroidUtilities.dp((counterText != null ? 1.0f : FilterTabsView.this.editingStartAnimationProgress) * 6.0f) + countWidth : 0);
            int textX = (getMeasuredWidth() - tabWidth) / 2;
            float f = this.lastTextX;
            if (textX != f) {
                this.animateTextX = true;
                this.animateFromTextX = f;
                changed2 = true;
            }
            if (this.lastTitle != null && !this.currentTab.title.equals(this.lastTitle)) {
                if (this.lastTitle.length() > this.currentTab.title.length()) {
                    animateOut = true;
                    maxStr = this.lastTitle;
                    substring = this.currentTab.title;
                } else {
                    animateOut = false;
                    maxStr = this.currentTab.title;
                    substring = this.lastTitle;
                }
                int startFrom = maxStr.indexOf(substring);
                if (startFrom >= 0) {
                    CharSequence text = Emoji.replaceEmoji(maxStr, FilterTabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    SpannableStringBuilder inStr = new SpannableStringBuilder(text);
                    SpannableStringBuilder stabeStr = new SpannableStringBuilder(text);
                    if (startFrom != 0) {
                        stabeStr.setSpan(new EmptyStubSpan(), 0, startFrom, 0);
                    }
                    if (substring.length() + startFrom != maxStr.length()) {
                        i = 0;
                        stabeStr.setSpan(new EmptyStubSpan(), substring.length() + startFrom, maxStr.length(), 0);
                    } else {
                        i = 0;
                    }
                    inStr.setSpan(new EmptyStubSpan(), startFrom, substring.length() + startFrom, i);
                    this.titleAnimateInLayout = new StaticLayout(inStr, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    StaticLayout staticLayout = new StaticLayout(stabeStr, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateStableLayout = staticLayout;
                    this.animateTextChange = true;
                    this.animateTextChangeOut = animateOut;
                    this.titleXOffset = startFrom == 0 ? 0.0f : -staticLayout.getPrimaryHorizontal(startFrom);
                    this.animateFromTitleWidth = this.lastTitleWidth;
                    this.titleAnimateOutLayout = null;
                    changed = true;
                } else {
                    this.titleAnimateInLayout = new StaticLayout(this.currentTab.title, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateOutLayout = new StaticLayout(this.lastTitle, FilterTabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleAnimateStableLayout = null;
                    this.animateTextChange = true;
                    this.titleXOffset = 0.0f;
                    this.animateFromTitleWidth = this.lastTitleWidth;
                    changed = true;
                }
            } else {
                changed = changed2;
            }
            if (tabWidth != this.lastTabWidth || getMeasuredWidth() != this.lastWidth) {
                this.animateTabWidth = true;
                this.animateFromTabWidth = this.lastTabWidth;
                this.animateFromWidth = this.lastWidth;
                return true;
            }
            return changed;
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setSelected((this.currentTab == null || FilterTabsView.this.selectedTabId == -1 || this.currentTab.id != FilterTabsView.this.selectedTabId) ? false : true);
            info.addAction(16);
            if (Build.VERSION.SDK_INT >= 21) {
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrOpenMenu2", R.string.AccDescrOpenMenu2)));
            } else {
                info.addAction(32);
            }
            if (this.currentTab != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.currentTab.title);
                Tab tab = this.currentTab;
                int unread = tab != null ? tab.counter : 0;
                if (unread > 0) {
                    sb.append("\n");
                    sb.append(LocaleController.formatPluralString("AccDescrUnreadCount", unread, new Object[0]));
                }
                info.setContentDescription(sb);
            }
        }

        public void clearTransitionParams() {
            this.animateChange = false;
            this.animateTabCounter = false;
            this.animateCounterChange = false;
            this.animateTextChange = false;
            this.animateTextX = false;
            this.animateTabWidth = false;
            this.changeAnimator = null;
            invalidate();
        }

        public void shakeLockIcon(final float x, final int num) {
            if (num == 6) {
                this.locIconXOffset = 0.0f;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, AndroidUtilities.dp(x));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.FilterTabsView$TabView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    FilterTabsView.TabView.this.m2620xe6a17167(valueAnimator);
                }
            });
            animatorSet.playTogether(animator);
            animatorSet.setDuration(50L);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FilterTabsView.TabView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    TabView tabView = TabView.this;
                    int i = num;
                    tabView.shakeLockIcon(i == 5 ? 0.0f : -x, i + 1);
                    TabView.this.locIconXOffset = 0.0f;
                    TabView.this.invalidate();
                }
            });
            animatorSet.start();
        }

        /* renamed from: lambda$shakeLockIcon$0$org-telegram-ui-Components-FilterTabsView$TabView */
        public /* synthetic */ void m2620xe6a17167(ValueAnimator animation) {
            this.locIconXOffset = ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }
    }

    public FilterTabsView(Context context) {
        super(context);
        this.textCounterPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textCounterPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.deletePaint.setStyle(Paint.Style.STROKE);
        this.deletePaint.setStrokeCap(Paint.Cap.ROUND);
        this.deletePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        float rad = AndroidUtilities.dpf2(3.0f);
        this.selectorDrawable.setCornerRadii(new float[]{rad, rad, rad, rad, 0.0f, 0.0f, 0.0f, 0.0f});
        this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setHorizontalScrollBarEnabled(false);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.FilterTabsView.3
            @Override // android.view.View
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                FilterTabsView.this.invalidate();
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean allowSelectChildAtPosition(View child) {
                return FilterTabsView.this.isEnabled() && FilterTabsView.this.delegate.canPerformActions();
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean canHighlightChildAt(View child, float x, float y) {
                if (FilterTabsView.this.isEditing) {
                    TabView tabView = (TabView) child;
                    int side = AndroidUtilities.dp(6.0f);
                    if (tabView.rect.left - side < x && tabView.rect.right + side > x) {
                        return false;
                    }
                }
                return super.canHighlightChildAt(child, x, y);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipChildren(false);
        AnonymousClass4 anonymousClass4 = new AnonymousClass4();
        this.itemAnimator = anonymousClass4;
        anonymousClass4.setDelayAnimations(false);
        this.listView.setItemAnimator(this.itemAnimator);
        this.listView.setSelectorType(8);
        this.listView.setSelectorRadius(6);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false) { // from class: org.telegram.ui.Components.FilterTabsView.5
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.FilterTabsView.5.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller, androidx.recyclerview.widget.RecyclerView.SmoothScroller
                    protected void onTargetFound(View targetView, RecyclerView.State state2, RecyclerView.SmoothScroller.Action action) {
                        int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
                        if (dx > 0 || (dx == 0 && targetView.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                            dx += AndroidUtilities.dp(60.0f);
                        } else if (dx < 0 || (dx == 0 && targetView.getRight() + AndroidUtilities.dp(21.0f) > FilterTabsView.this.getMeasuredWidth())) {
                            dx -= AndroidUtilities.dp(60.0f);
                        }
                        int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
                        int distance = (int) Math.sqrt((dx * dx) + (dy * dy));
                        int time = Math.max(180, calculateTimeForDeceleration(distance));
                        if (time > 0) {
                            action.update(-dx, -dy, time, this.mDecelerateInterpolator);
                        }
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (FilterTabsView.this.delegate.isTabMenuVisible()) {
                    dx = 0;
                }
                return super.scrollHorizontallyBy(dx, recycler, state);
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(this.listView);
        this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
        this.listView.setClipToPadding(false);
        this.listView.setDrawSelectorBehind(true);
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        listAdapter.setHasStableIds(true);
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.Components.FilterTabsView$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i, float f, float f2) {
                FilterTabsView.this.m2617lambda$new$0$orgtelegramuiComponentsFilterTabsView(view, i, f, f2);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.FilterTabsView$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return FilterTabsView.this.m2618lambda$new$1$orgtelegramuiComponentsFilterTabsView(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.FilterTabsView.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FilterTabsView.this.invalidate();
            }
        });
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* renamed from: org.telegram.ui.Components.FilterTabsView$4 */
    /* loaded from: classes5.dex */
    public class AnonymousClass4 extends DefaultItemAnimator {
        AnonymousClass4() {
            FilterTabsView.this = this$0;
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void runPendingAnimations() {
            boolean removalsPending = !this.mPendingRemovals.isEmpty();
            boolean movesPending = !this.mPendingMoves.isEmpty();
            boolean changesPending = !this.mPendingChanges.isEmpty();
            boolean additionsPending = !this.mPendingAdditions.isEmpty();
            if (removalsPending || movesPending || additionsPending || changesPending) {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.1f);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.FilterTabsView$4$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        FilterTabsView.AnonymousClass4.this.m2619xd36768ec(valueAnimator2);
                    }
                });
                valueAnimator.setDuration(getMoveDuration());
                valueAnimator.start();
            }
            super.runPendingAnimations();
        }

        /* renamed from: lambda$runPendingAnimations$0$org-telegram-ui-Components-FilterTabsView$4 */
        public /* synthetic */ void m2619xd36768ec(ValueAnimator valueAnimator12) {
            FilterTabsView.this.listView.invalidate();
            FilterTabsView.this.invalidate();
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
        public boolean animateMove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
            if (holder.itemView instanceof TabView) {
                View view = holder.itemView;
                int fromX2 = fromX + ((int) holder.itemView.getTranslationX());
                int fromY2 = fromY + ((int) holder.itemView.getTranslationY());
                resetAnimation(holder);
                int deltaX = toX - fromX2;
                int deltaY = toY - fromY2;
                if (deltaX != 0) {
                    view.setTranslationX(-deltaX);
                }
                if (deltaY != 0) {
                    view.setTranslationY(-deltaY);
                }
                TabView tabView = (TabView) holder.itemView;
                boolean animateChange = tabView.animateChange();
                if (animateChange) {
                    tabView.changeProgress = 0.0f;
                    tabView.animateChange = true;
                    FilterTabsView.this.invalidate();
                }
                if (deltaX == 0 && deltaY == 0 && !animateChange) {
                    dispatchMoveFinished(holder);
                    return false;
                }
                this.mPendingMoves.add(new DefaultItemAnimator.MoveInfo(holder, fromX2, fromY2, toX, toY));
                return true;
            }
            return super.animateMove(holder, info, fromX, fromY, toX, toY);
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        public void animateMoveImpl(RecyclerView.ViewHolder holder, DefaultItemAnimator.MoveInfo moveInfo) {
            super.animateMoveImpl(holder, moveInfo);
            if (holder.itemView instanceof TabView) {
                final TabView tabView = (TabView) holder.itemView;
                if (tabView.animateChange) {
                    if (tabView.changeAnimator != null) {
                        tabView.changeAnimator.removeAllListeners();
                        tabView.changeAnimator.removeAllUpdateListeners();
                        tabView.changeAnimator.cancel();
                    }
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.FilterTabsView$4$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            FilterTabsView.AnonymousClass4.lambda$animateMoveImpl$1(FilterTabsView.TabView.this, valueAnimator2);
                        }
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FilterTabsView.4.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            tabView.clearTransitionParams();
                        }
                    });
                    tabView.changeAnimator = valueAnimator;
                    valueAnimator.setDuration(getMoveDuration());
                    valueAnimator.start();
                }
            }
        }

        public static /* synthetic */ void lambda$animateMoveImpl$1(TabView tabView, ValueAnimator valueAnimator1) {
            tabView.changeProgress = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
            tabView.invalidate();
        }

        @Override // androidx.recyclerview.widget.SimpleItemAnimator
        public void onMoveFinished(RecyclerView.ViewHolder item) {
            super.onMoveFinished(item);
            item.itemView.setTranslationX(0.0f);
            if (item.itemView instanceof TabView) {
                ((TabView) item.itemView).clearTransitionParams();
            }
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void endAnimation(RecyclerView.ViewHolder item) {
            super.endAnimation(item);
            item.itemView.setTranslationX(0.0f);
            if (item.itemView instanceof TabView) {
                ((TabView) item.itemView).clearTransitionParams();
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FilterTabsView */
    public /* synthetic */ void m2617lambda$new$0$orgtelegramuiComponentsFilterTabsView(View view, int position, float x, float y) {
        FilterTabsViewDelegate filterTabsViewDelegate;
        if (!this.delegate.canPerformActions()) {
            return;
        }
        TabView tabView = (TabView) view;
        if (this.isEditing) {
            if (position != 0) {
                int side = AndroidUtilities.dp(6.0f);
                if (tabView.rect.left - side < x && tabView.rect.right + side > x) {
                    this.delegate.onDeletePressed(tabView.currentTab.id);
                }
            }
        } else if (position != this.currentPosition || (filterTabsViewDelegate = this.delegate) == null) {
            scrollToTab(tabView.currentTab, position);
        } else {
            filterTabsViewDelegate.onSamePageSelected();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-FilterTabsView */
    public /* synthetic */ boolean m2618lambda$new$1$orgtelegramuiComponentsFilterTabsView(View view, int position) {
        if (this.delegate.canPerformActions() && !this.isEditing) {
            if (this.delegate.didSelectTab((TabView) view, position == this.currentPosition)) {
                this.listView.hideSelector(true);
                return true;
            }
        }
        return false;
    }

    public void setDelegate(FilterTabsViewDelegate filterTabsViewDelegate) {
        this.delegate = filterTabsViewDelegate;
    }

    public boolean isAnimatingIndicator() {
        return this.animatingIndicator;
    }

    private void scrollToTab(Tab tab, int position) {
        if (tab.isLocked) {
            FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
            if (filterTabsViewDelegate != null) {
                filterTabsViewDelegate.onPageSelected(tab, false);
                return;
            }
            return;
        }
        int i = this.currentPosition;
        boolean scrollingForward = i < position;
        this.scrollingToChild = -1;
        this.previousPosition = i;
        this.previousId = this.selectedTabId;
        this.currentPosition = position;
        this.selectedTabId = tab.id;
        if (this.animatingIndicator) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animatingIndicator = false;
        }
        this.animationTime = 0.0f;
        this.animatingIndicatorProgress = 0.0f;
        this.animatingIndicator = true;
        setEnabled(false);
        AndroidUtilities.runOnUIThread(this.animationRunnable, 16L);
        FilterTabsViewDelegate filterTabsViewDelegate2 = this.delegate;
        if (filterTabsViewDelegate2 != null) {
            filterTabsViewDelegate2.onPageSelected(tab, scrollingForward);
        }
        scrollToChild(position);
    }

    public void selectFirstTab() {
        if (this.tabs.isEmpty()) {
            return;
        }
        scrollToTab(this.tabs.get(0), 0);
    }

    public void setAnimationIdicatorProgress(float value) {
        this.animatingIndicatorProgress = value;
        this.listView.invalidateViews();
        invalidate();
        FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
        if (filterTabsViewDelegate != null) {
            filterTabsViewDelegate.onPageScrolled(value);
        }
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public RecyclerListView getTabsContainer() {
        return this.listView;
    }

    public int getNextPageId(boolean forward) {
        return this.positionToId.get(this.currentPosition + (forward ? 1 : -1), -1);
    }

    public void removeTabs() {
        this.tabs.clear();
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.positionToX.clear();
        this.allTabsWidth = 0;
    }

    public boolean hasTab(int id) {
        return this.idToPosition.get(id, -1) != -1;
    }

    public void resetTabId() {
        this.selectedTabId = -1;
    }

    public void addTab(int id, int stableId, String text, boolean isDefault, boolean isLocked) {
        int position = this.tabs.size();
        if (position == 0 && this.selectedTabId == -1) {
            this.selectedTabId = id;
        }
        this.positionToId.put(position, id);
        this.positionToStableId.put(position, stableId);
        this.idToPosition.put(id, position);
        int i = this.selectedTabId;
        if (i != -1 && i == id) {
            this.currentPosition = position;
        }
        Tab tab = new Tab(id, text);
        tab.isDefault = isDefault;
        tab.isLocked = isLocked;
        this.allTabsWidth += tab.getWidth(true) + AndroidUtilities.dp(32.0f);
        this.tabs.add(tab);
    }

    public void finishAddingTabs(boolean animated) {
        this.listView.setItemAnimator(animated ? this.itemAnimator : null);
        this.adapter.notifyDataSetChanged();
    }

    public void animateColorsTo(String line, String active, String unactive, String selector, String background) {
        AnimatorSet animatorSet = this.colorChangeAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.aTabLineColorKey = line;
        this.aActiveTextColorKey = active;
        this.aUnactiveTextColorKey = unactive;
        this.aBackgroundColorKey = background;
        this.selectorColorKey = selector;
        this.listView.setSelectorDrawableColor(Theme.getColor(selector));
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.colorChangeAnimator = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, this.COLORS, 0.0f, 1.0f));
        this.colorChangeAnimator.setDuration(200L);
        this.colorChangeAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FilterTabsView.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FilterTabsView filterTabsView = FilterTabsView.this;
                filterTabsView.tabLineColorKey = filterTabsView.aTabLineColorKey;
                FilterTabsView filterTabsView2 = FilterTabsView.this;
                filterTabsView2.backgroundColorKey = filterTabsView2.aBackgroundColorKey;
                FilterTabsView filterTabsView3 = FilterTabsView.this;
                filterTabsView3.activeTextColorKey = filterTabsView3.aActiveTextColorKey;
                FilterTabsView filterTabsView4 = FilterTabsView.this;
                filterTabsView4.unactiveTextColorKey = filterTabsView4.aUnactiveTextColorKey;
                FilterTabsView.this.aTabLineColorKey = null;
                FilterTabsView.this.aActiveTextColorKey = null;
                FilterTabsView.this.aUnactiveTextColorKey = null;
                FilterTabsView.this.aBackgroundColorKey = null;
            }
        });
        this.colorChangeAnimator.start();
    }

    public int getCurrentTabId() {
        return this.selectedTabId;
    }

    public int getFirstTabId() {
        return this.positionToId.get(0, 0);
    }

    public String getSelectorColorKey() {
        return this.selectorColorKey;
    }

    public void updateTabsWidths() {
        this.positionToX.clear();
        this.positionToWidth.clear();
        int xOffset = AndroidUtilities.dp(7.0f);
        int N = this.tabs.size();
        for (int a = 0; a < N; a++) {
            int tabWidth = this.tabs.get(a).getWidth(false);
            this.positionToWidth.put(a, tabWidth);
            this.positionToX.put(a, (this.additionalTabWidth / 2) + xOffset);
            xOffset += AndroidUtilities.dp(32.0f) + tabWidth + this.additionalTabWidth;
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        float indicatorWidth;
        float indicatorX;
        int idx2;
        int idx1;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.listView) {
            int height = getMeasuredHeight();
            this.selectorDrawable.setAlpha((int) (this.listView.getAlpha() * 255.0f));
            float indicatorX2 = 0.0f;
            float indicatorWidth2 = 0.0f;
            if (!this.animatingIndicator && this.manualScrollingToPosition == -1) {
                RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.currentPosition);
                if (holder != null) {
                    TabView tabView = (TabView) holder.itemView;
                    indicatorWidth2 = Math.max(AndroidUtilities.dp(40.0f), tabView.animateTabWidth ? (tabView.animateFromTabWidth * (1.0f - tabView.changeProgress)) + (tabView.tabWidth * tabView.changeProgress) : tabView.tabWidth);
                    float viewWidth = tabView.animateTabWidth ? (tabView.animateFromWidth * (1.0f - tabView.changeProgress)) + (tabView.getMeasuredWidth() * tabView.changeProgress) : tabView.getMeasuredWidth();
                    indicatorX2 = (int) (tabView.getX() + ((viewWidth - indicatorWidth2) / 2.0f));
                }
            } else {
                int position = this.layoutManager.findFirstVisibleItemPosition();
                if (position == -1) {
                    indicatorX = 0.0f;
                    indicatorWidth = 0.0f;
                } else {
                    RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(position);
                    if (holder2 == null) {
                        indicatorX = 0.0f;
                        indicatorWidth = 0.0f;
                    } else {
                        if (this.animatingIndicator) {
                            idx1 = this.previousPosition;
                            idx2 = this.currentPosition;
                        } else {
                            idx1 = this.currentPosition;
                            idx2 = this.manualScrollingToPosition;
                        }
                        int prevX = this.positionToX.get(idx1);
                        int newX = this.positionToX.get(idx2);
                        int prevW = this.positionToWidth.get(idx1);
                        int newW = this.positionToWidth.get(idx2);
                        if (this.additionalTabWidth != 0) {
                            float indicatorX3 = this.animatingIndicatorProgress;
                            indicatorX2 = ((int) (prevX + ((newX - prevX) * indicatorX3))) + AndroidUtilities.dp(16.0f);
                        } else {
                            int x = this.positionToX.get(position);
                            float indicatorWidth3 = this.animatingIndicatorProgress;
                            indicatorX2 = (((int) (prevX + ((newX - prevX) * indicatorWidth3))) - (x - holder2.itemView.getLeft())) + AndroidUtilities.dp(16.0f);
                        }
                        indicatorWidth2 = (int) (prevW + ((newW - prevW) * this.animatingIndicatorProgress));
                    }
                }
                indicatorX2 = indicatorX;
                indicatorWidth2 = indicatorWidth;
            }
            if (indicatorWidth2 != 0.0f) {
                canvas.save();
                canvas.translate(this.listView.getTranslationX(), 0.0f);
                canvas.scale(this.listView.getScaleX(), 1.0f, this.listView.getPivotX() + this.listView.getX(), this.listView.getPivotY());
                this.selectorDrawable.setBounds((int) indicatorX2, height - AndroidUtilities.dpr(4.0f), (int) (indicatorX2 + indicatorWidth2), height);
                this.selectorDrawable.draw(canvas);
                canvas.restore();
            }
        }
        long newTime = SystemClock.elapsedRealtime();
        long dt = Math.min(17L, newTime - this.lastEditingAnimationTime);
        this.lastEditingAnimationTime = newTime;
        boolean invalidate = false;
        boolean z = this.isEditing;
        if (z || this.editingAnimationProgress != 0.0f) {
            boolean lessZero = true;
            boolean greaterZero = false;
            if (this.editingForwardAnimation) {
                float f = this.editingAnimationProgress;
                if (f > 0.0f) {
                    lessZero = false;
                }
                float f2 = f + (((float) dt) / 120.0f);
                this.editingAnimationProgress = f2;
                if (!z && lessZero && f2 >= 0.0f) {
                    this.editingAnimationProgress = 0.0f;
                }
                if (this.editingAnimationProgress >= 1.0f) {
                    this.editingAnimationProgress = 1.0f;
                    this.editingForwardAnimation = false;
                }
            } else {
                float f3 = this.editingAnimationProgress;
                if (f3 >= 0.0f) {
                    greaterZero = true;
                }
                float f4 = f3 - (((float) dt) / 120.0f);
                this.editingAnimationProgress = f4;
                if (!z && greaterZero && f4 <= 0.0f) {
                    this.editingAnimationProgress = 0.0f;
                }
                if (this.editingAnimationProgress <= -1.0f) {
                    this.editingAnimationProgress = -1.0f;
                    this.editingForwardAnimation = true;
                }
            }
            invalidate = true;
        }
        if (z) {
            float f5 = this.editingStartAnimationProgress;
            if (f5 < 1.0f) {
                float f6 = f5 + (((float) dt) / 180.0f);
                this.editingStartAnimationProgress = f6;
                if (f6 > 1.0f) {
                    this.editingStartAnimationProgress = 1.0f;
                }
                invalidate = true;
            }
        } else if (!z) {
            float f7 = this.editingStartAnimationProgress;
            if (f7 > 0.0f) {
                float f8 = f7 - (((float) dt) / 180.0f);
                this.editingStartAnimationProgress = f8;
                if (f8 < 0.0f) {
                    this.editingStartAnimationProgress = 0.0f;
                }
                invalidate = true;
            }
        }
        if (invalidate) {
            this.listView.invalidateViews();
            invalidate();
        }
        return result;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.tabs.isEmpty()) {
            int width = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
            Tab firstTab = findDefaultTab();
            firstTab.setTitle(LocaleController.getString("FilterAllChats", R.string.FilterAllChats));
            int tabWith = firstTab.getWidth(false);
            firstTab.setTitle(this.allTabsWidth > width ? LocaleController.getString("FilterAllChatsShort", R.string.FilterAllChatsShort) : LocaleController.getString("FilterAllChats", R.string.FilterAllChats));
            int trueTabsWidth = (this.allTabsWidth - tabWith) + firstTab.getWidth(false);
            int prevWidth = this.additionalTabWidth;
            int size = trueTabsWidth < width ? (width - trueTabsWidth) / this.tabs.size() : 0;
            this.additionalTabWidth = size;
            if (prevWidth != size) {
                this.ignoreLayout = true;
                RecyclerView.ItemAnimator animator = this.listView.getItemAnimator();
                this.listView.setItemAnimator(null);
                this.adapter.notifyDataSetChanged();
                this.listView.setItemAnimator(animator);
                this.ignoreLayout = false;
            }
            updateTabsWidths();
            this.invalidated = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private Tab findDefaultTab() {
        for (int i = 0; i < this.tabs.size(); i++) {
            if (this.tabs.get(i).isDefault) {
                return this.tabs.get(i);
            }
        }
        return null;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    private void scrollToChild(int position) {
        if (this.tabs.isEmpty() || this.scrollingToChild == position || position < 0 || position >= this.tabs.size()) {
            return;
        }
        this.scrollingToChild = position;
        this.listView.smoothScrollToPosition(position);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.prevLayoutWidth != r - l) {
            this.prevLayoutWidth = r - l;
            this.scrollingToChild = -1;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                FilterTabsViewDelegate filterTabsViewDelegate = this.delegate;
                if (filterTabsViewDelegate != null) {
                    filterTabsViewDelegate.onPageScrolled(1.0f);
                }
            }
        }
    }

    public void selectTabWithId(int id, float progress) {
        int position = this.idToPosition.get(id, -1);
        if (position < 0) {
            return;
        }
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        if (progress > 0.0f) {
            this.manualScrollingToPosition = position;
            this.manualScrollingToId = id;
        } else {
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
        }
        this.animatingIndicatorProgress = progress;
        this.listView.invalidateViews();
        invalidate();
        scrollToChild(position);
        if (progress >= 1.0f) {
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
            this.currentPosition = position;
            this.selectedTabId = id;
        }
    }

    private int getChildWidth(TextView child) {
        Layout layout = child.getLayout();
        if (layout != null) {
            int w = ((int) Math.ceil(layout.getLineWidth(0))) + AndroidUtilities.dp(2.0f);
            if (child.getCompoundDrawables()[2] != null) {
                return w + child.getCompoundDrawables()[2].getIntrinsicWidth() + AndroidUtilities.dp(6.0f);
            }
            return w;
        }
        return child.getMeasuredWidth();
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition == position) {
            return;
        }
        this.currentPosition = position;
        if (position >= this.tabs.size()) {
            return;
        }
        if (first == position && position > 1) {
            scrollToChild(position - 1);
        } else {
            scrollToChild(position);
        }
        invalidate();
    }

    public boolean isEditing() {
        return this.isEditing;
    }

    public void setIsEditing(boolean value) {
        this.isEditing = value;
        this.editingForwardAnimation = true;
        this.listView.invalidateViews();
        invalidate();
        if (!this.isEditing && this.orderChanged) {
            MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
            TLRPC.TL_messages_updateDialogFiltersOrder req = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> filters = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
            int N = filters.size();
            for (int a = 0; a < N; a++) {
                MessagesController.DialogFilter filter = filters.get(a);
                if (filter.isDefault()) {
                    req.order.add(0);
                } else {
                    req.order.add(Integer.valueOf(filter.id));
                }
            }
            int a2 = UserConfig.selectedAccount;
            MessagesController.getInstance(a2).lockFiltersInternal();
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, FilterTabsView$$ExternalSyntheticLambda0.INSTANCE);
            this.orderChanged = false;
        }
    }

    public static /* synthetic */ void lambda$setIsEditing$2(TLObject response, TLRPC.TL_error error) {
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0077  */
    /* JADX WARN: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void checkTabsCounter() {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            java.util.ArrayList<org.telegram.ui.Components.FilterTabsView$Tab> r2 = r11.tabs
            int r2 = r2.size()
        L8:
            if (r1 >= r2) goto L75
            java.util.ArrayList<org.telegram.ui.Components.FilterTabsView$Tab> r3 = r11.tabs
            java.lang.Object r3 = r3.get(r1)
            org.telegram.ui.Components.FilterTabsView$Tab r3 = (org.telegram.ui.Components.FilterTabsView.Tab) r3
            int r4 = r3.counter
            org.telegram.ui.Components.FilterTabsView$FilterTabsViewDelegate r5 = r11.delegate
            int r6 = r3.id
            int r5 = r5.getTabCounter(r6)
            if (r4 == r5) goto L72
            org.telegram.ui.Components.FilterTabsView$FilterTabsViewDelegate r4 = r11.delegate
            int r5 = r3.id
            int r4 = r4.getTabCounter(r5)
            if (r4 >= 0) goto L29
            goto L72
        L29:
            r0 = 1
            android.util.SparseIntArray r4 = r11.positionToWidth
            int r4 = r4.get(r1)
            r5 = 1
            int r6 = r3.getWidth(r5)
            if (r4 != r6) goto L3b
            boolean r7 = r11.invalidated
            if (r7 == 0) goto L72
        L3b:
            r11.invalidated = r5
            r11.requestLayout()
            r7 = 0
            r11.allTabsWidth = r7
            org.telegram.ui.Components.FilterTabsView$Tab r7 = r11.findDefaultTab()
            r8 = 2131625829(0x7f0e0765, float:1.8878877E38)
            java.lang.String r9 = "FilterAllChats"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7.setTitle(r8)
            r7 = 0
        L54:
            if (r7 >= r2) goto L71
            int r8 = r11.allTabsWidth
            java.util.ArrayList<org.telegram.ui.Components.FilterTabsView$Tab> r9 = r11.tabs
            java.lang.Object r9 = r9.get(r7)
            org.telegram.ui.Components.FilterTabsView$Tab r9 = (org.telegram.ui.Components.FilterTabsView.Tab) r9
            int r9 = r9.getWidth(r5)
            r10 = 1107296256(0x42000000, float:32.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            int r8 = r8 + r9
            r11.allTabsWidth = r8
            int r7 = r7 + 1
            goto L54
        L71:
            goto L75
        L72:
            int r1 = r1 + 1
            goto L8
        L75:
            if (r0 == 0) goto L83
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            androidx.recyclerview.widget.DefaultItemAnimator r2 = r11.itemAnimator
            r1.setItemAnimator(r2)
            org.telegram.ui.Components.FilterTabsView$ListAdapter r1 = r11.adapter
            r1.notifyDataSetChanged()
        L83:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FilterTabsView.checkTabsCounter():void");
    }

    public void notifyTabCounterChanged(int id) {
        int position = this.idToPosition.get(id, -1);
        if (position < 0 || position >= this.tabs.size()) {
            return;
        }
        Tab tab = this.tabs.get(position);
        if (tab.counter == this.delegate.getTabCounter(tab.id) || this.delegate.getTabCounter(tab.id) < 0) {
            return;
        }
        this.listView.invalidateViews();
        int oldWidth = this.positionToWidth.get(position);
        int width = tab.getWidth(true);
        if (oldWidth != width || this.invalidated) {
            this.invalidated = true;
            requestLayout();
            this.listView.setItemAnimator(this.itemAnimator);
            this.adapter.notifyDataSetChanged();
            this.allTabsWidth = 0;
            findDefaultTab().setTitle(LocaleController.getString("FilterAllChats", R.string.FilterAllChats));
            int N = this.tabs.size();
            for (int b = 0; b < N; b++) {
                this.allTabsWidth += this.tabs.get(b).getWidth(true) + AndroidUtilities.dp(32.0f);
            }
        }
    }

    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            FilterTabsView.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FilterTabsView.this.tabs.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            return FilterTabsView.this.positionToStableId.get(position);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new TabView(this.mContext));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TabView tabView = (TabView) holder.itemView;
            int oldId = tabView.currentTab != null ? tabView.getId() : -1;
            tabView.setTab((Tab) FilterTabsView.this.tabs.get(position), position);
            if (oldId != tabView.getId()) {
                tabView.progressToLocked = tabView.currentTab.isLocked ? 1.0f : 0.0f;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int count = FilterTabsView.this.tabs.size();
            if (fromIndex >= 0 && toIndex >= 0 && fromIndex < count) {
                if (toIndex >= count) {
                    return;
                }
                ArrayList<MessagesController.DialogFilter> filters = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
                MessagesController.DialogFilter filter1 = filters.get(fromIndex);
                MessagesController.DialogFilter filter2 = filters.get(toIndex);
                int temp = filter1.order;
                filter1.order = filter2.order;
                filter2.order = temp;
                filters.set(fromIndex, filter2);
                filters.set(toIndex, filter1);
                Tab tab1 = (Tab) FilterTabsView.this.tabs.get(fromIndex);
                Tab tab2 = (Tab) FilterTabsView.this.tabs.get(toIndex);
                int temp2 = tab1.id;
                tab1.id = tab2.id;
                tab2.id = temp2;
                int fromStableId = FilterTabsView.this.positionToStableId.get(fromIndex);
                int toStableId = FilterTabsView.this.positionToStableId.get(toIndex);
                FilterTabsView.this.positionToStableId.put(fromIndex, toStableId);
                FilterTabsView.this.positionToStableId.put(toIndex, fromStableId);
                FilterTabsViewDelegate filterTabsViewDelegate = FilterTabsView.this.delegate;
                int i = tab2.id;
                int idx1 = tab1.id;
                filterTabsViewDelegate.onPageReorder(i, idx1);
                if (FilterTabsView.this.currentPosition == fromIndex) {
                    FilterTabsView.this.currentPosition = toIndex;
                    FilterTabsView.this.selectedTabId = tab1.id;
                } else if (FilterTabsView.this.currentPosition == toIndex) {
                    FilterTabsView.this.currentPosition = fromIndex;
                    FilterTabsView.this.selectedTabId = tab2.id;
                }
                if (FilterTabsView.this.previousPosition == fromIndex) {
                    FilterTabsView.this.previousPosition = toIndex;
                    FilterTabsView.this.previousId = tab1.id;
                } else if (FilterTabsView.this.previousPosition == toIndex) {
                    FilterTabsView.this.previousPosition = fromIndex;
                    FilterTabsView.this.previousId = tab2.id;
                }
                FilterTabsView.this.tabs.set(fromIndex, tab2);
                FilterTabsView.this.tabs.set(toIndex, tab1);
                FilterTabsView.this.updateTabsWidths();
                FilterTabsView.this.orderChanged = true;
                FilterTabsView.this.listView.setItemAnimator(FilterTabsView.this.itemAnimator);
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    /* loaded from: classes5.dex */
    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
            FilterTabsView.this = this$0;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return FilterTabsView.this.isEditing;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!FilterTabsView.this.isEditing || (viewHolder.getAdapterPosition() == 0 && ((Tab) FilterTabsView.this.tabs.get(0)).isDefault && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium())) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(12, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if ((source.getAdapterPosition() != 0 && target.getAdapterPosition() != 0) || UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                FilterTabsView.this.adapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                FilterTabsView.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
                viewHolder.itemView.setBackgroundColor(Theme.getColor(FilterTabsView.this.backgroundColorKey));
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
            viewHolder.itemView.setBackground(null);
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public boolean currentTabIsDefault() {
        Tab defaultTab = findDefaultTab();
        return defaultTab != null && defaultTab.id == this.selectedTabId;
    }

    public int getDefaultTabId() {
        Tab defaultTab = findDefaultTab();
        if (defaultTab == null) {
            return -1;
        }
        return defaultTab.id;
    }

    public boolean isEmpty() {
        return this.tabs.isEmpty();
    }

    public boolean isFirstTabSelected() {
        return this.tabs.isEmpty() || this.selectedTabId == this.tabs.get(0).id;
    }

    public boolean isLocked(int id) {
        for (int i = 0; i < this.tabs.size(); i++) {
            if (this.tabs.get(i).id == id) {
                return this.tabs.get(i).isLocked;
            }
        }
        return false;
    }

    public void shakeLock(int id) {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof TabView) {
                TabView tabView = (TabView) this.listView.getChildAt(i);
                if (tabView.currentTab.id == id) {
                    tabView.shakeLockIcon(1.0f, 0);
                    tabView.performHapticFeedback(3);
                    return;
                }
            }
        }
    }
}
