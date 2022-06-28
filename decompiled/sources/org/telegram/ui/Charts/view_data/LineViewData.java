package org.telegram.ui.Charts.view_data;

import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Charts.data.ChartData;
/* loaded from: classes4.dex */
public class LineViewData {
    public ValueAnimator animatorIn;
    public ValueAnimator animatorOut;
    public final Paint bottomLinePaint;
    public final ChartData.Line line;
    public int lineColor;
    public float[] linesPath;
    public float[] linesPathBottom;
    public int linesPathBottomSize;
    public final Paint paint;
    public final Paint selectionPaint;
    public final Path bottomLinePath = new Path();
    public final Path chartPath = new Path();
    public final Path chartPathPicker = new Path();
    public boolean enabled = true;
    public float alpha = 1.0f;

    public LineViewData(ChartData.Line line) {
        Paint paint = new Paint(1);
        this.bottomLinePaint = paint;
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        Paint paint3 = new Paint(1);
        this.selectionPaint = paint3;
        this.line = line;
        paint2.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
        paint2.setStyle(Paint.Style.STROKE);
        if (!BaseChartView.USE_LINES) {
            paint2.setStrokeJoin(Paint.Join.ROUND);
        }
        paint2.setColor(line.color);
        paint.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(line.color);
        paint3.setStrokeWidth(AndroidUtilities.dpf2(10.0f));
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeCap(Paint.Cap.ROUND);
        paint3.setColor(line.color);
        this.linesPath = new float[line.y.length << 2];
        this.linesPathBottom = new float[line.y.length << 2];
    }

    public void updateColors() {
        if (this.line.colorKey != null && Theme.hasThemeKey(this.line.colorKey)) {
            this.lineColor = Theme.getColor(this.line.colorKey);
        } else {
            int color = Theme.getColor(Theme.key_windowBackgroundWhite);
            boolean darkBackground = ColorUtils.calculateLuminance(color) < 0.5d;
            ChartData.Line line = this.line;
            this.lineColor = darkBackground ? line.colorDark : line.color;
        }
        this.paint.setColor(this.lineColor);
        this.bottomLinePaint.setColor(this.lineColor);
        this.selectionPaint.setColor(this.lineColor);
    }
}
