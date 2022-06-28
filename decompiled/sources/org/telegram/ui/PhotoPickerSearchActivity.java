package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoPickerActivity;
/* loaded from: classes4.dex */
public class PhotoPickerSearchActivity extends BaseFragment {
    private static final Interpolator interpolator = PhotoPickerSearchActivity$$ExternalSyntheticLambda0.INSTANCE;
    private static final int search_button = 0;
    private boolean animatingForward;
    private boolean backAnimation;
    private ChatActivity chatActivity;
    private EditTextEmoji commentTextView;
    private PhotoPickerActivity gifsSearch;
    private PhotoPickerActivity imagesSearch;
    private int maximumVelocity;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private ActionBarMenuItem searchItem;
    private int selectPhotoType;
    private boolean sendPressed;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private boolean swipeBackEnabled = true;
    private Paint backgroundPaint = new Paint();
    private ViewPage[] viewPages = new ViewPage[2];

    /* loaded from: classes4.dex */
    public static class ViewPage extends FrameLayout {
        private ActionBar actionBar;
        private FrameLayout fragmentView;
        private RecyclerListView listView;
        private BaseFragment parentFragment;
        private int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    public static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public PhotoPickerSearchActivity(HashMap<Object, Object> selectedPhotos, ArrayList<Object> selectedPhotosOrder, int selectPhotoType, boolean allowCaption, ChatActivity chatActivity) {
        this.imagesSearch = new PhotoPickerActivity(0, null, selectedPhotos, selectedPhotosOrder, selectPhotoType, allowCaption, chatActivity, false);
        this.gifsSearch = new PhotoPickerActivity(1, null, selectedPhotos, selectedPhotosOrder, selectPhotoType, allowCaption, chatActivity, false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        View view;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
        boolean z = false;
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PhotoPickerSearchActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoPickerSearchActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.PhotoPickerSearchActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().openSearchField("", false);
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().openSearchField("", false);
                PhotoPickerSearchActivity.this.searchItem.getSearchField().requestFocus();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public boolean canCollapseSearch() {
                PhotoPickerSearchActivity.this.finishFragment();
                return false;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().setSearchFieldText(editText.getText().toString());
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().setSearchFieldText(editText.getText().toString());
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchPressed(EditText editText) {
                PhotoPickerSearchActivity.this.imagesSearch.getActionBar().onSearchPressed();
                PhotoPickerSearchActivity.this.gifsSearch.getActionBar().onSearchPressed();
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setCursorColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip;
        scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.scrollSlidingTextTabStrip.setColors(Theme.key_chat_attachActiveTab, Theme.key_chat_attachActiveTab, Theme.key_chat_attachUnactiveTab, Theme.key_dialogButtonSelector);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.PhotoPickerSearchActivity.3
            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageSelected(int id, boolean forward) {
                if (PhotoPickerSearchActivity.this.viewPages[0].selectedType == id) {
                    return;
                }
                PhotoPickerSearchActivity photoPickerSearchActivity = PhotoPickerSearchActivity.this;
                photoPickerSearchActivity.swipeBackEnabled = id == photoPickerSearchActivity.scrollSlidingTextTabStrip.getFirstTabId();
                PhotoPickerSearchActivity.this.viewPages[1].selectedType = id;
                PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                PhotoPickerSearchActivity.this.animatingForward = forward;
                if (id == 0) {
                    PhotoPickerSearchActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
                } else {
                    PhotoPickerSearchActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
                }
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageScrolled(float progress) {
                if (progress != 1.0f || PhotoPickerSearchActivity.this.viewPages[1].getVisibility() == 0) {
                    if (PhotoPickerSearchActivity.this.animatingForward) {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX((-progress) * PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() - (PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() * progress));
                    } else {
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() * progress);
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX((PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() * progress) - PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = PhotoPickerSearchActivity.this.viewPages[0];
                        PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                        PhotoPickerSearchActivity.this.viewPages[1] = tempPage;
                        PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.PhotoPickerSearchActivity.4
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                PhotoPickerSearchActivity.this.actionBar.setEnabled(false);
                PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                PhotoPickerSearchActivity.this.viewPages[1].selectedType = id;
                PhotoPickerSearchActivity.this.viewPages[1].setVisibility(0);
                PhotoPickerSearchActivity.this.animatingForward = forward;
                PhotoPickerSearchActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(-PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                }
                return true;
            }

            @Override // android.view.View
            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            /* JADX WARN: Removed duplicated region for block: B:16:0x0071  */
            /* JADX WARN: Removed duplicated region for block: B:24:0x00b9  */
            @Override // android.widget.FrameLayout, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected void onMeasure(int r17, int r18) {
                /*
                    Method dump skipped, instructions count: 341
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoPickerSearchActivity.AnonymousClass4.onMeasure(int, int):void");
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int paddingBottom = 0;
                int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
                if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                    paddingBottom = PhotoPickerSearchActivity.this.commentTextView.getEmojiPadding();
                }
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int absoluteGravity = gravity & 7;
                        int verticalGravity = gravity & 112;
                        switch (absoluteGravity & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (((r - l) - width) - lp.rightMargin) - getPaddingRight();
                                break;
                            default:
                                childLeft = lp.leftMargin + getPaddingLeft();
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (PhotoPickerSearchActivity.this.commentTextView != null && PhotoPickerSearchActivity.this.commentTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                float y = PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight() + ((int) PhotoPickerSearchActivity.this.actionBar.getTranslationY());
                canvas.drawLine(0.0f, y, getWidth(), y, Theme.dividerPaint);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.globalIgnoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            public boolean checkTabsAnimationInProgress() {
                if (PhotoPickerSearchActivity.this.tabsAnimationInProgress) {
                    boolean cancel = false;
                    int i = -1;
                    if (PhotoPickerSearchActivity.this.backAnimation) {
                        if (Math.abs(PhotoPickerSearchActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                            PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(0.0f);
                            ViewPage viewPage = PhotoPickerSearchActivity.this.viewPages[1];
                            int measuredWidth = PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth();
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                i = 1;
                            }
                            viewPage.setTranslationX(measuredWidth * i);
                            cancel = true;
                        }
                    } else if (Math.abs(PhotoPickerSearchActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                        ViewPage viewPage2 = PhotoPickerSearchActivity.this.viewPages[0];
                        int measuredWidth2 = PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth();
                        if (!PhotoPickerSearchActivity.this.animatingForward) {
                            i = 1;
                        }
                        viewPage2.setTranslationX(measuredWidth2 * i);
                        PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(0.0f);
                        cancel = true;
                    }
                    if (cancel) {
                        if (PhotoPickerSearchActivity.this.tabsAnimation != null) {
                            PhotoPickerSearchActivity.this.tabsAnimation.cancel();
                            PhotoPickerSearchActivity.this.tabsAnimation = null;
                        }
                        PhotoPickerSearchActivity.this.tabsAnimationInProgress = false;
                    }
                    return PhotoPickerSearchActivity.this.tabsAnimationInProgress;
                }
                return false;
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                PhotoPickerSearchActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                canvas.drawRect(0.0f, PhotoPickerSearchActivity.this.actionBar.getMeasuredHeight() + PhotoPickerSearchActivity.this.actionBar.getTranslationY(), getMeasuredWidth(), getMeasuredHeight(), PhotoPickerSearchActivity.this.backgroundPaint);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent ev) {
                float velY;
                float velX;
                float dx;
                int duration;
                boolean z2 = false;
                if (PhotoPickerSearchActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
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
                    if (this.startedTracking && ((PhotoPickerSearchActivity.this.animatingForward && dx2 > 0) || (!PhotoPickerSearchActivity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(0.0f);
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(PhotoPickerSearchActivity.this.animatingForward ? PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() : -PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                            PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, 0.0f);
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
                        PhotoPickerSearchActivity.this.viewPages[0].setTranslationX(dx2);
                        if (PhotoPickerSearchActivity.this.animatingForward) {
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() + dx2);
                        } else {
                            PhotoPickerSearchActivity.this.viewPages[1].setTranslationX(dx2 - PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth());
                        }
                        float scrollProgress = Math.abs(dx2) / PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth();
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, scrollProgress);
                    }
                } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, PhotoPickerSearchActivity.this.maximumVelocity);
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
                        float x = PhotoPickerSearchActivity.this.viewPages[0].getX();
                        PhotoPickerSearchActivity.this.tabsAnimation = new AnimatorSet();
                        PhotoPickerSearchActivity.this.backAnimation = Math.abs(x) < ((float) PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        if (!PhotoPickerSearchActivity.this.backAnimation) {
                            dx = PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth() - Math.abs(x);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, -PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            } else {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            }
                        } else {
                            dx = Math.abs(x);
                            if (PhotoPickerSearchActivity.this.animatingForward) {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth()));
                            } else {
                                PhotoPickerSearchActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(PhotoPickerSearchActivity.this.viewPages[1], View.TRANSLATION_X, -PhotoPickerSearchActivity.this.viewPages[1].getMeasuredWidth()));
                            }
                        }
                        PhotoPickerSearchActivity.this.tabsAnimation.setInterpolator(PhotoPickerSearchActivity.interpolator);
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
                        PhotoPickerSearchActivity.this.tabsAnimation.setDuration(Math.max(150, Math.min(duration, 600)));
                        PhotoPickerSearchActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoPickerSearchActivity.4.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                PhotoPickerSearchActivity.this.tabsAnimation = null;
                                if (PhotoPickerSearchActivity.this.backAnimation) {
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage tempPage = PhotoPickerSearchActivity.this.viewPages[0];
                                    PhotoPickerSearchActivity.this.viewPages[0] = PhotoPickerSearchActivity.this.viewPages[1];
                                    PhotoPickerSearchActivity.this.viewPages[1] = tempPage;
                                    PhotoPickerSearchActivity.this.viewPages[1].setVisibility(8);
                                    PhotoPickerSearchActivity.this.swipeBackEnabled = PhotoPickerSearchActivity.this.viewPages[0].selectedType == PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                PhotoPickerSearchActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass4.this.maybeStartTracking = false;
                                AnonymousClass4.this.startedTracking = false;
                                PhotoPickerSearchActivity.this.actionBar.setEnabled(true);
                                PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        PhotoPickerSearchActivity.this.tabsAnimation.start();
                        PhotoPickerSearchActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        PhotoPickerSearchActivity.this.actionBar.setEnabled(true);
                        PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
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
        this.fragmentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setWillNotDraw(false);
        this.imagesSearch.setParentFragment(this);
        EditTextEmoji editTextEmoji = this.imagesSearch.commentTextView;
        this.commentTextView = editTextEmoji;
        editTextEmoji.setSizeNotifierLayout(sizeNotifierFrameLayout);
        for (int a = 0; a < 4; a++) {
            switch (a) {
                case 0:
                    view = this.imagesSearch.frameLayout2;
                    break;
                case 1:
                    view = this.imagesSearch.writeButtonContainer;
                    break;
                case 2:
                    view = this.imagesSearch.selectedCountView;
                    break;
                default:
                    view = this.imagesSearch.shadow;
                    break;
            }
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        this.gifsSearch.setLayoutViews(this.imagesSearch.frameLayout2, this.imagesSearch.writeButtonContainer, this.imagesSearch.selectedCountView, this.imagesSearch.shadow, this.imagesSearch.commentTextView);
        this.gifsSearch.setParentFragment(this);
        int a2 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a2 < viewPageArr.length) {
                viewPageArr[a2] = new ViewPage(context) { // from class: org.telegram.ui.PhotoPickerSearchActivity.5
                    @Override // android.view.View
                    public void setTranslationX(float translationX) {
                        super.setTranslationX(translationX);
                        if (PhotoPickerSearchActivity.this.tabsAnimationInProgress && PhotoPickerSearchActivity.this.viewPages[0] == this) {
                            float scrollProgress = Math.abs(PhotoPickerSearchActivity.this.viewPages[0].getTranslationX()) / PhotoPickerSearchActivity.this.viewPages[0].getMeasuredWidth();
                            PhotoPickerSearchActivity.this.scrollSlidingTextTabStrip.selectTabWithId(PhotoPickerSearchActivity.this.viewPages[1].selectedType, scrollProgress);
                        }
                    }
                };
                sizeNotifierFrameLayout.addView(this.viewPages[a2], LayoutHelper.createFrame(-1, -1.0f));
                if (a2 == 0) {
                    this.viewPages[a2].parentFragment = this.imagesSearch;
                    this.viewPages[a2].listView = this.imagesSearch.getListView();
                } else if (a2 == 1) {
                    this.viewPages[a2].parentFragment = this.gifsSearch;
                    this.viewPages[a2].listView = this.gifsSearch.getListView();
                    this.viewPages[a2].setVisibility(8);
                }
                this.viewPages[a2].listView.setScrollingTouchSlop(1);
                ViewPage[] viewPageArr2 = this.viewPages;
                viewPageArr2[a2].fragmentView = (FrameLayout) viewPageArr2[a2].parentFragment.getFragmentView();
                this.viewPages[a2].listView.setClipToPadding(false);
                ViewPage[] viewPageArr3 = this.viewPages;
                viewPageArr3[a2].actionBar = viewPageArr3[a2].parentFragment.getActionBar();
                ViewPage[] viewPageArr4 = this.viewPages;
                viewPageArr4[a2].addView(viewPageArr4[a2].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
                ViewPage[] viewPageArr5 = this.viewPages;
                viewPageArr5[a2].addView(viewPageArr5[a2].actionBar, LayoutHelper.createFrame(-1, -2.0f));
                this.viewPages[a2].actionBar.setVisibility(8);
                final RecyclerView.OnScrollListener onScrollListener = this.viewPages[a2].listView.getOnScrollListener();
                this.viewPages[a2].listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.PhotoPickerSearchActivity.6
                    @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        onScrollListener.onScrollStateChanged(recyclerView, newState);
                        if (newState != 1) {
                            int scrollY = (int) (-PhotoPickerSearchActivity.this.actionBar.getTranslationY());
                            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                            if (scrollY != 0 && scrollY != actionBarHeight) {
                                if (scrollY < actionBarHeight / 2) {
                                    PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                                } else {
                                    PhotoPickerSearchActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                                }
                            }
                        }
                    }

                    @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        onScrollListener.onScrolled(recyclerView, dx, dy);
                        if (recyclerView == PhotoPickerSearchActivity.this.viewPages[0].listView) {
                            float currentTranslation = PhotoPickerSearchActivity.this.actionBar.getTranslationY();
                            float newTranslation = currentTranslation - dy;
                            if (newTranslation < (-ActionBar.getCurrentActionBarHeight())) {
                                newTranslation = -ActionBar.getCurrentActionBarHeight();
                            } else if (newTranslation > 0.0f) {
                                newTranslation = 0.0f;
                            }
                            if (newTranslation != currentTranslation) {
                                PhotoPickerSearchActivity.this.setScrollY(newTranslation);
                            }
                        }
                    }
                });
                a2++;
            } else {
                sizeNotifierFrameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
                sizeNotifierFrameLayout.addView(this.imagesSearch.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
                sizeNotifierFrameLayout.addView(this.imagesSearch.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
                sizeNotifierFrameLayout.addView(this.imagesSearch.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
                updateTabs();
                switchToCurrentSelectedMode(false);
                if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
                    z = true;
                }
                this.swipeBackEnabled = z;
                int statusBarBackground = Theme.getColor(Theme.key_dialogBackground);
                if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.computePerceivedBrightness(statusBarBackground) >= 0.721f) {
                    this.fragmentView.setSystemUiVisibility(this.fragmentView.getSystemUiVisibility() | 8192);
                }
                return this.fragmentView;
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.openSearch(true);
            getParentActivity().getWindow().setSoftInputMode(SharedConfig.smoothKeyboard ? 32 : 16);
        }
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onResume();
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onResume();
        }
    }

    public void setCaption(CharSequence text) {
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.setCaption(text);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onPause();
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return this.swipeBackEnabled;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onFragmentDestroy();
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PhotoPickerActivity photoPickerActivity = this.imagesSearch;
        if (photoPickerActivity != null) {
            photoPickerActivity.onConfigurationChanged(newConfig);
        }
        PhotoPickerActivity photoPickerActivity2 = this.gifsSearch;
        if (photoPickerActivity2 != null) {
            photoPickerActivity2.onConfigurationChanged(newConfig);
        }
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

    public void searchText(String text) {
        this.searchItem.getSearchField().setText(text);
        this.searchItem.getSearchField().setSelection(text.length());
        this.actionBar.onSearchPressed();
    }

    public void setDelegate(PhotoPickerActivity.PhotoPickerActivityDelegate delegate) {
        this.imagesSearch.setDelegate(delegate);
        this.gifsSearch.setDelegate(delegate);
        this.imagesSearch.setSearchDelegate(new PhotoPickerActivity.PhotoPickerActivitySearchDelegate() { // from class: org.telegram.ui.PhotoPickerSearchActivity.7
            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivitySearchDelegate
            public void shouldSearchText(String text) {
                PhotoPickerSearchActivity.this.searchText(text);
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivitySearchDelegate
            public void shouldClearRecentSearch() {
                PhotoPickerSearchActivity.this.imagesSearch.clearRecentSearch();
                PhotoPickerSearchActivity.this.gifsSearch.clearRecentSearch();
            }
        });
        this.gifsSearch.setSearchDelegate(new PhotoPickerActivity.PhotoPickerActivitySearchDelegate() { // from class: org.telegram.ui.PhotoPickerSearchActivity.8
            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivitySearchDelegate
            public void shouldSearchText(String text) {
                PhotoPickerSearchActivity.this.searchText(text);
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivitySearchDelegate
            public void shouldClearRecentSearch() {
                PhotoPickerSearchActivity.this.imagesSearch.clearRecentSearch();
                PhotoPickerSearchActivity.this.gifsSearch.clearRecentSearch();
            }
        });
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.imagesSearch.setMaxSelectedPhotos(value, order);
        this.gifsSearch.setMaxSelectedPhotos(value, order);
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip == null) {
            return;
        }
        scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("ImagesTab2", R.string.ImagesTab2));
        this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("GifsTab2", R.string.GifsTab2));
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
        viewPageArr[animated ? 1 : 0].listView.getAdapter();
        this.viewPages[animated].listView.setPinnedHeaderShadowDrawable(null);
        if (this.actionBar.getTranslationY() != 0.0f) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) this.viewPages[animated].listView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_dialogButtonSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_chat_messagePanelHint));
        arrayList.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextView.class}, null, null, null, Theme.key_chat_attachActiveTab));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_chat_attachUnactiveTab));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_dialogButtonSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, Theme.key_chat_attachActiveTab));
        arrayList.addAll(this.imagesSearch.getThemeDescriptions());
        arrayList.addAll(this.gifsSearch.getThemeDescriptions());
        return arrayList;
    }
}
