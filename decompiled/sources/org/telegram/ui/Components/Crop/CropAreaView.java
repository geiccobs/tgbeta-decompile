package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
/* loaded from: classes5.dex */
public class CropAreaView extends ViewGroup {
    private Control activeControl;
    private Animator animator;
    private Paint bitmapPaint;
    private float bottomPadding;
    private Bitmap circleBitmap;
    private Paint dimPaint;
    private Paint eraserPaint;
    private Paint framePaint;
    private Animator gridAnimator;
    private float gridProgress;
    private Paint handlePaint;
    private boolean inBubbleMode;
    private boolean isDragging;
    private long lastUpdateTime;
    private Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private Paint shadowPaint;
    private RectF topLeftCorner = new RectF();
    private RectF topRightCorner = new RectF();
    private RectF bottomLeftCorner = new RectF();
    private RectF bottomRightCorner = new RectF();
    private RectF topEdge = new RectF();
    private RectF leftEdge = new RectF();
    private RectF bottomEdge = new RectF();
    private RectF rightEdge = new RectF();
    private RectF actualRect = new RectF();
    private RectF tempRect = new RectF();
    private float frameAlpha = 1.0f;
    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private boolean freeform = true;
    private RectF targetRect = new RectF();
    public float rotate = 0.0f;
    public float scale = 1.0f;
    public float tx = 0.0f;
    public float ty = 0.0f;
    private boolean frameVisible = true;
    private boolean dimVisibile = true;
    private float sidePadding = AndroidUtilities.dp(16.0f);
    private float minWidth = AndroidUtilities.dp(32.0f);
    private GridType gridType = GridType.NONE;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public interface AreaViewListener {
        void onAreaChange();

        void onAreaChangeBegan();

        void onAreaChangeEnded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public enum Control {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    /* loaded from: classes5.dex */
    public enum GridType {
        NONE,
        MINOR,
        MAJOR
    }

    public CropAreaView(Context context) {
        super(context);
        this.inBubbleMode = context instanceof BubbleActivity;
        Paint paint = new Paint();
        this.dimPaint = paint;
        paint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        Paint paint2 = new Paint();
        this.shadowPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.shadowPaint.setColor(436207616);
        this.shadowPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        Paint paint4 = new Paint();
        this.handlePaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        this.handlePaint.setColor(-1);
        Paint paint5 = new Paint();
        this.framePaint = paint5;
        paint5.setStyle(Paint.Style.FILL);
        this.framePaint.setColor(-1291845633);
        Paint paint6 = new Paint(1);
        this.eraserPaint = paint6;
        paint6.setColor(0);
        this.eraserPaint.setStyle(Paint.Style.FILL);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint7 = new Paint(2);
        this.bitmapPaint = paint7;
        paint7.setColor(-1);
        setWillNotDraw(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }

    public void setIsVideo(boolean value) {
        this.minWidth = AndroidUtilities.dp(value ? 64.0f : 32.0f);
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDimVisibility(boolean visible) {
        this.dimVisibile = visible;
    }

    public void setFrameVisibility(boolean visible, boolean animated) {
        this.frameVisible = visible;
        float f = 1.0f;
        if (visible) {
            if (animated) {
                f = 0.0f;
            }
            this.frameAlpha = f;
            this.lastUpdateTime = SystemClock.elapsedRealtime();
            invalidate();
            return;
        }
        this.frameAlpha = 1.0f;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
    }

    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    public void setListener(AreaViewListener l) {
        this.listener = l;
    }

    public void setBitmap(int w, int h, boolean sideward, boolean fform) {
        float aspectRatio;
        this.freeform = fform;
        if (sideward) {
            aspectRatio = h / w;
        } else {
            float aspectRatio2 = w;
            aspectRatio = aspectRatio2 / h;
        }
        if (!fform) {
            aspectRatio = 1.0f;
            this.lockAspectRatio = 1.0f;
        }
        setActualRect(aspectRatio);
    }

    public void setFreeform(boolean fform) {
        this.freeform = fform;
    }

    public void setActualRect(float aspectRatio) {
        calculateRect(this.actualRect, aspectRatio);
        updateTouchAreas();
        invalidate();
    }

    public void setActualRect(RectF rect) {
        this.actualRect.set(rect);
        updateTouchAreas();
        invalidate();
    }

    public void setRotationScaleTranslation(float rotate, float scale, float tx, float ty) {
        this.rotate = rotate;
        this.scale = scale;
        this.tx = tx;
        this.ty = ty;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int handleSize;
        GridType type;
        int inset;
        int lineThickness;
        int lineThickness2;
        int inset2;
        int j;
        int lineThickness3;
        int lineThickness4;
        if (!this.freeform) {
            float width = getMeasuredWidth() - (this.sidePadding * 2.0f);
            int i = 0;
            float height = ((getMeasuredHeight() - this.bottomPadding) - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) - (this.sidePadding * 2.0f);
            int size = (int) Math.min(width, height);
            Bitmap bitmap = this.circleBitmap;
            if (bitmap == null || bitmap.getWidth() != size) {
                Bitmap bitmap2 = this.circleBitmap;
                boolean hasBitmap = bitmap2 != null;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                    this.circleBitmap = null;
                }
                try {
                    this.circleBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                    Canvas circleCanvas = new Canvas(this.circleBitmap);
                    circleCanvas.drawRect(0.0f, 0.0f, size, size, this.dimPaint);
                    circleCanvas.drawCircle(size / 2, size / 2, size / 2, this.eraserPaint);
                    circleCanvas.setBitmap(null);
                    if (!hasBitmap) {
                        this.frameAlpha = 0.0f;
                        this.lastUpdateTime = SystemClock.elapsedRealtime();
                    }
                } catch (Throwable th) {
                }
            }
            if (this.circleBitmap != null) {
                this.bitmapPaint.setAlpha((int) (this.frameAlpha * 255.0f));
                this.dimPaint.setAlpha((int) (this.frameAlpha * 127.0f));
                float f = this.sidePadding;
                float left = f + ((width - size) / 2.0f);
                float f2 = f + ((height - size) / 2.0f);
                if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
                    i = AndroidUtilities.statusBarHeight;
                }
                float top = f2 + i;
                float bottom = top + size;
                canvas.drawRect(0.0f, 0.0f, getWidth(), (int) top, this.dimPaint);
                canvas.drawRect(0.0f, (int) top, (int) left, (int) bottom, this.dimPaint);
                canvas.drawRect((int) (left + size), (int) top, getWidth(), (int) bottom, this.dimPaint);
                canvas.drawRect(0.0f, (int) bottom, getWidth(), getHeight(), this.dimPaint);
                canvas.drawBitmap(this.circleBitmap, (int) left, (int) top, this.bitmapPaint);
            }
        } else {
            int i2 = AndroidUtilities.dp(2.0f / this.scale);
            int handleSize2 = AndroidUtilities.dp(16.0f / this.scale);
            int handleThickness = AndroidUtilities.dp(3.0f / this.scale);
            int originX = ((int) this.actualRect.left) - i2;
            int originY = ((int) this.actualRect.top) - i2;
            int width2 = ((int) (this.actualRect.right - this.actualRect.left)) + (i2 * 2);
            int height2 = ((int) (this.actualRect.bottom - this.actualRect.top)) + (i2 * 2);
            canvas.save();
            canvas.translate(this.tx, this.ty);
            float f3 = this.scale;
            canvas.scale(f3, f3, (width2 / 2) + originX, (height2 / 2) + originY);
            canvas.rotate(this.rotate, (width2 / 2) + originX, (height2 / 2) + originY);
            if (!this.dimVisibile) {
                handleSize = handleSize2;
            } else {
                int left2 = (-getWidth()) * 4;
                int top2 = (-getHeight()) * 4;
                int right = getWidth() * 4;
                int bottom2 = getHeight() * 4;
                this.dimPaint.setAlpha((int) (255.0f - (this.frameAlpha * 127.0f)));
                handleSize = handleSize2;
                canvas.drawRect(left2, top2, right, 0.0f, this.dimPaint);
                canvas.drawRect(left2, 0.0f, 0.0f, getHeight(), this.dimPaint);
                canvas.drawRect(getWidth(), 0.0f, right, getHeight(), this.dimPaint);
                canvas.drawRect(left2, getHeight(), right, bottom2, this.dimPaint);
                canvas.drawRect(0.0f, 0.0f, getWidth(), originY + i2, this.dimPaint);
                canvas.drawRect(0.0f, originY + i2, originX + i2, (originY + height2) - i2, this.dimPaint);
                canvas.drawRect((originX + width2) - i2, originY + i2, getWidth(), (originY + height2) - i2, this.dimPaint);
                canvas.drawRect(0.0f, (originY + height2) - i2, getWidth(), getHeight(), this.dimPaint);
            }
            if (!this.frameVisible) {
                return;
            }
            int inset3 = handleThickness - i2;
            int gridWidth = width2 - (handleThickness * 2);
            int gridHeight = height2 - (handleThickness * 2);
            GridType type2 = this.gridType;
            if (type2 == GridType.NONE && this.gridProgress > 0.0f) {
                type = this.previousGridType;
            } else {
                type = type2;
            }
            this.shadowPaint.setAlpha((int) (this.gridProgress * 26.0f * this.frameAlpha));
            this.linePaint.setAlpha((int) (this.gridProgress * 178.0f * this.frameAlpha));
            this.framePaint.setAlpha((int) (this.frameAlpha * 178.0f));
            this.handlePaint.setAlpha((int) (this.frameAlpha * 255.0f));
            GridType type3 = type;
            canvas.drawRect(originX + inset3, originY + inset3, (originX + width2) - inset3, originY + inset3 + i2, this.framePaint);
            canvas.drawRect(originX + inset3, originY + inset3, originX + inset3 + i2, (originY + height2) - inset3, this.framePaint);
            canvas.drawRect(originX + inset3, ((originY + height2) - inset3) - i2, (originX + width2) - inset3, (originY + height2) - inset3, this.framePaint);
            canvas.drawRect(((originX + width2) - inset3) - i2, originY + inset3, (originX + width2) - inset3, (originY + height2) - inset3, this.framePaint);
            int i3 = 0;
            while (true) {
                int i4 = 3;
                if (i3 >= 3) {
                    break;
                }
                if (type3 == GridType.MINOR) {
                    int j2 = 1;
                    while (j2 < 4) {
                        if (i3 == 2 && j2 == i4) {
                            lineThickness3 = i2;
                            j = j2;
                            lineThickness4 = i3;
                            inset2 = inset3;
                        } else {
                            int startX = ((gridWidth / 3) * i3) + originX + handleThickness + (((gridWidth / 3) / i4) * j2);
                            lineThickness3 = i2;
                            int lineThickness5 = originY + handleThickness + gridHeight;
                            inset2 = inset3;
                            j = j2;
                            lineThickness4 = i3;
                            canvas.drawLine(startX, originY + handleThickness, startX, lineThickness5, this.shadowPaint);
                            canvas.drawLine(startX, originY + handleThickness, startX, originY + handleThickness + gridHeight, this.linePaint);
                            int startY = originY + handleThickness + (((gridHeight / 3) / 3) * j) + ((gridHeight / 3) * lineThickness4);
                            canvas.drawLine(originX + handleThickness, startY, originX + handleThickness + gridWidth, startY, this.shadowPaint);
                            canvas.drawLine(originX + handleThickness, startY, originX + handleThickness + gridWidth, startY, this.linePaint);
                        }
                        j2 = j + 1;
                        i3 = lineThickness4;
                        i2 = lineThickness3;
                        inset3 = inset2;
                        i4 = 3;
                    }
                    lineThickness = i2;
                    lineThickness2 = i3;
                    inset = inset3;
                } else {
                    lineThickness = i2;
                    lineThickness2 = i3;
                    inset = inset3;
                    if (type3 == GridType.MAJOR && lineThickness2 > 0) {
                        int startX2 = originX + handleThickness + ((gridWidth / 3) * lineThickness2);
                        canvas.drawLine(startX2, originY + handleThickness, startX2, originY + handleThickness + gridHeight, this.shadowPaint);
                        canvas.drawLine(startX2, originY + handleThickness, startX2, originY + handleThickness + gridHeight, this.linePaint);
                        int startY2 = originY + handleThickness + ((gridHeight / 3) * lineThickness2);
                        canvas.drawLine(originX + handleThickness, startY2, originX + handleThickness + gridWidth, startY2, this.shadowPaint);
                        canvas.drawLine(originX + handleThickness, startY2, originX + handleThickness + gridWidth, startY2, this.linePaint);
                    }
                }
                i3 = lineThickness2 + 1;
                i2 = lineThickness;
                inset3 = inset;
            }
            canvas.drawRect(originX, originY, originX + handleSize, originY + handleThickness, this.handlePaint);
            canvas.drawRect(originX, originY, originX + handleThickness, originY + handleSize, this.handlePaint);
            canvas.drawRect((originX + width2) - handleSize, originY, originX + width2, originY + handleThickness, this.handlePaint);
            canvas.drawRect((originX + width2) - handleThickness, originY, originX + width2, originY + handleSize, this.handlePaint);
            canvas.drawRect(originX, (originY + height2) - handleThickness, originX + handleSize, originY + height2, this.handlePaint);
            canvas.drawRect(originX, (originY + height2) - handleSize, originX + handleThickness, originY + height2, this.handlePaint);
            canvas.drawRect((originX + width2) - handleSize, (originY + height2) - handleThickness, originX + width2, originY + height2, this.handlePaint);
            canvas.drawRect((originX + width2) - handleThickness, (originY + height2) - handleSize, originX + width2, originY + height2, this.handlePaint);
            canvas.restore();
        }
        if (this.frameAlpha < 1.0f) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            float f4 = this.frameAlpha + (((float) dt) / 180.0f);
            this.frameAlpha = f4;
            if (f4 > 1.0f) {
                this.frameAlpha = 1.0f;
            }
            invalidate();
        }
    }

    public void updateTouchAreas() {
        int touchPadding = AndroidUtilities.dp(16.0f);
        this.topLeftCorner.set(this.actualRect.left - touchPadding, this.actualRect.top - touchPadding, this.actualRect.left + touchPadding, this.actualRect.top + touchPadding);
        this.topRightCorner.set(this.actualRect.right - touchPadding, this.actualRect.top - touchPadding, this.actualRect.right + touchPadding, this.actualRect.top + touchPadding);
        this.bottomLeftCorner.set(this.actualRect.left - touchPadding, this.actualRect.bottom - touchPadding, this.actualRect.left + touchPadding, this.actualRect.bottom + touchPadding);
        this.bottomRightCorner.set(this.actualRect.right - touchPadding, this.actualRect.bottom - touchPadding, this.actualRect.right + touchPadding, this.actualRect.bottom + touchPadding);
        this.topEdge.set(this.actualRect.left + touchPadding, this.actualRect.top - touchPadding, this.actualRect.right - touchPadding, this.actualRect.top + touchPadding);
        this.leftEdge.set(this.actualRect.left - touchPadding, this.actualRect.top + touchPadding, this.actualRect.left + touchPadding, this.actualRect.bottom - touchPadding);
        this.rightEdge.set(this.actualRect.right - touchPadding, this.actualRect.top + touchPadding, this.actualRect.right + touchPadding, this.actualRect.bottom - touchPadding);
        this.bottomEdge.set(this.actualRect.left + touchPadding, this.actualRect.bottom - touchPadding, this.actualRect.right - touchPadding, this.actualRect.bottom + touchPadding);
    }

    public float getLockAspectRatio() {
        return this.lockAspectRatio;
    }

    public void setLockedAspectRatio(float aspectRatio) {
        this.lockAspectRatio = aspectRatio;
    }

    public void setGridType(GridType type, boolean animated) {
        Animator animator = this.gridAnimator;
        if (animator != null && (!animated || this.gridType != type)) {
            animator.cancel();
            this.gridAnimator = null;
        }
        GridType gridType = this.gridType;
        if (gridType == type) {
            return;
        }
        this.previousGridType = gridType;
        this.gridType = type;
        float targetProgress = type == GridType.NONE ? 0.0f : 1.0f;
        if (!animated) {
            this.gridProgress = targetProgress;
            invalidate();
            return;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "gridProgress", this.gridProgress, targetProgress);
        this.gridAnimator = ofFloat;
        ofFloat.setDuration(200L);
        this.gridAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Crop.CropAreaView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                CropAreaView.this.gridAnimator = null;
            }
        });
        if (type == GridType.NONE) {
            this.gridAnimator.setStartDelay(200L);
        }
        this.gridAnimator.start();
    }

    private void setGridProgress(float value) {
        this.gridProgress = value;
        invalidate();
    }

    private float getGridProgress() {
        return this.gridProgress;
    }

    public float getAspectRatio() {
        return (this.actualRect.right - this.actualRect.left) / (this.actualRect.bottom - this.actualRect.top);
    }

    public void fill(final RectF targetRect, Animator scaleAnimator, boolean animated) {
        if (animated) {
            Animator animator = this.animator;
            if (animator != null) {
                animator.cancel();
                this.animator = null;
            }
            AnimatorSet set = new AnimatorSet();
            this.animator = set;
            set.setDuration(300L);
            float[] fArr = {targetRect.left};
            animators[0].setInterpolator(this.interpolator);
            float[] fArr2 = {targetRect.top};
            animators[1].setInterpolator(this.interpolator);
            float[] fArr3 = {targetRect.right};
            animators[2].setInterpolator(this.interpolator);
            float[] fArr4 = {targetRect.bottom};
            animators[3].setInterpolator(this.interpolator);
            Animator[] animators = {ObjectAnimator.ofFloat(this, "cropLeft", fArr), ObjectAnimator.ofFloat(this, "cropTop", fArr2), ObjectAnimator.ofFloat(this, "cropRight", fArr3), ObjectAnimator.ofFloat(this, "cropBottom", fArr4), scaleAnimator};
            animators[4].setInterpolator(this.interpolator);
            set.playTogether(animators);
            set.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Crop.CropAreaView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CropAreaView.this.setActualRect(targetRect);
                    CropAreaView.this.animator = null;
                }
            });
            set.start();
            return;
        }
        setActualRect(targetRect);
    }

    public void resetAnimator() {
        Animator animator = this.animator;
        if (animator != null) {
            animator.cancel();
            this.animator = null;
        }
    }

    private void setCropLeft(float value) {
        this.actualRect.left = value;
        invalidate();
    }

    public float getCropLeft() {
        return this.actualRect.left;
    }

    private void setCropTop(float value) {
        this.actualRect.top = value;
        invalidate();
    }

    public float getCropTop() {
        return this.actualRect.top;
    }

    private void setCropRight(float value) {
        this.actualRect.right = value;
        invalidate();
    }

    public float getCropRight() {
        return this.actualRect.right;
    }

    private void setCropBottom(float value) {
        this.actualRect.bottom = value;
        invalidate();
    }

    public float getCropBottom() {
        return this.actualRect.bottom;
    }

    public float getCropCenterX() {
        return (this.actualRect.left + this.actualRect.right) / 2.0f;
    }

    public float getCropCenterY() {
        return (this.actualRect.top + this.actualRect.bottom) / 2.0f;
    }

    public float getCropWidth() {
        return this.actualRect.right - this.actualRect.left;
    }

    public float getCropHeight() {
        return this.actualRect.bottom - this.actualRect.top;
    }

    public RectF getTargetRectToFill() {
        return getTargetRectToFill(getAspectRatio());
    }

    public RectF getTargetRectToFill(float aspectRatio) {
        calculateRect(this.targetRect, aspectRatio);
        return this.targetRect;
    }

    public void calculateRect(RectF rect, float cropAspectRatio) {
        float right;
        float top;
        float left;
        float bottom;
        float statusBarHeight = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        float measuredHeight = (getMeasuredHeight() - this.bottomPadding) - statusBarHeight;
        float aspectRatio = getMeasuredWidth() / measuredHeight;
        float minSide = Math.min(getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float f = this.sidePadding;
        float width = getMeasuredWidth() - (f * 2.0f);
        float height = measuredHeight - (f * 2.0f);
        float centerX = getMeasuredWidth() / 2.0f;
        float centerY = (measuredHeight / 2.0f) + statusBarHeight;
        if (Math.abs(1.0f - cropAspectRatio) < 1.0E-4d) {
            left = centerX - (minSide / 2.0f);
            top = centerY - (minSide / 2.0f);
            right = (minSide / 2.0f) + centerX;
            bottom = (minSide / 2.0f) + centerY;
        } else {
            float left2 = cropAspectRatio - aspectRatio;
            if (left2 > 1.0E-4d || height * cropAspectRatio > width) {
                float left3 = width / 2.0f;
                left = centerX - left3;
                top = centerY - ((width / cropAspectRatio) / 2.0f);
                right = (width / 2.0f) + centerX;
                bottom = centerY + ((width / cropAspectRatio) / 2.0f);
            } else {
                left = centerX - ((height * cropAspectRatio) / 2.0f);
                top = centerY - (height / 2.0f);
                right = ((height * cropAspectRatio) / 2.0f) + centerX;
                bottom = (height / 2.0f) + centerY;
            }
        }
        rect.set(left, top, right, bottom);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isDragging) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void updateStatusShow(boolean show) {
        int flags;
        try {
            Window window = ((Activity) getContext()).getWindow();
            View decorView = window.getDecorView();
            int flags2 = decorView.getSystemUiVisibility();
            if (show) {
                flags = flags2 | 4;
            } else {
                flags = flags2 & (-5);
            }
            decorView.setSystemUiVisibility(flags);
        } catch (Exception e) {
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) (event.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (event.getY() - ((ViewGroup) getParent()).getY());
        boolean b = false;
        float statusBarHeight = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        int action = event.getActionMasked();
        if (action == 0) {
            if (!this.freeform) {
                this.activeControl = Control.NONE;
                return false;
            }
            if (this.topLeftCorner.contains(x, y)) {
                this.activeControl = Control.TOP_LEFT;
            } else if (this.topRightCorner.contains(x, y)) {
                this.activeControl = Control.TOP_RIGHT;
            } else if (this.bottomLeftCorner.contains(x, y)) {
                this.activeControl = Control.BOTTOM_LEFT;
            } else if (this.bottomRightCorner.contains(x, y)) {
                this.activeControl = Control.BOTTOM_RIGHT;
            } else if (this.leftEdge.contains(x, y)) {
                this.activeControl = Control.LEFT;
            } else if (this.topEdge.contains(x, y)) {
                this.activeControl = Control.TOP;
            } else if (this.rightEdge.contains(x, y)) {
                this.activeControl = Control.RIGHT;
            } else if (this.bottomEdge.contains(x, y)) {
                this.activeControl = Control.BOTTOM;
            } else {
                this.activeControl = Control.NONE;
                return false;
            }
            this.previousX = x;
            this.previousY = y;
            setGridType(GridType.MAJOR, false);
            this.isDragging = true;
            updateStatusShow(true);
            AreaViewListener areaViewListener = this.listener;
            if (areaViewListener != null) {
                areaViewListener.onAreaChangeBegan();
            }
            return true;
        } else if (action == 1 || action == 3) {
            this.isDragging = false;
            updateStatusShow(false);
            if (this.activeControl == Control.NONE) {
                return false;
            }
            this.activeControl = Control.NONE;
            AreaViewListener areaViewListener2 = this.listener;
            if (areaViewListener2 != null) {
                areaViewListener2.onAreaChangeEnded();
            }
            return true;
        } else if (action != 2 || this.activeControl == Control.NONE) {
            return false;
        } else {
            this.tempRect.set(this.actualRect);
            float translationX = x - this.previousX;
            float translationY = y - this.previousY;
            this.previousX = x;
            this.previousY = y;
            if (Math.abs(translationX) > Math.abs(translationY)) {
                b = true;
            }
            switch (AnonymousClass3.$SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[this.activeControl.ordinal()]) {
                case 1:
                    this.tempRect.left += translationX;
                    this.tempRect.top += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float w = this.tempRect.width();
                        float h = this.tempRect.height();
                        if (b) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.left -= this.tempRect.width() - w;
                        this.tempRect.top -= this.tempRect.width() - h;
                        break;
                    }
                    break;
                case 2:
                    this.tempRect.right += translationX;
                    this.tempRect.top += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float h2 = this.tempRect.height();
                        if (b) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.top -= this.tempRect.width() - h2;
                        break;
                    }
                    break;
                case 3:
                    this.tempRect.left += translationX;
                    this.tempRect.bottom += translationY;
                    if (this.lockAspectRatio > 0.0f) {
                        float w2 = this.tempRect.width();
                        if (b) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        this.tempRect.left -= this.tempRect.width() - w2;
                        break;
                    }
                    break;
                case 4:
                    this.tempRect.right += translationX;
                    this.tempRect.bottom += translationY;
                    float f = this.lockAspectRatio;
                    if (f > 0.0f) {
                        if (b) {
                            constrainRectByWidth(this.tempRect, f);
                            break;
                        } else {
                            constrainRectByHeight(this.tempRect, f);
                            break;
                        }
                    }
                    break;
                case 5:
                    this.tempRect.top += translationY;
                    float f2 = this.lockAspectRatio;
                    if (f2 > 0.0f) {
                        constrainRectByHeight(this.tempRect, f2);
                        break;
                    }
                    break;
                case 6:
                    this.tempRect.left += translationX;
                    float f3 = this.lockAspectRatio;
                    if (f3 > 0.0f) {
                        constrainRectByWidth(this.tempRect, f3);
                        break;
                    }
                    break;
                case 7:
                    this.tempRect.right += translationX;
                    float f4 = this.lockAspectRatio;
                    if (f4 > 0.0f) {
                        constrainRectByWidth(this.tempRect, f4);
                        break;
                    }
                    break;
                case 8:
                    this.tempRect.bottom += translationY;
                    float f5 = this.lockAspectRatio;
                    if (f5 > 0.0f) {
                        constrainRectByHeight(this.tempRect, f5);
                        break;
                    }
                    break;
            }
            if (this.tempRect.left >= this.sidePadding) {
                if (this.tempRect.right > getWidth() - this.sidePadding) {
                    this.tempRect.right = getWidth() - this.sidePadding;
                    if (this.lockAspectRatio > 0.0f) {
                        RectF rectF = this.tempRect;
                        rectF.bottom = rectF.top + (this.tempRect.width() / this.lockAspectRatio);
                    }
                }
            } else {
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF2 = this.tempRect;
                    rectF2.bottom = rectF2.top + ((this.tempRect.right - this.sidePadding) / this.lockAspectRatio);
                }
                this.tempRect.left = this.sidePadding;
            }
            float f6 = this.sidePadding;
            float topPadding = statusBarHeight + f6;
            float finalBottomPadidng = this.bottomPadding + f6;
            if (this.tempRect.top < topPadding) {
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF3 = this.tempRect;
                    rectF3.right = rectF3.left + ((this.tempRect.bottom - topPadding) * this.lockAspectRatio);
                }
                this.tempRect.top = topPadding;
            } else if (this.tempRect.bottom > getHeight() - finalBottomPadidng) {
                this.tempRect.bottom = getHeight() - finalBottomPadidng;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectF4 = this.tempRect;
                    rectF4.right = rectF4.left + (this.tempRect.height() * this.lockAspectRatio);
                }
            }
            if (this.tempRect.width() < this.minWidth) {
                RectF rectF5 = this.tempRect;
                rectF5.right = rectF5.left + this.minWidth;
            }
            if (this.tempRect.height() < this.minWidth) {
                RectF rectF6 = this.tempRect;
                rectF6.bottom = rectF6.top + this.minWidth;
            }
            float f7 = this.lockAspectRatio;
            if (f7 > 0.0f) {
                if (f7 < 1.0f) {
                    if (this.tempRect.width() <= this.minWidth) {
                        RectF rectF7 = this.tempRect;
                        rectF7.right = rectF7.left + this.minWidth;
                        RectF rectF8 = this.tempRect;
                        rectF8.bottom = rectF8.top + (this.tempRect.width() / this.lockAspectRatio);
                    }
                } else if (this.tempRect.height() <= this.minWidth) {
                    RectF rectF9 = this.tempRect;
                    rectF9.bottom = rectF9.top + this.minWidth;
                    RectF rectF10 = this.tempRect;
                    rectF10.right = rectF10.left + (this.tempRect.height() * this.lockAspectRatio);
                }
            }
            setActualRect(this.tempRect);
            AreaViewListener areaViewListener3 = this.listener;
            if (areaViewListener3 != null) {
                areaViewListener3.onAreaChange();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.Crop.CropAreaView$3 */
    /* loaded from: classes5.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;

        static {
            int[] iArr = new int[Control.values().length];
            $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control = iArr;
            try {
                iArr[Control.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.TOP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.LEFT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private void constrainRectByWidth(RectF rect, float aspectRatio) {
        float w = rect.width();
        float h = w / aspectRatio;
        rect.right = rect.left + w;
        rect.bottom = rect.top + h;
    }

    private void constrainRectByHeight(RectF rect, float aspectRatio) {
        float h = rect.height();
        float w = h * aspectRatio;
        rect.right = rect.left + w;
        rect.bottom = rect.top + h;
    }

    public void getCropRect(RectF rect) {
        rect.set(this.actualRect);
    }
}
