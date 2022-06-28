package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.StateSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FilesMigrationService;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DialogsItemAnimator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.FiltersListBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PacmanAnimation;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.GroupCreateFinalActivity;
/* loaded from: classes4.dex */
public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ARCHIVE_ITEM_STATE_HIDDEN = 2;
    private static final int ARCHIVE_ITEM_STATE_PINNED = 0;
    private static final int ARCHIVE_ITEM_STATE_SHOWED = 1;
    public static final int DIALOGS_TYPE_START_ATTACH_BOT = 14;
    private static final int add_to_folder = 109;
    private static final int archive = 105;
    private static final int archive2 = 107;
    private static final int block = 106;
    private static final int clear = 103;
    private static final int delete = 102;
    private static final int mute = 104;
    private static final int pin = 100;
    private static final int pin2 = 108;
    private static final int read = 101;
    private static final int remove_from_folder = 110;
    private ValueAnimator actionBarColorAnimator;
    private ActionBarMenuSubItem addToFolderItem;
    private String addToGroupAlertString;
    private float additionalFloatingTranslation;
    private float additionalFloatingTranslation2;
    private float additionalOffset;
    private boolean afterSignup;
    private boolean allowBots;
    private boolean allowChannels;
    private boolean allowGroups;
    private boolean allowMoving;
    private boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    private boolean allowUsers;
    private boolean animatingForward;
    private ActionBarMenuItem archive2Item;
    private ActionBarMenuSubItem archiveItem;
    private boolean askingForPermissions;
    private ChatAvatarContainer avatarContainer;
    private boolean backAnimation;
    private BackDrawable backDrawable;
    private ActionBarMenuSubItem blockItem;
    private View blurredView;
    private int canClearCacheCount;
    private boolean canDeletePsaSelected;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canReportSpamCount;
    private boolean canShowFilterTabsView;
    private boolean canShowHiddenArchive;
    private int canUnarchiveCount;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private boolean checkCanWrite;
    private boolean checkingImportDialog;
    private ActionBarMenuSubItem clearItem;
    private boolean closeFragment;
    private boolean closeSearchFieldOnHide;
    private ChatActivityEnterView commentView;
    private AnimatorSet commentViewAnimator;
    private View commentViewBg;
    private ValueAnimator contactsAlphaAnimator;
    private int currentConnectionState;
    View databaseMigrationHint;
    private DialogsActivityDelegate delegate;
    private ActionBarMenuItem deleteItem;
    private int dialogChangeFinished;
    private int dialogInsertFinished;
    private int dialogRemoveFinished;
    private boolean dialogsListFrozen;
    private boolean disableActionBarScrolling;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimator;
    private ActionBarMenuItem downloadsItem;
    private boolean downloadsItemVisible;
    private float filterTabsMoveFrom;
    private float filterTabsProgress;
    private FilterTabsView filterTabsView;
    private boolean filterTabsViewIsVisible;
    private ValueAnimator filtersTabAnimator;
    private FiltersView filtersView;
    private RLottieImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    private boolean floatingForceVisible;
    private boolean floatingHidden;
    private AnimatorSet floatingProgressAnimator;
    private RadialProgressView floatingProgressView;
    private boolean floatingProgressVisible;
    private int folderId;
    private FragmentContextView fragmentContextView;
    private FragmentContextView fragmentLocationContextView;
    private ArrayList<TLRPC.Dialog> frozenDialogsList;
    private boolean hasInvoice;
    private int hasPoll;
    private int initialDialogsType;
    private String initialSearchString;
    boolean isDrawerTransition;
    boolean isSlideBackTransition;
    private ValueAnimator keyboardAnimator;
    private int lastMeasuredTopPadding;
    private int maximumVelocity;
    private boolean maybeStartTracking;
    private MenuDrawable menuDrawable;
    private int messagesCount;
    private DialogCell movingView;
    private boolean movingWas;
    private ActionBarMenuItem muteItem;
    private boolean onlySelect;
    private long openedDialogId;
    private int otherwiseReloginDays;
    private PacmanAnimation pacmanAnimation;
    private RLottieDrawable passcodeDrawable;
    private ActionBarMenuItem passcodeItem;
    private boolean passcodeItemVisible;
    private AlertDialog permissionDialog;
    private ActionBarMenuSubItem pin2Item;
    private ActionBarMenuItem pinItem;
    private int prevPosition;
    private int prevTop;
    private float progressToActionMode;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuItem proxyItem;
    private boolean proxyItemVisible;
    private ActionBarMenuSubItem readItem;
    private ActionBarMenuSubItem removeFromFolderItem;
    private AnimatorSet scrimAnimatorSet;
    private Paint scrimPaint;
    private ActionBarPopupWindow scrimPopupWindow;
    private ActionBarMenuSubItem[] scrimPopupWindowItems;
    private View scrimView;
    private boolean scrimViewAppearing;
    private Drawable scrimViewBackground;
    private boolean scrimViewSelected;
    private float scrollAdditionalOffset;
    private boolean scrollUpdated;
    private boolean scrollingManually;
    private float searchAnimationProgress;
    private boolean searchAnimationTabsDelayedCrossfade;
    private AnimatorSet searchAnimator;
    private long searchDialogId;
    private boolean searchFiltersWasShowed;
    private boolean searchIsShowed;
    private ActionBarMenuItem searchItem;
    private TLObject searchObject;
    private String searchString;
    private ViewPagerFixed.TabsView searchTabsView;
    private SearchViewPager searchViewPager;
    private boolean searchWas;
    private boolean searchWasFullyShowed;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private View selectedCountView;
    private NumberTextView selectedDialogsCountTextView;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean showSetPasswordConfirm;
    private String showingSuggestion;
    private RecyclerView sideMenu;
    ValueAnimator slideBackTransitionAnimator;
    private DialogCell slidingView;
    private boolean slowedReloadAfterDialogClick;
    private long startArchivePullingTime;
    private boolean startedTracking;
    private ActionBarMenuItem switchItem;
    private Animator tabsAlphaAnimator;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private float tabsYOffset;
    private int topPadding;
    private FrameLayout updateLayout;
    private AnimatorSet updateLayoutAnimator;
    private RadialProgress2 updateLayoutIcon;
    private boolean updatePullAfterScroll;
    private TextView updateTextView;
    private ViewPage[] viewPages;
    private boolean waitingForScrollFinished;
    private boolean whiteActionBar;
    private ImageView[] writeButton;
    private FrameLayout writeButtonContainer;
    public static boolean[] dialogsLoaded = new boolean[4];
    private static final Interpolator interpolator = DialogsActivity$$ExternalSyntheticLambda21.INSTANCE;
    public static float viewOffset = 0.0f;
    private int initialSearchType = -1;
    private final String ACTION_MODE_SEARCH_DIALOGS_TAG = "search_dialogs_action_mode";
    private float contactsAlpha = 1.0f;
    private UndoView[] undoView = new UndoView[2];
    private int[] scrimViewLocation = new int[2];
    private ArrayList<MessagesController.DialogFilter> movingDialogFilters = new ArrayList<>();
    private Paint actionBarDefaultPaint = new Paint();
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private final boolean commentViewAnimated = false;
    private RectF rect = new RectF();
    private Paint paint = new Paint(1);
    private TextPaint textPaint = new TextPaint(1);
    private boolean askAboutContacts = true;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean checkPermission = true;
    private boolean resetDelegate = true;
    private ArrayList<Long> selectedDialogs = new ArrayList<>();
    public boolean notify = true;
    private int animationIndex = -1;
    private int debugLastUpdateAction = -1;
    public final Property<DialogsActivity, Float> SCROLL_Y = new AnimationProperties.FloatProperty<DialogsActivity>("animationValue") { // from class: org.telegram.ui.DialogsActivity.1
        public void setValue(DialogsActivity object, float value) {
            object.setScrollY(value);
        }

        public Float get(DialogsActivity object) {
            return Float.valueOf(DialogsActivity.this.actionBar.getTranslationY());
        }
    };
    private int commentViewPreviousTop = -1;
    private boolean commentViewIgnoreTopUpdate = false;
    private boolean scrollBarVisible = true;
    private boolean isNextButton = false;
    float slideFragmentProgress = 1.0f;

    /* loaded from: classes4.dex */
    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    /* loaded from: classes4.dex */
    public static class ViewPage extends FrameLayout {
        private int archivePullViewState;
        private DialogsAdapter dialogsAdapter;
        private DialogsItemAnimator dialogsItemAnimator;
        private int dialogsType;
        private boolean isLocked;
        private ItemTouchHelper itemTouchhelper;
        private int lastItemsCount;
        private LinearLayoutManager layoutManager;
        private DialogsRecyclerView listView;
        private FlickerLoadingView progressView;
        private PullForegroundDrawable pullForegroundDrawable;
        private RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
        private RecyclerAnimationScrollHelper scrollHelper;
        private int selectedType;
        private SwipeController swipeController;

        static /* synthetic */ int access$11208(ViewPage x0) {
            int i = x0.lastItemsCount;
            x0.lastItemsCount = i + 1;
            return i;
        }

        static /* synthetic */ int access$11210(ViewPage x0) {
            int i = x0.lastItemsCount;
            x0.lastItemsCount = i - 1;
            return i;
        }

        public ViewPage(Context context) {
            super(context);
        }

        public boolean isDefaultDialogType() {
            int i = this.dialogsType;
            return i == 0 || i == 7 || i == 8;
        }
    }

    public static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    /* loaded from: classes4.dex */
    public class ContentView extends SizeNotifierFrameLayout {
        private boolean globalIgnoreLayout;
        private int inputFieldHeight;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker;
        private Paint actionBarSearchPaint = new Paint(1);
        private Paint windowBackgroundPaint = new Paint();
        private int[] pos = new int[2];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ContentView(Context context) {
            super(context);
            DialogsActivity.this = r2;
            this.needBlur = true;
            this.blurBehindViews.add(this);
        }

        private boolean prepareForMoving(MotionEvent ev, boolean forward) {
            int id = DialogsActivity.this.filterTabsView.getNextPageId(forward);
            if (id < 0) {
                return false;
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            DialogsActivity.this.maybeStartTracking = false;
            DialogsActivity.this.startedTracking = true;
            this.startedTrackingX = (int) (ev.getX() + DialogsActivity.this.additionalOffset);
            DialogsActivity.this.actionBar.setEnabled(false);
            DialogsActivity.this.filterTabsView.setEnabled(false);
            DialogsActivity.this.viewPages[1].selectedType = id;
            DialogsActivity.this.viewPages[1].setVisibility(0);
            DialogsActivity.this.animatingForward = forward;
            DialogsActivity.this.showScrollbars(false);
            DialogsActivity.this.switchToCurrentSelectedMode(true);
            if (forward) {
                DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth());
            } else {
                DialogsActivity.this.viewPages[1].setTranslationX(-DialogsActivity.this.viewPages[0].getMeasuredWidth());
            }
            return true;
        }

        @Override // android.view.View
        public void setPadding(int left, int top, int right, int bottom) {
            DialogsActivity.this.topPadding = top;
            DialogsActivity.this.updateContextViewPosition();
            if (DialogsActivity.this.whiteActionBar && DialogsActivity.this.searchViewPager != null) {
                DialogsActivity.this.searchViewPager.setTranslationY(DialogsActivity.this.topPadding - DialogsActivity.this.lastMeasuredTopPadding);
            } else {
                requestLayout();
            }
        }

        public boolean checkTabsAnimationInProgress() {
            if (DialogsActivity.this.tabsAnimationInProgress) {
                boolean cancel = false;
                int i = -1;
                if (DialogsActivity.this.backAnimation) {
                    if (Math.abs(DialogsActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                        DialogsActivity.this.viewPages[0].setTranslationX(0.0f);
                        ViewPage viewPage = DialogsActivity.this.viewPages[1];
                        int measuredWidth = DialogsActivity.this.viewPages[0].getMeasuredWidth();
                        if (DialogsActivity.this.animatingForward) {
                            i = 1;
                        }
                        viewPage.setTranslationX(measuredWidth * i);
                        cancel = true;
                    }
                } else if (Math.abs(DialogsActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                    ViewPage viewPage2 = DialogsActivity.this.viewPages[0];
                    int measuredWidth2 = DialogsActivity.this.viewPages[0].getMeasuredWidth();
                    if (!DialogsActivity.this.animatingForward) {
                        i = 1;
                    }
                    viewPage2.setTranslationX(measuredWidth2 * i);
                    DialogsActivity.this.viewPages[1].setTranslationX(0.0f);
                    cancel = true;
                }
                if (cancel) {
                    DialogsActivity.this.showScrollbars(true);
                    if (DialogsActivity.this.tabsAnimation != null) {
                        DialogsActivity.this.tabsAnimation.cancel();
                        DialogsActivity.this.tabsAnimation = null;
                    }
                    DialogsActivity.this.tabsAnimationInProgress = false;
                }
                return DialogsActivity.this.tabsAnimationInProgress;
            }
            return false;
        }

        public int getActionBarFullHeight() {
            float h = DialogsActivity.this.actionBar.getHeight();
            float filtersTabsHeight = 0.0f;
            if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() != 8) {
                filtersTabsHeight = DialogsActivity.this.filterTabsView.getMeasuredHeight() - ((1.0f - DialogsActivity.this.filterTabsProgress) * DialogsActivity.this.filterTabsView.getMeasuredHeight());
            }
            float searchTabsHeight = 0.0f;
            if (DialogsActivity.this.searchTabsView != null && DialogsActivity.this.searchTabsView.getVisibility() != 8) {
                searchTabsHeight = DialogsActivity.this.searchTabsView.getMeasuredHeight();
            }
            return (int) (h + ((1.0f - DialogsActivity.this.searchAnimationProgress) * filtersTabsHeight) + (DialogsActivity.this.searchAnimationProgress * searchTabsHeight));
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean result;
            if ((child != DialogsActivity.this.fragmentContextView || !DialogsActivity.this.fragmentContextView.isCallStyle()) && child != DialogsActivity.this.blurredView) {
                int i = 0;
                if (child != DialogsActivity.this.viewPages[0] && ((DialogsActivity.this.viewPages.length <= 1 || child != DialogsActivity.this.viewPages[1]) && child != DialogsActivity.this.fragmentContextView && child != DialogsActivity.this.fragmentLocationContextView && child != DialogsActivity.this.searchViewPager)) {
                    if (child == DialogsActivity.this.actionBar && DialogsActivity.this.slideFragmentProgress != 1.0f) {
                        canvas.save();
                        float s = 1.0f - ((1.0f - DialogsActivity.this.slideFragmentProgress) * 0.05f);
                        canvas.translate((DialogsActivity.this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                        float measuredWidth = DialogsActivity.this.isDrawerTransition ? getMeasuredWidth() : 0.0f;
                        if (DialogsActivity.this.actionBar.getOccupyStatusBar()) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        canvas.scale(s, s, measuredWidth, i + (ActionBar.getCurrentActionBarHeight() / 2.0f));
                        result = super.drawChild(canvas, child, drawingTime);
                        canvas.restore();
                    } else {
                        result = super.drawChild(canvas, child, drawingTime);
                    }
                } else {
                    canvas.save();
                    canvas.clipRect(0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + getActionBarFullHeight(), getMeasuredWidth(), getMeasuredHeight());
                    if (DialogsActivity.this.slideFragmentProgress != 1.0f) {
                        float s2 = 1.0f - ((1.0f - DialogsActivity.this.slideFragmentProgress) * 0.05f);
                        canvas.translate((DialogsActivity.this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                        canvas.scale(s2, s2, DialogsActivity.this.isDrawerTransition ? getMeasuredWidth() : 0.0f, (-getY()) + DialogsActivity.this.actionBar.getY() + getActionBarFullHeight());
                    }
                    result = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                }
                if (child == DialogsActivity.this.actionBar && DialogsActivity.this.parentLayout != null) {
                    int y = (int) (DialogsActivity.this.actionBar.getY() + getActionBarFullHeight());
                    DialogsActivity.this.parentLayout.drawHeaderShadow(canvas, (int) ((1.0f - DialogsActivity.this.searchAnimationProgress) * 255.0f), y);
                    if (DialogsActivity.this.searchAnimationProgress > 0.0f) {
                        if (DialogsActivity.this.searchAnimationProgress < 1.0f) {
                            int a = Theme.dividerPaint.getAlpha();
                            Theme.dividerPaint.setAlpha((int) (a * DialogsActivity.this.searchAnimationProgress));
                            canvas.drawLine(0.0f, y, getMeasuredWidth(), y, Theme.dividerPaint);
                            Theme.dividerPaint.setAlpha(a);
                        } else {
                            canvas.drawLine(0.0f, y, getMeasuredWidth(), y, Theme.dividerPaint);
                        }
                    }
                }
                return result;
            }
            return true;
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            int top;
            Paint paint;
            Paint paint2;
            int actionBarHeight = getActionBarFullHeight();
            if (!DialogsActivity.this.inPreviewMode) {
                top = (int) ((-getY()) + DialogsActivity.this.actionBar.getY());
            } else {
                top = AndroidUtilities.statusBarHeight;
            }
            boolean z = DialogsActivity.this.whiteActionBar;
            String str = Theme.key_actionBarDefault;
            if (z) {
                if (DialogsActivity.this.searchAnimationProgress != 1.0f) {
                    if (DialogsActivity.this.searchAnimationProgress == 0.0f && DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                        DialogsActivity.this.filterTabsView.setTranslationY(DialogsActivity.this.actionBar.getTranslationY());
                    }
                } else {
                    this.actionBarSearchPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    if (DialogsActivity.this.searchTabsView != null) {
                        DialogsActivity.this.searchTabsView.setTranslationY(0.0f);
                        DialogsActivity.this.searchTabsView.setAlpha(1.0f);
                        if (DialogsActivity.this.filtersView != null) {
                            DialogsActivity.this.filtersView.setTranslationY(0.0f);
                            DialogsActivity.this.filtersView.setAlpha(1.0f);
                        }
                    }
                }
                AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                Rect rect = AndroidUtilities.rectTmp2;
                if (DialogsActivity.this.searchAnimationProgress != 1.0f) {
                    paint2 = DialogsActivity.this.actionBarDefaultPaint;
                } else {
                    paint2 = this.actionBarSearchPaint;
                }
                drawBlurRect(canvas, 0.0f, rect, paint2, true);
                if (DialogsActivity.this.searchAnimationProgress > 0.0f && DialogsActivity.this.searchAnimationProgress < 1.0f) {
                    Paint paint3 = this.actionBarSearchPaint;
                    if (DialogsActivity.this.folderId != 0) {
                        str = Theme.key_actionBarDefaultArchived;
                    }
                    paint3.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor(Theme.key_windowBackgroundWhite), DialogsActivity.this.searchAnimationProgress));
                    if (DialogsActivity.this.searchIsShowed || !DialogsActivity.this.searchWasFullyShowed) {
                        canvas.save();
                        canvas.clipRect(0, top, getMeasuredWidth(), top + actionBarHeight);
                        float cX = getMeasuredWidth() - AndroidUtilities.dp(24.0f);
                        int statusBarH = DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                        float cY = statusBarH + ((DialogsActivity.this.actionBar.getMeasuredHeight() - statusBarH) / 2.0f);
                        drawBlurCircle(canvas, 0.0f, cX, cY, getMeasuredWidth() * 1.3f * DialogsActivity.this.searchAnimationProgress, this.actionBarSearchPaint, true);
                        canvas.restore();
                    } else {
                        AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                        drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, this.actionBarSearchPaint, true);
                    }
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                        DialogsActivity.this.filterTabsView.setTranslationY(actionBarHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.filterTabsView.getMeasuredHeight()));
                    }
                    if (DialogsActivity.this.searchTabsView != null) {
                        float y = actionBarHeight - (DialogsActivity.this.actionBar.getHeight() + DialogsActivity.this.searchTabsView.getMeasuredHeight());
                        float alpha = DialogsActivity.this.searchAnimationTabsDelayedCrossfade ? DialogsActivity.this.searchAnimationProgress < 0.5f ? 0.0f : (DialogsActivity.this.searchAnimationProgress - 0.5f) / 0.5f : DialogsActivity.this.searchAnimationProgress;
                        DialogsActivity.this.searchTabsView.setTranslationY(y);
                        DialogsActivity.this.searchTabsView.setAlpha(alpha);
                        if (DialogsActivity.this.filtersView != null) {
                            DialogsActivity.this.filtersView.setTranslationY(y);
                            DialogsActivity.this.filtersView.setAlpha(alpha);
                        }
                    }
                }
            } else if (!DialogsActivity.this.inPreviewMode) {
                if (DialogsActivity.this.progressToActionMode > 0.0f) {
                    Paint paint4 = this.actionBarSearchPaint;
                    if (DialogsActivity.this.folderId != 0) {
                        str = Theme.key_actionBarDefaultArchived;
                    }
                    paint4.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor(Theme.key_windowBackgroundWhite), DialogsActivity.this.progressToActionMode));
                    AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                    drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, this.actionBarSearchPaint, true);
                } else {
                    AndroidUtilities.rectTmp2.set(0, top, getMeasuredWidth(), top + actionBarHeight);
                    drawBlurRect(canvas, 0.0f, AndroidUtilities.rectTmp2, DialogsActivity.this.actionBarDefaultPaint, true);
                }
            }
            DialogsActivity.this.tabsYOffset = 0.0f;
            if (DialogsActivity.this.filtersTabAnimator == null || DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                    DialogsActivity.this.filterTabsView.setTranslationY(DialogsActivity.this.actionBar.getTranslationY());
                    DialogsActivity.this.filterTabsView.setAlpha(1.0f);
                }
            } else {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.tabsYOffset = (-(1.0f - dialogsActivity.filterTabsProgress)) * DialogsActivity.this.filterTabsView.getMeasuredHeight();
                DialogsActivity.this.filterTabsView.setTranslationY(DialogsActivity.this.actionBar.getTranslationY() + DialogsActivity.this.tabsYOffset);
                DialogsActivity.this.filterTabsView.setAlpha(DialogsActivity.this.filterTabsProgress);
                DialogsActivity.this.viewPages[0].setTranslationY((-(1.0f - DialogsActivity.this.filterTabsProgress)) * DialogsActivity.this.filterTabsMoveFrom);
            }
            DialogsActivity.this.updateContextViewPosition();
            super.dispatchDraw(canvas);
            if (DialogsActivity.this.whiteActionBar && DialogsActivity.this.searchAnimationProgress > 0.0f && DialogsActivity.this.searchAnimationProgress < 1.0f && DialogsActivity.this.searchTabsView != null) {
                this.windowBackgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.windowBackgroundPaint.setAlpha((int) (paint.getAlpha() * DialogsActivity.this.searchAnimationProgress));
                canvas.drawRect(0.0f, top + actionBarHeight, getMeasuredWidth(), DialogsActivity.this.actionBar.getMeasuredHeight() + top + DialogsActivity.this.searchTabsView.getMeasuredHeight(), this.windowBackgroundPaint);
            }
            if (DialogsActivity.this.fragmentContextView != null && DialogsActivity.this.fragmentContextView.isCallStyle()) {
                canvas.save();
                canvas.translate(DialogsActivity.this.fragmentContextView.getX(), DialogsActivity.this.fragmentContextView.getY());
                if (DialogsActivity.this.slideFragmentProgress != 1.0f) {
                    float s = 1.0f - ((1.0f - DialogsActivity.this.slideFragmentProgress) * 0.05f);
                    canvas.translate((DialogsActivity.this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - DialogsActivity.this.slideFragmentProgress), 0.0f);
                    canvas.scale(s, 1.0f, DialogsActivity.this.isDrawerTransition ? getMeasuredWidth() : 0.0f, DialogsActivity.this.fragmentContextView.getY());
                }
                DialogsActivity.this.fragmentContextView.setDrawOverlay(true);
                DialogsActivity.this.fragmentContextView.draw(canvas);
                DialogsActivity.this.fragmentContextView.setDrawOverlay(false);
                canvas.restore();
            }
            if (DialogsActivity.this.blurredView != null && DialogsActivity.this.blurredView.getVisibility() == 0) {
                if (DialogsActivity.this.blurredView.getAlpha() != 1.0f) {
                    if (DialogsActivity.this.blurredView.getAlpha() != 0.0f) {
                        canvas.saveLayerAlpha(DialogsActivity.this.blurredView.getLeft(), DialogsActivity.this.blurredView.getTop(), DialogsActivity.this.blurredView.getRight(), DialogsActivity.this.blurredView.getBottom(), (int) (DialogsActivity.this.blurredView.getAlpha() * 255.0f), 31);
                        canvas.translate(DialogsActivity.this.blurredView.getLeft(), DialogsActivity.this.blurredView.getTop());
                        DialogsActivity.this.blurredView.draw(canvas);
                        canvas.restore();
                    }
                } else {
                    DialogsActivity.this.blurredView.draw(canvas);
                }
            }
            if (DialogsActivity.this.scrimView != null) {
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), DialogsActivity.this.scrimPaint);
                canvas.save();
                getLocationInWindow(this.pos);
                canvas.translate(DialogsActivity.this.scrimViewLocation[0] - this.pos[0], DialogsActivity.this.scrimViewLocation[1] - (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0));
                if (DialogsActivity.this.scrimViewBackground != null) {
                    DialogsActivity.this.scrimViewBackground.setAlpha(DialogsActivity.this.scrimViewAppearing ? 255 : (int) ((DialogsActivity.this.scrimPaint.getAlpha() / 50.0f) * 255.0f));
                    DialogsActivity.this.scrimViewBackground.setBounds(0, 0, DialogsActivity.this.scrimView.getWidth(), DialogsActivity.this.scrimView.getHeight());
                    DialogsActivity.this.scrimViewBackground.draw(canvas);
                }
                Drawable selectorDrawable = DialogsActivity.this.filterTabsView.getListView().getSelectorDrawable();
                if (DialogsActivity.this.scrimViewAppearing && selectorDrawable != null) {
                    canvas.save();
                    Rect selectorBounds = selectorDrawable.getBounds();
                    canvas.translate(-selectorBounds.left, -selectorBounds.top);
                    selectorDrawable.draw(canvas);
                    canvas.restore();
                }
                DialogsActivity.this.scrimView.draw(canvas);
                if (DialogsActivity.this.scrimViewSelected) {
                    Drawable drawable = DialogsActivity.this.filterTabsView.getSelectorDrawable();
                    canvas.translate(-DialogsActivity.this.scrimViewLocation[0], (-drawable.getIntrinsicHeight()) - 1);
                    drawable.draw(canvas);
                }
                canvas.restore();
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            int heightSize2 = heightSize - getPaddingTop();
            if (DialogsActivity.this.doneItem != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) DialogsActivity.this.doneItem.getLayoutParams();
                layoutParams.topMargin = DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                layoutParams.height = ActionBar.getCurrentActionBarHeight();
            }
            measureChildWithMargins(DialogsActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int keyboardSize = measureKeyboardHeight();
            int childCount = getChildCount();
            float f = 0.0f;
            if (DialogsActivity.this.commentView != null) {
                measureChildWithMargins(DialogsActivity.this.commentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                Object tag = DialogsActivity.this.commentView.getTag();
                if (tag != null && tag.equals(2)) {
                    if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        heightSize2 -= DialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                } else {
                    this.inputFieldHeight = 0;
                }
                if (SharedConfig.smoothKeyboard && DialogsActivity.this.commentView.isPopupShowing()) {
                    DialogsActivity.this.fragmentView.setTranslationY(0.0f);
                    for (int a = 0; a < DialogsActivity.this.viewPages.length; a++) {
                        if (DialogsActivity.this.viewPages[a] != null) {
                            DialogsActivity.this.viewPages[a].setTranslationY(0.0f);
                        }
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.actionBar.setTranslationY(0.0f);
                    }
                    DialogsActivity.this.searchViewPager.setTranslationY(0.0f);
                }
            }
            int i = 0;
            while (i < childCount) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() != 8 && child != DialogsActivity.this.commentView) {
                    if (child != DialogsActivity.this.actionBar) {
                        if (child instanceof DatabaseMigrationHint) {
                            int contentWidthSpec = View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED);
                            int contentHeightSpec = View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (((View.MeasureSpec.getSize(heightMeasureSpec) + keyboardSize) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - DialogsActivity.this.actionBar.getMeasuredHeight()), C.BUFFER_FLAG_ENCRYPTED);
                            child.measure(contentWidthSpec, contentHeightSpec);
                        } else if (!(child instanceof ViewPage)) {
                            if (child == DialogsActivity.this.searchViewPager) {
                                DialogsActivity.this.searchViewPager.setTranslationY(0.0f);
                                int contentWidthSpec2 = View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED);
                                int contentHeightSpec2 = View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), ((((View.MeasureSpec.getSize(heightMeasureSpec) + keyboardSize) - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight())) - DialogsActivity.this.topPadding) - (DialogsActivity.this.searchTabsView == null ? 0 : AndroidUtilities.dp(44.0f)), C.BUFFER_FLAG_ENCRYPTED);
                                child.measure(contentWidthSpec2, contentHeightSpec2);
                                child.setPivotX(child.getMeasuredWidth() / 2);
                            } else if (DialogsActivity.this.commentView != null && DialogsActivity.this.commentView.isPopupView(child)) {
                                if (AndroidUtilities.isInMultiwindow) {
                                    if (AndroidUtilities.isTablet()) {
                                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), C.BUFFER_FLAG_ENCRYPTED));
                                    } else {
                                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(((heightSize2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), C.BUFFER_FLAG_ENCRYPTED));
                                    }
                                } else {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                                }
                            } else {
                                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                            }
                        } else {
                            int contentWidthSpec3 = View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED);
                            int h = (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) ? (((heightSize2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight())) - DialogsActivity.this.topPadding : (((heightSize2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)) - AndroidUtilities.dp(44.0f)) - DialogsActivity.this.topPadding;
                            if (DialogsActivity.this.filtersTabAnimator != null && DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                                h = (int) (h + DialogsActivity.this.filterTabsMoveFrom);
                            } else {
                                child.setTranslationY(f);
                            }
                            int transitionPadding = (DialogsActivity.this.isSlideBackTransition || DialogsActivity.this.isDrawerTransition) ? (int) (h * 0.05f) : 0;
                            child.setPadding(child.getPaddingLeft(), child.getPaddingTop(), child.getPaddingRight(), transitionPadding);
                            child.measure(contentWidthSpec3, View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), h + transitionPadding), C.BUFFER_FLAG_ENCRYPTED));
                            child.setPivotX(child.getMeasuredWidth() / 2);
                        }
                    }
                }
                i++;
                f = 0.0f;
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int paddingBottom;
            View child;
            int count;
            int childLeft;
            int childTop;
            int count2 = getChildCount();
            Object tag = DialogsActivity.this.commentView != null ? DialogsActivity.this.commentView.getTag() : null;
            int keyboardSize = measureKeyboardHeight();
            int i = 2;
            if (tag != null && tag.equals(2)) {
                paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsActivity.this.commentView.getEmojiPadding();
            } else {
                paddingBottom = 0;
            }
            setBottomClip(paddingBottom);
            DialogsActivity dialogsActivity = DialogsActivity.this;
            dialogsActivity.lastMeasuredTopPadding = dialogsActivity.topPadding;
            int i2 = -1;
            while (i2 < count2) {
                if (i2 == -1) {
                    child = DialogsActivity.this.commentView;
                } else {
                    child = getChildAt(i2);
                }
                if (child == null) {
                    count = count2;
                } else if (child.getVisibility() == 8) {
                    count = count2;
                } else {
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
                            childLeft = ((((r - l) - width) / i) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            childLeft = (r - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
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
                    if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(child)) {
                        if (child != DialogsActivity.this.filterTabsView && child != DialogsActivity.this.searchTabsView) {
                            if (child == DialogsActivity.this.filtersView) {
                                count = count2;
                            } else if (child == DialogsActivity.this.searchViewPager) {
                                count = count2;
                                childTop = (DialogsActivity.this.onlySelect ? 0 : DialogsActivity.this.actionBar.getMeasuredHeight()) + DialogsActivity.this.topPadding + (DialogsActivity.this.searchTabsView == null ? 0 : AndroidUtilities.dp(44.0f));
                            } else {
                                count = count2;
                                if (child instanceof DatabaseMigrationHint) {
                                    childTop = DialogsActivity.this.actionBar.getMeasuredHeight();
                                } else if (child instanceof ViewPage) {
                                    if (!DialogsActivity.this.onlySelect) {
                                        if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                                            childTop = DialogsActivity.this.actionBar.getMeasuredHeight();
                                        } else {
                                            childTop = AndroidUtilities.dp(44.0f);
                                        }
                                    }
                                    childTop += DialogsActivity.this.topPadding;
                                } else if (child instanceof FragmentContextView) {
                                    childTop += DialogsActivity.this.actionBar.getMeasuredHeight();
                                }
                            }
                        } else {
                            count = count2;
                        }
                        childTop = DialogsActivity.this.actionBar.getMeasuredHeight();
                    } else if (AndroidUtilities.isInMultiwindow) {
                        childTop = (DialogsActivity.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                        count = count2;
                    } else {
                        childTop = DialogsActivity.this.commentView.getBottom();
                        count = count2;
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
                i2++;
                count2 = count;
                i = 2;
            }
            DialogsActivity.this.searchViewPager.setKeyboardHeight(keyboardSize);
            notifyHeightChanged();
            DialogsActivity.this.updateContextViewPosition();
            DialogsActivity.this.updateCommentView();
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            int action = ev.getActionMasked();
            if ((action == 1 || action == 3) && DialogsActivity.this.actionBar.isActionModeShowed()) {
                DialogsActivity.this.allowMoving = true;
            }
            if (!checkTabsAnimationInProgress()) {
                return (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.isAnimatingIndicator()) || onTouchEvent(ev);
            }
            return true;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (DialogsActivity.this.maybeStartTracking && !DialogsActivity.this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            float velY;
            float velX;
            float dx;
            int duration;
            boolean z = false;
            if (DialogsActivity.this.parentLayout == null || DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.isEditing() || DialogsActivity.this.searching || DialogsActivity.this.parentLayout.checkTransitionAnimation() || DialogsActivity.this.parentLayout.isInPreviewMode() || DialogsActivity.this.parentLayout.isPreviewOpenAnimationInProgress() || DialogsActivity.this.parentLayout.getDrawerLayoutContainer().isDrawerOpened() || ((ev != null && !DialogsActivity.this.startedTracking && ev.getY() <= DialogsActivity.this.actionBar.getMeasuredHeight() + DialogsActivity.this.actionBar.getTranslationY()) || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5)) {
                return false;
            }
            if (ev != null) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.addMovement(ev);
            }
            if (ev != null && ev.getAction() == 0 && checkTabsAnimationInProgress()) {
                DialogsActivity.this.startedTracking = true;
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.startedTrackingX = (int) ev.getX();
                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(false);
                if (DialogsActivity.this.animatingForward) {
                    if (this.startedTrackingX >= DialogsActivity.this.viewPages[0].getMeasuredWidth() + DialogsActivity.this.viewPages[0].getTranslationX()) {
                        ViewPage page = DialogsActivity.this.viewPages[0];
                        DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                        DialogsActivity.this.viewPages[1] = page;
                        DialogsActivity.this.animatingForward = false;
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        dialogsActivity.additionalOffset = dialogsActivity.viewPages[0].getTranslationX();
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[0].selectedType, 1.0f);
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, DialogsActivity.this.additionalOffset / DialogsActivity.this.viewPages[0].getMeasuredWidth());
                        DialogsActivity.this.switchToCurrentSelectedMode(true);
                        DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                        DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
                    } else {
                        DialogsActivity dialogsActivity2 = DialogsActivity.this;
                        dialogsActivity2.additionalOffset = dialogsActivity2.viewPages[0].getTranslationX();
                    }
                } else if (this.startedTrackingX < DialogsActivity.this.viewPages[1].getMeasuredWidth() + DialogsActivity.this.viewPages[1].getTranslationX()) {
                    ViewPage page2 = DialogsActivity.this.viewPages[0];
                    DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                    DialogsActivity.this.viewPages[1] = page2;
                    DialogsActivity.this.animatingForward = true;
                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                    dialogsActivity3.additionalOffset = dialogsActivity3.viewPages[0].getTranslationX();
                    DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[0].selectedType, 1.0f);
                    DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, (-DialogsActivity.this.additionalOffset) / DialogsActivity.this.viewPages[0].getMeasuredWidth());
                    DialogsActivity.this.switchToCurrentSelectedMode(true);
                    DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                    DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
                } else {
                    DialogsActivity dialogsActivity4 = DialogsActivity.this;
                    dialogsActivity4.additionalOffset = dialogsActivity4.viewPages[0].getTranslationX();
                }
                DialogsActivity.this.tabsAnimation.removeAllListeners();
                DialogsActivity.this.tabsAnimation.cancel();
                DialogsActivity.this.tabsAnimationInProgress = false;
            } else if (ev != null && ev.getAction() == 0) {
                DialogsActivity.this.additionalOffset = 0.0f;
            }
            if (ev != null && ev.getAction() == 0 && !DialogsActivity.this.startedTracking && !DialogsActivity.this.maybeStartTracking && DialogsActivity.this.filterTabsView.getVisibility() == 0) {
                this.startedTrackingPointerId = ev.getPointerId(0);
                DialogsActivity.this.maybeStartTracking = true;
                this.startedTrackingX = (int) ev.getX();
                this.startedTrackingY = (int) ev.getY();
                this.velocityTracker.clear();
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                int dx2 = (int) ((ev.getX() - this.startedTrackingX) + DialogsActivity.this.additionalOffset);
                int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                if (DialogsActivity.this.startedTracking && ((DialogsActivity.this.animatingForward && dx2 > 0) || (!DialogsActivity.this.animatingForward && dx2 < 0))) {
                    if (!prepareForMoving(ev, dx2 < 0)) {
                        DialogsActivity.this.maybeStartTracking = true;
                        DialogsActivity.this.startedTracking = false;
                        DialogsActivity.this.viewPages[0].setTranslationX(0.0f);
                        DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.animatingForward ? DialogsActivity.this.viewPages[0].getMeasuredWidth() : -DialogsActivity.this.viewPages[0].getMeasuredWidth());
                        DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, 0.0f);
                    }
                }
                if (!DialogsActivity.this.maybeStartTracking || DialogsActivity.this.startedTracking) {
                    if (DialogsActivity.this.startedTracking) {
                        DialogsActivity.this.viewPages[0].setTranslationX(dx2);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth() + dx2);
                        } else {
                            DialogsActivity.this.viewPages[1].setTranslationX(dx2 - DialogsActivity.this.viewPages[0].getMeasuredWidth());
                        }
                        float scrollProgress = Math.abs(dx2) / DialogsActivity.this.viewPages[0].getMeasuredWidth();
                        if (!DialogsActivity.this.viewPages[1].isLocked || scrollProgress <= 0.3f) {
                            DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, scrollProgress);
                        } else {
                            dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                            DialogsActivity.this.filterTabsView.shakeLock(DialogsActivity.this.viewPages[1].selectedType);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$ContentView$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    DialogsActivity.ContentView.this.m3397x3054591d();
                                }
                            }, 200L);
                            return false;
                        }
                    }
                } else {
                    float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
                    int dxLocal = (int) (ev.getX() - this.startedTrackingX);
                    if (Math.abs(dxLocal) >= touchSlop && Math.abs(dxLocal) > dy) {
                        if (dx2 < 0) {
                            z = true;
                        }
                        prepareForMoving(ev, z);
                    }
                }
            } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                this.velocityTracker.computeCurrentVelocity(1000, DialogsActivity.this.maximumVelocity);
                if (ev != null && ev.getAction() != 3) {
                    velX = this.velocityTracker.getXVelocity();
                    velY = this.velocityTracker.getYVelocity();
                    if (!DialogsActivity.this.startedTracking && Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                        prepareForMoving(ev, velX < 0.0f);
                    }
                } else {
                    velX = 0.0f;
                    velY = 0.0f;
                }
                if (DialogsActivity.this.startedTracking) {
                    float x = DialogsActivity.this.viewPages[0].getX();
                    DialogsActivity.this.tabsAnimation = new AnimatorSet();
                    if (DialogsActivity.this.viewPages[1].isLocked) {
                        DialogsActivity.this.backAnimation = true;
                    } else if (DialogsActivity.this.additionalOffset != 0.0f) {
                        if (Math.abs(velX) <= 1500.0f) {
                            if (DialogsActivity.this.animatingForward) {
                                DialogsActivity dialogsActivity5 = DialogsActivity.this;
                                dialogsActivity5.backAnimation = dialogsActivity5.viewPages[1].getX() > ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                            } else {
                                DialogsActivity dialogsActivity6 = DialogsActivity.this;
                                dialogsActivity6.backAnimation = dialogsActivity6.viewPages[0].getX() < ((float) (DialogsActivity.this.viewPages[0].getMeasuredWidth() >> 1));
                            }
                        } else {
                            DialogsActivity dialogsActivity7 = DialogsActivity.this;
                            dialogsActivity7.backAnimation = !dialogsActivity7.animatingForward ? velX < 0.0f : velX > 0.0f;
                        }
                    } else {
                        DialogsActivity.this.backAnimation = Math.abs(x) < ((float) DialogsActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                    }
                    if (!DialogsActivity.this.backAnimation) {
                        dx = DialogsActivity.this.viewPages[0].getMeasuredWidth() - Math.abs(x);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, -DialogsActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                        } else {
                            DialogsActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, DialogsActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                        }
                    } else {
                        dx = Math.abs(x);
                        if (DialogsActivity.this.animatingForward) {
                            DialogsActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, DialogsActivity.this.viewPages[1].getMeasuredWidth()));
                        } else {
                            DialogsActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DialogsActivity.this.viewPages[1], View.TRANSLATION_X, -DialogsActivity.this.viewPages[1].getMeasuredWidth()));
                        }
                    }
                    DialogsActivity.this.tabsAnimation.setInterpolator(DialogsActivity.interpolator);
                    int width = getMeasuredWidth();
                    int halfWidth = width / 2;
                    float distanceRatio = Math.min(1.0f, (dx * 1.0f) / width);
                    float distance = halfWidth + (halfWidth * AndroidUtilities.distanceInfluenceForSnapDuration(distanceRatio));
                    float velX2 = Math.abs(velX);
                    if (velX2 > 0.0f) {
                        duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                    } else {
                        float pageDelta = dx / getMeasuredWidth();
                        duration = (int) ((1.0f + pageDelta) * 100.0f);
                    }
                    DialogsActivity.this.tabsAnimation.setDuration(Math.max(150, Math.min(duration, 600)));
                    DialogsActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.ContentView.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.tabsAnimation = null;
                            if (!DialogsActivity.this.backAnimation) {
                                ViewPage tempPage = DialogsActivity.this.viewPages[0];
                                DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                                DialogsActivity.this.viewPages[1] = tempPage;
                                DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[0].selectedType, 1.0f);
                                DialogsActivity.this.updateCounters(false);
                                DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                                DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
                            }
                            if (DialogsActivity.this.parentLayout != null) {
                                DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
                            }
                            DialogsActivity.this.viewPages[1].setVisibility(8);
                            DialogsActivity.this.showScrollbars(true);
                            DialogsActivity.this.tabsAnimationInProgress = false;
                            DialogsActivity.this.maybeStartTracking = false;
                            DialogsActivity.this.actionBar.setEnabled(true);
                            DialogsActivity.this.filterTabsView.setEnabled(true);
                            DialogsActivity.this.checkListLoad(DialogsActivity.this.viewPages[0]);
                        }
                    });
                    DialogsActivity.this.tabsAnimation.start();
                    DialogsActivity.this.tabsAnimationInProgress = true;
                    DialogsActivity.this.startedTracking = false;
                } else {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(DialogsActivity.this.viewPages[0].selectedType == DialogsActivity.this.filterTabsView.getFirstTabId() || DialogsActivity.this.searchIsShowed || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
                    DialogsActivity.this.maybeStartTracking = false;
                    DialogsActivity.this.actionBar.setEnabled(true);
                    DialogsActivity.this.filterTabsView.setEnabled(true);
                }
                VelocityTracker velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
            }
            return DialogsActivity.this.startedTracking;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-DialogsActivity$ContentView */
        public /* synthetic */ void m3397x3054591d() {
            DialogsActivity.this.showDialog(new LimitReachedBottomSheet(DialogsActivity.this, getContext(), 3, DialogsActivity.this.currentAccount));
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
        public void drawList(Canvas blurCanvas, boolean top) {
            if (DialogsActivity.this.searchIsShowed) {
                if (DialogsActivity.this.searchViewPager != null && DialogsActivity.this.searchViewPager.getVisibility() == 0) {
                    DialogsActivity.this.searchViewPager.drawForBlur(blurCanvas);
                    return;
                }
                return;
            }
            for (int i = 0; i < DialogsActivity.this.viewPages.length; i++) {
                if (DialogsActivity.this.viewPages[i] != null && DialogsActivity.this.viewPages[i].getVisibility() == 0) {
                    for (int j = 0; j < DialogsActivity.this.viewPages[i].listView.getChildCount(); j++) {
                        View child = DialogsActivity.this.viewPages[i].listView.getChildAt(j);
                        if (child.getY() < DialogsActivity.this.viewPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)) {
                            int restore = blurCanvas.save();
                            blurCanvas.translate(DialogsActivity.this.viewPages[i].getX(), DialogsActivity.this.viewPages[i].getY() + DialogsActivity.this.viewPages[i].listView.getY() + child.getY());
                            if (child instanceof DialogCell) {
                                DialogCell cell = (DialogCell) child;
                                cell.drawingForBlur = true;
                                cell.draw(blurCanvas);
                                cell.drawingForBlur = false;
                            } else {
                                child.draw(blurCanvas);
                            }
                            blurCanvas.restoreToCount(restore);
                        }
                    }
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public class DialogsRecyclerView extends BlurredRecyclerView {
        private int appliedPaddingTop;
        private boolean ignoreLayout;
        private int lastListPadding;
        private int lastTop;
        private final ViewPage parentPage;
        private boolean firstLayout = true;
        Paint paint = new Paint();
        RectF rectF = new RectF();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DialogsRecyclerView(Context context, ViewPage page) {
            super(context);
            DialogsActivity.this = this$0;
            this.parentPage = page;
            this.additionalClipBottom = AndroidUtilities.dp(200.0f);
        }

        @Override // org.telegram.ui.Components.RecyclerListView
        protected boolean updateEmptyViewAnimated() {
            return true;
        }

        public void setViewsOffset(float viewOffset) {
            View v;
            DialogsActivity.viewOffset = viewOffset;
            int n = getChildCount();
            for (int i = 0; i < n; i++) {
                getChildAt(i).setTranslationY(viewOffset);
            }
            int i2 = this.selectorPosition;
            if (i2 != -1 && (v = getLayoutManager().findViewByPosition(this.selectorPosition)) != null) {
                this.selectorRect.set(v.getLeft(), (int) (v.getTop() + viewOffset), v.getRight(), (int) (v.getBottom() + viewOffset));
                this.selectorDrawable.setBounds(this.selectorRect);
            }
            invalidate();
        }

        public float getViewOffset() {
            return DialogsActivity.viewOffset;
        }

        @Override // android.view.ViewGroup
        public void addView(View child, int index, ViewGroup.LayoutParams params) {
            super.addView(child, index, params);
            child.setTranslationY(DialogsActivity.viewOffset);
            child.setTranslationX(0.0f);
            child.setAlpha(1.0f);
        }

        @Override // android.view.ViewGroup, android.view.ViewManager
        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            view.setAlpha(1.0f);
        }

        @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
        public void onDraw(Canvas canvas) {
            if (this.parentPage.pullForegroundDrawable != null && DialogsActivity.viewOffset != 0.0f) {
                int pTop = getPaddingTop();
                if (pTop != 0) {
                    canvas.save();
                    canvas.translate(0.0f, pTop);
                }
                this.parentPage.pullForegroundDrawable.drawOverScroll(canvas);
                if (pTop != 0) {
                    canvas.restore();
                }
            }
            super.onDraw(canvas);
        }

        @Override // org.telegram.ui.Components.BlurredRecyclerView, org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (drawMovingViewsOverlayed()) {
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    if (((view instanceof DialogCell) && ((DialogCell) view).isMoving()) || ((view instanceof DialogsAdapter.LastEmptyView) && ((DialogsAdapter.LastEmptyView) view).moving)) {
                        if (view.getAlpha() != 1.0f) {
                            this.rectF.set(view.getX(), view.getY(), view.getX() + view.getMeasuredWidth(), view.getY() + view.getMeasuredHeight());
                            canvas.saveLayerAlpha(this.rectF, (int) (view.getAlpha() * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        canvas.translate(view.getX(), view.getY());
                        canvas.drawRect(0.0f, 0.0f, view.getMeasuredWidth(), view.getMeasuredHeight(), this.paint);
                        view.draw(canvas);
                        canvas.restore();
                    }
                }
                invalidate();
            }
            if (DialogsActivity.this.slidingView != null && DialogsActivity.this.pacmanAnimation != null) {
                DialogsActivity.this.pacmanAnimation.draw(canvas, DialogsActivity.this.slidingView.getTop() + (DialogsActivity.this.slidingView.getMeasuredHeight() / 2));
            }
        }

        private boolean drawMovingViewsOverlayed() {
            return (getItemAnimator() == null || !getItemAnimator().isRunning() || (DialogsActivity.this.dialogRemoveFinished == 0 && DialogsActivity.this.dialogInsertFinished == 0 && DialogsActivity.this.dialogChangeFinished == 0)) ? false : true;
        }

        @Override // org.telegram.ui.Components.BlurredRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (drawMovingViewsOverlayed() && (child instanceof DialogCell) && ((DialogCell) child).isMoving()) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView
        public void setAdapter(RecyclerView.Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        private void checkIfAdapterValid() {
            RecyclerView.Adapter adapter = getAdapter();
            if (this.parentPage.lastItemsCount != adapter.getItemCount() && !DialogsActivity.this.dialogsListFrozen) {
                this.ignoreLayout = true;
                adapter.notifyDataSetChanged();
                this.ignoreLayout = false;
            }
        }

        @Override // org.telegram.ui.Components.BlurredRecyclerView, org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public void onMeasure(int widthSpec, int heightSpec) {
            RecyclerView.ViewHolder holder;
            int t = 0;
            if (!DialogsActivity.this.onlySelect) {
                if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                    t = DialogsActivity.this.actionBar.getMeasuredHeight();
                } else {
                    t = AndroidUtilities.dp(44.0f);
                }
            }
            int pos = this.parentPage.layoutManager.findFirstVisibleItemPosition();
            if (pos != -1 && !DialogsActivity.this.dialogsListFrozen && this.parentPage.itemTouchhelper.isIdle() && (holder = this.parentPage.listView.findViewHolderForAdapterPosition(pos)) != null) {
                int top = holder.itemView.getTop();
                this.ignoreLayout = true;
                this.parentPage.layoutManager.scrollToPositionWithOffset(pos, (int) ((top - this.lastListPadding) + DialogsActivity.this.scrollAdditionalOffset));
                this.ignoreLayout = false;
            }
            if (!DialogsActivity.this.onlySelect) {
                this.ignoreLayout = true;
                if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0) {
                    t = (!DialogsActivity.this.inPreviewMode || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight;
                } else {
                    t = ActionBar.getCurrentActionBarHeight() + (DialogsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                }
                setTopGlowOffset(t);
                setPadding(0, t, 0, 0);
                this.parentPage.progressView.setPaddingTop(t);
                this.ignoreLayout = false;
            }
            if (this.firstLayout && DialogsActivity.this.getMessagesController().dialogsLoaded) {
                if (this.parentPage.dialogsType == 0 && DialogsActivity.this.hasHiddenArchive()) {
                    this.ignoreLayout = true;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                    layoutManager.scrollToPositionWithOffset(1, (int) DialogsActivity.this.actionBar.getTranslationY());
                    this.ignoreLayout = false;
                }
                this.firstLayout = false;
            }
            checkIfAdapterValid();
            super.onMeasure(widthSpec, heightSpec);
            if (!DialogsActivity.this.onlySelect && this.appliedPaddingTop != t && DialogsActivity.this.viewPages != null && DialogsActivity.this.viewPages.length > 1) {
                DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth());
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            this.lastListPadding = getPaddingTop();
            this.lastTop = t;
            DialogsActivity.this.scrollAdditionalOffset = 0.0f;
            if ((DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) && !this.parentPage.dialogsItemAnimator.isRunning()) {
                DialogsActivity.this.onDialogAnimationFinished();
            }
        }

        @Override // org.telegram.ui.Components.BlurredRecyclerView, org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        public void toggleArchiveHidden(boolean action, DialogCell dialogCell) {
            SharedConfig.toggleArchiveHidden();
            if (SharedConfig.archiveHidden) {
                if (dialogCell != null) {
                    DialogsActivity.this.disableActionBarScrolling = true;
                    DialogsActivity.this.waitingForScrollFinished = true;
                    smoothScrollBy(0, dialogCell.getMeasuredHeight() + (dialogCell.getTop() - getPaddingTop()), CubicBezierInterpolator.EASE_OUT);
                    if (action) {
                        DialogsActivity.this.updatePullAfterScroll = true;
                    } else {
                        updatePullState();
                    }
                }
                DialogsActivity.this.getUndoView().showWithAction(0L, 6, null, null);
                return;
            }
            DialogsActivity.this.getUndoView().showWithAction(0L, 7, null, null);
            updatePullState();
            if (action && dialogCell != null) {
                dialogCell.resetPinnedArchiveState();
                dialogCell.invalidate();
            }
        }

        public void updatePullState() {
            boolean z = false;
            this.parentPage.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
            if (this.parentPage.pullForegroundDrawable != null) {
                PullForegroundDrawable pullForegroundDrawable = this.parentPage.pullForegroundDrawable;
                if (this.parentPage.archivePullViewState != 0) {
                    z = true;
                }
                pullForegroundDrawable.setWillDraw(z);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public boolean onTouchEvent(MotionEvent e) {
            LinearLayoutManager layoutManager;
            int currentPosition;
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            int action = e.getAction();
            if (action == 0) {
                setOverScrollMode(0);
            }
            if ((action == 1 || action == 3) && !this.parentPage.itemTouchhelper.isIdle() && this.parentPage.swipeController.swipingFolder) {
                this.parentPage.swipeController.swipeFolderBack = true;
                if (this.parentPage.itemTouchhelper.checkHorizontalSwipe(null, 4) != 0 && this.parentPage.swipeController.currentItemViewHolder != null) {
                    RecyclerView.ViewHolder viewHolder = this.parentPage.swipeController.currentItemViewHolder;
                    if (viewHolder.itemView instanceof DialogCell) {
                        DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                        long dialogId = dialogCell.getDialogId();
                        if (DialogObject.isFolderDialogId(dialogId)) {
                            toggleArchiveHidden(false, dialogCell);
                        } else {
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            ArrayList<TLRPC.Dialog> dialogs = dialogsActivity.getDialogsArray(dialogsActivity.currentAccount, this.parentPage.dialogsType, DialogsActivity.this.folderId, false);
                            TLRPC.Dialog dialog = dialogs.get(dialogCell.getDialogIndex());
                            if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 1) {
                                if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 3) {
                                    if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 0) {
                                        if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4) {
                                            ArrayList<Long> selectedDialogs = new ArrayList<>();
                                            selectedDialogs.add(Long.valueOf(dialogId));
                                            DialogsActivity.this.performSelectedDialogsAction(selectedDialogs, 102, true);
                                        }
                                    } else {
                                        ArrayList<Long> selectedDialogs2 = new ArrayList<>();
                                        selectedDialogs2.add(Long.valueOf(dialogId));
                                        boolean pinned = DialogsActivity.this.isDialogPinned(dialog);
                                        DialogsActivity.this.canPinCount = !pinned ? 1 : 0;
                                        DialogsActivity.this.performSelectedDialogsAction(selectedDialogs2, 100, true);
                                    }
                                } else if (!DialogsActivity.this.getMessagesController().isDialogMuted(dialogId)) {
                                    NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialogId, 3);
                                    if (BulletinFactory.canShowBulletin(DialogsActivity.this)) {
                                        BulletinFactory.createMuteBulletin(DialogsActivity.this, 3).show();
                                    }
                                } else {
                                    ArrayList<Long> selectedDialogs3 = new ArrayList<>();
                                    selectedDialogs3.add(Long.valueOf(dialogId));
                                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                                    dialogsActivity2.canMuteCount = !MessagesController.getInstance(dialogsActivity2.currentAccount).isDialogMuted(dialogId);
                                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                                    dialogsActivity3.canUnmuteCount = dialogsActivity3.canMuteCount > 0 ? 0 : 1;
                                    DialogsActivity.this.performSelectedDialogsAction(selectedDialogs3, 104, true);
                                }
                            } else {
                                ArrayList<Long> selectedDialogs4 = new ArrayList<>();
                                selectedDialogs4.add(Long.valueOf(dialogId));
                                DialogsActivity.this.canReadCount = (dialog.unread_count > 0 || dialog.unread_mark) ? 1 : 0;
                                DialogsActivity.this.performSelectedDialogsAction(selectedDialogs4, 101, true);
                            }
                        }
                    }
                }
            }
            boolean result = super.onTouchEvent(e);
            if (this.parentPage.dialogsType == 0 && ((action == 1 || action == 3) && this.parentPage.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive() && (currentPosition = (layoutManager = (LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition()) == 0)) {
                int pTop = getPaddingTop();
                View view = layoutManager.findViewByPosition(currentPosition);
                int height = (int) (AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) * 0.85f);
                int diff = (view.getTop() - pTop) + view.getMeasuredHeight();
                if (view != null) {
                    long pullingTime = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                    if (diff < height || pullingTime < 200) {
                        DialogsActivity.this.disableActionBarScrolling = true;
                        smoothScrollBy(0, diff, CubicBezierInterpolator.EASE_OUT_QUINT);
                        this.parentPage.archivePullViewState = 2;
                    } else if (this.parentPage.archivePullViewState != 1) {
                        if (getViewOffset() == 0.0f) {
                            DialogsActivity.this.disableActionBarScrolling = true;
                            smoothScrollBy(0, view.getTop() - pTop, CubicBezierInterpolator.EASE_OUT_QUINT);
                        }
                        if (!DialogsActivity.this.canShowHiddenArchive) {
                            DialogsActivity.this.canShowHiddenArchive = true;
                            performHapticFeedback(3, 2);
                            if (this.parentPage.pullForegroundDrawable != null) {
                                this.parentPage.pullForegroundDrawable.colorize(true);
                            }
                        }
                        ((DialogCell) view).startOutAnimation();
                        this.parentPage.archivePullViewState = 1;
                    }
                    if (getViewOffset() != 0.0f) {
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(getViewOffset(), 0.0f);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$DialogsRecyclerView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                DialogsActivity.DialogsRecyclerView.this.m3398xa454faae(valueAnimator2);
                            }
                        });
                        valueAnimator.setDuration(Math.max(100L, 350.0f - ((getViewOffset() / PullForegroundDrawable.getMaxOverscroll()) * 120.0f)));
                        valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                        setScrollEnabled(false);
                        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.DialogsRecyclerView.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                DialogsRecyclerView.this.setScrollEnabled(true);
                            }
                        });
                        valueAnimator.start();
                    }
                }
            }
            return result;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-DialogsActivity$DialogsRecyclerView */
        public /* synthetic */ void m3398xa454faae(ValueAnimator animation) {
            setViewsOffset(((Float) animation.getAnimatedValue()).floatValue());
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent e) {
            if (this.fastScrollAnimationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            if (e.getAction() == 0) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.allowSwipeDuringCurrentTouch = !dialogsActivity.actionBar.isActionModeShowed();
                checkIfAdapterValid();
            }
            return super.onInterceptTouchEvent(e);
        }

        @Override // org.telegram.ui.Components.RecyclerListView
        public boolean allowSelectChildAtPosition(View child) {
            if ((child instanceof HeaderCell) && !child.isClickable()) {
                return false;
            }
            return true;
        }
    }

    /* loaded from: classes4.dex */
    public class SwipeController extends ItemTouchHelper.Callback {
        private RectF buttonInstance;
        private RecyclerView.ViewHolder currentItemViewHolder;
        private ViewPage parentPage;
        private boolean swipeFolderBack;
        private boolean swipingFolder;

        public SwipeController(ViewPage page) {
            DialogsActivity.this = r1;
            this.parentPage = page;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            TLRPC.Dialog dialog;
            if (DialogsActivity.this.waitingForDialogsAnimationEnd(this.parentPage) || (DialogsActivity.this.parentLayout != null && DialogsActivity.this.parentLayout.isInPreviewMode())) {
                return 0;
            }
            if (!this.swipingFolder || !this.swipeFolderBack) {
                if (DialogsActivity.this.onlySelect || !this.parentPage.isDefaultDialogType() || DialogsActivity.this.slidingView != null || !(viewHolder.itemView instanceof DialogCell)) {
                    return 0;
                }
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                long dialogId = dialogCell.getDialogId();
                if (!DialogsActivity.this.actionBar.isActionModeShowed(null)) {
                    if ((DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 5) || !DialogsActivity.this.allowSwipeDuringCurrentTouch || (((dialogId == DialogsActivity.this.getUserConfig().clientUserId || dialogId == 777000) && SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 2) || (DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) && DialogsActivity.this.getMessagesController().promoDialogType != MessagesController.PROMO_TYPE_PSA))) {
                        return 0;
                    }
                    boolean canSwipeBack = DialogsActivity.this.folderId == 0 && (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 3 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 0 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 4);
                    if (SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) == 1) {
                        MessagesController.DialogFilter filter = null;
                        if (DialogsActivity.this.viewPages[0].dialogsType == 7 || DialogsActivity.this.viewPages[0].dialogsType == 8) {
                            filter = DialogsActivity.this.getMessagesController().selectedDialogFilter[DialogsActivity.this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
                        }
                        if (filter != null && (filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && (dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId)) != null && !filter.alwaysShow(DialogsActivity.this.currentAccount, dialog) && (dialog.unread_count > 0 || dialog.unread_mark)) {
                            canSwipeBack = false;
                        }
                    }
                    this.swipeFolderBack = false;
                    this.swipingFolder = (canSwipeBack && !DialogObject.isFolderDialogId(dialogCell.getDialogId())) || (SharedConfig.archiveHidden && DialogObject.isFolderDialogId(dialogCell.getDialogId()));
                    dialogCell.setSliding(true);
                    return makeMovementFlags(0, 4);
                }
                TLRPC.Dialog dialog2 = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                if (!DialogsActivity.this.allowMoving || dialog2 == null || !DialogsActivity.this.isDialogPinned(dialog2) || DialogObject.isFolderDialogId(dialogId)) {
                    return 0;
                }
                DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.swipeFolderBack = false;
                return makeMovementFlags(3, 0);
            }
            if (viewHolder.itemView instanceof DialogCell) {
                ((DialogCell) viewHolder.itemView).swipeCanceled = true;
            }
            this.swipingFolder = false;
            return 0;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            char c = 0;
            if (!(target.itemView instanceof DialogCell)) {
                return false;
            }
            DialogCell dialogCell = (DialogCell) target.itemView;
            long dialogId = dialogCell.getDialogId();
            TLRPC.Dialog dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
            if (dialog == null || !DialogsActivity.this.isDialogPinned(dialog) || DialogObject.isFolderDialogId(dialogId)) {
                return false;
            }
            int fromIndex = source.getAdapterPosition();
            int toIndex = target.getAdapterPosition();
            this.parentPage.dialogsAdapter.notifyItemMoved(fromIndex, toIndex);
            DialogsActivity.this.updateDialogIndices();
            if (DialogsActivity.this.viewPages[0].dialogsType != 7 && DialogsActivity.this.viewPages[0].dialogsType != 8) {
                DialogsActivity.this.movingWas = true;
            } else {
                MessagesController.DialogFilter[] dialogFilterArr = DialogsActivity.this.getMessagesController().selectedDialogFilter;
                if (DialogsActivity.this.viewPages[0].dialogsType == 8) {
                    c = 1;
                }
                MessagesController.DialogFilter filter = dialogFilterArr[c];
                if (!DialogsActivity.this.movingDialogFilters.contains(filter)) {
                    DialogsActivity.this.movingDialogFilters.add(filter);
                }
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (this.swipeFolderBack) {
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder == null) {
                DialogsActivity.this.slidingView = null;
                return;
            }
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            long dialogId = dialogCell.getDialogId();
            int i = 0;
            if (DialogObject.isFolderDialogId(dialogId)) {
                this.parentPage.listView.toggleArchiveHidden(false, dialogCell);
                return;
            }
            final TLRPC.Dialog dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
            if (dialog == null) {
                return;
            }
            if (DialogsActivity.this.getMessagesController().isPromoDialog(dialogId, false) || DialogsActivity.this.folderId != 0 || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 1) {
                DialogsActivity.this.slidingView = dialogCell;
                final int position = viewHolder.getAdapterPosition();
                final int count = this.parentPage.dialogsAdapter.getItemCount();
                Runnable finishRunnable = new Runnable() { // from class: org.telegram.ui.DialogsActivity$SwipeController$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsActivity.SwipeController.this.m3400x3f436d65(dialog, count, position);
                    }
                };
                DialogsActivity.this.setDialogsListFrozen(true);
                if (Utilities.random.nextInt(1000) == 1) {
                    if (DialogsActivity.this.pacmanAnimation == null) {
                        DialogsActivity.this.pacmanAnimation = new PacmanAnimation(this.parentPage.listView);
                    }
                    DialogsActivity.this.pacmanAnimation.setFinishRunnable(finishRunnable);
                    DialogsActivity.this.pacmanAnimation.start();
                    return;
                }
                finishRunnable.run();
                return;
            }
            ArrayList<Long> selectedDialogs = new ArrayList<>();
            selectedDialogs.add(Long.valueOf(dialogId));
            DialogsActivity dialogsActivity = DialogsActivity.this;
            if (dialog.unread_count > 0 || dialog.unread_mark) {
                i = 1;
            }
            dialogsActivity.canReadCount = i;
            DialogsActivity.this.performSelectedDialogsAction(selectedDialogs, 101, true);
        }

        /* renamed from: lambda$onSwiped$1$org-telegram-ui-DialogsActivity$SwipeController */
        public /* synthetic */ void m3400x3f436d65(final TLRPC.Dialog dialog, int count, int position) {
            RecyclerView.ViewHolder holder;
            if (DialogsActivity.this.frozenDialogsList != null) {
                DialogsActivity.this.frozenDialogsList.remove(dialog);
                final int pinnedNum = dialog.pinnedNum;
                DialogsActivity.this.slidingView = null;
                this.parentPage.listView.invalidate();
                int lastItemPosition = this.parentPage.layoutManager.findLastVisibleItemPosition();
                if (lastItemPosition == count - 1) {
                    this.parentPage.layoutManager.findViewByPosition(lastItemPosition).requestLayout();
                }
                boolean hintShowed = false;
                if (!DialogsActivity.this.getMessagesController().isPromoDialog(dialog.id, false)) {
                    int added = DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0L);
                    if (added != 2 || position != 0) {
                        this.parentPage.dialogsItemAnimator.prepareForRemove();
                        ViewPage.access$11210(this.parentPage);
                        this.parentPage.dialogsAdapter.notifyItemRemoved(position);
                        DialogsActivity.this.dialogRemoveFinished = 2;
                    }
                    if (DialogsActivity.this.folderId == 0) {
                        if (added == 2) {
                            this.parentPage.dialogsItemAnimator.prepareForRemove();
                            if (position == 0) {
                                DialogsActivity.this.dialogChangeFinished = 2;
                                DialogsActivity.this.setDialogsListFrozen(true);
                                this.parentPage.dialogsAdapter.notifyItemChanged(0);
                            } else {
                                ViewPage.access$11208(this.parentPage);
                                this.parentPage.dialogsAdapter.notifyItemInserted(0);
                                if (!SharedConfig.archiveHidden && this.parentPage.layoutManager.findFirstVisibleItemPosition() == 0) {
                                    DialogsActivity.this.disableActionBarScrolling = true;
                                    this.parentPage.listView.smoothScrollBy(0, -AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f));
                                }
                            }
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            ArrayList<TLRPC.Dialog> dialogs = dialogsActivity.getDialogsArray(dialogsActivity.currentAccount, this.parentPage.dialogsType, DialogsActivity.this.folderId, false);
                            DialogsActivity.this.frozenDialogsList.add(0, dialogs.get(0));
                        } else if (added == 1 && (holder = this.parentPage.listView.findViewHolderForAdapterPosition(0)) != null && (holder.itemView instanceof DialogCell)) {
                            DialogCell cell = (DialogCell) holder.itemView;
                            cell.checkCurrentDialogIndex(true);
                            cell.animateArchiveAvatar();
                        }
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        if (preferences.getBoolean("archivehint_l", false) || SharedConfig.archiveHidden) {
                            hintShowed = true;
                        }
                        if (!hintShowed) {
                            preferences.edit().putBoolean("archivehint_l", true).commit();
                        }
                        DialogsActivity.this.getUndoView().showWithAction(dialog.id, hintShowed ? 2 : 3, null, new Runnable() { // from class: org.telegram.ui.DialogsActivity$SwipeController$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                DialogsActivity.SwipeController.this.m3399x3fb9d364(dialog, pinnedNum);
                            }
                        });
                    }
                    if (DialogsActivity.this.folderId != 0 && DialogsActivity.this.frozenDialogsList.isEmpty()) {
                        this.parentPage.listView.setEmptyView(null);
                        this.parentPage.progressView.setVisibility(4);
                        return;
                    }
                    return;
                }
                DialogsActivity.this.getMessagesController().hidePromoDialog();
                this.parentPage.dialogsItemAnimator.prepareForRemove();
                ViewPage.access$11210(this.parentPage);
                this.parentPage.dialogsAdapter.notifyItemRemoved(position);
                DialogsActivity.this.dialogRemoveFinished = 2;
            }
        }

        /* renamed from: lambda$onSwiped$0$org-telegram-ui-DialogsActivity$SwipeController */
        public /* synthetic */ void m3399x3fb9d364(TLRPC.Dialog dialog, int pinnedNum) {
            DialogsActivity.this.dialogsListFrozen = true;
            DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, 0, pinnedNum, 0L);
            DialogsActivity.this.dialogsListFrozen = false;
            ArrayList<TLRPC.Dialog> dialogs = DialogsActivity.this.getMessagesController().getDialogs(0);
            int index = dialogs.indexOf(dialog);
            if (index < 0) {
                this.parentPage.dialogsAdapter.notifyDataSetChanged();
                return;
            }
            ArrayList<TLRPC.Dialog> archivedDialogs = DialogsActivity.this.getMessagesController().getDialogs(1);
            if (!archivedDialogs.isEmpty() || index != 1) {
                DialogsActivity.this.dialogInsertFinished = 2;
                DialogsActivity.this.setDialogsListFrozen(true);
                this.parentPage.dialogsItemAnimator.prepareForRemove();
                ViewPage.access$11208(this.parentPage);
                this.parentPage.dialogsAdapter.notifyItemInserted(index);
            }
            if (archivedDialogs.isEmpty()) {
                dialogs.remove(0);
                if (index == 1) {
                    DialogsActivity.this.dialogChangeFinished = 2;
                    DialogsActivity.this.setDialogsListFrozen(true);
                    this.parentPage.dialogsAdapter.notifyItemChanged(0);
                    return;
                }
                DialogsActivity.this.frozenDialogsList.remove(0);
                this.parentPage.dialogsItemAnimator.prepareForRemove();
                ViewPage.access$11210(this.parentPage);
                this.parentPage.dialogsAdapter.notifyItemRemoved(0);
            }
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                this.parentPage.listView.hideSelector(false);
            }
            this.currentItemViewHolder = viewHolder;
            if (viewHolder != null && (viewHolder.itemView instanceof DialogCell)) {
                ((DialogCell) viewHolder.itemView).swipeCanceled = false;
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            if (animationType == 4) {
                return 200L;
            }
            if (animationType == 8 && DialogsActivity.this.movingView != null) {
                final View view = DialogsActivity.this.movingView;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$SwipeController$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        view.setBackgroundDrawable(null);
                    }
                }, this.parentPage.dialogsItemAnimator.getMoveDuration());
                DialogsActivity.this.movingView = null;
            }
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return 0.45f;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public float getSwipeEscapeVelocity(float defaultValue) {
            return 3500.0f;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public float getSwipeVelocityThreshold(float defaultValue) {
            return Float.MAX_VALUE;
        }
    }

    public DialogsActivity(Bundle args) {
        super(args);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.onlySelect = this.arguments.getBoolean("onlySelect", false);
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.initialDialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
            this.checkCanWrite = this.arguments.getBoolean("checkCanWrite", true);
            this.afterSignup = this.arguments.getBoolean("afterSignup", false);
            this.folderId = this.arguments.getInt("folderId", 0);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", true);
            this.messagesCount = this.arguments.getInt("messagesCount", 0);
            this.hasPoll = this.arguments.getInt("hasPoll", 0);
            this.hasInvoice = this.arguments.getBoolean("hasInvoice", false);
            this.showSetPasswordConfirm = this.arguments.getBoolean("showSetPasswordConfirm", this.showSetPasswordConfirm);
            this.otherwiseReloginDays = this.arguments.getInt("otherwiseRelogin");
            this.allowGroups = this.arguments.getBoolean("allowGroups", true);
            this.allowChannels = this.arguments.getBoolean("allowChannels", true);
            this.allowUsers = this.arguments.getBoolean("allowUsers", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.closeFragment = this.arguments.getBoolean("closeFragment", true);
        }
        if (this.initialDialogsType == 0) {
            this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
            SharedConfig.loadProxyList();
        }
        if (this.searchString == null) {
            this.currentConnectionState = getConnectionsManager().getConnectionState();
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
                getNotificationCenter().addObserver(this, NotificationCenter.filterSettingsUpdated);
                getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
                getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            }
            getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().addObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().addObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().addObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().addObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().addObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().addObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().addObserver(this, NotificationCenter.forceImportContactsStart);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseMigration);
        getNotificationCenter().addObserver(this, NotificationCenter.onDatabaseOpened);
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        loadDialogs(getAccountInstance());
        getMessagesController().loadPinnedDialogs(this.folderId, 0L, null);
        if (this.databaseMigrationHint != null && !getMessagesStorage().isDatabaseMigrationInProgress()) {
            View localView = this.databaseMigrationHint;
            if (localView.getParent() != null) {
                ((ViewGroup) localView.getParent()).removeView(localView);
            }
            this.databaseMigrationHint = null;
        }
        return true;
    }

    public static void loadDialogs(final AccountInstance accountInstance) {
        int currentAccount = accountInstance.getCurrentAccount();
        if (!dialogsLoaded[currentAccount]) {
            MessagesController messagesController = accountInstance.getMessagesController();
            messagesController.loadGlobalNotificationsSettings();
            messagesController.loadDialogs(0, 0, 100, true);
            messagesController.loadHintDialogs();
            messagesController.loadUserInfo(accountInstance.getUserConfig().getCurrentUser(), false, 0);
            accountInstance.getContactsController().checkInviteText();
            accountInstance.getMediaDataController().chekAllMedia(false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    AccountInstance.this.getDownloadController().loadDownloadingFiles();
                }
            }, 200L);
            Iterator<String> it = messagesController.diceEmojies.iterator();
            while (it.hasNext()) {
                String emoji = it.next();
                accountInstance.getMediaDataController().loadStickersByEmojiOrName(emoji, true, true);
            }
            dialogsLoaded[currentAccount] = true;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
                getNotificationCenter().removeObserver(this, NotificationCenter.filterSettingsUpdated);
                getNotificationCenter().removeObserver(this, NotificationCenter.dialogFiltersUpdated);
                getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            }
            getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.appDidLogout);
            getNotificationCenter().removeObserver(this, NotificationCenter.openedChatChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByAck);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
            getNotificationCenter().removeObserver(this, NotificationCenter.messageSendError);
            getNotificationCenter().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            getNotificationCenter().removeObserver(this, NotificationCenter.replyMessagesDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.reloadHints);
            getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateConnectionState);
            getNotificationCenter().removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
            getNotificationCenter().removeObserver(this, NotificationCenter.folderBecomeEmpty);
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoaded);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadFailed);
            getNotificationCenter().removeObserver(this, NotificationCenter.fileLoadProgressChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.dialogsUnreadReactionsCounterChanged);
            getNotificationCenter().removeObserver(this, NotificationCenter.forceImportContactsStart);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.appUpdateAvailable);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseMigration);
        getNotificationCenter().removeObserver(this, NotificationCenter.onDatabaseOpened);
        getNotificationCenter().removeObserver(this, NotificationCenter.didClearDatabase);
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        getNotificationCenter().onAnimationFinish(this.animationIndex);
        this.delegate = null;
        SuggestClearDatabaseBottomSheet.dismissDialog();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context) { // from class: org.telegram.ui.DialogsActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar, android.view.View
            public void setTranslationY(float translationY) {
                if (translationY != getTranslationY() && DialogsActivity.this.fragmentView != null) {
                    DialogsActivity.this.fragmentView.invalidate();
                }
                super.setTranslationY(translationY);
            }

            @Override // org.telegram.ui.ActionBar.ActionBar
            public boolean shouldClipChild(View child) {
                return super.shouldClipChild(child) || child == DialogsActivity.this.doneItem;
            }

            @Override // org.telegram.ui.ActionBar.ActionBar, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (DialogsActivity.this.inPreviewMode && DialogsActivity.this.avatarContainer != null && child != DialogsActivity.this.avatarContainer) {
                    return false;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode || (AndroidUtilities.isTablet() && this.folderId != 0)) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v227, types: [org.telegram.ui.ActionBar.ActionBar] */
    /* JADX WARN: Type inference failed for: r0v229, types: [org.telegram.ui.ActionBar.ActionBar] */
    /* JADX WARN: Type inference failed for: r0v69, types: [org.telegram.ui.ActionBar.ActionBar] */
    /* JADX WARN: Type inference failed for: r1v186, types: [org.telegram.ui.DialogsActivity$DialogsRecyclerView] */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        int type;
        boolean z;
        Drawable drawable;
        boolean z2;
        int i;
        int i2;
        boolean z3 = false;
        this.searching = false;
        this.searchWas = false;
        this.pacmanAnimation = null;
        this.selectedDialogs.clear();
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                Theme.createChatResources(context, false);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        int i3 = 8;
        boolean z4 = true;
        if (!this.onlySelect && this.searchString == null && this.folderId == 0) {
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, Theme.getColor(Theme.key_actionBarDefaultSelector), Theme.getColor(Theme.key_actionBarDefaultIcon), true);
            this.doneItem = actionBarMenuItem;
            actionBarMenuItem.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
            this.actionBar.addView(this.doneItem, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 10.0f, 0.0f));
            this.doneItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DialogsActivity.this.m3340lambda$createView$3$orgtelegramuiDialogsActivity(view);
                }
            });
            this.doneItem.setAlpha(0.0f);
            this.doneItem.setVisibility(8);
            ProxyDrawable proxyDrawable = new ProxyDrawable(context);
            this.proxyDrawable = proxyDrawable;
            ActionBarMenuItem addItem = menu.addItem(2, proxyDrawable);
            this.proxyItem = addItem;
            addItem.setContentDescription(LocaleController.getString("ProxySettings", R.string.ProxySettings));
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.passcode_lock_close, "passcode_lock_close", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
            this.passcodeDrawable = rLottieDrawable;
            ActionBarMenuItem addItem2 = menu.addItem(1, rLottieDrawable);
            this.passcodeItem = addItem2;
            addItem2.setContentDescription(LocaleController.getString("AccDescrPasscodeLock", R.string.AccDescrPasscodeLock));
            ActionBarMenuItem addItem3 = menu.addItem(3, new ColorDrawable(0));
            this.downloadsItem = addItem3;
            addItem3.addView(new DownloadProgressIcon(this.currentAccount, context));
            this.downloadsItem.setContentDescription(LocaleController.getString("DownloadsTabs", R.string.DownloadsTabs));
            this.downloadsItem.setVisibility(8);
            updatePasscodeButton();
            updateProxyButton(false, false);
        }
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.DialogsActivity.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                DialogsActivity.this.searching = true;
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(8);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                    DialogsActivity.this.proxyItem.setVisibility(8);
                }
                if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                    DialogsActivity.this.downloadsItem.setVisibility(8);
                }
                if (DialogsActivity.this.viewPages[0] != null) {
                    if (DialogsActivity.this.searchString != null) {
                        DialogsActivity.this.viewPages[0].listView.hide();
                        if (DialogsActivity.this.searchViewPager != null) {
                            DialogsActivity.this.searchViewPager.searchListView.show();
                        }
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(8);
                    }
                }
                DialogsActivity.this.setScrollY(0.0f);
                DialogsActivity.this.updatePasscodeButton();
                DialogsActivity.this.updateProxyButton(false, false);
                DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
                ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public boolean canCollapseSearch() {
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(0);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                    DialogsActivity.this.proxyItem.setVisibility(0);
                }
                if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                    DialogsActivity.this.downloadsItem.setVisibility(0);
                }
                if (DialogsActivity.this.searchString != null) {
                    DialogsActivity.this.finishFragment();
                    return false;
                }
                return true;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                DialogsActivity.this.searching = false;
                DialogsActivity.this.searchWas = false;
                if (DialogsActivity.this.viewPages[0] != null) {
                    DialogsActivity.this.viewPages[0].listView.setEmptyView(DialogsActivity.this.folderId == 0 ? DialogsActivity.this.viewPages[0].progressView : null);
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(0);
                        DialogsActivity.this.floatingHidden = true;
                        DialogsActivity.this.floatingButtonTranslation = AndroidUtilities.dp(100.0f);
                        DialogsActivity.this.floatingButtonHideProgress = 1.0f;
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                    DialogsActivity.this.showSearch(false, false, true);
                }
                DialogsActivity.this.updateProxyButton(false, false);
                DialogsActivity.this.updatePasscodeButton();
                if (DialogsActivity.this.menuDrawable != null) {
                    if (DialogsActivity.this.actionBar.getBackButton().getDrawable() != DialogsActivity.this.menuDrawable) {
                        DialogsActivity.this.actionBar.setBackButtonDrawable(DialogsActivity.this.menuDrawable);
                        DialogsActivity.this.menuDrawable.setRotation(0.0f, true);
                    }
                    DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0 || ((DialogsActivity.this.searchViewPager.dialogsSearchAdapter != null && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.hasRecentSearch()) || DialogsActivity.this.searchFiltersWasShowed)) {
                    DialogsActivity.this.searchWas = true;
                    if (!DialogsActivity.this.searchIsShowed) {
                        DialogsActivity.this.showSearch(true, false, true);
                    }
                }
                DialogsActivity.this.searchViewPager.onTextChanged(text);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchFilterCleared(FiltersView.MediaFilterData filterData) {
                if (DialogsActivity.this.searchIsShowed) {
                    DialogsActivity.this.searchViewPager.removeSearchFilter(filterData);
                    DialogsActivity.this.searchViewPager.onTextChanged(DialogsActivity.this.searchItem.getSearchField().getText().toString());
                    DialogsActivity.this.updateFiltersView(true, null, null, false, true);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public boolean canToggleSearch() {
                return !DialogsActivity.this.actionBar.isActionModeShowed() && DialogsActivity.this.databaseMigrationHint == null;
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        int i4 = this.initialDialogsType;
        if (i4 == 2 || i4 == 14) {
            actionBarMenuItemSearchListener.setVisibility(8);
        }
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            int i5 = this.initialDialogsType;
            if (i5 == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
            } else if (i5 == 10) {
                this.actionBar.setTitle(LocaleController.getString("SelectChats", R.string.SelectChats));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
            }
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        } else {
            if (this.searchString != null || this.folderId != 0) {
                ?? r0 = this.actionBar;
                BackDrawable backDrawable = new BackDrawable(false);
                this.backDrawable = backDrawable;
                r0.setBackButtonDrawable(backDrawable);
            } else {
                ?? r02 = this.actionBar;
                MenuDrawable menuDrawable = new MenuDrawable();
                this.menuDrawable = menuDrawable;
                r02.setBackButtonDrawable(menuDrawable);
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
            }
            if (this.folderId != 0) {
                this.actionBar.setTitle(LocaleController.getString("ArchivedChats", R.string.ArchivedChats));
            } else if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle(LocaleController.getString("AppNameBeta", R.string.AppNameBeta));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
            if (this.folderId == 0) {
                this.actionBar.setSupportsHolidayImage(true);
            }
        }
        if (!this.onlySelect) {
            this.actionBar.setAddToContainer(false);
            this.actionBar.setCastShadows(false);
            this.actionBar.setClipContent(true);
        }
        this.actionBar.setTitleActionRunnable(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3341lambda$createView$4$orgtelegramuiDialogsActivity();
            }
        });
        if (this.initialDialogsType == 0 && this.folderId == 0 && !this.onlySelect && TextUtils.isEmpty(this.searchString)) {
            this.scrimPaint = new Paint() { // from class: org.telegram.ui.DialogsActivity.4
                @Override // android.graphics.Paint
                public void setAlpha(int a) {
                    super.setAlpha(a);
                    if (DialogsActivity.this.fragmentView != null) {
                        DialogsActivity.this.fragmentView.invalidate();
                    }
                }
            };
            FilterTabsView filterTabsView = new FilterTabsView(context) { // from class: org.telegram.ui.DialogsActivity.5
                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    DialogsActivity.this.maybeStartTracking = false;
                    return super.onInterceptTouchEvent(ev);
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    if (getTranslationY() != translationY) {
                        super.setTranslationY(translationY);
                        DialogsActivity.this.updateContextViewPosition();
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.invalidate();
                        }
                    }
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.FilterTabsView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    if (DialogsActivity.this.scrimView != null) {
                        DialogsActivity.this.scrimView.getLocationInWindow(DialogsActivity.this.scrimViewLocation);
                        DialogsActivity.this.fragmentView.invalidate();
                    }
                }
            };
            this.filterTabsView = filterTabsView;
            filterTabsView.setVisibility(8);
            this.canShowFilterTabsView = false;
            this.filterTabsView.setDelegate(new AnonymousClass6(context));
        }
        int i6 = 4;
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            this.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(context);
            imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(imageView, LayoutHelper.createFrame(36, 36, 17));
            TLRPC.User user = getUserConfig().getCurrentUser();
            avatarDrawable.setInfo(user);
            imageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            Drawable thumb = (user == null || user.photo == null || user.photo.strippedBitmap == null) ? avatarDrawable : user.photo.strippedBitmap;
            imageView.setImage(ImageLocation.getForUserOrChat(user, 1), "50_50", ImageLocation.getForUserOrChat(user, 2), "50_50", thumb, user);
            int a = 0;
            while (a < i6) {
                TLRPC.User u = AccountInstance.getInstance(a).getUserConfig().getCurrentUser();
                if (u != null) {
                    AccountSelectCell cell = new AccountSelectCell(context, z3);
                    cell.setAccount(a, true);
                    this.switchItem.addSubItem(a + 10, cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
                a++;
                i6 = 4;
                z3 = false;
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        RecyclerView recyclerView = this.sideMenu;
        if (recyclerView != null) {
            recyclerView.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.getAdapter().notifyDataSetChanged();
        }
        createActionMode(null);
        final ContentView contentView = new ContentView(context);
        this.fragmentView = contentView;
        int pagesCount = (this.folderId == 0 && this.initialDialogsType == 0 && !this.onlySelect) ? 2 : 1;
        this.viewPages = new ViewPage[pagesCount];
        int a2 = 0;
        while (a2 < pagesCount) {
            final ViewPage viewPage = new ViewPage(context) { // from class: org.telegram.ui.DialogsActivity.7
                @Override // android.view.View
                public void setTranslationX(float translationX) {
                    if (getTranslationX() != translationX) {
                        super.setTranslationX(translationX);
                        if (DialogsActivity.this.tabsAnimationInProgress && DialogsActivity.this.viewPages[0] == this) {
                            float scrollProgress = Math.abs(DialogsActivity.this.viewPages[0].getTranslationX()) / DialogsActivity.this.viewPages[0].getMeasuredWidth();
                            DialogsActivity.this.filterTabsView.selectTabWithId(DialogsActivity.this.viewPages[1].selectedType, scrollProgress);
                        }
                        contentView.invalidateBlur();
                    }
                }
            };
            contentView.addView(viewPage, LayoutHelper.createFrame(-1, -1.0f));
            viewPage.dialogsType = this.initialDialogsType;
            this.viewPages[a2] = viewPage;
            viewPage.progressView = new FlickerLoadingView(context);
            viewPage.progressView.setViewType(7);
            viewPage.progressView.setVisibility(i3);
            viewPage.addView(viewPage.progressView, LayoutHelper.createFrame(-2, -2, 17));
            viewPage.listView = new DialogsRecyclerView(context, viewPage);
            viewPage.listView.setAccessibilityEnabled(false);
            viewPage.listView.setAnimateEmptyView(z4, 0);
            viewPage.listView.setClipToPadding(false);
            viewPage.listView.setPivotY(0.0f);
            viewPage.dialogsItemAnimator = new DialogsItemAnimator(viewPage.listView) { // from class: org.telegram.ui.DialogsActivity.8
                @Override // androidx.recyclerview.widget.SimpleItemAnimator
                public void onRemoveStarting(RecyclerView.ViewHolder item) {
                    super.onRemoveStarting(item);
                    if (viewPage.layoutManager.findFirstVisibleItemPosition() == 0) {
                        View v = viewPage.layoutManager.findViewByPosition(0);
                        if (v != null) {
                            v.invalidate();
                        }
                        if (viewPage.archivePullViewState == 2) {
                            viewPage.archivePullViewState = 1;
                        }
                        if (viewPage.pullForegroundDrawable != null) {
                            viewPage.pullForegroundDrawable.doNotShow();
                        }
                    }
                }

                @Override // androidx.recyclerview.widget.SimpleItemAnimator
                public void onRemoveFinished(RecyclerView.ViewHolder item) {
                    if (DialogsActivity.this.dialogRemoveFinished == 2) {
                        DialogsActivity.this.dialogRemoveFinished = 1;
                    }
                }

                @Override // androidx.recyclerview.widget.SimpleItemAnimator
                public void onAddFinished(RecyclerView.ViewHolder item) {
                    if (DialogsActivity.this.dialogInsertFinished == 2) {
                        DialogsActivity.this.dialogInsertFinished = 1;
                    }
                }

                @Override // androidx.recyclerview.widget.SimpleItemAnimator
                public void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
                    if (DialogsActivity.this.dialogChangeFinished == 2) {
                        DialogsActivity.this.dialogChangeFinished = 1;
                    }
                }

                @Override // org.telegram.ui.Components.DialogsItemAnimator
                protected void onAllAnimationsDone() {
                    if (DialogsActivity.this.dialogRemoveFinished == 1 || DialogsActivity.this.dialogInsertFinished == 1 || DialogsActivity.this.dialogChangeFinished == 1) {
                        DialogsActivity.this.onDialogAnimationFinished();
                    }
                }
            };
            viewPage.listView.setItemAnimator(viewPage.dialogsItemAnimator);
            viewPage.listView.setVerticalScrollBarEnabled(z4);
            viewPage.listView.setInstantClick(z4);
            viewPage.layoutManager = new AnonymousClass9(context, viewPage);
            LinearLayoutManager linearLayoutManager = viewPage.layoutManager;
            int i7 = z4 ? 1 : 0;
            int i8 = z4 ? 1 : 0;
            linearLayoutManager.setOrientation(i7);
            viewPage.listView.setLayoutManager(viewPage.layoutManager);
            viewPage.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
            viewPage.addView(viewPage.listView, LayoutHelper.createFrame(-1, -1.0f));
            viewPage.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda52
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i9) {
                    DialogsActivity.this.m3342lambda$createView$5$orgtelegramuiDialogsActivity(viewPage, view, i9);
                }
            });
            viewPage.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.DialogsActivity.11
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
                public boolean onItemClick(View view, int position, float x, float y) {
                    if (DialogsActivity.this.filterTabsView == null || DialogsActivity.this.filterTabsView.getVisibility() != 0 || !DialogsActivity.this.filterTabsView.isEditing()) {
                        return DialogsActivity.this.onItemLongClick(viewPage.listView, view, position, x, y, viewPage.dialogsType, viewPage.dialogsAdapter);
                    }
                    return false;
                }

                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
                public void onMove(float dx, float dy) {
                    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        DialogsActivity.this.movePreviewFragment(dy);
                    }
                }

                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
                public void onLongClickRelease() {
                    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        DialogsActivity.this.finishPreviewFragment();
                    }
                }
            });
            viewPage.swipeController = new SwipeController(viewPage);
            viewPage.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(viewPage.listView, false);
            viewPage.itemTouchhelper = new ItemTouchHelper(viewPage.swipeController);
            viewPage.itemTouchhelper.attachToRecyclerView(viewPage.listView);
            viewPage.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.DialogsActivity.12
                private boolean wasManualScroll;

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView2, int newState) {
                    if (newState != 1) {
                        DialogsActivity.this.scrollingManually = false;
                    } else {
                        this.wasManualScroll = true;
                        DialogsActivity.this.scrollingManually = true;
                    }
                    if (newState == 0) {
                        this.wasManualScroll = false;
                        DialogsActivity.this.disableActionBarScrolling = false;
                        if (DialogsActivity.this.waitingForScrollFinished) {
                            DialogsActivity.this.waitingForScrollFinished = false;
                            if (DialogsActivity.this.updatePullAfterScroll) {
                                viewPage.listView.updatePullState();
                                DialogsActivity.this.updatePullAfterScroll = false;
                            }
                            viewPage.dialogsAdapter.notifyDataSetChanged();
                        }
                        if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && DialogsActivity.this.viewPages[0].listView == recyclerView2) {
                            int scrollY = (int) (-DialogsActivity.this.actionBar.getTranslationY());
                            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
                            if (scrollY != 0 && scrollY != actionBarHeight) {
                                if (scrollY >= actionBarHeight / 2) {
                                    if (DialogsActivity.this.viewPages[0].listView.canScrollVertically(1)) {
                                        recyclerView2.smoothScrollBy(0, actionBarHeight - scrollY);
                                        return;
                                    }
                                    return;
                                }
                                recyclerView2.smoothScrollBy(0, -scrollY);
                            }
                        }
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView2, int dx, int dy) {
                    View child;
                    int firstVisibleItem;
                    boolean goingDown;
                    viewPage.dialogsItemAnimator.onListScroll(-dy);
                    DialogsActivity.this.checkListLoad(viewPage);
                    if (DialogsActivity.this.initialDialogsType != 10 && this.wasManualScroll && DialogsActivity.this.floatingButtonContainer.getVisibility() != 8 && recyclerView2.getChildCount() > 0 && (firstVisibleItem = viewPage.layoutManager.findFirstVisibleItemPosition()) != -1) {
                        RecyclerView.ViewHolder holder = recyclerView2.findViewHolderForAdapterPosition(firstVisibleItem);
                        if (!DialogsActivity.this.hasHiddenArchive() || (holder != null && holder.getAdapterPosition() != 0)) {
                            int firstViewTop = 0;
                            if (holder != null) {
                                firstViewTop = holder.itemView.getTop();
                            }
                            boolean changed = true;
                            if (DialogsActivity.this.prevPosition == firstVisibleItem) {
                                int topDelta = DialogsActivity.this.prevTop - firstViewTop;
                                goingDown = firstViewTop < DialogsActivity.this.prevTop;
                                changed = Math.abs(topDelta) > 1;
                            } else {
                                goingDown = firstVisibleItem > DialogsActivity.this.prevPosition;
                            }
                            if (changed && DialogsActivity.this.scrollUpdated && (goingDown || DialogsActivity.this.scrollingManually)) {
                                DialogsActivity.this.hideFloatingButton(goingDown);
                            }
                            DialogsActivity.this.prevPosition = firstVisibleItem;
                            DialogsActivity.this.prevTop = firstViewTop;
                            DialogsActivity.this.scrollUpdated = true;
                        }
                    }
                    if (DialogsActivity.this.filterTabsView != null && DialogsActivity.this.filterTabsView.getVisibility() == 0 && recyclerView2 == DialogsActivity.this.viewPages[0].listView && !DialogsActivity.this.searching && !DialogsActivity.this.actionBar.isActionModeShowed() && !DialogsActivity.this.disableActionBarScrolling && DialogsActivity.this.filterTabsViewIsVisible) {
                        if (dy > 0 && DialogsActivity.this.hasHiddenArchive() && DialogsActivity.this.viewPages[0].dialogsType == 0 && (child = recyclerView2.getChildAt(0)) != null && recyclerView2.getChildViewHolder(child).getAdapterPosition() == 0) {
                            int visiblePartAfterScroll = child.getMeasuredHeight() + (child.getTop() - recyclerView2.getPaddingTop());
                            if (visiblePartAfterScroll + dy > 0) {
                                if (visiblePartAfterScroll < 0) {
                                    dy = -visiblePartAfterScroll;
                                } else {
                                    return;
                                }
                            }
                        }
                        float currentTranslation = DialogsActivity.this.actionBar.getTranslationY();
                        float newTranslation = currentTranslation - dy;
                        if (newTranslation < (-ActionBar.getCurrentActionBarHeight())) {
                            newTranslation = -ActionBar.getCurrentActionBarHeight();
                        } else if (newTranslation > 0.0f) {
                            newTranslation = 0.0f;
                        }
                        if (newTranslation != currentTranslation) {
                            DialogsActivity.this.setScrollY(newTranslation);
                        }
                    }
                    if (DialogsActivity.this.fragmentView != null) {
                        ((SizeNotifierFrameLayout) DialogsActivity.this.fragmentView).invalidateBlur();
                    }
                }
            });
            viewPage.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
            if (viewPage.pullForegroundDrawable == null && this.folderId == 0) {
                viewPage.pullForegroundDrawable = new PullForegroundDrawable(LocaleController.getString("AccSwipeForArchive", R.string.AccSwipeForArchive), LocaleController.getString("AccReleaseForArchive", R.string.AccReleaseForArchive)) { // from class: org.telegram.ui.DialogsActivity.13
                    @Override // org.telegram.ui.Components.PullForegroundDrawable
                    protected float getViewOffset() {
                        return viewPage.listView.getViewOffset();
                    }
                };
                if (hasHiddenArchive()) {
                    viewPage.pullForegroundDrawable.showHidden();
                } else {
                    viewPage.pullForegroundDrawable.doNotShow();
                }
                viewPage.pullForegroundDrawable.setWillDraw(viewPage.archivePullViewState != 0);
            }
            ActionBarMenu menu2 = menu;
            int pagesCount2 = pagesCount;
            int a3 = a2;
            viewPage.dialogsAdapter = new DialogsAdapter(this, context, viewPage.dialogsType, this.folderId, this.onlySelect, this.selectedDialogs, this.currentAccount) { // from class: org.telegram.ui.DialogsActivity.14
                @Override // org.telegram.ui.Adapters.DialogsAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
                public void notifyDataSetChanged() {
                    viewPage.lastItemsCount = getItemCount();
                    try {
                        super.notifyDataSetChanged();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            };
            viewPage.dialogsAdapter.setForceShowEmptyCell(this.afterSignup);
            if (AndroidUtilities.isTablet() && this.openedDialogId != 0) {
                viewPage.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
            }
            viewPage.dialogsAdapter.setArchivedPullDrawable(viewPage.pullForegroundDrawable);
            viewPage.listView.setAdapter(viewPage.dialogsAdapter);
            viewPage.listView.setEmptyView(this.folderId == 0 ? viewPage.progressView : null);
            viewPage.scrollHelper = new RecyclerAnimationScrollHelper(viewPage.listView, viewPage.layoutManager);
            if (a3 != 0) {
                this.viewPages[a3].setVisibility(8);
            }
            a2 = a3 + 1;
            menu = menu2;
            pagesCount = pagesCount2;
            z4 = true;
            i3 = 8;
        }
        if (this.searchString != null) {
            type = 2;
        } else if (this.onlySelect) {
            type = 0;
        } else {
            type = 1;
        }
        SearchViewPager searchViewPager = new SearchViewPager(context, this, type, this.initialDialogsType, this.folderId, new SearchViewPager.ChatPreviewDelegate() { // from class: org.telegram.ui.DialogsActivity.15
            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void startChatPreview(RecyclerListView listView, DialogCell cell2) {
                DialogsActivity.this.showChatPreview(cell2);
            }

            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void move(float dy) {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.movePreviewFragment(dy);
                }
            }

            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void finish() {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }
        });
        this.searchViewPager = searchViewPager;
        contentView.addView(searchViewPager);
        this.searchViewPager.dialogsSearchAdapter.setDelegate(new AnonymousClass16());
        this.searchViewPager.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda50
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i9) {
                DialogsActivity.this.m3343lambda$createView$6$orgtelegramuiDialogsActivity(view, i9);
            }
        });
        this.searchViewPager.searchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.DialogsActivity.17
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public boolean onItemClick(View view, int position, float x, float y) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                return dialogsActivity.onItemLongClick(dialogsActivity.searchViewPager.searchListView, view, position, x, y, -1, DialogsActivity.this.searchViewPager.dialogsSearchAdapter);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public void onMove(float dx, float dy) {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.movePreviewFragment(dy);
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public void onLongClickRelease() {
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }
        });
        this.searchViewPager.setFilteredSearchViewDelegate(new FilteredSearchView.Delegate() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda53
            @Override // org.telegram.ui.FilteredSearchView.Delegate
            public final void updateFiltersView(boolean z5, ArrayList arrayList, ArrayList arrayList2, boolean z6) {
                DialogsActivity.this.m3344lambda$createView$7$orgtelegramuiDialogsActivity(z5, arrayList, arrayList2, z6);
            }
        });
        this.searchViewPager.setVisibility(8);
        FiltersView filtersView = new FiltersView(getParentActivity(), null);
        this.filtersView = filtersView;
        filtersView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda51
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i9) {
                DialogsActivity.this.m3345lambda$createView$8$orgtelegramuiDialogsActivity(view, i9);
            }
        });
        contentView.addView(this.filtersView, LayoutHelper.createFrame(-1, -2, 48));
        this.filtersView.setVisibility(8);
        FrameLayout frameLayout = new FrameLayout(context);
        this.floatingButtonContainer = frameLayout;
        frameLayout.setVisibility(((!this.onlySelect || this.initialDialogsType == 10) && this.folderId == 0) ? 0 : 8);
        contentView.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda13
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DialogsActivity.this.m3346lambda$createView$9$orgtelegramuiDialogsActivity(view);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.floatingButton = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        if (this.initialDialogsType == 10) {
            this.floatingButton.setImageResource(R.drawable.floating_check);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("Done", R.string.Done));
        } else {
            this.floatingButton.setAnimation(R.raw.write_contacts_fab_icon, 52, 52);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("NewMessageTitle", R.string.NewMessageTitle));
        }
        if (Build.VERSION.SDK_INT < 21) {
            z = true;
        } else {
            StateListAnimator animator = new StateListAnimator();
            z = true;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonContainer, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.DialogsActivity.18
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        Drawable drawable2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT >= 21) {
            drawable = drawable2;
        } else {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable2, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        updateFloatingButtonColor();
        this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.floatingProgressView = radialProgressView;
        radialProgressView.setProgressColor(Theme.getColor(Theme.key_chats_actionIcon));
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setVisibility(8);
        this.floatingProgressView.setSize(AndroidUtilities.dp(22.0f));
        this.floatingButtonContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchTabsView = null;
        if (!this.onlySelect && this.initialDialogsType == 0) {
            FragmentContextView fragmentContextView = new FragmentContextView(context, this, z);
            this.fragmentLocationContextView = fragmentContextView;
            fragmentContextView.setLayoutParams(LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            contentView.addView(this.fragmentLocationContextView);
            FragmentContextView fragmentContextView2 = new FragmentContextView(context, this, false) { // from class: org.telegram.ui.DialogsActivity.19
                @Override // org.telegram.ui.Components.FragmentContextView
                protected void playbackSpeedChanged(float value) {
                    if (Math.abs(value - 1.0f) > 0.001f || Math.abs(value - 1.8f) > 0.001f) {
                        DialogsActivity.this.getUndoView().showWithAction(0L, Math.abs(value - 1.0f) > 0.001f ? 50 : 51, Float.valueOf(value), null, null);
                    }
                }
            };
            this.fragmentContextView = fragmentContextView2;
            fragmentContextView2.setLayoutParams(LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            contentView.addView(this.fragmentContextView);
            this.fragmentContextView.setAdditionalContextView(this.fragmentLocationContextView);
            this.fragmentLocationContextView.setAdditionalContextView(this.fragmentContextView);
            i = 21;
            z2 = true;
        } else if (this.initialDialogsType != 3) {
            i = 21;
            z2 = true;
        } else {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.onDestroy();
            }
            z2 = true;
            i = 21;
            this.commentView = new ChatActivityEnterView(getParentActivity(), contentView, null, false) { // from class: org.telegram.ui.DialogsActivity.20
                @Override // android.view.ViewGroup, android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    if (ev.getAction() == 0) {
                        AndroidUtilities.requestAdjustResize(DialogsActivity.this.getParentActivity(), DialogsActivity.this.classGuid);
                    }
                    return super.dispatchTouchEvent(ev);
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                }
            };
            contentView.setClipChildren(false);
            contentView.setClipToPadding(false);
            this.commentView.allowBlur = false;
            this.commentView.forceSmoothKeyboard(true);
            this.commentView.setAllowStickersAndGifs(false, false);
            this.commentView.setForceShowSendButton(true, false);
            this.commentView.setPadding(0, 0, AndroidUtilities.dp(20.0f), 0);
            this.commentView.setVisibility(8);
            this.commentView.getSendButton().setAlpha(0.0f);
            View view = new View(getParentActivity());
            this.commentViewBg = view;
            view.setBackgroundColor(getThemedColor(Theme.key_chat_messagePanelBackground));
            contentView.addView(this.commentViewBg, LayoutHelper.createFrame(-1, 1600.0f, 87, 0.0f, 0.0f, 0.0f, -1600.0f));
            contentView.addView(this.commentView, LayoutHelper.createFrame(-1, -2, 83));
            this.commentView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() { // from class: org.telegram.ui.DialogsActivity.21
                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ int getContentViewHeight() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getContentViewHeight(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getSendAsPeers(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ boolean hasForwardingMessages() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasForwardingMessages(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ boolean hasScheduledMessages() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ int measureKeyboardHeight() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$measureKeyboardHeight(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ void onTrendingStickersShowed(boolean z5) {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onTrendingStickersShowed(this, z5);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ void openScheduledMessages() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ void prepareMessageSending() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$prepareMessageSending(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public /* synthetic */ void scrollToSendingMessage() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onMessageSend(CharSequence message, boolean notify, int scheduleDate) {
                    if (DialogsActivity.this.delegate != null && !DialogsActivity.this.selectedDialogs.isEmpty()) {
                        DialogsActivityDelegate dialogsActivityDelegate = DialogsActivity.this.delegate;
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        dialogsActivityDelegate.didSelectDialogs(dialogsActivity, dialogsActivity.selectedDialogs, message, false);
                    }
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onSwitchRecordMode(boolean video) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onTextSelectionChanged(int start, int end) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void bottomPanelTranslationYChanged(float translation) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onStickersExpandedChange() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onPreAudioVideoRecord() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onTextChanged(CharSequence text, boolean bigChange) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onTextSpansChanged(CharSequence text) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void needSendTyping() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onAttachButtonHidden() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onAttachButtonShow() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onMessageEditEnd(boolean loading) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onWindowSizeChanged(int size) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onStickersTab(boolean opened) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void didPressAttachButton() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void needStartRecordVideo(int state, boolean notify, int scheduleDate) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void needChangeVideoPreviewState(int state, float seekProgress) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void needStartRecordAudio(int state) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void needShowMediaBanHint() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onSendLongClick() {
                }

                @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
                public void onAudioVideoInterfaceUpdated() {
                }
            });
            FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.DialogsActivity.22
                @Override // android.view.View
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setText(LocaleController.formatPluralString("AccDescrShareInChats", DialogsActivity.this.selectedDialogs.size(), new Object[0]));
                    info.setClassName(Button.class.getName());
                    info.setLongClickable(true);
                    info.setClickable(true);
                }
            };
            this.writeButtonContainer = frameLayout2;
            frameLayout2.setFocusable(true);
            this.writeButtonContainer.setFocusableInTouchMode(true);
            this.writeButtonContainer.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            contentView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            View view2 = new View(context) { // from class: org.telegram.ui.DialogsActivity.23
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    String text = String.format("%d", Integer.valueOf(Math.max(1, DialogsActivity.this.selectedDialogs.size())));
                    int textSize = (int) Math.ceil(DialogsActivity.this.textPaint.measureText(text));
                    int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                    int cx = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    DialogsActivity.this.textPaint.setColor(DialogsActivity.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck));
                    DialogsActivity.this.paint.setColor(DialogsActivity.this.getThemedColor(Theme.isCurrentThemeDark() ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground));
                    DialogsActivity.this.rect.set(cx - (size / 2), 0.0f, (size / 2) + cx, getMeasuredHeight());
                    canvas.drawRoundRect(DialogsActivity.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), DialogsActivity.this.paint);
                    DialogsActivity.this.paint.setColor(DialogsActivity.this.getThemedColor(Theme.key_dialogRoundCheckBox));
                    DialogsActivity.this.rect.set((cx - (size / 2)) + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), ((size / 2) + cx) - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                    canvas.drawRoundRect(DialogsActivity.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), DialogsActivity.this.paint);
                    canvas.drawText(text, cx - (textSize / 2), AndroidUtilities.dp(16.2f), DialogsActivity.this.textPaint);
                }
            };
            this.selectedCountView = view2;
            view2.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            contentView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
            final FrameLayout writeButtonBackground = new FrameLayout(context);
            Drawable writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_dialogFloatingButton), getThemedColor(Build.VERSION.SDK_INT >= 21 ? Theme.key_dialogFloatingButtonPressed : Theme.key_dialogFloatingButton));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable2 = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                shadowDrawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(shadowDrawable2, drawable, 0, 0);
                combinedDrawable2.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                writeButtonDrawable = combinedDrawable2;
            }
            writeButtonBackground.setBackgroundDrawable(writeButtonDrawable);
            writeButtonBackground.setImportantForAccessibility(2);
            if (Build.VERSION.SDK_INT >= 21) {
                writeButtonBackground.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.DialogsActivity.24
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view3, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            writeButtonBackground.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    DialogsActivity.this.m3336lambda$createView$10$orgtelegramuiDialogsActivity(view3);
                }
            });
            writeButtonBackground.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda19
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view3) {
                    return DialogsActivity.this.m3337lambda$createView$11$orgtelegramuiDialogsActivity(writeButtonBackground, view3);
                }
            });
            this.writeButton = new ImageView[2];
            int a4 = 0;
            for (int i9 = 2; a4 < i9; i9 = 2) {
                this.writeButton[a4] = new ImageView(context);
                this.writeButton[a4].setImageResource(a4 == 1 ? R.drawable.msg_arrow_forward : R.drawable.attach_send);
                this.writeButton[a4].setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
                this.writeButton[a4].setScaleType(ImageView.ScaleType.CENTER);
                writeButtonBackground.addView(this.writeButton[a4], LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, 17));
                a4++;
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[0], true, 0.5f, false);
            AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[1], false, 0.5f, false);
            this.writeButtonContainer.addView(writeButtonBackground, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        }
        View view3 = this.filterTabsView;
        if (view3 == null) {
            i2 = -1;
        } else {
            i2 = -1;
            contentView.addView(view3, LayoutHelper.createFrame(-1, 44.0f));
        }
        if (!this.onlySelect) {
            FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(i2, -2.0f);
            if (this.inPreviewMode && Build.VERSION.SDK_INT >= i) {
                layoutParams.topMargin = AndroidUtilities.statusBarHeight;
            }
            contentView.addView(this.actionBar, layoutParams);
        }
        if (this.searchString == null && this.initialDialogsType == 0) {
            FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.DialogsActivity.25
                private int lastGradientWidth;
                private LinearGradient updateGradient;
                private Paint paint = new Paint();
                private Matrix matrix = new Matrix();

                @Override // android.view.View
                public void draw(Canvas canvas) {
                    if (this.updateGradient != null) {
                        this.paint.setColor(-1);
                        this.paint.setShader(this.updateGradient);
                        this.updateGradient.setLocalMatrix(this.matrix);
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
                        DialogsActivity.this.updateLayoutIcon.setBackgroundGradientDrawable(this.updateGradient);
                        DialogsActivity.this.updateLayoutIcon.draw(canvas);
                    }
                    super.draw(canvas);
                }

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int width = View.MeasureSpec.getSize(widthMeasureSpec);
                    if (this.lastGradientWidth != width) {
                        this.updateGradient = new LinearGradient(0.0f, 0.0f, width, 0.0f, new int[]{-9846926, -11291731}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                        this.lastGradientWidth = width;
                    }
                    int x = (getMeasuredWidth() - DialogsActivity.this.updateTextView.getMeasuredWidth()) / 2;
                    DialogsActivity.this.updateLayoutIcon.setProgressRect(x, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(22.0f) + x, AndroidUtilities.dp(35.0f));
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    DialogsActivity.this.additionalFloatingTranslation2 = AndroidUtilities.dp(48.0f) - translationY;
                    if (DialogsActivity.this.additionalFloatingTranslation2 < 0.0f) {
                        DialogsActivity.this.additionalFloatingTranslation2 = 0.0f;
                    }
                    if (!DialogsActivity.this.floatingHidden) {
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                }
            };
            this.updateLayout = frameLayout3;
            frameLayout3.setWillNotDraw(false);
            this.updateLayout.setVisibility(4);
            this.updateLayout.setTranslationY(AndroidUtilities.dp(48.0f));
            if (Build.VERSION.SDK_INT >= i) {
                this.updateLayout.setBackground(Theme.getSelectorDrawable((int) Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false));
            }
            contentView.addView(this.updateLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.updateLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    DialogsActivity.this.m3338lambda$createView$12$orgtelegramuiDialogsActivity(view4);
                }
            });
            RadialProgress2 radialProgress2 = new RadialProgress2(this.updateLayout);
            this.updateLayoutIcon = radialProgress2;
            radialProgress2.setColors(-1, -1, -1, -1);
            this.updateLayoutIcon.setCircleRadius(AndroidUtilities.dp(11.0f));
            this.updateLayoutIcon.setAsMini();
            this.updateLayoutIcon.setIcon(15, z2, false);
            TextView textView = new TextView(context);
            this.updateTextView = textView;
            int i10 = z2 ? 1 : 0;
            int i11 = z2 ? 1 : 0;
            int i12 = z2 ? 1 : 0;
            textView.setTextSize(i10, 15.0f);
            this.updateTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.updateTextView.setText(LocaleController.getString("AppUpdateNow", R.string.AppUpdateNow).toUpperCase());
            this.updateTextView.setTextColor(-1);
            this.updateTextView.setPadding(AndroidUtilities.dp(30.0f), 0, 0, 0);
            this.updateLayout.addView(this.updateTextView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        for (int a5 = 0; a5 < 2; a5++) {
            this.undoView[a5] = new AnonymousClass26(context);
            contentView.addView(this.undoView[a5], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        int a6 = this.folderId;
        if (a6 != 0) {
            this.viewPages[0].listView.setGlowColor(Theme.getColor(Theme.key_actionBarDefaultArchived));
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_actionBarDefaultArchivedTitle));
            this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultArchivedIcon), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSelector), false);
            this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSearch), false);
            this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_actionBarDefaultArchivedSearchPlaceholder), z2);
        }
        if (!this.onlySelect && this.initialDialogsType == 0) {
            this.blurredView = new View(context) { // from class: org.telegram.ui.DialogsActivity.27
                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (DialogsActivity.this.fragmentView != null) {
                        DialogsActivity.this.fragmentView.invalidate();
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= 23) {
                this.blurredView.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundWhite), 100)));
            }
            this.blurredView.setFocusable(false);
            this.blurredView.setImportantForAccessibility(2);
            this.blurredView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    DialogsActivity.this.m3339lambda$createView$13$orgtelegramuiDialogsActivity(view4);
                }
            });
            this.blurredView.setVisibility(8);
            this.blurredView.setFitsSystemWindows(z2);
            contentView.addView(this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.actionBarDefaultPaint.setColor(Theme.getColor(this.folderId == 0 ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived));
        if (this.inPreviewMode) {
            TLRPC.User currentUser = getUserConfig().getCurrentUser();
            ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(this.actionBar.getContext(), null, false);
            this.avatarContainer = chatAvatarContainer;
            chatAvatarContainer.setTitle(UserObject.getUserName(currentUser));
            this.avatarContainer.setSubtitle(LocaleController.formatUserStatus(this.currentAccount, currentUser));
            this.avatarContainer.setUserAvatar(currentUser, z2);
            this.avatarContainer.setOccupyStatusBar(false);
            this.avatarContainer.setLeftPadding(AndroidUtilities.dp(10.0f));
            this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
            this.floatingButton.setVisibility(4);
            this.actionBar.setOccupyStatusBar(false);
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
            View view4 = this.fragmentContextView;
            if (view4 != null) {
                contentView.removeView(view4);
            }
            View view5 = this.fragmentLocationContextView;
            if (view5 != null) {
                contentView.removeView(view5);
            }
        }
        this.searchIsShowed = false;
        updateFilterTabs(false, false);
        if (this.searchString != null) {
            showSearch(z2, false, false);
            this.actionBar.openSearchField(this.searchString, false);
        } else if (this.initialSearchString == null) {
            showSearch(false, false, false);
        } else {
            showSearch(z2, false, false);
            this.actionBar.openSearchField(this.initialSearchString, false);
            this.initialSearchString = null;
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null) {
                filterTabsView2.setTranslationY(-AndroidUtilities.dp(44.0f));
            }
        }
        if (Build.VERSION.SDK_INT >= 30) {
            FilesMigrationService.checkBottomSheet(this);
        }
        updateMenuButton(false);
        this.actionBar.setDrawBlurBackground(contentView);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3340lambda$createView$3$orgtelegramuiDialogsActivity(View v) {
        this.filterTabsView.setIsEditing(false);
        showDoneItem(false);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3341lambda$createView$4$orgtelegramuiDialogsActivity() {
        if (this.initialDialogsType != 10) {
            hideFloatingButton(false);
        }
        scrollToTop();
    }

    /* renamed from: org.telegram.ui.DialogsActivity$6 */
    /* loaded from: classes4.dex */
    public class AnonymousClass6 implements FilterTabsView.FilterTabsViewDelegate {
        final /* synthetic */ Context val$context;

        AnonymousClass6(Context context) {
            DialogsActivity.this = this$0;
            this.val$context = context;
        }

        private void showDeleteAlert(final MessagesController.DialogFilter dialogFilter) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("FilterDelete", R.string.FilterDelete));
            builder.setMessage(LocaleController.getString("FilterDeleteAlert", R.string.FilterDeleteAlert));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$6$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.AnonymousClass6.this.m3396lambda$showDeleteAlert$2$orgtelegramuiDialogsActivity$6(dialogFilter, dialogInterface, i);
                }
            });
            AlertDialog alertDialog = builder.create();
            DialogsActivity.this.showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }

        /* renamed from: lambda$showDeleteAlert$2$org-telegram-ui-DialogsActivity$6 */
        public /* synthetic */ void m3396lambda$showDeleteAlert$2$orgtelegramuiDialogsActivity$6(MessagesController.DialogFilter dialogFilter, DialogInterface dialog2, int which2) {
            TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
            req.id = dialogFilter.id;
            DialogsActivity.this.getConnectionsManager().sendRequest(req, DialogsActivity$6$$ExternalSyntheticLambda3.INSTANCE);
            DialogsActivity.this.getMessagesController().removeFilter(dialogFilter);
            DialogsActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
        }

        public static /* synthetic */ void lambda$showDeleteAlert$0() {
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public void onSamePageSelected() {
            DialogsActivity.this.scrollToTop();
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public void onPageReorder(int fromId, int toId) {
            for (int a = 0; a < DialogsActivity.this.viewPages.length; a++) {
                if (DialogsActivity.this.viewPages[a].selectedType == fromId) {
                    DialogsActivity.this.viewPages[a].selectedType = toId;
                } else if (DialogsActivity.this.viewPages[a].selectedType == toId) {
                    DialogsActivity.this.viewPages[a].selectedType = fromId;
                }
            }
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public void onPageSelected(FilterTabsView.Tab tab, boolean forward) {
            if (DialogsActivity.this.viewPages[0].selectedType == tab.id) {
                return;
            }
            if (tab.isLocked) {
                DialogsActivity.this.filterTabsView.shakeLock(tab.id);
                DialogsActivity dialogsActivity = DialogsActivity.this;
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                dialogsActivity.showDialog(new LimitReachedBottomSheet(dialogsActivity2, this.val$context, 3, dialogsActivity2.currentAccount));
                return;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = DialogsActivity.this.getMessagesController().dialogFilters;
            if (tab.isDefault || (tab.id >= 0 && tab.id < dialogFilters.size())) {
                if (DialogsActivity.this.parentLayout != null) {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(tab.id == DialogsActivity.this.filterTabsView.getFirstTabId() || SharedConfig.getChatSwipeAction(DialogsActivity.this.currentAccount) != 5);
                }
                DialogsActivity.this.viewPages[1].selectedType = tab.id;
                DialogsActivity.this.viewPages[1].setVisibility(0);
                DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth());
                DialogsActivity.this.showScrollbars(false);
                DialogsActivity.this.switchToCurrentSelectedMode(true);
                DialogsActivity.this.animatingForward = forward;
            }
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public boolean canPerformActions() {
            return !DialogsActivity.this.searching;
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public void onPageScrolled(float progress) {
            if (progress != 1.0f || DialogsActivity.this.viewPages[1].getVisibility() == 0 || DialogsActivity.this.searching) {
                if (DialogsActivity.this.animatingForward) {
                    DialogsActivity.this.viewPages[0].setTranslationX((-progress) * DialogsActivity.this.viewPages[0].getMeasuredWidth());
                    DialogsActivity.this.viewPages[1].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth() - (DialogsActivity.this.viewPages[0].getMeasuredWidth() * progress));
                } else {
                    DialogsActivity.this.viewPages[0].setTranslationX(DialogsActivity.this.viewPages[0].getMeasuredWidth() * progress);
                    DialogsActivity.this.viewPages[1].setTranslationX((DialogsActivity.this.viewPages[0].getMeasuredWidth() * progress) - DialogsActivity.this.viewPages[0].getMeasuredWidth());
                }
                if (progress == 1.0f) {
                    ViewPage tempPage = DialogsActivity.this.viewPages[0];
                    DialogsActivity.this.viewPages[0] = DialogsActivity.this.viewPages[1];
                    DialogsActivity.this.viewPages[1] = tempPage;
                    DialogsActivity.this.viewPages[1].setVisibility(8);
                    DialogsActivity.this.showScrollbars(true);
                    DialogsActivity.this.updateCounters(false);
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    dialogsActivity.checkListLoad(dialogsActivity.viewPages[0]);
                    DialogsActivity.this.viewPages[0].dialogsAdapter.resume();
                    DialogsActivity.this.viewPages[1].dialogsAdapter.pause();
                }
            }
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public int getTabCounter(int tabId) {
            if (tabId == DialogsActivity.this.filterTabsView.getDefaultTabId()) {
                return DialogsActivity.this.getMessagesStorage().getMainUnreadCount();
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = DialogsActivity.this.getMessagesController().dialogFilters;
            if (tabId < 0 || tabId >= dialogFilters.size()) {
                return 0;
            }
            return DialogsActivity.this.getMessagesController().dialogFilters.get(tabId).unreadCount;
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public boolean didSelectTab(FilterTabsView.TabView tabView, boolean selected) {
            final MessagesController.DialogFilter dialogFilter;
            ScrollView scrollView;
            if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                return false;
            }
            if (DialogsActivity.this.scrimPopupWindow != null) {
                DialogsActivity.this.scrimPopupWindow.dismiss();
                DialogsActivity.this.scrimPopupWindow = null;
                DialogsActivity.this.scrimPopupWindowItems = null;
                return false;
            }
            final Rect rect = new Rect();
            if (tabView.getId() == DialogsActivity.this.filterTabsView.getDefaultTabId()) {
                dialogFilter = null;
            } else {
                dialogFilter = DialogsActivity.this.getMessagesController().dialogFilters.get(tabView.getId());
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(DialogsActivity.this.getParentActivity());
            popupLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.DialogsActivity.6.1
                private int[] pos = new int[2];

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == 0) {
                        if (DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing()) {
                            View contentView = DialogsActivity.this.scrimPopupWindow.getContentView();
                            contentView.getLocationInWindow(this.pos);
                            Rect rect2 = rect;
                            int[] iArr = this.pos;
                            rect2.set(iArr[0], iArr[1], iArr[0] + contentView.getMeasuredWidth(), this.pos[1] + contentView.getMeasuredHeight());
                            if (!rect.contains((int) event.getX(), (int) event.getY())) {
                                DialogsActivity.this.scrimPopupWindow.dismiss();
                            }
                        }
                    } else if (event.getActionMasked() == 4 && DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing()) {
                        DialogsActivity.this.scrimPopupWindow.dismiss();
                    }
                    return false;
                }
            });
            popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.DialogsActivity$6$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    DialogsActivity.AnonymousClass6.this.m3394lambda$didSelectTab$3$orgtelegramuiDialogsActivity$6(keyEvent);
                }
            });
            Rect backgroundPaddings = new Rect();
            Drawable shadowDrawable = DialogsActivity.this.getParentActivity().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
            shadowDrawable.getPadding(backgroundPaddings);
            popupLayout.setBackgroundDrawable(shadowDrawable);
            popupLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
            final LinearLayout linearLayout = new LinearLayout(DialogsActivity.this.getParentActivity());
            if (Build.VERSION.SDK_INT >= 21) {
                scrollView = new ScrollView(DialogsActivity.this.getParentActivity(), null, 0, R.style.scrollbarShapeStyle) { // from class: org.telegram.ui.DialogsActivity.6.2
                    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        setMeasuredDimension(linearLayout.getMeasuredWidth(), getMeasuredHeight());
                    }
                };
            } else {
                scrollView = new ScrollView(DialogsActivity.this.getParentActivity());
            }
            scrollView.setClipToPadding(false);
            popupLayout.addView(scrollView, LayoutHelper.createFrame(-2, -2.0f));
            linearLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
            linearLayout.setOrientation(1);
            DialogsActivity.this.scrimPopupWindowItems = new ActionBarMenuSubItem[3];
            int a = 0;
            final int N = tabView.getId() == DialogsActivity.this.filterTabsView.getDefaultTabId() ? 2 : 3;
            while (a < N) {
                ActionBarMenuSubItem cell = new ActionBarMenuSubItem(DialogsActivity.this.getParentActivity(), a == 0, a == N + (-1));
                if (a == 0) {
                    if (DialogsActivity.this.getMessagesController().dialogFilters.size() <= 1) {
                        a++;
                    } else {
                        cell.setTextAndIcon(LocaleController.getString("FilterReorder", R.string.FilterReorder), R.drawable.tabs_reorder);
                    }
                } else if (a != 1) {
                    cell.setTextAndIcon(LocaleController.getString("FilterDeleteItem", R.string.FilterDeleteItem), R.drawable.msg_delete);
                } else if (N == 2) {
                    cell.setTextAndIcon(LocaleController.getString("FilterEditAll", R.string.FilterEditAll), R.drawable.msg_edit);
                } else {
                    cell.setTextAndIcon(LocaleController.getString("FilterEdit", R.string.FilterEdit), R.drawable.msg_edit);
                }
                DialogsActivity.this.scrimPopupWindowItems[a] = cell;
                linearLayout.addView(cell);
                final int i = a;
                cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$6$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        DialogsActivity.AnonymousClass6.this.m3395lambda$didSelectTab$4$orgtelegramuiDialogsActivity$6(i, N, dialogFilter, view);
                    }
                });
                a++;
            }
            scrollView.addView(linearLayout, LayoutHelper.createScroll(-2, -2, 51));
            DialogsActivity.this.scrimPopupWindow = new ActionBarPopupWindow(popupLayout, -2, -2) { // from class: org.telegram.ui.DialogsActivity.6.3
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
                public void dismiss() {
                    super.dismiss();
                    if (DialogsActivity.this.scrimPopupWindow == this) {
                        DialogsActivity.this.scrimPopupWindow = null;
                        DialogsActivity.this.scrimPopupWindowItems = null;
                        if (DialogsActivity.this.scrimAnimatorSet != null) {
                            DialogsActivity.this.scrimAnimatorSet.cancel();
                            DialogsActivity.this.scrimAnimatorSet = null;
                        }
                        DialogsActivity.this.scrimAnimatorSet = new AnimatorSet();
                        DialogsActivity.this.scrimViewAppearing = false;
                        ArrayList<Animator> animators = new ArrayList<>();
                        animators.add(ObjectAnimator.ofInt(DialogsActivity.this.scrimPaint, AnimationProperties.PAINT_ALPHA, 0));
                        DialogsActivity.this.scrimAnimatorSet.playTogether(animators);
                        DialogsActivity.this.scrimAnimatorSet.setDuration(220L);
                        DialogsActivity.this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.6.3.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (DialogsActivity.this.scrimView != null) {
                                    DialogsActivity.this.scrimView.setBackground(null);
                                    DialogsActivity.this.scrimView = null;
                                }
                                if (DialogsActivity.this.fragmentView != null) {
                                    DialogsActivity.this.fragmentView.invalidate();
                                }
                            }
                        });
                        DialogsActivity.this.scrimAnimatorSet.start();
                        if (Build.VERSION.SDK_INT >= 19) {
                            DialogsActivity.this.getParentActivity().getWindow().getDecorView().setImportantForAccessibility(0);
                        }
                    }
                }
            };
            DialogsActivity.this.scrimViewBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, Theme.getColor(Theme.key_actionBarDefault));
            DialogsActivity.this.scrimPopupWindow.setDismissAnimationDuration(220);
            DialogsActivity.this.scrimPopupWindow.setOutsideTouchable(true);
            DialogsActivity.this.scrimPopupWindow.setClippingEnabled(true);
            DialogsActivity.this.scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            DialogsActivity.this.scrimPopupWindow.setFocusable(true);
            popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            DialogsActivity.this.scrimPopupWindow.setInputMethodMode(2);
            DialogsActivity.this.scrimPopupWindow.setSoftInputMode(0);
            DialogsActivity.this.scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
            tabView.getLocationInWindow(DialogsActivity.this.scrimViewLocation);
            int popupX = (DialogsActivity.this.scrimViewLocation[0] + backgroundPaddings.left) - AndroidUtilities.dp(16.0f);
            if (popupX >= AndroidUtilities.dp(6.0f)) {
                if (popupX > (DialogsActivity.this.fragmentView.getMeasuredWidth() - AndroidUtilities.dp(6.0f)) - popupLayout.getMeasuredWidth()) {
                    popupX = (DialogsActivity.this.fragmentView.getMeasuredWidth() - AndroidUtilities.dp(6.0f)) - popupLayout.getMeasuredWidth();
                }
            } else {
                popupX = AndroidUtilities.dp(6.0f);
            }
            int popupY = (DialogsActivity.this.scrimViewLocation[1] + tabView.getMeasuredHeight()) - AndroidUtilities.dp(12.0f);
            DialogsActivity.this.scrimPopupWindow.showAtLocation(DialogsActivity.this.fragmentView, 51, popupX, popupY);
            DialogsActivity.this.scrimView = tabView;
            DialogsActivity.this.scrimViewSelected = selected;
            DialogsActivity.this.fragmentView.invalidate();
            if (DialogsActivity.this.scrimAnimatorSet != null) {
                DialogsActivity.this.scrimAnimatorSet.cancel();
            }
            DialogsActivity.this.scrimAnimatorSet = new AnimatorSet();
            DialogsActivity.this.scrimViewAppearing = true;
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofInt(DialogsActivity.this.scrimPaint, AnimationProperties.PAINT_ALPHA, 0, 50));
            DialogsActivity.this.scrimAnimatorSet.playTogether(animators);
            DialogsActivity.this.scrimAnimatorSet.setDuration(150L);
            DialogsActivity.this.scrimAnimatorSet.start();
            return true;
        }

        /* renamed from: lambda$didSelectTab$3$org-telegram-ui-DialogsActivity$6 */
        public /* synthetic */ void m3394lambda$didSelectTab$3$orgtelegramuiDialogsActivity$6(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing()) {
                DialogsActivity.this.scrimPopupWindow.dismiss();
            }
        }

        /* renamed from: lambda$didSelectTab$4$org-telegram-ui-DialogsActivity$6 */
        public /* synthetic */ void m3395lambda$didSelectTab$4$orgtelegramuiDialogsActivity$6(int i, int N, MessagesController.DialogFilter dialogFilter, View v1) {
            if (i == 0) {
                DialogsActivity.this.resetScroll();
                DialogsActivity.this.filterTabsView.setIsEditing(true);
                DialogsActivity.this.showDoneItem(true);
            } else if (i == 1) {
                if (N == 2) {
                    DialogsActivity.this.presentFragment(new FiltersSetupActivity());
                } else {
                    DialogsActivity.this.presentFragment(new FilterCreateActivity(dialogFilter));
                }
            } else if (i == 2) {
                showDeleteAlert(dialogFilter);
            }
            if (DialogsActivity.this.scrimPopupWindow != null) {
                DialogsActivity.this.scrimPopupWindow.dismiss();
            }
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public boolean isTabMenuVisible() {
            return DialogsActivity.this.scrimPopupWindow != null && DialogsActivity.this.scrimPopupWindow.isShowing();
        }

        @Override // org.telegram.ui.Components.FilterTabsView.FilterTabsViewDelegate
        public void onDeletePressed(int id) {
            showDeleteAlert(DialogsActivity.this.getMessagesController().dialogFilters.get(id));
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$9 */
    /* loaded from: classes4.dex */
    class AnonymousClass9 extends LinearLayoutManager {
        private boolean fixOffset;
        final /* synthetic */ ViewPage val$viewPage;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass9(Context context, ViewPage viewPage) {
            super(context);
            DialogsActivity.this = this$0;
            this.val$viewPage = viewPage;
        }

        @Override // androidx.recyclerview.widget.LinearLayoutManager
        public void scrollToPositionWithOffset(int position, int offset) {
            if (this.fixOffset) {
                offset -= this.val$viewPage.listView.getPaddingTop();
            }
            super.scrollToPositionWithOffset(position, offset);
        }

        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.ItemTouchHelper.ViewDropHandler
        public void prepareForDrop(View view, View target, int x, int y) {
            this.fixOffset = true;
            super.prepareForDrop(view, target, x, y);
            this.fixOffset = false;
        }

        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            if (DialogsActivity.this.hasHiddenArchive() && position == 1) {
                super.smoothScrollToPosition(recyclerView, state, position);
                return;
            }
            LinearSmoothScrollerCustom linearSmoothScroller = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }

        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            View view;
            if (this.val$viewPage.listView.fastScrollAnimationRunning) {
                return 0;
            }
            boolean isDragging = this.val$viewPage.listView.getScrollState() == 1;
            int measuredDy = dy;
            int pTop = this.val$viewPage.listView.getPaddingTop();
            if (this.val$viewPage.dialogsType == 0 && !DialogsActivity.this.onlySelect && DialogsActivity.this.folderId == 0 && dy < 0 && DialogsActivity.this.getMessagesController().hasHiddenArchive() && this.val$viewPage.archivePullViewState == 2) {
                this.val$viewPage.listView.setOverScrollMode(0);
                int currentPosition = this.val$viewPage.layoutManager.findFirstVisibleItemPosition();
                if (currentPosition == 0 && (view = this.val$viewPage.layoutManager.findViewByPosition(currentPosition)) != null && view.getBottom() - pTop <= AndroidUtilities.dp(1.0f)) {
                    currentPosition = 1;
                }
                if (!isDragging) {
                    View view2 = this.val$viewPage.layoutManager.findViewByPosition(currentPosition);
                    if (view2 != null) {
                        int dialogHeight = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f) + 1;
                        int canScrollDy = (-(view2.getTop() - pTop)) + ((currentPosition - 1) * dialogHeight);
                        int positiveDy = Math.abs(dy);
                        if (canScrollDy < positiveDy) {
                            measuredDy = -canScrollDy;
                        }
                    }
                } else if (currentPosition == 0) {
                    View v = this.val$viewPage.layoutManager.findViewByPosition(currentPosition);
                    float k = ((v.getTop() - pTop) / v.getMeasuredHeight()) + 1.0f;
                    if (k > 1.0f) {
                        k = 1.0f;
                    }
                    this.val$viewPage.listView.setOverScrollMode(2);
                    measuredDy = (int) (measuredDy * (0.45f - (0.25f * k)));
                    if (measuredDy > -1) {
                        measuredDy = -1;
                    }
                    if (DialogsActivity.this.undoView[0].getVisibility() == 0) {
                        DialogsActivity.this.undoView[0].hide(true, 1);
                    }
                }
            }
            if (this.val$viewPage.dialogsType == 0 && this.val$viewPage.listView.getViewOffset() != 0.0f && dy > 0 && isDragging) {
                float ty = (int) this.val$viewPage.listView.getViewOffset();
                float ty2 = ty - dy;
                if (ty2 < 0.0f) {
                    measuredDy = (int) ty2;
                    ty2 = 0.0f;
                } else {
                    measuredDy = 0;
                }
                this.val$viewPage.listView.setViewsOffset(ty2);
            }
            if (this.val$viewPage.dialogsType == 0 && this.val$viewPage.archivePullViewState != 0 && DialogsActivity.this.hasHiddenArchive()) {
                int usedDy = super.scrollVerticallyBy(measuredDy, recycler, state);
                if (this.val$viewPage.pullForegroundDrawable != null) {
                    this.val$viewPage.pullForegroundDrawable.scrollDy = usedDy;
                }
                int currentPosition2 = this.val$viewPage.layoutManager.findFirstVisibleItemPosition();
                View firstView = null;
                if (currentPosition2 == 0) {
                    firstView = this.val$viewPage.layoutManager.findViewByPosition(currentPosition2);
                }
                if (currentPosition2 != 0 || firstView == null || firstView.getBottom() - pTop < AndroidUtilities.dp(4.0f)) {
                    DialogsActivity.this.startArchivePullingTime = 0L;
                    DialogsActivity.this.canShowHiddenArchive = false;
                    this.val$viewPage.archivePullViewState = 2;
                    if (this.val$viewPage.pullForegroundDrawable != null) {
                        this.val$viewPage.pullForegroundDrawable.resetText();
                        this.val$viewPage.pullForegroundDrawable.pullProgress = 0.0f;
                        this.val$viewPage.pullForegroundDrawable.setListView(this.val$viewPage.listView);
                    }
                } else {
                    if (DialogsActivity.this.startArchivePullingTime == 0) {
                        DialogsActivity.this.startArchivePullingTime = System.currentTimeMillis();
                    }
                    if (this.val$viewPage.archivePullViewState == 2 && this.val$viewPage.pullForegroundDrawable != null) {
                        this.val$viewPage.pullForegroundDrawable.showHidden();
                    }
                    float k2 = ((firstView.getTop() - pTop) / firstView.getMeasuredHeight()) + 1.0f;
                    if (k2 > 1.0f) {
                        k2 = 1.0f;
                    }
                    long pullingTime = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                    boolean canShowInternal = k2 > 0.85f && pullingTime > 220;
                    if (DialogsActivity.this.canShowHiddenArchive != canShowInternal) {
                        DialogsActivity.this.canShowHiddenArchive = canShowInternal;
                        if (this.val$viewPage.archivePullViewState == 2) {
                            this.val$viewPage.listView.performHapticFeedback(3, 2);
                            if (this.val$viewPage.pullForegroundDrawable != null) {
                                this.val$viewPage.pullForegroundDrawable.colorize(canShowInternal);
                            }
                        }
                    }
                    if (this.val$viewPage.archivePullViewState == 2 && measuredDy - usedDy != 0 && dy < 0 && isDragging) {
                        float tk = this.val$viewPage.listView.getViewOffset() / PullForegroundDrawable.getMaxOverscroll();
                        float ty3 = this.val$viewPage.listView.getViewOffset() - ((dy * 0.2f) * (1.0f - tk));
                        this.val$viewPage.listView.setViewsOffset(ty3);
                    }
                    if (this.val$viewPage.pullForegroundDrawable != null) {
                        this.val$viewPage.pullForegroundDrawable.pullProgress = k2;
                        this.val$viewPage.pullForegroundDrawable.setListView(this.val$viewPage.listView);
                    }
                }
                if (firstView != null) {
                    firstView.invalidate();
                }
                return usedDy;
            }
            return super.scrollVerticallyBy(measuredDy, recycler, state);
        }

        @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                try {
                    super.onLayoutChildren(recycler, state);
                    return;
                } catch (IndexOutOfBoundsException e) {
                    throw new RuntimeException("Inconsistency detected. dialogsListIsFrozen=" + DialogsActivity.this.dialogsListFrozen + " lastUpdateAction=" + DialogsActivity.this.debugLastUpdateAction);
                }
            }
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e2) {
                FileLog.e(e2);
                final ViewPage viewPage = this.val$viewPage;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$9$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsActivity.ViewPage.this.dialogsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3342lambda$createView$5$orgtelegramuiDialogsActivity(ViewPage viewPage, View view, int position) {
        int i = this.initialDialogsType;
        if (i == 10) {
            onItemLongClick(viewPage.listView, view, position, 0.0f, 0.0f, viewPage.dialogsType, viewPage.dialogsAdapter);
        } else if ((i != 11 && i != 13) || position != 1) {
            onItemClick(view, position, viewPage.dialogsAdapter);
        } else {
            Bundle args = new Bundle();
            args.putBoolean("forImport", true);
            long[] array = {getUserConfig().getClientUserId()};
            args.putLongArray("result", array);
            args.putInt("chatType", 4);
            String title = this.arguments.getString("importTitle");
            if (title != null) {
                args.putString("title", title);
            }
            GroupCreateFinalActivity activity = new GroupCreateFinalActivity(args);
            activity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() { // from class: org.telegram.ui.DialogsActivity.10
                @Override // org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate
                public void didStartChatCreation() {
                }

                @Override // org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate
                public void didFinishChatCreation(GroupCreateFinalActivity fragment, long chatId) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Long.valueOf(-chatId));
                    DialogsActivityDelegate dialogsActivityDelegate = DialogsActivity.this.delegate;
                    if (DialogsActivity.this.closeFragment) {
                        DialogsActivity.this.removeSelfFromStack();
                    }
                    dialogsActivityDelegate.didSelectDialogs(DialogsActivity.this, arrayList, null, true);
                }

                @Override // org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate
                public void didFailChatCreation() {
                }
            });
            presentFragment(activity);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$16 */
    /* loaded from: classes4.dex */
    public class AnonymousClass16 implements DialogsSearchAdapter.DialogsSearchAdapterDelegate {
        AnonymousClass16() {
            DialogsActivity.this = this$0;
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void searchStateChanged(boolean search, boolean animated) {
            if (DialogsActivity.this.searchViewPager.emptyView.getVisibility() == 0) {
                animated = true;
            }
            if (DialogsActivity.this.searching && DialogsActivity.this.searchWas && DialogsActivity.this.searchViewPager.emptyView != null) {
                if (search || DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() != 0) {
                    DialogsActivity.this.searchViewPager.emptyView.showProgress(true, animated);
                } else {
                    DialogsActivity.this.searchViewPager.emptyView.showProgress(false, animated);
                }
            }
            if (search && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() == 0) {
                DialogsActivity.this.searchViewPager.cancelEnterAnimation();
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void didPressedOnSubDialog(long did) {
            if (DialogsActivity.this.onlySelect) {
                if (DialogsActivity.this.validateSlowModeDialog(did)) {
                    if (DialogsActivity.this.selectedDialogs.isEmpty()) {
                        DialogsActivity.this.didSelectResult(did, true, false);
                        return;
                    }
                    boolean checked = DialogsActivity.this.addOrRemoveSelectedDialog(did, null);
                    DialogsActivity.this.findAndUpdateCheckBox(did, checked);
                    DialogsActivity.this.updateSelectedCount();
                    DialogsActivity.this.actionBar.closeSearchField();
                    return;
                }
                return;
            }
            Bundle args = new Bundle();
            if (DialogObject.isUserDialog(did)) {
                args.putLong("user_id", did);
            } else {
                args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
            }
            DialogsActivity.this.closeSearch();
            if (AndroidUtilities.isTablet() && DialogsActivity.this.viewPages != null) {
                for (int a = 0; a < DialogsActivity.this.viewPages.length; a++) {
                    DialogsActivity.this.viewPages[a].dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = did);
                }
                DialogsActivity.this.updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
            if (DialogsActivity.this.searchString != null) {
                if (DialogsActivity.this.getMessagesController().checkCanOpenChat(args, DialogsActivity.this)) {
                    DialogsActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    DialogsActivity.this.presentFragment(new ChatActivity(args));
                }
            } else if (DialogsActivity.this.getMessagesController().checkCanOpenChat(args, DialogsActivity.this)) {
                DialogsActivity.this.presentFragment(new ChatActivity(args));
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void needRemoveHint(final long did) {
            TLRPC.User user;
            if (DialogsActivity.this.getParentActivity() == null || (user = DialogsActivity.this.getMessagesController().getUser(Long.valueOf(did))) == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("ChatHintsDeleteAlertTitle", R.string.ChatHintsDeleteAlertTitle));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", R.string.ChatHintsDeleteAlert, ContactsController.formatName(user.first_name, user.last_name))));
            builder.setPositiveButton(LocaleController.getString("StickersRemove", R.string.StickersRemove), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$16$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.AnonymousClass16.this.m3389lambda$needRemoveHint$0$orgtelegramuiDialogsActivity$16(did, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog dialog = builder.create();
            DialogsActivity.this.showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }

        /* renamed from: lambda$needRemoveHint$0$org-telegram-ui-DialogsActivity$16 */
        public /* synthetic */ void m3389lambda$needRemoveHint$0$orgtelegramuiDialogsActivity$16(long did, DialogInterface dialogInterface, int i) {
            DialogsActivity.this.getMediaDataController().removePeer(did);
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void needClearList() {
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            if (DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isSearchWas() && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                builder.setTitle(LocaleController.getString("ClearSearchAlertPartialTitle", R.string.ClearSearchAlertPartialTitle));
                builder.setMessage(LocaleController.formatPluralString("ClearSearchAlertPartial", DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getRecentResultsCount(), new Object[0]));
                builder.setPositiveButton(LocaleController.getString("Clear", R.string.Clear).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$16$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.AnonymousClass16.this.m3387lambda$needClearList$1$orgtelegramuiDialogsActivity$16(dialogInterface, i);
                    }
                });
            } else {
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", R.string.ClearSearchAlert));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$16$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.AnonymousClass16.this.m3388lambda$needClearList$2$orgtelegramuiDialogsActivity$16(dialogInterface, i);
                    }
                });
            }
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog dialog = builder.create();
            DialogsActivity.this.showDialog(dialog);
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        }

        /* renamed from: lambda$needClearList$1$org-telegram-ui-DialogsActivity$16 */
        public /* synthetic */ void m3387lambda$needClearList$1$orgtelegramuiDialogsActivity$16(DialogInterface dialogInterface, int i) {
            DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
        }

        /* renamed from: lambda$needClearList$2$org-telegram-ui-DialogsActivity$16 */
        public /* synthetic */ void m3388lambda$needClearList$2$orgtelegramuiDialogsActivity$16(DialogInterface dialogInterface, int i) {
            if (DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
            } else {
                DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentHashtags();
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void runResultsEnterAnimation() {
            if (DialogsActivity.this.searchViewPager != null) {
                DialogsActivity.this.searchViewPager.runResultsEnterAnimation();
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public boolean isSelected(long dialogId) {
            return DialogsActivity.this.selectedDialogs.contains(Long.valueOf(dialogId));
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3343lambda$createView$6$orgtelegramuiDialogsActivity(View view, int position) {
        if (this.initialDialogsType == 10) {
            onItemLongClick(this.searchViewPager.searchListView, view, position, 0.0f, 0.0f, -1, this.searchViewPager.dialogsSearchAdapter);
        } else {
            onItemClick(view, position, this.searchViewPager.dialogsSearchAdapter);
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3344lambda$createView$7$orgtelegramuiDialogsActivity(boolean showMediaFilters, ArrayList users, ArrayList dates, boolean archive3) {
        updateFiltersView(showMediaFilters, users, dates, archive3, true);
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3345lambda$createView$8$orgtelegramuiDialogsActivity(View view, int position) {
        this.filtersView.cancelClickRunnables(true);
        addSearchFilter(this.filtersView.getFilterAt(position));
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3346lambda$createView$9$orgtelegramuiDialogsActivity(View v) {
        if (this.parentLayout != null && this.parentLayout.isInPreviewMode()) {
            finishPreviewFragment();
        } else if (this.initialDialogsType == 10) {
            if (this.delegate == null || this.selectedDialogs.isEmpty()) {
                return;
            }
            this.delegate.didSelectDialogs(this, this.selectedDialogs, null, false);
        } else if (this.floatingButton.getVisibility() != 0) {
        } else {
            Bundle args = new Bundle();
            args.putBoolean("destroyAfterSelect", true);
            presentFragment(new ContactsActivity(args));
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3336lambda$createView$10$orgtelegramuiDialogsActivity(View v) {
        if (this.delegate == null || this.selectedDialogs.isEmpty()) {
            return;
        }
        this.delegate.didSelectDialogs(this, this.selectedDialogs, this.commentView.getFieldText(), false);
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-DialogsActivity */
    public /* synthetic */ boolean m3337lambda$createView$11$orgtelegramuiDialogsActivity(FrameLayout writeButtonBackground, View v) {
        if (this.isNextButton) {
            return false;
        }
        onSendLongClick(writeButtonBackground);
        return true;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3338lambda$createView$12$orgtelegramuiDialogsActivity(View v) {
        if (!SharedConfig.isAppUpdateAvailable()) {
            return;
        }
        AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, true, getParentActivity());
    }

    /* renamed from: org.telegram.ui.DialogsActivity$26 */
    /* loaded from: classes4.dex */
    public class AnonymousClass26 extends UndoView {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass26(Context context) {
            super(context);
            DialogsActivity.this = this$0;
        }

        @Override // android.view.View
        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            if (this == DialogsActivity.this.undoView[0] && DialogsActivity.this.undoView[1].getVisibility() != 0) {
                DialogsActivity.this.additionalFloatingTranslation = (getMeasuredHeight() + AndroidUtilities.dp(8.0f)) - translationY;
                if (DialogsActivity.this.additionalFloatingTranslation < 0.0f) {
                    DialogsActivity.this.additionalFloatingTranslation = 0.0f;
                }
                if (!DialogsActivity.this.floatingHidden) {
                    DialogsActivity.this.updateFloatingButtonOffset();
                }
            }
        }

        @Override // org.telegram.ui.Components.UndoView
        protected boolean canUndo() {
            for (int a = 0; a < DialogsActivity.this.viewPages.length; a++) {
                if (DialogsActivity.this.viewPages[a].dialogsItemAnimator.isRunning()) {
                    return false;
                }
            }
            return true;
        }

        @Override // org.telegram.ui.Components.UndoView
        protected void onRemoveDialogAction(long currentDialogId, int action) {
            if (action == 1 || action == 27) {
                DialogsActivity.this.debugLastUpdateAction = 1;
                DialogsActivity.this.setDialogsListFrozen(true);
                if (DialogsActivity.this.frozenDialogsList != null) {
                    int selectedIndex = -1;
                    int i = 0;
                    while (true) {
                        if (i >= DialogsActivity.this.frozenDialogsList.size()) {
                            break;
                        } else if (((TLRPC.Dialog) DialogsActivity.this.frozenDialogsList.get(i)).id != currentDialogId) {
                            i++;
                        } else {
                            selectedIndex = i;
                            break;
                        }
                    }
                    if (selectedIndex >= 0) {
                        final TLRPC.Dialog dialog = (TLRPC.Dialog) DialogsActivity.this.frozenDialogsList.remove(selectedIndex);
                        DialogsActivity.this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                        final int finalSelectedIndex = selectedIndex;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$26$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                DialogsActivity.AnonymousClass26.this.m3390lambda$onRemoveDialogAction$0$orgtelegramuiDialogsActivity$26(finalSelectedIndex, dialog);
                            }
                        });
                        return;
                    }
                    DialogsActivity.this.setDialogsListFrozen(false);
                }
            }
        }

        /* renamed from: lambda$onRemoveDialogAction$0$org-telegram-ui-DialogsActivity$26 */
        public /* synthetic */ void m3390lambda$onRemoveDialogAction$0$orgtelegramuiDialogsActivity$26(int finalSelectedIndex, TLRPC.Dialog dialog) {
            if (DialogsActivity.this.frozenDialogsList != null) {
                DialogsActivity.this.frozenDialogsList.add(finalSelectedIndex, dialog);
                DialogsActivity.this.viewPages[0].dialogsAdapter.notifyItemInserted(finalSelectedIndex);
                DialogsActivity.this.dialogInsertFinished = 2;
            }
        }
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3339lambda$createView$13$orgtelegramuiDialogsActivity(View e) {
        finishPreviewFragment();
    }

    public void updateCommentView() {
    }

    private /* synthetic */ void lambda$updateCommentView$14(ValueAnimator a) {
        this.commentView.setTranslationY(((Float) a.getAnimatedValue()).floatValue());
    }

    private void updateAppUpdateViews(boolean animated) {
        boolean show;
        if (this.updateLayout == null) {
            return;
        }
        if (SharedConfig.isAppUpdateAvailable()) {
            FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
            File path = getFileLoader().getPathToAttach(SharedConfig.pendingAppUpdate.document, true);
            show = path.exists();
        } else {
            show = false;
        }
        if (show) {
            if (this.updateLayout.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.updateLayoutAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.updateLayout.setVisibility(0);
            this.updateLayout.setTag(1);
            if (!animated) {
                this.updateLayout.setTranslationY(0.0f);
                return;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.updateLayoutAnimator = animatorSet2;
            animatorSet2.setDuration(180L);
            this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.updateLayoutAnimator.playTogether(ObjectAnimator.ofFloat(this.updateLayout, View.TRANSLATION_Y, 0.0f));
            this.updateLayoutAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.28
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    DialogsActivity.this.updateLayoutAnimator = null;
                }
            });
            this.updateLayoutAnimator.start();
        } else if (this.updateLayout.getTag() == null) {
        } else {
            this.updateLayout.setTag(null);
            if (!animated) {
                this.updateLayout.setTranslationY(AndroidUtilities.dp(48.0f));
                this.updateLayout.setVisibility(4);
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.updateLayoutAnimator = animatorSet3;
            animatorSet3.setDuration(180L);
            this.updateLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.updateLayoutAnimator.playTogether(ObjectAnimator.ofFloat(this.updateLayout, View.TRANSLATION_Y, AndroidUtilities.dp(48.0f)));
            this.updateLayoutAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.29
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (DialogsActivity.this.updateLayout.getTag() == null) {
                        DialogsActivity.this.updateLayout.setVisibility(4);
                    }
                    DialogsActivity.this.updateLayoutAnimator = null;
                }
            });
            this.updateLayoutAnimator.start();
        }
    }

    public void updateContextViewPosition() {
        float filtersTabsHeight = 0.0f;
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.getVisibility() != 8) {
            filtersTabsHeight = this.filterTabsView.getMeasuredHeight();
        }
        float searchTabsHeight = 0.0f;
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        if (tabsView != null && tabsView.getVisibility() != 8) {
            searchTabsHeight = this.searchTabsView.getMeasuredHeight();
        }
        if (this.fragmentContextView != null) {
            float from = 0.0f;
            FragmentContextView fragmentContextView = this.fragmentLocationContextView;
            if (fragmentContextView != null && fragmentContextView.getVisibility() == 0) {
                from = 0.0f + AndroidUtilities.dp(36.0f);
            }
            FragmentContextView fragmentContextView2 = this.fragmentContextView;
            float topPadding = fragmentContextView2.getTopPadding() + from + this.actionBar.getTranslationY();
            float f = this.searchAnimationProgress;
            fragmentContextView2.setTranslationY(topPadding + ((1.0f - f) * filtersTabsHeight) + (f * searchTabsHeight) + this.tabsYOffset);
        }
        if (this.fragmentLocationContextView != null) {
            float from2 = 0.0f;
            FragmentContextView fragmentContextView3 = this.fragmentContextView;
            if (fragmentContextView3 != null && fragmentContextView3.getVisibility() == 0) {
                from2 = 0.0f + AndroidUtilities.dp(this.fragmentContextView.getStyleHeight()) + this.fragmentContextView.getTopPadding();
            }
            FragmentContextView fragmentContextView4 = this.fragmentLocationContextView;
            float topPadding2 = fragmentContextView4.getTopPadding() + from2 + this.actionBar.getTranslationY();
            float f2 = this.searchAnimationProgress;
            fragmentContextView4.setTranslationY(topPadding2 + ((1.0f - f2) * filtersTabsHeight) + (f2 * searchTabsHeight) + this.tabsYOffset);
        }
    }

    public void updateFiltersView(boolean showMediaFilters, ArrayList<Object> users, ArrayList<FiltersView.DateData> dates, boolean archive3, boolean animated) {
        boolean archive4;
        if (!this.searchIsShowed || this.onlySelect) {
            return;
        }
        boolean hasMediaFilter = false;
        boolean hasUserFilter = false;
        boolean hasDateFilter = false;
        boolean hasArchiveFilter = false;
        ArrayList<FiltersView.MediaFilterData> currentSearchFilters = this.searchViewPager.getCurrentSearchFilters();
        for (int i = 0; i < currentSearchFilters.size(); i++) {
            if (currentSearchFilters.get(i).isMedia()) {
                hasMediaFilter = true;
            } else if (currentSearchFilters.get(i).filterType == 4) {
                hasUserFilter = true;
            } else if (currentSearchFilters.get(i).filterType == 6) {
                hasDateFilter = true;
            } else if (currentSearchFilters.get(i).filterType == 7) {
                hasArchiveFilter = true;
            }
        }
        if (!hasArchiveFilter) {
            archive4 = archive3;
        } else {
            archive4 = false;
        }
        boolean visible = false;
        boolean hasUsersOrDates = (users != null && !users.isEmpty()) || (dates != null && !dates.isEmpty()) || archive4;
        if ((hasMediaFilter || hasUsersOrDates || !showMediaFilters) && hasUsersOrDates) {
            ArrayList<Object> finalUsers = (users == null || users.isEmpty() || hasUserFilter) ? null : users;
            ArrayList<FiltersView.DateData> finalDates = (dates == null || dates.isEmpty() || hasDateFilter) ? null : dates;
            if (finalUsers != null || finalDates != null || archive4) {
                visible = true;
                this.filtersView.setUsersAndDates(finalUsers, finalDates, archive4);
            }
        }
        if (!visible) {
            this.filtersView.setUsersAndDates(null, null, false);
        }
        if (!animated) {
            this.filtersView.getAdapter().notifyDataSetChanged();
        }
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        if (tabsView != null) {
            tabsView.hide(visible, true);
        }
        this.filtersView.setEnabled(visible);
        this.filtersView.setVisibility(0);
    }

    private void addSearchFilter(FiltersView.MediaFilterData filter) {
        if (!this.searchIsShowed) {
            return;
        }
        ArrayList<FiltersView.MediaFilterData> currentSearchFilters = this.searchViewPager.getCurrentSearchFilters();
        if (!currentSearchFilters.isEmpty()) {
            for (int i = 0; i < currentSearchFilters.size(); i++) {
                if (filter.isSameType(currentSearchFilters.get(i))) {
                    return;
                }
            }
        }
        currentSearchFilters.add(filter);
        this.actionBar.setSearchFilter(filter);
        this.actionBar.setSearchFieldText("");
        updateFiltersView(true, null, null, false, true);
    }

    private void createActionMode(String tag) {
        if (this.actionBar.actionModeIsExist(tag)) {
            return;
        }
        ActionBarMenu actionMode = this.actionBar.createActionMode(false, tag);
        actionMode.setBackgroundColor(0);
        actionMode.drawBlur = false;
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(DialogsActivity$$ExternalSyntheticLambda20.INSTANCE);
        this.pinItem = actionMode.addItemWithWidth(100, R.drawable.msg_pin, AndroidUtilities.dp(54.0f));
        this.muteItem = actionMode.addItemWithWidth(104, R.drawable.msg_mute, AndroidUtilities.dp(54.0f));
        this.archive2Item = actionMode.addItemWithWidth(archive2, R.drawable.msg_archive, AndroidUtilities.dp(54.0f));
        this.deleteItem = actionMode.addItemWithWidth(102, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete));
        ActionBarMenuItem otherItem = actionMode.addItemWithWidth(0, R.drawable.ic_ab_other, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.archiveItem = otherItem.addSubItem(105, R.drawable.msg_archive, LocaleController.getString("Archive", R.string.Archive));
        this.pin2Item = otherItem.addSubItem(pin2, R.drawable.msg_pin, LocaleController.getString("DialogPin", R.string.DialogPin));
        this.addToFolderItem = otherItem.addSubItem(add_to_folder, R.drawable.msg_addfolder, LocaleController.getString("FilterAddTo", R.string.FilterAddTo));
        this.removeFromFolderItem = otherItem.addSubItem(remove_from_folder, R.drawable.msg_removefolder, LocaleController.getString("FilterRemoveFrom", R.string.FilterRemoveFrom));
        this.readItem = otherItem.addSubItem(101, R.drawable.msg_markread, LocaleController.getString("MarkAsRead", R.string.MarkAsRead));
        this.clearItem = otherItem.addSubItem(clear, R.drawable.msg_clear, LocaleController.getString("ClearHistory", R.string.ClearHistory));
        this.blockItem = otherItem.addSubItem(block, R.drawable.msg_block, LocaleController.getString("BlockUser", R.string.BlockUser));
        this.actionModeViews.add(this.pinItem);
        this.actionModeViews.add(this.archive2Item);
        this.actionModeViews.add(this.muteItem);
        this.actionModeViews.add(this.deleteItem);
        this.actionModeViews.add(otherItem);
        if (tag == null) {
            this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass30());
        }
    }

    public static /* synthetic */ boolean lambda$createActionMode$15(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: org.telegram.ui.DialogsActivity$30 */
    /* loaded from: classes4.dex */
    public class AnonymousClass30 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass30() {
            DialogsActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            int currentCount;
            ArrayList<Long> neverShow;
            long did;
            ArrayList<Long> neverShow2;
            if ((id == 201 || id == 200 || id == 202) && DialogsActivity.this.searchViewPager != null) {
                DialogsActivity.this.searchViewPager.onActionBarItemClick(id);
            } else if (id == -1) {
                if (DialogsActivity.this.filterTabsView == null || !DialogsActivity.this.filterTabsView.isEditing()) {
                    if (DialogsActivity.this.actionBar.isActionModeShowed()) {
                        if (DialogsActivity.this.searchViewPager == null || DialogsActivity.this.searchViewPager.getVisibility() != 0 || !DialogsActivity.this.searchViewPager.actionModeShowing()) {
                            DialogsActivity.this.hideActionMode(true);
                            return;
                        } else {
                            DialogsActivity.this.searchViewPager.hideActionMode();
                            return;
                        }
                    } else if (!DialogsActivity.this.onlySelect && DialogsActivity.this.folderId == 0) {
                        if (DialogsActivity.this.parentLayout != null) {
                            DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                            return;
                        }
                        return;
                    } else {
                        DialogsActivity.this.finishFragment();
                        return;
                    }
                }
                DialogsActivity.this.filterTabsView.setIsEditing(false);
                DialogsActivity.this.showDoneItem(false);
            } else if (id != 1) {
                if (id == 2) {
                    DialogsActivity.this.presentFragment(new ProxyListActivity());
                } else if (id == 3) {
                    DialogsActivity.this.showSearch(true, true, true);
                    DialogsActivity.this.actionBar.openSearchField(true);
                } else if (id >= 10 && id < 14) {
                    if (DialogsActivity.this.getParentActivity() != null) {
                        DialogsActivityDelegate oldDelegate = DialogsActivity.this.delegate;
                        LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                        launchActivity.switchToAccount(id - 10, true);
                        DialogsActivity dialogsActivity = new DialogsActivity(DialogsActivity.this.arguments);
                        dialogsActivity.setDelegate(oldDelegate);
                        launchActivity.presentFragment(dialogsActivity, false, true);
                    }
                } else if (id == DialogsActivity.add_to_folder) {
                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                    FiltersListBottomSheet sheet = new FiltersListBottomSheet(dialogsActivity2, dialogsActivity2.selectedDialogs);
                    sheet.setDelegate(new FiltersListBottomSheet.FiltersListBottomSheetDelegate() { // from class: org.telegram.ui.DialogsActivity$30$$ExternalSyntheticLambda2
                        @Override // org.telegram.ui.Components.FiltersListBottomSheet.FiltersListBottomSheetDelegate
                        public final void didSelectFilter(MessagesController.DialogFilter dialogFilter) {
                            DialogsActivity.AnonymousClass30.this.m3393lambda$onItemClick$2$orgtelegramuiDialogsActivity$30(dialogFilter);
                        }
                    });
                    DialogsActivity.this.showDialog(sheet);
                } else if (id != DialogsActivity.remove_from_folder) {
                    if (id == 100 || id == 101 || id == 102 || id == DialogsActivity.clear || id == 104 || id == 105 || id == DialogsActivity.block || id == DialogsActivity.archive2 || id == DialogsActivity.pin2) {
                        DialogsActivity dialogsActivity3 = DialogsActivity.this;
                        dialogsActivity3.performSelectedDialogsAction(dialogsActivity3.selectedDialogs, id, true);
                    }
                } else {
                    MessagesController.DialogFilter filter = DialogsActivity.this.getMessagesController().dialogFilters.get(DialogsActivity.this.viewPages[0].selectedType);
                    DialogsActivity dialogsActivity4 = DialogsActivity.this;
                    ArrayList<Long> neverShow3 = FiltersListBottomSheet.getDialogsCount(dialogsActivity4, filter, dialogsActivity4.selectedDialogs, false, false);
                    if (filter != null) {
                        currentCount = filter.neverShow.size();
                    } else {
                        currentCount = 0;
                    }
                    if (currentCount + neverShow3.size() > 100) {
                        DialogsActivity dialogsActivity5 = DialogsActivity.this;
                        dialogsActivity5.showDialog(AlertsCreator.createSimpleAlert(dialogsActivity5.getParentActivity(), LocaleController.getString("FilterAddToAlertFullTitle", R.string.FilterAddToAlertFullTitle), LocaleController.getString("FilterAddToAlertFullText", R.string.FilterAddToAlertFullText)).create());
                        return;
                    }
                    if (neverShow3.isEmpty()) {
                        neverShow = neverShow3;
                    } else {
                        filter.neverShow.addAll(neverShow3);
                        for (int a = 0; a < neverShow3.size(); a++) {
                            Long did2 = neverShow3.get(a);
                            filter.alwaysShow.remove(did2);
                            filter.pinnedDialogs.delete(did2.longValue());
                        }
                        neverShow = neverShow3;
                        FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, false, false, true, false, false, DialogsActivity.this, null);
                    }
                    if (neverShow.size() == 1) {
                        neverShow2 = neverShow;
                        did = neverShow2.get(0).longValue();
                    } else {
                        neverShow2 = neverShow;
                        did = 0;
                    }
                    DialogsActivity.this.getUndoView().showWithAction(did, 21, Integer.valueOf(neverShow2.size()), filter, (Runnable) null, (Runnable) null);
                    DialogsActivity.this.hideActionMode(false);
                }
            } else if (DialogsActivity.this.getParentActivity() != null) {
                SharedConfig.appLocked = true;
                SharedConfig.saveConfig();
                int[] position = new int[2];
                DialogsActivity.this.passcodeItem.getLocationInWindow(position);
                ((LaunchActivity) DialogsActivity.this.getParentActivity()).showPasscodeActivity(false, true, position[0] + (DialogsActivity.this.passcodeItem.getMeasuredWidth() / 2), position[1] + (DialogsActivity.this.passcodeItem.getMeasuredHeight() / 2), new Runnable() { // from class: org.telegram.ui.DialogsActivity$30$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsActivity.AnonymousClass30.this.m3391lambda$onItemClick$0$orgtelegramuiDialogsActivity$30();
                    }
                }, new Runnable() { // from class: org.telegram.ui.DialogsActivity$30$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsActivity.AnonymousClass30.this.m3392lambda$onItemClick$1$orgtelegramuiDialogsActivity$30();
                    }
                });
                DialogsActivity.this.updatePasscodeButton();
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-DialogsActivity$30 */
        public /* synthetic */ void m3391lambda$onItemClick$0$orgtelegramuiDialogsActivity$30() {
            DialogsActivity.this.passcodeItem.setAlpha(1.0f);
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-DialogsActivity$30 */
        public /* synthetic */ void m3392lambda$onItemClick$1$orgtelegramuiDialogsActivity$30() {
            DialogsActivity.this.passcodeItem.setAlpha(0.0f);
        }

        /* renamed from: lambda$onItemClick$2$org-telegram-ui-DialogsActivity$30 */
        public /* synthetic */ void m3393lambda$onItemClick$2$orgtelegramuiDialogsActivity$30(MessagesController.DialogFilter filter) {
            int currentCount;
            ArrayList<Long> alwaysShow;
            long did;
            ArrayList<Long> alwaysShow2;
            DialogsActivity dialogsActivity = DialogsActivity.this;
            ArrayList<Long> alwaysShow3 = FiltersListBottomSheet.getDialogsCount(dialogsActivity, filter, dialogsActivity.selectedDialogs, true, false);
            if (filter != null) {
                currentCount = filter.alwaysShow.size();
            } else {
                currentCount = 0;
            }
            int totalCount = currentCount + alwaysShow3.size();
            if ((totalCount > DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitDefault && !DialogsActivity.this.getUserConfig().isPremium()) || totalCount > DialogsActivity.this.getMessagesController().dialogFiltersChatsLimitPremium) {
                DialogsActivity dialogsActivity2 = DialogsActivity.this;
                DialogsActivity dialogsActivity3 = DialogsActivity.this;
                dialogsActivity2.showDialog(new LimitReachedBottomSheet(dialogsActivity3, dialogsActivity3.fragmentView.getContext(), 4, DialogsActivity.this.currentAccount));
                return;
            }
            if (filter == null) {
                DialogsActivity.this.presentFragment(new FilterCreateActivity(null, alwaysShow3));
            } else {
                if (!alwaysShow3.isEmpty()) {
                    for (int a = 0; a < alwaysShow3.size(); a++) {
                        filter.neverShow.remove(alwaysShow3.get(a));
                    }
                    filter.alwaysShow.addAll(alwaysShow3);
                    alwaysShow = alwaysShow3;
                    FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, false, false, true, true, false, DialogsActivity.this, null);
                } else {
                    alwaysShow = alwaysShow3;
                }
                if (alwaysShow.size() == 1) {
                    alwaysShow2 = alwaysShow;
                    did = alwaysShow2.get(0).longValue();
                } else {
                    alwaysShow2 = alwaysShow;
                    did = 0;
                }
                DialogsActivity.this.getUndoView().showWithAction(did, 20, Integer.valueOf(alwaysShow2.size()), filter, (Runnable) null, (Runnable) null);
            }
            DialogsActivity.this.hideActionMode(true);
        }
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
        MessagesController.DialogFilter filter = getMessagesController().dialogFilters.get(this.viewPages[animated].selectedType);
        int i = 0;
        if (filter.isDefault()) {
            this.viewPages[animated].dialogsType = 0;
            this.viewPages[animated].listView.updatePullState();
        } else {
            if (this.viewPages[animated == 0 ? (char) 1 : (char) 0].dialogsType == 7) {
                this.viewPages[animated].dialogsType = 8;
            } else {
                this.viewPages[animated].dialogsType = 7;
            }
            this.viewPages[animated].listView.setScrollEnabled(true);
            getMessagesController().selectDialogFilter(filter, this.viewPages[animated].dialogsType == 8 ? 1 : 0);
        }
        this.viewPages[1].isLocked = filter.locked;
        this.viewPages[animated].dialogsAdapter.setDialogsType(this.viewPages[animated].dialogsType);
        LinearLayoutManager linearLayoutManager = this.viewPages[animated].layoutManager;
        if (this.viewPages[animated].dialogsType == 0 && hasHiddenArchive()) {
            i = 1;
        }
        linearLayoutManager.scrollToPositionWithOffset(i, (int) this.actionBar.getTranslationY());
        checkListLoad(this.viewPages[animated]);
    }

    public void showScrollbars(boolean show) {
        if (this.viewPages == null || this.scrollBarVisible == show) {
            return;
        }
        this.scrollBarVisible = show;
        int a = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (a < viewPageArr.length) {
                if (show) {
                    viewPageArr[a].listView.setScrollbarFadingEnabled(false);
                }
                this.viewPages[a].listView.setVerticalScrollBarEnabled(show);
                if (show) {
                    this.viewPages[a].listView.setScrollbarFadingEnabled(true);
                }
                a++;
            } else {
                return;
            }
        }
    }

    private void scrollToFilterTab(int index) {
        if (this.filterTabsView == null || this.viewPages[0].selectedType == index) {
            return;
        }
        this.filterTabsView.selectTabWithId(index, 1.0f);
        this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(false);
        ViewPage[] viewPageArr = this.viewPages;
        viewPageArr[1].selectedType = viewPageArr[0].selectedType;
        this.viewPages[0].selectedType = index;
        switchToCurrentSelectedMode(false);
        switchToCurrentSelectedMode(true);
        updateCounters(false);
    }

    private void updateFilterTabs(boolean force, boolean animated) {
        int p;
        if (this.filterTabsView == null || this.inPreviewMode || this.searchIsShowed) {
            return;
        }
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.scrimPopupWindow = null;
        }
        ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
        MessagesController.getMainSettings(this.currentAccount);
        boolean z = true;
        if (filters.size() > 1) {
            if (force || this.filterTabsView.getVisibility() != 0) {
                boolean animatedUpdateItems = animated;
                if (this.filterTabsView.getVisibility() != 0) {
                    animatedUpdateItems = false;
                }
                this.canShowFilterTabsView = true;
                boolean updateCurrentTab = this.filterTabsView.isEmpty();
                updateFilterTabsVisibility(animated);
                int id = this.filterTabsView.getCurrentTabId();
                int stableId = this.filterTabsView.getCurrentTabStableId();
                if (id != this.filterTabsView.getDefaultTabId() && id >= filters.size()) {
                    this.filterTabsView.resetTabId();
                }
                this.filterTabsView.removeTabs();
                int N = filters.size();
                for (int a = 0; a < N; a++) {
                    if (filters.get(a).isDefault()) {
                        this.filterTabsView.addTab(a, 0, LocaleController.getString("FilterAllChats", R.string.FilterAllChats), true, filters.get(a).locked);
                    } else {
                        this.filterTabsView.addTab(a, filters.get(a).localId, filters.get(a).name, false, filters.get(a).locked);
                    }
                }
                if (stableId >= 0 && this.filterTabsView.getStableId(this.viewPages[0].selectedType) != stableId) {
                    updateCurrentTab = true;
                    this.viewPages[0].selectedType = id;
                }
                int a2 = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (a2 >= viewPageArr.length) {
                        break;
                    }
                    if (viewPageArr[a2].selectedType >= filters.size()) {
                        this.viewPages[a2].selectedType = filters.size() - 1;
                    }
                    this.viewPages[a2].listView.setScrollingTouchSlop(1);
                    a2++;
                }
                this.filterTabsView.finishAddingTabs(animatedUpdateItems);
                if (updateCurrentTab) {
                    switchToCurrentSelectedMode(false);
                }
                if (this.parentLayout != null) {
                    DrawerLayoutContainer drawerLayoutContainer = this.parentLayout.getDrawerLayoutContainer();
                    if (id != this.filterTabsView.getFirstTabId() && SharedConfig.getChatSwipeAction(this.currentAccount) == 5) {
                        z = false;
                    }
                    drawerLayoutContainer.setAllowOpenDrawerBySwipe(z);
                }
                FilterTabsView filterTabsView = this.filterTabsView;
                if (filterTabsView.isLocked(filterTabsView.getCurrentTabId())) {
                    this.filterTabsView.selectFirstTab();
                }
            }
        } else {
            if (this.filterTabsView.getVisibility() != 8) {
                this.filterTabsView.setIsEditing(false);
                showDoneItem(false);
                this.maybeStartTracking = false;
                if (this.startedTracking) {
                    this.startedTracking = false;
                    this.viewPages[0].setTranslationX(0.0f);
                    ViewPage[] viewPageArr2 = this.viewPages;
                    viewPageArr2[1].setTranslationX(viewPageArr2[0].getMeasuredWidth());
                }
                if (this.viewPages[0].selectedType != this.filterTabsView.getDefaultTabId()) {
                    this.viewPages[0].selectedType = this.filterTabsView.getDefaultTabId();
                    this.viewPages[0].dialogsAdapter.setDialogsType(0);
                    this.viewPages[0].dialogsType = 0;
                    this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                }
                this.viewPages[1].setVisibility(8);
                this.viewPages[1].selectedType = 0;
                this.viewPages[1].dialogsAdapter.setDialogsType(0);
                this.viewPages[1].dialogsType = 0;
                this.viewPages[1].dialogsAdapter.notifyDataSetChanged();
                this.canShowFilterTabsView = false;
                updateFilterTabsVisibility(animated);
                int a3 = 0;
                while (true) {
                    ViewPage[] viewPageArr3 = this.viewPages;
                    if (a3 >= viewPageArr3.length) {
                        break;
                    }
                    if (viewPageArr3[a3].dialogsType == 0 && this.viewPages[a3].archivePullViewState == 2 && hasHiddenArchive() && ((p = this.viewPages[a3].layoutManager.findFirstVisibleItemPosition()) == 0 || p == 1)) {
                        this.viewPages[a3].layoutManager.scrollToPositionWithOffset(1, 0);
                    }
                    this.viewPages[a3].listView.setScrollingTouchSlop(0);
                    this.viewPages[a3].listView.requestLayout();
                    this.viewPages[a3].requestLayout();
                    a3++;
                }
            }
            if (this.parentLayout != null) {
                this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(true);
            }
        }
        updateCounters(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    protected void onPanTranslationUpdate(float y) {
        if (this.viewPages == null) {
            return;
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null && chatActivityEnterView.isPopupShowing()) {
            this.fragmentView.setTranslationY(y);
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].setTranslationY(0.0f);
                a++;
            }
            if (!this.onlySelect) {
                this.actionBar.setTranslationY(0.0f);
            }
            this.searchViewPager.setTranslationY(0.0f);
            return;
        }
        int a2 = 0;
        while (true) {
            ViewPage[] viewPageArr2 = this.viewPages;
            if (a2 >= viewPageArr2.length) {
                break;
            }
            viewPageArr2[a2].setTranslationY(y);
            a2++;
        }
        if (!this.onlySelect) {
            this.actionBar.setTranslationY(y);
        }
        this.searchViewPager.setTranslationY(y);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void finishFragment() {
        super.finishFragment();
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        boolean tosAccepted;
        View view;
        super.onResume();
        if (!this.parentLayout.isInPreviewMode() && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.getVisibility() == 0) {
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(this.viewPages[0].selectedType == this.filterTabsView.getFirstTabId() || this.searchIsShowed || SharedConfig.getChatSwipeAction(this.currentAccount) != 5);
        }
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].dialogsAdapter.notifyDataSetChanged();
                a++;
            }
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        if (!this.onlySelect && this.folderId == 0) {
            getMediaDataController().checkStickers(4);
        }
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager != null) {
            searchViewPager.onResume();
        }
        if (!this.afterSignup) {
            tosAccepted = getUserConfig().unacceptedTermsOfService == null;
        } else {
            tosAccepted = true;
        }
        if (tosAccepted && this.checkPermission && !this.onlySelect && Build.VERSION.SDK_INT >= 23) {
            final Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                final boolean hasNotContactsPermission = activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0;
                final boolean hasNotStoragePermission = (Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda40
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsActivity.this.m3362lambda$onResume$17$orgtelegramuiDialogsActivity(hasNotContactsPermission, hasNotStoragePermission, activity);
                    }
                }, (!this.afterSignup || !hasNotContactsPermission) ? 0L : 4000L);
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && Build.VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(XiaomiUtilities.OP_SHOW_WHEN_LOCKED)) {
            if (getParentActivity() == null || MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
                return;
            }
            showDialog(new AlertDialog.Builder(getParentActivity()).setTopAnimation(R.raw.permission_request_apk, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", R.string.PermissionXiaomiLockscreen)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda57
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.m3363lambda$onResume$18$orgtelegramuiDialogsActivity(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), DialogsActivity$$ExternalSyntheticLambda5.INSTANCE).create());
        }
        showFiltersHint();
        if (this.viewPages != null) {
            int a2 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (a2 >= viewPageArr2.length) {
                    break;
                }
                if (viewPageArr2[a2].dialogsType == 0 && this.viewPages[a2].archivePullViewState == 2 && this.viewPages[a2].layoutManager.findFirstVisibleItemPosition() == 0 && hasHiddenArchive()) {
                    this.viewPages[a2].layoutManager.scrollToPositionWithOffset(1, 0);
                }
                if (a2 == 0) {
                    this.viewPages[a2].dialogsAdapter.resume();
                } else {
                    this.viewPages[a2].dialogsAdapter.pause();
                }
                a2++;
            }
        }
        showNextSupportedSuggestion();
        Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.DialogsActivity.31
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ int getBottomOffset(int i) {
                return Bulletin.Delegate.CC.$default$getBottomOffset(this, i);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public void onOffsetChange(float offset) {
                if (DialogsActivity.this.undoView[0] == null || DialogsActivity.this.undoView[0].getVisibility() != 0) {
                    DialogsActivity.this.additionalFloatingTranslation = offset;
                    if (DialogsActivity.this.additionalFloatingTranslation < 0.0f) {
                        DialogsActivity.this.additionalFloatingTranslation = 0.0f;
                    }
                    if (!DialogsActivity.this.floatingHidden) {
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                }
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public void onShow(Bulletin bulletin) {
                if (DialogsActivity.this.undoView[0] != null && DialogsActivity.this.undoView[0].getVisibility() == 0) {
                    DialogsActivity.this.undoView[0].hide(true, 2);
                }
            }
        });
        if (this.searchIsShowed) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        }
        updateVisibleRows(0, false);
        updateProxyButton(false, true);
        checkSuggestClearDatabase();
    }

    /* renamed from: lambda$onResume$17$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3362lambda$onResume$17$orgtelegramuiDialogsActivity(boolean hasNotContactsPermission, boolean hasNotStoragePermission, Activity activity) {
        this.afterSignup = false;
        if (hasNotContactsPermission || hasNotStoragePermission) {
            this.askingForPermissions = true;
            if (hasNotContactsPermission && this.askAboutContacts && getUserConfig().syncContacts && activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                AlertDialog.Builder builder = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda45
                    @Override // org.telegram.messenger.MessagesStorage.IntCallback
                    public final void run(int i) {
                        DialogsActivity.this.m3361lambda$onResume$16$orgtelegramuiDialogsActivity(i);
                    }
                });
                AlertDialog create = builder.create();
                this.permissionDialog = create;
                showDialog(create);
            } else if (hasNotStoragePermission && activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                if (activity instanceof BasePermissionsActivity) {
                    BasePermissionsActivity basePermissionsActivity = (BasePermissionsActivity) activity;
                    AlertDialog createPermissionErrorAlert = basePermissionsActivity.createPermissionErrorAlert(R.raw.permission_request_folder, LocaleController.getString((int) R.string.PermissionStorageWithHint));
                    this.permissionDialog = createPermissionErrorAlert;
                    showDialog(createPermissionErrorAlert);
                }
            } else {
                askForPermissons(true);
            }
        }
    }

    /* renamed from: lambda$onResume$16$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3361lambda$onResume$16$orgtelegramuiDialogsActivity(int param) {
        this.askAboutContacts = param != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).apply();
        askForPermissons(false);
    }

    /* renamed from: lambda$onResume$18$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3363lambda$onResume$18$orgtelegramuiDialogsActivity(DialogInterface dialog, int which) {
        Intent intent = XiaomiUtilities.getPermissionManagerIntent();
        if (intent != null) {
            try {
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent2 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent2.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                    getParentActivity().startActivity(intent2);
                } catch (Exception xx) {
                    FileLog.e(xx);
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean presentFragment(BaseFragment fragment) {
        boolean b = super.presentFragment(fragment);
        if (b && this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].dialogsAdapter.pause();
                a++;
            }
        }
        return b;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onResume();
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
        Bulletin.removeDelegate(this);
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a < viewPageArr.length) {
                    viewPageArr[a].dialogsAdapter.pause();
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            return false;
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.isEditing()) {
            this.filterTabsView.setIsEditing(false);
            showDoneItem(false);
            return false;
        } else if (this.actionBar != null && this.actionBar.isActionModeShowed()) {
            if (this.searchViewPager.getVisibility() == 0) {
                this.searchViewPager.hideActionMode();
                hideActionMode(true);
            } else {
                hideActionMode(true);
            }
            return false;
        } else {
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0 && !this.tabsAnimationInProgress && !this.filterTabsView.isAnimatingIndicator() && !this.startedTracking && !this.filterTabsView.isFirstTabSelected()) {
                this.filterTabsView.selectFirstTab();
                return false;
            }
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView != null && chatActivityEnterView.isPopupShowing()) {
                this.commentView.hidePopup(true);
                return false;
            }
            return super.onBackPressed();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        if (this.closeSearchFieldOnHide) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            if (this.searchObject != null) {
                this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.getVisibility() == 0 && this.filterTabsViewIsVisible) {
            int scrollY = (int) (-this.actionBar.getTranslationY());
            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (scrollY != 0 && scrollY != actionBarHeight) {
                if (scrollY >= actionBarHeight / 2) {
                    if (this.viewPages[0].listView.canScrollVertically(1)) {
                        setScrollY(-actionBarHeight);
                    }
                } else {
                    setScrollY(0.0f);
                }
            }
        }
        UndoView[] undoViewArr = this.undoView;
        if (undoViewArr[0] != null) {
            undoViewArr[0].hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void setInPreviewMode(boolean value) {
        super.setInPreviewMode(value);
        if (!value && this.avatarContainer != null) {
            this.actionBar.setBackground(null);
            ((ViewGroup.MarginLayoutParams) this.actionBar.getLayoutParams()).topMargin = 0;
            this.actionBar.removeView(this.avatarContainer);
            this.avatarContainer = null;
            updateFilterTabs(false, false);
            this.floatingButton.setVisibility(0);
            ContentView contentView = (ContentView) this.fragmentView;
            FragmentContextView fragmentContextView = this.fragmentContextView;
            if (fragmentContextView != null) {
                contentView.addView(fragmentContextView);
            }
            FragmentContextView fragmentContextView2 = this.fragmentLocationContextView;
            if (fragmentContextView2 != null) {
                contentView.addView(fragmentContextView2);
            }
        }
    }

    public boolean addOrRemoveSelectedDialog(long did, View cell) {
        if (this.selectedDialogs.contains(Long.valueOf(did))) {
            this.selectedDialogs.remove(Long.valueOf(did));
            if (cell instanceof DialogCell) {
                ((DialogCell) cell).setChecked(false, true);
            } else if (cell instanceof ProfileSearchCell) {
                ((ProfileSearchCell) cell).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(did));
        if (cell instanceof DialogCell) {
            ((DialogCell) cell).setChecked(true, true);
        } else if (cell instanceof ProfileSearchCell) {
            ((ProfileSearchCell) cell).setChecked(true, true);
        }
        return true;
    }

    public void search(String query, boolean animated) {
        showSearch(true, false, animated);
        this.actionBar.openSearchField(query, false);
    }

    public void showSearch(final boolean show, boolean startFromDownloads, boolean animated) {
        boolean animated2;
        FilterTabsView filterTabsView;
        boolean onlyDialogsAdapter;
        int i = this.initialDialogsType;
        if (i != 0 && i != 3) {
            animated2 = false;
        } else {
            animated2 = animated;
        }
        AnimatorSet animatorSet = this.searchAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimator = null;
        }
        Animator animator = this.tabsAlphaAnimator;
        if (animator != null) {
            animator.cancel();
            this.tabsAlphaAnimator = null;
        }
        this.searchIsShowed = show;
        ((SizeNotifierFrameLayout) this.fragmentView).invalidateBlur();
        boolean z = true;
        int i2 = 0;
        if (show) {
            if (this.searchFiltersWasShowed) {
                onlyDialogsAdapter = false;
            } else {
                onlyDialogsAdapter = onlyDialogsAdapter();
            }
            this.searchViewPager.showOnlyDialogsAdapter(onlyDialogsAdapter);
            boolean z2 = !onlyDialogsAdapter;
            this.whiteActionBar = z2;
            if (z2) {
                this.searchFiltersWasShowed = true;
            }
            ContentView contentView = (ContentView) this.fragmentView;
            ViewPagerFixed.TabsView tabsView = this.searchTabsView;
            if (tabsView == null && !onlyDialogsAdapter) {
                this.searchTabsView = this.searchViewPager.createTabsView();
                int filtersViewPosition = -1;
                if (this.filtersView != null) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= contentView.getChildCount()) {
                            break;
                        } else if (contentView.getChildAt(i3) != this.filtersView) {
                            i3++;
                        } else {
                            filtersViewPosition = i3;
                            break;
                        }
                    }
                }
                if (filtersViewPosition > 0) {
                    contentView.addView(this.searchTabsView, filtersViewPosition, LayoutHelper.createFrame(-1, 44.0f));
                } else {
                    contentView.addView(this.searchTabsView, LayoutHelper.createFrame(-1, 44.0f));
                }
            } else if (tabsView != null && onlyDialogsAdapter) {
                ViewParent parent = tabsView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.searchTabsView);
                }
                this.searchTabsView = null;
            }
            EditTextBoldCursor editText = this.searchItem.getSearchField();
            if (this.whiteActionBar) {
                editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                editText.setHintTextColor(Theme.getColor(Theme.key_player_time));
                editText.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor));
            } else {
                editText.setCursorColor(Theme.getColor(Theme.key_actionBarDefaultSearch));
                editText.setHintTextColor(Theme.getColor(Theme.key_actionBarDefaultSearchPlaceholder));
                editText.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSearch));
            }
            this.searchViewPager.setKeyboardHeight(((ContentView) this.fragmentView).getKeyboardHeight());
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(true);
            this.searchViewPager.clear();
            if (this.folderId != 0) {
                FiltersView.MediaFilterData filterData = new FiltersView.MediaFilterData(R.drawable.chats_archive, LocaleController.getString("ArchiveSearchFilter", R.string.ArchiveSearchFilter), null, 7);
                addSearchFilter(filterData);
            }
        } else if (this.filterTabsView != null && this.parentLayout != null) {
            this.parentLayout.getDrawerLayoutContainer().setAllowOpenDrawerBySwipe(this.viewPages[0].selectedType == this.filterTabsView.getFirstTabId() || SharedConfig.getChatSwipeAction(this.currentAccount) != 5);
        }
        if (!animated2 || !this.searchViewPager.dialogsSearchAdapter.hasRecentSearch()) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
        if (!show && (filterTabsView = this.filterTabsView) != null && this.canShowFilterTabsView) {
            filterTabsView.setVisibility(0);
        }
        float f = 0.9f;
        float f2 = 0.0f;
        if (animated2) {
            if (!show) {
                this.viewPages[0].listView.setVisibility(0);
                this.viewPages[0].setVisibility(0);
            } else {
                this.searchViewPager.setVisibility(0);
                this.searchViewPager.reset();
                updateFiltersView(true, null, null, false, false);
                ViewPagerFixed.TabsView tabsView2 = this.searchTabsView;
                if (tabsView2 != null) {
                    tabsView2.hide(false, false);
                    this.searchTabsView.setVisibility(0);
                }
            }
            setDialogsListFrozen(true);
            this.viewPages[0].listView.setVerticalScrollBarEnabled(false);
            this.searchViewPager.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.searchAnimator = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            ViewPage viewPage = this.viewPages[0];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(viewPage, property, fArr));
            ViewPage viewPage2 = this.viewPages[0];
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 0.9f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(viewPage2, property2, fArr2));
            ViewPage viewPage3 = this.viewPages[0];
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (!show) {
                f = 1.0f;
            }
            fArr3[0] = f;
            animators.add(ObjectAnimator.ofFloat(viewPage3, property3, fArr3));
            SearchViewPager searchViewPager = this.searchViewPager;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(searchViewPager, property4, fArr4));
            SearchViewPager searchViewPager2 = this.searchViewPager;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            float f3 = 1.05f;
            fArr5[0] = show ? 1.0f : 1.05f;
            animators.add(ObjectAnimator.ofFloat(searchViewPager2, property5, fArr5));
            SearchViewPager searchViewPager3 = this.searchViewPager;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (show) {
                f3 = 1.0f;
            }
            fArr6[0] = f3;
            animators.add(ObjectAnimator.ofFloat(searchViewPager3, property6, fArr6));
            ActionBarMenuItem actionBarMenuItem = this.passcodeItem;
            if (actionBarMenuItem != null) {
                RLottieImageView iconView = actionBarMenuItem.getIconView();
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                fArr7[0] = show ? 0.0f : 1.0f;
                animators.add(ObjectAnimator.ofFloat(iconView, property7, fArr7));
            }
            ActionBarMenuItem actionBarMenuItem2 = this.downloadsItem;
            if (actionBarMenuItem2 != null) {
                if (show) {
                    actionBarMenuItem2.setAlpha(0.0f);
                } else {
                    animators.add(ObjectAnimator.ofFloat(actionBarMenuItem2, View.ALPHA, 1.0f));
                }
                updateProxyButton(false, false);
            }
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
                RecyclerListView tabsContainer = this.filterTabsView.getTabsContainer();
                Property property8 = View.ALPHA;
                float[] fArr8 = new float[1];
                fArr8[0] = show ? 0.0f : 1.0f;
                ObjectAnimator duration = ObjectAnimator.ofFloat(tabsContainer, property8, fArr8).setDuration(100L);
                this.tabsAlphaAnimator = duration;
                duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.32
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        DialogsActivity.this.tabsAlphaAnimator = null;
                    }
                });
            }
            float[] fArr9 = new float[2];
            fArr9[0] = this.searchAnimationProgress;
            if (show) {
                f2 = 1.0f;
            }
            fArr9[1] = f2;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr9);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda54
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DialogsActivity.this.m3383lambda$showSearch$20$orgtelegramuiDialogsActivity(valueAnimator2);
                }
            });
            animators.add(valueAnimator);
            this.searchAnimator.playTogether(animators);
            this.searchAnimator.setDuration(show ? 200L : 180L);
            this.searchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            if (this.filterTabsViewIsVisible) {
                int backgroundColor1 = Theme.getColor(this.folderId == 0 ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived);
                int backgroundColor2 = Theme.getColor(Theme.key_windowBackgroundWhite);
                int sum = Math.abs(Color.red(backgroundColor1) - Color.red(backgroundColor2)) + Math.abs(Color.green(backgroundColor1) - Color.green(backgroundColor2)) + Math.abs(Color.blue(backgroundColor1) - Color.blue(backgroundColor2));
                if (sum / 255.0f <= 0.3f) {
                    z = false;
                }
                this.searchAnimationTabsDelayedCrossfade = z;
            } else {
                this.searchAnimationTabsDelayedCrossfade = true;
            }
            if (!show) {
                this.searchAnimator.setStartDelay(20L);
                Animator animator2 = this.tabsAlphaAnimator;
                if (animator2 != null) {
                    if (this.searchAnimationTabsDelayedCrossfade) {
                        animator2.setStartDelay(80L);
                        this.tabsAlphaAnimator.setDuration(100L);
                    } else {
                        animator2.setDuration(show ? 200L : 180L);
                    }
                }
            }
            this.searchAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.33
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    if (DialogsActivity.this.searchAnimator == animation) {
                        DialogsActivity.this.setDialogsListFrozen(false);
                        if (!show) {
                            DialogsActivity.this.searchItem.collapseSearchFilters();
                            DialogsActivity.this.whiteActionBar = false;
                            DialogsActivity.this.searchViewPager.setVisibility(8);
                            if (DialogsActivity.this.searchTabsView != null) {
                                DialogsActivity.this.searchTabsView.setVisibility(8);
                            }
                            DialogsActivity.this.searchItem.clearSearchFilters();
                            DialogsActivity.this.searchViewPager.clear();
                            DialogsActivity.this.filtersView.setVisibility(8);
                            DialogsActivity.this.viewPages[0].listView.show();
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.hideFloatingButton(false);
                            }
                            DialogsActivity.this.searchWasFullyShowed = false;
                        } else {
                            DialogsActivity.this.viewPages[0].listView.hide();
                            if (DialogsActivity.this.filterTabsView != null) {
                                DialogsActivity.this.filterTabsView.setVisibility(8);
                            }
                            DialogsActivity.this.searchWasFullyShowed = true;
                            AndroidUtilities.requestAdjustResize(DialogsActivity.this.getParentActivity(), DialogsActivity.this.classGuid);
                            DialogsActivity.this.searchItem.setVisibility(8);
                        }
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.requestLayout();
                        }
                        float f4 = 1.0f;
                        DialogsActivity.this.setSearchAnimationProgress(show ? 1.0f : 0.0f);
                        DialogsActivity.this.viewPages[0].listView.setVerticalScrollBarEnabled(true);
                        DialogsActivity.this.searchViewPager.setBackground(null);
                        DialogsActivity.this.searchAnimator = null;
                        if (DialogsActivity.this.downloadsItem != null) {
                            ActionBarMenuItem actionBarMenuItem3 = DialogsActivity.this.downloadsItem;
                            if (show) {
                                f4 = 0.0f;
                            }
                            actionBarMenuItem3.setAlpha(f4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    if (DialogsActivity.this.searchAnimator == animation) {
                        if (show) {
                            DialogsActivity.this.viewPages[0].listView.hide();
                        } else {
                            DialogsActivity.this.viewPages[0].listView.show();
                        }
                        DialogsActivity.this.searchAnimator = null;
                    }
                }
            });
            this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
            this.searchAnimator.start();
            Animator animator3 = this.tabsAlphaAnimator;
            if (animator3 != null) {
                animator3.start();
            }
        } else {
            setDialogsListFrozen(false);
            if (show) {
                this.viewPages[0].listView.hide();
            } else {
                this.viewPages[0].listView.show();
            }
            this.viewPages[0].setAlpha(show ? 0.0f : 1.0f);
            this.viewPages[0].setScaleX(show ? 0.9f : 1.0f);
            ViewPage viewPage4 = this.viewPages[0];
            if (!show) {
                f = 1.0f;
            }
            viewPage4.setScaleY(f);
            this.searchViewPager.setAlpha(show ? 1.0f : 0.0f);
            this.filtersView.setAlpha(show ? 1.0f : 0.0f);
            float f4 = 1.1f;
            this.searchViewPager.setScaleX(show ? 1.0f : 1.1f);
            SearchViewPager searchViewPager4 = this.searchViewPager;
            if (show) {
                f4 = 1.0f;
            }
            searchViewPager4.setScaleY(f4);
            FilterTabsView filterTabsView3 = this.filterTabsView;
            if (filterTabsView3 != null && filterTabsView3.getVisibility() == 0) {
                this.filterTabsView.setTranslationY(show ? -AndroidUtilities.dp(44.0f) : 0.0f);
                this.filterTabsView.getTabsContainer().setAlpha(show ? 0.0f : 1.0f);
            }
            FilterTabsView filterTabsView4 = this.filterTabsView;
            if (filterTabsView4 != null) {
                if (this.canShowFilterTabsView && !show) {
                    filterTabsView4.setVisibility(0);
                } else {
                    filterTabsView4.setVisibility(8);
                }
            }
            SearchViewPager searchViewPager5 = this.searchViewPager;
            if (!show) {
                i2 = 8;
            }
            searchViewPager5.setVisibility(i2);
            setSearchAnimationProgress(show ? 1.0f : 0.0f);
            this.fragmentView.invalidate();
            ActionBarMenuItem actionBarMenuItem3 = this.downloadsItem;
            if (actionBarMenuItem3 != null) {
                if (!show) {
                    f2 = 1.0f;
                }
                actionBarMenuItem3.setAlpha(f2);
            }
        }
        int i4 = this.initialSearchType;
        if (i4 >= 0) {
            SearchViewPager searchViewPager6 = this.searchViewPager;
            searchViewPager6.setPosition(searchViewPager6.getPositionForType(i4));
        }
        if (!show) {
            this.initialSearchType = -1;
        }
        if (show && startFromDownloads) {
            this.searchViewPager.showDownloads();
        }
    }

    /* renamed from: lambda$showSearch$20$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3383lambda$showSearch$20$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator1) {
        setSearchAnimationProgress(((Float) valueAnimator1.getAnimatedValue()).floatValue());
    }

    public boolean onlyDialogsAdapter() {
        int dialogsCount = getMessagesController().getTotalDialogsCount();
        return this.onlySelect || !this.searchViewPager.dialogsSearchAdapter.hasRecentSearch() || dialogsCount <= 10;
    }

    private void updateFilterTabsVisibility(boolean animated) {
        if (this.isPaused || this.databaseMigrationHint != null) {
            animated = false;
        }
        float f = 1.0f;
        if (this.searchIsShowed) {
            ValueAnimator valueAnimator = this.filtersTabAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z = this.canShowFilterTabsView;
            this.filterTabsViewIsVisible = z;
            if (!z) {
                f = 0.0f;
            }
            this.filterTabsProgress = f;
            return;
        }
        final boolean visible = this.canShowFilterTabsView;
        if (this.filterTabsViewIsVisible != visible) {
            ValueAnimator valueAnimator2 = this.filtersTabAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            this.filterTabsViewIsVisible = visible;
            int i = 0;
            if (animated) {
                if (visible) {
                    if (this.filterTabsView.getVisibility() != 0) {
                        this.filterTabsView.setVisibility(0);
                    }
                    this.filtersTabAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    this.filterTabsMoveFrom = AndroidUtilities.dp(44.0f);
                } else {
                    this.filtersTabAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
                    this.filterTabsMoveFrom = Math.max(0.0f, AndroidUtilities.dp(44.0f) + this.actionBar.getTranslationY());
                }
                final float animateFromScrollY = this.actionBar.getTranslationY();
                this.filtersTabAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.34
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        DialogsActivity.this.filtersTabAnimator = null;
                        DialogsActivity.this.scrollAdditionalOffset = AndroidUtilities.dp(44.0f) - DialogsActivity.this.filterTabsMoveFrom;
                        if (!visible) {
                            DialogsActivity.this.filterTabsView.setVisibility(8);
                        }
                        if (DialogsActivity.this.fragmentView != null) {
                            DialogsActivity.this.fragmentView.requestLayout();
                        }
                        DialogsActivity.this.getNotificationCenter().onAnimationFinish(DialogsActivity.this.animationIndex);
                    }
                });
                this.filtersTabAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda55
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                        DialogsActivity.this.m3386xa989b9b5(visible, animateFromScrollY, valueAnimator3);
                    }
                });
                this.filtersTabAnimator.setDuration(220L);
                this.filtersTabAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
                this.filtersTabAnimator.start();
                this.fragmentView.requestLayout();
                return;
            }
            if (!visible) {
                f = 0.0f;
            }
            this.filterTabsProgress = f;
            FilterTabsView filterTabsView = this.filterTabsView;
            if (!visible) {
                i = 8;
            }
            filterTabsView.setVisibility(i);
            if (this.fragmentView != null) {
                this.fragmentView.invalidate();
            }
        }
    }

    /* renamed from: lambda$updateFilterTabsVisibility$21$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3386xa989b9b5(boolean visible, float animateFromScrollY, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.filterTabsProgress = floatValue;
        if (!visible) {
            setScrollY(floatValue * animateFromScrollY);
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    public void setSearchAnimationProgress(float progress) {
        this.searchAnimationProgress = progress;
        if (this.whiteActionBar) {
            int color1 = Theme.getColor(this.folderId != 0 ? Theme.key_actionBarDefaultArchivedIcon : Theme.key_actionBarDefaultIcon);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(color1, Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), this.searchAnimationProgress), false);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), this.searchAnimationProgress), true);
            int color12 = Theme.getColor(this.folderId != 0 ? Theme.key_actionBarDefaultArchivedSelector : Theme.key_actionBarDefaultSelector);
            int color2 = Theme.getColor(Theme.key_actionBarActionModeDefaultSelector);
            this.actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(color12, color2, this.searchAnimationProgress), false);
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
        updateContextViewPosition();
    }

    public void findAndUpdateCheckBox(long dialogId, boolean checked) {
        if (this.viewPages == null) {
            return;
        }
        int b = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (b < viewPageArr.length) {
                int count = viewPageArr[b].listView.getChildCount();
                int a = 0;
                while (true) {
                    if (a < count) {
                        View child = this.viewPages[b].listView.getChildAt(a);
                        if (child instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) child;
                            if (dialogCell.getDialogId() == dialogId) {
                                dialogCell.setChecked(checked, true);
                                break;
                            }
                        }
                        a++;
                    }
                }
                b++;
            } else {
                return;
            }
        }
    }

    public void checkListLoad(ViewPage viewPage) {
        boolean loadArchivedFromCache;
        boolean loadArchived;
        boolean loadFromCache;
        boolean load;
        if (!this.tabsAnimationInProgress && !this.startedTracking) {
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView == null || filterTabsView.getVisibility() != 0 || !this.filterTabsView.isAnimatingIndicator()) {
                int firstVisibleItem = viewPage.layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = viewPage.layoutManager.findLastVisibleItemPosition();
                int visibleItemCount = Math.abs(viewPage.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (lastVisibleItem != -1) {
                    RecyclerView.ViewHolder holder = viewPage.listView.findViewHolderForAdapterPosition(lastVisibleItem);
                    boolean z = holder != null && holder.getItemViewType() == 11;
                    this.floatingForceVisible = z;
                    if (z) {
                        hideFloatingButton(false);
                    }
                } else {
                    this.floatingForceVisible = false;
                }
                if (viewPage.dialogsType == 7 || viewPage.dialogsType == 8) {
                    ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().dialogFilters;
                    if (viewPage.selectedType >= 0 && viewPage.selectedType < dialogFilters.size()) {
                        MessagesController.DialogFilter filter = getMessagesController().dialogFilters.get(viewPage.selectedType);
                        if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) == 0 && ((visibleItemCount > 0 && lastVisibleItem >= getDialogsArray(this.currentAccount, viewPage.dialogsType, 1, this.dialogsListFrozen).size() - 10) || (visibleItemCount == 0 && !getMessagesController().isDialogsEndReached(1)))) {
                            boolean loadArchivedFromCache2 = !getMessagesController().isDialogsEndReached(1);
                            if (!loadArchivedFromCache2 && getMessagesController().isServerDialogsEndReached(1)) {
                                loadArchived = false;
                                loadArchivedFromCache = loadArchivedFromCache2;
                            } else {
                                loadArchived = true;
                                loadArchivedFromCache = loadArchivedFromCache2;
                            }
                            if ((visibleItemCount <= 0 && lastVisibleItem >= getDialogsArray(this.currentAccount, viewPage.dialogsType, this.folderId, this.dialogsListFrozen).size() - 10) || (visibleItemCount == 0 && ((viewPage.dialogsType == 7 || viewPage.dialogsType == 8) && !getMessagesController().isDialogsEndReached(this.folderId)))) {
                                boolean loadFromCache2 = !getMessagesController().isDialogsEndReached(this.folderId);
                                if (!loadFromCache2 && getMessagesController().isServerDialogsEndReached(this.folderId)) {
                                    load = false;
                                    loadFromCache = loadFromCache2;
                                } else {
                                    load = true;
                                    loadFromCache = loadFromCache2;
                                }
                            } else {
                                load = false;
                                loadFromCache = false;
                            }
                            if (!load || loadArchived) {
                                final boolean loadFinal = load;
                                final boolean loadFromCacheFinal = loadFromCache;
                                final boolean loadArchivedFinal = loadArchived;
                                final boolean loadArchivedFromCacheFinal = loadArchivedFromCache;
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda41
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        DialogsActivity.this.m3335lambda$checkListLoad$22$orgtelegramuiDialogsActivity(loadFinal, loadFromCacheFinal, loadArchivedFinal, loadArchivedFromCacheFinal);
                                    }
                                });
                            }
                            return;
                        }
                    }
                }
                loadArchived = false;
                loadArchivedFromCache = false;
                if (visibleItemCount <= 0) {
                }
                load = false;
                loadFromCache = false;
                if (!load) {
                }
                final boolean loadFinal2 = load;
                final boolean loadFromCacheFinal2 = loadFromCache;
                final boolean loadArchivedFinal2 = loadArchived;
                final boolean loadArchivedFromCacheFinal2 = loadArchivedFromCache;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda41
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsActivity.this.m3335lambda$checkListLoad$22$orgtelegramuiDialogsActivity(loadFinal2, loadFromCacheFinal2, loadArchivedFinal2, loadArchivedFromCacheFinal2);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$checkListLoad$22$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3335lambda$checkListLoad$22$orgtelegramuiDialogsActivity(boolean loadFinal, boolean loadFromCacheFinal, boolean loadArchivedFinal, boolean loadArchivedFromCacheFinal) {
        if (loadFinal) {
            getMessagesController().loadDialogs(this.folderId, -1, 100, loadFromCacheFinal);
        }
        if (loadArchivedFinal) {
            getMessagesController().loadDialogs(1, -1, 100, loadArchivedFromCacheFinal);
        }
    }

    private void onItemClick(View view, int position, RecyclerView.Adapter adapter) {
        long dialogId;
        TLRPC.Document sticker;
        long did;
        long did2;
        long dialogId2;
        int filterId;
        String hash;
        TLRPC.ChatInvite invite;
        if (getParentActivity() == null) {
            return;
        }
        int message_id = 0;
        boolean isGlobalSearch = false;
        int folderId = 0;
        int filterId2 = 0;
        if (!(adapter instanceof DialogsAdapter)) {
            if (adapter != this.searchViewPager.dialogsSearchAdapter) {
                dialogId = 0;
            } else {
                Object obj = this.searchViewPager.dialogsSearchAdapter.getItem(position);
                isGlobalSearch = this.searchViewPager.dialogsSearchAdapter.isGlobalSearch(position);
                if (obj instanceof TLRPC.User) {
                    dialogId2 = ((TLRPC.User) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId2;
                        this.searchObject = (TLRPC.User) obj;
                    }
                } else if (obj instanceof TLRPC.Chat) {
                    dialogId2 = -((TLRPC.Chat) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId2;
                        this.searchObject = (TLRPC.Chat) obj;
                    }
                } else if (obj instanceof TLRPC.EncryptedChat) {
                    dialogId2 = DialogObject.makeEncryptedDialogId(((TLRPC.EncryptedChat) obj).id);
                    if (!this.onlySelect) {
                        this.searchDialogId = dialogId2;
                        this.searchObject = (TLRPC.EncryptedChat) obj;
                    }
                } else {
                    if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialogId = messageObject.getDialogId();
                        int message_id2 = messageObject.getId();
                        this.searchViewPager.dialogsSearchAdapter.addHashtagsFromMessage(this.searchViewPager.dialogsSearchAdapter.getLastSearchString());
                        message_id = message_id2;
                    } else {
                        if (obj instanceof String) {
                            String str = (String) obj;
                            if (this.searchViewPager.dialogsSearchAdapter.isHashtagSearch()) {
                                this.actionBar.openSearchField(str, false);
                            } else if (!str.equals("section")) {
                                NewContactActivity activity = new NewContactActivity();
                                activity.setInitialPhoneNumber(str, true);
                                presentFragment(activity);
                            }
                        }
                        dialogId = 0;
                    }
                    if (dialogId != 0 && this.actionBar.isActionModeShowed()) {
                        if (!this.actionBar.isActionModeShowed("search_dialogs_action_mode") && message_id == 0 && !isGlobalSearch) {
                            showOrUpdateActionMode(dialogId, view);
                            return;
                        }
                        return;
                    }
                }
                dialogId = dialogId2;
                if (dialogId != 0) {
                    if (!this.actionBar.isActionModeShowed("search_dialogs_action_mode")) {
                        return;
                    }
                    return;
                }
            }
        } else {
            DialogsAdapter dialogsAdapter = (DialogsAdapter) adapter;
            int dialogsType = dialogsAdapter.getDialogsType();
            if (dialogsType != 7 && dialogsType != 8) {
                filterId = 0;
            } else {
                MessagesController.DialogFilter dialogFilter = getMessagesController().selectedDialogFilter[dialogsType == 7 ? (char) 0 : (char) 1];
                filterId = dialogFilter.id;
            }
            TLObject object = dialogsAdapter.getItem(position);
            if (object instanceof TLRPC.User) {
                dialogId = ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Dialog) {
                TLRPC.Dialog dialog = (TLRPC.Dialog) object;
                int folderId2 = dialog.folder_id;
                if (dialog instanceof TLRPC.TL_dialogFolder) {
                    if (this.actionBar.isActionModeShowed(null)) {
                        return;
                    }
                    TLRPC.TL_dialogFolder dialogFolder = (TLRPC.TL_dialogFolder) dialog;
                    Bundle args = new Bundle();
                    args.putInt("folderId", dialogFolder.folder.id);
                    presentFragment(new DialogsActivity(args));
                    return;
                }
                dialogId = dialog.id;
                if (this.actionBar.isActionModeShowed(null)) {
                    showOrUpdateActionMode(dialogId, view);
                    return;
                }
                folderId = folderId2;
            } else if (object instanceof TLRPC.TL_recentMeUrlChat) {
                dialogId = -((TLRPC.TL_recentMeUrlChat) object).chat_id;
            } else if (object instanceof TLRPC.TL_recentMeUrlUser) {
                dialogId = ((TLRPC.TL_recentMeUrlUser) object).user_id;
            } else if (!(object instanceof TLRPC.TL_recentMeUrlChatInvite)) {
                if (object instanceof TLRPC.TL_recentMeUrlStickerSet) {
                    TLRPC.StickerSet stickerSet = ((TLRPC.TL_recentMeUrlStickerSet) object).set.set;
                    TLRPC.TL_inputStickerSetID set = new TLRPC.TL_inputStickerSetID();
                    set.id = stickerSet.id;
                    set.access_hash = stickerSet.access_hash;
                    showDialog(new StickersAlert(getParentActivity(), this, set, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                    return;
                }
                boolean z = object instanceof TLRPC.TL_recentMeUrlUnknown;
                return;
            } else {
                TLRPC.TL_recentMeUrlChatInvite chatInvite = (TLRPC.TL_recentMeUrlChatInvite) object;
                TLRPC.ChatInvite invite2 = chatInvite.chat_invite;
                if (invite2.chat != null || (invite2.channel && !invite2.megagroup)) {
                    if (invite2.chat == null) {
                        invite = invite2;
                    } else if (ChatObject.isChannel(invite2.chat) && !invite2.chat.megagroup) {
                        invite = invite2;
                    }
                    TLRPC.ChatInvite invite3 = invite;
                    if (invite3.chat != null) {
                        dialogId = -invite3.chat.id;
                    } else {
                        return;
                    }
                }
                String hash2 = chatInvite.url;
                int index = hash2.indexOf(47);
                if (index <= 0) {
                    hash = hash2;
                } else {
                    hash = hash2.substring(index + 1);
                }
                showDialog(new JoinGroupAlert(getParentActivity(), invite2, hash, this, null));
                return;
            }
            filterId2 = filterId;
        }
        if (dialogId == 0) {
            return;
        }
        if (this.onlySelect) {
            if (!validateSlowModeDialog(dialogId)) {
                return;
            }
            if (this.selectedDialogs.isEmpty() && (this.initialDialogsType != 3 || this.selectAlertString == null)) {
                didSelectResult(dialogId, true, false);
                return;
            } else if (!this.selectedDialogs.contains(Long.valueOf(dialogId)) && !checkCanWrite(dialogId)) {
                return;
            } else {
                boolean checked = addOrRemoveSelectedDialog(dialogId, view);
                if (adapter == this.searchViewPager.dialogsSearchAdapter) {
                    this.actionBar.closeSearchField();
                    findAndUpdateCheckBox(dialogId, checked);
                }
                updateSelectedCount();
                return;
            }
        }
        Bundle args2 = new Bundle();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            args2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
        } else if (DialogObject.isUserDialog(dialogId)) {
            args2.putLong("user_id", dialogId);
        } else {
            long did3 = dialogId;
            if (message_id == 0) {
                did2 = did3;
            } else {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-did3));
                if (chat == null || chat.migrated_to == null) {
                    did2 = did3;
                } else {
                    args2.putLong("migrated_to", did3);
                    did = -chat.migrated_to.channel_id;
                    args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
                }
            }
            did = did2;
            args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
        }
        if (message_id != 0) {
            args2.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, message_id);
        } else if (!isGlobalSearch) {
            closeSearch();
        } else if (this.searchObject != null) {
            this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
            this.searchObject = null;
        }
        args2.putInt("dialog_folder_id", folderId);
        args2.putInt("dialog_filter_id", filterId2);
        if (AndroidUtilities.isTablet()) {
            if (this.openedDialogId == dialogId && adapter != this.searchViewPager.dialogsSearchAdapter) {
                return;
            }
            if (this.viewPages != null) {
                int a = 0;
                while (true) {
                    ViewPage[] viewPageArr = this.viewPages;
                    if (a >= viewPageArr.length) {
                        break;
                    }
                    DialogsAdapter dialogsAdapter2 = viewPageArr[a].dialogsAdapter;
                    this.openedDialogId = dialogId;
                    dialogsAdapter2.setOpenedDialogId(dialogId);
                    a++;
                }
            }
            int a2 = MessagesController.UPDATE_MASK_SELECT_DIALOG;
            updateVisibleRows(a2);
        }
        if (this.searchViewPager.actionModeShowing()) {
            this.searchViewPager.hideActionMode();
        }
        if (this.searchString != null) {
            if (getMessagesController().checkCanOpenChat(args2, this)) {
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2));
                return;
            }
            return;
        }
        this.slowedReloadAfterDialogClick = true;
        if (getMessagesController().checkCanOpenChat(args2, this)) {
            ChatActivity chatActivity = new ChatActivity(args2);
            if ((adapter instanceof DialogsAdapter) && DialogObject.isUserDialog(dialogId) && getMessagesController().dialogs_dict.get(dialogId) == null && (sticker = getMediaDataController().getGreetingsSticker()) != null) {
                chatActivity.setPreloadedSticker(sticker, true);
            }
            presentFragment(chatActivity);
        }
    }

    public boolean onItemLongClick(RecyclerListView listView, View view, int position, float x, float y, int dialogsType, RecyclerView.Adapter adapter) {
        TLRPC.Dialog dialog;
        final long did;
        if (getParentActivity() == null) {
            return false;
        }
        if (!this.actionBar.isActionModeShowed() && !AndroidUtilities.isTablet() && !this.onlySelect && (view instanceof DialogCell)) {
            DialogCell cell = (DialogCell) view;
            if (cell.isPointInsideAvatar(x, y)) {
                return showChatPreview(cell);
            }
        }
        if (adapter == this.searchViewPager.dialogsSearchAdapter) {
            Object item = this.searchViewPager.dialogsSearchAdapter.getItem(position);
            if (!this.searchViewPager.dialogsSearchAdapter.isSearchWas()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchSingleAlertTitle", R.string.ClearSearchSingleAlertTitle));
                if (item instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) item;
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", R.string.ClearSearchSingleChatAlertText, chat.title));
                    did = -chat.id;
                } else if (item instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) item;
                    if (user.id == getUserConfig().clientUserId) {
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleChatAlertText", R.string.ClearSearchSingleChatAlertText, LocaleController.getString("SavedMessages", R.string.SavedMessages)));
                    } else {
                        builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", R.string.ClearSearchSingleUserAlertText, ContactsController.formatName(user.first_name, user.last_name)));
                    }
                    did = user.id;
                } else if (!(item instanceof TLRPC.EncryptedChat)) {
                    return false;
                } else {
                    TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) item;
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(encryptedChat.user_id));
                    builder.setMessage(LocaleController.formatString("ClearSearchSingleUserAlertText", R.string.ClearSearchSingleUserAlertText, ContactsController.formatName(user2.first_name, user2.last_name)));
                    did = DialogObject.makeEncryptedDialogId(encryptedChat.id);
                }
                builder.setPositiveButton(LocaleController.getString("ClearSearchRemove", R.string.ClearSearchRemove).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.this.m3359lambda$onItemLongClick$23$orgtelegramuiDialogsActivity(did, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
                return true;
            }
        }
        if (adapter == this.searchViewPager.dialogsSearchAdapter) {
            if (this.onlySelect) {
                onItemClick(view, position, adapter);
                return false;
            }
            long dialogId = 0;
            if ((view instanceof ProfileSearchCell) && !this.searchViewPager.dialogsSearchAdapter.isGlobalSearch(position)) {
                dialogId = ((ProfileSearchCell) view).getDialogId();
            }
            if (dialogId == 0) {
                return false;
            }
            showOrUpdateActionMode(dialogId, view);
            return true;
        }
        DialogsAdapter dialogsAdapter = (DialogsAdapter) adapter;
        ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, dialogsType, this.folderId, this.dialogsListFrozen);
        int position2 = dialogsAdapter.fixPosition(position);
        if (position2 < 0 || position2 >= dialogs.size() || (dialog = dialogs.get(position2)) == null) {
            return false;
        }
        if (this.onlySelect) {
            int i = this.initialDialogsType;
            if ((i != 3 && i != 10) || !validateSlowModeDialog(dialog.id)) {
                return false;
            }
            addOrRemoveSelectedDialog(dialog.id, view);
            updateSelectedCount();
            return true;
        } else if (dialog instanceof TLRPC.TL_dialogFolder) {
            onArchiveLongPress(view);
            return false;
        } else if (this.actionBar.isActionModeShowed() && isDialogPinned(dialog)) {
            return false;
        } else {
            showOrUpdateActionMode(dialog.id, view);
            return true;
        }
    }

    /* renamed from: lambda$onItemLongClick$23$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3359lambda$onItemLongClick$23$orgtelegramuiDialogsActivity(long did, DialogInterface dialogInterface, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(did);
    }

    private void onArchiveLongPress(View view) {
        String str;
        int i;
        view.performHapticFeedback(0, 2);
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        boolean hasUnread = getMessagesStorage().getArchiveUnreadCount() != 0;
        int[] icons = new int[2];
        icons[0] = hasUnread ? R.drawable.msg_markread : 0;
        icons[1] = SharedConfig.archiveHidden ? R.drawable.chats_pin : R.drawable.chats_unpin;
        CharSequence[] items = new CharSequence[2];
        items[0] = hasUnread ? LocaleController.getString("MarkAllAsRead", R.string.MarkAllAsRead) : null;
        if (SharedConfig.archiveHidden) {
            i = R.string.PinInTheList;
            str = "PinInTheList";
        } else {
            i = R.string.HideAboveTheList;
            str = "HideAboveTheList";
        }
        items[1] = LocaleController.getString(str, i);
        builder.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda56
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                DialogsActivity.this.m3357lambda$onArchiveLongPress$24$orgtelegramuiDialogsActivity(dialogInterface, i2);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$onArchiveLongPress$24$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3357lambda$onArchiveLongPress$24$orgtelegramuiDialogsActivity(DialogInterface d, int which) {
        if (which == 0) {
            getMessagesStorage().readAllDialogs(1);
        } else if (which == 1 && this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a < viewPageArr.length) {
                    if (viewPageArr[a].dialogsType == 0 && this.viewPages[a].getVisibility() == 0) {
                        View child = this.viewPages[a].listView.getChildAt(0);
                        DialogCell dialogCell = null;
                        if ((child instanceof DialogCell) && ((DialogCell) child).isFolderCell()) {
                            dialogCell = (DialogCell) child;
                        }
                        this.viewPages[a].listView.toggleArchiveHidden(true, dialogCell);
                    }
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean showChatPreview(final DialogCell cell) {
        MessagesController.DialogFilter dialogFilter;
        ChatActivity[] chatActivity;
        int flags;
        ActionBarMenuSubItem markAsUnreadItem;
        boolean z;
        ChatActivity[] chatActivity2;
        boolean z2;
        int maxPinnedCount;
        TLRPC.Chat chat;
        if (cell.isDialogFolder()) {
            if (cell.getCurrentDialogFolderId() == 1) {
                onArchiveLongPress(cell);
            }
            return false;
        }
        final long dialogId = cell.getDialogId();
        Bundle args = new Bundle();
        int message_id = cell.getMessageId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            return false;
        }
        if (DialogObject.isUserDialog(dialogId)) {
            args.putLong("user_id", dialogId);
        } else {
            long did = dialogId;
            if (message_id != 0 && (chat = getMessagesController().getChat(Long.valueOf(-did))) != null && chat.migrated_to != null) {
                args.putLong("migrated_to", did);
                did = -chat.migrated_to.channel_id;
            }
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
        }
        if (message_id != 0) {
            args.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, message_id);
        }
        final ArrayList<Long> dialogIdArray = new ArrayList<>();
        dialogIdArray.add(Long.valueOf(dialogId));
        int flags2 = 2;
        ChatActivity[] chatActivity3 = new ChatActivity[1];
        ActionBarPopupWindow.ActionBarPopupWindowLayout[] previewMenu = {new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), R.drawable.popup_fixed_alert, getResourceProvider(), 2)};
        ActionBarMenuSubItem markAsUnreadItem2 = new ActionBarMenuSubItem(getParentActivity(), true, false);
        if (cell.getHasUnread()) {
            markAsUnreadItem2.setTextAndIcon(LocaleController.getString("MarkAsRead", R.string.MarkAsRead), R.drawable.msg_markread);
        } else {
            markAsUnreadItem2.setTextAndIcon(LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread), R.drawable.msg_markunread);
        }
        markAsUnreadItem2.setMinimumWidth(160);
        markAsUnreadItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda18
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DialogsActivity.this.m3375lambda$showChatPreview$25$orgtelegramuiDialogsActivity(cell, dialogId, view);
            }
        });
        previewMenu[0].addView(markAsUnreadItem2);
        boolean[] hasPinAction = {true};
        final TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogId);
        boolean z3 = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null));
        boolean containsFilter = z3;
        if (z3) {
            dialogFilter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
        } else {
            dialogFilter = null;
        }
        final MessagesController.DialogFilter filter = dialogFilter;
        if (isDialogPinned(dialog)) {
            markAsUnreadItem = markAsUnreadItem2;
            flags = 2;
            chatActivity = chatActivity3;
            z = false;
        } else {
            int newPinnedCount = 0;
            int newPinnedSecretCount = 0;
            int pinnedCount = 0;
            MessagesController messagesController = getMessagesController();
            int pinnedSecretCount = this.folderId;
            ArrayList<TLRPC.Dialog> dialogs = messagesController.getDialogs(pinnedSecretCount);
            int N = dialogs.size();
            markAsUnreadItem = markAsUnreadItem2;
            int a = 0;
            int pinnedSecretCount2 = 0;
            while (true) {
                if (a >= N) {
                    flags = flags2;
                    chatActivity = chatActivity3;
                    break;
                }
                ArrayList<TLRPC.Dialog> dialogs2 = dialogs;
                TLRPC.Dialog dialog1 = dialogs.get(a);
                int N2 = N;
                if (dialog1 instanceof TLRPC.TL_dialogFolder) {
                    flags = flags2;
                    chatActivity = chatActivity3;
                } else if (isDialogPinned(dialog1)) {
                    flags = flags2;
                    chatActivity = chatActivity3;
                    if (DialogObject.isEncryptedDialog(dialog1.id)) {
                        pinnedSecretCount2++;
                    } else {
                        pinnedCount++;
                    }
                } else {
                    flags = flags2;
                    chatActivity = chatActivity3;
                    if (!getMessagesController().isPromoDialog(dialog1.id, false)) {
                        break;
                    }
                }
                a++;
                N = N2;
                dialogs = dialogs2;
                flags2 = flags;
                chatActivity3 = chatActivity;
            }
            int alreadyAdded = 0;
            if (dialog != null && !isDialogPinned(dialog)) {
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    newPinnedSecretCount = 0 + 1;
                } else {
                    newPinnedCount = 0 + 1;
                }
                if (filter != null && filter.alwaysShow.contains(Long.valueOf(dialogId))) {
                    alreadyAdded = 0 + 1;
                }
            }
            if (containsFilter && filter != null) {
                maxPinnedCount = 100 - filter.alwaysShow.size();
            } else if (this.folderId != 0 || filter != null) {
                maxPinnedCount = getMessagesController().maxFolderPinnedDialogsCount;
            } else {
                maxPinnedCount = getMessagesController().maxPinnedDialogsCount;
            }
            z = false;
            hasPinAction[0] = newPinnedSecretCount + pinnedSecretCount2 <= maxPinnedCount && (newPinnedCount + pinnedCount) - alreadyAdded <= maxPinnedCount;
        }
        int flags3 = z ? 1 : 0;
        int flags4 = z ? 1 : 0;
        if (hasPinAction[flags3]) {
            ActionBarMenuSubItem unpinItem = new ActionBarMenuSubItem(getParentActivity(), z, z);
            if (isDialogPinned(dialog)) {
                unpinItem.setTextAndIcon(LocaleController.getString("UnpinMessage", R.string.UnpinMessage), R.drawable.msg_unpin);
            } else {
                unpinItem.setTextAndIcon(LocaleController.getString("PinMessage", R.string.PinMessage), R.drawable.msg_pin);
            }
            unpinItem.setMinimumWidth(160);
            chatActivity2 = chatActivity;
            unpinItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda17
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DialogsActivity.this.m3377lambda$showChatPreview$27$orgtelegramuiDialogsActivity(filter, dialog, dialogId, view);
                }
            });
            previewMenu[0].addView(unpinItem);
        } else {
            chatActivity2 = chatActivity;
        }
        if (!DialogObject.isUserDialog(dialogId) || !UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(dialogId)))) {
            ActionBarMenuSubItem muteItem = new ActionBarMenuSubItem(getParentActivity(), false, false);
            if (!getMessagesController().isDialogMuted(dialogId)) {
                muteItem.setTextAndIcon(LocaleController.getString("Mute", R.string.Mute), R.drawable.msg_mute);
            } else {
                muteItem.setTextAndIcon(LocaleController.getString("Unmute", R.string.Unmute), R.drawable.msg_unmute);
            }
            muteItem.setMinimumWidth(160);
            muteItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda15
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DialogsActivity.this.m3378lambda$showChatPreview$28$orgtelegramuiDialogsActivity(dialogId, view);
                }
            });
            z2 = false;
            previewMenu[0].addView(muteItem);
        } else {
            z2 = false;
        }
        ActionBarMenuSubItem deleteItem = new ActionBarMenuSubItem(getParentActivity(), z2, true);
        deleteItem.setIconColor(getThemedColor(Theme.key_dialogRedIcon));
        deleteItem.setTextColor(getThemedColor(Theme.key_dialogTextRed));
        deleteItem.setTextAndIcon(LocaleController.getString("Delete", R.string.Delete), R.drawable.msg_delete);
        deleteItem.setMinimumWidth(160);
        deleteItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda16
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DialogsActivity.this.m3379lambda$showChatPreview$29$orgtelegramuiDialogsActivity(dialogIdArray, view);
            }
        });
        previewMenu[0].addView(deleteItem);
        if (getMessagesController().checkCanOpenChat(args, this)) {
            if (this.searchString != null) {
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            prepareBlurBitmap();
            this.parentLayout.highlightActionButtons = true;
            if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                BaseFragment chatActivity4 = new ChatActivity(args);
                chatActivity2[0] = chatActivity4;
                presentFragmentAsPreview(chatActivity4);
                return true;
            }
            BaseFragment chatActivity5 = new ChatActivity(args);
            chatActivity2[0] = chatActivity5;
            presentFragmentAsPreviewWithMenu(chatActivity5, previewMenu[0]);
            if (chatActivity2[0] != null) {
                chatActivity2[0].allowExpandPreviewByClick = true;
                try {
                    chatActivity2[0].getAvatarContainer().getAvatarImageView().performAccessibilityAction(64, null);
                    return true;
                } catch (Exception e) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$showChatPreview$25$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3375lambda$showChatPreview$25$orgtelegramuiDialogsActivity(DialogCell cell, long dialogId, View e) {
        if (cell.getHasUnread()) {
            markAsRead(dialogId);
        } else {
            markAsUnread(dialogId);
        }
        finishPreviewFragment();
    }

    /* renamed from: lambda$showChatPreview$27$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3377lambda$showChatPreview$27$orgtelegramuiDialogsActivity(final MessagesController.DialogFilter filter, final TLRPC.Dialog dialog, final long dialogId, View e) {
        finishPreviewFragment();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3376lambda$showChatPreview$26$orgtelegramuiDialogsActivity(filter, dialog, dialogId);
            }
        }, 100L);
    }

    /* renamed from: lambda$showChatPreview$26$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3376lambda$showChatPreview$26$orgtelegramuiDialogsActivity(MessagesController.DialogFilter filter, TLRPC.Dialog dialog, long dialogId) {
        int minPinnedNum;
        TLRPC.EncryptedChat encryptedChat;
        int minPinnedNum2 = Integer.MAX_VALUE;
        if (filter != null && isDialogPinned(dialog)) {
            int N = filter.pinnedDialogs.size();
            for (int c = 0; c < N; c++) {
                minPinnedNum2 = Math.min(minPinnedNum2, filter.pinnedDialogs.valueAt(c));
            }
            int c2 = this.canPinCount;
            minPinnedNum = minPinnedNum2 - c2;
        } else {
            minPinnedNum = Integer.MAX_VALUE;
        }
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            encryptedChat = null;
        } else {
            encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
        }
        if (!isDialogPinned(dialog)) {
            pinDialog(dialogId, true, filter, minPinnedNum, true);
            getUndoView().showWithAction(0L, 78, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
            if (filter != null) {
                if (encryptedChat != null) {
                    if (!filter.alwaysShow.contains(Long.valueOf(encryptedChat.user_id))) {
                        filter.alwaysShow.add(Long.valueOf(encryptedChat.user_id));
                    }
                } else if (!filter.alwaysShow.contains(Long.valueOf(dialogId))) {
                    filter.alwaysShow.add(Long.valueOf(dialogId));
                }
            }
        } else {
            pinDialog(dialogId, false, filter, minPinnedNum, true);
            getUndoView().showWithAction(0L, 79, (Object) 1, (Object) 1600, (Runnable) null, (Runnable) null);
        }
        if (filter != null) {
            FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, false, false, true, true, false, this, null);
        }
        getMessagesController().reorderPinnedDialogs(this.folderId, null, 0L);
        updateCounters(true);
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].dialogsAdapter.onReorderStateChanged(false);
                a++;
            }
        }
        int a2 = MessagesController.UPDATE_MASK_REORDER;
        updateVisibleRows(a2 | MessagesController.UPDATE_MASK_CHECK);
    }

    /* renamed from: lambda$showChatPreview$28$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3378lambda$showChatPreview$28$orgtelegramuiDialogsActivity(long dialogId, View e) {
        boolean isMuted = getMessagesController().isDialogMuted(dialogId);
        if (!isMuted) {
            getNotificationsController().setDialogNotificationsSettings(dialogId, 3);
        } else {
            getNotificationsController().setDialogNotificationsSettings(dialogId, 4);
        }
        BulletinFactory.createMuteBulletin(this, !isMuted, null).show();
        finishPreviewFragment();
    }

    /* renamed from: lambda$showChatPreview$29$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3379lambda$showChatPreview$29$orgtelegramuiDialogsActivity(ArrayList dialogIdArray, View e) {
        performSelectedDialogsAction(dialogIdArray, 102, false);
        finishPreviewFragment();
    }

    public void updateFloatingButtonOffset() {
        this.floatingButtonContainer.setTranslationY(this.floatingButtonTranslation - (Math.max(this.additionalFloatingTranslation, this.additionalFloatingTranslation2) * (1.0f - this.floatingButtonHideProgress)));
    }

    public boolean hasHiddenArchive() {
        return !this.onlySelect && this.initialDialogsType == 0 && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    public boolean waitingForDialogsAnimationEnd(ViewPage viewPage) {
        return (!viewPage.dialogsItemAnimator.isRunning() && this.dialogRemoveFinished == 0 && this.dialogInsertFinished == 0 && this.dialogChangeFinished == 0) ? false : true;
    }

    public void onDialogAnimationFinished() {
        this.dialogRemoveFinished = 0;
        this.dialogInsertFinished = 0;
        this.dialogChangeFinished = 0;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda26
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3358xc77d274f();
            }
        });
    }

    /* renamed from: lambda$onDialogAnimationFinished$30$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3358xc77d274f() {
        ArrayList<TLRPC.Dialog> arrayList;
        if (this.viewPages != null && this.folderId != 0 && ((arrayList = this.frozenDialogsList) == null || arrayList.isEmpty())) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].listView.setEmptyView(null);
                this.viewPages[a].progressView.setVisibility(4);
                a++;
            }
            finishFragment();
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    public void setScrollY(float value) {
        View view = this.scrimView;
        if (view != null) {
            view.getLocationInWindow(this.scrimViewLocation);
        }
        this.actionBar.setTranslationY(value);
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null) {
            filterTabsView.setTranslationY(value);
        }
        updateContextViewPosition();
        if (this.viewPages != null) {
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a].listView.setTopGlowOffset(this.viewPages[a].listView.getPaddingTop() + ((int) value));
                a++;
            }
        }
        this.fragmentView.invalidate();
    }

    private void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int w = (int) (this.fragmentView.getMeasuredWidth() / 6.0f);
        int h = (int) (this.fragmentView.getMeasuredHeight() / 6.0f);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        this.fragmentView.draw(canvas);
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
        this.blurredView.setBackground(new BitmapDrawable(bitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            if (isOpen) {
                this.blurredView.setAlpha(1.0f - progress);
            } else {
                this.blurredView.setAlpha(progress);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        View view;
        if (isOpen && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
        if (isOpen && this.afterSignup) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            if (getParentActivity() instanceof LaunchActivity) {
                ((LaunchActivity) getParentActivity()).getFireworksOverlay().start();
            }
        }
    }

    public void resetScroll() {
        if (this.actionBar.getTranslationY() == 0.0f) {
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, this.SCROLL_Y, 0.0f));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(180L);
        animatorSet.start();
    }

    public void hideActionMode(boolean animateCheck) {
        this.actionBar.hideActionMode();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", R.string.AccDescrOpenMenu));
        }
        this.selectedDialogs.clear();
        MenuDrawable menuDrawable = this.menuDrawable;
        if (menuDrawable != null) {
            menuDrawable.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable = this.backDrawable;
            if (backDrawable != null) {
                backDrawable.setRotation(0.0f, true);
            }
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null) {
            filterTabsView.animateColorsTo(Theme.key_actionBarTabLine, Theme.key_actionBarTabActiveText, Theme.key_actionBarTabUnactiveText, Theme.key_actionBarTabSelector, Theme.key_actionBarDefault);
        }
        ValueAnimator valueAnimator = this.actionBarColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 0;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToActionMode, 0.0f);
        this.actionBarColorAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda22
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsActivity.this.m3355lambda$hideActionMode$31$orgtelegramuiDialogsActivity(valueAnimator2);
            }
        });
        this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.actionBarColorAnimator.setDuration(200L);
        this.actionBarColorAnimator.start();
        this.allowMoving = false;
        if (!this.movingDialogFilters.isEmpty()) {
            int a = 0;
            for (int N = this.movingDialogFilters.size(); a < N; N = N) {
                MessagesController.DialogFilter filter = this.movingDialogFilters.get(a);
                FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, false, false, true, true, false, this, null);
                a++;
            }
            this.movingDialogFilters.clear();
        }
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, null, 0L);
            this.movingWas = false;
        }
        updateCounters(true);
        if (this.viewPages != null) {
            int a2 = 0;
            while (true) {
                ViewPage[] viewPageArr = this.viewPages;
                if (a2 >= viewPageArr.length) {
                    break;
                }
                viewPageArr[a2].dialogsAdapter.onReorderStateChanged(false);
                a2++;
            }
        }
        int a3 = MessagesController.UPDATE_MASK_REORDER;
        int i2 = a3 | MessagesController.UPDATE_MASK_CHECK;
        if (animateCheck) {
            i = MessagesController.UPDATE_MASK_CHAT;
        }
        updateVisibleRows(i2 | i);
    }

    /* renamed from: lambda$hideActionMode$31$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3355lambda$hideActionMode$31$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (this.actionBar.getChildAt(i).getVisibility() == 0 && this.actionBar.getChildAt(i) != this.actionBar.getActionMode() && this.actionBar.getChildAt(i) != this.actionBar.getBackButton()) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    private int getPinnedCount() {
        ArrayList<TLRPC.Dialog> dialogs;
        int pinnedCount = 0;
        boolean containsFilter = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null));
        if (containsFilter) {
            dialogs = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, this.dialogsListFrozen);
        } else {
            dialogs = getMessagesController().getDialogs(this.folderId);
        }
        int N = dialogs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                if (isDialogPinned(dialog)) {
                    pinnedCount++;
                } else if (!getMessagesController().isPromoDialog(dialog.id, false)) {
                    break;
                }
            }
        }
        return pinnedCount;
    }

    public boolean isDialogPinned(TLRPC.Dialog dialog) {
        MessagesController.DialogFilter filter;
        boolean containsFilter = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null));
        if (containsFilter) {
            filter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
        } else {
            filter = null;
        }
        if (filter != null) {
            return filter.pinnedDialogs.indexOfKey(dialog.id) >= 0;
        }
        return dialog.pinned;
    }

    /* JADX WARN: Code restructure failed: missing block: B:130:0x02b9, code lost:
        if (r13 != org.telegram.ui.DialogsActivity.pin2) goto L139;
     */
    /* JADX WARN: Code restructure failed: missing block: B:275:0x0670, code lost:
        r13 = false;
        org.telegram.ui.FilterCreateActivity.saveFilterToServer(r15, r15.flags, r15.name, r15.alwaysShow, r15.neverShow, r15.pinnedDialogs, false, false, true, true, false, r35, null);
        r2 = 0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:127:0x02b3  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x02dc  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x02ea  */
    /* JADX WARN: Removed duplicated region for block: B:264:0x0651  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x0653  */
    /* JADX WARN: Removed duplicated region for block: B:279:0x06bf  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x06d5  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x06e7  */
    /* JADX WARN: Type inference failed for: r13v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v33 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void performSelectedDialogsAction(java.util.ArrayList<java.lang.Long> r36, final int r37, boolean r38) {
        /*
            Method dump skipped, instructions count: 1949
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.performSelectedDialogsAction(java.util.ArrayList, int, boolean):void");
    }

    /* renamed from: lambda$performSelectedDialogsAction$32$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3366xef989341(ArrayList copy) {
        getMessagesController().addDialogToFolder(copy, this.folderId == 0 ? 0 : 1, -1, null, 0L);
    }

    /* renamed from: lambda$performSelectedDialogsAction$34$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3368xeb480b43(ArrayList selectedDialogs, final int action, DialogInterface dialog1, int which) {
        if (selectedDialogs.isEmpty()) {
            return;
        }
        final ArrayList<Long> didsCopy = new ArrayList<>(selectedDialogs);
        getUndoView().showWithAction(didsCopy, action == 102 ? 27 : 26, (Object) null, (Object) null, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3367x6d704f42(action, didsCopy);
            }
        }, (Runnable) null);
        hideActionMode(action == clear);
    }

    /* renamed from: lambda$performSelectedDialogsAction$33$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3367x6d704f42(int action, ArrayList didsCopy) {
        if (action == 102) {
            getMessagesController().setDialogsInTransaction(true);
            performSelectedDialogsAction(didsCopy, action, false);
            getMessagesController().setDialogsInTransaction(false);
            getMessagesController().checkIfFolderEmpty(this.folderId);
            if (this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 0) {
                this.viewPages[0].listView.setEmptyView(null);
                this.viewPages[0].progressView.setVisibility(4);
                finishFragment();
                return;
            }
            return;
        }
        performSelectedDialogsAction(didsCopy, action, false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$35$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3369x691fc744(ArrayList selectedDialogs, boolean report, boolean delete2) {
        int N = selectedDialogs.size();
        for (int a = 0; a < N; a++) {
            long did = ((Long) selectedDialogs.get(a)).longValue();
            if (report) {
                TLRPC.User u = getMessagesController().getUser(Long.valueOf(did));
                getMessagesController().reportSpam(did, u, null, null, false);
            }
            if (delete2) {
                getMessagesController().deleteDialog(did, 0, true);
            }
            getMessagesController().blockPeer(did);
        }
        hideActionMode(false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$36$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3370xe6f78345(DialogInterface dialog1, int which) {
        getMessagesController().hidePromoDialog();
        hideActionMode(false);
    }

    /* renamed from: lambda$performSelectedDialogsAction$38$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3372xe2a6fb47(final int action, final TLRPC.Chat chat, final long selectedDialog, final boolean isBot, final boolean param) {
        int selectedDialogIndex;
        ArrayList<TLRPC.Dialog> arrayList;
        hideActionMode(false);
        if (action == clear && ChatObject.isChannel(chat)) {
            if (!chat.megagroup || !TextUtils.isEmpty(chat.username)) {
                getMessagesController().deleteDialog(selectedDialog, 2, param);
                return;
            }
        }
        if (action == 102 && this.folderId != 0 && getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false).size() == 1) {
            this.viewPages[0].progressView.setVisibility(4);
        }
        this.debugLastUpdateAction = 3;
        if (action == 102) {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                for (int i = 0; i < this.frozenDialogsList.size(); i++) {
                    if (this.frozenDialogsList.get(i).id == selectedDialog) {
                        int selectedDialogIndex2 = i;
                        selectedDialogIndex = selectedDialogIndex2;
                        break;
                    }
                }
            }
        }
        selectedDialogIndex = -1;
        int selectedDialogIndex3 = selectedDialogIndex;
        getUndoView().showWithAction(selectedDialog, action == clear ? 0 : 1, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3371x64cf3f46(action, selectedDialog, chat, isBot, param);
            }
        });
        ArrayList<TLRPC.Dialog> currentDialogs = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
        int currentDialogIndex = -1;
        int i2 = 0;
        while (true) {
            if (i2 < currentDialogs.size()) {
                if (currentDialogs.get(i2).id != selectedDialog) {
                    i2++;
                } else {
                    currentDialogIndex = i2;
                    break;
                }
            } else {
                break;
            }
        }
        if (action == 102) {
            if (selectedDialogIndex3 < 0 || currentDialogIndex >= 0 || (arrayList = this.frozenDialogsList) == null) {
                setDialogsListFrozen(false);
                return;
            }
            arrayList.remove(selectedDialogIndex3);
            this.viewPages[0].dialogsItemAnimator.prepareForRemove();
            this.viewPages[0].dialogsAdapter.notifyItemRemoved(selectedDialogIndex3);
            this.dialogRemoveFinished = 2;
        }
    }

    /* renamed from: lambda$performSelectedDialogsAction$39$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3373x607eb748(DialogInterface dialog12) {
        hideActionMode(true);
    }

    private void markAsRead(long did) {
        MessagesController.DialogFilter filter;
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
        boolean containsFilter = (this.viewPages[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) && (!this.actionBar.isActionModeShowed() || this.actionBar.isActionModeShowed(null));
        if (containsFilter) {
            filter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
        } else {
            filter = null;
        }
        this.debugLastUpdateAction = 2;
        int selectedDialogIndex = -1;
        if (filter != null && (filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && !filter.alwaysShow(this.currentAccount, dialog)) {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                int i = 0;
                while (true) {
                    if (i >= this.frozenDialogsList.size()) {
                        break;
                    } else if (this.frozenDialogsList.get(i).id != did) {
                        i++;
                    } else {
                        selectedDialogIndex = i;
                        break;
                    }
                }
                if (selectedDialogIndex < 0) {
                    setDialogsListFrozen(false, false);
                }
            }
        }
        int selectedDialogIndex2 = selectedDialogIndex;
        getMessagesController().markMentionsAsRead(did);
        getMessagesController().markDialogAsRead(did, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, 0, true, 0);
        if (selectedDialogIndex2 >= 0) {
            this.frozenDialogsList.remove(selectedDialogIndex2);
            this.viewPages[0].dialogsItemAnimator.prepareForRemove();
            this.viewPages[0].dialogsAdapter.notifyItemRemoved(selectedDialogIndex2);
            this.dialogRemoveFinished = 2;
        }
    }

    private void markAsUnread(long did) {
        getMessagesController().markDialogAsUnread(did, null, 0L);
    }

    /* renamed from: performDeleteOrClearDialogAction */
    public void m3371x64cf3f46(int action, long selectedDialog, TLRPC.Chat chat, boolean isBot, boolean revoke) {
        if (action == clear) {
            getMessagesController().deleteDialog(selectedDialog, 1, revoke);
            return;
        }
        if (chat == null) {
            getMessagesController().deleteDialog(selectedDialog, 0, revoke);
            if (isBot) {
                getMessagesController().blockPeer((int) selectedDialog);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(selectedDialog, 0, revoke);
        } else {
            TLRPC.User currentUser = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            getMessagesController().deleteParticipantFromChat((int) (-selectedDialog), currentUser, null, null, revoke, false);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog));
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    private void pinDialog(long selectedDialog, boolean pin3, MessagesController.DialogFilter filter, int minPinnedNum, boolean animated) {
        boolean needScroll;
        int selectedDialogIndex;
        int selectedDialogIndex2;
        boolean updated;
        int currentDialogIndex = -1;
        int scrollToPosition = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        int currentPosition = this.viewPages[0].layoutManager.findFirstVisibleItemPosition();
        if (filter != null) {
            int index = filter.pinnedDialogs.get(selectedDialog, Integer.MIN_VALUE);
            if (!pin3 && index == Integer.MIN_VALUE) {
                return;
            }
        }
        this.debugLastUpdateAction = pin3 ? 4 : 5;
        if (currentPosition > scrollToPosition || !animated) {
            selectedDialogIndex = -1;
            needScroll = true;
        } else {
            setDialogsListFrozen(true);
            if (this.frozenDialogsList != null) {
                for (int i = 0; i < this.frozenDialogsList.size(); i++) {
                    if (this.frozenDialogsList.get(i).id == selectedDialog) {
                        selectedDialogIndex = i;
                        needScroll = false;
                        break;
                    }
                }
            }
            selectedDialogIndex = -1;
            needScroll = false;
        }
        if (filter != null) {
            if (pin3) {
                filter.pinnedDialogs.put(selectedDialog, minPinnedNum);
            } else {
                filter.pinnedDialogs.delete(selectedDialog);
            }
            if (animated) {
                getMessagesController().onFilterUpdate(filter);
            }
            updated = true;
            selectedDialogIndex2 = selectedDialogIndex;
        } else {
            selectedDialogIndex2 = selectedDialogIndex;
            updated = getMessagesController().pinDialog(selectedDialog, pin3, null, -1L);
        }
        if (updated) {
            if (!needScroll) {
                ArrayList<TLRPC.Dialog> currentDialogs = getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false);
                int i2 = 0;
                while (true) {
                    if (i2 < currentDialogs.size()) {
                        if (currentDialogs.get(i2).id != selectedDialog) {
                            i2++;
                        } else {
                            currentDialogIndex = i2;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } else {
                if (this.initialDialogsType != 10) {
                    hideFloatingButton(false);
                }
                scrollToTop();
            }
        }
        if (!needScroll) {
            boolean animate = false;
            if (selectedDialogIndex2 >= 0) {
                ArrayList<TLRPC.Dialog> arrayList = this.frozenDialogsList;
                if (arrayList != null && currentDialogIndex >= 0 && selectedDialogIndex2 != currentDialogIndex) {
                    arrayList.add(currentDialogIndex, arrayList.remove(selectedDialogIndex2));
                    this.viewPages[0].dialogsItemAnimator.prepareForRemove();
                    this.viewPages[0].dialogsAdapter.notifyItemRemoved(selectedDialogIndex2);
                    this.viewPages[0].dialogsAdapter.notifyItemInserted(currentDialogIndex);
                    this.dialogRemoveFinished = 2;
                    this.dialogInsertFinished = 2;
                    this.viewPages[0].layoutManager.scrollToPositionWithOffset((this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1, (int) this.actionBar.getTranslationY());
                    animate = true;
                } else if (currentDialogIndex >= 0 && selectedDialogIndex2 == currentDialogIndex) {
                    animate = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda28
                        @Override // java.lang.Runnable
                        public final void run() {
                            DialogsActivity.this.m3374lambda$pinDialog$40$orgtelegramuiDialogsActivity();
                        }
                    }, 200L);
                }
            }
            if (!animate) {
                setDialogsListFrozen(false);
            }
        }
    }

    /* renamed from: lambda$pinDialog$40$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3374lambda$pinDialog$40$orgtelegramuiDialogsActivity() {
        setDialogsListFrozen(false);
    }

    public void scrollToTop() {
        int scrollDistance = this.viewPages[0].layoutManager.findFirstVisibleItemPosition() * AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        int position = (this.viewPages[0].dialogsType != 0 || !hasHiddenArchive()) ? 0 : 1;
        this.viewPages[0].listView.getItemAnimator();
        if (scrollDistance >= this.viewPages[0].listView.getMeasuredHeight() * 1.2f) {
            this.viewPages[0].scrollHelper.setScrollDirection(1);
            this.viewPages[0].scrollHelper.scrollToPosition(position, 0, false, true);
            resetScroll();
            return;
        }
        this.viewPages[0].listView.smoothScrollToPosition(position);
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x00fe  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0162  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateCounters(boolean r23) {
        /*
            Method dump skipped, instructions count: 1018
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateCounters(boolean):void");
    }

    public boolean validateSlowModeDialog(long dialogId) {
        TLRPC.Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount > 1 || ((chatActivityEnterView = this.commentView) != null && chatActivityEnterView.getVisibility() == 0 && !TextUtils.isEmpty(this.commentView.getFieldText()))) && DialogObject.isChatDialog(dialogId) && (chat = getMessagesController().getChat(Long.valueOf(-dialogId))) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError));
            return false;
        }
        return true;
    }

    private void showOrUpdateActionMode(long dialogId, View cell) {
        addOrRemoveSelectedDialog(dialogId, cell);
        boolean updateAnimated = false;
        if (this.actionBar.isActionModeShowed()) {
            if (this.selectedDialogs.isEmpty()) {
                hideActionMode(true);
                return;
            }
            updateAnimated = true;
        } else {
            if (this.searchIsShowed) {
                createActionMode("search_dialogs_action_mode");
                if (this.actionBar.getBackButton().getDrawable() instanceof MenuDrawable) {
                    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
                }
            } else {
                createActionMode(null);
            }
            AndroidUtilities.hideKeyboard(this.fragmentView.findFocus());
            this.actionBar.setActionModeOverrideColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.actionBar.showActionMode();
            resetScroll();
            if (this.menuDrawable != null) {
                this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
            }
            if (getPinnedCount() > 1) {
                if (this.viewPages != null) {
                    int a = 0;
                    while (true) {
                        ViewPage[] viewPageArr = this.viewPages;
                        if (a >= viewPageArr.length) {
                            break;
                        }
                        viewPageArr[a].dialogsAdapter.onReorderStateChanged(true);
                        a++;
                    }
                }
                int a2 = MessagesController.UPDATE_MASK_REORDER;
                updateVisibleRows(a2);
            }
            if (!this.searchIsShowed) {
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList<>();
                for (int a3 = 0; a3 < this.actionModeViews.size(); a3++) {
                    View view = this.actionModeViews.get(a3);
                    view.setPivotY(ActionBar.getCurrentActionBarHeight() / 2);
                    AndroidUtilities.clearDrawableAnimation(view);
                    animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 1.0f));
                }
                animatorSet.playTogether(animators);
                animatorSet.setDuration(200L);
                animatorSet.start();
            }
            ValueAnimator valueAnimator = this.actionBarColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToActionMode, 1.0f);
            this.actionBarColorAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda44
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DialogsActivity.this.m3382lambda$showOrUpdateActionMode$41$orgtelegramuiDialogsActivity(valueAnimator2);
                }
            });
            this.actionBarColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.actionBarColorAnimator.setDuration(200L);
            this.actionBarColorAnimator.start();
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null) {
                filterTabsView.animateColorsTo(Theme.key_profile_tabSelectedLine, Theme.key_profile_tabSelectedText, Theme.key_profile_tabText, Theme.key_profile_tabSelector, Theme.key_actionBarActionModeDefault);
            }
            MenuDrawable menuDrawable = this.menuDrawable;
            if (menuDrawable != null) {
                menuDrawable.setRotateToBack(false);
                this.menuDrawable.setRotation(1.0f, true);
            } else {
                BackDrawable backDrawable = this.backDrawable;
                if (backDrawable != null) {
                    backDrawable.setRotation(1.0f, true);
                }
            }
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(this.selectedDialogs.size(), updateAnimated);
    }

    /* renamed from: lambda$showOrUpdateActionMode$41$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3382lambda$showOrUpdateActionMode$41$orgtelegramuiDialogsActivity(ValueAnimator valueAnimator) {
        this.progressToActionMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (this.actionBar.getChildAt(i).getVisibility() == 0 && this.actionBar.getChildAt(i) != this.actionBar.getActionMode() && this.actionBar.getChildAt(i) != this.actionBar.getBackButton()) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    public void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            if (this.searchObject != null) {
                this.searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    public RecyclerListView getListView() {
        return this.viewPages[0].listView;
    }

    public RecyclerListView getSearchListView() {
        return this.searchViewPager.searchListView;
    }

    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            ContentView contentView = (ContentView) this.fragmentView;
            contentView.removeView(this.undoView[0]);
            contentView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public void updateProxyButton(boolean animated, boolean force) {
        ActionBarMenuItem actionBarMenuItem;
        if (this.proxyDrawable != null) {
            ActionBarMenuItem actionBarMenuItem2 = this.doneItem;
            if (actionBarMenuItem2 != null && actionBarMenuItem2.getVisibility() == 0) {
                return;
            }
            boolean showDownloads = false;
            int i = 0;
            while (true) {
                if (i >= getDownloadController().downloadingFiles.size()) {
                    break;
                } else if (!getFileLoader().isLoadingFile(getDownloadController().downloadingFiles.get(i).getFileName())) {
                    i++;
                } else {
                    showDownloads = true;
                    break;
                }
            }
            boolean z = true;
            if (!this.searching && (getDownloadController().hasUnviewedDownloads() || showDownloads || (this.downloadsItem.getVisibility() == 0 && this.downloadsItem.getAlpha() == 1.0f && !force))) {
                this.downloadsItemVisible = true;
                this.downloadsItem.setVisibility(0);
            } else {
                this.downloadsItem.setVisibility(8);
                this.downloadsItemVisible = false;
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String proxyAddress = preferences.getString("proxy_ip", "");
            boolean proxyEnabled = preferences.getBoolean("proxy_enabled", false);
            if ((!this.downloadsItemVisible && !this.searching && proxyEnabled && !TextUtils.isEmpty(proxyAddress)) || (getMessagesController().blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                if (!this.actionBar.isSearchFieldVisible() && ((actionBarMenuItem = this.doneItem) == null || actionBarMenuItem.getVisibility() != 0)) {
                    this.proxyItem.setVisibility(0);
                }
                this.proxyItemVisible = true;
                ProxyDrawable proxyDrawable = this.proxyDrawable;
                int i2 = this.currentConnectionState;
                if (i2 != 3 && i2 != 5) {
                    z = false;
                }
                proxyDrawable.setConnected(proxyEnabled, z, animated);
                return;
            }
            this.proxyItemVisible = false;
            this.proxyItem.setVisibility(8);
        }
    }

    public void showDoneItem(final boolean show) {
        if (this.doneItem == null) {
            return;
        }
        AnimatorSet animatorSet = this.doneItemAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.doneItemAnimator = null;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.doneItemAnimator = animatorSet2;
        animatorSet2.setDuration(180L);
        if (show) {
            this.doneItem.setVisibility(0);
        } else {
            this.doneItem.setSelected(false);
            Drawable background = this.doneItem.getBackground();
            if (background != null) {
                background.setState(StateSet.NOTHING);
                background.jumpToCurrentState();
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(0);
            }
            ActionBarMenuItem actionBarMenuItem2 = this.proxyItem;
            if (actionBarMenuItem2 != null && this.proxyItemVisible) {
                actionBarMenuItem2.setVisibility(0);
            }
            ActionBarMenuItem actionBarMenuItem3 = this.passcodeItem;
            if (actionBarMenuItem3 != null && this.passcodeItemVisible) {
                actionBarMenuItem3.setVisibility(0);
            }
            ActionBarMenuItem actionBarMenuItem4 = this.downloadsItem;
            if (actionBarMenuItem4 != null && this.downloadsItemVisible) {
                actionBarMenuItem4.setVisibility(0);
            }
        }
        ArrayList<Animator> arrayList = new ArrayList<>();
        ActionBarMenuItem actionBarMenuItem5 = this.doneItem;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property, fArr));
        if (this.proxyItemVisible) {
            ActionBarMenuItem actionBarMenuItem6 = this.proxyItem;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property2, fArr2));
        }
        if (this.passcodeItemVisible) {
            ActionBarMenuItem actionBarMenuItem7 = this.passcodeItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property3, fArr3));
        }
        ActionBarMenuItem actionBarMenuItem8 = this.searchItem;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        if (show) {
            f = 0.0f;
        }
        fArr4[0] = f;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property4, fArr4));
        this.doneItemAnimator.playTogether(arrayList);
        this.doneItemAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.35
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                DialogsActivity.this.doneItemAnimator = null;
                if (show) {
                    if (DialogsActivity.this.searchItem != null) {
                        DialogsActivity.this.searchItem.setVisibility(4);
                    }
                    if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisible) {
                        DialogsActivity.this.proxyItem.setVisibility(4);
                    }
                    if (DialogsActivity.this.passcodeItem != null && DialogsActivity.this.passcodeItemVisible) {
                        DialogsActivity.this.passcodeItem.setVisibility(4);
                    }
                    if (DialogsActivity.this.downloadsItem != null && DialogsActivity.this.downloadsItemVisible) {
                        DialogsActivity.this.downloadsItem.setVisibility(4);
                    }
                } else if (DialogsActivity.this.doneItem != null) {
                    DialogsActivity.this.doneItem.setVisibility(8);
                }
            }
        });
        this.doneItemAnimator.start();
    }

    public void updateSelectedCount() {
        CharSequence charSequence = "";
        if (this.commentView != null) {
            if (this.selectedDialogs.isEmpty()) {
                if (this.initialDialogsType == 3 && this.selectAlertString == null) {
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
                }
                if (this.commentView.getTag() != null) {
                    this.commentView.hidePopup(false);
                    this.commentView.closeKeyboard();
                    AnimatorSet animatorSet = this.commentViewAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    this.commentViewAnimator = new AnimatorSet();
                    this.commentView.setTranslationY(0.0f);
                    this.commentViewAnimator.playTogether(ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, this.commentView.getMeasuredHeight()), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_X, 0.2f), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_Y, 0.2f), ObjectAnimator.ofFloat(this.writeButtonContainer, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_X, 0.2f), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_Y, 0.2f), ObjectAnimator.ofFloat(this.selectedCountView, View.ALPHA, 0.0f));
                    this.commentViewAnimator.setDuration(180L);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.36
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            DialogsActivity.this.commentView.setVisibility(8);
                            DialogsActivity.this.writeButtonContainer.setVisibility(8);
                        }
                    });
                    this.commentViewAnimator.start();
                    this.commentView.setTag(null);
                    this.fragmentView.requestLayout();
                }
            } else {
                this.selectedCountView.invalidate();
                if (this.commentView.getTag() == null) {
                    this.commentView.setFieldText(charSequence);
                    AnimatorSet animatorSet2 = this.commentViewAnimator;
                    if (animatorSet2 != null) {
                        animatorSet2.cancel();
                    }
                    this.commentView.setVisibility(0);
                    this.writeButtonContainer.setVisibility(0);
                    AnimatorSet animatorSet3 = new AnimatorSet();
                    this.commentViewAnimator = animatorSet3;
                    animatorSet3.playTogether(ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, this.commentView.getMeasuredHeight(), 0.0f), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.writeButtonContainer, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.writeButtonContainer, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, View.ALPHA, 1.0f));
                    this.commentViewAnimator.setDuration(180L);
                    this.commentViewAnimator.setInterpolator(new DecelerateInterpolator());
                    this.commentViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.37
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            DialogsActivity.this.commentView.setTag(2);
                            DialogsActivity.this.commentView.requestLayout();
                        }
                    });
                    this.commentViewAnimator.start();
                    this.commentView.setTag(1);
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.selectedDialogs.size(), new Object[0]));
            }
        } else if (this.initialDialogsType == 10) {
            hideFloatingButton(this.selectedDialogs.isEmpty());
        }
        ArrayList<Long> arrayList = this.selectedDialogs;
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            charSequence = chatActivityEnterView.getFieldText();
        }
        boolean shouldShowNextButton = shouldShowNextButton(this, arrayList, charSequence, false);
        this.isNextButton = shouldShowNextButton;
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[0], !shouldShowNextButton, 0.5f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.writeButton[1], this.isNextButton, 0.5f, true);
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        ArrayList<String> permissons = new ArrayList<>();
        if (getUserConfig().syncContacts && this.askAboutContacts && activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
            if (alert) {
                AlertDialog.Builder builder = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda43
                    @Override // org.telegram.messenger.MessagesStorage.IntCallback
                    public final void run(int i) {
                        DialogsActivity.this.m3334lambda$askForPermissons$42$orgtelegramuiDialogsActivity(i);
                    }
                });
                AlertDialog create = builder.create();
                this.permissionDialog = create;
                showDialog(create);
                return;
            }
            permissons.add("android.permission.READ_CONTACTS");
            permissons.add("android.permission.WRITE_CONTACTS");
            permissons.add("android.permission.GET_ACCOUNTS");
        }
        if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            permissons.add("android.permission.READ_EXTERNAL_STORAGE");
            permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (permissons.isEmpty()) {
            if (this.askingForPermissions) {
                this.askingForPermissions = false;
                showFiltersHint();
                return;
            }
            return;
        }
        String[] items = (String[]) permissons.toArray(new String[0]);
        try {
            activity.requestPermissions(items, 1);
        } catch (Exception e) {
        }
    }

    /* renamed from: lambda$askForPermissons$42$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3334lambda$askForPermissons$42$orgtelegramuiDialogsActivity(int param) {
        this.askAboutContacts = param != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null) {
            askForPermissons(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        FrameLayout frameLayout;
        super.onConfigurationChanged(newConfig);
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
        if (!this.onlySelect && (frameLayout = this.floatingButtonContainer) != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: org.telegram.ui.DialogsActivity.38
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    dialogsActivity.floatingButtonTranslation = dialogsActivity.floatingHidden ? AndroidUtilities.dp(100.0f) : 0.0f;
                    DialogsActivity.this.updateFloatingButtonOffset();
                    DialogsActivity.this.floatingButtonContainer.setClickable(!DialogsActivity.this.floatingHidden);
                    if (DialogsActivity.this.floatingButtonContainer != null) {
                        DialogsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a) {
                    String str = permissions[a];
                    char c = 65535;
                    switch (str.hashCode()) {
                        case 1365911975:
                            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 1977429404:
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                c = 0;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (grantResults[a] == 0) {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda27
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        DialogsActivity.this.m3360x3f791cc5();
                                    }
                                });
                                getContactsController().forceImportContacts();
                                break;
                            } else {
                                SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                                this.askAboutContacts = false;
                                edit.putBoolean("askAboutContacts", false).commit();
                                continue;
                            }
                        case 1:
                            if (grantResults[a] == 0) {
                                ImageLoader.getInstance().checkMediaPaths();
                                break;
                            } else {
                                continue;
                            }
                    }
                }
            }
            if (this.askingForPermissions) {
                this.askingForPermissions = false;
                showFiltersHint();
            }
        } else if (requestCode == 4) {
            boolean allGranted = true;
            int a2 = 0;
            while (true) {
                if (a2 < grantResults.length) {
                    if (grantResults[a2] == 0) {
                        a2++;
                    } else {
                        allGranted = false;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (allGranted && Build.VERSION.SDK_INT >= 30 && FilesMigrationService.filesMigrationBottomSheet != null) {
                FilesMigrationService.filesMigrationBottomSheet.migrateOldFolder();
            }
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultFragment$43$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3360x3f791cc5() {
        getNotificationCenter().postNotificationName(NotificationCenter.forceImportContactsStart, new Object[0]);
    }

    private void reloadViewPageDialogs(ViewPage viewPage, boolean newMessage) {
        int i;
        int i2;
        if (viewPage.getVisibility() == 0) {
            int oldItemCount = viewPage.dialogsAdapter.getCurrentCount();
            if (viewPage.dialogsType == 0 && hasHiddenArchive() && viewPage.listView.getChildCount() == 0) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) viewPage.listView.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(1, 0);
            }
            if (viewPage.dialogsAdapter.isDataSetChanged() || newMessage) {
                viewPage.dialogsAdapter.updateHasHints();
                int newItemCount = viewPage.dialogsAdapter.getItemCount();
                if (newItemCount != 1 || oldItemCount != 1 || viewPage.dialogsAdapter.getItemViewType(0) != 5) {
                    viewPage.dialogsAdapter.notifyDataSetChanged();
                    if (newItemCount > oldItemCount && (i = this.initialDialogsType) != 11 && i != 12 && i != 13) {
                        viewPage.recyclerItemsEnterAnimator.showItemsAnimated(oldItemCount);
                    }
                } else if (viewPage.dialogsAdapter.lastDialogsEmptyType != viewPage.dialogsAdapter.dialogsEmptyType()) {
                    viewPage.dialogsAdapter.notifyItemChanged(0);
                }
            } else {
                updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                if (viewPage.dialogsAdapter.getItemCount() > oldItemCount && (i2 = this.initialDialogsType) != 11 && i2 != 12 && i2 != 13) {
                    viewPage.recyclerItemsEnterAnimator.showItemsAnimated(oldItemCount);
                }
            }
            try {
                viewPage.listView.setEmptyView(this.folderId == 0 ? viewPage.progressView : null);
            } catch (Exception e) {
                FileLog.e(e);
            }
            checkListLoad(viewPage);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, final Object... args) {
        ViewPage[] viewPageArr;
        if (id == NotificationCenter.dialogsNeedReload) {
            if (this.viewPages == null || this.dialogsListFrozen) {
                return;
            }
            int a = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (a >= viewPageArr2.length) {
                    break;
                }
                final ViewPage viewPage = viewPageArr2[a];
                MessagesController.DialogFilter filter = null;
                if (viewPageArr2[0].dialogsType == 7 || this.viewPages[0].dialogsType == 8) {
                    filter = getMessagesController().selectedDialogFilter[this.viewPages[0].dialogsType == 8 ? (char) 1 : (char) 0];
                }
                boolean isUnread = (filter == null || (filter.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) == 0) ? false : true;
                if (!this.slowedReloadAfterDialogClick || !isUnread) {
                    reloadViewPageDialogs(viewPage, args.length > 0);
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda39
                        @Override // java.lang.Runnable
                        public final void run() {
                            DialogsActivity.this.m3347xa0368376(viewPage, args);
                        }
                    }, 160L);
                }
                a++;
            }
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null && filterTabsView.getVisibility() == 0) {
                this.filterTabsView.checkTabsCounter();
            }
            this.slowedReloadAfterDialogClick = false;
        } else if (id == NotificationCenter.dialogsUnreadCounterChanged) {
            FilterTabsView filterTabsView2 = this.filterTabsView;
            if (filterTabsView2 != null && filterTabsView2.getVisibility() == 0) {
                FilterTabsView filterTabsView3 = this.filterTabsView;
                filterTabsView3.notifyTabCounterChanged(filterTabsView3.getDefaultTabId());
            }
        } else if (id == NotificationCenter.dialogsUnreadReactionsCounterChanged) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.emojiLoaded) {
            updateVisibleRows(0);
            FilterTabsView filterTabsView4 = this.filterTabsView;
            if (filterTabsView4 != null) {
                filterTabsView4.getTabsContainer().invalidateViews();
            }
        } else if (id == NotificationCenter.closeSearchByActiveAction) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (id == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false, false);
        } else if (id == NotificationCenter.updateInterfaces) {
            Integer mask = (Integer) args[0];
            updateVisibleRows(mask.intValue());
            FilterTabsView filterTabsView5 = this.filterTabsView;
            if (filterTabsView5 != null && filterTabsView5.getVisibility() == 0 && (mask.intValue() & MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE) != 0) {
                this.filterTabsView.checkTabsCounter();
            }
            if (this.viewPages != null) {
                for (int a2 = 0; a2 < this.viewPages.length; a2++) {
                    if ((mask.intValue() & MessagesController.UPDATE_MASK_STATUS) != 0) {
                        this.viewPages[a2].dialogsAdapter.sortOnlineContacts(true);
                    }
                }
            }
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoad) {
            if (this.viewPages == null || this.dialogsListFrozen) {
                return;
            }
            boolean wasVisible = this.floatingProgressVisible;
            setFloatingProgressVisible(false, true);
            for (ViewPage page : this.viewPages) {
                page.dialogsAdapter.setForceUpdatingContacts(false);
            }
            if (wasVisible) {
                setContactsAlpha(0.0f);
                animateContactsAlpha(1.0f);
            }
            boolean updateVisibleRows = false;
            int a3 = 0;
            while (true) {
                ViewPage[] viewPageArr3 = this.viewPages;
                if (a3 >= viewPageArr3.length) {
                    break;
                }
                if (viewPageArr3[a3].isDefaultDialogType() && getMessagesController().getAllFoldersDialogsCount() <= 10) {
                    this.viewPages[a3].dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows = true;
                }
                a3++;
            }
            if (updateVisibleRows) {
                updateVisibleRows(0);
            }
        } else if (id == NotificationCenter.openedChatChanged) {
            if (this.viewPages == null) {
                return;
            }
            int a4 = 0;
            while (true) {
                ViewPage[] viewPageArr4 = this.viewPages;
                if (a4 < viewPageArr4.length) {
                    if (viewPageArr4[a4].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                        boolean close = ((Boolean) args[1]).booleanValue();
                        long dialog_id = ((Long) args[0]).longValue();
                        if (close) {
                            if (dialog_id == this.openedDialogId) {
                                this.openedDialogId = 0L;
                            }
                        } else {
                            this.openedDialogId = dialog_id;
                        }
                        this.viewPages[a4].dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                    }
                    a4++;
                } else {
                    int a5 = MessagesController.UPDATE_MASK_SELECT_DIALOG;
                    updateVisibleRows(a5);
                    return;
                }
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            SearchViewPager searchViewPager = this.searchViewPager;
            if (searchViewPager != null && searchViewPager.dialogsSearchAdapter != null) {
                this.searchViewPager.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(MessagesController.UPDATE_MASK_MESSAGE_TEXT);
        } else if (id == NotificationCenter.reloadHints) {
            SearchViewPager searchViewPager2 = this.searchViewPager;
            if (searchViewPager2 != null && searchViewPager2.dialogsSearchAdapter != null) {
                this.searchViewPager.dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = AccountInstance.getInstance(account).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                updateProxyButton(true, false);
            }
        } else if (id == NotificationCenter.onDownloadingFilesChanged) {
            updateProxyButton(true, false);
        } else if (id == NotificationCenter.needDeleteDialog) {
            if (this.fragmentView == null || this.isPaused) {
                return;
            }
            final long dialogId = ((Long) args[0]).longValue();
            final TLRPC.User user = (TLRPC.User) args[1];
            final TLRPC.Chat chat = (TLRPC.Chat) args[2];
            final boolean revoke = ((Boolean) args[3]).booleanValue();
            Runnable deleteRunnable = new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda37
                @Override // java.lang.Runnable
                public final void run() {
                    DialogsActivity.this.m3348x1e0e3f77(chat, dialogId, revoke, user);
                }
            };
            if (this.undoView[0] != null) {
                getUndoView().showWithAction(dialogId, 1, deleteRunnable);
            } else {
                deleteRunnable.run();
            }
        } else if (id == NotificationCenter.folderBecomeEmpty) {
            int fid = ((Integer) args[0]).intValue();
            int i = this.folderId;
            if (i == fid && i != 0) {
                finishFragment();
            }
        } else if (id == NotificationCenter.dialogFiltersUpdated) {
            updateFilterTabs(true, true);
        } else if (id == NotificationCenter.filterSettingsUpdated) {
            showFiltersHint();
        } else if (id == NotificationCenter.newSuggestionsAvailable) {
            showNextSupportedSuggestion();
        } else if (id == NotificationCenter.forceImportContactsStart) {
            setFloatingProgressVisible(true, true);
            for (ViewPage page2 : this.viewPages) {
                page2.dialogsAdapter.setForceShowEmptyCell(false);
                page2.dialogsAdapter.setForceUpdatingContacts(true);
                page2.dialogsAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            if (this.searchIsShowed && this.searchViewPager != null) {
                ArrayList<Integer> markAsDeletedMessages = (ArrayList) args[0];
                long channelId = ((Long) args[1]).longValue();
                this.searchViewPager.messagesDeleted(channelId, markAsDeletedMessages);
            }
        } else if (id == NotificationCenter.didClearDatabase) {
            if (this.viewPages != null) {
                int a6 = 0;
                while (true) {
                    ViewPage[] viewPageArr5 = this.viewPages;
                    if (a6 >= viewPageArr5.length) {
                        break;
                    }
                    viewPageArr5[a6].dialogsAdapter.didDatabaseCleared();
                    a6++;
                }
            }
            SuggestClearDatabaseBottomSheet.dismissDialog();
        } else if (id == NotificationCenter.appUpdateAvailable) {
            updateMenuButton(true);
        } else if (id == NotificationCenter.fileLoaded || id == NotificationCenter.fileLoadFailed || id == NotificationCenter.fileLoadProgressChanged) {
            String name = (String) args[0];
            if (SharedConfig.isAppUpdateAvailable()) {
                String fileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
                if (fileName.equals(name)) {
                    updateMenuButton(true);
                }
            }
        } else if (id == NotificationCenter.onDatabaseMigration) {
            boolean startMigration = ((Boolean) args[0]).booleanValue();
            if (this.fragmentView != null) {
                if (startMigration) {
                    if (this.databaseMigrationHint == null) {
                        DatabaseMigrationHint databaseMigrationHint = new DatabaseMigrationHint(this.fragmentView.getContext(), this.currentAccount);
                        this.databaseMigrationHint = databaseMigrationHint;
                        databaseMigrationHint.setAlpha(0.0f);
                        ((ContentView) this.fragmentView).addView(this.databaseMigrationHint);
                        this.databaseMigrationHint.animate().alpha(1.0f).setDuration(300L).setStartDelay(1000L).start();
                    }
                    this.databaseMigrationHint.setTag(1);
                    return;
                }
                View view = this.databaseMigrationHint;
                if (view != null && view.getTag() != null) {
                    final View localView = this.databaseMigrationHint;
                    localView.animate().setListener(null).cancel();
                    localView.animate().setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.39
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (localView.getParent() != null) {
                                ((ViewGroup) localView.getParent()).removeView(localView);
                            }
                            DialogsActivity.this.databaseMigrationHint = null;
                        }
                    }).alpha(0.0f).setStartDelay(0L).setDuration(150L).start();
                    this.databaseMigrationHint.setTag(null);
                }
            }
        } else if (id == NotificationCenter.onDatabaseOpened) {
            checkSuggestClearDatabase();
        }
    }

    /* renamed from: lambda$didReceivedNotification$44$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3347xa0368376(ViewPage viewPage, Object[] args) {
        reloadViewPageDialogs(viewPage, args.length > 0);
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.getVisibility() == 0) {
            this.filterTabsView.checkTabsCounter();
        }
    }

    /* renamed from: lambda$didReceivedNotification$45$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3348x1e0e3f77(TLRPC.Chat chat, long dialogId, boolean revoke, TLRPC.User user) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
            if (user != null && user.bot) {
                getMessagesController().blockPeer(user.id);
            }
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteParticipantFromChat(-dialogId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), null, null, revoke, revoke);
        }
        getMessagesController().checkIfFolderEmpty(this.folderId);
    }

    private void checkSuggestClearDatabase() {
        if (getMessagesStorage().showClearDatabaseAlert) {
            getMessagesStorage().showClearDatabaseAlert = false;
            SuggestClearDatabaseBottomSheet.show(this);
        }
    }

    private void updateMenuButton(boolean animated) {
        float downloadProgress;
        int type;
        if (this.menuDrawable == null || this.updateLayout == null) {
            return;
        }
        if (SharedConfig.isAppUpdateAvailable()) {
            String fileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
            if (getFileLoader().isLoadingFile(fileName)) {
                type = MenuDrawable.TYPE_UDPATE_DOWNLOADING;
                Float p = ImageLoader.getInstance().getFileProgress(fileName);
                downloadProgress = p != null ? p.floatValue() : 0.0f;
            } else {
                type = MenuDrawable.TYPE_UDPATE_AVAILABLE;
                downloadProgress = 0.0f;
            }
        } else {
            type = MenuDrawable.TYPE_DEFAULT;
            downloadProgress = 0.0f;
        }
        updateAppUpdateViews(animated);
        this.menuDrawable.setType(type, animated);
        this.menuDrawable.setUpdateDownloadProgress(downloadProgress, animated);
    }

    private void showNextSupportedSuggestion() {
        if (this.showingSuggestion != null) {
            return;
        }
        for (String suggestion : getMessagesController().pendingSuggestions) {
            if (showSuggestion(suggestion)) {
                this.showingSuggestion = suggestion;
                return;
            }
        }
    }

    private void onSuggestionDismiss() {
        if (this.showingSuggestion == null) {
            return;
        }
        getMessagesController().removeSuggestion(0L, this.showingSuggestion);
        this.showingSuggestion = null;
        showNextSupportedSuggestion();
    }

    private boolean showSuggestion(String suggestion) {
        if ("AUTOARCHIVE_POPULAR".equals(suggestion)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("HideNewChatsAlertTitle", R.string.HideNewChatsAlertTitle));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("HideNewChatsAlertText", R.string.HideNewChatsAlertText)));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString("GoToSettings", R.string.GoToSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.m3384lambda$showSuggestion$46$orgtelegramuiDialogsActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create(), new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda7
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    DialogsActivity.this.m3385lambda$showSuggestion$47$orgtelegramuiDialogsActivity(dialogInterface);
                }
            });
            return true;
        }
        return false;
    }

    /* renamed from: lambda$showSuggestion$46$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3384lambda$showSuggestion$46$orgtelegramuiDialogsActivity(DialogInterface dialog, int which) {
        presentFragment(new PrivacySettingsActivity());
        AndroidUtilities.scrollToFragmentRow(this.parentLayout, "newChatsRow");
    }

    /* renamed from: lambda$showSuggestion$47$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3385lambda$showSuggestion$47$orgtelegramuiDialogsActivity(DialogInterface dialog) {
        onSuggestionDismiss();
    }

    private void showFiltersHint() {
        if (this.askingForPermissions || !getMessagesController().dialogFiltersLoaded || !getMessagesController().showFiltersTooltip || this.filterTabsView == null || !getMessagesController().dialogFilters.isEmpty() || this.isPaused || !getUserConfig().filtersLoaded || this.inPreviewMode) {
            return;
        }
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (preferences.getBoolean("filterhint", false)) {
            return;
        }
        preferences.edit().putBoolean("filterhint", true).commit();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3381lambda$showFiltersHint$49$orgtelegramuiDialogsActivity();
            }
        }, 1000L);
    }

    /* renamed from: lambda$showFiltersHint$48$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3380lambda$showFiltersHint$48$orgtelegramuiDialogsActivity() {
        presentFragment(new FiltersSetupActivity());
    }

    /* renamed from: lambda$showFiltersHint$49$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3381lambda$showFiltersHint$49$orgtelegramuiDialogsActivity() {
        getUndoView().showWithAction(0L, 15, null, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3380lambda$showFiltersHint$48$orgtelegramuiDialogsActivity();
            }
        });
    }

    private void setDialogsListFrozen(boolean frozen, boolean notify) {
        if (this.viewPages == null || this.dialogsListFrozen == frozen) {
            return;
        }
        if (frozen) {
            this.frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.viewPages[0].dialogsType, this.folderId, false));
        } else {
            this.frozenDialogsList = null;
        }
        this.dialogsListFrozen = frozen;
        this.viewPages[0].dialogsAdapter.setDialogsListFrozen(frozen);
        if (!frozen && notify) {
            this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
        }
    }

    public void setDialogsListFrozen(boolean frozen) {
        setDialogsListFrozen(frozen, true);
    }

    /* loaded from: classes4.dex */
    public class DialogsHeader extends TLRPC.Dialog {
        public static final int HEADER_TYPE_GROUPS = 2;
        public static final int HEADER_TYPE_MY_CHANNELS = 0;
        public static final int HEADER_TYPE_MY_GROUPS = 1;
        public int headerType;

        public DialogsHeader(int type) {
            DialogsActivity.this = this$0;
            this.headerType = type;
        }
    }

    public ArrayList<TLRPC.Dialog> getDialogsArray(int currentAccount, int dialogsType, int folderId, boolean frozen) {
        ArrayList<TLRPC.Dialog> arrayList;
        if (frozen && (arrayList = this.frozenDialogsList) != null) {
            return arrayList;
        }
        MessagesController messagesController = AccountInstance.getInstance(currentAccount).getMessagesController();
        if (dialogsType == 0) {
            return messagesController.getDialogs(folderId);
        }
        char c = 1;
        if (dialogsType == 1 || dialogsType == 10 || dialogsType == 13) {
            return messagesController.dialogsServerOnly;
        }
        if (dialogsType == 2) {
            ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>(messagesController.dialogsCanAddUsers.size() + messagesController.dialogsMyChannels.size() + messagesController.dialogsMyGroups.size() + 2);
            if (messagesController.dialogsMyChannels.size() > 0 && this.allowChannels) {
                dialogs.add(new DialogsHeader(0));
                dialogs.addAll(messagesController.dialogsMyChannels);
            }
            if (messagesController.dialogsMyGroups.size() > 0 && this.allowGroups) {
                dialogs.add(new DialogsHeader(1));
                dialogs.addAll(messagesController.dialogsMyGroups);
            }
            if (messagesController.dialogsCanAddUsers.size() > 0) {
                int count = messagesController.dialogsCanAddUsers.size();
                boolean first = true;
                for (int i = 0; i < count; i++) {
                    TLRPC.Dialog dialog = messagesController.dialogsCanAddUsers.get(i);
                    if ((this.allowChannels && ChatObject.isChannelAndNotMegaGroup(-dialog.id, currentAccount)) || (this.allowGroups && (ChatObject.isMegagroup(currentAccount, -dialog.id) || !ChatObject.isChannel(-dialog.id, currentAccount)))) {
                        if (first) {
                            dialogs.add(new DialogsHeader(2));
                            first = false;
                        }
                        dialogs.add(dialog);
                    }
                }
            }
            return dialogs;
        } else if (dialogsType == 3) {
            return messagesController.dialogsForward;
        } else {
            if (dialogsType == 4 || dialogsType == 12) {
                return messagesController.dialogsUsersOnly;
            }
            if (dialogsType == 5) {
                return messagesController.dialogsChannelsOnly;
            }
            if (dialogsType == 6 || dialogsType == 11) {
                return messagesController.dialogsGroupsOnly;
            }
            if (dialogsType == 7 || dialogsType == 8) {
                MessagesController.DialogFilter[] dialogFilterArr = messagesController.selectedDialogFilter;
                if (dialogsType == 7) {
                    c = 0;
                }
                MessagesController.DialogFilter dialogFilter = dialogFilterArr[c];
                if (dialogFilter == null) {
                    return messagesController.getDialogs(folderId);
                }
                return dialogFilter.dialogs;
            } else if (dialogsType == 9) {
                return messagesController.dialogsForBlock;
            } else {
                if (dialogsType == 14) {
                    ArrayList<TLRPC.Dialog> dialogs2 = new ArrayList<>();
                    if (this.allowUsers || this.allowBots) {
                        Iterator<TLRPC.Dialog> it = messagesController.dialogsUsersOnly.iterator();
                        while (it.hasNext()) {
                            TLRPC.Dialog d = it.next();
                            if (messagesController.getUser(Long.valueOf(d.id)).bot) {
                                if (this.allowBots) {
                                    dialogs2.add(d);
                                }
                            } else if (this.allowUsers) {
                                dialogs2.add(d);
                            }
                        }
                    }
                    if (this.allowGroups) {
                        dialogs2.addAll(messagesController.dialogsGroupsOnly);
                    }
                    if (this.allowChannels) {
                        dialogs2.addAll(messagesController.dialogsChannelsOnly);
                    }
                    return dialogs2;
                }
                return new ArrayList<>();
            }
        }
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        recyclerView.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
    }

    public void updatePasscodeButton() {
        if (this.passcodeItem == null) {
            return;
        }
        if (SharedConfig.passcodeHash.length() != 0 && !this.searching) {
            ActionBarMenuItem actionBarMenuItem = this.doneItem;
            if (actionBarMenuItem == null || actionBarMenuItem.getVisibility() != 0) {
                this.passcodeItem.setVisibility(0);
            }
            this.passcodeItem.setIcon(this.passcodeDrawable);
            this.passcodeItemVisible = true;
            return;
        }
        this.passcodeItem.setVisibility(8);
        this.passcodeItemVisible = false;
    }

    private void setFloatingProgressVisible(final boolean visible, boolean animate) {
        if (this.floatingButton == null || this.floatingProgressView == null) {
            return;
        }
        float f = 0.0f;
        float f2 = 0.1f;
        if (animate) {
            if (visible == this.floatingProgressVisible) {
                return;
            }
            AnimatorSet animatorSet = this.floatingProgressAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.floatingProgressVisible = visible;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.floatingProgressAnimator = animatorSet2;
            Animator[] animatorArr = new Animator[6];
            RLottieImageView rLottieImageView = this.floatingButton;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = visible ? 0.0f : 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(rLottieImageView, property, fArr);
            RLottieImageView rLottieImageView2 = this.floatingButton;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = visible ? 0.1f : 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(rLottieImageView2, property2, fArr2);
            RLottieImageView rLottieImageView3 = this.floatingButton;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = visible ? 0.1f : 1.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(rLottieImageView3, property3, fArr3);
            RadialProgressView radialProgressView = this.floatingProgressView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (visible) {
                f = 1.0f;
            }
            fArr4[0] = f;
            animatorArr[3] = ObjectAnimator.ofFloat(radialProgressView, property4, fArr4);
            RadialProgressView radialProgressView2 = this.floatingProgressView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = visible ? 1.0f : 0.1f;
            animatorArr[4] = ObjectAnimator.ofFloat(radialProgressView2, property5, fArr5);
            RadialProgressView radialProgressView3 = this.floatingProgressView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (visible) {
                f2 = 1.0f;
            }
            fArr6[0] = f2;
            animatorArr[5] = ObjectAnimator.ofFloat(radialProgressView3, property6, fArr6);
            animatorSet2.playTogether(animatorArr);
            this.floatingProgressAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.40
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    DialogsActivity.this.floatingProgressView.setVisibility(0);
                    DialogsActivity.this.floatingButton.setVisibility(0);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation == DialogsActivity.this.floatingProgressAnimator) {
                        if (visible) {
                            if (DialogsActivity.this.floatingButton != null) {
                                DialogsActivity.this.floatingButton.setVisibility(8);
                            }
                        } else if (DialogsActivity.this.floatingButton != null) {
                            DialogsActivity.this.floatingProgressView.setVisibility(8);
                        }
                        DialogsActivity.this.floatingProgressAnimator = null;
                    }
                }
            });
            this.floatingProgressAnimator.setDuration(150L);
            this.floatingProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.floatingProgressAnimator.start();
            return;
        }
        AnimatorSet animatorSet3 = this.floatingProgressAnimator;
        if (animatorSet3 != null) {
            animatorSet3.cancel();
        }
        this.floatingProgressVisible = visible;
        if (visible) {
            this.floatingButton.setAlpha(0.0f);
            this.floatingButton.setScaleX(0.1f);
            this.floatingButton.setScaleY(0.1f);
            this.floatingButton.setVisibility(8);
            this.floatingProgressView.setAlpha(1.0f);
            this.floatingProgressView.setScaleX(1.0f);
            this.floatingProgressView.setScaleY(1.0f);
            this.floatingProgressView.setVisibility(0);
            return;
        }
        this.floatingButton.setAlpha(1.0f);
        this.floatingButton.setScaleX(1.0f);
        this.floatingButton.setScaleY(1.0f);
        this.floatingButton.setVisibility(0);
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingProgressView.setVisibility(8);
    }

    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            if (hide && this.floatingForceVisible) {
                return;
            }
            this.floatingHidden = hide;
            AnimatorSet animatorSet = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = this.floatingButtonHideProgress;
            fArr[1] = this.floatingHidden ? 1.0f : 0.0f;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda33
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DialogsActivity.this.m3356lambda$hideFloatingButton$50$orgtelegramuiDialogsActivity(valueAnimator2);
                }
            });
            animatorSet.playTogether(valueAnimator);
            animatorSet.setDuration(300L);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!hide);
            animatorSet.start();
        }
    }

    /* renamed from: lambda$hideFloatingButton$50$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3356lambda$hideFloatingButton$50$orgtelegramuiDialogsActivity(ValueAnimator animation) {
        this.floatingButtonHideProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = AndroidUtilities.dp(100.0f) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    public float getContactsAlpha() {
        return this.contactsAlpha;
    }

    public void animateContactsAlpha(float alpha) {
        ValueAnimator valueAnimator = this.contactsAlphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator duration = ValueAnimator.ofFloat(this.contactsAlpha, alpha).setDuration(250L);
        this.contactsAlphaAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.contactsAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsActivity.this.m3333lambda$animateContactsAlpha$51$orgtelegramuiDialogsActivity(valueAnimator2);
            }
        });
        this.contactsAlphaAnimator.start();
    }

    /* renamed from: lambda$animateContactsAlpha$51$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3333lambda$animateContactsAlpha$51$orgtelegramuiDialogsActivity(ValueAnimator animation) {
        setContactsAlpha(((Float) animation.getAnimatedValue()).floatValue());
    }

    public void setContactsAlpha(float alpha) {
        ViewPage[] viewPageArr;
        this.contactsAlpha = alpha;
        for (ViewPage p : this.viewPages) {
            RecyclerListView listView = p.listView;
            for (int i = 0; i < listView.getChildCount(); i++) {
                View v = listView.getChildAt(i);
                if (listView.getChildAdapterPosition(v) >= p.dialogsAdapter.getDialogsCount() + 1) {
                    v.setAlpha(alpha);
                }
            }
        }
    }

    public void setScrollDisabled(boolean disable) {
        ViewPage[] viewPageArr;
        for (ViewPage p : this.viewPages) {
            LinearLayoutManager llm = (LinearLayoutManager) p.listView.getLayoutManager();
            llm.setScrollDisabled(disable);
        }
    }

    public void updateDialogIndices() {
        int index;
        if (this.viewPages == null) {
            return;
        }
        int b = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (b < viewPageArr.length) {
                if (viewPageArr[b].getVisibility() == 0) {
                    ArrayList<TLRPC.Dialog> dialogs = getDialogsArray(this.currentAccount, this.viewPages[b].dialogsType, this.folderId, false);
                    int count = this.viewPages[b].listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = this.viewPages[b].listView.getChildAt(a);
                        if (child instanceof DialogCell) {
                            DialogCell dialogCell = (DialogCell) child;
                            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                            if (dialog != null && (index = dialogs.indexOf(dialog)) >= 0) {
                                dialogCell.setDialogIndex(index);
                            }
                        }
                    }
                }
                b++;
            } else {
                return;
            }
        }
    }

    public void updateVisibleRows(int mask) {
        updateVisibleRows(mask, true);
    }

    private void updateVisibleRows(int mask, boolean animated) {
        RecyclerListView list;
        if ((this.dialogsListFrozen && (MessagesController.UPDATE_MASK_REORDER & mask) == 0) || this.isPaused) {
            return;
        }
        for (int c = 0; c < 3; c++) {
            RecyclerListView recyclerListView = null;
            if (c == 2) {
                SearchViewPager searchViewPager = this.searchViewPager;
                if (searchViewPager != null) {
                    recyclerListView = searchViewPager.searchListView;
                }
                list = recyclerListView;
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr != null) {
                    if (c < viewPageArr.length) {
                        recyclerListView = viewPageArr[c].listView;
                    }
                    list = recyclerListView;
                    if (list != null && this.viewPages[c].getVisibility() != 0) {
                    }
                }
            }
            if (list != null) {
                int count = list.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = list.getChildAt(a);
                    if ((child instanceof DialogCell) && list.getAdapter() != this.searchViewPager.dialogsSearchAdapter) {
                        DialogCell cell = (DialogCell) child;
                        boolean z = true;
                        if ((MessagesController.UPDATE_MASK_REORDER & mask) != 0) {
                            cell.onReorderStateChanged(this.actionBar.isActionModeShowed(), true);
                            if (this.dialogsListFrozen) {
                            }
                        }
                        if ((MessagesController.UPDATE_MASK_CHECK & mask) != 0) {
                            if ((MessagesController.UPDATE_MASK_CHAT & mask) == 0) {
                                z = false;
                            }
                            cell.setChecked(false, z);
                        } else {
                            if ((MessagesController.UPDATE_MASK_NEW_MESSAGE & mask) != 0) {
                                cell.checkCurrentDialogIndex(this.dialogsListFrozen);
                                if (this.viewPages[c].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                    if (cell.getDialogId() != this.openedDialogId) {
                                        z = false;
                                    }
                                    cell.setDialogSelected(z);
                                }
                            } else if ((MessagesController.UPDATE_MASK_SELECT_DIALOG & mask) != 0) {
                                if (this.viewPages[c].isDefaultDialogType() && AndroidUtilities.isTablet()) {
                                    if (cell.getDialogId() != this.openedDialogId) {
                                        z = false;
                                    }
                                    cell.setDialogSelected(z);
                                }
                            } else {
                                cell.update(mask, animated);
                            }
                            ArrayList<Long> arrayList = this.selectedDialogs;
                            if (arrayList != null) {
                                cell.setChecked(arrayList.contains(Long.valueOf(cell.getDialogId())), false);
                            }
                        }
                    }
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(mask);
                    } else if (child instanceof ProfileSearchCell) {
                        ProfileSearchCell cell2 = (ProfileSearchCell) child;
                        cell2.update(mask);
                        ArrayList<Long> arrayList2 = this.selectedDialogs;
                        if (arrayList2 != null) {
                            cell2.setChecked(arrayList2.contains(Long.valueOf(cell2.getDialogId())), false);
                        }
                    }
                    if (!this.dialogsListFrozen && (child instanceof RecyclerListView)) {
                        RecyclerListView innerListView = (RecyclerListView) child;
                        int count2 = innerListView.getChildCount();
                        for (int b = 0; b < count2; b++) {
                            View child2 = innerListView.getChildAt(b);
                            if (child2 instanceof HintDialogCell) {
                                ((HintDialogCell) child2).update(mask);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public boolean shouldShowNextButton(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        return false;
    }

    public void setSearchString(String string) {
        this.searchString = string;
    }

    public void setInitialSearchString(String initialSearchString) {
        this.initialSearchString = initialSearchString;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    public void setInitialSearchType(int type) {
        this.initialSearchType = type;
    }

    private boolean checkCanWrite(long dialogId) {
        if (this.addToGroupAlertString == null && this.checkCanWrite) {
            if (DialogObject.isChatDialog(dialogId)) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    return true;
                }
                if (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-dialogId, this.currentAccount) || this.hasPoll == 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle));
                    if (this.hasPoll == 2) {
                        builder.setMessage(LocaleController.getString("PublicPollCantForward", R.string.PublicPollCantForward));
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                    }
                    builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                    showDialog(builder.create());
                    return false;
                }
                return true;
            } else if (!DialogObject.isEncryptedDialog(dialogId)) {
                return true;
            } else {
                if (this.hasPoll != 0 || this.hasInvoice) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                    builder2.setTitle(LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle));
                    if (this.hasPoll != 0) {
                        builder2.setMessage(LocaleController.getString("PollCantForwardSecretChat", R.string.PollCantForwardSecretChat));
                    } else {
                        builder2.setMessage(LocaleController.getString("InvoiceCantForwardSecretChat", R.string.InvoiceCantForwardSecretChat));
                    }
                    builder2.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                    showDialog(builder2.create());
                    return false;
                }
                return true;
            }
        }
        return true;
    }

    public void didSelectResult(final long dialogId, boolean useAlert, final boolean param) {
        TLRPC.Chat chat;
        TLRPC.User user;
        String message;
        String title;
        String buttonText;
        if (!checkCanWrite(dialogId)) {
            return;
        }
        int i = this.initialDialogsType;
        if (i != 11 && i != 12) {
            if (i != 13) {
                if (useAlert && ((this.selectAlertString != null && this.selectAlertStringGroup != null) || this.addToGroupAlertString != null)) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    if (DialogObject.isEncryptedDialog(dialogId)) {
                        TLRPC.EncryptedChat chat2 = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
                        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(chat2.user_id));
                        if (user2 == null) {
                            return;
                        }
                        title = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                        message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                        buttonText = LocaleController.getString("Send", R.string.Send);
                    } else if (DialogObject.isUserDialog(dialogId)) {
                        if (dialogId == getUserConfig().getClientUserId()) {
                            title = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                            message = LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", R.string.SavedMessages));
                            buttonText = LocaleController.getString("Send", R.string.Send);
                        } else {
                            TLRPC.User user3 = getMessagesController().getUser(Long.valueOf(dialogId));
                            if (user3 == null || this.selectAlertString == null) {
                                return;
                            }
                            title = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                            message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user3));
                            buttonText = LocaleController.getString("Send", R.string.Send);
                        }
                    } else {
                        TLRPC.Chat chat3 = getMessagesController().getChat(Long.valueOf(-dialogId));
                        if (chat3 == null) {
                            return;
                        }
                        if (this.addToGroupAlertString != null) {
                            title = LocaleController.getString("AddToTheGroupAlertTitle", R.string.AddToTheGroupAlertTitle);
                            message = LocaleController.formatStringSimple(this.addToGroupAlertString, chat3.title);
                            buttonText = LocaleController.getString("Add", R.string.Add);
                        } else {
                            title = LocaleController.getString("SendMessageTitle", R.string.SendMessageTitle);
                            message = LocaleController.formatStringSimple(this.selectAlertStringGroup, chat3.title);
                            buttonText = LocaleController.getString("Send", R.string.Send);
                        }
                    }
                    builder.setTitle(title);
                    builder.setMessage(AndroidUtilities.replaceTags(message));
                    builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda2
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            DialogsActivity.this.m3352lambda$didSelectResult$55$orgtelegramuiDialogsActivity(dialogId, dialogInterface, i2);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                } else if (this.delegate != null) {
                    ArrayList<Long> dids = new ArrayList<>();
                    dids.add(Long.valueOf(dialogId));
                    this.delegate.didSelectDialogs(this, dids, null, param);
                    if (this.resetDelegate) {
                        this.delegate = null;
                        return;
                    }
                    return;
                } else {
                    finishFragment();
                    return;
                }
            }
        }
        if (this.checkingImportDialog) {
            return;
        }
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user4 = getMessagesController().getUser(Long.valueOf(dialogId));
            if (user4.mutual_contact) {
                user = user4;
                chat = null;
            } else {
                getUndoView().showWithAction(dialogId, 45, (Runnable) null);
                return;
            }
        } else {
            TLRPC.Chat chat4 = getMessagesController().getChat(Long.valueOf(-dialogId));
            if (ChatObject.hasAdminRights(chat4) && ChatObject.canChangeChatInfo(chat4)) {
                user = null;
                chat = chat4;
            } else {
                getUndoView().showWithAction(dialogId, 46, (Runnable) null);
                return;
            }
        }
        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        final TLRPC.TL_messages_checkHistoryImportPeer req = new TLRPC.TL_messages_checkHistoryImportPeer();
        req.peer = getMessagesController().getInputPeer(dialogId);
        final TLRPC.User user5 = user;
        final TLRPC.Chat chat5 = chat;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda46
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                DialogsActivity.this.m3351lambda$didSelectResult$54$orgtelegramuiDialogsActivity(progressDialog, user5, chat5, dialogId, param, req, tLObject, tL_error);
            }
        });
        try {
            progressDialog.showDelayed(300L);
        } catch (Exception e) {
        }
    }

    /* renamed from: lambda$didSelectResult$54$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3351lambda$didSelectResult$54$orgtelegramuiDialogsActivity(final AlertDialog progressDialog, final TLRPC.User user, final TLRPC.Chat chat, final long dialogId, final boolean param, final TLRPC.TL_messages_checkHistoryImportPeer req, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3350lambda$didSelectResult$53$orgtelegramuiDialogsActivity(progressDialog, response, user, chat, dialogId, param, error, req);
            }
        });
    }

    /* renamed from: lambda$didSelectResult$53$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3350lambda$didSelectResult$53$orgtelegramuiDialogsActivity(AlertDialog progressDialog, TLObject response, TLRPC.User user, TLRPC.Chat chat, final long dialogId, final boolean param, TLRPC.TL_error error, TLRPC.TL_messages_checkHistoryImportPeer req) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.checkingImportDialog = false;
        if (response == null) {
            AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
            getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(dialogId), req, error);
            return;
        }
        TLRPC.TL_messages_checkedHistoryImportPeer res = (TLRPC.TL_messages_checkedHistoryImportPeer) response;
        AlertsCreator.createImportDialogAlert(this, this.arguments.getString("importTitle"), res.confirm_text, user, chat, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                DialogsActivity.this.m3349lambda$didSelectResult$52$orgtelegramuiDialogsActivity(dialogId, param);
            }
        });
    }

    /* renamed from: lambda$didSelectResult$52$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3349lambda$didSelectResult$52$orgtelegramuiDialogsActivity(long dialogId, boolean param) {
        setDialogsListFrozen(true);
        ArrayList<Long> dids = new ArrayList<>();
        dids.add(Long.valueOf(dialogId));
        this.delegate.didSelectDialogs(this, dids, null, param);
    }

    /* renamed from: lambda$didSelectResult$55$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3352lambda$didSelectResult$55$orgtelegramuiDialogsActivity(long dialogId, DialogInterface dialogInterface, int i) {
        didSelectResult(dialogId, false, false);
    }

    public RLottieImageView getFloatingButton() {
        return this.floatingButton;
    }

    private boolean onSendLongClick(View view) {
        Activity parentActivity = getParentActivity();
        Theme.ResourcesProvider resourcesProvider = getResourceProvider();
        if (parentActivity == null) {
            return false;
        }
        LinearLayout layout = new LinearLayout(parentActivity);
        layout.setOrientation(1);
        ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(parentActivity, resourcesProvider);
        sendPopupLayout2.setAnimationEnabled(false);
        sendPopupLayout2.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.DialogsActivity.41
            private Rect popupRect = new Rect();

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == 0 && DialogsActivity.this.sendPopupWindow != null && DialogsActivity.this.sendPopupWindow.isShowing()) {
                    v.getHitRect(this.popupRect);
                    if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                        DialogsActivity.this.sendPopupWindow.dismiss();
                        return false;
                    }
                    return false;
                }
                return false;
            }
        });
        sendPopupLayout2.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda47
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                DialogsActivity.this.m3364lambda$onSendLongClick$56$orgtelegramuiDialogsActivity(keyEvent);
            }
        });
        sendPopupLayout2.setShownFromBottom(false);
        sendPopupLayout2.setupRadialSelectors(getThemedColor(Theme.key_dialogButtonSelector));
        ActionBarMenuSubItem sendWithoutSound = new ActionBarMenuSubItem((Context) parentActivity, true, true, resourcesProvider);
        sendWithoutSound.setTextAndIcon(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound), R.drawable.input_notify_off);
        sendWithoutSound.setMinimumWidth(AndroidUtilities.dp(196.0f));
        sendPopupLayout2.addView((View) sendWithoutSound, LayoutHelper.createLinear(-1, 48));
        sendWithoutSound.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DialogsActivity.this.m3365lambda$onSendLongClick$57$orgtelegramuiDialogsActivity(view2);
            }
        });
        layout.addView(sendPopupLayout2, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(layout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSoundHint();
        layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int y = (location[1] - layout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f);
        this.sendPopupWindow.showAtLocation(view, 51, ((location[0] + view.getMeasuredWidth()) - layout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), y);
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$onSendLongClick$56$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3364lambda$onSendLongClick$56$orgtelegramuiDialogsActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$onSendLongClick$57$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3365lambda$onSendLongClick$57$orgtelegramuiDialogsActivity(View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        this.notify = false;
        if (this.delegate != null && !this.selectedDialogs.isEmpty()) {
            this.delegate.didSelectDialogs(this, this.selectedDialogs, this.commentView.getFieldText(), false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x0818 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x03fe  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.ArrayList<org.telegram.ui.ActionBar.ThemeDescription> getThemeDescriptions() {
        /*
            Method dump skipped, instructions count: 6666
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.getThemeDescriptions():java.util.ArrayList");
    }

    /* JADX WARN: Removed duplicated region for block: B:104:0x0050 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0022  */
    /* renamed from: lambda$getThemeDescriptions$58$org-telegram-ui-DialogsActivity */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m3354lambda$getThemeDescriptions$58$orgtelegramuiDialogsActivity() {
        /*
            Method dump skipped, instructions count: 439
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.m3354lambda$getThemeDescriptions$58$orgtelegramuiDialogsActivity():void");
    }

    private void updateFloatingButtonColor() {
        if (getParentActivity() == null || this.floatingButtonContainer == null) {
            return;
        }
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Animator getCustomSlideTransition(boolean topFragment, boolean backAnimation, float distanceToMove) {
        if (backAnimation) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.slideFragmentProgress, 1.0f);
            this.slideBackTransitionAnimator = ofFloat;
            return ofFloat;
        }
        int duration = (int) (Math.max((int) ((200.0f / getLayoutContainer().getMeasuredWidth()) * distanceToMove), 80) * 1.2f);
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.slideFragmentProgress, 1.0f);
        this.slideBackTransitionAnimator = ofFloat2;
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda11
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DialogsActivity.this.m3353x179b64cc(valueAnimator);
            }
        });
        this.slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.slideBackTransitionAnimator.setDuration(duration);
        this.slideBackTransitionAnimator.start();
        return this.slideBackTransitionAnimator;
    }

    /* renamed from: lambda$getCustomSlideTransition$59$org-telegram-ui-DialogsActivity */
    public /* synthetic */ void m3353x179b64cc(ValueAnimator valueAnimator) {
        setSlideTransitionProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void prepareFragmentToSlide(boolean topFragment, boolean beginSlide) {
        if (!topFragment && beginSlide) {
            this.isSlideBackTransition = true;
            setFragmentIsSliding(true);
            return;
        }
        this.slideBackTransitionAnimator = null;
        this.isSlideBackTransition = false;
        setFragmentIsSliding(false);
        setSlideTransitionProgress(1.0f);
    }

    private void setFragmentIsSliding(boolean sliding) {
        if (SharedConfig.getDevicePerformanceClass() == 0) {
            return;
        }
        if (sliding) {
            ViewPage[] viewPageArr = this.viewPages;
            if (viewPageArr != null && viewPageArr[0] != null) {
                viewPageArr[0].setLayerType(2, null);
                this.viewPages[0].setClipChildren(false);
                this.viewPages[0].setClipToPadding(false);
                this.viewPages[0].listView.setClipChildren(false);
            }
            if (this.actionBar != null) {
                this.actionBar.setLayerType(2, null);
            }
            FilterTabsView filterTabsView = this.filterTabsView;
            if (filterTabsView != null) {
                filterTabsView.getListView().setLayerType(2, null);
            }
            if (this.fragmentView != null) {
                ((ViewGroup) this.fragmentView).setClipChildren(false);
                this.fragmentView.requestLayout();
                return;
            }
            return;
        }
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr2 = this.viewPages;
            if (i >= viewPageArr2.length) {
                break;
            }
            ViewPage page = viewPageArr2[i];
            if (page != null) {
                page.setLayerType(0, null);
                page.setClipChildren(true);
                page.setClipToPadding(true);
                page.listView.setClipChildren(true);
            }
            i++;
        }
        if (this.actionBar != null) {
            this.actionBar.setLayerType(0, null);
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 != null) {
            filterTabsView2.getListView().setLayerType(0, null);
        }
        if (this.fragmentView != null) {
            ((ViewGroup) this.fragmentView).setClipChildren(true);
            this.fragmentView.requestLayout();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onSlideProgress(boolean isOpen, float progress) {
        if (SharedConfig.getDevicePerformanceClass() != 0 && this.isSlideBackTransition && this.slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(progress);
        }
    }

    private void setSlideTransitionProgress(float progress) {
        if (SharedConfig.getDevicePerformanceClass() == 0) {
            return;
        }
        this.slideFragmentProgress = progress;
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null) {
            float s = 1.0f - ((1.0f - this.slideFragmentProgress) * 0.05f);
            filterTabsView.getListView().setScaleX(s);
            this.filterTabsView.getListView().setScaleY(s);
            this.filterTabsView.getListView().setTranslationX((this.isDrawerTransition ? AndroidUtilities.dp(4.0f) : -AndroidUtilities.dp(4.0f)) * (1.0f - this.slideFragmentProgress));
            this.filterTabsView.getListView().setPivotX(this.isDrawerTransition ? this.filterTabsView.getMeasuredWidth() : 0.0f);
            this.filterTabsView.getListView().setPivotY(0.0f);
            this.filterTabsView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void setProgressToDrawerOpened(float progress) {
        if (SharedConfig.getDevicePerformanceClass() == 0 || this.isSlideBackTransition) {
            return;
        }
        boolean drawerTransition = progress > 0.0f;
        if (this.searchIsShowed) {
            drawerTransition = false;
            progress = 0.0f;
        }
        if (drawerTransition != this.isDrawerTransition) {
            this.isDrawerTransition = drawerTransition;
            if (drawerTransition) {
                setFragmentIsSliding(true);
            } else {
                setFragmentIsSliding(false);
            }
            if (this.fragmentView != null) {
                this.fragmentView.requestLayout();
            }
        }
        setSlideTransitionProgress(1.0f - progress);
    }

    public void setShowSearch(String query, int i) {
        if (!this.searching) {
            this.initialSearchType = i;
            this.actionBar.openSearchField(query, false);
            return;
        }
        if (!this.searchItem.getSearchField().getText().toString().equals(query)) {
            this.searchItem.getSearchField().setText(query);
        }
        int p = this.searchViewPager.getPositionForType(i);
        if (p >= 0 && this.searchViewPager.getTabsView().getCurrentTabId() != p) {
            this.searchViewPager.getTabsView().scrollToTab(p, p);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor((!this.searching || !this.whiteActionBar) ? this.folderId == 0 ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived : Theme.key_windowBackgroundWhite);
        if (this.actionBar.isActionModeShowed()) {
            color = Theme.getColor(Theme.key_actionBarActionModeDefault);
        }
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
