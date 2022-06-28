package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.StackLinearViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;
/* loaded from: classes4.dex */
public class StackLinearChartView<T extends StackLinearViewData> extends BaseChartView<StackLinearChartData, T> {
    boolean[] skipPoints;
    float[] startFromY;
    private Matrix matrix = new Matrix();
    private float[] mapPoints = new float[2];
    Path ovalPath = new Path();

    public StackLinearChartView(Context context) {
        super(context);
        this.superDraw = true;
        this.useAlphaSignature = true;
        this.drawPointOnSelection = false;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public T createLineViewData(ChartData.Line line) {
        return (T) new StackLinearViewData(line);
    }

    /* JADX WARN: Removed duplicated region for block: B:151:0x051e  */
    /* JADX WARN: Removed duplicated region for block: B:192:0x069a  */
    @Override // org.telegram.ui.Charts.BaseChartView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void drawChart(android.graphics.Canvas r52) {
        /*
            Method dump skipped, instructions count: 1861
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.StackLinearChartView.drawChart(android.graphics.Canvas):void");
    }

    private int quarterForPoint(float x, float y) {
        float cX = this.chartArea.centerX();
        float cY = this.chartArea.centerY() + AndroidUtilities.dp(16.0f);
        if (x >= cX && y <= cY) {
            return 0;
        }
        if (x >= cX && y >= cY) {
            return 1;
        }
        if (x < cX && y >= cY) {
            return 2;
        }
        return 3;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawPickerChart(Canvas canvas) {
        float f;
        float sum;
        int nl;
        float yPercentage;
        boolean hasEmptyPoint;
        if (this.chartData != 0) {
            int nl2 = this.lines.size();
            for (int k = 0; k < nl2; k++) {
                ((StackLinearViewData) this.lines.get(k)).chartPathPicker.reset();
            }
            int n = ((StackLinearChartData) this.chartData).simplifiedSize;
            boolean[] zArr = this.skipPoints;
            if (zArr == null || zArr.length < ((StackLinearChartData) this.chartData).lines.size()) {
                this.skipPoints = new boolean[((StackLinearChartData) this.chartData).lines.size()];
            }
            boolean hasEmptyPoint2 = false;
            int i = 0;
            while (true) {
                int i2 = 1;
                if (i >= n) {
                    break;
                }
                float stackOffset = 0.0f;
                float sum2 = 0.0f;
                int lastEnabled = 0;
                int drawingLinesCount = 0;
                int k2 = 0;
                while (true) {
                    f = 0.0f;
                    if (k2 >= this.lines.size()) {
                        break;
                    }
                    LineViewData line = (LineViewData) this.lines.get(k2);
                    if (line.enabled || line.alpha != 0.0f) {
                        if (((StackLinearChartData) this.chartData).simplifiedY[k2][i] > 0) {
                            sum2 += ((StackLinearChartData) this.chartData).simplifiedY[k2][i] * line.alpha;
                            drawingLinesCount++;
                        }
                        lastEnabled = k2;
                    }
                    k2++;
                }
                float xPoint = (i / (n - 1)) * this.pickerWidth;
                int k3 = 0;
                while (k3 < this.lines.size()) {
                    LineViewData line2 = (LineViewData) this.lines.get(k3);
                    if (line2.enabled || line2.alpha != f) {
                        if (drawingLinesCount == i2) {
                            if (((StackLinearChartData) this.chartData).simplifiedY[k3][i] == 0) {
                                yPercentage = 0.0f;
                            } else {
                                yPercentage = line2.alpha;
                            }
                        } else if (sum2 == f) {
                            yPercentage = 0.0f;
                        } else {
                            yPercentage = (((StackLinearChartData) this.chartData).simplifiedY[k3][i] * line2.alpha) / sum2;
                        }
                        if (yPercentage == f && k3 == lastEnabled) {
                            hasEmptyPoint2 = true;
                        }
                        float height = this.pikerHeight * yPercentage;
                        float yPoint = (this.pikerHeight - height) - stackOffset;
                        if (i != 0) {
                            nl = nl2;
                            hasEmptyPoint = hasEmptyPoint2;
                            sum = sum2;
                        } else {
                            nl = nl2;
                            hasEmptyPoint = hasEmptyPoint2;
                            sum = sum2;
                            line2.chartPathPicker.moveTo(0.0f, this.pikerHeight);
                            this.skipPoints[k3] = false;
                        }
                        if (((StackLinearChartData) this.chartData).simplifiedY[k3][i] == 0 && i > 0 && ((StackLinearChartData) this.chartData).simplifiedY[k3][i - 1] == 0 && i < n - 1 && ((StackLinearChartData) this.chartData).simplifiedY[k3][i + 1] == 0) {
                            if (!this.skipPoints[k3]) {
                                line2.chartPathPicker.lineTo(xPoint, this.pikerHeight);
                            }
                            this.skipPoints[k3] = true;
                        } else {
                            if (this.skipPoints[k3]) {
                                line2.chartPathPicker.lineTo(xPoint, this.pikerHeight);
                            }
                            line2.chartPathPicker.lineTo(xPoint, yPoint);
                            this.skipPoints[k3] = false;
                        }
                        if (i == n - 1) {
                            line2.chartPathPicker.lineTo(this.pickerWidth, this.pikerHeight);
                        }
                        stackOffset += height;
                        hasEmptyPoint2 = hasEmptyPoint;
                    } else {
                        nl = nl2;
                        sum = sum2;
                    }
                    k3++;
                    nl2 = nl;
                    sum2 = sum;
                    i2 = 1;
                    f = 0.0f;
                }
                i++;
            }
            if (hasEmptyPoint2) {
                canvas.drawColor(Theme.getColor(Theme.key_statisticChartLineEmpty));
            }
            for (int k4 = this.lines.size() - 1; k4 >= 0; k4--) {
                LineViewData line3 = (LineViewData) this.lines.get(k4);
                canvas.drawPath(line3.chartPathPicker, line3.paint);
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
    public int findMaxValue(int startXIndex, int endXIndex) {
        return 100;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected float getMinDistance() {
        return 0.1f;
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void fillTransitionParams(TransitionParams params) {
        float p;
        float offset;
        float fullWidth;
        float p2;
        float yPercentage;
        if (this.chartData != 0) {
            float fullWidth2 = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset2 = (this.pickerDelegate.pickerStart * fullWidth2) - HORIZONTAL_PADDING;
            int i = 2;
            int i2 = 1;
            if (((StackLinearChartData) this.chartData).xPercentage.length < 2) {
                p = 1.0f;
            } else {
                p = ((StackLinearChartData) this.chartData).xPercentage[1] * fullWidth2;
            }
            int additionalPoints = ((int) (HORIZONTAL_PADDING / p)) + 1;
            int localStart = Math.max(0, (this.startXIndex - additionalPoints) - 1);
            int localEnd = Math.min(((StackLinearChartData) this.chartData).xPercentage.length - 1, this.endXIndex + additionalPoints + 1);
            this.transitionParams.startX = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.startY = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.endX = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.endY = new float[((StackLinearChartData) this.chartData).lines.size()];
            this.transitionParams.angle = new float[((StackLinearChartData) this.chartData).lines.size()];
            int j = 0;
            while (j < i) {
                int i3 = localStart;
                if (j == i2) {
                    i3 = localEnd;
                }
                int stackOffset = 0;
                float sum = 0.0f;
                int drawingLinesCount = 0;
                for (int k = 0; k < this.lines.size(); k++) {
                    LineViewData line = (LineViewData) this.lines.get(k);
                    if ((line.enabled || line.alpha != 0.0f) && line.line.y[i3] > 0) {
                        sum += line.line.y[i3] * line.alpha;
                        drawingLinesCount++;
                    }
                }
                int k2 = 0;
                while (k2 < this.lines.size()) {
                    LineViewData line2 = (LineViewData) this.lines.get(k2);
                    if (line2.enabled || line2.alpha != 0.0f) {
                        int[] y = line2.line.y;
                        if (drawingLinesCount == 1) {
                            if (y[i3] == 0) {
                                p2 = p;
                                yPercentage = 0.0f;
                            } else {
                                yPercentage = line2.alpha;
                                p2 = p;
                            }
                        } else if (sum == 0.0f) {
                            yPercentage = 0.0f;
                            p2 = p;
                        } else {
                            p2 = p;
                            yPercentage = (y[i3] * line2.alpha) / sum;
                        }
                        float xPoint = (((StackLinearChartData) this.chartData).xPercentage[i3] * fullWidth2) - offset2;
                        fullWidth = fullWidth2;
                        float height = ((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT) * yPercentage;
                        offset = offset2;
                        float yPoint = ((getMeasuredHeight() - this.chartBottom) - height) - stackOffset;
                        int stackOffset2 = (int) (stackOffset + height);
                        if (j == 0) {
                            this.transitionParams.startX[k2] = xPoint;
                            this.transitionParams.startY[k2] = yPoint;
                        } else {
                            this.transitionParams.endX[k2] = xPoint;
                            this.transitionParams.endY[k2] = yPoint;
                        }
                        stackOffset = stackOffset2;
                    } else {
                        fullWidth = fullWidth2;
                        offset = offset2;
                        p2 = p;
                    }
                    k2++;
                    p = p2;
                    fullWidth2 = fullWidth;
                    offset2 = offset;
                }
                j++;
                i = 2;
                i2 = 1;
            }
        }
    }
}
