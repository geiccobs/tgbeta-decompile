package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PipRoundVideoView implements NotificationCenter.NotificationCenterDelegate {
    private static PipRoundVideoView instance;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private Bitmap bitmap;
    private int currentAccount;
    private DecelerateInterpolator decelerateInterpolator;
    private AnimatorSet hideShowAnimation;
    private ImageView imageView;
    private Runnable onCloseRunnable;
    private Activity parentActivity;
    private SharedPreferences preferences;
    private RectF rect = new RectF();
    private TextureView textureView;
    private int videoHeight;
    private int videoWidth;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;

    public void show(Activity activity, Runnable closeRunnable) {
        if (activity == null) {
            return;
        }
        instance = this;
        this.onCloseRunnable = closeRunnable;
        FrameLayout frameLayout = new FrameLayout(activity) { // from class: org.telegram.ui.Components.PipRoundVideoView.1
            private boolean dragging;
            private boolean startDragging;
            private float startX;
            private float startY;

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    this.startX = event.getRawX();
                    this.startY = event.getRawY();
                    this.startDragging = true;
                }
                return true;
            }

            @Override // android.view.ViewGroup, android.view.ViewParent
            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                MessageObject messageObject;
                if (this.startDragging || this.dragging) {
                    float x = event.getRawX();
                    float y = event.getRawY();
                    if (event.getAction() == 2) {
                        float dx = x - this.startX;
                        float dy = y - this.startY;
                        if (this.startDragging) {
                            if (Math.abs(dx) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(dy) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                                this.dragging = true;
                                this.startDragging = false;
                            }
                        } else if (this.dragging) {
                            WindowManager.LayoutParams layoutParams = PipRoundVideoView.this.windowLayoutParams;
                            layoutParams.x = (int) (layoutParams.x + dx);
                            WindowManager.LayoutParams layoutParams2 = PipRoundVideoView.this.windowLayoutParams;
                            layoutParams2.y = (int) (layoutParams2.y + dy);
                            int maxDiff = PipRoundVideoView.this.videoWidth / 2;
                            if (PipRoundVideoView.this.windowLayoutParams.x < (-maxDiff)) {
                                PipRoundVideoView.this.windowLayoutParams.x = -maxDiff;
                            } else if (PipRoundVideoView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) + maxDiff) {
                                PipRoundVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) + maxDiff;
                            }
                            float alpha = 1.0f;
                            if (PipRoundVideoView.this.windowLayoutParams.x < 0) {
                                alpha = ((PipRoundVideoView.this.windowLayoutParams.x / maxDiff) * 0.5f) + 1.0f;
                            } else if (PipRoundVideoView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - PipRoundVideoView.this.windowLayoutParams.width) {
                                alpha = 1.0f - ((((PipRoundVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + PipRoundVideoView.this.windowLayoutParams.width) / maxDiff) * 0.5f);
                            }
                            if (PipRoundVideoView.this.windowView.getAlpha() != alpha) {
                                PipRoundVideoView.this.windowView.setAlpha(alpha);
                            }
                            if (PipRoundVideoView.this.windowLayoutParams.y < (-0)) {
                                PipRoundVideoView.this.windowLayoutParams.y = -0;
                            } else if (PipRoundVideoView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - PipRoundVideoView.this.windowLayoutParams.height) + 0) {
                                PipRoundVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipRoundVideoView.this.windowLayoutParams.height) + 0;
                            }
                            PipRoundVideoView.this.windowManager.updateViewLayout(PipRoundVideoView.this.windowView, PipRoundVideoView.this.windowLayoutParams);
                            this.startX = x;
                            this.startY = y;
                        }
                    } else if (event.getAction() == 1) {
                        if (this.startDragging && !this.dragging && (messageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
                            if (MediaController.getInstance().isMessagePaused()) {
                                MediaController.getInstance().playMessage(messageObject);
                            } else {
                                MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(messageObject);
                            }
                        }
                        this.dragging = false;
                        this.startDragging = false;
                        PipRoundVideoView.this.animateToBoundsMaybe();
                    }
                    return true;
                }
                return false;
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (Theme.chat_roundVideoShadow != null) {
                    Theme.chat_roundVideoShadow.setAlpha((int) (getAlpha() * 255.0f));
                    Theme.chat_roundVideoShadow.setBounds(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(125.0f), AndroidUtilities.dp(125.0f));
                    Theme.chat_roundVideoShadow.draw(canvas);
                    Theme.chat_docBackPaint.setColor(Theme.getColor(Theme.key_chat_inBubble));
                    Theme.chat_docBackPaint.setAlpha((int) (getAlpha() * 255.0f));
                    canvas.drawCircle(AndroidUtilities.dp(63.0f), AndroidUtilities.dp(63.0f), AndroidUtilities.dp(59.5f), Theme.chat_docBackPaint);
                }
            }
        };
        this.windowView = frameLayout;
        frameLayout.setWillNotDraw(false);
        this.videoWidth = AndroidUtilities.dp(126.0f);
        this.videoHeight = AndroidUtilities.dp(126.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity) { // from class: org.telegram.ui.Components.PipRoundVideoView.2
                @Override // android.view.ViewGroup
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    MessageObject currentMessageObject;
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (child == PipRoundVideoView.this.textureView && (currentMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
                        PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), getMeasuredWidth() - AndroidUtilities.dpf2(1.5f), getMeasuredHeight() - AndroidUtilities.dpf2(1.5f));
                        canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, currentMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
                    }
                    return result;
                }
            };
            this.aspectRatioFrameLayout = aspectRatioFrameLayout;
            aspectRatioFrameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.PipRoundVideoView.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f));
                }
            });
            this.aspectRatioFrameLayout.setClipToOutline(true);
        } else {
            final Paint aspectPaint = new Paint(1);
            aspectPaint.setColor(-16777216);
            aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(activity) { // from class: org.telegram.ui.Components.PipRoundVideoView.4
                private Path aspectPath = new Path();

                @Override // android.view.View
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    this.aspectPath.reset();
                    this.aspectPath.addCircle(w / 2, h / 2, w / 2, Path.Direction.CW);
                    this.aspectPath.toggleInverseFillType();
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void dispatchDraw(Canvas canvas) {
                    super.dispatchDraw(canvas);
                    canvas.drawPath(this.aspectPath, aspectPaint);
                }

                @Override // android.view.ViewGroup
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result;
                    MessageObject currentMessageObject;
                    try {
                        result = super.drawChild(canvas, child, drawingTime);
                    } catch (Throwable th) {
                        result = false;
                    }
                    if (child == PipRoundVideoView.this.textureView && (currentMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
                        PipRoundVideoView.this.rect.set(AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), getMeasuredWidth() - AndroidUtilities.dpf2(1.5f), getMeasuredHeight() - AndroidUtilities.dpf2(1.5f));
                        canvas.drawArc(PipRoundVideoView.this.rect, -90.0f, currentMessageObject.audioProgress * 360.0f, false, Theme.chat_radialProgressPaint);
                    }
                    return result;
                }
            };
            this.aspectRatioFrameLayout = aspectRatioFrameLayout2;
            aspectRatioFrameLayout2.setLayerType(2, null);
        }
        this.aspectRatioFrameLayout.setAspectRatio(1.0f, 0);
        this.windowView.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(120, 120.0f, 51, 3.0f, 3.0f, 0.0f, 0.0f));
        this.windowView.setAlpha(1.0f);
        this.windowView.setScaleX(0.8f);
        this.windowView.setScaleY(0.8f);
        this.textureView = new TextureView(activity);
        float scale = (AndroidUtilities.dpf2(120.0f) + AndroidUtilities.dpf2(2.0f)) / AndroidUtilities.dpf2(120.0f);
        this.textureView.setScaleX(scale);
        this.textureView.setScaleY(scale);
        this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView = new ImageView(activity);
        this.imageView = imageView;
        this.aspectRatioFrameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        this.imageView.setVisibility(4);
        this.windowManager = (WindowManager) activity.getSystemService("window");
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
        this.preferences = sharedPreferences;
        int sidex = sharedPreferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.width = this.videoWidth;
            this.windowLayoutParams.height = this.videoHeight;
            this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.videoWidth);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.videoHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.parentActivity = activity;
            int i = UserConfig.selectedAccount;
            this.currentAccount = i;
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            runShowHideAnimation(true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private static int getSideCoord(boolean isX, int side, float p, int sideSize) {
        int total;
        int result;
        if (isX) {
            total = AndroidUtilities.displaySize.x - sideSize;
        } else {
            total = (AndroidUtilities.displaySize.y - sideSize) - ActionBar.getCurrentActionBarHeight();
        }
        if (side == 0) {
            result = AndroidUtilities.dp(10.0f);
        } else if (side == 1) {
            result = total - AndroidUtilities.dp(10.0f);
        } else {
            result = AndroidUtilities.dp(10.0f) + Math.round((total - AndroidUtilities.dp(20.0f)) * p);
        }
        if (!isX) {
            return result + ActionBar.getCurrentActionBarHeight();
        }
        return result;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        AspectRatioFrameLayout aspectRatioFrameLayout;
        if (id == NotificationCenter.messagePlayingProgressDidChanged && (aspectRatioFrameLayout = this.aspectRatioFrameLayout) != null) {
            aspectRatioFrameLayout.invalidate();
        }
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void close(boolean animated) {
        if (animated) {
            TextureView textureView = this.textureView;
            if (textureView != null && textureView.getParent() != null) {
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    this.bitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
                }
                try {
                    this.textureView.getBitmap(this.bitmap);
                } catch (Throwable th) {
                    this.bitmap = null;
                }
                this.imageView.setImageBitmap(this.bitmap);
                try {
                    this.aspectRatioFrameLayout.removeView(this.textureView);
                } catch (Exception e) {
                }
                this.imageView.setVisibility(0);
                runShowHideAnimation(false);
                return;
            }
            return;
        }
        if (this.bitmap != null) {
            this.imageView.setImageDrawable(null);
            this.bitmap.recycle();
            this.bitmap = null;
        }
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception e2) {
        }
        if (instance == this) {
            instance = null;
        }
        this.parentActivity = null;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
    }

    public void onConfigurationChanged() {
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.videoWidth);
        this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.videoHeight);
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    public void showTemporary(boolean show) {
        AnimatorSet animatorSet = this.hideShowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.hideShowAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        FrameLayout frameLayout2 = this.windowView;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        fArr2[0] = show ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
        FrameLayout frameLayout3 = this.windowView;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        if (!show) {
            f = 0.8f;
        }
        fArr3[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout3, property3, fArr3);
        animatorSet2.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150L);
        if (this.decelerateInterpolator == null) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PipRoundVideoView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }
        });
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
    }

    private void runShowHideAnimation(final boolean show) {
        AnimatorSet animatorSet = this.hideShowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.hideShowAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[3];
        FrameLayout frameLayout = this.windowView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        FrameLayout frameLayout2 = this.windowView;
        Property property2 = View.SCALE_X;
        float[] fArr2 = new float[1];
        fArr2[0] = show ? 1.0f : 0.8f;
        animatorArr[1] = ObjectAnimator.ofFloat(frameLayout2, property2, fArr2);
        FrameLayout frameLayout3 = this.windowView;
        Property property3 = View.SCALE_Y;
        float[] fArr3 = new float[1];
        if (!show) {
            f = 0.8f;
        }
        fArr3[0] = f;
        animatorArr[2] = ObjectAnimator.ofFloat(frameLayout3, property3, fArr3);
        animatorSet2.playTogether(animatorArr);
        this.hideShowAnimation.setDuration(150L);
        if (this.decelerateInterpolator == null) {
            this.decelerateInterpolator = new DecelerateInterpolator();
        }
        this.hideShowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PipRoundVideoView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    if (!show) {
                        PipRoundVideoView.this.close(false);
                    }
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (animation.equals(PipRoundVideoView.this.hideShowAnimation)) {
                    PipRoundVideoView.this.hideShowAnimation = null;
                }
            }
        });
        this.hideShowAnimation.setInterpolator(this.decelerateInterpolator);
        this.hideShowAnimation.start();
    }

    public void animateToBoundsMaybe() {
        int startX = getSideCoord(true, 0, 0.0f, this.videoWidth);
        int endX = getSideCoord(true, 1, 0.0f, this.videoWidth);
        int startY = getSideCoord(false, 0, 0.0f, this.videoHeight);
        int endY = getSideCoord(false, 1, 0.0f, this.videoHeight);
        ArrayList<Animator> animators = null;
        SharedPreferences.Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(20.0f);
        boolean slideOut = false;
        if (Math.abs(startX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x < 0 && this.windowLayoutParams.x > (-this.videoWidth) / 4)) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 0);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 1.0f));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", startX));
        } else if (Math.abs(endX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.videoWidth && this.windowLayoutParams.x < AndroidUtilities.displaySize.x - ((this.videoWidth / 4) * 3))) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 1);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 1.0f));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", endX));
        } else if (this.windowView.getAlpha() != 1.0f) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            if (this.windowLayoutParams.x < 0) {
                animators.add(ObjectAnimator.ofInt(this, "x", -this.videoWidth));
            } else {
                animators.add(ObjectAnimator.ofInt(this, "x", AndroidUtilities.displaySize.x));
            }
            slideOut = true;
        } else {
            editor.putFloat("px", (this.windowLayoutParams.x - startX) / (endX - startX));
            editor.putInt("sidex", 2);
        }
        if (!slideOut) {
            if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff || this.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 0);
                animators.add(ObjectAnimator.ofInt(this, "y", startY));
            } else if (Math.abs(endY - this.windowLayoutParams.y) <= maxDiff) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 1);
                animators.add(ObjectAnimator.ofInt(this, "y", endY));
            } else {
                editor.putFloat("py", (this.windowLayoutParams.y - startY) / (endY - startY));
                editor.putInt("sidey", 2);
            }
            editor.commit();
        }
        if (animators != null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150L);
            if (slideOut) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 0.0f));
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PipRoundVideoView.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PipRoundVideoView.this.close(false);
                        if (PipRoundVideoView.this.onCloseRunnable != null) {
                            PipRoundVideoView.this.onCloseRunnable.run();
                        }
                    }
                });
            }
            animatorSet.playTogether(animators);
            animatorSet.start();
        }
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    public void setX(int value) {
        this.windowLayoutParams.x = value;
        try {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
        }
    }

    public void setY(int value) {
        this.windowLayoutParams.y = value;
        try {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
        }
    }

    public static PipRoundVideoView getInstance() {
        return instance;
    }
}
