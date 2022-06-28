package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.PieLegendView;
import org.telegram.ui.Charts.view_data.TransitionParams;
/* loaded from: classes4.dex */
public class PieChartView extends StackLinearChartView<PieChartViewData> {
    float[] darawingValuesPercentage;
    boolean isEmpty;
    PieLegendView pieLegendView;
    float sum;
    TextPaint textPaint;
    float[] values;
    int currentSelection = -1;
    RectF rectF = new RectF();
    float MIN_TEXT_SIZE = AndroidUtilities.dp(9.0f);
    float MAX_TEXT_SIZE = AndroidUtilities.dp(13.0f);
    String[] lookupTable = new String[101];
    float emptyDataAlpha = 1.0f;
    int oldW = 0;
    int lastStartIndex = -1;
    int lastEndIndex = -1;

    public PieChartView(Context context) {
        super(context);
        for (int i = 1; i <= 100; i++) {
            String[] strArr = this.lookupTable;
            strArr[i] = i + "%";
        }
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setColor(-1);
        this.textPaint.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.canCaptureChartSelection = true;
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    protected void drawChart(Canvas canvas) {
        float a;
        float a2;
        int transitionAlpha;
        float sc;
        Canvas canvas2;
        int transitionAlpha2;
        int radius;
        float a3;
        float a4;
        int transitionAlpha3;
        int i;
        int i2;
        Canvas canvas3 = canvas;
        if (this.chartData == 0) {
            return;
        }
        int transitionAlpha4 = 255;
        if (canvas3 != null) {
            canvas.save();
        }
        if (this.transitionMode == 1) {
            transitionAlpha4 = (int) (this.transitionParams.progress * this.transitionParams.progress * 255.0f);
        }
        float f = 0.0f;
        if (this.isEmpty) {
            float f2 = this.emptyDataAlpha;
            if (f2 != 0.0f) {
                float f3 = f2 - 0.12f;
                this.emptyDataAlpha = f3;
                if (f3 < 0.0f) {
                    this.emptyDataAlpha = 0.0f;
                }
                invalidate();
            }
        } else {
            float f4 = this.emptyDataAlpha;
            if (f4 != 1.0f) {
                float f5 = f4 + 0.12f;
                this.emptyDataAlpha = f5;
                if (f5 > 1.0f) {
                    this.emptyDataAlpha = 1.0f;
                }
                invalidate();
            }
        }
        float f6 = this.emptyDataAlpha;
        int transitionAlpha5 = (int) (transitionAlpha4 * f6);
        float sc2 = (f6 * 0.6f) + 0.4f;
        if (canvas3 != null) {
            canvas3.scale(sc2, sc2, this.chartArea.centerX(), this.chartArea.centerY());
        }
        int radius2 = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
        this.rectF.set(this.chartArea.centerX() - radius2, (this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - radius2, this.chartArea.centerX() + radius2, this.chartArea.centerY() + AndroidUtilities.dp(16.0f) + radius2);
        int n = this.lines.size();
        float localSum = 0.0f;
        for (int i3 = 0; i3 < n; i3++) {
            float v = ((PieChartViewData) this.lines.get(i3)).drawingPart * ((PieChartViewData) this.lines.get(i3)).alpha;
            localSum += v;
        }
        if (localSum != 0.0f) {
            float a5 = -90.0f;
            int i4 = 0;
            while (true) {
                a = 2.0f;
                a2 = 8.0f;
                if (i4 >= n) {
                    break;
                }
                if (((PieChartViewData) this.lines.get(i4)).alpha > f || ((PieChartViewData) this.lines.get(i4)).enabled) {
                    ((PieChartViewData) this.lines.get(i4)).paint.setAlpha(transitionAlpha5);
                    float currentPercent = (((PieChartViewData) this.lines.get(i4)).drawingPart / localSum) * ((PieChartViewData) this.lines.get(i4)).alpha;
                    this.darawingValuesPercentage[i4] = currentPercent;
                    if (currentPercent != f) {
                        if (canvas3 != null) {
                            canvas.save();
                        }
                        double textAngle = a5 + ((currentPercent / 2.0f) * 360.0f);
                        if (((PieChartViewData) this.lines.get(i4)).selectionA > f) {
                            float ai = INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i4)).selectionA);
                            if (canvas3 == null) {
                                transitionAlpha3 = transitionAlpha5;
                            } else {
                                double cos = Math.cos(Math.toRadians(textAngle));
                                transitionAlpha3 = transitionAlpha5;
                                double dp = AndroidUtilities.dp(8.0f);
                                Double.isNaN(dp);
                                double d = cos * dp;
                                double d2 = ai;
                                Double.isNaN(d2);
                                float f7 = (float) (d2 * d);
                                double sin = Math.sin(Math.toRadians(textAngle));
                                double dp2 = AndroidUtilities.dp(8.0f);
                                Double.isNaN(dp2);
                                double d3 = sin * dp2;
                                double d4 = ai;
                                Double.isNaN(d4);
                                canvas3.translate(f7, (float) (d3 * d4));
                            }
                        } else {
                            transitionAlpha3 = transitionAlpha5;
                        }
                        ((PieChartViewData) this.lines.get(i4)).paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        ((PieChartViewData) this.lines.get(i4)).paint.setStrokeWidth(1.0f);
                        ((PieChartViewData) this.lines.get(i4)).paint.setAntiAlias(!USE_LINES);
                        if (canvas3 == null || this.transitionMode == 1) {
                            i = i4;
                            i2 = 255;
                        } else {
                            i2 = 255;
                            i = i4;
                            canvas.drawArc(this.rectF, a5, currentPercent * 360.0f, true, ((PieChartViewData) this.lines.get(i4)).paint);
                            ((PieChartViewData) this.lines.get(i)).paint.setStyle(Paint.Style.STROKE);
                            canvas.restore();
                        }
                        ((PieChartViewData) this.lines.get(i)).paint.setAlpha(i2);
                        a5 += 360.0f * currentPercent;
                        i4 = i + 1;
                        transitionAlpha5 = transitionAlpha3;
                        f = 0.0f;
                    }
                }
                i = i4;
                transitionAlpha3 = transitionAlpha5;
                i4 = i + 1;
                transitionAlpha5 = transitionAlpha3;
                f = 0.0f;
            }
            int n2 = transitionAlpha5;
            float a6 = -90.0f;
            if (canvas3 != null) {
                int i5 = 0;
                while (i5 < n) {
                    if (((PieChartViewData) this.lines.get(i5)).alpha > 0.0f || ((PieChartViewData) this.lines.get(i5)).enabled) {
                        float currentPercent2 = (((PieChartViewData) this.lines.get(i5)).drawingPart * ((PieChartViewData) this.lines.get(i5)).alpha) / localSum;
                        canvas.save();
                        double textAngle2 = ((currentPercent2 / a) * 360.0f) + a6;
                        if (((PieChartViewData) this.lines.get(i5)).selectionA > 0.0f) {
                            float ai2 = INTERPOLATOR.getInterpolation(((PieChartViewData) this.lines.get(i5)).selectionA);
                            double cos2 = Math.cos(Math.toRadians(textAngle2));
                            sc = sc2;
                            double dp3 = AndroidUtilities.dp(a2);
                            Double.isNaN(dp3);
                            double d5 = ai2;
                            Double.isNaN(d5);
                            double sin2 = Math.sin(Math.toRadians(textAngle2));
                            a4 = a6;
                            double dp4 = AndroidUtilities.dp(a2);
                            Double.isNaN(dp4);
                            double d6 = ai2;
                            Double.isNaN(d6);
                            canvas3.translate((float) (d5 * cos2 * dp3), (float) (sin2 * dp4 * d6));
                        } else {
                            a4 = a6;
                            sc = sc2;
                        }
                        int percent = (int) (100.0f * currentPercent2);
                        if (currentPercent2 < 0.02f || percent <= 0 || percent > 100) {
                            transitionAlpha2 = n2;
                            transitionAlpha = n;
                            canvas2 = canvas3;
                            radius = radius2;
                        } else {
                            double width = this.rectF.width() * 0.42f;
                            double sqrt = Math.sqrt(1.0f - currentPercent2);
                            Double.isNaN(width);
                            float rText = (float) (width * sqrt);
                            this.textPaint.setTextSize(this.MIN_TEXT_SIZE + (this.MAX_TEXT_SIZE * currentPercent2));
                            transitionAlpha2 = n2;
                            this.textPaint.setAlpha((int) (transitionAlpha2 * ((PieChartViewData) this.lines.get(i5)).alpha));
                            String str = this.lookupTable[percent];
                            double centerX = this.rectF.centerX();
                            double d7 = rText;
                            double cos3 = Math.cos(Math.toRadians(textAngle2));
                            Double.isNaN(d7);
                            Double.isNaN(centerX);
                            float f8 = (float) (centerX + (d7 * cos3));
                            double centerY = this.rectF.centerY();
                            radius = radius2;
                            transitionAlpha = n;
                            double d8 = rText;
                            double sin3 = Math.sin(Math.toRadians(textAngle2));
                            Double.isNaN(d8);
                            Double.isNaN(centerY);
                            canvas2 = canvas;
                            canvas2.drawText(str, f8, ((float) (centerY + (d8 * sin3))) - ((this.textPaint.descent() + this.textPaint.ascent()) / 2.0f), this.textPaint);
                        }
                        canvas.restore();
                        ((PieChartViewData) this.lines.get(i5)).paint.setAlpha(255);
                        a3 = a4 + (currentPercent2 * 360.0f);
                    } else {
                        a3 = a6;
                        sc = sc2;
                        transitionAlpha2 = n2;
                        transitionAlpha = n;
                        canvas2 = canvas3;
                        radius = radius2;
                    }
                    i5++;
                    a6 = a3;
                    radius2 = radius;
                    canvas3 = canvas2;
                    sc2 = sc;
                    n = transitionAlpha;
                    a = 2.0f;
                    a2 = 8.0f;
                    n2 = transitionAlpha2;
                }
                canvas.restore();
            }
        } else if (canvas3 != null) {
            canvas.restore();
        }
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    protected void drawPickerChart(Canvas canvas) {
        float f;
        float sum;
        int n;
        float yPercentage;
        if (this.chartData != 0) {
            int n2 = ((StackLinearChartData) this.chartData).xPercentage.length;
            int nl = this.lines.size();
            for (int k = 0; k < this.lines.size(); k++) {
                ((LineViewData) this.lines.get(k)).linesPathBottomSize = 0;
            }
            float p = (1.0f / ((StackLinearChartData) this.chartData).xPercentage.length) * this.pickerWidth;
            for (int i = 0; i < n2; i++) {
                float stackOffset = 0.0f;
                float xPoint = (p / 2.0f) + (((StackLinearChartData) this.chartData).xPercentage[i] * (this.pickerWidth - p));
                float sum2 = 0.0f;
                int drawingLinesCount = 0;
                boolean allDisabled = true;
                int k2 = 0;
                while (true) {
                    f = 0.0f;
                    if (k2 >= nl) {
                        break;
                    }
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != 0.0f) {
                        float v = line.line.y[i] * line.alpha;
                        sum2 += v;
                        if (v > 0.0f) {
                            drawingLinesCount++;
                            if (line.enabled) {
                                allDisabled = false;
                            }
                        }
                    }
                    k2++;
                }
                int k3 = 0;
                while (k3 < nl) {
                    LineViewData line2 = (LineViewData) this.lines.get(k3);
                    if (line2.enabled || line2.alpha != f) {
                        int[] y = line2.line.y;
                        if (drawingLinesCount == 1) {
                            if (y[i] == 0) {
                                yPercentage = 0.0f;
                            } else {
                                yPercentage = line2.alpha;
                            }
                        } else if (sum2 == f) {
                            yPercentage = 0.0f;
                        } else if (allDisabled) {
                            yPercentage = (y[i] / sum2) * line2.alpha * line2.alpha;
                        } else {
                            yPercentage = line2.alpha * (y[i] / sum2);
                        }
                        float yPoint = this.pikerHeight * yPercentage;
                        float[] fArr = line2.linesPath;
                        n = n2;
                        int n3 = line2.linesPathBottomSize;
                        sum = sum2;
                        line2.linesPathBottomSize = n3 + 1;
                        fArr[n3] = xPoint;
                        float[] fArr2 = line2.linesPath;
                        int i2 = line2.linesPathBottomSize;
                        line2.linesPathBottomSize = i2 + 1;
                        fArr2[i2] = (this.pikerHeight - yPoint) - stackOffset;
                        float[] fArr3 = line2.linesPath;
                        int i3 = line2.linesPathBottomSize;
                        line2.linesPathBottomSize = i3 + 1;
                        fArr3[i3] = xPoint;
                        float[] fArr4 = line2.linesPath;
                        int i4 = line2.linesPathBottomSize;
                        line2.linesPathBottomSize = i4 + 1;
                        fArr4[i4] = this.pikerHeight - stackOffset;
                        stackOffset += yPoint;
                    } else {
                        n = n2;
                        sum = sum2;
                    }
                    k3++;
                    n2 = n;
                    sum2 = sum;
                    f = 0.0f;
                }
            }
            for (int k4 = 0; k4 < nl; k4++) {
                LineViewData line3 = (LineViewData) this.lines.get(k4);
                line3.paint.setStrokeWidth(p);
                line3.paint.setAlpha(255);
                line3.paint.setAntiAlias(false);
                canvas.drawLines(line3.linesPath, 0, line3.linesPathBottomSize, line3.paint);
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawBottomLine(Canvas canvas) {
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawSelection(Canvas canvas) {
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawBottomSignature(Canvas canvas) {
    }

    public void setData(StackLinearChartData chartData) {
        super.setData((PieChartView) chartData);
        if (chartData != null) {
            this.values = new float[chartData.lines.size()];
            this.darawingValuesPercentage = new float[chartData.lines.size()];
            onPickerDataChanged(false, true, false);
        }
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    public PieChartViewData createLineViewData(ChartData.Line line) {
        return new PieChartViewData(line);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void selectXOnChart(int x, int y) {
        if (this.chartData == 0 || this.isEmpty) {
            return;
        }
        double theta = Math.atan2((this.chartArea.centerY() + AndroidUtilities.dp(16.0f)) - y, this.chartArea.centerX() - x);
        float a = (float) (Math.toDegrees(theta) - 90.0d);
        if (a < 0.0f) {
            double d = a;
            Double.isNaN(d);
            a = (float) (d + 360.0d);
        }
        float a2 = a / 360.0f;
        float p = 0.0f;
        int newSelection = -1;
        float selectionStartA = 0.0f;
        float selectionEndA = 0.0f;
        int i = 0;
        while (true) {
            if (i >= this.lines.size()) {
                break;
            }
            if (((PieChartViewData) this.lines.get(i)).enabled || ((PieChartViewData) this.lines.get(i)).alpha != 0.0f) {
                if (a2 > p) {
                    float[] fArr = this.darawingValuesPercentage;
                    if (a2 < fArr[i] + p) {
                        newSelection = i;
                        selectionStartA = p;
                        selectionEndA = p + fArr[i];
                        break;
                    }
                }
                p += this.darawingValuesPercentage[i];
            }
            i++;
        }
        if (this.currentSelection != newSelection && newSelection >= 0) {
            this.currentSelection = newSelection;
            invalidate();
            this.pieLegendView.setVisibility(0);
            LineViewData l = (LineViewData) this.lines.get(newSelection);
            this.pieLegendView.setData(l.line.name, (int) this.values[this.currentSelection], l.lineColor);
            this.pieLegendView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
            float r = this.rectF.width() / 2.0f;
            double centerX = this.rectF.centerX();
            double d2 = r;
            double cos = Math.cos(Math.toRadians((selectionEndA * 360.0f) - 90.0f));
            Double.isNaN(d2);
            Double.isNaN(centerX);
            double d3 = centerX + (d2 * cos);
            double centerX2 = this.rectF.centerX();
            double d4 = r;
            double cos2 = Math.cos(Math.toRadians((selectionStartA * 360.0f) - 90.0f));
            Double.isNaN(d4);
            Double.isNaN(centerX2);
            int xl = (int) Math.min(d3, centerX2 + (d4 * cos2));
            if (xl < 0) {
                xl = 0;
            }
            if (this.pieLegendView.getMeasuredWidth() + xl > getMeasuredWidth() - AndroidUtilities.dp(16.0f)) {
                xl -= (this.pieLegendView.getMeasuredWidth() + xl) - (getMeasuredWidth() - AndroidUtilities.dp(16.0f));
            }
            double centerY = this.rectF.centerY();
            double d5 = r;
            double sin = Math.sin(Math.toRadians((selectionStartA * 360.0f) - 90.0f));
            Double.isNaN(d5);
            Double.isNaN(centerY);
            double d6 = centerY + (d5 * sin);
            double centerY2 = this.rectF.centerY();
            double d7 = r;
            double sin2 = Math.sin(Math.toRadians((360.0f * selectionEndA) - 90.0f));
            Double.isNaN(d7);
            Double.isNaN(centerY2);
            int yl = (int) Math.min(d6, centerY2 + (d7 * sin2));
            int yl2 = ((int) Math.min(this.rectF.centerY(), yl)) - AndroidUtilities.dp(50.0f);
            this.pieLegendView.setTranslationX(xl);
            this.pieLegendView.setTranslationY(yl2);
            boolean v = false;
            if (Build.VERSION.SDK_INT >= 27) {
                v = performHapticFeedback(9, 2);
            }
            if (!v) {
                performHapticFeedback(3, 2);
            }
        }
        moveLegend();
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView, android.view.View
    public void onDraw(Canvas canvas) {
        if (this.chartData != 0) {
            for (int i = 0; i < this.lines.size(); i++) {
                if (i == this.currentSelection) {
                    if (((PieChartViewData) this.lines.get(i)).selectionA < 1.0f) {
                        ((PieChartViewData) this.lines.get(i)).selectionA += 0.1f;
                        if (((PieChartViewData) this.lines.get(i)).selectionA > 1.0f) {
                            ((PieChartViewData) this.lines.get(i)).selectionA = 1.0f;
                        }
                        invalidate();
                    }
                } else if (((PieChartViewData) this.lines.get(i)).selectionA > 0.0f) {
                    ((PieChartViewData) this.lines.get(i)).selectionA -= 0.1f;
                    if (((PieChartViewData) this.lines.get(i)).selectionA < 0.0f) {
                        ((PieChartViewData) this.lines.get(i)).selectionA = 0.0f;
                    }
                    invalidate();
                }
            }
        }
        super.onDraw(canvas);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void onActionUp() {
        this.currentSelection = -1;
        this.pieLegendView.setVisibility(8);
        invalidate();
    }

    @Override // org.telegram.ui.Charts.BaseChartView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != this.oldW) {
            this.oldW = getMeasuredWidth();
            int r = (int) ((this.chartArea.width() > this.chartArea.height() ? this.chartArea.height() : this.chartArea.width()) * 0.45f);
            this.MIN_TEXT_SIZE = r / 13;
            this.MAX_TEXT_SIZE = r / 7;
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void updatePicker(ChartData chartData, long d) {
        float p;
        int n = chartData.x.length;
        long startOfDay = d - (d % 86400000);
        int startIndex = 0;
        for (int i = 0; i < n; i++) {
            if (startOfDay >= chartData.x[i]) {
                startIndex = i;
            }
        }
        if (chartData.xPercentage.length < 2) {
            p = 0.5f;
        } else {
            p = 1.0f / chartData.x.length;
        }
        if (startIndex == 0) {
            this.pickerDelegate.pickerStart = 0.0f;
            this.pickerDelegate.pickerEnd = p;
        } else if (startIndex >= chartData.x.length - 1) {
            this.pickerDelegate.pickerStart = 1.0f - p;
            this.pickerDelegate.pickerEnd = 1.0f;
        } else {
            this.pickerDelegate.pickerStart = startIndex * p;
            this.pickerDelegate.pickerEnd = this.pickerDelegate.pickerStart + p;
            if (this.pickerDelegate.pickerEnd > 1.0f) {
                this.pickerDelegate.pickerEnd = 1.0f;
            }
            onPickerDataChanged(true, true, false);
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected LegendSignatureView createLegendView() {
        PieLegendView pieLegendView = new PieLegendView(getContext());
        this.pieLegendView = pieLegendView;
        return pieLegendView;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void onPickerDataChanged(boolean animated, boolean force, boolean useAnimator) {
        super.onPickerDataChanged(animated, force, useAnimator);
        if (this.chartData == 0 || ((StackLinearChartData) this.chartData).xPercentage == null) {
            return;
        }
        float startPercentage = this.pickerDelegate.pickerStart;
        float endPercentage = this.pickerDelegate.pickerEnd;
        updateCharValues(startPercentage, endPercentage, force);
    }

    private void updateCharValues(float startPercentage, float endPercentage, boolean force) {
        float animateTo;
        if (this.values == null) {
            return;
        }
        int n = ((StackLinearChartData) this.chartData).xPercentage.length;
        int nl = this.lines.size();
        int startIndex = -1;
        int endIndex = -1;
        for (int j = 0; j < n; j++) {
            if (((StackLinearChartData) this.chartData).xPercentage[j] >= startPercentage && startIndex == -1) {
                startIndex = j;
            }
            if (((StackLinearChartData) this.chartData).xPercentage[j] <= endPercentage) {
                endIndex = j;
            }
        }
        if (endIndex < startIndex) {
            startIndex = endIndex;
        }
        if (!force && this.lastEndIndex == endIndex && this.lastStartIndex == startIndex) {
            return;
        }
        this.lastEndIndex = endIndex;
        this.lastStartIndex = startIndex;
        this.isEmpty = true;
        this.sum = 0.0f;
        for (int i = 0; i < nl; i++) {
            this.values[i] = 0.0f;
        }
        for (int j2 = startIndex; j2 <= endIndex; j2++) {
            for (int i2 = 0; i2 < nl; i2++) {
                float[] fArr = this.values;
                fArr[i2] = fArr[i2] + ((StackLinearChartData) this.chartData).lines.get(i2).y[j2];
                this.sum += ((StackLinearChartData) this.chartData).lines.get(i2).y[j2];
                if (this.isEmpty && ((PieChartViewData) this.lines.get(i2)).enabled && ((StackLinearChartData) this.chartData).lines.get(i2).y[j2] > 0) {
                    this.isEmpty = false;
                }
            }
        }
        if (!force) {
            for (int i3 = 0; i3 < nl; i3++) {
                final PieChartViewData line = (PieChartViewData) this.lines.get(i3);
                if (line.animator != null) {
                    line.animator.cancel();
                }
                float f = this.sum;
                if (f == 0.0f) {
                    animateTo = 0.0f;
                } else {
                    animateTo = this.values[i3] / f;
                }
                ValueAnimator animator = createAnimator(line.drawingPart, animateTo, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.PieChartView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PieChartView.this.m1757lambda$updateCharValues$0$orgtelegramuiChartsPieChartView(line, valueAnimator);
                    }
                });
                line.animator = animator;
                animator.start();
            }
            return;
        }
        for (int i4 = 0; i4 < nl; i4++) {
            if (this.sum == 0.0f) {
                ((PieChartViewData) this.lines.get(i4)).drawingPart = 0.0f;
            } else {
                ((PieChartViewData) this.lines.get(i4)).drawingPart = this.values[i4] / this.sum;
            }
        }
    }

    /* renamed from: lambda$updateCharValues$0$org-telegram-ui-Charts-PieChartView */
    public /* synthetic */ void m1757lambda$updateCharValues$0$orgtelegramuiChartsPieChartView(PieChartViewData line, ValueAnimator animation) {
        line.drawingPart = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // org.telegram.ui.Charts.BaseChartView, org.telegram.ui.Charts.ChartPickerDelegate.Listener
    public void onPickerJumpTo(float start, float end, boolean force) {
        if (this.chartData == 0) {
            return;
        }
        if (force) {
            updateCharValues(start, end, false);
            return;
        }
        updateIndexes();
        invalidate();
    }

    @Override // org.telegram.ui.Charts.StackLinearChartView, org.telegram.ui.Charts.BaseChartView
    public void fillTransitionParams(TransitionParams params) {
        drawChart(null);
        float p = 0.0f;
        int i = 0;
        while (true) {
            float[] fArr = this.darawingValuesPercentage;
            if (i < fArr.length) {
                p += fArr[i];
                params.angle[i] = (360.0f * p) - 180.0f;
                i++;
            } else {
                return;
            }
        }
    }
}
