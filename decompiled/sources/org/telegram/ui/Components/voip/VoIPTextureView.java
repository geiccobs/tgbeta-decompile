package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.os.Build;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;
/* loaded from: classes5.dex */
public class VoIPTextureView extends FrameLayout {
    int animateFromHeight;
    float animateFromRendererH;
    float animateFromRendererW;
    float animateFromThumbScale;
    int animateFromWidth;
    float animateFromX;
    float animateFromY;
    long animateNextDuration;
    boolean animateOnNextLayout;
    ArrayList<Animator> animateOnNextLayoutAnimations;
    boolean animateWithParent;
    public float animationProgress;
    float aninateFromScale;
    float aninateFromScaleBlur;
    final boolean applyRotation;
    boolean applyRoundRadius;
    public View backgroundView;
    public TextureView blurRenderer;
    public Bitmap cameraLastBitmap;
    float clipHorizontal;
    boolean clipToTexture;
    float clipVertical;
    ValueAnimator currentAnimation;
    float currentClipHorizontal;
    float currentClipVertical;
    float currentThumbScale;
    boolean ignoreLayout;
    public final ImageView imageView;
    final boolean isCamera;
    public final TextureViewRenderer renderer;
    float roundRadius;
    public float scaleTextureToFill;
    private float scaleTextureToFillBlur;
    private float scaleThumb;
    public int scaleType;
    private boolean screencast;
    private ImageView screencastImage;
    private TextView screencastText;
    private FrameLayout screencastView;
    public float stubVisibleProgress;
    private Bitmap thumb;
    public static int SCALE_TYPE_NONE = 3;
    public static int SCALE_TYPE_FILL = 0;
    public static int SCALE_TYPE_FIT = 1;
    public static int SCALE_TYPE_ADAPTIVE = 2;

    public VoIPTextureView(Context context, boolean isCamera, boolean applyRotation) {
        this(context, isCamera, applyRotation, true, false);
    }

    public VoIPTextureView(Context context, boolean isCamera, boolean applyRotation, boolean applyRoundRadius, boolean blurBackground) {
        super(context);
        this.stubVisibleProgress = 1.0f;
        this.animateOnNextLayoutAnimations = new ArrayList<>();
        this.aninateFromScale = 1.0f;
        this.aninateFromScaleBlur = 1.0f;
        this.animateFromThumbScale = 1.0f;
        this.isCamera = isCamera;
        this.applyRotation = applyRotation;
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        TextureViewRenderer textureViewRenderer = new TextureViewRenderer(context) { // from class: org.telegram.ui.Components.voip.VoIPTextureView.1
            @Override // org.webrtc.TextureViewRenderer, org.webrtc.RendererCommon.RendererEvents
            public void onFirstFrameRendered() {
                super.onFirstFrameRendered();
                VoIPTextureView.this.onFirstFrameRendered();
            }

            @Override // android.view.TextureView, android.view.View
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
            }
        };
        this.renderer = textureViewRenderer;
        textureViewRenderer.setFpsReduction(30.0f);
        textureViewRenderer.setOpaque(false);
        textureViewRenderer.setEnableHardwareScaler(true);
        textureViewRenderer.setIsCamera(!applyRotation);
        if (!isCamera && applyRotation) {
            View view = new View(context);
            this.backgroundView = view;
            view.setBackgroundColor(-14999773);
            addView(this.backgroundView, LayoutHelper.createFrame(-1, -1.0f));
            if (blurBackground) {
                TextureView textureView = new TextureView(context);
                this.blurRenderer = textureView;
                addView(textureView, LayoutHelper.createFrame(-1, -2, 17));
            }
            textureViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            addView(textureViewRenderer, LayoutHelper.createFrame(-1, -2, 17));
        } else if (!isCamera) {
            if (blurBackground) {
                TextureView textureView2 = new TextureView(context);
                this.blurRenderer = textureView2;
                addView(textureView2, LayoutHelper.createFrame(-1, -2, 17));
            }
            addView(textureViewRenderer, LayoutHelper.createFrame(-1, -2, 17));
        } else {
            if (blurBackground) {
                TextureView textureView3 = new TextureView(context);
                this.blurRenderer = textureView3;
                addView(textureView3, LayoutHelper.createFrame(-1, -2, 17));
            }
            addView(textureViewRenderer);
        }
        addView(imageView);
        TextureView textureView4 = this.blurRenderer;
        if (textureView4 != null) {
            textureView4.setOpaque(false);
        }
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.screencastView = frameLayout;
        frameLayout.setBackground(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
        addView(this.screencastView, LayoutHelper.createFrame(-1, -1.0f));
        this.screencastView.setVisibility(8);
        ImageView imageView2 = new ImageView(getContext());
        this.screencastImage = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.screencastImage.setImageResource(R.drawable.screencast_big);
        this.screencastView.addView(this.screencastImage, LayoutHelper.createFrame(82, 82.0f, 17, 0.0f, 0.0f, 0.0f, 60.0f));
        TextView textView = new TextView(getContext());
        this.screencastText = textView;
        textView.setText(LocaleController.getString("VoipVideoScreenSharing", R.string.VoipVideoScreenSharing));
        this.screencastText.setGravity(17);
        this.screencastText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        this.screencastText.setTextColor(-1);
        this.screencastText.setTextSize(1, 15.0f);
        this.screencastText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.screencastView.addView(this.screencastText, LayoutHelper.createFrame(-1, -2.0f, 17, 21.0f, 28.0f, 21.0f, 0.0f));
        if (applyRoundRadius && Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.voip.VoIPTextureView.2
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view2, Outline outline) {
                    if (VoIPTextureView.this.roundRadius < 1.0f) {
                        outline.setRect((int) VoIPTextureView.this.currentClipHorizontal, (int) VoIPTextureView.this.currentClipVertical, (int) (view2.getMeasuredWidth() - VoIPTextureView.this.currentClipHorizontal), (int) (view2.getMeasuredHeight() - VoIPTextureView.this.currentClipVertical));
                    } else {
                        outline.setRoundRect((int) VoIPTextureView.this.currentClipHorizontal, (int) VoIPTextureView.this.currentClipVertical, (int) (view2.getMeasuredWidth() - VoIPTextureView.this.currentClipHorizontal), (int) (view2.getMeasuredHeight() - VoIPTextureView.this.currentClipVertical), VoIPTextureView.this.roundRadius);
                    }
                }
            });
            setClipToOutline(true);
        }
        if (isCamera && this.cameraLastBitmap == null) {
            try {
                File file = new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg");
                Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());
                this.cameraLastBitmap = decodeFile;
                if (decodeFile == null) {
                    File file2 = new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg");
                    this.cameraLastBitmap = BitmapFactory.decodeFile(file2.getAbsolutePath());
                }
                imageView.setImageBitmap(this.cameraLastBitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Throwable th) {
            }
        }
        if (!applyRotation) {
            Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
            this.renderer.setScreenRotation(display.getRotation());
        }
    }

    public void setScreenshareMiniProgress(float progress, boolean value) {
        float sc;
        if (!this.screencast) {
            return;
        }
        float scale = ((View) getParent()).getScaleX();
        this.screencastText.setAlpha(1.0f - progress);
        if (!value) {
            sc = (1.0f / scale) - ((0.4f / scale) * progress);
        } else {
            sc = 1.0f - (0.4f * progress);
        }
        this.screencastImage.setScaleX(sc);
        this.screencastImage.setScaleY(sc);
        this.screencastImage.setTranslationY(AndroidUtilities.dp(60.0f) * progress);
    }

    public void setIsScreencast(boolean value) {
        this.screencast = value;
        this.screencastView.setVisibility(value ? 0 : 8);
        if (this.screencast) {
            this.renderer.setVisibility(8);
            TextureView textureView = this.blurRenderer;
            if (textureView != null) {
                textureView.setVisibility(8);
            }
            this.imageView.setVisibility(8);
            return;
        }
        this.renderer.setVisibility(0);
        TextureView textureView2 = this.blurRenderer;
        if (textureView2 != null) {
            textureView2.setVisibility(0);
        }
    }

    protected void onFirstFrameRendered() {
        invalidate();
        if (this.renderer.getAlpha() != 1.0f) {
            this.renderer.animate().setDuration(300L).alpha(1.0f);
        }
        TextureView textureView = this.blurRenderer;
        if (textureView != null && textureView.getAlpha() != 1.0f) {
            this.blurRenderer.animate().setDuration(300L).alpha(1.0f);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.imageView.getVisibility() == 0 && this.renderer.isFirstFrameRendered()) {
            float f = this.stubVisibleProgress - 0.10666667f;
            this.stubVisibleProgress = f;
            if (f <= 0.0f) {
                this.stubVisibleProgress = 0.0f;
                this.imageView.setVisibility(8);
                return;
            }
            invalidate();
            this.imageView.setAlpha(this.stubVisibleProgress);
        }
    }

    public void setRoundCorners(float radius) {
        if (this.roundRadius != radius) {
            this.roundRadius = radius;
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline();
            } else {
                invalidate();
            }
        }
    }

    public void saveCameraLastBitmap() {
        Bitmap bitmap = this.renderer.getBitmap(150, 150);
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
            try {
                File file = new File(ApplicationLoader.getFilesDirFixed(), "voip_icthumb.jpg");
                FileOutputStream stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.close();
            } catch (Throwable th) {
            }
        }
    }

    public void setStub(VoIPTextureView from) {
        if (this.screencast) {
            return;
        }
        Bitmap bitmap = from.renderer.getBitmap();
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            this.imageView.setImageDrawable(from.imageView.getDrawable());
        } else {
            this.imageView.setImageBitmap(bitmap);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        this.stubVisibleProgress = 1.0f;
        this.imageView.setVisibility(0);
        this.imageView.setAlpha(1.0f);
    }

    public void animateToLayout() {
        if (this.animateOnNextLayout || getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            return;
        }
        this.animateFromHeight = getMeasuredHeight();
        this.animateFromWidth = getMeasuredWidth();
        if (this.animateWithParent && getParent() != null) {
            View parent = (View) getParent();
            this.animateFromY = parent.getY();
            this.animateFromX = parent.getX();
        } else {
            this.animateFromY = getY();
            this.animateFromX = getX();
        }
        this.aninateFromScale = this.scaleTextureToFill;
        this.aninateFromScaleBlur = this.scaleTextureToFillBlur;
        this.animateFromThumbScale = this.scaleThumb;
        this.animateFromRendererW = this.renderer.getMeasuredWidth();
        this.animateFromRendererH = this.renderer.getMeasuredHeight();
        this.animateOnNextLayout = true;
        requestLayout();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.applyRotation) {
            this.ignoreLayout = true;
            Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
            this.renderer.setScreenRotation(display.getRotation());
            this.ignoreLayout = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateRendererSize();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.renderer.updateRotation();
    }

    public void updateRendererSize() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            textureView.getLayoutParams().width = this.renderer.getMeasuredWidth();
            this.blurRenderer.getLayoutParams().height = this.renderer.getMeasuredHeight();
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float translationX;
        float translationY;
        float translationY2;
        float translationX2;
        super.onLayout(changed, left, top, right, bottom);
        if (this.blurRenderer != null) {
            this.scaleTextureToFillBlur = Math.max(getMeasuredHeight() / this.blurRenderer.getMeasuredHeight(), getMeasuredWidth() / this.blurRenderer.getMeasuredWidth());
        }
        if (!this.applyRotation) {
            this.renderer.updateRotation();
        }
        if (this.scaleType == SCALE_TYPE_NONE) {
            TextureView textureView = this.blurRenderer;
            if (textureView != null) {
                textureView.setScaleX(this.scaleTextureToFillBlur);
                this.blurRenderer.setScaleY(this.scaleTextureToFillBlur);
                return;
            }
            return;
        }
        if (this.renderer.getMeasuredHeight() == 0 || this.renderer.getMeasuredWidth() == 0 || getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            this.scaleTextureToFill = 1.0f;
            if (this.currentAnimation == null && !this.animateOnNextLayout) {
                this.currentClipHorizontal = 0.0f;
                this.currentClipVertical = 0.0f;
            }
        } else {
            int i = this.scaleType;
            if (i == SCALE_TYPE_FILL) {
                this.scaleTextureToFill = Math.max(getMeasuredHeight() / this.renderer.getMeasuredHeight(), getMeasuredWidth() / this.renderer.getMeasuredWidth());
            } else if (i == SCALE_TYPE_ADAPTIVE) {
                if (Math.abs((getMeasuredHeight() / getMeasuredWidth()) - 1.0f) < 0.02f) {
                    this.scaleTextureToFill = Math.max(getMeasuredHeight() / this.renderer.getMeasuredHeight(), getMeasuredWidth() / this.renderer.getMeasuredWidth());
                } else if (getMeasuredWidth() <= getMeasuredHeight() || this.renderer.getMeasuredHeight() <= this.renderer.getMeasuredWidth()) {
                    this.scaleTextureToFill = Math.min(getMeasuredHeight() / this.renderer.getMeasuredHeight(), getMeasuredWidth() / this.renderer.getMeasuredWidth());
                } else {
                    this.scaleTextureToFill = Math.max(getMeasuredHeight() / this.renderer.getMeasuredHeight(), (getMeasuredWidth() / 2.0f) / this.renderer.getMeasuredWidth());
                }
            } else if (i == SCALE_TYPE_FIT) {
                this.scaleTextureToFill = Math.min(getMeasuredHeight() / this.renderer.getMeasuredHeight(), getMeasuredWidth() / this.renderer.getMeasuredWidth());
                if (this.clipToTexture && !this.animateWithParent && this.currentAnimation == null && !this.animateOnNextLayout) {
                    this.currentClipHorizontal = (getMeasuredWidth() - this.renderer.getMeasuredWidth()) / 2.0f;
                    this.currentClipVertical = (getMeasuredHeight() - this.renderer.getMeasuredHeight()) / 2.0f;
                    if (Build.VERSION.SDK_INT >= 21) {
                        invalidateOutline();
                    }
                }
            }
        }
        if (this.thumb != null) {
            this.scaleThumb = Math.max(getMeasuredWidth() / this.thumb.getWidth(), getMeasuredHeight() / this.thumb.getHeight());
        }
        if (this.animateOnNextLayout) {
            this.aninateFromScale /= this.renderer.getMeasuredWidth() / this.animateFromRendererW;
            this.aninateFromScaleBlur /= this.renderer.getMeasuredWidth() / this.animateFromRendererW;
            this.animateOnNextLayout = false;
            if (this.animateWithParent && getParent() != null) {
                View parent = (View) getParent();
                translationY = this.animateFromY - parent.getTop();
                translationX = this.animateFromX - parent.getLeft();
            } else {
                translationY = this.animateFromY - getTop();
                translationX = this.animateFromX - getLeft();
            }
            this.clipVertical = 0.0f;
            this.clipHorizontal = 0.0f;
            if (this.animateFromHeight != getMeasuredHeight()) {
                float measuredHeight = (getMeasuredHeight() - this.animateFromHeight) / 2.0f;
                this.clipVertical = measuredHeight;
                translationY2 = translationY - measuredHeight;
            } else {
                translationY2 = translationY;
            }
            if (this.animateFromWidth != getMeasuredWidth()) {
                float measuredWidth = (getMeasuredWidth() - this.animateFromWidth) / 2.0f;
                this.clipHorizontal = measuredWidth;
                translationX2 = translationX - measuredWidth;
            } else {
                translationX2 = translationX;
            }
            setTranslationY(translationY2);
            setTranslationX(translationX2);
            ValueAnimator valueAnimator = this.currentAnimation;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.currentAnimation.cancel();
            }
            this.renderer.setScaleX(this.aninateFromScale);
            this.renderer.setScaleY(this.aninateFromScale);
            TextureView textureView2 = this.blurRenderer;
            if (textureView2 != null) {
                textureView2.setScaleX(this.aninateFromScaleBlur);
                this.blurRenderer.setScaleY(this.aninateFromScaleBlur);
            }
            this.currentClipVertical = this.clipVertical;
            this.currentClipHorizontal = this.clipHorizontal;
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline();
            }
            invalidate();
            final float fromScaleFinal = this.aninateFromScale;
            final float fromScaleBlurFinal = this.aninateFromScaleBlur;
            final float fromThumbScale = this.animateFromThumbScale;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.currentAnimation = ofFloat;
            final float finalTranslationX = translationX2;
            final float finalTranslationY = translationY2;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.VoIPTextureView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    VoIPTextureView.this.m3271x20db84c2(fromScaleFinal, fromScaleBlurFinal, finalTranslationX, finalTranslationY, fromThumbScale, valueAnimator2);
                }
            });
            long j = this.animateNextDuration;
            if (j != 0) {
                this.currentAnimation.setDuration(j);
            } else {
                this.currentAnimation.setDuration(350L);
            }
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPTextureView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    VoIPTextureView.this.currentClipVertical = 0.0f;
                    VoIPTextureView.this.currentClipHorizontal = 0.0f;
                    VoIPTextureView.this.renderer.setScaleX(VoIPTextureView.this.scaleTextureToFill);
                    VoIPTextureView.this.renderer.setScaleY(VoIPTextureView.this.scaleTextureToFill);
                    if (VoIPTextureView.this.blurRenderer != null) {
                        VoIPTextureView.this.blurRenderer.setScaleX(VoIPTextureView.this.scaleTextureToFillBlur);
                        VoIPTextureView.this.blurRenderer.setScaleY(VoIPTextureView.this.scaleTextureToFillBlur);
                    }
                    VoIPTextureView.this.setTranslationY(0.0f);
                    VoIPTextureView.this.setTranslationX(0.0f);
                    VoIPTextureView voIPTextureView = VoIPTextureView.this;
                    voIPTextureView.currentThumbScale = voIPTextureView.scaleThumb;
                    VoIPTextureView.this.currentAnimation = null;
                }
            });
            this.currentAnimation.start();
            if (!this.animateOnNextLayoutAnimations.isEmpty()) {
                for (int i2 = 0; i2 < this.animateOnNextLayoutAnimations.size(); i2++) {
                    this.animateOnNextLayoutAnimations.get(i2).start();
                }
            }
            this.animateOnNextLayoutAnimations.clear();
            this.animateNextDuration = 0L;
        } else if (this.currentAnimation == null) {
            this.renderer.setScaleX(this.scaleTextureToFill);
            this.renderer.setScaleY(this.scaleTextureToFill);
            TextureView textureView3 = this.blurRenderer;
            if (textureView3 != null) {
                textureView3.setScaleX(this.scaleTextureToFillBlur);
                this.blurRenderer.setScaleY(this.scaleTextureToFillBlur);
            }
            this.currentThumbScale = this.scaleThumb;
        }
    }

    /* renamed from: lambda$onLayout$0$org-telegram-ui-Components-voip-VoIPTextureView */
    public /* synthetic */ void m3271x20db84c2(float fromScaleFinal, float fromScaleBlurFinal, float finalTranslationX, float finalTranslationY, float fromThumbScale, ValueAnimator animator) {
        float v = ((Float) animator.getAnimatedValue()).floatValue();
        this.animationProgress = 1.0f - v;
        this.currentClipVertical = this.clipVertical * v;
        this.currentClipHorizontal = this.clipHorizontal * v;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
        invalidate();
        float s = (fromScaleFinal * v) + (this.scaleTextureToFill * (1.0f - v));
        this.renderer.setScaleX(s);
        this.renderer.setScaleY(s);
        float s2 = (fromScaleBlurFinal * v) + (this.scaleTextureToFillBlur * (1.0f - v));
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            textureView.setScaleX(s2);
            this.blurRenderer.setScaleY(s2);
        }
        setTranslationX(finalTranslationX * v);
        setTranslationY(finalTranslationY * v);
        this.currentThumbScale = (fromThumbScale * v) + (this.scaleThumb * (1.0f - v));
    }

    public void setCliping(float horizontalClip, float verticalClip) {
        if (this.currentAnimation != null || this.animateOnNextLayout) {
            return;
        }
        this.currentClipHorizontal = horizontalClip;
        this.currentClipVertical = verticalClip;
        if (Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
        invalidate();
    }

    public void setAnimateWithParent(boolean b) {
        this.animateWithParent = b;
    }

    public void synchOrRunAnimation(Animator animator) {
        if (this.animateOnNextLayout) {
            this.animateOnNextLayoutAnimations.add(animator);
        } else {
            animator.start();
        }
    }

    public void cancelAnimation() {
        this.animateOnNextLayout = false;
        this.animateNextDuration = 0L;
    }

    public void setAnimateNextDuration(long animateNextDuration) {
        this.animateNextDuration = animateNextDuration;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public void detachBackgroundRenderer() {
        TextureView textureView = this.blurRenderer;
        if (textureView == null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(textureView, View.ALPHA, 0.0f).setDuration(150L);
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.VoIPTextureView.4
            private boolean isCanceled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.isCanceled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!this.isCanceled) {
                    VoIPTextureView.this.renderer.setBackgroundRenderer(null);
                }
            }
        });
        animator.start();
    }

    public void reattachBackgroundRenderer() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            this.renderer.setBackgroundRenderer(textureView);
            ObjectAnimator.ofFloat(this.blurRenderer, View.ALPHA, 1.0f).setDuration(150L).start();
        }
    }

    public void attachBackgroundRenderer() {
        TextureView textureView = this.blurRenderer;
        if (textureView != null) {
            this.renderer.setBackgroundRenderer(textureView);
            if (!this.renderer.isFirstFrameRendered()) {
                this.blurRenderer.setAlpha(0.0f);
            }
        }
    }

    public boolean isInAnimation() {
        return this.currentAnimation != null;
    }

    public void updateRotation() {
        if (!this.applyRotation) {
            ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        }
    }
}
