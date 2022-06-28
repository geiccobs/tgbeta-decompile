package org.telegram.ui.Components.Paint.Views;

import android.view.MotionEvent;
/* loaded from: classes5.dex */
public class RotationGestureDetector {
    private float angle;
    private float fX;
    private float fY;
    private OnRotationGestureListener mListener;
    private float sX;
    private float sY;
    private float startAngle;

    /* loaded from: classes5.dex */
    public interface OnRotationGestureListener {
        void onRotation(RotationGestureDetector rotationGestureDetector);

        void onRotationBegin(RotationGestureDetector rotationGestureDetector);

        void onRotationEnd(RotationGestureDetector rotationGestureDetector);
    }

    public float getAngle() {
        return this.angle;
    }

    public float getStartAngle() {
        return this.startAngle;
    }

    public RotationGestureDetector(OnRotationGestureListener listener) {
        this.mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() != 2) {
            return false;
        }
        switch (event.getActionMasked()) {
            case 0:
            case 5:
                this.sX = event.getX(0);
                this.sY = event.getY(0);
                this.fX = event.getX(1);
                this.fY = event.getY(1);
                break;
            case 1:
            case 3:
                this.startAngle = Float.NaN;
                break;
            case 2:
                float nsX = event.getX(0);
                float nsY = event.getY(0);
                float nfX = event.getX(1);
                float nfY = event.getY(1);
                this.angle = angleBetweenLines(this.fX, this.fY, this.sX, this.sY, nfX, nfY, nsX, nsY);
                if (this.mListener != null) {
                    if (Float.isNaN(this.startAngle)) {
                        this.startAngle = this.angle;
                        this.mListener.onRotationBegin(this);
                        break;
                    } else {
                        this.mListener.onRotation(this);
                        break;
                    }
                }
                break;
            case 6:
                this.startAngle = Float.NaN;
                OnRotationGestureListener onRotationGestureListener = this.mListener;
                if (onRotationGestureListener != null) {
                    onRotationGestureListener.onRotationEnd(this);
                    break;
                }
                break;
        }
        return true;
    }

    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2(fY - sY, fX - sX);
        float angle2 = (float) Math.atan2(nfY - nsY, nfX - nsX);
        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360.0f;
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        if (angle > 180.0f) {
            return angle - 360.0f;
        }
        return angle;
    }
}
