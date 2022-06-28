package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageEnterTransitionContainer;
/* loaded from: classes4.dex */
public class VoiceMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    private final ValueAnimator animator;
    MessageEnterTransitionContainer container;
    float fromRadius;
    private final Paint gradientPaint;
    private final LinearGradient gradientShader;
    float lastToCx;
    float lastToCy;
    private final RecyclerListView listView;
    private final int messageId;
    private final ChatMessageCell messageView;
    float progress;
    private final ChatActivityEnterView.RecordCircle recordCircle;
    private final Theme.ResourcesProvider resourcesProvider;
    final Paint circlePaint = new Paint(1);
    private final Matrix gradientMatrix = new Matrix();

    public VoiceMessageEnterTransition(final ChatMessageCell messageView, ChatActivityEnterView chatActivityEnterView, RecyclerListView listView, final MessageEnterTransitionContainer container, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.messageView = messageView;
        this.container = container;
        this.listView = listView;
        this.fromRadius = chatActivityEnterView.getRecordCicle().drawingCircleRadius;
        messageView.setEnterTransitionInProgress(true);
        ChatActivityEnterView.RecordCircle recordCicle = chatActivityEnterView.getRecordCicle();
        this.recordCircle = recordCicle;
        recordCicle.voiceEnterTransitionInProgress = true;
        recordCicle.skipDraw = true;
        Paint paint = new Paint(1);
        this.gradientPaint = paint;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        LinearGradient linearGradient = new LinearGradient(0.0f, AndroidUtilities.dp(12.0f), 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
        this.gradientShader = linearGradient;
        paint.setShader(linearGradient);
        this.messageId = messageView.getMessageObject().stableId;
        container.addTransition(this);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.VoiceMessageEnterTransition$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoiceMessageEnterTransition.this.m4799lambda$new$0$orgtelegramuiVoiceMessageEnterTransition(container, valueAnimator);
            }
        });
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.setDuration(220L);
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoiceMessageEnterTransition.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                messageView.setEnterTransitionInProgress(false);
                container.removeTransition(VoiceMessageEnterTransition.this);
                VoiceMessageEnterTransition.this.recordCircle.skipDraw = false;
            }
        });
        if (messageView.getSeekBarWaveform() != null) {
            messageView.getSeekBarWaveform().setSent();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-VoiceMessageEnterTransition */
    public /* synthetic */ void m4799lambda$new$0$orgtelegramuiVoiceMessageEnterTransition(MessageEnterTransitionContainer container, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        container.invalidate();
    }

    public void start() {
        this.animator.start();
    }

    @Override // org.telegram.ui.MessageEnterTransitionContainer.Transition
    public void onDraw(Canvas canvas) {
        float toCx;
        float toCy;
        float progress;
        float cy;
        float radius;
        float cx;
        int clipBottom;
        float moveProgress = this.progress;
        float f = this.progress;
        float hideWavesProgress = f > 0.6f ? 1.0f : f / 0.6f;
        float fromCx = (this.recordCircle.drawingCx + this.recordCircle.getX()) - this.container.getX();
        float fromCy = (this.recordCircle.drawingCy + this.recordCircle.getY()) - this.container.getY();
        if (this.messageView.getMessageObject().stableId == this.messageId) {
            float toCy2 = ((this.messageView.getRadialProgress().getProgressRect().centerY() + this.messageView.getY()) + this.listView.getY()) - this.container.getY();
            toCx = ((this.messageView.getRadialProgress().getProgressRect().centerX() + this.messageView.getX()) + this.listView.getX()) - this.container.getX();
            toCy = toCy2;
        } else {
            toCx = this.lastToCx;
            toCy = this.lastToCy;
        }
        this.lastToCx = toCx;
        this.lastToCy = toCy;
        float progress2 = CubicBezierInterpolator.DEFAULT.getInterpolation(moveProgress);
        float xProgress = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(moveProgress);
        float cx2 = ((1.0f - xProgress) * fromCx) + (toCx * xProgress);
        float cy2 = ((1.0f - progress2) * fromCy) + (toCy * progress2);
        float toRadius = this.messageView.getRadialProgress().getProgressRect().height() / 2.0f;
        float radius2 = (this.fromRadius * (1.0f - progress2)) + (toRadius * progress2);
        float listViewBottom = (this.listView.getY() - this.container.getY()) + this.listView.getMeasuredHeight();
        if (this.container.getMeasuredHeight() > 0) {
            radius = radius2;
            cy = cy2;
            cx = cx2;
            progress = progress2;
            canvas.saveLayerAlpha(0.0f, this.container.getMeasuredHeight() - AndroidUtilities.dp(400.0f), this.container.getMeasuredWidth(), this.container.getMeasuredHeight(), 255, 31);
            clipBottom = (int) ((this.container.getMeasuredHeight() * (1.0f - progress2)) + (listViewBottom * progress2));
        } else {
            radius = radius2;
            cy = cy2;
            cx = cx2;
            progress = progress2;
            canvas.save();
            clipBottom = 0;
        }
        float progress3 = progress;
        this.circlePaint.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_messagePanelVoiceBackground), getThemedColor(this.messageView.getRadialProgress().getCircleColorKey()), progress3));
        float cy3 = cy;
        this.recordCircle.drawWaves(canvas, cx, cy3, 1.0f - hideWavesProgress);
        float radius3 = radius;
        canvas.drawCircle(cx, cy3, radius3, this.circlePaint);
        canvas.save();
        float scale = radius3 / toRadius;
        canvas.scale(scale, scale, cx, cy3);
        canvas.translate(cx - this.messageView.getRadialProgress().getProgressRect().centerX(), cy3 - this.messageView.getRadialProgress().getProgressRect().centerY());
        this.messageView.getRadialProgress().setOverrideAlpha(progress3);
        this.messageView.getRadialProgress().setDrawBackground(false);
        this.messageView.getRadialProgress().draw(canvas);
        this.messageView.getRadialProgress().setDrawBackground(true);
        this.messageView.getRadialProgress().setOverrideAlpha(1.0f);
        canvas.restore();
        if (this.container.getMeasuredHeight() > 0) {
            this.gradientMatrix.setTranslate(0.0f, clipBottom);
            this.gradientShader.setLocalMatrix(this.gradientMatrix);
            canvas.drawRect(0.0f, clipBottom, this.container.getMeasuredWidth(), this.container.getMeasuredHeight(), this.gradientPaint);
        }
        canvas.restore();
        this.recordCircle.drawIcon(canvas, (int) fromCx, (int) fromCy, 1.0f - moveProgress);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
