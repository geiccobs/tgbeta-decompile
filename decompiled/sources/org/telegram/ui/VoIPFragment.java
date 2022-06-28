package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.Instance;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.DarkAlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.AcceptDeclineView;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog;
import org.telegram.ui.Components.voip.VoIPButtonsLayout;
import org.telegram.ui.Components.voip.VoIPFloatingLayout;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Components.voip.VoIPNotificationsLayout;
import org.telegram.ui.Components.voip.VoIPOverlayBackground;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.Components.voip.VoIPStatusTextView;
import org.telegram.ui.Components.voip.VoIPTextureView;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.Components.voip.VoIPWindowView;
import org.telegram.ui.VoIPFragment;
import org.webrtc.EglBase;
import org.webrtc.GlRectDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;
/* loaded from: classes4.dex */
public class VoIPFragment implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    private static final int STATE_FLOATING = 2;
    private static final int STATE_FULLSCREEN = 1;
    private static final int STATE_GONE = 0;
    private static VoIPFragment instance;
    private AcceptDeclineView acceptDeclineView;
    private AccessibilityManager accessibilityManager;
    Activity activity;
    private ImageView backIcon;
    View bottomShadow;
    private VoIPButtonsLayout buttonsLayout;
    boolean callingUserIsVideo;
    private VoIPFloatingLayout callingUserMiniFloatingLayout;
    private TextureViewRenderer callingUserMiniTextureRenderer;
    private BackupImageView callingUserPhotoView;
    private BackupImageView callingUserPhotoViewMini;
    private VoIPTextureView callingUserTextureView;
    private TextView callingUserTitle;
    boolean cameraForceExpanded;
    private Animator cameraShowingAnimator;
    private boolean canHideUI;
    private boolean canSwitchToPip;
    private boolean canZoomGesture;
    private final int currentAccount;
    TLRPC.User currentUser;
    private VoIPFloatingLayout currentUserCameraFloatingLayout;
    private boolean currentUserCameraIsFullscreen;
    boolean currentUserIsVideo;
    private VoIPTextureView currentUserTextureView;
    private boolean deviceIsLocked;
    private boolean emojiExpanded;
    LinearLayout emojiLayout;
    private boolean emojiLoaded;
    TextView emojiRationalTextView;
    boolean enterFromPiP;
    private float enterTransitionProgress;
    boolean fillNaviagtionBar;
    float fillNaviagtionBarValue;
    private ViewGroup fragmentView;
    boolean hideUiRunnableWaiting;
    private boolean isFinished;
    private boolean isInPinchToZoomTouchMode;
    private boolean isVideoCall;
    long lastContentTapTime;
    private WindowInsets lastInsets;
    private boolean lockOnScreen;
    ValueAnimator naviagtionBarAnimator;
    VoIPNotificationsLayout notificationsLayout;
    private VoIPOverlayBackground overlayBackground;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartCenterX;
    private float pinchStartCenterY;
    private float pinchStartDistance;
    private float pinchTranslationX;
    private float pinchTranslationY;
    private int pointerId1;
    private int pointerId2;
    private PrivateVideoPreviewDialog previewDialog;
    private boolean screenWasWakeup;
    private ImageView speakerPhoneIcon;
    LinearLayout statusLayout;
    private int statusLayoutAnimateToOffset;
    private VoIPStatusTextView statusTextView;
    private boolean switchingToPip;
    HintView tapToVideoTooltip;
    View topShadow;
    float touchSlop;
    ValueAnimator uiVisibilityAnimator;
    private VoIPWindowView windowView;
    ValueAnimator zoomBackAnimator;
    private boolean zoomStarted;
    VoIPToggleButton[] bottomButtons = new VoIPToggleButton[4];
    ImageView[] emojiViews = new ImageView[4];
    Emoji.EmojiDrawable[] emojiDrawables = new Emoji.EmojiDrawable[4];
    Paint overlayPaint = new Paint();
    Paint overlayBottomPaint = new Paint();
    private boolean uiVisible = true;
    float uiVisibilityAlpha = 1.0f;
    int animationIndex = -1;
    ValueAnimator.AnimatorUpdateListener statusbarAnimatorListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFragment.this.m4777lambda$new$0$orgtelegramuiVoIPFragment(valueAnimator);
        }
    };
    ValueAnimator.AnimatorUpdateListener navigationBarAnimationListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda11
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFragment.this.m4778lambda$new$1$orgtelegramuiVoIPFragment(valueAnimator);
        }
    };
    Runnable hideUIRunnable = new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda14
        @Override // java.lang.Runnable
        public final void run() {
            VoIPFragment.this.m4779lambda$new$2$orgtelegramuiVoIPFragment();
        }
    };
    float pinchScale = 1.0f;
    TLRPC.User callingUser = VoIPService.getSharedInstance().getUser();
    boolean isOutgoing = VoIPService.getSharedInstance().isOutgoing();
    private int previousState = -1;
    private int currentState = VoIPService.getSharedInstance().getCallState();

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4777lambda$new$0$orgtelegramuiVoIPFragment(ValueAnimator valueAnimator) {
        this.uiVisibilityAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4778lambda$new$1$orgtelegramuiVoIPFragment(ValueAnimator valueAnimator) {
        this.fillNaviagtionBarValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4779lambda$new$2$orgtelegramuiVoIPFragment() {
        this.hideUiRunnableWaiting = false;
        if (this.canHideUI && this.uiVisible && !this.emojiExpanded) {
            this.lastContentTapTime = System.currentTimeMillis();
            showUi(false);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public static void show(Activity activity, int account) {
        show(activity, false, account);
    }

    public static void show(Activity activity, boolean overlay, int account) {
        boolean screenOn;
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null && voIPFragment.windowView.getParent() == null) {
            VoIPFragment voIPFragment2 = instance;
            if (voIPFragment2 != null) {
                voIPFragment2.callingUserTextureView.renderer.release();
                instance.currentUserTextureView.renderer.release();
                instance.callingUserMiniTextureRenderer.release();
                instance.destroy();
            }
            instance = null;
        }
        if (instance != null || activity.isFinishing()) {
            return;
        }
        boolean z = false;
        boolean transitionFromPip = VoIPPiPView.getInstance() != null;
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getUser() == null) {
            return;
        }
        final VoIPFragment fragment = new VoIPFragment(account);
        fragment.activity = activity;
        instance = fragment;
        if (!transitionFromPip) {
            z = true;
        }
        VoIPWindowView windowView = new VoIPWindowView(activity, z) { // from class: org.telegram.ui.VoIPFragment.1
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchKeyEvent(KeyEvent event) {
                VoIPService service;
                if (fragment.isFinished || fragment.switchingToPip) {
                    return false;
                }
                int keyCode = event.getKeyCode();
                if (keyCode == 4 && event.getAction() == 1 && !fragment.lockOnScreen) {
                    fragment.onBackPressed();
                    return true;
                } else if ((keyCode == 25 || keyCode == 24) && fragment.currentState == 15 && (service = VoIPService.getSharedInstance()) != null) {
                    service.stopRinging();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
            }
        };
        instance.deviceIsLocked = ((KeyguardManager) activity.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        PowerManager pm = (PowerManager) activity.getSystemService("power");
        if (Build.VERSION.SDK_INT >= 20) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }
        VoIPFragment voIPFragment3 = instance;
        voIPFragment3.screenWasWakeup = !screenOn;
        windowView.setLockOnScreen(voIPFragment3.deviceIsLocked);
        fragment.windowView = windowView;
        if (Build.VERSION.SDK_INT >= 20) {
            windowView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda1
                @Override // android.view.View.OnApplyWindowInsetsListener
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return VoIPFragment.lambda$show$3(VoIPFragment.this, view, windowInsets);
                }
            });
        }
        WindowManager wm = (WindowManager) activity.getSystemService("window");
        WindowManager.LayoutParams layoutParams = windowView.createWindowLayoutParams();
        if (overlay) {
            if (Build.VERSION.SDK_INT >= 26) {
                layoutParams.type = 2038;
            } else {
                layoutParams.type = 2003;
            }
        }
        wm.addView(windowView, layoutParams);
        View view = fragment.createView(activity);
        windowView.addView(view);
        if (transitionFromPip) {
            fragment.enterTransitionProgress = 0.0f;
            fragment.startTransitionFromPiP();
            return;
        }
        fragment.enterTransitionProgress = 1.0f;
        fragment.updateSystemBarColors();
    }

    public static /* synthetic */ WindowInsets lambda$show$3(VoIPFragment fragment, View view, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 21) {
            fragment.setInsets(windowInsets);
        }
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    public void onBackPressed() {
        if (this.isFinished || this.switchingToPip) {
            return;
        }
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.dismiss(false, false);
        } else if (this.callingUserIsVideo && this.currentUserIsVideo && this.cameraForceExpanded) {
            this.cameraForceExpanded = false;
            this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
            this.currentUserCameraIsFullscreen = false;
            this.previousState = this.currentState;
            updateViewState();
        } else if (this.emojiExpanded) {
            expandEmoji(false);
        } else if (this.emojiRationalTextView.getVisibility() != 8) {
        } else {
            if (this.canSwitchToPip && !this.lockOnScreen) {
                if (AndroidUtilities.checkInlinePermissions(this.activity)) {
                    switchToPip();
                    return;
                } else {
                    requestInlinePermissions();
                    return;
                }
            }
            this.windowView.finish();
        }
    }

    public static void clearInstance() {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        if (instance != null) {
            if (VoIPService.getSharedInstance() != null) {
                int h = instance.windowView.getMeasuredHeight();
                if (Build.VERSION.SDK_INT >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                    h -= windowInsets2.getSystemWindowInsetBottom();
                }
                VoIPFragment voIPFragment = instance;
                if (voIPFragment.canSwitchToPip) {
                    VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), h, 0);
                    if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                        VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                        VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
                    }
                }
            }
            instance.callingUserTextureView.renderer.release();
            instance.currentUserTextureView.renderer.release();
            instance.callingUserMiniTextureRenderer.release();
            instance.destroy();
        }
        instance = null;
    }

    public static VoIPFragment getInstance() {
        return instance;
    }

    private void setInsets(WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        ((FrameLayout.LayoutParams) this.buttonsLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.acceptDeclineView.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.backIcon.getLayoutParams()).topMargin = this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.speakerPhoneIcon.getLayoutParams()).topMargin = this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.topShadow.getLayoutParams()).topMargin = this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.statusLayout.getLayoutParams()).topMargin = AndroidUtilities.dp(68.0f) + this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.emojiLayout.getLayoutParams()).topMargin = AndroidUtilities.dp(17.0f) + this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.callingUserPhotoViewMini.getLayoutParams()).topMargin = AndroidUtilities.dp(68.0f) + this.lastInsets.getSystemWindowInsetTop();
        ((FrameLayout.LayoutParams) this.currentUserCameraFloatingLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.callingUserMiniFloatingLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.callingUserTextureView.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.notificationsLayout.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        ((FrameLayout.LayoutParams) this.bottomShadow.getLayoutParams()).bottomMargin = this.lastInsets.getSystemWindowInsetBottom();
        this.currentUserCameraFloatingLayout.setInsets(this.lastInsets);
        this.callingUserMiniFloatingLayout.setInsets(this.lastInsets);
        this.fragmentView.requestLayout();
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.setBottomPadding(this.lastInsets.getSystemWindowInsetBottom());
        }
    }

    public VoIPFragment(int account) {
        this.currentAccount = account;
        this.currentUser = MessagesController.getInstance(account).getUser(Long.valueOf(UserConfig.getInstance(account).getClientUserId()));
        VoIPService.getSharedInstance().registerStateListener(this);
        NotificationCenter.getInstance(account).addObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
    }

    public void destroy() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onStateChanged(int state) {
        int i = this.currentState;
        if (i != state) {
            this.previousState = i;
            this.currentState = state;
            if (this.windowView != null) {
                updateViewState();
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.voipServiceCreated) {
            if (this.currentState == 17 && VoIPService.getSharedInstance() != null) {
                this.currentUserTextureView.renderer.release();
                this.callingUserTextureView.renderer.release();
                this.callingUserMiniTextureRenderer.release();
                initRenderers();
                VoIPService.getSharedInstance().registerStateListener(this);
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            updateKeyView(true);
        } else if (id == NotificationCenter.closeInCallActivity) {
            this.windowView.finish();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onSignalBarsCountChanged(int count) {
        VoIPStatusTextView voIPStatusTextView = this.statusTextView;
        if (voIPStatusTextView != null) {
            voIPStatusTextView.setSignalBarCount(count);
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onMediaStateUpdated(int audioState, int videoState) {
        this.previousState = this.currentState;
        if (videoState == 2 && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraSwitch(boolean isFrontFace) {
        this.previousState = this.currentState;
        updateViewState();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onVideoAvailableChange(boolean isAvailable) {
        this.previousState = this.currentState;
        if (isAvailable && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onScreenOnChange(boolean screenOn) {
    }

    public View createView(Context context) {
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(context, AccessibilityManager.class);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.VoIPFragment.2
            boolean check;
            long pressedTime;
            float pressedX;
            float pressedY;

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (Build.VERSION.SDK_INT >= 20 && VoIPFragment.this.lastInsets != null) {
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), VoIPFragment.this.lastInsets.getSystemWindowInsetTop(), VoIPFragment.this.overlayPaint);
                }
                if (Build.VERSION.SDK_INT >= 20 && VoIPFragment.this.lastInsets != null) {
                    canvas.drawRect(0.0f, getMeasuredHeight() - VoIPFragment.this.lastInsets.getSystemWindowInsetBottom(), getMeasuredWidth(), getMeasuredHeight(), VoIPFragment.this.overlayBottomPaint);
                }
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent ev) {
                if (!VoIPFragment.this.canZoomGesture && !VoIPFragment.this.isInPinchToZoomTouchMode && !VoIPFragment.this.zoomStarted && ev.getActionMasked() != 0) {
                    VoIPFragment.this.finishZoom();
                    return false;
                }
                if (ev.getActionMasked() == 0) {
                    VoIPFragment.this.canZoomGesture = false;
                    VoIPFragment.this.isInPinchToZoomTouchMode = false;
                    VoIPFragment.this.zoomStarted = false;
                }
                VoIPTextureView currentTextureView = VoIPFragment.this.getFullscreenTextureView();
                if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
                    if (ev.getActionMasked() == 0) {
                        AndroidUtilities.rectTmp.set(currentTextureView.getX(), currentTextureView.getY(), currentTextureView.getX() + currentTextureView.getMeasuredWidth(), currentTextureView.getY() + currentTextureView.getMeasuredHeight());
                        AndroidUtilities.rectTmp.inset(((currentTextureView.getMeasuredHeight() * currentTextureView.scaleTextureToFill) - currentTextureView.getMeasuredHeight()) / 2.0f, ((currentTextureView.getMeasuredWidth() * currentTextureView.scaleTextureToFill) - currentTextureView.getMeasuredWidth()) / 2.0f);
                        if (!GroupCallActivity.isLandscapeMode) {
                            AndroidUtilities.rectTmp.top = Math.max(AndroidUtilities.rectTmp.top, ActionBar.getCurrentActionBarHeight());
                            AndroidUtilities.rectTmp.bottom = Math.min(AndroidUtilities.rectTmp.bottom, currentTextureView.getMeasuredHeight() - AndroidUtilities.dp(90.0f));
                        } else {
                            AndroidUtilities.rectTmp.top = Math.max(AndroidUtilities.rectTmp.top, ActionBar.getCurrentActionBarHeight());
                            AndroidUtilities.rectTmp.right = Math.min(AndroidUtilities.rectTmp.right, currentTextureView.getMeasuredWidth() - AndroidUtilities.dp(90.0f));
                        }
                        VoIPFragment.this.canZoomGesture = AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY());
                        if (!VoIPFragment.this.canZoomGesture) {
                            VoIPFragment.this.finishZoom();
                        }
                    }
                    if (VoIPFragment.this.canZoomGesture && !VoIPFragment.this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                        VoIPFragment.this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                        VoIPFragment voIPFragment = VoIPFragment.this;
                        voIPFragment.pinchStartCenterX = voIPFragment.pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                        VoIPFragment voIPFragment2 = VoIPFragment.this;
                        voIPFragment2.pinchStartCenterY = voIPFragment2.pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                        VoIPFragment.this.pinchScale = 1.0f;
                        VoIPFragment.this.pointerId1 = ev.getPointerId(0);
                        VoIPFragment.this.pointerId2 = ev.getPointerId(1);
                        VoIPFragment.this.isInPinchToZoomTouchMode = true;
                    }
                } else if (ev.getActionMasked() == 2 && VoIPFragment.this.isInPinchToZoomTouchMode) {
                    int index1 = -1;
                    int index2 = -1;
                    for (int i = 0; i < ev.getPointerCount(); i++) {
                        if (VoIPFragment.this.pointerId1 == ev.getPointerId(i)) {
                            index1 = i;
                        }
                        if (VoIPFragment.this.pointerId2 == ev.getPointerId(i)) {
                            index2 = i;
                        }
                    }
                    if (index1 != -1 && index2 != -1) {
                        VoIPFragment.this.pinchScale = ((float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1))) / VoIPFragment.this.pinchStartDistance;
                        if (VoIPFragment.this.pinchScale > 1.005f && !VoIPFragment.this.zoomStarted) {
                            VoIPFragment.this.pinchStartDistance = (float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1));
                            VoIPFragment voIPFragment3 = VoIPFragment.this;
                            voIPFragment3.pinchStartCenterX = voIPFragment3.pinchCenterX = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
                            VoIPFragment voIPFragment4 = VoIPFragment.this;
                            voIPFragment4.pinchStartCenterY = voIPFragment4.pinchCenterY = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
                            VoIPFragment.this.pinchScale = 1.0f;
                            VoIPFragment.this.pinchTranslationX = 0.0f;
                            VoIPFragment.this.pinchTranslationY = 0.0f;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            VoIPFragment.this.zoomStarted = true;
                            VoIPFragment.this.isInPinchToZoomTouchMode = true;
                        }
                        float newPinchCenterX = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
                        float newPinchCenterY = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
                        float moveDx = VoIPFragment.this.pinchStartCenterX - newPinchCenterX;
                        float moveDy = VoIPFragment.this.pinchStartCenterY - newPinchCenterY;
                        VoIPFragment voIPFragment5 = VoIPFragment.this;
                        voIPFragment5.pinchTranslationX = (-moveDx) / voIPFragment5.pinchScale;
                        VoIPFragment voIPFragment6 = VoIPFragment.this;
                        voIPFragment6.pinchTranslationY = (-moveDy) / voIPFragment6.pinchScale;
                        invalidate();
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        VoIPFragment.this.finishZoom();
                    }
                } else {
                    int index12 = ev.getActionMasked();
                    if (index12 == 1 || ((ev.getActionMasked() == 6 && VoIPFragment.this.checkPointerIds(ev)) || ev.getActionMasked() == 3)) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        VoIPFragment.this.finishZoom();
                    }
                }
                VoIPFragment.this.fragmentView.invalidate();
                switch (ev.getAction()) {
                    case 0:
                        this.pressedX = ev.getX();
                        this.pressedY = ev.getY();
                        this.check = true;
                        this.pressedTime = System.currentTimeMillis();
                        break;
                    case 1:
                        if (this.check) {
                            float dx = ev.getX() - this.pressedX;
                            float dy = ev.getY() - this.pressedY;
                            long currentTime = System.currentTimeMillis();
                            if ((dx * dx) + (dy * dy) < VoIPFragment.this.touchSlop * VoIPFragment.this.touchSlop && currentTime - this.pressedTime < 300 && currentTime - VoIPFragment.this.lastContentTapTime > 300) {
                                VoIPFragment.this.lastContentTapTime = System.currentTimeMillis();
                                if (VoIPFragment.this.emojiExpanded) {
                                    VoIPFragment.this.expandEmoji(false);
                                } else if (VoIPFragment.this.canHideUI) {
                                    VoIPFragment voIPFragment7 = VoIPFragment.this;
                                    voIPFragment7.showUi(!voIPFragment7.uiVisible);
                                    VoIPFragment voIPFragment8 = VoIPFragment.this;
                                    voIPFragment8.previousState = voIPFragment8.currentState;
                                    VoIPFragment.this.updateViewState();
                                }
                            }
                            this.check = false;
                            break;
                        }
                        break;
                    case 3:
                        this.check = false;
                        break;
                }
                return VoIPFragment.this.canZoomGesture || this.check;
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != VoIPFragment.this.callingUserPhotoView || (!VoIPFragment.this.currentUserIsVideo && !VoIPFragment.this.callingUserIsVideo)) {
                    if ((child == VoIPFragment.this.callingUserPhotoView || child == VoIPFragment.this.callingUserTextureView || (child == VoIPFragment.this.currentUserCameraFloatingLayout && VoIPFragment.this.currentUserCameraIsFullscreen)) && (VoIPFragment.this.zoomStarted || VoIPFragment.this.zoomBackAnimator != null)) {
                        canvas.save();
                        canvas.scale(VoIPFragment.this.pinchScale, VoIPFragment.this.pinchScale, VoIPFragment.this.pinchCenterX, VoIPFragment.this.pinchCenterY);
                        canvas.translate(VoIPFragment.this.pinchTranslationX, VoIPFragment.this.pinchTranslationY);
                        boolean b = super.drawChild(canvas, child, drawingTime);
                        canvas.restore();
                        return b;
                    }
                    return super.drawChild(canvas, child, drawingTime);
                }
                return false;
            }
        };
        boolean z = false;
        frameLayout.setClipToPadding(false);
        frameLayout.setClipChildren(false);
        frameLayout.setBackgroundColor(-16777216);
        updateSystemBarColors();
        this.fragmentView = frameLayout;
        frameLayout.setFitsSystemWindows(true);
        this.callingUserPhotoView = new BackupImageView(context) { // from class: org.telegram.ui.VoIPFragment.3
            int blackoutColor = ColorUtils.setAlphaComponent(-16777216, 76);

            @Override // org.telegram.ui.Components.BackupImageView, android.view.View
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawColor(this.blackoutColor);
            }
        };
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true, false, false);
        this.callingUserTextureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.callingUserTextureView.renderer.setEnableHardwareScaler(true);
        this.callingUserTextureView.renderer.setRotateTextureWithScreen(true);
        this.callingUserTextureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        frameLayout.addView(this.callingUserPhotoView);
        frameLayout.addView(this.callingUserTextureView);
        BackgroundGradientDrawable gradientDrawable = new BackgroundGradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963});
        BackgroundGradientDrawable.Sizes sizes = BackgroundGradientDrawable.Sizes.ofDeviceScreen(BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT);
        gradientDrawable.startDithering(sizes, new BackgroundGradientDrawable.ListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.4
            @Override // org.telegram.ui.Components.BackgroundGradientDrawable.ListenerAdapter, org.telegram.ui.Components.BackgroundGradientDrawable.Listener
            public void onAllSizesReady() {
                VoIPFragment.this.callingUserPhotoView.invalidate();
            }
        });
        VoIPOverlayBackground voIPOverlayBackground = new VoIPOverlayBackground(context);
        this.overlayBackground = voIPOverlayBackground;
        voIPOverlayBackground.setVisibility(8);
        this.callingUserPhotoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda21
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver, boolean z2, boolean z3, boolean z4) {
                VoIPFragment.this.m4770lambda$createView$4$orgtelegramuiVoIPFragment(imageReceiver, z2, z3, z4);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        this.callingUserPhotoView.setImage(ImageLocation.getForUserOrChat(this.callingUser, 0), (String) null, gradientDrawable, this.callingUser);
        VoIPFloatingLayout voIPFloatingLayout = new VoIPFloatingLayout(context);
        this.currentUserCameraFloatingLayout = voIPFloatingLayout;
        voIPFloatingLayout.setDelegate(new VoIPFloatingLayout.VoIPFloatingLayoutDelegate() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda23
            @Override // org.telegram.ui.Components.voip.VoIPFloatingLayout.VoIPFloatingLayoutDelegate
            public final void onChange(float f, boolean z2) {
                VoIPFragment.this.m4771lambda$createView$5$orgtelegramuiVoIPFragment(f, z2);
            }
        });
        this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
        this.currentUserCameraIsFullscreen = true;
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context, true, false);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setIsCamera(true);
        this.currentUserTextureView.renderer.setUseCameraRotation(true);
        this.currentUserCameraFloatingLayout.setOnTapListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPFragment.this.m4772lambda$createView$6$orgtelegramuiVoIPFragment(view);
            }
        });
        this.currentUserTextureView.renderer.setMirror(true);
        this.currentUserCameraFloatingLayout.addView(this.currentUserTextureView);
        VoIPFloatingLayout voIPFloatingLayout2 = new VoIPFloatingLayout(context);
        this.callingUserMiniFloatingLayout = voIPFloatingLayout2;
        voIPFloatingLayout2.alwaysFloating = true;
        this.callingUserMiniFloatingLayout.setFloatingMode(true, false);
        TextureViewRenderer textureViewRenderer = new TextureViewRenderer(context);
        this.callingUserMiniTextureRenderer = textureViewRenderer;
        textureViewRenderer.setEnableHardwareScaler(true);
        this.callingUserMiniTextureRenderer.setIsCamera(false);
        this.callingUserMiniTextureRenderer.setFpsReduction(30.0f);
        this.callingUserMiniTextureRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        View backgroundView = new View(context);
        backgroundView.setBackgroundColor(-14999773);
        this.callingUserMiniFloatingLayout.addView(backgroundView, LayoutHelper.createFrame(-1, -1.0f));
        this.callingUserMiniFloatingLayout.addView(this.callingUserMiniTextureRenderer, LayoutHelper.createFrame(-1, -2, 17));
        this.callingUserMiniFloatingLayout.setOnTapListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPFragment.this.m4773lambda$createView$7$orgtelegramuiVoIPFragment(view);
            }
        });
        this.callingUserMiniFloatingLayout.setVisibility(8);
        frameLayout.addView(this.currentUserCameraFloatingLayout, LayoutHelper.createFrame(-2, -2.0f));
        frameLayout.addView(this.callingUserMiniFloatingLayout);
        frameLayout.addView(this.overlayBackground);
        View view = new View(context);
        this.bottomShadow = view;
        view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 127)}));
        frameLayout.addView(this.bottomShadow, LayoutHelper.createFrame(-1, 140, 80));
        View view2 = new View(context);
        this.topShadow = view2;
        view2.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 102), 0}));
        frameLayout.addView(this.topShadow, LayoutHelper.createFrame(-1, 140, 48));
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.VoIPFragment.5
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setVisibleToUser(VoIPFragment.this.emojiLoaded);
            }
        };
        this.emojiLayout = linearLayout;
        linearLayout.setOrientation(0);
        this.emojiLayout.setPadding(0, 0, 0, AndroidUtilities.dp(30.0f));
        this.emojiLayout.setClipToPadding(false);
        this.emojiLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                VoIPFragment.this.m4774lambda$createView$8$orgtelegramuiVoIPFragment(view3);
            }
        });
        TextView textView = new TextView(context);
        this.emojiRationalTextView = textView;
        textView.setText(LocaleController.formatString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip, UserObject.getFirstName(this.callingUser)));
        this.emojiRationalTextView.setTextSize(1, 16.0f);
        this.emojiRationalTextView.setTextColor(-1);
        this.emojiRationalTextView.setGravity(17);
        this.emojiRationalTextView.setVisibility(8);
        int i = 0;
        while (i < 4) {
            this.emojiViews[i] = new ImageView(context);
            this.emojiViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
            this.emojiLayout.addView(this.emojiViews[i], LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            i++;
        }
        LinearLayout linearLayout2 = new LinearLayout(context) { // from class: org.telegram.ui.VoIPFragment.6
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                VoIPService service = VoIPService.getSharedInstance();
                CharSequence callingUserTitleText = VoIPFragment.this.callingUserTitle.getText();
                if (service != null && !TextUtils.isEmpty(callingUserTitleText)) {
                    StringBuilder builder = new StringBuilder(callingUserTitleText);
                    builder.append(", ");
                    if (service.privateCall != null && service.privateCall.video) {
                        builder.append(LocaleController.getString("VoipInVideoCallBranding", R.string.VoipInVideoCallBranding));
                    } else {
                        builder.append(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding));
                    }
                    long callDuration = service.getCallDuration();
                    if (callDuration > 0) {
                        builder.append(", ");
                        builder.append(LocaleController.formatDuration((int) (callDuration / 1000)));
                    }
                    info.setText(builder);
                }
            }
        };
        this.statusLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        this.statusLayout.setFocusable(true);
        this.statusLayout.setFocusableInTouchMode(true);
        BackupImageView backupImageView = new BackupImageView(context);
        this.callingUserPhotoViewMini = backupImageView;
        backupImageView.setImage(ImageLocation.getForUserOrChat(this.callingUser, 1), (String) null, Theme.createCircleDrawable(AndroidUtilities.dp(135.0f), -16777216), this.callingUser);
        this.callingUserPhotoViewMini.setRoundRadius(AndroidUtilities.dp(135.0f) / 2);
        this.callingUserPhotoViewMini.setVisibility(8);
        TextView textView2 = new TextView(context);
        this.callingUserTitle = textView2;
        textView2.setTextSize(1, 24.0f);
        this.callingUserTitle.setText(ContactsController.formatName(this.callingUser.first_name, this.callingUser.last_name));
        this.callingUserTitle.setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(0.6666667f), 1275068416);
        this.callingUserTitle.setTextColor(-1);
        this.callingUserTitle.setGravity(1);
        this.callingUserTitle.setImportantForAccessibility(2);
        this.statusLayout.addView(this.callingUserTitle, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        VoIPStatusTextView voIPStatusTextView = new VoIPStatusTextView(context);
        this.statusTextView = voIPStatusTextView;
        ViewCompat.setImportantForAccessibility(voIPStatusTextView, 4);
        this.statusLayout.addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        this.statusLayout.setClipChildren(false);
        this.statusLayout.setClipToPadding(false);
        this.statusLayout.setPadding(0, 0, 0, AndroidUtilities.dp(15.0f));
        frameLayout.addView(this.callingUserPhotoViewMini, LayoutHelper.createFrame(TsExtractor.TS_STREAM_TYPE_E_AC3, 135.0f, 1, 0.0f, 68.0f, 0.0f, 0.0f));
        frameLayout.addView(this.statusLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 68.0f, 0.0f, 0.0f));
        frameLayout.addView(this.emojiLayout, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 17.0f, 0.0f, 0.0f));
        frameLayout.addView(this.emojiRationalTextView, LayoutHelper.createFrame(-1, -2.0f, 17, 24.0f, 32.0f, 24.0f, 0.0f));
        this.buttonsLayout = new VoIPButtonsLayout(context);
        for (int i2 = 0; i2 < 4; i2++) {
            this.bottomButtons[i2] = new VoIPToggleButton(context);
            this.buttonsLayout.addView(this.bottomButtons[i2]);
        }
        AcceptDeclineView acceptDeclineView = new AcceptDeclineView(context);
        this.acceptDeclineView = acceptDeclineView;
        acceptDeclineView.setListener(new AcceptDeclineView.Listener() { // from class: org.telegram.ui.VoIPFragment.7
            @Override // org.telegram.ui.Components.voip.AcceptDeclineView.Listener
            public void onAccept() {
                if (VoIPFragment.this.currentState == 17) {
                    Intent intent = new Intent(VoIPFragment.this.activity, VoIPService.class);
                    intent.putExtra("user_id", VoIPFragment.this.callingUser.id);
                    intent.putExtra("is_outgoing", true);
                    intent.putExtra("start_incall_activity", false);
                    intent.putExtra("video_call", VoIPFragment.this.isVideoCall);
                    intent.putExtra("can_video_call", VoIPFragment.this.isVideoCall);
                    intent.putExtra("account", VoIPFragment.this.currentAccount);
                    try {
                        VoIPFragment.this.activity.startService(intent);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else if (Build.VERSION.SDK_INT >= 23 && VoIPFragment.this.activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                    VoIPFragment.this.activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                    if (VoIPFragment.this.currentUserIsVideo) {
                        VoIPService.getSharedInstance().requestVideoCall(false);
                    }
                }
            }

            @Override // org.telegram.ui.Components.voip.AcceptDeclineView.Listener
            public void onDecline() {
                if (VoIPFragment.this.currentState == 17) {
                    VoIPFragment.this.windowView.finish();
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
            }
        });
        this.acceptDeclineView.setScreenWasWakeup(this.screenWasWakeup);
        frameLayout.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2, 80));
        frameLayout.addView(this.acceptDeclineView, LayoutHelper.createFrame(-1, 186, 80));
        ImageView imageView = new ImageView(context);
        this.backIcon = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.backIcon.setImageResource(R.drawable.ic_ab_back);
        this.backIcon.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.backIcon.setContentDescription(LocaleController.getString("Back", R.string.Back));
        frameLayout.addView(this.backIcon, LayoutHelper.createFrame(56, 56, 51));
        ImageView imageView2 = new ImageView(context) { // from class: org.telegram.ui.VoIPFragment.8
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setClassName(ToggleButton.class.getName());
                info.setCheckable(true);
                VoIPService service = VoIPService.getSharedInstance();
                if (service != null) {
                    info.setChecked(service.isSpeakerphoneOn());
                }
            }
        };
        this.speakerPhoneIcon = imageView2;
        imageView2.setContentDescription(LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker));
        this.speakerPhoneIcon.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.speakerPhoneIcon.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        frameLayout.addView(this.speakerPhoneIcon, LayoutHelper.createFrame(56, 56, 53));
        this.speakerPhoneIcon.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                VoIPFragment.this.m4775lambda$createView$9$orgtelegramuiVoIPFragment(view3);
            }
        });
        this.backIcon.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                VoIPFragment.this.m4768lambda$createView$10$orgtelegramuiVoIPFragment(view3);
            }
        });
        if (this.windowView.isLockOnScreen()) {
            this.backIcon.setVisibility(8);
        }
        VoIPNotificationsLayout voIPNotificationsLayout = new VoIPNotificationsLayout(context);
        this.notificationsLayout = voIPNotificationsLayout;
        voIPNotificationsLayout.setGravity(80);
        this.notificationsLayout.setOnViewsUpdated(new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                VoIPFragment.this.m4769lambda$createView$11$orgtelegramuiVoIPFragment();
            }
        });
        frameLayout.addView(this.notificationsLayout, LayoutHelper.createFrame(-1, 200.0f, 80, 16.0f, 0.0f, 16.0f, 0.0f));
        HintView hintView = new HintView(context, 4);
        this.tapToVideoTooltip = hintView;
        hintView.setText(LocaleController.getString("TapToTurnCamera", R.string.TapToTurnCamera));
        frameLayout.addView(this.tapToVideoTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 8.0f));
        this.tapToVideoTooltip.setBottomOffset(AndroidUtilities.dp(4.0f));
        this.tapToVideoTooltip.setVisibility(8);
        updateViewState();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (!this.isVideoCall) {
                if (service.privateCall != null && service.privateCall.video) {
                    z = true;
                }
                this.isVideoCall = z;
            }
            initRenderers();
        }
        return frameLayout;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4770lambda$createView$4$orgtelegramuiVoIPFragment(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        ImageReceiver.BitmapHolder bmp = imageReceiver.getBitmapSafe();
        if (bmp != null) {
            this.overlayBackground.setBackground(bmp);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4771lambda$createView$5$orgtelegramuiVoIPFragment(float progress, boolean value) {
        this.currentUserTextureView.setScreenshareMiniProgress(progress, value);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4772lambda$createView$6$orgtelegramuiVoIPFragment(View view) {
        if (this.currentUserIsVideo && this.callingUserIsVideo && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.callingUserMiniFloatingLayout.setRelativePosition(this.currentUserCameraFloatingLayout);
            this.currentUserCameraIsFullscreen = true;
            this.cameraForceExpanded = true;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4773lambda$createView$7$orgtelegramuiVoIPFragment(View view) {
        if (this.cameraForceExpanded && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
            this.currentUserCameraIsFullscreen = false;
            this.cameraForceExpanded = false;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4774lambda$createView$8$orgtelegramuiVoIPFragment(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime < 500) {
            return;
        }
        this.lastContentTapTime = System.currentTimeMillis();
        if (this.emojiLoaded) {
            expandEmoji(!this.emojiExpanded);
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4775lambda$createView$9$orgtelegramuiVoIPFragment(View view) {
        if (this.speakerPhoneIcon.getTag() != null && VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4768lambda$createView$10$orgtelegramuiVoIPFragment(View view) {
        if (!this.lockOnScreen) {
            onBackPressed();
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4769lambda$createView$11$orgtelegramuiVoIPFragment() {
        this.previousState = this.currentState;
        updateViewState();
    }

    public boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0);
    }

    public VoIPTextureView getFullscreenTextureView() {
        if (this.callingUserIsVideo) {
            return this.callingUserTextureView;
        }
        return this.currentUserTextureView;
    }

    public void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.zoomBackAnimator = ofFloat;
            final float fromScale = this.pinchScale;
            final float fromTranslateX = this.pinchTranslationX;
            final float fromTranslateY = this.pinchTranslationY;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda22
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoIPFragment.this.m4776lambda$finishZoom$12$orgtelegramuiVoIPFragment(fromScale, fromTranslateX, fromTranslateY, valueAnimator);
                }
            });
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPFragment.this.zoomBackAnimator = null;
                    VoIPFragment.this.pinchScale = 1.0f;
                    VoIPFragment.this.pinchTranslationX = 0.0f;
                    VoIPFragment.this.pinchTranslationY = 0.0f;
                    VoIPFragment.this.fragmentView.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350L);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    /* renamed from: lambda$finishZoom$12$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4776lambda$finishZoom$12$orgtelegramuiVoIPFragment(float fromScale, float fromTranslateX, float fromTranslateY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (fromScale * v) + ((1.0f - v) * 1.0f);
        this.pinchTranslationX = fromTranslateX * v;
        this.pinchTranslationY = fromTranslateY * v;
        this.fragmentView.invalidate();
    }

    /* renamed from: org.telegram.ui.VoIPFragment$10 */
    /* loaded from: classes4.dex */
    public class AnonymousClass10 implements RendererCommon.RendererEvents {
        AnonymousClass10() {
            VoIPFragment.this = this$0;
        }

        /* renamed from: lambda$onFirstFrameRendered$0$org-telegram-ui-VoIPFragment$10 */
        public /* synthetic */ void m4796lambda$onFirstFrameRendered$0$orgtelegramuiVoIPFragment$10() {
            VoIPFragment.this.updateViewState();
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$10$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPFragment.AnonymousClass10.this.m4796lambda$onFirstFrameRendered$0$orgtelegramuiVoIPFragment$10();
                }
            });
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
        }
    }

    private void initRenderers() {
        this.currentUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new AnonymousClass10());
        this.callingUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new AnonymousClass11(), EglBase.CONFIG_PLAIN, new GlRectDrawer());
        this.callingUserMiniTextureRenderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), null);
    }

    /* renamed from: org.telegram.ui.VoIPFragment$11 */
    /* loaded from: classes4.dex */
    public class AnonymousClass11 implements RendererCommon.RendererEvents {
        AnonymousClass11() {
            VoIPFragment.this = this$0;
        }

        /* renamed from: lambda$onFirstFrameRendered$0$org-telegram-ui-VoIPFragment$11 */
        public /* synthetic */ void m4797lambda$onFirstFrameRendered$0$orgtelegramuiVoIPFragment$11() {
            VoIPFragment.this.updateViewState();
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$11$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPFragment.AnonymousClass11.this.m4797lambda$onFirstFrameRendered$0$orgtelegramuiVoIPFragment$11();
                }
            });
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
        }
    }

    public void switchToPip() {
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        if (this.isFinished || !AndroidUtilities.checkInlinePermissions(this.activity) || instance == null) {
            return;
        }
        this.isFinished = true;
        if (VoIPService.getSharedInstance() != null) {
            int h = instance.windowView.getMeasuredHeight();
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                h -= windowInsets2.getSystemWindowInsetBottom();
            }
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), h, 1);
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
            }
        }
        if (VoIPPiPView.getInstance() == null) {
            return;
        }
        this.speakerPhoneIcon.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.backIcon.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.emojiLayout.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.statusLayout.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.buttonsLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.bottomShadow.animate().alpha(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.topShadow.animate().alpha(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.callingUserMiniFloatingLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.notificationsLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        VoIPPiPView.switchingToPip = true;
        this.switchingToPip = true;
        Animator animator = createPiPTransition(false);
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, null);
        animator.addListener(new AnonymousClass12());
        animator.setDuration(350L);
        animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animator.start();
    }

    /* renamed from: org.telegram.ui.VoIPFragment$12 */
    /* loaded from: classes4.dex */
    public class AnonymousClass12 extends AnimatorListenerAdapter {
        AnonymousClass12() {
            VoIPFragment.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            VoIPPiPView.getInstance().windowView.setAlpha(1.0f);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$12$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPFragment.AnonymousClass12.this.m4798lambda$onAnimationEnd$0$orgtelegramuiVoIPFragment$12();
                }
            }, 200L);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-VoIPFragment$12 */
        public /* synthetic */ void m4798lambda$onAnimationEnd$0$orgtelegramuiVoIPFragment$12() {
            NotificationCenter.getInstance(VoIPFragment.this.currentAccount).onAnimationFinish(VoIPFragment.this.animationIndex);
            VoIPPiPView.getInstance().onTransitionEnd();
            VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
            VoIPFragment.this.callingUserTextureView.renderer.release();
            VoIPFragment.this.currentUserTextureView.renderer.release();
            VoIPFragment.this.callingUserMiniTextureRenderer.release();
            VoIPFragment.this.destroy();
            VoIPFragment.this.windowView.finishImmediate();
            VoIPPiPView.switchingToPip = false;
            VoIPFragment.this.switchingToPip = false;
            VoIPFragment unused = VoIPFragment.instance = null;
        }
    }

    public void startTransitionFromPiP() {
        this.enterFromPiP = true;
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.getVideoState(false) == 2) {
            this.callingUserTextureView.setStub(VoIPPiPView.getInstance().callingUserTextureView);
            this.currentUserTextureView.setStub(VoIPPiPView.getInstance().currentUserTextureView);
        }
        this.windowView.setAlpha(0.0f);
        updateViewState();
        this.switchingToPip = true;
        VoIPPiPView.switchingToPip = true;
        VoIPPiPView.prepareForTransition();
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                VoIPFragment.this.m4789lambda$startTransitionFromPiP$14$orgtelegramuiVoIPFragment();
            }
        }, 32L);
    }

    /* renamed from: lambda$startTransitionFromPiP$14$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4789lambda$startTransitionFromPiP$14$orgtelegramuiVoIPFragment() {
        this.windowView.setAlpha(1.0f);
        final Animator animator = createPiPTransition(true);
        this.backIcon.setAlpha(0.0f);
        this.emojiLayout.setAlpha(0.0f);
        this.statusLayout.setAlpha(0.0f);
        this.buttonsLayout.setAlpha(0.0f);
        this.bottomShadow.setAlpha(0.0f);
        this.topShadow.setAlpha(0.0f);
        this.speakerPhoneIcon.setAlpha(0.0f);
        this.notificationsLayout.setAlpha(0.0f);
        this.callingUserPhotoView.setAlpha(0.0f);
        this.currentUserCameraFloatingLayout.switchingToPip = true;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                VoIPFragment.this.m4788lambda$startTransitionFromPiP$13$orgtelegramuiVoIPFragment(animator);
            }
        }, 32L);
    }

    /* renamed from: lambda$startTransitionFromPiP$13$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4788lambda$startTransitionFromPiP$13$orgtelegramuiVoIPFragment(Animator animator) {
        VoIPPiPView.switchingToPip = false;
        VoIPPiPView.finish();
        this.speakerPhoneIcon.animate().setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.backIcon.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.emojiLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.statusLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.buttonsLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.bottomShadow.animate().alpha(1.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.topShadow.animate().alpha(1.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.notificationsLayout.animate().alpha(1.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.callingUserPhotoView.animate().alpha(1.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.13
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationCenter.getInstance(VoIPFragment.this.currentAccount).onAnimationFinish(VoIPFragment.this.animationIndex);
                VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
                VoIPFragment.this.switchingToPip = false;
                VoIPFragment.this.currentUserCameraFloatingLayout.switchingToPip = false;
                VoIPFragment voIPFragment = VoIPFragment.this;
                voIPFragment.previousState = voIPFragment.currentState;
                VoIPFragment.this.updateViewState();
            }
        });
        animator.setDuration(350L);
        animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animator.start();
    }

    public Animator createPiPTransition(boolean enter) {
        boolean animateCamera;
        float cameraToY;
        float cameraToX;
        float cameraToScale;
        float fromCameraAlpha;
        float cameraToX2;
        float cameraToScale2;
        float cameraToY2;
        this.currentUserCameraFloatingLayout.animate().cancel();
        float toX = VoIPPiPView.getInstance().windowLayoutParams.x + VoIPPiPView.getInstance().xOffset;
        float toY = VoIPPiPView.getInstance().windowLayoutParams.y + VoIPPiPView.getInstance().yOffset;
        final float cameraFromX = this.currentUserCameraFloatingLayout.getX();
        final float cameraFromY = this.currentUserCameraFloatingLayout.getY();
        final float cameraFromScale = this.currentUserCameraFloatingLayout.getScaleX();
        boolean animateCamera2 = true;
        final float pipScale = VoIPPiPView.isExpanding() ? 0.4f : 0.25f;
        final float callingUserToX = toX - ((this.callingUserTextureView.getMeasuredWidth() - (this.callingUserTextureView.getMeasuredWidth() * pipScale)) / 2.0f);
        final float callingUserToY = toY - ((this.callingUserTextureView.getMeasuredHeight() - (this.callingUserTextureView.getMeasuredHeight() * pipScale)) / 2.0f);
        if (this.callingUserIsVideo) {
            int currentW = this.currentUserCameraFloatingLayout.getMeasuredWidth();
            if (this.currentUserIsVideo && currentW != 0) {
                cameraToScale2 = (this.windowView.getMeasuredWidth() / currentW) * pipScale * 0.4f;
                cameraToX2 = (((toX - ((this.currentUserCameraFloatingLayout.getMeasuredWidth() - (this.currentUserCameraFloatingLayout.getMeasuredWidth() * cameraToScale2)) / 2.0f)) + (VoIPPiPView.getInstance().parentWidth * pipScale)) - ((VoIPPiPView.getInstance().parentWidth * pipScale) * 0.4f)) - AndroidUtilities.dp(4.0f);
                cameraToY2 = (((toY - ((this.currentUserCameraFloatingLayout.getMeasuredHeight() - (this.currentUserCameraFloatingLayout.getMeasuredHeight() * cameraToScale2)) / 2.0f)) + (VoIPPiPView.getInstance().parentHeight * pipScale)) - ((VoIPPiPView.getInstance().parentHeight * pipScale) * 0.4f)) - AndroidUtilities.dp(4.0f);
            } else {
                cameraToScale2 = 0.0f;
                cameraToX2 = 1.0f;
                cameraToY2 = 1.0f;
                animateCamera2 = false;
            }
            animateCamera = animateCamera2;
            cameraToY = cameraToY2;
            float f = cameraToX2;
            cameraToX = cameraToScale2;
            cameraToScale = f;
        } else {
            animateCamera = true;
            cameraToY = toY - ((this.currentUserCameraFloatingLayout.getMeasuredHeight() - (this.currentUserCameraFloatingLayout.getMeasuredHeight() * pipScale)) / 2.0f);
            cameraToX = pipScale;
            cameraToScale = toX - ((this.currentUserCameraFloatingLayout.getMeasuredWidth() - (this.currentUserCameraFloatingLayout.getMeasuredWidth() * pipScale)) / 2.0f);
        }
        boolean animateCamera3 = this.callingUserIsVideo;
        float f2 = 0.0f;
        final float cameraCornerRadiusFrom = animateCamera3 ? AndroidUtilities.dp(4.0f) : 0.0f;
        final float cameraCornerRadiusTo = (AndroidUtilities.dp(4.0f) * 1.0f) / cameraToX;
        if (!this.callingUserIsVideo) {
            fromCameraAlpha = 1.0f;
        } else {
            fromCameraAlpha = VoIPPiPView.isExpanding() ? 1.0f : 0.0f;
        }
        if (enter) {
            if (animateCamera) {
                this.currentUserCameraFloatingLayout.setScaleX(cameraToX);
                this.currentUserCameraFloatingLayout.setScaleY(cameraToX);
                this.currentUserCameraFloatingLayout.setTranslationX(cameraToScale);
                this.currentUserCameraFloatingLayout.setTranslationY(cameraToY);
                this.currentUserCameraFloatingLayout.setCornerRadius(cameraCornerRadiusTo);
                this.currentUserCameraFloatingLayout.setAlpha(fromCameraAlpha);
            }
            this.callingUserTextureView.setScaleX(pipScale);
            this.callingUserTextureView.setScaleY(pipScale);
            this.callingUserTextureView.setTranslationX(callingUserToX);
            this.callingUserTextureView.setTranslationY(callingUserToY);
            this.callingUserTextureView.setRoundCorners((AndroidUtilities.dp(6.0f) * 1.0f) / pipScale);
            this.callingUserPhotoView.setAlpha(0.0f);
            this.callingUserPhotoView.setScaleX(pipScale);
            this.callingUserPhotoView.setScaleY(pipScale);
            this.callingUserPhotoView.setTranslationX(callingUserToX);
            this.callingUserPhotoView.setTranslationY(callingUserToY);
        }
        float[] fArr = new float[2];
        fArr[0] = enter ? 1.0f : 0.0f;
        fArr[1] = enter ? 0.0f : 1.0f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr);
        if (!enter) {
            f2 = 1.0f;
        }
        this.enterTransitionProgress = f2;
        updateSystemBarColors();
        final boolean finalAnimateCamera = animateCamera;
        final float finalFromCameraAlpha = fromCameraAlpha;
        final float cameraCornerRadiusTo2 = cameraToX;
        final float cameraToX3 = cameraToScale;
        final float cameraToY3 = cameraToY;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda24
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPFragment.this.m4767lambda$createPiPTransition$15$orgtelegramuiVoIPFragment(finalAnimateCamera, cameraFromScale, cameraCornerRadiusTo2, cameraFromX, cameraToX3, cameraFromY, cameraToY3, cameraCornerRadiusFrom, cameraCornerRadiusTo, r13, finalFromCameraAlpha, r15, pipScale, r17, callingUserToX, r19, callingUserToY, valueAnimator);
            }
        });
        return animator;
    }

    /* renamed from: lambda$createPiPTransition$15$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4767lambda$createPiPTransition$15$orgtelegramuiVoIPFragment(boolean finalAnimateCamera, float cameraFromScale, float cameraToScale, float cameraFromX, float cameraToX, float cameraFromY, float cameraToY, float cameraCornerRadiusFrom, float cameraCornerRadiusTo, float toCameraAlpha, float finalFromCameraAlpha, float callingUserFromScale, float callingUserToScale, float callingUserFromX, float callingUserToX, float callingUserFromY, float callingUserToY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.enterTransitionProgress = 1.0f - v;
        updateSystemBarColors();
        if (finalAnimateCamera) {
            float cameraScale = ((1.0f - v) * cameraFromScale) + (cameraToScale * v);
            this.currentUserCameraFloatingLayout.setScaleX(cameraScale);
            this.currentUserCameraFloatingLayout.setScaleY(cameraScale);
            this.currentUserCameraFloatingLayout.setTranslationX(((1.0f - v) * cameraFromX) + (cameraToX * v));
            this.currentUserCameraFloatingLayout.setTranslationY(((1.0f - v) * cameraFromY) + (cameraToY * v));
            this.currentUserCameraFloatingLayout.setCornerRadius(((1.0f - v) * cameraCornerRadiusFrom) + (cameraCornerRadiusTo * v));
            this.currentUserCameraFloatingLayout.setAlpha(((1.0f - v) * toCameraAlpha) + (finalFromCameraAlpha * v));
        }
        float callingUserScale = ((1.0f - v) * callingUserFromScale) + (callingUserToScale * v);
        this.callingUserTextureView.setScaleX(callingUserScale);
        this.callingUserTextureView.setScaleY(callingUserScale);
        float tx = ((1.0f - v) * callingUserFromX) + (callingUserToX * v);
        float ty = ((1.0f - v) * callingUserFromY) + (callingUserToY * v);
        this.callingUserTextureView.setTranslationX(tx);
        this.callingUserTextureView.setTranslationY(ty);
        this.callingUserTextureView.setRoundCorners(((AndroidUtilities.dp(4.0f) * v) * 1.0f) / callingUserScale);
        if (!this.currentUserCameraFloatingLayout.measuredAsFloatingMode) {
            this.currentUserTextureView.setScreenshareMiniProgress(v, false);
        }
        this.callingUserPhotoView.setScaleX(callingUserScale);
        this.callingUserPhotoView.setScaleY(callingUserScale);
        this.callingUserPhotoView.setTranslationX(tx);
        this.callingUserPhotoView.setTranslationY(ty);
        this.callingUserPhotoView.setAlpha(1.0f - v);
    }

    public void expandEmoji(boolean expanded) {
        if (!this.emojiLoaded || this.emojiExpanded == expanded || !this.uiVisible) {
            return;
        }
        this.emojiExpanded = expanded;
        if (expanded) {
            AndroidUtilities.runOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            float s1 = this.emojiLayout.getMeasuredWidth();
            float s2 = this.windowView.getMeasuredWidth() - AndroidUtilities.dp(128.0f);
            float scale = s2 / s1;
            this.emojiLayout.animate().scaleX(scale).scaleY(scale).translationY((this.windowView.getHeight() / 2.0f) - this.emojiLayout.getBottom()).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(250L).start();
            this.emojiRationalTextView.animate().setListener(null).cancel();
            if (this.emojiRationalTextView.getVisibility() != 0) {
                this.emojiRationalTextView.setVisibility(0);
                this.emojiRationalTextView.setAlpha(0.0f);
            }
            this.emojiRationalTextView.animate().alpha(1.0f).setDuration(150L).start();
            this.overlayBackground.animate().setListener(null).cancel();
            if (this.overlayBackground.getVisibility() != 0) {
                this.overlayBackground.setVisibility(0);
                this.overlayBackground.setAlpha(0.0f);
                this.overlayBackground.setShowBlackout(this.currentUserIsVideo || this.callingUserIsVideo, false);
            }
            this.overlayBackground.animate().alpha(1.0f).setDuration(150L).start();
            return;
        }
        this.emojiLayout.animate().scaleX(1.0f).scaleY(1.0f).translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150L).start();
        if (this.emojiRationalTextView.getVisibility() != 8) {
            this.emojiRationalTextView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.14
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPService service = VoIPService.getSharedInstance();
                    if (VoIPFragment.this.canHideUI && !VoIPFragment.this.hideUiRunnableWaiting && service != null && !service.isMicMute()) {
                        AndroidUtilities.runOnUIThread(VoIPFragment.this.hideUIRunnable, 3000L);
                        VoIPFragment.this.hideUiRunnableWaiting = true;
                    }
                    VoIPFragment.this.emojiRationalTextView.setVisibility(8);
                }
            }).setDuration(150L).start();
            this.overlayBackground.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.15
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPFragment.this.overlayBackground.setVisibility(8);
                }
            }).setDuration(150L).start();
        }
    }

    public void updateViewState() {
        boolean animated;
        int i;
        float f;
        if (!this.isFinished && !this.switchingToPip) {
            this.lockOnScreen = false;
            boolean animated2 = this.previousState != -1;
            boolean showAcceptDeclineView = false;
            boolean showTimer = false;
            boolean showReconnecting = false;
            boolean showCallingAvatarMini = false;
            int statusLayoutOffset = 0;
            VoIPService service = VoIPService.getSharedInstance();
            switch (this.currentState) {
                case 1:
                case 2:
                    animated = animated2;
                    this.statusTextView.setText(LocaleController.getString("VoipConnecting", R.string.VoipConnecting), true, animated);
                    break;
                case 3:
                case 5:
                    animated = animated2;
                    updateKeyView(animated);
                    showTimer = true;
                    if (this.currentState == 5) {
                        showReconnecting = true;
                        break;
                    }
                    break;
                case 4:
                    this.statusTextView.setText(LocaleController.getString("VoipFailed", R.string.VoipFailed), false, animated2);
                    VoIPService voipService = VoIPService.getSharedInstance();
                    String lastError = voipService != null ? voipService.getLastError() : Instance.ERROR_UNKNOWN;
                    boolean animated3 = animated2;
                    if (TextUtils.equals(lastError, Instance.ERROR_UNKNOWN)) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda19
                            @Override // java.lang.Runnable
                            public final void run() {
                                VoIPFragment.this.m4795lambda$updateViewState$21$orgtelegramuiVoIPFragment();
                            }
                        }, 1000L);
                        animated = animated3;
                        break;
                    } else if (TextUtils.equals(lastError, Instance.ERROR_INCOMPATIBLE)) {
                        String name = ContactsController.formatName(this.callingUser.first_name, this.callingUser.last_name);
                        String message = LocaleController.formatString("VoipPeerIncompatible", R.string.VoipPeerIncompatible, name);
                        showErrorDialog(AndroidUtilities.replaceTags(message));
                        animated = animated3;
                        break;
                    } else if (!TextUtils.equals(lastError, Instance.ERROR_PEER_OUTDATED)) {
                        if (TextUtils.equals(lastError, Instance.ERROR_PRIVACY)) {
                            String name2 = ContactsController.formatName(this.callingUser.first_name, this.callingUser.last_name);
                            String message2 = LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, name2);
                            showErrorDialog(AndroidUtilities.replaceTags(message2));
                            animated = animated3;
                            break;
                        } else if (TextUtils.equals(lastError, Instance.ERROR_AUDIO_IO)) {
                            showErrorDialog("Error initializing audio hardware");
                            animated = animated3;
                            break;
                        } else if (!TextUtils.equals(lastError, Instance.ERROR_LOCALIZED)) {
                            if (TextUtils.equals(lastError, Instance.ERROR_CONNECTION_SERVICE)) {
                                showErrorDialog(LocaleController.getString("VoipErrorUnknown", R.string.VoipErrorUnknown));
                                animated = animated3;
                                break;
                            } else {
                                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda18
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        VoIPFragment.this.m4794lambda$updateViewState$20$orgtelegramuiVoIPFragment();
                                    }
                                }, 1000L);
                                animated = animated3;
                                break;
                            }
                        } else {
                            this.windowView.finish();
                            animated = animated3;
                            break;
                        }
                    } else if (this.isVideoCall) {
                        String name3 = UserObject.getFirstName(this.callingUser);
                        String message3 = LocaleController.formatString("VoipPeerVideoOutdated", R.string.VoipPeerVideoOutdated, name3);
                        final boolean[] callAgain = new boolean[1];
                        AlertDialog dlg = new DarkAlertDialog.Builder(this.activity).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(AndroidUtilities.replaceTags(message3)).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda26
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                VoIPFragment.this.m4791lambda$updateViewState$17$orgtelegramuiVoIPFragment(dialogInterface, i2);
                            }
                        }).setPositiveButton(LocaleController.getString("VoipPeerVideoOutdatedMakeVoice", R.string.VoipPeerVideoOutdatedMakeVoice), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda28
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                VoIPFragment.this.m4792lambda$updateViewState$18$orgtelegramuiVoIPFragment(callAgain, dialogInterface, i2);
                            }
                        }).show();
                        dlg.setCanceledOnTouchOutside(true);
                        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda30
                            @Override // android.content.DialogInterface.OnDismissListener
                            public final void onDismiss(DialogInterface dialogInterface) {
                                VoIPFragment.this.m4793lambda$updateViewState$19$orgtelegramuiVoIPFragment(callAgain, dialogInterface);
                            }
                        });
                        animated = animated3;
                        break;
                    } else {
                        String name4 = UserObject.getFirstName(this.callingUser);
                        String message4 = LocaleController.formatString("VoipPeerOutdated", R.string.VoipPeerOutdated, name4);
                        showErrorDialog(AndroidUtilities.replaceTags(message4));
                        animated = animated3;
                        break;
                    }
                case 6:
                case 7:
                case 8:
                case 9:
                default:
                    animated = animated2;
                    break;
                case 10:
                    animated = animated2;
                    break;
                case 11:
                    this.currentUserTextureView.saveCameraLastBitmap();
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda17
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPFragment.this.m4790lambda$updateViewState$16$orgtelegramuiVoIPFragment();
                        }
                    }, 200L);
                    animated = animated2;
                    break;
                case 12:
                    this.statusTextView.setText(LocaleController.getString("VoipExchangingKeys", R.string.VoipExchangingKeys), true, animated2);
                    animated = animated2;
                    break;
                case 13:
                    this.statusTextView.setText(LocaleController.getString("VoipWaiting", R.string.VoipWaiting), true, animated2);
                    animated = animated2;
                    break;
                case 14:
                    this.statusTextView.setText(LocaleController.getString("VoipRequesting", R.string.VoipRequesting), true, animated2);
                    animated = animated2;
                    break;
                case 15:
                    showAcceptDeclineView = true;
                    this.lockOnScreen = true;
                    statusLayoutOffset = AndroidUtilities.dp(24.0f);
                    this.acceptDeclineView.setRetryMod(false);
                    if (service != null && service.privateCall.video) {
                        if (this.currentUserIsVideo && this.callingUser.photo != null) {
                            showCallingAvatarMini = true;
                        } else {
                            showCallingAvatarMini = false;
                        }
                        this.statusTextView.setText(LocaleController.getString("VoipInVideoCallBranding", R.string.VoipInVideoCallBranding), true, animated2);
                        this.acceptDeclineView.setTranslationY(-AndroidUtilities.dp(60.0f));
                        animated = animated2;
                        break;
                    } else {
                        this.statusTextView.setText(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding), true, animated2);
                        this.acceptDeclineView.setTranslationY(0.0f);
                        animated = animated2;
                        break;
                    }
                    break;
                case 16:
                    this.statusTextView.setText(LocaleController.getString("VoipRinging", R.string.VoipRinging), true, animated2);
                    animated = animated2;
                    break;
                case 17:
                    showAcceptDeclineView = true;
                    this.statusTextView.setText(LocaleController.getString("VoipBusy", R.string.VoipBusy), false, animated2);
                    this.acceptDeclineView.setRetryMod(true);
                    this.currentUserIsVideo = false;
                    this.callingUserIsVideo = false;
                    animated = animated2;
                    break;
            }
            if (this.previewDialog != null) {
                return;
            }
            if (service != null) {
                this.callingUserIsVideo = service.getRemoteVideoState() == 2;
                boolean z = service.getVideoState(false) == 2 || service.getVideoState(false) == 1;
                this.currentUserIsVideo = z;
                if (z && !this.isVideoCall) {
                    this.isVideoCall = true;
                }
            }
            if (animated) {
                this.currentUserCameraFloatingLayout.saveRelativePosition();
                this.callingUserMiniFloatingLayout.saveRelativePosition();
            }
            if (this.callingUserIsVideo) {
                if (!this.switchingToPip) {
                    this.callingUserPhotoView.setAlpha(1.0f);
                }
                if (animated) {
                    this.callingUserTextureView.animate().alpha(1.0f).setDuration(250L).start();
                } else {
                    this.callingUserTextureView.animate().cancel();
                    this.callingUserTextureView.setAlpha(1.0f);
                }
                if (!this.callingUserTextureView.renderer.isFirstFrameRendered() && !this.enterFromPiP) {
                    this.callingUserIsVideo = false;
                }
            }
            if (this.currentUserIsVideo || this.callingUserIsVideo) {
                fillNavigationBar(true, animated);
            } else {
                fillNavigationBar(false, animated);
                this.callingUserPhotoView.setVisibility(0);
                if (!animated) {
                    this.callingUserTextureView.animate().cancel();
                    this.callingUserTextureView.setAlpha(0.0f);
                } else {
                    this.callingUserTextureView.animate().alpha(0.0f).setDuration(250L).start();
                }
            }
            boolean z2 = this.currentUserIsVideo;
            if (!z2 || !this.callingUserIsVideo) {
                this.cameraForceExpanded = false;
            }
            boolean showCallingUserVideoMini = z2 && this.cameraForceExpanded;
            showCallingUserAvatarMini(showCallingAvatarMini, animated);
            int statusLayoutOffset2 = statusLayoutOffset + (this.callingUserPhotoViewMini.getTag() == null ? 0 : AndroidUtilities.dp(135.0f) + AndroidUtilities.dp(12.0f));
            showAcceptDeclineView(showAcceptDeclineView, animated);
            this.windowView.setLockOnScreen(this.lockOnScreen || this.deviceIsLocked);
            boolean z3 = this.currentState == 3 && (this.currentUserIsVideo || this.callingUserIsVideo);
            this.canHideUI = z3;
            if (!z3 && !this.uiVisible) {
                showUi(true);
            }
            if (this.uiVisible && this.canHideUI && !this.hideUiRunnableWaiting && service != null && !service.isMicMute()) {
                AndroidUtilities.runOnUIThread(this.hideUIRunnable, 3000L);
                this.hideUiRunnableWaiting = true;
            } else if (service != null && service.isMicMute()) {
                AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
                this.hideUiRunnableWaiting = false;
            }
            if (!this.uiVisible) {
                statusLayoutOffset2 -= AndroidUtilities.dp(50.0f);
            }
            if (animated) {
                if (this.lockOnScreen || !this.uiVisible) {
                    if (this.backIcon.getVisibility() == 0) {
                        f = 0.0f;
                    } else {
                        this.backIcon.setVisibility(0);
                        f = 0.0f;
                        this.backIcon.setAlpha(0.0f);
                    }
                    this.backIcon.animate().alpha(f).start();
                } else {
                    this.backIcon.animate().alpha(1.0f).start();
                }
                this.notificationsLayout.animate().translationY((-AndroidUtilities.dp(16.0f)) - (this.uiVisible ? AndroidUtilities.dp(80.0f) : 0)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            } else {
                if (!this.lockOnScreen) {
                    this.backIcon.setVisibility(0);
                }
                this.backIcon.setAlpha(this.lockOnScreen ? 0.0f : 1.0f);
                this.notificationsLayout.setTranslationY((-AndroidUtilities.dp(16.0f)) - (this.uiVisible ? AndroidUtilities.dp(80.0f) : 0));
            }
            int i2 = this.currentState;
            if (i2 != 10 && i2 != 11) {
                updateButtons(animated);
            }
            if (showTimer) {
                this.statusTextView.showTimer(animated);
            }
            this.statusTextView.showReconnect(showReconnecting, animated);
            if (animated) {
                if (statusLayoutOffset2 != this.statusLayoutAnimateToOffset) {
                    this.statusLayout.animate().translationY(statusLayoutOffset2).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
            } else {
                this.statusLayout.setTranslationY(statusLayoutOffset2);
            }
            this.statusLayoutAnimateToOffset = statusLayoutOffset2;
            this.overlayBackground.setShowBlackout(this.currentUserIsVideo || this.callingUserIsVideo, animated);
            int i3 = this.currentState;
            this.canSwitchToPip = (i3 == 11 || i3 == 17 || (!this.currentUserIsVideo && !this.callingUserIsVideo)) ? false : true;
            if (service != null) {
                if (this.currentUserIsVideo) {
                    service.sharedUIParams.tapToVideoTooltipWasShowed = true;
                }
                this.currentUserTextureView.setIsScreencast(service.isScreencast());
                this.currentUserTextureView.renderer.setMirror(service.isFrontFaceCamera());
                service.setSinks((!this.currentUserIsVideo || service.isScreencast()) ? null : this.currentUserTextureView.renderer, showCallingUserVideoMini ? this.callingUserMiniTextureRenderer : this.callingUserTextureView.renderer);
                if (animated) {
                    this.notificationsLayout.beforeLayoutChanges();
                }
                if ((this.currentUserIsVideo || this.callingUserIsVideo) && (((i = this.currentState) == 3 || i == 5) && service.getCallDuration() > 500)) {
                    if (service.getRemoteAudioState() == 0) {
                        this.notificationsLayout.addNotification(R.drawable.calls_mute_mini, LocaleController.formatString("VoipUserMicrophoneIsOff", R.string.VoipUserMicrophoneIsOff, UserObject.getFirstName(this.callingUser)), "muted", animated);
                    } else {
                        this.notificationsLayout.removeNotification("muted");
                    }
                    if (service.getRemoteVideoState() == 0) {
                        this.notificationsLayout.addNotification(R.drawable.calls_camera_mini, LocaleController.formatString("VoipUserCameraIsOff", R.string.VoipUserCameraIsOff, UserObject.getFirstName(this.callingUser)), "video", animated);
                    } else {
                        this.notificationsLayout.removeNotification("video");
                    }
                } else {
                    if (service.getRemoteAudioState() == 0) {
                        this.notificationsLayout.addNotification(R.drawable.calls_mute_mini, LocaleController.formatString("VoipUserMicrophoneIsOff", R.string.VoipUserMicrophoneIsOff, UserObject.getFirstName(this.callingUser)), "muted", animated);
                    } else {
                        this.notificationsLayout.removeNotification("muted");
                    }
                    this.notificationsLayout.removeNotification("video");
                }
                if (this.notificationsLayout.getChildCount() == 0 && this.callingUserIsVideo && service.privateCall != null && !service.privateCall.video && !service.sharedUIParams.tapToVideoTooltipWasShowed) {
                    service.sharedUIParams.tapToVideoTooltipWasShowed = true;
                    this.tapToVideoTooltip.showForView(this.bottomButtons[1], true);
                } else if (this.notificationsLayout.getChildCount() != 0) {
                    this.tapToVideoTooltip.hide();
                }
                if (animated) {
                    this.notificationsLayout.animateLayoutChanges();
                }
            }
            int floatingViewsOffset = this.notificationsLayout.getChildsHight();
            this.callingUserMiniFloatingLayout.setBottomOffset(floatingViewsOffset, animated);
            this.currentUserCameraFloatingLayout.setBottomOffset(floatingViewsOffset, animated);
            this.currentUserCameraFloatingLayout.setUiVisible(this.uiVisible);
            this.callingUserMiniFloatingLayout.setUiVisible(this.uiVisible);
            if (!this.currentUserIsVideo) {
                showFloatingLayout(0, animated);
            } else if (!this.callingUserIsVideo || this.cameraForceExpanded) {
                showFloatingLayout(1, animated);
            } else {
                showFloatingLayout(2, animated);
            }
            if (showCallingUserVideoMini && this.callingUserMiniFloatingLayout.getTag() == null) {
                this.callingUserMiniFloatingLayout.setIsActive(true);
                if (this.callingUserMiniFloatingLayout.getVisibility() != 0) {
                    this.callingUserMiniFloatingLayout.setVisibility(0);
                    this.callingUserMiniFloatingLayout.setAlpha(0.0f);
                    this.callingUserMiniFloatingLayout.setScaleX(0.5f);
                    this.callingUserMiniFloatingLayout.setScaleY(0.5f);
                }
                this.callingUserMiniFloatingLayout.animate().setListener(null).cancel();
                this.callingUserMiniFloatingLayout.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).setStartDelay(150L).start();
                this.callingUserMiniFloatingLayout.setTag(1);
            } else if (!showCallingUserVideoMini && this.callingUserMiniFloatingLayout.getTag() != null) {
                this.callingUserMiniFloatingLayout.setIsActive(false);
                this.callingUserMiniFloatingLayout.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.16
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (VoIPFragment.this.callingUserMiniFloatingLayout.getTag() == null) {
                            VoIPFragment.this.callingUserMiniFloatingLayout.setVisibility(8);
                        }
                    }
                }).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                this.callingUserMiniFloatingLayout.setTag(null);
            }
            this.currentUserCameraFloatingLayout.restoreRelativePosition();
            this.callingUserMiniFloatingLayout.restoreRelativePosition();
            updateSpeakerPhoneIcon();
        }
    }

    /* renamed from: lambda$updateViewState$16$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4790lambda$updateViewState$16$orgtelegramuiVoIPFragment() {
        this.windowView.finish();
    }

    /* renamed from: lambda$updateViewState$17$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4791lambda$updateViewState$17$orgtelegramuiVoIPFragment(DialogInterface dialogInterface, int i) {
        this.windowView.finish();
    }

    /* renamed from: lambda$updateViewState$18$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4792lambda$updateViewState$18$orgtelegramuiVoIPFragment(boolean[] callAgain, DialogInterface dialogInterface, int i) {
        callAgain[0] = true;
        this.currentState = 17;
        Intent intent = new Intent(this.activity, VoIPService.class);
        intent.putExtra("user_id", this.callingUser.id);
        intent.putExtra("is_outgoing", true);
        intent.putExtra("start_incall_activity", false);
        intent.putExtra("video_call", false);
        intent.putExtra("can_video_call", false);
        intent.putExtra("account", this.currentAccount);
        try {
            this.activity.startService(intent);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$updateViewState$19$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4793lambda$updateViewState$19$orgtelegramuiVoIPFragment(boolean[] callAgain, DialogInterface dialog) {
        if (!callAgain[0]) {
            this.windowView.finish();
        }
    }

    /* renamed from: lambda$updateViewState$20$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4794lambda$updateViewState$20$orgtelegramuiVoIPFragment() {
        this.windowView.finish();
    }

    /* renamed from: lambda$updateViewState$21$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4795lambda$updateViewState$21$orgtelegramuiVoIPFragment() {
        this.windowView.finish();
    }

    private void fillNavigationBar(boolean fill, boolean animated) {
        if (this.switchingToPip) {
            return;
        }
        float f = 0.0f;
        float f2 = 1.0f;
        if (!animated) {
            ValueAnimator valueAnimator = this.naviagtionBarAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (fill) {
                f = 1.0f;
            }
            this.fillNaviagtionBarValue = f;
            Paint paint = this.overlayBottomPaint;
            if (!fill) {
                f2 = 0.5f;
            }
            paint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (f2 * 255.0f)));
        } else if (fill != this.fillNaviagtionBar) {
            ValueAnimator valueAnimator2 = this.naviagtionBarAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.fillNaviagtionBarValue;
            if (fill) {
                f = 1.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.naviagtionBarAnimator = ofFloat;
            ofFloat.addUpdateListener(this.navigationBarAnimationListener);
            this.naviagtionBarAnimator.setDuration(300L);
            this.naviagtionBarAnimator.setInterpolator(new LinearInterpolator());
            this.naviagtionBarAnimator.start();
        }
        this.fillNaviagtionBar = fill;
    }

    public void showUi(boolean show) {
        ValueAnimator valueAnimator = this.uiVisibilityAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int i = 0;
        if (!show && this.uiVisible) {
            this.speakerPhoneIcon.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(50.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.backIcon.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(50.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.emojiLayout.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(50.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.statusLayout.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.buttonsLayout.animate().alpha(0.0f).translationY(AndroidUtilities.dp(50.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.bottomShadow.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.topShadow.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.uiVisibilityAlpha, 0.0f);
            this.uiVisibilityAnimator = ofFloat;
            ofFloat.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.uiVisibilityAnimator.start();
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.buttonsLayout.setEnabled(false);
        } else if (show && !this.uiVisible) {
            this.tapToVideoTooltip.hide();
            this.speakerPhoneIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.backIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.emojiLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.statusLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.buttonsLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.bottomShadow.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.topShadow.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.uiVisibilityAlpha, 1.0f);
            this.uiVisibilityAnimator = ofFloat2;
            ofFloat2.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.uiVisibilityAnimator.start();
            this.buttonsLayout.setEnabled(true);
        }
        this.uiVisible = show;
        this.windowView.requestFullscreen(!show);
        ViewPropertyAnimator animate = this.notificationsLayout.animate();
        int i2 = -AndroidUtilities.dp(16.0f);
        if (this.uiVisible) {
            i = AndroidUtilities.dp(80.0f);
        }
        animate.translationY(i2 - i).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    private void showFloatingLayout(int state, boolean animated) {
        Animator animator;
        if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
            this.currentUserCameraFloatingLayout.setUiVisible(this.uiVisible);
        }
        if (!animated && (animator = this.cameraShowingAnimator) != null) {
            animator.removeAllListeners();
            this.cameraShowingAnimator.cancel();
        }
        boolean z = true;
        if (state == 0) {
            if (!animated) {
                this.currentUserCameraFloatingLayout.setVisibility(8);
            } else if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 0) {
                Animator animator2 = this.cameraShowingAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.cameraShowingAnimator.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.ALPHA, this.currentUserCameraFloatingLayout.getAlpha(), 0.0f));
                if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 2) {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_X, this.currentUserCameraFloatingLayout.getScaleX(), 0.7f), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_Y, this.currentUserCameraFloatingLayout.getScaleX(), 0.7f));
                }
                this.cameraShowingAnimator = animatorSet;
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.17
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        VoIPFragment.this.currentUserCameraFloatingLayout.setTranslationX(0.0f);
                        VoIPFragment.this.currentUserCameraFloatingLayout.setTranslationY(0.0f);
                        VoIPFragment.this.currentUserCameraFloatingLayout.setScaleY(1.0f);
                        VoIPFragment.this.currentUserCameraFloatingLayout.setScaleX(1.0f);
                        VoIPFragment.this.currentUserCameraFloatingLayout.setVisibility(8);
                    }
                });
                this.cameraShowingAnimator.setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.cameraShowingAnimator.setStartDelay(50L);
                this.cameraShowingAnimator.start();
            }
        } else {
            boolean switchToFloatAnimated = animated;
            if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) {
                switchToFloatAnimated = false;
            }
            if (animated) {
                if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) {
                    if (this.currentUserCameraFloatingLayout.getVisibility() == 8) {
                        this.currentUserCameraFloatingLayout.setAlpha(0.0f);
                        this.currentUserCameraFloatingLayout.setScaleX(0.7f);
                        this.currentUserCameraFloatingLayout.setScaleY(0.7f);
                        this.currentUserCameraFloatingLayout.setVisibility(0);
                    }
                    Animator animator3 = this.cameraShowingAnimator;
                    if (animator3 != null) {
                        animator3.removeAllListeners();
                        this.cameraShowingAnimator.cancel();
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_X, 0.7f, 1.0f), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, View.SCALE_Y, 0.7f, 1.0f));
                    this.cameraShowingAnimator = animatorSet2;
                    animatorSet2.setDuration(150L).start();
                }
            } else {
                this.currentUserCameraFloatingLayout.setVisibility(0);
            }
            if ((this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) && this.currentUserCameraFloatingLayout.relativePositionToSetX < 0.0f) {
                this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
                this.currentUserCameraIsFullscreen = true;
            }
            this.currentUserCameraFloatingLayout.setFloatingMode(state == 2, switchToFloatAnimated);
            if (state == 2) {
                z = false;
            }
            this.currentUserCameraIsFullscreen = z;
        }
        this.currentUserCameraFloatingLayout.setTag(Integer.valueOf(state));
    }

    private void showCallingUserAvatarMini(boolean show, boolean animated) {
        int i = 0;
        Integer num = null;
        if (!animated) {
            this.callingUserPhotoViewMini.animate().setListener(null).cancel();
            this.callingUserPhotoViewMini.setTranslationY(0.0f);
            this.callingUserPhotoViewMini.setAlpha(1.0f);
            BackupImageView backupImageView = this.callingUserPhotoViewMini;
            if (!show) {
                i = 8;
            }
            backupImageView.setVisibility(i);
        } else if (show && this.callingUserPhotoViewMini.getTag() == null) {
            this.callingUserPhotoViewMini.animate().setListener(null).cancel();
            this.callingUserPhotoViewMini.setVisibility(0);
            this.callingUserPhotoViewMini.setAlpha(0.0f);
            this.callingUserPhotoViewMini.setTranslationY(-AndroidUtilities.dp(135.0f));
            this.callingUserPhotoViewMini.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        } else if (!show && this.callingUserPhotoViewMini.getTag() != null) {
            this.callingUserPhotoViewMini.animate().setListener(null).cancel();
            this.callingUserPhotoViewMini.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(135.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.18
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPFragment.this.callingUserPhotoViewMini.setVisibility(8);
                }
            }).start();
        }
        BackupImageView backupImageView2 = this.callingUserPhotoViewMini;
        if (show) {
            num = 1;
        }
        backupImageView2.setTag(num);
    }

    private void updateKeyView(boolean animated) {
        VoIPService service;
        if (this.emojiLoaded || (service = VoIPService.getSharedInstance()) == null) {
            return;
        }
        byte[] auth_key = null;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            buf.write(service.getEncryptionKey());
            buf.write(service.getGA());
            auth_key = buf.toByteArray();
        } catch (Exception checkedExceptionsAreBad) {
            FileLog.e((Throwable) checkedExceptionsAreBad, false);
        }
        if (auth_key != null) {
            byte[] sha256 = Utilities.computeSHA256(auth_key, 0, auth_key.length);
            String[] emoji = EncryptionKeyEmojifier.emojifyForCall(sha256);
            for (int i = 0; i < 4; i++) {
                Emoji.preloadEmoji(emoji[i]);
                Emoji.EmojiDrawable drawable = Emoji.getEmojiDrawable(emoji[i]);
                if (drawable != null) {
                    drawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                    drawable.preload();
                    this.emojiViews[i].setImageDrawable(drawable);
                    this.emojiViews[i].setContentDescription(emoji[i]);
                    this.emojiViews[i].setVisibility(8);
                }
                this.emojiDrawables[i] = drawable;
            }
            checkEmojiLoaded(animated);
        }
    }

    private void checkEmojiLoaded(boolean animated) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            Emoji.EmojiDrawable[] emojiDrawableArr = this.emojiDrawables;
            if (emojiDrawableArr[i] != null && emojiDrawableArr[i].isLoaded()) {
                count++;
            }
        }
        if (count == 4) {
            this.emojiLoaded = true;
            for (int i2 = 0; i2 < 4; i2++) {
                if (this.emojiViews[i2].getVisibility() != 0) {
                    this.emojiViews[i2].setVisibility(0);
                    if (animated) {
                        this.emojiViews[i2].setAlpha(0.0f);
                        this.emojiViews[i2].setTranslationY(AndroidUtilities.dp(30.0f));
                        this.emojiViews[i2].animate().alpha(1.0f).translationY(0.0f).setDuration(200L).setStartDelay(i2 * 20).start();
                    }
                }
            }
        }
    }

    private void showAcceptDeclineView(boolean show, boolean animated) {
        int i = 0;
        Integer num = null;
        if (!animated) {
            AcceptDeclineView acceptDeclineView = this.acceptDeclineView;
            if (!show) {
                i = 8;
            }
            acceptDeclineView.setVisibility(i);
        } else {
            if (show && this.acceptDeclineView.getTag() == null) {
                this.acceptDeclineView.animate().setListener(null).cancel();
                if (this.acceptDeclineView.getVisibility() == 8) {
                    this.acceptDeclineView.setVisibility(0);
                    this.acceptDeclineView.setAlpha(0.0f);
                }
                this.acceptDeclineView.animate().alpha(1.0f);
            }
            if (!show && this.acceptDeclineView.getTag() != null) {
                this.acceptDeclineView.animate().setListener(null).cancel();
                this.acceptDeclineView.animate().setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoIPFragment.19
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        VoIPFragment.this.acceptDeclineView.setVisibility(8);
                    }
                }).alpha(0.0f);
            }
        }
        this.acceptDeclineView.setEnabled(show);
        AcceptDeclineView acceptDeclineView2 = this.acceptDeclineView;
        if (show) {
            num = 1;
        }
        acceptDeclineView2.setTag(num);
    }

    private void updateButtons(boolean animated) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            return;
        }
        if (animated && Build.VERSION.SDK_INT >= 19) {
            TransitionSet transitionSet = new TransitionSet();
            Visibility visibility = new Visibility() { // from class: org.telegram.ui.VoIPFragment.20
                @Override // android.transition.Visibility
                public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, AndroidUtilities.dp(100.0f), 0.0f);
                    if (view instanceof VoIPToggleButton) {
                        view.setTranslationY(AndroidUtilities.dp(100.0f));
                        animator.setStartDelay(((VoIPToggleButton) view).animationDelay);
                    }
                    return animator;
                }

                @Override // android.transition.Visibility
                public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), AndroidUtilities.dp(100.0f));
                }
            };
            transitionSet.addTransition(visibility.setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT)).addTransition(new ChangeBounds().setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT));
            transitionSet.excludeChildren(VoIPToggleButton.class, true);
            TransitionManager.beginDelayedTransition(this.buttonsLayout, transitionSet);
        }
        int i = this.currentState;
        if (i == 15 || i == 17) {
            if (service.privateCall != null && service.privateCall.video && this.currentState == 15) {
                if (!service.isScreencast() && (this.currentUserIsVideo || this.callingUserIsVideo)) {
                    setFrontalCameraAction(this.bottomButtons[0], service, animated);
                    if (this.uiVisible) {
                        this.speakerPhoneIcon.animate().alpha(1.0f).start();
                    }
                } else {
                    setSpeakerPhoneAction(this.bottomButtons[0], service, animated);
                    this.speakerPhoneIcon.animate().alpha(0.0f).start();
                }
                setVideoAction(this.bottomButtons[1], service, animated);
                setMicrohoneAction(this.bottomButtons[2], service, animated);
            } else {
                this.bottomButtons[0].setVisibility(8);
                this.bottomButtons[1].setVisibility(8);
                this.bottomButtons[2].setVisibility(8);
            }
            this.bottomButtons[3].setVisibility(8);
        } else if (instance == null) {
            return;
        } else {
            if (!service.isScreencast() && (this.currentUserIsVideo || this.callingUserIsVideo)) {
                setFrontalCameraAction(this.bottomButtons[0], service, animated);
                if (this.uiVisible) {
                    this.speakerPhoneIcon.setTag(1);
                    this.speakerPhoneIcon.animate().alpha(1.0f).start();
                }
            } else {
                setSpeakerPhoneAction(this.bottomButtons[0], service, animated);
                this.speakerPhoneIcon.setTag(null);
                this.speakerPhoneIcon.animate().alpha(0.0f).start();
            }
            setVideoAction(this.bottomButtons[1], service, animated);
            setMicrohoneAction(this.bottomButtons[2], service, animated);
            this.bottomButtons[3].setData(R.drawable.calls_decline, -1, -1041108, LocaleController.getString("VoipEndCall", R.string.VoipEndCall), false, animated);
            this.bottomButtons[3].setOnClickListener(VoIPFragment$$ExternalSyntheticLambda12.INSTANCE);
        }
        int animationDelay = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if (this.bottomButtons[i2].getVisibility() == 0) {
                this.bottomButtons[i2].animationDelay = animationDelay;
                animationDelay += 16;
            }
        }
        updateSpeakerPhoneIcon();
    }

    public static /* synthetic */ void lambda$updateButtons$22(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp();
        }
    }

    private void setMicrohoneAction(VoIPToggleButton bottomButton, VoIPService service, boolean animated) {
        if (service.isMicMute()) {
            bottomButton.setData(R.drawable.calls_unmute, -16777216, -1, LocaleController.getString("VoipUnmute", R.string.VoipUnmute), true, animated);
        } else {
            bottomButton.setData(R.drawable.calls_unmute, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipMute", R.string.VoipMute), false, animated);
        }
        this.currentUserCameraFloatingLayout.setMuted(service.isMicMute(), animated);
        bottomButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPFragment.this.m4783lambda$setMicrohoneAction$23$orgtelegramuiVoIPFragment(view);
            }
        });
    }

    /* renamed from: lambda$setMicrohoneAction$23$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4783lambda$setMicrohoneAction$23$orgtelegramuiVoIPFragment(View view) {
        String text;
        VoIPService serviceInstance = VoIPService.getSharedInstance();
        if (serviceInstance != null) {
            boolean micMute = !serviceInstance.isMicMute();
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (micMute) {
                    text = LocaleController.getString("AccDescrVoipMicOff", R.string.AccDescrVoipMicOff);
                } else {
                    text = LocaleController.getString("AccDescrVoipMicOn", R.string.AccDescrVoipMicOn);
                }
                view.announceForAccessibility(text);
            }
            serviceInstance.setMicMute(micMute, false, true);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    private void setVideoAction(VoIPToggleButton bottomButton, final VoIPService service, boolean animated) {
        boolean isVideoAvailable;
        if (this.currentUserIsVideo || this.callingUserIsVideo) {
            isVideoAvailable = true;
        } else {
            isVideoAvailable = service.isVideoAvailable();
        }
        if (isVideoAvailable) {
            if (this.currentUserIsVideo) {
                bottomButton.setData(service.isScreencast() ? R.drawable.calls_sharescreen : R.drawable.calls_video, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipStopVideo", R.string.VoipStopVideo), false, animated);
            } else {
                bottomButton.setData(R.drawable.calls_video, -16777216, -1, LocaleController.getString("VoipStartVideo", R.string.VoipStartVideo), true, animated);
            }
            bottomButton.setCrossOffset(-AndroidUtilities.dpf2(3.5f));
            bottomButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VoIPFragment.this.m4786lambda$setVideoAction$25$orgtelegramuiVoIPFragment(service, view);
                }
            });
            bottomButton.setEnabled(true);
            return;
        }
        bottomButton.setData(R.drawable.calls_video, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), "Video", false, animated);
        bottomButton.setOnClickListener(null);
        bottomButton.setEnabled(false);
    }

    /* renamed from: lambda$setVideoAction$25$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4786lambda$setVideoAction$25$orgtelegramuiVoIPFragment(final VoIPService service, View view) {
        if (Build.VERSION.SDK_INT >= 23 && this.activity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.activity.requestPermissions(new String[]{"android.permission.CAMERA"}, 102);
        } else if (Build.VERSION.SDK_INT < 21 && service.privateCall != null && !service.privateCall.video && !this.callingUserIsVideo && !service.sharedUIParams.cameraAlertWasShowed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setMessage(LocaleController.getString("VoipSwitchToVideoCall", R.string.VoipSwitchToVideoCall));
            builder.setPositiveButton(LocaleController.getString("VoipSwitch", R.string.VoipSwitch), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda27
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPFragment.this.m4785lambda$setVideoAction$24$orgtelegramuiVoIPFragment(service, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.create().show();
        } else {
            toggleCameraInput();
        }
    }

    /* renamed from: lambda$setVideoAction$24$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4785lambda$setVideoAction$24$orgtelegramuiVoIPFragment(VoIPService service, DialogInterface dialogInterface, int i) {
        service.sharedUIParams.cameraAlertWasShowed = true;
        toggleCameraInput();
    }

    private void updateSpeakerPhoneIcon() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            return;
        }
        if (service.isBluetoothOn()) {
            this.speakerPhoneIcon.setImageResource(R.drawable.calls_bluetooth);
        } else if (service.isSpeakerphoneOn()) {
            this.speakerPhoneIcon.setImageResource(R.drawable.calls_speaker);
        } else if (service.isHeadsetPlugged()) {
            this.speakerPhoneIcon.setImageResource(R.drawable.calls_menu_headset);
        } else {
            this.speakerPhoneIcon.setImageResource(R.drawable.calls_menu_phone);
        }
    }

    private void setSpeakerPhoneAction(VoIPToggleButton bottomButton, VoIPService service, boolean animated) {
        if (service.isBluetoothOn()) {
            bottomButton.setData(R.drawable.calls_bluetooth, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth), false, animated);
            bottomButton.setChecked(false, animated);
        } else if (service.isSpeakerphoneOn()) {
            bottomButton.setData(R.drawable.calls_speaker, -16777216, -1, LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker), false, animated);
            bottomButton.setChecked(true, animated);
        } else {
            bottomButton.setData(R.drawable.calls_speaker, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker), false, animated);
            bottomButton.setChecked(false, animated);
        }
        bottomButton.setCheckableForAccessibility(true);
        bottomButton.setEnabled(true);
        bottomButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPFragment.this.m4784lambda$setSpeakerPhoneAction$26$orgtelegramuiVoIPFragment(view);
            }
        });
    }

    /* renamed from: lambda$setSpeakerPhoneAction$26$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4784lambda$setSpeakerPhoneAction$26$orgtelegramuiVoIPFragment(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
        }
    }

    private void setFrontalCameraAction(VoIPToggleButton bottomButton, final VoIPService service, boolean animated) {
        if (!this.currentUserIsVideo) {
            bottomButton.setData(R.drawable.calls_flip, ColorUtils.setAlphaComponent(-1, 127), ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", R.string.VoipFlip), false, animated);
            bottomButton.setOnClickListener(null);
            bottomButton.setEnabled(false);
            return;
        }
        bottomButton.setEnabled(true);
        if (!service.isFrontFaceCamera()) {
            bottomButton.setData(R.drawable.calls_flip, -16777216, -1, LocaleController.getString("VoipFlip", R.string.VoipFlip), false, animated);
        } else {
            bottomButton.setData(R.drawable.calls_flip, -1, ColorUtils.setAlphaComponent(-1, 30), LocaleController.getString("VoipFlip", R.string.VoipFlip), false, animated);
        }
        bottomButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VoIPFragment.this.m4782lambda$setFrontalCameraAction$27$orgtelegramuiVoIPFragment(service, view);
            }
        });
    }

    /* renamed from: lambda$setFrontalCameraAction$27$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4782lambda$setFrontalCameraAction$27$orgtelegramuiVoIPFragment(VoIPService service, View view) {
        String text;
        VoIPService serviceInstance = VoIPService.getSharedInstance();
        if (serviceInstance != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (service.isFrontFaceCamera()) {
                    text = LocaleController.getString("AccDescrVoipCamSwitchedToBack", R.string.AccDescrVoipCamSwitchedToBack);
                } else {
                    text = LocaleController.getString("AccDescrVoipCamSwitchedToFront", R.string.AccDescrVoipCamSwitchedToFront);
                }
                view.announceForAccessibility(text);
            }
            serviceInstance.switchCamera();
        }
    }

    public void onScreenCastStart() {
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog == null) {
            return;
        }
        privateVideoPreviewDialog.dismiss(true, true);
    }

    private void toggleCameraInput() {
        String text;
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                if (!this.currentUserIsVideo) {
                    text = LocaleController.getString("AccDescrVoipCamOn", R.string.AccDescrVoipCamOn);
                } else {
                    text = LocaleController.getString("AccDescrVoipCamOff", R.string.AccDescrVoipCamOff);
                }
                this.fragmentView.announceForAccessibility(text);
            }
            if (!this.currentUserIsVideo) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (this.previewDialog == null) {
                        service.createCaptureDevice(false);
                        if (!service.isFrontFaceCamera()) {
                            service.switchCamera();
                        }
                        this.windowView.setLockOnScreen(true);
                        PrivateVideoPreviewDialog privateVideoPreviewDialog = new PrivateVideoPreviewDialog(this.fragmentView.getContext(), false, true) { // from class: org.telegram.ui.VoIPFragment.21
                            @Override // org.telegram.ui.Components.voip.PrivateVideoPreviewDialog
                            public void onDismiss(boolean screencast, boolean apply) {
                                VoIPFragment.this.previewDialog = null;
                                VoIPService service2 = VoIPService.getSharedInstance();
                                VoIPFragment.this.windowView.setLockOnScreen(false);
                                if (apply) {
                                    VoIPFragment.this.currentUserIsVideo = true;
                                    if (service2 != null && !screencast) {
                                        service2.requestVideoCall(false);
                                        service2.setVideoState(false, 2);
                                    }
                                } else if (service2 != null) {
                                    service2.setVideoState(false, 0);
                                }
                                VoIPFragment voIPFragment = VoIPFragment.this;
                                voIPFragment.previousState = voIPFragment.currentState;
                                VoIPFragment.this.updateViewState();
                            }
                        };
                        this.previewDialog = privateVideoPreviewDialog;
                        WindowInsets windowInsets = this.lastInsets;
                        if (windowInsets != null) {
                            privateVideoPreviewDialog.setBottomPadding(windowInsets.getSystemWindowInsetBottom());
                        }
                        this.fragmentView.addView(this.previewDialog);
                        return;
                    }
                    return;
                }
                this.currentUserIsVideo = true;
                if (!service.isSpeakerphoneOn()) {
                    VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
                }
                service.requestVideoCall(false);
                service.setVideoState(false, 2);
            } else {
                this.currentUserTextureView.saveCameraLastBitmap();
                service.setVideoState(false, 0);
                if (Build.VERSION.SDK_INT >= 21) {
                    service.clearCamera();
                }
            }
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onRequestPermissionsResultInternal(requestCode, permissions, grantResults);
        }
    }

    private void onRequestPermissionsResultInternal(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (VoIPService.getSharedInstance() == null) {
                this.windowView.finish();
                return;
            } else if (grantResults.length > 0 && grantResults[0] == 0) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            } else if (!this.activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                VoIPService.getSharedInstance().declineIncomingCall();
                VoIPHelper.permissionDenied(this.activity, new Runnable() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda15
                    @Override // java.lang.Runnable
                    public final void run() {
                        VoIPFragment.this.m4780xa53b898b();
                    }
                }, requestCode);
                return;
            }
        }
        if (requestCode == 102) {
            if (VoIPService.getSharedInstance() == null) {
                this.windowView.finish();
            } else if (grantResults.length > 0 && grantResults[0] == 0) {
                toggleCameraInput();
            }
        }
    }

    /* renamed from: lambda$onRequestPermissionsResultInternal$28$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4780xa53b898b() {
        this.windowView.finish();
    }

    private void updateSystemBarColors() {
        this.overlayPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (this.uiVisibilityAlpha * 102.0f * this.enterTransitionProgress)));
        this.overlayBottomPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (((this.fillNaviagtionBarValue * 0.5f) + 0.5f) * 255.0f * this.enterTransitionProgress)));
        ViewGroup viewGroup = this.fragmentView;
        if (viewGroup != null) {
            viewGroup.invalidate();
        }
    }

    public static void onPause() {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onPauseInternal();
        }
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().onPause();
        }
    }

    public static void onResume() {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onResumeInternal();
        }
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().onResume();
        }
    }

    public void onPauseInternal() {
        boolean screenOn;
        VoIPService service;
        WindowInsets windowInsets;
        WindowInsets windowInsets2;
        PowerManager pm = (PowerManager) this.activity.getSystemService("power");
        if (Build.VERSION.SDK_INT >= 20) {
            screenOn = pm.isInteractive();
        } else {
            screenOn = pm.isScreenOn();
        }
        boolean hasPermissionsToPip = AndroidUtilities.checkInlinePermissions(this.activity);
        if (this.canSwitchToPip && hasPermissionsToPip) {
            int h = instance.windowView.getMeasuredHeight();
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets2 = instance.lastInsets) != null) {
                h -= windowInsets2.getSystemWindowInsetBottom();
            }
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), h, 0);
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                VoIPPiPView.topInset = windowInsets.getSystemWindowInsetTop();
                VoIPPiPView.bottomInset = instance.lastInsets.getSystemWindowInsetBottom();
            }
        }
        if (this.currentUserIsVideo) {
            if ((!hasPermissionsToPip || !screenOn) && (service = VoIPService.getSharedInstance()) != null) {
                service.setVideoState(false, 1);
            }
        }
    }

    public void onResumeInternal() {
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.finish();
        }
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            if (service.getVideoState(false) == 1) {
                service.setVideoState(false, 2);
            }
            updateViewState();
        } else {
            this.windowView.finish();
        }
        this.deviceIsLocked = ((KeyguardManager) this.activity.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
    }

    private void showErrorDialog(CharSequence message) {
        if (this.activity.isFinishing()) {
            return;
        }
        AlertDialog dlg = new DarkAlertDialog.Builder(this.activity).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(message).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda29
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                VoIPFragment.this.m4787lambda$showErrorDialog$29$orgtelegramuiVoIPFragment(dialogInterface);
            }
        });
    }

    /* renamed from: lambda$showErrorDialog$29$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4787lambda$showErrorDialog$29$orgtelegramuiVoIPFragment(DialogInterface dialog) {
        this.windowView.finish();
    }

    private void requestInlinePermissions() {
        if (Build.VERSION.SDK_INT >= 21) {
            AlertsCreator.createDrawOverlayPermissionDialog(this.activity, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.VoIPFragment$$ExternalSyntheticLambda25
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPFragment.this.m4781lambda$requestInlinePermissions$30$orgtelegramuiVoIPFragment(dialogInterface, i);
                }
            }).show();
        }
    }

    /* renamed from: lambda$requestInlinePermissions$30$org-telegram-ui-VoIPFragment */
    public /* synthetic */ void m4781lambda$requestInlinePermissions$30$orgtelegramuiVoIPFragment(DialogInterface dialogInterface, int i) {
        VoIPWindowView voIPWindowView = this.windowView;
        if (voIPWindowView != null) {
            voIPWindowView.finish();
        }
    }
}
