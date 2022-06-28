package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SenderSelectView extends View {
    private ValueAnimator menuAnimator;
    private float menuProgress;
    private SpringAnimation menuSpring;
    private boolean scaleIn;
    private boolean scaleOut;
    private Drawable selectorDrawable;
    private static final float SPRING_MULTIPLIER = 100.0f;
    private static final FloatPropertyCompat<SenderSelectView> MENU_PROGRESS = new SimpleFloatPropertyCompat("menuProgress", SenderSelectView$$ExternalSyntheticLambda3.INSTANCE, SenderSelectView$$ExternalSyntheticLambda4.INSTANCE).setMultiplier(SPRING_MULTIPLIER);
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private Paint backgroundPaint = new Paint(1);
    private Paint menuPaint = new Paint(1);

    public static /* synthetic */ void lambda$static$1(SenderSelectView obj, float value) {
        obj.menuProgress = value;
        obj.invalidate();
    }

    public SenderSelectView(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.menuPaint.setStrokeCap(Paint.Cap.ROUND);
        this.menuPaint.setStyle(Paint.Style.STROKE);
        updateColors();
        setContentDescription(LocaleController.formatString("AccDescrSendAsPeer", R.string.AccDescrSendAsPeer, ""));
    }

    private void updateColors() {
        this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
        this.menuPaint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoicePressed));
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor(Theme.key_windowBackgroundWhite));
        this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(getLayoutParams().width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
        this.avatarImage.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float sc;
        canvas.save();
        if (this.scaleOut) {
            sc = 1.0f - this.menuProgress;
        } else if (this.scaleIn) {
            sc = this.menuProgress;
        } else {
            sc = 1.0f;
        }
        canvas.scale(sc, sc, getWidth() / 2.0f, getHeight() / 2.0f);
        super.onDraw(canvas);
        this.avatarImage.draw(canvas);
        int alpha = (int) (this.menuProgress * 255.0f);
        this.backgroundPaint.setAlpha(alpha);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min(getWidth(), getHeight()) / 2.0f, this.backgroundPaint);
        canvas.save();
        this.menuPaint.setAlpha(alpha);
        float padding = AndroidUtilities.dp(9.0f) + this.menuPaint.getStrokeWidth();
        canvas.drawLine(padding, padding, getWidth() - padding, getHeight() - padding, this.menuPaint);
        canvas.drawLine(padding, getHeight() - padding, getWidth() - padding, padding, this.menuPaint);
        canvas.restore();
        this.selectorDrawable.setBounds(0, 0, getWidth(), getHeight());
        this.selectorDrawable.draw(canvas);
        canvas.restore();
    }

    public void setAvatar(TLObject obj) {
        String objName = "";
        if (obj instanceof TLRPC.User) {
            objName = UserObject.getFirstName((TLRPC.User) obj);
        } else if (obj instanceof TLRPC.Chat) {
            objName = ((TLRPC.Chat) obj).title;
        } else if (obj instanceof TLRPC.ChatInvite) {
            objName = ((TLRPC.ChatInvite) obj).title;
        }
        setContentDescription(LocaleController.formatString("AccDescrSendAsPeer", R.string.AccDescrSendAsPeer, objName));
        this.avatarDrawable.setInfo(obj);
        this.avatarImage.setForUserOrChat(obj, this.avatarDrawable);
    }

    public void setProgress(float progress) {
        setProgress(progress, true);
    }

    public void setProgress(float progress, boolean animate) {
        setProgress(progress, animate, progress != 0.0f);
    }

    public void setProgress(float progress, boolean animate, boolean useSpring) {
        if (animate) {
            SpringAnimation springAnimation = this.menuSpring;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            ValueAnimator valueAnimator = this.menuAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            boolean z = false;
            this.scaleIn = false;
            this.scaleOut = false;
            if (useSpring) {
                final float startValue = this.menuProgress * SPRING_MULTIPLIER;
                SpringAnimation startValue2 = new SpringAnimation(this, MENU_PROGRESS).setStartValue(startValue);
                this.menuSpring = startValue2;
                final boolean reverse = progress < this.menuProgress;
                final float finalPos = SPRING_MULTIPLIER * progress;
                this.scaleIn = reverse;
                if (!reverse) {
                    z = true;
                }
                this.scaleOut = z;
                startValue2.setSpring(new SpringForce(finalPos).setFinalPosition(finalPos).setStiffness(450.0f).setDampingRatio(1.0f));
                this.menuSpring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectView$$ExternalSyntheticLambda2
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                        SenderSelectView.this.m2995lambda$setProgress$2$orgtelegramuiComponentsSenderSelectView(reverse, startValue, finalPos, dynamicAnimation, f, f2);
                    }
                });
                this.menuSpring.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectView$$ExternalSyntheticLambda1
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                        SenderSelectView.this.m2996lambda$setProgress$3$orgtelegramuiComponentsSenderSelectView(dynamicAnimation, z2, f, f2);
                    }
                });
                this.menuSpring.start();
                return;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(this.menuProgress, progress).setDuration(200L);
            this.menuAnimator = duration;
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.menuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SenderSelectView.this.m2997lambda$setProgress$4$orgtelegramuiComponentsSenderSelectView(valueAnimator2);
                }
            });
            this.menuAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SenderSelectView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation == SenderSelectView.this.menuAnimator) {
                        SenderSelectView.this.menuAnimator = null;
                    }
                }
            });
            this.menuAnimator.start();
            return;
        }
        this.menuProgress = progress;
        invalidate();
    }

    /* renamed from: lambda$setProgress$2$org-telegram-ui-Components-SenderSelectView */
    public /* synthetic */ void m2995lambda$setProgress$2$orgtelegramuiComponentsSenderSelectView(boolean reverse, float startValue, float finalPos, DynamicAnimation animation, float value, float velocity) {
        if (reverse) {
            if (value > startValue / 2.0f || !this.scaleIn) {
                return;
            }
        } else if (value < finalPos / 2.0f || !this.scaleOut) {
            return;
        }
        this.scaleIn = !reverse;
        this.scaleOut = reverse;
    }

    /* renamed from: lambda$setProgress$3$org-telegram-ui-Components-SenderSelectView */
    public /* synthetic */ void m2996lambda$setProgress$3$orgtelegramuiComponentsSenderSelectView(DynamicAnimation animation, boolean canceled, float value, float velocity) {
        this.scaleIn = false;
        this.scaleOut = false;
        if (!canceled) {
            animation.cancel();
        }
        if (animation == this.menuSpring) {
            this.menuSpring = null;
        }
    }

    /* renamed from: lambda$setProgress$4$org-telegram-ui-Components-SenderSelectView */
    public /* synthetic */ void m2997lambda$setProgress$4$orgtelegramuiComponentsSenderSelectView(ValueAnimator animation) {
        this.menuProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public float getProgress() {
        return this.menuProgress;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || this.selectorDrawable == who;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.selectorDrawable.setState(getDrawableState());
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.selectorDrawable.jumpToCurrentState();
    }
}
