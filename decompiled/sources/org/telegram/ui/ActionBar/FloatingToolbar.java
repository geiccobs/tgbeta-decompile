package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.FloatingToolbar;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public final class FloatingToolbar {
    private static final MenuItem.OnMenuItemClickListener NO_OP_MENUITEM_CLICK_LISTENER = FloatingToolbar$$ExternalSyntheticLambda0.INSTANCE;
    public static final int STYLE_BLACK = 2;
    public static final int STYLE_DIALOG = 0;
    public static final int STYLE_THEME = 1;
    private static final int TRANSLATE = 16908353;
    private int currentStyle;
    private Menu mMenu;
    private final FloatingToolbarPopup mPopup;
    private int mSuggestedWidth;
    private final View mWindowView;
    private final Theme.ResourcesProvider resourcesProvider;
    private final Rect mContentRect = new Rect();
    private final Rect mPreviousContentRect = new Rect();
    private List<MenuItem> mShowingMenuItems = new ArrayList();
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
    private boolean mWidthChanged = true;
    private final View.OnLayoutChangeListener mOrientationChangeHandler = new View.OnLayoutChangeListener() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.1
        private final Rect mNewRect = new Rect();
        private final Rect mOldRect = new Rect();

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int newLeft, int newRight, int newTop, int newBottom, int oldLeft, int oldRight, int oldTop, int oldBottom) {
            this.mNewRect.set(newLeft, newRight, newTop, newBottom);
            this.mOldRect.set(oldLeft, oldRight, oldTop, oldBottom);
            if (FloatingToolbar.this.mPopup.isShowing() && !this.mNewRect.equals(this.mOldRect)) {
                FloatingToolbar.this.mWidthChanged = true;
                FloatingToolbar.this.updateLayout();
            }
        }
    };
    private final Comparator<MenuItem> mMenuItemComparator = FloatingToolbar$$ExternalSyntheticLambda1.INSTANCE;

    public static /* synthetic */ boolean lambda$static$0(MenuItem item) {
        return false;
    }

    public static /* synthetic */ int lambda$new$1(MenuItem menuItem1, MenuItem menuItem2) {
        return menuItem1.getOrder() - menuItem2.getOrder();
    }

    public FloatingToolbar(Context context, View windowView, int style, Theme.ResourcesProvider resourcesProvider) {
        this.mWindowView = windowView;
        this.currentStyle = style;
        this.resourcesProvider = resourcesProvider;
        this.mPopup = new FloatingToolbarPopup(context, windowView);
    }

    public FloatingToolbar setMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public FloatingToolbar setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener menuItemClickListener) {
        if (menuItemClickListener != null) {
            this.mMenuItemClickListener = menuItemClickListener;
        } else {
            this.mMenuItemClickListener = NO_OP_MENUITEM_CLICK_LISTENER;
        }
        return this;
    }

    public FloatingToolbar setContentRect(Rect rect) {
        this.mContentRect.set(rect);
        return this;
    }

    public FloatingToolbar setSuggestedWidth(int suggestedWidth) {
        int difference = Math.abs(suggestedWidth - this.mSuggestedWidth);
        double d = this.mSuggestedWidth;
        Double.isNaN(d);
        this.mWidthChanged = ((double) difference) > d * 0.2d;
        this.mSuggestedWidth = suggestedWidth;
        return this;
    }

    public FloatingToolbar show() {
        registerOrientationHandler();
        doShow();
        return this;
    }

    public FloatingToolbar updateLayout() {
        if (this.mPopup.isShowing()) {
            doShow();
        }
        return this;
    }

    public void dismiss() {
        unregisterOrientationHandler();
        this.mPopup.dismiss();
    }

    public void hide() {
        this.mPopup.hide();
    }

    public boolean isShowing() {
        return this.mPopup.isShowing();
    }

    public boolean isHidden() {
        return this.mPopup.isHidden();
    }

    public void setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
        if (this.mPopup.setOutsideTouchable(outsideTouchable, onDismiss) && isShowing()) {
            dismiss();
            doShow();
        }
    }

    private void doShow() {
        List<MenuItem> menuItems = getVisibleAndEnabledMenuItems(this.mMenu);
        Collections.sort(menuItems, this.mMenuItemComparator);
        if (!isCurrentlyShowing(menuItems) || this.mWidthChanged) {
            this.mPopup.dismiss();
            this.mPopup.layoutMenuItems(menuItems, this.mMenuItemClickListener, this.mSuggestedWidth);
            this.mShowingMenuItems = menuItems;
        }
        if (!this.mPopup.isShowing()) {
            this.mPopup.show(this.mContentRect);
        } else if (!this.mPreviousContentRect.equals(this.mContentRect)) {
            this.mPopup.updateCoordinates(this.mContentRect);
        }
        this.mWidthChanged = false;
        this.mPreviousContentRect.set(this.mContentRect);
    }

    private boolean isCurrentlyShowing(List<MenuItem> menuItems) {
        if (this.mShowingMenuItems == null || menuItems.size() != this.mShowingMenuItems.size()) {
            return false;
        }
        int size = menuItems.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = menuItems.get(i);
            MenuItem showingItem = this.mShowingMenuItems.get(i);
            if (menuItem.getItemId() != showingItem.getItemId() || !TextUtils.equals(menuItem.getTitle(), showingItem.getTitle()) || !ColorUtils$$ExternalSyntheticBackport0.m(menuItem.getIcon(), showingItem.getIcon()) || menuItem.getGroupId() != showingItem.getGroupId()) {
                return false;
            }
        }
        return true;
    }

    private List<MenuItem> getVisibleAndEnabledMenuItems(Menu menu) {
        List<MenuItem> menuItems = new ArrayList<>();
        for (int i = 0; menu != null && i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isVisible() && menuItem.isEnabled()) {
                Menu subMenu = menuItem.getSubMenu();
                if (subMenu != null) {
                    menuItems.addAll(getVisibleAndEnabledMenuItems(subMenu));
                } else if (menuItem.getItemId() != TRANSLATE) {
                    menuItems.add(menuItem);
                }
            }
        }
        return menuItems;
    }

    private void registerOrientationHandler() {
        unregisterOrientationHandler();
        this.mWindowView.addOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    private void unregisterOrientationHandler() {
        this.mWindowView.removeOnLayoutChangeListener(this.mOrientationChangeHandler);
    }

    /* loaded from: classes4.dex */
    public final class FloatingToolbarPopup {
        private static final int MAX_OVERFLOW_SIZE = 4;
        private static final int MIN_OVERFLOW_SIZE = 2;
        private final Drawable mArrow;
        private final AnimationSet mCloseOverflowAnimation;
        private final ViewGroup mContentContainer;
        private final Context mContext;
        private final AnimatorSet mDismissAnimation;
        private final Interpolator mFastOutLinearInInterpolator;
        private final Interpolator mFastOutSlowInInterpolator;
        private boolean mHidden;
        private final AnimatorSet mHideAnimation;
        private final int mIconTextSpacing;
        private boolean mIsOverflowOpen;
        private final Interpolator mLinearOutSlowInInterpolator;
        private Size mMainPanelSize;
        private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
        private final AnimationSet mOpenOverflowAnimation;
        private boolean mOpenOverflowUpwards;
        private final Drawable mOverflow;
        private final ImageButton mOverflowButton;
        private final Size mOverflowButtonSize;
        private Size mOverflowPanelSize;
        private final OverflowPanelViewHelper mOverflowPanelViewHelper;
        private final View mParent;
        private final PopupWindow mPopupWindow;
        private final AnimatorSet mShowAnimation;
        private final AnimatedVectorDrawable mToArrow;
        private final AnimatedVectorDrawable mToOverflow;
        private int mTransitionDurationScale;
        private final Rect mViewPortOnScreen = new Rect();
        private final Point mCoordsOnWindow = new Point();
        private final int[] mTmpCoords = new int[2];
        private final Region mTouchableRegion = new Region();
        private final Runnable mPreparePopupContentRTLHelper = new Runnable() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.1
            @Override // java.lang.Runnable
            public void run() {
                FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
                FloatingToolbarPopup.this.mContentContainer.setAlpha(1.0f);
            }
        };
        private boolean mDismissed = true;
        private final View.OnClickListener mMenuItemButtonOnClickListener = new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if ((v.getTag() instanceof MenuItem) && FloatingToolbarPopup.this.mOnMenuItemClickListener != null) {
                    FloatingToolbarPopup.this.mOnMenuItemClickListener.onMenuItemClick((MenuItem) v.getTag());
                }
            }
        };
        private final int mMarginHorizontal = AndroidUtilities.dp(16.0f);
        private final int mMarginVertical = AndroidUtilities.dp(8.0f);
        private final int mLineHeight = AndroidUtilities.dp(48.0f);
        private final Interpolator mLogAccelerateInterpolator = new LogAccelerateInterpolator();
        private final ViewGroup mMainPanel = createMainPanel();
        private final OverflowPanel mOverflowPanel = createOverflowPanel();

        public FloatingToolbarPopup(Context context, View parent) {
            FloatingToolbar.this = r6;
            this.mParent = parent;
            this.mContext = context;
            ViewGroup createContentContainer = r6.createContentContainer(context);
            this.mContentContainer = createContentContainer;
            this.mPopupWindow = FloatingToolbar.createPopupWindow(createContentContainer);
            int dp = AndroidUtilities.dp(8.0f);
            this.mIconTextSpacing = dp;
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
            this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
            this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
            Drawable mutate = context.getDrawable(R.drawable.ft_avd_tooverflow).mutate();
            this.mArrow = mutate;
            mutate.setAutoMirrored(true);
            Drawable mutate2 = context.getDrawable(R.drawable.ft_avd_toarrow).mutate();
            this.mOverflow = mutate2;
            mutate2.setAutoMirrored(true);
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) context.getDrawable(R.drawable.ft_avd_toarrow_animation).mutate();
            this.mToArrow = animatedVectorDrawable;
            animatedVectorDrawable.setAutoMirrored(true);
            AnimatedVectorDrawable animatedVectorDrawable2 = (AnimatedVectorDrawable) context.getDrawable(R.drawable.ft_avd_tooverflow_animation).mutate();
            this.mToOverflow = animatedVectorDrawable2;
            animatedVectorDrawable2.setAutoMirrored(true);
            ImageButton createOverflowButton = createOverflowButton();
            this.mOverflowButton = createOverflowButton;
            this.mOverflowButtonSize = measure(createOverflowButton);
            this.mOverflowPanelViewHelper = new OverflowPanelViewHelper(context, dp);
            Animation.AnimationListener mOverflowAnimationListener = createOverflowAnimationListener();
            AnimationSet animationSet = new AnimationSet(true);
            this.mOpenOverflowAnimation = animationSet;
            animationSet.setAnimationListener(mOverflowAnimationListener);
            AnimationSet animationSet2 = new AnimationSet(true);
            this.mCloseOverflowAnimation = animationSet2;
            animationSet2.setAnimationListener(mOverflowAnimationListener);
            this.mShowAnimation = FloatingToolbar.createEnterAnimation(createContentContainer);
            this.mDismissAnimation = FloatingToolbar.createExitAnimation(createContentContainer, 150, new AnonymousClass3(r6));
            this.mHideAnimation = FloatingToolbar.createExitAnimation(createContentContainer, 0, new AnonymousClass4(r6));
        }

        /* renamed from: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$3 */
        /* loaded from: classes4.dex */
        public class AnonymousClass3 extends AnimatorListenerAdapter {
            final /* synthetic */ FloatingToolbar val$this$0;

            AnonymousClass3(FloatingToolbar floatingToolbar) {
                FloatingToolbarPopup.this = this$1;
                this.val$this$0 = floatingToolbar;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new Runnable() { // from class: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FloatingToolbar.FloatingToolbarPopup.AnonymousClass3.this.m1428xe3adac20();
                    }
                });
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup$3 */
            public /* synthetic */ void m1428xe3adac20() {
                FloatingToolbarPopup.this.mPopupWindow.dismiss();
                FloatingToolbarPopup.this.mContentContainer.removeAllViews();
            }
        }

        /* renamed from: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$4 */
        /* loaded from: classes4.dex */
        public class AnonymousClass4 extends AnimatorListenerAdapter {
            final /* synthetic */ FloatingToolbar val$this$0;

            AnonymousClass4(FloatingToolbar floatingToolbar) {
                FloatingToolbarPopup.this = this$1;
                this.val$this$0 = floatingToolbar;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new Runnable() { // from class: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FloatingToolbar.FloatingToolbarPopup.AnonymousClass4.this.m1429xe3adac21();
                    }
                });
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup$4 */
            public /* synthetic */ void m1429xe3adac21() {
                FloatingToolbarPopup.this.mPopupWindow.dismiss();
            }
        }

        public boolean setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
            boolean ret = false;
            if (this.mPopupWindow.isOutsideTouchable() ^ outsideTouchable) {
                this.mPopupWindow.setOutsideTouchable(outsideTouchable);
                this.mPopupWindow.setFocusable(!outsideTouchable);
                ret = true;
            }
            this.mPopupWindow.setOnDismissListener(onDismiss);
            return ret;
        }

        public void layoutMenuItems(List<MenuItem> menuItems, MenuItem.OnMenuItemClickListener menuItemClickListener, int suggestedWidth) {
            this.mOnMenuItemClickListener = menuItemClickListener;
            cancelOverflowAnimations();
            clearPanels();
            List<MenuItem> menuItems2 = layoutMainPanelItems(menuItems, getAdjustedToolbarWidth(suggestedWidth));
            if (!menuItems2.isEmpty()) {
                layoutOverflowPanelItems(menuItems2);
            }
            updatePopupSize();
        }

        public void show(Rect contentRectOnScreen) {
            if (isShowing()) {
                return;
            }
            this.mHidden = false;
            this.mDismissed = false;
            cancelDismissAndHideAnimations();
            cancelOverflowAnimations();
            refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
            preparePopupContent();
            this.mPopupWindow.showAtLocation(this.mParent, 0, this.mCoordsOnWindow.x, this.mCoordsOnWindow.y);
            setTouchableSurfaceInsetsComputer();
            runShowAnimation();
        }

        public void dismiss() {
            if (this.mDismissed) {
                return;
            }
            this.mHidden = false;
            this.mDismissed = true;
            this.mHideAnimation.cancel();
            runDismissAnimation();
            setZeroTouchableSurface();
        }

        public void hide() {
            if (!isShowing()) {
                return;
            }
            this.mHidden = true;
            runHideAnimation();
            setZeroTouchableSurface();
        }

        public boolean isShowing() {
            return !this.mDismissed && !this.mHidden;
        }

        public boolean isHidden() {
            return this.mHidden;
        }

        public void updateCoordinates(Rect contentRectOnScreen) {
            if (!isShowing() || !this.mPopupWindow.isShowing()) {
                return;
            }
            cancelOverflowAnimations();
            refreshCoordinatesAndOverflowDirection(contentRectOnScreen);
            preparePopupContent();
            this.mPopupWindow.update(this.mCoordsOnWindow.x, this.mCoordsOnWindow.y, this.mPopupWindow.getWidth(), this.mPopupWindow.getHeight());
        }

        private void refreshCoordinatesAndOverflowDirection(Rect contentRectOnScreen) {
            int minimumOverflowHeightWithMargin;
            refreshViewPort();
            int x = Math.min(contentRectOnScreen.centerX() - (this.mPopupWindow.getWidth() / 2), this.mViewPortOnScreen.right - this.mPopupWindow.getWidth());
            int availableHeightAboveContent = contentRectOnScreen.top - this.mViewPortOnScreen.top;
            int availableHeightBelowContent = this.mViewPortOnScreen.bottom - contentRectOnScreen.bottom;
            int margin = this.mMarginVertical * 2;
            int toolbarHeightWithVerticalMargin = this.mLineHeight + margin;
            if (!hasOverflow()) {
                if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin) {
                    minimumOverflowHeightWithMargin = contentRectOnScreen.top - toolbarHeightWithVerticalMargin;
                } else if (availableHeightBelowContent >= toolbarHeightWithVerticalMargin) {
                    minimumOverflowHeightWithMargin = contentRectOnScreen.bottom;
                } else {
                    int y = this.mLineHeight;
                    if (availableHeightBelowContent >= y) {
                        minimumOverflowHeightWithMargin = contentRectOnScreen.bottom - this.mMarginVertical;
                    } else {
                        minimumOverflowHeightWithMargin = Math.max(this.mViewPortOnScreen.top, contentRectOnScreen.top - toolbarHeightWithVerticalMargin);
                    }
                }
            } else {
                int y2 = calculateOverflowHeight(2);
                int minimumOverflowHeightWithMargin2 = y2 + margin;
                int availableHeightThroughContentDown = (this.mViewPortOnScreen.bottom - contentRectOnScreen.top) + toolbarHeightWithVerticalMargin;
                int availableHeightThroughContentUp = (contentRectOnScreen.bottom - this.mViewPortOnScreen.top) + toolbarHeightWithVerticalMargin;
                if (availableHeightAboveContent >= minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(availableHeightAboveContent - margin);
                    int y3 = contentRectOnScreen.top - this.mPopupWindow.getHeight();
                    this.mOpenOverflowUpwards = true;
                    minimumOverflowHeightWithMargin = y3;
                } else if (availableHeightAboveContent >= toolbarHeightWithVerticalMargin && availableHeightThroughContentDown >= minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(availableHeightThroughContentDown - margin);
                    int y4 = contentRectOnScreen.top - toolbarHeightWithVerticalMargin;
                    this.mOpenOverflowUpwards = false;
                    minimumOverflowHeightWithMargin = y4;
                } else if (availableHeightBelowContent >= minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(availableHeightBelowContent - margin);
                    int y5 = contentRectOnScreen.bottom;
                    this.mOpenOverflowUpwards = false;
                    minimumOverflowHeightWithMargin = y5;
                } else if (availableHeightBelowContent < toolbarHeightWithVerticalMargin || this.mViewPortOnScreen.height() < minimumOverflowHeightWithMargin2) {
                    updateOverflowHeight(this.mViewPortOnScreen.height() - margin);
                    int y6 = this.mViewPortOnScreen.top;
                    this.mOpenOverflowUpwards = false;
                    minimumOverflowHeightWithMargin = y6;
                } else {
                    updateOverflowHeight(availableHeightThroughContentUp - margin);
                    int y7 = (contentRectOnScreen.bottom + toolbarHeightWithVerticalMargin) - this.mPopupWindow.getHeight();
                    this.mOpenOverflowUpwards = true;
                    minimumOverflowHeightWithMargin = y7;
                }
            }
            this.mParent.getRootView().getLocationOnScreen(this.mTmpCoords);
            int[] iArr = this.mTmpCoords;
            int rootViewLeftOnScreen = iArr[0];
            int rootViewTopOnScreen = iArr[1];
            this.mParent.getRootView().getLocationInWindow(this.mTmpCoords);
            int[] iArr2 = this.mTmpCoords;
            int rootViewLeftOnWindow = iArr2[0];
            int rootViewTopOnWindow = iArr2[1];
            int windowLeftOnScreen = rootViewLeftOnScreen - rootViewLeftOnWindow;
            int windowTopOnScreen = rootViewTopOnScreen - rootViewTopOnWindow;
            this.mCoordsOnWindow.set(Math.max(0, x - windowLeftOnScreen), Math.max(0, minimumOverflowHeightWithMargin - windowTopOnScreen));
        }

        private void runShowAnimation() {
            this.mShowAnimation.start();
        }

        private void runDismissAnimation() {
            this.mDismissAnimation.start();
        }

        private void runHideAnimation() {
            this.mHideAnimation.start();
        }

        private void cancelDismissAndHideAnimations() {
            this.mDismissAnimation.cancel();
            this.mHideAnimation.cancel();
        }

        private void cancelOverflowAnimations() {
            this.mContentContainer.clearAnimation();
            this.mMainPanel.animate().cancel();
            this.mOverflowPanel.animate().cancel();
            this.mToArrow.stop();
            this.mToOverflow.stop();
        }

        private void openOverflow() {
            final int targetWidth = this.mOverflowPanelSize.getWidth();
            final int targetHeight = this.mOverflowPanelSize.getHeight();
            final int startWidth = this.mContentContainer.getWidth();
            final int startHeight = this.mContentContainer.getHeight();
            final float startY = this.mContentContainer.getY();
            final float left = this.mContentContainer.getX();
            final float right = left + this.mContentContainer.getWidth();
            Animation widthAnimation = new Animation() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.5
                @Override // android.view.animation.Animation
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaWidth = (int) ((targetWidth - startWidth) * interpolatedTime);
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, startWidth + deltaWidth);
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(left);
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        FloatingToolbarPopup.this.mOverflowPanel.setX(0.0f);
                        return;
                    }
                    FloatingToolbarPopup.this.mContentContainer.setX(right - FloatingToolbarPopup.this.mContentContainer.getWidth());
                    FloatingToolbarPopup.this.mMainPanel.setX(FloatingToolbarPopup.this.mContentContainer.getWidth() - startWidth);
                    FloatingToolbarPopup.this.mOverflowPanel.setX(FloatingToolbarPopup.this.mContentContainer.getWidth() - targetWidth);
                }
            };
            Animation heightAnimation = new Animation() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.6
                @Override // android.view.animation.Animation
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaHeight = (int) ((targetHeight - startHeight) * interpolatedTime);
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, startHeight + deltaHeight);
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(startY - (FloatingToolbarPopup.this.mContentContainer.getHeight() - startHeight));
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float overflowButtonStartX = this.mOverflowButton.getX();
            final float overflowButtonTargetX = isInRTLMode() ? (targetWidth + overflowButtonStartX) - this.mOverflowButton.getWidth() : (overflowButtonStartX - targetWidth) + this.mOverflowButton.getWidth();
            Animation overflowButtonAnimation = new Animation() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.7
                @Override // android.view.animation.Animation
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    float f = overflowButtonStartX;
                    float overflowButtonX = f + ((overflowButtonTargetX - f) * interpolatedTime);
                    float deltaContainerWidth = FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : FloatingToolbarPopup.this.mContentContainer.getWidth() - startWidth;
                    float actualOverflowButtonX = overflowButtonX + deltaContainerWidth;
                    FloatingToolbarPopup.this.mOverflowButton.setX(actualOverflowButtonX);
                }
            };
            widthAnimation.setInterpolator(this.mLogAccelerateInterpolator);
            widthAnimation.setDuration(getAdjustedDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            heightAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            heightAnimation.setDuration(getAdjustedDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            overflowButtonAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            overflowButtonAnimation.setDuration(getAdjustedDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            this.mOpenOverflowAnimation.getAnimations().clear();
            this.mOpenOverflowAnimation.addAnimation(widthAnimation);
            this.mOpenOverflowAnimation.addAnimation(heightAnimation);
            this.mOpenOverflowAnimation.addAnimation(overflowButtonAnimation);
            this.mContentContainer.startAnimation(this.mOpenOverflowAnimation);
            this.mIsOverflowOpen = true;
            this.mMainPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(250L).start();
            this.mOverflowPanel.setAlpha(1.0f);
        }

        private void closeOverflow() {
            final int targetWidth = this.mMainPanelSize.getWidth();
            final int startWidth = this.mContentContainer.getWidth();
            final float left = this.mContentContainer.getX();
            final float right = left + this.mContentContainer.getWidth();
            Animation widthAnimation = new Animation() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.8
                @Override // android.view.animation.Animation
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaWidth = (int) ((targetWidth - startWidth) * interpolatedTime);
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setWidth(floatingToolbarPopup.mContentContainer, startWidth + deltaWidth);
                    if (FloatingToolbarPopup.this.isInRTLMode()) {
                        FloatingToolbarPopup.this.mContentContainer.setX(left);
                        FloatingToolbarPopup.this.mMainPanel.setX(0.0f);
                        FloatingToolbarPopup.this.mOverflowPanel.setX(0.0f);
                        return;
                    }
                    FloatingToolbarPopup.this.mContentContainer.setX(right - FloatingToolbarPopup.this.mContentContainer.getWidth());
                    FloatingToolbarPopup.this.mMainPanel.setX(FloatingToolbarPopup.this.mContentContainer.getWidth() - targetWidth);
                    FloatingToolbarPopup.this.mOverflowPanel.setX(FloatingToolbarPopup.this.mContentContainer.getWidth() - startWidth);
                }
            };
            final int targetHeight = this.mMainPanelSize.getHeight();
            final int startHeight = this.mContentContainer.getHeight();
            final float bottom = this.mContentContainer.getY() + this.mContentContainer.getHeight();
            Animation heightAnimation = new Animation() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.9
                @Override // android.view.animation.Animation
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    int deltaHeight = (int) ((targetHeight - startHeight) * interpolatedTime);
                    FloatingToolbarPopup floatingToolbarPopup = FloatingToolbarPopup.this;
                    floatingToolbarPopup.setHeight(floatingToolbarPopup.mContentContainer, startHeight + deltaHeight);
                    if (FloatingToolbarPopup.this.mOpenOverflowUpwards) {
                        FloatingToolbarPopup.this.mContentContainer.setY(bottom - FloatingToolbarPopup.this.mContentContainer.getHeight());
                        FloatingToolbarPopup.this.positionContentYCoordinatesIfOpeningOverflowUpwards();
                    }
                }
            };
            final float overflowButtonStartX = this.mOverflowButton.getX();
            final float overflowButtonTargetX = isInRTLMode() ? (overflowButtonStartX - startWidth) + this.mOverflowButton.getWidth() : (startWidth + overflowButtonStartX) - this.mOverflowButton.getWidth();
            Animation overflowButtonAnimation = new Animation() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.10
                @Override // android.view.animation.Animation
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    float f = overflowButtonStartX;
                    float overflowButtonX = f + ((overflowButtonTargetX - f) * interpolatedTime);
                    float deltaContainerWidth = FloatingToolbarPopup.this.isInRTLMode() ? 0.0f : FloatingToolbarPopup.this.mContentContainer.getWidth() - startWidth;
                    float actualOverflowButtonX = overflowButtonX + deltaContainerWidth;
                    FloatingToolbarPopup.this.mOverflowButton.setX(actualOverflowButtonX);
                }
            };
            widthAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            widthAnimation.setDuration(getAdjustedDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            heightAnimation.setInterpolator(this.mLogAccelerateInterpolator);
            heightAnimation.setDuration(getAdjustedDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            overflowButtonAnimation.setInterpolator(this.mFastOutSlowInInterpolator);
            overflowButtonAnimation.setDuration(getAdjustedDuration(ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            this.mCloseOverflowAnimation.getAnimations().clear();
            this.mCloseOverflowAnimation.addAnimation(widthAnimation);
            this.mCloseOverflowAnimation.addAnimation(heightAnimation);
            this.mCloseOverflowAnimation.addAnimation(overflowButtonAnimation);
            this.mContentContainer.startAnimation(this.mCloseOverflowAnimation);
            this.mIsOverflowOpen = false;
            this.mMainPanel.animate().alpha(1.0f).withLayer().setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100L).start();
            this.mOverflowPanel.animate().alpha(0.0f).withLayer().setInterpolator(this.mLinearOutSlowInInterpolator).setDuration(150L).start();
        }

        public void setPanelsStatesAtRestingPosition() {
            this.mOverflowButton.setEnabled(true);
            this.mOverflowPanel.awakenScrollBars();
            if (this.mIsOverflowOpen) {
                Size containerSize = this.mOverflowPanelSize;
                setSize(this.mContentContainer, containerSize);
                this.mMainPanel.setAlpha(0.0f);
                this.mMainPanel.setVisibility(4);
                this.mOverflowPanel.setAlpha(1.0f);
                this.mOverflowPanel.setVisibility(0);
                this.mOverflowButton.setImageDrawable(this.mArrow);
                this.mOverflowButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
                if (isInRTLMode()) {
                    this.mContentContainer.setX(this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX(containerSize.getWidth() - this.mOverflowButtonSize.getWidth());
                    this.mOverflowPanel.setX(0.0f);
                } else {
                    this.mContentContainer.setX((this.mPopupWindow.getWidth() - containerSize.getWidth()) - this.mMarginHorizontal);
                    this.mMainPanel.setX(-this.mContentContainer.getX());
                    this.mOverflowButton.setX(0.0f);
                    this.mOverflowPanel.setX(0.0f);
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY(this.mMarginVertical);
                    this.mMainPanel.setY(containerSize.getHeight() - this.mContentContainer.getHeight());
                    this.mOverflowButton.setY(containerSize.getHeight() - this.mOverflowButtonSize.getHeight());
                    this.mOverflowPanel.setY(0.0f);
                    return;
                }
                this.mContentContainer.setY(this.mMarginVertical);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                this.mOverflowPanel.setY(this.mOverflowButtonSize.getHeight());
                return;
            }
            Size containerSize2 = this.mMainPanelSize;
            setSize(this.mContentContainer, containerSize2);
            this.mMainPanel.setAlpha(1.0f);
            this.mMainPanel.setVisibility(0);
            this.mOverflowPanel.setAlpha(0.0f);
            this.mOverflowPanel.setVisibility(4);
            this.mOverflowButton.setImageDrawable(this.mOverflow);
            this.mOverflowButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            if (hasOverflow()) {
                if (isInRTLMode()) {
                    this.mContentContainer.setX(this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX(0.0f);
                    this.mOverflowPanel.setX(0.0f);
                } else {
                    this.mContentContainer.setX((this.mPopupWindow.getWidth() - containerSize2.getWidth()) - this.mMarginHorizontal);
                    this.mMainPanel.setX(0.0f);
                    this.mOverflowButton.setX(containerSize2.getWidth() - this.mOverflowButtonSize.getWidth());
                    this.mOverflowPanel.setX(containerSize2.getWidth() - this.mOverflowPanelSize.getWidth());
                }
                if (this.mOpenOverflowUpwards) {
                    this.mContentContainer.setY((this.mMarginVertical + this.mOverflowPanelSize.getHeight()) - containerSize2.getHeight());
                    this.mMainPanel.setY(0.0f);
                    this.mOverflowButton.setY(0.0f);
                    this.mOverflowPanel.setY(containerSize2.getHeight() - this.mOverflowPanelSize.getHeight());
                    return;
                }
                this.mContentContainer.setY(this.mMarginVertical);
                this.mMainPanel.setY(0.0f);
                this.mOverflowButton.setY(0.0f);
                this.mOverflowPanel.setY(this.mOverflowButtonSize.getHeight());
                return;
            }
            this.mContentContainer.setX(this.mMarginHorizontal);
            this.mContentContainer.setY(this.mMarginVertical);
            this.mMainPanel.setX(0.0f);
            this.mMainPanel.setY(0.0f);
        }

        private void updateOverflowHeight(int suggestedHeight) {
            if (hasOverflow()) {
                int maxItemSize = (suggestedHeight - this.mOverflowButtonSize.getHeight()) / this.mLineHeight;
                int newHeight = calculateOverflowHeight(maxItemSize);
                if (this.mOverflowPanelSize.getHeight() != newHeight) {
                    this.mOverflowPanelSize = new Size(this.mOverflowPanelSize.getWidth(), newHeight);
                }
                setSize(this.mOverflowPanel, this.mOverflowPanelSize);
                if (this.mIsOverflowOpen) {
                    setSize(this.mContentContainer, this.mOverflowPanelSize);
                    if (this.mOpenOverflowUpwards) {
                        int deltaHeight = this.mOverflowPanelSize.getHeight() - newHeight;
                        ViewGroup viewGroup = this.mContentContainer;
                        viewGroup.setY(viewGroup.getY() + deltaHeight);
                        ImageButton imageButton = this.mOverflowButton;
                        imageButton.setY(imageButton.getY() - deltaHeight);
                    }
                } else {
                    setSize(this.mContentContainer, this.mMainPanelSize);
                }
                updatePopupSize();
            }
        }

        private void updatePopupSize() {
            int width = 0;
            int height = 0;
            Size size = this.mMainPanelSize;
            if (size != null) {
                width = Math.max(0, size.getWidth());
                height = Math.max(0, this.mMainPanelSize.getHeight());
            }
            Size size2 = this.mOverflowPanelSize;
            if (size2 != null) {
                width = Math.max(width, size2.getWidth());
                height = Math.max(height, this.mOverflowPanelSize.getHeight());
            }
            this.mPopupWindow.setWidth((this.mMarginHorizontal * 2) + width);
            this.mPopupWindow.setHeight((this.mMarginVertical * 2) + height);
            maybeComputeTransitionDurationScale();
        }

        private void refreshViewPort() {
            this.mParent.getWindowVisibleDisplayFrame(this.mViewPortOnScreen);
        }

        private int getAdjustedToolbarWidth(int suggestedWidth) {
            int width = suggestedWidth;
            refreshViewPort();
            int maximumWidth = this.mViewPortOnScreen.width() - (AndroidUtilities.dp(16.0f) * 2);
            if (width <= 0) {
                width = AndroidUtilities.dp(400.0f);
            }
            return Math.min(width, maximumWidth);
        }

        private void setZeroTouchableSurface() {
            this.mTouchableRegion.setEmpty();
        }

        public void setContentAreaAsTouchableSurface() {
            int height;
            int width;
            if (this.mIsOverflowOpen) {
                width = this.mOverflowPanelSize.getWidth();
                height = this.mOverflowPanelSize.getHeight();
            } else {
                width = this.mMainPanelSize.getWidth();
                height = this.mMainPanelSize.getHeight();
            }
            this.mTouchableRegion.set((int) this.mContentContainer.getX(), (int) this.mContentContainer.getY(), ((int) this.mContentContainer.getX()) + width, ((int) this.mContentContainer.getY()) + height);
        }

        private void setTouchableSurfaceInsetsComputer() {
        }

        public boolean isInRTLMode() {
            return false;
        }

        private boolean hasOverflow() {
            return this.mOverflowPanelSize != null;
        }

        public List<MenuItem> layoutMainPanelItems(List<MenuItem> menuItems, int toolbarWidth) {
            int availableWidth = toolbarWidth;
            LinkedList<MenuItem> remainingMenuItems = new LinkedList<>(menuItems);
            this.mMainPanel.removeAllViews();
            this.mMainPanel.setPaddingRelative(0, 0, 0, 0);
            boolean isFirstItem = true;
            while (!remainingMenuItems.isEmpty()) {
                MenuItem menuItem = remainingMenuItems.peek();
                boolean z = true;
                boolean isLastItem = remainingMenuItems.size() == 1;
                View menuItemButton = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing, isFirstItem, isLastItem);
                if (menuItemButton instanceof LinearLayout) {
                    ((LinearLayout) menuItemButton).setGravity(17);
                }
                double d = isFirstItem ? 1.5d : 1.0d;
                double paddingStart = menuItemButton.getPaddingStart();
                Double.isNaN(paddingStart);
                int i = (int) (paddingStart * d);
                int paddingTop = menuItemButton.getPaddingTop();
                double d2 = isLastItem ? 1.5d : 1.0d;
                double paddingEnd = menuItemButton.getPaddingEnd();
                Double.isNaN(paddingEnd);
                menuItemButton.setPaddingRelative(i, paddingTop, (int) (paddingEnd * d2), menuItemButton.getPaddingBottom());
                menuItemButton.measure(0, 0);
                int menuItemButtonWidth = Math.min(menuItemButton.getMeasuredWidth(), toolbarWidth);
                boolean canFitWithOverflow = menuItemButtonWidth <= availableWidth - this.mOverflowButtonSize.getWidth();
                if (!isLastItem || menuItemButtonWidth > availableWidth) {
                    z = false;
                }
                boolean canFitNoOverflow = z;
                if (!canFitWithOverflow && !canFitNoOverflow) {
                    break;
                }
                setButtonTagAndClickListener(menuItemButton, menuItem);
                this.mMainPanel.addView(menuItemButton);
                ViewGroup.LayoutParams params = menuItemButton.getLayoutParams();
                params.width = menuItemButtonWidth;
                menuItemButton.setLayoutParams(params);
                availableWidth -= menuItemButtonWidth;
                remainingMenuItems.pop();
                isFirstItem = false;
            }
            if (!remainingMenuItems.isEmpty()) {
                this.mMainPanel.setPaddingRelative(0, 0, this.mOverflowButtonSize.getWidth(), 0);
            }
            this.mMainPanelSize = measure(this.mMainPanel);
            return remainingMenuItems;
        }

        private void updateMainPanelItemsSelectors() {
        }

        private void layoutOverflowPanelItems(List<MenuItem> menuItems) {
            ArrayAdapter<MenuItem> overflowPanelAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            overflowPanelAdapter.clear();
            int size = menuItems.size();
            for (int i = 0; i < size; i++) {
                overflowPanelAdapter.add(menuItems.get(i));
            }
            this.mOverflowPanel.setAdapter((ListAdapter) overflowPanelAdapter);
            if (this.mOpenOverflowUpwards) {
                this.mOverflowPanel.setY(0.0f);
            } else {
                this.mOverflowPanel.setY(this.mOverflowButtonSize.getHeight());
            }
            int width = Math.max(getOverflowWidth(), this.mOverflowButtonSize.getWidth());
            int height = calculateOverflowHeight(4);
            Size size2 = new Size(width, height);
            this.mOverflowPanelSize = size2;
            setSize(this.mOverflowPanel, size2);
        }

        private void preparePopupContent() {
            this.mContentContainer.removeAllViews();
            if (hasOverflow()) {
                this.mContentContainer.addView(this.mOverflowPanel);
            }
            this.mContentContainer.addView(this.mMainPanel);
            if (hasOverflow()) {
                this.mContentContainer.addView(this.mOverflowButton);
            }
            setPanelsStatesAtRestingPosition();
            setContentAreaAsTouchableSurface();
            if (isInRTLMode()) {
                this.mContentContainer.setAlpha(0.0f);
                this.mContentContainer.post(this.mPreparePopupContentRTLHelper);
            }
        }

        private void clearPanels() {
            this.mOverflowPanelSize = null;
            this.mMainPanelSize = null;
            this.mIsOverflowOpen = false;
            this.mMainPanel.removeAllViews();
            ArrayAdapter<MenuItem> overflowPanelAdapter = (ArrayAdapter) this.mOverflowPanel.getAdapter();
            overflowPanelAdapter.clear();
            this.mOverflowPanel.setAdapter((ListAdapter) overflowPanelAdapter);
            this.mContentContainer.removeAllViews();
        }

        public void positionContentYCoordinatesIfOpeningOverflowUpwards() {
            if (this.mOpenOverflowUpwards) {
                this.mMainPanel.setY(this.mContentContainer.getHeight() - this.mMainPanelSize.getHeight());
                this.mOverflowButton.setY(this.mContentContainer.getHeight() - this.mOverflowButton.getHeight());
                this.mOverflowPanel.setY(this.mContentContainer.getHeight() - this.mOverflowPanelSize.getHeight());
            }
        }

        private int getOverflowWidth() {
            int overflowWidth = 0;
            int count = this.mOverflowPanel.getAdapter().getCount();
            for (int i = 0; i < count; i++) {
                MenuItem menuItem = (MenuItem) this.mOverflowPanel.getAdapter().getItem(i);
                overflowWidth = Math.max(this.mOverflowPanelViewHelper.calculateWidth(menuItem), overflowWidth);
            }
            return overflowWidth;
        }

        private int calculateOverflowHeight(int maxItemSize) {
            int actualSize = Math.min(4, Math.min(Math.max(2, maxItemSize), this.mOverflowPanel.getCount()));
            int extension = 0;
            if (actualSize < this.mOverflowPanel.getCount()) {
                extension = (int) (this.mLineHeight * 0.5f);
            }
            return (this.mLineHeight * actualSize) + this.mOverflowButtonSize.getHeight() + extension;
        }

        private void setButtonTagAndClickListener(View menuItemButton, MenuItem menuItem) {
            menuItemButton.setTag(menuItem);
            menuItemButton.setOnClickListener(this.mMenuItemButtonOnClickListener);
        }

        private int getAdjustedDuration(int originalDuration) {
            int i = this.mTransitionDurationScale;
            if (i < 150) {
                return Math.max(originalDuration - 50, 0);
            }
            if (i > 300) {
                return originalDuration + 50;
            }
            return originalDuration;
        }

        private void maybeComputeTransitionDurationScale() {
            Size size = this.mMainPanelSize;
            if (size != null && this.mOverflowPanelSize != null) {
                int w = size.getWidth() - this.mOverflowPanelSize.getWidth();
                int h = this.mOverflowPanelSize.getHeight() - this.mMainPanelSize.getHeight();
                double sqrt = Math.sqrt((w * w) + (h * h));
                double d = this.mContentContainer.getContext().getResources().getDisplayMetrics().density;
                Double.isNaN(d);
                this.mTransitionDurationScale = (int) (sqrt / d);
            }
        }

        private ViewGroup createMainPanel() {
            return new LinearLayout(this.mContext) { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.11
                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    if (FloatingToolbarPopup.this.isOverflowAnimating() && FloatingToolbarPopup.this.mMainPanelSize != null) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(FloatingToolbarPopup.this.mMainPanelSize.getWidth(), C.BUFFER_FLAG_ENCRYPTED);
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return FloatingToolbarPopup.this.isOverflowAnimating();
                }
            };
        }

        private ImageButton createOverflowButton() {
            int color;
            final ImageButton overflowButton = new ImageButton(this.mContext);
            overflowButton.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(48.0f)));
            overflowButton.setPaddingRelative(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            overflowButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            overflowButton.setImageDrawable(this.mOverflow);
            if (FloatingToolbar.this.currentStyle == 0) {
                color = FloatingToolbar.this.getThemedColor(Theme.key_dialogTextBlack);
                overflowButton.setBackgroundDrawable(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor(Theme.key_listSelector), 1));
            } else if (FloatingToolbar.this.currentStyle != 2) {
                color = FloatingToolbar.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
                overflowButton.setBackgroundDrawable(Theme.createSelectorDrawable(FloatingToolbar.this.getThemedColor(Theme.key_listSelector), 1));
            } else {
                color = -328966;
                overflowButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, 1));
            }
            this.mOverflow.setTint(color);
            this.mArrow.setTint(color);
            this.mToArrow.setTint(color);
            this.mToOverflow.setTint(color);
            overflowButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FloatingToolbar.FloatingToolbarPopup.this.m1425xc169d917(overflowButton, view);
                }
            });
            return overflowButton;
        }

        /* renamed from: lambda$createOverflowButton$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup */
        public /* synthetic */ void m1425xc169d917(ImageButton overflowButton, View v) {
            if (this.mIsOverflowOpen) {
                overflowButton.setImageDrawable(this.mToOverflow);
                this.mToOverflow.start();
                closeOverflow();
                return;
            }
            overflowButton.setImageDrawable(this.mToArrow);
            this.mToArrow.start();
            openOverflow();
        }

        private OverflowPanel createOverflowPanel() {
            final OverflowPanel overflowPanel = new OverflowPanel(this);
            overflowPanel.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            overflowPanel.setDivider(null);
            overflowPanel.setDividerHeight(0);
            ArrayAdapter adapter = new ArrayAdapter<MenuItem>(this.mContext, 0) { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.12
                @Override // android.widget.ArrayAdapter, android.widget.Adapter
                public View getView(int position, View convertView, ViewGroup parent) {
                    return FloatingToolbarPopup.this.mOverflowPanelViewHelper.getView(getItem(position), FloatingToolbarPopup.this.mOverflowPanelSize.getWidth(), convertView);
                }
            };
            overflowPanel.setAdapter((ListAdapter) adapter);
            overflowPanel.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$$ExternalSyntheticLambda1
                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                    FloatingToolbar.FloatingToolbarPopup.this.m1426x73cfc3fa(overflowPanel, adapterView, view, i, j);
                }
            });
            return overflowPanel;
        }

        /* renamed from: lambda$createOverflowPanel$1$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup */
        public /* synthetic */ void m1426x73cfc3fa(OverflowPanel overflowPanel, AdapterView parent, View view, int position, long id) {
            MenuItem menuItem = (MenuItem) overflowPanel.getAdapter().getItem(position);
            MenuItem.OnMenuItemClickListener onMenuItemClickListener = this.mOnMenuItemClickListener;
            if (onMenuItemClickListener != null) {
                onMenuItemClickListener.onMenuItemClick(menuItem);
            }
        }

        public boolean isOverflowAnimating() {
            boolean overflowOpening = this.mOpenOverflowAnimation.hasStarted() && !this.mOpenOverflowAnimation.hasEnded();
            boolean overflowClosing = this.mCloseOverflowAnimation.hasStarted() && !this.mCloseOverflowAnimation.hasEnded();
            return overflowOpening || overflowClosing;
        }

        /* renamed from: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13 */
        /* loaded from: classes4.dex */
        public class AnonymousClass13 implements Animation.AnimationListener {
            AnonymousClass13() {
                FloatingToolbarPopup.this = this$1;
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                FloatingToolbarPopup.this.mOverflowButton.setEnabled(false);
                FloatingToolbarPopup.this.mMainPanel.setVisibility(0);
                FloatingToolbarPopup.this.mOverflowPanel.setVisibility(0);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                FloatingToolbarPopup.this.mContentContainer.post(new Runnable() { // from class: org.telegram.ui.ActionBar.FloatingToolbar$FloatingToolbarPopup$13$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FloatingToolbar.FloatingToolbarPopup.AnonymousClass13.this.m1427x9207d7d5();
                    }
                });
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ActionBar-FloatingToolbar$FloatingToolbarPopup$13 */
            public /* synthetic */ void m1427x9207d7d5() {
                FloatingToolbarPopup.this.setPanelsStatesAtRestingPosition();
                FloatingToolbarPopup.this.setContentAreaAsTouchableSurface();
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }
        }

        private Animation.AnimationListener createOverflowAnimationListener() {
            return new AnonymousClass13();
        }

        private Size measure(View view) {
            view.measure(0, 0);
            return new Size(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        private void setSize(View view, int width, int height) {
            view.setMinimumWidth(width);
            view.setMinimumHeight(height);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            ViewGroup.LayoutParams params2 = params == null ? new ViewGroup.LayoutParams(0, 0) : params;
            params2.width = width;
            params2.height = height;
            view.setLayoutParams(params2);
        }

        private void setSize(View view, Size size) {
            setSize(view, size.getWidth(), size.getHeight());
        }

        public void setWidth(View view, int width) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            setSize(view, width, params.height);
        }

        public void setHeight(View view, int height) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            setSize(view, params.width, height);
        }

        /* loaded from: classes4.dex */
        public final class OverflowPanel extends ListView {
            private final FloatingToolbarPopup mPopup;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            OverflowPanel(FloatingToolbarPopup popup) {
                super(popup.mContext);
                FloatingToolbarPopup.this = r2;
                this.mPopup = popup;
                setVerticalScrollBarEnabled(false);
                setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.ActionBar.FloatingToolbar.FloatingToolbarPopup.OverflowPanel.1
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
                    }
                });
                setClipToOutline(true);
            }

            @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = this.mPopup.mOverflowPanelSize.getHeight() - this.mPopup.mOverflowButtonSize.getHeight();
                int heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec2);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (this.mPopup.isOverflowAnimating()) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override // android.view.View
            protected boolean awakenScrollBars() {
                return super.awakenScrollBars();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public final class LogAccelerateInterpolator implements Interpolator {
            private final int BASE;
            private final float LOGS_SCALE;

            private LogAccelerateInterpolator() {
                FloatingToolbarPopup.this = r2;
                this.BASE = 100;
                this.LOGS_SCALE = 1.0f / computeLog(1.0f, 100);
            }

            private float computeLog(float t, int base) {
                return (float) (1.0d - Math.pow(base, -t));
            }

            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float t) {
                return 1.0f - (computeLog(1.0f - t, 100) * this.LOGS_SCALE);
            }
        }

        /* loaded from: classes4.dex */
        public final class OverflowPanelViewHelper {
            private final Context mContext;
            private final int mIconTextSpacing;
            private final int mSidePadding = AndroidUtilities.dp(18.0f);
            private final View mCalculator = createMenuButton(null);

            public OverflowPanelViewHelper(Context context, int iconTextSpacing) {
                FloatingToolbarPopup.this = r1;
                this.mContext = context;
                this.mIconTextSpacing = iconTextSpacing;
            }

            public View getView(MenuItem menuItem, int minimumWidth, View convertView) {
                if (convertView != null) {
                    FloatingToolbar.updateMenuItemButton(convertView, menuItem, this.mIconTextSpacing);
                } else {
                    convertView = createMenuButton(menuItem);
                }
                convertView.setMinimumWidth(minimumWidth);
                return convertView;
            }

            public int calculateWidth(MenuItem menuItem) {
                FloatingToolbar.updateMenuItemButton(this.mCalculator, menuItem, this.mIconTextSpacing);
                this.mCalculator.measure(0, 0);
                return this.mCalculator.getMeasuredWidth();
            }

            private View createMenuButton(MenuItem menuItem) {
                View button = FloatingToolbar.this.createMenuItemButton(this.mContext, menuItem, this.mIconTextSpacing, false, false);
                int i = this.mSidePadding;
                button.setPadding(i, 0, i, 0);
                return button;
            }
        }
    }

    public View createMenuItemButton(Context context, MenuItem menuItem, int iconTextSpacing, boolean first, boolean last) {
        LinearLayout menuItemButton = new LinearLayout(context);
        menuItemButton.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        menuItemButton.setOrientation(0);
        menuItemButton.setMinimumWidth(AndroidUtilities.dp(48.0f));
        menuItemButton.setMinimumHeight(AndroidUtilities.dp(48.0f));
        menuItemButton.setPaddingRelative(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextSize(1, 14.0f);
        textView.setFocusable(false);
        textView.setImportantForAccessibility(2);
        textView.setFocusableInTouchMode(false);
        int selectorColor = Theme.getColor(Theme.key_listSelector);
        int i = this.currentStyle;
        if (i == 0) {
            textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        } else if (i == 2) {
            textView.setTextColor(-328966);
            selectorColor = Theme.ACTION_BAR_WHITE_SELECTOR_COLOR;
        } else if (i == 1) {
            textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        }
        if (first || last) {
            int i2 = 6;
            int i3 = first ? 6 : 0;
            int i4 = last ? 6 : 0;
            int i5 = last ? 6 : 0;
            if (!first) {
                i2 = 0;
            }
            menuItemButton.setBackgroundDrawable(Theme.createRadSelectorDrawable(selectorColor, i3, i4, i5, i2));
        } else {
            menuItemButton.setBackgroundDrawable(Theme.getSelectorDrawable(selectorColor, false));
        }
        textView.setPaddingRelative(AndroidUtilities.dp(11.0f), 0, 0, 0);
        menuItemButton.addView(textView, new LinearLayout.LayoutParams(-2, AndroidUtilities.dp(48.0f)));
        if (menuItem != null) {
            updateMenuItemButton(menuItemButton, menuItem, iconTextSpacing);
        }
        return menuItemButton;
    }

    public static void updateMenuItemButton(View menuItemButton, MenuItem menuItem, int iconTextSpacing) {
        ViewGroup viewGroup = (ViewGroup) menuItemButton;
        TextView buttonText = (TextView) viewGroup.getChildAt(0);
        buttonText.setEllipsize(null);
        if (TextUtils.isEmpty(menuItem.getTitle())) {
            buttonText.setVisibility(8);
        } else {
            buttonText.setVisibility(0);
            buttonText.setText(menuItem.getTitle());
        }
        buttonText.setPaddingRelative(0, 0, 0, 0);
    }

    public ViewGroup createContentContainer(Context context) {
        RelativeLayout contentContainer = new RelativeLayout(context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        int dp = AndroidUtilities.dp(20.0f);
        layoutParams.rightMargin = dp;
        layoutParams.topMargin = dp;
        layoutParams.leftMargin = dp;
        layoutParams.bottomMargin = dp;
        contentContainer.setLayoutParams(layoutParams);
        contentContainer.setElevation(AndroidUtilities.dp(2.0f));
        contentContainer.setFocusable(true);
        contentContainer.setFocusableInTouchMode(true);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(0);
        int r = AndroidUtilities.dp(6.0f);
        shape.setCornerRadii(new float[]{r, r, r, r, r, r, r, r});
        int i = this.currentStyle;
        if (i == 0) {
            shape.setColor(getThemedColor(Theme.key_dialogBackground));
        } else if (i == 2) {
            shape.setColor(-115203550);
        } else if (i == 1) {
            shape.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
        }
        contentContainer.setBackgroundDrawable(shape);
        contentContainer.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        contentContainer.setClipToOutline(true);
        return contentContainer;
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public static PopupWindow createPopupWindow(ViewGroup content) {
        ViewGroup popupContentHolder = new LinearLayout(content.getContext());
        PopupWindow popupWindow = new PopupWindow(popupContentHolder);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        content.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        popupContentHolder.addView(content);
        return popupWindow;
    }

    public static AnimatorSet createEnterAnimation(View view) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f).setDuration(150L));
        return animation;
    }

    public static AnimatorSet createExitAnimation(View view, int startDelay, Animator.AnimatorListener listener) {
        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f).setDuration(100L));
        animation.setStartDelay(startDelay);
        animation.addListener(listener);
        return animation;
    }
}
