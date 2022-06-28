package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public abstract class BaseFragment {
    protected ActionBar actionBar;
    protected Bundle arguments;
    protected boolean finishing;
    protected boolean fragmentBeginToShow;
    protected View fragmentView;
    protected boolean inBubbleMode;
    protected boolean inMenuMode;
    protected boolean inPreviewMode;
    private boolean isFinished;
    protected Dialog parentDialog;
    protected ActionBarLayout parentLayout;
    private boolean removingFromStack;
    protected Dialog visibleDialog;
    protected int currentAccount = UserConfig.selectedAccount;
    protected boolean hasOwnBackground = false;
    protected boolean isPaused = true;
    protected boolean inTransitionAnimation = false;
    protected int classGuid = ConnectionsManager.generateClassGuid();

    public BaseFragment() {
    }

    public BaseFragment(Bundle args) {
        this.arguments = args;
    }

    public void setCurrentAccount(int account) {
        if (this.fragmentView != null) {
            throw new IllegalStateException("trying to set current account when fragment UI already created");
        }
        this.currentAccount = account;
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public View getFragmentView() {
        return this.fragmentView;
    }

    public View createView(Context context) {
        return null;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public int getClassGuid() {
        return this.classGuid;
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return true;
    }

    public void setInBubbleMode(boolean value) {
        this.inBubbleMode = value;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    public boolean isInPreviewMode() {
        return this.inPreviewMode;
    }

    public boolean getInPassivePreviewMode() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && actionBarLayout.isInPassivePreviewMode();
    }

    public void setInPreviewMode(boolean value) {
        this.inPreviewMode = value;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            boolean z = false;
            if (value) {
                actionBar.setOccupyStatusBar(false);
                return;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                z = true;
            }
            actionBar.setOccupyStatusBar(z);
        }
    }

    public void setInMenuMode(boolean value) {
        this.inMenuMode = value;
    }

    public void onPreviewOpenAnimationEnd() {
    }

    public boolean hideKeyboardOnShow() {
        return true;
    }

    public void clearViews() {
        View view = this.fragmentView;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                try {
                    onRemoveFromParent();
                    parent.removeViewInLayout(this.fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.fragmentView = null;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            ViewGroup parent2 = (ViewGroup) actionBar.getParent();
            if (parent2 != null) {
                try {
                    parent2.removeViewInLayout(this.actionBar);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            this.actionBar = null;
        }
        this.parentLayout = null;
    }

    public void onRemoveFromParent() {
    }

    public void setParentFragment(BaseFragment fragment) {
        setParentLayout(fragment.parentLayout);
        this.fragmentView = createView(this.parentLayout.getContext());
    }

    public void setParentLayout(ActionBarLayout layout) {
        ViewGroup parent;
        if (this.parentLayout != layout) {
            this.parentLayout = layout;
            boolean differentParent = true;
            this.inBubbleMode = layout != null && layout.isInBubbleMode();
            View view = this.fragmentView;
            if (view != null) {
                ViewGroup parent2 = (ViewGroup) view.getParent();
                if (parent2 != null) {
                    try {
                        onRemoveFromParent();
                        parent2.removeViewInLayout(this.fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                ActionBarLayout actionBarLayout = this.parentLayout;
                if (actionBarLayout != null && actionBarLayout.getContext() != this.fragmentView.getContext()) {
                    this.fragmentView = null;
                }
            }
            if (this.actionBar != null) {
                ActionBarLayout actionBarLayout2 = this.parentLayout;
                if (actionBarLayout2 == null || actionBarLayout2.getContext() == this.actionBar.getContext()) {
                    differentParent = false;
                }
                if ((this.actionBar.shouldAddToContainer() || differentParent) && (parent = (ViewGroup) this.actionBar.getParent()) != null) {
                    try {
                        parent.removeViewInLayout(this.actionBar);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (differentParent) {
                    this.actionBar = null;
                }
            }
            ActionBarLayout actionBarLayout3 = this.parentLayout;
            if (actionBarLayout3 != null && this.actionBar == null) {
                ActionBar createActionBar = createActionBar(actionBarLayout3.getContext());
                this.actionBar = createActionBar;
                if (createActionBar != null) {
                    createActionBar.parentFragment = this;
                }
            }
        }
    }

    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context, getResourceProvider());
        actionBar.setBackgroundColor(getThemedColor(Theme.key_actionBarDefault));
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode || this.inBubbleMode) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    public void movePreviewFragment(float dy) {
        this.parentLayout.movePreviewFragment(dy);
    }

    public void finishPreviewFragment() {
        this.parentLayout.finishPreviewFragment();
    }

    public void finishFragment() {
        Dialog dialog = this.parentDialog;
        if (dialog != null) {
            dialog.dismiss();
        } else {
            finishFragment(true);
        }
    }

    public void finishFragment(boolean animated) {
        ActionBarLayout actionBarLayout;
        if (this.isFinished || (actionBarLayout = this.parentLayout) == null) {
            return;
        }
        this.finishing = true;
        actionBarLayout.closeLastFragment(animated);
    }

    public void removeSelfFromStack() {
        ActionBarLayout actionBarLayout;
        if (this.isFinished || (actionBarLayout = this.parentLayout) == null) {
            return;
        }
        Dialog dialog = this.parentDialog;
        if (dialog != null) {
            dialog.dismiss();
        } else {
            actionBarLayout.removeFragmentFromStack(this);
        }
    }

    public boolean isFinishing() {
        return this.finishing;
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        getMessagesStorage().cancelTasksForGuid(this.classGuid);
        boolean z = true;
        this.isFinished = true;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setEnabled(false);
        }
        if (hasForceLightStatusBar() && !AndroidUtilities.isTablet() && getParentLayout().getLastFragment() == this && getParentActivity() != null && !this.finishing) {
            Window window = getParentActivity().getWindow();
            if (Theme.getColor(Theme.key_actionBarDefault) != -1) {
                z = false;
            }
            AndroidUtilities.setLightStatusBar(window, z);
        }
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void resumeDelayedFragmentAnimation() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.resumeDelayedFragmentAnimation();
        }
    }

    public void onUserLeaveHint() {
    }

    public void onResume() {
        this.isPaused = false;
    }

    public void onPause() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onPause();
        }
        this.isPaused = true;
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public BaseFragment getFragmentForAlert(int offset) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout == null || actionBarLayout.fragmentsStack.size() <= offset + 1) {
            return this;
        }
        return this.parentLayout.fragmentsStack.get((this.parentLayout.fragmentsStack.size() - 2) - offset);
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public boolean onBackPressed() {
        return true;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
    }

    public void saveSelfArgs(Bundle args) {
    }

    public void restoreSelfArgs(Bundle args) {
    }

    public boolean isLastFragment() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        return actionBarLayout != null && !actionBarLayout.fragmentsStack.isEmpty() && this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) == this;
    }

    public ActionBarLayout getParentLayout() {
        return this.parentLayout;
    }

    public FrameLayout getLayoutContainer() {
        View view = this.fragmentView;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof FrameLayout) {
                return (FrameLayout) parent;
            }
            return null;
        }
        return null;
    }

    public boolean presentFragmentAsPreview(BaseFragment fragment) {
        ActionBarLayout actionBarLayout;
        return allowPresentFragment() && (actionBarLayout = this.parentLayout) != null && actionBarLayout.presentFragmentAsPreview(fragment);
    }

    public boolean presentFragmentAsPreviewWithMenu(BaseFragment fragment, ActionBarPopupWindow.ActionBarPopupWindowLayout menu) {
        ActionBarLayout actionBarLayout;
        return allowPresentFragment() && (actionBarLayout = this.parentLayout) != null && actionBarLayout.presentFragmentAsPreviewWithMenu(fragment, menu);
    }

    public boolean presentFragment(BaseFragment fragment) {
        ActionBarLayout actionBarLayout;
        return allowPresentFragment() && (actionBarLayout = this.parentLayout) != null && actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast) {
        ActionBarLayout actionBarLayout;
        return allowPresentFragment() && (actionBarLayout = this.parentLayout) != null && actionBarLayout.presentFragment(fragment, removeLast);
    }

    public boolean presentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation) {
        ActionBarLayout actionBarLayout;
        return allowPresentFragment() && (actionBarLayout = this.parentLayout) != null && actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false, null);
    }

    public Activity getParentActivity() {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            return actionBarLayout.parentActivity;
        }
        return null;
    }

    public void setParentActivityTitle(CharSequence title) {
        Activity activity = getParentActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.startActivityForResult(intent, requestCode);
        }
    }

    public void dismissCurrentDialog() {
        Dialog dialog = this.visibleDialog;
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
            this.visibleDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public boolean canBeginSlide() {
        return true;
    }

    public void onBeginSlide() {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onPause();
        }
    }

    public void onSlideProgress(boolean isOpen, float progress) {
    }

    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        this.inTransitionAnimation = true;
        if (isOpen) {
            this.fragmentBeginToShow = true;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        this.inTransitionAnimation = false;
    }

    public void onBecomeFullyVisible() {
        ActionBar actionBar;
        AccessibilityManager mgr = (AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility");
        if (mgr.isEnabled() && (actionBar = getActionBar()) != null) {
            String title = actionBar.getTitle();
            if (!TextUtils.isEmpty(title)) {
                setParentActivityTitle(title);
            }
        }
    }

    public int getPreviewHeight() {
        return -1;
    }

    public void onBecomeFullyHidden() {
    }

    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        return null;
    }

    public void onLowMemory() {
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, null);
    }

    public Dialog showDialog(Dialog dialog, DialogInterface.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean allowInTransition, final DialogInterface.OnDismissListener onDismissListener) {
        ActionBarLayout actionBarLayout;
        if (dialog == null || (actionBarLayout = this.parentLayout) == null || actionBarLayout.animationInProgress || this.parentLayout.startedTracking || (!allowInTransition && this.parentLayout.checkTransitionAnimation())) {
            return null;
        }
        try {
            Dialog dialog2 = this.visibleDialog;
            if (dialog2 != null) {
                dialog2.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            this.visibleDialog = dialog;
            dialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ActionBar.BaseFragment$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    BaseFragment.this.m1412lambda$showDialog$0$orgtelegramuiActionBarBaseFragment(onDismissListener, dialogInterface);
                }
            });
            this.visibleDialog.show();
            return this.visibleDialog;
        } catch (Exception e2) {
            FileLog.e(e2);
            return null;
        }
    }

    /* renamed from: lambda$showDialog$0$org-telegram-ui-ActionBar-BaseFragment */
    public /* synthetic */ void m1412lambda$showDialog$0$orgtelegramuiActionBarBaseFragment(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialog1) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog1);
        }
        onDialogDismiss((Dialog) dialog1);
        if (dialog1 == this.visibleDialog) {
            this.visibleDialog = null;
        }
    }

    public void onDialogDismiss(Dialog dialog) {
    }

    protected void onPanTranslationUpdate(float y) {
    }

    protected void onPanTransitionStart() {
    }

    protected void onPanTransitionEnd() {
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public boolean extendActionMode(Menu menu) {
        return false;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }

    public AccountInstance getAccountInstance() {
        return AccountInstance.getInstance(this.currentAccount);
    }

    public MessagesController getMessagesController() {
        return getAccountInstance().getMessagesController();
    }

    public ContactsController getContactsController() {
        return getAccountInstance().getContactsController();
    }

    public MediaDataController getMediaDataController() {
        return getAccountInstance().getMediaDataController();
    }

    public ConnectionsManager getConnectionsManager() {
        return getAccountInstance().getConnectionsManager();
    }

    public LocationController getLocationController() {
        return getAccountInstance().getLocationController();
    }

    public NotificationsController getNotificationsController() {
        return getAccountInstance().getNotificationsController();
    }

    public MessagesStorage getMessagesStorage() {
        return getAccountInstance().getMessagesStorage();
    }

    public SendMessagesHelper getSendMessagesHelper() {
        return getAccountInstance().getSendMessagesHelper();
    }

    public FileLoader getFileLoader() {
        return getAccountInstance().getFileLoader();
    }

    public SecretChatHelper getSecretChatHelper() {
        return getAccountInstance().getSecretChatHelper();
    }

    public DownloadController getDownloadController() {
        return getAccountInstance().getDownloadController();
    }

    public SharedPreferences getNotificationsSettings() {
        return getAccountInstance().getNotificationsSettings();
    }

    public NotificationCenter getNotificationCenter() {
        return getAccountInstance().getNotificationCenter();
    }

    public MediaController getMediaController() {
        return MediaController.getInstance();
    }

    public UserConfig getUserConfig() {
        return getAccountInstance().getUserConfig();
    }

    public void setFragmentPanTranslationOffset(int offset) {
        ActionBarLayout actionBarLayout = this.parentLayout;
        if (actionBarLayout != null) {
            actionBarLayout.setFragmentPanTranslationOffset(offset);
        }
    }

    public void saveKeyboardPositionBeforeTransition() {
    }

    public Animator getCustomSlideTransition(boolean topFragment, boolean backAnimation, float distanceToMove) {
        return null;
    }

    public boolean shouldOverrideSlideTransition(boolean topFragment, boolean backAnimation) {
        return false;
    }

    public void prepareFragmentToSlide(boolean topFragment, boolean beginSlide) {
    }

    public void setProgressToDrawerOpened(float v) {
    }

    public ActionBarLayout[] showAsSheet(BaseFragment fragment) {
        if (getParentActivity() == null) {
            return null;
        }
        ActionBarLayout[] actionBarLayout = {new ActionBarLayout(getParentActivity())};
        BottomSheet bottomSheet = new AnonymousClass1(getParentActivity(), true, actionBarLayout, fragment);
        fragment.setParentDialog(bottomSheet);
        bottomSheet.show();
        return actionBarLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ActionBar.BaseFragment$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends BottomSheet {
        final /* synthetic */ ActionBarLayout[] val$actionBarLayout;
        final /* synthetic */ BaseFragment val$fragment;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Context context, boolean needFocus, ActionBarLayout[] actionBarLayoutArr, final BaseFragment baseFragment) {
            super(context, needFocus);
            BaseFragment.this = this$0;
            this.val$actionBarLayout = actionBarLayoutArr;
            this.val$fragment = baseFragment;
            actionBarLayoutArr[0].init(new ArrayList<>());
            actionBarLayoutArr[0].addFragmentToStack(baseFragment);
            actionBarLayoutArr[0].showLastFragment();
            actionBarLayoutArr[0].setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
            this.containerView = actionBarLayoutArr[0];
            setApplyBottomPadding(false);
            setApplyBottomPadding(false);
            setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ActionBar.BaseFragment$1$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    BaseFragment.this.onFragmentDestroy();
                }
            });
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet
        protected boolean canDismissWithSwipe() {
            return false;
        }

        @Override // android.app.Dialog
        public void onBackPressed() {
            ActionBarLayout[] actionBarLayoutArr = this.val$actionBarLayout;
            if (actionBarLayoutArr[0] == null || actionBarLayoutArr[0].fragmentsStack.size() <= 1) {
                super.onBackPressed();
            } else {
                this.val$actionBarLayout[0].onBackPressed();
            }
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
        public void dismiss() {
            super.dismiss();
            this.val$actionBarLayout[0] = null;
        }
    }

    public int getThemedColor(String key) {
        return Theme.getColor(key, getResourceProvider());
    }

    public Drawable getThemedDrawable(String key) {
        return Theme.getThemeDrawable(key);
    }

    public boolean hasForceLightStatusBar() {
        return false;
    }

    public int getNavigationBarColor() {
        return Theme.getColor(Theme.key_windowBackgroundGray);
    }

    public void setNavigationBarColor(int color) {
        Activity activity = getParentActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= 26 && window != null && window.getNavigationBarColor() != color) {
                window.setNavigationBarColor(color);
                float brightness = AndroidUtilities.computePerceivedBrightness(color);
                AndroidUtilities.setLightNavigationBar(window, brightness >= 0.721f);
            }
        }
    }

    public boolean isBeginToShow() {
        return this.fragmentBeginToShow;
    }

    private void setParentDialog(Dialog dialog) {
        this.parentDialog = dialog;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return null;
    }

    protected boolean allowPresentFragment() {
        return true;
    }

    public boolean isRemovingFromStack() {
        return this.removingFromStack;
    }

    public void setRemovingFromStack(boolean b) {
        this.removingFromStack = b;
    }

    public boolean isLightStatusBar() {
        int color;
        if (!hasForceLightStatusBar() || Theme.getCurrentTheme().isDark()) {
            Theme.ResourcesProvider resourcesProvider = getResourceProvider();
            String key = Theme.key_actionBarDefault;
            ActionBar actionBar = this.actionBar;
            if (actionBar != null && actionBar.isActionModeShowed()) {
                key = Theme.key_actionBarActionModeDefault;
            }
            if (resourcesProvider != null) {
                color = resourcesProvider.getColorOrDefault(key);
            } else {
                color = Theme.getColor(key, null, true);
            }
            return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
        }
        return true;
    }
}
