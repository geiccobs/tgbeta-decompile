package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class StorageDiagramView extends View {
    private float[] animateToPercentage;
    private ClearViewData[] data;
    private float[] drawingPercentage;
    int enabledCount;
    StaticLayout layout1;
    StaticLayout layout2;
    private float[] startFromPercentage;
    ValueAnimator valueAnimator;
    private RectF rectF = new RectF();
    private float singleProgress = 0.0f;
    TextPaint textPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);

    public StorageDiagramView(Context context) {
        super(context);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(110.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(110.0f), C.BUFFER_FLAG_ENCRYPTED));
        this.rectF.set(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), getMeasuredWidth() - AndroidUtilities.dp(3.0f), getMeasuredHeight() - AndroidUtilities.dp(3.0f));
        updateDescription();
        this.textPaint.setTextSize(AndroidUtilities.dp(24.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textPaint2.setTextSize(AndroidUtilities.dp(13.0f));
    }

    public void setData(ClearViewData[] data) {
        this.data = data;
        invalidate();
        this.drawingPercentage = new float[data.length];
        this.animateToPercentage = new float[data.length];
        this.startFromPercentage = new float[data.length];
        update(false);
        if (this.enabledCount > 1) {
            this.singleProgress = 0.0f;
        } else {
            this.singleProgress = 1.0f;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        double d;
        int i;
        float percent;
        float a;
        float a2;
        if (this.data == null) {
            return;
        }
        float f = 1.0f;
        float f2 = 0.0f;
        if (this.enabledCount > 1) {
            float f3 = this.singleProgress;
            if (f3 > 0.0f) {
                double d2 = f3;
                Double.isNaN(d2);
                float f4 = (float) (d2 - 0.04d);
                this.singleProgress = f4;
                if (f4 < 0.0f) {
                    this.singleProgress = 0.0f;
                }
            }
        } else {
            float f5 = this.singleProgress;
            if (f5 < 1.0f) {
                double d3 = f5;
                Double.isNaN(d3);
                float f6 = (float) (d3 + 0.04d);
                this.singleProgress = f6;
                if (f6 > 1.0f) {
                    this.singleProgress = 1.0f;
                }
            }
        }
        float startFrom = 0.0f;
        int i2 = 0;
        while (true) {
            ClearViewData[] clearViewDataArr = this.data;
            d = 180.0d;
            i = 255;
            if (i2 >= clearViewDataArr.length) {
                break;
            }
            if (clearViewDataArr[i2] != null) {
                float[] fArr = this.drawingPercentage;
                if (fArr[i2] != 0.0f) {
                    float percent2 = fArr[i2];
                    if (clearViewDataArr[i2].firstDraw) {
                        float a3 = ((-360.0f) * percent2) + ((f - this.singleProgress) * 10.0f);
                        if (a3 <= 0.0f) {
                            a2 = a3;
                        } else {
                            a2 = 0.0f;
                        }
                        this.data[i2].paint.setColor(Theme.getColor(this.data[i2].color));
                        this.data[i2].paint.setAlpha(255);
                        float r = this.rectF.width() / 2.0f;
                        double d4 = r;
                        Double.isNaN(d4);
                        double d5 = a2;
                        Double.isNaN(d5);
                        float len = (float) (((d4 * 3.141592653589793d) / 180.0d) * d5);
                        if (Math.abs(len) <= f) {
                            float centerX = this.rectF.centerX();
                            double d6 = r;
                            double cos = Math.cos(Math.toRadians((-90.0f) - (startFrom * 360.0f)));
                            Double.isNaN(d6);
                            float x = centerX + ((float) (d6 * cos));
                            float centerY = this.rectF.centerY();
                            double d7 = r;
                            double sin = Math.sin(Math.toRadians((-90.0f) - (360.0f * startFrom)));
                            Double.isNaN(d7);
                            float y = centerY + ((float) (d7 * sin));
                            if (Build.VERSION.SDK_INT >= 21) {
                                canvas.drawPoint(x, y, this.data[i2].paint);
                            } else {
                                this.data[i2].paint.setStyle(Paint.Style.FILL);
                                canvas.drawCircle(x, y, this.data[i2].paint.getStrokeWidth() / 2.0f, this.data[i2].paint);
                            }
                        } else {
                            this.data[i2].paint.setStyle(Paint.Style.STROKE);
                            canvas.drawArc(this.rectF, (-90.0f) - (360.0f * startFrom), a2, false, this.data[i2].paint);
                        }
                    }
                    startFrom += percent2;
                }
            }
            i2++;
            f = 1.0f;
        }
        float startFrom2 = 0.0f;
        int i3 = 0;
        while (true) {
            ClearViewData[] clearViewDataArr2 = this.data;
            if (i3 >= clearViewDataArr2.length) {
                break;
            }
            if (clearViewDataArr2[i3] != null) {
                float[] fArr2 = this.drawingPercentage;
                if (fArr2[i3] != f2) {
                    float percent3 = fArr2[i3];
                    if (!clearViewDataArr2[i3].firstDraw) {
                        float a4 = (percent3 * (-360.0f)) + ((1.0f - this.singleProgress) * 10.0f);
                        if (a4 <= f2) {
                            a = a4;
                        } else {
                            a = 0.0f;
                        }
                        this.data[i3].paint.setColor(Theme.getColor(this.data[i3].color));
                        this.data[i3].paint.setAlpha(i);
                        float r2 = this.rectF.width() / 2.0f;
                        double d8 = r2;
                        Double.isNaN(d8);
                        double d9 = a;
                        Double.isNaN(d9);
                        float len2 = (float) (((d8 * 3.141592653589793d) / d) * d9);
                        if (Math.abs(len2) <= 1.0f) {
                            float centerX2 = this.rectF.centerX();
                            double d10 = r2;
                            double cos2 = Math.cos(Math.toRadians((-90.0f) - (startFrom2 * 360.0f)));
                            Double.isNaN(d10);
                            float x2 = centerX2 + ((float) (d10 * cos2));
                            float centerY2 = this.rectF.centerY();
                            double d11 = r2;
                            percent = percent3;
                            double sin2 = Math.sin(Math.toRadians((-90.0f) - (startFrom2 * 360.0f)));
                            Double.isNaN(d11);
                            float y2 = centerY2 + ((float) (d11 * sin2));
                            if (Build.VERSION.SDK_INT >= 21) {
                                canvas.drawPoint(x2, y2, this.data[i3].paint);
                            } else {
                                this.data[i3].paint.setStyle(Paint.Style.FILL);
                                canvas.drawCircle(x2, y2, this.data[i3].paint.getStrokeWidth() / 2.0f, this.data[i3].paint);
                            }
                        } else {
                            percent = percent3;
                            this.data[i3].paint.setStyle(Paint.Style.STROKE);
                            float len3 = a;
                            canvas.drawArc(this.rectF, (-90.0f) - (startFrom2 * 360.0f), len3, false, this.data[i3].paint);
                        }
                    } else {
                        percent = percent3;
                    }
                    startFrom2 += percent;
                }
            }
            i3++;
            i = 255;
            f2 = 0.0f;
            d = 180.0d;
        }
        if (this.layout1 != null) {
            canvas.save();
            canvas.translate((getMeasuredWidth() - this.layout1.getWidth()) >> 1, (((getMeasuredHeight() - this.layout1.getHeight()) - this.layout2.getHeight()) >> 1) + AndroidUtilities.dp(2.0f));
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textPaint2.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.layout1.draw(canvas);
            canvas.translate(0.0f, this.layout1.getHeight());
            this.layout2.draw(canvas);
            canvas.restore();
        }
    }

    /* loaded from: classes5.dex */
    public static class ClearViewData {
        public String color;
        Paint paint;
        private final StorageDiagramView parentView;
        public long size;
        public boolean clear = true;
        boolean firstDraw = false;

        public ClearViewData(StorageDiagramView parentView) {
            Paint paint = new Paint(1);
            this.paint = paint;
            this.parentView = parentView;
            paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(5.0f));
            this.paint.setStrokeCap(Paint.Cap.ROUND);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
        }

        public void setClear(boolean clear) {
            if (this.clear != clear) {
                this.clear = clear;
                this.parentView.updateDescription();
                this.firstDraw = true;
                this.parentView.update(true);
            }
        }
    }

    public void update(boolean animate) {
        long total = 0;
        final ClearViewData[] data = this.data;
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null && data[i].clear) {
                total += data[i].size;
            }
        }
        float k = 0.0f;
        float max = 0.0f;
        this.enabledCount = 0;
        for (int i2 = 0; i2 < data.length; i2++) {
            if (data[i2] != null && data[i2].clear) {
                this.enabledCount++;
            }
            if (data[i2] == null || !data[i2].clear) {
                this.animateToPercentage[i2] = 0.0f;
            } else {
                float percent = ((float) data[i2].size) / ((float) total);
                if (percent < 0.02777f) {
                    percent = 0.02777f;
                }
                k += percent;
                if (percent > max && data[i2].clear) {
                    max = percent;
                }
                this.animateToPercentage[i2] = percent;
            }
        }
        if (k > 1.0f) {
            float l = 1.0f / k;
            for (int i3 = 0; i3 < data.length; i3++) {
                if (data[i3] != null) {
                    float[] fArr = this.animateToPercentage;
                    fArr[i3] = fArr[i3] * l;
                }
            }
        }
        if (!animate) {
            System.arraycopy(this.animateToPercentage, 0, this.drawingPercentage, 0, data.length);
            return;
        }
        System.arraycopy(this.drawingPercentage, 0, this.startFromPercentage, 0, data.length);
        ValueAnimator valueAnimator = this.valueAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.valueAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.StorageDiagramView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                StorageDiagramView.this.m3114lambda$update$0$orgtelegramuiComponentsStorageDiagramView(data, valueAnimator2);
            }
        });
        this.valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.StorageDiagramView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                int i4 = 0;
                while (true) {
                    ClearViewData[] clearViewDataArr = data;
                    if (i4 < clearViewDataArr.length) {
                        if (clearViewDataArr[i4] != null) {
                            clearViewDataArr[i4].firstDraw = false;
                        }
                        i4++;
                    } else {
                        return;
                    }
                }
            }
        });
        this.valueAnimator.setDuration(450L);
        this.valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        this.valueAnimator.start();
    }

    /* renamed from: lambda$update$0$org-telegram-ui-Components-StorageDiagramView */
    public /* synthetic */ void m3114lambda$update$0$orgtelegramuiComponentsStorageDiagramView(ClearViewData[] data, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        for (int i = 0; i < data.length; i++) {
            this.drawingPercentage[i] = (this.startFromPercentage[i] * (1.0f - v)) + (this.animateToPercentage[i] * v);
        }
        invalidate();
    }

    public void updateDescription() {
        if (this.data == null) {
            return;
        }
        long total = 0;
        int i = 0;
        while (true) {
            ClearViewData[] clearViewDataArr = this.data;
            if (i >= clearViewDataArr.length) {
                break;
            }
            if (clearViewDataArr[i] != null && clearViewDataArr[i].clear) {
                total += this.data[i].size;
            }
            i++;
        }
        String str = " ";
        String[] str2 = AndroidUtilities.formatFileSize(total).split(str);
        if (str2.length > 1) {
            this.layout1 = new StaticLayout(total == 0 ? str : str2[0], this.textPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            if (total != 0) {
                str = str2[1];
            }
            this.layout2 = new StaticLayout(str, this.textPaint2, getMeasuredWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        }
    }
}
