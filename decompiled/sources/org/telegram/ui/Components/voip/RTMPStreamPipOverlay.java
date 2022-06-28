package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.LaunchActivity;
import org.webrtc.RendererCommon;
/* loaded from: classes5.dex */
public class RTMPStreamPipOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final float ROUNDED_CORNERS_DP = 10.0f;
    private static final float SIDE_PADDING_DP = 16.0f;
    private AccountInstance accountInstance;
    private Float aspectRatio;
    private BackupImageView avatarImageView;
    private TLRPC.TL_groupCallParticipant boundParticipant;
    private boolean boundPresentation;
    private View consumingChild;
    private FrameLayout contentFrameLayout;
    private ViewGroup contentView;
    private FrameLayout controlsView;
    private boolean firstFrameRendered;
    private View flickerView;
    private GestureDetectorCompat gestureDetector;
    private boolean isScrollDisallowed;
    private boolean isScrolling;
    private boolean isShowingControls;
    private boolean isVisible;
    private int pipHeight;
    private int pipWidth;
    private float pipX;
    private SpringAnimation pipXSpring;
    private float pipY;
    private SpringAnimation pipYSpring;
    private boolean postedDismissControls;
    private ValueAnimator scaleAnimator;
    private ScaleGestureDetector scaleGestureDetector;
    private VoIPTextureView textureView;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", RTMPStreamPipOverlay$$ExternalSyntheticLambda5.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda7.INSTANCE);
    private static final FloatPropertyCompat<RTMPStreamPipOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", RTMPStreamPipOverlay$$ExternalSyntheticLambda6.INSTANCE, RTMPStreamPipOverlay$$ExternalSyntheticLambda8.INSTANCE);
    private static RTMPStreamPipOverlay instance = new RTMPStreamPipOverlay();
    private float minScaleFactor = 0.6f;
    private float maxScaleFactor = 1.4f;
    private CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
    private boolean placeholderShown = true;
    private float scaleFactor = 1.0f;
    private Runnable dismissControlsCallback = new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            RTMPStreamPipOverlay.this.m3254xc9eadd9b();
        }
    };

    public static /* synthetic */ void lambda$static$1(RTMPStreamPipOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipX = value;
        layoutParams.x = (int) value;
        obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
    }

    public static /* synthetic */ void lambda$static$3(RTMPStreamPipOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipY = value;
        layoutParams.y = (int) value;
        obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-voip-RTMPStreamPipOverlay */
    public /* synthetic */ void m3254xc9eadd9b() {
        this.isShowingControls = false;
        toggleControls(false);
        this.postedDismissControls = false;
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    public int getSuggestedWidth() {
        if (getRatio() >= 1.0f) {
            return (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.35f);
        }
        return (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.6f);
    }

    public int getSuggestedHeight() {
        return (int) (getSuggestedWidth() * getRatio());
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            float ratio = 0.5625f;
            if (VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.isEmpty()) {
                ChatObject.VideoParticipant videoParticipant = VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.get(0);
                if (videoParticipant.aspectRatio != 0.0f) {
                    ratio = 1.0f / videoParticipant.aspectRatio;
                }
            }
            this.aspectRatio = Float.valueOf(ratio);
            this.maxScaleFactor = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(32.0f)) / getSuggestedWidth();
        }
        return this.aspectRatio.floatValue();
    }

    public void toggleControls(boolean show) {
        float[] fArr = new float[2];
        float f = 0.0f;
        fArr[0] = show ? 0.0f : 1.0f;
        if (show) {
            f = 1.0f;
        }
        fArr[1] = f;
        ValueAnimator duration = ValueAnimator.ofFloat(fArr).setDuration(200L);
        this.scaleAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RTMPStreamPipOverlay.this.m3255x4e63256(valueAnimator);
            }
        });
        this.scaleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RTMPStreamPipOverlay.this.scaleAnimator = null;
            }
        });
        this.scaleAnimator.start();
    }

    /* renamed from: lambda$toggleControls$5$org-telegram-ui-Components-voip-RTMPStreamPipOverlay */
    public /* synthetic */ void m3255x4e63256(ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        this.controlsView.setAlpha(value);
    }

    public static void dismiss() {
        instance.dismissInternal();
    }

    private void dismissInternal() {
        if (!this.isVisible) {
            return;
        }
        this.isVisible = false;
        AndroidUtilities.runOnUIThread(RTMPStreamPipOverlay$$ExternalSyntheticLambda4.INSTANCE, 100L);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
        ValueAnimator valueAnimator = this.scaleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.postedDismissControls) {
            AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
            this.postedDismissControls = false;
        }
        AnimatorSet set = new AnimatorSet();
        set.setDuration(250L);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.playTogether(ObjectAnimator.ofFloat(this.contentView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, 0.1f));
        set.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                RTMPStreamPipOverlay.this.windowManager.removeViewImmediate(RTMPStreamPipOverlay.this.contentView);
                RTMPStreamPipOverlay.this.textureView.renderer.release();
                RTMPStreamPipOverlay.this.boundParticipant = null;
                RTMPStreamPipOverlay.this.placeholderShown = true;
                RTMPStreamPipOverlay.this.firstFrameRendered = false;
                RTMPStreamPipOverlay.this.consumingChild = null;
                RTMPStreamPipOverlay.this.isScrolling = false;
            }
        });
        set.start();
    }

    public static void show() {
        instance.showInternal();
    }

    private void showInternal() {
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null && !this.isVisible) {
            this.isVisible = true;
            AccountInstance accountInstance = VoIPService.getSharedInstance().groupCall.currentAccount;
            this.accountInstance = accountInstance;
            accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
            this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
            this.pipWidth = getSuggestedWidth();
            this.pipHeight = getSuggestedHeight();
            this.scaleFactor = 1.0f;
            this.isShowingControls = false;
            this.pipXSpring = new SpringAnimation(this, PIP_X_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f));
            this.pipYSpring = new SpringAnimation(this, PIP_Y_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f));
            final Context context = ApplicationLoader.applicationContext;
            final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.scaleGestureDetector = new ScaleGestureDetector(context, new AnonymousClass3());
            if (Build.VERSION.SDK_INT >= 19) {
                this.scaleGestureDetector.setQuickScaleEnabled(false);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                this.scaleGestureDetector.setStylusScaleEnabled(false);
            }
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.4
                private float startPipX;
                private float startPipY;

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onDown(MotionEvent e) {
                    if (RTMPStreamPipOverlay.this.isShowingControls) {
                        for (int i = 1; i < RTMPStreamPipOverlay.this.contentFrameLayout.getChildCount(); i++) {
                            View child = RTMPStreamPipOverlay.this.contentFrameLayout.getChildAt(i);
                            boolean consumed = child.dispatchTouchEvent(e);
                            if (consumed) {
                                RTMPStreamPipOverlay.this.consumingChild = child;
                                return true;
                            }
                        }
                    }
                    this.startPipX = RTMPStreamPipOverlay.this.pipX;
                    this.startPipY = RTMPStreamPipOverlay.this.pipY;
                    return true;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onSingleTapUp(MotionEvent e) {
                    if (RTMPStreamPipOverlay.this.scaleAnimator != null) {
                        return true;
                    }
                    if (RTMPStreamPipOverlay.this.postedDismissControls) {
                        AndroidUtilities.cancelRunOnUIThread(RTMPStreamPipOverlay.this.dismissControlsCallback);
                        RTMPStreamPipOverlay.this.postedDismissControls = false;
                    }
                    RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
                    rTMPStreamPipOverlay.isShowingControls = !rTMPStreamPipOverlay.isShowingControls;
                    RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
                    rTMPStreamPipOverlay2.toggleControls(rTMPStreamPipOverlay2.isShowingControls);
                    if (RTMPStreamPipOverlay.this.isShowingControls && !RTMPStreamPipOverlay.this.postedDismissControls) {
                        AndroidUtilities.runOnUIThread(RTMPStreamPipOverlay.this.dismissControlsCallback, 2500L);
                        RTMPStreamPipOverlay.this.postedDismissControls = true;
                    }
                    return true;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (RTMPStreamPipOverlay.this.isScrolling && !RTMPStreamPipOverlay.this.isScrollDisallowed) {
                        RTMPStreamPipOverlay.this.pipXSpring.setStartVelocity(velocityX).setStartValue(RTMPStreamPipOverlay.this.pipX).getSpring().setFinalPosition((RTMPStreamPipOverlay.this.pipX + (((float) RTMPStreamPipOverlay.this.pipWidth) / 2.0f)) + (velocityX / 7.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP));
                        RTMPStreamPipOverlay.this.pipXSpring.start();
                        RTMPStreamPipOverlay.this.pipYSpring.setStartVelocity(velocityX).setStartValue(RTMPStreamPipOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY + (velocityY / 10.0f), AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP)));
                        RTMPStreamPipOverlay.this.pipYSpring.start();
                        return true;
                    }
                    return false;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (!RTMPStreamPipOverlay.this.isScrolling && RTMPStreamPipOverlay.this.scaleAnimator == null && !RTMPStreamPipOverlay.this.isScrollDisallowed && (Math.abs(distanceX) >= touchSlop || Math.abs(distanceY) >= touchSlop)) {
                        RTMPStreamPipOverlay.this.isScrolling = true;
                        RTMPStreamPipOverlay.this.pipXSpring.cancel();
                        RTMPStreamPipOverlay.this.pipYSpring.cancel();
                    }
                    if (RTMPStreamPipOverlay.this.isScrolling) {
                        RTMPStreamPipOverlay.this.windowLayoutParams.x = (int) RTMPStreamPipOverlay.this.pipX = (this.startPipX + e2.getRawX()) - e1.getRawX();
                        RTMPStreamPipOverlay.this.windowLayoutParams.y = (int) RTMPStreamPipOverlay.this.pipY = (this.startPipY + e2.getRawY()) - e1.getRawY();
                        RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
                    }
                    return true;
                }
            });
            this.contentFrameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.5
                private Path path = new Path();

                @Override // android.view.ViewGroup, android.view.View
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    int action = ev.getAction();
                    if (RTMPStreamPipOverlay.this.consumingChild != null) {
                        MotionEvent newEvent = MotionEvent.obtain(ev);
                        newEvent.offsetLocation(RTMPStreamPipOverlay.this.consumingChild.getX(), RTMPStreamPipOverlay.this.consumingChild.getY());
                        boolean consumed = RTMPStreamPipOverlay.this.consumingChild.dispatchTouchEvent(ev);
                        newEvent.recycle();
                        if (action == 1 || action == 3) {
                            RTMPStreamPipOverlay.this.consumingChild = null;
                        }
                        if (consumed) {
                            return true;
                        }
                    }
                    MotionEvent temp = MotionEvent.obtain(ev);
                    temp.offsetLocation(ev.getRawX() - ev.getX(), ev.getRawY() - ev.getY());
                    boolean scaleDetector = RTMPStreamPipOverlay.this.scaleGestureDetector.onTouchEvent(temp);
                    temp.recycle();
                    boolean detector = !RTMPStreamPipOverlay.this.scaleGestureDetector.isInProgress() && RTMPStreamPipOverlay.this.gestureDetector.onTouchEvent(ev);
                    if (action == 1 || action == 3) {
                        RTMPStreamPipOverlay.this.isScrolling = false;
                        RTMPStreamPipOverlay.this.isScrollDisallowed = false;
                        if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                            RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX).getSpring().setFinalPosition(RTMPStreamPipOverlay.this.pipX + (((float) RTMPStreamPipOverlay.this.pipWidth) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP));
                            RTMPStreamPipOverlay.this.pipXSpring.start();
                        }
                        if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                            RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(RTMPStreamPipOverlay.this.pipY, AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP)));
                            RTMPStreamPipOverlay.this.pipYSpring.start();
                        }
                    }
                    return scaleDetector || detector;
                }

                @Override // android.view.View
                protected void onConfigurationChanged(Configuration newConfig) {
                    AndroidUtilities.checkDisplaySize(getContext(), newConfig);
                    RTMPStreamPipOverlay.this.bindTextureView();
                }

                @Override // android.view.View
                public void draw(Canvas canvas) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        super.draw(canvas);
                        return;
                    }
                    canvas.save();
                    canvas.clipPath(this.path);
                    super.draw(canvas);
                    canvas.restore();
                }

                @Override // android.view.View
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    this.path.rewind();
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, w, h);
                    this.path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), Path.Direction.CW);
                }
            };
            ViewGroup viewGroup = new ViewGroup(context) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.6
                @Override // android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    RTMPStreamPipOverlay.this.contentFrameLayout.layout(0, 0, RTMPStreamPipOverlay.this.pipWidth, RTMPStreamPipOverlay.this.pipHeight);
                }

                @Override // android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                    RTMPStreamPipOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(RTMPStreamPipOverlay.this.pipHeight, C.BUFFER_FLAG_ENCRYPTED));
                }
            };
            this.contentView = viewGroup;
            viewGroup.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.7
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(10.0f));
                    }
                });
                this.contentFrameLayout.setClipToOutline(true);
            }
            this.contentFrameLayout.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_actionBar));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            this.contentFrameLayout.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
            VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, false, false, false);
            this.textureView = voIPTextureView;
            voIPTextureView.setAlpha(0.0f);
            this.textureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
            this.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FILL;
            this.textureView.renderer.setRotateTextureWithScreen(true);
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new AnonymousClass8());
            this.contentFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
            View view = new View(context) { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.9
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (getAlpha() == 0.0f) {
                        return;
                    }
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth(), getHeight());
                    RTMPStreamPipOverlay.this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, AndroidUtilities.dp(10.0f), null);
                    invalidate();
                }

                @Override // android.view.View
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    RTMPStreamPipOverlay.this.cellFlickerDrawable.setParentWidth(w);
                }
            };
            this.flickerView = view;
            this.contentFrameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.controlsView = frameLayout;
            frameLayout.setAlpha(0.0f);
            View scrim = new View(context);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColors(new int[]{1140850688, 0});
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            scrim.setBackground(gradientDrawable);
            this.controlsView.addView(scrim, LayoutHelper.createFrame(-1, -1.0f));
            int padding = AndroidUtilities.dp(8.0f);
            ImageView closeButton = new ImageView(context);
            closeButton.setImageResource(R.drawable.pip_video_close);
            closeButton.setColorFilter(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
            closeButton.setPadding(padding, padding, padding, padding);
            closeButton.setOnClickListener(RTMPStreamPipOverlay$$ExternalSyntheticLambda2.INSTANCE);
            this.controlsView.addView(closeButton, LayoutHelper.createFrame(38, 38, 5, 0.0f, 4, 4, 0.0f));
            ImageView expandButton = new ImageView(context);
            expandButton.setImageResource(R.drawable.pip_video_expand);
            expandButton.setColorFilter(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            expandButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
            expandButton.setPadding(padding, padding, padding, padding);
            expandButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    RTMPStreamPipOverlay.lambda$showInternal$8(context, view2);
                }
            });
            this.controlsView.addView(expandButton, LayoutHelper.createFrame(38, 38, 5, 0.0f, 4, 38 + 4 + 6, 0.0f));
            this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
            this.windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams();
            this.windowLayoutParams = createWindowLayoutParams;
            createWindowLayoutParams.width = this.pipWidth;
            this.windowLayoutParams.height = this.pipHeight;
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            float dp = (AndroidUtilities.displaySize.x - this.pipWidth) - AndroidUtilities.dp(SIDE_PADDING_DP);
            this.pipX = dp;
            layoutParams.x = (int) dp;
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            float dp2 = (AndroidUtilities.displaySize.y - this.pipHeight) - AndroidUtilities.dp(SIDE_PADDING_DP);
            this.pipY = dp2;
            layoutParams2.y = (int) dp2;
            this.windowLayoutParams.dimAmount = 0.0f;
            this.windowLayoutParams.flags = LaunchActivity.SCREEN_CAPTURE_REQUEST_CODE;
            this.contentView.setAlpha(0.0f);
            this.contentView.setScaleX(0.1f);
            this.contentView.setScaleY(0.1f);
            this.windowManager.addView(this.contentView, this.windowLayoutParams);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(250L);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.playTogether(ObjectAnimator.ofFloat(this.contentView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, 1.0f));
            set.start();
            bindTextureView();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    /* renamed from: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$3 */
    /* loaded from: classes5.dex */
    public class AnonymousClass3 implements ScaleGestureDetector.OnScaleGestureListener {
        AnonymousClass3() {
            RTMPStreamPipOverlay.this = this$0;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
            rTMPStreamPipOverlay.scaleFactor = MathUtils.clamp(rTMPStreamPipOverlay.scaleFactor * detector.getScaleFactor(), RTMPStreamPipOverlay.this.minScaleFactor, RTMPStreamPipOverlay.this.maxScaleFactor);
            RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
            rTMPStreamPipOverlay2.pipWidth = (int) (rTMPStreamPipOverlay2.getSuggestedWidth() * RTMPStreamPipOverlay.this.scaleFactor);
            RTMPStreamPipOverlay rTMPStreamPipOverlay3 = RTMPStreamPipOverlay.this;
            rTMPStreamPipOverlay3.pipHeight = (int) (rTMPStreamPipOverlay3.getSuggestedHeight() * RTMPStreamPipOverlay.this.scaleFactor);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RTMPStreamPipOverlay.AnonymousClass3.this.m3256xe5a0dec3();
                }
            });
            RTMPStreamPipOverlay.this.pipXSpring.setStartValue(RTMPStreamPipOverlay.this.pipX).getSpring().setFinalPosition(detector.getFocusX() >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - RTMPStreamPipOverlay.this.pipWidth) - AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP));
            if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                RTMPStreamPipOverlay.this.pipXSpring.start();
            }
            RTMPStreamPipOverlay.this.pipYSpring.setStartValue(RTMPStreamPipOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(detector.getFocusY() - (RTMPStreamPipOverlay.this.pipHeight / 2.0f), AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - RTMPStreamPipOverlay.this.pipHeight) - AndroidUtilities.dp(RTMPStreamPipOverlay.SIDE_PADDING_DP)));
            if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                RTMPStreamPipOverlay.this.pipYSpring.start();
                return true;
            }
            return true;
        }

        /* renamed from: lambda$onScale$0$org-telegram-ui-Components-voip-RTMPStreamPipOverlay$3 */
        public /* synthetic */ void m3256xe5a0dec3() {
            RTMPStreamPipOverlay.this.contentFrameLayout.invalidate();
            if (Build.VERSION.SDK_INT < 18 || !RTMPStreamPipOverlay.this.contentFrameLayout.isInLayout()) {
                RTMPStreamPipOverlay.this.contentFrameLayout.requestLayout();
                RTMPStreamPipOverlay.this.contentView.requestLayout();
                RTMPStreamPipOverlay.this.textureView.requestLayout();
            }
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (RTMPStreamPipOverlay.this.isScrolling) {
                RTMPStreamPipOverlay.this.isScrolling = false;
            }
            RTMPStreamPipOverlay.this.isScrollDisallowed = true;
            RTMPStreamPipOverlay.this.windowLayoutParams.width = (int) (RTMPStreamPipOverlay.this.getSuggestedWidth() * RTMPStreamPipOverlay.this.maxScaleFactor);
            RTMPStreamPipOverlay.this.windowLayoutParams.height = (int) (RTMPStreamPipOverlay.this.getSuggestedHeight() * RTMPStreamPipOverlay.this.maxScaleFactor);
            RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (RTMPStreamPipOverlay.this.pipXSpring.isRunning() || RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                final List<SpringAnimation> springs = new ArrayList<>();
                DynamicAnimation.OnAnimationEndListener endListener = new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay.3.1
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        animation.removeEndListener(this);
                        springs.add((SpringAnimation) animation);
                        if (springs.size() == 2) {
                            AnonymousClass3.this.updateLayout();
                        }
                    }
                };
                if (!RTMPStreamPipOverlay.this.pipXSpring.isRunning()) {
                    springs.add(RTMPStreamPipOverlay.this.pipXSpring);
                } else {
                    RTMPStreamPipOverlay.this.pipXSpring.addEndListener(endListener);
                }
                if (!RTMPStreamPipOverlay.this.pipYSpring.isRunning()) {
                    springs.add(RTMPStreamPipOverlay.this.pipYSpring);
                    return;
                } else {
                    RTMPStreamPipOverlay.this.pipYSpring.addEndListener(endListener);
                    return;
                }
            }
            updateLayout();
        }

        public void updateLayout() {
            RTMPStreamPipOverlay rTMPStreamPipOverlay = RTMPStreamPipOverlay.this;
            WindowManager.LayoutParams layoutParams = rTMPStreamPipOverlay.windowLayoutParams;
            int suggestedWidth = (int) (RTMPStreamPipOverlay.this.getSuggestedWidth() * RTMPStreamPipOverlay.this.scaleFactor);
            layoutParams.width = suggestedWidth;
            rTMPStreamPipOverlay.pipWidth = suggestedWidth;
            RTMPStreamPipOverlay rTMPStreamPipOverlay2 = RTMPStreamPipOverlay.this;
            WindowManager.LayoutParams layoutParams2 = rTMPStreamPipOverlay2.windowLayoutParams;
            int suggestedHeight = (int) (RTMPStreamPipOverlay.this.getSuggestedHeight() * RTMPStreamPipOverlay.this.scaleFactor);
            layoutParams2.height = suggestedHeight;
            rTMPStreamPipOverlay2.pipHeight = suggestedHeight;
            RTMPStreamPipOverlay.this.windowManager.updateViewLayout(RTMPStreamPipOverlay.this.contentView, RTMPStreamPipOverlay.this.windowLayoutParams);
        }
    }

    /* renamed from: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$8 */
    /* loaded from: classes5.dex */
    public class AnonymousClass8 implements RendererCommon.RendererEvents {
        AnonymousClass8() {
            RTMPStreamPipOverlay.this = this$0;
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFirstFrameRendered() {
            RTMPStreamPipOverlay.this.firstFrameRendered = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RTMPStreamPipOverlay.AnonymousClass8.this.m3257xadca3c28();
                }
            });
        }

        /* renamed from: lambda$onFirstFrameRendered$0$org-telegram-ui-Components-voip-RTMPStreamPipOverlay$8 */
        public /* synthetic */ void m3257xadca3c28() {
            RTMPStreamPipOverlay.this.bindTextureView();
        }

        @Override // org.webrtc.RendererCommon.RendererEvents
        public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
            if ((rotation / 90) % 2 == 0) {
                RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(videoHeight / videoWidth);
            } else {
                RTMPStreamPipOverlay.this.aspectRatio = Float.valueOf(videoWidth / videoHeight);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.RTMPStreamPipOverlay$8$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RTMPStreamPipOverlay.AnonymousClass8.this.m3258xae41e31e();
                }
            });
        }

        /* renamed from: lambda$onFrameResolutionChanged$1$org-telegram-ui-Components-voip-RTMPStreamPipOverlay$8 */
        public /* synthetic */ void m3258xae41e31e() {
            RTMPStreamPipOverlay.this.bindTextureView();
        }
    }

    public static /* synthetic */ void lambda$showInternal$8(Context context, View v) {
        if (VoIPService.getSharedInstance() != null) {
            Intent intent = new Intent(context, LaunchActivity.class).setAction("voip_chat");
            intent.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            if (!(context instanceof Activity)) {
                intent.addFlags(268435456);
            }
            context.startActivity(intent);
            dismiss();
        }
    }

    public void bindTextureView() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        boolean z = true;
        if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().groupCall != null && !VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.isEmpty()) {
            TLRPC.TL_groupCallParticipant participant = VoIPService.getSharedInstance().groupCall.visibleVideoParticipants.get(0).participant;
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = this.boundParticipant;
            if (tL_groupCallParticipant2 == null || MessageObject.getPeerId(tL_groupCallParticipant2.peer) != MessageObject.getPeerId(participant.peer)) {
                if (this.boundParticipant != null) {
                    VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, this.boundPresentation);
                }
                this.boundPresentation = participant.presentation != null;
                if (participant.self) {
                    VoIPService.getSharedInstance().setSinks(this.textureView.renderer, this.boundPresentation, null);
                } else {
                    VoIPService.getSharedInstance().addRemoteSink(participant, this.boundPresentation, this.textureView.renderer, null);
                }
                MessagesController messagesController = VoIPService.getSharedInstance().groupCall.currentAccount.getMessagesController();
                long peerId = MessageObject.getPeerId(participant.peer);
                if (peerId > 0) {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(peerId));
                    ImageLocation imageLocation = ImageLocation.getForUser(user, 1);
                    int color = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color, -16777216, 0.2f), ColorUtils.blendARGB(color, -16777216, 0.4f)});
                    this.avatarImageView.getImageReceiver().setImage(imageLocation, "50_50_b", gradientDrawable, null, user, 0);
                } else {
                    TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-peerId));
                    ImageLocation imageLocation2 = ImageLocation.getForChat(chat, 1);
                    int color2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color2, -16777216, 0.2f), ColorUtils.blendARGB(color2, -16777216, 0.4f)});
                    this.avatarImageView.getImageReceiver().setImage(imageLocation2, "50_50_b", gradientDrawable2, null, chat, 0);
                }
                this.boundParticipant = participant;
            }
        } else if (this.boundParticipant != null) {
            VoIPService.getSharedInstance().removeRemoteSink(this.boundParticipant, false);
            this.boundParticipant = null;
        }
        if (this.firstFrameRendered && (tL_groupCallParticipant = this.boundParticipant) != null && ((tL_groupCallParticipant.video != null || this.boundParticipant.presentation != null) && ((this.boundParticipant.video == null || !this.boundParticipant.video.paused) && (this.boundParticipant.presentation == null || !this.boundParticipant.presentation.paused)))) {
            z = false;
        }
        boolean showPlaceholder = z;
        if (this.placeholderShown != showPlaceholder) {
            this.flickerView.animate().cancel();
            float f = 1.0f;
            this.flickerView.animate().alpha(showPlaceholder ? 1.0f : 0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.avatarImageView.animate().cancel();
            this.avatarImageView.animate().alpha(showPlaceholder ? 1.0f : 0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.textureView.animate().cancel();
            ViewPropertyAnimator animate = this.textureView.animate();
            if (showPlaceholder) {
                f = 0.0f;
            }
            animate.alpha(f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.placeholderShown = showPlaceholder;
        }
        if (this.pipWidth != getSuggestedWidth() * this.scaleFactor || this.pipHeight != getSuggestedHeight() * this.scaleFactor) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            int suggestedWidth = (int) (getSuggestedWidth() * this.scaleFactor);
            this.pipWidth = suggestedWidth;
            layoutParams.width = suggestedWidth;
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            int suggestedHeight = (int) (getSuggestedHeight() * this.scaleFactor);
            this.pipHeight = suggestedHeight;
            layoutParams2.height = suggestedHeight;
            this.windowManager.updateViewLayout(this.contentView, this.windowLayoutParams);
            this.pipXSpring.setStartValue(this.pipX).getSpring().setFinalPosition(this.pipX + ((((float) getSuggestedWidth()) * this.scaleFactor) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - (getSuggestedWidth() * this.scaleFactor)) - AndroidUtilities.dp(SIDE_PADDING_DP) : AndroidUtilities.dp(SIDE_PADDING_DP));
            this.pipXSpring.start();
            this.pipYSpring.setStartValue(this.pipY).getSpring().setFinalPosition(MathUtils.clamp(this.pipY, AndroidUtilities.dp(SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - (getSuggestedHeight() * this.scaleFactor)) - AndroidUtilities.dp(SIDE_PADDING_DP)));
            this.pipYSpring.start();
        }
    }

    private WindowManager.LayoutParams createWindowLayoutParams() {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.gravity = 51;
        windowLayoutParams.format = -3;
        if (AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
            if (Build.VERSION.SDK_INT >= 26) {
                windowLayoutParams.type = 2038;
            } else {
                windowLayoutParams.type = 2003;
            }
        } else {
            windowLayoutParams.type = 2999;
        }
        windowLayoutParams.flags = LaunchActivity.SCREEN_CAPTURE_REQUEST_CODE;
        return windowLayoutParams;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didEndCall) {
            dismiss();
        } else if (id == NotificationCenter.groupCallUpdated) {
            bindTextureView();
        }
    }
}
