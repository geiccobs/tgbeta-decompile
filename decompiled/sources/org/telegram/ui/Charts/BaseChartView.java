package org.telegram.ui.Charts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.ChartPickerDelegate;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.view_data.ChartBottomSignatureData;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;
import org.telegram.ui.Components.CubicBezierInterpolator;
/* loaded from: classes4.dex */
public abstract class BaseChartView<T extends ChartData, L extends LineViewData> extends View implements ChartPickerDelegate.Listener {
    protected static final boolean ANIMATE_PICKER_SIZES;
    private static final int BOTTOM_SIGNATURE_COUNT = 6;
    public static FastOutSlowInInterpolator INTERPOLATOR = null;
    private static final float LINE_WIDTH = 1.0f;
    public static final int TRANSITION_MODE_ALPHA_ENTER = 3;
    public static final int TRANSITION_MODE_CHILD = 1;
    public static final int TRANSITION_MODE_NONE = 0;
    public static final int TRANSITION_MODE_PARENT = 2;
    public static final boolean USE_LINES;
    ValueAnimator alphaAnimator;
    ValueAnimator alphaBottomAnimator;
    protected float animatedToPickerMaxHeight;
    protected float animatedToPickerMinHeight;
    private Bitmap bottomChartBitmap;
    private Canvas bottomChartCanvas;
    protected int bottomSignatureOffset;
    float bottomSignaturePaintAlpha;
    protected boolean canCaptureChartSelection;
    long capturedTime;
    int capturedX;
    int capturedY;
    int chartActiveLineAlpha;
    int chartBottom;
    T chartData;
    public float chartEnd;
    public float chartFullWidth;
    ChartHeaderView chartHeaderView;
    public float chartStart;
    public float chartWidth;
    ChartBottomSignatureData currentBottomSignatures;
    protected DateSelectionListener dateSelectionListener;
    int endXIndex;
    int hintLinePaintAlpha;
    int lastX;
    int lastY;
    public LegendSignatureView legendSignatureView;
    Animator maxValueAnimator;
    private float minMaxUpdateStep;
    Animator pickerAnimator;
    protected float pickerMaxHeight;
    protected float pickerMinHeight;
    public float pickerWidth;
    ValueAnimator selectionAnimator;
    public SharedUiComponents sharedUiComponents;
    float signaturePaintAlpha;
    private float startFromMax;
    private float startFromMaxH;
    private float startFromMin;
    private float startFromMinH;
    int startXIndex;
    protected int tmpI;
    protected int tmpN;
    private final int touchSlop;
    public TransitionParams transitionParams;
    VibrationEffect vibrationEffect;
    public static final float HORIZONTAL_PADDING = AndroidUtilities.dpf2(16.0f);
    private static final float SELECTED_LINE_WIDTH = AndroidUtilities.dpf2(1.5f);
    private static final float SIGNATURE_TEXT_SIZE = AndroidUtilities.dpf2(12.0f);
    public static final int SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(18.0f);
    private static final int BOTTOM_SIGNATURE_TEXT_HEIGHT = AndroidUtilities.dp(14.0f);
    public static final int BOTTOM_SIGNATURE_START_ALPHA = AndroidUtilities.dp(10.0f);
    protected static final int PICKER_PADDING = AndroidUtilities.dp(16.0f);
    private static final int PICKER_CAPTURE_WIDTH = AndroidUtilities.dp(24.0f);
    private static final int LANDSCAPE_END_PADDING = AndroidUtilities.dp(16.0f);
    private static final int BOTTOM_SIGNATURE_OFFSET = AndroidUtilities.dp(10.0f);
    private static final int DP_12 = AndroidUtilities.dp(12.0f);
    private static final int DP_6 = AndroidUtilities.dp(6.0f);
    private static final int DP_5 = AndroidUtilities.dp(5.0f);
    private static final int DP_2 = AndroidUtilities.dp(2.0f);
    private static final int DP_1 = AndroidUtilities.dp(1.0f);
    ArrayList<ChartHorizontalLinesData> horizontalLines = new ArrayList<>(10);
    ArrayList<ChartBottomSignatureData> bottomSignatureDate = new ArrayList<>(25);
    public ArrayList<L> lines = new ArrayList<>();
    private final int ANIM_DURATION = 400;
    protected boolean drawPointOnSelection = true;
    public float currentMaxHeight = 250.0f;
    public float currentMinHeight = 0.0f;
    float animateToMaxHeight = 0.0f;
    float animateToMinHeight = 0.0f;
    float thresholdMaxHeight = 0.0f;
    boolean invalidatePickerChart = true;
    boolean landscape = false;
    public boolean enabled = true;
    Paint emptyPaint = new Paint();
    Paint linePaint = new Paint();
    Paint selectedLinePaint = new Paint();
    Paint signaturePaint = new TextPaint(1);
    Paint signaturePaint2 = new TextPaint(1);
    Paint bottomSignaturePaint = new TextPaint(1);
    Paint pickerSelectorPaint = new Paint(1);
    Paint unactiveBottomChartPaint = new Paint();
    Paint selectionBackgroundPaint = new Paint(1);
    Paint ripplePaint = new Paint(1);
    Paint whiteLinePaint = new Paint(1);
    Rect pickerRect = new Rect();
    Path pathTmp = new Path();
    boolean postTransition = false;
    public ChartPickerDelegate pickerDelegate = new ChartPickerDelegate(this);
    protected boolean chartCaptured = false;
    protected int selectedIndex = -1;
    protected float selectedCoordinate = -1.0f;
    public boolean legendShowing = false;
    public float selectionA = 0.0f;
    boolean superDraw = false;
    boolean useAlphaSignature = false;
    public int transitionMode = 0;
    public int pikerHeight = AndroidUtilities.dp(46.0f);
    public RectF chartArea = new RectF();
    private ValueAnimator.AnimatorUpdateListener pickerHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView.1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            BaseChartView.this.pickerMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
            BaseChartView.this.invalidatePickerChart = true;
            BaseChartView.this.invalidate();
        }
    };
    private ValueAnimator.AnimatorUpdateListener pickerMinHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView.2
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            BaseChartView.this.pickerMinHeight = ((Float) animation.getAnimatedValue()).floatValue();
            BaseChartView.this.invalidatePickerChart = true;
            BaseChartView.this.invalidate();
        }
    };
    private ValueAnimator.AnimatorUpdateListener heightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda0
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            BaseChartView.this.m1749lambda$new$0$orgtelegramuiChartsBaseChartView(valueAnimator);
        }
    };
    private ValueAnimator.AnimatorUpdateListener minHeightUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda1
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            BaseChartView.this.m1750lambda$new$1$orgtelegramuiChartsBaseChartView(valueAnimator);
        }
    };
    private ValueAnimator.AnimatorUpdateListener selectionAnimatorListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView.3
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            BaseChartView.this.selectionA = ((Float) animation.getAnimatedValue()).floatValue();
            BaseChartView.this.legendSignatureView.setAlpha(BaseChartView.this.selectionA);
            BaseChartView.this.invalidate();
        }
    };
    private Animator.AnimatorListener selectorAnimatorEndListener = new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.BaseChartView.4
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (!BaseChartView.this.animateLegentTo) {
                BaseChartView.this.legendShowing = false;
                BaseChartView.this.legendSignatureView.setVisibility(8);
                BaseChartView.this.invalidate();
            }
            BaseChartView.this.postTransition = false;
        }
    };
    protected boolean useMinHeight = false;
    int lastW = 0;
    int lastH = 0;
    long lastTime = 0;
    public boolean animateLegentTo = false;

    /* loaded from: classes4.dex */
    public interface DateSelectionListener {
        void onDateSelected(long j);
    }

    public abstract L createLineViewData(ChartData.Line line);

    static {
        boolean z = true;
        USE_LINES = Build.VERSION.SDK_INT < 28;
        if (Build.VERSION.SDK_INT <= 21) {
            z = false;
        }
        ANIMATE_PICKER_SIZES = z;
        INTERPOLATOR = new FastOutSlowInInterpolator();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Charts-BaseChartView */
    public /* synthetic */ void m1749lambda$new$0$orgtelegramuiChartsBaseChartView(ValueAnimator animation) {
        this.currentMaxHeight = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Charts-BaseChartView */
    public /* synthetic */ void m1750lambda$new$1$orgtelegramuiChartsBaseChartView(ValueAnimator animation) {
        this.currentMinHeight = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    public BaseChartView(Context context) {
        super(context);
        init();
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void init() {
        this.linePaint.setStrokeWidth(1.0f);
        this.selectedLinePaint.setStrokeWidth(SELECTED_LINE_WIDTH);
        Paint paint = this.signaturePaint;
        float f = SIGNATURE_TEXT_SIZE;
        paint.setTextSize(f);
        this.signaturePaint2.setTextSize(f);
        this.signaturePaint2.setTextAlign(Paint.Align.RIGHT);
        this.bottomSignaturePaint.setTextSize(f);
        this.bottomSignaturePaint.setTextAlign(Paint.Align.CENTER);
        this.selectionBackgroundPaint.setStrokeWidth(AndroidUtilities.dpf2(6.0f));
        this.selectionBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(2, null);
        setWillNotDraw(false);
        LegendSignatureView createLegendView = createLegendView();
        this.legendSignatureView = createLegendView;
        createLegendView.setVisibility(8);
        this.whiteLinePaint.setColor(-1);
        this.whiteLinePaint.setStrokeWidth(AndroidUtilities.dpf2(3.0f));
        this.whiteLinePaint.setStrokeCap(Paint.Cap.ROUND);
        updateColors();
    }

    protected LegendSignatureView createLegendView() {
        return new LegendSignatureView(getContext());
    }

    public void updateColors() {
        if (this.useAlphaSignature) {
            this.signaturePaint.setColor(Theme.getColor(Theme.key_statisticChartSignatureAlpha));
        } else {
            this.signaturePaint.setColor(Theme.getColor(Theme.key_statisticChartSignature));
        }
        this.bottomSignaturePaint.setColor(Theme.getColor(Theme.key_statisticChartSignature));
        this.linePaint.setColor(Theme.getColor(Theme.key_statisticChartHintLine));
        this.selectedLinePaint.setColor(Theme.getColor(Theme.key_statisticChartActiveLine));
        this.pickerSelectorPaint.setColor(Theme.getColor(Theme.key_statisticChartActivePickerChart));
        this.unactiveBottomChartPaint.setColor(Theme.getColor(Theme.key_statisticChartInactivePickerChart));
        this.selectionBackgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.ripplePaint.setColor(Theme.getColor(Theme.key_statisticChartRipple));
        this.legendSignatureView.recolor();
        this.hintLinePaintAlpha = this.linePaint.getAlpha();
        this.chartActiveLineAlpha = this.selectedLinePaint.getAlpha();
        this.signaturePaintAlpha = this.signaturePaint.getAlpha() / 255.0f;
        this.bottomSignaturePaintAlpha = this.bottomSignaturePaint.getAlpha() / 255.0f;
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            LineViewData l = it.next();
            l.updateColors();
        }
        if (this.legendShowing && this.selectedIndex < this.chartData.x.length) {
            this.legendSignatureView.setData(this.selectedIndex, this.chartData.x[this.selectedIndex], this.lines, false);
        }
        this.invalidatePickerChart = true;
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.landscape) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.displaySize.y - AndroidUtilities.dp(56.0f));
        }
        if (getMeasuredWidth() != this.lastW || getMeasuredHeight() != this.lastH) {
            this.lastW = getMeasuredWidth();
            this.lastH = getMeasuredHeight();
            float f = HORIZONTAL_PADDING;
            this.bottomChartBitmap = Bitmap.createBitmap((int) (getMeasuredWidth() - (f * 2.0f)), this.pikerHeight, Bitmap.Config.ARGB_4444);
            this.bottomChartCanvas = new Canvas(this.bottomChartBitmap);
            this.sharedUiComponents.getPickerMaskBitmap(this.pikerHeight, (int) (getMeasuredWidth() - (2.0f * f)));
            measureSizes();
            if (this.legendShowing) {
                moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - f);
            }
            onPickerDataChanged(false, true, false);
        }
    }

    private void measureSizes() {
        if (getMeasuredHeight() <= 0 || getMeasuredWidth() <= 0) {
            return;
        }
        float f = HORIZONTAL_PADDING;
        this.pickerWidth = getMeasuredWidth() - (2.0f * f);
        this.chartStart = f;
        float measuredWidth = getMeasuredWidth() - (this.landscape ? LANDSCAPE_END_PADDING : f);
        this.chartEnd = measuredWidth;
        float f2 = measuredWidth - this.chartStart;
        this.chartWidth = f2;
        this.chartFullWidth = f2 / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
        updateLineSignature();
        this.chartBottom = AndroidUtilities.dp(100.0f);
        this.chartArea.set(this.chartStart - f, 0.0f, this.chartEnd + f, getMeasuredHeight() - this.chartBottom);
        if (this.chartData != null) {
            this.bottomSignatureOffset = (int) (AndroidUtilities.dp(20.0f) / (this.pickerWidth / this.chartData.x.length));
        }
        measureHeightThreshold();
    }

    private void measureHeightThreshold() {
        int chartHeight = getMeasuredHeight() - this.chartBottom;
        float f = this.animateToMaxHeight;
        if (f == 0.0f || chartHeight == 0) {
            return;
        }
        this.thresholdMaxHeight = (f / chartHeight) * SIGNATURE_TEXT_SIZE;
    }

    protected void drawPickerChart(Canvas canvas) {
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.superDraw) {
            super.onDraw(canvas);
            return;
        }
        tick();
        int count = canvas.save();
        canvas.clipRect(0.0f, this.chartArea.top, getMeasuredWidth(), this.chartArea.bottom);
        drawBottomLine(canvas);
        this.tmpN = this.horizontalLines.size();
        int i = 0;
        this.tmpI = 0;
        while (true) {
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            drawHorizontalLines(canvas, this.horizontalLines.get(i2));
            this.tmpI++;
        }
        drawChart(canvas);
        while (true) {
            this.tmpI = i;
            int i3 = this.tmpI;
            if (i3 < this.tmpN) {
                drawSignaturesToHorizontalLines(canvas, this.horizontalLines.get(i3));
                i = this.tmpI + 1;
            } else {
                canvas.restoreToCount(count);
                drawBottomSignature(canvas);
                drawPicker(canvas);
                drawSelection(canvas);
                super.onDraw(canvas);
                return;
            }
        }
    }

    public void tick() {
        float f = this.minMaxUpdateStep;
        if (f == 0.0f) {
            return;
        }
        float f2 = this.currentMaxHeight;
        float f3 = this.animateToMaxHeight;
        if (f2 != f3) {
            float f4 = this.startFromMax + f;
            this.startFromMax = f4;
            if (f4 > 1.0f) {
                this.startFromMax = 1.0f;
                this.currentMaxHeight = f3;
            } else {
                float f5 = this.startFromMaxH;
                this.currentMaxHeight = f5 + ((f3 - f5) * CubicBezierInterpolator.EASE_OUT.getInterpolation(this.startFromMax));
            }
            invalidate();
        }
        if (this.useMinHeight) {
            float f6 = this.currentMinHeight;
            float f7 = this.animateToMinHeight;
            if (f6 != f7) {
                float f8 = this.startFromMin + this.minMaxUpdateStep;
                this.startFromMin = f8;
                if (f8 > 1.0f) {
                    this.startFromMin = 1.0f;
                    this.currentMinHeight = f7;
                } else {
                    float f9 = this.startFromMinH;
                    this.currentMinHeight = f9 + ((f7 - f9) * CubicBezierInterpolator.EASE_OUT.getInterpolation(this.startFromMin));
                }
                invalidate();
            }
        }
    }

    public void drawBottomSignature(Canvas canvas) {
        if (this.chartData == null) {
            return;
        }
        this.tmpN = this.bottomSignatureDate.size();
        float transitionAlpha = 1.0f;
        int i = this.transitionMode;
        int i2 = 1;
        if (i == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (i == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        char c = 0;
        this.tmpI = 0;
        while (true) {
            int i3 = this.tmpI;
            if (i3 >= this.tmpN) {
                return;
            }
            int resultAlpha = this.bottomSignatureDate.get(i3).alpha;
            int step = this.bottomSignatureDate.get(this.tmpI).step;
            if (step == 0) {
                step = 1;
            }
            int start = this.startXIndex - this.bottomSignatureOffset;
            while (start % step != 0) {
                start--;
            }
            int end = this.endXIndex - this.bottomSignatureOffset;
            while (true) {
                if (end % step == 0 && end >= this.chartData.x.length - i2) {
                    break;
                }
                end++;
                c = 0;
            }
            int i4 = this.bottomSignatureOffset;
            int start2 = start + i4;
            int end2 = end + i4;
            float offset = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
            int i5 = start2;
            while (i5 < end2) {
                if (i5 >= 0 && i5 < this.chartData.x.length - i2) {
                    float xPercentage = ((float) (this.chartData.x[i5] - this.chartData.x[c])) / ((float) (this.chartData.x[this.chartData.x.length - i2] - this.chartData.x[c]));
                    float xPoint = (this.chartFullWidth * xPercentage) - offset;
                    float xPointOffset = xPoint - BOTTOM_SIGNATURE_OFFSET;
                    if (xPointOffset > 0.0f) {
                        float f = this.chartWidth;
                        float f2 = HORIZONTAL_PADDING;
                        if (xPointOffset <= f + f2) {
                            int i6 = BOTTOM_SIGNATURE_START_ALPHA;
                            if (xPointOffset < i6) {
                                float a = 1.0f - ((i6 - xPointOffset) / i6);
                                this.bottomSignaturePaint.setAlpha((int) (resultAlpha * a * this.bottomSignaturePaintAlpha * transitionAlpha));
                            } else if (xPointOffset <= f) {
                                this.bottomSignaturePaint.setAlpha((int) (resultAlpha * this.bottomSignaturePaintAlpha * transitionAlpha));
                            } else {
                                float a2 = 1.0f - ((xPointOffset - f) / f2);
                                this.bottomSignaturePaint.setAlpha((int) (resultAlpha * a2 * this.bottomSignaturePaintAlpha * transitionAlpha));
                            }
                            canvas.drawText(this.chartData.getDayString(i5), xPoint, (getMeasuredHeight() - this.chartBottom) + BOTTOM_SIGNATURE_TEXT_HEIGHT + AndroidUtilities.dp(3.0f), this.bottomSignaturePaint);
                        }
                    }
                }
                i5 += step;
                c = 0;
                i2 = 1;
            }
            i2 = 1;
            this.tmpI++;
            c = 0;
        }
    }

    public void drawBottomLine(Canvas canvas) {
        if (this.chartData == null) {
            return;
        }
        float transitionAlpha = 1.0f;
        int i = this.transitionMode;
        if (i == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (i == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (this.hintLinePaintAlpha * transitionAlpha));
        this.signaturePaint.setAlpha((int) (this.signaturePaintAlpha * 255.0f * transitionAlpha));
        int textOffset = (int) (SIGNATURE_TEXT_HEIGHT - this.signaturePaint.getTextSize());
        int y = (getMeasuredHeight() - this.chartBottom) - 1;
        canvas.drawLine(this.chartStart, y, this.chartEnd, y, this.linePaint);
        if (this.useMinHeight) {
            return;
        }
        canvas.drawText("0", HORIZONTAL_PADDING, y - textOffset, this.signaturePaint);
    }

    public void drawSelection(Canvas canvas) {
        if (this.selectedIndex < 0 || !this.legendShowing || this.chartData == null) {
            return;
        }
        int alpha = (int) (this.chartActiveLineAlpha * this.selectionA);
        float fullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
        float offset = (this.pickerDelegate.pickerStart * fullWidth) - HORIZONTAL_PADDING;
        if (this.selectedIndex < this.chartData.xPercentage.length) {
            float xPoint = (this.chartData.xPercentage[this.selectedIndex] * fullWidth) - offset;
            this.selectedLinePaint.setAlpha(alpha);
            canvas.drawLine(xPoint, 0.0f, xPoint, this.chartArea.bottom, this.selectedLinePaint);
            if (this.drawPointOnSelection) {
                this.tmpN = this.lines.size();
                int i = 0;
                while (true) {
                    this.tmpI = i;
                    int i2 = this.tmpI;
                    if (i2 < this.tmpN) {
                        LineViewData line = this.lines.get(i2);
                        if (line.enabled || line.alpha != 0.0f) {
                            float f = this.currentMinHeight;
                            float yPercentage = (line.line.y[this.selectedIndex] - f) / (this.currentMaxHeight - f);
                            float yPoint = (getMeasuredHeight() - this.chartBottom) - (((getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT) * yPercentage);
                            line.selectionPaint.setAlpha((int) (line.alpha * 255.0f * this.selectionA));
                            this.selectionBackgroundPaint.setAlpha((int) (line.alpha * 255.0f * this.selectionA));
                            canvas.drawPoint(xPoint, yPoint, line.selectionPaint);
                            canvas.drawPoint(xPoint, yPoint, this.selectionBackgroundPaint);
                        }
                        i = this.tmpI + 1;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    protected void drawChart(Canvas canvas) {
    }

    public void drawHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        int n = a.values.length;
        float additionalOutAlpha = 1.0f;
        if (n > 2) {
            float v = (a.values[1] - a.values[0]) / (this.currentMaxHeight - this.currentMinHeight);
            if (v < 0.1d) {
                additionalOutAlpha = v / 0.1f;
            }
        }
        float transitionAlpha = 1.0f;
        int i = this.transitionMode;
        if (i == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (i == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (a.alpha * (this.hintLinePaintAlpha / 255.0f) * transitionAlpha * additionalOutAlpha));
        this.signaturePaint.setAlpha((int) (a.alpha * this.signaturePaintAlpha * transitionAlpha * additionalOutAlpha));
        int chartHeight = (getMeasuredHeight() - this.chartBottom) - SIGNATURE_TEXT_HEIGHT;
        for (int i2 = 1 ^ (this.useMinHeight ? 1 : 0); i2 < n; i2++) {
            float f = this.currentMinHeight;
            int y = (int) ((getMeasuredHeight() - this.chartBottom) - (chartHeight * ((a.values[i2] - f) / (this.currentMaxHeight - f))));
            canvas.drawRect(this.chartStart, y, this.chartEnd, y + 1, this.linePaint);
        }
    }

    public void drawSignaturesToHorizontalLines(Canvas canvas, ChartHorizontalLinesData a) {
        int n = a.values.length;
        float additionalOutAlpha = 1.0f;
        if (n > 2) {
            float v = (a.values[1] - a.values[0]) / (this.currentMaxHeight - this.currentMinHeight);
            if (v < 0.1d) {
                additionalOutAlpha = v / 0.1f;
            }
        }
        float transitionAlpha = 1.0f;
        int i = this.transitionMode;
        if (i == 2) {
            transitionAlpha = 1.0f - this.transitionParams.progress;
        } else if (i == 1) {
            transitionAlpha = this.transitionParams.progress;
        } else if (i == 3) {
            transitionAlpha = this.transitionParams.progress;
        }
        this.linePaint.setAlpha((int) (a.alpha * (this.hintLinePaintAlpha / 255.0f) * transitionAlpha * additionalOutAlpha));
        this.signaturePaint.setAlpha((int) (a.alpha * this.signaturePaintAlpha * transitionAlpha * additionalOutAlpha));
        int measuredHeight = getMeasuredHeight() - this.chartBottom;
        int i2 = SIGNATURE_TEXT_HEIGHT;
        int chartHeight = measuredHeight - i2;
        int textOffset = (int) (i2 - this.signaturePaint.getTextSize());
        for (int i3 = 1 ^ (this.useMinHeight ? 1 : 0); i3 < n; i3++) {
            float f = this.currentMinHeight;
            int y = (int) ((getMeasuredHeight() - this.chartBottom) - (chartHeight * ((a.values[i3] - f) / (this.currentMaxHeight - f))));
            canvas.drawText(a.valuesStr[i3], HORIZONTAL_PADDING, y - textOffset, this.signaturePaint);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x020a  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0243  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x03c3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void drawPicker(android.graphics.Canvas r29) {
        /*
            Method dump skipped, instructions count: 966
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.BaseChartView.drawPicker(android.graphics.Canvas):void");
    }

    private void setMaxMinValue(int newMaxHeight, int newMinHeight, boolean animated) {
        setMaxMinValue(newMaxHeight, newMinHeight, animated, false, false);
    }

    protected void setMaxMinValue(int newMaxHeight, int newMinHeight, boolean animated, boolean force, boolean useAnimator) {
        boolean heightChanged = true;
        if (Math.abs(ChartHorizontalLinesData.lookupHeight(newMaxHeight) - this.animateToMaxHeight) < this.thresholdMaxHeight || newMaxHeight == 0) {
            heightChanged = false;
        }
        if (heightChanged || newMaxHeight != this.animateToMinHeight) {
            final ChartHorizontalLinesData newData = createHorizontalLinesData(newMaxHeight, newMinHeight);
            int newMaxHeight2 = newData.values[newData.values.length - 1];
            int newMinHeight2 = newData.values[0];
            if (!useAnimator) {
                float f = this.currentMaxHeight;
                float f2 = this.currentMinHeight;
                float k = (f - f2) / (newMaxHeight2 - newMinHeight2);
                if (k > 1.0f) {
                    k = (newMaxHeight2 - newMinHeight2) / (f - f2);
                }
                float s = 0.045f;
                if (k > 0.7d) {
                    s = 0.1f;
                } else if (k < 0.1d) {
                    s = 0.03f;
                }
                boolean update = false;
                if (newMaxHeight2 != this.animateToMaxHeight) {
                    update = true;
                }
                if (this.useMinHeight && newMinHeight2 != this.animateToMinHeight) {
                    update = true;
                }
                if (update) {
                    Animator animator = this.maxValueAnimator;
                    if (animator != null) {
                        animator.removeAllListeners();
                        this.maxValueAnimator.cancel();
                    }
                    this.startFromMaxH = this.currentMaxHeight;
                    this.startFromMinH = this.currentMinHeight;
                    this.startFromMax = 0.0f;
                    this.startFromMin = 0.0f;
                    this.minMaxUpdateStep = s;
                }
            }
            float s2 = newMaxHeight2;
            this.animateToMaxHeight = s2;
            this.animateToMinHeight = newMinHeight2;
            measureHeightThreshold();
            long t = System.currentTimeMillis();
            if (t - this.lastTime < 320 && !force) {
                return;
            }
            this.lastTime = t;
            ValueAnimator valueAnimator = this.alphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.alphaAnimator.cancel();
            }
            if (!animated) {
                this.currentMaxHeight = newMaxHeight2;
                this.currentMinHeight = newMinHeight2;
                this.horizontalLines.clear();
                this.horizontalLines.add(newData);
                newData.alpha = 255;
                return;
            }
            this.horizontalLines.add(newData);
            if (useAnimator) {
                Animator animator2 = this.maxValueAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.maxValueAnimator.cancel();
                }
                this.minMaxUpdateStep = 0.0f;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(createAnimator(this.currentMaxHeight, newMaxHeight2, this.heightUpdateListener));
                if (this.useMinHeight) {
                    animatorSet.playTogether(createAnimator(this.currentMinHeight, newMinHeight2, this.minHeightUpdateListener));
                }
                this.maxValueAnimator = animatorSet;
                animatorSet.start();
            }
            int n = this.horizontalLines.size();
            for (int i = 0; i < n; i++) {
                ChartHorizontalLinesData a = this.horizontalLines.get(i);
                if (a != newData) {
                    a.fixedAlpha = a.alpha;
                }
            }
            ValueAnimator createAnimator = createAnimator(0.0f, 255.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BaseChartView.this.m1753lambda$setMaxMinValue$2$orgtelegramuiChartsBaseChartView(newData, valueAnimator2);
                }
            });
            this.alphaAnimator = createAnimator;
            createAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.BaseChartView.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BaseChartView.this.horizontalLines.clear();
                    BaseChartView.this.horizontalLines.add(newData);
                }
            });
            this.alphaAnimator.start();
        }
    }

    /* renamed from: lambda$setMaxMinValue$2$org-telegram-ui-Charts-BaseChartView */
    public /* synthetic */ void m1753lambda$setMaxMinValue$2$orgtelegramuiChartsBaseChartView(ChartHorizontalLinesData newData, ValueAnimator animation) {
        newData.alpha = (int) ((Float) animation.getAnimatedValue()).floatValue();
        Iterator<ChartHorizontalLinesData> it = this.horizontalLines.iterator();
        while (it.hasNext()) {
            ChartHorizontalLinesData a = it.next();
            if (a != newData) {
                a.alpha = (int) ((a.fixedAlpha / 255.0f) * (255 - newData.alpha));
            }
        }
        invalidate();
    }

    protected ChartHorizontalLinesData createHorizontalLinesData(int newMaxHeight, int newMinHeight) {
        return new ChartHorizontalLinesData(newMaxHeight, newMinHeight, this.useMinHeight);
    }

    public ValueAnimator createAnimator(float f1, float f2, ValueAnimator.AnimatorUpdateListener l) {
        ValueAnimator a = ValueAnimator.ofFloat(f1, f2);
        a.setDuration(400L);
        a.setInterpolator(INTERPOLATOR);
        a.addUpdateListener(l);
        return a;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean disable = false;
        if (this.chartData == null) {
            return false;
        }
        if (!this.enabled) {
            this.pickerDelegate.uncapture(event, event.getActionIndex());
            getParent().requestDisallowInterceptTouchEvent(false);
            this.chartCaptured = false;
            return false;
        }
        int x = (int) event.getX(event.getActionIndex());
        int y = (int) event.getY(event.getActionIndex());
        switch (event.getActionMasked()) {
            case 0:
                this.capturedTime = System.currentTimeMillis();
                getParent().requestDisallowInterceptTouchEvent(true);
                boolean captured = this.pickerDelegate.capture(x, y, event.getActionIndex());
                if (captured) {
                    return true;
                }
                this.lastX = x;
                this.capturedX = x;
                this.lastY = y;
                this.capturedY = y;
                if (!this.chartArea.contains(x, y)) {
                    return false;
                }
                if (this.selectedIndex < 0 || !this.animateLegentTo) {
                    this.chartCaptured = true;
                    selectXOnChart(x, y);
                }
                return true;
            case 1:
            case 3:
                if (this.pickerDelegate.uncapture(event, event.getActionIndex())) {
                    return true;
                }
                if (this.chartArea.contains(this.capturedX, this.capturedY) && !this.chartCaptured) {
                    animateLegend(false);
                }
                this.pickerDelegate.uncapture();
                updateLineSignature();
                getParent().requestDisallowInterceptTouchEvent(false);
                this.chartCaptured = false;
                onActionUp();
                invalidate();
                int min = 0;
                if (this.useMinHeight) {
                    min = findMinValue(this.startXIndex, this.endXIndex);
                }
                setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), min, true, true, false);
                return true;
            case 2:
                int dx = x - this.lastX;
                int dy = y - this.lastY;
                if (this.pickerDelegate.captured()) {
                    boolean rez = this.pickerDelegate.move(x, y, event.getActionIndex());
                    if (event.getPointerCount() > 1) {
                        this.pickerDelegate.move((int) event.getX(1), (int) event.getY(1), 1);
                    }
                    getParent().requestDisallowInterceptTouchEvent(rez);
                    return true;
                }
                if (this.chartCaptured) {
                    if (this.canCaptureChartSelection && System.currentTimeMillis() - this.capturedTime > 200) {
                        disable = true;
                    } else if (Math.abs(dx) > Math.abs(dy) || Math.abs(dy) < this.touchSlop) {
                        disable = true;
                    }
                    this.lastX = x;
                    this.lastY = y;
                    getParent().requestDisallowInterceptTouchEvent(disable);
                    selectXOnChart(x, y);
                } else if (this.chartArea.contains(this.capturedX, this.capturedY)) {
                    int dxCaptured = this.capturedX - x;
                    int dyCaptured = this.capturedY - y;
                    if (Math.sqrt((dxCaptured * dxCaptured) + (dyCaptured * dyCaptured)) > this.touchSlop || System.currentTimeMillis() - this.capturedTime > 200) {
                        this.chartCaptured = true;
                        selectXOnChart(x, y);
                    }
                }
                return true;
            case 4:
            default:
                return false;
            case 5:
                return this.pickerDelegate.capture(x, y, event.getActionIndex());
            case 6:
                this.pickerDelegate.uncapture(event, event.getActionIndex());
                return true;
        }
    }

    protected void onActionUp() {
    }

    protected void selectXOnChart(int x, int y) {
        int oldSelectedX = this.selectedIndex;
        if (this.chartData == null) {
            return;
        }
        float offset = (this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING;
        float xP = (x + offset) / this.chartFullWidth;
        this.selectedCoordinate = xP;
        if (xP < 0.0f) {
            this.selectedIndex = 0;
            this.selectedCoordinate = 0.0f;
        } else if (xP > 1.0f) {
            this.selectedIndex = this.chartData.x.length - 1;
            this.selectedCoordinate = 1.0f;
        } else {
            int findIndex = this.chartData.findIndex(this.startXIndex, this.endXIndex, xP);
            this.selectedIndex = findIndex;
            if (findIndex + 1 < this.chartData.xPercentage.length) {
                float dx = Math.abs(this.chartData.xPercentage[this.selectedIndex] - xP);
                float dx2 = Math.abs(this.chartData.xPercentage[this.selectedIndex + 1] - xP);
                if (dx2 < dx) {
                    this.selectedIndex++;
                }
            }
        }
        int i = this.selectedIndex;
        int i2 = this.endXIndex;
        if (i > i2) {
            this.selectedIndex = i2;
        }
        int i3 = this.selectedIndex;
        int i4 = this.startXIndex;
        if (i3 < i4) {
            this.selectedIndex = i4;
        }
        if (oldSelectedX != this.selectedIndex) {
            this.legendShowing = true;
            animateLegend(true);
            moveLegend(offset);
            DateSelectionListener dateSelectionListener = this.dateSelectionListener;
            if (dateSelectionListener != null) {
                dateSelectionListener.onDateSelected(getSelectedDate());
            }
            runSmoothHaptic();
            invalidate();
        }
    }

    public void runSmoothHaptic() {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
            if (this.vibrationEffect == null) {
                long[] vibrationWaveFormDurationPattern = {0, 2};
                this.vibrationEffect = VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, -1);
            }
            vibrator.cancel();
            vibrator.vibrate(this.vibrationEffect);
        }
    }

    public void animateLegend(boolean show) {
        moveLegend();
        if (this.animateLegentTo == show) {
            return;
        }
        this.animateLegentTo = show;
        ValueAnimator valueAnimator = this.selectionAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.selectionAnimator.cancel();
        }
        ValueAnimator duration = createAnimator(this.selectionA, show ? 1.0f : 0.0f, this.selectionAnimatorListener).setDuration(200L);
        this.selectionAnimator = duration;
        duration.addListener(this.selectorAnimatorEndListener);
        this.selectionAnimator.start();
    }

    public void moveLegend(float offset) {
        int i;
        float lXPoint;
        T t = this.chartData;
        if (t == null || (i = this.selectedIndex) == -1 || !this.legendShowing) {
            return;
        }
        this.legendSignatureView.setData(i, t.x[this.selectedIndex], this.lines, false);
        this.legendSignatureView.setVisibility(0);
        this.legendSignatureView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE));
        float lXPoint2 = (this.chartData.xPercentage[this.selectedIndex] * this.chartFullWidth) - offset;
        if (lXPoint2 > (this.chartStart + this.chartWidth) / 2.0f) {
            lXPoint = lXPoint2 - (this.legendSignatureView.getWidth() + DP_5);
        } else {
            lXPoint = lXPoint2 + DP_5;
        }
        if (lXPoint < 0.0f) {
            lXPoint = 0.0f;
        } else if (this.legendSignatureView.getMeasuredWidth() + lXPoint > getMeasuredWidth()) {
            lXPoint = getMeasuredWidth() - this.legendSignatureView.getMeasuredWidth();
        }
        this.legendSignatureView.setTranslationX(lXPoint);
    }

    public int findMaxValue(int startXIndex, int endXIndex) {
        int lineMax;
        int linesSize = this.lines.size();
        int maxValue = 0;
        for (int j = 0; j < linesSize; j++) {
            if (this.lines.get(j).enabled && (lineMax = this.lines.get(j).line.segmentTree.rMaxQ(startXIndex, endXIndex)) > maxValue) {
                maxValue = lineMax;
            }
        }
        return maxValue;
    }

    public int findMinValue(int startXIndex, int endXIndex) {
        int lineMin;
        int linesSize = this.lines.size();
        int minValue = Integer.MAX_VALUE;
        for (int j = 0; j < linesSize; j++) {
            if (this.lines.get(j).enabled && (lineMin = this.lines.get(j).line.segmentTree.rMinQ(startXIndex, endXIndex)) < minValue) {
                minValue = lineMin;
            }
        }
        return minValue;
    }

    public void setData(T chartData) {
        if (this.chartData != chartData) {
            invalidate();
            this.lines.clear();
            if (chartData != null && chartData.lines != null) {
                for (int i = 0; i < chartData.lines.size(); i++) {
                    this.lines.add(createLineViewData(chartData.lines.get(i)));
                }
            }
            clearSelection();
            this.chartData = chartData;
            if (chartData != null) {
                if (chartData.x[0] == 0) {
                    this.pickerDelegate.pickerStart = 0.0f;
                    this.pickerDelegate.pickerEnd = 1.0f;
                } else {
                    this.pickerDelegate.minDistance = getMinDistance();
                    if (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart < this.pickerDelegate.minDistance) {
                        ChartPickerDelegate chartPickerDelegate = this.pickerDelegate;
                        chartPickerDelegate.pickerStart = chartPickerDelegate.pickerEnd - this.pickerDelegate.minDistance;
                        if (this.pickerDelegate.pickerStart < 0.0f) {
                            this.pickerDelegate.pickerStart = 0.0f;
                            this.pickerDelegate.pickerEnd = 1.0f;
                        }
                    }
                }
            }
        }
        measureSizes();
        if (chartData != null) {
            updateIndexes();
            int min = this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0;
            setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), min, false);
            this.pickerMaxHeight = 0.0f;
            this.pickerMinHeight = 2.14748365E9f;
            initPickerMaxHeight();
            this.legendSignatureView.setSize(this.lines.size());
            this.invalidatePickerChart = true;
            updateLineSignature();
            return;
        }
        this.pickerDelegate.pickerStart = 0.7f;
        this.pickerDelegate.pickerEnd = 1.0f;
        this.pickerMinHeight = 0.0f;
        this.pickerMaxHeight = 0.0f;
        this.horizontalLines.clear();
        Animator animator = this.maxValueAnimator;
        if (animator != null) {
            animator.cancel();
        }
        ValueAnimator valueAnimator = this.alphaAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.alphaAnimator.cancel();
        }
    }

    protected float getMinDistance() {
        T t = this.chartData;
        if (t == null) {
            return 0.1f;
        }
        int n = t.x.length;
        if (n < 5) {
            return 1.0f;
        }
        float r = 5.0f / n;
        if (r >= 0.1f) {
            return r;
        }
        return 0.1f;
    }

    public void initPickerMaxHeight() {
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            LineViewData l = it.next();
            if (l.enabled && l.line.maxValue > this.pickerMaxHeight) {
                this.pickerMaxHeight = l.line.maxValue;
            }
            if (l.enabled && l.line.minValue < this.pickerMinHeight) {
                this.pickerMinHeight = l.line.minValue;
            }
            float f = this.pickerMaxHeight;
            float f2 = this.pickerMinHeight;
            if (f == f2) {
                this.pickerMaxHeight = f + 1.0f;
                this.pickerMinHeight = f2 - 1.0f;
            }
        }
    }

    @Override // org.telegram.ui.Charts.ChartPickerDelegate.Listener
    public void onPickerDataChanged() {
        onPickerDataChanged(true, false, false);
    }

    public void onPickerDataChanged(boolean animated, boolean force, boolean useAniamtor) {
        if (this.chartData == null) {
            return;
        }
        this.chartFullWidth = this.chartWidth / (this.pickerDelegate.pickerEnd - this.pickerDelegate.pickerStart);
        updateIndexes();
        int min = this.useMinHeight ? findMinValue(this.startXIndex, this.endXIndex) : 0;
        setMaxMinValue(findMaxValue(this.startXIndex, this.endXIndex), min, animated, force, useAniamtor);
        if (this.legendShowing && !force) {
            animateLegend(false);
            moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
        }
        invalidate();
    }

    @Override // org.telegram.ui.Charts.ChartPickerDelegate.Listener
    public void onPickerJumpTo(float start, float end, boolean force) {
        T t = this.chartData;
        if (t == null) {
            return;
        }
        if (force) {
            int startXIndex = t.findStartIndex(Math.max(start, 0.0f));
            int endXIndex = this.chartData.findEndIndex(startXIndex, Math.min(end, 1.0f));
            setMaxMinValue(findMaxValue(startXIndex, endXIndex), findMinValue(startXIndex, endXIndex), true, true, false);
            animateLegend(false);
            return;
        }
        updateIndexes();
        invalidate();
    }

    public void updateIndexes() {
        T t = this.chartData;
        if (t == null) {
            return;
        }
        int findStartIndex = t.findStartIndex(Math.max(this.pickerDelegate.pickerStart, 0.0f));
        this.startXIndex = findStartIndex;
        int findEndIndex = this.chartData.findEndIndex(findStartIndex, Math.min(this.pickerDelegate.pickerEnd, 1.0f));
        this.endXIndex = findEndIndex;
        int i = this.startXIndex;
        if (findEndIndex < i) {
            this.endXIndex = i;
        }
        ChartHeaderView chartHeaderView = this.chartHeaderView;
        if (chartHeaderView != null) {
            chartHeaderView.setDates(this.chartData.x[this.startXIndex], this.chartData.x[this.endXIndex]);
        }
        updateLineSignature();
    }

    private void updateLineSignature() {
        T t = this.chartData;
        if (t == null || this.chartWidth == 0.0f) {
            return;
        }
        float d = this.chartFullWidth * t.oneDayPercentage;
        float k = this.chartWidth / d;
        int step = (int) (k / 6.0f);
        updateDates(step);
    }

    private void updateDates(int step) {
        ChartBottomSignatureData chartBottomSignatureData = this.currentBottomSignatures;
        if (chartBottomSignatureData == null || step >= chartBottomSignatureData.stepMax || step <= this.currentBottomSignatures.stepMin) {
            int step2 = Integer.highestOneBit(step) << 1;
            ChartBottomSignatureData chartBottomSignatureData2 = this.currentBottomSignatures;
            if (chartBottomSignatureData2 != null && chartBottomSignatureData2.step == step2) {
                return;
            }
            ValueAnimator valueAnimator = this.alphaBottomAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.alphaBottomAnimator.cancel();
            }
            double d = step2;
            double d2 = step2;
            Double.isNaN(d2);
            Double.isNaN(d);
            int stepMax = (int) (d + (d2 * 0.2d));
            double d3 = step2;
            double d4 = step2;
            Double.isNaN(d4);
            Double.isNaN(d3);
            int stepMin = (int) (d3 - (d4 * 0.2d));
            final ChartBottomSignatureData data = new ChartBottomSignatureData(step2, stepMax, stepMin);
            data.alpha = 255;
            if (this.currentBottomSignatures == null) {
                this.currentBottomSignatures = data;
                data.alpha = 255;
                this.bottomSignatureDate.add(data);
                return;
            }
            this.currentBottomSignatures = data;
            this.tmpN = this.bottomSignatureDate.size();
            for (int i = 0; i < this.tmpN; i++) {
                ChartBottomSignatureData a = this.bottomSignatureDate.get(i);
                a.fixedAlpha = a.alpha;
            }
            this.bottomSignatureDate.add(data);
            if (this.bottomSignatureDate.size() > 2) {
                this.bottomSignatureDate.remove(0);
            }
            ValueAnimator duration = createAnimator(0.0f, 1.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BaseChartView.this.m1754lambda$updateDates$3$orgtelegramuiChartsBaseChartView(data, valueAnimator2);
                }
            }).setDuration(200L);
            this.alphaBottomAnimator = duration;
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.BaseChartView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    BaseChartView.this.bottomSignatureDate.clear();
                    BaseChartView.this.bottomSignatureDate.add(data);
                }
            });
            this.alphaBottomAnimator.start();
        }
    }

    /* renamed from: lambda$updateDates$3$org-telegram-ui-Charts-BaseChartView */
    public /* synthetic */ void m1754lambda$updateDates$3$orgtelegramuiChartsBaseChartView(ChartBottomSignatureData data, ValueAnimator animation) {
        float alpha = ((Float) animation.getAnimatedValue()).floatValue();
        Iterator<ChartBottomSignatureData> it = this.bottomSignatureDate.iterator();
        while (it.hasNext()) {
            ChartBottomSignatureData a = it.next();
            if (a == data) {
                data.alpha = (int) (255.0f * alpha);
            } else {
                a.alpha = (int) ((1.0f - alpha) * a.fixedAlpha);
            }
        }
        invalidate();
    }

    public void onCheckChanged() {
        onPickerDataChanged(true, true, true);
        this.tmpN = this.lines.size();
        int i = 0;
        while (true) {
            this.tmpI = i;
            int i2 = this.tmpI;
            if (i2 >= this.tmpN) {
                break;
            }
            final LineViewData lineViewData = this.lines.get(i2);
            if (lineViewData.enabled && lineViewData.animatorOut != null) {
                lineViewData.animatorOut.cancel();
            }
            if (!lineViewData.enabled && lineViewData.animatorIn != null) {
                lineViewData.animatorIn.cancel();
            }
            if (lineViewData.enabled && lineViewData.alpha != 1.0f) {
                if (lineViewData.animatorIn != null && lineViewData.animatorIn.isRunning()) {
                    i = this.tmpI + 1;
                } else {
                    lineViewData.animatorIn = createAnimator(lineViewData.alpha, 1.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda4
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            BaseChartView.this.m1751lambda$onCheckChanged$4$orgtelegramuiChartsBaseChartView(lineViewData, valueAnimator);
                        }
                    });
                    lineViewData.animatorIn.start();
                }
            }
            if (!lineViewData.enabled && lineViewData.alpha != 0.0f && (lineViewData.animatorOut == null || !lineViewData.animatorOut.isRunning())) {
                lineViewData.animatorOut = createAnimator(lineViewData.alpha, 0.0f, new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Charts.BaseChartView$$ExternalSyntheticLambda5
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        BaseChartView.this.m1752lambda$onCheckChanged$5$orgtelegramuiChartsBaseChartView(lineViewData, valueAnimator);
                    }
                });
                lineViewData.animatorOut.start();
            }
            i = this.tmpI + 1;
        }
        updatePickerMinMaxHeight();
        if (this.legendShowing) {
            this.legendSignatureView.setData(this.selectedIndex, this.chartData.x[this.selectedIndex], this.lines, true);
        }
    }

    /* renamed from: lambda$onCheckChanged$4$org-telegram-ui-Charts-BaseChartView */
    public /* synthetic */ void m1751lambda$onCheckChanged$4$orgtelegramuiChartsBaseChartView(LineViewData lineViewData, ValueAnimator animation) {
        lineViewData.alpha = ((Float) animation.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    /* renamed from: lambda$onCheckChanged$5$org-telegram-ui-Charts-BaseChartView */
    public /* synthetic */ void m1752lambda$onCheckChanged$5$orgtelegramuiChartsBaseChartView(LineViewData lineViewData, ValueAnimator animation) {
        lineViewData.alpha = ((Float) animation.getAnimatedValue()).floatValue();
        this.invalidatePickerChart = true;
        invalidate();
    }

    public void updatePickerMinMaxHeight() {
        if (!ANIMATE_PICKER_SIZES) {
            return;
        }
        int max = 0;
        int min = Integer.MAX_VALUE;
        Iterator<L> it = this.lines.iterator();
        while (it.hasNext()) {
            LineViewData l = it.next();
            if (l.enabled && l.line.maxValue > max) {
                max = l.line.maxValue;
            }
            if (l.enabled && l.line.minValue < min) {
                min = l.line.minValue;
            }
        }
        if ((min != Integer.MAX_VALUE && min != this.animatedToPickerMinHeight) || (max > 0 && max != this.animatedToPickerMaxHeight)) {
            this.animatedToPickerMaxHeight = max;
            Animator animator = this.pickerAnimator;
            if (animator != null) {
                animator.cancel();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(createAnimator(this.pickerMaxHeight, this.animatedToPickerMaxHeight, this.pickerHeightUpdateListener), createAnimator(this.pickerMinHeight, this.animatedToPickerMinHeight, this.pickerMinHeightUpdateListener));
            this.pickerAnimator = animatorSet;
            animatorSet.start();
        }
    }

    public void setLandscape(boolean b) {
        this.landscape = b;
    }

    public void saveState(Bundle outState) {
        if (outState == null) {
            return;
        }
        outState.putFloat("chart_start", this.pickerDelegate.pickerStart);
        outState.putFloat("chart_end", this.pickerDelegate.pickerEnd);
        ArrayList<L> arrayList = this.lines;
        if (arrayList != null) {
            int n = arrayList.size();
            boolean[] bArray = new boolean[n];
            for (int i = 0; i < n; i++) {
                bArray[i] = this.lines.get(i).enabled;
            }
            outState.putBooleanArray("chart_line_enabled", bArray);
        }
    }

    public void setHeader(ChartHeaderView chartHeaderView) {
        this.chartHeaderView = chartHeaderView;
    }

    public long getSelectedDate() {
        if (this.selectedIndex < 0) {
            return -1L;
        }
        return this.chartData.x[this.selectedIndex];
    }

    public void clearSelection() {
        this.selectedIndex = -1;
        this.legendShowing = false;
        this.animateLegentTo = false;
        this.legendSignatureView.setVisibility(8);
        this.selectionA = 0.0f;
    }

    public void selectDate(long activeZoom) {
        this.selectedIndex = Arrays.binarySearch(this.chartData.x, activeZoom);
        this.legendShowing = true;
        this.legendSignatureView.setVisibility(0);
        this.selectionA = 1.0f;
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
        performHapticFeedback(3, 2);
    }

    public long getStartDate() {
        return this.chartData.x[this.startXIndex];
    }

    public long getEndDate() {
        return this.chartData.x[this.endXIndex];
    }

    public void updatePicker(ChartData chartData, long d) {
        int n = chartData.x.length;
        long startOfDay = d - (d % 86400000);
        long endOfDay = (86400000 + startOfDay) - 1;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < n; i++) {
            if (startOfDay > chartData.x[i]) {
                startIndex = i;
            }
            if (endOfDay > chartData.x[i]) {
                endIndex = i;
            }
        }
        this.pickerDelegate.pickerStart = chartData.xPercentage[startIndex];
        this.pickerDelegate.pickerEnd = chartData.xPercentage[endIndex];
    }

    public void moveLegend() {
        moveLegend((this.chartFullWidth * this.pickerDelegate.pickerStart) - HORIZONTAL_PADDING);
    }

    @Override // android.view.View
    public void requestLayout() {
        super.requestLayout();
    }

    public static Path RoundedRect(Path path, float left, float top, float right, float bottom, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
        path.reset();
        float rx2 = rx < 0.0f ? 0.0f : rx;
        float ry2 = ry < 0.0f ? 0.0f : ry;
        float width = right - left;
        float height = bottom - top;
        if (rx2 > width / 2.0f) {
            rx2 = width / 2.0f;
        }
        if (ry2 > height / 2.0f) {
            ry2 = height / 2.0f;
        }
        float widthMinusCorners = width - (rx2 * 2.0f);
        float heightMinusCorners = height - (2.0f * ry2);
        path.moveTo(right, top + ry2);
        if (tr) {
            path.rQuadTo(0.0f, -ry2, -rx2, -ry2);
        } else {
            path.rLineTo(0.0f, -ry2);
            path.rLineTo(-rx2, 0.0f);
        }
        path.rLineTo(-widthMinusCorners, 0.0f);
        if (tl) {
            path.rQuadTo(-rx2, 0.0f, -rx2, ry2);
        } else {
            path.rLineTo(-rx2, 0.0f);
            path.rLineTo(0.0f, ry2);
        }
        path.rLineTo(0.0f, heightMinusCorners);
        if (bl) {
            path.rQuadTo(0.0f, ry2, rx2, ry2);
        } else {
            path.rLineTo(0.0f, ry2);
            path.rLineTo(rx2, 0.0f);
        }
        path.rLineTo(widthMinusCorners, 0.0f);
        if (br) {
            path.rQuadTo(rx2, 0.0f, rx2, -ry2);
        } else {
            path.rLineTo(rx2, 0.0f);
            path.rLineTo(0.0f, -ry2);
        }
        path.rLineTo(0.0f, -heightMinusCorners);
        path.close();
        return path;
    }

    public void setDateSelectionListener(DateSelectionListener dateSelectionListener) {
        this.dateSelectionListener = dateSelectionListener;
    }

    /* loaded from: classes4.dex */
    public static class SharedUiComponents {
        private Canvas canvas;
        private Bitmap pickerRoundBitmap;
        private Paint xRefP;
        private RectF rectF = new RectF();
        int k = 0;
        private boolean invalidate = true;

        public SharedUiComponents() {
            Paint paint = new Paint(1);
            this.xRefP = paint;
            paint.setColor(0);
            this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        Bitmap getPickerMaskBitmap(int h, int w) {
            if (((h + w) << 10) != this.k || this.invalidate) {
                this.invalidate = false;
                this.k = (h + w) << 10;
                this.pickerRoundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                this.canvas = new Canvas(this.pickerRoundBitmap);
                this.rectF.set(0.0f, 0.0f, w, h);
                this.canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.xRefP);
            }
            return this.pickerRoundBitmap;
        }

        public void invalidate() {
            this.invalidate = true;
        }
    }

    public void fillTransitionParams(TransitionParams params) {
    }
}
