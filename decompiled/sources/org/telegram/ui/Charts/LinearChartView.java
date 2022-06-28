package org.telegram.ui.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.LineViewData;
/* loaded from: classes4.dex */
public class LinearChartView extends BaseChartView<ChartData, LineViewData> {
    public LinearChartView(Context context) {
        super(context);
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public void init() {
        this.useMinHeight = true;
        super.init();
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawChart(Canvas canvas) {
        float offset;
        float fullWidth;
        float p;
        float p2;
        int localEnd;
        float offset2;
        float fullWidth2;
        if (this.chartData != 0) {
            float fullWidth3 = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
            float offset3 = (this.pickerDelegate.pickerStart * fullWidth3) - HORIZONTAL_PADDING;
            int k = 0;
            while (k < this.lines.size()) {
                LineViewData line = (LineViewData) this.lines.get(k);
                if (line.enabled || line.alpha != 0.0f) {
                    int j = 0;
                    if (this.chartData.xPercentage.length < 2) {
                        p = 0.0f;
                    } else {
                        p = this.chartData.xPercentage[1] * fullWidth3;
                    }
                    int[] y = line.line.y;
                    int additionalPoints = ((int) (HORIZONTAL_PADDING / p)) + 1;
                    line.chartPath.reset();
                    boolean first = true;
                    int localStart = Math.max(0, this.startXIndex - additionalPoints);
                    int localEnd2 = Math.min(this.chartData.xPercentage.length - 1, this.endXIndex + additionalPoints);
                    int i = localStart;
                    while (i <= localEnd2) {
                        if (y[i] < 0) {
                            fullWidth2 = fullWidth3;
                            offset2 = offset3;
                            localEnd = localEnd2;
                            p2 = p;
                        } else {
                            float xPoint = (this.chartData.xPercentage[i] * fullWidth3) - offset3;
                            fullWidth2 = fullWidth3;
                            float f = y[i] - this.currentMinHeight;
                            float f2 = this.currentMaxHeight;
                            offset2 = offset3;
                            float offset4 = this.currentMinHeight;
                            float yPercentage = f / (f2 - offset4);
                            float padding = line.paint.getStrokeWidth() / 2.0f;
                            int measuredHeight = getMeasuredHeight();
                            localEnd = localEnd2;
                            int localEnd3 = this.chartBottom;
                            p2 = p;
                            float yPoint = ((measuredHeight - localEnd3) - padding) - ((((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT) - padding) * yPercentage);
                            if (USE_LINES) {
                                if (j == 0) {
                                    int j2 = j + 1;
                                    line.linesPath[j] = xPoint;
                                    line.linesPath[j2] = yPoint;
                                    j = j2 + 1;
                                } else {
                                    int j3 = j + 1;
                                    line.linesPath[j] = xPoint;
                                    int j4 = j3 + 1;
                                    line.linesPath[j3] = yPoint;
                                    int j5 = j4 + 1;
                                    line.linesPath[j4] = xPoint;
                                    line.linesPath[j5] = yPoint;
                                    j = j5 + 1;
                                }
                            } else if (first) {
                                line.chartPath.moveTo(xPoint, yPoint);
                                first = false;
                            } else {
                                line.chartPath.lineTo(xPoint, yPoint);
                            }
                        }
                        i++;
                        fullWidth3 = fullWidth2;
                        offset3 = offset2;
                        localEnd2 = localEnd;
                        p = p2;
                    }
                    fullWidth = fullWidth3;
                    offset = offset3;
                    canvas.save();
                    float transitionAlpha = 1.0f;
                    float f3 = 1.0f;
                    if (this.transitionMode == 2) {
                        transitionAlpha = this.transitionParams.progress > 0.5f ? 0.0f : 1.0f - (this.transitionParams.progress * 2.0f);
                        canvas.scale((this.transitionParams.progress * 2.0f) + 1.0f, 1.0f, this.transitionParams.pX, this.transitionParams.pY);
                    } else if (this.transitionMode == 1) {
                        transitionAlpha = this.transitionParams.progress < 0.3f ? 0.0f : this.transitionParams.progress;
                        canvas.save();
                        float f4 = this.transitionParams.progress;
                        if (this.transitionParams.needScaleY) {
                            f3 = this.transitionParams.progress;
                        }
                        canvas.scale(f4, f3, this.transitionParams.pX, this.transitionParams.pY);
                    } else if (this.transitionMode == 3) {
                        transitionAlpha = this.transitionParams.progress;
                    }
                    line.paint.setAlpha((int) (line.alpha * 255.0f * transitionAlpha));
                    if (this.endXIndex - this.startXIndex > 100) {
                        line.paint.setStrokeCap(Paint.Cap.SQUARE);
                    } else {
                        line.paint.setStrokeCap(Paint.Cap.ROUND);
                    }
                    if (!USE_LINES) {
                        canvas.drawPath(line.chartPath, line.paint);
                    } else {
                        canvas.drawLines(line.linesPath, 0, j, line.paint);
                    }
                    canvas.restore();
                } else {
                    fullWidth = fullWidth3;
                    offset = offset3;
                }
                k++;
                fullWidth3 = fullWidth;
                offset3 = offset;
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    protected void drawPickerChart(Canvas canvas) {
        LinearChartView linearChartView = this;
        int measuredHeight = getMeasuredHeight() - PICKER_PADDING;
        int measuredHeight2 = (getMeasuredHeight() - linearChartView.pikerHeight) - PICKER_PADDING;
        int nl = linearChartView.lines.size();
        if (linearChartView.chartData != 0) {
            int k = 0;
            while (k < nl) {
                LineViewData line = (LineViewData) linearChartView.lines.get(k);
                if (line.enabled || line.alpha != 0.0f) {
                    line.bottomLinePath.reset();
                    int n = linearChartView.chartData.xPercentage.length;
                    int j = 0;
                    int[] y = line.line.y;
                    line.chartPath.reset();
                    int i = 0;
                    while (i < n) {
                        if (y[i] >= 0) {
                            float xPoint = linearChartView.chartData.xPercentage[i] * linearChartView.pickerWidth;
                            float h = ANIMATE_PICKER_SIZES ? linearChartView.pickerMaxHeight : linearChartView.chartData.maxValue;
                            float hMin = ANIMATE_PICKER_SIZES ? linearChartView.pickerMinHeight : linearChartView.chartData.minValue;
                            float yPercentage = (y[i] - hMin) / (h - hMin);
                            float yPoint = linearChartView.pikerHeight * (1.0f - yPercentage);
                            if (USE_LINES) {
                                if (j == 0) {
                                    int j2 = j + 1;
                                    line.linesPathBottom[j] = xPoint;
                                    j = j2 + 1;
                                    line.linesPathBottom[j2] = yPoint;
                                } else {
                                    int j3 = j + 1;
                                    line.linesPathBottom[j] = xPoint;
                                    int j4 = j3 + 1;
                                    line.linesPathBottom[j3] = yPoint;
                                    int j5 = j4 + 1;
                                    line.linesPathBottom[j4] = xPoint;
                                    j = j5 + 1;
                                    line.linesPathBottom[j5] = yPoint;
                                }
                            } else if (i == 0) {
                                line.bottomLinePath.moveTo(xPoint, yPoint);
                            } else {
                                line.bottomLinePath.lineTo(xPoint, yPoint);
                            }
                        }
                        i++;
                        linearChartView = this;
                    }
                    line.linesPathBottomSize = j;
                    if (line.enabled || line.alpha != 0.0f) {
                        line.bottomLinePaint.setAlpha((int) (line.alpha * 255.0f));
                        if (USE_LINES) {
                            canvas.drawLines(line.linesPathBottom, 0, line.linesPathBottomSize, line.bottomLinePaint);
                        } else {
                            canvas.drawPath(line.bottomLinePath, line.bottomLinePaint);
                        }
                    }
                }
                k++;
                linearChartView = this;
            }
        }
    }

    @Override // org.telegram.ui.Charts.BaseChartView
    public LineViewData createLineViewData(ChartData.Line line) {
        return new LineViewData(line);
    }
}
