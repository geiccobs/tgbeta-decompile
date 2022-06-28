package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class GroupCallPip implements NotificationCenter.NotificationCenterDelegate {
    private static boolean forceRemoved = true;
    private static GroupCallPip instance;
    FrameLayout alertContainer;
    boolean animateToPrepareRemove;
    boolean animateToShowRemoveTooltip;
    AvatarsImageView avatarsImageView;
    private final GroupCallPipButton button;
    boolean buttonInAlpha;
    int currentAccount;
    RLottieDrawable deleteIcon;
    private final RLottieImageView iconView;
    int lastScreenX;
    int lastScreenY;
    boolean moving;
    ValueAnimator pinAnimator;
    GroupCallPipAlertView pipAlertView;
    boolean pressedState;
    View removeTooltipView;
    boolean removed;
    boolean showAlert;
    AnimatorSet showRemoveAnimator;
    WindowManager.LayoutParams windowLayoutParams;
    int windowLeft;
    WindowManager windowManager;
    float windowOffsetLeft;
    float windowOffsetTop;
    FrameLayout windowRemoveTooltipOverlayView;
    FrameLayout windowRemoveTooltipView;
    int windowTop;
    FrameLayout windowView;
    float windowX;
    float windowY;
    float prepareToRemoveProgress = 0.0f;
    int[] location = new int[2];
    float[] point = new float[2];
    float xRelative = -1.0f;
    float yRelative = -1.0f;
    private ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.GroupCallPip.1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float x = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip.this.windowLayoutParams.x = (int) x;
            GroupCallPip.this.updateAvatarsPosition();
            if (GroupCallPip.this.windowView.getParent() != null) {
                GroupCallPip.this.windowManager.updateViewLayout(GroupCallPip.this.windowView, GroupCallPip.this.windowLayoutParams);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.GroupCallPip.2
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float y = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip.this.windowLayoutParams.y = (int) y;
            if (GroupCallPip.this.windowView.getParent() != null) {
                GroupCallPip.this.windowManager.updateViewLayout(GroupCallPip.this.windowView, GroupCallPip.this.windowLayoutParams);
            }
        }
    };
    boolean animateToPinnedToCenter = false;
    float pinnedProgress = 0.0f;

    public GroupCallPip(Context context, int account) {
        this.currentAccount = account;
        float touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(context, touchSlop);
        this.windowView = anonymousClass3;
        anonymousClass3.setAlpha(0.7f);
        GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context, this.currentAccount, false);
        this.button = groupCallPipButton;
        this.windowView.addView(groupCallPipButton, LayoutHelper.createFrame(-1, -1, 17));
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, true);
        this.avatarsImageView = avatarsImageView;
        avatarsImageView.setStyle(5);
        this.avatarsImageView.setCentered(true);
        this.avatarsImageView.setVisibility(8);
        this.avatarsImageView.setDelegate(new Runnable() { // from class: org.telegram.ui.Components.GroupCallPip$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallPip.this.m2655lambda$new$0$orgtelegramuiComponentsGroupCallPip();
            }
        });
        updateAvatars(false);
        this.windowView.addView(this.avatarsImageView, LayoutHelper.createFrame(108, 36, 49));
        this.windowRemoveTooltipView = new FrameLayout(context) { // from class: org.telegram.ui.Components.GroupCallPip.4
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                GroupCallPip.this.windowRemoveTooltipView.getLocationOnScreen(GroupCallPip.this.location);
                GroupCallPip groupCallPip = GroupCallPip.this;
                groupCallPip.windowLeft = groupCallPip.location[0];
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowTop = groupCallPip2.location[1] - AndroidUtilities.dp(25.0f);
            }

            @Override // android.view.View
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setVisibility(visibility);
            }
        };
        View view = new View(context) { // from class: org.telegram.ui.Components.GroupCallPip.5
            Paint paint = new Paint(1);

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (GroupCallPip.this.animateToPrepareRemove && GroupCallPip.this.prepareToRemoveProgress != 1.0f) {
                    GroupCallPip.this.prepareToRemoveProgress += 0.064f;
                    if (GroupCallPip.this.prepareToRemoveProgress > 1.0f) {
                        GroupCallPip.this.prepareToRemoveProgress = 1.0f;
                    }
                    invalidate();
                } else if (!GroupCallPip.this.animateToPrepareRemove && GroupCallPip.this.prepareToRemoveProgress != 0.0f) {
                    GroupCallPip.this.prepareToRemoveProgress -= 0.064f;
                    if (GroupCallPip.this.prepareToRemoveProgress < 0.0f) {
                        GroupCallPip.this.prepareToRemoveProgress = 0.0f;
                    }
                    invalidate();
                }
                this.paint.setColor(ColorUtils.blendARGB(1711607061, 1714752530, GroupCallPip.this.prepareToRemoveProgress));
                float r = AndroidUtilities.dp(35.0f) + (AndroidUtilities.dp(5.0f) * GroupCallPip.this.prepareToRemoveProgress);
                canvas.drawCircle(getMeasuredWidth() / 2.0f, (getMeasuredHeight() / 2.0f) - AndroidUtilities.dp(25.0f), r, this.paint);
            }

            @Override // android.view.View
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setAlpha(alpha);
            }

            @Override // android.view.View
            public void setScaleX(float scaleX) {
                super.setScaleX(scaleX);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleX(scaleX);
            }

            @Override // android.view.View
            public void setScaleY(float scaleY) {
                super.setScaleY(scaleY);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleY(scaleY);
            }

            @Override // android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setTranslationY(translationY);
            }
        };
        this.removeTooltipView = view;
        this.windowRemoveTooltipView.addView(view);
        this.windowRemoveTooltipOverlayView = new FrameLayout(context);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.group_pip_delete_icon, "2131558457", AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f), true, null);
        this.deleteIcon = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        rLottieImageView.setAnimation(this.deleteIcon);
        rLottieImageView.setColorFilter(-1);
        this.windowRemoveTooltipOverlayView.addView(rLottieImageView, LayoutHelper.createFrame(40, 40.0f, 17, 0.0f, 0.0f, 0.0f, 25.0f));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.GroupCallPip.6
            int lastSize = -1;

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int size = AndroidUtilities.displaySize.x + AndroidUtilities.displaySize.y;
                int i = this.lastSize;
                if (i > 0 && i != size) {
                    setVisibility(8);
                    GroupCallPip.this.showAlert = false;
                    GroupCallPip.this.checkButtonAlpha();
                }
                this.lastSize = size;
            }

            @Override // android.view.View
            public void setVisibility(int visibility) {
                super.setVisibility(visibility);
                if (visibility == 8) {
                    this.lastSize = -1;
                }
            }
        };
        this.alertContainer = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallPip$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                GroupCallPip.this.m2656lambda$new$1$orgtelegramuiComponentsGroupCallPip(view2);
            }
        });
        this.alertContainer.setClipChildren(false);
        FrameLayout frameLayout2 = this.alertContainer;
        GroupCallPipAlertView groupCallPipAlertView = new GroupCallPipAlertView(context, this.currentAccount);
        this.pipAlertView = groupCallPipAlertView;
        frameLayout2.addView(groupCallPipAlertView, LayoutHelper.createFrame(-2, -2.0f));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.GroupCallPip$3 */
    /* loaded from: classes5.dex */
    public class AnonymousClass3 extends FrameLayout {
        AnimatorSet moveToBoundsAnimator;
        boolean pressed;
        long startTime;
        float startX;
        float startY;
        final /* synthetic */ float val$touchSlop;
        Runnable pressedRunnable = new Runnable() { // from class: org.telegram.ui.Components.GroupCallPip.3.1
            @Override // java.lang.Runnable
            public void run() {
                VoIPService voIPService = VoIPService.getSharedInstance();
                if (voIPService != null && voIPService.isMicMute()) {
                    ChatObject.Call call = voIPService.groupCall;
                    TLRPC.TL_groupCallParticipant participant = call.participants.get(voIPService.getSelfId());
                    if (participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(voIPService.getChat())) {
                        return;
                    }
                    AndroidUtilities.runOnUIThread(AnonymousClass3.this.micRunnable, 90L);
                    AnonymousClass3.this.performHapticFeedback(3, 2);
                    AnonymousClass3.this.pressed = true;
                }
            }
        };
        Runnable micRunnable = GroupCallPip$3$$ExternalSyntheticLambda0.INSTANCE;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass3(Context arg0, float f) {
            super(arg0);
            GroupCallPip.this = this$0;
            this.val$touchSlop = f;
        }

        public static /* synthetic */ void lambda$$0() {
            if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute()) {
                VoIPService.getSharedInstance().setMicMute(false, true, false);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (AndroidUtilities.displaySize.x != GroupCallPip.this.lastScreenX || GroupCallPip.this.lastScreenY != AndroidUtilities.displaySize.y) {
                GroupCallPip.this.lastScreenX = AndroidUtilities.displaySize.x;
                GroupCallPip.this.lastScreenY = AndroidUtilities.displaySize.y;
                if (GroupCallPip.this.xRelative < 0.0f) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("groupcallpipconfig", 0);
                    GroupCallPip.this.xRelative = preferences.getFloat("relativeX", 1.0f);
                    GroupCallPip.this.yRelative = preferences.getFloat("relativeY", 0.4f);
                }
                if (GroupCallPip.instance != null) {
                    GroupCallPip.instance.setPosition(GroupCallPip.this.xRelative, GroupCallPip.this.yRelative);
                }
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            GroupCallPip groupCallPip;
            GroupCallPip groupCallPip2;
            GroupCallPip groupCallPip3;
            GroupCallPip groupCallPip4;
            double angle;
            if (GroupCallPip.instance == null) {
                return false;
            }
            float x = event.getRawX();
            float y = event.getRawY();
            ViewParent parent = getParent();
            switch (event.getAction()) {
                case 0:
                    getLocationOnScreen(GroupCallPip.this.location);
                    GroupCallPip.this.windowOffsetLeft = groupCallPip.location[0] - GroupCallPip.this.windowLayoutParams.x;
                    GroupCallPip.this.windowOffsetTop = groupCallPip2.location[1] - GroupCallPip.this.windowLayoutParams.y;
                    this.startX = x;
                    this.startY = y;
                    this.startTime = System.currentTimeMillis();
                    AndroidUtilities.runOnUIThread(this.pressedRunnable, 300L);
                    GroupCallPip.this.windowX = groupCallPip3.windowLayoutParams.x;
                    GroupCallPip.this.windowY = groupCallPip4.windowLayoutParams.y;
                    GroupCallPip.this.pressedState = true;
                    GroupCallPip.this.checkButtonAlpha();
                    return true;
                case 1:
                case 3:
                    AndroidUtilities.cancelRunOnUIThread(this.micRunnable);
                    AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                    if (GroupCallPip.this.animateToPrepareRemove) {
                        if (this.pressed && VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true, false, false);
                        }
                        this.pressed = false;
                        GroupCallPip.this.remove();
                        return false;
                    }
                    GroupCallPip.this.pressedState = false;
                    GroupCallPip.this.checkButtonAlpha();
                    if (this.pressed) {
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true, false, false);
                            performHapticFeedback(3, 2);
                        }
                        this.pressed = false;
                    } else if (event.getAction() == 1 && !GroupCallPip.this.moving) {
                        onTap();
                        return false;
                    }
                    if (parent != null && GroupCallPip.this.moving) {
                        parent.requestDisallowInterceptTouchEvent(false);
                        int parentWidth = AndroidUtilities.displaySize.x;
                        int parentHeight = AndroidUtilities.displaySize.y;
                        float left = GroupCallPip.this.windowLayoutParams.x;
                        float right = getMeasuredWidth() + left;
                        float top = GroupCallPip.this.windowLayoutParams.y;
                        float bottom = getMeasuredHeight() + top;
                        this.moveToBoundsAnimator = new AnimatorSet();
                        float finallyX = left;
                        float finallyY = top;
                        float paddingHorizontal = -AndroidUtilities.dp(36.0f);
                        if (left < paddingHorizontal) {
                            finallyX = paddingHorizontal;
                            ValueAnimator animator = ValueAnimator.ofFloat(GroupCallPip.this.windowLayoutParams.x, paddingHorizontal);
                            animator.addUpdateListener(GroupCallPip.this.updateXlistener);
                            this.moveToBoundsAnimator.playTogether(animator);
                        } else if (right > parentWidth - paddingHorizontal) {
                            float measuredWidth = (parentWidth - getMeasuredWidth()) - paddingHorizontal;
                            finallyX = measuredWidth;
                            ValueAnimator animator2 = ValueAnimator.ofFloat(GroupCallPip.this.windowLayoutParams.x, measuredWidth);
                            animator2.addUpdateListener(GroupCallPip.this.updateXlistener);
                            this.moveToBoundsAnimator.playTogether(animator2);
                        }
                        int maxBottom = AndroidUtilities.dp(36.0f) + parentHeight;
                        if (top < AndroidUtilities.statusBarHeight - AndroidUtilities.dp(36.0f)) {
                            float dp = AndroidUtilities.statusBarHeight - AndroidUtilities.dp(36.0f);
                            finallyY = dp;
                            ValueAnimator animator3 = ValueAnimator.ofFloat(GroupCallPip.this.windowLayoutParams.y, dp);
                            animator3.addUpdateListener(GroupCallPip.this.updateYlistener);
                            this.moveToBoundsAnimator.playTogether(animator3);
                        } else if (bottom > maxBottom) {
                            float measuredHeight = maxBottom - getMeasuredHeight();
                            finallyY = measuredHeight;
                            ValueAnimator animator4 = ValueAnimator.ofFloat(GroupCallPip.this.windowLayoutParams.y, measuredHeight);
                            animator4.addUpdateListener(GroupCallPip.this.updateYlistener);
                            this.moveToBoundsAnimator.playTogether(animator4);
                        }
                        this.moveToBoundsAnimator.setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.moveToBoundsAnimator.start();
                        if (GroupCallPip.this.xRelative >= 0.0f) {
                            GroupCallPip groupCallPip5 = GroupCallPip.this;
                            groupCallPip5.getRelativePosition(finallyX, finallyY, groupCallPip5.point);
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("groupcallpipconfig", 0);
                            SharedPreferences.Editor edit = preferences.edit();
                            GroupCallPip groupCallPip6 = GroupCallPip.this;
                            float f = groupCallPip6.point[0];
                            groupCallPip6.xRelative = f;
                            SharedPreferences.Editor putFloat = edit.putFloat("relativeX", f);
                            GroupCallPip groupCallPip7 = GroupCallPip.this;
                            float f2 = groupCallPip7.point[1];
                            groupCallPip7.yRelative = f2;
                            putFloat.putFloat("relativeY", f2).apply();
                        }
                    }
                    GroupCallPip.this.moving = false;
                    GroupCallPip.this.showRemoveTooltip(false);
                    return true;
                case 2:
                    float dx = x - this.startX;
                    float dy = y - this.startY;
                    if (!GroupCallPip.this.moving) {
                        float f3 = (dx * dx) + (dy * dy);
                        float f4 = this.val$touchSlop;
                        if (f3 > f4 * f4) {
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                            AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                            GroupCallPip.this.moving = true;
                            GroupCallPip.this.showRemoveTooltip(true);
                            GroupCallPip.this.showAlert(false);
                            this.startX = x;
                            this.startY = y;
                            dx = 0.0f;
                            dy = 0.0f;
                        }
                    }
                    if (!GroupCallPip.this.moving) {
                        return true;
                    }
                    GroupCallPip.this.windowX += dx;
                    GroupCallPip.this.windowY += dy;
                    this.startX = x;
                    this.startY = y;
                    GroupCallPip.this.updateButtonPosition();
                    float cx = GroupCallPip.this.windowX + (getMeasuredWidth() / 2.0f);
                    float cy = GroupCallPip.this.windowY + (getMeasuredHeight() / 2.0f);
                    float cxRemove = (GroupCallPip.this.windowLeft - GroupCallPip.this.windowOffsetLeft) + (GroupCallPip.this.windowRemoveTooltipView.getMeasuredWidth() / 2.0f);
                    float cyRemove = (GroupCallPip.this.windowTop - GroupCallPip.this.windowOffsetTop) + (GroupCallPip.this.windowRemoveTooltipView.getMeasuredHeight() / 2.0f);
                    float distanceToRemove = ((cx - cxRemove) * (cx - cxRemove)) + ((cy - cyRemove) * (cy - cyRemove));
                    boolean prepareToRemove = false;
                    boolean pinnedToCenter = false;
                    if (distanceToRemove < AndroidUtilities.dp(80.0f) * AndroidUtilities.dp(80.0f)) {
                        prepareToRemove = true;
                        double angle2 = Math.toDegrees(Math.atan((cx - cxRemove) / (cy - cyRemove)));
                        if ((cx > cxRemove && cy < cyRemove) || (cx < cxRemove && cy < cyRemove)) {
                            angle = 270.0d - angle2;
                        } else {
                            angle = 90.0d - angle2;
                        }
                        GroupCallPip.this.button.setRemoveAngle(angle);
                        if (distanceToRemove < AndroidUtilities.dp(50.0f) * AndroidUtilities.dp(50.0f)) {
                            pinnedToCenter = true;
                        }
                    }
                    GroupCallPip.this.pinnedToCenter(pinnedToCenter);
                    GroupCallPip.this.prepareToRemove(prepareToRemove);
                    return true;
                default:
                    return true;
            }
        }

        private void onTap() {
            if (VoIPService.getSharedInstance() != null) {
                GroupCallPip groupCallPip = GroupCallPip.this;
                groupCallPip.showAlert(!groupCallPip.showAlert);
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupCallPip */
    public /* synthetic */ void m2655lambda$new$0$orgtelegramuiComponentsGroupCallPip() {
        updateAvatars(true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GroupCallPip */
    public /* synthetic */ void m2656lambda$new$1$orgtelegramuiComponentsGroupCallPip(View view) {
        showAlert(false);
    }

    public static boolean isShowing() {
        if (!RTMPStreamPipOverlay.isVisible() && instance == null) {
            if (!checkInlinePermissions()) {
                return false;
            }
            VoIPService service = VoIPService.getSharedInstance();
            boolean groupCall = false;
            if (service != null && service.groupCall != null && !service.isHangingUp()) {
                groupCall = true;
            }
            return groupCall && !forceRemoved && (ApplicationLoader.mainInterfaceStopped || !GroupCallActivity.groupCallUiVisible);
        }
        return true;
    }

    public static boolean onBackPressed() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip == null || !groupCallPip.showAlert) {
            return false;
        }
        groupCallPip.showAlert(false);
        return true;
    }

    public void showAlert(boolean b) {
        if (b != this.showAlert) {
            this.showAlert = b;
            this.alertContainer.animate().setListener(null).cancel();
            if (this.showAlert) {
                if (this.alertContainer.getVisibility() != 0) {
                    this.alertContainer.setVisibility(0);
                    this.alertContainer.setAlpha(0.0f);
                    this.pipAlertView.setScaleX(0.7f);
                    this.pipAlertView.setScaleY(0.7f);
                }
                this.alertContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.GroupCallPip.7
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        GroupCallPip.this.alertContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallPip.this.alertContainer.getLocationOnScreen(GroupCallPip.this.location);
                        float cx = ((GroupCallPip.this.windowLayoutParams.x + GroupCallPip.this.windowOffsetLeft) + (GroupCallPip.this.button.getMeasuredWidth() / 2.0f)) - GroupCallPip.this.location[0];
                        float cy = ((GroupCallPip.this.windowLayoutParams.y + GroupCallPip.this.windowOffsetTop) + (GroupCallPip.this.button.getMeasuredWidth() / 2.0f)) - GroupCallPip.this.location[1];
                        boolean canHorizontal = cy - ((float) AndroidUtilities.dp(61.0f)) > 0.0f && ((float) AndroidUtilities.dp(61.0f)) + cy < ((float) GroupCallPip.this.alertContainer.getMeasuredHeight());
                        if (AndroidUtilities.dp(61.0f) + cx + GroupCallPip.this.pipAlertView.getMeasuredWidth() >= GroupCallPip.this.alertContainer.getMeasuredWidth() - AndroidUtilities.dp(16.0f) || !canHorizontal) {
                            if ((cx - AndroidUtilities.dp(61.0f)) - GroupCallPip.this.pipAlertView.getMeasuredWidth() > AndroidUtilities.dp(16.0f) && canHorizontal) {
                                float yOffset = cy / GroupCallPip.this.alertContainer.getMeasuredHeight();
                                float maxOffset = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredHeight();
                                float yOffset2 = Math.max(maxOffset, Math.min(yOffset, 1.0f - maxOffset));
                                GroupCallPip.this.pipAlertView.setTranslationX((int) ((cx - AndroidUtilities.dp(61.0f)) - GroupCallPip.this.pipAlertView.getMeasuredWidth()));
                                GroupCallPip.this.pipAlertView.setTranslationY((int) (cy - (GroupCallPip.this.pipAlertView.getMeasuredHeight() * yOffset2)));
                                GroupCallPip.this.pipAlertView.setPosition(1, cx, cy);
                            } else if (cy > GroupCallPip.this.alertContainer.getMeasuredHeight() * 0.3f) {
                                float xOffset = cx / GroupCallPip.this.alertContainer.getMeasuredWidth();
                                float maxOffset2 = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredWidth();
                                GroupCallPip.this.pipAlertView.setTranslationX((int) (cx - (GroupCallPip.this.pipAlertView.getMeasuredWidth() * Math.max(maxOffset2, Math.min(xOffset, 1.0f - maxOffset2)))));
                                GroupCallPip.this.pipAlertView.setTranslationY((int) ((cy - GroupCallPip.this.pipAlertView.getMeasuredHeight()) - AndroidUtilities.dp(61.0f)));
                                GroupCallPip.this.pipAlertView.setPosition(3, cx, cy);
                            } else {
                                float xOffset2 = cx / GroupCallPip.this.alertContainer.getMeasuredWidth();
                                float maxOffset3 = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredWidth();
                                GroupCallPip.this.pipAlertView.setTranslationX((int) (cx - (GroupCallPip.this.pipAlertView.getMeasuredWidth() * Math.max(maxOffset3, Math.min(xOffset2, 1.0f - maxOffset3)))));
                                GroupCallPip.this.pipAlertView.setTranslationY((int) (AndroidUtilities.dp(61.0f) + cy));
                                GroupCallPip.this.pipAlertView.setPosition(2, cx, cy);
                            }
                        } else {
                            GroupCallPip.this.pipAlertView.setTranslationX(AndroidUtilities.dp(61.0f) + cx);
                            float yOffset3 = cy / GroupCallPip.this.alertContainer.getMeasuredHeight();
                            float maxOffset4 = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredHeight();
                            GroupCallPip.this.pipAlertView.setTranslationY((int) (cy - (GroupCallPip.this.pipAlertView.getMeasuredHeight() * Math.max(maxOffset4, Math.min(yOffset3, 1.0f - maxOffset4)))));
                            GroupCallPip.this.pipAlertView.setPosition(0, cx, cy);
                        }
                        return false;
                    }
                });
                this.alertContainer.animate().alpha(1.0f).setDuration(150L).start();
                this.pipAlertView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
            } else {
                this.pipAlertView.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150L).start();
                this.alertContainer.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupCallPip.8
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        GroupCallPip.this.alertContainer.setVisibility(8);
                    }
                }).start();
            }
        }
        checkButtonAlpha();
    }

    public void checkButtonAlpha() {
        boolean alpha = this.pressedState || this.showAlert;
        if (this.buttonInAlpha != alpha) {
            this.buttonInAlpha = alpha;
            if (alpha) {
                this.windowView.animate().alpha(1.0f).start();
            } else {
                this.windowView.animate().alpha(0.7f).start();
            }
            this.button.setPressedState(alpha);
        }
    }

    public static GroupCallPip getInstance() {
        return instance;
    }

    public void remove() {
        View alert;
        if (instance != null) {
            this.removed = true;
            forceRemoved = true;
            this.button.removed = true;
            instance.showAlert(false);
            float cx = this.windowLayoutParams.x + (this.windowView.getMeasuredWidth() / 2.0f);
            float cy = this.windowLayoutParams.y + (this.windowView.getMeasuredHeight() / 2.0f);
            float cxRemove = (this.windowLeft - this.windowOffsetLeft) + (this.windowRemoveTooltipView.getMeasuredWidth() / 2.0f);
            float cyRemove = (this.windowTop - this.windowOffsetTop) + (this.windowRemoveTooltipView.getMeasuredHeight() / 2.0f);
            float dx = cxRemove - cx;
            float dy = cyRemove - cy;
            GroupCallPip groupCallPip = instance;
            WindowManager windowManager = groupCallPip.windowManager;
            View windowView = groupCallPip.windowView;
            View windowRemoveTooltipView = groupCallPip.windowRemoveTooltipView;
            View windowRemoveTooltipOverlayView = groupCallPip.windowRemoveTooltipOverlayView;
            View alert2 = groupCallPip.alertContainer;
            onDestroy();
            instance = null;
            AnimatorSet animatorSet = new AnimatorSet();
            long additionalDuration = 0;
            if (this.deleteIcon.getCurrentFrame() < 33) {
                alert = alert2;
                additionalDuration = ((1.0f - (this.deleteIcon.getCurrentFrame() / 33.0f)) * ((float) this.deleteIcon.getDuration())) / 2.0f;
            } else {
                alert = alert2;
            }
            ValueAnimator animator = ValueAnimator.ofFloat(this.windowLayoutParams.x, this.windowLayoutParams.x + dx);
            animator.addUpdateListener(this.updateXlistener);
            animator.setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.playTogether(animator);
            ValueAnimator animator2 = ValueAnimator.ofFloat(this.windowLayoutParams.y, (this.windowLayoutParams.y + dy) - AndroidUtilities.dp(30.0f), this.windowLayoutParams.y + dy);
            animator2.addUpdateListener(this.updateYlistener);
            animator2.setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.playTogether(animator2);
            animatorSet.playTogether(ObjectAnimator.ofFloat(windowView, View.SCALE_X, windowView.getScaleX(), 0.1f).setDuration(180L));
            animatorSet.playTogether(ObjectAnimator.ofFloat(windowView, View.SCALE_Y, windowView.getScaleY(), 0.1f).setDuration(180L));
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(windowView, View.ALPHA, 1.0f, 0.0f);
            alphaAnimator.setStartDelay(((float) 350) * 0.7f);
            alphaAnimator.setDuration(((float) 350) * 0.3f);
            animatorSet.playTogether(alphaAnimator);
            AndroidUtilities.runOnUIThread(GroupCallPip$$ExternalSyntheticLambda3.INSTANCE, 20 + 350);
            long moveDuration = 350 + additionalDuration + 180;
            ObjectAnimator o = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, 1.0f, 1.05f);
            o.setDuration(moveDuration);
            o.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            animatorSet.playTogether(o);
            ObjectAnimator o2 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, 1.0f, 1.05f);
            o2.setDuration(moveDuration);
            o2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            animatorSet.playTogether(o2);
            ObjectAnimator o3 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, 1.0f, 0.3f);
            o3.setStartDelay(moveDuration);
            o3.setDuration(350L);
            o3.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet.playTogether(o3);
            ObjectAnimator o4 = ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, 1.0f, 0.3f);
            o4.setStartDelay(moveDuration);
            o4.setDuration(350L);
            o4.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet.playTogether(o4);
            ObjectAnimator o5 = ObjectAnimator.ofFloat(this.removeTooltipView, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(60.0f));
            o5.setStartDelay(moveDuration);
            o5.setDuration(350L);
            o5.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet.playTogether(o5);
            ObjectAnimator o6 = ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, 1.0f, 0.0f);
            o6.setStartDelay(moveDuration);
            o6.setDuration(350L);
            o6.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            animatorSet.playTogether(o6);
            animatorSet.addListener(new AnonymousClass9(windowView, windowRemoveTooltipView, windowManager, windowRemoveTooltipOverlayView, alert));
            animatorSet.start();
            this.deleteIcon.setCustomEndFrame(66);
            this.iconView.stopAnimation();
            this.iconView.playAnimation();
        }
    }

    /* renamed from: org.telegram.ui.Components.GroupCallPip$9 */
    /* loaded from: classes5.dex */
    public class AnonymousClass9 extends AnimatorListenerAdapter {
        final /* synthetic */ View val$alert;
        final /* synthetic */ WindowManager val$windowManager;
        final /* synthetic */ View val$windowRemoveTooltipOverlayView;
        final /* synthetic */ View val$windowRemoveTooltipView;
        final /* synthetic */ View val$windowView;

        AnonymousClass9(View view, View view2, WindowManager windowManager, View view3, View view4) {
            GroupCallPip.this = this$0;
            this.val$windowView = view;
            this.val$windowRemoveTooltipView = view2;
            this.val$windowManager = windowManager;
            this.val$windowRemoveTooltipOverlayView = view3;
            this.val$alert = view4;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            NotificationCenter notificationCenter = NotificationCenter.getInstance(GroupCallPip.this.currentAccount);
            final View view = this.val$windowView;
            final View view2 = this.val$windowRemoveTooltipView;
            final WindowManager windowManager = this.val$windowManager;
            final View view3 = this.val$windowRemoveTooltipOverlayView;
            final View view4 = this.val$alert;
            notificationCenter.doOnIdle(new Runnable() { // from class: org.telegram.ui.Components.GroupCallPip$9$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallPip.AnonymousClass9.lambda$onAnimationEnd$0(view, view2, windowManager, view3, view4);
                }
            });
        }

        public static /* synthetic */ void lambda$onAnimationEnd$0(View windowView, View windowRemoveTooltipView, WindowManager windowManager, View windowRemoveTooltipOverlayView, View alert) {
            windowView.setVisibility(8);
            windowRemoveTooltipView.setVisibility(8);
            windowManager.removeView(windowView);
            windowManager.removeView(windowRemoveTooltipView);
            windowManager.removeView(windowRemoveTooltipOverlayView);
            windowManager.removeView(alert);
        }
    }

    private void updateAvatars(boolean animated) {
        ChatObject.Call call;
        if (this.avatarsImageView.avatarsDarawable.transitionProgressAnimator == null) {
            VoIPService voIPService = VoIPService.getSharedInstance();
            if (voIPService != null) {
                call = voIPService.groupCall;
            } else {
                call = null;
            }
            if (call != null) {
                long selfId = voIPService.getSelfId();
                int a = 0;
                int N = call.sortedParticipants.size();
                int k = 0;
                while (k < 2) {
                    if (a >= N) {
                        this.avatarsImageView.setObject(k, this.currentAccount, null);
                        k++;
                    } else {
                        TLRPC.TL_groupCallParticipant participant = call.sortedParticipants.get(a);
                        if (MessageObject.getPeerId(participant.peer) != selfId && SystemClock.uptimeMillis() - participant.lastSpeakTime <= 500) {
                            this.avatarsImageView.setObject(k, this.currentAccount, participant);
                            k++;
                        }
                    }
                    a++;
                }
                this.avatarsImageView.setObject(2, this.currentAccount, null);
                this.avatarsImageView.commitTransition(animated);
                return;
            }
            for (int a2 = 0; a2 < 3; a2++) {
                this.avatarsImageView.setObject(a2, this.currentAccount, null);
            }
            this.avatarsImageView.commitTransition(animated);
            return;
        }
        this.avatarsImageView.updateAfterTransitionEnd();
    }

    public static void show(Context context, int account) {
        if (instance != null) {
            return;
        }
        instance = new GroupCallPip(context, account);
        WindowManager wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        instance.windowManager = wm;
        WindowManager.LayoutParams windowLayoutParams = createWindowLayoutParams(context);
        windowLayoutParams.width = -1;
        windowLayoutParams.height = -1;
        windowLayoutParams.dimAmount = 0.25f;
        windowLayoutParams.flags = 522;
        wm.addView(instance.alertContainer, windowLayoutParams);
        instance.alertContainer.setVisibility(8);
        WindowManager.LayoutParams windowLayoutParams2 = createWindowLayoutParams(context);
        windowLayoutParams2.gravity = 81;
        windowLayoutParams2.width = AndroidUtilities.dp(100.0f);
        windowLayoutParams2.height = AndroidUtilities.dp(150.0f);
        wm.addView(instance.windowRemoveTooltipView, windowLayoutParams2);
        WindowManager.LayoutParams windowLayoutParams3 = createWindowLayoutParams(context);
        GroupCallPip groupCallPip = instance;
        groupCallPip.windowLayoutParams = windowLayoutParams3;
        wm.addView(groupCallPip.windowView, windowLayoutParams3);
        WindowManager.LayoutParams windowLayoutParams4 = createWindowLayoutParams(context);
        windowLayoutParams4.gravity = 81;
        windowLayoutParams4.width = AndroidUtilities.dp(100.0f);
        windowLayoutParams4.height = AndroidUtilities.dp(150.0f);
        wm.addView(instance.windowRemoveTooltipOverlayView, windowLayoutParams4);
        instance.windowRemoveTooltipView.setVisibility(8);
        instance.windowView.setScaleX(0.5f);
        instance.windowView.setScaleY(0.5f);
        instance.windowView.setAlpha(0.0f);
        instance.windowView.animate().alpha(0.7f).scaleY(1.0f).scaleX(1.0f).setDuration(350L).setInterpolator(new OvershootInterpolator()).start();
        NotificationCenter.getInstance(instance.currentAccount).addObserver(instance, NotificationCenter.groupCallUpdated);
        NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
    }

    private void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    public void setPosition(float xRelative, float yRelative) {
        float paddingHorizontal = -AndroidUtilities.dp(36.0f);
        float w = AndroidUtilities.displaySize.x - (2.0f * paddingHorizontal);
        this.windowLayoutParams.x = (int) (((w - AndroidUtilities.dp(105.0f)) * xRelative) + paddingHorizontal);
        this.windowLayoutParams.y = (int) ((AndroidUtilities.displaySize.y - AndroidUtilities.dp(105.0f)) * yRelative);
        updateAvatarsPosition();
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static void finish() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip != null) {
            groupCallPip.showAlert(false);
            GroupCallPip groupCallPip2 = instance;
            final WindowManager windowManager = groupCallPip2.windowManager;
            final View windowView = groupCallPip2.windowView;
            final View windowRemoveTooltipView = groupCallPip2.windowRemoveTooltipView;
            final View windowRemoveTooltipOverlayView = groupCallPip2.windowRemoveTooltipOverlayView;
            final View alert = groupCallPip2.alertContainer;
            groupCallPip2.windowView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupCallPip.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (windowView.getParent() != null) {
                        windowView.setVisibility(8);
                        windowRemoveTooltipView.setVisibility(8);
                        windowRemoveTooltipOverlayView.setVisibility(8);
                        windowManager.removeView(windowView);
                        windowManager.removeView(windowRemoveTooltipView);
                        windowManager.removeView(windowRemoveTooltipOverlayView);
                        windowManager.removeView(alert);
                    }
                }
            }).start();
            instance.onDestroy();
            instance = null;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    private static WindowManager.LayoutParams createWindowLayoutParams(Context context) {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.height = AndroidUtilities.dp(105.0f);
        windowLayoutParams.width = AndroidUtilities.dp(105.0f);
        windowLayoutParams.gravity = 51;
        windowLayoutParams.format = -3;
        if (AndroidUtilities.checkInlinePermissions(context)) {
            if (Build.VERSION.SDK_INT >= 26) {
                windowLayoutParams.type = 2038;
            } else {
                windowLayoutParams.type = 2003;
            }
        } else {
            windowLayoutParams.type = 99;
        }
        windowLayoutParams.flags = LaunchActivity.SCREEN_CAPTURE_REQUEST_CODE;
        return windowLayoutParams;
    }

    void showRemoveTooltip(boolean show) {
        if (this.animateToShowRemoveTooltip != show) {
            this.animateToShowRemoveTooltip = show;
            AnimatorSet animatorSet = this.showRemoveAnimator;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.showRemoveAnimator.cancel();
            }
            if (show) {
                if (this.windowRemoveTooltipView.getVisibility() != 0) {
                    this.windowRemoveTooltipView.setVisibility(0);
                    this.removeTooltipView.setAlpha(0.0f);
                    this.removeTooltipView.setScaleX(0.5f);
                    this.removeTooltipView.setScaleY(0.5f);
                    this.deleteIcon.setCurrentFrame(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.showRemoveAnimator = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, this.removeTooltipView.getAlpha(), 1.0f), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, this.removeTooltipView.getScaleX(), 1.0f), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, this.removeTooltipView.getScaleY(), 1.0f));
                this.showRemoveAnimator.setDuration(150L).start();
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.showRemoveAnimator = animatorSet3;
            animatorSet3.playTogether(ObjectAnimator.ofFloat(this.removeTooltipView, View.ALPHA, this.removeTooltipView.getAlpha(), 0.0f), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_X, this.removeTooltipView.getScaleX(), 0.5f), ObjectAnimator.ofFloat(this.removeTooltipView, View.SCALE_Y, this.removeTooltipView.getScaleY(), 0.5f));
            this.showRemoveAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupCallPip.11
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    GroupCallPip.this.windowRemoveTooltipView.setVisibility(8);
                    GroupCallPip.this.animateToPrepareRemove = false;
                    GroupCallPip.this.prepareToRemoveProgress = 0.0f;
                }
            });
            this.showRemoveAnimator.setDuration(150L);
            this.showRemoveAnimator.start();
        }
    }

    void prepareToRemove(boolean prepare) {
        if (this.animateToPrepareRemove != prepare) {
            this.animateToPrepareRemove = prepare;
            this.removeTooltipView.invalidate();
            if (!this.removed) {
                this.deleteIcon.setCustomEndFrame(prepare ? 33 : 0);
                this.iconView.playAnimation();
            }
            if (prepare) {
                this.button.performHapticFeedback(3, 2);
            }
        }
        this.button.prepareToRemove(prepare);
    }

    void pinnedToCenter(final boolean pinned) {
        if (!this.removed && this.animateToPinnedToCenter != pinned) {
            this.animateToPinnedToCenter = pinned;
            ValueAnimator valueAnimator = this.pinAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.pinAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.pinnedProgress;
            fArr[1] = pinned ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.pinAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.GroupCallPip$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    GroupCallPip.this.m2657lambda$pinnedToCenter$3$orgtelegramuiComponentsGroupCallPip(valueAnimator2);
                }
            });
            this.pinAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupCallPip.12
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (GroupCallPip.this.removed) {
                        return;
                    }
                    GroupCallPip.this.pinnedProgress = pinned ? 1.0f : 0.0f;
                    GroupCallPip.this.button.setPinnedProgress(GroupCallPip.this.pinnedProgress);
                    GroupCallPip.this.windowView.setScaleX(1.0f - (GroupCallPip.this.pinnedProgress * 0.6f));
                    GroupCallPip.this.windowView.setScaleY(1.0f - (GroupCallPip.this.pinnedProgress * 0.6f));
                    if (GroupCallPip.this.moving) {
                        GroupCallPip.this.updateButtonPosition();
                    }
                }
            });
            this.pinAnimator.setDuration(250L);
            this.pinAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.pinAnimator.start();
        }
    }

    /* renamed from: lambda$pinnedToCenter$3$org-telegram-ui-Components-GroupCallPip */
    public /* synthetic */ void m2657lambda$pinnedToCenter$3$orgtelegramuiComponentsGroupCallPip(ValueAnimator valueAnimator) {
        if (this.removed) {
            return;
        }
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinnedProgress = floatValue;
        this.button.setPinnedProgress(floatValue);
        this.windowView.setScaleX(1.0f - (this.pinnedProgress * 0.6f));
        this.windowView.setScaleY(1.0f - (this.pinnedProgress * 0.6f));
        if (this.moving) {
            updateButtonPosition();
        }
    }

    public void updateButtonPosition() {
        float cxRemove = ((this.windowLeft - this.windowOffsetLeft) + (this.windowRemoveTooltipView.getMeasuredWidth() / 2.0f)) - (this.windowView.getMeasuredWidth() / 2.0f);
        float cyRemove = (((this.windowTop - this.windowOffsetTop) + (this.windowRemoveTooltipView.getMeasuredHeight() / 2.0f)) - (this.windowView.getMeasuredHeight() / 2.0f)) - AndroidUtilities.dp(25.0f);
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        float f = this.windowX;
        float f2 = this.pinnedProgress;
        layoutParams.x = (int) ((f * (1.0f - f2)) + (f2 * cxRemove));
        WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
        float f3 = this.windowY;
        float f4 = this.pinnedProgress;
        layoutParams2.y = (int) ((f3 * (1.0f - f4)) + (f4 * cyRemove));
        updateAvatarsPosition();
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public void updateAvatarsPosition() {
        int parentWidth = AndroidUtilities.displaySize.x;
        float x = Math.min(Math.max(this.windowLayoutParams.x, -AndroidUtilities.dp(36.0f)), (parentWidth - this.windowView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
        if (x < 0.0f) {
            this.avatarsImageView.setTranslationX(Math.abs(x) / 3.0f);
        } else if (x > parentWidth - this.windowView.getMeasuredWidth()) {
            this.avatarsImageView.setTranslationX((-Math.abs(x - (parentWidth - this.windowView.getMeasuredWidth()))) / 3.0f);
        } else {
            this.avatarsImageView.setTranslationX(0.0f);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.groupCallUpdated || id == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            updateAvatars(true);
        } else if (id == NotificationCenter.didEndCall) {
            updateVisibility(ApplicationLoader.applicationContext);
        }
    }

    public void getRelativePosition(float x, float y, float[] point) {
        float width = AndroidUtilities.displaySize.x;
        float height = AndroidUtilities.displaySize.y;
        float paddingHorizontal = -AndroidUtilities.dp(36.0f);
        point[0] = (x - paddingHorizontal) / ((width - (2.0f * paddingHorizontal)) - AndroidUtilities.dp(105.0f));
        point[1] = y / (height - AndroidUtilities.dp(105.0f));
        point[0] = Math.min(1.0f, Math.max(0.0f, point[0]));
        point[1] = Math.min(1.0f, Math.max(0.0f, point[1]));
    }

    public static void updateVisibility(Context context) {
        boolean visible;
        VoIPService service = VoIPService.getSharedInstance();
        boolean groupCall = false;
        if (service != null && service.groupCall != null && !service.isHangingUp()) {
            groupCall = true;
        }
        if (!AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            visible = false;
        } else {
            visible = groupCall && !forceRemoved && (ApplicationLoader.mainInterfaceStopped || !GroupCallActivity.groupCallUiVisible);
        }
        if (visible) {
            show(context, service.getAccount());
            instance.showAvatars(true);
            return;
        }
        finish();
    }

    private void showAvatars(boolean show) {
        boolean isShowing = this.avatarsImageView.getTag() != null;
        if (show != isShowing) {
            Integer num = null;
            this.avatarsImageView.animate().setListener(null).cancel();
            if (show) {
                if (this.avatarsImageView.getVisibility() != 0) {
                    this.avatarsImageView.setVisibility(0);
                    this.avatarsImageView.setAlpha(0.0f);
                    this.avatarsImageView.setScaleX(0.5f);
                    this.avatarsImageView.setScaleY(0.5f);
                }
                this.avatarsImageView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
            } else {
                this.avatarsImageView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupCallPip.13
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        GroupCallPip.this.avatarsImageView.setVisibility(8);
                    }
                }).start();
            }
            AvatarsImageView avatarsImageView = this.avatarsImageView;
            if (show) {
                num = 1;
            }
            avatarsImageView.setTag(num);
        }
    }

    public static void clearForce() {
        forceRemoved = false;
    }

    public static boolean checkInlinePermissions() {
        if (Build.VERSION.SDK_INT < 23 || ApplicationLoader.canDrawOverlays) {
            return true;
        }
        return false;
    }
}
