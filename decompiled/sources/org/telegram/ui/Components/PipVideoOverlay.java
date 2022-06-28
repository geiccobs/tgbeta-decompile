package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class PipVideoOverlay {
    public static final boolean IS_TRANSITION_ANIMATION_SUPPORTED = true;
    public static final float ROUNDED_CORNERS_DP = 10.0f;
    private static final float SIDE_PADDING_DP = 16.0f;
    private Float aspectRatio;
    private float bufferProgress;
    private boolean canLongClick;
    private View consumingChild;
    private FrameLayout contentFrameLayout;
    private ViewGroup contentView;
    private ValueAnimator controlsAnimator;
    private FrameLayout controlsView;
    private GestureDetectorFixDoubleTap gestureDetector;
    private View innerView;
    private boolean isDismissing;
    private boolean isScrollDisallowed;
    private boolean isScrolling;
    private boolean isShowingControls;
    private boolean isVideoCompleted;
    private boolean isVisible;
    private int mVideoHeight;
    private int mVideoWidth;
    private boolean onSideToDismiss;
    private EmbedBottomSheet parentSheet;
    private PhotoViewer photoViewer;
    private PipConfig pipConfig;
    private int pipHeight;
    private int pipWidth;
    private float pipX;
    private SpringAnimation pipXSpring;
    private float pipY;
    private SpringAnimation pipYSpring;
    private ImageView playPauseButton;
    private boolean postedDismissControls;
    private ScaleGestureDetector scaleGestureDetector;
    private float videoProgress;
    private VideoProgressView videoProgressView;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private static final FloatPropertyCompat<PipVideoOverlay> PIP_X_PROPERTY = new SimpleFloatPropertyCompat("pipX", PipVideoOverlay$$ExternalSyntheticLambda2.INSTANCE, PipVideoOverlay$$ExternalSyntheticLambda4.INSTANCE);
    private static final FloatPropertyCompat<PipVideoOverlay> PIP_Y_PROPERTY = new SimpleFloatPropertyCompat("pipY", PipVideoOverlay$$ExternalSyntheticLambda3.INSTANCE, PipVideoOverlay$$ExternalSyntheticLambda5.INSTANCE);
    private static PipVideoOverlay instance = new PipVideoOverlay();
    private float minScaleFactor = 0.75f;
    private float maxScaleFactor = 1.4f;
    private float scaleFactor = 1.0f;
    private VideoForwardDrawable videoForwardDrawable = new VideoForwardDrawable(false);
    private Runnable progressRunnable = new Runnable() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda11
        @Override // java.lang.Runnable
        public final void run() {
            PipVideoOverlay.this.m2861lambda$new$4$orgtelegramuiComponentsPipVideoOverlay();
        }
    };
    private float[] longClickStartPoint = new float[2];
    private Runnable longClickCallback = new Runnable() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda13
        @Override // java.lang.Runnable
        public final void run() {
            PipVideoOverlay.this.onLongClick();
        }
    };
    private Runnable dismissControlsCallback = new Runnable() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda12
        @Override // java.lang.Runnable
        public final void run() {
            PipVideoOverlay.this.m2862lambda$new$5$orgtelegramuiComponentsPipVideoOverlay();
        }
    };

    public static /* synthetic */ void lambda$static$1(PipVideoOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipX = value;
        layoutParams.x = (int) value;
        try {
            obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
        } catch (IllegalArgumentException e) {
            obj.pipXSpring.cancel();
        }
    }

    public static /* synthetic */ void lambda$static$3(PipVideoOverlay obj, float value) {
        WindowManager.LayoutParams layoutParams = obj.windowLayoutParams;
        obj.pipY = value;
        layoutParams.y = (int) value;
        try {
            obj.windowManager.updateViewLayout(obj.contentView, obj.windowLayoutParams);
        } catch (IllegalArgumentException e) {
            obj.pipYSpring.cancel();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2861lambda$new$4$orgtelegramuiComponentsPipVideoOverlay() {
        VideoPlayer videoPlayer;
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null || (videoPlayer = photoViewer.getVideoPlayer()) == null) {
            return;
        }
        this.videoProgress = ((float) videoPlayer.getCurrentPosition()) / ((float) videoPlayer.getDuration());
        if (this.photoViewer == null) {
            this.bufferProgress = ((float) videoPlayer.getBufferedPosition()) / ((float) videoPlayer.getDuration());
        }
        this.videoProgressView.invalidate();
        AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2862lambda$new$5$orgtelegramuiComponentsPipVideoOverlay() {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer != null && photoViewer.getVideoPlayerRewinder().rewindCount > 0) {
            AndroidUtilities.runOnUIThread(this.dismissControlsCallback, 1500L);
            return;
        }
        this.isShowingControls = false;
        toggleControls(false);
        this.postedDismissControls = false;
    }

    public static void onRewindCanceled() {
        instance.onRewindCanceledInternal();
    }

    private void onRewindCanceledInternal() {
        this.videoForwardDrawable.setShowing(false);
    }

    public static void onUpdateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek) {
        instance.onUpdateRewindProgressUiInternal(timeDiff, progress, rewindByBackSeek);
    }

    public void onUpdateRewindProgressUiInternal(long timeDiff, float progress, boolean rewindByBackSeek) {
        this.videoForwardDrawable.setTime(0L);
        if (rewindByBackSeek) {
            this.videoProgress = progress;
            VideoProgressView videoProgressView = this.videoProgressView;
            if (videoProgressView != null) {
                videoProgressView.invalidate();
            }
            FrameLayout frameLayout = this.controlsView;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
        }
    }

    public static void onRewindStart(boolean rewindForward) {
        instance.onRewindStartInternal(rewindForward);
    }

    private void onRewindStartInternal(boolean rewindForward) {
        this.videoForwardDrawable.setOneShootAnimation(false);
        this.videoForwardDrawable.setLeftSide(!rewindForward);
        this.videoForwardDrawable.setShowing(true);
        VideoProgressView videoProgressView = this.videoProgressView;
        if (videoProgressView != null) {
            videoProgressView.invalidate();
        }
        FrameLayout frameLayout = this.controlsView;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public void onLongClick() {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null || photoViewer.getVideoPlayer() == null || this.isDismissing || this.isVideoCompleted || this.isScrolling || this.scaleGestureDetector.isInProgress() || !this.canLongClick) {
            return;
        }
        VideoPlayer videoPlayer = this.photoViewer.getVideoPlayer();
        boolean z = false;
        if (this.longClickStartPoint[0] >= getSuggestedWidth() * this.scaleFactor * 0.5f) {
            z = true;
        }
        boolean forward = z;
        long current = videoPlayer.getCurrentPosition();
        long total = videoPlayer.getDuration();
        if (current == C.TIME_UNSET || total < 15000) {
            return;
        }
        this.photoViewer.getVideoPlayerRewinder().startRewind(videoPlayer, forward, this.photoViewer.getCurrentVideoSpeed());
        if (!this.isShowingControls) {
            this.isShowingControls = true;
            toggleControls(true);
            if (!this.postedDismissControls) {
                AndroidUtilities.runOnUIThread(this.dismissControlsCallback, 1500L);
                this.postedDismissControls = true;
            }
        }
    }

    public PipConfig getPipConfig() {
        if (this.pipConfig == null) {
            this.pipConfig = new PipConfig(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        }
        return this.pipConfig;
    }

    public static boolean isVisible() {
        return instance.isVisible;
    }

    public int getSuggestedWidth() {
        return getSuggestedWidth(getRatio());
    }

    private static int getSuggestedWidth(float ratio) {
        if (ratio >= 1.0f) {
            return (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.35f);
        }
        return (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.6f);
    }

    public int getSuggestedHeight() {
        return getSuggestedHeight(getRatio());
    }

    private static int getSuggestedHeight(float ratio) {
        return (int) (getSuggestedWidth(ratio) * ratio);
    }

    private float getRatio() {
        if (this.aspectRatio == null) {
            this.aspectRatio = Float.valueOf(this.mVideoHeight / this.mVideoWidth);
            this.maxScaleFactor = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(32.0f)) / getSuggestedWidth();
            this.videoForwardDrawable.setPlayScaleFactor(this.aspectRatio.floatValue() < 1.0f ? 0.6f : 0.45f);
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
        this.controlsAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.controlsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PipVideoOverlay.this.m2867x6d8831f6(valueAnimator);
            }
        });
        this.controlsAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PipVideoOverlay.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PipVideoOverlay.this.controlsAnimator = null;
            }
        });
        this.controlsAnimator.start();
    }

    /* renamed from: lambda$toggleControls$6$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2867x6d8831f6(ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        this.controlsView.setAlpha(value);
    }

    public static void dimissAndDestroy() {
        PipVideoOverlay pipVideoOverlay = instance;
        EmbedBottomSheet embedBottomSheet = pipVideoOverlay.parentSheet;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        } else {
            PhotoViewer photoViewer = pipVideoOverlay.photoViewer;
            if (photoViewer != null) {
                photoViewer.destroyPhotoViewer();
            }
        }
        dismiss();
    }

    public static void dismiss() {
        dismiss(false);
    }

    public static void dismiss(boolean animate) {
        instance.dismissInternal(animate);
    }

    private void dismissInternal(boolean animate) {
        if (this.isDismissing) {
            return;
        }
        this.isDismissing = true;
        ValueAnimator valueAnimator = this.controlsAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.postedDismissControls) {
            AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
            this.postedDismissControls = false;
        }
        SpringAnimation springAnimation = this.pipXSpring;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.pipYSpring.cancel();
        }
        if (animate) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PipVideoOverlay.this.onDismissedInternal();
                }
            }, 100L);
            return;
        }
        AnimatorSet set = new AnimatorSet();
        set.setDuration(250L);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.playTogether(ObjectAnimator.ofFloat(this.contentView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, 0.1f));
        set.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PipVideoOverlay.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PipVideoOverlay.this.onDismissedInternal();
            }
        });
        set.start();
    }

    public void onDismissedInternal() {
        try {
            if (this.controlsView.getParent() != null) {
                this.windowManager.removeViewImmediate(this.contentView);
            }
        } catch (IllegalArgumentException e) {
        }
        this.videoProgressView = null;
        this.innerView = null;
        this.photoViewer = null;
        this.parentSheet = null;
        this.consumingChild = null;
        this.isScrolling = false;
        this.isVisible = false;
        this.isDismissing = false;
        this.canLongClick = false;
        cancelRewind();
        AndroidUtilities.cancelRunOnUIThread(this.longClickCallback);
    }

    public void cancelRewind() {
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer != null && photoViewer.getVideoPlayerRewinder().rewindCount > 0) {
            this.photoViewer.getVideoPlayerRewinder().cancelRewind();
        }
    }

    public static void updatePlayButton() {
        instance.updatePlayButtonInternal();
    }

    private void updatePlayButtonInternal() {
        VideoPlayer videoPlayer;
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null || (videoPlayer = photoViewer.getVideoPlayer()) == null || this.playPauseButton == null) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (!videoPlayer.isPlaying()) {
            if (this.isVideoCompleted) {
                this.playPauseButton.setImageResource(R.drawable.pip_replay_large);
                return;
            } else {
                this.playPauseButton.setImageResource(R.drawable.pip_play_large);
                return;
            }
        }
        this.playPauseButton.setImageResource(R.drawable.pip_pause_large);
        AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
    }

    public static void onVideoCompleted() {
        instance.onVideoCompletedInternal();
    }

    private void onVideoCompletedInternal() {
        VideoProgressView videoProgressView;
        if (!this.isVisible || (videoProgressView = this.videoProgressView) == null) {
            return;
        }
        this.isVideoCompleted = true;
        this.videoProgress = 0.0f;
        this.bufferProgress = 0.0f;
        if (videoProgressView != null) {
            videoProgressView.invalidate();
        }
        updatePlayButtonInternal();
        AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
        if (!this.isShowingControls) {
            toggleControls(true);
            AndroidUtilities.cancelRunOnUIThread(this.dismissControlsCallback);
        }
    }

    public static void setBufferedProgress(float progress) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.bufferProgress = progress;
        VideoProgressView videoProgressView = pipVideoOverlay.videoProgressView;
        if (videoProgressView != null) {
            videoProgressView.invalidate();
        }
    }

    public static void setParentSheet(EmbedBottomSheet parentSheet) {
        instance.parentSheet = parentSheet;
    }

    public static void setPhotoViewer(PhotoViewer photoViewer) {
        PipVideoOverlay pipVideoOverlay = instance;
        pipVideoOverlay.photoViewer = photoViewer;
        pipVideoOverlay.updatePlayButtonInternal();
    }

    public static Rect getPipRect(boolean inAnimation, float aspectRatio) {
        Rect rect = new Rect();
        float ratio = 1.0f / aspectRatio;
        PipVideoOverlay pipVideoOverlay = instance;
        if (pipVideoOverlay.isVisible && !inAnimation) {
            rect.x = pipVideoOverlay.pipX;
            rect.y = instance.pipY + AndroidUtilities.statusBarHeight;
            rect.width = instance.pipWidth;
            rect.height = instance.pipHeight;
            return rect;
        }
        float savedPipX = pipVideoOverlay.getPipConfig().getPipX();
        float savedPipY = instance.getPipConfig().getPipY();
        float scaleFactor = instance.getPipConfig().getScaleFactor();
        rect.width = getSuggestedWidth(ratio) * scaleFactor;
        rect.height = getSuggestedHeight(ratio) * scaleFactor;
        if (savedPipX != -1.0f) {
            rect.x = (rect.width / 2.0f) + savedPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - rect.width) - AndroidUtilities.dp(SIDE_PADDING_DP) : AndroidUtilities.dp(SIDE_PADDING_DP);
        } else {
            rect.x = (AndroidUtilities.displaySize.x - rect.width) - AndroidUtilities.dp(SIDE_PADDING_DP);
        }
        if (savedPipY != -1.0f) {
            rect.y = MathUtils.clamp(savedPipY, AndroidUtilities.dp(SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - AndroidUtilities.dp(SIDE_PADDING_DP)) - rect.height) + AndroidUtilities.statusBarHeight;
        } else {
            rect.y = AndroidUtilities.dp(SIDE_PADDING_DP) + AndroidUtilities.statusBarHeight;
        }
        return rect;
    }

    public static boolean show(boolean inAppOnly, Activity activity, View pipContentView, int videoWidth, int videoHeight) {
        return show(inAppOnly, activity, pipContentView, videoWidth, videoHeight, false);
    }

    public static boolean show(boolean inAppOnly, Activity activity, View pipContentView, int videoWidth, int videoHeight, boolean animate) {
        return instance.showInternal(inAppOnly, activity, pipContentView, videoWidth, videoHeight, animate);
    }

    private boolean showInternal(final boolean inAppOnly, Activity activity, View pipContentView, int videoWidth, int videoHeight, boolean animate) {
        if (this.isVisible) {
            return false;
        }
        this.isVisible = true;
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        this.aspectRatio = null;
        float savedPipX = getPipConfig().getPipX();
        float savedPipY = getPipConfig().getPipY();
        this.scaleFactor = getPipConfig().getScaleFactor();
        this.pipWidth = (int) (getSuggestedWidth() * this.scaleFactor);
        this.pipHeight = (int) (getSuggestedHeight() * this.scaleFactor);
        this.isShowingControls = false;
        this.pipXSpring = new SpringAnimation(this, PIP_X_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda9
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                PipVideoOverlay.this.m2865lambda$showInternal$7$orgtelegramuiComponentsPipVideoOverlay(dynamicAnimation, z, f, f2);
            }
        });
        this.pipYSpring = new SpringAnimation(this, PIP_Y_PROPERTY).setSpring(new SpringForce().setDampingRatio(0.75f).setStiffness(650.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda10
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                PipVideoOverlay.this.m2866lambda$showInternal$8$orgtelegramuiComponentsPipVideoOverlay(dynamicAnimation, z, f, f2);
            }
        });
        Context context = ApplicationLoader.applicationContext;
        int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.scaleGestureDetector = new ScaleGestureDetector(context, new AnonymousClass3());
        if (Build.VERSION.SDK_INT >= 19) {
            this.scaleGestureDetector.setQuickScaleEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.scaleGestureDetector.setStylusScaleEnabled(false);
        }
        this.gestureDetector = new GestureDetectorFixDoubleTap(context, new AnonymousClass4(touchSlop));
        this.contentFrameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.PipVideoOverlay.5
            private Path path = new Path();

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                int action = ev.getActionMasked();
                if (action == 0 || action == 5) {
                    if (ev.getPointerCount() == 1) {
                        PipVideoOverlay.this.canLongClick = true;
                        PipVideoOverlay.this.longClickStartPoint = new float[]{ev.getX(), ev.getY()};
                        AndroidUtilities.runOnUIThread(PipVideoOverlay.this.longClickCallback, 500L);
                    } else {
                        PipVideoOverlay.this.canLongClick = false;
                        PipVideoOverlay.this.cancelRewind();
                        AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                    }
                }
                if (action == 1 || action == 3 || action == 6) {
                    PipVideoOverlay.this.canLongClick = false;
                    PipVideoOverlay.this.cancelRewind();
                    AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
                }
                if (PipVideoOverlay.this.consumingChild != null) {
                    MotionEvent newEvent = MotionEvent.obtain(ev);
                    newEvent.offsetLocation(PipVideoOverlay.this.consumingChild.getX(), PipVideoOverlay.this.consumingChild.getY());
                    boolean consumed = PipVideoOverlay.this.consumingChild.dispatchTouchEvent(ev);
                    newEvent.recycle();
                    if (action == 1 || action == 3 || action == 6) {
                        PipVideoOverlay.this.consumingChild = null;
                    }
                    if (consumed) {
                        return true;
                    }
                }
                MotionEvent temp = MotionEvent.obtain(ev);
                temp.offsetLocation(ev.getRawX() - ev.getX(), ev.getRawY() - ev.getY());
                boolean scaleDetector = PipVideoOverlay.this.scaleGestureDetector.onTouchEvent(temp);
                temp.recycle();
                boolean detector = !PipVideoOverlay.this.scaleGestureDetector.isInProgress() && PipVideoOverlay.this.gestureDetector.onTouchEvent(ev);
                if (action == 1 || action == 3 || action == 6) {
                    PipVideoOverlay.this.isScrolling = false;
                    PipVideoOverlay.this.isScrollDisallowed = false;
                    if (PipVideoOverlay.this.onSideToDismiss) {
                        PipVideoOverlay.this.onSideToDismiss = false;
                        PipVideoOverlay.dimissAndDestroy();
                    } else {
                        if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                            PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX).getSpring().setFinalPosition(PipVideoOverlay.this.pipX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP));
                            PipVideoOverlay.this.pipXSpring.start();
                        }
                        if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                            PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY, AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP)));
                            PipVideoOverlay.this.pipYSpring.start();
                        }
                    }
                }
                return scaleDetector || detector;
            }

            @Override // android.view.View
            protected void onConfigurationChanged(Configuration newConfig) {
                AndroidUtilities.checkDisplaySize(getContext(), newConfig);
                PipVideoOverlay.this.pipConfig = null;
                if (PipVideoOverlay.this.pipWidth != PipVideoOverlay.this.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor || PipVideoOverlay.this.pipHeight != PipVideoOverlay.this.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor) {
                    WindowManager.LayoutParams layoutParams = PipVideoOverlay.this.windowLayoutParams;
                    PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                    layoutParams.width = pipVideoOverlay.pipWidth = (int) (pipVideoOverlay.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor);
                    WindowManager.LayoutParams layoutParams2 = PipVideoOverlay.this.windowLayoutParams;
                    PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                    layoutParams2.height = pipVideoOverlay2.pipHeight = (int) (pipVideoOverlay2.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor);
                    PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                    PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX).getSpring().setFinalPosition(PipVideoOverlay.this.pipX + ((((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor) / 2.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - (PipVideoOverlay.this.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor)) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP));
                    PipVideoOverlay.this.pipXSpring.start();
                    PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY, AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - (PipVideoOverlay.this.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor)) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP)));
                    PipVideoOverlay.this.pipYSpring.start();
                }
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
        ViewGroup viewGroup = new ViewGroup(context) { // from class: org.telegram.ui.Components.PipVideoOverlay.6
            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                PipVideoOverlay.this.contentFrameLayout.layout(0, 0, PipVideoOverlay.this.pipWidth, PipVideoOverlay.this.pipHeight);
            }

            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                PipVideoOverlay.this.contentFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(PipVideoOverlay.this.pipWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(PipVideoOverlay.this.pipHeight, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.view.View
            public void draw(Canvas canvas) {
                canvas.save();
                canvas.scale(PipVideoOverlay.this.pipWidth / PipVideoOverlay.this.contentFrameLayout.getWidth(), PipVideoOverlay.this.pipHeight / PipVideoOverlay.this.contentFrameLayout.getHeight());
                super.draw(canvas);
                canvas.restore();
            }
        };
        this.contentView = viewGroup;
        viewGroup.addView(this.contentFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        if (Build.VERSION.SDK_INT >= 21) {
            this.contentFrameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.PipVideoOverlay.7
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(10.0f));
                }
            });
            this.contentFrameLayout.setClipToOutline(true);
        }
        this.contentFrameLayout.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_actionBar));
        this.innerView = pipContentView;
        if (pipContentView.getParent() != null) {
            ((ViewGroup) this.innerView.getParent()).removeView(this.innerView);
        }
        this.contentFrameLayout.addView(this.innerView, LayoutHelper.createFrame(-1, -1.0f));
        this.videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() { // from class: org.telegram.ui.Components.PipVideoOverlay.8
            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
            public void onAnimationEnd() {
            }

            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
            public void invalidate() {
                PipVideoOverlay.this.controlsView.invalidate();
            }
        });
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.PipVideoOverlay.9
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (PipVideoOverlay.this.videoForwardDrawable.isAnimating()) {
                    PipVideoOverlay.this.videoForwardDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
                    PipVideoOverlay.this.videoForwardDrawable.draw(canvas);
                }
            }
        };
        this.controlsView = frameLayout;
        frameLayout.setWillNotDraw(false);
        this.controlsView.setAlpha(0.0f);
        View scrim = new View(context);
        scrim.setBackgroundColor(1275068416);
        this.controlsView.addView(scrim, LayoutHelper.createFrame(-1, -1.0f));
        int padding = AndroidUtilities.dp(8.0f);
        ImageView closeButton = new ImageView(context);
        closeButton.setImageResource(R.drawable.pip_video_close);
        closeButton.setColorFilter(Theme.getColor(Theme.key_voipgroup_actionBarItems), PorterDuff.Mode.MULTIPLY);
        closeButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        closeButton.setPadding(padding, padding, padding, padding);
        closeButton.setOnClickListener(PipVideoOverlay$$ExternalSyntheticLambda8.INSTANCE);
        float stiffness = 4;
        this.controlsView.addView(closeButton, LayoutHelper.createFrame(38, 38, 5, 0.0f, stiffness, 4, 0.0f));
        ImageView expandButton = new ImageView(context);
        expandButton.setImageResource(R.drawable.pip_video_expand);
        expandButton.setColorFilter(Theme.getColor(Theme.key_voipgroup_actionBarItems), PorterDuff.Mode.MULTIPLY);
        expandButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        expandButton.setPadding(padding, padding, padding, padding);
        expandButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PipVideoOverlay.this.m2863x4f46a443(inAppOnly, view);
            }
        });
        this.controlsView.addView(expandButton, LayoutHelper.createFrame(38, 38, 5, 0.0f, 4, 38 + 4 + 6, 0.0f));
        ImageView imageView = new ImageView(context);
        this.playPauseButton = imageView;
        imageView.setColorFilter(Theme.getColor(Theme.key_voipgroup_actionBarItems), PorterDuff.Mode.MULTIPLY);
        this.playPauseButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        this.playPauseButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PipVideoOverlay$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PipVideoOverlay.this.m2864x12330da2(view);
            }
        });
        this.playPauseButton.setVisibility(this.innerView instanceof WebView ? 8 : 0);
        this.controlsView.addView(this.playPauseButton, LayoutHelper.createFrame(38, 38, 17));
        VideoProgressView videoProgressView = new VideoProgressView(context);
        this.videoProgressView = videoProgressView;
        this.controlsView.addView(videoProgressView, LayoutHelper.createFrame(-1, -1.0f));
        this.contentFrameLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0f));
        this.windowManager = (WindowManager) (inAppOnly ? activity : ApplicationLoader.applicationContext).getSystemService("window");
        WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(inAppOnly);
        this.windowLayoutParams = createWindowLayoutParams;
        createWindowLayoutParams.width = this.pipWidth;
        this.windowLayoutParams.height = this.pipHeight;
        if (savedPipX != -1.0f) {
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            float dp = (((float) this.pipWidth) / 2.0f) + savedPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - this.pipWidth) - AndroidUtilities.dp(SIDE_PADDING_DP) : AndroidUtilities.dp(SIDE_PADDING_DP);
            this.pipX = dp;
            layoutParams.x = (int) dp;
        } else {
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            float dp2 = (AndroidUtilities.displaySize.x - this.pipWidth) - AndroidUtilities.dp(SIDE_PADDING_DP);
            this.pipX = dp2;
            layoutParams2.x = (int) dp2;
        }
        if (savedPipY != -1.0f) {
            WindowManager.LayoutParams layoutParams3 = this.windowLayoutParams;
            float clamp = MathUtils.clamp(savedPipY, AndroidUtilities.dp(SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - AndroidUtilities.dp(SIDE_PADDING_DP)) - this.pipHeight);
            this.pipY = clamp;
            layoutParams3.y = (int) clamp;
        } else {
            WindowManager.LayoutParams layoutParams4 = this.windowLayoutParams;
            float dp3 = AndroidUtilities.dp(SIDE_PADDING_DP);
            this.pipY = dp3;
            layoutParams4.y = (int) dp3;
        }
        this.windowLayoutParams.dimAmount = 0.0f;
        this.windowLayoutParams.flags = LaunchActivity.SCREEN_CAPTURE_REQUEST_CODE;
        if (animate) {
            this.windowManager.addView(this.contentView, this.windowLayoutParams);
            return true;
        }
        this.contentView.setAlpha(0.0f);
        this.contentView.setScaleX(0.1f);
        this.contentView.setScaleY(0.1f);
        this.windowManager.addView(this.contentView, this.windowLayoutParams);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(250L);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.playTogether(ObjectAnimator.ofFloat(this.contentView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.contentView, View.SCALE_Y, 1.0f));
        set.start();
        return true;
    }

    /* renamed from: lambda$showInternal$7$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2865lambda$showInternal$7$orgtelegramuiComponentsPipVideoOverlay(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        getPipConfig().setPipX(value);
    }

    /* renamed from: lambda$showInternal$8$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2866lambda$showInternal$8$orgtelegramuiComponentsPipVideoOverlay(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        getPipConfig().setPipY(value);
    }

    /* renamed from: org.telegram.ui.Components.PipVideoOverlay$3 */
    /* loaded from: classes5.dex */
    public class AnonymousClass3 implements ScaleGestureDetector.OnScaleGestureListener {
        AnonymousClass3() {
            PipVideoOverlay.this = this$0;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            PipVideoOverlay pipVideoOverlay;
            PipVideoOverlay pipVideoOverlay2;
            PipVideoOverlay pipVideoOverlay3 = PipVideoOverlay.this;
            pipVideoOverlay3.scaleFactor = MathUtils.clamp(pipVideoOverlay3.scaleFactor * detector.getScaleFactor(), PipVideoOverlay.this.minScaleFactor, PipVideoOverlay.this.maxScaleFactor);
            PipVideoOverlay.this.pipWidth = (int) (pipVideoOverlay.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor);
            PipVideoOverlay.this.pipHeight = (int) (pipVideoOverlay2.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PipVideoOverlay$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PipVideoOverlay.AnonymousClass3.this.m2868lambda$onScale$0$orgtelegramuiComponentsPipVideoOverlay$3();
                }
            });
            float finalX = detector.getFocusX() >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP);
            if (PipVideoOverlay.this.pipXSpring.isRunning()) {
                PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(finalX);
            } else {
                PipVideoOverlay.this.pipXSpring.setStartValue(PipVideoOverlay.this.pipX).getSpring().setFinalPosition(finalX);
            }
            PipVideoOverlay.this.pipXSpring.start();
            float finalY = MathUtils.clamp(detector.getFocusY() - (PipVideoOverlay.this.pipHeight / 2.0f), AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP));
            if (PipVideoOverlay.this.pipYSpring.isRunning()) {
                PipVideoOverlay.this.pipYSpring.getSpring().setFinalPosition(finalY);
            } else {
                PipVideoOverlay.this.pipYSpring.setStartValue(PipVideoOverlay.this.pipY).getSpring().setFinalPosition(finalY);
            }
            PipVideoOverlay.this.pipYSpring.start();
            return true;
        }

        /* renamed from: lambda$onScale$0$org-telegram-ui-Components-PipVideoOverlay$3 */
        public /* synthetic */ void m2868lambda$onScale$0$orgtelegramuiComponentsPipVideoOverlay$3() {
            PipVideoOverlay.this.contentView.invalidate();
            PipVideoOverlay.this.contentFrameLayout.requestLayout();
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (PipVideoOverlay.this.isScrolling) {
                PipVideoOverlay.this.isScrolling = false;
                PipVideoOverlay.this.canLongClick = false;
                PipVideoOverlay.this.cancelRewind();
                AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
            }
            PipVideoOverlay.this.isScrollDisallowed = true;
            PipVideoOverlay.this.windowLayoutParams.width = (int) (PipVideoOverlay.this.getSuggestedWidth() * PipVideoOverlay.this.maxScaleFactor);
            PipVideoOverlay.this.windowLayoutParams.height = (int) (PipVideoOverlay.this.getSuggestedHeight() * PipVideoOverlay.this.maxScaleFactor);
            PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (PipVideoOverlay.this.pipXSpring.isRunning() || PipVideoOverlay.this.pipYSpring.isRunning()) {
                final List<SpringAnimation> springs = new ArrayList<>();
                DynamicAnimation.OnAnimationEndListener endListener = new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.PipVideoOverlay.3.1
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                        animation.removeEndListener(this);
                        springs.add((SpringAnimation) animation);
                        if (springs.size() == 2) {
                            AnonymousClass3.this.updateLayout();
                        }
                    }
                };
                if (!PipVideoOverlay.this.pipXSpring.isRunning()) {
                    springs.add(PipVideoOverlay.this.pipXSpring);
                } else {
                    PipVideoOverlay.this.pipXSpring.addEndListener(endListener);
                }
                if (!PipVideoOverlay.this.pipYSpring.isRunning()) {
                    springs.add(PipVideoOverlay.this.pipYSpring);
                    return;
                } else {
                    PipVideoOverlay.this.pipYSpring.addEndListener(endListener);
                    return;
                }
            }
            updateLayout();
        }

        public void updateLayout() {
            PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
            WindowManager.LayoutParams layoutParams = pipVideoOverlay.windowLayoutParams;
            int suggestedWidth = (int) (PipVideoOverlay.this.getSuggestedWidth() * PipVideoOverlay.this.scaleFactor);
            layoutParams.width = suggestedWidth;
            pipVideoOverlay.pipWidth = suggestedWidth;
            PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
            WindowManager.LayoutParams layoutParams2 = pipVideoOverlay2.windowLayoutParams;
            int suggestedHeight = (int) (PipVideoOverlay.this.getSuggestedHeight() * PipVideoOverlay.this.scaleFactor);
            layoutParams2.height = suggestedHeight;
            pipVideoOverlay2.pipHeight = suggestedHeight;
            try {
                PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.PipVideoOverlay$4 */
    /* loaded from: classes5.dex */
    public class AnonymousClass4 extends GestureDetectorFixDoubleTap.OnGestureListener {
        private float startPipX;
        private float startPipY;
        final /* synthetic */ int val$touchSlop;

        AnonymousClass4(int i) {
            PipVideoOverlay.this = this$0;
            this.val$touchSlop = i;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            if (PipVideoOverlay.this.isShowingControls) {
                for (int i = 1; i < PipVideoOverlay.this.contentFrameLayout.getChildCount(); i++) {
                    View child = PipVideoOverlay.this.contentFrameLayout.getChildAt(i);
                    boolean consumed = child.dispatchTouchEvent(e);
                    if (consumed) {
                        PipVideoOverlay.this.consumingChild = child;
                        return true;
                    }
                }
            }
            this.startPipX = PipVideoOverlay.this.pipX;
            this.startPipY = PipVideoOverlay.this.pipY;
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (PipVideoOverlay.this.controlsAnimator != null) {
                return true;
            }
            if (PipVideoOverlay.this.postedDismissControls) {
                AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.dismissControlsCallback);
                PipVideoOverlay.this.postedDismissControls = false;
            }
            PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
            pipVideoOverlay.isShowingControls = !pipVideoOverlay.isShowingControls;
            PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
            pipVideoOverlay2.toggleControls(pipVideoOverlay2.isShowingControls);
            if (PipVideoOverlay.this.isShowingControls && !PipVideoOverlay.this.postedDismissControls) {
                AndroidUtilities.runOnUIThread(PipVideoOverlay.this.dismissControlsCallback, 2500L);
                PipVideoOverlay.this.postedDismissControls = true;
            }
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent e) {
            long current;
            boolean z = false;
            if (PipVideoOverlay.this.photoViewer == null || PipVideoOverlay.this.photoViewer.getVideoPlayer() == null || PipVideoOverlay.this.isDismissing || PipVideoOverlay.this.isVideoCompleted || PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.scaleGestureDetector.isInProgress() || !PipVideoOverlay.this.canLongClick) {
                return false;
            }
            VideoPlayer videoPlayer = PipVideoOverlay.this.photoViewer.getVideoPlayer();
            boolean forward = e.getX() >= (((float) PipVideoOverlay.this.getSuggestedWidth()) * PipVideoOverlay.this.scaleFactor) * 0.5f;
            long current2 = videoPlayer.getCurrentPosition();
            long total = videoPlayer.getDuration();
            if (current2 == C.TIME_UNSET || total < 15000) {
                return false;
            }
            long j = 10000;
            if (forward) {
                current = current2 + 10000;
            } else {
                current = current2 - 10000;
            }
            if (current2 == current) {
                return false;
            }
            boolean apply = true;
            if (current > total) {
                current = total;
            } else if (current < 0) {
                if (current < -9000) {
                    apply = false;
                }
                current = 0;
            }
            if (apply) {
                PipVideoOverlay.this.videoForwardDrawable.setOneShootAnimation(true);
                VideoForwardDrawable videoForwardDrawable = PipVideoOverlay.this.videoForwardDrawable;
                if (!forward) {
                    z = true;
                }
                videoForwardDrawable.setLeftSide(z);
                PipVideoOverlay.this.videoForwardDrawable.addTime(10000L);
                videoPlayer.seekTo(current);
                PipVideoOverlay pipVideoOverlay = PipVideoOverlay.this;
                if (!forward) {
                    j = -10000;
                }
                pipVideoOverlay.onUpdateRewindProgressUiInternal(j, ((float) current) / ((float) total), true);
                if (!PipVideoOverlay.this.isShowingControls) {
                    PipVideoOverlay pipVideoOverlay2 = PipVideoOverlay.this;
                    pipVideoOverlay2.toggleControls(pipVideoOverlay2.isShowingControls = true);
                    if (!PipVideoOverlay.this.postedDismissControls) {
                        PipVideoOverlay.this.postedDismissControls = true;
                        AndroidUtilities.runOnUIThread(PipVideoOverlay.this.dismissControlsCallback, 2500L);
                    }
                }
            }
            return true;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            if (!hasDoubleTap()) {
                return onSingleTapConfirmed(e);
            }
            return super.onSingleTapUp(e);
        }

        @Override // org.telegram.ui.Components.GestureDetectorFixDoubleTap.OnGestureListener
        public boolean hasDoubleTap() {
            if (PipVideoOverlay.this.photoViewer == null || PipVideoOverlay.this.photoViewer.getVideoPlayer() == null || PipVideoOverlay.this.isDismissing || PipVideoOverlay.this.isVideoCompleted || PipVideoOverlay.this.isScrolling || PipVideoOverlay.this.scaleGestureDetector.isInProgress() || !PipVideoOverlay.this.canLongClick) {
                return false;
            }
            VideoPlayer videoPlayer = PipVideoOverlay.this.photoViewer.getVideoPlayer();
            long current = videoPlayer.getCurrentPosition();
            long total = videoPlayer.getDuration();
            return current != C.TIME_UNSET && total >= 15000;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (PipVideoOverlay.this.isScrolling && !PipVideoOverlay.this.isScrollDisallowed) {
                PipVideoOverlay.this.pipXSpring.setStartVelocity(velocityX).setStartValue(PipVideoOverlay.this.pipX).getSpring().setFinalPosition((PipVideoOverlay.this.pipX + (((float) PipVideoOverlay.this.pipWidth) / 2.0f)) + (velocityX / 7.0f) >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP));
                PipVideoOverlay.this.pipXSpring.start();
                PipVideoOverlay.this.pipYSpring.setStartVelocity(velocityX).setStartValue(PipVideoOverlay.this.pipY).getSpring().setFinalPosition(MathUtils.clamp(PipVideoOverlay.this.pipY + (velocityY / 10.0f), AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP), (AndroidUtilities.displaySize.y - PipVideoOverlay.this.pipHeight) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP)));
                PipVideoOverlay.this.pipYSpring.start();
                return true;
            }
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int i;
            int i2;
            if (!PipVideoOverlay.this.isScrolling && PipVideoOverlay.this.controlsAnimator == null && !PipVideoOverlay.this.isScrollDisallowed && (Math.abs(distanceX) >= this.val$touchSlop || Math.abs(distanceY) >= this.val$touchSlop)) {
                PipVideoOverlay.this.isScrolling = true;
                PipVideoOverlay.this.pipXSpring.cancel();
                PipVideoOverlay.this.pipYSpring.cancel();
                PipVideoOverlay.this.canLongClick = false;
                PipVideoOverlay.this.cancelRewind();
                AndroidUtilities.cancelRunOnUIThread(PipVideoOverlay.this.longClickCallback);
            }
            if (PipVideoOverlay.this.isScrolling) {
                float wasPipX = PipVideoOverlay.this.pipX;
                final float newPipX = (this.startPipX + e2.getRawX()) - e1.getRawX();
                PipVideoOverlay.this.pipY = (this.startPipY + e2.getRawY()) - e1.getRawY();
                if (newPipX <= (-PipVideoOverlay.this.pipWidth) * 0.25f || newPipX >= AndroidUtilities.displaySize.x - (PipVideoOverlay.this.pipWidth * 0.75f)) {
                    if (!PipVideoOverlay.this.onSideToDismiss) {
                        SpringForce spring = PipVideoOverlay.this.pipXSpring.setStartValue(wasPipX).getSpring();
                        if ((PipVideoOverlay.this.pipWidth / 2.0f) + newPipX >= AndroidUtilities.displaySize.x / 2.0f) {
                            i2 = AndroidUtilities.displaySize.x;
                            i = AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP);
                        } else {
                            i2 = AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP);
                            i = PipVideoOverlay.this.pipWidth;
                        }
                        spring.setFinalPosition(i2 - i);
                        PipVideoOverlay.this.pipXSpring.start();
                    }
                    PipVideoOverlay.this.onSideToDismiss = true;
                } else if (PipVideoOverlay.this.onSideToDismiss) {
                    if (PipVideoOverlay.this.onSideToDismiss) {
                        PipVideoOverlay.this.pipXSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.PipVideoOverlay$4$$ExternalSyntheticLambda0
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                                PipVideoOverlay.AnonymousClass4.this.m2869lambda$onScroll$0$orgtelegramuiComponentsPipVideoOverlay$4(newPipX, dynamicAnimation, z, f, f2);
                            }
                        });
                        PipVideoOverlay.this.pipXSpring.setStartValue(wasPipX).getSpring().setFinalPosition(newPipX);
                        PipVideoOverlay.this.pipXSpring.start();
                    }
                    PipVideoOverlay.this.onSideToDismiss = false;
                } else {
                    if (PipVideoOverlay.this.pipXSpring.isRunning()) {
                        PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition(newPipX);
                    } else {
                        PipVideoOverlay.this.windowLayoutParams.x = (int) PipVideoOverlay.this.pipX = newPipX;
                        PipVideoOverlay.this.getPipConfig().setPipX(newPipX);
                    }
                    PipVideoOverlay.this.windowLayoutParams.y = (int) PipVideoOverlay.this.pipY;
                    PipVideoOverlay.this.getPipConfig().setPipY(PipVideoOverlay.this.pipY);
                    PipVideoOverlay.this.windowManager.updateViewLayout(PipVideoOverlay.this.contentView, PipVideoOverlay.this.windowLayoutParams);
                }
            }
            return true;
        }

        /* renamed from: lambda$onScroll$0$org-telegram-ui-Components-PipVideoOverlay$4 */
        public /* synthetic */ void m2869lambda$onScroll$0$orgtelegramuiComponentsPipVideoOverlay$4(float newPipX, DynamicAnimation animation, boolean canceled, float value, float velocity) {
            if (!canceled) {
                PipVideoOverlay.this.pipXSpring.getSpring().setFinalPosition((((float) PipVideoOverlay.this.pipWidth) / 2.0f) + newPipX >= ((float) AndroidUtilities.displaySize.x) / 2.0f ? (AndroidUtilities.displaySize.x - PipVideoOverlay.this.pipWidth) - AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP) : AndroidUtilities.dp(PipVideoOverlay.SIDE_PADDING_DP));
            }
        }
    }

    /* renamed from: lambda$showInternal$10$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2863x4f46a443(boolean inAppOnly, View v) {
        boolean isResumedByActivityManager = true;
        if (Build.VERSION.SDK_INT >= 21) {
            ActivityManager activityManager = (ActivityManager) v.getContext().getSystemService("activity");
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
            if (!appProcessInfos.isEmpty()) {
                boolean z = false;
                if (appProcessInfos.get(0).importance == 100) {
                    z = true;
                }
                isResumedByActivityManager = z;
            }
        }
        if (!inAppOnly && (!isResumedByActivityManager || !LaunchActivity.isResumed)) {
            v.getClass();
            LaunchActivity.onResumeStaticCallback = new ChatActivityEnterView$$ExternalSyntheticLambda26(v);
            Context ctx = ApplicationLoader.applicationContext;
            Intent intent = new Intent(ctx, LaunchActivity.class);
            intent.addFlags(268435456);
            ctx.startActivity(intent);
            return;
        }
        EmbedBottomSheet embedBottomSheet = this.parentSheet;
        if (embedBottomSheet != null) {
            embedBottomSheet.exitFromPip();
            return;
        }
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer != null) {
            photoViewer.exitFromPip();
        }
    }

    /* renamed from: lambda$showInternal$11$org-telegram-ui-Components-PipVideoOverlay */
    public /* synthetic */ void m2864x12330da2(View v) {
        VideoPlayer videoPlayer;
        PhotoViewer photoViewer = this.photoViewer;
        if (photoViewer == null || (videoPlayer = photoViewer.getVideoPlayer()) == null) {
            return;
        }
        if (videoPlayer.isPlaying()) {
            videoPlayer.pause();
        } else {
            videoPlayer.play();
        }
        updatePlayButton();
    }

    private WindowManager.LayoutParams createWindowLayoutParams(boolean inAppOnly) {
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.gravity = 51;
        windowLayoutParams.format = -3;
        if (!inAppOnly && AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext)) {
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

    /* loaded from: classes5.dex */
    public final class VideoProgressView extends View {
        private Paint progressPaint = new Paint();
        private Paint bufferPaint = new Paint();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public VideoProgressView(Context context) {
            super(context);
            PipVideoOverlay.this = r4;
            this.progressPaint.setColor(-1);
            this.progressPaint.setStyle(Paint.Style.STROKE);
            this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
            this.progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.bufferPaint.setColor(this.progressPaint.getColor());
            this.bufferPaint.setAlpha((int) (this.progressPaint.getAlpha() * 0.3f));
            this.bufferPaint.setStyle(Paint.Style.STROKE);
            this.bufferPaint.setStrokeCap(Paint.Cap.ROUND);
            this.bufferPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int progressSidePadding = AndroidUtilities.dp(10.0f);
            int progressRight = ((int) (((width - progressSidePadding) - progressSidePadding) * PipVideoOverlay.this.videoProgress)) + progressSidePadding;
            float y = getHeight() - AndroidUtilities.dp(8.0f);
            if (PipVideoOverlay.this.bufferProgress != 0.0f) {
                canvas.drawLine(progressSidePadding, y, progressSidePadding + (((width - progressSidePadding) - progressSidePadding) * PipVideoOverlay.this.bufferProgress), y, this.bufferPaint);
            }
            canvas.drawLine(progressSidePadding, y, progressRight, y, this.progressPaint);
        }
    }

    /* loaded from: classes5.dex */
    public static final class PipConfig {
        private SharedPreferences mPrefs;

        private PipConfig(int width, int height) {
            Context context = ApplicationLoader.applicationContext;
            this.mPrefs = context.getSharedPreferences("pip_layout_" + width + "_" + height, 0);
        }

        public void setPipX(float x) {
            this.mPrefs.edit().putFloat("x", x).apply();
        }

        public void setPipY(float y) {
            this.mPrefs.edit().putFloat("y", y).apply();
        }

        private void setScaleFactor(float scaleFactor) {
            this.mPrefs.edit().putFloat("scale_factor", scaleFactor).apply();
        }

        public float getScaleFactor() {
            return this.mPrefs.getFloat("scale_factor", 1.0f);
        }

        public float getPipX() {
            return this.mPrefs.getFloat("x", -1.0f);
        }

        public float getPipY() {
            return this.mPrefs.getFloat("y", -1.0f);
        }
    }
}
