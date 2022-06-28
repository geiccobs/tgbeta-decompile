package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPStatusTextView;
/* loaded from: classes5.dex */
public class VoIPStatusTextView extends FrameLayout {
    boolean animationInProgress;
    ValueAnimator animator;
    private boolean attachedToWindow;
    EllipsizeSpanAnimator ellipsizeAnimator;
    CharSequence nextTextToSet;
    TextView reconnectTextView;
    TextView[] textView = new TextView[2];
    boolean timerShowing;
    VoIPTimerView timerView;

    public VoIPStatusTextView(Context context) {
        super(context);
        for (int i = 0; i < 2; i++) {
            this.textView[i] = new TextView(context);
            this.textView[i].setTextSize(1, 15.0f);
            this.textView[i].setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(0.6666667f), 1275068416);
            this.textView[i].setTextColor(-1);
            this.textView[i].setGravity(1);
            addView(this.textView[i]);
        }
        TextView textView = new TextView(context);
        this.reconnectTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.reconnectTextView.setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(0.6666667f), 1275068416);
        this.reconnectTextView.setTextColor(-1);
        this.reconnectTextView.setGravity(1);
        addView(this.reconnectTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 22.0f, 0.0f, 0.0f));
        this.ellipsizeAnimator = new EllipsizeSpanAnimator(this);
        SpannableStringBuilder ssb = new SpannableStringBuilder(LocaleController.getString("VoipReconnecting", R.string.VoipReconnecting));
        SpannableString ell = new SpannableString("...");
        this.ellipsizeAnimator.wrap(ell, 0);
        ssb.append((CharSequence) ell);
        this.reconnectTextView.setText(ssb);
        this.reconnectTextView.setVisibility(8);
        VoIPTimerView voIPTimerView = new VoIPTimerView(context);
        this.timerView = voIPTimerView;
        addView(voIPTimerView, LayoutHelper.createFrame(-1, -2.0f));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setText(String text, boolean ellipsis, boolean animated) {
        CharSequence nextString = text;
        if (ellipsis) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            this.ellipsizeAnimator.reset();
            SpannableString ell = new SpannableString("...");
            this.ellipsizeAnimator.wrap(ell, 0);
            ssb.append((CharSequence) ell);
            nextString = ssb;
            this.ellipsizeAnimator.addView(this.textView[0]);
            this.ellipsizeAnimator.addView(this.textView[1]);
        } else {
            this.ellipsizeAnimator.removeView(this.textView[0]);
            this.ellipsizeAnimator.removeView(this.textView[1]);
        }
        if (TextUtils.isEmpty(this.textView[0].getText())) {
            animated = false;
        }
        if (!animated) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.animationInProgress = false;
            this.textView[0].setText(nextString);
            this.textView[0].setVisibility(0);
            this.textView[1].setVisibility(8);
            this.timerView.setVisibility(8);
        } else if (this.animationInProgress) {
            this.nextTextToSet = nextString;
        } else if (this.timerShowing) {
            this.textView[0].setText(nextString);
            replaceViews(this.timerView, this.textView[0], null);
        } else if (!this.textView[0].getText().equals(nextString)) {
            this.textView[1].setText(nextString);
            TextView[] textViewArr = this.textView;
            replaceViews(textViewArr[0], textViewArr[1], new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPStatusTextView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPStatusTextView.this.m3269x89ca5ab6();
                }
            });
        }
    }

    /* renamed from: lambda$setText$0$org-telegram-ui-Components-voip-VoIPStatusTextView */
    public /* synthetic */ void m3269x89ca5ab6() {
        TextView[] textViewArr = this.textView;
        TextView v = textViewArr[0];
        textViewArr[0] = textViewArr[1];
        textViewArr[1] = v;
    }

    public void showTimer(boolean animated) {
        if (TextUtils.isEmpty(this.textView[0].getText())) {
            animated = false;
        }
        if (this.timerShowing) {
            return;
        }
        this.timerView.updateTimer();
        if (!animated) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.timerShowing = true;
            this.animationInProgress = false;
            this.textView[0].setVisibility(8);
            this.textView[1].setVisibility(8);
            this.timerView.setVisibility(0);
        } else if (this.animationInProgress) {
            this.nextTextToSet = "timer";
            return;
        } else {
            this.timerShowing = true;
            replaceViews(this.textView[0], this.timerView, null);
        }
        this.ellipsizeAnimator.removeView(this.textView[0]);
        this.ellipsizeAnimator.removeView(this.textView[1]);
    }

    public void replaceViews(final View out, final View in, Runnable onEnd) {
        out.setVisibility(0);
        in.setVisibility(0);
        in.setTranslationY(AndroidUtilities.dp(15.0f));
        in.setAlpha(0.0f);
        this.animationInProgress = true;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPStatusTextView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPStatusTextView.lambda$replaceViews$1(in, out, valueAnimator);
            }
        });
        this.animator.addListener(new AnonymousClass1(out, in, onEnd));
        this.animator.setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.animator.start();
    }

    public static /* synthetic */ void lambda$replaceViews$1(View in, View out, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float inScale = (v * 0.6f) + 0.4f;
        float outScale = ((1.0f - v) * 0.6f) + 0.4f;
        in.setTranslationY(AndroidUtilities.dp(10.0f) * (1.0f - v));
        in.setAlpha(v);
        in.setScaleX(inScale);
        in.setScaleY(inScale);
        out.setTranslationY((-AndroidUtilities.dp(10.0f)) * v);
        out.setAlpha(1.0f - v);
        out.setScaleX(outScale);
        out.setScaleY(outScale);
    }

    /* renamed from: org.telegram.ui.Components.voip.VoIPStatusTextView$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 extends AnimatorListenerAdapter {
        final /* synthetic */ View val$in;
        final /* synthetic */ Runnable val$onEnd;
        final /* synthetic */ View val$out;

        AnonymousClass1(View view, View view2, Runnable runnable) {
            VoIPStatusTextView.this = this$0;
            this.val$out = view;
            this.val$in = view2;
            this.val$onEnd = runnable;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            this.val$out.setVisibility(8);
            this.val$out.setAlpha(1.0f);
            this.val$out.setTranslationY(0.0f);
            this.val$out.setScaleY(1.0f);
            this.val$out.setScaleX(1.0f);
            this.val$in.setAlpha(1.0f);
            this.val$in.setTranslationY(0.0f);
            this.val$in.setVisibility(0);
            this.val$in.setScaleY(1.0f);
            this.val$in.setScaleX(1.0f);
            Runnable runnable = this.val$onEnd;
            if (runnable != null) {
                runnable.run();
            }
            VoIPStatusTextView.this.animationInProgress = false;
            if (VoIPStatusTextView.this.nextTextToSet != null) {
                if (VoIPStatusTextView.this.nextTextToSet.equals("timer")) {
                    VoIPStatusTextView.this.showTimer(true);
                } else {
                    VoIPStatusTextView.this.textView[1].setText(VoIPStatusTextView.this.nextTextToSet);
                    VoIPStatusTextView voIPStatusTextView = VoIPStatusTextView.this;
                    voIPStatusTextView.replaceViews(voIPStatusTextView.textView[0], VoIPStatusTextView.this.textView[1], new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPStatusTextView$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            VoIPStatusTextView.AnonymousClass1.this.m3270x82ff88c2();
                        }
                    });
                }
                VoIPStatusTextView.this.nextTextToSet = null;
            }
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-voip-VoIPStatusTextView$1 */
        public /* synthetic */ void m3270x82ff88c2() {
            TextView v = VoIPStatusTextView.this.textView[0];
            VoIPStatusTextView.this.textView[0] = VoIPStatusTextView.this.textView[1];
            VoIPStatusTextView.this.textView[1] = v;
        }
    }

    public void setSignalBarCount(int count) {
        this.timerView.setSignalBarCount(count);
    }

    public void showReconnect(boolean showReconnecting, boolean animated) {
        int i = 0;
        if (!animated) {
            this.reconnectTextView.animate().setListener(null).cancel();
            TextView textView = this.reconnectTextView;
            if (!showReconnecting) {
                i = 8;
            }
            textView.setVisibility(i);
        } else if (showReconnecting) {
            if (this.reconnectTextView.getVisibility() != 0) {
                this.reconnectTextView.setVisibility(0);
                this.reconnectTextView.setAlpha(0.0f);
            }
            this.reconnectTextView.animate().setListener(null).cancel();
            this.reconnectTextView.animate().alpha(1.0f).setDuration(150L).start();
        } else {
            this.reconnectTextView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPStatusTextView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPStatusTextView.this.reconnectTextView.setVisibility(8);
                }
            }).setDuration(150L).start();
        }
        if (showReconnecting) {
            this.ellipsizeAnimator.addView(this.reconnectTextView);
        } else {
            this.ellipsizeAnimator.removeView(this.reconnectTextView);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        this.ellipsizeAnimator.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        this.ellipsizeAnimator.onDetachedFromWindow();
    }
}
