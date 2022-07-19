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
/* loaded from: classes3.dex */
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

    public VoiceMessageEnterTransition(final ChatMessageCell chatMessageCell, ChatActivityEnterView chatActivityEnterView, RecyclerListView recyclerListView, final MessageEnterTransitionContainer messageEnterTransitionContainer, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.messageView = chatMessageCell;
        this.container = messageEnterTransitionContainer;
        this.listView = recyclerListView;
        this.fromRadius = chatActivityEnterView.getRecordCicle().drawingCircleRadius;
        chatMessageCell.setEnterTransitionInProgress(true);
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
        this.messageId = chatMessageCell.getMessageObject().stableId;
        messageEnterTransitionContainer.addTransition(this);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.VoiceMessageEnterTransition$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoiceMessageEnterTransition.this.lambda$new$0(messageEnterTransitionContainer, valueAnimator);
            }
        });
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.setDuration(220L);
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.VoiceMessageEnterTransition.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                chatMessageCell.setEnterTransitionInProgress(false);
                messageEnterTransitionContainer.removeTransition(VoiceMessageEnterTransition.this);
                VoiceMessageEnterTransition.this.recordCircle.skipDraw = false;
            }
        });
        if (chatMessageCell.getSeekBarWaveform() != null) {
            chatMessageCell.getSeekBarWaveform().setSent();
        }
    }

    public /* synthetic */ void lambda$new$0(MessageEnterTransitionContainer messageEnterTransitionContainer, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        messageEnterTransitionContainer.invalidate();
    }

    public void start() {
        this.animator.start();
    }

    @Override // org.telegram.ui.MessageEnterTransitionContainer.Transition
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        float f3 = this.progress;
        float f4 = f3 > 0.6f ? 1.0f : f3 / 0.6f;
        ChatActivityEnterView.RecordCircle recordCircle = this.recordCircle;
        float x = (recordCircle.drawingCx + recordCircle.getX()) - this.container.getX();
        ChatActivityEnterView.RecordCircle recordCircle2 = this.recordCircle;
        float y = (recordCircle2.drawingCy + recordCircle2.getY()) - this.container.getY();
        if (this.messageView.getMessageObject().stableId != this.messageId) {
            f2 = this.lastToCx;
            f = this.lastToCy;
        } else {
            f = ((this.messageView.getRadialProgress().getProgressRect().centerY() + this.messageView.getY()) + this.listView.getY()) - this.container.getY();
            f2 = ((this.messageView.getRadialProgress().getProgressRect().centerX() + this.messageView.getX()) + this.listView.getX()) - this.container.getX();
        }
        this.lastToCx = f2;
        this.lastToCy = f;
        float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(f3);
        float interpolation2 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(f3);
        float f5 = ((1.0f - interpolation2) * x) + (f2 * interpolation2);
        float f6 = 1.0f - interpolation;
        float f7 = (y * f6) + (f * interpolation);
        float height = this.messageView.getRadialProgress().getProgressRect().height() / 2.0f;
        float f8 = (this.fromRadius * f6) + (height * interpolation);
        int measuredHeight = this.container.getMeasuredHeight() > 0 ? (int) ((this.container.getMeasuredHeight() * f6) + (((this.listView.getY() - this.container.getY()) + this.listView.getMeasuredHeight()) * interpolation)) : 0;
        this.circlePaint.setColor(ColorUtils.blendARGB(getThemedColor("chat_messagePanelVoiceBackground"), getThemedColor(this.messageView.getRadialProgress().getCircleColorKey()), interpolation));
        this.recordCircle.drawWaves(canvas, f5, f7, 1.0f - f4);
        canvas.drawCircle(f5, f7, f8, this.circlePaint);
        canvas.save();
        float f9 = f8 / height;
        canvas.scale(f9, f9, f5, f7);
        canvas.translate(f5 - this.messageView.getRadialProgress().getProgressRect().centerX(), f7 - this.messageView.getRadialProgress().getProgressRect().centerY());
        this.messageView.getRadialProgress().setOverrideAlpha(interpolation);
        this.messageView.getRadialProgress().setDrawBackground(false);
        this.messageView.getRadialProgress().draw(canvas);
        this.messageView.getRadialProgress().setDrawBackground(true);
        this.messageView.getRadialProgress().setOverrideAlpha(1.0f);
        canvas.restore();
        if (this.container.getMeasuredHeight() > 0) {
            this.gradientMatrix.setTranslate(0.0f, measuredHeight);
            this.gradientShader.setLocalMatrix(this.gradientMatrix);
        }
        this.recordCircle.drawIcon(canvas, (int) x, (int) y, 1.0f - f3);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
