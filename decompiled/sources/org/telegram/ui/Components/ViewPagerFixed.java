package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFiltersOrder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;
/* loaded from: classes3.dex */
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
                float abs = Math.abs(ViewPagerFixed.this.viewPages[0].getTranslationX()) / ViewPagerFixed.this.viewPages[0].getMeasuredWidth();
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                TabsView tabsView = viewPagerFixed.tabsView;
                if (tabsView == null) {
                    return;
                }
                tabsView.selectTab(viewPagerFixed.nextPosition, viewPagerFixed.currentPosition, 1.0f - abs);
            }
        }
    };
    private android.graphics.Rect rect = new android.graphics.Rect();
    private final float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
    private int[] viewTypes = new int[2];
    protected View[] viewPages = new View[2];

    /* loaded from: classes3.dex */
    public static abstract class Adapter {
        public abstract void bindView(View view, int i, int i2);

        public abstract View createView(int i);

        public abstract int getItemCount();

        public int getItemId(int i) {
            return i;
        }

        public abstract String getItemTitle(int i);

        public abstract int getItemViewType(int i);
    }

    public static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    protected void invalidateBlur() {
    }

    protected void onItemSelected(View view, View view2, int i, int i2) {
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
            public void onSamePageSelected() {
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public void onPageSelected(int i, boolean z) {
                ViewPagerFixed.this.animatingForward = z;
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                viewPagerFixed.nextPosition = i;
                viewPagerFixed.updateViewForIndex(1);
                if (z) {
                    View[] viewArr = ViewPagerFixed.this.viewPages;
                    viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth());
                    return;
                }
                View[] viewArr2 = ViewPagerFixed.this.viewPages;
                viewArr2[1].setTranslationX(-viewArr2[0].getMeasuredWidth());
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.TabsView.TabsViewDelegate
            public void onPageScrolled(float f) {
                if (f == 1.0f) {
                    ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                    if (viewPagerFixed.viewPages[1] == null) {
                        return;
                    }
                    viewPagerFixed.swapViews();
                    ViewPagerFixed viewPagerFixed2 = ViewPagerFixed.this;
                    viewPagerFixed2.viewsByType.put(viewPagerFixed2.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                    ViewPagerFixed viewPagerFixed3 = ViewPagerFixed.this;
                    viewPagerFixed3.removeView(viewPagerFixed3.viewPages[1]);
                    ViewPagerFixed.this.viewPages[0].setTranslationX(0.0f);
                    ViewPagerFixed.this.viewPages[1] = null;
                    return;
                }
                ViewPagerFixed viewPagerFixed4 = ViewPagerFixed.this;
                if (viewPagerFixed4.viewPages[1] == null) {
                    return;
                }
                if (viewPagerFixed4.animatingForward) {
                    View[] viewArr = ViewPagerFixed.this.viewPages;
                    viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth() * (1.0f - f));
                    View[] viewArr2 = ViewPagerFixed.this.viewPages;
                    viewArr2[0].setTranslationX((-viewArr2[0].getMeasuredWidth()) * f);
                    return;
                }
                View[] viewArr3 = ViewPagerFixed.this.viewPages;
                viewArr3[1].setTranslationX((-viewArr3[0].getMeasuredWidth()) * (1.0f - f));
                View[] viewArr4 = ViewPagerFixed.this.viewPages;
                viewArr4[0].setTranslationX(viewArr4[0].getMeasuredWidth() * f);
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

    public void updateViewForIndex(int i) {
        int i2 = i == 0 ? this.currentPosition : this.nextPosition;
        if (this.viewPages[i] == null) {
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view = this.viewsByType.get(this.viewTypes[i]);
            if (view == null) {
                view = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            addView(view);
            View[] viewArr = this.viewPages;
            viewArr[i] = view;
            this.adapter.bindView(viewArr[i], i2, this.viewTypes[i]);
            this.viewPages[i].setVisibility(0);
        } else if (this.viewTypes[i] == this.adapter.getItemViewType(i2)) {
            this.adapter.bindView(this.viewPages[i], i2, this.viewTypes[i]);
            this.viewPages[i].setVisibility(0);
        } else {
            this.viewsByType.put(this.viewTypes[i], this.viewPages[i]);
            this.viewPages[i].setVisibility(8);
            removeView(this.viewPages[i]);
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view2 = this.viewsByType.get(this.viewTypes[i]);
            if (view2 == null) {
                view2 = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            addView(view2);
            View[] viewArr2 = this.viewPages;
            viewArr2[i] = view2;
            viewArr2[i].setVisibility(0);
            Adapter adapter = this.adapter;
            adapter.bindView(this.viewPages[i], i2, adapter.getItemViewType(i2));
        }
    }

    private void fillTabs() {
        TabsView tabsView;
        if (this.adapter == null || (tabsView = this.tabsView) == null) {
            return;
        }
        tabsView.removeTabs();
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            this.tabsView.addTab(this.adapter.getItemId(i), this.adapter.getItemTitle(i));
        }
    }

    private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
        if ((z || this.currentPosition != 0) && (!z || this.currentPosition != this.adapter.getItemCount() - 1)) {
            getParent().requestDisallowInterceptTouchEvent(true);
            this.maybeStartTracking = false;
            this.startedTracking = true;
            this.startedTrackingX = (int) (motionEvent.getX() + this.additionalOffset);
            TabsView tabsView = this.tabsView;
            if (tabsView != null) {
                tabsView.setEnabled(false);
            }
            this.animatingForward = z;
            this.nextPosition = this.currentPosition + (z ? 1 : -1);
            updateViewForIndex(1);
            if (z) {
                View[] viewArr = this.viewPages;
                viewArr[1].setTranslationX(viewArr[0].getMeasuredWidth());
            } else {
                View[] viewArr2 = this.viewPages;
                viewArr2[1].setTranslationX(-viewArr2[0].getMeasuredWidth());
            }
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        TabsView tabsView = this.tabsView;
        if (tabsView == null || !tabsView.isAnimatingIndicator()) {
            if (checkTabsAnimationInProgress()) {
                return true;
            }
            onTouchEvent(motionEvent);
            return this.startedTracking;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    /* JADX WARN: Code restructure failed: missing block: B:135:0x0284, code lost:
        r6 = true;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r13) {
        /*
            Method dump skipped, instructions count: 1142
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void swapViews() {
        View[] viewArr = this.viewPages;
        View view = viewArr[0];
        viewArr[0] = viewArr[1];
        viewArr[1] = view;
        int i = this.currentPosition;
        int i2 = this.nextPosition;
        this.currentPosition = i2;
        this.nextPosition = i;
        int[] iArr = this.viewTypes;
        int i3 = iArr[0];
        iArr[0] = iArr[1];
        iArr[1] = i3;
        onItemSelected(viewArr[0], viewArr[1], i2, i);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x006c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean checkTabsAnimationInProgress() {
        /*
            r7 = this;
            boolean r0 = r7.tabsAnimationInProgress
            r1 = 0
            if (r0 == 0) goto L7b
            boolean r0 = r7.backAnimation
            r2 = -1
            r3 = 0
            r4 = 1065353216(0x3f800000, float:1.0)
            r5 = 1
            if (r0 == 0) goto L3b
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r1]
            float r0 = r0.getTranslationX()
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L69
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r1]
            r0.setTranslationX(r3)
            android.view.View[] r0 = r7.viewPages
            r3 = r0[r5]
            r0 = r0[r1]
            int r0 = r0.getMeasuredWidth()
            boolean r4 = r7.animatingForward
            if (r4 == 0) goto L34
            r2 = 1
        L34:
            int r0 = r0 * r2
            float r0 = (float) r0
            r3.setTranslationX(r0)
            goto L6a
        L3b:
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r5]
            float r0 = r0.getTranslationX()
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L69
            android.view.View[] r0 = r7.viewPages
            r4 = r0[r1]
            r0 = r0[r1]
            int r0 = r0.getMeasuredWidth()
            boolean r6 = r7.animatingForward
            if (r6 == 0) goto L5a
            goto L5b
        L5a:
            r2 = 1
        L5b:
            int r0 = r0 * r2
            float r0 = (float) r0
            r4.setTranslationX(r0)
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r5]
            r0.setTranslationX(r3)
            goto L6a
        L69:
            r5 = 0
        L6a:
            if (r5 == 0) goto L78
            android.animation.AnimatorSet r0 = r7.tabsAnimation
            if (r0 == 0) goto L76
            r0.cancel()
            r0 = 0
            r7.tabsAnimation = r0
        L76:
            r7.tabsAnimationInProgress = r1
        L78:
            boolean r0 = r7.tabsAnimationInProgress
            return r0
        L7b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.checkTabsAnimationInProgress():boolean");
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((f - 0.5f) * 0.47123894f);
    }

    public void setPosition(int i) {
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
        int i2 = this.currentPosition;
        if (i2 != i) {
            this.currentPosition = i;
            View view = this.viewPages[0];
            updateViewForIndex(0);
            onItemSelected(this.viewPages[0], view, this.currentPosition, i2);
            this.viewPages[0].setTranslationX(0.0f);
            TabsView tabsView = this.tabsView;
            if (tabsView == null) {
                return;
            }
            tabsView.selectTab(i, 0, 1.0f);
        }
    }

    @Override // android.view.View
    public boolean canScrollHorizontally(int i) {
        if (i == 0) {
            return false;
        }
        if (!this.tabsAnimationInProgress && !this.startedTracking) {
            boolean z = i > 0;
            if ((!z && this.currentPosition == 0) || (z && this.currentPosition == this.adapter.getItemCount() - 1)) {
                return false;
            }
        }
        return true;
    }

    public View getCurrentView() {
        return this.viewPages[0];
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    /* loaded from: classes3.dex */
    public static class TabsView extends FrameLayout {
        private ListAdapter adapter;
        private int additionalTabWidth;
        private int allTabsWidth;
        private boolean animatingIndicator;
        private float animatingIndicatorProgress;
        private float animationTime;
        private float crossfadeAlpha;
        private Bitmap crossfadeBitmap;
        private int currentPosition;
        private TabsViewDelegate delegate;
        private float editingAnimationProgress;
        private float editingStartAnimationProgress;
        private float hideProgress;
        private boolean ignoreLayout;
        private boolean isEditing;
        private boolean isInHiddenMode;
        private long lastAnimationTime;
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
        private String tabLineColorKey = "profile_tabSelectedLine";
        private String activeTextColorKey = "profile_tabSelectedText";
        private String unactiveTextColorKey = "profile_tabText";
        private String selectorColorKey = "profile_tabSelector";
        private String backgroundColorKey = "actionBarDefault";
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
                long elapsedRealtime = SystemClock.elapsedRealtime() - TabsView.this.lastAnimationTime;
                if (elapsedRealtime > 17) {
                    elapsedRealtime = 17;
                }
                TabsView.access$2716(TabsView.this, ((float) elapsedRealtime) / 200.0f);
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
                if (TabsView.this.delegate == null) {
                    return;
                }
                TabsView.this.delegate.onPageScrolled(1.0f);
            }
        };
        private GradientDrawable selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null);

        /* loaded from: classes3.dex */
        public interface TabsViewDelegate {
            boolean canPerformActions();

            void invalidateBlur();

            void onPageScrolled(float f);

            void onPageSelected(int i, boolean z);

            void onSamePageSelected();
        }

        public static /* synthetic */ void lambda$setIsEditing$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        static /* synthetic */ float access$2716(TabsView tabsView, float f) {
            float f2 = tabsView.animationTime + f;
            tabsView.animationTime = f2;
            return f2;
        }

        /* loaded from: classes3.dex */
        public static class Tab {
            public int counter;
            public int id;
            public String title;
            public int titleWidth;

            public Tab(int i, String str) {
                this.id = i;
                this.title = str;
            }

            public int getWidth(boolean z, TextPaint textPaint) {
                int ceil = (int) Math.ceil(textPaint.measureText(this.title));
                this.titleWidth = ceil;
                return Math.max(AndroidUtilities.dp(40.0f), ceil);
            }
        }

        /* loaded from: classes3.dex */
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
                TabsView.this = r1;
            }

            public void setTab(Tab tab, int i) {
                this.currentTab = tab;
                this.currentPosition = i;
                setContentDescription(tab.title);
                requestLayout();
            }

            @Override // android.view.View
            public int getId() {
                return this.currentTab.id;
            }

            @Override // android.view.View
            protected void onMeasure(int i, int i2) {
                setMeasuredDimension(this.currentTab.getWidth(false, TabsView.this.textPaint) + AndroidUtilities.dp(32.0f) + TabsView.this.additionalTabWidth, View.MeasureSpec.getSize(i2));
            }

            @Override // android.view.View
            @SuppressLint({"DrawAllocation"})
            protected void onDraw(Canvas canvas) {
                int i;
                int i2;
                String str;
                String str2;
                int i3;
                int i4;
                String str3;
                int i5;
                int i6;
                int i7;
                if (this.currentTab.id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.save();
                    float f = TabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                    canvas.translate(AndroidUtilities.dp(0.66f) * f, 0.0f);
                    canvas.rotate(f, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                }
                if (TabsView.this.manualScrollingToId != -1) {
                    i2 = TabsView.this.manualScrollingToId;
                    i = TabsView.this.selectedTabId;
                } else {
                    i2 = TabsView.this.selectedTabId;
                    i = TabsView.this.previousId;
                }
                String str4 = "chats_tabUnreadActiveBackground";
                String str5 = "chats_tabUnreadUnactiveBackground";
                if (this.currentTab.id == i2) {
                    str2 = TabsView.this.activeTextColorKey;
                    str = TabsView.this.unactiveTextColorKey;
                } else {
                    str2 = TabsView.this.unactiveTextColorKey;
                    str = TabsView.this.activeTextColorKey;
                    str5 = str4;
                    str4 = str5;
                }
                if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToId != -1) && ((i7 = this.currentTab.id) == i2 || i7 == i)) {
                    TabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor(str2), TabsView.this.animatingIndicatorProgress));
                } else {
                    TabsView.this.textPaint.setColor(Theme.getColor(str2));
                }
                int i8 = this.currentTab.counter;
                if (i8 > 0) {
                    str3 = String.format("%d", Integer.valueOf(i8));
                    i4 = (int) Math.ceil(TabsView.this.textCounterPaint.measureText(str3));
                    i3 = Math.max(AndroidUtilities.dp(10.0f), i4) + AndroidUtilities.dp(10.0f);
                } else {
                    str3 = null;
                    i4 = 0;
                    i3 = 0;
                }
                if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                    i3 = (int) (i3 + ((AndroidUtilities.dp(20.0f) - i3) * TabsView.this.editingStartAnimationProgress));
                }
                int i9 = this.currentTab.titleWidth;
                if (i3 != 0) {
                    i5 = AndroidUtilities.dp((str3 != null ? 1.0f : TabsView.this.editingStartAnimationProgress) * 6.0f) + i3;
                } else {
                    i5 = 0;
                }
                this.tabWidth = i9 + i5;
                int measuredWidth = (getMeasuredWidth() - this.tabWidth) / 2;
                if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                    String str6 = this.currentTab.title;
                    this.currentText = str6;
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(str6, TabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), TabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.textLayout = staticLayout;
                    this.textHeight = staticLayout.getHeight();
                    this.textOffsetX = (int) (-this.textLayout.getLineLeft(0));
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate(this.textOffsetX + measuredWidth, ((getMeasuredHeight() - this.textHeight) / 2) + 1);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (str3 != null || (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f))) {
                    TabsView.this.textCounterPaint.setColor(Theme.getColor(TabsView.this.backgroundColorKey));
                    if (!Theme.hasThemeKey(str4) || !Theme.hasThemeKey(str5)) {
                        TabsView.this.counterPaint.setColor(TabsView.this.textPaint.getColor());
                    } else {
                        int color = Theme.getColor(str4);
                        if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToPosition != -1) && ((i6 = this.currentTab.id) == i2 || i6 == i)) {
                            TabsView.this.counterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str5), color, TabsView.this.animatingIndicatorProgress));
                        } else {
                            TabsView.this.counterPaint.setColor(color);
                        }
                    }
                    int dp = measuredWidth + this.currentTab.titleWidth + AndroidUtilities.dp(6.0f);
                    int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2;
                    if (this.currentTab.id == Integer.MAX_VALUE || ((!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f) || str3 != null)) {
                        TabsView.this.counterPaint.setAlpha(255);
                    } else {
                        TabsView.this.counterPaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                    }
                    this.rect.set(dp, measuredHeight, dp + i3, AndroidUtilities.dp(20.0f) + measuredHeight);
                    RectF rectF = this.rect;
                    float f2 = AndroidUtilities.density;
                    canvas.drawRoundRect(rectF, f2 * 11.5f, f2 * 11.5f, TabsView.this.counterPaint);
                    if (str3 != null) {
                        if (this.currentTab.id != Integer.MAX_VALUE) {
                            TabsView.this.textCounterPaint.setAlpha((int) ((1.0f - TabsView.this.editingStartAnimationProgress) * 255.0f));
                        }
                        RectF rectF2 = this.rect;
                        canvas.drawText(str3, rectF2.left + ((rectF2.width() - i4) / 2.0f), measuredHeight + AndroidUtilities.dp(14.5f), TabsView.this.textCounterPaint);
                    }
                    if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                        TabsView.this.deletePaint.setColor(TabsView.this.textCounterPaint.getColor());
                        TabsView.this.deletePaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                        float dp2 = AndroidUtilities.dp(3.0f);
                        canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() - dp2, this.rect.centerX() + dp2, this.rect.centerY() + dp2, TabsView.this.deletePaint);
                        canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() + dp2, this.rect.centerX() + dp2, this.rect.centerY() - dp2, TabsView.this.deletePaint);
                    }
                }
                if (this.currentTab.id == Integer.MAX_VALUE || TabsView.this.editingAnimationProgress == 0.0f) {
                    return;
                }
                canvas.restore();
            }

            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setSelected((this.currentTab == null || TabsView.this.selectedTabId == -1 || this.currentTab.id != TabsView.this.selectedTabId) ? false : true);
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
            float dpf2 = AndroidUtilities.dpf2(3.0f);
            this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
            setHorizontalScrollBarEnabled(false);
            RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.2
                @Override // android.view.ViewGroup
                public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
                    super.addView(view, i, layoutParams);
                    if (TabsView.this.isInHiddenMode) {
                        view.setScaleX(0.3f);
                        view.setScaleY(0.3f);
                        view.setAlpha(0.0f);
                        return;
                    }
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    view.setAlpha(1.0f);
                }

                @Override // android.view.View
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    TabsView.this.invalidate();
                }

                @Override // org.telegram.ui.Components.RecyclerListView
                public boolean canHighlightChildAt(View view, float f, float f2) {
                    if (TabsView.this.isEditing) {
                        TabView tabView = (TabView) view;
                        float dp = AndroidUtilities.dp(6.0f);
                        if (tabView.rect.left - dp < f && tabView.rect.right + dp > f) {
                            return false;
                        }
                    }
                    return super.canHighlightChildAt(view, f, f2);
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
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.3.1
                        @Override // androidx.recyclerview.widget.LinearSmoothScroller, androidx.recyclerview.widget.RecyclerView.SmoothScroller
                        protected void onTargetFound(View view, RecyclerView.State state2, RecyclerView.SmoothScroller.Action action) {
                            int calculateDxToMakeVisible = calculateDxToMakeVisible(view, getHorizontalSnapPreference());
                            if (calculateDxToMakeVisible > 0 || (calculateDxToMakeVisible == 0 && view.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                                calculateDxToMakeVisible += AndroidUtilities.dp(60.0f);
                            } else if (calculateDxToMakeVisible < 0 || (calculateDxToMakeVisible == 0 && view.getRight() + AndroidUtilities.dp(21.0f) > TabsView.this.getMeasuredWidth())) {
                                calculateDxToMakeVisible -= AndroidUtilities.dp(60.0f);
                            }
                            int calculateDyToMakeVisible = calculateDyToMakeVisible(view, getVerticalSnapPreference());
                            int max = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((calculateDxToMakeVisible * calculateDxToMakeVisible) + (calculateDyToMakeVisible * calculateDyToMakeVisible))));
                            if (max > 0) {
                                action.update(-calculateDxToMakeVisible, -calculateDyToMakeVisible, max, this.mDecelerateInterpolator);
                            }
                        }
                    };
                    linearSmoothScroller.setTargetPosition(i);
                    startSmoothScroll(linearSmoothScroller);
                }

                @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
                    if (TabsView.this.isInHiddenMode) {
                        accessibilityNodeInfoCompat.setVisibleToUser(false);
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
                    ViewPagerFixed.TabsView.this.lambda$new$0(view, i, f, f2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.4
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    TabsView.this.invalidate();
                }
            });
            addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        }

        public /* synthetic */ void lambda$new$0(View view, int i, float f, float f2) {
            TabsViewDelegate tabsViewDelegate;
            if (!this.delegate.canPerformActions()) {
                return;
            }
            TabView tabView = (TabView) view;
            if (i != this.currentPosition || (tabsViewDelegate = this.delegate) == null) {
                scrollToTab(tabView.currentTab.id, i);
            } else {
                tabsViewDelegate.onSamePageSelected();
            }
        }

        public void setDelegate(TabsViewDelegate tabsViewDelegate) {
            this.delegate = tabsViewDelegate;
        }

        public boolean isAnimatingIndicator() {
            return this.animatingIndicator;
        }

        public void scrollToTab(int i, int i2) {
            int i3 = this.currentPosition;
            boolean z = i3 < i2;
            this.scrollingToChild = -1;
            this.previousPosition = i3;
            this.previousId = this.selectedTabId;
            this.currentPosition = i2;
            this.selectedTabId = i;
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
                tabsViewDelegate.onPageSelected(i, z);
            }
            scrollToChild(i2);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.tabsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    TabsView.this.setAnimationIdicatorProgress(floatValue);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(floatValue);
                    }
                }
            });
            this.tabsAnimator.setDuration(250L);
            this.tabsAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.tabsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ViewPagerFixed.TabsView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
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

        public void setAnimationIdicatorProgress(float f) {
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(f);
            }
        }

        public Drawable getSelectorDrawable() {
            return this.selectorDrawable;
        }

        public RecyclerListView getTabsContainer() {
            return this.listView;
        }

        public void addTab(int i, String str) {
            int size = this.tabs.size();
            if (size == 0 && this.selectedTabId == -1) {
                this.selectedTabId = i;
            }
            this.positionToId.put(size, i);
            this.idToPosition.put(i, size);
            int i2 = this.selectedTabId;
            if (i2 != -1 && i2 == i) {
                this.currentPosition = size;
            }
            Tab tab = new Tab(i, str);
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

        public int getCurrentTabId() {
            return this.selectedTabId;
        }

        public int getFirstTabId() {
            return this.positionToId.get(0, 0);
        }

        private void updateTabsWidths() {
            this.positionToX.clear();
            this.positionToWidth.clear();
            int dp = AndroidUtilities.dp(7.0f);
            int size = this.tabs.size();
            for (int i = 0; i < size; i++) {
                int width = this.tabs.get(i).getWidth(false, this.textPaint);
                this.positionToWidth.put(i, width);
                this.positionToX.put(i, (this.additionalTabWidth / 2) + dp);
                dp += width + AndroidUtilities.dp(32.0f) + this.additionalTabWidth;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:33:0x009d  */
        /* JADX WARN: Removed duplicated region for block: B:34:0x00a2  */
        /* JADX WARN: Removed duplicated region for block: B:37:0x00c4  */
        /* JADX WARN: Removed duplicated region for block: B:38:0x00d3  */
        /* JADX WARN: Removed duplicated region for block: B:41:0x00fb  */
        /* JADX WARN: Removed duplicated region for block: B:44:0x012a  */
        @Override // android.view.ViewGroup
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
            /*
                Method dump skipped, instructions count: 316
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.TabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            if (!this.tabs.isEmpty()) {
                int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
                int i3 = this.additionalTabWidth;
                int i4 = this.allTabsWidth;
                int size2 = i4 < size ? (size - i4) / this.tabs.size() : 0;
                this.additionalTabWidth = size2;
                if (i3 != size2) {
                    this.ignoreLayout = true;
                    this.adapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
                updateTabsWidths();
            }
            super.onMeasure(i, i2);
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

        private void scrollToChild(int i) {
            if (this.tabs.isEmpty() || this.scrollingToChild == i || i < 0 || i >= this.tabs.size()) {
                return;
            }
            this.scrollingToChild = i;
            this.listView.smoothScrollToPosition(i);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int i5 = i3 - i;
            if (this.prevLayoutWidth != i5) {
                this.prevLayoutWidth = i5;
                this.scrollingToChild = -1;
                if (!this.animatingIndicator) {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                TabsViewDelegate tabsViewDelegate = this.delegate;
                if (tabsViewDelegate == null) {
                    return;
                }
                tabsViewDelegate.onPageScrolled(1.0f);
            }
        }

        public void selectTab(int i, int i2, float f) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            this.currentPosition = i;
            this.selectedTabId = this.positionToId.get(i);
            if (f > 0.0f) {
                this.manualScrollingToPosition = i2;
                this.manualScrollingToId = this.positionToId.get(i2);
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(i);
            if (f >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = i2;
                this.selectedTabId = this.positionToId.get(i2);
            }
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.invalidateBlur();
            }
        }

        public void selectTabWithId(int i, float f) {
            int i2 = this.idToPosition.get(i, -1);
            if (i2 < 0) {
                return;
            }
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            if (f > 0.0f) {
                this.manualScrollingToPosition = i2;
                this.manualScrollingToId = i;
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(i2);
            if (f < 1.0f) {
                return;
            }
            this.manualScrollingToPosition = -1;
            this.manualScrollingToId = -1;
            this.currentPosition = i2;
            this.selectedTabId = i;
        }

        public void setIsEditing(boolean z) {
            this.isEditing = z;
            this.listView.invalidateViews();
            invalidate();
            if (this.isEditing || !this.orderChanged) {
                return;
            }
            MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
            TLRPC$TL_messages_updateDialogFiltersOrder tLRPC$TL_messages_updateDialogFiltersOrder = new TLRPC$TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i);
                tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i).id));
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, ViewPagerFixed$TabsView$$ExternalSyntheticLambda0.INSTANCE);
            this.orderChanged = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context mContext;

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public long getItemId(int i) {
                return i;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                return 0;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            public ListAdapter(Context context) {
                TabsView.this = r1;
                this.mContext = context;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return TabsView.this.tabs.size();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new TabView(this.mContext));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((TabView) viewHolder.itemView).setTab((Tab) TabsView.this.tabs.get(i), i);
            }
        }

        public void hide(boolean z, boolean z2) {
            this.isInHiddenMode = z;
            int i = 0;
            float f = 1.0f;
            if (z2) {
                while (i < this.listView.getChildCount()) {
                    this.listView.getChildAt(i).animate().alpha(z ? 0.0f : 1.0f).scaleX(z ? 0.0f : 1.0f).scaleY(z ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(220L).start();
                    i++;
                }
            } else {
                while (i < this.listView.getChildCount()) {
                    View childAt = this.listView.getChildAt(i);
                    childAt.setScaleX(z ? 0.0f : 1.0f);
                    childAt.setScaleY(z ? 0.0f : 1.0f);
                    childAt.setAlpha(z ? 0.0f : 1.0f);
                    i++;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.hideProgress = f;
            }
            invalidate();
        }
    }

    private View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(this.rect);
                if (!this.rect.contains((int) f, (int) f2)) {
                    continue;
                } else if (childAt.canScrollHorizontally(-1)) {
                    return childAt;
                } else {
                    if (childAt instanceof ViewGroup) {
                        android.graphics.Rect rect = this.rect;
                        View findScrollingChild = findScrollingChild((ViewGroup) childAt, f - rect.left, f2 - rect.top);
                        if (findScrollingChild != null) {
                            return findScrollingChild;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public void drawForBlur(Canvas canvas) {
        RecyclerListView findRecyclerView;
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPages;
            if (i < viewArr.length) {
                if (viewArr[i] != null && viewArr[i].getVisibility() == 0 && (findRecyclerView = findRecyclerView(this.viewPages[i])) != null) {
                    for (int i2 = 0; i2 < findRecyclerView.getChildCount(); i2++) {
                        View childAt = findRecyclerView.getChildAt(i2);
                        if (childAt.getY() < AndroidUtilities.dp(203.0f) + AndroidUtilities.dp(100.0f)) {
                            int save = canvas.save();
                            canvas.translate(this.viewPages[i].getX(), getY() + this.viewPages[i].getY() + findRecyclerView.getY() + childAt.getY());
                            childAt.draw(canvas);
                            canvas.restoreToCount(save);
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
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof RecyclerListView) {
                    return (RecyclerListView) childAt;
                }
                if (childAt instanceof ViewGroup) {
                    findRecyclerView(childAt);
                }
            }
            return null;
        }
        return null;
    }
}
