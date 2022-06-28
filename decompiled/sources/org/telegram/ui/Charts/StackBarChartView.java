package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.SegmentTree;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackBarChartData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.StackBarViewData;
/* loaded from: classes4.dex */
public class StackBarChartView extends BaseChartView<StackBarChartData, StackBarViewData> {
    private int[] yMaxPoints;

    public StackBarChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public StackBarViewData createLineViewData(ChartData.Line line) {
        return new StackBarViewData(line);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawChart(Canvas canvas) {
        float lineWidth;
        float p;
        float transitionAlpha;
        int localEnd;
        int localStart;
        int additionalPoints;
        if (this.chartData == 0) {
            return;
        }
        float fullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
        float offset = (this.pickerDelegate.pickerStart * fullWidth) - HORIZONTAL_PADDING;
        boolean z = true;
        if (((StackBarChartData) this.chartData).xPercentage.length < 2) {
            p = 1.0f;
            lineWidth = 1.0f;
        } else {
            float p2 = ((StackBarChartData) this.chartData).xPercentage[1] * fullWidth;
            p = p2;
            lineWidth = ((StackBarChartData) this.chartData).xPercentage[1] * (fullWidth - p2);
        }
        int additionalPoints2 = ((int) (HORIZONTAL_PADDING / p)) + 1;
        int localStart2 = Math.max(0, (this.startXIndex - additionalPoints2) - 2);
        int localEnd2 = Math.min(((StackBarChartData) this.chartData).xPercentage.length - 1, this.endXIndex + additionalPoints2 + 2);
        for (int k = 0; k < this.lines.size(); k++) {
            ((LineViewData) this.lines.get(k)).linesPathBottomSize = 0;
        }
        canvas.save();
        float f = 0.0f;
        if (this.transitionMode == 2) {
            this.postTransition = true;
            this.selectionA = 0.0f;
            float transitionAlpha2 = 1.0f - this.transitionParams.progress;
            canvas.scale((this.transitionParams.progress * 2.0f) + 1.0f, 1.0f, this.transitionParams.pX, this.transitionParams.pY);
            transitionAlpha = transitionAlpha2;
        } else if (this.transitionMode == 1) {
            float transitionAlpha3 = this.transitionParams.progress;
            canvas.scale(this.transitionParams.progress, 1.0f, this.transitionParams.pX, this.transitionParams.pY);
            transitionAlpha = transitionAlpha3;
        } else if (this.transitionMode != 3) {
            transitionAlpha = 1.0f;
        } else {
            transitionAlpha = this.transitionParams.progress;
        }
        if (this.selectedIndex < 0 || !this.legendShowing) {
            z = false;
        }
        boolean selected = z;
        int i = localStart2;
        while (i <= localEnd2) {
            float stackOffset = 0.0f;
            if (this.selectedIndex != i || !selected) {
                int k2 = 0;
                while (k2 < this.lines.size()) {
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != f) {
                        int[] y = line.line.y;
                        float xPoint = ((p / 2.0f) + (((StackBarChartData) this.chartData).xPercentage[i] * (fullWidth - p))) - offset;
                        float yPercentage = y[i] / this.currentMaxHeight;
                        additionalPoints = additionalPoints2;
                        float height = ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT) * yPercentage * line.alpha;
                        float yPoint = (getMeasuredHeight() - this.chartBottom) - height;
                        float[] fArr = line.linesPath;
                        localStart = localStart2;
                        int localStart3 = line.linesPathBottomSize;
                        localEnd = localEnd2;
                        int localEnd3 = localStart3 + 1;
                        line.linesPathBottomSize = localEnd3;
                        fArr[localStart3] = xPoint;
                        float[] fArr2 = line.linesPath;
                        int i2 = line.linesPathBottomSize;
                        line.linesPathBottomSize = i2 + 1;
                        fArr2[i2] = yPoint - stackOffset;
                        float[] fArr3 = line.linesPath;
                        int i3 = line.linesPathBottomSize;
                        line.linesPathBottomSize = i3 + 1;
                        fArr3[i3] = xPoint;
                        float[] fArr4 = line.linesPath;
                        int i4 = line.linesPathBottomSize;
                        line.linesPathBottomSize = i4 + 1;
                        fArr4[i4] = (getMeasuredHeight() - this.chartBottom) - stackOffset;
                        stackOffset += height;
                    } else {
                        additionalPoints = additionalPoints2;
                        localStart = localStart2;
                        localEnd = localEnd2;
                    }
                    k2++;
                    additionalPoints2 = additionalPoints;
                    localStart2 = localStart;
                    localEnd2 = localEnd;
                    f = 0.0f;
                }
            }
            i++;
            additionalPoints2 = additionalPoints2;
            localStart2 = localStart2;
            localEnd2 = localEnd2;
            f = 0.0f;
        }
        for (int k3 = 0; k3 < this.lines.size(); k3++) {
            StackBarViewData line2 = (StackBarViewData) this.lines.get(k3);
            Paint paint = (selected || this.postTransition) ? line2.unselectedPaint : line2.paint;
            if (selected) {
                line2.unselectedPaint.setColor(ColorUtils.blendARGB(line2.lineColor, line2.blendColor, this.selectionA));
            }
            if (this.postTransition) {
                line2.unselectedPaint.setColor(ColorUtils.blendARGB(line2.lineColor, line2.blendColor, 1.0f));
            }
            paint.setAlpha((int) (255.0f * transitionAlpha));
            paint.setStrokeWidth(lineWidth);
            canvas.drawLines(line2.linesPath, 0, line2.linesPathBottomSize, paint);
        }
        if (selected) {
            float stackOffset2 = 0.0f;
            for (int k4 = 0; k4 < this.lines.size(); k4++) {
                LineViewData line3 = (LineViewData) this.lines.get(k4);
                if (!line3.enabled && line3.alpha == 0.0f) {
                }
                int[] y2 = line3.line.y;
                float xPoint2 = ((p / 2.0f) + (((StackBarChartData) this.chartData).xPercentage[this.selectedIndex] * (fullWidth - p))) - offset;
                float yPercentage2 = y2[this.selectedIndex] / this.currentMaxHeight;
                float height2 = ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT) * yPercentage2 * line3.alpha;
                float yPoint2 = (getMeasuredHeight() - this.chartBottom) - height2;
                line3.paint.setStrokeWidth(lineWidth);
                line3.paint.setAlpha((int) (transitionAlpha * 255.0f));
                canvas.drawLine(xPoint2, yPoint2 - stackOffset2, xPoint2, (getMeasuredHeight() - this.chartBottom) - stackOffset2, line3.paint);
                stackOffset2 += height2;
            }
        }
        canvas.restore();
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void selectXOnChart(int x, int y) {
        float p;
        if (this.chartData == 0) {
            return;
        }
        int oldSelectedIndex = this.selectedIndex;
        float offset = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
        if (((StackBarChartData) this.chartData).xPercentage.length < 2) {
            p = 1.0f;
        } else {
            p = ((StackBarChartData) this.chartData).xPercentage[1] * this.chartFullWidth;
        }
        float xP = (x + offset) / (this.chartFullWidth - p);
        this.selectedCoordinate = xP;
        if (xP < 0.0f) {
            this.selectedIndex = 0;
            this.selectedCoordinate = 0.0f;
        } else if (xP > 1.0f) {
            this.selectedIndex = ((StackBarChartData) this.chartData).x.length - 1;
            this.selectedCoordinate = 1.0f;
        } else {
            this.selectedIndex = ((StackBarChartData) this.chartData).findIndex(this.startXIndex, this.endXIndex, xP);
            if (this.selectedIndex > this.endXIndex) {
                this.selectedIndex = this.endXIndex;
            }
            if (this.selectedIndex < this.startXIndex) {
                this.selectedIndex = this.startXIndex;
            }
        }
        if (oldSelectedIndex != this.selectedIndex) {
            this.legendShowing = true;
            animateLegend(true);
            moveLegend(offset);
            if (this.dateSelectionListener != null) {
                this.dateSelectionListener.onDateSelected(getSelectedDate());
            }
            invalidate();
            runSmoothHaptic();
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawPickerChart(Canvas canvas) {
        float p;
        float f;
        if (this.chartData != 0) {
            int n = ((StackBarChartData) this.chartData).xPercentage.length;
            int nl = this.lines.size();
            for (int k = 0; k < this.lines.size(); k++) {
                ((LineViewData) this.lines.get(k)).linesPathBottomSize = 0;
            }
            int step = Math.max(1, Math.round(n / 200.0f));
            int[] iArr = this.yMaxPoints;
            if (iArr == null || iArr.length < nl) {
                this.yMaxPoints = new int[nl];
            }
            for (int i = 0; i < n; i++) {
                float stackOffset = 0.0f;
                float xPoint = ((StackBarChartData) this.chartData).xPercentage[i] * this.pickerWidth;
                int k2 = 0;
                while (true) {
                    f = 0.0f;
                    if (k2 >= nl) {
                        break;
                    }
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != 0.0f) {
                        int y = line.line.y[i];
                        int[] iArr2 = this.yMaxPoints;
                        if (y > iArr2[k2]) {
                            iArr2[k2] = y;
                        }
                    }
                    k2++;
                }
                int k3 = i % step;
                if (k3 == 0) {
                    int k4 = 0;
                    while (k4 < nl) {
                        LineViewData line2 = (LineViewData) this.lines.get(k4);
                        if (line2.enabled || line2.alpha != f) {
                            float h = ANIMATE_PICKER_SIZES ? this.pickerMaxHeight : ((StackBarChartData) this.chartData).maxValue;
                            float yPercentage = (this.yMaxPoints[k4] / h) * line2.alpha;
                            float yPoint = this.pikerHeight * yPercentage;
                            float[] fArr = line2.linesPath;
                            int i2 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i2 + 1;
                            fArr[i2] = xPoint;
                            float[] fArr2 = line2.linesPath;
                            int i3 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i3 + 1;
                            fArr2[i3] = (this.pikerHeight - yPoint) - stackOffset;
                            float[] fArr3 = line2.linesPath;
                            int i4 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i4 + 1;
                            fArr3[i4] = xPoint;
                            float[] fArr4 = line2.linesPath;
                            int i5 = line2.linesPathBottomSize;
                            line2.linesPathBottomSize = i5 + 1;
                            fArr4[i5] = this.pikerHeight - stackOffset;
                            stackOffset += yPoint;
                            this.yMaxPoints[k4] = 0;
                        }
                        k4++;
                        f = 0.0f;
                    }
                }
            }
            if (((StackBarChartData) this.chartData).xPercentage.length >= 2) {
                p = ((StackBarChartData) this.chartData).xPercentage[1] * this.pickerWidth;
            } else {
                p = 1.0f;
            }
            for (int k5 = 0; k5 < nl; k5++) {
                LineViewData line3 = (LineViewData) this.lines.get(k5);
                line3.paint.setStrokeWidth(step * p);
                line3.paint.setAlpha(255);
                canvas.drawLines(line3.linesPath, 0, line3.linesPathBottomSize, line3.paint);
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void onCheckChanged() {
        int n = ((StackBarChartData) this.chartData).lines.get(0).y.length;
        int k = ((StackBarChartData) this.chartData).lines.size();
        ((StackBarChartData) this.chartData).ySum = new int[n];
        for (int i = 0; i < n; i++) {
            ((StackBarChartData) this.chartData).ySum[i] = 0;
            for (int j = 0; j < k; j++) {
                if (((StackBarViewData) this.lines.get(j)).enabled) {
                    int[] iArr = ((StackBarChartData) this.chartData).ySum;
                    iArr[i] = iArr[i] + ((StackBarChartData) this.chartData).lines.get(j).y[i];
                }
            }
        }
        ((StackBarChartData) this.chartData).ySumSegmentTree = new SegmentTree(((StackBarChartData) this.chartData).ySum);
        super.onCheckChanged();
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void drawSelection(Canvas canvas) {
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public int findMaxValue(int startXIndex, int endXIndex) {
        return ((StackBarChartData) this.chartData).findMax(startXIndex, endXIndex);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void updatePickerMinMaxHeight() {
        if (!ANIMATE_PICKER_SIZES) {
            return;
        }
        int max = 0;
        int n = ((StackBarChartData) this.chartData).x.length;
        int nl = this.lines.size();
        for (int i = 0; i < n; i++) {
            int h = 0;
            for (int k = 0; k < nl; k++) {
                StackBarViewData l = (StackBarViewData) this.lines.get(k);
                if (l.enabled) {
                    h += l.line.y[i];
                }
            }
            if (h > max) {
                max = h;
            }
        }
        if (max > 0 && max != this.animatedToPickerMaxHeight) {
            this.animatedToPickerMaxHeight = max;
            if (this.pickerAnimator != null) {
                this.pickerAnimator.cancel();
            }
            this.pickerAnimator = createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.StackBarChartView.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    StackBarChartView.this.pickerMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
                    StackBarChartView.this.invalidatePickerChart = true;
                    StackBarChartView.this.invalidate();
                }
            });
            this.pickerAnimator.start();
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void initPickerMaxHeight() {
        super.initPickerMaxHeight();
        this.pickerMaxHeight = 0.0f;
        int n = ((StackBarChartData) this.chartData).x.length;
        int nl = this.lines.size();
        for (int i = 0; i < n; i++) {
            int h = 0;
            for (int k = 0; k < nl; k++) {
                StackBarViewData l = (StackBarViewData) this.lines.get(k);
                if (l.enabled) {
                    h += l.line.y[i];
                }
            }
            if (h > this.pickerMaxHeight) {
                this.pickerMaxHeight = h;
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView, android.view.View
    public void onDraw(Canvas canvas) {
        tick();
        drawChart(canvas);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            if (this.tmpI < this.tmpN) {
                drawHorizontalLines(canvas, this.horizontalLines.get(this.tmpI));
                drawSignaturesToHorizontalLines(canvas, this.horizontalLines.get(this.tmpI));
                i = this.tmpI + 1;
            } else {
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected float getMinDistance() {
        return 0.1f;
    }
}
