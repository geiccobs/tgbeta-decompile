package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerSearchActivity;
/* loaded from: classes5.dex */
public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    public final Property<AttachAlertLayout, Float> ATTACH_ALERT_LAYOUT_TRANSLATION;
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    protected ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    protected boolean allowOrder;
    protected boolean allowPassConfirmationAlert;
    private SpringAnimation appearSpringAnimation;
    private final Paint attachButtonPaint;
    private int attachItemSize;
    private ChatAttachAlertAudioLayout audioLayout;
    protected int avatarPicker;
    protected boolean avatarSearch;
    protected BaseFragment baseFragment;
    private float baseSelectedTextViewTranslationY;
    private LongSparseArray<ChatAttachAlertBotWebViewLayout> botAttachLayouts;
    private boolean botButtonProgressWasVisible;
    private boolean botButtonWasVisible;
    private float botMainButtonOffsetY;
    private TextView botMainButtonTextView;
    private RadialProgressView botProgressView;
    private float bottomPannelTranslation;
    private boolean buttonPressed;
    private ButtonsAdapter buttonsAdapter;
    private AnimatorSet buttonsAnimation;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    public boolean canOpenPreview;
    private float captionEditTextTopOffset;
    private final NumberTextView captionLimitView;
    private float chatActivityEnterViewAnimateFromTop;
    private int codepointCount;
    protected EditTextEmoji commentTextView;
    private AnimatorSet commentsAnimator;
    private boolean confirmationAlertShown;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius;
    protected int currentAccount;
    private AttachAlertLayout currentAttachLayout;
    private int currentLimit;
    float currentPanTranslationY;
    private DecelerateInterpolator decelerateInterpolator;
    protected ChatAttachViewDelegate delegate;
    private ChatAttachAlertDocumentLayout documentLayout;
    protected ActionBarMenuItem doneItem;
    protected MessageObject editingMessageObject;
    private boolean enterCommentEventSent;
    private ArrayList<android.graphics.Rect> exclusionRects;
    private android.graphics.Rect exclustionRect;
    private final boolean forceDarkTheme;
    private FrameLayout frameLayout2;
    private float fromScrollY;
    protected FrameLayout headerView;
    protected boolean inBubbleMode;
    private boolean isSoundPicker;
    private ActionBarMenuSubItem[] itemCells;
    private AttachAlertLayout[] layouts;
    private ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos;
    private boolean mediaEnabled;
    protected TextView mediaPreviewTextView;
    protected LinearLayout mediaPreviewView;
    private AnimatorSet menuAnimator;
    private boolean menuShowed;
    private AttachAlertLayout nextAttachLayout;
    private boolean openTransitionFinished;
    protected boolean openWithFrontFaceCamera;
    private Paint paint;
    public ChatActivity.ThemeDelegate parentThemeDelegate;
    protected boolean paused;
    private ChatAttachAlertPhotoLayout photoLayout;
    private ChatAttachAlertPhotoLayoutPreview photoPreviewLayout;
    private ChatAttachAlertPollLayout pollLayout;
    private boolean pollsEnabled;
    private int previousScrollOffsetY;
    private RectF rect;
    protected int[] scrollOffsetY;
    protected ActionBarMenuItem searchItem;
    protected ImageView selectedArrowImageView;
    private View selectedCountView;
    private long selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    protected LinearLayout selectedView;
    private ValueAnimator sendButtonColorAnimator;
    boolean sendButtonEnabled;
    private float sendButtonEnabledProgress;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private View shadow;
    private final boolean showingFromDialog;
    protected SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private TextPaint textPaint;
    private float toScrollY;
    private ValueAnimator topBackgroundAnimator;
    public float translationProgress;
    protected boolean typeButtonsAvailable;
    private Object viewChangeAnimator;
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;

    public void setCanOpenPreview(boolean canOpenPreview) {
        this.canOpenPreview = canOpenPreview;
        this.selectedArrowImageView.setVisibility((!canOpenPreview || this.avatarPicker == 2) ? 8 : 0);
    }

    public float getClipLayoutBottom() {
        float alphaOffset = (this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f)) * (1.0f - this.frameLayout2.getAlpha());
        return this.frameLayout2.getMeasuredHeight() - alphaOffset;
    }

    public void showBotLayout(long id) {
        showBotLayout(id, null);
    }

    public void showBotLayout(long id, String startCommand) {
        if ((this.botAttachLayouts.get(id) == null || !ColorUtils$$ExternalSyntheticBackport0.m(startCommand, this.botAttachLayouts.get(id).getStartCommand()) || this.botAttachLayouts.get(id).needReload()) && (this.baseFragment instanceof ChatActivity)) {
            ChatAttachAlertBotWebViewLayout webViewLayout = new ChatAttachAlertBotWebViewLayout(this, getContext(), this.resourcesProvider);
            this.botAttachLayouts.put(id, webViewLayout);
            this.botAttachLayouts.get(id).setDelegate(new AnonymousClass1(webViewLayout));
            MessageObject replyingObject = ((ChatActivity) this.baseFragment).getChatActivityEnterView().getReplyingMessageObject();
            this.botAttachLayouts.get(id).requestWebView(this.currentAccount, ((ChatActivity) this.baseFragment).getDialogId(), id, false, replyingObject != null ? replyingObject.messageOwner.id : 0, startCommand);
        }
        if (this.botAttachLayouts.get(id) != null) {
            this.botAttachLayouts.get(id).disallowSwipeOffsetAnimation();
            showLayout(this.botAttachLayouts.get(id), -id);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 implements BotWebViewContainer.Delegate {
        private ValueAnimator botButtonAnimator;
        final /* synthetic */ ChatAttachAlertBotWebViewLayout val$webViewLayout;

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onSendWebViewData(String str) {
            BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public /* synthetic */ void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass1(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout) {
            ChatAttachAlert.this = this$0;
            this.val$webViewLayout = chatAttachAlertBotWebViewLayout;
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onCloseRequested(final Runnable callback) {
            if (ChatAttachAlert.this.currentAttachLayout != this.val$webViewLayout) {
                return;
            }
            ChatAttachAlert.this.setFocusable(false);
            ChatAttachAlert.this.getWindow().setSoftInputMode(48);
            ChatAttachAlert.this.dismiss();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.AnonymousClass1.lambda$onCloseRequested$0(callback);
                }
            }, 150L);
        }

        public static /* synthetic */ void lambda$onCloseRequested$0(Runnable callback) {
            if (callback != null) {
                callback.run();
            }
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetActionBarColor(String colorKey) {
            final int from = ((ColorDrawable) ChatAttachAlert.this.actionBar.getBackground()).getColor();
            final int to = ChatAttachAlert.this.getThemedColor(colorKey);
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlert.AnonymousClass1.this.m2396x676708a3(from, to, valueAnimator);
                }
            });
            animator.start();
        }

        /* renamed from: lambda$onWebAppSetActionBarColor$1$org-telegram-ui-Components-ChatAttachAlert$1 */
        public /* synthetic */ void m2396x676708a3(int from, int to, ValueAnimator animation) {
            ChatAttachAlert.this.actionBar.setBackgroundColor(ColorUtils.blendARGB(from, to, ((Float) animation.getAnimatedValue()).floatValue()));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppSetBackgroundColor(int color) {
            this.val$webViewLayout.setCustomBackground(color);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppOpenInvoice(final String slug, TLObject response) {
            BaseFragment parentFragment = ChatAttachAlert.this.baseFragment;
            PaymentFormActivity paymentFormActivity = null;
            if (response instanceof TLRPC.TL_payments_paymentForm) {
                TLRPC.TL_payments_paymentForm form = (TLRPC.TL_payments_paymentForm) response;
                MessagesController.getInstance(ChatAttachAlert.this.currentAccount).putUsers(form.users, false);
                paymentFormActivity = new PaymentFormActivity(form, slug, parentFragment);
            } else if (response instanceof TLRPC.TL_payments_paymentReceipt) {
                paymentFormActivity = new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response);
            }
            if (paymentFormActivity != null) {
                this.val$webViewLayout.scrollToTop();
                AndroidUtilities.hideKeyboard(this.val$webViewLayout);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(parentFragment.getParentActivity(), ChatAttachAlert.this.resourcesProvider);
                overlayActionBarLayoutDialog.show();
                final ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda3
                    @Override // org.telegram.ui.PaymentFormActivity.PaymentFormCallback
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        ChatAttachAlert.AnonymousClass1.lambda$onWebAppOpenInvoice$2(OverlayActionBarLayoutDialog.this, chatAttachAlertBotWebViewLayout, slug, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(ChatAttachAlert.this.resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        public static /* synthetic */ void lambda$onWebAppOpenInvoice$2(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout webViewLayout, String slug, PaymentFormActivity.InvoiceStatus status) {
            overlayActionBarLayoutDialog.dismiss();
            webViewLayout.getWebViewContainer().onInvoiceStatusUpdate(slug, status.name().toLowerCase(Locale.ROOT));
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onWebAppExpand() {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.canExpandByRequest()) {
                this.val$webViewLayout.scrollToTop();
            }
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetupMainButton(final boolean isVisible, boolean isActive, String text, int color, int textColor, final boolean isProgressVisible) {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.isBotButtonAvailable()) {
                ChatAttachAlert.this.botMainButtonTextView.setClickable(isActive);
                ChatAttachAlert.this.botMainButtonTextView.setText(text);
                ChatAttachAlert.this.botMainButtonTextView.setTextColor(textColor);
                ChatAttachAlert.this.botMainButtonTextView.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(color));
                float f = 0.0f;
                float f2 = 1.0f;
                if (ChatAttachAlert.this.botButtonWasVisible != isVisible) {
                    ChatAttachAlert.this.botButtonWasVisible = isVisible;
                    ValueAnimator valueAnimator = this.botButtonAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.cancel();
                    }
                    float[] fArr = new float[2];
                    fArr[0] = isVisible ? 0.0f : 1.0f;
                    fArr[1] = isVisible ? 1.0f : 0.0f;
                    ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(250L);
                    this.botButtonAnimator = duration;
                    duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            ChatAttachAlert.AnonymousClass1.this.m2395x94420554(valueAnimator2);
                        }
                    });
                    this.botButtonAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.1.1
                        {
                            AnonymousClass1.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            if (isVisible) {
                                ChatAttachAlert.this.botMainButtonTextView.setAlpha(0.0f);
                                ChatAttachAlert.this.botMainButtonTextView.setVisibility(0);
                                int offsetY = AndroidUtilities.dp(36.0f);
                                for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                                    ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(offsetY);
                                }
                                return;
                            }
                            ChatAttachAlert.this.buttonsRecyclerView.setAlpha(0.0f);
                            ChatAttachAlert.this.buttonsRecyclerView.setVisibility(0);
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (!isVisible) {
                                ChatAttachAlert.this.botMainButtonTextView.setVisibility(8);
                            } else {
                                ChatAttachAlert.this.buttonsRecyclerView.setVisibility(8);
                            }
                            int offsetY = isVisible ? AndroidUtilities.dp(36.0f) : 0;
                            for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                                ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(offsetY);
                            }
                            if (AnonymousClass1.this.botButtonAnimator == animation) {
                                AnonymousClass1.this.botButtonAnimator = null;
                            }
                        }
                    });
                    this.botButtonAnimator.start();
                }
                ChatAttachAlert.this.botProgressView.setProgressColor(textColor);
                if (ChatAttachAlert.this.botButtonProgressWasVisible != isProgressVisible) {
                    ChatAttachAlert.this.botProgressView.animate().cancel();
                    if (isProgressVisible) {
                        ChatAttachAlert.this.botProgressView.setAlpha(0.0f);
                        ChatAttachAlert.this.botProgressView.setVisibility(0);
                    }
                    ViewPropertyAnimator animate = ChatAttachAlert.this.botProgressView.animate();
                    if (isProgressVisible) {
                        f = 1.0f;
                    }
                    ViewPropertyAnimator scaleX = animate.alpha(f).scaleX(isProgressVisible ? 1.0f : 0.1f);
                    if (!isProgressVisible) {
                        f2 = 0.1f;
                    }
                    scaleX.scaleY(f2).setDuration(250L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.1.2
                        {
                            AnonymousClass1.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            ChatAttachAlert.this.botButtonProgressWasVisible = isProgressVisible;
                            if (!isProgressVisible) {
                                ChatAttachAlert.this.botProgressView.setVisibility(8);
                            }
                        }
                    }).start();
                }
            }
        }

        /* renamed from: lambda$onSetupMainButton$3$org-telegram-ui-Components-ChatAttachAlert$1 */
        public /* synthetic */ void m2395x94420554(ValueAnimator animation) {
            float value = ((Float) animation.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.buttonsRecyclerView.setAlpha(1.0f - value);
            ChatAttachAlert.this.botMainButtonTextView.setAlpha(value);
            ChatAttachAlert.this.botMainButtonOffsetY = AndroidUtilities.dp(36.0f) * value;
            ChatAttachAlert.this.shadow.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
            ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
        }

        @Override // org.telegram.ui.Components.BotWebViewContainer.Delegate
        public void onSetBackButtonVisible(boolean visible) {
            AndroidUtilities.updateImageViewImageAnimated(ChatAttachAlert.this.actionBar.getBackButton(), visible ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
        }
    }

    /* loaded from: classes5.dex */
    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3);

        void didSelectBot(TLRPC.User user);

        void doOnIdle(Runnable runnable);

        View getRevealView();

        boolean needEnterComment();

        void onCameraOpened();

        void openAvatarsSearch();

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ChatAttachViewDelegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static View $default$getRevealView(ChatAttachViewDelegate _this) {
                return null;
            }

            public static void $default$didSelectBot(ChatAttachViewDelegate _this, TLRPC.User user) {
            }

            public static boolean $default$needEnterComment(ChatAttachViewDelegate _this) {
                return false;
            }

            public static void $default$doOnIdle(ChatAttachViewDelegate _this, Runnable runnable) {
                runnable.run();
            }

            public static void $default$openAvatarsSearch(ChatAttachViewDelegate _this) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class AttachAlertLayout extends FrameLayout {
        protected ChatAttachAlert parentAlert;
        protected final Theme.ResourcesProvider resourcesProvider;

        public AttachAlertLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            this.parentAlert = alert;
        }

        boolean onSheetKeyDown(int keyCode, KeyEvent event) {
            return false;
        }

        public boolean onDismiss() {
            return false;
        }

        boolean onCustomMeasure(View view, int width, int height) {
            return false;
        }

        boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
            return false;
        }

        boolean onContainerViewTouchEvent(MotionEvent event) {
            return false;
        }

        void onPreMeasure(int availableWidth, int availableHeight) {
        }

        void onMenuItemClick(int id) {
        }

        boolean hasCustomBackground() {
            return false;
        }

        int getCustomBackground() {
            return 0;
        }

        void onButtonsTranslationYUpdated() {
        }

        boolean canScheduleMessages() {
            return true;
        }

        void checkColors() {
        }

        ArrayList<ThemeDescription> getThemeDescriptions() {
            return null;
        }

        void onPause() {
        }

        public void onResume() {
        }

        boolean canDismissWithTouchOutside() {
            return true;
        }

        void onDismissWithButtonClick(int item) {
        }

        public void onContainerTranslationUpdated(float currentPanTranslationY) {
        }

        void onHideShowProgress(float progress) {
        }

        void onOpenAnimationEnd() {
        }

        void onInit(boolean mediaEnabled) {
        }

        int getSelectedItemsCount() {
            return 0;
        }

        void onSelectedItemsCountChanged(int count) {
        }

        void applyCaption(CharSequence text) {
        }

        void onDestroy() {
        }

        public void onHide() {
        }

        public void onHidden() {
        }

        public int getCurrentItemTop() {
            return 0;
        }

        int getFirstOffset() {
            return 0;
        }

        int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12.0f : 17.0f);
        }

        public int getListTopPadding() {
            return 0;
        }

        int needsActionBar() {
            return 0;
        }

        void sendSelectedItems(boolean notify, int scheduleDate) {
        }

        void onShow(AttachAlertLayout previousLayout) {
        }

        void onShown() {
        }

        void scrollToTop() {
        }

        public boolean onBackPressed() {
            return false;
        }

        public int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        boolean shouldHideBottomButtons() {
            return true;
        }

        public void onPanTransitionStart(boolean keyboardVisible, int contentHeight) {
        }

        public void onPanTransitionEnd() {
        }
    }

    /* loaded from: classes5.dex */
    public class AttachButton extends FrameLayout {
        private String backgroundKey;
        private Animator checkAnimator;
        private boolean checked;
        private float checkedState;
        private int currentId;
        private RLottieImageView imageView;
        private String textKey;
        private TextView textView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AttachButton(Context context) {
            super(context);
            ChatAttachAlert.this = r10;
            setWillNotDraw(false);
            setFocusable(true);
            RLottieImageView rLottieImageView = new RLottieImageView(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.AttachButton.1
                {
                    AttachButton.this = this;
                }

                @Override // android.view.View
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    AttachButton.this.invalidate();
                }
            };
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(32, 32.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(r10.getThemedColor(Theme.key_dialogTextGray2));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing(-AndroidUtilities.dp(2.0f), 1.0f);
            this.textView.setImportantForAccessibility(2);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(this.textView.getText());
            info.setEnabled(true);
            info.setSelected(this.checked);
        }

        void updateCheckedState(boolean animate) {
            if (this.checked != (((long) this.currentId) == ChatAttachAlert.this.selectedId)) {
                this.checked = ((long) this.currentId) == ChatAttachAlert.this.selectedId;
                Animator animator = this.checkAnimator;
                if (animator != null) {
                    animator.cancel();
                }
                float f = 1.0f;
                if (animate) {
                    if (this.checked) {
                        this.imageView.setProgress(0.0f);
                        this.imageView.playAnimation();
                    }
                    float[] fArr = new float[1];
                    if (!this.checked) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                    this.checkAnimator = ofFloat;
                    ofFloat.setDuration(200L);
                    this.checkAnimator.start();
                    return;
                }
                this.imageView.stopAnimation();
                this.imageView.setProgress(0.0f);
                if (!this.checked) {
                    f = 0.0f;
                }
                setCheckedState(f);
            }
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            this.imageView.setScaleX(1.0f - (state * 0.06f));
            this.imageView.setScaleY(1.0f - (0.06f * state));
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
            invalidate();
        }

        public float getCheckedState() {
            return this.checkedState;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setTextAndIcon(int id, CharSequence text, RLottieDrawable drawable, String background, String textColor) {
            this.currentId = id;
            this.textView.setText(text);
            this.imageView.setAnimation(drawable);
            this.backgroundKey = background;
            this.textKey = textColor;
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float scale = this.imageView.getScaleX() + (this.checkedState * 0.06f);
            float radius = AndroidUtilities.dp(23.0f) * scale;
            float cx = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f);
            float cy = this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2.0f);
            ChatAttachAlert.this.attachButtonPaint.setColor(ChatAttachAlert.this.getThemedColor(this.backgroundKey));
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
            ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3.0f) * scale);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(cx, cy, radius - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius - (AndroidUtilities.dp(5.0f) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* loaded from: classes5.dex */
    public class AttachBotButton extends FrameLayout {
        private TLRPC.TL_attachMenuBot attachMenuBot;
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private ValueAnimator checkAnimator;
        private Boolean checked;
        private float checkedState;
        private TLRPC.User currentUser;
        private int iconBackgroundColor;
        private BackupImageView imageView;
        private TextView nameTextView;
        private View selector;
        private int textColor;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AttachBotButton(Context context) {
            super(context);
            ChatAttachAlert.this = r10;
            setWillNotDraw(false);
            setFocusable(true);
            setFocusableInTouchMode(true);
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(context, r10);
            this.imageView = anonymousClass1;
            anonymousClass1.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View view = new View(context);
                this.selector = view;
                view.setBackground(Theme.createSelectorDrawable(r10.getThemedColor(Theme.key_dialogButtonSelector), 1, AndroidUtilities.dp(23.0f)));
                addView(this.selector, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            }
            TextView textView = new TextView(context);
            this.nameTextView = textView;
            textView.setTextSize(1, 12.0f);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 60.0f, 6.0f, 0.0f));
        }

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 extends BackupImageView {
            final /* synthetic */ ChatAttachAlert val$this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(Context context, ChatAttachAlert chatAttachAlert) {
                super(context);
                AttachBotButton.this = this$1;
                this.val$this$0 = chatAttachAlert;
                this.imageReceiver.setDelegate(ChatAttachAlert$AttachBotButton$1$$ExternalSyntheticLambda0.INSTANCE);
            }

            public static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver1, boolean set, boolean thumb, boolean memCache) {
                Drawable drawable = imageReceiver1.getDrawable();
                if (drawable instanceof RLottieDrawable) {
                    ((RLottieDrawable) drawable).setCustomEndFrame(0);
                    ((RLottieDrawable) drawable).stop();
                    ((RLottieDrawable) drawable).setProgress(0.0f, false);
                }
            }

            @Override // android.view.View
            public void setScaleX(float scaleX) {
                super.setScaleX(scaleX);
                AttachBotButton.this.invalidate();
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.selector != null && this.checked.booleanValue()) {
                info.setCheckable(true);
                info.setChecked(true);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            this.imageView.setScaleX(1.0f - (state * 0.06f));
            this.imageView.setScaleY(1.0f - (0.06f * state));
            this.nameTextView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), this.textColor, this.checkedState));
            invalidate();
        }

        private void updateMargins() {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.nameTextView.getLayoutParams();
            params.topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 62.0f : 60.0f);
            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) this.imageView.getLayoutParams();
            params2.topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 11.0f : 9.0f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.attachMenuBot != null) {
                float imageScale = this.imageView.getScaleX();
                float scale = (this.checkedState * 0.06f) + imageScale;
                float radius = AndroidUtilities.dp(23.0f) * scale;
                float cx = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f);
                float cy = this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2.0f);
                ChatAttachAlert.this.attachButtonPaint.setColor(this.iconBackgroundColor);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
                ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3.0f) * scale);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(cx, cy, radius - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, radius - (AndroidUtilities.dp(5.0f) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        void updateCheckedState(boolean animate) {
            boolean newChecked = this.attachMenuBot != null && (-this.currentUser.id) == ChatAttachAlert.this.selectedId;
            Boolean bool = this.checked;
            if (bool != null && bool.booleanValue() == newChecked && animate) {
                return;
            }
            this.checked = Boolean.valueOf(newChecked);
            ValueAnimator valueAnimator = this.checkAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            RLottieDrawable drawable = this.imageView.getImageReceiver().getLottieAnimation();
            float f = 1.0f;
            if (animate) {
                if (this.checked.booleanValue() && drawable != null) {
                    drawable.setAutoRepeat(0);
                    drawable.setCustomEndFrame(-1);
                    drawable.setProgress(0.0f, false);
                    drawable.start();
                }
                float[] fArr = new float[2];
                fArr[0] = this.checked.booleanValue() ? 0.0f : 1.0f;
                if (!this.checked.booleanValue()) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.checkAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatAttachAlert.AttachBotButton.this.m2401x7fcd93d6(valueAnimator2);
                    }
                });
                this.checkAnimator.setDuration(200L);
                this.checkAnimator.start();
                return;
            }
            if (drawable != null) {
                drawable.stop();
                drawable.setProgress(0.0f, false);
            }
            if (!this.checked.booleanValue()) {
                f = 0.0f;
            }
            setCheckedState(f);
        }

        /* renamed from: lambda$updateCheckedState$0$org-telegram-ui-Components-ChatAttachAlert$AttachBotButton */
        public /* synthetic */ void m2401x7fcd93d6(ValueAnimator animation) {
            setCheckedState(((Float) animation.getAnimatedValue()).floatValue());
        }

        public void setUser(TLRPC.User user) {
            if (user != null) {
                this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2));
                this.currentUser = user;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
                this.imageView.setSize(-1, -1);
                this.imageView.setColorFilter(null);
                this.attachMenuBot = null;
                this.selector.setVisibility(0);
                updateMargins();
                setCheckedState(0.0f);
                invalidate();
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x0071, code lost:
            if (r5.equals(org.telegram.messenger.MediaDataController.ATTACH_MENU_BOT_COLOR_LIGHT_ICON) != false) goto L25;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void setAttachBot(org.telegram.tgnet.TLRPC.User r12, org.telegram.tgnet.TLRPC.TL_attachMenuBot r13) {
            /*
                Method dump skipped, instructions count: 348
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AttachBotButton.setAttachBot(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_attachMenuBot):void");
        }
    }

    public ChatAttachAlert(Context context, BaseFragment parentFragment, boolean forceDarkTheme, boolean showingFromDialog) {
        this(context, parentFragment, forceDarkTheme, showingFromDialog, null);
    }

    public ChatAttachAlert(Context context, BaseFragment parentFragment, boolean forceDarkTheme, final boolean showingFromDialog, final Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.canOpenPreview = false;
        this.isSoundPicker = false;
        this.translationProgress = 0.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION = new AnimationProperties.FloatProperty<AttachAlertLayout>("translation") { // from class: org.telegram.ui.Components.ChatAttachAlert.2
            {
                ChatAttachAlert.this = this;
            }

            public void setValue(AttachAlertLayout object, float value) {
                ChatAttachAlert.this.translationProgress = value;
                if ((ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                    int width = Math.max(ChatAttachAlert.this.nextAttachLayout.getWidth(), ChatAttachAlert.this.currentAttachLayout.getWidth());
                    if (ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) {
                        ChatAttachAlert.this.currentAttachLayout.setTranslationX((-width) * value);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((1.0f - value) * width);
                    } else {
                        ChatAttachAlert.this.currentAttachLayout.setTranslationX(width * value);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((-width) * (1.0f - value));
                    }
                } else {
                    if (value <= 0.7f) {
                        if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.locationLayout) {
                            ChatAttachAlert.this.nextAttachLayout.setAlpha(0.0f);
                        }
                    } else {
                        float alpha = 1.0f - ((1.0f - value) / 0.3f);
                        if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.locationLayout) {
                            ChatAttachAlert.this.currentAttachLayout.setAlpha(1.0f - alpha);
                            ChatAttachAlert.this.nextAttachLayout.setAlpha(1.0f);
                        } else {
                            ChatAttachAlert.this.nextAttachLayout.setAlpha(alpha);
                            ChatAttachAlert.this.nextAttachLayout.onHideShowProgress(alpha);
                        }
                    }
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                        chatAttachAlert.updateSelectedPosition(chatAttachAlert.nextAttachLayout == ChatAttachAlert.this.pollLayout ? 1 : 0);
                    }
                    ChatAttachAlert.this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f) * value);
                    ChatAttachAlert.this.currentAttachLayout.onHideShowProgress(1.0f - Math.min(1.0f, value / 0.7f));
                    ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                }
                ChatAttachAlert.this.containerView.invalidate();
            }

            public Float get(AttachAlertLayout object) {
                return Float.valueOf(ChatAttachAlert.this.translationProgress);
            }
        };
        this.layouts = new AttachAlertLayout[7];
        this.botAttachLayouts = new LongSparseArray<>();
        this.textPaint = new TextPaint(1);
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.sendButtonEnabled = true;
        this.sendButtonEnabledProgress = 1.0f;
        this.cornerRadius = 1.0f;
        this.botButtonProgressWasVisible = false;
        this.botButtonWasVisible = false;
        this.currentAccount = UserConfig.selectedAccount;
        this.mediaEnabled = true;
        this.pollsEnabled = true;
        this.maxSelectedPhotos = -1;
        this.allowOrder = true;
        this.attachItemSize = AndroidUtilities.dp(85.0f);
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.scrollOffsetY = new int[2];
        this.attachButtonPaint = new Paint(1);
        this.exclusionRects = new ArrayList<>();
        this.exclustionRect = new android.graphics.Rect();
        this.ATTACH_ALERT_PROGRESS = new AnimationProperties.FloatProperty<ChatAttachAlert>("openProgress") { // from class: org.telegram.ui.Components.ChatAttachAlert.20
            private float openProgress;

            {
                ChatAttachAlert.this = this;
            }

            public void setValue(ChatAttachAlert object, float value) {
                float scale;
                int N = ChatAttachAlert.this.buttonsRecyclerView.getChildCount();
                for (int a = 0; a < N; a++) {
                    float startTime = (3 - a) * 32.0f;
                    View child = ChatAttachAlert.this.buttonsRecyclerView.getChildAt(a);
                    if (value > startTime) {
                        float elapsedTime = value - startTime;
                        if (elapsedTime <= 200.0f) {
                            scale = CubicBezierInterpolator.EASE_OUT.getInterpolation(elapsedTime / 200.0f) * 1.1f;
                            child.setAlpha(CubicBezierInterpolator.EASE_BOTH.getInterpolation(elapsedTime / 200.0f));
                        } else {
                            child.setAlpha(1.0f);
                            float elapsedTime2 = elapsedTime - 200.0f;
                            scale = elapsedTime2 <= 100.0f ? 1.1f - (CubicBezierInterpolator.EASE_IN.getInterpolation(elapsedTime2 / 100.0f) * 0.1f) : 1.0f;
                        }
                    } else {
                        scale = 0.0f;
                    }
                    if (child instanceof AttachButton) {
                        AttachButton attachButton = (AttachButton) child;
                        attachButton.textView.setScaleX(scale);
                        attachButton.textView.setScaleY(scale);
                        attachButton.imageView.setScaleX(scale);
                        attachButton.imageView.setScaleY(scale);
                    } else if (child instanceof AttachBotButton) {
                        AttachBotButton attachButton2 = (AttachBotButton) child;
                        attachButton2.nameTextView.setScaleX(scale);
                        attachButton2.nameTextView.setScaleY(scale);
                        attachButton2.imageView.setScaleX(scale);
                        attachButton2.imageView.setScaleY(scale);
                    }
                }
            }

            public Float get(ChatAttachAlert object) {
                return Float.valueOf(this.openProgress);
            }
        };
        this.confirmationAlertShown = false;
        this.allowPassConfirmationAlert = false;
        this.forceDarkTheme = forceDarkTheme;
        this.showingFromDialog = showingFromDialog;
        this.drawNavigationBar = true;
        this.inBubbleMode = (parentFragment instanceof ChatActivity) && parentFragment.isInBubbleMode();
        this.openInterpolator = new OvershootInterpolator(0.7f);
        this.baseFragment = parentFragment;
        this.useSmoothKeyboard = true;
        setDelegate(this);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        this.exclusionRects.add(this.exclustionRect);
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(context, forceDarkTheme);
        this.sizeNotifierFrameLayout = anonymousClass3;
        anonymousClass3.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.4
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
            public void onSizeChanged(int keyboardHeight, boolean isWidthGreater) {
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout) {
                    ChatAttachAlert.this.currentAttachLayout.invalidate();
                }
            }
        });
        this.containerView = this.sizeNotifierFrameLayout;
        this.containerView.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        ActionBar actionBar = new ActionBar(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlert.5
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            public void setAlpha(float alpha) {
                float oldAlpha = getAlpha();
                super.setAlpha(alpha);
                if (oldAlpha != alpha) {
                    ChatAttachAlert.this.containerView.invalidate();
                    if (ChatAttachAlert.this.frameLayout2 != null && ChatAttachAlert.this.buttonsRecyclerView != null) {
                        float f = 1.0f;
                        if (ChatAttachAlert.this.frameLayout2.getTag() == null) {
                            if (ChatAttachAlert.this.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                                ChatAttachAlert.this.buttonsRecyclerView.setAlpha(1.0f - alpha);
                                ChatAttachAlert.this.shadow.setAlpha(1.0f - alpha);
                                ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(AndroidUtilities.dp(44.0f) * alpha);
                            }
                            ChatAttachAlert.this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f) * alpha);
                            ChatAttachAlert.this.shadow.setTranslationY((AndroidUtilities.dp(84.0f) * alpha) + ChatAttachAlert.this.botMainButtonOffsetY);
                        } else if (ChatAttachAlert.this.currentAttachLayout == null) {
                            if (alpha != 0.0f) {
                                f = 0.0f;
                            }
                            float value = f;
                            if (ChatAttachAlert.this.buttonsRecyclerView.getAlpha() != value) {
                                ChatAttachAlert.this.buttonsRecyclerView.setAlpha(value);
                            }
                        }
                    }
                }
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(getThemedColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setTitleColor(getThemedColor(Theme.key_dialogTextBlack));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.ChatAttachAlert.6
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatAttachAlert.this.currentAttachLayout.onBackPressed()) {
                        return;
                    }
                    ChatAttachAlert.this.dismiss();
                    return;
                }
                ChatAttachAlert.this.currentAttachLayout.onMenuItemClick(id);
            }
        });
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, getThemedColor(Theme.key_dialogTextBlack), false, resourcesProvider);
        this.selectedMenuItem = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.selectedMenuItem.setIcon(R.drawable.ic_ab_other);
        this.selectedMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.selectedMenuItem.setVisibility(4);
        this.selectedMenuItem.setAlpha(0.0f);
        this.selectedMenuItem.setSubMenuOpenSide(2);
        this.selectedMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda19
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i) {
                ChatAttachAlert.this.m2366lambda$new$0$orgtelegramuiComponentsChatAttachAlert(i);
            }
        });
        this.selectedMenuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
        this.selectedMenuItem.setTranslationX(AndroidUtilities.dp(6.0f));
        this.selectedMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 6));
        this.selectedMenuItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda34
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.m2367lambda$new$1$orgtelegramuiComponentsChatAttachAlert(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, null, 0, getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader), true, resourcesProvider);
        this.doneItem = actionBarMenuItem2;
        actionBarMenuItem2.setLongClickEnabled(false);
        this.doneItem.setText(LocaleController.getString("Create", R.string.Create).toUpperCase());
        this.doneItem.setVisibility(4);
        this.doneItem.setAlpha(0.0f);
        this.doneItem.setTranslationX(-AndroidUtilities.dp(12.0f));
        this.doneItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 3));
        this.doneItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.m2374lambda$new$2$orgtelegramuiComponentsChatAttachAlert(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, null, 0, getThemedColor(Theme.key_dialogTextBlack), false, resourcesProvider);
        this.searchItem = actionBarMenuItem3;
        actionBarMenuItem3.setLongClickEnabled(false);
        this.searchItem.setIcon(R.drawable.ic_ab_search);
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setVisibility(4);
        this.searchItem.setAlpha(0.0f);
        this.searchItem.setTranslationX(-AndroidUtilities.dp(42.0f));
        this.searchItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 6));
        this.searchItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.m2375lambda$new$3$orgtelegramuiComponentsChatAttachAlert(showingFromDialog, view);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.8
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                ChatAttachAlert.this.updateSelectedPosition(0);
                ChatAttachAlert.this.containerView.invalidate();
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onTouchEvent(event);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onInterceptTouchEvent(event);
            }
        };
        this.headerView = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlert.this.m2376lambda$new$4$orgtelegramuiComponentsChatAttachAlert(view);
            }
        });
        this.headerView.setAlpha(0.0f);
        this.headerView.setVisibility(4);
        LinearLayout linearLayout = new LinearLayout(context);
        this.selectedView = linearLayout;
        linearLayout.setOrientation(0);
        this.selectedView.setGravity(16);
        TextView textView = new TextView(context);
        this.selectedTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.selectedTextView.setTextSize(1, 16.0f);
        this.selectedTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.selectedTextView.setGravity(19);
        this.selectedView.addView(this.selectedTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.selectedArrowImageView = new ImageView(context);
        Drawable arrowRight = getContext().getResources().getDrawable(R.drawable.attach_arrow_right).mutate();
        arrowRight.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
        this.selectedArrowImageView.setImageDrawable(arrowRight);
        this.selectedArrowImageView.setVisibility(8);
        this.selectedView.addView(this.selectedArrowImageView, LayoutHelper.createLinear(-2, -2, 16, 4, 1, 0, 0));
        this.selectedView.setAlpha(1.0f);
        this.headerView.addView(this.selectedView, LayoutHelper.createFrame(-2, -1.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.mediaPreviewView = linearLayout2;
        linearLayout2.setOrientation(0);
        this.mediaPreviewView.setGravity(16);
        ImageView arrowView = new ImageView(context);
        Drawable arrowLeft = getContext().getResources().getDrawable(R.drawable.attach_arrow_left).mutate();
        arrowLeft.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
        arrowView.setImageDrawable(arrowLeft);
        this.mediaPreviewView.addView(arrowView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 4, 0));
        TextView textView2 = new TextView(context);
        this.mediaPreviewTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.mediaPreviewTextView.setTextSize(1, 16.0f);
        this.mediaPreviewTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.mediaPreviewTextView.setGravity(19);
        this.mediaPreviewTextView.setText(LocaleController.getString("AttachMediaPreview", R.string.AttachMediaPreview));
        this.mediaPreviewView.setAlpha(0.0f);
        this.mediaPreviewView.addView(this.mediaPreviewTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.headerView.addView(this.mediaPreviewView, LayoutHelper.createFrame(-2, -1.0f));
        AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = new ChatAttachAlertPhotoLayout(this, context, forceDarkTheme, resourcesProvider);
        this.photoLayout = chatAttachAlertPhotoLayout;
        attachAlertLayoutArr[0] = chatAttachAlertPhotoLayout;
        chatAttachAlertPhotoLayout.setTranslationX(0.0f);
        this.currentAttachLayout = this.photoLayout;
        this.selectedId = 1L;
        this.containerView.addView(this.photoLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, -2.0f, 51, 23.0f, 0.0f, 48.0f, 0.0f));
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.containerView.addView(this.selectedMenuItem, LayoutHelper.createFrame(48, 48, 53));
        this.containerView.addView(this.searchItem, LayoutHelper.createFrame(48, 48, 53));
        this.containerView.addView(this.doneItem, LayoutHelper.createFrame(-2, 48, 53));
        View view = new View(context);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        View view2 = new View(context);
        this.shadow = view2;
        view2.setBackgroundResource(R.drawable.attach_shadow);
        this.shadow.getBackground().setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 84.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.9
            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                ChatAttachAlert.this.currentAttachLayout.onButtonsTranslationYUpdated();
            }
        };
        this.buttonsRecyclerView = recyclerListView;
        ButtonsAdapter buttonsAdapter = new ButtonsAdapter(context);
        this.buttonsAdapter = buttonsAdapter;
        recyclerListView.setAdapter(buttonsAdapter);
        RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false);
        this.buttonsLayoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.buttonsRecyclerView.setVerticalScrollBarEnabled(false);
        this.buttonsRecyclerView.setHorizontalScrollBarEnabled(false);
        this.buttonsRecyclerView.setItemAnimator(null);
        this.buttonsRecyclerView.setLayoutAnimation(null);
        this.buttonsRecyclerView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.buttonsRecyclerView.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        this.buttonsRecyclerView.setImportantForAccessibility(1);
        this.containerView.addView(this.buttonsRecyclerView, LayoutHelper.createFrame(-1, 84, 83));
        this.buttonsRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda28
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view3, int i) {
                ChatAttachAlert.this.m2379lambda$new$7$orgtelegramuiComponentsChatAttachAlert(resourcesProvider, view3, i);
            }
        });
        this.buttonsRecyclerView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda29
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view3, int i) {
                return ChatAttachAlert.this.m2380lambda$new$8$orgtelegramuiComponentsChatAttachAlert(view3, i);
            }
        });
        TextView textView3 = new TextView(context);
        this.botMainButtonTextView = textView3;
        textView3.setVisibility(8);
        this.botMainButtonTextView.setAlpha(0.0f);
        this.botMainButtonTextView.setSingleLine();
        this.botMainButtonTextView.setGravity(17);
        this.botMainButtonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int padding = AndroidUtilities.dp(16.0f);
        this.botMainButtonTextView.setPadding(padding, 0, padding, 0);
        this.botMainButtonTextView.setTextSize(1, 14.0f);
        this.botMainButtonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatAttachAlert.this.m2381lambda$new$9$orgtelegramuiComponentsChatAttachAlert(view3);
            }
        });
        this.containerView.addView(this.botMainButtonTextView, LayoutHelper.createFrame(-1, 48, 83));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.botProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.botProgressView.setAlpha(0.0f);
        this.botProgressView.setScaleX(0.1f);
        this.botProgressView.setScaleY(0.1f);
        this.botProgressView.setVisibility(8);
        this.containerView.addView(this.botProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        AnonymousClass10 anonymousClass10 = new AnonymousClass10(context, forceDarkTheme);
        this.frameLayout2 = anonymousClass10;
        anonymousClass10.setWillNotDraw(false);
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setAlpha(0.0f);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2, 83));
        this.frameLayout2.setOnTouchListener(ChatAttachAlert$$ExternalSyntheticLambda5.INSTANCE);
        NumberTextView numberTextView = new NumberTextView(context);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        numberTextView.setTextSize(15);
        numberTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        numberTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        numberTextView.setCenterAlign(true);
        this.frameLayout2.addView(numberTextView, LayoutHelper.createFrame(56, 20.0f, 85, 3.0f, 0.0f, 14.0f, 78.0f));
        this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        AnonymousClass11 anonymousClass11 = new AnonymousClass11(context, this.sizeNotifierFrameLayout, null, 1, resourcesProvider);
        this.commentTextView = anonymousClass11;
        anonymousClass11.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.commentTextView.onResume();
        this.commentTextView.getEditText().addTextChangedListener(new AnonymousClass12());
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 84.0f, 0.0f));
        this.frameLayout2.setClipChildren(false);
        this.commentTextView.setClipChildren(false);
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.13
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout) {
                    info.setText(LocaleController.formatPluralString("AccDescrSendPhotos", ChatAttachAlert.this.photoLayout.getSelectedItemsCount(), new Object[0]));
                } else if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.documentLayout) {
                    info.setText(LocaleController.formatPluralString("AccDescrSendFiles", ChatAttachAlert.this.documentLayout.getSelectedItemsCount(), new Object[0]));
                } else if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.audioLayout) {
                    info.setText(LocaleController.formatPluralString("AccDescrSendAudio", ChatAttachAlert.this.audioLayout.getSelectedItemsCount(), new Object[0]));
                }
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
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        this.writeButton = new ImageView(context);
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_dialogFloatingButton), getThemedColor(Build.VERSION.SDK_INT >= 21 ? Theme.key_dialogFloatingButtonPressed : Theme.key_dialogFloatingButton));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, this.writeButtonDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButtonDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
        this.writeButton.setImageResource(R.drawable.attach_send);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setImportantForAccessibility(2);
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlert.14
                {
                    ChatAttachAlert.this = this;
                }

                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view3, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                ChatAttachAlert.this.m2369lambda$new$12$orgtelegramuiComponentsChatAttachAlert(resourcesProvider, view3);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda4
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view3) {
                return ChatAttachAlert.this.m2373lambda$new$16$orgtelegramuiComponentsChatAttachAlert(resourcesProvider, view3);
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        View view3 = new View(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.16
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                String text = String.format("%d", Integer.valueOf(Math.max(1, ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount())));
                int textSize = (int) Math.ceil(ChatAttachAlert.this.textPaint.measureText(text));
                int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                int cx = getMeasuredWidth() / 2;
                int color = ChatAttachAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck);
                TextPaint textPaint = ChatAttachAlert.this.textPaint;
                double alpha = Color.alpha(color);
                double d = ChatAttachAlert.this.sendButtonEnabledProgress;
                Double.isNaN(d);
                Double.isNaN(alpha);
                textPaint.setColor(ColorUtils.setAlphaComponent(color, (int) (alpha * ((d * 0.42d) + 0.58d))));
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogBackground));
                ChatAttachAlert.this.rect.set(cx - (size / 2), 0.0f, (size / 2) + cx, getMeasuredHeight());
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ChatAttachAlert.this.paint);
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogRoundCheckBox));
                ChatAttachAlert.this.rect.set((cx - (size / 2)) + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), ((size / 2) + cx) - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ChatAttachAlert.this.paint);
                canvas.drawText(text, cx - (textSize / 2), AndroidUtilities.dp(16.2f), ChatAttachAlert.this.textPaint);
            }
        };
        this.selectedCountView = view3;
        view3.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.containerView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        if (forceDarkTheme) {
            checkColors();
            this.navBarColorKey = null;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$3 */
    /* loaded from: classes5.dex */
    public class AnonymousClass3 extends SizeNotifierFrameLayout {
        private boolean ignoreLayout;
        private float initialTranslationY;
        private int lastNotifyWidth;
        final /* synthetic */ boolean val$forceDarkTheme;
        private RectF rect = new RectF();
        AdjustPanLayoutHelper adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) { // from class: org.telegram.ui.Components.ChatAttachAlert.3.1
            {
                AnonymousClass3.this = this;
            }

            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
                super.onTransitionStart(keyboardVisible, contentHeight);
                if (ChatAttachAlert.this.previousScrollOffsetY <= 0 || ChatAttachAlert.this.previousScrollOffsetY == ChatAttachAlert.this.scrollOffsetY[0] || !keyboardVisible) {
                    ChatAttachAlert.this.fromScrollY = -1.0f;
                } else {
                    ChatAttachAlert.this.fromScrollY = ChatAttachAlert.this.previousScrollOffsetY;
                    ChatAttachAlert.this.toScrollY = ChatAttachAlert.this.scrollOffsetY[0];
                }
                AnonymousClass3.this.invalidate();
                ChatAttachAlert.this.currentAttachLayout.onPanTransitionStart(keyboardVisible, contentHeight);
            }

            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            public void onTransitionEnd() {
                super.onTransitionEnd();
                ChatAttachAlert.this.updateLayout(ChatAttachAlert.this.currentAttachLayout, false, 0);
                ChatAttachAlert.this.previousScrollOffsetY = ChatAttachAlert.this.scrollOffsetY[0];
                ChatAttachAlert.this.currentAttachLayout.onPanTransitionEnd();
            }

            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
                ChatAttachAlert.this.currentPanTranslationY = y;
                if (ChatAttachAlert.this.fromScrollY > 0.0f) {
                    ChatAttachAlert.this.currentPanTranslationY += (ChatAttachAlert.this.fromScrollY - ChatAttachAlert.this.toScrollY) * (1.0f - progress);
                }
                ChatAttachAlert.this.actionBar.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                ChatAttachAlert.this.selectedMenuItem.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                ChatAttachAlert.this.searchItem.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                ChatAttachAlert.this.doneItem.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                ChatAttachAlert.this.actionBarShadow.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                ChatAttachAlert.this.updateSelectedPosition(0);
                ChatAttachAlert.this.setCurrentPanTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                AnonymousClass3.this.invalidate();
                ChatAttachAlert.this.frameLayout2.invalidate();
                if (ChatAttachAlert.this.currentAttachLayout != null) {
                    ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                }
            }

            @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
            protected boolean heightAnimationEnabled() {
                if (ChatAttachAlert.this.isDismissed() || !ChatAttachAlert.this.openTransitionFinished) {
                    return false;
                }
                return !ChatAttachAlert.this.commentTextView.isPopupVisible();
            }
        };

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(Context context, boolean z) {
            super(context);
            ChatAttachAlert.this = this$0;
            this.val$forceDarkTheme = z;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ChatAttachAlert.this.currentAttachLayout.onContainerViewTouchEvent(ev)) {
                return true;
            }
            if (ev.getAction() == 0 && ChatAttachAlert.this.scrollOffsetY[0] != 0 && ev.getY() < getCurrentTop() && ChatAttachAlert.this.actionBar.getAlpha() == 0.0f) {
                ChatAttachAlert.this.dismiss();
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (ChatAttachAlert.this.currentAttachLayout.onContainerViewTouchEvent(event)) {
                return true;
            }
            return !ChatAttachAlert.this.isDismissed() && super.onTouchEvent(event);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int totalHeight;
            if (getLayoutParams().height > 0) {
                totalHeight = getLayoutParams().height;
            } else {
                totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            }
            if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                this.ignoreLayout = true;
                setPadding(ChatAttachAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ChatAttachAlert.this.backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int paddingTop = totalHeight - getPaddingTop();
            int availableWidth = View.MeasureSpec.getSize(widthMeasureSpec) - (ChatAttachAlert.this.backgroundPaddingLeft * 2);
            if (AndroidUtilities.isTablet()) {
                ChatAttachAlert.this.selectedMenuItem.setAdditionalYOffset(-AndroidUtilities.dp(3.0f));
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                ChatAttachAlert.this.selectedMenuItem.setAdditionalYOffset(0);
            } else {
                ChatAttachAlert.this.selectedMenuItem.setAdditionalYOffset(-AndroidUtilities.dp(3.0f));
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ChatAttachAlert.this.actionBarShadow.getLayoutParams();
            layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) ChatAttachAlert.this.doneItem.getLayoutParams();
            layoutParams2.height = ActionBar.getCurrentActionBarHeight();
            this.ignoreLayout = true;
            int newSize = (int) (availableWidth / Math.min(4.5f, ChatAttachAlert.this.buttonsAdapter.getItemCount()));
            if (ChatAttachAlert.this.attachItemSize != newSize) {
                ChatAttachAlert.this.attachItemSize = newSize;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlert.AnonymousClass3.this.m2400lambda$onMeasure$0$orgtelegramuiComponentsChatAttachAlert$3();
                    }
                });
            }
            this.ignoreLayout = false;
            onMeasureInternal(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-Components-ChatAttachAlert$3 */
        public /* synthetic */ void m2400lambda$onMeasure$0$orgtelegramuiComponentsChatAttachAlert$3() {
            ChatAttachAlert.this.buttonsAdapter.notifyDataSetChanged();
        }

        private void onMeasureInternal(int widthMeasureSpec, int heightMeasureSpec) {
            int paddingBottom;
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            int widthSize2 = widthSize - (ChatAttachAlert.this.backgroundPaddingLeft * 2);
            int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
            if (!ChatAttachAlert.this.commentTextView.isWaitingForKeyboardOpen() && keyboardSize <= AndroidUtilities.dp(20.0f) && !ChatAttachAlert.this.commentTextView.isPopupShowing() && !ChatAttachAlert.this.commentTextView.isAnimatePopupClosing()) {
                this.ignoreLayout = true;
                ChatAttachAlert.this.commentTextView.hideEmojiView();
                this.ignoreLayout = false;
            }
            if (keyboardSize <= AndroidUtilities.dp(20.0f)) {
                if (SharedConfig.smoothKeyboard && ChatAttachAlert.this.keyboardVisible) {
                    paddingBottom = 0;
                } else {
                    paddingBottom = ChatAttachAlert.this.commentTextView.getEmojiPadding();
                }
                if (!AndroidUtilities.isInMultiwindow) {
                    heightSize -= paddingBottom;
                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize, C.BUFFER_FLAG_ENCRYPTED);
                }
                this.ignoreLayout = true;
                ChatAttachAlert.this.currentAttachLayout.onPreMeasure(widthSize2, heightSize);
                if (ChatAttachAlert.this.nextAttachLayout != null) {
                    ChatAttachAlert.this.nextAttachLayout.onPreMeasure(widthSize2, heightSize);
                }
                this.ignoreLayout = false;
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() != 8) {
                    if (ChatAttachAlert.this.commentTextView != null && ChatAttachAlert.this.commentTextView.isPopupView(child)) {
                        if (ChatAttachAlert.this.inBubbleMode) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getPaddingTop() + heightSize, C.BUFFER_FLAG_ENCRYPTED));
                        } else if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                            if (AndroidUtilities.isTablet()) {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), C.BUFFER_FLAG_ENCRYPTED));
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), C.BUFFER_FLAG_ENCRYPTED));
                            }
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int paddingBottom;
            int childLeft;
            int childTop;
            int i = l;
            int i2 = t;
            int i3 = r;
            if (this.lastNotifyWidth != i3 - i) {
                this.lastNotifyWidth = i3 - i;
                if (ChatAttachAlert.this.sendPopupWindow != null && ChatAttachAlert.this.sendPopupWindow.isShowing()) {
                    ChatAttachAlert.this.sendPopupWindow.dismiss();
                }
            }
            int count = getChildCount();
            if (Build.VERSION.SDK_INT >= 29) {
                ChatAttachAlert.this.exclustionRect.set(i, i2, i3, b);
                setSystemGestureExclusionRects(ChatAttachAlert.this.exclusionRects);
            }
            int keyboardSize = measureKeyboardHeight();
            int paddingBottom2 = getPaddingBottom();
            if (SharedConfig.smoothKeyboard && ChatAttachAlert.this.keyboardVisible) {
                paddingBottom = paddingBottom2 + 0;
            } else {
                paddingBottom = paddingBottom2 + ((keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChatAttachAlert.this.commentTextView.getEmojiPadding());
            }
            setBottomClip(paddingBottom);
            int i4 = 0;
            while (i4 < count) {
                View child = getChildAt(i4);
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
                            childLeft = ((((i3 - i) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            childLeft = ((((i3 - i) - width) - lp.rightMargin) - getPaddingRight()) - ChatAttachAlert.this.backgroundPaddingLeft;
                            break;
                        default:
                            childLeft = lp.leftMargin + getPaddingLeft();
                            break;
                    }
                    switch (verticalGravity) {
                        case 16:
                            childTop = (((((b - paddingBottom) - i2) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case 48:
                            childTop = getPaddingTop() + lp.topMargin;
                            break;
                        case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                            childTop = (((b - paddingBottom) - i2) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    if (ChatAttachAlert.this.commentTextView != null && ChatAttachAlert.this.commentTextView.isPopupView(child)) {
                        if (AndroidUtilities.isTablet()) {
                            childTop = getMeasuredHeight() - child.getMeasuredHeight();
                        } else {
                            childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                        }
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
                i4++;
                i = l;
                i2 = t;
                i3 = r;
            }
            notifyHeightChanged();
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, false, 0);
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
            chatAttachAlert2.updateLayout(chatAttachAlert2.nextAttachLayout, false, 0);
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        private void drawChildBackground(Canvas canvas, View child) {
            int backgroundColor;
            float alphaProgress;
            int color;
            float toMove;
            if (child instanceof AttachAlertLayout) {
                canvas.save();
                canvas.translate(0.0f, ChatAttachAlert.this.currentPanTranslationY);
                int viewAlpha = (int) (child.getAlpha() * 255.0f);
                AttachAlertLayout layout = (AttachAlertLayout) child;
                int actionBarType = layout.needsActionBar();
                int offset = AndroidUtilities.dp(13.0f) + (ChatAttachAlert.this.headerView != null ? AndroidUtilities.dp(ChatAttachAlert.this.headerView.getAlpha() * 26.0f) : 0);
                int top = (ChatAttachAlert.this.getScrollOffsetY(0) - ChatAttachAlert.this.backgroundPaddingTop) - offset;
                if (ChatAttachAlert.this.currentSheetAnimationType == 1 || ChatAttachAlert.this.viewChangeAnimator != null) {
                    top = (int) (top + child.getTranslationY());
                }
                int y = AndroidUtilities.dp(20.0f) + top;
                int height = getMeasuredHeight() + AndroidUtilities.dp(45.0f) + ChatAttachAlert.this.backgroundPaddingTop;
                float rad = 1.0f;
                int h = actionBarType != 0 ? ActionBar.getCurrentActionBarHeight() : ChatAttachAlert.this.backgroundPaddingTop;
                if (actionBarType != 2) {
                    if (ChatAttachAlert.this.backgroundPaddingTop + top < h) {
                        float toMove2 = offset;
                        if (layout != ChatAttachAlert.this.locationLayout) {
                            if (layout == ChatAttachAlert.this.pollLayout) {
                                toMove = toMove2 - AndroidUtilities.dp(3.0f);
                            } else {
                                toMove = toMove2 + AndroidUtilities.dp(4.0f);
                            }
                        } else {
                            toMove = toMove2 + AndroidUtilities.dp(11.0f);
                        }
                        float moveProgress = Math.min(1.0f, ((h - top) - ChatAttachAlert.this.backgroundPaddingTop) / toMove);
                        float availableToMove = h - toMove;
                        int diff = (int) (availableToMove * moveProgress);
                        top -= diff;
                        y -= diff;
                        height += diff;
                        rad = 1.0f - moveProgress;
                    }
                } else if (top < h) {
                    rad = Math.max(0.0f, 1.0f - ((h - top) / ChatAttachAlert.this.backgroundPaddingTop));
                }
                if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                    top += AndroidUtilities.statusBarHeight;
                    y += AndroidUtilities.statusBarHeight;
                    height -= AndroidUtilities.statusBarHeight;
                }
                if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                    backgroundColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
                } else {
                    backgroundColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
                }
                ChatAttachAlert.this.shadowDrawable.setAlpha(viewAlpha);
                ChatAttachAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                ChatAttachAlert.this.shadowDrawable.draw(canvas);
                if (actionBarType == 2) {
                    Theme.dialogs_onlineCirclePaint.setColor(backgroundColor);
                    Theme.dialogs_onlineCirclePaint.setAlpha(viewAlpha);
                    this.rect.set(ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.save();
                    canvas.clipRect(this.rect.left, this.rect.top, this.rect.right, this.rect.top + (this.rect.height() / 2.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                    canvas.restore();
                }
                if (rad != 1.0f && actionBarType != 2) {
                    Theme.dialogs_onlineCirclePaint.setColor(backgroundColor);
                    Theme.dialogs_onlineCirclePaint.setAlpha(viewAlpha);
                    this.rect.set(ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.save();
                    canvas.clipRect(this.rect.left, this.rect.top, this.rect.right, this.rect.top + (this.rect.height() / 2.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                    canvas.restore();
                }
                if ((ChatAttachAlert.this.headerView == null || ChatAttachAlert.this.headerView.getAlpha() != 1.0f) && rad != 0.0f) {
                    int w = AndroidUtilities.dp(36.0f);
                    this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, y + AndroidUtilities.dp(4.0f));
                    if (actionBarType != 2) {
                        color = ChatAttachAlert.this.getThemedColor(Theme.key_sheet_scrollUp);
                        alphaProgress = ChatAttachAlert.this.headerView == null ? 1.0f : 1.0f - ChatAttachAlert.this.headerView.getAlpha();
                    } else {
                        color = 536870912;
                        alphaProgress = rad;
                    }
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * alphaProgress * rad * child.getAlpha()));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                canvas.restore();
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            int backgroundColor;
            float alphaProgress;
            int color;
            int finalMove;
            float toMove;
            if ((child instanceof AttachAlertLayout) && child.getAlpha() > 0.0f) {
                canvas.save();
                canvas.translate(0.0f, ChatAttachAlert.this.currentPanTranslationY);
                int viewAlpha = (int) (child.getAlpha() * 255.0f);
                AttachAlertLayout layout = (AttachAlertLayout) child;
                int actionBarType = layout.needsActionBar();
                int offset = AndroidUtilities.dp(13.0f) + (ChatAttachAlert.this.headerView != null ? AndroidUtilities.dp(ChatAttachAlert.this.headerView.getAlpha() * 26.0f) : 0);
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                int top = (chatAttachAlert.getScrollOffsetY(layout == chatAttachAlert.currentAttachLayout ? 0 : 1) - ChatAttachAlert.this.backgroundPaddingTop) - offset;
                if (ChatAttachAlert.this.currentSheetAnimationType == 1 || ChatAttachAlert.this.viewChangeAnimator != null) {
                    top = (int) (top + child.getTranslationY());
                }
                int y = AndroidUtilities.dp(20.0f) + top;
                int height = getMeasuredHeight() + AndroidUtilities.dp(45.0f) + ChatAttachAlert.this.backgroundPaddingTop;
                float rad = 1.0f;
                int h = actionBarType != 0 ? ActionBar.getCurrentActionBarHeight() : ChatAttachAlert.this.backgroundPaddingTop;
                if (actionBarType != 2) {
                    if (ChatAttachAlert.this.backgroundPaddingTop + top < h) {
                        float toMove2 = offset;
                        if (layout != ChatAttachAlert.this.locationLayout) {
                            if (layout == ChatAttachAlert.this.pollLayout) {
                                toMove = toMove2 - AndroidUtilities.dp(3.0f);
                            } else {
                                toMove = toMove2 + AndroidUtilities.dp(4.0f);
                            }
                        } else {
                            toMove = toMove2 + AndroidUtilities.dp(11.0f);
                        }
                        float moveProgress = Math.min(1.0f, ((h - top) - ChatAttachAlert.this.backgroundPaddingTop) / toMove);
                        float availableToMove = h - toMove;
                        int diff = (int) (availableToMove * moveProgress);
                        top -= diff;
                        y -= diff;
                        height += diff;
                        rad = 1.0f - moveProgress;
                    }
                } else if (top < h) {
                    rad = Math.max(0.0f, 1.0f - ((h - top) / ChatAttachAlert.this.backgroundPaddingTop));
                }
                if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                    top += AndroidUtilities.statusBarHeight;
                    y += AndroidUtilities.statusBarHeight;
                    height -= AndroidUtilities.statusBarHeight;
                }
                if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                    backgroundColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
                } else {
                    backgroundColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
                }
                boolean drawBackground = (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout || ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.photoPreviewLayout || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout && ChatAttachAlert.this.nextAttachLayout == null)) ? false : true;
                if (drawBackground) {
                    ChatAttachAlert.this.shadowDrawable.setAlpha(viewAlpha);
                    ChatAttachAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                    ChatAttachAlert.this.shadowDrawable.draw(canvas);
                    if (actionBarType == 2) {
                        Theme.dialogs_onlineCirclePaint.setColor(backgroundColor);
                        Theme.dialogs_onlineCirclePaint.setAlpha(viewAlpha);
                        this.rect.set(ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                        canvas.save();
                        canvas.clipRect(this.rect.left, this.rect.top, this.rect.right, this.rect.top + (this.rect.height() / 2.0f));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                        canvas.restore();
                    }
                }
                boolean clip = !drawBackground && ChatAttachAlert.this.headerView != null && ChatAttachAlert.this.headerView.getAlpha() > 0.9f && ((ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) && (ChatAttachAlert.this.viewChangeAnimator instanceof SpringAnimation) && ((SpringAnimation) ChatAttachAlert.this.viewChangeAnimator).isRunning();
                if (clip) {
                    canvas.save();
                    if (AndroidUtilities.isTablet()) {
                        finalMove = 16;
                    } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        finalMove = 6;
                    } else {
                        finalMove = 12;
                    }
                    int clipTop = (int) (ChatAttachAlert.this.baseSelectedTextViewTranslationY + AndroidUtilities.statusBarHeight + ChatAttachAlert.this.headerView.getHeight() + AndroidUtilities.dp(finalMove * ChatAttachAlert.this.headerView.getAlpha()));
                    canvas.clipRect(ChatAttachAlert.this.backgroundPaddingLeft, clipTop, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, getMeasuredHeight());
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (clip) {
                    canvas.restore();
                }
                if (drawBackground) {
                    if (rad != 1.0f && actionBarType != 2) {
                        Theme.dialogs_onlineCirclePaint.setColor(backgroundColor);
                        Theme.dialogs_onlineCirclePaint.setAlpha(viewAlpha);
                        this.rect.set(ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                        canvas.save();
                        canvas.clipRect(this.rect.left, this.rect.top, this.rect.right, this.rect.top + (this.rect.height() / 2.0f));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                        canvas.restore();
                    }
                    if ((ChatAttachAlert.this.headerView == null || ChatAttachAlert.this.headerView.getAlpha() != 1.0f) && rad != 0.0f) {
                        int w = AndroidUtilities.dp(36.0f);
                        this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, y + AndroidUtilities.dp(4.0f));
                        if (actionBarType != 2) {
                            color = ChatAttachAlert.this.getThemedColor(Theme.key_sheet_scrollUp);
                            alphaProgress = ChatAttachAlert.this.headerView == null ? 1.0f : 1.0f - ChatAttachAlert.this.headerView.getAlpha();
                        } else {
                            color = 536870912;
                            alphaProgress = rad;
                        }
                        int alpha = Color.alpha(color);
                        Theme.dialogs_onlineCirclePaint.setColor(color);
                        Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * alphaProgress * rad * child.getAlpha()));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    }
                }
                canvas.restore();
                return result;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int color1;
            if (!ChatAttachAlert.this.inBubbleMode) {
                if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                    color1 = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
                } else {
                    color1 = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
                }
                int finalColor = Color.argb((int) (ChatAttachAlert.this.actionBar.getAlpha() * 255.0f), Color.red(color1), Color.green(color1), Color.blue(color1));
                Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                canvas.drawRect(ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.currentPanTranslationY, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight + ChatAttachAlert.this.currentPanTranslationY, Theme.dialogs_onlineCirclePaint);
            }
        }

        private int getCurrentTop() {
            int i = 0;
            int i2 = ChatAttachAlert.this.scrollOffsetY[0] - (ChatAttachAlert.this.backgroundPaddingTop * 2);
            int dp = AndroidUtilities.dp(13.0f);
            if (ChatAttachAlert.this.headerView != null) {
                i = AndroidUtilities.dp(ChatAttachAlert.this.headerView.getAlpha() * 26.0f);
            }
            int y = (i2 - (dp + i)) + AndroidUtilities.dp(20.0f);
            if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                return y + AndroidUtilities.statusBarHeight;
            }
            return y;
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, getPaddingTop() + ChatAttachAlert.this.currentPanTranslationY, getMeasuredWidth(), (getMeasuredHeight() + ChatAttachAlert.this.currentPanTranslationY) - getPaddingBottom());
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout || ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.photoPreviewLayout || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout && ChatAttachAlert.this.nextAttachLayout == null)) {
                drawChildBackground(canvas, ChatAttachAlert.this.currentAttachLayout);
            }
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override // android.view.View
        public void setTranslationY(float translationY) {
            float translationY2 = translationY + ChatAttachAlert.this.currentPanTranslationY;
            if (ChatAttachAlert.this.currentSheetAnimationType == 0) {
                this.initialTranslationY = translationY2;
            }
            if (ChatAttachAlert.this.currentSheetAnimationType == 1) {
                if (translationY2 < 0.0f) {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationY(translationY2);
                    if (ChatAttachAlert.this.avatarPicker != 0) {
                        ChatAttachAlert.this.headerView.setTranslationY((ChatAttachAlert.this.baseSelectedTextViewTranslationY + translationY2) - ChatAttachAlert.this.currentPanTranslationY);
                    }
                    translationY2 = 0.0f;
                    ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(0.0f);
                } else {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationY(0.0f);
                    ChatAttachAlert.this.buttonsRecyclerView.setTranslationY((-translationY2) + (ChatAttachAlert.this.buttonsRecyclerView.getMeasuredHeight() * (translationY2 / this.initialTranslationY)));
                }
                ChatAttachAlert.this.containerView.invalidate();
            }
            super.setTranslationY(translationY2 - ChatAttachAlert.this.currentPanTranslationY);
            if (ChatAttachAlert.this.currentSheetAnimationType != 1) {
                ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.adjustPanLayoutHelper.setResizableView(this);
            this.adjustPanLayoutHelper.onAttach();
            ChatAttachAlert.this.commentTextView.setAdjustPanLayoutHelper(this.adjustPanLayoutHelper);
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.adjustPanLayoutHelper.onDetach();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2366lambda$new$0$orgtelegramuiComponentsChatAttachAlert(int id) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(id);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2367lambda$new$1$orgtelegramuiComponentsChatAttachAlert(View v) {
        this.selectedMenuItem.toggleSubMenu();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2374lambda$new$2$orgtelegramuiComponentsChatAttachAlert(View v) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2375lambda$new$3$orgtelegramuiComponentsChatAttachAlert(boolean showingFromDialog, View v) {
        if (this.avatarPicker != 0) {
            this.delegate.openAvatarsSearch();
            dismiss();
            return;
        }
        final HashMap<Object, Object> photos = new HashMap<>();
        final ArrayList<Object> order = new ArrayList<>();
        PhotoPickerSearchActivity fragment = new PhotoPickerSearchActivity(photos, order, 0, true, (ChatActivity) this.baseFragment);
        fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.7
            private boolean sendPressed;

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            {
                ChatAttachAlert.this = this;
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void selectedPhotosChanged() {
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                if (canceled || photos.isEmpty() || this.sendPressed) {
                    return;
                }
                this.sendPressed = true;
                ArrayList media = new ArrayList();
                for (int a = 0; a < order.size(); a++) {
                    Object object = photos.get(order.get(a));
                    SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                    media.add(info);
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                    if (searchImage.imagePath != null) {
                        info.path = searchImage.imagePath;
                    } else {
                        info.searchImage = searchImage;
                    }
                    info.thumbPath = searchImage.thumbPath;
                    info.videoEditedInfo = searchImage.editedInfo;
                    info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                    info.entities = searchImage.entities;
                    info.masks = searchImage.stickers;
                    info.ttl = searchImage.ttl;
                    if (searchImage.inlineResult != null && searchImage.type == 1) {
                        info.inlineResult = searchImage.inlineResult;
                        info.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
                ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectSearchPhotos(media, notify, scheduleDate);
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void onCaptionChanged(CharSequence text) {
            }
        });
        fragment.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        if (showingFromDialog) {
            this.baseFragment.showAsSheet(fragment);
        } else {
            this.baseFragment.presentFragment(fragment);
        }
        dismiss();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2376lambda$new$4$orgtelegramuiComponentsChatAttachAlert(View e) {
        updatePhotoPreview(this.currentAttachLayout != this.photoPreviewLayout);
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2379lambda$new$7$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        if (this.baseFragment.getParentActivity() == null) {
            return;
        }
        if (view instanceof AttachButton) {
            int num = ((Integer) view.getTag()).intValue();
            if (num == 1) {
                showLayout(this.photoLayout);
            } else if (num == 3) {
                if (Build.VERSION.SDK_INT >= 23 && this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                    return;
                }
                openAudioLayout(true);
            } else if (num == 4) {
                if (Build.VERSION.SDK_INT >= 23 && this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                    return;
                }
                openDocumentsLayout(true);
            } else if (num == 5) {
                if (Build.VERSION.SDK_INT >= 23 && this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 5);
                    return;
                }
                openContactsLayout();
            } else if (num == 6) {
                if (!AndroidUtilities.isGoogleMapsInstalled(this.baseFragment)) {
                    return;
                }
                if (this.locationLayout == null) {
                    AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                    ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = new ChatAttachAlertLocationLayout(this, getContext(), resourcesProvider);
                    this.locationLayout = chatAttachAlertLocationLayout;
                    attachAlertLayoutArr[5] = chatAttachAlertLocationLayout;
                    chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlertLocationLayout.LocationActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda26
                        @Override // org.telegram.ui.Components.ChatAttachAlertLocationLayout.LocationActivityDelegate
                        public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                            ChatAttachAlert.this.m2377lambda$new$5$orgtelegramuiComponentsChatAttachAlert(messageMedia, i, z, i2);
                        }
                    });
                }
                showLayout(this.locationLayout);
            } else if (num == 9) {
                if (this.pollLayout == null) {
                    AttachAlertLayout[] attachAlertLayoutArr2 = this.layouts;
                    ChatAttachAlertPollLayout chatAttachAlertPollLayout = new ChatAttachAlertPollLayout(this, getContext(), resourcesProvider);
                    this.pollLayout = chatAttachAlertPollLayout;
                    attachAlertLayoutArr2[1] = chatAttachAlertPollLayout;
                    chatAttachAlertPollLayout.setDelegate(new ChatAttachAlertPollLayout.PollCreateActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda27
                        @Override // org.telegram.ui.Components.ChatAttachAlertPollLayout.PollCreateActivityDelegate
                        public final void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
                            ChatAttachAlert.this.m2378lambda$new$6$orgtelegramuiComponentsChatAttachAlert(tL_messageMediaPoll, hashMap, z, i);
                        }
                    });
                }
                showLayout(this.pollLayout);
            } else {
                this.delegate.didPressedButton(((Integer) view.getTag()).intValue(), true, true, 0, false);
            }
            int left = view.getLeft();
            int right = view.getRight();
            int extra = AndroidUtilities.dp(10.0f);
            if (left - extra < 0) {
                this.buttonsRecyclerView.smoothScrollBy(left - extra, 0);
            } else if (right + extra > this.buttonsRecyclerView.getMeasuredWidth()) {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                recyclerListView.smoothScrollBy((right + extra) - recyclerListView.getMeasuredWidth(), 0);
            }
        } else if (view instanceof AttachBotButton) {
            AttachBotButton button = (AttachBotButton) view;
            if (button.attachMenuBot != null) {
                showBotLayout(button.attachMenuBot.bot_id);
            } else {
                this.delegate.didSelectBot(button.currentUser);
                dismiss();
            }
        }
        if (view.getX() + view.getWidth() >= this.buttonsRecyclerView.getMeasuredWidth() - AndroidUtilities.dp(32.0f)) {
            this.buttonsRecyclerView.smoothScrollBy((int) (view.getWidth() * 1.5f), 0);
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2377lambda$new$5$orgtelegramuiComponentsChatAttachAlert(TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).didSelectLocation(location, live, notify, scheduleDate);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2378lambda$new$6$orgtelegramuiComponentsChatAttachAlert(TLRPC.TL_messageMediaPoll poll, HashMap params, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendPoll(poll, params, notify, scheduleDate);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ boolean m2380lambda$new$8$orgtelegramuiComponentsChatAttachAlert(View view, int position) {
        if (view instanceof AttachBotButton) {
            AttachBotButton button = (AttachBotButton) view;
            if (this.baseFragment == null || button.currentUser == null) {
                return false;
            }
            onLongClickBotButton(button.attachMenuBot, button.currentUser);
            return true;
        }
        return false;
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2381lambda$new$9$orgtelegramuiComponentsChatAttachAlert(View v) {
        ChatAttachAlertBotWebViewLayout webViewLayout;
        long j = this.selectedId;
        if (j < 0 && (webViewLayout = this.botAttachLayouts.get(-j)) != null) {
            webViewLayout.getWebViewContainer().onMainButtonPressed();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$10 */
    /* loaded from: classes5.dex */
    public class AnonymousClass10 extends FrameLayout {
        private int color;
        private final Paint p = new Paint();
        final /* synthetic */ boolean val$forceDarkTheme;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass10(Context arg0, boolean z) {
            super(arg0);
            ChatAttachAlert.this = this$0;
            this.val$forceDarkTheme = z;
        }

        @Override // android.view.View
        public void setAlpha(float alpha) {
            super.setAlpha(alpha);
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int newColor;
            if (ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop != 0.0f && ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop != ChatAttachAlert.this.frameLayout2.getTop() + ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop) {
                if (ChatAttachAlert.this.topBackgroundAnimator != null) {
                    ChatAttachAlert.this.topBackgroundAnimator.cancel();
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.captionEditTextTopOffset = chatAttachAlert.chatActivityEnterViewAnimateFromTop - (ChatAttachAlert.this.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset);
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                chatAttachAlert2.topBackgroundAnimator = ValueAnimator.ofFloat(chatAttachAlert2.captionEditTextTopOffset, 0.0f);
                ChatAttachAlert.this.topBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$10$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatAttachAlert.AnonymousClass10.this.m2397lambda$onDraw$0$orgtelegramuiComponentsChatAttachAlert$10(valueAnimator);
                    }
                });
                ChatAttachAlert.this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ChatAttachAlert.this.topBackgroundAnimator.setDuration(200L);
                ChatAttachAlert.this.topBackgroundAnimator.start();
                ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop = 0.0f;
            }
            float alphaOffset = (ChatAttachAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f)) * (1.0f - getAlpha());
            ChatAttachAlert.this.shadow.setTranslationY((-(ChatAttachAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f))) + ChatAttachAlert.this.captionEditTextTopOffset + ChatAttachAlert.this.currentPanTranslationY + ChatAttachAlert.this.bottomPannelTranslation + alphaOffset + ChatAttachAlert.this.botMainButtonOffsetY);
            if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                newColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
            } else {
                newColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
            }
            if (this.color != newColor) {
                this.color = newColor;
                this.p.setColor(newColor);
            }
            canvas.drawRect(0.0f, ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight(), this.p);
        }

        /* renamed from: lambda$onDraw$0$org-telegram-ui-Components-ChatAttachAlert$10 */
        public /* synthetic */ void m2397lambda$onDraw$0$orgtelegramuiComponentsChatAttachAlert$10(ValueAnimator valueAnimator) {
            ChatAttachAlert.this.captionEditTextTopOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.frameLayout2.invalidate();
            invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }

    public static /* synthetic */ boolean lambda$new$10(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$11 */
    /* loaded from: classes5.dex */
    public class AnonymousClass11 extends EditTextEmoji {
        private ValueAnimator messageEditTextAnimator;
        private int messageEditTextPredrawHeigth;
        private int messageEditTextPredrawScrollY;
        private boolean shouldAnimateEditTextWithBounds;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass11(Context context, SizeNotifierFrameLayout parent, BaseFragment fragment, int style, Theme.ResourcesProvider resourcesProvider) {
            super(context, parent, fragment, style, resourcesProvider);
            ChatAttachAlert.this = this$0;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!ChatAttachAlert.this.enterCommentEventSent) {
                if (ev.getX() > ChatAttachAlert.this.commentTextView.getEditText().getLeft() && ev.getX() < ChatAttachAlert.this.commentTextView.getEditText().getRight() && ev.getY() > ChatAttachAlert.this.commentTextView.getEditText().getTop() && ev.getY() < ChatAttachAlert.this.commentTextView.getEditText().getBottom()) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.makeFocusable(chatAttachAlert.commentTextView.getEditText(), true);
                } else {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    chatAttachAlert2.makeFocusable(chatAttachAlert2.commentTextView.getEditText(), false);
                }
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.shouldAnimateEditTextWithBounds) {
                final EditTextCaption editText = ChatAttachAlert.this.commentTextView.getEditText();
                float dy = (this.messageEditTextPredrawHeigth - editText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - editText.getScrollY());
                editText.setOffsetY(editText.getOffsetY() - dy);
                ValueAnimator a = ValueAnimator.ofFloat(editText.getOffsetY(), 0.0f);
                a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$11$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EditTextCaption.this.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                });
                ValueAnimator valueAnimator = this.messageEditTextAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.messageEditTextAnimator = a;
                a.setDuration(200L);
                a.setInterpolator(CubicBezierInterpolator.DEFAULT);
                a.start();
                this.shouldAnimateEditTextWithBounds = false;
            }
            super.dispatchDraw(canvas);
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        protected void onLineCountChanged(int oldLineCount, int newLineCount) {
            if (!TextUtils.isEmpty(getEditText().getText())) {
                this.shouldAnimateEditTextWithBounds = true;
                this.messageEditTextPredrawHeigth = getEditText().getMeasuredHeight();
                this.messageEditTextPredrawScrollY = getEditText().getScrollY();
                invalidate();
            } else {
                getEditText().animate().cancel();
                getEditText().setOffsetY(0.0f);
                this.shouldAnimateEditTextWithBounds = false;
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.chatActivityEnterViewAnimateFromTop = chatAttachAlert.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset;
            ChatAttachAlert.this.frameLayout2.invalidate();
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        protected void bottomPanelTranslationY(float translation) {
            ChatAttachAlert.this.bottomPannelTranslation = translation;
            ChatAttachAlert.this.frameLayout2.setTranslationY(translation);
            ChatAttachAlert.this.writeButtonContainer.setTranslationY(translation);
            ChatAttachAlert.this.selectedCountView.setTranslationY(translation);
            ChatAttachAlert.this.frameLayout2.invalidate();
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, true, 0);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$12 */
    /* loaded from: classes5.dex */
    public class AnonymousClass12 implements TextWatcher {
        private boolean processChange;

        AnonymousClass12() {
            ChatAttachAlert.this = this$0;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (count - before >= 1) {
                this.processChange = true;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:30:0x0134  */
        /* JADX WARN: Removed duplicated region for block: B:43:? A[RETURN, SYNTHETIC] */
        @Override // android.text.TextWatcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void afterTextChanged(android.text.Editable r12) {
            /*
                Method dump skipped, instructions count: 391
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass12.afterTextChanged(android.text.Editable):void");
        }

        /* renamed from: lambda$afterTextChanged$0$org-telegram-ui-Components-ChatAttachAlert$12 */
        public /* synthetic */ void m2398x162979f7(ValueAnimator valueAnimator) {
            ChatAttachAlert.this.sendButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            int color = ChatAttachAlert.this.getThemedColor(Theme.key_dialogFloatingIcon);
            int defaultAlpha = Color.alpha(color);
            ChatAttachAlert.this.writeButton.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(color, (int) (defaultAlpha * ((ChatAttachAlert.this.sendButtonEnabledProgress * 0.42f) + 0.58f))), PorterDuff.Mode.MULTIPLY));
            ChatAttachAlert.this.selectedCountView.invalidate();
        }
    }

    /* renamed from: lambda$new$12$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2369lambda$new$12$orgtelegramuiComponentsChatAttachAlert(Theme.ResourcesProvider resourcesProvider, View v) {
        if (this.currentLimit - this.codepointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator vibrator = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200L);
                return;
            }
            return;
        }
        if (this.editingMessageObject == null) {
            BaseFragment baseFragment = this.baseFragment;
            if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda22
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlert.this.m2368lambda$new$11$orgtelegramuiComponentsChatAttachAlert(z, i);
                    }
                }, resourcesProvider);
                return;
            }
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(true, 0);
            return;
        }
        attachAlertLayout.sendSelectedItems(true, 0);
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2368lambda$new$11$orgtelegramuiComponentsChatAttachAlert(boolean notify, int scheduleDate) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(notify, scheduleDate);
            return;
        }
        attachAlertLayout.sendSelectedItems(notify, scheduleDate);
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0083  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0092  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00bd  */
    /* renamed from: lambda$new$16$org-telegram-ui-Components-ChatAttachAlert */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ boolean m2373lambda$new$16$orgtelegramuiComponentsChatAttachAlert(final org.telegram.ui.ActionBar.Theme.ResourcesProvider r17, android.view.View r18) {
        /*
            Method dump skipped, instructions count: 412
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.m2373lambda$new$16$orgtelegramuiComponentsChatAttachAlert(org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.view.View):boolean");
    }

    /* renamed from: lambda$new$13$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2370lambda$new$13$orgtelegramuiComponentsChatAttachAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$new$15$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2372lambda$new$15$orgtelegramuiComponentsChatAttachAlert(int num, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda23
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i) {
                    ChatAttachAlert.this.m2371lambda$new$14$orgtelegramuiComponentsChatAttachAlert(z, i);
                }
            }, resourcesProvider);
        } else if (num == 1) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
                sendPressed(false, 0);
                return;
            }
            attachAlertLayout.sendSelectedItems(false, 0);
            dismiss();
        }
    }

    /* renamed from: lambda$new$14$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2371lambda$new$14$orgtelegramuiComponentsChatAttachAlert(boolean notify, int scheduleDate) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(notify, scheduleDate);
            return;
        }
        attachAlertLayout.sendSelectedItems(notify, scheduleDate);
        dismiss();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }

    private boolean isLightStatusBar() {
        int color = getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }

    public void onLongClickBotButton(final TLRPC.TL_attachMenuBot attachMenuBot, final TLRPC.User currentUser) {
        String botName = attachMenuBot != null ? attachMenuBot.short_name : UserObject.getUserName(currentUser);
        new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString((int) R.string.BotRemoveFromMenuTitle)).setMessage(AndroidUtilities.replaceTags(attachMenuBot != null ? LocaleController.formatString("BotRemoveFromMenu", R.string.BotRemoveFromMenu, botName) : LocaleController.formatString("BotRemoveInlineFromMenu", R.string.BotRemoveInlineFromMenu, botName))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda32
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatAttachAlert.this.m2388xdf2da15f(attachMenuBot, currentUser, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
    }

    /* renamed from: lambda$onLongClickBotButton$19$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2388xdf2da15f(final TLRPC.TL_attachMenuBot attachMenuBot, TLRPC.User currentUser, DialogInterface dialogInterface, int i) {
        if (attachMenuBot != null) {
            TLRPC.TL_messages_toggleBotInAttachMenu req = new TLRPC.TL_messages_toggleBotInAttachMenu();
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(currentUser);
            req.enabled = false;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda18
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatAttachAlert.this.m2387x1c413800(attachMenuBot, tLObject, tL_error);
                }
            }, 66);
            return;
        }
        MediaDataController.getInstance(this.currentAccount).removeInline(currentUser.id);
    }

    /* renamed from: lambda$onLongClickBotButton$18$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2387x1c413800(final TLRPC.TL_attachMenuBot attachMenuBot, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlert.this.m2386x5954cea1(attachMenuBot);
            }
        });
    }

    /* renamed from: lambda$onLongClickBotButton$17$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2386x5954cea1(TLRPC.TL_attachMenuBot attachMenuBot) {
        MediaDataController.getInstance(this.currentAccount).loadAttachMenuBots(false, true);
        if (this.currentAttachLayout == this.botAttachLayouts.get(attachMenuBot.bot_id)) {
            showLayout(this.photoLayout);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean shouldOverlayCameraViewOverNavBar() {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        return attachAlertLayout == chatAttachAlertPhotoLayout && chatAttachAlertPhotoLayout.cameraExpanded;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        boolean z = false;
        this.buttonPressed = false;
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            this.calcMandatoryInsets = chatActivity.isKeyboardVisible();
        }
        this.openTransitionFinished = false;
        if (Build.VERSION.SDK_INT >= 30) {
            this.navBarColorKey = null;
            this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundGray), 0);
            AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
            Window window = getWindow();
            if (AndroidUtilities.computePerceivedBrightness(this.navBarColor) > 0.721d) {
                z = true;
            }
            AndroidUtilities.setLightNavigationBar(window, z);
        }
    }

    public void setEditingMessageObject(MessageObject messageObject) {
        if (this.editingMessageObject == messageObject) {
            return;
        }
        this.editingMessageObject = messageObject;
        if (messageObject != null) {
            this.maxSelectedPhotos = 1;
            this.allowOrder = false;
        } else {
            this.maxSelectedPhotos = -1;
            this.allowOrder = true;
        }
        this.buttonsAdapter.notifyDataSetChanged();
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public void applyCaption() {
        if (this.commentTextView.length() <= 0) {
            return;
        }
        this.currentAttachLayout.applyCaption(this.commentTextView.getText());
    }

    private void sendPressed(boolean notify, int scheduleDate) {
        if (this.buttonPressed) {
            return;
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            TLRPC.Chat chat = chatActivity.getCurrentChat();
            TLRPC.User user = chatActivity.getCurrentUser();
            if (user != null || ((ChatObject.isChannel(chat) && chat.megagroup) || !ChatObject.isChannel(chat))) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                edit.putBoolean("silent_" + chatActivity.getDialogId(), !notify).commit();
            }
        }
        applyCaption();
        this.buttonPressed = true;
        this.delegate.didPressedButton(7, true, notify, scheduleDate, false);
    }

    private void showLayout(AttachAlertLayout layout) {
        long newId = this.selectedId;
        if (layout == this.photoLayout) {
            newId = 1;
        } else if (layout == this.audioLayout) {
            newId = 3;
        } else if (layout == this.documentLayout) {
            newId = 4;
        } else if (layout == this.contactsLayout) {
            newId = 5;
        } else if (layout == this.locationLayout) {
            newId = 6;
        } else if (layout == this.pollLayout) {
            newId = 9;
        }
        showLayout(layout, newId);
    }

    private void showLayout(final AttachAlertLayout layout, long newId) {
        if (this.viewChangeAnimator == null && this.commentsAnimator == null) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout != layout) {
                this.botButtonWasVisible = false;
                this.botButtonProgressWasVisible = false;
                this.botMainButtonOffsetY = 0.0f;
                this.botMainButtonTextView.setVisibility(8);
                this.botProgressView.setAlpha(0.0f);
                this.botProgressView.setScaleX(0.1f);
                this.botProgressView.setScaleY(0.1f);
                this.botProgressView.setVisibility(8);
                this.buttonsRecyclerView.setAlpha(1.0f);
                this.buttonsRecyclerView.setTranslationY(this.botMainButtonOffsetY);
                for (int i = 0; i < this.botAttachLayouts.size(); i++) {
                    this.botAttachLayouts.valueAt(i).setMeasureOffsetY(0);
                }
                this.selectedId = newId;
                int count = this.buttonsRecyclerView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.buttonsRecyclerView.getChildAt(a);
                    if (child instanceof AttachButton) {
                        AttachButton attachButton = (AttachButton) child;
                        attachButton.updateCheckedState(true);
                    } else if (child instanceof AttachBotButton) {
                        AttachBotButton attachButton2 = (AttachBotButton) child;
                        attachButton2.updateCheckedState(true);
                    }
                }
                int t = (this.currentAttachLayout.getFirstOffset() - AndroidUtilities.dp(11.0f)) - this.scrollOffsetY[0];
                this.nextAttachLayout = layout;
                if (Build.VERSION.SDK_INT >= 20) {
                    this.container.setLayerType(2, null);
                }
                this.actionBar.setVisibility(this.nextAttachLayout.needsActionBar() != 0 ? 0 : 4);
                this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
                if (this.actionBar.isSearchFieldVisible()) {
                    this.actionBar.closeSearchField();
                }
                this.currentAttachLayout.onHide();
                AttachAlertLayout attachAlertLayout2 = this.nextAttachLayout;
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
                if (attachAlertLayout2 == chatAttachAlertPhotoLayout) {
                    chatAttachAlertPhotoLayout.setCheckCameraWhenShown(true);
                }
                this.nextAttachLayout.onShow(this.currentAttachLayout);
                this.nextAttachLayout.setVisibility(0);
                if (layout.getParent() != null) {
                    this.containerView.removeView(this.nextAttachLayout);
                }
                int index = this.containerView.indexOfChild(this.currentAttachLayout);
                if (this.nextAttachLayout.getParent() != this.containerView) {
                    ViewGroup viewGroup = this.containerView;
                    AttachAlertLayout attachAlertLayout3 = this.nextAttachLayout;
                    viewGroup.addView(attachAlertLayout3, attachAlertLayout3 == this.locationLayout ? index : index + 1, LayoutHelper.createFrame(-1, -1.0f));
                }
                final Runnable onEnd = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlert.this.m2391lambda$showLayout$20$orgtelegramuiComponentsChatAttachAlert();
                    }
                };
                if ((this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                    int width = Math.max(this.nextAttachLayout.getWidth(), this.currentAttachLayout.getWidth());
                    AttachAlertLayout attachAlertLayout4 = this.nextAttachLayout;
                    if (attachAlertLayout4 instanceof ChatAttachAlertPhotoLayoutPreview) {
                        attachAlertLayout4.setTranslationX(width);
                        AttachAlertLayout attachAlertLayout5 = this.currentAttachLayout;
                        if (attachAlertLayout5 instanceof ChatAttachAlertPhotoLayout) {
                            ChatAttachAlertPhotoLayout photoLayout = (ChatAttachAlertPhotoLayout) attachAlertLayout5;
                            if (photoLayout.cameraView != null) {
                                photoLayout.cameraView.setVisibility(4);
                                photoLayout.cameraIcon.setVisibility(4);
                                photoLayout.cameraCell.setVisibility(0);
                            }
                        }
                    } else {
                        this.currentAttachLayout.setTranslationX(-width);
                        AttachAlertLayout attachAlertLayout6 = this.nextAttachLayout;
                        if (attachAlertLayout6 == this.photoLayout) {
                            ChatAttachAlertPhotoLayout photoLayout2 = (ChatAttachAlertPhotoLayout) attachAlertLayout6;
                            if (photoLayout2.cameraView != null) {
                                photoLayout2.cameraView.setVisibility(0);
                                photoLayout2.cameraIcon.setVisibility(0);
                            }
                        }
                    }
                    this.nextAttachLayout.setAlpha(1.0f);
                    this.currentAttachLayout.setAlpha(1.0f);
                    this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(0.0f));
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatAttachAlert.this.m2394lambda$showLayout$23$orgtelegramuiComponentsChatAttachAlert(layout, onEnd);
                        }
                    });
                    return;
                }
                AnimatorSet animator = new AnimatorSet();
                this.nextAttachLayout.setAlpha(0.0f);
                this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f));
                animator.playTogether(ObjectAnimator.ofFloat(this.currentAttachLayout, View.TRANSLATION_Y, AndroidUtilities.dp(78.0f) + t), ObjectAnimator.ofFloat(this.currentAttachLayout, this.ATTACH_ALERT_LAYOUT_TRANSLATION, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, this.actionBar.getAlpha(), 0.0f));
                animator.setDuration(180L);
                animator.setStartDelay(20L);
                animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator.addListener(new AnonymousClass17(onEnd));
                this.viewChangeAnimator = animator;
                animator.start();
                return;
            }
            attachAlertLayout.scrollToTop();
        }
    }

    /* renamed from: lambda$showLayout$20$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2391lambda$showLayout$20$orgtelegramuiComponentsChatAttachAlert() {
        AttachAlertLayout attachAlertLayout;
        ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview;
        if (Build.VERSION.SDK_INT >= 20) {
            this.container.setLayerType(0, null);
        }
        this.viewChangeAnimator = null;
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        if (attachAlertLayout2 != this.photoLayout && (attachAlertLayout = this.nextAttachLayout) != (chatAttachAlertPhotoLayoutPreview = this.photoPreviewLayout) && attachAlertLayout2 != attachAlertLayout && attachAlertLayout2 != chatAttachAlertPhotoLayoutPreview) {
            this.containerView.removeView(this.currentAttachLayout);
        }
        this.currentAttachLayout.setVisibility(8);
        this.currentAttachLayout.onHidden();
        this.nextAttachLayout.onShown();
        this.currentAttachLayout = this.nextAttachLayout;
        this.nextAttachLayout = null;
        int[] iArr = this.scrollOffsetY;
        iArr[0] = iArr[1];
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$17 */
    /* loaded from: classes5.dex */
    public class AnonymousClass17 extends AnimatorListenerAdapter {
        final /* synthetic */ Runnable val$onEnd;

        AnonymousClass17(Runnable runnable) {
            ChatAttachAlert.this = this$0;
            this.val$onEnd = runnable;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
            SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
            springAnimation.getSpring().setDampingRatio(0.75f);
            springAnimation.getSpring().setStiffness(500.0f);
            springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$17$$ExternalSyntheticLambda1
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    ChatAttachAlert.AnonymousClass17.this.m2399xe4ba0c27(dynamicAnimation, f, f2);
                }
            });
            final Runnable runnable = this.val$onEnd;
            springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$17$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    runnable.run();
                }
            });
            ChatAttachAlert.this.viewChangeAnimator = springAnimation;
            springAnimation.start();
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-ChatAttachAlert$17 */
        public /* synthetic */ void m2399xe4ba0c27(DynamicAnimation animation12, float value, float velocity) {
            if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout) {
                ChatAttachAlert.this.updateSelectedPosition(1);
            }
            ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            ChatAttachAlert.this.containerView.invalidate();
        }
    }

    /* renamed from: lambda$showLayout$23$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2394lambda$showLayout$23$orgtelegramuiComponentsChatAttachAlert(AttachAlertLayout layout, final Runnable onEnd) {
        final float fromActionBarAlpha = this.actionBar.getAlpha();
        final boolean showActionBar = this.nextAttachLayout.getCurrentItemTop() <= layout.getButtonsHideOffset();
        final float toActionBarAlpha = showActionBar ? 1.0f : 0.0f;
        SpringAnimation springAnimation = new SpringAnimation(new FloatValueHolder(0.0f));
        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda8
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                ChatAttachAlert.this.m2392lambda$showLayout$21$orgtelegramuiComponentsChatAttachAlert(fromActionBarAlpha, toActionBarAlpha, showActionBar, dynamicAnimation, f, f2);
            }
        });
        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda7
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ChatAttachAlert.this.m2393lambda$showLayout$22$orgtelegramuiComponentsChatAttachAlert(showActionBar, onEnd, dynamicAnimation, z, f, f2);
            }
        });
        springAnimation.setSpring(new SpringForce(500.0f));
        springAnimation.getSpring().setDampingRatio(1.0f);
        springAnimation.getSpring().setStiffness(1000.0f);
        springAnimation.start();
        this.viewChangeAnimator = springAnimation;
    }

    /* renamed from: lambda$showLayout$21$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2392lambda$showLayout$21$orgtelegramuiComponentsChatAttachAlert(float fromActionBarAlpha, float toActionBarAlpha, boolean showActionBar, DynamicAnimation animation, float value, float velocity) {
        float f = value / 500.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(f));
        this.actionBar.setAlpha(AndroidUtilities.lerp(fromActionBarAlpha, toActionBarAlpha, f));
        updateLayout(this.currentAttachLayout, false, 0);
        updateLayout(this.nextAttachLayout, false, 0);
        float mediaPreviewAlpha = (!(this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || showActionBar) ? 1.0f - f : f;
        this.mediaPreviewView.setAlpha(mediaPreviewAlpha);
        this.selectedView.setAlpha(1.0f - mediaPreviewAlpha);
        this.selectedView.setTranslationX((-AndroidUtilities.dp(16.0f)) * mediaPreviewAlpha);
        this.mediaPreviewView.setTranslationX((1.0f - mediaPreviewAlpha) * AndroidUtilities.dp(16.0f));
    }

    /* renamed from: lambda$showLayout$22$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2393lambda$showLayout$22$orgtelegramuiComponentsChatAttachAlert(boolean showActionBar, Runnable onEnd, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        this.currentAttachLayout.onHideShowProgress(1.0f);
        this.nextAttachLayout.onHideShowProgress(1.0f);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.nextAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.containerView.invalidate();
        this.actionBar.setTag(showActionBar ? 1 : null);
        onEnd.run();
    }

    public void updatePhotoPreview(boolean show) {
        if (show) {
            if (!this.canOpenPreview) {
                return;
            }
            if (this.photoPreviewLayout == null) {
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = new ChatAttachAlertPhotoLayoutPreview(this, getContext(), this.parentThemeDelegate);
                this.photoPreviewLayout = chatAttachAlertPhotoLayoutPreview;
                chatAttachAlertPhotoLayoutPreview.bringToFront();
            }
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            AttachAlertLayout attachAlertLayout2 = this.photoPreviewLayout;
            if (attachAlertLayout == attachAlertLayout2) {
                attachAlertLayout2 = this.photoLayout;
            }
            showLayout(attachAlertLayout2);
            return;
        }
        showLayout(this.photoLayout);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout;
        if (requestCode == 5 && grantResults != null && grantResults.length > 0 && grantResults[0] == 0) {
            openContactsLayout();
        } else if (requestCode == 30 && (chatAttachAlertLocationLayout = this.locationLayout) != null && this.currentAttachLayout == chatAttachAlertLocationLayout && isShowing()) {
            this.locationLayout.openShareLiveLocation();
        }
    }

    private void openContactsLayout() {
        if (this.contactsLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = new ChatAttachAlertContactsLayout(this, getContext(), this.resourcesProvider);
            this.contactsLayout = chatAttachAlertContactsLayout;
            attachAlertLayoutArr[2] = chatAttachAlertContactsLayout;
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda25
                @Override // org.telegram.ui.Components.ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate
                public final void didSelectContact(TLRPC.User user, boolean z, int i) {
                    ChatAttachAlert.this.m2390x1edc3a40(user, z, i);
                }
            });
        }
        showLayout(this.contactsLayout);
    }

    /* renamed from: lambda$openContactsLayout$24$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2390x1edc3a40(TLRPC.User user, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendContact(user, notify, scheduleDate);
    }

    public void openAudioLayout(boolean show) {
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext(), this.resourcesProvider);
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlertAudioLayout.AudioSelectDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda24
                @Override // org.telegram.ui.Components.ChatAttachAlertAudioLayout.AudioSelectDelegate
                public final void didSelectAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i) {
                    ChatAttachAlert.this.m2389x51cf13b4(arrayList, charSequence, z, i);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            TLRPC.Chat currentChat = chatActivity.getCurrentChat();
            this.audioLayout.setMaxSelectedFiles(((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) ? -1 : 1);
        }
        if (show) {
            showLayout(this.audioLayout);
        }
    }

    /* renamed from: lambda$openAudioLayout$25$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2389x51cf13b4(ArrayList audios, CharSequence caption, boolean notify, int scheduleDate) {
        ((ChatActivity) this.baseFragment).sendAudio(audios, caption, notify, scheduleDate);
    }

    private void openDocumentsLayout(boolean show) {
        if (this.documentLayout == null) {
            int type = this.isSoundPicker ? 2 : 0;
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), type, this.resourcesProvider);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.18
                {
                    ChatAttachAlert.this = this;
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void didSelectFiles(ArrayList<String> files, String caption, ArrayList<MessageObject> fmessages, boolean notify, int scheduleDate) {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) ChatAttachAlert.this.baseFragment).didSelectFiles(files, caption, fmessages, notify, scheduleDate);
                    } else if (ChatAttachAlert.this.baseFragment instanceof PassportActivity) {
                        ((PassportActivity) ChatAttachAlert.this.baseFragment).didSelectFiles(files, caption, notify, scheduleDate);
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                        ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectPhotos(photos, notify, scheduleDate);
                    } else if (ChatAttachAlert.this.baseFragment instanceof PassportActivity) {
                        ((PassportActivity) ChatAttachAlert.this.baseFragment).didSelectPhotos(photos, notify, scheduleDate);
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void startDocumentSelectActivity() {
                    if (ChatAttachAlert.this.baseFragment instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) ChatAttachAlert.this.baseFragment).startDocumentSelectActivity();
                    } else if (ChatAttachAlert.this.baseFragment instanceof PassportActivity) {
                        ((PassportActivity) ChatAttachAlert.this.baseFragment).startDocumentSelectActivity();
                    }
                }

                @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
                public void startMusicSelectActivity() {
                    ChatAttachAlert.this.openAudioLayout(true);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        int i = 1;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            TLRPC.Chat currentChat = chatActivity.getCurrentChat();
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout2 = this.documentLayout;
            if ((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) {
                i = -1;
            }
            chatAttachAlertDocumentLayout2.setMaxSelectedFiles(i);
        } else {
            this.documentLayout.setMaxSelectedFiles(this.maxSelectedPhotos);
            this.documentLayout.setCanSelectOnlyImageFiles(!this.isSoundPicker);
        }
        this.documentLayout.isSoundPicker = this.isSoundPicker;
        if (show) {
            showLayout(this.documentLayout);
        }
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        int i = 0;
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.commentsAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            if (!this.isSoundPicker) {
                this.frameLayout2.setVisibility(0);
            }
            this.writeButtonContainer.setVisibility(0);
            if (!this.typeButtonsAvailable && !this.isSoundPicker) {
                this.shadow.setVisibility(0);
            }
        } else if (this.typeButtonsAvailable) {
            this.buttonsRecyclerView.setVisibility(0);
        }
        float f = 0.2f;
        float f2 = 0.0f;
        if (animated) {
            this.commentsAnimator = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            FrameLayout frameLayout = this.frameLayout2;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout2 = this.writeButtonContainer;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout3, property3, fArr3));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout4, property4, fArr4));
            View view = this.selectedCountView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(view, property5, fArr5));
            View view2 = this.selectedCountView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (show) {
                f = 1.0f;
            }
            fArr6[0] = f;
            animators.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
            View view3 = this.selectedCountView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
            if (this.actionBar.getTag() != null) {
                FrameLayout frameLayout5 = this.frameLayout2;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = show ? 0.0f : AndroidUtilities.dp(48.0f);
                animators.add(ObjectAnimator.ofFloat(frameLayout5, property8, fArr8));
                View view4 = this.shadow;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = show ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f);
                animators.add(ObjectAnimator.ofFloat(view4, property9, fArr9));
                View view5 = this.shadow;
                Property property10 = View.ALPHA;
                float[] fArr10 = new float[1];
                if (show) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view5, property10, fArr10));
            } else if (this.typeButtonsAvailable) {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                Property property11 = View.TRANSLATION_Y;
                float[] fArr11 = new float[1];
                fArr11[0] = show ? AndroidUtilities.dp(36.0f) : 0.0f;
                animators.add(ObjectAnimator.ofFloat(recyclerListView, property11, fArr11));
                View view6 = this.shadow;
                Property property12 = View.TRANSLATION_Y;
                float[] fArr12 = new float[1];
                if (show) {
                    f2 = AndroidUtilities.dp(36.0f);
                }
                fArr12[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view6, property12, fArr12));
            } else if (!this.isSoundPicker) {
                this.shadow.setTranslationY(AndroidUtilities.dp(36.0f) + this.botMainButtonOffsetY);
                View view7 = this.shadow;
                Property property13 = View.ALPHA;
                float[] fArr13 = new float[1];
                if (show) {
                    f2 = 1.0f;
                }
                fArr13[0] = f2;
                animators.add(ObjectAnimator.ofFloat(view7, property13, fArr13));
            }
            this.commentsAnimator.playTogether(animators);
            this.commentsAnimator.setInterpolator(new DecelerateInterpolator());
            this.commentsAnimator.setDuration(180L);
            this.commentsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.19
                {
                    ChatAttachAlert.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ChatAttachAlert.this.commentsAnimator)) {
                        if (!show) {
                            if (!ChatAttachAlert.this.isSoundPicker) {
                                ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            }
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                            if (!ChatAttachAlert.this.typeButtonsAvailable && !ChatAttachAlert.this.isSoundPicker) {
                                ChatAttachAlert.this.shadow.setVisibility(4);
                            }
                        } else if (ChatAttachAlert.this.typeButtonsAvailable && (ChatAttachAlert.this.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons())) {
                            ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                        }
                        ChatAttachAlert.this.commentsAnimator = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(ChatAttachAlert.this.commentsAnimator)) {
                        ChatAttachAlert.this.commentsAnimator = null;
                    }
                }
            });
            this.commentsAnimator.start();
        } else {
            this.frameLayout2.setAlpha(show ? 1.0f : 0.0f);
            this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            View view8 = this.selectedCountView;
            if (show) {
                f = 1.0f;
            }
            view8.setScaleY(f);
            this.selectedCountView.setAlpha(show ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(show ? 0.0f : AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((show ? AndroidUtilities.dp(36.0f) : AndroidUtilities.dp(84.0f)) + this.botMainButtonOffsetY);
                View view9 = this.shadow;
                if (show) {
                    f2 = 1.0f;
                }
                view9.setAlpha(f2);
            } else if (this.typeButtonsAvailable) {
                AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
                if (attachAlertLayout == null || attachAlertLayout.shouldHideBottomButtons()) {
                    RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
                    if (show) {
                        f2 = AndroidUtilities.dp(36.0f);
                    }
                    recyclerListView2.setTranslationY(f2);
                }
                View view10 = this.shadow;
                if (show) {
                    i = AndroidUtilities.dp(36.0f);
                }
                view10.setTranslationY(i + this.botMainButtonOffsetY);
            } else {
                this.shadow.setTranslationY(AndroidUtilities.dp(36.0f) + this.botMainButtonOffsetY);
                View view11 = this.shadow;
                if (show) {
                    f2 = 1.0f;
                }
                view11.setAlpha(f2);
            }
            if (!show) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
                if (!this.typeButtonsAvailable) {
                    this.shadow.setVisibility(4);
                }
            }
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected void cancelSheetAnimation() {
        if (this.currentSheetAnimation != null) {
            this.currentSheetAnimation.cancel();
            SpringAnimation springAnimation = this.appearSpringAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            AnimatorSet animatorSet = this.buttonsAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        this.photoLayout.setTranslationX(0.0f);
        this.mediaPreviewView.setAlpha(0.0f);
        this.selectedView.setAlpha(1.0f);
        int fromTranslationY = this.containerView.getMeasuredHeight();
        this.containerView.setTranslationY(fromTranslationY);
        AnimatorSet animatorSet = new AnimatorSet();
        this.buttonsAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, 0.0f, 400.0f));
        this.buttonsAnimation.setDuration(400L);
        this.buttonsAnimation.setStartDelay(20L);
        this.ATTACH_ALERT_PROGRESS.set(this, Float.valueOf(0.0f));
        this.buttonsAnimation.start();
        if (this.navigationBarAnimation != null) {
            this.navigationBarAnimation.cancel();
        }
        this.navigationBarAnimation = ValueAnimator.ofFloat(this.navigationBarAlpha, 1.0f);
        this.navigationBarAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda10
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatAttachAlert.this.m2382x1a9a4de7(valueAnimator);
            }
        });
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(0.75f);
        this.appearSpringAnimation.getSpring().setStiffness(350.0f);
        this.appearSpringAnimation.start();
        if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
            this.container.setLayerType(2, null);
        }
        this.currentSheetAnimationType = 1;
        this.currentSheetAnimation = new AnimatorSet();
        AnimatorSet animatorSet2 = this.currentSheetAnimation;
        Animator[] animatorArr = new Animator[1];
        ColorDrawable colorDrawable = this.backDrawable;
        Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
        int[] iArr = new int[1];
        iArr[0] = this.dimBehind ? this.dimBehindAlpha : 0;
        animatorArr[0] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
        animatorSet2.playTogether(animatorArr);
        this.currentSheetAnimation.setDuration(400L);
        this.currentSheetAnimation.setStartDelay(20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        final BottomSheet.BottomSheetDelegateInterface delegate = super.delegate;
        final Runnable onAnimationEnd = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlert.this.m2383xdd86b746(delegate);
            }
        };
        this.appearSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda6
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ChatAttachAlert.this.m2384xa07320a5(onAnimationEnd, dynamicAnimation, z, f, f2);
            }
        });
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.21
            {
                ChatAttachAlert.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animation) && ChatAttachAlert.this.appearSpringAnimation != null && !ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                    onAnimationEnd.run();
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animation)) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                    ChatAttachAlert.this.currentSheetAnimationType = 0;
                }
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        ValueAnimator navigationBarAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        setNavBarAlpha(0.0f);
        navigationBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda21
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatAttachAlert.this.m2385x635f8a04(valueAnimator);
            }
        });
        navigationBarAnimator.setStartDelay(25L);
        navigationBarAnimator.setDuration(200L);
        navigationBarAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        navigationBarAnimator.start();
        return true;
    }

    /* renamed from: lambda$onCustomOpenAnimation$26$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2382x1a9a4de7(ValueAnimator a) {
        this.navigationBarAlpha = ((Float) a.getAnimatedValue()).floatValue();
        if (this.container != null) {
            this.container.invalidate();
        }
    }

    /* renamed from: lambda$onCustomOpenAnimation$27$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2383xdd86b746(BottomSheet.BottomSheetDelegateInterface delegate) {
        this.currentSheetAnimation = null;
        this.appearSpringAnimation = null;
        this.currentSheetAnimationType = 0;
        if (delegate != null) {
            delegate.onOpenAnimationEnd();
        }
        if (this.useHardwareLayer) {
            this.container.setLayerType(0, null);
        }
        if (this.isFullscreen) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= -1025;
            getWindow().setAttributes(params);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
    }

    /* renamed from: lambda$onCustomOpenAnimation$28$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2384xa07320a5(Runnable onAnimationEnd, DynamicAnimation animation, boolean cancelled, float value, float velocity) {
        if (this.currentSheetAnimation != null && !this.currentSheetAnimation.isRunning()) {
            onAnimationEnd.run();
        }
    }

    /* renamed from: lambda$onCustomOpenAnimation$29$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2385x635f8a04(ValueAnimator a) {
        setNavBarAlpha(((Float) a.getAnimatedValue()).floatValue());
    }

    private void setNavBarAlpha(float alpha) {
        boolean z = false;
        this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundGray), Math.min(255, Math.max(0, (int) (255.0f * alpha))));
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
        Window window = getWindow();
        if (AndroidUtilities.computePerceivedBrightness(this.navBarColor) > 0.721d) {
            z = true;
        }
        AndroidUtilities.setLightNavigationBar(window, z);
        getContainer().invalidate();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onContainerTouchEvent(MotionEvent event) {
        return this.currentAttachLayout.onContainerViewTouchEvent(event);
    }

    public void makeFocusable(final EditTextBoldCursor editText, final boolean showKeyboard) {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null && !this.enterCommentEventSent) {
            boolean keyboardVisible = chatAttachViewDelegate.needEnterComment();
            this.enterCommentEventSent = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.this.m2365x84f5e6e7(editText, showKeyboard);
                }
            }, keyboardVisible ? 200L : 0L);
        }
    }

    /* renamed from: lambda$makeFocusable$31$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2365x84f5e6e7(final EditTextBoldCursor editText, boolean showKeyboard) {
        setFocusable(true);
        editText.requestFocus();
        if (showKeyboard) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                }
            });
        }
    }

    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            AttachButton button = (AttachButton) view;
            button.textView.setTextColor(ColorUtils.blendARGB(getThemedColor(Theme.key_dialogTextGray2), getThemedColor(button.textKey), button.checkedState));
        } else if (view instanceof AttachBotButton) {
            AttachBotButton button2 = (AttachBotButton) view;
            button2.nameTextView.setTextColor(ColorUtils.blendARGB(getThemedColor(Theme.key_dialogTextGray2), button2.textColor, button2.checkedState));
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList;
        ArrayList<ThemeDescription> descriptions = new ArrayList<>();
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[a] != null && (arrayList = attachAlertLayoutArr[a].getThemeDescriptions()) != null) {
                    descriptions.addAll(arrayList);
                }
                a++;
            } else {
                descriptions.add(new ThemeDescription(this.container, 0, null, null, null, null, Theme.key_dialogBackgroundGray));
                return descriptions;
            }
        }
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView == null) {
            return;
        }
        int count = recyclerListView.getChildCount();
        for (int a = 0; a < count; a++) {
            applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(a));
        }
        this.selectedTextView.setTextColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItems) : getThemedColor(Theme.key_dialogTextBlack));
        this.mediaPreviewTextView.setTextColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItems) : getThemedColor(Theme.key_dialogTextBlack));
        this.doneItem.getTextView().setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        this.selectedMenuItem.setIconColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItems) : getThemedColor(Theme.key_dialogTextBlack));
        Theme.setDrawableColor(this.selectedMenuItem.getBackground(), this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItemsSelector) : getThemedColor(Theme.key_dialogButtonSelector));
        this.selectedMenuItem.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false);
        this.selectedMenuItem.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), true);
        this.selectedMenuItem.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        this.searchItem.setIconColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItems) : getThemedColor(Theme.key_dialogTextBlack));
        Theme.setDrawableColor(this.searchItem.getBackground(), this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItemsSelector) : getThemedColor(Theme.key_dialogButtonSelector));
        this.commentTextView.updateColors();
        if (this.sendPopupLayout != null) {
            int a2 = 0;
            while (true) {
                ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.itemCells;
                if (a2 >= actionBarMenuSubItemArr.length) {
                    break;
                }
                if (actionBarMenuSubItemArr[a2] != null) {
                    actionBarMenuSubItemArr[a2].setColors(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon));
                    this.itemCells[a2].setSelectorColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItemsSelector) : getThemedColor(Theme.key_dialogButtonSelector));
                }
                a2++;
            }
            this.sendPopupLayout.setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupLayout.invalidate();
            }
        }
        Drawable drawable = this.writeButtonDrawable;
        String str = Theme.key_dialogFloatingButton;
        Theme.setSelectorDrawableColor(drawable, getThemedColor(str), false);
        Drawable drawable2 = this.writeButtonDrawable;
        if (Build.VERSION.SDK_INT >= 21) {
            str = Theme.key_dialogFloatingButtonPressed;
        }
        Theme.setSelectorDrawableColor(drawable2, getThemedColor(str), true);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
        this.actionBarShadow.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.buttonsRecyclerView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
        boolean z = this.forceDarkTheme;
        String str2 = Theme.key_voipgroup_listViewBackground;
        recyclerListView2.setBackgroundColor(getThemedColor(z ? str2 : Theme.key_dialogBackground));
        this.frameLayout2.setBackgroundColor(getThemedColor(this.forceDarkTheme ? str2 : Theme.key_dialogBackground));
        this.selectedCountView.invalidate();
        this.actionBar.setBackgroundColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBar) : getThemedColor(Theme.key_dialogBackground));
        this.actionBar.setItemsColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItems) : getThemedColor(Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItemsSelector) : getThemedColor(Theme.key_dialogButtonSelector), false);
        this.actionBar.setTitleColor(this.forceDarkTheme ? getThemedColor(Theme.key_voipgroup_actionBarItems) : getThemedColor(Theme.key_dialogTextBlack));
        Drawable drawable3 = this.shadowDrawable;
        if (!this.forceDarkTheme) {
            str2 = Theme.key_dialogBackground;
        }
        Theme.setDrawableColor(drawable3, getThemedColor(str2));
        this.containerView.invalidate();
        int a3 = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a3 < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[a3] != null) {
                    attachAlertLayoutArr[a3].checkColors();
                }
                a3++;
            } else {
                return;
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomMeasure(View view, int width, int height) {
        if (this.photoLayout.onCustomMeasure(view, width, height)) {
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        if (this.photoLayout.onCustomLayout(view, left, top, right, bottom)) {
            return true;
        }
        return false;
    }

    public void onPause() {
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[a] != null) {
                    attachAlertLayoutArr[a].onPause();
                }
                a++;
            } else {
                this.paused = true;
                return;
            }
        }
    }

    public void onResume() {
        this.paused = false;
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[a] != null) {
                attachAlertLayoutArr[a].onResume();
            }
            a++;
        }
        if (isShowing()) {
            this.delegate.needEnterComment();
        }
    }

    public void onActivityResultFragment(int requestCode, Intent data, String currentPicturePath) {
        this.photoLayout.onActivityResultFragment(requestCode, data, currentPicturePath);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.reloadInlineHints || id == NotificationCenter.attachMenuBotsDidLoad) {
            ButtonsAdapter buttonsAdapter = this.buttonsAdapter;
            if (buttonsAdapter != null) {
                buttonsAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        }
    }

    public int getScrollOffsetY(int idx) {
        AttachAlertLayout attachAlertLayout = this.nextAttachLayout;
        if (attachAlertLayout != null && ((this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (attachAlertLayout instanceof ChatAttachAlertPhotoLayoutPreview))) {
            int[] iArr = this.scrollOffsetY;
            return AndroidUtilities.lerp(iArr[0], iArr[1], this.translationProgress);
        }
        return this.scrollOffsetY[idx];
    }

    public void updateSelectedPosition(int idx) {
        float toMove;
        int t;
        float moveProgress;
        int finalMove;
        int finalMove2;
        AttachAlertLayout layout = idx == 0 ? this.currentAttachLayout : this.nextAttachLayout;
        int scrollOffset = getScrollOffsetY(idx);
        int t2 = scrollOffset - this.backgroundPaddingTop;
        if (layout == this.pollLayout) {
            t = t2 - AndroidUtilities.dp(13.0f);
            toMove = AndroidUtilities.dp(11.0f);
        } else {
            t = t2 - AndroidUtilities.dp(39.0f);
            toMove = AndroidUtilities.dp(43.0f);
        }
        if (this.backgroundPaddingTop + t < ActionBar.getCurrentActionBarHeight()) {
            moveProgress = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - t) - this.backgroundPaddingTop) / toMove);
            this.cornerRadius = 1.0f - moveProgress;
        } else {
            moveProgress = 0.0f;
            this.cornerRadius = 1.0f;
        }
        if (AndroidUtilities.isTablet()) {
            finalMove = 16;
        } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            finalMove = 6;
        } else {
            finalMove = 12;
        }
        float offset = this.actionBar.getAlpha() != 0.0f ? 0.0f : AndroidUtilities.dp((1.0f - this.headerView.getAlpha()) * 26.0f);
        if (this.menuShowed && this.avatarPicker == 0) {
            this.selectedMenuItem.setTranslationY((scrollOffset - AndroidUtilities.dp((finalMove * moveProgress) + 37.0f)) + offset + this.currentPanTranslationY);
        } else {
            this.selectedMenuItem.setTranslationY(((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp(finalMove + 37)) + this.currentPanTranslationY);
        }
        this.searchItem.setTranslationY(((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(4.0f)) - AndroidUtilities.dp(finalMove + 37)) + this.currentPanTranslationY);
        FrameLayout frameLayout = this.headerView;
        float dp = (scrollOffset - AndroidUtilities.dp((finalMove * moveProgress) + 25.0f)) + offset + this.currentPanTranslationY;
        this.baseSelectedTextViewTranslationY = dp;
        frameLayout.setTranslationY(dp);
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout != null && layout == chatAttachAlertPollLayout) {
            if (AndroidUtilities.isTablet()) {
                finalMove2 = 63;
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                finalMove2 = 53;
            } else {
                finalMove2 = 59;
            }
            this.doneItem.setTranslationY(Math.max(0.0f, (this.pollLayout.getTranslationY() + scrollOffset) - AndroidUtilities.dp((finalMove2 * moveProgress) + 7.0f)) + this.currentPanTranslationY);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0047, code lost:
        if (((org.telegram.ui.ChatActivity) r0).allowSendGifs() != false) goto L26;
     */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x009d  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x013f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateActionBarVisibility(final boolean r12, boolean r13) {
        /*
            Method dump skipped, instructions count: 408
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateActionBarVisibility(boolean, boolean):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x0067, code lost:
        if (((androidx.dynamicanimation.animation.SpringAnimation) r5).isRunning() != false) goto L41;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateLayout(org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r9, boolean r10, int r11) {
        /*
            r8 = this;
            if (r9 != 0) goto L3
            return
        L3:
            int r0 = r9.getCurrentItemTop()
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto Ld
            return
        Ld:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r1 = r8.currentAttachLayout
            r2 = 1
            r3 = 0
            if (r9 != r1) goto L1b
            int r1 = r9.getButtonsHideOffset()
            if (r0 > r1) goto L1b
            r1 = 1
            goto L1c
        L1b:
            r1 = 0
        L1c:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r8.currentAttachLayout
            org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview r5 = r8.photoPreviewLayout
            if (r4 == r5) goto L2f
            boolean r4 = r8.keyboardVisible
            if (r4 == 0) goto L2f
            if (r10 == 0) goto L2f
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r8.currentAttachLayout
            boolean r4 = r4 instanceof org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout
            if (r4 != 0) goto L2f
            r10 = 0
        L2f:
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r4 = r8.currentAttachLayout
            if (r9 != r4) goto L36
            r8.updateActionBarVisibility(r1, r10)
        L36:
            android.view.ViewGroup$LayoutParams r4 = r9.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            if (r4 != 0) goto L40
            r5 = 0
            goto L42
        L40:
            int r5 = r4.topMargin
        L42:
            r6 = 1093664768(0x41300000, float:11.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            int r0 = r0 + r5
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r5 = r8.currentAttachLayout
            if (r5 != r9) goto L50
            r6 = 0
            goto L51
        L50:
            r6 = 1
        L51:
            boolean r5 = r5 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r5 != 0) goto L5b
            org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout r5 = r8.nextAttachLayout
            boolean r5 = r5 instanceof org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview
            if (r5 == 0) goto L6a
        L5b:
            java.lang.Object r5 = r8.viewChangeAnimator
            boolean r7 = r5 instanceof androidx.dynamicanimation.animation.SpringAnimation
            if (r7 == 0) goto L6a
            androidx.dynamicanimation.animation.SpringAnimation r5 = (androidx.dynamicanimation.animation.SpringAnimation) r5
            boolean r5 = r5.isRunning()
            if (r5 == 0) goto L6a
            goto L6b
        L6a:
            r2 = 0
        L6b:
            int[] r3 = r8.scrollOffsetY
            r5 = r3[r6]
            if (r5 != r0) goto L7b
            if (r2 == 0) goto L74
            goto L7b
        L74:
            if (r11 == 0) goto L89
            r3 = r3[r6]
            r8.previousScrollOffsetY = r3
            goto L89
        L7b:
            r5 = r3[r6]
            r8.previousScrollOffsetY = r5
            r3[r6] = r0
            r8.updateSelectedPosition(r6)
            android.view.ViewGroup r3 = r8.containerView
            r3.invalidate()
        L89:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateLayout(org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout, boolean, int):void");
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:60:0x0107  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0116  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0125  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0163  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateCountButton(int r19) {
        /*
            Method dump skipped, instructions count: 473
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateCountButton(int):void");
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void init() {
        AttachAlertLayout layoutToSet;
        if (this.baseFragment == null) {
            return;
        }
        this.botButtonWasVisible = false;
        this.botButtonProgressWasVisible = false;
        this.botMainButtonOffsetY = 0.0f;
        this.botMainButtonTextView.setVisibility(8);
        this.botProgressView.setAlpha(0.0f);
        this.botProgressView.setScaleX(0.1f);
        this.botProgressView.setScaleY(0.1f);
        this.botProgressView.setVisibility(8);
        this.buttonsRecyclerView.setAlpha(1.0f);
        this.buttonsRecyclerView.setTranslationY(0.0f);
        for (int i = 0; i < this.botAttachLayouts.size(); i++) {
            this.botAttachLayouts.valueAt(i).setMeasureOffsetY(0);
        }
        this.shadow.setAlpha(1.0f);
        this.shadow.setTranslationY(0.0f);
        BaseFragment baseFragment = this.baseFragment;
        int i2 = 4;
        if ((baseFragment instanceof ChatActivity) && this.avatarPicker != 2) {
            TLRPC.Chat chat = ((ChatActivity) baseFragment).getCurrentChat();
            TLRPC.User user = ((ChatActivity) this.baseFragment).getCurrentUser();
            if (chat != null) {
                this.mediaEnabled = ChatObject.canSendMedia(chat);
                this.pollsEnabled = ChatObject.canSendPolls(chat);
            } else {
                this.pollsEnabled = user != null && user.bot;
            }
        } else {
            this.commentTextView.setVisibility(4);
        }
        this.photoLayout.onInit(this.mediaEnabled);
        this.commentTextView.hidePopup(true);
        this.enterCommentEventSent = false;
        setFocusable(false);
        if (this.isSoundPicker) {
            openDocumentsLayout(false);
            layoutToSet = this.documentLayout;
            this.selectedId = 4L;
        } else {
            MessageObject messageObject = this.editingMessageObject;
            if (messageObject != null && (messageObject.isMusic() || (this.editingMessageObject.isDocument() && !this.editingMessageObject.isGif()))) {
                if (this.editingMessageObject.isMusic()) {
                    openAudioLayout(false);
                    layoutToSet = this.audioLayout;
                    this.selectedId = 3L;
                } else {
                    openDocumentsLayout(false);
                    layoutToSet = this.documentLayout;
                    this.selectedId = 4L;
                }
                this.typeButtonsAvailable = !this.editingMessageObject.hasValidGroupId();
            } else {
                layoutToSet = this.photoLayout;
                this.typeButtonsAvailable = this.avatarPicker == 0;
                this.selectedId = 1L;
            }
        }
        this.buttonsRecyclerView.setVisibility(this.typeButtonsAvailable ? 0 : 8);
        this.shadow.setVisibility(this.typeButtonsAvailable ? 0 : 4);
        if (this.currentAttachLayout != layoutToSet) {
            if (this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
            }
            this.containerView.removeView(this.currentAttachLayout);
            this.currentAttachLayout.onHide();
            this.currentAttachLayout.setVisibility(8);
            this.currentAttachLayout.onHidden();
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            this.currentAttachLayout = layoutToSet;
            setAllowNestedScroll(true);
            if (this.currentAttachLayout.getParent() == null) {
                this.containerView.addView(this.currentAttachLayout, 0, LayoutHelper.createFrame(-1, -1.0f));
            }
            layoutToSet.setAlpha(1.0f);
            layoutToSet.setVisibility(0);
            layoutToSet.onShow(null);
            layoutToSet.onShown();
            ActionBar actionBar = this.actionBar;
            if (layoutToSet.needsActionBar() != 0) {
                i2 = 0;
            }
            actionBar.setVisibility(i2);
            this.actionBarShadow.setVisibility(this.actionBar.getVisibility());
        }
        AttachAlertLayout previousLayout = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (previousLayout != chatAttachAlertPhotoLayout) {
            chatAttachAlertPhotoLayout.setCheckCameraWhenShown(true);
        }
        updateCountButton(0);
        this.buttonsAdapter.notifyDataSetChanged();
        this.commentTextView.setText("");
        this.buttonsLayoutManager.scrollToPositionWithOffset(0, MediaController.VIDEO_BITRATE_480);
    }

    public void onDestroy() {
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[a] != null) {
                attachAlertLayoutArr[a].onDestroy();
            }
            a++;
        }
        int a2 = this.currentAccount;
        NotificationCenter.getInstance(a2).removeObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        this.baseFragment = null;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
    public void onOpenAnimationEnd() {
        MediaController.AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        this.currentAttachLayout.onOpenAnimationEnd();
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
        this.openTransitionFinished = true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
    public void onOpenAnimationStart() {
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
    public boolean canDismiss() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void setAllowDrawContent(boolean value) {
        super.setAllowDrawContent(value);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
    }

    public void setAvatarPicker(int type, boolean search) {
        this.avatarPicker = type;
        this.avatarSearch = search;
        if (type != 0) {
            this.typeButtonsAvailable = false;
            if (this.currentAttachLayout == null) {
                this.buttonsRecyclerView.setVisibility(8);
                this.shadow.setVisibility(8);
            }
            if (this.avatarPicker == 2) {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", R.string.ChoosePhotoOrVideo));
                return;
            } else {
                this.selectedTextView.setText(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
                return;
            }
        }
        this.typeButtonsAvailable = true;
    }

    public void setSoundPicker() {
        this.isSoundPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
        this.selectedTextView.setText(LocaleController.getString("ChoosePhotoOrVideo", R.string.ChoosePhotoOrVideo));
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        if (this.editingMessageObject != null) {
            return;
        }
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
    }

    public void setOpenWithFrontFaceCamera(boolean value) {
        this.openWithFrontFaceCamera = value;
    }

    public ChatAttachAlertPhotoLayout getPhotoLayout() {
        return this.photoLayout;
    }

    /* loaded from: classes5.dex */
    public class ButtonsAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_BOT_BUTTON = 1;
        private static final int VIEW_TYPE_BUTTON = 0;
        private int attachBotsEndRow;
        private int attachBotsStartRow;
        private List<TLRPC.TL_attachMenuBot> attachMenuBots = new ArrayList();
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;

        public ButtonsAdapter(Context context) {
            ChatAttachAlert.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new AttachButton(this.mContext);
                    break;
                default:
                    view = new AttachBotButton(this.mContext);
                    break;
            }
            view.setImportantForAccessibility(1);
            view.setFocusable(true);
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    AttachButton attachButton = (AttachButton) holder.itemView;
                    if (position == this.galleryButton) {
                        attachButton.setTextAndIcon(1, LocaleController.getString("ChatGallery", R.string.ChatGallery), Theme.chat_attachButtonDrawables[0], Theme.key_chat_attachGalleryBackground, Theme.key_chat_attachGalleryText);
                        attachButton.setTag(1);
                        return;
                    } else if (position == this.documentButton) {
                        attachButton.setTextAndIcon(4, LocaleController.getString("ChatDocument", R.string.ChatDocument), Theme.chat_attachButtonDrawables[2], Theme.key_chat_attachFileBackground, Theme.key_chat_attachFileText);
                        attachButton.setTag(4);
                        return;
                    } else if (position == this.locationButton) {
                        attachButton.setTextAndIcon(6, LocaleController.getString("ChatLocation", R.string.ChatLocation), Theme.chat_attachButtonDrawables[4], Theme.key_chat_attachLocationBackground, Theme.key_chat_attachLocationText);
                        attachButton.setTag(6);
                        return;
                    } else if (position == this.musicButton) {
                        attachButton.setTextAndIcon(3, LocaleController.getString("AttachMusic", R.string.AttachMusic), Theme.chat_attachButtonDrawables[1], Theme.key_chat_attachAudioBackground, Theme.key_chat_attachAudioText);
                        attachButton.setTag(3);
                        return;
                    } else if (position == this.pollButton) {
                        attachButton.setTextAndIcon(9, LocaleController.getString("Poll", R.string.Poll), Theme.chat_attachButtonDrawables[5], Theme.key_chat_attachPollBackground, Theme.key_chat_attachPollText);
                        attachButton.setTag(9);
                        return;
                    } else if (position == this.contactButton) {
                        attachButton.setTextAndIcon(5, LocaleController.getString("AttachContact", R.string.AttachContact), Theme.chat_attachButtonDrawables[3], Theme.key_chat_attachContactBackground, Theme.key_chat_attachContactText);
                        attachButton.setTag(5);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    AttachBotButton child = (AttachBotButton) holder.itemView;
                    int i = this.attachBotsStartRow;
                    if (position >= i && position < this.attachBotsEndRow) {
                        int position2 = position - i;
                        child.setTag(Integer.valueOf(position2));
                        TLRPC.TL_attachMenuBot bot = this.attachMenuBots.get(position2);
                        child.setAttachBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(bot.bot_id)), bot);
                        return;
                    }
                    int position3 = position - this.buttonsCount;
                    child.setTag(Integer.valueOf(position3));
                    child.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(position3).peer.user_id)));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ChatAttachAlert.this.applyAttachButtonColors(holder.itemView);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.buttonsCount;
            if (ChatAttachAlert.this.editingMessageObject == null && (ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                return count + MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size();
            }
            return count;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            this.attachBotsStartRow = -1;
            this.attachBotsEndRow = -1;
            if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                int i = this.buttonsCount;
                int i2 = i + 1;
                this.buttonsCount = i2;
                this.galleryButton = i;
                this.buttonsCount = i2 + 1;
                this.documentButton = i2;
            } else if (ChatAttachAlert.this.editingMessageObject == null) {
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i3 = this.buttonsCount;
                    this.buttonsCount = i3 + 1;
                    this.galleryButton = i3;
                    if ((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && !((ChatActivity) ChatAttachAlert.this.baseFragment).isInScheduleMode() && !((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat()) {
                        ChatActivity chatActivity = (ChatActivity) ChatAttachAlert.this.baseFragment;
                        this.attachBotsStartRow = this.buttonsCount;
                        this.attachMenuBots.clear();
                        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).getAttachMenuBots().bots.iterator();
                        while (it.hasNext()) {
                            TLRPC.TL_attachMenuBot bot = it.next();
                            if (MediaDataController.canShowAttachMenuBot(bot, chatActivity.getCurrentChat() != null ? chatActivity.getCurrentChat() : chatActivity.getCurrentUser())) {
                                this.attachMenuBots.add(bot);
                            }
                        }
                        int size = this.buttonsCount + this.attachMenuBots.size();
                        this.buttonsCount = size;
                        this.attachBotsEndRow = size;
                    }
                    int i4 = this.buttonsCount;
                    this.buttonsCount = i4 + 1;
                    this.documentButton = i4;
                }
                int i5 = this.buttonsCount;
                this.buttonsCount = i5 + 1;
                this.locationButton = i5;
                if (ChatAttachAlert.this.pollsEnabled) {
                    int i6 = this.buttonsCount;
                    this.buttonsCount = i6 + 1;
                    this.pollButton = i6;
                } else {
                    int i7 = this.buttonsCount;
                    this.buttonsCount = i7 + 1;
                    this.contactButton = i7;
                }
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i8 = this.buttonsCount;
                    this.buttonsCount = i8 + 1;
                    this.musicButton = i8;
                }
                TLRPC.User user = ChatAttachAlert.this.baseFragment instanceof ChatActivity ? ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentUser() : null;
                if (user != null && user.bot) {
                    int i9 = this.buttonsCount;
                    this.buttonsCount = i9 + 1;
                    this.contactButton = i9;
                }
            } else if ((ChatAttachAlert.this.editingMessageObject.isMusic() || ChatAttachAlert.this.editingMessageObject.isDocument()) && ChatAttachAlert.this.editingMessageObject.hasValidGroupId()) {
                if (ChatAttachAlert.this.editingMessageObject.isMusic()) {
                    int i10 = this.buttonsCount;
                    this.buttonsCount = i10 + 1;
                    this.musicButton = i10;
                } else {
                    int i11 = this.buttonsCount;
                    this.buttonsCount = i11 + 1;
                    this.documentButton = i11;
                }
            } else {
                int i12 = this.buttonsCount;
                int i13 = i12 + 1;
                this.buttonsCount = i13;
                this.galleryButton = i12;
                int i14 = i13 + 1;
                this.buttonsCount = i14;
                this.documentButton = i13;
                this.buttonsCount = i14 + 1;
                this.musicButton = i14;
            }
            super.notifyDataSetChanged();
        }

        public int getButtonsCount() {
            return this.buttonsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position < this.buttonsCount) {
                return (position < this.attachBotsStartRow || position >= this.attachBotsEndRow) ? 0 : 1;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.doOnIdle(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlert.this.removeFromRoot();
                }
            });
        } else {
            removeFromRoot();
        }
    }

    public void removeFromRoot() {
        if (this.containerView != null) {
            this.containerView.setVisibility(4);
        }
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        }
        this.contactsLayout = null;
        this.audioLayout = null;
        this.pollLayout = null;
        this.locationLayout = null;
        this.documentLayout = null;
        int a = 1;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a < attachAlertLayoutArr.length) {
                if (attachAlertLayoutArr[a] != null) {
                    attachAlertLayoutArr[a].onDestroy();
                    this.containerView.removeView(this.layouts[a]);
                    this.layouts[a] = null;
                }
                a++;
            } else {
                updateActionBarVisibility(false, false);
                super.dismissInternal();
                return;
            }
        }
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        } else if (this.currentAttachLayout.onBackPressed()) {
        } else {
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
                this.commentTextView.hidePopup(true);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissWithButtonClick(int item) {
        super.dismissWithButtonClick(item);
        this.currentAttachLayout.onDismissWithButtonClick(item);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithTouchOutside() {
        return this.currentAttachLayout.canDismissWithTouchOutside();
    }

    public void dismiss(boolean passConfirmationAlert) {
        if (passConfirmationAlert) {
            this.allowPassConfirmationAlert = passConfirmationAlert;
        }
        dismiss();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        if (this.currentAttachLayout.onDismiss() || isDismissed()) {
            return;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        this.botAttachLayouts.clear();
        if (!this.allowPassConfirmationAlert && this.baseFragment != null && this.currentAttachLayout.getSelectedItemsCount() > 0) {
            if (this.confirmationAlertShown) {
                return;
            }
            this.confirmationAlertShown = true;
            AlertDialog dialog = new AlertDialog.Builder(this.baseFragment.getParentActivity(), this.parentThemeDelegate).setTitle(LocaleController.getString("DiscardSelectionAlertTitle", R.string.DiscardSelectionAlertTitle)).setMessage(LocaleController.getString("DiscardSelectionAlertMessage", R.string.DiscardSelectionAlertMessage)).setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda31
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatAttachAlert.this.m2361lambda$dismiss$32$orgtelegramuiComponentsChatAttachAlert(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda30
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatAttachAlert.this.m2362lambda$dismiss$33$orgtelegramuiComponentsChatAttachAlert(dialogInterface);
                }
            }).setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda33
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ChatAttachAlert.this.m2363lambda$dismiss$34$orgtelegramuiComponentsChatAttachAlert(dialogInterface);
                }
            }).create();
            dialog.show();
            TextView button = (TextView) dialog.getButton(-1);
            if (button != null) {
                button.setTextColor(getThemedColor(Theme.key_dialogTextRed2));
                return;
            }
            return;
        }
        int a = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (a >= attachAlertLayoutArr.length) {
                break;
            }
            if (attachAlertLayoutArr[a] != null && this.currentAttachLayout != attachAlertLayoutArr[a]) {
                attachAlertLayoutArr[a].onDismiss();
            }
            a++;
        }
        AndroidUtilities.setNavigationBarColor(getWindow(), ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundGray), 0), true, new AndroidUtilities.IntColorCallback() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda17
            @Override // org.telegram.messenger.AndroidUtilities.IntColorCallback
            public final void run(int i) {
                ChatAttachAlert.this.m2364lambda$dismiss$35$orgtelegramuiComponentsChatAttachAlert(i);
            }
        });
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
        super.dismiss();
        this.allowPassConfirmationAlert = false;
    }

    /* renamed from: lambda$dismiss$32$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2361lambda$dismiss$32$orgtelegramuiComponentsChatAttachAlert(DialogInterface dialogInterface, int i) {
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    /* renamed from: lambda$dismiss$33$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2362lambda$dismiss$33$orgtelegramuiComponentsChatAttachAlert(DialogInterface di) {
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(1.5f);
        this.appearSpringAnimation.getSpring().setStiffness(1500.0f);
        this.appearSpringAnimation.start();
    }

    /* renamed from: lambda$dismiss$34$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2363lambda$dismiss$34$orgtelegramuiComponentsChatAttachAlert(DialogInterface di) {
        this.confirmationAlertShown = false;
    }

    /* renamed from: lambda$dismiss$35$org-telegram-ui-Components-ChatAttachAlert */
    public /* synthetic */ void m2364lambda$dismiss$35$orgtelegramuiComponentsChatAttachAlert(int tcolor) {
        this.navBarColorKey = null;
        this.navBarColor = tcolor;
        this.containerView.invalidate();
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.currentAttachLayout.onSheetKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void setAllowNestedScroll(boolean allowNestedScroll) {
        this.allowNestedScroll = allowNestedScroll;
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    public EditTextEmoji getCommentTextView() {
        return this.commentTextView;
    }

    public ChatAttachAlertDocumentLayout getDocumentLayout() {
        return this.documentLayout;
    }
}
