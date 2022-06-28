package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Premium.SpeedLineParticles;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class VideoScreenPreview extends FrameLayout implements PagerHeaderView, NotificationCenter.NotificationCenterDelegate {
    private static final float[] speedScaleVideoTimestamps = {0.02f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.02f};
    boolean allowPlay;
    float aspectRatio;
    AspectRatioFrameLayout aspectRatioFrameLayout;
    String attachFileName;
    boolean attached;
    CellFlickerDrawable.DrawableInterface cellFlickerDrawable;
    int currentAccount;
    File file;
    boolean firstFrameRendered;
    boolean fromTop;
    long lastFrameTime;
    private MatrixParticlesDrawable matrixParticlesDrawable;
    boolean play;
    float progress;
    private float roundRadius;
    RoundedBitmapDrawable roundedBitmapDrawable;
    int size;
    SpeedLineParticles.Drawable speedLinesDrawable;
    StarParticlesView.Drawable starDrawable;
    private final SvgHelper.SvgDrawable svgIcon;
    TextureView textureView;
    int type;
    VideoPlayer videoPlayer;
    boolean visible;
    Paint phoneFrame1 = new Paint(1);
    Paint phoneFrame2 = new Paint(1);
    ImageReceiver imageReceiver = new ImageReceiver(this);

    private void checkVideo() {
        File file = this.file;
        if (file != null && file.exists()) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(this.file));
            int width = Integer.valueOf(retriever.extractMetadata(18)).intValue();
            int height = Integer.valueOf(retriever.extractMetadata(19)).intValue();
            retriever.release();
            this.aspectRatio = width / height;
            if (this.allowPlay) {
                runVideoPlayer();
            }
        }
    }

    public VideoScreenPreview(Context context, SvgHelper.SvgDrawable svgDrawable, int currentAccount, int type) {
        super(context);
        this.fromTop = false;
        this.currentAccount = currentAccount;
        this.type = type;
        this.svgIcon = svgDrawable;
        this.phoneFrame1.setColor(-16777216);
        this.phoneFrame2.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_premiumGradient2), -16777216, 0.5f));
        this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        setVideo();
        if (type != 1) {
            if (type == 6 || type == 9 || type == 3 || type == 7) {
                StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(40);
                this.starDrawable = drawable;
                drawable.speedScale = 3.0f;
                this.starDrawable.type = type;
                if (type == 3) {
                    this.starDrawable.size1 = 14;
                    this.starDrawable.size2 = 18;
                    this.starDrawable.size3 = 18;
                } else {
                    this.starDrawable.size1 = 14;
                    this.starDrawable.size2 = 16;
                    this.starDrawable.size3 = 15;
                }
                StarParticlesView.Drawable drawable2 = this.starDrawable;
                drawable2.k3 = 0.98f;
                drawable2.k2 = 0.98f;
                drawable2.k1 = 0.98f;
                this.starDrawable.speedScale = 4.0f;
                this.starDrawable.colorKey = Theme.key_premiumStartSmallStarsColor2;
                this.starDrawable.init();
            } else if (type == 2) {
                SpeedLineParticles.Drawable drawable3 = new SpeedLineParticles.Drawable(200);
                this.speedLinesDrawable = drawable3;
                drawable3.init();
            } else {
                int particlesCount = 100;
                if (SharedConfig.getDevicePerformanceClass() == 2) {
                    particlesCount = 800;
                } else if (SharedConfig.getDevicePerformanceClass() == 1) {
                    particlesCount = 400;
                }
                StarParticlesView.Drawable drawable4 = new StarParticlesView.Drawable(particlesCount);
                this.starDrawable = drawable4;
                drawable4.colorKey = Theme.key_premiumStartSmallStarsColor2;
                this.starDrawable.size1 = 8;
                this.starDrawable.size1 = 6;
                this.starDrawable.size1 = 4;
                StarParticlesView.Drawable drawable5 = this.starDrawable;
                drawable5.k3 = 0.98f;
                drawable5.k2 = 0.98f;
                drawable5.k1 = 0.98f;
                this.starDrawable.useRotate = true;
                this.starDrawable.speedScale = 4.0f;
                this.starDrawable.checkBounds = true;
                this.starDrawable.checkTime = true;
                this.starDrawable.useBlur = true;
                this.starDrawable.roundEffect = false;
                this.starDrawable.init();
            }
        } else {
            MatrixParticlesDrawable matrixParticlesDrawable = new MatrixParticlesDrawable();
            this.matrixParticlesDrawable = matrixParticlesDrawable;
            matrixParticlesDrawable.init();
        }
        if (type == 1 || type == 3) {
            this.fromTop = true;
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(context) { // from class: org.telegram.ui.Components.Premium.VideoScreenPreview.1
            Path clipPath = new Path();

            @Override // com.google.android.exoplayer2.ui.AspectRatioFrameLayout, android.widget.FrameLayout, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                this.clipPath.reset();
                if (VideoScreenPreview.this.fromTop) {
                    AndroidUtilities.rectTmp.set(0.0f, -VideoScreenPreview.this.roundRadius, getMeasuredWidth(), getMeasuredHeight());
                } else {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), (int) (getMeasuredHeight() + VideoScreenPreview.this.roundRadius));
                }
                float rad = VideoScreenPreview.this.roundRadius - AndroidUtilities.dp(3.0f);
                this.clipPath.addRoundRect(AndroidUtilities.rectTmp, new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, Path.Direction.CW);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(this.clipPath);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.aspectRatioFrameLayout = aspectRatioFrameLayout;
        aspectRatioFrameLayout.setResizeMode(0);
        TextureView textureView = new TextureView(context);
        this.textureView = textureView;
        this.aspectRatioFrameLayout.addView(textureView);
        setWillNotDraw(false);
        addView(this.aspectRatioFrameLayout);
    }

    private void setVideo() {
        TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(this.currentAccount).getPremiumPromo();
        String typeString = PremiumPreviewFragment.featureTypeToServerString(this.type);
        if (premiumPromo != null) {
            int index = -1;
            int i = 0;
            while (true) {
                if (i < premiumPromo.video_sections.size()) {
                    if (!premiumPromo.video_sections.get(i).equals(typeString)) {
                        i++;
                    } else {
                        index = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (index >= 0) {
                final TLRPC.Document document = premiumPromo.videos.get(index);
                CombinedDrawable combinedDrawable = null;
                for (int i2 = 0; i2 < document.thumbs.size(); i2++) {
                    if (document.thumbs.get(i2) instanceof TLRPC.TL_photoStrippedSize) {
                        this.roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), ImageLoader.getStrippedPhotoBitmap(document.thumbs.get(i2).bytes, "b"));
                        CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();
                        flickerDrawable.repeatProgress = 4.0f;
                        flickerDrawable.progress = 3.5f;
                        flickerDrawable.frameInside = true;
                        this.cellFlickerDrawable = flickerDrawable.getDrawableInterface(this, this.svgIcon);
                        CombinedDrawable combinedDrawable2 = new CombinedDrawable(this.roundedBitmapDrawable, this.cellFlickerDrawable) { // from class: org.telegram.ui.Components.Premium.VideoScreenPreview.2
                            @Override // android.graphics.drawable.Drawable
                            public void setBounds(int left, int top, int right, int bottom) {
                                if (VideoScreenPreview.this.fromTop) {
                                    super.setBounds(left, (int) (top - VideoScreenPreview.this.roundRadius), right, bottom);
                                } else {
                                    super.setBounds(left, top, right, (int) (bottom + VideoScreenPreview.this.roundRadius));
                                }
                            }
                        };
                        combinedDrawable2.setFullsize(true);
                        combinedDrawable = combinedDrawable2;
                    }
                }
                this.attachFileName = FileLoader.getAttachFileName(document);
                this.imageReceiver.setImage(null, null, combinedDrawable, null, null, 1);
                FileLoader.getInstance(this.currentAccount).loadFile(document, null, 1, 0);
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Premium.VideoScreenPreview$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoScreenPreview.this.m2909xfd64f837(document);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$setVideo$1$org-telegram-ui-Components-Premium-VideoScreenPreview */
    public /* synthetic */ void m2909xfd64f837(TLRPC.Document document) {
        final File file = FileLoader.getInstance(this.currentAccount).getPathToAttach(document);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.VideoScreenPreview$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VideoScreenPreview.this.m2908xd410a2f6(file);
            }
        });
    }

    /* renamed from: lambda$setVideo$0$org-telegram-ui-Components-Premium-VideoScreenPreview */
    public /* synthetic */ void m2908xd410a2f6(File file) {
        this.file = file;
        checkVideo();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int size = (int) (View.MeasureSpec.getSize(heightMeasureSpec) * 0.9f);
        float h = size;
        float w = size * 0.671f;
        float horizontalPadding = (measuredWidth - w) / 2.0f;
        this.roundRadius = size * 0.0671f;
        if (Build.VERSION.SDK_INT >= 21) {
            this.aspectRatioFrameLayout.invalidateOutline();
        }
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, 0.0f, measuredWidth - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, measuredHeight - h, measuredWidth - horizontalPadding, measuredHeight);
        }
        this.aspectRatioFrameLayout.getLayoutParams().width = (int) AndroidUtilities.rectTmp.width();
        this.aspectRatioFrameLayout.getLayoutParams().height = (int) AndroidUtilities.rectTmp.height();
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).leftMargin = (int) AndroidUtilities.rectTmp.left;
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).topMargin = (int) AndroidUtilities.rectTmp.top;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int sizeInternal = getMeasuredWidth() << (getMeasuredHeight() + 16);
        int size = (int) (getMeasuredHeight() * 0.9f);
        float h = size;
        float w = size * 0.671f;
        float horizontalPadding = (getMeasuredWidth() - w) / 2.0f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, -this.roundRadius, getMeasuredWidth() - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, getMeasuredHeight() - h, getMeasuredWidth() - horizontalPadding, getMeasuredHeight() + this.roundRadius);
        }
        if (this.size != sizeInternal) {
            this.size = sizeInternal;
            MatrixParticlesDrawable matrixParticlesDrawable = this.matrixParticlesDrawable;
            if (matrixParticlesDrawable != null) {
                matrixParticlesDrawable.drawingRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.matrixParticlesDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.matrixParticlesDrawable.excludeRect.inset(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            }
            StarParticlesView.Drawable drawable = this.starDrawable;
            if (drawable != null) {
                int i = this.type;
                if (i == 6 || i == 9 || i == 3 || i == 7) {
                    drawable.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    this.starDrawable.rect.inset(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
                } else {
                    int getParticlesWidth = (int) (AndroidUtilities.rectTmp.width() * 0.4f);
                    this.starDrawable.rect.set(AndroidUtilities.rectTmp.centerX() - getParticlesWidth, AndroidUtilities.rectTmp.centerY() - getParticlesWidth, AndroidUtilities.rectTmp.centerX() + getParticlesWidth, AndroidUtilities.rectTmp.centerY() + getParticlesWidth);
                    this.starDrawable.rect2.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                }
                this.starDrawable.resetPositions();
                this.starDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.starDrawable.excludeRect.inset(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
            }
            SpeedLineParticles.Drawable drawable2 = this.speedLinesDrawable;
            if (drawable2 != null) {
                drawable2.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.speedLinesDrawable.screenRect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.speedLinesDrawable.rect.inset(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(100.0f));
                this.speedLinesDrawable.rect.offset(0.0f, getMeasuredHeight() * 0.1f);
                this.speedLinesDrawable.resetPositions();
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float f;
        if (this.starDrawable != null || this.speedLinesDrawable != null || this.matrixParticlesDrawable != null) {
            if (this.progress < 0.5f) {
                float s = (float) Math.pow(1.0f - f, 2.0d);
                canvas.save();
                canvas.scale(s, s, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
                MatrixParticlesDrawable matrixParticlesDrawable = this.matrixParticlesDrawable;
                if (matrixParticlesDrawable != null) {
                    matrixParticlesDrawable.onDraw(canvas);
                } else {
                    StarParticlesView.Drawable drawable = this.starDrawable;
                    if (drawable != null) {
                        drawable.onDraw(canvas);
                    } else if (this.speedLinesDrawable != null) {
                        float videoSpeedScale = 0.2f;
                        VideoPlayer videoPlayer = this.videoPlayer;
                        if (videoPlayer != null) {
                            float p = ((float) videoPlayer.getCurrentPosition()) / ((float) this.videoPlayer.getDuration());
                            float p2 = Utilities.clamp(p, 1.0f, 0.0f);
                            float[] fArr = speedScaleVideoTimestamps;
                            float step = 1.0f / (fArr.length - 1);
                            int fromIndex = (int) (p2 / step);
                            int toIndex = fromIndex + 1;
                            float localProgress = (p2 - (fromIndex * step)) / step;
                            if (toIndex < fArr.length) {
                                videoSpeedScale = (fArr[fromIndex] * (1.0f - localProgress)) + (fArr[toIndex] * localProgress);
                            } else {
                                videoSpeedScale = fArr[fromIndex];
                            }
                        }
                        float p3 = this.progress;
                        float progressSpeedScale = ((1.0f - Utilities.clamp(p3 / 0.1f, 1.0f, 0.0f)) * 0.9f) + 0.1f;
                        this.speedLinesDrawable.speedScale = 150.0f * progressSpeedScale * videoSpeedScale;
                        this.speedLinesDrawable.onDraw(canvas);
                    }
                }
                canvas.restore();
                invalidate();
            }
        }
        int size = (int) (getMeasuredHeight() * 0.9f);
        float h = size;
        float w = size * 0.671f;
        float horizontalPadding = (getMeasuredWidth() - w) / 2.0f;
        this.roundRadius = size * 0.0671f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, -this.roundRadius, getMeasuredWidth() - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, getMeasuredHeight() - h, getMeasuredWidth() - horizontalPadding, getMeasuredHeight() + this.roundRadius);
        }
        AndroidUtilities.rectTmp.inset(-AndroidUtilities.dp(3.0f), -AndroidUtilities.dp(3.0f));
        AndroidUtilities.rectTmp.inset(-AndroidUtilities.dp(3.0f), -AndroidUtilities.dp(3.0f));
        canvas.drawRoundRect(AndroidUtilities.rectTmp, this.roundRadius + AndroidUtilities.dp(3.0f), this.roundRadius + AndroidUtilities.dp(3.0f), this.phoneFrame2);
        AndroidUtilities.rectTmp.inset(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        RectF rectF = AndroidUtilities.rectTmp;
        float f2 = this.roundRadius;
        canvas.drawRoundRect(rectF, f2, f2, this.phoneFrame1);
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, 0.0f, getMeasuredWidth() - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, getMeasuredHeight() - h, getMeasuredWidth() - horizontalPadding, getMeasuredHeight());
        }
        float dp = this.roundRadius - AndroidUtilities.dp(3.0f);
        this.roundRadius = dp;
        RoundedBitmapDrawable roundedBitmapDrawable = this.roundedBitmapDrawable;
        if (roundedBitmapDrawable != null) {
            roundedBitmapDrawable.setCornerRadius(dp);
        }
        CellFlickerDrawable.DrawableInterface drawableInterface = this.cellFlickerDrawable;
        if (drawableInterface != null) {
            drawableInterface.radius = this.roundRadius;
        }
        if (this.fromTop) {
            ImageReceiver imageReceiver = this.imageReceiver;
            float f3 = this.roundRadius;
            imageReceiver.setRoundRadius(0, 0, (int) f3, (int) f3);
        } else {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            float f4 = this.roundRadius;
            imageReceiver2.setRoundRadius((int) f4, (int) f4, 0, 0);
        }
        if (!this.firstFrameRendered) {
            this.imageReceiver.setImageCoords(AndroidUtilities.rectTmp.left, AndroidUtilities.rectTmp.top, AndroidUtilities.rectTmp.width(), AndroidUtilities.rectTmp.height());
            this.imageReceiver.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if (!this.fromTop) {
            canvas.drawCircle(this.imageReceiver.getCenterX(), this.imageReceiver.getImageY() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f), this.phoneFrame1);
        }
    }

    @Override // org.telegram.ui.Components.Premium.PagerHeaderView
    public void setOffset(float translationX) {
        boolean localAllowPlay;
        boolean localVisible;
        boolean localAllowPlay2 = true;
        if (translationX < 0.0f) {
            float p = (-translationX) / getMeasuredWidth();
            setAlpha((Utilities.clamp(1.0f - p, 1.0f, 0.0f) * 0.5f) + 0.5f);
            setRotationY(50.0f * p);
            invalidate();
            if (this.fromTop) {
                setTranslationY((-getMeasuredHeight()) * 0.3f * p);
            } else {
                setTranslationY(getMeasuredHeight() * 0.3f * p);
            }
            this.progress = Math.abs(p);
            localVisible = p < 1.0f;
            if (p >= 0.1f) {
                localAllowPlay2 = false;
            }
            localAllowPlay = localAllowPlay2;
        } else {
            float p2 = (-translationX) / getMeasuredWidth();
            invalidate();
            setRotationY(50.0f * p2);
            if (this.fromTop) {
                setTranslationY(getMeasuredHeight() * 0.3f * p2);
            } else {
                setTranslationY((-getMeasuredHeight()) * 0.3f * p2);
            }
            localVisible = p2 > -1.0f;
            if (p2 <= -0.1f) {
                localAllowPlay2 = false;
            }
            this.progress = Math.abs(p2);
            localAllowPlay = localAllowPlay2;
        }
        if (localVisible != this.visible) {
            this.visible = localVisible;
            updateAttachState();
        }
        if (localAllowPlay != this.allowPlay) {
            this.allowPlay = localAllowPlay;
            this.imageReceiver.setAllowStartAnimation(localAllowPlay);
            if (this.allowPlay) {
                this.imageReceiver.startAnimation();
                runVideoPlayer();
                return;
            }
            stopVideoPlayer();
            this.imageReceiver.stopAnimation();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        updateAttachState();
        if (!this.firstFrameRendered) {
            checkVideo();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        updateAttachState();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            String path = (String) args[0];
            String str = this.attachFileName;
            if (str != null && str.equals(path)) {
                this.file = (File) args[1];
                checkVideo();
            }
        }
    }

    private void updateAttachState() {
        boolean localPlay = this.visible && this.attached;
        if (this.play != localPlay) {
            this.play = localPlay;
            if (localPlay) {
                this.imageReceiver.onAttachedToWindow();
            } else {
                this.imageReceiver.onDetachedFromWindow();
            }
        }
    }

    private void runVideoPlayer() {
        if (this.file == null || this.videoPlayer != null) {
            return;
        }
        this.aspectRatioFrameLayout.setAspectRatio(this.aspectRatio, 0);
        VideoPlayer videoPlayer = new VideoPlayer();
        this.videoPlayer = videoPlayer;
        videoPlayer.setTextureView(this.textureView);
        this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() { // from class: org.telegram.ui.Components.Premium.VideoScreenPreview.3
            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public void onStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == 4) {
                    VideoScreenPreview.this.videoPlayer.seekTo(0L);
                    VideoScreenPreview.this.videoPlayer.play();
                } else if (playbackState == 1) {
                    VideoScreenPreview.this.videoPlayer.play();
                }
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public void onError(VideoPlayer player, Exception e) {
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public void onRenderedFirstFrame() {
                if (!VideoScreenPreview.this.firstFrameRendered) {
                    VideoScreenPreview.this.textureView.setAlpha(0.0f);
                    VideoScreenPreview.this.textureView.animate().alpha(1.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.VideoScreenPreview.3.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            VideoScreenPreview.this.firstFrameRendered = true;
                            VideoScreenPreview.this.invalidate();
                        }
                    }).setDuration(200L);
                }
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
            public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }
        });
        this.videoPlayer.preparePlayer(Uri.fromFile(this.file), "other");
        this.videoPlayer.setPlayWhenReady(true);
        if (!this.firstFrameRendered) {
            this.imageReceiver.stopAnimation();
            this.textureView.setAlpha(0.0f);
        }
        this.videoPlayer.seekTo(this.lastFrameTime + 60);
        this.videoPlayer.play();
    }

    private void stopVideoPlayer() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            this.lastFrameTime = videoPlayer.getCurrentPosition();
            this.videoPlayer.setTextureView(null);
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
    }
}
