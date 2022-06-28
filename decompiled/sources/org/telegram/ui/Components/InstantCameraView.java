package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Property;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.webrtc.EglBase;
/* loaded from: classes5.dex */
public class InstantCameraView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private static final int audioSampleRate = 48000;
    private float animationTranslationY;
    private AnimatorSet animatorSet;
    private org.telegram.messenger.camera.Size aspectRatio;
    private ChatActivity baseFragment;
    private BlurBehindDrawable blurBehindDrawable;
    private InstantViewCameraContainer cameraContainer;
    private File cameraFile;
    private volatile boolean cameraReady;
    private CameraSession cameraSession;
    private CameraGLThread cameraThread;
    private boolean cancelled;
    private boolean deviceHasGoodCamera;
    private TLRPC.InputEncryptedFile encryptedFile;
    private TLRPC.InputFile file;
    ValueAnimator finishZoomTransition;
    private boolean flipAnimationInProgress;
    boolean isInPinchToZoomTouchMode;
    private boolean isMessageTransition;
    private boolean isSecretChat;
    private byte[] iv;
    private byte[] key;
    private Bitmap lastBitmap;
    private float[] mMVPMatrix;
    private float[] mSTMatrix;
    boolean maybePinchToZoomTouchMode;
    private float[] moldSTMatrix;
    private AnimatorSet muteAnimation;
    private ImageView muteImageView;
    private boolean needDrawFlickerStub;
    private org.telegram.messenger.camera.Size oldTexturePreviewSize;
    private FloatBuffer oldTextureTextureBuffer;
    public boolean opened;
    private Paint paint;
    private float panTranslationY;
    private View parentView;
    private org.telegram.messenger.camera.Size pictureSize;
    float pinchScale;
    float pinchStartDistance;
    private int pointerId1;
    private int pointerId2;
    private org.telegram.messenger.camera.Size previewSize;
    private float progress;
    private Timer progressTimer;
    private long recordStartTime;
    private long recordedTime;
    private boolean recording;
    private int recordingGuid;
    private RectF rect;
    private boolean requestingPermissions;
    private final Theme.ResourcesProvider resourcesProvider;
    private float scaleX;
    private float scaleY;
    private CameraInfo selectedCamera;
    private long size;
    private ImageView switchCameraButton;
    private FloatBuffer textureBuffer;
    private BackupImageView textureOverlayView;
    private TextureView textureView;
    private int textureViewSize;
    private boolean updateTextureViewSize;
    private FloatBuffer vertexBuffer;
    private VideoEditedInfo videoEditedInfo;
    private VideoPlayer videoPlayer;
    private int currentAccount = UserConfig.selectedAccount;
    AnimatedVectorDrawable switchCameraDrawable = null;
    private boolean isFrontface = true;
    private int[] position = new int[2];
    private int[] cameraTexture = new int[1];
    private int[] oldCameraTexture = new int[1];
    private float cameraTextureAlpha = 1.0f;

    static /* synthetic */ float access$2516(InstantCameraView x0, float x1) {
        float f = x0.cameraTextureAlpha + x1;
        x0.cameraTextureAlpha = f;
        return f;
    }

    public InstantCameraView(Context context, ChatActivity parentFragment, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.aspectRatio = SharedConfig.roundCamera16to9 ? new org.telegram.messenger.camera.Size(1, 1) : new org.telegram.messenger.camera.Size(4, 3);
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        this.resourcesProvider = resourcesProvider;
        this.parentView = parentFragment.getFragmentView();
        setWillNotDraw(false);
        this.baseFragment = parentFragment;
        this.recordingGuid = parentFragment.getClassGuid();
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null;
        Paint paint = new Paint(1) { // from class: org.telegram.ui.Components.InstantCameraView.1
            @Override // android.graphics.Paint
            public void setAlpha(int a) {
                super.setAlpha(a);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (Build.VERSION.SDK_INT >= 21) {
            InstantViewCameraContainer instantViewCameraContainer = new InstantViewCameraContainer(context) { // from class: org.telegram.ui.Components.InstantCameraView.2
                @Override // android.view.View
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                @Override // android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer = instantViewCameraContainer;
            instantViewCameraContainer.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.InstantCameraView.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, InstantCameraView.this.textureViewSize, InstantCameraView.this.textureViewSize);
                }
            });
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            final Path path = new Path();
            final Paint paint2 = new Paint(1);
            paint2.setColor(-16777216);
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            InstantViewCameraContainer instantViewCameraContainer2 = new InstantViewCameraContainer(context) { // from class: org.telegram.ui.Components.InstantCameraView.4
                @Override // android.view.View
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                @Override // android.view.View
                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    path.reset();
                    path.addCircle(w / 2, h / 2, w / 2, Path.Direction.CW);
                    path.toggleInverseFillType();
                }

                @Override // org.telegram.ui.Components.InstantCameraView.InstantViewCameraContainer, android.view.ViewGroup, android.view.View
                protected void dispatchDraw(Canvas canvas) {
                    try {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(path, paint2);
                    } catch (Exception e) {
                    }
                }
            };
            this.cameraContainer = instantViewCameraContainer2;
            instantViewCameraContainer2.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, null);
        }
        addView(this.cameraContainer, new FrameLayout.LayoutParams(AndroidUtilities.roundPlayingMessageSize, AndroidUtilities.roundPlayingMessageSize, 17));
        ImageView imageView = new ImageView(context);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", R.string.AccDescrSwitchCamera));
        addView(this.switchCameraButton, LayoutHelper.createFrame(62, 62.0f, 83, 8.0f, 0.0f, 0.0f, 0.0f));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstantCameraView.this.m2685lambda$new$0$orgtelegramuiComponentsInstantCameraView(view);
            }
        });
        ImageView imageView2 = new ImageView(context);
        this.muteImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.muteImageView.setImageResource(R.drawable.video_mute);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        final Paint blackoutPaint = new Paint(1);
        blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 40));
        BackupImageView backupImageView = new BackupImageView(getContext()) { // from class: org.telegram.ui.Components.InstantCameraView.7
            CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();

            @Override // org.telegram.ui.Components.BackupImageView, android.view.View
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (InstantCameraView.this.needDrawFlickerStub) {
                    this.flickerDrawable.setParentWidth(InstantCameraView.this.textureViewSize);
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, InstantCameraView.this.textureViewSize, InstantCameraView.this.textureViewSize);
                    float rad = AndroidUtilities.rectTmp.width() / 2.0f;
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, blackoutPaint);
                    AndroidUtilities.rectTmp.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    this.flickerDrawable.draw(canvas, AndroidUtilities.rectTmp, rad, null);
                    invalidate();
                }
            }
        };
        this.textureOverlayView = backupImageView;
        addView(backupImageView, new FrameLayout.LayoutParams(AndroidUtilities.roundPlayingMessageSize, AndroidUtilities.roundPlayingMessageSize, 17));
        setVisibility(4);
        this.blurBehindDrawable = new BlurBehindDrawable(this.parentView, this, 0, resourcesProvider);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-InstantCameraView */
    public /* synthetic */ void m2685lambda$new$0$orgtelegramuiComponentsInstantCameraView(View v) {
        CameraSession cameraSession;
        if (!this.cameraReady || (cameraSession = this.cameraSession) == null || !cameraSession.isInitied() || this.cameraThread == null) {
            return;
        }
        switchCamera();
        AnimatedVectorDrawable animatedVectorDrawable = this.switchCameraDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
        this.flipAnimationInProgress = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.setDuration(300L);
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InstantCameraView.5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                float p;
                float p2 = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                if (p2 >= 0.5f) {
                    p = (p2 - 0.5f) / 0.5f;
                } else {
                    p = 1.0f - (p2 / 0.5f);
                }
                float scaleDown = (0.1f * p) + 0.9f;
                InstantCameraView.this.cameraContainer.setScaleX(p * scaleDown);
                InstantCameraView.this.cameraContainer.setScaleY(scaleDown);
                InstantCameraView.this.textureOverlayView.setScaleX(p * scaleDown);
                InstantCameraView.this.textureOverlayView.setScaleY(scaleDown);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InstantCameraView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                InstantCameraView.this.cameraContainer.setScaleX(1.0f);
                InstantCameraView.this.cameraContainer.setScaleY(1.0f);
                InstantCameraView.this.textureOverlayView.setScaleY(1.0f);
                InstantCameraView.this.textureOverlayView.setScaleX(1.0f);
                InstantCameraView.this.flipAnimationInProgress = false;
                InstantCameraView.this.invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newSize;
        if (this.updateTextureViewSize) {
            if (View.MeasureSpec.getSize(heightMeasureSpec) > View.MeasureSpec.getSize(widthMeasureSpec) * 1.3f) {
                newSize = AndroidUtilities.roundPlayingMessageSize;
            } else {
                newSize = AndroidUtilities.roundMessageSize;
            }
            if (newSize != this.textureViewSize) {
                this.textureViewSize = newSize;
                ViewGroup.LayoutParams layoutParams = this.textureOverlayView.getLayoutParams();
                ViewGroup.LayoutParams layoutParams2 = this.textureOverlayView.getLayoutParams();
                int i = this.textureViewSize;
                layoutParams2.height = i;
                layoutParams.width = i;
                ViewGroup.LayoutParams layoutParams3 = this.cameraContainer.getLayoutParams();
                ViewGroup.LayoutParams layoutParams4 = this.cameraContainer.getLayoutParams();
                int i2 = this.textureViewSize;
                layoutParams4.height = i2;
                layoutParams3.width = i2;
                ((FrameLayout.LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (this.textureViewSize / 2) - AndroidUtilities.dp(24.0f);
                this.textureOverlayView.setRoundRadius(this.textureViewSize / 2);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraContainer.invalidateOutline();
                }
            }
            this.updateTextureViewSize = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getVisibility() != 0) {
            this.animationTranslationY = getMeasuredHeight() / 2;
            updateTranslationY();
        }
        this.blurBehindDrawable.checkSizes();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileUploaded) {
            String location = (String) args[0];
            File file = this.cameraFile;
            if (file != null && file.getAbsolutePath().equals(location)) {
                this.file = (TLRPC.InputFile) args[1];
                this.encryptedFile = (TLRPC.InputEncryptedFile) args[2];
                this.size = ((Long) args[5]).longValue();
                if (this.encryptedFile != null) {
                    this.key = (byte[]) args[3];
                    this.iv = (byte[]) args[4];
                }
            }
        }
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.blurBehindDrawable.draw(canvas);
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - AndroidUtilities.dp(8.0f), y - AndroidUtilities.dp(8.0f), this.cameraContainer.getMeasuredWidth() + x + AndroidUtilities.dp(8.0f), this.cameraContainer.getMeasuredHeight() + y + AndroidUtilities.dp(8.0f));
        if (this.recording) {
            long currentTimeMillis = System.currentTimeMillis() - this.recordStartTime;
            this.recordedTime = currentTimeMillis;
            this.progress = Math.min(1.0f, ((float) currentTimeMillis) / 60000.0f);
            invalidate();
        }
        if (this.progress != 0.0f) {
            canvas.save();
            if (!this.flipAnimationInProgress) {
                canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), this.rect.centerX(), this.rect.centerY());
            }
            canvas.drawArc(this.rect, -90.0f, this.progress * 360.0f, false, this.paint);
            canvas.restore();
        }
        if (Theme.chat_roundVideoShadow != null) {
            int x1 = ((int) x) - AndroidUtilities.dp(3.0f);
            int y1 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            if (this.isMessageTransition) {
                canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), x, y);
            } else {
                float scaleX = this.cameraContainer.getScaleX();
                float scaleY = this.cameraContainer.getScaleY();
                int i = this.textureViewSize;
                canvas.scale(scaleX, scaleY, (i / 2.0f) + x, (i / 2.0f) + y);
            }
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(x1, y1, this.textureViewSize + x1 + AndroidUtilities.dp(6.0f), this.textureViewSize + y1 + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        BlurBehindDrawable blurBehindDrawable;
        super.setVisibility(visibility);
        if (visibility != 0 && (blurBehindDrawable = this.blurBehindDrawable) != null) {
            blurBehindDrawable.clear();
        }
        this.switchCameraButton.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.textureOverlayView.setAlpha(0.0f);
        this.muteImageView.setAlpha(0.0f);
        this.muteImageView.setScaleX(1.0f);
        this.muteImageView.setScaleY(1.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        this.textureOverlayView.setScaleX(0.1f);
        this.textureOverlayView.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            InstantViewCameraContainer instantViewCameraContainer = this.cameraContainer;
            instantViewCameraContainer.setPivotX(instantViewCameraContainer.getMeasuredWidth() / 2);
            InstantViewCameraContainer instantViewCameraContainer2 = this.cameraContainer;
            instantViewCameraContainer2.setPivotY(instantViewCameraContainer2.getMeasuredHeight() / 2);
            BackupImageView backupImageView = this.textureOverlayView;
            backupImageView.setPivotX(backupImageView.getMeasuredWidth() / 2);
            BackupImageView backupImageView2 = this.textureOverlayView;
            backupImageView2.setPivotY(backupImageView2.getMeasuredHeight() / 2);
        }
        try {
            if (visibility == 0) {
                ((Activity) getContext()).getWindow().addFlags(128);
            } else {
                ((Activity) getContext()).getWindow().clearFlags(128);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void showCamera() {
        if (this.textureView != null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) ContextCompat.getDrawable(getContext(), R.drawable.avd_flip);
            this.switchCameraDrawable = animatedVectorDrawable;
            this.switchCameraButton.setImageDrawable(animatedVectorDrawable);
        } else {
            this.switchCameraButton.setImageResource(R.drawable.vd_flip);
        }
        this.textureOverlayView.setAlpha(1.0f);
        this.textureOverlayView.invalidate();
        if (this.lastBitmap == null) {
            try {
                File file = new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg");
                this.lastBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            } catch (Throwable th) {
            }
        }
        Bitmap bitmap = this.lastBitmap;
        if (bitmap != null) {
            this.textureOverlayView.setImageBitmap(bitmap);
        } else {
            this.textureOverlayView.setImageResource(R.drawable.icplaceholder);
        }
        this.cameraReady = false;
        this.isFrontface = true;
        this.selectedCamera = null;
        this.recordedTime = 0L;
        this.progress = 0.0f;
        this.cancelled = false;
        this.file = null;
        this.encryptedFile = null;
        this.key = null;
        this.iv = null;
        this.needDrawFlickerStub = true;
        if (!initCamera()) {
            return;
        }
        MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
        File directory = FileLoader.getDirectory(4);
        this.cameraFile = new File(directory, SharedConfig.getLastLocalId() + ".mp4");
        SharedConfig.saveConfig();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("show round camera");
        }
        TextureView textureView = new TextureView(getContext());
        this.textureView = textureView;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: org.telegram.ui.Components.InstantCameraView.8
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("camera surface available");
                }
                if (InstantCameraView.this.cameraThread != null || surface == null || InstantCameraView.this.cancelled) {
                    return;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("start create thread");
                }
                InstantCameraView.this.cameraThread = new CameraGLThread(surface, width, height);
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (InstantCameraView.this.cameraThread == null) {
                    return;
                }
                InstantCameraView.this.cameraThread.surfaceWidth = width;
                InstantCameraView.this.cameraThread.surfaceHeight = height;
                InstantCameraView.this.cameraThread.updateScale();
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (InstantCameraView.this.cameraThread != null) {
                    InstantCameraView.this.cameraThread.shutdown(0);
                    InstantCameraView.this.cameraThread = null;
                }
                if (InstantCameraView.this.cameraSession != null) {
                    CameraController.getInstance().close(InstantCameraView.this.cameraSession, null, null);
                    return true;
                }
                return true;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        this.updateTextureViewSize = true;
        setVisibility(0);
        startAnimation(true);
        MediaController.getInstance().requestAudioFocus(true);
    }

    public InstantViewCameraContainer getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean open) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.animatorSet.cancel();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.showTemporary(!open);
        }
        if (open && !this.opened) {
            this.cameraContainer.setTranslationX(0.0f);
            this.textureOverlayView.setTranslationX(0.0f);
            this.animationTranslationY = getMeasuredHeight() / 2.0f;
            updateTranslationY();
        }
        this.opened = open;
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
        this.blurBehindDrawable.show(open);
        this.animatorSet = new AnimatorSet();
        float toX = 0.0f;
        if (!open) {
            toX = this.recordedTime > 300 ? AndroidUtilities.dp(24.0f) - (getMeasuredWidth() / 2.0f) : 0.0f;
        }
        float[] fArr = new float[2];
        float f = 1.0f;
        fArr[0] = open ? 1.0f : 0.0f;
        fArr[1] = open ? 0.0f : 1.0f;
        ValueAnimator translationYAnimator = ValueAnimator.ofFloat(fArr);
        translationYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                InstantCameraView.this.m2686xca1896e2(valueAnimator);
            }
        });
        AnimatorSet animatorSet2 = this.animatorSet;
        Animator[] animatorArr = new Animator[12];
        ImageView imageView = this.switchCameraButton;
        Property property = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = open ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(imageView, property, fArr2);
        animatorArr[1] = ObjectAnimator.ofFloat(this.muteImageView, View.ALPHA, 0.0f);
        Paint paint = this.paint;
        Property<Paint, Integer> property2 = AnimationProperties.PAINT_ALPHA;
        int[] iArr = new int[1];
        iArr[0] = open ? 255 : 0;
        animatorArr[2] = ObjectAnimator.ofInt(paint, property2, iArr);
        InstantViewCameraContainer instantViewCameraContainer = this.cameraContainer;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = open ? 1.0f : 0.0f;
        animatorArr[3] = ObjectAnimator.ofFloat(instantViewCameraContainer, property3, fArr3);
        InstantViewCameraContainer instantViewCameraContainer2 = this.cameraContainer;
        Property property4 = View.SCALE_X;
        float[] fArr4 = new float[1];
        fArr4[0] = open ? 1.0f : 0.1f;
        animatorArr[4] = ObjectAnimator.ofFloat(instantViewCameraContainer2, property4, fArr4);
        InstantViewCameraContainer instantViewCameraContainer3 = this.cameraContainer;
        Property property5 = View.SCALE_Y;
        float[] fArr5 = new float[1];
        fArr5[0] = open ? 1.0f : 0.1f;
        animatorArr[5] = ObjectAnimator.ofFloat(instantViewCameraContainer3, property5, fArr5);
        animatorArr[6] = ObjectAnimator.ofFloat(this.cameraContainer, View.TRANSLATION_X, toX);
        BackupImageView backupImageView = this.textureOverlayView;
        Property property6 = View.ALPHA;
        float[] fArr6 = new float[1];
        fArr6[0] = open ? 1.0f : 0.0f;
        animatorArr[7] = ObjectAnimator.ofFloat(backupImageView, property6, fArr6);
        BackupImageView backupImageView2 = this.textureOverlayView;
        Property property7 = View.SCALE_X;
        float[] fArr7 = new float[1];
        fArr7[0] = open ? 1.0f : 0.1f;
        animatorArr[8] = ObjectAnimator.ofFloat(backupImageView2, property7, fArr7);
        BackupImageView backupImageView3 = this.textureOverlayView;
        Property property8 = View.SCALE_Y;
        float[] fArr8 = new float[1];
        if (!open) {
            f = 0.1f;
        }
        fArr8[0] = f;
        animatorArr[9] = ObjectAnimator.ofFloat(backupImageView3, property8, fArr8);
        animatorArr[10] = ObjectAnimator.ofFloat(this.textureOverlayView, View.TRANSLATION_X, toX);
        animatorArr[11] = translationYAnimator;
        animatorSet2.playTogether(animatorArr);
        if (!open) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InstantCameraView.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.animatorSet)) {
                        InstantCameraView.this.hideCamera(true);
                        InstantCameraView.this.setVisibility(4);
                    }
                }
            });
        } else {
            setTranslationX(0.0f);
        }
        this.animatorSet.setDuration(180L);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    /* renamed from: lambda$startAnimation$1$org-telegram-ui-Components-InstantCameraView */
    public /* synthetic */ void m2686xca1896e2(ValueAnimator animation) {
        this.animationTranslationY = (getMeasuredHeight() / 2.0f) * ((Float) animation.getAnimatedValue()).floatValue();
        updateTranslationY();
    }

    private void updateTranslationY() {
        this.textureOverlayView.setTranslationY(this.animationTranslationY + this.panTranslationY);
        this.cameraContainer.setTranslationY(this.animationTranslationY + this.panTranslationY);
    }

    public Rect getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        int[] iArr = this.position;
        return new Rect(iArr[0], iArr[1], this.cameraContainer.getWidth(), this.cameraContainer.getHeight());
    }

    public void changeVideoPreviewState(int state, float progress) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        if (state == 0) {
            startProgressTimer();
            this.videoPlayer.play();
        } else if (state == 1) {
            stopProgressTimer();
            this.videoPlayer.pause();
        } else if (state == 2) {
            videoPlayer.seekTo(((float) videoPlayer.getDuration()) * progress);
        }
    }

    public void send(int state, boolean notify, int scheduleDate) {
        int reason;
        int send;
        if (this.textureView == null) {
            return;
        }
        stopProgressTimer();
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
        if (state == 4) {
            if (!this.videoEditedInfo.needConvert()) {
                this.videoEditedInfo.estimatedSize = Math.max(1L, this.size);
            } else {
                this.file = null;
                this.encryptedFile = null;
                this.key = null;
                this.iv = null;
                double totalDuration = this.videoEditedInfo.estimatedDuration;
                long startTime = this.videoEditedInfo.startTime >= 0 ? this.videoEditedInfo.startTime : 0L;
                long endTime = this.videoEditedInfo.endTime >= 0 ? this.videoEditedInfo.endTime : this.videoEditedInfo.estimatedDuration;
                this.videoEditedInfo.estimatedDuration = endTime - startTime;
                VideoEditedInfo videoEditedInfo = this.videoEditedInfo;
                double d = this.size;
                double d2 = videoEditedInfo.estimatedDuration;
                Double.isNaN(d2);
                Double.isNaN(totalDuration);
                Double.isNaN(d);
                videoEditedInfo.estimatedSize = Math.max(1L, (long) (d * (d2 / totalDuration)));
                this.videoEditedInfo.bitrate = MediaController.VIDEO_BITRATE_480;
                if (this.videoEditedInfo.startTime > 0) {
                    this.videoEditedInfo.startTime *= 1000;
                }
                if (this.videoEditedInfo.endTime > 0) {
                    this.videoEditedInfo.endTime *= 1000;
                }
                FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.cameraFile.getAbsolutePath(), false);
            }
            this.videoEditedInfo.file = this.file;
            this.videoEditedInfo.encryptedFile = this.encryptedFile;
            this.videoEditedInfo.key = this.key;
            this.videoEditedInfo.iv = this.iv;
            this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0L, this.cameraFile.getAbsolutePath(), 0, true, 0, 0, 0L), this.videoEditedInfo, notify, scheduleDate, false);
            if (scheduleDate != 0) {
                startAnimation(false);
            }
            MediaController.getInstance().requestAudioFocus(false);
            return;
        }
        boolean z = this.recordedTime < 800;
        this.cancelled = z;
        this.recording = false;
        if (z) {
            reason = 4;
        } else {
            reason = state == 3 ? 2 : 5;
        }
        if (this.cameraThread != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(this.recordingGuid), Integer.valueOf(reason));
            if (this.cancelled) {
                send = 0;
            } else if (state == 3) {
                send = 2;
            } else {
                send = 1;
            }
            saveLastCameraBitmap();
            this.cameraThread.shutdown(send);
            this.cameraThread = null;
        }
        if (this.cancelled) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), true, Integer.valueOf((int) this.recordedTime));
            startAnimation(false);
            MediaController.getInstance().requestAudioFocus(false);
        }
    }

    private void saveLastCameraBitmap() {
        Bitmap bitmap = this.textureView.getBitmap();
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(this.textureView.getBitmap(), 50, 50, true);
            this.lastBitmap = createScaledBitmap;
            if (createScaledBitmap != null) {
                Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), this.lastBitmap.getHeight(), this.lastBitmap.getRowBytes());
                try {
                    File file = new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg");
                    FileOutputStream stream = new FileOutputStream(file);
                    this.lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    stream.close();
                } catch (Throwable th) {
                }
            }
        }
    }

    public void cancel(boolean byGesture) {
        stopProgressTimer();
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
        if (this.textureView == null) {
            return;
        }
        this.cancelled = true;
        this.recording = false;
        NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
        int i = NotificationCenter.recordStopped;
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(this.recordingGuid);
        objArr[1] = Integer.valueOf(byGesture ? 0 : 6);
        notificationCenter.postNotificationName(i, objArr);
        if (this.cameraThread != null) {
            saveLastCameraBitmap();
            this.cameraThread.shutdown(0);
            this.cameraThread = null;
        }
        File file = this.cameraFile;
        if (file != null) {
            file.delete();
            this.cameraFile = null;
        }
        MediaController.getInstance().requestAudioFocus(false);
        startAnimation(false);
        this.blurBehindDrawable.show(false);
        invalidate();
    }

    public View getSwitchButtonView() {
        return this.switchCameraButton;
    }

    public View getMuteImageView() {
        return this.muteImageView;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void hideCamera(boolean async) {
        ViewGroup parent;
        destroy(async, null);
        this.cameraContainer.setTranslationX(0.0f);
        this.textureOverlayView.setTranslationX(0.0f);
        this.animationTranslationY = 0.0f;
        updateTranslationY();
        MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        TextureView textureView = this.textureView;
        if (textureView != null && (parent = (ViewGroup) textureView.getParent()) != null) {
            parent.removeView(this.textureView);
        }
        this.textureView = null;
        this.cameraContainer.setImageReceiver(null);
    }

    private void switchCamera() {
        saveLastCameraBitmap();
        Bitmap bitmap = this.lastBitmap;
        if (bitmap != null) {
            this.needDrawFlickerStub = false;
            this.textureOverlayView.setImageBitmap(bitmap);
            this.textureOverlayView.setAlpha(1.0f);
        }
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, null, null);
            this.cameraSession = null;
        }
        this.isFrontface = !this.isFrontface;
        initCamera();
        this.cameraReady = false;
        this.cameraThread.reinitForNewCamera();
    }

    private boolean initCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return false;
        }
        CameraInfo notFrontface = null;
        for (int a = 0; a < cameraInfos.size(); a++) {
            CameraInfo cameraInfo = cameraInfos.get(a);
            if (!cameraInfo.isFrontface()) {
                notFrontface = cameraInfo;
            }
            if ((this.isFrontface && cameraInfo.isFrontface()) || (!this.isFrontface && !cameraInfo.isFrontface())) {
                this.selectedCamera = cameraInfo;
                break;
            }
            notFrontface = cameraInfo;
        }
        if (this.selectedCamera == null) {
            this.selectedCamera = notFrontface;
        }
        CameraInfo cameraInfo2 = this.selectedCamera;
        if (cameraInfo2 == null) {
            return false;
        }
        ArrayList<org.telegram.messenger.camera.Size> previewSizes = cameraInfo2.getPreviewSizes();
        ArrayList<org.telegram.messenger.camera.Size> pictureSizes = this.selectedCamera.getPictureSizes();
        this.previewSize = chooseOptimalSize(previewSizes);
        this.pictureSize = chooseOptimalSize(pictureSizes);
        if (this.previewSize.mWidth != this.pictureSize.mWidth) {
            boolean found = false;
            for (int a2 = previewSizes.size() - 1; a2 >= 0; a2--) {
                org.telegram.messenger.camera.Size preview = previewSizes.get(a2);
                int b = pictureSizes.size() - 1;
                while (true) {
                    if (b < 0) {
                        break;
                    }
                    org.telegram.messenger.camera.Size picture = pictureSizes.get(b);
                    if (preview.mWidth >= this.pictureSize.mWidth && preview.mHeight >= this.pictureSize.mHeight && preview.mWidth == picture.mWidth && preview.mHeight == picture.mHeight) {
                        this.previewSize = preview;
                        this.pictureSize = picture;
                        found = true;
                        break;
                    }
                    b--;
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                for (int a3 = previewSizes.size() - 1; a3 >= 0; a3--) {
                    org.telegram.messenger.camera.Size preview2 = previewSizes.get(a3);
                    int b2 = pictureSizes.size() - 1;
                    while (true) {
                        if (b2 < 0) {
                            break;
                        }
                        org.telegram.messenger.camera.Size picture2 = pictureSizes.get(b2);
                        if (preview2.mWidth >= 360 && preview2.mHeight >= 360 && preview2.mWidth == picture2.mWidth && preview2.mHeight == picture2.mHeight) {
                            this.previewSize = preview2;
                            this.pictureSize = picture2;
                            found = true;
                            break;
                        }
                        b2--;
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }
        boolean found2 = BuildVars.LOGS_ENABLED;
        if (found2) {
            FileLog.d("preview w = " + this.previewSize.mWidth + " h = " + this.previewSize.mHeight);
        }
        return true;
    }

    private org.telegram.messenger.camera.Size chooseOptimalSize(ArrayList<org.telegram.messenger.camera.Size> previewSizes) {
        ArrayList<org.telegram.messenger.camera.Size> sortedSizes = new ArrayList<>();
        for (int i = 0; i < previewSizes.size(); i++) {
            if (Math.max(previewSizes.get(i).mHeight, previewSizes.get(i).mWidth) <= 1200 && Math.min(previewSizes.get(i).mHeight, previewSizes.get(i).mWidth) >= 320) {
                sortedSizes.add(previewSizes.get(i));
            }
        }
        if (sortedSizes.isEmpty() || SharedConfig.getDevicePerformanceClass() == 0 || SharedConfig.getDevicePerformanceClass() == 1) {
            return CameraController.chooseOptimalSize(previewSizes, 480, 270, this.aspectRatio);
        }
        Collections.sort(sortedSizes, InstantCameraView$$ExternalSyntheticLambda6.INSTANCE);
        return sortedSizes.get(0);
    }

    public static /* synthetic */ int lambda$chooseOptimalSize$2(org.telegram.messenger.camera.Size o1, org.telegram.messenger.camera.Size o2) {
        float a1 = Math.abs(1.0f - (Math.min(o1.mHeight, o1.mWidth) / Math.max(o1.mHeight, o1.mWidth)));
        float a2 = Math.abs(1.0f - (Math.min(o2.mHeight, o2.mWidth) / Math.max(o2.mHeight, o2.mWidth)));
        if (a1 < a2) {
            return -1;
        }
        if (a1 > a2) {
            return 1;
        }
        return 0;
    }

    public void createCamera(final SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                InstantCameraView.this.m2683x14c3aa3f(surfaceTexture);
            }
        });
    }

    /* renamed from: lambda$createCamera$5$org-telegram-ui-Components-InstantCameraView */
    public /* synthetic */ void m2683x14c3aa3f(SurfaceTexture surfaceTexture) {
        if (this.cameraThread == null) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("create camera session");
        }
        surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
        CameraSession cameraSession = new CameraSession(this.selectedCamera, this.previewSize, this.pictureSize, 256, true);
        this.cameraSession = cameraSession;
        this.cameraThread.setCurrentSession(cameraSession);
        CameraController.getInstance().openRound(this.cameraSession, surfaceTexture, new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                InstantCameraView.this.m2681xa1d48f01();
            }
        }, new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                InstantCameraView.this.m2682x5b4c1ca0();
            }
        });
    }

    /* renamed from: lambda$createCamera$3$org-telegram-ui-Components-InstantCameraView */
    public /* synthetic */ void m2681xa1d48f01() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("camera initied");
            }
            this.cameraSession.setInitied();
        }
    }

    /* renamed from: lambda$createCamera$4$org-telegram-ui-Components-InstantCameraView */
    public /* synthetic */ void m2682x5b4c1ca0() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e(GLES20.glGetShaderInfoLog(shader));
            }
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    public void startProgressTimer() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            try {
                timer.cancel();
                this.progressTimer = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        Timer timer2 = new Timer();
        this.progressTimer = timer2;
        timer2.schedule(new AnonymousClass10(), 0L, 17L);
    }

    /* renamed from: org.telegram.ui.Components.InstantCameraView$10 */
    /* loaded from: classes5.dex */
    public class AnonymousClass10 extends TimerTask {
        AnonymousClass10() {
            InstantCameraView.this = this$0;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$10$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InstantCameraView.AnonymousClass10.this.m2687lambda$run$0$orgtelegramuiComponentsInstantCameraView$10();
                }
            });
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Components-InstantCameraView$10 */
        public /* synthetic */ void m2687lambda$run$0$orgtelegramuiComponentsInstantCameraView$10() {
            try {
                if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoEditedInfo != null) {
                    long j = 0;
                    if (InstantCameraView.this.videoEditedInfo.endTime > 0 && InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime) {
                        VideoPlayer videoPlayer = InstantCameraView.this.videoPlayer;
                        if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                            j = InstantCameraView.this.videoEditedInfo.startTime;
                        }
                        videoPlayer.seekTo(j);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void stopProgressTimer() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            try {
                timer.cancel();
                this.progressTimer = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public boolean blurFullyDrawing() {
        BlurBehindDrawable blurBehindDrawable = this.blurBehindDrawable;
        return blurBehindDrawable != null && blurBehindDrawable.isFullyDrawing() && this.opened;
    }

    public void invalidateBlur() {
        BlurBehindDrawable blurBehindDrawable = this.blurBehindDrawable;
        if (blurBehindDrawable != null) {
            blurBehindDrawable.invalidate();
        }
    }

    public void cancelBlur() {
        this.blurBehindDrawable.show(false);
        invalidate();
    }

    public void onPanTranslationUpdate(float y) {
        this.panTranslationY = y / 2.0f;
        updateTranslationY();
        this.blurBehindDrawable.onPanTranslationUpdate(y);
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void setIsMessageTransition(boolean isMessageTransition) {
        this.isMessageTransition = isMessageTransition;
    }

    /* loaded from: classes5.dex */
    public class CameraGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private SurfaceTexture cameraSurface;
        private CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initied;
        private int positionHandle;
        private boolean recording;
        private int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        private int surfaceWidth;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private VideoRecorder videoEncoder;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_SETSESSION_MESSAGE = 3;
        private Integer cameraId = 0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CameraGLThread(SurfaceTexture surface, int surfaceWidth, int surfaceHeight) {
            super("CameraGLThread");
            InstantCameraView.this = this$0;
            this.surfaceTexture = surface;
            this.surfaceWidth = surfaceWidth;
            this.surfaceHeight = surfaceHeight;
            updateScale();
        }

        public void updateScale() {
            int width = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            float scale = this.surfaceWidth / Math.min(width, height);
            int width2 = (int) (width * scale);
            int height2 = (int) (height * scale);
            if (width2 == height2) {
                InstantCameraView.this.scaleX = 1.0f;
                InstantCameraView.this.scaleY = 1.0f;
            } else if (width2 > height2) {
                InstantCameraView.this.scaleX = 1.0f;
                InstantCameraView.this.scaleY = width2 / this.surfaceHeight;
            } else {
                InstantCameraView.this.scaleX = height2 / this.surfaceWidth;
                InstantCameraView.this.scaleY = 1.0f;
            }
            FileLog.d("camera scaleX = " + InstantCameraView.this.scaleX + " scaleY = " + InstantCameraView.this.scaleY);
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start init gl");
            }
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] version = new int[2];
            if (!this.egl10.eglInitialize(this.eglDisplay, version)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            int[] configSpec = {12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344};
            if (!this.egl10.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eglConfig = configs[0];
                int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture = this.surfaceTexture;
                if (surfaceTexture instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eglConfig, surfaceTexture, null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface != null && eglCreateWindowSurface != EGL10.EGL_NO_SURFACE) {
                        EGL10 egl102 = this.egl10;
                        EGLDisplay eGLDisplay = this.eglDisplay;
                        EGLSurface eGLSurface = this.eglSurface;
                        if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            }
                            finish();
                            return false;
                        }
                        this.eglContext.getGL();
                        float tX = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                        float tY = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                        float[] verticesData = {-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                        float[] texData = {0.5f - tX, 0.5f - tY, tX + 0.5f, 0.5f - tY, 0.5f - tX, tY + 0.5f, tX + 0.5f, tY + 0.5f};
                        this.videoEncoder = new VideoRecorder();
                        InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.vertexBuffer.put(verticesData).position(0);
                        InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        InstantCameraView.this.textureBuffer.put(texData).position(0);
                        Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                        int vertexShader = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                        int fragmentShader = InstantCameraView.this.loadShader(35632, InstantCameraView.FRAGMENT_SCREEN_SHADER);
                        if (vertexShader != 0 && fragmentShader != 0) {
                            int glCreateProgram = GLES20.glCreateProgram();
                            this.drawProgram = glCreateProgram;
                            GLES20.glAttachShader(glCreateProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] != 0) {
                                this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.e("failed link shader");
                                }
                                GLES20.glDeleteProgram(this.drawProgram);
                                this.drawProgram = 0;
                            }
                            GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                            GLES20.glTexParameteri(36197, 10241, 9729);
                            GLES20.glTexParameteri(36197, 10240, 9729);
                            GLES20.glTexParameteri(36197, 10242, 33071);
                            GLES20.glTexParameteri(36197, 10243, 33071);
                            Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                            SurfaceTexture surfaceTexture2 = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                            this.cameraSurface = surfaceTexture2;
                            surfaceTexture2.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() { // from class: org.telegram.ui.Components.InstantCameraView$CameraGLThread$$ExternalSyntheticLambda1
                                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                                public final void onFrameAvailable(SurfaceTexture surfaceTexture3) {
                                    InstantCameraView.CameraGLThread.this.m2689xbcdb2188(surfaceTexture3);
                                }
                            });
                            InstantCameraView.this.createCamera(this.cameraSurface);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("gl initied");
                                return true;
                            }
                            return true;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("failed creating shader");
                        }
                        finish();
                        return false;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        /* renamed from: lambda$initGL$0$org-telegram-ui-Components-InstantCameraView$CameraGLThread */
        public /* synthetic */ void m2689xbcdb2188(SurfaceTexture surfaceTexture) {
            requestRender();
        }

        public void reinitForNewCamera() {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2), 0);
            }
            updateScale();
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw(Integer cameraId) {
            if (!this.initied) {
                return;
            }
            if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                EGL10 egl10 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        return;
                    }
                    return;
                }
            }
            this.cameraSurface.updateTexImage();
            if (!this.recording) {
                this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext());
                this.recording = true;
                int orientation = this.currentSession.getCurrentOrientation();
                if (orientation == 90 || orientation == 270) {
                    float temp = InstantCameraView.this.scaleX;
                    InstantCameraView instantCameraView = InstantCameraView.this;
                    instantCameraView.scaleX = instantCameraView.scaleY;
                    InstantCameraView.this.scaleY = temp;
                }
            }
            this.videoEncoder.frameAvailable(this.cameraSurface, cameraId, System.nanoTime());
            this.cameraSurface.getTransformMatrix(InstantCameraView.this.mSTMatrix);
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, (Buffer) InstantCameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, (Buffer) InstantCameraView.this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
        }

        @Override // org.telegram.messenger.DispatchQueue, java.lang.Thread, java.lang.Runnable
        public void run() {
            this.initied = initGL();
            super.run();
        }

        @Override // org.telegram.messenger.DispatchQueue
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            switch (what) {
                case 0:
                    onDraw((Integer) inputMessage.obj);
                    return;
                case 1:
                    finish();
                    if (this.recording) {
                        this.videoEncoder.stopRecording(inputMessage.arg1);
                    }
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                        return;
                    }
                    return;
                case 2:
                    EGL10 egl10 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            return;
                        }
                        return;
                    }
                    SurfaceTexture surfaceTexture = this.cameraSurface;
                    if (surfaceTexture != null) {
                        surfaceTexture.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
                        this.cameraSurface.setOnFrameAvailableListener(null);
                        this.cameraSurface.release();
                        InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
                        InstantCameraView.this.cameraTextureAlpha = 0.0f;
                        InstantCameraView.this.cameraTexture[0] = 0;
                        InstantCameraView instantCameraView = InstantCameraView.this;
                        instantCameraView.oldTextureTextureBuffer = instantCameraView.textureBuffer.duplicate();
                        InstantCameraView instantCameraView2 = InstantCameraView.this;
                        instantCameraView2.oldTexturePreviewSize = instantCameraView2.previewSize;
                    }
                    this.cameraId = Integer.valueOf(this.cameraId.intValue() + 1);
                    InstantCameraView.this.cameraReady = false;
                    GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                    GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9728);
                    GLES20.glTexParameteri(36197, 10240, 9728);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    SurfaceTexture surfaceTexture2 = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture2;
                    surfaceTexture2.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() { // from class: org.telegram.ui.Components.InstantCameraView$CameraGLThread$$ExternalSyntheticLambda0
                        @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                        public final void onFrameAvailable(SurfaceTexture surfaceTexture3) {
                            InstantCameraView.CameraGLThread.this.m2688x43215339(surfaceTexture3);
                        }
                    });
                    InstantCameraView.this.createCamera(this.cameraSurface);
                    InstantCameraView.this.cameraThread.updateScale();
                    float tX = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                    float tY = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                    float[] texData = {0.5f - tX, 0.5f - tY, tX + 0.5f, 0.5f - tY, 0.5f - tX, tY + 0.5f, tX + 0.5f, 0.5f + tY};
                    InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                    InstantCameraView.this.textureBuffer.put(texData).position(0);
                    return;
                case 3:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("set gl rednderer session");
                    }
                    CameraSession newSession = (CameraSession) inputMessage.obj;
                    CameraSession cameraSession = this.currentSession;
                    if (cameraSession == newSession) {
                        int rotationAngle = cameraSession.getWorldAngle();
                        Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                        if (rotationAngle != 0) {
                            Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, rotationAngle, 0.0f, 0.0f, 1.0f);
                            return;
                        }
                        return;
                    }
                    this.currentSession = newSession;
                    return;
                default:
                    return;
            }
        }

        /* renamed from: lambda$handleMessage$1$org-telegram-ui-Components-InstantCameraView$CameraGLThread */
        public /* synthetic */ void m2688x43215339(SurfaceTexture surfaceTexture) {
            requestRender();
        }

        public void shutdown(int send) {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, send, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference<>(encoder);
        }

        @Override // android.os.Handler
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            VideoRecorder encoder = this.mWeakEncoder.get();
            if (encoder == null) {
                return;
            }
            switch (what) {
                case 0:
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("start encoder");
                        }
                        encoder.prepareEncoder();
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        encoder.handleStopRecording(0);
                        Looper.myLooper().quit();
                        return;
                    }
                case 1:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stop encoder");
                    }
                    encoder.handleStopRecording(inputMessage.arg1);
                    return;
                case 2:
                    long timestamp = (inputMessage.arg1 << 32) | (inputMessage.arg2 & 4294967295L);
                    Integer cameraId = (Integer) inputMessage.obj;
                    encoder.handleVideoFrameAvailable(timestamp, cameraId);
                    return;
                case 3:
                    encoder.handleAudioFrameAvailable((AudioBufferInfo) inputMessage.obj);
                    return;
                default:
                    return;
            }
        }

        public void exit() {
            Looper.myLooper().quit();
        }
    }

    /* loaded from: classes5.dex */
    public static class AudioBufferInfo {
        public static final int MAX_SAMPLES = 10;
        public boolean last;
        public int lastWroteBuffer;
        public int results;
        public ByteBuffer[] buffer = new ByteBuffer[10];
        public long[] offset = new long[10];
        public int[] read = new int[10];

        public AudioBufferInfo() {
            for (int i = 0; i < 10; i++) {
                this.buffer[i] = ByteBuffer.allocateDirect(2048);
                this.buffer[i].order(ByteOrder.nativeOrder());
            }
        }
    }

    /* loaded from: classes5.dex */
    public class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private int alphaHandle;
        private MediaCodec.BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        private AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        private ArrayBlockingQueue<AudioBufferInfo> buffers;
        private ArrayList<AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private android.opengl.EGLConfig eglConfig;
        private android.opengl.EGLContext eglContext;
        private android.opengl.EGLDisplay eglDisplay;
        private android.opengl.EGLSurface eglSurface;
        private boolean firstEncode;
        private int frameCount;
        private DispatchQueue generateKeyframeThumbsQueue;
        private volatile EncoderHandler handler;
        private ArrayList<Bitmap> keyframeThumbs;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private int prependHeaderSize;
        private int previewSizeHandle;
        private boolean ready;
        private Runnable recorderRunnable;
        private int resolutionHandle;
        private volatile boolean running;
        private volatile int sendWhenDone;
        private android.opengl.EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private MediaCodec.BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private File videoFile;
        private long videoFirst;
        private int videoHeight;
        private long videoLast;
        private int videoTrackIndex;
        private int videoWidth;
        private int zeroTimeStamps;

        private VideoRecorder() {
            InstantCameraView.this = r5;
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList<>();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1L;
            this.currentTimestamp = 0L;
            this.lastTimestamp = -1L;
            this.sync = new Object();
            this.videoFirst = -1L;
            this.audioFirst = -1L;
            this.lastCameraId = 0;
            this.buffers = new ArrayBlockingQueue<>(10);
            this.keyframeThumbs = new ArrayList<>();
            this.recorderRunnable = new AnonymousClass1();
        }

        /* renamed from: org.telegram.ui.Components.InstantCameraView$VideoRecorder$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
                VideoRecorder.this = this$1;
            }

            /* JADX WARN: Code restructure failed: missing block: B:13:0x0031, code lost:
                if (org.telegram.ui.Components.InstantCameraView.VideoRecorder.this.sendWhenDone == 0) goto L61;
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 347
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.AnonymousClass1.run():void");
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-InstantCameraView$VideoRecorder$1 */
            public /* synthetic */ void m2696x7daf50d6(double amplitude) {
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(InstantCameraView.this.recordingGuid), Double.valueOf(amplitude));
            }
        }

        public void startRecording(File outputFile, android.opengl.EGLContext sharedContext) {
            int resolution = MessagesController.getInstance(InstantCameraView.this.currentAccount).roundVideoSize;
            int bitrate = MessagesController.getInstance(InstantCameraView.this.currentAccount).roundVideoBitrate * 1024;
            this.videoFile = outputFile;
            this.videoWidth = resolution;
            this.videoHeight = resolution;
            this.videoBitrate = bitrate;
            this.sharedEglContext = sharedContext;
            synchronized (this.sync) {
                if (this.running) {
                    return;
                }
                this.running = true;
                Thread thread = new Thread(this, "TextureMovieEncoder");
                thread.setPriority(10);
                thread.start();
                while (!this.ready) {
                    try {
                        this.sync.wait();
                    } catch (InterruptedException e) {
                    }
                }
                this.keyframeThumbs.clear();
                this.frameCount = 0;
                DispatchQueue dispatchQueue = this.generateKeyframeThumbsQueue;
                if (dispatchQueue != null) {
                    dispatchQueue.cleanupQueue();
                    this.generateKeyframeThumbsQueue.recycle();
                }
                this.generateKeyframeThumbsQueue = new DispatchQueue("keyframes_thumb_queque");
                this.handler.sendMessage(this.handler.obtainMessage(0));
            }
        }

        public void stopRecording(int send) {
            this.handler.sendMessage(this.handler.obtainMessage(1, send, 0));
        }

        public void frameAvailable(SurfaceTexture st, Integer cameraId, long timestampInternal) {
            synchronized (this.sync) {
                if (!this.ready) {
                    return;
                }
                long timestamp = st.getTimestamp();
                if (timestamp == 0) {
                    int i = this.zeroTimeStamps + 1;
                    this.zeroTimeStamps = i;
                    if (i > 1) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("fix timestamp enabled");
                        }
                        timestamp = timestampInternal;
                    } else {
                        return;
                    }
                } else {
                    this.zeroTimeStamps = 0;
                }
                this.handler.sendMessage(this.handler.obtainMessage(2, (int) (timestamp >> 32), (int) timestamp, cameraId));
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (this.sync) {
                this.handler = new EncoderHandler(this);
                this.ready = true;
                this.sync.notify();
            }
            Looper.loop();
            synchronized (this.sync) {
                this.ready = false;
            }
        }

        public void handleAudioFrameAvailable(AudioBufferInfo input) {
            ByteBuffer inputBuffer;
            if (this.audioStopedByTime) {
                return;
            }
            AudioBufferInfo input2 = input;
            this.buffersToWrite.add(input2);
            if (this.audioFirst == -1) {
                if (this.videoFirst != -1) {
                    while (true) {
                        boolean ok = false;
                        int a = 0;
                        while (true) {
                            if (a >= input2.results) {
                                break;
                            } else if (a != 0 || Math.abs(this.videoFirst - input2.offset[a]) <= 10000000) {
                                if (input2.offset[a] >= this.videoFirst) {
                                    input2.lastWroteBuffer = a;
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("found first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("ignore first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                    a++;
                                }
                            } else {
                                this.desyncTime = this.videoFirst - input2.offset[a];
                                this.audioFirst = input2.offset[a];
                                ok = true;
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("detected desync between audio and video " + this.desyncTime);
                                }
                            }
                        }
                        if (ok) {
                            break;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("first audio frame not found, removing buffers " + input2.results);
                        }
                        this.buffersToWrite.remove(input2);
                        if (!this.buffersToWrite.isEmpty()) {
                            AudioBufferInfo input3 = this.buffersToWrite.get(0);
                            input2 = input3;
                        } else {
                            return;
                        }
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("video record not yet started");
                    return;
                } else {
                    return;
                }
            }
            if (this.audioStartTime == -1) {
                this.audioStartTime = input2.offset[input2.lastWroteBuffer];
            }
            if (this.buffersToWrite.size() > 1) {
                AudioBufferInfo input4 = this.buffersToWrite.get(0);
                input2 = input4;
            }
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            boolean isLast = false;
            while (input2 != null) {
                try {
                    int inputBufferIndex = this.audioEncoder.dequeueInputBuffer(0L);
                    if (inputBufferIndex >= 0) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            inputBuffer = this.audioEncoder.getInputBuffer(inputBufferIndex);
                        } else {
                            ByteBuffer[] inputBuffers = this.audioEncoder.getInputBuffers();
                            ByteBuffer inputBuffer2 = inputBuffers[inputBufferIndex];
                            inputBuffer2.clear();
                            inputBuffer = inputBuffer2;
                        }
                        long startWriteTime = input2.offset[input2.lastWroteBuffer];
                        int a2 = input2.lastWroteBuffer;
                        while (true) {
                            if (a2 > input2.results) {
                                break;
                            }
                            if (a2 < input2.results) {
                                if (!this.running && input2.offset[a2] >= this.videoLast - this.desyncTime) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("stop audio encoding because of stoped video recording at " + input2.offset[a2] + " last video " + this.videoLast);
                                    }
                                    this.audioStopedByTime = true;
                                    isLast = true;
                                    input2 = null;
                                    this.buffersToWrite.clear();
                                } else if (inputBuffer.remaining() < input2.read[a2]) {
                                    input2.lastWroteBuffer = a2;
                                    input2 = null;
                                    break;
                                } else {
                                    inputBuffer.put(input2.buffer[a2]);
                                }
                            }
                            if (a2 >= input2.results - 1) {
                                this.buffersToWrite.remove(input2);
                                if (this.running) {
                                    this.buffers.put(input2);
                                }
                                if (!this.buffersToWrite.isEmpty()) {
                                    input2 = this.buffersToWrite.get(0);
                                } else {
                                    isLast = input2.last;
                                    input2 = null;
                                    break;
                                }
                            }
                            a2++;
                        }
                        MediaCodec mediaCodec = this.audioEncoder;
                        int position = inputBuffer.position();
                        long j = 0;
                        if (startWriteTime != 0) {
                            j = startWriteTime - this.audioStartTime;
                        }
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, position, j, isLast ? 4 : 0);
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                    return;
                }
            }
        }

        public void handleVideoFrameAvailable(long timestampNanos, Integer cameraId) {
            long dt;
            long alphaDt;
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (!this.lastCameraId.equals(cameraId)) {
                this.lastTimestamp = -1L;
                this.lastCameraId = cameraId;
            }
            long dt2 = this.lastTimestamp;
            if (dt2 == -1) {
                this.lastTimestamp = timestampNanos;
                dt = 0;
                if (this.currentTimestamp != 0) {
                    alphaDt = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000;
                    dt = 0;
                } else {
                    alphaDt = 0;
                }
            } else {
                alphaDt = timestampNanos - dt2;
                this.lastTimestamp = timestampNanos;
                dt = alphaDt;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                long j = this.skippedTime + alphaDt;
                this.skippedTime = j;
                if (j < 200000000) {
                    return;
                }
                this.skippedFirst = true;
            }
            this.currentTimestamp += alphaDt;
            if (this.videoFirst == -1) {
                this.videoFirst = timestampNanos / 1000;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("first video frame was at " + this.videoFirst);
                }
            }
            this.videoLast = timestampNanos;
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniform2f(this.resolutionHandle, this.videoWidth, this.videoHeight);
            if (InstantCameraView.this.oldCameraTexture[0] != 0) {
                if (!this.blendEnabled) {
                    GLES20.glEnable(3042);
                    this.blendEnabled = true;
                }
                if (InstantCameraView.this.oldTexturePreviewSize != null) {
                    GLES20.glUniform2f(this.previewSizeHandle, InstantCameraView.this.oldTexturePreviewSize.getWidth(), InstantCameraView.this.oldTexturePreviewSize.getHeight());
                }
                GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, (Buffer) InstantCameraView.this.oldTextureTextureBuffer);
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.moldSTMatrix, 0);
                GLES20.glUniform1f(this.alphaHandle, 1.0f);
                GLES20.glBindTexture(36197, InstantCameraView.this.oldCameraTexture[0]);
                GLES20.glDrawArrays(5, 0, 4);
            }
            if (InstantCameraView.this.previewSize != null) {
                GLES20.glUniform2f(this.previewSizeHandle, InstantCameraView.this.previewSize.getWidth(), InstantCameraView.this.previewSize.getHeight());
            }
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, (Buffer) InstantCameraView.this.vertexBuffer);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, (Buffer) InstantCameraView.this.textureBuffer);
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
            GLES20.glUniform1f(this.alphaHandle, InstantCameraView.this.cameraTextureAlpha);
            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
            EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            createKeyframeThumb();
            this.frameCount++;
            if (InstantCameraView.this.oldCameraTexture[0] == 0 || InstantCameraView.this.cameraTextureAlpha >= 1.0f) {
                if (!InstantCameraView.this.cameraReady) {
                    InstantCameraView.this.cameraReady = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            InstantCameraView.VideoRecorder.this.m2694xdb51bd44();
                        }
                    });
                    return;
                }
                return;
            }
            InstantCameraView.access$2516(InstantCameraView.this, ((float) dt) / 2.0E8f);
            if (InstantCameraView.this.cameraTextureAlpha > 1.0f) {
                GLES20.glDisable(3042);
                this.blendEnabled = false;
                InstantCameraView.this.cameraTextureAlpha = 1.0f;
                GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
                InstantCameraView.this.oldCameraTexture[0] = 0;
                if (!InstantCameraView.this.cameraReady) {
                    InstantCameraView.this.cameraReady = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            InstantCameraView.VideoRecorder.this.m2693xd54df1e5();
                        }
                    });
                }
            }
        }

        /* renamed from: lambda$handleVideoFrameAvailable$0$org-telegram-ui-Components-InstantCameraView$VideoRecorder */
        public /* synthetic */ void m2693xd54df1e5() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120L).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        /* renamed from: lambda$handleVideoFrameAvailable$1$org-telegram-ui-Components-InstantCameraView$VideoRecorder */
        public /* synthetic */ void m2694xdb51bd44() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120L).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        private void createKeyframeThumb() {
            if (Build.VERSION.SDK_INT >= 21 && SharedConfig.getDevicePerformanceClass() == 2 && this.frameCount % 33 == 0) {
                GenerateKeyframeThumbTask task = new GenerateKeyframeThumbTask();
                this.generateKeyframeThumbsQueue.postRunnable(task);
            }
        }

        /* loaded from: classes5.dex */
        public class GenerateKeyframeThumbTask implements Runnable {
            private GenerateKeyframeThumbTask() {
                VideoRecorder.this = r1;
            }

            @Override // java.lang.Runnable
            public void run() {
                TextureView textureView = InstantCameraView.this.textureView;
                if (textureView != null) {
                    try {
                        final Bitmap bitmap = textureView.getBitmap(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$GenerateKeyframeThumbTask$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                InstantCameraView.VideoRecorder.GenerateKeyframeThumbTask.this.m2697x5a43423d(bitmap);
                            }
                        });
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-InstantCameraView$VideoRecorder$GenerateKeyframeThumbTask */
            public /* synthetic */ void m2697x5a43423d(Bitmap bitmap) {
                if ((bitmap == null || bitmap.getPixel(0, 0) == 0) && VideoRecorder.this.keyframeThumbs.size() > 1) {
                    VideoRecorder.this.keyframeThumbs.add((Bitmap) VideoRecorder.this.keyframeThumbs.get(VideoRecorder.this.keyframeThumbs.size() - 1));
                } else {
                    VideoRecorder.this.keyframeThumbs.add(bitmap);
                }
            }
        }

        public void handleStopRecording(final int send) {
            if (this.running) {
                this.sendWhenDone = send;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
            MediaCodec mediaCodec = this.videoEncoder;
            if (mediaCodec != null) {
                try {
                    mediaCodec.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            MediaCodec mediaCodec2 = this.audioEncoder;
            if (mediaCodec2 != null) {
                try {
                    mediaCodec2.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
            MP4Builder mP4Builder = this.mediaMuxer;
            if (mP4Builder != null) {
                try {
                    mP4Builder.finishMovie();
                } catch (Exception e4) {
                    FileLog.e(e4);
                }
            }
            DispatchQueue dispatchQueue = this.generateKeyframeThumbsQueue;
            if (dispatchQueue != null) {
                dispatchQueue.cleanupQueue();
                this.generateKeyframeThumbsQueue.recycle();
                this.generateKeyframeThumbsQueue = null;
            }
            if (send == 0) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelFileUpload(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        InstantCameraView.VideoRecorder.this.m2692xedeb4749(send);
                    }
                });
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Surface surface = this.surface;
            if (surface != null) {
                surface.release();
                this.surface = null;
            }
            if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(this.eglDisplay);
            }
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglConfig = null;
            this.handler.exit();
        }

        /* renamed from: lambda$handleStopRecording$4$org-telegram-ui-Components-InstantCameraView$VideoRecorder */
        public /* synthetic */ void m2692xedeb4749(int send) {
            InstantCameraView.this.videoEditedInfo = new VideoEditedInfo();
            InstantCameraView.this.videoEditedInfo.roundVideo = true;
            InstantCameraView.this.videoEditedInfo.startTime = -1L;
            InstantCameraView.this.videoEditedInfo.endTime = -1L;
            InstantCameraView.this.videoEditedInfo.file = InstantCameraView.this.file;
            InstantCameraView.this.videoEditedInfo.encryptedFile = InstantCameraView.this.encryptedFile;
            InstantCameraView.this.videoEditedInfo.key = InstantCameraView.this.key;
            InstantCameraView.this.videoEditedInfo.iv = InstantCameraView.this.iv;
            InstantCameraView.this.videoEditedInfo.estimatedSize = Math.max(1L, InstantCameraView.this.size);
            InstantCameraView.this.videoEditedInfo.framerate = 25;
            VideoEditedInfo videoEditedInfo = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalWidth = 360;
            videoEditedInfo.resultWidth = 360;
            VideoEditedInfo videoEditedInfo2 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalHeight = 360;
            videoEditedInfo2.resultHeight = 360;
            InstantCameraView.this.videoEditedInfo.originalPath = this.videoFile.getAbsolutePath();
            if (send == 1) {
                if (!InstantCameraView.this.baseFragment.isInScheduleMode()) {
                    InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0L, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0L), InstantCameraView.this.videoEditedInfo, true, 0, false);
                } else {
                    AlertsCreator.createScheduleDatePickerDialog(InstantCameraView.this.baseFragment.getParentActivity(), InstantCameraView.this.baseFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i) {
                            InstantCameraView.VideoRecorder.this.m2690xe1e3b08b(z, i);
                        }
                    }, new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            InstantCameraView.VideoRecorder.this.m2691xe7e77bea();
                        }
                    }, InstantCameraView.this.resourcesProvider);
                }
            } else {
                InstantCameraView.this.videoPlayer = new VideoPlayer();
                InstantCameraView.this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() { // from class: org.telegram.ui.Components.InstantCameraView.VideoRecorder.2
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
                        if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoPlayer.isPlaying() && playbackState == 4) {
                            VideoPlayer videoPlayer = InstantCameraView.this.videoPlayer;
                            long j = 0;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            videoPlayer.seekTo(j);
                        }
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
                    public void onError(VideoPlayer player, Exception e) {
                        FileLog.e(e);
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
                    public void onRenderedFirstFrame() {
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }
                });
                InstantCameraView.this.videoPlayer.setTextureView(InstantCameraView.this.textureView);
                InstantCameraView.this.videoPlayer.preparePlayer(Uri.fromFile(this.videoFile), "other");
                InstantCameraView.this.videoPlayer.play();
                InstantCameraView.this.videoPlayer.setMute(true);
                InstantCameraView.this.startProgressTimer();
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, View.ALPHA, 0.0f), ObjectAnimator.ofInt(InstantCameraView.this.paint, AnimationProperties.PAINT_ALPHA, 0), ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, View.ALPHA, 1.0f));
                animatorSet.setDuration(180L);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
                InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.recordedTime;
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, Integer.valueOf(InstantCameraView.this.recordingGuid), InstantCameraView.this.videoEditedInfo, this.videoFile.getAbsolutePath(), this.keyframeThumbs);
            }
            didWriteData(this.videoFile, 0L, true);
            MediaController.getInstance().requestAudioFocus(false);
        }

        /* renamed from: lambda$handleStopRecording$2$org-telegram-ui-Components-InstantCameraView$VideoRecorder */
        public /* synthetic */ void m2690xe1e3b08b(boolean notify, int scheduleDate) {
            InstantCameraView.this.baseFragment.sendMedia(new MediaController.PhotoEntry(0, 0, 0L, this.videoFile.getAbsolutePath(), 0, true, 0, 0, 0L), InstantCameraView.this.videoEditedInfo, notify, scheduleDate, false);
            InstantCameraView.this.startAnimation(false);
        }

        /* renamed from: lambda$handleStopRecording$3$org-telegram-ui-Components-InstantCameraView$VideoRecorder */
        public /* synthetic */ void m2691xe7e77bea() {
            InstantCameraView.this.startAnimation(false);
        }

        public void prepareEncoder() {
            try {
                int recordBufferSize = AudioRecord.getMinBufferSize(InstantCameraView.audioSampleRate, 16, 2);
                if (recordBufferSize <= 0) {
                    recordBufferSize = 3584;
                }
                int bufferSize = 49152;
                if (49152 < recordBufferSize) {
                    bufferSize = ((recordBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int a = 0; a < 3; a++) {
                    this.buffers.add(new AudioBufferInfo());
                }
                AudioRecord audioRecord = new AudioRecord(0, InstantCameraView.audioSampleRate, 16, 2, bufferSize);
                this.audioRecorder = audioRecord;
                audioRecord.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + bufferSize);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("sample-rate", InstantCameraView.audioSampleRate);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", MessagesController.getInstance(InstantCameraView.this.currentAccount).roundAudioBitrate * 1024);
                audioFormat.setInteger("max-input-size", CacheDataSink.DEFAULT_BUFFER_SIZE);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(audioFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.firstEncode = true;
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", 2130708361);
                format.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(format, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie, InstantCameraView.this.isSecretChat);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.InstantCameraView$VideoRecorder$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        InstantCameraView.VideoRecorder.this.m2695x434a2d80();
                    }
                });
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("EGL already set up");
                }
                android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                this.eglDisplay = eglGetDisplay;
                if (eglGetDisplay == EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("unable to get EGL14 display");
                }
                int[] version = new int[2];
                if (!EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                    this.eglDisplay = null;
                    throw new RuntimeException("unable to initialize EGL14");
                }
                if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                    int[] attribList = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, EglBase.EGL_RECORDABLE_ANDROID, 1, 12344};
                    android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
                    int[] numConfigs = new int[1];
                    if (!EGL14.eglChooseConfig(this.eglDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0)) {
                        throw new RuntimeException("Unable to find a suitable EGLConfig");
                    }
                    int[] attrib2_list = {12440, 2, 12344};
                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, attrib2_list, 0);
                    this.eglConfig = configs[0];
                }
                int[] values = new int[1];
                EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, values, 0);
                if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                    int[] surfaceAttribs = {12344};
                    android.opengl.EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, surfaceAttribs, 0);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface != null) {
                        if (!EGL14.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                            }
                            throw new RuntimeException("eglMakeCurrent failed");
                        }
                        GLES20.glBlendFunc(770, 771);
                        int vertexShader = InstantCameraView.this.loadShader(35633, InstantCameraView.VERTEX_SHADER);
                        InstantCameraView instantCameraView = InstantCameraView.this;
                        int fragmentShader = instantCameraView.loadShader(35632, instantCameraView.createFragmentShader(instantCameraView.previewSize));
                        if (vertexShader != 0 && fragmentShader != 0) {
                            int glCreateProgram = GLES20.glCreateProgram();
                            this.drawProgram = glCreateProgram;
                            GLES20.glAttachShader(glCreateProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] != 0) {
                                this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                this.previewSizeHandle = GLES20.glGetUniformLocation(this.drawProgram, "preview");
                                this.resolutionHandle = GLES20.glGetUniformLocation(this.drawProgram, "resolution");
                                this.alphaHandle = GLES20.glGetUniformLocation(this.drawProgram, "alpha");
                                this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                                return;
                            }
                            GLES20.glDeleteProgram(this.drawProgram);
                            this.drawProgram = 0;
                            return;
                        }
                        return;
                    }
                    throw new RuntimeException("surface was null");
                }
                throw new IllegalStateException("surface already created");
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        /* renamed from: lambda$prepareEncoder$5$org-telegram-ui-Components-InstantCameraView$VideoRecorder */
        public /* synthetic */ void m2695x434a2d80() {
            if (InstantCameraView.this.cancelled) {
                return;
            }
            try {
                InstantCameraView.this.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
            InstantCameraView.this.recording = true;
            InstantCameraView.this.recordStartTime = System.currentTimeMillis();
            InstantCameraView.this.invalidate();
            NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(InstantCameraView.this.recordingGuid), false);
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, long availableSize, boolean last) {
            long j = 0;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1L, ConnectionsManager.FileTypeVideo, false);
                this.videoConvertFirstWrite = false;
                if (last) {
                    FileLoader fileLoader = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    String file2 = file.toString();
                    boolean z = InstantCameraView.this.isSecretChat;
                    if (last) {
                        j = file.length();
                    }
                    fileLoader.checkUploadNewDataAvailable(file2, z, availableSize, j);
                    return;
                }
                return;
            }
            FileLoader fileLoader2 = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            String file3 = file.toString();
            boolean z2 = InstantCameraView.this.isSecretChat;
            if (last) {
                j = file.length();
            }
            fileLoader2.checkUploadNewDataAvailable(file3, z2, availableSize, j);
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            ByteBuffer encodedData;
            ByteBuffer encodedData2;
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = null;
            int i = 21;
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000L);
                if (encoderStatus == -1) {
                    if (!endOfStream) {
                        break;
                    }
                    i = 21;
                } else {
                    if (encoderStatus == -3) {
                        if (Build.VERSION.SDK_INT < i) {
                            encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                        }
                    } else if (encoderStatus == -2) {
                        MediaFormat newFormat = this.videoEncoder.getOutputFormat();
                        if (this.videoTrackIndex == -5) {
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                            if (newFormat.containsKey("prepend-sps-pps-to-idr-frames") && newFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                                ByteBuffer spsBuff = newFormat.getByteBuffer("csd-0");
                                ByteBuffer ppsBuff = newFormat.getByteBuffer("csd-1");
                                this.prependHeaderSize = spsBuff.limit() + ppsBuff.limit();
                            }
                        }
                    } else if (encoderStatus < 0) {
                        continue;
                    } else {
                        if (Build.VERSION.SDK_INT < i) {
                            encodedData2 = encoderOutputBuffers[encoderStatus];
                        } else {
                            encodedData2 = this.videoEncoder.getOutputBuffer(encoderStatus);
                        }
                        if (encodedData2 == null) {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                        }
                        if (this.videoBufferInfo.size > 1) {
                            if ((this.videoBufferInfo.flags & 2) == 0) {
                                if (this.prependHeaderSize != 0 && (this.videoBufferInfo.flags & 1) != 0) {
                                    this.videoBufferInfo.offset += this.prependHeaderSize;
                                    this.videoBufferInfo.size -= this.prependHeaderSize;
                                }
                                if (this.firstEncode && (this.videoBufferInfo.flags & 1) != 0) {
                                    if (this.videoBufferInfo.size > 100) {
                                        encodedData2.position(this.videoBufferInfo.offset);
                                        byte[] temp = new byte[100];
                                        encodedData2.get(temp);
                                        int nalCount = 0;
                                        int a = 0;
                                        while (true) {
                                            if (a < temp.length - 4) {
                                                if (temp[a] != 0 || temp[a + 1] != 0 || temp[a + 2] != 0 || temp[a + 3] != 1 || (nalCount = nalCount + 1) <= 1) {
                                                    a++;
                                                } else {
                                                    this.videoBufferInfo.offset += a;
                                                    this.videoBufferInfo.size -= a;
                                                    break;
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    this.firstEncode = false;
                                }
                                long availableSize = this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData2, this.videoBufferInfo, true);
                                if (availableSize != 0) {
                                    didWriteData(this.videoFile, availableSize, false);
                                }
                            } else if (this.videoTrackIndex == -5) {
                                byte[] csd = new byte[this.videoBufferInfo.size];
                                encodedData2.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                                encodedData2.position(this.videoBufferInfo.offset);
                                encodedData2.get(csd);
                                ByteBuffer sps = null;
                                ByteBuffer pps = null;
                                int a2 = this.videoBufferInfo.size - 1;
                                while (true) {
                                    if (a2 < 0 || a2 <= 3) {
                                        break;
                                    } else if (csd[a2] != 1 || csd[a2 - 1] != 0 || csd[a2 - 2] != 0 || csd[a2 - 3] != 0) {
                                        a2--;
                                    } else {
                                        sps = ByteBuffer.allocate(a2 - 3);
                                        pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a2 - 3));
                                        sps.put(csd, 0, a2 - 3).position(0);
                                        pps.put(csd, a2 - 3, this.videoBufferInfo.size - (a2 - 3)).position(0);
                                        break;
                                    }
                                }
                                MediaFormat newFormat2 = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                                if (sps != null && pps != null) {
                                    newFormat2.setByteBuffer("csd-0", sps);
                                    newFormat2.setByteBuffer("csd-1", pps);
                                }
                                this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat2, false);
                            }
                        }
                        this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                        if ((this.videoBufferInfo.flags & 4) != 0) {
                            break;
                        }
                    }
                    i = 21;
                }
            }
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0L);
                if (encoderStatus2 == -1) {
                    if (endOfStream) {
                        if (!this.running && this.sendWhenDone == 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else if (encoderStatus2 == -3) {
                    if (Build.VERSION.SDK_INT < 21) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus2 == -2) {
                    MediaFormat newFormat3 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat3, true);
                    }
                } else if (encoderStatus2 >= 0) {
                    if (Build.VERSION.SDK_INT < 21) {
                        encodedData = encoderOutputBuffers[encoderStatus2];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus2);
                    }
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus2 + " was null");
                    }
                    if ((this.audioBufferInfo.flags & 2) != 0) {
                        this.audioBufferInfo.size = 0;
                    }
                    if (this.audioBufferInfo.size != 0) {
                        long availableSize2 = this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false);
                        if (availableSize2 != 0) {
                            didWriteData(this.videoFile, availableSize2, false);
                        }
                    }
                    this.audioEncoder.releaseOutputBuffer(encoderStatus2, false);
                    if ((this.audioBufferInfo.flags & 4) != 0) {
                        return;
                    }
                }
            }
        }

        protected void finalize() throws Throwable {
            try {
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(this.eglDisplay);
                    this.eglDisplay = EGL14.EGL_NO_DISPLAY;
                    this.eglContext = EGL14.EGL_NO_CONTEXT;
                    this.eglConfig = null;
                }
            } finally {
                super.finalize();
            }
        }
    }

    public String createFragmentShader(org.telegram.messenger.camera.Size previewSize) {
        if (SharedConfig.getDevicePerformanceClass() == 0 || SharedConfig.getDevicePerformanceClass() == 1 || Math.max(previewSize.getHeight(), previewSize.getWidth()) * 0.7f < MessagesController.getInstance(this.currentAccount).roundVideoSize) {
            return "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float alpha;\nuniform vec2 preview;\nuniform vec2 resolution;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec4 textColor = texture2D(sTexture, vTextureCoord);\n   vec2 coord = resolution * 0.5;\n   float radius = 0.51 * resolution.x;\n   float d = length(coord - gl_FragCoord.xy) - radius;\n   float t = clamp(d, 0.0, 1.0);\n   vec3 color = mix(textColor.rgb, vec3(1, 1, 1), t);\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
        }
        return "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform vec2 resolution;\nuniform vec2 preview;\nuniform float alpha;\nconst float kernel = 1.0;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   float pixelSizeX = 1.0 / preview.x;\n   float pixelSizeY = 1.0 / preview.y;\n   vec3 accumulation = vec3(0);\n   vec3 weightsum = vec3(0);\n   for (float x = -kernel; x < kernel; x++){\n       for (float y = -kernel; y < kernel; y++){\n           accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n           weightsum += 1.0;\n       }\n   }\n   vec4 textColor = vec4(accumulation / weightsum, 1.0);\n   vec2 coord = resolution * 0.5;\n   float radius = 0.51 * resolution.x;\n   float d = length(coord - gl_FragCoord.xy) - radius;\n   float t = clamp(d, 0.0, 1.0);\n   vec3 color = mix(textColor.rgb, vec3(1, 1, 1), t);\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
    }

    /* loaded from: classes5.dex */
    public class InstantViewCameraContainer extends FrameLayout {
        float imageProgress;
        ImageReceiver imageReceiver;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public InstantViewCameraContainer(Context context) {
            super(context);
            InstantCameraView.this = this$0;
            this$0.setWillNotDraw(false);
        }

        public void setImageReceiver(ImageReceiver imageReceiver) {
            if (this.imageReceiver == null) {
                this.imageProgress = 0.0f;
            }
            this.imageReceiver = imageReceiver;
            invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            float f = this.imageProgress;
            if (f != 1.0f) {
                float f2 = f + 0.064f;
                this.imageProgress = f2;
                if (f2 > 1.0f) {
                    this.imageProgress = 1.0f;
                }
                invalidate();
            }
            if (this.imageReceiver != null) {
                canvas.save();
                if (this.imageReceiver.getImageWidth() != InstantCameraView.this.textureViewSize) {
                    float s = InstantCameraView.this.textureViewSize / this.imageReceiver.getImageWidth();
                    canvas.scale(s, s);
                }
                canvas.translate(-this.imageReceiver.getImageX(), -this.imageReceiver.getImageY());
                float oldAlpha = this.imageReceiver.getAlpha();
                this.imageReceiver.setAlpha(this.imageProgress);
                this.imageReceiver.draw(canvas);
                this.imageReceiver.setAlpha(oldAlpha);
                canvas.restore();
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        VideoPlayer videoPlayer;
        if (ev.getAction() == 0 && this.baseFragment != null && (videoPlayer = this.videoPlayer) != null) {
            boolean mute = !videoPlayer.isMuted();
            this.videoPlayer.setMute(mute);
            AnimatorSet animatorSet = this.muteAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.muteAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[3];
            ImageView imageView = this.muteImageView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = mute ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView, property, fArr);
            ImageView imageView2 = this.muteImageView;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            float f = 0.5f;
            fArr2[0] = mute ? 1.0f : 0.5f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView2, property2, fArr2);
            ImageView imageView3 = this.muteImageView;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            if (mute) {
                f = 1.0f;
            }
            fArr3[0] = f;
            animatorArr[2] = ObjectAnimator.ofFloat(imageView3, property3, fArr3);
            animatorSet2.playTogether(animatorArr);
            this.muteAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InstantCameraView.11
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.muteAnimation)) {
                        InstantCameraView.this.muteAnimation = null;
                    }
                }
            });
            this.muteAnimation.setDuration(180L);
            this.muteAnimation.setInterpolator(new DecelerateInterpolator());
            this.muteAnimation.start();
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2 && this.finishZoomTransition == null && this.recording) {
                this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
            if (ev.getActionMasked() == 0) {
                AndroidUtilities.rectTmp.set(this.cameraContainer.getX(), this.cameraContainer.getY(), this.cameraContainer.getX() + this.cameraContainer.getMeasuredWidth(), this.cameraContainer.getY() + this.cameraContainer.getMeasuredHeight());
                this.maybePinchToZoomTouchMode = AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY());
            }
            return true;
        }
        if (ev.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
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
                finishZoom();
                return false;
            }
            float hypot = ((float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            float zoom = Math.min(1.0f, Math.max(0.0f, hypot - 1.0f));
            this.cameraSession.setZoom(zoom);
        } else {
            int index12 = ev.getActionMasked();
            if ((index12 == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
                this.isInPinchToZoomTouchMode = false;
                finishZoom();
            }
        }
        return true;
    }

    public void finishZoom() {
        if (this.finishZoomTransition != null) {
            return;
        }
        float zoom = Math.min(1.0f, Math.max(0.0f, this.pinchScale - 1.0f));
        if (zoom > 0.0f) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(zoom, 0.0f);
            this.finishZoomTransition = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.InstantCameraView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    InstantCameraView.this.m2684lambda$finishZoom$6$orgtelegramuiComponentsInstantCameraView(valueAnimator);
                }
            });
            this.finishZoomTransition.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.InstantCameraView.12
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (InstantCameraView.this.finishZoomTransition != null) {
                        InstantCameraView.this.finishZoomTransition = null;
                    }
                }
            });
            this.finishZoomTransition.setDuration(350L);
            this.finishZoomTransition.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.finishZoomTransition.start();
        }
    }

    /* renamed from: lambda$finishZoom$6$org-telegram-ui-Components-InstantCameraView */
    public /* synthetic */ void m2684lambda$finishZoom$6$orgtelegramuiComponentsInstantCameraView(ValueAnimator valueAnimator) {
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.setZoom(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }
}
