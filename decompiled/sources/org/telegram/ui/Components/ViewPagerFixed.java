package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;
/* loaded from: classes5.dex */
public class ViewPagerFixed extends FrameLayout {
    private static final Interpolator interpolator = ViewPagerFixed$$ExternalSyntheticLambda0.INSTANCE;
    private Adapter adapter;
    private float additionalOffset;
    private boolean animatingForward;
    private boolean backAnimation;
    int currentPosition;
    private int maximumVelocity;
    private boolean maybeStartTracking;
    int nextPosition;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    TabsView tabsView;
    private VelocityTracker velocityTracker;
    protected SparseArray<View> viewsByType = new SparseArray<>();
    ValueAnimator.AnimatorUpdateListener updateTabProgress = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ViewPagerFixed.1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (ViewPagerFixed.this.tabsAnimationInProgress) {
                float scrollProgress = Math.abs(ViewPagerFixed.this.viewPages[0].getTranslationX()) / ViewPagerFixed.this.viewPages[0].getMeasuredWidth();
                if (ViewPagerFixed.this.tabsView != null) {
                    ViewPagerFixed.this.tabsView.selectTab(ViewPagerFixed.this.nextPosition, ViewPagerFixed.this.currentPosition, 1.0f - scrollProgress);
                }
            }
        }
    };
    private android.graphics.Rect rect = new android.graphics.Rect();
    private final float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
    private int[] viewTypes = new int[2];
    protected View[] viewPages = new View[2];

    public static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public ViewPagerFixed(Context context) {
        super(context);
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        setClipChildren(true);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        this.viewTypes[0] = adapter.getItemViewType(this.currentPosition);
        this.viewPages[0] = adapter.createView(this.viewTypes[0]);
        adapter.bindView(this.viewPages[0], this.currentPosition, this.viewTypes[0]);
        addView(this.viewPages[0]);
        this.viewPages[0].setVisibility(0);
        fillTabs();
    }

    public TabsView createTabsView() {
        TabsView tabsView = new TabsView(getContext());
        this.tabsView = tabsView;
        tabsView.setDelegate(new TabsView.TabsViewDelegate() { // from class: org.telegram.ui.Components.ViewPagerFixed.2
            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public void onPageSelected(int page, boolean forward) {
                ViewPagerFixed.this.animatingForward = forward;
                ViewPagerFixed.this.nextPosition = page;
                ViewPagerFixed.this.updateViewForIndex(1);
                if (forward) {
                    ViewPagerFixed.this.viewPages[1].setTranslationX(ViewPagerFixed.this.viewPages[0].getMeasuredWidth());
                } else {
                    ViewPagerFixed.this.viewPages[1].setTranslationX(-ViewPagerFixed.this.viewPages[0].getMeasuredWidth());
                }
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public void onPageScrolled(float progress) {
                if (progress == 1.0f) {
                    if (ViewPagerFixed.this.viewPages[1] != null) {
                        ViewPagerFixed.this.swapViews();
                        ViewPagerFixed.this.viewsByType.put(ViewPagerFixed.this.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                        ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                        viewPagerFixed.removeView(viewPagerFixed.viewPages[1]);
                        ViewPagerFixed.this.viewPages[0].setTranslationX(0.0f);
                        ViewPagerFixed.this.viewPages[1] = null;
                    }
                } else if (ViewPagerFixed.this.viewPages[1] != null) {
                    if (ViewPagerFixed.this.animatingForward) {
                        ViewPagerFixed.this.viewPages[1].setTranslationX(ViewPagerFixed.this.viewPages[0].getMeasuredWidth() * (1.0f - progress));
                        ViewPagerFixed.this.viewPages[0].setTranslationX((-ViewPagerFixed.this.viewPages[0].getMeasuredWidth()) * progress);
                        return;
                    }
                    ViewPagerFixed.this.viewPages[1].setTranslationX((-ViewPagerFixed.this.viewPages[0].getMeasuredWidth()) * (1.0f - progress));
                    ViewPagerFixed.this.viewPages[0].setTranslationX(ViewPagerFixed.this.viewPages[0].getMeasuredWidth() * progress);
                }
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public void onSamePageSelected() {
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public boolean canPerformActions() {
                return !ViewPagerFixed.this.tabsAnimationInProgress && !ViewPagerFixed.this.startedTracking;
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public void invalidateBlur() {
                ViewPagerFixed.this.invalidateBlur();
            }
        });
        fillTabs();
        return this.tabsView;
    }

    protected void invalidateBlur() {
    }

    public void updateViewForIndex(int index) {
        int adapterPosition = index == 0 ? this.currentPosition : this.nextPosition;
        if (this.viewPages[index] == null) {
            this.viewTypes[index] = this.adapter.getItemViewType(adapterPosition);
            View v = this.viewsByType.get(this.viewTypes[index]);
            if (v == null) {
                v = this.adapter.createView(this.viewTypes[index]);
            } else {
                this.viewsByType.remove(this.viewTypes[index]);
            }
            if (v.getParent() != null) {
                ViewGroup parent = (ViewGroup) v.getParent();
                parent.removeView(v);
            }
            addView(v);
            View[] viewArr = this.viewPages;
            viewArr[index] = v;
            this.adapter.bindView(viewArr[index], adapterPosition, this.viewTypes[index]);
            this.viewPages[index].setVisibility(0);
        } else if (this.viewTypes[index] == this.adapter.getItemViewType(adapterPosition)) {
            this.adapter.bindView(this.viewPages[index], adapterPosition, this.viewTypes[index]);
            this.viewPages[index].setVisibility(0);
        } else {
            this.viewsByType.put(this.viewTypes[index], this.viewPages[index]);
            this.viewPages[index].setVisibility(8);
            removeView(this.viewPages[index]);
            this.viewTypes[index] = this.adapter.getItemViewType(adapterPosition);
            View v2 = this.viewsByType.get(this.viewTypes[index]);
            if (v2 == null) {
                v2 = this.adapter.createView(this.viewTypes[index]);
            } else {
                this.viewsByType.remove(this.viewTypes[index]);
            }
            addView(v2);
            View[] viewArr2 = this.viewPages;
            viewArr2[index] = v2;
            viewArr2[index].setVisibility(0);
            Adapter adapter = this.adapter;
            adapter.bindView(this.viewPages[index], adapterPosition, adapter.getItemViewType(adapterPosition));
        }
    }

    private void fillTabs() {
        TabsView tabsView;
        if (this.adapter != null && (tabsView = this.tabsView) != null) {
            tabsView.removeTabs();
            for (int i = 0; i < this.adapter.getItemCount(); i++) {
                this.tabsView.addTab(this.adapter.getItemId(i), this.adapter.getItemTitle(i));
            }
        }
    }

    private boolean prepareForMoving(MotionEvent ev, boolean forward) {
        if ((!forward && this.currentPosition == 0) || (forward && this.currentPosition == this.adapter.getItemCount() - 1)) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) (ev.getX() + this.additionalOffset);
        TabsView tabsView = this.tabsView;
        if (tabsView != null) {
            tabsView.setEnabled(false);
        }
        this.animatingForward = forward;
        this.nextPosition = this.currentPosition + (forward ? 1 : -1);
        updateViewForIndex(1);
        if (forward) {
            View[] viewArr = this.viewPages;
            viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth());
        } else {
            View[] viewArr2 = this.viewPages;
            viewArr2[1].setTranslationX(-viewArr2[0].getMeasuredWidth());
        }
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        TabsView tabsView = this.tabsView;
        if (tabsView != null && tabsView.isAnimatingIndicator()) {
            return false;
        }
        if (checkTabsAnimationInProgress()) {
            return true;
        }
        onTouchEvent(ev);
        return this.startedTracking;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /* JADX WARN: Code restructure failed: missing block: B:136:0x028e, code lost:
        r10 = true;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r17) {
        /*
            Method dump skipped, instructions count: 1174
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void swapViews() {
        View[] viewArr = this.viewPages;
        View page = viewArr[0];
        viewArr[0] = viewArr[1];
        viewArr[1] = page;
        int p = this.currentPosition;
        int i = this.nextPosition;
        this.currentPosition = i;
        this.nextPosition = p;
        int[] iArr = this.viewTypes;
        int p2 = iArr[0];
        iArr[0] = iArr[1];
        iArr[1] = p2;
        onItemSelected(viewArr[0], viewArr[1], i, p);
    }

    public boolean checkTabsAnimationInProgress() {
        if (this.tabsAnimationInProgress) {
            boolean cancel = false;
            int i = -1;
            if (this.backAnimation) {
                if (Math.abs(this.viewPages[0].getTranslationX()) < 1.0f) {
                    this.viewPages[0].setTranslationX(0.0f);
                    View[] viewArr = this.viewPages;
                    View view = viewArr[1];
                    int measuredWidth = viewArr[0].getMeasuredWidth();
                    if (this.animatingForward) {
                        i = 1;
                    }
                    view.setTranslationX(measuredWidth * i);
                    cancel = true;
                }
            } else if (Math.abs(this.viewPages[1].getTranslationX()) < 1.0f) {
                View[] viewArr2 = this.viewPages;
                View view2 = viewArr2[0];
                int measuredWidth2 = viewArr2[0].getMeasuredWidth();
                if (!this.animatingForward) {
                    i = 1;
                }
                view2.setTranslationX(measuredWidth2 * i);
                this.viewPages[1].setTranslationX(0.0f);
                cancel = true;
            }
            if (cancel) {
                AnimatorSet animatorSet = this.tabsAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.tabsAnimation = null;
                }
                this.tabsAnimationInProgress = false;
            }
            return this.tabsAnimationInProgress;
        }
        return false;
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((f - 0.5f) * 0.47123894f);
    }

    public void setPosition(int position) {
        AnimatorSet animatorSet = this.tabsAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        View[] viewArr = this.viewPages;
        if (viewArr[1] != null) {
            this.viewsByType.put(this.viewTypes[1], viewArr[1]);
            removeView(this.viewPages[1]);
            this.viewPages[1] = null;
        }
        if (this.currentPosition != position) {
            int oldPosition = this.currentPosition;
            this.currentPosition = position;
            View oldView = this.viewPages[0];
            updateViewForIndex(0);
            onItemSelected(this.viewPages[0], oldView, this.currentPosition, oldPosition);
            this.viewPages[0].setTranslationX(0.0f);
            TabsView tabsView = this.tabsView;
            if (tabsView != null) {
                tabsView.selectTab(position, 0, 1.0f);
            }
        }
    }

    protected void onItemSelected(View currentPage, View oldPage, int position, int oldPosition) {
    }

    /* loaded from: classes5.dex */
    public static abstract class Adapter {
        public abstract void bindView(View view, int i, int i2);

        public abstract View createView(int i);

        public abstract int getItemCount();

        public int getItemId(int position) {
            return position;
        }

        public String getItemTitle(int position) {
            return "";
        }

        public int getItemViewType(int position) {
            return 0;
        }
    }

    @Override // android.view.View
    public boolean canScrollHorizontally(int direction) {
        if (direction == 0) {
            return false;
        }
        if (this.tabsAnimationInProgress || this.startedTracking) {
            return true;
        }
        boolean forward = direction > 0;
        return (forward || this.currentPosition != 0) && (!forward || this.currentPosition != this.adapter.getItemCount() - 1);
    }

    public View getCurrentView() {
        return this.viewPages[0];
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    /* loaded from: classes5.dex */
    public static class TabsView extends FrameLayout {
        private ListAdapter adapter;
        private int additionalTabWidth;
        private int allTabsWidth;
        private boolean animatingIndicator;
        private float animatingIndicatorProgress;
        private boolean animationRunning;
        private float animationTime;
        private float animationValue;
        private boolean commitCrossfade;
        private float crossfadeAlpha;
        private Bitmap crossfadeBitmap;
        private int currentPosition;
        private TabsViewDelegate delegate;
        private float editingAnimationProgress;
        private boolean editingForwardAnimation;
        private float editingStartAnimationProgress;
        private float hideProgress;
        private boolean ignoreLayout;
        private boolean invalidated;
        private boolean isEditing;
        private boolean isInHiddenMode;
        private long lastAnimationTime;
        private long lastEditingAnimationTime;
        private LinearLayoutManager layoutManager;
        private RecyclerListView listView;
        private boolean orderChanged;
        private int prevLayoutWidth;
        private int previousId;
        private int previousPosition;
        ValueAnimator tabsAnimator;
        private TextPaint textPaint = new TextPaint(1);
        private TextPaint textCounterPaint = new TextPaint(1);
        private Paint deletePaint = new TextPaint(1);
        private Paint counterPaint = new Paint(1);
        private ArrayList<Tab> tabs = new ArrayList<>();
        private Paint crossfadePaint = new Paint();
        private int selectedTabId = -1;
        private int manualScrollingToPosition = -1;
        private int manualScrollingToId = -1;
        private int scrollingToChild = -1;
        private String tabLineColorKey = Theme.key_profile_tabSelectedLine;
        private String activeTextColorKey = Theme.key_profile_tabSelectedText;
        private String unactiveTextColorKey = Theme.key_profile_tabText;
        private String selectorColorKey = Theme.key_profile_tabSelector;
        private String backgroundColorKey = Theme.key_actionBarDefault;
        private CubicBezierInterpolator interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        private SparseIntArray positionToId = new SparseIntArray(5);
        private SparseIntArray idToPosition = new SparseIntArray(5);
        private SparseIntArray positionToWidth = new SparseIntArray(5);
        private SparseIntArray positionToX = new SparseIntArray(5);
        private Runnable animationRunnable = new Runnable() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.1
            @Override // java.lang.Runnable
            public void run() {
                if (!TabsView.this.animatingIndicator) {
                    return;
                }
                long newTime = SystemClock.elapsedRealtime();
                long dt = newTime - TabsView.this.lastAnimationTime;
                if (dt > 17) {
                    dt = 17;
                }
                TabsView.access$2716(TabsView.this, ((float) dt) / 200.0f);
                TabsView tabsView = TabsView.this;
                tabsView.setAnimationIdicatorProgress(tabsView.interpolator.getInterpolation(TabsView.this.animationTime));
                if (TabsView.this.animationTime > 1.0f) {
                    TabsView.this.animationTime = 1.0f;
                }
                if (TabsView.this.animationTime < 1.0f) {
                    AndroidUtilities.runOnUIThread(TabsView.this.animationRunnable);
                    return;
                }
                TabsView.this.animatingIndicator = false;
                TabsView.this.setEnabled(true);
                if (TabsView.this.delegate != null) {
                    TabsView.this.delegate.onPageScrolled(1.0f);
                }
            }
        };
        private GradientDrawable selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null);

        /* loaded from: classes5.dex */
        public interface TabsViewDelegate {
            boolean canPerformActions();

            void invalidateBlur();

            void onPageScrolled(float f);

            void onPageSelected(int i, boolean z);

            void onSamePageSelected();
        }

        static /* synthetic */ float access$2716(TabsView x0, float x1) {
            float f = x0.animationTime + x1;
            x0.animationTime = f;
            return f;
        }

        /* loaded from: classes5.dex */
        public static class Tab {
            public int counter;
            public int id;
            public String title;
            public int titleWidth;

            public Tab(int i, String t) {
                this.id = i;
                this.title = t;
            }

            public int getWidth(boolean store, TextPaint textPaint) {
                int width = (int) Math.ceil(textPaint.measureText(this.title));
                this.titleWidth = width;
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
            private int currentPosition;
            private Tab currentTab;
            private String currentText;
            private RectF rect = new RectF();
            private int tabWidth;
            private int textHeight;
            private StaticLayout textLayout;
            private int textOffsetX;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public TabView(Context context) {
                super(context);
                TabsView.this = this$0;
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
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int w = this.currentTab.getWidth(false, TabsView.this.textPaint) + AndroidUtilities.dp(32.0f) + TabsView.this.additionalTabWidth;
                setMeasuredDimension(w, View.MeasureSpec.getSize(heightMeasureSpec));
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int id2;
                int id1;
                String unreadOtherKey;
                String unreadKey;
                String otherKey;
                String key;
                String counterText;
                int counterWidth;
                int countWidth;
                int countWidth2;
                int i;
                if (this.currentTab.id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.save();
                    float p = TabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                    canvas.translate(AndroidUtilities.dp(0.66f) * p, 0.0f);
                    canvas.rotate(p, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                }
                if (TabsView.this.manualScrollingToId != -1) {
                    id1 = TabsView.this.manualScrollingToId;
                    id2 = TabsView.this.selectedTabId;
                } else {
                    id1 = TabsView.this.selectedTabId;
                    id2 = TabsView.this.previousId;
                }
                if (this.currentTab.id == id1) {
                    String key2 = TabsView.this.activeTextColorKey;
                    String otherKey2 = TabsView.this.unactiveTextColorKey;
                    key = key2;
                    otherKey = otherKey2;
                    unreadKey = Theme.key_chats_tabUnreadActiveBackground;
                    unreadOtherKey = Theme.key_chats_tabUnreadUnactiveBackground;
                } else {
                    String key3 = TabsView.this.unactiveTextColorKey;
                    String otherKey3 = TabsView.this.activeTextColorKey;
                    key = key3;
                    otherKey = otherKey3;
                    unreadKey = Theme.key_chats_tabUnreadUnactiveBackground;
                    unreadOtherKey = Theme.key_chats_tabUnreadActiveBackground;
                }
                if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToId != -1) && (this.currentTab.id == id1 || this.currentTab.id == id2)) {
                    TabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(otherKey), Theme.getColor(key), TabsView.this.animatingIndicatorProgress));
                } else {
                    TabsView.this.textPaint.setColor(Theme.getColor(key));
                }
                if (this.currentTab.counter > 0) {
                    String counterText2 = String.format("%d", Integer.valueOf(this.currentTab.counter));
                    int counterWidth2 = (int) Math.ceil(TabsView.this.textCounterPaint.measureText(counterText2));
                    counterWidth = counterWidth2;
                    counterText = counterText2;
                    countWidth = Math.max(AndroidUtilities.dp(10.0f), counterWidth2) + AndroidUtilities.dp(10.0f);
                } else {
                    counterWidth = 0;
                    counterText = null;
                    countWidth = 0;
                }
                if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                    countWidth2 = (int) (countWidth + ((AndroidUtilities.dp(20.0f) - countWidth) * TabsView.this.editingStartAnimationProgress));
                } else {
                    countWidth2 = countWidth;
                }
                int i2 = this.currentTab.titleWidth;
                if (countWidth2 != 0) {
                    i = AndroidUtilities.dp((counterText != null ? 1.0f : TabsView.this.editingStartAnimationProgress) * 6.0f) + countWidth2;
                } else {
                    i = 0;
                }
                this.tabWidth = i2 + i;
                int textX = (getMeasuredWidth() - this.tabWidth) / 2;
                if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                    String str = this.currentTab.title;
                    this.currentText = str;
                    CharSequence text = Emoji.replaceEmoji(str, TabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    StaticLayout staticLayout = new StaticLayout(text, TabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.textLayout = staticLayout;
                    this.textHeight = staticLayout.getHeight();
                    this.textOffsetX = (int) (-this.textLayout.getLineLeft(0));
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate(this.textOffsetX + textX, ((getMeasuredHeight() - this.textHeight) / 2) + 1);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (counterText != null || (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f))) {
                    TabsView.this.textCounterPaint.setColor(Theme.getColor(TabsView.this.backgroundColorKey));
                    if (!Theme.hasThemeKey(unreadKey) || !Theme.hasThemeKey(unreadOtherKey)) {
                        TabsView.this.counterPaint.setColor(TabsView.this.textPaint.getColor());
                    } else {
                        int color1 = Theme.getColor(unreadKey);
                        if ((!TabsView.this.animatingIndicator && TabsView.this.manualScrollingToPosition == -1) || (this.currentTab.id != id1 && this.currentTab.id != id2)) {
                            TabsView.this.counterPaint.setColor(color1);
                        } else {
                            int color3 = Theme.getColor(unreadOtherKey);
                            TabsView.this.counterPaint.setColor(ColorUtils.blendARGB(color3, color1, TabsView.this.animatingIndicatorProgress));
                        }
                    }
                    int x = this.currentTab.titleWidth + textX + AndroidUtilities.dp(6.0f);
                    int countTop = (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2;
                    if (this.currentTab.id == Integer.MAX_VALUE || ((!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f) || counterText != null)) {
                        TabsView.this.counterPaint.setAlpha(255);
                    } else {
                        TabsView.this.counterPaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                    }
                    this.rect.set(x, countTop, x + countWidth2, countTop + AndroidUtilities.dp(20.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, TabsView.this.counterPaint);
                    if (counterText != null) {
                        if (this.currentTab.id != Integer.MAX_VALUE) {
                            TabsView.this.textCounterPaint.setAlpha((int) ((1.0f - TabsView.this.editingStartAnimationProgress) * 255.0f));
                        }
                        canvas.drawText(counterText, this.rect.left + ((this.rect.width() - counterWidth) / 2.0f), AndroidUtilities.dp(14.5f) + countTop, TabsView.this.textCounterPaint);
                    }
                    if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                        TabsView.this.deletePaint.setColor(TabsView.this.textCounterPaint.getColor());
                        TabsView.this.deletePaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                        int side = AndroidUtilities.dp(3.0f);
                        canvas.drawLine(this.rect.centerX() - side, this.rect.centerY() - side, side + this.rect.centerX(), this.rect.centerY() + side, TabsView.this.deletePaint);
                        canvas.drawLine(this.rect.centerX() - side, side + this.rect.centerY(), side + this.rect.centerX(), this.rect.centerY() - side, TabsView.this.deletePaint);
                    }
                }
                if (this.currentTab.id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.restore();
                }
            }

            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setSelected((this.currentTab == null || TabsView.this.selectedTabId == -1 || this.currentTab.id != TabsView.this.selectedTabId) ? false : true);
            }
        }

        public TabsView(Context context) {
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
            RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.2
                @Override // android.view.ViewGroup
                public void addView(View child, int index, ViewGroup.LayoutParams params) {
                    super.addView(child, index, params);
                    if (TabsView.this.isInHiddenMode) {
                        child.setScaleX(0.3f);
                        child.setScaleY(0.3f);
                        child.setAlpha(0.0f);
                        return;
                    }
                    child.setScaleX(1.0f);
                    child.setScaleY(1.0f);
                    child.setAlpha(1.0f);
                }

                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    TabsView.this.invalidate();
                }

                @Override // org.telegram.ui.Components.RecyclerListView
                public boolean canHighlightChildAt(View child, float x, float y) {
                    if (TabsView.this.isEditing) {
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
            ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setDelayAnimations(false);
            this.listView.setSelectorType(8);
            this.listView.setSelectorRadius(6);
            this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
            RecyclerListView recyclerListView2 = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false) { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.3
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.3.1
                        @Override // androidx.recyclerview.widget.LinearSmoothScroller, androidx.recyclerview.widget.RecyclerView.SmoothScroller
                        protected void onTargetFound(View targetView, RecyclerView.State state2, RecyclerView.SmoothScroller.Action action) {
                            int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
                            if (dx > 0 || (dx == 0 && targetView.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                                dx += AndroidUtilities.dp(60.0f);
                            } else if (dx < 0 || (dx == 0 && targetView.getRight() + AndroidUtilities.dp(21.0f) > TabsView.this.getMeasuredWidth())) {
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

                @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(recycler, state, info);
                    if (TabsView.this.isInHiddenMode) {
                        info.setVisibleToUser(false);
                    }
                }
            };
            this.layoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
            this.listView.setClipToPadding(false);
            this.listView.setDrawSelectorBehind(true);
            RecyclerListView recyclerListView3 = this.listView;
            ListAdapter listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            recyclerListView3.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.Components.ViewPagerFixed$TabsView$$ExternalSyntheticLambda1
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
                    ViewPagerFixed.TabsView.this.m3207lambda$new$0$orgtelegramuiComponentsViewPagerFixed$TabsView(view, i, f, f2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.4
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    TabsView.this.invalidate();
                }
            });
            addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ViewPagerFixed$TabsView */
        public /* synthetic */ void m3207lambda$new$0$orgtelegramuiComponentsViewPagerFixed$TabsView(View view, int position, float x, float y) {
            TabsViewDelegate tabsViewDelegate;
            if (!this.delegate.canPerformActions()) {
                return;
            }
            TabView tabView = (TabView) view;
            if (position != this.currentPosition || (tabsViewDelegate = this.delegate) == null) {
                scrollToTab(tabView.currentTab.id, position);
            } else {
                tabsViewDelegate.onSamePageSelected();
            }
        }

        public void setDelegate(TabsViewDelegate filterTabsViewDelegate) {
            this.delegate = filterTabsViewDelegate;
        }

        public boolean isAnimatingIndicator() {
            return this.animatingIndicator;
        }

        public void scrollToTab(int id, int position) {
            int i = this.currentPosition;
            boolean scrollingForward = i < position;
            this.scrollingToChild = -1;
            this.previousPosition = i;
            this.previousId = this.selectedTabId;
            this.currentPosition = position;
            this.selectedTabId = id;
            ValueAnimator valueAnimator = this.tabsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.animatingIndicator) {
                this.animatingIndicator = false;
            }
            this.animationTime = 0.0f;
            this.animatingIndicatorProgress = 0.0f;
            this.animatingIndicator = true;
            setEnabled(false);
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageSelected(id, scrollingForward);
            }
            scrollToChild(position);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.tabsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    float progress = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    TabsView.this.setAnimationIdicatorProgress(progress);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(progress);
                    }
                }
            });
            this.tabsAnimator.setDuration(250L);
            this.tabsAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.tabsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    TabsView.this.animatingIndicator = false;
                    TabsView.this.setEnabled(true);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(1.0f);
                    }
                    TabsView.this.invalidate();
                }
            });
            this.tabsAnimator.start();
        }

        public void setAnimationIdicatorProgress(float value) {
            this.animatingIndicatorProgress = value;
            this.listView.invalidateViews();
            invalidate();
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(value);
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

        public void addTab(int id, String text) {
            int position = this.tabs.size();
            if (position == 0 && this.selectedTabId == -1) {
                this.selectedTabId = id;
            }
            this.positionToId.put(position, id);
            this.idToPosition.put(id, position);
            int i = this.selectedTabId;
            if (i != -1 && i == id) {
                this.currentPosition = position;
            }
            Tab tab = new Tab(id, text);
            this.allTabsWidth += tab.getWidth(true, this.textPaint) + AndroidUtilities.dp(32.0f);
            this.tabs.add(tab);
        }

        public void removeTabs() {
            this.tabs.clear();
            this.positionToId.clear();
            this.idToPosition.clear();
            this.positionToWidth.clear();
            this.positionToX.clear();
            this.allTabsWidth = 0;
        }

        public void finishAddingTabs() {
            this.adapter.notifyDataSetChanged();
        }

        public int getCurrentTabId() {
            return this.selectedTabId;
        }

        public int getFirstTabId() {
            return this.positionToId.get(0, 0);
        }

        private void updateTabsWidths() {
            this.positionToX.clear();
            this.positionToWidth.clear();
            int xOffset = AndroidUtilities.dp(7.0f);
            int N = this.tabs.size();
            for (int a = 0; a < N; a++) {
                int tabWidth = this.tabs.get(a).getWidth(false, this.textPaint);
                this.positionToWidth.put(a, tabWidth);
                this.positionToX.put(a, (this.additionalTabWidth / 2) + xOffset);
                xOffset += AndroidUtilities.dp(32.0f) + tabWidth + this.additionalTabWidth;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:29:0x009d  */
        /* JADX WARN: Removed duplicated region for block: B:41:0x011f  */
        /* JADX WARN: Removed duplicated region for block: B:44:0x012c  */
        /* JADX WARN: Removed duplicated region for block: B:47:0x015c  */
        @Override // android.view.ViewGroup
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected boolean drawChild(android.graphics.Canvas r21, android.view.View r22, long r23) {
            /*
                Method dump skipped, instructions count: 369
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.TabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (!this.tabs.isEmpty()) {
                int width = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
                int prevWidth = this.additionalTabWidth;
                int i = this.allTabsWidth;
                int size = i < width ? (width - i) / this.tabs.size() : 0;
                this.additionalTabWidth = size;
                if (prevWidth != size) {
                    this.ignoreLayout = true;
                    this.adapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
                updateTabsWidths();
                this.invalidated = false;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void updateColors() {
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
            this.listView.invalidateViews();
            this.listView.invalidate();
            invalidate();
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
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.prevLayoutWidth != r - l) {
                this.prevLayoutWidth = r - l;
                this.scrollingToChild = -1;
                if (this.animatingIndicator) {
                    AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                    this.animatingIndicator = false;
                    setEnabled(true);
                    TabsViewDelegate tabsViewDelegate = this.delegate;
                    if (tabsViewDelegate != null) {
                        tabsViewDelegate.onPageScrolled(1.0f);
                    }
                }
            }
        }

        public void selectTab(int currentPosition, int nextPosition, float progress) {
            if (progress < 0.0f) {
                progress = 0.0f;
            } else if (progress > 1.0f) {
                progress = 1.0f;
            }
            this.currentPosition = currentPosition;
            this.selectedTabId = this.positionToId.get(currentPosition);
            if (progress > 0.0f) {
                this.manualScrollingToPosition = nextPosition;
                this.manualScrollingToId = this.positionToId.get(nextPosition);
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = progress;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(currentPosition);
            if (progress >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = nextPosition;
                this.selectedTabId = this.positionToId.get(nextPosition);
            }
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.invalidateBlur();
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
                    filters.get(a);
                    req.order.add(Integer.valueOf(filters.get(a).id));
                }
                int a2 = UserConfig.selectedAccount;
                ConnectionsManager.getInstance(a2).sendRequest(req, ViewPagerFixed$TabsView$$ExternalSyntheticLambda0.INSTANCE);
                this.orderChanged = false;
            }
        }

        public static /* synthetic */ void lambda$setIsEditing$1(TLObject response, TLRPC.TL_error error) {
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes5.dex */
        public class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context mContext;

            public ListAdapter(Context context) {
                TabsView.this = r1;
                this.mContext = context;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return TabsView.this.tabs.size();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public long getItemId(int i) {
                return i;
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
                tabView.setTab((Tab) TabsView.this.tabs.get(position), position);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                return 0;
            }
        }

        public void hide(boolean hide, boolean animated) {
            this.isInHiddenMode = hide;
            float f = 1.0f;
            if (animated) {
                for (int i = 0; i < this.listView.getChildCount(); i++) {
                    this.listView.getChildAt(i).animate().alpha(hide ? 0.0f : 1.0f).scaleX(hide ? 0.0f : 1.0f).scaleY(hide ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(220L).start();
                }
            } else {
                for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                    View v = this.listView.getChildAt(i2);
                    v.setScaleX(hide ? 0.0f : 1.0f);
                    v.setScaleY(hide ? 0.0f : 1.0f);
                    v.setAlpha(hide ? 0.0f : 1.0f);
                }
                if (!hide) {
                    f = 0.0f;
                }
                this.hideProgress = f;
            }
            invalidate();
        }
    }

    private View findScrollingChild(ViewGroup parent, float x, float y) {
        View v;
        int n = parent.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(this.rect);
                if (!this.rect.contains((int) x, (int) y)) {
                    continue;
                } else if (child.canScrollHorizontally(-1)) {
                    return child;
                } else {
                    if ((child instanceof ViewGroup) && (v = findScrollingChild((ViewGroup) child, x - this.rect.left, y - this.rect.top)) != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    public void drawForBlur(Canvas blurCanvas) {
        RecyclerListView recyclerListView;
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPages;
            if (i < viewArr.length) {
                if (viewArr[i] != null && viewArr[i].getVisibility() == 0 && (recyclerListView = findRecyclerView(this.viewPages[i])) != null) {
                    for (int j = 0; j < recyclerListView.getChildCount(); j++) {
                        View child = recyclerListView.getChildAt(j);
                        if (child.getY() < AndroidUtilities.dp(203.0f) + AndroidUtilities.dp(100.0f)) {
                            int restore = blurCanvas.save();
                            blurCanvas.translate(this.viewPages[i].getX(), getY() + this.viewPages[i].getY() + recyclerListView.getY() + child.getY());
                            child.draw(blurCanvas);
                            blurCanvas.restoreToCount(restore);
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    private RecyclerListView findRecyclerView(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof RecyclerListView) {
                    return (RecyclerListView) child;
                }
                if (child instanceof ViewGroup) {
                    findRecyclerView(child);
                }
            }
            return null;
        }
        return null;
    }
}
