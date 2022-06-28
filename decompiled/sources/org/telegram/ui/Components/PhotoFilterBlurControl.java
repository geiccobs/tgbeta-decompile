package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.BubbleActivity;
/* loaded from: classes5.dex */
public class PhotoFilterBlurControl extends FrameLayout {
    private static final float BlurMinimumDifference = 0.02f;
    private static final float BlurMinimumFalloff = 0.1f;
    private BlurViewActiveControl activeControl;
    private float angle;
    private boolean checkForZooming;
    private PhotoFilterLinearBlurControlDelegate delegate;
    private boolean inBubbleMode;
    private boolean isMoving;
    private boolean isZooming;
    private float pointerStartX;
    private float pointerStartY;
    private float startDistance;
    private float startPointerDistance;
    private float startRadius;
    private int type;
    private static final float BlurInsetProximity = AndroidUtilities.dp(20.0f);
    private static final float BlurViewCenterInset = AndroidUtilities.dp(30.0f);
    private static final float BlurViewRadiusInset = AndroidUtilities.dp(30.0f);
    private final int GestureStateBegan = 1;
    private final int GestureStateChanged = 2;
    private final int GestureStateEnded = 3;
    private final int GestureStateCancelled = 4;
    private final int GestureStateFailed = 5;
    private Point startCenterPoint = new Point();
    private Size actualAreaSize = new Size();
    private Point centerPoint = new Point(0.5f, 0.5f);
    private float falloff = 0.15f;
    private float size = 0.35f;
    private RectF arcRect = new RectF();
    private float pointerScale = 1.0f;
    private boolean checkForMoving = true;
    private Paint paint = new Paint(1);
    private Paint arcPaint = new Paint(1);

    /* loaded from: classes5.dex */
    public enum BlurViewActiveControl {
        BlurViewActiveControlNone,
        BlurViewActiveControlCenter,
        BlurViewActiveControlInnerRadius,
        BlurViewActiveControlOuterRadius,
        BlurViewActiveControlWholeArea,
        BlurViewActiveControlRotation
    }

    /* loaded from: classes5.dex */
    public interface PhotoFilterLinearBlurControlDelegate {
        void valueChanged(Point point, float f, float f2, float f3);
    }

    public PhotoFilterBlurControl(Context context) {
        super(context);
        setWillNotDraw(false);
        this.paint.setColor(-1);
        this.arcPaint.setColor(-1);
        this.arcPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.arcPaint.setStyle(Paint.Style.STROKE);
        this.inBubbleMode = context instanceof BubbleActivity;
    }

    public void setType(int blurType) {
        this.type = blurType;
        invalidate();
    }

    public void setDelegate(PhotoFilterLinearBlurControlDelegate delegate) {
        this.delegate = delegate;
    }

    private float getDistance(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return 0.0f;
        }
        float x1 = event.getX(0);
        float y1 = event.getY(0);
        float x2 = event.getX(1);
        float y2 = event.getY(1);
        return (float) Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }

    private float degreesToRadians(float degrees) {
        return (3.1415927f * degrees) / 180.0f;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case 0:
            case 5:
                if (event.getPointerCount() != 1) {
                    if (this.isMoving) {
                        handlePan(3, event);
                        this.checkForMoving = true;
                        this.isMoving = false;
                    }
                    if (event.getPointerCount() != 2) {
                        handlePinch(3, event);
                        this.checkForZooming = true;
                        this.isZooming = false;
                        return true;
                    } else if (this.checkForZooming && !this.isZooming) {
                        handlePinch(1, event);
                        this.isZooming = true;
                        return true;
                    } else {
                        return true;
                    }
                } else if (!this.checkForMoving || this.isMoving) {
                    return true;
                } else {
                    float locationX = event.getX();
                    float locationY = event.getY();
                    Point centerPoint = getActualCenterPoint();
                    Point delta = new Point(locationX - centerPoint.x, locationY - centerPoint.y);
                    float radialDistance = (float) Math.sqrt((delta.x * delta.x) + (delta.y * delta.y));
                    float innerRadius = getActualInnerRadius();
                    float outerRadius = getActualOuterRadius();
                    boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                    float outerRadiusInnerInset = 0.0f;
                    float innerRadiusOuterInset = close ? 0.0f : BlurViewRadiusInset;
                    if (!close) {
                        outerRadiusInnerInset = BlurViewRadiusInset;
                    }
                    int i = this.type;
                    if (i == 0) {
                        double d = delta.x;
                        double degreesToRadians = degreesToRadians(this.angle);
                        Double.isNaN(degreesToRadians);
                        double cos = Math.cos(degreesToRadians + 1.5707963267948966d);
                        Double.isNaN(d);
                        double d2 = d * cos;
                        double d3 = delta.y;
                        double degreesToRadians2 = degreesToRadians(this.angle);
                        Double.isNaN(degreesToRadians2);
                        double sin = Math.sin(degreesToRadians2 + 1.5707963267948966d);
                        Double.isNaN(d3);
                        float distance = (float) Math.abs(d2 + (d3 * sin));
                        if (radialDistance < BlurViewCenterInset) {
                            this.isMoving = true;
                        } else {
                            float f = BlurViewRadiusInset;
                            if (distance > innerRadius - f && distance < innerRadius + innerRadiusOuterInset) {
                                this.isMoving = true;
                            } else if (distance > outerRadius - outerRadiusInnerInset && distance < outerRadius + f) {
                                this.isMoving = true;
                            } else if (distance <= innerRadius - f || distance >= f + outerRadius) {
                                this.isMoving = true;
                            }
                        }
                    } else if (i == 1) {
                        if (radialDistance < BlurViewCenterInset) {
                            this.isMoving = true;
                        } else {
                            float f2 = BlurViewRadiusInset;
                            if (radialDistance > innerRadius - f2 && radialDistance < innerRadius + innerRadiusOuterInset) {
                                this.isMoving = true;
                            } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < f2 + outerRadius) {
                                this.isMoving = true;
                            }
                        }
                    }
                    this.checkForMoving = false;
                    if (this.isMoving) {
                        handlePan(1, event);
                    }
                    return true;
                }
            case 1:
            case 3:
            case 6:
                if (this.isMoving) {
                    handlePan(3, event);
                    this.isMoving = false;
                } else if (this.isZooming) {
                    handlePinch(3, event);
                    this.isZooming = false;
                }
                this.checkForMoving = true;
                this.checkForZooming = true;
                return true;
            case 2:
                if (this.isMoving) {
                    handlePan(2, event);
                    return true;
                } else if (!this.isZooming) {
                    return true;
                } else {
                    handlePinch(2, event);
                    return true;
                }
            case 4:
            default:
                return true;
        }
    }

    private void handlePan(int state, MotionEvent event) {
        float locationX = event.getX();
        float locationY = event.getY();
        Point actualCenterPoint = getActualCenterPoint();
        float dx = locationX - actualCenterPoint.x;
        float dy = locationY - actualCenterPoint.y;
        float radialDistance = (float) Math.sqrt((dx * dx) + (dy * dy));
        float shorterSide = Math.min(this.actualAreaSize.width, this.actualAreaSize.height);
        float innerRadius = this.falloff * shorterSide;
        float outerRadius = this.size * shorterSide;
        double d = dx;
        double degreesToRadians = degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians);
        double cos = Math.cos(degreesToRadians + 1.5707963267948966d);
        Double.isNaN(d);
        double d2 = d * cos;
        double d3 = dy;
        double degreesToRadians2 = degreesToRadians(this.angle);
        Double.isNaN(degreesToRadians2);
        double sin = Math.sin(degreesToRadians2 + 1.5707963267948966d);
        Double.isNaN(d3);
        float distance = (float) Math.abs(d2 + (d3 * sin));
        switch (state) {
            case 1:
                this.pointerStartX = event.getX();
                this.pointerStartY = event.getY();
                boolean close = Math.abs(outerRadius - innerRadius) < BlurInsetProximity;
                float innerRadiusOuterInset = close ? 0.0f : BlurViewRadiusInset;
                float outerRadiusInnerInset = close ? 0.0f : BlurViewRadiusInset;
                int i = this.type;
                if (i == 0) {
                    if (radialDistance < BlurViewCenterInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        this.startCenterPoint = actualCenterPoint;
                    } else {
                        float f = BlurViewRadiusInset;
                        if (distance > innerRadius - f && distance < innerRadius + innerRadiusOuterInset) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            this.startDistance = distance;
                            this.startRadius = innerRadius;
                        } else if (distance > outerRadius - outerRadiusInnerInset && distance < outerRadius + f) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            this.startDistance = distance;
                            this.startRadius = outerRadius;
                        } else if (distance <= innerRadius - f || distance >= f + outerRadius) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
                        }
                    }
                } else if (i == 1) {
                    if (radialDistance < BlurViewCenterInset) {
                        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
                        this.startCenterPoint = actualCenterPoint;
                    } else {
                        float f2 = BlurViewRadiusInset;
                        if (radialDistance > innerRadius - f2 && radialDistance < innerRadius + innerRadiusOuterInset) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
                            this.startDistance = radialDistance;
                            this.startRadius = innerRadius;
                        } else if (radialDistance > outerRadius - outerRadiusInnerInset && radialDistance < f2 + outerRadius) {
                            this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
                            this.startDistance = radialDistance;
                            this.startRadius = outerRadius;
                        }
                    }
                }
                setSelected(true, true);
                return;
            case 2:
                int i2 = this.type;
                if (i2 == 0) {
                    switch (AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()]) {
                        case 1:
                            float translationX = locationX - this.pointerStartX;
                            float translationY = locationY - this.pointerStartY;
                            Rect actualArea = new Rect((getWidth() - this.actualAreaSize.width) / 2.0f, ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight) + ((getHeight() - this.actualAreaSize.height) / 2.0f), this.actualAreaSize.width, this.actualAreaSize.height);
                            float max = Math.max(actualArea.x, Math.min(actualArea.x + actualArea.width, this.startCenterPoint.x + translationX));
                            float f3 = actualArea.y;
                            float f4 = actualArea.y;
                            float translationX2 = actualArea.height;
                            Point newPoint = new Point(max, Math.max(f3, Math.min(f4 + translationX2, this.startCenterPoint.y + translationY)));
                            this.centerPoint = new Point((newPoint.x - actualArea.x) / this.actualAreaSize.width, ((newPoint.y - actualArea.y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                            break;
                        case 2:
                            float d4 = distance - this.startDistance;
                            this.falloff = Math.min(Math.max(0.1f, (this.startRadius + d4) / shorterSide), this.size - BlurMinimumDifference);
                            break;
                        case 3:
                            float d5 = distance - this.startDistance;
                            this.size = Math.max(this.falloff + BlurMinimumDifference, (this.startRadius + d5) / shorterSide);
                            break;
                        case 4:
                            float translationX3 = locationX - this.pointerStartX;
                            float translationY2 = locationY - this.pointerStartY;
                            int i3 = 0;
                            boolean right = locationX > actualCenterPoint.x;
                            boolean bottom = locationY > actualCenterPoint.y;
                            boolean b = Math.abs(translationY2) > Math.abs(translationX3);
                            if (!right && !bottom) {
                                if (b) {
                                    if (translationY2 < 0.0f) {
                                        i3 = 1;
                                    }
                                } else if (translationX3 > 0.0f) {
                                    i3 = 1;
                                }
                            } else if (right && !bottom) {
                                if (b) {
                                    if (translationY2 > 0.0f) {
                                        i3 = 1;
                                    }
                                } else if (translationX3 > 0.0f) {
                                    i3 = 1;
                                }
                            } else if (right && bottom) {
                                if (b) {
                                    if (translationY2 > 0.0f) {
                                        i3 = 1;
                                    }
                                } else if (translationX3 < 0.0f) {
                                    i3 = 1;
                                }
                            } else if (b) {
                                if (translationY2 < 0.0f) {
                                    i3 = 1;
                                }
                            } else if (translationX3 < 0.0f) {
                                i3 = 1;
                            }
                            float d6 = (float) Math.sqrt((translationX3 * translationX3) + (translationY2 * translationY2));
                            this.angle += ((((i3 * 2) - 1) * d6) / 3.1415927f) / 1.15f;
                            this.pointerStartX = locationX;
                            this.pointerStartY = locationY;
                            break;
                    }
                } else if (i2 == 1) {
                    switch (AnonymousClass1.$SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()]) {
                        case 1:
                            float translationX4 = locationX - this.pointerStartX;
                            float translationY3 = locationY - this.pointerStartY;
                            Rect actualArea2 = new Rect((getWidth() - this.actualAreaSize.width) / 2.0f, ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight) + ((getHeight() - this.actualAreaSize.height) / 2.0f), this.actualAreaSize.width, this.actualAreaSize.height);
                            Point newPoint2 = new Point(Math.max(actualArea2.x, Math.min(actualArea2.x + actualArea2.width, this.startCenterPoint.x + translationX4)), Math.max(actualArea2.y, Math.min(actualArea2.y + actualArea2.height, this.startCenterPoint.y + translationY3)));
                            this.centerPoint = new Point((newPoint2.x - actualArea2.x) / this.actualAreaSize.width, ((newPoint2.y - actualArea2.y) + ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) / this.actualAreaSize.width);
                            break;
                        case 2:
                            float d7 = radialDistance - this.startDistance;
                            this.falloff = Math.min(Math.max(0.1f, (this.startRadius + d7) / shorterSide), this.size - BlurMinimumDifference);
                            break;
                        case 3:
                            float d8 = radialDistance - this.startDistance;
                            this.size = Math.max(this.falloff + BlurMinimumDifference, (this.startRadius + d8) / shorterSide);
                            break;
                    }
                }
                invalidate();
                PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
                if (photoFilterLinearBlurControlDelegate != null) {
                    photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
                    return;
                }
                return;
            case 3:
            case 4:
            case 5:
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            default:
                return;
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoFilterBlurControl$1 */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl;

        static {
            int[] iArr = new int[BlurViewActiveControl.values().length];
            $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl = iArr;
            try {
                iArr[BlurViewActiveControl.BlurViewActiveControlCenter.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlInnerRadius.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlOuterRadius.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[BlurViewActiveControl.BlurViewActiveControlRotation.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private void handlePinch(int state, MotionEvent event) {
        switch (state) {
            case 1:
                this.startPointerDistance = getDistance(event);
                this.pointerScale = 1.0f;
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlWholeArea;
                setSelected(true, true);
                break;
            case 2:
                break;
            case 3:
            case 4:
            case 5:
                this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
                setSelected(false, true);
                return;
            default:
                return;
        }
        float newDistance = getDistance(event);
        float f = this.pointerScale + (((newDistance - this.startPointerDistance) / AndroidUtilities.density) * 0.01f);
        this.pointerScale = f;
        float max = Math.max(0.1f, this.falloff * f);
        this.falloff = max;
        this.size = Math.max(max + BlurMinimumDifference, this.size * this.pointerScale);
        this.pointerScale = 1.0f;
        this.startPointerDistance = newDistance;
        invalidate();
        PhotoFilterLinearBlurControlDelegate photoFilterLinearBlurControlDelegate = this.delegate;
        if (photoFilterLinearBlurControlDelegate != null) {
            photoFilterLinearBlurControlDelegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.5707964f);
        }
    }

    private void setSelected(boolean selected, boolean animated) {
    }

    public void setActualAreaSize(float width, float height) {
        this.actualAreaSize.width = width;
        this.actualAreaSize.height = height;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point centerPoint = getActualCenterPoint();
        float innerRadius = getActualInnerRadius();
        float outerRadius = getActualOuterRadius();
        canvas.translate(centerPoint.x, centerPoint.y);
        int i = this.type;
        if (i == 0) {
            canvas.rotate(this.angle);
            float space = AndroidUtilities.dp(6.0f);
            float length = AndroidUtilities.dp(12.0f);
            float thickness = AndroidUtilities.dp(1.5f);
            int i2 = 0;
            while (i2 < 30) {
                int i3 = i2;
                canvas.drawRect((length + space) * i2, -innerRadius, (i2 * (length + space)) + length, thickness - innerRadius, this.paint);
                float left = (((-i3) * (length + space)) - space) - length;
                float right = ((-i3) * (length + space)) - space;
                canvas.drawRect(left, -innerRadius, right, thickness - innerRadius, this.paint);
                canvas.drawRect((length + space) * i3, innerRadius, length + (i3 * (length + space)), thickness + innerRadius, this.paint);
                canvas.drawRect(left, innerRadius, right, thickness + innerRadius, this.paint);
                i2 = i3 + 1;
            }
            float length2 = AndroidUtilities.dp(6.0f);
            for (int i4 = 0; i4 < 64; i4++) {
                canvas.drawRect((length2 + space) * i4, -outerRadius, length2 + (i4 * (length2 + space)), thickness - outerRadius, this.paint);
                float left2 = (((-i4) * (length2 + space)) - space) - length2;
                float right2 = ((-i4) * (length2 + space)) - space;
                canvas.drawRect(left2, -outerRadius, right2, thickness - outerRadius, this.paint);
                canvas.drawRect((length2 + space) * i4, outerRadius, length2 + (i4 * (length2 + space)), thickness + outerRadius, this.paint);
                canvas.drawRect(left2, outerRadius, right2, thickness + outerRadius, this.paint);
            }
        } else if (i == 1) {
            this.arcRect.set(-innerRadius, -innerRadius, innerRadius, innerRadius);
            for (int i5 = 0; i5 < 22; i5++) {
                canvas.drawArc(this.arcRect, (6.15f + 10.2f) * i5, 10.2f, false, this.arcPaint);
            }
            this.arcRect.set(-outerRadius, -outerRadius, outerRadius, outerRadius);
            for (int i6 = 0; i6 < 64; i6++) {
                canvas.drawArc(this.arcRect, (2.02f + 3.6f) * i6, 3.6f, false, this.arcPaint);
            }
        }
        canvas.drawCircle(0.0f, 0.0f, AndroidUtilities.dp(8.0f), this.paint);
    }

    private Point getActualCenterPoint() {
        return new Point(((getWidth() - this.actualAreaSize.width) / 2.0f) + (this.centerPoint.x * this.actualAreaSize.width), ((((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight) + ((getHeight() - this.actualAreaSize.height) / 2.0f)) - ((this.actualAreaSize.width - this.actualAreaSize.height) / 2.0f)) + (this.centerPoint.y * this.actualAreaSize.width));
    }

    private float getActualInnerRadius() {
        return Math.min(this.actualAreaSize.width, this.actualAreaSize.height) * this.falloff;
    }

    private float getActualOuterRadius() {
        return Math.min(this.actualAreaSize.width, this.actualAreaSize.height) * this.size;
    }
}
