package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.Vector;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Paint.Brush;
/* loaded from: classes5.dex */
public class Input {
    private boolean beganDrawing;
    private boolean clearBuffer;
    private boolean hasMoved;
    private Matrix invertMatrix;
    private boolean isFirst;
    private float lastAngle;
    private Point lastLocation;
    private double lastRemainder;
    private int pointsCount;
    private RenderView renderView;
    private Point[] points = new Point[3];
    private float[] tempPoint = new float[2];

    public Input(RenderView render) {
        this.renderView = render;
    }

    public void setMatrix(Matrix m) {
        Matrix matrix = new Matrix();
        this.invertMatrix = matrix;
        m.invert(matrix);
    }

    public void process(MotionEvent event, float scale) {
        int i;
        int action = event.getActionMasked();
        float x = event.getX();
        float y = this.renderView.getHeight() - event.getY();
        float[] fArr = this.tempPoint;
        fArr[0] = x;
        fArr[1] = y;
        this.invertMatrix.mapPoints(fArr);
        float[] fArr2 = this.tempPoint;
        Point location = new Point(fArr2[0], fArr2[1], 1.0d);
        switch (action) {
            case 0:
            case 2:
                if (!this.beganDrawing) {
                    this.beganDrawing = true;
                    this.hasMoved = false;
                    this.isFirst = true;
                    this.lastLocation = location;
                    this.points[0] = location;
                    this.pointsCount = 1;
                    this.clearBuffer = true;
                    return;
                }
                float distance = location.getDistanceTo(this.lastLocation);
                if (distance < AndroidUtilities.dp(5.0f) / scale) {
                    return;
                }
                if (this.hasMoved) {
                    i = 1;
                } else {
                    this.renderView.onBeganDrawing();
                    i = 1;
                    this.hasMoved = true;
                }
                Point[] pointArr = this.points;
                int i2 = this.pointsCount;
                pointArr[i2] = location;
                int i3 = i2 + i;
                this.pointsCount = i3;
                if (i3 == 3) {
                    this.lastAngle = (float) Math.atan2(pointArr[2].y - this.points[i].y, this.points[2].x - this.points[i].x);
                    smoothenAndPaintPoints(false);
                }
                this.lastLocation = location;
                return;
            case 1:
                if (!this.hasMoved) {
                    if (this.renderView.shouldDraw()) {
                        location.edge = true;
                        paintPath(new Path(location));
                    }
                    reset();
                } else if (this.pointsCount > 0) {
                    smoothenAndPaintPoints(true);
                    Brush brush = this.renderView.getCurrentBrush();
                    if (brush instanceof Brush.Arrow) {
                        float arrowLength = this.renderView.getCurrentWeight() * 4.5f;
                        float angle = this.lastAngle;
                        Point location2 = this.points[this.pointsCount - 1];
                        Point tip = new Point(location2.x, location2.y, 0.800000011920929d);
                        double d = location2.x;
                        double d2 = angle;
                        Double.isNaN(d2);
                        double cos = Math.cos(d2 - 2.356194490192345d);
                        double d3 = arrowLength;
                        Double.isNaN(d3);
                        double d4 = location2.y;
                        double d5 = angle;
                        Double.isNaN(d5);
                        double sin = Math.sin(d5 - 2.5132741228718345d);
                        double d6 = arrowLength;
                        Double.isNaN(d6);
                        Point leftTip = new Point(d + (cos * d3), d4 + (sin * d6), 1.0d);
                        leftTip.edge = true;
                        Path left = new Path(new Point[]{tip, leftTip});
                        paintPath(left);
                        double d7 = location2.x;
                        double d8 = angle;
                        Double.isNaN(d8);
                        double cos2 = Math.cos(d8 + 2.356194490192345d);
                        double d9 = arrowLength;
                        Double.isNaN(d9);
                        double d10 = d7 + (cos2 * d9);
                        double d11 = location2.y;
                        double d12 = angle;
                        Double.isNaN(d12);
                        double sin2 = Math.sin(d12 + 2.5132741228718345d);
                        double d13 = arrowLength;
                        Double.isNaN(d13);
                        Point rightTip = new Point(d10, d11 + (sin2 * d13), 1.0d);
                        rightTip.edge = true;
                        Path right = new Path(new Point[]{tip, rightTip});
                        paintPath(right);
                    }
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
                return;
            case 3:
                this.renderView.getPainting().clearStroke();
                this.pointsCount = 0;
                this.beganDrawing = false;
                return;
            default:
                return;
        }
    }

    private void reset() {
        this.pointsCount = 0;
    }

    private void smoothenAndPaintPoints(boolean ended) {
        int i = this.pointsCount;
        if (i > 2) {
            Vector<Point> points = new Vector<>();
            Point[] pointArr = this.points;
            Point prev2 = pointArr[0];
            Point prev1 = pointArr[1];
            Point cur = pointArr[2];
            if (cur == null || prev1 == null) {
                return;
            }
            if (prev2 == null) {
                return;
            }
            Point midPoint1 = prev1.multiplySum(prev2, 0.5d);
            Point midPoint2 = cur.multiplySum(prev1, 0.5d);
            float distance = midPoint1.getDistanceTo(midPoint2);
            int numberOfSegments = (int) Math.min(48.0d, Math.max(Math.floor(distance / 1), 24.0d));
            float t = 0.0f;
            float step = 1.0f / numberOfSegments;
            for (int j = 0; j < numberOfSegments; j++) {
                Point point = smoothPoint(midPoint1, midPoint2, prev1, t);
                if (this.isFirst) {
                    point.edge = true;
                    this.isFirst = false;
                }
                points.add(point);
                t += step;
            }
            if (ended) {
                midPoint2.edge = true;
            }
            points.add(midPoint2);
            Point[] result = new Point[points.size()];
            points.toArray(result);
            Path path = new Path(result);
            paintPath(path);
            Point[] pointArr2 = this.points;
            System.arraycopy(pointArr2, 1, pointArr2, 0, 2);
            if (ended) {
                this.pointsCount = 0;
                return;
            } else {
                this.pointsCount = 2;
                return;
            }
        }
        Point[] result2 = new Point[i];
        System.arraycopy(this.points, 0, result2, 0, i);
        Path path2 = new Path(result2);
        paintPath(path2);
    }

    private Point smoothPoint(Point midPoint1, Point midPoint2, Point prev1, float t) {
        double a1 = Math.pow(1.0f - t, 2.0d);
        double a2 = (1.0f - t) * 2.0f * t;
        double a3 = t * t;
        double d = prev1.x;
        Double.isNaN(a2);
        double d2 = (midPoint1.x * a1) + (d * a2);
        double d3 = midPoint2.x;
        Double.isNaN(a3);
        double d4 = midPoint1.y * a1;
        double a12 = prev1.y;
        Double.isNaN(a2);
        double d5 = d4 + (a12 * a2);
        double d6 = midPoint2.y;
        Double.isNaN(a3);
        return new Point(d2 + (d3 * a3), d5 + (d6 * a3), 1.0d);
    }

    private void paintPath(final Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Runnable() { // from class: org.telegram.ui.Components.Paint.Input$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                Input.this.m2771lambda$paintPath$1$orgtelegramuiComponentsPaintInput(path);
            }
        });
        this.clearBuffer = false;
    }

    /* renamed from: lambda$paintPath$0$org-telegram-ui-Components-Paint-Input */
    public /* synthetic */ void m2770lambda$paintPath$0$orgtelegramuiComponentsPaintInput(Path path) {
        this.lastRemainder = path.remainder;
    }

    /* renamed from: lambda$paintPath$1$org-telegram-ui-Components-Paint-Input */
    public /* synthetic */ void m2771lambda$paintPath$1$orgtelegramuiComponentsPaintInput(final Path path) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Input$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Input.this.m2770lambda$paintPath$0$orgtelegramuiComponentsPaintInput(path);
            }
        });
    }
}
