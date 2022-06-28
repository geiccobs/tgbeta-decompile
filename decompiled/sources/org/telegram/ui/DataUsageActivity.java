package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.StatsController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
/* loaded from: classes4.dex */
public class DataUsageActivity extends BaseFragment {
    private static final Interpolator interpolator = DataUsageActivity$$ExternalSyntheticLambda1.INSTANCE;
    private boolean animatingForward;
    private boolean backAnimation;
    private int maximumVelocity;
    private ListAdapter mobileAdapter;
    private ListAdapter roamingAdapter;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private ListAdapter wifiAdapter;
    private Paint backgroundPaint = new Paint();
    private ViewPage[] viewPages = new ViewPage[2];
    private boolean swipeBackEnabled = true;

    /* loaded from: classes4.dex */
    public class ViewPage extends FrameLayout {
        private LinearLayoutManager layoutManager;
        private ListAdapter listAdapter;
        private RecyclerListView listView;
        private int selectedType;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ViewPage(Context context) {
            super(context);
            DataUsageActivity.this = r1;
        }
    }

    public static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("NetworkUsage", R.string.NetworkUsage));
        boolean z = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        int i = 1;
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.DataUsageActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    DataUsageActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.mobileAdapter = new ListAdapter(context, 0);
        this.wifiAdapter = new ListAdapter(context, 1);
        this.roamingAdapter = new ListAdapter(context, 2);
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip;
        scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.DataUsageActivity.2
            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageSelected(int id, boolean forward) {
                if (DataUsageActivity.this.viewPages[0].selectedType == id) {
                    return;
                }
                DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                dataUsageActivity.swipeBackEnabled = id == dataUsageActivity.scrollSlidingTextTabStrip.getFirstTabId();
                DataUsageActivity.this.viewPages[1].selectedType = id;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                DataUsageActivity.this.animatingForward = forward;
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageScrolled(float progress) {
                if (progress != 1.0f || DataUsageActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DataUsageActivity.this.animatingForward) {
                        DataUsageActivity.this.viewPages[0].setTranslationX((-progress) * DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                        DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth() - (DataUsageActivity.this.viewPages[0].getMeasuredWidth() * progress));
                    } else {
                        DataUsageActivity.this.viewPages[0].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth() * progress);
                        DataUsageActivity.this.viewPages[1].setTranslationX((DataUsageActivity.this.viewPages[0].getMeasuredWidth() * progress) - DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = DataUsageActivity.this.viewPages[0];
                        DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                        DataUsageActivity.this.viewPages[1] = tempPage;
                        DataUsageActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.DataUsageActivity.3
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = DataUsageActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                DataUsageActivity.this.actionBar.setEnabled(false);
                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                DataUsageActivity.this.viewPages[1].selectedType = id;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                DataUsageActivity.this.animatingForward = forward;
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DataUsageActivity.this.viewPages[1].setTranslationX(-DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                }
                return true;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                measureChildWithMargins(DataUsageActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = DataUsageActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int a = 0; a < DataUsageActivity.this.viewPages.length; a++) {
                    if (DataUsageActivity.this.viewPages[a] != null && DataUsageActivity.this.viewPages[a].listView != null) {
                        DataUsageActivity.this.viewPages[a].listView.setPadding(0, actionBarHeight, 0, AndroidUtilities.dp(4.0f));
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View child = getChildAt(i2);
                    if (child != null && child.getVisibility() != 8 && child != DataUsageActivity.this.actionBar) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DataUsageActivity.this.parentLayout != null) {
                    DataUsageActivity.this.parentLayout.drawHeaderShadow(canvas, DataUsageActivity.this.actionBar.getMeasuredHeight() + ((int) DataUsageActivity.this.actionBar.getTranslationY()));
                }
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.globalIgnoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            public boolean checkTabsAnimationInProgress() {
                if (DataUsageActivity.this.tabsAnimationInProgress) {
                    boolean cancel = false;
                    int i2 = -1;
                    if (DataUsageActivity.this.backAnimation) {
                        if (Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                            DataUsageActivity.this.viewPages[0].setTranslationX(0.0f);
                            ViewPage viewPage = DataUsageActivity.this.viewPages[1];
                            int measuredWidth = DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                            if (DataUsageActivity.this.animatingForward) {
                                i2 = 1;
                            }
                            viewPage.setTranslationX(measuredWidth * i2);
                            cancel = true;
                        }
                    } else if (Math.abs(DataUsageActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                        ViewPage viewPage2 = DataUsageActivity.this.viewPages[0];
                        int measuredWidth2 = DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                        if (!DataUsageActivity.this.animatingForward) {
                            i2 = 1;
                        }
                        viewPage2.setTranslationX(measuredWidth2 * i2);
                        DataUsageActivity.this.viewPages[1].setTranslationX(0.0f);
                        cancel = true;
                    }
                    if (cancel) {
                        if (DataUsageActivity.this.tabsAnimation != null) {
                            DataUsageActivity.this.tabsAnimation.cancel();
                            DataUsageActivity.this.tabsAnimation = null;
                        }
                        DataUsageActivity.this.tabsAnimationInProgress = false;
                    }
                    return DataUsageActivity.this.tabsAnimationInProgress;
                }
                return false;
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || DataUsageActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                DataUsageActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                canvas.drawRect(0.0f, DataUsageActivity.this.actionBar.getMeasuredHeight() + DataUsageActivity.this.actionBar.getTranslationY(), getMeasuredWidth(), getMeasuredHeight(), DataUsageActivity.this.backgroundPaint);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent ev) {
                float velY;
                float velX;
                float dx;
                int duration;
                boolean z2 = false;
                if (DataUsageActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (ev != null) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(ev);
                }
                if (ev != null && ev.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = ev.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) ev.getX();
                    this.startedTrackingY = (int) ev.getY();
                    this.velocityTracker.clear();
                } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                    int dx2 = (int) (ev.getX() - this.startedTrackingX);
                    int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                    if (this.startedTracking && ((DataUsageActivity.this.animatingForward && dx2 > 0) || (!DataUsageActivity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            DataUsageActivity.this.viewPages[0].setTranslationX(0.0f);
                            DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.animatingForward ? DataUsageActivity.this.viewPages[0].getMeasuredWidth() : -DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                            DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, 0.0f);
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
                        if (Math.abs(dx2) >= touchSlop && Math.abs(dx2) > dy) {
                            if (dx2 < 0) {
                                z2 = true;
                            }
                            prepareForMoving(ev, z2);
                        }
                    } else if (this.startedTracking) {
                        if (DataUsageActivity.this.animatingForward) {
                            DataUsageActivity.this.viewPages[0].setTranslationX(dx2);
                            DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth() + dx2);
                        } else {
                            DataUsageActivity.this.viewPages[0].setTranslationX(dx2);
                            DataUsageActivity.this.viewPages[1].setTranslationX(dx2 - DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                        }
                        float scrollProgress = Math.abs(dx2) / DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, scrollProgress);
                    }
                } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, DataUsageActivity.this.maximumVelocity);
                    if (ev != null && ev.getAction() != 3) {
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    } else {
                        velX = 0.0f;
                        velY = 0.0f;
                    }
                    if (this.startedTracking) {
                        float x = DataUsageActivity.this.viewPages[0].getX();
                        DataUsageActivity.this.tabsAnimation = new AnimatorSet();
                        DataUsageActivity.this.backAnimation = Math.abs(x) < ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        if (!DataUsageActivity.this.backAnimation) {
                            dx = DataUsageActivity.this.viewPages[0].getMeasuredWidth() - Math.abs(x);
                            if (DataUsageActivity.this.animatingForward) {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, -DataUsageActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            } else {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, DataUsageActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            }
                        } else {
                            dx = Math.abs(x);
                            if (DataUsageActivity.this.animatingForward) {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, DataUsageActivity.this.viewPages[1].getMeasuredWidth()));
                            } else {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, -DataUsageActivity.this.viewPages[1].getMeasuredWidth()));
                            }
                        }
                        DataUsageActivity.this.tabsAnimation.setInterpolator(DataUsageActivity.interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distanceRatio = Math.min(1.0f, (dx * 1.0f) / width);
                        float distance = halfWidth + (halfWidth * AndroidUtilities.distanceInfluenceForSnapDuration(distanceRatio));
                        float velX2 = Math.abs(velX);
                        if (velX2 > 0.0f) {
                            duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                        } else {
                            int duration2 = getMeasuredWidth();
                            float pageDelta = dx / duration2;
                            duration = (int) ((1.0f + pageDelta) * 100.0f);
                        }
                        DataUsageActivity.this.tabsAnimation.setDuration(Math.max(150, Math.min(duration, 600)));
                        DataUsageActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DataUsageActivity.3.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                DataUsageActivity.this.tabsAnimation = null;
                                if (DataUsageActivity.this.backAnimation) {
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage tempPage = DataUsageActivity.this.viewPages[0];
                                    DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                                    DataUsageActivity.this.viewPages[1] = tempPage;
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                    DataUsageActivity.this.swipeBackEnabled = DataUsageActivity.this.viewPages[0].selectedType == DataUsageActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                DataUsageActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass3.this.maybeStartTracking = false;
                                AnonymousClass3.this.startedTracking = false;
                                DataUsageActivity.this.actionBar.setEnabled(true);
                                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DataUsageActivity.this.tabsAnimation.start();
                        DataUsageActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        DataUsageActivity.this.actionBar.setEnabled(true);
                        DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = frameLayout;
        frameLayout.setWillNotDraw(false);
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            if (a == 0 && viewPageArr[a] != null && viewPageArr[a].layoutManager != null) {
                scrollToPositionOnRecreate = this.viewPages[a].layoutManager.findFirstVisibleItemPosition();
                if (scrollToPositionOnRecreate != this.viewPages[a].layoutManager.getItemCount() - i) {
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.viewPages[a].listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate);
                    if (holder != null) {
                        scrollToOffsetOnRecreate = holder.itemView.getTop();
                    } else {
                        scrollToPositionOnRecreate = -1;
                    }
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            }
            ViewPage ViewPage2 = new ViewPage(context) { // from class: org.telegram.ui.DataUsageActivity.4
                @Override // android.view.View
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (DataUsageActivity.this.tabsAnimationInProgress && DataUsageActivity.this.viewPages[0] == this) {
                        float scrollProgress = Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) / DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, scrollProgress);
                    }
                }
            };
            frameLayout.addView(ViewPage2, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr2 = this.viewPages;
            viewPageArr2[a] = ViewPage2;
            LinearLayoutManager layoutManager = viewPageArr2[a].layoutManager = new LinearLayoutManager(context, i, false) { // from class: org.telegram.ui.DataUsageActivity.5
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            final RecyclerListView listView = new RecyclerListView(context);
            this.viewPages[a].listView = listView;
            this.viewPages[a].listView.setScrollingTouchSlop(i);
            this.viewPages[a].listView.setItemAnimator(null);
            this.viewPages[a].listView.setClipToPadding(false);
            this.viewPages[a].listView.setSectionsType(2);
            this.viewPages[a].listView.setLayoutManager(layoutManager);
            ViewPage[] viewPageArr3 = this.viewPages;
            viewPageArr3[a].addView(viewPageArr3[a].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.viewPages[a].listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DataUsageActivity$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i2) {
                    DataUsageActivity.this.m3312lambda$createView$2$orgtelegramuiDataUsageActivity(listView, view, i2);
                }
            });
            this.viewPages[a].listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.DataUsageActivity.6
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState != 1) {
                        int scrollY = (int) (-DataUsageActivity.this.actionBar.getTranslationY());
                        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (scrollY != 0 && scrollY != actionBarHeight) {
                            if (scrollY < actionBarHeight / 2) {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                            } else {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                            }
                        }
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView == DataUsageActivity.this.viewPages[0].listView) {
                        float currentTranslation = DataUsageActivity.this.actionBar.getTranslationY();
                        float newTranslation = currentTranslation - dy;
                        if (newTranslation < (-ActionBar.getCurrentActionBarHeight())) {
                            newTranslation = -ActionBar.getCurrentActionBarHeight();
                        } else if (newTranslation > 0.0f) {
                            newTranslation = 0.0f;
                        }
                        if (newTranslation != currentTranslation) {
                            DataUsageActivity.this.setScrollY(newTranslation);
                        }
                    }
                }
            });
            if (a == 0 && scrollToPositionOnRecreate != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
            }
            if (a != 0) {
                this.viewPages[a].setVisibility(8);
            }
            a++;
            i = 1;
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-DataUsageActivity */
    public /* synthetic */ void m3312lambda$createView$2$orgtelegramuiDataUsageActivity(RecyclerListView listView, View view, int position) {
        if (getParentActivity() == null) {
            return;
        }
        final ListAdapter adapter = (ListAdapter) listView.getAdapter();
        if (position == adapter.resetRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("ResetStatisticsAlertTitle", R.string.ResetStatisticsAlertTitle));
            builder.setMessage(LocaleController.getString("ResetStatisticsAlert", R.string.ResetStatisticsAlert));
            builder.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DataUsageActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DataUsageActivity.this.m3311lambda$createView$1$orgtelegramuiDataUsageActivity(adapter, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog dialog = builder.create();
            showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-DataUsageActivity */
    public /* synthetic */ void m3311lambda$createView$1$orgtelegramuiDataUsageActivity(ListAdapter adapter, DialogInterface dialogInterface, int i) {
        StatsController.getInstance(this.currentAccount).resetStats(adapter.currentType);
        adapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.mobileAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        ListAdapter listAdapter2 = this.wifiAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        ListAdapter listAdapter3 = this.roamingAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return this.swipeBackEnabled;
    }

    public void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a < viewPageArr.length) {
                viewPageArr[a].listView.setPinnedSectionOffsetY((int) value);
                a++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip == null) {
            return;
        }
        scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("NetworkUsageMobileTab", R.string.NetworkUsageMobileTab));
        this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("NetworkUsageWiFiTab", R.string.NetworkUsageWiFiTab));
        this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("NetworkUsageRoamingTab", R.string.NetworkUsageRoamingTab));
        this.scrollSlidingTextTabStrip.setVisibility(0);
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
        if (id >= 0) {
            this.viewPages[0].selectedType = id;
        }
        this.scrollSlidingTextTabStrip.finishAddingTabs();
    }

    public void switchToCurrentSelectedMode(boolean animated) {
        ViewPage[] viewPageArr;
        int a = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a].listView.stopScroll();
            a++;
        }
        RecyclerView.Adapter currentAdapter = viewPageArr[animated ? 1 : 0].listView.getAdapter();
        this.viewPages[animated].listView.setPinnedHeaderShadowDrawable(null);
        if (this.viewPages[animated].selectedType != 0) {
            if (this.viewPages[animated].selectedType != 1) {
                if (this.viewPages[animated].selectedType == 2 && currentAdapter != this.roamingAdapter) {
                    this.viewPages[animated].listView.setAdapter(this.roamingAdapter);
                }
            } else if (currentAdapter != this.wifiAdapter) {
                this.viewPages[animated].listView.setAdapter(this.wifiAdapter);
            }
        } else if (currentAdapter != this.mobileAdapter) {
            this.viewPages[animated].listView.setAdapter(this.mobileAdapter);
        }
        this.viewPages[animated].listView.setVisibility(0);
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.viewPages[animated].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int audiosBytesReceivedRow;
        private int audiosBytesSentRow;
        private int audiosReceivedRow;
        private int audiosSection2Row;
        private int audiosSectionRow;
        private int audiosSentRow;
        private int callsBytesReceivedRow;
        private int callsBytesSentRow;
        private int callsReceivedRow;
        private int callsSection2Row;
        private int callsSectionRow;
        private int callsSentRow;
        private int callsTotalTimeRow;
        private int currentType;
        private int filesBytesReceivedRow;
        private int filesBytesSentRow;
        private int filesReceivedRow;
        private int filesSection2Row;
        private int filesSectionRow;
        private int filesSentRow;
        private Context mContext;
        private int messagesBytesReceivedRow;
        private int messagesBytesSentRow;
        private int messagesSection2Row;
        private int messagesSectionRow;
        private int photosBytesReceivedRow;
        private int photosBytesSentRow;
        private int photosReceivedRow;
        private int photosSection2Row;
        private int photosSentRow;
        private int resetRow;
        private int resetSection2Row;
        private int rowCount;
        private int totalBytesReceivedRow;
        private int totalBytesSentRow;
        private int totalSection2Row;
        private int totalSectionRow;
        private int videosBytesReceivedRow;
        private int videosBytesSentRow;
        private int videosReceivedRow;
        private int videosSection2Row;
        private int videosSectionRow;
        private int videosSentRow;
        private int photosSectionRow = 0;
        private int messagesSentRow = -1;
        private int messagesReceivedRow = -1;

        public ListAdapter(Context context, int type) {
            DataUsageActivity.this = r2;
            this.mContext = context;
            this.currentType = type;
            this.rowCount = 0;
            int i = 0 + 1;
            this.rowCount = i;
            int i2 = i + 1;
            this.rowCount = i2;
            this.photosSentRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.photosReceivedRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.photosBytesSentRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.photosBytesReceivedRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.photosSection2Row = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.videosSectionRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.videosSentRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.videosReceivedRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.videosBytesSentRow = i9;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.videosBytesReceivedRow = i10;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.videosSection2Row = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.audiosSectionRow = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.audiosSentRow = i13;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.audiosReceivedRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.audiosBytesSentRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.audiosBytesReceivedRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.audiosSection2Row = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.filesSectionRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.filesSentRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.filesReceivedRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.filesBytesSentRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.filesBytesReceivedRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.filesSection2Row = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.callsSectionRow = i24;
            int i26 = i25 + 1;
            this.rowCount = i26;
            this.callsSentRow = i25;
            int i27 = i26 + 1;
            this.rowCount = i27;
            this.callsReceivedRow = i26;
            int i28 = i27 + 1;
            this.rowCount = i28;
            this.callsBytesSentRow = i27;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.callsBytesReceivedRow = i28;
            int i30 = i29 + 1;
            this.rowCount = i30;
            this.callsTotalTimeRow = i29;
            int i31 = i30 + 1;
            this.rowCount = i31;
            this.callsSection2Row = i30;
            int i32 = i31 + 1;
            this.rowCount = i32;
            this.messagesSectionRow = i31;
            int i33 = i32 + 1;
            this.rowCount = i33;
            this.messagesBytesSentRow = i32;
            int i34 = i33 + 1;
            this.rowCount = i34;
            this.messagesBytesReceivedRow = i33;
            int i35 = i34 + 1;
            this.rowCount = i35;
            this.messagesSection2Row = i34;
            int i36 = i35 + 1;
            this.rowCount = i36;
            this.totalSectionRow = i35;
            int i37 = i36 + 1;
            this.rowCount = i37;
            this.totalBytesSentRow = i36;
            int i38 = i37 + 1;
            this.rowCount = i38;
            this.totalBytesReceivedRow = i37;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.totalSection2Row = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.resetRow = i39;
            this.rowCount = i40 + 1;
            this.resetSection2Row = i40;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == this.resetSection2Row) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == this.resetRow) {
                        textCell.setTag(Theme.key_windowBackgroundWhiteRedText2);
                        textCell.setText(LocaleController.getString("ResetStatistics", R.string.ResetStatistics), false);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText2));
                        return;
                    }
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    int i = this.callsSentRow;
                    if (position == i || position == this.callsReceivedRow || position == this.callsBytesSentRow || position == this.callsBytesReceivedRow) {
                        type = 0;
                    } else if (position == this.messagesSentRow || position == this.messagesReceivedRow || position == this.messagesBytesSentRow || position == this.messagesBytesReceivedRow) {
                        type = 1;
                    } else if (position == this.photosSentRow || position == this.photosReceivedRow || position == this.photosBytesSentRow || position == this.photosBytesReceivedRow) {
                        type = 4;
                    } else if (position == this.audiosSentRow || position == this.audiosReceivedRow || position == this.audiosBytesSentRow || position == this.audiosBytesReceivedRow) {
                        type = 3;
                    } else if (position == this.videosSentRow || position == this.videosReceivedRow || position == this.videosBytesSentRow || position == this.videosBytesReceivedRow) {
                        type = 2;
                    } else if (position == this.filesSentRow || position == this.filesReceivedRow || position == this.filesBytesSentRow || position == this.filesBytesReceivedRow) {
                        type = 5;
                    } else {
                        type = 6;
                    }
                    if (position == i) {
                        textCell.setTextAndValue(LocaleController.getString("OutgoingCalls", R.string.OutgoingCalls), String.format("%d", Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, type))), true);
                        return;
                    } else if (position == this.callsReceivedRow) {
                        textCell.setTextAndValue(LocaleController.getString("IncomingCalls", R.string.IncomingCalls), String.format("%d", Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, type))), true);
                        return;
                    } else if (position == this.callsTotalTimeRow) {
                        String time = AndroidUtilities.formatShortDuration(StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(this.currentType));
                        textCell.setTextAndValue(LocaleController.getString("CallsTotalTime", R.string.CallsTotalTime), time, false);
                        return;
                    } else if (position == this.messagesSentRow || position == this.photosSentRow || position == this.videosSentRow || position == this.audiosSentRow || position == this.filesSentRow) {
                        textCell.setTextAndValue(LocaleController.getString("CountSent", R.string.CountSent), String.format("%d", Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, type))), true);
                        return;
                    } else if (position == this.messagesReceivedRow || position == this.photosReceivedRow || position == this.videosReceivedRow || position == this.audiosReceivedRow || position == this.filesReceivedRow) {
                        textCell.setTextAndValue(LocaleController.getString("CountReceived", R.string.CountReceived), String.format("%d", Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, type))), true);
                        return;
                    } else if (position == this.messagesBytesSentRow || position == this.photosBytesSentRow || position == this.videosBytesSentRow || position == this.audiosBytesSentRow || position == this.filesBytesSentRow || position == this.callsBytesSentRow || position == this.totalBytesSentRow) {
                        textCell.setTextAndValue(LocaleController.getString("BytesSent", R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(this.currentType, type)), true);
                        return;
                    } else if (position == this.messagesBytesReceivedRow || position == this.photosBytesReceivedRow || position == this.videosBytesReceivedRow || position == this.audiosBytesReceivedRow || position == this.filesBytesReceivedRow || position == this.callsBytesReceivedRow || position == this.totalBytesReceivedRow) {
                        String string = LocaleController.getString("BytesReceived", R.string.BytesReceived);
                        String formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(this.currentType, type));
                        if (position == this.callsBytesReceivedRow) {
                            z = true;
                        }
                        textCell.setTextAndValue(string, formatFileSize, z);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == this.totalSectionRow) {
                        headerCell.setText(LocaleController.getString("TotalDataUsage", R.string.TotalDataUsage));
                        return;
                    } else if (position == this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("CallsDataUsage", R.string.CallsDataUsage));
                        return;
                    } else if (position == this.filesSectionRow) {
                        headerCell.setText(LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage));
                        return;
                    } else if (position == this.audiosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache));
                        return;
                    } else if (position == this.videosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache));
                        return;
                    } else if (position == this.photosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache));
                        return;
                    } else if (position == this.messagesSectionRow) {
                        headerCell.setText(LocaleController.getString("MessagesDataUsage", R.string.MessagesDataUsage));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    cell.setText(LocaleController.formatString("NetworkUsageSince", R.string.NetworkUsageSince, LocaleController.getInstance().formatterStats.format(StatsController.getInstance(DataUsageActivity.this.currentAccount).getResetStatsDate(this.currentType))));
                    return;
                default:
                    return;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getAdapterPosition() == this.resetRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.resetSection2Row) {
                return 3;
            }
            if (position == this.callsSection2Row || position == this.filesSection2Row || position == this.audiosSection2Row || position == this.videosSection2Row || position == this.photosSection2Row || position == this.messagesSection2Row || position == this.totalSection2Row) {
                return 0;
            }
            if (position == this.totalSectionRow || position == this.callsSectionRow || position == this.filesSectionRow || position == this.audiosSectionRow || position == this.videosSectionRow || position == this.photosSectionRow || position == this.messagesSectionRow) {
                return 2;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabActiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabUnactiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabLine));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, Theme.key_actionBarTabSelector));
        for (int a = 0; a < this.viewPages.length; a++) {
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
            arrayList.add(new ThemeDescription(this.viewPages[a].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText2));
        }
        return arrayList;
    }
}
