package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SharedMediaFastScrollTooltip extends FrameLayout {
    public SharedMediaFastScrollTooltip(Context context) {
        super(context);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("SharedMediaFastScrollHint", R.string.SharedMediaFastScrollHint));
        textView.setTextSize(1, 14.0f);
        textView.setMaxLines(3);
        textView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 16, 46.0f, 8.0f, 8.0f, 8.0f));
        TooltipDrawableView hintView = new TooltipDrawableView(context);
        addView(hintView, LayoutHelper.createFrame(29, 32.0f, 0, 8.0f, 8.0f, 8.0f, 8.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(300.0f), View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(32.0f)), Integer.MIN_VALUE), heightMeasureSpec);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class TooltipDrawableView extends View {
        float toProgress;
        Random random = new Random();
        Paint paint = new Paint(1);
        Paint paint2 = new Paint(1);
        float progress = 1.0f;
        float fromProgress = 0.0f;
        Paint fadePaint = new Paint();
        Paint fadePaintBack = new Paint();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TooltipDrawableView(Context context) {
            super(context);
            SharedMediaFastScrollTooltip.this = r22;
            this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_chat_gifSaveHintText), 76));
            this.paint2.setColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
            LinearGradient gradient = new LinearGradient(0.0f, AndroidUtilities.dp(4.0f), 0.0f, 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.fadePaint.setShader(gradient);
            this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            LinearGradient gradient2 = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(4.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.fadePaintBack.setShader(gradient2);
            this.fadePaintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
            float f = 3.0f;
            int rectSize = (getMeasuredWidth() / 2) - AndroidUtilities.dp(3.0f);
            float f2 = 1.0f;
            int i = 7;
            int totalHeight = ((AndroidUtilities.dp(1.0f) + rectSize) * 7) + AndroidUtilities.dp(1.0f);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
            float f3 = this.progress;
            float progress = cubicBezierInterpolator.getInterpolation(f3 > 0.4f ? (f3 - 0.4f) / 0.6f : 0.0f);
            float p = (this.fromProgress * (1.0f - progress)) + (this.toProgress * progress);
            canvas.save();
            canvas.translate(0.0f, (-(totalHeight - (getMeasuredHeight() - AndroidUtilities.dp(4.0f)))) * p);
            int i2 = 0;
            while (i2 < i) {
                int y = AndroidUtilities.dp(f) + ((AndroidUtilities.dp(f2) + rectSize) * i2);
                AndroidUtilities.rectTmp.set(0.0f, y, rectSize, y + rectSize);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint);
                AndroidUtilities.rectTmp.set(AndroidUtilities.dp(f2) + rectSize, y, AndroidUtilities.dp(f2) + rectSize + rectSize, y + rectSize);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint);
                i2++;
                i = 7;
                f = 3.0f;
                f2 = 1.0f;
            }
            canvas.restore();
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(4.0f), this.fadePaint);
            canvas.translate(0.0f, getMeasuredHeight() - AndroidUtilities.dp(4.0f));
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(4.0f), this.fadePaintBack);
            canvas.restore();
            float y2 = AndroidUtilities.dp(3.0f) + ((getMeasuredHeight() - AndroidUtilities.dp(21.0f)) * p);
            AndroidUtilities.rectTmp.set(getMeasuredWidth() - AndroidUtilities.dp(3.0f), y2, getMeasuredWidth(), AndroidUtilities.dp(15.0f) + y2);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(1.5f), AndroidUtilities.dp(1.5f), this.paint2);
            float cy = AndroidUtilities.rectTmp.centerY();
            float cx = AndroidUtilities.dp(0.5f) + rectSize;
            AndroidUtilities.rectTmp.set(cx - AndroidUtilities.dp(8.0f), cy - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(8.0f) + cx, AndroidUtilities.dp(3.0f) + cy);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.paint2);
            float f4 = this.progress + 0.016f;
            this.progress = f4;
            if (f4 > 1.0f) {
                this.fromProgress = this.toProgress;
                float abs = Math.abs(this.random.nextInt() % 1001) / 1000.0f;
                this.toProgress = abs;
                if (abs > this.fromProgress) {
                    this.toProgress = abs + 0.3f;
                } else {
                    this.toProgress = abs - 0.3f;
                }
                this.toProgress = Math.max(0.0f, Math.min(1.0f, this.toProgress));
                this.progress = 0.0f;
            }
            invalidate();
        }
    }
}
