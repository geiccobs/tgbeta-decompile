package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.LaunchActivity;
import org.webrtc.RendererCommon;
/* loaded from: classes5.dex */
public abstract class PrivateVideoPreviewDialog extends FrameLayout implements VoIPService.StateListener {
    private boolean cameraReady;
    private int currentPage;
    private boolean isDismissed;
    public boolean micEnabled;
    private RLottieImageView micIconView;
    private boolean needScreencast;
    private float outProgress;
    private float pageOffset;
    private TextView positiveButton;
    private VoIPTextureView textureView;
    private TextView[] titles;
    private LinearLayout titlesLayout;
    private ViewPager viewPager;
    private int currentTexturePage = 1;
    private int visibleCameraPage = 1;

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onAudioSettingsChanged() {
        VoIPService.StateListener.CC.$default$onAudioSettingsChanged(this);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onStateChanged(int i) {
        VoIPService.StateListener.CC.$default$onStateChanged(this, i);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public PrivateVideoPreviewDialog(Context context, boolean mic, boolean screencast) {
        super(context);
        this.needScreencast = screencast;
        this.titles = new TextView[screencast ? 3 : 2];
        ViewPager viewPager = new ViewPager(context);
        this.viewPager = viewPager;
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.1
            private int scrollState = 0;
            private int willSetPage;

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                PrivateVideoPreviewDialog.this.currentPage = position;
                PrivateVideoPreviewDialog.this.pageOffset = positionOffset;
                PrivateVideoPreviewDialog.this.updateTitlesLayout();
            }

            /* JADX WARN: Type inference failed for: r0v2, types: [boolean] */
            /* JADX WARN: Type inference failed for: r0v4, types: [boolean] */
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                if (this.scrollState == 0) {
                    if (i <= PrivateVideoPreviewDialog.this.needScreencast) {
                        PrivateVideoPreviewDialog.this.currentTexturePage = 1;
                    } else {
                        PrivateVideoPreviewDialog.this.currentTexturePage = 2;
                    }
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                } else if (i <= PrivateVideoPreviewDialog.this.needScreencast) {
                    this.willSetPage = 1;
                } else {
                    this.willSetPage = 2;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                this.scrollState = state;
                if (state == 0) {
                    PrivateVideoPreviewDialog.this.currentTexturePage = this.willSetPage;
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                }
            }
        });
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, false);
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        this.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        this.textureView.clipToTexture = true;
        this.textureView.renderer.setAlpha(0.0f);
        this.textureView.renderer.setRotateTextureWithScreen(true);
        this.textureView.renderer.setUseCameraRotation(true);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setBackgroundColor(0);
        actionBar.setItemsColor(Theme.getColor(Theme.key_voipgroup_actionBarItems), false);
        actionBar.setOccupyStatusBar(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivateVideoPreviewDialog.this.dismiss(false, false);
                }
            }
        });
        addView(actionBar);
        TextView textView = new TextView(getContext()) { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.3
            private Paint[] gradientPaint;

            {
                PrivateVideoPreviewDialog.this = this;
                this.gradientPaint = new Paint[this.titles.length];
                int a = 0;
                while (true) {
                    Paint[] paintArr = this.gradientPaint;
                    if (a < paintArr.length) {
                        paintArr[a] = new Paint(1);
                        a++;
                    } else {
                        return;
                    }
                }
            }

            @Override // android.view.View
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                int color3;
                int color2;
                int color1;
                Shader gradient;
                super.onSizeChanged(w, h, oldw, oldh);
                for (int a = 0; a < this.gradientPaint.length; a++) {
                    if (a == 0 && PrivateVideoPreviewDialog.this.needScreencast) {
                        color1 = -8919716;
                        color2 = -11089922;
                        color3 = 0;
                    } else if (a == 0 || (a == 1 && PrivateVideoPreviewDialog.this.needScreencast)) {
                        color1 = -11033346;
                        color2 = -9015575;
                        color3 = 0;
                    } else {
                        color1 = -9015575;
                        color2 = -1026983;
                        color3 = -1792170;
                    }
                    if (color3 != 0) {
                        gradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{color1, color2, color3}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        gradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{color1, color2}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                    this.gradientPaint[a].setShader(gradient);
                }
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage].setAlpha(255);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage]);
                if (PrivateVideoPreviewDialog.this.pageOffset > 0.0f) {
                    int i = PrivateVideoPreviewDialog.this.currentPage + 1;
                    Paint[] paintArr = this.gradientPaint;
                    if (i < paintArr.length) {
                        paintArr[PrivateVideoPreviewDialog.this.currentPage + 1].setAlpha((int) (PrivateVideoPreviewDialog.this.pageOffset * 255.0f));
                        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage + 1]);
                    }
                }
                super.onDraw(canvas);
            }
        };
        this.positiveButton = textView;
        textView.setMinWidth(AndroidUtilities.dp(64.0f));
        this.positiveButton.setTag(-1);
        this.positiveButton.setTextSize(1, 14.0f);
        this.positiveButton.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
        this.positiveButton.setGravity(17);
        this.positiveButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.positiveButton.setText(LocaleController.getString("VoipShareVideo", R.string.VoipShareVideo));
        if (Build.VERSION.SDK_INT >= 23) {
            this.positiveButton.setForeground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_nameText), 76)));
        }
        this.positiveButton.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.positiveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PrivateVideoPreviewDialog.this.m3251x9f58a97(view);
            }
        });
        addView(this.positiveButton, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 64.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.titlesLayout = linearLayout;
        addView(linearLayout, LayoutHelper.createFrame(-2, 64, 80));
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a >= textViewArr.length) {
                break;
            }
            textViewArr[a] = new TextView(context);
            this.titles[a].setTextSize(1, 12.0f);
            this.titles[a].setTextColor(-1);
            this.titles[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titles[a].setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.titles[a].setGravity(16);
            this.titles[a].setSingleLine(true);
            this.titlesLayout.addView(this.titles[a], LayoutHelper.createLinear(-2, -1));
            if (a == 0 && this.needScreencast) {
                this.titles[a].setText(LocaleController.getString("VoipPhoneScreen", R.string.VoipPhoneScreen));
            } else if (a == 0 || (a == 1 && this.needScreencast)) {
                this.titles[a].setText(LocaleController.getString("VoipFrontCamera", R.string.VoipFrontCamera));
            } else {
                this.titles[a].setText(LocaleController.getString("VoipBackCamera", R.string.VoipBackCamera));
            }
            final int num = a;
            this.titles[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.m3252x1aab5758(num, view);
                }
            });
            a++;
        }
        setAlpha(0.0f);
        setTranslationX(AndroidUtilities.dp(32.0f));
        animate().alpha(1.0f).translationX(0.0f).setDuration(150L).start();
        setWillNotDraw(false);
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            this.textureView.renderer.setMirror(service.isFrontFaceCamera());
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.4
                @Override // org.webrtc.RendererCommon.RendererEvents
                public void onFirstFrameRendered() {
                }

                @Override // org.webrtc.RendererCommon.RendererEvents
                public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
                }
            });
            service.setLocalSink(this.textureView.renderer, false);
        }
        this.viewPager.setCurrentItem(this.needScreencast ? 1 : 0);
        if (mic) {
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.micIconView = rLottieImageView;
            rLottieImageView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f));
            this.micIconView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(48.0f), ColorUtils.setAlphaComponent(-16777216, 76)));
            final RLottieDrawable micIcon = new RLottieDrawable(R.raw.voice_mini, "2131558576", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
            this.micIconView.setAnimation(micIcon);
            this.micIconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.micEnabled = true;
            micIcon.setCurrentFrame(69);
            this.micIconView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.m3253x2b612419(micIcon, view);
                }
            });
            addView(this.micIconView, LayoutHelper.createFrame(48, 48.0f, 83, 24.0f, 0.0f, 0.0f, 136.0f));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-PrivateVideoPreviewDialog */
    public /* synthetic */ void m3251x9f58a97(View view) {
        if (this.isDismissed) {
            return;
        }
        if (this.currentPage == 0 && this.needScreencast) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getContext().getSystemService("media_projection");
            ((Activity) getContext()).startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), LaunchActivity.SCREEN_CAPTURE_REQUEST_CODE);
            return;
        }
        dismiss(false, true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-PrivateVideoPreviewDialog */
    public /* synthetic */ void m3252x1aab5758(int num, View view) {
        this.viewPager.setCurrentItem(num, true);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-PrivateVideoPreviewDialog */
    public /* synthetic */ void m3253x2b612419(RLottieDrawable micIcon, View v) {
        boolean z = !this.micEnabled;
        this.micEnabled = z;
        if (z) {
            micIcon.setCurrentFrame(36);
            micIcon.setCustomEndFrame(69);
        } else {
            micIcon.setCurrentFrame(69);
            micIcon.setCustomEndFrame(99);
        }
        micIcon.start();
    }

    public void setBottomPadding(int padding) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.positiveButton.getLayoutParams();
        layoutParams.bottomMargin = AndroidUtilities.dp(64.0f) + padding;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.titlesLayout.getLayoutParams();
        layoutParams2.bottomMargin = padding;
    }

    public void updateTitlesLayout() {
        float alpha;
        float scale;
        View[] viewArr = this.titles;
        int i = this.currentPage;
        View current = viewArr[i];
        View next = i < viewArr.length + (-1) ? viewArr[i + 1] : null;
        float measuredWidth = getMeasuredWidth() / 2;
        float currentCx = current.getLeft() + (current.getMeasuredWidth() / 2);
        float tx = (getMeasuredWidth() / 2) - currentCx;
        if (next != null) {
            float nextCx = next.getLeft() + (next.getMeasuredWidth() / 2);
            tx -= (nextCx - currentCx) * this.pageOffset;
        }
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a >= textViewArr.length) {
                break;
            }
            int i2 = this.currentPage;
            if (a < i2 || a > i2 + 1) {
                alpha = 0.7f;
                scale = 0.9f;
            } else if (a == i2) {
                float f = this.pageOffset;
                alpha = 1.0f - (0.3f * f);
                scale = 1.0f - (f * 0.1f);
            } else {
                float f2 = this.pageOffset;
                alpha = (0.3f * f2) + 0.7f;
                scale = (f2 * 0.1f) + 0.9f;
            }
            textViewArr[a].setAlpha(alpha);
            this.titles[a].setScaleX(scale);
            this.titles[a].setScaleY(scale);
            a++;
        }
        this.titlesLayout.setTranslationX(tx);
        this.positiveButton.invalidate();
        if (this.needScreencast && this.currentPage == 0 && this.pageOffset <= 0.0f) {
            this.textureView.setVisibility(4);
            return;
        }
        this.textureView.setVisibility(0);
        if (this.currentPage + (!this.needScreencast ? 1 : 0) == this.currentTexturePage) {
            this.textureView.setTranslationX((-this.pageOffset) * getMeasuredWidth());
        } else {
            this.textureView.setTranslationX((1.0f - this.pageOffset) * getMeasuredWidth());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.registerStateListener(this);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
    }

    public void onFinishMoveCameraPage() {
        VoIPService service = VoIPService.getSharedInstance();
        if (this.currentTexturePage == this.visibleCameraPage || service == null) {
            return;
        }
        boolean currentFrontface = service.isFrontFaceCamera();
        int i = this.currentTexturePage;
        if ((i == 1 && !currentFrontface) || (i == 2 && currentFrontface)) {
            saveLastCameraBitmap();
            this.cameraReady = false;
            VoIPService.getSharedInstance().switchCamera();
            this.textureView.setAlpha(0.0f);
        }
        this.visibleCameraPage = this.currentTexturePage;
    }

    private void saveLastCameraBitmap() {
        if (!this.cameraReady) {
            return;
        }
        try {
            Bitmap bitmap = this.textureView.renderer.getBitmap();
            if (bitmap != null) {
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.textureView.renderer.getMatrix(), true);
                bitmap.recycle();
                int i = 1;
                Bitmap lastBitmap = Bitmap.createScaledBitmap(newBitmap, 80, (int) (newBitmap.getHeight() / (newBitmap.getWidth() / 80.0f)), true);
                if (lastBitmap != null) {
                    if (lastBitmap != newBitmap) {
                        newBitmap.recycle();
                    }
                    Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    File file = new File(filesDirFixed, "cthumb" + this.visibleCameraPage + ".jpg");
                    FileOutputStream stream = new FileOutputStream(file);
                    lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    ViewPager viewPager = this.viewPager;
                    int i2 = this.visibleCameraPage;
                    if (this.needScreencast) {
                        i = 0;
                    }
                    View view = viewPager.findViewWithTag(Integer.valueOf(i2 - i));
                    if (view instanceof ImageView) {
                        ((ImageView) view).setImageBitmap(lastBitmap);
                    }
                }
            }
        } catch (Throwable th) {
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraFirstFrameAvailable() {
        if (!this.cameraReady) {
            this.cameraReady = true;
            this.textureView.animate().alpha(1.0f).setDuration(250L);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateTitlesLayout();
    }

    public void dismiss(boolean screencast, boolean apply) {
        if (this.isDismissed) {
            return;
        }
        this.isDismissed = true;
        saveLastCameraBitmap();
        onDismiss(screencast, apply);
        animate().alpha(0.0f).translationX(AndroidUtilities.dp(32.0f)).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (PrivateVideoPreviewDialog.this.getParent() != null) {
                    ((ViewGroup) PrivateVideoPreviewDialog.this.getParent()).removeView(PrivateVideoPreviewDialog.this);
                }
            }
        });
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    protected void onDismiss(boolean screencast, boolean apply) {
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isLandscape = View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.positiveButton.getLayoutParams();
        if (isLandscape) {
            int dp = AndroidUtilities.dp(80.0f);
            marginLayoutParams.leftMargin = dp;
            marginLayoutParams.rightMargin = dp;
        } else {
            int dp2 = AndroidUtilities.dp(16.0f);
            marginLayoutParams.leftMargin = dp2;
            marginLayoutParams.rightMargin = dp2;
        }
        RLottieImageView rLottieImageView = this.micIconView;
        if (rLottieImageView != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) rLottieImageView.getLayoutParams();
            if (isLandscape) {
                int dp3 = AndroidUtilities.dp(88.0f);
                marginLayoutParams2.leftMargin = dp3;
                marginLayoutParams2.rightMargin = dp3;
            } else {
                int dp4 = AndroidUtilities.dp(24.0f);
                marginLayoutParams2.leftMargin = dp4;
                marginLayoutParams2.rightMargin = dp4;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildWithMargins(this.titlesLayout, View.MeasureSpec.makeMeasureSpec(0, 0), 0, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), C.BUFFER_FLAG_ENCRYPTED), 0);
    }

    public int getBackgroundColor() {
        int color = Theme.getColor(Theme.key_voipgroup_actionBar);
        return ColorUtils.setAlphaComponent(color, (int) (getAlpha() * (1.0f - this.outProgress) * 255.0f));
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraSwitch(boolean isFrontFace) {
        update();
    }

    public void update() {
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.setMirror(VoIPService.getSharedInstance().isFrontFaceCamera());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class Adapter extends PagerAdapter {
        private Adapter() {
            PrivateVideoPreviewDialog.this = r1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return PrivateVideoPreviewDialog.this.titles.length;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            int i = 1;
            if (PrivateVideoPreviewDialog.this.needScreencast && position == 0) {
                FrameLayout frameLayout = new FrameLayout(PrivateVideoPreviewDialog.this.getContext());
                frameLayout.setBackground(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                view = frameLayout;
                ImageView imageView = new ImageView(PrivateVideoPreviewDialog.this.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.screencast_big);
                frameLayout.addView(imageView, LayoutHelper.createFrame(82, 82.0f, 17, 0.0f, 0.0f, 0.0f, 60.0f));
                TextView textView = new TextView(PrivateVideoPreviewDialog.this.getContext());
                textView.setText(LocaleController.getString("VoipVideoPrivateScreenSharing", R.string.VoipVideoPrivateScreenSharing));
                textView.setGravity(17);
                textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                textView.setTextColor(-1);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 17, 21.0f, 28.0f, 21.0f, 0.0f));
            } else {
                ImageView imageView2 = new ImageView(PrivateVideoPreviewDialog.this.getContext());
                imageView2.setTag(Integer.valueOf(position));
                Bitmap bitmap = null;
                try {
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    StringBuilder sb = new StringBuilder();
                    sb.append("cthumb");
                    if (position != 0 && (position != 1 || !PrivateVideoPreviewDialog.this.needScreencast)) {
                        i = 2;
                    }
                    sb.append(i);
                    sb.append(".jpg");
                    File file = new File(filesDirFixed, sb.toString());
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                } catch (Throwable th) {
                }
                if (bitmap != null) {
                    imageView2.setImageBitmap(bitmap);
                } else {
                    imageView2.setImageResource(R.drawable.icplaceholder);
                }
                imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
                view = imageView2;
            }
            if (view.getParent() != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            container.addView(view, 0);
            return view;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
