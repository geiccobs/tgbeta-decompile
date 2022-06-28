package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class AspectRatioFrameLayout extends FrameLayout {
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;
    public static final int RESIZE_MODE_FILL = 3;
    public static final int RESIZE_MODE_FIT = 0;
    public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
    public static final int RESIZE_MODE_FIXED_WIDTH = 1;
    public static final int RESIZE_MODE_ZOOM = 4;
    private AspectRatioListener aspectRatioListener;
    private boolean drawingReady;
    private int rotation;
    private float videoAspectRatio;
    private Matrix matrix = new Matrix();
    private int resizeMode = 0;
    private final AspectRatioUpdateDispatcher aspectRatioUpdateDispatcher = new AspectRatioUpdateDispatcher();

    /* loaded from: classes3.dex */
    public interface AspectRatioListener {
        void onAspectRatioUpdated(float f, float f2, boolean z);
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ResizeMode {
    }

    public AspectRatioFrameLayout(Context context) {
        super(context);
    }

    public void setAspectRatio(float widthHeightRatio, int rotation) {
        if (this.videoAspectRatio != widthHeightRatio) {
            this.videoAspectRatio = widthHeightRatio;
            this.rotation = rotation;
            requestLayout();
        }
    }

    public void setAspectRatioListener(AspectRatioListener listener) {
        this.aspectRatioListener = listener;
    }

    public int getResizeMode() {
        return this.resizeMode;
    }

    public void setResizeMode(int resizeMode) {
        if (this.resizeMode != resizeMode) {
            this.resizeMode = resizeMode;
            requestLayout();
        }
    }

    public void setDrawingReady(boolean value) {
        if (this.drawingReady == value) {
            return;
        }
        this.drawingReady = value;
    }

    public float getAspectRatio() {
        return this.videoAspectRatio;
    }

    public int getVideoRotation() {
        return this.rotation;
    }

    public boolean isDrawingReady() {
        return this.drawingReady;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.videoAspectRatio <= 0.0f) {
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float viewAspectRatio = width / height;
        float aspectDeformation = (this.videoAspectRatio / viewAspectRatio) - 1.0f;
        if (Math.abs(aspectDeformation) <= 0.01f) {
            this.aspectRatioUpdateDispatcher.scheduleUpdate(this.videoAspectRatio, viewAspectRatio, false);
            return;
        }
        switch (this.resizeMode) {
            case 0:
                if (aspectDeformation > 0.0f) {
                    height = (int) (width / this.videoAspectRatio);
                    break;
                } else {
                    width = (int) (height * this.videoAspectRatio);
                    break;
                }
            case 1:
                height = (int) (width / this.videoAspectRatio);
                break;
            case 2:
                width = (int) (height * this.videoAspectRatio);
                break;
            case 3:
                if (aspectDeformation <= 0.0f) {
                    height = (int) (width / this.videoAspectRatio);
                    break;
                } else {
                    width = (int) (height * this.videoAspectRatio);
                    break;
                }
            case 4:
                if (aspectDeformation > 0.0f) {
                    width = (int) (height * this.videoAspectRatio);
                    break;
                } else {
                    height = (int) (width / this.videoAspectRatio);
                    break;
                }
        }
        this.aspectRatioUpdateDispatcher.scheduleUpdate(this.videoAspectRatio, viewAspectRatio, true);
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof TextureView) {
                this.matrix.reset();
                int px = getWidth() / 2;
                int py = getHeight() / 2;
                this.matrix.postRotate(this.rotation, px, py);
                int i = this.rotation;
                if (i == 90 || i == 270) {
                    float ratio = getHeight() / getWidth();
                    this.matrix.postScale(1.0f / ratio, ratio, px, py);
                }
                ((TextureView) child).setTransform(this.matrix);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class AspectRatioUpdateDispatcher implements Runnable {
        private boolean aspectRatioMismatch;
        private boolean isScheduled;
        private float naturalAspectRatio;
        private float targetAspectRatio;

        private AspectRatioUpdateDispatcher() {
            AspectRatioFrameLayout.this = r1;
        }

        public void scheduleUpdate(float targetAspectRatio, float naturalAspectRatio, boolean aspectRatioMismatch) {
            this.targetAspectRatio = targetAspectRatio;
            this.naturalAspectRatio = naturalAspectRatio;
            this.aspectRatioMismatch = aspectRatioMismatch;
            if (!this.isScheduled) {
                this.isScheduled = true;
                AspectRatioFrameLayout.this.post(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.isScheduled = false;
            if (AspectRatioFrameLayout.this.aspectRatioListener != null) {
                AspectRatioFrameLayout.this.aspectRatioListener.onAspectRatioUpdated(this.targetAspectRatio, this.naturalAspectRatio, this.aspectRatioMismatch);
            }
        }
    }
}
