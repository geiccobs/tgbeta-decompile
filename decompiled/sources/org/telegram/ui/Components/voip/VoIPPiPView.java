package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPFragment;
/* loaded from: classes5.dex */
public class VoIPPiPView implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    public static final int ANIMATION_ENTER_TYPE_NONE = 3;
    public static final int ANIMATION_ENTER_TYPE_SCALE = 0;
    public static final int ANIMATION_ENTER_TYPE_TRANSITION = 1;
    private static final float SCALE_EXPANDED = 0.4f;
    private static final float SCALE_NORMAL = 0.25f;
    public static int bottomInset;
    private static VoIPPiPView expandedInstance;
    private static VoIPPiPView instance;
    public static boolean switchingToPip = false;
    public static int topInset;
    ValueAnimator animatorToCameraMini;
    boolean callingUserIsVideo;
    public final VoIPTextureView callingUserTextureView;
    ImageView closeIcon;
    private int currentAccount;
    boolean currentUserIsVideo;
    public final VoIPTextureView currentUserTextureView;
    ImageView enlargeIcon;
    ValueAnimator expandAnimator;
    public boolean expanded;
    private boolean expandedAnimationInProgress;
    FloatingView floatingView;
    AnimatorSet moveToBoundsAnimator;
    boolean moving;
    public final int parentHeight;
    public final int parentWidth;
    float progressToCameraMini;
    long startTime;
    float startX;
    float startY;
    View topShadow;
    public WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    public FrameLayout windowView;
    public int xOffset;
    public int yOffset;
    ValueAnimator.AnimatorUpdateListener animatorToCameraMiniUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPPiPView$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPPiPView.this.m3265lambda$new$0$orgtelegramuiComponentsvoipVoIPPiPView(valueAnimator);
        }
    };
    float[] point = new float[2];
    int animationIndex = -1;
    Runnable collapseRunnable = new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPPiPView.1
        @Override // java.lang.Runnable
        public void run() {
            if (VoIPPiPView.instance == null) {
                return;
            }
            VoIPPiPView.instance.floatingView.expand(false);
        }
    };
    private ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPPiPView.2
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float x = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView.this.windowLayoutParams.x = (int) x;
            if (VoIPPiPView.this.windowView.getParent() != null) {
                VoIPPiPView.this.windowManager.updateViewLayout(VoIPPiPView.this.windowView, VoIPPiPView.this.windowLayoutParams);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPPiPView.3
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float y = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            VoIPPiPView.this.windowLayoutParams.y = (int) y;
            if (VoIPPiPView.this.windowView.getParent() != null) {
                VoIPPiPView.this.windowManager.updateViewLayout(VoIPPiPView.this.windowView, VoIPPiPView.this.windowLayoutParams);
            }
        }
    };

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-VoIPPiPView */
    public /* synthetic */ void m3265lambda$new$0$orgtelegramuiComponentsvoipVoIPPiPView(ValueAnimator valueAnimator) {
        this.progressToCameraMini = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingView.invalidate();
    }

    public static void show(Activity activity, int account, int parentWidth, int parentHeight, int animationType) {
        WindowManager wm;
        if (instance != null || VideoCapturerDevice.eglBase == null) {
            return;
        }
        WindowManager.LayoutParams windowLayoutParams = createWindowLayoutParams(activity, parentWidth, parentHeight, 0.25f);
        instance = new VoIPPiPView(activity, parentWidth, parentHeight, false);
        if (AndroidUtilities.checkInlinePermissions(activity)) {
            wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        } else {
            wm = (WindowManager) activity.getSystemService("window");
        }
        VoIPPiPView voIPPiPView = instance;
        voIPPiPView.currentAccount = account;
        voIPPiPView.windowManager = wm;
        voIPPiPView.windowLayoutParams = windowLayoutParams;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0);
        float x = preferences.getFloat("relativeX", 1.0f);
        float y = preferences.getFloat("relativeY", 0.0f);
        instance.setRelativePosition(x, y);
        NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
        wm.addView(instance.windowView, windowLayoutParams);
        instance.currentUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
        instance.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
        if (animationType == 0) {
            instance.windowView.setScaleX(0.5f);
            instance.windowView.setScaleY(0.5f);
            instance.windowView.setAlpha(0.0f);
            instance.windowView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).start();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().setSinks(instance.currentUserTextureView.renderer, instance.callingUserTextureView.renderer);
            }
        } else if (animationType == 1) {
            instance.windowView.setAlpha(0.0f);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().setBackgroundSinks(instance.currentUserTextureView.renderer, instance.callingUserTextureView.renderer);
            }
        }
    }

    public static WindowManager.LayoutParams createWindowLayoutParams(Context context, int parentWidth, int parentHeight, float scale) {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        int topPadding = ((int) (((parentHeight * 0.4f) * 1.05f) - (parentHeight * 0.4f))) / 2;
        int leftPadding = ((int) (((parentWidth * 0.4f) * 1.05f) - (parentWidth * 0.4f))) / 2;
        windowLayoutParams.height = (int) ((parentHeight * scale) + (topPadding * 2));
        windowLayoutParams.width = (int) ((parentWidth * scale) + (leftPadding * 2));
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
        if (Build.VERSION.SDK_INT >= 21) {
            windowLayoutParams.flags |= Integer.MIN_VALUE;
        }
        windowLayoutParams.flags = 16778120;
        return windowLayoutParams;
    }

    public static void prepareForTransition() {
        if (expandedInstance != null) {
            instance.expandAnimator.cancel();
        }
    }

    public static void finish() {
        if (switchingToPip) {
            return;
        }
        VoIPPiPView voIPPiPView = expandedInstance;
        if (voIPPiPView != null) {
            voIPPiPView.finishInternal();
        }
        VoIPPiPView voIPPiPView2 = instance;
        if (voIPPiPView2 != null) {
            voIPPiPView2.finishInternal();
        }
        expandedInstance = null;
        instance = null;
    }

    public static boolean isExpanding() {
        return instance.expanded;
    }

    private void setRelativePosition(float x, float y) {
        float width = AndroidUtilities.displaySize.x;
        float height = AndroidUtilities.displaySize.y;
        float leftPadding = AndroidUtilities.dp(16.0f);
        float rightPadding = AndroidUtilities.dp(16.0f);
        float topPadding = AndroidUtilities.dp(60.0f);
        float bottomPadding = AndroidUtilities.dp(16.0f);
        float widthNormal = this.parentWidth * 0.25f;
        float heightNormal = this.parentHeight * 0.25f;
        float floatingWidth = this.floatingView.getMeasuredWidth() == 0 ? widthNormal : this.floatingView.getMeasuredWidth();
        float floatingHeight = this.floatingView.getMeasuredWidth() == 0 ? heightNormal : this.floatingView.getMeasuredHeight();
        this.windowLayoutParams.x = (int) (((((width - leftPadding) - rightPadding) - floatingWidth) * x) - (this.xOffset - leftPadding));
        this.windowLayoutParams.y = (int) (((((height - topPadding) - bottomPadding) - floatingHeight) * y) - (this.yOffset - topPadding));
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static VoIPPiPView getInstance() {
        VoIPPiPView voIPPiPView = expandedInstance;
        if (voIPPiPView != null) {
            return voIPPiPView;
        }
        return instance;
    }

    public VoIPPiPView(final Context context, int parentWidth, int parentHeight, boolean expanded) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.yOffset = ((int) (((parentHeight * 0.4f) * 1.05f) - (parentHeight * 0.4f))) / 2;
        this.xOffset = ((int) (((parentWidth * 0.4f) * 1.05f) - (parentWidth * 0.4f))) / 2;
        final Drawable outerDrawable = ContextCompat.getDrawable(context, R.drawable.calls_pip_outershadow);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.voip.VoIPPiPView.4
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                canvas.save();
                canvas.scale(VoIPPiPView.this.floatingView.getScaleX(), VoIPPiPView.this.floatingView.getScaleY(), VoIPPiPView.this.floatingView.getLeft() + VoIPPiPView.this.floatingView.getPivotX(), VoIPPiPView.this.floatingView.getTop() + VoIPPiPView.this.floatingView.getPivotY());
                outerDrawable.setBounds(VoIPPiPView.this.floatingView.getLeft() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getTop() - AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getRight() + AndroidUtilities.dp(2.0f), VoIPPiPView.this.floatingView.getBottom() + AndroidUtilities.dp(2.0f));
                outerDrawable.draw(canvas);
                canvas.restore();
                super.onDraw(canvas);
            }
        };
        this.windowView = frameLayout;
        frameLayout.setWillNotDraw(false);
        FrameLayout frameLayout2 = this.windowView;
        int i = this.xOffset;
        int i2 = this.yOffset;
        frameLayout2.setPadding(i, i2, i, i2);
        this.floatingView = new FloatingView(context);
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true);
        this.callingUserTextureView = voIPTextureView;
        voIPTextureView.scaleType = VoIPTextureView.SCALE_TYPE_NONE;
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context, false, true);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setMirror(true);
        this.floatingView.addView(voIPTextureView);
        this.floatingView.addView(voIPTextureView2);
        this.floatingView.setBackgroundColor(-7829368);
        this.windowView.addView(this.floatingView);
        this.windowView.setClipChildren(false);
        this.windowView.setClipToPadding(false);
        if (expanded) {
            View view = new View(context);
            this.topShadow = view;
            view.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ColorUtils.setAlphaComponent(-16777216, 76), 0}));
            this.floatingView.addView(this.topShadow, -1, AndroidUtilities.dp(60.0f));
            ImageView imageView = new ImageView(context);
            this.closeIcon = imageView;
            imageView.setImageResource(R.drawable.pip_close);
            this.closeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.closeIcon.setContentDescription(LocaleController.getString("Close", R.string.Close));
            this.floatingView.addView(this.closeIcon, LayoutHelper.createFrame(40, 40.0f, 53, 4.0f, 4.0f, 4.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.enlargeIcon = imageView2;
            imageView2.setImageResource(R.drawable.pip_enlarge);
            this.enlargeIcon.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.enlargeIcon.setContentDescription(LocaleController.getString("Open", R.string.Open));
            this.floatingView.addView(this.enlargeIcon, LayoutHelper.createFrame(40, 40.0f, 51, 4.0f, 4.0f, 4.0f, 0.0f));
            this.closeIcon.setOnClickListener(VoIPPiPView$$ExternalSyntheticLambda2.INSTANCE);
            this.enlargeIcon.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.VoIPPiPView$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    VoIPPiPView.this.m3266lambda$new$2$orgtelegramuiComponentsvoipVoIPPiPView(context, view2);
                }
            });
        }
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.registerStateListener(this);
        }
        updateViewState();
    }

    public static /* synthetic */ void lambda$new$1(View v) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.hangUp();
        } else {
            finish();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-VoIPPiPView */
    public /* synthetic */ void m3266lambda$new$2$orgtelegramuiComponentsvoipVoIPPiPView(Context context, View v) {
        if ((context instanceof LaunchActivity) && !ApplicationLoader.mainInterfacePaused) {
            VoIPFragment.show((Activity) context, this.currentAccount);
        } else if (context instanceof LaunchActivity) {
            Intent intent = new Intent(context, LaunchActivity.class);
            intent.setAction("voip");
            context.startActivity(intent);
        }
    }

    public void finishInternal() {
        this.currentUserTextureView.renderer.release();
        this.callingUserTextureView.renderer.release();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
        this.windowView.setVisibility(8);
        if (this.windowView.getParent() != null) {
            this.floatingView.getRelativePosition(this.point);
            float x = Math.min(1.0f, Math.max(0.0f, this.point[0]));
            float y = Math.min(1.0f, Math.max(0.0f, this.point[1]));
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("voippipconfig", 0);
            preferences.edit().putFloat("relativeX", x).putFloat("relativeY", y).apply();
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onStateChanged(int state) {
        if (state == 11 || state == 17 || state == 4 || state == 10) {
            AndroidUtilities.runOnUIThread(VoIPPiPView$$ExternalSyntheticLambda3.INSTANCE, 200L);
        }
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            finish();
        } else if (state == 3 && !service.isVideoAvailable()) {
            finish();
        } else {
            updateViewState();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onSignalBarsCountChanged(int count) {
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onAudioSettingsChanged() {
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onMediaStateUpdated(int audioState, int videoState) {
        updateViewState();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraSwitch(boolean isFrontFace) {
        updateViewState();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onVideoAvailableChange(boolean isAvailable) {
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onScreenOnChange(boolean screenOn) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            return;
        }
        if (!screenOn && this.currentUserIsVideo) {
            service.setVideoState(false, 1);
        } else if (screenOn && service.getVideoState(false) == 1) {
            service.setVideoState(false, 2);
        }
    }

    private void updateViewState() {
        boolean animated = this.floatingView.getMeasuredWidth() != 0;
        boolean callingUserWasVideo = this.callingUserIsVideo;
        VoIPService service = VoIPService.getSharedInstance();
        float f = 1.0f;
        if (service != null) {
            this.callingUserIsVideo = service.getRemoteVideoState() == 2;
            this.currentUserIsVideo = service.getVideoState(false) == 2 || service.getVideoState(false) == 1;
            this.currentUserTextureView.renderer.setMirror(service.isFrontFaceCamera());
            this.currentUserTextureView.setIsScreencast(service.isScreencast());
            this.currentUserTextureView.setScreenshareMiniProgress(1.0f, false);
        }
        if (!animated) {
            if (!this.callingUserIsVideo) {
                f = 0.0f;
            }
            this.progressToCameraMini = f;
        } else if (callingUserWasVideo != this.callingUserIsVideo) {
            ValueAnimator valueAnimator = this.animatorToCameraMini;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.progressToCameraMini;
            if (!this.callingUserIsVideo) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.animatorToCameraMini = ofFloat;
            ofFloat.addUpdateListener(this.animatorToCameraMiniUpdater);
            this.animatorToCameraMini.setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animatorToCameraMini.start();
        }
    }

    public void onTransitionEnd() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().swapSinks();
        }
    }

    public void onPause() {
        if (this.windowLayoutParams.type == 99) {
            VoIPService service = VoIPService.getSharedInstance();
            if (this.currentUserIsVideo) {
                service.setVideoState(false, 1);
            }
        }
    }

    public void onResume() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.getVideoState(false) == 1) {
            service.setVideoState(false, 2);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didEndCall) {
            finish();
        }
    }

    /* loaded from: classes5.dex */
    public class FloatingView extends FrameLayout {
        float bottomPadding;
        float leftPadding;
        float rightPadding;
        float topPadding;
        float touchSlop;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FloatingView(Context context) {
            super(context);
            VoIPPiPView.this = r3;
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.1
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f / view.getScaleX()) * AndroidUtilities.dp(4.0f));
                    }
                });
                setClipToOutline(true);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.leftPadding = AndroidUtilities.dp(16.0f);
            this.rightPadding = AndroidUtilities.dp(16.0f);
            this.topPadding = AndroidUtilities.dp(60.0f);
            this.bottomPadding = AndroidUtilities.dp(16.0f);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            VoIPPiPView.this.currentUserTextureView.setPivotX(VoIPPiPView.this.callingUserTextureView.getMeasuredWidth());
            VoIPPiPView.this.currentUserTextureView.setPivotY(VoIPPiPView.this.callingUserTextureView.getMeasuredHeight());
            VoIPPiPView.this.currentUserTextureView.setTranslationX((-AndroidUtilities.dp(4.0f)) * (1.0f / getScaleX()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setTranslationY((-AndroidUtilities.dp(4.0f)) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setRoundCorners(AndroidUtilities.dp(8.0f) * (1.0f / getScaleY()) * VoIPPiPView.this.progressToCameraMini);
            VoIPPiPView.this.currentUserTextureView.setScaleX(((1.0f - VoIPPiPView.this.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView.this.currentUserTextureView.setScaleY(((1.0f - VoIPPiPView.this.progressToCameraMini) * 0.6f) + 0.4f);
            VoIPPiPView.this.currentUserTextureView.setAlpha(Math.min(1.0f, 1.0f - VoIPPiPView.this.progressToCameraMini));
            super.dispatchDraw(canvas);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            WindowManager.LayoutParams layoutParams;
            WindowManager.LayoutParams layoutParams2;
            if (VoIPPiPView.this.expandedAnimationInProgress || VoIPPiPView.switchingToPip || VoIPPiPView.instance == null) {
                return false;
            }
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            float x = event.getRawX();
            float y = event.getRawY();
            ViewParent parent = getParent();
            switch (event.getAction()) {
                case 0:
                    VoIPPiPView.this.startX = x;
                    VoIPPiPView.this.startY = y;
                    VoIPPiPView.this.startTime = System.currentTimeMillis();
                    if (VoIPPiPView.this.moveToBoundsAnimator != null) {
                        VoIPPiPView.this.moveToBoundsAnimator.cancel();
                        break;
                    }
                    break;
                case 1:
                case 3:
                    if (VoIPPiPView.this.moveToBoundsAnimator != null) {
                        VoIPPiPView.this.moveToBoundsAnimator.cancel();
                    }
                    if (event.getAction() == 1 && !VoIPPiPView.this.moving && System.currentTimeMillis() - VoIPPiPView.this.startTime < 150) {
                        Context context = getContext();
                        if ((context instanceof LaunchActivity) && !ApplicationLoader.mainInterfacePaused) {
                            VoIPFragment.show((Activity) context, VoIPPiPView.this.currentAccount);
                        } else if (context instanceof LaunchActivity) {
                            Intent intent = new Intent(context, LaunchActivity.class);
                            intent.setAction("voip");
                            context.startActivity(intent);
                        }
                        VoIPPiPView.this.moving = false;
                        return false;
                    }
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                        int parentWidth = AndroidUtilities.displaySize.x;
                        int parentHeight = AndroidUtilities.displaySize.y + VoIPPiPView.topInset;
                        float maxTop = this.topPadding;
                        float maxBottom = this.bottomPadding;
                        float left = VoIPPiPView.this.windowLayoutParams.x + VoIPPiPView.this.floatingView.getLeft();
                        float right = VoIPPiPView.this.floatingView.getMeasuredWidth() + left;
                        float top = VoIPPiPView.this.windowLayoutParams.y + VoIPPiPView.this.floatingView.getTop();
                        float bottom = VoIPPiPView.this.floatingView.getMeasuredHeight() + top;
                        VoIPPiPView.this.moveToBoundsAnimator = new AnimatorSet();
                        if (left < this.leftPadding) {
                            ValueAnimator animator = ValueAnimator.ofFloat(VoIPPiPView.this.windowLayoutParams.x, this.leftPadding - VoIPPiPView.this.floatingView.getLeft());
                            animator.addUpdateListener(VoIPPiPView.this.updateXlistener);
                            VoIPPiPView.this.moveToBoundsAnimator.playTogether(animator);
                        } else if (right > parentWidth - this.rightPadding) {
                            ValueAnimator animator2 = ValueAnimator.ofFloat(VoIPPiPView.this.windowLayoutParams.x, (parentWidth - VoIPPiPView.this.floatingView.getRight()) - this.rightPadding);
                            animator2.addUpdateListener(VoIPPiPView.this.updateXlistener);
                            VoIPPiPView.this.moveToBoundsAnimator.playTogether(animator2);
                        }
                        if (top < maxTop) {
                            ValueAnimator animator3 = ValueAnimator.ofFloat(VoIPPiPView.this.windowLayoutParams.y, maxTop - VoIPPiPView.this.floatingView.getTop());
                            animator3.addUpdateListener(VoIPPiPView.this.updateYlistener);
                            VoIPPiPView.this.moveToBoundsAnimator.playTogether(animator3);
                        } else if (bottom > parentHeight - maxBottom) {
                            ValueAnimator animator4 = ValueAnimator.ofFloat(VoIPPiPView.this.windowLayoutParams.y, (parentHeight - VoIPPiPView.this.floatingView.getMeasuredHeight()) - maxBottom);
                            animator4.addUpdateListener(VoIPPiPView.this.updateYlistener);
                            VoIPPiPView.this.moveToBoundsAnimator.playTogether(animator4);
                        }
                        VoIPPiPView.this.moveToBoundsAnimator.setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                        VoIPPiPView.this.moveToBoundsAnimator.start();
                    }
                    VoIPPiPView.this.moving = false;
                    if (VoIPPiPView.instance.expanded) {
                        AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000L);
                        break;
                    }
                    break;
                case 2:
                    float dx = x - VoIPPiPView.this.startX;
                    float dy = y - VoIPPiPView.this.startY;
                    if (!VoIPPiPView.this.moving) {
                        float f = (dx * dx) + (dy * dy);
                        float f2 = this.touchSlop;
                        if (f > f2 * f2) {
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                            VoIPPiPView.this.moving = true;
                            VoIPPiPView.this.startX = x;
                            VoIPPiPView.this.startY = y;
                            dx = 0.0f;
                            dy = 0.0f;
                        }
                    }
                    if (VoIPPiPView.this.moving) {
                        VoIPPiPView.this.windowLayoutParams.x = (int) (layoutParams.x + dx);
                        VoIPPiPView.this.windowLayoutParams.y = (int) (layoutParams2.y + dy);
                        VoIPPiPView.this.startX = x;
                        VoIPPiPView.this.startY = y;
                        if (VoIPPiPView.this.windowView.getParent() != null) {
                            VoIPPiPView.this.windowManager.updateViewLayout(VoIPPiPView.this.windowView, VoIPPiPView.this.windowLayoutParams);
                            break;
                        }
                    }
                    break;
            }
            return true;
        }

        public void getRelativePosition(float[] point) {
            float width = AndroidUtilities.displaySize.x;
            float height = AndroidUtilities.displaySize.y;
            float f = this.leftPadding;
            point[0] = ((VoIPPiPView.this.windowLayoutParams.x + VoIPPiPView.this.floatingView.getLeft()) - f) / (((width - f) - this.rightPadding) - VoIPPiPView.this.floatingView.getMeasuredWidth());
            float f2 = this.topPadding;
            point[1] = ((VoIPPiPView.this.windowLayoutParams.y + VoIPPiPView.this.floatingView.getTop()) - f2) / (((height - f2) - this.bottomPadding) - VoIPPiPView.this.floatingView.getMeasuredHeight());
            point[0] = Math.min(1.0f, Math.max(0.0f, point[0]));
            point[1] = Math.min(1.0f, Math.max(0.0f, point[1]));
        }

        public void expand(boolean expanded) {
            AndroidUtilities.cancelRunOnUIThread(VoIPPiPView.this.collapseRunnable);
            if (VoIPPiPView.instance != null && !VoIPPiPView.this.expandedAnimationInProgress && VoIPPiPView.instance.expanded != expanded) {
                VoIPPiPView.instance.expanded = expanded;
                float widthNormal = (VoIPPiPView.this.parentWidth * 0.25f) + (VoIPPiPView.this.xOffset * 2);
                float heightNormal = (VoIPPiPView.this.parentHeight * 0.25f) + (VoIPPiPView.this.yOffset * 2);
                float widthExpanded = (VoIPPiPView.this.parentWidth * 0.4f) + (VoIPPiPView.this.xOffset * 2);
                float heightExpanded = (VoIPPiPView.this.parentHeight * 0.4f) + (VoIPPiPView.this.yOffset * 2);
                VoIPPiPView.this.expandedAnimationInProgress = true;
                if (expanded) {
                    WindowManager.LayoutParams layoutParams = VoIPPiPView.createWindowLayoutParams(VoIPPiPView.instance.windowView.getContext(), VoIPPiPView.this.parentWidth, VoIPPiPView.this.parentHeight, 0.4f);
                    final VoIPPiPView pipViewExpanded = new VoIPPiPView(getContext(), VoIPPiPView.this.parentWidth, VoIPPiPView.this.parentHeight, true);
                    getRelativePosition(VoIPPiPView.this.point);
                    float cX = VoIPPiPView.this.point[0];
                    float cY = VoIPPiPView.this.point[1];
                    layoutParams.x = (int) (VoIPPiPView.this.windowLayoutParams.x - ((widthExpanded - widthNormal) * cX));
                    layoutParams.y = (int) (VoIPPiPView.this.windowLayoutParams.y - ((heightExpanded - heightNormal) * cY));
                    VoIPPiPView.this.windowManager.addView(pipViewExpanded.windowView, layoutParams);
                    pipViewExpanded.windowView.setAlpha(1.0f);
                    pipViewExpanded.windowLayoutParams = layoutParams;
                    pipViewExpanded.windowManager = VoIPPiPView.this.windowManager;
                    VoIPPiPView unused = VoIPPiPView.expandedInstance = pipViewExpanded;
                    swapRender(VoIPPiPView.instance, VoIPPiPView.expandedInstance);
                    final float scale = VoIPPiPView.this.floatingView.getScaleX() * 0.625f;
                    pipViewExpanded.floatingView.setPivotX(VoIPPiPView.this.parentWidth * cX * 0.4f);
                    pipViewExpanded.floatingView.setPivotY(VoIPPiPView.this.parentHeight * cY * 0.4f);
                    pipViewExpanded.floatingView.setScaleX(scale);
                    pipViewExpanded.floatingView.setScaleY(scale);
                    VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPPiPView.FloatingView.this.m3267x8bc833f(scale, pipViewExpanded);
                        }
                    }, 64L);
                } else if (VoIPPiPView.expandedInstance != null) {
                    VoIPPiPView.expandedInstance.floatingView.getRelativePosition(VoIPPiPView.this.point);
                    float cX2 = VoIPPiPView.this.point[0];
                    float cY2 = VoIPPiPView.this.point[1];
                    VoIPPiPView.instance.windowLayoutParams.x = (int) (VoIPPiPView.expandedInstance.windowLayoutParams.x + ((widthExpanded - widthNormal) * cX2));
                    VoIPPiPView.instance.windowLayoutParams.y = (int) (VoIPPiPView.expandedInstance.windowLayoutParams.y + ((heightExpanded - heightNormal) * cY2));
                    final float scale2 = VoIPPiPView.this.floatingView.getScaleX() * 0.625f;
                    VoIPPiPView.expandedInstance.floatingView.setPivotX(VoIPPiPView.this.parentWidth * cX2 * 0.4f);
                    VoIPPiPView.expandedInstance.floatingView.setPivotY(VoIPPiPView.this.parentHeight * cY2 * 0.4f);
                    showUi(false);
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            VoIPPiPView.FloatingView.lambda$expand$2(scale2, valueAnimator2);
                        }
                    });
                    valueAnimator.setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    valueAnimator.addListener(new AnonymousClass3(expanded));
                    valueAnimator.start();
                    VoIPPiPView.this.expandAnimator = valueAnimator;
                }
            }
        }

        /* renamed from: lambda$expand$1$org-telegram-ui-Components-voip-VoIPPiPView$FloatingView */
        public /* synthetic */ void m3267x8bc833f(final float scale, final VoIPPiPView pipViewExpanded) {
            if (VoIPPiPView.expandedInstance == null) {
                return;
            }
            VoIPPiPView.this.windowView.setAlpha(0.0f);
            try {
                VoIPPiPView.this.windowManager.removeView(VoIPPiPView.this.windowView);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            animate().cancel();
            showUi(true);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    VoIPPiPView.FloatingView.lambda$expand$0(scale, r2, pipViewExpanded, valueAnimator2);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPPiPView.FloatingView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    VoIPPiPView.this.expandedAnimationInProgress = false;
                }
            });
            valueAnimator.setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            valueAnimator.start();
            VoIPPiPView.this.expandAnimator = valueAnimator;
        }

        public static /* synthetic */ void lambda$expand$0(float scale, float animateToScale, VoIPPiPView pipViewExpanded, ValueAnimator a) {
            float v = ((Float) a.getAnimatedValue()).floatValue();
            float sc = ((1.0f - v) * scale) + (animateToScale * v);
            pipViewExpanded.floatingView.setScaleX(sc);
            pipViewExpanded.floatingView.setScaleY(sc);
            pipViewExpanded.floatingView.invalidate();
            pipViewExpanded.windowView.invalidate();
            if (Build.VERSION.SDK_INT >= 21) {
                pipViewExpanded.floatingView.invalidateOutline();
            }
        }

        public static /* synthetic */ void lambda$expand$2(float scale, ValueAnimator a) {
            float v = ((Float) a.getAnimatedValue()).floatValue();
            float sc = (1.0f - v) + (scale * v);
            if (VoIPPiPView.expandedInstance != null) {
                VoIPPiPView.expandedInstance.floatingView.setScaleX(sc);
                VoIPPiPView.expandedInstance.floatingView.setScaleY(sc);
                VoIPPiPView.expandedInstance.floatingView.invalidate();
                if (Build.VERSION.SDK_INT >= 21) {
                    VoIPPiPView.expandedInstance.floatingView.invalidateOutline();
                }
                VoIPPiPView.expandedInstance.windowView.invalidate();
            }
        }

        /* renamed from: org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3 */
        /* loaded from: classes5.dex */
        public class AnonymousClass3 extends AnimatorListenerAdapter {
            final /* synthetic */ boolean val$expanded;

            AnonymousClass3(boolean z) {
                FloatingView.this = this$1;
                this.val$expanded = z;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (VoIPPiPView.expandedInstance != null) {
                    FloatingView.this.swapRender(VoIPPiPView.expandedInstance, VoIPPiPView.instance);
                    VoIPPiPView.instance.windowView.setAlpha(1.0f);
                    VoIPPiPView.this.windowManager.addView(VoIPPiPView.instance.windowView, VoIPPiPView.instance.windowLayoutParams);
                    final boolean z = this.val$expanded;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPPiPView$FloatingView$3$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPPiPView.FloatingView.AnonymousClass3.this.m3268xbeabb793(z);
                        }
                    }, 64L);
                }
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-voip-VoIPPiPView$FloatingView$3 */
            public /* synthetic */ void m3268xbeabb793(boolean expanded) {
                if (VoIPPiPView.instance != null && VoIPPiPView.expandedInstance != null) {
                    VoIPPiPView.expandedInstance.windowView.setAlpha(0.0f);
                    VoIPPiPView.expandedInstance.finishInternal();
                    VoIPPiPView.this.expandedAnimationInProgress = false;
                    if (expanded) {
                        AndroidUtilities.runOnUIThread(VoIPPiPView.this.collapseRunnable, 3000L);
                    }
                }
            }
        }

        private void showUi(boolean show) {
            if (VoIPPiPView.expandedInstance == null) {
                return;
            }
            float f = 0.0f;
            if (show) {
                VoIPPiPView.expandedInstance.topShadow.setAlpha(0.0f);
                VoIPPiPView.expandedInstance.closeIcon.setAlpha(0.0f);
                VoIPPiPView.expandedInstance.enlargeIcon.setAlpha(0.0f);
            }
            VoIPPiPView.expandedInstance.topShadow.animate().alpha(show ? 1.0f : 0.0f).setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            VoIPPiPView.expandedInstance.closeIcon.animate().alpha(show ? 1.0f : 0.0f).setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ViewPropertyAnimator animate = VoIPPiPView.expandedInstance.enlargeIcon.animate();
            if (show) {
                f = 1.0f;
            }
            animate.alpha(f).setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }

        public void swapRender(VoIPPiPView from, VoIPPiPView to) {
            to.currentUserTextureView.setStub(from.currentUserTextureView);
            to.callingUserTextureView.setStub(from.callingUserTextureView);
            from.currentUserTextureView.renderer.release();
            from.callingUserTextureView.renderer.release();
            if (VideoCapturerDevice.eglBase == null) {
                return;
            }
            to.currentUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
            to.callingUserTextureView.renderer.init(VideoCapturerDevice.eglBase.getEglBaseContext(), null);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().setSinks(to.currentUserTextureView.renderer, to.callingUserTextureView.renderer);
            }
        }
    }
}
