package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.PointF;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.util.List;
import org.telegram.ui.Components.Size;
/* loaded from: classes5.dex */
public class PhotoFace {
    private float angle;
    private org.telegram.ui.Components.Point chinPoint;
    private org.telegram.ui.Components.Point eyesCenterPoint;
    private float eyesDistance;
    private org.telegram.ui.Components.Point foreheadPoint;
    private org.telegram.ui.Components.Point mouthPoint;
    private float width;

    public PhotoFace(Face face, Bitmap sourceBitmap, Size targetSize, boolean sideward) {
        List<Landmark> landmarks = face.getLandmarks();
        org.telegram.ui.Components.Point leftEyePoint = null;
        org.telegram.ui.Components.Point rightEyePoint = null;
        org.telegram.ui.Components.Point leftMouthPoint = null;
        org.telegram.ui.Components.Point rightMouthPoint = null;
        for (Landmark landmark : landmarks) {
            PointF point = landmark.getPosition();
            switch (landmark.getType()) {
                case 4:
                    leftEyePoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                case 5:
                    leftMouthPoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                case 10:
                    rightEyePoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                case 11:
                    rightMouthPoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
            }
        }
        if (leftEyePoint != null && rightEyePoint != null) {
            if (leftEyePoint.x < rightEyePoint.x) {
                org.telegram.ui.Components.Point temp = leftEyePoint;
                leftEyePoint = rightEyePoint;
                rightEyePoint = temp;
            }
            this.eyesCenterPoint = new org.telegram.ui.Components.Point((leftEyePoint.x * 0.5f) + (rightEyePoint.x * 0.5f), (leftEyePoint.y * 0.5f) + (rightEyePoint.y * 0.5f));
            this.eyesDistance = (float) Math.hypot(rightEyePoint.x - leftEyePoint.x, rightEyePoint.y - leftEyePoint.y);
            float degrees = (float) Math.toDegrees(Math.atan2(rightEyePoint.y - leftEyePoint.y, rightEyePoint.x - leftEyePoint.x) + 3.141592653589793d);
            this.angle = degrees;
            float f = this.eyesDistance;
            this.width = 2.35f * f;
            float foreheadHeight = f * 0.8f;
            float upAngle = (float) Math.toRadians(degrees - 90.0f);
            this.foreheadPoint = new org.telegram.ui.Components.Point(this.eyesCenterPoint.x + (((float) Math.cos(upAngle)) * foreheadHeight), this.eyesCenterPoint.y + (((float) Math.sin(upAngle)) * foreheadHeight));
        }
        if (leftMouthPoint != null && rightMouthPoint != null) {
            if (leftMouthPoint.x < rightMouthPoint.x) {
                org.telegram.ui.Components.Point temp2 = leftMouthPoint;
                leftMouthPoint = rightMouthPoint;
                rightMouthPoint = temp2;
            }
            this.mouthPoint = new org.telegram.ui.Components.Point((leftMouthPoint.x * 0.5f) + (rightMouthPoint.x * 0.5f), (leftMouthPoint.y * 0.5f) + (rightMouthPoint.y * 0.5f));
            float chinDepth = this.eyesDistance * 0.7f;
            float downAngle = (float) Math.toRadians(this.angle + 90.0f);
            this.chinPoint = new org.telegram.ui.Components.Point(this.mouthPoint.x + (((float) Math.cos(downAngle)) * chinDepth), this.mouthPoint.y + (((float) Math.sin(downAngle)) * chinDepth));
        }
    }

    public boolean isSufficient() {
        return this.eyesCenterPoint != null;
    }

    private org.telegram.ui.Components.Point transposePoint(PointF point, Bitmap sourceBitmap, Size targetSize, boolean sideward) {
        float bitmapW = sideward ? sourceBitmap.getHeight() : sourceBitmap.getWidth();
        float bitmapH = sideward ? sourceBitmap.getWidth() : sourceBitmap.getHeight();
        return new org.telegram.ui.Components.Point((targetSize.width * point.x) / bitmapW, (targetSize.height * point.y) / bitmapH);
    }

    public org.telegram.ui.Components.Point getPointForAnchor(int anchor) {
        switch (anchor) {
            case 0:
                return this.foreheadPoint;
            case 1:
                return this.eyesCenterPoint;
            case 2:
                return this.mouthPoint;
            case 3:
                return this.chinPoint;
            default:
                return null;
        }
    }

    public float getWidthForAnchor(int anchor) {
        if (anchor == 1) {
            return this.eyesDistance;
        }
        return this.width;
    }

    public float getAngle() {
        return this.angle;
    }
}
