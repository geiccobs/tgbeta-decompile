package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.os.CancellationSignal;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FingerprintController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.PasscodeView;
/* loaded from: classes5.dex */
public class PasscodeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int id_fingerprint_imageview = 1001;
    private static final int id_fingerprint_textview = 1000;
    private static final int[] ids = {R.id.passcode_btn_0, R.id.passcode_btn_1, R.id.passcode_btn_2, R.id.passcode_btn_3, R.id.passcode_btn_4, R.id.passcode_btn_5, R.id.passcode_btn_6, R.id.passcode_btn_7, R.id.passcode_btn_8, R.id.passcode_btn_9, R.id.passcode_btn_backspace, R.id.passcode_btn_fingerprint};
    private Drawable backgroundDrawable;
    private FrameLayout backgroundFrameLayout;
    private CancellationSignal cancellationSignal;
    private ImageView checkImage;
    private FrameLayout container;
    private PasscodeViewDelegate delegate;
    private ImageView eraseView;
    private AlertDialog fingerprintDialog;
    private ImageView fingerprintImage;
    private ImageView fingerprintImageView;
    private TextView fingerprintStatusTextView;
    private ImageView fingerprintView;
    private RLottieImageView imageView;
    private int imageY;
    private int lastValue;
    private FrameLayout numbersFrameLayout;
    private TextView passcodeTextView;
    private EditTextBoldCursor passwordEditText;
    private AnimatingTextView passwordEditText2;
    private FrameLayout passwordFrameLayout;
    private TextView retryTextView;
    private boolean selfCancelled;
    private int keyboardHeight = 0;
    private android.graphics.Rect rect = new android.graphics.Rect();
    private ArrayList<InnerAnimator> innerAnimators = new ArrayList<>();
    private Runnable checkRunnable = new Runnable() { // from class: org.telegram.ui.Components.PasscodeView.7
        @Override // java.lang.Runnable
        public void run() {
            PasscodeView.this.checkRetryTextView();
            AndroidUtilities.runOnUIThread(PasscodeView.this.checkRunnable, 100L);
        }
    };
    private int[] pos = new int[2];
    private ArrayList<TextView> lettersTextViews = new ArrayList<>(10);
    private ArrayList<TextView> numberTextViews = new ArrayList<>(10);
    private ArrayList<FrameLayout> numberFrameLayouts = new ArrayList<>(10);

    /* loaded from: classes5.dex */
    public interface PasscodeViewDelegate {
        void didAcceptedPassword();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didGenerateFingerprintKeyPair) {
            checkFingerprintButton();
            if (((Boolean) args[0]).booleanValue() && SharedConfig.appLocked) {
                checkFingerprint();
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class AnimatingTextView extends FrameLayout {
        private static final String DOT = "•";
        private AnimatorSet currentAnimation;
        private Runnable dotRunnable;
        private ArrayList<TextView> characterTextViews = new ArrayList<>(4);
        private ArrayList<TextView> dotTextViews = new ArrayList<>(4);
        private StringBuilder stringBuilder = new StringBuilder(4);

        public AnimatingTextView(Context context) {
            super(context);
            for (int a = 0; a < 4; a++) {
                TextView textView = new TextView(context);
                textView.setTextColor(-1);
                textView.setTextSize(1, 36.0f);
                textView.setGravity(17);
                textView.setAlpha(0.0f);
                textView.setPivotX(AndroidUtilities.dp(25.0f));
                textView.setPivotY(AndroidUtilities.dp(25.0f));
                addView(textView, LayoutHelper.createFrame(50, 50, 51));
                this.characterTextViews.add(textView);
                TextView textView2 = new TextView(context);
                textView2.setTextColor(-1);
                textView2.setTextSize(1, 36.0f);
                textView2.setGravity(17);
                textView2.setAlpha(0.0f);
                textView2.setText(DOT);
                textView2.setPivotX(AndroidUtilities.dp(25.0f));
                textView2.setPivotY(AndroidUtilities.dp(25.0f));
                addView(textView2, LayoutHelper.createFrame(50, 50, 51));
                this.dotTextViews.add(textView2);
            }
        }

        private int getXForTextView(int pos) {
            return (((getMeasuredWidth() - (this.stringBuilder.length() * AndroidUtilities.dp(30.0f))) / 2) + (AndroidUtilities.dp(30.0f) * pos)) - AndroidUtilities.dp(10.0f);
        }

        public void appendCharacter(String c) {
            if (this.stringBuilder.length() == 4) {
                return;
            }
            try {
                performHapticFeedback(3);
            } catch (Exception e) {
                FileLog.e(e);
            }
            ArrayList<Animator> animators = new ArrayList<>();
            final int newPos = this.stringBuilder.length();
            this.stringBuilder.append(c);
            TextView textView = this.characterTextViews.get(newPos);
            textView.setText(c);
            textView.setTranslationX(getXForTextView(newPos));
            animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, 0.0f, 1.0f));
            animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, 0.0f, 1.0f));
            animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, 0.0f, 1.0f));
            animators.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, AndroidUtilities.dp(20.0f), 0.0f));
            TextView textView2 = this.dotTextViews.get(newPos);
            textView2.setTranslationX(getXForTextView(newPos));
            textView2.setAlpha(0.0f);
            animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, 0.0f, 1.0f));
            animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, 0.0f, 1.0f));
            animators.add(ObjectAnimator.ofFloat(textView2, View.TRANSLATION_Y, AndroidUtilities.dp(20.0f), 0.0f));
            for (int a = newPos + 1; a < 4; a++) {
                TextView textView3 = this.characterTextViews.get(a);
                if (textView3.getAlpha() != 0.0f) {
                    animators.add(ObjectAnimator.ofFloat(textView3, View.SCALE_X, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView3, View.SCALE_Y, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView3, View.ALPHA, 0.0f));
                }
                TextView textView4 = this.dotTextViews.get(a);
                if (textView4.getAlpha() != 0.0f) {
                    animators.add(ObjectAnimator.ofFloat(textView4, View.SCALE_X, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView4, View.SCALE_Y, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView4, View.ALPHA, 0.0f));
                }
            }
            Runnable runnable = this.dotRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.PasscodeView.AnimatingTextView.1
                @Override // java.lang.Runnable
                public void run() {
                    if (AnimatingTextView.this.dotRunnable != this) {
                        return;
                    }
                    ArrayList<Animator> animators2 = new ArrayList<>();
                    TextView textView5 = (TextView) AnimatingTextView.this.characterTextViews.get(newPos);
                    animators2.add(ObjectAnimator.ofFloat(textView5, View.SCALE_X, 0.0f));
                    animators2.add(ObjectAnimator.ofFloat(textView5, View.SCALE_Y, 0.0f));
                    animators2.add(ObjectAnimator.ofFloat(textView5, View.ALPHA, 0.0f));
                    TextView textView6 = (TextView) AnimatingTextView.this.dotTextViews.get(newPos);
                    animators2.add(ObjectAnimator.ofFloat(textView6, View.SCALE_X, 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(textView6, View.SCALE_Y, 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(textView6, View.ALPHA, 1.0f));
                    AnimatingTextView.this.currentAnimation = new AnimatorSet();
                    AnimatingTextView.this.currentAnimation.setDuration(150L);
                    AnimatingTextView.this.currentAnimation.playTogether(animators2);
                    AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.AnimatingTextView.1.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                                AnimatingTextView.this.currentAnimation = null;
                            }
                        }
                    });
                    AnimatingTextView.this.currentAnimation.start();
                }
            };
            this.dotRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 1500L);
            for (int a2 = 0; a2 < newPos; a2++) {
                TextView textView5 = this.characterTextViews.get(a2);
                animators.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_X, getXForTextView(a2)));
                animators.add(ObjectAnimator.ofFloat(textView5, View.SCALE_X, 0.0f));
                animators.add(ObjectAnimator.ofFloat(textView5, View.SCALE_Y, 0.0f));
                animators.add(ObjectAnimator.ofFloat(textView5, View.ALPHA, 0.0f));
                animators.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_Y, 0.0f));
                TextView textView6 = this.dotTextViews.get(a2);
                animators.add(ObjectAnimator.ofFloat(textView6, View.TRANSLATION_X, getXForTextView(a2)));
                animators.add(ObjectAnimator.ofFloat(textView6, View.SCALE_X, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textView6, View.SCALE_Y, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textView6, View.ALPHA, 1.0f));
                animators.add(ObjectAnimator.ofFloat(textView6, View.TRANSLATION_Y, 0.0f));
            }
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.setDuration(150L);
            this.currentAnimation.playTogether(animators);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.AnimatingTextView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                        AnimatingTextView.this.currentAnimation = null;
                    }
                }
            });
            this.currentAnimation.start();
        }

        public String getString() {
            return this.stringBuilder.toString();
        }

        public int length() {
            return this.stringBuilder.length();
        }

        public boolean eraseLastCharacter() {
            if (this.stringBuilder.length() == 0) {
                return false;
            }
            try {
                performHapticFeedback(3);
            } catch (Exception e) {
                FileLog.e(e);
            }
            ArrayList<Animator> animators = new ArrayList<>();
            int deletingPos = this.stringBuilder.length() - 1;
            if (deletingPos != 0) {
                this.stringBuilder.deleteCharAt(deletingPos);
            }
            for (int a = deletingPos; a < 4; a++) {
                TextView textView = this.characterTextViews.get(a);
                if (textView.getAlpha() != 0.0f) {
                    animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, getXForTextView(a)));
                }
                TextView textView2 = this.dotTextViews.get(a);
                TextView textView3 = textView2;
                if (textView3.getAlpha() != 0.0f) {
                    animators.add(ObjectAnimator.ofFloat(textView3, View.SCALE_X, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView3, View.SCALE_Y, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView3, View.ALPHA, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView3, View.TRANSLATION_Y, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(textView3, View.TRANSLATION_X, getXForTextView(a)));
                }
            }
            if (deletingPos == 0) {
                this.stringBuilder.deleteCharAt(deletingPos);
            }
            for (int a2 = 0; a2 < deletingPos; a2++) {
                TextView textView4 = this.characterTextViews.get(a2);
                animators.add(ObjectAnimator.ofFloat(textView4, View.TRANSLATION_X, getXForTextView(a2)));
                TextView textView5 = this.dotTextViews.get(a2);
                animators.add(ObjectAnimator.ofFloat(textView5, View.TRANSLATION_X, getXForTextView(a2)));
            }
            Runnable runnable = this.dotRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.dotRunnable = null;
            }
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.setDuration(150L);
            this.currentAnimation.playTogether(animators);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.AnimatingTextView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                        AnimatingTextView.this.currentAnimation = null;
                    }
                }
            });
            this.currentAnimation.start();
            return true;
        }

        public void eraseAllCharacters(boolean animated) {
            if (this.stringBuilder.length() == 0) {
                return;
            }
            Runnable runnable = this.dotRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.dotRunnable = null;
            }
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            StringBuilder sb = this.stringBuilder;
            sb.delete(0, sb.length());
            if (animated) {
                ArrayList<Animator> animators = new ArrayList<>();
                for (int a = 0; a < 4; a++) {
                    TextView textView = this.characterTextViews.get(a);
                    if (textView.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_X, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(textView, View.SCALE_Y, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(textView, View.ALPHA, 0.0f));
                    }
                    TextView textView2 = this.dotTextViews.get(a);
                    if (textView2.getAlpha() != 0.0f) {
                        animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_X, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(textView2, View.SCALE_Y, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(textView2, View.ALPHA, 0.0f));
                    }
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.setDuration(150L);
                this.currentAnimation.playTogether(animators);
                this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.AnimatingTextView.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (AnimatingTextView.this.currentAnimation != null && AnimatingTextView.this.currentAnimation.equals(animation)) {
                            AnimatingTextView.this.currentAnimation = null;
                        }
                    }
                });
                this.currentAnimation.start();
                return;
            }
            for (int a2 = 0; a2 < 4; a2++) {
                this.characterTextViews.get(a2).setAlpha(0.0f);
                this.dotTextViews.get(a2).setAlpha(0.0f);
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            Runnable runnable = this.dotRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.dotRunnable = null;
            }
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
            for (int a = 0; a < 4; a++) {
                if (a < this.stringBuilder.length()) {
                    TextView textView = this.characterTextViews.get(a);
                    textView.setAlpha(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setTranslationY(0.0f);
                    textView.setTranslationX(getXForTextView(a));
                    TextView textView2 = this.dotTextViews.get(a);
                    textView2.setAlpha(1.0f);
                    textView2.setScaleX(1.0f);
                    textView2.setScaleY(1.0f);
                    textView2.setTranslationY(0.0f);
                    textView2.setTranslationX(getXForTextView(a));
                } else {
                    this.characterTextViews.get(a).setAlpha(0.0f);
                    this.dotTextViews.get(a).setAlpha(0.0f);
                }
            }
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }
    }

    public PasscodeView(Context context) {
        super(context);
        char c = 0;
        setWillNotDraw(false);
        setVisibility(8);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.PasscodeView.1
            private Paint paint = new Paint();

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (PasscodeView.this.backgroundDrawable != null) {
                    if ((PasscodeView.this.backgroundDrawable instanceof MotionBackgroundDrawable) || (PasscodeView.this.backgroundDrawable instanceof ColorDrawable) || (PasscodeView.this.backgroundDrawable instanceof GradientDrawable)) {
                        PasscodeView.this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        PasscodeView.this.backgroundDrawable.draw(canvas);
                    } else {
                        float scaleX = getMeasuredWidth() / PasscodeView.this.backgroundDrawable.getIntrinsicWidth();
                        float scaleY = (getMeasuredHeight() + PasscodeView.this.keyboardHeight) / PasscodeView.this.backgroundDrawable.getIntrinsicHeight();
                        float scale = Math.max(scaleX, scaleY);
                        int width = (int) Math.ceil(PasscodeView.this.backgroundDrawable.getIntrinsicWidth() * scale);
                        int height = (int) Math.ceil(PasscodeView.this.backgroundDrawable.getIntrinsicHeight() * scale);
                        int x = (getMeasuredWidth() - width) / 2;
                        int y = ((getMeasuredHeight() - height) + PasscodeView.this.keyboardHeight) / 2;
                        PasscodeView.this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                        PasscodeView.this.backgroundDrawable.draw(canvas);
                    }
                } else {
                    super.onDraw(canvas);
                }
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
            }

            @Override // android.view.View
            public void setBackgroundColor(int color) {
                this.paint.setColor(color);
            }
        };
        this.backgroundFrameLayout = frameLayout;
        frameLayout.setWillNotDraw(false);
        int i = -1;
        addView(this.backgroundFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setAnimation(R.raw.passcode_lock_close, 58, 58);
        this.imageView.setAutoRepeat(false);
        addView(this.imageView, LayoutHelper.createFrame(58, 58, 51));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.passwordFrameLayout = frameLayout2;
        this.backgroundFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f));
        TextView textView = new TextView(context);
        this.passcodeTextView = textView;
        textView.setTextColor(-1);
        this.passcodeTextView.setTextSize(1, 14.0f);
        this.passcodeTextView.setGravity(1);
        this.passwordFrameLayout.addView(this.passcodeTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 74.0f));
        TextView textView2 = new TextView(context);
        this.retryTextView = textView2;
        textView2.setTextColor(-1);
        this.retryTextView.setTextSize(1, 15.0f);
        this.retryTextView.setGravity(1);
        this.retryTextView.setVisibility(4);
        this.backgroundFrameLayout.addView(this.retryTextView, LayoutHelper.createFrame(-2, -2, 17));
        AnimatingTextView animatingTextView = new AnimatingTextView(context);
        this.passwordEditText2 = animatingTextView;
        this.passwordFrameLayout.addView(animatingTextView, LayoutHelper.createFrame(-1, -2.0f, 81, 70.0f, 0.0f, 70.0f, 6.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.passwordEditText = editTextBoldCursor;
        float f = 36.0f;
        editTextBoldCursor.setTextSize(1, 36.0f);
        this.passwordEditText.setTextColor(-1);
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setImeOptions(6);
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setBackgroundDrawable(null);
        this.passwordEditText.setCursorColor(-1);
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(32.0f));
        this.passwordFrameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1, -2.0f, 81, 70.0f, 0.0f, 70.0f, 0.0f));
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda6
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView3, int i2, KeyEvent keyEvent) {
                return PasscodeView.this.m2793lambda$new$0$orgtelegramuiComponentsPasscodeView(textView3, i2, keyEvent);
            }
        });
        this.passwordEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.PasscodeView.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (PasscodeView.this.backgroundDrawable instanceof MotionBackgroundDrawable) {
                    if (count == 0 && after == 1) {
                        ((MotionBackgroundDrawable) PasscodeView.this.backgroundDrawable).switchToNextPosition(true);
                    } else if (count == 1 && after == 0) {
                        ((MotionBackgroundDrawable) PasscodeView.this.backgroundDrawable).switchToPrevPosition(true);
                    }
                }
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (PasscodeView.this.passwordEditText.length() == 4 && SharedConfig.passcodeType == 0) {
                    PasscodeView.this.processDone(false);
                }
            }
        });
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.Components.PasscodeView.3
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
        ImageView imageView = new ImageView(context);
        this.checkImage = imageView;
        imageView.setImageResource(R.drawable.passcode_check);
        this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
        this.checkImage.setBackgroundResource(R.drawable.bar_selector_lock);
        this.passwordFrameLayout.addView(this.checkImage, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 10.0f, 4.0f));
        this.checkImage.setContentDescription(LocaleController.getString("Done", R.string.Done));
        this.checkImage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PasscodeView.this.m2794lambda$new$1$orgtelegramuiComponentsPasscodeView(view);
            }
        });
        ImageView imageView2 = new ImageView(context);
        this.fingerprintImage = imageView2;
        imageView2.setImageResource(R.drawable.fingerprint);
        this.fingerprintImage.setScaleType(ImageView.ScaleType.CENTER);
        this.fingerprintImage.setBackgroundResource(R.drawable.bar_selector_lock);
        this.passwordFrameLayout.addView(this.fingerprintImage, LayoutHelper.createFrame(60, 60.0f, 83, 10.0f, 0.0f, 0.0f, 4.0f));
        this.fingerprintImage.setContentDescription(LocaleController.getString("AccDescrFingerprint", R.string.AccDescrFingerprint));
        this.fingerprintImage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PasscodeView.this.m2795lambda$new$2$orgtelegramuiComponentsPasscodeView(view);
            }
        });
        FrameLayout lineFrameLayout = new FrameLayout(context);
        lineFrameLayout.setBackgroundColor(654311423);
        this.passwordFrameLayout.addView(lineFrameLayout, LayoutHelper.createFrame(-1, 1.0f, 83, 20.0f, 0.0f, 20.0f, 0.0f));
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.numbersFrameLayout = frameLayout3;
        this.backgroundFrameLayout.addView(frameLayout3, LayoutHelper.createFrame(-1, -1, 51));
        int a = 0;
        for (int i2 = 10; a < i2; i2 = 10) {
            TextView textView3 = new TextView(context);
            textView3.setTextColor(i);
            textView3.setTextSize(1, f);
            textView3.setGravity(17);
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[c] = Integer.valueOf(a);
            textView3.setText(String.format(locale, "%d", objArr));
            this.numbersFrameLayout.addView(textView3, LayoutHelper.createFrame(50, 50, 51));
            textView3.setImportantForAccessibility(2);
            this.numberTextViews.add(textView3);
            TextView textView4 = new TextView(context);
            textView4.setTextSize(1, 12.0f);
            textView4.setTextColor(Integer.MAX_VALUE);
            textView4.setGravity(17);
            this.numbersFrameLayout.addView(textView4, LayoutHelper.createFrame(50, 50, 51));
            textView4.setImportantForAccessibility(2);
            switch (a) {
                case 0:
                    textView4.setText("+");
                    break;
                case 2:
                    textView4.setText("ABC");
                    break;
                case 3:
                    textView4.setText("DEF");
                    break;
                case 4:
                    textView4.setText("GHI");
                    break;
                case 5:
                    textView4.setText("JKL");
                    break;
                case 6:
                    textView4.setText("MNO");
                    break;
                case 7:
                    textView4.setText("PQRS");
                    break;
                case 8:
                    textView4.setText("TUV");
                    break;
                case 9:
                    textView4.setText("WXYZ");
                    break;
            }
            this.lettersTextViews.add(textView4);
            a++;
            c = 0;
            i = -1;
            f = 36.0f;
        }
        ImageView imageView3 = new ImageView(context);
        this.eraseView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.eraseView.setImageResource(R.drawable.passcode_delete);
        this.numbersFrameLayout.addView(this.eraseView, LayoutHelper.createFrame(50, 50, 51));
        ImageView imageView4 = new ImageView(context);
        this.fingerprintView = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.fingerprintView.setImageResource(R.drawable.fingerprint);
        this.fingerprintView.setVisibility(8);
        this.numbersFrameLayout.addView(this.fingerprintView, LayoutHelper.createFrame(50, 50, 51));
        checkFingerprintButton();
        for (int a2 = 0; a2 < 12; a2++) {
            FrameLayout frameLayout4 = new FrameLayout(context) { // from class: org.telegram.ui.Components.PasscodeView.4
                @Override // android.view.View
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setClassName("android.widget.Button");
                }
            };
            frameLayout4.setBackgroundResource(R.drawable.bar_selector_lock);
            frameLayout4.setTag(Integer.valueOf(a2));
            if (a2 == 11) {
                frameLayout4.setContentDescription(LocaleController.getString("AccDescrFingerprint", R.string.AccDescrFingerprint));
                setNextFocus(frameLayout4, R.id.passcode_btn_0);
            } else if (a2 == 10) {
                frameLayout4.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        return PasscodeView.this.m2796lambda$new$3$orgtelegramuiComponentsPasscodeView(view);
                    }
                });
                frameLayout4.setContentDescription(LocaleController.getString("AccDescrBackspace", R.string.AccDescrBackspace));
                setNextFocus(frameLayout4, R.id.passcode_btn_1);
            } else {
                frameLayout4.setContentDescription(a2 + "");
                if (a2 == 0) {
                    setNextFocus(frameLayout4, R.id.passcode_btn_backspace);
                } else if (a2 != 9) {
                    setNextFocus(frameLayout4, ids[a2 + 1]);
                } else if (this.fingerprintView.getVisibility() == 0) {
                    setNextFocus(frameLayout4, R.id.passcode_btn_fingerprint);
                } else {
                    setNextFocus(frameLayout4, R.id.passcode_btn_0);
                }
            }
            frameLayout4.setId(ids[a2]);
            frameLayout4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PasscodeView.this.m2797lambda$new$4$orgtelegramuiComponentsPasscodeView(view);
                }
            });
            this.numberFrameLayouts.add(frameLayout4);
        }
        for (int a3 = 11; a3 >= 0; a3--) {
            this.numbersFrameLayout.addView(this.numberFrameLayouts.get(a3), LayoutHelper.createFrame(100, 100, 51));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ boolean m2793lambda$new$0$orgtelegramuiComponentsPasscodeView(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone(false);
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ void m2794lambda$new$1$orgtelegramuiComponentsPasscodeView(View v) {
        processDone(false);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ void m2795lambda$new$2$orgtelegramuiComponentsPasscodeView(View v) {
        checkFingerprint();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ boolean m2796lambda$new$3$orgtelegramuiComponentsPasscodeView(View v) {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        Drawable drawable = this.backgroundDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            ((MotionBackgroundDrawable) drawable).switchToPrevPosition(true);
        }
        return true;
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ void m2797lambda$new$4$orgtelegramuiComponentsPasscodeView(View v) {
        int tag = ((Integer) v.getTag()).intValue();
        boolean erased = false;
        switch (tag) {
            case 0:
                this.passwordEditText2.appendCharacter("0");
                break;
            case 1:
                this.passwordEditText2.appendCharacter(IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE);
                break;
            case 2:
                this.passwordEditText2.appendCharacter(ExifInterface.GPS_MEASUREMENT_2D);
                break;
            case 3:
                this.passwordEditText2.appendCharacter(ExifInterface.GPS_MEASUREMENT_3D);
                break;
            case 4:
                this.passwordEditText2.appendCharacter("4");
                break;
            case 5:
                this.passwordEditText2.appendCharacter("5");
                break;
            case 6:
                this.passwordEditText2.appendCharacter("6");
                break;
            case 7:
                this.passwordEditText2.appendCharacter("7");
                break;
            case 8:
                this.passwordEditText2.appendCharacter("8");
                break;
            case 9:
                this.passwordEditText2.appendCharacter("9");
                break;
            case 10:
                erased = this.passwordEditText2.eraseLastCharacter();
                break;
            case 11:
                checkFingerprint();
                break;
        }
        if (this.passwordEditText2.length() == 4) {
            processDone(false);
        }
        if (tag != 11) {
            if (tag == 10) {
                if (!erased) {
                    return;
                }
                Drawable drawable = this.backgroundDrawable;
                if (drawable instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) drawable).switchToPrevPosition(true);
                    return;
                }
                return;
            }
            Drawable drawable2 = this.backgroundDrawable;
            if (drawable2 instanceof MotionBackgroundDrawable) {
                ((MotionBackgroundDrawable) drawable2).switchToNextPosition(true);
            }
        }
    }

    private void setNextFocus(View view, int nextId) {
        view.setNextFocusForwardId(nextId);
        if (Build.VERSION.SDK_INT >= 22) {
            view.setAccessibilityTraversalBefore(nextId);
        }
    }

    public void setDelegate(PasscodeViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void processDone(boolean fingerprint) {
        if (!fingerprint) {
            if (SharedConfig.passcodeRetryInMs > 0) {
                return;
            }
            String password = "";
            if (SharedConfig.passcodeType == 0) {
                password = this.passwordEditText2.getString();
            } else if (SharedConfig.passcodeType == 1) {
                password = this.passwordEditText.getText().toString();
            }
            if (password.length() == 0) {
                onPasscodeError();
                return;
            } else if (!SharedConfig.checkPasscode(password)) {
                SharedConfig.increaseBadPasscodeTries();
                if (SharedConfig.passcodeRetryInMs > 0) {
                    checkRetryTextView();
                }
                this.passwordEditText.setText("");
                this.passwordEditText2.eraseAllCharacters(true);
                onPasscodeError();
                Drawable drawable = this.backgroundDrawable;
                if (drawable instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) drawable).rotatePreview(true);
                    return;
                }
                return;
            }
        }
        SharedConfig.badPasscodeTries = 0;
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        if (Build.VERSION.SDK_INT >= 23 && FingerprintController.isKeyReady() && FingerprintController.checkDeviceFingerprintsChanged()) {
            FingerprintController.deleteInvalidKey();
        }
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        setOnTouchListener(null);
        PasscodeViewDelegate passcodeViewDelegate = this.delegate;
        if (passcodeViewDelegate != null) {
            passcodeViewDelegate.didAcceptedPassword();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                PasscodeView.this.m2799lambda$processDone$5$orgtelegramuiComponentsPasscodeView();
            }
        });
    }

    /* renamed from: lambda$processDone$5$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ void m2799lambda$processDone$5$orgtelegramuiComponentsPasscodeView() {
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.setDuration(200L);
        AnimatorSet.playTogether(ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, AndroidUtilities.dp(20.0f)), ObjectAnimator.ofFloat(this, View.ALPHA, AndroidUtilities.dp(0.0f)));
        AnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PasscodeView.this.setVisibility(8);
            }
        });
        AnimatorSet.start();
    }

    public void shakeTextView(final float x, final int num) {
        if (num == 6) {
            return;
        }
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator.ofFloat(this.passcodeTextView, View.TRANSLATION_X, AndroidUtilities.dp(x)));
        AnimatorSet.setDuration(50L);
        AnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PasscodeView passcodeView = PasscodeView.this;
                int i = num;
                passcodeView.shakeTextView(i == 5 ? 0.0f : -x, i + 1);
            }
        });
        AnimatorSet.start();
    }

    public void checkRetryTextView() {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime > SharedConfig.lastUptimeMillis) {
            SharedConfig.passcodeRetryInMs -= currentTime - SharedConfig.lastUptimeMillis;
            if (SharedConfig.passcodeRetryInMs < 0) {
                SharedConfig.passcodeRetryInMs = 0L;
            }
        }
        SharedConfig.lastUptimeMillis = currentTime;
        SharedConfig.saveConfig();
        if (SharedConfig.passcodeRetryInMs > 0) {
            double d = SharedConfig.passcodeRetryInMs;
            Double.isNaN(d);
            int value = Math.max(1, (int) Math.ceil(d / 1000.0d));
            if (value != this.lastValue) {
                this.retryTextView.setText(LocaleController.formatString("TooManyTries", R.string.TooManyTries, LocaleController.formatPluralString("Seconds", value, new Object[0])));
                this.lastValue = value;
            }
            if (this.retryTextView.getVisibility() != 0) {
                this.retryTextView.setVisibility(0);
                this.passwordFrameLayout.setVisibility(4);
                if (this.numbersFrameLayout.getVisibility() == 0) {
                    this.numbersFrameLayout.setVisibility(4);
                }
                AndroidUtilities.hideKeyboard(this.passwordEditText);
            }
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 100L);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        if (this.passwordFrameLayout.getVisibility() != 0) {
            this.retryTextView.setVisibility(4);
            this.passwordFrameLayout.setVisibility(0);
            if (SharedConfig.passcodeType == 0) {
                this.numbersFrameLayout.setVisibility(0);
            } else if (SharedConfig.passcodeType == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        }
    }

    private void onPasscodeError() {
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200L);
        }
        shakeTextView(2.0f, 0);
    }

    public void onResume() {
        checkRetryTextView();
        if (this.retryTextView.getVisibility() != 0) {
            if (SharedConfig.passcodeType == 1) {
                EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
                if (editTextBoldCursor != null) {
                    editTextBoldCursor.requestFocus();
                    AndroidUtilities.showKeyboard(this.passwordEditText);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        PasscodeView.this.m2798lambda$onResume$6$orgtelegramuiComponentsPasscodeView();
                    }
                }, 200L);
            }
            checkFingerprint();
        }
    }

    /* renamed from: lambda$onResume$6$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ void m2798lambda$onResume$6$orgtelegramuiComponentsPasscodeView() {
        EditTextBoldCursor editTextBoldCursor;
        if (this.retryTextView.getVisibility() != 0 && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void onPause() {
        CancellationSignal cancellationSignal;
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        AlertDialog alertDialog = this.fingerprintDialog;
        if (alertDialog != null) {
            try {
                if (alertDialog.isShowing()) {
                    this.fingerprintDialog.dismiss();
                }
                this.fingerprintDialog = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            if (Build.VERSION.SDK_INT >= 23 && (cancellationSignal = this.cancellationSignal) != null) {
                cancellationSignal.cancel();
                this.cancellationSignal = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didGenerateFingerprintKeyPair);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didGenerateFingerprintKeyPair);
    }

    private void checkFingerprint() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        Activity parentActivity = (Activity) getContext();
        if (parentActivity != null && this.fingerprintView.getVisibility() == 0 && !ApplicationLoader.mainInterfacePaused) {
            try {
                AlertDialog alertDialog = this.fingerprintDialog;
                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints() && FingerprintController.isKeyReady() && !FingerprintController.checkDeviceFingerprintsChanged()) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    TextView fingerprintTextView = new TextView(getContext());
                    fingerprintTextView.setId(1000);
                    fingerprintTextView.setTextAppearance(16974344);
                    fingerprintTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    fingerprintTextView.setText(LocaleController.getString("FingerprintInfo", R.string.FingerprintInfo));
                    relativeLayout.addView(fingerprintTextView);
                    RelativeLayout.LayoutParams layoutParams = LayoutHelper.createRelative(-2, -2);
                    layoutParams.addRule(10);
                    layoutParams.addRule(20);
                    fingerprintTextView.setLayoutParams(layoutParams);
                    ImageView imageView = new ImageView(getContext());
                    this.fingerprintImageView = imageView;
                    imageView.setImageResource(R.drawable.ic_fp_40px);
                    this.fingerprintImageView.setId(1001);
                    relativeLayout.addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0f, -2.0f, 0, 20, 0, 0, 20, 3, 1000));
                    TextView textView = new TextView(getContext());
                    this.fingerprintStatusTextView = textView;
                    textView.setGravity(16);
                    this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", R.string.FingerprintHelp));
                    this.fingerprintStatusTextView.setTextAppearance(16974320);
                    this.fingerprintStatusTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack) & 1124073471);
                    relativeLayout.addView(this.fingerprintStatusTextView);
                    RelativeLayout.LayoutParams layoutParams2 = LayoutHelper.createRelative(-2, -2);
                    layoutParams2.setMarginStart(AndroidUtilities.dp(16.0f));
                    layoutParams2.addRule(8, 1001);
                    layoutParams2.addRule(6, 1001);
                    layoutParams2.addRule(17, 1001);
                    this.fingerprintStatusTextView.setLayoutParams(layoutParams2);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setView(relativeLayout);
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.PasscodeView$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnDismissListener
                        public final void onDismiss(DialogInterface dialogInterface) {
                            PasscodeView.this.m2792x228947ec(dialogInterface);
                        }
                    });
                    AlertDialog alertDialog2 = this.fingerprintDialog;
                    if (alertDialog2 != null) {
                        try {
                            if (alertDialog2.isShowing()) {
                                this.fingerprintDialog.dismiss();
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                    this.fingerprintDialog = builder.show();
                    CancellationSignal cancellationSignal = new CancellationSignal();
                    this.cancellationSignal = cancellationSignal;
                    this.selfCancelled = false;
                    fingerprintManager.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() { // from class: org.telegram.ui.Components.PasscodeView.8
                        @Override // org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            if (errMsgId == 10) {
                                try {
                                    if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                        PasscodeView.this.fingerprintDialog.dismiss();
                                    }
                                } catch (Exception e3) {
                                    FileLog.e(e3);
                                }
                                PasscodeView.this.fingerprintDialog = null;
                            } else if (!PasscodeView.this.selfCancelled && errMsgId != 5) {
                                PasscodeView.this.showFingerprintError(errString);
                            }
                        }

                        @Override // org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            PasscodeView.this.showFingerprintError(helpString);
                        }

                        @Override // org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback
                        public void onAuthenticationFailed() {
                            PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", R.string.FingerprintNotRecognized));
                        }

                        @Override // org.telegram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback
                        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                            try {
                                if (PasscodeView.this.fingerprintDialog.isShowing()) {
                                    PasscodeView.this.fingerprintDialog.dismiss();
                                }
                            } catch (Exception e3) {
                                FileLog.e(e3);
                            }
                            PasscodeView.this.fingerprintDialog = null;
                            PasscodeView.this.processDone(true);
                        }
                    }, null);
                }
            } catch (Throwable th) {
            }
        }
    }

    /* renamed from: lambda$checkFingerprint$7$org-telegram-ui-Components-PasscodeView */
    public /* synthetic */ void m2792x228947ec(DialogInterface dialog) {
        CancellationSignal cancellationSignal = this.cancellationSignal;
        if (cancellationSignal != null) {
            this.selfCancelled = true;
            try {
                cancellationSignal.cancel();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.cancellationSignal = null;
        }
    }

    public void onShow(boolean fingerprint, boolean animated) {
        onShow(fingerprint, animated, -1, -1, null, null);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x0050 -> B:36:0x005e). Please submit an issue!!! */
    private void checkFingerprintButton() {
        Activity parentActivity = (Activity) getContext();
        if (Build.VERSION.SDK_INT >= 23 && parentActivity != null && SharedConfig.useFingerprint) {
            try {
                AlertDialog alertDialog = this.fingerprintDialog;
                if (alertDialog != null) {
                    if (alertDialog.isShowing()) {
                        return;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
                if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints() && FingerprintController.isKeyReady() && !FingerprintController.checkDeviceFingerprintsChanged()) {
                    this.fingerprintView.setVisibility(0);
                } else {
                    this.fingerprintView.setVisibility(8);
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
                this.fingerprintView.setVisibility(8);
            }
        } else {
            this.fingerprintView.setVisibility(8);
        }
        if (SharedConfig.passcodeType == 1) {
            this.fingerprintImage.setVisibility(this.fingerprintView.getVisibility());
        }
        if (this.numberFrameLayouts.size() >= 11) {
            this.numberFrameLayouts.get(11).setVisibility(this.fingerprintView.getVisibility());
        }
    }

    public void onShow(boolean fingerprint, boolean animated, int x, int y, Runnable onShow, Runnable onStart) {
        View currentFocus;
        EditTextBoldCursor editTextBoldCursor;
        checkFingerprintButton();
        checkRetryTextView();
        Activity parentActivity = (Activity) getContext();
        if (SharedConfig.passcodeType == 1) {
            if (!animated && this.retryTextView.getVisibility() != 0 && (editTextBoldCursor = this.passwordEditText) != null) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        } else if (parentActivity != null && (currentFocus = parentActivity.getCurrentFocus()) != null) {
            currentFocus.clearFocus();
            AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
        }
        if (fingerprint && this.retryTextView.getVisibility() != 0) {
            checkFingerprint();
        }
        if (getVisibility() != 0) {
            setTranslationY(0.0f);
            this.backgroundDrawable = null;
            if (Theme.getCachedWallpaper() instanceof MotionBackgroundDrawable) {
                this.backgroundDrawable = Theme.getCachedWallpaper();
                this.backgroundFrameLayout.setBackgroundColor(-1090519040);
            } else if (Theme.isCustomTheme() && !"CJz3BZ6YGEYBAAAABboWp6SAv04".equals(Theme.getSelectedBackgroundSlug()) && !"qeZWES8rGVIEAAAARfWlK1lnfiI".equals(Theme.getSelectedBackgroundSlug())) {
                BackgroundGradientDrawable currentGradientWallpaper = Theme.getCurrentGradientWallpaper();
                this.backgroundDrawable = currentGradientWallpaper;
                if (currentGradientWallpaper == null) {
                    this.backgroundDrawable = Theme.getCachedWallpaper();
                }
                if (this.backgroundDrawable instanceof BackgroundGradientDrawable) {
                    this.backgroundFrameLayout.setBackgroundColor(570425344);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(-1090519040);
                }
            } else {
                String selectedBackgroundSlug = Theme.getSelectedBackgroundSlug();
                if (Theme.DEFAULT_BACKGROUND_SLUG.equals(selectedBackgroundSlug) || Theme.isPatternWallpaper()) {
                    this.backgroundFrameLayout.setBackgroundColor(-11436898);
                } else {
                    Drawable cachedWallpaper = Theme.getCachedWallpaper();
                    this.backgroundDrawable = cachedWallpaper;
                    if (cachedWallpaper instanceof BackgroundGradientDrawable) {
                        this.backgroundFrameLayout.setBackgroundColor(570425344);
                    } else if (cachedWallpaper != null) {
                        this.backgroundFrameLayout.setBackgroundColor(-1090519040);
                    } else {
                        this.backgroundFrameLayout.setBackgroundColor(-11436898);
                    }
                }
            }
            Drawable drawable = this.backgroundDrawable;
            if (drawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable drawable2 = (MotionBackgroundDrawable) drawable;
                int[] colors = drawable2.getColors();
                this.backgroundDrawable = new MotionBackgroundDrawable(colors[0], colors[1], colors[2], colors[3], false);
                if (drawable2.hasPattern() && drawable2.getIntensity() < 0) {
                    this.backgroundFrameLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                } else {
                    this.backgroundFrameLayout.setBackgroundColor(570425344);
                }
                ((MotionBackgroundDrawable) this.backgroundDrawable).setParentView(this.backgroundFrameLayout);
            }
            this.passcodeTextView.setText(LocaleController.getString("EnterYourTelegramPasscode", R.string.EnterYourTelegramPasscode));
            if (SharedConfig.passcodeType == 0) {
                if (this.retryTextView.getVisibility() != 0) {
                    this.numbersFrameLayout.setVisibility(0);
                }
                this.passwordEditText.setVisibility(8);
                this.passwordEditText2.setVisibility(0);
                this.checkImage.setVisibility(8);
                this.fingerprintImage.setVisibility(8);
            } else if (SharedConfig.passcodeType == 1) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.numbersFrameLayout.setVisibility(8);
                this.passwordEditText.setFocusable(true);
                this.passwordEditText.setFocusableInTouchMode(true);
                this.passwordEditText.setVisibility(0);
                this.passwordEditText2.setVisibility(8);
                this.checkImage.setVisibility(0);
                this.fingerprintImage.setVisibility(this.fingerprintView.getVisibility());
            }
            setVisibility(0);
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.passwordEditText.setText("");
            this.passwordEditText2.eraseAllCharacters(false);
            if (!animated) {
                setAlpha(1.0f);
                this.imageView.setScaleX(1.0f);
                this.imageView.setScaleY(1.0f);
                this.imageView.stopAnimation();
                this.imageView.getAnimatedDrawable().setCurrentFrame(38, false);
                if (onShow != null) {
                    onShow.run();
                }
            } else {
                setAlpha(0.0f);
                getViewTreeObserver().addOnGlobalLayoutListener(new AnonymousClass9(x, y, onShow));
                requestLayout();
            }
            setOnTouchListener(PasscodeView$$ExternalSyntheticLambda5.INSTANCE);
        }
    }

    /* renamed from: org.telegram.ui.Components.PasscodeView$9 */
    /* loaded from: classes5.dex */
    public class AnonymousClass9 implements ViewTreeObserver.OnGlobalLayoutListener {
        final /* synthetic */ Runnable val$onShow;
        final /* synthetic */ int val$x;
        final /* synthetic */ int val$y;

        AnonymousClass9(int i, int i2, Runnable runnable) {
            PasscodeView.this = this$0;
            this.val$x = i;
            this.val$y = i2;
            this.val$onShow = runnable;
        }

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public void onGlobalLayout() {
            float ix;
            double d3;
            double d4;
            double d2;
            int h;
            final AnimatorSet animatorSetInner;
            PasscodeView.this.setAlpha(1.0f);
            PasscodeView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            PasscodeView.this.imageView.setProgress(0.0f);
            PasscodeView.this.imageView.playAnimation();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PasscodeView$9$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PasscodeView.AnonymousClass9.this.m2800x50b696ea();
                }
            }, 350L);
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            int w = AndroidUtilities.displaySize.x;
            int h2 = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            if (Build.VERSION.SDK_INT < 21) {
                animators.add(ObjectAnimator.ofFloat(PasscodeView.this.backgroundFrameLayout, View.ALPHA, 0.0f, 1.0f));
                animatorSet.setDuration(350L);
            } else {
                int i = this.val$x;
                int i2 = (w - i) * (w - i);
                int i3 = this.val$y;
                double d1 = Math.sqrt(i2 + ((h2 - i3) * (h2 - i3)));
                int i4 = this.val$x;
                int i5 = this.val$y;
                double d22 = Math.sqrt((i4 * i4) + ((h2 - i5) * (h2 - i5)));
                int i6 = this.val$x;
                int i7 = this.val$y;
                double d32 = Math.sqrt((i6 * i6) + (i7 * i7));
                int i8 = this.val$x;
                int i9 = (w - i8) * (w - i8);
                int i10 = this.val$y;
                double d42 = Math.sqrt(i9 + (i10 * i10));
                final double finalRadius = Math.max(Math.max(Math.max(d1, d22), d32), d42);
                PasscodeView.this.innerAnimators.clear();
                int a = -1;
                int N = PasscodeView.this.numbersFrameLayout.getChildCount();
                while (a < N) {
                    View child = a == -1 ? PasscodeView.this.passcodeTextView : PasscodeView.this.numbersFrameLayout.getChildAt(a);
                    int N2 = N;
                    if (!(child instanceof TextView) && !(child instanceof ImageView)) {
                        h = h2;
                        d2 = d22;
                        d4 = d42;
                        d3 = d32;
                    } else {
                        child.setScaleX(0.7f);
                        child.setScaleY(0.7f);
                        child.setAlpha(0.0f);
                        h = h2;
                        InnerAnimator innerAnimator = new InnerAnimator();
                        child.getLocationInWindow(PasscodeView.this.pos);
                        int buttonX = PasscodeView.this.pos[0] + (child.getMeasuredWidth() / 2);
                        d2 = d22;
                        double d43 = d42;
                        int buttonY = PasscodeView.this.pos[1] + (child.getMeasuredHeight() / 2);
                        int i11 = this.val$x;
                        int i12 = (i11 - buttonX) * (i11 - buttonX);
                        int i13 = this.val$y;
                        innerAnimator.startRadius = ((float) Math.sqrt(i12 + ((i13 - buttonY) * (i13 - buttonY)))) - AndroidUtilities.dp(40.0f);
                        if (a != -1) {
                            animatorSetInner = new AnimatorSet();
                            d4 = d43;
                            animatorSetInner.playTogether(ObjectAnimator.ofFloat(child, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(child, View.SCALE_Y, 1.0f));
                            animatorSetInner.setDuration(140L);
                            animatorSetInner.setInterpolator(new DecelerateInterpolator());
                        } else {
                            d4 = d43;
                            animatorSetInner = null;
                        }
                        innerAnimator.animatorSet = new AnimatorSet();
                        AnimatorSet animatorSet2 = innerAnimator.animatorSet;
                        Animator[] animatorArr = new Animator[3];
                        Property property = View.SCALE_X;
                        d3 = d32;
                        float[] fArr = new float[2];
                        float f = 0.9f;
                        fArr[0] = a == -1 ? 0.9f : 0.6f;
                        float f2 = 1.04f;
                        fArr[1] = a == -1 ? 1.0f : 1.04f;
                        animatorArr[0] = ObjectAnimator.ofFloat(child, property, fArr);
                        Property property2 = View.SCALE_Y;
                        float[] fArr2 = new float[2];
                        if (a != -1) {
                            f = 0.6f;
                        }
                        fArr2[0] = f;
                        if (a == -1) {
                            f2 = 1.0f;
                        }
                        fArr2[1] = f2;
                        animatorArr[1] = ObjectAnimator.ofFloat(child, property2, fArr2);
                        animatorArr[2] = ObjectAnimator.ofFloat(child, View.ALPHA, 0.0f, 1.0f);
                        animatorSet2.playTogether(animatorArr);
                        innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.9.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                AnimatorSet animatorSet3 = animatorSetInner;
                                if (animatorSet3 != null) {
                                    animatorSet3.start();
                                }
                            }
                        });
                        innerAnimator.animatorSet.setDuration(a == -1 ? 232L : 200L);
                        innerAnimator.animatorSet.setInterpolator(new DecelerateInterpolator());
                        PasscodeView.this.innerAnimators.add(innerAnimator);
                    }
                    a++;
                    N = N2;
                    h2 = h;
                    d22 = d2;
                    d42 = d4;
                    d32 = d3;
                }
                animators.add(ViewAnimationUtils.createCircularReveal(PasscodeView.this.backgroundFrameLayout, this.val$x, this.val$y, 0.0f, (float) finalRadius));
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animators.add(animator);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PasscodeView$9$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PasscodeView.AnonymousClass9.this.m2801x35f805ab(finalRadius, valueAnimator);
                    }
                });
                animatorSet.setInterpolator(Easings.easeInOutQuad);
                animatorSet.setDuration(498L);
            }
            animatorSet.playTogether(animators);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PasscodeView.9.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AnonymousClass9.this.val$onShow != null) {
                        AnonymousClass9.this.val$onShow.run();
                    }
                    if (SharedConfig.passcodeType == 1 && PasscodeView.this.retryTextView.getVisibility() != 0 && PasscodeView.this.passwordEditText != null) {
                        PasscodeView.this.passwordEditText.requestFocus();
                        AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
                    }
                }
            });
            animatorSet.start();
            AnimatorSet animatorSet22 = new AnimatorSet();
            animatorSet22.setDuration(332L);
            if (AndroidUtilities.isTablet() || PasscodeView.this.getContext().getResources().getConfiguration().orientation != 2) {
                float ix2 = w;
                ix = (ix2 / 2.0f) - AndroidUtilities.dp(29.0f);
            } else {
                ix = ((SharedConfig.passcodeType == 0 ? w / 2.0f : w) / 2.0f) - AndroidUtilities.dp(30.0f);
            }
            animatorSet22.playTogether(ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.TRANSLATION_X, this.val$x - AndroidUtilities.dp(29.0f), ix), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.TRANSLATION_Y, this.val$y - AndroidUtilities.dp(29.0f), PasscodeView.this.imageY), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.SCALE_X, 0.5f, 1.0f), ObjectAnimator.ofFloat(PasscodeView.this.imageView, View.SCALE_Y, 0.5f, 1.0f));
            animatorSet22.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet22.start();
        }

        /* renamed from: lambda$onGlobalLayout$0$org-telegram-ui-Components-PasscodeView$9 */
        public /* synthetic */ void m2800x50b696ea() {
            PasscodeView.this.imageView.performHapticFeedback(3, 2);
        }

        /* renamed from: lambda$onGlobalLayout$1$org-telegram-ui-Components-PasscodeView$9 */
        public /* synthetic */ void m2801x35f805ab(double finalRadius, ValueAnimator animation) {
            float fraction = animation.getAnimatedFraction();
            double d = fraction;
            Double.isNaN(d);
            double rad = d * finalRadius;
            int a = 0;
            while (a < PasscodeView.this.innerAnimators.size()) {
                InnerAnimator innerAnimator = (InnerAnimator) PasscodeView.this.innerAnimators.get(a);
                if (innerAnimator.startRadius <= rad) {
                    innerAnimator.animatorSet.start();
                    PasscodeView.this.innerAnimators.remove(a);
                    a--;
                }
                a++;
            }
        }
    }

    public static /* synthetic */ boolean lambda$onShow$8(View v, MotionEvent event) {
        return true;
    }

    public void showFingerprintError(CharSequence error) {
        this.fingerprintImageView.setImageResource(R.drawable.ic_fingerprint_error);
        this.fingerprintStatusTextView.setText(error);
        this.fingerprintStatusTextView.setTextColor(-765666);
        Vibrator v = (Vibrator) getContext().getSystemService("vibrator");
        if (v != null) {
            v.vibrate(200L);
        }
        AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0f, 0);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        FrameLayout.LayoutParams layoutParams;
        int num;
        int top;
        FrameLayout.LayoutParams layoutParams2;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int i = 0;
        int height = AndroidUtilities.displaySize.y - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
        if (!AndroidUtilities.isTablet() && getContext().getResources().getConfiguration().orientation == 2) {
            this.imageView.setTranslationX(((SharedConfig.passcodeType == 0 ? width / 2.0f : width) / 2.0f) - AndroidUtilities.dp(29.0f));
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams3.width = SharedConfig.passcodeType == 0 ? width / 2 : width;
            layoutParams3.height = AndroidUtilities.dp(140.0f);
            layoutParams3.topMargin = ((height - AndroidUtilities.dp(140.0f)) / 2) + (SharedConfig.passcodeType == 0 ? AndroidUtilities.dp(40.0f) : 0);
            this.passwordFrameLayout.setLayoutParams(layoutParams3);
            layoutParams = (FrameLayout.LayoutParams) this.numbersFrameLayout.getLayoutParams();
            layoutParams.height = height;
            layoutParams.leftMargin = width / 2;
            int i2 = height - layoutParams.height;
            if (Build.VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            layoutParams.topMargin = i2 + i;
            layoutParams.width = width / 2;
            this.numbersFrameLayout.setLayoutParams(layoutParams);
        } else {
            this.imageView.setTranslationX((width / 2.0f) - AndroidUtilities.dp(29.0f));
            int top2 = 0;
            int left = 0;
            if (AndroidUtilities.isTablet()) {
                if (width > AndroidUtilities.dp(498.0f)) {
                    left = (width - AndroidUtilities.dp(498.0f)) / 2;
                    width = AndroidUtilities.dp(498.0f);
                }
                if (height > AndroidUtilities.dp(528.0f)) {
                    top2 = (height - AndroidUtilities.dp(528.0f)) / 2;
                    height = AndroidUtilities.dp(528.0f);
                }
            }
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams4.height = (height / 3) + (SharedConfig.passcodeType == 0 ? AndroidUtilities.dp(40.0f) : 0);
            layoutParams4.width = width;
            layoutParams4.topMargin = top2;
            layoutParams4.leftMargin = left;
            this.passwordFrameLayout.setTag(Integer.valueOf(top2));
            this.passwordFrameLayout.setLayoutParams(layoutParams4);
            FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.numbersFrameLayout.getLayoutParams();
            layoutParams5.height = (height / 3) * 2;
            layoutParams5.leftMargin = left;
            if (AndroidUtilities.isTablet()) {
                layoutParams5.topMargin = (height - layoutParams5.height) + top2 + AndroidUtilities.dp(20.0f);
            } else {
                int i3 = (height - layoutParams5.height) + top2;
                if (SharedConfig.passcodeType == 0) {
                    i = AndroidUtilities.dp(40.0f);
                }
                layoutParams5.topMargin = i3 + i;
            }
            layoutParams5.width = width;
            this.numbersFrameLayout.setLayoutParams(layoutParams5);
            layoutParams = layoutParams5;
        }
        float f = 50.0f;
        int sizeBetweenNumbersX = (layoutParams.width - (AndroidUtilities.dp(50.0f) * 3)) / 4;
        int sizeBetweenNumbersY = (layoutParams.height - (AndroidUtilities.dp(50.0f) * 4)) / 5;
        int a = 0;
        while (a < 12) {
            if (a == 0) {
                num = 10;
            } else if (a == 10) {
                num = 11;
            } else if (a == 11) {
                num = 9;
            } else {
                num = a - 1;
            }
            int row = num / 3;
            int col = num % 3;
            if (a >= 10) {
                if (a == 10) {
                    layoutParams2 = (FrameLayout.LayoutParams) this.eraseView.getLayoutParams();
                    int top3 = ((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY + AndroidUtilities.dp(8.0f);
                    layoutParams2.topMargin = top3;
                    layoutParams2.leftMargin = ((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersX) * col) + sizeBetweenNumbersX;
                    top = top3 - AndroidUtilities.dp(8.0f);
                    this.eraseView.setLayoutParams(layoutParams2);
                } else {
                    layoutParams2 = (FrameLayout.LayoutParams) this.fingerprintView.getLayoutParams();
                    int top4 = ((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY + AndroidUtilities.dp(8.0f);
                    layoutParams2.topMargin = top4;
                    layoutParams2.leftMargin = ((AndroidUtilities.dp(50.0f) + sizeBetweenNumbersX) * col) + sizeBetweenNumbersX;
                    top = top4 - AndroidUtilities.dp(8.0f);
                    this.fingerprintView.setLayoutParams(layoutParams2);
                }
            } else {
                TextView textView = this.numberTextViews.get(a);
                TextView textView1 = this.lettersTextViews.get(a);
                layoutParams2 = (FrameLayout.LayoutParams) textView.getLayoutParams();
                FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) textView1.getLayoutParams();
                top = ((AndroidUtilities.dp(f) + sizeBetweenNumbersY) * row) + sizeBetweenNumbersY;
                layoutParams2.topMargin = top;
                layoutParams1.topMargin = top;
                int dp = sizeBetweenNumbersX + ((sizeBetweenNumbersX + AndroidUtilities.dp(f)) * col);
                layoutParams2.leftMargin = dp;
                layoutParams1.leftMargin = dp;
                layoutParams1.topMargin += AndroidUtilities.dp(40.0f);
                textView.setLayoutParams(layoutParams2);
                textView1.setLayoutParams(layoutParams1);
            }
            FrameLayout frameLayout = this.numberFrameLayouts.get(a);
            FrameLayout.LayoutParams layoutParams12 = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
            layoutParams12.topMargin = top - AndroidUtilities.dp(17.0f);
            layoutParams12.leftMargin = layoutParams2.leftMargin - AndroidUtilities.dp(25.0f);
            frameLayout.setLayoutParams(layoutParams12);
            a++;
            f = 50.0f;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View rootView = getRootView();
        int usableViewHeight = (rootView.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.getViewInset(rootView);
        getWindowVisibleDisplayFrame(this.rect);
        this.keyboardHeight = usableViewHeight - (this.rect.bottom - this.rect.top);
        if (SharedConfig.passcodeType == 1 && (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2)) {
            int t = 0;
            if (this.passwordFrameLayout.getTag() != null) {
                t = ((Integer) this.passwordFrameLayout.getTag()).intValue();
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.passwordFrameLayout.getLayoutParams();
            layoutParams.topMargin = ((layoutParams.height + t) - (this.keyboardHeight / 2)) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            this.passwordFrameLayout.setLayoutParams(layoutParams);
        }
        super.onLayout(changed, left, top, right, bottom);
        this.passcodeTextView.getLocationInWindow(this.pos);
        if (!AndroidUtilities.isTablet() && getContext().getResources().getConfiguration().orientation == 2) {
            RLottieImageView rLottieImageView = this.imageView;
            int dp = this.pos[1] - AndroidUtilities.dp(100.0f);
            this.imageY = dp;
            rLottieImageView.setTranslationY(dp);
            return;
        }
        RLottieImageView rLottieImageView2 = this.imageView;
        int dp2 = this.pos[1] - AndroidUtilities.dp(100.0f);
        this.imageY = dp2;
        rLottieImageView2.setTranslationY(dp2);
    }
}
