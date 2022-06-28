package org.telegram.ui.Components.Crop;

import android.graphics.Bitmap;
import android.graphics.Matrix;
/* loaded from: classes5.dex */
public class CropState {
    private float height;
    private float minimumScale;
    private float width;
    private float x = 0.0f;
    private float y = 0.0f;
    private float scale = 1.0f;
    private float rotation = 0.0f;
    private Matrix matrix = new Matrix();
    private float[] values = new float[9];

    public CropState(Bitmap bitmap) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    private void updateValues() {
        this.matrix.getValues(this.values);
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
        this.matrix.postTranslate(x, y);
    }

    public float getX() {
        updateValues();
        return this.values[2];
    }

    public float getY() {
        updateValues();
        return this.values[5];
    }

    public void scale(float s, float pivotX, float pivotY) {
        this.scale *= s;
        this.matrix.postScale(s, s, pivotX, pivotY);
    }

    public float getScale() {
        return this.scale;
    }

    public void rotate(float angle, float pivotX, float pivotY) {
        this.rotation += angle;
        this.matrix.postRotate(angle, pivotX, pivotY);
    }

    public float getRotation() {
        return this.rotation;
    }

    public void getConcatMatrix(Matrix toMatrix) {
        toMatrix.postConcat(this.matrix);
    }

    public Matrix getMatrix() {
        Matrix m = new Matrix();
        m.set(this.matrix);
        return m;
    }
}
