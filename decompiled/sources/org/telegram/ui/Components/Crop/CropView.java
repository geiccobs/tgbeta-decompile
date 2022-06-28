package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.Crop.CropGestureDetector;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.VideoEditTextureView;
/* loaded from: classes5.dex */
public class CropView extends FrameLayout implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener {
    private static final float EPSILON = 1.0E-5f;
    private static final float MAX_SCALE = 30.0f;
    private static final int RESULT_SIDE = 1280;
    public CropAreaView areaView;
    private Bitmap bitmap;
    private int bitmapRotation;
    private float bottomPadding;
    private CropTransform cropTransform;
    private CropGestureDetector detector;
    private boolean freeform;
    private boolean hasAspectRatioDialog;
    private ImageView imageView;
    private boolean inBubbleMode;
    private boolean isVisible;
    private CropViewListener listener;
    private PaintingOverlay paintingOverlay;
    private float rotationStartScale;
    public CropState state;
    private VideoEditTextureView videoEditTextureView;
    float[] values = new float[9];
    RectF cropRect = new RectF();
    RectF sizeRect = new RectF(0.0f, 0.0f, 1280.0f, 1280.0f);
    private RectF previousAreaRect = new RectF();
    private RectF initialAreaRect = new RectF();
    private Matrix overlayMatrix = new Matrix();
    private CropRectangle tempRect = new CropRectangle();
    private Matrix tempMatrix = new Matrix();
    private boolean animating = false;

    /* loaded from: classes5.dex */
    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();
    }

    /* loaded from: classes5.dex */
    public class CropState {
        public float baseRotation;
        public float height;
        public Matrix matrix;
        public float minimumScale;
        public boolean mirrored;
        public float orientation;
        public float rotation;
        public float scale;
        public float width;
        public float x;
        public float y;

        private CropState(int w, int h, int bRotation) {
            CropView.this = this$0;
            this.width = w;
            this.height = h;
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = bRotation;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        public void update(int w, int h, int rotation) {
            float ps = this.width / w;
            this.scale *= ps;
            this.width = w;
            this.height = h;
            updateMinimumScale();
            this.matrix.getValues(CropView.this.values);
            this.matrix.reset();
            Matrix matrix = this.matrix;
            float f = this.scale;
            matrix.postScale(f, f);
            this.matrix.postTranslate(CropView.this.values[2], CropView.this.values[5]);
            CropView.this.updateMatrix();
        }

        public boolean hasChanges() {
            return Math.abs(this.x) > CropView.EPSILON || Math.abs(this.y) > CropView.EPSILON || Math.abs(this.scale - this.minimumScale) > CropView.EPSILON || Math.abs(this.rotation) > CropView.EPSILON || Math.abs(this.orientation) > CropView.EPSILON;
        }

        public float getWidth() {
            return this.width;
        }

        public float getHeight() {
            return this.height;
        }

        public float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        public float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        public void translate(float x, float y) {
            this.x += x;
            this.y += y;
            this.matrix.postTranslate(x, y);
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }

        private void setScale(float s, float pivotX, float pivotY) {
            this.scale = s;
            this.matrix.reset();
            this.matrix.setScale(s, s, pivotX, pivotY);
        }

        public void scale(float s, float pivotX, float pivotY) {
            this.scale *= s;
            this.matrix.postScale(s, s, pivotX, pivotY);
        }

        public float getScale() {
            return this.scale;
        }

        private float getMinimumScale() {
            return this.minimumScale;
        }

        public void rotate(float angle, float pivotX, float pivotY) {
            this.rotation += angle;
            this.matrix.postRotate(angle, pivotX, pivotY);
        }

        public float getRotation() {
            return this.rotation;
        }

        private boolean isMirrored() {
            return this.mirrored;
        }

        public float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        public int getOrientationOnly() {
            return (int) this.orientation;
        }

        public float getBaseRotation() {
            return this.baseRotation;
        }

        public void mirror() {
            this.mirrored = !this.mirrored;
        }

        public void reset(CropAreaView areaView, float orient, boolean freeform) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = orient;
            updateMinimumScale();
            float f = this.minimumScale;
            this.scale = f;
            this.matrix.postScale(f, f);
        }

        private void rotateToOrientation(float orientation) {
            Matrix matrix = this.matrix;
            float f = this.scale;
            matrix.postScale(1.0f / f, 1.0f / f);
            this.orientation = orientation;
            float wasMinimumScale = this.minimumScale;
            updateMinimumScale();
            float f2 = (this.scale / wasMinimumScale) * this.minimumScale;
            this.scale = f2;
            this.matrix.postScale(f2, f2);
        }

        private void updateMinimumScale() {
            float f = this.orientation;
            float f2 = this.baseRotation;
            float w = (f + f2) % 180.0f != 0.0f ? this.height : this.width;
            float h = (f + f2) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / w;
                return;
            }
            float wScale = CropView.this.areaView.getCropWidth() / w;
            float hScale = CropView.this.areaView.getCropHeight() / h;
            this.minimumScale = Math.max(wScale, hScale);
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

    public float getStateOrientation() {
        return this.state.orientation;
    }

    public float getStateFullOrientation() {
        return this.state.baseRotation + this.state.orientation;
    }

    public boolean getStateMirror() {
        return this.state.mirrored;
    }

    public CropView(Context context) {
        super(context);
        this.inBubbleMode = context instanceof BubbleActivity;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        CropAreaView cropAreaView = new CropAreaView(context);
        this.areaView = cropAreaView;
        cropAreaView.setListener(this);
        addView(this.areaView);
    }

    public boolean isReady() {
        return !this.detector.isScaling() && !this.detector.isDragging() && !this.areaView.isDragging();
    }

    public void setListener(CropViewListener l) {
        this.listener = l;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
        this.areaView.setBottomPadding(value);
    }

    public void setAspectRatio(float ratio) {
        this.areaView.setActualRect(ratio);
    }

    public void setBitmap(Bitmap b, int rotation, boolean fform, boolean same, PaintingOverlay overlay, CropTransform transform, VideoEditTextureView videoView, final MediaController.CropState restoreState) {
        this.freeform = fform;
        this.paintingOverlay = overlay;
        this.videoEditTextureView = videoView;
        this.cropTransform = transform;
        this.bitmapRotation = rotation;
        this.bitmap = b;
        this.areaView.setIsVideo(videoView != null);
        if (b == null && videoView == null) {
            this.state = null;
            this.imageView.setImageDrawable(null);
            return;
        }
        final int w = getCurrentWidth();
        final int h = getCurrentHeight();
        CropState cropState = this.state;
        if (cropState != null && same) {
            cropState.update(w, h, rotation);
        } else {
            this.state = new CropState(w, h, 0);
            this.areaView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.Crop.CropView.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    int rotatedH;
                    int rotatedW;
                    float stateHeight;
                    float stateWidth;
                    CropView.this.reset();
                    MediaController.CropState cropState2 = restoreState;
                    if (cropState2 != null) {
                        boolean z = true;
                        if (cropState2.lockedAspectRatio > 1.0E-4f) {
                            CropView.this.areaView.setLockedAspectRatio(restoreState.lockedAspectRatio);
                            if (CropView.this.listener != null) {
                                CropView.this.listener.onAspectLock(true);
                            }
                        }
                        CropView.this.setFreeform(restoreState.freeform);
                        float aspect = CropView.this.areaView.getAspectRatio();
                        if (restoreState.transformRotation == 90 || restoreState.transformRotation == 270) {
                            aspect = 1.0f / aspect;
                            stateWidth = CropView.this.state.height;
                            stateHeight = CropView.this.state.width;
                            rotatedW = h;
                            rotatedH = w;
                        } else {
                            stateWidth = CropView.this.state.width;
                            stateHeight = CropView.this.state.height;
                            rotatedW = w;
                            rotatedH = h;
                        }
                        int orientation = restoreState.transformRotation;
                        boolean fform2 = CropView.this.freeform;
                        if (CropView.this.freeform && CropView.this.areaView.getLockAspectRatio() > 0.0f) {
                            CropView.this.areaView.setLockedAspectRatio(1.0f / CropView.this.areaView.getLockAspectRatio());
                            CropView.this.areaView.setActualRect(CropView.this.areaView.getLockAspectRatio());
                            fform2 = false;
                        } else {
                            CropAreaView cropAreaView = CropView.this.areaView;
                            int currentWidth = CropView.this.getCurrentWidth();
                            int currentHeight = CropView.this.getCurrentHeight();
                            if ((orientation + CropView.this.state.getBaseRotation()) % 180.0f == 0.0f) {
                                z = false;
                            }
                            cropAreaView.setBitmap(currentWidth, currentHeight, z, CropView.this.freeform);
                        }
                        CropView.this.state.reset(CropView.this.areaView, orientation, fform2);
                        CropView.this.areaView.setActualRect((restoreState.cropPw * aspect) / restoreState.cropPh);
                        CropView.this.state.mirrored = restoreState.mirrored;
                        CropView.this.state.rotate(restoreState.cropRotate, 0.0f, 0.0f);
                        CropView.this.state.translate(restoreState.cropPx * rotatedW * CropView.this.state.minimumScale, restoreState.cropPy * rotatedH * CropView.this.state.minimumScale);
                        float ts = Math.max(CropView.this.areaView.getCropWidth() / stateWidth, CropView.this.areaView.getCropHeight() / stateHeight) / CropView.this.state.minimumScale;
                        CropView.this.state.scale(restoreState.cropScale * ts, 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                        if (CropView.this.listener != null) {
                            CropView.this.listener.onChange(false);
                        }
                    }
                    CropView.this.areaView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
        this.imageView.setImageBitmap(videoView == null ? this.bitmap : null);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true, false);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void setFreeform(boolean fform) {
        this.areaView.setFreeform(fform);
        this.freeform = fform;
    }

    public void onShow() {
        this.isVisible = true;
    }

    public void onHide() {
        this.videoEditTextureView = null;
        this.paintingOverlay = null;
        this.isVisible = false;
    }

    public void show() {
        updateCropTransform();
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true, true);
        this.areaView.invalidate();
    }

    public void hide() {
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false, false);
        this.areaView.invalidate();
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean force) {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
        this.state.mirrored = false;
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix(force);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public void updateMatrix() {
        updateMatrix(false);
    }

    public void updateMatrix(boolean force) {
        this.overlayMatrix.reset();
        if (this.state.getBaseRotation() == 90.0f || this.state.getBaseRotation() == 270.0f) {
            this.overlayMatrix.postTranslate((-this.state.getHeight()) / 2.0f, (-this.state.getWidth()) / 2.0f);
        } else {
            this.overlayMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        }
        this.overlayMatrix.postRotate(this.state.getOrientationOnly());
        this.state.getConcatMatrix(this.overlayMatrix);
        this.overlayMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        if (!this.freeform || this.isVisible || force) {
            updateCropTransform();
            this.listener.onUpdate();
        }
        invalidate();
    }

    private void fillAreaView(RectF targetRect, boolean allowZoomOut) {
        boolean ensureFit;
        float scale;
        int i = 0;
        final float[] currentScale = {1.0f};
        float scale2 = Math.max(targetRect.width() / this.areaView.getCropWidth(), targetRect.height() / this.areaView.getCropHeight());
        float newScale = this.state.getScale() * scale2;
        if (newScale > 30.0f) {
            scale = 30.0f / this.state.getScale();
            ensureFit = true;
        } else {
            scale = scale2;
            ensureFit = false;
        }
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            i = AndroidUtilities.statusBarHeight;
        }
        float statusBarHeight = i;
        final float x = ((targetRect.centerX() - (this.imageView.getWidth() / 2)) / this.areaView.getCropWidth()) * this.state.getOrientedWidth();
        final float y = ((targetRect.centerY() - (((this.imageView.getHeight() - this.bottomPadding) + statusBarHeight) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        final float targetScale = scale;
        final boolean animEnsureFit = ensureFit;
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Crop.CropView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.m2548lambda$fillAreaView$0$orgtelegramuiComponentsCropCropView(targetScale, currentScale, x, y, valueAnimator);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Crop.CropView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (animEnsureFit) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(targetRect, animator, true);
        this.initialAreaRect.set(targetRect);
    }

    /* renamed from: lambda$fillAreaView$0$org-telegram-ui-Components-Crop-CropView */
    public /* synthetic */ void m2548lambda$fillAreaView$0$orgtelegramuiComponentsCropCropView(float targetScale, float[] currentScale, float x, float y, ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        float deltaScale = (((targetScale - 1.0f) * value) + 1.0f) / currentScale[0];
        currentScale[0] = currentScale[0] * deltaScale;
        this.state.scale(deltaScale, x, y);
        updateMatrix();
    }

    private float fitScale(RectF contentRect, float scale, float ratio) {
        float scaledW = contentRect.width() * ratio;
        float scaledH = contentRect.height() * ratio;
        float scaledX = (contentRect.width() - scaledW) / 2.0f;
        float scaledY = (contentRect.height() - scaledH) / 2.0f;
        contentRect.set(contentRect.left + scaledX, contentRect.top + scaledY, contentRect.left + scaledX + scaledW, contentRect.top + scaledY + scaledH);
        return scale * ratio;
    }

    private void fitTranslation(RectF contentRect, RectF boundsRect, PointF translation, float radians) {
        float frameLeft = boundsRect.left;
        float frameTop = boundsRect.top;
        float frameRight = boundsRect.right;
        float frameBottom = boundsRect.bottom;
        if (contentRect.left > frameLeft) {
            frameRight += contentRect.left - frameLeft;
            frameLeft = contentRect.left;
        }
        if (contentRect.top > frameTop) {
            frameBottom += contentRect.top - frameTop;
            frameTop = contentRect.top;
        }
        if (contentRect.right < frameRight) {
            frameLeft += contentRect.right - frameRight;
        }
        if (contentRect.bottom < frameBottom) {
            frameTop += contentRect.bottom - frameBottom;
        }
        float deltaX = boundsRect.centerX() - ((boundsRect.width() / 2.0f) + frameLeft);
        float deltaY = boundsRect.centerY() - ((boundsRect.height() / 2.0f) + frameTop);
        double d = radians;
        Double.isNaN(d);
        double sin = Math.sin(1.5707963267948966d - d);
        double d2 = deltaX;
        Double.isNaN(d2);
        float xCompX = (float) (sin * d2);
        double d3 = radians;
        Double.isNaN(d3);
        double cos = Math.cos(1.5707963267948966d - d3);
        double d4 = deltaX;
        Double.isNaN(d4);
        float xCompY = (float) (cos * d4);
        double d5 = radians;
        Double.isNaN(d5);
        double cos2 = Math.cos(d5 + 1.5707963267948966d);
        double d6 = deltaY;
        Double.isNaN(d6);
        float yCompX = (float) (cos2 * d6);
        double d7 = radians;
        Double.isNaN(d7);
        double sin2 = Math.sin(d7 + 1.5707963267948966d);
        double d8 = deltaY;
        Double.isNaN(d8);
        float yCompY = (float) (sin2 * d8);
        translation.set(translation.x + xCompX + yCompX, translation.y + xCompY + yCompY);
    }

    public RectF calculateBoundingBox(float w, float h, float rotation) {
        RectF result = new RectF(0.0f, 0.0f, w, h);
        Matrix m = new Matrix();
        m.postRotate(rotation, w / 2.0f, h / 2.0f);
        m.mapRect(result);
        return result;
    }

    public float scaleWidthToMaxSize(RectF sizeRect, RectF maxSizeRect) {
        float w = maxSizeRect.width();
        float h = (float) Math.floor((sizeRect.height() * w) / sizeRect.width());
        if (h > maxSizeRect.height()) {
            float h2 = maxSizeRect.height();
            return (float) Math.floor((sizeRect.width() * h2) / sizeRect.height());
        }
        return w;
    }

    /* loaded from: classes5.dex */
    public static class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        void setRect(RectF rect) {
            this.coords[0] = rect.left;
            this.coords[1] = rect.top;
            this.coords[2] = rect.right;
            this.coords[3] = rect.top;
            this.coords[4] = rect.right;
            this.coords[5] = rect.bottom;
            this.coords[6] = rect.left;
            this.coords[7] = rect.bottom;
        }

        void applyMatrix(Matrix m) {
            m.mapPoints(this.coords);
        }

        void getRect(RectF rect) {
            float[] fArr = this.coords;
            rect.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    public void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated) {
        fitContentInBounds(allowScale, maximize, animated, false);
    }

    public void fitContentInBounds(final boolean allowScale, final boolean maximize, final boolean animated, final boolean fast) {
        float targetScale;
        if (this.state == null) {
            return;
        }
        float boundsW = this.areaView.getCropWidth();
        float boundsH = this.areaView.getCropHeight();
        float contentW = this.state.getOrientedWidth();
        float contentH = this.state.getOrientedHeight();
        float rotation = this.state.getRotation();
        float radians = (float) Math.toRadians(rotation);
        RectF boundsRect = calculateBoundingBox(boundsW, boundsH, rotation);
        RectF contentRect = new RectF(0.0f, 0.0f, contentW, contentH);
        float initialX = (boundsW - contentW) / 2.0f;
        float initialY = (boundsH - contentH) / 2.0f;
        float scale = this.state.getScale();
        this.tempRect.setRect(contentRect);
        Matrix matrix = this.state.getMatrix();
        matrix.preTranslate(initialX / scale, initialY / scale);
        this.tempMatrix.reset();
        this.tempMatrix.setTranslate(contentRect.centerX(), contentRect.centerY());
        Matrix matrix2 = this.tempMatrix;
        matrix2.setConcat(matrix2, matrix);
        this.tempMatrix.preTranslate(-contentRect.centerX(), -contentRect.centerY());
        this.tempRect.applyMatrix(this.tempMatrix);
        this.tempMatrix.reset();
        this.tempMatrix.preRotate(-rotation, contentW / 2.0f, contentH / 2.0f);
        this.tempRect.applyMatrix(this.tempMatrix);
        this.tempRect.getRect(contentRect);
        PointF targetTranslation = new PointF(this.state.getX(), this.state.getY());
        float targetScale2 = scale;
        if (!contentRect.contains(boundsRect)) {
            if (allowScale && (boundsRect.width() > contentRect.width() || boundsRect.height() > contentRect.height())) {
                targetScale2 = fitScale(contentRect, scale, boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect));
            }
            fitTranslation(contentRect, boundsRect, targetTranslation, radians);
            targetScale = targetScale2;
        } else if (maximize && this.rotationStartScale > 0.0f) {
            float ratio = boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect);
            float newScale = this.state.getScale() * ratio;
            if (newScale < this.rotationStartScale) {
                ratio = 1.0f;
            }
            float targetScale3 = fitScale(contentRect, scale, ratio);
            fitTranslation(contentRect, boundsRect, targetTranslation, radians);
            targetScale = targetScale3;
        } else {
            targetScale = targetScale2;
        }
        final float dx = targetTranslation.x - this.state.getX();
        final float dy = targetTranslation.y - this.state.getY();
        if (!animated) {
            this.state.translate(dx, dy);
            this.state.scale(targetScale / scale, 0.0f, 0.0f);
            updateMatrix();
            return;
        }
        final float animScale = targetScale / scale;
        if (Math.abs(animScale - 1.0f) >= EPSILON || Math.abs(dx) >= EPSILON || Math.abs(dy) >= EPSILON) {
            this.animating = true;
            final float[] currentValues = {1.0f, 0.0f, 0.0f};
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Crop.CropView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CropView.this.m2549x3055c289(dx, currentValues, dy, animScale, valueAnimator);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Crop.CropView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CropView.this.animating = false;
                    if (!fast) {
                        CropView.this.fitContentInBounds(allowScale, maximize, animated, true);
                    }
                }
            });
            animator.setInterpolator(this.areaView.getInterpolator());
            animator.setDuration(fast ? 100L : 200L);
            animator.start();
        }
    }

    /* renamed from: lambda$fitContentInBounds$1$org-telegram-ui-Components-Crop-CropView */
    public /* synthetic */ void m2549x3055c289(float animDX, float[] currentValues, float animDY, float animScale, ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        float deltaX = (animDX * value) - currentValues[1];
        currentValues[1] = currentValues[1] + deltaX;
        float deltaY = (animDY * value) - currentValues[2];
        currentValues[2] = currentValues[2] + deltaY;
        this.state.translate(currentValues[0] * deltaX, currentValues[0] * deltaY);
        float deltaScale = (((animScale - 1.0f) * value) + 1.0f) / currentValues[0];
        currentValues[0] = currentValues[0] * deltaScale;
        this.state.scale(deltaScale, 0.0f, 0.0f);
        updateMatrix();
    }

    public int getCurrentWidth() {
        VideoEditTextureView videoEditTextureView = this.videoEditTextureView;
        if (videoEditTextureView != null) {
            return videoEditTextureView.getVideoWidth();
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? this.bitmap.getHeight() : this.bitmap.getWidth();
    }

    public int getCurrentHeight() {
        VideoEditTextureView videoEditTextureView = this.videoEditTextureView;
        if (videoEditTextureView != null) {
            return videoEditTextureView.getVideoHeight();
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? this.bitmap.getWidth() : this.bitmap.getHeight();
    }

    public boolean isMirrored() {
        CropState cropState = this.state;
        if (cropState == null) {
            return false;
        }
        return cropState.mirrored;
    }

    public boolean mirror() {
        CropState cropState = this.state;
        boolean z = false;
        if (cropState == null) {
            return false;
        }
        cropState.mirror();
        updateMatrix();
        if (this.listener != null) {
            float orientation = (this.state.getOrientation() - this.state.getBaseRotation()) % 360.0f;
            CropViewListener cropViewListener = this.listener;
            if (!this.state.hasChanges() && orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored) {
                z = true;
            }
            cropViewListener.onChange(z);
        }
        return this.state.mirrored;
    }

    public void maximize(boolean animated) {
        float aspectRatio;
        float aspectRatio2;
        final float toScale = this.state.minimumScale;
        this.areaView.resetAnimator();
        if (this.state.getOrientation() % 180.0f != 0.0f) {
            aspectRatio = getCurrentHeight() / getCurrentWidth();
        } else {
            aspectRatio = getCurrentWidth() / getCurrentHeight();
        }
        if (this.freeform) {
            aspectRatio2 = aspectRatio;
        } else {
            aspectRatio2 = 1.0f;
        }
        this.areaView.calculateRect(this.initialAreaRect, aspectRatio2);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        resetRotationStartScale();
        if (!animated) {
            this.areaView.setActualRect(this.initialAreaRect);
            CropState cropState = this.state;
            cropState.translate(-cropState.x, -this.state.y);
            CropState cropState2 = this.state;
            cropState2.scale(cropState2.minimumScale / this.state.scale, 0.0f, 0.0f);
            CropState cropState3 = this.state;
            cropState3.rotate(-cropState3.rotation, 0.0f, 0.0f);
            updateMatrix();
            resetRotationStartScale();
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        final RectF fromActualRect = new RectF();
        final RectF animatedRect = new RectF();
        this.areaView.getCropRect(fromActualRect);
        final float fromX = this.state.x;
        final float fromY = this.state.y;
        final float fromScale = this.state.scale;
        final float fromRot = this.state.rotation;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Crop.CropView$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.m2550lambda$maximize$2$orgtelegramuiComponentsCropCropView(fromActualRect, animatedRect, fromX, fromY, fromRot, fromScale, toScale, valueAnimator);
            }
        });
        animator.setInterpolator(this.areaView.getInterpolator());
        animator.setDuration(250L);
        animator.start();
    }

    /* renamed from: lambda$maximize$2$org-telegram-ui-Components-Crop-CropView */
    public /* synthetic */ void m2550lambda$maximize$2$orgtelegramuiComponentsCropCropView(RectF fromActualRect, RectF animatedRect, float fromX, float fromY, float fromRot, float fromScale, float toScale, ValueAnimator animation) {
        float t = ((Float) animation.getAnimatedValue()).floatValue();
        AndroidUtilities.lerp(fromActualRect, this.initialAreaRect, t, animatedRect);
        this.areaView.setActualRect(animatedRect);
        float dx = this.state.x - ((1.0f - t) * fromX);
        float dy = this.state.y - ((1.0f - t) * fromY);
        float dr = this.state.rotation - ((1.0f - t) * fromRot);
        float ds = AndroidUtilities.lerp(fromScale, toScale, t) / this.state.scale;
        this.state.translate(-dx, -dy);
        this.state.scale(ds, 0.0f, 0.0f);
        this.state.rotate(-dr, 0.0f, 0.0f);
        fitContentInBounds(true, false, false);
    }

    public boolean rotate(float angle) {
        if (this.state == null) {
            return false;
        }
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float orientation = ((this.state.getOrientation() - this.state.getBaseRotation()) + angle) % 360.0f;
        boolean fform = this.freeform;
        if (!this.freeform || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), (this.state.getBaseRotation() + orientation) % 180.0f != 0.0f, this.freeform);
        } else {
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
            CropAreaView cropAreaView2 = this.areaView;
            cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
            fform = false;
        }
        this.state.reset(this.areaView, orientation, fform);
        updateMatrix();
        fitContentInBounds(true, false, false);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored);
        }
        return this.state.getOrientationOnly() != 0;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.animating && !this.areaView.onTouchEvent(event)) {
            switch (event.getAction()) {
                case 0:
                    onScrollChangeBegan();
                    break;
                case 1:
                case 3:
                    onScrollChangeEnded();
                    break;
            }
            try {
                boolean result = this.detector.onTouchEvent(event);
                return result;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override // org.telegram.ui.Components.Crop.CropAreaView.AreaViewListener
    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    @Override // org.telegram.ui.Components.Crop.CropAreaView.AreaViewListener
    public void onAreaChange() {
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
        float x = this.previousAreaRect.centerX() - this.areaView.getCropCenterX();
        float y = this.previousAreaRect.centerY() - this.areaView.getCropCenterY();
        CropState cropState = this.state;
        if (cropState != null) {
            cropState.translate(x, y);
        }
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    @Override // org.telegram.ui.Components.Crop.CropAreaView.AreaViewListener
    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    @Override // org.telegram.ui.Components.Crop.CropGestureDetector.CropGestureListener
    public void onDrag(float dx, float dy) {
        if (!this.animating) {
            this.state.translate(dx, dy);
            updateMatrix();
        }
    }

    @Override // org.telegram.ui.Components.Crop.CropGestureDetector.CropGestureListener
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
    }

    @Override // org.telegram.ui.Components.Crop.CropGestureDetector.CropGestureListener
    public void onTapUp() {
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onTapUp();
        }
    }

    public void onScrollChangeBegan() {
        if (this.animating) {
            return;
        }
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    @Override // org.telegram.ui.Components.Crop.CropGestureDetector.CropGestureListener
    public void onScale(float scale, float x, float y) {
        if (!this.animating) {
            float newScale = this.state.getScale() * scale;
            if (newScale > 30.0f) {
                scale = 30.0f / this.state.getScale();
            }
            float statusBarHeight = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            float pivotX = ((x - (this.imageView.getWidth() / 2)) / this.areaView.getCropWidth()) * this.state.getOrientedWidth();
            float pivotY = ((y - (((this.imageView.getHeight() - this.bottomPadding) - statusBarHeight) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
            this.state.scale(scale, pivotX, pivotY);
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
        if (this.rotationStartScale < EPSILON) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    @Override // android.view.View
    public void setRotation(float angle) {
        float deltaAngle = angle - this.state.getRotation();
        this.state.rotate(deltaAngle, 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public static void editBitmap(Context context, String path, Bitmap b, Canvas canvas, Bitmap canvasBitmap, Bitmap.CompressFormat format, Matrix stateMatrix, int contentWidth, int contentHeight, float stateScale, float rotation, float orientationOnly, float scale, boolean mirror, ArrayList<VideoEditedInfo.MediaEntity> entities, boolean clear) {
        Throwable e;
        Bitmap b2;
        float sc;
        Matrix matrix;
        FileOutputStream stream;
        FileOutputStream stream2;
        Matrix matrix2;
        TextPaintView textPaintView;
        int type;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = entities;
        if (clear) {
            try {
                canvasBitmap.eraseColor(0);
            } catch (Throwable th) {
                e = th;
                FileLog.e(e);
            }
        }
        if (b != null) {
            b2 = b;
        } else {
            b2 = BitmapFactory.decodeFile(path);
        }
        try {
            sc = Math.max(b2.getWidth(), b2.getHeight()) / Math.max(contentWidth, contentHeight);
            matrix = new Matrix();
            matrix.postTranslate((-b2.getWidth()) / 2, (-b2.getHeight()) / 2);
            if (mirror) {
                matrix.postScale(-1.0f, 1.0f);
            }
            matrix.postScale(1.0f / sc, 1.0f / sc);
            matrix.postRotate(orientationOnly);
            matrix.postConcat(stateMatrix);
            matrix.postScale(scale, scale);
            matrix.postTranslate(canvasBitmap.getWidth() / 2, canvasBitmap.getHeight() / 2);
            canvas.drawBitmap(b2, matrix, new Paint(2));
            try {
                stream = new FileOutputStream(new File(path));
            } catch (Throwable th2) {
                e = th2;
            }
        } catch (Throwable th3) {
            e = th3;
        }
        try {
            canvasBitmap.compress(format, 87, stream);
            stream.close();
            if (arrayList != null && !entities.isEmpty()) {
                float[] point = new float[4];
                float newScale = (1.0f / sc) * scale * stateScale;
                float widthScale = b2.getWidth() / canvasBitmap.getWidth();
                float newScale2 = newScale * widthScale;
                TextPaintView textPaintView2 = null;
                int N = entities.size();
                int a = 0;
                while (true) {
                    int N2 = N;
                    if (a >= N2) {
                        break;
                    }
                    VideoEditedInfo.MediaEntity entity = arrayList.get(a);
                    float sc2 = sc;
                    point[0] = (entity.x * b2.getWidth()) + ((entity.viewWidth * entity.scale) / 2.0f);
                    point[1] = (entity.y * b2.getHeight()) + ((entity.viewHeight * entity.scale) / 2.0f);
                    point[2] = entity.textViewX * b2.getWidth();
                    point[3] = entity.textViewY * b2.getHeight();
                    matrix.mapPoints(point);
                    if (entity.type == 0) {
                        int width = canvasBitmap.getWidth() / 2;
                        entity.viewHeight = width;
                        entity.viewWidth = width;
                        textPaintView = textPaintView2;
                        matrix2 = matrix;
                        stream2 = stream;
                    } else if (entity.type != 1) {
                        textPaintView = textPaintView2;
                        matrix2 = matrix;
                        stream2 = stream;
                    } else {
                        entity.fontSize = canvasBitmap.getWidth() / 9;
                        if (textPaintView2 == null) {
                            matrix2 = matrix;
                            stream2 = stream;
                            textPaintView2 = new TextPaintView(context, new Point(0.0f, 0.0f), entity.fontSize, "", new Swatch(-16777216, 0.85f, 0.1f), 0);
                            textPaintView2.setMaxWidth(canvasBitmap.getWidth() - 20);
                        } else {
                            matrix2 = matrix;
                            stream2 = stream;
                        }
                        if ((entity.subType & 1) != 0) {
                            type = 0;
                        } else {
                            int type2 = entity.subType;
                            if ((type2 & 4) != 0) {
                                type = 2;
                            } else {
                                type = 1;
                            }
                        }
                        textPaintView2.setType(type);
                        textPaintView2.setText(entity.text);
                        textPaintView2.measure(View.MeasureSpec.makeMeasureSpec(canvasBitmap.getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(canvasBitmap.getHeight(), Integer.MIN_VALUE));
                        entity.viewWidth = textPaintView2.getMeasuredWidth();
                        entity.viewHeight = textPaintView2.getMeasuredHeight();
                        entity.scale *= newScale2;
                        entity.x = (point[0] - ((entity.viewWidth * entity.scale) / 2.0f)) / canvasBitmap.getWidth();
                        entity.y = (point[1] - ((entity.viewHeight * entity.scale) / 2.0f)) / canvasBitmap.getHeight();
                        entity.textViewX = point[2] / canvasBitmap.getWidth();
                        entity.textViewY = point[3] / canvasBitmap.getHeight();
                        entity.width = (entity.viewWidth * entity.scale) / canvasBitmap.getWidth();
                        entity.height = (entity.viewHeight * entity.scale) / canvasBitmap.getHeight();
                        entity.textViewWidth = entity.viewWidth / canvasBitmap.getWidth();
                        entity.textViewHeight = entity.viewHeight / canvasBitmap.getHeight();
                        double d = entity.rotation;
                        double d2 = rotation + orientationOnly;
                        Double.isNaN(d2);
                        Double.isNaN(d);
                        entity.rotation = (float) (d - (d2 * 0.017453292519943295d));
                        a++;
                        arrayList = entities;
                        N = N2;
                        sc = sc2;
                        matrix = matrix2;
                        stream = stream2;
                    }
                    textPaintView2 = textPaintView;
                    entity.scale *= newScale2;
                    entity.x = (point[0] - ((entity.viewWidth * entity.scale) / 2.0f)) / canvasBitmap.getWidth();
                    entity.y = (point[1] - ((entity.viewHeight * entity.scale) / 2.0f)) / canvasBitmap.getHeight();
                    entity.textViewX = point[2] / canvasBitmap.getWidth();
                    entity.textViewY = point[3] / canvasBitmap.getHeight();
                    entity.width = (entity.viewWidth * entity.scale) / canvasBitmap.getWidth();
                    entity.height = (entity.viewHeight * entity.scale) / canvasBitmap.getHeight();
                    entity.textViewWidth = entity.viewWidth / canvasBitmap.getWidth();
                    entity.textViewHeight = entity.viewHeight / canvasBitmap.getHeight();
                    double d3 = entity.rotation;
                    double d22 = rotation + orientationOnly;
                    Double.isNaN(d22);
                    Double.isNaN(d3);
                    entity.rotation = (float) (d3 - (d22 * 0.017453292519943295d));
                    a++;
                    arrayList = entities;
                    N = N2;
                    sc = sc2;
                    matrix = matrix2;
                    stream = stream2;
                }
            }
            b2.recycle();
        } catch (Throwable th4) {
            e = th4;
            FileLog.e(e);
        }
    }

    private void updateCropTransform() {
        int sh;
        int sw;
        float realMininumScale;
        if (this.cropTransform != null && this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            float w = scaleWidthToMaxSize(this.cropRect, this.sizeRect);
            int width = (int) Math.ceil(w);
            int height = (int) Math.ceil(width / this.areaView.getAspectRatio());
            float scale = width / this.areaView.getCropWidth();
            this.state.matrix.getValues(this.values);
            float sc = this.state.minimumScale * scale;
            int transformRotation = this.state.getOrientationOnly();
            while (transformRotation < 0) {
                transformRotation += 360;
            }
            if (transformRotation == 90 || transformRotation == 270) {
                sw = (int) this.state.height;
                sh = (int) this.state.width;
            } else {
                sw = (int) this.state.width;
                sh = (int) this.state.height;
            }
            double d = width;
            double ceil = Math.ceil(sw * sc);
            Double.isNaN(d);
            float cropPw = (float) (d / ceil);
            double d2 = height;
            double ceil2 = Math.ceil(sh * sc);
            Double.isNaN(d2);
            float cropPh = (float) (d2 / ceil2);
            if (cropPw > 1.0f || cropPh > 1.0f) {
                float max = Math.max(cropPw, cropPh);
                cropPw /= max;
                cropPh /= max;
            }
            RectF rect = this.areaView.getTargetRectToFill(sw / sh);
            if (this.freeform) {
                realMininumScale = rect.width() / sw;
            } else {
                float wScale = rect.width() / sw;
                float hScale = rect.height() / sh;
                realMininumScale = Math.max(wScale, hScale);
            }
            float cropScale = this.state.scale / realMininumScale;
            float trueCropScale = this.state.scale / this.state.minimumScale;
            float cropPx = (this.values[2] / sw) / this.state.scale;
            float cropPy = (this.values[5] / sh) / this.state.scale;
            float cropRotate = this.state.rotation;
            RectF targetRect = this.areaView.getTargetRectToFill();
            float tx = this.areaView.getCropCenterX() - targetRect.centerX();
            float ty = this.areaView.getCropCenterY() - targetRect.centerY();
            this.cropTransform.setViewTransform(this.state.mirrored || this.state.hasChanges() || this.state.getBaseRotation() >= EPSILON, cropPx, cropPy, cropRotate, this.state.getOrientationOnly(), cropScale, trueCropScale, this.state.minimumScale / realMininumScale, cropPw, cropPh, tx, ty, this.state.mirrored);
        }
    }

    public static String getCopy(String path) {
        File directory = FileLoader.getDirectory(4);
        File f = new File(directory, SharedConfig.getLastLocalId() + "_temp.jpg");
        try {
            AndroidUtilities.copyFile(new File(path), f);
        } catch (Exception e) {
            FileLog.e(e);
        }
        return f.getAbsolutePath();
    }

    public void makeCrop(MediaController.MediaEditState editState) {
        int sh;
        int sw;
        if (this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            float w = scaleWidthToMaxSize(this.cropRect, this.sizeRect);
            int width = (int) Math.ceil(w);
            int height = (int) Math.ceil(width / this.areaView.getAspectRatio());
            float scale = width / this.areaView.getCropWidth();
            if (editState.paintPath != null) {
                Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(resultBitmap);
                String path = getCopy(editState.paintPath);
                if (editState.croppedPaintPath != null) {
                    new File(editState.croppedPaintPath).delete();
                    editState.croppedPaintPath = null;
                }
                editState.croppedPaintPath = path;
                if (editState.mediaEntities != null && !editState.mediaEntities.isEmpty()) {
                    editState.croppedMediaEntities = new ArrayList<>(editState.mediaEntities.size());
                    int N = editState.mediaEntities.size();
                    for (int a = 0; a < N; a++) {
                        editState.croppedMediaEntities.add(editState.mediaEntities.get(a).copy());
                    }
                } else {
                    editState.croppedMediaEntities = null;
                }
                editBitmap(getContext(), path, null, canvas, resultBitmap, Bitmap.CompressFormat.PNG, this.state.matrix, getCurrentWidth(), getCurrentHeight(), this.state.scale, this.state.rotation, this.state.getOrientationOnly(), scale, false, editState.croppedMediaEntities, false);
            }
            if (editState.cropState == null) {
                editState.cropState = new MediaController.CropState();
            }
            this.state.matrix.getValues(this.values);
            float sc = this.state.minimumScale * scale;
            editState.cropState.transformRotation = this.state.getOrientationOnly();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("set transformRotation = " + editState.cropState.transformRotation);
            }
            while (editState.cropState.transformRotation < 0) {
                editState.cropState.transformRotation += 360;
            }
            if (editState.cropState.transformRotation == 90 || editState.cropState.transformRotation == 270) {
                sw = (int) this.state.height;
                sh = (int) this.state.width;
            } else {
                sw = (int) this.state.width;
                sh = (int) this.state.height;
            }
            MediaController.CropState cropState = editState.cropState;
            double d = width;
            double ceil = Math.ceil(sw * sc);
            Double.isNaN(d);
            cropState.cropPw = (float) (d / ceil);
            MediaController.CropState cropState2 = editState.cropState;
            double d2 = height;
            double ceil2 = Math.ceil(sh * sc);
            Double.isNaN(d2);
            cropState2.cropPh = (float) (d2 / ceil2);
            if (editState.cropState.cropPw > 1.0f || editState.cropState.cropPh > 1.0f) {
                float max = Math.max(editState.cropState.cropPw, editState.cropState.cropPh);
                editState.cropState.cropPw /= max;
                editState.cropState.cropPh /= max;
            }
            editState.cropState.cropScale = this.state.scale * Math.min(sw / this.areaView.getCropWidth(), sh / this.areaView.getCropHeight());
            editState.cropState.cropPx = (this.values[2] / sw) / this.state.scale;
            editState.cropState.cropPy = (this.values[5] / sh) / this.state.scale;
            editState.cropState.cropRotate = this.state.rotation;
            editState.cropState.stateScale = this.state.scale;
            editState.cropState.mirrored = this.state.mirrored;
            editState.cropState.scale = scale;
            editState.cropState.matrix = this.state.matrix;
            editState.cropState.width = width;
            editState.cropState.height = height;
            editState.cropState.freeform = this.freeform;
            editState.cropState.lockedAspectRatio = this.areaView.getLockAspectRatio();
            editState.cropState.initied = true;
        }
    }

    private void setLockedAspectRatio(float aspectRatio) {
        this.areaView.setLockedAspectRatio(aspectRatio);
        RectF targetRect = new RectF();
        this.areaView.calculateRect(targetRect, aspectRatio);
        fillAreaView(targetRect, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.state == null || this.hasAspectRatioDialog) {
            return;
        }
        this.hasAspectRatioDialog = true;
        String[] actions = new String[8];
        final Integer[][] ratios = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
        actions[0] = LocaleController.getString("CropOriginal", R.string.CropOriginal);
        actions[1] = LocaleController.getString("CropSquare", R.string.CropSquare);
        int i = 2;
        for (Integer[] ratioPair : ratios) {
            if (this.areaView.getAspectRatio() > 1.0f) {
                actions[i] = String.format("%d:%d", ratioPair[0], ratioPair[1]);
            } else {
                actions[i] = String.format("%d:%d", ratioPair[1], ratioPair[0]);
            }
            i++;
        }
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(actions, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.Crop.CropView$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                CropView.this.m2551xf75a1fe5(ratios, dialogInterface, i2);
            }
        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.Crop.CropView$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                CropView.this.m2552xfebf5504(dialogInterface);
            }
        });
        dialog.show();
    }

    /* renamed from: lambda$showAspectRatioDialog$3$org-telegram-ui-Components-Crop-CropView */
    public /* synthetic */ void m2551xf75a1fe5(Integer[][] ratios, DialogInterface dialog12, int which) {
        this.hasAspectRatioDialog = false;
        switch (which) {
            case 0:
                float w = this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth();
                float h = this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight();
                setLockedAspectRatio(w / h);
                return;
            case 1:
                setLockedAspectRatio(1.0f);
                return;
            default:
                Integer[] ratioPair = ratios[which - 2];
                if (this.areaView.getAspectRatio() <= 1.0f) {
                    setLockedAspectRatio(ratioPair[1].intValue() / ratioPair[0].intValue());
                    return;
                } else {
                    setLockedAspectRatio(ratioPair[0].intValue() / ratioPair[1].intValue());
                    return;
                }
        }
    }

    /* renamed from: lambda$showAspectRatioDialog$4$org-telegram-ui-Components-Crop-CropView */
    public /* synthetic */ void m2552xfebf5504(DialogInterface dialog1) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        CropState cropState;
        float w = this.areaView.getCropWidth();
        if (w != 0.0f && (cropState = this.state) != null) {
            this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setActualRect(cropAreaView.getAspectRatio());
            this.areaView.getCropRect(this.previousAreaRect);
            float ratio = this.areaView.getCropWidth() / w;
            this.state.scale(ratio, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public float getCropLeft() {
        return this.areaView.getCropLeft();
    }

    public float getCropTop() {
        return this.areaView.getCropTop();
    }

    public float getCropWidth() {
        return this.areaView.getCropWidth();
    }

    public float getCropHeight() {
        return this.areaView.getCropHeight();
    }

    public RectF getActualRect() {
        this.areaView.getCropRect(this.cropRect);
        return this.cropRect;
    }
}
