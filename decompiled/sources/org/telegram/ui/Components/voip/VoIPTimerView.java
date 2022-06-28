package org.telegram.ui.Components.voip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.voip.VoIPService;
/* loaded from: classes5.dex */
public class VoIPTimerView extends View {
    String currentTimeStr;
    StaticLayout timerLayout;
    RectF rectF = new RectF();
    Paint activePaint = new Paint(1);
    Paint inactivePaint = new Paint(1);
    TextPaint textPaint = new TextPaint(1);
    private int signalBarCount = 4;
    Runnable updater = new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPTimerView$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            VoIPTimerView.this.m3272lambda$new$0$orgtelegramuiComponentsvoipVoIPTimerView();
        }
    };

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-VoIPTimerView */
    public /* synthetic */ void m3272lambda$new$0$orgtelegramuiComponentsvoipVoIPTimerView() {
        if (getVisibility() == 0) {
            updateTimer();
        }
    }

    public VoIPTimerView(Context context) {
        super(context);
        this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
        this.textPaint.setColor(-1);
        this.textPaint.setShadowLayer(AndroidUtilities.dp(3.0f), 0.0f, AndroidUtilities.dp(0.6666667f), 1275068416);
        this.activePaint.setColor(ColorUtils.setAlphaComponent(-1, 229));
        this.inactivePaint.setColor(ColorUtils.setAlphaComponent(-1, 102));
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        StaticLayout timerLayout = this.timerLayout;
        if (timerLayout != null) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), timerLayout.getHeight());
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(15.0f));
        }
    }

    public void updateTimer() {
        removeCallbacks(this.updater);
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            return;
        }
        String str = AndroidUtilities.formatLongDuration((int) (service.getCallDuration() / 1000));
        String str2 = this.currentTimeStr;
        if (str2 == null || !str2.equals(str)) {
            this.currentTimeStr = str;
            if (this.timerLayout == null) {
                requestLayout();
            }
            String str3 = this.currentTimeStr;
            TextPaint textPaint = this.textPaint;
            this.timerLayout = new StaticLayout(str3, textPaint, (int) textPaint.measureText(str3), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        postDelayed(this.updater, 300L);
        invalidate();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            if (visibility == 0) {
                this.currentTimeStr = "00:00";
                String str = this.currentTimeStr;
                TextPaint textPaint = this.textPaint;
                this.timerLayout = new StaticLayout(str, textPaint, (int) textPaint.measureText(str), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                updateTimer();
            } else {
                this.currentTimeStr = null;
                this.timerLayout = null;
            }
        }
        super.setVisibility(visibility);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        StaticLayout timerLayout = this.timerLayout;
        int totalWidth = timerLayout == null ? 0 : timerLayout.getWidth() + AndroidUtilities.dp(21.0f);
        canvas.save();
        canvas.translate((getMeasuredWidth() - totalWidth) / 2.0f, 0.0f);
        canvas.save();
        canvas.translate(0.0f, (getMeasuredHeight() - AndroidUtilities.dp(11.0f)) / 2.0f);
        for (int i = 0; i < 4; i++) {
            Paint p = i + 1 > this.signalBarCount ? this.inactivePaint : this.activePaint;
            this.rectF.set(AndroidUtilities.dpf2(4.16f) * i, AndroidUtilities.dpf2(2.75f) * (3 - i), (AndroidUtilities.dpf2(4.16f) * i) + AndroidUtilities.dpf2(2.75f), AndroidUtilities.dp(11.0f));
            canvas.drawRoundRect(this.rectF, AndroidUtilities.dpf2(0.7f), AndroidUtilities.dpf2(0.7f), p);
        }
        canvas.restore();
        if (timerLayout != null) {
            canvas.translate(AndroidUtilities.dp(21.0f), 0.0f);
            timerLayout.draw(canvas);
        }
        canvas.restore();
    }

    public void setSignalBarCount(int count) {
        this.signalBarCount = count;
        invalidate();
    }
}
