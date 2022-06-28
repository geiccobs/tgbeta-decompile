package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class AttachBotIntroTopView extends View {
    private static final int ICONS_SIDE_PADDING = 24;
    private static final int ICONS_SIZE_DP = 42;
    private Drawable attachDrawable;
    private ImageReceiver imageReceiver;
    private Paint paint = new Paint(1);
    private Paint backgroundPaint = new Paint(1);

    public AttachBotIntroTopView(Context context) {
        super(context);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.imageReceiver = imageReceiver;
        imageReceiver.setAlpha(0.0f);
        this.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.Components.AttachBotIntroTopView$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
                AttachBotIntroTopView.this.m2189lambda$new$1$orgtelegramuiComponentsAttachBotIntroTopView(imageReceiver2, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver2) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver2);
            }
        });
        this.attachDrawable = ContextCompat.getDrawable(context, R.drawable.input_attach).mutate().getConstantState().newDrawable();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AttachBotIntroTopView */
    public /* synthetic */ void m2189lambda$new$1$orgtelegramuiComponentsAttachBotIntroTopView(ImageReceiver imageReceiver1, boolean set, boolean thumb, boolean memCache) {
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.AttachBotIntroTopView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AttachBotIntroTopView.this.m2188lambda$new$0$orgtelegramuiComponentsAttachBotIntroTopView(valueAnimator);
            }
        });
        anim.start();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AttachBotIntroTopView */
    public /* synthetic */ void m2188lambda$new$0$orgtelegramuiComponentsAttachBotIntroTopView(ValueAnimator animation) {
        this.imageReceiver.setAlpha(((Float) animation.getAnimatedValue()).floatValue());
        invalidate();
    }

    public void setAttachBot(TLRPC.TL_attachMenuBot bot) {
        TLRPC.TL_attachMenuBotIcon icon = MediaDataController.getStaticAttachMenuBotIcon(bot);
        if (icon != null) {
            this.imageReceiver.setImage(ImageLocation.getForDocument(icon.icon), "42_42", DocumentObject.getSvgThumb(icon.icon, Theme.key_dialogTextGray2, 1.0f), "svg", bot, 0);
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.backgroundPaint.setColor(color);
    }

    public void setColor(int color) {
        this.attachDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        this.paint.setColor(color);
        this.imageReceiver.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth(), getHeight() + AndroidUtilities.dp(6.0f));
        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.backgroundPaint);
        this.imageReceiver.setImageCoords((getWidth() / 2.0f) - AndroidUtilities.dp(66.0f), (getHeight() / 2.0f) - (AndroidUtilities.dp(42.0f) / 2.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
        this.imageReceiver.draw(canvas);
        canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(8.0f), getHeight() / 2.0f, (getWidth() / 2.0f) + AndroidUtilities.dp(8.0f), getHeight() / 2.0f, this.paint);
        canvas.drawLine(getWidth() / 2.0f, (getHeight() / 2.0f) - AndroidUtilities.dp(8.0f), getWidth() / 2.0f, (getHeight() / 2.0f) + AndroidUtilities.dp(8.0f), this.paint);
        this.attachDrawable.setBounds((getWidth() / 2) + AndroidUtilities.dp(24.0f), (getHeight() / 2) - (AndroidUtilities.dp(42.0f) / 2), (getWidth() / 2) + AndroidUtilities.dp(66.0f), (getHeight() / 2) + (AndroidUtilities.dp(42.0f) / 2));
        this.attachDrawable.draw(canvas);
    }
}
