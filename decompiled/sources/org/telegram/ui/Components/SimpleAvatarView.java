package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SimpleAvatarView extends View {
    public static final int SELECT_ANIMATION_DURATION = 200;
    private ValueAnimator animator;
    private boolean isAvatarHidden;
    private float selectProgress;
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private Paint selectPaint = new Paint(1);

    public SimpleAvatarView(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.selectPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
    }

    public SimpleAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.selectPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
    }

    public SimpleAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.selectPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float scale = (this.selectProgress * 0.1f) + 0.9f;
        canvas.scale(scale, scale);
        this.selectPaint.setColor(Theme.getColor(Theme.key_dialogTextBlue));
        Paint paint = this.selectPaint;
        paint.setAlpha((int) (Color.alpha(paint.getColor()) * this.selectProgress));
        float stroke = this.selectPaint.getStrokeWidth();
        AndroidUtilities.rectTmp.set(stroke, stroke, getWidth() - stroke, getHeight() - stroke);
        canvas.drawArc(AndroidUtilities.rectTmp, -90.0f, this.selectProgress * 360.0f, false, this.selectPaint);
        canvas.restore();
        if (!this.isAvatarHidden) {
            float pad = this.selectPaint.getStrokeWidth() * 2.5f * this.selectProgress;
            this.avatarImage.setImageCoords(pad, pad, getWidth() - (pad * 2.0f), getHeight() - (2.0f * pad));
            this.avatarImage.draw(canvas);
        }
    }

    public void setAvatar(TLObject obj) {
        this.avatarDrawable.setInfo(obj);
        this.avatarImage.setForUserOrChat(obj, this.avatarDrawable);
    }

    @Override // android.view.View
    public boolean isSelected() {
        return this.selectProgress == 1.0f;
    }

    public void setSelected(boolean s, boolean animate) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float to = 1.0f;
        if (animate) {
            if (!s) {
                to = 0.0f;
            }
            ValueAnimator anim = ValueAnimator.ofFloat(this.selectProgress, to).setDuration(200L);
            anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SimpleAvatarView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SimpleAvatarView.this.m3058lambda$setSelected$0$orgtelegramuiComponentsSimpleAvatarView(valueAnimator2);
                }
            });
            anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SimpleAvatarView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (SimpleAvatarView.this.animator == animation) {
                        SimpleAvatarView.this.animator = null;
                    }
                }
            });
            anim.start();
            this.animator = anim;
            return;
        }
        if (!s) {
            to = 0.0f;
        }
        this.selectProgress = to;
        invalidate();
    }

    /* renamed from: lambda$setSelected$0$org-telegram-ui-Components-SimpleAvatarView */
    public /* synthetic */ void m3058lambda$setSelected$0$orgtelegramuiComponentsSimpleAvatarView(ValueAnimator animation) {
        this.selectProgress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setHideAvatar(boolean h) {
        this.isAvatarHidden = h;
        invalidate();
    }
}
