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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes4.dex */
public class DialogOrContactPickerActivity extends BaseFragment {
    private static final Interpolator interpolator = DialogOrContactPickerActivity$$ExternalSyntheticLambda1.INSTANCE;
    private static final int search_button = 0;
    private boolean animatingForward;
    private boolean backAnimation;
    private ContactsActivity contactsActivity;
    private DialogsActivity dialogsActivity;
    private int maximumVelocity;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private ActionBarMenuItem searchItem;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private Paint backgroundPaint = new Paint();
    private ViewPage[] viewPages = new ViewPage[2];
    private boolean swipeBackEnabled = true;

    /* loaded from: classes4.dex */
    public static class ViewPage extends FrameLayout {
        private ActionBar actionBar;
        private FrameLayout fragmentView;
        private RecyclerListView listView;
        private RecyclerListView listView2;
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

    public DialogOrContactPickerActivity() {
        Bundle args = new Bundle();
        args.putBoolean("onlySelect", true);
        args.putBoolean("checkCanWrite", false);
        args.putBoolean("resetDelegate", false);
        args.putInt("dialogsType", 9);
        DialogsActivity dialogsActivity = new DialogsActivity(args);
        this.dialogsActivity = dialogsActivity;
        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.DialogOrContactPickerActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
            public final void didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z) {
                DialogOrContactPickerActivity.this.m3330lambda$new$1$orgtelegramuiDialogOrContactPickerActivity(dialogsActivity2, arrayList, charSequence, z);
            }
        });
        this.dialogsActivity.onFragmentCreate();
        Bundle args2 = new Bundle();
        args2.putBoolean("onlyUsers", true);
        args2.putBoolean("destroyAfterSelect", true);
        args2.putBoolean("returnAsResult", true);
        args2.putBoolean("disableSections", true);
        args2.putBoolean("needFinishFragment", false);
        args2.putBoolean("resetDelegate", false);
        args2.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity = new ContactsActivity(args2);
        this.contactsActivity = contactsActivity;
        contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() { // from class: org.telegram.ui.DialogOrContactPickerActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ContactsActivity.ContactsActivityDelegate
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity2) {
                DialogOrContactPickerActivity.this.m3331lambda$new$2$orgtelegramuiDialogOrContactPickerActivity(user, str, contactsActivity2);
            }
        });
        this.contactsActivity.onFragmentCreate();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-DialogOrContactPickerActivity */
    public /* synthetic */ void m3330lambda$new$1$orgtelegramuiDialogOrContactPickerActivity(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        if (dids.isEmpty()) {
            return;
        }
        long did = ((Long) dids.get(0)).longValue();
        if (!DialogObject.isUserDialog(did)) {
            return;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(did));
        showBlockAlert(user);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-DialogOrContactPickerActivity */
    public /* synthetic */ void m3331lambda$new$2$orgtelegramuiDialogOrContactPickerActivity(TLRPC.User user, String param, ContactsActivity activity) {
        showBlockAlert(user);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("BlockUserMultiTitle", R.string.BlockUserMultiTitle));
        boolean z = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.DialogOrContactPickerActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    DialogOrContactPickerActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.DialogOrContactPickerActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.searchItem.getSearchField().requestFocus();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().closeSearchField(false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().closeSearchField(false);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip;
        scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.DialogOrContactPickerActivity.3
            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageSelected(int id, boolean forward) {
                if (DialogOrContactPickerActivity.this.viewPages[0].selectedType == id) {
                    return;
                }
                DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                dialogOrContactPickerActivity.swipeBackEnabled = id == dialogOrContactPickerActivity.scrollSlidingTextTabStrip.getFirstTabId();
                DialogOrContactPickerActivity.this.viewPages[1].selectedType = id;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                DialogOrContactPickerActivity.this.animatingForward = forward;
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageScrolled(float progress) {
                if (progress != 1.0f || DialogOrContactPickerActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DialogOrContactPickerActivity.this.animatingForward) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((-progress) * DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() - (DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() * progress));
                    } else {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() * progress);
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX((DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() * progress) - DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                    }
                    if (progress == 1.0f) {
                        ViewPage tempPage = DialogOrContactPickerActivity.this.viewPages[0];
                        DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                        DialogOrContactPickerActivity.this.viewPages[1] = tempPage;
                        DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.DialogOrContactPickerActivity.4
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent ev, boolean forward) {
                int id = DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getNextPageId(forward);
                if (id < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) ev.getX();
                DialogOrContactPickerActivity.this.actionBar.setEnabled(false);
                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                DialogOrContactPickerActivity.this.viewPages[1].selectedType = id;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                DialogOrContactPickerActivity.this.animatingForward = forward;
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                if (forward) {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(-DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                }
                return true;
            }

            @Override // android.view.View
            public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
                super.forceHasOverlappingRendering(hasOverlappingRendering);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                measureChildWithMargins(DialogOrContactPickerActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int a = 0; a < DialogOrContactPickerActivity.this.viewPages.length; a++) {
                    if (DialogOrContactPickerActivity.this.viewPages[a] != null) {
                        if (DialogOrContactPickerActivity.this.viewPages[a].listView != null) {
                            DialogOrContactPickerActivity.this.viewPages[a].listView.setPadding(0, actionBarHeight, 0, 0);
                        }
                        if (DialogOrContactPickerActivity.this.viewPages[a].listView2 != null) {
                            DialogOrContactPickerActivity.this.viewPages[a].listView2.setPadding(0, actionBarHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child != null && child.getVisibility() != 8 && child != DialogOrContactPickerActivity.this.actionBar) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DialogOrContactPickerActivity.this.parentLayout != null) {
                    DialogOrContactPickerActivity.this.parentLayout.drawHeaderShadow(canvas, DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight() + ((int) DialogOrContactPickerActivity.this.actionBar.getTranslationY()));
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
                if (DialogOrContactPickerActivity.this.tabsAnimationInProgress) {
                    boolean cancel = false;
                    int i = -1;
                    if (DialogOrContactPickerActivity.this.backAnimation) {
                        if (Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(0.0f);
                            ViewPage viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                            int measuredWidth = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                i = 1;
                            }
                            viewPage.setTranslationX(measuredWidth * i);
                            cancel = true;
                        }
                    } else if (Math.abs(DialogOrContactPickerActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                        ViewPage viewPage2 = DialogOrContactPickerActivity.this.viewPages[0];
                        int measuredWidth2 = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                        if (!DialogOrContactPickerActivity.this.animatingForward) {
                            i = 1;
                        }
                        viewPage2.setTranslationX(measuredWidth2 * i);
                        DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(0.0f);
                        cancel = true;
                    }
                    if (cancel) {
                        if (DialogOrContactPickerActivity.this.tabsAnimation != null) {
                            DialogOrContactPickerActivity.this.tabsAnimation.cancel();
                            DialogOrContactPickerActivity.this.tabsAnimation = null;
                        }
                        DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                    }
                    return DialogOrContactPickerActivity.this.tabsAnimationInProgress;
                }
                return false;
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return checkTabsAnimationInProgress() || DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                DialogOrContactPickerActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                canvas.drawRect(0.0f, DialogOrContactPickerActivity.this.actionBar.getMeasuredHeight() + DialogOrContactPickerActivity.this.actionBar.getTranslationY(), getMeasuredWidth(), getMeasuredHeight(), DialogOrContactPickerActivity.this.backgroundPaint);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent ev) {
                float velY;
                float velX;
                float dx;
                int duration;
                boolean z2 = false;
                if (DialogOrContactPickerActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
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
                    if (this.startedTracking && ((DialogOrContactPickerActivity.this.animatingForward && dx2 > 0) || (!DialogOrContactPickerActivity.this.animatingForward && dx2 < 0))) {
                        if (!prepareForMoving(ev, dx2 < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(0.0f);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(DialogOrContactPickerActivity.this.animatingForward ? DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() : -DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                            DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, 0.0f);
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
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(dx2);
                        if (DialogOrContactPickerActivity.this.animatingForward) {
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() + dx2);
                        } else {
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(dx2 - DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                        }
                        float scrollProgress = Math.abs(dx2) / DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, scrollProgress);
                    }
                } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, DialogOrContactPickerActivity.this.maximumVelocity);
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
                        float x = DialogOrContactPickerActivity.this.viewPages[0].getX();
                        DialogOrContactPickerActivity.this.tabsAnimation = new AnimatorSet();
                        DialogOrContactPickerActivity.this.backAnimation = Math.abs(x) < ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        if (!DialogOrContactPickerActivity.this.backAnimation) {
                            dx = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() - Math.abs(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, -DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            }
                        } else {
                            dx = Math.abs(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()));
                            } else {
                                DialogOrContactPickerActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], View.TRANSLATION_X, -DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()));
                            }
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setInterpolator(DialogOrContactPickerActivity.interpolator);
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
                        DialogOrContactPickerActivity.this.tabsAnimation.setDuration(Math.max(150, Math.min(duration, 600)));
                        DialogOrContactPickerActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogOrContactPickerActivity.4.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                DialogOrContactPickerActivity.this.tabsAnimation = null;
                                if (DialogOrContactPickerActivity.this.backAnimation) {
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage tempPage = DialogOrContactPickerActivity.this.viewPages[0];
                                    DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                                    DialogOrContactPickerActivity.this.viewPages[1] = tempPage;
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                    DialogOrContactPickerActivity.this.swipeBackEnabled = DialogOrContactPickerActivity.this.viewPages[0].selectedType == DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass4.this.maybeStartTracking = false;
                                AnonymousClass4.this.startedTracking = false;
                                DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DialogOrContactPickerActivity.this.tabsAnimation.start();
                        DialogOrContactPickerActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        DialogOrContactPickerActivity.this.actionBar.setEnabled(true);
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
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
        this.dialogsActivity.setParentFragment(this);
        this.contactsActivity.setParentFragment(this);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a] = new ViewPage(context) { // from class: org.telegram.ui.DialogOrContactPickerActivity.5
                @Override // android.view.View
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (DialogOrContactPickerActivity.this.tabsAnimationInProgress && DialogOrContactPickerActivity.this.viewPages[0] == this) {
                        float scrollProgress = Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) / DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, scrollProgress);
                    }
                }
            };
            frameLayout.addView(this.viewPages[a], LayoutHelper.createFrame(-1, -1.0f));
            if (a == 0) {
                this.viewPages[a].parentFragment = this.dialogsActivity;
                this.viewPages[a].listView = this.dialogsActivity.getListView();
                this.viewPages[a].listView2 = this.dialogsActivity.getSearchListView();
            } else if (a == 1) {
                this.viewPages[a].parentFragment = this.contactsActivity;
                this.viewPages[a].listView = this.contactsActivity.getListView();
                this.viewPages[a].setVisibility(8);
            }
            this.viewPages[a].listView.setScrollingTouchSlop(1);
            ViewPage[] viewPageArr2 = this.viewPages;
            viewPageArr2[a].fragmentView = (FrameLayout) viewPageArr2[a].parentFragment.getFragmentView();
            ViewPage[] viewPageArr3 = this.viewPages;
            viewPageArr3[a].actionBar = viewPageArr3[a].parentFragment.getActionBar();
            ViewPage[] viewPageArr4 = this.viewPages;
            viewPageArr4[a].addView(viewPageArr4[a].fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr5 = this.viewPages;
            viewPageArr5[a].addView(viewPageArr5[a].actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[a].actionBar.setVisibility(8);
            int i = 0;
            while (i < 2) {
                ViewPage[] viewPageArr6 = this.viewPages;
                RecyclerListView listView = i == 0 ? viewPageArr6[a].listView : viewPageArr6[a].listView2;
                if (listView != null) {
                    listView.setClipToPadding(false);
                    final RecyclerView.OnScrollListener onScrollListener = listView.getOnScrollListener();
                    listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.DialogOrContactPickerActivity.6
                        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            onScrollListener.onScrollStateChanged(recyclerView, newState);
                            if (newState != 1) {
                                int scrollY = (int) (-DialogOrContactPickerActivity.this.actionBar.getTranslationY());
                                int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                                if (scrollY != 0 && scrollY != actionBarHeight) {
                                    if (scrollY < actionBarHeight / 2) {
                                        DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, -scrollY);
                                        if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                            DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, -scrollY);
                                            return;
                                        }
                                        return;
                                    }
                                    DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, actionBarHeight - scrollY);
                                    if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                        DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, actionBarHeight - scrollY);
                                    }
                                }
                            }
                        }

                        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            onScrollListener.onScrolled(recyclerView, dx, dy);
                            if (recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView || recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView2) {
                                float currentTranslation = DialogOrContactPickerActivity.this.actionBar.getTranslationY();
                                float newTranslation = currentTranslation - dy;
                                if (newTranslation < (-ActionBar.getCurrentActionBarHeight())) {
                                    newTranslation = -ActionBar.getCurrentActionBarHeight();
                                } else if (newTranslation > 0.0f) {
                                    newTranslation = 0.0f;
                                }
                                if (newTranslation != currentTranslation) {
                                    DialogOrContactPickerActivity.this.setScrollY(newTranslation);
                                }
                            }
                        }
                    });
                }
                i++;
            }
            a++;
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onResume();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onResume();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onPause();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        return this.swipeBackEnabled;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onFragmentDestroy();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    public void setScrollY(float value) {
        this.actionBar.setTranslationY(value);
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a < viewPageArr.length) {
                viewPageArr[a].listView.setPinnedSectionOffsetY((int) value);
                if (this.viewPages[a].listView2 != null) {
                    this.viewPages[a].listView2.setPinnedSectionOffsetY((int) value);
                }
                a++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    private void showBlockAlert(final TLRPC.User user) {
        if (user == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
        builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogOrContactPickerActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DialogOrContactPickerActivity.this.m3332x7c60c050(user, dialogInterface, i);
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

    /* renamed from: lambda$showBlockAlert$3$org-telegram-ui-DialogOrContactPickerActivity */
    public /* synthetic */ void m3332x7c60c050(TLRPC.User user, DialogInterface dialogInterface, int i) {
        if (MessagesController.isSupportUser(user)) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
        } else {
            MessagesController.getInstance(this.currentAccount).blockPeer(user.id);
            AlertsCreator.showSimpleToast(this, LocaleController.getString("UserBlocked", R.string.UserBlocked));
        }
        finishFragment();
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip == null) {
            return;
        }
        scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("BlockUserChatsTitle", R.string.BlockUserChatsTitle));
        this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("BlockUserContactsTitle", R.string.BlockUserContactsTitle));
        this.scrollSlidingTextTabStrip.setVisibility(0);
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
        if (id >= 0) {
            this.viewPages[0].selectedType = id;
        }
        this.scrollSlidingTextTabStrip.finishAddingTabs();
    }

    public void switchToCurrentSelectedMode(boolean animated) {
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a >= viewPageArr.length) {
                break;
            }
            viewPageArr[a].listView.stopScroll();
            if (this.viewPages[a].listView2 != null) {
                this.viewPages[a].listView2.stopScroll();
            }
            a++;
        }
        int i = 0;
        while (i < 2) {
            ViewPage[] viewPageArr2 = this.viewPages;
            RecyclerListView listView = i == 0 ? viewPageArr2[animated].listView : viewPageArr2[animated ? 1 : 0].listView2;
            if (listView != null) {
                listView.getAdapter();
                listView.setPinnedHeaderShadowDrawable(null);
                if (this.actionBar.getTranslationY() != 0.0f) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) listView.getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
                }
            }
            i++;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabActiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabUnactiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabLine));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, Theme.key_actionBarTabSelector));
        arrayList.addAll(this.dialogsActivity.getThemeDescriptions());
        arrayList.addAll(this.contactsActivity.getThemeDescriptions());
        return arrayList;
    }
}
