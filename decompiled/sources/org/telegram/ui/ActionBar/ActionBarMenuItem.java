package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
/* loaded from: classes4.dex */
public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    private boolean animateClear;
    private boolean animationEnabled;
    private ImageView clearButton;
    private ArrayList<FiltersView.MediaFilterData> currentSearchFilters;
    private ActionBarMenuItemDelegate delegate;
    private boolean forceSmoothKeyboard;
    protected RLottieImageView iconView;
    private boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    protected ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled;
    private boolean measurePopup;
    private int notificationIndex;
    private View.OnClickListener onClickListener;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private FrameLayout searchContainer;
    AnimatorSet searchContainerAnimator;
    private EditTextBoldCursor searchField;
    private TextView searchFieldCaption;
    private LinearLayout searchFilterLayout;
    private ArrayList<SearchFilterView> searchFilterViews;
    private int selectedFilterIndex;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private View showSubMenuFrom;
    private boolean showSubmenuByMove;
    private ActionBarSubMenuItemDelegate subMenuDelegate;
    private int subMenuOpenSide;
    protected TextView textView;
    private float transitionOffset;
    private FrameLayout wrappedSearchFrameLayout;
    private int yOffset;

    /* loaded from: classes4.dex */
    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    /* loaded from: classes4.dex */
    public interface ActionBarSubMenuItemDelegate {
        void onHideSubMenu();

        void onShowSubMenu();
    }

    /* loaded from: classes4.dex */
    public static class ActionBarMenuItemSearchListener {
        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {
        }

        public void onTextChanged(EditText editText) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onCaptionCleared() {
        }

        public boolean forceShowClear() {
            return false;
        }

        public Animator getCustomToggleTransition() {
            return null;
        }

        public void onLayout(int l, int t, int r, int b) {
        }

        public void onSearchFilterCleared(FiltersView.MediaFilterData filterData) {
        }

        public boolean canToggleSearch() {
            return true;
        }
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor) {
        this(context, menu, backgroundColor, iconColor, false);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, Theme.ResourcesProvider resourcesProvider) {
        this(context, menu, backgroundColor, iconColor, false, resourcesProvider);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, boolean text) {
        this(context, menu, backgroundColor, iconColor, text, null);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu menu, int backgroundColor, int iconColor, boolean text, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.searchFilterViews = new ArrayList<>();
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.animateClear = true;
        this.measurePopup = true;
        this.showSubmenuByMove = true;
        this.currentSearchFilters = new ArrayList<>();
        this.selectedFilterIndex = -1;
        this.notificationIndex = -1;
        this.resourcesProvider = resourcesProvider;
        if (backgroundColor != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(backgroundColor, text ? 5 : 1));
        }
        this.parentMenu = menu;
        if (text) {
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 15.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            this.textView.setImportantForAccessibility(2);
            if (iconColor != 0) {
                this.textView.setTextColor(iconColor);
            }
            addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
            return;
        }
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.iconView.setImportantForAccessibility(2);
        addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
        if (iconColor != 0) {
            this.iconView.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override // android.view.View
    public void setTranslationX(float translationX) {
        super.setTranslationX(this.transitionOffset + translationX);
    }

    public void setLongClickEnabled(boolean value) {
        this.longClickEnabled = value;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        ActionBarPopupWindow actionBarPopupWindow3;
        if (event.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow3 = this.popupWindow) == null || !actionBarPopupWindow3.isShowing())) {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActionBarMenuItem.this.m1396x5aa2d208();
                    }
                };
                this.showMenuRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 200L);
            }
        } else if (event.getActionMasked() == 2) {
            if (this.showSubmenuByMove && hasSubMenu() && ((actionBarPopupWindow2 = this.popupWindow) == null || !actionBarPopupWindow2.isShowing())) {
                if (event.getY() > getHeight()) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    toggleSubMenu();
                    return true;
                }
            } else if (this.showSubmenuByMove && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
                getLocationOnScreen(this.location);
                float x = event.getX() + this.location[0];
                float y = event.getY();
                int[] iArr = this.location;
                float y2 = y + iArr[1];
                this.popupLayout.getLocationOnScreen(iArr);
                int[] iArr2 = this.location;
                float x2 = x - iArr2[0];
                float y3 = y2 - iArr2[1];
                this.selectedMenuView = null;
                for (int a = 0; a < this.popupLayout.getItemsCount(); a++) {
                    View child = this.popupLayout.getItemAt(a);
                    child.getHitRect(this.rect);
                    Object tag = child.getTag();
                    if ((tag instanceof Integer) && ((Integer) tag).intValue() < 100) {
                        if (!this.rect.contains((int) x2, (int) y3)) {
                            child.setPressed(false);
                            child.setSelected(false);
                            if (Build.VERSION.SDK_INT == 21 && child.getBackground() != null) {
                                child.getBackground().setVisible(false, false);
                            }
                        } else {
                            child.setPressed(true);
                            child.setSelected(true);
                            if (Build.VERSION.SDK_INT >= 21) {
                                if (Build.VERSION.SDK_INT == 21 && child.getBackground() != null) {
                                    child.getBackground().setVisible(true, false);
                                }
                                child.drawableHotspotChanged(x2, y3 - child.getTop());
                            }
                            this.selectedMenuView = child;
                        }
                    }
                }
            }
        } else {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 != null && actionBarPopupWindow4.isShowing() && event.getActionMasked() == 1) {
                View view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    ActionBarMenu actionBarMenu = this.parentMenu;
                    if (actionBarMenu != null) {
                        actionBarMenu.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                    } else {
                        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
                        if (actionBarMenuItemDelegate != null) {
                            actionBarMenuItemDelegate.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                        }
                    }
                    this.popupWindow.dismiss(this.allowCloseAnimation);
                } else if (this.showSubmenuByMove) {
                    this.popupWindow.dismiss();
                }
            } else {
                View view2 = this.selectedMenuView;
                if (view2 != null) {
                    view2.setSelected(false);
                    this.selectedMenuView = null;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1396x5aa2d208() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        toggleSubMenu();
    }

    public void setDelegate(ActionBarMenuItemDelegate actionBarMenuItemDelegate) {
        this.delegate = actionBarMenuItemDelegate;
    }

    public void setSubMenuDelegate(ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate) {
        this.subMenuDelegate = actionBarSubMenuItemDelegate;
    }

    public void setShowSubmenuByMove(boolean value) {
        this.showSubmenuByMove = value;
    }

    public void setIconColor(int color) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView = this.textView;
        if (textView != null) {
            textView.setTextColor(color);
        }
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setSubMenuOpenSide(int side) {
        this.subMenuOpenSide = side;
    }

    public void setLayoutInScreen(boolean value) {
        this.layoutInScreen = value;
    }

    public void setForceSmoothKeyboard(boolean value) {
        this.forceSmoothKeyboard = value;
    }

    private void createPopupLayout() {
        if (this.popupLayout != null) {
            return;
        }
        this.rect = new Rect();
        this.location = new int[2];
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert2, this.resourcesProvider, 1);
        this.popupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda13
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ActionBarMenuItem.this.m1392x2a5ff32f(view, motionEvent);
            }
        });
        this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                ActionBarMenuItem.this.m1393xc500b5b0(keyEvent);
            }
        });
        if (this.popupLayout.getSwipeBack() != null) {
            this.popupLayout.getSwipeBack().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ActionBarMenuItem.this.m1394x5fa17831(view);
                }
            });
        }
    }

    /* renamed from: lambda$createPopupLayout$1$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ boolean m1392x2a5ff32f(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            v.getHitRect(this.rect);
            if (!this.rect.contains((int) event.getX(), (int) event.getY())) {
                this.popupWindow.dismiss();
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$createPopupLayout$2$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1393xc500b5b0(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* renamed from: lambda$createPopupLayout$3$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1394x5fa17831(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public void removeAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        actionBarPopupWindowLayout.removeInnerViews();
    }

    public void setShowedFromBottom(boolean value) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        actionBarPopupWindowLayout.setShownFromBottom(value);
    }

    public void addSubItem(View view, int width, int height) {
        createPopupLayout();
        this.popupLayout.addView(view, new LinearLayout.LayoutParams(width, height));
    }

    public void addSubItem(int id, View view, int width, int height) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(id));
        view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ActionBarMenuItem.this.m1387lambda$addSubItem$4$orgtelegramuiActionBarActionBarMenuItem(view2);
            }
        });
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    /* renamed from: lambda$addSubItem$4$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1387lambda$addSubItem$4$orgtelegramuiActionBarActionBarMenuItem(View view1) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view1.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view1.getTag()).intValue());
        }
    }

    public TextView addSubItem(int id, CharSequence text) {
        createPopupLayout();
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!LocaleController.isRTL) {
            textView.setGravity(16);
        } else {
            textView.setGravity(21);
        }
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView.setTextSize(1, 16.0f);
        textView.setMinWidth(AndroidUtilities.dp(196.0f));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTag(Integer.valueOf(id));
        textView.setText(text);
        this.popupLayout.addView(textView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionBarMenuItem.this.m1388lambda$addSubItem$5$orgtelegramuiActionBarActionBarMenuItem(view);
            }
        });
        return textView;
    }

    /* renamed from: lambda$addSubItem$5$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1388lambda$addSubItem$5$orgtelegramuiActionBarActionBarMenuItem(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            if (!this.allowCloseAnimation) {
                this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
            }
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text) {
        return addSubItem(id, icon, null, text, true, false);
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text, Theme.ResourcesProvider resourcesProvider) {
        return addSubItem(id, icon, null, text, true, false, resourcesProvider);
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, CharSequence text, boolean needCheck) {
        return addSubItem(id, icon, null, text, true, needCheck);
    }

    public View addGap(int id) {
        createPopupLayout();
        View cell = new View(getContext());
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        cell.setTag(Integer.valueOf(id));
        cell.setTag(R.id.object_tag, 1);
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(6.0f);
        cell.setLayoutParams(layoutParams);
        return cell;
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, Drawable iconDrawable, CharSequence text, boolean dismiss, boolean needCheck) {
        return addSubItem(id, icon, iconDrawable, text, dismiss, needCheck, null);
    }

    public ActionBarMenuSubItem addSubItem(int id, int icon, Drawable iconDrawable, CharSequence text, final boolean dismiss, boolean needCheck, Theme.ResourcesProvider resourcesProvider) {
        createPopupLayout();
        ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext(), needCheck, false, false, resourcesProvider);
        cell.setTextAndIcon(text, icon, iconDrawable);
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        cell.setTag(Integer.valueOf(id));
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        cell.setLayoutParams(layoutParams);
        cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionBarMenuItem.this.m1389lambda$addSubItem$6$orgtelegramuiActionBarActionBarMenuItem(dismiss, view);
            }
        });
        return cell;
    }

    /* renamed from: lambda$addSubItem$6$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1389lambda$addSubItem$6$orgtelegramuiActionBarActionBarMenuItem(boolean dismiss, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing() && dismiss) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public ActionBarMenuSubItem addSwipeBackItem(int icon, Drawable iconDrawable, String text, View viewToSwipeBack) {
        createPopupLayout();
        final ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext(), false, false, false, this.resourcesProvider);
        cell.setTextAndIcon(text, icon, iconDrawable);
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        cell.setRightIcon(R.drawable.msg_arrowright);
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        cell.setLayoutParams(layoutParams);
        final int swipeBackIndex = this.popupLayout.addViewToSwipeBack(viewToSwipeBack);
        cell.openSwipeBackLayout = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ActionBarMenuItem.this.m1390xd1635648(swipeBackIndex);
            }
        };
        cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionBarMenuSubItem.this.openSwipeBack();
            }
        });
        this.popupLayout.swipeBackGravityRight = true;
        return cell;
    }

    /* renamed from: lambda$addSwipeBackItem$7$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1390xd1635648(int swipeBackIndex) {
        if (this.popupLayout.getSwipeBack() != null) {
            this.popupLayout.getSwipeBack().openForeground(swipeBackIndex);
        }
    }

    public View addDivider(int color) {
        createPopupLayout();
        TextView cell = new TextView(getContext());
        cell.setBackgroundColor(color);
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        this.popupLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = 1;
        int dp = AndroidUtilities.dp(3.0f);
        layoutParams.bottomMargin = dp;
        layoutParams.topMargin = dp;
        cell.setLayoutParams(layoutParams);
        return cell;
    }

    public void redrawPopup(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && actionBarPopupWindowLayout.getBackgroundColor() != color) {
            this.popupLayout.setBackgroundColor(color);
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupLayout.invalidate();
            }
        }
    }

    public void setPopupItemsColor(int color, boolean icon) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        LinearLayout layout = actionBarPopupWindowLayout.linearLayout;
        int count = layout.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = layout.getChildAt(a);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);
            } else if (child instanceof ActionBarMenuSubItem) {
                if (icon) {
                    ((ActionBarMenuSubItem) child).setIconColor(color);
                } else {
                    ((ActionBarMenuSubItem) child).setTextColor(color);
                }
            }
        }
    }

    public void setPopupItemsSelectorColor(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        LinearLayout layout = actionBarPopupWindowLayout.linearLayout;
        int count = layout.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = layout.getChildAt(a);
            if (child instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) child).setSelectorColor(color);
            }
        }
    }

    public void setupPopupRadialSelectors(int color) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setupRadialSelectors(color);
        }
    }

    public boolean hasSubMenu() {
        return this.popupLayout != null;
    }

    public ActionBarPopupWindow.ActionBarPopupWindowLayout getPopupLayout() {
        if (this.popupLayout == null) {
            createPopupLayout();
        }
        return this.popupLayout;
    }

    public void setMenuYOffset(int offset) {
        this.yOffset = offset;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void toggleSubMenu(final View topView, View fromView) {
        if (this.popupLayout != null) {
            ActionBarMenu actionBarMenu = this.parentMenu;
            if (actionBarMenu != null && actionBarMenu.isActionMode && this.parentMenu.parentActionBar != null && !this.parentMenu.parentActionBar.isActionModeShowed()) {
                return;
            }
            Runnable runnable = this.showMenuRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.showMenuRunnable = null;
            }
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
                this.showSubMenuFrom = fromView;
                ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
                if (actionBarSubMenuItemDelegate != null) {
                    actionBarSubMenuItemDelegate.onShowSubMenu();
                }
                if (this.popupLayout.getParent() != null) {
                    ((ViewGroup) this.popupLayout.getParent()).removeView(this.popupLayout);
                }
                ViewGroup container = this.popupLayout;
                if (topView != null) {
                    LinearLayout linearLayout = new LinearLayout(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.1
                        @Override // android.widget.LinearLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            ActionBarMenuItem.this.popupLayout.measure(widthMeasureSpec, heightMeasureSpec);
                            if (ActionBarMenuItem.this.popupLayout.getSwipeBack() != null) {
                                topView.getLayoutParams().width = ActionBarMenuItem.this.popupLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
                            } else {
                                topView.getLayoutParams().width = ActionBarMenuItem.this.popupLayout.getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                            }
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }
                    };
                    linearLayout.setOrientation(1);
                    FrameLayout frameLayout = new FrameLayout(getContext());
                    frameLayout.setAlpha(0.0f);
                    frameLayout.animate().alpha(1.0f).setDuration(100L).start();
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert2).mutate();
                    drawable.setColorFilter(new PorterDuffColorFilter(this.popupLayout.getBackgroundColor(), PorterDuff.Mode.MULTIPLY));
                    frameLayout.setBackground(drawable);
                    frameLayout.addView(topView);
                    linearLayout.addView(frameLayout, LayoutHelper.createLinear(-2, -2));
                    linearLayout.addView(this.popupLayout, LayoutHelper.createLinear(-2, -2, 0, 0, -AndroidUtilities.dp(4.0f), 0, 0));
                    container = linearLayout;
                    this.popupLayout.setTopView(frameLayout);
                }
                this.popupWindow = new ActionBarPopupWindow(container, -2, -2);
                if (this.animationEnabled && Build.VERSION.SDK_INT >= 19) {
                    this.popupWindow.setAnimationStyle(0);
                } else {
                    this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
                }
                boolean z = this.animationEnabled;
                if (!z) {
                    this.popupWindow.setAnimationEnabled(z);
                }
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                if (this.layoutInScreen) {
                    this.popupWindow.setLayoutInScreen(true);
                }
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                container.setFocusableInTouchMode(true);
                container.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda12
                    @Override // android.view.View.OnKeyListener
                    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                        return ActionBarMenuItem.this.m1400x522f9868(view, i, keyEvent);
                    }
                });
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda14
                    @Override // android.widget.PopupWindow.OnDismissListener
                    public final void onDismiss() {
                        ActionBarMenuItem.this.m1399x7858737a();
                    }
                });
                container.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
                this.measurePopup = false;
                this.processedPopupClick = false;
                this.popupWindow.setFocusable(true);
                if (container.getMeasuredWidth() == 0) {
                    updateOrShowPopup(true, true);
                } else {
                    updateOrShowPopup(true, false);
                }
                this.popupLayout.updateRadialSelectors();
                if (this.popupLayout.getSwipeBack() != null) {
                    this.popupLayout.getSwipeBack().closeForeground(false);
                }
                this.popupWindow.startAnimation();
                return;
            }
            this.popupWindow.dismiss();
        }
    }

    /* renamed from: lambda$toggleSubMenu$9$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ boolean m1400x522f9868(View v, int keyCode, KeyEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyCode == 82 && event.getRepeatCount() == 0 && event.getAction() == 1 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$toggleSubMenu$10$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1399x7858737a() {
        onDismiss();
        ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
        if (actionBarSubMenuItemDelegate != null) {
            actionBarSubMenuItemDelegate.onHideSubMenu();
        }
    }

    public void toggleSubMenu() {
        toggleSubMenu(null, null);
    }

    public void openSearch(boolean openKeyboard) {
        ActionBarMenu actionBarMenu;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout == null || frameLayout.getVisibility() == 0 || (actionBarMenu = this.parentMenu) == null) {
            return;
        }
        actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(openKeyboard));
    }

    protected void onDismiss() {
    }

    public boolean isSearchFieldVisible() {
        return this.searchContainer.getVisibility() == 0;
    }

    public boolean toggleSearch(boolean openKeyboard) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        View iconView;
        Animator animator;
        if (this.searchContainer == null || ((actionBarMenuItemSearchListener = this.listener) != null && !actionBarMenuItemSearchListener.canToggleSearch())) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
        if (actionBarMenuItemSearchListener2 != null && (animator = actionBarMenuItemSearchListener2.getCustomToggleTransition()) != null) {
            animator.start();
            return true;
        }
        final ArrayList<View> menuIcons = new ArrayList<>();
        for (int i = 0; i < this.parentMenu.getChildCount(); i++) {
            View view = this.parentMenu.getChildAt(i);
            if ((view instanceof ActionBarMenuItem) && (iconView = ((ActionBarMenuItem) view).getIconView()) != null) {
                menuIcons.add(iconView);
            }
        }
        if (this.searchContainer.getTag() != null) {
            this.searchContainer.setTag(null);
            AnimatorSet animatorSet = this.searchContainerAnimator;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.searchContainerAnimator.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.searchContainerAnimator = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.searchContainer, View.ALPHA, this.searchContainer.getAlpha(), 0.0f));
            for (int i2 = 0; i2 < menuIcons.size(); i2++) {
                menuIcons.get(i2).setAlpha(0.0f);
                this.searchContainerAnimator.playTogether(ObjectAnimator.ofFloat(menuIcons.get(i2), View.ALPHA, menuIcons.get(i2).getAlpha(), 1.0f));
            }
            this.searchContainerAnimator.setDuration(150L);
            this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ActionBarMenuItem.this.searchContainer.setAlpha(0.0f);
                    for (int i3 = 0; i3 < menuIcons.size(); i3++) {
                        ((View) menuIcons.get(i3)).setAlpha(1.0f);
                    }
                    ActionBarMenuItem.this.searchContainer.setVisibility(8);
                }
            });
            this.searchContainerAnimator.start();
            this.searchField.clearFocus();
            setVisibility(0);
            if (!this.currentSearchFilters.isEmpty() && this.listener != null) {
                for (int i3 = 0; i3 < this.currentSearchFilters.size(); i3++) {
                    if (this.currentSearchFilters.get(i3).removable) {
                        this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i3));
                    }
                }
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
            if (actionBarMenuItemSearchListener3 != null) {
                actionBarMenuItemSearchListener3.onSearchCollapse();
            }
            if (openKeyboard) {
                AndroidUtilities.hideKeyboard(this.searchField);
            }
            this.parentMenu.requestLayout();
            requestLayout();
            return false;
        }
        this.searchContainer.setVisibility(0);
        this.searchContainer.setAlpha(0.0f);
        AnimatorSet animatorSet3 = this.searchContainerAnimator;
        if (animatorSet3 != null) {
            animatorSet3.removeAllListeners();
            this.searchContainerAnimator.cancel();
        }
        AnimatorSet animatorSet4 = new AnimatorSet();
        this.searchContainerAnimator = animatorSet4;
        animatorSet4.playTogether(ObjectAnimator.ofFloat(this.searchContainer, View.ALPHA, this.searchContainer.getAlpha(), 1.0f));
        for (int i4 = 0; i4 < menuIcons.size(); i4++) {
            this.searchContainerAnimator.playTogether(ObjectAnimator.ofFloat(menuIcons.get(i4), View.ALPHA, menuIcons.get(i4).getAlpha(), 0.0f));
        }
        this.searchContainerAnimator.setDuration(150L);
        this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ActionBarMenuItem.this.searchContainer.setAlpha(1.0f);
                for (int i5 = 0; i5 < menuIcons.size(); i5++) {
                    ((View) menuIcons.get(i5)).setAlpha(0.0f);
                }
            }
        });
        this.searchContainerAnimator.start();
        setVisibility(8);
        clearSearchFilters();
        this.searchField.setText("");
        this.searchField.requestFocus();
        if (openKeyboard) {
            AndroidUtilities.showKeyboard(this.searchField);
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener4 = this.listener;
        if (actionBarMenuItemSearchListener4 != null) {
            actionBarMenuItemSearchListener4.onSearchExpand();
        }
        this.searchContainer.setTag(1);
        return true;
    }

    public void removeSearchFilter(FiltersView.MediaFilterData filter) {
        if (!filter.removable) {
            return;
        }
        this.currentSearchFilters.remove(filter);
        int i = this.selectedFilterIndex;
        if (i < 0 || i > this.currentSearchFilters.size() - 1) {
            this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
        }
        onFiltersChanged();
        this.searchField.hideActionMode();
    }

    public void addSearchFilter(FiltersView.MediaFilterData filter) {
        this.currentSearchFilters.add(filter);
        if (this.searchContainer.getTag() != null) {
            this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
        }
        onFiltersChanged();
    }

    public void clearSearchFilters() {
        int i = 0;
        while (i < this.currentSearchFilters.size()) {
            if (this.currentSearchFilters.get(i).removable) {
                this.currentSearchFilters.remove(i);
                i--;
            }
            i++;
        }
        onFiltersChanged();
    }

    /* JADX WARN: Type inference failed for: r8v1, types: [org.telegram.ui.ActionBar.ActionBarMenuItem$4] */
    public void onFiltersChanged() {
        boolean visible = !this.currentSearchFilters.isEmpty();
        ArrayList<FiltersView.MediaFilterData> localFilters = new ArrayList<>(this.currentSearchFilters);
        if (Build.VERSION.SDK_INT >= 19 && this.searchContainer.getTag() != null) {
            TransitionSet transition = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150L);
            transition.addTransition(new Visibility() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.4
                @Override // android.transition.Visibility
                public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    if (view instanceof SearchFilterView) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.5f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.5f, 1.0f));
                        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        return set;
                    }
                    return ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f);
                }

                @Override // android.transition.Visibility
                public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    if (view instanceof SearchFilterView) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 0.0f), ObjectAnimator.ofFloat(view, View.SCALE_X, view.getScaleX(), 0.5f), ObjectAnimator.ofFloat(view, View.SCALE_Y, view.getScaleX(), 0.5f));
                        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        return set;
                    }
                    return ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f);
                }
            }.setDuration(150L)).addTransition(changeBounds);
            transition.setOrdering(0);
            transition.setInterpolator((TimeInterpolator) CubicBezierInterpolator.EASE_OUT);
            final int selectedAccount = UserConfig.selectedAccount;
            transition.addListener(new Transition.TransitionListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.5
                @Override // android.transition.Transition.TransitionListener
                public void onTransitionStart(Transition transition2) {
                    ActionBarMenuItem.this.notificationIndex = NotificationCenter.getInstance(selectedAccount).setAnimationInProgress(ActionBarMenuItem.this.notificationIndex, null);
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition2) {
                    NotificationCenter.getInstance(selectedAccount).onAnimationFinish(ActionBarMenuItem.this.notificationIndex);
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionCancel(Transition transition2) {
                    NotificationCenter.getInstance(selectedAccount).onAnimationFinish(ActionBarMenuItem.this.notificationIndex);
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionPause(Transition transition2) {
                }

                @Override // android.transition.Transition.TransitionListener
                public void onTransitionResume(Transition transition2) {
                }
            });
            TransitionManager.beginDelayedTransition(this.searchFilterLayout, transition);
        }
        int i = 0;
        while (i < this.searchFilterLayout.getChildCount()) {
            boolean removed = localFilters.remove(((SearchFilterView) this.searchFilterLayout.getChildAt(i)).getFilter());
            if (!removed) {
                this.searchFilterLayout.removeViewAt(i);
                i--;
            }
            i++;
        }
        for (int i2 = 0; i2 < localFilters.size(); i2++) {
            final SearchFilterView searchFilterView = new SearchFilterView(getContext(), this.resourcesProvider);
            searchFilterView.setData(localFilters.get(i2));
            searchFilterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ActionBarMenuItem.this.m1395xc3bfad94(searchFilterView, view);
                }
            });
            this.searchFilterLayout.addView(searchFilterView, LayoutHelper.createLinear(-2, -1, 0, 0, 0, 6, 0));
        }
        int i3 = 0;
        while (i3 < this.searchFilterLayout.getChildCount()) {
            ((SearchFilterView) this.searchFilterLayout.getChildAt(i3)).setExpanded(i3 == this.selectedFilterIndex);
            i3++;
        }
        this.searchFilterLayout.setTag(visible ? 1 : null);
        final float oldX = this.searchField.getX();
        if (this.searchContainer.getTag() != null) {
            this.searchField.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.6
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ActionBarMenuItem.this.searchField.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (ActionBarMenuItem.this.searchField.getX() != oldX) {
                        ActionBarMenuItem.this.searchField.setTranslationX(oldX - ActionBarMenuItem.this.searchField.getX());
                    }
                    ActionBarMenuItem.this.searchField.animate().translationX(0.0f).setDuration(250L).setStartDelay(0L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    return true;
                }
            });
        }
        checkClearButton();
    }

    /* renamed from: lambda$onFiltersChanged$11$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1395xc3bfad94(SearchFilterView searchFilterView, View view) {
        int index = this.currentSearchFilters.indexOf(searchFilterView.getFilter());
        if (this.selectedFilterIndex != index) {
            this.selectedFilterIndex = index;
            onFiltersChanged();
        } else if (!searchFilterView.getFilter().removable) {
        } else {
            if (!searchFilterView.selectedForDelete) {
                searchFilterView.setSelectedForDelete(true);
                return;
            }
            FiltersView.MediaFilterData filterToRemove = searchFilterView.getFilter();
            removeSearchFilter(filterToRemove);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener != null) {
                actionBarMenuItemSearchListener.onSearchFilterCleared(filterToRemove);
                this.listener.onTextChanged(this.searchField);
            }
        }
    }

    public static boolean checkRtl(String string) {
        char c;
        return !TextUtils.isEmpty(string) && (c = string.charAt(0)) >= 1424 && c <= 1791;
    }

    public boolean isSubMenuShowing() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        return actionBarPopupWindow != null && actionBarPopupWindow.isShowing();
    }

    public void closeSubMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void setIcon(Drawable drawable) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null) {
            return;
        }
        if (drawable instanceof RLottieDrawable) {
            rLottieImageView.setAnimation((RLottieDrawable) drawable);
        } else {
            rLottieImageView.setImageDrawable(drawable);
        }
    }

    public RLottieImageView getIconView() {
        return this.iconView;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setIcon(int resId) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null) {
            return;
        }
        rLottieImageView.setImageResource(resId);
    }

    public void setText(CharSequence text) {
        TextView textView = this.textView;
        if (textView == null) {
            return;
        }
        textView.setText(text);
    }

    public View getContentView() {
        RLottieImageView rLottieImageView = this.iconView;
        return rLottieImageView != null ? rLottieImageView : this.textView;
    }

    public void setSearchFieldHint(CharSequence hint) {
        if (this.searchFieldCaption == null) {
            return;
        }
        this.searchField.setHint(hint);
        setContentDescription(hint);
    }

    public void setSearchFieldText(CharSequence text, boolean animated) {
        if (this.searchFieldCaption == null) {
            return;
        }
        this.animateClear = animated;
        this.searchField.setText(text);
        if (!TextUtils.isEmpty(text)) {
            this.searchField.setSelection(text.length());
        }
    }

    public void onSearchPressed() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        }
    }

    public EditTextBoldCursor getSearchField() {
        return this.searchField;
    }

    public ActionBarMenuItem setOverrideMenuClick(boolean value) {
        this.overrideMenuClick = value;
        return this;
    }

    public ActionBarMenuItem setIsSearchField(boolean value) {
        return setIsSearchField(value, false);
    }

    public ActionBarMenuItem setIsSearchField(boolean value, final boolean wrapInScrollView) {
        if (this.parentMenu == null) {
            return this;
        }
        if (value && this.searchContainer == null) {
            FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.7
                private boolean ignoreRequestLayout;

                @Override // android.view.View
                public void setVisibility(int visibility) {
                    super.setVisibility(visibility);
                    if (ActionBarMenuItem.this.clearButton != null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(visibility);
                    }
                    if (ActionBarMenuItem.this.wrappedSearchFrameLayout != null) {
                        ActionBarMenuItem.this.wrappedSearchFrameLayout.setVisibility(visibility);
                    }
                }

                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    if (ActionBarMenuItem.this.clearButton != null && ActionBarMenuItem.this.clearButton.getTag() != null) {
                        ActionBarMenuItem.this.clearButton.setAlpha(alpha);
                        ActionBarMenuItem.this.clearButton.setScaleX(alpha);
                        ActionBarMenuItem.this.clearButton.setScaleY(alpha);
                    }
                }

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width;
                    int width2;
                    if (!wrapInScrollView) {
                        measureChildWithMargins(ActionBarMenuItem.this.clearButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                    if (!LocaleController.isRTL) {
                        if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                            measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, widthMeasureSpec, View.MeasureSpec.getSize(widthMeasureSpec) / 2, heightMeasureSpec, 0);
                            width2 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            width2 = 0;
                        }
                        int minWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                        this.ignoreRequestLayout = true;
                        measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, widthMeasureSpec, width2, heightMeasureSpec, 0);
                        int filterWidth = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                        measureChildWithMargins(ActionBarMenuItem.this.searchField, widthMeasureSpec, width2 + filterWidth, heightMeasureSpec, 0);
                        this.ignoreRequestLayout = false;
                        setMeasuredDimension(Math.max(ActionBarMenuItem.this.searchField.getMeasuredWidth() + filterWidth, minWidth), View.MeasureSpec.getSize(heightMeasureSpec));
                        return;
                    }
                    if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                        measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, widthMeasureSpec, View.MeasureSpec.getSize(widthMeasureSpec) / 2, heightMeasureSpec, 0);
                        width = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                    } else {
                        width = 0;
                    }
                    int minWidth2 = View.MeasureSpec.getSize(widthMeasureSpec);
                    this.ignoreRequestLayout = true;
                    measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, widthMeasureSpec, width, heightMeasureSpec, 0);
                    int filterWidth2 = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                    measureChildWithMargins(ActionBarMenuItem.this.searchField, View.MeasureSpec.makeMeasureSpec(minWidth2 - AndroidUtilities.dp(12.0f), 0), width + filterWidth2, heightMeasureSpec, 0);
                    this.ignoreRequestLayout = false;
                    setMeasuredDimension(Math.max(ActionBarMenuItem.this.searchField.getMeasuredWidth() + filterWidth2, minWidth2), View.MeasureSpec.getSize(heightMeasureSpec));
                }

                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreRequestLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int x;
                    super.onLayout(changed, left, top, right, bottom);
                    if (!LocaleController.isRTL) {
                        if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                            x = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            x = 0;
                        }
                    } else {
                        x = 0;
                    }
                    if (ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0) {
                        x += ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(x, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + x, ActionBarMenuItem.this.searchField.getBottom());
                }
            };
            this.searchContainer = frameLayout;
            frameLayout.setClipChildren(false);
            this.wrappedSearchFrameLayout = null;
            if (!wrapInScrollView) {
                this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0f, 6, 0, 0, 0));
            } else {
                this.wrappedSearchFrameLayout = new FrameLayout(getContext());
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.8
                    boolean isDragging;

                    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent ev) {
                        checkDragg(ev);
                        return super.onInterceptTouchEvent(ev);
                    }

                    @Override // android.widget.HorizontalScrollView, android.view.View
                    public boolean onTouchEvent(MotionEvent ev) {
                        checkDragg(ev);
                        return super.onTouchEvent(ev);
                    }

                    private void checkDragg(MotionEvent ev) {
                        if (ev.getAction() == 0) {
                            this.isDragging = true;
                        } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                            this.isDragging = false;
                        }
                    }

                    @Override // android.widget.HorizontalScrollView, android.view.View
                    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                        if (!this.isDragging) {
                            return;
                        }
                        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
                    }
                };
                horizontalScrollView.addView(this.searchContainer, LayoutHelper.createScroll(-2, -1, 0));
                horizontalScrollView.setHorizontalScrollBarEnabled(false);
                horizontalScrollView.setClipChildren(false);
                this.wrappedSearchFrameLayout.addView(horizontalScrollView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 48.0f, 0.0f));
                this.parentMenu.addView(this.wrappedSearchFrameLayout, 0, LayoutHelper.createLinear(0, -1, 1.0f, 0, 0, 0, 0));
            }
            this.searchContainer.setVisibility(8);
            TextView textView = new TextView(getContext());
            this.searchFieldCaption = textView;
            textView.setTextSize(1, 18.0f);
            this.searchFieldCaption.setTextColor(getThemedColor(Theme.key_actionBarDefaultSearch));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.9
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int minWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                    setMeasuredDimension(Math.max(minWidth, getMeasuredWidth()) + AndroidUtilities.dp(3.0f), getMeasuredHeight());
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
                public void onSelectionChanged(int selStart, int selEnd) {
                    super.onSelectionChanged(selStart, selEnd);
                }

                @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                    if (keyCode == 67 && ActionBarMenuItem.this.searchField.length() == 0 && ((ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0 && ActionBarMenuItem.this.searchFieldCaption.length() > 0) || ActionBarMenuItem.this.hasRemovableFilters())) {
                        if (ActionBarMenuItem.this.hasRemovableFilters()) {
                            FiltersView.MediaFilterData filterToRemove = (FiltersView.MediaFilterData) ActionBarMenuItem.this.currentSearchFilters.get(ActionBarMenuItem.this.currentSearchFilters.size() - 1);
                            if (ActionBarMenuItem.this.listener != null) {
                                ActionBarMenuItem.this.listener.onSearchFilterCleared(filterToRemove);
                            }
                            ActionBarMenuItem.this.removeSearchFilter(filterToRemove);
                        } else {
                            ActionBarMenuItem.this.clearButton.callOnClick();
                        }
                        return true;
                    }
                    return super.onKeyDown(keyCode, event);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    boolean result = super.onTouchEvent(event);
                    if (event.getAction() == 1 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return result;
                }
            };
            this.searchField = editTextBoldCursor;
            editTextBoldCursor.setScrollContainer(false);
            this.searchField.setCursorWidth(1.5f);
            this.searchField.setCursorColor(getThemedColor(Theme.key_actionBarDefaultSearch));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(getThemedColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(getThemedColor(Theme.key_actionBarDefaultSearch));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            int inputType = this.searchField.getInputType() | 524288;
            this.searchField.setInputType(inputType);
            if (Build.VERSION.SDK_INT < 23) {
                this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.10
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
            this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda1
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView2, int i, KeyEvent keyEvent) {
                    return ActionBarMenuItem.this.m1397xf8dea8e7(textView2, i, keyEvent);
                }
            });
            this.searchField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.11
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    if (ActionBarMenuItem.this.listener != null) {
                        ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
                    }
                    ActionBarMenuItem.this.checkClearButton();
                    if (!ActionBarMenuItem.this.currentSearchFilters.isEmpty() && !TextUtils.isEmpty(ActionBarMenuItem.this.searchField.getText()) && ActionBarMenuItem.this.selectedFilterIndex >= 0) {
                        ActionBarMenuItem.this.selectedFilterIndex = -1;
                        ActionBarMenuItem.this.onFiltersChanged();
                    }
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                }
            });
            this.searchField.setImeOptions(33554435);
            this.searchField.setTextIsSelectable(false);
            this.searchField.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.searchField.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.searchFilterLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.searchFilterLayout.setVisibility(0);
            if (!LocaleController.isRTL) {
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-2, 36.0f, 16, 6.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
            } else {
                this.searchContainer.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-2, 36.0f, 16, 0.0f, 0.0f, wrapInScrollView ? 0.0f : 48.0f, 0.0f));
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 21, 0.0f, 5.5f, 48.0f, 0.0f));
            }
            this.searchFilterLayout.setClipChildren(false);
            ImageView imageView = new ImageView(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.12
                @Override // android.widget.ImageView, android.view.View
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    clearAnimation();
                    if (getTag() == null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(4);
                        ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                        return;
                    }
                    ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                    ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                    ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                    ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                }
            };
            this.clearButton = imageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.13
                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                public int getCurrentColor() {
                    return ActionBarMenuItem.this.parentMenu.parentActionBar.itemsColor;
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ActionBarMenuItem.this.m1398x937f6b68(view);
                }
            });
            this.clearButton.setContentDescription(LocaleController.getString("ClearButton", R.string.ClearButton));
            if (wrapInScrollView) {
                this.wrappedSearchFrameLayout.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            } else {
                this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
            }
        }
        this.isSearchField = value;
        return this;
    }

    /* renamed from: lambda$setIsSearchField$12$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ boolean m1397xf8dea8e7(TextView v, int actionId, KeyEvent event) {
        if (event != null) {
            if ((event.getAction() == 1 && event.getKeyCode() == 84) || (event.getAction() == 0 && event.getKeyCode() == 66)) {
                AndroidUtilities.hideKeyboard(this.searchField);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
                if (actionBarMenuItemSearchListener != null) {
                    actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$setIsSearchField$13$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1398x937f6b68(View v) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        } else if (hasRemovableFilters()) {
            this.searchField.hideActionMode();
            for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                if (this.listener != null && this.currentSearchFilters.get(i).removable) {
                    this.listener.onSearchFilterCleared(this.currentSearchFilters.get(i));
                }
            }
            clearSearchFilters();
        } else {
            TextView textView = this.searchFieldCaption;
            if (textView != null && textView.getVisibility() == 0) {
                this.searchFieldCaption.setVisibility(8);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
                if (actionBarMenuItemSearchListener != null) {
                    actionBarMenuItemSearchListener.onCaptionCleared();
                }
            }
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public View.OnClickListener getOnClickListener() {
        return this.onClickListener;
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        this.onClickListener = l;
        super.setOnClickListener(l);
    }

    public void checkClearButton() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        TextView textView;
        if (this.clearButton != null) {
            if (!hasRemovableFilters() && TextUtils.isEmpty(this.searchField.getText()) && (((actionBarMenuItemSearchListener = this.listener) == null || !actionBarMenuItemSearchListener.forceShowClear()) && ((textView = this.searchFieldCaption) == null || textView.getVisibility() != 0))) {
                if (this.clearButton.getTag() != null) {
                    this.clearButton.setTag(null);
                    this.clearButton.clearAnimation();
                    if (this.animateClear) {
                        this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180L).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                ActionBarMenuItem.this.m1391xa64a0f78();
                            }
                        }).start();
                        return;
                    }
                    this.clearButton.setAlpha(0.0f);
                    this.clearButton.setRotation(45.0f);
                    this.clearButton.setScaleX(0.0f);
                    this.clearButton.setScaleY(0.0f);
                    this.clearButton.setVisibility(4);
                    this.animateClear = true;
                }
            } else if (this.clearButton.getTag() == null) {
                this.clearButton.setTag(1);
                this.clearButton.clearAnimation();
                this.clearButton.setVisibility(0);
                if (this.animateClear) {
                    this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180L).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                    return;
                }
                this.clearButton.setAlpha(1.0f);
                this.clearButton.setRotation(0.0f);
                this.clearButton.setScaleX(1.0f);
                this.clearButton.setScaleY(1.0f);
                this.animateClear = true;
            }
        }
    }

    /* renamed from: lambda$checkClearButton$14$org-telegram-ui-ActionBar-ActionBarMenuItem */
    public /* synthetic */ void m1391xa64a0f78() {
        this.clearButton.setVisibility(4);
    }

    public boolean hasRemovableFilters() {
        if (this.currentSearchFilters.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.currentSearchFilters.size(); i++) {
            if (this.currentSearchFilters.get(i).removable) {
                return true;
            }
        }
        return false;
    }

    public void setShowSearchProgress(boolean show) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 == null) {
            return;
        }
        if (show) {
            closeProgressDrawable2.startAnimation();
        } else {
            closeProgressDrawable2.stopAnimation();
        }
    }

    public void setSearchFieldCaption(CharSequence caption) {
        if (this.searchFieldCaption == null) {
            return;
        }
        if (TextUtils.isEmpty(caption)) {
            this.searchFieldCaption.setVisibility(8);
            return;
        }
        this.searchFieldCaption.setVisibility(0);
        this.searchFieldCaption.setText(caption);
    }

    public void setIgnoreOnTextChange() {
        this.ignoreOnTextChange = true;
    }

    public boolean isSearchField() {
        return this.isSearchField;
    }

    public void clearSearchText() {
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor == null) {
            return;
        }
        editTextBoldCursor.setText("");
    }

    public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener actionBarMenuItemSearchListener) {
        this.listener = actionBarMenuItemSearchListener;
        return this;
    }

    public ActionBarMenuItem setAllowCloseAnimation(boolean value) {
        this.allowCloseAnimation = value;
        return this;
    }

    public void setPopupAnimationEnabled(boolean value) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setAnimationEnabled(value);
        }
        this.animationEnabled = value;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onLayout(left, top, right, bottom);
        }
    }

    public void setAdditionalYOffset(int value) {
        this.additionalYOffset = value;
    }

    public void setAdditionalXOffset(int value) {
        this.additionalXOffset = value;
    }

    public void forceUpdatePopupPosition() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        updateOrShowPopup(true, true);
    }

    private void updateOrShowPopup(boolean show, boolean update) {
        int offsetY;
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            offsetY = (-actionBarMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop() + this.parentMenu.getPaddingTop();
        } else {
            float scaleY = getScaleY();
            offsetY = (-((int) ((getMeasuredHeight() * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)))) + this.additionalYOffset;
        }
        int offsetY2 = offsetY + this.yOffset;
        if (show) {
            this.popupLayout.scrollToTop();
        }
        View fromView = this.showSubMenuFrom;
        if (fromView == null) {
            fromView = this;
        }
        ActionBarMenu actionBarMenu2 = this.parentMenu;
        if (actionBarMenu2 != null) {
            View parent = actionBarMenu2.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (show) {
                    this.popupWindow.showAsDropDown(parent, (((fromView.getLeft() + this.parentMenu.getLeft()) + fromView.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()), offsetY2);
                }
                if (update) {
                    this.popupWindow.update(parent, ((int) getTranslationX()) + (((fromView.getLeft() + this.parentMenu.getLeft()) + fromView.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()), offsetY2, -1, -1);
                    return;
                }
                return;
            }
            if (show) {
                if (this.forceSmoothKeyboard) {
                    this.popupWindow.showAtLocation(parent, 51, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2);
                } else {
                    this.popupWindow.showAsDropDown(parent, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2);
                }
            }
            if (update) {
                this.popupWindow.update(parent, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), offsetY2, -1, -1);
                return;
            }
            return;
        }
        int i = this.subMenuOpenSide;
        if (i == 0) {
            if (getParent() != null) {
                View parent2 = (View) getParent();
                if (show) {
                    this.popupWindow.showAsDropDown(parent2, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, offsetY2);
                }
                if (update) {
                    this.popupWindow.update(parent2, this.additionalXOffset + ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()), offsetY2, -1, -1);
                }
            }
        } else if (i == 1) {
            if (show) {
                this.popupWindow.showAsDropDown(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, offsetY2);
            }
            if (update) {
                this.popupWindow.update(this, this.additionalXOffset + (-AndroidUtilities.dp(8.0f)), offsetY2, -1, -1);
            }
        } else {
            if (show) {
                this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, offsetY2);
            }
            if (update) {
                this.popupWindow.update(this, this.additionalXOffset + (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()), offsetY2, -1, -1);
            }
        }
    }

    public void hideSubItem(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) != null && view.getVisibility() != 8) {
            view.setVisibility(8);
            this.measurePopup = true;
        }
    }

    public void checkHideMenuItem() {
        boolean isVisible = false;
        int i = 0;
        while (true) {
            if (i >= this.popupLayout.getItemsCount()) {
                break;
            } else if (this.popupLayout.getItemAt(i).getVisibility() != 0) {
                i++;
            } else {
                isVisible = true;
                break;
            }
        }
        int v = isVisible ? 0 : 8;
        if (v != getVisibility()) {
            setVisibility(v);
        }
    }

    public void hideAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        int N = actionBarPopupWindowLayout.getItemsCount();
        for (int a = 0; a < N; a++) {
            this.popupLayout.getItemAt(a).setVisibility(8);
        }
        this.measurePopup = true;
        checkHideMenuItem();
    }

    public boolean isSubItemVisible(int id) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        return (actionBarPopupWindowLayout == null || (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) == null || view.getVisibility() != 0) ? false : true;
    }

    public void showSubItem(int id) {
        showSubItem(id, false);
    }

    public void showSubItem(int id, boolean animated) {
        View view;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null && (view = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(id))) != null && view.getVisibility() != 0) {
            view.setAlpha(0.0f);
            view.animate().alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150L).start();
            view.setVisibility(0);
            this.measurePopup = true;
        }
    }

    public void requestFocusOnSearchView() {
        if (this.searchContainer.getWidth() != 0 && !this.searchField.isFocused()) {
            this.searchField.requestFocus();
            AndroidUtilities.showKeyboard(this.searchField);
        }
    }

    public void clearFocusOnSearchView() {
        this.searchField.clearFocus();
        AndroidUtilities.hideKeyboard(this.searchField);
    }

    public FrameLayout getSearchContainer() {
        return this.searchContainer;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.iconView != null) {
            info.setClassName("android.widget.ImageButton");
        } else if (this.textView != null) {
            info.setClassName("android.widget.Button");
            if (TextUtils.isEmpty(info.getText())) {
                info.setText(this.textView.getText());
            }
        }
    }

    public void updateColor() {
        if (this.searchFilterLayout != null) {
            for (int i = 0; i < this.searchFilterLayout.getChildCount(); i++) {
                if (this.searchFilterLayout.getChildAt(i) instanceof SearchFilterView) {
                    ((SearchFilterView) this.searchFilterLayout.getChildAt(i)).updateColors();
                }
            }
        }
        if (this.popupLayout != null) {
            for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
                if (this.popupLayout.getItemAt(i2) instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) this.popupLayout.getItemAt(i2)).setSelectorColor(getThemedColor(Theme.key_dialogButtonSelector));
                }
            }
        }
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.setCursorColor(getThemedColor(Theme.key_actionBarDefaultSearch));
            this.searchField.setHintTextColor(getThemedColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(getThemedColor(Theme.key_actionBarDefaultSearch));
            this.searchField.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.searchField.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        }
    }

    public void collapseSearchFilters() {
        this.selectedFilterIndex = -1;
        onFiltersChanged();
    }

    public void setTransitionOffset(float offset) {
        this.transitionOffset = offset;
        setTranslationX(0.0f);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes4.dex */
    public static class SearchFilterView extends FrameLayout {
        BackupImageView avatarImageView;
        ImageView closeIconView;
        FiltersView.MediaFilterData data;
        Runnable removeSelectionRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.SearchFilterView.1
            @Override // java.lang.Runnable
            public void run() {
                if (SearchFilterView.this.selectedForDelete) {
                    SearchFilterView.this.setSelectedForDelete(false);
                }
            }
        };
        private final Theme.ResourcesProvider resourcesProvider;
        ValueAnimator selectAnimator;
        private boolean selectedForDelete;
        private float selectedProgress;
        ShapeDrawable shapeDrawable;
        Drawable thumbDrawable;
        TextView titleView;

        public SearchFilterView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            ImageView imageView = new ImageView(context);
            this.closeIconView = imageView;
            imageView.setImageResource(R.drawable.ic_close_white);
            addView(this.closeIconView, LayoutHelper.createFrame(24, 24.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 16.0f, 0.0f));
            ShapeDrawable shapeDrawable = (ShapeDrawable) Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), -12292204);
            this.shapeDrawable = shapeDrawable;
            setBackground(shapeDrawable);
            updateColors();
        }

        public void updateColors() {
            int defaultBackgroundColor = getThemedColor(Theme.key_groupcreate_spanBackground);
            int selectedBackgroundColor = getThemedColor(Theme.key_avatar_backgroundBlue);
            int textDefaultColor = getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
            int textSelectedColor = getThemedColor(Theme.key_avatar_actionBarIconBlue);
            this.shapeDrawable.getPaint().setColor(ColorUtils.blendARGB(defaultBackgroundColor, selectedBackgroundColor, this.selectedProgress));
            this.titleView.setTextColor(ColorUtils.blendARGB(textDefaultColor, textSelectedColor, this.selectedProgress));
            this.closeIconView.setColorFilter(textSelectedColor);
            this.closeIconView.setAlpha(this.selectedProgress);
            this.closeIconView.setScaleX(this.selectedProgress * 0.82f);
            this.closeIconView.setScaleY(this.selectedProgress * 0.82f);
            Drawable drawable = this.thumbDrawable;
            if (drawable != null) {
                Theme.setCombinedDrawableColor(drawable, getThemedColor(Theme.key_avatar_backgroundBlue), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
            }
            this.avatarImageView.setAlpha(1.0f - this.selectedProgress);
            FiltersView.MediaFilterData mediaFilterData = this.data;
            if (mediaFilterData != null && mediaFilterData.filterType == 7) {
                setData(this.data);
            }
            invalidate();
        }

        public void setData(FiltersView.MediaFilterData data) {
            this.data = data;
            this.titleView.setText(data.title);
            CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), data.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(Theme.key_avatar_backgroundBlue), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
            if (data.filterType == 4) {
                if (data.chat instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) data.chat;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        CombinedDrawable combinedDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_saved);
                        combinedDrawable.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(Theme.key_avatar_backgroundSaved), false);
                        Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
                        this.avatarImageView.setImageDrawable(combinedDrawable);
                        return;
                    }
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(user, this.thumbDrawable);
                } else if (data.chat instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) data.chat;
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(chat, this.thumbDrawable);
                }
            } else if (data.filterType == 7) {
                CombinedDrawable combinedDrawable2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_archive);
                combinedDrawable2.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(combinedDrawable2, getThemedColor(Theme.key_avatar_backgroundArchived), false);
                Theme.setCombinedDrawableColor(combinedDrawable2, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
                this.avatarImageView.setImageDrawable(combinedDrawable2);
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
        }

        public void setExpanded(boolean expanded) {
            if (expanded) {
                this.titleView.setVisibility(0);
                return;
            }
            this.titleView.setVisibility(8);
            setSelectedForDelete(false);
        }

        public void setSelectedForDelete(final boolean select) {
            if (this.selectedForDelete == select) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.removeSelectionRunnable);
            this.selectedForDelete = select;
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.selectAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.selectedProgress;
            fArr[1] = select ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.selectAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem$SearchFilterView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ActionBarMenuItem.SearchFilterView.this.m1401x8690ed44(valueAnimator2);
                }
            });
            this.selectAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarMenuItem.SearchFilterView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    SearchFilterView.this.selectedProgress = select ? 1.0f : 0.0f;
                    SearchFilterView.this.updateColors();
                }
            });
            this.selectAnimator.setDuration(150L).start();
            if (this.selectedForDelete) {
                AndroidUtilities.runOnUIThread(this.removeSelectionRunnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }

        /* renamed from: lambda$setSelectedForDelete$0$org-telegram-ui-ActionBar-ActionBarMenuItem$SearchFilterView */
        public /* synthetic */ void m1401x8690ed44(ValueAnimator valueAnimator) {
            this.selectedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateColors();
        }

        public FiltersView.MediaFilterData getFilter() {
            return this.data;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public ActionBarPopupWindow.GapView addColoredGap() {
        createPopupLayout();
        ActionBarPopupWindow.GapView gap = new ActionBarPopupWindow.GapView(getContext(), this.resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
        gap.setTag(R.id.fit_width_tag, 1);
        this.popupLayout.addView((View) gap, LayoutHelper.createLinear(-1, 8));
        return gap;
    }

    public static ActionBarMenuSubItem addItem(ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout, int icon, CharSequence text, boolean needCheck, Theme.ResourcesProvider resourcesProvider) {
        ActionBarMenuSubItem cell = new ActionBarMenuSubItem(windowLayout.getContext(), needCheck, false, false, resourcesProvider);
        cell.setTextAndIcon(text, icon);
        cell.setMinimumWidth(AndroidUtilities.dp(196.0f));
        windowLayout.addView(cell);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cell.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        cell.setLayoutParams(layoutParams);
        return cell;
    }
}
