package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Crop.CropRotationWheel;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.Crop.CropView;
/* loaded from: classes5.dex */
public class PhotoCropView extends FrameLayout {
    public CropView cropView;
    private PhotoCropViewDelegate delegate;
    private boolean inBubbleMode;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatorSet thumbAnimation;
    private boolean thumbImageVisible;
    private float thumbImageVisibleProgress;
    private AnimatorSet thumbOverrideAnimation;
    public CropRotationWheel wheelView;
    public boolean isReset = true;
    private boolean thumbImageVisibleOverride = true;
    private float thumbAnimationProgress = 1.0f;
    private float flashAlpha = 0.0f;
    private Paint circlePaint = new Paint(1);
    public final Property<PhotoCropView, Float> ANIMATION_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbAnimationProgress") { // from class: org.telegram.ui.Components.PhotoCropView.1
        public void setValue(PhotoCropView object, float value) {
            PhotoCropView.this.thumbAnimationProgress = value;
            object.invalidate();
        }

        public Float get(PhotoCropView object) {
            return Float.valueOf(PhotoCropView.this.thumbAnimationProgress);
        }
    };
    public final Property<PhotoCropView, Float> PROGRESS_VALUE = new AnimationProperties.FloatProperty<PhotoCropView>("thumbImageVisibleProgress") { // from class: org.telegram.ui.Components.PhotoCropView.2
        public void setValue(PhotoCropView object, float value) {
            PhotoCropView.this.thumbImageVisibleProgress = value;
            object.invalidate();
        }

        public Float get(PhotoCropView object) {
            return Float.valueOf(PhotoCropView.this.thumbImageVisibleProgress);
        }
    };
    private ImageReceiver thumbImageView = new ImageReceiver(this);

    /* loaded from: classes5.dex */
    public interface PhotoCropViewDelegate {
        int getVideoThumbX();

        boolean mirror();

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();

        void onVideoThumbClick();

        boolean rotate();
    }

    public PhotoCropView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.inBubbleMode = context instanceof BubbleActivity;
        CropView cropView = new CropView(context);
        this.cropView = cropView;
        cropView.setListener(new CropView.CropViewListener() { // from class: org.telegram.ui.Components.PhotoCropView.3
            @Override // org.telegram.ui.Components.Crop.CropView.CropViewListener
            public void onChange(boolean reset) {
                PhotoCropView.this.isReset = reset;
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(reset);
                }
            }

            @Override // org.telegram.ui.Components.Crop.CropView.CropViewListener
            public void onUpdate() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onUpdate();
                }
            }

            @Override // org.telegram.ui.Components.Crop.CropView.CropViewListener
            public void onAspectLock(boolean enabled) {
                PhotoCropView.this.wheelView.setAspectLock(enabled);
            }

            @Override // org.telegram.ui.Components.Crop.CropView.CropViewListener
            public void onTapUp() {
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onTapUp();
                }
            }
        });
        this.cropView.setBottomPadding(AndroidUtilities.dp(64.0f));
        addView(this.cropView);
        CropRotationWheel cropRotationWheel = new CropRotationWheel(context);
        this.wheelView = cropRotationWheel;
        cropRotationWheel.setListener(new CropRotationWheel.RotationWheelListener() { // from class: org.telegram.ui.Components.PhotoCropView.4
            @Override // org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener
            public void onStart() {
                PhotoCropView.this.cropView.onRotationBegan();
            }

            @Override // org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener
            public void onChange(float angle) {
                PhotoCropView.this.cropView.setRotation(angle);
                PhotoCropView.this.isReset = false;
                if (PhotoCropView.this.delegate != null) {
                    PhotoCropView.this.delegate.onChange(false);
                }
            }

            @Override // org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener
            public void onEnd(float angle) {
                PhotoCropView.this.cropView.onRotationEnded();
            }

            @Override // org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener
            public void aspectRatioPressed() {
                PhotoCropView.this.cropView.showAspectRatioDialog();
            }

            @Override // org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener
            public boolean rotate90Pressed() {
                if (PhotoCropView.this.delegate != null) {
                    return PhotoCropView.this.delegate.rotate();
                }
                return false;
            }

            @Override // org.telegram.ui.Components.Crop.CropRotationWheel.RotationWheelListener
            public boolean mirror() {
                if (PhotoCropView.this.delegate != null) {
                    return PhotoCropView.this.delegate.mirror();
                }
                return false;
            }
        });
        addView(this.wheelView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.thumbImageVisibleOverride && this.thumbImageVisible && this.thumbImageView.isInsideImage(event.getX(), event.getY())) {
            if (event.getAction() == 1) {
                this.delegate.onVideoThumbClick();
            }
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.thumbImageVisibleOverride && this.thumbImageVisible && this.thumbImageView.isInsideImage(event.getX(), event.getY())) {
            if (event.getAction() == 1) {
                this.delegate.onVideoThumbClick();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        CropView cropView;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.thumbImageVisible && child == (cropView = this.cropView)) {
            RectF rect = cropView.getActualRect();
            int targetSize = AndroidUtilities.dp(32.0f);
            int targetX = (this.delegate.getVideoThumbX() - (targetSize / 2)) + AndroidUtilities.dp(2.0f);
            int targetY = getMeasuredHeight() - AndroidUtilities.dp(156.0f);
            float x = rect.left + ((targetX - rect.left) * this.thumbAnimationProgress);
            float y = rect.top + ((targetY - rect.top) * this.thumbAnimationProgress);
            float size = rect.width() + ((targetSize - rect.width()) * this.thumbAnimationProgress);
            this.thumbImageView.setRoundRadius((int) (size / 2.0f));
            this.thumbImageView.setImageCoords(x, y, size, size);
            this.thumbImageView.setAlpha(this.thumbImageVisibleProgress);
            this.thumbImageView.draw(canvas);
            if (this.flashAlpha > 0.0f) {
                this.circlePaint.setColor(-1);
                this.circlePaint.setAlpha((int) (this.flashAlpha * 255.0f));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f, this.circlePaint);
            }
            this.circlePaint.setColor(getThemedColor(Theme.key_dialogFloatingButton));
            this.circlePaint.setAlpha(Math.min(255, (int) (this.thumbAnimationProgress * 255.0f * this.thumbImageVisibleProgress)));
            canvas.drawCircle((targetSize / 2) + targetX, targetY + targetSize + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(3.0f), this.circlePaint);
        }
        return result;
    }

    public boolean rotate(float diff) {
        CropRotationWheel cropRotationWheel = this.wheelView;
        if (cropRotationWheel != null) {
            cropRotationWheel.reset(false);
        }
        return this.cropView.rotate(diff);
    }

    public boolean mirror() {
        return this.cropView.mirror();
    }

    public void setBitmap(Bitmap bitmap, int rotation, boolean freeform, boolean update, PaintingOverlay paintingOverlay, CropTransform cropTransform, VideoEditTextureView videoView, MediaController.CropState state) {
        requestLayout();
        int i = 0;
        this.thumbImageVisible = false;
        this.thumbImageView.setImageBitmap((Drawable) null);
        this.cropView.setBitmap(bitmap, rotation, freeform, update, paintingOverlay, cropTransform, videoView, state);
        this.wheelView.setFreeform(freeform);
        boolean z = true;
        this.wheelView.reset(true);
        if (state != null) {
            this.wheelView.setRotation(state.cropRotate, false);
            CropRotationWheel cropRotationWheel = this.wheelView;
            if (state.transformRotation == 0) {
                z = false;
            }
            cropRotationWheel.setRotated(z);
            this.wheelView.setMirrored(state.mirrored);
        } else {
            this.wheelView.setRotated(false);
            this.wheelView.setMirrored(false);
        }
        CropRotationWheel cropRotationWheel2 = this.wheelView;
        if (!freeform) {
            i = 4;
        }
        cropRotationWheel2.setVisibility(i);
    }

    public void setVideoThumbFlashAlpha(float alpha) {
        this.flashAlpha = alpha;
        invalidate();
    }

    public Bitmap getVideoThumb() {
        if (!this.thumbImageVisible || !this.thumbImageVisibleOverride) {
            return null;
        }
        return this.thumbImageView.getBitmap();
    }

    public void setVideoThumb(Bitmap bitmap, int orientation) {
        this.thumbImageVisible = bitmap != null;
        this.thumbImageView.setImageBitmap(bitmap);
        this.thumbImageView.setOrientation(orientation, false);
        AnimatorSet animatorSet = this.thumbAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.thumbOverrideAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.thumbImageVisibleOverride = true;
        this.thumbImageVisibleProgress = 1.0f;
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.thumbAnimation = animatorSet3;
        animatorSet3.playTogether(ObjectAnimator.ofFloat(this, this.ANIMATION_VALUE, 0.0f, 1.0f));
        this.thumbAnimation.setDuration(250L);
        this.thumbAnimation.setInterpolator(new OvershootInterpolator(1.01f));
        this.thumbAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhotoCropView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PhotoCropView.this.thumbAnimation = null;
            }
        });
        this.thumbAnimation.start();
    }

    public void cancelThumbAnimation() {
        AnimatorSet animatorSet = this.thumbAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.thumbAnimation = null;
            this.thumbImageVisible = false;
        }
    }

    public void setVideoThumbVisible(boolean visible) {
        if (this.thumbImageVisibleOverride == visible) {
            return;
        }
        this.thumbImageVisibleOverride = visible;
        AnimatorSet animatorSet = this.thumbOverrideAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.thumbOverrideAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[1];
        Property<PhotoCropView, Float> property = this.PROGRESS_VALUE;
        float[] fArr = new float[1];
        fArr[0] = visible ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(this, property, fArr);
        animatorSet2.playTogether(animatorArr);
        this.thumbOverrideAnimation.setDuration(180L);
        this.thumbOverrideAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhotoCropView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PhotoCropView.this.thumbOverrideAnimation = null;
            }
        });
        this.thumbOverrideAnimation.start();
    }

    public boolean isReady() {
        return this.cropView.isReady();
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean force) {
        this.wheelView.reset(true);
        this.cropView.reset(force);
    }

    public void onAppear() {
        this.cropView.willShow();
    }

    public void setAspectRatio(float ratio) {
        this.cropView.setAspectRatio(ratio);
    }

    public void setFreeform(boolean freeform) {
        this.cropView.setFreeform(freeform);
    }

    public void onAppeared() {
        this.cropView.show();
    }

    public void onDisappear() {
        this.cropView.hide();
    }

    public void onShow() {
        this.cropView.onShow();
    }

    public void onHide() {
        this.cropView.onHide();
    }

    public float getRectX() {
        return this.cropView.getCropLeft() - AndroidUtilities.dp(14.0f);
    }

    public float getRectY() {
        return (this.cropView.getCropTop() - AndroidUtilities.dp(14.0f)) - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
    }

    public float getRectSizeX() {
        return this.cropView.getCropWidth();
    }

    public float getRectSizeY() {
        return this.cropView.getCropHeight();
    }

    public void makeCrop(MediaController.MediaEditState editState) {
        this.cropView.makeCrop(editState);
    }

    public void setDelegate(PhotoCropViewDelegate photoCropViewDelegate) {
        this.delegate = photoCropViewDelegate;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.cropView.updateLayout();
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.cropView.invalidate();
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
