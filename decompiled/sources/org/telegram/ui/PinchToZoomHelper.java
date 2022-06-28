package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class PinchToZoomHelper {
    Callback callback;
    private View child;
    private ImageReceiver childImage;
    ClipBoundsListener clipBoundsListener;
    private float enterProgress;
    private float finishProgress;
    ValueAnimator finishTransition;
    float fragmentOffsetX;
    float fragmentOffsetY;
    private final ViewGroup fragmentView;
    private float fullImageHeight;
    private float fullImageWidth;
    private float imageHeight;
    private float imageWidth;
    private float imageX;
    private float imageY;
    private boolean inOverlayMode;
    private boolean isHardwareVideo;
    boolean isInPinchToZoomTouchMode;
    private MessageObject messageObject;
    private ZoomOverlayView overlayView;
    float parentOffsetX;
    float parentOffsetY;
    private final ViewGroup parentView;
    float pinchCenterX;
    float pinchCenterY;
    float pinchScale;
    float pinchStartCenterX;
    float pinchStartCenterY;
    float pinchStartDistance;
    float pinchTranslationX;
    float pinchTranslationY;
    private int pointerId1;
    private int pointerId2;
    private float progressToFullView;
    private ImageReceiver fullImage = new ImageReceiver();
    private float[] clipTopBottom = new float[2];

    /* loaded from: classes4.dex */
    public interface ClipBoundsListener {
        void getClipTopBottom(float[] fArr);
    }

    static /* synthetic */ float access$1416(PinchToZoomHelper x0, float x1) {
        float f = x0.progressToFullView + x1;
        x0.progressToFullView = f;
        return f;
    }

    static /* synthetic */ float access$616(PinchToZoomHelper x0, float x1) {
        float f = x0.enterProgress + x1;
        x0.enterProgress = f;
        return f;
    }

    public PinchToZoomHelper(ViewGroup parentView, ViewGroup fragmentView) {
        this.parentView = parentView;
        this.fragmentView = fragmentView;
    }

    public void startZoom(View child, ImageReceiver image, MessageObject messageObject) {
        this.child = child;
        this.messageObject = messageObject;
        if (this.overlayView == null) {
            ZoomOverlayView zoomOverlayView = new ZoomOverlayView(this.parentView.getContext());
            this.overlayView = zoomOverlayView;
            zoomOverlayView.setFocusable(false);
            this.overlayView.setFocusableInTouchMode(false);
            this.overlayView.setEnabled(false);
        }
        if (this.fullImage == null) {
            ImageReceiver imageReceiver = new ImageReceiver();
            this.fullImage = imageReceiver;
            imageReceiver.setCrossfadeAlpha((byte) 2);
            this.fullImage.setCrossfadeWithOldImage(false);
            this.fullImage.onAttachedToWindow();
        }
        this.inOverlayMode = true;
        this.parentView.addView(this.overlayView);
        this.finishProgress = 1.0f;
        this.progressToFullView = 0.0f;
        setFullImage(messageObject);
        this.imageX = image.getImageX();
        this.imageY = image.getImageY();
        this.imageHeight = image.getImageHeight();
        this.imageWidth = image.getImageWidth();
        this.fullImageHeight = image.getBitmapHeight();
        float bitmapWidth = image.getBitmapWidth();
        this.fullImageWidth = bitmapWidth;
        float f = this.fullImageHeight;
        float f2 = this.imageHeight;
        float f3 = this.imageWidth;
        if (f / bitmapWidth != f2 / f3) {
            if (f / bitmapWidth < f2 / f3) {
                this.fullImageWidth = (bitmapWidth / f) * f2;
                this.fullImageHeight = f2;
            } else {
                this.fullImageHeight = (f / bitmapWidth) * f3;
                this.fullImageWidth = f3;
            }
        } else {
            this.fullImageHeight = f2;
            this.fullImageWidth = f3;
        }
        if (messageObject != null && messageObject.isVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
            this.isHardwareVideo = true;
            MediaController.getInstance().setTextureView(this.overlayView.videoTextureView, this.overlayView.aspectRatioFrameLayout, this.overlayView.videoPlayerContainer, true);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.overlayView.videoPlayerContainer.getLayoutParams();
            this.overlayView.videoPlayerContainer.setTag(R.id.parent_tag, image);
            if (layoutParams.width != image.getImageWidth() || layoutParams.height != image.getImageHeight()) {
                this.overlayView.aspectRatioFrameLayout.setResizeMode(3);
                layoutParams.width = (int) image.getImageWidth();
                layoutParams.height = (int) image.getImageHeight();
                this.overlayView.videoPlayerContainer.setLayoutParams(layoutParams);
            }
            this.overlayView.videoTextureView.setScaleX(1.0f);
            this.overlayView.videoTextureView.setScaleY(1.0f);
            if (this.callback != null) {
                this.overlayView.backupImageView.setImageBitmap(this.callback.getCurrentTextureView().getBitmap((int) this.fullImageWidth, (int) this.fullImageHeight));
                this.overlayView.backupImageView.setSize((int) this.fullImageWidth, (int) this.fullImageHeight);
                this.overlayView.backupImageView.getImageReceiver().setRoundRadius(image.getRoundRadius());
            }
            this.overlayView.videoPlayerContainer.setVisibility(0);
        } else {
            this.isHardwareVideo = false;
            ImageReceiver imageReceiver2 = new ImageReceiver();
            this.childImage = imageReceiver2;
            imageReceiver2.onAttachedToWindow();
            Drawable drawable = image.getDrawable();
            this.childImage.setImageBitmap(drawable);
            if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).addSecondParentView(this.overlayView);
                ((AnimatedFileDrawable) drawable).setInvalidateParentViewWithSecond(true);
            }
            this.childImage.setImageCoords(this.imageX, this.imageY, this.imageWidth, this.imageHeight);
            this.childImage.setRoundRadius(image.getRoundRadius());
            this.fullImage.setRoundRadius(image.getRoundRadius());
            this.overlayView.videoPlayerContainer.setVisibility(8);
        }
        Callback callback = this.callback;
        if (callback != null) {
            callback.onZoomStarted(messageObject);
        }
        this.enterProgress = 0.0f;
    }

    private void setFullImage(MessageObject messageObject) {
        if (messageObject == null || !messageObject.isPhoto()) {
            return;
        }
        int[] size = new int[1];
        ImageLocation imageLocation = getImageLocation(messageObject, size);
        if (imageLocation != null) {
            boolean cacheOnly = messageObject != null && messageObject.isWebpage();
            this.fullImage.setImage(imageLocation, null, null, null, null, size[0], null, messageObject, cacheOnly ? 1 : 0);
            this.fullImage.setCrossfadeAlpha((byte) 2);
        }
        updateViewsLocation();
    }

    public boolean updateViewsLocation() {
        float parentOffsetX = 0.0f;
        float parentOffsetY = 0.0f;
        for (View currentView = this.child; currentView != this.parentView; currentView = (View) currentView.getParent()) {
            if (currentView == null) {
                return false;
            }
            parentOffsetX += currentView.getLeft();
            parentOffsetY += currentView.getTop();
        }
        float fragmentOffsetX = 0.0f;
        float fragmentOffsetY = 0.0f;
        for (View currentView2 = this.child; currentView2 != this.fragmentView; currentView2 = (View) currentView2.getParent()) {
            if (currentView2 == null) {
                return false;
            }
            fragmentOffsetX += currentView2.getLeft();
            fragmentOffsetY += currentView2.getTop();
        }
        this.fragmentOffsetX = fragmentOffsetX;
        this.fragmentOffsetY = fragmentOffsetY;
        this.parentOffsetX = parentOffsetX;
        this.parentOffsetY = parentOffsetY;
        return true;
    }

    public void finishZoom() {
        if (this.finishTransition != null || !this.inOverlayMode) {
            return;
        }
        if (!updateViewsLocation()) {
            clear();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
        this.finishTransition = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PinchToZoomHelper$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PinchToZoomHelper.this.m4304lambda$finishZoom$0$orgtelegramuiPinchToZoomHelper(valueAnimator);
            }
        });
        this.finishTransition.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PinchToZoomHelper.1
            {
                PinchToZoomHelper.this = this;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (PinchToZoomHelper.this.finishTransition != null) {
                    PinchToZoomHelper.this.finishTransition = null;
                    PinchToZoomHelper.this.clear();
                }
            }
        });
        this.finishTransition.setDuration(220L);
        this.finishTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.finishTransition.start();
    }

    /* renamed from: lambda$finishZoom$0$org-telegram-ui-PinchToZoomHelper */
    public /* synthetic */ void m4304lambda$finishZoom$0$orgtelegramuiPinchToZoomHelper(ValueAnimator valueAnimator) {
        this.finishProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateViews();
    }

    public void clear() {
        if (this.inOverlayMode) {
            Callback callback = this.callback;
            if (callback != null) {
                callback.onZoomFinished(this.messageObject);
            }
            this.inOverlayMode = false;
        }
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (zoomOverlayView != null && zoomOverlayView.getParent() != null) {
            this.parentView.removeView(this.overlayView);
            this.overlayView.backupImageView.getImageReceiver().clearImage();
            ImageReceiver imageReceiver = this.childImage;
            if (imageReceiver != null) {
                Drawable drawable = imageReceiver.getDrawable();
                if (drawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable).removeSecondParentView(this.overlayView);
                }
            }
        }
        View view = this.child;
        if (view != null) {
            view.invalidate();
            this.child = null;
        }
        ImageReceiver imageReceiver2 = this.childImage;
        if (imageReceiver2 != null) {
            imageReceiver2.onDetachedFromWindow();
            this.childImage.clearImage();
            this.childImage = null;
        }
        ImageReceiver imageReceiver3 = this.fullImage;
        if (imageReceiver3 != null) {
            imageReceiver3.onDetachedFromWindow();
            this.fullImage.clearImage();
            this.fullImage = null;
        }
        this.messageObject = null;
    }

    public boolean inOverlayMode() {
        return this.inOverlayMode;
    }

    public boolean isInOverlayMode() {
        return this.inOverlayMode;
    }

    public boolean isInOverlayModeFor(View child) {
        return this.inOverlayMode && child == this.child;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (updateViewsLocation() && this.child != null) {
            ev.offsetLocation(-this.fragmentOffsetX, -this.fragmentOffsetY);
            return this.child.onTouchEvent(ev);
        }
        return false;
    }

    public Bitmap getVideoBitmap(int w, int h) {
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (zoomOverlayView == null) {
            return null;
        }
        return zoomOverlayView.videoTextureView.getBitmap(w, h);
    }

    public ImageReceiver getPhotoImage() {
        return this.childImage;
    }

    public boolean zoomEnabled(View child, ImageReceiver receiver) {
        Drawable drawable = receiver.getDrawable();
        if (drawable instanceof AnimatedFileDrawable) {
            if (((AnimatedFileDrawable) receiver.getDrawable()).isLoadingStream()) {
                return false;
            }
            return true;
        }
        return receiver.hasNotThumb();
    }

    /* loaded from: classes4.dex */
    public class ZoomOverlayView extends FrameLayout {
        private Paint aspectPaint;
        private Path aspectPath;
        private AspectRatioFrameLayout aspectRatioFrameLayout;
        private BackupImageView backupImageView;
        private FrameLayout videoPlayerContainer;
        private TextureView videoTextureView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ZoomOverlayView(Context context) {
            super(context);
            PinchToZoomHelper.this = r5;
            this.aspectPath = new Path();
            this.aspectPaint = new Paint(1);
            if (Build.VERSION.SDK_INT >= 21) {
                FrameLayout frameLayout = new FrameLayout(context);
                this.videoPlayerContainer = frameLayout;
                frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.PinchToZoomHelper.ZoomOverlayView.1
                    {
                        ZoomOverlayView.this = this;
                    }

                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        ImageReceiver imageReceiver = (ImageReceiver) view.getTag(R.id.parent_tag);
                        if (imageReceiver != null) {
                            int[] rad = imageReceiver.getRoundRadius();
                            int maxRad = 0;
                            for (int a = 0; a < 4; a++) {
                                maxRad = Math.max(maxRad, rad[a]);
                            }
                            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), maxRad);
                            return;
                        }
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.videoPlayerContainer.setClipToOutline(true);
            } else {
                this.videoPlayerContainer = new FrameLayout(context) { // from class: org.telegram.ui.PinchToZoomHelper.ZoomOverlayView.2
                    RectF rect = new RectF();

                    {
                        ZoomOverlayView.this = this;
                    }

                    @Override // android.view.View
                    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ZoomOverlayView.this.aspectPath.reset();
                        ImageReceiver imageReceiver = (ImageReceiver) getTag(R.id.parent_tag);
                        if (imageReceiver == null) {
                            ZoomOverlayView.this.aspectPath.addCircle(w / 2, h / 2, w / 2, Path.Direction.CW);
                        } else {
                            int[] rad = imageReceiver.getRoundRadius();
                            int maxRad = 0;
                            for (int a = 0; a < 4; a++) {
                                maxRad = Math.max(maxRad, rad[a]);
                            }
                            this.rect.set(0.0f, 0.0f, w, h);
                            ZoomOverlayView.this.aspectPath.addRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Path.Direction.CW);
                        }
                        ZoomOverlayView.this.aspectPath.toggleInverseFillType();
                    }

                    @Override // android.view.View
                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
                            setLayerType(2, null);
                        }
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        if (getTag() == null) {
                            canvas.drawPath(ZoomOverlayView.this.aspectPath, ZoomOverlayView.this.aspectPaint);
                        }
                    }
                };
                this.aspectPath = new Path();
                Paint paint = new Paint(1);
                this.aspectPaint = paint;
                paint.setColor(-16777216);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            BackupImageView backupImageView = new BackupImageView(context);
            this.backupImageView = backupImageView;
            this.videoPlayerContainer.addView(backupImageView);
            this.videoPlayerContainer.setWillNotDraw(false);
            AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(context);
            this.aspectRatioFrameLayout = aspectRatioFrameLayout;
            aspectRatioFrameLayout.setBackgroundColor(0);
            this.videoPlayerContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
            TextureView textureView = new TextureView(context);
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
            addView(this.videoPlayerContainer, LayoutHelper.createFrame(-2, -2.0f));
            setWillNotDraw(false);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (PinchToZoomHelper.this.finishTransition == null && PinchToZoomHelper.this.enterProgress != 1.0f) {
                PinchToZoomHelper.access$616(PinchToZoomHelper.this, 0.07272727f);
                if (PinchToZoomHelper.this.enterProgress > 1.0f) {
                    PinchToZoomHelper.this.enterProgress = 1.0f;
                } else {
                    PinchToZoomHelper.this.invalidateViews();
                }
            }
            float progress = PinchToZoomHelper.this.finishProgress * CubicBezierInterpolator.DEFAULT.getInterpolation(PinchToZoomHelper.this.enterProgress);
            float clipTop = 0.0f;
            float clipBottom = getMeasuredHeight();
            if (progress != 1.0f && PinchToZoomHelper.this.clipBoundsListener != null) {
                PinchToZoomHelper.this.clipBoundsListener.getClipTopBottom(PinchToZoomHelper.this.clipTopBottom);
                canvas.save();
                clipTop = PinchToZoomHelper.this.clipTopBottom[0] * (1.0f - progress);
                clipBottom = (PinchToZoomHelper.this.clipTopBottom[1] * (1.0f - progress)) + (getMeasuredHeight() * progress);
                canvas.clipRect(0.0f, clipTop, getMeasuredWidth(), clipBottom);
                drawImage(canvas);
                super.dispatchDraw(canvas);
                canvas.restore();
            } else {
                drawImage(canvas);
                super.dispatchDraw(canvas);
            }
            float parentOffsetX = PinchToZoomHelper.this.parentOffsetX - getLeft();
            float parentOffsetY = PinchToZoomHelper.this.parentOffsetY - getTop();
            PinchToZoomHelper.this.drawOverlays(canvas, 1.0f - progress, parentOffsetX, parentOffsetY, clipTop, clipBottom);
        }

        private void drawImage(Canvas canvas) {
            float p;
            if (PinchToZoomHelper.this.inOverlayMode && PinchToZoomHelper.this.child != null && PinchToZoomHelper.this.parentView != null) {
                PinchToZoomHelper.this.updateViewsLocation();
                float parentOffsetX = PinchToZoomHelper.this.parentOffsetX - getLeft();
                float parentOffsetY = PinchToZoomHelper.this.parentOffsetY - getTop();
                canvas.save();
                float s = ((PinchToZoomHelper.this.pinchScale * PinchToZoomHelper.this.finishProgress) + 1.0f) - PinchToZoomHelper.this.finishProgress;
                canvas.scale(s, s, PinchToZoomHelper.this.pinchCenterX + parentOffsetX, PinchToZoomHelper.this.pinchCenterY + parentOffsetY);
                canvas.translate((PinchToZoomHelper.this.pinchTranslationX * PinchToZoomHelper.this.finishProgress) + parentOffsetX, (PinchToZoomHelper.this.pinchTranslationY * PinchToZoomHelper.this.finishProgress) + parentOffsetY);
                if (PinchToZoomHelper.this.fullImage != null && PinchToZoomHelper.this.fullImage.hasNotThumb()) {
                    if (PinchToZoomHelper.this.progressToFullView != 1.0f) {
                        PinchToZoomHelper.access$1416(PinchToZoomHelper.this, 0.10666667f);
                        if (PinchToZoomHelper.this.progressToFullView > 1.0f) {
                            PinchToZoomHelper.this.progressToFullView = 1.0f;
                        } else {
                            PinchToZoomHelper.this.invalidateViews();
                        }
                    }
                    PinchToZoomHelper.this.fullImage.setAlpha(PinchToZoomHelper.this.progressToFullView);
                }
                float x = PinchToZoomHelper.this.imageX;
                float y = PinchToZoomHelper.this.imageY;
                if (PinchToZoomHelper.this.imageHeight != PinchToZoomHelper.this.fullImageHeight || PinchToZoomHelper.this.imageWidth != PinchToZoomHelper.this.fullImageWidth) {
                    if (s < 1.0f) {
                        p = 0.0f;
                    } else if (s < 1.4f) {
                        p = (s - 1.0f) / 0.4f;
                    } else {
                        p = 1.0f;
                    }
                    float verticalPadding = (PinchToZoomHelper.this.fullImageHeight - PinchToZoomHelper.this.imageHeight) / 2.0f;
                    float horizontalPadding = (PinchToZoomHelper.this.fullImageWidth - PinchToZoomHelper.this.imageWidth) / 2.0f;
                    x = PinchToZoomHelper.this.imageX - (horizontalPadding * p);
                    y = PinchToZoomHelper.this.imageY - (verticalPadding * p);
                    if (PinchToZoomHelper.this.childImage != null) {
                        PinchToZoomHelper.this.childImage.setImageCoords(x, y, PinchToZoomHelper.this.imageWidth + (horizontalPadding * p * 2.0f), PinchToZoomHelper.this.imageHeight + (verticalPadding * p * 2.0f));
                    }
                }
                if (!PinchToZoomHelper.this.isHardwareVideo) {
                    if (PinchToZoomHelper.this.childImage != null) {
                        if (PinchToZoomHelper.this.progressToFullView != 1.0f) {
                            PinchToZoomHelper.this.childImage.draw(canvas);
                            PinchToZoomHelper.this.fullImage.setImageCoords(PinchToZoomHelper.this.childImage.getImageX(), PinchToZoomHelper.this.childImage.getImageY(), PinchToZoomHelper.this.childImage.getImageWidth(), PinchToZoomHelper.this.childImage.getImageHeight());
                            PinchToZoomHelper.this.fullImage.draw(canvas);
                        } else {
                            PinchToZoomHelper.this.fullImage.setImageCoords(PinchToZoomHelper.this.childImage.getImageX(), PinchToZoomHelper.this.childImage.getImageY(), PinchToZoomHelper.this.childImage.getImageWidth(), PinchToZoomHelper.this.childImage.getImageHeight());
                            PinchToZoomHelper.this.fullImage.draw(canvas);
                        }
                    }
                } else {
                    this.videoPlayerContainer.setPivotX(PinchToZoomHelper.this.pinchCenterX - PinchToZoomHelper.this.imageX);
                    this.videoPlayerContainer.setPivotY(PinchToZoomHelper.this.pinchCenterY - PinchToZoomHelper.this.imageY);
                    this.videoPlayerContainer.setScaleY(s);
                    this.videoPlayerContainer.setScaleX(s);
                    this.videoPlayerContainer.setTranslationX(x + parentOffsetX + (PinchToZoomHelper.this.pinchTranslationX * s * PinchToZoomHelper.this.finishProgress));
                    this.videoPlayerContainer.setTranslationY(y + parentOffsetY + (PinchToZoomHelper.this.pinchTranslationY * s * PinchToZoomHelper.this.finishProgress));
                }
                canvas.restore();
            }
        }
    }

    protected void drawOverlays(Canvas canvas, float alpha, float parentOffsetX, float parentOffsetY, float clipTop, float clipBottom) {
    }

    private ImageLocation getImageLocation(MessageObject message, int[] size) {
        if (message.messageOwner instanceof TLRPC.TL_messageService) {
            if (message.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                return null;
            }
            TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                if (size != null) {
                    size[0] = sizeFull.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                }
                return ImageLocation.getForObject(sizeFull, message.photoThumbsObject);
            } else if (size != null) {
                size[0] = -1;
            }
        } else if (((message.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && message.messageOwner.media.photo != null) || ((message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && message.messageOwner.media.webpage != null)) {
            if (message.isGif()) {
                return ImageLocation.getForDocument(message.getDocument());
            }
            TLRPC.PhotoSize sizeFull2 = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, AndroidUtilities.getPhotoSize(), false, null, true);
            if (sizeFull2 != null) {
                if (size != null) {
                    size[0] = sizeFull2.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                }
                return ImageLocation.getForObject(sizeFull2, message.photoThumbsObject);
            } else if (size != null) {
                size[0] = -1;
            }
        } else if (message.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
            return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC.TL_messageMediaInvoice) message.messageOwner.media).photo));
        } else {
            if (message.getDocument() != null) {
                TLRPC.Document document = message.getDocument();
                if (MessageObject.isDocumentHasThumb(message.getDocument())) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if (size != null) {
                        size[0] = thumb.size;
                        if (size[0] == 0) {
                            size[0] = -1;
                        }
                    }
                    return ImageLocation.getForDocument(thumb, document);
                }
            }
        }
        return null;
    }

    public void setClipBoundsListener(ClipBoundsListener clipBoundsListener) {
        this.clipBoundsListener = clipBoundsListener;
    }

    /* loaded from: classes4.dex */
    public interface Callback {
        TextureView getCurrentTextureView();

        void onZoomFinished(MessageObject messageObject);

        void onZoomStarted(MessageObject messageObject);

        /* renamed from: org.telegram.ui.PinchToZoomHelper$Callback$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static TextureView $default$getCurrentTextureView(Callback _this) {
                return null;
            }

            public static void $default$onZoomStarted(Callback _this, MessageObject messageObject) {
            }

            public static void $default$onZoomFinished(Callback _this, MessageObject messageObject) {
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean checkPinchToZoom(MotionEvent ev, View child, ImageReceiver image, MessageObject messageObject) {
        if (!zoomEnabled(child, image)) {
            return false;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (!this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                float x = (ev.getX(0) + ev.getX(1)) / 2.0f;
                this.pinchCenterX = x;
                this.pinchStartCenterX = x;
                float y = (ev.getY(0) + ev.getY(1)) / 2.0f;
                this.pinchCenterY = y;
                this.pinchStartCenterY = y;
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
        } else if (ev.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int index1 = -1;
            int index2 = -1;
            for (int i = 0; i < ev.getPointerCount(); i++) {
                if (this.pointerId1 == ev.getPointerId(i)) {
                    index1 = i;
                }
                if (this.pointerId2 == ev.getPointerId(i)) {
                    index2 = i;
                }
            }
            if (index1 == -1 || index2 == -1) {
                this.isInPinchToZoomTouchMode = false;
                child.getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
                return false;
            }
            float hypot = ((float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (hypot > 1.005f && !isInOverlayMode()) {
                this.pinchStartDistance = (float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1));
                float x2 = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
                this.pinchCenterX = x2;
                this.pinchStartCenterX = x2;
                float y2 = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
                this.pinchCenterY = y2;
                this.pinchStartCenterY = y2;
                this.pinchScale = 1.0f;
                this.pinchTranslationX = 0.0f;
                this.pinchTranslationY = 0.0f;
                child.getParent().requestDisallowInterceptTouchEvent(true);
                startZoom(child, image, messageObject);
            }
            float newPinchCenterX = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
            float newPinchCenterY = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
            float moveDx = this.pinchStartCenterX - newPinchCenterX;
            float moveDy = this.pinchStartCenterY - newPinchCenterY;
            float f = this.pinchScale;
            this.pinchTranslationX = (-moveDx) / f;
            this.pinchTranslationY = (-moveDy) / f;
            invalidateViews();
        } else {
            int index12 = ev.getActionMasked();
            if ((index12 == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
                this.isInPinchToZoomTouchMode = false;
                child.getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
            }
        }
        return isInOverlayModeFor(child);
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0);
    }

    public void invalidateViews() {
        ZoomOverlayView zoomOverlayView = this.overlayView;
        if (zoomOverlayView != null) {
            zoomOverlayView.invalidate();
        }
    }

    public View getChild() {
        return this.child;
    }
}
